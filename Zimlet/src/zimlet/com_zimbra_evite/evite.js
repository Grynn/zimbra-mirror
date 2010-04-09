/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Zimlets
 * Copyright (C) 2006, 2007, 2008, 2009, 2010 Zimbra, Inc.
 *
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.3 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 */

/**
 * Constructor.
 */
function Com_Zimbra_Evite_HandlerObject() {
}

Com_Zimbra_Evite_HandlerObject.prototype = new ZmZimletBase();
Com_Zimbra_Evite_HandlerObject.prototype.constructor = Com_Zimbra_Evite_HandlerObject;

/**
 *Shorten Zimlet Handler class name
 */
var EviteZimlet = Com_Zimbra_Evite_HandlerObject;

EviteZimlet.CALENDAR_VIEW = "appointment";

/**
 * Defines the "username" user property.
 */
EviteZimlet.USER_PROP_USERNAME = "user";
/**
 * Defines the "password" user property.
 */
EviteZimlet.USER_PROP_PASSWORD = "passwd";

/**
 * Defines the "auth URL" config property.
 */
EviteZimlet.CONFIG_PROP_AUTH_URL = "authUrl";
/**
 * Defines the "my URL" config property.
 */
EviteZimlet.CONFIG_PROP_MY_URL = "myUrl";

/**
 * Defines the "sync" menu item.
 */
EviteZimlet.MENU_ITEM_ID_SYNC = "sync";
/**
 * Defines the "pref" menu item.
 */
EviteZimlet.MENU_ITEM_ID_PREF = "pref";

/**
 * Initializes the zimlet.
 *
 */
EviteZimlet.prototype.init =
function() {
	this._login(null, true);
};

/**
 * Logs users into Evite
 *
 * @param {AjxCallback} callback The callback
 * @param {boolean}  init if<code>true</code> login is called from init function
 */
EviteZimlet.prototype._login =
function(callback, init) {
	if (callback == null) {
		callback = false;
	}
	var user = this.getUserProperty(EviteZimlet.USER_PROP_USERNAME);
	var passwd = this.getUserProperty(EviteZimlet.USER_PROP_PASSWORD);
	if (user && passwd) {
		this._listFolders();
		this._eviteAuth(user, passwd, callback, init);
	} else if (!init || callback) {
		this.createPropertyEditor(new AjxCallback(this, this._login, [ callback ]));
	}
};

/**
 * Called by Zimbra framework when a menu item was selected
 *
 * @param {String} itemId  id of the menuitem
 */
EviteZimlet.prototype.menuItemSelected =
function(itemId) {
	switch (itemId) {
	    case EviteZimlet.MENU_ITEM_ID_SYNC:
	    	this._getCalendarInvites();
	    	break;
	    case EviteZimlet.MENU_ITEM_ID_PREF:
			this.createPropertyEditor();
			break;
	}
};

/**
 * Generates a random number.
 *
 * @return	{number}		a random number
 */
EviteZimlet.prototype._getRandomNumber =
function() {
	return Math.round((Math.random()*1000)+1);
};

/**
 * Authenticates user to Evite application
 *
 * @param {String} user   username
 * @param {String} passwd  password
 * @param {AjxCallback} callback   callback function
 * @param {boolean} init  if<code>true</code> login is called from init function
 */
EviteZimlet.prototype._eviteAuth =
function(user, passwd, callback, init) {
	var authUrl = [this.getConfig(EviteZimlet.CONFIG_PROP_AUTH_URL),'?email=',user,'&pass=',passwd,'&src=zimbra&rndm=',this._getRandomNumber()].join("");
	var url = ZmZimletBase.PROXY + AjxStringUtil.urlComponentEncode(authUrl);
	AjxRpc.invoke(null, url, null, new AjxCallback(this, this._authCallbackHandler, [ init, callback ]), true);
};

/**
 *Makes http call to get Calendar invites
 */
EviteZimlet.prototype._getCalendarInvites =
function() {
	if (!this.userID) {
		this._login(new AjxCallback(this, this._getCalendarInvites), false);
		return;
	}
	var myUrl = [this.getConfig(EviteZimlet.CONFIG_PROP_MY_URL),'?userID=',this.userID,'&src=zimbra&rndm=',this._getRandomNumber()].join("");
	var url = ZmZimletBase.PROXY + AjxStringUtil.urlComponentEncode(myUrl);
	AjxRpc.invoke(null, url, null, new AjxCallback(this, this._getCalInvitesCallback), true);
};

