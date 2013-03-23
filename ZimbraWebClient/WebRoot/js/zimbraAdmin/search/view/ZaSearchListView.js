/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2006, 2007, 2008, 2009, 2010, 2011, 2012 VMware, Inc.
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
* @constructor
* @class ZaSearchListView
* @param parent
* @author Roland Schemers
* @author Greg Solovyev
**/
ZaSearchListView = function(parent) {

	var className = null;
	var posStyle = DwtControl.ABSOLUTE_STYLE;
	
	var headerList = this._getHeaderList(); 
	
	ZaListView.call(this, {
		parent:parent, 
		className:className, 
		posStyle:posStyle, 
		headerList:headerList,
		id:ZaId.TAB_SEARCH_MANAGE,
        scrollLoading:true
	});

	this._appCtxt = this.shell.getData(ZaAppCtxt.LABEL);
	
	
}

ZaSearchListView.prototype = new ZaListView;
ZaSearchListView.prototype.constructor = ZaSearchListView;

ZaSearchListView.prototype.toString = 
function() {
	return "ZaSearchListView";
}

ZaSearchListView.prototype.getTitle = 
function () {
	return ZaMsg.Accounts_view_title;
}

ZaSearchListView.prototype.getTabIcon =
function () {
	return "search" ;
}

ZaSearchListView.prototype.getTabTitle =
function () {
	return ZaMsg.Search_view_title ;
}

ZaSearchListView.prototype.getTabToolTip =
function () {
	var controller = ZaApp.getInstance().getSearchListController () ;
	if (controller) {
		if (controller._isAdvancedSearch && controller._currentQuery) {
			return ZaMsg.tt_tab_Search + controller._currentQuery ;
		}else if (!controller._isAdvancedSearch && controller._searchFieldInput) {
			return ZaMsg.tt_tab_Search + controller._searchFieldInput ;
		}
	}

	return ZaMsg.Search_view_title ;
}

