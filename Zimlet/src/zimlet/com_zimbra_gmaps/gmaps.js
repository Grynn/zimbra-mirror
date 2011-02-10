/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Zimlets
 * Copyright (C) 2005, 2006, 2007, 2008, 2009, 2010 Zimbra, Inc.
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
 * Zimlet to handle integration with a Yahoo! Maps
 *
 * @author Raja Rao DV
 */
function ZmGMapsZimlet() {
}

ZmGMapsZimlet.prototype = new ZmZimletBase();
ZmGMapsZimlet.prototype.constructor = ZmGMapsZimlet;



/**
 * GMaps Static map url
 */
ZmGMapsZimlet.URL = "http://maps.google.com/maps/api/staticmap";
ZmGMapsZimlet.URLPARAMS = [];
ZmGMapsZimlet.URLPARAMS["size"] = "345x245";
ZmGMapsZimlet.URLPARAMS["zoom"] = "12";
ZmGMapsZimlet.URLPARAMS["sensor"] = "false";
ZmGMapsZimlet.URLPARAMS["markers"] = "color:red";
ZmGMapsZimlet.URLPARAMS["center"] = "";

/**
 * Map image URI cache.
 */
ZmGMapsZimlet.CACHE = [];


/**
 * Called by the framework when generating the span for in-context link.
 *
 */
ZmGMapsZimlet.prototype.match =
function(line, startIndex) {
	this._setRegExps();
	if (this._regexps.length == 0) {
		return;
	}

	var a = this._regexps;
	var ret = null;
	for (var i = 0; i < a.length; ++i) {
		var re = a[i];
		re.lastIndex = startIndex;
		var m = re.exec(line);
		if (m && m[0] != "") {
			if(this._skipRegex && this._skipRegex.test(m[0])) {
				continue;
			}

			if (!ret || m.index < ret.index) {
				ret = m;
				ret.matchLength = m[0].length;
				return ret;
			}
		}
	}
	return ret;
};

ZmGMapsZimlet.prototype._setRegExps =
function() {
	if(this._regexps) {
		return this._regexps;
	}
	var re = this.getMessage("completeAddressRegex");
	if(!re || re == "\"\"") {
		var addressFirstPartRegEx = this.getMessage("addressFirstPartRegEx");
		var zipCodeRegEx = this.getMessage("zipCodeRegEx");
		var countryNameRegEx = this.getMessage("countryNameRegEx");
		re = [addressFirstPartRegEx , "(", zipCodeRegEx, "|", countryNameRegEx, ")"].join("");
	}
	this._regexps = new Array();
	this._regexps.push(new RegExp(re, "ig"));
	var sRE = this.getMessage("skipRegex");
	if(!sRE || sRE == "\"\"") {
		this._skipRegex = null;
	} else {
		this._skipRegex = new RegExp(AjxStringUtil.trim(sRE), "ig");
	}	
};

/**
 * Called when clicked on matched text.
 */
ZmGMapsZimlet.prototype.clicked =
function(spanElem, contentObj, matchContext, canvas) {
	var addr = contentObj.replace("\n","+").replace("\r","+").replace(/ /g, "+");
	canvas = window.open("http://maps.google.com/maps?q="+escape(addr));
};

/**
 * Handles tooltip popped-up event.
 *
 */
ZmGMapsZimlet.prototype.toolTipPoppedUp =
function(spanElement, addrs, context, canvas) {
	var url = this._getMapUrl(addrs);
	var id = Dwt.getNextId();
	canvas.innerHTML = [
		'<center><img width="345" height="245" id="',
		id,
		'" src="',
		this.getResource('blank_pixel.gif'),
		'"/></center>'
	].join("");
	var el = document.getElementById(id);
	el.style.backgroundImage = "url("+url+")";	
};

ZmGMapsZimlet.prototype._getMapUrl =
function(addrs) {
	addrs = addrs.replace("\n","+").replace("\r","+").replace(/ /g, "+");
	var params = [];
	var val = "";
	for(var el in ZmGMapsZimlet.URLPARAMS) {		
		if(el == "center") {
			val = AjxStringUtil.urlEncode(addrs);
		} else {
			val = AjxStringUtil.urlComponentEncode(ZmGMapsZimlet.URLPARAMS[el]);
		}
		params.push(el + "=" + val);
	}
	var url = ZmGMapsZimlet.URL + "?" + params.join("&");
	url = ZmZimletBase.PROXY + AjxStringUtil.urlComponentEncode(url);
	return url;
};