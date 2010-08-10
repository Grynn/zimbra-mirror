package framework.util;

import java.io.IOException;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class CommandLine {
	private static Logger logger = LogManager.getLogger(CommandLine.class);
	
	public static int CmdExec(String command) throws IOException, InterruptedException {
		Process p = Runtime.getRuntime().exec(command);
		int exitValue = p.waitFor();
		logger.info(command + " - " + exitValue);
		return (exitValue);
	}
}