/**
* Renders a single item as a DIV element.
*/
ZaSearchListView.prototype._createItemHtml =
function(account, now, isDragProxy) {
	var html = new Array(50);
	var	div = document.createElement("div");
	div[DwtListView._STYLE_CLASS] = "Row";
	div[DwtListView._SELECTED_STYLE_CLASS] = div[DwtListView._STYLE_CLASS] + "-" + DwtCssStyle.SELECTED;
	div.className = div[DwtListView._STYLE_CLASS];
	this.associateItemWithElement(account, div, DwtListView.TYPE_LIST_ITEM);
	
	var idx = 0;
	html[idx++] = "<table width='100%' cellspacing='0' cellpadding='0'>";

	html[idx++] = "<tr>";

	var cnt = this._headerList.length;
	for(var i = 0; i < cnt; i++) {
		var field = this._headerList[i]._field;
		var IEWidth = this._headerList[i]._width + 4 ;

		var dwtId = Dwt.getNextId();
		var rowId = ZaId.TAB_SEARCH_MANAGE;		
		if(field == "type") {
			// type
			html[idx++] = "<td id=\"" + rowId + "_data_type_" + dwtId + "\" width=" + this._headerList[i]._width + ">";
			switch(account.type) {
				case ZaItem.ACCOUNT:
					if(account.attrs[ZaAccount.A_zimbraIsAdminAccount]=="TRUE" ) {
						html[idx++] = AjxImg.getImageHtml("AdminUser");
					} else if (account.attrs[ZaAccount.A_zimbraIsDelegatedAdminAccount] == "TRUE") {
						html[idx++] = AjxImg.getImageHtml("DomainAdminUser");
					} else if (account.attrs[ZaAccount.A_zimbraIsSystemAccount] == "TRUE") {
						html[idx++] = AjxImg.getImageHtml("SpecialAccount");
					} else if (account.attrs[ZaAccount.A_zimbraIsSystemResource] == "TRUE") {
						html[idx++] = AjxImg.getImageHtml("SystemResource");
                    } else if (account.attrs[ZaAccount.A_zimbraIsExternalVirtualAccount] == "TRUE") {
						html[idx++] = AjxImg.getImageHtml("AccountExternalVirtual");
					} else {
						html[idx++] = AjxImg.getImageHtml("Account");
					}                          
				break;
				case ZaItem.DL:
					if (account.attrs[ZaDistributionList.A_isAdminGroup] == "TRUE") {
					    html[idx++] = AjxImg.getImageHtml("DistributionListGroup");
                    }else {
                        html[idx++] = AjxImg.getImageHtml("DistributionList");
                    }	
				break;
				case ZaItem.ALIAS:
					html[idx++] = AjxImg.getImageHtml("AccountAlias");				
				break;	
				case ZaItem.RESOURCE:
					if (account.attrs[ZaResource.A_zimbraCalResType] == ZaResource.RESOURCE_TYPE_LOCATION){
						html[idx++] = AjxImg.getImageHtml("Location");
					}else {//equipment or other resource types
						html[idx++] = AjxImg.getImageHtml("Resource");
					}						
				break;	
				case ZaItem.DOMAIN:
					html[idx++] = AjxImg.getImageHtml("Domain");		
				break;								
                                case ZaItem.COS: 
                                        html[idx++] = AjxImg.getImageHtml("COS");
                                break;	
				default:
					html[idx++] = account.type;
				break;
			}
			html[idx++] = "</td>";
		} else if(field == ZaAccount.A_name) {
			// name
			html[idx++] = "<td id=\"" + rowId + "_data_emailaddress_" + dwtId + "\" nowrap width=" + (AjxEnv.isIE ? IEWidth : this._headerList[i]._width) + "><nobr>";
			if(account.type == ZaItem.DOMAIN) {
				html[idx++] = AjxStringUtil.htmlEncode(account.attrs[ZaDomain.A_domainName]);
			} else {
				if(account.isExternal) {
					html[idx++] = "<span class='asterisk'>*</span>";	
				}				
				html[idx++] = AjxStringUtil.htmlEncode(account.name);
			}
			html[idx++] = "</nobr></td>";
		} else if (field == ZaAccount.A_displayname) {
			// display name
			html[idx++] = "<td id=\"" + rowId + "_data_displayname_" + dwtId + "\" nowrap width=" + (AjxEnv.isIE ? IEWidth : this._headerList[i]._width) + "><nobr>";
			html[idx++] = AjxStringUtil.htmlEncode(account.attrs[ZaAccount.A_displayname]);
			html[idx++] = "</nobr></td>";	
		} else if(field == ZaAccount.A_accountStatus) {
			// status
			html[idx++] = "<td id=\"" + rowId + "_data_status_" + dwtId + "\" width=" + (AjxEnv.isIE ? IEWidth : this._headerList[i]._width) + "><nobr>";
			var status = "";
			if (account.type == ZaItem.ACCOUNT) {
				status = ZaAccount._accountStatus(account.attrs[ZaAccount.A_accountStatus]);
			} else if (account.type == ZaItem.DL) {
				status = ZaDistributionList.getDLStatus(account.attrs[ZaDistributionList.A_mailStatus]);
			}else if ( account.type == ZaItem.RESOURCE) {
				status = ZaResource.getAccountStatusLabel(account.attrs[ZaAccount.A_accountStatus]);
			}else if (account.type == ZaItem.DOMAIN) {
				status =  ZaDomain._domainStatus(account.attrs[ZaDomain.A_zimbraDomainStatus]);
			}
			html[idx++] = status;
			html[idx++] = "</nobr></td>";		
		}else if (field == ZaAccount.A_zimbraLastLogonTimestamp) {
			// display last login time for accounts only
			html[idx++] = "<td id=\"" + rowId + "_data_lastlogontime_" + dwtId + "\" width=" + (AjxEnv.isIE ? IEWidth : this._headerList[i]._width) + "><nobr>";
			html[idx++] = AjxStringUtil.htmlEncode(ZaAccount.getLastLoginTime(account.attrs[ZaAccount.A_zimbraLastLogonTimestamp]));
			html[idx++] = "</nobr></td>";	
		} else if (field == ZaAccount.A_description) {		
			// description
			html[idx++] = "<td id=\"" + rowId + "_data_description_" + dwtId + "\" width=" + this._headerList[i]._width + "><nobr>";
			html[idx++] = AjxStringUtil.htmlEncode(
                    ZaItem.getDescriptionValue(account.attrs[ZaAccount.A_description]));
			html[idx++] = "</nobr></td>";	
		}
	}
		html[idx++] = "</tr></table>";
	div.innerHTML = html.join("");
	return div;
}

