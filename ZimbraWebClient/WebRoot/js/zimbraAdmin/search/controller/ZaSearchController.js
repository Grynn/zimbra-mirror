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

function ZaSearchController(appCtxt, container) {

	ZaController.call(this, appCtxt, container);
	this._inited = false;
	this._currentSearch = null;
	this._app = appCtxt.getAppController().getApp(ZaZimbraAdmin.ADMIN_APP);
	this._setView();    
}

ZaSearchController.prototype = new ZaController;
ZaSearchController.prototype.constructor = ZaSearchController;

ZaSearchController._TOOLBAR_SEPARATION = 2;

ZaSearchController.prototype.toString = 
function() {
	return "ZaSearchController";
}

ZaSearchController.prototype.getSearchPanel =
function() {
	return this._searchPanel;
}

ZaSearchController.prototype.setSearchField =
function(searchString) {
	this._searchField.setValue(searchString);
}

ZaSearchController.prototype.getSearchField =
function() {
	return this._searchToolBar.getSearchField().getValue();
}

ZaSearchController.prototype.search =
function(searchString, sortBy, offset, limit) {

	// if the search string starts with "$set:" then it is a command to the client 
	if (searchString.indexOf("$set:") == 0) {
		this._appCtxt.getClientCmdHdlr().execute((searchString.substr(5)).split(" "));
		return;
	}
	
	this._searchField.setValue(searchString);
	this._currentSearch = searchString;
	this._searchField.setEnabled(false);
	this._searchField.setFieldChanged(false);		
	this._schedule(this._doSearch, {sortBy: sortBy, offset: offset, limit: limit});
}

ZaSearchController.prototype.setEnabled =
function(enabled) {
	this._searchField.setEnabled(enabled);
}

ZaSearchController.prototype._setView =
function() {
    this._searchPanel = new DwtComposite(this._container, "SearchPanel", DwtControl.ABSOLUTE_STYLE);
    
	// Create search toolbar and setup browse tool bar button handlers
	this._searchToolBar = new ZaSearchToolBar(this._searchPanel);
	this._searchToolBar.setLocation(0, 0);
	this._searchPanel.setBounds(0, 0, Dwt.DEFAULT, this._searchToolBar.getSize().y);
   	this._createBannerBar();
    
    // Search By tool bar button/menu item handlers
    var searchForListener = new AjxListener(this, ZaSearchController.prototype._searchForButtonListener);
	// Setup search field handler
	this._searchField = this._searchToolBar.getSearchField();
	this._searchField.registerCallback(ZaSearchController.prototype._searchFieldCallback, this);	
	this._searchPanel.zShow(true);
}

// Creates buttons for general non app-related functions and puts them on the banner.
ZaSearchController.prototype._createBannerBar =
function() {

	this.bannerBar = new DwtComposite(this._searchPanel, "BannerBar", DwtControl.RELATIVE_STYLE);
	
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

	this.createBannerBarHtml();
}
ZaSearchController.prototype.createBannerBarHtml =
function () {

	if(!this.bannerBar || !this.bannerBar._helpId ||  !this.bannerBar._pdfHelpId || !this.bannerBar._logOffId || !this.bannerBar._helpId2 ||  !this.bannerBar._pdfHelpId2 || !this.bannerBar._logOffId2)
		return;
		
	var html = new Array();
	var i = 0;

	html[i++] = "<table align='right' id='" + this._bannerTableId + "'><tr>";
	html[i++] = "<td><a id='" + this.bannerBar._migrationId + "'  target=\"_blank\" href=\"http://zimbra.com/downloads/migrationwizard/accept\">";
	html[i++] = AjxImg.getImageHtml("MigrationWiz", "cursor:hand");
	html[i++] = "</a></td>";
	html[i++] = "<td><a id='" + this.bannerBar._migrationId2 + "' style='cursor: hand' target=\"_blank\" href=\"http://zimbra.com/downloads/migrationwizard/accept\">";
	html[i++] = ZaMsg.migrationWiz + "</a></td>";	

	html[i++] = "<td><a id='" + this.bannerBar._helpId + "'>";
	html[i++] = AjxImg.getImageHtml("Help", "cursor:hand");
	html[i++] = "</a></td>";
	html[i++] = "<td><a id='" + this.bannerBar._helpId2 + "'>";
	html[i++] = ZaMsg.help + "</a></td>";		

	html[i++] = "<td><a id='" + this.bannerBar._logAboutId + "'>";
	html[i++] = AjxImg.getImageHtml("ZimbraIcon", "cursor:hand");
	html[i++] = "</a></td>";
	html[i++] = "<td><a id='" + this.bannerBar._logAboutId2 + "'>";
	html[i++] = ZaMsg.about + "</a></td>";		

	html[i++] = "<td><a id='" + this.bannerBar._pdfHelpId + "' target=\"_blank\" href=\"/zimbraAdmin/adminhelp/pdf/admin.pdf\">";
	html[i++] = AjxImg.getImageHtml("PDFDoc", "cursor:hand");
	html[i++] = "</a></td>";	
	html[i++] = "<td><a id='" + this.bannerBar._pdfHelpId2 + "' target=\"_blank\" href=\"/zimbraAdmin/adminhelp/pdf/admin.pdf\">";
	html[i++] = ZaMsg.adminGuide + "</a></td>";	

	html[i++] = "<td><a id='" + this.bannerBar._logOffId + "'>";
	html[i++] = AjxImg.getImageHtml("Logoff", "cursor:hand");		
	html[i++] = "</a></td>";
	html[i++] = "<td><a id='" + this.bannerBar._logOffId2 + "'>";		
	html[i++] = ZaMsg.logOff + "</a></td></tr></table>";
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
ZaSearchController.prototype._doSearch =
function(params) {
DBG.dumpObj(params);
	try {
		this._inited = true;
		this._searchField.setEnabled(true);	
		//
		var szQuery = ZaAccount.getSearchByNameQuery(this._currentSearch);
		this._app.getAccountListController().setPageNum(1);					
		if(this._app.getCurrentController()) {
			this._app.getCurrentController().switchToNextView(this._app.getAccountListController(), ZaAccountListController.prototype.show,ZaAccount.search(szQuery, "1", ZaAccount.A_uid, true, this._app));
		} else {					
			this._app.getAccountListController().show(ZaAccount.search(szQuery, "1", ZaAccount.A_uid, true, this._app));
		}
		var curQuery = new ZaAccountQuery(szQuery, false, "");							
		this._app.getAccountListController().setQuery(curQuery);	
	} catch (ex) {
		// Only restart on error if we are not initialized and it isn't a parse error
		if (ex.code != ZmCsfeException.MAIL_QUERY_PARSE_ERROR) {
			this._handleException(ex, ZaSearchController.prototype._doSearch, null, (this._inited) ? false : true);
		} else {
			this.popupMsgDialog(ZaMsg.queryParseError, ex);
			this._searchField.setEnabled(true);	
		}
	}
}


/*********** Search Field Callback */

ZaSearchController.prototype._searchFieldCallback =
function(searchField, queryString) {
	this.search(queryString);
}

/*********** Search Bar Callbacks */

// needed for ZaAppViewMgr
ZaSearchController.prototype.getBrowseView =
function() {
	return null;
}
