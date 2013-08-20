/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2006, 2007, 2008, 2009, 2010, 2013 Zimbra Software, LLC.
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
 * Creates a radio button group.
 * @constructor
 * @class
 * This class implements a group of radio buttons
 *
 * @param {hash} [radios] 	a hash whose keys are the ids of the radio button elements
 * 		and whose values are the values associated with those buttons
 * @param {string} [selectedId]	the id of the button to select initially
 * 
 * TODO: this really should be a DwtComposite
 * 
 * @private
 */
DwtRadioButtonGroup = function(radios, selectedId) {
	this._radios = {};
	this._radioButtons = {};
	this._values = {};
	this._value2id = {};
	this._eventMgr = new AjxEventMgr();
	
	for (var id in radios) {
		this.addRadio(id, radios[id], (id == selectedId));
	}
};

DwtRadioButtonGroup.prototype.toString =
function() {
	return "DwtRadioButtonGroup";
};

//
// Data
//

DwtRadioButtonGroup.prototype._enabled = true;
DwtRadioButtonGroup.prototype._visible = true;

//
// Public methods
//

DwtRadioButtonGroup.prototype.addSelectionListener = function(listener) {
	return this._eventMgr.addListener(DwtEvent.SELECTION, listener);
};

DwtRadioButtonGroup.prototype.removeSelectionListener = function(listener) {
	return this._eventMgr.removeListener(DwtEvent.SELECTION, listener);
};

DwtRadioButtonGroup.prototype.setEnabled = function(enabled) {
	this._enabled = enabled;
	for (var id in this._radios) {
		this._radios[id].disabled = !enabled;
	}
};
DwtRadioButtonGroup.prototype.isEnabled = function() {
	return this._enabled;
};

DwtRadioButtonGroup.prototype.setVisible = function(visible) {
	this._visible = visible;
	for (var id in this._radioButtons) {
		this._radioButtons[id].setVisible(visible);
	}
	for (var id in this._radios) {
		Dwt.setVisible(this._radios[id], visible);
	}
};
DwtRadioButtonGroup.prototype.isVisible = function() {
	return this._visible;
};

DwtRadioButtonGroup.prototype.addRadio =
function(id, radioButtonOrValue, selected) {
	var isRadioButton = radioButtonOrValue instanceof DwtRadioButton;
	var radioButton = isRadioButton ? radioButtonOrValue : null;
	var value = radioButton ? radioButton.getValue() : radioButtonOrValue;

	this._values[id] = value;
	this._value2id[value] = id;
	var element = document.getElementById(id);
	this._radios[id] = element;
	this._radioButtons[id] = radioButton;
	var handler = AjxCallback.simpleClosure(this._handleClick, this);
	Dwt.setHandler(element, DwtEvent.ONCLICK, handler);
   	element.checked = selected;
    if (selected) {
    	this._selectedId = id;
    }
};

DwtRadioButtonGroup.prototype.getRadioByValue = function(value) {
	var id = this._value2id[value];
	return this._radios[id];
};

DwtRadioButtonGroup.prototype.getRadioButtonByValue = function(value) {
	var id = this._value2id[value];
	return this._radioButtons[id];
};

DwtRadioButtonGroup.prototype.setSelectedId =
function(id, skipNotify) {
	if (id != this._selectedId) {
		var el = document.getElementById(id);
		if (!el) return;
		el.checked = true;
		this._selectedId = id;
		if (!skipNotify) {
			var selEv = DwtShell.selectionEvent;
			selEv.reset();
			this._notifySelection(selEv);
		}
	}
};

DwtRadioButtonGroup.prototype.setSelectedValue =
function(value, skipNotify) {
	var id = this._valueToId(value);
	this.setSelectedId(id, skipNotify);
};

DwtRadioButtonGroup.prototype.getSelectedId =
function() {
	return this._selectedId;
};

DwtRadioButtonGroup.prototype.getSelectedValue =
function() {
	return this._values[this._selectedId];
};

DwtRadioButtonGroup.prototype.getValue =
function() {
	return this.getSelectedValue();
};

DwtRadioButtonGroup.prototype.getData =
function(key) {
	var selectedRadio = !AjxUtil.isUndefined(this._selectedId) && this._radioButtons[this._selectedId];
	if (selectedRadio) {
		return selectedRadio.getData(key);
	}
	// return undefined;
}

//
// Protected methods
//

DwtRadioButtonGroup.prototype._valueToId =
function(value) {
	for (var id in this._values) {
		if (this._values[id] == value) {
			return id;
		}
		if (value === true && this._values[id] == "true") {
			return id;
		}
		if (value === false && (this._values[id] == "false" || this._values[id] == "")) {
			return id;
		}
	}
	return null;
};

DwtRadioButtonGroup.prototype._notifySelection = 
function(selEv) {
    selEv.item = this;
    selEv.detail = { id: this._selectedId, value: this._values[this._selectedId] };
    this._eventMgr.notifyListeners(DwtEvent.SELECTION, selEv);
};

DwtRadioButtonGroup.prototype._handleClick = 
function(event) {
	event = DwtUiEvent.getEvent(event);

	var target = DwtUiEvent.getTarget(event);
	if (target && target.nodeName.match(/label/i)) {
        target = document.getElementById(target.getAttribute(AjxEnv.isIE ? "htmlFor" : "for"));
    }

	var id = target.id;
	// NOTE: When you use the arrows on radio button groups in FF,
	//       the radio button that is being unselected is the target
	//       of the event. So we need to check to see if this target
	//       is the one that is checked.
	if (!target.checked) {
		for (id in this._radios) {
			if (this._radios[id].checked) {
				break;
			}
		}
	}
	if (id != this._selectedId) {
		this._selectedId = id;
	    var selEv = DwtShell.selectionEvent;
	    DwtUiEvent.copy(selEv, event);
		this._notifySelection(selEv);
	}
};
