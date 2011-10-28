function ZmCloudChatListController(app, model, socket, routingKey, users) {
	this.routingKey = routingKey;
	this.app = app;
	this.users = users;
	this.isGroupChat = users.length > 1;
	ZmCloudChatController.call(this, app.zimlet, model, socket);
	this.app.onPresenceEvent.attach(AjxCallback.simpleClosure(this.updateItemPresence, this), "tab_presence_" + routingKey);
	this.currentUsersEmailOrAliasForThisChat = this._getMyEmailOrAlias();
}

ZmCloudChatListController.prototype = new ZmCloudChatController;
ZmCloudChatListController.prototype.constructor = ZmCloudChatListController;

ZmCloudChatListController.prototype.updateItemPresence = function (item) {
	this.model.updateItemPresence(item);
};

ZmCloudChatListController.prototype.reconnectWithUser = function () {
	this.app.sendReconnectChatRequest({routingKey: this.routingKey, users:this.users});
};

ZmCloudChatListController.prototype.sendUserIsTyping = function () {
	this.app.sendUserIsTyping({routingKey: this.routingKey, user:this.currentUsersEmailOrAliasForThisChat});
};

ZmCloudChatListController.prototype.handleUserIsTyping = function (jsonObj) {
	this.view.handleUserIsTyping(jsonObj);
};

ZmCloudChatListController.prototype.notifyWhenItemAdded = function (callback) {
	this.model.itemAdded.attach(callback, "tab_item_added_" + this.routingKey);
};

ZmCloudChatListController.prototype.removeTabListeners = function () {
	this.app.onPresenceEvent.removeListener("tab_presence_" + this.routingKey);
	this.model.itemAdded.removeListener("tab_item_added_" + this.routingKey);
};

ZmCloudChatListController.prototype._getMyEmailOrAlias = function () {
	for(var i = 0; i < this.app.currentUserEmails.length; i++) {
		var cEmail = this.app.currentUserEmails[i];
		for(var j = 0; j < this.users.length; j++) {
			var user = this.users[j];
			if(user == cEmail) {
				return user;
			}
		}
	}
	return this.app.currentUserName;
};



