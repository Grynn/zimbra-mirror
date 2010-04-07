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
 * This zimlet checks for x-mailer which contains name of the email-client that was used to 
 * send the email and displays that information when the mail is opened
 */
function com_zimbra_xmailer() {
}

com_zimbra_xmailer.prototype = new ZmZimletBase();
com_zimbra_xmailer.prototype.constructor = com_zimbra_xmailer;

com_zimbra_xmailer.prototype.init =
function() {
	this.turnONxmailerZimlet = this.getUserProperty("turnONxmailerZimlet") == "true";
	if (!this.turnONxmailerZimlet)
		return;

	if (appCtxt.get(ZmSetting.MAIL_ENABLED)) {
		AjxPackage.require({name:"MailCore", callback:new AjxCallback(this, this._applyRequestHeaders)});
	}
};


com_zimbra_xmailer.prototype._applyRequestHeaders =
function() {	
	ZmMailMsg.requestHeaders["X-Mailer"] = null;
	 ZmMailMsgView.displayAdditionalHdrsInMsgView["X-Mailer"] = "Sent Using:";
};

com_zimbra_xmailer.prototype.doubleClicked = function() {
	this.singleClicked();
};


com_zimbra_xmailer.prototype.singleClicked = function() {
    this.showPrefDialog();
};

com_zimbra_xmailer.prototype.showPrefDialog =
function() {
    //if zimlet dialog already exists...
    if (this.pbDialog) {
        this.pbDialog.popup();
        return;
    }
    this.pView = new DwtComposite(this.getShell());
    this.pView.getHtmlElement().innerHTML = this.createPrefView();

    if (this.getUserProperty("turnONxmailerZimlet") == "true") {
        document.getElementById("turnONxmailerZimlet_chkbx").checked = true;
    }

    this.pbDialog = this._createDialog({title:"'X-Mailer' Zimlet Preferences", view:this.pView, standardButtons:[DwtDialog.OK_BUTTON]});
    this.pbDialog.setButtonListener(DwtDialog.OK_BUTTON, new AjxListener(this, this._okBtnListner));
    this.pbDialog.popup();

};


com_zimbra_xmailer.prototype.createPrefView =
function() {
    var html = new Array();
    var i = 0;
    html[i++] = "<DIV>";
    html[i++] = "<input id='turnONxmailerZimlet_chkbx'  type='checkbox'/>Enable 'X-Mailer' Zimlet (Changing this would refresh browser)";
    html[i++] = "</DIV>";
    return html.join("");

};

com_zimbra_xmailer.prototype._okBtnListner =
function() {
	this._reloadRequired = false;
    if (document.getElementById("turnONxmailerZimlet_chkbx").checked) {
		if(!this.turnONxmailerZimlet){
			this._reloadRequired = true;
		}
        this.setUserProperty("turnONxmailerZimlet", "true", true);
    } else {
        this.setUserProperty("turnONxmailerZimlet", "false", true);
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