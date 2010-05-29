/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Zimlets
 * Copyright (C) 2009, 2010 Zimbra, Inc.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.3 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 * @Author Raja Rao DV
 * 
 */

/**
 * @class
 * This class represents the BroadSoft zimlet.
 * 
 * @extends	ZmZimletBase
 */
function com_zimbra_broadsoft() {
}
com_zimbra_broadsoft.prototype = new ZmZimletBase();
com_zimbra_broadsoft.prototype.constructor = com_zimbra_broadsoft;

/**
 * Initializes the zimlet.
 * 
 */
com_zimbra_broadsoft.prototype.init =
function() {
	ZmZimletBase.prototype.init.apply(this, arguments);
	this.transitions = [ ZmToast.FADE_IN, ZmToast.PAUSE, ZmToast.PAUSE,  ZmToast.PAUSE,  ZmToast.PAUSE, ZmToast.PAUSE, ZmToast.PAUSE, ZmToast.PAUSE, ZmToast.PAUSE, ZmToast.FADE_OUT ];
	this.transitions2 = [ ZmToast.FADE_IN, ZmToast.PAUSE, ZmToast.PAUSE,  ZmToast.PAUSE,  ZmToast.PAUSE, ZmToast.PAUSE, ZmToast.PAUSE, ZmToast.FADE_OUT ];
	this._createBroadSoftApp();
};

//---------------------------------------------------------------------------------
//
// CREATE A UNIQUE LIST VIEW
//
//---------------------------------------------------------------------------------

/**
 * @class
 * This class represents the BroadSoft list view.
 * 
 * @extends	ZmListView
 */
BroadSoftListView = function(params) {
	ZmListView.call(this, params);
};

BroadSoftListView.prototype = new ZmListView;
BroadSoftListView.prototype.constructor = BroadSoftListView;

/**
 * Gets the cell contents.
 * 
 */
BroadSoftListView.prototype._getCellContents =
function(htmlArr, idx, item, field, colIdx, params) {
	if (field == "ph")
		htmlArr[idx++] = item.ph;
	else if (field == "date")
		htmlArr[idx++] = item.date;

	return idx;
};

//---------------------------------------------------------------------------------
//
// OVERRIDE ZIMLET FRAMEWORK FUNCTIONS AND CREATE TAB APPLICATION
//
//---------------------------------------------------------------------------------

/**
 * This method is called by the zimlet framework when the zimlet is double-clicked.
 * 
 */
com_zimbra_broadsoft.prototype.doubleClicked =
function() {
	this.singleClicked();
};

/**
 * This method is called by the zimlet framework when the zimlet is single-clicked.
 * 
 */
com_zimbra_broadsoft.prototype.singleClicked =
function() {
	// do nothing
};

/**
 * This method is called when the zimlet tool-tip is popped-up.
 * 
 */
com_zimbra_broadsoft.prototype.toolTipPoppedUp =
function(spanElement, contentObjText, matchContext, canvas) {
	var html = new Array();
	var i = 0;
	html[i++] = "<table cellpadding=2 cellspacing=0 border=0>";
	html[i++] = ["<tr valign='center'>", "<td>", AjxImg.getImageHtml("Telephone"), "</td>"].join("");
	html[i++] = ["<td><b><div style='white-space:nowrap'>", ZmMsg.phone, " :</div>", "</b></td>"].join("");
	html[i++] = ["<td><b><div style='white-space:nowrap'>", contentObjText, "</div>", "</b></td>"].join("");
	html[i++] = ["</tr></table>"].join("");			
	canvas.innerHTML = html.join("");
};

/**
 * Creates the BroadSoft tab application.
 * 
 */
com_zimbra_broadsoft.prototype._createBroadSoftApp =
function() {
	this._broadsoftPhoneApp = this.createApp("Phone", "Telephone", "BroadSoft Phone Application");
};

/**
 * This method is called by the zimlet framework when the tab application is active.
 * 
 * @param	{String}	appName		the application
 * @param	{Boolean}	active		<code>true</code> if the tab application is active
 */
com_zimbra_broadsoft.prototype.appActive =
function(appName, active) {
	if (active) {
		this._appName = appName;
		this._BroadSoftlistViews = [];
	}
	else {
		this._hideApp(appName);
	}
};

/**
 * Hides the tab application.
 * 
 */
com_zimbra_broadsoft.prototype._hideApp =
function(appName) {
	// do nothing
};

/**
 * This method is called by the zimlet framework when the tab application is launched.
 * 
 * @param	{String}	appName		the tab application name
 * @param	{Hash}		params		a hash of parameters
 */
com_zimbra_broadsoft.prototype.appLaunch =
function(appName, params) {
	if (this._broadsoftPhoneApp != appName)
		return;

	this._createTreeView(appName);


	this._setZimletCurrentPreferences();
	//this._selectDefaultFolder();
};

/**
 * Creates a tree view and call-logs list view.
 * 
 */
