package com.janx57.aweb.server.config;

import java.io.File;

import net.jcip.annotations.Immutable;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Immutable
@Singleton
public class ServerPaths {
  private static final String WWW_DIR = "www";
  private static final String LOG_DIR = "log";

  public final File wwwDir;
  public final File logDir;

  @Inject
  public ServerPaths(AWebConfig config) {
    wwwDir = new File(config.getAppDir(), WWW_DIR);
    logDir = new File(config.getAppDir(), LOG_DIR);
  }
}
