/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2005, 2006, 2007, 2008, 2009, 2010 Zimbra, Inc.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.3 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 */

ZaSettings = function() {
}

ZaSettings.initialized = false;
ZaSettings.initializing = false
ZaSettings.initMethods = new Array();
ZaSettings.RESULTSPERPAGE = 25;
ZaSettings.MAXSEARCHRESULTS = 50;
ZaSettings.HAVE_MORE_DOMAINS = false;
/**
* Look for admin name cookies and admin type cookies
**/
ZaSettings.postInit = function() {
    if (AjxEnv.hasFirebug)
        console.log("Finishing loading all the zimlets, and ready to initialize the application ...");
        
	//Instrumentation code end	
	var shell = DwtShell.getShell(window);
	var appCtxt = ZaAppCtxt.getFromShell(shell);
	var appController = appCtxt.getAppController();
	appController._createApp();
	        
    //Instrumentation code start
	if(ZaSettings.initMethods) {
		var cnt = ZaSettings.initMethods.length;
		for(var i = 0; i < cnt; i++) {
			if(typeof(ZaSettings.initMethods[i]) == "function") {
				ZaSettings.initMethods[i].call(this);
			}
		}
	}	

	appController._launchApp();
    if (ZaZimbraAdmin.isWarnOnExit) {
	    ZaZimbraAdmin.setOnbeforeunload(ZaZimbraAdmin._confirmExitMethod);
    }
	ZaSettings.initialized = true;
	ZaSettings.initializing = false;
};

ZaSettings.initRights = function () {
	ZaSettings.ENABLED_UI_COMPONENTS=[];
	ZaZimbraAdmin.currentAdminAccount = new ZaAccount();
	if(ZaZimbraAdmin.currentAdminId) {
		ZaZimbraAdmin.currentAdminAccount.load("id", ZaZimbraAdmin.currentAdminId,false,true);
	} else {
		ZaZimbraAdmin.currentAdminAccount.load("name", ZaZimbraAdmin.currentUserLogin,false,true);
	}
	if(AjxUtil.isEmpty(ZaZimbraAdmin.currentAdminAccount.attrs[ZaAccount.A_zimbraAdminConsoleUIComponents])) {
		ZaZimbraAdmin.currentAdminAccount.attrs[ZaAccount.A_zimbraAdminConsoleUIComponents] = [];
		//if this is a system admin account - enable access to all UI elements
		if(ZaZimbraAdmin.currentAdminAccount.attrs[ZaAccount.A_zimbraIsAdminAccount] == 'TRUE') {
			ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.CARTE_BLANCHE_UI] = true;
		}			
	} else {
		if(typeof(ZaZimbraAdmin.currentAdminAccount.attrs[ZaAccount.A_zimbraAdminConsoleUIComponents])=="string") {
			ZaZimbraAdmin.currentAdminAccount.attrs[ZaAccount.A_zimbraAdminConsoleUIComponents] = [ZaZimbraAdmin.currentAdminAccount.attrs[ZaAccount.A_zimbraAdminConsoleUIComponents]];
			//ZaSettings.ENABLED_UI_COMPONENTS[ZaZimbraAdmin.currentAdminAccount.attrs[ZaAccount.A_zimbraAdminConsoleUIComponents]] = true;		
		}	
		var cnt = ZaZimbraAdmin.currentAdminAccount.attrs[ZaAccount.A_zimbraAdminConsoleUIComponents].length;
		for(var i=0;i<cnt;i++) {
			ZaSettings.ENABLED_UI_COMPONENTS[ZaZimbraAdmin.currentAdminAccount.attrs[ZaAccount.A_zimbraAdminConsoleUIComponents][i]] = true;
		}
	}
	//load global permissions, e.g. createTopDomain, createCos
	var soapDoc = AjxSoapDoc.create("GetEffectiveRightsRequest", ZaZimbraAdmin.URN, null);
	var elTarget = soapDoc.set("target", "");
	elTarget.setAttribute("type","global");


	var elGrantee = soapDoc.set("grantee", ZaZimbraAdmin.currentUserId);
	elGrantee.setAttribute("by","id");
	
	var csfeParams = new Object();
	csfeParams.soapDoc = soapDoc;	
	var reqMgrParams = {} ;
	reqMgrParams.controller = ZaApp.getInstance().getCurrentController();
	reqMgrParams.busyMsg = ZaMsg.BUSY_REQUESTING_ACCESS_RIGHTS ;
	try {
		var resp = ZaRequestMgr.invoke(csfeParams, reqMgrParams ).Body.GetEffectiveRightsResponse;
		ZaZimbraAdmin.currentAdminAccount.initEffectiveRightsFromJS(resp);
	} catch (ex) {
		//not implemented yet
	}	
	
	var comps = ZaSettings.getUIComponents() ;
    var cnt = comps.length;
    for(var i=0;i<cnt;i++) {
      ZaSettings.ENABLED_UI_COMPONENTS[comps[i]._content] = true;
    }
}
ZaSettings.initMethods.push(ZaSettings.initRights);

