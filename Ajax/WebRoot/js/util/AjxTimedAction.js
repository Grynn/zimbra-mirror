function AjxTimedAction() {
	this.obj = null;
	this.method = null;
	this.params = new AjxVector();
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
