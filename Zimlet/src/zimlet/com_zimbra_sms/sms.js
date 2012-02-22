/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Zimlets
 * Copyright (C) 2006, 2007, 2009, 2010 Zimbra, Inc.
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

//////////////////////////////////////////////////////////////
//  Zimlet to handle SMS alerts                             //
//  @author Satish Dharmaraj                                //
//  @author Raja Rao DV(rrao@zimbra.com)                    //
//////////////////////////////////////////////////////////////

function Com_Zimbra_sms() {
}

Com_Zimbra_sms.authNS = "http://upsidewireless.com/webservice/authentication";
Com_Zimbra_sms.autoURI = "http://api.upsidewireless.com/soap/Authentication.asmx";
Com_Zimbra_sms.smsNS = "http://upsidewireless.com/webservice/sms";
Com_Zimbra_sms.smsURI = "http://api.upsidewireless.com/soap/sms.asmx";

Com_Zimbra_sms.prototype = new ZmZimletBase();
Com_Zimbra_sms.prototype.constructor = Com_Zimbra_sms;


// Panel Zimlet Methods
// Called by the Zimbra framework upon an accepted drag'n'drop
Com_Zimbra_sms.prototype.doDrop =
function(obj) {
	switch (obj.TYPE) {
		case "ZmContact":
			this._contactDropped(obj);
			break;
		case "ZmMailMsg":
			this._msgDropped(obj.from, obj);
			break;
		case "ZmAppt":
			this._apptDropped(obj);
			break;
		case "ZmConv":
			var from = obj.participants[obj.participants.length - 1];
			this._msgDropped(from, obj);
			break;

		default:
			this.displayErrorMessage("You somehow managed to drop a \"" + obj.TYPE + "\" but however the SMS Zimlet does't support it for drag'n'drop.");
	}
};

Com_Zimbra_sms.prototype.init = function() {

	this.turnOnZimlet_SMS = this.getUserProperty("turnOnZimlet_SMS") == "true";
	if (!this.turnOnZimlet_SMS)
		return;

	this.sms_smsUsername = this.getUserProperty("sms_smsUsername");
	this.sms_smsPassword = this.getUserProperty("sms_smsPassword");
	this.sms_showSendAndSMSButton = this.getUserProperty("sms_showSendAndSMSButton") == "true";
	this.sms_alsoSendEmail = this.getUserProperty("sms_alsoSendEmail") == "true";
};

// Called by the Zimlet framework when the SMS panel item was double clicked
Com_Zimbra_sms.prototype.doubleClicked = function() {
	this.singleClicked();
};

// Called by the Zimlet framework when the SMS panel item was single clicked
Com_Zimbra_sms.prototype.singleClicked = function(toValue, bodyValue) {
	if (!this.turnOnZimlet_SMS) {
		this._showWarningMsg("Please Turn-ON SMS Zimlet. Right-click on Zimlet > Select Preferences");
		return;
	}
	var accountNotSet = this._initializeVariables(false);
	if (accountNotSet)
		return;
	var view = new DwtComposite(this.getShell());
	var el = view.getHtmlElement();
	var div = document.createElement("div");
	var toId = Dwt.getNextId();
	var bodyId = Dwt.getNextId();

	if (bodyValue) {
		// replace any appostrophes ...to avoid catastrophe ..
		bodyValue = bodyValue.replace(/\x27/, "");
		bodyValue = AjxStringUtil.htmlEncode(bodyValue);
		DBG.println(AjxDebug.DBG2, "body: " + bodyValue);
	}


	div.innerHTML =
	[ "<table><tbody>",
		"<tr>",
		"<td align='right'><label for='", toId, "'>Cell:</td>",
		"<td>",
		"<input autocomplete='off' style='width:21em' type='text' id='", toId, "' value='", toValue, "'/>",
		"</td>",
		"</tr>",
		"<tr>",
		"<td colspan='2'>",
		"<textarea style='width:25em;height:50px' id='", bodyId, "'>", bodyValue,  "</textarea>",
		"</td>",
		"</tr></tbody></table>" ].join("");
	el.appendChild(div);

	var dialog_args = {
		title : "Send SMS Zimlet",
		view  : view
	};
	var dlg = this._createDialog(dialog_args);
	dlg.popup();

	if (!bodyValue) {
		el = document.getElementById(bodyId);
		el.select();
		el.focus();
	} else {
		el = document.getElementById(toId);
		el.select();
		el.focus();
	}

	dlg.setButtonListener(DwtDialog.OK_BUTTON,
		new AjxListener(this, function() {
			this._sendSMS(document.getElementById(toId).value, document.getElementById(bodyId).value);
			dlg.popdown();
			dlg.dispose();
		}));

	dlg.setButtonListener(DwtDialog.CANCEL_BUTTON,
		new AjxListener(this, function() {
			dlg.popdown();
			dlg.dispose();
		}));
};

