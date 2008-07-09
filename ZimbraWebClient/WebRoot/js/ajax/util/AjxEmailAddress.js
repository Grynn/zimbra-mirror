/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2004, 2005, 2006, 2007 Zimbra, Inc.
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
* Creates a new AjxEmailAddress, either by parsing an email string or from component parts.
* @constructor
* @class
* This class represents an email address and defines some related constants. The class does not attempt full compliance
* with RFC2822, so there are limitations for some of the edge cases.
*
* @author Conrad Damon
* @param address	[string]		an email string, or just the address portion
* @param type		[constant]*		from, to, cc, bcc, or reply-to
* @param name		[string]*		the personal name portion
* @param dispName	[string]*		a brief display version of the name
* @param isGroup	[boolean]*		whether the address param is really a list of email addresses
*/
AjxEmailAddress = function(address, type, name, dispName, isGroup) {
	this.address = address;
	this.name = this._setName(name);
	this.dispName = dispName;
	this.type = type || AjxEmailAddress.TO;
	this.isGroup = isGroup;
};

AjxEmailAddress.FROM		= "FROM";
AjxEmailAddress.TO			= "TO";
AjxEmailAddress.CC			= "CC";
AjxEmailAddress.BCC			= "BCC";
AjxEmailAddress.REPLY_TO	= "REPLY_TO";
AjxEmailAddress.SENDER		= "SENDER";

AjxEmailAddress.TYPE_STRING = {};
AjxEmailAddress.TYPE_STRING[AjxEmailAddress.FROM]		= "from";
AjxEmailAddress.TYPE_STRING[AjxEmailAddress.TO]			= "to";
AjxEmailAddress.TYPE_STRING[AjxEmailAddress.CC]			= "cc";
AjxEmailAddress.TYPE_STRING[AjxEmailAddress.BCC]		= "bcc";
AjxEmailAddress.TYPE_STRING[AjxEmailAddress.REPLY_TO]	= "replyTo";
AjxEmailAddress.TYPE_STRING[AjxEmailAddress.SENDER]		= "sender";

AjxEmailAddress.fromSoapType = {};
AjxEmailAddress.fromSoapType["f"] = AjxEmailAddress.FROM;
AjxEmailAddress.fromSoapType["t"] = AjxEmailAddress.TO;
AjxEmailAddress.fromSoapType["c"] = AjxEmailAddress.CC;
AjxEmailAddress.fromSoapType["b"] = AjxEmailAddress.BCC;
AjxEmailAddress.fromSoapType["r"] = AjxEmailAddress.REPLY_TO;
AjxEmailAddress.fromSoapType["s"] = AjxEmailAddress.SENDER;

AjxEmailAddress.toSoapType = {};
AjxEmailAddress.toSoapType[AjxEmailAddress.FROM]		= "f";
AjxEmailAddress.toSoapType[AjxEmailAddress.TO]			= "t";
AjxEmailAddress.toSoapType[AjxEmailAddress.CC]			= "c";
AjxEmailAddress.toSoapType[AjxEmailAddress.BCC]			= "b";
AjxEmailAddress.toSoapType[AjxEmailAddress.REPLY_TO]	= "r";
AjxEmailAddress.toSoapType[AjxEmailAddress.SENDER]		= "s";

AjxEmailAddress.SEPARATOR = "; ";				// used to join addresses
AjxEmailAddress.DELIMS = [';', ',', '\n', ' '];	// recognized as address delimiters
AjxEmailAddress.IS_DELIM = {};
for (var i = 0; i < AjxEmailAddress.DELIMS.length; i++) {
	AjxEmailAddress.IS_DELIM[AjxEmailAddress.DELIMS[i]] = true;
}

// validation patterns

