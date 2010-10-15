/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2005, 2006, 2007, 2008, 2009, 2010 Zimbra, Inc.
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
 * 
 * @private
 */
AjxTimedAction = function(obj, func, args) {
	AjxCallback.call(this, obj, func, args);
	this._tid = -1;
	this._id = -1;
    this._runResult = null;
}
AjxTimedAction.prototype = new AjxCallback();
AjxTimedAction.prototype.constructor = AjxTimedAction;

// Setting a timeout of 25 days or more appears to revert it
// to 0 in FF3 and Safari3. There's really no reason to set
// it to anything above a few days, so set a max of 20 days.
AjxTimedAction.MAX_TIMEOUT = 20 * 24 * 60 * 60 * 1000;

AjxTimedAction.prototype.toString = 
function() {
	return "AjxTimedAction";
};

AjxTimedAction.prototype.getRunResult =
function() {
    return this._runResult;
};

AjxTimedAction._pendingActions = {};
AjxTimedAction._nextActionId = 1;

AjxTimedAction.scheduleAction =
function(action, timeout){
	if (!action) { return; }
	// if tid already exists, cancel previous timeout before setting a new one
	if (action._tid && action._tid != -1) {
		AjxTimedAction.cancelAction(action._id);
	}

	timeout = timeout || 0; // make sure timeout is numeric
	if (timeout > AjxTimedAction.MAX_TIMEOUT) {
		if (window.DBG) {
			DBG.println(AjxDebug.DBG1, "timeout value above maximum: " + timeout);
		}
		timeout = AjxTimedAction.MAX_TIMEOUT;
	}
	var id = action._id = AjxTimedAction._nextActionId++;
	AjxTimedAction._pendingActions[id] = action;
	var actionStr = "AjxTimedAction._exec(" + id + ")";
	action._tid = window.setTimeout(actionStr, timeout);
	return action._id;
};

AjxTimedAction.cancelAction =
function(actionId) {
	var action = AjxTimedAction._pendingActions[actionId];
	if (action) {
		window.clearTimeout(action._tid);
		delete AjxTimedAction._pendingActions[actionId];
		delete action._tid;
	}
};

AjxTimedAction._exec =
function(actionId) {

	try {

	var action = AjxTimedAction._pendingActions[actionId];
	if (action) {
		delete AjxTimedAction._pendingActions[actionId];
		delete action._tid;
	    action._runResult = action.run();
	}

	} catch (ex) {
		AjxException.reportScriptError(ex);
	}
};

