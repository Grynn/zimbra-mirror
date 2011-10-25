function ZmCloudChatApp(zimlet) {
	this.zimlet = zimlet;
	var soapURL =   appCtxt.getSettings().getInfoResponse.soapURL;
	if(soapURL.indexOf("localhost") >= 0 || soapURL.indexOf("rr.zimbra.com") >= 0) {
		this._zmAuthToken =  ZmCsfeCommand.getAuthToken();
		this._zimbraServerUrl =  soapURL + "GetInfoRequest";
		this._nodeUrl = "http://localhost:3000";
	} else {
		//this._zmAuthToken = "enter hardcoded auth-token for dogfood";
		//this._zimbraServerUrl = "https://dogfood.zimbra.com:443/service/soap/GetInfoRequest";
		//this._nodeUrl = "https://cloudchat.cloudfoundry.com:443/";
		this._zmAuthToken =  ZmCsfeCommand.getAuthToken();
		this._zimbraServerUrl = soapURL + "GetInfoRequest";
		this._zimbraServerUrl = this._zimbraServerUrl.replace("http://", "https://");
		this._nodeUrl = this.zimlet.getConfig("cloudChatServer");
	}

	this.chatTabs = [];
	//set/unset by ZmBuddyListView upon successful login
	 this.isLoggedIn = false;

    this._dialogView = new DwtComposite(appCtxt.getShell());
    this._dialogView.setSize("510px", "300px");

    var params = {
        parent: appCtxt.getShell(),
        title: this.zimlet.getMessage("cloudChat"),
        view: this._dialogView,
        standardButtons: [DwtDialog.NO_BUTTONS],
		mode: DwtBaseDialog.MODELESS
    };

    DwtDialog.call(this, params);
	this._routingKeyAndTabInfoMap = [];
    //we need _buttonDesc = {} as we dont have any buttons. Otherwise hitting esc will throw expn
    this._buttonDesc = {};
	//event for buddy presence
	this.onPresenceEvent = new ZmCloudChatEvent(this);
	this.loginEvent = new ZmCloudChatEvent(this);
	this.logoutEvent = new ZmCloudChatEvent(this);
	this.currentUserName = appCtxt.getSettings().getInfoResponse.name;
	this.myCoWorkersEmail = "0000AAAA_mycoworkers@work.com"; //show at the top
	this._setPrefs();
	this.createBuddyListWidget();
}

ZmCloudChatApp.prototype = new DwtDialog;
ZmCloudChatApp.prototype.constructor = ZmCloudChatApp;

ZmCloudChatApp.prototype.CONTROLS_TEMPLATE = null;

ZmCloudChatApp.prototype._dragStart = function(x, y){
 //override but dont do anything to ensure draggin below the screen
};

ZmCloudChatApp.prototype._setPrefs = function(){
	this.autoLogin = this.zimlet.getUserProperty("autoLogin") == "true";
	this.loginAsOffline = this.zimlet.getUserProperty("loginAsOffline") == "true";
	this.showEmailParticipants =  this.zimlet.getUserProperty("showEmailParticipants") == "true";
};

ZmCloudChatApp.prototype.startChat = function(params) {
	if(this.isTabDisplayed(params)) {
		return;
	}
	this.setTabInfo(params);//set tab info we have so far
	if(!this.isLoggedIn) {
		this.__loginRoutingKey = params.routingKey;
		this.login();
	} else {
		this.sendChatRequest(params);
	}
};

ZmCloudChatApp.prototype.logout = function() {
	this.cloudChatSocket.disconnect();
	this.logoutEvent.notify({});
};

ZmCloudChatApp.prototype.displayEmailParticipantsList = function(participants) {
	if(!participants) {
		return;
	}
	var emails = [];
	var arry = participants.getArray();
	var len = arry.length > 5 ? 5 : arry.length;
	for(var i = 0; i < len; i++) {
		var email = arry[i].address;
		if(email != this.currentUserName) {
			 emails.push(email);
		}
	}
	if(emails.length == 0) {
		return;
	}
	this._buddyListController.removeEmailParticipants();
	this.getEmailParticipantsAndPresence(emails);

};

