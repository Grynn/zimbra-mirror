/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite, Network Edition.
 * Copyright (C) 2009, 2010 Zimbra, Inc.  All Rights Reserved.
 * ***** END LICENSE BLOCK *****
 */
ZmVoicePrefsPage = function(parent, section, controller) {
	if (arguments.length==0) return;
	DwtTabViewPage.call(this, parent, "ZmVoicePrefsPage ZmPreferencesPage");
	//ZmPreferencesPage.call(this, parent, section, controller);
	this._className = "ZmVoicePrefsPage ZmPreferencesPage";

	this._controller = controller;
	this._hasRendered = false;
	this._item = null;
	this._section = section;
	this._title = [ZmMsg.zimbraTitle, controller.getApp().getDisplayName(), this._section && this._section.title].join(": ");
	this._ui = [];
	this._changes = null;
	this._myCard = null;
};

ZmVoicePrefsPage.prototype = new DwtTabViewPage;
//ZmVoicePrefsPage.prototype = new ZmPreferencesPage;
ZmVoicePrefsPage.prototype.constructor = ZmVoicePrefsPage;

ZmVoicePrefsPage.prototype.toString =
function() {
	return "ZmVoicePrefsPage";
};

ZmVoicePrefsPage.prototype.hasRendered =
function () {
	return this._hasRendered;
};

ZmVoicePrefsPage.prototype.resetOnAccountChange =
function() {
	this._hasRendered = false;
};

ZmVoicePrefsPage.prototype.getPreSaveCallback =
function() {
	return new AjxCallback(this, this._preSaveCallback);
}

ZmVoicePrefsPage.prototype._preSaveCallback = function(callback) {
	var preSaveCallbacks = this.getPreSaveCallbacks();
	if (preSaveCallbacks && preSaveCallbacks.length > 0) {
		var continueCallback = new AjxCallback(this, this._doPreSave);
		continueCallback.args = [continueCallback, preSaveCallbacks, callback];
		this._doPreSave.apply(this, continueCallback.args);
	} else {
		callback.run(true);
	}
};


ZmVoicePrefsPage.prototype._doPreSave =
function(continueCallback, preSaveCallbacks, callback, noPop, success) {
	// cancel save
	if (success != null && !success) { return; }

	// perform save
	if (preSaveCallbacks.length == 0) {
		callback.run(true);
	}

	// continue pre-save operations
	else {
		var preSaveCallback = preSaveCallbacks.shift();
		preSaveCallback.run(continueCallback);
	}
};

ZmVoicePrefsPage.prototype.getPreSaveCallbacks =
function() {
	var callbacks = [];
	if (this._hasRendered) {
		for (var i = 0; i < this._ui.length; i++) {
			var ui = this._ui[i];
			if (ui && ui.getPreSaveCallback) {
				var callback = ui.getPreSaveCallback();
				if (callback) {
					callbacks.push(callback);
				}
			}
		}
	}
	return callbacks;
};

ZmVoicePrefsPage.prototype.getList =
function() {
	return this._list;
};

ZmVoicePrefsPage.prototype.getUIByName =
function(name) {
	for (var i = 0; i < this._ui.length; i++) {
		var ui = this._ui[i];
		if (ui.getName() == name)
			return ui;
	}
	return null;
};

ZmVoicePrefsPage.prototype.setItem =
function(item) {
	if (item) {
		this._item = item;
		//TODO: Retarded.
		this._getChanges(false);
		this.showItem(item);
	}
};

ZmVoicePrefsPage.prototype.validate =
function(errors) {
	errors = errors || [];
	if (!this._item) {
		return true;
	}
	for(var i = 0, count = this._ui.length; i < count; i++) {
		var ui = this._ui[i];
		if (!ui._checkbox || ui._checkbox.isSelected()) {
			ui.validate(errors);
		}
	}
	this._errors = errors;
	return this._errors.length == 0;
};

ZmVoicePrefsPage.prototype.getErrorMessage =
function() {
	if (!this._errors.length) {
		return null;
	} else {
		return this._errors.join("<br/>").replace(/\{\d+\}/g, "");
	}
};

ZmVoicePrefsPage.prototype.getTitle =
function() {
	return this._title;
};

ZmVoicePrefsPage.prototype.showMe =
function() {
	var prefsController = AjxDispatcher.run("GetPrefController");
	prefsController._resetOperations(prefsController._toolbar, this._section && this._section.id);
	Dwt.setTitle(this._title);

	if (this._hasRendered) return;

	this._handleResponseGetFeaturesObj = new AjxCallback(this, this._handleResponseGetFeatures);
	this._handleErrorGetFeaturesObj = new AjxCallback(this, this._handleErrorGetFeatures);

	var voiceInfoCallback = new AjxCallback(this, this._handleResponseGetVoiceInfo);
	var voiceInfoErrorCallback = new AjxCallback(this, this._handleErrorGetVoiceInfo);
	appCtxt.getApp(ZmApp.VOICE).getVoiceInfo(voiceInfoCallback, voiceInfoErrorCallback);
};



