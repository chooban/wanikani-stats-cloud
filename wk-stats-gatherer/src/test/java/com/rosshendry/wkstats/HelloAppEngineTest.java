package com.rosshendry.wkstats;

import java.io.IOException;

import org.junit.Ignore;
import org.junit.Test;

public class HelloAppEngineTest {

  @Test
  @Ignore
  public void test() throws IOException {
    MockHttpServletResponse response = new MockHttpServletResponse();
    new HelloAppEngine().doGet(null, response);
  }
}
