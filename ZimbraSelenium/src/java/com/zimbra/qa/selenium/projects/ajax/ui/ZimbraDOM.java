package com.zimbra.qa.selenium.projects.ajax.ui;

import java.util.*;

import org.apache.log4j.*;
import org.json.*;

import com.thoughtworks.selenium.SeleniumException;
import com.zimbra.qa.selenium.framework.core.ClientSessionFactory;
import com.zimbra.qa.selenium.framework.util.HarnessException;


/**
 * This utility class helps the harness determine the dynamically generated
 * DOM ID's.  Based on given paramenters, the DOM ID can be found using
 * Zimbra app provided ZmId.lookup().
 * 
 * There are two ways to access the methods: instance and static
 * 
 * Using instance: create a ZimbraDOM, add the params, then getID(s)
 * Using static: create a JSONObject, add the params, then ZimbraDOM.getID(JSON)
 * 
 * @author Matt Rhoades
 *
 */
public class ZimbraDOM {
	public static Logger logger = LogManager.getLogger(ZimbraDOM.class);

	
	////
	// Hints:
	//
	// Use ZmId.showIds() in the firebug console to find all
	// the currently defined IDs that can be returned
	// using ZmId.lookup().
	// 1. Open firefox with firebug
	// 2. Login to Zimbra using ?dev=1
	// 3. Execute test case
	// 4. In firebug console, type ZmId.showIds()
	// 5. In the zimbra debug window, the currently defined ID's will be shown
	//
	// Add any undefined values to the static definitions below.
	//
	////
	
	
	public static class KEYS {
		
		public static final String APP = "app";
		public static final String COMPONENT_NAME = "componentName";
		public static final String COMPONENT_TYPE = "componentType";
		public static final String CONTAINING_VIEW = "containingView";
		public static final String SKIN_COMPONENT = "skinComponent";
		
	}
	
	
	public static class APP {
	
		public static final String APP_TASKS = "ZmId.APP_TASKS"; 
		
	}
	
	public static class COMPONENT_NAME {
		
		public static final String VIEW_TASKLIST = "ZmId.VIEW_TASKLIST";
		
		public static final String OP_EDIT = "ZmId.OP_EDIT";
		public static final String OP_DELETE = "ZmId.OP_DELETE";
		public static final String OP_PRINT = "ZmId.OP_PRINT";
		public static final String OP_MARK_AS_COMPLETED = "ZmId.OP_MARK_AS_COMPLETED";
		public static final String OP_CLOSE = "ZmId.OP_CLOSE";
		
		public static final String OP_MOVE_MENU = "ZmId.OP_MOVE_MENU";
		public static final String OP_TAG_MENU = "ZmId.OP_TAG_MENU";
		public static final String OP_VIEW_MENU = "ZmId.OP_VIEW_MENU";
		
		public static final String SEP = "SEP";

	}
	
	public static class COMPONENT_TYPE {
		
		public static final String WIDGET_VIEW = "ZmId.WIDGET_VIEW";
		public static final String WIDGET_BUTTON = "ZmId.WIDGET_BUTTON";
		
	}

	public static class CONTAINING_VIEW {
		
		public static final String VIEW_TASKLIST = "ZmId.VIEW_TASKLIST";
		
	}

	public static class SKIN_COMPONENT {
		
		public static final String SKIN_APP_MAIN = "ZmId.SKIN_APP_MAIN";
		public static final String SKIN_APP_TOP_TOOLBAR = "ZmId.SKIN_APP_TOP_TOOLBAR";
		
	}

	////
	// BEGIN: instance methods
	////
	
	
	protected JSONObject MyJSON = null;
	
	/**
	 * Create a new ZimbraDOM to determine a DOM ID
	 */
	public ZimbraDOM() {
		MyJSON = new JSONObject();
	}
	
