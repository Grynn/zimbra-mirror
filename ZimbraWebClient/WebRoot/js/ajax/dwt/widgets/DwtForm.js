/*
 * ***** BEGIN LICENSE BLOCK *****
 *
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2008 Zimbra, Inc.
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

DwtForm.prototype.setValue = function(id, value, force) {
	var item = this._items[id];
	if (!item) return;
	if (!force && value == item.value) return;
	this._setModelValue(id, value);
	this._setControlValue(id, value);
};
DwtForm.prototype.getValue = function(id, defaultValue) {
	var item = this._items[id];
	if (!item) return;
	if (item.getter) {
		return this._call(item.getter) || defaultValue;
	}
	var value = this._getControlValue(id) || item.value;
	return value || defaultValue;
};

DwtForm.prototype.getControl = function(id) {
	var item = this._items[id];
	return item && item.control;
};

/** By default, returns <code>isVisible(id) && isEnabled(id)</code>. */
DwtForm.prototype.isRelevant = function(id) {
	return this.isVisible(id) && this.isEnabled(id);
};

// control methods

DwtForm.prototype.setLabel = function(id, label) {
	var item = this._items[id];
	if (!item) return;
	if (label == this.getLabel(id)) return;
	var control = item.control;
	if (!control) return;
	if (control.setLabel) { control.setLabel(label); return; }
	if (control.setText) { control.setText(label); return; }
};

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

DwtForm.prototype.setValid = function(id, valid) {
	if (typeof id == "boolean") {
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
DwtForm.prototype.isValid = function(id) {
	if (arguments.length == 0) {
		for (var id in this._invalid) {
			return false;
		}
		return true;
	}
	return !(id in this._invalid);
};
DwtForm.prototype.getInvalidItems = function() {
	return AjxUtil.keys(this._invalid);
};

DwtForm.prototype.setDirty = function(id, dirty) {
	if (typeof id == "boolean") {
		dirty = arguments[0];
		for (id in this._items) {
			this.setDirty(id, dirty);
		}
		return;
	}
	if (dirty) {
		this._dirty[id] = true;
	}
	else {
		delete this._dirty[id]; 
	}
};
DwtForm.prototype.isDirty = function(id) {
	if (arguments.length == 0) {
		for (var id in this._dirty) {
			return true;
		}
		return false;
	}
	return id in this._dirty;
};
DwtForm.prototype.getDirtyItems = function() {
	return AjxUtil.keys(this._dirty);
};

// convenience control methods

DwtForm.prototype.set = function(id, value) {
	this.setValue(value);
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
			var item = this._items[id];
			try {
				this.setValid(item.validator ? item.validator(this.getValue(id)) : true);
			}
			catch (e) {
				// TODO: What to do with error message?
			}
		}
		return this.isValid();
	}
	var item = this._items[id];
	if (!item) return true;
	try {
		this.setValid(item.validator ? item.validator(this.getValue(id)) : true);
	}
	catch (e) {
		// TODO: What to do with error message?
	}
	return !(id in this._invalid);
};

DwtForm.prototype.reset = function() {
	// init state
	this._dirty = {};
	this._invalid = {};
	for (var id in this._items) {
		var itemDef = this._items[id].def;
		this._initControl(itemDef);
	}
	// update values
	this.update();
	for (var id in this._items) {
		var item = this._items[id];
		item.ovalue = item.value;
	}
	// clear state
	this.setDirty(false);
	this.validate();
	// call handler
	if (this._onreset) {
		this._call(this._onreset);
	}
};

