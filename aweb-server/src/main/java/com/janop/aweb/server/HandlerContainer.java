package com.janop.aweb.server;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.janop.aweb.server.config.ServerPaths;
import com.janop.aweb.server.handler.Handler;
import com.janop.aweb.server.handler.GetHandler;
import com.janop.aweb.server.handler.HeadHandler;
import com.janop.aweb.server.http.HttpMethod;

@Singleton
class HandlerContainer {
  final ConcurrentMap<HttpMethod, Handler> handlers;

  @Inject
  HandlerContainer(ServerPaths paths) {
    this.handlers = new ConcurrentHashMap<>();
    this.handlers.put(HttpMethod.GET, new GetHandler(paths));
    this.handlers.put(HttpMethod.HEAD, new HeadHandler(paths));
  }

  void registerHandler(HttpMethod method, Handler handler) {
    if (handlers.putIfAbsent(method, handler) != null) {
      throw new IllegalStateException(String.format(
          "Handler for %s method exists", method));
    }
  }

  Handler getHandler(HttpMethod method) {
    return handlers.get(method);
  }
}
