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

ZaSearchField = function(parent, className, size, posStyle, id) {

	DwtComposite.call(this, parent, className, posStyle, null, ZaId.getViewId(ZaId.SEARCH_VIEW,null,id));
	this._containedObject = new ZaSearch();
	this._initForm(ZaSearch.myXModel,this._getMyXForm());
	this._localXForm.setInstance(this._containedObject);
	this._app = ZaApp.getInstance();
	this._searchFieldId = id;
}

ZaSearchField.prototype = new DwtComposite;
ZaSearchField.prototype.constructor = ZaSearchField;

ZaSearchField.prototype.toString = 
function() {
	return "ZaSearchField";
}

ZaSearchField.UNICODE_CHAR_RE = /\S/;

ZaSearchField.prototype.registerCallback =
function(callbackFunc, obj) {
	this._callbackFunc = callbackFunc;
	this._callbackObj = obj;
}

ZaSearchField.prototype.setObject = 
function (searchObj) {
	this._containedObject = searchObj;
	this._localXForm.setInstance(this._containedObject);
}

ZaSearchField.prototype.getObject = 
function() {
	return this._containedObject;
}


ZaSearchField.prototype.invokeCallback =
function() {
    var query = this._containedObject[ZaSearch.A_query] = this.getSearchFieldElement().value;
	if (query.indexOf("$set:") == 0) {
		ZaApp.getInstance().getAppCtxt().getClientCmdHdlr().execute((query.substr(5)).split(" "));
		return;
	}
		
	var params = {};
	var sb_controller = ZaApp.getInstance().getSearchBuilderController();
	var isAdvanced = sb_controller.isAdvancedSearch (query) ;
	var searchListController = ZaApp.getInstance().getSearchListController() ;
	searchListController._isAdvancedSearch = isAdvanced ;

	// reset search controller
	searchListController._currentDomain = null;
	searchListController._currentPageNum = 1;
	searchListController.fetchAttrs = ZaSearch.standardAttributes;
	
	params.types = this.getSearchTypes();
	
	if (isAdvanced) {
		DBG.println(AjxDebug.DBG1, "Advanced Search ... " ) ;
		//Use the text in the search field to do a search
		//params.query = sb_controller.getQuery ();
		params.query = query;
		DBG.println(AjxDebug.DBG1, "Query = " + params.query) ;
		//params.types = sb_controller.getAddressTypes ();
	}else {
		DBG.println(AjxDebug.DBG1, "Basic Search ....") ;
		searchListController._searchFieldInput = query ;
		params.query = ZaSearch.getSearchByNameQuery(query, params.types);      
	}
	
	//set the currentController's _currentQuery
	
	ZaApp.getInstance().getSearchListController()._currentQuery = params.query ;
	searchListController._currentQuery = params.query ;

	this._isSearchButtonClicked = false ;
	
	if (this._callbackFunc != null) {
		if (this._callbackObj != null) {
			//this._callbackFunc.call(this._callbackObj, this, params);
			ZaApp.getInstance().getCurrentController().switchToNextView(this._callbackObj,
		 this._callbackFunc, params);
		} else {
			ZaApp.getInstance().getCurrentController().switchToNextView(ZaApp.getInstance().getSearchListController(), this._callbackFunc, params);
//			this._callbackFunc(this, params);
		}
	}
}

ZaSearchField.isLDAPQuery =
function (query) {
	var regEx =  /\([^\(\)\=]+=[^\(\)\=]+\)/ ; //ldap query string regEx
	if (query.match(regEx) != null) {
		return true ;
	}

	return  false ;
}

ZaSearchField.prototype.startSearch= function (ldapQuery, type) {
	var params = {};

    if (!ldapQuery) {
        ldapQuery = "";
    }
	var searchListController = ZaApp.getInstance().getSearchListController() ;
	searchListController._isAdvancedSearch = true ;
    if (!type)
        type = ZaSearchOption.getDefaultObjectTypes();

	// reset search controller
	searchListController._currentDomain = null;
	searchListController._currentPageNum = 1;
	searchListController.fetchAttrs = ZaSearch.standardAttributes;

	params.types = type;
    if (!ZaSearchField.isLDAPQuery(ldapQuery)){
        ldapQuery = ZaSearch.getSearchByNameQuery(ldapQuery, type);
    }
    params.query = ldapQuery;

	//set the currentController's _currentQuery

	ZaApp.getInstance().getSearchListController()._currentQuery = params.query ;
	searchListController._currentQuery = params.query ;


	if (this._callbackFunc != null) {
		if (this._callbackObj != null) {
			//this._callbackFunc.call(this._callbackObj, this, params);
			ZaApp.getInstance().getCurrentController().switchToNextView(this._callbackObj,
		 this._callbackFunc, params);
		} else {
			ZaApp.getInstance().getCurrentController().switchToNextView(ZaApp.getInstance().getSearchListController(), this._callbackFunc, params);
//			this._callbackFunc(this, params);
		}
	}
}

