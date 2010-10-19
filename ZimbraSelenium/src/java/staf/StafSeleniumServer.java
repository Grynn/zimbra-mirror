package staf;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.URL;
import java.net.URLConnection;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.openqa.selenium.server.RemoteControlConfiguration;
import org.openqa.selenium.server.SeleniumServer;

import com.ibm.staf.STAFException;
import com.ibm.staf.STAFHandle;
import com.ibm.staf.STAFResult;

import framework.util.HarnessException;


public class StafSeleniumServer {
	private static Logger logger = LogManager.getLogger(StafSeleniumServer.class);

	public static final String defaultUserExtensionsURI		= "http://zqa-004.eng.vmware.com/files/user-extensions.js";
    public static final String defaultSeleniumServerHost	= "localhost";
    public static final int defaultSeleniumServerPort		= 4445;

	private SeleniumServer seleniumServer = null;
	private static String operatingSystem = null;


	private File getUserExtensionsFile(String uri) throws HarnessException {
		
		
		String filename = "/tmp/user-extensions.js";
		File file = new File(filename);

		try {

			OutputStream out = null;
			InputStream in = null;

			try {

				// Open the OutputStream for writing
				out = new FileOutputStream(file);

				// Open the URL for reading
				URL u = new URL(uri);
				URLConnection uc = u.openConnection();
				in = uc.getInputStream();

				// Stream the URL to the File
				byte[] buffer = new byte[1024];
				int length;
				while ((length=in.read(buffer))>0) {
					out.write(buffer, 0 , length);
				}

			} finally {

				// Remember to close pointers.
				if ( in != null )			in.close();
				if ( out != null )			out.close();
				
			}
			
		} catch (IOException e) {
			throw new HarnessException("Unable to read user-extensions from "+ uri, e);
		}

		return (file);
	}
	
	/**
	 * Start the appropriate selenium server, as per config.properties settings
	 * @throws HarnessException 
	 */
	public void startSeleniumServer() throws HarnessException {
		logger.info("StafSeleniumServer.startSeleniumServer()");
		
		
		RemoteControlConfiguration rcc = new RemoteControlConfiguration();
		rcc.setPort(defaultSeleniumServerPort);
		rcc.setUserExtensions(getUserExtensionsFile(defaultUserExtensionsURI));
		rcc.setTrustAllSSLCertificates(true);
		
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

	
	public static void stopBrowsers() throws HarnessException {
		try {
			stopBrowsersXP();
			stopBrowsersUbuntu();
		} catch (Exception e) {
			throw new HarnessException(e);
		}
	}
	
	private static void stopBrowsersXP() throws HarnessException {
		if ( operatingSystem.startsWith("Windows") ) {
			KillProcess.kill("iexplore.exe");
			KillProcess.kill("firefox.exe");
			KillProcess.kill("Safari.exe");
			KillProcess.kill("chrome.exe");
		}
	}

	private static void stopBrowsersUbuntu() throws IOException, InterruptedException {
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

	
	private static class KillProcess {
		public static void kill(String processname) throws HarnessException {
			KillProcess kill = new KillProcess(processname);
			kill.exec();
		}
		
		
		private String processName;
		private KillProcess(String p) {
			processName = p;
		}
		private void exec() throws HarnessException {
			
			// TODO: Handle different operating systems besides Windows (i.e. taskkill)
			
			String server = "localhost";
			String service = "PROCESS";
			String command = "START SHELL COMMAND \"taskkill /f /t /im "+ processName +"\" RETURNSTDOUT RETURNSTDERR WAIT";
			
			STAFHandle handle = null;
			STAFResult result = null;
			
			try
			{
				
				handle = new STAFHandle(KillProcess.class.getName());
				
		        try
		        {
		        			        			            
		            result = handle.submit2(server, service, command);
		            if (result.rc != STAFResult.Ok) {
		            	// TODO: Handle errors
		            }
	 
				} finally {
		        	
		            try {
		            	
						handle.unRegister();
				    	
					} catch (STAFException e) {
			        	throw new HarnessException("Error unregistering with STAF, RC:" + e.rc, e);
					}
					
				}
	        
			}
			catch (STAFException e)
			{
	        	throw new HarnessException("Error registering or unregistering with STAF, RC:" + e.rc, e);
			}
		        	

		}
		
	}

	
}
