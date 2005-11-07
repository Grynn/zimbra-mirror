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
 * The Original Code is: Zimbra Collaboration Suite.
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
*	_COS_STRING_ model item type
**/

Cos_String_XModelItem = function () {}
XModelItemFactory.createItemType("_COS_STRING_", "cos_string", Cos_String_XModelItem);


Cos_String_XModelItem.prototype.dataType = _STRING_;

Cos_String_XModelItem.prototype.getter = "getValue";
Cos_String_XModelItem.prototype.getterScope = _MODELITEM_;
Cos_String_XModelItem.prototype.setter = "setLocalValue";
Cos_String_XModelItem.prototype.setterScope = _MODELITEM_;

Cos_String_XModelItem.prototype.setValueAt = function (instance, val, ref) {
	var pathParts = new Array();
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
}

Cos_String_XModelItem.prototype.getValue = function(instance, current, ref) {
	var value = this.getLocalValue(instance);
	if (value == null) value = this.getSuperValue(instance);
	return value;
}
Cos_String_XModelItem.prototype.getSuperValue = function(instance) {
	if(!instance || !instance.cos)
		return null;
	var _ref = this.ref.replace("/", ".");
	return eval("instance.cos." + _ref);
}
Cos_String_XModelItem.prototype.getLocalValue = function(instance) {
	if(!instance)
		return null;
	var _ref = this.ref.replace("/", ".");
	return eval("instance." + _ref);
}

Cos_String_XModelItem.prototype.setLocalValue = function(value, instance, current, ref) {
	this.setValueAt(instance, value, ref);
}

Cos_String_XModelItem.prototype.validateType = XModelItem.prototype.validateString;



/**
*	_COS_NUMBER_ model item type
**/
Cos_Number_XModelItem = function (){}
XModelItemFactory.createItemType("_COS_NUMBER_", "cos_number", Cos_Number_XModelItem, Cos_String_XModelItem);
Cos_Number_XModelItem.prototype.validateType = XModelItem.prototype.validateNumber;


Cos_Enum_XModelItem = function (){}
XModelItemFactory.createItemType("_COS_ENUM_", "cos_enum", Cos_Enum_XModelItem, Cos_String_XModelItem);
Cos_Enum_XModelItem.prototype.getDefaultValue = function () {	return this.choices[0]; };

Cos_Enum_XModelItem.prototype.getChoices = function()		 {		return this.choices;		}
Cos_Enum_XModelItem.prototype.getSelection = function() 	{		return this.selection;		}

Cos_Enum_XModelItem.prototype.validateType = function (value) {
	// if the selection is open, they can enter any value they want
	var selectionIsOpen = this.getSelection() == _OPEN_;
	if (selectionIsOpen) return value;
	
	// selection is not open: it must be one of the supplied choices
	var choices = this.getChoices();
	for (var i = 0; i < choices.length; i++) {
		var choice = choices[i];
		if (AjxUtil.isInstance(choice, Object)) {
			if (choice.value == value) return value;
		} else {
			if (choice == value) return value;
		}
	}
	
	// if we get here, we didn't match any of the choices
	throw this.getModel().getErrorMessage("didNotMatchChoice", value);
}

/**
* _COS_MAILQUOTA_ XModel item type
**/
Cos_MailQuota_XModelItem = function (){}
XModelItemFactory.createItemType("_COS_MAILQUOTA_", "cos_mailquota", Cos_MailQuota_XModelItem, Cos_Number_XModelItem);
Cos_MailQuota_XModelItem.prototype.getterScope = _MODELITEM_;
Cos_MailQuota_XModelItem.prototype.setterScope = _MODELITEM_;
Cos_MailQuota_XModelItem.prototype.getter = "getValue";
Cos_MailQuota_XModelItem.prototype.setter = "setLocalValue";
Cos_MailQuota_XModelItem.prototype.maxInclusive = 2047;
Cos_MailQuota_XModelItem.prototype.minInclusive = 0;

