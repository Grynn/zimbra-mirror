function ZmCloudChatListController(app, model, socket, routingKey, users) {
	this.routingKey = routingKey;
	this.app = app;
	this.users = users;
	this.isGroupChat = users.length > 1;
	ZmCloudChatController.call(this, app.zimlet, model, socket);
	this.app.onPresenceEvent.attach(AjxCallback.simpleClosure(this.updateItemPresence, this), "tab_presence_" + routingKey);
}

ZmCloudChatListController.prototype = new ZmCloudChatController;
ZmCloudChatListController.prototype.constructor = ZmCloudChatListController;

ZmCloudChatListController.prototype.updateItemPresence = function (item) {
	this.model.updateItemPresence(item);
};

ZmCloudChatListController.prototype.reconnectWithUser = function () {
	this.app.sendReconnectChatRequest({routingKey: this.routingKey, users:this.users});
};

ZmCloudChatListController.prototype.notifyWhenItemAdded = function (callback) {
	this.model.itemAdded.attach(callback, "tab_item_added_" + this.routingKey);
};

ZmCloudChatListController.prototype.removeTabListeners = function () {
	this.app.onPresenceEvent.removeListener("tab_presence_" + this.routingKey);
	this.model.itemAdded.removeListener("tab_item_added_" + this.routingKey);
};



