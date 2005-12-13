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
	if (this._errorIconStyle == DwtInputField.ERROR_ICON_NONE) {
		htmlEl.innerHTML = ["<input autocomplete='off' id='", inputFieldId, "' type='",
			(this._type != DwtInputField.PASSWORD) ? "text" : "password", "'/>"].join("");;
	} else {
		var htmlArr = ["<table cellspacing='0' cellpadding='0'><tr>"];
		var i = 1;
		if (this._errorIconStyle == DwtInputField.ERROR_ICON_LEFT)
			htmlArr[i++] = ["<td style='padding-right:2px;'id='", errorIconId, "'></td>"].join("");

		htmlArr[i++] = ["<td><input autocomplete='off' id='", inputFieldId, "' type='",
			(this._type != DwtInputField.PASSWORD) ? "text" : "password",
			"'/></td>"].join("");

		if (this._errorIconStyle == DwtInputField.ERROR_ICON_RIGHT)
			htmlArr[i++] = ["<td style='padding-left:2px;' id='", errorIconId, "'></td>"].join("");

		htmlArr[i++] = "</tr></table>";
		htmlEl.innerHTML = htmlArr.join("");

		if (this._errorIconStyle != DwtInputField.ERROR_ICON_NONE) {
			this._errorIconTd = document.getElementById(errorIconId);
			this._errorIconTd.vAlign = "middle";
			this._errorIconTd.innerHTML = DwtInputField._NOERROR_ICON_HTML;
		}
	}

	this._inputField = document.getElementById(inputFieldId);
	Dwt.associateElementWithObject(this._inputField, this);

	this._inputField.onkeyup = DwtInputField._keyUpHdlr;
	this._inputField.onblur = DwtInputField._blurHdlr;

	if (size) this._inputField.size = size;
	if (maxLen) {
		this._inputField.maxLength = maxLen;
		this._maxLen = maxLen;
	}

	this.setCursor("default");

	this._inputField.value = initialValue || "";

	this.setValidatorFunction(validatorCtxtObj, validator);
	this._setMouseEventHdlrs(false);
	this._setKeyPressEventHdlr(false);
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
DwtInputField.INTEGER	= 1; // Integer input field
DwtInputField.FLOAT		= 2; // Numeric input field
DwtInputField.STRING	= 3; // String input field
DwtInputField.PASSWORD	= 4; // Password input field
DwtInputField.DATE 		= 5; // Date input field

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
		    case DwtInputField.INTEGER:	this._validator = DwtInputField.validateInteger; break;
		    case DwtInputField.FLOAT:	this._validator = DwtInputField.validateFloat; break;
		    case DwtInputField.STRING:
		    case DwtInputField.PASSWORD:this._validator = DwtInputField.validateString;	break;
		    case DwtInputField.DATE: 	this._validator = DwtInputField.validateDate; break;
		    default: 					this._validator = DwtInputField.validateAny;
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
	this._errorString = errorString || "";
};

/**
* Sets a validation callback. This callback is invoked any time
* the input field is validated. The callback is invoked with two
* parameters. The first (params[0]) is the value of the input field
* The second is a boolean that if true indicates if the value is valid
*
* @param callback [AjxCallback] The callback
*/
DwtInputField.prototype.setValidationCallback =
function(callback) {
	this._validationCallback = callback;
};

/**
* Gets the internal native input element
*
* @return native input element
*/
DwtInputField.prototype.getInputElement =
function() {
	return this._inputField;
};

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
	value = this._validateInput(value);
	if (value != null)
		this._inputField.value = value;
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
	var value = this._validateInput(this._inputField.value);
	if (value != null)
		this._inputField.value = value;
};

DwtInputField.prototype.setValidStringLengths =
function(minLen, maxLen) {
	this._minLen = minLen || 0;
	if (maxLen != null) {
		this._inputField.maxLength = maxLen;
		this._maxLen = maxLen;
	}
};

DwtInputField.prototype.setNumberPrecision =
function(decimals) {
	this._decimals = decimals;
};

DwtInputField.prototype.setReadOnly =
function(readonly) {
	this._inputField.setAttribute("readonly", (readonly == null ? true : readonly));
};

DwtInputField.prototype.setRequired =
function(required) {
	this._required = required == null ? true : required;
};

DwtInputField.prototype.focus = 
function() {
	this.getInputElement().focus();
};

DwtInputField.prototype.blur = 
function() {
	this.getInputElement().blur();
};

/**
 * Checks the validity of the input field's value
 *
 * @return a canonical value if valid, or null if the field's value is not
 * valid.  Check for correction using dwtInputField.isValid() != null.
 */
DwtInputField.prototype.isValid =
function() {
	try {
		if (typeof this._validator == "function") {
			return this._validatorObj
				? this._validator.call(this._validatorObj, this._inputField.value)
				: this._validator(this._inputField.value);
		} else {
			return this._validator.test(this._inputField.value);
		}
	} catch(ex) {
		if (typeof ex == "string")
			return null;
		else
			throw ex;
	}
};

