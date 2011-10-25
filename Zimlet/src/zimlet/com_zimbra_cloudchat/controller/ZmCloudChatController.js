function ZmCloudChatController(zimlet, model, socket) {
	this.zimlet = zimlet;
	this.model = model;
	this._socket = socket;
}

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

ZmCloudChatController.prototype._getZimbraHostName = function() {
	var soapURL = appCtxt.getSettings().getInfoResponse.soapURL;
	var hostName = soapURL.replace("http://","").replace("https://", "");
	hostName = hostName.split("/")[0];
	if(hostName.indexOf(":") >0) {
		hostName = hostName.split(":")[0];
	}
	return hostName;
};