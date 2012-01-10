/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite, Network Edition.
 * Copyright (C) 2009, 2010 Zimbra, Inc.  All Rights Reserved.
 * ***** END LICENSE BLOCK *****
 */

/**
 * Creates a new, voice prefs controller.
 * @constructor
 * @class
 * Manages the voice prefs page.
 *
 * @param container		[DwtShell]			the shell
 * @param prefsApp		[ZmPreferencesApp]	the preferences app
 * @param prefsView		[ZmPreferencesView]	the preferences view
 */
ZmVoicePrefsGeneralController = function(container, prefsApp, prefsView) {
	ZmVoicePrefsController.call(this, container, prefsApp, prefsView);
	var section = ZmPref.getPrefSectionWithPref(ZmSetting.VOICE_ACCOUNTS);
	this._listView = new ZmVoicePrefsGeneralPage(prefsView._parent, section, this);
	this._editPhoneDlg = new ZmVoiceEditPhoneDlg(prefsView);
};

ZmVoicePrefsGeneralController.prototype = new ZmVoicePrefsController();
ZmVoicePrefsGeneralController.prototype.constructor = ZmVoicePrefsGeneralController;

ZmVoicePrefsGeneralController.prototype.toString = function() {
	return "ZmVoicePrefsGeneralController";
};

ZmVoicePrefsGeneralController.getInstance = function() {
	if (!ZmVoicePrefsGeneralController._INSTANCE) {
        var prefsView = AjxDispatcher.run("GetPrefController").getPrefsView();
        var prefsApp = appCtxt.getApp(ZmApp.PREFERENCES);
        ZmVoicePrefsGeneralController._INSTANCE = new ZmVoicePrefsGeneralController(this._container, prefsApp, prefsView);
	}
	return ZmVoicePrefsGeneralController._INSTANCE;
};


ZmVoicePrefsGeneralController.prototype.deletePhone = function(phone) {
	this._updatePhone({phone: phone, newPhoneNumber: "", newLabel: ""});
};

ZmVoicePrefsGeneralController.prototype.editPhone = function(phone) {
	this._editPhoneDlg.setPhone(phone);
	this._editPhoneDlg.setEditPhoneCallback(new AjxCallback(this, this._getNewPhoneInfo));
	this._editPhoneDlg.popup();
};

ZmVoicePrefsGeneralController.prototype._getNewPhoneInfo = function(params) {
	var phone = params.phone;
	var newLabel = params.newLabel;
	var newPhoneNumber = params.newPhoneNumber;
	if(newLabel != "" && newPhoneNumber != "" && !(newLabel == phone.label && newPhoneNumber == phone.name)) {
		this._updatePhone(params);
	}
};

ZmVoicePrefsGeneralController.prototype.updatePIN = function(params) {
	var phone = params.phone;
	var soapDoc = AjxSoapDoc.create("ModifyVoiceMailPINRequest", "urn:zimbraVoice");
	appCtxt.getApp(ZmApp.VOICE).setStorePrincipal(soapDoc);
	var node = soapDoc.set("phone");
	node.setAttribute("name", phone.name);
 	soapDoc.set("oldPin", params.oldPin, node);
	soapDoc.set("pin", params.newPin, node);
	appCtxt.getAppController().sendRequest({soapDoc:soapDoc, noBusyOverlay:false, asyncMode:true, callback: (new AjxCallback(this, this._handleUpdatePinResponse, params))});
};

ZmVoicePrefsGeneralController.prototype._updatePhone  = function(params) {
	var phone = params.phone;
	var soapDoc = AjxSoapDoc.create("ModifyFromNumRequest", "urn:zimbraVoice");
	appCtxt.getApp(ZmApp.VOICE).setStorePrincipal(soapDoc);
	var node = soapDoc.set("phone");
	node.setAttribute("id", phone.id);
	node.setAttribute("oldPhone", phone.name);
	node.setAttribute("phone", params.newPhoneNumber);
	node.setAttribute("label", params.newLabel);
	node.setAttribute("numpublishable", phone.allProps.numpublishable);
	appCtxt.getAppController().sendRequest({soapDoc:soapDoc, noBusyOverlay:false, asyncMode:true, callback: (new AjxCallback(this, this._handleUpdatePhoneResponse, params))});
};

ZmVoicePrefsGeneralController.prototype._handleUpdatePhoneResponse =
function(params, result) {
	var response = result.getResponse();
	if(!response.ModifyFromNumResponse || !response.ModifyFromNumResponse.phone) {
		this.displayErrorMessage(com_zimbra_voiceprefs.couldNotEditOrUpdatePhone, com_zimbra_voiceprefs.couldNotEditOrUpdatePhone, com_zimbra_voiceprefs.voiceError);
		return;
	}
	this._listView.updatePhone(params);
};

ZmVoicePrefsGeneralController.prototype._handleUpdatePinResponse =
function(params, result) {
	var response = result.getResponse();
	if(!response.ModifyVoiceMailPINResponse || !response.ModifyVoiceMailPINResponse.phone) {
		this.displayErrorMessage(com_zimbra_voiceprefs.couldNotUpdatePIN, com_zimbra_voiceprefs.couldNotUpdatePIN, com_zimbra_voiceprefs.voiceError);
	} else {
		appCtxt.setStatusMsg(com_zimbra_voiceprefs.voicePINUpdated);
	}

};

ZmVoicePrefsGeneralController.prototype.displayErrorMessage =
function(msg, data, title) {
	var dlg = appCtxt.getErrorDialog();
	dlg.reset();
	dlg.setMessage(msg, data, DwtMessageDialog.WARNING_STYLE, title);
	dlg.popup(null, true);
};

