package com.janop.aweb.server.commons;

import java.io.IOException;
import java.nio.channels.SocketChannel;

import com.google.common.cache.RemovalCause;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;

public class SocketCloser implements RemovalListener<SocketChannel, Object> {
  @Override
  public void onRemoval(RemovalNotification<SocketChannel, Object> notification) {
    if (notification.getCause() == RemovalCause.EXPIRED) {
      try {
        notification.getKey().close();
      } catch (IOException e) {
        throw new IllegalStateException(e.getCause());
      }
    }
  }
}
