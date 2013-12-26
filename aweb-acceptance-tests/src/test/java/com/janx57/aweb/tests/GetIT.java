package com.janx57.aweb.tests;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

import junit.framework.Assert;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolVersion;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.Test;

public class GetIT extends AbstractServerTest {
  @Test
  public void testGet() throws IllegalStateException, IOException {
    String url = "http://localhost:9123/index.html";

    HttpClient client = HttpClientBuilder.create().build();
    HttpGet request = new HttpGet(url);

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
}