com_zimbra_broadsoft.prototype._createTreeView =
function() {
	var html = new Array();
	var i = 0;
	this.expandIconAndFolderTreeMap = new Array();
	var activeApp = appCtxt.getCurrentApp();
	if (activeApp.getName() != this._broadsoftPhoneApp)
		return;

	var overview = activeApp ? activeApp.getOverview() : null;
	var element = overview.getHtmlElement();

	var expandIconId = "broadsoft_expandIcon_" + Dwt.getNextId();
	this.expandIconAndFolderTreeMap[expandIconId] = new Array();
	html[i++] = this._getTreeHeaderHTML("Broadsoft Voice", expandIconId);	//header
	var childExpandIconId = "tweetzi_expandIcon_" + Dwt.getNextId();
	this.expandIconAndFolderTreeMap[childExpandIconId] = new Array();
	html[i++] = this._getFolderHTML({name:"Calls", icon:"Telephone", account: "", sn:"", type:"phone"}, expandIconId, childExpandIconId, false, true);
	html[i++] = this._getFolderHTML({name:"Missed Calls", icon:"MissedCalls", account:"", sn:"missed", type:"phone"}, expandIconId, childExpandIconId, true);
	html[i++] = this._getFolderHTML({name:"Placed Calls", icon:"PlacedCalls", account:"", sn:"placed", type:"phone"}, expandIconId, childExpandIconId, true);
	html[i++] = this._getFolderHTML({name:"Answered Calls", icon:"AnsweredCalls", account:"", sn:"received", type:"phone"}, expandIconId, childExpandIconId, true);

	var expandIconId = "broadsoft_expandIcon_" + Dwt.getNextId();
	this.expandIconAndFolderTreeMap[expandIconId] = new Array();
	html[i++] = this._getTreeHeaderHTML("Preferences", expandIconId);	//header
	html[i++] = this._getFolderHTML({name:"Preferences", icon:"Preferences", account:"", sn:"preferences", type:"preferences"}, expandIconId, childExpandIconId, false);
	element.innerHTML = html.join("");
	element.onclick = AjxCallback.simpleClosure(this._handleTreeClick, this);
};

/**
 * Handles a tree click event.
 * 
 * @param	{Object}		ev		the event
 * @see		#_createTreeView
 */
com_zimbra_broadsoft.prototype._handleTreeClick =
function(ev) {
	if (AjxEnv.isIE) {
		ev = window.event;
	}
	var dwtev = DwtShell.mouseEvent;
	dwtev.setFromDhtmlEvent(ev);
	var el = dwtev.target;
	var origTarget = dwtev.target;
	if (origTarget.className == "ImgNodeExpanded" || origTarget.className == "ImgNodeCollapsed") {
		var display = "block";
		if (origTarget.className == "ImgNodeExpanded") {
			origTarget.className = "ImgNodeCollapsed";
			display = "none";
		} else {
			origTarget.className = "ImgNodeExpanded";
		}
		var foldersId = this.expandIconAndFolderTreeMap[origTarget.id];
		for (var i = 0; i < foldersId.length; i++) {
			document.getElementById(foldersId[i]).style.display = display;
		}
		return;
	}
	while (el && el.className != "DwtTreeItem") {
		el = el.parentNode;
	}
	if (el == null)
		return;

	var tds = el.getElementsByTagName("td");
	var label = "";
	for (var i = 0; i < tds.length; i++) {
		var td = tds[i];
		if (td.className == "DwtTreeItem-selected" || td.className == "DwtTreeItem-Text") {
			label = AjxEnv.isIE ? td.innerText : td.textContent;
			break;
		}
	}
	if (td.className == "DwtTreeItem-Text") {
		if (this.previousFolderCell) {
			this.previousFolderCell.className = "DwtTreeItem-Text";
			td.className = "DwtTreeItem-selected";
			this.previousFolderCell = td;
		} else {
			td.className = "DwtTreeItem-selected";
			this.previousFolderCell = td;
		}
	}
	this._treeClickAction(this.treeIdAndFolderItemMap[el.id]);
};

/**
 * Handles a tree click action.
 * 
 * @param	{Object}	folder		the folder
 */
com_zimbra_broadsoft.prototype._treeClickAction =
function(folder) {
	switch (folder.name) {
		case "Missed Calls":
		case "Placed Calls":
		case "Answered Calls":
			this._getCallLogs(folder.sn);
			break;
		case "Preferences":
			this._showPreferencesView();
			this._setZimletCurrentPreferences();
			this._addToolbarBtns({enableCallBtn:false, enableSaveBtn:true});

			break;
		case "Calls":
			this._getCallLogs(folder.sn);
			break;
		default:
	}
};

/**
 * Gets the tree header HTML.
 * 
 * @param	{String}	treeName		the tree name
 * @param	{String}	expandIconId	the expand icon id
 * @return	{String}	the HTML
 */
com_zimbra_broadsoft.prototype._getTreeHeaderHTML =
function(treeName, expandIconId) {
	var html = new Array();
	var i = 0;
	if (expandIconId) {
		html[i++] = "<div  class='overviewHeader'>";
	} else {
		html[i++] = "<div  class='overviewHeader'>";
	}
	html[i++] = "<TABLE cellpadding=\"0\" cellspacing=\"0\">";
	html[i++] = "<TR>";
	html[i++] = "<TD style=\"width:16px;height:16px\" align='center'>";
	html[i++] = "<div class=\"ImgNodeExpanded\" id= '" + expandIconId + "'/>";
	html[i++] = "</TD>";
	html[i++] = "<td class=\"imageCell\" />";
	html[i++] = "<TD  class='overviewHeader-Text'>";
	html[i++] = treeName;
	html[i++] = "</TD>";
	html[i++] = "<TD style=\"width:16px;height:16px\">";
	html[i++] = AjxImg.getImageHtml("Blank_16");
	html[i++] = "</TD>";
	html[i++] = "</TR>";
	html[i++] = "</TABLE>";
	html[i++] = "</div>";
	return html.join("");
};

/**
 * Gets the folder HTML.
 * 
 * @return	{String}	the HTML
 */
