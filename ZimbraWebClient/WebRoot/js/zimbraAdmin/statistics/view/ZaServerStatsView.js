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
* @class ZaServerStatsView 
* @contructor ZaServerStatsView
* @param parent
* @param app
* @author Greg Solovyev
**/
function ZaServerStatsView(parent, app) {
	this._app = app;
	DwtTabView.call(this, parent);
	this._appCtxt = this.shell.getData(ZaAppCtxt.LABEL);
	this._msgCountPage = new ZaServerMessageCountPage(this, app);
	this._msgsVolumePage = new ZaServerMessageVolumePage(this, app);
	this._spamPage = new ZaServerSpamActivityPage(this, app);	
	this._diskPage = new ZaServerDiskStatsPage(this, app);	
	this._mbxPage = new ZaServerMBXStatsPage (this, app);
	this.addTab(ZaMsg.TABT_InMsgs, this._msgCountPage);		
	this.addTab(ZaMsg.TABT_InData, this._msgsVolumePage);			
	this.addTab(ZaMsg.TABT_Spam_Activity, this._spamPage);
	this.addTab(ZaMsg.TABT_Disk, this._diskPage);
	ZaServerMBXStatsPage.TAB_KEY = this.addTab(ZaMsg.TABT_MBX, this._mbxPage);	
		
	//this.setScrollStyle(DwtControl.SCROLL);
}

ZaServerStatsView.prototype = new DwtTabView;
ZaServerStatsView.prototype.constructor = ZaServerStatsView;

ZaServerStatsView.prototype.toString = 
function() {
	return "ZaServerStatsView";
}
 
/**
* @method setObject sets the object contained in the view
* @param entry - ZaServer object to display
**/
ZaServerStatsView.prototype.setObject =
function(entry) {
	this._msgCountPage.setObject(entry);
	this._msgsVolumePage.setObject(entry);
	this._spamPage.setObject(entry);
	this._diskPage.setObject(entry);
	this._mbxPage.setObject(entry);
	var szTitle = AjxStringUtil.htmlEncode(ZaMsg.NAD_ServerStatistics);
	if(entry.name) {
		szTitle = szTitle + entry.name;
	}
	this.titleCell.innerHTML = szTitle;
}

ZaServerStatsView.prototype._resetTabSizes = 
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

ZaServerStatsView.prototype._createHTML = 
function() {
	DwtTabView.prototype._createHTML.call(this);
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
	this.titleCell.innerHTML = AjxStringUtil.htmlEncode(ZaMsg.NAD_ServerStatistics);
	this.titleCell.className="AdminTitleBar";
}
