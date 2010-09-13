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
* A Email tooltip class that adds a blank slide when VMPersonZimlet is not loaded. 
* Usually this is for customers outside of VMWare where they dont have access to 
* VMPersonZimlet but can still use LinkedIn, TwitterSearch etc Zimlets
*/
function UnknownPersonSlide() {
}

//Static variables
UnknownPersonSlide.PHOTO_ID = "unkownPerson_photoBG";
UnknownPersonSlide.PHOTO_PARENT_ID = "unkownPerson_photoBGDiv";
UnknownPersonSlide.TEXT_DIV_ID = "unkownPerson_TextDiv";
UnknownPersonSlide.PHOTO_BASE_URL =  "https://people.vmware.com/webapp/htdocs/ServeJPG.php?imgurl=https://people.vmware.com/employees/";
UnknownPersonSlide.EMPLOYEE_PROFILE_BASE_URL = "https://people.vmware.com/webapp/htdocs/PersonalProfile.php?guid=";

/**
* Implement onEmailHoverOver to get notified by Email tooltip zimlet.
* This function registers UnkownPerson Zimlet as a subscriber Zimlet
*/
UnknownPersonSlide.prototype.onEmailHoverOver =
function(emailZimlet) {
	emailZimlet.addSubscriberZimlet(this, false);
	this.emailZimlet = emailZimlet;
};

/**
* This is called by Email Zimlet when user hoverovers an email
*/
UnknownPersonSlide.prototype.showTooltip =
function() {
	var emailAddress = this.emailZimlet.emailAddress.toLowerCase();	
	if (emailAddress.indexOf("vmware.com") == -1) {
		this._handleImgLoadFailure();
		return;
	}
	var timeoutCallback = new AjxCallback(this, this._handleImgLoadFailure);
	this.emailZimlet.tooltip.popdown();
	this.emailZimlet.showBusyImg(timeoutCallback, -20, -15);
	var url = ZmZimletBase.PROXY + UnknownPersonSlide.EMPLOYEE_PROFILE_BASE_URL + emailAddress;
	AjxRpc.invoke(null, url, null, new AjxCallback(this, this._handleProfilePageResponse), true);
};

UnknownPersonSlide.prototype._handleProfilePageResponse =
function(response) {
	if(!response.success) {
		this._handleImgLoadFailure();
		return;
	}
	var imageURL = this._getImageUrlFromProfileHTML(response.text);
	var img = new Image();
	img.onload = AjxCallback.simpleClosure(this._handleImageLoad, this, img);
	img.id = UnknownPersonSlide.PHOTO_ID;
	img.src = ZmZimletBase.PROXY + imageURL;
};

/**
* Walks Profile page DOM and grabs the url
*/
UnknownPersonSlide.prototype._getImageUrlFromProfileHTML =
function(html) {
	var div  =  document.createElement("div");
	div.style.display = "none";
	div.innerHTML = html;
	var imgs = div.getElementsByTagName("img");
	for(var i =0; i< imgs.length; i++) {
		var img = imgs[i];
		if(img.className == "pic") {
			var src = img.src;
			var tmpArry = src.split("/");
			if(tmpArry && tmpArry.length > 0) {
				var imageName = tmpArry[tmpArry.length-1];
				return UnknownPersonSlide.PHOTO_BASE_URL + imageName;
			}
			break;
		}
	}
	return UnknownPersonSlide.PHOTO_BASE_URL + "noname.jpg";
};

UnknownPersonSlide.prototype._handleImgLoadFailure =
function() {//onfailure to load img w/in 5 secs, load an dataNotFound.png
	var img = new Image();
	img.onload = AjxCallback.simpleClosure(this._handleImageLoad, this, img);
	img.id = UnknownPersonSlide.PHOTO_ID;
	img.src = this.emailZimlet.getResource("img/UnknownPerson_dataNotFound.png");
};

UnknownPersonSlide.prototype._handleImageLoad =
function(img) {
	this.emailZimlet.hideBusyImg();
	var tthtml = this._getTooltipBGHtml();
	var selectCallback = new AjxCallback(this, this._handleSlideSelect, img);
	this._slide = new EmailToolTipSlide(tthtml, true, "UnknownPerson_contact", selectCallback, this.emailZimlet.getMessage("contact"));
	this.emailZimlet.slideShow.addSlide(this._slide);
	this._slide.select();
};

