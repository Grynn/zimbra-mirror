package com.zimbra.qa.selenium.framework.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.InputStreamReader;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

class StreamGobbler extends Thread
{
   InputStream is;
   protected static Logger logger = LogManager.getLogger(StreamGobbler.class);

   StreamGobbler(InputStream is)
   {
      this.is = is;
   }

   public void run()
   {
      try
      {
         InputStreamReader isr = new InputStreamReader(is);
         BufferedReader br = new BufferedReader(isr);
         String line = null;
         while ( (line = br.readLine()) != null)
               logger.info(line);    
       } catch (IOException ioe) {
          ioe.printStackTrace();  
       }
    }
}

public class CommandLine {
	private static Logger logger = LogManager.getLogger(CommandLine.class);

	/**
	 * Execute Command line with no STDIN parameter and return the execution status
	 * @param command Command line to be executed
	 * @return (Integer) Execution status code
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public static int CmdExec(String command)
	throws IOException, InterruptedException {
	   return CmdExec(command, null);
	}

   /**
    * Execute Command line and return the execution status
    * @param command Command line to be executed
    * @param params Parameter to be passed to STDIN
    * @return (Integer) Execution status code
    * @throws IOException
    * @throws InterruptedException
    */
   public static int CmdExec(String command, String[] params)
   throws IOException, InterruptedException {
      return CmdExec(command, params, false);
   }

   /**
	 * Execute Command line and return the execution status
	 * @param command Command line to be executed
	 * @param params Parameter to be passed to STDIN
	 * @param background Running in the background process
	 * @return (Integer) Execution status code
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public static int CmdExec(String command, String[] params, boolean background)
	throws IOException, InterruptedException {
		Process p = Runtime.getRuntime().exec(command);

		StreamGobbler errorGobbler = new
		      StreamGobbler(p.getErrorStream());
		StreamGobbler outputGobbler = new
            StreamGobbler(p.getInputStream());
		errorGobbler.start();
		outputGobbler.start();

		if (params != null) {
		   OutputStream outputStream = p.getOutputStream();
		   for (int i = 0; i < params.length; i++) {
		      outputStream.write(params[i].getBytes());
		      outputStream.flush();
		   }
		   outputStream.close();
		}
		int exitValue = -1;

		if (!background) {
		   exitValue = p.waitFor();
		}

		logger.info(command + " - " + exitValue);
		return (exitValue);
	}

   /**
    * Execute command line with no params and return the output as a String
    * @param command Command line to be executed
    * @return (String) output from the console
    * @throws IOException
    * @throws InterruptedException
    */
   public static String cmdExecWithOutput(String command)
   throws IOException, InterruptedException {
      return cmdExecWithOutput(command, null);
   }

   /**
	 * Execute command line with parameters and return the output as a String
	 * @param command Command line to be executed
	 * @param params Parameter to be passed to STDIN
	 * @return (String) output from the console
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public static String cmdExecWithOutput(String command, String[] params) throws IOException, InterruptedException {
	   logger.debug("Executing command: " + command);
      Process process = Runtime.getRuntime().exec(command);

      InputStream inputStream = process.getInputStream();
      StreamGobbler errorGobbler = new
            StreamGobbler(process.getErrorStream());
      StreamGobbler outputGobbler = new
            StreamGobbler(inputStream);

      errorGobbler.start();
      outputGobbler.start();

      if (params != null) {
         OutputStream outputStream = process.getOutputStream();
         for (int i = 0; i < params.length; i++) {
            outputStream.write(params[i].getBytes());
            outputStream.flush();
         }
         outputStream.close();
      }

      StreamReader reader = new StreamReader(inputStream);
      logger.debug("Starting the reader thread");
      reader.start();
      process.waitFor();
      reader.join();
      String output = reader.getResult();
      return output;
	}
}
