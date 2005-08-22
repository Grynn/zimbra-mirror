/*
***** BEGIN LICENSE BLOCK *****
Version: ZPL 1.1

The contents of this file are subject to the Zimbra Public License Version 1.1 ("License");
You may not use this file except in compliance with the License. You may obtain a copy of
the License at http://www.zimbra.com/license

Software distributed under the License is distributed on an "AS IS" basis, WITHOUT WARRANTY
OF ANY KIND, either express or implied. See the License for the specific language governing
rights and limitations under the License.

The Original Code is: Zimbra Collaboration Suite.

The Initial Developer of the Original Code is Zimbra, Inc.
Portions created by Zimbra are Copyright (C) 2005 Zimbra, Inc.
All Rights Reserved.
Contributor(s): ______________________________________.

***** END LICENSE BLOCK *****
*/

/**
* @class ZaStatusServicesPage 
* @contructor ZaStatusServicesPage
* @param parent
* @param app
* @author Greg Solovyev
**/
function ZaStatusServicesPage (parent, app) {
	DwtTabViewPage.call(this, parent, "ZaStatusServicesPage", DwtControl.ABSOLUTE_STYLE);
	this._fieldIds = new Object(); //stores the ids of all the form elements
	this._app = app;
	this.initialized=false;
	this._rendered = false;
	this.setScrollStyle(DwtControl.SCROLL);	
}
 
ZaStatusServicesPage.prototype = new DwtTabViewPage;
ZaStatusServicesPage.prototype.constructor = ZaStatusServicesPage;

ZaStatusServicesPage.prototype.toString = 
function() {
	return "ZaStatusServicesPage";
}

ZaStatusServicesPage.prototype.showMe = 
function() {
	if(!this._rendered) {
		this._createHtml();		
	}
	var mystatusVector = this._app.getStatusList(true).getVector();
	this._statusListView.set(mystatusVector);
	DwtTabViewPage.prototype.showMe.call(this);
	this.getHtmlElement().style.width = '520px';
	DBG.println(AjxDebug.DBG3, "this._statusListView.getHtmlElement().offsetWidth: " + this._statusListView.getHtmlElement().offsetWidth);		
	DBG.println(AjxDebug.DBG3, "this._statusListView.getHtmlElement().clientWidth: " + this._statusListView.getHtmlElement().clientWidth);				
	DBG.println(AjxDebug.DBG3, "this.parent.getHtmlElement().offsetWidth: " + this.parent.getHtmlElement().offsetWidth);				
	
	DBG.println(AjxDebug.DBG3, "this.getHtmlElement().offsetWidth: " + this.getHtmlElement().offsetWidth);		
	DBG.println(AjxDebug.DBG3, "this.getHtmlElement().clientWidth: " + this.getHtmlElement().clientWidth);				
	DBG.println(AjxDebug.DBG3, "this.getHtmlElement().style.width: " + this.getHtmlElement().style.width);				
	
}

ZaStatusServicesPage.prototype._createHtml = 
function() {
	var idx = 0;
	var html = new Array(50);
	this._listContainerDivId = Dwt.getNextId();	
	html[idx++] = "<div style='width:520; height:520;'>";
	html[idx++] = "<table cellpadding='0' cellspacing='0' border='0' align='left' width='100%'>";	
	html[idx++] = "<tr valign='top'><td align='left' style='width:520;'>&nbsp</td></tr>";	
	html[idx++] = "<tr valign='top'><td align='left' style='width:520;'>";	
	html[idx++] = "<div id='" + this._listContainerDivId + "' style='width:520; height:520;'></div></td>";
	html[idx++] = "</td></tr></table>";
	html[idx++] = "</div>";
	this.getHtmlElement().innerHTML = html.join("");

	this._createUI();
	this._rendered=true;
}

ZaStatusServicesPage.prototype._createUI = 
function () {
	var htmlElement = this.getHtmlElement();
	var _contentDiv = Dwt.getDomObj(this.getDocument(), this._listContainerDivId);

	this._statusListView = new ZaStatusServicesPage_ZaListView(this);
//	alert(this._statusListView.getHtmlElement().style.width);
	//DBG.println(AjxDebug.DBG3, "width 1: " + this._statusListView.getHtmlElement().style.width + "width2: " + this._statusListView.getHtmlElement().width);
	this._statusListView.getHtmlElement().width='520';
	_contentDiv.appendChild(this._statusListView.getHtmlElement());
}

