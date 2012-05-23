/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
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
 * Default constructor does nothing (static class).
 * @constructor
 * @class
 * This class provides static methods to perform miscellaneous string-related utility functions.
 *
 * @author Ross Dargahi
 * @author Roland Schemers
 * @author Conrad Damon
 */
AjxStringUtil = function() {};

AjxStringUtil.TRIM_RE = /^\s+|\s+$/g;
AjxStringUtil.COMPRESS_RE = /\s+/g;
AjxStringUtil.ELLIPSIS = " ... ";
AjxStringUtil.LIST_SEP = ", ";

AjxStringUtil.makeString =
function(val) {
	return val ? String(val) : "";
};

/**
 * Capitalizes the specified string by upper-casing the first character
 * and lower-casing the rest of the string.
 *
 * @param {string} str  The string to capitalize.
 */
AjxStringUtil.capitalize = function(str) {
	return str.length > 0 ? str.charAt(0).toUpperCase() + str.substr(1).toLowerCase() : "";
};

/**
 * Capitalizes all the words in the specified string by upper-casing the first
 * character of each word and lower-casing the rest of the word.
 *
 * @param {string} str  The string to capitalize.
 */
AjxStringUtil.capitalizeWords = function(str) {
    return AjxUtil.map(str.split(/\s+/g), AjxStringUtil.capitalize).join(" ");
};

/**
 * Converts the given text to mixed-case. The input text is one or more words
 * separated by spaces. The output is a single word in mixed (or camel) case.
 * 
 * @param {string}	text		text to convert
 * @param {string|RegEx}	sep		text separator (defaults to any space)
 * @param {boolean}	camel		if <code>true</code>, first character of result is lower-case
 * @return	{string}	the resulting string
 */
AjxStringUtil.toMixed =
function(text, sep, camel) {
	if (!text || (typeof text != "string")) { return ""; }
	sep = sep || /\s+/;
	var wds = text.split(sep);
	var newText = [];
	newText.push(camel ? wds[0].toLowerCase() : wds[0].substring(0, 1).toUpperCase() + wds[0].substring(1).toLowerCase());
	for (var i = 1; i < wds.length; i++) {
		newText.push(wds[i].substring(0, 1).toUpperCase() + wds[i].substring(1).toLowerCase());
	}
	return newText.join("");
};

/**
 * Converts the given mixed-case text to a string of one or more words
 * separated by spaces.
 *
 * @param {string} text The mixed-case text.
 * @param {string} sep  (Optional) The separator between words. Default
 *                      is a single space.
 */
AjxStringUtil.fromMixed = function(text, sep) {
    sep = ["$1", sep || " ", "$2"].join("");
    return AjxStringUtil.trim(text.replace(/([a-z])([A-Z]+)/g, sep));
};

/**
 * Removes white space from the beginning and end of a string, optionally compressing internal white space. By default, white
 * space is defined as a sequence of  Unicode whitespace characters (\s in regexes). Optionally, the user can define what
 * white space is by passing it as an argument.
 *
 * <p>TODO: add left/right options</p>
 *
 * @param {string}	str      	the string to trim
 * @param {boolean}	compress 	whether to compress internal white space to one space
 * @param {string}	space    	a string that represents a user definition of white space
 * @return	{string}	a trimmed string
 */
AjxStringUtil.trim =
function(str, compress, space) {

	if (!str) {return "";}

	var trim_re = AjxStringUtil.TRIM_RE;

	var compress_re = AjxStringUtil.COMPRESS_RE;
	if (space) {
		trim_re = new RegExp("^" + space + "+|" + space + "+$", "g");
		compress_re = new RegExp(space + "+", "g");
	} else {
		space = " ";
	}
	str = str.replace(trim_re, '');
	if (compress) {
		str = str.replace(compress_re, space);
	}

	return str;
};

/**
 * Returns the string repeated the given number of times.
 *
 * @param {string}	str		a string
 * @param {number}	num		number of times to repeat the string
 * @return	{string}	the string
 */
AjxStringUtil.repeat =
function(str, num) {
	var text = "";
	for (var i = 0; i < num; i++) {
		text += str;
	}
	return text;
};

/**
 * Gets the units from size string.
 * 
 * @param	{string}	sizeString	the size string
 * @return	{string}	the units
 */
AjxStringUtil.getUnitsFromSizeString =
function(sizeString) {
	var units = "px";
	if (typeof(sizeString) == "string") {
		var digitString = Number(parseInt(sizeString,10)).toString();
		if (sizeString.length > digitString.length) {
			units = sizeString.substr(digitString.length, (sizeString.length-digitString.length));
			if (!(units=="em" || units=="ex" || units=="px" || units=="in" || units=="cm" == units=="mm" || units=="pt" || units=="pc" || units=="%")) {
				units = "px";
			}
		}
	}
	return units;
};

/**
* Splits a string, ignoring delimiters that are in quotes or parentheses. Comma
* is the default split character, but the user can pass in a string of multiple
* delimiters. It can handle nested parentheses, but not nested quotes.
*
* <p>TODO: handle escaped quotes</p>
*
* @param {string} str	the string to split
* @param {string}	[dels]	an optional string of delimiter characters
* @return	{array}	an array of strings
*/
AjxStringUtil.split =
function(str, dels) {

	if (!str) {return [];}
	var i = 0;
	dels = dels ? dels : ',';
	var isDel = new Object();
	if (typeof dels == 'string') {
		isDel[dels] = 1;
	} else {
		for (i = 0; i < dels.length; i++) {
			isDel[dels[i]] = 1;
		}
	}

	var q = false;
	var p = 0;
	var start = 0;
	var chunk;
	var chunks = [];
	var j = 0;
	for (i = 0; i < str.length; i++) {
		var c = str.charAt(i);
		if (c == '"') {
			q = !q;
		} else if (c == '(') {
			p++;
		} else if (c == ')') {
			p--;
		} else if (isDel[c]) {
			if (!q && !p) {
				chunk = str.substring(start, i);
				chunks[j++] = chunk;
				start = i + 1;
			}
		}
	}
	chunk = str.substring(start, str.length);
	chunks[j++] = chunk;

	return chunks;
};

/**
 *
 * splits the line into words, keeping leading whitespace with each word
 *
 * @param line the text to split
 *
 * @return {array} the array of words
 */
AjxStringUtil.splitKeepLeadingWhitespace =
function(line) {

	var wordWithLeadingSpaces = /\s*\S+/g;
	var words = [];
	var result;
	while (result = wordWithLeadingSpaces.exec(line)) {
		var word = result[0];
		words.push(word);
	}
	return words;
};

/**
 * Wraps text to the given length and optionally quotes it. The level of quoting in the
 * source text is preserved based on the prefixes. Special lines such as email headers
 * always start a new line.
 *
 * @param {hash}	params	a hash of parameters
 * @param {string}      params.text 				the text to be wrapped
 * @param {number}      [params.len=80]				the desired line length of the wrapped text, defaults to 80
 * @param {string}      [params.pre]				an optional string to prepend to each line (useful for quoting)
 * @param {string}      [params.before]				text to prepend to final result
 * @param {string}      [params.after]				text to append to final result
 * @param {boolean}		[params.preserveReturns]	if true, don't combine small lines
 *
 * @return	{string}	the wrapped/quoted text
 */
AjxStringUtil.wordWrap =
function(params) {

	if (!(params && params.text)) { return ""; }

	var text = params.text;
	var before = params.before || "", after = params.after || "";

	// For HTML, just surround the content with the before and after, which is
	// typically a block-level element that puts a border on the left
	if (params.htmlMode) {
		return [before, text, after].join("");
	}

	var len = params.len || 80;
	var pre = params.pre || "";
	var eol = "\n";

	text = AjxStringUtil.trim(text, false, "[\t ]");
	text = text.replace(/\r\n/g, eol);
	var lines = text.split(eol);
	var words = [];

	// Divides lines into words. Each word is part of a hash that also has
	// the word's prefix, whether it's a paragraph break, and whether it's
	// special (cannot be wrapped into a previous line)
	for (var l = 0, llen = lines.length; l < llen; l++) {
		var line = lines[l];
		// get this line's prefix
		var m = line.match(/^([\s>\|]+)/);
		var prefix = m ? m[1] : "";
		if (prefix) {
			line = line.substr(prefix.length);
		}
		var wds = AjxStringUtil.splitKeepLeadingWhitespace(line);
		if (wds && wds[0] && wds[0].length) {
			var isSpecial = AjxStringUtil.MSG_SEP_RE.test(line) || AjxStringUtil.COLON_RE.test(line) ||
							AjxStringUtil.HDR_RE.test(line) || AjxStringUtil.SIG_RE.test(line);
			for (var w = 0, wlen = wds.length; w < wlen; w++) {
				var lastWord = params.preserveReturns && (w == wlen - 1);
				words.push({w:wds[w], p:prefix, special:(isSpecial && w == 0), lastWord:lastWord});
			}
		} else {
			words.push({para:true, p:prefix});	// paragraph marker
		}
	}

	// Take the array of words and put them back together. We break for a new line
	// when we hit the max line length, change prefixes, or hit a special word.
	var max = params.len || 72;
	var addPrefix = params.pre || "";
	var apl = addPrefix.length;
	var result = "", curLen = 0, wds = [], curP = null;
	for (var i = 0, len = words.length; i < len; i++) {
		var word = words[i];
		var w = word.w, p = word.p;
		var sp = wds.length ? 1 : 0;
		var pl = (curP === null) ? 0 : curP.length;
		if (word.para) {
			// paragraph break - output what we have, add a blank line
			if (wds.length) {
				result += addPrefix + (curP || "") + wds.join("").replace(/^ /,"") + eol;
			}
			result += addPrefix + p + eol;
			wds = [];
			curLen = 0;
			curP = null;
		} else if ((apl + pl + curLen + sp + w.length <= max) && (p == curP || curP === null) && !word.special) {
			// still room left on the current line, add the word
			wds.push(w);
			curLen += w.length;
			curP = p;
			if (word.lastWord && words[i + 1]) {
				words[i + 1].special = true;
			}
		} else {
			// output what we have and start a new line
			if (wds.length) {
				result += addPrefix + (curP || "") + wds.join("").replace(/^ /,"") + eol;
			}
			wds = [w];
			curLen = w.length;
			curP = p;
			if (word.lastWord && words[i + 1]) {
				words[i + 1].special = true;
			}
		}
	}

	// handle last line
	if (wds.length) {
		result += addPrefix + curP + wds.join("").replace(/^ /,"") + eol;
	}

	return [before, result, after].join("");
};

