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
* A tooltip based implementation for a generic contact.
* This class forms the basis for creating an extensible contact card
* into which a customer can add LinkedIn, TwitterSearch, Click2Call etc.
*
* The basic design is to have the tooltip as the canvas for the contact card.
* The extensions to the basic card (e.g. Linked-in) are added as "slides" into the card.
* Clicking on these extensions could open the corresponding application either in the tooltip itself or outside.
*
* Since tooltips are hidden on mouse movement, we need to make the tooltip to stay around
* by setting the sticky flag on the tooltip.
*
* A word of caution: the tooltip is a singleton, so making it unsticky asap is important for application
* responsiveness.
*/
function UnknownPersonSlide() {
}

//Static variables
UnknownPersonSlide.PHOTO_ID = "unkownPerson_photoBG";
UnknownPersonSlide.PHOTO_PARENT_ID = "unkownPerson_photoBGDiv";
UnknownPersonSlide.TEXT_DIV_ID = "unkownPerson_TextDiv";
UnknownPersonSlide.DOMAIN = "";

/**
* Implement onEmailHoverOver to get notified by Email tooltip zimlet.
* This function registers UnkownPerson Zimlet as a subscriber Zimlet
*/
UnknownPersonSlide.prototype.onEmailHoverOver =
function(emailZimlet) {
	emailZimlet.addSubscriberZimlet(this, false);
	this.emailZimlet = emailZimlet;
	this._alwaysSetTooltipToSmall();
    //appCtxt.getAppController().activateApp(ZmApp.CONTACTS);
};

UnknownPersonSlide.prototype._alwaysSetTooltipToSmall =
function() {
	var tooltipSize = EmailToolTipPrefDialog.DIMENSIONS[EmailToolTipPrefDialog.SIZE_VERYSMALL];
	var size = tooltipSize.replace(/px/ig, "");
	var arry = size.split(" x ");
	this._tooltipWidth = arry[0];
	this._tooltipHeight = arry[1];
};

/**
* This is called by Email Zimlet when user hovers over an email
*/
UnknownPersonSlide.prototype.showTooltip =
function() {
    this._setCalendarFrame();

    this._setMailFrame();

    this._setContactFrame();

    //this.addEmailSlide();
    //this.addCalendarSlide();
	this.emailZimlet.tooltip.popup(this.emailZimlet.x, this.emailZimlet.y, true, null);
	this._slide.select();
};

UnknownPersonSlide.prototype._setContactFrame =
function() {
	this.emailZimlet.hideBusyImg();
	var tthtml = this._getTooltipBGHtml();
	var selectCallback = new AjxCallback(this, this._handleContactSlideSelect);
	this._slide = new EmailToolTipSlide(tthtml, true, "UnknownPerson_contact", selectCallback, this.emailZimlet.getMessage("slideTooltip"));
	this.emailZimlet.slideShow.addSlide(this._slide);
	this._mainDiv = document.getElementById(UnknownPersonSlide.TEXT_DIV_ID);
	this._slide.setCanvasElement(this._mainDiv);
};

UnknownPersonSlide.prototype._setMailFrame =
    function() {
        //this.emailZimlet.hideBusyImg();
        //var tthtml = this._getTooltipBGHtml();
        var selectCallback = new AjxCallback(this, this._handleMailSlideSelect);
        var slide = new EmailToolTipSlide(null, true, "Mail_icon", selectCallback, this.emailZimlet.getMessage("slideMailTooltip"));
        this.emailZimlet.slideShow.addSlide(slide);
        //this._mainDiv = document.getElementById(UnknownPersonSlide.TEXT_DIV_ID);
        //this._slide.setCanvasElement(this._mainDiv);
    };

UnknownPersonSlide.prototype._setCalendarFrame =
    function() {
        //this.emailZimlet.hideBusyImg();
        //var tthtml = this._getTooltipBGHtml();
        var selectCallback = new AjxCallback(this, this._handleCalendarSlideSelect);
        var slide = new EmailToolTipSlide(null, true, "Calendar_icon", selectCallback, this.emailZimlet.getMessage("slideCalendarTooltip"));
        this.emailZimlet.slideShow.addSlide(slide);
        //this._mainDiv = document.getElementById(UnknownPersonSlide.TEXT_DIV_ID);
        //this._slide.setCanvasElement(this._mainDiv);
    };

UnknownPersonSlide.prototype._handleImgLoadFailure =
function() {//onfailure to load img w/in 5 secs, load an dataNotFound image
	var img = new Image();
	img.onload = AjxCallback.simpleClosure(this._handleImageLoad, this, img);
	img.id = UnknownPersonSlide.PHOTO_ID;
	img.src = this.emailZimlet.getResource("img/UnknownPerson_dataNotFound.jpg");
};

UnknownPersonSlide.prototype._handleImageLoad =
function(img) {
	this.emailZimlet.hideBusyImg();
	var div = document.getElementById(UnknownPersonSlide.PHOTO_PARENT_ID);
	if(!div) {
		return;
	}
	div.innerHTML = "";
	div.appendChild(img);
	if (this.emailZimlet.emailAddress.indexOf(UnknownPersonSlide.DOMAIN) != -1) {
		img.onclick =  AjxCallback.simpleClosure(this._handleProfileImageClick, this); 
		img.style.cursor = "pointer";
	}
	img.width = 65;
	img.height = 80;
};

