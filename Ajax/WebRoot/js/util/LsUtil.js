/**
 * LsUtil - static class with some utility methods. This is where to
 * put things when no other class wants them.
 *
 * 12/3/2004 At this point, it only needs LsEnv to be loaded.
 */
function LsUtil () {
};

LsUtil.FLOAT_RE = /^[+\-]?((\d+(\.\d*)?)|((\d*\.)?\d+))([eE][+\-]?\d+)?$/;
LsUtil.NOTFLOAT_RE = /[^\d\.]/;
LsUtil.NOTINT_RE = /[^0-9]+/;
LsUtil.LIFETIME_FIELD = /^([0-9])+([dhms])?$/;
LsUtil.isSpecified 	= function(aThing) { return ((aThing !== void 0) && (aThing !== null)); };
LsUtil.isUndefined 	= function(aThing) { return (aThing === void 0); };
LsUtil.isNull 		= function(aThing) { return (aThing === null); };
LsUtil.isBoolean 	= function(aThing) { return (typeof(aThing) == 'boolean'); };
LsUtil.isString 	= function(aThing) { return (typeof(aThing) == 'string'); };
LsUtil.isNumber 	= function(aThing) { return (typeof(aThing) == 'number'); };
LsUtil.isObject 	= function(aThing) { return ((typeof(aThing) == 'object') && (aThing !== null)); };
LsUtil.isArray 		= function(aThing) { return LsUtil.isInstance(aThing, Array); };
LsUtil.isFunction 	= function(aThing) { return (typeof(aThing) == 'function'); };
LsUtil.isDate 		= function(aThing) { return LsUtil.isInstance(aThing, Date); };

LsUtil.isNumeric 	= function(aThing) { return (!isNaN(parseInt(aThing)) && LsUtil.FLOAT_RE.test(aThing) && !LsUtil.NOTFLOAT_RE.test(aThing)); };
LsUtil.isInteger	= function(aThing) { return (LsUtil.isNumeric(aThing) && !LsUtil.NOTINT_RE.test(aThing)); };
LsUtil.isNonNegativeInteger = function (aThing) {
	var retVal = (LsUtil.isNumeric(aThing) && LsUtil.isInteger(aThing) && (parseInt(aThing) >= 0) ); 
	return retVal;
};
LsUtil.isLifeTime = function (aThing) { return LsUtil.LIFETIME_FIELD.test(aThing); };

LsUtil.SIZE_GIGABYTES = "GB";
LsUtil.SIZE_MEGABYTES = "MB";
LsUtil.SIZE_KILOBYTES = "KB";
LsUtil.SIZE_BYTES = "B";

/**
 * Formats a size (in bytes) to the largest whole unit. For example,
 * LsUtil.formatSize(302132199) returns "288 MB".
 *
 * @param size      The size (in bytes) to be formatted.
 * @param round     True to round to nearest integer. Default is true.
 * @param fractions Number of fractional digits to display, if not rounding.
 *                  Trailing zeros after the decimal point are trimmed.
 */
LsUtil.formatSize = 
function(size, round, fractions) {
	if (round == null) round = true;
	if (fractions == null) fractions = 20; // max allowed for toFixed is 20

	var units = LsUtil.SIZE_BYTES;
	if (size >= 1073741824) {
		size /= 1073741824;
		units = LsUtil.SIZE_GIGABYTES;
	}
	else if (size >= 1048576) {
		size /= 1048576;
		units = LsUtil.SIZE_MEGABYTES;
	}
	else if (size > 1023) {
		size /= 1024;
		units = LsUtil.SIZE_KILOBYTES;
	}

	var formattedSize = round ? Math.round(size) : size.toFixed(fractions).replace(/\.?0+$/,"");
	var formattedUnits = ' '+units;
	
	return formattedSize + formattedUnits;
}

