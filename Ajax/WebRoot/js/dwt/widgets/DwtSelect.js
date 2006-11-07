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
 * @class DwtSelect
 * @constructor
 * Widget to replace the native select element.
 *
 * Note: Currently this does not support multiple selection.
 * @param options (Array) optional array of options. This can be either
 *                        an array of DwtSelectOptions or an array of strings.
 */
function DwtSelect(parent, options, className, posStyle) {
    var clsName = className || "DwtSelectComposite";
    var positionStyle = posStyle || Dwt.STATIC_STYLE;
    DwtComposite.call(this, parent, clsName, positionStyle);
	this._origClassName = this._className;
	this._heightClassName =  this._className + "Height";
	this._menuClassName =  this._className + "Menu";
	
	this._menuTableId = false;

    // initialize some variables
    this._currentSelectionId = -1;
    this._options = new AjxVector();
    this._optionValuesToIndices = new Object();
    this._selectedValue = this._selectedOption = null;
	this.disabled = false;
	this._menuListenerObject = new AjxListener(this, this._menuListener);

    this._render(options);
}

DwtSelect.prototype = new DwtComposite;
DwtSelect.prototype.constructor = DwtSelect;

DwtSelect.prototype.toString = 
function() {
    return "DwtSelect";
};


DwtSelect.prototype.getButton = 
function() {
	return this._button;
};

DwtSelect.prototype.setText = 
function(text) {
	this._button.setText(text);
};

DwtSelect.prototype.setToolTipContent = 
function(text) {
	this._button.setToolTipContent(text);
};

DwtSelect.prototype.getToolTipContent = 
function() {
	return this._button.getToolTipContent();
};

DwtSelect.prototype.setImage = 
function(image) {
	this._button.setImage(image);
};

DwtSelect.prototype.setAlign =
function(align) {
	this._button.setAlign(align);
};

// -----------------------------------------------------------
// static attributes
// -----------------------------------------------------------
/** This keeps track of all instances out there **/
DwtSelect._objectIds = [null];

// -----------------------------------------------------------
// instance tracking methods
// -----------------------------------------------------------
DwtSelect._assignId = 
function(anObject) {
    var myId = DwtSelect._objectIds.length;
    DwtSelect._objectIds[myId]= anObject;
    return myId;
};

DwtSelect._getObjectWithId = 
function(anId) {
    return DwtSelect._objectIds[anId];
};

DwtSelect._unassignId = 
function(anId) {
    DwtSelect._objectIds[anId] = null;
};

DwtSelect.getObjectFromElement = 
function(element) {
	return element && element.dwtObj 
		? AjxCore.objectWithId(element.dwtObj) : null
};

DwtSelect.prototype.dispose = 
function() {
	DwtControl.prototype.dispose.call(this);
	if (this._internalObjectId)
		DwtSelect._unassignId(this._internalObjectId);
};

// -----------------------------------------------------------
// rendering methods
// -----------------------------------------------------------

DwtSelect.prototype._render = 
function(options) {
	var buttonRowId = Dwt.getNextId();
	this._menuTableId = Dwt.getNextId();
	var selectedValue = this._selectedValue ? this._selectedValue : "";
	var html = [
			  "<div class='", this._heightClassName, "'style='overflow:hidden;'>",
			   "<table border=0 cellpadding=0 cellspacing=0>",
			    "<tr><td id='", buttonRowId, "'></td></tr>",
			    "<tr><td>",
			     "<table id='", this._menuTableId, "' class='", this._menuClassName, "' border=0 cellpadding=0 cellspacing=0>",
				 "</table>",
			    "</td></tr> ",
			   "</table>",
			  "</div>"];
	
	var element = this.getHtmlElement();
	element.innerHTML = html.join("");
	
	// Insert the button.
	this._button = new DwtButton(this, DwtLabel.ALIGN_LEFT);
	this._button.reparentHtmlElement(buttonRowId);
	this._button.setDropDownImages(	"SelectPullDownArrow",				// normal
									"SelectPullDownArrowDis",			// disabled
									"SelectPullDownArrowHover",			// hover
								   	"SelectPullDownArrowSel");			// down
	this._button.getHtmlElement().style.minWidth = 0;
	this._button.setMenu(this._menuListenerObject, false);
	
	// Fill the list of options.
    if (options) {
        for (var i = 0; i < options.length; ++i) {
            this.addOption(options[i]);
        }
    }
};


