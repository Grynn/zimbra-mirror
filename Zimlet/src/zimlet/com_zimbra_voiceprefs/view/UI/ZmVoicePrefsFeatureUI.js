/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite, Network Edition.
 * Copyright (C) 2009, 2010 Zimbra, Inc.  All Rights Reserved.
 * ***** END LICENSE BLOCK *****
 */
ZmVoicePrefsFeatureUI = function(view) {
	this._view = view;
}

ZmVoicePrefsFeatureUI.prototype.setFeature =
function(feature) {
	this._feature = feature;
	this.show(feature);
	if (this._checkbox) {
		this._checkbox.setSelected(feature.isActive);
		this._checkbox.setEnabled(feature.isSubscribed);
	}
	this.setEnabled(feature.isActive);
};

ZmVoicePrefsFeatureUI.prototype.getFeature =
function() {
	if (this._feature) {
		var result = this._feature.createProxy();
		result.isActive = this._checkbox ? this._checkbox.isSelected() : true;
		return result;
	}
	return null;
};

ZmVoicePrefsFeatureUI.prototype.isDirty =
function() {
	if (!this._feature || !this._feature.isSubscribed) {
		return false;
	}
	if (this._checkbox && (this._feature.isActive != this._checkbox.isSelected())) {
		return true;
	}
	return this._isValueDirty();
};

ZmVoicePrefsFeatureUI.prototype._createCheckbox =
function(text, id) {
	this._checkbox = new DwtCheckbox({parent:this._view});
	this._checkbox.setText(text);
	this._checkbox.replaceElement(id);
	this._checkbox.addSelectionListener(new AjxListener(this, this._checkboxListener));
};

ZmVoicePrefsFeatureUI.prototype._checkboxListener =
function(ev) {
	this.setEnabled(this._checkbox.isSelected());
};

ZmVoicePrefsFeatureUI.prototype._populateEmailComboBox =
function(comboBox) {
    comboBox.removeAll();
    var accountAddress = appCtxt.get(ZmSetting.USERNAME);
    this._comboBox.add(accountAddress, false);
};

ZmVoicePrefsFeatureUI.prototype._isComboBoxValid =
function(comboBox) {
	return comboBox && comboBox.input && comboBox.input.isValid() !== null;
};

ZmVoicePrefsFeatureUI.prototype._validateComboBox =
function(comboBox, errorList, message) {
	if (!this._isComboBoxValid(comboBox)) {
		errorList.push(message);
	}
};

ZmVoicePrefsFeatureUI.prototype._clearField =
function(field) {
	if (field instanceof DwtComboBox || field instanceof DwtInputField) {
		field.setValue(null, true);
		field.focus();
	}
}

ZmVoicePrefsFeatureUI.prototype.getFaqLink =
function() {
	return AjxMessageFormat.format(com_zimbra_voiceprefs.FAQLink, [com_zimbra_voiceprefs.FAQURL]);
}

ZmVoicePrefsFeatureUI.prototype.showDialog =
function(text, okCallback) {
	var shell = appCtxt.getShell();
	var dialog = new ZmDialog({parent:shell, title:ZmMsg.errorCap});
	dialog.setContent(text);
	if (okCallback instanceof AjxCallback) {
		var okListener = new AjxListener(dialog, function() {
			okCallback.run();
			this.popdown();
		});
		dialog.setButtonListener(DwtDialog.OK_BUTTON, okListener);
	}
	dialog.popup();
};

ZmVoicePrefsFeatureUI.prototype.showDialogWithFAQ =
function(text, okCallback) {
	if (AjxUtil.isString(text)) {
		var msg = AjxStringUtil.trim(text);
		if (msg.length > 0) {
			if (msg.indexOf("{0}") == -1) {
				if (!msg.match(/\.$/))
					msg += ".";
//				msg += " ";
//				msg += this.getFaqLink();
			} else {
				msg = AjxMessageFormat.format(msg, [this.getFaqLink()]);
			}
			this.showDialog(msg, okCallback);
		}
	}
};

// "Abstract" methods:
ZmVoicePrefsFeatureUI.prototype.getName =
function() {
	//alert('ZmVoicePrefsFeatureUI.prototype.getName');
};
ZmVoicePrefsFeatureUI.prototype._initialize =
function(id) {
	//alert('ZmVoicePrefsFeatureUI.prototype._initialize');
};
ZmVoicePrefsFeatureUI.prototype.setEnabled =
function(enabled) {
	//alert('ZmVoicePrefsFeatureUI.prototype.setEnabled ' + enabled);
};
ZmVoicePrefsFeatureUI.prototype.validate =
function(errors) {
	// No-op.
};
