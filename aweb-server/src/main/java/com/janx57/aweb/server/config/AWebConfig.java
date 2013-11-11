package com.janx57.aweb.server.config;

import java.io.File;
import java.net.InetAddress;

import net.jcip.annotations.Immutable;

@Immutable
public class AWebConfig {
  public final InetAddress ip;
  public final int port;
  public final File appDir;

  public AWebConfig(final InetAddress ip, final int port, final File appDir) {
    this.ip = ip;
    this.port = port;
    this.appDir = appDir;
  }
}
