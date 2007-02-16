/*
 * ***** BEGIN LICENSE BLOCK *****
 * Version: ZPL 1.2
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.2 ("License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.zimbra.com/license
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See
 * the License for the specific language governing rights and limitations
 * under the License.
 * 
 * The Original Code is: Zimbra Collaboration Suite Web Client
 * 
 * The Initial Developer of the Original Code is Zimbra, Inc.
 * Portions created by Zimbra are Copyright (C) 2005, 2006 Zimbra, Inc.
 * All Rights Reserved.
 * 
 * Contributor(s):
 * 
 * ***** END LICENSE BLOCK *****
 */

function ZaSettings() {
}

ZaSettings.initialized = false;
ZaSettings.initializing = false
ZaSettings.initMethods = new Array();
ZaSettings.RESULTSPERPAGE = "25";
ZaSettings.MAXSEARCHRESULTS = "50";
/**
* Look for admin name cookies and admin type cookies
**/
ZaSettings.postInit = function() {
	//Instrumentation code start	
	if(ZaSettings.initMethods) {
		var cnt = ZaSettings.initMethods.length;
		for(var i = 0; i < cnt; i++) {
			if(typeof(ZaSettings.initMethods[i]) == "function") {
				ZaSettings.initMethods[i].call(this);
			}
		}
	}	
	//Instrumentation code end	
	var shell = DwtShell.getShell(window);
	var appCtxt = ZaAppCtxt.getFromShell(shell);
	var appController = appCtxt.getAppController();
	
	appController._launchApp();	
	ZaZimbraAdmin.setOnbeforeunload(ZaZimbraAdmin._confirmExitMethod);
	ZaSettings.initialized = true;
	ZaSettings.initializing = false;
};
ZaSettings.init = function () {
	if(ZaSettings.initialized || ZaSettings.initializing)
		return;
		
	ZaSettings.initializing = true ;
	DBG.println(AjxDebug.DBG1,"Initializing ZaSettings");		
	

	try {
		var soapDoc = AjxSoapDoc.create("GetAdminExtensionZimletsRequest", "urn:zimbraAdmin", null);	
		var command = new ZmCsfeCommand();
		var params = new Object();
		params.soapDoc = soapDoc;	
		var resp = command.invoke(params);
		var zimlets = null;
		try {
			if(resp && resp.Body && resp.Body.GetAdminExtensionZimletsResponse && resp.Body.GetAdminExtensionZimletsResponse.zimlets && resp.Body.GetAdminExtensionZimletsResponse.zimlets.zimlet) {
				zimlets = resp.Body.GetAdminExtensionZimletsResponse.zimlets.zimlet;
			}
		} catch (ex) {
			//go on
		}
		if(zimlets && zimlets.length > 0) {
			var includes = new Array();	
			var cnt = zimlets.length;
			for(var ix = 0; ix < cnt; ix++) {
				if(zimlets[ix] && zimlets[ix].zimlet && zimlets[ix].zimlet[0] && zimlets[ix].zimletContext && zimlets[ix].zimletContext[0]) {
					var zimlet = zimlets[ix].zimlet[0];
					var zimletContext = zimlets[ix].zimletContext[0];
					if(zimlet.include && zimlet.include.length>0) {
	
						var cnt2 = zimlet.include.length;
						for (var j=0;j<cnt2;j++) {
							includes.push(zimletContext.baseUrl + zimlet.include[j]._content);
						}
					}
				} else {
					continue;
				}
			}
			try {
				if(includes.length > 0)
					AjxInclude(includes, null,new AjxCallback(ZaSettings.postInit ));	
			} catch (ex) {
				//go on
				throw ex;
			}
					
		} else {
			ZaSettings.postInit();
		}
	} catch (ex) {
		ZaSettings.initializing = false ;
//		DBG.dumpObj(ex);
		throw ex;	
	}
	
	// post-processing code
/*	DBG.println("+++ document.location.pathname: "+document.location.pathname);
	var files = [ document.location.pathname + "public/adminPost.js" ];
	AjxInclude(files);
	*/
	
};


/**
* Static method so that static code can get the default value of a setting if it needs to.
*
* @param id		the numeric ID of the setting
*/
ZaSettings.get =
function(id) {
	var args = ZaSettings.INIT[id];
	return args ? args[3] : null;
}

// setting types
ZaSettings.T_CONFIG		= 1;

