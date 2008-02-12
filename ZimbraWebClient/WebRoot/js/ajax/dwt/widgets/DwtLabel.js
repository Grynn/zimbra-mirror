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
 * This class implements a label, which consists of an image and/or text. It is used
 * both as a concrete class and as the base class for <i>DwtButton</i>. The label's 
 * components are managed within a table. The label can be enabled or disabled, which are reflected in 
 * its display. A disabled label looks greyed out.
 * 
 * <h4>CSS</h4>
 * <i>.className</i> table - The label's table
 * <i>.className</i> .Icon - class name for the icon image cell
 * <i>.className</i> .Text - enabled text cell 
 * <i>.className</i> .DisabledText - disabled text cell
 * 
 * <h4>Keyboard Actions</h4>
 * None
 * 
 * <h4>Events</h4>
 * None
 * 
 * @author Ross Dargahi
 * 
 * @param params		[hash]				hash of params:
 *        parent		[DwtComposite] 		parent widget
 *        style			[constant]*			label style: May be one of: <i>DwtLabel.IMAGE_LEFT</i> 
 * 											or <i>DwtLabel.IMAGE_RIGHT</i> arithmetically or'd (|) with  one of:
 * 											<i>DwtLabel.ALIGN_LEFT</i>, <i>DwtLabel.ALIGN_CENTER</i>, or <i>DwtLabel.ALIGN_LEFT</i>
 * 											The first determines were in the label the icon will appear (if one is set), the second
 * 											determine how the content of the label will be aligned. The default value for
 * 											this parameter is: <code>DwtLabel.IMAGE_LEFT | DwtLabel.ALIGN_CENTER</code>
 *        className		[string]*			CSS class
 *        posStyle		[constant]*			positioning style
 *        id			[string]*			ID to use for the control's HTML element
 *        index 		[int]*				index at which to add this control among parent's children 
 */
DwtLabel = function(params) {
	if (arguments.length == 0) { return; }
	params = Dwt.getParams(arguments, DwtLabel.PARAMS);
	
	params.className = params.className || "DwtLabel";
	DwtControl.call(this, params);

	/**The label's style. See the constructor documentation for more info
	 * @type Int*/
	this._style = params.style || (DwtLabel.IMAGE_LEFT | DwtLabel.ALIGN_CENTER);
	
	/**The label text's background color
	 * @type String*/
	this._textBackground = null;
	
	/**The label text's foreground color
	 * @type String*/
	this._textForeground = null;

    this._createHtml();
    //MOW:  this.setCursor("default");
}

DwtLabel.PARAMS = ["parent", "style", "className", "posStyle", "id", "index"];

DwtLabel.prototype = new DwtControl;
DwtLabel.prototype.constructor = DwtLabel;

/**
 * This method returns the class name for the control.
 *
 * @return class name
 * @type String
 */
DwtLabel.prototype.toString =
function() {
	return "DwtLabel";
}

//
// Constants
//

// display styles
/** Align image to the left of text, if both present
 * @type Int*/
DwtLabel.IMAGE_LEFT = 1;

/** Align image to the right of text, if both present
 * @type Int*/
DwtLabel.IMAGE_RIGHT = 2;

/** Align the label to the left
 * @type Int*/
DwtLabel.ALIGN_LEFT = 4;

/** Align the label to the right
 * @type Int*/
DwtLabel.ALIGN_RIGHT = 8;

/** Align the label to the center
 * @type Int*/
DwtLabel.ALIGN_CENTER = 16;

/** The last label style. Used by subclasses when adding styles
 * @type Int*/
DwtLabel._LAST_STYLE = 16;

//
// Data
//

DwtLabel.prototype.TEMPLATE = "dwt.Widgets#ZLabel";

//
// Public methods
//

DwtLabel.prototype.dispose =
function() {
	delete this._dropDownEl;
	delete this._iconEl;
	delete this._textEl;
	DwtControl.prototype.dispose.call(this);
};

/**
 * Sets the enabled/disabled state of the label. A disabled label may have a different
 * image, and greyed out text. This method overrides <code>DwtControl.setEnabled</code>
 *
 * @param {Boolean} enabled True set the label as enabled
 */
DwtLabel.prototype.setEnabled =
function(enabled) {
	if (enabled != this._enabled) {
		DwtControl.prototype.setEnabled.call(this, enabled);
		this.__setImage(this.__imageInfo);
	}
}

/**
* Returns the current Image Info.
*/
DwtLabel.prototype.getImage =
function() {
	return this.__imageInfo;
}

/**
* Sets the main (enabled) image. If the label is currently enabled, its image is updated.
*/
DwtLabel.prototype.setImage =
function(imageInfo) {
	this.__imageInfo = imageInfo;
	this.__setImage(imageInfo);
}

/**
* Returns the disabled image. If the label is currently disabled, its image is updated.
*
* @param imageSrc	the disabled image
*/
DwtLabel.prototype.setDisabledImage =
function(imageInfo) {
	// DEPRECATED -- we no longer support different images for disabled.
	//	See __setImage() for details.
}

/**
* Returns the label text.
*/
DwtLabel.prototype.getText =
function() {
	return (this.__text != null) ? this.__text : null;
}

/**
* Sets the label text, and manages its placement and display.
*
* @param text	the new label text
*/
DwtLabel.prototype.setText =
function(text) {
    if (!this._textEl) return;

    if (text == null || text == "") {
        this.__text = null;
        this._textEl.innerHTML = "";
    }
    else {
		this.__text = text;
        this._textEl.innerHTML = text;
    }
}

DwtLabel.prototype.setTextBackground =
function(color) {
	this._textBackground = color;
    if (this._textEl) {
        this._textEl.style.backgroundColor = color;
    }
}

DwtLabel.prototype.setTextForeground =
function(color) {
	this._textForeground = color;
    if (this._textEl) {
		this._textEl.style.color = color;
    }
}


DwtLabel.prototype.setAlign =
function(alignStyle) {
	this._style = alignStyle;

	// reset dom since alignment style may have changed
    this.__setImage(this.__imageInfo);
}

DwtLabel.prototype.isStyle = function(style) {
    return this._style & style;
};

//
// Protected methods
//

DwtLabel.prototype._createHtml = function(templateId) {
    var data = { id: this._htmlElId };
    this._createHtmlFromTemplate(templateId || this.TEMPLATE, data);
};

DwtLabel.prototype._createHtmlFromTemplate = function(templateId, data) {
    DwtControl.prototype._createHtmlFromTemplate.call(this, templateId, data);
    this._textEl = document.getElementById(data.id+"_title");
};


DwtLabel.prototype._getIconEl = function() {
	// MOW: getting the proper icon element on demand rather than all the time for speed
	var direction = (this._style & DwtLabel.IMAGE_RIGHT ? "right" : "left");
	return this._iconEl || 
			(this._iconEl = document.getElementById(this._htmlElId+"_"+direction+"_icon"));
}

//
// Private methods
//

/**Set the label's image, and manage its placement.
 * @private*/
DwtLabel.prototype.__setImage =
function(imageInfo) {
	var iconEl = this._getIconEl();
    if (iconEl) {
    	AjxImg.setImage(iconEl, imageInfo, null, !this._enabled);

		// set a ZHasRightIcon or ZHasLeftIcon on the outer element, depending on which we set
    	var elementClass = (this._style & DwtLabel.IMAGE_RIGHT ? "ZHasRightIcon" : "ZHasLeftIcon");
		Dwt.addClass(this.getHtmlElement(), elementClass);
    }
}
