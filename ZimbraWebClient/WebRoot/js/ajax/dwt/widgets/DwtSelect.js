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
 * Creates a select element.
 * @constructor
 * @class
 * Widget to replace the native select element.
 * <p>
 * Note: Currently this does not support multiple selection.
 * 
 * @param {hash}	params		a hash of parameters
 * @param {DwtComposite}      params.parent		the parent widget
 * @param {array}      params.options 		a list of options. This can be either an array of {@link DwtSelectOption} or {String} objects.
 * @param {string}      params.className		the CSS class
 * @param {constant}      params.posStyle		the positioning style (see {@link DwtControl})
 * @param {boolean}      [layout=true]		layout to use: DwtMenu.LAYOUT_STACK, DwtMenu.LAYOUT_CASCADE or DwtMenu.LAYOUT_SCROLL. A value of [true] defaults to DwtMenu.LAYOUT_CASCADE and a value of [false] defaults to DwtMenu.LAYOUT_STACK.
 *        
 * @extends		DwtButton
 *
 * TODO: add option to keep options sorted by display text
 */
DwtSelect = function(params) {
	if (arguments.length == 0) { return; }
	params = Dwt.getParams(arguments, DwtSelect.PARAMS);
	params.className = params.className || "ZSelect";
	params.posStyle = params.posStyle || Dwt.STATIC_STYLE;
    DwtButton.call(this, params);

	var events = AjxEnv.isIE ? [DwtEvent.ONMOUSEDOWN, DwtEvent.ONMOUSEUP] :
							   [DwtEvent.ONMOUSEDOWN, DwtEvent.ONMOUSEUP, DwtEvent.ONMOUSEOVER, DwtEvent.ONMOUSEOUT];
	this._setEventHdlrs(events);
	this._hasSetMouseEvents = true;

    // initialize some variables
    this._currentSelectedOption = null;
    this._options = new AjxVector();
    this._optionValuesToIndices = {};
    this._selectedValue = this._selectedOption = null;
	this._maxRows = params.maxRows || 0;
	this._layout = params.layout;
    this._congruent = params.congruent;
    this._hrCount = 0;

    // add options
    var options = params.options;
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
};

DwtSelect.PARAMS = ["parent", "options", "style", "className", "layout"];

DwtSelect.prototype = new DwtButton;
DwtSelect.prototype.constructor = DwtSelect;

DwtSelect.prototype.toString = 
function() {
    return "DwtSelect";
};

//
// Constants
//

/**
 * This template is only used for the auto-sizing of the select width.
 * 
 * @private
 */
DwtSelect._CONTAINER_TEMPLATE = "dwt.Widgets#ZSelectAutoSizingContainer";

//
// Data
//

// static

/**
 * This keeps track of all instances out there
 * 
 * @private
 */
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
		? AjxCore.objectWithId(element.dwtObj) : null;
};

// other

/**
 * Adds an option.
 * 
 * @param {string|DwtSelectOption|DwtSelectOptionData}		option			a {String} for the option value or the {@link DwtSelectOption} object
 * @param {boolean}	[selected]		indicates whether option should be the selected option
 * @param {Object}	value			if the option parameter is a {@link DwtSelectOption}, this will override the value already set in the option.
 * @param {String}  image	(optional)
 * @return 	{number} a handle to the newly added option
 *
 * TODO: support adding at an index
 */