ZaSettings.getUIComponents = function (item) {
    soapDoc = AjxSoapDoc.create("GetAdminConsoleUICompRequest", ZaZimbraAdmin.URN, null);
	if (item && item.type && item.id) {
        var el = soapDoc.set(item.type, item.id) ;
        el.setAttribute("by", "id");
    }
    csfeParams = new Object();
	csfeParams.soapDoc = soapDoc;
	reqMgrParams = {} ;
	reqMgrParams.controller = ZaApp.getInstance().getCurrentController();
	reqMgrParams.busyMsg = ZaMsg.BUSY_REQUESTING_UI_COMP ;
	try {
		var resp = ZaRequestMgr.invoke(csfeParams, reqMgrParams ).Body.GetAdminConsoleUICompResponse;
		var comps = [];
		if(!AjxUtil.isEmpty(resp.a)) {
			if (typeof(resp.a) == "string" ) {
				comps = [resp.a];
			} else if(resp.a instanceof Array) {
				comps = resp.a;
			}
		}

        return comps ;
    } catch (ex) {
		//not implemented yet
	}

    return [];
}

ZaSettings.loadStyles = function(includes) {
    var head = document.getElementsByTagName("head")[0];
    for (var i = 0; i < includes.length; i++) {
        var style = document.createElement("link");
        style.type = "text/css";
        style.rel = "stylesheet";
        style.href = includes[i];

        head.appendChild(style);
    }
};

