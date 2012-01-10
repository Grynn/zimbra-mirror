/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite, Network Edition.
 * Copyright (C) 2009, 2010 Zimbra, Inc.  All Rights Reserved.
 * ***** END LICENSE BLOCK *****
 */

/**
* Zimlet constructor.
*/
function ZmVoicePrefs() {
    ZmZimletBase.call(this);

	this.sections = {
		VOICE: {
			title: com_zimbra_voiceprefs.voice,
			icon: "VoicemailApp",
			templateId: "voicemail.Voicemail#VoicePrefsGeneralView",
			priority: 60,
			precondition: ZmSetting.VOICE_ENABLED,
			prefs: [
				ZmSetting.VOICE_ACCOUNTS
			],
			manageDirty: true,
			createView: function(parent, section, controller) {
				return ZmVoicePrefsGeneralController.getInstance().getListView();
			}
		}
	};
	AjxPackage.require({name: "PreferencesCore", callback: new AjxCallback(this, this._registerPrefs)});
	AjxPackage.require({name: "Voicemail", callback: new AjxCallback(this, this._handleVoicemailLoad)});
}

ZmVoicePrefs.prototype = new ZmZimletBase;
ZmVoicePrefs.prototype.constructor = ZmVoicePrefs;

ZmVoicePrefs.prototype.toString = function() {
	return "ZmVoicePrefs";
};

ZmVoicePrefs.prototype._registerPrefs = function() {
	for (var id in this.sections) {
		ZmPref.registerPrefSection(id, this.sections[id]);
	}
};


