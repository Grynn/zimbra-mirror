// Don't directly instantiate LsXmlDoc, use one of the create factory methods instead
function LsXmlDoc(init) {
	if (arguments.length == 0) return;
	if (!LsXmlDoc._inited)
		LsXmlDoc._init();
}

LsXmlDoc.prototype.toString = 
function() {
	return "LsXmlDoc";
}

LsXmlDoc._inited = false;
LsXmlDoc._msxmlVers = null;

LsXmlDoc.create =
function() {
	var xmlDoc = new LsXmlDoc(true);
	var newDoc = null;
	if (LsEnv.isIE) {
		newDoc = new ActiveXObject(LsXmlDoc._msxmlVers);
		newDoc.async = true; // Force Async loading
		if (LsXmlDoc._msxmlVers == "MSXML2.DOMDocument.4.0") {
			newDoc.setProperty("SelectionLanguage", "XPath");
			newDoc.setProperty("SelectionNamespaces", "xmlns:liquid='urn:liquid' xmlns:mail='urn:liquidMail' xmlns:account='urn:liquidAccount'");
		}
	} else if (document.implementation && document.implementation.createDocument) {
		newDoc = document.implementation.createDocument("", "", null);
	} else {
		throw new LsException("Unable to create new Doc", LsException.INTERNAL_ERROR, "LsXmlDoc.create");
	}
	xmlDoc._doc = newDoc;
	return xmlDoc;
}

LsXmlDoc.createFromDom =
function(doc) {
	var xmlDoc = new LsXmlDoc();
	xmlDoc._doc = doc;
	return xmlDoc;
}

LsXmlDoc.createFromXml =
function(xml) {
	var xmlDoc = LsXmlDoc.create();
	xmlDoc.loadFromString(xml);
	return xmlDoc;
}

LsXmlDoc.getXml =
function(node) {
	var ser = new XMLSerializer();
	return ser.serializeToString(node);
}

LsXmlDoc.prototype.getDoc =
function() {
	return this._doc;
}

LsXmlDoc.prototype.loadFromString =
function(str) {
	this._doc.loadXML(str);

}

LsXmlDoc.prototype.loadFromUrl =
function(url) {
	this._doc.load(url);
}

LsXmlDoc._init =
function() {
	if (LsEnv.isIE) {
		var msxmlVers = ["MSXML4.DOMDocument", "MSXML3.DOMDocument", "MSXML2.DOMDocument.4.0",
										 "MSXML2.DOMDocument.3.0", "MSXML2.DOMDocument", "MSXML.DOMDocument", 
										 "Microsoft.XmlDom"];
		for (var i = 0; i < msxmlVers.length; i++) {
			try {
				new ActiveXObject(msxmlVers[i]);
				LsXmlDoc._msxmlVers = msxmlVers[i];
				break;
			} catch (ex) {
			}
		}
		if (LsXmlDoc._msxmlVers == null)
			throw new LsException("MSXML not installed", LsException.INTERNAL_ERROR, "LsXmlDoc._init");
	}	else if (LsEnv.isNav) {
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
	
	if (LsEnv.isNav || LsEnv.isSafari) {
	}
	
	LsXmlDoc._inited = true;
}

