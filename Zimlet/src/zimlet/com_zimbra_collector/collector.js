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
 * Zimlet to collect addresses in messages.
 * 
 * NOTE: This zimlet should be activated based on user's COS (Contacts app should be enabled)
 * 
 * @author Parag Shah.
 */
function com_zimbra_collector_HandlerObject() {
}

com_zimbra_collector_HandlerObject.prototype = new ZmZimletBase();
com_zimbra_collector_HandlerObject.prototype.constructor = com_zimbra_collector_HandlerObject;

/**
 * Simplify handler object
 *
 */
var CollectorZimlet = com_zimbra_collector_HandlerObject;

/**
 * Defines the contact checkbox.
 */
CollectorZimlet.CHECKBOX_NAME = Dwt.getNextId();

/**
 * This method is called when an item is dropped on the Zimlet item as realized
 * in the UI. At this point the Zimlet should perform the actions it needs to
 * for the drop. This method defines the following formal parameters:
 * 
 * @param	zmObject
 * @param	canvas
 */
CollectorZimlet.prototype.doDrop =
function(zmObject) {

	// create a dialog if one does not already exist
	if (!this._collectorDialog) {
		this._initialize();
	}

	// initialize participants array containing aggregated participants
	this._participants.length = 0;

	this._createHtml(zmObject);
	this._collectorDialog.popup();
};

//
// Private methods
//

/**
 * Initializes the zimlet.
 */
CollectorZimlet.prototype._initialize = 
function() {
	this._parentView = new DwtComposite(this.getShell());
	this._parentView.setSize("400", "150");
	this._parentView.getHtmlElement().style.overflow = "auto";

	var dialogArgs = {
			title	: this.getMessage("CollectorZimlet_dialog_contact_title"),
			view	: this._parentView,
			parent	: this.getShell()
			};
	
	this._collectorDialog = this._createDialog(dialogArgs);
	this._collectorDialog.setButtonListener(DwtDialog.OK_BUTTON, new AjxListener(this, this._collectorDialogOkListener));

	// get reference to user's contact list (making sure contacts app is enabled as well)
	this._contactList = AjxDispatcher.run("GetContacts");

	this._participants = new Array();
};

/**
 * Creates the contact collector HTML.
 * 
 * @param	{ZmConv|ZmMailMsg}		zmObjects
 */
CollectorZimlet.prototype._createHtml = 
function(zmObject) {
	var html = new Array();
	var i = 0;

	if (this._contactList) {
		if (zmObject.length == null)
			zmObject = [zmObject];

		html[i++] = "<table border=0 cellpadding=1 cellspacing=1 width=100%>";
		for (var j = 0; j < zmObject.length; j++) {
			// collect all the participants differently based on whether obj is ZmConv or ZmMsg
			var obj = zmObject[j];
			var participants = [];
			if (obj.participants) {
				participants = obj.participants;
			} else {
				for (var m = 0; m < obj.from.length; m++)
					participants.push(obj.from[m]);
				for (var m = 0; m < obj.cc.length; m++)
					participants.push(obj.cc[m]);
			}
			for (var k = 0; k < participants.length; k++) {
				var participant = participants[k];
				var contact = this._contactList.getContactByEmail(participant.address);

				html[i++] = "<tr><td width=1%><input type='checkbox' ";
				// if user already has this contact, disable the checkbox
				if (contact) {
					html[i++] = "checked disabled>";
				} else {
					html[i++] = "name='";
					html[i++] = CollectorZimlet.CHECKBOX_NAME;
					html[i++] = "'>";
					// save this participant for later use
					this._participants.push(participant);
				}
				html[i++] = "</td><td>";
				html[i++] = participant.name || participant.dispName;
				html[i++] = "</td><td>";
				html[i++] = participant.address;
				html[i++] = "</td></tr>";
			}
		}
		html[i++] = "</table>";
	} else {
		html[i++] = this.getMessage("CollectorZimlet_addedError");
	}

	this._parentView.getHtmlElement().innerHTML = html.join("");
};

/**
 * Handles the OK button.
 * 
 * @see		_initialize
 */
CollectorZimlet.prototype._collectorDialogOkListener = 
function(ev) {
	var soapDoc = null;

	// set up a BatchRequest for all contacts that need to be added
	var checkedParts = document.getElementsByName(CollectorZimlet.CHECKBOX_NAME);

	for (var i = 0; i < checkedParts.length; i++) {
		if (checkedParts[i].checked) {
			if (!soapDoc) {
				soapDoc = AjxSoapDoc.create("BatchRequest", "urn:zimbra");
				soapDoc.setMethodAttribute("onerror", "continue");
			}

			// create a temporary ZmContact so we can get the right parts out of it
			var contact = new ZmContact(null);
			contact.initFromEmail(this._participants[i]);

			var createContactReq = soapDoc.set("CreateContactRequest",null,null,"urn:zimbraMail");
			var doc = soapDoc.getDoc();
			var cn = doc.createElement("cn");
			createContactReq.appendChild(cn);

			// add first name
			var fn = doc.createElement("a");
			fn.setAttribute("n", ZmContact.F_firstName);
			fn.appendChild(doc.createTextNode(ZmContact.getAttr(contact, ZmContact.F_firstName)));
			cn.appendChild(fn);

			// add last name
			var ln = doc.createElement("a");
			ln.setAttribute("n", ZmContact.F_lastName);
			ln.appendChild(doc.createTextNode(ZmContact.getAttr(contact, ZmContact.F_lastName)));
			cn.appendChild(ln);

			// add email address
			var em = doc.createElement("a");
			em.setAttribute("n", ZmContact.F_email);
			em.appendChild(doc.createTextNode(ZmContact.getAttr(contact, ZmContact.F_email)));
			cn.appendChild(em);
		}
	}

	if (soapDoc) {
		// finally, send the BatchRequest to the server
		var respCallback = new AjxCallback(this, this._handleResponseCreate);
		appCtxt.getAppController().sendRequest({soapDoc:soapDoc, asyncMode:true, callback:respCallback});
	} else {
		this._collectorDialog.popdown();
	}
};

/**
 * Handles the create contacts callback.
 * 
 * @see		_collectorDialogOkListener
 */
CollectorZimlet.prototype._handleResponseCreate =
function(result) {
	this._collectorDialog.popdown();

	var numAdded = result.getResponse().BatchResponse.CreateContactResponse.length;
	var msgDialog = appCtxt.getMsgDialog();
	var msg = AjxMessageFormat.format(this.getMessage("CollectorZimlet_addedSuccess"), numAdded);
	
	msgDialog.setMessage(msg, DwtMessageDialog.INFO_STYLE);
	msgDialog.popup();
};
