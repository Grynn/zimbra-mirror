/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2004, 2005, 2006, 2007, 2008, 2009, 2010 Zimbra, Inc.
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
 * Creates a new emal address, either by parsing an email string or from component parts.
 * @constructor
 * @class
 * This class represents an email address and defines some related constants. The class does not attempt full compliance
 * with RFC2822, so there are limitations for some of the edge cases.
 *
 * @author Conrad Damon
 * 
 * @param {string}	address		an email string, or just the address portion
 * @param {constant}	type		from, to, cc, bcc, or reply-to
 * @param {string}	name		the personal name portion
 * @param {string}	dispName	a brief display version of the name
 * @param {boolean}	isGroup		if <code>true</code>, the address param is really a list of email addresses
 * 
 */
AjxEmailAddress = function(address, type, name, dispName, isGroup) {
	this.address = address;
	this.name = this._setName(name);
	this.dispName = dispName;
	this.type = type || AjxEmailAddress.TO;
	this.isGroup = isGroup;
	this.canExpand = false;
};

AjxEmailAddress.prototype.isAjxEmailAddress = true;
/**
 * Defines list of custom invalid RegEx patterns that are set in LDAP
 */
AjxEmailAddress.customInvalidEmailPats = [];

/**
 * Defines the "from" type.
 */
AjxEmailAddress.FROM		= "FROM";
/**
 * Defines the "to" type.
 */
AjxEmailAddress.TO			= "TO";
/**
 * Defines the "cc" type.
 */
AjxEmailAddress.CC			= "CC";
/**
 * Defines the "bcc" type.
 */
AjxEmailAddress.BCC			= "BCC";
AjxEmailAddress.REPLY_TO	= "REPLY_TO";
AjxEmailAddress.SENDER		= "SENDER";
AjxEmailAddress.READ_RECEIPT= "READ_RECEIPT";
AjxEmailAddress.RESENT_FROM = "RESENT_FROM";

AjxEmailAddress.TYPE_STRING = {};
AjxEmailAddress.TYPE_STRING[AjxEmailAddress.FROM]			= "from";
AjxEmailAddress.TYPE_STRING[AjxEmailAddress.TO]				= "to";
AjxEmailAddress.TYPE_STRING[AjxEmailAddress.CC]				= "cc";
AjxEmailAddress.TYPE_STRING[AjxEmailAddress.BCC]			= "bcc";
AjxEmailAddress.TYPE_STRING[AjxEmailAddress.REPLY_TO]		= "replyTo";
AjxEmailAddress.TYPE_STRING[AjxEmailAddress.SENDER]			= "sender";
AjxEmailAddress.TYPE_STRING[AjxEmailAddress.READ_RECEIPT]	= "readReceipt";
AjxEmailAddress.TYPE_STRING[AjxEmailAddress.RESENT_FROM]	= "resentFrom";

AjxEmailAddress.fromSoapType = {};
AjxEmailAddress.fromSoapType["f"]  = AjxEmailAddress.FROM;
AjxEmailAddress.fromSoapType["t"]  = AjxEmailAddress.TO;
AjxEmailAddress.fromSoapType["c"]  = AjxEmailAddress.CC;
AjxEmailAddress.fromSoapType["b"]  = AjxEmailAddress.BCC;
AjxEmailAddress.fromSoapType["r"]  = AjxEmailAddress.REPLY_TO;
AjxEmailAddress.fromSoapType["s"]  = AjxEmailAddress.SENDER;
AjxEmailAddress.fromSoapType["n"]  = AjxEmailAddress.READ_RECEIPT;
AjxEmailAddress.fromSoapType["rf"] = AjxEmailAddress.RESENT_FROM;

