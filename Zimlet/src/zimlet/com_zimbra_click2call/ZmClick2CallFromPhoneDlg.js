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

ZmClick2CallFromPhoneDlg = function(shell, parent) {
    this.zimlet = parent;
    this.toPhoneNumber = "";
    this._dialogView = new DwtComposite(appCtxt.getShell());
    this._dialogView.setSize(300, 125);
    DwtDialog.call(this, {
        parent: shell,
        className: "ZmClick2CallFromPhoneDlg",
        title: this.zimlet.getMessage("fromPhoneDlgTitle"),
        view: this._dialogView,
        standardButtons: [DwtDialog.NO_BUTTONS],
        mode: DwtBaseDialog.MODELESS
    });

    this._setWhiteBackground();
	if (!this.click2CallDlg) {
		this.click2CallDlg = new ZmClick2CallDlg(this.zimlet.getShell(), this.zimlet);
	}
    //set this to null otherwise esc will throw expn
    this._buttonDesc = {};
    this._isLoaded = false;
	this.RE = new RegExp("\\+?\\b\\d([0-9\\(\\)\\.\\s\\-]){8,20}\\d\\b", "g");
};

ZmClick2CallFromPhoneDlg.prototype = new DwtDialog;
ZmClick2CallFromPhoneDlg.prototype.constructor = ZmClick2CallFromPhoneDlg;

ZmClick2CallFromPhoneDlg.prototype.CONTROLS_TEMPLATE = null;

//Set WindowInnerContainer cell's bg to white
ZmClick2CallFromPhoneDlg.prototype._setWhiteBackground = function() {
    var el = this._dialogView.getHtmlElement();
    while (el && el.className && el.className.indexOf("WindowInnerContainer") == -1) {
        el = el.parentNode;
    }
    if (el == null) {
        return;
    }
    el.style.backgroundColor = "white";
};

ZmClick2CallFromPhoneDlg.prototype.showDialog =
function() {

    if (!this._isLoaded) {
		appCtxt.setStatusMsg(ZmMsg.loading);
        this._getVoiceInfoAndShowDlg();
    } else {
        this.setToPhoneText();
        this.popup();
    }
};

ZmClick2CallFromPhoneDlg.prototype._getVoiceInfoAndShowDlg =
function() {
    var soapDoc = AjxSoapDoc.create("GetVoiceInfoRequest", "urn:zimbraVoice");
    var respCallback = new AjxCallback(this, this._handleResponseVoiceInfo);
    var respErrorCallback = new AjxCallback(this, this._handleErrorResponseVoiceInfo);
    var params = {
        soapDoc: soapDoc,
        asyncMode: true,
        noBusyOverlay: true,
        callback: respCallback,
        errorCallback: respErrorCallback
    };
    appCtxt.getAppController().sendRequest(params);
    this._gettingVoiceInfo = true;

};
ZmClick2CallFromPhoneDlg.prototype._handleResponseVoiceInfo = function(result) {
    var response = result.getResponse();
    if (response.GetVoiceInfoResponse && response.GetVoiceInfoResponse.phone) {
        var phones = response.GetVoiceInfoResponse.phone;
        this._createCallFromHtml(phones);
        this.setToPhoneText();
		this._addMinimizeAndCloseBtns();
        this.popup();
        this._isLoaded = true;
    }
};

ZmClick2CallFromPhoneDlg.prototype._addMinimizeAndCloseBtns = function() {
	var html = ["<table><tr><td class='minWidth' ></td>",
		"<td class='",this._titleEl.className,"' id='", this._titleEl.id,"'> ", this._titleEl.innerHTML, "</td>",
		"<td  width='18px' align=right ><div style='cursor:pointer;' id='fromPhoneDlg_minMaxBtn' class='ImgClick2Call-minimize-icon' /></td>",
		"<td  width='18px' align=right ><div style='cursor:pointer;' id='fromPhoneDlg_closeBtn' class='ImgClose' /></td>",
		"</tr></table>"];

	this._titleEl.parentNode.innerHTML = html.join("");
	this._minMaxeDlgBtn = document.getElementById("fromPhoneDlg_minMaxBtn");
	this._minMaxeDlgBtn.onclick = AjxCallback.simpleClosure(this._handleMinMaxDlg, this);
	this._closeDlgBtn = document.getElementById("fromPhoneDlg_closeBtn");
	this._closeDlgBtn.onclick = AjxCallback.simpleClosure(this._handleCloseDlg, this);
};

