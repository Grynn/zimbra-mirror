package com.zimbra.qa.selenium.framework.util;

import java.io.*;
import java.net.*;
import java.util.*;

import net.sf.json.*;

import org.apache.log4j.*;
import org.dom4j.*;
import org.dom4j.io.*;

import com.ibm.staf.STAFResult;
import com.zimbra.qa.selenium.framework.core.*;
import com.zimbra.qa.selenium.framework.util.ZimbraSeleniumProperties.AppType;
import com.zimbra.qa.selenium.framework.util.staf.*;

public class CodeCoverage {
	protected static Logger logger = LogManager.getLogger(CodeCoverage.class);
	
	protected static final List<AppType> supportedAppTypes = Arrays.asList(
																	AppType.AJAX,
																	AppType.ADMIN
																	);
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
	 * Write coverage.xml to the output folder
	 */
	public void writeXml() {
		logger.info("writeXml()");
		
		if ( !isEnabled() ) {
			logger.info("writeXml(): Code Coverage reporting is disabled");
			return;
		}
		
		if ( cumulativeCoverage == null ) {
			logger.info("writeXml(): cumulativeCoverage was null");
			return;
		}

		/**
		 * Report should look like:
		 * 
	<?xml version="1.0" encoding="UTF-8"?>
	<report>
   		<stats>
     		<packages value="158"/>
     		<classes value="4740"/>
     		<methods value="38102"/>
     		<srcfiles value="3104"/>
     		<srclines value="202617"/>
   		</stats>
   		<data>
     		<all name="all classes">
       			<coverage type="class, %" value="56%  (2642/4740)"/>
       			<coverage type="method, %" value="43%  (16433/38102)"/>
       			<coverage type="block, %" value="43%  (425121/979752)"/>
       			<coverage type="line, %" value="44%  (89560/202617)"/>
			</all>
		</data>
	</report>

		 * 
		 */

		int countFiles = 0;
		int countTotalLines = 0;
		int countTotalCovered = 0;
		int percent = 0;

		Iterator<?> iterator = cumulativeCoverage.keys();
		while (iterator.hasNext()) {
			String key = (String)iterator.next();

			// Add 1 to the file count
			countFiles++;

			JSONArray coverage = cumulativeCoverage.getJSONObject(key).getJSONArray("coverage");
			for (int i = 0; i < coverage.size(); i++) {
				
				String sValue = coverage.getString(i);
				if ( !sValue.equalsIgnoreCase("null") ) {
					
					countTotalLines++;

					Integer iValue = Integer.parseInt(sValue);
					if ( iValue > 0 ) {

						// Add 1 to the line count
						countTotalCovered++;

					}
				}
				
			}

		}

		if ( countTotalLines > 0 ) {
			percent = Math.round(((float)countTotalCovered * 100)/((float)countTotalLines));
		}

		Document doc = DocumentHelper.createDocument();
		Element report = doc.addElement( "report" );
		
		Element stats = report.addElement( "stats" );
		Element srcfiles = stats.addElement( "srcfiles" );
		srcfiles.addAttribute("value", ""+ countFiles);
		Element srclines = stats.addElement( "srclines" );
		srclines.addAttribute("value", "" + countTotalLines);
		
		Element data = report.addElement( "data" );
		Element all = data.addElement( "all" );
		all.addAttribute( "name", "all classes" );
		Element coverage = all.addElement( "coverage" );
		coverage.addAttribute("type", "line, %");
		coverage.addAttribute("value", String.format("%d%%  (%d/%d)", percent, countTotalCovered, countTotalLines));
		
		XMLWriter writer = null;
		OutputFormat format = OutputFormat.createPrettyPrint();
		
		try  {

			try {

				writer = new XMLWriter(new FileWriter(CODE_COVERAGE_DIRECTORY_PATH + "/../coverage.xml", false), format);
				writer.write(doc);

			} finally {
				if ( writer != null ) {
					writer.close();
					writer = null;
				}
			}

		} catch (IOException e) {
			logger.error("Unable to write coverage.xml", e);
		}


	}


