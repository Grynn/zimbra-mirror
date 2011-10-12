/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Zimlets
 * Copyright (C) 2009, 2010, 2011 Zimbra, Inc.
 *
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.3 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 * @Author Raja Rao DV
 *
 */

function ZmClick2CallProviderAPIs(zimlet, server, email, password) {
	this.zimlet = zimlet;
	this._server = server;
	this._email = email;
	this._password = password;
}

ZmClick2CallProviderAPIs.prototype.getDestinationsXML = function() {
	var html = [];
	html.push("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:mod=\"http://www.mitel.com/ucs/ws/model\">"
			, "<soapenv:Header/>"
			, "<soapenv:Body>"
			, "<mod:GetDestinations/>"
			, "</soapenv:Body>"
			, "</soapenv:Envelope>");

	return html.join("");
};

ZmClick2CallProviderAPIs.prototype.getAccountInfoXML = function() {
	var html = [];
	html.push("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:mod=\"http://www.mitel.com/ucs/ws/model\">"
			, "<soapenv:Header/>"
			, "<soapenv:Body>"
			, "<mod:GetAccountInfo/>"
			, "</soapenv:Body>"
			, "</soapenv:Envelope>");

	return html.join("");
};

ZmClick2CallProviderAPIs.prototype.getPhysicalDeviceInformationXML = function(deviceId) {
	var html = [];
	html.push("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ed4=\"http://www.ecma-international.org/standards/ecma-323/csta/ed4\">"
			, "<soapenv:Header/>"
			, "<soapenv:Body>"
			, "<ed4:GetPhysicalDeviceInformation>"
			, "<ed4:device>", deviceId, "</ed4:device>"
			, "</ed4:GetPhysicalDeviceInformation>"
			, "</soapenv:Body>"
			, "</soapenv:Envelope>");

	return html.join("");
};

ZmClick2CallProviderAPIs.prototype.getMakeCallXML = function(fromPhoneNumber, toPhoneNumber) {
	var html = [];
	html.push("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ed4=\"http://www.ecma-international.org/standards/ecma-323/csta/ed4\">"
			, "<soapenv:Header/>"
			, "<soapenv:Body>"
			, "<ed4:MakeCall>"
			, "<ed4:callingDevice>", fromPhoneNumber, "</ed4:callingDevice>"
			, "<ed4:calledDirectoryNumber>", toPhoneNumber, "</ed4:calledDirectoryNumber>"
			, "</ed4:MakeCall>"
			, "</soapenv:Body>"
			, "</soapenv:Envelope>");

	return html.join("");
};

ZmClick2CallProviderAPIs.prototype.getCallLogsXML = function(type) {
	var html = [];
	html.push("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:mod=\"http://www.mitel.com/ucs/ws/model\">"
			, "<soapenv:Header/>"
			, "<soapenv:Body>"
			, "<mod:GetCallLogs>"
			, "<mod:type>", type, "</mod:type>"
			, "</mod:GetCallLogs>"
			, "</soapenv:Body>"
			, "</soapenv:Envelope>");

	return html.join("");
};

ZmClick2CallProviderAPIs.prototype.getStartSessionRequestXML = function(mailboxId, pin) {
	var html = [];
	html.push("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:urn=\"urn:NpViewService\">"
			, "<soapenv:Header/>"
			, "<soapenv:Body>"
			, "<urn:StartSessionRequest>"
			, "<Mailbox>", mailboxId, "</Mailbox>"
			, "<Pin>", pin, "</Pin>"
			, "</urn:StartSessionRequest>"
			, "</soapenv:Body>"
			, "</soapenv:Envelope>");

	return html.join("");
};

ZmClick2CallProviderAPIs.prototype.getMessagesRequestXML = function(sessionHandle) {
	var html = [];
	html.push("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:urn=\"urn:NpViewService\">"
			, "<soapenv:Header/>"
			, "<soapenv:Body>"
			, "<urn:GetMessagesRequest>"
			, "<SessionHandle>", sessionHandle, "</SessionHandle>"
			, "</urn:GetMessagesRequest>"
			, "</soapenv:Body>"
			, "</soapenv:Envelope>");

	return html.join("");
};

ZmClick2CallProviderAPIs.prototype.getMessageUrlRequestXML = function(sessionHandle, recordingId) {
	var html = [];
	html.push("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:urn=\"urn:NpViewService\">"
			, "<soapenv:Header/>"
			, "<soapenv:Body>"
			, "<urn:GetMessageUrlRequest>"
			, "<SessionHandle>", sessionHandle, "</SessionHandle>"
			, "<RecordingID>", recordingId, "</RecordingID>"
			, "</urn:GetMessageUrlRequest>"
			, "</soapenv:Body>"
			, "</soapenv:Envelope>");

	return html.join("");
};

