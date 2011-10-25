function ZmCloudChatBuddyListController (model, app) {
	this._model = model;
	this.app = app;
	this.app.loginEvent.attach(AjxCallback.simpleClosure(this.handleLoginEvent, this));

	//listen to presence
	this.app.onPresenceEvent.attach(AjxCallback.simpleClosure(this.updateItemPresence, this));

	this.app.logoutEvent.attach(AjxCallback.simpleClosure(this.handleLogoutEvent, this));


}

ZmCloudChatBuddyListController.prototype.addItem = function(item) {
	this._model.addItem(item);
};

ZmCloudChatBuddyListController.prototype.addMultipleItems = function(items) {
	if(!items) {
		items = [];
	}

	items = items.sort(ZmCloudChatBuddyListController_sort);
	for(var i = 0; i < items.length; i++) {
		this._model.addItem(items[i]);
	}
};

ZmCloudChatBuddyListController.prototype.removeMultipleItems = function(items) {
	if(!items) {
		items = [];
	}
	for(var i = 0; i < items.length; i++) {
		this._model.removeItem(items[i]);
	}
};

ZmCloudChatBuddyListController.prototype.removeEmailParticipants = function() {
	this._model.removeEmailParticipants();
	this._view.hideEmailParticipantsHdr();

};

ZmCloudChatBuddyListController.prototype.showLoginForm = function() {
	this._model.showLoginForm();
};

ZmCloudChatBuddyListController.prototype.delItem = function(item) {
		this._model.removeItem(item);
};

ZmCloudChatBuddyListController.prototype.updateItemPresence = function(item) {
	this._model.updateItemPresence(item);
};

ZmCloudChatBuddyListController.prototype.handleLoginEvent = function(item) {
	this._view.handleLoginEvent(item);
};

ZmCloudChatBuddyListController.prototype.handleLogoutEvent = function() {
	this._model.removeAllItems();
};


ZmCloudChatBuddyListController.prototype.createChat = function(usersArray) {
	var emailsArray = this._getEmailsArray(usersArray);
	var tabName = "";
	if(emailsArray.length == 1) {
		if(emailsArray[0] == this.app.myCoWorkersEmail) {
			this.showChatWithUsersDlg();
			return;
		}
		tabName = emailsArray[0];
	} else {
		tabName = this.app.zimlet.getMessage("groupChat");
	}
	this.app.startChat({tabName: tabName, users: emailsArray, routingKey: "" + (new Date()).getTime()});
};


ZmCloudChatBuddyListController.prototype.showChatWithUsersDlg = function() {
	if(!this._chatWithUsersDlg) {
		this._chatWithUsersDlg = new ZmCloudChatWithUsersDlg(this);
	}
	this._chatWithUsersDlg.popup();
};

ZmCloudChatBuddyListController.prototype.showAddUsersDlg = function() {
	if(!this._addUsersDlg) {
		this._addUsersDlg = new ZmCloudChatAddUsersDlg(this);
	}
	this._addUsersDlg.popup();
};

ZmCloudChatBuddyListController.prototype.removeUsers = function(usersArray) {
	var eArray = this._getEmailsArray(usersArray);
	if(eArray.length > 0) {
		this.removeMultipleItems(usersArray);
		this.app.removeUsers(eArray);
	}
};

ZmCloudChatBuddyListController.prototype.addNewUsers = function(usersArray) {
	var eArray = this._getEmailsArray(usersArray);
	var newUsers = [];
	var items = this._model.getItems();
	for(var i = 0; i < eArray.length; i++) {
		if(!items[eArray[i]]) {
			newUsers.push(eArray[i]);
		}
	}
	if(newUsers.length > 0) {
		this.app.addNewUsers(newUsers);
	}
};

ZmCloudChatBuddyListController.prototype._getEmailsArray = function(usersArray) {
	var eArray = [];
	var emailAlreadyExist = [];
	for(var i = 0; i < usersArray.length; i++) {
		var email = usersArray[i].email;
		if(!email || email == this.app.currentUserName || emailAlreadyExist[email]) {
			continue;
		}
		eArray.push(email);
		emailAlreadyExist[email] = true;
	}
	return eArray;
};



function ZmCloudChatBuddyListController_sort(a, b) {
	var x = a.email;
	var y = b.email;
	return ((x < y) ? -1 : ((x > y) ? 1 : 0));
}
