/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2005, 2006, 2007, 2009, 2010, 2012 VMware, Inc.
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
 * XSLT engine <a href="http://www.w3.org/TR/xslt">http://www.w3.org/TR/xslt</a>
 * @class
 * Supports IE and Firefox. Use the following static methods to create instance.
 *
 * <pre>
 * xslt = AjxXslt.createFromUrl(url of the stylesheet)
 * xslt = AjxXslt.createFromString(stylesheet in string)
 * </pre>
 *
 * Then apply the transformation on a document.  Two methods are available depending on the needs.
 *
 * <pre>
 * dom = xslt.transformToDom(doc);
 * xml = xslt.transformToString(doc);
 * </pre>
 * 
 * @private
 */
AjxXslt = function() {
	var doc = AjxXmlDoc.create();
	if (AjxEnv.isIE) {
		var msdoc = null;
		var vers = ["MSXML2.FreeThreadedDOMDocument.5.0", "MSXML2.FreeThreadedDOMDocument.3.0"];
		for (var i = 0; i < vers.length; i++) {
			try {
				msdoc = new ActiveXObject(vers[i]);
				break;
			} catch (ex) {
			}
		}
		if (!msdoc) {
			throw new AjxException("FreeThreadedDOMDocument", AjxException.UNSUPPORTED, "AjxXslt");
		}
		msdoc.async = false;
		doc._doc = msdoc;
	}
	this._doc = doc;
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
	
	xslt._doc.loadFromString(str);
	xslt.createProcessor();
	
	return xslt;
};

AjxXslt.prototype.createProcessor =
function() {
	var doc = this._doc.getDoc();
	if (AjxEnv.isNav || AjxEnv.isChrome || AjxEnv.isSafari) {
		this._processor = new XSLTProcessor();
		if(this._processor) {
			this._processor.importStylesheet(doc);
		}
	} else if (AjxEnv.isIE) {
		var err = doc.parseError;
	    if (err.errorCode != 0) {
			DBG.println(AjxDebug.DBG1, "Parse error (" + err.reason + ") at line " + err.line + ", character " + err.linepos + "\n" + err.srcText);
			throw new AjxException(err.reason, AjxException.INVALID_PARAM, "AjxXslt.createProcessor");
		}

		var proc = null;
		var vers = ["MSXML2.XSLTemplate.5.0", "MSXML2.XSLTemplate.3.0"];
		for (var i = 0; i < vers.length; i++) {
			try {
				proc = new ActiveXObject(vers[i]);
				break;
			} catch (ex) {
			}
		}
		if (!proc) {
			throw new AjxException("XSLTemplate", AjxException.UNSUPPORTED, "AjxXslt.createProcessor");
		}
        this._processor = proc;
		if(this._processor) {
			this._processor.stylesheet = doc;
		}
	}
};

AjxXslt._finishedLoading =
function() {
	var xslt = this._xslt;  // "this" is the document which xsl is being loaded to.
	xslt.createProcessor();
};

AjxXslt.prototype.loadUrl =
function(url) {
	var doc = this._doc;
	
	if (AjxEnv.isNav) {
		var docImpl = doc.getDoc();
		docImpl._xslt = this;  // for callback
		docImpl.addEventListener("load", AjxXslt._finishedLoading, false);
	}

	doc.loadFromUrl(url);

	if (AjxEnv.isIE || AjxEnv.isChrome || AjxEnv.isSafari) {
		this.createProcessor();
	}
};

AjxXslt.prototype.transformToDom =
function(dom) {
	var ret;
	if (AjxEnv.isIE) {
		ret = this.transformIE(dom);
	} else if (AjxEnv.isNav || AjxEnv.isChrome || AjxEnv.isSafari) {
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
	} else if (AjxEnv.isNav || AjxEnv.isChrome || AjxEnv.isSafari) {
		ret = this.transformNav(dom);
	} else {
		DBG.println(AjxDebug.DBG1, "No XSL transformation due to browser incompatibility.");
		return dom.documentElement.innerHTML;
	}
	
	if (!ret || !ret.documentElement) {
		throw new AjxException("XSL transformation failed.", AjxException.INVALID_PARAM, "AjxXslt.transformToString");
	}
	
	var elem = ret.documentElement;
	if ((elem instanceof HTMLElement) ||
		(elem instanceof HTMLHtmlElement)) {
		// good.
		return elem.innerHTML;
	} else if (elem instanceof Element) {
		// ok.
		return AjxXmlDoc.replaceInvalidChars(elem.xml);
	}
	DBG.println(AjxDebug.DBG1, "Transformation resulted in non-element.");
	return dom.documentElement.innerHTML;
};

/**
* IE returns html text.
*/
AjxXslt.prototype.transformIE =
function(dom) {
	try {
		var xsltProc = this._processor.createProcessor();
        xsltProc.input = dom;
        xsltProc.transform();
		return xsltProc.output;
	} catch (exception) {
		DBG.println(AjxDebug.DBG1, "Exception in XSL transformation: "+exception.description);
		throw new AjxException(exception.description, AjxException.INVALID_PARAM, "AjxXslt.transformIE");
	}
};

/**
* Returns either HTMLDocument or XMLDocument, depending on the transformation.
*/
AjxXslt.prototype.transformNav =
function(dom) {
	if(!this._processor) {
		return "";
	}
	return this._processor.transformToDocument(dom);
};

/**
* Returns DocumentFragment
*/
AjxXslt.prototype.transformNav2 =
function(dom) {
	this._fragment = document.implementation.createDocument("", "", null);
	if(!this._processor) {
		return "";
	}
	return this._processor.transformToFragment(dom, this._fragment);
};
