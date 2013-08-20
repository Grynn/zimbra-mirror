/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2010, 2011, 2012, 2013 Zimbra Software, LLC.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.4 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 */

/**
 * @overview
 * This file contains ids.
 * 
 */

/**
 * Constructor
 * 
 * @author Conrad Damon
 */
ZaId = function() {};


/* Element IDs, and functions to generate them */


// widget types (used to prefix IDs)
/**
 * Defines the "generic element" widget type prefix.
 */
ZaId.WIDGET				= "z";			// generic element
/**
 * Defines the "view within content area" widget type prefix.
 */
ZaId.WIDGET_VIEW			= "zv";			// view within content area
/**
 * Defines the "toolbar" widget type prefix.
 */
ZaId.WIDGET_TOOLBAR			= "ztb";		// toolbar
/**
 * Defines the "button" widget type prefix.
 */
ZaId.WIDGET_BUTTON			= "zb";			// button
/**
 * Defines the "text input or textarea" widget type prefix.
 */
ZaId.WIDGET_INPUT			= "zi";			// text input or textarea
/**
 * Defines the "menu" widget type prefix.
 */
ZaId.WIDGET_MENU			= "zm";			// menu
/**
 * Defines the "menu item" widget type prefix.
 */
ZaId.WIDGET_MENU_ITEM			= "zmi";		// menu item
/**
 * Defines the "dropdown select" widget type prefix.
 */
ZaId.WIDGET_SELECT			= "zs";			// dropdown select
/**
 * Defines the "collection of overview" widget type prefix.
 */
ZaId.WIDGET_OVERVIEW_CNTR		= "zovc";		// collection of overviews
/**
 * Defines the "collection of tree views" widget type prefix.
 */
ZaId.WIDGET_OVERVIEW			= "zov";		// collection of tree views
/**
 * Defines the "tree view" widget type prefix.
 */
ZaId.WIDGET_TREE			= "zt";			// tree view
/**
 * Defines the "root tree item" widget type prefix.
 */
ZaId.WIDGET_TREE_ITEM_HDR		= "ztih";		// root tree item
/**
 * Defines the "tree item (node)" widget type prefix.
 */
ZaId.WIDGET_TREE_ITEM			= "zti";		// tree item (node)
/**
 * Defines the "tab button" widget type prefix.
 */
ZaId.WIDGET_TAB				= "ztab";		// tab button
/**
 *  * Defines the "tab view" widget type prefix.
 *   */
ZaId.WIDGET_TAB_VIEW                    = "ztabv";               // tab view

/**
 *  * Defines the "dialog" widget type prefix.
 *   */
ZaId.WIDGET_DLG                         = "zdlg";               //  dialog

/**
 *  * Defines the "dialog view " widget type prefix.
 *   */
ZaId.WIDGET_DLG_VIEW                    = "zdlgv";               // dialog view

/**
 * Defines the "dialog button" widget type prefix.
 */
ZaId.WIDGET_DLG_BUTTON			= "zdlgb";		//  dialog button

/**
 * Defines the "search panel view" widget type prefix.
 */
ZaId.WIDGET_SEARCH_VIEW			= "zschv";		// view within search panel



/* Preset IDs */


/*
 * Container IDs defined by the skin.
 * 
 * These must match what's in skin.html. They are used by ZmAppViewMgr to visually
 * match components to the containers in which they should be positioned. 
 */
