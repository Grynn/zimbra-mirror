package com.zimbra.qa.selenium.framework.ui;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.SleepUtil;
import com.zimbra.qa.selenium.framework.util.ZimbraSeleniumProperties;


/**
 * Measure application load/rendering times
 * @author Matt Rhoades
 *
 */
public class PerfMetrics extends AbsSeleniumObject {
	protected static Logger logger = LogManager.getLogger(PerfMetrics.class);

	protected static Logger traceLog = LogManager.getLogger(PerfMetrics.class.getName() + ".trace");

	/**
	 * A list of perf metrics that can be measured.
	 * See http://bugzilla.zimbra.com/show_bug.cgi?id=61972
	 * See http://bugzilla.zimbra.com/attachment.cgi?id=33941
	 * @author Matt Rhoades
	 *
	 */
	public static class PerfKey {
		
		public static final PerfKey ZmMailApp 			= new PerfKey("ZmMailApp", "ZmMailApp_launched", "ZmMailApp_loaded");
		public static final PerfKey ZmMailItem 			= new PerfKey("ZmMailItem", "ZmMailItem_loading", "ZmMailItem_loaded");
		public static final PerfKey ZmContactsApp 		= new PerfKey("ZmContactsApp", "ZmContactsApp_launched", "ZmContactsApp_loaded");
		public static final PerfKey ZmContactsItem 		= new PerfKey("ZmContactsItem", "ZmContactsItem_loading", "ZmContactsItem_loaded");
		public static final PerfKey ZmCalendarApp 		= new PerfKey("ZmCalendarApp", "ZmCalendarApp_launched", "ZmCalendarApp_loaded");
		public static final PerfKey ZmCalWorkWeekView 	= new PerfKey("ZmCalWorkWeekView", "ZmCalWorkWeekView_loading", "ZmCalWorkWeekView_loaded");
		public static final PerfKey ZmCalItemView 		= new PerfKey("ZmCalItemView", "ZmCalItemView_loading", "ZmCalItemView_loaded");
		public static final PerfKey ZmCalViewItem 		= new PerfKey("ZmCalViewItem", "ZmCalViewItem_loading", "ZmCalViewItem_loaded");
		public static final PerfKey ZmTasksApp 			= new PerfKey("ZmTasksApp", "ZmTasksApp_launched", "ZmTasksApp_loaded");
		public static final PerfKey ZmTaskItem 			= new PerfKey("ZmTaskItem", "ZmTaskItem_loading", "ZmTaskItem_loaded");
		public static final PerfKey ZmBriefcaseApp 		= new PerfKey("ZmBriefcaseApp", "ZmBriefcaseApp_launched", "ZmBriefcaseApp_loaded");
		public static final PerfKey ZmBriefcaseItem 	= new PerfKey("ZmBriefcaseItem", "ZmBriefcaseItem_loading", "ZmBriefcaseItem_loaded");
		
		protected String Key;
		protected String LaunchKey;
		protected String FinishKey;
		
		public PerfKey(String key, String launch, String finish) {
			Key = key;
			LaunchKey = launch;
			FinishKey = finish; 
		}
		
		public String toString() {
			return (Key);
		}
		
	}
		
	/**
	 * A PerfToken can be used to track one instance of measuring the browser performance
	 * @author Matt Rhoades
	 *
	 */
	public static class PerfToken {
		
		private static int c = 1;
		
		private int t;
		
		public PerfToken() {
			t = c++;
		}
		
		public String toString() {
			return (""+ t);
		}
	}
	
	/**
	 * A PerfData object tracks all the client performance data
	 * @author Matt Rhoades
	 *
	 */
	protected static class PerfData {
		
		protected PerfKey Key;
		
		protected String Message;
		/**
		 * When startTimestamp() is called
		 */
		protected long StartStamp = 0;

		/**
		 * The original value of Key_loaded and Key_Launched (so the harness can determine when a new value is set)
		 */
		protected String OriginalFinishStamp = null;
		protected String OriginalLaunchStamp = null;

		/**
		 * The new value of Key_loaded and Key_Launched
		 */
		protected String FinishStamp = null;
		protected String LaunchStamp = null;

		
		public PerfData(PerfKey key, String message) {
			Key = key;
			Message = message;
		}
		
		public String prettyPrint() {
			
			if ( StartStamp == 0 ) {
				// No start time!
				return ("0, 0, 0, 0, 0, 0, Error: No Start Stamp");
			}
			
			if ( FinishStamp == null || FinishStamp.trim().equals("")) {
				return ("0, 0, 0, 0, 0, 0, Error: No Finish Stamp");
			}
			
			// The 'real-time' delta from selenium
			String rDelta = "" + (Long.parseLong(FinishStamp) - StartStamp);
			
			// The 'internal-time' delta from the ajax app
			String iDelta = "0";
			if ( LaunchStamp != null && !LaunchStamp.trim().equals("") ) {
				iDelta = "" + (Long.parseLong(FinishStamp) - Long.parseLong(LaunchStamp));
			}

			return (String.format("%s, %s, %s, %s, %s, %s, %s",
					Key, "" + StartStamp, LaunchStamp, FinishStamp, rDelta, iDelta, Message));
		}
		
