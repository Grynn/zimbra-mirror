package com.zimbra.qa.selenium.framework.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.openqa.selenium.server.RemoteControlConfiguration;
import org.openqa.selenium.server.SeleniumServer;

import com.zimbra.qa.selenium.framework.util.CommandLine;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.OperatingSystem;
import com.zimbra.qa.selenium.framework.util.SleepUtil;
import com.zimbra.qa.selenium.framework.util.ZimbraSeleniumProperties;

import sun.net.www.protocol.http.HttpURLConnection;

/**
 * The <code>SeleniumService</code> class is used to start
 * a Selenium server instance, specifically for the Zimbra Selenium
 * Harness implementation.
 * <p>
 * 
 * @author Matt Rhoades
 *
 */
public class SeleniumService {
	private static Logger logger = LogManager.getLogger(SeleniumService.class);

	
	public enum SeleniumMode {
		Local, Remote, Grid, SauceLabs
	}
	
	
	/**
	 * Check http://service:port to determine if selenium is running
	 * @return true if running, false otherwise
	 * @throws HarnessException
	 */
	private boolean serviceIsServerRunning() throws HarnessException {
		logger.debug("isServerRunning");

		// Create the URL
		URL url = null;
		try {
			URI uri = new URI("http", null, SeleniumServer, SeleniumPort, null, null, null);
			url = uri.toURL();
		} catch (URISyntaxException e) {
			throw new HarnessException("Unable to build URL", e);
		} catch (MalformedURLException e) {
			throw new HarnessException("Unable to build URL", e);
		}
		
		// Connect to the URL, if successful, then server is up
		try {
			
			HttpURLConnection connection = null;
			
			try {
				
				connection = (HttpURLConnection)url.openConnection();
				connection.setRequestMethod("GET");
				connection.setDoOutput(true);
				connection.setReadTimeout(10000);
				connection.connect();
				
				int status = connection.getResponseCode();
				logger.debug("Selenium Service returned " + status);

			} finally {
				if (connection != null ){
					connection.disconnect();
					connection = null;
				}
			}
			
		} catch (IOException e) {
			logger.info("Selenium Service is not running at "+ url.toString());
			return (false);
		}
		
		logger.info("Selenium Service is running at "+ url.toString());
		return (true);
		

	}
	
	/**
	 * Do HTTP Get on http://service:port/selenium-server/driver?cmd=shutDownSeleniumServer
	 * @throws HarnessException
	 */
	private void serviceShutDownSeleniumServer() throws HarnessException {
		logger.debug("shutDownSeleniumServer");

		// Create the URL
		URL url = null;
		try {
			URI uri = new URI("http", null, SeleniumServer, SeleniumPort, "/selenium-server/driver", "cmd=shutDownSeleniumServer", null);
			url = uri.toURL();
		} catch (URISyntaxException e) {
			throw new HarnessException("Unable to build URL", e);
		} catch (MalformedURLException e) {
			throw new HarnessException("Unable to build URL", e);
		}
		
		// Connect to the URL, if successful, then server is up
		try {
			
			HttpURLConnection connection = null;
			BufferedReader reader = null;
			
			try {
				
				logger.info("shutDownSeleniumServer @ "+ url.toString());

				connection = (HttpURLConnection)url.openConnection();
				connection.setRequestMethod("GET");
				connection.setDoOutput(true);
				connection.setReadTimeout(10000);
				connection.connect();
				
				reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
				String line;
				while ((line = reader.readLine()) != null ) {
					logger.debug(line);
				}

			} finally {
				if ( reader != null ) {
					reader.close();
					reader = null;
				}
				if (connection != null ){
					connection.disconnect();
					connection = null;
				}
				
			}
			
		} catch (IOException e) {
			logger.debug("Selenium Service threw IOException", e);
		}
		
		// Wait for the service to shut down
		for (int i = 0; i < 10; i++) {
			logger.debug("Waiting for service to stop ...");
			SleepUtil.sleep(1000);
			if ( !serviceIsServerRunning() )
				break;
		}
	
	}
	
	
	/**
	 * Start the appropriate selenium server, as per config.properties settings
	 * @throws HarnessException 
	 */
	public void startSeleniumServer() throws HarnessException {
		logger.info("SeleniumService.startSeleniumServer()");
		
		
		try
		{
			if ( mode == SeleniumMode.Local ) {
				
				serviceShutDownSeleniumServer();
				stopBrowsers();
				
				RemoteControlConfiguration rcConfig = new RemoteControlConfiguration();
				rcConfig.setPort(SeleniumPort);
				ss = new SeleniumServer(false, rcConfig);				
				ss.boot();

				// Wait for the service to start
				for (int i = 0; i < 10; i++) {
					logger.info("Waiting for service to start ...");
					SleepUtil.sleep(1000);
					if ( serviceIsServerRunning() )
						break;
				}

			}
			
		} catch (Exception e) {
			throw new HarnessException("Unable to start selenium service", e);
		}
		
	}
	