ZaId.SKIN_APP_BOTTOM_TOOLBAR			= "skin_container_app_bottom_toolbar";
ZaId.SKIN_APP_CHOOSER				= "skin_container_app_chooser";
ZaId.SKIN_APP_MAIN_FULL				= "skin_container_app_main_full";
ZaId.SKIN_APP_MAIN				= "skin_container_app_main";
ZaId.SKIN_APP_MAIN_ROW_FULL			= "skin_tr_main_full";
ZaId.SKIN_APP_MAIN_ROW				= "skin_tr_main";
ZaId.SKIN_APP_TOP_TOOLBAR			= "skin_container_app_top_toolbar";
ZaId.SKIN_LINKS					= "skin_container_links";
ZaId.SKIN_LOGO					= "skin_container_logo";
ZaId.SKIN_QUOTA_INFO				= "skin_container_quota";
ZaId.SKIN_SASH					= "skin_container_tree_app_sash";
ZaId.SKIN_SEARCH_BUILDER			= "skin_container_search_builder";
ZaId.SKIN_SEARCH_BUILDER_TOOLBAR		= "skin_container_search_builder_toolbar";
ZaId.SKIN_SEARCH_BUILDER_TR			= "skin_tr_search_builder";
ZaId.SKIN_SEARCH				= "skin_container_search";
ZaId.SKIN_PEOPLE_SEARCH				= "skin_container_people_search";
ZaId.SKIN_SHELL					= "skin_outer";
ZaId.SKIN_SPACING_SEARCH			= "skin_spacing_search";
ZaId.SKIN_SPLASH_SCREEN				= "skin_container_splash_screen";
ZaId.SKIN_STATUS				= "skin_container_status";
ZaId.SKIN_STATUS_ROW				= "skin_tr_status";
ZaId.SKIN_TREE_FOOTER				= "skin_container_tree_footer";
ZaId.SKIN_TREE					= "skin_container_tree";
ZaId.SKIN_USER_INFO				= "skin_container_username";
ZaId.SKIN_TASKBAR				= "skin_container_taskbar";
ZaId.SKIN_FOOTER				= "skin_footer";
ZaId.SKIN_AD					= "skin_adsrvc";


/* Literal IDs */


/*
 * Top-level components. These are elements that are placed directly into skin containers.
 */
ZaId.SHELL					= "z_shell";			// the main shell
ZaId.LOADING_VIEW				= "z_loading";			// "Loading..." view
ZaId.MAIN_SASH					= "z_sash";				// sash between overview and content
ZaId.BANNER					= "z_banner";			// logo (at upper left by default)
ZaId.SEARCH_TOOLBAR				= "ztb_search";			// search toolbar
ZaId.PEOPLE_SEARCH_TOOLBAR			= "ztb_people_search";	// people search toolbar
ZaId.USER_NAME					= "z_userName";			// account name
ZaId.USER_QUOTA					= "z_userQuota";		// quota
ZaId.PRESENCE					= "z_presence";			// presence
ZaId.TASKBAR					= "z_taskbar";			// taskbar
ZaId.NEW_FOLDER_BUTTON				= "zb_newFolder";		// New Folder button on current app toolbar
ZaId.STATUS_VIEW				= "z_status";			// status view (shows toast)
ZaId.TOAST					= "z_toast";			// toast
ZaId.APP_CHOOSER				= "ztb_appChooser";		// app chooser toolbar
/* New UI*/
ZaId.CURRENT_APP_BAR         = "zb_currentApp";

/* Functions for generating IDs */


/**
 * Generates the ID for a toolbar.
 * 
 * <p>
 * Examples: <code>ztb|CLV ztb|TV|Nav ztb|CV|Inv</code>
 * </p>
 * 
 * @param 	{String}	context	the toolbar context (ID of owning view)
 * @param	{constant}	tbType	the type of toolbar (for example, invite or nav)
 * @return	{String}	the id
 */
ZaId.getToolbarId =
function(context, tbType) {
	return DwtId._makeId(ZaId.WIDGET_TOOLBAR, context, tbType);
};

/**
 * Generates the ID for a button. Intended for use with the top toolbar, nav toolbar,
 * and invite toolbar.
 * 
 * @param 	{String}	context	the toolbar context (ID of owning view)
 * @param 	{constant}	op	the button operation
 * @param 	{constant}	tbType	the type of toolbar (eg invite or nav)
 * @return	{String}	the id
 */
ZaId.getButtonId =
function(context, op, tbType) {
	return DwtId._makeId(ZaId.WIDGET_BUTTON, context, tbType, op);
};

/**
 * Generates the ID for an action menu.
 * 
 * @param 	{String}	context		the menu context (eg ID of owning view, or app)
 * @param 	{constant}	menuType	the type of menu (eg participant)
 * @return	{String}	the id
 */
ZaId.getMenuId =
function(context, menuType) {
	return DwtId._makeId(ZaId.WIDGET_MENU, context, menuType);
};