ZaSearchListView.prototype._getHeaderList =
function() {

	var headerList = new Array();
	var sortable = 1;
	var i = 0

	headerList[i++] = new ZaListHeaderItem("type", ZaMsg.ALV_Type_col, null, "40px", null, null, true, true);
	this._defaultColumnSortable = sortable ;
	headerList[i++] = new ZaListHeaderItem(ZaAccount.A_name, ZaMsg.CLV_Name_col, null, "220px", null,  null, true, true);
	
//idPrefix, label, iconInfo, width, sortable, sortField, resizeable, visible	
	headerList[i++] = new ZaListHeaderItem(ZaAccount.A_displayname, ZaMsg.ALV_DspName_col, null, "220px",  null, null, true, true);
	
	headerList[i++] = new ZaListHeaderItem(ZaAccount.A_accountStatus, ZaMsg.ALV_Status_col, null, "120px",  null, null, true, true);
	headerList[i++] = new ZaListHeaderItem(ZaAccount.A_zimbraLastLogonTimestamp, ZaMsg.ALV_Last_Login, null, Dwt_Button_XFormItem.estimateMyWidth(ZaMsg.ALV_Last_Login, false, 0), null, null, true, true);
	headerList[i++] = new ZaListHeaderItem(ZaAccount.A_description, ZaMsg.ALV_Description_col, null, "auto", null, null,true, true );
	
	return headerList;
}


ZaSearchListView.prototype._sortColumn = 
function(columnItem, bSortAsc) {
	try {
		ZaApp.getInstance().getAccountListController().setSortOrder(bSortAsc);
		ZaApp.getInstance().getAccountListController().setSortField(columnItem.getSortField());
		ZaApp.getInstance().getAccountListController().show();
		//ZaApp.getInstance().getAccountListController().show(searchResult);
	} catch (ex) {
		ZaApp.getInstance().getCurrentController()._handleException(ex);
	}
}

/**
* @class ZaSearchXFormView
* @contructor
* @param parent
* @author Ming Zhang
**/
function ZaSearchXFormView (parent) {
	ZaTabView.call(this, parent,"ZaSearchXFormView", "DwtTabView ZaXFormListView");
	this.initForm(ZaSearchEdit.myXModel,this.getMyXForm());
	this._localXForm.removeListener(DwtEvent.XFORMS_VALUE_CHANGED, this.formChangeListener);
//	this._localXForm.addListener(DwtEvent.XFORMS_FORM_DIRTY_CHANGE, new AjxListener(this, ZaBackupsXFormView.prototype.handleXFormChange));
	this._localXForm.setController(ZaApp.getInstance());
    this._localXForm.setInstance({});
    this._bubbleList = new ZaSearchBubbleList();
   // this.widget = this.getItemsById("searchReusltList").getWidget();
}

ZaSearchXFormView.prototype = new ZaTabView();
ZaSearchXFormView.prototype.constructor = ZaSearchXFormView;
ZaTabView.XFormModifiers["ZaSearchXFormView"] = new Array();

ZaSearchXFormView.prototype.bubbleChangeCallback =
function() {
     var currentQueryValue = ZaApp.getInstance().getSearchListController()._uiContainer.getQueryFormBubbles();
     currentQueryValue = currentQueryValue ? currentQueryValue: "";
     ZaApp.getInstance().getSearchListController()._uiContainer.setQueryField(currentQueryValue);
    if(ZaApp.getInstance().getSearchListController()._uiContainer.isEnableForSearch()){
        var currentQueryType = ZaApp.getInstance().getSearchListController()._uiContainer.getSearchTypeFormBubbles();
        var searchField = ZaApp.getInstance().getSearchListController()._searchField;
        searchField.startSearch(currentQueryValue,currentQueryType,true);
    }
}

