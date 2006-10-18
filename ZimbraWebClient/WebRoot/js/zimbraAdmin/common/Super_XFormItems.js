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

Cos_List_XModelItem = function (){}
XModelItemFactory.createItemType("_COS_LIST_", "list_enum", Cos_List_XModelItem, Cos_String_XModelItem);
Cos_List_XModelItem.prototype.outputType = List_XModelItem.prototype.outputType;
Cos_List_XModelItem.prototype.itemDelimiter = List_XModelItem.prototype.itemDelimiter;
Cos_List_XModelItem.prototype.listItem = List_XModelItem.prototype.listItem;
Cos_List_XModelItem.prototype.getOutputType  = List_XModelItem.prototype.getOutputType;
Cos_List_XModelItem.prototype.getItemDelimiter = List_XModelItem.prototype.getItemDelimiter;
Cos_List_XModelItem.prototype.getListItem  = List_XModelItem.prototype.getListItem;
Cos_List_XModelItem.prototype.initializeItems = List_XModelItem.prototype.initializeItems;
Cos_List_XModelItem.prototype.validateType = List_XModelItem.prototype.validateType;



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
	else if ( (this.getModelItem().getLocalValue(this.getInstance()) instanceof Array) && 
	(this.getModelItem().getLocalValue(this.getInstance()).length==0) )	
		return false;
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
		if(container.className != null && container.className != "ZmOverride")
			this._originalClassName = container.className;
		else 
			this._originalClassName	= "xform_field_container";
			
		container.className="ZmOverride";
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
Super_Textfield_XFormItem.prototype.subLabel = null;
Super_Textfield_XFormItem.prototype.numCols = 3;
Super_Textfield_XFormItem.prototype.initializeItems = function() {
	var subLabel = this.getInheritedProperty("subLabel");
	var textFieldCssClass = this.getInheritedProperty("textFieldCssClass");
	var textFieldCssStyle = this.getInheritedProperty("textFieldCssStyle");
	var textFieldWidth = this.getInheritedProperty("textFieldWidth");
	var toolTip = this.getInheritedProperty("toolTipContent");

	var txtField =	{	
		type:_TEXTFIELD_, ref:".",align:_RIGHT_,
		elementChanged: function(elementValue,instanceValue, event) {
			this.getForm().itemChanged(this, elementValue, event);
		},		
		onChange:Composite_XFormItem.onFieldChange,
		//onClick: "Super_Textfield_XFormItem.handleClick",
		//onMouseout: "Super_Textfield_XFormItem.handleMouseout",
		toolTipContent: toolTip,
		updateElement:function(value) {
			Super_XFormItem.updateCss.call(this,5);
			Textfield_XFormItem.prototype.updateElement.call(this, value);
		},
		label:subLabel,	labelLocation:(subLabel ? _RIGHT_ : _NONE_),
		cssClass:textFieldCssClass,
		cssStyle:textFieldCssStyle,
		width:textFieldWidth,
		forceUpdate:true,
		relevantBehavior:_PARENT_
	};
	
	
	
	var anchorCssStyle = this.getInheritedProperty("anchorCssStyle");
	
	var anchorHlpr = {	
		type:_SUPER_ANCHOR_HELPER_, ref:".",
		relevant:"Super_XFormItem.checkIfOverWriten.call(item)",
		relevantBehavior:_BLOCK_HIDE_,
		onChange:Composite_XFormItem.onFieldChange,
		cssStyle: (anchorCssStyle ? anchorCssStyle : "width:200px")
	};
	this.items = [txtField,anchorHlpr];
	Composite_XFormItem.prototype.initializeItems.call(this);
	

}	

Super_Textfield_XFormItem.prototype.items = [];

/**
*	_SUPER_CHECKBOX_ form item type
**/
Super_Checkbox_XFormItem = function () {}
XFormItemFactory.createItemType("_SUPER_CHECKBOX_", "super_checkbox", Super_Checkbox_XFormItem, Super_XFormItem);

