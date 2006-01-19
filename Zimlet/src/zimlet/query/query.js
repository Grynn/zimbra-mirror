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

query.prototype._translations = {
	yahoo: "yahoo.xsl",
	yahoolocal: "yahoolocal.xsl",
	google: "google.xsl",
	amazonmusic: "amazon.xsl",
	amazonbooks: "amazon.xsl",
	wikipedia: "mediawiki.xsl",
	wiktionary: "mediawiki.xsl"
};

query.prototype.init =
function() {
	var stylesheet, processor;
	this._processors = {};
	for (var key in this._translations) {
		stylesheet = this.getResource(this._translations[key]);
		try {
			processor = AjxXslt.createFromUrl(stylesheet);
		} catch (ex) {
			DBG.println(AjxDebug.DBG1, ex.dump());
		}
		this._processors[key] = processor;
	}
	this._query = "yahoo";
};

query.prototype.queryYahoo =
function(q, canvas) {
	var request = new AjxRpcRequest("queryYahoo");
	var args = {};
	args.appid = this.getConfig("ywsAppId");
	args.results = this.getConfig("numResults");
	args.query = AjxStringUtil.urlEncode(q);

	var q_url;
	if (this._query == "yahoo") {
		q_url = this.getConfig("yhooSearchUrl");
	} else if (this._query == "yahoolocal") {
		q_url = this.getConfig("yhooLocalUrl");
		args.zip = this.getConfig("zipcode");
	}
	var sep = "?";
	for (var arg in args) {
		q_url = q_url + sep + arg + "=" + args[arg];
		sep = "&";
	}

	var url = ZmZimletBase.PROXY + AjxStringUtil.urlEncode(q_url);
	request.invoke(null, url, null, new AjxCallback(this, query._callback, [ canvas, this._query ]), true);
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

	var request = new AjxRpcRequest("queryGoogle");
	var url = ZmZimletBase.PROXY + AjxStringUtil.urlEncode(this.getConfig("googUrl"));
	request.invoke(reqmsg.join(""), url, {"Content-Type": "text/xml"}, new AjxCallback(this, query._callback, [ canvas, "google" ]), false);
};

query.prototype.queryAmazon =
function(q, canvas) {
	var searchIndex;
	if (this._query == "amazonmusic") {
		searchIndex = "Music";
	} else if (this._query == "amazonbooks") {
		searchIndex = "Books";
	}
	var request = new AjxRpcRequest("queryAmazon");
	var q_url = this.getConfig("amznUrl");
	var args = { Service: "AWSECommerceService", 
				 Operation: "ItemSearch", 
				 SearchIndex: searchIndex, 
				 ResponseGroup: "Request,Small", 
				 Version: "2004-11-10" };
	args.SubscriptionId = this.getConfig("amazonKey");
	args.Keywords = AjxStringUtil.urlEncode(q);
	var sep = "?";
	for (var arg in args) {
		q_url = q_url + sep + arg + "=" + args[arg];
		sep = "&";
	}
	var url = ZmZimletBase.PROXY + AjxStringUtil.urlEncode(q_url);
	request.invoke(null, url, null, new AjxCallback(this, query._callback, [ canvas, this._query ]), true);
};

query.prototype.queryWiki =
function(q, canvas) {
	var url;
	if (this._query == "wikipedia") {
		url = this.getConfig("wikipediaUrl");
	} else if (this._query == "wiktionary") {
		url = this.getConfig("wiktionaryUrl");
	}
	var request = new AjxRpcRequest("queryWiki");
	var q_url = url + AjxStringUtil.urlEncode(q);
	var url = ZmZimletBase.PROXY + AjxStringUtil.urlEncode(q_url);
	request.invoke(null, url, null, new AjxCallback(this, query._callback, [ canvas, "wikipedia" ]), true);
};

query.prototype._evListener =
function(ev) {
	this._query = ev.target.innerHTML;
};

query.prototype._buttonListener =
function(ev) {
	var el = document.getElementById(this._subjectId);
	var q = el.value;
	el = document.getElementById(this._canvasId);
	this.makeQuery(q, el);
};

