package com.janx57.aweb.server.http;

import java.util.HashMap;
import java.util.Map;

class HttpMessage {
  final static String sp = " ";
  final static String crlf = "\r\n";

  String version;
  Map<String, String> headers = new HashMap<>();
  byte[] body;

  public void setVersion(final String version) {
    this.version = version;
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
}