UnknownPersonSlide.prototype._handleAllClicks =
function(ev) {
	var isRightClick;
	this.emailZimlet.popdown();
	if (AjxEnv.isIE) {
		ev = window.event;
	}
	if (ev.which){
		isRightClick = (ev.which == 3);
	} else if (ev.button) {
		isRightClick = (ev.button == 2);
	}
	if(isRightClick) {
		DwtUiEvent.setBehaviour(ev, true, false);
		this.emailZimlet.contextMenu.popup(100, this.emailZimlet.x, this.emailZimlet.y);
		return;
	}

	//if its not right click.. 
	var dwtev = DwtShell.mouseEvent;
	dwtev.setFromDhtmlEvent(ev);
	var el = dwtev.target;
	if(el.id == "UnknownPersonSlide_EmailAnchorId") {
		this.emailZimlet._composeListener(null, this.emailZimlet.emailAddress);
	} else if(el.id == "UnknownPersonSlide_NameAnchorId") {
		this.emailZimlet._contactListener(true);
	}
};

UnknownPersonSlide.prototype._handleRightClick =
function(ev) {
	var rightclick;
	if (!ev) {
		var ev = window.event;
	}
	if (ev.which){
		rightclick = (ev.which == 3);
	} else if (ev.button) {
		rightclick = (ev.button == 2);
	}
	if(!rightclick) {
		return;
	}
	this.emailZimlet.popdown();
	DwtUiEvent.setBehaviour(ev, true, false);
	this.emailZimlet.contextMenu.popup(100, this.emailZimlet.x, this.emailZimlet.y);
};

UnknownPersonSlide.prototype._handleContactSlideSelect =
function() {
	if (this._slide.loaded) {
		return;
	}
	this._slide.loaded = true;
	this._getContactDetailsAndShowTooltip();
};

UnknownPersonSlide.prototype._handleMailSlideSelect =
    function() {
        this.emailZimlet.popdown();
        this.emailZimlet._composeListener(null, this.emailZimlet.emailAddress);
    };

UnknownPersonSlide.prototype._handleCalendarSlideSelect =
    function() {
        this.emailZimlet.popdown();
        //var calController = AjxDispatcher.run("GetApptComposeController");
        //var contact = this.emailZimlet._getActionedContact();
        //var appt = calController._createComposeView().show();
        var appt = new ZmAppt();
        var c =  this.emailZimlet._getActionedContact() || new AjxEmailAddress(this.emailZimlet.emailAddress);
        appt.setAttendees(c, ZmCalBaseItem.PERSON);
        AjxDispatcher.run("GetCalController").newAppointment(appt, null, null, null);
    };

UnknownPersonSlide.prototype._getContactDetailsAndShowTooltip =
function() {
	this._slide.setInfoMessage(this.emailZimlet.getMessage("loading"));

    var contactList = AjxDispatcher.run("GetContacts");
    var contact = contactList ? contactList.getContactByEmail(this.emailZimlet.emailAddress) : null;
    if (contact) {
        //this._handleContactDetails(null, contact);
        var jsonObj, request, soapDoc;
        jsonObj = {SearchRequest:{_jsns:"urn:zimbraMail"}};
        request = jsonObj.SearchRequest;
        request.query = "in:contacts";
        request.types = "contact";
        request.name = this.emailZimlet.emailAddress;
        request.offset = 0;
        request.limit = 3;
        var callback = new AjxCallback(this, this._handleContactDetails);
        appCtxt.getAppController().sendRequest({jsonObj:jsonObj,asyncMode:true,callback:callback, noBusyOverlay:true});
    }

	if (!contact) {//make gal search request
		var jsonObj, request, soapDoc;
		jsonObj = {SearchGalRequest:{_jsns:"urn:zimbraAccount"}};
		request = jsonObj.SearchGalRequest;
		request.type = "account";
		request.name = this.emailZimlet.emailAddress;
		request.offset = 0;
		request.limit = 3;
		var callback = new AjxCallback(this, this._handleContactDetails);
		appCtxt.getAppController().sendRequest({jsonObj:jsonObj,asyncMode:true,callback:callback, noBusyOverlay:true});
	}
};

