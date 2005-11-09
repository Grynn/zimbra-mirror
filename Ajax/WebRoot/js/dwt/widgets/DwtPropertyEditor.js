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

/** Generic Property Editor Widget.
 *
 * @author Mihai Bazon
 *
 * See initProperties() below
 */
function DwtPropertyEditor(parent, className, positionType) {
	if (arguments.length > 0) {
		if (!className)
			className = "DwtPropertyEditor";
		DwtComposite.call(this, parent, className, positionType);
		this._schema = null;
// 		this._propsById = {};
// 		this._props = [];
		this._init();

		this._setMouseEventHdlrs();
	}
};

DwtPropertyEditor.MSG = {
	lengthFault   : "The length of this field must be between MINLEN and MAXLEN characters",
	mustBeInteger : "This field must be an integer",
	mustBeNumber  : "This field must be a number",
	isRequired    : "This field must not be empty",
	minValueFault : "This field must be >= MINVAL",
	maxValueFault : "This field must be <= MAXVAL",

	// Now these 2 are kind of pointless...
	// We should allow a message in the prop. object.
	mustMatch     : "This field does not match validators: REGEXP",
	mustNotMatch  : "This field matches anti-validators: REGEXP" // LOL
};

DwtPropertyEditor.prototype = new DwtComposite;
DwtPropertyEditor.prototype.constructor = DwtPropertyEditor;

DwtPropertyEditor.prototype.toString = function() { return "DwtPropertyEditor"; }

DwtPropertyEditor.prototype._init = function() {
	var doc = this.getDocument();
	this._relDiv = doc.createElement("div");
	this._relDiv.style.position = "relative";
	this._tableEl = doc.createElement("table");
	this._tableEl.cellSpacing = this._tableEl.cellPadding = 0;
	this._tableEl.appendChild(doc.createElement("tbody"));
	this._relDiv.appendChild(this._tableEl);
	this.getHtmlElement().appendChild(this._relDiv);
	this.maxLabelWidth = 0;
	this.addListener(DwtEvent.ONMOUSEDOWN, new AjxListener(this, this._onMouseDown));
};

DwtPropertyEditor.prototype._onMouseDown = function(event) {
	if (this._currentInputField && !this._currentInputField.onblur()) {
		event._stopPropagation = true;
		event._returnValue = false;
	}
};

/** This function will initialize the Property Editor with a given schema and
 * property set.
 *
 *  @param schema - declares which properties/types are allowed; see below
 *  @param parent - parent schema, for subproperties
 *
 * "schema" is an object that maps property names to property declaration.
 * Here's an example of what I have in mind:
 *
 *  [
 *    {
 *      label        : "User Name",
 *      id           : "userName",
 *      type         : "string",
 *      value        : "",
 *      minLength    : 4,
 *      maxLength    : 8,
 *      mustMatch    : /^[a-z][a-z0-9_]+$/i,
 *      mustNotMatch : /^(admin|root|guest)$/i
 *    },
 *    {
 *      label     : "Address",
 *      id        : "address",
 *      type      : "struct",
 *      children  : [ // this is a nested schema definition
 *              { label : "Street", id: "street", type: "string" },
 *              { label  : "Country",
 *                id     : "country",
 *                type   : "list",
 *                values : [ "US", "UK", "Etc." ] }
 *      ]
 *    },
 *    {
 *      label     : "Age",
 *      id        : "age",
 *      type      : "integer",
 *      minValue  : 18,
 *      maxValue  : 80
 *    },
 *    {
 *      label     : "Birthday",
 *      id        : "birthday",
 *      type      : "date",
 *      minValue  : "YYYY/MM/DD"  // can we restrict the DwtCalendar?
 *    }
 *  ]
 *
 * The types we will support for now are:
 *
 *   - "number" / "integer" : Allows floating point numbers or integers only.
 *     Properties: "minValue", "maxValue".
 *
 *   - "string" : Allows any string to be inserted.  "minLength", "maxLength",
 *     "mustMatch", "mustNotMatch".
 *
 *   - "password" : Same as "string", only it's not displayed.
 *
 *   - "struct" : Composite property; doesn't have a value by itself, but has
 *     child properties (the "children" array) that are defined in the same way
 *     as a toplevel property.
 *
 * All types except "struct" will allow a "value" property which is expected
 * to be of a valid type that matches all validating properties (such as
 * minLength, etc.).  The value of this property will be displayed initially
 * when the widget is constructed.
 *
 * Also, all types will support a "readonly" property.
 */