// setting data types
ZaSettings.D_STRING		= 1; // default type
ZaSettings.D_INT			= 2;
ZaSettings.D_BOOLEAN		= 3;
ZaSettings.D_LDAP_TIME 	= 4;
ZaSettings.D_HASH_TABLE 	= 5;
ZaSettings.LOGO_URI = "http://www.zimbra.com";
ZaSettings.CSFE_SERVER_URI = (location.port == "80") ? "/service/admin/soap/" : ":" + location.port + "/service/admin/soap/";
ZaSettings.CSFE_MSG_FETCHER_URI = (location.port == "80") ? "/service/content/get?" : ":" + location.port + "/service/content/get?";
ZaSettings.CONFIG_PATH = "/zimbraAdmin/js/zimbraAdmin/config";
//ZaSettings.ADMIN_NAME_COOKIE = "ZA_ADMIN_NAME_COOKIE";
ZaSettings.myDomainName = "zimbra.com";

var i = 1;
// IDs FOR HTML COMPONENTS IN THE SKIN
ZaSettings.SKIN_APP_BOTTOM_TOOLBAR_ID	= i++;
ZaSettings.SKIN_APP_CHOOSER_ID			= i++;
ZaSettings.SKIN_APP_MAIN_ID				= i++;
ZaSettings.SKIN_APP_TOP_TOOLBAR_ID		= i++;
ZaSettings.SKIN_CURRENT_APP_ID			= i++;
ZaSettings.SKIN_LOGO_ID					= i++;
ZaSettings.SKIN_SASH_ID					= i++;
ZaSettings.SKIN_SEARCH_BUILDER_ID		= i++;
ZaSettings.SKIN_SEARCH_BUILDER_TOOLBAR_ID= i++;
ZaSettings.SKIN_SEARCH_BUILDER_TR_ID		= i++;
ZaSettings.SKIN_SEARCH_ID				= i++;
ZaSettings.SKIN_SHELL_ID					= i++;
ZaSettings.SKIN_STATUS_ID				= i++;
ZaSettings.SKIN_TREE_ID					= i++;
ZaSettings.SKIN_TREE_FOOTER_ID			= i++;
ZaSettings.SKIN_USER_INFO_ID				= i++;
ZaSettings.SKIN_APP_TABS_ID				= i++;
ZaSettings.SKIN_HELP_ID					= i++ ;
ZaSettings.SKIN_DW_ID					= i++ ;

//CONSTANTS FOR ROLE-BASED ACCESS
ZaSettings.STATUS_ENABLED= true;
ZaSettings.STATS_ENABLED= true;
ZaSettings.ACCOUNTS_CHPWD_ENABLED = true;
ZaSettings.ACCOUNTS_ENABLED = true;
ZaSettings.ACCOUNTS_FEATURES_ENABLED = true;
ZaSettings.ACCOUNTS_ADVANCED_ENABLED = true;
ZaSettings.ACCOUNTS_ALIASES_ENABLED=true;
ZaSettings.ACCOUNTS_FORWARDING_ENABLED=true;
ZaSettings.ACCOUNTS_MOVE_ALIAS_ENABLED=true;
ZaSettings.ACCOUNTS_REINDEX_ENABLED=true;
ZaSettings.ACCOUNTS_PREFS_ENABLED = true;
ZaSettings.ACCOUNTS_VIEW_MAIL_ENABLED = true;
ZaSettings.ACCOUNTS_RESTORE_ENABLED = true;
ZaSettings.ACCOUNTS_SECURITY_ENABLED = true;
ZaSettings.COSES_ENABLED=true;
ZaSettings.DOMAINS_ENABLED=true;
ZaSettings.SERVERS_ENABLED=true;
ZaSettings.SERVER_STATS_ENABLED=true;
ZaSettings.GLOBAL_CONFIG_ENABLED= true;
ZaSettings.DISTRIBUTION_LISTS_ENABLED = true;
ZaSettings.MAILQ_ENABLED = true;
ZaSettings.MONITORING_ENABLED = true;
ZaSettings.SYSTEM_CONFIG_ENABLED = true;
ZaSettings.ADDRESSES_ENABLED = true;
ZaSettings.RESOURCES_ENABLED = true;
ZaSettings.SKIN_PREFS_ENABLED = true;
ZaSettings.LICENSE_ENABLED = true;
ZaSettings.ZIMLETS_ENABLED = true;
ZaSettings.ADMIN_ZIMLETS_ENABLED = true;