/**
 * Generates the ID for a menu item in an action menu.
 * 
 * @param 	{String}	context		the menu context
 * @param 	{constant}	op		the menu operation
 * @param 	{constant}	menuType	the type of menu (eg participant)
 * @return	{String}	the id
 */
ZaId.getMenuItemId =
function(context, op, menuType) {
	return DwtId._makeId(ZaId.WIDGET_MENU_ITEM, context, menuType, op);
};

/**
 * Generates the ID for an overview container.
 *
 * @param 	{String}	overviewContainerId	the overview container ID
 * @return	{String}	the id
 */
ZaId.getOverviewContainerId =
function(overviewContainerId) {
	return DwtId._makeId(ZaId.WIDGET_OVERVIEW_CNTR, overviewContainerId);
};

/**
 * Generates the ID for an overview.
 * 
 * @param 	{String}	overviewId	the overview ID
 * @return	{String}	the id
 */
ZaId.getOverviewId =
function(overviewId) {
	return DwtId._makeId(ZaId.WIDGET_OVERVIEW, overviewId);
};

/**
 * Generates the ID for a tree within an overview.
 * 
 * @param 	{String}	overviewId	the overview ID
 * @param 	{String}	orgType 	the organizer type (see <code>ZaId.ORG_</code> constants)
 * @return	{String}	the id
 */
ZaId.getTreeId =
function(overviewId, orgType) {
	return DwtId._makeId(ZaId.WIDGET_TREE, overviewId, orgType);
};

/**
 * Generates a tree item ID 
 * 
 * @param 	{String}	overviewId	the unique ID for overview
 * @param 	{ZmOrganizer}	organizerId	the ID of the data object backing tree item
 * @param 	{constant}	type		the organizer type (for headers only)
 * @return	{String}	the id
 */
ZaId.getTreeItemId =
function(overviewId, organizerId, type, indexNo) {
	//if (!organizerId && !type) { return; }
	if (type) {
		return DwtId._makeId(ZaId.WIDGET_TREE_ITEM_HDR, overviewId, organizerId, indexNo);
	} else {
		return DwtId._makeId(ZaId.WIDGET_TREE_ITEM, overviewId, organizerId, indexNo);
	}
};

/**
 * Generates an ID for a view 
 * 
 * @param 	{constant}	viewId		the view identifier 
 * @param 	{constant}	component	the component identifier 
 * @param 	{constant}	context		the ID of owning view
 * @return	{String}	the id
 */
ZaId.getViewId =
function(viewId, component, context) {
	var id = DwtId._makeId(ZaId.WIDGET_VIEW, context, viewId);
	return component ? [id, component].join("") : id;
};

/**
 * Generates an ID for a dialog
 * 
 * @param       {constant}      component       the component identifier
 * @param       {constant}      context         the ID of owning view
 * @return      {String}        the id
 */
ZaId.getDialogId =
function(component, context) {
	return DwtId._makeId(ZaId.WIDGET_DLG, component, context);
};

/**
 * Generates an ID for a dialog view
 * 
 * @param       {constant}      component       the component identifier
 * @param       {constant}      context         the ID of owning view
 * @return      {String}        the id
 */
ZaId.getDialogViewId =
function(component, context) {
	return DwtId._makeId(ZaId.WIDGET_DLG_VIEW, component, context);
};


/**
 * Generates an ID for a dialog button
 * 
 * @param       {constant}      component       the component identifier
 * @param       {constant}      context         the ID of owning view
 * @return      {String}        the id
 */
ZaId.getDialogButtonId =
function(component, context) {
	return DwtId._makeId(ZaId.WIDGET_DLG_BUTTON, component, context);
};



/**
 * Generates an ID for the compose view
 * 
 * @param 	{constant}	component	component identifier 
 * @return	{String}	the id
 */
ZaId.getComposeViewId =
function(component) {
	var id = DwtId._makeId(ZaId.WIDGET, ZaId.COMPOSE_VIEW);
	return component ? [id, component].join("") : id;
};

/**
 * Generates an ID for a tab (actually the tab button in the tab bar).
 * 
 * @param 	{constant}	context		the owning view identifier 
 * @param 	{String}	tabName		[string]name of tab
 * @return	{String}	the id
 */
