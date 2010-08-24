package framework.core;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.openqa.selenium.server.RemoteControlConfiguration;
import org.openqa.selenium.server.SeleniumServer;

import framework.util.CommandLine;
import framework.util.HarnessException;
import framework.util.SleepUtil;
import framework.util.ZimbraSeleniumProperties;

public class SeleniumService {
	private static Logger logger = LogManager.getLogger(SeleniumService.class);

	
	public enum SeleniumMode {
		Local, Remote, Grid, SauceLabs
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
				
				stopBrowsers();
				
				RemoteControlConfiguration rcConfig = new RemoteControlConfiguration();
				rcConfig.setPort(SeleniumPort);
				rcConfig.setUserExtensions(new File("src/java/framework/lib/user-extensions.js"));
				ss = new SeleniumServer(false, rcConfig);
				
				BufferedReader in = null;
				try {

					URI stopUri = new URI("http", null, SeleniumServer, SeleniumPort, "/selenium-server/driver", "cmd=shutDownSeleniumServer", null);
					String s = stopUri.toString();
					logger.debug("Connecting to "+ s);
					in = new BufferedReader(new InputStreamReader(stopUri.toURL().openStream()));
					if ( in.ready() )
						logger.info("A Selenium Server was running already.  Attempting to kill and start then");
						
					String line;
					while ((line = in.readLine()) != null)
						logger.info(line);

				} catch (Exception e) {
					logger.debug("SeleniumServer was not running.  Ignoring.", e);
				} finally {
					if ( in != null )
						in.close();
				}
				
				// TODO: any way to detect that the server is ready?
				SleepUtil.sleep(10000);
				
				ss.boot();

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
					BufferedReader in = new BufferedReader(new InputStreamReader(stopUri.toURL().openStream()));
			
					if (in.ready())
						logger.info("A Selenium Server was not stopped. Attempting to kill");
						
					String line;
					while ((line = in.readLine()) != null)
						logger.info(line);

					in.close();
					
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
		try {
			stopBrowsersXP();
			stopBrowsersUbuntu();
		} catch (Exception e) {
			throw new HarnessException(e);
		}
	}
	
	private void stopBrowsersXP() throws IOException, InterruptedException {
		if (!SelNGBase.suiteName.equals("debugSuite")) {
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
		SeleniumBrowser = ZimbraSeleniumProperties.getStringProperty("browser");
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
	          "\"user-extensions-url\": \"http://" + ZimbraSeleniumProperties.getStringProperty("server") + ":8080/user-extensions.js\"}";
		
		} else {
			
			logger.error("Unknown seleniumMode "+ modeProp + ".  Using "+ SeleniumMode.Local);
		
		}
	}

	
	
}