com_zimbra_broadsoft.prototype._getFolderHTML =
function(folder, expandIconId, childExpandIconId, isSubFolder, hasChild) {
	var html = new Array();
	var i = 0;
	if (this.treeIdAndFolderItemMap == undefined) {
		this.treeIdAndFolderItemMap = new Array();
	}

	html[i++] = "<div class='DwtComposite'>";
	var id = "tweetziTreeItem__" + folder.type + "_" + Dwt.getNextId();
	this.treeIdAndFolderItemMap[id] = folder;
	this.expandIconAndFolderTreeMap[expandIconId].push(id);
	if (isSubFolder) {
		this.expandIconAndFolderTreeMap[childExpandIconId].push(id);
	}
	html[i++] = "<div class='DwtTreeItem' id='" + id + "'>";

	html[i++] = "<TABLE width=100% cellpadding=\"1\" cellspacing=\"1\">";
	html[i++] = "<TR>";
	html[i++] = "<TD style=\"width:16px;height:16px\" align='center'>";
	if (hasChild) {
		html[i++] = "<div class=\"ImgNodeExpanded\" id= '" + childExpandIconId + "'/>";
	} else {
		html[i++] = AjxImg.getImageHtml("Blank_16");
	}
	html[i++] = "</TD>";
	if (isSubFolder) {
		html[i++] = "<TD style=\"width:16px;height:16px\" align='center'>";
		html[i++] = AjxImg.getImageHtml("Blank_16");
		html[i++] = "</TD>";
	}
	html[i++] = "<TD style=\"width:16px;height:16px\">";
	html[i++] = AjxImg.getImageHtml(folder.icon);
	html[i++] = "</TD>";
	html[i++] = "<TD class='DwtTreeItem-Text' nowrap=''>";
	html[i++] = folder.name;
	html[i++] = "</TD>";
	html[i++] = "<TD style=\"width:16px;height:16px\">";
	html[i++] = AjxImg.getImageHtml("Blank_16");
	html[i++] = "</TD>";
	html[i++] = "</TR>";
	html[i++] = "</TABLE>";
	html[i++] = "</div>";
	html[i++] = "</div>";
	return html.join("");
};

/**
 * Listens for tree events.
 * 
 * @param	{Object}	ev		the event
 */
com_zimbra_broadsoft.prototype._treeListener =
function(ev) {
	var txt = ev.item._text;
	if(ev.detail == 1)
		return;

	switch (txt) {
		case "Missed Calls":
		case "Placed Calls":
		case "Answered Calls":

			this._getCallLogs(this._getShortNameFromName(txt));
			break;
		case "Preferences":
			this._showPreferencesView();
			this._setZimletCurrentPreferences();
			this._addToolbarBtns({enableCallBtn:false, enableSaveBtn:true});

			break;
		default:
	}
};

/**
 * Gets the header list.
 * 
 * @return	{Array}	an array of {@link DwtListHeaderItem} objects
 */
com_zimbra_broadsoft.prototype._getHeaderList =
function() {
	var hList = [];
	hList.push(new DwtListHeaderItem({field:"ph", text:"Phone Number"}));
	hList.push(new DwtListHeaderItem({field:"date", text:"Date And Time", width:"150px"}));
	return hList;
};

/**
 * Adds toolbar buttons.
 * 
 * @param	{Hash}	param	a hash of parameters
 */
com_zimbra_broadsoft.prototype._addToolbarBtns =
function(param) {
		var app = appCtxt.getApp(this._appName);
		var toolbar = app.getToolbar();
		if(toolbar.getButton("broadsoft_call_toolbarBtn") == undefined) {
			this._callBtn  = toolbar.createButton("broadsoft_call_toolbarBtn", {text:"Call", image:"Telephone"});
			toolbar.addSelectionListener("broadsoft_call_toolbarBtn", new AjxListener(this, this._handleCallToolbarButton));
		}
		if(toolbar.getButton("broadsoft_save_toolbarBtn") == undefined) {
			this._saveBtn  = toolbar.createButton("broadsoft_save_toolbarBtn", {text:"Save", image:"Save"});
			toolbar.addSelectionListener("broadsoft_save_toolbarBtn", new AjxListener(this, this._okPreferenceBtnListener));
			
		}
		this._callBtn.setEnabled(param.enableCallBtn);
		this._saveBtn.setEnabled(param.enableSaveBtn);
};

/**
 * This method is called when a phone number is clicked.
 * 
 * @param	{Object}	myElement	the element
 * @param	{String}	toPhoneNumber	the phone number
 * 
 */
com_zimbra_broadsoft.prototype.clicked =
function(myElement, toPhoneNumber) {
	this._setZimletCurrentPreferences();
	this._clickToCall(toPhoneNumber);
};

/**
 * This method is called when items are dropped onto the zimlet.
 * 
 * @param	{Object}	obj		the dropped object
 */
com_zimbra_broadsoft.prototype.doDrop =
function(obj) {
	this._setZimletCurrentPreferences();
	switch (obj.TYPE) {
		case "ZmMailMsg":
		case "ZmConv":
			this.msgDropped(obj);
			break;

		case "ZmContact":
			this.contactDropped(obj);
			break;

		case "ZmAppt":
			this.apptDropped(obj);
			break;

		default:
			this.displayErrorMessage("BroadSoft Zimlet: Object type \"" + obj.TYPE
					+ "\" is not supported for drag-and-drop.");
	}
};

/**
 * Handles a dropped message.
 * 
 * @param	{ZmMailMsg|ZmConv}	zmObject		the dropped message
 */
com_zimbra_broadsoft.prototype.msgDropped =
function(zmObject) {
	var msgObj = zmObject.srcObj;//get access to source-object
	if (zmObject.type == "CONV") {
		msgObj = zmObject.getFirstHotMsg();
	}

	var fromEmails = [];//stores the from email
	var participants = msgObj.participants.getArray(); //where msgObj is of type "ZmMailMsg"
	for (var i = 0; i < participants.length; i++) {
		if (participants[i].type == AjxEmailAddress.FROM) {
			fromEmails.push(participants[i].address);
		}
	}
	var respCallback = new AjxCallback(this, this._callContact, fromEmails[0]);
	appCtxt.getApp(ZmApp.CONTACTS).getContactByEmail(fromEmails[0], respCallback);
};

