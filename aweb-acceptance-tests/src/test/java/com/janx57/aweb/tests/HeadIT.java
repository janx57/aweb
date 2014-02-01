package com.janx57.aweb.tests;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.Assert;
import org.junit.Test;

public class HeadIT extends AbstractServerTest {

  @Test
  public void headTest() throws InterruptedException, ClientProtocolException, IOException {
    String url = String.format("http://%s:%s/index.html", host, port);
    HttpHead request = new HttpHead(url);
    HttpClient client = HttpClientBuilder.create().build();

    latch.await();

    HttpResponse response = client.execute(request);

    Verify.status(response, 200, "OK");
    Verify.protocolVersion(response);

    int contentLength = Integer.parseInt(response.getLastHeader("Content-Length").getValue());
    Assert.assertEquals(68, contentLength);

    Assert.assertNull(response.getEntity());
  }
}
