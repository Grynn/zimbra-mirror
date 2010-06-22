/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2005, 2006, 2007, 2008, 2009, 2010 Zimbra, Inc.
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
* @class ZaDashBoardView
* @contructor ZaDashBoardView
* @param parent
* @author Greg Solovyev
**/
ZaDashBoardView = function(parent) {
	if (arguments.length == 0) return;
	ZaTabView.call(this, parent,"ZaDashBoardView");
	this.setScrollStyle(Dwt.SCROLL);
	this.TAB_INDEX = 0;	
	var item = {};
	this.sortBy = ZaAccount.A_uid;
	this.bSortAsc = true;
	this.initForm(ZaDashBoard.myXModel, this.getMyXForm({}), item);
//	this._createHTML();
}
ZaDashBoardView.mainHelpPage = "administration_console_help.htm#appliance/zap_working_in_the_administration_console.htm";
ZaDashBoardView.prototype = new ZaTabView();
ZaDashBoardView.prototype.constructor = ZaDashBoardView;
ZaTabView.XFormModifiers["ZaDashBoardView"] = new Array();

ZaDashBoardView.prototype.getTabTitle =
function () {
	return com_zimbra_dashboard.DashBoard_view_title;
}

/**
* @method setObject sets the object contained in the view
* @param entry - ZaItem object to display
**/
ZaDashBoardView.prototype.setObject =
function(entry) {
	this._containedObject = new Object();
	this._containedObject[ZaDashBoard.settingsTab] = 1;
	this._containedObject.attrs = new Object();
	this._containedObject.type = entry.type ;

	if(entry.rights)
		this._containedObject.rights = entry.rights;
	
	if(entry.setAttrs)
		this._containedObject.setAttrs = entry.setAttrs;
	
	if(entry.getAttrs)
		this._containedObject.getAttrs = entry.getAttrs;
		
	if(entry._defaultValues)
		this._containedObject._defaultValues = entry._defaultValues;
		
	for (var a in entry.attrs) {
		if(entry.attrs[a] instanceof Array) {
			this._containedObject.attrs[a] = [].concat(entry.attrs[a]);
		} else {
			this._containedObject.attrs[a] = entry.attrs[a];
		}
	}	
	this._containedObject[ZaSearch.A_query] = "";
	this._containedObject[ZaDashBoard.searchResults] = entry[ZaDashBoard.searchResults];
	this._localXForm.setInstance(this._containedObject);
	
	this.formDirtyLsnr = new AjxListener(ZaApp.getInstance().getCurrentController(), ZaXFormViewController.prototype.handleXFormChange);
	this._localXForm.addListener(DwtEvent.XFORMS_FORM_DIRTY_CHANGE, this.formDirtyLsnr);
	this._localXForm.addListener(DwtEvent.XFORMS_VALUE_ERROR, this.formDirtyLsnr);	
	
	this.updateTab();
	var busyId = Dwt.getNextId();
	//var callback = new AjxCallback(this, ZaDashBoardView.onSearchResult, {limit:ZaSettings.RESULTSPERPAGE,CONS:null,busyId:busyId});
	this.types = [ZaSearch.ACCOUNTS,ZaSearch.ALIASES, ZaSearch.DLS,ZaSearch.RESOURCES];
	this.offset = 0;
	this.query = ZaSearch.getSearchByNameQuery("",this.types);
	var searchParams = {
			query: this.query, 
			types:this.types,
			sortBy:ZaAccount.A_uid,
			offset:0,
			limit:ZaSettings.RESULTSPERPAGE,
			attrs:ZaSearch.standardAttributes
			//callback:callback,
			//controller: ZaApp.getInstance().getCurrentController(),
			//showBusy:true,
			//busyId:busyId,
			//busyMsg:ZaMsg.BUSY_SEARCHING,
			//skipCallbackIfCancelled:false			
	}
	try {
		var resp = ZaSearch.searchDirectory(searchParams);
		var list = new ZaItemList(null);
		ZaDashBoardView.processSearchResult(resp.Body.SearchDirectoryResponse, list, searchParams);
		this._localXForm.setInstanceValue(list.getArray(),ZaDashBoard.searchResults);
	} catch (ex) {
		ZaApp.getInstance().getCurrentController()._handleException(ex, "ZaDashBoardView.onSearchResult");	
	}
}

