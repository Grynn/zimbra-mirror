/*
 * ***** BEGIN LICENSE BLOCK *****
 * Version: ZPL 1.1
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.1 ("License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.zimbra.com/license
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See
 * the License for the specific language governing rights and limitations
 * under the License.
 * 
 * The Original Code is: Zimbra Collaboration Suite Web Client
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
* @class ZaItem
* @param app reference to the application instance
**/
function ZaItem(app) {
	if (arguments.length == 0) return;
	this._app = app;
	ZaModel.call(this, true);

}

ZaItem.prototype = new ZaModel;
ZaItem.prototype.constructor = ZaItem;

ZaItem.ACCOUNT = "account";
ZaItem.DL = "dl";
ZaItem.ALIAS = "alias";
ZaItem.DOMAIN = "domain";
ZaItem.COS = "cos";
ZaItem.SERVER = "server";

ZaItem.A_objectClass = "objectClass";
ZaItem.A_zimbraId = "zimbraId";
ZaItem.compareNamesAsc = 
function(a,b) {
	var al = a.name.toLowerCase();
	var bl = b.name.toLowerCase();

	if (al < bl)
		return -1;
	if (al > bl)
		return 1;
	else
		return 0;
}

ZaItem.compareNamesDesc = 
function(a,b) {
	var al = a.name.toLowerCase();
	var bl = b.name.toLowerCase();

	if (al < bl)
		return 1;
	if (al > bl)
		return -1;
	else
		return 0;
}

ZaItem.compareDescription = 
function(a,b) {
	return ZaItem.compareAttr(a,b,"description");
}

ZaItem.compareAttr = 
function(a, b, attr) {
	if (a.attrs[attr] < b.attrs[attr])
		return -1;
	if (a.attrs[attr] > b.attrs[attr])
		return 1;
	else
		return 0;
}

/**
* Item Factory
**/
ZaItem.getFromType = 
function (type, app) {
	switch (type) {
		case ZaItem.ACCOUNT:
			return new ZaAccount(app);
		break;
		case ZaItem.ALIAS:
			return new ZaAlias(app);
		break;
		case ZaItem.DL:
			return new ZaDistributionList(app);
		break;
		case ZaItem.DOMAIN:
			return new ZaDomain(app);
		break;
		case ZaItem.COS:
			return new ZaCos(app);
		break;
		case ZaItem.SERVER:
			return new ZaServer(app);
		break;
	}
}

ZaItem.prototype.toString = 
function() {
	return "ZaItem "+this.type+": name="+this.name+" id="+this.id;
}

ZaItem.prototype.remove = 
function () {
	//abstract
}
/**
	full recursion copy
	don't use on objects with many references
**/

/*ZaItem._copyTo = function (targetObj) {
	if(this instaceof Array) {
		if(!targetObj)
			targetObj = new Array();
			
		var cnt = this.length;
		for (var ix = 0; ix < cnt; ix++) {
			ZaItem._copyTo.call(this[ix], targetObj[ix]);
		}
	} else if (typeof(this) == "object")) {
		if(!targetObj)
			targetObj = new Object();
				
		for(var i in this) {
			if(this[i] == null)
				continue;
		
			if(typeof(this[i]) == "object") {
				ZaItem._copyTo.call(this[i], targetObj[i]);
			} else if (typeof(this[i] == "function")) {
				continue;
			} else {
				targetObj[i] = this[i];
			}
		}
	}
}*/

ZaItem.prototype.copyTo = 
function (target/*, fullRecursion*/) {
	for(var a in this) {
		target[a] = this[a];
	}

/*	if(!fullRecursion) {
		for(var a in this) {
			target[a] = this[a];
		}
	} else {
		ZaItem._copyTo.call(this, target);
	}
*/	
}

ZaItem.prototype.initFromDom =
function(node) {
	this.name = node.getAttribute("name");
	this.id = node.getAttribute("id");
	this.attrs = new Object();
	this.type = node.nodeName;
	
	var children = node.childNodes;
	for (var i=0; i< children.length;  i++) {
		var child = children[i];
		if (child.nodeName != 'a') continue;
		var name = child.getAttribute("n");
		if (child.firstChild != null) {
			var value = child.firstChild.nodeValue;
			if (name in this.attrs) {
				var vc = this.attrs[name];
				if ((typeof vc) == "object") {
					vc.push(value);
				} else {
					this.attrs[name] = [vc, value];
				}
			} else {
				this.attrs[name] = value;
			}
		}
	}
}

// Adds a row to the tool tip.
ZaItem.prototype._addRow =
function(msg, value, html, idx) {
	if (value != null) {
		html[idx++] = "<tr valign='top'><td align='right' style='padding-right: 5px;'><b>";
		html[idx++] = AjxStringUtil.htmlEncode(msg) + ":";
		html[idx++] = "</b></td><td align='left'><div style='white-space:nowrap; overflow:hidden;'>";
		html[idx++] = AjxStringUtil.htmlEncode(value);
		html[idx++] = "</div></td></tr>";
	}
	return idx;
}

// Adds a row to the tool tip.
ZaItem.prototype._addAttrRow =
function(name, html, idx) {
	var value = this.attrs[name];
	if (value != null) {
		var desc = ZaItem._attrDesc(name);
		html[idx++] = "<tr valign='top'><td align='left' style='padding-right: 5px;'><b>";
		html[idx++] = AjxStringUtil.htmlEncode(desc) + ":";
		html[idx++] = "</b></td><td align='left'><div style='white-space:nowrap; overflow:hidden;'>";
		html[idx++] = AjxStringUtil.htmlEncode(value);
		html[idx++] = "</div></td></tr>";
	}
	return idx;
}

ZaItem._attrDesc = 
function(name) {
	var desc = ZaItem._ATTR[name];
	return (desc == null) ? name : desc;
}

/* Translation of  the attribute names to the screen names */
ZaItem._ATTR = new Object();
ZaItem._ATTR[ZaItem.A_zimbraId] = ZaMsg.attrDesc_zimbraId;

