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
* This class represents an input field..
*
* CSS Elements
*	<className> input - specifies the look of the input field during normal editing
*   <className>-error input - specifies the look of the input field in an error state
*
*
* @author Ross Dargahi
* @param parent	[DwtComposite] the parent widget
* @param type [int]	the type of the input field (e.g. DwtInputField.INTEGER). Required
* @param initialValue [string] the initial value of the field. Default browser
* @param size [int] size of the input field (in characters). Default system
* @param maxLen [int] maximum length (in characters) of the field value. Default browser
* @param errorIconStyle [int] error icon style. Default DwtInputField.ERROR_ICON_LEFT
* @param validationStyle [int] validation type. Default DwtInputField.ONEXIT_VALIDATION 
* @param validator [function] validation function. Default built-in validator for <i>type</i>
* @param validatorCtxtObj [object] validator context object. If presen then the validator will
*		be called in the context of this object.
* @param className [string]	a CSS class
* @param posStyle [string] positioning style
*/
function DwtInputField(parent, type, initialValue, size, maxLen, errorIconStyle, validationStyle,
                       validator, validatorCtxtObj, className, posStyle) {
                       
	if (arguments.length == 0) return;
	this._origClassName = className ? className : "DwtInputField";
	this._errorClassName = this._origClassName + "-Error";
	
	DwtControl.call(this, parent, className, posStyle);

	this._type = type ? type : DwtInputField.STRING;
	this._errorIconStyle = errorIconStyle ? errorIconStyle : DwtInputField.ERROR_ICON_RIGHT;
	this._validationStyle = validationStyle ? validationStyle : DwtInputField.ONEXIT_VALIDATION;

	var inputFieldId = Dwt.getNextId();
	var errorIconId = Dwt.getNextId();
	var htmlEl = this.getHtmlElement();
	var htmlArr = ["<table cellspacing='0' cellpadding='0'><tr>"];
	var i = 1;
	if (this._errorIconStyle == DwtInputField.ERROR_ICON_LEFT)
		htmlArr[i++] = ["<td style='padding-right:2px;'id='", errorIconId, "'></td>"].join("");

	htmlArr[i++] = ["<td><input id='", inputFieldId, "' type='", 
		(this._type != DwtInputField.PASSWORD) ? "text" : "password", 
		"'/></td>"].join("");
		
	if (this._errorIconStyle == DwtInputField.ERROR_ICON_RIGHT)
		htmlArr[i++] = ["<td style='padding-left:2px;' id='", errorIconId, "'></td>"].join("");
	
	htmlArr[i++] = "</tr></table>";
	htmlEl.innerHTML = htmlArr.join("");
	
	this._inputField = document.getElementById(inputFieldId);
	Dwt.associateElementWithObject(this._inputField, this);
	
	this._inputField.onkeyup = DwtInputField._keyUpHdlr;
	this._inputField.onblur = DwtInputField._blurHdlr;
	
	if (this._errorIconStyle != DwtInputField.ERROR_ICON_NONE) {
		this._errorIconTd = document.getElementById(errorIconId);
		this._errorIconTd.vAlign = "middle";
		this._errorIconTd.innerHTML = DwtInputField._NOERROR_ICON_HTML;
	}
	
	if (size) this._inputField.size = size;
	if (maxLen) this._inputField.maxLength = maxLen;
	
	this.setCursor("default");
	
	this._inputField.value = (initialValue) ? initialValue : "";

	this.setValidatorFunction(validatorCtxtObj, validator);
}

DwtInputField.prototype = new DwtControl;
DwtInputField.prototype.constructor = DwtInputField;

// Error Icon Style
DwtInputField.ERROR_ICON_LEFT = 1;
DwtInputField.ERROR_ICON_RIGHT = 2;
DwtInputField.ERROR_ICON_NONE = 3;

// Validation Style
DwtInputField.CONTINUAL_VALIDATION = 1; // validate field after each character is typed
DwtInputField.ONEXIT_VALIDATION    = 2; // validate the field (i.e. after TAB or CR)
DwtInputField.MANUAL_VALIDATION    = 3; // validate the field  manually

// types
DwtInputField.INTEGER  = 1; // Integer input field
DwtInputField.STRING   = 2; // String input field
DwtInputField.PASSWORD = 3; // Password input field

DwtInputField._ERROR_ICON_HTML = AjxImg.getImageHtml("ClearSearch");
DwtInputField._NOERROR_ICON_HTML = AjxImg.getImageHtml("Blank_9");

// Public methods

DwtInputField.prototype.toString = 
function() {
	return "DwtInputField";
};