AjxEmailAddress.toSoapType = {};
AjxEmailAddress.toSoapType[AjxEmailAddress.FROM]		= "f";
AjxEmailAddress.toSoapType[AjxEmailAddress.TO]			= "t";
AjxEmailAddress.toSoapType[AjxEmailAddress.CC]			= "c";
AjxEmailAddress.toSoapType[AjxEmailAddress.BCC]			= "b";
AjxEmailAddress.toSoapType[AjxEmailAddress.REPLY_TO]	= "r";
AjxEmailAddress.toSoapType[AjxEmailAddress.SENDER]		= "s";
AjxEmailAddress.toSoapType[AjxEmailAddress.READ_RECEIPT]= "n";

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
 * atoms.
 * <p>
 * If the address parses successfully, the current object's properties will be set.
 * </p>
 * 
 * @param	{string}	str		the string to parse
 * @return	{AjxEmailAddress}	the email address or <code>null</code>
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
			if (parts[1] == '"') {
				return null;	// unmatched quote
			}
            //AjxEmailAddress.addrPat recognizes the email better than using parts[0] from AjxEmailAddress.addrPat1
            addr = str.match(AjxEmailAddress.addrPat);
            addr = (addr && addr.length && addr[0] != "") ? AjxStringUtil.trim(addr[0]) : parts[0];
			if (addr && addr.indexOf("..") != -1) {
				return null;
			}
			str = str.replace(AjxEmailAddress.addrPat, '');
		}
	}
 	if (!addr) {
		return null;
	}
	//Invalidate if address matches any of AjxEmailAddress.customInvalidEmailPats
	for(var i = 0; i< AjxEmailAddress.customInvalidEmailPats.length; i++) {
	   var match = addr.match(AjxEmailAddress.customInvalidEmailPats[i]);
		if(match) {
			return null;
		}
	}

	// What remains is the name
	parts = str.match(AjxEmailAddress.phrasePat);
	if (parts) {
		name = AjxStringUtil.trim(parts[0]);

		// Trim off leading and trailing quotes, but leave escaped quotes and unescape them
		name = name.replace(/\\"/g,"&quot;");
		name = AjxStringUtil.trim(name, null, "\"");
		name = name.replace(/&quot;/g,"\"");
	}
	
	return new AjxEmailAddress(addr, null, name);
};

/**
 * Parses a string with one or more addresses and parses it. An object with lists of good addresses, bad
 * addresses, and all addresses is returned. Strict RFC822 validation (at least as far as it goes in the
 * regexes we have) is optional. If it's off, we'll retry a failed address after quoting the personal part.
 *
 * @param {string}	emailStr	an email string with one or more addresses
 * @param {constant}	type		address type of the string
 * @param {boolean}	strict		if <code>true</code>, do strict checking
 * @return	{hash}		the good/bad/all addresses
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
 * Checks if a string to see if it's a valid email string according to our mailbox pattern.
 *
 * @param {string}	str		an email string
 * @return	{boolean}	<code>true</code> if the string is valid
 */
AjxEmailAddress.isValid = function(str) {
	return AjxEmailAddress.parse(str) != null;
};

AjxEmailAddress._prelimCheck =
function(str) {
	// Do preliminary check for @ since we don't support local addresses, and as workaround for Mozilla bug
	// https://bugzilla.mozilla.org/show_bug.cgi?id=225094
	// Also check for . since we require FQDN
	var atIndex = str.indexOf('@');
	var dotIndex = str.lastIndexOf('.');
	return ((atIndex != -1) && (dotIndex != -1) && (dotIndex > atIndex) && (dotIndex != str.length - 1));
};

