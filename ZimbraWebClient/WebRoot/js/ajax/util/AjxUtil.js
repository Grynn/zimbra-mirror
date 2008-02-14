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


/**
 * AjxUtil - static class with some utility methods. This is where to
 * put things when no other class wants them.
 *
 * 12/3/2004 At this point, it only needs AjxEnv to be loaded.
 */
AjxUtil = function() {
};

AjxUtil.FLOAT_RE = /^[+\-]?((\d+(\.\d*)?)|((\d*\.)?\d+))([eE][+\-]?\d+)?$/;
AjxUtil.NOTFLOAT_RE = /[^\d\.]/;
AjxUtil.NOTINT_RE = /[^0-9]+/;
AjxUtil.LIFETIME_FIELD = /^([0-9])+([dhms])?$/;

AjxUtil.isSpecified 		= function(aThing) { return ((aThing !== void 0) && (aThing !== null)); };
AjxUtil.isUndefined 		= function(aThing) { return (aThing === void 0); };
AjxUtil.isNull 				= function(aThing) { return (aThing === null); };
AjxUtil.isBoolean 			= function(aThing) { return (typeof(aThing) == 'boolean'); };
AjxUtil.isString 			= function(aThing) { return (typeof(aThing) == 'string'); };
AjxUtil.isNumber 			= function(aThing) { return (typeof(aThing) == 'number'); };
AjxUtil.isObject 			= function(aThing) { return ((typeof(aThing) == 'object') && (aThing !== null)); };
AjxUtil.isArray 			= function(aThing) { return AjxUtil.isInstance(aThing, Array); };
AjxUtil.isFunction 			= function(aThing) { return (typeof(aThing) == 'function'); };
AjxUtil.isDate 				= function(aThing) { return AjxUtil.isInstance(aThing, Date); };
AjxUtil.isLifeTime 			= function(aThing) { return AjxUtil.LIFETIME_FIELD.test(aThing); };
AjxUtil.isNumeric 			= function(aThing) { return (!isNaN(parseFloat(aThing)) && AjxUtil.FLOAT_RE.test(aThing) && !AjxUtil.NOTFLOAT_RE.test(aThing)); };
AjxUtil.isLong			    = function(aThing) { return (AjxUtil.isNumeric(aThing) && !AjxUtil.NOTINT_RE.test(aThing)); };
AjxUtil.isNonNegativeLong   = function(aThing) { return (AjxUtil.isNumeric(aThing) && AjxUtil.isLong(aThing) && (parseFloat(aThing) >= 0)); };
AjxUtil.isInt			    = function(aThing) { return (AjxUtil.isNumeric(aThing) && !AjxUtil.NOTINT_RE.test(aThing)); };
AjxUtil.isPositiveInt   	= function(aThing) { return (AjxUtil.isNumeric(aThing) && AjxUtil.isInt(aThing) && (parseInt(aThing) > 0)); };

// REVISIT: Should do more precise checking. However, there are names in
//			common use that do not follow the RFC patterns (e.g. domain
//			names that start with digits).
AjxUtil.IP_ADDRESS_RE = /^\d{1,3}(\.\d{1,3}){3}(\.\d{1,3}\.\d{1,3})?$/;
AjxUtil.IP_ADDRESS_WITH_PORT_RE = /^\d{1,3}(\.\d{1,3}){3}(\.\d{1,3}\.\d{1,3})?:\d{1,5}$/;
AjxUtil.SUBNET_RE = /^\d{1,3}(\.\d{1,3}){3}(\.\d{1,3}\.\d{1,3})?\/\d{1,2}$/;
AjxUtil.DOMAIN_NAME_SHORT_RE = /^[A-Za-z0-9\-]{2,}$/;
AjxUtil.DOMAIN_NAME_FULL_RE = /^[A-Za-z0-9\-]{1,}(\.[A-Za-z0-9\-]{2,}){1,}$/;
AjxUtil.HOST_NAME_RE = /^[A-Za-z0-9\-]{2,}(\.[A-Za-z0-9\-]{1,})*(\.[A-Za-z0-9\-]{2,})*$/;
AjxUtil.HOST_NAME_WITH_PORT_RE = /^[A-Za-z0-9\-]{2,}(\.[A-Za-z0-9\-]{2,})*:([0-9])+$/;
AjxUtil.EMAIL_SHORT_RE = /^[^@\s]+$/;
AjxUtil.EMAIL_FULL_RE = /^[^@\s]+@[A-Za-z0-9\-]{2,}(\.[A-Za-z0-9\-]{2,})+$/;
AjxUtil.SHORT_URL_RE = /^[A-Za-z0-9]{2,}:\/\/[A-Za-z]+[A-Za-z0-9\-]+(\.[A-Za-z]+[A-Za-z0-9\-]+)*(:([0-9])+)*$/;
AjxUtil.IP_SHORT_URL_RE = /^[A-Za-z0-9]{2,}:\/\/\d{1,3}(\.\d{1,3}){3}(\.\d{1,3}\.\d{1,3})?(:([0-9])+)*$/;

