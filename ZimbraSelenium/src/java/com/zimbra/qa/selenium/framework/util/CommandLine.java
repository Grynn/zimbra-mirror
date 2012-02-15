package com.zimbra.qa.selenium.framework.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Calendar;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

class StreamGobbler extends Thread
{
   InputStream is;
   protected static Logger logger = LogManager.getLogger(StreamGobbler.class);
   StringBuilder output = new StringBuilder("");

   StreamGobbler(InputStream is)
   {
      this.is = is;
   }

   public String getOutput() {
      return this.output.toString();
   }

   public void run()
   {
      try
      {
         InputStreamReader isr = new InputStreamReader(is);
         BufferedReader br = new BufferedReader(isr);
         String line = null;
         while ( (line = br.readLine()) != null)
            this.output.append(line).append("\n");   
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
    * @throws HarnessException 
    */
   public static String cmdExecWithOutput(String command)
   throws IOException, InterruptedException, HarnessException {
      return cmdExecWithOutput(command, null);
   }

   /**
	 * Execute command line with parameters and return the output as a String
	 * @param command Command line to be executed
	 * @param params Parameter to be passed to STDIN
	 * @return (String) output from the console
	 * @throws IOException
	 * @throws InterruptedException
    * @throws HarnessException 
	 */
	public static String cmdExecWithOutput(String command, String[] params)
	throws IOException, InterruptedException, HarnessException {
	   logger.debug("Executing command: " + command);
      Process process = Runtime.getRuntime().exec(command);

      return _startStreaming(process, params);
	}

	/**
    * Execute (tokenized) command line with parameters and return the output as a String
    * @param command Command line to be executed
    * @param params Parameter to be passed to STDIN
    * @return (String) output from the console
    * @throws IOException
    * @throws InterruptedException
    * @throws HarnessException 
    */
	public static String cmdExecWithOutput(String [] command, String[] params)
	throws IOException, InterruptedException, HarnessException {
	   logger.debug("Executing command: " + Arrays.toString(command));
	   Process process = Runtime.getRuntime().exec(command);

	   return _startStreaming(process, params);
	}


	/**
	 * Streaming the input and output from the command line execution
	 * @param process
	 * @param params
	 * @return Aggregated output from the command line execution
	 * @throws IOException
	 * @throws InterruptedException
	 */
	private static String _startStreaming(Process process, String[] params)
	throws IOException, InterruptedException {
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


      long startTime = Calendar.getInstance().getTimeInMillis();

      while ((Calendar.getInstance().getTimeInMillis() - startTime) < 30000 &&
            (errorGobbler.isAlive() || outputGobbler.isAlive())) {
    	  continue;
      }

      logger.debug("Starting the reader thread");
      process.waitFor();
      String output = outputGobbler.output.toString() + errorGobbler.output.toString();
      return output;
	}
}