	/**
	 * Stop the selenium server
	 */
	public void stopSeleniumServer() throws HarnessException {
		logger.info("SeleniumService.stopSeleniumServer()");
	
		try
		{
			if ( mode == SeleniumMode.Local ) {

				ss.stop();
				
				try {
					URI stopUri = new URI("http", null, SeleniumServer, SeleniumPort, "/selenium-server/driver", "cmd=shutDownSeleniumServer", null);
					BufferedReader in = null;
					try {
						
						in = new BufferedReader(new InputStreamReader(stopUri.toURL().openStream()));
						if (in.ready())
							logger.info("A Selenium Server was not stopped. Attempting to kill");
							
						String line;
						while ((line = in.readLine()) != null)
							logger.info(line);

					} finally {
						if ( in != null ) {
							in.close();
							in = null;
						}
					}
					
				} catch (IOException e) {
					logger.warn("Selenium server is stopped");
				}

			}
		} catch (Exception e) {
			throw new HarnessException("Unable to stop SeleniumService", e);
		}
	}
	
	
	/**
	 * Check the configured selenium mode
	 * @return true: if the mode matches, false: otherwise
	 */
	public boolean isSeleniumMode(SeleniumMode m) {
		return (mode == m);
	}
	
	public String getSeleniumServer() {
		return (SeleniumServer);
	}
	
	public int getSeleniumPort() {
		return (SeleniumPort);
	}
	
	public String getSeleniumBrowser() {
		return (SeleniumBrowser);
	}
	
	public String getSeleniumBrowserVersion() {
		return (SeleniumBrowserVersion);
	}
	
	private void stopBrowsers() throws HarnessException {
		stopBrowsersXP();
		stopBrowsersMac();
		stopBrowsersLinux();
	}
	
	private void stopBrowsersXP() throws HarnessException {
		
		// Only run for windows
		if ( !OperatingSystem.isWindows() )
			return;

		try {
			if (SeleniumBrowser.contains("iexplore")) {
			    CommandLine.CmdExec("taskkill /f /t /im iexplore.exe");
			} else if (SeleniumBrowser.contains("firefox")) {
				CommandLine.CmdExec("taskkill /f /t /im firefox.exe");
			} else if (SeleniumBrowser.contains("safariproxy")) {
			    CommandLine.CmdExec("taskkill /f /t /im safari.exe");
			} else if (SeleniumBrowser.contains("chrome")) {
				CommandLine.CmdExec("taskkill /f /t /im chrome.exe");
			}
			
		} catch (IOException e) {
			throw new HarnessException("Unable to kill browsers", e);
		} catch (InterruptedException e) {
			throw new HarnessException("Unable to kill browsers", e);
		}

	}

	private void stopBrowsersMac() throws HarnessException {
		// Only run for Mac
		if ( !OperatingSystem.isMac() )
			return;

		logger.warn("implement me!", new Throwable("implement me!"));
	}
	