		public static String prettyPrintHeaders() {
			return ("Key, Start, Launched, Loaded, Real Time, Internal Time, Description");
		}
	}

	/**
	 * Start monitoring the specified perf metric
	 * @param key the perf metric to watch
	 * @return
	 */
	public static PerfToken startTimestamp(PerfKey key, String message) {
		logger.info("startTimestamp("+ key +")");

		if ( !getInstance().Enabled )
			return (null);

		
		PerfToken token = new PerfToken();
		PerfData data = new PerfData(key, message);
		
		// Save the data to the table
		getInstance().metrics.put(token, data);

		data.OriginalLaunchStamp = getInstance().getValue(key.LaunchKey);
		data.OriginalFinishStamp = getInstance().getValue(key.FinishKey);
		data.StartStamp = System.currentTimeMillis();

		logger.trace(key + "_start="+ data.StartStamp);

		return (token);
	}
	
	/**
	 * Wait for the perf metric (being tracked by 'token') to be set
	 * @param key the perf metric to watch
	 * @return
	 * @throws HarnessException 
	 */
	public static void waitTimestamp(PerfToken token) throws HarnessException {
		logger.info("waitTimestamp("+ token +")");

		if ( !getInstance().Enabled )
			return;

		if ( token == null ) {
			logger.warn("Null token");
			return;
		}
		
		PerfData data = getInstance().metrics.get(token);
		if ( data == null ) {
			logger.warn("Null data");
			return;
		}

		for (int i = 0; i < 30; i ++) {
			
			String loaded = getInstance().getValue(data.Key.FinishKey);
			if ( loaded != null && !loaded.equals("") ) {
				if ( !loaded.equals(data.OriginalFinishStamp) ) {
					data.FinishStamp = loaded;
					break;
				}
			}

			SleepUtil.sleep(1000);
		}
		
		data.LaunchStamp = getInstance().getValue(data.Key.LaunchKey);
		
		// Log the data to a text file
		traceLog.info(data.prettyPrint());
		
		// Log the data to the database
		PerfDatabase.record(data);
		
	}
	
	
	/**
	 * Return a map of URL query parameters, required to enable perf metrics from the Zimbra ajax app.
	 * See config.properties performance.metrics.query value (per http://bugzilla.zimbra.com/show_bug.cgi?id=61972#c11 : perfMetric=1)
	 * @return
	 */
	public Map<String, String> getQueryMap() {
		Map<String, String> map = new HashMap<String, String>();
		
		// Use the app property, if specified
		// i.e. "coverage.query.AJAX"
		// But, if not specified, default to the non-specific property
		// i.e. "coverage.query"
		//
		String property = ZimbraSeleniumProperties.getStringProperty("performance.metrics.query", "");
		String appPoperty = ZimbraSeleniumProperties.getStringProperty("performance.metrics.query."+ ZimbraSeleniumProperties.getAppType(), null );
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

	/**
	 * Set the folder where perf metric data will be logged
	 * @param folder
	 * @throws IOException
	 */
	public static void setOutputFolder(String folder) throws IOException {
		File f = new File(folder + "/perf.txt");
		
		logger.info("setOutputFolder("+ f.getCanonicalPath() +")");

		// Remove any old appenders
		traceLog.removeAllAppenders();
		
		// Add a new appender
		FileAppender perfAppender = new FileAppender(new PatternLayout("%-4r %-5p - %m%n"), folder + "/perf.txt", false);
		traceLog.addAppender(perfAppender);
		traceLog.setLevel(Level.TRACE);
		
		// Print the server version
		try {
			traceLog.info("Server Version: "+ ZimbraSeleniumProperties.zimbraGetVersionString());
		} catch (HarnessException e) {
			logger.warn("Unable to determine the server version to log into the perf output.", e);
		}
		
		// Print the header
		traceLog.info(PerfData.prettyPrintHeaders());
		
	}

	/**
	 * Get the perf metric value.  See http://bugzilla.zimbra.com/show_bug.cgi?id=61972
	 * @param key
	 * @param type
	 * @return
	 */
	private String getValue(String id) {
		String value = "";
		try {
			value = this.sGetEval("this.browserbot.getCurrentWindow().document.getElementById('"+ id +"').innerHTML");
		} catch (Exception e) {
			logger.debug(e);
		}
		logger.trace(id + "=" + value);
		return (value);
	}
	
	protected Hashtable<PerfToken, PerfData> metrics = null;
	
	public boolean Enabled = false;
	
	public static PerfMetrics getInstance() {
		if (Instance == null) {
			synchronized(PerfMetrics.class) {
				if ( Instance == null) {
					Instance = new PerfMetrics();
				}
			}
		}
		return (Instance);
	}
	
	protected volatile static PerfMetrics Instance;
	
	protected PerfMetrics() {	
		logger.info("New "+ this.getClass().getCanonicalName());
		
		metrics = new Hashtable<PerfToken, PerfData>();
		
		
	}

}
