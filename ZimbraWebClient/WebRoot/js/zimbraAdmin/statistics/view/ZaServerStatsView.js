/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2004, 2005, 2006, 2007, 2008, 2009, 2010 Zimbra, Inc.
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
* @class ZaServerStatsView 
* @contructor ZaServerStatsView
* @param parent
* @param app
* @author Greg Solovyev
**/
ZaServerStatsView = function(parent) {

	DwtTabView.call(this, parent);
	
	this._appCtxt = this.shell.getData(ZaAppCtxt.LABEL);
//    if(ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.SERVER_STATS_MSG_COUNT_TAB] || ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.CARTE_BLANCHE_UI]) {
        this._msgCountPage = new ZaServerMessageCountPage(this);
        this.addTab(ZaMsg.TABT_InMsgs, this._msgCountPage);
//    }

//    if(ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.SERVER_STATS_MSG_VOL_TAB] || ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.CARTE_BLANCHE_UI]) {
        this._msgsVolumePage = new ZaServerMessageVolumePage(this);
        this.addTab(ZaMsg.TABT_InData, this._msgsVolumePage);
//    }

  //  if(ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.SERVER_STATS_MSG_ASAV_TAB] || ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.CARTE_BLANCHE_UI]) {
        this._spamPage = new ZaServerSpamActivityPage(this);
        this.addTab(ZaMsg.TABT_Spam_Activity, this._spamPage);
//    }

  //  if(ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.SERVER_STATS_DISK_TAB] || ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.CARTE_BLANCHE_UI]) {
        this._diskPage = new ZaServerDiskStatsPage(this);
        this.addTab(ZaMsg.TABT_Disk, this._diskPage);
//    }

  //  if(ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.SERVER_STATS_SESSION_TAB] || ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.CARTE_BLANCHE_UI]) {
        this._sessionPage = new ZaServerSessionStatsPage(this);
        this.addTab(ZaMsg.TABT_Session, this._sessionPage);
//    }

  //  if(ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.SERVER_STATS_QUOTA_TAB] || ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.CARTE_BLANCHE_UI]) {
        this._mbxPage = new ZaServerMBXStatsPage (this);
        ZaServerMBXStatsPage.TAB_KEY = this.addTab(ZaMsg.TABT_MBX, this._mbxPage);
   // }
}

ZaServerStatsView.prototype = new DwtTabView;
ZaServerStatsView.prototype.constructor = ZaServerStatsView;

ZaServerStatsView.prototype.toString = 
function() {
	return "ZaServerStatsView";
}

ZaServerStatsView.prototype.getTabToolTip =
function () {
	if (this._containedObject) {
		return	ZaMsg.tt_tab_View + " " + this._containedObject.type + " " + this._containedObject.name + " " + ZaMsg.tt_tab_Statistics ;
	}else{
		return "" ;
	}
}

ZaServerStatsView.prototype.getTabIcon = 
function () {
	return "StatisticsByServer" ;
}

ZaServerStatsView.prototype.getTabTitle =
function () {
	if (this._containedObject) {
		return this._containedObject.name ;
	}else{
		return "" ;
	}
}
 
ZaServerStatsView.prototype.updateTab =
function () {
	var tab = this.getAppTab ();
	tab.resetLabel (this.getTabTitle()) ;
	tab.setImage (this.getTabIcon());
	tab.setToolTipContent (this.getTabToolTip()) ;
}

ZaServerStatsView.prototype.getAppTab =
function () {
	return ZaApp.getInstance().getTabGroup().getTabById(this.__internalId) ;
} 
 
/**
* @method setObject sets the object contained in the view
* @param entry - ZaServer object to display
**/
ZaServerStatsView.prototype.setObject =
function(entry) {
	this._containedObject = entry ;
	this._msgCountPage.setObject(entry);
	this._msgsVolumePage.setObject(entry);
	this._spamPage.setObject(entry);
	this._diskPage.setObject(entry);
	this._mbxPage.setObject(entry);
	this._sessionPage.setObject(entry) ;
	var szTitle = AjxStringUtil.htmlEncode(ZaMsg.NAD_ServerStatistics);
	if(entry.name) {
		szTitle = szTitle + entry.name;
	}
	this.titleCell.innerHTML = szTitle;
	this.updateTab ();
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

ZaServerStatsView.prototype._createHtml = 
function() {
	DwtTabView.prototype._createHtml.call(this);
	this._table = document.createElement("table") ;
	var htmlEl = this.getHtmlElement()
	htmlEl.insertBefore (this._table, htmlEl.firstChild);
	//this.getHtmlElement().appendChild(this._table) ;
	
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