/**
 * Helps finding xml element's child
 *
 * @param {XMLElement} elem xml element
 * @param {String} nodeName xml node name
 * @return {XMLElement} returns child element
 */
EviteZimlet.prototype._findChild =
function(elem, nodeName) {
	return this.findSibling(elem.firstChild, nodeName);
};

/**
 * Helps finding xml element's sibling
 *
 * @param {XMLElement} elem xml element
 * @param {String} nodeName xml node name
 * @return {XMLElement} returns sibling element
 */
EviteZimlet.prototype.findSibling =
function(elem, nodeName) {
	var child;
	for (child = elem; child != null; child = child.nextSibling){
		if (child.nodeName == nodeName) {
			return child;
		}
	}
	throw new AjxException(this.getMessage("EviteZimlet_CannotFindNode")+nodeName, AjxException.INVALID_PARAM, "findSibling");
};

/**
 * Authintication callback handler
 *
 * @param {boolean} init if <cod>true</code> it was originated from .init
 * @param {AjxCallback} callback a Callback function
 * @param {Object} result Authintication response object
 */
EviteZimlet.prototype._authCallbackHandler =
function(init, callback, result) {
	var elem;
	if (!result.success) {
		return;
	}

	try {
		if (!result.xml || !result.xml.documentElement) {
			var doc = AjxXmlDoc.createFromXml(result.text);
			elem = doc.getDoc();
		} else {
			elem = result.xml;
		}
		if (elem != null) {
			elem = this._findChild(elem, 'EviteUserInfo');
			elem = this._findChild(elem, 'eviteAuth');
			elem = this._findChild(elem, 'userID');
		}
		
	} catch (ex) {
		appCtxt.getAppController().setStatusMsg(this.getMessage("EviteZimlet_InvalidEviteLogin")+ex.dump(), ZmStatusView.LEVEL_CRITICAL);
		return;
	}

	if (elem.firstChild)
		this.userID = elem.firstChild.data;
		appCtxt.getAppController().setStatusMsg(this.getMessage("EviteZimlet_LoggedIntoEvite"), ZmStatusView.LEVEL_INFO);
	if (!init && (!elem.firstChild || !this.userID)) {
		appCtxt.getAppController().setStatusMsg(this.getMessage("EviteZimlet_InvalidEviteLogin"), ZmStatusView.LEVEL_CRITICAL);
		this.createPropertyEditor(new AjxCallback(this, this._login, [ callback ]));
	}
	//if (callback) callback.run();
};

/**
 * Handles the result of calendar invite call.
 *
 * @param {object} result contains Calendar invite information
 */
EviteZimlet.prototype._getCalInvitesCallback =
function(result) {
	var events, event, elem, appts;
	if (!result.success) {
		return;
	}
	elem = result.xml;
	try {
		appts = this._fetchEviteAppts();
		elem = this._findChild(elem, 'invitations');
		events = this._findChild(elem, 'events');
		for (event = events.firstChild; event != null; event = event.nextSibling) {
			if (event.nodeName != 'event') continue;
			var title, url, evdate, evtime;
			title = "evite: "+this._findChild(event, 'title').firstChild.data;
			url = this._findChild(event, 'url').firstChild.data;
			evdate = this._findChild(event, 'eventDate').firstChild.data;
			evtime = this._findChild(event, 'eventTime').firstChild.data;
			var found = false;
			for (var i = 0; i < appts.size(); i++) {
				var appt = appts.get(i);
				var name = appt.getName();
				var startDate = AjxDateUtil.getTimeStr(appt.startDate, "%Y%n%d");
				if (name == title) {
					found = true;
					break;
				}
			}
			if (!found) {
				this._createAppt(title, url, evdate, evtime);
			}
		}
	} catch (ex) {
		DBG.println(AjxDebug.DBG1, ex.dump());
		return;
	}
};

/**
 *   Returns Zimbra username
 */
EviteZimlet.prototype._getUsername =
function() {
	return appCtxt.get(ZmSetting.USERNAME);
};

