/*
* ***** BEGIN LICENSE BLOCK *****
* Version: MPL 1.1
*
* The contents of this file are subject to the Mozilla Public
* License Version 1.1 ("License"); you may not use this file except in
* compliance with the License. You may obtain a copy of the License at
* http://www.zimbra.com/license
*
* Software distributed under the License is distributed on an "AS IS"
* basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See
* the License for the specific language governing rights and limitations
* under the License.
*
* The Original Code is: Zimbra AJAX Toolkit.
*
* The Initial Developer of the Original Code is Zimbra, Inc.
* Portions created by Zimbra are Copyright (C) 2005 Zimbra, Inc.
* All Rights Reserved.
*
* Contributor(s):
*
* ***** END LICENSE BLOCK *****
*/


/**
* XSLT engine.  (http://www.w3.org/TR/xslt)
* Supports IE and Firefox.
*
* Use the following static methods to create instance.
*
* xslt = AjxXslt.createFromUrl(url of the stylesheet)
* xslt = AjxXslt.createFromString(stylesheet in string)
*
* Then apply the transformation on a document.  Two methods are available depending on the needs.
*
* dom = xslt.transformToDom(doc);
* xml = xslt.transformToString(doc);
*
*/
function AjxXslt() {
	this._doc = AjxXmlDoc.create();
};

AjxXslt.prototype.toString =
function() {
	return "AjxXslt";
};

AjxXslt.createFromUrl =
function(url) {
	var xslt = new AjxXslt();
	xslt.loadUrl(url);
	return xslt;
};

AjxXslt.createFromString =
function(str) {
	var xslt = new AjxXslt();
	xslt.loadFromString(str);
	
	if (AjxEnv.isIE()) {
		return xslt;
	}
	
	xslt.createProcessor(xslt._doc.getDoc());
	
	return xslt;
};

AjxXslt.prototype.createProcessor =
function(doc) {
	this._processor = new XSLTProcessor();
	this._processor.importStylesheet(doc);
};

AjxXslt._finishedLoading =
function() {
	DBG.println(AjxDebug.DBG2, "finished loading xsl");
	var xslt = this._xslt;  // "this" is the document which xsl is being loaded to.
	xslt.createProcessor(this);
};

AjxXslt.prototype.loadUrl =
function(url) {
	DBG.println(AjxDebug.DBG2, "loading xsl: "+url);
	var doc = this._doc;
	
	if (AjxEnv.isIE) {
	} else if (AjxEnv.isNav) {
		var docImpl = doc.getDoc();
		docImpl._xslt = this;  // for callback
		docImpl.addEventListener("load", AjxXslt._finishedLoading, false);
	}
	doc.loadFromUrl(url);
};

AjxXslt.prototype.transformToDom =
function(dom) {
	var ret;
	if (AjxEnv.isIE) {
		ret = this.transformIE(dom);
	} else if (AjxEnv.isNav) {
		return this.transformNav(dom);  // already in dom
	} else {
		DBG.println(AjxDebug.DBG1, "No XSL transformation due to browser incompatibility.");
		return dom;
	}
	var doc = AjxXmlDoc.createFromXml(ret);
	return doc.getDoc();
};

AjxXslt.prototype.transformToString =
function(dom) {
	var ret;
	if (AjxEnv.isIE) {
		return this.transformIE(dom);  // already in str
	} else if (AjxEnv.isNav) {
		DBG.println(AjxDebug.DBG1, "transforming..");
		ret = this.transformNav(dom);
	} else {
		DBG.println(AjxDebug.DBG1, "No XSL transformation due to browser incompatibility.");
	}
	DBG.println(AjxDebug.DBG1, "ret: "+ret);
	DBG.println(AjxDebug.DBG1, "ret.documentElement: "+ret.documentElement);
	return ret.documentElement.innerHTML;
};

/**
* IE returns html text.
*/
AjxXslt.prototype.transformIE =
function(dom) {
	return dom.transformNode(this._doc.getDoc());
};

/**
* Returns either HTMLDocument or XMLDocument, depending on the transformation.
*/
AjxXslt.prototype.transformNav =
function(dom) {
	return this._processor.transformToDocument(dom);
};

/**
* Returns DocumentFragment
*/
AjxXslt.prototype.transformNav2 =
function(dom) {
	this._fragment = document.implementation.createDocument("", "", null);
	return this._processor.transformToFragment(dom, this._fragment);
};
