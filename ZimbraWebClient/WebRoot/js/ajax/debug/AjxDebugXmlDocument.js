/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2005, 2006, 2007, 2009, 2010 Zimbra, Inc.
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
 * XmlDocument factory
 * 
 * @private
 */
AjxDebugXmlDocument = function() {
}

// used to find the Automation server name
getDomDocumentPrefix = function() {
	if (getDomDocumentPrefix.prefix)
		return getDomDocumentPrefix.prefix;
	
	var prefixes = ["MSXML2", "Microsoft", "MSXML", "MSXML3"];
	var o;
	var len = prefixes.length;
	for (var i = 0; i < len; i++) {
		try {
			// try to create the objects
			o = new ActiveXObject(prefixes[i] + ".DomDocument");
			return getDomDocumentPrefix.prefix = prefixes[i];
		}
		catch (ex) {};
	}
	
	throw new Error("Could not find an installed XML parser");
}

AjxDebugXmlDocument.prototype.create = 
function () {
	try {
		// DOM2
		if (document.implementation && document.implementation.createDocument) {
			var doc = document.implementation.createDocument("", "", null);
			
			// some versions of Moz do not support the readyState property
			// and the onreadystate event so we patch it!
			if (doc.readyState == null) {
				doc.readyState = 1;
				doc.addEventListener("load", function () {
					doc.readyState = 4;
					if (typeof doc.onreadystatechange == "function")
						doc.onreadystatechange();
				}, false);
			}
			
			return doc;
		}
		if (window.ActiveXObject)
			return new ActiveXObject(getDomDocumentPrefix() + ".DomDocument");
	}
	catch (ex) {}
	throw new Error("Your browser does not support XmlDocument objects");
}

// Create the loadXML method and xml getter for Mozilla
if (window.DOMParser &&
	window.XMLSerializer &&
	window.Node && Node.prototype && Node.prototype.__defineGetter__)
{
	if (AjxEnv.isSafari) {
		Document.prototype.loadXML = function(s) {
			// parse the string to a new doc
			var doc2 = (new DOMParser()).parseFromString(s, "text/xml");

			// remove all initial children
			while (this.hasChildNodes()) {
				this.removeChild(this.lastChild);
			}

			// insert and import nodes
			var len = doc2.childNodes.length;
			for (var i = 0; i < len; i++) {
				this.appendChild(this.importNode(doc2.childNodes[i], true));
			}
		};

		// This serializes the DOM tree to an XML String
		// Usage: var sXml = oNode.xml
		Document.prototype.__defineGetter__("xml", function () {
			return (new XMLSerializer()).serializeToString(this);
		});
	}
	//
	// XMLDocument did not extend Document interface in some versions of Mozilla
	// so explicitly define it here.
	//
	else {
		AjxDebugXmlDocument.prototype.loadXML = function(s) {
			// parse the string to a new doc
			var doc2 = (new DOMParser()).parseFromString(s, "text/xml");
		
			// remove all initial children
			while (this.hasChildNodes())
				this.removeChild(this.lastChild);

			// insert and import nodes
			var len = doc2.childNodes.length;
			for (var i = 0; i < len; i++)
				this.appendChild(this.importNode(doc2.childNodes[i], true));
		};

		// This serializes the DOM tree to an XML String
		// Usage: var sXml = oNode.xml
		AjxDebugXmlDocument.prototype.__defineGetter__("xml", function () {
			return (new XMLSerializer()).serializeToString(this);
		});
	}
};
