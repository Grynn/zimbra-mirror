package com.zimbra.qa.selenium.framework.util;

import java.io.*;
import java.net.*;
import java.util.*;

import org.apache.log4j.*;


/**
 * These properties define client-specific configuration values.
 * <p>
 * In the Zimbra labs, test clients are hard coded to use a specific
 * test configuration of OS, browser, language, theme, etc.  The
 * configuration properties are defined in a file named 
 * "client.properties.hostname", where hostname is the client's IP address,
 * fully qualified host name, or hostname, in that order.  If none
 * of those files exist, "client.properties" will be used.  And, if
 * that file does not exist, an empty properties table will be used.
 * <p>
 * This singleton class opens the file and serves the client specific
 * properties.
 * <p>
 * 
 * 
 * @author Matt Rhoades
 *
 */
public class ZimbraClientProps {
	public static Logger logger = LogManager.getLogger(ZimbraClientProps.class);

	/**
	 * Determine if the Client Properties contains the specified key
	 * @param key
	 * @return
	 */
	public boolean containsProperty(String key) {
		return (MyProperties.containsKey(key));
	}
	
	/**
	 * Get the value for the specified key
	 * @param key
	 * @return
	 * @throws HarnessException if the key does not exist
	 */
	public String getProperty(String key) throws HarnessException {
		String value = MyProperties.getProperty(key, null);
		if ( value == null )
			throw new HarnessException("Unable to find value for key("+ key +")");
		return (value);
	}

	/**
	 * Get the value for the specified key, or return the defaultValue if it doesn't exist
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	public String getProperty(String key, String defaultValue) {
		return (MyProperties.getProperty(key, defaultValue));
	}
	
	
	public void load() throws HarnessException {
		
		String clientHostname = "undefined";
		String clientFQDN = "undefined.com";
		String clientIPaddress = "0.0.0.0";

		try {
			
			// Determine the local hostname and IP address
			
			InetAddress client = InetAddress.getLocalHost();
			clientFQDN = client.getCanonicalHostName();
			clientHostname = client.getHostName();
			clientIPaddress = client.getHostAddress();
			
		} catch (UnknownHostException e) {
			logger.error("Unable to determine hostnames.  Using defaults.", e);
		}

		// Determine where the ZimbraSelenium/conf folder is located
		File confFolder = new File(ZimbraSeleniumProperties.getBaseDirectory(), "conf");
		
		// Build a list of possible properties files
		// Primary: conf/client.properties.IPAddress
		// Secondary: conf/client.properties.Hostname
		// Default: conf/client.properties
		List<File> files = new ArrayList<File>();
		files.add(new File(confFolder, "client.properties" + "." + clientIPaddress));
		files.add(new File(confFolder, "client.properties" + "." + clientFQDN));
		files.add(new File(confFolder, "client.properties" + "." + clientHostname));
		files.add(new File(confFolder, "client.properties"));
		
		for ( File file : files ) {
			logger.info("Checking for client file: "+ file.getAbsolutePath());
			
			if ( file.exists() ) {
				logger.info("exists: "+ file.getAbsolutePath());
				
				try {
					load(file);
				} catch (FileNotFoundException e) {
					throw new HarnessException("Unable to load file: "+ file.getAbsolutePath(), e);
				} catch (IOException e) {
					throw new HarnessException("Unable to load file: "+ file.getAbsolutePath(), e);
				}
				
				return; // done!
				
			}
		}
		
		throw new HarnessException("Unable to open any client properties");

	}
	
	/**
	 * Load the properties using the specified properties file
	 * @param properties
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public void load(File properties) throws FileNotFoundException, IOException {
		MyProperties = new Properties();
		MyProperties.load(new FileInputStream(properties));
		logger.info("Loaded client.properties: "+ properties.getCanonicalPath());
	}
	

	/**
	 * The properties for this client
	 */
	protected Properties MyProperties = null;
	
		
	
	/**
	 * Singleton methods
	 */
	
	private volatile static ZimbraClientProps instance;
	private ZimbraClientProps() {
		logger.info("new ZimbraClientProps()");
		try {
			load();
		} catch (HarnessException e) {
			logger.error("Unable to load properties.  Using blank");
			MyProperties = new Properties();
		}
	}
	
	public static ZimbraClientProps getInstance() {
		if ( instance == null ) {
			synchronized(ZimbraClientProps.class){
				if ( instance == null ) {
					instance = new ZimbraClientProps();
				}
			}
		}
		return (instance);
	}
	
}
