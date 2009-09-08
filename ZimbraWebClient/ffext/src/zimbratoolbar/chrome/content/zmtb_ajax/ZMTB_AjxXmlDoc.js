/*
 * Copyright (C) 2006, The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


// Don't directly instantiate ZMTB_AjxXmlDoc, use one of the create factory methods instead
function ZMTB_AjxXmlDoc() {
	if (!ZMTB_AjxXmlDoc._inited)
		ZMTB_AjxXmlDoc._init();
}

ZMTB_AjxXmlDoc.prototype.toString =
function() {
	return "ZMTB_AjxXmlDoc";
}

ZMTB_AjxXmlDoc._inited = false;
ZMTB_AjxXmlDoc._msxmlVers = null;

ZMTB_AjxXmlDoc.create =
function() {
	var xmlDoc = new ZMTB_AjxXmlDoc();
	var newDoc = null;
	if (ZMTB_AjxEnv.isIE) {
		newDoc = new ActiveXObject(ZMTB_AjxXmlDoc._msxmlVers);
		newDoc.async = true; // Force Async loading
		if (ZMTB_AjxXmlDoc._msxmlVers == "MSXML2.DOMDocument.4.0") {
			newDoc.setProperty("SelectionLanguage", "XPath");
			newDoc.setProperty("SelectionNamespaces", "xmlns:zimbra='urn:zimbra' xmlns:mail='urn:zimbraMail' xmlns:account='urn:zimbraAccount'");
		}
	} else if (document.implementation && document.implementation.createDocument) {
		newDoc = document.implementation.createDocument("", "", null);
	} else {
		throw new ZMTB_AjxException("Unable to create new Doc", ZMTB_AjxException.INTERNAL_ERROR, "ZMTB_AjxXmlDoc.create");
	}
	xmlDoc._doc = newDoc;
	return xmlDoc;
}

ZMTB_AjxXmlDoc.createFromDom =
function(doc) {
	var xmlDoc = new ZMTB_AjxXmlDoc();
	xmlDoc._doc = doc;
	return xmlDoc;
}

ZMTB_AjxXmlDoc.createFromXml =
function(xml) {
	var xmlDoc = ZMTB_AjxXmlDoc.create();
	xmlDoc.loadFromString(xml);
	return xmlDoc;
}

ZMTB_AjxXmlDoc.getXml =
function(node) {
	var ser = new XMLSerializer();
	return ser.serializeToString(node);
}

ZMTB_AjxXmlDoc.prototype.getDoc =
function() {
	return this._doc;
}

ZMTB_AjxXmlDoc.prototype.loadFromString =
function(str) {
	var doc = this._doc;
	doc.loadXML(str);
	if (ZMTB_AjxEnv.isIE) {
		if (doc.parseError.errorCode != 0)
			throw new ZMTB_AjxException(doc.parseError.reason, ZMTB_AjxException.INVALID_PARAM, "ZMTB_AjxXmlDoc.loadFromString");
	}
}

ZMTB_AjxXmlDoc.prototype.loadFromUrl =
function(url) {
	this._doc.load(url);
}

/**
 * This function tries to create a JavaScript representation of the DOM.  Why,
 * because it's so much fun to work with JS objets rather than do DOM lookups
 * using getElementsByTagName 'n stuff.
 *
 * Rules:
 *
 *   1. The top-level tag gets lost; only it's content is seen important.
 *   2. Each node will be represented as a JS object.  It's textual content
 *      will be saved in node.__msh_content (returned by toString()).
 *   3. Attributes get discarded; this might not be good in general but it's OK
 *      for the application I have in mind now.  IAE, I'll be able to fix this if
 *      anyone requires--mail mihai@zimbra.com.
 *   4. Each subnode will map to a property with its tagName in the parent
 *      node.  So, parent[subnode.tagName] == subnode.
 *   5. If multiple nodes with the same tagName have the same parent node, then
 *      parent[tagName] will be an array containing the objects, rather than a
 *      single object.
 *
 * So what this function allows us to do is for instance this:
 *
 * XML doc:
 *
 * <error>
 *   <code>404</code>
 *   <name>Not Found</name>
 *   <description>Page wasn't found on this server.</description>
 * </error>
 *
 * var obj = ZMTB_AjxXmlDoc.createFromXml(XML).toJSObject();
 * alert(obj.code + " " + obj.name + " " + obj.description);
 *
 * Here's an array example:
 *
 * <return>
 *   <item>
 *     <name>John Doe</name>
 *     <email>foo@bar.com</email>
 *   </item>
 *   <item>
 *     <name>Johnny Bravo</name>
 *     <email>bravo@cartoonnetwork.com</email>
 *   </item>
 * </return>
 *
 * var obj = ZMTB_AjxXmlDoc.createFromXml(XML).toJSObject();
 * for (var i = 0; i < obj.item.length; ++i) {
 *   alert(obj.item[i].name + " / " + obj.item[i].email);
 * }
 *
 * Note that if there's only one <item> tag, then obj.item will be an object
 * rather than an array.  And if there is no <item> tag, then obj.item will be
 * undefined.  These are cases that the calling application must take care of.
 */
