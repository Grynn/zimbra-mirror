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

ZmClick2CallVoicePINDlg = function(zimlet) {
	if(!zimlet) {
		return;
	}
    this.zimlet = zimlet;
    this._dialogView = new DwtComposite(appCtxt.getShell());
	this._createView();
	var updatePINBtn = new DwtDialog_ButtonDescriptor(ZmClick2CallVoicePINDlg.UPDATE_PIN_BTN_ID, this.zimlet.getMessage("updatePin"), DwtDialog.ALIGN_RIGHT);

    DwtDialog.call(this, {
        parent: appCtxt.getShell(),
		title: this.zimlet.getMessage("voicePINDlgTitle"),
        view: this._dialogView,
        standardButtons: [DwtDialog.CANCEL_BUTTON],
			extraButtons : [updatePINBtn]
	});
	this.setButtonListener(ZmClick2CallVoicePINDlg.UPDATE_PIN_BTN_ID, new AjxListener(this, this._updateBtnHandler));
	this._pinField = document.getElementById("click2CallVoicePINDlg_PIN_field");
	this._confirmPinField = document.getElementById("click2CallVoicePINDlg_PIN_confirm_field");

	this._addTabControl();

	//regex to check for only-digits
	this.RE = new RegExp("\\d+", "g");
};

ZmClick2CallVoicePINDlg.prototype = new DwtDialog;
ZmClick2CallVoicePINDlg.prototype.constructor = ZmClick2CallVoicePINDlg;

ZmClick2CallVoicePINDlg.UPDATE_PIN_BTN_ID = "click2call_voice_pin_dlg_update_btn";
ZmClick2CallVoicePINDlg.PIN_FIELD_ID = "click2CallVoicePINDlg_PIN_field";
ZmClick2CallVoicePINDlg.CONFIRM_PIN_FIELD_ID = "click2CallVoicePINDlg_PIN_confirm_field";


ZmClick2CallVoicePINDlg.prototype._createView = function() {
var subs = {
		pinStr : this.zimlet.getMessage("pinStr"),
		confirmPINStr : this.zimlet.getMessage("confirmPINStr"),
		pinFieldId : ZmClick2CallVoicePINDlg.PIN_FIELD_ID,
		confirmPinFieldId : ZmClick2CallVoicePINDlg.CONFIRM_PIN_FIELD_ID
	};
	this._dialogView.getHtmlElement().innerHTML =  AjxTemplate.expand("com_zimbra_click2call.templates.ZmClick2Call#voicePINDlg", subs);
};

ZmClick2CallVoicePINDlg.prototype._updateBtnHandler = function() {
	if(this._pinField && this._confirmPinField){
		if(this._pinField.value == "" && this._confirmPinField.value == "") {
			return false;
		} else if(this._pinField.value != this._confirmPinField.value) {
			this.zimlet.displayErrorMessage(this.zimlet.getMessage("pinAndConfirmPinDidntMatch"), this.zimlet.getMessage("pinAndConfirmPinDidntMatch"),
			this.zimlet.getMessage("invalidPin"));
			return false;
		}  else if(!this.isPINDigits(this._pinField.value)) {
			this.zimlet.displayErrorMessage(this.zimlet.getMessage("pinCanOnlyBeDigits"), this.zimlet.getMessage("pinCanOnlyBeDigits"),
			this.zimlet.getMessage("invalidPin"));
			return false;
		} else {
			this._updatePIN(this._pinField.value);
		}
	}

};

ZmClick2CallVoicePINDlg.prototype._addTabControl = function() {
	this._tabGroup.removeAllMembers();
	this._tabGroup.addMember(this._pinField, 0);
	this._tabGroup.addMember(this._confirmPinField, 1);
	this._tabGroup.addMember(this.getButton(ZmClick2CallVoicePINDlg.UPDATE_PIN_BTN_ID), 2);
	this._tabGroup.addMember(this.getButton(ZmClick2CallVoicePINDlg.CANCEL_BUTTON), 3);
};

ZmClick2CallVoicePINDlg.prototype.isPINDigits = function(pin) {
	return this.RE.test(pin);
};

ZmClick2CallVoicePINDlg.prototype._updatePIN = function(pin) {
	//todo Send this pin info to voice server (currently we don't have server support yet.
};