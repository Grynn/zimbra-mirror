package com.zimbra.qa.selenium.framework.util;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.FileChannel;
import java.util.*;

import net.sf.json.*;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.zimbra.qa.selenium.framework.core.ClientSessionFactory;

public class CodeCoverage {
	protected static Logger logger = LogManager.getLogger(CodeCoverage.class);
	
	/**
	 * Set the output folder to write the coverage report
	 * @param path
	 */
	public void setOutputFolder(String path) {
		logger.info("Set code coverage folder: "+ path);
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
		
		logger.info("<=======><=======><=== Writing Coverage to json file ===><=======><=======>");
		
		// TODO: change from BufferedWriter to Logger
		BufferedWriter out = null;
		
		try {
			
			try {
				
				File f = new File(CODE_COVERAGE_DIRECTORY_PATH, CODE_COVERAGE_DIRECTORY_FILE);
				out = new BufferedWriter(new FileWriter(f));
				
				out.write("{");
				for (String key : FILENAME_TO_COVERAGE.keySet() ) {
					out.write(
						"\"" + key + "\"" + ":{\"coverage\":"
						+ FILENAME_TO_COVERAGE.get(key) + ",\"source\":"
						+ FILENAME_TO_SOURCE.get(key) + "}," );
	
				}
				out.write("}");
				
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
		
	}

	/**
	 * Update the coverage data
	 */
	public void calculateCoverage() {
		logger.info("Calculating code coverage ...");

		if ( !Enabled ) {
			logger.info("calculateCoverage(): Code Coverage reporting is disabled");
			return;
		}
		

		try {
			
			String coverage_string = ClientSessionFactory.session().selenium().getEval(COVERAGE_SCRIPT);
			JSONObject jsonCoverage = (JSONObject) JSONSerializer.toJSON(coverage_string);
			String individualFileInfo[] = coverage_string.split("},");
			for (int i = 0; i < individualFileInfo.length; i++) {
				String jsonElements[] = individualFileInfo[i].split(":");
				if (jsonElements[0].startsWith("{"))
					jsonElements[0] = jsonElements[0].replace("{", "");
				if (jsonElements[0].startsWith("\"")
						&& jsonElements[0].endsWith(".js\"")) {
					String jsFileName = jsonElements[0].replace("\"", "");
					parseCoverage(jsFileName, jsonCoverage);
					updateSource(jsFileName);
				}
			}

		} catch (Exception e) {
			logger.error("Unable to calculate coverage", e);
		}
		
	}

	private void parseCoverage(String file, JSONObject jsonCoverage) {
		JSONObject fileName = jsonCoverage.getJSONObject(file);
		JSONArray jsonCoverageArray = fileName.getJSONArray("coverage");
		ArrayList<Integer> coverage = new ArrayList<Integer>();
		for (int j = 0; j < jsonCoverageArray.size(); j++) {
			if (jsonCoverageArray.getString(j).equalsIgnoreCase("null")) {
				coverage.add(null);
			} else {
				coverage.add(Integer.parseInt(jsonCoverageArray.getString(j)));
			}
		}
		updateCoverage(file, coverage);
	}

	private void updateSource(String file) {
		if (FILENAME_TO_SOURCE.containsKey(file)) {
			return;
		}
		
		try {
			
			URL url = new URL("http://" + CoverageServer +"/zimbra/"+ file);
			URLConnection uc = url.openConnection();
			BufferedReader reader = null;
			
			try {
				
				JSONArray jsonSourceArray = new JSONArray();
				reader = new BufferedReader(new InputStreamReader(uc.getInputStream()));
				
				String line;
				while ((line = reader.readLine()) != null) {
					jsonSourceArray.add(line);
				}

				FILENAME_TO_SOURCE.put(file, jsonSourceArray);
	
			} finally {
				if ( reader != null ) {
					reader.close();
					reader = null;
				}
			}
			
		} catch (IOException e) {
			logger.error("Unable to update coverage source", e);
		}

	}

	private void updateCoverage(String file, ArrayList<Integer> data) {
		if (FILENAME_TO_COVERAGE.containsKey(file)) {
			ArrayList<Integer> coverage = FILENAME_TO_COVERAGE.get(file);
			int i = 0;
			for (; i < coverage.size(); i++) {
				Integer oldValue = coverage.get(i);
				Integer newValue = data.get(i);
				if (oldValue == null && newValue == null) {
					continue;
				}
				if (newValue == null) {
					continue;
				}
				if (oldValue == null) {
					oldValue = 0;
				}
				coverage.set(i, oldValue + newValue);
			}

			for (; i < data.size(); i++) {
				coverage.add(data.get(i));
			}
			FILENAME_TO_COVERAGE.put(file, coverage);
		} else {
			FILENAME_TO_COVERAGE.put(file, data);
		}
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
		logger.info("Update output folder with html files");

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
		logger.info("copy "+ source.getCanonicalPath() +" to "+ destination.getCanonicalPath());

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
    private Map<String, ArrayList<Integer>> FILENAME_TO_COVERAGE = new HashMap<String, ArrayList<Integer>>();
    private Map<String, JSONArray> FILENAME_TO_SOURCE = new HashMap<String, JSONArray>();

	
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
	
	private static class StafExecuteCommand extends StafAbstract {
		public StafExecuteCommand(String server) {
			StafServer = server;
			StafService = "PROCESS";
		}
		public boolean execute(String command) throws HarnessException {
			if ( command.trim().startsWith("zm") ) {
				// For zm commands, run as zimbra user, and prepend the full path
				StafParms = String.format("START SHELL COMMAND \"su - zimbra -c '/opt/zimbra/bin/%s'\" RETURNSTDOUT RETURNSTDERR WAIT %d", command, 90000);
			} else {
				StafParms = String.format("START SHELL COMMAND \"%s\" RETURNSTDOUT RETURNSTDERR WAIT %d", command, 90000);
			}
			return (super.execute());
		}
	}
	
	/**
	 * Instrument the code on the Zimbra server
	 * <p>
	 * STAF must be installed on the client and server.  Code will be instrumented and the server restarted.
	 */
	public void instrumentServer() {
		
		if ( !Enabled ) {
			logger.info("instrumentServer(): Code Coverage reporting is disabled");
			return;
		}
				
		WebappsZimbraOriginal		= "/opt/zimbra/jetty/webapps/zimbra" + ZimbraSeleniumProperties.getUniqueString();
		WebappsZimbraInstrumented	= "/opt/zimbra/jetty/webapps/instrumented" + ZimbraSeleniumProperties.getUniqueString();
		
		try {
			StafExecuteCommand staf = new StafExecuteCommand(ZimbraSeleniumProperties.getStringProperty("server.host"));
			staf.execute("zmmailboxdctl stop");
			staf.execute("/usr/local/bin/jscoverage --no-instrument=help/ "+ WebappsZimbra +" "+ WebappsZimbraInstrumented);
			staf.execute("mv "+ WebappsZimbra +" "+ WebappsZimbraOriginal);
			staf.execute("mv "+ WebappsZimbraInstrumented +" "+ WebappsZimbra);
			staf.execute("zmmailboxdctl start");
			staf.execute("zmcontrol status");
		} catch (HarnessException e) {
			logger.error("Unable to instrument code.  Disabling code coverage.", e);
		}

	}
	
	public void instrumentServerUndo() {
		
		if ( !Enabled ) {
			logger.info("instrumentServerUndo(): Code Coverage reporting is disabled");
			return;
		}
			
		WebappsZimbraInstrumented	= "/opt/zimbra/jetty/webapps/instrumented" + ZimbraSeleniumProperties.getUniqueString();

		try {
			StafExecuteCommand staf = new StafExecuteCommand(ZimbraSeleniumProperties.getStringProperty("server.host"));
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

	}


	protected boolean Enabled = false;
	protected String CoverageServer = null;
	
	// Singleton methods

	private volatile static CodeCoverage instance;

	private CodeCoverage() {
		Enabled = ZimbraSeleniumProperties.getStringProperty("runCodeCoverage", "no").equalsIgnoreCase("yes");
		CoverageServer = ZimbraSeleniumProperties.getStringProperty("coverageServer", "zqa-060.eng.vmware.com");
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
