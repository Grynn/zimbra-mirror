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
 * Portions created by Zimbra are Copyright (C) 2004, 2005, 2006 Zimbra, Inc.
 * All Rights Reserved.
 * 
 * Contributor(s):
 * 
 * ***** END LICENSE BLOCK *****
 */

function ZaSearchField(parent, className, size, posStyle, app) {

	DwtComposite.call(this, parent, className, posStyle);
	this._containedObject = new ZaSearch();
	this._initForm(ZaSearch.myXModel,this._getMyXForm());
	this._localXForm.setInstance(this._containedObject);
	this._app = app;
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
	this._containedObject[ZaSearch.A_query] = this._localXForm.getItemsById(ZaSearch.A_query)[0].getElement().value;

	if (this._containedObject[ZaSearch.A_query].indexOf("$set:") == 0) {
		this._app.getAppCtxt().getClientCmdHdlr().execute((this._containedObject[ZaSearch.A_query].substr(5)).split(" "));
		return;
	}

	var objList = new Array();
	var params = {};
	if(this._containedObject[ZaSearch.A_fAccounts] == "TRUE") {
		objList.push(ZaSearch.ACCOUNTS);
	}
	if(this._containedObject[ZaSearch.A_fAliases] == "TRUE") {
		objList.push(ZaSearch.ALIASES);
	}
	if(this._containedObject[ZaSearch.A_fdistributionlists] == "TRUE") {
		objList.push(ZaSearch.DLS);
	}
	if(this._containedObject[ZaSearch.A_fResources] == "TRUE") {
		objList.push(ZaSearch.RESOURCES);
	}
	if(ZaSettings.DOMAINS_ENABLED) {
		if(this._containedObject[ZaSearch.A_fDomains] == "TRUE") {
			objList.push(ZaSearch.DOMAINS);
		}	
	}
	params.types = objList;
	
	var sb_controller = this._app.getSearchBuilderController()
	var isAdvanced = sb_controller.isSBVisible () ;
	if (isAdvanced) {
		DBG.println(AjxDebug.DBG1, "Advanced Search ... " ) ;
		//Use the text in the search field to do a search
		//params.query = sb_controller.getQuery ();
		params.query = this._containedObject[ZaSearch.A_query];
		DBG.println(AjxDebug.DBG1, "Query = " + params.query) ;
		params.types = sb_controller.getAddressTypes ();
		
	}else {
		DBG.println(AjxDebug.DBG1, "Basic Search ....") ;
		params.query = ZaSearch.getSearchByNameQuery(this._containedObject[ZaSearch.A_query]);
	}
	
	this._isSearchButtonClicked = false ;
	
	if (this._callbackFunc != null) {
		if (this._callbackObj != null) {
			//this._callbackFunc.call(this._callbackObj, this, params);
			this._app.getCurrentController().switchToNextView(this._callbackObj,
		 this._callbackFunc, params);
		} else {
			this._app.getCurrentController().switchToNextView(this._app.getSearchListController(), this._callbackFunc, params);
//			this._callbackFunc(this, params);
		}
	}
}

ZaSearchField.srchButtonHndlr = 
function(evt) {
	var fieldObj = this.getForm().parent;
	//fieldObj._isSearchButtonClicked = true ; //to Distinguish the action from the overveiw tree items
	fieldObj.invokeCallback(evt);
}

//only show or hide the advanced search options
ZaSearchField.advancedButtonHndlr =
function (evt) {
	//DBG.println(AjxDebug.DBG1, "Advanced Button Clicked ...") ;
	var form = this.getForm() ;
	var app = form.parent._app ;
	var sb_controller = app.getSearchBuilderController ();
	sb_controller.toggleVisible ();
	app._appViewMgr.showSearchBuilder (sb_controller.isSBVisible());
	
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
}