/**
 * Validates the current input in the field. This method should be called
 * if the validation style has been set to DwtInputField.MANUAL_VALIDATION
 * and it is time for the field to be validated
 *
 * @return true if the field is valid, else false
 */
DwtInputField.prototype.validate =
function() {
	var value = this._validateInput(this._inputField.value);
	if (value != null) {
		this._inputField.value = value;
		return true;
	} else {
		return false;
	}
};

/* Built-in validators */

DwtInputField.validateInteger =
function(value) {
	var n = new Number(value);
	if (isNaN(n) || (Math.round(n) != n))
		throw AjxMsg.notAnInteger;
	return DwtInputField.validateFloat.call(this, value);
};

DwtInputField.validateFloat =
function(value) {
	if (this._required && value == "")
		throw AjxMsg.valueIsRequired;
	var n = new Number(value);
	if (isNaN(n))
		throw AjxMsg.notANumber;
	if (this._minNumVal && value < this._minNumVal)
		throw AjxMessageFormat.format(AjxMsg.numberLessThanMin, this._minNumVal);
	if (this._maxNumVal && value > this._maxNumVal)
		throw AjxMessageFormat.format(AjxMsg.numberMoreThanMax, this._maxNumVal);

	// make canonical value
	if (this._decimals != null) {
		var str = n.toString();
		var pos = str.indexOf(".");
		if (pos == -1)
			pos = str.length;
		value = n.toPrecision(pos + this._decimals);
	} else {
		value = n.toString();
	}

	return value;
};

DwtInputField.validateString =
function(value) {
	if (this._required && value == "")
		throw AjxMsg.valueIsRequired;
	if (this._minLen != null && value.length < this._minLen)
		throw AjxMessageFormat.format(AjxMsg.stringTooShort, this._minLen);
	if (this._maxLen != null && value.length > this._maxLen)
		throw AjxMessageFormat.format(AjxMsg.stringTooLong, this._maxLen);
	return value;
};

DwtInputField.validateDate = 
function(value) {
	if (this._required && value == "")
		throw AjxMsg.valueIsRequired;
	
	var d = new Date(value);
	if (isNaN(d)) {
		throw AjxMsg.invalidDatetimeString;
	}

	return value;
};

DwtInputField.validateAny =
function(value) {
	if (this._required && value == "")
		throw AjxMsg.valueIsRequired;
	// note that null will always be regarded as invalid. :-) I guess this
	// is OK.  An input field never has a null value.
	return value;
};

// Private methods

DwtInputField.prototype._validateRegExp =
function(value) {
	if (this._required && value == "")
		throw AjxMsg.valueIsRequired;
	if (this._regExp && !this._regExp.test(value)) {
		throw this._errorString;
	}
	return value;
};

DwtInputField._keyUpHdlr =
function(ev) {
	var keyEv = DwtShell.keyEvent;
	keyEv.setFromDhtmlEvent(ev);

	var obj = keyEv.dwtObj;
	var keyCode = keyEv.keyCode;

	// ENTER || TAB
	var val = null;
	if ((keyCode == 0x0D || keyCode == 0x09)
	    && obj._validationStyle == DwtInputField.ONEXIT_VALIDATION)
		val = obj._validateInput(keyEv.target.value);
	else if (obj._validationStyle == DwtInputField.CONTINUAL_VALIDATION)
		val = obj._validateInput(keyEv.target.value);

	if (val != null)
		keyEv.target.value = val;

	return true;
};

DwtInputField._blurHdlr =
function(ev) {
	var obj = DwtUiEvent.getDwtObjFromEvent(ev);
	if (obj._validationStyle == DwtInputField.ONEXIT_VALIDATION) {
		var val = obj._validateInput(obj._inputField.value);
		if (val != null)
			obj._inputField.value = val;
	}
};

DwtInputField.prototype._validateInput =
function(value) {
	var isValid = true;
	var retVal;
	var errorStr;

	try {
		if (typeof this._validator == "function") {
			retVal = value = this._validatorObj
				? this._validator.call(this._validatorObj, value)
				: this._validator(value);
		} else if (!this._validator.test(value)) {
			errorStr = this._errorString;
		}
	} catch(ex) {
		if (typeof ex == "string")
			errorStr = ex;
		else
			throw ex;
	}

	if (errorStr) {
		this.getHtmlElement().className = this._errorClassName;
		if (this._errorIconTd)
			this._errorIconTd.innerHTML = DwtInputField._ERROR_ICON_HTML;
		this.setToolTipContent(errorStr);
		isValid = false;
		retVal = null;
	} else {
		this.getHtmlElement().className = this._origClassName;
		if (this._errorIconTd)
			this._errorIconTd.innerHTML = DwtInputField._NOERROR_ICON_HTML;
		this.setToolTipContent(null);
		isValid = true;
	}

	if (this._validationCallback)
		this._validationCallback.run([this, isValid]);

	return retVal;
};
