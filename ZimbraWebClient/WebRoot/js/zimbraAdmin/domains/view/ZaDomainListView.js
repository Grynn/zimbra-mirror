/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2004, 2005, 2006, 2007, 2008, 2009, 2010, 2011, 2012, 2013 Zimbra Software, LLC.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.4 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 */

/**
* @constructor
* @class ZaDomainListView
* @param parent
* @author Roland Schemers
* @author Greg Solovyev
**/

ZaDomainListView = function(parent, listType) {

//	var className = "ZaDomainListView";
	var className = null;
	var posStyle = DwtControl.ABSOLUTE_STYLE;

    this._listType = listType;

	var headerList = this._getHeaderList();

	ZaListView.call(this, {
		parent:parent, 
		className:className, 
		posStyle:posStyle, 
		headerList:headerList,
		id: ZaId.TAB_DOMAIN_MANAGE,
		scrollLoading:true
	});

	this._appCtxt = this.shell.getData(ZaAppCtxt.LABEL);
	
}

ZaDomainListView.prototype = new ZaListView;
ZaDomainListView.prototype.constructor = ZaDomainListView;

ZaDomainListView.prototype.toString = 
function() {
	return "ZaDomainListView";
}

ZaDomainListView.prototype.getTitle = 
function () {
	return ZaMsg.Domain_view_title;
}

ZaDomainListView.prototype.getTabIcon =
function () {
	return "Domain" ;
}

/**
* Renders a single item as a DIV element.
*/
ZaDomainListView.prototype._createItemHtml =
function(domain, now, isDragProxy) {
	var html = new Array(50);
	var	div = document.createElement("div");
	div[DwtListView._STYLE_CLASS] = "Row";
	div[DwtListView._SELECTED_STYLE_CLASS] = div[DwtListView._STYLE_CLASS] + "-" + DwtCssStyle.SELECTED;
	div.className = div[DwtListView._STYLE_CLASS];
	this.associateItemWithElement(domain, div, DwtListView.TYPE_LIST_ITEM);
	
	var idx = 0;
	html[idx++] = "<table width='100%'>";
	html[idx++] = "<tr>";
	var cnt = this._headerList.length;
    var dwtId = Dwt.getNextId();
    var rowId = this._listType;
	for(var i = 0; i < cnt; i++) {
		var field = this._headerList[i]._field;
		var cellWidth = this._getCellWidth(i, {});
		if(field == "type") {
			// type
            var partialClassName = "Domain";
            var domainType = domain.attrs[ZaDomain.A_domainType];
            if (domainType === "alias") {
                partialClassName += "Alias";
            }
			html[idx++] = "<td id=\"" + rowId + "_data_type_" + dwtId + "\" width=" + this._headerList[i]._width + ">" + AjxImg.getImageHtml(partialClassName) + "</td>";
		} else if(field == ZaDomain.A_domainName) {
			// name
			html[idx++] = "<td id=\"" + rowId + "_data_name_" + dwtId + "\" align='left' width=" + cellWidth + ">";
			html[idx++] = AjxStringUtil.htmlEncode(domain.name);
			html[idx++] = "</td>";
		} else if(field == ZaDomain.A_description) {
			// description		
			html[idx++] = "<td id=\"" + rowId + "_data_desc_" + dwtId + "\" align='left' width=" + cellWidth + ">";
			html[idx++] = AjxStringUtil.htmlEncode(
				ZaItem.getDescriptionValue(domain.attrs[ZaDomain.A_description]));
			html[idx++] = "</td>";
		} else if(field == ZaDomain.A_zimbraDomainStatus) {
			// description		
			html[idx++] = "<td id=\"" + rowId + "_data_status_" + dwtId + "\" align='left' width=" + cellWidth + ">";
			html[idx++] = ZaDomain._domainStatus(domain.attrs[ZaDomain.A_zimbraDomainStatus]);
			html[idx++] = "</td>";
		} else if (field == ZaDomain.A_domainType) {
			// domain type
			html[idx++] = "<td id=\"" + rowId + "_data_domain_type_" + dwtId + "\" align='left' width=" + cellWidth + ">";
			html[idx++] = AjxStringUtil.htmlEncode(domain.attrs[ZaDomain.A_domainType]);
			html[idx++] = "</td>";
		}
	}
	html[idx++] = "</tr></table>";
	div.innerHTML = html.join("");
	return div;
}

ZaDomainListView.prototype._getHeaderList =
function() {

	var headerList = new Array();
	var sortable = 1;
	var i = 0 ;
	//idPrefix, label, iconInfo, width, sortable, sortField, resizeable, visible
	headerList[i++] = new ZaListHeaderItem("type", null, null, "22px", null, "objectClass", false, true);
	headerList[i++] = new ZaListHeaderItem(ZaDomain.A_domainName , ZaMsg.DLV_Name_col, null, "250px", sortable++, ZaDomain.A_domainName, true, true);
	//headerList[0].initialize(ZaMsg.CLV_Name_col, null, "245", true, ZaDomain.A_domainName);
	headerList[i++] = new ZaListHeaderItem(ZaDomain.A_domainType , ZaMsg.DLV_Type_col, null, "100px", null, ZaDomain.A_domainType, true, true);
	headerList[i++] = new ZaListHeaderItem(ZaDomain.A_zimbraDomainStatus , ZaMsg.DLV_Status_col, null, "100px", null, ZaDomain.A_zimbraDomainStatus, true, true);
	headerList[i++] = new ZaListHeaderItem(ZaDomain.A_description, ZaMsg.DLV_Description_col, null, "auto", null, null, true, true);
	//headerList[1].initialize(ZaMsg.CLV_Description_col, null, "245", false, ZaDomain.A_description);
	
	return headerList;
}


