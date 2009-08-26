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

/**
* Does nothing (static class).
* @constructor
* @class
* This class provides static methods to perform miscellaneous string-related utility functions.
*
* @author Ross Dargahi
* @author Roland Schemers
* @author Conrad Damon
*/
function ZMTB_AjxStringUtil() {
}

/**
* Removes white space from the beginning and end of a string, optionally compressing internal white space. By default, white
* space is defined as a sequence of  Unicode whitespace characters (\s in regexes). Optionally, the user can define what
* white space is by passing it as an argument.
*
* <p>TODO: add left/right options</p>
*
* @param str      	the string to trim
* @param compress 	whether to compress internal white space to one space
* @param space    	a string that represents a user definition of white space
* @returns			a trimmed string
*/

ZMTB_AjxStringUtil.TRIM_RE = /^\s+|\s+$/g;
ZMTB_AjxStringUtil.COMPRESS_RE = /\s+/g;
ZMTB_AjxStringUtil.ELLIPSIS = " ... ";

ZMTB_AjxStringUtil.makeString =
function(val) {
	return val ? String(val) : "";
};

ZMTB_AjxStringUtil.trim =
function(str, compress, space) {

	if (!str) {return "";}

	var trim_re = ZMTB_AjxStringUtil.TRIM_RE;

	var compress_re = ZMTB_AjxStringUtil.COMPRESS_RE;
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
* @param str		a string
* @param num		number of times to repeat the string
*/
ZMTB_AjxStringUtil.repeat =
function(str, num) {
	var text = "";
	for (var i = 0; i < num; i++) {
		text += str;
	}
	return text;
};

ZMTB_AjxStringUtil.getUnitsFromSizeString =
function(sizeString) {
	var units="px";
	if(typeof(sizeString) == "string") {
		var digitString=Number(parseInt(sizeString,10)).toString();
		if(sizeString.length > digitString.length) {
			units = sizeString.substr(digitString.length, (sizeString.length-digitString.length));
			if(!(units=="em" || units=="ex" || units=="px" || units=="in" || units=="cm" == units=="mm" || units=="pt" || units=="pc" || units=="%")) {
				units="px";
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
* @param str	the string to split
* @param dels	an optional string of delimiter characters
* @returns		an array of strings
*/
ZMTB_AjxStringUtil.split =
function(str, dels) {

	if (!str) {return new Array();}
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
	var chunks = new Array();
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
* Wraps text to the given length and quotes it, breaking on space when possible.
* Preserves line breaks. At this point, it assumes that the text to be wrapped
* is raw text, not HTML, and that line returns are represented by '\n'. Wrapping
* is optionally done across line returns that appear in paragraphs.
*
* @param text 		the text to be wrapped
* @param len		the desired line length of the wrapped text, defaults to 80
* @param pre		an optional string to prepend to each line (useful for quoting)
* @param eol		the eol sequence for each wrapped line, defaults to '\n'
* @param breakOkay	whether long words (longer than <code>len</code>) can be broken, default is false
* @param compress	remove single returns within a paragraph before wrapping
* @returns			the wrapped/quoted text
*/
ZMTB_AjxStringUtil.wordWrap =
function(text, len, pre, eol, breakOkay, compress) {

	if (!text) {return "";}

	len = len ? len : 80;
	eol = eol ? eol : '\n';
	pre = pre ? pre : '';
	len -= pre.length;

	var chunks = new Array();
	var c = 0;

	// preprocess the text: remove leading/trailing space, space at the end of
	// lines, and set up for wrapping paragraphs
	text = ZMTB_AjxStringUtil.trim(text, false);
	text = text.replace(/[ \t]+\n/g, '\n'); // optional tidying, could remove this step
	if (compress) {
		text = text.replace(/\b\n\b/g, ' ');
	}
	var textLen = text.length;
	// Wrap text by dividing it into chunks. We remember the last space we saw,
	// and use it to begin a chunk when the length limit is reached.
	for (var i = 0, bk = 0, sp = -1; i < textLen; i++) {
		var ch = text.charAt(i);
		if (ch.match(/[ \t]/)) { // found a space
			sp = i;
		}
		if (ch == '\n') { // found a return
			chunks[c++] = pre + text.substring(bk, i);
			bk = i + 1; // skip the \n (those are added later in the join)
			sp = -1;
		}
		if (i - bk >= len) { // hit the limit
			if (sp == -1) { // current chunk is bigger than the limit (a 'long' word)
				if (breakOkay) {
					chunks[c++] = pre + text.substring(bk, i);
					bk = i;
					sp = -1;
				}
			} else {
				chunks[c++] = pre + text.substring(bk, sp);
				bk = sp + 1;
				sp = -1;
			}
		}
	}
	// add remaining portion
	if (i > bk) {
		chunks[c++] = pre + text.substring(bk, i);
	}
	return chunks.join(eol) + eol;
};

/**
* Returns true if the character for the given key is considered printable.
*
* @param keycode	a numeric keycode (not a character code)
* @returns 			true if the character for the given key is considered printable
*/

ZMTB_AjxStringUtil.IS_PRINT_CODE = new Object();
var print_codes = [32,48,49,50,51,52,53,54,55,56,57,59,65,66,67,68,69,70,71,72,73,74,75,76,77,78,79,80,
                   81,82,83,84,85,86,87,88,89,90,96,97,98,99,100,101,102,103,104,105,106,107,109,110,111,186,
                   187,188,189,190,191,192,219,220,221,222];
var l = print_codes.length;
for (var i = 0; i < l; i++) {
	ZMTB_AjxStringUtil.IS_PRINT_CODE[print_codes[i]] = true;
}

ZMTB_AjxStringUtil.isPrintKey =
function(keycode) {
	return ZMTB_AjxStringUtil.IS_PRINT_CODE[keycode];
};

/**
* Returns the character for the given key, taking the shift key into consideration.
*
* @param keycode	a numeric keycode (not a character code)
* @param shifted	whether the shift key is down
* @returns			a character
*/

ZMTB_AjxStringUtil.SHIFT_CHAR = { 48:')', 49:'!', 50:'@', 51:'#', 52:'$', 53:'%', 54:'^', 55:'&', 56:'*', 57:'(',
							59:':', 186:':', 187:'+', 188:'<', 189:'_', 190:'>', 191:'?', 192:'~',
							219:'{', 220:'|', 221:'}', 222:'"' };

ZMTB_AjxStringUtil.shiftChar =
function(keycode, shifted) {
	return shifted ? ZMTB_AjxStringUtil.SHIFT_CHAR[keycode] || String.fromCharCode(keycode) : String.fromCharCode(keycode);
};

/**
* Does a diff between two strings, returning the index of the first differing character.
*
* @param str1	a string
* @param str2	another string
* @returns		the index at which they first differ
*/
ZMTB_AjxStringUtil.diffPoint =
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

/**
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
*/
ZMTB_AjxStringUtil.resolve =
function(str, values) {
	// DBG.println(AjxDebug.DBG1, "Call to deprecated function ZMTB_AjxStringUtil.resolve");
	return AjxMessageFormat.format(str, values);
/*
	if (!str) return "";
	if (!(values instanceof Array)) values = [values];
	if (!ZMTB_AjxEnv.isSafari)
		return str.replace(/\$(\d+)/g, function(str, num) { return values[num]; });

	//quick hack
	var match;
	while ((match = str.match(/\$(\d+)/)) != null) {
		var d = match[1];
		var re = new RegExp("\\$"+d);
		str = str.replace(re, values[d]);
	}
	return str;
*/
};

/**
* URL-encodes a string. Replace + with %2B and space with +.
*
* @param str	the string to encode
*/
ZMTB_AjxStringUtil.urlEncode =
function(str) {
	if (!str) return "";
	var func = window.encodeURL || window.encodeURI;
	return func(str);
};

/**
 * Encodes a string as if it were a <em>part</em> of a URL. The
 * difference between this function and {@link ZMTB_AjxStringUtil.urlEncode}
 * is that this will also encode forward slashes.
 */
ZMTB_AjxStringUtil.urlComponentEncode = function(str) {
	if (!str) return "";
	var func = window.encodeURLComponent || window.encodeURIComponent;
	return func(str);
};

/**
* HTML-encodes a string.
*
* @param str	the string to encode
*/

ZMTB_AjxStringUtil.ENCODE_MAP = { '>' : '&gt;', '<' : '&lt;', '&' : '&amp;' };

ZMTB_AjxStringUtil.htmlEncode =
function(str, includeSpaces) {
	if (!str) {return "";}

	if (!ZMTB_AjxEnv.isSafari) {
		if (includeSpaces) {
			return str.replace(/[<>&]/g, function(htmlChar) { return ZMTB_AjxStringUtil.ENCODE_MAP[htmlChar]; }).replace(/  /g, ' &nbsp;');
		} else {
			return str.replace(/[<>&]/g, function(htmlChar) { return ZMTB_AjxStringUtil.ENCODE_MAP[htmlChar]; });
		}
	} else {
		if (includeSpaces) {
			return str.replace(/[&]/g, '&amp;').replace(/  /g, ' &nbsp;').replace(/[<]/g, '&lt;').replace(/[>]/g, '&gt;');
		} else {
			return str.replace(/[&]/g, '&amp;').replace(/[<]/g, '&lt;').replace(/[>]/g, '&gt;');
		}
	}
};

ZMTB_AjxStringUtil.convertToHtml = function(str) {
	if (!str) {return "";}
	str = str
		.replace(/&/mg, "&amp;")
		.replace(/  /mg, " &nbsp;")
		.replace(/^ /mg, "&nbsp;")
		.replace(/\t/mg, "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;")
		.replace(/</mg, "&lt;")
		.replace(/>/mg, "&gt;")
		.replace(/\r?\n/mg, "<br />");
	return str;
};

/**
* HTML-encodes a string.
*
* @param str	the string to encode
*/

ZMTB_AjxStringUtil.SPACE_ENCODE_MAP = { ' ' : '&nbsp;', '>' : '&gt;', '<' : '&lt;', '&' : '&amp;' , '\n': '<br>'};

ZMTB_AjxStringUtil.htmlEncodeSpace =
function(str) {
	if (!str) {return "";}

	if (!ZMTB_AjxEnv.isSafari) {
		return str.replace(/[ <>&\n]/g, function(htmlChar) { return ZMTB_AjxStringUtil.SPACE_ENCODE_MAP[htmlChar]; });
	} else {
		return str.replace(/[&]/g, '&amp;').replace(/ /g, '&nbsp;').replace(/[<]/g, '&lt;').replace(/[>]/g, '&gt;');
	}
};

// this function makes sure a leading space is preservered, takes care of tabs,
// then finally takes replaces newlines with <br>'s
ZMTB_AjxStringUtil.nl2br =
function(str) {
	if (!str) return "";
	return str.replace(/^ /mg, "&nbsp;").
		// replace(/\t/g, "<pre style='display:inline;'>\t</pre>").
		// replace(/\t/mg, "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;").
		replace(/\t/mg, "<span style='white-space:pre'>\t</span>").
		replace(/\n/g, "<br>");
};

ZMTB_AjxStringUtil.xmlEncode =
function(str) {
	if (str) {
		// bug fix #8779 - safari barfs if "str" is not a String type
		str = "" + str;
		return str.replace(/&/g,"&amp;").replace(/</g,"&lt;");
	}
	return "";
};

ZMTB_AjxStringUtil.xmlDecode =
function(str) {
	return str ? str.replace(/&amp;/g,"&").replace(/&lt;/g,"<") : "";
};

ZMTB_AjxStringUtil.xmlAttrEncode =
function(str) {
	return str ? str.replace(/&/g,"&amp;").replace(/</g,"&lt;").replace(/\x22/g, '&quot;').replace(/\x27/g,"&apos;") : "";
};

ZMTB_AjxStringUtil.xmlAttrDecode =
function(str) {
	return str ? str.replace(/&amp;/g,"&").replace(/&lt;/g,"<").replace(/&quot;/g, '"').replace(/&apos;/g,"'") : "";
};

ZMTB_AjxStringUtil.regExEscape =
function(str) {
	return str.replace(/(\W)/g, "\\$1");
};

ZMTB_AjxStringUtil._calcDIV = null; // used by 'clip()' and 'wrap()' functions

ZMTB_AjxStringUtil.calcDIV =
function() {
	if (ZMTB_AjxStringUtil._calcDIV == null) {
		ZMTB_AjxStringUtil._calcDIV = document.createElement("div");
		ZMTB_AjxStringUtil._calcDIV.style.zIndex = 0;
		ZMTB_AjxStringUtil._calcDIV.style.position = DwtControl.ABSOLUTE_STYLE;
		ZMTB_AjxStringUtil._calcDIV.style.visibility = "hidden";
		document.body.appendChild(ZMTB_AjxStringUtil._calcDIV);
	}
	return ZMTB_AjxStringUtil._calcDIV;
};

/**
 * Clips a string at "pixelWidth" using using "className" on hidden 'ZMTB_AjxStringUtil._calcDIV'.
 * Returns "origString" with "..." appended if clipped.
 *
 * NOTE: The same CSS style ("className") must be assigned to both the intended
 * display area and the hidden 'ZMTB_AjxStringUtil._calcDIV'.  "className" is
 * optional; if supplied, it will be assigned to 'ZMTB_AjxStringUtil._calcDIV' to
 * handle different CSS styles ("className"s) on same page.
 *
 * NOTE2: MSIE Benchmark - clipping an average of 17 characters each over 190
 * iterations averaged 27ms each (5.1 seconds total for 190)
 */
ZMTB_AjxStringUtil.clip =
function(origString, pixelWidth, className) {
	var calcDIV = ZMTB_AjxStringUtil.calcDIV();
	if (arguments.length == 3) calcDIV.className = className;
	//calcDIV.innerHTML = "<div>" + origString + "</div>"; // prevents screen flash in IE?
	calcDIV.innerHTML = origString;
	if (calcDIV.offsetWidth <= pixelWidth) return origString;

	for (var i=origString.length-1; i>0; i--) {
		var newString = origString.substr(0,i);
		calcDIV.innerHTML = newString + ZMTB_AjxStringUtil.ELLIPSIS;
		if (calcDIV.offsetWidth <= pixelWidth) return newString + ZMTB_AjxStringUtil.ELLIPSIS;
	}
	return origString;
};

/**
 * Forces a string to wrap at "pixelWidth" using "className" on hidden 'ZMTB_AjxStringUtil._calcDIV'.
 * Returns "origString" with "&lt;br&gt;" tags inserted to force wrapping.
 * Breaks string on embedded space characters, EOL ("/n") and "&lt;br&gt;" tags when possible.
 *
 * @returns		"origString" with "&lt;br&gt;" tags inserted to force wrapping.
 */
ZMTB_AjxStringUtil.wrap =
function(origString, pixelWidth, className) {
	var calcDIV = ZMTB_AjxStringUtil.calcDIV();
	if (arguments.length == 3) calcDIV.className = className;

	var newString = "";
	var newLine = "";
	textRows = origString.split("/n");
	for (var trCount=0; trCount<textRows.length; trCount++) {
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

// Regexes for finding non-quoted content
ZMTB_AjxStringUtil.MSG_SEP_RE = new RegExp("^\\s*--+\\s*(" + "Original Message" + "|" + "Forwarded Message" + ")\\s*--+", "i");
ZMTB_AjxStringUtil.SIG_RE = /^(- ?-+)|(__+)\r?$/;
ZMTB_AjxStringUtil.COLON_RE = /\S+:$/;
ZMTB_AjxStringUtil.PREFIX_RE = /^\s*(>|\|)/;
ZMTB_AjxStringUtil.BRACKET_RE = /^\s*\[.+\]\s*$/;
ZMTB_AjxStringUtil.LINE_RE = /^\s*_{30,}\s*$/;
ZMTB_AjxStringUtil.BLANK_RE = /^\s*$/;
ZMTB_AjxStringUtil.HDR_RE = /^\s*\w+:/;

/**
* Returns a list of chunks of top-level content in a message body. Top-level content is what was
* actually typed by the sender. We attempt to exclude quoted content and signatures.
*
* The following lines/blocks (and variants) and any text after them are ignored:
*
* 		----- Original Message -----
*
* 		----- Forwarded Message -----
*
*		--
*		some signature text
*
*		______________________________		|
*											| Outlook 2003 does this
*		From:								|
*
* Lines that begin with a prefix character ("&gt;" or "|") are ignored. The following
* lines/blocks are ignored if they precede a line that begins with a prefix character:
*
* 		Fred Flintstone <fred@bedrock.org> wrote:
*
* 		Fred Flintstone <fred@bedrock.org> wrote:
*		[snipped]
*
* Since quoted text may be interleaved with original text, we may return several chunks of
* original text. That is so they may be separated when they are quoted.
*
* @param text		a message body
* @param eol		the eol sequence, defaults to '\n'
*/
ZMTB_AjxStringUtil.getTopLevel =
function(text, eol) {
	eol = eol ? eol : '\n';
	text = ZMTB_AjxStringUtil._trimBlankLines(text, eol);
	var lines = text.split(eol);
	var len = lines.length;
	var i = 0, start = 0;
	var chunks = new Array();
	var skipping = false;
	while (i < len) {
		var wasSkipping = skipping;
		var skip = ZMTB_AjxStringUtil._linesToSkip(lines, i);
		skipping = (skip > 0);
		if (wasSkipping && !skipping)
			start = i;
		else if (!wasSkipping && skipping && i > start)
			chunks.push(ZMTB_AjxStringUtil._trimBlankLines(lines.slice(start, i).join(eol), eol) + eol);
		i += skipping ? skip : 1;
	}
	if (!skipping && i > start)
		chunks.push(ZMTB_AjxStringUtil._trimBlankLines(lines.slice(start, i).join(eol), eol) + eol);

	return chunks;
};

// Starting at a given line, returns the number of lines that should be skipped because
// they are quoted (or signature) content.
ZMTB_AjxStringUtil._linesToSkip =
function(lines, i) {
	var len = lines.length;
	var skip = 0;
	var start = i;
	if (ZMTB_AjxStringUtil.MSG_SEP_RE.test(lines[i])) {
		skip = len - i;
	} else if (ZMTB_AjxStringUtil.SIG_RE.test(lines[i])) {
		skip = len - i;
	} else if (ZMTB_AjxStringUtil.PREFIX_RE.test(lines[i])) {
		while (i < lines.length && (ZMTB_AjxStringUtil.PREFIX_RE.test(lines[i]) || ZMTB_AjxStringUtil.BLANK_RE.test(lines[i])))
			i++;
		skip = i - start;
	} else if (ZMTB_AjxStringUtil.COLON_RE.test(lines[i])) {
		var idx = ZMTB_AjxStringUtil._nextNonBlankLineIndex(lines, i + 1);
		var line1 = (idx != -1) ? lines[idx] : null;
		if (line1 && ZMTB_AjxStringUtil.PREFIX_RE.test(line1)) {
			skip = idx - i;
		} else {
			if (idx != -1)
				idx = ZMTB_AjxStringUtil._nextNonBlankLineIndex(lines, idx + 1);
			var line2 = (idx != -1) ? lines[idx] : null;
			if (line2 && ZMTB_AjxStringUtil.BRACKET_RE.test(line1) && ZMTB_AjxStringUtil.PREFIX_RE.test(line2))
				skip = idx - i;
		}
	} else if (ZMTB_AjxStringUtil.LINE_RE.test(lines[i])) {
		var idx = ZMTB_AjxStringUtil._nextNonBlankLineIndex(lines, i + 1);
		var line1 = (idx != -1) ? lines[idx] : null;
		if (line1 && ZMTB_AjxStringUtil.HDR_RE.test(line1))
			skip = len - i;
	}
	return skip;
};

// Returns the index of the next non-blank line
ZMTB_AjxStringUtil._nextNonBlankLineIndex =
function(lines, i) {
	while (i < lines.length && ZMTB_AjxStringUtil.BLANK_RE.test(lines[i]))
		i++;
	return ((i < lines.length) ? i : -1);
};

// Removes blank lines from the beginning and end of text
ZMTB_AjxStringUtil._trimBlankLines =
function(text, eol) {
	eol = eol ? eol : '\n';
	var lines = text.split(eol);
	var len = lines.length;
	var i = 0;
	while (i < len && ZMTB_AjxStringUtil.BLANK_RE.test(lines[i]))
		i++;
	var j = len;
	while (j > 0 && ZMTB_AjxStringUtil.BLANK_RE.test(lines[j - 1]))
		j--;
	if (i != 0 || j != len)
		text = lines.slice(i, j).join(eol) + eol;

	return text;
};

/**
* Converts a HTML document represented by a DOM tree  to text
*
* There has got to be a better way of doing this!
*/

ZMTB_AjxStringUtil._NO_LIST = 0;
ZMTB_AjxStringUtil._ORDERED_LIST = 1;
ZMTB_AjxStringUtil._UNORDERED_LIST = 2;
ZMTB_AjxStringUtil._INDENT = "    ";
ZMTB_AjxStringUtil._NON_WHITESPACE = /\S+/;
ZMTB_AjxStringUtil._LF = /\n/;

ZMTB_AjxStringUtil.convertHtml2Text =
function(domRoot) {
	if (!domRoot) return null;
    if (typeof domRoot == "string") {
        var domNode = document.createElement("SPAN");
        domNode.innerHTML = domRoot;
        domRoot = domNode;
    }
    var text = new Array();
	var idx = 0;
	var ctxt = new Object();
	this._traverse(domRoot, text, idx, ZMTB_AjxStringUtil._NO_LIST, 0, 0, ctxt);
	var textStr = text.join("");
	return textStr;
};

ZMTB_AjxStringUtil._traverse =
function(el, text, idx, listType, listLevel, bulletNum, ctxt) {
	var nodeName = el.nodeName.toLowerCase();

	if (nodeName == "#text") {
		if (el.nodeValue.search(ZMTB_AjxStringUtil._NON_WHITESPACE) != -1) {
			if (ctxt.lastNode == "ol" || ctxt.lastNode == "ul")
				text[idx++] = "\n";
			if (ctxt.isPreformatted)
				text[idx++] = ZMTB_AjxStringUtil.trim(el.nodeValue) + " ";
			else
				text[idx++] = ZMTB_AjxStringUtil.trim(el.nodeValue.replace(ZMTB_AjxStringUtil._LF, " "), true) + " ";
		}
	} else if (nodeName == "p") {
		text[idx++] = "\n\n";
	} else if (listType == ZMTB_AjxStringUtil._NO_LIST && (nodeName == "br" || nodeName == "hr")) {
		text[idx++] = "\n";
	} else if (nodeName == "ol" || nodeName == "ul") {
		text[idx++] = "\n";
		if (el.parentNode.nodeName.toLowerCase() != "li" && ctxt.lastNode != "br"
			&& ctxt.lastNode != "hr")
			text[idx++] = "\n";
		listType = (nodeName == "ol") ? ZMTB_AjxStringUtil._ORDERED_LIST : ZMTB_AjxStringUtil._UNORDERED_LIST;
		listLevel++;
		bulletNum = 0;
	} else if (nodeName == "li") {
		for (var i = 0; i < listLevel; i++)
			text[idx++] = ZMTB_AjxStringUtil._INDENT;
		if (listType == ZMTB_AjxStringUtil._ORDERED_LIST)
			text[idx++] = bulletNum + ". ";
		else
			text[idx++] = "\u2022 "; // TODO LmMsg.bullet
	} else if (nodeName == "img") {
		if (el.alt && el.alt != "")
			text[idx++] = el.alt;
	} else if (nodeName == "tr" && el.parentNode.firstChild != el) {
		text[idx++] = "\n";
	} else if (nodeName == "td" && el.parentNode.firstChild != el) {
		text[idx++] = "\t";
	} else if (nodeName == "div") {
		text[idx++] = "\n";
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
		if (nodeName == "ol" && tmp.nodeType == 1 && tmp.tagName.toLowerCase() == "li")
			bulletNum++;
		idx = this._traverse(tmp, text, idx, listType, listLevel, bulletNum, ctxt);
	}

	if (nodeName == "h1" || nodeName == "h2" || nodeName == "h3" || nodeName == "h4"
		|| nodeName == "h5" || nodeName == "h6") {
			text[idx++] = "\n";
			ctxt.list = false;
	} else if (nodeName == "pre") {
		ctxt.isPreformatted = false;
	} else if (nodeName == "li") {
		if (!ctxt.list)
			text[idx++] = "\n";
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
 * Sets the given name/value pair into the given query string.
 *
 * @param qs	[string]	a query string
 * @param name	[string]	arg name
 * @param value	[string]	arg value
 */
ZMTB_AjxStringUtil.queryStringSet =
function(qs, name, value) {
	qs = qs ? qs : "";
	if (qs.indexOf("?") == 0) {
		qs = qs.substr(1);
	}
	var pairs = qs.split("&");
	var pairs1 = [];
	var found = false;
	for (var i = 0; i < pairs.length; i++) {
		if (pairs[i].indexOf(name) == 0) {
			found = true;
			pairs1.push([name, value].join("="));
		} else {
			pairs1.push(pairs[i]);
		}
	}
	if (!found) {
		pairs1.push([name, value].join("="));
	}

	return "?" + pairs1.join("&");
};

/**
 * Removes the given arg from the query string.
 *
 * @param qs	[string]	a query string
 * @param name	[string]	arg name
 */
ZMTB_AjxStringUtil.queryStringRemove =
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
 * @param o		[object|primitive]		an object or primitive
 */
ZMTB_AjxStringUtil.getAsString =
function(o) {
	return !o ? "" : (typeof(o) == 'object') ? o.toString() : o;
};
