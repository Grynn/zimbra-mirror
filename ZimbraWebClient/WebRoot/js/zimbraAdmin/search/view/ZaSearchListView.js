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
* @constructor
* @class ZaSearchListView
* @param parent
* @author Roland Schemers
* @author Greg Solovyev
**/
function ZaSearchListView(parent, app) {
	this._app = app;
	var className = null;
	var posStyle = DwtControl.ABSOLUTE_STYLE;
	
	var headerList = this._getHeaderList();
	
	ZaListView.call(this, parent, className, posStyle, headerList);

	this._appCtxt = this.shell.getData(ZaAppCtxt.LABEL);
	
	this.setScrollStyle(DwtControl.SCROLL);
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
	var controller = this._app.getCurrentController () ;
	if (controller) {
		return ZaMsg.tt_tab_Search + controller._currentQuery ;
	}else {
		return this.getTabTitle () ;
	}
}

/**
* Renders a single item as a DIV element.
*/
ZaSearchListView.prototype._createItemHtml =
function(account, now, isDndIcon) {
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
		if(id.indexOf("type")==0) {
			// type
			html[idx++] = "<td width=" + this._headerList[i]._width + ">";
			switch(account.type) {
				case ZaItem.ACCOUNT:
					html[idx++] = AjxImg.getImageHtml("Account");
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
				case ZaItem.DOMAIN:
					html[idx++] = AjxImg.getImageHtml("Domain");		
				break;									
				default:
					html[idx++] = account.type;
				break;
			}
			html[idx++] = "</td>";
		} else if(id.indexOf(ZaAccount.A_name)==0) {
			// name
			html[idx++] = "<td width=" + this._headerList[i]._width + "><nobr>";
			if(account.type == ZaItem.DOMAIN) {
				html[idx++] = AjxStringUtil.htmlEncode(account.attrs[ZaDomain.A_domainName]);
			} else {
				html[idx++] = AjxStringUtil.htmlEncode(account.name);
			}
			html[idx++] = "</nobr></td>";
		} else if (id.indexOf(ZaAccount.A_displayname)==0) {
			// display name
			html[idx++] = "<td width=" + this._headerList[i]._width + "><nobr>";
			html[idx++] = AjxStringUtil.htmlEncode(account.attrs[ZaAccount.A_displayname]);
			html[idx++] = "</nobr></td>";	
		} else if(id.indexOf(ZaAccount.A_accountStatus)==0) {
			// status
			html[idx++] = "<td width=" + this._headerList[i]._width + "><nobr>";
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
		} else if (id.indexOf(ZaAccount.A_description)==0) {		
			// description
			html[idx++] = "<td width=" + this._headerList[i]._width + "><nobr>";
			html[idx++] = AjxStringUtil.htmlEncode(account.attrs[ZaAccount.A_description]);
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
	
	headerList[0] = new ZaListHeaderItem("type", ZaMsg.ALV_Type_col, null, "40px", null, null, true, true);
	this._defaultColumnSortable = sortable ;
	headerList[1] = new ZaListHeaderItem(ZaAccount.A_name, ZaMsg.CLV_Name_col, null, "220px", null,  null, true, true);
	
//idPrefix, label, iconInfo, width, sortable, sortField, resizeable, visible	
	headerList[2] = new ZaListHeaderItem(ZaAccount.A_displayname, ZaMsg.ALV_DspName_col, null, "220px",  null, null, true, true);

	headerList[3] = new ZaListHeaderItem(ZaAccount.A_accountStatus, ZaMsg.ALV_Status_col, null, "80px",  null, null, true, true);

	headerList[4] = new ZaListHeaderItem(ZaAccount.A_description, ZaMsg.ALV_Description_col, null, null, null, null,true, true );
	
	return headerList;
}


ZaSearchListView.prototype._sortColumn = 
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

