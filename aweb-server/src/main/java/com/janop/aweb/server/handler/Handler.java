package com.janop.aweb.server.handler;

import com.janop.aweb.server.http.HttpRequest;
import com.janop.aweb.server.http.HttpResponse;

public interface Handler {
  void handle(HttpRequest request, HttpResponse response);
}
