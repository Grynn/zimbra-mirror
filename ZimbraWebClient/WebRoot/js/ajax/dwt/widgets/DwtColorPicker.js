/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2005, 2006, 2007, 2008, 2009, 2010, 2011 VMware, Inc.
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
 * Creates a color picker displaying "Web safe" colors.
 * @constructor
 * @class
 * Instances of this class may be
 * used with {@link DwtMenu} to create a {@link DwtColorPicker} menu. Clicking on a color cell generates a
 * DwtSelectionEvent the detail attribute of which contains the color string associated
 * the cell on which the user clicked
 *
 *
 * @author Ross Dargahi
 * 
 * @param {hash}	params		a hash of parameters
 * @param {DwtComposite} params.parent		the parent widget
 * @param {string}       params.className	a CSS class
 * @param {constant}     params.posStyle	the positioning style
 * @param {boolean}      params.hideNoFill  True to hide the no-fill/use-default option
 * @param {string}       params.noFillLabel			the no-fill label
 * @param {boolean}      params.allowColorInput		if <code>true</code>, allow a text field to allow user to input their customized RGB value
 * @param {string}       params.defaultColor Default color.
 * 
 * @extends		DwtControl
 */
DwtColorPicker = function(params) {
	if (arguments.length == 0) return;
    params = Dwt.getParams(arguments, DwtColorPicker.PARAMS);

	params.className = params.className || "DwtColorPicker";
	DwtComposite.call(this, params);

    this._hideNoFill = params.hideNoFill;
	this._noFillLabel = params.noFillLabel;
    this._allowColorInput = params.allowColorInput;
    this._defaultColor = params.defaultColor || "#000000";
    this._createHtml();
};

DwtColorPicker.prototype = new DwtComposite;
DwtColorPicker.prototype.constructor = DwtColorPicker;

DwtColorPicker.prototype.toString = function() {
	return "DwtColorPicker";
};

DwtColorPicker.PARAMS = ["parent", "className", "posStyle", "noFillLabel", "allowColorInput", "defaultColor"];

//
// Constants
//

// RE to parse out components out of a "rgb(r, g, b);" string
DwtColorPicker._RGB_RE = /^rgb\(([0-9]{1,3}),\s*([0-9]{1,3}),\s*([0-9]{1,3})\)$/;
DwtColorPicker._HEX_RE = /^\#([0-9a-f]{2})([0-9a-f]{2})([0-9a-f]{2})$/i;

//
// Data
//

DwtColorPicker.prototype.TEMPLATE = "dwt.Widgets#DwtColorPicker";

//
// Public methods
//

/**
 * Adds a listener to be notified when the button is pressed.
 *
 * @param {AjxListener}	listener	a listener
 */
DwtColorPicker.prototype.addSelectionListener = 
function(listener) {
	this.addListener(DwtEvent.SELECTION, listener);
};

/**
 * Removes a selection listener.
 *
 * @param {AjxListener}	listener	a listener
 */
DwtColorPicker.prototype.removeSelectionListener = 
function(listener) { 
	this.removeListener(DwtEvent.SELECTION, listener);
};

DwtColorPicker.prototype.dispose = 
function () {
	if (this._disposed) { return; }
	DwtControl.prototype.dispose.call(this);
};

DwtColorPicker.prototype._createHtml = function(templateId) {
    this._createHtmlFromTemplate(templateId||this.TEMPLATE, {id:this.getHtmlElement().id});
};

DwtColorPicker.prototype._createHtmlFromTemplate = function(templateId, data) {
    data.allowColorInput = this._allowColorInput;
    data.hideNoFill = this._hideNoFill;
    data.noFillLabel = this._noFillLabel;
    DwtComposite.prototype._createHtmlFromTemplate.apply(this, arguments);

    // create controls
    if (data.allowColorInput) {
        var inputEl = document.getElementById(data.id+"_input");
	var inputParams = {
		parent: this,
		validationStyle: DwtInputField.CONTINUAL_VALIDATION, //update the preview for each key up 
		errorIconStyle: DwtInputField.ERROR_ICON_RIGHT, 
		validator: DwtColorPicker.__isValidInputValue
	};
        var input = this._colorInput = new DwtInputField(inputParams);
        input.replaceElement(inputEl);
	// Add  callback for update the preview when the input value is validated.
	var updateCallback = new AjxCallback(this, this._updatePreview);
	input.setValidationCallback(updateCallback);
	
	var error = this._error = new DwtLabel({parent:this});
	var errorEl = document.getElementById(data.id+"_error");
        error.replaceElement(errorEl);
        error.setVisible(false);
        
	this._preview = document.getElementById(data.id+"_preview");
        
	var buttonEl = document.getElementById(data.id+"_button");
        var button = new DwtButton({parent:this});
        button.setText(AjxMsg.setColor);
        button.replaceElement(buttonEl);
        button.addSelectionListener(new AjxListener(this, this._handleSetColor));
    }

    var buttonEl = document.getElementById(data.id+"_default");
    if (buttonEl) {
        if (!DwtColorPicker.Button) {
            DwtColorPicker.__defineClasses();
        }
        var button = this._defaultColorButton = new DwtColorPicker.Button({parent:this});
        button.setText(data.noFillLabel || AjxMsg.colorsUseDefault);
        button.replaceElement(buttonEl);
        button.addSelectionListener(new AjxListener(this, this._handleColorSelect, [0]));
    }

    // set color handlers
    var colorsEl = document.getElementById(data.id+"_colors");
    var mouseOver = AjxEnv.isIE ? DwtEvent.ONMOUSEENTER : DwtEvent.ONMOUSEOVER;
    var mouseOut  = AjxEnv.isIE ? DwtEvent.ONMOUSELEAVE : DwtEvent.ONMOUSEOUT;

    Dwt.setHandler(colorsEl, DwtEvent.ONMOUSEDOWN, AjxCallback.simpleClosure(this._handleMouseDown, this));
    Dwt.setHandler(colorsEl, DwtEvent.ONMOUSEUP, AjxCallback.simpleClosure(this._handleMouseUp, this));
    Dwt.setHandler(colorsEl, mouseOver, AjxCallback.simpleClosure(this._handleMouseOver, this));
    Dwt.setHandler(colorsEl, mouseOut, AjxCallback.simpleClosure(this._handleMouseOut, this));
};