ZaSearchField.prototype.allFilterSelected = function (ev) {
	ev.item.parent.parent.setImage(ev.item.getImage());
	this._containedObject[ZaSearch.A_fAccounts] = "TRUE";
	this._containedObject[ZaSearch.A_fdistributionlists] = "TRUE";	
	this._containedObject[ZaSearch.A_fAliases] = "TRUE";
	this._containedObject[ZaSearch.A_fResources] = "TRUE";
	if(ZaSettings.DOMAINS_ENABLED) {
		this._containedObject[ZaSearch.A_fDomains] = "TRUE";	
	}
	this.setTooltipForSearchButton (ZaMsg.searchForAll);	
}

ZaSearchField.prototype.accFilterSelected = function (ev) {
	this.resetSearchFilter();
	//ev.item.parent.parent.setImage(ev.item.getImage());	
	this.setIconForSearchMenuButton ("Account");
	this._containedObject[ZaSearch.A_fAccounts] = "TRUE";
	this.setTooltipForSearchButton (ZaMsg.searchForAccounts);	
}

ZaSearchField.prototype.aliasFilterSelected = function (ev) {
	this.resetSearchFilter();
	//ev.item.parent.parent.setImage(ev.item.getImage());
	this.setIconForSearchMenuButton ("AccountAlias");
	this._containedObject[ZaSearch.A_fAliases] = "TRUE";	
	this.setTooltipForSearchButton (ZaMsg.searchForAliases);
}

ZaSearchField.prototype.dlFilterSelected = function (ev) {
	this.resetSearchFilter();
	//ev.item.parent.parent.setImage(ev.item.getImage());
	this.setIconForSearchMenuButton ("Group");
	this._containedObject[ZaSearch.A_fdistributionlists] = "TRUE";	
	this.setTooltipForSearchButton (ZaMsg.searchForDLs);	
}

ZaSearchField.prototype.resFilterSelected = function (ev) {
	this.resetSearchFilter();
	//ev.item.parent.parent.setImage(ev.item.getImage());
	this.setIconForSearchMenuButton ("Resource");
	this._containedObject[ZaSearch.A_fResources] = "TRUE";
	this.setTooltipForSearchButton (ZaMsg.searchForResources);	
}

ZaSearchField.prototype.domainFilterSelected = function (ev) {
	if(ZaSettings.DOMAINS_ENABLED) {
		this.resetSearchFilter();
		ev.item.parent.parent.setImage(ev.item.getImage());
		this._containedObject[ZaSearch.A_fDomains] = "TRUE";
		this.setTooltipForSearchButton (ZaMsg.searchForDomains);	
	}
}