ZaSearchField.prototype.getSearchTypes =
function () {
		var sb_controller = ZaApp.getInstance().getSearchBuilderController();
		var query = this.getSearchFieldElement().value ;
		var isAdvancedSearch = sb_controller.isAdvancedSearch (query) ;
		
		var objList = new Array();
		if (isAdvancedSearch) {
			objList = sb_controller.getAddressTypes();
		}else{
            if (ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.ACCOUNT_LIST_VIEW] || ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.CARTE_BLANCHE_UI]) {
                if(this._containedObject[ZaSearch.A_fAccounts] == "TRUE") {
                    objList.push(ZaSearch.ACCOUNTS);
                }
            }
            
            if (ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.ALIAS_LIST_VIEW] || ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.CARTE_BLANCHE_UI]) {
                if(this._containedObject[ZaSearch.A_fAliases] == "TRUE") {
                    objList.push(ZaSearch.ALIASES);
                }
            }

            if (ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.DL_LIST_VIEW] || ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.CARTE_BLANCHE_UI]) { 
            	if(this._containedObject[ZaSearch.A_fdistributionlists] == "TRUE") {
                    objList.push(ZaSearch.DLS);
                }
            }

            if (ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.RESOURCE_LIST_VIEW] || ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.CARTE_BLANCHE_UI]) { 
                if(this._containedObject[ZaSearch.A_fResources] == "TRUE") {
                    objList.push(ZaSearch.RESOURCES);
                }
            }
            if (ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.DOMAIN_LIST_VIEW] || ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.CARTE_BLANCHE_UI]) { 
				if(this._containedObject[ZaSearch.A_fDomains] == "TRUE") {
					objList.push(ZaSearch.DOMAINS);
				}	
			}

            if (ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.COS_LIST_VIEW] || ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.CARTE_BLANCHE_UI]) {
                                if(this._containedObject[ZaSearch.A_fCoses] == "TRUE") {
                                        objList.push(ZaSearch.COSES);
                                }
            }
		}
		
		return objList;
}

ZaSearchField.srchButtonHndlr = 
function(evt) {	
	var fieldObj = this.getForm().parent;
	//reset the search list toolbar parameters

	
	var currentController = ZaApp.getInstance().getCurrentController ();
	if (currentController && currentController.setPageNum) {
		currentController.setPageNum (1) ;		
	}

    fieldObj.setCurrentSavedSearch({});
    if (appNewUI) {
        var overviewController =  ZaZimbraAdmin.getInstance().getOverviewPanelController();
        var tree = overviewController.getOverviewPanel().getFolderTree();
        /*
        It will introduce many issues when renaming for this items.
        Fix it in future
        var searchText = ZaMsg.OVP_search;
        var newPath = tree.renameTreeItem(overviewController.getSearchItemPath(), searchText);
        */
        var newPath = overviewController.getSearchItemPath();
        overviewController.setSearchItemPath(newPath);
        tree.setSelectionByPath(newPath);
        return;
    }
	//fieldObj._isSearchButtonClicked = true ; //to Distinguish the action from the overveiw tree items
	fieldObj.invokeCallback(evt);

}

ZaSearchField.entryKeyHandler =
function(xformItem, value) {
    ZaSearchField.srchButtonHndlr.call(xformItem);
}

ZaSearchField.helpSrchButtonHndlr =
function (evt) {
	var helpQuery = this.getForm().getItemsById(ZaSearch.A_query)[0].getElement().value ;
	if (helpQuery && helpQuery.length > 0){
			var url = "http://support.zimbra.com/help/index.php"
			var args = [];
			args.push("query=" + helpQuery) ;
			if (typeof (ZaLicense) == typeof (_UNDEFINED_)) { //FOSS version
				args.push("FOSS=1") ;
			}
			
			if (ZaServerVersionInfo.version) {
				args.push("version=" + ZaServerVersionInfo.version ) ;
			}
			
			url = url + "?" + AjxStringUtil.urlEncode(args.join("&"));
			window.open(url, "_blank");
	}
}

ZaSearchField.saveSrchButtonHndlr =
function (evt) {
	var form =this.getForm() ;
	var searchField = form.parent ;
	var query = form.getItemsById(ZaSearch.A_query)[0].getElement().value ;
	/*if(window.console && window.console.log) {
		console.log("Save current query: " + query) ;
		//console.log("Current Search types = " + searchField.getSearchTypes()) ;
	}*/
	if (query && query.length > 0) {
		searchField.getSaveAndEditSeachDialog().show(null, query) ;
	}
}

ZaSearchField.prototype.getSaveAndEditSeachDialog =
function() {
	if (!this._savedAndEditSearchDialog) {
        this._savedAndEditSearchDialog = new ZaSaveSearchDialog (this) ;
	}
	
	return this._savedAndEditSearchDialog ;
}

ZaSearchField.prototype.showSavedSearchButtonHndlr =
function (evt) {
	//if(window.console && window.console.log) console.log("Show saved Searches") ;
	var searchField = this.getForm().parent ;
	searchField.showSavedSearchMenus() ;
}

ZaSearchField.prototype.showSavedSearchMenus =
function () {
	//if (this._savedSearchMenu) this._savedSearchMenu.popdown(); //force popdown
	
	if (this._savedSearchMenu && this._savedSearchMenu.isPoppedUp()) {
		return ;
	}
	if (ZaSearch.SAVED_SEARCHES.length <= 0 || ZaSearch._savedSearchToBeUpdated) {
		var callback = new AjxCallback (this, this.popupSavedSearch) ;
		ZaSearch.getSavedSearches(null, callback); //TODO, we may want to provide the autocomplete feature to return the saved results when user is typing
	}else{
		this.popupSavedSearch(null);
	}
}

