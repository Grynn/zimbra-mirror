/*
 * ***** BEGIN LICENSE BLOCK *****
 * Version: ZPL 1.1
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.1 ("License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.zimbra.com/license
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See
 * the License for the specific language governing rights and limitations
 * under the License.
 * 
 * The Original Code is: Zimbra Collaboration Suite.
 * 
 * The Initial Developer of the Original Code is Zimbra, Inc.
 * Portions created by Zimbra are Copyright (C) 2005 Zimbra, Inc.
 * All Rights Reserved.
 * 
 * Contributor(s):
 * 
 * ***** END LICENSE BLOCK *****
 */

function ZaSettings() {
}

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

// initialization for settings: [name, type, data type, default value]
ZaSettings.INIT = new Object();
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

//CONSTANTS FOR ROLE-BASED ACCESS
ZaSettings.STATUS_ENABLED= true;
ZaSettings.STATS_ENABLED= true;
ZaSettings.ACCOUNTS_ENABLED= true;
ZaSettings.COSES_ENABLED= true;
ZaSettings.DOMAINS_ENABLED= true;
ZaSettings.SERVERS_ENABLED= true;
ZaSettings.GLOBAL_ENABLED= true;

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

//ZaSettings.INIT[ZaSettings.SKIN_USER_INFO_ID]				= [null, ZaSettings.T_CONFIG, ZaSettings.D_STRING, "skin_container_links"];
