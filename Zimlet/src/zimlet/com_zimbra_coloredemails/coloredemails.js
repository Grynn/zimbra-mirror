/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Zimlets
 * Copyright (C) 2008, 2009 Zimbra, Inc.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.2 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 *@Author Raja Rao DV
 */

function com_zimbra_coloredemails() {
}

com_zimbra_coloredemails.prototype = new ZmZimletBase();
com_zimbra_coloredemails.prototype.constructor = com_zimbra_coloredemails;

com_zimbra_coloredemails.prototype.init =
function() {
	this.turnColoredEmailsZimletON = this.getUserProperty("turnColoredEmailsZimletON") == "true";
	this._areVariablesInitialized = false;
	if (!this.turnColoredEmailsZimletON) {
		return;
	}

	this._initializeVariables();
	this._resetView();
};

com_zimbra_coloredemails.prototype._initializeVariables =
function() {
	this._defaultEmail = "joe@sender.com";
	this._cEmail_manualAddedEmailsArray = [];
	this._cEmail_autoAddedEmailsArray = [];
	this._colorChangedDueToTag = false;

	this.uprop_cEmail_manualAddedEmails = this.getUserProperty("cEmail_manualAddedEmails");
	this.uprop_cEmail_autoAddedEmails = this.getUserProperty("cEmail_autoAddedEmails");
	this.uprop_cEmail_autoAssociateChkbx = this.getUserProperty("cEmail_autoAssociateChkbx") == "true";
	this.uprop_cEmail_useAutoAssociatedEmailsChkbx = this.getUserProperty("cEmail_useAutoAssociatedEmailsChkbx") == "true";
	this._setEmails(this._cEmail_manualAddedEmailsArray, this.uprop_cEmail_manualAddedEmails);

	//if we also need to color auto-added emails..
	if (this.uprop_cEmail_useAutoAssociatedEmailsChkbx) {
		this._setEmails(this._cEmail_autoAddedEmailsArray, this.uprop_cEmail_autoAddedEmails);
		setTimeout(AjxCallback.simpleClosure(this._saveAutoAddedEmails, this), 300000);//save autoaddedemails after 5 minutes
	}
	this._areVariablesInitialized = true;
};

com_zimbra_coloredemails.prototype._saveAutoAddedEmails =
function() {
	if (this.uprop_cEmail_autoAddedEmails.split(",").length >= 25) {
		var tmp = this.uprop_cEmail_autoAddedEmails.split(",");
		var str = "";
		for (var i = 0; i < 25; i++) {
			if (str == "")
				str = AjxStringUtil.trim(tmp[i]);
			else
				str = str + ", " + AjxStringUtil.trim(tmp[i]);
		}
	}
	this.setUserProperty("cEmail_autoAddedEmails", str, true);
};

com_zimbra_coloredemails.prototype._setEmails =
function(storeArray, userProp) {
	var arry = userProp.split(",");
	for (var i = 0; i < arry.length; i++) {
		try {
			var tmp = arry[i].split(":");
			if (tmp[0] == this._defaultEmail)
				continue;

			var eml = AjxStringUtil.trim(tmp[0]).toLowerCase();
			if (tmp.length == 3) {
				var bg = AjxStringUtil.trim(tmp[2]);
				if (bg == "white" || bg == "#FFFFFF")//if background is white, ignore it
					storeArray[eml] = " style=\"color: " + AjxStringUtil.trim(tmp[1]) + ";\" ";
				else
					storeArray[eml] = " style=\"color: " + AjxStringUtil.trim(tmp[1]) + ";background-color: " + bg + ";\" ";
			} else if (tmp.length == 2) {
					storeArray[eml] = " style=\"color: " + AjxStringUtil.trim(tmp[1]) + ";\" ";
			}
		} catch(e) {
		}
	}
};


com_zimbra_coloredemails.prototype._autoAddEmail =
function(eml) {
	var colors = [ "#FF0000","#CC6600","#006600","#6600CC","#660000","#009900","#666666","#330000","#663333","#000099","#330033","#CC33CC","#6666CC","#CC9933","#CC0000","#006600"];
	var randomnumber = Math.floor(Math.random() * colors.length);
	if (this.uprop_cEmail_autoAddedEmails.indexOf(eml) == -1) {
		if(this.uprop_cEmail_autoAddedEmails == "")
			this.uprop_cEmail_autoAddedEmails = eml + ":" + colors[randomnumber] + ", ";
		else
			this.uprop_cEmail_autoAddedEmails =  this.uprop_cEmail_autoAddedEmails + ", " +  eml + ":" + colors[randomnumber] ;

		this._cEmail_autoAddedEmailsArray[eml] = " style=\"color: " + colors[randomnumber] + ";\" ";
	}

};