ZaSearchField.prototype.popupSavedSearch =
function (resp, searchName) {
	//if(window.console && window.console.log) console.debug("popup saved searches ...") ;
	
	if (resp){
		ZaSearch.updateSavedSearch (resp);
	}
	
	if (ZaSearch.SAVED_SEARCHES.length <=0) {
		if (this._savedSearchMenu) this._savedSearchMenu.popdown() ; //force popdown if the saved-search is 0
		return ;
	}	
	
	this._queryFieldElement = this.getSearchFieldElement();
	var b = Dwt.getBounds(this._queryFieldElement);
	
	/*
	if (!this._savedSearchMenu || resp != null) {
		this._savedSearchMenu = new DwtMenu(this);
		
		//add the menu items
		for (var i=0; i < ZaSearch.SAVED_SEARCHES.length; i ++) {
			var n = ZaSearch.SAVED_SEARCHES[i].name ;
			var q = ZaSearch.SAVED_SEARCHES[i].query ;
			var mItem =  new DwtMenuItem (this._savedSearchMenu) ;
			mItem.setText(n + " .......... " + q) ;
			mItem.setSize(b.width) ;
			mItem.addSelectionListener(new AjxListener(this, ZaSearchField.prototype.selectSavedSearch, [n, q]));
			mItem.addListener(DwtEvent.ONMOUSEUP, new AjxListener(this, this._savedSearchItemMouseUpListener, [n, q] ));
		}
	}*/
	this.getSavedSearchMenu().popup(0, b.x, b.y + b.height);
	//this._savedSearchMenu.setBounds( b.x, b.y + b.height, b.width);
	
}

ZaSearchField.prototype.getSearchFieldElement =
function () {
    if (!appNewUI)  {
	    return this._localXForm.getItemsById(ZaSearch.A_query)[0].getElement();
    }  else {
        var queryXFormItem = this._localXForm.getItemsById(ZaSearch.A_query)[0];
        var displayId = queryXFormItem.getId() + "_display";
        var inputEl = document.getElementById(displayId);
        return inputEl;
    }
}

ZaSearchField.prototype.selectSavedSearch =
function (name, query, event){
	//if(window.console && window.console.log) console.debug("Item " + name + " is selected - " + query);
    var queryString = ZaSearch.parseSavedSearchQuery(query) ;
    if (!appNewUI) {
        this.getSearchFieldElement().value = queryString;
        this.invokeCallback() ; //do the real search call (simulate the search button click)
    } else {
        this.startSearch(queryString);
    }
}

ZaSearchField.prototype.getSavedSearchActionMenu =
function () {
	if (!this._savedSearchActionMenu) {
		this._popupOperations = [];
		this._popupOperations[ZaOperation.EDIT] = new ZaOperation(ZaOperation.EDIT, ZaMsg.TBB_Edit, ZaMsg.ACTBB_Edit_tt, "Properties", "PropertiesDis", 
				new AjxListener(this, this._editSavedSearchListener));
		this._popupOperations[ZaOperation.DELETE] = new ZaOperation(ZaOperation.DELETE, ZaMsg.TBB_Delete, ZaMsg.ACTBB_Delete_tt, "Delete", "DeleteDis", 
				new AjxListener(this, this._deleteSavedSearchListener));
		this._savedSearchActionMenu = 
			new ZaPopupMenu(this, "ActionMenu", null, this._popupOperations, this._searchFieldId, ZaId.MENU_POP);
	}
	
	return this._savedSearchActionMenu ;
}

ZaSearchField.prototype._savedSearchItemMouseUpListener =
function(name, query, ev) {
	this.getSavedSearchActionMenu().popdown();
	if (ev.button == DwtMouseEvent.RIGHT){
		//if(window.console && window.console.log) console.debug("Right Button of Mouse Up: Item " + name + " is selected - " + query);
		
		this._currentSavedSearch = {name: name, query: query};
		//if(window.console && window.console.log) console.debug("Saved Search Menu ZIndex = " + this._savedSearchMenu.getZIndex());
		this.getSavedSearchActionMenu().popup(0, ev.docX, ev.docY);
		this.getSavedSearchActionMenu().setZIndex(this._savedSearchMenu.getZIndex() + 1) ;
		//if(window.console && window.console.log) console.debug("Saved Search Action Menu ZIndex = " + this.getSavedSearchActionMenu().getZIndex());
	}
}

ZaSearchField.prototype._editSavedSearchListener =
function (ev) {
	//if(window.console && window.console.log) console.debug("Edit a saved search item");
	this._savedSearchActionMenu.popdown();
	this.getSaveAndEditSeachDialog().show(this._currentSavedSearch.name, this._currentSavedSearch.query);
}

ZaSearchField.prototype._deleteSavedSearchListener =
function (ev) {
	//if(window.console && window.console.log) console.debug("Delete a saved search item");
	this._savedSearchActionMenu.popdown();
	ZaSearch._savedSearchToBeUpdated = true ;
	var callback = new AjxCallback (this, this.modifySavedSearchCallback) ;

	ZaSearch.modifySavedSearches(	
		[{name: this._currentSavedSearch.name, query: null}], callback ) ;

}

ZaSearchField.prototype.modifySavedSearchCallback =
function () {
	//update the ZaSearch.SAVED_SEARCH
	ZaSearch.updateSavedSearch (ZaSearch.getSavedSearches()); 

	//Update the Search Tree
	if(ZaSettings.TREE_ENABLED) {
		var overviewPanelCtrl = ZaApp.getInstance()._appCtxt.getAppController().getOverviewPanelController() ;
		overviewPanelCtrl.updateSavedSearchTreeList() ;
	}
	//Update the SavedSearchMenu
	this.updateSavedSearchMenu() ;
}

