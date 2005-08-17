function LsTimedAction() {
	this.obj = null;
	this.method = null;
	this.params = new LsVector();
	this._tid = -1;
	this._id = -1;
}

LsTimedAction.prototype.toString = 
function() {
	return "LsTimedAction";
}

LsTimedAction._pendingActions = new Object();
LsTimedAction._nextActionId = 0;

LsTimedAction.scheduleAction =
function(action, timeout){
	var id = action._id = LsTimedAction._nextActionId++;
	LsTimedAction._pendingActions[id] = action;
	var actionStr = "LsTimedAction._exec(" + id + ")";
	action._tid = window.setTimeout(actionStr, timeout);
	return action._id;
}

LsTimedAction.cancelAction =
function(actionId) {
	var action = LsTimedAction._pendingActions[actionId];
	if (action) {
		window.clearTimeout(action._tid);
		delete LsTimedAction._pendingActions[actionId];
	}
}

LsTimedAction._exec =
function(actionId) {
	var action = LsTimedAction._pendingActions[actionId];
	delete LsTimedAction._pendingActions[actionId];
	if (action) {
		if (action.obj)
			action.method.apply(action.obj, action.params.getArray());
		else
			action.method(action.params.getArray());
	}
}
