package com.janx57.aweb.server;

import java.nio.ByteBuffer;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.FutureTask;

import com.google.inject.Inject;
import com.janx57.aweb.lifecycle.LifecycleListener;
import com.janx57.aweb.server.http.HttpResponse;

// Manages the pool of worker threads that process requests
class RequestsProcessor implements LifecycleListener {

  final RequestsBus bus;
  final ExecutorService workers;

  @Inject
  RequestsProcessor(final RequestsBus bus, final ExecutorService workers) {
    this.bus = bus;
    this.workers = workers;
  }

  @Override
  public void start() {
    this.bus.setRequestsListener(new EventListener<RequestTask>() {

      @Override
      public void onEvent(final RequestTask event) {
        workers.execute(new FutureTask<HttpResponse>(event) {
          @Override
          protected void done() {
            try {
              HttpResponse response = get();
              bus.addResponseFor(event.getChannel(),
                  ByteBuffer.wrap(response.toByteArray()));
            } catch (InterruptedException e) {
              Thread.currentThread().interrupt();
            } catch (ExecutionException e) {
              throw new IllegalStateException(e);
            }
          }
        });
      }
    });
  }

  @Override
  public void stop() {
    workers.shutdown();
  }
}
