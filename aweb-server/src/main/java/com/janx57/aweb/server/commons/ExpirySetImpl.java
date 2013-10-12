package com.janx57.aweb.server.commons;

import java.util.concurrent.TimeUnit;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalListener;

public class ExpirySetImpl<T> implements ExpirySet<T> {

  protected Cache<T, Object> cache;

  // Concept from HashSet implementation from OpenJDK
  // Dummy value to associate with an Object in the backing Map
  private static final Object PRESENT = new Object();

  public ExpirySetImpl(int timeoutMs,
      RemovalListener<T, Object> removalListener) {
    cache =
        CacheBuilder.newBuilder()
            .expireAfterWrite(timeoutMs, TimeUnit.MILLISECONDS)
            .removalListener(removalListener).build();
  }

  public void add(T element) {
    cache.put(element, PRESENT);
  }

  public void remove(T element) {
    cache.invalidate(element);
  }

  public void cleanUp() {
    cache.cleanUp();
  }
}
