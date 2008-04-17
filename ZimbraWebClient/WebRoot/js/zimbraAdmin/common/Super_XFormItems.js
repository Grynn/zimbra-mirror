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
	if(!ins || !ins.cos)
		return null;
	var _ref = this.ref.replace("/", ".");
	return eval("ins.cos." + _ref);
}
Cos_String_XModelItem.prototype.getLocalValue = function(ins) {
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
Cos_List_XModelItem.prototype.listItem = List_XModelItem.prototype.listItem;
Cos_List_XModelItem.prototype.getOutputType  = List_XModelItem.prototype.getOutputType;
Cos_List_XModelItem.prototype.getItemDelimiter = List_XModelItem.prototype.getItemDelimiter;
Cos_List_XModelItem.prototype.getListItem  = List_XModelItem.prototype.getListItem;
Cos_List_XModelItem.prototype.initializeItems = List_XModelItem.prototype.initializeItems;
Cos_List_XModelItem.prototype.validateType = List_XModelItem.prototype.validateType;


Cos_List_XModelItem.prototype.getSuperValue = function(ins) {
	if(!ins || !ins.cos)
		return null;
	var _ref = this.ref.replace("/", ".");
	var lst = eval("ins.cos." + _ref);
	var retval = [];
	if(lst) {
		var cnt = lst.length
		for(var i=0;i<cnt;i++) {
			retval.push(lst[i]);
		}
	}
	return retval;
}
//	methods

List_XModelItem.prototype.initializeItems = function () {
	var listItem = this.listItem;
	listItem.ref = listItem.id = "#";	
	this.listItem = XModelItemFactory.createItem(listItem, this, this.getModel());
	this.listItem.initializeItems();
}


List_XModelItem.prototype.validateType = function (value) {
	return value;
//XXX REWORK THIS TO USE THE listItem MODEL ITEM FOR EACH SUB-ITEM
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
	if((eval("ins.cos." + _ref) != null) && (eval("ins.cos." + _ref) != 0) && (eval("ins.cos." + _ref) != "")) {
		value = (eval("ins.cos." + _ref) / 1048576);
		if(value != Math.round(value)) {
			value = Number(value).toFixed(2);
	  	}
	} 	
//	var value = (eval("ins.cos." + _ref) != null) ? Number(eval("ins.cos." + _ref) / 1048576).toFixed(0) : 0;
	return value;
}
Cos_MailQuota_XModelItem.prototype.getLocalValue = function(ins) {
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
XModelItemFactory.createItemType("_COS_HOSTNAME_OR_IP_", "cos_subnet", Cos_Subnet_XModelItem, Cos_String_XModelItem);
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
Super_XFormItem.checkIfOverWriten = function() {
	if(this.getModelItem().getLocalValue(this.getInstance())==null)
		return false;
	/*else if ( (this.getModelItem().getLocalValue(this.getInstance()) instanceof Array) && 
	(this.getModelItem().getLocalValue(this.getInstance()).length==0) )	
		return false;*/
	else if ( (this.getModelItem().getLocalValue(this.getInstance()) instanceof AjxVector) && 
	(this.getModelItem().getLocalValue(this.getInstance()).size==0) )	
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

// implement the following to actually construct the instance of your widget
Super_AnchorHelper_XFormItem.prototype.constructWidget = function () {
	var widget = this.widget = new DwtButton(this.getForm(), this.getCssClass());
	var height = this.getHeight();
	var width = this.getWidth();
	if(!width) width = "120px";
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

/*XFormItemFactory.createItemType("_SUPER_ANCHOR_HELPER_", "super_anchor_helper", Super_AnchorHelper_XFormItem, Anchor_XFormItem);
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
}*/

Super_AnchorHelper_XFormItem.prototype.resetToSuperValue = function(event) {
	this.getForm().itemChanged(this.getParentItem(), null, event);
}

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

SuperWiz_Textfield_XFormItem = function () {}
XFormItemFactory.createItemType("_SUPERWIZ_TEXTFIELD_", "superwiz_textfield", SuperWiz_Textfield_XFormItem, Super_Textfield_XFormItem);
SuperWiz_Textfield_XFormItem.prototype.colSizes=["200px", "250px","150px"];

Super_Textfield_XFormItem.prototype.initializeItems = function() {
	var txtBoxLabel = this.getInheritedProperty("txtBoxLabel");
	var textFieldCssClass = this.getInheritedProperty("textFieldCssClass");
	var textFieldCssStyle = this.getInheritedProperty("textFieldCssStyle");
	var textFieldWidth = this.getInheritedProperty("textFieldWidth");
	var toolTip = this.getInheritedProperty("toolTipContent");

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
		labelLocation:(txtBoxLabel ? _LEFT_ : _NONE_),
		cssClass:textFieldCssClass,
		cssStyle:textFieldCssStyle,
		width:textFieldWidth,
		forceUpdate:true,
		relevantBehavior:_PARENT_,
		nowrap:this.getInheritedProperty("nowrap"),
		labelWrap:this.getInheritedProperty("labelWrap")		
	};
	
	
	
	var anchorCssStyle = this.getInheritedProperty("anchorCssStyle");
	
	var anchorHlpr = {	
		type:_SUPER_ANCHOR_HELPER_, ref:".",
		relevant:"Super_XFormItem.checkIfOverWriten.call(item)",
		relevantBehavior:_BLOCK_HIDE_,
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

SuperWiz_Textarea_XFormItem = function () {}
XFormItemFactory.createItemType("_SUPERWIZ_TEXTAREA_", "superwiz_textarea", SuperWiz_Textarea_XFormItem, Super_Textarea_XFormItem);
SuperWiz_Textarea_XFormItem.prototype.colSizes=["200px", "250px","150px"];

Super_Textarea_XFormItem.prototype.initializeItems = function() {
	var txtBoxLabel = this.getInheritedProperty("txtBoxLabel");
	var textAreaCssClass = this.getInheritedProperty("textAreaCssClass");
	var textAreaCssStyle = this.getInheritedProperty("textAreaCssStyle");
	var textAreaWidth = this.getInheritedProperty("textAreaWidth");
	var toolTip = this.getInheritedProperty("toolTipContent");
	var labelCssStyle = this.getInheritedProperty("labelCssStyle");
	
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
		cssClass:textAreaCssClass,
		cssStyle:textAreaCssStyle,
		width:textAreaWidth,
		forceUpdate:true,
		relevantBehavior:_PARENT_,
		nowrap:this.getInheritedProperty("nowrap"),
		labelWrap:this.getInheritedProperty("labelWrap")		
	};
	
	var anchorCssStyle = this.getInheritedProperty("anchorCssStyle");
	
	var anchorHlpr = {	
		type:_SUPER_ANCHOR_HELPER_, ref:".",
		relevant:"Super_XFormItem.checkIfOverWriten.call(item)",
		relevantBehavior:_BLOCK_HIDE_,
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
SuperWiz_Checkbox_XFormItem.prototype.colSizes = ["200px","250px","150px"];

Super_Checkbox_XFormItem.prototype.useParentTable = false;
Super_Checkbox_XFormItem.prototype.numCols = 3;
Super_Checkbox_XFormItem.prototype.colSizes = ["275px","275px","150px"];
Super_Checkbox_XFormItem.prototype.initializeItems = function() {
	var anchorCssStyle = this.getInheritedProperty("anchorCssStyle");
	if(anchorCssStyle) {
		this.getItems()[1].cssStyle = anchorCssStyle;
	} /*else {
		this.getItems()[1].cssStyle = "width:200px";
	}	*/
	
	Composite_XFormItem.prototype.initializeItems.call(this);
	var checkBoxLabel = this.getInheritedProperty("checkBoxLabel");
	//var checkBoxLabel = this.getLabel();
	/*if(!checkBoxLabel)
		checkBoxLabel = this.getInheritedProperty("checkBoxLabel");*/
		
	var checkBoxLabelLocation = this.getInheritedProperty("checkBoxLabelLocation");
	//var checkBoxLabelLocation = this.getLabelLocation();
/*	if(!checkBoxLabelLocation)
		checkBoxLabelLocation = this.getInheritedProperty("checkBoxLabelLocation");*/
		
	if(checkBoxLabel) {
		this.getItems()[0].label = checkBoxLabel;
		this.getItems()[0].labelWrap = this.getInheritedProperty("labelWrap");
		this.numCols = 3;
		this.colSpan=3;
	}
	if(checkBoxLabelLocation) {
		this.getItems()[0].labelLocation = checkBoxLabelLocation;
	}
	var trueValue = this.getInheritedProperty("trueValue");
	var falseValue = this.getInheritedProperty("falseValue");	
	this.getItems()[0].trueValue = trueValue;
	this.getItems()[0].falseValue = falseValue;	
}	

Super_Checkbox_XFormItem.prototype.items = [
	{	type:_CHECKBOX_, ref:".", 
		//trueValue:"TRUE", falseValue:"FALSE", 
		onChange:Composite_XFormItem.onFieldChange,
		updateElement:function(value) {
			Super_XFormItem.updateCss.call(this,5);
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
		cssStyle:"width:150px"
	}
];


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
	var anchorCssStyle = this.getInheritedProperty("anchorCssStyle");
	if(anchorCssStyle) {
		this.getItems()[1].cssStyle = anchorCssStyle;
	} 
	Composite_XFormItem.prototype.initializeItems.call(this);
	var textBoxLabel = this.getInheritedProperty("textBoxLabel");
		
	if(textBoxLabel) {
		this.getItems()[0].label = textBoxLabel;
	}
}	
Super_HostPort_XFormItem.prototype.items = [
	{	type:_HOSTPORT_, ref:".",
		onChange:Composite_XFormItem.onFieldChange,
		onClick: "Super_HostPort_XFormItem.handleClick",
		onMouseout: "Super_HostPort_XFormItem.handleMouseout",
		updateElement:function(value) {
			Super_XFormItem.updateCss.call(this,5);
		}
	},
	{	
		type:_SUPER_ANCHOR_HELPER_, ref:".",
		relevant:"Super_XFormItem.checkIfOverWriten.call(item)",
		relevantBehavior:_BLOCK_HIDE_,
		onChange:Composite_XFormItem.onFieldChange,cssStyle:"width:150px"
	}
];

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
	
	/*if(anchorCssStyle) {
		this.getItems()[0].cssStyle = anchorCssStyle;
	} else {
		this.getItems()[0].cssStyle = "width:200px";
	}	

	var sorted = this.getInheritedProperty("sorted");
	var layoutStyle = this.getInheritedProperty("layoutStyle");	
	var sourceRef = this.getInheritedProperty("sourceRef");	
	var widgetClass = this.getInheritedProperty("widgetClass");			
	
	this.getItems()[2].sorted = sorted;
	this.getItems()[2].layoutStyle = layoutStyle;	
	this.getItems()[2].sourceRef = sourceRef;
	this.getItems()[2].widgetClass = widgetClass;	
	this.getItems()[2].tableWidth = (this.getInheritedProperty("tableWidth") ? this.getInheritedProperty("tableWidth") : null);
	this.getItems()[2].labelWidth = (this.getInheritedProperty("labelWidth") ? this.getInheritedProperty("labelWidth") : null);
	this.getItems()[2].splitButtons = this.getInheritedProperty("splitButtons");
	*/
	var anchorItem = {	
			type:_SUPER_ANCHOR_HELPER_, ref:".",
			relevant:"Super_XFormItem.checkIfOverWriten.call(item)",
			relevantBehavior:_BLOCK_HIDE_,cssSyle:(anchorCssStyle ? anchorCssStyle : "width:150px;"),
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
*	_ZIMLET_SELECT_ form item type
**/
Zimlet_Select_XFormItem = function () {}
XFormItemFactory.createItemType("_ZIMLET_SELECT_", "zimlet_select_", Zimlet_Select_XFormItem, Super_XFormItem);
Zimlet_Select_XFormItem.prototype.numCols=2;
Zimlet_Select_XFormItem.prototype.colSizes=["275px","275px"];
Zimlet_Select_XFormItem.prototype.nowrap = false;
Zimlet_Select_XFormItem.prototype.labelWrap = true;
Zimlet_Select_XFormItem.prototype.items = [];
Zimlet_Select_XFormItem.prototype.labelWidth = "275px";

Zimlet_Select_XFormItem.prototype.initializeItems = function() {
	var selectRef = this.getInheritedProperty("selectRef");
	var choices = this.getInheritedProperty("choices");	
	var selectLabel = this.getInheritedProperty("selectLabel");	
	var selectChck = {
		type:_OSELECT_CHECK_,
		choices:choices,
		colSpan:3,
		ref:selectRef,
		label:selectLabel,
		labelLocation:_TOP_,
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
		
	this.items = [{type:_CELLSPACER_,width:this.labelWidth},selectChckGrp];
	
	
	Composite_XFormItem.prototype.initializeItems.call(this);
}

/**
*	_ZIMLET_SELECT_RADIO_ form item type
**/
Zimlet_SelectRadio_XFormItem = function () {}
XFormItemFactory.createItemType("_ZIMLET_SELECT_RADIO_", "zimlet_select_radio", Zimlet_SelectRadio_XFormItem, Super_XFormItem);
Zimlet_SelectRadio_XFormItem.prototype.numCols=2;
Zimlet_SelectRadio_XFormItem.prototype.colSizes=["275px","275px"];
Zimlet_SelectRadio_XFormItem.prototype.nowrap = false;
Zimlet_SelectRadio_XFormItem.prototype.labelWrap = true;
Zimlet_SelectRadio_XFormItem.prototype.items = [];
Zimlet_SelectRadio_XFormItem.prototype.labelWidth = "275px";

Zimlet_SelectRadio_XFormItem.prototype.initializeItems = function() {
	var selectRef = this.getInheritedProperty("selectRef");
	var radioBoxLabel1 = this.getInheritedProperty("radioBoxLabel1");
	var radioBoxLabel2 = this.getInheritedProperty("radioBoxLabel2");
	var choices = this.getInheritedProperty("choices");	

	var radioBox1 = {type:_RADIO_, groupname:"zimlet_select_check_grp"+selectRef,ref:".",
		label:radioBoxLabel1, labelLocation:_RIGHT_,
		elementChanged:function(elementValue,instanceValue, event) {
			if(elementValue==true) {
				this.getForm().itemChanged(this.getParentItem(), null, event);	
			}
		},
		updateElement:function(value) {
			this.getElement().checked = !value;
		}
		
	};
	
	var radioBox2 = {type:_RADIO_, groupname:"zimlet_select_check_grp"+selectRef,ref:".",
		label:radioBoxLabel2, labelLocation:_RIGHT_ ,
		updateElement:function(value) {
			this.getElement().checked = value;
		},
		elementChanged:function(elementValue,instanceValue, event) {

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
		
	this.items = [radioBox1,radioBox2,{type:_CELLSPACER_,width:this.labelWidth},selectChckGrp];
	
	
	Composite_XFormItem.prototype.initializeItems.call(this);
}



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
*	_SUPER_ZIMLET_SELECT_CHECK_ form item type
**/
SuperZimlet_Select_XFormItem = function () {}
XFormItemFactory.createItemType("_SUPER_ZIMLET_SELECT_CHECK_", "super_zimlet_select_check", SuperZimlet_Select_XFormItem, Super_XFormItem);
SuperZimlet_Select_XFormItem.prototype.numCols=2;
SuperZimlet_Select_XFormItem.prototype.colSizes=["275px","275px"];
SuperZimlet_Select_XFormItem.prototype.nowrap = false;
SuperZimlet_Select_XFormItem.prototype.labelWrap = true;
SuperZimlet_Select_XFormItem.prototype.items = [];
SuperZimlet_Select_XFormItem.prototype.labelWidth = "275px";

SuperZimlet_Select_XFormItem.prototype.initializeItems = function() {
	var selectRef = this.getInheritedProperty("selectRef");
	var limitLabel = this.getInheritedProperty("limitLabel");
	var choices = this.getInheritedProperty("choices");	
	var radioBox1 = {type:_RADIO_, groupname:"zimlet_select_check_grp"+selectRef,ref:".",
		label:ZaMsg.NAD_UseCosSettings, labelLocation:_RIGHT_,
		elementChanged:function(elementValue,instanceValue, event) {
			if(elementValue==true) {
				this.getForm().itemChanged(this.getParentItem(), null, event);	
			}
		},
		updateElement:function() {
			this.getElement().checked = !this.getModelItem().getLocalValue(this.getInstance());
		}
		
	};
	
	var radioBox2 = {type:_RADIO_, groupname:"zimlet_select_check_grp"+selectRef,ref:".",
		label:limitLabel, labelLocation:_RIGHT_ ,
		updateElement:function() {
			this.getElement().checked = this.getModelItem().getLocalValue(this.getInstance());
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
*	_SUPER_ZIMLETWIZ_SELECT_CHECK_ form item type
**/
SuperZimletWiz_Select_XFormItem = function () {}
XFormItemFactory.createItemType("_SUPER_ZIMLETWIZ_SELECT_CHECK_", "super_zimletwiz_select_check", SuperZimletWiz_Select_XFormItem, SuperZimlet_Select_XFormItem);
SuperZimletWiz_Select_XFormItem.prototype.numCols=2;
SuperZimletWiz_Select_XFormItem.prototype.colSizes=["200px","275px"];
SuperZimletWiz_Select_XFormItem.prototype.labelWidth = "200px";

/**
*	SUPER_SELECT1 form item type
**/
Super_Select1_XFormItem = function () {}
XFormItemFactory.createItemType("_SUPER_SELECT1_", "super_select1", Super_Select1_XFormItem, Super_XFormItem);
Super_Select1_XFormItem.prototype.labelCssClass = "xform_label_left";
Super_Select1_XFormItem.prototype.labelCssStyle = "width:275px" ;
Super_Select1_XFormItem.prototype.colSizes=["275px","150px"];
Super_Select1_XFormItem.prototype.nowrap = false;
Super_Select1_XFormItem.prototype.labelWrap = true;
Super_Select1_XFormItem.prototype.trueValue = "TRUE";
Super_Select1_XFormItem.prototype.falseValue = "FALSE";
Super_Select1_XFormItem.prototype.initializeItems = function() {
	var anchorCssStyle = this.getInheritedProperty("anchorCssStyle");
	if(anchorCssStyle) {
		this.getItems()[1].cssStyle = anchorCssStyle;
	} else {
		this.getItems()[1].cssStyle = "width:150px";
	}	

	var trueValue = this.getInheritedProperty("trueValue");
	var falseValue = this.getInheritedProperty("falseValue");	
	var choices = this.getInheritedProperty("choices");	
	
	this.getItems()[0].trueValue = trueValue;
	this.getItems()[0].falseValue = falseValue;	

	Composite_XFormItem.prototype.initializeItems.call(this);
	
	if(choices)
		this.getItems()[0].choices = choices;		
		

}	


Super_Select1_XFormItem.prototype.useParentTable = false;
Super_Select1_XFormItem.prototype.numCols = 2;

Super_Select1_XFormItem.prototype.items = [
	{	type:_OSELECT1_, ref:".",
		onChange:Composite_XFormItem.onFieldChange,
		forceUpdate:true,
		updateElement:function(value) {
			Super_XFormItem.updateCss.call(this,5);
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
*	_SUPERWIZ_SELECT1_ form item type
**/
SuperWiz_Select1_XFormItem = function () {}
XFormItemFactory.createItemType("_SUPERWIZ_SELECT1_", "superwiz_select1", SuperWiz_Select1_XFormItem, Super_Select1_XFormItem);
SuperWiz_Select1_XFormItem.prototype.labelCssClass = "xform_label_left ZaWizLabel";
SuperWiz_Select1_XFormItem.prototype.labelCssStyle = "width:200px" ;
SuperWiz_Select1_XFormItem.prototype.colSizes=["250px","150px"];
SuperWiz_Select1_XFormItem.prototype.nowrap = false;
SuperWiz_Select1_XFormItem.prototype.labelWrap = true;

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
	this.items = [
		{	type:_DWT_COLORPICKER_, ref:".", 
			onChange:Composite_XFormItem.onFieldChange,
			forceUpdate:true,
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
			relevant:"Super_XFormItem.checkIfOverWriten.call(item)",
			relevantBehavior:_BLOCK_HIDE_,
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
Super_Lifetime_XFormItem.prototype.colSizes =["275px","80px","120px","150px"];
Super_Lifetime_XFormItem.prototype.useParenttable = false;

Super_Lifetime_XFormItem.prototype.initializeItems = function() {
	var txtBoxLabel = this.getInheritedProperty("txtBoxLabel");
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
		labelLocation:(txtBoxLabel ? _LEFT_ : _NONE_),
		relevantBehavior:_PARENT_, cssClass:"admin_xform_number_input", 
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
		type:_OSELECT1_, ref:".", relevantBehavior:_PARENT_, 
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
		relevant:"Super_XFormItem.checkIfOverWriten.call(item)",
		relevantBehavior:_BLOCK_HIDE_,
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


SuperWiz_Lifetime1_XFormItem = function() {}
XFormItemFactory.createItemType("_SUPERWIZ_LIFETIME1_", "superwiz_lifetime1", SuperWiz_Lifetime1_XFormItem, Super_Lifetime1_XFormItem);
SuperWiz_Lifetime1_XFormItem.prototype.colSizes =["200px","80px","120px","150px"];

Super_Lifetime1_XFormItem.prototype.initializeItems = function() {
	var txtBoxLabel = this.getInheritedProperty("txtBoxLabel");
	var toolTip = this.getInheritedProperty("toolTipContent");
	
	var txtField =	{
		type:_TEXTFIELD_, ref:".", 
		label:txtBoxLabel,	
		toolTipContent: toolTip,
		nowrap:this.getInheritedProperty("nowrap"),
		labelWrap:this.getInheritedProperty("labelWrap"),		
		labelCssStyle:this.getLabelCssStyle(),
		labelLocation:(txtBoxLabel ? _LEFT_ : _NONE_),
		relevantBehavior:_PARENT_, cssClass:"admin_xform_number_input", 
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
		type:_OSELECT1_, ref:".", relevantBehavior:_PARENT_, 
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
		relevant:"Super_XFormItem.checkIfOverWriten.call(item)",
		relevantBehavior:_BLOCK_HIDE_,
		onChange:Composite_XFormItem.onFieldChange,
		cssStyle: (anchorCssStyle ? anchorCssStyle : "width:150px")
	};
	this.items = [txtField,selectField,anchorHlpr];
	Composite_XFormItem.prototype.initializeItems.call(this);	
}

Super_Lifetime1_XFormItem.prototype.items = [ ];

/**
* _SUPER_LIFETIME1_ XForm item type for displaying Email message retention time
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

SuperWiz_Lifetime2_XFormItem = function() {}
XFormItemFactory.createItemType("_SUPERWIZ_LIFETIME2_", "superwiz_lifetime2", SuperWiz_Lifetime2_XFormItem, Super_Lifetime2_XFormItem);
SuperWiz_Lifetime2_XFormItem.prototype.colSizes =["200px","80px","120px","150px"];

Super_Lifetime2_XFormItem.prototype.initializeItems = function() {
	var txtBoxLabel = this.getInheritedProperty("txtBoxLabel");
	var toolTip = this.getInheritedProperty("toolTipContent");
	
	var txtField =	{
		type:_TEXTFIELD_, ref:".", 
		label:txtBoxLabel,	
		toolTipContent: toolTip,
		nowrap:this.getInheritedProperty("nowrap"),
		labelWrap:this.getInheritedProperty("labelWrap"),		
		labelCssStyle:this.getLabelCssStyle(),
		labelLocation:(txtBoxLabel ? _LEFT_ : _NONE_),
		relevantBehavior:_PARENT_, cssClass:"admin_xform_number_input", 
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
		type:_OUTPUT_, relevantBehavior:_PARENT_, 
		ref:null,
		label:null,
		labelLocation:_NONE_,
		value:"d",
		getDisplayValue:function (itemVal){ return AjxMsg.days; }	
	};
	var anchorCssStyle = this.getInheritedProperty("anchorCssStyle");
	
	var anchorHlpr = {	
		type:_SUPER_ANCHOR_HELPER_, ref:".",
		relevant:"Super_XFormItem.checkIfOverWriten.call(item)",
		relevantBehavior:_BLOCK_HIDE_,
		onChange:Composite_XFormItem.onFieldChange,
		cssStyle: (anchorCssStyle ? anchorCssStyle : "width:150px")
	};
	this.items = [txtField,selectField,anchorHlpr];
	Composite_XFormItem.prototype.initializeItems.call(this);	
}

Super_Lifetime2_XFormItem.prototype.items = [ ];

/**
 * Groupers
 */
TopGrouper_XFormItem.prototype.colSizes = ["275px","275px"];
TopGrouper_XFormItem.prototype.numCols = 2;

ZACheckbox_XFormItem = function() {}
XFormItemFactory.createItemType("_ZA_CHECKBOX_", "za_checkbox", ZACheckbox_XFormItem, Checkbox_XFormItem);
ZACheckbox_XFormItem.prototype.labelLocation = _RIGHT_;
ZACheckbox_XFormItem.prototype.align = _RIGHT_;

ZATopGrouper_XFormItem = function() {}
XFormItemFactory.createItemType("_ZA_TOP_GROUPER_", "za_top_grouper", ZATopGrouper_XFormItem, TopGrouper_XFormItem);
ZATopGrouper_XFormItem.prototype.numCols = 2;
ZATopGrouper_XFormItem.prototype.colSizes = ["275px","auto"];

ZAPlainGrouper_XFormItem = function() {}
XFormItemFactory.createItemType("_ZA_PLAIN_GROUPER_", "za_plain_grouper", ZAPlainGrouper_XFormItem, Group_XFormItem);
ZAPlainGrouper_XFormItem.prototype.numCols = 2;
ZAPlainGrouper_XFormItem.prototype.colSizes = ["275px","auto"];
ZAPlainGrouper_XFormItem.prototype.cssClass = "PlainGrouperBorder";

ZAWizTopGrouper_XFormItem = function() {}
XFormItemFactory.createItemType("_ZAWIZ_TOP_GROUPER_", "zawiz_top_grouper", ZAWizTopGrouper_XFormItem, TopGrouper_XFormItem);
ZAWizTopGrouper_XFormItem.prototype.numCols = 2;
ZAWizTopGrouper_XFormItem.prototype.colSizes = ["200px","auto"];

ZAGroup_XFormItem = function() {}
XFormItemFactory.createItemType("_ZAGROUP_", "zagroup", ZAGroup_XFormItem, Group_XFormItem);
ZAGroup_XFormItem.prototype.numCols = 2;
ZAGroup_XFormItem.prototype.colSizes = ["275px","275px"];
ZAGroup_XFormItem.prototype.cssStyle = "margin-top:20px;margin-bottom:0px;padding-bottom:0px;";

ZAWizGroup_XFormItem = function() {}
XFormItemFactory.createItemType("_ZAWIZGROUP_", "zawizgroup", ZAWizGroup_XFormItem, Group_XFormItem);
ZAWizGroup_XFormItem.prototype.numCols = 2;
ZAWizGroup_XFormItem.prototype.colSizes = ["200px","275px"];
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
ZATabCase_XFormItem.prototype.align = _LEFT_;
ZATabCase_XFormItem.prototype.valign = _TOP_;
ZATabCase_XFormItem.prototype.getCustomHeight = function () {
	try {
//		DBG.println("getCustomHeight start");
		var form = this.getForm();
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
				headerHeight = formHeader.getElement().clientHeight ? formHeader.getElement().clientHeight : formHeader.getElement().offsetHeight;				
			}
		}
		var formTabBars = form.getItemsById("xform_tabbar");
		var tabBarHeight = 0;
		if(formTabBars) {
			var formTabBar = formTabBars[0];		
			if(formTabBar) {
				tabBarHeight = formTabBar.getElement().clientHeight ? formTabBar.getElement().clientHeight : formTabBar.getElement().offsetHeight;				
			}
		}
//		DBG.println(["getCustomHeight: \ntotalHeight:",totalHeight,"headerHeight:",headerHeight,"tabBarHeight:", tabBarHeight].join(" "));
		if(totalHeight<=0)
			return "100%";
		else
			return totalHeight - headerHeight - tabBarHeight - 2;
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
		//var tabBarHeight = this.getForm().getItemsById("xform_tabbar")[0].getElement().offsetHeight;
		if(totalWidth<=0)
			return "100%";
		else
			return totalWidth;
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