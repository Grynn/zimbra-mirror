/*
* ***** BEGIN LICENSE BLOCK *****
* Version: MPL 1.1
*
* The contents of this file are subject to the Mozilla Public
* License Version 1.1 ("License"); you may not use this file except in
* compliance with the License. You may obtain a copy of the License at
* http://www.zimbra.com/license
*
* Software distributed under the License is distributed on an "AS IS"
* basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See
* the License for the specific language governing rights and limitations
* under the License.
*
* The Original Code is: Zimbra AJAX Toolkit.
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
* Creates an input field.
* @constructor
* @class
* This class represents a label, which consists of an image and/or some text. It is used
* both as a concrete class and as the base class for buttons. The label's components are
* managed within a table. The label can be enabled or disabled, which are reflected in 
* its display. A disabled label looks greyed out.
*
* CSS Elements
*	<className> input - specifies the look of the input field during normal editing
*   <className>-error input - specifies the look of the input field in an error state
*
*
* @author Ross Dargahi
* @param parent		the parent widget
* @param labelText	the text of the label. If this is null, then no label is created
* @param fieldValue the initial field value. If null, the field is empty
* @param className	a CSS class
* @param posStyle	positioning style
*/
function DwtInputField(parent, type, initialValue, size, maxLen, errorIconStyle, validationStyle,
                       validator, className, posStyle) {
                       
	if (arguments.length == 0) return;
	this._origClassName = className ? className : "DwtInputField";
	this._errorClassName = this._origClassName + "-Error";
	
	DwtControl.call(this, parent, className, posStyle);

	this._type = type ? type : DwtInputField.STRING;
	this._errorIconStyle = errorIconStyle ? errorIconStyle : DwtInputField.ERROR_ICON_RIGHT;
	this._validationStyle = validationStyle ? validationStyle : DwtInputField.ONEXIT_VALIDATION;
	this.setValidatorFunction(validator);

	var inputFieldId = Dwt.getNextId();
	var htmlEl = this.getHtmlElement();
	
	htmlEl.innerHTML = [
		"<table cellspacing='0' cellpadding='0'><tr><td><input id='", inputFieldId, "' type='", 
		(this._type != DwtInputField.PASSWORD) ? "text" : "password", 
		"'/></td></tr></table>"].join("");
	
	this._inputField = document.getElementById(inputFieldId);
	this._inputField.value = initialValue ? initialValue : "";
	Dwt.associateElementWithObject(this._inputField, this);
	
	this._inputField.onkeyup = DwtInputField._keyUpHdlr;
	this._inputField.onblur = DwtInputField._blurHdlr;
	
	if (size) this._inputField.size = size;
	if (maxLen) this._inputField.maxLength = maxLen;
	
	this.setCursor("default");
}

DwtInputField.prototype = new DwtControl;
DwtInputField.prototype.constructor = DwtInputField;

// Error Icon Style
DwtInputField.ERROR_ICON_LEFT = 1;
DwtInputField.ERROR_ICON_RIGHT = 2;
DwtInputField.ERROR_ICON_NONE = 3;

// Validation Style
DwtInputField.CONTINUAL_VALIDATION = 1;  // validate field after each character is typed
DwtInputField.ONEXIT_VALIDATION = 2; // validate the field (i.e. after TAB or CR)

// types
DwtInputField.INTEGER = 1;
DwtInputField.STRING = 2;
DwtInputField.PASSWORD = 3;


// Public methods

DwtInputField.prototype.toString = 
function() {
	return "DwtInputField";
};

DwtInputField.prototype.setValidatorFunction =
function(validator) {
	if (validator) {
		this._validator = validator;
	} else {
		switch (this._type) {
			case DwtInputField.INTEGER:
				this._validator = DwtInputField.validateInteger;
				break;
			default:
				this._validator = DwtInputField.validateAny;
		}
	}
};

DwtInputField.prototype.setInputFieldSize =
function() {
	this._inputField.size = size;
};

DwtInputField.prototype.setInputFieldMaxLength =
function(maxLen) {
	this._inputField.maxLen = maxLen;
};

DwtInputField.prototype.getValue =
function() {
	return this._inputField.value;
};

DwtInputField.prototype.setValue =
function(value) {
	this._inputField.value = value;
};

/* Built-in validators */
DwtInputField.validateInteger =
function(value) {
	if (AjxUtil.isInteger(value))
		return null;
	else
		return "BOGUS INTEGER";
};

DwtInputField.validateAny =
function(value) {
	return null;
};

DwtInputField._keyUpHdlr = 
function(ev) {
	var keyEv = DwtShell.keyEvent;
	keyEv.setFromDhtmlEvent(ev);
	
	var obj = keyEv.dwtObj;
    var keyCode = keyEv.keyCode;

	// ENTER || TAB   
    if ((keyCode == 0x0D || keyCode == 0x09)
    	  && obj._validationStyle == DwtInputField.ONEXIT_VALIDATION)
    	obj._validateInput(keyEv.target.value);
	else
	    obj._validateInput(keyEv.target.value);
	
    return true;
};

DwtInputField._blurHdlr = 
function(ev) {
DBG.println("BLUR HDLR");
	var obj = DwtUiEvent.getDwtObjFromEvent(ev);
    if (obj._validationStyle == DwtInputField.ONEXIT_VALIDATION)
    	obj._validateInput(obj._inputField.value);
};

DwtInputField.prototype._validateInput =
function(value) {
	var errorStr = this._validator(value);
	if (errorStr) {
		this.getHtmlElement().className = this._errorClassName;
		this._inputField.title = errorStr;
	} else {
		this.getHtmlElement().className = this._origClassName;
		this._inputField.title = "";
	}
};

