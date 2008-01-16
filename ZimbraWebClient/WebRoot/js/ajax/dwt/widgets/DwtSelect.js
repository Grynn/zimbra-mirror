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
 * @class DwtSelect
 * @constructor
 * Widget to replace the native select element.
 *
 * Note: Currently this does not support multiple selection.
 * @param options (Array) optional array of options. This can be either
 *                        an array of DwtSelectOptions or an array of strings.
 */
DwtSelect = function(parent, options, className, posStyle) {
    var clsName = className || "ZSelect";
    var positionStyle = posStyle || Dwt.STATIC_STYLE;
    DwtButton.call(this, parent, null, clsName, positionStyle);

    // initialize some variables
    this._currentSelectionId = -1;
    this._options = new AjxVector();
    this._optionValuesToIndices = new Object();
    this._selectedValue = this._selectedOption = null;

    // add options
    if (options) {
        for (var i = 0; i < options.length; ++i) {
            this.addOption(options[i]);
        }
    }

    // setup display
    this.setDropDownImages("SelectPullDownArrow",			// normal
                           "SelectPullDownArrowDis",		// disabled
                           "SelectPullDownArrow",			// hover
                           "SelectPullDownArrow");			// down

    // add listeners
    this._menuCallback = new AjxListener(this, this._createMenu);
    this.setMenu(this._menuCallback, true);
}

DwtSelect.prototype = new DwtButton;
DwtSelect.prototype.constructor = DwtSelect;

DwtSelect.prototype.toString = 
function() {
    return "DwtSelect";
};

//
// Constants
//

/** This template is only used for the auto-sizing of the select width. */
DwtSelect._CONTAINER_TEMPLATE = "dwt.Widgets#ZSelectAutoSizingContainer";

//
// Data
//

// static

/** This keeps track of all instances out there **/
DwtSelect._objectIds = [null];

// templates

DwtSelect.prototype.TEMPLATE = "dwt.Widgets#ZSelect";

//
// Public methods
//

// static

DwtSelect.getObjectFromElement =
function(element) {
	return element && element.dwtObj
		? AjxCore.objectWithId(element.dwtObj) : null
};

// other

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
			opt = new DwtSelectOption(val, option.isSelected, option.displayValue, this, null, option.image, option.selectedValue);
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
	var table = this._pseudoItemsEl;
	var row = table.insertRow(-1);
	var cell = row.insertCell(-1);
	cell.className = 'ZSelectPseudoItem';
	cell.innerHTML = [
        "<div class='ZWidgetTitle'>",
            AjxStringUtil.htmlEncode(opt.getDisplayValue()),
        "</div>"
    ].join("");

	// Register listener to create new menu.
	this.setMenu(this._menuCallback, true);

    // return the index of the option.
    this._optionValuesToIndices[opt.getValue()] = this._options.size() - 1;
    return (this._options.size() - 1);
};

