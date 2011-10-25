function ZmCloudChatList(zimlet) {
	this.itemPresenceUpdated = new ZmCloudChatEvent();
	ZmCloudList.call(this, zimlet);
}

ZmCloudChatList.prototype = new ZmCloudList;
ZmCloudChatList.prototype.constructor = ZmCloudChatList;

ZmCloudChatList.prototype.updateItemPresence = function (item) {
	this.itemPresenceUpdated.notify(item);
};
