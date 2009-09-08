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


function ZMTB_AjxTimedAction(obj, func, args) {
	ZMTB_AjxCallback.call(this, obj, func, args);
	this._tid = -1;
	this._id = -1;
}
ZMTB_AjxTimedAction.prototype = new ZMTB_AjxCallback();
ZMTB_AjxTimedAction.prototype.constructor = ZMTB_AjxTimedAction;

ZMTB_AjxTimedAction.prototype.toString = 
function() {
	return "ZMTB_AjxTimedAction";
};

ZMTB_AjxTimedAction._pendingActions = {};
ZMTB_AjxTimedAction._nextActionId = 0;

ZMTB_AjxTimedAction.scheduleAction =
function(action, timeout){
	// if tid already exists, cancel previous timeout before setting a new one
	if (action._tid) {
		ZMTB_AjxTimedAction.cancelAction(action._id);
	}

	var id = action._id = ZMTB_AjxTimedAction._nextActionId++;
	ZMTB_AjxTimedAction._pendingActions[id] = action;
	var actionStr = "ZMTB_AjxTimedAction._exec(" + id + ")";
	action._tid = window.setTimeout(actionStr, timeout ? timeout : 0); // mac no like null/void
	return action._id;
};

ZMTB_AjxTimedAction.cancelAction =
function(actionId) {
	var action = ZMTB_AjxTimedAction._pendingActions[actionId];
	if (action) {
		window.clearTimeout(action._tid);
		delete ZMTB_AjxTimedAction._pendingActions[actionId];
		delete action._tid;
	}
};

ZMTB_AjxTimedAction._exec =
function(actionId) {
	var action = ZMTB_AjxTimedAction._pendingActions[actionId];
	delete ZMTB_AjxTimedAction._pendingActions[actionId];
	delete action._tid;
	action.run();
};