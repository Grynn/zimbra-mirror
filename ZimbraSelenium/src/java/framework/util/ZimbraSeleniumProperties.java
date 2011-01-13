//helper class for retrieving properties
package framework.util;

import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class ZimbraSeleniumProperties {
	private static final Logger logger = LogManager.getLogger(ZimbraSeleniumProperties.class);
	
	// Use these strings as arguments for some standard properties, e.g. ZimbraSeleniumProperties.getStringProperty(PropZimbraServer, "default");
	public static final String PropZimbraVersion = "zimbraserverversion"; 
	
	private static ZimbraSeleniumProperties instance = null;
	private File BaseDirectory = null;
	private File PropertiesConfigurationFilename = null;	
	private PropertiesConfiguration configProp;
	
	public static void setStringProperty(String key,String value) {
		ZimbraSeleniumProperties.getInstance().getConfigProp().setProperty(key, value);
	}
	
	public static String getStringProperty(String key, String defaultValue) {
		return (ZimbraSeleniumProperties.getInstance().getConfigProp()
				.getString(key, defaultValue));
	}

	public static String getStringProperty(String key) {
		return (getStringProperty(key, null));
	}
	
	public static int getIntProperty(String key) {
		return (getIntProperty(key, 0));
	}

	public static int getIntProperty(String key, int defaultValue) {
		String value = ZimbraSeleniumProperties.getInstance().getConfigProp().getString(key, null);
		if ( value == null )
			return (defaultValue);
		return (Integer.parseInt(value));
	}

	private static int counter = 0;
	public static String getUniqueString() {
		return ("" + System.currentTimeMillis() + (++counter));
	}

	public static ResourceBundle getResourceBundleProperty(String key) {
		return ((ResourceBundle) ZimbraSeleniumProperties.getInstance()
				.getConfigProp().getProperty(key));
	}

	public static PropertiesConfiguration getConfigProperties() {
		return ZimbraSeleniumProperties.getInstance().getConfigProp();
	}
	
	public static PropertiesConfiguration setConfigProperties(String filename) {
		logger.info("setConfigProperties using: "+ filename);
		ZimbraSeleniumProperties.getInstance().PropertiesConfigurationFilename = new File(filename);
		ZimbraSeleniumProperties.getInstance().init();
		return (ZimbraSeleniumProperties.getInstance().getConfigProp());
	}

	public static String getBaseDirectory() {
		if (ZimbraSeleniumProperties.getInstance().BaseDirectory == null)
			return (".");
		return (ZimbraSeleniumProperties.getInstance().BaseDirectory.getAbsolutePath());
	}
	
	public static File setBaseDirectory(String directory) {
		logger.info("setWorkingDirectory using: "+ directory);
		ZimbraSeleniumProperties.getInstance().BaseDirectory = new File(directory);
		return (ZimbraSeleniumProperties.getInstance().BaseDirectory);
	}
	
	private PropertiesConfiguration getConfigProp() {
		return configProp;
	}

	private static ZimbraSeleniumProperties getInstance() {
		if ( instance == null ) {
			synchronized(ZimbraSeleniumProperties.class) {
				if ( instance == null ) {
					instance = new ZimbraSeleniumProperties();
					instance.init();
				}
			}
		}
		return instance;
	}

	private ZimbraSeleniumProperties() {
		logger.debug("new ZimbraSeleniumProperties");
	}

	private void init() {

		// Load the config.properties values
		if ( PropertiesConfigurationFilename == null ) {
			logger.info("config.properties is default");
			configProp = createDefaultProperties();
		} else {
			try {
				logger.info("config.properties is "+ PropertiesConfigurationFilename.getAbsolutePath());
				configProp = new PropertiesConfiguration(PropertiesConfigurationFilename);
			} catch (ConfigurationException e) {
				ZimbraSeleniumLogger.mLog.error("Unable to open config file: " + PropertiesConfigurationFilename.getAbsolutePath(), e);
				logger.info("config.properties is default");
				configProp = createDefaultProperties();
			}
		}


		// Load the locale information
		String locale = configProp.getString("locale");

		configProp.setProperty("zmMsg", ResourceBundle.getBundle("ZmMsg", new Locale(locale)));

		configProp.setProperty("zhMsg", ResourceBundle.getBundle("ZhMsg", new Locale(locale)));

		configProp.setProperty("ajxMsg", ResourceBundle.getBundle("AjxMsg", new Locale(locale)));

		configProp.setProperty("i18Msg", ResourceBundle.getBundle("I18nMsg", new Locale(locale)));

		configProp.setProperty("zsMsg", ResourceBundle.getBundle("ZsMsg", new Locale(locale)));

	}

	private PropertiesConfiguration createDefaultProperties() {
		PropertiesConfiguration defaultProp = new PropertiesConfiguration();

		defaultProp.setProperty("browser", "FF3");

		defaultProp.setProperty("runMode", "DEBUG");

		defaultProp.setProperty("product", "zcs");

		defaultProp.setProperty("locale", "en_US");

		defaultProp.setProperty("intl", "us");

		defaultProp.setProperty("testdomain", "testdomain.com");

		defaultProp.setProperty("multiWindow", "true");

		defaultProp.setProperty("objectDataFile",
				"projects/zcs/data/objectdata.xml");

		defaultProp.setProperty("testDataFile",
				"projects/zcs/data/testdata.xml");

		defaultProp.setProperty("serverMachineName", "localhost");

		defaultProp.setProperty("serverport", "4444");

		defaultProp.setProperty("mode", "http");

		defaultProp.setProperty("server", "qa60.lab.zimbra.com");

		defaultProp.setProperty("ZimbraLogRoot", "test-output");

		defaultProp.setProperty("adminName", "admin");

		defaultProp.setProperty("adminPwd", "test123");

		defaultProp.setProperty("small_wait", "1000");

		defaultProp.setProperty("medium_wait", "2000");

		defaultProp.setProperty("long_wait", "4000");

		defaultProp.setProperty("very_long_wait", "10000");

		String locale = defaultProp.getString("locale");

		defaultProp.setProperty("zmMsg", ResourceBundle.getBundle("ZmMsg", new Locale(locale)));

		defaultProp.setProperty("zhMsg", ResourceBundle.getBundle("ZhMsg", new Locale(locale)));

		defaultProp.setProperty("ajxMsg", ResourceBundle.getBundle("AjxMsg", new Locale(locale)));

		defaultProp.setProperty("i18Msg", ResourceBundle.getBundle("I18nMsg", new Locale(locale)));

		defaultProp.setProperty("zsMsg", ResourceBundle.getBundle("ZsMsg", new Locale(locale)));

		return defaultProp;
	}

	private static class CurClassGetter extends SecurityManager {
		private Class<?> getCurrentClass() {
			return getClassContext()[1];
		}
	}

	/**
	 * App type
	 */
	public enum AppType {
		AJAX, HTML, MOBILE, DESKTOP, ADMIN, APPLIANCE
	}
	
	private static AppType appType = AppType.AJAX;
	public static void setAppType(AppType type) {
		appType = type;
	}
	public static AppType getAppType() {
		return (appType);
	}

	public final static String [] possibleFiles = {
	   "/opt/zmdesktop/zimbra/zdesktop/conf/localconfig.xml",
	   "/home/zmdesktop/zimbra/zdesktop/conf/localconfig.xml",
	   "C:\\Documents and Settings\\<USER_NAME>\\Local Settings\\Application Data\\Zimbra\\Zimbra Desktop\\conf\\localconfig.xml"
	};	   

	public enum OsType {
	   WINDOWS, LINUX, MAC
	}

	/**
	 * Get the OS type from the system information
	 * @return OS Type (Windows, MAC, or Linux)
	 */
	public static OsType getOSType() {
	   String os = System.getProperty("os.name").toLowerCase();
	   logger.info("os.name is: " + os);
	   OsType osType = null;
	   if (os.indexOf("win") >= 0) {
	      osType = OsType.WINDOWS;
	   } else if (os.indexOf("mac") >= 0) {
	      osType = OsType.MAC;
	   } else if (os.indexOf("nix") >= 0 || os.indexOf("nux") >= 0) {
	      osType = OsType.LINUX;
	   }
	   return osType;
	}

	/**
    * Get value out of a specified element's name in XML file
    * @param xmlFile XML File to look at
    * @param elementName Element name, in which the value is wanted
    * @return (String) Element's value
    */
	public static String parseXmlFile(String xmlFile, String elementName) {
      DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
      String output = null;
      try {
         File file = new File(xmlFile);
         DocumentBuilder db = dbf.newDocumentBuilder();
         Document doc = db.parse(file);
         doc.getDocumentElement().normalize();
         NodeList nodeLst = doc.getDocumentElement().
               getElementsByTagName("key");
         for (int i = 0; i < nodeLst.getLength(); i++) {
            Node currentNode = nodeLst.item(i);
            Element currentElement = (Element)currentNode;
            String keyName = currentElement.getAttribute("name");
            if (!keyName.equals(elementName)) {
               continue;
            } else {
               Element value = (Element)currentElement.
                     getElementsByTagName("value").item(0);
               output = value.getChildNodes().item(0).getNodeValue();
               break;
            }
         }
      } catch(ParserConfigurationException pce) {
         pce.printStackTrace();
      }catch(SAXException se) {
         se.printStackTrace();
      }catch(IOException ioe) {
         ioe.printStackTrace();
      }
      return output;
   }

	/**
	 * Get Base URL for selenium to open to access the application
	 * under test
	 * @return Base URL
	 */
	public static String getBaseURL() {
		String scheme = ZimbraSeleniumProperties.getStringProperty("server.scheme", "http");
		String host = ZimbraSeleniumProperties.getStringProperty("server.host", "localhost");
		String port = ZimbraSeleniumProperties.getStringProperty("server.port", "7070");

		if ( appType == AppType.DESKTOP ) {
		   OsType osType = getOSType();
		   logger.info("AppType is: " + appType);
		   logger.info("OS Type is: " + osType);

		   for (int i = 0; i < possibleFiles.length; i++) {
		      if (osType == OsType.WINDOWS) {
		         if (!possibleFiles[i].contains("C:\\")) {
		            continue;
		         } else {
		            String currentLoggedInUser = System.getProperty(
		                  "user.name");
		            logger.info("currentLoggedInUser: " +
		                  currentLoggedInUser);
		            possibleFiles[i] = possibleFiles[i].replace(
		                  "<USER_NAME>", currentLoggedInUser);
		         }
		      } else {
		         if (possibleFiles[i].contains("C:\\")) {
                  continue;
		         }
		      }
		      logger.info("Parsing XML file: " + possibleFiles[i]);
		      port = parseXmlFile(possibleFiles[i],
		            "zimbra_admin_service_port");
		      String serialNumber = parseXmlFile(possibleFiles[i],
		            "zdesktop_installation_key");
		      String baseUrl = scheme + "://" + host + ":" + port +
            "/desktop/login.jsp?at=" + serialNumber;

		      logger.info("Base URL is: " + baseUrl);

		      return (baseUrl);
		   }
		}

		if ( appType == AppType.AJAX ) {
			return (scheme + "://"+ host + ":" + port);
		}

		if ( appType == AppType.HTML ) {
			return (scheme + "://"+ host + ":" + port + "/h/");
		}

		if ( appType == AppType.MOBILE ) {
			return (scheme + "://"+ host + ":" + port + "/m/");
		}

		if(ZimbraSeleniumProperties.getStringProperty("runCodeCoverage", "no").equalsIgnoreCase("yes")) {
			return (scheme +"://"+ host + ":"+ port +"?dev=1&debug=0");
		}

		if ( appType == AppType.ADMIN ) {
			return ("https://"+ host +":7071");
		}

		// Default
		logger.warn("Using default URL");
		return (scheme +"://"+ host);
	}

	public static String zimbraGetVersionString() throws HarnessException {		
		ZimbraAdminAccount.GlobalAdmin().soapSend("<GetVersionInfoRequest xmlns='urn:zimbraAdmin'/>");
		String version = ZimbraAdminAccount.GlobalAdmin().soapSelectValue("//admin:info", "version");
		if ( version == null )
			throw new HarnessException("Unable to determine version from GetVersionInfoResponse "+ ZimbraAdminAccount.GlobalAdmin().soapLastResponse());
		
		// The version string looks like 6.0.7_GA_2470.UBUNTU8.NETWORK
		return (version);
	}


	// for unit test need to change access to public
	public static void main(String[] args) {
		ZimbraSeleniumLogger.setmLog(new CurClassGetter().getCurrentClass());

		System
				.setProperty("log4j.configuration",
						"file:///C:/log4j.properties");
		System.out.println(System.getProperty("log4j.configuration"));

		System.out.println(System.getProperty("user.dir"));

		String br = (String) ZimbraSeleniumProperties.getInstance()
				.getConfigProp().getProperty("browser");
		System.out.println(br);
		ZimbraSeleniumLogger.mLog.debug(br);

		ResourceBundle zmMsg = (ResourceBundle) ZimbraSeleniumProperties
				.getInstance().getConfigProp().getProperty("zmMsg");
		System.out.println(zmMsg.getLocale());
		ZimbraSeleniumLogger.mLog.debug(zmMsg.getLocale());
	}

}