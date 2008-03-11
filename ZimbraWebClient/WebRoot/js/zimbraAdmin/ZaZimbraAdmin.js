/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2004, 2005, 2006, 2007 Zimbra, Inc.
 * 
 * The contents of this file are subject to the Yahoo! Public License
 * Version 1.0 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * 
 * ***** END LICENSE BLOCK *****
 */

/**
* Creates a controller to run ZimbraAdmin. Do not call directly, instead use the run()
* factory method.
* @constructor ZimbraAdmin
* @param appCtx
* @class ZimbraAdmin
* This class is responsible for bootstrapping the ZimbraAdmin application.
*/
ZaZimbraAdmin = function(appCtxt) {
	ZaZimbraAdmin._instance = this;
	ZaController.call(this, appCtxt, null, null,"ZaZimbraAdmin");

	ZaZimbraAdmin.showSplash(this._shell);
	
	appCtxt.setAppController(this);

		
	// handles to various apps
	this._appFactory = new Object();
	this._appFactory[ZaZimbraAdmin.ADMIN_APP] = ZaApp;
 
 	this.startup();

    this.aboutDialog = new ZaAboutDialog(this._shell,null,ZaMsg.about_title);
}

ZaZimbraAdmin.prototype = new ZaController;
ZaZimbraAdmin.prototype.constructor = ZaZimbraAdmin;
ZaZimbraAdmin._instance = null;

ZaZimbraAdmin.ADMIN_APP = "admin";
ZaZimbraAdmin.currentUserName = "" ;
ZaZimbraAdmin.URN = "urn:zimbraAdmin";
ZaZimbraAdmin.VIEW_INDEX = 0;

ZaZimbraAdmin._ADDRESSES = ZaZimbraAdmin.VIEW_INDEX++;
ZaZimbraAdmin._SEARCHES = ZaZimbraAdmin.VIEW_INDEX++;
ZaZimbraAdmin._ACCOUNTS_LIST_VIEW = ZaZimbraAdmin.VIEW_INDEX++;
ZaZimbraAdmin._ALIASES_LIST_VIEW = ZaZimbraAdmin.VIEW_INDEX++;
ZaZimbraAdmin._DISTRIBUTION_LISTS_LIST_VIEW = ZaZimbraAdmin.VIEW_INDEX++;
ZaZimbraAdmin._SYS_CONFIG = ZaZimbraAdmin.VIEW_INDEX++;
ZaZimbraAdmin._GLOBAL_SETTINGS = ZaZimbraAdmin.VIEW_INDEX++;
ZaZimbraAdmin._SERVERS_LIST_VIEW = ZaZimbraAdmin.VIEW_INDEX++;
ZaZimbraAdmin._DOMAINS_LIST_VIEW = ZaZimbraAdmin.VIEW_INDEX++;
ZaZimbraAdmin._COS_LIST_VIEW = ZaZimbraAdmin.VIEW_INDEX++;
ZaZimbraAdmin._MONITORING = ZaZimbraAdmin.VIEW_INDEX++;
ZaZimbraAdmin._STATUS = ZaZimbraAdmin.VIEW_INDEX++;
ZaZimbraAdmin._STATISTICS = ZaZimbraAdmin.VIEW_INDEX++;
ZaZimbraAdmin._STATISTICS_BY_SERVER = ZaZimbraAdmin.VIEW_INDEX++;
ZaZimbraAdmin._SEARCH_LIST_VIEW = ZaZimbraAdmin.VIEW_INDEX++;
ZaZimbraAdmin._SEARCH_BUILDER_VIEW = ZaZimbraAdmin.VIEW_INDEX++;
ZaZimbraAdmin._SEARCH_BUILDER_TOOLBAR_VIEW = ZaZimbraAdmin.VIEW_INDEX++;
ZaZimbraAdmin._ZIMLET_LIST_VIEW = ZaZimbraAdmin.VIEW_INDEX++;
ZaZimbraAdmin._ADMIN_ZIMLET_LIST_VIEW = ZaZimbraAdmin.VIEW_INDEX++;
ZaZimbraAdmin._RESOURCE_LIST_VIEW = ZaZimbraAdmin.VIEW_INDEX++;