ZmCloudChatApp.prototype.login = function() {
    var loginParams = {
        action: "LOGIN_USING_ZIMBRA_CREDENTIALS",
        zmAuthToken: this._zmAuthToken,
        zimbraServerUrl:this._zimbraServerUrl,
        socketType: "CHAT_CLIENT",
        exchangeName: "ChatExchange",
		loginAsOffline: this.loginAsOffline

    };
	if(!this.cloudChatSocket) {
		this.cloudChatSocket = new ZmCloudChatSocket(this._nodeUrl);
		this._initializeSocketAndConnect(loginParams);
	} else {
	   	if(this.cloudChatSocket.isConnected()) {
			this._handleOnConnect(loginParams);
		} else {
			this.cloudChatSocket.removeAllListeners();//remove all listeners
			this._initializeSocketAndConnect(loginParams);
		}
	}
};

ZmCloudChatApp.prototype._initializeSocketAndConnect = function(loginParams) {
    this.cloudChatSocket.onConnectEvent.attach(AjxCallback.simpleClosure(
    this._handleOnConnect, this, loginParams));

    this.cloudChatSocket.onMessageEvent.attach(AjxCallback.simpleClosure(
    this._handleIncomingMessage, this));

    this.cloudChatSocket.onLoggedInEvent.attach(AjxCallback.simpleClosure(
    this._handleLoggedInEvent, this));

    this.cloudChatSocket.onDisconnectEvent.attach(AjxCallback.simpleClosure(
    this._handleSystemMessages, this, JSON.stringify({
        connectionData: "Disconnected"
    })));

    this.cloudChatSocket.onReconnectEvent.attach(AjxCallback.simpleClosure(
    this._handleSystemMessages, this, JSON.stringify({
        connectionData: "Reconnecting"
    })));

    this.cloudChatSocket.onReconnectFailedEvent.attach(AjxCallback.simpleClosure(
    this._handleSystemMessages, this, JSON.stringify({
        connectionData: "Reconnection failed"
    })));

    this._handleSystemMessages(JSON.stringify({
        connectionData: "Connecting"
    }));
    this.cloudChatSocket.connect();
};

ZmCloudChatApp.prototype.display = function(routingKey, users, initialMessage) {
	if(!this._tabView) {
		this._tabView = new ZmCloudChatTabView( {
        	parent: this._dialogView,
        	className: "ZmChatDialogView"
    	}, this);
	}

	if(!routingKey) {
		return;
	}
	var tabInfo = this.getTabInfo(routingKey);
    if (tabInfo && tabInfo.displayed) {
        return;
    }
	var tabName  = "";
	if(users.length == 2) {
		for(var i = 0; i < users.length; i++) {
			var name = users[i];
			if(name != this.currentUserName) {
				tabName = name.split("@")[0];
				break;
			}
		}
	} else {
		tabName = this.zimlet.getMessage("groupChat");
	}
	var dontShowUsersInTab = users.length == 2;
    var chatList = new ZmCloudChatList(this.zimlet);
    var chatController = new ZmCloudChatListController(this, chatList, this.cloudChatSocket, routingKey, users);

	var tabPage = this._tabView.addRemovableTab(tabName, routingKey, chatController,  dontShowUsersInTab);

    var view = new ZmCloudChatListView(this.zimlet, chatList, chatController, tabPage, dontShowUsersInTab);
    view.createView();
    if (tabPage.sendBtn && tabPage.inputFieldId) {
        view.setSendBtnAndInputField(tabPage.sendBtn, tabPage.inputFieldId);
    }
	this.setTabInfo({routingKey:routingKey, users:users, tabName:tabName, model: chatList, view: view, controller: chatController, displayed:true});

    this.popup();
	this._tabView.focusInputFieldOfFirstTab();

   // this.sendAcceptChatRequest(routingKey);
    this.getPresence(routingKey, users);
	if(initialMessage){
	  chatController.displayMessage(initialMessage);
	}
};

ZmCloudChatApp.prototype.handleTabClose = function(routingKey) {
	//keep routingkey && users (so that if the other user sends an email after this tab was closed (but
	//he is using the same-session on his end), we can reuse this as we are still have the binding
	this.setTabInfo({routingKey:routingKey, displayed:false, model: null, view: null, controller: null});
};

ZmCloudChatApp.prototype.popup = function() {
	if(!this._isInitialized) {
		this._addMinimizeAndCloseBtns();
		this._isInitialized = true;
	}
	if(!this._poppedUp) {
		//reset internal props of DwtTabView when dlg is closed (so things start afresh)
		this._tabs = [];
		this._tabIx = 1;
	}
	DwtDialog.prototype.popup.apply(this);
};

