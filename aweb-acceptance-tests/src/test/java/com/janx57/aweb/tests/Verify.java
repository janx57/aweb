package com.janx57.aweb.tests;

import org.apache.http.HttpResponse;
import org.apache.http.ProtocolVersion;
import org.apache.http.StatusLine;
import org.junit.Assert;

public class Verify {
  static void status(HttpResponse r, int expectedCode, String expectedPhrase) {
    StatusLine sl = r.getStatusLine();
    Assert.assertEquals(expectedCode, sl.getStatusCode());
    Assert.assertEquals(expectedPhrase, sl.getReasonPhrase());
  }

  static void protocolVersion(HttpResponse r) {
    ProtocolVersion pv = r.getProtocolVersion();
    Assert.assertEquals("HTTP", pv.getProtocol());
    Assert.assertEquals(1, pv.getMajor());
    Assert.assertEquals(1, pv.getMinor());
  }
}
