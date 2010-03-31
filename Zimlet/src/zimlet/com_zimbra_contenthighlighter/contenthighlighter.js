/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Zimlets
 * Copyright (C) 2009, 2010 Zimbra, Inc.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.3 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 * @Author Raja Rao DV (rrao@zimbra.com)
 * Highlights email contents to differentiate b/w original, Replied and Forwarded parts
 */

function com_zimbra_contenthltr() {
}

com_zimbra_contenthltr.prototype = new ZmZimletBase();
com_zimbra_contenthltr.prototype.constructor = com_zimbra_contenthltr;


com_zimbra_contenthltr.prototype.init =
function(line, startIndex) {
	this._reg1 = new RegExp("[>\\|]");
	this._reg2 = new RegExp("^[>\\|]\\s?[>\\|]");
	this._reg3 = new RegExp("^[>\\|]\\s?[>\\|]\\s?[>\\|]");
	this._reg4 = new RegExp("^[>\\|]\\s?[>\\|]\\s?[>\\|]\\s?[>\\|]");
};

com_zimbra_contenthltr.prototype.match =
function(line, startIndex) {
	var a = [new RegExp("^[>\|].*","ig")];
	var ret = null;
	for (var i = 0; i < a.length; ++i) {
		var re = a[i];
		re.lastIndex = startIndex;
		var m = re.exec(line);
        if (m) {
            if (!ret || m.index < ret.index) {
                ret = m;
            }
        }
	}
	return ret;	
};

com_zimbra_contenthltr.prototype.generateSpan =
function(html, idx, obj, spanId, context) {
	var id = Dwt.getNextId();

	if(this._reg4.exec(obj)) {
		html[idx++] = ["<span id= '",id,"' style='color:darkBlue'>",obj,"</span>"].join("");	
	} else if(this._reg3.exec(obj)) {
		html[idx++] = ["<span id= '",id,"' style='color:brown'>",obj,"</span>"].join("");	
	} else 	if(this._reg2.exec(obj)) {
		html[idx++] = ["<span id= '",id,"' style='color:darkGreen'>",obj,"</span>"].join("");	
	} else {
		html[idx++] = ["<span id= '",id,"' style='color:blue'>",obj,"</span>"].join("");	
	}
	return idx;
};