ZaId.getTabId =
function(context, tabName) {
	return DwtId._makeId(ZaId.WIDGET_TAB, context, tabName);
};


/**
 * Generates an ID for a tab view.
 * 
 * @param 	{constant}	context		the owning view identifier 
 * @param 	{String}	tabName		[string]name of tab
 * @return	{String}	the id
 */
ZaId.getTabViewId =
function(context, tabName) {
	return DwtId._makeId(ZaId.WIDGET_TAB_VIEW, context, tabName);
};


/**
 * Generates an ID for a pref page tab.
 *
 * @param	{String}	tabKey		the tab key
 * @return	{String}	the id
 */
ZaId.getPrefPageId = function(tabKey) {
	return "PREF_PAGE_"+tabKey;
};


/**
 * Generates an ID for a search view.
 * 
 * @param 	{constant}	context		the owning view identifier 
 * @param 	{String}	tabName		[string]name of tab
 * @return	{String}	the id
 */
ZaId.getSearchViewId =
function(context, tabName) {
	return DwtId._makeId(ZaId.WIDGET_SEARCH_VIEW, context, tabName);
};



/*
 * 
 * Gettings IDs for different areas of Admin console
 * 
 */

/*
 * -----------
 *    App 
 * -----------
 * 
 */

// context
ZaId.APP	= "App";

/*
 * ---------
 * Overviews
 * ---------
 * 
 */


/* address */
ZaId.TREEITEM_ACCOUNT		= "ACCOUNT";
ZaId.TREEITEM_ALIASES		= "ALIASES";
ZaId.TREEITEM_DL		= "DL";
ZaId.TREEITEM_RESOURCES		= "RESOURCES";
/* configuration */
ZaId.TREEITEM_COS		= "COS";
ZaId.TREEITEM_DOMAINS		= "DOMAINS";
ZaId.TREEITEM_SERVERS		= "SERVERS";
ZaId.TREEITEM_ZIMLETS		= "ZIMLETS";
ZaId.TREEITEM_ADMINEXT		= "AEMINEXT";
ZaId.TREEITEM_GSET		= "GSET";
ZaId.TREEITEM_RIGHTS		= "RIGHTS";
ZaId.TREEITEM_GACL		= "GACL";
/* monitoring */
ZaId.TREEITEM_SSTATUS		= "SSTATUS";
ZaId.TREEITEM_SSTATIS		= "SSTATIS";
/* tools */
ZaId.TREEITEM_MQUEUE		= "MQUEUE";
ZaId.TREEITEM_BACKUPS		= "BACKUPS";
ZaId.TREEITEM_DATAINPUT		= "DATAINPUT";
ZaId.TREEITEM_CERT		= "CERT";
ZaId.TREEITEM_SEARCHMAIL	= "SEARCHMAIL";
/* search */



/*
 * ----------
 *  panel 
 * ----------
 *
 */

// Overview panel
ZaId.PANEL_APP			= "AppAdmin";
ZaId.PANEL_ADDRESS		= "ADDRESS";
ZaId.PANEL_CONFIGURATION	= "CONFIGURATION";
ZaId.PANEL_MONITORING		= "MONITORING";
ZaId.PANEL_TOOLS		= "TOOLS";
ZaId.PANEL_SEARCHS		= "SEARCHOPTS";


// search panel
ZaId.PANEL_APPSEARCH		= "AppSearch";
ZaId.SEARCH_VIEW		= "SEARCHV";
ZaId.SEARCH_QUERY		= "SEARCH_QUERY";



/* 
 * ------------
 * List  View 
 * ------------
 *
 * */

ZaId.VIEW_ACCTLIST              = "ACLV";
ZaId.VIEW_ALASLIST		= "ALLV";
ZaId.VIEW_DLLIST		= "DLLV";  // redundant
ZaId.VIEW_RESLIST		= "RESLV";

ZaId.VIEW_COSLIST		= "COSLV";
ZaId.VIEW_DMLIST		= "DMLV";
ZaId.VIEW_SERLIST		= "SERLV";
ZaId.VIEW_ZIMLIST		= "ZIMLV";
ZaId.VIEW_AELIST		= "AELV";
ZaId.VIEW_GSETLIST		= "GSLV";  // redundant
ZaId.VIEW_RIGHTLIST		= "RLV";
ZaId.VIEW_GACLIST		= "ACLV";

