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
 * Portions created by Zimbra are Copyright (C) 2006 Zimbra, Inc.
 * All Rights Reserved.
 * 
 * Contributor(s):
 * 
 * ***** END LICENSE BLOCK *****
 */
 
/**
* @class ZaSearchBuilderToolbarView
* @contructor ZaSearchBuilderToolbarView
* Class to create the advance search options toolbar view
* @author Charles Cao
**/
function ZaSearchBuilderToolbarView (parent, app){
	//toolbar operations
	this._ops = [] ;
	this._ops.push(new ZaOperation(ZaOperation.SEARCH_BY_BASIC, ZaMsg.searchByBasic, ZaMsg.tt_searchByBasic, "SearchAll", "SearchAll", new AjxListener(this, this.basicTypeSelectHndlr)));
	this._ops.push(new ZaOperation(ZaOperation.SEARCH_BY_ADDESS_TYPE, ZaMsg.searchByAddressType, ZaMsg.tt_searchByAddressType, "SearchAll", "SearchAll", new AjxListener(this, this.objTypeSelectHndlr)));
	if (!ZaSettings.isDomainAdmin) { //hide domain and server feature for the domain admin
		DBG.println(AjxDebug.DBG1, "Domain Admin - No advanced cross domain or server search");
		this._ops.push(new ZaOperation(ZaOperation.SEARCH_BY_DOMAIN, ZaMsg.searchByDomain, ZaMsg.tt_searchByDomain, "Domain", "DomainDis", new AjxListener(this, this.domainSelectHndlr)));
		this._ops.push(new ZaOperation(ZaOperation.SEARCH_BY_SERVER, ZaMsg.searchByServer, ZaMsg.tt_searchByServer, "Server", "ServerDis", new AjxListener(this, this.serverSelectHndlr)));
	}
	this._ops.push(new ZaOperation(ZaOperation.SEP));
	this._ops.push(new ZaOperation(ZaOperation.SEARCH_BY_REMOVE_ALL, ZaMsg.searchByRemoveAll, ZaMsg.tt_searchByRemoveAll, null, null, new AjxListener(this, this.removeSelectHndlr)));
	this._ops.push(new ZaOperation(ZaOperation.NONE));
	this._ops.push(new ZaOperation (ZaOperation.CLOSE, ZaMsg.TBB_Close, ZaMsg.tt_advanced_search_close, "Close", "CloseDis", new AjxListener(this, this.closeHndlr)));   
	
	ZaToolBar.call(this, parent, this._ops, null, AjxEnv.isIE ? null : "ZaSearchBuilderToolBar" );
	
	
	this._table.width = "100%";
	this._app = app;
	this.zShow(false);
	//this.setVisible (false);
	this._app = app;
	this._controller = this._app.getSearchBuilderController () ;
	//this._searchBuilder
	this._view = {};
}

ZaSearchBuilderToolbarView.prototype = new ZaToolBar ;
ZaSearchBuilderToolbarView.prototype.constructor = ZaSearchBuilderToolbarView;

ZaSearchBuilderToolbarView.prototype.toString = 
function() {
	return "ZaSearchBuilderToolbarView";
}

ZaSearchBuilderToolbarView.prototype.basicTypeSelectHndlr =
function (event) {
	//DBG.println (AjxDebug.DBG1, "Object Type Selected ... ");	
	this._controller.addOptionView(ZaSearchOption.BASIC_TYPE_ID);
}

ZaSearchBuilderToolbarView.prototype.objTypeSelectHndlr =
function (event) {
	//DBG.println (AjxDebug.DBG1, "Object Type Selected ... ");	
	this._controller.addOptionView(ZaSearchOption.OBJECT_TYPE_ID);
	
	//disable the button since we only allow to add one time
	event.item.setEnabled (false);
}

ZaSearchBuilderToolbarView.prototype.domainSelectHndlr =
function (event) {
	//DBG.println (AjxDebug.DBG1, "Domain Selected ... ");	
	this._controller.addOptionView(ZaSearchOption.DOMAIN_ID);
	
}

ZaSearchBuilderToolbarView.prototype.serverSelectHndlr =
function (event) {
	//DBG.println (AjxDebug.DBG1, "Server Selected ... ");	
	this._controller.addOptionView(ZaSearchOption.SERVER_ID);
	this._controller.listAllServers ();
	//disable the button since we show all the server at one time
	event.item.setEnabled (false);
}

ZaSearchBuilderToolbarView.prototype.removeSelectHndlr =
function (event) {
	//DBG.println (AjxDebug.DBG1, "Server Selected ... ");	
	//1) clear all the selections
	this._controller.removeAllOptionViews();
	this._controller.setQuery();
}

//clear all the options and close the search builder view
ZaSearchBuilderToolbarView.prototype.closeHndlr =
function (event) {
	DBG.println (AjxDebug.DBG3, "Close ... ");
	
	this._controller.toggleVisible ();
	this._app._appViewMgr.showSearchBuilder (this._controller.isSBVisible());
	
	//clear the search field
	this._controller.setQuery ();
	
	//reset the advanced search button tooltip
	this._app.getSearchListController()._searchField.setTooltipForSearchBuildButton (ZaMsg.tt_advanced_search_open);
}