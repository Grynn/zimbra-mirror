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
* XModelItem class
*
* @class MailQuota_XModelItem
* @constructor MailQuota_XModelItem
* @author Greg Solovyev
**/
MailQuota_XModelItem = function (){}
XModelItemFactory.createItemType("_MAILQUOTA_", "mailquota", MailQuota_XModelItem);
MailQuota_XModelItem.prototype.validateType = XModelItem.prototype.validateNumber;
MailQuota_XModelItem.prototype.getterScope = _MODELITEM_;
MailQuota_XModelItem.prototype.setterScope = _MODELITEM_;
MailQuota_XModelItem.prototype.getter = "getValue";
MailQuota_XModelItem.prototype.setter = "setValue";
MailQuota_XModelItem.prototype.maxInclusive = 8796093022207;
MailQuota_XModelItem.prototype.minInclusive = 0;

MailQuota_XModelItem.prototype.getValue =  function(ins, current, ref) {
	var value = null;
	if(eval("ins." + ref) != null) {
	  value = eval("ins." + ref) / 1048576;
	  if(value != Math.round(value)) {
		 value = Number(value).toFixed(2);
	  }
	}
	if(typeof value == "number" && value == 0)
		value = "0";	
	return value;
}

MailQuota_XModelItem.prototype.setValue = function(value, instance, current, ref) {
	var pathParts = new Array();
	var val = Math.round(value * 1048576);
	if(ref.indexOf(".") >= 0) {
		pathParts = ref.split(".");
	} else if (ref.indexOf("/") >=0) {
		pathParts = ref.split("/");
	} else {
		instance[ref] = val
		return val;
	}
	var cnt = pathParts.length-1;
	var obj = instance[pathParts[0]];
	for(var ix=1; ix<cnt; ix++) {
		obj = obj[pathParts[ix]];
	}
	obj[pathParts[cnt]] = val;
	return val;
}

/**
* _MAILQUOTA_2_ XModel item type
**/
MailQuota2_XModelItem = function (){}
XModelItemFactory.createItemType("_MAILQUOTA_2_", "mailquota_2", MailQuota2_XModelItem);
MailQuota2_XModelItem.prototype.getterScope = _MODELITEM_;
MailQuota2_XModelItem.prototype.getter = "getValue";

MailQuota2_XModelItem.prototype.getValue = function(instance, current, ref) {
	var value = this.getLocalValue(instance, current, ref);
	if (value == null) value = this.getSuperValue(instance, current, ref);
	if(value <=0) 
		value = ZaMsg.Unlimited;
	return value;
}

MailQuota2_XModelItem.prototype.getSuperValue = function(ins, current, ref) {
	if (!ins) { return null; }
	var _ref  = ref  ? ref.replace("/", ".") : this.ref.replace("/", ".");
//	var _ref = this.ref.replace("/", ".");
	var value = (eval("ins._defaultValues." + _ref) != null) ? Number(eval("ins._defaultValues." + _ref) / 1048576).toFixed(0) : 0;
	return value;
}
MailQuota2_XModelItem.prototype.getLocalValue = function(ins, current, ref) {
	if (!ins) { return null; }
	var _ref  = ref  ? ref.replace("/", ".") : this.ref.replace("/", ".");
//	var _ref = this.ref.replace("/", ".");
	var value = (eval("ins." + _ref) != null) ? Number(eval("ins." + _ref) / 1048576).toFixed(0) : null;
	return value;
}
