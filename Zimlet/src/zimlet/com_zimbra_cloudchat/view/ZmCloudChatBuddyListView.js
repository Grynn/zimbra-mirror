function ZmCloudChatBuddyListView(model, controller, parentDom, app) {
    this.zimlet = app.zimlet;
    this.app = app;
    this._model = model;
    this._controller = controller;
	controller._view = this;//store view in controller
    this._parentDom = parentDom;
    this._emailAndInfoMap = [];
	this._epHdrDisplayed = false;
    // attach model listeners
    this._model.itemAdded.attach(AjxCallback.simpleClosure(this._addBuddyItem, this));
    this._model.itemRemoved.attach(AjxCallback.simpleClosure(this._removeBuddyItem, this));
    this._model.itemPresenceUpdated.attach(AjxCallback.simpleClosure(this.updateItemPresence, this));
	this._model.allItemsRemoved.attach(AjxCallback.simpleClosure(this.removeAllItems, this));
	//loggedInAs is used to figure out if the user is logged in or not
	this._loggedInAsStr = this.zimlet.getMessage("loggedInAs");
}

ZmCloudChatBuddyListView.prototype.display = function() {

	var subs = {cloudChatStr: this.zimlet.getMessage("cloudChat"),
				chatStatusStr: this.zimlet.getMessage("chatStatus"),
				chatUsers: this.zimlet.getMessage("chatUsers"),
				emailParticipants: this.zimlet.getMessage("emailParticipants")};
	this._parentDom.innerHTML = AjxTemplate.expand("com_zimbra_cloudchat.templates.ZmCloudChat#BuddyListWidget", subs);

    //main frame..
    this._contentDiv = document.getElementById("cloudchat_content_div");
    this._buddyListDiv = document.getElementById("cloudchat_buddyList_div");
	this._emailParticipantsDiv = document.getElementById("cloudchat_emailParticipants_div");
	this._emailParticipantsHdr = document.getElementById("cloudchat_emailParticipants_hdr");

	this._buddyListWidget = document.getElementById("cloudchat_buddyList_widget");
	this._loginWidgetDiv = document.getElementById("cloudchat_login_widget_div");
	this._buddyListActionMenuContainer = document.getElementById("cloudChat_buddy_list_actions_menu_container");
    document.getElementById("cloudchat_buddy_list_expand_icon").onclick = AjxCallback.simpleClosure(this._handleClick, this);
	document.getElementById("cloudchat_buddy_list_pref_icon").onclick = AjxCallback.simpleClosure(this._handlePrefIconClick, this);

	this._buddyListDiv.onclick = AjxCallback.simpleClosure(this._handleClick, this);
    //login form..
    this.loginFieldInfoDiv = document.getElementById("cloudChat_buddy_login_info_div");
	this.loginFieldInfoDiv.innerHTML = this.zimlet.getMessage("notLoggedIn");
    this.loginBtn = new DwtButton({
        parent: this.zimlet.getShell()
    });
    this.loginBtn.setText(this.zimlet.getMessage("loginAsZimbraUser"));
	this.loginBtn.setImage("CCZimbraIcon");

    document.getElementById("cloudChat_buddy_login_btn_cell").appendChild(
    this.loginBtn.getHtmlElement());
	this.loginBtn.addSelectionListener(new AjxListener(this, this._handleLoginBtnClick));

    //buddy list btns..
    this.buddyListActionsMenu = new DwtButton({
        parent: this.zimlet.getShell()
    });
    this.buddyListActionsMenu.setImage("cloudchat-icon");
    this.buddyListActionsMenu.setText(this.zimlet.getMessage("actions"));
    document.getElementById("cloudChat_buddy_list_actions_menu").appendChild(
    this.buddyListActionsMenu.getHtmlElement());
    menu = new ZmPopupMenu(this.buddyListActionsMenu);
    //create menu
    this.buddyListActionsMenu.setMenu(menu);
    menu.noMenuBar = true;
    mi = menu.createMenuItem(Dwt.getNextId(), {
        image: "cloudchat-icon",
        text: this.zimlet.getMessage("groupChat")
    });
	mi.addSelectionListener(new AjxListener(this, this._handleChatBtnClick));

    mi = menu.createMenuItem(Dwt.getNextId(), {
        style: DwtMenuItem.SEPARATOR_STYLE
    });

    mi = menu.createMenuItem(Dwt.getNextId(), {
        image: "cloudchat-icon",
        text: this.zimlet.getMessage("addUsers")
    });
	mi.addSelectionListener(new AjxListener(this, this._handleAddUsersClick));

    mi = menu.createMenuItem(Dwt.getNextId(), {
        image: "cloudchat-icon",
        text: this.zimlet.getMessage("removeUsers")
    });
	mi.addSelectionListener(new AjxListener(this, this._handleRemoveUsersClick));

    mi = menu.createMenuItem(Dwt.getNextId(), {
        style: DwtMenuItem.SEPARATOR_STYLE
    });

    this._showAsOnlineMenu = menu.createMenuItem(Dwt.getNextId(), {
        image: "cloudchat-icon",
        text: this.zimlet.getMessage("showAsOnline"),
        style: DwtMenuItem.RADIO_STYLE
    });
    this._showAsOnlineMenu.addSelectionListener(new AjxListener(this, this._togglePresence, "ONLINE"));
	this._showAsOnlineMenu.setChecked(!this.app.loginAsOffline, true);

    this._showAsOfflineMenu = menu.createMenuItem(Dwt.getNextId(), {
        image: "cloudchat-icon",
        text: this.zimlet.getMessage("showAsOffline"),
        style: DwtMenuItem.RADIO_STYLE
    });
    this._showAsOfflineMenu.addSelectionListener(new AjxListener(this, this._togglePresence, "OFFLINE"));
	this._showAsOfflineMenu.setChecked(this.app.loginAsOffline, true);

    mi = menu.createMenuItem(Dwt.getNextId(), {
        style: DwtMenuItem.SEPARATOR_STYLE
    });

    mi = menu.createMenuItem(Dwt.getNextId(), {
        image: "cloudchat-icon",
        text: this.zimlet.getMessage("logOut")
    });
    mi.addSelectionListener(new AjxListener(this, this._handleLogoutBtnClick));
	if(this.app.autoLogin) {
		this._handleLoginBtnClick();
	}
};

