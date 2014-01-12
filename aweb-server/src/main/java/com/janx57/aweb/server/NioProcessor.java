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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.google.inject.Inject;
import com.janx57.aweb.lifecycle.LifecycleListener;
import com.janx57.aweb.server.commons.ExpirySet;
import com.janx57.aweb.server.commons.ExpirySetImpl;
import com.janx57.aweb.server.commons.SocketCloser;
import com.janx57.aweb.server.config.AWebConfig;
import com.janx57.aweb.server.util.log.ErrorLog;

// Manages read/write operations from/to network
public final class NioProcessor implements LifecycleListener {
  private final static int PERSISTENT_CONNECTION_TIMEOUT_MS = 1000 * 4;
  private final static int SELECTOR_TIMEOUT_MS =
      PERSISTENT_CONNECTION_TIMEOUT_MS / 2;
  protected final ExpirySet<SocketChannel> waitingSockets =
      new ExpirySetImpl<>(PERSISTENT_CONNECTION_TIMEOUT_MS, new SocketCloser());
  private Selector accept;
  private final RequestsBus bus;
  private final ErrorLog log;
  private final HandlerContainer handlers;
  private final ExecutorService acceptorExecutor = Executors.newSingleThreadExecutor();
  private final AWebConfig config;

  @Inject
  public NioProcessor(final RequestsBus bus, final HandlerContainer handlers,
      final AWebConfig config, final ErrorLog log) {
    this.bus = bus;
    this.log = log;
    this.handlers = handlers;
    this.config = config;
  }

  @Override
  public void start() {
    // TODO: abstract out the nio details to an Acceptor class. Then the start()
    // should look like:
    // acceptor.bind()
    // acceptor.accept()
    // And stop():
    // acceptorExecutor.shutdownNow()
    // acceptor.unbind()

    bus.setResponseListener(new Runnable() {
      @Override
      public void run() {
        accept.wakeup();
      }
    });

    // bind
    try {
      this.accept = Selector.open();
    } catch (IOException e) {
      throw new IllegalStateException(e.getMessage());
    }

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
    // end bind

    // accept
    acceptorExecutor.execute(new Runnable() {

      @Override
      public void run() {
        while (true) {
          try {
            if (Thread.currentThread().isInterrupted()) {
              break;
            }

            accept.select(SELECTOR_TIMEOUT_MS);

            // Close sockets which weren't used for at least
            // PERSISTENT_CONNECTION_TIMEOUT
            // milliseconds and at most SELECTOR_TIMEOUT +
            // PERSISTENT_CONNECTION_TIMEOUT milliseconds.
            waitingSockets.cleanUp();

            ChangeRequest cr = null;
            while ((cr = bus.getChangeRequest()) != null) {
              SelectionKey key = cr.channel.keyFor(accept);
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
    });
  }

  @Override
  public void stop() {
    acceptorExecutor.shutdownNow();
  }

  private void accept(final SelectionKey sk) throws IOException {
    ServerSocketChannel ssc = (ServerSocketChannel) sk.channel();
    SocketChannel sc = ssc.accept();
    sc.configureBlocking(false);
    sc.register(accept, SelectionKey.OP_READ);
  }

  private void read(final SelectionKey sk) throws IOException {
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
      bus.submit(new RequestTask(request, channel, handlers));
    }
  }

  private void write(final SelectionKey sk) throws IOException {
    SocketChannel channel = (SocketChannel) sk.channel();
    synchronized (channel) {
      Queue<ByteBuffer> toWrite = bus.getResponseFor(channel);
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
