/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2005, 2006, 2007, 2008, 2009, 2010, 2011, 2012, 2013 Zimbra Software, LLC.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.4 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 */


/**
 * Creates an input field.
 * @constructor
 * @class
 * This class represents an input field..
 * <p>
 * <h3>CSS Elements</h3>
 * <ul>
 * <li><code>&lt;className&gt;</code> input 			specifies the look of the input field during normal editing
 * <li><code>&lt;className&gt;-error</code> input		specifies the look of the input field in an error state
 * </ul>
 *
 * @author Ross Dargahi
 *
 * @param {hash}	params			a hash of parameters
 * @param {DwtComposite}      params.parent			the parent widget
 * @param {constant}      params.type				the data type of the input field
 * @param {string}      params.initialValue		the initial value of the field
 * @param {number}      params.size				size of the input field (in characters)
 * @param {number}      params.rows				the number of rows (more than 1 means textarea)
 * @param {boolean}      params.forceMultiRow		if <code>true</code>, forces use of textarea even if rows == 1
 * @param {number}      params.maxLen			the maximum length (in characters) of the input
 * @param {constant}      params.errorIconStyle		the error icon style
 * @param {constant}      params.validationStyle	the validation type
 * @param  {function}     params.validator			the custom validation function
 * @param {Object}      params.validatorCtxtObj		the object context for validation function
 * @param {string}      params.className			the CSS class
 * @param {constant}      params.posStyle			the positioning style (see {@link DwtControl})
 * @param {boolean}      params.required          if <code>true</code>, mark as required.
 * @param {string}      params.hint				a hint to display in the input field when the value is empty.
 * @param {string}      params.id				an explicit ID to use for the control's DIV element
 * @param {string}      params.inputId			an explicit ID to use for the control's INPUT element
 *
 * @extends		DwtComposite
 * 
 */