ZaId.VIEW_STATUSLIST		= "STALV"; 
ZaId.VIEW_STATISLIST		= "STSLV";

ZaId.VIEW_MQLIST		= "MQLV";  // redundant
ZaId.VIEW_BKLIST		= "BKLV";
ZaId.VIEW_DILIST		= "DILV";
ZaId.VIEW_CRTLIST		= "CRTLV";
ZaId.VIEW_SMLIST		= "SMLV";
ZaId.VIEW_SCHLIST		= "SCHLV";

ZaId.VIEW_MTALIST		= "MTALV";

ZaId.VIEW_MEMLIST		= "MEMLV";  // memberOf list view

/* View */
ZaId.VIEW_ACCT              	= "ACCTV";
ZaId.VIEW_ALAS              	= "ALASV";
ZaId.VIEW_DL                	= "DLV";
ZaId.VIEW_RES               	= "RESV";

ZaId.VIEW_COS               	= "COSV";
ZaId.VIEW_DOMAIN                = "DMV";
ZaId.VIEW_SERVER               	= "SERV";
ZaId.VIEW_ZIMLET               	= "ZIMV";
ZaId.VIEW_ADEXT                	= "AEV";
ZaId.VIEW_GSET              	= "GSV";
ZaId.VIEW_RIGHT             	= "RTV";
ZaId.VIEW_GACL               	= "GACV";

ZaId.VIEW_STATUS            	= "STAV";
ZaId.VIEW_STATIS            	= "STSV";

ZaId.VIEW_MQ                	= "MQV";  //redundant
ZaId.VIEW_BK                	= "BKV";
ZaId.VIEW_DI                	= "DIV";
ZaId.VIEW_CRT               	= "CRTV";
ZaId.VIEW_SM                	= "SMV";
ZaId.VIEW_SCH               	= "SCHV";

ZaId.VIEW_MTA			= "MTAV";

ZaId.VIEW_HOME          = "HOMEV";
/* Operation  */
ZaId.OP_NEW_ACCT		= "NEW_ACCT";
ZaId.OP_EDIT_ACCT		= "EDIT_ACCT";
ZaId.OP_DEL_ACCT		= "DEL_ACCT";
ZaId.OP_CHD_ACCTPWD		= "CHD_ACTPWD";

/* click type */
ZaId.OP_CLICK_LEFT		= "LEFT_CLICK";
ZaId.OP_CLICK_RIGHT		= "RIGHT_CLICK";
ZaId.OP_CLICK_DBLEFT 		= "DBLEFT_CLICK";


/* Menu type */
ZaId.MENU_DROP			= "MENU_DROP";
ZaId.MENU_POP			= "MENU_POP";


/* tab */
ZaId.TAB_GROUP			= "TAB_GROUP";

ZaId.TAB_MAIN			= "MAIN_TAB";
ZaId.TAB_ACCT_MANAGE		= "ACCT_MANAGE";
ZaId.TAB_ACCT_EDIT		= "ACCT_EDIT";
ZaId.TAB_ALIAS_MANAGE           = "ALIAS_MANAGE";
ZaId.TAB_ALIAS_EDIT             = "ALIAS_EDIT";
ZaId.TAB_DL_MANAGE            	= "DL_MANAGE";
ZaId.TAB_DL_EDIT              	= "DL_EDIT";
ZaId.TAB_RES_MANAGE            	= "RES_MANAGE";
ZaId.TAB_RES_EDIT              	= "RES_EDIT";

