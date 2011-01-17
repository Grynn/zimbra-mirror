package com.zimbra.qa.selenium.framework.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

/**
 * Class for StreamReader, which extends Thread class
 * to ensure that it always synchronizes
 * @author Jeffry Hidayat
 *
 */
public class StreamReader extends Thread {
   private InputStream is;
   private StringWriter sw = new StringWriter();
   public StreamReader(InputStream is) {
      this.is = is;
   }

   /**
    * Run method of the StreamReader class
    */
   public void run() {
      try {
         int c;
         while ((c = is.read()) != -1)
            sw.write(c);
      } catch (IOException e) {

      }
   }

   /**
    * Get the result of the Reader
    * @return StringWriter object that was written by run Method
    */
   public String getResult() {
      return sw.toString();
   }
}
