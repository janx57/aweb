package com.janx57.aweb.server;

interface EventListener<T> {
  void onEvent(T event);
}
