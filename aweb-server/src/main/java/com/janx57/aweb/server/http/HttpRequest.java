package com.janx57.aweb.server.http;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import net.jcip.annotations.Immutable;

// http://www.w3.org/Protocols/rfc2616/rfc2616-sec5.html

@Immutable
public final class HttpRequest extends HttpMessage {
  private final HttpMethod method;
  private final String uri;
  private final String body;
  private final String version;
  private final Map<String, String> headers;

  public HttpRequest(final String requestBody) {
    String[] lines = requestBody.split(crlf);
    String[] requestLine = lines[0].split(sp);
    method = HttpMethod.valueOf(requestLine[0]);
    uri = requestLine[1];
    version = requestLine[2];
    int curr = 1;
    Map<String, String> h = new HashMap<>();
    while (curr < lines.length && !lines[curr].equals(crlf)) {
      String[] pair = lines[curr].split(":" + sp);
      h.put(pair[0], pair[1]);
      curr++;
    }
    headers = Collections.unmodifiableMap(h);
    h = null;

    String b = "";
    if (++curr < lines.length) {
      b = lines[curr];
    }
    body = b;
  }

  public HttpMethod getMethod() {
    return method;
  }

  public String getUri() {
    return uri;
  }
}