AjxUtil.isIpAddress 		= function(s) { return AjxUtil.IP_ADDR_RE.test(s); };
AjxUtil.isDomain 			= function(s) {	return AjxUtil.DOMAIN_RE.test(s); };
AjxUtil.isHostName 			= function(s) { return AjxUtil.HOST_NAME_RE.test(s); };
AjxUtil.isDomainName = 
function(s, shortMatch) {
	return shortMatch 
		? AjxUtil.DOMAIN_NAME_SHORT_RE.test(s) 
		: AjxUtil.DOMAIN_NAME_FULL_RE.test(s);
};

AjxUtil.isEmailAddress = 
function(s, nameOnly) {
	return nameOnly 
		? AjxUtil.EMAIL_SHORT_RE.test(s) 
		: AjxUtil.EMAIL_FULL_RE.test(s);
};

AjxUtil.SIZE_GIGABYTES = "GB";
AjxUtil.SIZE_MEGABYTES = "MB";
AjxUtil.SIZE_KILOBYTES = "KB";
AjxUtil.SIZE_BYTES = "B";

/**
 * Formats a size (in bytes) to the largest whole unit. For example,
 * AjxUtil.formatSize(302132199) returns "288 MB".
 *
 * @param size      The size (in bytes) to be formatted.
 * @param round     True to round to nearest integer. Default is true.
 * @param fractions Number of fractional digits to display, if not rounding.
 *                  Trailing zeros after the decimal point are trimmed.
 */
AjxUtil.formatSize = 
function(size, round, fractions) {
	if (round == null) round = true;
	if (fractions == null) fractions = 20; // max allowed for toFixed is 20

	var formattedUnits = AjxMsg.sizeBytes;
	var units = AjxMsg.SIZE_BYTES;
	if (size >= 1073741824) {
		formattedUnits = AjxMsg.sizeGigaBytes;
		units = AjxUtil.SIZE_GIGABYTES;
	}
	else if (size >= 1048576) {
		formattedUnits = AjxMsg.sizeMegaBytes;
		units = AjxUtil.SIZE_MEGABYTES;
	}
	else if (size > 1023) {
		formattedUnits = AjxMsg.sizeKiloBytes;
		units = AjxUtil.SIZE_KILOBYTES;
	}


	var formattedSize = AjxUtil.formatSizeForUnits(size, units, round, fractions);
	return AjxMessageFormat.format(AjxMsg.formatSizeAndUnits, [formattedSize, formattedUnits]);
};

/**
 * Formats a size (in bytes) to a specific unit. Since the unit size is
 * known, the unit is not shown in the returned string. For example,
 * AjxUtil.formatSizeForUnit(302132199, AjxUtil.SIZE_MEGABYTES, false, 2) 
 * returns "288.13".
 *
 * @param size      The size (in bytes) to be formatted.
 * @param units     The unit of measure.
 * @param round     True to round to nearest integer. Default is true.
 * @param fractions Number of fractional digits to display, if not rounding.
 *                  Trailing zeros after the decimal point are trimmed.
 */
