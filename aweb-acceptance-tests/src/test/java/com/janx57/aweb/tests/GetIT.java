package com.janx57.aweb.tests;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

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
    String url = String.format("http://%s:%s/index.html", host, port);
    HttpClient client = HttpClientBuilder.create().build();
    HttpGet request = new HttpGet(url);

    latch.await();

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
