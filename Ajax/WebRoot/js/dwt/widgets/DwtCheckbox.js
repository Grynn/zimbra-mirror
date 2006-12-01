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
 * This class implements a checkbox -- very similar to DwtLabel.
 *
 * @param {DwtComposite} parent Parent widget. Except in the case of <i>DwtShell</i> the
 * 		parent will be a control that has subclassed from <i>DwtComposite</i>
 * @param {Int} style The text style. May be one of: <i>DwtCheckbox.TEXT_LEFT</i>
 * 		or <i>DwtCheckbox.TEXT_RIGHT</i> arithimatically or'd (|) with  one of:
 * 		<i>DwtCheckbox.ALIGN_LEFT</i>, <i>DwtCheckbox.ALIGN_CENTER</i>, or <i>DwtCheckbox.ALIGN_LEFT</i>
 * 		The first determines were in the checkbox the text will appear (if set), the second
 * 		determine how the content of the text will be aligned. The default value for
 * 		this parameter is: <code>DwtCheckbox.TEXT_LEFT | DwtCheckbox.ALIGN_CENTER</code>
 * @param {String} className CSS class. If not provided defaults to the class name (optional)
 * @param {String} posStyle Positioning style (absolute, static, or relative). If
 * 		not provided defaults to <i>DwtControl.STATIC_STYLE</i> (optional)
 * @param {int} id An explicit ID to use for the control's HTML element. If not
 * 		specified defaults to an auto-generated id (optional)
 * @param {int} index index at which to add this control among parent's children (optional)
 * 
 * @see DwtLabel
 * 
 * @extends DwtControl
 * 
 * @requires DwtControl
 */
function DwtCheckbox(parent, style, className, posStyle, id, index) {
	if (arguments.length == 0) return;
	className = className ? className : "DwtCheckbox";
	DwtControl.call(this, parent, className, posStyle, false, id, index);
    this.__createHtml();
}

DwtCheckbox.prototype = new DwtControl;
DwtCheckbox.prototype.constructor = DwtCheckbox;

DwtCheckbox.prototype.toString = function() {
	return "DwtCheckbox";
};

//
// Constants
//

DwtCheckbox.TEXT_LEFT = 1;
DwtCheckbox.TEXT_RIGHT = 2;

DwtCheckbox.DEFAULT_PLACEMENT = DwtCheckbox.TEXT_RIGHT;

//
// Data
//

DwtCheckbox.prototype._textPlacement = DwtCheckbox.DEFAULT_PLACEMENT;
DwtCheckbox.prototype._selected = false;

//
// Public methods
//

// listeners

DwtCheckbox.prototype.addSelectionListener =
function(listener) {
	this.addListener(DwtEvent.SELECTION, listener);
};
DwtCheckbox.prototype.removeSelectionListener =
function(listener) {
	this.removeListener(DwtEvent.SELECTION, listener);
};

// properties

DwtCheckbox.prototype.setEnabled =
function(enabled) {
	if (enabled != this._enabled) {
		DwtControl.prototype.setEnabled.call(this, enabled);
        this._inputEl.disabled = !enabled;
        if (this._textCell) {
            this._textCell.className = enabled ? "Text" : "DisabledText";
        }
    }
};

DwtCheckbox.prototype.setSelected = function(selected) {
    if (this._selected != selected) {
        this._selected = selected;
        this._inputEl.checked = selected;
    }
};
DwtCheckbox.prototype.isSelected = function() {
    return this._selected;
};

DwtCheckbox.prototype.setText = function(text) {
    if (this._text != text) {
        this._text = text;
        if (!this._textEl) {
            this.__createHtml();
        }
        else if (text) {
            this._textEl.innerHTML = text;
        }
    }
};

DwtCheckbox.prototype.getText = function() {
    return this._text;
};

DwtCheckbox.prototype.getInputElement = function() {
    return this._inputEl;
};

//
// Protected methods
//

/** The input field inherits the id for accessibility purposes. */
DwtCheckbox.prototype._replaceElementHook =
function(oel, nel, inheritClass, inheritStyle) {
    nel = this.getInputElement();
    DwtControl.prototype._replaceElementHook.call(this, oel, nel, inheritClass, inheritStyle);
    if (oel.id) {
        nel.id = oel.id;
    }
};

//
// Private methods
//

DwtCheckbox.prototype.__createHtml =
function() {
    var oel = this._inputEl;

    var templateId = "ajax.dwt.templates.Widgets#checkbox";
    if (this._text) {
        templateId += this._textPlacement == DwtCheckbox.TEXT_LEFT ? "-text-left" : "-text-right";
    }

    var id = oel ? oel.id : this._htmlElId;
    console.log("oel: "+oel);
    console.log("this._htmlElId: "+this._htmlElId);
    console.log("id: "+id);
    this.getHtmlElement().innerHTML = AjxTemplate.expand(templateId, id);

    this._inputEl = document.getElementById(id+"_input");
    this._inputEl.checked = this._selected;
    if (oel) {
        var className = oel.className;
        if (className) {
            Dwt.addClass(this._inputEl, className);
        }
        var style = oel.getAttribute("style");
        if (style) {
            this._inputEl.setAttribute([this._inputEl.getAttribute("style"),style].join(";"));
        }
    }
    Dwt.setHandler(this._inputEl, DwtEvent.ONCLICK, DwtCheckbox.__handleClick);
    Dwt.associateElementWithObject(this._inputEl, this);

    this._textEl = document.getElementById(id+"_text");
    if (this._text) {
        this._textEl.innerHTML = this._text;
    }
};

//
// Private functions
//

DwtCheckbox.__handleClick = function(event) {
    var target = DwtUiEvent.getTarget(event);

    var selEv = DwtShell.selectionEvent;
    DwtUiEvent.copy(selEv, event);
    selEv.item = this;
    selEv.detail = target.checked;

    var checkbox = Dwt.getObjectFromElement(target);
    checkbox.setSelected(target.checked);
    checkbox.notifyListeners(DwtEvent.SELECTION, selEv);
};