AjxUtil.formatSizeForUnits = 
function(size, units, round, fractions) {
	if (units == null) units = AjxUtil.SIZE_BYTES;
	if (round == null) round = true;
	if (fractions == null) fractions = 20; // max allowed for toFixed is 20

	switch (units) {
		case AjxUtil.SIZE_GIGABYTES: { size /= 1073741824; break; }
		case AjxUtil.SIZE_MEGABYTES: { size /= 1048576; break; }
		case AjxUtil.SIZE_KILOBYTES: { size /= 1024; break; }
	}
	
	var pattern = I18nMsg.formatNumber.replace(/\..*$/, ""); // Strip off decimal, we'll be adding one anyway
	pattern = pattern.replace(/,/, "");       // Remove the ,
	if (!round && fractions) {
		pattern = pattern += ".";
		for (var i = 0; i < fractions; i++) {
			pattern += "#";
		}
	}
	return AjxNumberFormat.format(pattern, size);
};

/**
 * Performs the opposite of AjxUtil.formatSize in that this function takes a 
 * formatted size.
 *
 * @param units Unit constant: "GB", "MB", "KB", "B". Must be specified 
 *              unless the formatted size ends with the size marker, in
 *				which case the size marker in the formattedSize param
 *				overrides this parameter.
 */
AjxUtil.parseSize = 
function(formattedSize, units) {
	// NOTE: Take advantage of fact that parseFloat ignores bad chars
	//       after numbers
	if (typeof formattedSize != _STRING_) {
		formattedSize = formattedSize.toString() ;
	}
	var size = parseFloat(formattedSize.replace(/^\s*/,""));

	var marker = /[GMK]?B$/i;
	var result = marker.exec(formattedSize);
	if (result) {
		//alert("units: "+units+", result[0]: '"+result[0]+"'");
		units = result[0].toUpperCase();
	}
	
	switch (units) {
		case AjxUtil.SIZE_GIGABYTES: size *= 1073741824; break;
		case AjxUtil.SIZE_MEGABYTES: size *= 1048576; break; 
		case AjxUtil.SIZE_KILOBYTES: size *= 1024; break;
	}
	
	//alert("AjxUtil#parseSize: formattedSize="+formattedSize+", size="+size);
	return size;
};

AjxUtil.isInstance = 
function(aThing, aClass) { 
	return !!(aThing && aThing.constructor && (aThing.constructor === aClass)); 
};

AjxUtil.assert = 
function(aCondition, aMessage) {
	if (!aCondition && AjxUtil.onassert) AjxUtil.onassert(aMessage);
};

AjxUtil.onassert = 
function(aMessage) {
	// Create an exception object and set the message
	var myException = new Object();
	myException.message = aMessage;
	
	// Compile a stack trace
	var myStack = new Array();
	if (AjxEnv.isIE5_5up) {
		// On IE, the caller chain is on the arguments stack
		var myTrace = arguments.caller;
		while (myTrace) {
		    myStack[myStack.length] = myTrace.callee;
	    	myTrace = myTrace.caller;
		}
	} else {
		try {
			var myTrace = arguments.callee.caller;
			while (myTrace) {
				myStack[myStack.length] = myTrace;
				if (myStack.length > 2) break;
				myTrace = myTrace.caller;
		    }
		} catch (e) {
		}
	}
	myException.stack = myStack;
	
	// Alert with the message and a description of the stack
	var stackString = '';
	var MAX_LEN = 170;
	for (var i = 1; i < myStack.length; i++) {
		if (i > 1) stackString += '\n';
		if (i < 11) {
			var fs = myStack[i].toString();
			if (fs.length > MAX_LEN) {
				fs = fs.substr(0,MAX_LEN) + '...';
				fs = fs.replace(/\n/g, '');
			}
			stackString += i + ': ' + fs;
		} else {
			stackString += '(' + (myStack.length - 11) + ' frames follow)';
			break;
		}
	}
	alert('assertion:\n\n' + aMessage + '\n\n---- Call Stack ---\n' + stackString);
	
	// Now throw the exception
	throw myException;
};

// IE doesn't define Node type constants
AjxUtil.ELEMENT_NODE		= 1;
AjxUtil.TEXT_NODE			= 3;
AjxUtil.DOCUMENT_NODE		= 9;

AjxUtil.getInnerText = 
function(node) {
 	if (AjxEnv.isIE)
 		return node.innerText;

	function f(n) {
		if (n) {
			if (n.nodeType == 3 /* TEXT_NODE */)
				return n.data;
			if (n.nodeType == 1 /* ELEMENT_NODE */) {
				if (/^br$/i.test(n.tagName))
					return "\r\n";
				var str = "";
				for (var i = n.firstChild; i; i = i.nextSibling)
					str += f(i);
				return str;
			}
		}
		return "";
	};
	return f(node);
};