ZmCloudChatApp.prototype._addMinimizeAndCloseBtns = function() {
	var html = ["<table><tr><td class='minWidth' ></td>",
		"<td class='",this._titleEl.className,"' id='", this._titleEl.id,"'> ", this._titleEl.innerHTML, "</td>",
		"<td  width='18px' align=right ><div style='cursor:pointer;' id='cloudChatDlg_minMaxBtn' class='Imgcc-minimize-icon' /></td>",
		"<td  width='18px' align=right ><div style='cursor:pointer;' id='cloudChatDlg_closeBtn' class='ImgClose' /></td>",
		"</tr></table>"];

	this._titleEl.parentNode.innerHTML = html.join("");
	this._minMaxeDlgBtn = document.getElementById("cloudChatDlg_minMaxBtn");
	this._minMaxeDlgBtn.onclick = AjxCallback.simpleClosure(this._handleMinMaxDlg, this);
	this._closeDlgBtn = document.getElementById("cloudChatDlg_closeBtn");
	this._closeDlgBtn.onclick = AjxCallback.simpleClosure(this._handleCloseDlg, this);
};

ZmCloudChatApp.prototype._handleMinMaxDlg = function() {
	if(this._minMaxeDlgBtn.className == "Imgcc-minimize-icon") {
		this._dlgTopPosB4Minimize = (this.getHtmlElement().style.top).replace("px", "");
		this._minMaxeDlgBtn.className = "Imgcc-maximize-icon";
		this.getHtmlElement().style.top = (document.body.offsetHeight - 25) + "px";
	} else if(this._minMaxeDlgBtn.className == "Imgcc-maximize-icon") {
		this._minMaxeDlgBtn.className = "Imgcc-minimize-icon";
		this.getHtmlElement().style.top = this._dlgTopPosB4Minimize + "px";
	}
};

ZmCloudChatApp.prototype._handleCloseDlg = function() {
	this.popdown();
	this._tabView.closeAllTabs();
};

ZmCloudChatApp.prototype.setTabInfo = function(params) {
	var routingKey = params.routingKey;
	if(!routingKey){
		return;
	}
	if(!this._routingKeyAndTabInfoMap[routingKey]) {
		this._routingKeyAndTabInfoMap[routingKey] = {};
	}
	for(var name in params) {
		this._routingKeyAndTabInfoMap[routingKey][name] = params[name];
	}
};

ZmCloudChatApp.prototype.replaceTabInfoRoutingKey = function(oldRoutingKey, newRoutingKey) {
	if(oldRoutingKey == newRoutingKey) {
		return newRoutingKey;
	}
	var tabInfo = this._routingKeyAndTabInfoMap[oldRoutingKey];
	if(!tabInfo){
		return newRoutingKey;
	} else if(tabInfo.controller && tabInfo.controller.isGroupChat){
		return oldRoutingKey;//return old routingkey
	}
	tabInfo.routingKey = newRoutingKey;
	this._routingKeyAndTabInfoMap[newRoutingKey]  = tabInfo;
	delete this._routingKeyAndTabInfoMap[oldRoutingKey];
	return newRoutingKey;
};

ZmCloudChatApp.prototype.getTabInfo = function(routingKey) {
	return this._routingKeyAndTabInfoMap[routingKey];
};

ZmCloudChatApp.prototype.getAllTabInfo = function() {
	return this._routingKeyAndTabInfoMap;
};

ZmCloudChatApp.prototype.removeTabInfo = function(routingKey) {
	delete this._routingKeyAndTabInfoMap[routingKey];
};

ZmCloudChatApp.prototype.isTabDisplayed = function(params) {
	var users = params.users;
	if(users.length !=  1) {
		return false;
	}
	var routingKey = [this.currentUserName].concat(users).sort().join("");
	var tabInfo = this.getTabInfo(routingKey);

	return tabInfo && tabInfo.displayed;
};

ZmCloudChatApp.prototype.sendAcceptChatRequest = function(routingKey) {
    this.cloudChatSocket.send(JSON.stringify({
        action: "ACCEPT_CHAT_REQUEST",
        routingKey: routingKey
    }));
};