// initialization for settings: [name, type, data type, default value]
ZaSettings.INIT = new Object();
// IDs FOR HTML COMPONENTS IN THE SKIN
ZaSettings.INIT[ZaSettings.SKIN_APP_BOTTOM_TOOLBAR_ID]	= [null, ZaSettings.T_CONFIG, ZaSettings.D_STRING, "skin_container_app_bottom_toolbar"];
ZaSettings.INIT[ZaSettings.SKIN_APP_CHOOSER_ID]			= [null, ZaSettings.T_CONFIG, ZaSettings.D_STRING, "skin_container_app_chooser"];
ZaSettings.INIT[ZaSettings.SKIN_APP_MAIN_ID]				= [null, ZaSettings.T_CONFIG, ZaSettings.D_STRING, "skin_container_app_main"];
ZaSettings.INIT[ZaSettings.SKIN_APP_TOP_TOOLBAR_ID]		= [null, ZaSettings.T_CONFIG, ZaSettings.D_STRING, "skin_container_app_top_toolbar"];
ZaSettings.INIT[ZaSettings.SKIN_CURRENT_APP_ID]			= [null, ZaSettings.T_CONFIG, ZaSettings.D_STRING, "skin_container_current_app"];
ZaSettings.INIT[ZaSettings.SKIN_LOGO_ID]					= [null, ZaSettings.T_CONFIG, ZaSettings.D_STRING, "skin_container_logo"];
ZaSettings.INIT[ZaSettings.SKIN_SASH_ID]					= [null, ZaSettings.T_CONFIG, ZaSettings.D_STRING, "skin_container_tree_app_sash"];
ZaSettings.INIT[ZaSettings.SKIN_SEARCH_BUILDER_ID]		= [null, ZaSettings.T_CONFIG, ZaSettings.D_STRING, "skin_container_search_builder"];
ZaSettings.INIT[ZaSettings.SKIN_SEARCH_BUILDER_TOOLBAR_ID]= [null, ZaSettings.T_CONFIG, ZaSettings.D_STRING, "skin_container_search_builder_toolbar"];
ZaSettings.INIT[ZaSettings.SKIN_SEARCH_BUILDER_TR_ID]		= [null, ZaSettings.T_CONFIG, ZaSettings.D_STRING, "skin_tr_search_builder"];
ZaSettings.INIT[ZaSettings.SKIN_SEARCH_ID]				= [null, ZaSettings.T_CONFIG, ZaSettings.D_STRING, "skin_container_search"];
ZaSettings.INIT[ZaSettings.SKIN_SHELL_ID]					= [null, ZaSettings.T_CONFIG, ZaSettings.D_STRING, "skin_outer"];
ZaSettings.INIT[ZaSettings.SKIN_STATUS_ID]				= [null, ZaSettings.T_CONFIG, ZaSettings.D_STRING, "skin_container_status"];
ZaSettings.INIT[ZaSettings.SKIN_TREE_ID]					= [null, ZaSettings.T_CONFIG, ZaSettings.D_STRING, "skin_container_tree"];
ZaSettings.INIT[ZaSettings.SKIN_TREE_FOOTER_ID]			= [null, ZaSettings.T_CONFIG, ZaSettings.D_STRING, "skin_container_tree_footer"];

ZaSettings.INIT[ZaSettings.SKIN_USER_INFO_ID]				= [null, ZaSettings.T_CONFIG, ZaSettings.D_STRING, "skin_container_quota"];
ZaSettings.timeZoneChoices = new XFormChoices(AjxTimezoneData.TIMEZONE_RULES, XFormChoices.OBJECT_LIST, "serverId", "serverId");	
ZaSettings.INIT[ZaSettings.SKIN_APP_TABS_ID]					= [null, ZaSettings.T_CONFIG, ZaSettings.D_STRING, "skin_container_app_tabs"];

ZaSettings.SKIN_LOGOFF_DOM_ID = "skin_container_logoff" ;
ZaSettings.SKIN_HELP_DOM_ID = "skin_container_help" ;
ZaSettings.SKIN_DW_DOM_ID = "skin_container_dw" ;
ZaSettings.SKIN_TABS_DOM_ID = "skin_container_app_tabs" ;
