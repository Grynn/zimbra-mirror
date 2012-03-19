/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2004, 2005, 2006, 2007, 2008, 2009, 2010 Zimbra, Inc.
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

/**
* Creates a controller to run ZimbraAdmin. Do not call directly, instead use the run()
* factory method.
* @constructor ZimbraAdmin
* @param appCtx
* @class ZimbraAdmin
* This class is responsible for bootstrapping the ZimbraAdmin application.
*/
ZaZimbraAdmin = function(appCtxt) {
	if (arguments.length == 0) return;
	ZaZimbraAdmin._instance = this;
	ZaController.call(this, appCtxt, null,"ZaZimbraAdmin");

	ZaZimbraAdmin.showSplash(this._shell);
	skin.showSkin();
	appCtxt.setAppController(this);
    
    // handles to various apps
	this._appFactory = new Object();
	this._appFactory[ZaZimbraAdmin.ADMIN_APP] = ZaApp;
    this.startup();
    //this.aboutDialog = new ZaAboutDialog(this._shell,null,ZabMsg.about_title);
}

ZaZimbraAdmin.prototype = new ZaController;
ZaZimbraAdmin.prototype.constructor = ZaZimbraAdmin;
ZaZimbraAdmin._instance = null;

ZaZimbraAdmin.ADMIN_APP = "admin";
ZaZimbraAdmin.currentUserName = "" ;
ZaZimbraAdmin.currentUserLogin = "";
ZaZimbraAdmin.currentUserId = "";
ZaZimbraAdmin.URN = "urn:zimbraAdmin";
ZaZimbraAdmin.VIEW_INDEX = 0;
ZaZimbraAdmin.FIRST_DAY_OF_WEEK = 0;
ZaZimbraAdmin.isFirstRequest = false;

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
ZaZimbraAdmin._TOOLS = ZaZimbraAdmin.VIEW_INDEX++;
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

// new UI
ZaZimbraAdmin._HOME_VIEW = ZaZimbraAdmin.VIEW_INDEX++;
ZaZimbraAdmin._MONITOR_HOME_VIEW = ZaZimbraAdmin.VIEW_INDEX++;
ZaZimbraAdmin._MANAGE_ACCOUNT_HOME_VIEW = ZaZimbraAdmin.VIEW_INDEX++;
ZaZimbraAdmin._ADMINISTRATION_HOME_VIEW = ZaZimbraAdmin.VIEW_INDEX++;
ZaZimbraAdmin._MIGRATION_HOME_VIEW = ZaZimbraAdmin.VIEW_INDEX++;
ZaZimbraAdmin._DOWNLOAD_VIEW = ZaZimbraAdmin.VIEW_INDEX++;
ZaZimbraAdmin._SEARCH_HOME_VIEW = ZaZimbraAdmin.VIEW_INDEX++;
ZaZimbraAdmin._SEARCH_RESULT_VIEW = ZaZimbraAdmin.VIEW_INDEX++;
ZaZimbraAdmin._SEARCH_FILTER_VIEW = ZaZimbraAdmin.VIEW_INDEX++;
ZaZimbraAdmin._SERVER_STATUS_VIEW =  ZaZimbraAdmin.VIEW_INDEX++;
ZaZimbraAdmin._SERVER_STATISTICS_VIEW = ZaZimbraAdmin.VIEW_INDEX++;
ZaZimbraAdmin._SERVER_STATISTICS_TAB_VIEW = ZaZimbraAdmin.VIEW_INDEX++;
ZaZimbraAdmin._SERVER_LIST_FOR_STATISTICS_VIEW = ZaZimbraAdmin.VIEW_INDEX++;

ZaZimbraAdmin._HOME_VIEW = ZaZimbraAdmin.VIEW_INDEX++;
ZaZimbraAdmin._XFORM_VIEW = ZaZimbraAdmin.VIEW_INDEX++;
ZaZimbraAdmin._XFORM_TAB_VIEW = ZaZimbraAdmin.VIEW_INDEX++;
ZaZimbraAdmin._ACCOUNT_ALIAS_LIST_VIEW = ZaZimbraAdmin.VIEW_INDEX++;
ZaZimbraAdmin._COS_ACCOUNT_LIST_VIEW = ZaZimbraAdmin.VIEW_INDEX++;
ZaZimbraAdmin._COS_DOMAIN_LIST_VIEW = ZaZimbraAdmin.VIEW_INDEX++;
ZaZimbraAdmin._DOMAIN_ACCOUNT_LIST_VIEW = ZaZimbraAdmin.VIEW_INDEX++;
ZaZimbraAdmin._DOMAIN_ALIAS_LIST_VIEW = ZaZimbraAdmin.VIEW_INDEX++;
ZaZimbraAdmin._DL_ALIAS_LIST_VIEW = ZaZimbraAdmin.VIEW_INDEX++;
ZaZimbraAdmin._DL_MEMBERS_LIST_VIEW = ZaZimbraAdmin.VIEW_INDEX++;

// do not change the name of the cookie! SoapServlet looks for it
ZaZimbraAdmin._COOKIE_NAME = "ZM_ADMIN_AUTH_TOKEN";
ZaZimbraAdmin.TEST_COOKIE_NAME = "ZA_TEST";
	
// Public methods

ZaZimbraAdmin.prototype.toString = 
function() {
	return "ZaZimbraAdmin";
}

