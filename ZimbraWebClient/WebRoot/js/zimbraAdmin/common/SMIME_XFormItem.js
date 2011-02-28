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
* XFormItem class: S/MIME Configuration
* @class SMIME_XFormItem
* @constructor SMIME_XFormItem
* @author qin@zimbra.com
**/
SMIME_XFormItem = function() {}
XFormItemFactory.createItemType("_SMIME_", "smime", SMIME_XFormItem, Composite_XFormItem);
SMIME_XFormItem.prototype.numCols = 1;
SMIME_XFormItem.prototype.nowrap = true;

SMIME_XFormItem.A_zimbraSMIMELdapURL = "zimbraSMIMELdapURL";
SMIME_XFormItem.A_zimbraSMIMELdapBindDn = "zimbraSMIMELdapBindDn";
SMIME_XFormItem.A_zimbraSMIMELdapBindPassword = "zimbraSMIMELdapBindPassword";
SMIME_XFormItem.A_zimbraSMIMELdapSearchBase = "zimbraSMIMELdapSearchBase";
SMIME_XFormItem.A_zimbraSMIMELdapFilter = "zimbraSMIMELdapFilter";
SMIME_XFormItem.A_zimbraSMIMELdapAttribute = "zimbraSMIMELdapAttribute";

SMIME_XFormItem.prototype.visibilityChecks = [ZaItem.hasReadPermission];
SMIME_XFormItem.prototype.enableDisableChecks = [ZaItem.hasWritePermission];
SMIME_XFormItem.prototype.initializeItems = function () {

    Composite_XFormItem.prototype.initializeItems.call(this);
	
}

