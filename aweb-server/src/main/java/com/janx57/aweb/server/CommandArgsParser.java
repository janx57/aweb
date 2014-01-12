package com.janx57.aweb.server;

import java.net.UnknownHostException;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

class CommandArgsParser {
  @Option(name = "-h", usage = "hostname to bind on")
  private String hostname = "127.0.0.1";

  @Option(name = "-p", usage = "port to listen on")
  private int port = 8080;

  @Option(name = "-d", usage = "www root directory")
  private String wwwDir = "~/aweb";

  private String[] args;

  public CommandArgsParser(String[] args) {
    this.args = args;
  }

  void parse() throws UnknownHostException {
    CmdLineParser parser = new CmdLineParser(this);
    try {
      parser.parseArgument(args);
    } catch (CmdLineException cle) {
      System.err.println(cle.getMessage());
      System.err.println("Available arguments:");
      parser.printUsage(System.err);
      System.err.println();
      throw new IllegalArgumentException(cle.getMessage());
    }
  }

  public String getHostname() {
    return hostname;
  }

  public int getPort() {
    return port;
  }

  public String getWwwDir() {
    return wwwDir;
  }
}