Com_Zimbra_sms.prototype.menuItemSelected = function(itemId) {
	switch (itemId) {
		case "PREFERENCES":
			this.createPropertyEditor();
			break;
	}
};

// Private Methods
Com_Zimbra_sms.prototype._apptDropped = function(appt) {

	var body = "time: " + appt.startDate;
	if (appt.location) {
		body = body + " location: " + appt.location;
	}
	if (appt.notes) {
		body = body + " - " + appt.notes;
	}
	this.singleClicked(null, body);
};

Com_Zimbra_sms.prototype._msgDropped = function(from, note) {

	var body = "from: " + from + " subject: " + note.subject;
	if (note.fragment) {
		body = body + "-" + note.fragment;
	}
	this.singleClicked(null, body);
};

Com_Zimbra_sms.prototype._contactDropped =
function(contact) {

	var cf = contact.firstName ? contact.firstName : " ";
	var cl = contact.lastName ? contact.lastName : " ";
	var ce = contact.email ? contact.email : " ";
	var chp = contact.homePhone ? " h:" + contact.homePhone : " ";
	var cwp = contact.workPhone ? " w:" + contact.workPhone : " ";
	var cmp = contact.mobilePhone ? " c:" + contact.mobilePhone : " ";
	var body = cf + " " + cl + " " + ce + chp + cwp + cmp;
	if (contact.homeStreet) {
		var chst = contact.homeState ? contact.homeState : " ";
		var chz = contact.homePostalCode ? contact.homePostalCode : " ";
		body = body + " addr:" + contact.homeStreet + " " + chst + " " + chz;
	}
	this.singleClicked(contact.mobilePhone, body);
};
/*	deprecated we now use upsidewireless.com's soap api
 Com_Zimbra_sms.prototype._sendSMS =
 function(to, body) {
 var url = this.getResource('sms.jsp');

 to = AjxStringUtil.urlEncode(to);
 body = AjxStringUtil.urlEncode(body);
 var reqParam = 'to=' + to + '&body=' + body;
 var reqHeader = {"Content-Type":"application/x-www-form-urlencoded"};

 AjxRpc.invoke(reqParam, url, reqHeader, new AjxCallback(this, this._resultCallback));
 };

 Com_Zimbra_sms.prototype._resultCallback=
 function(result) {
 var r = result.text;
 DBG.println(AjxDebug.DBG2, "result:" + r);
 this.displayStatusMessage(r);
 };
 */

//============================================================================
// Updated SMS engine to use upsidewireless.com's soap api instead of jsp+smtp
//@author: Raja Rao DV
//=============================================================================