/**
* Sets the validator function. This function is executed during validation
*
* @param obj [object] If present then the validator function is executed within 
*		the context of this object
* @param validator [function] Validator function
*/
DwtInputField.prototype.setValidatorFunction =
function(obj, validator) {
	if (validator) {
		this._validator = validator;
		this._validatorObj = obj;
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

/**
* Sets the validator to be a regular expression instead of a function
*
* @param regExp [reg exp] regular exxression
* @param errorString Error string to set for tooltip if the user enters invalid data
*/
DwtInputField.prototype.setValidatorRegExp =
function(regExp, errorString) {
	this._validator = regExp;
	this._validatorObj = null;
	this._errorString = (errorString) ? errorString : "";
}

/**
* Sets a validation callback. This callback is invoked any time 
* the input field is validated
*
* @param callback [AjxCallback] The callback
*/
DwtInputField.prototype.setValidationCallback =
function(callback) {
	this._validationCallback = callback;
}

/**
* Gets the input fields current value
*
* @return Input field's current value
*/
DwtInputField.prototype.getValue =
function() {
	return this._inputField.value;
};

/**
* Sets a new value for the input field
*/
DwtInputField.prototype.setValue =
function(value) {
	this._inputField.value = value;
	this._validateInput();
};

/**
* This method is only applicable for numeric input fields. It sets
* the valid range (inclusive) of numeric values for the field
*
* @param min minimum permittedvalue. If null, then no minimum is established
* @param max maximum permitted value. If null, then no maximum is established
*/
DwtInputField.prototype.setValidNumberRange =
function(min, max) {
	this._minNumVal = min;
	this._maxNumVal = max;
	this._validateInput(this._inputField.value);
}

/**
* Checks the validity of the input field's value
*
* @return true if the field is valid, else false
*/
DwtInputField.prototype.isValid =
function() {
	if (typeof this._validator == "function") {
		return ((this._validatorObj) ? this._validator.call(this._validatorObj, this._inputField.value) 
					: this._validator(this._inputField.value)) == null;
	} else {
		return this._validator.test(this._inputField.value);
	}
}

/**
* Validates the current input in the field. This method should be called
* if the validation style has been set to DwtInputField.MANUAL_VALIDATION
* and it is time for the field to be validated
*
* @return true if the field is valid, else false
*/
DwtInputField.prototype.validate =
function() {
	return (this._validateInput(this._inputField.value) == null);
}	
	

/* Built-in validators */

DwtInputField.validateInteger =
function(value) {
	if (!AjxUtil.isInteger(value))
		return AjxMsg.notANumber; 
	else if (this._minNumVal && value < this._minNumVal)
		return AjxMessageFormat.format(AjxMsg.numberLessThanMin, this._minNumVal).toString();
	else if(this._maxNumVal && value > this._maxNumVal)
		return AjxMessageFormat.format(AjxMsg.numberMoreThanMax, this._maxNumVal).toString();
	else
		return null;
};

DwtInputField.validateAny =
function(value) {
	return null;
};


// Private methods

DwtInputField.prototype._validateRegExp =
function(value) {
	if (this._regExp && !this._regExp.test(value)) {
		return this._invalidString;
	}
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
	else if (obj._validationStyle == DwtInputField.CONTINUAL_VALIDATION)
	    obj._validateInput(keyEv.target.value);
	
    return true;
};

DwtInputField._blurHdlr = 
function(ev) {
	var obj = DwtUiEvent.getDwtObjFromEvent(ev);
    if (obj._validationStyle == DwtInputField.ONEXIT_VALIDATION)
    	obj._validateInput(obj._inputField.value);
};

DwtInputField.prototype._validateInput =
function(value) {
	var retVal = true;
	var errorStr;
	
	if (typeof this._validator == "function")
		errorStr = (this._validatorObj) ? this._validator.call(this._validatorObj, value) : this._validator(value);
	else if (!this._validator.test(value))
		errorStr = this._errorString;
		
	if (errorStr) {
		this.getHtmlElement().className = this._errorClassName;
		this._inputField.title = errorStr;
		if (this._errorIconTd)
			this._errorIconTd.innerHTML = DwtInputField._ERROR_ICON_HTML;
		retVal = false;
	} else {
		this.getHtmlElement().className = this._origClassName;
		this._inputField.title = "";
		if (this._errorIconTd) 
			this._errorIconTd.innerHTML = DwtInputField._NOERROR_ICON_HTML;
	}
	
	if (this._validationCallback)
		this._validationCallback.run(retVal);
	
	return retVal;
};