/**
 * Quotes text with the given quote character. For HTML, surrounds the text with the
 * given strings. Does no wrapping.
 *
 * @param {hash}	params	a hash of parameters
 * @param {string}      params.text 				the text to be wrapped
 * @param {string}      [params.pre]				prefix for quoting
 * @param {string}      [params.before]				text to prepend to final result
 * @param {string}      [params.after]				text to append to final result
 *
 * @return	{string}	the quoted text
 */
AjxStringUtil.quoteText =
function(params) {

	if (!(params && params.text)) { return ""; }

	var text = params.text;
	var before = params.before || "", after = params.after || "";

	// For HTML, just surround the content with the before and after, which is
	// typically a block-level element that puts a border on the left
	if (params.htmlMode || !params.pre) {
		return [before, text, after].join("");
	}

	var len = params.len || 80;
	var pre = params.pre || "";
	var eol = "\n";

	text = AjxStringUtil.trim(text);
	text = text.replace(/\n\r/g, eol);
	var lines = text.split(eol);
	var result = [];

	for (var l = 0, llen = lines.length; l < llen; l++) {
		var line = AjxStringUtil.trim(lines[l]);
		result.push(pre + line + eol);
	}

	return before + result.join("") + after;
};

AjxStringUtil.SHIFT_CHAR = { 48:')', 49:'!', 50:'@', 51:'#', 52:'$', 53:'%', 54:'^', 55:'&', 56:'*', 57:'(',
							59:':', 186:':', 187:'+', 188:'<', 189:'_', 190:'>', 191:'?', 192:'~',
							219:'{', 220:'|', 221:'}', 222:'"' };

/**
* Returns the character for the given key, taking the shift key into consideration.
*
* @param {number}	keycode	a numeric keycode (not a character code)
* @param {boolean}	shifted		whether the shift key is down
* @return	{char}	a character
*/
AjxStringUtil.shiftChar =
function(keycode, shifted) {
	return shifted ? AjxStringUtil.SHIFT_CHAR[keycode] || String.fromCharCode(keycode) : String.fromCharCode(keycode);
};

/**
 * Does a diff between two strings, returning the index of the first differing character.
 *
 * @param {string}	str1	a string
 * @param {string}	str2	another string
 * @return	{number}	the index at which they first differ
 */
AjxStringUtil.diffPoint =
function(str1, str2) {
	if (!(str1 && str2)) {
		return 0;
	}
	var len = Math.min(str1.length, str2.length);
	var i = 0;
	while (i < len && (str1.charAt(i) == str2.charAt(i))) {
		i++;
	}
	return i;
};

/*
* DEPRECATED
*
* Replaces variables in a string with values from a list. The variables are
* denoted by a '$' followed by a number, starting from 0. For example, a string
* of "Hello $0, meet $1" with a list of ["Harry", "Sally"] would result in the
* string "Hello Harry, meet Sally".
*
* @param str		the string to resolve
* @param values	 	an array of values to interpolate
* @returns			a string with the variables replaced
* 
* @deprecated
*/
AjxStringUtil.resolve =
function(str, values) {
	DBG.println(AjxDebug.DBG1, "Call to deprecated function AjxStringUtil.resolve");
	return AjxMessageFormat.format(str, values);
};

/**
 * Encodes a complete URL. Leaves delimiters alone.
 *
 * @param {string}	str	the string to encode
 * @return	{string}	the encoded string
 */
AjxStringUtil.urlEncode =
function(str) {
	if (!str) return "";
	var func = window.encodeURL || window.encodeURI;
	return func(str);
};

/**
 * Encodes a string as if it were a <em>part</em> of a URL. The
 * difference between this function and {@link AjxStringUtil.urlEncode}
 * is that this will also encode the following delimiters:
 *
 * <pre>
 *  			: / ? & =
 * </pre>
 * 
 * @param	{string}	str		the string to encode
 * @return	{string}	the resulting string
 */
AjxStringUtil.urlComponentEncode =
function(str) {
	if (!str) return "";
	var func = window.encodeURLComponent || window.encodeURIComponent;
	return func(str);
};

/**
 * Decodes a complete URL.
 *
 * @param {string}	str	the string to decode
 * @return	{string}	the decoded string
 */
AjxStringUtil.urlDecode =
function(str) {
	if (!str) return "";
	var func = window.decodeURL || window.decodeURI;
	return func(str);
};

/**
 * Decodes a string as if it were a <em>part</em> of a URL. Falls back
 * to unescape() if necessary.
 * 
 * @param	{string}	str		the string to decode
 * @return	{string}	the decoded string
 */
AjxStringUtil.urlComponentDecode =
function(str) {
	if (!str) return "";
	var func = window.decodeURLComponent || window.decodeURIComponent;
	var result;
	try {
		result = func(str);
	} catch(e) {
		result = unescape(str);
	}

	return result || str;
};

AjxStringUtil.ENCODE_MAP = { '>' : '&gt;', '<' : '&lt;', '&' : '&amp;' };

/**
 * HTML-encodes a string.
 *
 * @param {string}	str	the string to encode
 * @param	{boolean}	includeSpaces		if <code>true</code>, to include encoding spaces
 * @return	{string}	the encoded string
 */
AjxStringUtil.htmlEncode =
function(str, includeSpaces) {

	if (!str) {return "";}
	if (typeof(str) != "string") {
		str = str.toString ? str.toString() : "";
	}

	if (!AjxEnv.isSafari || AjxEnv.isSafariNightly) {
		if (includeSpaces) {
			return str.replace(/[<>&]/g, function(htmlChar) { return AjxStringUtil.ENCODE_MAP[htmlChar]; }).replace(/  /g, ' &nbsp;');
		} else {
			return str.replace(/[<>&]/g, function(htmlChar) { return AjxStringUtil.ENCODE_MAP[htmlChar]; });
		}
	} else {
		if (includeSpaces) {
			return str.replace(/[&]/g, '&amp;').replace(/  /g, ' &nbsp;').replace(/[<]/g, '&lt;').replace(/[>]/g, '&gt;');
		} else {
			return str.replace(/[&]/g, '&amp;').replace(/[<]/g, '&lt;').replace(/[>]/g, '&gt;');
		}
	}
};

/**
 * encode quotes for using in inline JS code, so the text does not end a quoted param prematurely.
 * @param str
 */