ZaSearchField.prototype.getSavedSearchMenu =
function (refresh) {
	if (!this._savedSearchMenu  || refresh) {
		this.updateSavedSearchMenu();
	}
	return this._savedSearchMenu ;
}

ZaSearchField.prototype.updateSavedSearchMenu =
function () {
	
	var isPoppedUp = false ;
	this._queryFieldElement = this.getSearchFieldElement();
	var b = Dwt.getBounds(this._queryFieldElement);
	
	if (this._savedSearchMenu) {
		isPopup = this._savedSearchMenu.isPoppedUp();
		this._savedSearchMenu.popdown() ;
		this._savedSearchMenu.dispose();	
	}
	
	this._savedSearchMenu = new DwtMenu({parent:this,id:ZaId.getMenuId(ZaId.PANEL_APPSEARCH,ZaId.MENU_DROP)});
	
	//add the menu items
	for (var i=0; i < ZaSearch.SAVED_SEARCHES.length; i ++) {
		var n = ZaSearch.SAVED_SEARCHES[i].name ;
		var q = ZaSearch.SAVED_SEARCHES[i].query ;
		var mItem =  new DwtMenuItem ({parent:this._savedSearchMenu, id: (ZaId.getMenuItemId(ZaId.SEARCH_QUERY) + "_" + (i+1))}) ;
		mItem.setText(n) ;
		mItem.setSize(b.width) ;
		mItem.addSelectionListener(new AjxListener(this, ZaSearchField.prototype.selectSavedSearch, [n, q]));
		mItem.addListener(DwtEvent.ONMOUSEUP, new AjxListener(this, this._savedSearchItemMouseUpListener, [n, q] ));
		//set the overflow style to hidden
		mItem.getHtmlElement().style.overflow = "hidden";
	}
	
	if (isPoppedUp) this.popupSavedSearch();
}

//only show or hide the advanced search options
ZaSearchField.advancedButtonHndlr =
function (evt) {
	//DBG.println(AjxDebug.DBG1, "Advanced Button Clicked ...") ;
	var form = this.getForm() ;

	var sb_controller = ZaApp.getInstance().getSearchBuilderController ();
	sb_controller.toggleVisible ();
	ZaApp.getInstance()._appViewMgr.showSearchBuilder (sb_controller.isSBVisible());
	
	if (sb_controller.isSBVisible()) {
		this.widget.setToolTipContent(ZaMsg.tt_advanced_search_close);
	}else{
		this.widget.setToolTipContent (ZaMsg.tt_advanced_search_open) ;
	}
	//clear the search field
	sb_controller.setQuery ();
}

ZaSearchField.prototype.getItemByName =
function (name) {
	var items = this._localXForm.getItems()[0].getItems();
	var cnt = items.length ;
	for (var i=0; i < cnt; i++){
		if (items[i].getName () == name ) 
			return items[i];	
	}
	
	return null ;
}

ZaSearchField.prototype.setTooltipForSearchBuildButton =
function (tooltip){
	//change the tooltip for the search build button
	var searchBuildButtonItem = this.getItemByName("searchBuildButton") ;
	if (searchBuildButtonItem) {
		searchBuildButtonItem.getWidget().setToolTipContent (tooltip);
	}
}

ZaSearchField.prototype.setTooltipForSearchButton =
function (tooltip){
	//change the tooltip for the search button
	var searchButtonItem = this.getItemByName("searchButton") ;
	if (searchButtonItem) {
		searchButtonItem.getWidget().setToolTipContent (tooltip);
	}
}


ZaSearchField.prototype.setIconForSearchMenuButton =
function (imageName){
	//change the tooltip for the search button
	var searchMenuButtonItem = this.getItemByName("searchMenuButton") ;
	if (searchMenuButtonItem) {
		searchMenuButtonItem.getWidget().setImage (imageName);
	}
}

ZaSearchField.prototype.resetSearchFilter = function () {
	this._containedObject[ZaSearch.A_fAccounts] = "FALSE";
	this._containedObject[ZaSearch.A_fdistributionlists] = "FALSE";	
	this._containedObject[ZaSearch.A_fAliases] = "FALSE";
	this._containedObject[ZaSearch.A_fResources] = "FALSE";
	this._containedObject[ZaSearch.A_fDomains] = "FALSE";
	this._containedObject[ZaSearch.A_fCoses] = "FALSE";	
}

ZaSearchField.prototype.restoreSearchFilter = function(){
    if(this.searchSelectedType == ZaSearch.ACCOUNTS)
        this.accFilterSelected();
    else if(this.searchSelectedType == ZaSearch.ALIASES)
        this.aliasFilterSelected();
    else if(this.searchSelectedType == ZaSearch.DLS)
        this.dlFilterSelected();
    else if(this.searchSelectedType == ZaSearch.RESOURCES)
        this.resFilterSelected();
    else if(this.searchSelectedType == ZaSearch.DOMAINS)
        this.domainFilterSelected()
    else if(this.searchSelectedType == ZaSearch.COSES)
        this.cosFilterSelected()
    else
        this.allFilterSelected();
}