ZaZimbraAdmin._SERVER_VIEW = ZaZimbraAdmin.VIEW_INDEX++;
ZaZimbraAdmin._DOMAIN_VIEW = ZaZimbraAdmin.VIEW_INDEX++;
ZaZimbraAdmin._COS_VIEW = ZaZimbraAdmin.VIEW_INDEX++;
ZaZimbraAdmin._ACCOUNT_VIEW = ZaZimbraAdmin.VIEW_INDEX++;
ZaZimbraAdmin._ALIAS_VIEW = ZaZimbraAdmin.VIEW_INDEX++;
ZaZimbraAdmin._DL_VIEW = ZaZimbraAdmin.VIEW_INDEX++;
ZaZimbraAdmin._HELP_VIEW = ZaZimbraAdmin.VIEW_INDEX++;
ZaZimbraAdmin._MIGRATION_WIZ_VIEW = ZaZimbraAdmin.VIEW_INDEX++;
ZaZimbraAdmin._POSTQ_VIEW = ZaZimbraAdmin.VIEW_INDEX++;
ZaZimbraAdmin._POSTQ_BY_SERVER_VIEW = ZaZimbraAdmin.VIEW_INDEX++;
ZaZimbraAdmin._RESOURCE_VIEW = ZaZimbraAdmin.VIEW_INDEX++;
ZaZimbraAdmin._ZIMLET_VIEW = ZaZimbraAdmin.VIEW_INDEX++;

ZaZimbraAdmin.MSG_KEY = new Object();
ZaZimbraAdmin.MSG_KEY[ZaZimbraAdmin._ACCOUNTS_LIST_VIEW] = "Accounts_view_title";
ZaZimbraAdmin.MSG_KEY[ZaZimbraAdmin._SEARCH_LIST_VIEW] = "Search_view_title";
ZaZimbraAdmin.MSG_KEY[ZaZimbraAdmin._ACCOUNT_VIEW] = "Accounts_view_title";
ZaZimbraAdmin.MSG_KEY[ZaZimbraAdmin._ALIASES_LIST_VIEW] = "Aliases_view_title";
ZaZimbraAdmin.MSG_KEY[ZaZimbraAdmin._ALIAS_VIEW] = "Aliases_view_title";
ZaZimbraAdmin.MSG_KEY[ZaZimbraAdmin._DISTRIBUTION_LISTS_LIST_VIEW] = "DL_view_title";
ZaZimbraAdmin.MSG_KEY[ZaZimbraAdmin._DL_VIEW] = "DL_view_title";
ZaZimbraAdmin.MSG_KEY[ZaZimbraAdmin._GLOBAL_SETTINGS] = "GlobalConfig_view_title";
ZaZimbraAdmin.MSG_KEY[ZaZimbraAdmin._SERVERS_LIST_VIEW] = "Servers_view_title";
ZaZimbraAdmin.MSG_KEY[ZaZimbraAdmin._DOMAINS_LIST_VIEW] = "Domain_view_title";
ZaZimbraAdmin.MSG_KEY[ZaZimbraAdmin._COS_LIST_VIEW] = "COS_view_title";
ZaZimbraAdmin.MSG_KEY[ZaZimbraAdmin._STATISTICS] = "GlobalStats_view_title";
ZaZimbraAdmin.MSG_KEY[ZaZimbraAdmin._STATISTICS_BY_SERVER] = "ServerStats_view_title";
ZaZimbraAdmin.MSG_KEY[ZaZimbraAdmin._SERVER_VIEW] = "Servers_view_title";
ZaZimbraAdmin.MSG_KEY[ZaZimbraAdmin._HELP_VIEW] = "Help_view_title";
ZaZimbraAdmin.MSG_KEY[ZaZimbraAdmin._DOMAIN_VIEW] = "Domain_view_title";
ZaZimbraAdmin.MSG_KEY[ZaZimbraAdmin._COS_VIEW] = "COS_view_title";
ZaZimbraAdmin.MSG_KEY[ZaZimbraAdmin._STATUS] = "Status_view_title";
ZaZimbraAdmin.MSG_KEY[ZaZimbraAdmin._MIGRATION_WIZ_VIEW] = "Migration_wiz_title";
ZaZimbraAdmin.MSG_KEY[ZaZimbraAdmin._POSTQ_VIEW] = "PostQ_title";
ZaZimbraAdmin.MSG_KEY[ZaZimbraAdmin._POSTQ_BY_SERVER_VIEW] = "PostQ_title";
ZaZimbraAdmin.MSG_KEY[ZaZimbraAdmin._RESOURCE_VIEW] = "Resources_view_title";
ZaZimbraAdmin.MSG_KEY[ZaZimbraAdmin._RESOURCE_LIST_VIEW] = "Resources_view_title";
ZaZimbraAdmin.MSG_KEY[ZaZimbraAdmin._ADMIN_ZIMLET_LIST_VIEW] = "AdminZimlets_view_title";
ZaZimbraAdmin.MSG_KEY[ZaZimbraAdmin._ZIMLET_LIST_VIEW] = "Zimlets_view_title";
ZaZimbraAdmin.MSG_KEY[ZaZimbraAdmin._ZIMLET_VIEW] = "Zimlets_view_title";
// do not change the name of the cookie! SoapServlet looks for it
ZaZimbraAdmin._COOKIE_NAME = "ZM_ADMIN_AUTH_TOKEN";
	
