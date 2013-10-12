package com.janx57.aweb.server.handler;

import com.janx57.aweb.server.config.ServerPaths;
import com.janx57.aweb.server.http.HttpRequest;
import com.janx57.aweb.server.http.HttpResponse;

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