ZaSearchField.prototype.allFilterSelected = function (ev) {
    if (ev)
	    ev.item.parent.parent.setImage(ev.item.getImage());
    else
        this.setIconForSearchMenuButton("SearchAll");
	this._containedObject[ZaSearch.A_fAccounts] = "TRUE";
	this._containedObject[ZaSearch.A_fdistributionlists] = "TRUE";	
	this._containedObject[ZaSearch.A_fAliases] = "TRUE";
	this._containedObject[ZaSearch.A_fResources] = "TRUE";
	//if(ZaSettings.DOMAINS_ENABLED) {
	this._containedObject[ZaSearch.A_fDomains] = "TRUE";	
	//}
	this._containedObject[ZaSearch.A_fCoses] = "TRUE";
	this.setTooltipForSearchButton (ZaMsg.searchForAll);
    this.searchSelectedType = "";
}



ZaSearchField.prototype.accFilterSelected = function (ev) {
	this.resetSearchFilter();
	//ev.item.parent.parent.setImage(ev.item.getImage());	
	this.setIconForSearchMenuButton ("Account");
	this._containedObject[ZaSearch.A_fAccounts] = "TRUE";
	this.setTooltipForSearchButton (ZaMsg.searchForAccounts);
    this.searchSelectedType = ZaSearch.ACCOUNTS;
}

ZaSearchField.prototype.accFilterSelectedFromResults = function (ev) {
	this.resetSearchFilter();
	this._containedObject[ZaSearch.A_fAccounts] = "TRUE";
}

ZaSearchField.prototype.aliasFilterSelected = function (ev) {
	this.resetSearchFilter();
	//ev.item.parent.parent.setImage(ev.item.getImage());
	this.setIconForSearchMenuButton ("AccountAlias");
	this._containedObject[ZaSearch.A_fAliases] = "TRUE";	
	this.setTooltipForSearchButton (ZaMsg.searchForAliases);
    this.searchSelectedType = ZaSearch.ALIASES;
}

ZaSearchField.prototype.dlFilterSelected = function (ev) {
	this.resetSearchFilter();
	//ev.item.parent.parent.setImage(ev.item.getImage());
	this.setIconForSearchMenuButton ("DistributionList");
	this._containedObject[ZaSearch.A_fdistributionlists] = "TRUE";	
	this.setTooltipForSearchButton (ZaMsg.searchForDLs);
    this.searchSelectedType = ZaSearch.DLS;
}

ZaSearchField.prototype.dlFilterSelectedFromResults = function (ev) {
	this.resetSearchFilter();
	this._containedObject[ZaSearch.A_fdistributionlists] = "TRUE";
}

ZaSearchField.prototype.resFilterSelected = function (ev) {
	this.resetSearchFilter();
	//ev.item.parent.parent.setImage(ev.item.getImage());
	this.setIconForSearchMenuButton ("Resource");
	this._containedObject[ZaSearch.A_fResources] = "TRUE";
	this.setTooltipForSearchButton (ZaMsg.searchForResources);
    this.searchSelectedType = ZaSearch.RESOURCES;
}

ZaSearchField.prototype.domainFilterSelected = function (ev) {
	//if(ZaSettings.DOMAINS_ENABLED) {
		this.resetSearchFilter();
		//ev.item.parent.parent.setImage(ev.item.getImage());
		this.setIconForSearchMenuButton ("Domain");
		this._containedObject[ZaSearch.A_fDomains] = "TRUE";
		this.setTooltipForSearchButton (ZaMsg.searchForDomains);
        this.searchSelectedType = ZaSearch.DOMAINS;
	//}
}


ZaSearchField.prototype.domainFilterSelectedFromResults = function (ev) {
		this.resetSearchFilter();
		this._containedObject[ZaSearch.A_fDomains] = "TRUE";

}

ZaSearchField.prototype.cosFilterSelected = function (ev) {
                this.resetSearchFilter();
                this.setIconForSearchMenuButton ("COS");
                this._containedObject[ZaSearch.A_fCoses] = "TRUE";
                this.setTooltipForSearchButton (ZaMsg.searchForCOSES);
                this.searchSelectedType = ZaSearch.COSES;
}