ZmClick2CallFromPhoneDlg.prototype._handleMinMaxDlg = function() {
	if(this._minMaxeDlgBtn.className == "ImgClick2Call-minimize-icon") {
		this._dlgTopPosB4Minimize = (this.getHtmlElement().style.top).replace("px", "");
		this._minMaxeDlgBtn.className = "ImgClick2Call-maximize-icon ";
		this.getHtmlElement().style.top = (document.body.offsetHeight - 25) + "px";
	} else if(this._minMaxeDlgBtn.className == "ImgClick2Call-maximize-icon ") {
		this._minMaxeDlgBtn.className = "ImgClick2Call-minimize-icon";
		this.getHtmlElement().style.top = this._dlgTopPosB4Minimize + "px";
	}
};

ZmClick2CallFromPhoneDlg.prototype._handleCloseDlg = function() {
	this.popdown();
};

ZmClick2CallFromPhoneDlg.prototype._createCallFromHtml = function(phones) {
	var subs = {
		fromStr : this.zimlet.getMessage("from"),
		toStr : this.zimlet.getMessage("to")
	};
	this._dialogView.getHtmlElement().innerHTML =  AjxTemplate.expand("com_zimbra_click2call.templates.ZmClick2Call#fromPhoneDlg", subs);
    this._setFromPhoneMenu(phones);
    var btn = new DwtButton({
        parent: this.zimlet.getShell()
    });
    btn.setText(this.zimlet.getMessage("call"));
    //button name
    btn.setImage("Telephone");
    btn.addSelectionListener(new AjxListener(this, this._makeCall));
    document.getElementById("click2CallFromPhoneDlg_callBtn").appendChild(btn.getHtmlElement());
};

ZmClick2CallFromPhoneDlg.prototype._setFromPhoneMenu = function(phones) {
    if (!phones) {
        return;
    }
    var html = [];
    var i = 0;
    html[i++] = "<select id=\"click2CallFromPhoneDlg_callFromMenu\" style='width:200px' >";
    for (var j = 0; j < phones.length; j++) {
        var phone = phones[j];
        if (!phone.c2cDeviceId) {
            continue;
        }
        html[i++] = ["<option value='", phone.c2cDeviceId, "'>", phone.label, " (", phone.name, ")</option>"].join("");

    }
    html[i++] = "</select>";
    document.getElementById("click2CallFromPhoneDlg_menuDiv").innerHTML = html.join("");
};

ZmClick2CallFromPhoneDlg.prototype._makeCall = function() {
    //set the value from the TO field
    this.click2CallDlg.toPhoneNumber = document.getElementById("click2CallDlg_callToPHText").value;
    this.click2CallDlg.fromPhoneNumber = document.getElementById("click2CallFromPhoneDlg_callFromMenu").value;
	if(!this.isValidPhoneNumber(this.click2CallDlg.toPhoneNumber))  {
		var errorDialog = appCtxt.getErrorDialog();
		errorDialog.reset();
		errorDialog.setMessage(this.zimlet.getMessage("notAValidPhoneNumber"), this.zimlet.getMessage("notAValidPhoneNumber"), DwtMessageDialog.CRITICAL_STYLE, ZmMsg.zimbraTitle);
		errorDialog.popup();
		return;
	}
	 this.popdown();
    this.click2CallDlg.clickToCall();
};

ZmClick2CallFromPhoneDlg.prototype.isValidPhoneNumber = function(phoneNumber) {
	return this.RE.test(phoneNumber);
};

ZmClick2CallFromPhoneDlg.prototype.setToPhoneText = function() {
    document.getElementById("click2CallDlg_callToPHText").value = this.toPhoneNumber ? this.toPhoneNumber : "";
};

ZmClick2CallFromPhoneDlg.prototype.popdown =
function() {
    ZmDialog.prototype.popdown.call(this);
};
