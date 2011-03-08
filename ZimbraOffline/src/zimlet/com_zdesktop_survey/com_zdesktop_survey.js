/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Zimlets
 * Copyright (C) 2011 VMware, Inc.
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

ZmSurveyZimlet = function() {
};
ZmSurveyZimlet.prototype = new ZmZimletBase;
ZmSurveyZimlet.prototype.constructor = ZmSurveyZimlet;

ZmSurveyZimlet.CURR_VERSION = "1.0";
ZmSurveyZimlet.ATTR_PREFIX = "ZDSurveyZimlet_";
ZmSurveyZimlet.INITIAL_WAIT_SECONDS = ZmSurveyZimlet.ATTR_PREFIX + "InitialWaitSeconds";
ZmSurveyZimlet.REMIND_ME_IN_SECONDS = ZmSurveyZimlet.ATTR_PREFIX + "RemindMeInSeconds";
ZmSurveyZimlet.IS_DONT_ASK_ME_AGAIN_SET = ZmSurveyZimlet.ATTR_PREFIX + "IsDontAskMeAgainSet";
ZmSurveyZimlet.SURVEY_TAKEN = ZmSurveyZimlet.ATTR_PREFIX + "SurveyTaken";
ZmSurveyZimlet.SURVEY_VERSION = ZmSurveyZimlet.ATTR_PREFIX + "SurveyVersion";
ZmSurveyZimlet.SURVEY_URL = ZmSurveyZimlet.ATTR_PREFIX + "SurveyURL";
ZmSurveyZimlet.FIRST_LAUNCH_DATE = ZmSurveyZimlet.ATTR_PREFIX + "FirstLaunchDate"; //Actual date of first usage
ZmSurveyZimlet.REMIND_ME_WAS_SET_ON_DATE = ZmSurveyZimlet.ATTR_PREFIX + "RemindMeWasSetOnDate"; //Date on which Remind me later button was pressed

ZmSurveyZimlet.prototype.init =
function() {
	this._resetZimletOnNewVersion();

	var locale = appCtxt.get("LOCALE_NAME").toLowerCase();
	if (locale.indexOf("en") != 0) { // until it's translated, don't run this for non-English users
		return;
	}
	var dontAsk = this.getUserProperty(ZmSurveyZimlet.IS_DONT_ASK_ME_AGAIN_SET);
	if (dontAsk && dontAsk == "true") {
		return;
	}
	var taken = this.getUserProperty(ZmSurveyZimlet.SURVEY_TAKEN);
	if (taken && taken == "true") {
		return;
	}
	if (!this._isOnline()) {
		return;
	}
	
	//check if this is first launch..
	this._now = this._getNowInSeconds();
	this.ZmSurveyZimlet_FirstLaunchDate = this.getUserProperty(ZmSurveyZimlet.FIRST_LAUNCH_DATE);
	if (!this.ZmSurveyZimlet_FirstLaunchDate || this.ZmSurveyZimlet_FirstLaunchDate == "") {
		this.setUserProperty(ZmSurveyZimlet.FIRST_LAUNCH_DATE, this._now, true);
		return;
	}

	if (this._hasPastInitialWaitSeconds() && this._hasPastReminderDate()) {
		this._showSurveyDialog();
	}
};

/**
 * Invalidate some attrs if it's a newer survey version
 * 
 */
ZmSurveyZimlet.prototype._resetZimletOnNewVersion = function() {
	var storedVersion = this.getUserProperty(ZmSurveyZimlet.SURVEY_VERSION);
	if(!storedVersion) {//first launch(no upgrade)
		this.setUserProperty(ZmSurveyZimlet.SURVEY_VERSION, ZmSurveyZimlet.CURR_VERSION, true);
		return;
	} else if(storedVersion == ZmSurveyZimlet.CURR_VERSION) {
		return;
	} else {//upgrade..
		this.setUserProperty(ZmSurveyZimlet.SURVEY_VERSION, ZmSurveyZimlet.CURR_VERSION, true);
		this.setUserProperty(ZmSurveyZimlet.SURVEY_TAKEN, "false", true);	
		this.setUserProperty(ZmSurveyZimlet.FIRST_LAUNCH_DATE, "", true);			
		this.setUserProperty(ZmSurveyZimlet.REMIND_ME_WAS_SET_ON_DATE, "", true);
	}	
};

ZmSurveyZimlet.prototype._hasPastInitialWaitSeconds = function() {
	var d1 = this.ZmSurveyZimlet_FirstLaunchDate;
	var waitSeconds = this.getUserProperty(ZmSurveyZimlet.INITIAL_WAIT_SECONDS);
	if(!waitSeconds) {
		return false;
	}

	if(parseInt(this._now) > (parseInt(d1) + parseInt(waitSeconds))) {
		return true;
	}
	return false;
};

ZmSurveyZimlet.prototype._hasPastReminderDate = function() {
	var remindMeWasSetOnDate = this.getUserProperty(ZmSurveyZimlet.REMIND_ME_WAS_SET_ON_DATE);
	if(!remindMeWasSetOnDate || remindMeWasSetOnDate == "") {//remindMeDate is not set
		return true;
	}

	var remindMeInSeconds = this.getUserProperty(ZmSurveyZimlet.REMIND_ME_IN_SECONDS);
	if(parseInt(this._now) > (parseInt(remindMeWasSetOnDate) + parseInt(remindMeInSeconds))) {
		return true;
	}
	return false;
};