// -----------------------------------------------------------
// public api methods
// -----------------------------------------------------------
/**
 * @param option (String or DwtSelectOption ) -- string for the option value
 *                                               or the option object.
 * @param selected (boolen) -- optional argument indicating whether
 *                             the newly added option should be
 *                             set as the selected option.
 * @param value (var) -- if the option parameter is a DwtSelectOption, this 
 *                       will override the value already set in the option.
 * @return integer -- A handle to the option added. The handle
 *                    can be used in other api methods.
 */
DwtSelect.prototype.addOption = 
function(option, selected, value) {
	var opt = null;
	var val = null;
	if (typeof(option) == 'string') {
		val = value != null ? value : option;
		opt = new DwtSelectOption(val, selected, option, this, null, null);
	} else {
		if (option instanceof DwtSelectOption) {
			opt = option;
			if (value)
				opt.setValue(value);
			selected = opt.isSelected();
		} else if(option instanceof DwtSelectOptionData || option.value) {
			val = value != null ? value : option.value;
			opt = new DwtSelectOption(val, option.isSelected, option.displayValue, this, null, null, option.selectedValue);
			selected = Boolean(option.isSelected);
		} else {
			return -1;
		}
	}

	this._options.add(opt);
	if (this._options.size() == 1 || selected)
		this._setSelectedOption(opt);

	// Insert the option into the table that's below the button.
	// This is what gives the button the same size as the select menu.
	var table = document.getElementById(this._menuTableId);
	var row = table.insertRow(-1);
	var cell = row.insertCell(-1);
	cell.className = 'DwtMenuItem';
	cell.innerHTML = ["<div class='Text'>", AjxStringUtil.htmlEncode(opt.getDisplayValue()), "</div>"].join("");

	// Register listener to create new menu.	
	this._button.setMenu(this._menuListenerObject, false);

    // return the index of the option.
    this._optionValuesToIndices[opt.getValue()] = this._options.size() - 1;
    return (this._options.size() - 1);
};

/**
 * Renames an option.
 * 
 * @param value	{object} value the value of the option to rename
 * @param name	{string} name the new name
 */
DwtSelect.prototype.rename =
function(value, name) {
	var option = this.getOptionWithValue(value);
	option._displayValue = name;

	if (this.__selectedOption && (this.__selectedOption._value == value))	{
		this.setText(name);
	}
	
	// Register listener to create new menu.	
	this._button.setMenu(this._menuListenerObject, false);
};

DwtSelect.prototype.clearOptions = 
function() {
	var opts = this._options.getArray();
	for (var i = 0; i < opts.length; ++i) {
		opts[i] = null;
	}
	this._options.removeAll();
	this._optionValuesToIndices = null;
	this._optionValuesToIndices = new Array();
	this._selectedValue = null;
	this._selectedOption = null;
	this._currentSelectionId = -1;
};

DwtSelect.prototype.setName = 
function(name) {
	this._name = name;
};

DwtSelect.prototype.getName = 
function() {
	return this._name;
};

/**
 * @return The enabled state of the control
 * @type Boolean
 * 
 * @see #setEnabled
 */
DwtSelect.prototype.getEnabled =
function() {
	return this._button.getEnabled();
};

/**
 * Sets the control's enabled state. If <code>setHtmlElement</code> is true, then 
 * this method will also set the control's html element disabled attribute
 * 
 * @param {Boolean} enabled true the control is enabled
 * @param {Boolean} setHtmlElement true, then set the control's html element 
 * 		disabled attribute (optional)
 */
DwtSelect.prototype.setEnabled =
function(enabled, setHtmlElement) {
	this._button.setEnabled(enabled, setHtmlElement);
};