ZmVoicePrefsPage.prototype._handleErrorGetVoiceInfo =
function(ex) {
	if (ex.code == "voice.SECONDARY_NOT_ALLOWED") {
		if (!this._showingErrorMessage) {
			this._showingErrorMessage = true;
			this.getHtmlElement().innerHTML = ZMsg["voice.SECONDARY_NOT_ALLOWED_PREFS"];
		}
		return true;
	}
	return false;
};

ZmVoicePrefsPage.prototype._handleResponseGetVoiceInfo =
function() {
	var id = this._htmlElId;
	var data = { id: id };
	this.getHtmlElement().innerHTML = AjxTemplate.expand(this._template, data);
	this._showingErrorMessage = false;

	// Create the list view and the contents of the detail pane.
	this._list = new ZmVoicePrefsPhoneList(this);
	
	this._list.replaceElement(id + "_list");
	this._list.sortingEnabled = false;

	for(var i = 0, count = this._ui.length; i < count; i++) {
		this._ui[i]._initialize(id);
	}

	this._controller._setup(this);

	this._hasRendered = true;
};


ZmVoicePrefsPage.prototype.isDirty =
function() {
	this._getChanges();
	return this._changes != null;
};

ZmVoicePrefsPage.prototype._getChanges =
function() {
	if (!this._phone) {
		return;
	}
	for(var i = 0, count = this._ui.length; i < count; i++) {
		if (this._ui[i].isDirty()) {
			this._addChange(this._ui[i]);
		}
	}
};

ZmVoicePrefsPage.prototype._addChange =
function(ui) {
	if (!this._changes) {
		this._changes = {};
	}
	if (!this._changes[this._phone.name]) {
		this._changes[this._phone.name] = { phone: this._phone, features: {} };
	}
	var feature = ui.getFeature();
	if (feature)
		this._changes[this._phone.name].features[feature.name] = feature;
};

ZmVoicePrefsPage.prototype.reset =
function() {
	this._changes = null;
	//if (this._item)
		this.showItem(this._item);
};

ZmVoicePrefsPage.prototype.showItem =
function(phone) {
	if (phone instanceof ZmPhone) {
		this._phone = phone;
		phone.getCallFeatures(this._handleResponseGetFeaturesObj, this._handleErrorGetFeaturesObj);
	}
};

ZmVoicePrefsPage.prototype._handleResponseGetFeatures =
function(features, phone) {
	var changedFeatures = (this._changes && this._changes[phone.name]) ? this._changes[phone.name].features : null;
	for(var i = 0, count = this._ui.length; i < count; i++) {
		var featureName = this._ui[i].getName();
		var feature;
		if (changedFeatures && changedFeatures[featureName]) {
			feature = changedFeatures[featureName];
		} else {
			feature = features[featureName];
		}
		if (feature) {
			this._ui[i].setFeature(feature);
		}
	}
};

ZmVoicePrefsPage.prototype._handleErrorGetFeatures =
function(csfeException) {
	for(var i = 0, count = this._ui.length; i < count; i++) {
		var ui = this._ui[i];
		ui.setEnabled(false);
		if (ui._checkbox) {
			ui._checkbox.setEnabled(false);
		}
	}
};

ZmVoicePrefsPage.prototype.addCommand =
function(batchCommand) {
	var first = true;
	for (var i in this._changes) {
		var change = this._changes[i];
		var phone = change.phone;
		var callback = null;
		if (first) {
			if (!this._handleResponseObj) {
				this._handleResponseObj = new AjxCallback(this, this._handleResponseCallFeatures);
			}
			callback = this._handleResponseObj;
			first = false;
		}
		var list = [];
		var features = change.features;
		for (var name in features) {
			list.push(features[name]);
		}
		phone.modifyCallFeatures(batchCommand, list, callback);
	 }
};

ZmVoicePrefsPage.prototype._handleResponseCallFeatures =
function() {
	this._changes = null;
};

ZmVoicePrefsPage.prototype._validatePhoneNumber =
function(value) {
	if (AjxStringUtil.trim(value) == "") {
		throw AjxMsg.valueIsRequired;
	}
	var errors = [];
	if (!this._phone.validate(value, errors)) {
		throw AjxMessageFormat.format(errors[0], [""]);
	}
	return value;
};

ZmVoicePrefsPage.prototype._validatePhoneNumberFct =
function(value) {
	try {
		this._validatePhoneNumber(value);
	} catch (e) {
		return e;
	}
	return null;
}

ZmVoicePrefsPage._validateEmailAddress =
function(value) {
	if (value == "") {
		throw AjxMsg.valueIsRequired;
	} else if (!AjxEmailAddress.isValid(value) || value.length > ZmCallFeature.EMAIL_MAX_LENGTH) {
		throw AjxMsg.invalidEmailAddr;
	}
	return value;
};