ZaDashBoardView.processSearchResult = function(respBody, list,params) {
	list.loadFromJS(respBody);
	var act = new AjxTimedAction(list, ZaItemList.prototype.loadEffectiveRights, null);
	AjxTimedAction.scheduleAction(act, 150)	
	var searchTotal = respBody.searchTotal;
	var limit = params.limit ? params.limit : ZaSettings.RESULTSPERPAGE; 
	var numPages = Math.ceil(searchTotal/params.limit);
	ZaApp.getInstance().getDashBoardController().numPages = numPages;
	ZaApp.getInstance().getDashBoardController().searchTotal = searchTotal;
	ZaApp.getInstance().getDashBoardController().changeActionsState();
	var s_result_end_n = 0;
	var s_result_start_n = 0;				
	var srCountBt = ZaApp.getInstance().getDashBoardController()._toolbar.getButton (ZaOperation.SEARCH_RESULT_COUNT) ;
	if (srCountBt ) {
		s_result_start_n = (ZaApp.getInstance().getDashBoardController().currentPageNum - 1) * ZaSettings.RESULTSPERPAGE + 1;
		var max = ZaApp.getInstance().getDashBoardController().currentPageNum  * ZaSettings.RESULTSPERPAGE;
		s_result_end_n = (searchTotal > max) ? max : searchTotal;
		
		srCountBt.setText ( AjxMessageFormat.format (ZaMsg.searchResultCount, 
				[s_result_start_n + " - " + s_result_end_n, searchTotal]));
	}
}

ZaDashBoardView.onSearchResult = function(params,resp) {
	try {
		if(params.busyId)
			ZaApp.getInstance().getAppCtxt().getShell().setBusy(false, params.busyId);
			
		if(!resp) {
			throw(new AjxException(ZaMsg.ERROR_EMPTY_RESPONSE_ARG, AjxException.UNKNOWN, "ZaDashBoardView.onSearchResult"));
		}
		if(resp && resp.isException()) {
			ZaSearch.handleTooManyResultsException(resp.getException(), "ZaDashBoardView.onSearchResult");
			var list = new ZaItemList(params.CONS);	
			this._localXForm.setInstanceValue(list,ZaDashboard.searchResults);
		} else {
			ZaSearch.TOO_MANY_RESULTS_FLAG = false;
			var list = new ZaItemList(params.CONS);
			var searchTotal = 0;
			if(resp && !resp.isException()) {
				var response = resp.getResponse().Body.SearchDirectoryResponse;
				ZaDashBoardView.processSearchResult(response,list,params);
			}
			//change list headers
			if(params.types && params.types.length > 0) {
				var showStatus = false;
				var showDisplayName = false;
				var types = params.types;
				var cnt = types.length;
				var typeField = "objectClass";
				var displayNameSortable = true;
				if(cnt==1) {
					if( types[0] == ZaSearch.RESOURCES) {
						typeField = ZaResource.A_zimbraCalResType;
					} else if(types[0] == ZaSearch.ACCOUNTS) {
						typeField = ZaAccount.A_zimbraIsSystemResource;
					}
				}
				
				for(var i=0;i<cnt;i++) {
					if(types[i] == ZaSearch.DLS || types[i] == ZaSearch.DOMAINS || types[i]==ZaSearch.COSES || types[i]==ZaSearch.ALIASES) {
						displayNameSortable = false;
					}
					if(!showStatus && (types[i] == ZaSearch.ACCOUNTS || types[i] == ZaSearch.RESOURCES || types[i] == ZaSearch.DLS || types[i] == ZaSearch.DOMAINS)) {
						showStatus = true;
					}
					if(!showDisplayName && (types[i] == ZaSearch.ACCOUNTS || types[i] == ZaSearch.RESOURCES)) {
						showDisplayName = true;
					}
				}
				var listItems = this._localXForm.getItemsById("dashBoardSearchResults");
				if(listItems && listItems[0]) {
					var listWidget = listItems[0].getWidget();
					listWidget.setHeaderList(listWidget.getHeaderList(showStatus,showDisplayName,typeField,displayNameSortable));
				}
			}
			this._localXForm.setInstanceValue(list.getArray(),ZaDashBoard.searchResults);
			var dashBoardController = ZaApp.getInstance().getDashBoardController(ZaSettings.DASHBOARD_VIEW);
			dashBoardController.changeActionsState();
		}
	} catch (ex) {
		if (ex.code != ZmCsfeException.MAIL_QUERY_PARSE_ERROR) {
			ZaApp.getInstance().getCurrentController()._handleException(ex, "ZaDashBoardView.onSearchResult");	
		} else {
			ZaApp.getInstance().getCurrentController().popupErrorDialog(ZaMsg.queryParseError, ex);
		}		
	}
	

}

	
ZaDashBoardView.prototype.searchAddresses = function (types, offset, sortBy, bSortAsc) {  
	offset = offset ? offset : 0;
	this.offset = offset;
	var busyId = Dwt.getNextId();
	types = types ? types : [ZaSearch.ACCOUNTS,ZaSearch.ALIASES,ZaSearch.DLS,ZaSearch.RESOURCES];
	if(!AjxUtil.isEmpty(sortBy)) {
		this.sortBy = sortBy;
	}
	if(!AjxUtil.isEmpty(bSortAsc)) {
		this.bSortAsc = bSortAsc;
	}
	
	this.types = types;
	this.query = ZaSearch.getSearchByNameQuery(this._containedObject[ZaSearch.A_query],types);
	var callback = new AjxCallback(this, ZaDashBoardView.onSearchResult, {limit:ZaSettings.RESULTSPERPAGE,CONS:null,busyId:busyId,types:types,query:this.query});
	var searchParams = {
		query: this.query, 
		types:types,
		sortBy:this.sortBy,
		offset:offset,
		limit:ZaSettings.RESULTSPERPAGE,
		attrs:ZaSearch.standardAttributes,
		callback:callback,
		controller: ZaApp.getInstance().getCurrentController(),
		showBusy:true,
		busyId:busyId,
		busyMsg:ZaMsg.BUSY_SEARCHING,
		skipCallbackIfCancelled:false,
		sortAscending:(this.bSortAsc ? "1" : "0")
	}
	ZaSearch.searchDirectory(searchParams);	
}