DwtSelect.prototype.disable = 
function() {
	this._button.setEnabled(false);
};

DwtSelect.prototype.enable = 
function() {
	this._button.setEnabled(true);
};

DwtSelect.prototype._disableSelectionIE = 
function() {
	return false;
};

DwtSelect.prototype._disableSelection = 
function() {
	var func = function() {
		window.getSelection().removeAllRanges();
	};
	window.setTimeout(func, 5);
};

DwtSelect.prototype.setSelectedValue = 
function(optionValue) {
    var index = this._optionValuesToIndices[optionValue];
    if ((index !== void 0) && (index !== null)) {
        this.setSelected(index);
    }
};

/**
 * Sets the option as the selected option.
 * @param optionHandle (integer) -- handle returned from addOption
 */
DwtSelect.prototype.setSelected = 
function(optionHandle) {
    var optionObj = this.getOptionWithHandle(optionHandle);
	this.setSelectedOption(optionObj);
};

DwtSelect.prototype.getOptionWithHandle = 
function(optionHandle) {
	return this._options.get(optionHandle);
};

DwtSelect.prototype.getIndexForValue = 
function(value) {
	return this._optionValuesToIndices[value];
};

DwtSelect.prototype.getOptionWithValue = 
function(optionValue) {
	var index = this._optionValuesToIndices[optionValue];
	var option = null;
    if ((index !== void 0) && ( index !== null)) {
        option = this.getOptionWithHandle(index);
    }
	return option;
};

DwtSelect.prototype.setSelectedOption = 
function(optionObj) {
	if (optionObj)
		this._setSelectedOption(optionObj);
};

DwtSelect.prototype.getValue = 
function() {
    return this._selectedValue;
};

DwtSelect.prototype.getSelectedOption = 
function() {
	return this._selectedOption;
};

DwtSelect.prototype.getSelectedIndex =
function() {
	return this.getIndexForValue(this.getValue());
};

DwtSelect.prototype.getWidth = 
function() {
	return DwtControl.prototype.getSize.call(this).x;
};

DwtSelect.prototype.addChangeListener = 
function(listener) {
    this.addListener(DwtEvent.ONCHANGE, listener);
};


// -----------------------------------------------------------
// public interface for DwtSelectOption
// -----------------------------------------------------------

DwtSelect.prototype.size = 
function() {
	return this._options.size();
}

// --------------------------------------------------------------------
// private methods
// --------------------------------------------------------------------

DwtSelect.prototype._menuListener =
function() {
	var menu = new DwtMenu(this, DwtMenu.DROPDOWN_STYLE, "DwtSelectMenu", null, true);
	this._button.setMenu(menu, true);
	menu.setAssociatedObj(this);
    for (var i = 0, len = this._options.size(); i < len; ++i) {
		var mi = new DwtMenuItem(menu, DwtMenuItem.SELECT_STYLE);
		var option = this._options.get(i);
		var text = option.getDisplayValue();
		if (text) {
			mi.setText(text);
		}
		var image = option.getImage();
		if (image) {
			mi.setImage(image);
			// HACK to get image width
			option._imageWidth = Dwt.getSize(AjxImg.getImageElement(mi._iconCell)).x;
		}
		mi.addSelectionListener(new AjxListener(this, this._handleOptionSelection));
		mi._optionIndex = i;
		option.setItem(mi);
    }
    // Set the size of the menu.
    var size = this._button.getSize();
	menu.getHtmlElement().style.width = size.x;
	var el = this.getHtmlElement();
	
	return menu;
};


DwtSelect.prototype._handleOptionSelection = 
function(ev) {
	var menuItem = ev.item;
	var optionIndex = menuItem._optionIndex;
	var opt = this._options.get(optionIndex);
	var oldValue = this.getValue();
	this._setSelectedOption(opt);

	// notify our listeners
    var args = new Object();
    args.selectObj = this;
    args.newValue = opt.getValue();
    args.oldValue = oldValue;
    var event = DwtUiEvent.getEvent(ev);
    event._args = args;
    this.notifyListeners(DwtEvent.ONCHANGE, event);
};

