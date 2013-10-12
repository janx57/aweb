package com.janop.aweb.launcher;

import java.util.ArrayList;
import java.util.List;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.janop.aweb.server.AWebServer;
import com.janop.aweb.server.ServerModule;
import com.janop.aweb.server.config.ConfigModule;

public final class ServerLauncher {

  public int launch(String[] args) {
    List<Module> modules = new ArrayList<>();
    modules.add(new ConfigModule(new CommandArgsParser(args).getConfig()));
    modules.add(new ServerModule());
    Injector injector = Guice.createInjector(modules);
    injector.getInstance(AWebServer.class).start();
    return 0;
  }
}