/**
 * Formats a size (in bytes) to a specific unit. Since the unit size is
 * known, the unit is not shown in the returned string. For example,
 * LsUtil.formatSizeForUnit(302132199, LsUtil.SIZE_MEGABYTES, false, 2) 
 * returns "288.13".
 *
 * @param size      The size (in bytes) to be formatted.
 * @param units     The unit of measure.
 * @param round     True to round to nearest integer. Default is true.
 * @param fractions Number of fractional digits to display, if not rounding.
 *                  Trailing zeros after the decimal point are trimmed.
 */
LsUtil.formatSizeForUnits = function(size, units, round, fractions) {
	if (units == null) units = LsUtil.SIZE_BYTES;
	if (round == null) round = true;
	if (fractions == null) fractions = 20; // max allowed for toFixed is 20

	switch (units) {
		case LsUtil.SIZE_GIGABYTES: { size /= 1073741824; break; }
		case LsUtil.SIZE_MEGABYTES: { size /= 1048576; break; }
		case LsUtil.SIZE_KILOBYTES: { size /= 1024; break; }
	}
	
	var formattedSize = round ? Math.round(size) : size.toFixed(fractions).replace(/\.?0+$/,"");
	return formattedSize;
}

/**
 * Performs the opposite of LsUtil.formatSize in that this function takes a 
 * formatted size.
 *
 * @param units Unit constant: "GB", "MB", "KB", "B". Must be specified 
 *              unless the formatted size ends with the size marker, in
 *				which case the size marker in the formattedSize param
 *				overrides this parameter.
 */
LsUtil.parseSize = function(formattedSize, units) {
	// NOTE: Take advantage of fact that parseFloat ignores bad chars
	//       after numbers
	var size = parseFloat(formattedSize.replace(/^\s*/,""));

	var marker = /[GMK]?B$/i;
	var result = marker.exec(formattedSize);
	if (result) {
		//alert("units: "+units+", result[0]: '"+result[0]+"'");
		units = result[0].toUpperCase();
	}
	
	switch (units) {
		case LsUtil.SIZE_GIGABYTES: size *= 1073741824; break;
		case LsUtil.SIZE_MEGABYTES: size *= 1048576; break; 
		case LsUtil.SIZE_KILOBYTES: size *= 1024; break;
	}
	
	//alert("LsUtil#parseSize: formattedSize="+formattedSize+", size="+size);
	return size;
}

LsUtil.isInstance = 
function(aThing, aClass) { 
	return !!(aThing && aThing.constructor && (aThing.constructor === aClass)); 
};

LsUtil.assert = function(aCondition, aMessage) {
	if (!aCondition && LsUtil.onassert) LsUtil.onassert(aMessage);
};

