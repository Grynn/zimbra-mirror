// Don't directly instantiate SoapDoc, use one of the create factory methods instead
function AjxSoapDoc() {
}

AjxSoapDoc.prototype.toString = 
function() {
	return "AjxSoapDoc";
}

AjxSoapDoc._SOAP_URI = "http://www.w3.org/2003/05/soap-envelope";
AjxSoapDoc._XMLNS_URI = "http://www.w3.org/2000/xmlns";

AjxSoapDoc.create =
function(method, namespace, namespaceId) {
	var sd = new AjxSoapDoc();
	sd._xmlDoc = AjxXmlDoc.create();
	var d = sd._xmlDoc.getDoc();	
	var envEl = d.createElement("soap:Envelope");
	
	envEl.setAttribute("xmlns:soap", AjxSoapDoc._SOAP_URI);

	d.appendChild(envEl);
	
	var bodyEl = d.createElement("soap:Body");
	envEl.appendChild(bodyEl);

	sd._methodEl = d.createElement(method);
	if (namespaceId == null)
		sd._methodEl.setAttribute("xmlns", namespace);
	else 
		sd._methodEl.setAttribute("xmlns:" + namespaceId, namespace);
		
	bodyEl.appendChild(sd._methodEl);
	return sd;
}

AjxSoapDoc.createFromDom =
function(doc) {	
	var sd = new AjxSoapDoc();
	sd._xmlDoc = AjxXmlDoc.createFromDom(doc);
	sd._methodEl = sd._check(sd._xmlDoc);
	return sd;
}

AjxSoapDoc.createFromXml =
function(xml) {
	var sd = new AjxSoapDoc();
	sd._xmlDoc = AjxXmlDoc.createFromXml(xml);
	sd._methodEl = sd._check(sd._xmlDoc);
	return sd;
}

AjxSoapDoc.element2FaultObj =
function(el) {
	// If the element is not a SOAP fault, then return null
	var faultEl = el.firstChild;
	// Safari sux at handling namespaces
	if (!AjxEnv.isSafari) {
		if (faultEl != null && faultEl.namespaceURI != AjxSoapDoc._SOAP_URI || faultEl.nodeName != (el.prefix + ":Fault"))
			return null;
	} else {
		if (faultEl != null && faultEl.nodeName != (el.prefix + ":Fault"))
			return null;
	}
	return new AjxSoapFault(faultEl);
}

AjxSoapDoc.prototype.setMethodAttribute =
function(name, value){
	this._methodEl.setAttribute(name, value);
};

AjxSoapDoc.prototype.set =
function(name, value, element) {
	var p = this._xmlDoc.getDoc().createElement(name);
	if (value != null) {
   		 var cdata = this._xmlDoc.getDoc().createTextNode("");
   		 p.appendChild(cdata);
		cdata.nodeValue = value;
	}
	if (element == null) {
		this._methodEl.appendChild(p);
	} else {
		element.appendChild(p);
	}
	return p;
}

AjxSoapDoc.prototype.getMethod =
function() {
	return this._methodEl;
}

AjxSoapDoc.prototype.createHeaderElement =
function() {
	var d = this._xmlDoc.getDoc();
	var envEl = d.firstChild;
	var header = this.getHeader();
	if (header != null) {
		throw new AjxSoapException("SOAP header already exists", AjxSoapException.ELEMENT_EXISTS, "AjxSoapDoc.prototype.createHeaderElement");
	}
	header = d.createElement("soap:Header")
	envEl.insertBefore(header, envEl.firstChild);
	return header;
}

AjxSoapDoc.prototype.getHeader =
function() {
	// would love to use getElementsByTagNameNS, but IE does not support it
	var d = this._xmlDoc.getDoc();
	var nodeList;
	if (AjxEnv.isIE)
		nodeList = d.getElementsByTagName(d.firstChild.prefix + ":Header");
	else
		nodeList = d.getElementsByTagNameNS(AjxSoapDoc._SOAP_URI, "Header");
	if (nodeList == null) 
		return null;
	return nodeList[0];
}


AjxSoapDoc.prototype.getBody =
function() {
	// would love to use getElementsByTagNameNS, but IE does not support it
	var d = this._xmlDoc.getDoc();
	var nodeList;
	if (AjxEnv.isIE)
		nodeList = d.getElementsByTagName(d.firstChild.prefix + ":Body");
	else
		nodeList = d.getElementsByTagNameNS(AjxSoapDoc._SOAP_URI, "Body");
	if (nodeList == null) 
		return null;
	return nodeList[0];
}

AjxSoapDoc.prototype.getDoc =
function() {
	return this._xmlDoc.getDoc();
}

AjxSoapDoc.prototype.getXml =
function() {
	if (AjxEnv.isSafari)
		return AjxXmlDoc.getXml(this._xmlDoc.getDoc());
	else 
		return this._xmlDoc.getDoc().xml;
}


// Very simple checking of soap doc. Should be made more comprehensive
AjxSoapDoc.prototype._check =
function(xmlDoc) {
	var doc = xmlDoc.getDoc();
	if (doc.childNodes.length != 1) 
		throw new AjxSoapException("Invalid SOAP PDU", AjxSoapException.INVALID_PDU, "AjxSoapDoc.createFromXml:1");

	// Check to make sure we have a soap envelope
	var el = doc.firstChild;

	// Safari sux at handling namespaces
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
}