ZmCloudChatApp.prototype.sendReconnectChatRequest = function(params) {
	this.__loginRoutingKey = null;
    this.cloudChatSocket.send(JSON.stringify({
        action: "SEND_RECONNECT_CHAT_REQUEST",
        routingKey: params.routingKey,
        users: params.users
    }));
};

ZmCloudChatApp.prototype.sendChatRequest = function(params) {
	this.__loginRoutingKey = null;
    this.cloudChatSocket.send(JSON.stringify({
        action: "SEND_CHAT_REQUEST",
        routingKey: params.routingKey,
        users: params.users
    }));
};

ZmCloudChatApp.prototype.getPresence = function(routingKey, users) {
    this.cloudChatSocket.send(JSON.stringify({
        action: "GET_PRESENCE",
        routingKey: routingKey,
        users: users
    }));
};

ZmCloudChatApp.prototype.publishPresence = function(presence) {
    this.cloudChatSocket.send(JSON.stringify({
        action: "PUBLISH_PRESENCE",
        data: presence
    }));
};

ZmCloudChatApp.prototype.getBuddyListAndPresence = function(routingKey) {
    this.cloudChatSocket.send(JSON.stringify({
        action: "GET_BUDDY_LIST_AND_PRESENCE",
        routingKey: routingKey
	}));
};
ZmCloudChatApp.prototype.getEmailParticipantsAndPresence = function(emails) {
    this.cloudChatSocket.send(JSON.stringify({
        action: "GET_EMAIL_PARTICIPANTS_AND_PRESENCE",
        users: emails
	}));
};

ZmCloudChatApp.prototype.addNewUsers = function(emails) {
    this.cloudChatSocket.send(JSON.stringify({
        action: "ADD_NEW_BUDDIES",
        emails: emails
	}));
};
ZmCloudChatApp.prototype.removeUsers = function(emails) {
    this.cloudChatSocket.send(JSON.stringify({
        action: "REMOVE_BUDDIES",
        emails: emails
	}));
};



ZmCloudChatApp.prototype.createBuddyListWidget = function () {
	var ccDiv = document.createElement("div");
	ccDiv.id = "cloudchat_buddy_list";

	var overview = appCtxt.getCurrentApp().getOverview();
	var treeView = overview.getTreeView(ZmOrganizer.FOLDER);
	if(treeView) {
		var el = treeView.getHtmlElement();
		el.parentNode.appendChild(ccDiv);
	}
	var model = new ZmCloudChatBuddyList();
	this._buddyListController = new ZmCloudChatBuddyListController(model, this);
	var view = new ZmCloudChatBuddyListView(model, this._buddyListController, ccDiv, this);
	view.display();
};

ZmCloudChatApp.prototype.createEmailParticipantsList = function (jsonObj) {
	var buddies = this._parseBuddiesJSON(jsonObj, true);
	this._buddyListController.addMultipleItems(buddies);
};

ZmCloudChatApp.prototype.createBuddylist = function (jsonObj) {
	var buddies = this._parseBuddiesJSON(jsonObj);
	//add myself
	 /*
	buddies.push(new ZmCloudChatBuddy({email: this.currentUserName,
									presence: (this.loginAsOffline ? "OFFLINE" : "ONLINE"),
									dontAllowGroupChat:true,
									isReadOnly:true,
									label: "Me ("+ this.currentUserName.split("@")[0] + ")"}));
	*/

	buddies.push(new ZmCloudChatBuddy({email: this.myCoWorkersEmail,
									presence: "ONLINE",
									dontAllowGroupChat: true,
									isReadOnly: false,
									label: this.zimlet.getMessage("myCoWorkers")}));
	this._buddyListController.addMultipleItems(buddies);
};

ZmCloudChatApp.prototype._parseBuddiesJSON = function(jsonObj, isEmailParticipant) {
	var buddies = [];
	var emails = jsonObj.emails;
	if(!emails) {
		return [];
	}
	var emailAndPresenceList = jsonObj.data;
	for(var i = 0; i < emails.length; i++) {
		var email = emails[i];
		var presence = "OFFLINE";
		for(var j = 0; j < emailAndPresenceList.length; j++) {
			var emailAndPresence = emailAndPresenceList[j];
			if(emailAndPresence && emailAndPresence.indexOf(email) == 0) {
				if(emailAndPresence.indexOf("ONLINE") > 0) {
					presence = "ONLINE";
				}
				break;
			}
		}
		buddies.push(new ZmCloudChatBuddy({email:email, presence:presence, isEmailParticipant: isEmailParticipant}));
	}
	return buddies;
};

