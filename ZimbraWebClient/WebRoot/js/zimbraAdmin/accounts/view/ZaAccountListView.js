/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2004, 2005, 2006, 2007 Zimbra, Inc.
 * 
 * The contents of this file are subject to the Yahoo! Public License
 * Version 1.0 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * 
 * ***** END LICENSE BLOCK *****
 */

/**
* @constructor
* @class ZaAccountListView
* @param parent
* @param listType: the account type of the list: alias or other types
* @author Roland Schemers
* @author Greg Solovyev
**/
ZaAccountListView = function(parent, app, listType) {
	this._app = app;
	var className = null;
	var posStyle = DwtControl.ABSOLUTE_STYLE;
	this._listType = listType ;
	var headerList = this._getHeaderList();
	
	ZaListView.call(this, parent, className, posStyle, headerList);

	this._appCtxt = this.shell.getData(ZaAppCtxt.LABEL);
	
	this._domains = {} ;
}

ZaAccountListView.prototype = new ZaListView;
ZaAccountListView.prototype.constructor = ZaAccountListView;

ZaAccountListView.prototype.toString = 
function() {
	return "ZaAccountListView";
}

ZaAccountListView.prototype.getTitle = 
function () {
	var title = ZaMsg.Addresses_view_title ;
	var cc = this._app.getControllerById (this.__internalId) ;
	switch (cc._defaultType) {
		case ZaItem.DL :
			title = ZaMsg.DL_view_title; break ;
		case ZaItem.ALIAS :
			title = ZaMsg.Aliases_view_title; break ;
		case ZaItem.RESOURCE : 
			title = ZaMsg.Resourse_view_title; break ;	
		default :
			title = ZaMsg.Accounts_view_title ;
	}	
	
	return title;
}

ZaAccountListView.prototype.getTabIcon =
function () {
	var icon = null ;
	var cc = this._app.getControllerById (this.__internalId) ;
	switch (cc._defaultType) {
		case ZaItem.DL :
			icon = "Group"; break ;
		case ZaItem.ALIAS :
			icon = "AccountAlias" ; break ;
		case ZaItem.RESOURCE : 
			icon = "Resource" ; break ;	
		default :
			icon = "Account" ;
	}	
	
	return icon ;
}

/**
* Renders a single item as a DIV element.
*/
ZaAccountListView.prototype._createItemHtml =
function(account, now, isDragProxy) {
	var html = new Array(50);
	var	div = document.createElement("div");
	div[DwtListView._STYLE_CLASS] = "Row";
	div[DwtListView._SELECTED_STYLE_CLASS] = div[DwtListView._STYLE_CLASS] + "-" + DwtCssStyle.SELECTED;
	div.className = div[DwtListView._STYLE_CLASS];
	this.associateItemWithElement(account, div, DwtListView.TYPE_LIST_ITEM);
	
	var idx = 0;
	html[idx++] = "<table width='100%' cellspacing='2' cellpadding='0'>";

	html[idx++] = "<tr>";
	
	var cnt = this._headerList.length;
	for(var i = 0; i < cnt; i++) {
		var id = this._headerList[i]._id;
		var IEWidth = this._headerList[i]._width + 4 ;
		
		if(id.indexOf("type")==0) {
			// type
			html[idx++] = "<td width=" + this._headerList[i]._width + ">";
			switch(account.type) {
				case ZaItem.ACCOUNT:
					if(account.attrs[ZaAccount.A_isAdminAccount]=="TRUE") {
						html[idx++] = AjxImg.getImageHtml("AdminUser");
					} else if (ZaAccount.A_zimbraIsDomainAdminAccount && account.attrs[ZaAccount.A_zimbraIsDomainAdminAccount]=="TRUE") {
						html[idx++] = AjxImg.getImageHtml("DomainAdminUser");
					} else {
						html[idx++] = AjxImg.getImageHtml("Account");
					}	
				break;
				case ZaItem.DL:
					html[idx++] = AjxImg.getImageHtml("Group");				
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
					//html[idx++] = AjxImg.getImageHtml("Resource");				
				break;											
				default:
					html[idx++] = account.type;
				break;
			}
			html[idx++] = "</td>";
		} else if(id.indexOf(ZaAccount.A_name)==0) {
			// name
			html[idx++] = "<td nowrap width=" + (AjxEnv.isIE ? IEWidth : this._headerList[i]._width) + "><nobr>";
			html[idx++] = AjxStringUtil.htmlEncode(account.name);
			html[idx++] = "</nobr></td>";
		} else if (id.indexOf(ZaAccount.A_displayname)==0) {
			// display name
			html[idx++] = "<td nowrap width=" + (AjxEnv.isIE ? IEWidth : this._headerList[i]._width) + "><nobr>";
			html[idx++] = AjxStringUtil.htmlEncode(account.attrs[ZaAccount.A_displayname]);
			html[idx++] = "</nobr></td>";	
		} else if(id.indexOf(ZaAccount.A_accountStatus)==0) {
			// status
			html[idx++] = "<td width=" + (AjxEnv.isIE ? IEWidth : this._headerList[i]._width) + "><nobr>";
			var status = "";
			if (account.type == ZaItem.ACCOUNT) {
				status = ZaAccount._accountStatus(account.attrs[ZaAccount.A_accountStatus]);
			} else if (account.type == ZaItem.DL) {
				status = ZaDistributionList._dlStatus[account.attrs.zimbraMailStatus];
			}else if ( account.type == ZaItem.RESOURCE) {
				status = ZaResource.getAccountStatusLabel(account.attrs[ZaAccount.A_accountStatus]);
			} 
			html[idx++] = status;
			html[idx++] = "</nobr></td>";		
		} else if (id.indexOf(ZaAccount.A_zimbraLastLogonTimestamp)==0 ) {
			// display last login time for accounts only
			html[idx++] = "<td nowrap width=" + (AjxEnv.isIE ? IEWidth : this._headerList[i]._width) + "><nobr>";
			html[idx++] = AjxStringUtil.htmlEncode(ZaAccount.getLastLoginTime(account.attrs[ZaAccount.A_zimbraLastLogonTimestamp]));
			html[idx++] = "</nobr></td>";	
		} else if (id.indexOf(ZaAccount.A_description)==0) {		
			// description
			html[idx++] = "<td width=" + this._headerList[i]._width + "><nobr>";
			html[idx++] = AjxStringUtil.htmlEncode(account.attrs[ZaAccount.A_description]);
			html[idx++] = "</nobr></td>";	
		} else if (id.indexOf("target" + ZaAlias.A_targetType) == 0) {
			html[idx++] = "<td width=" + this._headerList[i]._width + "><nobr>";
			var targetType = account.attrs[ZaAlias.A_targetType] ;
			var targetType_desc ;
			if (targetType == "account" ) {
				targetType_desc = ZaMsg.aliasTargetTypeAccount ;
			}else if (targetType == "distributionlist") {
				targetType_desc = ZaMsg.aliasTargetTypeDL ;
			}
			html[idx++] = AjxStringUtil.htmlEncode(targetType_desc);
			html[idx++] = "</nobr></td>";
		} else if (id.indexOf(ZaAlias.A_targetAccount) == 0) {
			html[idx++] = "<td width=" + this._headerList[i]._width + "><nobr>";
			html[idx++] = AjxStringUtil.htmlEncode(account.attrs[ZaAlias.A_targetAccount]);
			html[idx++] = "</nobr></td>";
		}
	}
		html[idx++] = "</tr></table>";
	div.innerHTML = html.join("");
	return div;
}

