package com.janx57.aweb.lifecycle;

import java.util.LinkedList;
import java.util.List;

import com.google.inject.Binding;
import com.google.inject.Injector;
import com.google.inject.TypeLiteral;

public class LifecycleManager {
  List<LifecycleListener> listeners = new LinkedList<>();

  public void add(LifecycleListener listener) {
    listeners.add(listener);
  }

  public void add(Injector injector) {
    for (Binding<LifecycleListener> binding : get(injector)) {
      add(binding.getProvider().get());
    }
  }

  public void start() {
    for(LifecycleListener listener : listeners) {
      listener.start();
    }
  }

  public void stop() {
    for (int i = listeners.size() - 1; i >= 0; i--) {
      listeners.get(i).stop();
    }
  }

  private static List<Binding<LifecycleListener>> get(Injector i) {
    return i.findBindingsByType(new TypeLiteral<LifecycleListener>() {});
  }
}
