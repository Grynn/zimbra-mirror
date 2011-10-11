ZmMitelClick2CallDlg = function(shell, parent) {
	this.toPhoneNumber = "";
	this.fromPhoneNumber = "";
	this.zimlet = parent;
	this.mitelSoapCalls = new MitelSoapCalls();

	this._dialogView = new DwtComposite(appCtxt.getShell());
	this._dialogView.setSize(300, 150);
	DwtDialog.call(this, {
				parent : shell,
				className : "ZmMitelClick2CallDlg",
				title : this.zimlet.getMessage("click2callDlgTitle"),
				view: this._dialogView,
				standardButtons : [ DwtDialog.NO_BUTTONS],
				mode: DwtBaseDialog.MODELESS
			});

	this._setWhiteBackground();
	this._buttonDesc = {}; //set this to null otherwise esc will throw expn
	this._callingStr = this.zimlet.getMessage("calling");
	this._connectionFailedStr  = this.zimlet.getMessage("connectionFailedTryRedialling");
	this._connectionSuccessfulStr = this.zimlet.getMessage("connectionSuccessfulStr");
	this._callHungUpStr = this.zimlet.getMessage("callHungUp");
	this._addMinimizeAndCloseBtns();
};

ZmMitelClick2CallDlg.prototype = new DwtDialog;
ZmMitelClick2CallDlg.prototype.constructor = ZmMitelClick2CallDlg;

ZmMitelClick2CallDlg.prototype.CONTROLS_TEMPLATE = null;

ZmMitelClick2CallDlg.PHOTO_ID = "ZmMitelClick2CallDlg_photoBG";
ZmMitelClick2CallDlg.PHOTO_PARENT_ID = "ZmMitelClick2CallDlg_photoBGDiv";
ZmMitelClick2CallDlg.TEXT_DIV_ID = "ZmMitelClick2CallDlg_TextDiv";


ZmMitelClick2CallDlg.prototype._dragStart = function(x, y){
 //override but dont do anything
};

//Set WindowInnerContainer cell's bg to white
ZmMitelClick2CallDlg.prototype._setWhiteBackground = function(){
 	var el = this._dialogView.getHtmlElement();
	while (el && el.className &&  el.className.indexOf( "WindowInnerContainer") == -1) {
				el = el.parentNode;
	}
	if (el == null) {
		return;
	}
	el.style.backgroundColor = "white";
};


ZmMitelClick2CallDlg.prototype.popup = function() {

	DwtDialog.prototype.popup.call(this);
	this._searchAndShowContactInfo();
  	this._showAsCalling();
	this._animateCallingText();
	this._callingTxtInterval = setInterval(AjxCallback.simpleClosure(this._animateCallingText, this), 700);
};

