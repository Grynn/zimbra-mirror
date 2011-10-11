ZmMitelFromPhoneDlg = function(shell, parent) {
    this.zimlet = parent;
    this.toPhoneNumber = "";
    this._dialogView = new DwtComposite(appCtxt.getShell());
    this._dialogView.setSize(300, 125);
    DwtDialog.call(this, {
        parent: shell,
        className: "ZmMitelFromPhoneDlg",
        title: this.zimlet.getMessage("fromPhoneDlgTitle"),
        view: this._dialogView,
        standardButtons: [DwtDialog.NO_BUTTONS],
        mode: DwtBaseDialog.MODELESS
    });

    this._setWhiteBackground();


	if (!this.zmMitelClick2CallDlg) {
		this.zmMitelClick2CallDlg = new ZmMitelClick2CallDlg(this.zimlet.getShell(), this.zimlet);
	}
    //set this to null otherwise esc will throw expn
    this._buttonDesc = {};
    this._isLoaded = false;
};

ZmMitelFromPhoneDlg.prototype = new DwtDialog;
ZmMitelFromPhoneDlg.prototype.constructor = ZmMitelFromPhoneDlg;

ZmMitelFromPhoneDlg.prototype.CONTROLS_TEMPLATE = null;

//Set WindowInnerContainer cell's bg to white
ZmMitelFromPhoneDlg.prototype._setWhiteBackground = function() {
    var el = this._dialogView.getHtmlElement();
    while (el && el.className && el.className.indexOf("WindowInnerContainer") == -1) {
        el = el.parentNode;
    }
    if (el == null) {
        return;
    }
    el.style.backgroundColor = "white";
};

ZmMitelFromPhoneDlg.prototype.showDialog =
function() {

    if (!this._isLoaded) {
        this._getVoiceInfoAndShowDlg();
    } else {
        this.setToPhoneText();
        this.popup();
    }
};

ZmMitelFromPhoneDlg.prototype._getVoiceInfoAndShowDlg =
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
ZmMitelFromPhoneDlg.prototype._handleResponseVoiceInfo = function(result) {
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

ZmMitelFromPhoneDlg.prototype._addMinimizeAndCloseBtns = function() {
	var html = ["<table><tr><td class='minWidth' ></td>",
		"<td class='",this._titleEl.className,"' id='", this._titleEl.id,"'> ", this._titleEl.innerHTML, "</td>",
		"<td  width='18px' align=right ><div style='cursor:pointer;' id='mitelFromPhoneDlg_minMaxBtn' class='Imgmitel-minimize-icon' /></td>",
		"<td  width='18px' align=right ><div style='cursor:pointer;' id='mitelFromPhoneDlg_closeBtn' class='ImgClose' /></td>",
		"</tr></table>"];

	this._titleEl.parentNode.innerHTML = html.join("");
	this._minMaxeDlgBtn = document.getElementById("mitelFromPhoneDlg_minMaxBtn");
	this._minMaxeDlgBtn.onclick = AjxCallback.simpleClosure(this._handleMinMaxDlg, this);
	this._closeDlgBtn = document.getElementById("mitelFromPhoneDlg_closeBtn");
	this._closeDlgBtn.onclick = AjxCallback.simpleClosure(this._handleCloseDlg, this);
};

ZmMitelFromPhoneDlg.prototype._handleMinMaxDlg = function() {
	if(this._minMaxeDlgBtn.className == "Imgmitel-minimize-icon") {
		this._dlgTopPosB4Minimize = (this.getHtmlElement().style.top).replace("px", "");
		this._minMaxeDlgBtn.className = "Imgmitel-maximize-icon";
		this.getHtmlElement().style.top = (document.body.offsetHeight - 25) + "px";
	} else if(this._minMaxeDlgBtn.className == "Imgmitel-maximize-icon") {
		this._minMaxeDlgBtn.className = "Imgmitel-minimize-icon";
		this.getHtmlElement().style.top = this._dlgTopPosB4Minimize + "px";
	}
};

ZmMitelFromPhoneDlg.prototype._handleCloseDlg = function() {
	this.popdown();
};

ZmMitelFromPhoneDlg.prototype._createCallFromHtml = function(phones) {
	var subs = {
		fromStr : this.zimlet.getMessage("from"),
		toStr : this.zimlet.getMessage("to")
	};
	this._dialogView.getHtmlElement().innerHTML =  AjxTemplate.expand("com_zimbra_mitel.templates.ZmMitel#fromPhoneDlg", subs);
    this._setFromPhoneMenu(phones);
    var btn = new DwtButton({
        parent: this.zimlet.getShell()
    });
    btn.setText(this.zimlet.getMessage("call"));
    //button name
    btn.setImage("Telephone");
    btn.addSelectionListener(new AjxListener(this, this._makeCall));
    document.getElementById("ZmMitelFromPhoneDlg_callBtn").appendChild(btn.getHtmlElement());

    //var callback = AjxCallback.simpleClosure(this.popdown, this);
    //document.getElementById("mitel_clickFromDlg_closeBtn").onclick = callback;
};

ZmMitelFromPhoneDlg.prototype._setFromPhoneMenu = function(phones) {
    if (!phones) {
        return;
    }
    var html = [];
    var i = 0;
    html[i++] = "<select id=\"ZmMitelFromPhoneDlg_callFromMenu\" style='width:200px' >";
    for (var j = 0; j < phones.length; j++) {
        var phone = phones[j];
        if (!phone.c2cDeviceId) {
            continue;
        }
        html[i++] = ["<option value='", phone.c2cDeviceId, "'>", phone.label, " (", phone.name, ")</option>"].join("");

    }
    html[i++] = "</select>";
    document.getElementById("ZmMitelFromPhoneDlg_menuDiv").innerHTML = html.join("");
};

ZmMitelFromPhoneDlg.prototype._makeCall = function() {
    //set the value from the TO field
    this.zmMitelClick2CallDlg.toPhoneNumber = document.getElementById("zmMitelDlg_callToPHText").value;

    this.popdown();
    this.zmMitelClick2CallDlg.fromPhoneNumber = document.getElementById("ZmMitelFromPhoneDlg_callFromMenu").value;
    this.zmMitelClick2CallDlg.clickToCall();
};

ZmMitelFromPhoneDlg.prototype.setToPhoneText = function() {
    document.getElementById("zmMitelDlg_callToPHText").value = this.toPhoneNumber;
};

ZmMitelFromPhoneDlg.prototype.popdown =
function() {
    ZmDialog.prototype.popdown.call(this);
};