ZaSearchField.searchChoices = new XFormChoices([],XFormChoices.OBJECT_REFERENCE_LIST, null, "labelId");
ZaSearchField.prototype._getMyXForm = function() {	
	var newMenuOpList = new Array();

    if(ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.ACCOUNT_LIST_VIEW] || ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.CARTE_BLANCHE_UI]) {
	    newMenuOpList.push(new ZaOperation(ZaOperation.SEARCH_ACCOUNTS, ZaMsg.SearchFilter_Accounts, ZaMsg.searchForAccounts, "Account", "AccountDis", new AjxListener(this,this.accFilterSelected)));
    }

    if(ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.DL_LIST_VIEW] || ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.CARTE_BLANCHE_UI]) {
	    newMenuOpList.push(new ZaOperation(ZaOperation.SEARCH_DLS, ZaMsg.SearchFilter_DLs, ZaMsg.searchForDLs, "DistributionList", "DistributionListDis", new AjxListener(this,this.dlFilterSelected)));
    }

    if(ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.ALIAS_LIST_VIEW] || ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.CARTE_BLANCHE_UI]) {
        newMenuOpList.push(new ZaOperation(ZaOperation.SEARCH_ALIASES, ZaMsg.SearchFilter_Aliases, ZaMsg.searchForAliases, "AccountAlias", "AccountAlias", new AjxListener(this, this.aliasFilterSelected)));
    }

    if (ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.RESOURCE_LIST_VIEW] || ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.CARTE_BLANCHE_UI]) {
	    newMenuOpList.push(new ZaOperation(ZaOperation.SEARCH_RESOURCES, ZaMsg.SearchFilter_Resources, ZaMsg.searchForResources, "Resource", "ResourceDis", new AjxListener(this, this.resFilterSelected)));
    }

    if (ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.DOMAIN_LIST_VIEW] || ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.CARTE_BLANCHE_UI]) {
        newMenuOpList.push(new ZaOperation(ZaOperation.SEARCH_DOMAINS, ZaMsg.SearchFilter_Domains, ZaMsg.searchForDomains, "Domain", "DomainDis", new AjxListener(this, this.domainFilterSelected)));
    }

    if (ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.COS_LIST_VIEW] || ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.CARTE_BLANCHE_UI]) {
        newMenuOpList.push(new ZaOperation(ZaOperation.SEARCH_COSES, ZaMsg.SearchFilter_COSES, ZaMsg.searchForCOSES, "COS", "COS", new AjxListener(this, this.cosFilterSelected)));
    }
	newMenuOpList.push(new ZaOperation(ZaOperation.SEP));				
	newMenuOpList.push(new ZaOperation(ZaOperation.SEARCH_ALL, ZaMsg.SearchFilter_All, ZaMsg.searchForAll, "SearchAll", "SearchAll", new AjxListener(this, this.allFilterSelected)));		
	ZaSearchField.searchChoices.setChoices(newMenuOpList);
	
	var numCols = 3;
	var colSizes;
	if(ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.CARTE_BLANCHE_UI]) {
		numCols = 7;
		colSizes = ["59", "*", "80", "110", "28", "12", "110"];
	} else {
		colSizes = ["59", "*", "80"];
		if(ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.HELP_SEARCH]) {
			numCols++;
			colSizes.push("100");
		}

                if(ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.SAVE_SEARCH]) {
                        numCols++;
                        colSizes.push("28");
                }

		if(ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.ACCOUNT_LIST_VIEW] || ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.DL_LIST_VIEW] || ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.ALIAS_LIST_VIEW] || ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.RESOURCE_LIST_VIEW]) {
			numCols+=2;
			colSizes.push("12");
			colSizes.push("110");
		}
	}

    var xFormObject;
    if (appNewUI) {
        numCols = 4;
        colSizes = ["46","3", "*", "28"];
        var entryKeyCallback= new AjxCallback(this, ZaSearchField.entryKeyHandler);
        xFormObject = {
            tableCssStyle:"width:100%;",numCols:numCols,width:"100%",
            colSizes:colSizes,
            items: [
                {type:_MENU_BUTTON_, label:null, choices:ZaSearchField.searchChoices,
                    name: "searchMenuButton",
                    toolTipContent:ZaMsg.searchToolTip,
                    icon:"SearchAll", cssClass:"ZaSearchFieldButton DwtToolbarButton"
                },
                {type:_SPACER_, colSpan:1, cssStyle:"", cssClass:"SearchFieldVert", height: 14},
                {type: _GROUP_,  numCols: 2, width: "100%", cssClass: "oselect",
                    items: [
                    {type:_DYNSELECT_, ref:ZaSearch.A_query, dataFetcherClass:ZaSearch,
                        dataFetcherMethod:ZaSearch.prototype.dynSearchField,
                        labelLocation:_NONE_,
                        width:"100%", inputWidth:"100%", editable:true, forceUpdate:true,
                        choices:new XFormChoices([], XFormChoices.OBJECT_REFERENCE_LIST, "name", "name"),
                        visibilityChecks:[],enableDisableChecks:[],
                        entryKeyMethod: entryKeyCallback,
                        onChange: function(value, event, form){
                            if (value instanceof ZaItem ) {
                                this.setInstanceValue(value.name);
                            } else {
                                this.setInstanceValue(value);
                            }
                        }
                    }

                ]},
                {type:_DWT_BUTTON_, toolTipContent:ZaMsg.searchForAll, icon:"Search", name: "searchButton",
                    onActivate:ZaSearchField.srchButtonHndlr, autoPadding: false,
                    cssStyle:"background-color:white;",
                    cssClass:"ZaSearchFieldButton   DwtToolbarButton"
                }
            ]
        };
    }  else  {
        xFormObject = {
            tableCssStyle:"width:100%;padding:2px;",numCols:numCols,width:"100%",
            colSizes:colSizes,
            items: [
                {type:_MENU_BUTTON_, label:null, choices:ZaSearchField.searchChoices,
                    name: "searchMenuButton",
                    toolTipContent:ZaMsg.searchToolTip,
                    icon:"SearchAll", cssClass:"DwtToolbarButton"
                },

                {type: _GROUP_,  numCols: 2, width: "100%", cssClass: "oselect",
                    //cssStyle:"margin-left: 5px; height: 22px; border: 1px solid; ",
                    items: [
                    {type:_TEXTFIELD_, ref:ZaSearch.A_query, containerCssClass:"search_field_container", label:null,
                        elementChanged: function(elementValue,instanceValue, event) {
                            var charCode = event.charCode;
                            if (charCode == 13 || charCode == 3) {
                               this.getForm().parent.invokeCallback();
                            } else {
                                this.getForm().itemChanged(this, elementValue, event);
                            }
                        },
                        visibilityChecks:[],
                        enableDisableChecks:[],
                        //cssClass:"search_input",
                        cssStyle:"overflow: hidden;", width:"100%"
                    },
                    {type:_DWT_BUTTON_, label:"", toolTipContent:ZaMsg.tt_savedSearch,
                        icon: "SelectPullDownArrow", name: "showSavedSearchButton",
                        onActivate:  ZaSearchField.prototype.showSavedSearchButtonHndlr,
                        cssClass: "ZaShowSavedSearchArrowButton",
                        enableDisableChecks: [[ZaSearchField.canViewSavedSearch]],
                        visibilityChecks:["(ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.SAVE_SEARCH] || ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.CARTE_BLANCHE_UI])"]
                    }
                ]},

                {type:_DWT_BUTTON_, label: ZaMsg.search, toolTipContent:ZaMsg.searchForAll, icon:"Search", name: "searchButton",
                    onActivate:ZaSearchField.srchButtonHndlr,
                    cssStyle: AjxEnv.isIE ? "marginLeft: 2px;" : "marginLeft: 5px;",
                    cssClass:"DwtToolbarButton"
                }
            ]
        };
        //Help search button
        if(ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.HELP_SEARCH] || ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.CARTE_BLANCHE_UI]) {
            xFormObject.items.push(
                {type:_DWT_BUTTON_, label: ZaMsg.help_search , toolTipContent:ZaMsg.tt_help_search, icon:"Help", name: "helpSearchButton",
                                    cssStyle:"overflow: hidden" ,onActivate:ZaSearchField.helpSrchButtonHndlr, cssClass:"DwtToolbarButton"}
            );
        }
        //Save button
        if(ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.SAVE_SEARCH] || ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.CARTE_BLANCHE_UI]) {
            xFormObject.items.push({type:_DWT_BUTTON_, label: null , toolTipContent:ZaMsg.tt_save_search, icon:"Save", name: "saveSearchButton",
                    onActivate:ZaSearchField.saveSrchButtonHndlr, cssClass:"DwtToolbarButton",
                    enableDisableChecks: [[ZaSearchField.canSaveSearch]],
                    visibilityChecks:["(ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.SAVE_SEARCH] || ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.CARTE_BLANCHE_UI])"]
                });
        }

        //advanced search button
        if(ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.CARTE_BLANCHE_UI] || ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.ACCOUNT_LIST_VIEW] || ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.DL_LIST_VIEW] || ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.ALIAS_LIST_VIEW] || ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.RESOURCE_LIST_VIEW]) {
            xFormObject.items.push({type: _OUTPUT_, value: ZaToolBar.getSeparatorHtml()});
            xFormObject.items.push({type:_DWT_BUTTON_, label:ZaMsg.advanced_search, toolTipContent: ZaMsg.tt_advanced_search_open, name: "searchBuildButton",
                    cssStyle:"overflow: hidden" ,
                    onActivate:ZaSearchField.advancedButtonHndlr,
                    cssClass: "DwtToolbarButton ZaAdvancedSearchButton"
                });
        }

        // set the last button's width to 98 percents of its container
        // to reserve some place between last button and its panel.
        xFormObject.items[numCols - 1].width = "98%";
    }

	return xFormObject;
};

