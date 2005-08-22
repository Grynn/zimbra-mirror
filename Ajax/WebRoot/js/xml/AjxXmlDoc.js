/*
***** BEGIN LICENSE BLOCK *****
Version: ZAPL 1.1

The contents of this file are subject to the Zimbra AJAX Public License Version 1.1 ("License");
You may not use this file except in compliance with the License. You may obtain a copy of the
License at http://www.zimbra.com/license

Software distributed under the License is distributed on an "AS IS" basis, WITHOUT WARRANTY OF
ANY KIND, either express or implied. See the License for the specific language governing rights
and limitations under the License.

The Original Code is: Zimbra AJAX Toolkit.

The Initial Developer of the Original Code is Zimbra, Inc.
Portions created by Zimbra are Copyright (C) 2005 Zimbra, Inc.
All Rights Reserved.
Contributor(s): ______________________________________.

***** END LICENSE BLOCK *****
*/

// Don't directly instantiate AjxXmlDoc, use one of the create factory methods instead
function AjxXmlDoc(init) {
	if (arguments.length == 0) return;
	if (!AjxXmlDoc._inited)
		AjxXmlDoc._init();
}

AjxXmlDoc.prototype.toString = 
function() {
	return "AjxXmlDoc";
}

AjxXmlDoc._inited = false;
AjxXmlDoc._msxmlVers = null;

AjxXmlDoc.create =
function() {
	var xmlDoc = new AjxXmlDoc(true);
	var newDoc = null;
	if (AjxEnv.isIE) {
		newDoc = new ActiveXObject(AjxXmlDoc._msxmlVers);
		newDoc.async = true; // Force Async loading
		if (AjxXmlDoc._msxmlVers == "MSXML2.DOMDocument.4.0") {
			newDoc.setProperty("SelectionLanguage", "XPath");
			newDoc.setProperty("SelectionNamespaces", "xmlns:zimbra='urn:zimbra' xmlns:mail='urn:zimbraMail' xmlns:account='urn:zimbraAccount'");
		}
	} else if (document.implementation && document.implementation.createDocument) {
		newDoc = document.implementation.createDocument("", "", null);
	} else {
		throw new AjxException("Unable to create new Doc", AjxException.INTERNAL_ERROR, "AjxXmlDoc.create");
	}
	xmlDoc._doc = newDoc;
	return xmlDoc;
}

AjxXmlDoc.createFromDom =
function(doc) {
	var xmlDoc = new AjxXmlDoc();
	xmlDoc._doc = doc;
	return xmlDoc;
}

AjxXmlDoc.createFromXml =
function(xml) {
	var xmlDoc = AjxXmlDoc.create();
	xmlDoc.loadFromString(xml);
	return xmlDoc;
}

AjxXmlDoc.getXml =
function(node) {
	var ser = new XMLSerializer();
	return ser.serializeToString(node);
}

AjxXmlDoc.prototype.getDoc =
function() {
	return this._doc;
}

AjxXmlDoc.prototype.loadFromString =
function(str) {
	this._doc.loadXML(str);

}

AjxXmlDoc.prototype.loadFromUrl =
function(url) {
	this._doc.load(url);
}

AjxXmlDoc._init =
function() {
	if (AjxEnv.isIE) {
		var msxmlVers = ["MSXML4.DOMDocument", "MSXML3.DOMDocument", "MSXML2.DOMDocument.4.0",
										 "MSXML2.DOMDocument.3.0", "MSXML2.DOMDocument", "MSXML.DOMDocument", 
										 "Microsoft.XmlDom"];
		for (var i = 0; i < msxmlVers.length; i++) {
			try {
				new ActiveXObject(msxmlVers[i]);
				AjxXmlDoc._msxmlVers = msxmlVers[i];
				break;
			} catch (ex) {
			}
		}
		if (AjxXmlDoc._msxmlVers == null)
			throw new AjxException("MSXML not installed", AjxException.INTERNAL_ERROR, "AjxXmlDoc._init");
	}	else if (AjxEnv.isNav) {
		Document.prototype.loadXML = function(str) {
			var domParser = new DOMParser();
			var domObj = domParser.parseFromString(str, "text/xml");
			while (this.hasChildNodes())
				this.removeChild(this.lastChild);
			for (var i = 0; i < domObj.childNodes.length; i++) {
					var importedNode = this.importNode(domObj.childNodes[i], true);
					this.appendChild(importedNode);
			}
		}	
		
		_NodeGetXml = function() {
	  		var ser = new XMLSerializer();
	  		return ser.serializeToString(this);
	 	 }	
		Node.prototype.__defineGetter__("xml", _NodeGetXml);
	}
	
	if (AjxEnv.isNav || AjxEnv.isSafari) {
	}
	
	AjxXmlDoc._inited = true;
}

