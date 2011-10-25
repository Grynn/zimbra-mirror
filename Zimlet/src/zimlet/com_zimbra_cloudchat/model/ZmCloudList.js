function ZmCloudList(zimlet, id) {
	//this._items = [];
	this.zimlet = zimlet;
	this.itemAdded = new ZmCloudChatEvent(this);
	this.connectionEvent = new ZmCloudChatEvent(this);
}


ZmCloudList.prototype.addMessage = function(message) {
	//this._items.push(message);
	this.itemAdded.notify(message);
};

ZmCloudList.prototype.addConnectionMessage = function(message) {
	//this._items.push(message);
	this.connectionEvent.notify(message);
};