/**
 * Handles a dropped contact.
 * 
 * @param	{ZmContact}	zmObject		the dropped contact
 */
com_zimbra_broadsoft.prototype.contactDropped =
function(zmObject) {
	this._callContact(null, zmObject, false);
}

/**
 * Calls the contact.
 * 
 * @param	{Object}	email		an email
 * @param	{ZmContact}	contact		the contact
 * @param	{Boolean}	isContactSrcObj	<code>true</code> if the contact source object
 * 
 */
com_zimbra_broadsoft.prototype._callContact =
function(email, contact, isContactSrcObj) {

	if (contact == undefined) {
		appCtxt.getAppController().setStatusMsg("No Contact found for this email: " + email, ZmStatusView.LEVEL_WARNING);
		return;
	}
	if (isContactSrcObj == undefined)
		isContactSrcObj = true;

	var phoneNumber = this._getPhoneNumber(contact, isContactSrcObj);
	if (phoneNumber == "") {
		appCtxt.getAppController().setStatusMsg("No Phone Number found for this email", ZmStatusView.LEVEL_WARNING, null, this.transitions);
		return;
	}

	this._clickToCall(phoneNumber);
};

/**
 * Gets the phone number.
 * 
 * @param	{ZmContact}	contact		the contact
 * @param	{Boolean}	isContactSrcObj	<code>true</code> if the contact source object
 * @return	{String}	the phone number or an empty string for none
 */
com_zimbra_broadsoft.prototype._getPhoneNumber =
function(contact, isContactSrcObj) {
	var phoneNumber = "";
	try {
		if (isContactSrcObj) {
			if (contact.attr.workPhone) {
				phoneNumber = contact.attr.workPhone;
			} else if (contact.attr.mobilePhone) {
				phoneNumber = contact.attr.mobilePhone;
			} else if (contact.attr.homePhone) {
				phoneNumber = contact.attr.homePhone;
			}
		} else {
			if (contact.workPhone) {
				phoneNumber = contact.workPhone;
			} else if (contact.attr.mobilePhone) {
				phoneNumber = contact.mobilePhone;
			} else if (contact.homePhone) {
				phoneNumber = contact.homePhone;
			}
		}
		return phoneNumber;
	} catch(e) {
	}
	return "";

};

//---------------------------------------------------------------------------------
//
// BROADSOFT API URLS
//
//---------------------------------------------------------------------------------

/**
 * Gets the "Click2Call" url.
 * 
 * @param	{String}	phoneNumberToCall		the phone number
 * @return	{String}	the url
 */
com_zimbra_broadsoft.prototype.getClick2CallURL =
function(phoneNumberToCall) {
	return "https://" + this.broadsoft_server + "/com.broadsoft.xsi-actions/v1.0/user/" + this.broadsoft_email + "/calls/new/" + phoneNumberToCall;
};

/**
 * Gets the "CallAnyWhere" url.
 * 
 * @return	{String}	the url
 */
com_zimbra_broadsoft.prototype.getCallAnyWhereURL =
function() {
	return "https://" + this.broadsoft_server + "/com.broadsoft.xsi-actions/v1.0/user/" + this.broadsoft_email + "/services/BroadWorksAnywhere";
};

/**
 * Gets the "CallLogs" url.
 * 
 * @return	{String}	the url
 */
com_zimbra_broadsoft.prototype.getCallLogsURL =
function() {
	return "https://" + this.broadsoft_server + "/com.broadsoft.xsi-actions/v1.0/user/" + this.broadsoft_email + "/directories/CallLogs";
};

/**
 * Gets the "Calls" url.
 * 
 * @return	{String}	the url
 */
com_zimbra_broadsoft.prototype.getCallsURL =
function() {
	return "https://" + this.broadsoft_server + "/com.broadsoft.xsi-actions/v1.0/user/" + this.broadsoft_email + "/calls";
};

/**
 * Gets the "CancelCalls" url.
 * 
 * @return	{String}	the url
 */
com_zimbra_broadsoft.prototype.getCancelCallsURL =
function() {
	return "https://" + this.broadsoft_server + "/com.broadsoft.xsi-actions/v1.0/user/" + this.broadsoft_email + "/calls/" + this.callId;
};

/**
 * Gets the "DoNotDisturb" url.
 * 
 * @return	{String}	the url
 */
com_zimbra_broadsoft.prototype.getDoNotDisturbURL =
function() {
	return" https://" + this.broadsoft_server + "/com.broadsoft.xsi-actions/v1.0/user/" + this.broadsoft_email + "/services/DoNotDisturb";
};

/**
 * Calls the zimlet JSP.
 * 
 * @param	{String}	turnOn	"true" or "false"
 * @param	{String}	actionName	the action name ("doNotDisturb" or "callAnyWhere")
 */
com_zimbra_broadsoft.prototype._callJSP =
function(turnON, actionName) {
	var pArray =  new Array();
	pArray.push("server="+ AjxStringUtil.urlComponentEncode(this.broadsoft_server));
	pArray.push("email="+AjxStringUtil.urlComponentEncode(this.broadsoft_email));
	pArray.push("password="+AjxStringUtil.urlComponentEncode(this.broadsoft_password));
	pArray.push("turnon="+AjxStringUtil.urlComponentEncode(turnON));
	pArray.push("action="+AjxStringUtil.urlComponentEncode("doNotDisturb"));
	var jspUrl = this.getResource("broadsoft.jsp")+"?"+pArray.join("&");
	var response = AjxRpc.invoke(null, jspUrl, null, null, true);
	//var obj = eval("("+response.text+")");

};