	/**
	 * Write coverage.json to the output folder
	 */
	public void writeCoverage() {
		logger.info("writeCoverage()");

		if ( !isEnabled() ) {
			logger.info("writeCoverage(): Code Coverage reporting is disabled");
			return;
		}
	
		logger.debug("<=======><=======><=== Writing Coverage to json file ===><=======><=======>");
		Date start = new Date();
		
		try {


			if (EnableSourceCodeReport) {
				logger.debug("writeCoverage(): Updating source files");
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
			createWebsiteFiles();

		} finally {

			// Update the total duration value
			durationTotalUpdate(start, new Date());

		}

		// Log how much time code coverage took
		logger.info("CodeCoverage: took an additional "+ durationTotalGet() +" seconds of processing time");
	}

	/**
	 * Update the coverage data
	 * @throws HarnessException 
	 */
	public void calculateCoverage(String method) throws HarnessException {
		logger.info("calculateCoverage()");

		if ( !isEnabled() ) {
			logger.info("calculateCoverage(): Code Coverage is disabled");
			return;
		}

		Date start = new Date();
		try {

			// Log the name of the method
			logger.info("METHOD: "+ method);


			// COVERAGE_SCRIPT returns a JSON object
			// The key is the file name
			// The values (coverage) is the coverage counts, null = not covered, 1 = covered 1 times, 2 = covered 2 times, etc.
			// Example:
			//

			if ( cumulativeCoverage == null ) {

				// First time in, just initialize the object
				logger.debug("initalizing coverage object");
				try {
					cumulativeCoverage = (JSONObject) JSONSerializer.toJSON(ClientSessionFactory.session().selenium().getEval(COVERAGE_SCRIPT));
				} catch (net.sf.json.JSONException e) {
					throw new HarnessException("JSON = ("+ ClientSessionFactory.session().selenium().getEval(COVERAGE_SCRIPT) +")", e);
				}
				
				// Log coverage statistics
				traceCoverage(null, cumulativeCoverage);
				
				return;
				
			}
			

			// Second time in, update the cumulative data

			JSONObject jsonCoverage = null;
			try {
				
				// Get the latest coverage
				logger.debug("getting updates to coverage object");
				jsonCoverage = (JSONObject) JSONSerializer.toJSON(ClientSessionFactory.session().selenium().getEval(COVERAGE_SCRIPT));

			} catch (JSONException e) {
				
				logger.error("Unable to calculate code coverage.  Disabling code coverage", e);
				logger.error(ClientSessionFactory.session().selenium().getEval(COVERAGE_SCRIPT));
				isDisabled = true;
				throw e;

			}
			
			// Log coverage statistics
			traceCoverage(cumulativeCoverage, jsonCoverage);

			Iterator<?> iterator = jsonCoverage.keys();
			while (iterator.hasNext()) {
				String key = (String)iterator.next();
				JSONArray nCoverage = jsonCoverage.getJSONObject(key).getJSONArray("coverage");

				logger.debug(key +": "+ nCoverage);
				
				if ( !cumulativeCoverage.containsKey(key) ) {
					
					// New filename, simply add the data
					logger.debug("add new filename: "+ key);
					cumulativeCoverage.put(key, jsonCoverage.getJSONObject(key));

				} else {

					// Sum the old data with the new updates
					logger.debug("updating filename: "+ key);
					cumulativeCoverage.getJSONObject(key).put("coverage", updateCoverage(cumulativeCoverage.getJSONObject(key).getJSONArray("coverage"), nCoverage));
					
				}

			}
		} finally {
			durationTotalUpdate(start, new Date());
		}
	}
	
	// For logging, report how many new files were touched and how many new lines were touched
	//
	private void traceCoverage(JSONObject oldJSON, JSONObject newJSON) {
		logger.info("CodeCoverage: URL="+ ClientSessionFactory.session().selenium().getLocation());
		
		
		int countFiles = 0;			// # of new files touched
		int countLines = 0;			// # of new lines touched
		int countDuplicates = 0;	// # of lines that were previously touched that were touched again
		
		try {
			
			if ( newJSON == null ) {
				// No new data
				return;
			}
			
			if ( oldJSON == null ) {
				
				// No old coverage to compare to
				// Just count up the statistics in the new object and report it

				Iterator<?> iterator = newJSON.keys();
				while (iterator.hasNext()) {
					String key = (String)iterator.next();

					// Add 1 to the file count
					countFiles++;

					JSONArray coverage = newJSON.getJSONObject(key).getJSONArray("coverage");
					for (int i = 0; i < coverage.size(); i++) {
						String sValue = coverage.getString(i);
						if ( !sValue.equalsIgnoreCase("null") ) {
							Integer iValue = Integer.parseInt(sValue);
							if ( iValue > 0 ) {

								// Add 1 to the line count
								countLines++;

							}
						}
					}

				}

			} else {

				// Old coverage object exists
				// Count up the differences between the old object and the new object

				Iterator<?> iterator = newJSON.keys();
				while (iterator.hasNext()) {
					String key = (String)iterator.next();
					
					if (oldJSON.containsKey(key)) {
						
						// Not a new file
						
						JSONArray oCoverage = oldJSON.getJSONObject(key).getJSONArray("coverage");
						JSONArray nCoverage = newJSON.getJSONObject(key).getJSONArray("coverage");

						for (int i = 0; i < nCoverage.size(); i++) {
						
							Integer oldValue = 0;
							Integer newValue = 0;
							
							if ( (i < oCoverage.size()) && (!oCoverage.getString(i).equalsIgnoreCase("null")) ) {
								oldValue = Integer.parseInt(oCoverage.getString(i));
							}
							if ( !nCoverage.getString(i).equalsIgnoreCase("null") ) {
								newValue = Integer.parseInt(nCoverage.getString(i));
							}
							
							if ( (oldValue == 0) && (newValue > 0) )
								countLines++;
							
							if ( (oldValue > 0) && (newValue > oldValue) )
								countDuplicates++;
							
						}
						
					} else {
						
						// New file was covered
						// Add 1 to the file count
						countFiles++;
						
						// For all new lines covered, add 1
						JSONArray coverage = newJSON.getJSONObject(key).getJSONArray("coverage");
						for (int i = 0; i < coverage.size(); i++) {
							if ( (!coverage.getString(i).equalsIgnoreCase("null")) && (Integer.parseInt(coverage.getString(i)) > 0)) {
								
								// If the value is not null and it is greater than 0
								// Add 1 to the line count
								countLines++;
				
							}
								
						}

					}
					
				}

			}
		} finally {
			logger.info("CodeCoverage: files("+ countFiles +") lines("+ countLines +") duplicates("+ countDuplicates +")");
		}
	}

	private JSONArray updateCoverage(JSONArray oCoverage, JSONArray nCoverage) {
		logger.debug("updateCoverage()");
		
		JSONArray array = new JSONArray();
		
		for (int i = 0; i < nCoverage.size(); i++) {
			
			Integer oldValue = null;
			Integer newValue = null;
			if ( (i < oCoverage.size()) && (!oCoverage.getString(i).equalsIgnoreCase("null")) ) {
				oldValue = Integer.parseInt(oCoverage.getString(i));
			}
			if ( !nCoverage.getString(i).equalsIgnoreCase("null") ) {
				newValue = Integer.parseInt(nCoverage.getString(i));
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
		
		return (array);
	}

	private void updateSourceFiles() {
		logger.debug("updateSourceFiles()");
		
		if ( cumulativeCoverage == null ) {
			logger.warn("updateSourceFiles(): cumulativeCoverage was null");
			return;
		}
		
		Iterator<?> iterator = cumulativeCoverage.keys();
		while (iterator.hasNext()) {
			String filename = (String)iterator.next();
			cumulativeCoverage.getJSONObject(filename).put("source", getSourceFileContentAsJSONArray(filename));
			logger.debug("updateSourceFiles(): Added source for "+ filename);
		}
	}
	
	private JSONArray getSourceFileContentAsJSONArray(String jsFilename) {
		logger.debug("getSourceFileContentAsJSONArray("+ jsFilename +")");

		JSONArray jsonSourceArray = new JSONArray();

		try {
			
			URL url = new URL("http://" + ZimbraSeleniumProperties.getStringProperty("server.host","qa60.lab.zimbra.com") +"/zimbra/"+ jsFilename);
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
		
		
	private void createWebsiteFiles() {
		logger.debug("createWebsiteFiles()");
		

		for (String filename : CodeCoverageWebSourceFiles.filenames) {

			File destination = new File(CODE_COVERAGE_DIRECTORY_PATH, filename);
			if ( destination.exists() ) {
				logger.debug("The destination file already exists.  Assume it was written previously.");
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
	private static final String WebappsZimbraAdmin = "/opt/zimbra/jetty/webapps/zimbraAdmin";
	private String WebappsOriginal = null;
	private String WebappsInstrumented = null;
	
	/**
	 * Check if jscoverage is available on the server
	 * 
	 */
	public void instrumentServerCheck() throws HarnessException {
		logger.info("instrumentServerCheck()");

		if ( !isEnabled() ) {
			logger.info("instrumentServerCheck(): Code Coverage is disabled");
			return;

		}
		
		StafServiceFS staf = new StafServiceFS();
		staf.execute("QUERY ENTRY "+ Tool);
		if ( staf.getSTAFResult().rc == STAFResult.DoesNotExist ) {
			isDisabled = true;
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
		logger.info("instrumentServer()");

		if ( !isEnabled() ) {
			logger.info("instrumentServer(): Code Coverage is disabled");
			return;
		}
			
		Date start = new Date();

		try {
			
			if ( !InstrumentServer ) {
				logger.info("instrumentServer(): InstrumentServer=false ... skipping ");
				return;
			}

			
			if ( ZimbraSeleniumProperties.getAppType().equals(AppType.AJAX) ) {
				instrumentServer(WebappsZimbra);
			} else if ( ZimbraSeleniumProperties.getAppType().equals(AppType.ADMIN) ) {
				instrumentServer(WebappsZimbraAdmin);
			}
		
		} finally {
			
			// Update the total duration value
			durationTotalUpdate(start, new Date());
			
		}
	
	}
	
	private void instrumentServer(String appfolder) throws HarnessException {
		
		// Check that JScoverage is installed correctly
		instrumentServerCheck();

		WebappsOriginal		= appfolder + ZimbraSeleniumProperties.getUniqueString();
		WebappsInstrumented	= "/opt/zimbra/jetty/webapps/instrumented" + ZimbraSeleniumProperties.getUniqueString();

		try {
			StafServicePROCESS staf = new StafServicePROCESS();
			
			// Stop the server
			staf.execute("zmmailboxdctl stop");
			
			// Instrument the code
			// Instrumentation could take some time, so increase the timeout
			staf.setTimeout(120000);
			staf.execute(Tool +" --no-instrument=help/ "+ appfolder +" "+ WebappsInstrumented);
			staf.resetTimeout();
			
			// Move the zimbra folder out of the way
			staf.execute("mv "+ appfolder +" "+ WebappsOriginal);
			
			// Move the instrumented code into place
			staf.execute("mv "+ WebappsInstrumented +" "+ appfolder);
			
			// Start the server
			staf.execute("zmmailboxdctl start");
			
			// Log the current status
			staf.execute("zmcontrol status");
			
		} catch (HarnessException e) {
			logger.error("Unable to instrument code.  Disabling code coverage.", e);
		}

	}
	
	/**
	 * Undo the instrumented code
	 * <p>
	 * STAF must be installed on the client and server.  Code will be instrumented and the server restarted.
	 */
	public void instrumentServerUndo() throws HarnessException {
		logger.info("instrumentServerUndo()");

		if ( !isEnabled() ) {
			logger.info("instrumentServerUndo(): Code Coverage is disabled");
			return;
		}
			
		Date start = new Date();
		
		try {

			if ( !InstrumentServer ) {
				logger.info("instrumentServerUndo(): InstrumentServer=false ... skipping ");
				return;
			}

			if ( ZimbraSeleniumProperties.getAppType().equals(AppType.AJAX) ) {
				instrumentServerUndo(WebappsZimbra);
			} else if ( ZimbraSeleniumProperties.getAppType().equals(AppType.ADMIN) ) {
				instrumentServerUndo(WebappsZimbraAdmin);
			}

		} finally {

			// Update the total duration value
			durationTotalUpdate(start, new Date());

		}
		
	}

	private void instrumentServerUndo(String appfolder) throws HarnessException {

		WebappsInstrumented	= "/opt/zimbra/jetty/webapps/instrumented" + ZimbraSeleniumProperties.getUniqueString();

		try {

			StafServicePROCESS staf = new StafServicePROCESS();
			staf.execute("zmmailboxdctl stop");
			staf.execute("rm -rf "+ appfolder); // Delete the instrumented code
			staf.execute("mv "+ WebappsOriginal +" "+ appfolder);
			staf.execute("zmmailboxdctl start");
			staf.execute("zmcontrol status");

		} catch (HarnessException e) {
			logger.error("Unable to instrument code (undo).  Disabling code coverage.", e);
		} finally {
			WebappsOriginal = null;
			WebappsInstrumented = null;
		}

	}

	// Time data (in seconds)
	private long durationTotal = 0;
	protected long durationTotalGet() {
		return (durationTotal);
	}
	protected void durationTotalUpdate(Date start, Date finish) {
		if ( start.after(finish) || start.equals(finish) ) {
			logger.error("updateTotalDuration: start wasn't before finish");
			return;
		}
		durationTotal += ((finish.getTime()/1000) - (start.getTime()/1000));
	}

	protected String Tool = "/usr/local/bin/jscoverage";
	protected boolean EnableSourceCodeReport = false;
	protected boolean InstrumentServer = true;
	
	/**
	 * Return a map of URL query parameters, required to enable code coverage from the Zimbra ajax app
	 * @return
	 */
	public Map<String, String> getQueryMap() {
		Map<String, String> map = new HashMap<String, String>();
		
		// Use the app property, if specified
		// i.e. "coverage.query.AJAX"
		// But, if not specified, default to the non-specific property
		// i.e. "coverage.query"
		//
		String property = ZimbraSeleniumProperties.getStringProperty("coverage.query", "");
		String appPoperty = ZimbraSeleniumProperties.getStringProperty(
				"coverage.query."+ ZimbraSeleniumProperties.getAppType(), null );
		if ( appPoperty != null ) {
			property = appPoperty; // Override the default
		}
		
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

	private volatile static CodeCoverage instance = null;

	private CodeCoverage() {
		logger.info("new "+ CodeCoverage.class.getCanonicalName());
		
		if ( !supportedAppTypes.contains(ZimbraSeleniumProperties.getAppType())) {
			logger.info("CodeCoverage(): code coverage does not support type "+ ZimbraSeleniumProperties.getAppType() +".  Disabling.");
			isDisabled = true;
			return;
		}
		
		// Read the Code Coverage JS function into a string
		StringBuffer sb = new StringBuffer();
		BufferedReader reader = null;
		try {
			try {
				
				InputStream stream = this.getClass().getResourceAsStream("/coverageScript.js");
				if ( stream == null ) {
					stream = this.getClass().getResourceAsStream("/com/zimbra/qa/selenium/framework/util/coverage/coverageScript.js");
				}
				if ( stream == null ) {
					logger.error("CodeCoverage(): unable to find resource: /coverageScript.js");
					isDisabled = true;
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
			isDisabled = true;
			return;
		}

		
		COVERAGE_SCRIPT = sb.toString();
		

		// Get the settings form config.properties
		//
		Tool = ZimbraSeleniumProperties.getStringProperty("coverage.tool", "/usr/local/bin/jscoverage");
		EnableSourceCodeReport = ZimbraSeleniumProperties.getStringProperty("coverage.reportsource", "false").equalsIgnoreCase("true");
		String timeout = ZimbraSeleniumProperties.getStringProperty("coverage.maxpageload.msec", "10000");
		ZimbraSeleniumProperties.setStringProperty("selenium.maxpageload.msec", timeout);
		InstrumentServer = ZimbraSeleniumProperties.getStringProperty("coverage.instrument", "true").equalsIgnoreCase("true");


	}
	
	// Sometimes, there may be an exception that should disable
	// code coverage metrics for the remainder of the run.
	// In those cases, isDisabled will be flipped to true
	private boolean isDisabled = false;
		
	protected boolean isEnabled() {
		String v = ZimbraSeleniumProperties.getStringProperty("coverage.enabled", "false");
		logger.info("coverage.enabled="+v);
		if ( isDisabled ) {
			logger.info("isDiabled is true, therefore Code Coverage is disabled");
			return (false);
		}
		return (v.equalsIgnoreCase("true"));
	}

	/**
	 * Get the CodeCoverage object
	 * @return
	 */
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
						if ( stream == null ) {
							stream = this.getClass().getResourceAsStream("/com/zimbra/qa/selenium/framework/util/coverage/" + filename);
						}
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
			logger.debug("new "+ CodeCoverage.class.getCanonicalName());
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