ZaAccountListView.prototype._getHeaderList =
function(listType) {

	var headerList = new Array();
	var sortable = 1;
	var i = 0 ;
	headerList[i++] = new ZaListHeaderItem("type", ZaMsg.ALV_Type_col, null, "40px", sortable++, "objectClass", true, true);
	this._defaultColumnSortable = sortable ;
	headerList[i++] = new ZaListHeaderItem(ZaAccount.A_name, ZaMsg.ALV_Name_col, null, "220px", sortable++, ZaAccount.A_name, true, true);
	
	if (this._listType && this._listType == ZaItem.ALIAS) {
		headerList[i++] = new ZaListHeaderItem(ZaAlias.A_targetAccount, ZaMsg.ALV_TargetName_col, null, "220px", sortable++,ZaAlias.A_targetAccount, true, true);
		headerList[i++] = new ZaListHeaderItem("target" + ZaAlias.A_targetType, ZaMsg.ALV_TargetType_col, null, "200px", sortable++,ZaAlias.A_targetType, true, true);
	}else{
		//idPrefix, label, iconInfo, width, sortable, sortField, resizeable, visible	
		headerList[i++] = new ZaListHeaderItem(ZaAccount.A_displayname, ZaMsg.ALV_DspName_col, null, "220px", sortable++,ZaAccount.A_displayname, true, true);
		headerList[i++] = new ZaListHeaderItem(ZaAccount.A_accountStatus, ZaMsg.ALV_Status_col, null, "120px", sortable++,ZaAccount.A_accountStatus, true, true);
		headerList[i++] = new ZaListHeaderItem(ZaAccount.A_zimbraLastLogonTimestamp, ZaMsg.ALV_Last_Login, null, "120px", sortable++, ZaAccount.A_zimbraLastLogonTimestamp, true, true);
	}
	headerList[i++] = new ZaListHeaderItem(ZaAccount.A_description, ZaMsg.ALV_Description_col, null, "200px", null, null,false, true );
	
	return headerList;
}


ZaAccountListView.prototype._sortColumn = 
function(columnItem, bSortAsc) {
	try {
		this._app.getAccountListController().setSortOrder(bSortAsc);
		this._app.getAccountListController().setSortField(columnItem.getSortField());
		this._app.getAccountListController().show();
		//this._app.getAccountListController().show(searchResult);
	} catch (ex) {
		this._app.getCurrentController()._handleException(ex);
	}
}

ZaAccountListView.prototype._mouseOverAction =
function(ev, div) {
	if (this._timedMouseOverAction) {
		AjxTimedAction.cancelAction(this._timedMouseOverAction._id) ;
	}
	this._timedMouseOverAction = 
		new AjxTimedAction (this, ZaListView.prototype._mouseOverAction, [ev, div]) ;
			
	AjxTimedAction.scheduleAction(this._timedMouseOverAction, 500) ;	
}