UnknownPersonSlide.prototype._handleSlideSelect =
function(img) {
	if (this._slide.loaded) {
		return;
	}
	if (img) {
		var div = document.getElementById(UnknownPersonSlide.PHOTO_PARENT_ID);
		div.insertBefore(img, document.getElementById(UnknownPersonSlide.TEXT_DIV_ID));
	}
	if(this.emailZimlet.emailAddress.indexOf("@vmware.com")  == -1) {
		this._GALRequestCallback(img, null);
	} else {//make gal search request
		var jsonObj, request, soapDoc;
		jsonObj = {SearchGalRequest:{_jsns:"urn:zimbraAccount"}};
		request = jsonObj.SearchGalRequest;
		request.type = "account";
		request.name = this.emailZimlet.emailAddress;
		request.offset = 0;
		request.limit = 3;
		var callback = new AjxCallback(this, this._GALRequestCallback, img);
		appCtxt.getAppController().sendRequest({jsonObj:jsonObj,asyncMode:true,callback:callback, noBusyOverlay:true});
	}
	this._slide.loaded = true;
};

UnknownPersonSlide.prototype._GALRequestCallback =
function(img, response) {
	var validResponse = false;
	var attrs = {};
	if(response) {
		var data = response.getResponse();	
		var cn = data.SearchGalResponse.cn;
		if (cn && cn[0] && cn[0]._attrs) {
			attrs = data.SearchGalResponse.cn[0]._attrs;
			validResponse = true;
		}
	}
	
	if(!validResponse) {
		if(this.emailZimlet.fullName) {
			attrs["fullName"] = this.emailZimlet.fullName;
		}
		if(this.emailZimlet.emailAddress) {
			attrs["email"] = this.emailZimlet.emailAddress;
		}
	}
	this._setContactDetails(attrs);
	this._adjustBGPhotoSize();
	this._adjustTextDivAspects();
	this._popupToolTip();
};

UnknownPersonSlide.prototype._popupToolTip =
function() {
	this.emailZimlet.tooltip.popup(this.emailZimlet.x, this.emailZimlet.y, true, null);
	this.emailZimlet.tooltip._poppedUp = false;
	this.emailZimlet.seriesAnimation.addFadeIn(EmailToolTipSlideShow.mainDivId);
	this.emailZimlet.seriesAnimation.addPause(500);
	this.emailZimlet.seriesAnimation.addslideUp(UnknownPersonSlide.TEXT_DIV_ID, this._textDivOffsetHeight);
	this.emailZimlet.seriesAnimation.startAnimation();
	setTimeout(AjxCallback.simpleClosure(this._slideTextDownIfMouseNotOver, this), 5000);
	this.addListeners();
};

UnknownPersonSlide.prototype.addListeners =
function() {
	document.getElementById(UnknownPersonSlide.PHOTO_PARENT_ID).onmouseover = AjxCallback.simpleClosure(this._slideTextDivUp, this);
	document.getElementById(UnknownPersonSlide.PHOTO_PARENT_ID).onmouseout = AjxCallback.simpleClosure(this._slideTextDivDown, this);
};

UnknownPersonSlide.prototype._slideTextDivUp =
function(ev) {
	this._hoverOnPhoto = true;
	if (!ev) {
		var ev = window.event;
	}
	var relTarg = ev.relatedTarget || ev.toElement;
	if (relTarg && (relTarg.parentNode.id == UnknownPersonSlide.TEXT_DIV_ID || relTarg.id == UnknownPersonSlide.TEXT_DIV_ID)) {
		return;
	}
	this.emailZimlet.seriesAnimation.addslideUp(UnknownPersonSlide.TEXT_DIV_ID, this._textDivOffsetHeight);
	this.emailZimlet.seriesAnimation.startAnimation();
};

UnknownPersonSlide.prototype._slideTextDivDown =
function(ev) {
	this._hoverOnPhoto = false;
	if (!ev) {
		var ev = window.event;
	}
	var relTarg = ev.relatedTarget || ev.toElement;
	if (relTarg && (relTarg.parentNode.id == UnknownPersonSlide.TEXT_DIV_ID || relTarg.id == UnknownPersonSlide.TEXT_DIV_ID)) {
		return;
	}
	this.emailZimlet.seriesAnimation.addslideDown(UnknownPersonSlide.TEXT_DIV_ID, 20);
	this.emailZimlet.seriesAnimation.startAnimation();
};

