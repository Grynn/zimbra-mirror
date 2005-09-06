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

/**
* Creates a controller to run ZimbraAdmin. Do not call directly, instead use the run()
* factory method.
* @constructor ZimbraAdmin
* @param appCtx
* @class ZimbraAdmin
* This class is responsible for bootstrapping the ZimbraAdmin application.
*/
function ZaZimbraAdmin(appCtxt) {

	ZaController.call(this, appCtxt, null, null, true);
	this._shell = this._appCtxt.getShell();	
	this._splashScreen = new ZaSplashScreen(this._shell, ZaImg.M_SPLASH);
	
	appCtxt.setAppController(this);
	appCtxt.setClientCmdHdlr(new ZaClientCmdHandler(appCtxt));
		
	this._apps = new Object();
	this._activeApp = null;
	
	// handles to various apps
	this._appFactory = new Object();
	this._appFactory[ZaZimbraAdmin.ADMIN_APP] = ZaApp;
	

//	this._createBanner();								// creates the banner
	this._schedule(ZaZimbraAdmin.prototype.startup);	// creates everything else
}

ZaZimbraAdmin.prototype = new ZaController;
ZaZimbraAdmin.prototype.constructor = ZaZimbraAdmin;

ZaZimbraAdmin.ADMIN_APP = "admin";

ZaZimbraAdmin._MIGRATION_ID = 1;
ZaZimbraAdmin._HELP_ID = 2;
ZaZimbraAdmin._LOGOFF_ID = 3;
ZaZimbraAdmin._PDF_HELP_ID = 4;

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

	ZmCsfeCommand.setServerUri(location.protocol+"//" + domain + ZaSettings.CSFE_SERVER_URI);
	ZmCsfeCommand.setCookieName(ZaZimbraAdmin._COOKIE_NAME);
//	ZaAuthenticate.setAdmin(true);
	
	// Create the global app context
	var appCtxt = new ZaAppCtxt();


	// Create the shell
//	var shell = new DwtShell(window, false, ZaZimbraAdmin._confirmExitMethod);
	var shell = new DwtShell(window, false, null);
    appCtxt.setShell(shell);
    
    // Go!
    var lm = new ZaZimbraAdmin(appCtxt);
}


/**
* Returns a handle to the given app.
*
* @param appName	an app name
*/
ZaZimbraAdmin.prototype.getApp =
function(appName) {
//DBG.println(AjxDebug.DBG3, "getApp " + appName);
	if (this._apps[appName] == null)
		this._createApp(appName);
	return this._apps[appName];
}

ZaZimbraAdmin.prototype.getAdminApp = 
function() {
	return this.getApp(ZaZimbraAdmin.ADMIN_APP);
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
		this._overviewPanelController = new ZaOverviewPanelController(this._appCtxt, this._shell, this);
	return this._overviewPanelController;
}

/**
* Returns a handle to the search bar's controller.
*/
ZaZimbraAdmin.prototype.getSearchController =
function() {
	if (this._searchController == null)
		this._searchController = new ZaSearchController(this._appCtxt, this._shell, this);
	return this._searchController;
}

/**
* Makes the given app the active (displayed) one. The stack of hidden views will be cleared.
* Note that setting the name of the currently active app is done separately, since a view
* switch may not actually happen due to view preemption.
*
* @param appName	an app name
*/
ZaZimbraAdmin.prototype.activateApp =
function(appName) {
DBG.println(AjxDebug.DBG1, "activateApp: " + appName + ", current app = " + this._activeApp);
	var view = this._appViewMgr.getAppView(appName);
	if (this._activeApp)
		this._apps[this._activeApp].activate(false); // notify previously active app
DBG.println(AjxDebug.DBG3, "activateApp, current " + appName + " view: " + view);
	if (view) {
		if (this._appViewMgr.setView(view)) {
			this._apps[appName].activate(true);
			this._appViewMgr.setAppView(appName, view);
		}
	} else {
		this._launchApp(appName);
	}
}

/**
* Sets the name of the currently active app. Done so we can figure out when an
* app needs to be launched.
*
* @param appName	the app
*/
ZaZimbraAdmin.prototype.setActiveApp =
function(appName) {
	this._activeApp = appName;
}

ZaZimbraAdmin.logOff =
function() {
	ZmCsfeCommand.clearAuthToken();
	var locationStr = location.protocol + "//" + location.hostname + ((location.port == '80')? "" : ":" +location.port) + "/zimbraAdmin";
	if (AjxEnv.isIE){
		var act = new AjxTimedAction ();
		act.method = ZaZimbraAdmin.redir;
		act.params.add(locationStr);
		AjxTimedAction.scheduleAction(act, 1);
	} else {
		window.location = locationStr;
	}
}