Com_Zimbra_sms.prototype._sendMultipleSMS =
function(recepients, body) {
	var accountNotSet = this._initializeVariables(true);
	if (accountNotSet)
		return;
	var counter = 0;
	this._totalRecepients = recepients.length;
	for (var i = 0; i < this._totalRecepients; i++) {
		var emailAndPhone = recepients[i];
		var email = emailAndPhone.email;
		var phone = emailAndPhone.to;
		if (phone == undefined || phone == null) {
			continue;
		}
		var smsParams = {to:phone, body:body, email:email};
		var sendSmsCallback = new AjxCallback(this, this._sendSMSThruSOAP, smsParams);
		setTimeout(AjxCallback.simpleClosure(this._auth, this, sendSmsCallback), counter * 1000);
		counter++;
	}
	if (!this.sms_alsoSendEmail) {
		this._closeComposeView();
	}
};

Com_Zimbra_sms.prototype._sendSMS =
function(to, body) {
	var accountNotSet = this._initializeVariables(false);
	if (accountNotSet)
		return;
	var smsParams = {to:to, body:body};
	var sendSmsCallback = new AjxCallback(this, this._sendSMSThruSOAP, smsParams);
	this._auth(sendSmsCallback);
};

Com_Zimbra_sms.prototype._initializeVariables =
function(isMultiple) {
	this._totalResponseCount = 0;
	if (!isMultiple) {
		this._totalRecepients = 1;
		this._sendToMultiple = false;
	} else {
		this._sendToMultiple = true;
	}
	this._errList = new Array();
	this._smsUsername = this.getUserProperty("sms_smsUsername");
	this._smsPassword = this.getUserProperty("sms_smsPassword");
	if (this._smsUsername == "" || this._smsPassword == "") {
		var transitions = [ ZmToast.FADE_IN, ZmToast.PAUSE, ZmToast.PAUSE, ZmToast.PAUSE,  ZmToast.PAUSE,  ZmToast.PAUSE, ZmToast.FADE_OUT ];
		appCtxt.getAppController().setStatusMsg("1. Please Enter Username and Password. 2. Please Select Turn-ON Zimlet Checkbox (if its not ON)", ZmStatusView.LEVEL_WARNING, null, transitions);
		this.createPropertyEditor();
		return true;//return account-not-set == true
	}
	return false;//return account-not-set == true
};

Com_Zimbra_sms.prototype.createPropertyEditor =
function() {
	//if zimlet dialog already exists...
	if (this.pbDialog) {
		this.pbDialog.popup();
		return;
	}
	this.pView = new DwtComposite(this.getShell());
	this.pView.getHtmlElement().innerHTML = this.createPrefView();

	if (this.getUserProperty("turnOnZimlet_SMS") == "true") {
		document.getElementById("turnOnZimlet_SMS").checked = true;
	}
	if (this.getUserProperty("sms_showSendAndSMSButton") == "true") {
		document.getElementById("sms_showSendAndSMSButton").checked = true;
	}
	if (this.getUserProperty("sms_alsoSendEmail") == "true") {
		document.getElementById("sms_alsoSendEmail").checked = true;
	}

	document.getElementById("sms_smsUsername").value = this.getUserProperty("sms_smsUsername");
	document.getElementById("sms_smsPassword").value = this.getUserProperty("sms_smsPassword");
	var readMeButtonId = Dwt.getNextId();
	var readMeButton = new DwtDialog_ButtonDescriptor(readMeButtonId, ("Read Me"), DwtDialog.ALIGN_LEFT);
	this.pbDialog = this._createDialog({title:"SMS Zimlet Preferences", view:this.pView, standardButtons:[DwtDialog.OK_BUTTON, DwtDialog.CANCEL_BUTTON],  extraButtons:[readMeButton]});
	this.pbDialog.setButtonListener(DwtDialog.OK_BUTTON, new AjxListener(this, this._okBtnListner));
	this.pbDialog.setButtonListener(readMeButtonId, new AjxListener(this, this._showSMSreadMe));
	this.pbDialog.popup();
};

Com_Zimbra_sms.prototype._showSMSreadMe =
function() {
	window.open(this.getResource("showSMSreadMe.html"));
};

