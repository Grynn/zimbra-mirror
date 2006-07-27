/*
 * Copyright (C) 2006, The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
 * @param {DwtComposite} parent Parent widget. Except in the case of <i>DwtShell</i> the
 * 		parent will be a control that has subclassed from <i>DwtComposite</i>
 * @param {Int} style The label style. May be one of: <i>DwtLabel.IMAGE_LEFT</i> 
 * 		or <i>DwtLabel.IMAGE_RIGHT</i> arithimatically or'd (|) with  one of:
 * 		<i>DwtLabel.ALIGN_LEFT</i>, <i>DwtLabel.ALIGN_CENTER</i>, or <i>DwtLabel.ALIGN_LEFT</i>
 * 		The first determines were in the label the icon will appear (if one is set), the second
 * 		determine how the content of the label will be aligned. The default value for
 * 		this parameter is: <code>DwtLabel.IMAGE_LEFT | DwtLabel.ALIGN_CENTER</code>
 * @param {String} className CSS class. If not provided defaults to the class name (optional)
 * @param {String} posStyle Positioning style (absolute, static, or relative). If
 * 		not provided defaults to <i>DwtControl.STATIC_STYLE</i> (optional)
 * @param {int} id An explicit ID to use for the control's HTML element. If not
 * 		specified defaults to an auto-generated id (optional)
 * @param {int} index index at which to add this control among parent's children (optional)
 * 
 * @see DwtButton
 * 
 * @extends DwtControl
 * 
 * @requires DwtControl
 */
function DwtLabel(parent, style, className, posStyle, id, index) {
	if (arguments.length == 0) return;
	className = className ? className : "DwtLabel";
	DwtControl.call(this, parent, className, posStyle, false, id, index);

	/**The label's style. See the constructor documentation for more info
	 * @type Int*/
	this._style = style ? style : (DwtLabel.IMAGE_LEFT | DwtLabel.ALIGN_CENTER);
	
	/**The table row with the label content (i.e. text and image).
	 * @type tr*/	
	this._row = null;
	
	/**The table cell containing the label text
	 * @type td*/
	this._textCell = null;
	
	/**The label text's background color
	 * @type String*/
	this._textBackground = null;
	
	/**The label text's foreground color
	 * @type String*/
	this._textForeground = null;

	/**The image cell containing the label icon
	 * @type td*/
	this._imageCell = null;

	this.__createTable();
	//MOW:  this.setCursor("default");
}

DwtLabel.prototype = new DwtControl;
DwtLabel.prototype.constructor = DwtLabel;

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

// Public methods
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
		if (enabled) {
			this.__setImage(this.__imageInfo);
			if (this._textCell != null)
				this._textCell.className = "Text";
		} else {
			if (this.__disabledImageInfo)
				this.__setImage(this.__disabledImageInfo);
			if (this._textCell)
				this._textCell.className = "DisabledText";
		}
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
	if (this._enabled || (!this._enabled && this.__disabledImageInfo))
		this.__setImage(imageInfo);
}

/**
* Returns the disabled image. If the label is currently disabled, its image is updated.
*
* @param imageSrc	the disabled image
*/
DwtLabel.prototype.setDisabledImage =
function(imageInfo) {
	this.__disabledImageInfo = imageInfo;
	if (!this._enabled && imageInfo)
		this.__setImage(imageInfo);
}

/**
* Returns the label text.
*/
DwtLabel.prototype.getText =
function() {
	return (this.__text != null) ? this.__text.data : null;
}

/**
* Sets the label text, and manages its placement and display.
*
* @param text	the new label text
*/
DwtLabel.prototype.setText =
function(text) {
	if (text == null || text == "") {
		if (this._textCell != null) {
			var cellIndex = Dwt.getCellIndex(this._textCell);
			this._row.deleteCell(cellIndex);
		}
	} else {
		if (this.__text == null) {
		  this.__text = document.createTextNode(text);
		}
		this.__text.data = text;
		var idx;
		if (this._textCell == null) {
			if (this._style & DwtLabel.IMAGE_RIGHT) {
				idx = 0;
			} else {
				idx = (this._imageCell != null) ? 1 : 0;
			}
			this._textCell = this._row.insertCell(idx);
			this._textCell.className = this._enabled ? "Text" : "DisabledText";
			if (this._textBackground)
				this._textCell.style.backgroundColor = this._textBackground;
			if (this._textForeground)
				this._textCell.style.color = this._textForeground;
			this.__doAlign();
			this._textCell.noWrap = true;
			this._textCell.style.verticalAlign = "middle";
//			this._textCell.style.width = "auto";
			this._textCell.appendChild(this.__text);
		}
	}
}

DwtLabel.prototype.setTextBackground =
function(color) {
	this._textBackground = color;
	if (this._textCell)
		this._textCell.style.backgroundColor = color;
}

DwtLabel.prototype.setTextForeground =
function(color) {
	this._textForeground = color;
	if (this._textCell)
		this._textCell.style.color = color;
}


DwtLabel.prototype.setAlign =
function(alignStyle) {
	this._style = alignStyle;
	
	// reset dom since alignment style may have changed
	if (this._textCell) {
		this._row.removeChild(this._textCell);
		this._textCell = null;
		this.setText(this.__text.data)
	}
	if (this._imageCell) {
		this._row.removeChild(this._imageCell);
		this._imageCell = null;
		this.__setImage(this.__imageInfo);
	}
}

// Private methods

/**@private*/
DwtLabel.prototype.__createTable =
function() {
	this.__table = document.createElement("table");
	this.__table.border = 0;
	
	// Left is the default alignment. Note that if we do an explicit align left, Firefox freaks out
	if (this._style & DwtLabel.ALIGN_RIGHT)
		this.__table.align = "right";
	else if (!(this._style & DwtLabel.ALIGN_LEFT)) {
		this.__table.align = "center";
		this.__table.width = AjxEnv.isIE ? "95%" : "100%"; // 95% is a hack to deal with border-collapsed tables in IE. Bug 9148.
	}

	this._row = this.__table.insertRow(0);
	this.getHtmlElement().appendChild(this.__table);
};


/**Set the label's image, and manage its placement.
 * @private*/
DwtLabel.prototype.__setImage =
function(imageInfo) {
	if (!imageInfo) {
		if (this._imageCell) {
			var cellIndex = Dwt.getCellIndex(this._imageCell);
			this._row.deleteCell(cellIndex);
			this._imageCell = null;
		}
	} else {
		var idx;
		if (!this._imageCell) {
			if (this._style & DwtLabel.IMAGE_LEFT) {
				idx = 0;
			} else {
				idx = this._textCell ? 1 : 0;
			}
			this._imageCell = this._row.insertCell(idx);
			this._imageCell.className = "Icon";
			this.__doAlign();
		}
		AjxImg.setImage(this._imageCell, imageInfo);
	}	
}

/** Handle the alignment style.
 * @private*/
DwtLabel.prototype.__doAlign =
function() {
	if (this._style & DwtLabel.ALIGN_CENTER) {
		if (this._imageCell != null && this._textCell != null) {
			// XXX: this doesnt seem right (no pun intended)
			if (this._style & DwtLabel.IMAGE_LEFT) {
				this._imageCell.align = "right";
				this._textCell.align = "left";
			} else {
				this._imageCell.align = "left";
				this._textCell.align = "right";
			}
		} else if (this._imageCell != null) {
			this._imageCell.align = "center";
		} else if (this._textCell != null) {
			this._textCell.align = "center";
		}
	}
}
