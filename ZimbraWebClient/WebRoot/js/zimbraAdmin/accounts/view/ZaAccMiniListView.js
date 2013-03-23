/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2005, 2006, 2007, 2008, 2009, 2010, 2012 VMware, Inc.
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
 * @author Greg Solovyev
 **/
ZaAccMiniListView = function(parent, className, posStyle, headerList, view, id, scrollLoading) {
	if (arguments.length == 0) return;
    if (posStyle == null) posStyle = DwtControl.RELATIVE_STYLE ;
	ZaListView.call(this, parent, className, posStyle, headerList, view, id, scrollLoading);
	this.hideHeader = true;
}

ZaAccMiniListView.prototype = new ZaListView;
ZaAccMiniListView.prototype.constructor = ZaAccMiniListView;

ZaAccMiniListView.prototype.toString = function() {
	return "ZaAccMiniListView";
};

ZaAccMiniListView.prototype.createHeaderHtml = function (defaultColumnSort) {
	if(!this.hideHeader) {
		DwtListView.prototype.createHeaderHtml.call(this,defaultColumnSort);
	} 
}


ZaAccMiniListView.prototype._createItemHtml =
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
	if(typeof(account)=="string") {
		html[idx++] = "<td width=100%>";
		html[idx++] = AjxStringUtil.htmlEncode(account);
		html[idx++] = "</td>";			
	} else if(this._headerList) {
		var cnt = this._headerList.length;
		for(var i = 0; i < cnt; i++) {
			var field = this._headerList[i]._field;
			var imgName = null;
			if(field!=null) {			
				if(field == "type") {
					// type
					html[idx++] = "<td width=" + this._headerList[i]._width + ">";
					switch(account.type) {
						case ZaItem.ACCOUNT:
							if(account.attrs[ZaAccount.A_zimbraIsAdminAccount]=="TRUE" ) {
								imgName = ("AdminUser");
							} else if (account.attrs[ZaAccount.A_zimbraIsDelegatedAdminAccount] == "TRUE") {
								imgName = ("DomainAdminUser");
							} else if (account.attrs[ZaAccount.A_zimbraIsSystemAccount] == "TRUE") {
								imgName = ("SpecialAccount");
							} else if (account.attrs[ZaAccount.A_zimbraIsSystemResource] == "TRUE") {
								imgName = ("SystemResource");
							} else if (account.attrs[ZaAccount.A_zimbraIsExternalVirtualAccount] == "TRUE") {
								imgName = ("AccountExternalVirtual");
							} else {
								if(account.isExternal) {
									imgName = ("AccountIMAP");
								} else {
									imgName = ("Account");
								}
							}
						break;
						case ZaItem.DL:
							if (account.attrs[ZaDistributionList.A_isAdminGroup] == "TRUE") {
								imgName = ("DistributionListGroup");
							} else {
								imgName = ("DistributionList");
							}
						break;
						case ZaItem.ALIAS:
							imgName = ("AccountAlias");
						break;
						case ZaItem.DOMAIN:
							imgName = ("Domain");
						break;
						case ZaItem.COS:
							imgName = ("COS");
						break;
						case ZaItem.RESOURCE:
							if (account.attrs[ZaResource.A_zimbraCalResType] == ZaResource.RESOURCE_TYPE_LOCATION){
								imgName = ("Location");
							} else {//equipment or other resource types
								imgName = ("Resource");
							}
						break;
						default:
							html[idx++] = AjxStringUtil.htmlEncode(account.type);
						break;
					}

					if (imgName != null) {
						html[idx++] = AjxImg.getImageHtml(imgName);
					}
					html[idx++] = "</td>";
				} else if(field == ZaAccount.A_name) {
					// name
					html[idx++] = "<td align='left' width=" + this._headerList[i]._width + "><nobr>";
					html[idx++] = AjxStringUtil.htmlEncode(account.name);
					html[idx++] = "</nobr></td>";
				} else if (field == ZaAccount.A_displayname) {
					// display name
					html[idx++] = "<td align='left' width=" + this._headerList[i]._width + "><nobr>";
					html[idx++] = AjxStringUtil.htmlEncode(account.attrs[ZaAccount.A_displayname]);
					html[idx++] = "</nobr></td>";	
				} 
			}
		}
	} else if(typeof(account)=="object") {
		html[idx++] = "<td width=100%>";
		html[idx++] = AjxStringUtil.htmlEncode(account.name);
		html[idx++] = "</td>";
	} else {
		html[idx++] = "<td width=100%>";
		html[idx++] = AjxStringUtil.htmlEncode(String(account));		
		html[idx++] = "</td>";		
	}
	
	html[idx++] = "</tr></table>";
	div.innerHTML = html.join("");
	return div;
}


ZaAccMiniListView.prototype._setNoResultsHtml = function() {
	var buffer = new AjxBuffer();
	var	div = document.createElement("div");
	
	buffer.append("<table width='100%' cellspacing='0' cellpadding='1'>",
				  "<tr><td class='NoResults'><br>&nbsp",
				  "</td></tr></table>");
	
	div.innerHTML = buffer.toString();
	this._addRow(div);
};

ZaAccMiniListView.prototype._sortColumn = function (columnItem, bSortAsc){
	if(this.parent.parent.searchAccounts) {
		this.parent.parent.searchAccounts(columnItem.getSortField(),bSortAsc);
	}
};
