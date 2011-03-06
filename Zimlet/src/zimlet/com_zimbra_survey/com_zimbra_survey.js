/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Zimlets
 * Copyright (C) 2005, 2006, 2007, 2008, 2009, 2010 Zimbra, Inc.
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

ZmSurveyZimlet.INITIAL_WAIT_DAYS = "ZmSurveyZimlet_InitialWaitDays";
ZmSurveyZimlet.REMIND_ME_IN_DAYS = "ZmSurveyZimlet_RemindMeInDays";
ZmSurveyZimlet.IS_DONT_ASK_ME_AGAIN_SET = "ZmSurveyZimlet_IsDontAskMeAgainSet";
ZmSurveyZimlet.SURVEY_TAKEN = "ZmSurveyZimlet_SurveyTaken";
ZmSurveyZimlet.SURVEY_FOR_ZIMBRA_VERSION = "ZmSurveyZimlet_SurveyForZimbraVersion";

ZmSurveyZimlet.SURVEY_URL = "ZmSurveyZimlet_SurveyURL";

//Actual date of first usage
ZmSurveyZimlet.FIRST_LAUNCH_DATE = "ZmSurveyZimlet_FirstLaunchDate";

//Date on which Remind me later button was pressed
ZmSurveyZimlet.REMIND_ME_WAS_SET_ON_DATE = "ZmSurveyZimlet_RemindMeWasSetOnDate";

ZmSurveyZimlet.prototype.init =
function() {
	this._resetZimletOnNewVersion();
	
	//check if this is first launch..
	this._todayStr = this._getTodayStr();
	this.ZmSurveyZimlet_FirstLaunchDate = this.getUserProperty(ZmSurveyZimlet.FIRST_LAUNCH_DATE);
	if(!this.ZmSurveyZimlet_FirstLaunchDate || this.ZmSurveyZimlet_FirstLaunchDate == "") {
		this.setUserProperty(ZmSurveyZimlet.FIRST_LAUNCH_DATE, this._todayStr, true);
		return;
	}
	var isSurveyTaken = this.getUserProperty(ZmSurveyZimlet.SURVEY_TAKEN) == "true";
	var isDonAskMeAgainSet =  this.getUserProperty(ZmSurveyZimlet.IS_DONT_ASK_ME_AGAIN_SET) == "true";
	if(!isSurveyTaken && !isDonAskMeAgainSet && this._hasPastInitialWaitDays() && this._hasPastReminderDate()) {
		this._showSurveyDialog();
	}
};

/**
 * Invalidate all the settings if its a new Zimbra version
 * 
 */
ZmSurveyZimlet.prototype._resetZimletOnNewVersion = function() {
	var currentVersion = appCtxt.getSettings().getInfoResponse.version;
	if(!currentVersion || currentVersion == "") {
		return;
	}
	var storedVersion = this.getUserProperty(ZmSurveyZimlet.SURVEY_FOR_ZIMBRA_VERSION);
	if(!storedVersion) {//first launch(no upgrade)
		this.setUserProperty(ZmSurveyZimlet.SURVEY_FOR_ZIMBRA_VERSION, currentVersion, true);
		return;
	} else if(storedVersion == currentVersion) {
		return;
	} else {//upgrade..
		this.setUserProperty(ZmSurveyZimlet.SURVEY_FOR_ZIMBRA_VERSION, currentVersion, true);
		this.setUserProperty(ZmSurveyZimlet.IS_DONT_ASK_ME_AGAIN_SET, "false", true);
		this.setUserProperty(ZmSurveyZimlet.SURVEY_TAKEN, "false", true);	
		this.setUserProperty(ZmSurveyZimlet.REMIND_ME_WAS_SET_ON_DATE, "", true);
		this.setUserProperty(ZmSurveyZimlet.FIRST_LAUNCH_DATE, "", true);			
	}	
};

ZmSurveyZimlet.prototype._hasPastInitialWaitDays = function() {
	var d1 = this.ZmSurveyZimlet_FirstLaunchDate;
	var waitDays = this.getUserProperty(ZmSurveyZimlet.INITIAL_WAIT_DAYS);
	if(!waitDays) {
		return false;
	}
	if(parseInt(this._todayStr) > (parseInt(d1) + (parseInt(waitDays) * 24 * 3600))) {
		return true;
	}
	return false;
};

ZmSurveyZimlet.prototype._hasPastReminderDate = function() {
	var remindMeWasSetOnDate = this.getUserProperty(ZmSurveyZimlet.REMIND_ME_WAS_SET_ON_DATE);
	if(!remindMeWasSetOnDate || remindMeWasSetOnDate == "") {//remindMeDate is not set
		return true;
	}
	var remindMeInDays = this.getUserProperty(ZmSurveyZimlet.REMIND_ME_IN_DAYS);
	if(parseInt(this._todayStr) > (parseInt(remindMeWasSetOnDate) + (parseInt(remindMeInDays) * 24 * 3600))) {
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
	debugger;
	var win = this.openCenteredWindow(this.getUserProperty(ZmSurveyZimlet.SURVEY_URL));
	if(win) {
		this.setUserProperty(ZmSurveyZimlet.SURVEY_TAKEN, "true", true);
	}
	this.pbDialog.popdown();
};

/**
 * Saves current date as remindMeSetDate. This is used to check and popup survey again
 * 
 */
ZmSurveyZimlet.prototype._handleRemindMeLater =
function() {
	this.setUserProperty(ZmSurveyZimlet.REMIND_ME_WAS_SET_ON_DATE, this._todayStr, true);
	var remindMeInDays = this.getUserProperty(ZmSurveyZimlet.REMIND_ME_IN_DAYS);
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



/*
 * -------------------------------------
 * Supporting functions
 * -------------------------------------
 */



/**
 * Gets time as of midnight today
 * 
 * @return	{string}	Gets time as of midnight today
 */
ZmSurveyZimlet.prototype._getTodayStr =
function() {
	var d = new Date();
	d.setHours(0, 0, 0, 0);
	return d.getTime();
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
	var win = window.open(url, "subWind", windowFeatures);
	if (!win) {
		this._showWarningMsg(ZmMsg.popupBlocker);
	}
	return win;
};
