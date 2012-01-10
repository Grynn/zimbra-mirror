/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite, Network Edition.
 * Copyright (C) 2009, 2010 Zimbra, Inc.  All Rights Reserved.
 * ***** END LICENSE BLOCK *****
 */
ZmVoicePrefsGeneralUI = function(view) {
	ZmVoicePrefsFeatureUI.call(this, view);
}

ZmVoicePrefsGeneralUI.prototype = new ZmVoicePrefsFeatureUI;
ZmVoicePrefsGeneralUI.prototype.constructor = ZmVoicePrefsGeneralUI;

ZmVoicePrefsGeneralUI.prototype.toString =
function() {
	return "ZmVoicePrefsGeneralUI";
}

ZmVoicePrefsGeneralUI.prototype._initialize =
function(id) {
	var deleteButtonDiv = document.getElementById(id+"_DELETE");
	if (deleteButtonDiv) {
		var button = new DwtButton({parent:this._view});
		button.setText(ZmMsg.del);
		button.setEnabled(true);
		button.addSelectionListener(new AjxListener(this, this._handleDeleteButton));
		button.replaceElement(deleteButtonDiv);
		this._deleteButton = button;
	}
	var editButtonDiv = document.getElementById(id+"_EDIT");
	if (editButtonDiv) {
		button = new DwtButton({parent:this._view});
		button.setText(ZmMsg.edit);
		button.setEnabled(true);
		button.addSelectionListener(new AjxListener(this, this._handleEditButton));
		button.replaceElement(editButtonDiv);
		this._editButton = button;
	}
	var updatePinButtonDiv = document.getElementById(id+"_UPDATE_PIN");
	if (updatePinButtonDiv) {
		button = new DwtButton({parent:this._view});
		button.setText(com_zimbra_voiceprefs.update);
		button.setEnabled(true);
		button.addSelectionListener(new AjxListener(this, this._handleUpdatePinBtn, id));
		button.replaceElement(updatePinButtonDiv);
		this._updatePinButton = button;
	}
};

ZmVoicePrefsGeneralUI.prototype._handleUpdatePinBtn =
		function(id) {
			var phone = this._view._item;
			if(!phone.hasVoiceMail) {
				appCtxt.setStatusMsg(com_zimbra_voiceprefs.pleaseSelectVoiceMailAccountAbove, ZmStatusView.LEVEL_WARNING);
				return;
			}
			var oldPin = document.getElementById(id+"_OLD_PIN").value;
			var newPin = document.getElementById(id+"_NEW_PIN").value;
			var confirmPin = document.getElementById(id+"_CONFIRM_NEW_PIN").value;
			if(newPin != confirmPin) {
				appCtxt.setStatusMsg(com_zimbra_voiceprefs.newPinAndConfirmPinDoesNotMatch, ZmStatusView.LEVEL_WARNING);
				return;
			}
			if(oldPin != "" && newPin != "") {
				this._view._controller.updatePIN({phone: phone, oldPin: oldPin, newPin: newPin});
			}
		};

ZmVoicePrefsGeneralUI.prototype._handleEditButton =
function() {
	var phone = this._view._item;
	if(phone.allProps && !phone.allProps.editable) {
		appCtxt.setStatusMsg(com_zimbra_voiceprefs.phoneNotEditable, ZmStatusView.LEVEL_WARNING);
		return;
	}
	this._view._controller.editPhone(phone);
};

ZmVoicePrefsGeneralUI.prototype._handleDeleteButton =
function() {
	var phone = this._view._item;
	if(phone.allProps && !phone.allProps.editable) {
		appCtxt.setStatusMsg(com_zimbra_voiceprefs.phoneNotEditable, ZmStatusView.LEVEL_WARNING);
		return;
	}
	this._view._controller.deletePhone(phone);
};

ZmVoicePrefsGeneralUI.prototype._editPhoneHandler =
function(params) {
	var phone = params.phone;
	this._view._list.removeItem(phone);
	phone.label = params.newLabel;
	phone.name = params.newPhoneNumber;
	this._view._list.addItem(phone);
};
