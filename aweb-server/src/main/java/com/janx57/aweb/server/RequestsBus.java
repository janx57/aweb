package com.janx57.aweb.server;

import java.nio.ByteBuffer;
import java.nio.channels.Channel;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.google.inject.Singleton;

// Designed to decouple and facilitate communication between NioProcessor
// and RequestsProcessor
@Singleton
class RequestsBus {
  private final int initQueueSize = 1000;
  private final ConcurrentMap<Channel, Queue<ByteBuffer>> responses =
      new ConcurrentHashMap<>();
  private final BlockingQueue<ChangeRequest> selectorChanges =
      new ArrayBlockingQueue<>(initQueueSize);

  private EventListener<RequestTask> requestListener;
  private Runnable responseListener;

  void submit(RequestTask request) {
    requestListener.onEvent(request);
  }

  Queue<ByteBuffer> getResponseFor(Channel channel) {
    return responses.get(channel);
  }

  void addResponseFor(SocketChannel channel, ByteBuffer response) {
    synchronized (channel) {
      Queue<ByteBuffer> toWrite = responses.get(channel);
      if (toWrite == null) {
        // Guarded by channel
        toWrite = new LinkedList<>();
        responses.put(channel, toWrite);
      }
      toWrite.offer(response);
      selectorChanges.offer(new ChangeRequest(channel, SelectionKey.OP_WRITE));
      responseListener.run();
    }
  }

  ChangeRequest getChangeRequest() {
    return selectorChanges.poll();
  }

  void setRequestsListener(EventListener<RequestTask> listener) {
    this.requestListener = listener;
  }

  void setResponseListener(Runnable responseListener) {
    this.responseListener = responseListener;
  }
}
