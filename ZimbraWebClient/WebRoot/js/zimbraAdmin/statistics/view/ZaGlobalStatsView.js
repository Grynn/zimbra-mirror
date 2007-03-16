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
 * Portions created by Zimbra are Copyright (C) 2004, 2005, 2006 Zimbra, Inc.
 * All Rights Reserved.
 * 
 * Contributor(s):
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
function ZaGlobalStatsView(parent, app) {
	this._app = app;
	DwtTabView.call(this, parent);
	//BEATS ME! Not sure why ZaGlobalStatsView.prototype._createHTML is not called in DwtTabView initialization
	//So have to call it explicitly HERE.
	//TODO: Temparary solution to fix the troubles by Templates. Need to formally fix it in the future by using templates.
	this._createHTML ();
	
	this._appCtxt = this.shell.getData(ZaAppCtxt.LABEL);
	this._msgCountPage = new ZaGlobalMessageCountPage(this, app);
	this._msgsVolumePage = new ZaGlobalMessageVolumePage(this, app);
	this._spamPage = new ZaGlobalSpamActivityPage(this, app);	
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

ZaGlobalStatsView.prototype.getTitle = 
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

ZaGlobalStatsView.prototype._createHTML = 
function() {
	//DwtTabView.prototype._createHTML.call(this);
	this._table = document.createElement("table") ;
	this.getHtmlElement().appendChild(this._table) ;
	
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