Cos_MailQuota_XModelItem.prototype.getValue = function(instance, current, ref) {
	var value = this.getLocalValue(instance, current, ref);
	if (value == null) value = this.getSuperValue(instance, current, ref);
	if(value == 0)
		value = "0";
		
	return value;
}

Cos_MailQuota_XModelItem.prototype.getSuperValue = function(instance) {
	if(!instance)
		return null;
	var _ref = this.ref.replace("/", ".");
	var value = 0;
	if((eval("instance.cos." + _ref) != null) && (eval("instance.cos." + _ref) != 0)) {
		value = (eval("instance.cos." + _ref) / 1048576);
		if(value != Math.round(value)) {
			value = Number(value).toFixed(2);
	  	}
	} 	
//	var value = (eval("instance.cos." + _ref) != null) ? Number(eval("instance.cos." + _ref) / 1048576).toFixed(0) : 0;
	return value;
}
Cos_MailQuota_XModelItem.prototype.getLocalValue = function(instance) {
	if(!instance)
		return null;	
	var _ref = this.ref.replace("/", ".");
	var value = null;
	if(eval("instance." + _ref) != null) {
		value = (eval("instance." + _ref) / 1048576);
		if(value != Math.round(value)) {
			value = Number(value).toFixed(2);
	  	}
	} 
	return value;
}

Cos_MailQuota_XModelItem.prototype.setLocalValue = function(value, instance, current, ref) {
	var val = (value != null) ? Math.round(value * 1048576) : null;
	this.setValueAt(instance, val, ref);	
}

/**
* COS_MLIFETIME XModelItem
**/