com_zimbra_coloredemails.prototype.onTagAction =
function(items, tag, doTag) {//basically refreshes the view to reflect new colors
	if (!this.turnColoredEmailsZimletON)
		return;
	if(!this._colorChangedDueToTag)
		return;

	try {
		var type = items[0].type;
		if (type != null && (type == ZmItem.CONV || type == ZmItem.MSG)) {
			this._resetView();
		}
	} catch(e) {
	}
	this._colorChangedDueToTag = false;
};


com_zimbra_coloredemails.prototype._resetView =
function() {
	try {
		var q = appCtxt.getSearchController().currentSearch.query;
		appCtxt.getSearchController().search({query:q});
	} catch(e) {
	}
};


com_zimbra_coloredemails.prototype.getMailCellStyle =
function(item, field) {
	if (!this.turnColoredEmailsZimletON)
		return null;

	var eml = "";
	var tagName = "";
	try {
		if (item.type == ZmId.ITEM_CONV) {
			var arry = item.participants.getArray();
			if(arry.length >0) {
				eml = arry[arry.length - 1].address;
			}
		} else if (item.type == ZmId.ITEM_MSG) {
			var obj = item.getAddress(AjxEmailAddress.FROM);
			if(obj)
				eml = obj.address;
		} else {
			return null;
		}

		var tgs = item.tags;
		if (tgs.length > 0) {
			tagName = appCtxt.getById(tgs[0]).name;
		}

	} catch(e) {
	}

	if (eml == "" && tagName == "") {
		return "";
	}

	//highest priority is for tags, then emails, followed by auto-added emails
	if (tagName != "") {//if we might need to color based on tagName
		var colorsByTag = this._cEmail_manualAddedEmailsArray[tagName];
		if (colorsByTag) {
			this._colorChangedDueToTag = true;
			return colorsByTag;
		}
	}
	var eml =  AjxStringUtil.trim(eml).toLowerCase();//trim and ignore case
	var colors = this._cEmail_manualAddedEmailsArray[eml];
	if (colors) {
		return colors;
	} 

	if (this.uprop_cEmail_autoAssociateChkbx) {//if we need to auto-associate
		this._autoAddEmail(eml);
		if (this.uprop_cEmail_useAutoAssociatedEmailsChkbx) {//if we need to color auto-added email...
			if(!this._cEmail_autoAddedEmailsArray[eml])
				return "";
			else
				return  this._cEmail_autoAddedEmailsArray[eml];//this will be populated by this._autoAddEmail
		}
	}

	return "";
};


com_zimbra_coloredemails.prototype.doubleClicked = function() {
	this.singleClicked();
};

com_zimbra_coloredemails.prototype.singleClicked = function() {
	this._showPreferenceDlg();
};

com_zimbra_coloredemails.prototype.doDrop =
function(msg) {
	if(msg instanceof Array)
		msg = msg[0];

	if (this.turnColoredEmailsZimletON) {
		this._showPreferenceDlg();
		var tagName = "";
		var eml = "";

		var tgs = msg.tags;
		if (tgs.length > 0) {
			tagName = appCtxt.getById(tgs[0]).name;
		}

		var from = msg.from;

		if (from) {
			eml = from[0].address;
		} else {
			eml = msg.participants[msg.participants.length - 1].address;
		}
		if (eml == "" && tagName == "")
			return;

		document.getElementById("cEmail_emailField").value = tagName != "" ? tagName : eml;
		document.getElementById("cEmail_exampleEmailNameTD").innerHTML = eml;
	}
};

