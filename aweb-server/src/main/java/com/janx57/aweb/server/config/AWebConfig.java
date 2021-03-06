package com.janx57.aweb.server.config;

import java.io.File;
import java.net.InetAddress;

import net.jcip.annotations.Immutable;

@Immutable
public final class AWebConfig {
  private final InetAddress ip; // TODO change to InetSocketAddress and then remove the port field
  private final int port;
  private final File appDir;

  public AWebConfig(final InetAddress ip, final int port, final File appDir) {
    this.ip = ip;
    this.port = port;
    this.appDir = appDir;
  }

  public InetAddress getIp() {
    return ip;
  }

  public int getPort() {
    return port;
  }

  public File getAppDir() {
    return appDir;
  }
}
