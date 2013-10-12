package com.janx57.aweb.server.util.log;

import java.io.File;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggingEvent;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.janx57.aweb.server.config.ServerPaths;
import com.janx57.aweb.server.http.HttpRequest;

@Singleton
public class HttpLog extends AbstractLog {
  private static final Logger log = Logger.getLogger(HttpLog.class);
  private static final String LOG_NAME = "httpd_log";

  @Inject
  public HttpLog(ServerPaths paths) {
    dst.setName(LOG_NAME);
    dst.setFile(new File(paths.logDir, LOG_NAME).getPath());
    dst.activateOptions();
  }

  public void info(HttpRequest request) {
    String payload = request.getMethod() + " " + request.getUri();
    final LoggingEvent event =
        new LoggingEvent(Logger.class.getName(), log, time(), Level.INFO,
            payload, null, null, null, null, null);
    async.append(event);
  }
}