LsUtil.onassert = 
function(aMessage) {
	// Create an exception object and set the message
	var myException = new Object();
	myException.message = aMessage;
	
	// Compile a stack trace
	var myStack = new Array();
	if (LsEnv.isIE5_5up) {
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

LsUtil.NODE_REPEATS = new Object();
LsUtil.NODE_REPEATS["folder"]	= true;
LsUtil.NODE_REPEATS["search"]	= true;
LsUtil.NODE_REPEATS["tag"]		= true;
LsUtil.NODE_REPEATS["pref"]		= true;
LsUtil.NODE_REPEATS["attr"]		= true;
LsUtil.NODE_REPEATS["c"]		= true;
LsUtil.NODE_REPEATS["m"]		= true;
LsUtil.NODE_REPEATS["cn"]		= true;
LsUtil.NODE_REPEATS["e"]		= true;
LsUtil.NODE_REPEATS["a"]		= true;
LsUtil.NODE_REPEATS["mbx"]		= true;
//LsUtil.NODE_REPEATS["mp"]		= true; // only when parent is "mp"
// these really shouldn't repeat
LsUtil.NODE_REPEATS["prefs"]	= true;
LsUtil.NODE_REPEATS["attrs"]	= true;
LsUtil.NODE_REPEATS["tags"]	= true;

LsUtil.NODE_IS_ATTR = new Object();
LsUtil.NODE_IS_ATTR["authToken"]	= true;
LsUtil.NODE_IS_ATTR["lifetime"]		= true;
LsUtil.NODE_IS_ATTR["sessionId"]	= true;
LsUtil.NODE_IS_ATTR["name"]			= true;
LsUtil.NODE_IS_ATTR["quotaUsed"]	= true;
LsUtil.NODE_IS_ATTR["su"]			= true;
LsUtil.NODE_IS_ATTR["fr"]			= true;
LsUtil.NODE_IS_ATTR["mid"]			= true;
//LsUtil.NODE_IS_ATTR["content"]	= true; // only when parent is "note"

LsUtil.NODE_CONTENT = new Object();
LsUtil.NODE_CONTENT["pref"]	= true;
LsUtil.NODE_CONTENT["attr"]	= true;
LsUtil.NODE_CONTENT["a"]	= true;

// IE doesn't define Node type constants
LsUtil.ELEMENT_NODE	= 1;
LsUtil.TEXT_NODE	= 3;

LsUtil.xmlToJs =
function(node, omitName) {

	if (node.nodeType == LsUtil.TEXT_NODE)
		return ['"', node.data, '"'].join("");

	var name = node.name ? node.name : node.localName;
	if (node.nodeType == LsUtil.ELEMENT_NODE) {
		var text = omitName ? "{" : [name, ":{"].join("");
		var needComma = false;	
		if (node.attributes) {
			for (var i = 0; i < node.attributes.length; i++) {
				var attr = node.attributes[i];
				if (attr.name == "xmlns") continue;
				if (needComma) text += ",";
				var value = LsUtil.isNumeric(attr.value) ? attr.value : LsUtil.jsEncode(attr.value);
				text = [text, attr.name, ':', value].join("");
				needComma = true;
			}
		}
		if (node.hasChildNodes()) {
			var cnodes = new Object();
			var hasChild = false;
			for (var i = 0; i < node.childNodes.length; i++) {
				var child = node.childNodes[i];
				var cname = child.name ? child.name : child.localName;
				var isAttr = LsUtil.NODE_IS_ATTR[cname] || 
							 (name == "content" && parent.name == "note");
				if (isAttr) {
					if (needComma) text += ",";
					text = [text, cname, ':', LsUtil.jsEncode(child.textContent)].join("");
					needComma = true;
				} else {
					if (!cnodes[cname])
						cnodes[cname] = new Array();
					cnodes[cname].push(child);
					hasChild = true;
				}
			}
			if (hasChild && needComma) {text += ","; needComma = false;}
			for (var cname in cnodes) {
				if (needComma) {
					text += ",";
					needComma = false;
				}
				var repeats = LsUtil.NODE_REPEATS[cname] ||
							  (cname == "mp" && name == "mp");
				if (repeats) text += cname + ":[";
				var clist = cnodes[cname];
				for (var i = 0; i < clist.length; i++) {
					if (needComma) text += ",";
					text += LsUtil.xmlToJs(clist[i], repeats);
					needComma = true;
				}
				if (repeats) text += "]";
			}
		}
		text += "}";
	}

	return text;
}

LsUtil.JS_CHAR_ENCODINGS = [
	"\\u0000", "\\u0001", "\\u0002", "\\u0003", "\\u0004", "\\u0005", "\\u0006", "\\u0007",
	"\\b",     "\\t",     "\\n",     "\\u000B", "\\f",     "\\r",     "\\u000E", "\\u000F",
	"\\u0010", "\\u0011", "\\u0012", "\\u0013", "\\u0014", "\\u0015", "\\u0016", "\\u0017",
	"\\u0018", "\\u0019", "\\u001A", "\\u001B", "\\u001C", "\\u001D", "\\u001E", "\\u001F"
];

LsUtil.jsEncode =
function(string) {

	if (!string) return "\"\"";

	var text = '"';
	for (var i = 0; i < string.length; i++) {
		var c = string.charAt(i);
		switch (c) {
			case '\\': case '"': case '/':
				text += '\\' + c;
				break;
			default:
				var code = string.charCodeAt(i);
				text += (code < 32) ? LsUtil.JS_CHAR_ENCODINGS[code] : c;
		}
	}
	text += '"';
	return text;
}