DwtPropertyEditor.prototype.initProperties = function(schema, parent) {
	if (parent == null) {
		this._schema = schema;
		parent = null;
	}
	for (var i = 0; i < schema.length; ++i)
		this._createProperty(schema[i], parent);
};

DwtPropertyEditor.prototype._createProperty = function(prop, parent) {
	var
		doc   = this.getDocument(),
		level = parent ? parent._level + 1 : 0,
		tr    = this._tableEl.firstChild.appendChild(doc.createElement("tr"));

	// Initialize the "prop" object with some interesting attributes...
	prop._parent = parent;
	prop._level = level;
	prop._rowEl = tr;
	prop._propertyEditor = this;
	prop.type != null || (prop.type = "string");
	prop.value != null || (prop.value = "");

	// ... and methods.
	for (var i in DwtPropertyEditor._prop_functions)
		prop[i] = DwtPropertyEditor._prop_functions[i];

	// indent if needed
	tr.className = "level-" + level;

	if (prop.readonly)
		tr.className += " readonly";

	if (prop.type != "struct") {
		tr.className += " " + prop.type;

		// this is a simple property, create a label and value cell.
		var tdLabel = doc.createElement("td");
		tdLabel.className = "label";
		tr.appendChild(tdLabel);
		tdLabel.innerHTML = AjxStringUtil.htmlEncode(prop.label);
		tdLabel.onmousedown = DwtPropertyEditor.simpleClosure(prop._edit, prop);
		var tdField = doc.createElement("td");
		tdField.className = "field";
		tr.appendChild(tdField);
		tdField.value = prop.getValue();
		tdField.innerHTML = prop._makeDisplayValue();
		tdField.onmousedown = DwtPropertyEditor.simpleClosure(prop._edit, prop);

		prop._fieldCell = tdField;

		if (tdLabel.offsetWidth > this.maxLabelWidth)
			this.maxLabelWidth = tdLabel.offsetWidth;
	} else {
		var td = doc.createElement("td");
		td.colSpan = 2;
		tr.appendChild(td);
		td.className = "label";
		tr.className += " expander-collapsed";
		td.innerHTML = [ "<div>", AjxStringUtil.htmlEncode(prop.label), "</div>" ].join("");
		this.initProperties(prop.children, prop);
		td.onmousedown = DwtPropertyEditor.simpleClosure(prop._toggle, prop);
	}

	// collapsed by default
	if (level > 0) {
		tr.style.display = "none";
		parent._hidden = true;
	}
};

DwtPropertyEditor.prototype.makeFixedLabelWidth = function() {
	this._tableEl.rows[0].cells[0].style.width = this.maxLabelWidth + "px";
};