Com_Zimbra_sms.prototype._okBtnListner =
function() {
	this._reloadRequired = false;
	if (document.getElementById("turnOnZimlet_SMS").checked) {
		if (!this.turnOnZimlet_SMS) {
			this._reloadRequired = true;
		}
		this.setUserProperty("turnOnZimlet_SMS", "true");

	} else {
		this.setUserProperty("turnOnZimlet_SMS", "false");
		if (this.turnOnZimlet_SMS)
			this._reloadRequired = true;
	}

	if (document.getElementById("sms_showSendAndSMSButton").checked) {
		if (!this.sms_showSendAndSMSButton) {
			this._reloadRequired = true;
		}
		this.setUserProperty("sms_showSendAndSMSButton", "true");
	} else {
		if (this.sms_showSendAndSMSButton) {
			this._reloadRequired = true;
		}
		this.setUserProperty("sms_showSendAndSMSButton", "false");
	}
	if (document.getElementById("sms_alsoSendEmail").checked) {
		if (!this.sms_alsoSendEmail) {
			this._reloadRequired = true;
		}
		this.setUserProperty("sms_alsoSendEmail", "true");
	} else {
		if (this.sms_alsoSendEmail) {
			this._reloadRequired = true;
		}
		this.setUserProperty("sms_alsoSendEmail", "false");
	}


	if (this.sms_smsUsername != document.getElementById("sms_smsUsername").value
		|| this.sms_smsPassword != document.getElementById("sms_smsPassword").value) {

		this.setUserProperty("sms_smsUsername", document.getElementById("sms_smsUsername").value);
		this.setUserProperty("sms_smsPassword", document.getElementById("sms_smsPassword").value);
		this._reloadRequired = true;
	}

	this.pbDialog.popdown();
	if (this._reloadRequired) {
		var transitions = [ ZmToast.FADE_IN, ZmToast.PAUSE, ZmToast.PAUSE, ZmToast.PAUSE, ZmToast.PAUSE,  ZmToast.FADE_OUT ];
		appCtxt.getAppController().setStatusMsg("Please wait, saving preferences. Browser will be refreshed for changes to take effect..", ZmStatusView.LEVEL_INFO, null, transitions);
		this.saveUserProperties(new AjxCallback(this, this._reloadBrowser));
	}
};

Com_Zimbra_sms.prototype._reloadBrowser =
function() {
	window.onbeforeunload = null;
	var url = AjxUtil.formatUrl({});
	ZmZimbraMail.sendRedirect(url);
};


Com_Zimbra_sms.prototype.createPrefView =
function() {
	var html = new Array();
	var i = 0;
	html[i++] = "<DIV class='sms_cardDetailDiv'>";
	html[i++] = "<B>SMS ACCOUNT PREFERENCES</B><br> Please enter www.upsidewireless.com's username and password:";
	html[i++] = "</DIV>";
	html[i++] = "<BR>";
	html[i++] = "<DIV>";
	html[i++] = "Username:<input id='sms_smsUsername'  type='text'/>";
	html[i++] = "</DIV>";
	html[i++] = "<DIV>";
	html[i++] = "Password: <input id='sms_smsPassword'  type='password'/>";
	html[i++] = "</DIV>";
	html[i++] = "<BR>";
	html[i++] = "<DIV class='sms_cardDetailDiv'>";
	html[i++] = "<B>MAIL COMPOSE VIEW PREFERENCES:</B><br>Send SMS to one or more users via mail compose";
	html[i++] = "</DIV>";
	html[i++] = "<DIV>";
	html[i++] = "<input id='sms_showSendAndSMSButton'  type='checkbox'/>Show 'Send SMS' Button in mail compose";
	html[i++] = "</DIV>";
	html[i++] = "<DIV>";
	html[i++] = "<input id='sms_alsoSendEmail'  type='checkbox'/>Also Send Email to all recepients(along with SMS)";
	html[i++] = "</DIV>";
	html[i++] = "<DIV>";
	html[i++] = "<BR>";
	html[i++] = "PS:Please Check out the Read Me";
	html[i++] = "</DIV>";
	html[i++] = "<BR><BR>";
	html[i++] = "<DIV>";
	html[i++] = "<input id='turnOnZimlet_SMS'  type='checkbox'/>Turn ON SMS Zimlet";
	html[i++] = "</DIV>";
	return html.join("");

};