ZmCloudChatApp.prototype._handleLoggedInEvent = function(message) {

	if(this.__loginRoutingKey) {
		this.sendChatRequest(this.getTabInfo(this.__loginRoutingKey));
		return;
	}
	var jsonObj = JSON.parse(message);
	if(jsonObj.error) {
   		this._handleSystemMessages(JSON.stringify({
        	connectionData: jsonObj.error
   		}));
	} else {
		this._handleSystemMessages(JSON.stringify({

			connectionData: this.zimlet.getMessage("loggedInAs")+ " " + jsonObj.email
		}));
		this.getBuddyListAndPresence();
	}
};

ZmCloudChatApp.prototype._handleOnConnect = function(loginParams) {
    this._handleSystemMessages(JSON.stringify({
        connectionData: "Doing single-signon"
    }));
    this.cloudChatSocket.send(JSON.stringify(loginParams), "LOGIN_USING_ZIMBRA_CREDENTIALS");
};

ZmCloudChatApp.prototype._handleSystemMessages = function(message) {
    var handledInChatWindow = false;
	var tabInfos = this.getAllTabInfo();
    for (var key in tabInfos) {
		var tabInfo = tabInfos[key];
		if(!tabInfo.displayed) {
			continue;
		}
        handledInChatWindow = true;
        tabInfos[key].controller.displayConnectionMessage(message);
    }
    var jsonObj = JSON.parse(message);
	this.loginEvent.notify(jsonObj.connectionData);
};

ZmCloudChatApp.prototype._handleIncomingMessage = function(message) {
    var jsonObj = JSON.parse(message);
	var action = jsonObj.action;
	var routingKey = jsonObj.routingKey;

	if(action == "GET_BUDDY_LIST_AND_PRESENCE") {
		this.createBuddylist(jsonObj);
	} else if(action == "GET_EMAIL_PARTICIPANTS_AND_PRESENCE") {
		   this.createEmailParticipantsList(jsonObj);
	}else if(action == "PRESENCE") {
		var presence = jsonObj.presence;
		var item;
		if(presence && presence.indexOf(":") > 0) {
			var tmp = presence.split(":");
			item =  new ZmCloudChatBuddy({email:tmp[0], presence:tmp[1]});
		} else {
			item = new ZmCloudChatBuddy({email:jsonObj.email, presence:"OFFLINE"});
		}
		this.onPresenceEvent.notify(item);

	} else if (jsonObj.routingKey) {
        if (action == "SEND_CHAT_REQUEST" || action == "SEND_RECONNECT_CHAT_REQUEST") {
			if(jsonObj.tabRoutingKey) {
				routingKey = this.replaceTabInfoRoutingKey(jsonObj.tabRoutingKey, routingKey);
			}
			this.sendAcceptChatRequest(routingKey);
			this.setTabInfo({routingKey:routingKey, users:jsonObj.users});//store
			if(this.currentUserName == jsonObj.from) {
           		this.display(routingKey, jsonObj.users);
			}
        } else {
            //if no action, its publish
			var tabInfo = this.getTabInfo(routingKey);
			var controller;
			if(tabInfo) {
				controller = tabInfo.controller;
			}
			if(controller) {
				controller.displayMessage(jsonObj);
			} else {
				if(jsonObj.tabRoutingKey) {
					routingKey = this.replaceTabInfoRoutingKey(jsonObj.tabRoutingKey, routingKey);
				} else {
					this.display(routingKey, this.getTabInfo(routingKey).users, jsonObj);
				}
			}
        }
    } else {
		//var transitions = [ZmToast.FADE_IN, ZmToast.PAUSE, ZmToast.FADE_OUT];
		//appCtxt.setStatusMsg(message, ZmStatusView.LEVEL_INFO, null, transitions);
		this.loginEvent.notify(message);

    }
};

ZmCloudChatApp.prototype._getZimbraHostName = function() {
    var soapURL = appCtxt.getSettings().getInfoResponse.soapURL;
    var hostName = soapURL.replace("http://", "").replace("https://", "");
    hostName = hostName.split("/")[0];
    if (hostName.indexOf(":") > 0) {
        hostName = hostName.split(":")[0];
    }
    return hostName;
};