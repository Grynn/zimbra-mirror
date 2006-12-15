/*
 * ***** BEGIN LICENSE BLOCK *****
 * Version: ZPL 1.2
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.2 ("License"); you may not use this file except in
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
 * Portions created by Zimbra are Copyright (C) 2004, 2005, 2006 Zimbra, Inc.
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
function ZaItem(app, iKeyName) {
	if (arguments.length == 0) return;
	this._app = app;
	this._iKeyName = iKeyName;
	ZaModel.call(this, true);

}

ZaItem.prototype = new ZaModel;
ZaItem.prototype.constructor = ZaItem;

ZaItem.loadMethods = new Object();
ZaItem.initMethods = new Object();
ZaItem.modifyMethods = new Object();
ZaItem.createMethods = new Object();

ZaItem.ACCOUNT = "account";
ZaItem.DL = "dl";
ZaItem.ALIAS = "alias";
ZaItem.RESOURCE = "calresource";
ZaItem.DOMAIN = "domain";
ZaItem.COS = "cos";
ZaItem.SERVER = "server";
ZaItem.ZIMLET = "zimlet";
ZaItem.MAILQ_ITEM = "message";
ZaItem.MAILQ = "mailque";
ZaItem.A_objectClass = "objectClass";
ZaItem.A_zimbraId = "zimbraId";

/* Translation of  the attribute names to the screen names */
ZaItem._ATTR = new Object();
ZaItem._ATTR[ZaItem.A_zimbraId] = ZaMsg.attrDesc_zimbraId;

ZaItem.prototype.toString = 
function() {
	if(this.name)
		return this.name;
	else if (this.id)
		return this.id;
	else
		return "ZaItem "+this.type+": name="+this.name+" id="+this.id;
}

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

		case ZaItem.ALIAS:
			return new ZaAlias(app);

		case ZaItem.DL:
			return new ZaDistributionList(app);

		case ZaItem.RESOURCE:
			return new ZaResource(app);
		
		case ZaItem.DOMAIN:
			return new ZaDomain(app);

		case ZaItem.COS:
			return new ZaCos(app);

		case ZaItem.SERVER:
			return new ZaServer(app);

		case ZaItem.MAILQ:
			return new ZaMTA(app);

	}
}

ZaItem.prototype.remove = 
function () {
	//abstract
}

ZaItem.prototype.refresh = 
function () {
	this.load();
}

ZaItem.prototype.copyTo = 
function (target/*, fullRecursion*/) {
	for(var a in this) {
		target[a] = this[a];
	}
}

ZaItem.prototype.load = function (by, val, withConfig) {
	//Instrumentation code start
	if(ZaItem.loadMethods[this._iKeyName]) {
		var methods = ZaItem.loadMethods[this._iKeyName];
		var cnt = methods.length;
		for(var i = 0; i < cnt; i++) {
			if(typeof(methods[i]) == "function") {
				methods[i].call(this, by, val, withConfig);
			}
		}
	}	
	//Instrumentation code end
}


ZaItem.prototype.modify = function (mods) {
	//Instrumentation code start
	if(ZaItem.modifyMethods[this._iKeyName]) {
		var methods = ZaItem.modifyMethods[this._iKeyName];
		var cnt = methods.length;
		for(var i = 0; i < cnt; i++) {
			if(typeof(methods[i]) == "function") {
				methods[i].call(this, mods);
			}
		}
	}	
	//Instrumentation code end
}

/**
* Factory method
* creates a new object of class constructorFunction, then passes the new object to every method in
* ZaItem.createMethods[key] 
* @see ZaItem#createMethods
**/
ZaItem.create = function (tmpObj, constructorFunction, key,  app) {
	var item = new constructorFunction(app);
	//Instrumentation code start
	if(ZaItem.createMethods[key]) {
		var methods = ZaItem.createMethods[key];
		var cnt = methods.length;
		for(var i = 0; i < cnt; i++) {
			if(typeof(methods[i]) == "function") {
				methods[i].call(this, tmpObj, item, app);
			}
		}
	}	
	//Instrumentation code end
	return item;
}

ZaItem.prototype.initFromDom =
function(node) {
	this.name = node.getAttribute("name");
	this.id = node.getAttribute("id");
	this.attrs = new Object();
	this.type = node.nodeName;
	
	var children = node.childNodes;
	var cnt = children.length;
	for (var i=0; i< cnt;  i++) {
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

ZaItem.prototype.initFromJS = 
function (obj) {
	if(!obj)
		return;
	this.name = obj.name;
	this.id = obj.id;
	if (obj.isgroup == false) {
		this.isgroup = 0 ;
	}else if (obj.isgroup == true){
		this.isgroup = 1 ;
	}
	this.attrs = new Object();
	if(obj.a) {
		var len = obj.a.length;
		for(var ix = 0; ix < len; ix++) {
			if(!this.attrs[[obj.a[ix].n]]) {
				this.attrs[[obj.a[ix].n]] = obj.a[ix]._content;
			} else {
				if(!(this.attrs[[obj.a[ix].n]] instanceof Array)) {
					this.attrs[[obj.a[ix].n]] = [this.attrs[[obj.a[ix].n]]];
				} 
				this.attrs[[obj.a[ix].n]].push(obj.a[ix]._content);
			}
		}
	}
	if(obj._attrs) {
		for (var ix in obj._attrs) {
			if(!this.attrs[ix]) {
				this.attrs[ix] = obj._attrs[ix];
			} else {
				if(!(this.attrs[ix] instanceof Array)) {
					this.attrs[ix] = [this.attrs[ix]];
				} 
				this.attrs[ix].push(obj._attrs[ix]);
			}
		}
	}
}

// Adds a row to the tool tip.
ZaItem.prototype._addRow =
function(msg, value, html, idx) {
	if (value != null) {
		html[idx++] = "<tr valign='top'><td align='right' style='padding-right: 5px;'><b>";
		html[idx++] = AjxStringUtil.htmlEncode(msg);
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

ZaItem.prototype._init = function (app) {
	//Instrumentation code start
	if(ZaItem.initMethods[this._iKeyName]) {
		var methods = ZaItem.initMethods[this._iKeyName];
		var cnt = methods.length;
		for(var i = 0; i < cnt; i++) {
			if(typeof(methods[i]) == "function") {
				methods[i].call(this,app);
			}
		}
	}	
	//Instrumentation code end
}

