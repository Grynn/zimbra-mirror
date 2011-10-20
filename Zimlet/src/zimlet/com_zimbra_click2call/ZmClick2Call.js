/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Zimlets
 * Copyright (C) 2009, 2010, 2011 Zimbra, Inc.
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
 * @class This class represents the Click2Call zimlet.
 *
 * @extends ZmZimletBase
 */
function ZmClick2CallZimlet() {
}
ZmClick2CallZimlet.prototype = new ZmZimletBase();
ZmClick2CallZimlet.prototype.constructor = ZmClick2CallZimlet;


ZmClick2CallZimlet.prototype.init = function() {
	ZmZimletBase.prototype.init.apply(this, arguments);
	this.toPhoneNumber = "16504274506";

	this.email = "raja";
	this.password = "password";
	this.mailbox_id = "1010";
	this.voice_pin = "1234";
	this.server = this.getConfig("click2call_server");

	var regexps = [
        new RegExp("\\b" + this.getMessage("localPhoneRegEx") + "\\b","ig")
    ];
	this.regexps = regexps;
	this.countryCode = this.getMessage("countryCode");
	if(!this.countryCode) {
		this.countryCode = 1;
	}
};

ZmClick2CallZimlet.prototype.toolTipPoppedUp = function(spanElement, contentObjText, matchContext, canvas) {
	var subs = {contentObjText: contentObjText, phoneStr: ZmMsg.phone};
	canvas.innerHTML = AjxTemplate.expand("com_zimbra_click2call.templates.ZmClick2Call#Tooltip", subs);
};

ZmClick2CallZimlet.prototype.clicked = function(myElement, toPhoneNumber) {
	this.toPhoneNumber = toPhoneNumber;
	this._showFromPhoneDlg();
};

ZmClick2CallZimlet.prototype.display = function(toPhoneNumber) {
	this.toPhoneNumber = toPhoneNumber;
	this._showFromPhoneDlg();
};


ZmClick2CallZimlet.prototype._showFromPhoneDlg = function() {
	if (!this.zmFromPhoneDlg) {
		this.zmFromPhoneDlg = new ZmClick2CallFromPhoneDlg(this.getShell(), this,
				this._destinationsInZimbra);
	}
	this.zmFromPhoneDlg.toPhoneNumber = this.toPhoneNumber;
	this.zmFromPhoneDlg.showDialog();
};
