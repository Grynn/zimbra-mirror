/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2005, 2006, 2007, 2009, 2010, 2011, 2012 VMware, Inc.
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
 * Default constructor.
 * @class
 * Note: do not directly instantiate AjxSoapDoc. Use one of the <code>create</code> methods instead
 * 
 * @see		AjxSoapDoc.create
 */
AjxSoapDoc = function() {
	this._soapURI = AjxSoapDoc._SOAP_URI;
}

AjxSoapDoc.prototype.isAjxSoapDoc = true;
AjxSoapDoc.prototype.toString = function() { return "AjxSoapDoc"; };

AjxSoapDoc._SOAP_URI = "http://www.w3.org/2003/05/soap-envelope";
// AjxSoapDoc._SOAP_URI = "http://schemas.xmlsoap.org/soap/envelope/";
AjxSoapDoc._XMLNS_URI = "http://www.w3.org/2000/xmlns";

/**
 * Creates a SOAP document.
 * 
 * @param	{string}	method		the soap method
 * @param	{string}	namespace	the method namespace
 * @param	{string}	[namespaceId]	the namespace id
 * @param	{string}	[soapURI]	the SOAP uri
 * @return	{AjxSoapDoc}		the document
 */
AjxSoapDoc.create =
function(method, namespace, namespaceId, soapURI) {
	var sd = new AjxSoapDoc();
	sd._xmlDoc = AjxXmlDoc.create();
	var d = sd._xmlDoc.getDoc();

	if (!soapURI)
		soapURI = AjxSoapDoc._SOAP_URI;
	sd._soapURI = soapURI;

	var useNS = d.createElementNS && !AjxEnv.isSafari;
	var envEl = useNS ?  d.createElementNS(soapURI, "soap:Envelope") : d.createElement("soap:Envelope");
	if (!useNS) envEl.setAttribute("xmlns:soap", soapURI);

	d.appendChild(envEl);

	var bodyEl = useNS ? d.createElementNS(soapURI, "soap:Body") : d.createElement("soap:Body");
	envEl.appendChild(bodyEl);

	sd._methodEl = namespace && useNS ?  d.createElementNS(namespace, method) : d.createElement(method);
	if (namespace != null && !useNS) {
		if (namespaceId == null)
			sd._methodEl.setAttribute("xmlns", namespace);
		else
			sd._methodEl.setAttribute("xmlns:" + namespaceId, namespace);
	}
	bodyEl.appendChild(sd._methodEl);
	return sd;
};

/**
 * Creates from a DOM object.
 * 
 * @param	{Object}	doc		the DOM object
 * @return	{AjxSoapDoc}		the document
 */
AjxSoapDoc.createFromDom =
function(doc) {
	var sd = new AjxSoapDoc();
	sd._xmlDoc = AjxXmlDoc.createFromDom(doc);
	sd._methodEl = sd._check(sd._xmlDoc);
	return sd;
};

/**
 * Creates from an XML object.
 * 
 * @param	{Object}	xml		the XML object
 * @return	{AjxSoapDoc}		the document
 */
AjxSoapDoc.createFromXml =
function(xml) {
	var sd = new AjxSoapDoc();
	sd._xmlDoc = AjxXmlDoc.createFromXml(xml);
	sd._methodEl = sd._check(sd._xmlDoc);
	return sd;
};

AjxSoapDoc.element2FaultObj =
function(el) {
	// If the element is not a SOAP fault, then return null
	var faultEl = el.firstChild;
	// Safari is bad at handling namespaces
	if (!AjxEnv.isSafari) {
		if (faultEl != null && faultEl.namespaceURI != AjxSoapDoc._SOAP_URI || faultEl.nodeName != (el.prefix + ":Fault"))
		return null;
	} else {
		if (faultEl != null && faultEl.nodeName != (el.prefix + ":Fault"))
			return null;
	}
	return new AjxSoapFault(faultEl);
};

AjxSoapDoc.prototype.setMethodAttribute =
function(name, value) {
	this._methodEl.setAttribute(name, value);
};

/**
 * Creates arguments to pass within the envelope.  "value" can be a JS object
 * or a scalar (string, number, etc.).
 * <p>
 * When "value" is a JS object, set() will call itself recursively in order to
 * create a complex data structure.  Don't pass a "way-too-complicated" object
 * ("value" should only contain references to simple JS objects, or better put,
 * hashes--don't include a reference to the "window" object as it will kill
 * your browser).
 * <p>
 * Example:
 *
 * <pre>
 *    soapDoc.set("user_auth", {
 *       user_name : "foo",
 *       password  : "bar"
 *    });
 * </pre>
 * 
 * will create an XML like this under the method tag:
 *
 * <pre>
 *    &lt;user_auth>
 *      &lt;user_name>foo&lt;/user_name>
 *      &lt;password>bar&lt;/password>
 *    &lt;/user_auth>
 * </pre>
 * 
 * Of course, nesting other hashes is allowed and will work as expected.
 * <p>
 * NOTE: you can pass null for "name", in which case "value" is expected to be
 * an object whose properties will be created directly under the method el.
 * 
 * @param	{string}	name	the name
 * @param	{hash}	value		the attribute name/value pairs
 * @param	{string}	[parent]	the parent element to append to
 * @param	{string}	[namespace]	the namespace
 * @return	{Element}	the node element
 */