/**
 * Displays the dialog.
 * 
 */
ZmSurveyZimlet.prototype._showSurveyDialog = 
function() {
	if (this.pbDialog) { //if zimlet dialog already exists...
		this.pbDialog.popup(); //simply popup the dialog
		return;
	}
	
	var sDialogTitle = this.getMessage("dialog_title");
	
	this.pView = new DwtComposite(this.getShell()); //creates an empty div as a child of main shell div
	this.pView.setSize("400", "100"); // set width and height
	this.pView.getHtmlElement().style.overflow = "auto"; // adds scrollbar
	this.pView.getHtmlElement().innerHTML = this._createDialogView(); // insert html to the dialogbox
	
	var remindMeLaterBtnId = Dwt.getNextId();
	var remindMeLaterBtn = new DwtDialog_ButtonDescriptor(remindMeLaterBtnId, this.getMessage("remindMeLater"), DwtDialog.ALIGN_RIGHT);
	var noDontAskmeAgainBtnId = Dwt.getNextId();
	var noDontAskmeAgainBtn = new DwtDialog_ButtonDescriptor(noDontAskmeAgainBtnId, this.getMessage("noDontAskMeAgain"), DwtDialog.ALIGN_RIGHT);
	// pass the title, view and buttons information and create dialog box
	this.pbDialog = new ZmDialog({title:sDialogTitle, 
								view:this.pView, 
								parent:this.getShell(), 
								standardButtons:[DwtDialog.YES_BUTTON], 
								extraButtons:[remindMeLaterBtn, noDontAskmeAgainBtn]});

	this.pbDialog.getButton(DwtDialog.YES_BUTTON).setImage("TasksApp");		
	this.pbDialog.getButton(remindMeLaterBtnId).setImage("ApptReminder");
	this.pbDialog.getButton(noDontAskmeAgainBtnId).setImage("Delete");
	
	this.pbDialog.setButtonListener(DwtDialog.YES_BUTTON, new AjxListener(this, this._handleYesButton));	
	this.pbDialog.setButtonListener(remindMeLaterBtnId, new AjxListener(this, this._handleRemindMeLater));
	this.pbDialog.setButtonListener(noDontAskmeAgainBtnId, new AjxListener(this, this._handleNoDontAskMeAgain));
	
	this.pbDialog.popup(); //show the dialog
};

/**
 * Opens Survey window and sets SURVEY_TAKEN to true
 * 
 */
ZmSurveyZimlet.prototype._handleYesButton =
function() {
	this.openCenteredWindow(this.getUserProperty(ZmSurveyZimlet.SURVEY_URL));
	this.setUserProperty(ZmSurveyZimlet.SURVEY_TAKEN, "true", true);
	this.pbDialog.popdown();
};

/**
 * Saves current date as remindMeSetDate. This is used to check and popup survey again
 * 
 */
ZmSurveyZimlet.prototype._handleRemindMeLater =
function() {
	this.setUserProperty(ZmSurveyZimlet.REMIND_ME_WAS_SET_ON_DATE, this._now, true);
	var remindMeInSeconds = this.getUserProperty(ZmSurveyZimlet.REMIND_ME_IN_SECONDS);
	var remindMeInDays = Math.floor(remindMeInSeconds / 86400);
	appCtxt.getAppController().setStatusMsg(AjxMessageFormat.format(this.getMessage("willRemindAfterXDays"), remindMeInDays));	
	this.pbDialog.popdown();
};

/**
 * Sets ZmSurveyZimlet.REMIND_ME_WAS_SET_ON_DATE to <code>true</code>
 * 
 */
ZmSurveyZimlet.prototype._handleNoDontAskMeAgain =
function() {
	this.setUserProperty(ZmSurveyZimlet.IS_DONT_ASK_ME_AGAIN_SET, "true", true);	
	this.pbDialog.popdown();
};

/**
 * Creates the dialog view.
 * 
 */
ZmSurveyZimlet.prototype._createDialogView =
function() {
	return this.getMessage("dialog_body");
};

ZmSurveyZimlet.prototype._getNowInSeconds =
function() {
	var d = new Date();
	return Math.floor(d.getTime() / 1000);
};

ZmSurveyZimlet.prototype._isOnline =
function() {
	return appCtxt.getAppController()._isPrismOnline;
};

ZmSurveyZimlet.prototype._showWarningMsg = function(message) {
	var style = DwtMessageDialog.WARNING_STYLE;
	var dialog = appCtxt.getMsgDialog();
	this.warningDialog = dialog;
	dialog.setMessage(message, style);
	dialog.popup();
};

ZmSurveyZimlet.prototype.openCenteredWindow =
function (url) {
	var width = 800;
	var height = 600;
	var left = parseInt((screen.availWidth / 2) - (width / 2));
	var top = parseInt((screen.availHeight / 2) - (height / 2));
	var windowFeatures = "width=" + width + ",height=" + height + ",status,resizable,scrollbars,left=" + left + ",top=" + top + "screenX=" + left + ",screenY=" + top;

	try {
		var win = window.open(url, "subWind", windowFeatures);
	} catch (e) {}
};