ZaDashBoardView.restartMailboxD = function () {
	var soapDoc = AjxSoapDoc.create("RestartMailboxDRequest", ZaZimbraAdmin.URN, null);
	var server = soapDoc.set("server","gsolovyev-mbp");
	server.setAttribute("by", "name");
	
	var busyid = Dwt.getNextId ();
	var callback = new AjxCallback(this, ZaDashBoardView.restartCallback, {busyid:busyid});

	var reqMgrParams = {
		controller : ZaApp.getInstance().getCurrentController(),
		busyMsg : ZaMsg.BUSY_FLUSH_CACHE,
		busyid:busyid
	}
		
	var reqParams = {
		serverUri:"https://localhost:7070/service/",
		soapDoc: soapDoc,
		asyncMode:true,
		callback: callback
	}
	ZaRequestMgr.invoke(reqParams, reqMgrParams);	
}

ZaDashBoardView.restartCallback = function() {
	
}

ZaDashBoardView.prototype.resetSearchPageCounts = function () {
    ZaApp.getInstance().getDashBoardController().currentPageNum = 1;
}

ZaDashBoardView.prototype.accFilterSelected = function() {	
	this.setIconForSearchMenuButton ("Account");
    this.resetSearchPageCounts () ;
	this.setLabelForSearchMenuButton(ZaMsg.SearchFilter_Accounts);
	this.searchAddresses([ZaSearch.ACCOUNTS],0, null, true);
}

ZaDashBoardView.prototype.dlFilterSelected = function() {
	this.setIconForSearchMenuButton ("DistributionList");
	this.resetSearchPageCounts () ;
    this.setLabelForSearchMenuButton(ZaMsg.SearchFilter_DLs);
	this.searchAddresses([ZaSearch.DLS],0, null, true);
}

ZaDashBoardView.prototype.aliasFilterSelected = function() {
	this.setIconForSearchMenuButton ("AccountAlias");
	this.resetSearchPageCounts () ;
    this.setLabelForSearchMenuButton(ZaMsg.SearchFilter_Aliases);
	this.searchAddresses([ZaSearch.ALIASES],0, null, true);
}

ZaDashBoardView.prototype.resFilterSelected = function() {
	this.setIconForSearchMenuButton ("Resource");	
	this.resetSearchPageCounts () ;
    this.setLabelForSearchMenuButton(ZaMsg.SearchFilter_Resources);
	this.searchAddresses([ZaSearch.RESOURCES],0, null, true);
}

ZaDashBoardView.prototype.domainFilterSelected = function() {
	this.setIconForSearchMenuButton ("Domain");
	this.resetSearchPageCounts () ;
    this.setLabelForSearchMenuButton(ZaMsg.SearchFilter_Domains);
	this.searchAddresses([ZaSearch.DOMAINS],0, null, true);
}

ZaDashBoardView.prototype.allFilterSelected = function() {
	this.setIconForSearchMenuButton ("SearchAll");
	this.resetSearchPageCounts () ;
    this.setLabelForSearchMenuButton(com_zimbra_dashboard.SearchFilter_All);
	this.searchAddresses([ZaSearch.ACCOUNTS, ZaSearch.ALIASES, ZaSearch.DLS, ZaSearch.RESOURCES],0, null, true);
}

