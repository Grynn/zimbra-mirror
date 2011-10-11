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
 * @class This class represents the Mitel zimlet.
 *
 * @extends ZmZimletBase
 */
function ZmMitelZimlet() {
}
ZmMitelZimlet.prototype = new ZmZimletBase();
ZmMitelZimlet.prototype.constructor = ZmMitelZimlet;


ZmMitelZimlet.prototype.init = function() {
	ZmZimletBase.prototype.init.apply(this, arguments);
	this.toPhoneNumber = "16504274506";

	this.mitel_email = "raja";
	this.mitel_password = "password";
	this.mitel_mailbox_id = "1010";
	this.mitel_voice_pin = "1234";
	this.mitel_server = this.getConfig("mitel_server");

	var regexps = [
        new RegExp("\\b" + this.getMessage("localPhoneRegEx") + "\\b","ig")
    ];
	this.regexps = regexps;
	this.countryCode = this.getMessage("countryCode");
	if(!this.countryCode) {
		this.countryCode = 1;
	}
	this._showFromPhoneDlg();
};

ZmMitelZimlet.prototype.match =
function(line, startIndex) {
	var a = this.regexps;
	var ret = null;
	for (var i = 0; i < a.length; ++i) {
		var re = a[i];
		re.lastIndex = startIndex;
		var m = re.exec(line);
        if (m) {
            if (!ret || m.index < ret.index) {
                ret = m;
            }
        }
	}
	return ret;
};

/**
 * This method is called when the zimlet tool-tip is popped-up.
 *
 */
ZmMitelZimlet.prototype.toolTipPoppedUp = function(spanElement, contentObjText, matchContext, canvas) {
	var subs = {contentObjText: contentObjText, phoneStr: ZmMsg.phone};
	canvas.innerHTML = AjxTemplate.expand("com_zimbra_mitel.templates.ZmMitel#Tooltip", subs);
};


ZmMitelZimlet.prototype.clicked = function(myElement, toPhoneNumber) {
	this.toPhoneNumber = toPhoneNumber;
	this._showFromPhoneDlg();
};


ZmMitelZimlet.prototype._showFromPhoneDlg = function() {
	if (!this.zmMitelFromPhoneDlg) {
		this.zmMitelFromPhoneDlg = new ZmMitelFromPhoneDlg(this.getShell(), this,
				this._destinationsInZimbra);
	}
	this.zmMitelFromPhoneDlg.toPhoneNumber = this.toPhoneNumber;
	this.zmMitelFromPhoneDlg.showDialog();
};
