function ZmCloudChatWithUsersDlg(buddyListController) {
	if(!buddyListController) {
		return;
	}
	this.buddyListController = buddyListController;
    this.zimlet = buddyListController.app.zimlet;

    this._dialogView = new DwtComposite(appCtxt.getShell());
    this._dialogView.setSize("500px", "30px");
	var chatBtn = new DwtDialog_ButtonDescriptor(ZmCloudChatWithUsersDlg.CHAT_BTN_ID, this.zimlet.getMessage("chat"), DwtDialog.ALIGN_RIGHT);

    var params = {
        parent: appCtxt.getShell(),
        title: "Enter one of more email(s) to chat or group-chat",
        view: this._dialogView,
        standardButtons: [DwtDialog.CANCEL_BUTTON],
		extraButtons : [chatBtn]
    };
    DwtDialog.call(this, params);
	this._createView();
	this._chatWithUsersField = document.getElementById("cloudChatZimlet_chat_users_field");
	this.setButtonListener(ZmCloudChatWithUsersDlg.CHAT_BTN_ID, new AjxListener(this, this._chatBtnHandler));
}

ZmCloudChatWithUsersDlg.prototype = new DwtDialog;
ZmCloudChatWithUsersDlg.prototype.constructor = ZmCloudChatWithUsersDlg;

ZmCloudChatWithUsersDlg.CHAT_BTN_ID = "cloudchat_with_users_dlg_chat_btn_id";

ZmCloudChatWithUsersDlg.prototype.popup = function() {
	this._chatWithUsersField.value = "";
	DwtDialog.prototype.popup.apply(this);
	if(!this._isInitialized) {
		this._addAutoCompleteHandler();
		this._isInitialized = true;
	}
	this._chatWithUsersField.focus();
};

ZmCloudChatWithUsersDlg.prototype._addAutoCompleteHandler =
function() {
	if (appCtxt.get(ZmSetting.CONTACTS_ENABLED) || appCtxt.get(ZmSetting.GAL_ENABLED)) {
		var params = {
			dataClass:		appCtxt.getAutocompleter(),
			matchValue:		ZmAutocomplete.AC_VALUE_EMAIL
		};
		this._acAddrSelectList = new ZmAutocompleteListView(params);
		this._acAddrSelectList.handle(this._chatWithUsersField);
	}
};

ZmCloudChatWithUsersDlg.prototype._createView =
function() {
	var html = [];
	html.push("<div><input id='cloudChatZimlet_chat_users_field' style='width:500px'></div>");
	this._dialogView.getHtmlElement().innerHTML = html.join("");
};

ZmCloudChatWithUsersDlg.prototype._chatBtnHandler =
function() {
	var participants = [];
	var emails = this._chatWithUsersField.value;
	emails = AjxEmailAddress.parseEmailString(emails);
	if(emails.good) {
		var arry = emails.good.getArray();
		for(var i = 0; i < arry.length; i++) {
			participants.push({email: arry[i].address});
		}
	}
	if(participants.length == 0){
		return;
	}
	this.popdown();
	this.buddyListController.createChat(participants);
};
