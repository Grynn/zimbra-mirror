package staf;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.openqa.selenium.server.RemoteControlConfiguration;
import org.openqa.selenium.server.SeleniumServer;

import framework.util.HarnessException;


public class StafSeleniumServer {
	private static Logger logger = LogManager.getLogger(StafSeleniumServer.class);

	public static final String defaultUserExtensionsFilename = "/p4/matt/main/ZimbraSelenium/src/java/framework/lib/user-extensions.js";
    public static final String defaultSeleniumServerHost	= "localhost";
    public static final int defaultSeleniumServerPort		= 4445;

	private SeleniumServer seleniumServer = null;
	private String operatingSystem = null;


	
	/**
	 * Start the appropriate selenium server, as per config.properties settings
	 * @throws HarnessException 
	 */
	public void startSeleniumServer() throws HarnessException {
		logger.info("StafSeleniumServer.startSeleniumServer()");
		
		
		RemoteControlConfiguration rcc = new RemoteControlConfiguration();
		rcc.setPort(defaultSeleniumServerPort);
		rcc.setUserExtensions(new File(defaultUserExtensionsFilename));
		
		try {
			stopBrowsers();
			seleniumServer = new SeleniumServer(false, rcc);
			seleniumServer.boot(); // required to load the user-extensions.js
		} catch (Exception e) {
			throw new HarnessException("Unable to start SeleniumServer", e);
		}
				
		
	}
	
	/**
	 * Stop the selenium server
	 */
	public void stopSeleniumServer()  {
		logger.info("StafSeleniumServer.stopSeleniumServer()");
	
		seleniumServer.stop();
		
	}
	
	public boolean isRunning() throws HarnessException {
		logger.info("StafSeleniumServer.isRunning()");
		boolean status = false;
		try {
			
			// If the socket can be opened, then assume selenium is running
			ServerSocket socket = null;
			try {
				socket = new ServerSocket(defaultSeleniumServerPort);
			} finally {
				if ( socket != null ) socket.close();
			}
		} catch (IOException e) {
			status = true;
		}
		
		logger.info("StafSeleniumServer.isRunning() == " + status);
		return (status);
	}

	
	public void stopBrowsers() throws HarnessException {
		try {
			stopBrowsersXP();
			stopBrowsersUbuntu();
		} catch (Exception e) {
			throw new HarnessException(e);
		}
	}
	
	private void stopBrowsersXP() throws IOException, InterruptedException {
		if ( operatingSystem.startsWith("Windows") ) {
			CommandLine.CmdExec("taskkill /f /t /im iexplore.exe");
			CommandLine.CmdExec("taskkill /f /t /im firefox.exe");
			CommandLine.CmdExec("taskkill /f /t /im Safari.exe");
			CommandLine.CmdExec("taskkill /f /t /im chrome.exe");
		}
	}

	private void stopBrowsersUbuntu() throws IOException, InterruptedException {
		// TODO
		logger.warn("Implement me!");
	}
	

	

	/*
	 * Singleton methods
	 */
	
	/**
	 * Get the SeleniumService instance
	 * @return the SeleniumService
	 */
	public static StafSeleniumServer getInstance() {
		if (Instance == null) {
			synchronized(StafSeleniumServer.class) {
				if ( Instance == null) {
					Instance = new StafSeleniumServer();
				}
			}
		}
		return (Instance);
	}
	private volatile static StafSeleniumServer Instance;
	
	private StafSeleniumServer() {	
		logger.info("New StafSeleniumServer object");
		
		operatingSystem = System.getProperty("os.name");
		
	}

	private static class CommandLine {	
		private static int CmdExec(String command) throws IOException, InterruptedException {
			Process p = Runtime.getRuntime().exec(command);
			int exitValue = p.waitFor();
			logger.info(command + " - " + exitValue);
			return (exitValue);
		}
	}

	
}