ZaSearchField.canSaveSearch = function () {
    return ZaItem.hasWritePermission ("zimbraAdminSavedSearches", 
            ZaZimbraAdmin.currentAdminAccount) ;
}

ZaSearchField.canViewSavedSearch = function () {
    return ZaItem.hasReadPermission ("zimbraAdminSavedSearches",
                ZaZimbraAdmin.currentAdminAccount) ;
}

ZaSearchField.prototype.setCurrentSavedSearch = function (currentSavedSearch) {
    this._currentSavedSearch = currentSavedSearch;
}

ZaSearchField.prototype.getCurrentSavedSearch = function() {
    if (AjxUtil.isEmpty(this._currentSavedSearch))
        this._currentSavedSearch = {};
    return this._currentSavedSearch;

}

ZaSearchField.defaultName = "Saved Search";
ZaSearchField.nameCache = {};
ZaSearchField.prototype.getDefaultSearchName = function (name) {
    if (!name)
        name = ZaSearchField.defaultName;
    if (!ZaSearchField.nameCache[name])
        ZaSearchField.nameCache[name] = [];

    var index = ZaSearchField.nameCache[name].length + 1;
    ZaSearchField.nameCache [name].push(index);

    return name + " {" + index + "}";
}

ZaSearchField.prototype.doSaveSearch = function (queryString) {
    var currentSearch = this.getCurrentSavedSearch();
    var dialog = this.getSaveAndEditSeachDialog();
    var isCreated = currentSearch.name ? false: true;
    var name = this.getDefaultSearchName(currentSearch.name);
    dialog.show(name, queryString, isCreated);
}

/**
* @param xModelMetaData - XModel metadata that describes data model
* @param xFormMetaData - XForm metadata that describes the form
**/
ZaSearchField.prototype._initForm = 
function (xModelMetaData, xFormMetaData) {
	if(xModelMetaData == null || xFormMetaData == null)
		throw new AjxException("Metadata for XForm and/or XModel are not defined", AjxException.INVALID_PARAM, "ZaSearchField.prototype._initForm");

	this._localXModel = new XModel(xModelMetaData);
	this._localXForm = new XForm(xFormMetaData, this._localXModel, null, this);
	this._localXForm.draw();
	this._drawn = true;
}

