// Don't directly instantiate SoapDoc, use one of the create factory methods instead
function LsSoapDoc() {
}

LsSoapDoc.prototype.toString = 
function() {
	return "LsSoapDoc";
}

LsSoapDoc._SOAP_URI = "http://www.w3.org/2003/05/soap-envelope";
LsSoapDoc._XMLNS_URI = "http://www.w3.org/2000/xmlns";

LsSoapDoc.create =
function(method, namespace, namespaceId) {
	var sd = new LsSoapDoc();
	sd._xmlDoc = LsXmlDoc.create();
	var d = sd._xmlDoc.getDoc();	
	var envEl = d.createElement("soap:Envelope");
	
	envEl.setAttribute("xmlns:soap", LsSoapDoc._SOAP_URI);

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

LsSoapDoc.createFromDom =
function(doc) {	
	var sd = new LsSoapDoc();
	sd._xmlDoc = LsXmlDoc.createFromDom(doc);
	sd._methodEl = sd._check(sd._xmlDoc);
	return sd;
}

LsSoapDoc.createFromXml =
function(xml) {
	var sd = new LsSoapDoc();
	sd._xmlDoc = LsXmlDoc.createFromXml(xml);
	sd._methodEl = sd._check(sd._xmlDoc);
	return sd;
}

LsSoapDoc.element2FaultObj =
function(el) {
	// If the element is not a SOAP fault, then return null
	var faultEl = el.firstChild;
	// Safari sux at handling namespaces
	if (!LsEnv.isSafari) {
		if (faultEl != null && faultEl.namespaceURI != LsSoapDoc._SOAP_URI || faultEl.nodeName != (el.prefix + ":Fault"))
			return null;
	} else {
		if (faultEl != null && faultEl.nodeName != (el.prefix + ":Fault"))
			return null;
	}
	return new LsSoapFault(faultEl);
}

LsSoapDoc.prototype.setMethodAttribute =
function(name, value){
	this._methodEl.setAttribute(name, value);
};

LsSoapDoc.prototype.set =
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

LsSoapDoc.prototype.getMethod =
function() {
	return this._methodEl;
}

LsSoapDoc.prototype.createHeaderElement =
function() {
	var d = this._xmlDoc.getDoc();
	var envEl = d.firstChild;
	var header = this.getHeader();
	if (header != null) {
		throw new LsSoapException("SOAP header already exists", LsSoapException.ELEMENT_EXISTS, "LsSoapDoc.prototype.createHeaderElement");
	}
	header = d.createElement("soap:Header")
	envEl.insertBefore(header, envEl.firstChild);
	return header;
}

LsSoapDoc.prototype.getHeader =
function() {
	// would love to use getElementsByTagNameNS, but IE does not support it
	var d = this._xmlDoc.getDoc();
	var nodeList;
	if (LsEnv.isIE)
		nodeList = d.getElementsByTagName(d.firstChild.prefix + ":Header");
	else
		nodeList = d.getElementsByTagNameNS(LsSoapDoc._SOAP_URI, "Header");
	if (nodeList == null) 
		return null;
	return nodeList[0];
}


LsSoapDoc.prototype.getBody =
function() {
	// would love to use getElementsByTagNameNS, but IE does not support it
	var d = this._xmlDoc.getDoc();
	var nodeList;
	if (LsEnv.isIE)
		nodeList = d.getElementsByTagName(d.firstChild.prefix + ":Body");
	else
		nodeList = d.getElementsByTagNameNS(LsSoapDoc._SOAP_URI, "Body");
	if (nodeList == null) 
		return null;
	return nodeList[0];
}

LsSoapDoc.prototype.getDoc =
function() {
	return this._xmlDoc.getDoc();
}

LsSoapDoc.prototype.getXml =
function() {
	if (LsEnv.isSafari)
		return LsXmlDoc.getXml(this._xmlDoc.getDoc());
	else 
		return this._xmlDoc.getDoc().xml;
}


// Very simple checking of soap doc. Should be made more comprehensive
LsSoapDoc.prototype._check =
function(xmlDoc) {
	var doc = xmlDoc.getDoc();
	if (doc.childNodes.length != 1) 
		throw new LsSoapException("Invalid SOAP PDU", LsSoapException.INVALID_PDU, "LsSoapDoc.createFromXml:1");

	// Check to make sure we have a soap envelope
	var el = doc.firstChild;

	// Safari sux at handling namespaces
	if (!LsEnv.isSafari) {
		if (el.namespaceURI != LsSoapDoc._SOAP_URI || 
		    el.nodeName != (el.prefix + ":Envelope") || 
		    (el.childNodes.length < 1 || el.childNodes.length > 2)) 
		{
			DBG.println("<font color=red>XML PARSE ERROR on RESPONSE:</font>");
			DBG.printRaw(doc.xml);
			throw new LsSoapException("Invalid SOAP PDU", LsSoapException.INVALID_PDU, "LsSoapDoc.createFromXml:2");
		}
	} else {
		if (el.nodeName != (el.prefix + ":Envelope"))
			throw new LsSoapException("Invalid SOAP PDU", LsSoapException.INVALID_PDU, "LsSoapDoc.createFromXml:2");
	}
}


