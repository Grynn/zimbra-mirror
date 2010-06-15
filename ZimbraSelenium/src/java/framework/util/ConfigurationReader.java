package framework.util;


import org.apache.commons.configuration.*;
//The following libraries are required for Apache Commons-Configuration usage:
//commons-configuration, commons-beanutils, commons-collections,
//commons-digester, commons-lang, commons-logging, xerces and dom4j
public class ConfigurationReader {
	
	Configuration config = null;
	
	public ConfigurationReader() {}
	
	public void load() throws Exception {
		try {
			ConfigurationFactory factory = new ConfigurationFactory();
			factory.setConfigurationFileName(this.getEnvConfigXMLPath());
			config = factory.getConfiguration();
		} catch (Exception exc) {
			throw new Exception(exc.getMessage());
		}
}
	
	
	public Configuration getConfiguration() throws Exception {
		if (config == null) {
			load();
		}
		return config;
	}

	private String getEnvConfigXMLPath() {
	 StringBuffer srtBuffer = new StringBuffer("configuration/cg/us");
	 // System env variable is mapped to configuration/[env] folder
	 //srtBuffer.append(System.getProperty("product") + "/");
	 //srtBuffer.append(System.getProperty("intl"));
	 srtBuffer.append("/config.xml");
	 return srtBuffer.toString();
	}
}

