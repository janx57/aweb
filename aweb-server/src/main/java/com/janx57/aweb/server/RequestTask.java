package com.janx57.aweb.server;

import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.nio.ByteBuffer;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import com.janx57.aweb.server.handler.Handler;
import com.janx57.aweb.server.http.HttpRequest;
import com.janx57.aweb.server.http.HttpResponse;
import com.janx57.aweb.server.util.log.HttpLog;

final class RequestTask implements Runnable {
  interface Factory {
    RequestTask create(String request, SocketChannel channel, MessageBus bus);
  }

  private final MessageBus bus;
  private final AWebServer server;
  private final String request;
  private final SocketChannel channel;
  private final HandlerContainer handlers;
  private final HttpLog log;

  @Inject
  RequestTask(@Assisted String request, @Assisted SocketChannel channel,
      @Assisted MessageBus bus, AWebServer server, HandlerContainer handlers, HttpLog log) {
    this.bus = bus;
    this.server = server;
    this.request = request;
    this.channel = channel;
    this.handlers = handlers;
    this.log = log;
  }

  @Override
  public void run() {
    HttpRequest hr = new HttpRequest(request);
    log.info(hr);
    HttpResponse response = process(hr);

    synchronized (channel) {
      Queue<ByteBuffer> toWrite = bus.response.get(channel);
      if (toWrite == null) {
        // TODO can be changed to a LinkedList because when
        // accessing we always synchronize on the channel
        // @GuardedBy(channel)
        toWrite = new ConcurrentLinkedQueue<>();
        bus.response.put(channel, toWrite);
      }
      toWrite.offer(ByteBuffer.wrap(response.toByteArray()));
      bus.selectorChanges.offer(new ChangeRequest(channel,
          SelectionKey.OP_WRITE));
    }
    server.wakeup();
  }

  private HttpResponse process(HttpRequest request) {
    HttpResponse response = new HttpResponse();

    Handler handler = handlers.getHandler(request.getMethod());
    if (handler == null) {
      // TODO this behaviour is incorrect. If the method is not implemented we
      // should return 501
      throw new IllegalStateException(String.format(
          "No handler available for %s method", request.getMethod()));
    }
    handler.handle(request, response);
    return response;
  }
}
