/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2006, 2007, 2008, 2009, 2010 VMware, Inc.
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
* @class ZaSearchBuilderToolbarView
* @contructor ZaSearchBuilderToolbarView
* Class to create the advance search options toolbar view
* @author Charles Cao
**/
ZaSearchBuilderToolbarView = function(parent){
	//toolbar operations
	this._toolbarOperations = {} ;
	this._toolbarOrder = [];
	this._toolbarOperations[ZaOperation.SEARCH_BY_BASIC] = new ZaOperation(ZaOperation.SEARCH_BY_BASIC, ZaMsg.searchByBasic, ZaMsg.tt_searchByBasic, "SearchAll", "SearchAll", new AjxListener(this, this.basicTypeSelectHndlr));
	this._toolbarOperations[ZaOperation.SEARCH_BY_ADDESS_TYPE] = new ZaOperation(ZaOperation.SEARCH_BY_ADDESS_TYPE, ZaMsg.searchByAddressType, ZaMsg.tt_searchByAddressType, "SearchAll", "SearchAll", new AjxListener(this, this.objTypeSelectHndlr));
	//if (!ZaSettings.isDomainAdmin) { //hide domain and server feature for the domain admin
		DBG.println(AjxDebug.DBG1, "Domain Admin - No advanced cross domain or server search");
		this._toolbarOperations [ZaOperation.SEARCH_BY_DOMAIN] = new ZaOperation(ZaOperation.SEARCH_BY_DOMAIN, ZaMsg.searchByDomain, ZaMsg.tt_searchByDomain, "Domain", "DomainDis", new AjxListener(this, this.domainSelectHndlr));
		this._toolbarOperations [ZaOperation.SEARCH_BY_SERVER] = new ZaOperation(ZaOperation.SEARCH_BY_SERVER, ZaMsg.searchByServer, ZaMsg.tt_searchByServer, "Server", "ServerDis", new AjxListener(this, this.serverSelectHndlr));
	//}
	this._toolbarOperations [ZaOperation.SEARCH_BY_ADVANCED] = new ZaOperation(ZaOperation.SEARCH_BY_ADVANCED, ZaMsg.searchByAdvanced, ZaMsg.tt_searchByAdvanced, "SearchAll", "SearchAll", new AjxListener(this, this.advancedSelectHndlr));
	this._toolbarOperations [ZaOperation.SEP] = new ZaOperation(ZaOperation.SEP);
	this._toolbarOperations [ZaOperation.SEARCH_BY_REMOVE_ALL] = new ZaOperation(ZaOperation.SEARCH_BY_REMOVE_ALL, ZaMsg.searchByRemoveAll, ZaMsg.tt_searchByRemoveAll, null, null, new AjxListener(this, this.removeSelectHndlr), null, null, "ZaSearchBuilderOptionRemoveAll");
	this._toolbarOperations[ZaOperation.SEARCH_BY_COS] = new ZaOperation(ZaOperation.SEARCH_BY_COS, ZaMsg.searchByCOS, ZaMsg.tt_searchByCOS, "COS", "COS", new AjxListener(this, this.cosSelectHndlr));
	this._toolbarOperations [ZaOperation.NONE] = new ZaOperation(ZaOperation.NONE);
	this._toolbarOperations [ZaOperation.CLOSE] = new ZaOperation (ZaOperation.CLOSE, ZaMsg.TBB_Close, ZaMsg.tt_advanced_search_close, "Close", "CloseDis", new AjxListener(this, this.closeHndlr));
	
	this._toolbarOrder.push(ZaOperation.SEARCH_BY_BASIC);
	
	if(ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.CARTE_BLANCHE_UI] 
		|| ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.ACCOUNT_LIST_VIEW]
		|| ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.DL_LIST_VIEW]
		|| ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.ALIAS_LIST_VIEW]
		|| ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.RESOURCE_LIST_VIEW])
		this._toolbarOrder.push(ZaOperation.SEARCH_BY_ADDESS_TYPE);
	
	if(ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.DOMAIN_LIST_VIEW] || ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.CARTE_BLANCHE_UI])
		this._toolbarOrder.push(ZaOperation.SEARCH_BY_DOMAIN);
	
	if(ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.SERVER_LIST_VIEW] || ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.CARTE_BLANCHE_UI])
		this._toolbarOrder.push(ZaOperation.SEARCH_BY_SERVER);
		
	this._toolbarOrder.push(ZaOperation.SEARCH_BY_ADVANCED);
        this._toolbarOrder.push(ZaOperation.SEARCH_BY_COS);
	this._toolbarOrder.push(ZaOperation.SEP);
	this._toolbarOrder.push(ZaOperation.SEARCH_BY_REMOVE_ALL);
	this._toolbarOrder.push(ZaOperation.NONE);
	this._toolbarOrder.push(ZaOperation.CLOSE);	
	ZaToolBar.call(this, parent, this._toolbarOperations, this._toolbarOrder, null, "ZaSearchBuilderToolBar" );
	
	
	this._app = ZaApp.getInstance();
	this.zShow(false);
	//this.setVisible (false);

	this._controller = ZaApp.getInstance().getSearchBuilderController () ;
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

ZaSearchBuilderToolbarView.prototype.advancedSelectHndlr =
function (event) {
	//if(window.console && window.console.log) console.log("Advanced Attributes search builder button is clicked.") ;
	this._controller.addOptionView (ZaSearchOption.ADVANCED_ID) ;
	//this._controller.listAll
}

ZaSearchBuilderToolbarView.prototype.cosSelectHndlr =
function (event) {
        this._controller.addOptionView(ZaSearchOption.COS_ID);
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
	ZaApp.getInstance()._appViewMgr.showSearchBuilder (this._controller.isSBVisible());
	
	//clear the search field
	this._controller.setQuery ();
	
	//reset the advanced search button tooltip
	ZaApp.getInstance().getSearchListController()._searchField.setTooltipForSearchBuildButton (ZaMsg.tt_advanced_search_open);
}
