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
	ZaZimbraAdmin._instance = this;
	ZaController.call(this, appCtxt, null, null, true);
	this._shell = this._appCtxt.getShell();	
	this._splashScreen = new ZaSplashScreen(this._shell, "Admin_SplashScreen");
	
	appCtxt.setAppController(this);
	appCtxt.setClientCmdHdlr(new ZaClientCmdHandler(appCtxt));
		
	// handles to various apps
	this._appFactory = new Object();
	this._appFactory[ZaZimbraAdmin.ADMIN_APP] = ZaApp;
 
 	this._schedule(this.startup);								// creates the banner
	this.aboutDialog = new ZaAboutDialog(this._shell,null,ZaMsg.about_title);
}

ZaZimbraAdmin.prototype = new ZaController;
ZaZimbraAdmin.prototype.constructor = ZaZimbraAdmin;
ZaZimbraAdmin._instance = null;

ZaZimbraAdmin.ADMIN_APP = "admin";

ZaZimbraAdmin._MIGRATION_ID = 1;
ZaZimbraAdmin._HELP_ID = 2;
ZaZimbraAdmin._LOGOFF_ID = 3;
ZaZimbraAdmin._PDF_HELP_ID = 4;
ZaZimbraAdmin._ABOUT_ID = 5;

ZaZimbraAdmin._ADDRESSES = 1;
ZaZimbraAdmin._ACCOUNTS_LIST_VIEW = 2;
ZaZimbraAdmin._ALIASES_LIST_VIEW = 3;
ZaZimbraAdmin._DISTRIBUTION_LISTS_LIST_VIEW = 4;
ZaZimbraAdmin._SYS_CONFIG = 5;
ZaZimbraAdmin._GLOBAL_SETTINGS = 6;
ZaZimbraAdmin._SERVERS_LIST_VIEW = 7;
ZaZimbraAdmin._DOMAINS_LIST_VIEW = 8;
ZaZimbraAdmin._COS_LIST_VIEW = 9;
ZaZimbraAdmin._MONITORING = 10;
ZaZimbraAdmin._STATUS = 11;
ZaZimbraAdmin._STATISTICS = 12;

ZaZimbraAdmin._STATISTICS_BY_SERVER = 13;
ZaZimbraAdmin._SERVER_VIEW = 14;
ZaZimbraAdmin._DOMAIN_VIEW = 15;
ZaZimbraAdmin._COS_VIEW = 16;
ZaZimbraAdmin._ACCOUNT_VIEW = 17;
ZaZimbraAdmin._ALIAS_VIEW = 18;
ZaZimbraAdmin._DL_VIEW = 19;

