package com.zimbra.qa.selenium.framework.util;

import java.io.*;
import java.net.*;
import java.util.*;

import net.sf.json.*;

import org.apache.log4j.*;

import com.ibm.staf.STAFResult;
import com.zimbra.qa.selenium.framework.core.ClientSessionFactory;
import com.zimbra.qa.selenium.framework.util.staf.*;

public class CodeCoverage {
	protected static Logger logger = LogManager.getLogger(CodeCoverage.class);
	
	// For debugging ...
	// Don't instrument server
	private static boolean debugging = false;
	
	
	
	/**
	 * The cumulative code coverage object
	 * A map with the application JS filename as the key
	 * "source" points to the JS file source code (optional)
	 * "coverage" points to an array of the source code usage
	 *  with the "array index" as the source code line number
	 *  with "null" as "not applicable" (i.e comments)
	 *  with "0" as "not touched"
	 *  with ">0" as "the number of touches on that line"
	 */
	protected JSONObject cumulativeCoverage = null;
	
	/**
	 * Set the output folder to write the coverage report
	 * @param path
	 */
	public void setOutputFolder(String path) {
		logger.info("setOutputFolder("+ path +")");
		CODE_COVERAGE_DIRECTORY_PATH = path;
	}
	
	/**
	 * Write coverage.json to the output folder
	 */
	public void writeCoverage() {

		if ( !Enabled ) {
			logger.info("writeCoverage(): Code Coverage reporting is disabled");
			return;
		}
	
		logger.info("writeCoverage()");
		logger.info("<=======><=======><=== Writing Coverage to json file ===><=======><=======>");
		Date start = new Date();
		
		if (EnableSourceCodeReport) {
			logger.info("writeCoverage(): Updating source files");
			updateSourceFiles();
		}
		
		
		// Write the JSON object to a file
		BufferedWriter out = null;
		
		try {
			
			try {
				
				out = new BufferedWriter(new FileWriter(new File(CODE_COVERAGE_DIRECTORY_PATH, CODE_COVERAGE_DIRECTORY_FILE)));
				
				if ( cumulativeCoverage != null ) {
					cumulativeCoverage.write(out);
				}
				
			} finally {
				if (out != null) {
					out.close();
					out = null;
				}
			}
			
		} catch (IOException e) {
			logger.error("Unable to write coverage report", e);
		}
		
		// Write the other html, css, etc. files to the folder
		updateOutputFolder();
		
		updateTotalDuration(start, new Date());
		
		// Log how much time code coverage took
		logger.info("CodeCoverage: took "+ getTotalDuration() +" seconds");
	}

	/**
	 * Update the coverage data
	 */
	public void calculateCoverage() {
		logger.info("calculateCoverage()");

		if ( !Enabled ) {
			logger.info("calculateCoverage(): Code Coverage reporting is disabled");
			return;
		}

		Date start = new Date();
		try {

			// COVERAGE_SCRIPT returns a JSON object
			// The key is the file name
			// The values (coverage) is the coverage counts, null = not covered, 1 = covered 1 times, 2 = covered 2 times, etc.
			// Example:
			//

			if ( cumulativeCoverage == null ) {

				// First time in, just initialize the object
				logger.info("initalizing coverage object");
				cumulativeCoverage = (JSONObject) JSONSerializer.toJSON(ClientSessionFactory.session().selenium().getEval(COVERAGE_SCRIPT));
				return;
				
			}
			

			// Second time in, update the cumulative data

			JSONObject jsonCoverage = null;
			try {
				
				// Get the latest coverage
				logger.info("getting updates to coverage object");
				jsonCoverage = (JSONObject) JSONSerializer.toJSON(ClientSessionFactory.session().selenium().getEval(COVERAGE_SCRIPT));

			} catch (JSONException e) {
				
				logger.error("Unable to calculate code coverage.  Disabling code coverage", e);
				logger.error(ClientSessionFactory.session().selenium().getEval(COVERAGE_SCRIPT));
				Enabled = false;
				throw e;

			}

			Iterator<?> iterator = jsonCoverage.keys();
			while (iterator.hasNext()) {
				String key = (String)iterator.next();
				JSONArray nCoverage = jsonCoverage.getJSONObject(key).getJSONArray("coverage");

				logger.debug(key +": "+ nCoverage);
				
				if ( !cumulativeCoverage.containsKey(key) ) {
					
					// New filename, simply add the data
					logger.info("add new filename: "+ key);
					cumulativeCoverage.put(key, jsonCoverage.getJSONObject(key));

				} else {

					// Sum the old data with the new updates
					logger.info("updating filename: "+ key);
					cumulativeCoverage.getJSONObject(key).put("coverage", updateCoverage(cumulativeCoverage.getJSONObject(key).getJSONArray("coverage"), nCoverage));
					
				}

			}
		} finally {
			updateTotalDuration(start, new Date());
		}
	}
	