ZaSearchXFormView.prototype.addBubble = function(params,skipNotify) {
    try{
        if(params.displayName){
            params.parent = this;
            params.parentId	= this._htmlElId;
            params.queryCell = this._localXForm.getItemById(this._localXForm.getId()+"_"+ZaSearchEdit.A2_currentQuery).getContainer();
            this._bubbleList.addBubble(params);
        }
        if(!skipNotify)
            this.bubbleChangeCallback();
    }catch(ex){
        ZaApp.getInstance().getCurrentController()._handleException(ex, "ZaSearchXFormView.prototype.addBubble");
    }
}

ZaSearchXFormView.prototype._removeBubble = function(bubbleId, skipNotify) {
    var bubble = DwtControl.fromElementId(bubbleId);
	if (!bubble) {
        return;
    }
	this._bubbleList.removeBubble(bubble);
	bubble.dispose();

    if(!skipNotify)
        this.bubbleChangeCallback();
}

ZaSearchXFormView.removeBubble = function(bubbleId,parentId,skipNotify) {
    try{
        var searchXFormView =  DwtControl.ALL_BY_ID[parentId];
        if(searchXFormView){
            searchXFormView._removeBubble(bubbleId, skipNotify);
        }
    }catch(ex){
        ZaApp.getInstance().getCurrentController()._handleException(ex, "ZaSearchXFormView.removeBubble");
    }
}

ZaSearchXFormView.prototype.removeAllBubbles =
function (skipNotify) {
    var len = this._bubbleList.size();
    for(var i = len-1; i>=0; i--){
        var bubble = this._bubbleList.getBubble(i);
        if(bubble){
            this._bubbleList.removeBubble(bubble);
	        bubble.dispose();
        }
    }
    if(!skipNotify)
        this.bubbleChangeCallback();
}

ZaSearchXFormView.prototype.getQueryFormBubbles = function(){
    return this._bubbleList.getQueryFormBubbles();
}

ZaSearchXFormView.prototype.getSearchTypeFormBubbles = function(){
    return this._bubbleList.getSearchTypeFormBubbles();
}

ZaSearchXFormView.prototype.isEnableForSearch = function(){
    return this._bubbleList.isEnableForSearch();
}

ZaSearchXFormView.labelSelectionListener = function (ev) {
	if (ev.detail == DwtListView.ITEM_DBL_CLICKED) {
		if(ev.item) {
			this._selectedItem = ev.item;
			ZaApp.getInstance().getAccountViewController().show(ev.item);
		}
	}
}

ZaSearchXFormView.prototype.getQueryField = function () {
    return this._localXForm.getInstanceValue(ZaSearchEdit.A2_currentQuery);
}

ZaSearchXFormView.prototype.setQueryField = function (query) {
    this._localXForm.setInstanceValue(query, ZaSearchEdit.A2_currentQuery);
}

ZaSearchXFormView.prototype.setQueryFieldVisible = function(visible){
    this.queryVisible = visible ;
}

ZaSearchXFormView.createPopupMenu = function (listWidget) {
	/*ZaApp.getInstance().getCurrentController()._actionMenu = listWidget.actionMenu = new ZaPopupMenu(listWidget, "ActionMenu", null, ZaApp.getInstance().getCurrentController()._popupOperations);
	listWidget.addActionListener(new AjxListener(ZaApp.getInstance().getCurrentController(), ZaApp.getInstance().getCurrentController().listActionListener));
	listWidget.xFormItem = this;*/
    this.getForm().parent.widget = listWidget;
}

ZaSearchXFormView.getCustomHeight = function () {
	try {
		var form = this.getForm();
		var formParentElement = this.getForm().parent.getHtmlElement();
		var totalHeight = parseInt(formParentElement.style.height);
		if(isNaN(totalHeight)) {
			totalHeight = formParentElement.clientHeight ? formParentElement.clientHeight : formParentElement.offsetHeight;
		}
		var formHeaders = form.getItemsById("xform_header");
		var headerHeight = 0;
		if(formHeaders) {
			var formHeader = formHeaders[0];
			if(formHeader) {
				headerHeight = formHeader.getElement().clientHeight ? formHeader.getElement().clientHeight : formHeader.getElement().offsetHeight;
			}
		}
		if(totalHeight<=0)
			return "100%";
		else
			return totalHeight - headerHeight - 2;
	} catch (ex) {

	}
	return "100%";
};