ZmMitelClick2CallDlg.prototype._addMinimizeAndCloseBtns = function() {
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

ZmMitelClick2CallDlg.prototype._handleMinMaxDlg = function() {
	if(this._minMaxeDlgBtn.className == "Imgmitel-minimize-icon") {
		this._dlgTopPosB4Minimize = (this.getHtmlElement().style.top).replace("px", "");
		this._minMaxeDlgBtn.className = "Imgmitel-maximize-icon";
		this.getHtmlElement().style.top = (document.body.offsetHeight - 25) + "px";
	} else if(this._minMaxeDlgBtn.className == "Imgmitel-maximize-icon") {
		this._minMaxeDlgBtn.className = "Imgmitel-minimize-icon";
		this.getHtmlElement().style.top = this._dlgTopPosB4Minimize + "px";
	}
};

ZmMitelClick2CallDlg.prototype._handleCloseDlg = function() {
	this.popdown();
};

ZmMitelClick2CallDlg.prototype._searchAndShowContactInfo = function(limit, query, postCallback) {
	if (!limit) {
		limit = 1;
	}
	if (!query) {
		query = this.toPhoneNumber;
	}
	var jsonObj, request, soapDoc;
	jsonObj = {
		SearchRequest : {
			_jsns : "urn:zimbraMail"
		}
	};
	request = jsonObj.SearchRequest;
	request.types = "contact";
	request.query = query;
	request.offset = 0;
	request.limit = limit;
	if (!postCallback) {
		postCallback = "";
	}
	var callback = new AjxCallback(this, this._handleContactSearch,
			postCallback);
	appCtxt.getAppController().sendRequest({
				jsonObj : jsonObj,
				asyncMode : true,
				callback : callback,
				noBusyOverlay : true
			});
};

ZmMitelClick2CallDlg.prototype._handleContactSearch = function(postCallback, response) {
	var photoUrl;
	var attrs = {};
	if (response) {
		var contact;
		var data = response.getResponse();
		var cn = data.SearchResponse.cn;
		if (postCallback && postCallback != "") {
			postCallback.run(cn);
			return;
		}
		if (cn) {
			contact = cn[0];
		}
		if (contact && contact._attrs) {
			attrs = contact._attrs;
			if (attrs.image) {
				var msgFetchUrl = appCtxt.get(ZmSetting.CSFE_MSG_FETCHER_URI);
				photoUrl = [ msgFetchUrl, "/?auth=co&id=", contact.id,
					"&part=", attrs.image.part, "&t=",
					(new Date()).getTime() ].join("");
			}
			attrs["toPhoneNumber"] = this.toPhoneNumber;

			//this._setProfileImage(photoUrl);
			this._setContactDetails(attrs, photoUrl);
			return;
		}
	}
	// else, make gal request..
	var jsonObj, request, soapDoc;
	jsonObj = {
		SearchGalRequest : {
			_jsns : "urn:zimbraAccount"
		}
	};
	request = jsonObj.SearchGalRequest;
	request.type = "account";
	request.name = this.toPhoneNumber;
	request.offset = 0;
	request.limit = 1;
	var callback = new AjxCallback(this, this._handleGalSearchResponse);
	appCtxt.getAppController().sendRequest({
				jsonObj : jsonObj,
				asyncMode : true,
				callback : callback,
				noBusyOverlay : true
			});

};

ZmMitelClick2CallDlg.prototype._handleGalSearchResponse = function(response) {
	var validResponse = false;
	var attrs = {};
	if (response) {
		var data = response.getResponse();
		var cn = data.SearchGalResponse.cn;
		if (cn && cn[0] && cn[0]._attrs) {
			attrs = data.SearchGalResponse.cn[0]._attrs;
			validResponse = true;
		}
	}
	attrs["toPhoneNumber"] = this.toPhoneNumber;
	var photoUrl;
	if (attrs["photoFileName"]) {
		var photoName = attrs["photoFileName"] ? attrs["photoFileName"]
				: "noname.jpg";
		photoUrl = ZmZimletBase.PROXY + ZmMitelClick2CallDlg.PHOTO_BASE_URL
				+ attrs["photoFileName"];
	}
	//this._setProfileImage(photoUrl);
	this._setContactDetails(attrs, photoUrl);
};

ZmMitelClick2CallDlg.prototype._getTooltipBGHtml = function(email) {
	var width = ";";
	var left = ";";
	if (AjxEnv.isIE) {
		var width = "width:100%;";
		var left = "left:3%;";
	}
	var subs = {
		photoParentId : ZmMitelClick2CallDlg.PHOTO_PARENT_ID,
		textDivId : ZmMitelClick2CallDlg.TEXT_DIV_ID,
		width : width,
		left : left
	};
	return AjxTemplate.expand("com_zimbra_mitel.templates.ZmMitel#Frame",
			subs);
};

ZmMitelClick2CallDlg.prototype._setContactDetails = function(attrs, photoUrl) {
	var params = attrs;
	if (attrs.workState || attrs.workCity || attrs.workStreet
			|| attrs.workPostalCode) {
		var workState = attrs.workState ? attrs.workState : "";
		var workCity = attrs.workCity ? attrs.workCity : "";
		var workStreet = attrs.workStreet ? attrs.workStreet : "";
		var workPostalCode = attrs.workPostalCode ? attrs.workPostalCode : "";
		var address = [ workStreet, " ", workCity, " ", workState, " ",
			workPostalCode ].join("");
		params["address"] = AjxStringUtil.trim(address);
	}
	params = this._formatTexts(params);
	params.callingStr = this._callingStr;
	params.connectionFailedStr = this._connectionFailedStr;
	params.callSuccessfulStr = this._connectionSuccessfulStr;
	params.callHungUpStr = this._callHungUpStr;
	var iHtml = AjxTemplate.expand(
			"com_zimbra_mitel.templates.ZmMitel#ContactDetails", params);

	this._dialogView.getHtmlElement().innerHTML = iHtml;

	this._setProfileImage(photoUrl);

	var btn = new DwtButton({
				parent : this.zimlet.getShell()
			});
	btn.setText("Hang up");// button name
	btn.setImage("MitelPhoneHangupBtn");
	btn.addSelectionListener(new AjxListener(this, this._handleHangUpBtnClick));
	document.getElementById("mitelClick2CallDlg_hangupBtnDiv").appendChild(
			btn.getHtmlElement());

	btn = new DwtButton({
				parent : this.zimlet.getShell()
			});
	btn.setText("Redial");// button name
	btn.setImage("Telephone");
	btn.addSelectionListener(new AjxListener(this, this._redialBtnHandler));
	document.getElementById("mitelClick2CallDlg_reDialBtnDiv").appendChild(
			btn.getHtmlElement());

	this._removeCustomAttrs(attrs);
};

ZmMitelClick2CallDlg.prototype._showAsCalling = function() {
	var params = {mitelClick2CallDlg_hangupBtnDiv: "none",
		mitel_ringingPhoneMsgDiv:"block",
		mitelClick2CallDlg_reDialBtnDiv:"none",
		mitel_click2callDlg_reDialMsgDiv:"none",
		mitel_click2callDlg_callCompletedMsgDiv: "none",
		mitel_click2callDlg_callHungUpMsgDiv:"none"};

	this._showHideCardParts(params);
};

ZmMitelClick2CallDlg.prototype.showErrorMsgAndRedial = function(errorMsg) {
	var params = {mitelClick2CallDlg_hangupBtnDiv: "none",
		mitel_ringingPhoneMsgDiv:"none",
		mitelClick2CallDlg_reDialBtnDiv:"block",
		mitel_click2callDlg_reDialMsgDiv:"block",
		mitel_click2callDlg_callCompletedMsgDiv: "none",
		mitel_click2callDlg_callHungUpMsgDiv:"none"};

	this._showHideCardParts(params);
};


ZmMitelClick2CallDlg.prototype._showHungUpMsgAndRedial = function() {
	var params = {mitelClick2CallDlg_hangupBtnDiv: "none",
		mitel_ringingPhoneMsgDiv:"none",
		mitelClick2CallDlg_reDialBtnDiv:"block",
		mitel_click2callDlg_reDialMsgDiv:"none",
		mitel_click2callDlg_callCompletedMsgDiv: "none",
		mitel_click2callDlg_callHungUpMsgDiv:"block"};
	this._showHideCardParts(params);
};


ZmMitelClick2CallDlg.prototype._showCallCompleted = function() {
	var params = {mitelClick2CallDlg_hangupBtnDiv: "block",
		mitel_ringingPhoneMsgDiv:"none",
		mitelClick2CallDlg_reDialBtnDiv:"none",
		mitel_click2callDlg_reDialMsgDiv:"none",
		mitel_click2callDlg_callCompletedMsgDiv: "block",
		mitel_click2callDlg_callHungUpMsgDiv:"none"};

	this._showHideCardParts(params);
};


ZmMitelClick2CallDlg.prototype._showHideCardParts = function(params) {
	clearInterval(this._callingTxtInterval);
	var mitelClick2CallDlg_hangupBtnDiv = document.getElementById("mitelClick2CallDlg_hangupBtnDiv");
	var mitel_ringingPhoneMsgDiv = document.getElementById("mitel_ringingPhoneMsgDiv");
	var mitelClick2CallDlg_reDialBtnDiv = document.getElementById("mitelClick2CallDlg_reDialBtnDiv");
	var mitel_click2callDlg_reDialMsgDiv = document.getElementById("mitel_click2callDlg_reDialMsgDiv");
	var mitel_click2callDlg_callCompletedMsgDiv = document.getElementById("mitel_click2callDlg_callCompletedMsgDiv");
	var mitel_click2callDlg_callHungUpMsgDiv = document.getElementById("mitel_click2callDlg_callHungUpMsgDiv");

	if (mitelClick2CallDlg_hangupBtnDiv) {
		document.getElementById("mitelClick2CallDlg_hangupBtnDiv").style.display = params.mitelClick2CallDlg_hangupBtnDiv;
	}
	if (mitel_ringingPhoneMsgDiv) {
		document.getElementById("mitel_ringingPhoneMsgDiv").style.display = params.mitel_ringingPhoneMsgDiv;
	}
	if (mitelClick2CallDlg_reDialBtnDiv) {
		document.getElementById("mitelClick2CallDlg_reDialBtnDiv").style.display = params.mitelClick2CallDlg_reDialBtnDiv;
	}
	if (mitel_click2callDlg_reDialMsgDiv) {
		document.getElementById("mitel_click2callDlg_reDialMsgDiv").style.display = params.mitel_click2callDlg_reDialMsgDiv;
	}
	// document.getElementById("mitel_click2CallDlg_errDiv").innerHTML =
	// errorMsg;
	if (mitel_click2callDlg_callCompletedMsgDiv) {
		document.getElementById("mitel_click2callDlg_callCompletedMsgDiv").style.display = params.mitel_click2callDlg_callCompletedMsgDiv;
	}
	if (mitel_click2callDlg_callHungUpMsgDiv) {
		document.getElementById("mitel_click2callDlg_callHungUpMsgDiv").style.display = params.mitel_click2callDlg_callHungUpMsgDiv;
	}
};

ZmMitelClick2CallDlg.prototype._animateCallingText = function() {
	var d = document.getElementById("mitel_click2Call_callingLabel");
	if (d) {
		if (this.__callingLableColor == "red") {
			this.__callingLableColor = "maroon";
		} else {
			this.__callingLableColor = "red";
		}
		d.style.color = this.__callingLableColor;
	} else {
		clearInterval(this._callingTxtInterval);
	}
};

ZmMitelClick2CallDlg.prototype._redialBtnHandler = function() {
	this._callingTxtInterval = setInterval(AjxCallback.simpleClosure(this._animateCallingText, this), 700);
	this._showAsCalling();
	this.clickToCall(true);
};

ZmMitelClick2CallDlg.prototype._removeCustomAttrs = function(attrs) {
	var customAttrs = ["rightClickForMoreOptions", "formattedEmail", "address", "toPhoneNumber",
						"callingStr", "connectionFailedStr", "callSuccessfulStr", "callHungUpStr"];
	for(var i =0; i < customAttrs.length; i++) {
		var attr = customAttrs[i];
		if (attrs[attr]) {
			delete attrs[attr];
		}
	}
};

ZmMitelClick2CallDlg.prototype._formatTexts = function(attrs) {
	var email = attrs.email ? attrs.email : "";
	attrs["formattedEmail"] = email;
	if (email.length > 25) {
		var tmp = email.split("@");
		var fPart = tmp[0];
		var lPart = tmp[1];
		if (fPart.length > 25) {
			fPart = fPart.substring(0, 24) + "..";
		}
		attrs["formattedEmail"] = [ fPart, " @", lPart ].join("");
	}
	var fullName = attrs.fullName ? attrs.fullName : "";
	if (fullName == email) {
		attrs["fullName"] = "";
	}
	return attrs;
}

ZmMitelClick2CallDlg.prototype._setProfileImage = function(photoUrl) {
	var div = document.getElementById(ZmMitelClick2CallDlg.PHOTO_PARENT_ID);
	div.width = 65;
	div.height = 80;
	div.style.width = 65;
	div.style.height = 80;
	if (!photoUrl || photoUrl == "") {
		this._handleImgLoadFailure();
		return;
	}

	var img = new Image();
	img.src = photoUrl;
	img.onload = AjxCallback.simpleClosure(this._handleImageLoad, this, img);
};


ZmMitelClick2CallDlg.prototype._handleImgLoadFailure = function() {// onfailure
	var img = new Image();
	img.onload = AjxCallback.simpleClosure(this._handleImageLoad, this, img);
	img.id = ZmMitelClick2CallDlg.PHOTO_ID;
	img.src = this.zimlet.getResource("img/unknownPerson.jpg");
};

ZmMitelClick2CallDlg.prototype._handleImageLoad = function(img) {
	var div = document.getElementById(ZmMitelClick2CallDlg.PHOTO_PARENT_ID);
	if(!div) {
		return;
	}
	div.innerHTML = "";
	div.appendChild(img);
	img.width = 65;
	img.height = 80;
};

ZmMitelClick2CallDlg.prototype.clickToCall = function(isClick2CallDlgShown) {
	if (!isClick2CallDlgShown) {
		this.popup();
	}
	var url = [ "https://", this.zimlet.mitel_server, "/ucs/ws/services/csta" ]
			.join("");
	var xml = this.mitelSoapCalls.getMakeCallXML(this.fromPhoneNumber,
			this.toPhoneNumber);
	var hdrs = new Array();
	hdrs["Authorization"] = this.make_basic_auth(this.zimlet.mitel_email,
			this.zimlet.mitel_password);
	hdrs["content-type"] = "text/xml";
	hdrs["content-length"] = xml.length;
	var feedUrl = ZmZimletBase.PROXY + AjxStringUtil.urlComponentEncode(url);
	AjxRpc.invoke(xml, feedUrl, hdrs, new AjxCallback(this,
			this._mitelMakeCallHandler), false);

};

ZmMitelClick2CallDlg.prototype._mitelMakeCallHandler = function(response) {
	var jsonObj;
	if (!response.success) {
		jsonObj = this.xmlToObject(response);
		if (jsonObj && jsonObj.Body && jsonObj.Body.Fault
				&& jsonObj.Body.Fault.faultstring) {
			this.showErrorMsgAndRedial(jsonObj.Body.Fault.faultstring.toString());
		} else { //show generic error & redial btn
			this.showErrorMsgAndRedial();
		}
	} else {
		jsonObj = this.xmlToObject(response);
		if (jsonObj.Body && jsonObj.Body.MakeCallResponse) {
			this._showCallCompleted();
		}
	}
};

ZmMitelClick2CallDlg.prototype._handleHangUpBtnClick = function() {
	var tmp = this.fromPhoneNumber.split(":");
	var deviceId = tmp[1] + ":" + tmp[2];
	var url = [ "https://", this.zimlet.mitel_server, "/ucs/ws/service/ucs" ].join("");
	var xml = this.mitelSoapCalls.getClearConnectionXML(deviceId);
	var hdrs = new Array();
	hdrs["Authorization"] = this.make_basic_auth(this.zimlet.mitel_email,
			this.zimlet.mitel_password);
	hdrs["content-type"] = "text/xml";
	hdrs["content-length"] = xml.length;
	var feedUrl = ZmZimletBase.PROXY + AjxStringUtil.urlComponentEncode(url);
	AjxRpc.invoke(xml, feedUrl, hdrs, new AjxCallback(this,
			this._mitelHangupHandler), false);
};

ZmMitelClick2CallDlg.prototype._mitelHangupHandler = function(response) {
	this._showHungUpMsgAndRedial("");
	/**
	 * API doesnt work properly
	var jsonObj;
	if (!response.success) {
		jsonObj = this.xmlToObject(response);
		return;
	}  */
};

ZmMitelClick2CallDlg.prototype.xmlToObject = function(result, dontConvertToJSObj) {
	if(!result.success) {
		this.zimlet.displayErrorMessage(this.zimlet.getMessage("coulNotConnectToMitel"), result.text, this.zimlet.getMessage("mitelError"));
		return;
	}
	if (dontConvertToJSObj) {
		var xd = new AjxXmlDoc.createFromDom(result.xml);
	} else {
		var xd = new AjxXmlDoc.createFromDom(result.xml)
				.toJSObject(true, false);
	}
	return xd;
};

ZmMitelClick2CallDlg.prototype.make_basic_auth = function(user, password) {
	var tok = user + ':' + password;
	var hash = Base64.encode(tok);
	return "Basic " + hash;
};