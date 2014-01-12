package com.janx57.aweb.server;

import java.nio.channels.SocketChannel;
import java.util.concurrent.Callable;

import com.janx57.aweb.server.handler.Handler;
import com.janx57.aweb.server.http.HttpRequest;
import com.janx57.aweb.server.http.HttpResponse;

public class RequestTask implements Callable<HttpResponse> {

  private final String request;
  private final SocketChannel channel;
  private final HandlerContainer handlers;

  public RequestTask(final String request, final SocketChannel channel,
      final HandlerContainer handlers) {
    this.request = request;
    this.channel = channel;
    this.handlers = handlers;
  }

  @Override
  public HttpResponse call() throws Exception {
    HttpRequest hr = new HttpRequest(request);
    HttpResponse response = process(hr);
    return response;
  }

  public String getRequest() {
    return request;
  }

  public SocketChannel getChannel() {
    return channel;
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
