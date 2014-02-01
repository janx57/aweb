package com.janx57.aweb.tests;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.Assert;
import org.junit.Test;

public class GetIT extends AbstractServerTest {

  @Test
  public void getTest() throws ClientProtocolException, IOException,
      InterruptedException {
    latch.await();
    executeGetTest();
  }

  @Test
  public void getConcurrentTest() throws InterruptedException {
    latch.await();
    int threadsNum = 16;
    ExecutorService e = Executors.newFixedThreadPool(threadsNum);
    final CountDownLatch cdl = new CountDownLatch(threadsNum);
    for (int i = 0; i < threadsNum; i++) {
      e.execute(new Runnable() {
        @Override
        public void run() {
          cdl.countDown();
          try {
            cdl.await();
            executeGetTest();
          } catch (InterruptedException | IOException e) {
            throw new IllegalStateException(e);
          }
        }
      });
    }
    e.shutdown();
    e.awaitTermination(15, TimeUnit.SECONDS);
  }

  private void executeGetTest() throws ClientProtocolException, IOException {
    String url = String.format("http://%s:%s/index.html", host, port);
    HttpClient client = HttpClientBuilder.create().build();
    HttpGet request = new HttpGet(url);

    HttpResponse response = client.execute(request);

    Verify.status(response, 200, "OK");
    Verify.protocolVersion(response);

    String respBody =
        IOUtils.toString(response.getEntity().getContent(), "UTF-8");

    Scanner targetFile =
        new Scanner(new File(System.getProperty("user.dir")
            + "/target/test-classes/test_site/www/index.html"));

    String expectedBody = targetFile.nextLine();
    Assert.assertEquals(expectedBody, respBody);
    Assert.assertFalse(targetFile.hasNextLine());

    targetFile.close();
  }
}
