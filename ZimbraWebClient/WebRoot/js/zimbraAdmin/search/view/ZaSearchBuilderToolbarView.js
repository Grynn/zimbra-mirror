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
	this._ops.push(new ZaOperation (ZaOperation.CLOSE, ZaMsg.TBB_Close, ZaMsg.ALTBB_Close_tt, "Close", "CloseDis", new AjxListener(this, this.closeHndlr)));   
	ZaToolBar.call(this, parent, this._ops);
	
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
	DBG.println (AjxDebug.DBG1, "Close ... ");
	
	this._controller.toggleVisible ();
	this._app._appViewMgr.showSearchBuilder (this._controller.isSBVisible());
	
	//clear the search field
	this._controller.setQuery ();
}