ZmCloudChatBuddyListView.prototype._togglePresence = function(presence) {
	if(presence == "ONLINE") {
		this._showAsOnlineMenu.setChecked(true, true);
		this._showAsOfflineMenu.setChecked(false, true);
	} else {
		this._showAsOnlineMenu.setChecked(false, true);
		this._showAsOfflineMenu.setChecked(true, true);
	}
	this.app.publishPresence(presence);
};

ZmCloudChatBuddyListView.prototype._handleLoginBtnClick = function() {
	this.app.login();
};

ZmCloudChatBuddyListView.prototype._handleLogoutBtnClick = function() {
	this.app.logout();
};

ZmCloudChatBuddyListView.prototype.updateItemPresence = function(item) {
    var info = this._emailAndInfoMap[item.email];
    document.getElementById(info.labelClassId).innerHTML = this._getBuddyLabelHtml(item.email, item.presence);
    document.getElementById(info.presenceClassId).className = this._getPresenceClass(item.presence);
};

ZmCloudChatBuddyListView.prototype._handleAutoLoginClick = function() {
    this.app.autoLogin = !this.app.autoLogin;
	this.zimlet.setUserProperty("autoLogin", ""+ this.app.autoLogin , true);
};

ZmCloudChatBuddyListView.prototype.handleLoginEvent = function(data) {
   this.loginFieldInfoDiv.innerHTML = data;
	if(data.indexOf(this._loggedInAsStr) == 0) {
		this._loginWidgetDiv.style.display = "none";
		this._buddyListWidget.style.display = "block";
		this.app.isLoggedIn = true;
	} else {
		this._loginWidgetDiv.style.display = "block";
		this._buddyListWidget.style.display = "none";
		this.app.isLoggedIn = false;

	}
};

ZmCloudChatBuddyListView.prototype._handleLoginAsOffline = function() {
    this.app.loginAsOffline = !this.app.loginAsOffline;
	this.zimlet.setUserProperty("loginAsOffline", ""+ this.app.loginAsOffline , true);
};

ZmCloudChatBuddyListView.prototype._showEmailParticipants = function() {
    this.app.showEmailParticipants = !this.app.showEmailParticipants;
	this.zimlet.setUserProperty("showEmailParticipants", ""+ this.app.showEmailParticipants , true);
};