AjxSoapDoc.prototype.set =
function(name, value, parent, namespace) {
	var	doc = this.getDoc();

	var useNS = doc.createElementNS && !AjxEnv.isSafari;

	var	p = name
		? (namespace && useNS ? doc.createElementNS(namespace, name) : doc.createElement(name))
		: doc.createDocumentFragment();

    if ((namespace !== undefined) && (namespace !== null) && !useNS) {
        p.setAttribute("xmlns", namespace);
    }

	if (value != null) {
		if (typeof value == "object") {
			for (var i in value) {
                                var val = value[i];
                                if (i.charAt(0) == "!") {
                                        // attribute
                                        p.setAttribute(i.substr(1), val);
                                } else if (val instanceof Array) {
                                        // add multiple elements
                                        for (var j = 0; j < val.length; ++j)
                                                this.set(i, val[j], p);
                                } else {
				        this.set(i, val, p);
                                }
			}
		} else {
			p.appendChild(doc.createTextNode(value));
		}
	}
	if (!parent)
		parent = this._methodEl;
	return parent.appendChild(p);
};

/**
 * Gets the method.
 * 
 * @return	{string}	the method
 */
AjxSoapDoc.prototype.getMethod =
function() {
	return this._methodEl;
};

/**
 * Creates a header element.
 * 
 * @return	{Element}	the header element
 */
AjxSoapDoc.prototype.createHeaderElement =
function() {
	var d = this._xmlDoc.getDoc();
	var envEl = d.firstChild;
	var header = this.getHeader();
	if (header != null) {
		throw new AjxSoapException("SOAP header already exists", AjxSoapException.ELEMENT_EXISTS, "AjxSoapDoc.prototype.createHeaderElement");
	}
	var useNS = d.createElementNS && !AjxEnv.isSafari;
	header = useNS ? d.createElementNS(this._soapURI, "soap:Header") : d.createElement("soap:Header")
	envEl.insertBefore(header, envEl.firstChild);
	return header;
};

/**
 * Gets the header.
 * 
 * @return	{Element}	the header or <code>null</code> if not created
 */
AjxSoapDoc.prototype.getHeader =
function() {
	// fall back to getElementsByTagName in IE 8 and earlier
	var d = this._xmlDoc.getDoc();
	var nodeList = !d.getElementsByTagNameNS
		? (d.getElementsByTagName(d.firstChild.prefix + ":Header"))
		: (d.getElementsByTagNameNS(this._soapURI, "Header"));

	return nodeList ? nodeList[0] : null;
};

/**
 * Gets the body.
 * 
 * @return	{Element}	the body element
 */
AjxSoapDoc.prototype.getBody =
function() {
	// fall back to getElementsByTagName in IE 8 and earlier
	var d = this._xmlDoc.getDoc();
	var nodeList = !d.getElementsByTagNameNS
		? (d.getElementsByTagName(d.firstChild.prefix + ":Body"))
		: (d.getElementsByTagNameNS(this._soapURI, "Body"));

	return nodeList ? nodeList[0] : null;
};

AjxSoapDoc.prototype.getByTagName =
function(type) {
	if (type.indexOf(":") == -1)
		type = "soap:" + type;

	var a = this.getDoc().getElementsByTagName(type);

	if (a.length == 1)		return a[0];
	else if (a.length > 0)	return a;
	else					return null;
};

// gimme a header, no exceptions.
AjxSoapDoc.prototype.ensureHeader =
function() {
	return (this.getHeader() || this.createHeaderElement());
};

/**
 * Gets the document.
 * 
 * @return	{Document}	the document
 */
AjxSoapDoc.prototype.getDoc =
function() {
	return this._xmlDoc.getDoc();
};

/**
 * Adopts a node from another document to this document.
 * 
 * @param	{Element}	node		the node
 * @private
 */
AjxSoapDoc.prototype.adoptNode =
function(node) {
	// Older firefoxes throw not implemented error when you call adoptNode.
	if (AjxEnv.isFirefox3up || !AjxEnv.isFirefox) {
		try {
			var doc = this.getDoc();
			if (doc.adoptNode) {
				return doc.adoptNode(node, true);
			}
		} catch (ex) {
			// handle below by returning the input node.
		}
	}
	// call removeChild since Safari complains if you try to add an already
	// parented node to another document.
	return node.parentNode.removeChild(node);
};

/**
 * Gets the XML.
 * 
 * @return	{string}	the XML
 */
AjxSoapDoc.prototype.getXml =
function() {
	return AjxEnv.isSafari || AjxEnv.isOpera || AjxEnv.isIE9up
		? (AjxXmlDoc.getXml(this._xmlDoc.getDoc()))
		: AjxXmlDoc.replaceInvalidChars(this._xmlDoc.getDoc().xml);
};

// Very simple checking of soap doc. Should be made more comprehensive
AjxSoapDoc.prototype._check =
function(xmlDoc) {
	var doc = xmlDoc.getDoc();
	if (doc.childNodes.length != 1)
		throw new AjxSoapException("Invalid SOAP PDU", AjxSoapException.INVALID_PDU, "AjxSoapDoc.createFromXml:1");

	// Check to make sure we have a soap envelope
	var el = doc.firstChild;

	// Safari is bad at handling namespaces
	if (!AjxEnv.isSafari) {
		if (el.namespaceURI != AjxSoapDoc._SOAP_URI ||
			el.nodeName != (el.prefix + ":Envelope") ||
			(el.childNodes.length < 1 || el.childNodes.length > 2))
		{
			DBG.println("<font color=red>XML PARSE ERROR on RESPONSE:</font>");
			DBG.printRaw(doc.xml);
			throw new AjxSoapException("Invalid SOAP PDU", AjxSoapException.INVALID_PDU, "AjxSoapDoc.createFromXml:2");
		}
	} else {
		if (el.nodeName != (el.prefix + ":Envelope"))
			throw new AjxSoapException("Invalid SOAP PDU", AjxSoapException.INVALID_PDU, "AjxSoapDoc.createFromXml:2");
	}
};