ZaSearchXFormView.getCustomWidth = function () {
	try {

		var formParentElement = this.getForm().parent.getHtmlElement();
		var totalWidth = parseInt(formParentElement.style.width);
		if(isNaN(totalWidth)) {
			totalWidth = formParentElement.clientWidth ? formParentElement.clientWidth : formParentElement.offsetWidth;
		}
		//var tabBarHeight = this.getForm().getItemsById("xform_tabbar")[0].getElement().offsetHeight;
		if(totalWidth<=0)
			return "100%";
		else
			return totalWidth;
	} catch (ex) {

	}
	return "100%";
};

ZaSearchXFormView.doQuickSearch = function () {
    if(ZaApp.getInstance().getSearchListController()._uiContainer)
        ZaApp.getInstance().getSearchListController()._uiContainer.bubbleChangeCallback();
}

ZaSearchXFormView.doSaveSearch = function () {
    var currentQueryValue = ZaApp.getInstance().getSearchListController()._uiContainer.getQueryFormBubbles();
    currentQueryValue = currentQueryValue ? currentQueryValue: "";
    ZaApp.getInstance().getSearchListController()._searchField.doSaveSearch(currentQueryValue);
}


ZaSearchXFormView.isSearchFieldVisible = function(){
   var searchContainer = ZaApp.getInstance().getSearchListController()._uiContainer;
   if(searchContainer){
       if(!searchContainer.queryVisible )
         return false;
   }
   return true;
}

ZaSearchXFormView.myXFormModifier = function(xFormObject) {
	xFormObject.tableCssStyle="width:100%;overflow:auto;";


	var headerList = ZaSearchListView.prototype._getHeaderList();
	xFormObject.items = [
		{type:_GROUP_, visibilityChecks:[ZaSearchXFormView.isSearchFieldVisible],
            visibilityChangeEventSources:[ZaSearchEdit.A2_currentQuery],
            colSizes:["*","70px","100px"], colSpan:2, numCols:3, width:"100%", id:"xform_header",
			items:[
				{type:_OUTPUT_, ref:ZaSearchEdit.A2_currentQuery,
					containerCssClass:"search_field_container",
					cssStyle:"display:none;"
				},
				{type:_DWT_BUTTON_, label:ZaMsg.LBL_QuickSearch, name: "SearchButton", autoPadding: false,
						onActivate:ZaSearchXFormView.doQuickSearch, visibilityChecks:[], enableDisableChecks:[]},
				{type:_DWT_BUTTON_, label:ZaMsg.LBL_SaveSearch, name: "saveSearchButton",  autoPadding: false,
						onActivate:ZaSearchXFormView.doSaveSearch, visibilityChecks:[],
                        visibilityChecks:["(ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.SAVE_SEARCH] || ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.CARTE_BLANCHE_UI])"],
                        enableDisableChangeEventSources:[ZaSearchEdit.A2_currentQuery],enableDisableChecks:[[XForm.checkInstanceValueNotEmty,ZaSearchEdit.A2_currentQuery]]}
			]
		},

	    {ref:ZaSearchEdit.A2_searchResult, colSpan:2,cssClass: "ZaFullPageXFormListView", id:"searchReusltList",
	    	type:_DWT_LIST_,
            createPopupMenu: ZaSearchXFormView.createPopupMenu,
	   		multiselect:true, widgetClass:ZaSearchListView,headerList:headerList,getCustomHeight:ZaSearchXFormView.getCustomHeight,
	   		getCustomWidth:ZaSearchXFormView.getCustomWidth, visibilityChecks:[], enableDisableChecks:[]
	   	}
	];
};
ZaTabView.XFormModifiers["ZaSearchXFormView"].push(ZaSearchXFormView.myXFormModifier);

ZaSearchEdit = function () {
}

ZaSearchEdit.A2_currentQuery = "currentQuery";
ZaSearchEdit.A2_searchResult = "searchResult";

ZaSearchEdit.myXModel = {
    items: [
        {id: ZaSearchEdit.A2_currentQuery, type:_STRING_},
        {id: ZaSearchEdit.A2_searchResult, type:_LIST_}
    ]
}