Super_Checkbox_XFormItem.prototype.useParentTable = false;
Super_Checkbox_XFormItem.prototype.numCols = 2;
Super_Checkbox_XFormItem.prototype.initializeItems = function() {
	var anchorCssStyle = this.getInheritedProperty("anchorCssStyle");
	if(anchorCssStyle) {
		this.getItems()[1].cssStyle = anchorCssStyle;
	} else {
		this.getItems()[1].cssStyle = "width:200px";
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

Super_Checkbox_XFormItem.prototype.items = [
	{	type:_CHECKBOX_, ref:".", align:_LEFT_,
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
		cssStyle:"width:100px"
	}
];


/**
*	SUPER__HOSTPORT_ form item type
**/
Super_HostPort_XFormItem = function () {}
XFormItemFactory.createItemType("_SUPER_HOSTPORT_", "super_hostport", Super_HostPort_XFormItem, Super_XFormItem);


Super_HostPort_XFormItem.prototype.useParentTable = false;
Super_HostPort_XFormItem.prototype.numCols = 3;

Super_HostPort_XFormItem.prototype.items = [
	{	type:_HOSTPORT_, ref:".",
		onChange:Composite_XFormItem.onFieldChange,
		onClick: "Super_HostPort_XFormItem.handleClick",
		onMouseout: "Super_HostPort_XFormItem.handleMouseout",
		updateElement:function(value) {
			Super_XFormItem.updateCss.call(this,5);
			//HostPort_XFormItem.prototype.updateElement.call(this, value);
		}
	},
	{	
		type:_SUPER_ANCHOR_HELPER_, ref:".",
		relevant:"Super_XFormItem.checkIfOverWriten.call(item)",
		relevantBehavior:_BLOCK_HIDE_,
		onChange:Composite_XFormItem.onFieldChange,cssStyle:"width:100px"
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
			relevantBehavior:_BLOCK_HIDE_,cssSyle:(anchorCssStyle ? anchorCssStyle : "width:200px;"),
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
*	SUPER_SELECT1 form item type
**/
Super_Select1_XFormItem = function () {}
XFormItemFactory.createItemType("_SUPER_SELECT1_", "super_select1", Super_Select1_XFormItem, Super_XFormItem);
Super_Select1_XFormItem.prototype.trueValue = "TRUE";
Super_Select1_XFormItem.prototype.falseValue = "FALSE";
Super_Select1_XFormItem.prototype.initializeItems = function() {
	var anchorCssStyle = this.getInheritedProperty("anchorCssStyle");
	if(anchorCssStyle) {
		this.getItems()[1].cssStyle = anchorCssStyle;
	} else {
		this.getItems()[1].cssStyle = "width:200px";
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
Super_Select1_XFormItem.prototype.numCols = 3;

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
* _SUPER_LIFETIME_ XForm item type
**/

function Super_Lifetime_XFormItem() {}
XFormItemFactory.createItemType("_SUPER_LIFETIME_", "super_lifetime", Super_Lifetime_XFormItem, Super_XFormItem);
Super_Lifetime_XFormItem.prototype.numCols = 5;
Super_Lifetime_XFormItem.prototype.TIME_CHOICES = [
 				{value:"d", label:"Days"},
				{value:"h", label:"Hours"},
				{value:"m", label:"Minutes"},
				{value:"s", label:"Seconds"}
];

Super_Lifetime_XFormItem.prototype.initializeItems = function() {
	var subLabel = this.getInheritedProperty("subLabel");

	var txtField =	{
		type:_TEXTFIELD_, ref:".", labelLocation:_NONE_,relevantBehavior:_PARENT_, cssClass:"admin_xform_number_input", 
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
		type:_OSELECT1_, ref:".", relevantBehavior:_PARENT_, choices:Super_Lifetime_XFormItem.prototype.TIME_CHOICES,
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
		label:subLabel,	labelLocation:(subLabel ? _RIGHT_ : _NONE_),
		forceUpdate:true		
	};
	var anchorCssStyle = this.getInheritedProperty("anchorCssStyle");
	
	var anchorHlpr = {	
		type:_SUPER_ANCHOR_HELPER_, ref:".",
		relevant:"Super_XFormItem.checkIfOverWriten.call(item)",
		relevantBehavior:_BLOCK_HIDE_,
		onChange:Composite_XFormItem.onFieldChange,
		cssStyle: (anchorCssStyle ? anchorCssStyle : "width:200px")
	};
	this.items = [txtField,selectField,anchorHlpr];
	Composite_XFormItem.prototype.initializeItems.call(this);	
}

Super_Lifetime_XFormItem.prototype.items = [ ];



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
