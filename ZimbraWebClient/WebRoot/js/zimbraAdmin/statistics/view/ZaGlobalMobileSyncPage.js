/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2005, 2006, 2007, 2008, 2009, 2010 Zimbra, Inc.
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
* @class ZaGlobalMobileSyncPage 
* @contructor ZaGlobalMobileSyncPage
* @param parent
* @author qin@zimbra.com
**/
ZaGlobalMobileSyncPage = function(parent) {
	DwtTabViewPage.call(this, parent);
}
 
ZaGlobalMobileSyncPage.prototype = new DwtTabViewPage;
ZaGlobalMobileSyncPage.prototype.constructor = ZaGlobalMobileSyncPage;

ZaGlobalMobileSyncPage.prototype.toString = 
function() {
	return "ZaGlobalMobileSyncPage";
}


ZaGlobalMobileSyncPage.prototype.showMe =  function(refresh) {
	this.getMobileSyncSessions();
	DwtTabViewPage.prototype.showMe.call(this);	
	if (refresh)
	    this.setObject(this._server);
}

ZaGlobalMobileSyncPage.prototype.setObject =
function (currentServer) {
	this._server = currentServer;
	
}

ZaGlobalMobileSyncPage.prototype._createHtml = 
function () {
	DwtTabViewPage.prototype._createHtml.call(this);
	var idx = 0;
	var html = new Array(20);
        html[idx++] = "<h1 style='display: none' id='loggerchartglobalmc-flashdetect'></h1>";
	html[idx++] = "<h3 style='padding-left: 10px'>" + ZaMsg.Stats_MobileSync_Header + "</h3>" ;	
        html[idx++] = "<div>";
        html[idx++] = "<tr valign='top'><td align='left'>&nbsp;&nbsp;</td></tr>";
        html[idx++] = "<tr><td>" +  ZaMsg.Stats_MobileSync_LOADING  + "</td></tr>";
        html[idx++] = "</div>";

	this.getHtmlElement().innerHTML = html.join("");
}

ZaGlobalMobileSyncPage.prototype.getMobileSyncSessions =
function (params) {
        var soapDoc = AjxSoapDoc.create("GetDevicesCountRequest", ZaZimbraAdmin.URN, null);
        if (!params) params = {} ;

        var getSessCmd = new ZmCsfeCommand ();
        params.soapDoc = soapDoc ;
        params.targetServer = this._server.id ;
        params.asyncMode = true ;
        params.callback = new AjxCallback (this, this.getMobileSyncSessionsCallback, [params]) ;
        getSessCmd.invoke(params) ;
}

ZaGlobalMobileSyncPage.prototype.getMobileSyncSessionsCallback =
function (reqParams, resp) {
	var resMsg = "";
        if (resp._data.Body && resp._data.Body.GetDevicesCountResponse) {
                var sessionStats = resp._data.Body.GetDevicesCountResponse ;
		resMsg = ZaMsg.Stats_MobileSync_Count + " " + sessionStats.count ;
        }
	else resMsg = ZaMsg.Stats_Unavailable;
        this._setUI(resMsg);
}

ZaGlobalMobileSyncPage.prototype._setUI =
function(resMsg) {
        var idx = 0;
        var html = new Array(20);
        html[idx++] = "<h1 style='display: none' id='loggerchartglobalmc-flashdetect'></h1>";
	html[idx++] = "<h3 style='padding-left: 10px'>" + ZaMsg.Stats_MobileSync_Header + "</h3>" ;	
        html[idx++] = "<div>";
        html[idx++] = "<tr valign='top'><td align='left'>&nbsp;&nbsp;</td></tr>";
        html[idx++] = "<tr><td>" + resMsg  + "</td></tr>";
        html[idx++] = "</div>";
        this.getHtmlElement().innerHTML = html.join("");

}
