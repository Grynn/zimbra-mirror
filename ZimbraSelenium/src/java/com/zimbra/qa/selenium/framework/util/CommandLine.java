package com.zimbra.qa.selenium.framework.util;

import java.io.IOException;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class CommandLine {
	private static Logger logger = LogManager.getLogger(CommandLine.class);

	/**
	 * Execute Command line and return the execution status
	 * @param command Command line to be executed
	 * @return (Integer) Execution status code
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public static int CmdExec(String command) throws IOException, InterruptedException {
		Process p = Runtime.getRuntime().exec(command);
		int exitValue = p.waitFor();
		logger.info(command + " - " + exitValue);
		return (exitValue);
	}

	/**
	 * Execute command line and return the output as a String
	 * @param command Command line to be executed
	 * @return (String) output from the console
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public static String cmdExecWithOutput(String command) throws IOException, InterruptedException {
	   logger.debug("Executing command: " + command);
      Process process = Runtime.getRuntime().exec(command);

      StreamReader reader = new StreamReader(process.getInputStream());
      logger.debug("Starting the reader thread");
      reader.start();
      process.waitFor();
      reader.join();
      String output = reader.getResult();
      return output;
	}
}
