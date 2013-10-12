package com.janop.aweb.server.commons;

public interface ExpirySet<T> {
  void add(T element);
  void remove(T element);
  void cleanUp();
}
