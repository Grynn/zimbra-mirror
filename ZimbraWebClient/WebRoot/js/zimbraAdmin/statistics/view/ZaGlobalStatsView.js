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
* @class ZaGlobalStatsView 
* @contructor ZaGlobalStatsView
* @param parent
* @param app
* @author Greg Solovyev
**/
ZaGlobalStatsView = function(parent) {

	DwtTabView.call(this, parent);
	
	this._appCtxt = this.shell.getData(ZaAppCtxt.LABEL);
	this._msgCountPage = new ZaGlobalMessageCountPage(this);
	this._msgsVolumePage = new ZaGlobalMessageVolumePage(this);
	this._spamPage = new ZaGlobalSpamActivityPage(this);	
	this.addTab(ZaMsg.TABT_InMsgs, this._msgCountPage);		
	this.addTab(ZaMsg.TABT_InData, this._msgsVolumePage);			
	this.addTab(ZaMsg.TABT_Spam_Activity, this._spamPage);				
//	this.setScrollStyle(DwtControl.SCROLL);
}

ZaGlobalStatsView.prototype = new DwtTabView;
ZaGlobalStatsView.prototype.constructor = ZaGlobalStatsView;

ZaGlobalStatsView.prototype.toString = 
function() {
	return "ZaGlobalStatsView";
}

ZaGlobalStatsView.prototype.setObject = function (entry) {
	this._containedObject = entry ;
	this._msgCountPage.setObject(entry);
	this._msgsVolumePage.setObject(entry);
	this._spamPage.setObject(entry);
}

ZaGlobalStatsView.prototype.getTitle = 
function () {
	return ZaMsg.GlobalStats_view_title;
}

ZaGlobalStatsView.prototype.getTabTitle = 
function () {
	return ZaMsg.GlobalStats_view_title;
}

ZaGlobalStatsView.prototype.getTabIcon =
function () {
	return "Statistics";
}

ZaGlobalStatsView.prototype.getTabToolTip =
function () {
	return ZaMsg.GlobalStats_view_title;
}

ZaGlobalStatsView.prototype._resetTabSizes = 
function (width, height) {
    var tabBarSize = this._tabBar.getSize();
	var titleCellSize = Dwt.getSize(this.titleCell);

	var tabBarHeight = tabBarSize.y || this._tabBar.getHtmlElement().clientHeight;
	var titleCellHeight = titleCellSize.y || this.titleCell.clientHeight;
		
	var tabWidth = width;
	var newHeight = (height - tabBarHeight - titleCellHeight);
	var tabHeight = ( newHeight > 50 ) ? newHeight : 50;
	
	if(this._tabs && this._tabs.length) {
		for(var curTabKey in this._tabs) {
			if(this._tabs[curTabKey]["view"]) {
				this._tabs[curTabKey]["view"].resetSize(tabWidth, tabHeight);
			}	
		}
	}		
}

ZaGlobalStatsView.prototype._createHtml = 
function() {
	DwtTabView.prototype._createHtml.call(this);
	
	//create a Title Table
	this._table = document.createElement("table") ;
		
	//this.getHtmlElement().appendChild(this._table) ;
	var htmlEl = this.getHtmlElement()
	htmlEl.insertBefore (this._table, htmlEl.firstChild);
	
	var row1;
	//var col1;
	var row2;
	var col2;
	row1 = this._table.insertRow(0);
	row1.align = "center";
	row1.vAlign = "middle";
	
	this.titleCell = row1.insertCell(row1.cells.length);
	this.titleCell.align = "center";
	this.titleCell.vAlign = "middle";
	this.titleCell.noWrap = true;	

	this.titleCell.id = Dwt.getNextId();
	this.titleCell.align="left";
	this.titleCell.innerHTML = AjxStringUtil.htmlEncode(ZaMsg.NAD_GlobalStatistics);
	this.titleCell.className="AdminTitleBar";
}