// Public methods

ZaZimbraAdmin.prototype.toString = 
function() {
	return "ZaZimbraAdmin";
}

/**
* Sets up ZimbraMail, and then starts it by calling its constructor. It is assumed that the
* CSFE is on the same host.
*
* @param domain		the host that we're running on
*/
ZaZimbraAdmin.run =
function(domain) {
	if(window._dwtShellId )
		return;
	if(!DBG)
		DBG = new AjxDebug(AjxDebug.NONE, null, false);
	AjxEnv.hasFirebug = (AjxEnv.isFirefox && (typeof (console) != typeof (_UNDEFINED_)) && DBG && (DBG.getDebugLevel() > 0)) ; 
		
	ZmCsfeCommand.setServerUri(location.protocol+"//" + domain + ZaSettings.CSFE_SERVER_URI);
	ZmCsfeCommand.setCookieName(ZaZimbraAdmin._COOKIE_NAME);
	
	//License information will be load after the login and in the com_zimbra_license.js
	ZaServerVersionInfo.load();
	// Create the global app context
	var appCtxt = new ZaAppCtxt();

	// Create the shell
	var userShell = window.document.getElementById(ZaSettings.get(ZaSettings.SKIN_SHELL_ID));
	var shell = new DwtShell({userShell:userShell});
    appCtxt.setShell(shell);    
	
	/* Register our keymap and global key action handler with the shell's keyboard manager 
	 * CURRENTLY use $set: kbnav. 
	 */
	this._kbMgr = shell.getKeyboardMgr();
	this._kbMgr.registerKeyMap(new ZaKeyMap());
	this._kbMgr.pushDefaultHandler(this);
	
    // Go!
    var lm = new ZaZimbraAdmin(appCtxt);
}
ZaZimbraAdmin.prototype.getKeymapNameToUse = function () {
	if (this._app && this._app.getCurrentController()) {
		var c = this._app.getCurrentController();
		if (c && c.handleKeyAction)
			return c.toString();
	}
	return "ZaGlobal";
}

ZaZimbraAdmin.prototype.handleKeyAction = function () {
	switch (actionCode) {
		case ZaKeyMap.DBG_NONE:
			alert("Setting domain search limit to:" + AjxDebug.NONE);
			DBG.setDebugLevel(AjxDebug.NONE);
			break;
			
		case ZaKeyMap.DBG_1:
						alert("Setting domain search limit to:" + AjxDebug.DBG1);
			DBG.setDebugLevel(AjxDebug.DBG1);
			break;
			
		case ZaKeyMap.DBG_2:
			alert("Setting domain search limit to:" + AjxDebug.DBG2);
			DBG.setDebugLevel(AjxDebug.DBG2);
			break;
			
		case ZaKeyMap.DBG_3:
			alert("Setting domain search limit to:" + AjxDebug.DBG3);
			DBG.setDebugLevel(AjxDebug.DBG3);
			break;
			
		default: {
			
			if (this._app && this._app.getCurrentController()) {
				var c = this._app.getCurrentController();
				if (c && c.handleKeyAction)
					return c.handleKeyAction(actionCode, ev);
			} else {
				return false;
			}
			break;
		}
	}
	return true;
}
ZaZimbraAdmin.getInstance = function() {
	if(ZaZimbraAdmin._instance) {
		return ZaZimbraAdmin._instance;
	} else {
		ZaZimbraAdmin.run(document.domain);
		return ZaZimbraAdmin._instance;
	}
}

/**
* Returns a handle to the given app.
*
* @param appName	an app name
*/
ZaZimbraAdmin.prototype.getApp =
function() {
	return this._app;	
}

