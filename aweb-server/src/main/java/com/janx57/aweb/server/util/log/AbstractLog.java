package com.janx57.aweb.server.util.log;

import org.apache.log4j.AsyncAppender;
import org.apache.log4j.DailyRollingFileAppender;
import org.apache.log4j.PatternLayout;

public abstract class AbstractLog {
  private static final String ENCODING = "UTF-8";
  private static final String LAYOUT = "%d{HH:mm:ss,SSS} %-5p %m%n";
  protected final DailyRollingFileAppender dst;
  protected final AsyncAppender async;

  public AbstractLog() {
    dst = new DailyRollingFileAppender();
    dst.setLayout(new PatternLayout(LAYOUT));
    dst.setEncoding(ENCODING);
    dst.setImmediateFlush(true);
    dst.setAppend(true);

    async = new AsyncAppender();
    async.setBlocking(true);
    async.setLocationInfo(false);
    async.addAppender(dst);
    async.activateOptions();
  }

  protected long time() {
    return System.currentTimeMillis();
  }
}