Com_Zimbra_sms.prototype._auth =
function(callbackAfterSuccessfulAuth) {
	if (this._token && this._signature) {//if we already have authtoken and signature, reuse
		callbackAfterSuccessfulAuth.run(this);
		return;
	}
	var soapDoc = this._makeEnvelope("GetParameters", Com_Zimbra_sms.authNS);
	soapDoc.set("username", this._smsUsername, null, Com_Zimbra_sms.authNS);
	soapDoc.set("password", this._smsPassword, null, Com_Zimbra_sms.authNS);

	var hdrs = new Array();
	hdrs["Content-type"] = "text/xml";
	hdrs["Content-length"] = soapDoc.getXml().length;
	hdrs["Connection"] = "close";

	this.sendRequest(soapDoc, Com_Zimbra_sms.autoURI, hdrs, new AjxCallback(this, this._handleAuthResponse, callbackAfterSuccessfulAuth), false, true);
};


Com_Zimbra_sms.prototype._handleAuthResponse =
function(callback, response) {
	if (!response.success) {
		this._totalResponseCount++;
		this._handleExpn(callback.args, response);
		return;
	}

	var x = response.xml;
	var tokenObj = x.getElementsByTagName("Token")[0];
	if (tokenObj.textContent) {
		this._token = tokenObj.textContent;
		this._signature = x.getElementsByTagName("Signature")[0].textContent;
	} else if (tokenObj.text) {
		this._token = tokenObj.text;
		this._signature = x.getElementsByTagName("Signature")[0].text;
	}
	if (callback)
		callback.run(this);

};

Com_Zimbra_sms.prototype._sendSMSThruSOAP =
function(smsParams) {
	var soapDoc = this._makeEnvelope("Send_Plain", Com_Zimbra_sms.smsNS);
	soapDoc.set("token", this._token, null, Com_Zimbra_sms.smsNS);
	soapDoc.set("signature", this._signature, null, Com_Zimbra_sms.smsNS);
	soapDoc.set("recipient", smsParams.to, null, Com_Zimbra_sms.smsNS);
	soapDoc.set("message", smsParams.body, null, Com_Zimbra_sms.smsNS);
	soapDoc.set("encoding", "Eight", null, Com_Zimbra_sms.smsNS);

	var hdrs = new Array();
	hdrs["Content-type"] = "text/xml";
	hdrs["Content-length"] = soapDoc.getXml().length;
	hdrs["Connection"] = "close";
	this.sendRequest(soapDoc, Com_Zimbra_sms.smsURI, hdrs, new AjxCallback(this, this._handleSMS, smsParams), false, true);
};

Com_Zimbra_sms.prototype._handleSMS =
function(smsParams, response) {
	//console.log(response.text);
	this._totalResponseCount++;
	if (!response.success || response.text.indexOf("<BlockedReason>") > 0) {
		this._handleExpn(smsParams, response);
	}
	if (this._errList.length == 0 && this._totalResponseCount == this._totalRecepients) {
		appCtxt.getAppController().setStatusMsg("SMS was sent successfully", ZmStatusView.LEVEL_INFO);
	}
};


Com_Zimbra_sms.prototype._makeEnvelope = function(method, ns) {
	var soap = AjxSoapDoc.create(
		method, ns, null,
		"http://schemas.xmlsoap.org/soap/envelope/");
	var envEl = soap.getDoc().firstChild;
	envEl.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
	envEl.setAttribute("xmlns:xsd", "http://www.w3.org/2001/XMLSchema");
	return soap;
};

