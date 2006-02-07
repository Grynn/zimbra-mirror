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


function _initGoogle() {
	var goog = new Object();
	goog.label = "Google";
	goog.id = "googlewebservice";
	goog.icon = "Google-panelIcon";
	goog.xsl = "google/google.xsl";
	goog.getRequest = 
		function (ctxt, q) {
			var i = 0,reqmsg = [];
			reqmsg[i++] = '<?xml version="1.0" encoding="UTF-8"?>';
			reqmsg[i++] = '<SOAP-ENV:Envelope xmlns:SOAP-ENV="http://schemas.xmlsoap.org/soap/envelope/" xmlns:xsi="http://www.w3.org/1999/XMLSchema-instance" xmlns:xsd="http://www.w3.org/1999/XMLSchema">';
			reqmsg[i++] = '<SOAP-ENV:Body>';
			reqmsg[i++] = '<ns1:doGoogleSearch xmlns:ns1="urn:GoogleSearch" SOAP-ENV:encodingStyle="http://schemas.xmlsoap.org/soap/encoding/">';
			reqmsg[i++] = '<key xsi:type="xsd:string">';
			reqmsg[i++] = ctxt.getConfig("googleKey");
			reqmsg[i++] = '</key><q xsi:type="xsd:string">';
			reqmsg[i++] = q;
			reqmsg[i++] = '</q>';
			reqmsg[i++] = '<start xsi:type="xsd:int">0</start>';
			reqmsg[i++] = '<maxResults xsi:type="xsd:int">';
			reqmsg[i++] = 10; // ctxt.getConfig("numResults");  // 10 is the limit for google beta api.
			reqmsg[i++] = '</maxResults>';
			reqmsg[i++] = '<filter xsi:type="xsd:boolean">true</filter>';
			reqmsg[i++] = '<restrict xsi:type="xsd:string"/>';
			reqmsg[i++] = '<safeSearch xsi:type="xsd:boolean">false</safeSearch>';
			reqmsg[i++] = '<lr xsi:type="xsd:string"/>';
			reqmsg[i++] = '<ie xsi:type="xsd:string">UTF-8</ie>';
			reqmsg[i++] = '<oe xsi:type="xsd:string">UTF-8</oe>';
			reqmsg[i++] = '</ns1:doGoogleSearch></SOAP-ENV:Body></SOAP-ENV:Envelope>';

			return {"url":ctxt.getConfig("googUrl"), "req":reqmsg.join("")}
		};
		
	com_zimbra_xslt.registerService(goog);
};

_initGoogle();