/**
 * Calls the zimlet JSP for "DoNotDisturb" action
 * 
 * @param	{String}	turnOn	"true" or "false"
 */
com_zimbra_broadsoft.prototype._callJspForDoNotDisturb =
function(turnON) {
	this._callJSP(turnON, "doNotDisturb");
};

/**
 * Calls the zimlet JSP for "CallAnyWhere" action
 * 
 * @param	{String}	turnOn	"true" or "false"
 */
com_zimbra_broadsoft.prototype._callJspForCallAnyWhere =
function(turnON) {
	//this._callJSP(turnON, "callAnyWhere");
};

//---------------------------------------------------------------------------------
//
// BROADSOFT OPERATIONS
//
//---------------------------------------------------------------------------------

/**
 * Click to call operation.
 * 
 * @param	{String}	toPhoneNumber	the phone number
 */
com_zimbra_broadsoft.prototype._clickToCall =
function(toPhoneNumber) {

	appCtxt.getAppController().setStatusMsg(["Calling ", toPhoneNumber ,"...<BR/>(From Zimbra&nbsp;&nbsp;->&nbsp;&nbsp;YOUR PHONE&nbsp;&nbsp;->&nbsp;&nbsp;", toPhoneNumber, "...)"].join(""), ZmStatusView.LEVEL_INFO, null, this.transitions);
	appCtxt.getAppController().setStatusMsg(["Please pick up YOUR phone to initiate call to <b>", toPhoneNumber, "</b>.."].join(""), ZmStatusView.LEVEL_INFO, null, this.transitions2);
	
	var hdrs = new Array();
	hdrs["Authorization"] = this.make_basic_auth(this.broadsoft_email, this.broadsoft_password);
	var feedUrl = ZmZimletBase.PROXY + AjxStringUtil.urlComponentEncode(this.getClick2CallURL(toPhoneNumber));
	AjxRpc.invoke(null, feedUrl, hdrs, new AjxCallback(this, this._responseHandler), false);

};

/**
 * Handles the click to call response.
 * 
 * @param	{Object}	response	the response
 * @see		#_clickToCall
 */
com_zimbra_broadsoft.prototype._responseHandler =
function(response) {
//	this._getCurrentCalls();
};

/**
 * Anywhere operation.
 * 
 */
com_zimbra_broadsoft.prototype._getBroadsoftAnywhere =
function() {
	var hdrs = new Array();
	hdrs["Authorization"] = this.make_basic_auth(this.broadsoft_email, this.broadsoft_password);
	var feedUrl = ZmZimletBase.PROXY + AjxStringUtil.urlComponentEncode(this.getCallAnyWhereURL());
	AjxRpc.invoke(null, feedUrl, hdrs, new AjxCallback(this, this._getBroadsoftAnywhereHdlr), true);
};

/**
 * Handles the anywhere response.
 * 
 * @param	{Object}	response	the response
 * @see		#_getBroadsoftAnywhere
 */
com_zimbra_broadsoft.prototype._getBroadsoftAnywhereHdlr =
function(response) {
};

/**
 * Check do not disturb operation.
 * 
 */
com_zimbra_broadsoft.prototype._checkDoNotDisturb =
function() {
	var hdrs = new Array();
	hdrs["Authorization"] = this.make_basic_auth(this.broadsoft_email, this.broadsoft_password);
	var feedUrl = ZmZimletBase.PROXY + AjxStringUtil.urlComponentEncode(this.getDoNotDisturbURL());
	AjxRpc.invoke(null, feedUrl, hdrs, new AjxCallback(this, this._checkDoNotDisturbHdlr), true);
};

/**
 * Handles the do not disturb response.
 * 
 * @param	{Object}	response	the response
 * @see		#_checkDoNotDisturb
 */
com_zimbra_broadsoft.prototype._checkDoNotDisturbHdlr =
function(response) {
	try {
		var arry = response.xml.getElementsByTagName("isActive");
		var c = arry[0].firstChild;
		this.callId = "";
		if (c.textContent) {
			this.isDoNotDisturbON_server = c.textContent;
		} else if (c.text) {
			this.isDoNotDisturbON_server = c.text;
		}
	} catch(e) {
	}
	if (this.isDoNotDisturbON_server == "true") {
		var transitions = [ ZmToast.FADE_IN, ZmToast.PAUSE,  ZmToast.PAUSE,  ZmToast.PAUSE, ZmToast.FADE_OUT ];
		appCtxt.getAppController().setStatusMsg("Note: Do Not Disturb Is ON", ZmStatusView.LEVEL_WARNING, null, transitions);
	}
};

/**
 * Current calls operation.
 * 
 */
com_zimbra_broadsoft.prototype._getCurrentCalls =
function() {
	var hdrs = new Array();
	hdrs["Authorization"] = this.make_basic_auth(this.broadsoft_email, this.broadsoft_password);
	var feedUrl = ZmZimletBase.PROXY + AjxStringUtil.urlComponentEncode(this.getCallsURL());
	AjxRpc.invoke(null, feedUrl, hdrs, new AjxCallback(this, this._getCurrentCallsHdlr), true);
};

/**
 * Handles the current calls response.
 * 
 * @param	{Object}	response	the response
 * @see		#_getCurrentCalls
 */
com_zimbra_broadsoft.prototype._getCurrentCallsHdlr =
function(response) {
	try {
		var arry = response.xml.getElementsByTagName("callId");
		if(arry.length == 0)
			return;

		var c = arry[0].firstChild;
		this.callId = "";
		if (c.textContent) {
			this.callId = c.textContent;
		} else if (c.text) {
			this.callId = c.text;
		}

	} catch(e) {
	}
};

/**
 * Get call logs operation.
 * 
 * @param	{Object}	callType		the call type
 */