DwtColorPicker.prototype._handleMouseOver = function(htmlEvent) {
    var event = DwtUiEvent.getEvent(htmlEvent);
    var target = DwtUiEvent.getTarget(event);
    if (!Dwt.hasClass(target, "Color")) return;

    this._handleMouseOut(htmlEvent);
    Dwt.addClass(target, DwtControl.HOVER);
    this._mouseOverEl = target;
};

DwtColorPicker.prototype._handleMouseOut = function(htmlEvent) {
    if (this._mouseOverEl) {
        Dwt.delClass(this._mouseOverEl, DwtControl.HOVER);
    }
    this._mouseOverEl = null;
};

DwtColorPicker.prototype._handleMouseDown = function(htmlEvent) {
    var event = DwtUiEvent.getEvent(htmlEvent);
    var target = DwtUiEvent.getTarget(event);
    this._mouseDownEl = Dwt.hasClass(target, "Color") ? target : null;
};
DwtColorPicker.prototype._handleMouseUp = function(htmlEvent) {
    var event = DwtUiEvent.getEvent(htmlEvent);
    var target = DwtUiEvent.getTarget(event);
    if (this._mouseDownEl != target) return;

    var cssColor = DwtCssStyle.getProperty(target, "background-color");
    this._handleColorSelect(DwtColorPicker.__color2hex(cssColor));
};

DwtColorPicker.prototype._handleSetColor = function(evt) {
    var color = this._colorInput.getValue();
    if (color) {
	color = DwtColorPicker.__color2hex(color);
	if(!color) 
		return; 
    	this._handleColorSelect(color);
    }
};

DwtColorPicker.prototype._handleColorSelect = function(color) {
    this._inputColor = color;

    // If our parent is a menu then we need to have it close
    if (this.parent instanceof DwtMenu) {
        DwtMenu.closeActiveMenu();
    }

    // Call Listeners on mouseEv.target.id
    if (this.isListenerRegistered(DwtEvent.SELECTION)) {
        var selEvent = DwtShell.selectionEvent;
//        DwtUiEvent.copy(selEvent, htmlEvent);
        selEvent.item = this;
        selEvent.detail = this._inputColor;
        this.notifyListeners(DwtEvent.SELECTION, selEvent);
    }
};

/**
 * Gets the input color.
 * 
 * @return	{string}	the color (in hex) from the input color field
 */
DwtColorPicker.prototype.getInputColor = function () {
    return this._inputColor;
};

DwtColorPicker.prototype.setDefaultColor = function (color) {
    if(this._defaultColorButton) {
        this._defaultColorButton.setDefaultColor(color);
    }
};

DwtColorPicker.__color2hex = function(s) {
	//in IE we can't get the calculated value so for white/black we get white/black (of course it could be set the the hex value in the markup but this is more bulletproof to make sure here)
	if (s == "white") {
		return "#FFFFFF";
	}
	if (s == "black") {
		return "#000000";
	}

    var m = s && s.match(DwtColorPicker._RGB_RE);
    if (m) {
	// each component should be in range of (0 - 255)
	for( var i = 1; i <= 3; i++ ) {
		if(parseInt(m[i]) > 255)
			return "";
	}
        return AjxColor.color(m[1], m[2], m[3]);
    }
    m = s && s.match(DwtColorPicker._HEX_RE);
    if (m) {
        return s;
    }
    return "";
};

DwtColorPicker.__isValidInputValue = function(s) {
   // null is valid for we consider the condition
   // the user delete all the word it has been input
   if (!s)
	return s;
   var r = DwtColorPicker.__color2hex(s);
   if (!r) { 
	throw AjxMsg.colorFormatError;	
   }
   return s;	
};

DwtColorPicker.prototype._updatePreview = function(inputelement, isValid, value){
   if (isValid) {
	value = DwtColorPicker.__color2hex(value);
	Dwt.setVisible(this._preview, true);
	this._preview.style.backgroundColor = value;
	this._error.setVisible(false);
   }
   else {
	Dwt.setVisible(this._preview, false);
	this._error.setVisible(true);
	this._error.setText(AjxMsg.colorFormatError);
   }
};
//
// Classes
//

DwtColorPicker.__defineClasses = function() {
    // HACK: This defines the custom button after the color picker has
    // HACK: been initialized and instantiated so that we dont' get
    // HACK: weird dependency issues. (I noticed this in particular
    // HACK: in the admin client.)
    DwtColorPicker.Button = function(params) {
        params.className = params.className || "DwtColorPickerButton";
        DwtButton.call(this, params);
        this._colorDiv = document.getElementById(this.getHtmlElement().id+"_color");
    };
    DwtColorPicker.Button.prototype = new DwtButton;
    DwtColorPicker.Button.prototype.constructor = DwtColorPicker.Button;

    DwtColorPicker.Button.prototype.setDefaultColor = function(color) {
        this._colorDiv.style.backgroundColor = color;
    };

    DwtColorPicker.Button.prototype.toString = function() {
        return "DwtColorPickerButton";
    };

    DwtColorPicker.Button.prototype.TEMPLATE = "dwt.Widgets#DwtColorPickerButton";
};