com_zimbra_coloredemails.prototype._showPreferenceDlg = function() {
	//if zimlet dialog already exists...
	if (this._preferenceDialog) {
		//this._setZimletCurrentPreferences();
		this._preferenceDialog.popup();
		return;
	}
	this._preferenceView = new DwtComposite(this.getShell());
	this._preferenceView.setSize("520", "490");
	this._preferenceView.getHtmlElement().style.overflow = "auto";
	this._preferenceView.getHtmlElement().innerHTML = this._createPrefView();

	document.getElementById('cEmail_showHideLnk').onclick = AjxCallback.simpleClosure(this._showHideDetails, this);
	this._showHideDetails();//initially hide the div
	var pickrComposite = new DwtComposite(this.getShell());
	this._fontColorButton = new ZmHtmlEditorColorPicker(pickrComposite, null, "ZButton");
	this._fontColorButton.dontStealFocus();
	this._fontColorButton.setImage("FontColor");
	this._fontColorButton.showColorDisplay(true);
	this._fontColorButton.setToolTipContent(ZmMsg.fontColor);
	this._fontColorButton.addSelectionListener(new AjxListener(this, this._emailAndColorBtnListener));
	document.getElementById("cEmail_colorsMenuTD").appendChild(pickrComposite.getHtmlElement());

	var pickrBgComposite = new DwtComposite(this.getShell());
	this._fontBackgroundButton = new ZmHtmlEditorColorPicker(pickrBgComposite, null, "ZButton");
	this._fontBackgroundButton.dontStealFocus();
	this._fontBackgroundButton.setImage("FontBackground");
	this._fontBackgroundButton.showColorDisplay(true);
	this._fontBackgroundButton.setToolTipContent(ZmMsg.fontBackground);
	this._fontBackgroundButton.addSelectionListener(new AjxListener(this, this._emailAndColorBtnListener));
	document.getElementById("cEmail_colorsBgMenuTD").appendChild(pickrBgComposite.getHtmlElement());

	var addButton = new DwtButton(this.getShell());
	addButton.setText("Add");
	addButton.addSelectionListener(new AjxListener(this, this._addButtonListener, true));
	document.getElementById("cEmail_AddBtnTD").appendChild(addButton.getHtmlElement());

	var saveButtonId = Dwt.getNextId();
	var saveButton = new DwtDialog_ButtonDescriptor(saveButtonId, ("Save Changes"), DwtDialog.ALIGN_RIGHT);
	this._preferenceDialog = this._createDialog({title:"'Colored Emails' Zimlet Preferences", view:this._preferenceView, standardButtons:[DwtDialog.CANCEL_BUTTON], extraButtons:[saveButton]});
	this._preferenceDialog.setButtonListener(saveButtonId, new AjxListener(this, this._okPreferenceBtnListener));

	Dwt.setHandler(document.getElementById("cEmail_emailField"), DwtEvent.ONKEYUP, AjxCallback.simpleClosure(this._emailAndColorBtnListener, this));

	this._setZimletCurrentPreferences();
	//    this._setZimletCurrentPreferences();
	this._preferenceDialog.popup();
};


com_zimbra_coloredemails.prototype._createPrefView =
function() {
	var html = new Array();
	var i = 0;
	html[i++] = "<BR>";
	html[i++] = "<DIV>";
	html[i++] = "<B>Associate a sender's email or tag name with a color:</B>";
	html[i++] = "</DIV>";
	html[i++] = "<TABLE width=95% align='center'>";
	html[i++] = "<TR><TD width='24%'>Email or Tag name:</TD>";
	html[i++] = "<TD width=40%><INPUT id='cEmail_emailField' type=\"text\" style=\"width:100%;\" type='text' value='" + this._defaultEmail + "'></INPUT></TD>";
	html[i++] = "<TD width='12%' id='cEmail_colorsMenuTD'></TD>";
	html[i++] = "<TD width='12%' id='cEmail_colorsBgMenuTD'></TD>";
	html[i++] = "<TD width='22%' id='cEmail_AddBtnTD'></TD></TR>";
	html[i++] = "</TABLE>";
	html[i++] = "</DIV>";

	html[i++] = "<DIV>";
	html[i++] = "<TABLE id='cEmail_exampleEmailTableID' class='cEmail_exampleEmailTable' align='center'>";
	html[i++] = "<TR><TD id='cEmail_exampleEmailNameTD'>" + this._defaultEmail + "</TD>";
	html[i++] = "<TD>Re:This is an example subject of an email</TD></TR>";
	html[i++] = "</TABLE>";
	html[i++] = "</DIV>";
	html[i++] = "<BR>";
	html[i++] = "<DIV>";
	html[i++] = "<a href=\"#\" id='cEmail_showHideLnk'></a>";
	html[i++] = "</DIV>";

	html[i++] = "<DIV id='cEmail_showHideDiv'>";
	html[i++] = "<TABLE width=95% align='center'>";
	html[i++] = "<TR><TD>Manually added Email & colors:</TD><TD id='cEmail_exampleFormatTD' width='65%'></TD></TR>";
	html[i++] = "<TR><TD colspan=2><TEXTAREA rows=\"5\" cols=\"20\" id='cEmail_manualAddedEmails'/></TEXTAREA></TD></TR>";
	html[i++] = "</TABLE>";
	html[i++] = "<BR>";
	html[i++] = "<BR>";
	html[i++] = "<TABLE width=95% >";
	html[i++] = "<TR><TD><input id='cEmail_autoAssociateChkbx'  type='checkbox'/><B>Automatically associate a sender's email with a color";
	html[i++] = "</B><BR>(This will do the job of adding emails and colors for you. You can review and simply remove unwanted emails, then move them to above section.)</TD></TR>";
	html[i++] = "<TR><TD><input id='cEmail_useAutoAssociatedEmailsChkbx'  type='checkbox'/> Also use automatically added emails shown below during coloring.";
	html[i++] = "</TD></TR>";
	html[i++] = "</TABLE>";
	html[i++] = "<TABLE width=95% align='center'>";
	html[i++] = "<TR><TD>Automatically added Email & color details:</TD></TR>";
	html[i++] = "<TR><TD><TEXTAREA rows=\"5\" cols=\"20\"  id='cEmail_autoAddedEmails'/></TEXTAREA></TD></TR>";
	html[i++] = "</TABLE>";
	html[i++] = "</DIV>";
	html[i++] = "<DIV>";
	html[i++] = "<BR>";
	html[i++] = "<TABLE  width=95%>";
	html[i++] = "<TR><TD><input id='turnONColoredEmailsChkbox'  type='checkbox'/>Turn ON 'Colored Emails'-Zimlet</TD></TR>";
	html[i++] = "<TR><TD><FONT size='1pt'>*Changes to the above preferences would refresh the browser</FONT></TD></TR>";
	html[i++] = "</TABLE>";
	html[i++] = "</DIV>";
	return html.join("");
};