ZaDashBoardView.prototype.cosFilterSelected = function() {
	this.setIconForSearchMenuButton ("COS");
	this.resetSearchPageCounts () ;
    this.setLabelForSearchMenuButton(com_zimbra_dashboard.SearchFilter_Profiles);
	this.searchAddresses([ZaSearch.COSES],0,null,true);
}

ZaDashBoardView.prototype.setTooltipForSearchButton =	function (tooltip){
	//change the tooltip for the search button
	var searchButtonItem = this._localXForm.getItemsById("dashBoardSearchButton")[0];
	if (searchButtonItem) {
		searchButtonItem.getWidget().setToolTipContent (tooltip);
	}
}

ZaDashBoardView.prototype.setIconForSearchMenuButton = function (imageName){
	//change the tooltip for the search button
	var searchMenuButtonItem = this._localXForm.getItemsById("dashBoardSearchMenuButton")[0];
	if (searchMenuButtonItem) {
		searchMenuButtonItem.getWidget().setImage (imageName);
	}
}

ZaDashBoardView.prototype.setLabelForSearchMenuButton = function (labelText){
	//change the tooltip for the search button
	var searchMenuButtonItem = this._localXForm.getItemsById("dashBoardSearchMenuButton")[0];
	if (searchMenuButtonItem) {
		searchMenuButtonItem.getWidget().setText (labelText);
	}
}

ZaDashBoardView.getCustomHeight = function () {
	try {
		var form = this.getForm();
		var formParentElement = this.getForm().parent.getHtmlElement();
		var totalHeight = parseInt(formParentElement.style.height);
		if(isNaN(totalHeight)) {
			totalHeight = formParentElement.clientHeight ? formParentElement.clientHeight : formParentElement.offsetHeight;
		}
		var formHeaders = form.getItemsById("dashBoardSearchField");
		var headerHeight = 0;
		if(formHeaders) {
			var formHeader = formHeaders[0];		
			if(formHeader) {
				headerHeight = formHeader.getElement().clientHeight ? formHeader.getElement().clientHeight : formHeader.getElement().offsetHeight;				
			}
		}
		var spacer1 = form.getItemsById("dashBoardSpacer1");
		var spacer1Height = 0;
		if(spacer1) {
			var spacer1 = spacer1[0];		
			if(spacer1) {
				spacer1Height = spacer1.getElement().clientHeight ? spacer1.getElement().clientHeight : spacer1.getElement().offsetHeight;				
			}
		}		
		
		var spacer2 = form.getItemsById("dashBoardSpacer2");
		var spacer2Height = 0;
		if(spacer2) {
			var spacer2 = spacer1[0];		
			if(spacer2) {
				spacer2Height = spacer2.getElement().clientHeight ? spacer2.getElement().clientHeight : spacer2.getElement().offsetHeight;				
			}
		}			
		if(totalHeight<=0)
			return "100%";
		else
			return totalHeight - headerHeight - spacer1Height - spacer2Height - 20;
	} catch (ex) {
        
	}
	return "100%";  					
};

ZaDashBoardView.getCustomWidth = function () {
	try {

		var form = this.getForm();
		var formElement = this.getForm().getHtmlElement();
		var totalWidth = parseInt(formElement.style.width);
		if(isNaN(totalWidth)) {
			totalWidth = formElement.clientWidth ? formElement.clientWidth : formElement.offsetWidth;
		}

		if(totalWidth<=0)
			return "100%";
		else
			return totalWidth;
	} catch (ex) {
        
	}
	return "100%";  					
};					

ZaDashBoardView.listSelectionListener = function (ev) {
	if (ev.detail == DwtListView.ITEM_DBL_CLICKED) {
		if(ev.item) {
			this._selectedItem = ev.item;
			ZaApp.getInstance().getDashBoardController().editItem(ev.item);
		}
	} else {
		//enable/disable toolbar buttons
		ZaApp.getInstance().getDashBoardController().changeActionsState();
	}
};

ZaDashBoardView.createPopupMenu = function (listWidget) {
	ZaApp.getInstance().getDashBoardController()._actionMenu = listWidget.actionMenu = new ZaPopupMenu(listWidget, "ActionMenu", null, ZaApp.getInstance().getDashBoardController()._popupOperations);
	listWidget.addActionListener(new AjxListener(ZaApp.getInstance().getDashBoardController(), ZaApp.getInstance().getDashBoardController().listActionListener));		
	listWidget.xFormItem = this;
}

