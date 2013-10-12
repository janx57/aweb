package com.janx57.aweb.server.handler;

import com.janx57.aweb.server.http.HttpRequest;
import com.janx57.aweb.server.http.HttpResponse;

public interface Handler {
  void handle(HttpRequest request, HttpResponse response);
}