/**
 * Splits a string into (possible) email address strings based on delimiters. Tries to
 * be flexible about what it will accept. The following delimiters are recognized, under
 * the following conditions:
 *
 * <ul>
 * <li><i>return</i> -- always</li>
 * <li><i>semicolon</i> -- must not be inside quoted or comment text</li>
 * <li><i>comma</i> -- must not be inside quoted or comment text, and must follow an address (which may be in angle brackets)</li>
 * <li><i>space</i> -- can only separate plain addresses (no quoted or comment text)</li>
 * </ul>
 * 
 * The requirement that a comma follow an address allows us to be lenient when a mailer
 * doesn't quote the friendly part, so that a string such as the one below is split correctly:
 * <code>Smith, John &lt;jsmith@aol.com&gt;</code>
 *
 * @param {string}	str	the string to be split
 * @return	{array}	the list of {String} addresses
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
						addrList.push(AjxStringUtil.trim(test));
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
			addrList.push(AjxStringUtil.trim(sub));
			startPos += sub.length + 1;
		}
	}
	AjxEmailAddress.IS_DELIM[" "] = true;

	return addrList;
};

/**
 * Returns a string representation of this object.
 * 
 * @param {boolean}		shortForm	if true, return a brief version (name if available, otherwise email)
 * @param {boolean}		forceUnescape	if true, name will not be in quotes and any quotes inside the name will be unescaped (e.g. "John \"JD\" Doe" <jd@zimbra.com> becomes John "JD" Doe <jd@zimbra.com>
 * 
 * @return	{string}		a string representation of this object
 */
AjxEmailAddress.prototype.toString =
function(shortForm, forceUnescape) {

	if (this.name) {
		var name = this.name;
		if (!shortForm && !forceUnescape) {
			name = name.replace(/\\+"/g, '"');	// unescape double quotes (avoid double-escaping)
			name = name.replace(/"/g,'\\"');  // escape quotes
		}
		var buffer = (shortForm || forceUnescape) ? [name] : ['"', name, '"'];
		if (this.address && !shortForm) {
			buffer.push(" <", this.address, ">");
		}
		return buffer.join("");	// quote friendly part
	} else {
		return this.address;
	}
};

/**
 * Gets the address.
 * 
 * @return	{string}	the address
 */
AjxEmailAddress.prototype.getAddress =
function() {
	return this.address;
};

/**
 * Sets the address.
 * 
 * @param	{string}	addr		the address
 */
AjxEmailAddress.prototype.setAddress =
function(addr) {
	this.address = addr;
};

/**
 * Gets the type (to/from/cc/bcc).
 * 
 * @return	{constant}	the type
 */
AjxEmailAddress.prototype.getType =
function() {
	return this.type;
};

/**
 * Sets the type.
 * 
 * @param	{constant}	type		the type (to/from/cc/bcc)
 */
AjxEmailAddress.prototype.setType =
function(type) {
	this.type = type;
};

/**
 * Gets the type as a string.
 * 
 * @return	{string}	the type (to/from/cc/bcc)
 */
AjxEmailAddress.prototype.getTypeAsString =
function() {
	return AjxEmailAddress.TYPE_STRING[this.type];
};

/**
 * Gets the name.
 * 
 * @return	{string}	the name
 */
AjxEmailAddress.prototype.getName =
function() {
	return this.name;
};

/**
 * Gets the display name.
 * 
 * @return	{string}	the name
 */
AjxEmailAddress.prototype.getDispName =
function() {
	return this.dispName;
};

/**
 * Clones this email address.
 * 
 * @return	{AjxEmailAddress}	a clone of this email address
 */
AjxEmailAddress.prototype.clone =
function() {
	var addr = new AjxEmailAddress(this.address, this.type, this.name, this.dispName);
	addr.icon = this.icon;
	addr.isGroup = this.isGroup;
	addr.canExpand = this.canExpand;
	return addr;
};

/**
 * Copies the email address.
 * 
 * @param	{AjxEmailAddress}	obj		the email to copy
 * @return	{AjxEmailAddress}	the newly copied email address
 */
AjxEmailAddress.copy =
function(obj){    
    var addr = new AjxEmailAddress(obj.address, obj.type, obj.name, obj.dispName);
    addr.icon = obj.icon;
	addr.isGroup = obj.isGroup;
	addr.canExpand = obj.canExpand;
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

AjxEmailAddress.sortCompareByAddress =
function(a, b) {

	var addrA = a.getAddress() || "";
	var addrB = b.getAddress() || "";
	if (addrA.toLowerCase() > addrB.toLowerCase()) { return 1; }
	if (addrA.toLowerCase() < addrB.toLowerCase()) { return -1; }
	return 0;
};