UnknownPersonSlide.prototype._handleContactDetails =
function(response, contact) {
    var attrsFromAB = contact && contact.attr;
	var validResponse = false;
	var attrs = {};
	if(response) {
		var data = response.getResponse();
        var r = data.SearchResponse || data.SearchGalResponse;
		var cn = r.cn;
		if (cn && cn[0] && cn[0]._attrs) {
			attrs = r.cn[0]._attrs;
			validResponse = true;
		}
	} else if(attrsFromAB) {
		attrs = attrsFromAB;
		validResponse = true;
	}
	
	if(!validResponse) {
		if(this.emailZimlet.fullName) {
			attrs["fullName"] = this.emailZimlet.fullName;
		}
		if(this.emailZimlet.emailAddress) {
			attrs["email"] = this.emailZimlet.emailAddress;
		}
	}
	//var photoName = attrs.image && attrs.image.filename || "noname.jpg";
    var image = attrs[ZmContact.F_image];
    var msgFetchUrl = appCtxt.get(ZmSetting.CSFE_MSG_FETCHER_URI);
    var imgUrl = null;
    if(image && image.part) {
        imgUrl =  [msgFetchUrl, "&id=", cn[0].id, "&part=", image.part, "&t=", (new Date()).getTime()].join("");
    }
	this._setProfileImage(imgUrl);
	this._setContactDetails(attrs);
	this._popupToolTip();
};



UnknownPersonSlide.prototype._popupToolTip =
function() {
	this.emailZimlet.tooltip.popup(this.emailZimlet.x, this.emailZimlet.y, true, null);
};

UnknownPersonSlide.prototype._getTooltipBGHtml =
function(email) {
	var width = ";";
	var left = ";";
	if (AjxEnv.isIE) {
		var width = "width:100%;";
		var left = "left:3%;";
	}
	var subs = {
		photoParentId: UnknownPersonSlide.PHOTO_PARENT_ID,
		textDivId: UnknownPersonSlide.TEXT_DIV_ID,
		width: width,
		left: left
	};
	return AjxTemplate.expand("com_zimbra_email.templates.Email1#Frame", subs);
};

UnknownPersonSlide.prototype._setContactDetails =
function(attrs) {
	if (attrs.workState || attrs.workCity || attrs.workStreet || attrs.workPostalCode) {
		var workState = attrs.workState ? attrs.workState : "";
		var workCity = attrs.workCity ? attrs.workCity : "";
		var workStreet = attrs.workStreet ? attrs.workStreet : "";
		var workPostalCode = attrs.workPostalCode ? attrs.workPostalCode : "";
		var address = [workStreet, " ", workCity, " ", workState, " ", workPostalCode].join("");
		attrs["address"] = AjxStringUtil.trim(address);
	}

	if (!this.emailZimlet.noRightClick) {
		//attrs["rightClickForMoreOptions"] = this.emailZimlet.getMessage("rightClickForMoreOptions");
        attrs["rightClickForMoreOptions"] = false;
	}
	attrs = this._formatTexts(attrs);
	var iHtml = AjxTemplate.expand("com_zimbra_email.templates.Email1#ContactDetails", attrs);
	document.getElementById(UnknownPersonSlide.TEXT_DIV_ID).innerHTML = iHtml;
	document.getElementById("UnknownPersonSlide_Frame").onmouseup =  AjxCallback.simpleClosure(this._handleAllClicks, this);
	/*
	document.getElementById("UnknownPersonSlide_EmailAnchorId").onclick =  AjxCallback.simpleClosure(this._openCompose, this);
	if(document.getElementById("UnknownPersonSlide_NameAnchorId")) {
		document.getElementById("UnknownPersonSlide_NameAnchorId").onclick =  AjxCallback.simpleClosure(this._openContact, this); 
	}
	*/
	this._removeCustomAttrs(attrs);
};



UnknownPersonSlide.prototype._removeCustomAttrs =
function(attrs) {
	if(attrs["rightClickForMoreOptions"]) {
		delete attrs["rightClickForMoreOptions"];
	}
	if(attrs["formattedEmail"]) {
		delete attrs["formattedEmail"];
	}
	if(attrs["address"]) {
		delete attrs["address"];
	}
};

UnknownPersonSlide.prototype._formatTexts =
function(attrs) {
	var email = attrs.email ? attrs.email : "";
	attrs["formattedEmail"] = email;
	if(email.length > 25) {
		var tmp = email.split("@");
		var fPart = tmp[0];
		var lPart = tmp[1];
		if(fPart.length > 25){
			fPart = fPart.substring(0, 24) + "..";
		}
		attrs["formattedEmail"] = [fPart, " @", lPart].join("");
	}
	var fullName = attrs.fullName ? attrs.fullName : "";
	if(fullName  ==  email) {
		attrs["fullName"] = "";
	}
	return attrs;
}

UnknownPersonSlide.prototype._setProfileImage =
function(imgUrl) {
	var div = document.getElementById(UnknownPersonSlide.PHOTO_PARENT_ID);
	div.width = 65;
	div.height = 80;
	div.style.width = 65;
	div.style.height = 80;
	if (this.emailZimlet.emailAddress.indexOf(UnknownPersonSlide.DOMAIN) == -1) {
		this._handleImgLoadFailure();
		return;
	}

	var img = new Image();
	//img.src = ZmZimletBase.PROXY + UnknownPersonSlide.PHOTO_BASE_URL + photoName;
    img.src = imgUrl;
	img.onload = AjxCallback.simpleClosure(this._handleImageLoad, this, img);
	var timeoutCallback = new AjxCallback(this, this._handleImgLoadFailure);
	this.emailZimlet.showLoadingAtId(timeoutCallback, UnknownPersonSlide.PHOTO_PARENT_ID);
};