ZMTB_AjxXmlDoc.prototype.toJSObject = 
function(dropns, lowercase, withAttrs) {
	function _node() { this.__msh_content = ''; };
	_node.prototype.toString = function() { return this.__msh_content; };
	function rec(i, o) {
		var tags = {}, t, n;
		for (i = i.firstChild; i; i = i.nextSibling) {
			if (i.nodeType == 1) {
				t = i.tagName;
				if (dropns)      t = t.replace(/^.*?:/, "");
				if (lowercase)   t = t.toLowerCase();
				n = new _node();
				if (tags[t]) {
					if (tags[t] == 1) {
						o[t] = [ o[t] ];
						tags[t] = 2;
					}
					o[t].push(n);
				} else {
					o[t] = n;
					tags[t] = 1;
				}
				//do attributes
				if(withAttrs) {
					if(i.attributes && i.attributes.length) {
						for(var ix = 0;ix<i.attributes.length;ix++) {
							attr = i.attributes[ix];
							n[attr.name] = AjxUtil.isNumeric(attr.value) ? attr.value : String(attr.value);
						}
					}
				}
				rec(i, n);
			} else if (i.nodeType == 3)
				o.__msh_content += i.nodeValue;
		}
	};
	var o = new _node();
	rec(this._doc.documentElement, o);
	return o;
};

ZMTB_AjxXmlDoc.prototype.getElementsByTagNameNS = 
function(ns, tag) {
	var doc = this.getDoc();
	return ZMTB_AjxEnv.isIE
		? doc.getElementsByTagName(ns + ":" + tag)
		: doc.getElementsByTagNameNS(ns, tag);
};

ZMTB_AjxXmlDoc.prototype.getFirstElementByTagNameNS = 
function(ns, tag) {
	return this.getElementsByTagNameNS(ns, tag)[0];
};

ZMTB_AjxXmlDoc._init =
function() {
	if (ZMTB_AjxEnv.isIE) {
		var msxmlVers = ["MSXML4.DOMDocument", "MSXML3.DOMDocument", "MSXML2.DOMDocument.4.0",
				 "MSXML2.DOMDocument.3.0", "MSXML2.DOMDocument", "MSXML.DOMDocument",
				 "Microsoft.XmlDom"];
		for (var i = 0; i < msxmlVers.length; i++) {
			try {
				new ActiveXObject(msxmlVers[i]);
				ZMTB_AjxXmlDoc._msxmlVers = msxmlVers[i];
				break;
			} catch (ex) {
			}
		}
		if (!ZMTB_AjxXmlDoc._msxmlVers) {
			throw new ZMTB_AjxException("MSXML not installed", ZMTB_AjxException.INTERNAL_ERROR, "ZMTB_AjxXmlDoc._init");
		}
	} else if (ZMTB_AjxEnv.isNav || ZMTB_AjxEnv.isOpera) {
		// add loadXML to Document's API
		Document.prototype.loadXML = function(str) {
			var domParser = new DOMParser();
			var domObj = domParser.parseFromString(str, "text/xml");
			// remove old child nodes since we recycle DOMParser and append new
			while (this.hasChildNodes()) {
				this.removeChild(this.lastChild);
			}
			var len = domObj.childNodes.length;
			for (var i = 0; i < len; i++) {
				var importedNode = this.importNode(domObj.childNodes[i], true);
				this.appendChild(importedNode);
			}
		}
		
		if (ZMTB_AjxEnv.isNav) {
			_NodeGetXml = function() {
				var ser = new XMLSerializer();
				return ser.serializeToString(this);
			}
			Node.prototype.__defineGetter__("xml", _NodeGetXml);
		}
	} else if (ZMTB_AjxEnv.isSafari) {
		// add loadXML to Document's API
		document.__proto__.loadXML = function(str) {
			var domParser = new DOMParser();
			var domObj = domParser.parseFromString(str, "text/xml");
			// remove old child nodes since we recycle DOMParser and append new
			while (this.hasChildNodes()) {
				this.removeChild(this.lastChild);
			}
			var len = domObj.childNodes.length;
			for (var i = 0; i < len; i++) {
				var importedNode = this.importNode(domObj.childNodes[i], true);
				this.appendChild(importedNode);
			}
		}
	}

	ZMTB_AjxXmlDoc._inited = true;
};