query.prototype.menuItemSelected =
function(contextMenu, menuItemId, spanElement, contentObjText, canvas) {
	this._query = menuItemId.id;
	var view = new DwtComposite(this.getShell());
	var el = view.getHtmlElement();
	var div = document.createElement("div");
	var subjectId = Dwt.getNextId();
	var canvasId = Dwt.getNextId();
	
	div.innerHTML =
		[ "<table><tbody>",
		  "<tr>",
		  "<td align='right'><label for='", subjectId, "'>Search:</td>",
		  "<td>",
		  "<input autocomplete='off' style='width: 21em' type='text' id='", subjectId, "' value=''/>",
		  "</td>",
		  "</tr>",
		  "<td colspan='2'>",
		  "<div id='", canvasId, "'/>",
		  "</td>",
		  "<tr>",
		  "</tr></tbody></table>" ].join("");
	el.appendChild(div);

	var dialog_args = {
		title : menuItemId.label,
		view  : view
	};
	var dlg = this._createDialog(dialog_args);
	dlg.popup();

	el = document.getElementById(subjectId);
	el.select();
	el.focus();

	this._subjectId = subjectId;
	this._canvasId = canvasId;
	
	dlg.setButtonListener(DwtDialog.OK_BUTTON,
		      new AjxListener(this, query.prototype._buttonListener));

	dlg.setButtonListener(DwtDialog.CANCEL_BUTTON,
		      new AjxListener(this, function() {
			      dlg.popdown();
			      dlg.dispose();
		      }));
};

query.prototype.getActionMenu =
function(obj, span, context, isDialog) {
	if (this._menu == null) {
		var i = 0;
		this._menu =  new ZmPopupMenu(this._appCtxt.getShell(), "ActionMenu", isDialog);
		for (var key in this._translations) {
			this._menu.createMenuItem(i, null, key, null, true);
			this._menu.addSelectionListener(i, new AjxListener(this, query.prototype._evListener));
		}
	}
	return this._menu;
};

query.prototype.toolTipPoppedUp =
function(spanElement, obj, context, canvas) {
	canvas.innerHTML = "<b>Query: </b>"+context;
	this.makeQuery(context, canvas);
};

query.prototype.makeQuery =
function(query, canvas) {
	if (this._query == "yahoo" || this._query == "yahoolocal") {
		this.queryYahoo(query, canvas);
	} else if (this._query == "google") {
		this.queryGoogle(query, canvas);
	} else if (this._query == "amazonmusic" || this._query == "amazonbooks") {
		this.queryAmazon(query, canvas);
	} else if (this._query == "wikipedia" || this._query == "wiktionary") {
		this.queryWiki(query, canvas);
	}
};

query.prototype.getSanitizedDocFromHtml =
function(text) {
	text = text ? text.replace(/&nbsp;/g," ").replace(/&reg;/g,"(R)") : "";
	var doc = AjxXmlDoc.createFromXml(text);
	return doc.getDoc();
};

query._callback =
function(canvas, id, result) {
	var html, resp;
	var processor = this._processors[id];
	
	if (!result.success) {
		canvas.innerHTML = "<div><b>Web service returned error.</b></div>"+result.text;
		return;
	}
	
	try {
		if (!result.xml || !result.xml.documentElement) {
			resp = this.getSanitizedDocFromHtml(result.text);
		} else {
			resp = result.xml;
		}
		html = processor.transformToString(resp);
	} catch (ex) {
		DBG.println(AjxDebug.DBG1, ex.dump());
		canvas.innerHTML = "<div><b>Transformation resulted in error.</b></div>";
		return;
	}
	
	//DBG.println(AjxDebug.DBG1, "*********"+AjxStringUtil.htmlEncode(html)+"************");
	if (id == "google") {
		html = html ? html.replace(/&gt;/g,">").replace(/&lt;/g,"<").replace(/&quot;/g, '"').replace(/&apos;/g,"'") : "";
	}
	canvas.innerHTML = html;
};