ZaZimbraAdmin.redir =
function(args){
	var locationStr = args[0];
	window.location = locationStr;
}

// Private methods

// Start up the ZimbraMail application
ZaZimbraAdmin.prototype.startup =
function() {

	this._appViewMgr = new ZaAppViewMgr(this._shell, this._banner, this);
								        
	try {
		var domains = ZaDomain.getAll(); // catch an exception before building the UI
		this._appViewMgr.setOverviewPanel(this.getOverviewPanelController().getOverviewPanel());
		this._appViewMgr.setSearchPanel(this.getSearchController().getSearchPanel());
		// Default to showing admin app
		this.activateApp(ZaZimbraAdmin.ADMIN_APP);
	} catch (ex) {
		this._handleException(ex, "ZaZimbraAdmin.prototype._startup", null, true);
	}
	this._schedule(this._killSplash);	// kill splash screen	
}

ZaZimbraAdmin.prototype._killSplash =
function() {
	this._splashScreen.setVisible(false);
}


// Creates an app object, which doesn't necessarily do anything just yet.
ZaZimbraAdmin.prototype._createApp =
function(appName) {
	if (this._apps[appName] != null)
		return;
DBG.println(AjxDebug.DBG1, "Creating app " + appName);
	this._apps[appName] = new this._appFactory[appName](this._appCtxt, this._shell);	
}

// Launching an app causes it to create a view (if necessary) and display it. The view that is created is up to the app.
// Since most apps schedule an action as part of their launch, a call to this function should not be
// followed by any code that depends on it (ie, it should be a leaf action).
ZaZimbraAdmin.prototype._launchApp =
function(appName) {
	if (!this._apps[appName])
		this._createApp(appName);
DBG.println(AjxDebug.DBG1, "Launching app " + appName);
	this._apps[appName].launch();
}

// Listeners

// Banner button mouseover/mouseout handlers
ZaZimbraAdmin._bannerBarMouseHdlr =
function(ev) {
	window.status = ZaMsg.done;
	return true;
}

ZaZimbraAdmin.prototype._showLoginDialog =
function(bReloginMode) {
	this._authenticating = true;
	this._loginDialog.setVisible(true, false);
	this._loginDialog.setUpKeyHandlers();	
	try {
		this._loginDialog.setFocus(this._appCtxt.getUsername(), bReloginMode);
	} catch (ex) {
		// something is out of whack... just make the user relogin
		ZaZimbraAdmin.logOff();
	}
}

ZaZimbraAdmin.prototype._hideLoginDialog =
function() {
	this._loginDialog.setVisible(false);
	this._loginDialog.setError(null);
	this._loginDialog.clearPassword();
	this._loginDialog.clearKeyHandlers();	
}

// Banner button click
ZaZimbraAdmin._bannerBarHdlr =
function(id, tableId) {
	var bannerBar = Dwt.getObjectFromElement(Dwt.getDomObj(document,tableId));
	if(!bannerBar)
		return;
		
	var doc = bannerBar.getDocument();
	switch (id) {
		case ZaZimbraAdmin._MIGRATION_ID:
			Dwt.getDomObj(doc, bannerBar._migrationId).blur();
			Dwt.getDomObj(doc, bannerBar._migrationId2).blur();			
			window.open("http://zimbra.com/downloads/migrationwizard/accept");
			break;
			
		case ZaZimbraAdmin._HELP_ID:
			Dwt.getDomObj(doc, bannerBar._helpId).blur();
			Dwt.getDomObj(doc, bannerBar._helpId2).blur();			
			window.open("/zimbraAdmin/adminhelp/html/WebHelp/administration_console_help.htm");
			break;

		case ZaZimbraAdmin._PDF_HELP_ID:
			Dwt.getDomObj(doc, bannerBar._helpId).blur();
			Dwt.getDomObj(doc, bannerBar._helpId2).blur();			
			window.open("/zimbraAdmin/adminhelp/pdf/admin.pdf");
			break;
						
		case ZaZimbraAdmin._LOGOFF_ID:
			Dwt.getDomObj(doc, bannerBar._logOffId).blur();
			ZaZimbraAdmin.logOff();
			break;
	}
}

// This method is called by the window.onbeforeunload method.
ZaZimbraAdmin._confirmExitMethod =
function() {
	return ZaMsg.appExitWarning;
}