UnknownPersonSlide.prototype._slideTextDownIfMouseNotOver =
function() {
	if (!this._hoverOnPhoto) {
		this.emailZimlet.seriesAnimation.addslideDown(UnknownPersonSlide.TEXT_DIV_ID, 20);
		this.emailZimlet.seriesAnimation.startAnimation();
	}
};

UnknownPersonSlide.prototype.showTooltipVeil =
function() {
	if (this._toolTipVeil) {
		this._toolTipVeil.style.display = "block";
		return;
	}
	this._toolTipVeil = this.emailZimlet.getShell().getHtmlElement().appendChild(document.createElement('div'));
	this._toolTipVeil.style.position = "absolute";
	this._toolTipVeil.style.display = "block";
	this._toolTipVeil.style.width = "100%";
	this._toolTipVeil.style.height = "100%";
	this._toolTipVeil.style.zIndex = "700";
	this._toolTipVeil.style.background = "black";
	this._toolTipVeil.style.opacity = 0.2;
};

UnknownPersonSlide.prototype._adjustTextDivAspects =
function() {
	var styleObj = document.getElementById(UnknownPersonSlide.TEXT_DIV_ID).style;
	var height = document.getElementById(EmailToolTipSlideShow.navDivId).offsetHeight;
	var width = document.getElementById(UnknownPersonSlide.PHOTO_PARENT_ID).offsetWidth;
	styleObj.bottom = height + 8;
	styleObj.width = width;
	styleObj.zoom = 1;
	styleObj.filter = "progid:DXImageTransform.Microsoft.Alpha(opacity=50)";
	styleObj.opacity = 0.5;
};

UnknownPersonSlide.prototype._adjustBGPhotoSize =
function() {
	var el = document.getElementById(UnknownPersonSlide.PHOTO_ID);
	var width = el.width;
	var height = el.height;
	var changed = false;
	var keepAspectRatio = false;
	if (height > EmailTooltipZimlet.tooltipHeight) {
		var percent = parseInt((EmailTooltipZimlet.tooltipHeight / height) * 10) / 10;
		height = height * percent;
		changed = true;
		if (width > EmailTooltipZimlet.tooltipWidth) {//if both height & width are larger then we can shrink them w/ aspectRatio intact
			width = width * percent;
			keepAspectRatio = true;
		}
	}
	if (width > EmailTooltipZimlet.tooltipWidth && !keepAspectRatio) {
		var percent = parseInt((EmailTooltipZimlet.tooltipWidth / width) * 10) / 10;
		width = width * percent;
		changed = true;

	}
	if (!changed) {
		width = EmailTooltipZimlet.tooltipWidth;
		height = EmailTooltipZimlet.tooltipHeight;
	}
	el.width = width;
	el.height = height;

	document.getElementById(EmailToolTipSlideShow.mainDivId).style.width = width + "px";
	document.getElementById(EmailToolTipSlideShow.mainDivId).style.height = height + "px";
	document.getElementById(UnknownPersonSlide.PHOTO_PARENT_ID).style.display = "block";
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
	return AjxTemplate.expand("com_zimbra_email.templates.Email#Frame", subs);
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

	attrs["rightClickForMoreOptions"] = this.emailZimlet.getMessage("rightClickForMoreOptions");
	var iHtml = AjxTemplate.expand("com_zimbra_email.templates.Email#ContactDetails", attrs);
	this._setTextDivHeight(iHtml);
	document.getElementById(UnknownPersonSlide.TEXT_DIV_ID).innerHTML = iHtml;
	document.getElementById(UnknownPersonSlide.TEXT_DIV_ID).style.height = "0px"; //set it to 0 to basically hide it so we can slideUp
};

UnknownPersonSlide.prototype._setTextDivHeight =
function(html) {
	if (!this._tempdiv) {
		this._tempdiv = document.createElement("div");
		this._tempdiv.style.left = -1000;
		this.emailZimlet.getShell().getHtmlElement().appendChild(this._tempdiv);
	}
	this._tempdiv.innerHTML = html;
	this._textDivOffsetHeight = this._tempdiv.offsetHeight + 5;
	this._tempdiv.innerHTML = "";
};