ZaZimbraAdmin.prototype.getAdminApp = 
function() {
	return this._app;
}

/**
* Returns a handle to the app view manager.
*/
ZaZimbraAdmin.prototype.getAppViewMgr =
function() {
	return this._appViewMgr;
}

/**
* Returns a handle to the overview panel controller.
*/

ZaZimbraAdmin.prototype.getOverviewPanelController =
function() {
	if (this._overviewPanelController == null)
		this._overviewPanelController = new ZaOverviewPanelController(this._appCtxt, this._shell, this._app);
	return this._overviewPanelController;
}

/**
* Sets the name of the currently active app. Done so we can figure out when an
* app needs to be launched.
*
* @param appName	the app
*/
ZaZimbraAdmin.prototype.setActiveApp =
function(appName) {
//	this._activeApp = appName;
}

ZaZimbraAdmin.logOff =
function() {
	ZmCsfeCommand.clearAuthToken();
	window.onbeforeunload = null;
	
	// NOTE: Mozilla sometimes handles UI events while the page is
	//       unloading which references classes and objects that no
	//       longer exist. So we put up the busy veil and reload
	//       after a short delay.
	var shell = DwtShell.getShell(window);
	shell.setBusy(true);
	
	var locationStr = location.protocol + "//" + location.hostname + ((location.port == '80') ? "" : ":" +location.port) + location.pathname;
	var act = new AjxTimedAction(null, ZaZimbraAdmin.redir, [locationStr]);
	AjxTimedAction.scheduleAction(act, 100);
}

ZaZimbraAdmin.redir =
function(locationStr){
	window.location = locationStr;
}


// Start up the ZimbraMail application
ZaZimbraAdmin.prototype.startup =
function() {

	this._appViewMgr = new ZaAppViewMgr(this._shell, this, true);
								        
	try {
		//if we're not logged in we will be thrown out here
		var soapDoc = AjxSoapDoc.create("GetInfoRequest", "urn:zimbraAccount", null);	
		var command = new ZmCsfeCommand();
		var params = new Object();
		params.soapDoc = soapDoc;	
		params.noSession = true;
		var resp = command.invoke(params);
		//initialize my rights
		ZaZimbraAdmin.initInfo (resp);
		if(!ZaSettings.initialized)
			ZaSettings.init();
		else
			ZaZimbraAdmin._killSplash();
		
	} catch (ex) {
		if(ex && ex.code != ZmCsfeException.NO_AUTH_TOKEN && ex.code != ZmCsfeException.SVC_AUTH_EXPIRED && ex.code != ZmCsfeException.SVC_AUTH_REQUIRED) {
			if(!ZaSettings.initialized)
				ZaSettings.init();
			else
				ZaZimbraAdmin._killSplash();
		}					
		this._handleException(ex, "ZaZimbraAdmin.prototype.startup", null, true);
	}
}

//process the GetInfoRequest response to set the domainAdminMaxMailQuota value in MB

ZaZimbraAdmin.initInfo =
function (resp) {
	if (resp && resp.Body && resp.Body.GetInfoResponse && resp.Body.GetInfoResponse.attrs){
		if(resp.Body.GetInfoResponse.attrs.attr && resp.Body.GetInfoResponse.attrs.attr instanceof Array) {
			var attrsArr = resp.Body.GetInfoResponse.attrs.attr;
			for ( var i=0; i < attrsArr.length; i ++) {
				if (attrsArr[i].name == "displayName") {
					var v = attrsArr[i]._content ;
					if (v != null && v.length > 0) {
						ZaZimbraAdmin.currentUserName = v ;
					}
				}
			}
		} else if (resp.Body.GetInfoResponse.attrs._attrs && typeof(resp.Body.GetInfoResponse.attrs._attrs) == "object") {
			var attrsArr = resp.Body.GetInfoResponse.attrs._attrs;
			if(attrsArr["displayName"] && attrsArr["displayName"].length) 
				ZaZimbraAdmin.currentUserName = attrsArr["displayName"];
		}	
		//fallback to email address	
		if ((!ZaZimbraAdmin.currentUserName || ZaZimbraAdmin.currentUserName.length <=0) && resp.Body.GetInfoResponse.name){
			ZaZimbraAdmin.currentUserName = resp.Body.GetInfoResponse.name;
		}
	}
}

ZaZimbraAdmin.prototype._setLicenseStatusMessage = function () {
	if ((typeof ZaLicense == "function") && (ZaSettings.LICENSE_ENABLED)){
		ZaLicense.setLicenseStatus(this);
	}
};

