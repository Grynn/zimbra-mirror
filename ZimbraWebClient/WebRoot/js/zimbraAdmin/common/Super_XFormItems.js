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
Cos_String_XModelItem.prototype.getSuperValue = function(ins) {
	if(!ins || !ins._defaultValues)
		return null;
	var _ref = this.ref.replace("/", ".");
	return eval("ins._defaultValues." + _ref);
}
Cos_String_XModelItem.prototype.getLocalValue = function(ins, refPath) {
	if(!ins)
		return null;
	var _ref = this.ref.replace("/", ".");
	return eval("ins." + _ref);
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

Cos_Int_XModelItem = function (){}
XModelItemFactory.createItemType("_COS_INT_", "cos_int", Cos_Int_XModelItem, Cos_String_XModelItem);
Cos_Int_XModelItem.prototype.validateType = XModelItem.prototype.validateInt;

Cos_Enum_XModelItem = function (){}
XModelItemFactory.createItemType("_COS_ENUM_", "cos_enum", Cos_Enum_XModelItem, Cos_String_XModelItem);
Cos_Enum_XModelItem.prototype.getDefaultValue = function () {	return this.getChoices()[0]; };

Cos_Enum_XModelItem.prototype.getChoices = function()		 {
    if (typeof this.choices == "function") {  //due to the i18n complexity, we have to define the choices use the function
        this.choices = this.choices.call (this) ;
    }
    return this.choices;
}
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

Cos_List_XModelItem = function (){}
XModelItemFactory.createItemType("_COS_LIST_", "list_enum", Cos_List_XModelItem, Cos_String_XModelItem);
Cos_List_XModelItem.prototype.outputType = _LIST_;
Cos_List_XModelItem.prototype.itemDelimiter = List_XModelItem.prototype.itemDelimiter;
Cos_List_XModelItem.prototype.inputDelimiter = List_XModelItem.prototype.inputDelimiter;
Cos_List_XModelItem.prototype.listItem = List_XModelItem.prototype.listItem;
Cos_List_XModelItem.prototype.getOutputType  = List_XModelItem.prototype.getOutputType;
Cos_List_XModelItem.prototype.getItemDelimiter = List_XModelItem.prototype.getItemDelimiter;
Cos_List_XModelItem.prototype.getInputDelimiter = List_XModelItem.prototype.getInputDelimiter;
Cos_List_XModelItem.prototype.getListItem  = List_XModelItem.prototype.getListItem;
Cos_List_XModelItem.prototype.initializeItems = List_XModelItem.prototype.initializeItems;
Cos_List_XModelItem.prototype.validateType = List_XModelItem.prototype.validateType;


Cos_List_XModelItem.prototype.getSuperValue = function(ins) {
	if(!ins || !ins._defaultValues)
		return null;
	var _ref = this.ref.replace("/", ".");
	var lst = eval("ins._defaultValues." + _ref);
	
	if(lst) {
		if(this.getOutputType() == _STRING_) {
			if(lst instanceof Array) {
				return lst.join(this.getItemDelimiter());
			}
		} else {
			var retval = [];
			if(!(lst instanceof Array))
				lst = [lst];
	
			var cnt = lst.length
			for(var i=0;i<cnt;i++) {
				retval.push(lst[i]);
			}
			return retval;		
		}
	}
	
}

Cos_List_XModelItem.prototype.setLocalValue = function(val, ins, current, ref) {
	if(val && this.getOutputType() == _STRING_ && !(val instanceof Array)) {
		var value = val.split(this.getInputDelimiter());
		eval("ins."+ref+" = value");
		this.setValueAt(ins, value, ref);
	} else {
        var value = eval("ins."+ref+" = val");
        this.setValueAt(ins, value, ref);
	}
	
}

Cos_List_XModelItem.prototype.getLocalValue = function(ins, refPath) {
	if(!ins)
		return null;
	
	var _ref = this.ref.replace("/", ".");	
	var value =  eval("ins." + _ref);
	if(value && this.getOutputType() ==_STRING_ && value instanceof Array) {
		return value.join(this.getItemDelimiter());
	} else {
		return value;
	}
}
//	methods

Cos_List_XModelItem.prototype.initializeItems = function () {
	var listItem = this.listItem;
	listItem.ref = listItem.id = "#";	
	this.listItem = XModelItemFactory.createItem(listItem, this, this.getModel());
	this.listItem.initializeItems();
}


Cos_List_XModelItem.prototype.validateType = function (value) {
	return value;
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
Cos_MailQuota_XModelItem.prototype.maxInclusive = 8796093022207;
Cos_MailQuota_XModelItem.prototype.minInclusive = 0;

Cos_MailQuota_XModelItem.prototype.getValue = function(instance, current, ref) {
	var value = this.getLocalValue(instance, current, ref);
	if (value == null ) value = this.getSuperValue(instance, current, ref);
	if(typeof value == "number" && value == 0)
		value = "0";
		
	return value;
}

Cos_MailQuota_XModelItem.prototype.getSuperValue = function(ins) {
	if(!ins)
		return null;
	var _ref = this.ref.replace("/", ".");
	//var value = 0;
	var value = null;
	if((eval("ins._defaultValues." + _ref) != null) && (eval("ins._defaultValues." + _ref) != "")) {
		value = (eval("ins._defaultValues." + _ref) / 1048576);
		if(value != Math.round(value)) {
			value = Number(value).toFixed(2);
	  	}
	} 	
//	var value = (eval("ins._defaultValues." + _ref) != null) ? Number(eval("ins._defaultValues." + _ref) / 1048576).toFixed(0) : 0;
	return value;
}
Cos_MailQuota_XModelItem.prototype.getLocalValue = function(ins, refPath) {
	if(!ins)
		return null;	
	var _ref = this.ref.replace("/", ".");
	var value =null;
	if(eval("ins." + _ref) != null && eval("ins." + _ref) != "") {
		value = (eval("ins." + _ref) / 1048576);
		if(value != Math.round(value)) {
			value = Number(value).toFixed(2);
	  	}
	} 
	return value || eval("ins." + _ref) ;
}

Cos_MailQuota_XModelItem.prototype.setLocalValue = function(value, instance, current, ref) {
	if (value != null && value != "") {
		 value = Math.round(value * 1048576);
	}
	this.setValueAt(instance, value, ref);	
}

/**
* COS_MLIFETIME XModelItem
**/

Cos_MLifetime_XModelItem = function () {}
XModelItemFactory.createItemType("_COS_MLIFETIME_", "cos_mlifetime", Cos_MLifetime_XModelItem, Cos_String_XModelItem);
Cos_MLifetime_XModelItem.prototype.validateType = function (value) {
	var val = 1;
	var lastChar = "d";
	if(value != null && value.length >0) {
		var lastChar = (value.toLowerCase()).charAt(value.length-1);
		lastChar = (lastChar == "d" || lastChar == "h" || lastChar== "m" || lastChar == "s") ? lastChar : "s"
		val = parseInt(value);
	}
	
	val =  [XModelItem.prototype.validateNumber.call(this, val),lastChar].join("");
	return val;
}

/**
* _COS_HOSTNAME_OR_IP_
**/
Cos_HostNameOrIp_XModelItem = function (){}
XModelItemFactory.createItemType("_COS_HOSTNAME_OR_IP_", "cos_hostname_or_ip", Cos_HostNameOrIp_XModelItem, Cos_String_XModelItem);
Cos_HostNameOrIp_XModelItem.prototype.validateType = XModelItem.prototype.validateString;
Cos_HostNameOrIp_XModelItem.prototype.maxLength = 256;
//Cos_HostNameOrIp_XModelItem.prototype.pattern = [ AjxUtil.HOST_NAME_RE, AjxUtil.IP_ADDRESS_RE ];
Cos_HostNameOrIp_XModelItem.prototype.pattern =  [AjxUtil.HOST_NAME_RE, AjxUtil.IP_ADDRESS_RE, AjxUtil.HOST_NAME_WITH_PORT_RE];
/**
* _COS_PORT_
**/
Cos_Port_XModelItem = function (){}
XModelItemFactory.createItemType("_COS_PORT_", "cos_port", Cos_Port_XModelItem, Cos_Number_XModelItem);
Cos_Port_XModelItem.prototype.validateType = XModelItem.prototype.validateNumber;
Cos_Port_XModelItem.prototype.minInclusive = 0;
Cos_Port_XModelItem.prototype.maxInclusive = 65535;

/**
* _COS_SUBNET_
**/
Cos_Subnet_XModelItem = function (){}
XModelItemFactory.createItemType("_COS_SUBNET_", "cos_subnet", Cos_Subnet_XModelItem, Cos_String_XModelItem);
Cos_Subnet_XModelItem.prototype.validateType = XModelItem.prototype.validateString;
Cos_Subnet_XModelItem.prototype.maxLength = 256;
Cos_Subnet_XModelItem.prototype.pattern =  [AjxUtil.IP_ADDRESS_RE, AjxUtil.SUBNET_RE];

/**
*	XForm Items that have overwritable super values
**/

/**
* Super_XFormItem - prototype for all other XForm items with overwritable super values
**/
Super_XFormItem = function () { }
XFormItemFactory.createItemType("_SUPER_FIELD_", "cos_field", Super_XFormItem, Composite_XFormItem);
Super_XFormItem.prototype.bmolsnr = true;
Super_XFormItem.prototype.visibilityChecks = [ZaItem.hasReadPermission];
Super_XFormItem.prototype.enableDisableChecks = [ZaItem.hasWritePermission];
Super_XFormItem.checkIfOverWriten = function() {
	if(!ZaItem.hasWritePermission.call(this))
		return false;
		
	if(this.getModelItem() && this.getModelItem().getLocalValue(this.getInstance(), this.refPath)==null)
		return false;
	else if (this.getModelItem() &&  (this.getModelItem().getLocalValue(this.getInstance(), this.refPath) instanceof AjxVector) && 
	(this.getModelItem().getLocalValue(this.getInstance(), this.refPath).size==0) )	
		return false;
	else 
		return true;
}

Super_XFormItem.updateCss = function(levels) {
	var container = this.getContainer();
	if(this.getParentItem().getInheritedProperty("useParentTable"))
		levels = levels-4;
	
	for(var ix=0; ix < levels; ix++) {
		container = container.parentNode;
	}
	if(Super_XFormItem.checkIfOverWriten.call(this)) {
		if(container.className != null && container.className != "ZaOverride")
			this._originalClassName = container.className;
		else 
			this._originalClassName	= "xform_field_container";
			
		container.className="ZaOverride";
	} else {
		if(this._originalClassName != null)
			container.className=this._originalClassName;
		else
			container.className="xform_field_container";
	}
}



/**
* SUPER_ANCHOR_HELPER
* "Reset to *** value" link
**/
Super_AnchorHelper_XFormItem = function () {}
XFormItemFactory.createItemType("_SUPER_ANCHOR_HELPER_","super_anchor_helper", Super_AnchorHelper_XFormItem, Dwt_Button_XFormItem);
Super_AnchorHelper_XFormItem.prototype.containerCssClass = "xform_container xform_override_btn_contaier";
// implement the following to actually construct the instance of your widget
Super_AnchorHelper_XFormItem.prototype.constructWidget = function () {
	var widget = this.widget = new DwtButton(this.getForm(), this.getCssClass());
	var height = this.getHeight();
	var width = this.getWidth();
	//if(!width) width = "100%";
	var el = null;
	if (width != null || height != null){
		el = widget.getHtmlElement();
		if (width != null) el.style.width = width;
		if (height != null) el.style.height = height;
	} 

	var label = this.getLabel();
	
	if(!label)
		label = this.getParentItem().getInheritedProperty("resetToSuperLabel");		
		
	widget.setText(label);

	var ls = new AjxListener(this, Super_AnchorHelper_XFormItem.prototype.resetToSuperValue);
	widget.addSelectionListener(ls);

	return widget;
}

Super_AnchorHelper_XFormItem.prototype.resetToSuperValue = function(event) {
	this.getForm().itemChanged(this.getParentItem(), null, event);
}
Super_AnchorHelper_XFormItem.prototype.isBlockElement = true;

/**
*	_SUPER_TEXTFIELD_ form item type
**/
Super_Textfield_XFormItem = function () {}
XFormItemFactory.createItemType("_SUPER_TEXTFIELD_", "super_textfield", Super_Textfield_XFormItem, Super_XFormItem);

Super_Textfield_XFormItem.prototype.useParentTable = false;
Super_Textfield_XFormItem.prototype.txtBoxLabel = null;
Super_Textfield_XFormItem.prototype.numCols = 3;
Super_Textfield_XFormItem.prototype.colSizes = ["275px","275px","150px"];
Super_Textfield_XFormItem.prototype.colSpan = 3;
Super_Textfield_XFormItem.prototype.nowrap = false;
Super_Textfield_XFormItem.prototype.labelWrap = true;
if(appNewUI){
Super_Textfield_XFormItem.prototype.colSizes = ["275px","*","150px"];
Super_Textfield_XFormItem.prototype.labelCssStyle = "border-right: 1px solid black;";
Super_Textfield_XFormItem.prototype.tableCssClass = "grid_composite_table";
}

SuperWiz_Textfield_XFormItem = function () {}
XFormItemFactory.createItemType("_SUPERWIZ_TEXTFIELD_", "superwiz_textfield", SuperWiz_Textfield_XFormItem, Super_Textfield_XFormItem);
SuperWiz_Textfield_XFormItem.prototype.colSizes=["200px", "250px","150px"];
SuperWiz_Textfield_XFormItem.prototype.visibilityChecks = [ZaItem.hasWritePermission];
SuperWiz_Textfield_XFormItem.prototype.enableDisableChecks = [ZaItem.hasWritePermission];
if(appNewUI){
SuperWiz_Textfield_XFormItem.prototype.labelCssStyle = "";
}

Super_Textfield_XFormItem.prototype.initializeItems = function() {
	var txtBoxLabel = this.getInheritedProperty("txtBoxLabel");
    var labelCssStyle = this.getInheritedProperty("labelCssStyle");
    var labelCssClass = this.getInheritedProperty("labelCssClass");
	var textFieldCssClass = this.getInheritedProperty("textFieldCssClass");
	var textFieldCssStyle = this.getInheritedProperty("textFieldCssStyle");
	var textFieldWidth = this.getInheritedProperty("textFieldWidth");
	var toolTip = this.getInheritedProperty("toolTipContent");

	var getDisplayValue = this.getInheritedProperty("getDisplayValue");
	
	var txtField =	{	
		type:_TEXTFIELD_, ref:".",align:_LEFT_,
		elementChanged: function(elementValue,instanceValue, event) {
			this.getForm().itemChanged(this, elementValue, event);
		},		
		onChange:Composite_XFormItem.onFieldChange,
		toolTipContent: toolTip,
		updateElement:function(value) {
			Super_XFormItem.updateCss.call(this,5);
			Textfield_XFormItem.prototype.updateElement.call(this, value);
		},
		label:txtBoxLabel,
        labelCssStyle:labelCssStyle,
        labelCssClass: labelCssClass,
		labelLocation:(txtBoxLabel ? _LEFT_ : _NONE_),
		cssClass:textFieldCssClass,
		cssStyle:textFieldCssStyle,
		width:textFieldWidth,
		forceUpdate:true,
		nowrap:this.getInheritedProperty("nowrap"),
		labelWrap:this.getInheritedProperty("labelWrap")		
	};
	
	if(getDisplayValue) {
		txtField.getDisplayValue = getDisplayValue;
	}
	
	var anchorCssStyle = this.getInheritedProperty("anchorCssStyle");
	
	var anchorHlpr = {	
		type:_SUPER_ANCHOR_HELPER_, ref:".",
		visibilityChecks:[Super_XFormItem.checkIfOverWriten],
		visibilityChangeEventSources:[this.getRefPath()],
		onChange:Composite_XFormItem.onFieldChange,
		cssStyle: (anchorCssStyle ? anchorCssStyle : "width:150px")
	};
	this.items = [txtField,anchorHlpr];
	Composite_XFormItem.prototype.initializeItems.call(this);
	

}	

Super_Textfield_XFormItem.prototype.items = [];

/**
*	_SUPER_TEXTAREA_ form item type
**/
Super_Textarea_XFormItem = function () {}
XFormItemFactory.createItemType("_SUPER_TEXTAREA_", "super_textarea", Super_Textarea_XFormItem, Super_XFormItem);

Super_Textarea_XFormItem.prototype.useParentTable = false;
Super_Textarea_XFormItem.prototype.txtBoxLabel = null;
Super_Textarea_XFormItem.prototype.numCols = 3;
Super_Textarea_XFormItem.prototype.colSizes = ["275px","275px","150px"];
Super_Textarea_XFormItem.prototype.colSpan = 3;
Super_Textarea_XFormItem.prototype.nowrap = false;
Super_Textarea_XFormItem.prototype.labelWrap = true;
if(appNewUI){
Super_Textarea_XFormItem.prototype.colSizes = ["275px","*","150px"];
Super_Textarea_XFormItem.prototype.tableCssClass = "grid_composite_table";
}

SuperWiz_Textarea_XFormItem = function () {}
XFormItemFactory.createItemType("_SUPERWIZ_TEXTAREA_", "superwiz_textarea", SuperWiz_Textarea_XFormItem, Super_Textarea_XFormItem);
SuperWiz_Textarea_XFormItem.prototype.colSizes=["200px", "250px","150px"];
SuperWiz_Textarea_XFormItem.prototype.visibilityChecks = [ZaItem.hasWritePermission];
SuperWiz_Textarea_XFormItem.prototype.enableDisableChecks = [ZaItem.hasWritePermission];


Super_Textarea_XFormItem.prototype.initializeItems = function() {
	var txtBoxLabel = this.getInheritedProperty("txtBoxLabel");
    var txtBoxLabelCssStyle = this.getInheritedProperty("txtBoxLabelCssStyle");
	var textAreaCssClass = this.getInheritedProperty("textAreaCssClass");
	var textAreaCssStyle = this.getInheritedProperty("textAreaCssStyle");
	var textAreaWidth = this.getInheritedProperty("textAreaWidth");
	var toolTip = this.getInheritedProperty("toolTipContent");
	var labelCssStyle = this.getInheritedProperty("labelCssStyle");
    var labelCssClass = this.getInheritedProperty("labelCssClass");
	
	var txtArea =	{	
		type:_TEXTAREA_, ref:".",align:_LEFT_,
		elementChanged: function(elementValue,instanceValue, event) {
			this.getForm().itemChanged(this, elementValue, event);
		},		
		onChange:Composite_XFormItem.onFieldChange,
		toolTipContent: toolTip,
		updateElement:function(value) {
			Super_XFormItem.updateCss.call(this,5);
			Textarea_XFormItem.prototype.updateElement.call(this, value);
		},
		label:txtBoxLabel,
		labelLocation:(txtBoxLabel ? _LEFT_ : _NONE_),
		labelCssStyle: labelCssStyle,
        labelCssClass: labelCssClass,
		cssClass:textAreaCssClass,
		cssStyle:textAreaCssStyle,
		width:textAreaWidth,
		forceUpdate:true,
		nowrap:this.getInheritedProperty("nowrap"),
		labelWrap:this.getInheritedProperty("labelWrap")		
	};
	
	var anchorCssStyle = this.getInheritedProperty("anchorCssStyle");
	
	var anchorHlpr = {	
		type:_SUPER_ANCHOR_HELPER_, ref:".",
		visibilityChecks:[Super_XFormItem.checkIfOverWriten],
		visibilityChangeEventSources:[this.getRefPath()],
		onChange:Composite_XFormItem.onFieldChange,
		cssStyle: (anchorCssStyle ? anchorCssStyle : "width:150px")
	};
	this.items = [txtArea,anchorHlpr];
	Composite_XFormItem.prototype.initializeItems.call(this);
}	

Super_Textarea_XFormItem.prototype.items = [];

/**
*	_SUPER_CHECKBOX_ form item type
**/
Super_Checkbox_XFormItem = function () {}
XFormItemFactory.createItemType("_SUPER_CHECKBOX_", "super_checkbox", Super_Checkbox_XFormItem, Super_XFormItem);

SuperWiz_Checkbox_XFormItem = function () {}
XFormItemFactory.createItemType("_SUPER_WIZ_CHECKBOX_", "super_wiz_checkbox", SuperWiz_Checkbox_XFormItem, Super_Checkbox_XFormItem);
SuperWiz_Checkbox_XFormItem.prototype.colSizes = ["200px","300px","150px"];
SuperWiz_Checkbox_XFormItem.prototype.visibilityChecks = [ZaItem.hasWritePermission];
SuperWiz_Checkbox_XFormItem.prototype.enableDisableChecks = [ZaItem.hasWritePermission];
if(appNewUI){
SuperWiz_Checkbox_XFormItem.prototype.labelCssStyle = "";
SuperWiz_Checkbox_XFormItem.prototype.labelCssClass = "";
SuperWiz_Checkbox_XFormItem.prototype.checkBoxLabelLocation = _RIGHT_;
SuperWiz_Checkbox_XFormItem.prototype.checkboxSubLabel = "";
SuperWiz_Checkbox_XFormItem.prototype.checkboxAlign = _RIGHT_;
SuperWiz_Checkbox_XFormItem.prototype.colSizes = ["200px","*","150px"];
}

Super_Checkbox_XFormItem.prototype.useParentTable = false;
Super_Checkbox_XFormItem.prototype.numCols = 3;
Super_Checkbox_XFormItem.prototype.colSizes = ["275px","275px","*"];
Super_Checkbox_XFormItem.prototype.nowrap = false;
Super_Checkbox_XFormItem.prototype.labelWrap = true;
Super_Checkbox_XFormItem.prototype.checkboxSubLabel = null;
if(appNewUI){
Super_Checkbox_XFormItem.prototype.labelCssStyle = "border-right: 1px solid black;";
Super_Checkbox_XFormItem.prototype.labelCssClass = "gridGroupBodyLabel";
Super_Checkbox_XFormItem.prototype.tableCssClass = "grid_composite_table";
Super_Checkbox_XFormItem.prototype.colSizes = ["275px","*","150px"];
}else{
Super_Checkbox_XFormItem.prototype.labelCssClass = "xform_checkbox";
}

Super_Checkbox_XFormItem.prototype.initializeItems = function() {
	var anchorCssStyle = this.getInheritedProperty("anchorCssStyle");
	var checkboxSubLabel = this.getInheritedProperty("checkboxSubLabel");
    var checkLabelCssClass = this.getInheritedProperty("labelCssClass");

	var chkBox = {
		type:_CHECKBOX_, ref:".",  labelCssClass:checkLabelCssClass, subLabel:checkboxSubLabel,
		onChange:Composite_XFormItem.onFieldChange,
		updateElement:function(value) {
			Super_XFormItem.updateCss.call(this,5);
			Checkbox_XFormItem.prototype.updateElement.call(this, value);
		},
		trueValue:this.getInheritedProperty("trueValue"),
		falseValue:this.getInheritedProperty("falseValue"),
		forceUpdate:true
	};
	var chkBoxElementChanged = this.getInheritedProperty("checkBoxElementChanged");
	if(chkBoxElementChanged) {
		chkBox.elementChanged = chkBoxElementChanged;
	}
	var anchorHlpr = {	
		type:_SUPER_ANCHOR_HELPER_, ref:".",
		visibilityChecks:[Super_XFormItem.checkIfOverWriten],
		visibilityChangeEventSources:[this.getRefPath()],
		onChange:Composite_XFormItem.onFieldChange,
		cssStyle:"width:150px"
	};
	
	var customOvewriteChecks = this.getInheritedProperty("customOvewriteChecks");
	if(customOvewriteChecks) {
		anchorHlpr.visibilityChecks = customOvewriteChecks; 
	}
	
	if(anchorCssStyle) {
		anchorHlpr.cssStyle = anchorCssStyle;
	} 
	
	
	var checkBoxLabel = this.getInheritedProperty("checkBoxLabel");
	if(checkBoxLabel) {
		chkBox.label = checkBoxLabel;
		chkBox.labelWrap = this.getInheritedProperty("labelWrap");
        chkBox.labelCssStyle = this.getInheritedProperty("labelCssStyle");
		this.numCols = 3;
		this.colSpan= this.getInheritedProperty("conSpan") || 3;
	}
	
	var checkBoxLabelLocation = this.getInheritedProperty("checkBoxLabelLocation");
	if(checkBoxLabelLocation) {
		chkBox.labelLocation = checkBoxLabelLocation;
	}

    var checkBoxAlign = this.getInheritedProperty("checkboxAlign");
	if(checkBoxLabelLocation) {
		chkBox.align = checkBoxAlign;
	}
	
	this.items = [chkBox,anchorHlpr];

	Composite_XFormItem.prototype.initializeItems.call(this);
}	

Super_Checkbox_XFormItem.prototype.items = []; 



/**
*	SUPER__HOSTPORT_ form item type
**/
Super_HostPort_XFormItem = function () {}
XFormItemFactory.createItemType("_SUPER_HOSTPORT_", "super_hostport", Super_HostPort_XFormItem, Super_XFormItem);
Super_HostPort_XFormItem.prototype.colSizes = ["275px","275px","150px"];
Super_HostPort_XFormItem.prototype.useParentTable = false;
Super_HostPort_XFormItem.prototype.numCols = 3;
Super_HostPort_XFormItem.prototype.colSpan = 3;
Super_HostPort_XFormItem.prototype.initializeItems = function() {
	
	var txtField = {	type:_HOSTPORT_, ref:".",
		onChange:Composite_XFormItem.onFieldChange,
		onClick: "Super_HostPort_XFormItem.handleClick",
		onMouseout: "Super_HostPort_XFormItem.handleMouseout",
		updateElement:function(value) {
			Super_XFormItem.updateCss.call(this,5);
			XFormItem.prototype.updateElement.call(this, value);
		}
	};
	var anchorHlpr = {	
		type:_SUPER_ANCHOR_HELPER_, ref:".",
		visibilityChecks:[Super_XFormItem.checkIfOverWriten],
		visibilityChangeEventSources:[this.getRefPath()],
		onChange:Composite_XFormItem.onFieldChange,cssStyle:"width:150px"
	}
	
	var anchorCssStyle = this.getInheritedProperty("anchorCssStyle");
	if(anchorCssStyle) {
		anchorHlpr.cssStyle = anchorCssStyle;
	} 
	
	var textBoxLabel = this.getInheritedProperty("textBoxLabel");
	if(textBoxLabel) {
		txtField.label = textBoxLabel;
	}
	this.items = [txtField,anchorHlpr];
	Composite_XFormItem.prototype.initializeItems.call(this);
}	
Super_HostPort_XFormItem.prototype.items = [];

Super_HostPort_XFormItem.handleClick =
function (event, _parent) {
	//DBG.println(AjxDebug.DBG1, "Handle Click from Super Items ...");
	var p = _parent || this ; //used for the call from the HostPort_XFormItem
	p = p.getParentItem () ; 
	var focusFunc = p.getInheritedProperty("onClick") ;
	if (focusFunc != null && focusFunc != "") {
		var func = new Function ("event", "item", "return " + focusFunc + "( event, item);") ;
		func (event, p) ;
	}
}

Super_HostPort_XFormItem.handleMouseout =
function (event, _parent) {
	//DBG.println(AjxDebug.DBG1, "Handle onmouseout event ...");
	var p = _parent || this ;
	p = p.getParentItem () ; //get the super_textfield item from the _textfield_
	var focusFunc = p.getInheritedProperty("onMouseout") ;
	if (focusFunc != null && focusFunc != "") {
		var func = new Function ("event",  "item", "return " + focusFunc + "(event, item);") ;
		func (event, p) ;
	}
}


/**
*	SUPER_DWT_CHOOSER form item type
**/
Super_DwtChooser_XFormItem = function () {}
XFormItemFactory.createItemType("_SUPER_DWT_CHOOSER_", "super_dwt_chooser", Super_DwtChooser_XFormItem, Super_XFormItem);
Super_DwtChooser_XFormItem.prototype.numCols = 1;

Super_DwtChooser_XFormItem.prototype.sorted = true;
Super_DwtChooser_XFormItem.prototype.layoutStyle = DwtChooser.HORIZ_STYLE;
Super_DwtChooser_XFormItem.prototype.sourceRef = ".";
Super_DwtChooser_XFormItem.prototype.widgetClass = DwtChooser;
Super_DwtChooser_XFormItem.prototype.align = _CENTER_;

Super_DwtChooser_XFormItem.prototype.initializeItems = function() {
	var anchorCssStyle = this.getInheritedProperty("anchorCssStyle");
	var sorted = this.getInheritedProperty("sorted");
	var layoutStyle = this.getInheritedProperty("layoutStyle");	
	var sourceRef = this.getInheritedProperty("sourceRef");	
	var widgetClass = this.getInheritedProperty("widgetClass");	
	var tableWidth = this.getInheritedProperty("tableWidth");
	var labelWidth = this.getInheritedProperty("labelWidth");
	var splitButtons = this.getInheritedProperty("splitButtons");
	var resetToSuperLabel = this.getInheritedProperty("resetToSuperLabel");
	var listWidth = this.getInheritedProperty("listWidth");
	var listHeight = this.getInheritedProperty("listHeight");

	var anchorItem = {	
			type:_SUPER_ANCHOR_HELPER_, ref:".",
			visibilityChecks:[Super_XFormItem.checkIfOverWriten],
			visibilityChangeEventSources:[this.getRefPath()],
			cssSyle:(anchorCssStyle ? anchorCssStyle : "width:150px;"),
			onChange:Composite_XFormItem.onFieldChange,
			label:resetToSuperLabel,align:_CENTER_,
			containerCssStyle:"width:90%;float:center;align:center;text-align:center;"
		};
	var chooserItem = {	type:_DWT_CHOOSER_, ref:".",onChange:Composite_XFormItem.onFieldChange,
			updateElement:function(value) {
				Super_XFormItem.updateCss.call(this,5);
				this.updateWidget(value, true, Super_DwtChooser_XFormItem.getElemValue);
			},
			listSize:"90%",
			sorted:sorted, layoutStyle:layoutStyle,sourceRef:sourceRef,widgetClass:widgetClass,
			tableWidth:(tableWidth ? tableWidth : null), 
			labelWidth : (labelWidth ? labelWidth : null), 
			listWidth : (listWidth ? listWidth : null), 
			listHeight : (listHeight ? listHeight : null), 
			splitButtons : (splitButtons ? splitButtons : null) 			
		};
	this.items = [
		{type:_GROUP_, align:_CENTER_, ref:".",width:"100%", numCols:3, colSizes:["*","200px","*" ],
			items:[
				{type:_CELLSPACER_},
				anchorItem,
				{type:_CELLSPACER_}
			],onChange:this.getInheritedProperty("onChange")
		},
		chooserItem];
	Composite_XFormItem.prototype.initializeItems.call(this);
}	
Super_DwtChooser_XFormItem.getElemValue = function () {
	return this.toString();
}
Super_DwtChooser_XFormItem.prototype.items = [];





/**
*	_ZIMLET_SELECT_CHECK_ form item type
**/
Zimlet_SelectCheck_XFormItem = function () {}
XFormItemFactory.createItemType("_ZIMLET_SELECT_CHECK_", "zimlet_select_check", Zimlet_SelectCheck_XFormItem, Super_XFormItem);
Zimlet_SelectCheck_XFormItem.prototype.numCols=2;
Zimlet_SelectCheck_XFormItem.prototype.colSizes=["275px","275px"];
Zimlet_SelectCheck_XFormItem.prototype.nowrap = false;
Zimlet_SelectCheck_XFormItem.prototype.labelWrap = true;
Zimlet_SelectCheck_XFormItem.prototype.items = [];
Zimlet_SelectCheck_XFormItem.prototype.labelWidth = "275px";

Zimlet_SelectCheck_XFormItem.prototype.initializeItems = function() {
	var selectRef = this.getInheritedProperty("selectRef");
	var checkBoxLabel = this.getInheritedProperty("checkBoxLabel");
	var choices = this.getInheritedProperty("choices");	
	var checkBox = {type:_CHECKBOX_, ref:".",
		label:checkBoxLabel, labelLocation:_RIGHT_,
		elementChanged:function(elementValue,instanceValue, event) {
			if(!elementValue) {
				this.getForm().itemChanged(this.getParentItem(), null, event);	
			}
		},
		updateElement:function(value) {
			this.getElement().checked = value;
		}
	};
	
	var selectChck = {
		type:_OSELECT_CHECK_,
		choices:choices,
		colSpan:3,
		ref:selectRef,
		width:"275px",
		onChange:function (value, event, form) {
			if (this.getParentItem() && this.getParentItem().getParentItem() && this.getParentItem().getParentItem().getOnChangeMethod()) {
				return this.getParentItem().getParentItem().getOnChangeMethod().call(this, value, event, form);
			} else {
				return this.setInstanceValue(value);
			}
		},
		forceUpdate:true,
		updateElement:function(value) {
			OSelect_XFormItem.prototype.updateElement.call(this, value);
		},
		cssStyle:"margin-bottom:5px;margin-top:5px;border:2px inset gray;"				
	};
	
	var selectChckGrp = {
		type:_GROUP_,
		numCols:3,
		colSizes:["130px","15px","130px"],
		items:[
			selectChck,
			{type:_DWT_BUTTON_,label:ZaMsg.SelectAll,width:"120px",
				onActivate:function (ev) {
					var lstElement = this.getParentItem().items[0];
					if(lstElement) {
						lstElement.selectAll(ev);
					}
				}
			},
			{type:_CELLSPACER_,width:"15px"},
			{type:_DWT_BUTTON_,label:ZaMsg.DeselectAll,width:"120px",
				onActivate:function (ev) {
					var lstElement = this.getParentItem().items[0];
					if(lstElement) {
						lstElement.deselectAll(ev);
					}
				}
			}
		]
		
	}
		
	this.items = [checkBox,{type:_CELLSPACER_,width:this.labelWidth},selectChckGrp];
	
	
	Composite_XFormItem.prototype.initializeItems.call(this);
}

/**
*	_SUPER_SELECT_CHECK_ form item type
**/
SuperSelect_Check_XFormItem = function () {}
XFormItemFactory.createItemType("_SUPER_SELECT_CHECK_", "super_select_check", SuperSelect_Check_XFormItem, Super_XFormItem);
SuperSelect_Check_XFormItem.prototype.numCols=2;
SuperSelect_Check_XFormItem.prototype.colSizes=["275px","275px"];
SuperSelect_Check_XFormItem.prototype.nowrap = false;
SuperSelect_Check_XFormItem.prototype.labelWrap = true;
SuperSelect_Check_XFormItem.prototype.items = [];
SuperSelect_Check_XFormItem.prototype.labelWidth = "275px";

SuperSelect_Check_XFormItem.prototype.initializeItems = function() {
	var selectRef = this.getInheritedProperty("selectRef");
	var limitLabel = this.getInheritedProperty("limitLabel");
	var choices = this.getInheritedProperty("choices");	
	var radioBox1 = {type:_RADIO_, groupname:this.id+"zimlet_select_check_grp"+selectRef,ref:".",
		label:ZaMsg.NAD_UseCosSettings, labelLocation:_RIGHT_,
		elementChanged:function(elementValue,instanceValue, event) {
			if(elementValue==true) {
				this.getForm().itemChanged(this.getParentItem(), null, event);	
			}
		},
		updateElement:function() {
			this.getElement().checked = !this.getModelItem().getLocalValue(this.getInstance(), this.refPath);
		}
		
	};
	
	var radioBox2 = {type:_RADIO_, groupname:this.id+"zimlet_select_check_grp"+selectRef,ref:".",
		label:limitLabel, labelLocation:_RIGHT_ ,
		updateElement:function() {
			this.getElement().checked = this.getModelItem().getLocalValue(this.getInstance(), this.refPath);
		},		
		elementChanged:function(elementValue,instanceValue, event) {
			var arr = this.getModelItem().getSuperValue(this.getInstance());
			var arr2 = [];
			if(arr) {
				var cnt = arr.length;
				for(var i=0;i<cnt;i++) {
					arr2.push(arr[i]);
				}
			}
			this.getForm().itemChanged(this.getParentItem(), arr2, event);	
		}
	};	
	
	var selectChck = {
		type:_OSELECT_CHECK_,
		choices:choices,
		colSpan:3,
		ref:selectRef,
		width:"275px",
		onChange:function (value, event, form) {
			if (this.getParentItem() && this.getParentItem().getParentItem() && this.getParentItem().getParentItem().getOnChangeMethod()) {
				return this.getParentItem().getParentItem().getOnChangeMethod().call(this, value, event, form);
			} else {
				return this.setInstanceValue(value);
			}
		},
		forceUpdate:true,
		updateElement:function(value) {
			if (!(value instanceof Array))
				value = [value];
				
			var cnt = value.length;
			for(var i=cnt-1;i>=0;i--) {
				if(value[i]==ZaZimlet.NULL_ZIMLET) {
					value.splice(i,1);
					break;
				}
			}
			OSelect_XFormItem.prototype.updateElement.call(this, value);
		},
		cssStyle:"margin-bottom:5px;margin-top:5px;border:2px inset gray;"				
	};
	
	var selectChckGrp = {
		type:_GROUP_,
		numCols:3,
		colSizes:["130px","15px","130px"],
		items:[
			selectChck,
			{type:_DWT_BUTTON_,label:ZaMsg.SelectAll,width:"120px",
				onActivate:function (ev) {
					var lstElement = this.getParentItem().items[0];
					if(lstElement) {
						lstElement.selectAll(ev);
					}
				}
			},
			{type:_CELLSPACER_,width:"15px"},
			{type:_DWT_BUTTON_,label:ZaMsg.DeselectAll,width:"120px",
				onActivate:function (ev) {
					var lstElement = this.getParentItem().items[0];
					if(lstElement) {
						lstElement.deselectAll(ev);
					}
				}
			}
		]
		
	}
		
	this.items = [radioBox1,radioBox2,{type:_CELLSPACER_,width:this.labelWidth},selectChckGrp/*,{type:_CELLSPACER_,width:"15px"},anchorHlpr*/];
	
	
	Composite_XFormItem.prototype.initializeItems.call(this);
}

/**
*	_SUPER_WIZ_SELECT_CHECK_ form item type
**/
SuperWiz_Select_XFormItem = function () {}
XFormItemFactory.createItemType("_SUPER_WIZ_SELECT_CHECK_", "super_wiz_select_check", SuperWiz_Select_XFormItem, SuperSelect_Check_XFormItem);
SuperWiz_Select_XFormItem.prototype.numCols=2;
SuperWiz_Select_XFormItem.prototype.colSizes=["200px","275px"];
SuperWiz_Select_XFormItem.prototype.labelWidth = "200px";

/**
*	_SUPER_ZIMLET_SELECT_ form item type
**/
SuperZimlet_Select_XFormItem = function () {}
XFormItemFactory.createItemType("_SUPER_ZIMLET_SELECT_", "super_zimlet_select", SuperZimlet_Select_XFormItem, Super_XFormItem);
SuperZimlet_Select_XFormItem.prototype.numCols=2;
SuperZimlet_Select_XFormItem.prototype.colSizes=["275px","*"];
SuperZimlet_Select_XFormItem.prototype.nowrap = false;
SuperZimlet_Select_XFormItem.prototype.labelWrap = true;
SuperZimlet_Select_XFormItem.prototype.items = [];
SuperZimlet_Select_XFormItem.prototype.labelWidth = "275px";
if(appNewUI){
SuperZimlet_Select_XFormItem.prototype.colSizes=["100px","*"];
SuperZimlet_Select_XFormItem.prototype.labelWidth = "100px";
}

SuperZimlet_Select_XFormItem.prototype.initializeItems = function() {
	var selectRef = this.getInheritedProperty("selectRef");
	var limitLabel = this.getInheritedProperty("limitLabel");
	var choices = this.getInheritedProperty("choices");	
	var radioBox1 = {type:_RADIO_, groupname:this.id+"zimlet_select_check_grp"+selectRef,ref:".",
		label:ZaMsg.NAD_UseCosSettings, labelLocation:_RIGHT_,
		elementChanged:function(elementValue,instanceValue, event) {
			if(elementValue==true) {
				this.getForm().itemChanged(this.getParentItem(), null, event);	
			}
		},
		updateElement:function() {
			this.getElement().checked = !this.getModelItem().getLocalValue(this.getInstance(), this.refPath);
		}
		
	};
	
	var radioBox2 = {type:_RADIO_, groupname:this.id+"zimlet_select_check_grp"+selectRef,ref:".",
		label:limitLabel, labelLocation:_RIGHT_ ,
		updateElement:function() {
			this.getElement().checked = this.getModelItem().getLocalValue(this.getInstance(), this.refPath);
		},		
		elementChanged:function(elementValue,instanceValue, event) {
			var arr = this.getModelItem().getSuperValue(this.getInstance());
			var arr2 = [];
			if(arr) {
				var cnt = arr.length;
				for(var i=0;i<cnt;i++) {
					arr2.push(arr[i]);
				}
			}
			this.getForm().itemChanged(this.getParentItem(), arr2, event);	
		}
	};	
	
	var selectChck = {
		type:_ZA_ZIMLET_SELECT_,
		choices:choices,
		colSpan:4,
		ref:selectRef,
		label:"",
		labelLocation:_NONE_,
		width:"500px",
		cssStyle:"margin-bottom:5px;margin-top:5px;border:2px inset gray;"				
	};
	
	var selectChckGrp = {
		type:_GROUP_,
		numCols:4,
		colSizes:["130px","15px","130px"],
		items:[
			selectChck,
			{type:_DWT_BUTTON_,label:ZaMsg.SelectAll,width:"120px",
				onActivate:function (ev) {
					var lstElement = this.getParentItem().items[0];
					if(lstElement) {
						lstElement.selectAll(ev);
					}
				}
			},
			{type:_CELLSPACER_,width:"5px"},
			{type:_DWT_BUTTON_,label:ZaMsg.DeselectAll,width:"120px",
				onActivate:function (ev) {
					var lstElement = this.getParentItem().items[0];
					if(lstElement) {
						lstElement.deselectAll(ev);
					}
				}
			},
			{type:_CELLSPACER_,width:"235px"}
		]
		
	}
		
	this.items = [radioBox1,radioBox2,{type:_CELLSPACER_,width:this.labelWidth},selectChckGrp];
	
	
	Composite_XFormItem.prototype.initializeItems.call(this);
}

/**
*	_SUPER_WIZ_ZIMLET_SELECT_ form item type
**/
SuperWiz_Zimlet_Select_XFormItem = function () {}
XFormItemFactory.createItemType("_SUPER_WIZ_ZIMLET_SELECT_", "super_wiz_zimlet_select", SuperWiz_Zimlet_Select_XFormItem, SuperZimlet_Select_XFormItem);
SuperWiz_Zimlet_Select_XFormItem.prototype.numCols=2;
SuperWiz_Zimlet_Select_XFormItem.prototype.colSizes=["50px","*"];
SuperWiz_Zimlet_Select_XFormItem.prototype.labelWidth = "50px";

/**
*	SUPER_SELECT1 form item type
**/
Super_Select1_XFormItem = function () {}
XFormItemFactory.createItemType("_SUPER_SELECT1_", "super_select1", Super_Select1_XFormItem, Super_XFormItem);
Super_Select1_XFormItem.prototype.labelCssClass = "xform_label_left";
Super_Select1_XFormItem.prototype.labelCssStyle = "width:269px" ; // 6px for padding
Super_Select1_XFormItem.prototype.colSizes=["275px","150px"];
Super_Select1_XFormItem.prototype.nowrap = false;
Super_Select1_XFormItem.prototype.labelWrap = true;
Super_Select1_XFormItem.prototype.trueValue = "TRUE";
Super_Select1_XFormItem.prototype.falseValue = "FALSE";
Super_Select1_XFormItem.prototype.initializeItems = function() {
	var slct = {	type:_OSELECT1_, ref:".",
		onChange:Composite_XFormItem.onFieldChange,
		forceUpdate:true,
		updateElement:function(value) {
			Super_XFormItem.updateCss.call(this,5);
			OSelect1_XFormItem.prototype.updateElement.call(this, value);
		}
	};
	var anchorHlpr = {	
		type:_SUPER_ANCHOR_HELPER_, ref:".",
		visibilityChecks:[Super_XFormItem.checkIfOverWriten],
		visibilityChangeEventSources:[this.getRefPath()],
		onChange:Composite_XFormItem.onFieldChange
	};
	
	var anchorCssStyle = this.getInheritedProperty("anchorCssStyle");
	if(anchorCssStyle) {
		anchorHlpr.cssStyle = anchorCssStyle;
	} else {
		anchorHlpr.cssStyle = "width:150px";
	}	

	var choices = this.getInheritedProperty("choices");	
	
	if(choices)
		slct.choices = choices;	
	
	var editable = this.getInheritedProperty("editable");
	if(editable)
		slct.editable = editable;
		
	var inputSize = this.getInheritedProperty("inputSize");
	if(inputSize)
		slct.inputSize = inputSize;
    var valueWidth = this.getInheritedProperty("valueWidth");
    if(valueWidth)
        slct.width =  valueWidth;
	this.items = [slct,anchorHlpr];
	Composite_XFormItem.prototype.initializeItems.call(this);
	
		

}	


Super_Select1_XFormItem.prototype.useParentTable = false;
Super_Select1_XFormItem.prototype.numCols = 2;

Super_Select1_XFormItem.prototype.items = [];

/**
*	_SUPERWIZ_SELECT1_ form item type
**/
SuperWiz_Select1_XFormItem = function () {}
XFormItemFactory.createItemType("_SUPERWIZ_SELECT1_", "superwiz_select1", SuperWiz_Select1_XFormItem, Super_Select1_XFormItem);
SuperWiz_Select1_XFormItem.prototype.labelCssClass = "xform_label_left ZaWizLabel";
SuperWiz_Select1_XFormItem.prototype.labelCssStyle = "width:194px" ; // for it has 6px padding
SuperWiz_Select1_XFormItem.prototype.colSizes=["250px","*"];
SuperWiz_Select1_XFormItem.prototype.valueWidth = "auto";
SuperWiz_Select1_XFormItem.prototype.nowrap = false;
SuperWiz_Select1_XFormItem.prototype.labelWrap = true;
SuperWiz_Select1_XFormItem.prototype.visibilityChecks = [ZaItem.hasWritePermission];
SuperWiz_Select1_XFormItem.prototype.enableDisableChecks = [ZaItem.hasWritePermission];

/**
*	SUPER_DWT_COLORPICKER form item type
**/
Super_Dwt_ColorPicker_XFormItem = function () {}
XFormItemFactory.createItemType("_SUPER_DWT_COLORPICKER_", "super_dwt_colorpicker", Super_Dwt_ColorPicker_XFormItem, Super_XFormItem);
Super_Dwt_ColorPicker_XFormItem.prototype.labelCssClass = "xform_label_left";
Super_Dwt_ColorPicker_XFormItem.prototype.labelCssStyle = "width:275px" ;
Super_Dwt_ColorPicker_XFormItem.prototype.colSizes=["275px","150px"];
Super_Dwt_ColorPicker_XFormItem.prototype.nowrap = false;
Super_Dwt_ColorPicker_XFormItem.prototype.labelWrap = true;
Super_Dwt_ColorPicker_XFormItem.prototype.useParentTable = false;
Super_Dwt_ColorPicker_XFormItem.prototype.numCols = 2;
Super_Dwt_ColorPicker_XFormItem.prototype.initializeItems = function() {
    var buttonImage = this.getInheritedProperty("buttonImage") ;
    var width = this.getInheritedProperty("width") ;
    this.items = [
		{	type:_DWT_COLORPICKER_, ref:".", 
			onChange:Composite_XFormItem.onFieldChange,
			forceUpdate:true,
            buttonImage: buttonImage, width: width,
            //this method is requied to show the "reset to cos" upon the element update
			elementChanged:function(elementValue, instanceValue, event) {
				this.getForm().itemChanged(this, elementValue, event);
			},
			updateElement:function(value) {
				Super_XFormItem.updateCss.call(this,5);
				Dwt_ColorPicker_XFormItem.prototype.updateWidget.call(this, value);
			}
		},
		{	
			type:_SUPER_ANCHOR_HELPER_, ref:".",
			visibilityChecks:[Super_XFormItem.checkIfOverWriten],
			visibilityChangeEventSources:[this.getRefPath()],
			onChange:Composite_XFormItem.onFieldChange
		}
	];
	
	var anchorCssStyle = this.getInheritedProperty("anchorCssStyle");
	if(anchorCssStyle) {
		this.getItems()[1].cssStyle = anchorCssStyle;
	} else {
		this.getItems()[1].cssStyle = "width:150px";
	}	
	Composite_XFormItem.prototype.initializeItems.call(this);
}	



/**
*	_SUPERWIZ_DWT_COLORPICKER_ form item type
**/

SuperWiz_Dwt_ColorPicker_XFormItem = function () {}
XFormItemFactory.createItemType("_SUPERWIZ_DWT_COLORPICKER_", "superwiz_dwt_colorpicker", SuperWiz_Dwt_ColorPicker_XFormItem, Super_Dwt_ColorPicker_XFormItem);
SuperWiz_Dwt_ColorPicker_XFormItem.prototype.labelCssClass = "xform_label_left ZaWizLabel";
SuperWiz_Dwt_ColorPicker_XFormItem.prototype.labelCssStyle = "width:200px" ;
SuperWiz_Dwt_ColorPicker_XFormItem.prototype.colSizes=["250px","150px"];
SuperWiz_Dwt_ColorPicker_XFormItem.prototype.nowrap = false;
SuperWiz_Dwt_ColorPicker_XFormItem.prototype.labelWrap = true;


/**
* _SUPER_LIFETIME_ XForm item type
**/

Super_Lifetime_XFormItem = function() {}
XFormItemFactory.createItemType("_SUPER_LIFETIME_", "super_lifetime", Super_Lifetime_XFormItem, Super_XFormItem);
Super_Lifetime_XFormItem.prototype.nowrap = false;
Super_Lifetime_XFormItem.prototype.labelWrap = true;
Super_Lifetime_XFormItem.prototype.numCols = 4;
Super_Lifetime_XFormItem.prototype.colSpan = 4;
Super_Lifetime_XFormItem.prototype.colSizes =["275px","80px","120px","*"];
Super_Lifetime_XFormItem.prototype.useParenttable = false;
Super_Lifetime_XFormItem.prototype.visibilityChecks = [ZaItem.hasReadPermission];
Super_Lifetime_XFormItem.prototype.enableDisableChecks = [ZaItem.hasWritePermission];
if(appNewUI){
Super_Lifetime_XFormItem.prototype.labelCssStyle = "border-right: 1px solid black;";
Super_Lifetime_XFormItem.prototype.tableCssClass = "grid_composite_table";
}
Super_Lifetime_XFormItem.prototype.initializeItems = function() {
	var txtBoxLabel = this.getInheritedProperty("txtBoxLabel");
    var labelCssClass = this.getInheritedProperty("labelCssClass");
	var toolTip = this.getInheritedProperty("toolTipContent");
	
	var txtField =	{
		type:_TEXTFIELD_, ref:".", 
		label:txtBoxLabel,	
		toolTipContent: toolTip,
		//nowrap:false,
		//labelWrap:true,
		nowrap:this.getInheritedProperty("nowrap"),
		labelWrap:this.getInheritedProperty("labelWrap"),		
		labelCssStyle:this.getLabelCssStyle(),
        labelCssClass: labelCssClass,
		labelLocation:(txtBoxLabel ? _LEFT_ : _NONE_),
		cssClass:"admin_xform_number_input", 
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
			this.getForm().itemChanged(this, val, event);
		},onChange:Composite_XFormItem.onFieldChange,
		updateElement:function(value) {
			Super_XFormItem.updateCss.call(this,5);
			Textfield_XFormItem.prototype.updateElement.call(this, value);
		}
	};
	
	var selectField = 	{
		type:_OSELECT1_, ref:".",  
		choices:ZaModel.getTimeChoices(),
		getDisplayValue:function (itemVal){
			var val = "d";
			if(itemVal != null && itemVal.length >0) {
				if(itemVal.length > 1) {
					var lastChar = (itemVal.toLowerCase()).charAt(itemVal.length-1);
					val = (lastChar == "d" || lastChar == "h" || lastChar== "m" || lastChar == "s") ? lastChar : "s";
				} else {
					if(itemVal == "0") {
						val = "d";
					} else {
						val = "s"
					}
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
		label:null,
		labelLocation:_NONE_,
		forceUpdate:true		
	};
	var anchorCssStyle = this.getInheritedProperty("anchorCssStyle");
	
	var anchorHlpr = {	
		type:_SUPER_ANCHOR_HELPER_, ref:".",
		visibilityChecks:[Super_XFormItem.checkIfOverWriten],
		visibilityChangeEventSources:[this.getRefPath()],
		onChange:Composite_XFormItem.onFieldChange,
		cssStyle: (anchorCssStyle ? anchorCssStyle : "width:150px")
	};
	this.items = [txtField,selectField,anchorHlpr];
	Composite_XFormItem.prototype.initializeItems.call(this);	
}

Super_Lifetime_XFormItem.prototype.items = [ ];

/**
* _SUPERWIZ_LIFETIME_ XForm item type
**/
SuperWiz_Lifetime_XFormItem = function() {}
XFormItemFactory.createItemType("_SUPERWIZ_LIFETIME_", "superwiz_lifetime", SuperWiz_Lifetime_XFormItem, Super_Lifetime_XFormItem);
SuperWiz_Lifetime_XFormItem.prototype.colSizes =["200px","80px","120px","150px"];
SuperWiz_Lifetime_XFormItem.prototype.visibilityChecks = [ZaItem.hasWritePermission];
SuperWiz_Lifetime_XFormItem.prototype.enableDisableChecks = [ZaItem.hasWritePermission];
if(appNewUI){
SuperWiz_Lifetime_XFormItem.prototype.labelCssStyle = "";
}
/**
* _SUPER_LIFETIME1_ XForm item type for displaying trash message retention and spam message retention settings
**/
Super_Lifetime1_XFormItem = function() {}
XFormItemFactory.createItemType("_SUPER_LIFETIME1_", "super_lifetime1", Super_Lifetime1_XFormItem, Super_XFormItem);
Super_Lifetime1_XFormItem.prototype.nowrap = false;
Super_Lifetime1_XFormItem.prototype.labelWrap = true;
Super_Lifetime1_XFormItem.prototype.numCols = 4;
Super_Lifetime1_XFormItem.prototype.colSpan = 4;
Super_Lifetime1_XFormItem.prototype.colSizes =["275px","80px","120px","150px"];
Super_Lifetime1_XFormItem.prototype.useParenttable = false;
Super_Lifetime1_XFormItem.prototype.visibilityChecks = [ZaItem.hasReadPermission];
Super_Lifetime1_XFormItem.prototype.enableDisableChecks = [ZaItem.hasWritePermission];

SuperWiz_Lifetime1_XFormItem = function() {}
SuperWiz_Lifetime1_XFormItem.prototype.visibilityChecks = [ZaItem.hasReadPermission];
SuperWiz_Lifetime1_XFormItem.prototype.enableDisableChecks = [ZaItem.hasWritePermission];

XFormItemFactory.createItemType("_SUPERWIZ_LIFETIME1_", "superwiz_lifetime1", SuperWiz_Lifetime1_XFormItem, Super_Lifetime1_XFormItem);
SuperWiz_Lifetime1_XFormItem.prototype.colSizes =["200px","80px","120px","150px"];

Super_Lifetime1_XFormItem.prototype.initializeItems = function() {
	var txtBoxLabel = this.getInheritedProperty("txtBoxLabel");
    var labelCssStyle = this.getInheritedProperty("labelCssStyle");
	var toolTip = this.getInheritedProperty("toolTipContent");
	
	var txtField =	{
		type:_TEXTFIELD_, ref:".", 
		label:txtBoxLabel,	
		toolTipContent: toolTip,
		nowrap:this.getInheritedProperty("nowrap"),
		labelWrap:this.getInheritedProperty("labelWrap"),		
		labelCssStyle:labelCssStyle || this.getLabelCssStyle(),
		labelLocation:(txtBoxLabel ? _LEFT_ : _NONE_),
		cssClass:"admin_xform_number_input", 
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
	};
	
	var selectField = 	{
		type:_OSELECT1_, ref:".", 
		choices:ZaModel.getTimeChoices1(),
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
		label:null,
		labelLocation:_NONE_,
		forceUpdate:true		
	};
	var anchorCssStyle = this.getInheritedProperty("anchorCssStyle");
	
	var anchorHlpr = {	
		type:_SUPER_ANCHOR_HELPER_, ref:".",
		visibilityChecks:[Super_XFormItem.checkIfOverWriten],
		visibilityChangeEventSources:[this.getRefPath()],
		onChange:Composite_XFormItem.onFieldChange,
		cssStyle: (anchorCssStyle ? anchorCssStyle : "width:150px")
	};
	this.items = [txtField,selectField,anchorHlpr];
	Composite_XFormItem.prototype.initializeItems.call(this);	
}

Super_Lifetime1_XFormItem.prototype.items = [ ];

/**
* _SUPER_LIFETIME2_ XForm item type allows time interval to be expressed only in days
**/

Super_Lifetime2_XFormItem = function() {}
XFormItemFactory.createItemType("_SUPER_LIFETIME2_", "super_lifetime2", Super_Lifetime2_XFormItem, Super_Lifetime1_XFormItem);
Super_Lifetime2_XFormItem.prototype.nowrap = false;
Super_Lifetime2_XFormItem.prototype.labelWrap = true;
Super_Lifetime2_XFormItem.prototype.numCols = 4;
Super_Lifetime2_XFormItem.prototype.colSpan = 4;
Super_Lifetime2_XFormItem.prototype.colSizes =["275px","80px","120px","150px"];
Super_Lifetime2_XFormItem.prototype.useParenttable = false;
Super_Lifetime2_XFormItem.prototype._stringPart = "d";
Super_Lifetime2_XFormItem.prototype.visibilityChecks = [ZaItem.hasReadPermission];
Super_Lifetime2_XFormItem.prototype.enableDisableChecks = [ZaItem.hasWritePermission];

SuperWiz_Lifetime2_XFormItem = function() {}
XFormItemFactory.createItemType("_SUPERWIZ_LIFETIME2_", "superwiz_lifetime2", SuperWiz_Lifetime2_XFormItem, Super_Lifetime2_XFormItem);
SuperWiz_Lifetime2_XFormItem.prototype.colSizes =["200px","80px","120px","150px"];
SuperWiz_Lifetime2_XFormItem.prototype.visibilityChecks = [ZaItem.hasWritePermission];
SuperWiz_Lifetime2_XFormItem.prototype.enableDisableChecks = [ZaItem.hasWritePermission];

Super_Lifetime2_XFormItem.prototype.initializeItems = function() {
	var txtBoxLabel = this.getInheritedProperty("txtBoxLabel");
    var labelCssClass = this.getInheritedProperty("labelCssClass");
	var toolTip = this.getInheritedProperty("toolTipContent");
	
	var txtField =	{
		type:_TEXTFIELD_, ref:".", 
		label:txtBoxLabel,	
		toolTipContent: toolTip,
		nowrap:this.getInheritedProperty("nowrap"),
		labelWrap:this.getInheritedProperty("labelWrap"),		
		labelCssStyle:this.getLabelCssStyle(),
        labelCssClass:labelCssClass,
		labelLocation:(txtBoxLabel ? _LEFT_ : _NONE_),
		cssClass:"admin_xform_number_input", 
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
			this.getParentItem()._stringPart="d";
			return val;	
		},
		elementChanged:function(numericPart, instanceValue, event) {
			var val = numericPart + "d";
			this.getForm().itemChanged(this, val, event);
		},onChange:Composite_XFormItem.onFieldChange,
		updateElement:function(value) {
			Super_XFormItem.updateCss.call(this,5);
			Textfield_XFormItem.prototype.updateElement.call(this, value);
		}
	};
	
	var selectField = 	{
		type:_OUTPUT_,
		ref:null,
		label:null,
		labelLocation:_NONE_,
		value:"d",
		getDisplayValue:function (itemVal){ return AjxMsg.days; }	
	};
	var anchorCssStyle = this.getInheritedProperty("anchorCssStyle");
	
	var anchorHlpr = {	
		type:_SUPER_ANCHOR_HELPER_, ref:".",
		visibilityChecks:[Super_XFormItem.checkIfOverWriten],
		visibilityChangeEventSources:[this.getRefPath()],
		onChange:Composite_XFormItem.onFieldChange,
		cssStyle: (anchorCssStyle ? anchorCssStyle : "width:150px")
	};
	this.items = [txtField,selectField,anchorHlpr];
	Composite_XFormItem.prototype.initializeItems.call(this);	
}

Super_Lifetime2_XFormItem.prototype.items = [ ];

/**
* _SUPER_LIFETIME_MINUTES_ XForm item type allows time interval to be expressed only in minutes
**/
Super_LifetimeMinutes_XFormItem = function() {}
XFormItemFactory.createItemType("_SUPER_LIFETIME_MINUTES_", "super_lifetime_minutes", Super_LifetimeMinutes_XFormItem, Super_Lifetime1_XFormItem);
Super_LifetimeMinutes_XFormItem.prototype.nowrap = false;
Super_LifetimeMinutes_XFormItem.prototype.labelWrap = true;
Super_LifetimeMinutes_XFormItem.prototype.numCols = 4;
Super_LifetimeMinutes_XFormItem.prototype.colSpan = 4;
Super_LifetimeMinutes_XFormItem.prototype.colSizes =["275px","80px","120px","150px"];
Super_LifetimeMinutes_XFormItem.prototype.useParenttable = false;
Super_LifetimeMinutes_XFormItem.prototype._stringPart = "d";
Super_LifetimeMinutes_XFormItem.prototype.visibilityChecks = [ZaItem.hasReadPermission];
Super_LifetimeMinutes_XFormItem.prototype.enableDisableChecks = [ZaItem.hasWritePermission];

/**
* _SUPERWIZ_LIFETIME_MINUTES_ customization or _SUPER_LIFETIME_MINUTES_ for wizard dialogs
**/
SuperWiz_LifetimeMinutes_XFormItem = function() {}
XFormItemFactory.createItemType("_SUPERWIZ_LIFETIME_MINUTES_", "superwiz_lifetime2", SuperWiz_LifetimeMinutes_XFormItem, Super_LifetimeMinutes_XFormItem);
SuperWiz_LifetimeMinutes_XFormItem.prototype.colSizes =["200px","80px","120px","150px"];
SuperWiz_LifetimeMinutes_XFormItem.prototype.visibilityChecks = [ZaItem.hasWritePermission];
SuperWiz_LifetimeMinutes_XFormItem.prototype.enableDisableChecks = [ZaItem.hasWritePermission];

Super_LifetimeMinutes_XFormItem.prototype.initializeItems = function() {
	var txtBoxLabel = this.getInheritedProperty("txtBoxLabel");
    var labelCssClass = this.getInheritedProperty("labelCssClass");
	var toolTip = this.getInheritedProperty("toolTipContent");
	
	var txtField =	{
		type:_TEXTFIELD_, ref:".", 
		label:txtBoxLabel,	
		toolTipContent: toolTip,
		nowrap:this.getInheritedProperty("nowrap"),
		labelWrap:this.getInheritedProperty("labelWrap"),		
		labelCssStyle:this.getLabelCssStyle(),
        labelCssClass:labelCssClass,
		labelLocation:(txtBoxLabel ? _LEFT_ : _NONE_),
		cssClass:"admin_xform_number_input", 
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
			this.getParentItem()._stringPart="m";
			return val;	
		},
		elementChanged:function(numericPart, instanceValue, event) {
			var val = numericPart + "m";
			this.getForm().itemChanged(this, val, event);
		},onChange:Composite_XFormItem.onFieldChange,
		updateElement:function(value) {
			Super_XFormItem.updateCss.call(this,5);
			Textfield_XFormItem.prototype.updateElement.call(this, value);
		}
	};
	
	var selectField = 	{
		type:_OUTPUT_,
		ref:null,
		label:null,
		labelLocation:_NONE_,
		value:"m",
		getDisplayValue:function (itemVal){ return AjxMsg.minutes; }	
	};
	var anchorCssStyle = this.getInheritedProperty("anchorCssStyle");
	
	var anchorHlpr = {	
		type:_SUPER_ANCHOR_HELPER_, ref:".",
		visibilityChecks:[Super_XFormItem.checkIfOverWriten],
		visibilityChangeEventSources:[this.getRefPath()],
		onChange:Composite_XFormItem.onFieldChange,
		cssStyle: (anchorCssStyle ? anchorCssStyle : "width:150px")
	};
	this.items = [txtField,selectField,anchorHlpr];
	Composite_XFormItem.prototype.initializeItems.call(this);	
}

Super_LifetimeMinutes_XFormItem.prototype.items = [ ];

/**
 * Groupers
 */
TopGrouper_XFormItem.prototype.colSizes = ["275px","275px"];
TopGrouper_XFormItem.prototype.numCols = 2;

ZACheckbox_XFormItem = function() {}
XFormItemFactory.createItemType("_ZA_CHECKBOX_", "za_checkbox", ZACheckbox_XFormItem, Checkbox_XFormItem);
//ZACheckbox_XFormItem.prototype.labelLocation = _RIGHT_;
//ZACheckbox_XFormItem.prototype.align = _RIGHT_;

ZATopGrouper_XFormItem = function() {}
XFormItemFactory.createItemType("_ZA_TOP_GROUPER_", "za_top_grouper", ZATopGrouper_XFormItem, TopGrouper_XFormItem);
ZATopGrouper_XFormItem.prototype.numCols = 2;
ZATopGrouper_XFormItem.prototype.colSizes = ["275px","auto"];
ZATopGrouper_XFormItem.isGroupVisible = function(attrsArray, rightsArray,entry) {
	if(ZaZimbraAdmin.currentAdminAccount.attrs[ZaAccount.A_zimbraIsAdminAccount] == 'TRUE')
		return true;

	if(!entry)
		entry = this.getInstance();
	
	if(!entry)
		return false;

	if (!entry.getAttrs)
		return false;
			
	if(!attrsArray && !rightsArray)
		return true;
		
	if(attrsArray) {
		var cntAttrs = attrsArray.length;
		for(var i=0; i< cntAttrs; i++) {
			if(ZaItem.hasReadPermission(attrsArray[i],entry)) {
				return true;
			}
		}
	} 
	
	if(rightsArray) {
		var cntRights = rightsArray.length;
		for(var i=0; i< cntRights; i++) {
			if(ZaItem.hasRight(rightsArray[i],entry)) {
				return true;
			}
		}
	}
	
	return false; 
}
ZAPlainGrouper_XFormItem = function() {}
XFormItemFactory.createItemType("_ZA_PLAIN_GROUPER_", "za_plain_grouper", ZAPlainGrouper_XFormItem, Group_XFormItem);
if (!appNewUI) {
ZAPlainGrouper_XFormItem.prototype.numCols = 2;
ZAPlainGrouper_XFormItem.prototype.colSizes = ["275px","auto"];
ZAPlainGrouper_XFormItem.prototype.cssClass = "PlainGrouperBorder";
ZAPlainGrouper_XFormItem.isGroupVisible = ZATopGrouper_XFormItem.isGroupVisible;
} else {
ZAPlainGrouper_XFormItem.prototype.colSizes = "100%";
ZAPlainGrouper_XFormItem.prototype.numCols = 1;
ZAPlainGrouper_XFormItem.prototype.width = "100%";
ZAPlainGrouper_XFormItem.prototype.gridLabelCss = "gridGroupBodyLabel";
ZAPlainGrouper_XFormItem.prototype.initializeItems = function () {
    var gridLabelCss = this.getInheritedProperty("gridLabelCss");
    var oldItems = this.getItems();
    var subitems;
    if(oldItems.length == 1 && oldItems[0].type == "group")  {
        oldItems[0].border = 1;
        if(oldItems[0].colSizes.length > 1)
           oldItems[0].colSizes[oldItems[0].colSizes.length -1] = "100%";
        //oldItems[0].colSizes = ["275px","100%"];
        subitems = oldItems[0].items;
    } else  subitems = oldItems;
    for(var i = 0; i < subitems.length; i++) {
        if(subitems[i].label || subitems[i].txtBoxLabel)
            //subitems[i].labelCssStyle = "text-align:left;background-color:#BBB;";
            subitems[i].labelCssClass = gridLabelCss;
    }
    Group_XFormItem.prototype.initializeItems.call(this);
}

}

ZAWizTopGrouper_XFormItem = function() {}
if (appNewUI) {
XFormItemFactory.createItemType("_ZAWIZ_TOP_GROUPER_", "zawiz_top_grouper", ZAWizTopGrouper_XFormItem, BaseTopGrouper_XFormItem);
} else
XFormItemFactory.createItemType("_ZAWIZ_TOP_GROUPER_", "zawiz_top_grouper", ZAWizTopGrouper_XFormItem, TopGrouper_XFormItem);
ZAWizTopGrouper_XFormItem.prototype.numCols = 2;
ZAWizTopGrouper_XFormItem.prototype.colSizes = ["200px","auto"];
ZAWizTopGrouper_XFormItem.isGroupVisible = function(entry, attrsArray, rightsArray) {
	if(!entry)
		return true;
		
	if(!attrsArray && !rightsArray)
		return true;
		
	if(attrsArray) {
		var cntAttrs = attrsArray.length;
		for(var i=0; i< cntAttrs; i++) {
			if(ZaItem.hasWritePermission(attrsArray[i],entry)) {
				return true;
			}
		}
	} 
	
	if(rightsArray) {
		var cntRights = rightsArray.length;
		for(var i=0; i< cntRights; i++) {
			if(ZaItem.hasRight(rightsArray[i],entry)) {
				return true;
			}
		}
	}
	
	return false; 
}

ZAGroup_XFormItem = function() {}
XFormItemFactory.createItemType("_ZAGROUP_", "zagroup", ZAGroup_XFormItem, Group_XFormItem);
ZAGroup_XFormItem.prototype.numCols = 2;
ZAGroup_XFormItem.prototype.colSizes = ["275px","275px"];
if (appNewUI) {
ZAGroup_XFormItem.prototype.border = 1;
ZAGroup_XFormItem.prototype.width = "100%";

}
ZAGroup_XFormItem.prototype.cssStyle = "margin-top:20px;margin-bottom:0px;padding-bottom:0px;";
ZAGroup_XFormItem.isGroupVisible = function(entry, attrsArray, rightsArray) {
	if(!entry)
		entry = this.getInstance();

	if(!entry)
		return true;
		
	if(!attrsArray && !rightsArray)
		return true;
		
	if(attrsArray) {
		var cntAttrs = attrsArray.length;
		for(var i=0; i< cntAttrs; i++) {
			if(ZaItem.hasReadPermission(attrsArray[i],entry) || ZaItem.hasWritePermission(attrsArray[i],entry)) {
				return true;
			}
		}
	} 
	
	if(rightsArray) {
		var cntRights = rightsArray.length;
		for(var i=0; i< cntRights; i++) {
			if(ZaItem.hasRight(rightsArray[i],entry)) {
				return true;
			}
		}
	}
	
	return false; 
}

ZAGroup_XFormItem.prototype.initializeItems = function () {
    if(appNewUI) {
        var gridLabelCss = this.getInheritedProperty("gridLabelCss") || "gridGroupBodyLabel";
        var oldItems = this.getItems();
        if(oldItems) {
            for(var i = 0; i < oldItems.length; i++) {
                if(oldItems[i].type == "radio")
                    continue;  // don't deal with _RADIO_
                if(oldItems[i].label || oldItems[i].txtBoxLabel)
                    //oldItems[i].labelCssStyle = "text-align:left; background-color:#BBB;";
                    oldItems[i].labelCssClass = gridLabelCss;
            }
        }
    }

    Group_XFormItem.prototype.initializeItems.call(this);
}

ZAWizGroup_XFormItem = function() {}
XFormItemFactory.createItemType("_ZAWIZGROUP_", "zawizgroup", ZAWizGroup_XFormItem, Group_XFormItem);
ZAWizGroup_XFormItem.prototype.numCols = 2;
ZAWizGroup_XFormItem.prototype.colSizes = [(AjxEnv.isIE ? "100px":"200px"),(AjxEnv.isIE ? "450px":"275px" )];// modified by qin@zimbra.com
ZAWizGroup_XFormItem.prototype.cssStyle = "margin-top:20px;margin-bottom:0px;padding-bottom:0px;";

ZARightGrouper_XFormItem = function() {}
XFormItemFactory.createItemType("_ZARIGHT_GROUPER_", "zaright_grouper", ZARightGrouper_XFormItem, Grouper_XFormItem);
ZARightGrouper_XFormItem.prototype.borderCssClass = "RightGrouperBorder";

ZALeftGrouper_XFormItem = function() {}
XFormItemFactory.createItemType("_ZALEFT_GROUPER_", "zaleft_grouper", ZALeftGrouper_XFormItem, Grouper_XFormItem);
ZALeftGrouper_XFormItem.prototype.borderCssClass = "LeftGrouperBorder";

ZACenterGrouper_XFormItem = function() {}
XFormItemFactory.createItemType("_ZACENTER_GROUPER_", "zacenter_grouper", ZACenterGrouper_XFormItem, Grouper_XFormItem);
ZACenterGrouper_XFormItem.prototype.borderCssClass = "CenterGrouperBorder";

ZAAllScreenGrouper_XFormItem = function() {}
XFormItemFactory.createItemType("_ZAALLSCREEN_GROUPER_", "zaallscreen_grouper", ZAAllScreenGrouper_XFormItem, Grouper_XFormItem);
ZAAllScreenGrouper_XFormItem.prototype.borderCssClass = "AllScreenGrouperBorder";

ZASmallCenterGrouper_XFormItem = function() {}
XFormItemFactory.createItemType("_ZASMALL_CENTER_GROUPER_", "zasmall_center_grouper", ZASmallCenterGrouper_XFormItem, Grouper_XFormItem);
ZASmallCenterGrouper_XFormItem.prototype.borderCssClass = "CenterGrouperBorder";
ZASmallCenterGrouper_XFormItem.prototype.labelCssClass = "SmallRadioGrouperLabel";

ZATabCase_XFormItem = function() {
	Case_XFormItem.call(this);
}
XFormItemFactory.createItemType("_ZATABCASE_", "zatabcase",ZATabCase_XFormItem, Case_XFormItem);
ZATabCase_XFormItem.prototype.caseVarRef = ZaModel.currentTab;
ZATabCase_XFormItem.prototype.visibilityChangeEventSources = [ZaModel.currentTab];
ZATabCase_XFormItem.prototype.align = _LEFT_;
ZATabCase_XFormItem.prototype.valign = _TOP_;
if(appNewUI){
   ZATabCase_XFormItem.prototype.paddingStyle = "padding-left:15px;";
   ZATabCase_XFormItem.prototype.width = "98%";
   ZATabCase_XFormItem.prototype.cellpadding = "2";
}
else{
   ZATabCase_XFormItem.prototype.width = "100%";
   ZATabCase_XFormItem.prototype.cellpadding = "0";
}

ZATabCase_XFormItem.prototype.getTabLevel = function () {
	return this.getInheritedProperty("tabLevel") || 1;
}

ZATabCase_XFormItem.prototype.getHeaderLevel = function () {
    return this.getInheritedProperty("headerLevel") || 1;
}

ZATabCase_XFormItem.prototype.getHMargin = function () {
    return this.getInheritedProperty("hMargin") || 0;
}

ZATabCase_XFormItem.prototype.getCustomPaddingStyle = function () {
    return this.getInheritedProperty("paddingStyle");
}

ZATabCase_XFormItem.prototype.getCustomHeight = function () {
	try {
		var form = this.getForm();
        var tabLevel = this.getTabLevel () ;
        var headerLevel = this.getHeaderLevel () ;
		var formParentElement = this.getForm().parent.getHtmlElement();
		var totalHeight = parseInt(formParentElement.style.height);
		if(isNaN(totalHeight)) {
			totalHeight = formParentElement.clientHeight ? formParentElement.clientHeight : formParentElement.offsetHeight;
		}
		var formHeaders = form.getItemsById("xform_header");
		var headerHeight = 0;
		if(formHeaders) {
			var formHeader = formHeaders[0];		
			if(formHeader) {
				if(formHeader.getContainer()) {
					formHeader = formHeader.getContainer();
				}
				else {
					formHeader = formHeader.getElement();
				}

				headerHeight = formHeader.clientHeight ? formHeader.clientHeight : formHeader.offsetHeight;				
			}
		}
		var formTabBars = form.getItemsById("xform_tabbar");
		var tabBarHeight = 0;
		if(formTabBars) {
			var formTabBar = formTabBars[0];		
			if(formTabBar) {
				if(formTabBar.getContainer()) {
                                        formTabBar = formTabBar.getContainer();
                                }
				else {
					formTabBar =  formTabBar.getElement();
				}

				tabBarHeight = formTabBar.clientHeight ? formTabBar.clientHeight : formTabBar.offsetHeight;				
			}
		}
        var totalHeaderHeight = headerHeight * headerLevel ;
        var totalTabBarHeight = tabBarHeight * tabLevel ;
		if(totalHeight<=0 || totalHeight < (totalHeaderHeight + totalTabBarHeight + 2))
			return "100%";
		else
			return totalHeight - totalHeaderHeight - totalTabBarHeight - 2;
	} catch (ex) {
        
	}
	return "100%";  					
};

ZATabCase_XFormItem.prototype.getCustomWidth = function () {
	try {

		var form = this.getForm();
		var formParentElement = this.getForm().parent.getHtmlElement();
		var totalWidth = parseInt(formParentElement.style.width);
		if(isNaN(totalWidth)) {
			totalWidth = formParentElement.clientWidth ? formParentElement.clientWidth : formParentElement.offsetWidth;
		}
		if(totalWidth<=0)
			return "100%";
		else {
            var res = totalWidth - this.getHMargin();
            if(this.cacheInheritedMethod("getCustomPaddingStyle", "$getCustomPaddingStyle")) {
                var paddingStyle = this.cacheInheritedMethod("getCustomPaddingStyle", "$getCustomPaddingStyle").call(this);
                if(paddingStyle&&!AjxEnv.isIE)
                    res = res - 15;
            }
			return res;
		}
	} catch (ex) {
        
	}
	return "100%";  					
};

ZATabCase_XFormItem.prototype.resizeHdlr = 
function() {
	try {
		var element = this.getElement();
		var height = this.cacheInheritedMethod("getCustomHeight", "$getCustomHeight").call(this);
		var width = this.cacheInheritedMethod("getCustomWidth", "$getCustomWidth").call(this);		
		element.style.height = height;
		element.style.width = width;
	} catch (ex) {
		alert(ex);
	}
};

/**
 * _SUPER_TABCASE_
 * 
 * on-demand loading tab data
*/

SuperTabCase_XFormItem = function () {}
XFormItemFactory.createItemType("_SUPER_TABCASE_", "super_tabcase", SuperTabCase_XFormItem, ZATabCase_XFormItem);
SuperTabCase_XFormItem.prototype.loadDataMethods = [];

SuperTabCase_XFormItem.prototype.show = function(isBlock) {
        var loadMethods = this.getInheritedProperty("loadDataMethods");

        if(loadMethods && loadMethods instanceof Array) {
                var cnt = loadMethods.length;
                for(var i = 0; i < cnt; i++) {
                        if(loadMethods[i] == null) continue;
                        if(typeof(loadMethods[i]) == "function") {
                                loadMethods[i].call(this);
                        } else if(loadMethods[i] instanceof Array) {
                                var func = loadMethods[i].shift();
                                if(!func || !func.apply) continue;
                                func.apply(this, loadMethods[i]);
                                loadMethods[i].unshift(func);
                        }
                }
        }
        Case_XFormItem.prototype.show.call(this, isBlock);
}

/**
*	_SUPER_REPEAT_ form item type
**/
SuperRepeat_XFormItem = function () {}
XFormItemFactory.createItemType("_SUPER_REPEAT_", "super_repeat", SuperRepeat_XFormItem, Super_XFormItem);
SuperRepeat_XFormItem.prototype.numCols=2;
SuperRepeat_XFormItem.prototype.colSizes=["275px","275px"];
SuperRepeat_XFormItem.prototype.nowrap = false;
SuperRepeat_XFormItem.prototype.labelWrap = true;
SuperRepeat_XFormItem.prototype.items = [];
SuperRepeat_XFormItem.prototype.labelWidth = "275px";

SuperRepeat_XFormItem.prototype.initializeItems = function() {

	var items = this.getInheritedProperty("repeatItems");
		
	var repeatItem = {
		type:_REPEAT_,
		items:items,
		ref:".",
		label:null, labelLocation:_NONE_,
		repeatInstance:this.getRepeatInstance(),
		showAddButton:this.getShowAddButton(),
		showRemoveButton:this.getShowRemoveButton(),
		addButtonLabel:this.getInheritedProperty("addButtonLabel"),
		removeButtonLabel:this.getInheritedProperty("removeButtonLabel"),
		removeButtonCSSStyle:this.getInheritedProperty("removeButtonCSSStyle"),
		addButtonWidth:this.getInheritedProperty("addButtonWidth"),
		removeButtonWidth:this.getInheritedProperty("removeButtonWidth"),
		showAddOnNextRow:AjxUtil.isEmpty(this.getInheritedProperty("showAddOnNextRow")) ? true : this.getInheritedProperty("showAddOnNextRow"),
		alwaysShowAddButton:false,
		updateElement:function(value) {
			Super_XFormItem.updateCss.call(this,5);
			Repeat_XFormItem.prototype.updateElement.call(this, value);
		},		
		bmolsnr:true
	}
	
	var anchorCssStyle = this.getInheritedProperty("anchorCssStyle");	
	var anchorHlpr = {	
		type:_SUPER_ANCHOR_HELPER_, ref:".",
		visibilityChecks:[Super_XFormItem.checkIfOverWriten],
		visibilityChangeEventSources:[this.getRefPath()],
		onChange:Composite_XFormItem.onFieldChange,
		cssStyle: (anchorCssStyle ? anchorCssStyle : "width:150px")
	};
		
	this.items = [repeatItem,anchorHlpr];
	
	
	Composite_XFormItem.prototype.initializeItems.call(this);
}

/**
*	SUPER_DYNSELECT form item type
**/
Super_DynSelect_XFormItem = function () {}
XFormItemFactory.createItemType("_SUPER_DYNSELECT_", "super_dynselect", Super_DynSelect_XFormItem, Super_XFormItem);
Super_DynSelect_XFormItem.prototype.labelCssClass = "xform_label_left";
Super_DynSelect_XFormItem.prototype.labelCssStyle = "width:275px" ;
Super_DynSelect_XFormItem.prototype.colSizes=["275px","150px"];
Super_DynSelect_XFormItem.prototype.nowrap = false;
Super_DynSelect_XFormItem.prototype.labelWrap = true;
Super_DynSelect_XFormItem.prototype.trueValue = "TRUE";
Super_DynSelect_XFormItem.prototype.falseValue = "FALSE";
Super_DynSelect_XFormItem.prototype.initializeItems = function() {
	
	
	var slct = {	type:_DYNSELECT_, ref:".",
		onChange:Composite_XFormItem.onFieldChange,
		forceUpdate:true,
	 	errorLocation:_PARENT_,
		updateElement:function(value) {
			Super_XFormItem.updateCss.call(this,5);
			DynSelect_XFormItem.prototype.updateElement.call(this, value);
		}
	};
	var anchorHlpr = {	
		type:_SUPER_ANCHOR_HELPER_, ref:".",
		visibilityChecks:[Super_XFormItem.checkIfOverWriten],
		visibilityChangeEventSources:[this.getRefPath()],
		onChange:Composite_XFormItem.onFieldChange
	};
	
	var anchorCssStyle = this.getInheritedProperty("anchorCssStyle");
	if(anchorCssStyle) {
		anchorHlpr.cssStyle = anchorCssStyle;
	} else {
		anchorHlpr.cssStyle = "width:150px";
	}	

	var choices = this.getInheritedProperty("choices");	
	
	if(choices)
		slct.choices = choices;
		
	var toolTipContent = this.getInheritedProperty("slctToolTipContent");
	if(toolTipContent) {
	 slct.toolTipContent = toolTipContent;
	}
	var dataFetcherClass = this.getInheritedProperty("dataFetcherClass");
	if(dataFetcherClass) {
	 slct.dataFetcherClass = dataFetcherClass;
	}

	var dataFetcherMethod = this.getInheritedProperty("dataFetcherMethod");
	if(dataFetcherMethod) {
	 slct.dataFetcherMethod = dataFetcherMethod;
	}
	
	this.items = [slct,anchorHlpr];
	Composite_XFormItem.prototype.initializeItems.call(this);
}	


Super_DynSelect_XFormItem.prototype.useParentTable = false;
Super_DynSelect_XFormItem.prototype.numCols = 2;

Super_DynSelect_XFormItem.prototype.items = [];

/**
*	_SUPERWIZ_DYNSELECT_ form item type
**/
SuperWiz_DynSelect_XFormItem = function () {}
XFormItemFactory.createItemType("_SUPERWIZ_DYNSELECT_", "superwiz_dynselect", SuperWiz_DynSelect_XFormItem, Super_DynSelect_XFormItem);
SuperWiz_DynSelect_XFormItem.prototype.labelCssClass = "xform_label_left ZaWizLabel";
SuperWiz_DynSelect_XFormItem.prototype.labelCssStyle = "width:200px" ;
SuperWiz_DynSelect_XFormItem.prototype.colSizes=["250px","150px"];
SuperWiz_DynSelect_XFormItem.prototype.nowrap = false;
SuperWiz_DynSelect_XFormItem.prototype.labelWrap = true;
SuperWiz_DynSelect_XFormItem.prototype.visibilityChecks = [ZaItem.hasWritePermission];
SuperWiz_DynSelect_XFormItem.prototype.enableDisableChecks = [ZaItem.hasWritePermission];