/**
 * This method returns a proxy for the specified object. This is useful when
 * you want to modify properties of an object and want to keep those values
 * separate from the values in the original object. You can then iterate
 * over the proxy's properties and use the <code>hasOwnProperty</code>
 * method to determine if the property is a new value in the proxy.
 * <p>
 * <strong>Note:</strong>
 * A reference to the original object is stored in the proxy as the "_object_" 
 * property.
 *
 * @param object [object] The object to proxy.
 * @param level  [number] The number of property levels deep to proxy.
 *						  Defaults to zero.
 */
AjxUtil.createProxy = 
function(object, level) {
	var proxy;
	var proxyCtor = function(){}; // bug #6517 (Safari doesnt like 'new Function')
	proxyCtor.prototype = object;
	if (object instanceof Array) {
		proxy  = new Array();
		var cnt  = object.length;
		for(var ix = 0; ix < cnt; ix++) {
			proxy[ix] = object[ix];
		}
	} else {
		proxy = new proxyCtor;
	}
	
	if (level) {
		for (var prop in object) {
			if (typeof object[prop] == "object")
				proxy[prop] = AjxUtil.createProxy(object[prop], level - 1);
		}
	}	
	
	proxy._object_ = object;
	return proxy;
};

/**
* Returns a copy of a list with empty members removed.
*
* @param list	[array]		original list
*/
AjxUtil.collapseList =
function(list) {
	var newList = [];
	for (var i = 0; i < list.length; i++)
		if (list[i])
			newList.push(list[i]);
	return newList;
};

AjxUtil.indexOf =
function(array, object, strict) {
	if (array) {
		for (var i = 0; i < array.length; i++) {
			var item = array[i];
			if ((strict && item === object) || (!strict && item == object)) {
				return i;
			}
		}
	}
	return -1;
};

AjxUtil.keys = function(object) {
    var keys = [];
    for (var p in object) {
        keys.push(p);
    }
    return keys;
};
AjxUtil.values = function(object) {
    var values = [];
    for (var p in object) {
        values.push(object[p]);
    }
    return values;
};



/**
 *	Parse the query string (part after the "?") and return an object of {param:value, param:value}
 **/
AjxUtil.parseQueryString = function() {
	var location = "" + window.location;
	var questionChar = location.indexOf("?");
	if (questionChar == -1) return {};
	var list = location.substring(questionChar+1).split("&");
	var params = {};
	for (var i = 0; i < list.length; i++) {
		var split = list[i].split("=");
		params[split[0]] = split[1];
	}
	return params;
};

AjxUtil.getFirstElement = function(parent, ename, aname, avalue) {
    for (var child = parent.firstChild; child; child = child.nextSibling) {
        if (child.nodeType != AjxUtil.ELEMENT_NODE) continue;
        if (ename && child.nodeName != ename) continue;
        if (aname) {
            var attr = child.getAttributeNode(aname);
            if (attr.nodeName != aname) continue;
            if (avalue && attr.nodeValue != avalue) continue;
        }
        return child;
    }
    return null;
};

/**
 * @param params	[hash]		hash of params:
 *        relative	[boolean]*	if true, return a relative URL
 *        protocol	[string]*	protocol
 *        host		[string]*	server hostname
 *        port		[int]*		server port
 *        path		[string]*	URL path
 *        qsReset	[boolean]*	if true, clear current query string
 *        qsArgs	[hash]*		set of query string names and values
 */
AjxUtil.formatUrl =
function(params) {
	params = params || {};
	var url = [];
	var i = 0;
	if (!params.relative) {
		url[i++] = params.protocol || location.protocol;
		url[i++] = "//";
		url[i++] = params.host || location.hostname;
		var port = Number(params.port || location.port);
		if (port && (port != 80)) {
			url[i++] =  ":";
			url[i++] = port;
		}
	}
	url[i++] = params.path || location.pathname;
	var qs = "";
	if (params.qsArgs) {
		qs = AjxStringUtil.queryStringSet(params.qsArgs, params.qsReset);
	} else {
		qs = params.qsReset ? "" : location.search;
	}
	url[i++] = qs;
	
	return url.join("");
};

AjxUtil.byNumber = function(a, b) {
	return Number(a) - Number(b);
};