// these will be merged to each prop object that comes in the schema
DwtPropertyEditor._prop_functions = {

	_makeDisplayValue : function() {
		var val = this.getValue();
		if (this.type == "password")
			val = val.replace(/./g, "*");
		if (val == "")
			val = "<br />";
		else
			val = AjxStringUtil.htmlEncode(String(val));
		return val;
	},

	_display : function(visible) {
		var
			c = this.children,
			d = visible ? "" : "none";
		if (c) {
			var i = c.length;
			while (--i >= 0) {
				c[i]._rowEl.style.display = d;
				if (!visible)
					c[i]._display(false);
			}
			this._hidden = !visible;

			// change the class name accordingly
			this._rowEl.className = this._rowEl.className.replace(
				/expander-[^\s]+/,
				visible ? "expander-expanded" : "expander-collapsed");
		}
	},

	_toggle : function() { this._display(this._hidden); },

	_edit : function() {
		// Depending on the type, this should probably create different
		// fields for editing.  For instance, in a "date" property we
		// would want a calendar, while in a "list" property we would
		// want a drop-down select box.

		if (this.readonly)
			return;

		var self = this;

		switch (this.type) {
		    case "string" :
		    case "number" :
		    case "integer" :
		    case "password" :
			setTimeout(function() {
				self._createInputField();
			}, 50);
			break;

		    default :
			alert("We don't support this type yet");
		}
	},

	_createInputField : function() {
		var
			td     = this._fieldCell,
			pe     = this._propertyEditor,
			doc    = pe.getDocument(),
			canvas = pe._relDiv,
			input  = doc.createElement("input");

		input.className = this.type;
		input.setAttribute("autocomplete", "off");

		if (this.type == "password")
			input.type = "password";

		var left = td.offsetLeft, top = td.offsetTop;
		if (AjxEnv.isGeckoBased) {
			--left;
			--top;
		}
		input.style.left = left + "px";
		input.style.top = top + "px";
		input.style.width = td.offsetWidth + 1 + "px";
		input.style.height = td.offsetHeight + 1 + "px";

		input.value = this.getValue();

		canvas.appendChild(input);
		input.focus();

		input.onblur = DwtPropertyEditor.simpleClosure(this._saveInput, this);
		input.onkeydown = DwtPropertyEditor.simpleClosure(this._inputKeyPress, this);

		this._propertyEditor._currentInputField = this._inputField = input;
		if (!AjxEnv.isGeckoBased)
			setTimeout(function() {
				input.select();
			}, 10);
		else
			input.setSelectionRange(0, input.value.length);
	},

	getValue : function() {
		return this.value || "";
	},

	checkValue : function(val) {
		var empty = val == "";

		if (empty) {
			if (!this.required)
				return val;
			window.status = DwtPropertyEditor.MSG.isRequired;
			return null;
		}

		if (val.length > this.maxLength ||
		    val.length < this.minLength) {
			window.status = DwtPropertyEditor.MSG.lengthFault
				.replace(/MINLEN/, this.minLength)
				.replace(/MAXLEN/, this.maxLength);
			return null;
		}

		if (this.mustMatch && !this.mustMatch.test(val)) {
			window.status = this.msg_mustMatch ||
				DwtPropertyEditor.MSG.mustMatch.replace(
					/REGEXP/, this.mustMatch.toString());
			return null;
		}

		if (this.mustNotMatch && this.mustNotMatch.test(val)) {
			window.status = this.msg_mustNotMatch ||
				DwtPropertyEditor.MSG.mustNotMatch.replace(
					/REGEXP/, this.mustNotMatch.toString());
			return null;
		}

		switch (this.type) {
		    case "integer" :
		    case "number" :
			var n = new Number(val);
			if (isNaN(n)) {
				window.status = DwtPropertyEditor.MSG.mustBeNumber;
				return null;
			}
			if (this.type == "integer" && Math.round(n) != n) {
				window.status = DwtPropertyEditor.MSG.mustBeInteger;
				return null;
			}
			if (this.minValue != null && n < this.minValue) {
				window.status = DwtPropertyEditor.MSG.minValueFault
					.replace(/MINVAL/, this.minValue);
				return null;
			}
			if (this.maxValue != null && n > this.maxValue) {
				window.status = DwtPropertyEditor.MSG.maxValueFault
					.replace(/MAXVAL/, this.maxValue);
				return null;
			}
			val = n;
			if (this.type == "number" && this.decimals != null) {
				var str = val.toString();
				var pos = str.indexOf(".");
				if (pos == -1)
					pos = str.length;
				val = val.toPrecision(pos + this.decimals);
			}
			break;
		}
		return val;
	},

	_saveInput : function() {
		var input = this._inputField;
		var val = this.checkValue(input.value);
		if (val != null) {
			this.value = val;
			input.onblur = null;
			input.onkeyup = null;
			input.onkeydown = null;
			input.onkeypress = null;
			input.parentNode.removeChild(input);
			this._fieldCell.innerHTML = this._makeDisplayValue();
			this._inputField = null;
			this._propertyEditor._currentInputField = null;
			return true;
		} else {
			if (input.className.indexOf(" error") == -1)
				input.className += " error";
			return false;
		}
	},

	_inputKeyPress : function(ev) {
		ev || (ev = window.event);
		var input = this._inputField;
		if (ev.keyCode == 13)
			this._saveInput();
		if (ev.keyCode == 27) {
			input.value = this.value;
			this._saveInput();
		}
	}
};

// Since we don't like nested functions...
DwtPropertyEditor.simpleClosure = function(func, obj) {
	return function() { return func.call(obj, arguments[0]); };
};