com_zimbra_broadsoft.prototype._getCallLogs =
function(callType) {
	if (callType && this._hasCashedCallLogs) {
		this._showCallLogs(callType);
		this._addToolbarBtns({enableCallBtn:true, enableSaveBtn:false});
		return;
	}

	var hdrs = new Array();
	hdrs["Authorization"] = this.make_basic_auth(this.broadsoft_email, this.broadsoft_password);
	var feedUrl = ZmZimletBase.PROXY + AjxStringUtil.urlComponentEncode(this.getCallLogsURL());
	AjxRpc.invoke(null, feedUrl, hdrs, new AjxCallback(this, this._getCallLogsHdlr, callType), true);
};

/**
 * Handles the call logs response.
 * 
 * @param	{Object}	response	the response
 * @see		#_getCallLogs
 */
com_zimbra_broadsoft.prototype._getCallLogsHdlr =
function(callType, response) {
	var xml = response.xml;
	if(xml == undefined) {
		this._addToolbarBtns({enableCallBtn:false, enableSaveBtn:false});
		return;
	}

	this._callLogs = new Array();
	var p = xml.getElementsByTagName("placed")[0].getElementsByTagName("callLogsEntry");
	var m = xml.getElementsByTagName("missed")[0].getElementsByTagName("callLogsEntry");
	var r = xml.getElementsByTagName("received")[0].getElementsByTagName("callLogsEntry");

	this._storeCallLogs(p, "placed");
	this._storeCallLogs(m, "missed");
	this._storeCallLogs(r, "received");
	this._showCallLogs(callType);
	this._addToolbarBtns({enableCallBtn:true, enableSaveBtn:false});
};

/**
 * Show call logs.
 * 
 * @param	{Object}	callType		the call type
 */
com_zimbra_broadsoft.prototype._showCallLogs =
function(callType) {
	if (callType == undefined)
		return;

	var app = appCtxt.getApp(this._appName);
	var params = {parent:DwtShell.getShell(window), headerList: this._getHeaderList(), posStyle: Dwt.ABSOLUTE_STYLE};

	this._listView = new BroadSoftListView(params);
	this._listView._controller = {};//set dummy controller
	app.setView(this._listView);
	this._listView.set(this._callLogs[callType], 1, true);
};

/**
 * Handles call toolbar button
 * 
 * @see	#_addToolbarBtns
 */
com_zimbra_broadsoft.prototype._handleCallToolbarButton =
function() {
	var selection = this._listView.getSelection();
	if(selection.length > 0) {
		this._clickToCall(selection[0].ph);
	}
};

/**
 * Stores the call logs.
 * 
 * @param	{Array}		callLogsList		the call logs list
 * @param	{String}	category		the category
 */
com_zimbra_broadsoft.prototype._storeCallLogs =
function(callLogsList, category) {
	this._callLogs[category] = new AjxVector();
	for (var i = 0; i < callLogsList.length; i++) {
		var callLog = callLogsList[i];
		var countryCode = callLog.getElementsByTagName("countryCode")[0].firstChild;
		var phoneNumber = callLog.getElementsByTagName("phoneNumber")[0].firstChild;
		var name = callLog.getElementsByTagName("name")[0].firstChild;
		var time = callLog.getElementsByTagName("time")[0].firstChild;
		if (countryCode.textContent) {
			var obj = {ph: (countryCode.textContent + phoneNumber.textContent), n:name.textContent, date:this._formatDateTime(time.textContent)};
		} else if (countryCode.text) {
			var obj = {pn: (countryCode.text + phoneNumber.text), n:name.text, date:this._formatDateTime(time.text)};
		}
		this._callLogs[category].add(obj);
	}
};

/**
 * Formats date time.
 * 
 * @param	{Date}	date		the date
 * @return	{String}	the formatted date string
 */
com_zimbra_broadsoft.prototype._formatDateTime =
function(dateTime) {			
	//get time and date
	var tmp = dateTime.split("T");
	var date = this._formatDate(tmp[0]);
	var time = tmp[1].split(".")[0];
	return date + " " + time;
};

/**
 * Formats the date.
 * 
 * @param	{Date}	d	the date
 * @return	{String}	the formatted date string
 */
com_zimbra_broadsoft.prototype._formatDate =
function(d) {
	var tmp1 = d.split("-");
	var tmpDay = tmp1[1];
	tmpDay = parseInt(tmpDay) - 1;

	var d1 = new Date(tmp1[0], tmpDay, tmp1[2]);
	var today = new Date();
	var d2 = new Date(today.getFullYear(), today.getMonth(), today.getDate(), 0, 0, 0);
	var diff = (d2 - d1) / (3600 * 1000);
	if (diff == 0)
		return "Today";
	else if (diff == 1)
		return "Yesterday";
	else
		return (tmpDay + 1) + "/" + tmp1[2] + "/" + tmp1[0];

};

/**
 * Makes basic auth.
 * 
 * @param	{String}	user		the username
 * @param	{String}	password	the password
 * @return	{String}	the basic auth string
 */
com_zimbra_broadsoft.prototype.make_basic_auth =
function (user, password) {
	var tok = user + ':' + password;
	var hash = Base64.encode(tok);
	return "Basic " + hash;
};


/**
 * Sets the current preferences.
 * 
 */