DwtForm.prototype.update = function() {
	// update all the values first
	for (var id in this._items) {
		var item = this._items[id];
		if (item.getter) {
			this.setValue(id, this._call(item.getter));
		}
	}
	// now set visible/enabled based on values
	for (var id in this._items) {
		var item = this._items[id];
		if (item.visible) {
			this.setVisible(id, this._call(item.visible));
		}
		if (item.enabled) {
			this.setEnabled(id, this._call(item.enabled));
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
	if (item.setter) {
		this._call(item.setter, [value]);
	}
	item.value = value;
	this.setDirty(id, value != item.ovalue);
	this.validate(id);
};

DwtForm.prototype._setControlValue = function(id, value) {
	var control = this._items[id].control;
	if (control) {
		// TODO: display value
		if (control instanceof DwtCheckbox || control instanceof DwtRadioButton) {
			control.setSelected(value);
			return;
		}
		if (control.setSelectedValue) { control.setSelectedValue(value); return; }
		if (control.setValue) { control.setValue(value); return; }
		if (control.setText && !(control instanceof DwtButton)) { control.setText(value); return; }
		if (!(control instanceof DwtControl)) { control.value = value; return; }
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
		if (!(control instanceof DwtControl)) return control.value;
	}
};

// utility

DwtForm.prototype._call = function(func, args) {
	return func.apply(this, args);
};

// html creation

DwtForm.prototype._createHtml = function(templateId) {
	this._createHtmlFromTemplate(templateId || this.TEMPLATE, { id: this._htmlElId });
};

DwtForm.prototype._createHtmlFromTemplate = function(templateId, data) {
	DwtComposite.prototype._createHtmlFromTemplate.apply(this, arguments);

	// initialize state
	var controls = [];
	this._items = {};
	this._tabGroup.removeAllMembers();
	this._onupdate = null;
	this._onreset = null;

	// create form
	var form = this.form;
	if (form && form.items) {
		// create controls
		this._registerControls(form.items, null, controls);
		// create handlers
		this._onupdate = DwtForm.__makeFunc(form.onupdate);
		this._onreset = DwtForm.__makeFunc(form.onreset);
	}

	// add links to list of controls
	var links = this.getHtmlElement().getElementsByTagName("A");
	for (var i = 0; i < links.length; i++) {
		var link = links[i];
		if (!link.href) continue;
		controls.push({
			tabindex:	link.getAttribute("tabindex") || Number.MAX_VALUE,
			control:	link
		});
	}

	// add controls to tab group
	controls.sort(DwtForm.__byTabIndex);
	for (var i = 0; i < controls.length; i++) {
		this._tabGroup.addMember(controls[i].control);
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
	parent = parent || this;
	var id = itemDef.id || [this._htmlElId, Dwt.getNextId()].join("_");
	var type = itemDef.type = itemDef.type || defaultType;

	// create item entry
	var item = this._items[id] = {
		id:			id, // for convenience
		def:		itemDef,
		parentDef:	parentDef,
		getter:		DwtForm.__makeGetter(itemDef),
		setter:		DwtForm.__makeSetter(itemDef),
		visible:	DwtForm.__makeFunc(itemDef.visible),
		enabled:	DwtForm.__makeFunc(itemDef.enabled),
		validator:	DwtForm.__makeFunc(itemDef.validator)
	};

	// create control
	var element = document.getElementById([parent._htmlElId,id].join("_"));
	var control;
	if (Dwt.instanceOf(type, "DwtRadioButtonGroup")) {
		// create control
		control = new window[type]({});
		item.control = control;
//		control._zform_id = id;
		// add children
		var nparams = {
			name:  [parent._htmlElId, id].join("_"),
			value: itemDef.value
		};
		for (var i = 0; i < itemDef.items.length; i++) {
			var radioItemDef = itemDef.items[i];
			var checked = radioItemDef.checked || radioItemDef.value == itemDef.value;
			var radio = this._registerControl(radioItemDef, itemDef, tabIndexes, nparams, parent, "DwtRadioButton");
			radio.setValue(radioItemDef.value);
			this._items[radioItemDef.id].value = checked;
			// handlers
			control.addRadio(radio.getInputElement().id, radio, checked);
			radio.addSelectionListener(new AjxListener(this, this._radio2group2model, [radioItemDef.id, id]));
			if (radioItemDef.onclick) {
				var handler = DwtForm.__makeFunc(radioItemDef.onclick);
				radio.addSelectionListener(new AjxListener(this, this._item2handler, [radioItemDef.id, handler]));
			}
			if (itemDef.onclick) {
				var handler = DwtForm.__makeFunc(itemDef.onclick);
				radio.addSelectionListener(new AjxListener(this, this._item2handler, [itemDef.id, handler]));
			}
			// HACK: Work around fact that the DwtRadioButtonGroup overwrites
			//       the radio button input element's onclick handler.
			DwtForm.__hack_fixRadioButtonHandler(radio);
		}
	}
	else {
		control = type ? this._createControl(itemDef, parentDef, tabIndexes, params, parent, defaultType) : element;
		item.control = control;
	}
	if (element && control instanceof DwtControl) {
		control.replaceElement(element);
	}

	// add to list of tab indexes
	if (tabIndexes && control) {
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

DwtForm.prototype._createControl = function(itemDef, parentDef,
                                            tabIndexes, params,
                                            parent, defaultType) {
	var id = itemDef.id || [this._htmlElId, Dwt.getNextId()].join("_");
	var type = itemDef.type = itemDef.type || defaultType;
	params = params ? AjxUtil.createProxy(params) : {};
	params.parent = parent || this;
	params.template = itemDef.template || params.template;
	params.className = itemDef.className || params.className;

	// constructor params for radio buttons
	var isRadioButton = Dwt.instanceOf(type, "DwtRadioButton");
	var isCheckBox = Dwt.instanceOf(type, "DwtCheckbox");
	if (isRadioButton || isCheckBox) {
		params.name = itemDef.name || params.name;
		params.checked = itemDef.checked != null ? itemDef.checked : params.checked;
	}

	// constructor params for input fields
	var isTextField = Dwt.instanceOf(type, "DwtInputField");
	if (isTextField) {
		params.size = itemDef.cols;
		params.rows = itemDef.rows;
	}

	var isTabPage = Dwt.instanceOf(type, "DwtTabViewPage");
	if (isTabPage) {
		params.contentTemplate = itemDef.template;
		delete itemDef.template;
	}

	// create control
	var control = new window[type](params);
//	control._zform_id = id;

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
		control.setImage(itemDef.image);
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
			parentId = parentDef.id; //parent._zform_id;
		}
		// handlers
		control.addSelectionListener(new AjxListener(this, this._item2parent, [id, parentId]));
		if (itemDef.onclick) {
			var handler = DwtForm.__makeFunc(itemDef.onclick);
			control.addSelectionListener(new AjxListener(this, this._item2handler, [id, handler]));
		}
		if (parentDef && parentDef.onclick) {
			var handler = DwtForm.__makeFunc(parentDef.onclick);
			control.addSelectionListener(new AjxListener(this, this._item2handler, [parentDef.id, handler]));
		}
	}

	// init checkbox, radio button
	else if (control instanceof DwtCheckbox && !(control instanceof DwtRadioButton)) {
		control.addSelectionListener(new AjxListener(this, this._control2model, [id]));
		if (itemDef.onclick) {
			var handler = DwtForm.__makeFunc(itemDef.onclick);
			control.addSelectionListener(new AjxListener(this, this._item2handler, [id, handler]));
		}
	}

	// init input field
	else if (control instanceof DwtInputField) {
		var func = DwtForm.__makeFunc(itemDef.onchange);
		var handler = AjxCallback.simpleClosure(this._input2model2handler, this, id, func);
		control.setHandler(DwtEvent.ONKEYUP, handler);
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

	// TODO: other controls (e.g. combobox, listview, slider, spinner, tree)

	// init anonymous composites
	else if (control instanceof DwtComposite) {
		if (itemDef.items) {
			this._registerControls(itemDef.items, itemDef, tabIndexes, null, control);
		}
	}

	// return control
	return control;
};

DwtForm.prototype._initControl = function(itemDef) {
	var id = itemDef.id;
	if (itemDef.label) this.setLabel(id, itemDef.label);
	if (itemDef.value) {
		var item = this._items[id];
		if (Dwt.instanceOf(itemDef.type, "DwtRadioButton")) {
			item.ovalue = item.value = item.control.isSelected();
		}
		else {
			this.setValue(id, itemDef.value, true);
			item.ovalue = item.value;
		}
	}
	if (typeof itemDef.enabled == "boolean") this.setEnabled(id, itemDef.enabled);
	if (typeof itemDef.visible == "boolean") this.setVisible(id, itemDef.visible);
};

// handlers

DwtForm.prototype._control2model = function(id) {
	this._setModelValue(id, this._getControlValue(id));
	this.update();
};

DwtForm.prototype._radio2group2model = function(radioId, groupId) {
	this._setModelValue(groupId, this.getControl(radioId).getValue());
	this._setModelValue(radioId, this._getControlValue(radioId));
	this.update();
};

DwtForm.prototype._input2model2handler = function(id, handler) {
	this._setModelValue(id, this._getControlValue(id));
	this.update();
	if (handler) {
		this._call(handler, id);
	}
};

DwtForm.prototype._item2parent = function(itemId, parentId) {
	var control = this.getControl(itemId);
	var itemDef = this._items[itemId].def;
	if (control instanceof DwtButtonColorPicker || (itemDef.menu && !itemDef.onclick)) {
		control._toggleMenu(); // HACK: button should have public API
	}
	else if (parentId) {
		this._setModelValue(parentId, this._getControlValue(itemId) || itemId);
		this.update();
	}
};

DwtForm.prototype._item2handler = function(id, handler) {
	this._call(handler, [id]);
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
		"var context = this.model;",
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
		"var context = this.model;",
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
	if (value == null || typeof type == "boolean") return null;
	if (typeof value == "function" && !(value instanceof RegExp)) return value;//return DwtForm.__wrapFunc(value);
	var body = [
		"with (this._context) {",
			"return (",value,");",
		"}"
	].join("");
	return new Function(body);
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