function ZaStatusServicesPage_ZaListView(parent) {
	if (arguments.length == 0) return;
	var posStyle = DwtControl.ABSOLUTE_STYLE;
	var headerList = this._getHeaderList();
	ZaListView.call(this, parent, null, posStyle, headerList);
}


ZaStatusServicesPage_ZaListView.prototype = new ZaListView;
ZaStatusServicesPage_ZaListView.prototype.constructor = ZaStatusServicesPage_ZaListView;

ZaStatusServicesPage_ZaListView.prototype.toString = 
function() {
	return "ZaStatusServicesPage_ZaListView";
}

ZaStatusServicesPage_ZaListView.prototype._getViewPrefix = 
function() {
	return "Status_Service";
}

ZaStatusServicesPage_ZaListView.prototype._createItemHtml = 
function(item) {
	var html = new Array(50);
	var	div = this.getDocument().createElement("div");
	div._styleClass = "Row";
	div._selectedStyleClass = div._styleClass + "-" + DwtCssStyle.SELECTED;
	div.className = div._styleClass;
	this.associateItemWithElement(item, div, DwtListView.TYPE_LIST_ITEM);

	var idx = 0;
	html[idx++] = "<table cellpadding=0 cellspacing=2 border=0 width=100%>";
	
	html[idx++] = "<tr>";
	var cnt = this._headerList.length;
	for(var i = 0; i < cnt; i++) {
		var id = this._headerList[i]._id;
		if(id.indexOf(ZaStatus.PRFX_Server)==0) {		
			html[idx++] = "<td width=" + this._headerList[i]._width + " aligh=left>";
			html[idx++] = AjxStringUtil.htmlEncode(item.serverName);
			html[idx++] = "</td>";
		} else if(id.indexOf(ZaStatus.PRFX_Service)==0) {		
			html[idx++] = "<td width=" + this._headerList[i]._width + " aligh=left>";
			html[idx++] = AjxStringUtil.htmlEncode(item.serviceName);
			html[idx++] = "</td>";
		} else if(id.indexOf(ZaStatus.PRFX_Time)==0) {
			html[idx++] = "<td width=" + this._headerList[i]._width + " aligh=left>";
			html[idx++] = AjxStringUtil.htmlEncode(item.time);
			html[idx++] = "</td>";
		} else if(id.indexOf(ZaStatus.PRFX_Status)==0) {
			html[idx++] = "<td width=" + this._headerList[i]._width + " aligh=left>";
			if(item.status==1) {
				html[idx++] = "On";
			} else {
				html[idx++] = "Off";
			}
			html[idx++] = "</td>";
		}
	}
	html[idx++] = "</tr></table>";
	div.innerHTML = html.join("");
	return div;
}

ZaStatusServicesPage_ZaListView.prototype._setNoResultsHtml = 
function() {
	var	div = this.getDocument().createElement("div");
	div.innerHTML = "<table width='100%' cellspacing='0' cellpadding='1'><tr><td class='NoResults'><br>Status data is not available.</td></tr></table>";
	this._parentEl.appendChild(div);
}

ZaStatusServicesPage_ZaListView.prototype._getHeaderList =
function() {

	var headerList = new Array();

	headerList[0] = new ZaListHeaderItem(ZaStatus.PRFX_Server, ZaMsg.STV_Server_col, null, 100, false, null, true, true);

	headerList[1] = new ZaListHeaderItem(ZaStatus.PRFX_Service, ZaMsg.STV_Service_col, null, 150, false, null, true, true);
	
	headerList[2] = new ZaListHeaderItem(ZaStatus.PRFX_Time, ZaMsg.STV_Time_col, null, 150, false, null, true, true);
	
	headerList[3] = new ZaListHeaderItem(ZaStatus.PRFX_Status, ZaMsg.STV_Status_col, null, null, false, null, true, true);
	
	return headerList;
}
