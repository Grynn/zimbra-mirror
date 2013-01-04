package com.zimbra.qa.selenium.projects.ajax.ui;

import java.util.*;

import org.apache.log4j.*;
import org.json.JSONObject;

import com.thoughtworks.selenium.SeleniumException;
import com.zimbra.qa.selenium.framework.core.ClientSessionFactory;
import com.zimbra.qa.selenium.framework.util.HarnessException;

public class ZimbraDOM {
	public static Logger logger = LogManager.getLogger(ZimbraDOM.class);
	
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
	
	public static List<String> getIDs(JSONObject json) throws HarnessException {
		logger.debug("getIds()");
		
		// The lookup method returns a comma separated list of matching DOM ids
		List<String> ids = Arrays.asList(lookup(json).split(","));
		logger.info("getIds() = " + Arrays.toString(ids.toArray()));

		return (ids);
	}
	
	public static String getID(Map<String, String> parms) throws HarnessException {
		return (getID(new JSONObject(parms)));
	}
	
	public static List<String> getIDs(Map<String, String> parms) throws HarnessException {
		return (getIDs(new JSONObject(parms)));
	}
	
	protected static String lookup(JSONObject parms) throws HarnessException {

		String command = null;
		
		try {
			
			// The ZmId methods don't like the double quotes around the strings
			// in the JSON object (which is the standard).  So, strip them.
			//
			String idParams = parms.toString().replaceAll("\"", "");
			
			StringBuilder js = new StringBuilder();
			js.append("var ZmId = this.browserbot.getUserWindow().top.ZmId;");
			js.append("var idParams = ").append(idParams).append(";");
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
