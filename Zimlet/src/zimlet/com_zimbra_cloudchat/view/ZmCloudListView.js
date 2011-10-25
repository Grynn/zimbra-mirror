function ZmCloudListView(zimlet, model, controller, parentViewId, userListViewId) {
	this.controller = controller;
	this.model = model;
	this.zimlet = zimlet;
	this.parentViewId = parentViewId;
	this.userListViewId = userListViewId;
	if(controller) {
		this.routingKey = controller.routingKey ? controller.routingKey : "";
	}
	this.isRendered = false;
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
		return;
	}
	this._handleSendBtn();
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