DwtSelect.prototype.popup = function() {
	var menu = this.getMenu();
	if (!menu) return;

	var selectElement = this._selectEl;
	var selectBounds = Dwt.getBounds(selectElement);
    
    // since buttons are often absolutely positioned, and menus aren't, we need x,y relative to window
	var verticalBorder = (selectElement.style.borderLeftWidth == "") ? 0 : parseInt(selectElement.style.borderLeftWidth);
	var horizontalBorder = (selectElement.style.borderTopWidth == "") ? 0 : parseInt(selectElement.style.borderTopWidth);
	horizontalBorder += (selectElement.style.borderBottomWidth == "") ? 0 : parseInt(selectElement.style.borderBottomWidth);

    var selectLocation = Dwt.toWindow(selectElement, 0, 0);
    var x = selectLocation.x + verticalBorder;
    var y = selectLocation.y + selectBounds.height + horizontalBorder;
    menu.popup(0, x, y);
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
	this.setMenu(this._menuCallback, true);
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

DwtSelect.prototype.addChangeListener =
function(listener) {
    this.addListener(DwtEvent.ONCHANGE, listener);
};

DwtSelect.prototype.size =
function() {
	return this._options.size();
}

DwtSelect.prototype.disable =
function() {
	this.setEnabled(false);
};

DwtSelect.prototype.enable =
function() {
	this.setEnabled(true);
};

DwtSelect.prototype.setImage =
function(imageInfo) {
	// dont call DwtButton base class!
	DwtLabel.prototype.setImage.call(this, imageInfo);
}

DwtSelect.prototype.setText =
function(text) {
	// dont call DwtButton base class!
	DwtLabel.prototype.setText.call(this, text);
}


DwtSelect.prototype.dispose =
function() {
	DwtButton.prototype.dispose.call(this);
	if (this._internalObjectId)
		DwtSelect._unassignId(this._internalObjectId);
};

//
// Protected methods
//

// static

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

// other

DwtSelect.prototype._createHtmlFromTemplate = function(templateId, data) {
    // wrap params
    var containerTemplateId = DwtSelect._CONTAINER_TEMPLATE;
    var containerData = {
        id: data.id,
        selectTemplateId: templateId || this.TEMPLATE,
        selectData: data
    };

    // generate html
    DwtButton.prototype._createHtmlFromTemplate.call(this, containerTemplateId, containerData);
    this._selectEl = document.getElementById(data.id+"_select_container");
    this._pseudoItemsEl = document.getElementById(data.id+"_pseudoitems_container");

    // set classes
    var el = this.getHtmlElement();
    this._containerEl = el;

    this._selectEl.className = el.className;
//    this._selectEl.setAttribute("style", el.getAttribute("style"));

    el.className = "ZSelectAutoSizingContainer";
    el.setAttribute("style", "");
    if (AjxEnv.isIE) {
        el.style.overflow = "hidden";
    }

//    this.getHtmlElement = new Function("return this._selectEl;"); // avoid closure
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

DwtSelect.prototype._createMenu = function() {
    var menu = new DwtSelectMenu(this);
    for (var i = 0, len = this._options.size(); i < len; ++i) {
		var mi = new DwtSelectMenuItem(menu, DwtMenuItem.SELECT_STYLE);
		var option = this._options.get(i);
        var image = option.getImage();
        if (image) {
            mi.setImage(image);
        }
        var text = option.getDisplayValue();
		if (text) {
			mi.setText(text);
		}

		mi.addSelectionListener(new AjxListener(this, this._handleOptionSelection));
		mi._optionIndex = i;
		option.setItem(mi);
    }
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

//
// Class
//

/**
* Greg Solovyev 2/2/2004 added this class to be able to create a list of options 
* before creating the DwtSelect control. This is a workaround an IE bug, that 
* causes IE to crash with error R6025 when DwtSelectOption object are added to empty DwtSelect
* @class DwtSelectOptionData
* @constructor
*/
DwtSelectOptionData = function(value, displayValue, isSelected, selectedValue, image) {
	if(value == null || displayValue==null) 
		return null;

	this.value = value;
	this.displayValue = displayValue;
	this.isSelected = isSelected;
	this.selectedValue = selectedValue;
	this.image = image;
}

//
// Class
//

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
DwtSelectOption = function(value, selected, displayValue, owner, optionalDOMId, image, selectedValue) {
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

//
// Class
//

DwtSelectMenu = function(parent) {
//    DwtMenu.call(this, parent, DwtMenu.DROPDOWN_STYLE, "ZSelectMenu", null, true);
    DwtMenu.call(this, parent, DwtMenu.DROPDOWN_STYLE, "DwtMenu", null, true); // TODO
}
DwtSelectMenu.prototype = new DwtMenu;
DwtSelectMenu.prototype.constructor = DwtSelectMenu;

DwtSelectMenu.prototype.TEMPLATE = "dwt.Widgets#ZSelectMenu";

//
// Class
//

DwtSelectMenuItem = function(parent) {
    DwtMenuItem.call(this, parent, DwtMenuItem.SELECT_STYLE, null, null, "ZSelectMenuItem");
}
DwtSelectMenuItem.prototype = new DwtMenuItem;
DwtSelectMenuItem.prototype.constructor = DwtSelectMenuItem;

DwtSelectMenuItem.prototype.TEMPLATE = "dwt.Widgets#ZSelectMenuItem";
