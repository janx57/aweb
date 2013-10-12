package com.janop.aweb.server.handler;

import com.janop.aweb.server.config.ServerPaths;
import com.janop.aweb.server.http.HttpRequest;
import com.janop.aweb.server.http.HttpResponse;

public class GetHandler extends HeadHandler implements Handler {
  public GetHandler(ServerPaths paths) {
    super(paths);
  }

  @Override
  public void handle(HttpRequest request, HttpResponse response) {
    super.handle(request, response);
    response.setBody(body);
  }
}
