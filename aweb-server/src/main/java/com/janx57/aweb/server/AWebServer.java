package com.janx57.aweb.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.Executor;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.janx57.aweb.server.commons.ExpirySet;
import com.janx57.aweb.server.commons.ExpirySetImpl;
import com.janx57.aweb.server.commons.SocketCloser;
import com.janx57.aweb.server.config.AWebConfig;
import com.janx57.aweb.server.util.log.ErrorLog;

@Singleton
public class AWebServer implements Runnable {
  public final static String protocolVersion = "HTTP/1.1";

  private final static int PERSISTENT_CONNECTION_TIMEOUT_MS = 1000 * 4;
  private final static int SELECTOR_TIMEOUT_MS =
      PERSISTENT_CONNECTION_TIMEOUT_MS / 2;

  protected final Executor workers;
  protected final Selector accept;
  protected final MessageBus bus;
  protected final AWebConfig config;
  protected final HandlerContainer handlers;
  protected final RequestTask.Factory requestTaskFactory;
  protected final ErrorLog log;

  // Part of the HTTP persistent connections feature.
  // A set of sockets to which we finished writing but haven't started reading a
  // new request.
  protected final ExpirySet<SocketChannel> waitingSockets;

  @Inject
  public AWebServer(Executor workers, AWebConfig config, MessageBus bus,
      HandlerContainer handlers, RequestTask.Factory requestTaskFactory,
      ErrorLog log) {
    this.workers = workers;
    this.config = config;
    this.bus = bus;
    this.handlers = handlers;
    this.requestTaskFactory = requestTaskFactory;
    this.log = log;
    this.waitingSockets =
        new ExpirySetImpl<>(PERSISTENT_CONNECTION_TIMEOUT_MS,
            new SocketCloser());
    try {
      this.accept = Selector.open();
    } catch (IOException e) {
      throw new IllegalStateException(e.getMessage());
    }
  }

  @Override
  public void run() {
    start();
  }

  private void start() {
    try {
      ServerSocketChannel ssc = ServerSocketChannel.open();
      ssc.configureBlocking(false);

      InetSocketAddress isa =
          new InetSocketAddress(config.getIp(), config.getPort());
      ssc.socket().bind(isa);
      ssc.register(accept, SelectionKey.OP_ACCEPT);
    } catch (IOException e) {
      throw new IllegalStateException(e);
    }

    while (true) {
      try {
        accept.select(SELECTOR_TIMEOUT_MS);

        // Close sockets which weren't used for at least
        // PERSISTENT_CONNECTION_TIMEOUT
        // milliseconds and at most SELECTOR_TIMEOUT +
        // PERSISTENT_CONNECTION_TIMEOUT milliseconds.
        waitingSockets.cleanUp();

        ChangeRequest cr = null;
        while ((cr = bus.selectorChanges.poll()) != null) {
          SelectionKey key = cr.channel.keyFor(this.accept);
          if (key == null) {
            break; // client must have closed the connection when we were
                   // processing the request.
          }
          key.interestOps(cr.ops);
        }

        Set<SelectionKey> readyKeys = accept.selectedKeys();
        Iterator<SelectionKey> i = readyKeys.iterator();

        while (i.hasNext()) {
          SelectionKey sk = i.next();
          if (sk.isValid() && sk.isAcceptable()) {
            accept(sk);
          }
          if (sk.isValid() && sk.isReadable()) {
            read(sk);
          }
          if (sk.isValid() && sk.isWritable()) {
            write(sk);
          }
          i.remove();
        }
      } catch (IOException e) {
        log.error(e.getMessage(), e);
      }
    }

  }

  public void wakeup() {
    accept.wakeup();
  }

  private void accept(SelectionKey sk) throws IOException {
    ServerSocketChannel ssc = (ServerSocketChannel) sk.channel();
    SocketChannel sc = ssc.accept();
    sc.configureBlocking(false);
    sc.register(accept, SelectionKey.OP_READ);
  }

  private void read(SelectionKey sk) throws IOException {
    ChannelSession session = (ChannelSession) sk.attachment();
    if (session == null) {
      sk.attach(session = new ChannelSession());
    }

    ByteBuffer buffer = ByteBuffer.allocate(20000);
    buffer.clear();

    SocketChannel channel = (SocketChannel) sk.channel();

    // We just started or are in the middle of reading a request thus we don't
    // want this channel to be closed any time soon.
    waitingSockets.remove(channel);

    int bytesRead = channel.read(buffer);
    if (bytesRead == -1) {
      channel.close();
      return;
    }
    buffer.flip();
    while (buffer.hasRemaining()) {
      session.append((char) buffer.get());
    }
    if (session.isReady()) {
      String request = session.get();
      workers.execute(requestTaskFactory.create(request, channel));
    }
  }

  private void write(SelectionKey sk) throws IOException {
    SocketChannel channel = (SocketChannel) sk.channel();
    synchronized (channel) {
      Queue<ByteBuffer> toWrite = bus.response.get(channel);
      while (!toWrite.isEmpty()) {
        ByteBuffer buffer = toWrite.peek();
        channel.write(buffer);
        if (buffer.remaining() > 0) {
          break;
        }
        toWrite.poll();
      }
      if (toWrite.isEmpty()) {
        // Used for HTTP persistent connections. Multiple HTTP requests can be
        // sent over a single TCP socket.
        sk.interestOps(SelectionKey.OP_READ);

        // Sockets in this set will be closed after specified amount of time.
        waitingSockets.add(channel);
      }
    }
  }

  private static class ChannelSession {
    private StringBuffer data;
    private final String headerEnd = "\r\n\r\n";

    ChannelSession() {
      data = new StringBuffer();
    }

    void append(char c) {
      data.append(c);
    }

    String get() {
      final String retData = data.toString();
      data.setLength(0);
      return retData;
    }

    boolean isReady() {
      return data.toString().endsWith(headerEnd);
    }
  }
}