ZaDashBoardView.myXFormModifier = function(xFormObject,entry) {	
    var tabBarChoices = [];
    ZaDashBoardView.searchChoices = new XFormChoices([],XFormChoices.OBJECT_REFERENCE_LIST, null, "labelId");
	var searchMenuOpList = new Array();
	searchMenuOpList.push(new ZaOperation(ZaOperation.SEARCH_ACCOUNTS, ZaMsg.SearchFilter_Accounts, ZaMsg.searchForAccounts, "Account", "AccountDis", new AjxListener(this,this.accFilterSelected)));
	searchMenuOpList.push(new ZaOperation(ZaOperation.SEARCH_DLS, ZaMsg.SearchFilter_DLs, ZaMsg.searchForDLs, "DistributionList", "DistributionListDis", new AjxListener(this,this.dlFilterSelected)));
	searchMenuOpList.push(new ZaOperation(ZaOperation.SEARCH_ALIASES, ZaMsg.SearchFilter_Aliases, ZaMsg.searchForAliases, "AccountAlias", "AccountAlias", new AjxListener(this, this.aliasFilterSelected)));
	searchMenuOpList.push(new ZaOperation(ZaOperation.SEARCH_RESOURCES, ZaMsg.SearchFilter_Resources, ZaMsg.searchForResources, "Resource", "ResourceDis", new AjxListener(this, this.resFilterSelected)));
	searchMenuOpList.push(new ZaOperation(ZaOperation.SEARCH_ALL, com_zimbra_dashboard.SearchFilter_All, com_zimbra_dashboard.searchForAll, "SearchAll", "SearchAll", new AjxListener(this, this.allFilterSelected)));
	searchMenuOpList.push(new ZaOperation(ZaOperation.SEP));
	searchMenuOpList.push(new ZaOperation(ZaOperation.SEARCH_DOMAINS, ZaMsg.SearchFilter_Domains, ZaMsg.searchForDomains, "Domain", "DomainDis", new AjxListener(this, this.domainFilterSelected)));
	searchMenuOpList.push(new ZaOperation(ZaOperation.SEARCH_COSES, com_zimbra_dashboard.SearchFilter_Profiles, com_zimbra_dashboard.searchForProfiles, "COS", "COS", new AjxListener(this, this.cosFilterSelected)));				
	ZaDashBoardView.searchChoices.setChoices(searchMenuOpList);
	
	xFormObject.tableCssStyle="width:100%;overflow:auto;";
	xFormObject.numCols=1;
	xFormObject.colSizes=["auto"];
	xFormObject.items = [
	   {type:_GROUP_, id:"dashboard_listview_group",
		   numCols:4, colSizes: ["200px","500px","5px","125px"],visibilityChecks:[],enableDisableChecks:[],
		   items:[  	
	            {type:_SPACER_,colSpan:4, id:"dashBoardSpacer1"},	    	
   	    	    {type:_TEXTFIELD_,label:com_zimbra_dashboard.LookupAddress,labelLocation:_LEFT_,
   	    	    	cssStyle:"overflow: hidden;", width:"100%",ref:ZaSearch.A_query, bmolsnr: true,
   	    	    	enableDisableChecks:[],visibilityChecks:[],id:"dashBoardSearchField"
   	    	    },
   	    	    {type:_CELLSPACER_,width:"5px"},
   	    	    {type:_MENU_BUTTON_,label:com_zimbra_dashboard.SearchFilter_All,icon:"SearchAll",enableDisableChecks:[],visibilityChecks:[],
   	    	    	choices:ZaDashBoardView.searchChoices,toolTipContent:ZaMsg.searchToolTip,id:"dashBoardSearchMenuButton"
             	},
             	{type:_SPACER_,colSpan:4, id:"dashBoardSpacer2"},
 	    	    {colSpan:4, ref:ZaDashBoard.searchResults, id:"dashBoardSearchResults",
 	    	    	onSelection:ZaDashBoardView.listSelectionListener, type:_DWT_LIST_, 
 	    	   		forceUpdate: true,createPopupMenu:ZaDashBoardView.createPopupMenu,
 	    	   		multiselect:true, widgetClass:ZaDashBoardListView,
 	    	   		getCustomHeight:ZaDashBoardView.getCustomHeight,
 	    	   		getCustomWidth:ZaDashBoardView.getCustomWidth,
 	    	   		bmolsnr:true,
 	    	   		visibilityChecks:[], enableDisableChecks:[]
 	    	   	}             	
   	    	]	
	   }                    
      
	];
	
}
ZaTabView.XFormModifiers["ZaDashBoardView"].push(ZaDashBoardView.myXFormModifier);
