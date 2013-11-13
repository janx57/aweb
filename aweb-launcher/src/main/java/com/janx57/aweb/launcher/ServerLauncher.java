package com.janx57.aweb.launcher;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.janx57.aweb.server.AWebServer;
import com.janx57.aweb.server.ServerModule;
import com.janx57.aweb.server.config.ConfigModule;

public final class ServerLauncher {

  public int launch(String[] args) throws UnknownHostException {
    List<Module> modules = new ArrayList<>();
    modules.add(new ConfigModule(new CommandArgsParser(args).getConfig()));
    modules.add(new ServerModule());
    Injector injector = Guice.createInjector(modules);
    injector.getInstance(AWebServer.class).run();
    return 0;
  }
}