AjxEmailAddress.addrAnglePat = /(\s*<(((\s*([^\x00-\x1F\x7F()<>\[\]:;@\,."\s]+(\.[^\x00-\x1F\x7F()<>\[\]:;@\,."\s]+)*)\s*)|(\s*"(([^\\"])|(\\([^\x0A\x0D])))+"\s*))\@((\s*([^\x00-\x1F\x7F()<>\[\]:;@\,."\s]+(\.[^\x00-\x1F\x7F()<>\[\]:;@\,."\s]+)*)\s*)|(\s*\[(\s*(([^\[\]\\])|(\\([^\x0A\x0D])))+)*\s*\]\s*)))>\s*)/;
AjxEmailAddress.addrAngleQuotePat = /(\s*<'(((\s*([^\x00-\x1F\x7F()<>\[\]:;@\,."\s]+(\.[^\x00-\x1F\x7F()<>\[\]:;@\,."\s]+)*)\s*)|(\s*"(([^\\"])|(\\([^\x0A\x0D])))+"\s*))\@((\s*([^\x00-\x1F\x7F()<>\[\]:;@\,."\s]+(\.[^\x00-\x1F\x7F()<>\[\]:;@\,."\s]+)*)\s*)|(\s*\[(\s*(([^\[\]\\])|(\\([^\x0A\x0D])))+)*\s*\]\s*)))'>\s*)/;
// use addrPat to validate strings as email addresses
AjxEmailAddress.addrPat = /(((\s*([^\x00-\x1F\x7F()<>\[\]:;@\,."\s]+(\.[^\x00-\x1F\x7F()<>\[\]:;@\,."\s]+)*)\s*)|(\s*"(([^\\"])|(\\([^\x0A\x0D])))+"\s*))\@((\s*([^\x00-\x1F\x7F()<>\[\]:;@\,."\s]+(\.[^\x00-\x1F\x7F()<>\[\]:;@\,."\s]+)*)\s*)|(\s*\[(\s*(([^\[\]\\])|(\\([^\x0A\x0D])))+)*\s*\]\s*)))/;
// use addrPat1 to parse email addresses - pattern is lenient in that it will allow the following:
// 		"Joe Smith" joe@x.com
//		"Joe Smith"joe@x.com
// (RFC822 wants the address part to be in <> if preceded by name part)
AjxEmailAddress.addrPat1 = /(^|"|\s)(((\s*([^\x00-\x1F\x7F()<>\[\]:;@\,."\s]+(\.[^\x00-\x1F\x7F()<>\[\]:;@\,."\s]+)*)\s*)|(\s*"(([^\\"])|(\\([^\x0A\x0D])))+"\s*))\@((\s*([^\x00-\x1F\x7F()<>\[\]:;@\,."\s]+(\.[^\x00-\x1F\x7F()<>\[\]:;@\,."\s]+)*)\s*)|(\s*\[(\s*(([^\[\]\\])|(\\([^\x0A\x0D])))+)*\s*\]\s*)))/;
// pattern below is for account part of address (before @)
AjxEmailAddress.accountPat = /((\s*([^\x00-\x1F\x7F()<>\[\]:;@\,."\s]+(\.[^\x00-\x1F\x7F()<>\[\]:;@\,."\s]+)*)\s*)|(\s*"(([^\\"])|(\\([^\x0A\x0D])))+"\s*))/;
// Pattern below hangs on an unclosed comment, so use simpler one if parsing for comments
//AjxEmailAddress.commentPat = /(\s*\((\s*(([^()\\])|(\\([^\x0A\x0D]))|(\s*\((\s*(([^()\\])|(\\([^\x0A\x0D]))|(\s*\((\s*(([^()\\])|(\\([^\x0A\x0D]))|(\s*\((\s*(([^()\\])|(\\([^\x0A\x0D]))|(\s*\((\s*(([^()\\])|(\\([^\x0A\x0D]))|)+)*\s*\)\s*))+)*\s*\)\s*))+)*\s*\)\s*))+)*\s*\)\s*))+)*\s*\)\s*)/;
AjxEmailAddress.commentPat = /\((.*)\)/g;
AjxEmailAddress.phrasePat = /(((\s*[^\x00-\x1F\x7F()<>\[\]:;@\"\s]+\s*)|(\s*"(([^\\"])|(\\([^\x0A\x0D])))+"\s*))+)/;
AjxEmailAddress.boundAddrPat = /(\s*<?(((\s*([^\x00-\x1F\x7F()<>\[\]:;@\,."\s]+(\.[^\x00-\x1F\x7F()<>\[\]:;@\,."\s]+)*)\s*)|(\s*"(([^\\"])|(\\([^\x0A\x0D])))+"\s*))\@((\s*([^\x00-\x1F\x7F()<>\[\]:;@\,."\s]+(\.[^\x00-\x1F\x7F()<>\[\]:;@\,."\s]+)*)\s*)|(\s*\[(\s*(([^\[\]\\])|(\\([^\x0A\x0D])))+)*\s*\]\s*)))>?\s*)$/;

/**
* Parses an email address string into its component parts. The parsing is adapted from the perl module 
* <a href="http://search.cpan.org/~cwest/Email-Address-1.2/lib/Email/Address.pm">Email::Address</a>. Check that out if you
* want to know how the gory regexes that do the parsing were built. They are based on RFC2822, but don't represent a full 
* implementation. We don't really need or want that, since we don't want to be overly restrictive or bloated. It was easier
* to just use the resulting regexes from the Perl module, rather than go through all the rigmarole of building them up from
* atoms. Plus, I get to use the word "rigmarole".
* <p>
* If the address parses successfully, the current object's properties will be set.</p>
*/
AjxEmailAddress.parse =
function(str) {
	var addr, name;
	var str = AjxStringUtil.trim(str);
	var prelimOkay = AjxEmailAddress._prelimCheck(str);
	if (!(prelimOkay && str.match(AjxEmailAddress.addrPat))) {
		DBG.println(AjxDebug.DBG2, "mailbox match failed: " + str);
		return null;
	}

	// Note: It would be nice if you could get back the matching parenthesized subexpressions from replace,
	// then we wouldn't have to do both a match and a replace. The parsing works by removing parts after it
	// finds them.
	
	// First find the address (and remove it)
	var parts = str.match(AjxEmailAddress.addrAngleQuotePat) || str.match(AjxEmailAddress.addrAnglePat);
	if (parts && parts.length) {
		addr = parts[2];
		str = str.replace(AjxEmailAddress.addrAnglePat, '');
	} else {
		parts = str.match(AjxEmailAddress.addrPat1);
		if (parts && parts.length) {
			addr = parts[0];
			str = str.replace(AjxEmailAddress.addrPat, '');
		}
	}
	
	// What remains is the name
	parts = str.match(AjxEmailAddress.phrasePat);
	if (parts) {
		name = AjxStringUtil.trim(AjxStringUtil.trim(parts[0]), false, '"');
	}
	
	return new AjxEmailAddress(addr, null, name);
};

/**
* Takes a string with one or more addresses and parses it. An object with lists of good addresses, bad
* addresses, and all addresses is returned. Strict RFC822 validation (at least as far as it goes in the
* regexes we have) is optional. If it's off, we'll retry a failed address after quoting the personal part.
*
* @param emailStr	[string]	an email string with one or more addresses
* @param type		[constant]	address type of the string
* @param strict		[boolean]*	if true, do strict checking
*/
AjxEmailAddress.parseEmailString =
function(emailStr, type, strict) {
	var good = new AjxVector();
	var bad = new AjxVector();
	var all = new AjxVector();
	var addrList = AjxEmailAddress.split(emailStr);
	for (var i = 0; i < addrList.length; i++) {
		var addrStr = AjxStringUtil.trim(addrList[i]);
		if (addrStr) {
			var addr = AjxEmailAddress.parse(addrStr);
			if (!addr && !strict) {
				var temp = addrStr;
				var parts = temp.match(AjxEmailAddress.addrAnglePat);
				if (parts && parts.length) {
					var name = temp.replace(AjxEmailAddress.addrAnglePat, '');
					var newAddr = ['"', name, '" ', parts[0]].join("");
					addr = AjxEmailAddress.parse(newAddr);
					if (addr) {
						addr.name = name; // reset name to original unquoted form
					}
				}
			}
			if (addr) {
				addr.type = type;
				good.add(addr);
				all.add(addr);
			} else {
				bad.add(addrStr);
				all.add(new AjxEmailAddress(addrStr));
			}
		}
	}
	return {good: good, bad: bad, all: all};
};

/**
* Tests a string to see if it's a valid email string according to our mailbox pattern.
*
* @param str		an email string
*/
AjxEmailAddress.isValid =
function(str) {
	str = AjxStringUtil.trim(str);
	var prelimOkay = AjxEmailAddress._prelimCheck(str);
	return (prelimOkay && (str.match(AjxEmailAddress.addrPat) != null));
};

AjxEmailAddress._prelimCheck =
function(str) {
	// Do preliminary check for @ since we don't support local addresses, and as workaround for Mozilla bug
	// https://bugzilla.mozilla.org/show_bug.cgi?id=225094
	// Also check for . since we require FQDN
	var atIndex = str.indexOf('@');
	var dotIndex = str.lastIndexOf('.');
	return ((atIndex != -1) && (dotIndex != -1) && (dotIndex > atIndex));
};

/**
* Splits a string into (possible) email address strings based on delimiters. Tries to
* be flexible about what it will accept. The following delimiters are recognized, under
* the following conditions:
*
* <p><pre>
* return		always
* semicolon		must not be inside quoted or comment text
* comma			must not be inside quoted or comment text, and must follow an address (which
*				may be in angle brackets)
* space			can only separate plain addresses (no quoted or comment text)
* </pre></p>
* <p>
* The requirement that a comma follow an address allows us to be lenient when a mailer
* doesn't quote the friendly part, so that a string such as the one below is split correctly:
* 	<code>Smith, John &lt;jsmith@aol.com&gt;</code>
* </p>
*
* @param str	the string to be split
*/
AjxEmailAddress.split =
function(str) {
	str = AjxStringUtil.trim(str);
	// first, construct a list of ranges to ignore because they are quoted or comment text
	var ignore = [];
	var pos = 0, startPos = 0;
	var prevCh = "", startCh = "";
	var inside = false;
	while (pos < str.length) {
		var ch = str.charAt(pos);
		if ((ch == '"' || ch == '(') && prevCh != "\\") {
			inside = true;
			startCh = ch;
			startPos = pos;
			pos++;
			while (inside && pos < str.length) {
				var ch = str.charAt(pos);
				if (((startCh == '"' && ch == '"') || (startCh == '(' && ch == ')')) && (prevCh != "\\")) {
					ignore.push({start: startPos, end: pos});
					inside = false;
				}
				pos++;
				prevCh = ch;
			}
		} else {
			pos++;
		}
		prevCh = ch;
	}
	if (ignore.length) {
		AjxEmailAddress.IS_DELIM[" "] = false;
	}
	
	// Progressively scan the string for delimiters. Once an email string has been found, continue with
	// the remainder of the original string.
	startPos = 0;
	var addrList = [];
	while (startPos < str.length) {
		var sub = str.substring(startPos, str.length);
		pos = 0;
		var delimPos = sub.length;
		while ((delimPos == sub.length) && (pos < sub.length)) {
			var ch = sub.charAt(pos);
			if (AjxEmailAddress.IS_DELIM[ch]) {
				var doIgnore = false;
				if (ch != "\n") {
					for (var i = 0; i < ignore.length; i++) {
						var range = ignore[i];
						var absPos = startPos + pos;
						doIgnore = (absPos >= range.start && absPos <= range.end);
						if (doIgnore) break;
					}
				}
				if (!doIgnore) {
					var doAdd = true;
					var test = sub.substring(0, pos);
					if (ch == "," || ch == " ") {
						// comma/space allowed as non-delimeter outside quote/comment,
						// so we make sure it follows an actual address
						doAdd = test.match(AjxEmailAddress.boundAddrPat);
					}
					if (doAdd) {
						addrList.push(test);
						delimPos = pos;
						startPos += test.length + 1;
					}
				}
				// strip extra delimeters
				ch = str.charAt(startPos);
				while ((startPos < str.length) && AjxEmailAddress.IS_DELIM[ch]) {
					startPos++;
					ch = str.charAt(startPos);
				}
				pos++;
			} else {
				pos++;
			}
		}
		if (delimPos == sub.length) {
			addrList.push(sub);
			startPos += sub.length + 1;
		}
	}
	AjxEmailAddress.IS_DELIM[" "] = true;

	return addrList;
};

AjxEmailAddress.prototype.toString =
function() {
	if (this.name && !this.isGroup) {
		var name = this.name.replace(/\\+"/g, '"');	// unescape double quotes (avoid double-escaping)
		name = name.replace(/"/g, '\\"');			// escape double quotes
		return ['"', name, '" <', this.address, ">"].join("");	// quote friendly part
	} else {
		return this.address;
	}
};

AjxEmailAddress.prototype.getAddress =
function() {
	return this.address;
};

AjxEmailAddress.prototype.setAddress =
function(addr) {
	this.address = addr;
};

AjxEmailAddress.prototype.getType =
function() {
	return this.type;
};

AjxEmailAddress.prototype.setType =
function(type) {
	this.type = type;
};

AjxEmailAddress.prototype.getTypeAsString =
function() {
	return AjxEmailAddress.TYPE_STRING[this.type];
};

AjxEmailAddress.prototype.getName =
function() {
	return this.name;
};

AjxEmailAddress.prototype.getDispName =
function() {
	return this.dispName;
};

AjxEmailAddress.prototype.clone =
function() {
	var addr = new AjxEmailAddress(this.address, this.type, this.name, this.dispName);
	addr.icon = this.icon;
	return addr;
};

AjxEmailAddress.prototype._setName =
function(name) {
	if (!name) return "";
	
	// remove wrapping single quotes from name if present
	if (name && name.charAt(0) == "'" && name.charAt(name.length - 1) == "'")
		name = name.substring(1, name.length - 1);
		
	return name;		
};