Com_Zimbra_sms.prototype._handleExpn =
function(smsParams, response) {
	var title = "SMS Zimlet: Error Report Dialog";
	var expn = "";
	var rtxt = response.text;
	if (rtxt.indexOf("<BlockedReason>") > 0) {
		expn = rtxt.substring(rtxt.indexOf("<BlockedReason>") + "<BlockedReason>".length, rtxt.indexOf("</BlockedReason>"));
	}

	if (expn == "") {//things are not blocked due to invalid cell number but is because of some other expn
		var tmp = response.xml.getElementsByTagName("detail")[0];
		expn = AjxEnv.isIE ? tmp.text : tmp.textContent;
		if (expn == "InvalidPasswordException") {
			expn = "Username or Password is incorrect. Please check your account information";
		} else if (expn == "UnauthorizedAccessException") {
			expn = "Please purchase SMS credits at <a href='http://www.upsidewireless.com' target='_blank'>www.upsidewireless.com</a> and/or ask them to upgrade to Enterprise account";
		} else {
			expn = "There was an unknown error. Please see details";
		}
	}
	var eml = "";
	if (smsParams.email) {
		eml = "(" + smsParams.email + ")";
	}
	expn = smsParams.to + eml + "<br>" + expn;
	rtxt = rtxt.replace(/</g, "&lt;").replace(/>/g, "&gt;<br>");
	this._errList[this._errList.length] = {expnMsg:expn, soapResponseTxt:rtxt};

	if (this._totalResponseCount == this._totalRecepients)
		this._showExpnDlg(title);
};

Com_Zimbra_sms.prototype._showExpnDlg =
function(title) {
	if (this._expnDialog) {
		this._expnDialog.setContent(this._expnDialogViewHtml());
		this._addListListeners();
		this._expnDialog.popup();
		return;
	}
	this._expnDlgView = new DwtComposite(this.getShell());
	this._expnDlgView.setSize("500", "200");
	this._expnDlgView.getHtmlElement().style.overflow = "auto";
	this._expnDlgView.getHtmlElement().innerHTML = this._expnDialogViewHtml();
	this._expnDialog = this._createDialog({title: title, view:this._expnDlgView, standardButtons:[DwtDialog.OK_BUTTON]});
	this._addListListeners();
	this._expnDialog.popup();

};

Com_Zimbra_sms.prototype._expnDialogViewHtml =
function() {
	var html = new Array();
	var i = 0;
	var id_indx = 0;
	html[i++] = "<DIV class='sms_mainDiv'>";
	for (var j = 0; j < this._errList.length; j++) {
		var err = this._errList[j];

		//header
		var k = 0;
		var cardHdr = new Array();
		cardHdr[k++] = "<DIV id='sms_cardHdrDiv" + id_indx + "' class='sms_cardHdrDiv'>";
		cardHdr[k++] = "<TABLE><TR>";
		cardHdr[k++] = "<TD width='5%' id='sms_expCollIcon" + id_indx + "'  class='sms_expCollIcon'>" + AjxImg.getImageHtml("NodeCollapsed") + "</TD>";
		cardHdr[k++] = "<TD>" + err.expnMsg + "</TD>";
		cardHdr[k++] = "</TR></TABLE>";
		cardHdr[k++] = "</DIV>";

		//card details..
		var n = 0;
		var card = new Array();
		card[n++] = "<DIV id='sms_cardDetailDiv" + id_indx + "' class='sms_cardDetailDiv sms_hidden'>";
		card[n++] = err.soapResponseTxt;
		card[n++] = "</DIV>";

		//html list item..
		html[i++] = "<DIV id='sms_card" + id_indx + "'  class='sms_card'>";
		html[i++] = cardHdr.join("");
		html[i++] = card.join("");
		html[i++] = "</DIV>";//for sms_card
		id_indx++;
	}
	html[i++] = "</DIV>";

	return html.join("");
};