	private JSONArray updateCoverage(JSONArray oCoverage, JSONArray nCoverage) {
		logger.debug("updateCoverage()");
		
		Integer additionalCoverage = 0;  // For debugging, keep track of the new lines covered

		JSONArray array = new JSONArray();
		
		for (int i = 0; i < nCoverage.size(); i++) {
			
			Integer oldValue = null;
			Integer newValue = null;
			if ( (i < oCoverage.size()) && (!oCoverage.getString(i).equalsIgnoreCase("null")) ) {
				oldValue = Integer.parseInt(oCoverage.getString(i));
			}
			if ( !nCoverage.getString(i).equalsIgnoreCase("null") ) {
				newValue = Integer.parseInt(nCoverage.getString(i));
				additionalCoverage += newValue;
			}
			
			if (oldValue == null && newValue == null) {
				array.add("null");
				continue;
			}
			if (newValue == null) {
				array.add(oldValue);
				continue;
			}
			if (oldValue == null) {
				array.add(newValue);
				continue;
			}
			
			array.add(oldValue + newValue);

		}

		logger.debug("Additional lines covered:" + additionalCoverage);
		
		return (array);
	}

	private void updateSourceFiles() {
		logger.debug("updateSourceFiles()");
		
		Iterator<?> iterator = cumulativeCoverage.keys();
		while (iterator.hasNext()) {
			String key = (String)iterator.next();
			cumulativeCoverage.getJSONObject(key).put("source", updateSourceFileContent(key));
			logger.info("Added source for "+ key);
		}
	}
	
	private JSONArray updateSourceFileContent(String filename) {
		logger.debug("updateSourceFileContent("+ filename +")");

		JSONArray jsonSourceArray = new JSONArray();

		try {
			
			URL url = new URL("http://" + ZimbraSeleniumProperties.getStringProperty("server.host","qa60.lab.zimbra.com") +"/zimbra/"+ filename);
			URLConnection uc = url.openConnection();
			BufferedReader reader = null;
			
			try {
				
				reader = new BufferedReader(new InputStreamReader(uc.getInputStream()));
				
				String line;
				while ((line = reader.readLine()) != null) {
					jsonSourceArray.add(line);
				}

	
			} finally {
				if ( reader != null ) {
					reader.close();
					reader = null;
				}
			}
			
		} catch (IOException e) {
			logger.error("Unable to update coverage source", e);
		}

		return (jsonSourceArray);

	}
		
		
	private void updateOutputFolder() {
		logger.debug("updateOutputFolder()");
		

		for (String filename : CodeCoverageWebSourceFiles.filenames) {

			File destination = new File(CODE_COVERAGE_DIRECTORY_PATH, filename);
			if ( destination.exists() ) {
				logger.info("The destination file already exists.  Assume it was written previously.");
				continue;
			}
			
			try {

				// Create the directory and file
				destination.getParentFile().mkdirs();
				destination.createNewFile();

				OutputStreamWriter writer = null;
				try {
					writer = new OutputStreamWriter(new FileOutputStream(destination, false));
					writer.write(CodeCoverageWebSourceFiles.getInstance().getContents(filename));
				} finally {
					if ( writer != null ){
						writer.close();
						writer = null;
					}
				}

			} catch (FileNotFoundException e) {
				logger.error("Unable to write "+ destination.getAbsolutePath(), e);
			} catch (IOException e) {
				logger.error("Unable to write "+ destination.getAbsolutePath(), e);
			} catch (HarnessException e) {
				logger.error("Unable to write "+ destination.getAbsolutePath(), e);
			}

		}
	}
	
    private String CODE_COVERAGE_DIRECTORY_PATH = "CODECOVERAGE";
    private String CODE_COVERAGE_DIRECTORY_FILE = "jscoverage.json";

	
    private String COVERAGE_SCRIPT = "";

	private static final String WebappsZimbra = "/opt/zimbra/jetty/webapps/zimbra";
	private String WebappsZimbraOriginal = null;
	private String WebappsZimbraInstrumented = null;
	