	private void stopBrowsersLinux() throws HarnessException {
		// Only run for Linux
		if ( !OperatingSystem.isLinux() )
			return;

		logger.warn("implement me!", new Throwable("implement me!"));
	}
	

	private SeleniumMode mode;
	private String SeleniumServer;
	private int SeleniumPort;
	private String SeleniumBrowser;
	private String SeleniumBrowserVersion;
	
	private SeleniumServer ss;

	/*
	 * Singleton methods
	 */
	
	/**
	 * Get the SeleniumService instance
	 * @return the SeleniumService
	 */
	public static SeleniumService getInstance() {
		if (Instance == null) {
			synchronized(SeleniumService.class) {
				if ( Instance == null) {
					Instance = new SeleniumService();
				}
			}
		}
		return (Instance);
	}
	private volatile static SeleniumService Instance;
	
	private SeleniumService() {	
		logger.info("New SeleniumService object");
		
		String modeProp = ZimbraSeleniumProperties.getStringProperty("seleniumMode", "local").toLowerCase();
		logger.info("New SeleniumService object: "+ modeProp);
		
		// Set Defaults
		mode = SeleniumMode.Local;
		SeleniumServer = ZimbraSeleniumProperties.getStringProperty("serverName", "localhost");
		SeleniumPort = ZimbraSeleniumProperties.getIntProperty("serverPort", 4444);
		SeleniumBrowser = ZimbraSeleniumProperties.getStringProperty(
				ZimbraSeleniumProperties.getLocalHost() + ".browser",
				ZimbraSeleniumProperties.getStringProperty("browser")).split(
				"_")[0];
		SeleniumBrowserVersion = ZimbraSeleniumProperties.getStringProperty("browserVersion");

		if (modeProp.equals(SeleniumMode.Local.toString().toLowerCase())) {
			
			mode = SeleniumMode.Local;
		
		} else if (modeProp.equals(SeleniumMode.Remote.toString().toLowerCase())) {
			
			mode = SeleniumMode.Remote;
		
		} else if (modeProp.equals(SeleniumMode.Grid.toString().toLowerCase())) {
			
			mode = SeleniumMode.Grid;
			SeleniumServer = ZimbraSeleniumProperties.getStringProperty("grid.serverMachineName", "tbd.lab.zimbra.com");
			SeleniumPort = ZimbraSeleniumProperties.getIntProperty("grid.serverMachinePort", 4444);
		
		} else if (modeProp.equals(SeleniumMode.SauceLabs.toString().toLowerCase())) {
			
			mode = SeleniumMode.SauceLabs;
			SeleniumServer = ZimbraSeleniumProperties.getStringProperty("sauce.serverMachineName", "ondemand.saucelabs.com");
			SeleniumPort = ZimbraSeleniumProperties.getIntProperty("sauce.serverMachinePort", 80);
			SeleniumBrowser = "{\"username\": \"" + ZimbraSeleniumProperties.getStringProperty("sauceUsername") + "\"," +
	          "\"access-key\": \"" + ZimbraSeleniumProperties.getStringProperty("sauceAccessKey") + "\"," +
	          "\"os\": \"" + ZimbraSeleniumProperties.getStringProperty("OS", "Windows 2003") + "\"," +
	          "\"browser\": \"" + ZimbraSeleniumProperties.getStringProperty("browser") + "\"," +
	          "\"browser-version\": \"" + ZimbraSeleniumProperties.getStringProperty("browserVersion") + "\"," +
/* TODO: Adding the job name would be useful for finding the test videos in OnDemand
	          "\"job-name\": \"" + 	Current method or class name + "\"," +  */
	          "\"user-extensions-url\": \"http://" + ZimbraSeleniumProperties.getStringProperty("server.host") + ":8080/user-extensions.js\"}";
		
		} else {
			
			logger.error("Unknown seleniumMode "+ modeProp + ".  Using "+ SeleniumMode.Local);
		
		}
	}

	
	
}
