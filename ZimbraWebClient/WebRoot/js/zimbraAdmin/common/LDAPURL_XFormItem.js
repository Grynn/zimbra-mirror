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
* XFormItem class: LDAP URL
* this item is used in the Admin UI to display LDAP URL fields like LDAP URL for GAL Search and LDAP URL  for Authentication
* @class LDAPURL_XFormItem
* @constructor LDAPURL_XFormItem
* @author Greg Solovyev
**/
LDAPURL_XFormItem = function() {}
XFormItemFactory.createItemType("_LDAPURL_", "ldapurl", LDAPURL_XFormItem, Composite_XFormItem);
LDAPURL_XFormItem.prototype.numCols = 5;
LDAPURL_XFormItem.prototype.nowrap = true;
LDAPURL_XFormItem.prototype._protocolPart = "ldap://";
LDAPURL_XFormItem.prototype._serverPart = "";
LDAPURL_XFormItem.prototype._portPart = "389";
LDAPURL_XFormItem.prototype.defSSLPort = "636";
LDAPURL_XFormItem.prototype.defPort = "389";
LDAPURL_XFormItem.prototype.initializeItems = function () {
	var ldapPort = this.getInheritedProperty("ldapPort");
	var ldapSSLPort = this.getInheritedProperty("ldapSSLPort");
	this.defPort = ldapPort ? ldapPort : "389";
    this.defSSLPort = ldapSSLPort ? ldapSSLPort : "636";
    
	Composite_XFormItem.prototype.initializeItems.call(this);
	
    this.items[0].valueChangeEventSources = [this.getRefPath()];
    this.items[1].valueChangeEventSources = [this.getRefPath()];
    this.items[3].valueChangeEventSources = [this.getRefPath()];
}

LDAPURL_XFormItem.prototype.items = [
	{type:_OUTPUT_, width:"35px", ref:".", labelLocation:_NONE_, label:null,
		getDisplayValue:function(itemVal) {
			var val = "ldap://";
			if(itemVal!=null && itemVal.length>0) {
				var URLChunks = itemVal.split(/(:\/\/)/);
				if(AjxEnv.isIE) {
					if(URLChunks[0] == "ldap" || URLChunks[0] == "ldaps")
						val = URLChunks[0] + "://";	
				} else {
					if(URLChunks.length==3) {
						val = URLChunks[0] + URLChunks[1];
					}
				}
			}
			this.getParentItem()._protocolPart = val;
			return val;
		}
	},
	{type:_TEXTFIELD_, width:"200px", forceUpdate:true, ref:".", labelLocation:_NONE_, label:null,
		required:true,
		getDisplayValue:function (itemVal) {
			var val = "";
			if(itemVal) {
				var URLChunks = itemVal.split(/[:\/]/);
				if(AjxEnv.isIE) {
					if(URLChunks.length >= 3) {
						val = URLChunks[1];
					} 
				} else {
					if(URLChunks.length >= 4) {
						val = URLChunks[3];
					} 
				}
				this.getParentItem()._serverPart = val;
			} 
			return val;	
		},
		elementChanged:function(serverPart, instanceValue, event) {
			this.getParentItem()._serverPart = serverPart;
			var val = this.getParentItem()._protocolPart + serverPart+ ":" + this.getParentItem()._portPart;
			this.getForm().itemChanged(this.getParentItem(), val, event);
		}
	},
	{type:_OUTPUT_, width:"5px", labelLocation:_NONE_, label:null,value:":", ref:null},
	{type:_TEXTFIELD_,width:"40px",forceUpdate:true, ref:".", labelLocation:_NONE_, label:null, 
		getDisplayValue:function (itemVal) {
			var val = this.getParentItem().defPort;
			if(itemVal) {
				var URLChunks = itemVal.split(/[:\/]/);
					
					/*DBG.println(AjxDebug.DBG1, "_TEXTFIELD_");
					for(var ix in URLChunks) {
						DBG.println(AjxDebug.DBG1, "URLChunks[" + ix + "] = " + URLChunks[ix]);
					}*/
					
				if(AjxEnv.isIE) {
					var tmp = parseInt(URLChunks[URLChunks.length-1]);
					if(tmp != NaN)
						val = tmp;
				} else {
					if(URLChunks.length >= 5) {
						val = URLChunks[4];
					}  
				}
				this.getParentItem()._portPart = val;
			} 
			return val;	
		},
		elementChanged:function(portPart, instanceValue, event) {
			this.getParentItem()._portPart = portPart;
			var val = this.getParentItem()._protocolPart + this.getParentItem()._serverPart+ ":" + portPart;
			this.getForm().itemChanged(this.getParentItem(), val, event);
		}
	},
	{type:_CHECKBOX_,width:"40px",containerCssStyle:"width:40px", forceUpdate:true, ref:".", labelLocation:_NONE_, label:null, 
		getDisplayValue:function (itemVal) {
			var val = false;
			var protocol = "ldap://";
			if(itemVal!=null && itemVal.length>0) {
				var URLChunks = itemVal.split(/[:\/]/);
					/*DBG.println(AjxDebug.DBG1, "_CHECKBOX_");
					for(var ix in URLChunks) {
						DBG.println(AjxDebug.DBG1, "URLChunks[" + ix + "] = " + URLChunks[ix]);
					}*/
				protocol = URLChunks[0] + "://";				
			}
			this.getParentItem()._protocolPart = protocol;
			if(protocol.length==8) {
				val = true;
			}
			return val;			
		},
		elementChanged:function(isChecked, instanceValue, event) {
			if(isChecked) {
				this.getParentItem()._protocolPart = "ldaps://";
				this.getParentItem()._portPart = this.getParentItem().defSSLPort;
			} else {
				this.getParentItem()._protocolPart = "ldap://";
				this.getParentItem()._portPart = this.getParentItem().defPort;
			}
			var val = this.getParentItem()._protocolPart + this.getParentItem()._serverPart+ ":" + this.getParentItem()._portPart;
			this.getForm().itemChanged(this.getParentItem(), val, event);
		}
	}
];

