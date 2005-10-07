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
	this.addSelectionListener(new AjxListener(this, this._fullySelect));
}

ZaClusteredServicesListView.prototype = new ZaServicesListView;
ZaClusteredServicesListView.prototype.constructor = ZaClusteredServicesListView;

ZaClusteredServicesListView.prototype._getHeaderList = function() {

	var headerList = [
					  new ZaListHeaderItem(ZaStatus.PRFX_Server, ZaMsg.STV_Server_col, null, 175, true, "serverName", true, true),

					  new ZaListHeaderItem(ZaStatus.PRFX_Service, ZaMsg.STV_Service_col, null, 100, false, null, true, true),

					  new ZaListHeaderItem(ZaStatus.PRFX_Status, ZaMsg.STV_Status_col, null, 50, false, null, true, true),

					  new ZaListHeaderItem(ZaStatus.PRFX_Time, ZaMsg.STV_Time_col, null, 250, false, null, true, true),

					  new ZaListHeaderItem("ZaStatus.clustered", "Clustered", null, 50, false, null, true, true),

					  new ZaListHeaderItem("ZaStatus.clusteredStatus", "Cluster Node Status", null, null, false, null, true, true)

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
	html[idx++] = "<table class='ZaServicesListView_table'>";
	
	html[idx++] = "<tr>";
	var cnt = this._headerList.length;
	//var prevItem = this.getList().get(index - 1);
	var onlyServiceInfo = false;
	if (prevItem != null && prevItem.serverName == item.serverName) {
		onlyServiceInfo = true;
	} else {
		div._styleClass = ZaClusteredServicesListView.STYLE_CLASS;
		div._selectedStyleClass = ZaClusteredServicesListView.SELECTED_STYLE_CLASS;
		div.className = ZaClusteredServicesListView.STYLE_CLASS;
	}

	for(var i = 0; i < cnt; i++) {
		var id = this._headerList[i]._id;
		if(id.indexOf(ZaStatus.PRFX_Server)==0) {
			if (!onlyServiceInfo) {
				html[idx++] = "<td width=\"12px\" aligh=left onclick=\'javascript:AjxCore.objectWithId(" + this.__internalId + ")._expand(event, this)\'>";
				html[idx++] = AjxImg.getImageHtml("NodeExpanded");
				html[idx++] = "</td>";
				html[idx++] = "<td width=" + (this._headerList[i]._width-12) + " aligh=left>";
			} else {
				html[idx++] = "<td width=" + (this._headerList[i]._width) + " aligh=left>";
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
			html[idx++] = "<td width=" + this._headerList[i]._width + " aligh=left>";
			if (onlyServiceInfo) {
				html[idx++] = AjxStringUtil.htmlEncode(item.serviceName);
			} else {
				html[idx++] = AjxStringUtil.htmlEncode(" ");
			}
			html[idx++] = "</td>";
		} else if(id.indexOf(ZaStatus.PRFX_Time)==0) {
			html[idx++] = "<td width=" + this._headerList[i]._width + " aligh=left>";
			if (onlyServiceInfo){
				html[idx++] = AjxStringUtil.htmlEncode(item.time);
			} else {
				html[idx++] = AjxStringUtil.htmlEncode(" ");
			}
			html[idx++] = "</td>";
		} else if(id.indexOf("ZaStatus.clusteredStatus")==0) {
			html[idx++] = "<td width=" + this._headerList[i]._width + " aligh=left>";
			if (onlyServiceInfo){
				html[idx++] = "&nbsp;";
			} else if (item.clusterStatus != null) {
				html[idx++] = "<b>"
				html[idx++] = AjxStringUtil.htmlEncode(item.clusterStatus);
				html[idx++] = "</b>";
			}
			html[idx++] = "</td>";
		} else if(id.indexOf("ZaStatus.clustered")==0) {
			html[idx++] = "<td width=" + this._headerList[i]._width + " aligh=left>";
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
			html[idx++] = "<td width=" + this._headerList[i]._width + " aligh=left>";
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
	div.innerHTML = html.join("");

	if (onlyServiceInfo) {
		div._serviceInfo = true;
		div.onmousedown = ZaClusteredServicesListView.emptyHandler;
		div.onmouseup =  ZaClusteredServicesListView.emptyHandler;
		div.ondblclick =  ZaClusteredServicesListView.emptyHandler;
		div.onclick = ZaClusteredServicesListView.bubbleEventToParent;
		div._objId = this.__internalId;
	} else {
		div._serverInfo = true;
	}

	return div;

};

ZaClusteredServicesListView.bubbleEventToParent = function (event) {
	event = DwtUiEvent.getEvent(event);
	serviceDiv = DwtUiEvent.getTargetWithProp(event, "_serviceInfo");

	var sibling = serviceDiv.previousSibling;
	while (sibling != null && sibling._serverInfo != true) {
		sibling = sibling.previousSibling;
	}	
	if (sibling != null) {
		var serverDiv = sibling;
		// simulating a click event for the list view
		var lv = AjxCore.objectWithId(serviceDiv._objId);
		var ev = new Object();
		ev.target = serverDiv;
		ev.button = DwtMouseEvent.LEFT;
		ev.dwtObj = lv;
		lv._itemClicked(serverDiv, ev);
		lv._mouseUpAction(ev, serverDiv);		
	}
};
ZaClusteredServicesListView.prototype._expand = function (event, domObj) {
	var ev = DwtUiEvent.getEvent(event);
	var div = DwtUiEvent.getTargetWithProp(event, "_styleClass");
	var sibling = div.nextSibling;
	var collapse = true;
	if (sibling.style.display == "none"){
		domObj.firstChild.className = AjxImg.getClassForImage("NodeExpanded");
		collapse = false;
	} else {
		domObj.firstChild.className = AjxImg.getClassForImage("NodeCollapsed");
	}
	while (sibling != null && sibling._serviceInfo == true) {
		if (collapse){
			sibling.style.display = "none";
		} else {
			sibling.style.display = "";
		}
		sibling = sibling.nextSibling;
	}
	
};

ZaClusteredServicesListView.emptyHandler = function(event) {
	event = event? event: window.event;
	DwtUiEvent.setBehaviour(event, true, true);
};


ZaClusteredServicesListView.prototype._renderList = function (list) {
	if (list instanceof AjxVector && list.size()) {
		var size = list.size();
		var prevItem = null;
		for (var i = 0; i < size; i++) {
			var item = list.get(i);
			var div = this._createItemHtml(item, this._now, null, prevItem);
			if (div) {
				this._addRow(div);
			}
			if (prevItem == null || prevItem.serverName != item.serverName) {
				var div = this._createItemHtml(item, this._now, null, item);
				if (div) {
					this._addRow(div);
				}
			}
			prevItem = item;
		}
	} else {
		this._setNoResultsHtml();
	}
};

ZaClusteredServicesListView.prototype._changeClass = function (startDiv, newClass) {
	var sibling = startDiv.nextSibling;
	while (sibling != null && sibling._serviceInfo == true) {
		sibling.className = newClass;
		sibling = sibling.nextSibling;
	}	
};

ZaClusteredServicesListView.prototype._fullySelect = function (event) {
	if (this._lastSelection) {
		this._changeClass(this._lastSelection, " ");
	}
	var div = DwtUiEvent.getTargetWithProp(event, "_styleClass");
	this._changeClass(div, "selected");

	this._lastSelection = div;
	
};