	/**
	 * Check if jscoverage is available on the server
	 * 
	 */
	public void instrumentServerCheck() throws HarnessException {
		
		if ( !Enabled ) {
			logger.info("instrumentServerCheck(): Code Coverage reporting is disabled");
			return;

		}
		
		StafServiceFS staf = new StafServiceFS();
		staf.execute("QUERY ENTRY "+ Tool);
		if ( staf.getSTAFResult().rc == STAFResult.DoesNotExist ) {
			Enabled = false;
			throw new HarnessException(Tool +" does not exist!");
		}	


	}
	
	/**
	 * Instrument the code on the Zimbra server
	 * <p>
	 * STAF must be installed on the client and server.  Code will be instrumented and the server restarted.
	 * @throws HarnessException 
	 */
	public void instrumentServer() throws HarnessException {
		
		if ( !Enabled ) {
			logger.info("instrumentServer(): Code Coverage reporting is disabled");
			return;
		}
			
		logger.info("instrumentServer()");
		Date start = new Date();

		if ( debugging ) {
			logger.info("instrumentServer(): debugging ... skipping ");
			return;
		}
		
		// Check that JScoverage is installed correctly
		instrumentServerCheck();
		
		WebappsZimbraOriginal		= "/opt/zimbra/jetty/webapps/zimbra" + ZimbraSeleniumProperties.getUniqueString();
		WebappsZimbraInstrumented	= "/opt/zimbra/jetty/webapps/instrumented" + ZimbraSeleniumProperties.getUniqueString();
		
		try {
			StafServicePROCESS staf = new StafServicePROCESS();
			staf.execute("zmmailboxdctl stop");
			staf.execute(Tool +" --no-instrument=help/ "+ WebappsZimbra +" "+ WebappsZimbraInstrumented);
			staf.execute("mv "+ WebappsZimbra +" "+ WebappsZimbraOriginal);
			staf.execute("mv "+ WebappsZimbraInstrumented +" "+ WebappsZimbra);
			staf.execute("zmmailboxdctl start");
			staf.execute("zmcontrol status");
		} catch (HarnessException e) {
			logger.error("Unable to instrument code.  Disabling code coverage.", e);
		}

		updateTotalDuration(start, new Date());
	}
	
	/**
	 * Undo the instrumented code
	 * <p>
	 * STAF must be installed on the client and server.  Code will be instrumented and the server restarted.
	 */
	public void instrumentServerUndo() throws HarnessException {
		
		if ( !Enabled ) {
			logger.info("instrumentServerUndo(): Code Coverage reporting is disabled");
			return;
		}
			
		logger.info("instrumentServerUndo()");
		Date start = new Date();
		
		if ( debugging ) {
			logger.info("instrumentServer(): debugging ... skipping ");
			return;
		}
		
		WebappsZimbraInstrumented	= "/opt/zimbra/jetty/webapps/instrumented" + ZimbraSeleniumProperties.getUniqueString();

		try {
			
			StafServicePROCESS staf = new StafServicePROCESS();
			staf.execute("zmmailboxdctl stop");
			staf.execute("rm -rf "+ WebappsZimbra); // Delete the instrumented code
			staf.execute("mv "+ WebappsZimbraOriginal +" "+ WebappsZimbra);
			staf.execute("zmmailboxdctl start");
			staf.execute("zmcontrol status");
			
		} catch (HarnessException e) {
			logger.error("Unable to instrument code.  Disabling code coverage.", e);
		} finally {
			WebappsZimbraOriginal = null;
			WebappsZimbraInstrumented = null;
		}

		updateTotalDuration(start, new Date());
	}

	// Time data (in seconds)
	private long totalDuration = 0;
	protected long getTotalDuration() {
		return (totalDuration);
	}
	protected void updateTotalDuration(Date start, Date finish) {
		if ( start.after(finish) || start.equals(finish) ) {
			logger.error("updateTotalDuration: start wasn't before finish");
			return;
		}
		totalDuration += ((finish.getTime()/1000) - (start.getTime()/1000));
	}

	protected boolean Enabled = false;
	protected String Tool = "/usr/local/bin/jscoverage";
	protected boolean EnableSourceCodeReport = false;
	
