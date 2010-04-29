/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2006, 2007, 2008, 2009, 2010 Zimbra, Inc.
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
* @class ZaDashBoardListView
* @param parent
* @author Roland Schemers
* @author Greg Solovyev
**/
ZaDashBoardListView = function(parent) {

	var className = null;
	var posStyle = DwtControl.ABSOLUTE_STYLE;
	
	var headerList = this._getHeaderList();
	
	ZaListView.call(this, parent, className, posStyle, headerList);

	this._appCtxt = this.shell.getData(ZaAppCtxt.LABEL);
	
}

ZaDashBoardListView.prototype = new ZaListView;
ZaDashBoardListView.prototype.constructor = ZaDashBoardListView;

ZaDashBoardListView.prototype.toString = 
function() {
	return "ZaDashBoardListView";
}

ZaDashBoardListView.prototype.getTitle = 
function () {
	return ZaMsg.Accounts_view_title;
}

ZaDashBoardListView.prototype.getTabIcon =
function () {
	return "search" ;
}

ZaDashBoardListView.prototype.getTabTitle =
function () {
	return ZaMsg.Search_view_title ;
}

ZaDashBoardListView.prototype.getTabToolTip =
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
ZaDashBoardListView.prototype._createItemHtml =
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
		
		if(field == "type") {
			// type
			html[idx++] = "<td width=" + this._headerList[i]._width + ">";
			switch(account.type) {
				case ZaItem.ACCOUNT:
					if(account.attrs[ZaAccount.A_zimbraIsAdminAccount]=="TRUE" ) {
						html[idx++] = AjxImg.getImageHtml("AdminUser");
					} else if (account.attrs[ZaAccount.A_zimbraIsDelegatedAdminAccount] == "TRUE") {
						html[idx++] = AjxImg.getImageHtml("DomainAdminUser");
					} else if (account.attrs[ZaAccount.A_zimbraIsSystemResource] == "TRUE") {
						html[idx++] = AjxImg.getImageHtml("SystemResource");
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
			html[idx++] = "<td nowrap width=" + (AjxEnv.isIE ? IEWidth : this._headerList[i]._width) + "><nobr>";
			if(account.type == ZaItem.DOMAIN) {
				html[idx++] = AjxStringUtil.htmlEncode(account.attrs[ZaDomain.A_domainName]);
			} else {
				html[idx++] = AjxStringUtil.htmlEncode(account.name);
			}
			html[idx++] = "</nobr></td>";
		} else if (field == ZaAccount.A_displayname) {
			// display name
			html[idx++] = "<td nowrap width=" + (AjxEnv.isIE ? IEWidth : this._headerList[i]._width) + "><nobr>";
			html[idx++] = AjxStringUtil.htmlEncode(account.attrs[ZaAccount.A_displayname]);
			html[idx++] = "</nobr></td>";	
		} else if(field == ZaAccount.A_accountStatus) {
			// status
			html[idx++] = "<td width=" + (AjxEnv.isIE ? IEWidth : this._headerList[i]._width) + "><nobr>";
			var status = "";
			if (account.type == ZaItem.ACCOUNT) {
				status = ZaAccount._accountStatus(account.attrs[ZaAccount.A_accountStatus]);
			} else if (account.type == ZaItem.DL) {
				status = account.attrs.zimbraMailStatus;
			}else if ( account.type == ZaItem.RESOURCE) {
				status = ZaResource.getAccountStatusLabel(account.attrs[ZaAccount.A_accountStatus]);
			} 
			html[idx++] = status;
			html[idx++] = "</nobr></td>";		
		}else if (field == ZaAccount.A_zimbraLastLogonTimestamp) {
			// display last login time for accounts only
			html[idx++] = "<td width=" + (AjxEnv.isIE ? IEWidth : this._headerList[i]._width) + "><nobr>";
			html[idx++] = AjxStringUtil.htmlEncode(ZaAccount.getLastLoginTime(account.attrs[ZaAccount.A_zimbraLastLogonTimestamp]));
			html[idx++] = "</nobr></td>";	
		} else if (field == ZaAccount.A_description) {		
			// description
			html[idx++] = "<td width=" + this._headerList[i]._width + "><nobr>";
			html[idx++] = AjxStringUtil.htmlEncode(
                    ZaItem.getDescriptionValue(account.attrs[ZaAccount.A_description]));
			html[idx++] = "</nobr></td>";	
		}
	}
		html[idx++] = "</tr></table>";
	div.innerHTML = html.join("");
	return div;
}

ZaDashBoardListView.prototype._getHeaderList =
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
	//headerList[i++] = new ZaListHeaderItem(ZaAccount.A_zimbraLastLogonTimestamp, ZaMsg.ALV_Last_Login, null, Dwt_Button_XFormItem.estimateMyWidth(ZaMsg.ALV_Last_Login, false, 0), null, null, true, true);
	headerList[i++] = new ZaListHeaderItem(ZaAccount.A_description, ZaMsg.ALV_Description_col, null, "auto", null, null,true, true );
	
	return headerList;
}


ZaDashBoardListView.prototype._sortColumn = 
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