Cos_MLifetime_XModelItem = function () {}
XModelItemFactory.createItemType("_COS_MLIFETIME_", "cos_mlifetime", Cos_MLifetime_XModelItem, Cos_String_XModelItem);
Cos_MLifetime_XModelItem.prototype.validateType = function (value) {
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
*	XForm Items that have overwritable super values
**/

/**
* Super_XFormItem - prototype for all other XForm items with overwritable super values
**/
Super_XFormItem = function () { }
XFormItemFactory.createItemType("_SUPER_FIELD_", "cos_field", Super_XFormItem, Composite_XFormItem);
Super_XFormItem.checkIfOverWriten = function() {
	return (this.getModelItem().getLocalValue(this.getInstance()) != null);
}

Super_XFormItem.updateCss = function(levels) {
	var container = this.getContainer();
	for(var ix=0; ix < levels; ix++) {
		container = container.parentNode;
	}
	if(Super_XFormItem.checkIfOverWriten.call(this)) {
		if(container.className != null && container.className != "ZmOverride")
			this._originalClassName = container.className;
		else 
			this._originalClassName	= "xform_field_container";
			
		container.className="ZmOverride";
	} else {
		if(this._originalClassName != null)
			container.className=this._originalClassName;
	}
}



/**
* SUPER_ANCHOR_HELPER
* "Reset to *** value" link
**/
Super_AnchorHelper_XFormItem = function () {}
XFormItemFactory.createItemType("_SUPER_ANCHOR_HELPER_", "super_anchor_helper", Super_AnchorHelper_XFormItem, Anchor_XFormItem);
Super_AnchorHelper_XFormItem.prototype.getAnchorTag = function(href, label) {
	if (href == null) href = this.getHref();
	if (label == null) label = this.getParentItem().getInheritedProperty("resetToSuperLabel");
	
	var inNewWindow = this.getShowInNewWindow();
	return AjxBuffer.concat(
			"<a href=\"javascript:", this.getGlobalRef(), 
			".resetToSuperValue();\"",
			">",
				label,
			"</a>");
}

Super_AnchorHelper_XFormItem.prototype.resetToSuperValue = function(event) {
	this.getForm().itemChanged(this.getParentItem(), null, event);
}

/**
*	_SUPER_TEXTFIELD_ form item type
**/
Super_Textfield_XFormItem = function () {}
XFormItemFactory.createItemType("_SUPER_TEXTFIELD_", "super_textfield", Super_Textfield_XFormItem, Super_XFormItem);

Super_Textfield_XFormItem.prototype.useParentTable = true;
Super_Textfield_XFormItem.prototype.items = [
	{	type:_TEXTFIELD_, ref:".", width:100,
		elementChanged: function(elementValue,instanceValue, event) {
			this.getForm().itemChanged(this, elementValue, event);
		},		
		onChange:Composite_XFormItem.onFieldChange,
		updateElement:function(value) {
			Super_XFormItem.updateCss.call(this,1);
			Textfield_XFormItem.prototype.updateElement.call(this, value);
		},
		forceUpdate:true
	},
	{	
		type:_SUPER_ANCHOR_HELPER_, ref:".",
		relevant:"Super_XFormItem.checkIfOverWriten.call(item)",
		relevantBehavior:_BLOCK_HIDE_,
		onChange:Composite_XFormItem.onFieldChange
	}
];

/**
*	_SUPER_CHECKBOX_ form item type
**/
Super_Checkbox_XFormItem = function () {}
XFormItemFactory.createItemType("_SUPER_CHECKBOX_", "super_checkbox", Super_Checkbox_XFormItem, Super_XFormItem);

Super_Checkbox_XFormItem.prototype.useParentTable = true;
Super_Checkbox_XFormItem.prototype.numCols = 2;
Super_Checkbox_XFormItem.prototype.initializeItems = function() {
	var anchorCssStyle = this.getInheritedProperty("anchorCssStyle");
	if(anchorCssStyle) {
		this.getItems()[1].cssStyle = anchorCssStyle;
	}	
	Composite_XFormItem.prototype.initializeItems.call(this);
	var checkBoxLabel = this.getInheritedProperty("checkBoxLabel");
	var checkBoxLabelLocation = this.getInheritedProperty("checkBoxLabelLocation");
	if(checkBoxLabel) {
		this.getItems()[0].label = checkBoxLabel;
		this.numCols = 3;
	}
	if(checkBoxLabelLocation) {
		this.getItems()[0].labelLocation = checkBoxLabelLocation;
	}
	var trueValue = this.getInheritedProperty("trueValue");
	var falseValue = this.getInheritedProperty("falseValue");	
	this.getItems()[0].trueValue = trueValue;
	this.getItems()[0].falseValue = falseValue;	
}	

Super_Checkbox_XFormItem.prototype.outputHTML = function (html, updateScript, indent, currentCol) {
	this.getForm().outputItemList(this.getItems(), this, html, updateScript, indent, this.getNumCols(), currentCol);
}

Super_Checkbox_XFormItem.prototype.items = [
	{	type:_CHECKBOX_, ref:".", align:_LEFT_,
		//trueValue:"TRUE", falseValue:"FALSE", 
		onChange:Composite_XFormItem.onFieldChange,
		updateElement:function(value) {
			Super_XFormItem.updateCss.call(this,1);
			Checkbox_XFormItem.prototype.updateElement.call(this, value);
		},
		relevantBehavior:_PARENT_,
		forceUpdate:true
	},
	{	
		type:_SUPER_ANCHOR_HELPER_, ref:".",
		relevant:"Super_XFormItem.checkIfOverWriten.call(item)",
		onChange:Composite_XFormItem.onFieldChange,
		relevantBehavior:_BLOCK_HIDE_,
		cssStyle:"width:100px"
	}
];





/**
*	SUPER_SELECT1 form item type
**/
Super_Select1_XFormItem = function () {}
XFormItemFactory.createItemType("_SUPER_SELECT1_", "super_select1", Super_Select1_XFormItem, Super_XFormItem);


Super_Select1_XFormItem.prototype.useParentTable = true;
Super_Select1_XFormItem.prototype.numCols = 3;

Super_Select1_XFormItem.prototype.items = [
	{	type:_OSELECT1_, ref:".",
		trueValue:"TRUE", falseValue:"FALSE", 
		onChange:Composite_XFormItem.onFieldChange,
		updateElement:function(value) {
			Super_XFormItem.updateCss.call(this,1);
			OSelect1_XFormItem.prototype.updateElement.call(this, value);
		}
	},
	{	
		type:_SUPER_ANCHOR_HELPER_, ref:".",
		relevant:"Super_XFormItem.checkIfOverWriten.call(item)",
		relevantBehavior:_BLOCK_HIDE_,
		onChange:Composite_XFormItem.onFieldChange
	}
];

/**
* _SUPER_LIFETIME_ XForm item type
**/

function Super_Lifetime_XFormItem() {}
XFormItemFactory.createItemType("_SUPER_LIFETIME_", "super_lifetime", Super_Lifetime_XFormItem, Super_XFormItem);
Super_Lifetime_XFormItem.prototype.numCols = 4;
Super_Lifetime_XFormItem.prototype.TIME_CHOICES = [
 				{value:"d", label:"Days"},
				{value:"h", label:"Hours"},
				{value:"m", label:"Minutes"},
				{value:"s", label:"Seconds"}
];



Super_Lifetime_XFormItem.prototype.items = [
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
			this.getForm().itemChanged(this, val, event);
		},onChange:Composite_XFormItem.onFieldChange,
		updateElement:function(value) {
			Super_XFormItem.updateCss.call(this,5);
			Textfield_XFormItem.prototype.updateElement.call(this, value);
		}
	},
	{type:_OSELECT1_, ref:".", labelLocation:_NONE_, relevantBehavior:_PARENT_, choices:Super_Lifetime_XFormItem.prototype.TIME_CHOICES,
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
		},
		updateElement:function(value) {
			Super_XFormItem.updateCss.call(this,5);
			OSelect1_XFormItem.prototype.updateElement.call(this, value);
		},
		forceUpdate:true		
	},
	{	
		type:_SUPER_ANCHOR_HELPER_, ref:".",
		relevant:"Super_XFormItem.checkIfOverWriten.call(item)",
		relevantBehavior:_BLOCK_HIDE_,
		onChange:Composite_XFormItem.onFieldChange
	}
];