ZaZimbraAdmin.MSG_KEY = new Object();
ZaZimbraAdmin.MSG_KEY[ZaZimbraAdmin._ACCOUNTS_LIST_VIEW] = "Accounts_view_title";
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
ZaZimbraAdmin.MSG_KEY[ZaZimbraAdmin._DOMAIN_VIEW] = "Domain_view_title";
ZaZimbraAdmin.MSG_KEY[ZaZimbraAdmin._COS_VIEW] = "COS_view_title";
ZaZimbraAdmin.MSG_KEY[ZaZimbraAdmin._STATUS] = "Status_view_title";

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
	
	// Create the global app context
	var appCtxt = new ZaAppCtxt();


	// Create the shell
	var userShell = window.document.getElementById(ZaSettings.get(ZaSettings.SKIN_SHELL_ID));
	var shell = new DwtShell(null, false, ZaZimbraAdmin._confirmExitMethod, userShell);
    appCtxt.setShell(shell);
    
    // Go!
    var lm = new ZaZimbraAdmin(appCtxt);
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
function(appName) {
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
		this._overviewPanelController = new ZaOverviewPanelController(this._appCtxt, this._shell);
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


// Start up the ZimbraMail application
ZaZimbraAdmin.prototype.startup =
function() {

	this._appViewMgr = new ZaAppViewMgr(this._shell, this, true);
								        
	try {
		var domains = ZaDomain.getAll(); // catch an exception before building the UI
		//if we're not logged in we will be thrown out here
		
		//draw stuff
		var elements = new Object();
		elements[ZaAppViewMgr.C_SASH] = new DwtSash(this._shell, DwtSash.HORIZONTAL_STYLE,"console_inset_app_l", 20);
		elements[ZaAppViewMgr.C_BANNER] = this._createBanner();		
		elements[ZaAppViewMgr.C_APP_CHOOSER] = this._createAppChooser();
		elements[ZaAppViewMgr.C_STATUS] = this._statusBox = new DwtText(this._shell, "statusBox", Dwt.ABSOLUTE_STYLE);
		this._statusBox.setScrollStyle(Dwt.CLIP);
		
	// the outer element of the entire skin is hidden until this point
	// so that the skin won't flash (become briefly visible) during app loading
		if (skin && skin.showSkin)
			skin.showSkin(true);		
		this._appViewMgr.addComponents(elements, true);
		this._launchApp();
		
		elements = new Object();
		elements[ZaAppViewMgr.C_TREE] = this.getOverviewPanelController().getOverviewPanel();
		elements[ZaAppViewMgr.C_SEARCH] = this._app.getAccountListController().getSearchPanel();		
		elements[ZaAppViewMgr.C_CURRENT_APP] = new ZaCurrentAppToolBar(this._shell);
		this._appViewMgr.addComponents(elements, true);
	} catch (ex) {
		this._handleException(ex, "ZaZimbraAdmin.prototype.startup", null, true);
	}
	this._schedule(this._killSplash);	// kill splash screen	
}

ZaZimbraAdmin.prototype._createAppChooser =
function() {
	var buttons = new Array();
	
	if (ZaSettings.MONITORING_ENABLED)
		buttons.push(ZaAppChooser.B_MONITORING);
	if (ZaSettings.SYSTEM_CONFIG_ENABLED)
		buttons.push(ZaAppChooser.B_SYSTEM_CONFIG);
	if (ZaSettings.ADDRESSES_ENABLED)
		buttons.push(ZaAppChooser.B_ADDRESSES);
		
	buttons.push(ZaAppChooser.SEP, ZaAppChooser.B_HELP, ZaAppChooser.B_LOGOUT);
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
// Private methods

ZaZimbraAdmin.prototype._killSplash =
function() {
	this._splashScreen.setVisible(false);
}

ZaZimbraAdmin.prototype._appButtonListener =
function(ev) {
	//var searchController = this._appCtxt.getSearchController();
	var id = ev.item.getData(Dwt.KEY_ID);
	switch (id) {
		case ZaAppChooser.B_MONITORING:

			if(this._app.getCurrentController()) {
				this._app.getCurrentController().switchToNextView(this._app.getStatusViewController(),ZaStatusViewController.prototype.show, null);
			} else {					
				this._app.getStatusViewController().show();
			}
			break;		
		case ZaAppChooser.B_SYSTEM_CONFIG:
			if(this._app.getCurrentController()) {
				this._app.getCurrentController().switchToNextView(this._app.getServerListController(), ZaServerListController.prototype.show, ZaServer.getAll());
			} else {					
				this._app.getServerListController().show(ZaServer.getAll());
			}
			break;		
		case ZaAppChooser.B_ADDRESSES:
			this._showAccountsView(ZaItem.ACCOUNT,ev);
			break;				
		case ZaAppChooser.B_LOGOUT:
			ZaZimbraAdmin.logOff();
			break;
	}
}

ZaZimbraAdmin.prototype._showAccountsView = function (defaultType, ev){
	var queryHldr = this._getCurrentQueryHolder();
	queryHldr.isByDomain = false;
	queryHldr.byValAttr = false;
	queryHldr.queryString = "";
	queryHldr.types = [ZaSearch.TYPES[defaultType]];
	var acctListController = this._app.getAccountListController();
	acctListController.setPageNum(1);
	queryHldr.fetchAttrs = ZaSearch.standardAttributes;
	
	if(this._app.getCurrentController()) {
		this._app.getCurrentController().switchToNextView(acctListController, ZaAccountListController.prototype.show,ZaSearch.searchByQueryHolder(queryHldr,acctListController.getPageNum(), ZaAccount.A_uid, null,this._app));
	} else {					
		acctListController.show(ZaSearch.searchByQueryHolder(queryHldr,1, ZaAccount.A_uid, null,this._app));
	}
	acctListController.setDefaultType(defaultType);
	acctListController.setQuery(queryHldr);
};

ZaZimbraAdmin.prototype._getCurrentQueryHolder = 
function () {
	var srchField = this._app.getAccountListController()._searchField;
	var curQuery = new ZaSearchQuery("", ZaZimbraAdmin._accountTypesArray, false, "");							
	if(srchField) {
		var obj = srchField.getObject();
		if(obj) {
			curQuery.types = new Array();
			if(obj[ZaSearch.A_fAliases]=="TRUE") {
				curQuery.types.push(ZaSearch.ALIASES);
			}
			if(obj[ZaSearch.A_fdistributionlists]=="TRUE") {
				curQuery.types.push(ZaSearch.DLS);
			}			
			if(obj[ZaSearch.A_fAccounts]=="TRUE") {
				curQuery.types.push(ZaSearch.ACCOUNTS);
			}			
		}
	}
	return curQuery;
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

	this._app.launch();
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
			window.open("/zimbraAdmin/adminhelp/html/OpenSourceAdminHelp/administration_console_help.htm");
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
		case ZaZimbraAdmin._ABOUT_ID:
			Dwt.getDomObj(doc, bannerBar._logAboutId).blur();
			Dwt.getDomObj(doc, bannerBar._logAboutId2).blur();			
			//show about screen
			ZaZimbraAdmin.getInstance().aboutDialog.popup();
			break;
		
	}
}

// Creates buttons for general non app-related functions and puts them on the banner.
ZaZimbraAdmin.prototype._createBannerBar =
function() {

	this.bannerBar = new DwtComposite(this._headerPanel, "BannerBar", DwtControl.RELATIVE_STYLE);
	
	this._bannerTableId = Dwt.getNextId();
	
	this.bannerBar._migrationId = Dwt.getNextId();
	this.bannerBar._helpId = Dwt.getNextId();
	this.bannerBar._pdfHelpId = Dwt.getNextId();	
	this.bannerBar._logOffId = Dwt.getNextId();
	this.bannerBar._logAboutId = Dwt.getNextId();
	
	this.bannerBar._migrationId2 = Dwt.getNextId();
	this.bannerBar._helpId2 = Dwt.getNextId();
	this.bannerBar._pdfHelpId2 = Dwt.getNextId();	
	this.bannerBar._logOffId2 = Dwt.getNextId();
	this.bannerBar._logAboutId2 = Dwt.getNextId();
	
	var html = new Array();
	var i = 0;
	
	html[i++] = "<table align='right' id='" + this._bannerTableId + "'><tr><td>&nbsp;";
	html[i++] = "</td></tr></table>";
	this.bannerBar.getHtmlElement().innerHTML = html.join("");
	var doc = this.bannerBar.getDocument();
	var t = Dwt.getDomObj(doc, this._bannerTableId);
	this.bannerBar.app = this._app;	
	Dwt.associateElementWithObject(t, this.bannerBar);		

	this._createBannerBarHtml();
}
ZaZimbraAdmin.prototype._createBannerBarHtml =
function () {

	if(!this.bannerBar || !this.bannerBar._helpId ||  !this.bannerBar._pdfHelpId || !this.bannerBar._logOffId || !this.bannerBar._helpId2 ||  !this.bannerBar._pdfHelpId2 || !this.bannerBar._logOffId2)
		return;
		
	var html = new Array();
	var i = 0;

	html[i++] = "<table width=100% id='" + this._bannerTableId + "'><tr>";
	html[i++] = "<td valign='middle' nowrap align='left'>";
	html[i++] = AjxImg.getImageHtml("AppBanner");
	html[i++] = "</td>";
	html[i++] = "<td valign='middle' align='right'>";
		html[i++] = "<table align='right'><tr>";
		html[i++] = "<td align=right><a id='" + this.bannerBar._migrationId + "'  target=\"_blank\" href=\"http://zimbra.com/downloads/migrationwizard/accept\">";
		html[i++] = AjxImg.getImageHtml("MigrationWiz", "cursor:hand");
		html[i++] = "</a></td>";
		html[i++] = "<td align=right><a id='" + this.bannerBar._migrationId2 + "' style='cursor: hand' target=\"_blank\" href=\"http://zimbra.com/downloads/migrationwizard/accept\">";
		html[i++] = ZaMsg.migrationWiz + "</a></td>";	
	
		html[i++] = "<td align=right><a id='" + this.bannerBar._helpId + "'>";
		html[i++] = AjxImg.getImageHtml("Help", "cursor:hand");
		html[i++] = "</a></td>";
		html[i++] = "<td align=right><a id='" + this.bannerBar._helpId2 + "'>";
		html[i++] = ZaMsg.help + "</a></td>";		
	
		html[i++] = "<td align=right><a id='" + this.bannerBar._logAboutId + "'>";
		html[i++] = AjxImg.getImageHtml("ZimbraIcon", "cursor:hand");
		html[i++] = "</a></td>";
		html[i++] = "<td align=right><a id='" + this.bannerBar._logAboutId2 + "'>";
		html[i++] = ZaMsg.about + "</a></td>";		
	
		html[i++] = "<td align=right><a id='" + this.bannerBar._pdfHelpId + "' target=\"_blank\" href=\"/zimbraAdmin/adminhelp/pdf/admin.pdf\">";
		html[i++] = AjxImg.getImageHtml("PDFDoc", "cursor:hand");
		html[i++] = "</a></td>";	
		html[i++] = "<td align=right><a id='" + this.bannerBar._pdfHelpId2 + "' target=\"_blank\" href=\"/zimbraAdmin/adminhelp/pdf/admin.pdf\">";
		html[i++] = ZaMsg.adminGuide + "</a></td>";	
	
		html[i++] = "<td align=right><a id='" + this.bannerBar._logOffId + "'>";
		html[i++] = AjxImg.getImageHtml("Logoff", "cursor:hand");		
		html[i++] = "</a></td>";
		html[i++] = "<td align=right><a id='" + this.bannerBar._logOffId2 + "'>";		
		html[i++] = ZaMsg.logOff + "</a></td></tr></table>";
	html[i++] = "</td></tr></table>";		
		
	this.bannerBar.getHtmlElement().innerHTML = html.join("");
	var doc = this.bannerBar.getDocument();
	var t = Dwt.getDomObj(doc, this._bannerTableId);
	this.bannerBar.app = this._app;	
	Dwt.associateElementWithObject(t, this.bannerBar);	

	var a;
	
	var a = Dwt.getDomObj(doc, this.bannerBar._helpId);
	if(a) {
		a.href = "javascript: void ZaZimbraAdmin._bannerBarHdlr(" + ZaZimbraAdmin._HELP_ID + ",'" + this._bannerTableId + "');";
		a.onmouseover = a.onmouseout = ZaZimbraAdmin._bannerBarMouseHdlr;
	}
	
	a = Dwt.getDomObj(doc, this.bannerBar._helpId2);
	if(a) {
		a.href = "javascript: void ZaZimbraAdmin._bannerBarHdlr(" + ZaZimbraAdmin._HELP_ID + ",'" + this._bannerTableId + "');";
	}	
			
	a = Dwt.getDomObj(doc, this.bannerBar._logOffId);
	if(a) {
		a.href = "javascript: void ZaZimbraAdmin._bannerBarHdlr(" + ZaZimbraAdmin._LOGOFF_ID + ",'" + this._bannerTableId + "');";
		a.onmouseover = a.onmouseout = ZaZimbraAdmin._bannerBarMouseHdlr;
	}

	a = Dwt.getDomObj(doc, this.bannerBar._logOffId2);
	if(a) {
		a.href = "javascript: void ZaZimbraAdmin._bannerBarHdlr(" + ZaZimbraAdmin._LOGOFF_ID + ",'" + this._bannerTableId + "');";
	}
	
	a = Dwt.getDomObj(doc, this.bannerBar._logAboutId);
	if(a) {
		a.href = "javascript: void ZaZimbraAdmin._bannerBarHdlr(" + ZaZimbraAdmin._ABOUT_ID + ",'" + this._bannerTableId + "');";
	}	
	
	a = Dwt.getDomObj(doc, this.bannerBar._logAboutId2);
	if(a) {
		a.href = "javascript: void ZaZimbraAdmin._bannerBarHdlr(" + ZaZimbraAdmin._ABOUT_ID + ",'" + this._bannerTableId + "');";
	}		
}
// This method is called by the window.onbeforeunload method.
ZaZimbraAdmin._confirmExitMethod =
function() {
	return ZaMsg.appExitWarning;
}


function ZaAboutDialog(parent, className, title, w, h) {
	if (arguments.length == 0) return;
	var clsName = className || "DwtDialog";
	
	DwtDialog.call(this, parent, clsName, null, [DwtDialog.OK_BUTTON]);
	this._createContentHtml();
}

ZaAboutDialog.prototype = new DwtDialog;
ZaAboutDialog.prototype.constructor = ZaAboutDialog;

ZaAboutDialog.prototype._createContentHtml = function () {
	AjxImg.setImage(this._contentDiv,"Admin_SplashScreen");
}