	/**
	 * Return a map of URL query parameters, required to enable code coverage from the Zimbra ajax app
	 * @return
	 */
	public Map<String, String> getQueryMap() {
		Map<String, String> map = new HashMap<String, String>();
		
		String property = ZimbraSeleniumProperties.getStringProperty("coverage.query", "");
		
		for (String p : property.split("&")) {
			if ( p.contains("=") ) {
				map.put(p.split("=")[0], p.split("=")[1]);
			} else {
				// No value, just use p as the key and null as the value
				map.put(p, null);
			}
		}
		
		return (map);
	}

	// Singleton methods

	private volatile static CodeCoverage instance;

	private CodeCoverage() {
		logger.info("new "+ CodeCoverage.class.getCanonicalName());
		
		Enabled = ZimbraSeleniumProperties.getStringProperty("coverage.enabled", "false").equalsIgnoreCase("true");
		
		if ( !Enabled )
			return;
		

		// Read the Code Coverage JS function into a string
		StringBuffer sb = new StringBuffer();
		BufferedReader reader = null;
		try {
			try {
				
				InputStream stream = this.getClass().getResourceAsStream("/coverageScript.js");
				if ( stream == null ) {
					logger.error("unable to find resource: /coverageScript.js");
					Enabled = false;
					return;
				}
				
				// Convert stream to String
				byte[] b = new byte[1024];
				for (int n; (n = stream.read(b)) != -1;) {
					sb.append(new String(b, 0, n));
				}
				
			} finally {
				if ( reader != null ) {
					reader.close();
					reader = null;
				}
			}
		} catch (IOException e) {
			logger.error("unable to read resource: /coverageScript.js", e);
			Enabled = false;
			return;
		}

		
		COVERAGE_SCRIPT = sb.toString();
		

		// Get the settings form config.properties
		//
		Tool = ZimbraSeleniumProperties.getStringProperty("coverage.tool", "/usr/local/bin/jscoverage");
		EnableSourceCodeReport = ZimbraSeleniumProperties.getStringProperty("coverage.reportsource", "false").equalsIgnoreCase("true");
		String timeout = ZimbraSeleniumProperties.getStringProperty("coverage.maxpageload.msec", "10000");
		ZimbraSeleniumProperties.setStringProperty("selenium.maxpageload.msec", timeout);


	}

	public static CodeCoverage getInstance() {
		if(instance==null) {
			synchronized(CodeCoverage.class){
				if(instance == null) {
					instance = new CodeCoverage();
				}
			}
		}
		return instance;
	}


	// A class that writes the coverage website files
	public static class CodeCoverageWebSourceFiles {
		
		public static final List<String> filenames =
	        Arrays.asList(
	        		"jscoverage-highlight.css",
	        		"jscoverage-ie.css",
	        		"jscoverage-throbber.gif",
	        		"jscoverage.css",
	        		"jscoverage.html",
	        		"index.html",
	        		"jscoverage.js");
		
		private Map<String, String> filecontents;
		

		public String getContents(String filename) throws HarnessException {
			if ( !filecontents.containsKey(filename) )
				throw new HarnessException("Invalid filename: "+ filename);
			
			if ( filecontents.get(filename) == null ) {
				
				// Contents never read.  Read them now.
				
				StringBuffer sb = new StringBuffer();
				BufferedReader reader = null;
				try {
					try {
						
						InputStream stream = this.getClass().getResourceAsStream("/" +filename);
						if ( stream == null )
							throw new HarnessException("unable to find resource: "+ filename);
						
						// Convert stream to String
						byte[] b = new byte[1024];
						for (int n; (n = stream.read(b)) != -1;) {
							sb.append(new String(b, 0, n));
						}
						
					} finally {
						if ( reader != null ) {
							reader.close();
							reader = null;
						}
					}
				} catch (IOException e) {
					throw new HarnessException(e);
				}

				// Save the contents
				filecontents.put(filename, sb.toString());
				
			}
			return (filecontents.get(filename));
			
		}
		

		private volatile static CodeCoverageWebSourceFiles instance;

		private CodeCoverageWebSourceFiles() {
			logger.info("new "+ CodeCoverage.class.getCanonicalName());
			filecontents = new HashMap<String, String>();
			for (String name : filenames) {
				filecontents.put(name, null);
			}
		}

		public static CodeCoverageWebSourceFiles getInstance() {
			if(instance==null) {
				synchronized(CodeCoverageWebSourceFiles.class){
					if(instance == null) {
						instance = new CodeCoverageWebSourceFiles();
					}
				}
			}
			return instance;
		}

	}


 }