DwtInputField = function(params) {

	if (arguments.length == 0) return;
	params.className = params.className  || "DwtInputField";
	this._origClassName = params.className;
	this._errorClassName = this._origClassName + "-Error";
	this._hintClassName = this._origClassName + "-hint";
	this._disabledClassName = this._origClassName + "-disabled";
	this._errorHintClassName = this._origClassName + "-errorhint";
	DwtComposite.call(this, params);

    this._inputEventHandlers = {};

    this._type = params.type ? params.type : DwtInputField.STRING;
    this._rows = params.rows ? params.rows : 1;
    this._size = params.size;

	this._errorIconStyle = params.errorIconStyle ? params.errorIconStyle :
							params.validator ? DwtInputField.ERROR_ICON_RIGHT : DwtInputField.ERROR_ICON_NONE;
	this._validationStyle = params.validationStyle ? params.validationStyle : DwtInputField.ONEXIT_VALIDATION;

	this._hasError = false;
	this._hintIsVisible = false;
	this._hint = params.hint;
	
	var inputFieldId = params.inputId || Dwt.getNextId();
	var errorIconId = Dwt.getNextId();
	var htmlEl = this.getHtmlElement();
	if (this._errorIconStyle == DwtInputField.ERROR_ICON_NONE) {
		if (params.forceMultiRow || (params.rows && params.rows > 1)) {
			var htmlArr = ["<textarea id='", inputFieldId, "' rows=", params.rows];
			var i = htmlArr.length;
			if (params.forceMultiRow || params.size) {
				htmlArr[i++] = " cols=";
				htmlArr[i++] = params.size || 1;
			}
			if (params.wrap) {
				htmlArr[i++] = " wrap=";
				htmlArr[i++] = params.wrap;
			}
			htmlArr[i++] = "></textarea>"
			htmlEl.innerHTML = htmlArr.join("");
		} else {
			htmlEl.innerHTML = ["<input id='",inputFieldId,"'>"].join("");
		}

	} else {
		var htmlArr = ["<table cellspacing='0' cellpadding='0'><tr>"];
		var i = 1;
		if (this._errorIconStyle == DwtInputField.ERROR_ICON_LEFT)
			htmlArr[i++] = ["<td style='padding-right:2px;'id='", errorIconId, "'></td>"].join("");

		htmlArr[i++] = ["<td>", "<input id='", inputFieldId, "'>", "</td>"].join("");

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

	this._tabGroup = new DwtTabGroup(this._htmlElId);
	if (params.forceMultiRow || this._rows > 1) {
        this._inputField = document.getElementById(inputFieldId);
        this._inputField.onkeyup = DwtInputField._keyUpHdlr;
        this._inputField.onblur = DwtInputField._blurHdlr;
		this._inputField.onfocus = DwtInputField._focusHdlr;
        this._inputField.onkeydown = DwtInputField._keyDownHdlr;

        if (params.size)
            this._inputField.size = params.size;
        if (params.maxLen)
            this._inputField.maxLength = this._maxLen = params.maxLen;

        //MOW:  this.setCursor("default");

        this._inputField.value = params.initialValue || "";
		this._tabGroup.addMember(this._inputField);
	}
    else {
        var oinput = document.getElementById(inputFieldId);
        var ninput = this.__createInputEl(params);
		// bug fix #
		if (AjxEnv.isCamino) {
			oinput.parentNode.style.overflow = "hidden";
		}
		oinput.parentNode.replaceChild(ninput, oinput);
	}

    this.setValidatorFunction(params.validatorCtxtObj, params.validator);
	this._setMouseEventHdlrs(false);
	this._setKeyPressEventHdlr(false);
	
    if (params.required != null) {
        this.setRequired(params.required);
    }
};

DwtInputField.prototype = new DwtComposite;
DwtInputField.prototype.constructor = DwtInputField;

DwtInputField.prototype.isDwtInputField = true;
DwtInputField.prototype.isInputControl = true;
DwtInputField.prototype.toString = function() { return "DwtInputField"; };

//
// Constants
//

// Error Icon Style
/**
 * Defines the "left" error icon style.
 */
DwtInputField.ERROR_ICON_LEFT = 1;
/**
 * Defines the "right" error icon style.
 */
DwtInputField.ERROR_ICON_RIGHT = 2;
/**
 * Defines the "none" error icon style.
 */
DwtInputField.ERROR_ICON_NONE = 3;

// Validation Style
/**
 * Validate field after each character is typed.
 */
DwtInputField.CONTINUAL_VALIDATION = 1;
/**
 * Validate the field (i.e. after TAB or CR).
 */
DwtInputField.ONEXIT_VALIDATION    = 2;
/**
 * Validate the field  manually.
 */
DwtInputField.MANUAL_VALIDATION    = 3;

// types
/**
 * Defines the "Integer or float input field" data type.
 */
DwtInputField.NUMBER 	= 1;
/**
 * Defines the "Integer input field (no floating point numbers)" data type.
 */
DwtInputField.INTEGER	= 2;
/**
 * Defines the "Numeric input field" data type.
 */
DwtInputField.FLOAT		= 3;
/**
 * Defines the "String input field" data type.
 */
DwtInputField.STRING	= 4;
/**
 * Defines the "Password input field" data type.
 */
DwtInputField.PASSWORD	= 5;
/**
 * Defines the "Date input field" data type.
 */
DwtInputField.DATE 		= 6;

DwtInputField._ERROR_ICON_HTML = AjxImg.getImageHtml("ClearSearch");
DwtInputField._NOERROR_ICON_HTML = AjxImg.getImageHtml("Blank_9");

//
// Public methods
//

DwtInputField.prototype.dispose =
function() {
	this._errorIconTd = null;
	this._inputField = null;
	DwtComposite.prototype.dispose.call(this);
};

DwtInputField.prototype.getTabGroupMember =
function() {
	return this._tabGroup;
};

DwtInputField.prototype.setHandler =
function(eventType, hdlrFunc) {
	if (!this._checkState()) return;
    this._inputEventHandlers[eventType] = hdlrFunc;
	Dwt.setHandler(this.getInputElement(), eventType, hdlrFunc);
};

/**
 * Sets the input type.
 * 
 * @param	{constant}	type		the input type
 */
DwtInputField.prototype.setInputType = function(type) {
    if (type != this._type && this._rows == 1) {
        this._type = type;
        if (AjxEnv.isIE) {
            var oinput = this._inputField;
            var ninput = this.__createInputEl();
            oinput.parentNode.replaceChild(ninput, oinput);
        }
        else {
            this._inputField.type = this._type != DwtInputField.PASSWORD ? "text" : "password";
        }
    }
}

/**
* Sets the validator function. This function is executed during validation.
*
* @param {Object}	obj 		if present, the validator function is executed within
*		the context of this object
* @param {function}	validator 	the validator function
*/
DwtInputField.prototype.setValidatorFunction =
function(obj, validator) {
	if (validator) {
		this._validator = validator;
		this._validatorObj = obj;
	} else {
		switch (this._type) {
			case DwtInputField.NUMBER:	this._validator = DwtInputField.validateNumber; break;
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
* Sets the validator to be a regular expression instead of a function.
*
* @param {string}	regExp 	the regular expression
* @param {string}	errorString 		the error string to set for tooltip if the user enters invalid data
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
* parameters. The first <code>params[0]</code> is the value of the input field.
* The second <code>params[1]</code> is a {Boolean} that if <code>true</code> indicates if the value is valid.
*
* @param {AjxCallback}	callback the callback
*/
DwtInputField.prototype.setValidationCallback =
function(callback) {
	this._validationCallback = callback;
};

/**
* Gets the internal native input element
*
* @return {Element}	the input element
*/
DwtInputField.prototype.getInputElement =
function() {
	return this._inputField;
};

/**
* Gets the input field current value.
*
* @return {string}	 the value
*/
DwtInputField.prototype.getValue =
function() {
	return this._hintIsVisible ? '' : AjxStringUtil.trim(this._inputField.value);
};

/**
 * Sets the value for the input field.
 *
 * @param	{string}	value	the value
 * @param	{boolean}	noValidate		if <code>true</code>, do not validate
 */
DwtInputField.prototype.setValue =
function(value, noValidate) {
	// XXX: if we're disabled, the validation step messes up the style
	value = value || "";
	this._inputField.value = value;
	if(!noValidate) {
		value = this._validateInput(value);
		if (value != null) {
			this._inputField.value = value;
		}
	}
	if (this._hintIsVisible && value) {
		this._hideHint(value);
	} else if (!value) {
		this._showHint();
	}
};

DwtInputField.prototype.clear =
function() {
	this.setValue("");
};

/**
 * Sets the hint for the input field.
 *
 * @param {string}	hint 	the hint
 */
DwtInputField.prototype.setHint =
function(hint) {
	this._hint = hint;
	var inputElement = this.getInputElement();
	if (AjxEnv.supportsPlaceholder) {
		inputElement.placeholder = hint || "";
		return;
	}

	if (this._hintIsVisible) {
		inputElement.value = hint;
		if (!hint) {
			this._hintIsVisible = false;
			this._updateClassName();
		}
	}
	else if (inputElement.value === '') {
		this._showHint();
	}
};

/**
 * Sets a valid number range. This method is only applicable for numeric input fields. It sets
 * the valid range (inclusive) of numeric values for the field
 *
 * @param {number}		min 		the minimum permitted value or <code>null</code> for no minimum
 * @param {number}	max 		the maximum permitted value or <code>null</code> for no maximum
 */
DwtInputField.prototype.setValidNumberRange =
function(min, max) {
	this._minNumVal = min;
	this._maxNumVal = max;
	var value = this._validateInput(this.getValue());
	if (value != null)
		this.setValue(value);
};

/**
 * Sets a valid string length.
 *
 * @param {number}	min 		the minimum length or <code>null</code> for no minimum
 * @param {number}	max 		the maximum length or <code>null</code> for no maximum
 */
DwtInputField.prototype.setValidStringLengths =
function(minLen, maxLen) {
	this._minLen = minLen || 0;
	if (maxLen != null) {
		this._inputField.maxLength = maxLen;
		this._maxLen = maxLen;
	}
};

/**
 * Sets the number precision.
 * 
 * @param	{number}	decimals	the decimals
 */
DwtInputField.prototype.setNumberPrecision =
function(decimals) {
	this._decimals = decimals;
};

/**
 * Sets the read only flag.
 * 
 * @param	{boolean}	readonly		if <code>true</code>, make field read only
 */
DwtInputField.prototype.setReadOnly =
function(readonly) {
	this._inputField.setAttribute("readonly", (readonly == null ? true : readonly));
};

/**
 * Sets the required flag.
 * 
 * @param	{boolean}	required		if <code>true</code>, make field required
 */
DwtInputField.prototype.setRequired =
function(required) {
	var nrequired = required == null ? true : required;
	if (this._required != nrequired) {
		this._required = nrequired;
		this.validate();
	}
};

/**
 * Gets the enabled flag.
 * 
 * @return	{boolean}	<code>true</code> if the field is disabled
 */
DwtInputField.prototype.getEnabled = 
function() {
	return !this.getInputElement().disabled;
};

/**
 * Sets the enabled flag.
 * 
 * @param	{boolean}	enabled		if <code>true</code>, enable the field
 */
DwtInputField.prototype.setEnabled = 
function(enabled) {	
	DwtControl.prototype.setEnabled.call(this, enabled);
	this.getInputElement().disabled = !enabled;
	this._validateInput(this.getValue());
};

/**
 * Focuses on this field.
 */
DwtInputField.prototype.focus = 
function() {
	if (this.getEnabled()) {
		this._hasFocus = true;
		this.getInputElement().focus();
        DwtShell.getShell(window).getKeyboardMgr().grabFocus(this.getTabGroupMember());
	}
};

DwtInputField.prototype.blur = 
function() {
	this.getInputElement().blur();
};

/**
 * Sets the visibility flag.
 * 
 * @param	{boolean}	visible		if <code>true</code>, the field is visible
 */
DwtInputField.prototype.setVisible = function(visible) {
	DwtComposite.prototype.setVisible.apply(this, arguments);
	Dwt.setVisible(this.getInputElement(), visible);
};

/**
 * Checks the validity of the input field value.
 *
 * @return {string}	a canonical value if valid or <code>null</code> if the field value is not valid
 */
DwtInputField.prototype.isValid =
function() {
	if (!this.getEnabled()) {
		return this.getValue();
	}
	try {
		if (typeof this._validator == "function") {
			return this._validatorObj
				? this._validator.call(this._validatorObj, this.getValue(), this)
				: this._validator(this.getValue());
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
 * Checks the validity of the input field value; returns the error message, if any.
 */
DwtInputField.prototype.getValidationError =
function() {
	this.validate();

	return this._validationError;
};

/**
 * Validates the current input in the field. This method should be called
 * if the validation style has been set to DwtInputField.MANUAL_VALIDATION
 * and it is time for the field to be validated
 *
 * @return {boolean}	<code>true</code> if the field is valid
 */
DwtInputField.prototype.validate =
function() {
	var value = this._validateInput(this.getValue());
	if (value != null) {
		this.setValue(value);
		return true;
	} else {
		return false;
	}
};

/* Built-in validators */

/**
 * Validates a number.
 * 
 * @param	{string}	value		the value
 * @return	{boolean}	<code>true</code> if valid
 */
DwtInputField.validateNumber =
function(value) {
	var n = new Number(value);
	if (isNaN(n) || (Math.round(n) != n))
		throw AjxMsg.notAnInteger;
	return DwtInputField.validateFloat.call(this, value);
};

/**
 * Validates an integer.
 * 
 * @param	{string}	value		the value
 * @return	{boolean}	<code>true</code> if valid
 */
DwtInputField.validateInteger =
function(value) {
	var n = new Number(value);
	if (isNaN(n) || (Math.round(n) != n) || (n.toString() != value))
		throw AjxMsg.notAnInteger;
	if (this._minNumVal && value < this._minNumVal)
		throw AjxMessageFormat.format(AjxMsg.numberLessThanMin, this._minNumVal);
	if (this._maxNumVal && value > this._maxNumVal)
		throw AjxMessageFormat.format(AjxMsg.numberMoreThanMax, this._maxNumVal);
	return value;
};

/**
 * Validates a float.
 * 
 * @param	{string}	value		the value
 * @return	{boolean}	<code>true</code> if valid
 */
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

/**
 * Validates a string.
 * 
 * @param	{string}	value		the value
 * @return	{boolean}	<code>true</code> if valid
 */
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

/**
 * Validates a date.
 * 
 * @param	{string}	value		the value
 * @return	{boolean}	<code>true</code> if valid
 */
DwtInputField.validateDate = 
function(value) {
	if (this._required && value == "")
		throw AjxMsg.valueIsRequired;
	
	if (AjxDateUtil.simpleParseDateStr(value) == null) {
		throw AjxMsg.invalidDatetimeString;
	}

	return value;
};

/**
 * Validates an email.
 * 
 * @param	{string}	value		the value
 * @return	{boolean}	<code>true</code> if valid
 */
DwtInputField.validateEmail = function(value) {
	if (this._required && value == "")
		throw AjxMsg.valueIsRequired;
	if (!AjxEmailAddress.isValid(value))
		throw AjxMsg.invalidEmailAddr;
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

//
// Protected methods
//

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
	keyEv.setFromDhtmlEvent(ev, true);

	var obj = keyEv.dwtObj;
	var keyCode = keyEv.keyCode;
    if (obj.notifyListeners(DwtEvent.ONKEYUP, keyEv)) {
        return true;
    }

	// ENTER || TAB
	var val = null;
	if ((keyCode == 0x0D || keyCode == 0x09)
	    && obj._validationStyle == DwtInputField.ONEXIT_VALIDATION)
		val = obj._validateInput(obj.getValue());
	else if (obj._validationStyle == DwtInputField.CONTINUAL_VALIDATION)
		val = obj._validateInput(obj.getValue());

	if (val != null && val != obj.getValue())
		obj.setValue(val);

	return true;
};

DwtInputField._blurHdlr =
function(ev) {
	var obj = DwtControl.getTargetControl(ev);
	if (obj) {
		obj._hasFocus = false;
		if (obj._validationStyle == DwtInputField.ONEXIT_VALIDATION) {
			var val = obj._validateInput(obj.getValue());
			if (val != null) {
				obj.setValue(val);
			}
		}
		if (!obj._hintIsVisible && obj._hint) {
			obj._showHint();
		}
	}
	obj.notifyListeners(DwtEvent.ONBLUR, ev)
};

DwtInputField._focusHdlr =
function(ev) {
	var obj = DwtControl.getTargetControl(ev);
	if (obj) {
		var kbMgr = DwtShell.getShell(window).getKeyboardMgr().inputGotFocus(obj);
		if (obj._hintIsVisible) {
			obj._hideHint('');
		}
	}
	obj.notifyListeners(DwtEvent.ONFOCUS, ev)
};

DwtInputField._keyDownHdlr =
function(ev) {
    var obj = DwtControl.getTargetControl(ev);
    if (obj) {
        if (obj._hintIsVisible) {
            obj._hideHint('');
        }
    }
};

DwtInputField.prototype._hideHint = 
function(value) {
	if (!AjxEnv.supportsPlaceholder) {
		var element = this.getInputElement();
		element.value = value;
		element.title = this._hint || "";
		this._hintIsVisible = false;
		this._updateClassName();
	}
};

DwtInputField.prototype._showHint = 
function() {
	if (!AjxEnv.supportsPlaceholder && this._hint) {
		var element = this.getInputElement();
		if (!element.value) {
			this._hintIsVisible = true;
			this._updateClassName();
			element.title = "";
			element.value = this._hint;
		}
	}
};

DwtInputField.prototype._updateClassName = 
function() {
	var className;
	if (!this.getEnabled()) {
		className = this._disabledClassName;
	} else if (this._hasError) {
		if (this._hintIsVisible && !this._hasFocus) {
			className = this._errorHintClassName;
		} else {
			className = this._errorClassName;
		}
	} else if (this._hintIsVisible && !this._hasFocus) {
		className = this._hintClassName;
	} else {
		className = this._origClassName;
	}
	this.getHtmlElement().className = className;
};

DwtInputField.prototype._validateInput =
function(value) {
	var isValid = true;
	var retVal;
	this._validationError = null;

	if (!this.getEnabled()) {
		retVal = this.getValue();
	} else {
		try {
			if (typeof this._validator == "function") {
				retVal = value = this._validatorObj
					? this._validator.call(this._validatorObj, value, this)
					: this._validator(value);
			} else if (!this._validator.test(value)) {
				this._validationError = this._errorString;
			}
		} catch(ex) {
			if (typeof ex == "string")
				this._validationError = ex;
			else
				throw ex;
		}
	}
	
	if (this._validationError) {
		this._hasError = true;
		if (this._errorIconTd)
			this._errorIconTd.innerHTML = DwtInputField._ERROR_ICON_HTML;
		this.setToolTipContent(this._validationError);
		isValid = false;
		retVal = null;
	} else {
		this._hasError = false;
		if (this._errorIconTd)
			this._errorIconTd.innerHTML = DwtInputField._NOERROR_ICON_HTML;
		this.setToolTipContent(null);
		isValid = true;
	}
	this._updateClassName();

	if (this._validationCallback)
		this._validationCallback.run(this, isValid, value);

	return retVal;
};

/** 
 * Overriding default implementation in {@link DwtControl}.
 * 
 * @private
 */
DwtInputField.prototype._focusByMouseUpEvent =
function()  {
	if (this.getEnabled()) {
		this._hasFocus = true;
	}
};

/**
 * The input field inherits the id for accessibility purposes.
 *
 * @private
 */
DwtInputField.prototype._replaceElementHook =
function(oel, nel, inheritClass, inheritStyle) {
    nel = this.getInputElement();
    DwtControl.prototype._replaceElementHook.call(this, oel, nel, inheritClass, inheritStyle);
    if (oel.id) {
        nel.id = oel.id;
    }
	if (oel.size) {
		nel.size = oel.size;
	}
	if (oel.title) {
		this.setHint(oel.title);
	}
};

//
// Private methods
//


DwtInputField.prototype.__createInputEl =
function(params) {
	// clean up old input field if present
	var oinput = this._inputField;
	if (oinput) {
		for (var eventType in this._inputEventHandlers) {
			oinput.removeAttribute(eventType);
		}
	}

	// create new input field
	var ninput;
	var type = this._type != DwtInputField.PASSWORD ? "text" : "password";
	ninput = document.createElement("INPUT");
	ninput.type = type;
	this._inputField = ninput;

	// set common values
	var size = params ? params.size : oinput.size;
	var maxLen = params ? params.maxLen : oinput.maxLength;

	ninput.autocomplete = "off";
	if (size) {
		ninput.size = size;
	}
	if (maxLen) {
		ninput.maxLength = maxLen;
	}
	ninput.value = (params ? params.initialValue : oinput.value) || "";
	ninput.readonly = oinput ? oinput.readonly : false;
    if (params && params.inputId) {
        ninput.id = params.inputId;
    }

	if (AjxEnv.supportsPlaceholder && this._hint) {
		ninput.placeholder = this._hint;
	}

	// add event handlers
	ninput.onkeyup = DwtInputField._keyUpHdlr;
	ninput.onblur = DwtInputField._blurHdlr;
	ninput.onfocus = DwtInputField._focusHdlr;
    ninput.onkeydown = DwtInputField._keyDownHdlr;
	for (var eventType in this._inputEventHandlers) {
		ninput[eventType] = this._inputEventHandlers[eventType];
	}

	this._tabGroup.removeAllMembers();
	this._tabGroup.addMember(ninput);
	return ninput;
};

/*
 * clears the onFocus handler
 */
DwtInputField.prototype.disableFocusHdlr =
function() {
    this._inputField.onfocus = null;
};

/*
 * enables the onFocus handler
 */
DwtInputField.prototype.enableFocusHdlr =
function(){
    this._inputField.onfocus = DwtInputField._focusHdlr;
};

/*
 * enables the onKeyDown handler
 * bug fix # 80423 - Firefox loses the handler
 */
DwtInputField.prototype.enableKeyDownHdlr =
function() {
    this._inputField.onkeydown = DwtInputField._keyDownHdlr;
};

DwtInputField.prototype.moveCursorToEnd =
function() {
	Dwt.moveCursorToEnd(this._inputField);
};