ZaSearchField.searchChoices = new XFormChoices([],XFormChoices.OBJECT_REFERENCE_LIST, null, "labelId");
ZaSearchField.prototype._getMyXForm = function() {	
	var newMenuOpList = new Array();

	newMenuOpList.push(new ZaOperation(ZaOperation.SEARCH_ACCOUNTS, ZaMsg.SearchFilter_Accounts, ZaMsg.searchForAccounts, "Account", "AccountDis", new AjxListener(this,this.accFilterSelected)));	
	newMenuOpList.push(new ZaOperation(ZaOperation.SEARCH_DLS, ZaMsg.SearchFilter_DLs, ZaMsg.searchForDLs, "Group", "GroupDis", new AjxListener(this,this.dlFilterSelected)));		
	newMenuOpList.push(new ZaOperation(ZaOperation.SEARCH_ALIASES, ZaMsg.SearchFilter_Aliases, ZaMsg.searchForAliases, "AccountAlias", "AccountAlias", new AjxListener(this, this.aliasFilterSelected)));		
	newMenuOpList.push(new ZaOperation(ZaOperation.SEARCH_RESOURCES, ZaMsg.SearchFilter_Resources, ZaMsg.searchForResources, "Resource", "ResourceDis", new AjxListener(this, this.resFilterSelected)));		
	if(ZaSettings.DOMAINS_ENABLED) {
		newMenuOpList.push(new ZaOperation(ZaOperation.SEARCH_DOMAINS, ZaMsg.SearchFilter_Domains, ZaMsg.searchForDomains, "Domain", "DomainDis", new AjxListener(this, this.domainFilterSelected)));			
	}
	newMenuOpList.push(new ZaOperation(ZaOperation.SEP));				
	newMenuOpList.push(new ZaOperation(ZaOperation.SEARCH_ALL, ZaMsg.SearchFilter_All, ZaMsg.searchForAll, "SearchAll", "SearchAll", new AjxListener(this, this.allFilterSelected)));		
	ZaSearchField.searchChoices.setChoices(newMenuOpList);
	
	var xFormObject = {
		tableCssStyle:"width:100%;padding:2px;",numCols:5,width:"100%",
		colSizes:["50px", "*", "65px", "10px", "95px"],
		items: [
//			{type:_OUTPUT_, value:ZaMsg.searchForAccountsLabel, nowrap:true},
			{type:_MENU_BUTTON_, label:null, choices:ZaSearchField.searchChoices, 
				name: "searchMenuButton",
				toolTipContent:ZaMsg.searchToolTip, 
				icon:"SearchAll", cssClass:"DwtToolbarButton"},
			{type:_TEXTFIELD_, ref:ZaSearch.A_query, containerCssClass:"search_field_container", label:null, 
				elementChanged: function(elementValue,instanceValue, event) {
					var charCode = event.charCode;
					if (charCode == 13 || charCode == 3) {
					   this.getForm().parent.invokeCallback();
					} else {
						this.getForm().itemChanged(this, elementValue, event);
					}
				},
				//cssClass:"search_input", 
				cssStyle:"overflow: hidden;", width:"100%"
			},
			{type:_DWT_BUTTON_, label:ZaMsg.search, toolTipContent:ZaMsg.searchForAll, icon:ZaMsg.search, name: "searchButton",
					onActivate:ZaSearchField.srchButtonHndlr, cssClass:"DwtToolbarButton"},
			{type: _OUTPUT_, value: ZaToolBar.getSeparatorHtml() },
			{type:_DWT_BUTTON_, label:ZaMsg.advanced_search, toolTipContent: ZaMsg.tt_advanced_search_open, name: "searchBuildButton",
					onActivate:ZaSearchField.advancedButtonHndlr, cssClass: "DwtToolbarButton" }
			/*{type:_OUTPUT_, value:ZaMsg.Filter+":", label:null},
			{type:_CHECKBOX_, ref:ZaSearch.A_fAccounts,label:ZaMsg.Filter_Accounts, labelLocation:_RIGHT_,trueValue:"TRUE", falseValue:"FALSE"},					
			{type:_CHECKBOX_, ref:ZaSearch.A_fAliases,label:ZaMsg.Filter_Aliases, labelLocation:_RIGHT_,trueValue:"TRUE", falseValue:"FALSE"},
			{type:_CHECKBOX_, ref:ZaSearch.A_fdistributionlists,label:ZaMsg.Filter_DLs, labelLocation:_RIGHT_,trueValue:"TRUE", falseValue:"FALSE"}
			//HC:Resource
			{type:_CHECKBOX_, ref:ZaSearch.A_fResources,label:ZaMsg.Filter_Resources, labelLocation:_RIGHT_,trueValue:"TRUE", falseValue:"FALSE"}*/

		]
	};
	return xFormObject;
};

/**
* @param xModelMetaData - XModel metadata that describes data model
* @param xFormMetaData - XForm metadata that describes the form
**/
ZaSearchField.prototype._initForm = 
function (xModelMetaData, xFormMetaData) {
	if(xModelMetaData == null || xFormMetaData == null)
		throw new AjxException("Metadata for XForm and/or XModel are not defined", AjxException.INVALID_PARAM, "DwtXWizardDialog.prototype._initForm");

	this._localXModel = new XModel(xModelMetaData);
	this._localXForm = new XForm(xFormMetaData, this._localXModel, null, this);
	this._localXForm.draw();
	this._drawn = true;
}