ZaZimbraAdmin.prototype.setStatusMsg = 
function(msg, clear) {
	this._statusBox.setText(msg);
	
	//HC: Why it has the ZmZimbraMail reference? Somebody please remove it.
	if (msg && clear) {
		var act = new AjxTimedAction(null, ZmZimbraMail._clearStatus, [this._statusBox]);
		AjxTimedAction.scheduleAction(act, ZmZimbraMail.STATUS_LIFE);
	}
}

ZaZimbraAdmin._clearStatus = 
function(statusBox) {
	statusBox.setText("");
	statusBox.getHtmlElement().className = "statusBox";
}

/*
ZaZimbraAdmin.prototype._createAppChooser =
function() {
	var buttons = new Array();
	
	if (ZaSettings.ADDRESSES_ENABLED)
		buttons.push(ZaAppChooser.B_ADDRESSES);
	if (ZaSettings.SYSTEM_CONFIG_ENABLED)
		buttons.push(ZaAppChooser.B_SYSTEM_CONFIG);
	if (ZaSettings.MONITORING_ENABLED)
		buttons.push(ZaAppChooser.B_MONITORING);

		
	buttons.push(ZaAppChooser.SEP, ZaAppChooser.B_HELP,ZaAppChooser.B_MIGRATION_WIZ, ZaAppChooser.B_LOGOUT);
	var appChooser = new ZaAppChooser(this._shell, null, buttons);
	
	var buttonListener = new AjxListener(this, this._appButtonListener);
	for (var i = 0; i < buttons.length; i++) {
		var id = buttons[i];
		if (id == ZaAppChooser.SEP) continue;
		var b = appChooser.getButton(id);
		b.addSelectionListener(buttonListener);
	}

	return appChooser;
}
*/
ZaZimbraAdmin.prototype._createAppTabs =
function () {
	var appTabGroup = new ZaAppTabGroup(this._shell, this.getApp());
	return appTabGroup ;
}

/*
ZaZimbraAdmin.prototype._createMainTab =
function () {
	var tabGroup = this._app.getTabGroup() ;
	tabGroup._mainTab = new ZaAppTab (tabGroup , this._app, 
				//this._app.getViewById(this._tabId)["APP CONTENT"].getTitle(), 
				//"Status",
				null, null,  
				null, null, false, true);
	
} */

ZaZimbraAdmin.prototype._createHelpLink =
function() {

	var helpLabel = new DwtComposite (this._shell, "HelpContainer", Dwt.RELATIVE_STYLE);
	var listener = new AjxListener(this, this._helpListener);
	var helpEl = helpLabel.getHtmlElement();
		
	var adminObj = this ;
	helpLabel.getHtmlElement().onclick = function () { ZaZimbraAdmin.prototype._helpListener.call(adminObj) ;};
	helpLabel.setCursor ("pointer") ;
	
	helpLabel.getHtmlElement().innerHTML = 
		this._getAppLink(null, "Help",  ZaMsg.helpDesk);
	
	helpLabel.reparentHtmlElement (ZaSettings.SKIN_HELP_DOM_ID) ;
}

ZaZimbraAdmin.prototype._createDownloadLink =
function() {
	var dwLabel = new DwtComposite (this._shell, "DWContainer", Dwt.RELATIVE_STYLE);
	var listener = new AjxListener(this, this._dwListener);
	
	//AjxTK addListener doesn't seem to work
	var adminObj = this ;
	dwLabel.getHtmlElement().onclick = function () { ZaZimbraAdmin.prototype._dwListener.call(adminObj) ;};
	dwLabel.setCursor ("pointer") ;
	
	dwLabel.getHtmlElement().innerHTML = 
		this._getAppLink(null, "MigrationWiz",  ZaMsg.goToMigrationWiz);
	
	dwLabel.reparentHtmlElement (ZaSettings.SKIN_DW_DOM_ID) ;
}

ZaZimbraAdmin.prototype._setUserName =
function () {
	var e = document.getElementById("skin_container_username") ;
	e.innerHTML = (ZaZimbraAdmin.currentUserName!=null && String(ZaZimbraAdmin.currentUserName).length>(skin.maxAdminName+1)) ? String(ZaZimbraAdmin.currentUserName).substr(0,skin.maxAdminName) : ZaZimbraAdmin.currentUserName;
}

