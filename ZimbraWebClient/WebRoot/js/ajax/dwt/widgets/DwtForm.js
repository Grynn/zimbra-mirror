/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2008, 2009, 2010, 2011, 2012 VMware, Inc.
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
 * Creates a form.
 * @class
 * This class represents a form.
 * 
 * @param	{hash}	params		a hash of parameters
 * 
 * @extends		DwtComposite
 * 
 * @private
 */
DwtForm = function(params) {
	if (arguments.length == 0) return;
	params = Dwt.getParams(arguments, DwtForm.PARAMS);
	params.className = params.className || "DwtForm";
	DwtComposite.apply(this, arguments);
	this.setScrollStyle(DwtControl.SCROLL);

	// data
	this._tabGroup = new DwtTabGroup(this._htmlElId);

	// context
	this._context = {
		set: AjxCallback.simpleClosure(this.set, this),
		get: AjxCallback.simpleClosure(this.get, this)
	};

	// construct form
	this._dirty = {};
	this._ignore = {};
	this._invalid = {};
	this._errorMessages = {};
	this.setModel(params.model);
	this.setForm(params.form);
	this.reset();
};
DwtForm.prototype = new DwtComposite;
DwtForm.prototype.constructor = DwtForm;

DwtForm.prototype.toString = function() {
	return "DwtForm";
};

//
// Constants
//

DwtForm.PARAMS = DwtControl.PARAMS.concat("form", "model");

//
// Public methods
//

/**
 * Sets the value.
 * 
 * @param	{string}	id	the id
 * @param	{string}	value		the value
 * @param	{boolean}	force		if <code>true</code>, to force update
 */
