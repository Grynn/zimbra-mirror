package com.zimbra.qa.selenium.projects.ajax.ui;

import java.util.*;
import java.util.Map.Entry;

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
	
	public static Map<String, EnumMap<KEY,String>> ids;
	    
	public static enum KEY {
	    app("app"),
	    componentName("componentName"),
	    componentType("componentType"),
	    containingView("containingView"),		
	    skinComponent("skinComponent");	
		
	    private String key;

	    private KEY(final String key) {
		this.key = key;
	    }

	    public String getKEY() {
		return key;
	    }
	}
	    
	public static final String SCRIPT = "var AjxUtil = this.browserbot.getUserWindow().top.AjxUtil; " +
		"var AjxStringUtil = this.browserbot.getUserWindow().top.AjxStringUtil;" +
		"var ZmId = this.browserbot.getUserWindow().top.ZmId;" +
		"var ids = ZmId.lookup()," +
		"len = ids.length," +
		"backMap = ZmId._getBackMap()," +
		"text = \"\\n\\n{\"," +
		"i; " +
		"for (i = 0; i < len; i++) " +
		"{var id = ids[i].id;" +
		"var params = ZmId._idHash[id];" +
		"text += \"\\n\" + id + \":\" + params.description + \",\\n\";" +
		"var paramNames = AjxUtil.keys(params).sort();" +
		"for (var j = 0; j < paramNames.length; j++) " +
		"{var paramName = paramNames[j];" +
		"if (paramName === 'id' || paramName === 'description') {" +
		"continue;" +
		"}" +
		"var value = params[paramName];" +
		"if (!value) {" +
		"continue;" +
		"}" +
		"value = backMap[value] ? \"ZmId.\" + backMap[value] : value;" +
		"text += paramName + \":\" + value + \",\\n\";}text + \"}\\n\\n\"}";
	
	
	public static String jsShowIds() throws HarnessException{
	    try {
		final String response = 
			ClientSessionFactory.session().selenium().getEval(SCRIPT);
		    
		logger.info("\n...showIds response: " + response);
		    
		return response;
	    } catch (Exception ex) {
		throw new HarnessException(ex);				
	    }
	}	    
	
	public static Map<String, EnumMap<KEY,String>> getMapFromScript() throws HarnessException{
	    final Map<String, EnumMap<KEY,String>> map = new HashMap<String, EnumMap<KEY,String>>(); 
	    String resp;
	    try {
		resp = jsShowIds().replaceAll("[\n\\{\\}]","");
		final List<String> args = Arrays.asList(resp.split(","));
		EnumMap<KEY,String> emap = null;
		for(String arg : args){
		    final String[] arr = arg.trim().split(":");
		    if(arr[0].startsWith("zcs")){
			emap = new EnumMap<KEY,String>(KEY.class); 
			map.put(arr[0],emap);
			continue;
		    }
		    if(arr[0].contains(KEY.app.getKEY())){
			emap.put(KEY.app, arr[1]);
		    }else if(arr[0].contains(KEY.componentName.getKEY())){
			emap.put(KEY.componentName, arr[1]);
		    }else if(arr[0].contains(KEY.componentType.getKEY())){
			emap.put(KEY.componentType, arr[1]);
		    }else if(arr[0].contains(KEY.containingView.getKEY())){
			emap.put(KEY.containingView, arr[1]);
		    }else if(arr[0].contains(KEY.skinComponent.getKEY())){
			emap.put(KEY.skinComponent, arr[1]);
		    }
		}
	    } catch (Exception ex) {
		throw new HarnessException(ex);				
	    }
	    ids = map;
	  		
	    return ids;
	}

	public static JSONObject getJsonFromScript() throws HarnessException{
	    final JSONObject jso = new JSONObject(); 
	    String resp;
	    try {
		resp = jsShowIds().replaceAll("[\n\\{\\}]","");
		final List<String> args = Arrays.asList(resp.split(","));
		for(String arg : args){
		    final String[] arr = arg.trim().split(":");
		    jso.accumulate(arr[0], arr[1]);
		}		    
	    } catch (Exception ex) {
		throw new HarnessException(ex);				
	    }
	    return jso;
	}
		        
	public static String getIdFromMap(final String... params) throws HarnessException{
	    String id = null;
	    if(params == null || !(params.length > 0)){
		logger.info("...empty arguments list");		
	    } else{		
		if(ids == null || ids.isEmpty()){
		    getMapFromScript();
		}
		final List<String> args = Arrays.asList(params);
		final Set<Entry<String, EnumMap<KEY, String>>> set = ids.entrySet();
		for(Entry <String, EnumMap<KEY, String>> en : set){
		    final EnumMap<KEY, String> emap = en.getValue();
		    final Collection<String> vals = emap.values();
		    if(vals.containsAll(args)){
			if(id == null){
			    id = en.getKey();
			    logger.info("\n id = " + id + 
				    "\n params provided: " + args +
				    "\n available values: " + vals);
			    if(args.containsAll(vals)){
				break;
			    }
			}else{
			    logger.info("\n for provided params: " + args +
				    "\n ...found more than one matches of id");
			    break;
			}
		    }		    
		}
		if(id==null){
		    logger.info("\n for provided params: " + args +
			    "...id is null ");
		}
	    }	
	    return id;
	}
	    
	
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
