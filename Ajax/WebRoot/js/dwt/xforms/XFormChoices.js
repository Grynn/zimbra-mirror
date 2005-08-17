function XFormChoices(choiceObject, type, valueProperty, labelProperty) {
	if (arguments.length == 0) return;
	
	if (choiceObject != null) this._choiceObject = choiceObject;
	if (type != null) this._type = type;
	if (valueProperty != null) this._valueProperty = valueProperty;
	if (labelProperty != null) this._labelProperty = labelProperty;
	
	this._choiceChangeTime = new Date().getTime();
	this._lastNormalizeTime = 0;
	
	if (this._type == XFormChoices.AUTO) this.autoDetermineType();
	
	this._eventMgr = new AjxEventMgr();
}
XFormChoices.prototype = new Object();
XFormChoices.prototype.constructor = XFormChoices;


//
//	static methods
//
XFormChoices.normalizeChoices = function (choices, type, valueProperty, labelProperty) {
	var values;
	var labels;
	switch (type) {
		case XFormChoices.SIMPLE_LIST:
			values = [].concat(choices)
			labels = [].concat(choices)
		
			break;


		case XFormChoices.OBJECT_LIST:
			values = []; labels = [];
			if (valueProperty == null) valueProperty = "value";
			if (labelProperty == null) labelProperty = "label";
			for (var i = 0; i < choices.length; i++) {
				values.push(choices[i][valueProperty]);
				labels.push(choices[i][labelProperty]);
			}
		
			break;


		case XFormChoices.HASH:
			values = []; labels = [];
			for (var prop in choices) {
				values.push(prop);
				labels.push(choices[prop]);
			}
		
			break;
	}
	return {values:values, labels:labels};
}


// constants
XFormChoices.AUTO = "auto";
XFormChoices.SIMPLE_LIST = "list";
XFormChoices.HASH = "hash";
XFormChoices.OBJECT_LIST = "object";


// type defaults
XFormChoices.prototype._type = XFormChoices.AUTO;
XFormChoices.prototype._valueProperty = "value";
XFormChoices.prototype._labelProperty = "label";



XFormChoices.prototype.autoDetermineType = function () {
	var type;

	var choices = this._choiceObject;
	if (choices) {
		if (AjxUtil.isArray(choices)) {
			var firstChoice = choices[0];
			if (AjxUtil.isObject(firstChoice)) {
				type = XFormChoices.OBJECT_LIST;
			} else {
				type = XFormChoices.SIMPLE_LIST;
			}
		} else if (AjxUtil.isObject(choices)) {
			type = XFormChoices.HASH;
		}
	}
	
	if (type == null) type = XFormChoices.SIMPLE_LIST;
	this._type = type;
}

XFormChoices.prototype.setChoices = function (choiceObject) {
	this._choiceObject = choiceObject;
}

XFormChoices.prototype.getChoices = function () {
	// only normalize if dirty
	if (this._lastNormalizeTime == this._choiceChangeTime && this.$normalizedChoices) {
		return this.$normalizedChoices;
	}
	this._lastNormalizeTime = this._choiceChangeTime;

	this.$normalizedChoices = XFormChoices.normalizeChoices(this._choiceObject, this._type, this._valueProperty, this._labelProperty);
	return this.$normalizedChoices;
}


XFormChoices.prototype.dirtyChoices = function () {
	this._choiceChangeTime = new Date().getTime();
	this.notifyListeners(DwtEvent.XFORMS_CHOICES_CHANGED, {});
}






//
//	listening -- these are from DwtControl  -- make an installable interface?
//
XFormChoices.prototype.addListener = function(eventType, listener) {
	return this._eventMgr.addListener(eventType, listener); 	
}

XFormChoices.prototype.notifyListeners = function(eventType, event) {
	return this._eventMgr.notifyListeners(eventType, event);
}

XFormChoices.prototype.isListenerRegistered = function(eventType) {
	return this._eventMgr.isListenerRegistered(eventType);
}

XFormChoices.prototype.removeListener =  function(eventType, listener) {
	return this._eventMgr.removeListener(eventType, listener);
}