ZmClick2CallProviderAPIs.prototype.getEndSessionRequestXML = function (sessionHandle) {
	var html = [];
	html.push("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:urn=\"urn:NpViewService\">"
			, "<soapenv:Header/>"
			, "<soapenv:Body>"
			, "<urn:EndSessionRequest>"
			, "<SessionHandle>", sessionHandle, "</SessionHandle>"
			, "</urn:EndSessionRequest>"
			, "</soapenv:Body>"
			, "</soapenv:Envelope>");

	return html.join("");
};

ZmClick2CallProviderAPIs.prototype.getClearConnectionXML = function (physicalDeviceId) {
	var html = [];
	html.push("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ed4=\"http://www.ecma-international.org/standards/ecma-323/csta/ed4\">"
   	, "<soapenv:Header/>"
	, "<soapenv:Body>"
    , "<ed4:ClearConnection>"
	, "<ed4:connectionToBeCleared>"
    , "<ed4:deviceID>" ,physicalDeviceId ,"</ed4:deviceID>"
    , "</ed4:connectionToBeCleared>"
    ,  "</ed4:ClearConnection>"
    , "</soapenv:Body>"
	, "</soapenv:Envelope>");

	return html.join("")
};

ZmClick2CallProviderAPIs.prototype.doClick2Call = function (fromPhoneNumber, toPhoneNumber, postCallback) {
	 	var url = [ "https://", this._server, "/ucs/ws/services/csta" ]
			.join("");
	var xml = this.getMakeCallXML(fromPhoneNumber,
			toPhoneNumber);
	var hdrs = new Array();
	hdrs["Authorization"] = this.make_basic_auth(this._email,
			this._password);
	hdrs["content-type"] = "text/xml";
	hdrs["content-length"] = xml.length;
	var feedUrl = ZmZimletBase.PROXY + AjxStringUtil.urlComponentEncode(url);
	var callback = new AjxCallback(this,this._call2CallHandler, postCallback);
	AjxRpc.invoke(xml, feedUrl, hdrs, callback, false);
};

ZmClick2CallProviderAPIs.prototype._call2CallHandler = function(postCallback, response) {
	var jsonObj;
	var result = {};
	if (!response.success) {
		jsonObj = this.xmlToObject(response);
		if (jsonObj && jsonObj.Body && jsonObj.Body.Fault
				&& jsonObj.Body.Fault.faultstring) {
			result.success = false;
			result.error =  jsonObj.Body.Fault.faultstring.toString();
		} else { //show generic error & redial btn
			result.success = false;
			result.error =  "";
		}
	} else {
		jsonObj = this.xmlToObject(response);
		if (jsonObj.Body && jsonObj.Body.MakeCallResponse) {
			result.success = true;
		}
	}
	if(postCallback) {
		postCallback.run(result);
	}
};

ZmClick2CallProviderAPIs.prototype.doHangUp = function (fromPhoneNumber, postCallback) {
	var tmp = fromPhoneNumber.split(":");
	var deviceId = tmp[1] + ":" + tmp[2];
	var url = [ "https://", this._server, "/ucs/ws/services/csta" ].join("");
	var xml = this.getClearConnectionXML(deviceId);
	var hdrs = new Array();
	hdrs["Authorization"] = this.make_basic_auth(this._email,
			this._password);
	hdrs["content-type"] = "text/xml";
	hdrs["content-length"] = xml.length;
	var feedUrl = ZmZimletBase.PROXY + AjxStringUtil.urlComponentEncode(url);
	AjxRpc.invoke(xml, feedUrl, hdrs, new AjxCallback(this,
			this._hangupHandler, postCallback), false);
};

ZmClick2CallProviderAPIs.prototype._hangupHandler = function(postCallback, response) {
	var result = {};
	/**  API DOESNT WORK PROPERLY - Mitel is investigating
	var jsonObj = this.xmlToObject(response);
	var result = {};
	if (!response.success) {
		if (jsonObj && jsonObj.Body && jsonObj.Body.Fault
				&& jsonObj.Body.Fault.faultstring) {
			result.success = false;
			result.error =  jsonObj.Body.Fault.faultstring.toString();
		} else { //show generic error & redial btn
			result.success = false;
			result.error =  "";
		}
	} else {
		jsonObj = this.xmlToObject(response);
		if (jsonObj.Body) {
			result.success = true;
		}
	}*/
	result = {success:true};
	if(postCallback) {
		postCallback.run(result);
	}
};

ZmClick2CallProviderAPIs.prototype.make_basic_auth = function(user, password) {
	var tok = user + ':' + password;
	var hash = Base64.encode(tok);
	return "Basic " + hash;
};

ZmClick2CallProviderAPIs.prototype.xmlToObject = function(result, dontConvertToJSObj) {
	if(!result.success) {
		this.zimlet.displayErrorMessage(this.zimlet.getMessage("coulNotConnect"), result.text, this.zimlet.getMessage("providerError"));
		return;
	}
	if (dontConvertToJSObj) {
		var xd = new AjxXmlDoc.createFromDom(result.xml);
	} else {
		var xd = new AjxXmlDoc.createFromDom(result.xml)
				.toJSObject(true, false);
	}
	return xd;
};