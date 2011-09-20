package com.semaphore.server;

import java.io.StringWriter;

import javax.servlet.http.HttpServletResponse;

import org.junit.Test;

public class SemaphoreServletTest
{
   @Test
   public void get()
   {
      StringWriter out = new StringWriter();

      //HttpServletResponse res = new MockResponse(out);
      
      SemaphoreServerServlet servlet = new SemaphoreServerServlet();
      
   }
}
