package com.janx57.aweb.tests;

import java.net.UnknownHostException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;

import com.janx57.aweb.launcher.ServerLauncher;

public class AbstractServerTest {
  final String initSite = initSite();
  final String port = "9123";

  ExecutorService serverExecutor = Executors.newSingleThreadExecutor();

  @Before
  public void beforeTest() throws UnknownHostException {
    serverExecutor.execute(new Runnable() {
      @Override
      public void run() {
        try {
          new ServerLauncher()
              .launch(new String[] {"-d", initSite, "-p", port});
        } catch (UnknownHostException e) {
          Assert.assertTrue(e.getMessage(), false);
        }
      }
    });
  }

  @After
  public void afterTest() {
    serverExecutor.shutdownNow();
    try {
      serverExecutor.awaitTermination(1, TimeUnit.MINUTES);
    } catch (InterruptedException e) {
      // Shouldn't have happened.
      Assert.assertTrue(e.getMessage(), false);
    }
    Assert.assertTrue(serverExecutor.isTerminated());
  }

  private String initSite() {
    return System.getProperty("user.dir") + "/target/test-classes/test_site";
  }
}