/**
 *  Creates soap request to create an appointment in Evite-calendar folder
 *
 * @param {string} title  Appointment subject
 * @param {string} url Evite url
 * @param {date} date  date of the appointment
 * @param {string} time  of the appointment
 */
EviteZimlet.prototype._createAppt =
function(title, url, date, time) {
	if (!this.userID || !this.eviteFolderID) {
		appCtxt.getAppController().setStatusMsg(this.getMessage("EviteZimlet_NotInitiliazed"), ZmStatusView.LEVEL_CRITICAL);
		return;
	}
	var soapDoc = AjxSoapDoc.create("CreateAppointmentRequest", "urn:zimbraMail");
	var m = soapDoc.set("m");
	m.setAttribute("l", this.eviteFolderID);
	var node = soapDoc.set("inv", null, m);
	node.setAttribute("method", "REQUEST");
	node.setAttribute("type", "event");
	node.setAttribute("fb", "B");
	node.setAttribute("transp", "O");
	node.setAttribute("status", "CONF");
	node.setAttribute("allDay", "1");
	node.setAttribute("name", title);
	node.setAttribute("loc", "");

	var subnode = soapDoc.set("s", null, node);
	subnode.setAttribute("d", date);
	subnode = soapDoc.set("e", null, node);
	subnode.setAttribute("d", date);
	subnode = soapDoc.set("or", null, node);
	subnode.setAttribute("a", this._getUsername());

	node = soapDoc.set("mp", null, m);
	node.setAttribute("ct", "text/plain");
	subnode = soapDoc.set("content", url, node);

	node = soapDoc.set("su", title, m);

	var command = new ZmCsfeCommand();
	var resp = command.invoke({soapDoc: soapDoc});
};

/**
 * Checks if Evite-calendar exists, if so, sets the id or else creates an Evite-calendar and sets the id
 */
EviteZimlet.prototype._listFolders =
function() {
	var soapDoc = AjxSoapDoc.create("GetFolderRequest", "urn:zimbraMail");
	var command = new ZmCsfeCommand();
	var top = command.invoke({soapDoc: soapDoc}).Body.GetFolderResponse.folder[0];

	var folders = top.folder;
	if (folders) {
		for (var i = 0; i < folders.length; i++) {
			var f = folders[i];
			if (f && f.name == 'evite' && f.view == EviteZimlet.CALENDAR_VIEW) {
				this.eviteFolderID = f.id;
				return;
			}
		}
	}
	this._createEviteFolder(top.id);
};

/**
 * Creates Evite Calendar
 *
 * @param {string} parent id of the parent folder
 */
EviteZimlet.prototype._createEviteFolder =
function(parent) {
	var soapDoc = AjxSoapDoc.create("CreateFolderRequest", "urn:zimbraMail");
	var folderNode = soapDoc.set("folder");
	folderNode.setAttribute("name", "evite");
	folderNode.setAttribute("l", parent);
    folderNode.setAttribute("view", EviteZimlet.CALENDAR_VIEW);
	var command = new ZmCsfeCommand();
	var resp = command.invoke({soapDoc: soapDoc});
	var id = resp.Body.CreateFolderResponse.folder[0].id;
	if (!id) {
		throw new AjxException("Cannot create evite folder ", AjxException.INTERNAL_ERROR, "createEviteFolder");
	}
	this.eviteFolderID = id;

	soapDoc = AjxSoapDoc.create("FolderActionRequest", "urn:zimbraMail");
	var actionNode = soapDoc.set("action");
	actionNode.setAttribute("op", "color");
	actionNode.setAttribute("id", id);
	actionNode.setAttribute("color", "6");
	command = new ZmCsfeCommand();
	resp = command.invoke({soapDoc: soapDoc});
};

/**
 *  Returns list of appointments in Evite-Calendar.
 */
EviteZimlet.prototype._fetchEviteAppts =
function() {
	if (!this.eviteFolderID) { return; }

	// for one month ahead.
	var start = new Date();
	start.setHours(0, 0, 0, 0);
	var params = {
		start: start.getTime(),
		end: (start.getTime()+AjxDateUtil.MSEC_PER_DAY * 30),
		fanoutAllDay: true,
		folderIds: this.eviteFolderID
	};

	var controller = AjxDispatcher.run("GetCalController");
	return controller.getApptSummaries(params);
};