Com_Zimbra_sms.prototype._addListListeners = function() {
	var divs = this._expnDialog.getHtmlElement().getElementsByTagName("div");
	for (var i = 0; i < divs.length; i++) {
		var hdr = divs[i];
		if (hdr.className == "sms_cardHdrDiv") {
			hdr.onclick = AjxCallback.simpleClosure(this._onListClick, this, hdr);
		}
	}
};

Com_Zimbra_sms.prototype._onListClick = function(hdr, ev) {
	var id = hdr.id;
	var indxId = id.replace("sms_cardHdrDiv", "");
	var expndCell = document.getElementById("sms_expCollIcon" + indxId);
	var detailsDiv = document.getElementById("sms_cardDetailDiv" + indxId);

	if (detailsDiv.className == "sms_cardDetailDiv sms_hidden") {
		detailsDiv.className = "sms_cardDetailDiv sms_shown";
		expndCell.innerHTML = AjxImg.getImageHtml("NodeExpanded");
	} else {
		detailsDiv.className = "sms_cardDetailDiv sms_hidden";
		expndCell.innerHTML = AjxImg.getImageHtml("NodeCollapsed");
	}
};


//<...end> updated SMS engine to use upsidewireless.com's soap api instead of jsp+smtp
//=============================================================================


//============================================================================
// Following section deals with hooking up with composeView for Send SMS
//@author: Raja Rao DV
//=============================================================================
Com_Zimbra_sms.prototype.onShowView = function(viewId, isNewView) {
	if (!this.turnOnZimlet_SMS || !this.sms_showSendAndSMSButton)
		return;

	if (viewId == ZmId.VIEW_COMPOSE && !this._toolbar) {
		this._initComposeSMSToolbar();
	}
};

Com_Zimbra_sms.prototype._initComposeSMSToolbar = function() {

	if (!appCtxt.get(ZmSetting.MAIL_ENABLED))
		this._toolbar = true;

	if (this._toolbar)
		return;

	// Add the 'send and add' button to the Compose Page
	this._composerCtrl = AjxDispatcher.run("GetComposeController");
	this._composerCtrl._smsZimlet = this;
	if (!this._composerCtrl._toolbar) {
		// initialize the compose controller's toolbar
		this._composerCtrl._initializeToolBar();
	}
	this._toolbar = this._composerCtrl._toolbar;
	this._composeView = this._composerCtrl._composeView;
	// Add button to toolbar
	if (!this._toolbar.getButton("SEND_AND_SMS")) {
		ZmMsg.smsAdd = "Send SMS";
		ZmMsg.smsTooltip = "Send this Email as SMS";

		var btn = this._toolbar.createOp(
			"SEND_AND_SMS",
		{
			text	: ZmMsg.smsAdd,
			tooltip : ZmMsg.smsTooltip,
			index   : 1,
			image   : "SMS-panelIcon"
		}
			);

		btn.addSelectionListener(new AjxListener(this, this._sendAndSMS));
	}

};

Com_Zimbra_sms.prototype._closeComposeView = function() {
	this._composeView.reset(true);
	this._composeView.reEnableDesignMode();
	appCtxt.getAppViewMgr().popView(true);
};

