package com.janx57.aweb.server;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.assistedinject.FactoryModuleBuilder;

public class ServerModule extends AbstractModule {

  @Override
  protected void configure() {
    install(new FactoryModuleBuilder().build(RequestTask.Factory.class));
  }

  @Provides
  @Singleton
  Executor createTasksExecutor() {
    int workers = Runtime.getRuntime().availableProcessors() * 3 / 2;
    return Executors.newFixedThreadPool(workers);
  }
}
