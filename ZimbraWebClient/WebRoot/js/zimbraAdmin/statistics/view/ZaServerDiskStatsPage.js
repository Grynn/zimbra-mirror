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
* @class ZaServerDiskStatsPage
* @contructor ZaServerDiskStatsPage
* @param parent
* @param app
* @author Greg Solovyev
**/
function ZaServerDiskStatsPage (parent, app) {
	this._app = app;
	DwtTabViewPage.call(this, parent);
	this._fieldIds = new Object(); //stores the ids of all the form elements
	this.cellId = Dwt.getNextId();
	this._rendered = false;
	this.internalView = null;
	this._createHTML();
}

ZaServerDiskStatsPage.prototype = new DwtTabViewPage;
ZaServerDiskStatsPage.prototype.constructor = ZaServerDiskStatsPage;

ZaServerDiskStatsPage.prototype.toString = 
function() {
	return "ZaServerDiskStatsPage";
}

ZaServerDiskStatsPage.prototype.showMe = 
function() {
	if(this.internalView !=null) {
		if(!this._rendered) {
			var elem = Dwt.getDomObj(this.getDocument(), this.cellId);
			elem.appendChild(this.internalView.getHtmlElement());
			this._rendered = true;
		}	

		DwtTabViewPage.prototype.showMe.call(this);
		this.internalView.getHtmlElement().style.height=this.getHtmlElement().style.height;
		this.internalView.getHtmlElement().style.width=this.getHtmlElement().style.width;	
		this.internalView.switchToTab(this.internalView.firstTabKey); 				
	}
}

ZaServerDiskStatsPage.prototype.setObject = 
function (entry) {
	if(this.internalView==null)
		this.internalView = new ZaServerDiskStatsTabPage(this, this._app);	
		
	this.internalView.setObject(entry);
}

ZaServerDiskStatsPage.prototype._createHTML = 
function () {
 	var idx = 0;
	var html = new Array(5);
//	html[idx++] = "<div style='width:85ex;'>";	
	html[idx++] = "<table cellpadding='0' cellspacing='0' border='0' align='left' style='width:85ex;table-layout:fixed;'>";	
	html[idx++] = "<tr valign='top'><td align='left'><div style='width:85ex;' id='" + this.cellId + "'>&nbsp;<br>&nbsp;</div>";
	html[idx++] = "</td></tr></table>";	
	html[idx++] = "</div>";
	this.getHtmlElement().innerHTML = html.join("");
}
/**
* @class ZaServerDiskStatsPage 
* @contructor ZaServerDiskStatsPage
* @param parent
* @param app
* @author Greg Solovyev
**/
function ZaServerDiskStatsTabPage(parent, app) {
	this._app = app;
	DwtTabView.call(this, parent);
	this._appCtxt = this.shell.getData(ZaAppCtxt.LABEL);
	this._1DPage = new ZaServerDiskStatsPageD(this, app);
	this._3MPage = new ZaServerDiskStatsPage3M(this, app);
	this._12MPage = new ZaServerDiskStatsPage12M(this, app);	
	this.firstTabKey = this.addTab(ZaMsg.TABT_StatsDataLastDay, this._1DPage);		
	this.addTab(ZaMsg.TABT_StatsDataLast3Months, this._3MPage);			
	this.addTab(ZaMsg.TABT_StatsDataLast12Months, this._12MPage);				
//	this.setScrollStyle(DwtControl.SCROLL);
}

ZaServerDiskStatsTabPage.prototype = new DwtTabView;
ZaServerDiskStatsTabPage.prototype.constructor = ZaServerDiskStatsTabPage;

ZaServerDiskStatsTabPage.prototype.toString = 
function() {
	return "ZaServerDiskStatsTabPage";
}

ZaServerDiskStatsTabPage.prototype.setObject = 
function (entry) {
	this._1DPage.setObject(entry);
	this._3MPage.setObject(entry);
	this._12MPage.setObject(entry);
}

ZaServerDiskStatsTabPage.prototype._createHTML = 
function() {
	DwtTabView.prototype._createHTML.call(this);
}