ZmCloudChatBuddyListView.prototype._addBuddyItem = function(item) {
    var nextId = Dwt.getNextId();
    this._emailAndInfoMap[item.email] = {
        item: item,
        chkId: "cloudChatZimlet_buddy_chk_" + nextId,
        labelClassId: "cloudChatZimlet_buddy_label_" + nextId,
        presenceClassId: "cloudChatZimlet_buddy_presence_" + nextId,
        divId: "cloudChatZimlet_buddy_div_" + nextId,
        //divClass: (item.isReadOnly ? "CloudChatFeedRow CloudChatDisabledRow" : "CloudChatFeedRow"),
		divClass: "CloudChatFeedRow",
		dontAllowGroupChat: item.dontAllowGroupChat,
		label : item.label,
		isEmailParticipant : item.isEmailParticipant
    };
	this._addBuddyRow(this._emailAndInfoMap[item.email]);
};

 ZmCloudChatBuddyListView.prototype._removeBuddyItem = function(item) {
	 var info = this._emailAndInfoMap[item.email];
	 var row = document.getElementById(info.divId);
	 if(row) {
		 var parent = row.parentNode;
		 parent.removeChild(row);
	 }
 };

ZmCloudChatBuddyListView.prototype.removeAllItems = function() {
	this._buddyListDiv.innerHTML = "";
	this._emailParticipantsDiv.innerHTML = "";
	this._emailAndInfoMap = [];
};
//todo should we remove this? as it kind of creates a flickring effect
ZmCloudChatBuddyListView.prototype.hideEmailParticipantsHdr = function() {
	return;
	this._emailParticipantsHdr.style.display = "none";
	this._epHdrDisplayed = false;
};

ZmCloudChatBuddyListView.prototype._handlePrefIconClick =
function(ev) {
	if (AjxEnv.isIE) {
        ev = window.event;
    }
    var dwtev = DwtShell.mouseEvent;
    dwtev.setFromDhtmlEvent(ev);
	if (this._prefMenu) {
		this._prefMenu.popup(0, dwtev.docX, dwtev.docY);
		return;
	}
	this._prefMenu = new ZmActionMenu({parent:DwtShell.getShell(window), menuItems:ZmOperation.NONE});
	var mi = this._prefMenu.createMenuItem(Dwt.getNextId(), {
        image: "cloudchat-icon",
        text: this.zimlet.getMessage("autoLogin"),
        style: DwtMenuItem.CHECK_STYLE
    });
    mi.addSelectionListener(new AjxListener(this, this._handleAutoLoginClick));
	mi.setChecked(this.app.autoLogin, true);

    mi = this._prefMenu.createMenuItem(Dwt.getNextId(), {
        image: "cloudchat-icon",
        text: this.zimlet.getMessage("loginInOfflineMode"),
        style: DwtMenuItem.CHECK_STYLE
    });
    mi.addSelectionListener(new AjxListener(this, this._handleLoginAsOffline));
	mi.setChecked(this.app.loginAsOffline, true);

	mi = this._prefMenu.createMenuItem(Dwt.getNextId(), {
        image: "cloudchat-icon",
        text: this.zimlet.getMessage("showCurrentEmailParticipants"),
        style: DwtMenuItem.CHECK_STYLE
    });
    mi.addSelectionListener(new AjxListener(this, this._showEmailParticipants));
	mi.setChecked(this.app.showEmailParticipants, true);
	this._prefMenu.popup(0, dwtev.docX, dwtev.docY);
};

ZmCloudChatBuddyListView.prototype._handleClick =
function(ev) {
    if (AjxEnv.isIE) {
        ev = window.event;
    }
    var dwtev = DwtShell.mouseEvent;
    dwtev.setFromDhtmlEvent(ev);
    var el = dwtev.target;
    var origTarget = dwtev.target;
    if (el.type && el.type == "checkbox") {
        return;
    }
    if (origTarget.className == "ImgNodeExpanded" || origTarget.className == "ImgNodeCollapsed") {
        if (origTarget.className == "ImgNodeExpanded") {
            origTarget.className = "ImgNodeCollapsed";
            this._contentDiv.style.display = "none";
        } else {
            origTarget.className = "ImgNodeExpanded";
            this._contentDiv.style.display = "block";
        }
        return;
    }
    while (el && (el.className != undefined) && el.className.indexOf("CloudChatFeedRow") == -1) {
        el = el.parentNode;
    }
    if (el == null) {
        return;
    }
    this._controller.createChat([this._getItemFromFeedRowEl(el)]);
};

