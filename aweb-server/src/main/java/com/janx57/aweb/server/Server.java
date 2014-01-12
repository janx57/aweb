package com.janx57.aweb.server;

import java.io.File;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.janx57.aweb.lifecycle.LifecycleManager;
import com.janx57.aweb.server.config.AWebConfig;
import com.janx57.aweb.server.config.ConfigModule;

public class Server implements Runnable {
  public final static String protocolVersion = "HTTP/1.1";

  private final LifecycleManager lifecycleManager = new LifecycleManager();

  private File appDir;
  private InetSocketAddress addr;
  private Runnable started = null;

  public Server() {
  }

  public Server(Runnable started) {
    this.started = started;
  }

  public Server(InetSocketAddress addr, File appDir) {
    this.appDir = appDir;
    this.addr = addr;
  }

  public int main(String... args) {
    CommandArgsParser parser = new CommandArgsParser(args);
    try {
      parser.parse();
    } catch (UnknownHostException e) {
      return 1;
    }
    appDir = new File(parser.getWwwDir());
    addr = new InetSocketAddress(parser.getHostname(), parser.getPort());

    run();

    return 0;
  }

  @Override
  public void run() {
    List<Module> modules = new ArrayList<>();
    modules.add(new ServerModule());
    modules.add(new ConfigModule(new AWebConfig(addr.getAddress(), addr
        .getPort(), appDir)));
    Injector serverInjector = Guice.createInjector(modules);
    lifecycleManager.add(serverInjector);

    lifecycleManager.start();

    if (started != null) {
      started.run();
    }

    Runtime.getRuntime().addShutdownHook(new Thread() {
      @Override
      public void run() {
        System.out.println("Stopping the server...");
        lifecycleManager.stop();
      }
    });
  }
}
