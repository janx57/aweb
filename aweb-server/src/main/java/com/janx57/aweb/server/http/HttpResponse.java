package com.janx57.aweb.server.http;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HttpResponse extends HttpMessage {
  private String version;
  private int statusCode;
  private String reasonPhrase;
  private byte[] body;
  private final Map<String, String> headers = new HashMap<>();

  public void setVersion(final String version) {
    this.version = version;
  }

  public void setStatusCode(final int statusCode) {
    this.statusCode = statusCode;
  }

  public void setReasonPhrase(final String reasonPhrase) {
    this.reasonPhrase = reasonPhrase;
  }

  public void setHeader(final String name, final String value) {
    headers.put(name, value);
  }

  public void setBody(final byte[] body) {
    this.body = body;
  }

  public byte[] getBody() {
    return body;
  }

  public byte[] toByteArray() {
    StringBuilder responseText = new StringBuilder();
    responseText.append(version + sp + statusCode + reasonPhrase);
    responseText.append(crlf);
    for (String key : headers.keySet()) {
      responseText.append(key + ":" + sp + headers.get(key) + crlf);
    }
    responseText.append(crlf);

    ByteArrayOutputStream response = new ByteArrayOutputStream();
    try {
      response.write(responseText.toString().getBytes());
      response.write(body);
    } catch (IOException e) {
      throw new IllegalStateException(e.getMessage());
    }
    return response.toByteArray();
  }
}
