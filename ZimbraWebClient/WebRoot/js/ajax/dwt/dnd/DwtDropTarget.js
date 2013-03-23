/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2005, 2006, 2007, 2009, 2010 VMware, Inc.
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
 * @constructor
 * @class
 * A drop target is registered with a control to indicate that the control is 
 * a drop target. The drop target is the mechanism by which the DnD framework provides 
 * the binding between the UI components and the application.
 * <p>
 * Application developers instantiate {@link DwtDropTarget} and register it with the control
 * which is to be a drop target (via {@link DwtControl.setDropTarget}). The
 * application should then register a listener with the {@link DwtDropTarget}. This way
 * when drop events occur the application will be notified and may act on them 
 * accordingly
 * </p>
 * 
 * @author Ross Dargahi
 * 
 * @param {array} transferType	a list of supported object types that may be dropped onto
 * 		this drop target. Typically the items represent classes (i.e. functions) whose 
 * 		instances may be dropped on this drop target e.g. 
 * 		<code>new DwtDropTarget(MailItem, AppointmentItme)</code>
 * 
 * @see DwtDropEvent
 * @see DwtControl
 * @see DwtControl#setDropTarget
 */
DwtDropTarget = function(types) {
	/** @private */
	this._evtMgr = new AjxEventMgr();

	/** @private */
	this.__hasMultiple = false;
	
	this._types = {};
	if (typeof types == "string") {
		types = [types];
	}
	if (types && types.length) {
		for (var i = 0; i < types.length; i++) {
			this.addTransferType(types[i]);
		}
	}
}

/** @private */
DwtDropTarget.__DROP_LISTENER = "DwtDropTarget.__DROP_LISTENER";

/** @private */
DwtDropTarget.__dropEvent = new DwtDropEvent();

/**
 * Returns a string representation of this object.
 * 
 * @return {string}	a string representation of this object
 */
DwtDropTarget.prototype.toString = 
function() {
	return "DwtDropTarget";
}

/**
 * Registers a listener for {@link DwtDragEvent} events.
 *
 * @param {AjxListener} dropTargetListener Listener to be registered 
 * 
 * @see DwtDropEvent
 * @see AjxListener
 * @see #removeDropListener
 */
DwtDropTarget.prototype.addDropListener =
function(dropTargetListener) {
	this._evtMgr.addListener(DwtDropTarget.__DROP_LISTENER, dropTargetListener);
}

/**
 * Removes a registered event listener.
 * 
 * @param {AjxListener} dropTargetListener Listener to be removed
 * 
 * @see AjxListener
 * @see #addDropListener
 */
DwtDropTarget.prototype.removeDropListener =
function(dropTargetListener) {
	this._evtMgr.removeListener(DwtDropTarget.__DROP_LISTENER, dropTargetListener);
}

/**
 *  Check to see if the types in <code>items</code> can be dropped on this drop target
 *
 * @param {object|array} items an array of objects or single object whose types are
 * 		to be checked against the set of transfer types supported by this drop target
 * 
 * @return true if all of the objects in <code>items</code> may legally be dropped on 
 * 		this drop target
 * @type boolean
 */
DwtDropTarget.prototype.isValidTarget =
function(items) {
	if (items instanceof Array) {
		var len = items.length;
		for (var i = 0; i < len; i++) {
			if (!this.__checkTarget(items[i])) {
				return false;
			}
		}
		return true;
	} else {
		return this.__checkTarget(items);
	}
}

/**
 * Calling this method indicates that the UI component backing this drop target has multiple 
 * sub-components
 */
DwtDropTarget.prototype.markAsMultiple = 
function() {
	this.__hasMultiple = true;
};

/**
 * Checks if the UI component backing this drop target has multiple sub-components.
 * 
 * @return	{boolean}		<code>true</code> if the UI component has multiple sub-components
 */
DwtDropTarget.prototype.hasMultipleTargets = 
function () {
	return this.__hasMultiple;
};

/**
 * Gets the transfer types.
 * 
 * @return {array}	the list of transfer types supported by this drop target
 * 
 * @see #setTransferTypes
 */
DwtDropTarget.prototype.getTransferTypes =
function() {
	return this._types;
}

/**
 * Declares a type of object as valid for being dropped onto this target. The type is provided
 * as a string, since the corresponding class may not yet be defined. The type is eval'ed before
 * it is used for any validation, since the check is done with <code>instanceof</code>.
 * 
 * @param {string}	type		the name of class
 */
DwtDropTarget.prototype.addTransferType =
function(type) {
	this._types[type] = null;
};

// The following methods are called by DwtControl during the Drag lifecycle 

/** @private */
DwtDropTarget.prototype._dragEnter =
function(operation, targetControl, srcData, ev, dndProxy) {
	DwtDropTarget.__dropEvent.operation = operation;
	DwtDropTarget.__dropEvent.targetControl = targetControl;
	DwtDropTarget.__dropEvent.action = DwtDropEvent.DRAG_ENTER;
	DwtDropTarget.__dropEvent.srcData = srcData;
	DwtDropTarget.__dropEvent.uiEvent = ev;
	DwtDropTarget.__dropEvent.doIt = true;
	DwtDropTarget.__dropEvent.dndProxy = dndProxy;
	this._evtMgr.notifyListeners(DwtDropTarget.__DROP_LISTENER, DwtDropTarget.__dropEvent);
	return DwtDropTarget.__dropEvent.doIt;
}

/** @private */
DwtDropTarget.prototype._dragLeave =
function() {
	DwtDropTarget.__dropEvent.action = DwtDropEvent.DRAG_LEAVE;
	this._evtMgr.notifyListeners(DwtDropTarget.__DROP_LISTENER, DwtDropTarget.__dropEvent);
}

/** @private */
DwtDropTarget.prototype._dragOpChanged =
function(newOperation) {
	DwtDropTarget.__dropEvent.operation = newOperation;
	DwtDropTarget.__dropEvent.action = DwtDropEvent.DRAG_OP_CHANGED;
	this._evtMgr.notifyListeners(DwtDropTarget.__DROP_LISTENER, DwtDropTarget.__dropEvent);
	return DwtDropTarget.__dropEvent.doIt;
};

/** @private */
DwtDropTarget.prototype._drop =
function(srcData, ev) {
	DwtDropTarget.__dropEvent.action = DwtDropEvent.DRAG_DROP;
	DwtDropTarget.__dropEvent.srcData = srcData;
	DwtDropTarget.__dropEvent.uiEvent = ev;
	this._evtMgr.notifyListeners(DwtDropTarget.__DROP_LISTENER, DwtDropTarget.__dropEvent);
	return DwtDropTarget.__dropEvent.doIt;
};


// Private methods

/**@private*/
DwtDropTarget.prototype.__checkTarget =
function(item) {
	if (this._types) {
		for (var i in this._types) {
			var ctor;
			if (this._types[i]) {
				ctor = this._types[i];
			} else {
				ctor = this._types[i] = eval(i);
			}
			if (ctor && (typeof ctor == "function") && (item instanceof ctor)) {
				return true;
			}
		}
		return false;
	}
};
