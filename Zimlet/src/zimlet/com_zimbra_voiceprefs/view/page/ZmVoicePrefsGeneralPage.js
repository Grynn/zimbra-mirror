ZmVoicePrefsGeneralPage = function(parent, section, controller) {
	ZmVoicePrefsPage.call(this, parent, section, controller);

	this._controller = controller;
	this._hasRendered = false;
	this._item = null;

	this._ui = [new ZmVoicePrefsGeneralUI(this)];
	this._changes = null;
	
	this._template = "voicemail.Voicemail#VoicePrefsGeneralView";
	this._title = [ZmMsg.zimbraTitle, controller.getApp().getDisplayName(), this._section && this._section.title].join(": ");
};

ZmVoicePrefsGeneralPage.prototype = new ZmVoicePrefsPage;
ZmVoicePrefsGeneralPage.prototype.constructor = ZmVoicePrefsGeneralPage;

ZmVoicePrefsGeneralPage.prototype.toString =
function() {
	return "ZmVoicePrefsGeneralPage";
};

ZmVoicePrefsGeneralPage.prototype.hasRendered =
function () {
	return this._hasRendered;
};

ZmVoicePrefsGeneralPage.prototype.updatePhone =
function (params) {
	var phone = params.phone;
	this._list.removeItem(phone);
	phone.label = params.newLabel;
	phone.name = params.newPhoneNumber;
	this._list.addItem(phone);
};

