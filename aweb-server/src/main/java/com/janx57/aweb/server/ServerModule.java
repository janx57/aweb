package com.janx57.aweb.server;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.internal.UniqueAnnotations;
import com.janx57.aweb.lifecycle.LifecycleListener;

public class ServerModule extends AbstractModule {

  @Override
  protected void configure() {
    bind(LifecycleListener.class).annotatedWith(UniqueAnnotations.create()).to(
        NioProcessor.class);
    bind(LifecycleListener.class).annotatedWith(UniqueAnnotations.create()).to(
        RequestsProcessor.class);
  }

  @Provides
  @Singleton
  ExecutorService createTasksExecutor() {
    int workers = Runtime.getRuntime().availableProcessors() * 3 / 2;
    return Executors.newFixedThreadPool(workers);
  }
}
