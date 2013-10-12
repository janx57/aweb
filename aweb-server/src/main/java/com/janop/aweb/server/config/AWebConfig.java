package com.janop.aweb.server.config;

import java.io.File;

import net.jcip.annotations.Immutable;

@Immutable
public class AWebConfig {
  public final int port;
  public final File appDir;

  public AWebConfig(final int port, final File appDir) {
    this.port = port;
    this.appDir = appDir;
  }
}
