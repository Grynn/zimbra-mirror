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
 * This zimlet checks for x-mailer message header, which contains name of the email-client that was used to 
 * send the email. The x-mailer is displayed below the message subject when the message is opened.
 * 
 */
function com_zimbra_xmailer_HandlerObject() {
}

com_zimbra_xmailer_HandlerObject.prototype = new ZmZimletBase();
com_zimbra_xmailer_HandlerObject.prototype.constructor = com_zimbra_xmailer_HandlerObject;

/**
 * Simplify handler object
 *
 */
var XMailerZimlet = com_zimbra_xmailer_HandlerObject;

/**
 *  Defines "enable" user property.
 */
XMailerZimlet.USER_PROPERTY_ENABLE = "turnONxmailerZimlet";

/**
 *  Defines "enable checkbox" element id.
 */
XMailerZimlet.ELEMENT_ID_ENABLE_CHECKBOX = "turnONxmailerZimlet_chkbx";

/**
 * Initializes the zimlet.
 */
XMailerZimlet.prototype.init =
function() {
	this.turnONxmailerZimlet = this.getUserProperty(XMailerZimlet.USER_PROPERTY_ENABLE) == "true";
	if (!this.turnONxmailerZimlet)
		return;

	if (appCtxt.get(ZmSetting.MAIL_ENABLED)) {
		AjxPackage.require({name:"MailCore", callback:new AjxCallback(this, this._applyRequestHeaders)});
	}
};

/**
 * Applies the request headers.
 * 
 */
XMailerZimlet.prototype._applyRequestHeaders =
function() {	
	ZmMailMsg.requestHeaders["X-Mailer"] = null;
	 ZmMailMsgView.displayAdditionalHdrsInMsgView["X-Mailer"] = this.getMessage("XMailerZimlet_label_sent");
};

/**
 * Called by the framework on double-click.
 */
XMailerZimlet.prototype.doubleClicked = function() {
	this.singleClicked();
};

/**
 * Called by the framework on single-click.
 */
XMailerZimlet.prototype.singleClicked = function() {
    this._showPrefDialog();
};

/**
 * Shows the pref dialog.
 * 
 */
XMailerZimlet.prototype._showPrefDialog =
function() {
    //if zimlet dialog already exists...
    if (this.pbDialog) {
        this.pbDialog.popup();
        return;
    }
    this.pView = new DwtComposite(this.getShell());
    this.pView.getHtmlElement().innerHTML = this._createPrefView();

    if (this.getUserProperty("turnONxmailerZimlet") == "true") {
        document.getElementById("turnONxmailerZimlet_chkbx").checked = true;
    }

    var dialog_args = {
    		title	: this.getMessage("XMailerZimlet_dialog_title"),
    		view	: this.pView,
    		parent	: this.getShell(),
    		standardButtons	: [DwtDialog.OK_BUTTON]
    	};
    
    this.pbDialog = new ZmDialog(dialog_args);
    this.pbDialog.setButtonListener(DwtDialog.OK_BUTTON, new AjxListener(this, this._okBtnListner));
    this.pbDialog.popup();

};

/**
 * Creates the prefs view.
 * 
 * @see		_showPrefDialog
 */
XMailerZimlet.prototype._createPrefView =
function() {
    var html = new Array();
    var i = 0;
    html[i++] = "<DIV>";
    html[i++] = "<input id='";
    html[i++] = XMailerZimlet.ELEMENT_ID_ENABLE_CHECKBOX;
    html[i++] = "' type='checkbox'/>";
    html[i++] = this.getMessage("XMailerZimlet_dialog_enable");
    html[i++] = "</DIV>";
    return html.join("");

};

/**
 * Handles the OK button click.
 * 
 * @see		_showPrefDialog
 */
XMailerZimlet.prototype._okBtnListner =
function() {
	this._reloadRequired = false;
    if (document.getElementById(XMailerZimlet.ELEMENT_ID_ENABLE_CHECKBOX).checked) {
		if(!this.turnONxmailerZimlet){
			this._reloadRequired = true;
		}
        this.setUserProperty(XMailerZimlet.USER_PROPERTY_ENABLE, "true", true);
    } else {
        this.setUserProperty(XMailerZimlet.USER_PROPERTY_ENABLE, "false", true);
		if(this.turnONxmailerZimlet)
			this._reloadRequired = true;
    }
    this.pbDialog.popdown();

	if(this._reloadRequired) {
		window.onbeforeunload = null;
		var url = AjxUtil.formatUrl({});
		ZmZimbraMail.sendRedirect(url);
	}
};