DwtSelect.prototype.addOption =
function(option, selected, value, image) {

	if (!option) { return -1; }
	image = image || null;

	var opt = null;
	var val = null;
    var id = null;
	if (typeof(option) == 'string') {
		val = value != null ? value : option;
		opt = new DwtSelectOption(val, selected, option, this, null, image);
	} else {
		if (option instanceof DwtSelectOption) {
			opt = option;
			if (value) {
				opt.setValue(value);
			}
			selected = opt.isSelected();
		} else if(option instanceof DwtSelectOptionData || option.value != null) {
			val = value != null ? value : option.value;
			opt = new DwtSelectOption(val, option.isSelected, option.displayValue, this, null, option.image, option.selectedValue, false, option.extraData, option.id);
			selected = Boolean(option.isSelected);
            id = option.id;
		} else {
			return -1;
		}
	}

	this._options.add(opt);
	if (this._options.size() == 1 || selected) {
		this._setSelectedOption(opt);
	}

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

	this.fixedButtonWidth(); //good to call always to prevent future bugs due to the vertical space.

	// Register listener to create new menu.
	this.setMenu(this._menuCallback, true);

    // return the index of the option.
    this._optionValuesToIndices[opt.getValue()] = this._options.size() - 1;
    return (this._options.size() - 1);
};

DwtSelect.prototype.addHR =
function() {
    opt = new DwtSelectOption("hr" + this._hrCount.toString(), false, "", this, null, null, null, true);
    this._hrCount++;
	this._options.add(opt);
};

/**
 * Removes an option.
 *
 * @param {DwtSelectOption}		option			option to remove
 *
 * @return {number} index of the option that was removed, or -1 if there was an error
 */
DwtSelect.prototype.removeOption =
function(option) {

	if (!option) { return -1; }

	// Register listener to create new menu.
	this.setMenu(this._menuCallback, true);

	this._options.remove(option);
	var size = this._options.size();

	var value = option.getValue();
	var index = this._optionValuesToIndices[value];
	if (index != null) {
		this._pseudoItemsEl.deleteRow(index);
		if (this._selectedOption == option && size > 0) {
			var newSelIndex = (index >= size) ? size - 1 : index;
			this._setSelectedOption(this._options.get(newSelIndex));
		}
		this.fixedButtonWidth(); //good to call always to prevent future bugs due to the vertical space.
	}

	delete this._optionValuesToIndices[value];
	for (var i = index; i < size; i++) {
		var option = this._options.get(i);
		this._optionValuesToIndices[option.getValue()] = i;
	}

	return index;
};

/**
 * Removes an option based on its value.
 *
 * @param {string}		value			value of the option to remove
 *
 * @return {number} index of the option that was removed, or -1 if there was an error
 */
DwtSelect.prototype.removeOptionWithValue =
function(value) {

	var option = this.getOptionWithValue(value);
	return option ? this.removeOption(option) : -1;
};