SMIME_XFormItem.prototype.items = [
	{type:_TEXTFIELD_, ref:".", labelLocation:_LEFT_, forceUpdate:true,label:ZaMsg.Domain_SMIMEConfName,
		enableDisableChecks:false,
		required:true,
		getDisplayValue:function(itemVal) {
			var val;
			if(itemVal && itemVal["name"])
				val = itemVal["name"];
			else {
				val = "";
				this.setElementEnabled(true);
			}
            		return val;
		},
                elementChanged:function(nameVal, curVal, event) {
                        var newVal = {};
                        if(curVal == "" || !curVal)
                                curVal = {};
			newVal["name"] = nameVal;
			if(curVal[SMIME_XFormItem.A_zimbraSMIMELdapURL])
				newVal[SMIME_XFormItem.A_zimbraSMIMELdapURL] = curVal[SMIME_XFormItem.A_zimbraSMIMELdapURL];
			if(curVal[SMIME_XFormItem.A_zimbraSMIMELdapBindDn])
				newVal[SMIME_XFormItem.A_zimbraSMIMELdapBindDn] = curVal[SMIME_XFormItem.A_zimbraSMIMELdapBindDn];
			if(curVal[SMIME_XFormItem.A_zimbraSMIMELdapBindPassword])
				newVal[SMIME_XFormItem.A_zimbraSMIMELdapBindPassword] = curVal[SMIME_XFormItem.A_zimbraSMIMELdapBindPassword];
			if(curVal[SMIME_XFormItem.A_zimbraSMIMELdapSearchBase])
				newVal[SMIME_XFormItem.A_zimbraSMIMELdapSearchBase] = curVal[SMIME_XFormItem.A_zimbraSMIMELdapSearchBase];
			if(curVal[SMIME_XFormItem.A_zimbraSMIMELdapFilter])
				newVal[SMIME_XFormItem.A_zimbraSMIMELdapFilter] = curVal[SMIME_XFormItem.A_zimbraSMIMELdapFilter];
			if(curVal[SMIME_XFormItem.A_zimbraSMIMELdapAttribute])
				newVal[SMIME_XFormItem.A_zimbraSMIMELdapAttribute] = curVal[SMIME_XFormItem.A_zimbraSMIMELdapAttribute];

                        this.getForm().itemChanged(this.getParentItem(), newVal, event);
                }

	},
	{type:_TEXTFIELD_, width: "300px",  forceUpdate:true, ref:".", labelLocation:_LEFT_, label:ZaMsg.Domain_SMIMELdapURL,
		required:true,
		getDisplayValue:function (itemVal) {
			var val;
			if(itemVal && itemVal[SMIME_XFormItem.A_zimbraSMIMELdapURL])
				val = itemVal[SMIME_XFormItem.A_zimbraSMIMELdapURL];
			else val = ""; 
			return val;	
		},
		elementChanged:function(urlVal, curVal, event) {
                        var newVal = {};
                        if(curVal == "" || !curVal)
                                newVal = {};
                        if(curVal["name"])
				newVal["name"] = curVal["name"];
                        //if(curVal[SMIME_XFormItem.A_zimbraSMIMELdapURL])
                        //        newVal[SMIME_XFormItem.A_zimbraSMIMELdapURL] = curVal[SMIME_XFormItem.A_zimbraSMIMELdapURL];
                        if(curVal[SMIME_XFormItem.A_zimbraSMIMELdapBindDn])
                                newVal[SMIME_XFormItem.A_zimbraSMIMELdapBindDn] = curVal[SMIME_XFormItem.A_zimbraSMIMELdapBindDn];
                        if(curVal[SMIME_XFormItem.A_zimbraSMIMELdapBindPassword])
                                newVal[SMIME_XFormItem.A_zimbraSMIMELdapBindPassword] = curVal[SMIME_XFormItem.A_zimbraSMIMELdapBindPassword];
                        if(curVal[SMIME_XFormItem.A_zimbraSMIMELdapSearchBase])
                                newVal[SMIME_XFormItem.A_zimbraSMIMELdapSearchBase] = curVal[SMIME_XFormItem.A_zimbraSMIMELdapSearchBase];
                        if(curVal[SMIME_XFormItem.A_zimbraSMIMELdapFilter])
                                newVal[SMIME_XFormItem.A_zimbraSMIMELdapFilter] = curVal[SMIME_XFormItem.A_zimbraSMIMELdapFilter];
                        if(curVal[SMIME_XFormItem.A_zimbraSMIMELdapAttribute])
                                newVal[SMIME_XFormItem.A_zimbraSMIMELdapAttribute] = curVal[SMIME_XFormItem.A_zimbraSMIMELdapAttribute];

			newVal[SMIME_XFormItem.A_zimbraSMIMELdapURL] = urlVal;
			this.getForm().itemChanged(this.getParentItem(), newVal, event);
		}
	},
        {type:_TEXTFIELD_, forceUpdate:true, ref:".", labelLocation:_LEFT_, label:ZaMsg.Domain_SMIMELdapBindDn,
                getDisplayValue:function (itemVal) {
                        var val;
                        if(itemVal && itemVal[SMIME_XFormItem.A_zimbraSMIMELdapBindDn])
                                val = itemVal[SMIME_XFormItem.A_zimbraSMIMELdapBindDn];
                        else val = "";
                        return val;
                },
                elementChanged:function(dnVal, curVal, event) {
                        var newVal = {};
                        if(curVal == "" || !curVal)
                                newVal = {};

                        if(curVal["name"]) 
                                newVal["name"] = curVal["name"];
                        if(curVal[SMIME_XFormItem.A_zimbraSMIMELdapURL])
                                newVal[SMIME_XFormItem.A_zimbraSMIMELdapURL] = curVal[SMIME_XFormItem.A_zimbraSMIMELdapURL];
                        //if(curVal[SMIME_XFormItem.A_zimbraSMIMELdapBindDn])
                        //        newVal[SMIME_XFormItem.A_zimbraSMIMELdapBindDn] = curVal[SMIME_XFormItem.A_zimbraSMIMELdapBindDn];
                        if(curVal[SMIME_XFormItem.A_zimbraSMIMELdapBindPassword])
                                newVal[SMIME_XFormItem.A_zimbraSMIMELdapBindPassword] = curVal[SMIME_XFormItem.A_zimbraSMIMELdapBindPassword];
                        if(curVal[SMIME_XFormItem.A_zimbraSMIMELdapSearchBase])
                                newVal[SMIME_XFormItem.A_zimbraSMIMELdapSearchBase] = curVal[SMIME_XFormItem.A_zimbraSMIMELdapSearchBase];
                        if(curVal[SMIME_XFormItem.A_zimbraSMIMELdapFilter])
                                newVal[SMIME_XFormItem.A_zimbraSMIMELdapFilter] = curVal[SMIME_XFormItem.A_zimbraSMIMELdapFilter];
                        if(curVal[SMIME_XFormItem.A_zimbraSMIMELdapAttribute])
                                newVal[SMIME_XFormItem.A_zimbraSMIMELdapAttribute] = curVal[SMIME_XFormItem.A_zimbraSMIMELdapAttribute];

                        newVal[SMIME_XFormItem.A_zimbraSMIMELdapBindDn] = dnVal;
                        this.getForm().itemChanged(this.getParentItem(), newVal, event);
                }
        },
        {type:_PASSWORD_, forceUpdate:true, ref:".", labelLocation:_LEFT_, label:ZaMsg.Domain_SMIMELdapBindPassword,
                getDisplayValue:function (itemVal) {
                        var val;
                        if(itemVal && itemVal[SMIME_XFormItem.A_zimbraSMIMELdapBindPassword])
                                val = itemVal[SMIME_XFormItem.A_zimbraSMIMELdapBindPassword];
                        else val = "";
                        return val;
                }
		,
                elementChanged:function(passVal, curVal, event) {
                       	var newVal = {};
			if(curVal == "" || !curVal)
                                newVal = {};

                        if(curVal["name"]) 
                                newVal["name"] = curVal["name"];
                        if(curVal[SMIME_XFormItem.A_zimbraSMIMELdapURL])
                                newVal[SMIME_XFormItem.A_zimbraSMIMELdapURL] = curVal[SMIME_XFormItem.A_zimbraSMIMELdapURL];
                        if(curVal[SMIME_XFormItem.A_zimbraSMIMELdapBindDn])
                                newVal[SMIME_XFormItem.A_zimbraSMIMELdapBindDn] = curVal[SMIME_XFormItem.A_zimbraSMIMELdapBindDn];
                        //if(curVal[SMIME_XFormItem.A_zimbraSMIMELdapBindPassword])
                        //       newVal[SMIME_XFormItem.A_zimbraSMIMELdapBindPassword] = curVal[SMIME_XFormItem.A_zimbraSMIMELdapBindPassword];
                        if(curVal[SMIME_XFormItem.A_zimbraSMIMELdapSearchBase])
                                newVal[SMIME_XFormItem.A_zimbraSMIMELdapSearchBase] = curVal[SMIME_XFormItem.A_zimbraSMIMELdapSearchBase];
                        if(curVal[SMIME_XFormItem.A_zimbraSMIMELdapFilter])
                                newVal[SMIME_XFormItem.A_zimbraSMIMELdapFilter] = curVal[SMIME_XFormItem.A_zimbraSMIMELdapFilter];
                        if(curVal[SMIME_XFormItem.A_zimbraSMIMELdapAttribute])
                                newVal[SMIME_XFormItem.A_zimbraSMIMELdapAttribute] = curVal[SMIME_XFormItem.A_zimbraSMIMELdapAttribute];

                        newVal[SMIME_XFormItem.A_zimbraSMIMELdapBindPassword] = passVal;
			this.getForm().itemChanged(this.getParentItem(), newVal, event);
                }

        },
        {type:_TEXTFIELD_, forceUpdate:true, ref:".", labelLocation:_LEFT_, label:ZaMsg.Domain_SMIMELdapSearchBase,
                getDisplayValue:function (itemVal) {
                        var val;
                        if(itemVal && itemVal[SMIME_XFormItem.A_zimbraSMIMELdapSearchBase])
                                val = itemVal[SMIME_XFormItem.A_zimbraSMIMELdapSearchBase];
                        else val = "";
                        return val;
                },
                elementChanged:function(searchbaseVal, curVal, event) {
                        var newVal = {};
                        if(curVal == "" || !curVal)
                                newVal = {};

                        if(curVal["name"]) 
                                newVal["name"] = curVal["name"];
                        if(curVal[SMIME_XFormItem.A_zimbraSMIMELdapURL])
                                newVal[SMIME_XFormItem.A_zimbraSMIMELdapURL] = curVal[SMIME_XFormItem.A_zimbraSMIMELdapURL];
                        if(curVal[SMIME_XFormItem.A_zimbraSMIMELdapBindDn])
                                newVal[SMIME_XFormItem.A_zimbraSMIMELdapBindDn] = curVal[SMIME_XFormItem.A_zimbraSMIMELdapBindDn];
                        if(curVal[SMIME_XFormItem.A_zimbraSMIMELdapBindPassword])
                                newVal[SMIME_XFormItem.A_zimbraSMIMELdapBindPassword] = curVal[SMIME_XFormItem.A_zimbraSMIMELdapBindPassword];
                        //if(curVal[SMIME_XFormItem.A_zimbraSMIMELdapSearchBase])
                        //        newVal[SMIME_XFormItem.A_zimbraSMIMELdapSearchBase] = curVal[SMIME_XFormItem.A_zimbraSMIMELdapSearchBase];
                        if(curVal[SMIME_XFormItem.A_zimbraSMIMELdapFilter])
                                newVal[SMIME_XFormItem.A_zimbraSMIMELdapFilter] = curVal[SMIME_XFormItem.A_zimbraSMIMELdapFilter];
                        if(curVal[SMIME_XFormItem.A_zimbraSMIMELdapAttribute])
                                newVal[SMIME_XFormItem.A_zimbraSMIMELdapAttribute] = curVal[SMIME_XFormItem.A_zimbraSMIMELdapAttribute];

			newVal[SMIME_XFormItem.A_zimbraSMIMELdapSearchBase] = searchbaseVal;
                        this.getForm().itemChanged(this.getParentItem(), newVal, event);
                }
        },
        {type:_TEXTAREA_, width: "300px", height:"40px", forceUpdate:true, ref:".", 
		labelLocation:_LEFT_, label:ZaMsg.Domain_SMIMELdapFilter,
                getDisplayValue:function (itemVal) {
                        var val;
                        if(itemVal && itemVal[SMIME_XFormItem.A_zimbraSMIMELdapFilter])
                                val = itemVal[SMIME_XFormItem.A_zimbraSMIMELdapFilter];
                        else val = "";
                        return val;
                },
                elementChanged:function(filterVal, curVal, event) {
                        var newVal = {};
                        if(curVal == "" || !curVal)
                                newVal = {};

                        if(curVal["name"]) 
                                newVal["name"] = curVal["name"];
                        if(curVal[SMIME_XFormItem.A_zimbraSMIMELdapURL])
                                newVal[SMIME_XFormItem.A_zimbraSMIMELdapURL] = curVal[SMIME_XFormItem.A_zimbraSMIMELdapURL];
                        if(curVal[SMIME_XFormItem.A_zimbraSMIMELdapBindDn])
                                newVal[SMIME_XFormItem.A_zimbraSMIMELdapBindDn] = curVal[SMIME_XFormItem.A_zimbraSMIMELdapBindDn];
                        if(curVal[SMIME_XFormItem.A_zimbraSMIMELdapBindPassword])
                                newVal[SMIME_XFormItem.A_zimbraSMIMELdapBindPassword] = curVal[SMIME_XFormItem.A_zimbraSMIMELdapBindPassword];
                        if(curVal[SMIME_XFormItem.A_zimbraSMIMELdapSearchBase])
                                newVal[SMIME_XFormItem.A_zimbraSMIMELdapSearchBase] = curVal[SMIME_XFormItem.A_zimbraSMIMELdapSearchBase];
                        //if(curVal[SMIME_XFormItem.A_zimbraSMIMELdapFilter])
                        //        newVal[SMIME_XFormItem.A_zimbraSMIMELdapFilter] = curVal[SMIME_XFormItem.A_zimbraSMIMELdapFilter];
                        if(curVal[SMIME_XFormItem.A_zimbraSMIMELdapAttribute])
                                newVal[SMIME_XFormItem.A_zimbraSMIMELdapAttribute] = curVal[SMIME_XFormItem.A_zimbraSMIMELdapAttribute];

			newVal[SMIME_XFormItem.A_zimbraSMIMELdapFilter] = filterVal;
                        this.getForm().itemChanged(this.getParentItem(), newVal, event);
                }
        },
        {type:_TEXTAREA_, width: "300px", height:"40px", forceUpdate:true, 
		ref:".", labelLocation:_LEFT_, label:ZaMsg.Domain_SMIMELdapAttribute,
                getDisplayValue:function (itemVal) {
                        var val;
                        if(itemVal && itemVal[SMIME_XFormItem.A_zimbraSMIMELdapAttribute])
                                val = itemVal[SMIME_XFormItem.A_zimbraSMIMELdapAttribute];
                        else val = "";
                        return val;
                },
                elementChanged:function(attrVal, curVal, event) {
                        var newVal = {};
                        if(curVal == "" || !curVal)
                                newVal = {};

                        if(curVal["name"]) 
                                newVal["name"] = curVal["name"];
                        if(curVal[SMIME_XFormItem.A_zimbraSMIMELdapURL])
                                newVal[SMIME_XFormItem.A_zimbraSMIMELdapURL] = curVal[SMIME_XFormItem.A_zimbraSMIMELdapURL];
                        if(curVal[SMIME_XFormItem.A_zimbraSMIMELdapBindDn])
                                newVal[SMIME_XFormItem.A_zimbraSMIMELdapBindDn] = curVal[SMIME_XFormItem.A_zimbraSMIMELdapBindDn];
                        if(curVal[SMIME_XFormItem.A_zimbraSMIMELdapBindPassword])
                                newVal[SMIME_XFormItem.A_zimbraSMIMELdapBindPassword] = curVal[SMIME_XFormItem.A_zimbraSMIMELdapBindPassword];
                        if(curVal[SMIME_XFormItem.A_zimbraSMIMELdapSearchBase])
                                newVal[SMIME_XFormItem.A_zimbraSMIMELdapSearchBase] = curVal[SMIME_XFormItem.A_zimbraSMIMELdapSearchBase];
                        if(curVal[SMIME_XFormItem.A_zimbraSMIMELdapFilter])
                                newVal[SMIME_XFormItem.A_zimbraSMIMELdapFilter] = curVal[SMIME_XFormItem.A_zimbraSMIMELdapFilter];
                        //if(curVal[SMIME_XFormItem.A_zimbraSMIMELdapAttribute])
                        //        newVal[SMIME_XFormItem.A_zimbraSMIMELdapAttribute] = curVal[SMIME_XFormItem.A_zimbraSMIMELdapAttribute];

			newVal[SMIME_XFormItem.A_zimbraSMIMELdapAttribute] = attrVal;
                        this.getForm().itemChanged(this.getParentItem(), newVal, event);
                }
        },

	{type:_CELLSPACER_,height: "40px"}
];