ZaId.TAB_COS_MANAGE		= "COS_MANAGE";
ZaId.TAB_COS_EDIT		= "COS_EDIT";
ZaId.TAB_DOMAIN_MANAGE          = "DOMAIN_MANAGE";
ZaId.TAB_DOMAIN_EDIT            = "DOAMIN_EDIT";
ZaId.TAB_SERVER_MANAGE          = "SERVER_MANAGE";
ZaId.TAB_SERVER_EDIT            = "SERVER_EDIT";
ZaId.TAB_ZIM_MANAGE             = "ZIMLET_MANAGE";
ZaId.TAB_ZIM_EDIT               = "ZIMLET_EDIT";
ZaId.TAB_AE_MANAGE              = "ADMEXT_MANAGE";
ZaId.TAB_AE_EDIT                = "ADMEXT_EDIT";
ZaId.TAB_STATUS_MANAGE          = "STATUS_MANAGE";
ZaId.TAB_STATUS_EDIT            = "STATUS_EDIT";
ZaId.TAB_MTX_MANAGE             = "MTX_MANAGE";
ZaId.TAB_MTX_EDIT               = "MTX_EDIT";
ZaId.TAB_SEARCH_MANAGE          = "SEARCH_MANAGE";
ZaId.TAB_SEARCH_EDIT            = "SEARCH_EDIT";
ZaId.TAB_GSET_EDIT              = "GSET_EDIT";

ZaId.TAB_DOWNLOADS		= "DOWNLOADS";
ZaId.TAB_VIEWRIGHTS		= "VIEW_RIGHTS";

ZaId.TAB_UNDEF			= "UNDEFINE";


/* tab view */
/*
ZaId.TABV_ACCT_EDIT              = "V_ACCT_EDIT";
ZaId.TABV_DL_EDIT                = "V_DL_EDIT";
ZaId.TABV_RES_EDIT               = "V_RES_EDIT";
ZaId.TABV_COS_EDIT               = "V_COS_EDIT";
ZaId.TABV_DOMAIN_EDIT            = "V_DOAMIN_EDIT";
ZaId.TABV_SERVER_EDIT            = "V_SERVER_EDIT";
ZaId.TABV_ZIM_EDIT               = "V_ZIMLET_EDIT";
ZaId.TABV_AE_EDIT                = "V_ADMEXT_EDIT";
ZaId.TABV_STATUS_EDIT            = "V_STATUS_EDIT";
ZaId.TABV_MTX_EDIT               = "V_MTX_EDIT";
ZaId.TABV_GSET_EDIT              = "V_GSET_EDIT";
*/



/* Dialog */
ZaId.DLG_NEW_ACCT		= "NEW_ACCT";
ZaId.DLG_NEW_ALIAS              = "NEW_ALIAS";
ZaId.DLG_EDIT_ALIAS             = "EDIT_ALIAS";
ZaId.DLG_EDIT_SIGNATURE         = "EDIT_SIGNATURE";
ZaId.DLG_NEW_RES               	= "NEW_RES";
ZaId.DLG_NEW_ADMIN              = "NEW_ADMIN";
ZaId.DLG_NEW_DOMAIN             = "NEW_DOMAIN";
ZaId.DLG_ZIM_DEPLOY             = "ZIM_DEPLOY";
ZaId.DLG_AUTH_CONFIG            = "AUTH_CONFIG";
ZaId.DLG_AUTPROV_CONFIG         = "AUTOPROV_CONFIG";
ZaId.DLG_AUTPROV_MANUAL         = "AUTOPROV_MANUAL";
ZaId.DLG_AUTPROV_MANUAL_PWD     = "AUTOPROV_MANUAL_PWD";
ZaId.DLG_AUTH_SPNEGO            = "AUTH_SPNEGO";
ZaId.DLG_AUTH_SPNEGO_UA         = "AUTH_SPNEGO_UA";
ZaId.DLG_NEW_DL                 = "NEW_DL";
ZaId.DLG_NEW_COS                = "NEW_COS";
ZaId.FLUSH_SERVER_CACHE			= "FLUSH_SERVER_CACHE";

ZaId.DLG_MSG			= "MSG";
ZaId.DLG_ERR			= "ERR";


ZaId.DLG_UNDEF			= "UNDEFINE";

/* controller */

ZaId.CTR_GLOBAL			= "GLOBAL_"
ZaId.CTR_PREFIX			= "CTR_";

// ZaId.CTR_PREFIX should be used like the follows:
// e.g.
// ZaId.CTR_VIEW_ACCT = ZaId.CTR_PREFIX + ZaId.VIEW_ACCT
// ZaId.CTR_VIEW_ACCTLIST = ZaId.CTR_PREFIX + ZaId.VIEW_ACCTLIST 

ZaId.CTR_UNDEF			= "CTR_UNDEFINE";
