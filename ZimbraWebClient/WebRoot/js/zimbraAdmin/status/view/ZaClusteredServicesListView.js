/*
 * ***** BEGIN LICENSE BLOCK *****
 * Version: ZPL 1.1
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.1 ("License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.zimbra.com/license
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See
 * the License for the specific language governing rights and limitations
 * under the License.
 * 
 * The Original Code is: Zimbra Collaboration Suite.
 * 
 * The Initial Developer of the Original Code is Zimbra, Inc.
 * Portions created by Zimbra are Copyright (C) 2005 Zimbra, Inc.
 * All Rights Reserved.
 * 
 * Contributor(s):
 * 
 * ***** END LICENSE BLOCK *****
 */

function ZaClusteredServicesListView (parent, app) {
	ZaServicesListView.call(this, parent,app);
	this.setMultiSelect(false);
}

ZaClusteredServicesListView.prototype = new ZaServicesListView;
ZaClusteredServicesListView.prototype.constructor = ZaClusteredServicesListView;

ZaClusteredServicesListView.prototype._getHeaderList = function() {

	var headerList = [
					  new ZaListHeaderItem(ZaStatus.PRFX_Server, ZaMsg.STV_Server_col, null, 175, true, "serverName", true, true),

					  new ZaListHeaderItem("ZaStatus.physicalServer", "Physical Server", null, 150, false, null, true, true),

					  new ZaListHeaderItem("ZaStatus.clustered", "Clustered", null, 50, false, null, true, true),

					  new ZaListHeaderItem("ZaStatus.clusteredStatus", "Status", null, 60, false, null, true, true),

					  new ZaListHeaderItem(ZaStatus.PRFX_Service, ZaMsg.STV_Service_col, null, 50, false, null, true, true),

					  new ZaListHeaderItem(ZaStatus.PRFX_Status, ZaMsg.STV_Status_col, null, 50, false, null, true, true),

					  new ZaListHeaderItem(ZaStatus.PRFX_Time, ZaMsg.STV_Time_col, null, null, false, null, true, true)
					  ];
	
	return headerList;
};

ZaClusteredServicesListView.STYLE_CLASS = "Row";
ZaClusteredServicesListView.SELECTED_STYLE_CLASS = "Row" + "-" + DwtCssStyle.SELECTED;

ZaClusteredServicesListView.prototype._createItemHtml = function(item, now, isDndIcon, prevItem) {
	var html = new Array(50);
	var	div = this.getDocument().createElement("div");
	this.associateItemWithElement(item, div, DwtListView.TYPE_LIST_ITEM);

	var idx = 0;
	div._styleClass = ZaClusteredServicesListView.STYLE_CLASS;
	div._selectedStyleClass = ZaClusteredServicesListView.SELECTED_STYLE_CLASS;
	div.className = ZaClusteredServicesListView.STYLE_CLASS;
	
	idx = this._writeElement(html, idx, item, false);
	for (var i = 0; i < item.services.length; ++i) {
		idx = this._writeElement(html, idx, item.services[i], true);
	}
	div.innerHTML = html.join("");

	return div;

};

