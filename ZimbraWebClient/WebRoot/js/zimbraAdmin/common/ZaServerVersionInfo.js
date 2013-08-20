/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2005, 2006, 2007, 2008, 2009, 2010, 2013 Zimbra Software, LLC.
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


ZaServerVersionInfo = function() {}

ZaServerVersionInfo.load = function () {
	if (!ZaServerVersionInfo._loaded){
		var soapDoc = AjxSoapDoc.create("BatchRequest", "urn:zimbra");
		soapDoc.setMethodAttribute("onerror", "continue");
		var versionInfoReq = soapDoc.set("GetVersionInfoRequest", null, null, ZaZimbraAdmin.URN);
		
		
		//var licenseInfoReq = soapDoc.set("GetLicenseInfoRequest");
		//licenseInfoReq.setAttribute("xmlns", ZaZimbraAdmin.URN);
		var command = new ZmCsfeCommand();
		var params = new Object();
		params.soapDoc = soapDoc;	
		params.noAuthToken = true;
		var resp = command.invoke(params).Body.BatchResponse;		
		var versionResponse = resp.GetVersionInfoResponse[0];

		ZaServerVersionInfo.buildDate = this._parseDateTime(versionResponse.info[0].buildDate);
		ZaServerVersionInfo.host = versionResponse.info[0].host;
		ZaServerVersionInfo.release = versionResponse.info[0].release;
		ZaServerVersionInfo.version = versionResponse.info[0].version;
		//license expiration information is handled in com_zimbra_license.js
	}
};

ZaServerVersionInfo._parseDate = function (dateTimeStr) {
	var d = new Date();
	d.setHours(0, 0, 0, 0);
	var yyyy = parseInt(dateTimeStr.substr(0,4), 10);
	var MM = parseInt(dateTimeStr.substr(4,2), 10);
	var dd = parseInt(dateTimeStr.substr(6,2), 10);
	d.setFullYear(yyyy);
	// EMC 8/31/05 - fix for bug 3839. It looks like firefox needs to call setMonth twice for 
	// dates starting sept 1. No good reason at this point, but I noticed that
	// setting it twice seems to do the trick. Very odd.
	d.setMonth(MM - 1);
	d.setMonth(MM - 1);
	d.setDate(dd);
	return d;
};
ZaServerVersionInfo._parseDateTime = function (dateTimeStr) {
	var d = ZaServerVersionInfo._parseDate(dateTimeStr);
	var hh = parseInt(dateTimeStr.substr(9,2), 10);
	var mm = parseInt(dateTimeStr.substr(11,2), 10);
	d.setHours(hh, mm, 0, 0);
	return d;
};
