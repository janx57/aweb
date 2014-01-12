package com.janx57.aweb.lifecycle;

import java.util.EventListener;

public interface LifecycleListener extends EventListener {
  public void start();

  public void stop();
}