ZmCloudChatBuddyListView.prototype._getItemFromFeedRowEl =
function(el) {
    var elId = el.id;
    if (!elId || elId == "") {
        return;
    }
    for (var email in this._emailAndInfoMap) {
        if (this._emailAndInfoMap[email].divId == elId) {
            return this._emailAndInfoMap[email].item;
        }
    }
};

ZmCloudChatBuddyListView.prototype._addBuddyRow =
function(params) {
	var email = params.item.email;
	var checkBoxStr = "";
	var isEmailParticipant = params.isEmailParticipant;
	if(params.dontAllowGroupChat){
		checkBoxStr = "DISABLED";
	}
	var presence = params.item.presence;
   // var picsUrl = "pics/" + email.split("@")[0] + ".jpg";
    var html = [];
    var idx = 0;
    html[idx++] = "<table cellpadding=\"0\" cellspacing=\"2\">";
    html[idx++] = "<tr>";
    html[idx++] = "<td width=16px>";
	html[idx++] = "<input id='";
    html[idx++] = params.chkId;
    html[idx++] = "' type='checkbox' ";
	html[idx++] =   checkBoxStr;
	html[idx++] =  "/>";
	html[idx++] = "</td>";
    html[idx++] = "</td>";
    html[idx++] = "<td width=24px height='24px' valign=middle>";
    //html[idx++] = "<img width=24px height=24px src='";
    //html[idx++] = this.zimlet.getResource(picsUrl);
    //html[idx++] = "'></img>";
    html[idx++] = "<div class=\"ImgContact\"></div>";
    html[idx++] = "</td>";
    html[idx++] = "<td width=16px valign=middle>";
    html[idx++] = "<div id='";
    html[idx++] = params.presenceClassId;
    html[idx++] = "' class='";
    html[idx++] = this._getPresenceClass(presence);
    html[idx++] = "' />";
    html[idx++] = "</td>";
    html[idx++] = "<td id='";
    html[idx++] = params.labelClassId;
    html[idx++] = "' width='90%'>";
    html[idx++] = this._getBuddyLabelHtml((params.label ? params.label : email), presence);
    html[idx++] = "</td>";
    html[idx++] = "</tr>";
    html[idx++] = "</table>";

	var row = document.createElement("div");
	row.className = params.divClass;
	row.id = params.divId;
	if(isEmailParticipant) {
		if(!this._epHdrDisplayed) {
			this._emailParticipantsHdr.style.display = "block";
			this._epHdrDisplayed = true;
		}
		this._emailParticipantsDiv.insertBefore(row, this._emailParticipantsDiv.firstChild);
	} else {
		this._buddyListDiv.appendChild(row);
	}

   	row.innerHTML =  html.join("");

};

ZmCloudChatBuddyListView.prototype._getBuddyLabelHtml =
function(email, presence) {
    return presence == "ONLINE" ? "<b>" + email + "</b>": "<label style='color:gray;cursor:pointer;'>" + email + "</label>";
};

ZmCloudChatBuddyListView.prototype._getPresenceClass =
function(presence) {
    return presence == "ONLINE" ? "ImgMsgUnread": "ImgMsgRead";
};

ZmCloudChatBuddyListView.prototype._handleChatBtnClick =
function() {
	var items = this._getSelectedItems();
    if (items.length > 0) {
        this._controller.createChat(items);
    }
};

ZmCloudChatBuddyListView.prototype._handleAddUsersClick =
function() {
	this._controller.showAddUsersDlg();
};

ZmCloudChatBuddyListView.prototype._handleRemoveUsersClick =
function() {
	var items = this._getSelectedItems();
    if (items.length > 0) {
		this._controller.removeUsers(items);
	}
};

ZmCloudChatBuddyListView.prototype._getSelectedItems =
function() {
	var items = [];
	for (var email in this._emailAndInfoMap) {
		var info = this._emailAndInfoMap[email];
		var chkbx = document.getElementById(info.chkId);
		if (chkbx && chkbx.checked) {
			items.push(info.item);
		}
    }
	return items;
};

