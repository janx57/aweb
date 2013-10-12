package com.janop.aweb.server;

import java.nio.ByteBuffer;
import java.nio.channels.Channel;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.google.inject.Singleton;

@Singleton
class MessageBus {
  private final int initQueueSize = 1000;

  final ConcurrentMap<Channel, Queue<ByteBuffer>> response =
      new ConcurrentHashMap<>();
  final BlockingQueue<ChangeRequest> selectorChanges =
      new ArrayBlockingQueue<>(initQueueSize);
}