Com_Zimbra_sms.prototype._sendAndSMS = function() {
	var emailOrSMSArry = [];
	var smsNumber = "";
	var justSMS = false;
	var fields = this._composeView.getAddrFields();
	for (var i = 0; i < fields.length; i++) {
		var val = fields[i].value;
		if (val != "" && val.indexOf("@") == -1) { //just sms
			if (val.indexOf(",") > 0 || val.indexOf(";") > 0) {
				var transitions = [ ZmToast.FADE_IN, ZmToast.PAUSE, ZmToast.PAUSE, ZmToast.PAUSE, ZmToast.PAUSE, ZmToast.FADE_OUT ];
				this._showWarningMsg("Cannot send sms to multiple users when mobile phone is directly entered.<BR>");
				return;
			}
			smsNumber = val;
			justSMS = true;
			break;
		}
	}
	if (justSMS) {
		var subject = "";
		if(this._composeView._subjectField) {
			subject = this._composeView._subjectField.value;
			subject = subject + "\r\n";
		}
		this._sendSMS(smsNumber, subject + "\r\n"+ this._composeView.getHtmlEditor().getTextVersion());
		this._closeComposeView();
		return;
	}

	this._msg = this._composeView.getMsg();
	if (!this._msg) {
		return;
	}

	if (this.sms_alsoSendEmail) {//if we have to send email as well..
		this._composerCtrl._send();
	}
	this._emailAndPhone = new Array();
	this.__oldNumContacts = 0;
	this._noOpLoopCnt = 0;
	this._totalWaitCnt = 0;

	if (!this._contactsAreLoaded) {
		var transitions = [ ZmToast.FADE_IN, ZmToast.PAUSE, ZmToast.PAUSE, ZmToast.PAUSE, ZmToast.PAUSE, ZmToast.PAUSE, ZmToast.FADE_OUT ];
		appCtxt.getAppController().setStatusMsg("Please wait, scanning Address Book for Mobile numbers..", ZmStatusView.LEVEL_INFO, null, transitions);
		this._waitForContactToLoadAndProcess();
		this._contactsAreLoaded = true;
	} else {
		this._startProcessing();//start processing
	}
};

Com_Zimbra_sms.prototype._waitForContactToLoadAndProcess = function() {
	this._contactList = AjxDispatcher.run("GetContacts");
	if (!this._contactList)
		return;

	this.__currNumContacts = this._contactList.getArray().length;
	if (this._totalWaitCnt < 2 || this._noOpLoopCnt < 3) {//minimum 2 cycles post currentCnt==oldCnt
		if (this.__oldNumContacts == this.__currNumContact) {
			this._noOpLoopCnt++;
		}
		this._totalWaitCnt++;
		this.__oldNumContacts = this.__currNumContact;
		setTimeout(AjxCallback.simpleClosure(this._waitForContactToLoadAndProcess, this), 3000);
	} else {

		this._startProcessing();//start processing
	}
};

Com_Zimbra_sms.prototype._startProcessing = function() {
	var to = this._msg.getAddresses("TO", true, true);
	var cc = this._msg.getAddresses("CC", true, true);
	var bcc = this._msg.getAddresses("BCC", true, true);
	var allemails = new Array();
	var emailsWithNoNumber = new Array();
	allemails = to.getArray().concat(cc.getArray()).concat(bcc.getArray());

	for (var j = 0; j < allemails.length; j++) {
		var currentContact = allemails[j];
		var attr = currentContact.attr ? currentContact.attr : currentContact._attrs;
		var mp = attr.mobilePhone;
		if (mp == undefined || mp == "") {
			emailsWithNoNumber.push(attr.email);
			continue;
		}
		this._emailAndPhone[j] = {email:attr.email, to:mp};
	}
	if (emailsWithNoNumber.length > 0) {
		this._showWarningMsg("Cannot send SMS. Following emails don't have mobile numbers associated in AddressBook. <br>" + emailsWithNoNumber.join(", "));
		return;
	}

	var content = this._msg.subject + "\r\n" + this._composeView.getHtmlEditor().getTextVersion();
	this._sendMultipleSMS(this._emailAndPhone, content);
};

Com_Zimbra_sms.prototype._showWarningMsg = function(message) {
	var style = DwtMessageDialog.WARNING_STYLE;
	var dialog = appCtxt.getMsgDialog();
	this.warningDialog = dialog;
	dialog.setMessage(message, style);
	dialog.popup();
};