	/**
	 * Add a new parameter to narrow down the DOM search.  For example:
	 *  to only look for DOM elements in the tasks app, use:
	 *  accumulate(ZimbraDOM.KEYS.APP, ZimbraDOM.APP.APP_TASKS);
	 *  to only look for buttons, use:
	 *  accumulate(ZimbraDOM.KEYS.COMPONENT_TYPE, ZimbraDOM.COMPONENT_TYPE.WIDGET_BUTTON);
	 *  to only look for delete operations, use:
	 *  accumulate(ZimbraDOM.KEYS.COMPONENT_NAME, ZimbraDOM.COMPONENT_NAME.OP_DELETE);			
     *
	 * @param key (ZimbraDOM.KEYS)
	 * @param value (ZimbraDOM.COMPONENT_TYPE, ZimbraDOM.COMPONENT_NAME, etc.)
	 * @return
	 * @throws HarnessException
	 */
	public ZimbraDOM accumulate(String key, Object value) throws HarnessException {
		try {
			MyJSON.accumulate(key, value);
			return (this);
		} catch (JSONException e) {
			throw new HarnessException(e);
		}
	}
	
	/**
	 * Return the current DOM id that this ZimbraDOM points to
	 * @return
	 * @throws HarnessException if more than one DOM element is referenced 
	 */
	public String getID() throws HarnessException {
		return (ZimbraDOM.getID(this.MyJSON));
	}
	
	/**
	 * Return the current list of DOM ids that this ZimbraDOM points to
	 * @return
	 * @throws HarnessException
	 */
	public List<String> getIDs() throws HarnessException {
		return (ZimbraDOM.getIDs(this.MyJSON));
	}
	
	////
	// END: instance methods
	////

	
	
	
	
	////
	// BEGIN: static methods
	////

	/**
	 * Given a JSONObject with key/value pairs for ZmId.lookup(),
	 * return the current DOM id that the JSONObject points to
	 * 
	 * @param json
	 * @return
	 * @throws HarnessException if more than one DOM element is referenced 
	 */
	public static String getID(JSONObject json) throws HarnessException {
		logger.debug("getId()");
		
		List<String> ids = getIDs(json);
		if ( ids.size() == 1 ) {
			String id = ids.get(0);
			logger.info("getId() = "+ id);
			return (id);
		}
		
		throw new HarnessException("Incorrect number of matches.  Expected 1, got: "+ ids.size());

	}
	
	/**
	 * Given a JSONObject with key/value pairs for ZmId.lookup(),
	 * return the current list of DOM ids that the JSONObject points to
	 * @return
	 * @throws HarnessException
	 */
	public static List<String> getIDs(JSONObject json) throws HarnessException {
		logger.debug("getIds()");
		
		// The lookup method returns a comma separated list of matching DOM ids
		List<String> ids = Arrays.asList(lookup(json).split(","));
		logger.info("getIds() = " + Arrays.toString(ids.toArray()));

		return (ids);
	}
	
	////
	// END: static methods
	////


	
	
	
	
	
	
	/**
	 * Use selenium.getEval() to call ZmId.lookup()
	 * @param parms a JSONObject to use, such as "ZmId.lookup(JSONObject)"
	 * @return
	 * @throws HarnessException on Selenium exception trying to call getEval()
	 */
	protected static String lookup(JSONObject parms) throws HarnessException {

		String command = null;
		
		try {
			
			StringBuilder js = new StringBuilder();
			js.append("var ZmId = this.browserbot.getUserWindow().top.ZmId;");
			js.append("var idParams = ").append(parms.toString().replace("\"", "")).append(";");
			js.append("var domIds = ZmId.lookup(idParams);");
			js.append("domIds;");			
			command = js.toString();
			
			logger.debug("Selenium.getEval("+ command +")");
			String value = ClientSessionFactory.session().selenium().getEval(command);
			logger.info("Selenium.getEval("+ command +") = "+ value);
			
			return (value);

		} catch (SeleniumException e) {
			throw new HarnessException("Selenium.getEval("+ command +") threw SeleniumException", e);
		}

	}
	
}