ZaZimbraAdmin.prototype._helpListener =
function(ev) {
	//DBG.println(AjxDebug.DBG1, "Help is clicked ...") ;
	if(this._app.getCurrentController()) {
		this._app.getCurrentController().switchToNextView(this._app.getHelpViewController(), ZaHelpViewController.prototype.show, null);
	} else {					
		this._app.getHelpViewController().show();
	}
}

ZaZimbraAdmin.prototype._dwListener = 
function (ev) {
	//DBG.println(AjxDebug.DBG1, "Download is clicked ...") ;
	if(this._app.getCurrentController()) {
		this._app.getCurrentController().switchToNextView(this._app.getMigrationWizController(), ZaMigrationWizController.prototype.show, null);
	} else {					
		this._app.getMigrationWizController().show();
	}
}

ZaZimbraAdmin.prototype._createBanner =
function() {
	// The LogoContainer style centers the logo
	var banner = new DwtComposite(this._shell, "LogoContainer", Dwt.ABSOLUTE_STYLE);
	var html = new Array();
	var i = 0;
	html[i++] = "<a href='";
	html[i++] = ZaSettings.LOGO_URI;
	html[i++] = "' target='_blank'><div class='"+AjxImg.getClassForImage("AppBanner")+"'></div></a>";
	banner.getHtmlElement().innerHTML = html.join("");
	return banner;
}

ZaZimbraAdmin.prototype._createLogOff =
function () {
	var logoff = document.getElementById(ZaSettings.SKIN_LOGOFF_DOM_ID);
	if (logoff) logoff.innerHTML = this._getAppLink("ZaZimbraAdmin.logOff();", "Logoff",  ZaMsg.logOff);
	logoff.style.cursor = "pointer" ;
}

//set the html content for logoff, help and download
ZaZimbraAdmin.prototype._getAppLink =
function(staticFunc, icon, lbl) {
	var html = [];
	var i = 0;
	html[i++] = "<table border=0 cellpadding=1 cellspacing=1 align=right><tr>";
	
	//html[i++] = "<td align=right><a  href='javascript:;'";
	html[i++] = "<td align=right><span ";
	if (staticFunc) {
		html[i++] = " onclick='" + staticFunc + "' " ;
	}
	html[i++] = ">";
	html[i++] = AjxImg.getImageHtml(icon, null, "border=0");
	//html[i++] = "</a></td>";
	html[i++] = "</span></td>";
	
	html[i++] = "<td width=1% align=right style='white-space:nowrap; font-weight:bold'><span " ;
	if (staticFunc) {
		html[i++] = " onclick='" + staticFunc + "' " ;
	}
	html[i++] = ">";
	html[i++] = lbl;
	html[i++] = "</span></td></tr></table>";
	
	//var cell = document.getElementById(id);
	//if (cell) cell.innerHTML = html.join("");
	return html.join("");
}

// Private methods

ZaZimbraAdmin._killSplash =
function() {
	if(ZaZimbraAdmin._splashScreen)
		ZaZimbraAdmin._splashScreen.setVisible(false);
}

ZaZimbraAdmin.showSplash =
function(shell) {
	if(ZaZimbraAdmin._splashScreen)
		ZaZimbraAdmin._splashScreen.setVisible(true);
	else {
		ZaZimbraAdmin._splashScreen = new ZaSplashScreen(shell);
	}
}



/**
* Creates an app object, which doesn't necessarily do anything just yet.
**/
ZaZimbraAdmin.prototype._createApp =
function() {
	this._app = new ZaApp(this._appCtxt, this._shell);	
}


