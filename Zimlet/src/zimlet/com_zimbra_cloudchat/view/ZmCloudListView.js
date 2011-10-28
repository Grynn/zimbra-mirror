function ZmCloudListView(zimlet, model, controller) {
	this.zimlet = zimlet;
	this.model = model;
	this.controller = controller;

	if(controller) {
		this.routingKey = controller.routingKey ? controller.routingKey : "";
	}
	this.isRendered = false;
	this._oldIsTypingCharCount = 0;
}

ZmCloudListView.prototype.setCloudChatFolder = function(folder) {
	this.folder = folder;
};

ZmCloudListView.prototype.setSendBtnAndInputField = function(sendBtn, inputFieldId) {
	this.inputField = document.getElementById(inputFieldId);
	var callback = AjxCallback.simpleClosure(this._inputFieldKeyHdlr, this);
	Dwt.setHandler(this.inputField , DwtEvent.ONKEYUP, callback);

	sendBtn.addSelectionListener(new AjxListener(this,
			this._handleSendBtn));
};

ZmCloudListView.prototype._inputFieldKeyHdlr =
function(ev) {
	var event = ev || window.event;
	if (event.keyCode == undefined) {
		return;
	}
	if (event.keyCode != 13) {//if not enter key
		this._sendIsTyping();
		return;
	}
	this._oldIsTypingCharCount = 0;//reset
	this._handleSendBtn();
};

ZmCloudListView.prototype._sendIsTyping = function() {
	if(this._oldIsTypingCharCount == 0
			|| (this.inputField.value.length - this._oldIsTypingCharCount) > 10
			|| (this.inputField.value.length - this._oldIsTypingCharCount) < 0) {
		this._oldIsTypingCharCount = this.inputField.value.length;
		this.controller.sendUserIsTyping(this.routingKey);
	}

};

ZmCloudListView.prototype._handleSendBtn = function() {
	if(this.inputField.value == "") {
		return;
	}
	this.controller.sendMessage(JSON.stringify({"action":"PUBLISH",routingKey: this.routingKey, data: this.inputField.value}));
	this.inputField.value = "";
};

//override this !
ZmCloudListView.prototype.createView = function() {

};