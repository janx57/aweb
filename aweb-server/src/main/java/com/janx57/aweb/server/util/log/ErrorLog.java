package com.janx57.aweb.server.util.log;

import java.io.File;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggingEvent;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.janx57.aweb.server.config.ServerPaths;

@Singleton
public class ErrorLog extends AbstractLog {
  private static final Logger log = Logger.getLogger(ErrorLog.class);
  private static final String LOG_NAME = "error_log";

  @Inject
  public ErrorLog(ServerPaths paths) {
    dst.setName(LOG_NAME);
    dst.setFile(new File(paths.logDir, LOG_NAME).getPath());
    dst.activateOptions();
  }

  public void error(String message) {
    error(message, null);
  }

  public void error(String message, Object o) {
    String payload = message + (o != null ? o.toString() : "");
    final LoggingEvent event =
        new LoggingEvent(Logger.class.getName(), log, time(), Level.ERROR,
            payload, null, null, null, null, null);
    async.append(event);
  }
}