com_zimbra_coloredemails.prototype._showHideDetails =
function() {
	var doc = document.getElementById("cEmail_showHideDiv");
	if (doc.style.display == "none") {
		this._preferenceView.setSize("520", "490");
		doc.style.display = "block";
		document.getElementById("cEmail_showHideLnk").innerHTML = "Hide Advanced Options";
	} else {
		doc.style.display = "none";
		this._preferenceView.setSize("520", "180");
		document.getElementById("cEmail_showHideLnk").innerHTML = "Show Advanced Options";
	}
};

com_zimbra_coloredemails.prototype._addButtonListener =
function(showInfoMsg) {
	var email = document.getElementById("cEmail_emailField").value;


	var fColor = this._fontColorButton.getColor();
	var bColor = this._fontBackgroundButton.getColor();
	//check for duplicates
	this._mAddedEmailsTextarea = document.getElementById("cEmail_manualAddedEmails");
	var allEmails = this._mAddedEmailsTextarea.value;

	//remove duplicates(replace old with new)
	var arry = allEmails.split(",");
	var nonDupeArry = [];
	for (var i = 0; i < arry.length; i++) {
		var mailAndColor = AjxStringUtil.trim(arry[i]);
		var tmpArry = mailAndColor.split(":");
		if (tmpArry.length == 0)//there is no :
			continue;

		var currEmail = tmpArry[0].toLowerCase();
		var newEmail = email.toLowerCase();
		if (newEmail != currEmail) {
			nonDupeArry.push(mailAndColor);
		}
	}
	var nonDupeVal = nonDupeArry.join(", ");
	this._mAddedEmailsTextarea.value = email + ":" + fColor + ":" + bColor + ", " + nonDupeVal;

	if (showInfoMsg) {
		var transitions = [ ZmToast.FADE_IN, ZmToast.PAUSE, ZmToast.PAUSE, ZmToast.FADE_OUT ];
		appCtxt.getAppController().setStatusMsg("New Color Added", ZmStatusView.LEVEL_INFO, null, transitions);
	}
};

com_zimbra_coloredemails.prototype._emailAndColorBtnListener =
function(ev) {
	document.getElementById("cEmail_exampleEmailTableID").style.color = this._fontColorButton.getColor();
	document.getElementById("cEmail_exampleEmailTableID").style.backgroundColor = this._fontBackgroundButton.getColor();
	var email = document.getElementById("cEmail_emailField").value;
	if (email == "undefined") {
		email = this._defaultEmail;
		document.getElementById("cEmail_emailField").value = email;
	}

	document.getElementById("cEmail_exampleFormatTD").innerHTML = "(Current Value:  " + email + ":" + this._fontColorButton.getColor() + ":" + this._fontBackgroundButton.getColor() + ")";
	document.getElementById("cEmail_exampleEmailNameTD").innerHTML = email;
};

