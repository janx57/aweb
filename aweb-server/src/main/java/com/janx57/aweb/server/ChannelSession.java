package com.janx57.aweb.server;

class ChannelSession {
  protected StringBuffer data;
  private final String headerEnd = "\r\n\r\n";

  ChannelSession() {
    data = new StringBuffer();
  }

  void append(char c) {
    data.append(c);
  }

  String get() {
    final String retData = data.toString();
    data.setLength(0);
    return retData;
  }

  boolean isReady() {
    return data.toString().endsWith(headerEnd);
  }
}
