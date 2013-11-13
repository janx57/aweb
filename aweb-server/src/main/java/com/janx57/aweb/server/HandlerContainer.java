package com.janx57.aweb.server;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.janx57.aweb.server.config.ServerPaths;
import com.janx57.aweb.server.handler.GetHandler;
import com.janx57.aweb.server.handler.Handler;
import com.janx57.aweb.server.handler.HeadHandler;
import com.janx57.aweb.server.http.HttpMethod;

@Singleton
final class HandlerContainer {
  final private ConcurrentMap<HttpMethod, Handler> handlers;

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