/**
* Launching an app causes it to create a view (if necessary) and display it. The view that is created is up to the app.
* Since most apps schedule an action as part of their launch, a call to this function should not be
* followed by any code that depends on it (ie, it should be a leaf action).
**/
ZaZimbraAdmin.prototype._launchApp =
function() {
	if (!this._app)
		this._createApp();

    this._appCtxt.setClientCmdHdlr(new ZaClientCmdHandler(this._app));
    //draw stuff
	var elements = new Object();
	elements[ZaAppViewMgr.C_SASH] = new DwtSash(this._shell, DwtSash.HORIZONTAL_STYLE,"console_inset_app_l", 20);
	elements[ZaAppViewMgr.C_BANNER] = this._createBanner();		
	//elements[ZaAppViewMgr.C_APP_CHOOSER] = this._createAppChooser();
	elements[ZaAppViewMgr.C_STATUS] = this._statusBox = new DwtText(this._shell, "statusBox", Dwt.ABSOLUTE_STYLE);
	this._statusBox.setScrollStyle(Dwt.CLIP);
	this._setLicenseStatusMessage();
	// the outer element of the entire skin is hidden until this point
	// so that the skin won't flash (become briefly visible) during app loading
	if (skin && skin.showSkin){
		skin.showSkin(true);	
		//hide the advanced search builder at the beginning
		skin.showSearchBuilder(false);  
	}	
	this._appViewMgr.addComponents(elements, true);

	var elements = new Object();
	elements[ZaAppViewMgr.C_TREE] = this.getOverviewPanelController().getOverviewPanel();
	elements[ZaAppViewMgr.C_SEARCH] = this._app.getSearchListController().getSearchPanel();		
	elements[ZaAppViewMgr.C_SEARCH_BUILDER_TOOLBAR] = this._app.getSearchBuilderToolbarController ().getSearchBuilderTBPanel();
	elements[ZaAppViewMgr.C_SEARCH_BUILDER] = this._app.getSearchBuilderController().getSearchBuilderPanel();
	//Use reparentHtmlelement to add the tabs. Reenable this line if it doesn't work well.
	elements[ZaAppViewMgr.C_APP_TABS] = this._createAppTabs() ;
	elements[ZaAppViewMgr.C_CURRENT_APP] = new ZaCurrentAppToolBar(this._shell);
	this._appViewMgr.addComponents(elements, true);

	//add logoff
	this._createLogOff();
	this._createHelpLink();
	this._createDownloadLink() ;
	this._setUserName() ;
	//this._createAppTabs() ;
	
	this._app.launch();
	
	//create main Tab
	//this._createMainTab() ;
	
	ZaZimbraAdmin._killSplash();
};

// Listeners

// Banner button mouseover/mouseout handlers
ZaZimbraAdmin._bannerBarMouseHdlr =
function(ev) {
	window.status = ZaMsg.done;
	return true;
}

// This method is called by the window.onbeforeunload method.
ZaZimbraAdmin._confirmExitMethod =
function() {
	//check whether all the tabs are clean by close them
	var msg = ZaMsg.appExitWarning ;
	var tabTitles = ZaAppTabGroup.getDirtyTabTitles() ;
	if ( tabTitles.length > 0 ){
		msg = ZaMsg.appExitWarningWithDirtyTab + "\n" + tabTitles.join("\n");
	}
	return msg;
}


ZaZimbraAdmin.setOnbeforeunload = 
function(msg) {
	if (msg){
		window.onbeforeunload = msg;
	}else{
		window.onbeforeunload = null;
	}
};

/** This method is used for the download link hack to avoid the exit warning message **/
ZaZimbraAdmin.unloadHackCallback =
function() {
	ZaZimbraAdmin.setOnbeforeunload (null) ;
	var f = function() { ZaZimbraAdmin.setOnbeforeunload(ZaZimbraAdmin._confirmExitMethod); };
	var t = new AjxTimedAction(null, f);
	AjxTimedAction.scheduleAction(t, 3000);
};


ZaAboutDialog = function(parent, className, title, w, h) {
	if (arguments.length == 0) return;
 	var clsName = className || "DwtDialog";
 	DwtDialog.call(this, parent, clsName,  ZaMsg.about_title, [DwtDialog.OK_BUTTON]);
}

ZaAboutDialog.prototype = new DwtDialog;
ZaAboutDialog.prototype.constructor = ZaAboutDialog;

ZaAboutDialog.prototype.popup = function () {
	// Set the content of the dialog before popping it up.
	// This is done here because of the global IDs used by ZLoginFactory.
	var date = AjxDateFormat.getDateInstance().format(ZaServerVersionInfo.buildDate);
    var params = ZLoginFactory.copyDefaultParams(ZaMsg);
	params.showAbout = true,
	params.showPanelBorder = false;
	params.longVersion = AjxBuffer.concat(ZaMsg.splashScreenVersion, " ", ZaServerVersionInfo.version , " " , date);
    var html = ZLoginFactory.getLoginDialogHTML(params);
    this.setContent(html);

 	DwtBaseDialog.prototype.popup.call(this);
};

ZaAboutDialog.prototype.popdown =
function() {
 	DwtBaseDialog.prototype.popdown.call(this);
    this.setContent("");
};

