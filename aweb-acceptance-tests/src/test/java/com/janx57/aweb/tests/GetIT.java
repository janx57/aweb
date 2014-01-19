package com.janx57.aweb.tests;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolVersion;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.Assert;
import org.junit.Test;

public class GetIT extends AbstractServerTest {

  @Test
  public void getTest() throws ClientProtocolException, IOException,
      InterruptedException {
    String url = String.format("http://%s:%s/index.html", host, port);
    HttpClient client = HttpClientBuilder.create().build();
    HttpGet request = new HttpGet(url);

    latch.await();

    HttpResponse response = client.execute(request);

    StatusLine sl = response.getStatusLine();
    Assert.assertEquals(200, sl.getStatusCode());
    Assert.assertEquals("OK", sl.getReasonPhrase());

    ProtocolVersion pv = response.getProtocolVersion();
    Assert.assertEquals("HTTP", pv.getProtocol());
    Assert.assertEquals(1, pv.getMajor());
    Assert.assertEquals(1, pv.getMinor());

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

  @Test
  public void headTest() throws InterruptedException, ClientProtocolException, IOException {
    String url = String.format("http://%s:%s/index.html", host, port);
    HttpHead request = new HttpHead(url);
    HttpClient client = HttpClientBuilder.create().build();

    latch.await();

    HttpResponse response = client.execute(request);

    StatusLine sl = response.getStatusLine();
    Assert.assertEquals(200, sl.getStatusCode());
    Assert.assertEquals("OK", sl.getReasonPhrase());

    ProtocolVersion pv = response.getProtocolVersion();
    Assert.assertEquals("HTTP", pv.getProtocol());
    Assert.assertEquals(1, pv.getMajor());
    Assert.assertEquals(1, pv.getMinor());

    int contentLength = Integer.parseInt(response.getLastHeader("Content-Length").getValue());
    Assert.assertEquals(68, contentLength);

    Assert.assertNull(response.getEntity());
  }
}
