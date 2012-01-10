/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Zimlets
 * Copyright (C) 2009, 2010, 2011 Zimbra, Inc.
 *
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.3 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 * @Author Raja Rao DV
 *
 */

ZmVoiceEditPhoneDlg = function(arguments) {
	if(!arguments) {
		return;
	}
    this._dialogView = new DwtComposite(appCtxt.getShell());
	this._createView();

    DwtDialog.call(this, {
        parent: appCtxt.getShell(),
		title: com_zimbra_voiceprefs.editPhone,
        view: this._dialogView,
        standardButtons: [DwtDialog.OK_BUTTON, DwtDialog.CANCEL_BUTTON]
	});
	this.setButtonListener(DwtDialog.OK_BUTTON, new AjxListener(this, this._editPhoneListener));
	this._nameField = document.getElementById("voice_phone_name_field_id");
	this._phoneField = document.getElementById("voice_phone_field_id");

	this._editTabControl();

	//regex to check for only-digits
	this.RE = new RegExp("\\d+", "g");
};

ZmVoiceEditPhoneDlg.prototype = new DwtDialog;
ZmVoiceEditPhoneDlg.prototype.constructor = ZmVoiceEditPhoneDlg;

ZmVoiceEditPhoneDlg.PHONE_FIELD_ID = "voice_phone_field_id";
ZmVoiceEditPhoneDlg.PHONE_NAME_FIELD_ID = "voice_phone_name_field_id";


ZmVoiceEditPhoneDlg.prototype._createView = function() {
var subs = {
		nameFieldId : ZmVoiceEditPhoneDlg.PHONE_NAME_FIELD_ID,
		phoneFieldId : ZmVoiceEditPhoneDlg.PHONE_FIELD_ID
	};
	this._dialogView.getHtmlElement().innerHTML =  AjxTemplate.expand("voicemail.Voicemail#voiceEditPhoneDlg", subs);
};

ZmVoiceEditPhoneDlg.prototype._editPhoneListener = function() {
	if(this._editPhonePostCallback) {
		this._editPhonePostCallback.run({phone: this._phone, newLabel: this._nameField.value, newPhoneNumber: this._phoneField.value});
	}
	this.popdown();
};

ZmVoiceEditPhoneDlg.prototype._editTabControl = function() {
	this._tabGroup.removeAllMembers();
	this._tabGroup.addMember(this._nameField, 1);
	this._tabGroup.addMember(this._phoneField, 2);
	this._tabGroup.addMember(this.getButton(DwtDialog.OK_BUTTON), 3);
	this._tabGroup.addMember(this.getButton(DwtDialog.CANCEL_BUTTON), 4);
};

ZmVoiceEditPhoneDlg.prototype.popup = function() {
	DwtDialog.prototype.popup.call(this);
 	if(this._phone) {
		 this._nameField.value = this._phone.label;
		 this._phoneField.value = this._phone.name;
	}
};

ZmVoiceEditPhoneDlg.prototype.setEditPhoneCallback = function(callback) {
	this._editPhonePostCallback = callback;
};

ZmVoiceEditPhoneDlg.prototype.setPhone = function(phone) {
	this._phone = phone;
};