com_zimbra_broadsoft.prototype._setZimletCurrentPreferences =
function() {
	this.broadsoft_email = this.getUserProperty("broadsoft_email");
	if (document.getElementById("broadsoft_email") != null && this.broadsoft_email != undefined) {
		document.getElementById("broadsoft_email").value = this.broadsoft_email;
	}
	this.broadsoft_password = this.getUserProperty("broadsoft_password");
	if (document.getElementById("broadsoft_password") != null && this.broadsoft_password != undefined) {
		document.getElementById("broadsoft_password").value = this.broadsoft_password;
	}

	this.broadsoft_server = this.getUserProperty("broadsoft_server");
	if (document.getElementById("broadsoft_server") != null  && this.broadsoft_server != undefined) {
		document.getElementById("broadsoft_server").value = this.broadsoft_server;
	}
	this.broadsoft_fromWorkNumber = this.getUserProperty("broadsoft_fromWorkNumber");
	if (document.getElementById("broadsoft_fromWorkNumber") != null  && this.broadsoft_fromWorkNumber != undefined) {
		document.getElementById("broadsoft_fromWorkNumber").value = this.broadsoft_fromWorkNumber;
	}
	this.broadsoft_fromMobileNumber = this.getUserProperty("broadsoft_fromMobileNumber");
	if (document.getElementById("broadsoft_fromMobileNumber") != null  && this.broadsoft_fromMobileNumber != undefined) {
		document.getElementById("broadsoft_fromMobileNumber").value = this.broadsoft_fromMobileNumber;
	}
	this.broadsoft_fromHouseNumber = this.getUserProperty("broadsoft_fromHouseNumber");
	if (document.getElementById("broadsoft_fromHouseNumber") != null  && this.broadsoft_fromHouseNumber != undefined) {
		document.getElementById("broadsoft_fromHouseNumber").value = this.broadsoft_fromHouseNumber;
	}
	this.broadsoft_forwardToPhoneNumber = this.getUserProperty("broadsoft_forwardToPhoneNumber");
	if (document.getElementById("broadsoft_forwardToPhoneNumber") != null  && this.broadsoft_forwardToPhoneNumber != undefined) {
		document.getElementById("broadsoft_forwardToPhoneNumber").value = this.broadsoft_forwardToPhoneNumber;
	}

	this.broadsoft_incomingCallsRadio = this.getUserProperty("broadsoft_incomingCallsRadio");

	if (document.getElementById("broadsoft_enableCallAnyWhere") != null && this.broadsoft_incomingCallsRadio == "CALL_ANYWHERE") {
		document.getElementById("broadsoft_enableCallAnyWhere").checked = true;
	} else if (document.getElementById("broadsoft_enableCallForwarding") != null && this.broadsoft_incomingCallsRadio == "CALL_FORWARDING") {
		document.getElementById("broadsoft_enableCallForwarding").checked = true;
	} else if (document.getElementById("broadsoft_enableDoNotDisturb") != null && this.broadsoft_incomingCallsRadio == "CALL_DONOTDISTURB") {
		document.getElementById("broadsoft_enableDoNotDisturb").checked = true;
	}
};

/**
 * Shows the preferences view.
 * 
 */
com_zimbra_broadsoft.prototype._showPreferencesView =
function() {
	var app = appCtxt.getApp(this._appName);
	app.setContent(this._createPreferenceHTML());
};

/**
 * Creates the preferences HTML.
 * 
 * @return	{String}	the HTML
 */
com_zimbra_broadsoft.prototype._createPreferenceHTML =
function() {
	var html = new Array();
	var i = 0;
	html[i++] = "<BR/>";
	html[i++] = "<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\"><tbody><tr class=\"ZOptionsHeaderRow\"><td class=\"ZOptionsHeaderL\"><div class=\"ImgPrefsHeader_L\"/></td><td class=\"ZOptionsHeader ImgPrefsHeader\">Voice Configuration</td><td class=\"ZOptionsHeaderR\"><div class=\"ImgPrefsHeader_R\"/></td></tr></tbody></table>";
	html[i++] = "<TABLE>";
	html[i++] = "<TR>";
	html[i++] = "<TD>";
	html[i++] = "Broadsoft User Name: ";
	html[i++] = "</TD><TD>";
	html[i++] = "<input id='broadsoft_email'  size='30' type='text'/>";
	html[i++] = "</TD>";
	html[i++] = "</TR>";
	html[i++] = "<TR>";
	html[i++] = "<TD>";
	html[i++] = "Broadsoft Password:";
	html[i++] = "</TD><TD>";
	html[i++] =" <input id='broadsoft_password'  size='30'  type='password'/>";
	html[i++] = "</TD>";
	html[i++] = "</TR>";
	html[i++] = "<TR>";
	html[i++] = "<TD>";
	html[i++] = "Broadsoft Server:";
	html[i++] = "</TD><TD>";
	html[i++] =" <input id='broadsoft_server'  size='30' type='text'/>";
	html[i++] = "</TD>";
	html[i++] = "</TR>";
	html[i++] = "</TABLE>";
	html[i++] = "<BR/>";
	html[i++] = "<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\"><tbody><tr class=\"ZOptionsHeaderRow\"><td class=\"ZOptionsHeaderL\"><div class=\"ImgPrefsHeader_L\"/></td><td class=\"ZOptionsHeader ImgPrefsHeader\">My Phone Numbers</td><td class=\"ZOptionsHeaderR\"><div class=\"ImgPrefsHeader_R\"/></td></tr></tbody></table>";
	html[i++] = "<TABLE>";
	html[i++] = "<TR>";
	html[i++] = "<TD>";
	html[i++] = "Work Phone Number:";
	html[i++] = "</TD><TD>";
	html[i++] =" <input id='broadsoft_fromWorkNumber'  size='30' type='text'/>";
	html[i++] = "</TD>";
	html[i++] = "</TR>";
	html[i++] = "<TR>";
	html[i++] = "<TD>";
	html[i++] = "Mobile Phone Number:";
	html[i++] = "</TD><TD>";
	html[i++] =" <input id='broadsoft_fromMobileNumber'  size='30' type='text' />";
	html[i++] = "</TD>";
	html[i++] = "</TR>";
	html[i++] = "<TR>";
	html[i++] = "<TD>";
	html[i++] = "House Phone Number:";
	html[i++] = "</TD><TD>";
	html[i++] =" <input id='broadsoft_fromHouseNumber'  size='30' type='text'/>";
	html[i++] = "</TD>";
	html[i++] = "</TR>";
	html[i++] = "</TABLE>";
	html[i++] = "<BR/>";
	html[i++] = "<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\"><tbody><tr class=\"ZOptionsHeaderRow\"><td class=\"ZOptionsHeaderL\"><div class=\"ImgPrefsHeader_L\"/></td><td class=\"ZOptionsHeader ImgPrefsHeader\">Forwarding Options</td><td class=\"ZOptionsHeaderR\"><div class=\"ImgPrefsHeader_R\"/></td></tr></tbody></table>";
	html[i++] = "<DIV>";
	html[i++] = "<input id='broadsoft_enableCallAnyWhere'  name='broadsoft_incomingCallsRadio'  checked type='radio'/>Enable Call Anywhere.<span style=\"color :gray;font-size:11px;\"> Calls all the above phones so you can recieve from any one of them</span>";
	html[i++] = "</DIV>";
	html[i++] = "<BR/>";
	html[i++] = "<DIV>";
	html[i++] = "<input id='broadsoft_enableCallForwarding' name='broadsoft_incomingCallsRadio'  type='radio'/>Forward all incoming call to:";
	html[i++] =" <input id='broadsoft_forwardToPhoneNumber'  size='25' type='text' />";
	html[i++] = "</DIV>";
	html[i++] = "<BR/>";
	html[i++] = "<DIV>";
	html[i++] = "<input id='broadsoft_enableDoNotDisturb' name='broadsoft_incomingCallsRadio'  type='radio'/>Enable 'Do Not Disturb'  <span style=\"color :gray;font-size:11px;\">Forwards all incoming calls to voicemail</span>";
	html[i++] = "</DIV>";
	return html.join("");
};

