function ZmCloudChatSocket(nodeServer) {
    this.nodeServer = nodeServer;
    this.onConnectEvent = new ZmCloudChatEvent(this);
    this.onReconnectEvent = new ZmCloudChatEvent(this);
    this.onMessageEvent = new ZmCloudChatEvent(this);
    this.onDisconnectEvent = new ZmCloudChatEvent(this);
    this.onReconnectFailedEvent = new ZmCloudChatEvent(this);
    this.onChatRequestEvent = new ZmCloudChatEvent(this);
    this.onLoggedInEvent = new ZmCloudChatEvent(this);
}

ZmCloudChatSocket.prototype.removeAllListeners = function() {
	this.onConnectEvent.removeAllListeners();
    this.onReconnectEvent.removeAllListeners();
    this.onMessageEvent.removeAllListeners();
    this.onDisconnectEvent.removeAllListeners();
    this.onReconnectFailedEvent.removeAllListeners();
    this.onChatRequestEvent.removeAllListeners();
    this.onLoggedInEvent.removeAllListeners();
};

ZmCloudChatSocket.prototype.connect = function() {
    this._io = io.connect(this.nodeServer, {
											'force new connection':true,
											'remember transport': false,
											'try multiple transports': false,
											 transports: ["xhr-polling"]});

    var ZDS = this;
    this._io.on('connect',
    function(message) {
        ZDS.onConnectEvent.notify(message);
    });
    this._io.on('reconnect',
    function(message) {
        ZDS.onReconnectEvent.notify(message);
    });

    this._io.on('message',
    function(message) {
        ZDS.onMessageEvent.notify(message);
    });

    this._io.on('logged_in',
    function(message) {
        ZDS.onLoggedInEvent.notify(message);
    });

    this._io.on('disconnect',
    function(message) {
        ZDS.onDisconnectEvent.notify(message);
    });
    this._io.on('reconnect_failed',
    function(message) {
        ZDS.onReconnectFailedEvent.notify(message);
    });

};

ZmCloudChatSocket.prototype.disconnect = function() {
    this._io.disconnect();
};

ZmCloudChatSocket.prototype.send = function(message, messageType) {
    messageType = messageType ? messageType: "message";
    this._io.emit(messageType, message);
};

ZmCloudChatSocket.prototype.close = function() {
    this._io.close;
};

ZmCloudChatSocket.prototype.getSocket = function() {
    return this._io;
};

ZmCloudChatSocket.prototype.isConnected = function() {
    return this._io.socket.connected;
};
