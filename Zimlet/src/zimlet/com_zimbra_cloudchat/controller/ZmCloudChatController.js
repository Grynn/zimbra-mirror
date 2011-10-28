function ZmCloudChatController(zimlet, model, socket) {
	this.zimlet = zimlet;
	this.model = model;
	this._socket = socket;
	this.view; //chatlistview set itself as the view
}

ZmCloudChatController.prototype.setView = function(view) {
	this.view = view;
};

ZmCloudChatController.prototype.sendMessage = function(message) {
	this._socket.send(message);
};

ZmCloudChatController.prototype.resetSocketListeners = function() {
	this._socket.onConnectEvent.removeAllListeners();
		this._socket.onConnectEvent.attach(AjxCallback.simpleClosure(
			this._displayConnectionData, this, "connected"));

	
	this._socket.onMessageEvent.removeAllListeners();
	this._socket.onMessageEvent.attach(AjxCallback.simpleClosure(
			this.model.addMessage, this.model));
	
	this._socket.onDisconnectEvent.removeAllListeners();
	this._socket.onDisconnectEvent.attach(AjxCallback.simpleClosure(
			this._displayConnectionData, this, "disconnected"));
	
	this._socket.onReconnectFailedEvent.removeAllListeners();
	this._socket.onReconnectFailedEvent.attach(AjxCallback.simpleClosure(
			this._displayConnectionData, this, "Reconnection failed"));
};

ZmCloudChatController.prototype.displayMessage = function(message) {
	this.model.addMessage(message);
};

ZmCloudChatController.prototype.displayConnectionMessage = function(message) {
	this.model.addConnectionMessage(message);
};