DwtSelect.prototype.popup =
function() {
	var menu = this.getMenu();
	if (!menu) { return; }
	if (this._currentSelectedOption) {
		menu.setSelectedItem(this._currentSelectedOption.getItem());
	}

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
 * @param {Object}	value		the value of the option to rename
 * @param {string}	newValue	the new display value
 */
DwtSelect.prototype.rename =
function(value, newValue) {

	var option = this.getOptionWithValue(value);
	if (!option) { return; }
	option._displayValue = newValue;

	if (this._selectedOption && (this._selectedOption._value == value))	{
		this.setText(AjxStringUtil.htmlEncode(newValue));
	}

	// Register listener to create new menu.
	this.setMenu(this._menuCallback, true);
};

/**
 * Enables or disables an option.
 *
 * @param {Object}	value		the value of the option to enable/disable
 * @param {boolean}	enabled		if <code>true</code>, enable the option
 */
DwtSelect.prototype.enableOption =
function(value, enabled) {
	var option = this.getOptionWithValue(value);
	if (!option) { return; }
	if (option.enabled != enabled) {
		option.enabled = enabled;
		var item = option.getItem();
		if (item) {
			item.setEnabled(enabled);
		}
	}
};

/**
 * Clears the options.
 * 
 */
DwtSelect.prototype.clearOptions =
function() {
	var opts = this._options.getArray();
	for (var i = 0; i < opts.length; ++i) {
		opts[i] = null;
	}
	this._options.removeAll();
	this._optionValuesToIndices = null;
	this._optionValuesToIndices = [];
	this._selectedValue = null;
	this._selectedOption = null;
	this._currentSelectedOption = null;
	if (this._pseudoItemsEl) {
		try {
			this._pseudoItemsEl.innerHTML = ""; //bug 81504
		}
		catch (e) {
			//do nothing - this happens in IE for some reason. Stupid IE. "Unknown runtime error".
		}
	}
};

/**
 * Sets the select name.
 * 
 * @param	{string}	name		the name
 */
DwtSelect.prototype.setName =
function(name) {
	this._name = name;
};

/**
 * Gets the select name.
 * 
 * @return	{string}	the name
 */
DwtSelect.prototype.getName =
function() {
	return this._name;
};

/**
 * Sets the selected value.
 * 
 * @param	{Object}	optionValue		the value of the option to select
 */
DwtSelect.prototype.setSelectedValue =
function(optionValue) {
    var index = this._optionValuesToIndices[optionValue];
    if (index != null) {
        this.setSelected(index);
    }
};

/**
 * Sets the option as the selected option.
 * 
 * @param {number}	optionHandle 	a handle to the option
 * 
 * @see		#addOption
 */
DwtSelect.prototype.setSelected =
function(optionHandle) {
    var optionObj = this.getOptionWithHandle(optionHandle);
	this.setSelectedOption(optionObj);
};

/**
 * Gets the option count.
 * 
 * @return	{number}	the option count
 */
DwtSelect.prototype.getOptionCount =
function() {
	return this._options.size();
};

/**
 * Gets the options.
 * 
 * @return	{AjxVector}		a vector of {@link DwtSelectOption} objects
 */
DwtSelect.prototype.getOptions =
function() {
	return this._options;
};

/**
 * Gets the option .
 * 
 * @param {number}	optionHandle 	a handle to the option
 * @return	{DwtSelectOption}	the option
 * @see		#addOption
 */
DwtSelect.prototype.getOptionWithHandle =
function(optionHandle) {
	return this._options.get(optionHandle);
};

DwtSelect.prototype.getOptionAtIndex = DwtSelect.prototype.getOptionWithHandle;

/**
 * Gets the index for a given value.
 * 
 * @param	{Object}	value		the value
 * @return	{number}		the index
 */
DwtSelect.prototype.getIndexForValue =
function(value) {
	return this._optionValuesToIndices[value];
};

/**
 * Gets the option for a given value.
 * 
 * @param	{Object}	optionValue		the value
 * @return	{DwtSelectOption}		the option
 */
DwtSelect.prototype.getOptionWithValue =
function(optionValue) {
	var index = this._optionValuesToIndices[optionValue];
	var option = null;
    if (index != null) {
        option = this.getOptionWithHandle(index);
    }
	return option;
};

/**
 * Sets the selected option.
 * 
 * @param	{Object}	optionObj		the object
 */
DwtSelect.prototype.setSelectedOption =
function(optionObj) {
	if (optionObj) {
		this._setSelectedOption(optionObj);
	}
};

/**
 * Gets the selected value.
 * 
 * @return	{Object}	the value
 */
DwtSelect.prototype.getValue =
function() {
    return this._selectedValue;
};

/**
 * Gets the selected option.
 * 
 * @return	{DwtSelectOption}	the selected option
 */
DwtSelect.prototype.getSelectedOption =
function() {
	return this._selectedOption;
};

/**
 * Gets the selected option index.
 * 
 * @return	{number}	the selected option index
 */
DwtSelect.prototype.getSelectedIndex =
function() {
	return this.getIndexForValue(this.getValue());
};

/**
 * Adds a change listener.
 * 
 * @param	{AjxListener}	listener		the listener
 */
DwtSelect.prototype.addChangeListener =
function(listener) {
    this.addListener(DwtEvent.ONCHANGE, listener);
};

/**
 * Gets the count of options.
 * 
 * @return	{number}	the count
 */
DwtSelect.prototype.size =
function() {
	return this._options.size();
};

/**
 * Disables the select.
 */
DwtSelect.prototype.disable =
function() {
	this.setEnabled(false);
};

/**
 * Enables the select.
 */
DwtSelect.prototype.enable =
function() {
	this.setEnabled(true);
};

DwtSelect.prototype.setImage =
function(imageInfo) {
	// dont call DwtButton base class!
	DwtLabel.prototype.setImage.call(this, imageInfo);
};

DwtSelect.prototype.setText =
function(text) {
	// dont call DwtButton base class!
	DwtLabel.prototype.setText.call(this, text);
};

DwtSelect.prototype.dispose =
function() {
	this._selectEl = null;
	if (this._pseudoItemsEl) {
		this._pseudoItemsEl.innerHTML = "";
		this._pseudoItemsEl = null;
	}
	this._containerEl = null;

	DwtButton.prototype.dispose.call(this);

	if (this._internalObjectId) {
		DwtSelect._unassignId(this._internalObjectId);
	}
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

/* use this in case you want the button to take as little space as needed, and not be aligned with the size of the drop-down.
	Especially useful in cases where we mess up the button (remove the text) such as in ZmFreeBusySchedulerView 
 */
DwtSelect.prototype.dynamicButtonWidth = 
function() {
	this._isDynamicButtonWidth = true; //if this is set, set this so fixedButtonWidth doesn't change this.
	this._selectEl.style.width = "auto"; //set to default in case fixedButtonWidth was called before setting it explicitely.
	this._pseudoItemsEl.style.display =  "none";
};

/*
 * Use this in case you want the select to be as wide as the widest option and
 * the options hidden so they don't overflow outside containers.
 */
DwtSelect.prototype.fixedButtonWidth =
function(){
	if (this._isDynamicButtonWidth) {
		return;
	}
	this._pseudoItemsEl.style.display = "block"; //in case this function was called before. This will fix the width of the _selectEl to match the options.
    var elm = this._selectEl;
	var width = elm.offsetWidth;
	//offsetWidth is 0 if some parent (ancestor) has display:none which is the case only in Prefs pages when the select is setup.
	//don't set width to 0px in this case as it acts inconsistent - filling the entire space. Better to keep it just dynamic.
	if (width) {
		elm.style.width = width + "px";
	}
    this._pseudoItemsEl.style.display = "none";
};

DwtSelect.prototype._createHtmlFromTemplate =
function(templateId, data) {
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
	// this has to be block for it to affect the layout. it is not seen because its visibility hidden for the TDs
	// inside, and also "overflow:hidden" (so mouse over the hidden stuff does not highlight)
	this._pseudoItemsEl.style.display = "block";
    // set classes
    var el = this.getHtmlElement();
    this._containerEl = el;

    this._selectEl.className = el.className;

    el.className = "ZSelectAutoSizingContainer";
    el.setAttribute("style", "");
    if (AjxEnv.isIE && !AjxEnv.isIE9up) {
        el.style.overflow = "hidden";
    }
};

DwtSelect.prototype._createMenu =
function() {
    var menu = new DwtSelectMenu(this);
    var mi;
    for (var i = 0, len = this._options.size(); i < len; ++i) {
	    var option = this._options.get(i);
        if (option._hr) {
            mi = new DwtMenuItem({parent:menu, style:DwtMenuItem.SEPARATOR_STYLE});
            mi.setEnabled(false);
        } else {
            var mi = new DwtSelectMenuItem(menu, Dwt.getNextId((option.id || option._value) + "_"));
            var image = option.getImage();
            if (image) {
                mi.setImage(image);
            }
            var text = option.getDisplayValue();
            if (text) {
                mi.setText(AjxStringUtil.htmlEncode(text));
            }
            mi.setEnabled(option.enabled);

            mi.addSelectionListener(new AjxListener(this, this._handleOptionSelection));
            mi._optionIndex = i;
        }
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

DwtSelect.prototype._setSelectedOption =
function(option) {
	var displayValue = option.getSelectedValue() || option.getDisplayValue();
	var image = option.getImage();
	if (this._selectedOption != option) {
 		if (displayValue) {
 			this.setText(AjxStringUtil.htmlEncode(displayValue));
 		}
 		this.setImage(image);
 		this._selectedValue = option._value;
		this._selectedOption = option;
	}
    this._updateSelection(option);

    this.autoResize();
};

DwtSelect.prototype.autoResize =
function() {
    /* bug: 21041 */
    var divElId = this.getHtmlElement();
    AjxTimedAction.scheduleAction(new AjxTimedAction(this,
        function(){
            var divEl = document.getElementById(divElId.id);
            if (divEl) {
                divEl.style.width = divEl.childNodes[0].offsetWidth || "auto"; // offsetWidth doesn't work in IE if the element or one of its parents has display:none
            }
    }, 200));
};

DwtSelect.prototype._updateSelection = 
function(newOption) {
	var currOption = this._currentSelectedOption;

	if (currOption) {
		currOption.deSelect();
	}
	this._currentSelectedOption = newOption;
	if (!newOption) {
		return;
	}
	newOption.select();
	var menu = this.getMenu(true);
	if (!menu) {
		return;
	}
	menu.setSelectedItem(newOption.getItem());
};

// Call this function to update the rendering of the element
// Firefox sometimes renders the element incorrectly on certain DOM updates, so this function rectifies that
DwtSelect.prototype.updateRendering = 
function() {
	var scrollStyle = this.getScrollStyle();
	this.setScrollStyle(scrollStyle == Dwt.VISIBLE ? Dwt.CLIP : Dwt.VISIBLE);
	var reset = function() {
					try {
						this.setScrollStyle(scrollStyle);
					} catch(e) {}
				};
	var resetAction = new AjxTimedAction(this, reset);
	AjxTimedAction.scheduleAction(resetAction, 4);
};


//
// Class
//

/**
 * Greg Solovyev 2/2/2004 added this class to be able to create a list of options 
 * before creating the DwtSelect control. This is a workaround an IE bug, that 
 * causes IE to crash with error R6025 when DwtSelectOption object are added to empty DwtSelect
 * @class
 * @constructor
 * 
 * @private
 */
DwtSelectOptionData = function(value, displayValue, isSelected, selectedValue, image, id, extraData) {
	if (value == null || displayValue == null) { return null; }

	this.value = value;
	this.displayValue = displayValue;
	this.isSelected = isSelected;
	this.selectedValue = selectedValue;
	this.image = image;
	this.extraData = extraData;
    this.id = id || Dwt.getNextId();
};

//
// Class
//

/**
 * Creates a select option.
 * @constructor
 * @class
 * This class encapsulates the option object that the {@link DwtSelect} widget uses. 
 *
 * @param {String}	value this is the value for the object, it will be returned in any onchange event
 * @param {Boolean}	selected whether or not the option should be selected to start with
 * @param {String}	displayValue the value that the user will see (HTML encoding will be done on this value internally)
 * @param {DwtSelect}	owner 	not used
 * @param {String}	optionalDOMId		not used
 * @param {String}	[selectedValue] 	the text value to use when this value is the currently selected value
 * @param {Boolean}	hr                  True => This option will be usd to create a unselectable horizontal rule
 * @param {Object} extraData  map of extra name/value pairs
 */
DwtSelectOption = function(value, selected, displayValue, owner, optionalDOMId, image, selectedValue, hr, extraData, id) {
	this._value = value;
	this._selected = selected;
	this._displayValue = displayValue;
	this._image = image;
	this._selectedValue = selectedValue;
    this._hr = hr;
	this._extraData = extraData;

	this.id = id;

	this._internalObjectId = DwtSelect._assignId(this);
	this.enabled = true;
};

DwtSelectOption.prototype.toString =
function() {
    return "DwtSelectOption";
};

/**
 * Sets the item.
 * 
 * @param	{DwtSelectMenuItem}	menuItem		the menu item
 */
DwtSelectOption.prototype.setItem = 
function(menuItem) {
	this._menuItem = menuItem;
};

/**
 * Gets the item.
 * 
 * @return	{DwtSelectMenuItem}	the menu item
 */
DwtSelectOption.prototype.getItem = 
function(menuItem) {
	return this._menuItem;
};

/**
 * Gets the display value.
 * 
 * @return	{String}	the display value
 */
DwtSelectOption.prototype.getDisplayValue = 
function() {
	return this._displayValue;
};

/**
 * Gets the image.
 * 
 * @return	{String}	the image
 */
DwtSelectOption.prototype.getImage = 
function() {
	return this._image;
};

/**
 * Gets the selected value.
 * 
 * @return	{String}	the selected value
 */
DwtSelectOption.prototype.getSelectedValue =
function() {
	return this._selectedValue;
};

/**
 * Gets the value.
 * 
 * @return	{String}	the value
 */
DwtSelectOption.prototype.getValue = 
function() {
	return this._value;
};

/**
 * Sets the value.
 * 
 * @param	{String|Number}	stringOrNumber	the value
 */
DwtSelectOption.prototype.setValue = 
function(stringOrNumber) {
	this._value = stringOrNumber;
};

/**
 * Selects the option.
 */
DwtSelectOption.prototype.select = 
function() {
	this._selected = true;
};

/**
 * De-selects the option.
 */
DwtSelectOption.prototype.deSelect = 
function() {
	this._selected = false;
};

/**
 * Checks if the option is selected.
 * 
 * @return	{Boolean}	<code>true</code> if the option is selected
 */
DwtSelectOption.prototype.isSelected = 
function() {
	return this._selected;
};

/**
 * Gets the id.
 * 
 * @return	{String}	the id
 */
DwtSelectOption.prototype.getIdentifier = 
function() {
	return this._internalObjectId;
};

DwtSelectOption.prototype.getExtraData =
function(key) {
	return this._extraData && this._extraData[key];
};



/**
 * Creates a select menu.
 * @constructor
 * @class
 * This class represents a select menu.
 * 
 * @param	{DwtComposite}	parent		the parent
 * 
 * @extends		DwtMenu
 */
DwtSelectMenu = function(parent) {
    DwtMenu.call(this, {parent:parent, style:DwtMenu.DROPDOWN_STYLE, className:"DwtMenu", layout:parent._layout,
        maxRows:parent._maxRows, congruent:parent._congruent,
        id:Dwt.getNextId(parent.getHTMLElId() + "_Menu_")});
// Dwt.getNextId should be removed once Bug 66510 is fixed
};
DwtSelectMenu.prototype = new DwtMenu;
DwtSelectMenu.prototype.constructor = DwtSelectMenu;

DwtSelectMenu.prototype.TEMPLATE = "dwt.Widgets#ZSelectMenu";

DwtSelectMenu.prototype.toString =
function() {
    return "DwtSelectMenu";
};

/**
 * Creates a select menu item.
 * @constructor
 * @class
 * This class represents a menu item.
 * 
 * @param	{DwtComposite}	parent		the parent
 * 
 * @extends 	DwtMenuItem
 */
DwtSelectMenuItem = function(parent, id) {
    DwtMenuItem.call(this, {parent:parent, style:DwtMenuItem.SELECT_STYLE, className:"ZSelectMenuItem", id: id});
};
DwtSelectMenuItem.prototype = new DwtMenuItem;
DwtSelectMenuItem.prototype.constructor = DwtSelectMenuItem;

DwtSelectMenuItem.prototype.TEMPLATE = "dwt.Widgets#ZSelectMenuItem";

DwtSelectMenuItem.prototype.toString =
function() {
    return "DwtSelectMenuItem";
};
