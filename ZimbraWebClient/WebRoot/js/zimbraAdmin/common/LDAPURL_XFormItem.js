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
LDAPURL_XFormItem.prototype.visibilityChecks = [ZaItem.hasReadPermission];
LDAPURL_XFormItem.prototype.enableDisableChecks = [ZaItem.hasWritePermission];
LDAPURL_XFormItem.prototype.initializeItems = function () {
	var ldapPort = this.getInheritedProperty("ldapPort");
	var ldapSSLPort = this.getInheritedProperty("ldapSSLPort");
	this.defSSLPort = ldapSSLPort ? ldapSSLPort : "636";
    this.defPort = ldapPort ? ldapPort : "389";


    var instance = this.getForm().getInstance () ;
    if (instance && instance [ZaDomain.A2_allowClearTextLDAPAuth] == "FALSE" )  {
        //force SSL  by default
        this._protocolPart = "ldaps://";
	    this._portPart = this.defSSLPort;
    } else {
        this._protocolPart = "ldap://";
        this._portPart = this.defPort ;
    }

	Composite_XFormItem.prototype.initializeItems.call(this);
	
    this.items[0].valueChangeEventSources = [this.getRefPath()];
    this.items[1].valueChangeEventSources = [this.getRefPath()];
    this.items[3].valueChangeEventSources = [this.getRefPath()];
}

LDAPURL_XFormItem.prototype.items = [
	{type:_OUTPUT_, width:"35px", ref:".", labelLocation:_NONE_, label:null,
		getDisplayValue:function(itemVal) {
             var val ;

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
                this.getParentItem()._protocolPart = val;
            } else if (this.getParentItem()._protocolPart != null) {
                val =  this.getParentItem()._protocolPart ;
            } else {
                val = "ldap://";
            }
            
            return val;
		}
	},
	{type:_TEXTFIELD_, width:"200px", forceUpdate:true, ref:".", labelLocation:_NONE_, label:null,
		required:true,
	 	visibilityChecks:[],
	 	enableDisableChecks:[],		
		getDisplayValue:function (itemVal) {
			var val = "";
			if(itemVal) {
				var URLChunks = itemVal.split(/[:\/]/);
				if(AjxEnv.isIE) {
				    // bug 68747, IE's split's result length is not fixed, don't use it
				    var urlPortPair = itemVal.substring(7); //trim the prefix "ldap://"
				    var chunks = urlPortPair.split(":");
				    val = chunks[0];
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
	 	visibilityChecks:[],
	 	enableDisableChecks:[],		
		getDisplayValue:function (itemVal) {
			var val ;
           
            if(itemVal) {
				var URLChunks = itemVal.split(/[:\/]/);
				
				if(AjxEnv.isIE) {
				    // bug 68747, IE's split's result length is not fixed, don't use it
				    var urlPortPair = itemVal.substring(7); //trim the prefix "ldap://"
				    var chunks = urlPortPair.split(":");
				    val = chunks[1];
				} else {
					if(URLChunks.length >= 5) {
						val = URLChunks[4];
					}  
				}
				this.getParentItem()._portPart = val;
			} else if (this.getParentItem()._portPart) {
                val = this.getParentItem()._portPart ;
            } else {
                val = this.getParentItem().defPort;
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
		visibilityChecks:[], subLabel:"", align:_RIGHT_,
	 	enableDisableChecks:[],
		getDisplayValue:function (itemVal) {
            var instance = this.getForm().getInstance () ;
            if ((itemVal==null || itemVal.length<=0) //make sure it is a new URL input
                    && (instance [ZaDomain.A2_allowClearTextLDAPAuth] && instance [ZaDomain.A2_allowClearTextLDAPAuth] == "FALSE" )) {
                //check the SSL and define the default SSL URL value
                this.getParentItem()._protocolPart = "ldaps://";
				this.getParentItem()._portPart = this.getParentItem().defSSLPort;
                return true ; //force SSL
            }                                   

            var val = false;
			var protocol = "ldap://";
			if(itemVal!=null && itemVal.length>0) {
				var URLChunks = itemVal.split(/[:\/]/);
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