AjxStringUtil.encodeQuotes =
function(str) {
	return str.replace(/"/g, '&quot;').replace(/'/g, "&#39;");
};


/**
 * Decodes the string.
 * 
 * @param	{string}	str		the string to decode
 * @param	{boolean}	decodeSpaces	if <code>true</code>, decode spaces
 * @return	{string}	the string
 */
AjxStringUtil.htmlDecode =
function(str, decodeSpaces) {
	 
	 if(decodeSpaces)
	 	str = str.replace(/&nbsp;/g," ");
	 	
     return str.replace(/&amp;/g, "&").replace(/&lt;/g, "<").replace(/&gt;/g, ">");
};

/**
 * Removes HTML tags from the given string.
 * 
 * @param {string}	str			text from which to strip tags
 * @param {boolean}	removeContent	if <code>true</code>, also remove content within tags
 * @return	{string}	the resulting HTML string
 */
AjxStringUtil.stripTags =
function(str, removeContent) {
	if (!str) { return ""; }
	if (removeContent) {
		str = str.replace(/(<(\w+)[^>]*>).*(<\/\2[^>]*>)/, "$1$3");
	}
	return str.replace(/<\/?[^>]+>/gi, '');
};

/**
 * Converts the string to HTML.
 * 
 * @param	{string}	str		the string
 * @return	{string}	the resulting string
 */
AjxStringUtil.convertToHtml =
function(str, quotePrefix) {
	if (!str) {return "";}

	if (quotePrefix) {
		// Convert a section of lines prefixed with > or |
		// to a section encapsuled in <blockquote> tags
		var prefix_re = /^(>|\|\s+)/;
		var lines = str.split(/\r?\n/);
		var level = 0;
		for (var i=0; i<lines.length; i++) {
			var line = lines[i];
			if (line.length > 0) {
				var lineLevel = 0;
				while (line.match(prefix_re)) { // Remove prefixes while counting how many there are on the line
					line = line.replace(prefix_re,"");
					lineLevel++;
				}
				while (lineLevel > level) { // If the lineLevel has changed since the last line, add blockquote start or end tags, and adjust level accordingly
					line = "<blockquote>" + line;
					level++;
				}
				while (lineLevel < level) {
					line = line + "</blockquote>";
					level--;
				}
			}
			lines[i] = line;
		}
		while (level > 0) {
			lines.push("</blockquote>");
			level--;
		}

		str = lines.join("\n");
		str = str.replace(/&/mg, "&amp;");
		
		str = str.replace(/<(?!\/?blockquote)/mg, "&lt;"); // Replace "<" only if it is not followed by "blockquote"

		str = str.replace(/(\/?blockquote)?>/mg, function($0, $1) { // Replace ">" only if it is not preceded by "blockquote"
			return $1 ? $0 : '&gt;';
		});

	} else {
		str = str
			.replace(/&/mg, "&amp;")
			.replace(/</mg, "&lt;")
			.replace(/>/mg, "&gt;")
	}
		

	str = str
		.replace(/  /mg, " &nbsp;")
		.replace(/^ /mg, "&nbsp;")
		.replace(/\t/mg, "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;")
		.replace(/\r?\n/mg, "<br>");
	return str;
};

AjxStringUtil.SPACE_ENCODE_MAP = { ' ' : '&nbsp;', '>' : '&gt;', '<' : '&lt;', '&' : '&amp;' , '\n': '<br>'};

/**
 * HTML-encodes a string.
 *
 * @param {string}	str	the string to encode
 * 
 * @private
 */
AjxStringUtil.htmlEncodeSpace =
function(str) {
	if (!str) { return ""; }
	return str.replace(/[&]/g, '&amp;').replace(/ /g, '&nbsp;').replace(/[<]/g, '&lt;').replace(/[>]/g, '&gt;');
};

/**
 * Encode
 * @param base {string} Ruby base.
 * @param text {string} Ruby text (aka furigana).
 */
AjxStringUtil.htmlRubyEncode = function(base, text) {
    if (base && text) {
        return [
            "<ruby>",
                "<rb>",AjxStringUtil.htmlEncode(base),"</rb> ",
                "<rp>(</rp><rt>",AjxStringUtil.htmlEncode(text),"</rt><rp>)</rp>",
            "</ruby>"
        ].join("");
    }
    return AjxStringUtil.htmlEncode(base || text || "");
};

// this function makes sure a leading space is preservered, takes care of tabs,
// then finally takes replaces newlines with <br>'s
AjxStringUtil.nl2br =
function(str) {
	if (!str) return "";
	return str.replace(/^ /mg, "&nbsp;").
		// replace(/\t/g, "<pre style='display:inline;'>\t</pre>").
		// replace(/\t/mg, "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;").
		replace(/\t/mg, "<span style='white-space:pre'>\t</span>").
		replace(/\n/g, "<br>");
};

AjxStringUtil.xmlEncode =
function(str) {
	if (str) {
		// bug fix #8779 - safari barfs if "str" is not a String type
		str = "" + str;
		return str.replace(/&/g,"&amp;").replace(/</g,"&lt;");
	}
	return "";
};

AjxStringUtil.xmlDecode =
function(str) {
	return str ? str.replace(/&amp;/g,"&").replace(/&lt;/g,"<") : "";
};

AjxStringUtil.xmlAttrEncode =
function(str) {
	return str ? str.replace(/&/g,"&amp;").replace(/</g,"&lt;").replace(/\x22/g, '&quot;').replace(/\x27/g,"&apos;") : "";
};

AjxStringUtil.xmlAttrDecode =
function(str) {
	return str ? str.replace(/&amp;/g,"&").replace(/&lt;/g,"<").replace(/&quot;/g, '"').replace(/&apos;/g,"'") : "";
};

AjxStringUtil.__RE_META = { " ":" ", "\n":"\\n", "\r":"\\r", "\t":"\\t" };
AjxStringUtil.__reMetaEscape = function($0, $1) {
	return AjxStringUtil.__RE_META[$1] || "\\"+$1;
};
AjxStringUtil.regExEscape =
function(str) {
	return str.replace(/(\W)/g, AjxStringUtil.__reMetaEscape);
};

AjxStringUtil._calcDIV = null; // used by 'clip()' and 'wrap()' functions

AjxStringUtil.calcDIV =
function() {
	if (AjxStringUtil._calcDIV == null) {
		AjxStringUtil._calcDIV = document.createElement("div");
		AjxStringUtil._calcDIV.style.zIndex = 0;
		AjxStringUtil._calcDIV.style.position = DwtControl.ABSOLUTE_STYLE;
		AjxStringUtil._calcDIV.style.visibility = "hidden";
		document.body.appendChild(AjxStringUtil._calcDIV);
	}
	return AjxStringUtil._calcDIV;
};

/**
 * Clips a string at "pixelWidth" using using "className" on hidden 'AjxStringUtil._calcDIV'.
 * Returns "origString" with "..." appended if clipped.
 *
 * NOTE: The same CSS style ("className") must be assigned to both the intended
 * display area and the hidden 'AjxStringUtil._calcDIV'.  "className" is
 * optional; if supplied, it will be assigned to 'AjxStringUtil._calcDIV' to
 * handle different CSS styles ("className"s) on same page.
 *
 * NOTE2: MSIE Benchmark - clipping an average of 17 characters each over 190
 * iterations averaged 27ms each (5.1 seconds total for 190)
 * 
 * @private
 */
AjxStringUtil.clip =
function(origString, pixelWidth, className) {
	var calcDIV = AjxStringUtil.calcDIV();
	if (arguments.length == 3) calcDIV.className = className;
	//calcDIV.innerHTML = "<div>" + origString + "</div>"; // prevents screen flash in IE?
	calcDIV.innerHTML = origString;
	if (calcDIV.offsetWidth <= pixelWidth) return origString;

	for (var i=origString.length-1; i>0; i--) {
		var newString = origString.substr(0,i);
		calcDIV.innerHTML = newString + AjxStringUtil.ELLIPSIS;
		if (calcDIV.offsetWidth <= pixelWidth) return newString + AjxStringUtil.ELLIPSIS;
	}
	return origString;
};

AjxStringUtil.clipByLength =
function(str,clipLen) {
	var len = str.length;
	return (len <= clipLen)
		?  str
		: [str.substr(0,clipLen/2), '...', str.substring(len - ((clipLen/2) - 3),len)].join("");
};

/**
 * Forces a string to wrap at "pixelWidth" using "className" on hidden 'AjxStringUtil._calcDIV'.
 * Returns "origString" with "&lt;br&gt;" tags inserted to force wrapping.
 * Breaks string on embedded space characters, EOL ("/n") and "&lt;br&gt;" tags when possible.
 *
 * @returns		"origString" with "&lt;br&gt;" tags inserted to force wrapping.
 * 
 * @private
 */
AjxStringUtil.wrap =
function(origString, pixelWidth, className) {
	var calcDIV = AjxStringUtil.calcDIV();
	if (arguments.length == 3) calcDIV.className = className;

	var newString = "";
	var newLine = "";
	var textRows = origString.split("/n");
	for (var trCount = 0; trCount < textRows.length; trCount++) {
		if (trCount != 0) {
			newString += newLine + "<br>";
			newLine = "";
		}
		htmlRows = textRows[trCount].split("<br>");
		for (var hrCount=0; hrCount<htmlRows.length; hrCount++) {
			if (hrCount != 0) {
				newString += newLine + "<br>";
				newLine = "";
			}
			words = htmlRows[hrCount].split(" ");
			var wCount=0;
			while (wCount<words.length) {
				calcDIV.innerHTML = newLine + " " + words[wCount];
				var newLinePixels = calcDIV.offsetWidth;
				if (newLinePixels > pixelWidth) {
					// whole "words[wCount]" won't fit on current "newLine" - insert line break, avoid incrementing "wCount"
					calcDIV.innerHTML = words[wCount];
					newLinePixels = newLinePixels - calcDIV.offsetWidth;
					if ( (newLinePixels >= pixelWidth) || (calcDIV.offsetWidth <= pixelWidth) ) {
						// either a) excess caused by <space> character or b) will fit completely on next line
						// so just break without incrementing "wCount" and append next time
						newString += newLine + "<br>";
						newLine = "";
					}
					else { // must break "words[wCount]"
						var keepLooping = true;
						var atPos = 0;
						while (keepLooping) {
							atPos++;
							calcDIV.innerHTML = newLine + " " + words[wCount].substring(0,atPos);
							keepLooping = (calcDIV.offsetWidth <= pixelWidth);
						}
						atPos--;
						newString += newLine + words[wCount].substring(0,atPos) + "<br>";
						words[wCount] = words[wCount].substr(atPos);
						newLine = "";
					}
				} else { // doesn't exceed pixelWidth, append to "newLine" and increment "wCount"
					newLine += " " + words[wCount];
					wCount++;
				}
			}
		}
	}
	newString += newLine;
	return newString;
};

// Regexes for finding stuff in msg content
AjxStringUtil.MSG_SEP_RE = new RegExp("^\\s*--+\\s*(" + AjxMsg.origMsg + "|" + AjxMsg.forwardedMessage + ")\\s*--+", "i");
AjxStringUtil.SIG_RE = /^(- ?-+)|(__+)\r?$/;
AjxStringUtil.SPLIT_RE = /\r\n|\r|\n/;
AjxStringUtil.HDR_RE = /^\s*\w+:/;
AjxStringUtil.COLON_RE = /\S+:$/;
AjxStringUtil.HTML_QUOTE_COLOR = "rgb(16, 16, 255)";
AjxStringUtil.HTML_QUOTE_STYLE = "color:#000;font-weight:normal;font-style:normal;text-decoration:none;font-family:Helvetica,Arial,sans-serif;font-size:12pt;";


// Converts a HTML document represented by a DOM tree to text
// XXX: There has got to be a better way of doing this!
AjxStringUtil._NO_LIST = 0;
AjxStringUtil._ORDERED_LIST = 1;
AjxStringUtil._UNORDERED_LIST = 2;
AjxStringUtil._INDENT = "    ";
AjxStringUtil._NON_WHITESPACE = /\S+/;
AjxStringUtil._LF = /\n/;

AjxStringUtil.convertHtml2Text =
function(domRoot, convertor, onlyOneNewLinePerP) {

	if (!domRoot) { return ""; }

	if (convertor && AjxUtil.isFunction(convertor._before)) {
		domRoot = convertor._before(domRoot);
	}

	if (typeof domRoot == "string") {
		var domNode = document.createElement("SPAN");
		domNode.innerHTML = domRoot;
		domRoot = domNode;
	}
	var text = [];
	var idx = 0;
	var ctxt = {};
	AjxStringUtil._traverse(domRoot, text, idx, AjxStringUtil._NO_LIST, 0, 0, ctxt, convertor, onlyOneNewLinePerP);

	var result = text.join("");

	if (convertor && AjxUtil.isFunction(convertor._after)) {
		result = convertor._after(result);
	}

	return result;
};

AjxStringUtil._traverse =
function(el, text, idx, listType, listLevel, bulletNum, ctxt, convertor, onlyOneNewLinePerP) {

	var nodeName = el.nodeName.toLowerCase();

	var result = null;
	if (convertor && convertor[nodeName]) {
		result = convertor[nodeName](el,ctxt);
	}

	if (result != null) {
		text[idx++] = result;
	} else if (nodeName == "#text") {
		if (el.nodeValue.search(AjxStringUtil._NON_WHITESPACE) != -1) {
			if (ctxt.lastNode == "ol" || ctxt.lastNode == "ul") {
				text[idx++] = "\n";
			}
			if (ctxt.isPreformatted) {
				text[idx++] = AjxStringUtil.trim(el.nodeValue) + " ";
			} else {
				text[idx++] = AjxStringUtil.trim(el.nodeValue.replace(AjxStringUtil._LF, " "), true) + " ";
			}
		}
	} else if (nodeName == "p") {
		text[idx++] = onlyOneNewLinePerP ? "\n" : "\n\n";
	} else if (listType == AjxStringUtil._NO_LIST && (nodeName == "br" || nodeName == "hr")) {
		text[idx++] = "\n";
	} else if (nodeName == "ol" || nodeName == "ul") {
		text[idx++] = "\n";
		if (el.parentNode.nodeName.toLowerCase() != "li" && ctxt.lastNode != "br" && ctxt.lastNode != "hr") {
			text[idx++] = "\n";
		}
		listType = (nodeName == "ol") ? AjxStringUtil._ORDERED_LIST : AjxStringUtil._UNORDERED_LIST;
		listLevel++;
		bulletNum = 0;
	} else if (nodeName == "li") {
		for (var i = 0; i < listLevel; i++) {
			text[idx++] = AjxStringUtil._INDENT;
		}
		if (listType == AjxStringUtil._ORDERED_LIST) {
			text[idx++] = bulletNum + ". ";
		} else {
			text[idx++] = "\u002A "; // TODO ZmMsg.bullet
		}
	} else if (nodeName == "tr" && el.parentNode.firstChild != el) {
		text[idx++] = "\n";
	} else if (nodeName == "td" && el.parentNode.firstChild != el) {
		text[idx++] = "\t";
	} else if (nodeName == "div") {
        if(el.parentNode && !( el.parentNode.nodeName.toLowerCase() === "body" && el.parentNode.firstChild === el ) ){//No \n for body element first Child if it is div
            text[idx++] = "\n";
        }
	} else if (nodeName == "blockquote") {
		text[idx++] = "\n\n";
	} else if (nodeName == "pre") {
		ctxt.isPreformatted = true;
	} else if (nodeName == "#comment" ||
			   nodeName == "script" ||
			   nodeName == "select" ||
			   nodeName == "style") {
		return idx;
	}

	var childNodes = el.childNodes;
	var len = childNodes.length;
	for (var i = 0; i < len; i++) {
		var tmp = childNodes[i];
		if (tmp.nodeType == 1 && tmp.tagName.toLowerCase() == "li") {
			bulletNum++;
		}
		idx = AjxStringUtil._traverse(tmp, text, idx, listType, listLevel, bulletNum, ctxt, convertor, onlyOneNewLinePerP);
	}

	if (convertor && convertor["/"+nodeName]) {
		text[idx++] = convertor["/"+nodeName](el);
	}

	if (nodeName == "h1" || nodeName == "h2" || nodeName == "h3" || nodeName == "h4"
		|| nodeName == "h5" || nodeName == "h6") {
			text[idx++] = "\n";
			ctxt.list = false;
	} else if (nodeName == "pre") {
		ctxt.isPreformatted = false;
	} else if (nodeName == "li") {
		if (!ctxt.list) {
			text[idx++] = "\n";
		}
		ctxt.list = false;
	} else if (nodeName == "ol" || nodeName == "ul") {
		ctxt.list = true;
	} else if (nodeName != "#text") {
		ctxt.list = false;
	}

	ctxt.lastNode = nodeName;
	return idx;
};

/**
 * Sets the given name/value pairs into the given query string. Args that appear
 * in both will get the new value. The order of args in the returned query string
 * is indeterminate.
 *
 * @param args		[hash]		name/value pairs to add to query string
 * @param qsReset	[boolean]	if true, start with empty query string
 * 
 * @private
 */
AjxStringUtil.queryStringSet =
function(args, qsReset) {
	var qs = qsReset ? "" : location.search;
	if (qs.indexOf("?") == 0) {
		qs = qs.substr(1);
	}
	var qsArgs = qs.split("&");
	var newArgs = {};
	for (var i = 0; i < qsArgs.length; i++) {
		var f = qsArgs[i].split("=");
		newArgs[f[0]] = f[1];
	}
	for (var name in args) {
		newArgs[name] = args[name];
	}
	var pairs = [];
	var i = 0;
	for (var name in newArgs) {
		if (name) {
			pairs[i++] = [name, newArgs[name]].join("=");
		}
	}

	return "?" + pairs.join("&");
};

/**
 * Removes the given arg from the query string.
 *
 * @param {String}	qs	a query string
 * @param {String}	name	the arg name
 * 
 * @return	{String}	the resulting query string
 */
AjxStringUtil.queryStringRemove =
function(qs, name) {
	qs = qs ? qs : "";
	if (qs.indexOf("?") == 0) {
		qs = qs.substr(1);
	}
	var pairs = qs.split("&");
	var pairs1 = [];
	for (var i = 0; i < pairs.length; i++) {
		if (pairs[i].indexOf(name) != 0) {
			pairs1.push(pairs[i]);
		}
	}

	return "?" + pairs1.join("&");
};

/**
 * Returns the given object/primitive as a string.
 *
 * @param {primitive|Object}	o		an object or primitive
 * @return	{String}	the string
 */
AjxStringUtil.getAsString =
function(o) {
	return !o ? "" : (typeof(o) == 'object') ? o.toString() : o;
};

AjxStringUtil.isWhitespace = 
function(str) {
	return (str.charCodeAt(0) <= 32);
};

AjxStringUtil.isDigit = 
function(str) {
	var charCode = str.charCodeAt(0);
	return (charCode >= 48 && charCode <= 57);
};

AjxStringUtil.compareRight = 
function(a,b) {
	var bias = 0;
	var idxa = 0;
	var idxb = 0;
	var ca;
	var cb;

	for (; (idxa < a.length || idxb < b.length); idxa++, idxb++) {
		ca = a.charAt(idxa);
		cb = b.charAt(idxb);

		if (!AjxStringUtil.isDigit(ca) &&
			!AjxStringUtil.isDigit(cb))
		{
			return bias;
		}
		else if (!AjxStringUtil.isDigit(ca))
		{
			return -1;
		}
		else if (!AjxStringUtil.isDigit(cb))
		{
			return +1;
		}
		else if (ca < cb)
		{
			if (bias == 0) bias = -1;
		}
		else if (ca > cb)
		{
			if (bias == 0) bias = +1;
		}
	}
};

AjxStringUtil.natCompare = 
function(a, b) {
	var idxa = 0, idxb = 0;
	var nza = 0, nzb = 0;
	var ca, cb;

	while (idxa < a.length || idxb < b.length)
	{
		// number of zeroes leading the last number compared
		nza = nzb = 0;

		ca = a.charAt(idxa);
		cb = b.charAt(idxb);

		// ignore overleading spaces/zeros and move the index accordingly
		while (AjxStringUtil.isWhitespace(ca) || ca =='0') {
			nza = (ca == '0') ? (nza+1) : 0;
			ca = a.charAt(++idxa);
		}
		while (AjxStringUtil.isWhitespace(cb) || cb == '0') {
			nzb = (cb == '0') ? (nzb+1) : 0;
			cb = b.charAt(++idxb);
		}

		// current index points to digit in both str
		if (AjxStringUtil.isDigit(ca) && AjxStringUtil.isDigit(cb)) {
			var result = AjxStringUtil.compareRight(a.substring(idxa), b.substring(idxb));
			if (result && result!=0) {
				return result;
			}
		}

		if (ca == 0 && cb == 0) {
			return nza - nzb;
		}

		if (ca < cb) {
			return -1;
		} else if (ca > cb) {
			return +1;
		}

		++idxa; ++idxb;
	}
};

AjxStringUtil.clipFile =
function(fileName, limit) {
	var index = fileName.lastIndexOf('.');
	var len = index ? (index + 1) : fileName.length;

	if (len <= limit) {
		return fileName;
	} else {
		var fName = fileName.substr(0, index);
		var ext = fileName.substr(index+1, fileName.length-1);

		return [
			fName.substr(0, limit/2),
			'...',
			fName.substring(len - ((limit/2) - 3), len),
			'.', (ext ? ext : '')  // file extension
		].join("")
	}
};


AjxStringUtil.URL_PARSE_RE = new RegExp("^(?:([^:/?#.]+):)?(?://)?(([^:/?#]*)(?::(\\d*))?)?((/(?:[^?#](?![^?#/]*\\.[^?#/.]+(?:[\\?#]|$)))*/?)?([^?#/]*))?(?:\\?([^#]*))?(?:#(.*))?");

AjxStringUtil.parseURL = 
function(sourceUri) {

	var names = ["source","protocol","authority","domain","port","path","directoryPath","fileName","query","anchor"];
	var parts = AjxStringUtil.URL_PARSE_RE.exec(sourceUri);
	var uri = {};

	for (var i = 0; i < names.length; i++) {
		uri[names[i]] = (parts[i] ? parts[i] : "");
	}

	if (uri.directoryPath.length > 0) {
		uri.directoryPath = uri.directoryPath.replace(/\/?$/, "/");
	}

	return uri;
};

/**
 * Parse the query string (part after the "?") and return it as a hash of key/value pairs.
 * 
 * @param	{String}	sourceUri		the source query string
 * @return	{Hash}	a hash of query string params
 */
AjxStringUtil.parseQueryString =
function(sourceUri) {

	var location = sourceUri || ("" + window.location);
	var idx = location.indexOf("?");
	if (idx == -1) { return null; }
	var qs = location.substring(idx + 1).replace(/#.*$/, '');
	var list = qs.split("&");
	var params = {};
	for (var i = 0; i < list.length; i++) {
		var pair = list[i].split("=");
		params[pair[0]] = pair[1];
	}
	return params;
};

/**
 * Pretty-prints a JS object. Preferred over JSON.stringify for the debug-related dumping
 * of an object for several reasons:
 * 		- doesn't have an enclosing object, which shifts everything over one level
 * 		- doesn't put quotes around keys
 * 		- shows indexes for arrays (downside is that prevents output from being eval-able)
 * 
 * @param obj
 * @param recurse
 * @param showFuncs
 * @param omit
 */
AjxStringUtil.prettyPrint =
function(obj, recurse, showFuncs, omit) {

	AjxStringUtil._visited = new AjxVector();
	var text = AjxStringUtil._prettyPrint(obj, recurse, showFuncs, omit);
	AjxStringUtil._visited = null;

	return text;
};

AjxStringUtil._visited = null;

AjxStringUtil._prettyPrint =
function(obj, recurse, showFuncs, omit) {

	var indentLevel = 0;
	var showBraces = false;
	var stopRecursion = false;
	if (arguments.length > 4) {
		indentLevel = arguments[4];
		showBraces = arguments[5];
		stopRecursion = arguments[6];
	}

	if (AjxUtil.isObject(obj)) {
		var objStr = obj.toString ? obj.toString() : "";
		if (omit && objStr && omit[objStr]) {
			return "[" + objStr + "]";
		}
		if (AjxStringUtil._visited.contains(obj)) {
			return "[visited object]";
		} else {
			AjxStringUtil._visited.add(obj);
		}
	}

	var indent = AjxStringUtil.repeat(" ", indentLevel);
	var text = "";

	if (obj === undefined) {
		text += "[undefined]";
	} else if (obj === null) {
		text += "[null]";
	} else if (AjxUtil.isBoolean(obj)) {
		text += obj ? "true" : "false";
	} else if (AjxUtil.isString(obj)) {
		text += '"' + AjxStringUtil._escapeForHTML(obj) + '"';
	} else if (AjxUtil.isNumber(obj)) {
		text += obj;
	} else if (AjxUtil.isObject(obj)) {
		var isArray = AjxUtil.isArray(obj) || AjxUtil.isArray1(obj);
		if (stopRecursion) {
			text += isArray ? "[Array]" : obj.toString();
		} else {
			stopRecursion = !recurse;
			var keys = new Array();
			for (var i in obj) {
                if (obj.hasOwnProperty(i)) {
                    keys.push(i);
                }
			}

			if (isArray) {
				keys.sort(function(a,b) {return a - b;});
			} else {
				keys.sort();
			}

			if (showBraces) {
				text += isArray ? "[" : "{";
			}
			var len = keys.length;
			for (var i = 0; i < len; i++) {
				var key = keys[i];
				var nextObj = obj[key];
				var value = null;
				// For dumping events, and dom elements, though I may not want to
				// traverse the node, I do want to know what the attribute is.
				if (nextObj == window || nextObj == document || (!AjxEnv.isIE && nextObj instanceof Node)){
					value = nextObj.toString();
				}
				if ((typeof(nextObj) == "function")) {
					if (showFuncs) {
						value = "[function]";
					} else {
						continue;
					}
				}

				if (i > 0) {
					text += ",";
				}
				text += "\n" + indent;
                var keyString;
                if (isArray) {
                    keyString = "// [" + key + "]:\n" + indent;
                } else {
                    keyString = key + ": ";
                }
				if (omit && omit[key]) {
					text += keyString + "[" + key + "]";
				} else if (value != null) {
					text += keyString + value;
				} else {
					text += keyString + AjxStringUtil._prettyPrint(nextObj, recurse, showFuncs, omit, indentLevel + 2, true, stopRecursion);
				}
			}
			if (i > 0) {
				text += "\n" + AjxStringUtil.repeat(" ", indentLevel - 1);
			}
			if (showBraces) {
				text += isArray ? "]" : "}";
			}
		}
	}
	return text;
};

AjxStringUtil._escapeForHTML =
function(str){

	if (typeof(str) != 'string') { return str; }

	var s = str;
	s = s.replace(/\&/g, '&amp;');
	s = s.replace(/\</g, '&lt;');
	s = s.replace(/\>/g, '&gt;');
	s = s.replace(/\"/g, '&quot;');
	s = s.replace(/\xA0/g, '&nbsp;');

	return s;
};

// hidden SPANs for measuring regular and bold strings
AjxStringUtil._testSpan;
AjxStringUtil._testSpanBold;

// cached string measurements
AjxStringUtil.WIDTH			= {};		// regular strings
AjxStringUtil.WIDTH_BOLD	= {};		// bold strings
AjxStringUtil.MAX_CACHE		= 1000;		// max total number of cached strings
AjxStringUtil._cacheSize	= 0;		// current number of cached strings

/**
 * Returns the width in pixels of the given string.
 *
 * @param {string}	str		string to measure
 * @param {boolean}	bold	if true, string should be measured in bold font
 * @param {string|number}   font size to measure string in. If unset, use default font size
 */
AjxStringUtil.getWidth =
function(str, bold, fontSize) {

	if (!AjxStringUtil._testSpan) {
		var span1 = AjxStringUtil._testSpan = document.createElement("SPAN");
		var span2 = AjxStringUtil._testSpanBold = document.createElement("SPAN");
		span1.style.position = span2.style.position = Dwt.ABSOLUTE_STYLE;
		var shellEl = DwtShell.getShell(window).getHtmlElement();
		shellEl.appendChild(span1);
		shellEl.appendChild(span2);
		Dwt.setLocation(span1, Dwt.LOC_NOWHERE, Dwt.LOC_NOWHERE);
		Dwt.setLocation(span2, Dwt.LOC_NOWHERE, Dwt.LOC_NOWHERE);
		span2.style.fontWeight = "bold";
	}

	if (AjxUtil.isString(fontSize)) {
		fontSize = fontSize.replace(/px$/,"");
	}
	var sz = "" + (fontSize || 0); // 0 means "default";
	
	var cache = bold ? AjxStringUtil.WIDTH_BOLD : AjxStringUtil.WIDTH;
	if (cache[str] && cache[str][sz]) {
		return cache[str][sz];
	}

	if (AjxStringUtil._cacheSize >= AjxStringUtil.MAX_CACHE) {
		AjxStringUtil.WIDTH = {};
		AjxStringUtil.WIDTH_BOLD = {};
		AjxStringUtil._cacheSize = 0;
	}

	var span = bold ? AjxStringUtil._testSpanBold : AjxStringUtil._testSpan;
	span.innerHTML = str;
	span.style.fontSize = fontSize ? (fontSize+"px") : null;

	if (!cache[str]) {
		cache[str] = {};
	}

	var w = cache[str][sz] = Dwt.getSize(span).x;
	AjxStringUtil._cacheSize++;

	return w;
};

/**
 * correct the cross domain reference in passed url content
 * eg: http://<ipaddress>/ url might have rest url page which points to http://<server name>/ pages
 *
 */
AjxStringUtil.fixCrossDomainReference =
function(url, restUrlAuthority) {
	var urlParts = AjxStringUtil.parseURL(url);
	if (urlParts.authority == window.location.host) {
		return url;
	}

	if ((restUrlAuthority && url.indexOf(restUrlAuthority) >=0) || !restUrlAuthority) {
		var oldRef = urlParts.protocol + "://" + urlParts.authority;
		var newRef = window.location.protocol + "//" + window.location.host;
		url = url.replace(oldRef, newRef);
	}
	return url;
};


AjxStringUtil._dummyDiv = document.createElement("DIV");

AjxStringUtil.htmlPlatformIndependent =
function(html) {
	var div = AjxStringUtil._dummyDiv;
	div.innerHTML = html;
	var inner = div.innerHTML;
	div.innerHTML = "";
	return inner;
};

/**
 * compare two html code fragments, ignoring the case of tags, since the tags inside innnerHTML are returned differently by different browsers (and from Outlook)
 * e.g. IE returns CAPS for tag names in innerHTML while FF returns lowercase tag names. Outlook signature creation also returns lowercase.
 * this approach is also good in case the browser removes some of the innerHTML set to it, like I suspect might be in the case of stuff coming from Outlook. (e.g. it removes head tag since it's illegal inside a div)
 *
 * @param html1
 * @param html2
 */
AjxStringUtil.equalsHtmlPlatformIndependent =
function(html1, html2) {
	return AjxStringUtil.htmlPlatformIndependent(html1) == AjxStringUtil.htmlPlatformIndependent(html2);
};

// Stuff for parsing messages to find original (as opposed to quoted) content

// types of content related to finding original content; not all are used
AjxStringUtil.ORIG_UNKNOWN		= "UNKNOWN";
AjxStringUtil.ORIG_QUOTED		= "QUOTED";
AjxStringUtil.ORIG_SEP_STRONG	= "SEP_STRONG";
AjxStringUtil.ORIG_SEP_WEAK		= "SEP_WEAK";
AjxStringUtil.ORIG_WROTE_STRONG	= "WROTE_STRONG";
AjxStringUtil.ORIG_WROTE_WEAK	= "WROTE_WEAK";
AjxStringUtil.ORIG_HEADER		= "HEADER";
AjxStringUtil.ORIG_LINE			= "LINE";
AjxStringUtil.ORIG_SIG_SEP		= "SIG_SEP";

// regexes for parsing msg body content so we can figure out what was quoted and what's new
// TODO: should these be moved to ZmMsg to be fully localizable?
AjxStringUtil.MSG_REGEXES = [
	{
		// the two most popular quote characters, > and |
		type:	AjxStringUtil.ORIG_QUOTED,
		regex:	/^\s*(>|\|)/
	},
	{
		// marker for Original or Forwarded message, used by ZCS and others
		type:	AjxStringUtil.ORIG_SEP_STRONG,
		regex:	new RegExp("^\\s*--+\\s*(" + AjxMsg.origMsg + "|" + AjxMsg.forwardedMessage + "|" + AjxMsg.origAppt + ")\\s*--+\\s*$", "i")
	},
	{
		// one of the commonly quoted email headers
		type:	AjxStringUtil.ORIG_HEADER,
		regex:	new RegExp("^\\s*(" + [AjxMsg.from, AjxMsg.to, AjxMsg.subject, AjxMsg.date, AjxMsg.sent, AjxMsg.cc].join("|") + ")")
	},
	{
		// some clients use a series of underscores as a text-mode separator (text version of <hr>)
		type:	AjxStringUtil.ORIG_LINE,
		regex:	/^\s*_{5,}\s*$/
	}/*,
	{
		// in case a client doesn't use the exact words above
		type:	AjxStringUtil.ORIG_SEP_WEAK,
		regex:	/^\s*--+\s*[\w\s]+\s*--+$/
	},
	{
		// internet style signature separator
		type:	AjxStringUtil.ORIG_SIG_SEP,
		regex:	/^- ?-\s*$/
	}*/
];

// ID for an HR to mark it as ours
AjxStringUtil.HTML_SEP_ID = "zwchr";

// regexes for finding a delimiter such as "On DATE, NAME (EMAIL) wrote:"
AjxStringUtil.ORIG_EMAIL_RE = /[^@\s]+@[A-Za-z0-9\-]{2,}(\.[A-Za-z0-9\-]{2,})+/;	// see AjxUtil.EMAIL_FULL_RE
AjxStringUtil.ORIG_DATE_RE = /, 20\d\d/;
AjxStringUtil.ORIG_INTRO_RE = new RegExp("^(-{2,}|" + AjxMsg.on + ")", "i")


// Lazily creates a test hidden IFRAME and writes the given html to it, then returns the HTML element.
AjxStringUtil._writeToTestIframeDoc =
function(html) {

	var idoc = AjxStringUtil._htmlContentIframeDoc;
	// Firefox has weird issue where iframe gets stuck with first content written to it; subsequent opens do not truncate
	// IE sometimes throws "Permission denied" error
	if (!idoc || AjxEnv.isFirefox || AjxEnv.isIE) {
		var iframe = document.createElement("IFRAME");
		iframe.id = Dwt.getNextId();	// without an ID, IE will throw "Permission denied" errors ?!
		
		// position offscreen rather than set display:none so we can get metrics if needed; no perf difference seen
		Dwt.setPosition(iframe, Dwt.ABSOLUTE_STYLE);
		Dwt.setLocation(iframe, Dwt.LOC_NOWHERE, Dwt.LOC_NOWHERE);
		document.body.appendChild(iframe);
		idoc = AjxStringUtil._htmlContentIframeDoc = Dwt.getIframeDoc(iframe);
		AjxStringUtil.__curIframeId = AjxEnv.isFirefox ? iframe.id : null;
	}
	idoc.open();
	idoc.write(html);
	idoc.close();

	return idoc.childNodes[0];
};

// Firefox only - clean up test iframe since we can't reuse it
AjxStringUtil._removeTestIframeDoc =
function() {
	if (AjxStringUtil.__curIframeId) {
		var iframe = document.getElementById(AjxStringUtil.__curIframeId);
		if (iframe) {
			iframe.parentNode.removeChild(iframe);
		}
		AjxStringUtil.__curIframeId = null;
	}
};

/**
 * Analyze the text and return what appears to be original (as opposed to quoted) content. We
 * look for separators commonly used by mail clients, as well as prefixes that indicate that
 * a line is being quoted.
 * 
 * @param {string}	text		message body content
 * 
 * @return	{string}	original content if quoted content was found, otherwise NULL
 */
AjxStringUtil.getOriginalContent =
function(text, isHtml) {
	
	if (!text) { return ""; }
	
	if (isHtml) {
		return AjxStringUtil._getOriginalHtmlContent(text);
	}

	var results = [];
	var lines = text.split(AjxStringUtil.SPLIT_RE);
	
	var curType, curBlock = [], count = {}, isMerged, unknownBlock;
	for (var i = 0; i < lines.length; i++) {
		var line = lines[i];
		var testLine = AjxStringUtil.trim(line);

		// blank lines are just added to the current block
		if (!AjxStringUtil._NON_WHITESPACE.test(testLine)) {
			curBlock.push(line);
			continue;
		}
		
		// Bugzilla is very good at fooling us, and does not have quoted content, so bail
		if ((testLine.indexOf("| DO NOT REPLY") == 0) && (lines[i + 2].indexOf("bugzilla") != -1)) {
			return text;
		}

		var type = AjxStringUtil._getLineType(testLine);

		// WROTE can stretch over two lines; if so, join them into one line
		var nextLine = lines[i + 1];
		var isMerged = false;
		if ((type == AjxStringUtil.ORIG_UNKNOWN) && AjxStringUtil.ORIG_INTRO_RE.test(testLine) && nextLine.match(/\w+:$/)) {
			testLine = [testLine, nextLine].join(" ");
			type = AjxStringUtil._getLineType(testLine);
			isMerged = true;
		}
		
		// LINE sometimes used as delimiter; if HEADER follows, lump it in with them
		if (type == AjxStringUtil.ORIG_LINE) {
			var j = i + 1;
			nextLine = lines[j];
			while (!AjxStringUtil._NON_WHITESPACE.test(nextLine) && j < lines.length) {
				nextLine = lines[++j];
			}
			var nextType = nextLine && AjxStringUtil._getLineType(nextLine);
			if (nextType == AjxStringUtil.ORIG_HEADER) {
				type = AjxStringUtil.ORIG_HEADER;
			}
			else {
				type = AjxStringUtil.ORIG_UNKNOWN;
			}
		}
				
		// see if we're switching to a new type; if so, package up what we have so far
		if (curType) {
			if (curType != type) {
				results.push({type:curType, block:curBlock});
				unknownBlock = (curType == AjxStringUtil.ORIG_UNKNOWN) ? curBlock : unknownBlock;
				count[curType] = count[curType] ? count[curType] + 1 : 1;
				curBlock = [];
				curType = type;
			}
		}
		else {
			curType = type;
		}
		
		if (isMerged && (type == AjxStringUtil.ORIG_WROTE_WEAK || type == AjxStringUtil.ORIG_WROTE_STRONG)) {
			curBlock.push(line);
			curBlock.push(nextLine);
			i++;
			isMerged = false;
		}
		else {
			curBlock.push(line);
		}
	}

	if (curBlock.length) {
		results.push({type:curType, block:curBlock});
		unknownBlock = (curType == AjxStringUtil.ORIG_UNKNOWN) ? curBlock : unknownBlock;
		count[curType] = count[curType] ? count[curType] + 1 : 1;
	}
	
	// If we have a STRONG separator (eg "--- Original Message ---"), consider it authoritative and return the text that precedes it
	if (count[AjxStringUtil.ORIG_SEP_STRONG] > 0) {
		var block = [];
		for (var i = 0; i < results.length; i++) {
			var result = results[i];
			if (result.type == AjxStringUtil.ORIG_SEP_STRONG) {
				break;
			}
			block = block.concat(result.block);
		}
		var originalText = AjxStringUtil._getTextFromBlock(block);
		if (originalText) {
			return originalText;
		}
	}

	// check for special case of WROTE preceded by UNKNOWN, followed by mix of UNKNOWN and QUOTED (inline reply)
	var originalText = AjxStringUtil._checkInlineWrote(count, results, false);
	if (originalText) {
		return originalText;
	}
	
	// If we found quoted content and there's exactly one UNKNOWN block, return it.
	if (count[AjxStringUtil.ORIG_UNKNOWN] == 1 && count[AjxStringUtil.ORIG_QUOTED] > 0) {
		var originalText = AjxStringUtil._getTextFromBlock(unknownBlock);
		if (originalText) {
			return originalText;
		}
	}
	
	return text;
};

// Matches a line of text against some regexes to see if has structural meaning within a mail msg.
AjxStringUtil._getLineType =
function(testLine) {

	var type = AjxStringUtil.ORIG_UNKNOWN;
	
	// see if the line matches any known delimiters or quote patterns
	for (var j = 0; j < AjxStringUtil.MSG_REGEXES.length; j++) {
		var msgTest = AjxStringUtil.MSG_REGEXES[j];
		var regex = msgTest.regex;
		if (regex.test(testLine.toLowerCase())) {
			// line that starts and ends with | is considered ASCII art (eg a table) rather than quoted
			if (msgTest.type == AjxStringUtil.ORIG_QUOTED && /^\s*\|.*\|\s*$/.test(testLine)) {
				continue;
			}
			type = msgTest.type;
			break;	// first match wins
		}
	}
	
	if (type == AjxStringUtil.ORIG_UNKNOWN) {
		// "so-and-so wrote:" takes a lot of different forms; look for various common parts and
		// assign points to determine confidence
		var m = testLine.match(/(\w+):$/);
		var verb = m && m[1] && m[1].toLowerCase();
		if (verb) {
			var points = 0;
			// look for "wrote:" (and discount "changed:", which is used by Bugzilla)
			points = points + (verb == AjxMsg.wrote) ? 5 : (verb == AjxMsg.changed) ? 0 : 3;
			if (AjxStringUtil.ORIG_EMAIL_RE.test(testLine)) {
				points += 4;
			}
			if (AjxStringUtil.ORIG_DATE_RE.test(testLine)) {
				points += 3;
			}
			var regEx = new RegExp("^(--|" + AjxMsg.on + ")", "i");
			if (AjxStringUtil.ORIG_INTRO_RE.test(testLine)) {
				points += 1;
			}
			if (points >= 7) {
				type = AjxStringUtil.ORIG_WROTE_STRONG;
			}
			else if (points >= 5) {
				type = AjxStringUtil.ORIG_WROTE_WEAK;
			}
		}
	}
	
	return type;
};

AjxStringUtil._getTextFromBlock =
function(block) {
	if (!(block && block.length)) { return null; }
	var originalText = block.join("\n") + "\n";
	originalText = originalText.replace(/\s+$/, "\n");
	return (AjxStringUtil._NON_WHITESPACE.test(originalText)) ? originalText : null;
};

AjxStringUtil.SCRIPT_REGEX = /<script\b[^<]*(?:(?!<\/script>)<[^<]*)*<\/script>/gi;

/**
 * For HTML, we strip off the html, head, and body tags and stick the rest in a temporary DOM node so that
 * we can walk the tree. Instead of going line by line, we go element by element. The easiest way is to figure
 * out which nodes we can remove if the message appears to have quoted and original content.
 * 
 * @param {string}	text		message body content
 * 
 * @return	{string}	original content if quoted content was found, otherwise NULL
 * @private
 */
AjxStringUtil._getOriginalHtmlContent =
function(text) {
	
	// strip <script> tags (which should not be there)
	while (AjxStringUtil.SCRIPT_REGEX.test(text)) {
		text = text.replace(AjxStringUtil.SCRIPT_REGEX, "");
	}
	
	var htmlNode = AjxStringUtil._writeToTestIframeDoc(text);
	var ctxt = {
		curType:	null,
		count:		{},
		level:		0,
		toRemove:	[],
		done:		false,
		hasQuoted:	false,
		sepNode:	null,
		results:	[]
	};
	AjxStringUtil._traverseOriginalHtmlContent(htmlNode, ctxt);

	// check for special case of WROTE preceded by UNKNOWN, followed by mix of UNKNOWN and QUOTED (inline reply)
	AjxStringUtil._checkInlineWrote(ctxt.count, ctxt.results, true, ctxt);
	
	// if there's one UNKNOWN section and some QUOTED, preserve the UNKNOWN
	if (!ctxt.done) {
		if (ctxt.count[AjxStringUtil.ORIG_UNKNOWN] == 1 && ctxt.hasQuoted) {
			for (var i = 0; i < ctxt.toRemove.length; i++) {
				var el = ctxt.toRemove[i];
				if (el && el.parentNode) {
					el.parentNode.removeChild(el);
				}
			}
			ctxt.done = true;
		}
	}

	// convert back to text, restoring html, head, and body nodes
	var result = ctxt.done ? "<html>" + htmlNode.innerHTML + "</html>" : text;

	AjxStringUtil._removeTestIframeDoc();
	return result;	
};

// nodes to ignore; they won't have anything we're interested in
AjxStringUtil.IGNORE_NODE_LIST = ["#comment", "script", "select", "style"];
AjxStringUtil.IGNORE_NODE = AjxUtil.arrayAsHash(AjxStringUtil.IGNORE_NODE_LIST);

// Walk the tree, looking for a definitive delimiter and determining content types for nodes. If we find a node
// that's a delimiter, we clip it and all subsequent nodes at its level, and we're done.
AjxStringUtil._traverseOriginalHtmlContent =
function(el, ctxt) {

	if (ctxt.done || !el) { return; }
	
	var nodeName = el.nodeName.toLowerCase();
	DBG.println("html", AjxStringUtil.repeat("&nbsp;&nbsp;&nbsp;&nbsp;", ctxt.level) + nodeName + ((nodeName == "#text" && /\S+/.test(el.nodeValue) ? " - " + el.nodeValue.substr(0, 20) : "")));
	var type;
	var processChildren = true;
	
	// Text node: test against our regexes
	if (nodeName == "#text") {
		if (!AjxStringUtil._NON_WHITESPACE.test(el.nodeValue)) {
			return;
		}
		var testLine = AjxStringUtil.trim(el.nodeValue);
		type = AjxStringUtil._getLineType(testLine);
		if (type == AjxStringUtil.ORIG_SEP_STRONG || type == AjxStringUtil.ORIG_WROTE_STRONG) {
			ctxt.sepNode = el;	// mark for removal
		}
		else if (type != AjxStringUtil.ORIG_WROTE_STRONG) {
			var m = testLine.match(/(\w+):$/);
			if (m && m[1] && el.parentNode) {
				// what appears as a single "... wrote:" line may have multiple elements, so gather it all
				// together into one line and test that
				testLine = AjxStringUtil.trim(AjxStringUtil.htmlDecode(AjxStringUtil.stripTags(el.parentNode.innerHTML)));
				type = AjxStringUtil._getLineType(testLine);
				if (type == AjxStringUtil.ORIG_WROTE_STRONG) {
					// check for a multinode WROTE; if we find one, gather it into a SPAN so that we
					// have a single node to deal with later
					var pn = el.parentNode, nodes = pn.childNodes, startNodeIndex, stopNodeIndex;
					for (var i = pn.childNodes.length - 1; i >= 0; i--) {
						var childNode = pn.childNodes[i];
						var text = childNode.nodeValue;
						if (!text) { continue; }
						if (text.match(/(\w+):$/)) {
							stopNodeIndex = i;
						}
						else if ((stopNodeIndex != null) && text.match(AjxStringUtil.ORIG_INTRO_RE)) {
							startNodeIndex = i;
							break;
						}
					}
					if (startNodeIndex != null && stopNodeIndex != null) {
						var span = document.createElement("span");
						for (var i = 0; i < (stopNodeIndex - startNodeIndex) + 1; i++) {
							span.appendChild(nodes[startNodeIndex]);
						}
						pn.insertBefore(span, nodes[startNodeIndex]);
						ctxt.sepNode = span;
					}
				}
			}
		}
		
	// HR: look for a couple different forms that are used to delimit quoted content
	} else if (nodeName == "hr") {
		// see if the HR is ours, or one commonly used by other mail clients such as Outlook
		if (el.id == AjxStringUtil.HTML_SEP_ID ||
			(el.size == "2" && el.width == "100%" && el.align == "center")) {
			
			type = AjxStringUtil.ORIG_SEP_STRONG;
			ctxt.sepNode = el;	// mark for removal
		}
		
	// PRE: treat as one big line of text (should maybe go line by line)
	} else if (nodeName == "pre") {
		var text = AjxStringUtil.htmlDecode(AjxStringUtil.stripTags(el.innerHTML));
		type = AjxStringUtil._getLineType(text);

	// BR: ignore
	} else if (nodeName == "br") {
		return;

	// DIV: check for Outlook class used as delimiter
	} else if (nodeName == "div") {
		if (el.className == "OutlookMessageHeader") {
			type = AjxStringUtil.ORIG_SEP_STRONG;
			ctxt.sepNode = el;	// mark for removal
		}

	// SPAN: check for Outlook ID used as delimiter
	} else if (nodeName == "span") {
		if (el.id == "OLK_SRC_BODY_SECTION") {
			type = AjxStringUtil.ORIG_SEP_STRONG;
			ctxt.sepNode = el;	// mark for removal
		}

	// IMG: treat as original content
	} else if (nodeName == "img") {
		type = AjxStringUtil.ORIG_UNKNOWN;

	// BLOCKQUOTE: treat as quoted section
	} else if (nodeName == "blockquote") {
		type = AjxStringUtil.ORIG_QUOTED;
		ctxt.toRemove.push(el);
		ctxt.hasQuoted = true;
		processChildren = false;

	} else if (nodeName == "script") {
		throw new Error("SCRIPT tag found in AjxStringUtil._traverseOriginalHtmlContent");
	
	// node types to ignore
	} else if (AjxStringUtil.IGNORE_NODE[nodeName]) {
		return;
	}

	// see if we've found a new type
	if (type) {
		if (ctxt.curType) {
			if (ctxt.curType != type) {
				ctxt.count[ctxt.curType] = ctxt.count[ctxt.curType] ? ctxt.count[ctxt.curType] + 1 : 1;
				ctxt.results.push({type:ctxt.curType});
				ctxt.curType = type;
				ctxt.curBlock = [];
			}
		}
		else {
			ctxt.curType = type;
		}
	}

	// if we found a recognized delimiter, set flag to clip it and subsequent nodes at its level
	if (type == AjxStringUtil.ORIG_SEP_STRONG) {
		if (ctxt.count[AjxStringUtil.ORIG_UNKNOWN] == 1) {
			ctxt.done = true;
		}
	}
	
	if (!processChildren) {
		return;	// don't visit node's children
	}
	
	ctxt.level++;

	// any element that gets here will get recursed into
	for (var i = 0, len = el.childNodes.length; i < len; i++) {
		var childNode = el.childNodes[i];
		AjxStringUtil._traverseOriginalHtmlContent(childNode, ctxt);
		// see if we ran into a delimiter
		if (ctxt.done) {
			// clip all subsequent nodes
			while (el && el.lastChild && el.lastChild != childNode) {
				el.removeChild(el.lastChild);
			}
			// clip the delimiter node
			if (el && el.lastChild == ctxt.sepNode) {
				el.removeChild(el.lastChild);
			}
			break;
		}
	}

	ctxt.level--;
};

/**
 * Checks the given HTML to see if it is "safe", and cleans it up if it is. It must have only
 * the tags in the given list, otherwise false is returned. Attributes in the given list will
 * be removed. It is not necessary to include "#text", "html", "head", and "body" in the list
 * of allowed tags.
 * 
 * @param {string}	html			HTML text
 * @param {array}	okTags			whitelist of allowed tags
 * @param {array}	attrsToRemove	list of attributes to remove from each element
 * @param {array}	badStyles		list of forbidden styles (eg "position:absolute")
 */
AjxStringUtil.checkForCleanHtml =
function(html, okTags, attrsToRemove, badStyles) {

	var htmlNode = AjxStringUtil._writeToTestIframeDoc(html);
	var ctxt = {
		tags:	AjxUtil.arrayAsHash(okTags),
		attrs:	attrsToRemove || [],
		styles:	badStyles
	}
	AjxStringUtil._traverseCleanHtml(htmlNode, ctxt);
	
	var result = !ctxt.fail && "<html>" + htmlNode.innerHTML + "</html>";
	var width = Math.max(htmlNode.scrollWidth, htmlNode.lastChild.scrollWidth);

	AjxStringUtil._removeTestIframeDoc();
	return {html:result, width:width};
};

AjxStringUtil._traverseCleanHtml =
function(el, ctxt) {
		
	var nodeName = el.nodeName.toLowerCase();
	
	// useless <style> that we used to add, remove it
	if (nodeName == "style" && el.innerHTML == "p { margin: 0; }") {
		el.doDelete = true;
	}
	
	// IE likes to insert an empty <title> in the <head>, let it go
	else if (nodeName == "title" && el.innerHTML == "") {
	}
	
	// allowed tag - strip the forbidden attributes from it
	else if (ctxt.tags[nodeName]) {
		if (el.removeAttribute && el.attributes.length) {
			// blacklisted attrs
			for (var i = 0; i < ctxt.attrs.length; i++) {
				el.removeAttribute(ctxt.attrs[i]);
			}
			// on* handlers (should have been removed by server, check again to be safe)
			for (var i = 0, attrs = el.attributes, l = attrs.length; i < l; i++) {
				var attr = attrs.item(i);
				var attrName = attr.nodeName && attr.nodeName.toLowerCase();
				var attrValue = String(attr.nodeValue).toLowerCase();
				// we have global CSS rules for TD that trump table properties, so bail
				if (nodeName == "table" && (attrName == "cellpadding" || attrName == "cellspacing" ||
						attrName == "border") && attrValue != "0") {
					ctxt.fail = true;
					break;
				}
				if (attrName && attrName.indexOf("on") === 0) {
					el.removeAttribute(attrName);
				}
				if (ctxt.styles && (attrName == "style") && attrValue) {
					var value = attrValue.replace(/\s*/g, "");
					if (value) {
						for (var j = 0; j < ctxt.styles.length; j++) {
							var style = ctxt.styles[j];
							if (value.indexOf(style) != -1) {
								ctxt.fail = true;
								break;
							}
						}
					}
				}
			}
		}
	}
	
	// disallowed tag - bail
	else {
		ctxt.fail = true;
	}

	if (ctxt.fail) { return; }
	
	// process child nodes
	for (var i = 0, len = el.childNodes.length; i < len; i++) {
		var childNode = el.childNodes[i];
		AjxStringUtil._traverseCleanHtml(childNode, ctxt);
		if (ctxt.fail) { return; }
	}
	
	// remove nodes marked for deletion
	for (var i = el.childNodes.length - 1; i >= 0; i--) {
		var childNode = el.childNodes[i];
		if (childNode.doDelete) {
			el.removeChild(childNode);
		}
	}
};

/**
 * A "... wrote:" separator is not quite as authoritative, since the user might be replying inline. If we have
 * a single UNKNOWN block before the WROTE separator, return it unless there is a mix of QUOTED and UNKNOWN
 * following the separator.
 * 
 * @private
 */
AjxStringUtil._checkInlineWrote =
function(count, results, isHtml, ctxt) {

	if (count[AjxStringUtil.ORIG_WROTE_STRONG] > 0) {
		var unknownBlock, foundSep = false, afterSep = {};
		for (var i = 0; i < results.length; i++) {
			var result = results[i], type = result.type;
			if (type == AjxStringUtil.ORIG_WROTE_STRONG) {
				foundSep = true;
			}
			else if (type == AjxStringUtil.ORIG_UNKNOWN && !foundSep) {
				unknownBlock = isHtml ? true : result.block;
			}
			else if (foundSep) {
				afterSep[type] = true;
			}
		}

		if (unknownBlock && (!isHtml || ctxt.sepNode) && !(afterSep[AjxStringUtil.ORIG_UNKNOWN] && afterSep[AjxStringUtil.ORIG_QUOTED])) {
			if (isHtml) {
				// In HTML mode we do DOM surgery rather than returning the original content
				var el = ctxt.sepNode.parentNode;
				// clip all subsequent nodes
				while (el && el.lastChild && el.lastChild != ctxt.sepNode) {
					el.removeChild(el.lastChild);
				}
				// clip the delimiter node
				if (el && el.lastChild == ctxt.sepNode) {
					el.removeChild(el.lastChild);
				}
			}
			else {
				var originalText = AjxStringUtil._getTextFromBlock(unknownBlock);
				if (originalText) {
					return originalText;
				}
			}
		}
	}
};

// Strips trailing <BR> from HTML text that appears before closing tags.
AjxStringUtil.removeTrailingBR =
function(str) {
	if (!str) {
		return "";
	}
	var m = str && str.match(/((<br>)+)((<\/\w+>)+)$/i);
	if (m && m.length) {
		var regex = new RegExp(m[1] + m[3] + "$", "i");
		str = str.replace(regex, m[3]);
	}
	return str;
};

// Replaces img src to cid for inline or dfsrc if external image and remove dfsrc before sending for a given htmlContent
AjxStringUtil.defangHtmlContent =
function(htmlContent) {
    var content = htmlContent;

        var content = htmlContent;
        var imgContent = content && content.match(/<img/i) && content.split(/<img/i);
		if (imgContent && imgContent.length) {
			for (var i = 0; i < imgContent.length; i++) {
				var externalImage = false;
				var dfsrc = imgContent[i].match(/dfsrc=[\"|\'](cid:[^\"\']+)/); //look for CID assignment in image
				if (dfsrc && dfsrc.length > 1) {
					dfsrc = [dfsrc[1]]; //the cid is the 2nd element, but next lines expect it as first
				}
				if (!dfsrc) {
					dfsrc = imgContent[i].match(/\s+dfsrc=[\"\'][^\"\']+[\"\']+/); //look for dfsrc="" in image
					externalImage = dfsrc ? true : false;
				}
				if (dfsrc && dfsrc.length > 0 && !externalImage) {
					var tempStr = imgContent[i].replace(/\s+src=[\"\'][^\"\']+[\"\']/," src=\""+dfsrc[0]+"\""); //set src to cid
					tempStr = tempStr.replace(/\s+dfsrc=[\"\'][^\"\']+[\"\']+/,"");
					content = content.replace(imgContent[i], tempStr);
				}
				else if (dfsrc && dfsrc.length > 0 && externalImage) {
					var tempArr = imgContent[i].match(/\s+dfsrc=[\"\']([^\"\']+)[\"\']/); //match dfsrc
					if (tempArr && tempArr.length > 1) {
					   var tempStr = imgContent[i].replace(/\s+dfsrc=[\"\'][^\"\']+[\"\']/," src=\""+tempArr[1]+"\"");
					   content = content.replace(imgContent[i], tempStr);
					}
				}
			}
        }
        return content;

};

