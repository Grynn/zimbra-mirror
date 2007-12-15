/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2005, 2006, 2007 Zimbra, Inc.
 * 
 * The contents of this file are subject to the Yahoo! Public License
 * Version 1.0 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * 
 * ***** END LICENSE BLOCK *****
 */


// Don't directly instantiate AjxXmlDoc, use one of the create factory methods instead
AjxXmlDoc = function() {
	if (!AjxXmlDoc._inited)
		AjxXmlDoc._init();
}

AjxXmlDoc.prototype.toString =
function() {
	return "AjxXmlDoc";
}

//
// Constants
//

/**
 * <strong>Note:</strong>
 * Anybody that uses these regular expressions MUST reset the lastIndex
 * property to zero or else the results are not guaranteed to be correct.
 * <p>
 * You should use {@link AjxXmlDoc.replaceInvalidChars} instead.
 */
AjxXmlDoc.INVALID_CHARS_RE = /[\u0000-\u0008\u000B-\u000C\u000E-\u001F\uD800-\uDFFF\uFFFE-\uFFFF]/g;
AjxXmlDoc.REC_AVOID_CHARS_RE = /[\u007F-\u0084\u0086-\u009F\uFDD0-\uFDDF]/g;

//
// Data
//

AjxXmlDoc._inited = false;
AjxXmlDoc._msxmlVers = null;

AjxXmlDoc.create =
function() {
	var xmlDoc = new AjxXmlDoc();
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

AjxXmlDoc.replaceInvalidChars = function(s) {
	AjxXmlDoc.INVALID_CHARS_RE.lastIndex = 0;
	return s.replace(AjxXmlDoc.INVALID_CHARS_RE, "?");
};

AjxXmlDoc.getXml =
function(node) {
	var ser = new XMLSerializer();
	return AjxXmlDoc.replaceInvalidChars(ser.serializeToString(node));
}

AjxXmlDoc.prototype.getDoc =
function() {
	return this._doc;
}

AjxXmlDoc.prototype.loadFromString =
function(str) {
	var doc = this._doc;
	doc.loadXML(str);
	if (AjxEnv.isIE) {
		if (doc.parseError.errorCode != 0)
			throw new AjxException(doc.parseError.reason, AjxException.INVALID_PARAM, "AjxXmlDoc.loadFromString");
	}
}

AjxXmlDoc.prototype.loadFromUrl =
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
 * var obj = AjxXmlDoc.createFromXml(XML).toJSObject();
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
 * var obj = AjxXmlDoc.createFromXml(XML).toJSObject();
 * for (var i = 0; i < obj.item.length; ++i) {
 *   alert(obj.item[i].name + " / " + obj.item[i].email);
 * }
 *
 * Note that if there's only one <item> tag, then obj.item will be an object
 * rather than an array.  And if there is no <item> tag, then obj.item will be
 * undefined.  These are cases that the calling application must take care of.
 */
AjxXmlDoc.prototype.toJSObject = 
function(dropns, lowercase, withAttrs) {
	_node = function() { this.__msh_content = ''; };
	_node.prototype.toString = function() { return this.__msh_content; };
	rec = function(i, o) {
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

AjxXmlDoc.prototype.getElementsByTagNameNS = 
function(ns, tag) {
	var doc = this.getDoc();
	return AjxEnv.isIE
		? doc.getElementsByTagName(ns + ":" + tag)
		: doc.getElementsByTagNameNS(ns, tag);
};

AjxXmlDoc.prototype.getFirstElementByTagNameNS = 
function(ns, tag) {
	return this.getElementsByTagNameNS(ns, tag)[0];
};

AjxXmlDoc.prototype.getElementsByTagName = 
function(tag) {
	var doc = this.getDoc();
	return doc.getElementsByTagName(tag);
};

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
		if (!AjxXmlDoc._msxmlVers) {
			throw new AjxException("MSXML not installed", AjxException.INTERNAL_ERROR, "AjxXmlDoc._init");
		}
	} else if (AjxEnv.isNav || AjxEnv.isOpera || AjxEnv.isSafari) {
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
		
		if (AjxEnv.isNav) {
			_NodeGetXml = function() {
				var ser = new XMLSerializer();
				return ser.serializeToString(this);
			}
			Node.prototype.__defineGetter__("xml", _NodeGetXml);
		}
	}/*else if (AjxEnv.isSafari) {												XXX: Safari3 seems to support DOMParser native :)
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
	}*/

	AjxXmlDoc._inited = true;
};


AjxXmlDoc.prototype.set =
function(name, value, element) {
   var p = this._doc.createElement(name);
      if (value != null) {
         var cdata = this._doc.createTextNode("");
         p.appendChild(cdata);
         cdata.nodeValue = value;
      }
      if (element == null) {
         this.root.appendChild(p);
      } else {
         element.appendChild(p);
      }
   return p;
};

AjxXmlDoc.prototype.getDocXml =
function() {
   if (AjxEnv.isSafari)
      return AjxXmlDoc.getXml(this.getDoc());
   else
      return AjxXmlDoc.replaceInvalidChars(this.getDoc().xml);
};

AjxXmlDoc.createRoot =
function(rootName) {
   var xmldoc = AjxXmlDoc.create();
   var d = xmldoc.getDoc();
   xmldoc.root = d.createElement(rootName);

   d.appendChild(xmldoc.root);
   return xmldoc;
};

AjxXmlDoc.createElement =
function(name, value) {
	
   var xmldoc = AjxXmlDoc.create();
   var d = xmldoc.getDoc();
   xmldoc.root = d.createElement(name);
   if (value != null) {
   		//xmldoc.root.nodeValue = value;
   	 	var cdata = d.createTextNode("");
        xmldoc.root.appendChild(cdata);
        cdata.nodeValue = value;
   }
   
   d.appendChild(xmldoc.root);
   return xmldoc;
   
};


AjxXmlDoc.prototype.appendChild =
function(xmldoc) {
   this.root.appendChild(xmldoc.root);   
};