ZaZimbraAdmin.clearCookie = function () {
	try {
		var soapDoc = AjxSoapDoc.create("ClearCookieRequest", ZaZimbraAdmin.URN, null);
		var cookieBy = soapDoc.set("cookie");
		cookieBy.setAttribute("name", ZaZimbraAdmin._COOKIE_NAME);
		var clearCookieCommand = new ZmCsfeCommand();
		var params = new Object();
		params.soapDoc = soapDoc;
		params.skipExpiredToken = true;	
		params.asyncMode = false;
		params.noAuthToken = true;
		clearCookieCommand.invoke(params);
	} catch (ex) {
		this._handleException(ex, "ZaZimbraAdmin.clearCookie", null, true);
	}
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
	//AjxEnv.hasFirebug = (AjxEnv.isFirefox && (typeof (console) != typeof (_UNDEFINED_)) && DBG && (DBG.getDebugLevel() > 0)) ; 
		
	ZmCsfeCommand.setServerUri(location.protocol+"//" + domain + ZaSettings.CSFE_SERVER_URI);
	ZmCsfeCommand.setCookieName(ZaZimbraAdmin._COOKIE_NAME);

	var soapDoc = AjxSoapDoc.create("BatchRequest", "urn:zimbra");
	soapDoc.setMethodAttribute("onerror", "continue");
	
	if (!ZaServerVersionInfo._loaded){
		var versionInfoReq = soapDoc.set("GetVersionInfoRequest", null, null, ZaZimbraAdmin.URN);
	}	

	var domainInfoReq = soapDoc.set("GetDomainInfoRequest", null, null, ZaZimbraAdmin.URN);
	var elBy = soapDoc.set("domain", location.hostname, domainInfoReq);
	elBy.setAttribute("by", "virtualHostname");
	
	var command = new ZmCsfeCommand();
	var params = new Object();
	params.soapDoc = soapDoc;	
	params.noAuthToken = true;
	var resp;
    var isResend = false;
    try {
    	resp = command.invoke(params).Body.BatchResponse;		
	} catch (ex) {
        if (ex.code == ZmCsfeException.SVC_AUTH_EXPIRED) {
          isResend = true;
        } else {
			throw ex;
        }
	}
    if (isResend) {
    	ZaZimbraAdmin.clearCookie();
		params.resend = true;
        resp = command.invoke(params).Body.BatchResponse;
    }

	if(resp.GetVersionInfoResponse && resp.GetVersionInfoResponse[0]) {
		var versionResponse = resp.GetVersionInfoResponse[0];
		ZaServerVersionInfo.buildDate = ZaServerVersionInfo._parseDateTime(versionResponse.info[0].buildDate);
		ZaServerVersionInfo.host = versionResponse.info[0].host;
		ZaServerVersionInfo.release = versionResponse.info[0].release;
		ZaServerVersionInfo.version = versionResponse.info[0].version;
		ZaServerVersionInfo._loaded = true;
	}	

	if(resp.GetDomainInfoResponse && resp.GetDomainInfoResponse[0]) {
		var domainInfoResponse = resp.GetDomainInfoResponse[0];
    		var obj = {};
    		ZaItem.prototype.initFromJS.call(obj, domainInfoResponse.domain[0]);
    		ZaZimbraAdmin.zimbraAdminLoginURL = obj.attrs["zimbraAdminConsoleLoginURL"] ;
		if(obj.attrs["zimbraSkinLogoURL"]){
			ZaSettings.LOGO_URI = obj.attrs["zimbraSkinLogoURL"];
		}	
	}
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
	this._kbMgr.enable(true);
	this._kbMgr.registerKeyMap(new ZaKeyMap());
	this._kbMgr.pushDefaultHandler(this);
    // Go!
    var lm = new ZaZimbraAdmin(appCtxt);
}
ZaZimbraAdmin.prototype.getKeymapNameToUse = function () {
	if (ZaApp.getInstance() && ZaApp.getInstance().getCurrentController()) {
		var c = ZaApp.getInstance().getCurrentController();
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
			
			if (ZaApp.getInstance() && ZaApp.getInstance().getCurrentController()) {
				var c = ZaApp.getInstance().getCurrentController();
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
	if(ZaSettings.TREE_ENABLED) {
		if (this._overviewPanelController == null) {
            this._overviewPanelController = new ZaOverviewPanelController(this._appCtxt, this._shell);
            this._overviewPanelController.addSearchListener(new AjxListener(this._overviewPanelController, ZaOverviewPanelController.prototype.handleSearchFinished));
        }
		return this._overviewPanelController;
	} else {
		return null;
	}
}

ZaZimbraAdmin.prototype.getTaskController =
function() {
    if (this._taskController == null) {
        this._taskController= new ZaTaskController(this._appCtxt, this._shell);
    }
    return this._taskController;
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
	ZmCsfeCommand.noAuth = true;
	window.onbeforeunload = null;
	
	// NOTE: Mozilla sometimes handles UI events while the page is
	//       unloading which references classes and objects that no
	//       longer exist. So we put up the busy veil and reload
	//       after a short delay.
	var shell = DwtShell.getShell(window);
	shell.setBusy(true);
	
	var locationStr = location.protocol + "//" + location.hostname
            + ((location.port == '80') ? "" : ":" +location.port)
            + location.pathname
            //we want to add the query string as well
            + location.search;
	if (location.search) {
		locationStr = locationStr + "&logoff=1";
	} else {
		locationStr = locationStr + "?logoff=1";
	}

    var act = new AjxTimedAction(null, ZaZimbraAdmin.redir, [locationStr]);
	AjxTimedAction.scheduleAction(act, 100);
}

ZaZimbraAdmin.redir =
function(locationStr){
	window.location = locationStr;
}
// This function must be called after ZaZimbraAdmin.initInfo() is called.
ZaZimbraAdmin.isLanguage =
function(lang){
	var defaultLang = null;
	//if user have set its pref, just use user's seting
	//else use brower's setting 
	if(ZaZimbraAdmin.LOCALE == null){
		defaultLang = AjxEnv.DEFAULT_LOCALE; 
	}else{
		defaultLang = ZaZimbraAdmin.LOCALE; 
	}
	
	return defaultLang == lang;		
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
		params.noAuthToken = true;
		ZaZimbraAdmin.isFirstRequest = true;
		var resp = command.invoke(params);
		ZaZimbraAdmin.isFirstRequest = false;
		//initialize my rights
		ZaZimbraAdmin.initInfo (resp);

        //check the user locale settings and reload the message is needed.
        ZaZimbraAdmin.LOCALE_QS = "" ;
        if (ZaZimbraAdmin.LOCALE && (ZaZimbraAdmin.LOCALE != AjxEnv.DEFAULT_LOCALE)) {
            if (ZaZimbraAdmin.LOCALE != null) {
                var index = ZaZimbraAdmin.LOCALE.indexOf("_");
                if (index == -1) {
                    ZaZimbraAdmin.LOCALE_QS = "&language=" + ZaZimbraAdmin.LOCALE;
                } else {
                    ZaZimbraAdmin.LOCALE_QS = "&language=" + ZaZimbraAdmin.LOCALE.substring(0, index) +
                               "&country=" + ZaZimbraAdmin.LOCALE.substring(ZaZimbraAdmin.LOCALE.length - 2);
                }
            }

            ZaZimbraAdmin.reload_msg ();
            this.initDialogs(true) ;  //make sure all the precreated dialogs are also recreated.
        }

	if(ZaZimbraAdmin.isLanguage("ja")){
		if(ZaAccountXFormView.CONTACT_TAB_ATTRS)
        		ZaAccountXFormView.CONTACT_TAB_ATTRS.push(ZaAccount.A_zimbraPhoneticCompany);

		if(ZaAccountXFormView.ACCOUNT_NAME_GROUP_ATTRS)
        		ZaAccountXFormView.ACCOUNT_NAME_GROUP_ATTRS.push(ZaAccount.A_zimbraPhoneticFirstName, 
				ZaAccount.A_zimbraPhoneticLastName);
	}
	
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

ZaZimbraAdmin.reload_msg = function () {
    //if(window.console && window.console.log) console.log("Reloading the message ...") ;
    var includes = [] ;
    includes.push ( [appContextPath , "/res/" , "I18nMsg,AjxMsg,ZMsg,ZaMsg,AjxKeys" , ".js?v=" ,
                        appVers , ZaZimbraAdmin.LOCALE_QS].join("") );

    //the dynamic script load is asynchronous, may need a callback to make sure all the messages are actually loaded
    //if(window.console && window.console.log) console.log("Reload the message file: " + includes.toString()) ;

    //reinitialize the AjxFormat after the message files are loaded
    var callback = new AjxCallback (ZaZimbraAdmin.reinit_func); 

    AjxInclude(includes, null, callback);
    ZaZimbraAdmin._LOCALE_MSG_RELOADED = true ;
}

ZaZimbraAdmin.reinit_func = function() {
    AjxFormat.initialize();
    ZaItem.initDescriptionItem(); 
    ZaSettings.initConst();
    ZaDomain.initDomainStatus();
    Dwt_Datetime_XFormItem.initialize();
}

ZaZimbraAdmin.initInfo =
function (resp) {
	if(resp && resp.Body && resp.Body.GetInfoResponse) {
		ZaZimbraAdmin.currentUserLogin = resp.Body.GetInfoResponse.name;
		ZaZimbraAdmin.currentUserId = resp.Body.GetInfoResponse.id;
		var adminName = resp.Body.GetInfoResponse.name;

        if(adminName) {
            var emailChunks = adminName.split("@");
            if(emailChunks.length > 1 ) {
                ZaSettings.myDomainName = emailChunks[1];
            }
        }
        
        ZaZimbraAdmin.currentAdminId = resp.Body.GetInfoResponse.id;
        
		if (resp.Body.GetInfoResponse.attrs){
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
			if (!ZaZimbraAdmin.currentUserName || ZaZimbraAdmin.currentUserName.length <=0){
				ZaZimbraAdmin.currentUserName = ZaZimbraAdmin.currentUserLogin;
			}
	
	        if (resp && resp.Body && resp.Body.GetInfoResponse && resp.Body.GetInfoResponse.prefs) {
	            var prefs = resp.Body.GetInfoResponse.prefs._attrs ;
	            if (prefs && prefs["zimbraPrefLocale"]) {
	                //get the zimbraPrefLocale
	                ZaZimbraAdmin.LOCALE = prefs["zimbraPrefLocale"] ;
	            }

                if (prefs && prefs[ZaAccount.A_zimbraPrefAdminConsoleWarnOnExit]) {
                    ZaZimbraAdmin.isWarnOnExit = (prefs[ZaAccount.A_zimbraPrefAdminConsoleWarnOnExit] == "TRUE") ;
                }
               
				if (prefs && !AjxUtil.isEmpty(prefs[ZaAccount.A_zimbraPrefCalendarFirstDayOfWeek])) {
                    ZaZimbraAdmin.FIRST_DAY_OF_WEEK = prefs[ZaAccount.A_zimbraPrefCalendarFirstDayOfWeek];
                }
	        }
		}
    }
}

ZaZimbraAdmin.prototype._setLicenseStatusMessage = function () {
	if ((typeof ZaLicense == "function") && (ZaSettings.LICENSE_ENABLED)){
		ZaLicense.setLicenseStatus(this);
	} 
}

ZaZimbraAdmin.prototype.setStatusMsg = 
function(msg, clear) {
	if(!ZaSettings.STATUS_ENABLED) {
		return;
	}
    if(!appNewUI) {
    	Dwt.show(this._statusBox.getHTMLElId());
    	this._statusBox.setText(msg);
    	Dwt.show(ZaSettings.SKIN_STATUS_ID);
    	ZaApp.getInstance().getAppViewMgr().fitAll();
    }
}

ZaZimbraAdmin.prototype.clearStatus = 
function() {
	if(!ZaSettings.STATUS_ENABLED) {
		return;
	}
	if(!appNewUI) {
		Dwt.hide(this._statusBox.getHTMLElId());
		this._statusBox.setText("");
		Dwt.hide(ZaSettings.SKIN_STATUS_ID);
		ZaApp.getInstance().getAppViewMgr().fitAll();
	}
}

ZaZimbraAdmin.prototype._createAppTabs =
function () {
	var appTabGroup = new ZaAppTabGroup(this._shell);
	return appTabGroup ;
}

ZaZimbraAdmin.prototype._createRefreshLink =
function() {
	var refreshContainer = document.getElementById(ZaSettings.SKIN_REFRESH_DOM_ID);
	if(!refreshContainer) {
		return;
	}
    var refreshLabel = new DwtComposite (this._shell, "RefreshContainer", Dwt.RELATIVE_STYLE);
    var refreshEl = refreshLabel.getHtmlElement();
    refreshLabel.setCursor ("pointer");
    refreshEl.onclick = function () { ZaZimbraAdmin.prototype._refreshListener.call(ZaZimbraAdmin.getInstance());};
    refreshEl.innerHTML = this._getAppLink(null, "SearchRefreshWhite");
    refreshLabel.reparentHtmlElement (ZaSettings.SKIN_REFRESH_DOM_ID) ;
}


ZaZimbraAdmin.prototype._createPreviousLink =
function() {
	var previousContainer = document.getElementById(ZaSettings.SKIN_PREVIOUS_DOM_ID);
	if(!previousContainer) {
		return;
	}
    var previousLabel = this._previousContainer = new DwtComposite (this._shell, "PreviousContainer", Dwt.RELATIVE_STYLE);
    var previousEl = previousLabel.getHtmlElement();
    previousLabel.setCursor ("pointer");
    previousEl.onclick = function () { ZaZimbraAdmin.prototype._goPrevListener.call(ZaZimbraAdmin.getInstance());};
    previousEl.innerHTML =  AjxImg.getImageSpanHtml("LeftArrowNormal");
    previousLabel.reparentHtmlElement (ZaSettings.SKIN_PREVIOUS_DOM_ID) ;
}

ZaZimbraAdmin.prototype._createNextLink =
function() {
	var nextContainer = document.getElementById(ZaSettings.SKIN_NEXT_DOM_ID);
	if(!nextContainer) {
		return;
	}
    var nextLabel = this._nextContainer = new DwtComposite (this._shell, "NextContainer", Dwt.RELATIVE_STYLE);
    var nextEl = nextLabel.getHtmlElement();
    nextLabel.setCursor ("pointer");
    nextEl.onclick = function () { ZaZimbraAdmin.prototype._goNextListener.call(ZaZimbraAdmin.getInstance());};
    nextEl.innerHTML = AjxImg.getImageSpanHtml("RightArrowNormal");
    nextLabel.reparentHtmlElement (ZaSettings.SKIN_NEXT_DOM_ID) ;
}

ZaZimbraAdmin.prototype.updatePreNext =
function () {
    var isPrevious = this._historyMgr.isPrevious();
    var isNext = this._historyMgr.isNext();

    this._previousContainer.setEnabled(isPrevious);
    this._nextContainer.setEnabled(isNext);

}
ZaZimbraAdmin.prototype._refreshListener =
function(ev) {
	var curController = ZaApp.getInstance().getCurrentController();
	if (AjxUtil.isInstance(curController, ZaHelpViewController)){
		return; //don't refresh help
	}

	var tree = ZaZimbraAdmin.getInstance().getOverviewPanelController().getOverviewPanel().getFolderTree();
	if(AjxUtil.isEmpty(tree)){
		return;
	}

	var curItems = tree.getCurrentSelectedItems().getArray();
	if(AjxUtil.isEmpty(curItems) || AjxUtil.isEmpty(curItems[0])){
		return;
	}

	var curItem = curItems[0];
	var curPath = tree.getABPath(curItem.getData("dataItem"));
	if(AjxUtil.isEmpty(curPath)){
		return;
	}

	tree.setSelectionByPath(curPath, false, undefined, undefined, undefined, true);
}

ZaZimbraAdmin.prototype._goPrevListener =
function(ev) {
    var currentObject = this._historyMgr.getPrevious();
    if (currentObject) {
        currentObject.goToView();
        this.updatePreNext();
    }
}

ZaZimbraAdmin.prototype._goNextListener =
function(ev) {
    var currentObject = this._historyMgr.getNext();
    if (currentObject) {
        currentObject.goToView();
        this.updatePreNext();
    }
}

ZaZimbraAdmin.prototype._createHelpLink =
function() {
    if (!appNewUI) {
        this._createOldHelpLink();
        return;
    }

    var helpSkinContainer = document.getElementById(ZaSettings.SKIN_HELP_DOM_ID);
    if(!helpSkinContainer) {
        return;
    }
    var dwButton = new DwtBorderlessButton(this._shell, "", "", Dwt.RELATIVE_STYLE);
    dwButton.setText(ZaMsg.help);
    if (appNewUI)
        dwButton.setDropDownImages("NodeExpandedWhite");

	helpSkinContainer.innerHTML = "";
	dwButton.reparentHtmlElement (ZaSettings.SKIN_HELP_DOM_ID);

    // Add Zimbra Help Desk Menu
    var helpMenuOpList = new Array();
    var menu = new ZaPopupMenu(dwButton, "ZaHelpDropdown",null, helpMenuOpList, "ZA_HELP");

    var mItem =  new DwtMenuItem ({parent:menu, id: "zaHelpHomepage", className:"ZaHelpDropdownFirstItem"});
    mItem.setText(ZabMsg.zimbraHomePage);
    mItem.addSelectionListener(new AjxListener(this, this._contextHelpListener));

    mItem =  new DwtMenuItem ({parent:menu, id: "zaHelpCenter", className:"ZaHelpDropdownItem"});
    mItem.setText(ZaMsg.zimbraHelpCenter);
    mItem.addSelectionListener(new AjxListener(this, this._helpListener));

    mItem =  new DwtMenuItem ({parent:menu, id: "aboutZimbra", className:"ZaHelpDropdownLastItem"});
    mItem.setText(ZabMsg.zimbraAbout);
    mItem.addSelectionListener(new AjxListener(this, this._aboutZimbraListener));

    menu.addChild(this._createHelpSearch(), 0);
    menu.addPopupListener(new AjxListener(this, this._helpMenuPopupListener, menu));

    dwButton.setMenu(menu,true);
}

ZaZimbraAdmin.prototype._createHelpSearch =
function() {
    var helpSearch = new DwtComposite (this._shell, "ZaHelpDropdownSearch", Dwt.RELATIVE_STYLE);
    var helpSearchBox = new DwtComposite (helpSearch, "SearchPanel", Dwt.RELATIVE_STYLE);
    var searchIcon = new DwtComposite({parent:helpSearchBox, className:"ImgSearch ZaHelpSearchIcon"});
    searchIcon.getHtmlElement().onclick = this._openSupportSite;

    var searchInputField = new DwtInputField({parent:helpSearchBox, validationStyle:DwtInputField.ONEXIT_VALIDATION, inputId:"ZaHelpSearchInput"});
    searchInputField.setValidatorFunction(searchInputField.getInputElement(), this._openSupportSite);
    searchInputField.getInputElement().onblur = null;
    return helpSearch;
}

ZaZimbraAdmin.prototype._helpMenuPopupListener =
function(menu) {
    var helpHomePageItem = menu.getItem(1);
    if (!helpHomePageItem) {
        return null;
    }

    var itemW = helpHomePageItem.getW() - 20;

    //assume 5.5px per letter
    var maxNumberOfLetters = Math.floor((itemW - 30)/5.5);
    var text = ZaApp.getInstance().getCurrentController()._helpButtonText;
    if (maxNumberOfLetters < text.length){ //set the new text
        text = text.substring(0, (maxNumberOfLetters - 3)) + "...";
    }

    helpHomePageItem.setText(text);
}

ZaZimbraAdmin.prototype._openSupportSite =
function() {
    window.open(ZaSettings.ZIMBRA_SUPPORT_URL_QUERY + document.getElementById("ZaHelpSearchInput").value);
}

ZaZimbraAdmin.prototype._contextHelpListener =
function() {
    window.open(ZaApp.getInstance().getCurrentController()._helpURL);
}

ZaZimbraAdmin.prototype._aboutZimbraListener =
function() {
    if (!this.aboutZimbraDialog) {
        this.aboutZimbraDialog = new ZaAboutDialog(this._shell);
    }
    this.aboutZimbraDialog.popup();
}

ZaZimbraAdmin.prototype._createOldHelpLink =
function() {
	var helpSkinContainer = document.getElementById(ZaSettings.SKIN_HELP_DOM_ID);
	if(!helpSkinContainer) {
		return;
	}
    var helpLabel = new DwtComposite (this._shell, "HelpContainer", Dwt.RELATIVE_STYLE);
    var helpEl = helpLabel.getHtmlElement();
    helpLabel.setCursor ("pointer") ;

    var iconName = (appNewUI)?"":"Help";
    if (ZaSettings.isYahooSmbPADomainAdmin)   {
        helpLabel.getHtmlElement().innerHTML =
            this._getAppLink("SMBAccount.openHelpDesk();", iconName,  ZaMsg.helpDesk, skin.skin_container_help_max_str_length);
    } else { //this is the help link for the regular admin
        var listener = new AjxListener(this, this._helpListener);
        var adminObj = this ;
        helpLabel.getHtmlElement().onclick = function () { ZaZimbraAdmin.prototype._helpListener.call(adminObj) ;};
        helpLabel.getHtmlElement().innerHTML =
             this._getAppLink(null, iconName,  ZaMsg.helpDesk, skin.skin_container_help_max_str_length);
    }
    helpLabel.reparentHtmlElement (ZaSettings.SKIN_HELP_DOM_ID) ;
}

ZaZimbraAdmin.prototype._createDownloadLink =
function() {
	var downloadsContainer = document.getElementById(ZaSettings.SKIN_DW_DOM_ID);
	if(!downloadsContainer) {
		return;
	}
	var dwLabel = new DwtComposite (this._shell, "DWContainer", Dwt.RELATIVE_STYLE);
	var listener = new AjxListener(this, this._dwListener);
	
	//AjxTK addListener doesn't seem to work
	var adminObj = this ;
	dwLabel.getHtmlElement().onclick = function () { ZaZimbraAdmin.prototype._dwListener.call(adminObj) ;};
	dwLabel.setCursor ("pointer") ;
	
	dwLabel.getHtmlElement().innerHTML = 
		this._getAppLink(null, "Migration",  ZaMsg.goToMigrationWiz, skin.skin_container_dw_max_str_length);
	
	dwLabel.reparentHtmlElement (ZaSettings.SKIN_DW_DOM_ID) ;
}

ZaZimbraAdmin.prototype._setUserName =
function () {
	var userNameContainer = document.getElementById(ZaSettings.SKIN_USER_NAME_ID) ;
	if(!userNameContainer) {
		return;
	}
		
	if(!ZaZimbraAdmin.currentUserName) {
		return;
	}
	
	var dwLabel = new DwtLabel(this._shell, "", "", Dwt.RELATIVE_STYLE);	
	var containerWidth = Dwt.getSize(userNameContainer).x;
	var innerContent = null;
    var tmpName = AjxStringUtil.htmlEncode(ZaZimbraAdmin.currentUserName);
	if(containerWidth <= 20) {
		// if there are not enough space, just follow skin's setting
		innerContent = ( String(tmpName).length>(skin.maxAdminName+1)) ? String(tmpName).substr(0,skin.maxAdminName) : tmpName;
	}
	else {
		// reserve 20px for estimation error. 
		// here we assume 5.5px for one word, just follow the apptab.
		var maxNumberOfLetters = Math.floor((containerWidth - 20)/5.5);
		innerContent = tmpName;
		if (maxNumberOfLetters < innerContent.length) {
			innerContent = innerContent.substring(0, (maxNumberOfLetters - 3)) + "..."
		}	
	}

	dwLabel.setText(innerContent);	
	if(innerContent != ZaZimbraAdmin.currentUserName){
		dwLabel._setMouseEvents();
		dwLabel.setToolTipContent( tmpName );
	}	
	userNameContainer.innerHTML = ""; // clean the "Administrator" inherited from the skin's raw html code	
	dwLabel.reparentHtmlElement (ZaSettings.SKIN_USER_NAME_ID) ;
}

ZaZimbraAdmin.prototype._createUserName =
function () {
	var userNameContainer = document.getElementById(ZaSettings.SKIN_USERNAME_DOM_ID) ;
	if(!userNameContainer) {
		return;
	}

	if(!ZaZimbraAdmin.currentUserName) {
		return;
	}

	var dwButton = new DwtBorderlessButton(this._shell, "", "", Dwt.RELATIVE_STYLE);
	var containerWidth = Dwt.getSize(userNameContainer).x;
	if (appNewUI) {
		containerWidth = containerWidth - 16; // substract drop-down icon's width
		if(AjxEnv.isIE) {
			containerWidth -= 12; //substract extra padding+border
		}
	}
	var innerContent = null;
    var tmpName = AjxStringUtil.htmlEncode(ZaZimbraAdmin.currentUserName);
	if(containerWidth <= 40) {
		// if there are not enough space, just follow skin's setting
		innerContent = ( String(tmpName).length>(skin.maxAdminName+1)) ? String(tmpName).substr(0,skin.maxAdminName) : tmpName;
	}
	else {
		// reserve 10px for estimation error.
		// here we assume 5.5px for one word, just follow the apptab.
		var maxNumberOfLetters = Math.floor((containerWidth - 10)/5.5);
		innerContent = tmpName;
		if (maxNumberOfLetters < innerContent.length) {
			innerContent = innerContent.substring(0, (maxNumberOfLetters - 3)) + "..."
		}
	}

	dwButton.setText(innerContent);
    if (appNewUI)
	    dwButton.setDropDownImages("NodeExpandedWhite");
	if(innerContent != ZaZimbraAdmin.currentUserName){
		dwButton.setToolTipContent( tmpName );
	}
	userNameContainer.innerHTML = "";
	dwButton.reparentHtmlElement (ZaSettings.SKIN_USERNAME_DOM_ID);

    // Add LogOff Menu
    var userNameMenuOpList = new Array();
	userNameMenuOpList.push(new ZaOperation(ZaOperation.LOGOFF, ZaMsg.logOff, ZaMsg.logOff,  "Logoff", "LogoffDis", new AjxListener(window,ZaZimbraAdmin.logOff)));

    var menu = new ZaPopupMenu(dwButton, null,null, userNameMenuOpList, "ZA_LOGOFF");
    dwButton.setMenu(menu,true);

}

ZaZimbraAdmin.prototype._helpListener =
function(ev) {
	//DBG.println(AjxDebug.DBG1, "Help is clicked ...") ;
    var acctName = ZaZimbraAdmin.currentUserLogin;
    var domainName = acctName.split('@')[1];
    var domain = ZaDomain.getDomainByName(domainName);
    var curAcct = ZaZimbraAdmin.currentAdminAccount;
    if(curAcct && domain) {
        var url = null;
        if(curAcct.attrs[ZaAccount.A_zimbraIsAdminAccount] == "TRUE")
            url = domain.attrs[ZaDomain.A_zimbraHelpAdminURL];
        else url = domain.attrs[ZaDomain.A_zimbraHelpDelegatedURL];
        if(url) {
                window.open(url);
                return;
        }
    }
    //skin takes the zimbraHelpAdminURL and put it into the skin hints
    var helpButton = skin && skin.hints && skin.hints.helpButton;	  
    if (helpButton && helpButton.url) {
		var sep = helpButton.url.match(/\?/) ? "&" : "?";
		var url = [ helpButton.url, sep, "locid=", AjxEnv.DEFAULT_LOCALE ].join("");
		window.open(url);
		return;
	}

    if(ZaApp.getInstance().getCurrentController()) {
		ZaApp.getInstance().getCurrentController().switchToNextView(ZaApp.getInstance().getHelpViewController(), ZaHelpViewController.prototype.show, null);
	} else {					
		ZaApp.getInstance().getHelpViewController().show();
	}

    if (appNewUI) {
        var historyObject = new ZaHistory("HelpView", undefined, undefined, false, new AjxCallback(this, this._helpListener));
        this._historyMgr.addHistory(historyObject);
    }
}

ZaZimbraAdmin.prototype._dwListener = 
function (ev) {
	//DBG.println(AjxDebug.DBG1, "Download is clicked ...") ;
	if(ZaApp.getInstance().getCurrentController()) {
		ZaApp.getInstance().getCurrentController().switchToNextView(ZaApp.getInstance().getMigrationWizController(), ZaMigrationWizController.prototype.show, null);
	} else {					
		ZaApp.getInstance().getMigrationWizController().show();
	}
}

ZaZimbraAdmin.prototype._createBanner =
function() {
	var logoContainer = document.getElementById(ZaSettings.SKIN_LOGO_DOM_ID);
	if(!logoContainer) {
		return;
	}
	// The LogoContainer style centers the logo
	var banner = new DwtComposite(this._shell, "LogoContainer", Dwt.ABSOLUTE_STYLE);
	var html = new Array();
	var i = 0;
	html[i++] = "<a href='";
	html[i++] = ZaAppCtxt.getLogoURI ();
	html[i++] = "' target='_blank'><div  class='"+AjxImg.getClassForImage("AppBanner")+"'></div></a>";
	banner.getHtmlElement().innerHTML = html.join("");
	return banner;
}

ZaZimbraAdmin.prototype._createLogOff =
function () {
    var logoffMethod ;
    if (ZaSettings.isYahooSmbPADomainAdmin)   {
        logoffMethod = "SMBAccount.logOff();"
    }   else {
        logoffMethod = "ZaZimbraAdmin.logOff();" ;
    }
    
    var logoff = document.getElementById(ZaSettings.SKIN_LOGOFF_DOM_ID);
	if (logoff) {
		logoff.innerHTML = this._getAppLink(logoffMethod, "Logoff",  ZaMsg.logOff);
		logoff.style.cursor = "pointer" ;
	}
}


ZaZimbraAdmin.prototype._getLoginMsgPanel = function () {
    if (!this._loginMsgPanel) {
        this._loginMsgPanel = new DwtComposite (this._shell, null, Dwt.ABSOLUTE_STYLE);

        var loginMsg ;
        try {
            loginMsg = ZaDomain.getLoginMessage() ;
        }catch (ex) {
            this._handleException(ex, "ZaZimbraAdmin.prototype._getLoginMsgPanel", null, true);
        }
        if (loginMsg) {
            var loginMsgEl = new DwtAlert (this._loginMsgPanel, null, Dwt.ABSOLUTE_STYLE) ;

            loginMsgEl.setStyle(DwtAlert.INFORMATION) ;
            loginMsgEl.setContent (loginMsg);

            var dismissBt = new DwtButton({parent: this._loginMsgPanel, className: "DwtToolbarButton"})
            dismissBt.setImage("Close") ;
            dismissBt.setText(ZaMsg.TBB_Close) ;
            dismissBt.addSelectionListener(new AjxListener(this, this.closeLoginMsg)) ;
            loginMsgEl.setDismissContent(dismissBt) ;           
        }else{
            this.closeLoginMsg();
        }
    }
    return this._loginMsgPanel ;
}

ZaZimbraAdmin.prototype.closeLoginMsg = function () {
    //hide the login msg skin component
//    this._loginMsgPanel.setVisible(false)  ;
    var loginMsgPanelId = this._loginMsgPanel.getHTMLElId() ;
    Dwt.hide(loginMsgPanelId) ;
    skin.hideLoginMsg() ;

    //resize the components
    var appViewMgr = ZaApp.getInstance ().getAppViewMgr () ;
    /*var list = [//ZaAppViewMgr.C_LOGIN_MESSAGE,
                ZaAppViewMgr.C_CURRENT_APP, ZaAppViewMgr.C_APP_TABS,
				ZaAppViewMgr.C_TREE,ZaAppViewMgr.C_SASH,
				ZaAppViewMgr.C_TREE_FOOTER ,
				ZaAppViewMgr.C_TOOLBAR_TOP, ZaAppViewMgr.C_APP_CONTENT];
	appViewMgr._stickToGrid(list);
     */
    appViewMgr.fitAll();
	
}

//set the html content for logoff, help and download
//max_lbl_length is used to constrict the maximum length of the label
//which is different in different languages.
ZaZimbraAdmin.prototype._getAppLink =
function(staticFunc, icon, lbl, max_lbl_length) {
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

    if (appNewUI)
        html[i++] = "<td width=1% align=right style='white-space:nowrap;'><span " ;
    else
	    html[i++] = "<td width=1% align=right style='white-space:nowrap; font-weight:bold'><span " ;
	if (staticFunc) {
		html[i++] = " onclick='" + staticFunc + "' " ;
	}

    //if the label is too long, it will be replaced by the
    //label.substr(0, max_length -3) + "..."
    //And a title will also be added, so user will have the chance
    //to view the whole length
    if ((max_lbl_length != null) && max_lbl_length > 0) {
         if (lbl != null && lbl.length > max_lbl_length) {
             var title = lbl ;
             lbl = lbl.substring(0, max_lbl_length -3) + "..." ;
             html[i++] = "title='" + title +"'" ;
         }
    }
    html[i++] = ">";
	html[i++] = lbl;
	html[i++] = "</span></td></tr></table>";
	
	//var cell = document.getElementById(id);
	//if (cell) cell.innerHTML = html.join("");
	return html.join("");
}

/**
* Creates an action status view
**/
ZaZimbraAdmin.prototype._createActionStatus =
function() {
	this.actionStatusView = new ZaActionStatusView(this._shell, "ZaStatus", Dwt.ABSOLUTE_STYLE);
}

/**
 * Displays a status message.
 *
 * @param	{Hash}	params		a hash of parameters
 * @param {String}	params.msg		the message
 * @param {constant}	[params.level] ZaActionStatusView.LEVEL_INFO, ZaActionStatusView.LEVEL_WARNING, or ZaActionStatusView.LEVEL_CRITICAL
 * @param {constant}	[params.detail] 	the details
 * @param {constant}	[params.transitions]		the transitions
 * @param {constant}	[params.toast]		the toast control
 * @param {boolean}     [force]        force any displayed toasts out of the way (dismiss them and run their dismissCallback). Enqueued messages that are not yet displayed will not be displayed
 * @param {AjxCallback}    [dismissCallback]    callback to run when the toast is dismissed (by another message using [force], or explicitly calling ZmStatusView.prototype.dismiss())
 * @param {AjxCallback}    [finishCallback]     callback to run when the toast finishes its transitions by itself (not when dismissed)
 */
ZaZimbraAdmin.prototype.setActionStatusMsg =
function(params) {
    if(this.actionStatusView) {
	    params = Dwt.getParams(arguments, ZaActionStatusView.MSG_PARAMS);
	    this.actionStatusView.setStatusMsg(params);
    }
};
// Private methods

ZaZimbraAdmin._killSplash =
function() {
    //if(window.console && window.console.log) console.log("Killing splash window now ...") ;
    if(ZaZimbraAdmin._splashScreen)
		ZaZimbraAdmin._splashScreen.setVisible(false);
}

ZaZimbraAdmin.showSplash =
function(shell) {
	if(ZaZimbraAdmin._splashScreen)
		ZaZimbraAdmin._splashScreen.setVisible(true);
	else {
		ZaZimbraAdmin._splashScreen = new ZaSplashScreen(shell);
		ZaZimbraAdmin._splashScreen.setVisible(true);
	}
}



/**
* Creates an app object, which doesn't necessarily do anything just yet.
**/
ZaZimbraAdmin.prototype._createApp =
function() {
	this._app = ZaApp.getInstance(this._appCtxt, this._shell);
		
}

ZaZimbraAdmin.prototype.getHisotryMgr =
function() {
	return this._historyMgr;
}

ZaZimbraAdmin.prototype._createHistoryMgr =
function() {
    if (!this._historyMgr)
        this._historyMgr = new ZaHistoryMgr();
}
/**
* Launching an app causes it to create a view (if necessary) and display it. The view that is created is up to the app.
* Since most apps schedule an action as part of their launch, a call to this function should not be
* followed by any code that depends on it (ie, it should be a leaf action).
**/
ZaZimbraAdmin.prototype._launchApp =
function() {
	ZaSettings.TREE_ENABLED = (document.getElementById(ZaSettings.SKIN_TREE_ID)!=null);
	ZaSettings.CURRENT_APP_ENABLED = (document.getElementById(ZaSettings.SKIN_CURRENT_APP_ID)!=null);
	ZaSettings.BANNER_ENABLED = (document.getElementById(ZaSettings.SKIN_LOGO_DOM_ID)!=null);
	ZaSettings.STATUS_ENABLED = (document.getElementById(ZaSettings.SKIN_STATUS_ID)!=null);
	ZaSettings.SEARCH_PANEL_ENABLED = (document.getElementById(ZaSettings.SKIN_SEARCH_PANEL_ID)!=null);
	
    //console.log("Launching ZimbraAdmin Application ....") ;
    if (!this._app)
		this._createApp();

    //recreate the error/msg dialogs
    ZaApp.getInstance().initDialogs();
   // if (ZaZimbraAdmin._LOCALE_MSG_RELOADED) this.initDialogs(true) ;

    this._appCtxt.setClientCmdHdlr(new ZaClientCmdHandler());
    //draw stuff
	var elements = new Object();
		
	//elements[ZaAppViewMgr.C_APP_CHOOSER] = this._createAppChooser();
	


    // the outer element of the entire skin is hidden until this point
	// so that the skin won't flash (become briefly visible) during app loading
	if (skin && skin.show){
		skin.show(true);	
		//hide the advanced search builder at the beginning
		skin.showSearchBuilder(false);  
	}	

	//add logoff
	this._createLogOff();
	this._createHelpLink();
	this._createDownloadLink() ;
	this._setUserName() ;
	
	if(ZaSettings.BANNER_ENABLED) {
		elements[ZaAppViewMgr.C_BANNER] = this._createBanner();
	}
	if(ZaSettings.STATUS_ENABLED) {
		elements[ZaAppViewMgr.C_STATUS] = this._statusBox = new DwtText(this._shell, "statusBox", Dwt.ABSOLUTE_STYLE);
		this.clearStatus();
		this._setLicenseStatusMessage();	
	}
	if(ZaSettings.SEARCH_PANEL_ENABLED) {
        this._createActionStatus();
		elements[ZaAppViewMgr.C_SEARCH_BUILDER_TOOLBAR] = ZaApp.getInstance().getSearchBuilderToolbarController ().getSearchBuilderTBPanel();
		elements[ZaAppViewMgr.C_SEARCH_BUILDER] = ZaApp.getInstance().getSearchBuilderController().getSearchBuilderPanel();
	}
	if(ZaSettings.TREE_ENABLED) {
		elements[ZaAppViewMgr.C_TREE] = this.getOverviewPanelController().getOverviewPanel();
	} 
	if(document.getElementById(ZaSettings.SKIN_APP_SASH_ID)) {
		elements[ZaAppViewMgr.C_SASH] =  new DwtSash({parent:this._shell, style:DwtSash.HORIZONTAL_STYLE,className:"AppSash-horiz", threshold:20, id:"z_sash"});
	}
	if(ZaSettings.SEARCH_PANEL_ENABLED) {
		elements[ZaAppViewMgr.C_SEARCH] = ZaApp.getInstance().getSearchListController().getSearchPanel();
	}
       
       if(document.getElementById (ZaSettings.SKIN_SEARCH_BUILDER_APP_SASH_ID) != null){
            elements[ZaAppViewMgr.C_SEARCH_BUILDER_SASH] = new DwtSash({parent:this._shell, style: DwtSash.VERTICAL_STYLE, className: "AppSash-vert", threshod:20, id:"z_sb_sash"});
	}

	elements[ZaAppViewMgr.C_LOGIN_MESSAGE]  = this._getLoginMsgPanel();
    //Use reparentHtmlelement to add the tabs. Reenable this line if it doesn't work well.
	elements[ZaAppViewMgr.C_APP_TABS] = this._createAppTabs() ;
	if(ZaSettings.CURRENT_APP_ENABLED) {
		elements[ZaAppViewMgr.C_CURRENT_APP] = new ZaCurrentAppToolBar(this._shell);
	}
	this._appViewMgr.addComponents(elements, true);
	
    ZaApp.getInstance().launch();

 	ZaZimbraAdmin._killSplash();
};

ZaZimbraAdmin.prototype.updateHistory =
function(historyObject, isAddHistory) {
    if(isAddHistory)
        this._historyMgr.addHistory(historyObject);

    if (historyObject.displayName)
        this._header.setText(historyObject);

    if (historyObject.path)
        this._currentAppBar.setText(historyObject.path);

}

ZaZimbraAdmin.prototype.refreshHistoryTreeByDelete = function(items) {
    var itemArray = AjxUtil.toArray(items);
    var refresh = false;
    for(var i = 0 ; i <  itemArray.length; i++){
        var itemName = itemArray[i].name;

        var historys = this._historyMgr.findHistoryByName(itemName);
        for(var j = 0; j< historys.size(); j++){
            historys.get(j).setEnabled(false);
            refresh = true;
        }
        this._historyMgr.deleteHistoryObjByName(itemName);
    }
    if(refresh)
        this._historyMgr.refreshHistory();
}

ZaZimbraAdmin.prototype.getSettingMenu =
function(popupOperation, popupOrder) {
    if (!this._currentAppBar)
        return "";

    return this._currentAppBar.getMenu();
}

ZaZimbraAdmin.prototype.getCurrentAppBar =
function () {
    return this._currentAppBar;
}

ZaZimbraAdmin.prototype._lauchNewApp =
function() {
	ZaSettings.TREE_ENABLED = (document.getElementById(ZaSettings.SKIN_TREE_DOM_ID)!=null);
	ZaSettings.BANNER_ENABLED = (document.getElementById(ZaSettings.SKIN_LOGO_DOM_ID)!=null);
    ZaSettings.TOASTER_ENABLED = (document.getElementById(ZaSettings.SKIN_TOASTER_DOM_ID)!=null);

    //console.log("Launching ZimbraAdmin Application ....") ;
    if (!this._app)
		this._createApp();

    // add history mgr
    this._createHistoryMgr();
    //recreate the error/msg dialogs
    ZaApp.getInstance().initDialogs();
   // if (ZaZimbraAdmin._LOCALE_MSG_RELOADED) this.initDialogs(true) ;

    this._appCtxt.setClientCmdHdlr(new ZaClientCmdHandler());
    //draw stuff
	var elements = new Object();

    // the outer element of the entire skin is hidden until this point
	// so that the skin won't flash (become briefly visible) during app loading
	if (skin && skin.show){
		skin.show(true);
	}

	//add logoff
    /*
	this._createLogOff();
	this._createHelpLink();
	this._createDownloadLink() ;
	this._setUserName();
    */
    this._createRefreshLink();
    this._createPreviousLink();
    this._createNextLink();
    this._historyMgr.addChangeListener(new AjxListener(this, this.updatePreNext));
    this._createUserName();
	this._createHelpLink();

	if(ZaSettings.BANNER_ENABLED) {
		elements[ZaAppViewMgr.C_BANNER] = this._createBanner();
	}

    if (ZaSettings.TOASTER_ENABLED) {
        this._createActionStatus();
    }
    elements[ZaAppViewMgr.C_SEARCH] = ZaApp.getInstance().getSearchListController().getSearchPanel();

    this._header = elements[ZaAppViewMgr.C_TREE_TOP] = new ZaCrtAppTreeHeader(this._shell);
	if(ZaSettings.TREE_ENABLED) {
		elements[ZaAppViewMgr.C_TREE] = this.getOverviewPanelController().getOverviewPanel();
	}

    this._currentAppBar = elements[ZaAppViewMgr.C_APP_HEADER] = new ZaCurrentAppBar(this._shell);
    elements[ZaAppViewMgr.C_TOOL_HEADER] = this.getTaskController().getTaskHeaderPanel();
    elements[ZaAppViewMgr.C_TOOL] = this.getTaskController().getTaskContentPanel();
	this._appViewMgr.addComponents(elements, true);
    ZaApp.getInstance().launch();

 	ZaZimbraAdmin._killSplash();
};

ZaZimbraAdmin.noOpAction = null;
ZaZimbraAdmin.noOpHandler = null;
ZaZimbraAdmin.noOpInterval = 120000;

ZaZimbraAdmin.prototype.cancelNoOp = function() {
	if(ZaZimbraAdmin.noOpHandler) {
		AjxTimedAction.cancelAction(this.noOpHandler);
		ZaZimbraAdmin.noOpHandler = null;
	}	
}

ZaZimbraAdmin.prototype.scheduleNoOp = function() {
	if(!ZaZimbraAdmin.noOpAction) {
		ZaZimbraAdmin.noOpAction = new AjxTimedAction(this, this.sendNoOp);
	}
	ZaZimbraAdmin.noOpHandler = AjxTimedAction.scheduleAction(ZaZimbraAdmin.noOpAction, 120000);
};

ZaZimbraAdmin.prototype.sendNoOp = function () {
	try {
		var soapDoc = AjxSoapDoc.create("NoOpRequest", ZaZimbraAdmin.URN, null);
		var noOpCommand = new ZmCsfeCommand();
		var params = new Object();
		params.soapDoc = soapDoc;	
		params.asyncMode = false;
		params.noAuthToken = true;
		noOpCommand.invoke(params);
		this.scheduleNoOp();
	} catch (ex) {
		this._handleException(ex, "ZaZimbraAdmin.prototype.sendNoOp", null, true);
		this.cancelNoOp();
	}
}

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

ZaZimbraAdmin._confirmAuthInvalidExitMethod =
function () {
    var msg = ZaMsg.authInvalidExitWarning ;
    return msg ;
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
    if (ZaZimbraAdmin.isWarnOnExit) { //hack is only needed when we are set to warn on exit
        ZaZimbraAdmin.setOnbeforeunload (null) ;
        var f = function() { ZaZimbraAdmin.setOnbeforeunload(ZaZimbraAdmin._confirmExitMethod); };
        var t = new AjxTimedAction(null, f);
        AjxTimedAction.scheduleAction(t, 3000);
    }
};

ZaZimbraAdmin.isGlobalAdmin = function () {
    return (ZaZimbraAdmin.currentAdminAccount 
            && ZaZimbraAdmin.currentAdminAccount.attrs
            && (ZaZimbraAdmin.currentAdminAccount.attrs[ZaAccount.A_zimbraIsAdminAccount] == 'TRUE'));
}

ZaZimbraAdmin.hasGlobalDomainListAccess = function () {
    return (ZaZimbraAdmin.isGlobalAdmin() || ZaDomain.globalRights[ZaDomain.RIGHT_LIST_DOMAIN]);
}

ZaZimbraAdmin.hasGlobalCOSSListAccess = function () {
	return (ZaZimbraAdmin.isGlobalAdmin() || ZaCos.globalRights[ZaCos.RIGHT_LIST_COS]);
}

ZaAboutDialog = function(parent, className, title, w, h) {
	if (arguments.length == 0) return;
 	var clsName = className || "DwtDialog AboutScreen";
 	DwtDialog.call(this, parent, clsName,  ZabMsg.about_title, [DwtDialog.OK_BUTTON]);
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
    params.companyURL = ZaAppCtxt.getLogoURI () ;
    params.showLongVersion = true;
    params.longVersion = AjxBuffer.concat(ZaMsg.splashScreenVersion, " ", ZaServerVersionInfo.version , " " , date);
    params.copyrightText = ZaItem.getSplashScreenCopyright();
    var html = ZLoginFactory.getLoginDialogHTML(params);
    this.setContent(html);

 	DwtBaseDialog.prototype.popup.call(this);
};

ZaAboutDialog.prototype.popdown =
function() {
 	DwtBaseDialog.prototype.popdown.call(this);
    this.setContent("");
};
