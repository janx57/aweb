package com.janx57.aweb.tests;

import java.lang.reflect.Field;
import java.net.UnknownHostException;
import java.util.concurrent.CountDownLatch;

import org.junit.After;
import org.junit.Before;

import com.janx57.aweb.lifecycle.LifecycleManager;
import com.janx57.aweb.server.Server;

public class AbstractServerTest {
  final String initSite = initSite();
  final String host = "localhost";
  final String port = "9123";

  Server server = null;
  CountDownLatch latch = null;

  @Before
  public void beforeTest() throws UnknownHostException {
    latch = new CountDownLatch(1);
    server = new Server(new Runnable() {
      @Override
      public void run() {
        latch.countDown();
      }
    });
    server.main(new String[] {"-d", initSite, "-p", port, "-h", host});
  }

  @After
  public void afterTest() throws Exception {
    Field f = server.getClass().getDeclaredField("lifecycleManager");
    f.setAccessible(true);
    LifecycleManager m = (LifecycleManager) f.get(server);
    m.stop();
  }

  private String initSite() {
    return System.getProperty("user.dir") + "/target/test-classes/test_site";
  }
}