/**
* _SUPER_LIFETIME1_ XForm item type
**/
function Super_Lifetime1_XFormItem() {}
XFormItemFactory.createItemType("_SUPER_LIFETIME1_", "super_lifetime1", Super_Lifetime1_XFormItem, Super_XFormItem);
Super_Lifetime1_XFormItem.prototype.numCols = 4;
Super_Lifetime1_XFormItem.prototype.TIME_CHOICES = [
 				{value:"d", label:"Days"},
				{value:"h", label:"Hours"}
];

Super_Lifetime1_XFormItem.prototype.items = [
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
			this.getForm().itemChanged(this, val, event);
		},
		onChange:Composite_XFormItem.onFieldChange,
		updateElement:function(value) {
			Super_XFormItem.updateCss.call(this,5);
			Textfield_XFormItem.prototype.updateElement.call(this, value);
		}
	},
	{type:_OSELECT1_, ref:".", labelLocation:_NONE_, relevantBehavior:_PARENT_, choices:Super_Lifetime1_XFormItem.prototype.TIME_CHOICES,
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
		},
		updateElement:function(value) {
			Super_XFormItem.updateCss.call(this,5);
			OSelect1_XFormItem.prototype.updateElement.call(this, value);
		},
		forceUpdate:true
	},
	{	
		type:_SUPER_ANCHOR_HELPER_, ref:".",
		relevant:"Super_XFormItem.checkIfOverWriten.call(item)",
		relevantBehavior:_BLOCK_HIDE_,
		onChange:Composite_XFormItem.onFieldChange
	}
];
