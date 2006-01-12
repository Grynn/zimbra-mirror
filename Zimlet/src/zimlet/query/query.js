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

function query() {
}

query.prototype = new ZmZimletBase;
query.prototype.constructor = query;

query.prototype.init =
function() {
	var stylesheet, processor;
	stylesheet = this.getResource("yahoo.xsl");
	processor = AjxXslt.createFromUrl(stylesheet);
	this._yahooXslt = processor;
	stylesheet = this.getResource("google.xsl");
	processor = AjxXslt.createFromUrl(stylesheet);
	this._googleXslt = processor;
	stylesheet = this.getResource("amazon.xsl");
	processor = AjxXslt.createFromUrl(stylesheet);
	this._amazonXslt = processor;
	stylesheet = this.getResource("mediawiki.xsl");
	processor = AjxXslt.createFromUrl(stylesheet);
	this._wikiXslt = processor;
};

query.prototype.queryYahoo =
function(q, canvas) {
	var request = new AjxRpcRequest("query");
	var q_url = this.getConfig("yhooUrl")+q;
	var url = ZmZimletBase.PROXY + AjxStringUtil.urlEncode(q_url);
	request.invoke(null, url, null, new AjxCallback(this, query._callback, [ canvas, this._yahooXslt ]), true);
};

query.prototype.queryGoogle =
function(q, canvas) {
	var i = 0,reqmsg = [];
	reqmsg[i++] = '<?xml version="1.0" encoding="UTF-8"?>';
	reqmsg[i++] = '<SOAP-ENV:Envelope xmlns:SOAP-ENV="http://schemas.xmlsoap.org/soap/envelope/" xmlns:xsi="http://www.w3.org/1999/XMLSchema-instance" xmlns:xsd="http://www.w3.org/1999/XMLSchema">';
	reqmsg[i++] = '<SOAP-ENV:Body>';
	reqmsg[i++] = '<ns1:doGoogleSearch xmlns:ns1="urn:GoogleSearch" SOAP-ENV:encodingStyle="http://schemas.xmlsoap.org/soap/encoding/">';
	reqmsg[i++] = '<key xsi:type="xsd:string">';
	reqmsg[i++] = this.getConfig("googleKey");
	reqmsg[i++] = '</key><q xsi:type="xsd:string">';
	reqmsg[i++] = q;
	reqmsg[i++] = '</q>';
	reqmsg[i++] = '<start xsi:type="xsd:int">0</start>';
	reqmsg[i++] = '<maxResults xsi:type="xsd:int">5</maxResults>';
	reqmsg[i++] = '<filter xsi:type="xsd:boolean">true</filter>';
	reqmsg[i++] = '<restrict xsi:type="xsd:string"/>';
	reqmsg[i++] = '<safeSearch xsi:type="xsd:boolean">false</safeSearch>';
	reqmsg[i++] = '<lr xsi:type="xsd:string"/>';
	reqmsg[i++] = '<ie xsi:type="xsd:string">UTF-8</ie>';
	reqmsg[i++] = '<oe xsi:type="xsd:string">UTF-8</oe>';
	reqmsg[i++] = '</ns1:doGoogleSearch></SOAP-ENV:Body></SOAP-ENV:Envelope>';

	var request = new AjxRpcRequest("query");
	var url = ZmZimletBase.PROXY + AjxStringUtil.urlEncode(this.getConfig("googUrl"));
	request.invoke(reqmsg.join(""), url, {"Content-Type": "text/xml"}, new AjxCallback(this, query._callback, [ canvas, this._googleXslt ]), false);
};

query.prototype.queryAmazon =
function(q, canvas) {
	var request = new AjxRpcRequest("query");
	var q_url = this.getConfig("amznUrl");
	var args = { Service: "AWSECommerceService", 
				 Operation: "ItemSearch", 
				 SearchIndex: "Music", 
//				 SearchIndex: "Books", 
				 ResponseGroup: "Request,Small", 
				 Version: "2004-11-10" };
	args.SubscriptionId = this.getConfig("amazonKey");
	args.Keywords = q;
	var sep = "?";
	for (var arg in args) {
		q_url = q_url + sep + arg + "=" + args[arg];
		sep = "&";
	}
	var url = ZmZimletBase.PROXY + AjxStringUtil.urlEncode(q_url);
	request.invoke(null, url, null, new AjxCallback(this, query._callback, [ canvas, this._amazonXslt ]), true);
};

query.prototype.queryWiki =
function(url, q, canvas) {
	var request = new AjxRpcRequest("query");
	var q_url = url+q;
	var url = ZmZimletBase.PROXY + AjxStringUtil.urlEncode(q_url);
	request.invoke(null, url, null, new AjxCallback(this, query._callback, [ canvas, this._wikiXslt ]), true);
};


query.prototype.toolTipPoppedUp =
function(spanElement, obj, context, canvas) {
	canvas.innerHTML = "<b>Query: </b>"+context;
	this.queryWiki(this.getConfig("wiktionaryUrl"), context, canvas);
};

query._callback =
function(canvas, processor, result) {
	var html, resp = result.xml;
	if (resp == undefined) {
		var doc = AjxXmlDoc.createFromXml(result.text);
		resp = doc.getDoc();
	}
	var html = processor.transformToString(resp);
	canvas.innerHTML = html;
};

