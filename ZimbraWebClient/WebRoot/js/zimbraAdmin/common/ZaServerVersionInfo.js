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


function ZaServerVersionInfo() {}

ZaServerVersionInfo.load = function () {
	if (!ZaServerVersionInfo._loaded){
		var soapDoc = AjxSoapDoc.create("BatchRequest", "urn:zimbra");
		soapDoc.setMethodAttribute("onerror", "continue");
		var versionInfoReq = soapDoc.set("GetVersionInfoRequest");
		versionInfoReq.setAttribute("xmlns", "urn:zimbraAdmin");

		var licenseInfoReq = soapDoc.set("GetLicenseInfoRequest");
		licenseInfoReq.setAttribute("xmlns", "urn:zimbraAdmin");

		var resp = ZmCsfeCommand.invoke(soapDoc, true, null, null, false).Body.BatchResponse;
		var versionResponse = resp.GetVersionInfoResponse[0];

		ZaServerVersionInfo.buildDate = this._parseDateTime(versionResponse.info[0].buildDate);
		ZaServerVersionInfo.host = versionResponse.info[0].host;
		ZaServerVersionInfo.release = versionResponse.info[0].release;
		ZaServerVersionInfo.version = versionResponse.info[0].version;
		ZaServerVersionInfo.licenseExists = false;
		if (resp.GetLicenseInfoResponse[0].expiration != null) {
			var licenseResponse = resp.GetLicenseInfoResponse[0].expiration[0];
			if (licenseResponse.date != null && licenseResponse.date != ""){
				ZaServerVersionInfo.licenseExists = true;
				ZaServerVersionInfo.licenseExpirationDate = ZaServerVersionInfo._parseDate(licenseResponse.date);
				var now = new Date();
				ZaServerVersionInfo.licenseExpired = (now.getTime() > ZaServerVersionInfo.licenseExpirationDate.getTime());
			}
		} else {
			ZaServerVersionInfo.licenseExpired = false;
			ZaServerVersionInfo.licenseExists = false;
		}
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
