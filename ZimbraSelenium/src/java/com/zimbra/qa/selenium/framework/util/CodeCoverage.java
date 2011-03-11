package com.zimbra.qa.selenium.framework.util;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.FileChannel;
import java.util.*;

import net.sf.json.*;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

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
		
		
		// TODO: change from BufferedWriter to Logger
		BufferedWriter out = null;
		
		try {
			
			try {
				
				File f = new File(CODE_COVERAGE_DIRECTORY_PATH, CODE_COVERAGE_DIRECTORY_FILE);
				out = new BufferedWriter(new FileWriter(f));
				
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
			
			URL url = new URL("http://" + CoverageServer +"/zimbra/"+ filename);
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
		
		
	private static final String CODE_COVERAGE_SOURCE_PATH = "src/CODECOVERAGE/";
	private static final List<String> reportFiles = new ArrayList<String>() {
		private static final long serialVersionUID = -6339218908274560120L;
	{
		add("jscoverage.css");
		add("jscoverage.html");
		add("jscoverage.js");
		add("jscoverage-highlight.css");
		add("jscoverage-ie.css");
		add("jscoverage-throbber.gif");
	}};

	private void updateOutputFolder() {
		logger.debug("updateOutputFolder()");

		for (String filename : reportFiles) {
			File destination = new File(CODE_COVERAGE_DIRECTORY_PATH, filename);
			if ( destination.exists() ) {
				logger.info("The destination file already exists.  Assume it was written previously.");
				continue;
			}
			File source = new File(CODE_COVERAGE_SOURCE_PATH, filename);
			if ( !source.exists() ) {
				logger.error("Unable to find report file: "+ source.getAbsolutePath());
				continue;
			}
			try {
				copy(source, destination);
			} catch (IOException e) {
				logger.error("Unable to copy file from "+ source.getAbsolutePath() +" to "+ destination.getAbsolutePath(), e);
			}
		}
	}
	
	private static void copy(File source, File destination) throws IOException {
		logger.debug("copy "+ source.getCanonicalPath() +" to "+ destination.getCanonicalPath());

		if ( !destination.exists() ) {
			destination.createNewFile();
		}
		
		FileChannel sourceChannel = null;
		FileChannel destinationChannel = null;
		try {
			sourceChannel = (new FileInputStream(source)).getChannel();
			destinationChannel = (new FileOutputStream(destination)).getChannel();
			destinationChannel.transferFrom(sourceChannel, 0 , sourceChannel.size());
		} finally {
			if ( sourceChannel != null ) {
				sourceChannel.close();
				sourceChannel = null;
			}
			if ( destinationChannel != null ) {
				destinationChannel.close();
				destinationChannel = null;
			}
		}
	}

	
    private String CODE_COVERAGE_DIRECTORY_PATH = "CODECOVERAGE";
    private String CODE_COVERAGE_DIRECTORY_FILE = "jscoverage.json";

	
    private static final String COVERAGE_SCRIPT = 
    	      "if (! window.jscoverage_report) {\n"
			+ "  window.jscoverage_report = function jscoverage_report(dir) {\n"
			+ "    if(window._$jscoverage == undefined) return \"\";\n"
			+ "    var pad = function (s) {   \n"
			+ "          return '0000'.substr(s.length) + s; \n"
			+ "   };\n"
			+ "  var quote = function (s) {   \n"
			+ "   return '\"' + s.replace(/[\\u0000-\\u001f\"\\\\\\u007f-\\uffff]/g, function (c) {  \n"
			+ "      switch (c) {\n"
			+ "        case '\\b':\n"
			+ "          return '\\\\b';\n"
			+ "        case '\\f':    \n"
			+ "         return '\\\\f';\n"
			+ "        case '\\n': \n"
			+ "         return '\\\\n'; \n"
			+ "       case '\\r':\n"
			+ "          return '\\\\r'; \n"
			+ "       case '\\t':\n"
			+ "          return '\\\\t'; \n"
			+ "       case '\"':     \n"
			+ "         return '\\\\\"'; \n"
			+ "       case '\\\\':\n"
			+ "          return '\\\\\\\\';\n"
			+ "       default:   \n"
			+ "              return '\\\\u' + pad(c.charCodeAt(0).toString(16));\n"
			+ "        }\n"
			+ "      }) + '\"';\n"
			+ "    };\n"
			+ "\n"
			+ "    var json = [];\n"
			+ "    for (var file in window._$jscoverage) { \n"
			+ "     var coverage = window._$jscoverage[file];\n"
			+ "      var array = []; \n"
			+ "     var length = coverage.length;\n"
			+ "      for (var line = 0; line < length; line++) {\n"
			+ "        var value = coverage[line];       \n"
			+ "    if (value === undefined || value === null) {\n"
			+ "          value = 'null';    \n"
			+ "    }else{\n"
			+ "          coverage[line] = 0; //stops double counting\n"
			+ "        }\n"
			+ "        array.push(value);}\n"
			+ "      json.push(quote(file) + ':{\"coverage\":[' + array.join(',') + ']}');    } \n"
			+ "   json = '{' + json.join(',') + '}';\n"
			+ "    return json;\n"
			+ "  };\n" 
			+ "}; \n" 
			+ "window.jscoverage_report()\n";


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
			staf.execute("mv "+ WebappsZimbra +" "+ WebappsZimbraInstrumented);
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
	protected String CoverageServer = null;
	
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
		
		if ( Enabled ) {
			Tool = ZimbraSeleniumProperties.getStringProperty("coverage.tool", "/usr/local/bin/jscoverage");
			CoverageServer = ZimbraSeleniumProperties.getStringProperty("coverage.server", "zqa-060.eng.vmware.com");
			EnableSourceCodeReport = ZimbraSeleniumProperties.getStringProperty("coverage.reportsource", "false").equalsIgnoreCase("true");
			String timeout = ZimbraSeleniumProperties.getStringProperty("coverage.maxpageload.msec", "10000");
			ZimbraSeleniumProperties.setStringProperty("selenium.maxpageload.msec", timeout);
		}
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


 }
