/*
 * ***** BEGIN LICENSE BLOCK *****
 * Version: ZAPL 1.1
 * 
 * The contents of this file are subject to the Zimbra AJAX Public
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

function AjxTimedAction(obj, method, params) {
	this.obj = obj;
	this.method = method;
	this.params = params ? params : new AjxVector();
	this._tid = -1;
	this._id = -1;
}

AjxTimedAction.prototype.toString = 
function() {
	return "AjxTimedAction";
}

AjxTimedAction._pendingActions = new Object();
AjxTimedAction._nextActionId = 0;

AjxTimedAction.scheduleAction =
function(action, timeout){
	var id = action._id = AjxTimedAction._nextActionId++;
	AjxTimedAction._pendingActions[id] = action;
	var actionStr = "AjxTimedAction._exec(" + id + ")";
	action._tid = window.setTimeout(actionStr, timeout);
	return action._id;
}

AjxTimedAction.cancelAction =
function(actionId) {
	var action = AjxTimedAction._pendingActions[actionId];
	if (action) {
		window.clearTimeout(action._tid);
		delete AjxTimedAction._pendingActions[actionId];
	}
}

AjxTimedAction._exec =
function(actionId) {
	var action = AjxTimedAction._pendingActions[actionId];
	delete AjxTimedAction._pendingActions[actionId];
	if (action) {
		if (action.obj)
			action.method.apply(action.obj, action.params.getArray());
		else
			action.method(action.params.getArray());
	}
}
