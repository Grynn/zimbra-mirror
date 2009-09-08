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


// Don't directly instantiate SoapDoc, use one of the create factory methods instead
function ZMTB_AjxSoapDoc() {
	this._soapURI = ZMTB_AjxSoapDoc._SOAP_URI;
}

ZMTB_AjxSoapDoc.prototype.toString =
function() {
	return "ZMTB_AjxSoapDoc";
};

ZMTB_AjxSoapDoc._SOAP_URI = "http://www.w3.org/2003/05/soap-envelope";
// ZMTB_AjxSoapDoc._SOAP_URI = "http://schemas.xmlsoap.org/soap/envelope/";
ZMTB_AjxSoapDoc._XMLNS_URI = "http://www.w3.org/2000/xmlns";

ZMTB_AjxSoapDoc.create =
function(method, namespace, namespaceId, soapURI) {
	var sd = new ZMTB_AjxSoapDoc();
	sd._xmlDoc = ZMTB_AjxXmlDoc.create();
	var d = sd._xmlDoc.getDoc();

	if (!soapURI)
		soapURI = ZMTB_AjxSoapDoc._SOAP_URI;
	sd._soapURI = soapURI;		
	
	var useNS = d.createElementNS && !ZMTB_AjxEnv.isSafari;
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

ZMTB_AjxSoapDoc.createFromDom =
function(doc) {
	var sd = new ZMTB_AjxSoapDoc();
	sd._xmlDoc = ZMTB_AjxXmlDoc.createFromDom(doc);
	sd._methodEl = sd._check(sd._xmlDoc);
	return sd;
};

ZMTB_AjxSoapDoc.createFromXml =
function(xml) {
	var sd = new ZMTB_AjxSoapDoc();
	sd._xmlDoc = ZMTB_AjxXmlDoc.createFromXml(xml);
	sd._methodEl = sd._check(sd._xmlDoc);
	return sd;
};

ZMTB_AjxSoapDoc.element2FaultObj =
function(el) {
	// If the element is not a SOAP fault, then return null
	var faultEl = el.firstChild;
	// Safari is bad at handling namespaces
	if (!ZMTB_AjxEnv.isSafari) {
		if (faultEl != null && faultEl.namespaceURI != ZMTB_AjxSoapDoc._SOAP_URI || faultEl.nodeName != (el.prefix + ":Fault"))
			return null;
	} else {
		if (faultEl != null && faultEl.nodeName != (el.prefix + ":Fault"))
			return null;
	}
	return new ZMTB_AjxSoapFault(faultEl);
};

ZMTB_AjxSoapDoc.prototype.setMethodAttribute =
function(name, value) {
	this._methodEl.setAttribute(name, value);
};

/**
 * Creates arguments to pass within the envelope.  "value" can be a JS object
 * or a scalar (string, number, etc.).
 *
 * When "value" is a JS object, set() will call itself recursively in order to
 * create a complex data structure.  Don't pass a "way-too-complicated" object
 * ("value" should only contain references to simple JS objects, or better put,
 * hashes--don't include a reference to the "window" object as it will kill
 * your browser).
 *
 * Example:
 *
 *    soapDoc.set("user_auth", {
 *       user_name : "foo",
 *       password  : "bar"
 *    });
 *
 * will create an XML like this under the method tag:
 *
 *    <user_auth>
 *      <user_name>foo</user_name>
 *      <password>bar</password>
 *    </user_auth>
 *
 * Of course, nesting other hashes is allowed and will work as expected.
 *
 * NOTE: you can pass null for "name", in which case "value" is expected to be
 * an object whose properties will be created directly under the method el.
 */
ZMTB_AjxSoapDoc.prototype.set = 
function(name, value, parent, namespace) {

	var	doc = this.getDoc();
	var useNS = doc.createElementNS && !ZMTB_AjxEnv.isSafari;
	if (value != null && value instanceof Array)
	{
		for (var i=0; i < value.length; i++) {
			var	p = name
				? (namespace && useNS ? doc.createElementNS(namespace, name) : doc.createElement(name))
				: doc.createDocumentFragment();
			this.set(name, value[i], parent);
			//parent.appendChild(p)
		};
		return;
	}
	//Components.utils.reportError("name is "+name)	
	var	p = name
		? (namespace && useNS ? doc.createElementNS(namespace, name) : doc.createElement(name))
		: doc.createDocumentFragment();

	if (namespace && !useNS) p.setAttribute("xmlns", namespace);
		
	if (value != null) {
		if (typeof value == "object")
		{
			for (i in value)
				this.set(i, value[i], p);
		}else {
			if (ZMTB_AjxEnv.isSafari) value = ZMTB_AjxStringUtil.xmlEncode(value);
			p.appendChild(doc.createTextNode(value));
		}
	}
	if (!parent)
		parent = this._methodEl;
	return parent.appendChild(p);
};

ZMTB_AjxSoapDoc.prototype.getMethod =
function() {
	return this._methodEl;
};

ZMTB_AjxSoapDoc.prototype.createHeaderElement =
function() {
	var d = this._xmlDoc.getDoc();
	var envEl = d.firstChild;
	var header = this.getHeader();
	if (header != null) {
		throw new AjxSoapException("SOAP header already exists", AjxSoapException.ELEMENT_EXISTS, "ZMTB_AjxSoapDoc.prototype.createHeaderElement");
	}
	var useNS = d.createElementNS && !ZMTB_AjxEnv.isSafari;	
	header = useNS ? d.createElementNS(this._soapURI, "soap:Header") : d.createElement("soap:Header")
	envEl.insertBefore(header, envEl.firstChild);
	return header;
};

ZMTB_AjxSoapDoc.prototype.getHeader =
function() {
	// would love to use getElementsByTagNameNS, but IE does not support it
	var d = this._xmlDoc.getDoc();
	var nodeList = ZMTB_AjxEnv.isIE
		? (d.getElementsByTagName(d.firstChild.prefix + ":Header"))
		: (d.getElementsByTagNameNS(this._soapURI, "Header"));

	return nodeList ? nodeList[0] : null;
};

ZMTB_AjxSoapDoc.prototype.getBody =
function() {
	// would love to use getElementsByTagNameNS, but IE does not support it
	var d = this._xmlDoc.getDoc();
	var nodeList = ZMTB_AjxEnv.isIE
		? (d.getElementsByTagName(d.firstChild.prefix + ":Body"))
		: (d.getElementsByTagNameNS(this._soapURI, "Body"));

	return nodeList ? nodeList[0] : null;
};

ZMTB_AjxSoapDoc.prototype.getByTagName =
function(type) {
	if (type.indexOf(":") == -1)
		type = "soap:" + type;
	var a = this.getDoc().getElementsByTagName(type);

	if (a.length == 1)		return a[0];
	else if (a.length > 0)	return a;
	else					return null;
};

// gimme a header, no exceptions.
ZMTB_AjxSoapDoc.prototype.ensureHeader =
function() {
	return (this.getHeader() || this.createHeaderElement());
};

ZMTB_AjxSoapDoc.prototype.getDoc =
function() {
	return this._xmlDoc.getDoc();
};

/**
 * Adopts a node from another document to this document.
 */
ZMTB_AjxSoapDoc.prototype.adoptNode =
function(node) {
	// Older firefoxes throw not implemented error when you call adoptNode.	
	if (ZMTB_AjxEnv.isFirefox3up || !ZMTB_AjxEnv.isFirefox) {
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

ZMTB_AjxSoapDoc.prototype.getXml =
function() {
	return ZMTB_AjxEnv.isSafari || ZMTB_AjxEnv.isOpera
		? (ZMTB_AjxXmlDoc.getXml(this._xmlDoc.getDoc()))
		: this._xmlDoc.getDoc().xml;
};

// Very simple checking of soap doc. Should be made more comprehensive
ZMTB_AjxSoapDoc.prototype._check =
function(xmlDoc) {
	var doc = xmlDoc.getDoc();
	if (doc.childNodes.length != 1)
		throw new AjxSoapException("Invalid SOAP PDU", AjxSoapException.INVALID_PDU, "ZMTB_AjxSoapDoc.createFromXml:1");

	// Check to make sure we have a soap envelope
	var el = doc.firstChild;

	// Safari is bad at handling namespaces
	if (!ZMTB_AjxEnv.isSafari) {
		if (el.namespaceURI != ZMTB_AjxSoapDoc._SOAP_URI ||
		    el.nodeName != (el.prefix + ":Envelope") ||
		    (el.childNodes.length < 1 || el.childNodes.length > 2))
		{
			//DBG.println("<font color=red>XML PARSE ERROR on RESPONSE:</font>");
			//DBG.printRaw(doc.xml);
			throw new AjxSoapException("Invalid SOAP PDU", AjxSoapException.INVALID_PDU, "ZMTB_AjxSoapDoc.createFromXml:2");
		}
	} else {
		if (el.nodeName != (el.prefix + ":Envelope"))
			throw new AjxSoapException("Invalid SOAP PDU", AjxSoapException.INVALID_PDU, "ZMTB_AjxSoapDoc.createFromXml:2");
	}
};