ZaSettings.init = function () {
	if(ZaSettings.initialized || ZaSettings.initializing)
		return;
		
	ZaSettings.initializing = true ;
	DBG.println(AjxDebug.DBG1,"Initializing ZaSettings");		
	

	try {
		if(appDevMode || (DBG.getDebugLevel() > AjxDebug.NONE) || (location.search && (location.search.indexOf("mode=mjsf") != -1)) ) {
			var soapDoc = AjxSoapDoc.create("GetAdminExtensionZimletsRequest", ZaZimbraAdmin.URN, null);	
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
	            if (AjxEnv.hasFirebug)
	                console.log("Error Getting the Zimlets: " + ex.message);
	        }
			if(zimlets && zimlets.length > 0) {
				var includes = new Array();	
				var cssIncludes = new Array();	
				var cnt = zimlets.length;
				for(var ix = 0; ix < cnt; ix++) {
					if(zimlets[ix] && zimlets[ix].zimlet && zimlets[ix].zimlet[0] && zimlets[ix].zimletContext && zimlets[ix].zimletContext[0]) {
						var zimlet = zimlets[ix].zimlet[0];
						var zimletContext = zimlets[ix].zimletContext[0];
	                    if (AjxEnv.hasFirebug)
	                        console.log("Adding zimlet: " + zimlet.name);
	                    //load message file first because consequent files may reference it
	                    includes.push([appContextPath, "/res/", zimlet.name, ".js?v=",appVers,ZaZimbraAdmin.LOCALE_QS].join(""));
						if(zimlet.include && zimlet.include.length>0) {
							var cnt2 = zimlet.include.length;
							for (var j=0;j<cnt2;j++) {
								includes.push(zimletContext.baseUrl + zimlet.include[j]._content + "?v=" +appVers);
							}
						}
						if(zimlet.includeCSS && zimlet.includeCSS.length>0) {
							var cnt3 = zimlet.includeCSS.length;
							for (var j=0;j<cnt3;j++) {
								cssIncludes.push(zimletContext.baseUrl + zimlet.includeCSS[j]._content  + "?v=" +appVers);
							}
						}
					} else {
						continue;
					}
				}
				try {
		
					if(cssIncludes.length > 0){
					    if (AjxEnv.hasFirebug)
	                        console.log ("Loading Zimlets CSS: " + cssIncludes.join(", ") );
	                    ZaSettings.loadStyles(cssIncludes);
	                }
	
					if(includes.length > 0)   {
	                    if (AjxEnv.hasFirebug)
	                        console.log ("Loading Zimlets JS: " + includes.join(", ") );
	                   	AjxInclude(includes, null,new AjxCallback(ZaSettings.postInit ));
	                }
	
	            } catch (ex) {
					//go on
					throw ex;
				}
						
			} else {
				ZaSettings.postInit();
			}
		} else {
			var zimletURL = ["/service/zimlet/res/Zimlets-nodev_all.js.zgz", ".js?v=",appVers,ZaZimbraAdmin.LOCALE_QS].join("");
			AjxInclude([zimletURL], null,new AjxCallback(ZaSettings.postInit ));
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
ZaSettings.CONFIG_PATH = location.pathname + "js/zimbraAdmin/config";
//ZaSettings.ADMIN_NAME_COOKIE = "ZA_ADMIN_NAME_COOKIE";
ZaSettings.myDomainName = null;

ZaSettings.SKIN_IDX = 1;
// IDs FOR HTML COMPONENTS IN THE SKIN
ZaSettings.SKIN_APP_BOTTOM_TOOLBAR_ID	= ZaSettings.SKIN_IDX++;
ZaSettings.SKIN_APP_MAIN_ID				= ZaSettings.SKIN_IDX++;
ZaSettings.SKIN_APP_TOP_TOOLBAR_ID		= ZaSettings.SKIN_IDX++;
ZaSettings.SKIN_CURRENT_APP_ID			= ZaSettings.SKIN_IDX++;
ZaSettings.SKIN_LOGO_ID					= ZaSettings.SKIN_IDX++;
ZaSettings.SKIN_SASH_ID					= ZaSettings.SKIN_IDX++;
ZaSettings.SKIN_SEARCH_BUILDER_ID		= ZaSettings.SKIN_IDX++;
ZaSettings.SKIN_SEARCH_BUILDER_TOOLBAR_ID= ZaSettings.SKIN_IDX++;
ZaSettings.SKIN_SEARCH_ID				= ZaSettings.SKIN_IDX++;
ZaSettings.SKIN_SHELL_ID				= ZaSettings.SKIN_IDX++;
ZaSettings.SKIN_STATUS_ID				= ZaSettings.SKIN_IDX++;
ZaSettings.SKIN_TREE_ID					= ZaSettings.SKIN_IDX++;
ZaSettings.SKIN_USER_INFO_ID			= ZaSettings.SKIN_IDX++;
ZaSettings.SKIN_APP_TABS_ID				= ZaSettings.SKIN_IDX++;
ZaSettings.SKIN_HELP_ID					= ZaSettings.SKIN_IDX++;
ZaSettings.SKIN_DW_ID					= ZaSettings.SKIN_IDX++;
ZaSettings.SKIN_LOGIN_MSG_ID            = ZaSettings.SKIN_IDX++;

//CONSTANTS FOR ROLE-BASED ACCESS
/**
 * In order for an admin to be able to access a UI component, zimbraAdminConsoleUIComponents attribute of the admin's account should contain the corresponding values listed below
 */
//carte blanche - gives access to any UI element
ZaSettings.CARTE_BLANCHE_UI = "cartBlancheUI";

ZaSettings.ALL_UI_COMPONENTS = [] ;

//List views
ZaSettings.ACCOUNT_LIST_VIEW = "accountListView";
ZaSettings.ALL_UI_COMPONENTS.push({ value: ZaSettings.ACCOUNT_LIST_VIEW, label: ZaMsg.UI_Comp_AccountListView });
ZaSettings.DL_LIST_VIEW = "DLListView";
ZaSettings.ALL_UI_COMPONENTS.push({ value: ZaSettings.DL_LIST_VIEW, label: ZaMsg.UI_Comp_DlListView });
ZaSettings.ALIAS_LIST_VIEW = "aliasListView";
ZaSettings.ALL_UI_COMPONENTS.push({ value: ZaSettings.ALIAS_LIST_VIEW, label: ZaMsg.UI_Comp_AliasListView });
ZaSettings.RESOURCE_LIST_VIEW = "resourceListView";
ZaSettings.ALL_UI_COMPONENTS.push({ value: ZaSettings.RESOURCE_LIST_VIEW, label: ZaMsg.UI_Comp_ResourceListView });
ZaSettings.COS_LIST_VIEW = "COSListView";
ZaSettings.ALL_UI_COMPONENTS.push({ value: ZaSettings.COS_LIST_VIEW, label: ZaMsg.UI_Comp_COSListView });
ZaSettings.DOMAIN_LIST_VIEW = "domainListView";
ZaSettings.ALL_UI_COMPONENTS.push({ value: ZaSettings.DOMAIN_LIST_VIEW, label: ZaMsg.UI_Comp_DomainListView });
ZaSettings.SERVER_LIST_VIEW = "serverListView";
ZaSettings.ALL_UI_COMPONENTS.push({ value: ZaSettings.SERVER_LIST_VIEW, label: ZaMsg.UI_Comp_ServerListView });
ZaSettings.ZIMLET_LIST_VIEW = "zimletListView";
ZaSettings.ALL_UI_COMPONENTS.push({ value: ZaSettings.ZIMLET_LIST_VIEW, label: ZaMsg.UI_Comp_ZimletListView });
ZaSettings.ADMIN_ZIMLET_LIST_VIEW = "adminZimletListView";
ZaSettings.ALL_UI_COMPONENTS.push({ value: ZaSettings.ADMIN_ZIMLET_LIST_VIEW, label: ZaMsg.UI_Comp_AdminZimletListView });
ZaSettings.GLOBAL_CONFIG_VIEW="globalConfigView";
ZaSettings.ALL_UI_COMPONENTS.push({ value: ZaSettings.GLOBAL_CONFIG_VIEW, label: ZaMsg.UI_Comp_globalConfigView });
ZaSettings.GLOBAL_STATUS_VIEW = "globalServerStatusView";
ZaSettings.ALL_UI_COMPONENTS.push({ value: ZaSettings.GLOBAL_STATUS_VIEW, label: ZaMsg.UI_Comp_GlobalStatusView });
//ZaSettings.GLOBAL_STATS_VIEW = "globalServerStatisticsView";
//ZaSettings.ALL_UI_COMPONENTS.push({ value: ZaSettings.GLOBAL_STATS_VIEW, label: ZaMsg.UI_Comp_GlobalStatsView });
ZaSettings.SAVE_SEARCH = "saveSearch";
ZaSettings.ALL_UI_COMPONENTS.push({ value: ZaSettings.SAVE_SEARCH, label: ZaMsg.UI_Comp_SaveSearch });
ZaSettings.MAILQ_VIEW = "mailQueue";
ZaSettings.ALL_UI_COMPONENTS.push({ value: ZaSettings.MAILQ_VIEW, label: ZaMsg.UI_Comp_mailQueueView });

/*   
ZaSettings.SERVER_STATS_VIEW = "perServerStatisticsView";
ZaSettings.ALL_UI_COMPONENTS.push({ value: ZaSettings.SERVER_STATS_VIEW, label: ZaMsg.UI_Comp_ServerStatsView });
*/

//List view groups
ZaSettings.OVERVIEW_CONFIG_ITEMS = [ZaSettings.COS_LIST_VIEW,ZaSettings.ZIMLET_LIST_VIEW,ZaSettings.SERVER_LIST_VIEW,ZaSettings.ADMIN_ZIMLET_LIST_VIEW,
    ZaSettings.DOMAIN_LIST_VIEW,ZaSettings.GLOBAL_CONFIG_VIEW];
ZaSettings.OVERVIEW_ADDRESSES_ITEMS = [ZaSettings.ACCOUNT_LIST_VIEW,ZaSettings.ALIAS_LIST_VIEW,ZaSettings.DL_LIST_VIEW,ZaSettings.RESOURCE_LIST_VIEW];
ZaSettings.OVERVIEW_TOOLS_ITEMS = [ZaSettings.MAILQ_VIEW];
ZaSettings.OVERVIEW_MONITORING_ITEMS = [ZaSettings.GLOBAL_STATS_VIEW,ZaSettings.GLOBAL_STATUS_VIEW,ZaSettings.SERVER_STATS_VIEW];

//Domain operations  - it might be duplicated to domain view tabs
ZaSettings.DOMAIN_GAL_WIZ = "domainGALWizard";
ZaSettings.DOMAIN_AUTH_WIZ = "domainAuthWizard";
ZaSettings.DOMAIN_WIKI_WIZ = "domainWikiWizard";
ZaSettings.DOMAIN_CHECK_MX_WIZ = "domainCheckMXWiz";

//Global Server Statistics View
/* No need to have the statistics view since they are all system admin only rights
ZaSettings.GLOBAL_STATS_MSG_COUNT_TAB = "globalServerStatsMsgCountTab" ;
ZaSettings.ALL_UI_COMPONENTS.push({ value: ZaSettings.GLOBAL_STATS_MSG_COUNT_TAB, label: ZaMsg.UI_Comp_globalStatsMsgCountTab });

ZaSettings.GLOBAL_STATS_MSG_VOL_TAB = "globalServerStatsMsgVolTab" ;
ZaSettings.ALL_UI_COMPONENTS.push({ value: ZaSettings.GLOBAL_STATS_MSG_VOL_TAB, label: ZaMsg.UI_Comp_globalStatsMsgVolTab });

ZaSettings.GLOBAL_STATS_MSG_ASAV_TAB = "globalServerStatsASAVTab" ;
ZaSettings.ALL_UI_COMPONENTS.push({ value: ZaSettings.GLOBAL_STATS_MSG_ASAV_TAB, label: ZaMsg.UI_Comp_globalStatsMsgASAVTab });

ZaSettings.GLOBAL_ADVANCED_STATS_TAB = "globalAdvancedStatsTab" ;
ZaSettings.ALL_UI_COMPONENTS.push({ value: ZaSettings.GLOBAL_ADVANCED_STATS_TAB, label: ZaMsg.UI_Comp_globalAdvancedStatsTab });

//Individual Server Statistics View
ZaSettings.SERVER_STATS_MSG_COUNT_TAB = "perServerStatsMsgCountTab" ;
ZaSettings.ALL_UI_COMPONENTS.push({ value: ZaSettings.SERVER_STATS_MSG_COUNT_TAB, label: ZaMsg.UI_Comp_serverStatsMsgCountTab });

ZaSettings.SERVER_STATS_MSG_VOL_TAB = "perServerStatsMsgVolTab" ;
ZaSettings.ALL_UI_COMPONENTS.push({ value: ZaSettings.SERVER_STATS_MSG_VOL_TAB, label: ZaMsg.UI_Comp_serverStatsMsgVolTab });

ZaSettings.SERVER_STATS_MSG_ASAV_TAB = "perServerStatsASAVTab" ;
ZaSettings.ALL_UI_COMPONENTS.push({ value: ZaSettings.SERVER_STATS_MSG_ASAV_TAB, label: ZaMsg.UI_Comp_serverStatsMsgASAVTab });

ZaSettings.SERVER_STATS_DISK_TAB = "perServerStatsDiskTab" ;
ZaSettings.ALL_UI_COMPONENTS.push({ value: ZaSettings.SERVER_STATS_DISK_TAB, label: ZaMsg.UI_Comp_serverStatsDiskTab });

ZaSettings.SERVER_STATS_SESSION_TAB = "perServerSessionTab" ;
ZaSettings.ALL_UI_COMPONENTS.push({ value: ZaSettings.SERVER_STATS_SESSION_TAB, label: ZaMsg.UI_Comp_serverStatsSessTab });

ZaSettings.SERVER_STATS_QUOTA_TAB = "perServerStatsQuotaTab" ;
ZaSettings.ALL_UI_COMPONENTS.push({ value: ZaSettings.SERVER_STATS_QUOTA_TAB, label: ZaMsg.UI_Comp_serverStatsQuotaTab });
*/
//Distribution list operations
//ZaSettings.DL_CREATE_RIGHT = "createDL";

//Alias operations
//ZaSettings.ALIAS_CREATE_RIGHT = "createAlias";

//Resources operations
//ZaSettings.RESOURCES_CREATE_RIGHT = "createResource";
                                            
ZaSettings.VIEW_RIGHTS = {} ;
ZaSettings.VIEW_RIGHTS [ZaSettings.ACCOUNT_LIST_VIEW] = "adminConsoleAccountRights" ;
ZaSettings.VIEW_RIGHTS [ZaSettings.DL_LIST_VIEW] = "adminConsoleDLRights" ;
ZaSettings.VIEW_RIGHTS [ZaSettings.ALIAS_LIST_VIEW] = "adminConsoleAliasRights" ;
ZaSettings.VIEW_RIGHTS [ZaSettings.RESOURCE_LIST_VIEW] = "adminConsoleResourceRights" ;

ZaSettings.VIEW_RIGHTS [ZaSettings.COS_LIST_VIEW] = "adminConsoleCOSRights" ;
ZaSettings.VIEW_RIGHTS [ZaSettings.DOMAIN_LIST_VIEW] = "adminConsoleDomainRights" ;
ZaSettings.VIEW_RIGHTS [ZaSettings.SERVER_LIST_VIEW] = "adminConsoleServerRights" ;
ZaSettings.VIEW_RIGHTS [ZaSettings.ZIMLET_LIST_VIEW] = "adminConsoleZimletRights" ;

ZaSettings.VIEW_RIGHTS [ZaSettings.GLOBAL_STATUS_VIEW] = "adminConsoleServerStatusRights" ;
ZaSettings.VIEW_RIGHTS [ZaSettings.ADMIN_ZIMLET_LIST_VIEW] = "adminConsoleExtensionRights" ;
ZaSettings.VIEW_RIGHTS [ZaSettings.GLOBAL_CONFIG_VIEW] = "adminConsoleGlobalRights" ;
ZaSettings.VIEW_RIGHTS [ZaSettings.MAILQ_VIEW] = "adminConsoleMailQueueRights" ;

ZaSettings.VIEW_RIGHTS [ZaSettings.SAVE_SEARCH] = "adminConsoleSavedSearchRights" ;


ZaSettings.LICENSE_ENABLED = true;
ZaSettings.ADMIN_ZIMLETS_ENABLED = true;
ZaSettings.SAVE_SEARCH_ENABLED = true ;
ZaSettings.TREE_ENABLED = true;
ZaSettings.CURRENT_APP_ENABLED = true;
ZaSettings.BANNER_ENABLED = true;
ZaSettings.STATUS_ENABLED = true;
ZaSettings.SEARCH_PANEL_ENABLED = true;

ZaSettings.SKIN_LOGOFF_DOM_ID = "skin_container_logoff" ;
ZaSettings.SKIN_HELP_DOM_ID = "skin_container_help" ;
ZaSettings.SKIN_DW_DOM_ID = "skin_container_dw" ;
ZaSettings.SKIN_USER_NAME_ID = "skin_container_username";
ZaSettings.SKIN_LOGO_ID = "skin_container_logo";
ZaSettings.SKIN_TREE_ID = "skin_container_tree";
ZaSettings.SKIN_SEARCH_BUILDER_ID = "skin_container_search_builder";
ZaSettings.SKIN_SEARCH_BUILDER_TB_ID = "skin_container_search_builder_toolbar";
ZaSettings.SKIN_APP_SASH_ID = "skin_container_tree_app_sash";
ZaSettings.SKIN_CURRENT_APP_ID = "skin_container_current_app";
ZaSettings.SKIN_STATUS_ID = "skin_container_status";
ZaSettings.SKIN_SEARCH_PANEL_ID = "skin_container_search";

// initialization for settings: [name, type, data type, default value]
ZaSettings.INIT = new Object();
// IDs FOR HTML COMPONENTS IN THE SKIN
ZaSettings.INIT[ZaSettings.SKIN_APP_MAIN_ID]				= [null, ZaSettings.T_CONFIG, ZaSettings.D_STRING, "skin_container_app_main"];
ZaSettings.INIT[ZaSettings.SKIN_APP_TOP_TOOLBAR_ID]		= [null, ZaSettings.T_CONFIG, ZaSettings.D_STRING, "skin_container_top_toolbar"];
ZaSettings.INIT[ZaSettings.SKIN_CURRENT_APP_ID]			= [null, ZaSettings.T_CONFIG, ZaSettings.D_STRING, ZaSettings.SKIN_CURRENT_APP_ID];
ZaSettings.INIT[ZaSettings.SKIN_LOGO_ID]					= [null, ZaSettings.T_CONFIG, ZaSettings.D_STRING, ZaSettings.SKIN_LOGO_ID];
ZaSettings.INIT[ZaSettings.SKIN_SASH_ID]					= [null, ZaSettings.T_CONFIG, ZaSettings.D_STRING, ZaSettings.SKIN_APP_SASH_ID];
ZaSettings.INIT[ZaSettings.SKIN_SEARCH_BUILDER_ID]		= [null, ZaSettings.T_CONFIG, ZaSettings.D_STRING, ZaSettings.SKIN_SEARCH_BUILDER_ID];
ZaSettings.INIT[ZaSettings.SKIN_SEARCH_BUILDER_TOOLBAR_ID]= [null, ZaSettings.T_CONFIG, ZaSettings.D_STRING, ZaSettings.SKIN_SEARCH_BUILDER_TB_ID];
ZaSettings.INIT[ZaSettings.SKIN_SEARCH_ID]				= [null, ZaSettings.T_CONFIG, ZaSettings.D_STRING, ZaSettings.SKIN_SEARCH_PANEL_ID];
ZaSettings.INIT[ZaSettings.SKIN_SHELL_ID]					= [null, ZaSettings.T_CONFIG, ZaSettings.D_STRING, "skin_outer"];
ZaSettings.INIT[ZaSettings.SKIN_STATUS_ID]				= [null, ZaSettings.T_CONFIG, ZaSettings.D_STRING, ZaSettings.SKIN_STATUS_ID];
ZaSettings.INIT[ZaSettings.SKIN_TREE_ID]					= [null, ZaSettings.T_CONFIG, ZaSettings.D_STRING, ZaSettings.SKIN_TREE_ID];
//ZaSettings.INIT[ZaSettings.SKIN_TREE_FOOTER_ID]			= [null, ZaSettings.T_CONFIG, ZaSettings.D_STRING, "skin_container_tree_footer"];
ZaSettings.INIT[ZaSettings.SKIN_LOGIN_MSG_ID]           = [null, ZaSettings.T_CONFIG, ZaSettings.D_STRING, "skin_container_login_msg"];
//ZaSettings.timeZoneChoices = new XFormChoices(AjxTimezoneData.TIMEZONE_RULES, XFormChoices.OBJECT_LIST, "serverId", "serverId");
//in order to add the "Not Set" label to the timezone choices, we need to normalize it to label value pair
ZaSettings.getTimeZoneChoices = function () {
    if (!ZaSettings._timeZoneChoices) {
        ZaSettings._timeZoneChoices = [{ label: ZaMsg.VALUE_NOT_SET, value: "" }]  ;
        var tz_options_values =  AjxTimezone.getZonePreferencesOptions () ;
        var tz_options_labels = AjxTimezone.getZonePreferences () ;

        for (var i=0; i < tz_options_values.length ; i ++) {
            ZaSettings._timeZoneChoices.push({label: tz_options_labels[i],  value: tz_options_values[i]});
        }
    }
    return ZaSettings._timeZoneChoices;
}

ZaSettings.timeZoneChoices = ZaSettings.getTimeZoneChoices  ;

ZaSettings.INIT[ZaSettings.SKIN_APP_TABS_ID] = [null, ZaSettings.T_CONFIG, ZaSettings.D_STRING, "skin_container_app_tabs"];


//ZaSettings.SKIN_TABS_DOM_ID = "skin_container_app_tabs" ;
//ZaSettings.SKIN_LOGIN_MSG_ID = "skin_td_login_msg" ;

ZaSettings.calendarViewChoinces = [
	{value:"day",label:ZaMsg.CalViewDay},
	{value:"week",label:ZaMsg.CalViewWeek},
	{value:"workWeek",label:ZaMsg.CalViewWorkWeek},
	{value:"month",label:ZaMsg.CalViewMonth},
	{value:"schedule",label:ZaMsg.CalViewSchedule},
	{value:"list",label:ZaMsg.CalViewList}
];

ZaSettings.dayOfWeekChoices = [
	{value:0,label:ZaMsg.Sunday},
	{value:1,label:ZaMsg.Monday},
	{value:2,label:ZaMsg.Tuesday},
	{value:3,label:ZaMsg.Wednesday},
	{value:4,label:ZaMsg.Thursday},
	{value:5,label:ZaMsg.Friday},
	{value:6,label:ZaMsg.Saturday}
];

ZaSettings.apptVisibilityChoices = [
	{value:"public",label:ZaMsg.AptVisibilityPublic},
	{value:"private",label:ZaMsg.AptVisibilityPrivate}
];

ZaSettings.clientTypeChoices = [
	{value:"advanced", label:ZaMsg.clientAdvanced},
	{value:"standard", label:ZaMsg.clientStandard}
];
ZaSettings.mailCharsetChoices = [
	{ value: "Big5" , label: "Big5" } ,
	{ value: "Big5-HKSCS" , label: "Big5-HKSCS" } ,
	{ value: "EUC-JP" , label: "EUC-JP" } ,
	{ value: "EUC-KR" , label: "EUC-KR" } ,
	{ value: "GB18030" , label: "GB18030" } ,
	{ value: "GB2312" , label: "GB2312" } ,
	{ value: "GBK" , label: "GBK" } ,
	{ value: "IBM-Thai" , label: "IBM-Thai" } ,
	{ value: "IBM00858" , label: "IBM00858" } ,
	{ value: "IBM01140" , label: "IBM01140" } ,
	{ value: "IBM01141" , label: "IBM01141" } ,
	{ value: "IBM01142" , label: "IBM01142" } ,
	{ value: "IBM01143" , label: "IBM01143" } ,
	{ value: "IBM01144" , label: "IBM01144" } ,
	{ value: "IBM01145" , label: "IBM01145" } ,
	{ value: "IBM01146" , label: "IBM01146" } ,
	{ value: "IBM01147" , label: "IBM01147" } ,
	{ value: "IBM01148" , label: "IBM01148" } ,
	{ value: "IBM01149" , label: "IBM01149" } ,
	{ value: "IBM037" , label: "IBM037" } ,
	{ value: "IBM1026" , label: "IBM1026" } ,
	{ value: "IBM1047" , label: "IBM1047" } ,
	{ value: "IBM273" , label: "IBM273" } ,
	{ value: "IBM277" , label: "IBM277" } ,
	{ value: "IBM278" , label: "IBM278" } ,
	{ value: "IBM280" , label: "IBM280" } ,
	{ value: "IBM284" , label: "IBM284" } ,
	{ value: "IBM285" , label: "IBM285" } ,
	{ value: "IBM297" , label: "IBM297" } ,
	{ value: "IBM420" , label: "IBM420" } ,
	{ value: "IBM424" , label: "IBM424" } ,
	{ value: "IBM437" , label: "IBM437" } ,
	{ value: "IBM500" , label: "IBM500" } ,
	{ value: "IBM775" , label: "IBM775" } ,
	{ value: "IBM850" , label: "IBM850" } ,
	{ value: "IBM852" , label: "IBM852" } ,
	{ value: "IBM855" , label: "IBM855" } ,
	{ value: "IBM857" , label: "IBM857" } ,
	{ value: "IBM860" , label: "IBM860" } ,
	{ value: "IBM861" , label: "IBM861" } ,
	{ value: "IBM862" , label: "IBM862" } ,
	{ value: "IBM863" , label: "IBM863" } ,
	{ value: "IBM864" , label: "IBM864" } ,
	{ value: "IBM865" , label: "IBM865" } ,
	{ value: "IBM866" , label: "IBM866" } ,
	{ value: "IBM868" , label: "IBM868" } ,
	{ value: "IBM869" , label: "IBM869" } ,
	{ value: "IBM870" , label: "IBM870" } ,
	{ value: "IBM871" , label: "IBM871" } ,
	{ value: "IBM918" , label: "IBM918" } ,
	{ value: "imap-utf-7" , label: "imap-utf-7" } ,
	{ value: "ISO-2022-CN" , label: "ISO-2022-CN" } ,
	{ value: "ISO-2022-JP" , label: "ISO-2022-JP" } ,
	{ value: "ISO-2022-KR" , label: "ISO-2022-KR" } ,
	{ value: "ISO-8859-1" , label: "ISO-8859-1" } ,
	{ value: "ISO-8859-13" , label: "ISO-8859-13" } ,
	{ value: "ISO-8859-15" , label: "ISO-8859-15" } ,
	{ value: "ISO-8859-2" , label: "ISO-8859-2" } ,
	{ value: "ISO-8859-3" , label: "ISO-8859-3" } ,
	{ value: "ISO-8859-4" , label: "ISO-8859-4" } ,
	{ value: "ISO-8859-5" , label: "ISO-8859-5" } ,
	{ value: "ISO-8859-6" , label: "ISO-8859-6" } ,
	{ value: "ISO-8859-7" , label: "ISO-8859-7" } ,
	{ value: "ISO-8859-8" , label: "ISO-8859-8" } ,
	{ value: "ISO-8859-9" , label: "ISO-8859-9" } ,
	{ value: "JIS_X0201" , label: "JIS_X0201" } ,
	{ value: "JIS_X0212-1990" , label: "JIS_X0212-1990" } ,
	{ value: "KOI8-R" , label: "KOI8-R" } ,
	{ value: "macintosh" , label: "macintosh" } ,
	{ value: "macintosh_ce" , label: "macintosh_ce" } ,
	{ value: "Shift_JIS" , label: "Shift_JIS" } ,
	{ value: "TIS-620" , label: "TIS-620" } ,
	{ value: "US-ASCII" , label: "US-ASCII" } ,
	{ value: "UTF-16" , label: "UTF-16" } ,
	{ value: "UTF-16BE" , label: "UTF-16BE" } ,
	{ value: "UTF-16LE" , label: "UTF-16LE" } ,
	{ value: "utf-7" , label: "utf-7" } ,
	{ value: "UTF-8" , label: "UTF-8" } ,
	{ value: "windows-1250" , label: "windows-1250" } ,
	{ value: "windows-1251" , label: "windows-1251" } ,
	{ value: "windows-1252" , label: "windows-1252" } ,
	{ value: "windows-1253" , label: "windows-1253" } ,
	{ value: "windows-1254" , label: "windows-1254" } ,
	{ value: "windows-1255" , label: "windows-1255" } ,
	{ value: "windows-1256" , label: "windows-1256" } ,
	{ value: "windows-1257" , label: "windows-1257" } ,
	{ value: "windows-1258" , label: "windows-1258" } ,
	{ value: "windows-31j" , label: "windows-31j" } ,
	{ value: "x-Big5-Solaris" , label: "x-Big5-Solaris" } ,
	{ value: "x-euc-jp-linux" , label: "x-euc-jp-linux" } ,
	{ value: "x-EUC-TW" , label: "x-EUC-TW" } ,
	{ value: "x-eucJP-Open" , label: "x-eucJP-Open" } ,
	{ value: "x-IBM1006" , label: "x-IBM1006" } ,
	{ value: "x-IBM1025" , label: "x-IBM1025" } ,
	{ value: "x-IBM1046" , label: "x-IBM1046" } ,
	{ value: "x-IBM1097" , label: "x-IBM1097" } ,
	{ value: "x-IBM1098" , label: "x-IBM1098" } ,
	{ value: "x-IBM1112" , label: "x-IBM1112" } ,
	{ value: "x-IBM1122" , label: "x-IBM1122" } ,
	{ value: "x-IBM1123" , label: "x-IBM1123" } ,
	{ value: "x-IBM1124" , label: "x-IBM1124" } ,
	{ value: "x-IBM1381" , label: "x-IBM1381" } ,
	{ value: "x-IBM1383" , label: "x-IBM1383" } ,
	{ value: "x-IBM33722" , label: "x-IBM33722" } ,
	{ value: "x-IBM737" , label: "x-IBM737" } ,
	{ value: "x-IBM834" , label: "x-IBM834" } ,
	{ value: "x-IBM856" , label: "x-IBM856" } ,
	{ value: "x-IBM874" , label: "x-IBM874" } ,
	{ value: "x-IBM875" , label: "x-IBM875" } ,
	{ value: "x-IBM921" , label: "x-IBM921" } ,
	{ value: "x-IBM922" , label: "x-IBM922" } ,
	{ value: "x-IBM930" , label: "x-IBM930" } ,
	{ value: "x-IBM933" , label: "x-IBM933" } ,
	{ value: "x-IBM935" , label: "x-IBM935" } ,
	{ value: "x-IBM937" , label: "x-IBM937" } ,
	{ value: "x-IBM939" , label: "x-IBM939" } ,
	{ value: "x-IBM942" , label: "x-IBM942" } ,
	{ value: "x-IBM942C" , label: "x-IBM942C" } ,
	{ value: "x-IBM943" , label: "x-IBM943" } ,
	{ value: "x-IBM943C" , label: "x-IBM943C" } ,
	{ value: "x-IBM948" , label: "x-IBM948" } ,
	{ value: "x-IBM949" , label: "x-IBM949" } ,
	{ value: "x-IBM949C" , label: "x-IBM949C" } ,
	{ value: "x-IBM950" , label: "x-IBM950" } ,
	{ value: "x-IBM964" , label: "x-IBM964" } ,
	{ value: "x-IBM970" , label: "x-IBM970" } ,
	{ value: "x-ISCII91" , label: "x-ISCII91" } ,
	{ value: "x-ISO-2022-CN-CNS" , label: "x-ISO-2022-CN-CNS" } ,
	{ value: "x-ISO-2022-CN-GB" , label: "x-ISO-2022-CN-GB" } ,
	{ value: "x-iso-8859-11" , label: "x-iso-8859-11" } ,
	{ value: "x-JIS0208" , label: "x-JIS0208" } ,
	{ value: "x-JISAutoDetect" , label: "x-JISAutoDetect" } ,
	{ value: "x-Johab" , label: "x-Johab" } ,
	{ value: "x-MacArabic" , label: "x-MacArabic" } ,
	{ value: "x-MacCentralEurope" , label: "x-MacCentralEurope" } ,
	{ value: "x-MacCroatian" , label: "x-MacCroatian" } ,
	{ value: "x-MacCyrillic" , label: "x-MacCyrillic" } ,
	{ value: "x-MacDingbat" , label: "x-MacDingbat" } ,
	{ value: "x-MacGreek" , label: "x-MacGreek" } ,
	{ value: "x-MacHebrew" , label: "x-MacHebrew" } ,
	{ value: "x-MacIceland" , label: "x-MacIceland" } ,
	{ value: "x-MacRoman" , label: "x-MacRoman" } ,
	{ value: "x-MacRomania" , label: "x-MacRomania" } ,
	{ value: "x-MacSymbol" , label: "x-MacSymbol" } ,
	{ value: "x-MacThai" , label: "x-MacThai" } ,
	{ value: "x-MacTurkish" , label: "x-MacTurkish" } ,
	{ value: "x-MacUkraine" , label: "x-MacUkraine" } ,
	{ value: "x-MS950-HKSCS" , label: "x-MS950-HKSCS" } ,
	{ value: "x-mswin-936" , label: "x-mswin-936" } ,
	{ value: "x-PCK" , label: "x-PCK" } ,
	{ value: "x-windows-50220" , label: "x-windows-50220" } ,
	{ value: "x-windows-50221" , label: "x-windows-50221" } ,
	{ value: "x-windows-874" , label: "x-windows-874" } ,
	{ value: "x-windows-949" , label: "x-windows-949" } ,
	{ value: "x-windows-950" , label: "x-windows-950" } ,                                   
	{ value: "x-windows-iso2022jp" , label: "x-windows-iso2022jp" } 
] ;

ZaSettings.getLocaleChoices = function () {

    if (! ZaSettings.localeChoices) {
       //getAllLocalesRequest
//        var soapDoc = AjxSoapDoc.create("GetAllLocalesRequest", ZaZimbraAdmin.URN, null);
        var soapDoc = AjxSoapDoc.create("GetAvailableLocalesRequest", "urn:zimbraAccount", null);
        var params = {};
        params.soapDoc = soapDoc;
        var reqMgrParams = {
                controller: (ZaApp.getInstance() ? ZaApp.getInstance().getCurrentController(): null ),
                busyMsg : ZaMsg.BUSY_GET_LOCALE
            }
//        var resp = ZaRequestMgr.invoke(params, reqMgrParams).Body.GetAllLocalesResponse;
        var resp = ZaRequestMgr.invoke(params, reqMgrParams).Body.GetAvailableLocalesResponse;
        var locales = resp.locale ;            
        ZaSettings.localeChoices = [] ;

        for (var i=0; i < locales.length; i ++) {
            ZaSettings.localeChoices.push({
                value: locales[i].id,
                label: locales[i].name
            });
        }
    }

    return ZaSettings.localeChoices ;
}

ZaSettings.isNetworkVersion = function () {
    return (ZaSettings.IS_ZCS_NETWORK_VERSION || false );
}