/**
 * Listens for the OK preferences button.
 * 
 * @see		#_addToolbarBtns
 */
com_zimbra_broadsoft.prototype._okPreferenceBtnListener =
function() {
	var _saveRequired = false;
	if (this.broadsoft_email != document.getElementById("broadsoft_email").value) {
		this.setUserProperty("broadsoft_email", document.getElementById("broadsoft_email").value);
		_saveRequired = true;
	}
	if (this.broadsoft_password != document.getElementById("broadsoft_password").value) {
		this.setUserProperty("broadsoft_password", document.getElementById("broadsoft_password").value);
		_saveRequired = true;
	}
	if (this.broadsoft_server != document.getElementById("broadsoft_server").value) {
		this.setUserProperty("broadsoft_server", document.getElementById("broadsoft_server").value);
		_saveRequired = true;
	}
	if (this.broadsoft_fromWorkNumber != document.getElementById("broadsoft_fromWorkNumber").value) {
		this.setUserProperty("broadsoft_fromWorkNumber", document.getElementById("broadsoft_fromWorkNumber").value);
		_saveRequired = true;
	}
	if (this.broadsoft_fromMobileNumber != document.getElementById("broadsoft_fromMobileNumber").value) {
		this.setUserProperty("broadsoft_fromMobileNumber", document.getElementById("broadsoft_fromMobileNumber").value);
		_saveRequired = true;
	}
	if (this.broadsoft_fromHouseNumber != document.getElementById("broadsoft_fromHouseNumber").value) {
		this.setUserProperty("broadsoft_fromHouseNumber", document.getElementById("broadsoft_fromHouseNumber").value);
		_saveRequired = true;
	}

	if (document.getElementById("broadsoft_enableCallAnyWhere").checked && this.broadsoft_incomingCallsRadio != "CALL_ANYWHERE") {
		this.setUserProperty("broadsoft_incomingCallsRadio", "CALL_ANYWHERE");
		this._callJspForDoNotDisturb("false");
		//this._callJspForCallAnyWhere("true"); enabling callanywhere disables donotcall automatically

		_saveRequired = true;
	}
	if (document.getElementById("broadsoft_enableCallForwarding").checked && this.broadsoft_incomingCallsRadio != "CALL_FORWARDING") {
		this.setUserProperty("broadsoft_incomingCallsRadio", "CALL_FORWARDING");
		this._callJspForDoNotDisturb("false");
		this._callJspForCallAnyWhere("false");
		_saveRequired = true;
	}
	if (document.getElementById("broadsoft_enableDoNotDisturb").checked && this.broadsoft_incomingCallsRadio != "CALL_DONOTDISTURB") {
		this.setUserProperty("broadsoft_incomingCallsRadio", "CALL_DONOTDISTURB");
		this._callJspForDoNotDisturb("true");
		this._callJspForCallAnyWhere("false");
		_saveRequired = true;
	}

	if (this.broadsoft_forwardToPhoneNumber != document.getElementById("broadsoft_forwardToPhoneNumber").value) {
		this.setUserProperty("broadsoft_forwardToPhoneNumber", document.getElementById("broadsoft_forwardToPhoneNumber").value);
		_saveRequired = true;
	}

	//this._mainDlg.popdown();
	if (_saveRequired) {
		this.saveUserProperties(new AjxCallback(this, this._showPreferenceSavedMsg));
	}
};

/**
 * Shows the preference saved message.
 * 
 */
com_zimbra_broadsoft.prototype._showPreferenceSavedMsg =
function() {
	var transitions = [ ZmToast.FADE_IN, ZmToast.PAUSE, ZmToast.PAUSE, ZmToast.FADE_OUT ];
	appCtxt.getAppController().setStatusMsg("Preferences Saved", ZmStatusView.LEVEL_INFO, null, transitions);
	this._setZimletCurrentPreferences();
};




