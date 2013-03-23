/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Zimlets
 * Copyright (C) 2013 VMware, Inc.
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
 *
 * @author Eran Yarkon (eyarkon@zimbra.com)
 */
function com_zimbra_linkedinimage_HandlerObject() {
}

com_zimbra_linkedinimage_HandlerObject.prototype = new ZmZimletBase();
com_zimbra_linkedinimage_HandlerObject.prototype.constructor = com_zimbra_linkedinimage_HandlerObject;

/**
 * Simplify handler object
 *
 */
var LinkedInImageZimlet = com_zimbra_linkedinimage_HandlerObject;

/**
 * Initializes the zimlet.
 *
 */
LinkedInImageZimlet.prototype.init = function() {
	this._itemsLeftToScan = 0;
	this._itemsLeftToClear = 0;
	this._updateActionMenu();
	this._loadLinkedIn();
};



LinkedInImageZimlet.prototype.menuItemSelected =
function(itemId) {
	switch (itemId) {
		case "SCAN":
			this._startScanning();
			break;
		case "SIGN_IN":
			this._signIn();
			break;
		case "SIGN_OUT":
			this._signOut();
			break;
		case "CLEAR":
			this._clear();
			break;
	}
};

LinkedInImageZimlet.prototype._startScanning =
function() {
	var contactList = AjxDispatcher.run("GetContacts");
	var contacts = contactList.getVector().getArray();
	this._itemsLeftToScan = this._totalItemsToScan = contacts.length;
	this._successCount = 0;
	this._updateActionMenu();
	appCtxt.setStatusMsg(this.getMessage("LinkedInImageZimlet_linkedInScanningStarted"));
	for (var i = 0; i < contacts.length; i++) {
		var contact = contacts[i];
		contact = contactList._realizeContact(contact);
		this._setLinkedInImage(contact);
	}
};

LinkedInImageZimlet.prototype._updateJob =
function(success) {
	this._itemsLeftToScan--;
	this._successCount += success ? 1 : 0;
	if (this._itemsLeftToScan > 0) {
		return;
	}
	var msg = this.getMessage("LinkedInImageZimlet_linkedInScanningSummary").replace("{0}", this._totalItemsToScan).replace("{1}", this._successCount);
	var dlg = appCtxt.getMsgDialog();
	dlg.reset();
	dlg.setMessage(msg, DwtMessageDialog.INFO_STYLE, this.getMessage("LinkedInImageZimlet_linkedInScanningFinished"));
	dlg.popup();

	this._updateActionMenu();
};

LinkedInImageZimlet.prototype._setLinkedInImage =
function(contact) {
	var firstName = contact.getAttr(ZmContact.F_firstName);
	var lastName = contact.getAttr(ZmContact.F_lastName);
	if (!firstName || !lastName) {
		this._updateJob(false);
		return;
	}
	IN.API.PeopleSearch()
	.fields("pictureUrl")
	.params({"first-name": firstName, "last-name": lastName, count: 1, sort: "distance"})
	.result(this._handleLinkedInImageSearchResponse.bind(this, contact))
	.error(this._handleLinkedInImageSearchError.bind(this, contact));
};

LinkedInImageZimlet.prototype._handleLinkedInImageSearchResponse =
function(contact, result) {
	var people = result.people;
	var image;
	if (result.numResults !== 0 && people.values && people.values.length > 0) {
		image = people.values[0].pictureUrl;
	}
	this._updateJob(image);
	//I set even if null to allow future removal of images (on LinkedIn side) to reflect.
	var attr = {};
	attr[ZmContact.F_zimletImage] = image;
	contact.modify(attr, null, true);
};

LinkedInImageZimlet.prototype._handleLinkedInImageSearchError =
function() {
	this._updateJob(false);
};

LinkedInImageZimlet.prototype._clear =
function() {
	var contactList = AjxDispatcher.run("GetContacts");
	var contacts = contactList.getVector().getArray();
	this._itemsLeftToClear = contacts.length;
	this._updateActionMenu();
	appCtxt.setStatusMsg(this.getMessage("LinkedInImageZimlet_linkedInClearingStarted"));
	for (var i = 0; i < contacts.length; i++) {
		var contact = contacts[i];
		contact = contactList._realizeContact(contact);
		this._itemsLeftToClear--;
		if (!contact.getAttr(ZmContact.F_zimletImage)) {
			continue;
		}
		var attr = {};
		attr[ZmContact.F_zimletImage] = "";
		contact.modify(attr, null, true);
	}
	//this is approximate as the modify requests might have not yet ben finished, but good enough I believe. Can update count in callback instead, but keeping it simple.
	appCtxt.setStatusMsg(this.getMessage("LinkedInImageZimlet_linkedInClearingFinished"));
	this._updateActionMenu();
};

LinkedInImageZimlet.prototype._loadLinkedIn =
function() {
	if (this._linkedInLoaded) {
		return;
	}
	LinkedInImageZimlet.thisZimlet = this; //there's no way to get a specific context for the onLoad in the LI script load
	var script = document.createElement("script");
	script.text = [
		'api_key: ', this.getUserProperty("linkedinZimlet_api_key"),
		'\nonLoad: onLinkedInLoad',
		'\nscope: r_network',
		'\nauthorize: true\n'
	].join("");
	script.type = "text/javascript";
	script.src = "http://platform.linkedin.com/in.js";

	document.getElementsByTagName('head')[0].appendChild(script); //must be done to execute the code in the script tag.
};


LinkedInImageZimlet.prototype._signIn =
function() {
	IN.User.authorize();
};

LinkedInImageZimlet.prototype._signOut =
function() {
	IN.User.logout(this._handleSignOut, this);
};

LinkedInImageZimlet.prototype._handleSignOut =
function() {
	this._linkedInLoggedIn = false;
	this._updateActionMenu();
};


LinkedInImageZimlet.prototype._updateActionMenu =
function() {
	var ctxt = this.xmlObj();
	var menu = ctxt.getPanelActionMenu();
	var signIn = menu.getMenuItem("SIGN_IN");
	var signOut = menu.getMenuItem("SIGN_OUT");
	var scanning = menu.getMenuItem("SCAN");
	var clearing = menu.getMenuItem("CLEAR");
	signIn.setEnabled(!this._linkedInLoggedIn && this._linkedInLoaded);
	scanning.setEnabled(this._linkedInLoggedIn && this._itemsLeftToScan === 0);
	signOut.setEnabled(this._linkedInLoggedIn);
	clearing.setEnabled(this._itemsLeftToClear === 0);
};


LinkedInImageZimlet.prototype.onLinkedInAuth =
function() {
	this._linkedInLoggedIn = true;
	this._updateActionMenu();
};

LinkedInImageZimlet.prototype._afterLinkedInLoaded =
function() {
	this._linkedInLoaded = true;
	this._updateActionMenu();
	IN.Event.on(IN, "auth", this.onLinkedInAuth.bind(this));
};

function onLinkedInLoad() {
	var that = LinkedInImageZimlet.thisZimlet;
	that._afterLinkedInLoaded.call(that);
}


