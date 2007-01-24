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
 * Portions created by Zimbra are Copyright (C) 2005, 2006 Zimbra, Inc.
 * All Rights Reserved.
 * 
 * Contributor(s):
 * 
 * ***** END LICENSE BLOCK *****
 */

/**
* _MLIFETIME XModelItem
**/

MLifetime_XModelItem = function () {}
XModelItemFactory.createItemType("_MLIFETIME_", "mlifetime", MLifetime_XModelItem);
MLifetime_XModelItem.prototype.validateType = function (value) {
	var val = "1";
	if(value != null && value.length >0) {
		if(value.length > 1) {
			val = value.substr(0, value.length-1);				
		} else {
			if(value == "0") {
				val = "0";
			} else {
				val = "1";
			}
		}
	}
	
	val =  XModelItem.prototype.validateNumber.call(this, val);
	return value;
}
/**
* XFormItem class: "lifetime (composite item)
* this item is used in the Admin UI to display fields such as session token lifetime
* instance values are strings that contain numbers and characters (/^([0-9])+([dhms])?$/;)
* values d, h, m, and s mean 1 day, 1 hour, 1 minute and 1 second
* 1d means 1 day, 4d means 4 days, 4h means 4 hours, etc.
*
* @class Lifetime_XFormItem
* @constructor Lifetime_XFormItem
* @author Greg Solovyev
**/
function Lifetime_XFormItem() {}
XFormItemFactory.createItemType("_LIFETIME_", "lifetime", Lifetime_XFormItem, Composite_XFormItem);

Lifetime_XFormItem.prototype.TIME_CHOICES = [
 				{value:"d", label:"Days"},
				{value:"h", label:"Hours"},
				{value:"m", label:"Minutes"},
				{value:"s", label:"Seconds"}
];


Lifetime_XFormItem.prototype.items = [
	{type:_TEXTFIELD_, ref:".", labelLocation:_NONE_,relevantBehavior:_PARENT_, cssClass:"admin_xform_number_input", 
		getDisplayValue:function (itemVal) {
			var val = "1";
			if(itemVal != null && itemVal.length >0) {
				if(itemVal.length > 1) {
					val = parseInt(itemVal);			
				} else {
					if(itemVal == "0") {
						val = "0";
					} else {
						val = "1";
					}
				}
			}
			this.getParentItem()._numericPart = val;
			return val;	
		},
		elementChanged:function(numericPart, instanceValue, event) {
			var val = numericPart + this.getParentItem()._stringPart;
			this.getForm().itemChanged(this.getParentItem(), val, event);
		}
	},
	{type:_OSELECT1_, ref:".", labelLocation:_NONE_, relevantBehavior:_PARENT_, choices:Lifetime_XFormItem.prototype.TIME_CHOICES,
		getDisplayValue:function (itemVal){
			var val = "d";
			if(itemVal != null && itemVal.length >0) {
				if(itemVal.length > 1) {
					val = itemVal.substr(itemVal.length-1, 1);
				} else if (itemVal != "0") {
					val = (itemVal == "d" || itemVal == "h" || itemVal== "m" || itemVal == "s") ? itemVal : "d";
				}
			}
			this.getParentItem()._stringPart = val;
			return val;
		},
		elementChanged:function(stringPart,instanceValue, event) {
			var val = this.getParentItem()._numericPart + stringPart;
			this.getForm().itemChanged(this.getParentItem(), val, event);
		}
	}
];

function Lifetime1_XFormItem() {}
XFormItemFactory.createItemType("_LIFETIME1_", "lifetime1", Lifetime1_XFormItem, Composite_XFormItem);

Lifetime1_XFormItem.prototype.TIME_CHOICES = [
 				{value:"d", label:"Days"},
				{value:"h", label:"Hours"}
];


Lifetime1_XFormItem.prototype.items = [
	{type:_TEXTFIELD_, ref:".", labelLocation:_NONE_,relevantBehavior:_PARENT_, cssClass:"admin_xform_number_input", 
		getDisplayValue:function (itemVal) {
			var val = "1";
			if(itemVal != null && itemVal.length >0) {
				if(itemVal.length > 1) {
					val = itemVal.substr(0, itemVal.length-1);				
				} else {
					if(itemVal == "0") {
						val = "0";
					} else {
						val = "1";
					}
				}
			}
			this.getParentItem()._numericPart = val;
			return val;	
		},
		elementChanged:function(numericPart, instanceValue, event) {
			var val = numericPart + this.getParentItem()._stringPart;
			this.getForm().itemChanged(this.getParentItem(), val, event);
		}
	},
	{type:_OSELECT1_, ref:".", labelLocation:_NONE_, relevantBehavior:_PARENT_, choices:Lifetime1_XFormItem.prototype.TIME_CHOICES,
		getDisplayValue:function (itemVal){
			var val = "d";
			if(itemVal != null && itemVal.length >0) {
				if(itemVal.length > 1) {
					val = itemVal.substr(itemVal.length-1, 1);
				} else if (itemVal != "0") {
					val = (itemVal == "d" || itemVal == "h" || itemVal== "m" || itemVal == "s") ? itemVal : "d";
				}
			}
			this.getParentItem()._stringPart = val;
			return val;
		},
		elementChanged:function(stringPart,instanceValue, event) {
			var val = this.getParentItem()._numericPart + stringPart;
			this.getForm().itemChanged(this.getParentItem(), val, event);
		}
	}
];
