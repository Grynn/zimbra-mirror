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
* _MLIFETIME XModelItem
**/

MLifetime_XModelItem = function () {}
XModelItemFactory.createItemType("_MLIFETIME_", "mlifetime", MLifetime_XModelItem);
MLifetime_XModelItem.prototype.validateType = function (value) {
	var val = "";
	if(value == ZaMsg.Unlimited) {
		val = "0";
	} else if(value != null && value.length >0) {
		if(value.length > 1) {
			val = value.substr(0, value.length-1);				
		} else {
			val = "0";
		}
	}
	
	if(val)
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
Lifetime_XFormItem = function() {}
XFormItemFactory.createItemType("_LIFETIME_", "lifetime", Lifetime_XFormItem, Composite_XFormItem);
Lifetime_XFormItem.prototype.visibilityChecks = [ZaItem.hasReadPermission];
Lifetime_XFormItem.prototype.enableDisableChecks = [ZaItem.hasWritePermission];
Lifetime_XFormItem.prototype.nowrap = false;
Lifetime_XFormItem.prototype.labelWrap = true;
Lifetime_XFormItem.prototype.initializeItems = function(){
	this.items = [
	{type:_TEXTFIELD_, ref:".", labelLocation:_NONE_, cssClass:"admin_xform_number_input", 
	 	visibilityChecks:[],
	 	enableDisableChecks:[],		
		getDisplayValue:function (itemVal) {
			var val = "";
			if(itemVal != null && itemVal.length >0) {
				if(itemVal.length > 1) {
					val = parseInt(itemVal);			
				} else {
					if(itemVal == "0") {
						val = "0";
					} else {
						val = "";
					}
				}
			}
			this.getParentItem()._numericPart = val;
			return ((!val || val=="0") ? ZaMsg.Unlimited : val);	
		},
		elementChanged:function(numericPart, instanceValue, event) {
			var val = numericPart + this.getParentItem()._stringPart;
			this.getParentItem()._numericPart = numericPart;
			this.getForm().itemChanged(this.getParentItem(), val, event);
		}
	},
	{type:_OSELECT1_, ref:".", labelLocation:_NONE_, choices: ZaModel.getTimeChoices(),
		visibilityChecks:[],
	 	enableDisableChecks:[],
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
			this.getParentItem()._stringPart = stringPart;
			this.getForm().itemChanged(this.getParentItem(), val, event);
		}
	}
];
	Composite_XFormItem.prototype.initializeItems.call(this);
}
Lifetime_XFormItem.prototype.items = [];
Lifetime_XFormItem.prototype.getDisplayElement = function () {
	return this.getElement(this.getId() + "_display");
}

Lifetime1_XFormItem = function() {}
XFormItemFactory.createItemType("_LIFETIME1_", "lifetime1", Lifetime1_XFormItem, Composite_XFormItem);
Lifetime1_XFormItem.prototype.nowrap = false;
Lifetime1_XFormItem.prototype.labelWrap = true;
Lifetime1_XFormItem.prototype.visibilityChecks = [ZaItem.hasReadPermission];
Lifetime1_XFormItem.prototype.enableDisableChecks = [ZaItem.hasWritePermission];
Lifetime1_XFormItem.prototype.initializeItems  = function() {
	this.items = [
	{type:_TEXTFIELD_, ref:".", labelLocation:_NONE_,cssClass:"admin_xform_number_input", 
		visibilityChecks:[],
	 	enableDisableChecks:[],
		getDisplayValue:function (itemVal) {
			var val = "";
			if(itemVal != null && itemVal.length >0) {
				if(itemVal.length > 1) {
					val = itemVal.substr(0, itemVal.length-1);				
				} else {
					if(itemVal == "0") {
						val = "0";
					} else {
						val = "";
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
	{type:_OSELECT1_, ref:".", labelLocation:_NONE_, choices:ZaModel.getTimeChoices1(),
		visibilityChecks:[],
	 	enableDisableChecks:[],
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
Composite_XFormItem.prototype.initializeItems.call(this);
}
Lifetime1_XFormItem.prototype.items = [];

Lifetime2_XFormItem = function() {}
Lifetime2_XFormItem.prototype.nowrap = false;
Lifetime2_XFormItem.prototype.labelWrap = true;
XFormItemFactory.createItemType("_LIFETIME2_", "lifetime2", Lifetime2_XFormItem, Lifetime1_XFormItem);

Lifetime2_XFormItem.prototype.initializeItems = function () {
	this.items = [
		{type:_TEXTFIELD_, ref:".", labelLocation:_NONE_,cssClass:"admin_xform_number_input", 
			visibilityChecks:[],
		 	enableDisableChecks:[],		
			getDisplayValue:function (itemVal) {
				var val = "";
				if(itemVal != null && itemVal.length >0) {
					if(itemVal.length > 1) {
						val = itemVal.substr(0, itemVal.length-1);				
					} else {
						if(itemVal == "0") {
							val = "0";
						} else {
							val = "";
						}
					}
				}
				this.getParentItem()._numericPart = val;
				this.getParentItem()._stringPart="d";
				return val;	
			},
			elementChanged:function(numericPart, instanceValue, event) {
				var val = numericPart + "d";
				this.getForm().itemChanged(this.getParentItem(), val, event);
			}
		},
		{type:_OUTPUT_, ref:null, labelLocation:_NONE_, value:"d",getDisplayValue:function (itemVal){ return AjxMsg.days;}}
	];
	Composite_XFormItem.prototype.initializeItems.call(this);
};
Lifetime2_XFormItem.prototype.items = [];

/**
* _LIFETIME_MINUTES_ XForm item type allows time interval to be expressed only in minutes
**/
LifetimeMinutes_XFormItem = function() {}
LifetimeMinutes_XFormItem.prototype.nowrap = false;
LifetimeMinutes_XFormItem.prototype.labelWrap = true;
XFormItemFactory.createItemType("_LIFETIME_MINUTES_", "lifetime_minutes", LifetimeMinutes_XFormItem, Lifetime1_XFormItem);

LifetimeMinutes_XFormItem.prototype.initializeItems = function () {
	this.items = [
		{type:_TEXTFIELD_, ref:".", labelLocation:_NONE_,cssClass:"admin_xform_number_input", 
			visibilityChecks:[],
		 	enableDisableChecks:[],		
			getDisplayValue:function (itemVal) {
				var val = "";
				if(itemVal != null && itemVal.length >0) {
					if(itemVal.length > 1) {
						val = itemVal.substr(0, itemVal.length-1);				
					} else {
						if(itemVal == "0") {
							val = "0";
						} else {
							val = "";
						}
					}
				}
				this.getParentItem()._numericPart = val;
				this.getParentItem()._stringPart="m";
				return val;	
			},
			elementChanged:function(numericPart, instanceValue, event) {
				var val = numericPart + "m";
				this.getForm().itemChanged(this.getParentItem(), val, event);
			}
		},
		{type:_OUTPUT_, ref:null, labelLocation:_NONE_, value:"m",getDisplayValue:function (itemVal){ return AjxMsg.minutes;}}
	];
	Composite_XFormItem.prototype.initializeItems.call(this);
};
LifetimeMinutes_XFormItem.prototype.items = [];