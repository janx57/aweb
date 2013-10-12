package com.janop.aweb.server.http;

public enum Status {
  OK(200, "OK"), NOT_FOUND(404, "Not found");

  public final int code;
  public final String phrase;

  Status(int code, String phrase) {
    this.code = code;
    this.phrase = phrase;
  }
}