DwtSelect.prototype._clearOptionSelection = 
function() {
    if (this._currentSelectionId != -1) {
        var currOption = DwtSelect._getObjectWithId(this._currentSelectionId);
        currOption.deSelect();
    }
};

DwtSelect.prototype._setSelectedOption = 
function(option) {
	var displayValue = option.getSelectedValue() || option.getDisplayValue();
	var image = option.getImage();
	if (this._selectedOption != option) {
 		if (displayValue) {
 			this.setText(displayValue);
 		}
 		if (image) {
 			this.setImage(image);
 		}
		this._selectedValue = option._value;
		this._selectedOption = option;
	}
    this._updateSelection(option);
};

DwtSelect.prototype._updateSelection = 
function(newOption) {
    var currOption = null;
    if (this._currentSelectionId != -1)
        currOption = DwtSelect._getObjectWithId(this._currentSelectionId);

    if (currOption)
        currOption.deSelect();

    if (newOption) {
		newOption.select();
		this._currentSelectionId = newOption.getIdentifier();
    }
};

DwtSelect.prototype._setDisabledStyle = 
function() {
	this.setClassName(this._className + " disabled");
};

DwtSelect.prototype._setEnabledStyle = 
function() {
	this.setClassName(this._origClassName);
};

/**
* Greg Solovyev 2/2/2004 added this class to be able to create a list of options 
* before creating the DwtSelect control. This is a workaround an IE bug, that 
* causes IE to crash with error R6025 when DwtSelectOption object are added to empty DwtSelect
* @class DwtSelectOptionData
* @constructor
*/
function DwtSelectOptionData (value, displayValue, isSelected, selectedValue) {
	if(value == null || displayValue==null) 
		return null;

	this.value = value;
	this.displayValue = displayValue;
	this.isSelected = isSelected;
	this.selectedValue = selectedValue;
}

/**
 * @class DwtSelectOption
 * @constructor
 *
 * DwtSelectOption encapsulates the option object that the DwtSelect widget
 * uses. 
 *
 * @param value (string) -- this is the value for the object, it will be 
 *                          returned in any onchange event.
 * @param selected (Boolean) -- whether or not the option should be selected
 *                              to start with.
 * @param displayValue (string) -- The value that the user will see 
 *                                 ( html encoding will be done on this 
 *                                 value internally ).
 * @param owner (DwtSelect) -- unused
 * @param optionalDOMId (string) -- unused
 * @param selectedValue 	[string]	Optional. The text value to use
 *										when this value is the currently
 *										selected value.
 */
function DwtSelectOption (value, selected, displayValue, owner, optionalDOMId, image, selectedValue) {
	this._value = value;
	this._selected = selected;
	this._displayValue = displayValue;
	this._image = image;
	this._selectedValue = selectedValue;

	this._internalObjectId = DwtSelect._assignId(this);
}

DwtSelectOption.prototype.setItem = 
function(menuItem) {
	this._menuItem = menuItem;
};

DwtSelectOption.prototype.getItem = 
function(menuItem) {
	return this._menuItem;
};

DwtSelectOption.prototype.getDisplayValue = 
function() {
	return this._displayValue;
};

DwtSelectOption.prototype.getImage = 
function() {
	return this._image;
};

DwtSelectOption.prototype.getSelectedValue =
function() {
	return this._selectedValue;
};

DwtSelectOption.prototype.getValue = 
function() {
	return this._value;
};

DwtSelectOption.prototype.setValue = 
function(stringOrNumber) {
	this._value = stringOrNumber;
};

DwtSelectOption.prototype.select = 
function() {
	this._selected = true;
};

DwtSelectOption.prototype.deSelect = 
function() {
	this._selected = false;
};

DwtSelectOption.prototype.isSelected = 
function() {
	return this._selected;
};

DwtSelectOption.prototype.getIdentifier = 
function() {
	return this._internalObjectId;
};