com_zimbra_coloredemails.prototype._setZimletCurrentPreferences =
function(ev) {

	if (!this._areVariablesInitialized) {
		this._initializeVariables();
	}

	//set the values to ui..
	if (this.turnColoredEmailsZimletON) {
		document.getElementById("turnONColoredEmailsChkbox").checked = true;
	}

	if (this.uprop_cEmail_autoAssociateChkbx) {
		document.getElementById("cEmail_autoAssociateChkbx").checked = true;
	}
	if (this.uprop_cEmail_useAutoAssociatedEmailsChkbx) {
		document.getElementById("cEmail_useAutoAssociatedEmailsChkbx").checked = true;
	}

	document.getElementById("cEmail_manualAddedEmails").value = this.uprop_cEmail_manualAddedEmails;
	document.getElementById("cEmail_autoAddedEmails").value = this.uprop_cEmail_autoAddedEmails;

	this._fontColorButton.setColor("#009900");
	this._fontBackgroundButton.setColor("#FFFFFF");
	this._emailAndColorBtnListener();
};

com_zimbra_coloredemails.prototype._okPreferenceBtnListener =
function() {
	this._addButtonListener(false);//people might forget to press Add button, so try to add them
	this._reloadRequired = false;
	if (document.getElementById("turnONColoredEmailsChkbox").checked) {
		if (!this.turnColoredEmailsZimletON) {
			this._reloadRequired = true;
		}
		this.setUserProperty("turnColoredEmailsZimletON", "true", true);

	} else {
		this.setUserProperty("turnColoredEmailsZimletON", "false", true);
		if (this.turnColoredEmailsZimletON)
			this._reloadRequired = true;
	}


	if (document.getElementById("cEmail_useAutoAssociatedEmailsChkbx").checked) {
		if (!this.uprop_cEmail_useAutoAssociatedEmailsChkbx) {
			this._reloadRequired = true;
		}
		this.setUserProperty("cEmail_useAutoAssociatedEmailsChkbx", "true", true);

	} else {
		this.setUserProperty("cEmail_useAutoAssociatedEmailsChkbx", "false", true);
		if (this.uprop_cEmail_useAutoAssociatedEmailsChkbx)
			this._reloadRequired = true;
	}
	if (document.getElementById("cEmail_autoAssociateChkbx").checked) {
		if (!this.uprop_cEmail_autoAssociateChkbx) {
			this._reloadRequired = true;
		}
		this.setUserProperty("cEmail_autoAssociateChkbx", "true", true);

	} else {
		this.setUserProperty("cEmail_autoAssociateChkbx", "false", true);
		if (this.uprop_cEmail_autoAssociateChkbx)
			this._reloadRequired = true;
	}

	this.setUserProperty("cEmail_manualAddedEmails", document.getElementById("cEmail_manualAddedEmails").value, true);
	this.uprop_cEmail_autoAddedEmails = document.getElementById("cEmail_autoAddedEmails").value;
	this._saveAutoAddedEmails();
	//reset the values and run getMsg to instantly reflect the changes
	this._cEmail_manualAddedEmailsArray = [];
	this._setEmails(this._cEmail_manualAddedEmailsArray, document.getElementById("cEmail_manualAddedEmails").value);
	this._resetView();

	appCtxt.getAppController().setStatusMsg("Changes are Saved", ZmStatusView.LEVEL_INFO);

	this._preferenceDialog.popdown();
	if (this._reloadRequired) {
		var transitions = [ ZmToast.FADE_IN, ZmToast.PAUSE, ZmToast.PAUSE, ZmToast.PAUSE,  ZmToast.FADE_OUT ];
		appCtxt.getAppController().setStatusMsg("Browser will be refreshed for changes to take effect..", ZmStatusView.LEVEL_INFO, null, transitions);
		setTimeout(AjxCallback.simpleClosure(this._reloadBrowser, this), 5000);
	}
};

com_zimbra_coloredemails.prototype._reloadBrowser =
function() {
	window.onbeforeunload = null;
	var url = AjxUtil.formatUrl({});
	ZmZimbraMail.sendRedirect(url);
};