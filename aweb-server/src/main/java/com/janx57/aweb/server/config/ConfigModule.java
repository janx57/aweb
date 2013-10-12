package com.janx57.aweb.server.config;

import com.google.inject.AbstractModule;

public class ConfigModule extends AbstractModule {

  private AWebConfig cfg;

  public ConfigModule(AWebConfig cfg) {
    this.cfg = cfg;
  }

  @Override
  protected void configure() {
    bind(AWebConfig.class).toInstance(cfg);
  }
}