ZaClusteredServicesListView.prototype._writeElement = function (html, idx, item, onlyServiceInfo) {
	html[idx++] = "<table class='ZaServicesListView_table'";	
	html[idx++] = "_serviceInfo=";
	html[idx++] = onlyServiceInfo;
	html[idx++] = ">";
	html[idx++] = "<tr>";
	var cnt = this._headerList.length;
	for(var i = 0; i < cnt; i++) {
		var id = this._headerList[i]._id;
		if(id.indexOf(ZaStatus.PRFX_Server)==0) {
			if (!onlyServiceInfo) {
				html[idx++] = "<td width=\"12px\" aligh=left onclick=\'javascript:AjxCore.objectWithId(";
				html[idx++] = this.__internalId;
				html[idx++] = ")._expand(event, this)\'>";
				html[idx++] = AjxImg.getImageHtml("NodeExpanded");
				html[idx++] = "</td>";
				html[idx++] = "<td width=";
				html[idx++] = (this._headerList[i]._width-12);
				html[idx++] = " aligh=left>";
			} else {
				html[idx++] = "<td width=";
				html[idx++] = (this._headerList[i]._width);
				html[idx++] = " aligh=left>";
			}

			if (onlyServiceInfo){
				html[idx++] = AjxStringUtil.htmlEncode(" ");
			} else {
				html[idx++] = "<b>"
				html[idx++] = AjxStringUtil.htmlEncode(item.serverName);
				html[idx++] = "</b>"
			}
			html[idx++] = "</td>";
		} else if(id.indexOf(ZaStatus.PRFX_Service)==0) {		
			html[idx++] = "<td width=";
			html[idx++] = this._headerList[i]._width;
			html[idx++] = " aligh=left>";
			if (onlyServiceInfo) {
				html[idx++] = AjxStringUtil.htmlEncode(item.serviceName);
			} else {
				html[idx++] = AjxStringUtil.htmlEncode(" ");
			}
			html[idx++] = "</td>";
		} else if(id.indexOf(ZaStatus.PRFX_Time)==0) {
			html[idx++] = "<td width=";
			html[idx++] = this._headerList[i]._width;
			html[idx++] = " aligh=left>";
			if (onlyServiceInfo){
				html[idx++] = AjxStringUtil.htmlEncode(item.time);
			} else {
				html[idx++] = AjxStringUtil.htmlEncode(" ");
			}
			html[idx++] = "</td>";
		} else if(id.indexOf("ZaStatus.clusteredStatus")==0) {
			html[idx++] = "<td width=";
			html[idx++] = this._headerList[i]._width;
			html[idx++] = " aligh=left>";
			if (onlyServiceInfo){
				html[idx++] = "&nbsp;";
			} else if (item.clusterStatus != null) {
				html[idx++] = "<b>"
				html[idx++] = AjxStringUtil.htmlEncode(item.clusterStatus);
				html[idx++] = "</b>";
			}
			html[idx++] = "</td>";
		} else if (id.indexOf("ZaStatus.physicalServer")==0){
			html[idx++] = "<td width=";
			html[idx++] = this._headerList[i]._width;
			html[idx++] = " aligh=left>";
			if (onlyServiceInfo) {
				html[idx++] = AjxStringUtil.htmlEncode(" ");
			} else {
				html[idx++] = "<b>";
				html[idx++] = AjxStringUtil.htmlEncode(item.physicalServerName);
				html[idx++] = "</b>";
			}
		} else if(id.indexOf("ZaStatus.clustered")==0) {
			html[idx++] = "<td width=";
			html[idx++] = this._headerList[i]._width;
			html[idx++] = " aligh=left>";
			if (onlyServiceInfo) {
					html[idx++] = "&nbsp;";
			} else {
				if (item.clusterStatus == null) {
					html[idx++] = "<b>No</b>";
				} else {
					html[idx++] = "<b>Yes</b>";
				}
			}
			html[idx++] = "</td>";
		} else if(id.indexOf(ZaStatus.PRFX_Status)==0) {
			html[idx++] = "<td width=";
			html[idx++] = this._headerList[i]._width;
			html[idx++] = " aligh=left>";
			if (onlyServiceInfo) {
				if(item.status==1) {
					html[idx++] = AjxImg.getImageHtml("Check");
				} else {
					html[idx++] = AjxImg.getImageHtml("Cancel");
				}
			} else {
				html[idx++] = AjxStringUtil.htmlEncode(" ");
			}
			html[idx++] = "</td>";
		}
	}
	html[idx++] = "</tr></table>";
	return idx;
};

ZaClusteredServicesListView.getAncestorWithTag = function (ev, tagName) {
	var htmlEl = DwtUiEvent.getTarget(ev);
	while (htmlEl) {
		if (htmlEl.tagName == tagName) {
			return htmlEl;
		}
		htmlEl = htmlEl.parentNode;
	}
	return null;
};

ZaClusteredServicesListView.prototype._expand = function (event, domObj) {
	var ev = DwtUiEvent.getEvent(event);
	var htmlEl = DwtUiEvent.getTarget(ev);
	//var table = DwtUiEvent.getTargetWithProp(event, "_serviceInfo");
	var table = ZaClusteredServicesListView.getAncestorWithTag(event, "TABLE");
	var sibling = table.nextSibling;
	var collapse = true;
	if (sibling != null) {
		if (sibling.style.display == "none"){
			domObj.firstChild.className = AjxImg.getClassForImage("NodeExpanded");
			collapse = false;
		} else {
			domObj.firstChild.className = AjxImg.getClassForImage("NodeCollapsed");
		}
		while (sibling != null && sibling.getAttribute("_serviceInfo") == "true") {
			if (collapse){
				sibling.style.display = "none";
			} else {
				sibling.style.display = "";
			}
			sibling = sibling.nextSibling;
		}
	} else {
		domObj.firstChild.className = "";
	}
	
};
