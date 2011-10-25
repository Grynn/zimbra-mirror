function ZmCloudChatBuddyList() {
    this._items = [];
    this.itemAdded = new ZmCloudChatEvent(this);
    this.itemRemoved = new ZmCloudChatEvent(this);
	this.itemPresenceUpdated = new ZmCloudChatEvent(this);
	this.allItemsRemoved = new ZmCloudChatEvent(this);
}

ZmCloudChatBuddyList.prototype.getItems = function () {
	return this._items;
};

ZmCloudChatBuddyList.prototype.addItem = function (item) {
	if(this._items[item.email]) {
		//see if we need to update presence
		if(this._items[item.email].presence != item.presence) {
			this._items[item.email].presence = item.presence;
			this.itemPresenceUpdated.notify(item);
		}
	} else {
		this._items[item.email] = item;
		this.itemAdded.notify(item);
	}
};

ZmCloudChatBuddyList.prototype.updateItemPresence = function (item) {
	this._items[item.email] = item;
	this.itemPresenceUpdated.notify(item);
};

ZmCloudChatBuddyList.prototype.removeAllItems = function () {
  	this._items = [];
	this.allItemsRemoved.notify();
};


ZmCloudChatBuddyList.prototype.removeEmailParticipants = function () {
	for(var email in this._items) {
		if(this._items[email].isEmailParticipant) {
			this.removeItem(this._items[email]);
		}
	}
};

ZmCloudChatBuddyList.prototype.removeItem = function (item) {
  	delete this._items[item.email];
	this.itemRemoved.notify(item);
};