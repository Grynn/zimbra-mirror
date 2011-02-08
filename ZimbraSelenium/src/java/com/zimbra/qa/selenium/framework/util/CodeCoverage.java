package com.zimbra.qa.selenium.framework.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import net.sf.json.*;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.zimbra.qa.selenium.framework.core.ClientSessionFactory;

public class CodeCoverage {
	protected static Logger logger = LogManager.getLogger(CodeCoverage.class);
	
	public void writeCoverage() {
		
		if ( !Enabled ) {
			logger.info("skipping code coverage");
			return;
		}
		
		logger.info("<=======><=======><=== Writing Coverage to json file ===><=======><=======>");
		
		// TODO: change from BufferedWriter to Logger
		BufferedWriter out = null;
		
		try {
			
			try {
				
				out = new BufferedWriter(new FileWriter(CODE_COVERAGE_DIRECTORY_PATH));
				
				// TODO: convert to StringBuilder
				String jsonString = "";
				for (String key : FILENAME_TO_COVERAGE.keySet() ) {
					jsonString = jsonString + "\"" + key + "\"" + ":{\"coverage\":"
						+ FILENAME_TO_COVERAGE.get(key) + ",\"source\":"
						+ FILENAME_TO_SOURCE.get(key) + "},";
	
				}
				out.write("{" + jsonString + "}");
				
			} finally {
				if (out != null) {
					out.close();
					out = null;
				}
			}
			
		} catch (IOException e) {
			logger.error("Unable to write coverage report", e);
		}
	}

	public void calculateCoverage() {

		if ( !Enabled ) {
			logger.info("skipping code coverage");
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

	
    private Map<String, ArrayList<Integer>> FILENAME_TO_COVERAGE = new HashMap<String, ArrayList<Integer>>();
    private Map<String, JSONArray> FILENAME_TO_SOURCE = new HashMap<String, JSONArray>();

	
    private static final String CODE_COVERAGE_DIRECTORY_PATH = "CODECOVERAGE\\jscoverage.json";
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
