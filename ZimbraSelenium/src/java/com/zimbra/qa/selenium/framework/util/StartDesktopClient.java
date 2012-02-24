package com.zimbra.qa.selenium.framework.util;

import java.io.IOException;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class StartDesktopClient extends Thread {
	protected static final Logger logger = LogManager.getLogger(StartDesktopClient.class);


	private String[] executablePath = null;
	private String[] params = null;

	public StartDesktopClient(String[] executablePath, String[] params) {

		this.executablePath = (String[])executablePath.clone();
		this.params = (String [])params.clone();

	}

	public void run() {
		try {
			logger.info(CommandLine.cmdExecWithOutput(executablePath, params));
		} catch (HarnessException e) {
			logger.error("Getting Harness Exception", e);
		} catch (IOException e) {
			logger.error("Getting IOException", e);
		} catch (InterruptedException e) {
			logger.error("Getting InterruptedException", e);
		}
	}
}