//The popup dialog to allow user to specify the name/query of the search to be saved.
ZaSaveSearchDialog = function(searchField) {
	if (!searchField) return ; 
	this._searchField = searchField
	DwtDialog.call(this, searchField.shell);
	this._okButton = this.getButton(DwtDialog.OK_BUTTON);
	this.registerCallback (DwtDialog.OK_BUTTON, ZaSaveSearchDialog.prototype.okCallback, this );
}

ZaSaveSearchDialog.prototype = new DwtDialog ;
ZaSaveSearchDialog.prototype.constructor = ZaSaveSearchDialog ;

ZaSaveSearchDialog.prototype.okCallback =
function() {
	//if(window.console && window.console.log) console.debug("Ok button of saved search dialog is clicked.");
	var savedSearchArr = [] ;
	var nameValue = this._nameInput.value;
	var queryValue =  this._queryInput.value ;

	if(!nameValue) {
		ZaApp.getInstance().getCurrentController().popupErrorDialog(ZaMsg.ERROR_SAVENAME_EMPTY);
		ZaSaveSearchDialog.prototype.show(null,queryValue);
	}
	
	savedSearchArr.push({
			name: nameValue,
			query: queryValue
		})
	
	if (this._isEditMode && this._origNameOfEdittedSearch != nameValue) { //saved search name is changed
		savedSearchArr.push({
			name: this._origNameOfEdittedSearch,
			query: ""
		}); 
	}
	
	this.savedSearchArr = savedSearchArr;
	// check whether replace existing queries
	this._checkExistSearch();

	this.popdown();

}


ZaSaveSearchDialog.prototype._checkExistSearch = function() {

	var isExist = false;
        ZaSearch.updateSavedSearch (ZaSearch.getSavedSearches());
	for(var i = 0; i < this.savedSearchArr.length; i++) {
		var searchName = this.savedSearchArr[i].name;
	        if(ZaSearch.SAVED_SEARCHES && searchName) {
        	        for(var j = 0; j < ZaSearch.SAVED_SEARCHES.length; j++) {
	                        if(ZaSearch.SAVED_SEARCHES[j].name == searchName) {
        	                        isExist = true;
                	                break;
	                        }
        	        }
	        }
	}
        if(isExist) {
		// If exist searchquries, call confirm dialog
		ZaApp.getInstance().dialogs["confirmMessageDialog"] = new ZaMsgDialog(ZaApp.getInstance().getAppCtxt().getShell(), null, [DwtDialog.YES_BUTTON, DwtDialog.NO_BUTTON]);
                ZaApp.getInstance().dialogs["confirmMessageDialog"].setMessage(ZaMsg.Q_SAVE_REPLACE, DwtMessageDialog.INFO_STYLE);
                ZaApp.getInstance().dialogs["confirmMessageDialog"].registerCallback(DwtDialog.YES_BUTTON, this._continueDoSave, this);
                ZaApp.getInstance().dialogs["confirmMessageDialog"].registerCallback(DwtDialog.NO_BUTTON, this._cancelDoSave, this);
                ZaApp.getInstance().dialogs["confirmMessageDialog"].popup();
                
        } else {
	        ZaSearch.modifySavedSearches(this.savedSearchArr,
                        new AjxCallback(this._searchField, this._searchField.modifySavedSearchCallback )) ;
	}

        return isExist;
}


ZaSaveSearchDialog.prototype._continueDoSave = function() {

        ZaSearch.modifySavedSearches(this.savedSearchArr,
                        new AjxCallback(this._searchField, this._searchField.modifySavedSearchCallback )) ;

        ZaApp.getInstance().dialogs["confirmMessageDialog"].popdown();

}

ZaSaveSearchDialog.prototype._cancelDoSave = function() {
	ZaApp.getInstance().dialogs["confirmMessageDialog"].popdown();
	this.popup();
}


ZaSaveSearchDialog.prototype.show =
function (name, query, isCreated){
	if (!this._createUI) {
		this._nameInputId = Dwt.getNextId();
		this._queryInputId = Dwt.getNextId();
		var html = [
			"<table><tr>",
			"<td>",  ZaMsg.saved_search_editor_name, "</td>",
			"<td><div style='overflow:auto;'><input id='", this._nameInputId, "' type=text size=50 maxlength=50 /></div></td></tr>",
			//"<td>", this._queryInput.getHtmlElement().innerHTML ,"</td></tr>",
			
			"<tr><td>",  ZaMsg.saved_search_editor_query, "</td>",	
			"<td><div style='overflow:auto;'><input id='", this._queryInputId, "' type=text size=50 maxlength=200 /><div></td>",
			//"<td>", this._nameInput.getHtmlElement().innerHTML ,"</td></tr>",
			"</tr></table>"
		] ; 
		this.setContent (html.join("")) ;			
		this._createUI = true ;
	}
	
	if (!name || isCreated) {
		this.setTitle (ZaMsg.t_saved_search) ;
		this._isEditMode = false ; 
	}else{
		this.setTitle (ZaMsg.t_edit_saved_search) ;
		this._isEditMode = true;
		this._origNameOfEdittedSearch = name ;
	}
		
	this.popup() ;
	
	if (!this._nameInput) {
		this._nameInput = document.getElementById(this._nameInputId);
	}
	this._nameInput.value = name || "";
	
	if (!this._queryInput) {
		this._queryInput = document.getElementById(this._queryInputId) ;
	}
	this._queryInput.value = query || "" ;
}