DwtForm.prototype.setValue = function(id, value, force) {
	if (typeof id != "string") id = String(id);
	if (id.match(/\./) || id.match(/\[/)) {
		var parts = id.replace(/\[(\d+)\](\.)?/,".$1$2").split(".");
		var control = this.getControl(parts[0]);
		if (Dwt.instanceOf(control, "DwtForm")) {
			control.setValue(parts.slice(1).join("."), value, force);
		}
		return;
	}
	var item = this._items[id];
	if (!item) return;
	if (!force && value == item.value) return;
	this._setModelValue(id, value);
	this._setControlValue(id, value);
};

/**
 * Gets the value.
 * 
 * @param	{string}	id	the id
 * @param	{string}	defaultValue		the default value
 * @return	{string}	the value
 */
DwtForm.prototype.getValue = function(id, defaultValue) {
	if (typeof id != "string") id = String(id);
	if (id.match(/\./) || id.match(/\[/)) {
		var parts = id.replace(/\[(\d+)\](\.)?/,".$1$2").split(".");
		var control = this.getControl(parts[0]);
		if (Dwt.instanceOf(control, "DwtForm")) {
			return control.getValue(parts.slice(1).join("."));
		}
		return null;
	}
	var item = this._items[id];
	if (!item) return;
	if (item.getter) {
		return this._call(item.getter) || defaultValue;
	}
	var value = this._getControlValue(id);
    if (value == null) value = item.value;

    //added <|| ""> because ... if value="" than it always returns defaultValue which could be undefined.
	return value || defaultValue || "";
};

/**
 * Gets the control for the item.
 * 
 * @param	{string}	id		the id
 * @return	{DwtControl}	the control
 */
DwtForm.prototype.getControl = function(id) {
	if (typeof id != "string") id = String(id);
	var item = this._items[id];
	return item && item.control;
};

/**
 * Checks if the id is relevant (meaning: is visible and is enabled).
 * 
 * @param	{string}	id 		the id
 * @return	 {boolean}	<code>true</code> if the item is relevant
 */
DwtForm.prototype.isRelevant = function(id) {
	return this.isVisible(id) && this.isEnabled(id);
};

DwtForm.prototype.getTabGroupMember = function() {
	return this._tabGroup;
};

// control methods

/**
 * Sets the label.
 * 
 * @param	{string}	id 		the id
 * @param	{string}	label 		the label
 */
DwtForm.prototype.setLabel = function(id, label) {
	var item = this._items[id];
	if (!item) return;
	if (label == this.getLabel(id)) return;
	var control = item.control;
	if (!control) return;
	if (control.setLabel) { control.setLabel(label); return; }
	if (control.setText) { control.setText(label); return; }
};

/**
 * Gets the label.
 * 
 * @param	{string}	id 		the id
 * @return	{string}	the label
 */
DwtForm.prototype.getLabel = function(id) {
	var item = this._items[id];
	var control = item && item.control;
	if (control) {
		if (control.getLabel) return control.getLabel();
		if (control.getText) return control.getText();
	}
	return "";
};

DwtForm.prototype.setVisible = function(id, visible) {
	// set the form's visibility
	if (arguments.length == 1) {
		DwtComposite.prototype.setVisible.call(this, arguments[0]);
		return;
	}
	// set control's visibility
	var item = this._items[id];
	var control = item && item.control;
	if (!control) return;
	if (control.setVisible) {
		control.setVisible(visible);
	}
	else {
		Dwt.setVisible(control, visible);
	}
	// if there's a corresponding "*_row" element
	var el = document.getElementById([this._htmlElId, id, "row"].join("_"));
	if (el) {
		Dwt.setVisible(el, visible);
	}
};

DwtForm.prototype.isVisible = function(id) {
	// this form's visibility
	if (arguments.length == 0) {
		return DwtComposite.prototype.isVisible.call(this);
	}
	// control's visibility
	var item = this._items[id];
	var control = item && item.control;
	if (!control) return false;
	if (control.getVisible) return control.getVisible();
	if (control.isVisible) return control.isVisible();
	return  Dwt.getVisible(control);
};

/**
 * Sets the enabled flag.
 * 
 * @param	{string}	id 		the id
 * @param	{boolean}	enabled		if <code>true</code>, the item is enabled
 */
DwtForm.prototype.setEnabled = function(id, enabled) {
	// set the form enabled
	if (arguments.length == 1) {
		DwtComposite.prototype.setEnabled.call(this, arguments[0]);
		return;
	}
	// set the control enabled
	var item = this._items[id];
	var control = item && item.control;
	if (!control) return;
	if (control.setEnabled) {
		control.setEnabled(enabled);
	}
	else {
		control.disabled = !enabled;
	}
};

/**
 * Checks if the item is enabled.
 * 
 * @param	{string}	id 		the id
 * @return	{boolean}	<code>true</code> if the item is enabled
 */
DwtForm.prototype.isEnabled = function(id) {
	// this form enabled?
	if (arguments.length == 0) {
		return DwtComposite.prototype.isEnabled.call(this);
	}
	// the control enabled?
	var item = this._items[id];
	var control = item && item.control;
	if (!control) return false;
	if (control.isEnabled) return control.isEnabled();
	if (control.getEnabled) return control.getEnabled();
	return  !control.disabled;
};

/**
 * Sets the valid flag.
 * 
 * @param	{string}	id 		the id
 * @param	{boolean}	valid		if <code>true</code>, the item is valid
 */
DwtForm.prototype.setValid = function(id, valid) {
	if (typeof(id) == "boolean") {
		valid = arguments[0];
		for (id in this._items) {
			this.setValid(id, valid);
		}
		return;
	}
	if (valid) {
		delete this._invalid[id]; 
	}
	else {
		this._invalid[id] = true;
	}
};

/**
 * Checks if the item is valid.
 * 
 * @param	{string}	id 		the id
 * @return	{boolean}	<code>true</code> if the item is valid
 */
DwtForm.prototype.isValid = function(id) {
	if (arguments.length == 0 || AjxUtil.isUndefined(id)) {
		for (var id in this._invalid) {
			return false;
		}
		return true;
	}
	return !(id in this._invalid);
};

/**
 * Sets the error message.
 * 
 * @param	{string}	id 		the id
 * @param	{string}	message	the message
 */
DwtForm.prototype.setErrorMessage = function(id, message) {
	if (!id || id == "") {
		this._errorMessages = {};
		return;
	}
	if (!message) {
		delete this._errorMessages[id]; 
	} else {
		this._errorMessages[id] = message;
	}
};

/**
 * Gets the error message.
 * 
 * @param	{string}	id 		the id
 * @return	{string|array}	the message(s)
 */
DwtForm.prototype.getErrorMessage = function(id) {
	if (arguments.length == 0) {
		var messages = {};
		for (var id in this._invalid) {
			messages[id] = this._errorMessages[id];
		}
		return messages;
	}
	return this._errorMessages[id];
};

DwtForm.prototype.getInvalidItems = function() {
	return AjxUtil.keys(this._invalid);
};

DwtForm.prototype.setDirty = function(id, dirty, skipNotify) {
	if (typeof id == "boolean") {
		dirty = arguments[0];
		for (id in this._items) {
			this.setDirty(id, dirty, true);
		}
		if (!skipNotify && this._ondirty) {
			this._call(this._ondirty, ["*"]);
		}
		return;
	}
	if (dirty) {
		this._dirty[id] = true;
	}
	else {
		delete this._dirty[id]; 
	}
	if (!skipNotify && this._ondirty) {
		var item = this._items[id];
		if (!item.ignore || !this._call(item.ignore)) {
			this._call(this._ondirty, [id]);
		}
	}
};
DwtForm.prototype.isDirty = function(id) {
	if (arguments.length == 0) {
		for (var id in this._dirty) {
			var item = this._items[id];
			if (item.ignore && this._call(item.ignore)) {
				continue;
			}
			return true;
		}
		return false;
	}
	var item = this._items[id];
	return item.ignore && this._call(item.ignore) ? false : id in this._dirty;
};
DwtForm.prototype.getDirtyItems = function() {
	// NOTE: This avoids needing a closure
	DwtForm.__acceptDirtyItem.form = this;
	return AjxUtil.keys(this._dirty, DwtForm.__acceptDirtyItem);
};
DwtForm.__acceptDirtyItem = function(id) {
	var form = arguments.callee.form;
	var item = form._items[id];
	return !item.ignore || !form._call(item.ignore);
};

DwtForm.prototype.setIgnore = function(id, ignore) {
	if (typeof id == "boolean") {
		this._ignore = {};
		return;
	}
	if (ignore) {
		this._ignore[id] = true;
		return;
	}
	delete this._ignore[id];
};
DwtForm.prototype.isIgnore = function(id) {
	return id in this._ignore;
};

// convenience control methods

DwtForm.prototype.set = function(id, value) {
	this.setValue(id, value, true);
	this.update();
};
DwtForm.prototype.get = DwtForm.prototype.getValue;

// properties

DwtForm.prototype.setModel = function(model, reset) {
	this._context.model = this.model = model;
};

DwtForm.prototype.setForm = function(form) {
	this._context.form = this.form = form;
	this._createHtml(form.template);
};

// form maintenance

DwtForm.prototype.validate = function(id) {
	if (arguments.length == 0) {
		this.setValid(true);
		for (var id in this._items) {
			this._validateItem(id);
		}
		return this.isValid();
	}
	return this._validateItem(id);
};

DwtForm.prototype._validateItem = function(id) {
	if (!id) return true;
	var item = this._items[id];
	if (!item) return true;
	try {
		var value = this.getValue(id);
		var outcome = item.validator ? item.validator(value) : ((item.control && item.control.validator) ? item.control.validator(value) : true);
		// the validator may return false to signify that the validation failed (but preferably throw an error with a message)
		// it may return true to signify that the field validates
		// It also may return a string or hash (truthy) that we may put into the value field (for normalization of data; e.g. if 13/10/2009 is transformed to 1/10/2010 by the validator)
		this.setValid(id, Boolean(outcome) || outcome === "");
		if (AjxUtil.isString(outcome) || AjxUtil.isObject(outcome)) {
			this._setControlValue(id, outcome); // Set display value
			item.value = item.setter ? this._call(item.setter, [outcome]) : outcome; // Set model value
			var dirty = !Boolean(this._call(item.equals, [item.value,item.ovalue]));
			this.setDirty(id, dirty);
		}
	}
	catch (e) {
		this.setErrorMessage(id, AjxUtil.isString(e) ? e : e.message);
		this.setValid(id, false);
	}
	return !(id in this._invalid);
};

DwtForm.prototype.reset = function(useCurrentValues) {
	// init state
	this._dirty = {};
	this._ignore = {};
	this._invalid = {};
	for (var id in this._items) {
		var item = this._items[id];
		if (item.control instanceof DwtForm) {
			item.control.reset(useCurrentValues);
		}
		var itemDef = this._items[id].def;
		if (!itemDef) continue;
		this._initControl(itemDef, useCurrentValues);
	}
	// update values
	this.update();
	for (var id in this._items) {
		var item = this._items[id];
		item.ovalue = item.value;
	}
	// clear state
	this.validate();
    this.setDirty(false);
	// call handler
	if (this._onreset) {
		this._call(this._onreset);
	}
};

DwtForm.prototype.update = function() {
	// update all the values first
	for (var id in this._items) {
		var item = this._items[id];
		if (item.control instanceof DwtForm) {
			item.control.update();
		}
		if (item.getter) {
			this.setValue(id, this._call(item.getter));
		}
	}
	// now set visible/enabled/ignore based on values
	for (var id in this._items) {
		var item = this._items[id];
		if (item.visible) {
			this.setVisible(id, Boolean(this._call(item.visible)));
		}
		if (item.enabled) {
			this.setEnabled(id, Boolean(this._call(item.enabled)));
		}
		if (item.ignore) {
			this.setIgnore(id, Boolean(this._call(item.ignore)));
		}
	}
	// call handler
	if (this._onupdate) {
		this._call(this._onupdate);
	}
};

//
// Protected methods
//

DwtForm.prototype._setModelValue = function(id, value) {
	var item = this._items[id];
	item.value = item.setter ? this._call(item.setter, [value]) : value;
	var dirty = !Boolean(this._call(item.equals, [item.value,item.ovalue]));
	this.setDirty(id, dirty);
	this.validate(id);
	return dirty;
};

DwtForm.prototype._setControlValue = function(id, value) {
	var control = this._items[id].control;
	if (control) {
		// TODO: display value
		if (control instanceof DwtCheckbox || control instanceof DwtRadioButton) {
			control.setSelected(value);
			return;
		}
		if (control instanceof DwtMenuItem && control.isStyle(DwtMenuItem.CHECK_STYLE)) {
			control.setChecked(value, true);
			return;
		}
		if (control.setSelectedValue) { control.setSelectedValue(value); return; }
		if (control.setValue) { control.setValue(value); return; }
		if (control.setText && !(control instanceof DwtButton)) { control.setText(value); return; }
		if (!(control instanceof DwtControl)) {
			// TODO: support other native form elements like select
			if (control.type == "checkbox" || control == "radio") {
				control.checked = value;
			}
			else {
				// TODO: handle error setting form input value
				control.value = value;
			}
			return;
		}
	}
};
DwtForm.prototype._getControlValue = function(id) {
	var control = this._items[id].control;
	if (control) {
		if (control instanceof DwtCheckbox || control instanceof DwtRadioButton) {
			return control.isSelected();
		}
		if (control.getSelectedValue) return control.getSelectedValue();
		if (control.getValue) return control.getValue();
		if (control.getText && !(control instanceof DwtButton)) return control.getText();
		if (!(control instanceof DwtControl)) {
			if (control.type == "checkbox" || control == "radio") return control.checked;
			return control.value;
		}
	}
};

DwtForm.prototype._deleteItem = function(id) {
	delete this._items[id];
	delete this._dirty[id];
	delete this._invalid[id];
	delete this._ignore[id];
};

// utility

DwtForm.prototype._call = function(func, args) {
	if (func) {
		if (args) return func.apply(this, args);
		// NOTE: Hack for IE which barfs with null args on apply
		return func.call(this);
	}
};

// html creation

DwtForm.prototype._createHtml = function(templateId) {
	this._createHtmlFromTemplate(templateId || this.TEMPLATE, { id: this._htmlElId });
};

DwtForm.prototype._createHtmlFromTemplate = function(templateId, data) {
	DwtComposite.prototype._createHtmlFromTemplate.apply(this, arguments);

	// initialize state
	var tabIndexes = [];
	this._items = {};
	this._tabGroup.removeAllMembers();
	this._onupdate = null;
	this._onreset = null;
	this._ondirty = null;

	// create form
	var form = this.form;
	if (form && form.items) {
		// create controls
		this._registerControls(form.items, null, tabIndexes);
		// create handlers
		this._onupdate = DwtForm.__makeFunc(form.onupdate);
		this._onreset = DwtForm.__makeFunc(form.onreset);
		this._ondirty = DwtForm.__makeFunc(form.ondirty);
	}

	// add links to list of tabIndexes
	var links = this.getHtmlElement().getElementsByTagName("A");
	for (var i = 0; i < links.length; i++) {
		var link = links[i];
		if (!link.href || link.getAttribute("notab") == "true") continue;
        var controlId = link.id && link.id.substr(this.getHTMLElId().length+1);
		if (this._items[controlId]) continue;
		tabIndexes.push({
			tabindex:	link.getAttribute("tabindex") || Number.MAX_VALUE,
			control:	link
		});
	}

	// add controls to tab group
	tabIndexes.sort(DwtForm.__byTabIndex);
	for (var i = 0; i < tabIndexes.length; i++) {
		var control = tabIndexes[i].control;
		var member = (control.getTabGroupMember && control.getTabGroupMember()) || control;
		this._tabGroup.addMember(member);
	}
};

DwtForm.prototype._registerControls = function(itemDefs, parentDef,
                                               tabIndexes, params,
                                               parent, defaultType) {
	for (var i = 0; i < itemDefs.length; i++) {
		this._registerControl(itemDefs[i], parentDef, tabIndexes, params, parent, defaultType);
	}
};

DwtForm.prototype._registerControl = function(itemDef, parentDef,
                                              tabIndexes, params,
                                              parent, defaultType) {
	// create item entry
	var id = itemDef.id || [this._htmlElId, Dwt.getNextId()].join("_");
	var item = this._items[id] = {
		id:			id, // for convenience
		def:		itemDef,
		parentDef:	parentDef,
		equals:		DwtForm.__makeFunc(itemDef.equals) || DwtForm.__equals,
		getter:		DwtForm.__makeGetter(itemDef),
		setter:		DwtForm.__makeSetter(itemDef),
		value:		itemDef.value,
		visible:	DwtForm.__makeFunc(itemDef.visible),
		enabled:	DwtForm.__makeFunc(itemDef.enabled),
		validator:	DwtForm.__makeFunc(itemDef.validator),
		ignore:		DwtForm.__makeFunc(itemDef.ignore),
		control:	itemDef.control
	};
	// NOTE: This is used internally for indexing of rows
	if (itemDef.aka) {
		this._items[id].aka = itemDef.aka;
		this._items[itemDef.aka] = item;
	}

	// is control already created?
	var control = item.control;
	if (control) {
		return control;
	}

	// create control
	parent = parent || this;
	var type = itemDef.type = itemDef.type || defaultType;
	var isMenu = (parentDef && parentDef.menu == itemDef);
	var element = document.getElementById([parent._htmlElId,id].join("_"));
	if (Dwt.instanceOf(type, "DwtRadioButtonGroup")) {
		// create control
		control = new window[type]({});
		item.control = control;

		// add children
		var nparams = {
			name:  [parent._htmlElId, id].join("_"),
			value: itemDef.value
		};
		if (itemDef.items) {
			for (var i = 0; i < itemDef.items.length; i++) {
				var radioItemDef = itemDef.items[i];
				var checked = radioItemDef.checked || radioItemDef.value == itemDef.value;
				var radio = this._registerControl(radioItemDef, itemDef, tabIndexes, nparams, parent, "DwtRadioButton");
				this._items[radioItemDef.id].value = checked;
				if (radio) {
					control.addRadio(radio.getInputElement().id, radio, checked);
					// handlers
					var handler = DwtForm.__makeFunc(radioItemDef.onclick || itemDef.onclick);
					radio.addSelectionListener(new AjxListener(this, this._radio2group2model, [radioItemDef.id, id, handler]));
					// HACK: Work around fact that the DwtRadioButtonGroup overwrites
					//       the radio button input element's onclick handler.
					DwtForm.__hack_fixRadioButtonHandler(radio);
				}
			}
		}
	}
	else if (type) {
		if (Dwt.instanceOf(type, "DwtInputField")) {
			item.value = item.value || "";
		}
		if (Dwt.instanceOf(type, "DwtFormRows")) {
		    item.equals = DwtFormRows.__equals;
		}
		if (element || isMenu) {
			control = item.control = this._createControl(itemDef, parentDef, tabIndexes, params, parent, defaultType);
		}
	}
	else if (element) {
		this._attachElementHandlers(itemDef, parentDef, tabIndexes, parent, element);
		control = item.control = element;
		if (itemDef.items) {
			this._registerControls(itemDef.items, itemDef, tabIndexes, null, parent, null);
		}
	}
	if (element && control instanceof DwtControl) {
		control.replaceElement(element);
	}
	if (element && control instanceof DwtInputField) {
		control.getInputElement().id += "_input";
	}

	// add to list of tab indexes
	if (itemDef.notab == null) {
		itemDef.notab = element && element.getAttribute("notab") == "true";
	}
	if (tabIndexes && control && !itemDef.notab && !(control instanceof DwtRadioButtonGroup)) {
		tabIndexes.push({
			tabindex:	(element && element.getAttribute("tabindex")) || Number.MAX_VALUE,
			control:	control
		});
	}

	// clean up
	if (control instanceof DwtListView) {
		item.getter = item.getter || AjxCallback.simpleClosure(this.__list_getValue, this, id);
		item.setter = item.setter || AjxCallback.simpleClosure(this.__list_setValue, this, id);
	}

	// return control
	return control;
};

DwtForm.prototype._attachElementHandlers = function(itemDef, parentDef, tabIndexes, parent, element) {
	var id = itemDef.id;
	var name = element.nodeName.toLowerCase();
	var type = element.type;
	if (type == "checkbox" || type == "radio") {
		var parentId;
		if (type == "radio") {
			parentId = element.name;
			if (!this._items[parentId]) this._items[parentId] = { id: parentId };
			if (element.checked) {
				this._items[parentId].value = element.value;
			}
		}
		// checked
		var onclick = element.onclick ;
		var handler = DwtForm.__makeFunc(itemDef.onclick);
		element.onclick = AjxCallback.simpleClosure(this._htmlInput_checked, this, id, parentId, handler, onclick);
	}
	else if (name == "select") {
		// map selectedIndex to value of option
		var onchange = element.onchange;
		var handler = DwtForm.__makeFunc(itemDef.onchange);
		element.onchange = AjxCallback.simpleClosure(this._htmlSelect_selectedIndex, this, id, handler, onchange);
	}
	else if (name == "button" || name == "a" || 
	         type == "button" || type == "reset" || type == "submit") {
		// checked
		var onclick = element.onclick ;
		var handler = DwtForm.__makeFunc(itemDef.onclick);
		element.onclick = AjxCallback.simpleClosure(this._htmlElement, this, id, handler, onclick);
	}
	else if (name == "textarea" || name == "input") { // type == "text" ||  || type == "file" || type == "password") {
		// value
		var onchange = element.onchange;
		var handler = DwtForm.__makeFunc(itemDef.onchange);
		element.onchange = AjxCallback.simpleClosure(this._htmlInput_value, this, id, handler, onchange);
	}
	// TODO: attach other handlers
	return element;
};

DwtForm.prototype._createControl = function(itemDef, parentDef,
                                            tabIndexes, params,
                                            parent, defaultType) {
	var id = itemDef.id || [this._htmlElId, Dwt.getNextId()].join("_");
	var type = itemDef.type = itemDef.type || defaultType;
	params = params ? AjxUtil.createProxy(params) : {};
	params.id = params.id || [this._htmlElId, id].join("_");
	params.parent = parent || this;
	params.template = itemDef.template || params.template;
	params.className = itemDef.className || params.className;

	// constructor params for radio buttons
	var isRadioButton = Dwt.instanceOf(type, "DwtRadioButton");
	var isCheckBox = Dwt.instanceOf(type, "DwtCheckbox");
	if (isRadioButton || isCheckBox) {
		params.name = itemDef.name || params.name;
        params.value = itemDef.value || params.value;
		params.checked = itemDef.checked != null ? itemDef.checked : params.checked;
	}

	// constructor params for input fields
	var isTextField = Dwt.instanceOf(type, "DwtInputField");
	if (isTextField) {
		params.type = itemDef.password ? DwtInputField.PASSWORD : null;
		params.size = itemDef.cols;
		params.rows = itemDef.rows;
	}

	var isTabPage = Dwt.instanceOf(type, "DwtTabViewPage");
	if (isTabPage) {
		params.contentTemplate = itemDef.template;
		delete itemDef.template;
	}

    var isTree = Dwt.instanceOf(type, "DwtTree");
    if (isTree) {
        params.style = itemDef.style;
    }

	// add extra params
	params.formItemDef = itemDef;
	if (itemDef.params) {
		for (var p in itemDef.params) {
			params[p] = itemDef.params[p];
		}
	}

	// create control
	var control = new window[type](params);

	// init select
	if (control instanceof DwtSelect) {
		var options = itemDef.items;
		if (options) {
			for (var i = 0; i < options.length; i++) {
				var option = options[i];
				// convert to format that DwtSelect#addOption recognizes
				option.displayValue = option.label || option.value;
				control.addOption(option);
			}
		}
		var handler = DwtForm.__makeFunc(itemDef.onchange);
		control.addChangeListener(new AjxListener(this, this._control2model, [id, handler]));
	}

	// init button, menu item
	else if (control instanceof DwtButton || control instanceof DwtMenuItem) {
		if (itemDef.label) { control.setText(itemDef.label); }
		if (itemDef.image) { control.setImage(itemDef.image); }
		if (itemDef.menu) {
			var isMenu = Dwt.instanceOf(itemDef.menu.type || "DwtMenu", "DwtMenu");
			var menu;
			if (isMenu) {
				menu = this._registerControl(itemDef.menu, itemDef, null, null, control, "DwtMenu");
			}
			else {
				menu = new DwtMenu({parent:control});
				var style = Dwt.instanceOf(itemDef.menu.type, "DwtCalendar") ?
							DwtMenu.CALENDAR_PICKER_STYLE : DwtMenu.GENERIC_WIDGET_STYLE;
				this._registerControl(itemDef.menu, itemDef, null, { style: style }, menu);
			}
			control.setMenu(menu);
		}
		var parentId;
		if (parent instanceof DwtToolBar || parent instanceof DwtMenu) {
			parentId = parentDef.id;
		}
		// handlers
		var handler = DwtForm.__makeFunc(itemDef.onclick || (parentDef && parentDef.onclick));
		control.addSelectionListener(new AjxListener(this, this._item2parent, [id, parentId, handler]));
	}

	// init checkbox, radio button
	else if (control instanceof DwtCheckbox && !(control instanceof DwtRadioButton)) {
		var handler = DwtForm.__makeFunc(itemDef.onclick);
		control.addSelectionListener(new AjxListener(this, this._control2model, [id, handler]));
	}

	// init input field
	else if (control instanceof DwtInputField) {
		var changehandler = DwtForm.__makeFunc(itemDef.onchange);
		var onkeyup = AjxCallback.simpleClosure(this._input2model2handler, this, id, changehandler);
		control.setHandler(DwtEvent.ONKEYUP, onkeyup);
        if (AjxEnv.isFirefox){
            var onkeydown = this._onkeydownhandler.bind(this, id, changehandler);
            control.setHandler(DwtEvent.ONKEYDOWN, onkeydown);
        }
		var blurhandler = DwtForm.__makeFunc(itemDef.onblur);
        if (blurhandler) {
		    var onblur = AjxCallback.simpleClosure(this._input2model2handler, this, id, blurhandler);
		    control.setHandler(DwtEvent.ONBLUR, onblur);
        }

		control.setHint(itemDef.hint);
	}

	// init list
	else if (control instanceof DwtListView) {
		control.addSelectionListener(new AjxListener(this, this._handleListSelection, [id]));
	}

	// init menu
	else if (control instanceof DwtMenu) {
		if (itemDef.items) {
			var menuItemDefs = itemDef.items;
			for (var i = 0; i < menuItemDefs.length; i++) {
				var menuItemDef = menuItemDefs[i];
				if (menuItemDef.type == DwtMenuItem.SEPARATOR_STYLE) {
					new DwtMenuItem({parent:control, style:DwtMenuItem.SEPARATOR_STYLE});
					continue;
				}
				this._registerControl(menuItemDef, itemDef, null, null, control, "DwtMenuItem");
			}
		}
	}

	// init tabs
	else if (control instanceof DwtTabView) {
		var pageDefs = itemDef.items;
		if (pageDefs) {
			this._registerControls(pageDefs, itemDef, null, null, control, "DwtTabViewPage");
		}
	}

	// init tab page
	else if (control instanceof DwtTabViewPage && parent instanceof DwtTabView) {
		var key = parent.addTab(itemDef.label, control);
		if (itemDef.image) {
			parent.getTabButton(key).setImage(itemDef.image);
		}
		if (itemDef.items) {
			this._registerControls(itemDef.items, itemDef, tabIndexes, null, control);
		}
	}

	// init toolbar
	else if (control instanceof DwtToolBar) {
		var toolbarItemDefs = itemDef.items;
		if (toolbarItemDefs) {
			for (var i = 0; i < toolbarItemDefs.length; i++) {
				var toolbarItemDef = toolbarItemDefs[i];
				if (toolbarItemDef.type == DwtToolBar.SPACER) {
					control.addSpacer(toolbarItemDef.size);
					continue;
				}
				if (toolbarItemDef.type == DwtToolBar.SEPARATOR) {
					control.addSeparator(toolbarItemDef.className);
					continue;
				}
				if (toolbarItemDef.type == DwtToolBar.FILLER) {
					control.addFiller(toolbarItemDef.className);
					continue;
				}
				this._registerControl(toolbarItemDef, itemDef, null, null, control, "DwtToolBarButton");
			}
		}
	}
	else if (control instanceof DwtCalendar) {
		if (itemDef.onselect instanceof AjxListener) {
			control.addSelectionListener(itemDef.onselect);
		}
	}

	// TODO: other controls (e.g. combobox, listview, slider, spinner, tree)

	// init anonymous composites
	else if (control instanceof DwtComposite) {
		if (itemDef.items) {
			this._registerControls(itemDef.items, itemDef, tabIndexes, null, control);
		}
	}

    // size control
    if (itemDef.width || itemDef.height) {
        if (control instanceof DwtInputField) {
            Dwt.setSize(control.getInputElement(), itemDef.width, itemDef.height);
        }
        else {
            control.setSize(itemDef.width, itemDef.height);
        }
    }

	// return control
	return control;
};

DwtForm.prototype._onkeydownhandler  = function(id, changehandler){
    setTimeout(this._input2model2handler.bind(this, id, changehandler), 500);
};

DwtForm.prototype._initControl = function(itemDef, useCurrentValues) {
	var id = itemDef.id;
	if (itemDef.label) this.setLabel(id, itemDef.label);
	var item = this._items[id];
	if (useCurrentValues) {
		item.ovalue = item.value;
	}
	else if (itemDef.value) {
		if (Dwt.instanceOf(itemDef.type, "DwtRadioButton")) {
			item.ovalue = item.value = item.control && item.control.isSelected();
		}
		else {
			this.setValue(id, itemDef.value, true);
			item.ovalue = item.value;
		}
	}
	else {
		item.ovalue = null;
	}
	if (typeof itemDef.enabled == "boolean") this.setEnabled(id, itemDef.enabled);
	if (typeof itemDef.visible == "boolean") this.setVisible(id, itemDef.visible);
};

// html handlers

DwtForm.prototype._htmlElement = function(id, formHandler, elementHandler, evt) {
	if (formHandler) {
		this._call(formHandler, [id]);
	}
	if (elementHandler) {
		elementHandler(evt);
	}
};

DwtForm.prototype._htmlInput_checked = function(id, parentId, handler, onclick, evt) {
	var control = this.getControl(id);
	var checked = control.checked;
	this._setModelValue(id, checked);
	if (parentId && checked) {
		this._setModelValue(parentId, control.value);
	}
	this.update();
	this._htmlElement(id, handler, onclick, evt);
};

DwtForm.prototype._htmlInput_value = function(id, handler, onchange, evt) {
	this._setModelValue(id, this.getControl(id).value);
	this.update();
	this._htmlElement(id, handler, onchange, evt);
};

DwtForm.prototype._htmlSelect_selectedIndex = function(id, handler, onchange, evt) {
	var select = this.getControl(id);
	this._setModelValue(id, select.options[select.selectedIndex].value);
	this.update();
	this._htmlElement(id, handler, onchange, evt);
};

// dwt handlers

DwtForm.prototype._control2model = function(id, handler) {
	this._setModelValue(id, this._getControlValue(id));
	this.update();
	if (handler) {
		this._call(handler, [id]);
	}
};

DwtForm.prototype._radio2group2model = function(radioId, groupId, handler) {
	this._setModelValue(groupId, this.getControl(radioId).getValue());
	this._setModelValue(radioId, this._getControlValue(radioId));
	this.update();
	if (handler) {
		this._call(handler, [radioId]);
	}
};

DwtForm.prototype._input2model2handler = function(id, handler) {
	this._setModelValue(id, this._getControlValue(id));
	this.update();
	if (handler) {
		this._call(handler, [id]);
	}
};

DwtForm.prototype._item2parent = function(itemId, parentId, handler) {
	var control = this.getControl(itemId);
	var itemDef = this._items[itemId].def;
	if (control instanceof DwtButtonColorPicker || (itemDef.menu && !itemDef.onclick)) {
		control._toggleMenu(); // HACK: button should have public API
	}
	else if (parentId) {
		this._setModelValue(parentId, this._getControlValue(itemId) || itemId);
		this.update();
	}
	if (handler) {
		this._call(handler, [itemId]);
	}
};

DwtForm.prototype._handleListSelection = function(id, evt) {
	this.update();
};

// setters and getters

DwtForm.prototype.__list_getValue = function(id) {
	return this.getControl(id).getSelection();
};
DwtForm.prototype.__list_setValue = function(id, value) {
	this.getControl(id).setSelection(value);
};

//
// Private functions
//

// code generation

DwtForm.__makeGetter = function(item) {
	var getter = item.getter;
	if (getter) return DwtForm.__makeFunc(getter);

	var ref = item.ref;
	if (!ref) return null;

	var parts = ref.split(".");
	var body = [
		"var context = this.model;"
	];
	for (var i = 0; i < parts.length; i++) {
		var name = parts[i];
		var fname = DwtForm.__makeFuncName(name);
		if (i == parts.length - 1) break;
		body.push(
			"context = context && (context.",fname," ? context.",fname,"() : context.",name,");"
		);
	}
	body.push(
		"var value = context ? (context.",fname," ? context.",fname,"() : context.",name,") : this._items.",name,".value;",
		"return value !== undefined ? value : defaultValue;"
	);
	return new Function("defaultValue", body.join(""));
};

DwtForm.__makeSetter = function(item) {
	var setter = item.setter;
	if (setter) return DwtForm.__makeFunc(setter);

	var ref = item.ref;
	if (!ref) return null;

	var parts = ref.split(".");
	var body = [
		"var context = this.model;"
	];
	for (var i = 0; i < parts.length; i++) {
		var isLast = i == parts.length - 1;
		var name = parts[i];
		var fname = DwtForm.__makeFuncName(name, isLast ? "set" : "get");
		if (isLast) break;
		body.push(
			"context = context && (context.",fname," ? context.",fname,"() : context.",name,");"
		);
	}
	body.push(
		"if (context) {",
			"if (context.",fname,") {",
				"context.",fname,"(value);",
			"}",
			"else {",
				"context.",name," = value;",
			"}",
		"}"
	);
	return new Function("value", body.join("\n"));
};

DwtForm.__makeFuncName = function(name, prefix) {
	return [prefix||"get",name.substr(0,1).toUpperCase(),name.substr(1)].join("");
};

DwtForm.__makeFunc = function(value) {
	if (value == null) return null;
	if (typeof value == "function" && !(value instanceof RegExp)) return value;
	var body = [
		"with (this._context) {",
			"return (",value,");",
		"}"
	].join("");
	return new Function(body);
};

DwtForm.__equals = function(a, b) {
	return a == b;
};

// Array.sort

DwtForm.__byTabIndex = function(a, b) {
	return a.tabindex - b.tabindex;
};

// hacks

DwtForm.__hack_fixRadioButtonHandler = function(radio) {
	var handlers = [radio.getInputElement().onclick, DwtCheckbox.__handleClick];
	var handler = function(evt) {
		for (var i = 0; i < handlers.length; i++) {
			var func = handlers[i];
			if (func) {
				func(evt);
			}
		}
	};
	Dwt.setHandler(radio.getInputElement(), DwtEvent.ONCLICK, handler);
};

//
// Class: DwtFormRows
//

// TODO: tab-group

/**
 * 
 * @extends		DwtForm
 * 
 * @private
 */
DwtFormRows = function(params) {
	if (arguments.length == 0) return;
	this._itemDef = params.formItemDef || {};
	params.className = params.className || "DwtFormRows";
	DwtForm.call(this, {
		id:params.id, parent:params.parent,
		form:{}, template:this._itemDef.template
	});

	// init state
	this._rowsTabGroup = new DwtTabGroup(this._htmlElId);

	// save state
	this._rowDef = this._itemDef.rowitem || {};
	this._equals = DwtForm.__makeFunc(this._rowDef.equals) || DwtForm.__equals;
	this._rowCount = 0;
	this._minRows = this._itemDef.minrows || 1;
	this._maxRows = this._itemDef.maxrows || Number.MAX_VALUE;
	if (this._itemDef.rowtemplate) {
		this.ROW_TEMPLATE = this._itemDef.rowtemplate;
	}

	// add default rows
	var itemDefs = this._itemDef.items || [];
	for (var i = 0; i < itemDefs .length; i++) {
		this.addRow(itemDefs[i]);
	}

	// add empty rows to satisfy minimum row count
	for ( ; i < this._minRows; i++) {
		this.addRow();
	}

	// remember listeners
	this._onaddrow = DwtForm.__makeFunc(this._itemDef.onaddrow);
	this._onremoverow = DwtForm.__makeFunc(this._itemDef.onremoverow);
};
DwtFormRows.prototype = new DwtForm;
DwtFormRows.prototype.constructor = DwtFormRows;

DwtFormRows.prototype.toString = function() {
	return "DwtFormRows";
};

// Data

DwtFormRows.prototype.TEMPLATE = "dwt.Widgets#DwtFormRows";
DwtFormRows.prototype.ROW_TEMPLATE = "dwt.Widgets#DwtFormRow";

// Public methods

DwtFormRows.prototype.getTabGroupMember = function() {
	return this._rowsTabGroup;
};

DwtFormRows.prototype.setValue = function(array) {
	if (arguments.length > 1) {
		DwtForm.prototype.setValue.apply(this, arguments);
		return;
	}
	// adjust row count
	var min = Math.max(array.length, this._minRows);
	for (var i = this._rowCount; i > min; i--) {
		this.removeRow(i-1);
	}
	var max = Math.min(array.length, this._maxRows);
	for (var i = this._rowCount; i < max; i++) {
		this.addRow();
	}
	// initialize values
	for (var i = 0; i < max; i++) {
		this.setValue(String(i), array[i], true);
	}
	for (var i = array.length; i < this._rowCount; i++) {
		this.setValue(String(i), null, true);
	}
};

DwtFormRows.prototype.getValue = function() {
	if (arguments.length > 0) {
		return DwtForm.prototype.getValue.apply(this, arguments);
	}
	var array = new Array(this._rowCount);
	for (var i = 0; i < this._rowCount; i++) {
		array[i] = this.getValue(String(i));
	}
	return array;
};

DwtFormRows.prototype.getRowCount = function() {
	return this._rowCount;
};

DwtFormRows.prototype.addRow = function(itemDef, index) {
	if (this._rowCount >= this._maxRows) {
		return;
	}
	itemDef = itemDef || (this._rowDef && AjxUtil.createProxy(this._rowDef));
	if (!itemDef) return;

	if (index == null) index = this._rowCount;

	// move other rows "up"
	for (var i = this._rowCount - 1; i >= index; i--) {
		var oindex = i, nindex = i+1;
		var item = this._items[oindex];
		item.aka = String(nindex);
		delete this._items[oindex];
		this._items[item.aka] = item;
		this._setControlIds(item.id, item.aka);
	}

	// initialize definition
	itemDef.id = itemDef.id || Dwt.getNextId();
	itemDef.aka = String(index);
	this._rowCount++;

	// create row html
	var data = { id: [this.getHTMLElId(), itemDef.id].join("_") };
	var rowHtml = AjxTemplate.expand(this.ROW_TEMPLATE, data);

	var rowsEl = this._rowsEl;
	rowsEl.appendChild(Dwt.toDocumentFragment(rowHtml, data.id+"_row"));
	var rowEl = rowsEl.lastChild;
	if (index != this._rowCount - 1) {
		rowsEl.insertBefore(rowEl, rowsEl.childNodes[index]);
	}

	// create controls
	var tabIndexes = [];
	var control = this._registerControl(itemDef, null, tabIndexes);

	var addDef = this._itemDef.additem ? AjxUtil.createProxy(this._itemDef.additem) : { image: "Add" };
	addDef.id = addDef.id || itemDef.id+"_add";
	addDef.visible = "this.getRowCount() < this.getMaxRows()";
	addDef.ignore = true;
	var addButton = this._registerControl(addDef,null,tabIndexes,null,null,"DwtButton");
	if (!addDef.onclick) {
		addButton.addSelectionListener(new AjxListener(this, this._handleAddRow, [itemDef.id]));
	}

	var removeDef = this._itemDef.removeitem ? AjxUtil.createProxy(this._itemDef.removeitem) : { image: "Remove" };
	removeDef.id = removeDef.id || itemDef.id+"_remove";
	removeDef.visible = "this.getRowCount() > this.getMinRows()";
	removeDef.ignore = true;
	var removeButton = this._registerControl(removeDef,null,tabIndexes,null,null,"DwtButton");
	if (!removeDef.onclick) {
		removeButton.addSelectionListener(new AjxListener(this, this._handleRemoveRow, [itemDef.id]));
	}

	// remember where we put it
	var item = this._items[itemDef.id];
	item._rowEl = rowEl;
	item._addId= addDef.id;
	item._removeId = removeDef.id;

	// set control identifiers
	this._setControlIds(item.id, index);

	// create tab group for row
	var tabGroup = new DwtTabGroup(itemDef.id);
	tabIndexes.sort(DwtForm.__byTabIndex);
	for (var i = 0; i < tabIndexes.length; i++) {
		var control = tabIndexes[i].control;
		tabGroup.addMember(control.getTabGroupMember() || control);
	}

	// add to tab group
	if (index == this._rowCount - 1) {
		this._rowsTabGroup.addMember(tabGroup);
	}
	else {
		var indexItemDef = this._items[String(index+1)];
		var indexTabGroup = this._rowsTabGroup.getTabGroupMemberByName(indexItemDef.id);
		this._rowsTabGroup.addMemberBefore(tabGroup, indexTabGroup);
	}

	// update display and notify handler
	this.update();
	if (this._onaddrow) {
		this._call(this._onaddrow, [index]);
	}

	return control;
};

DwtFormRows.prototype.removeRow = function(indexOrId) {
	if (this._rowCount <= this._minRows) {
		return;
	}

	// delete item at specified index
	var item = this._items[indexOrId];
	if (item.control instanceof DwtControl) {
		this.removeChild(item.control);
	}
	delete this._items[item.aka];
	this._deleteItem(item.id);

	// delete add item
	var addItem = this._items[item._addId];
	if (addItem) {
		this.removeChild(addItem.control);
		this._deleteItem(addItem.id);
	}

	// delete remove item
	var removeItem = this._items[item._removeId];
	if (removeItem) {
		this.removeChild(removeItem.control);
		this._deleteItem(removeItem.id);
	}

	// shift everything down one, removing old last row
	var fromIndex = Number(item.aka);
	for (var i = fromIndex + 1; i < this._rowCount; i++) {
		var oindex = i, nindex = i-1;
		this._items[nindex] = this._items[oindex];
		this._items[nindex].aka = String(nindex);
		this._setControlIds(this._items[nindex].id, this._items[nindex].aka);
	}
	this._deleteItem(String(--this._rowCount));

	// remove row element
	var rowEl = item._rowEl;
	rowEl.parentNode.removeChild(rowEl);
	delete item._rowEl;

	// remove from tab group
	var tabGroup = this._rowsTabGroup.getTabGroupMemberByName(item.id);
	this._rowsTabGroup.removeMember(tabGroup);

	// update display and notify handler
	this.update();
	if (this._onremoverow) {
		this._call(this._onremoverow, [Number(item.aka)]);
	}

	// TODO: move focus to previous/next row/control???
};

DwtFormRows.prototype.getMinRows = function() {
	return this._minRows;
};
DwtFormRows.prototype.getMaxRows = function() {
	return this._maxRows;
};
DwtFormRows.prototype.getRowCount = function() {
	return this._rowCount;
};

DwtFormRows.prototype.getIndexForRowId = function(rowId) {
	var children = this._rowsEl.childNodes;
	for (var i = 0; i < children.length; i++) {
		if (children[i].id == [this._htmlElId,rowId,"row"].join("_")) {
			return i;
		}
	}
	return -1;
};

DwtFormRows.__equals = function(a,b) {
	if (a === b) return true;
	if (!a || !b || a.length != b.length) return false;
	for (var i = 0; i < a.length; i++) {
		if (!this._call(this._equals, [a[i],b[i]])) {
			return false;
		}
	}
	return true;
};

// Protected methods

/** Override to set child controls' identifiers. */
DwtFormRows.prototype._setControlIds = function(rowId, index) {
	var id = [this.getHTMLElId(), index].join("_");
	var item = this._items[rowId];
	this._setControlId(item && item.control, id);
	var addButton = this._items[item._addId];
	this._setControlId(addButton && addButton.control, id+"_add");
	var removeButton = this._items[item._removeId];
	this._setControlId(removeButton && removeButton.control, id+"_remove");
	// TODO: update parentid attribute of children
};

DwtFormRows.prototype._setControlId = function(control, id) {
	if (!control) return;
	if (control instanceof DwtControl) {
		control.setHtmlElementId(id);
	}
	else {
		control.id = id;
	}
};

DwtFormRows.prototype._handleAddRow = function(rowId) {
	if (this.getRowCount() < this.getMaxRows()) {
		var index = this.getIndexForRowId(rowId) + 1;
		this.addRow(null, index);
	}
};

DwtFormRows.prototype._handleRemoveRow = function(rowId) {
	this.removeRow(rowId);
};

// DwtForm methods

DwtFormRows.prototype._setModelValue = function(id, value) {
	if (DwtForm.prototype._setModelValue.apply(this, arguments)) {
		this.parent.setDirty(this._itemDef.id, true);
	}
};

// DwtControl methods

DwtFormRows.prototype._createHtmlFromTemplate = function(templateId, data) {
	DwtForm.prototype._createHtmlFromTemplate.apply(this, arguments);
	this._rowsEl = document.getElementById(this._htmlElId+"_rows");
};


