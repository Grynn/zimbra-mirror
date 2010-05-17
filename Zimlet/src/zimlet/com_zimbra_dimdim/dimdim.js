/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Zimlets
 * Copyright (C) 2007, 2009, 2010 Zimbra, Inc.
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
 * Constructor.
 *
 * @author Raja Rao DV (rrao@zimbra.com)
 */
function com_zimbra_dimdim_HandlerObject() {
}

com_zimbra_dimdim_HandlerObject.prototype = new ZmZimletBase();
com_zimbra_dimdim_HandlerObject.prototype.constructor = com_zimbra_dimdim_HandlerObject;

/**
 * Simplify handler object
 *
 */
var DimDimZimlet = com_zimbra_dimdim_HandlerObject;

/**
 * Stores DimDim userName property.
 */
DimDimZimlet.PROP_USERNAME = {propId:"DimDimZimlet_username", label:"DimDimZimlet_userName", defaultVal:"", extraLabel:""};

/**
 * Stores DimDim password property.
 */
DimDimZimlet.PROP_PASSWORD = {propId:"DimDimZimlet_pwd", label:"DimDimZimlet_password", defaultVal:"", extraLabel:"", objType:"password"};

/**
 * Stores DimDim company id property.
 */
DimDimZimlet.PROP_COMPANY_ID = {propId:"DimDimZimlet_companyId", label:"DimDimZimlet_companyId", defaultVal:"",
	extraLabel:""};


/**
 * Stores calendar that is associated with a particular DimDim Account
 */
DimDimZimlet.PROP_ASSOCIATED_CALENDAR = {propId:"DimDimZimlet_AssociatedCalendar", label:"DimDimZimlet_useThisAccountFor", defaultVal:"", extraLabel:""};

/**
 * Stores Meeting password for a particular account
 */
DimDimZimlet.PROP_MEETING_PASSWORD = {propId:"DimDimZimlet_meetingPwd", label:"DimDimZimlet_meetingPwd", defaultVal:"", extraLabel:"DimDimZimlet_optional"};

/**
 * Stores toll-free phone number property
 */
DimDimZimlet.PROP_TOLL_FREE_PHONE_NUMBER = {propId:"DimDimZimlet_tollFreePH", label:"DimDimZimlet_tollFreeNumber", defaultVal:"", extraLabel:""};

/**
 * Stores toll phone number property.
 */
DimDimZimlet.PROP_TOLL_PHONE_NUMBER = {propId:"DimDimZimlet_tollPH", label:"DimDimZimlet_tollNumber", defaultVal:"", extraLabel:""};

/**
 * Stores DimDim international phone number property.
 */
DimDimZimlet.PROP_INTL_PHONE_NUMBER = {propId:"DimDimZimlet_intlPH", label:"DimDimZimlet_intlPhoneNumber", defaultVal:"", extraLabel:""};

/**
 * Stores DimDim phone passcode property.
 */
DimDimZimlet.PROP_PHONE_PASSCODE = {propId:"DimDimZimlet_phonePasscode", label:"DimDimZimlet_phonePasscode", defaultVal:"", extraLabel:"DimDimZimlet_digitsOnly"};

/**
 * An Array w/ just account properties. Helps in drawing account specific UI
 */
DimDimZimlet.SINGLE_DimDim_ACCNT_PROPS = [DimDimZimlet.PROP_USERNAME,DimDimZimlet.PROP_PASSWORD,
	DimDimZimlet.PROP_COMPANY_ID, DimDimZimlet.PROP_MEETING_PASSWORD];

/**
 * An Array w/ just tele-conf properties.  Used in drawing tele-conf specific UI
 */
DimDimZimlet.DimDim_TELECONF_PROPS = [DimDimZimlet.PROP_TOLL_FREE_PHONE_NUMBER,
	DimDimZimlet.PROP_TOLL_PHONE_NUMBER,DimDimZimlet.PROP_INTL_PHONE_NUMBER,
	DimDimZimlet.PROP_PHONE_PASSCODE];

/**
 * An array with all properties.
 */
DimDimZimlet.ALL_ACCNT_PROPS = [DimDimZimlet.PROP_USERNAME,DimDimZimlet.PROP_PASSWORD,
	DimDimZimlet.PROP_COMPANY_ID,DimDimZimlet.PROP_MEETING_PASSWORD,
	DimDimZimlet.PROP_ASSOCIATED_CALENDAR,DimDimZimlet.PROP_TOLL_FREE_PHONE_NUMBER,
	DimDimZimlet.PROP_TOLL_PHONE_NUMBER,DimDimZimlet.PROP_INTL_PHONE_NUMBER,
	DimDimZimlet.PROP_PHONE_PASSCODE];


/**
 * Stores append meeting password  property
 */
DimDimZimlet.PROP_APPEND_DimDim_MEETING_PWD = {propId:"DimDimZimlet_appendDimDimMeetingPassword", label:"DimDimZimlet_appendMeetingPwd", defaultVal:"NONE", extraLabel:""};

/**
 * Stores append toll free property property
 */
DimDimZimlet.PROP_APPEND_TOLL_FREE_PHONE_NUMBER = {propId:"DimDimZimlet_appendTollFreeConfNumber", label:"DimDimZimlet_appendToolFreeNumber", defaultVal:"NONE", extraLabel:""};

/**
 * Stores append toll phone property
 */
DimDimZimlet.PROP_APPEND_TOLL_PHONE_NUMBER = {propId:"DimDimZimlet_appendTollConfNumber", label:"DimDimZimlet_appendTollConfNumber", defaultVal:"NONE", extraLabel:""};

/**
 * Stores append passcode property
 */
DimDimZimlet.PROP_APPEND_PHONE_PASSCODE = {propId:"DimDimZimlet_appendPhonePasscode", label:"DimDimZimlet_appendPhonePasscode", defaultVal:"NONE", extraLabel:""};

/**
 * Helps draw append options UI
 */
DimDimZimlet.APPEND_SUB_OPTIONS = [DimDimZimlet.PROP_APPEND_DimDim_MEETING_PWD,
	DimDimZimlet.PROP_APPEND_TOLL_FREE_PHONE_NUMBER,DimDimZimlet.PROP_APPEND_TOLL_PHONE_NUMBER,
	DimDimZimlet.PROP_APPEND_PHONE_PASSCODE];

/**
 * Array with all General options. This entire list is stored  in Zimbra DB
 */
DimDimZimlet.ALL_GENERAL_PROPS = [DimDimZimlet.PROP_APPEND_DimDim_MEETING_PWD,
	DimDimZimlet.PROP_APPEND_TOLL_FREE_PHONE_NUMBER,DimDimZimlet.PROP_APPEND_TOLL_PHONE_NUMBER,
	DimDimZimlet.PROP_APPEND_PHONE_PASSCODE];


/**
 * Map the DimDim timezoneIds to TimeZone Names 
 * Note: For most TimeZones, DimDim's TimeZone name is exactly same as Zimbra's. This list only those where we need to map.
 *
 */
DimDimZimlet.DimDimToZimbraTZIDMap = {"Asia/Kolkata" :"Asia/Calcutta","Etc/GMT+12" :"HST","America/Los_Angeles" :"US/Pacific-New","America/Tijuana" :"US/Pacific-New","America/Phoenix" :"US/Arizona","America/Denver" :"US/Mountain","America/Chicago" :"US/East-Indiana","America/New_York" :"US/East-Indiana","America/Indiana/Indianapolis" :"US/East-Indiana"};


/**
 * Converts a Zimbra's TimeZone Name to DimDim's timezone name
 */
DimDimZimlet.prototype.convertTimeZone_Zimbra2DimDim = function(zTimeZone) {
	if(!this._notSupportedTZ) {
		this._notSupportedTZ =	["Pacific/Auckland","Pacific/Honolulu","America/Anchorage","America/Halifax","America/Guyana",
			"America/La_Paz","America/Manaus","America/Sao_Paulo","America/Argentina/Buenos_Aires","America/Montevideo",
			"Atlantic/South_Georgia","Africa/Monrovia","Africa/Algiers","Africa/Windhoek","Europe/Warsaw","Europe/Berlin",
			"Asia/Amman","Asia/Beirut","Europe/Minsk","Asia/Tbilisi","Asia/Yerevan","Asia/Tashkent","Asia/Yekaterinburg",
			"America/Regina","Asia/Novosibirsk","Asia/Kuala_Lumpur","Australia/Sydney","Pacific/Guam"];
	}
	if(this._notSupportedTZ[zTimeZone]) {
		throw new AjxException("TimeZone Not supported by DimDim", AjxException.INTERNAL_ERROR, "convertTimeZone_Zimbra2DimDim");
	}

	if(DimDimZimlet.DimDimToZimbraTZIDMap[zTimeZone]) {
		return DimDimZimlet.DimDimToZimbraTZIDMap[zTimeZone];
	}
	return zTimeZone;//dimdim directly supports this timezone

};


/**
 * Initializes the zimlet.
 *
 */
DimDimZimlet.prototype.init = function() {
	this.metaData = appCtxt.getActiveAccount().metaData;
	//this._displayGeneralPrefsDlg();
};

/**
 * Adds button to Calendar toolbar.
 *
 */
DimDimZimlet.prototype.initializeToolbar = function(app, toolbar, controller, viewId) {
	if (viewId.indexOf("APPT") >= 0) {
		this._initCalendarDimDimToolbar(toolbar, controller);
	}
};

/**
 * Called by the framework when an appointment is deleted
 *
 * @param {ZmAppt|array} appt	the appointment or an array of {@link ZmAppt} objects just deleted
 */
DimDimZimlet.prototype.onAppointmentDelete = function(appt) {
	if (!(appt instanceof ZmAppt)) {
		for (var el in appt) {
			appt = appt[el][0];
			break
		}
	}
	this._appt = appt;//store this appt.
	var postCallback;
	if (appt.viewMode != ZmCalItem.MODE_DELETE_INSTANCE) {
		postCallback = new AjxCallback(this, this._doDeleteDimDimAppt);
	} else {
		postCallback = new AjxCallback(this, this._showDeleteDimDimApptYesNoDlg);
	}
	this._getApptIdsHashMetaData(appt.id, postCallback);
};

/**
 * Displays Yes/No dialog.
 *
 * @param {hash} DimDimKeyData  a hash of parameters
 * @param {int} DimDimKeyData.scheduleId	the DimDim meeting key
 * @param {int} DimDimKeyData.exceptionMeetingKey	the DimDim meeting key of an exception to the series appt
 * @param {string} DimDimKeyData.username	 the DimDim username
 * @param {string} DimDimKeyData.password	the DimDim password
 * @param {string} DimDimKeyData.companyId	the DimDim company id
 */
DimDimZimlet.prototype._showDeleteDimDimApptYesNoDlg =
function(DimDimKeyData) {
	if (!DimDimKeyData || !DimDimKeyData.scheduleId) {
		return;
	}
	var dlg = appCtxt.getYesNoMsgDialog();
	dlg.registerCallback(DwtDialog.YES_BUTTON, this._deleteYesButtonClicked, this, [dlg, DimDimKeyData]);
	dlg.registerCallback(DwtDialog.NO_BUTTON, this._deleteNoButtonClicked, this, dlg);
	dlg.setMessage(this.getMessage("DimDimZimlet_entireSeriesWillBeDeleted"), DwtMessageDialog.WARNING_STYLE);
	dlg.popup();
};

/**
 * Called when yes button is clicked to confirm delete.
 *
 * @param {ZmDialog} dlg		the dialog
 * @param {obj} DimDimKeyData	 the object containing DimDim information stored for a particular appointment
 */
DimDimZimlet.prototype._deleteYesButtonClicked =
function(dlg, DimDimKeyData) {
	dlg.popdown();
	this._doDeleteDimDimAppt(DimDimKeyData);
};

/**
 * Deletes a DimDim appointment.
 *
 * @param {hash} DimDimKeyData	a hash of parameters
 *
 * @see this._showDeleteDimDimApptYesNoDlg
 */
DimDimZimlet.prototype._doDeleteDimDimAppt =
function(DimDimKeyData) {
	var key = "";
	if (!DimDimKeyData || !DimDimKeyData.scheduleId) {
		return;
	}
	key = DimDimKeyData.scheduleId;
	if (this._appt.viewMode == ZmCalItem.MODE_DELETE || this._appt.viewMode == ZmCalItem.MODE_DELETE_INSTANCE) {
		if (DimDimKeyData.exceptionMeetingKey) {
			key = DimDimKeyData.exceptionMeetingKey;
		}
	}
	var url = this._getDimDimServerURL()+"/api/prtl/delete_schedule";
	var proxyUrl = [ZmZimletBase.PROXY, AjxStringUtil.urlComponentEncode(url)].join(""); 
	var jsonData = ["{\"request\":{\"scheduleId\": \"", key,"\"}}"].join("");
	var params = {jsonData: jsonData, proxyUrl:proxyUrl, errorMsg: "Could not delete DimDim meeting"};
	var callback = new AjxCallback(this, this._deleteDimDimWasSuccessful);
	params.callback = callback;
	this._doPost(params);
};

DimDimZimlet.prototype._deleteDimDimWasSuccessful =
function() {
	appCtxt.getAppController().setStatusMsg(this.getMessage("DimDimZimlet_meetingWasDeleted"), ZmStatusView.LEVEL_INFO);
};

/**
 * Closes warning dialog when No was clicked to a a warning.
 *
 * @param {ZmDialog} dlg		 the yes/no dialog
 */
DimDimZimlet.prototype._deleteNoButtonClicked =
function(dlg) {
	dlg.popdown();
};

/**
 * Initiates calendar toolbar.
 *
 * @param {ZmToolbar} toolbar	 the Zimbra toolbar
 * @param {ZmCalController} controller  the Zimbra calendar controller
 */
DimDimZimlet.prototype._initCalendarDimDimToolbar = function(toolbar, controller) {
	if (!toolbar.getButton("SAVE_AS_DimDim")) {
		ZmMsg.sforceAdd = this.getMessage("DimDimZimlet_saveAsDimDim");
		for (var i = 0; i < toolbar.opList.length; i++) {
			if (toolbar.opList[i] == "COMPOSE_FORMAT" || toolbar.opList[i] == "VIEW_MENU") {
				buttonIndex = i + 1;
				break;
			}
		}
		var btn = toolbar.createOp("SAVE_AS_DimDim", {image:"DimDim-panelIcon", text:ZmMsg.sforceAdd, tooltip:this.getMessage("DimDimZimlet_savesThisApptAsDimDim"), index:buttonIndex});
		var buttonIndex = 0;
		toolbar.addOp("SAVE_AS_DimDim", buttonIndex);
		this._composerCtrl = controller;
		this._composerCtrl._DimDimZimlet = this;
		btn.addSelectionListener(new AjxListener(this._composerCtrl, this._saveAsDimDimHandler));
	}
};
/**
 * Saves a DimDim appointment.
 *
 * @param {event} ev		an event object
 */
DimDimZimlet.prototype._saveAsDimDimHandler = function(ev) {
	try {
		if (this._composeView.isValid()) {
			var appt = this._composeView.getAppt();
			var viewMode = appt.viewMode;
			var params = {apptController:this, apptComposeView:this._composeView, appt:appt};

			//check if it is an update.. if so, check if we already have that appt.
			if (viewMode != ZmCalItem.MODE_EDIT_SINGLE_INSTANCE && viewMode != ZmCalItem.MODE_EDIT
					&& viewMode != ZmCalItem.MODE_EDIT_SERIES) {
				this._DimDimZimlet._doSaveDimDimAppt(params);
			} else {
				var postCallback = new AjxCallback(this._DimDimZimlet, this._DimDimZimlet._checkIfItsDimDimUpdate, params);
				this._DimDimZimlet._getApptIdsHashMetaData(appt.id, postCallback);
			}
		}
	} catch(ex) {
		this._showErrorMessage(ex);
		return;
	}
};

/**
 * Verifies if its a DimDim appointment
 *
 * @param {hash} params		a hash of parameters
 * @param {ZmCalComposeController} params.apptController the calendar Controller
 * @param {ZmCalendarComposeView} params.apptComposeView the Calendar compose view
 * @param {ZmAppointment} params.appt	ann appointment
 * @param {hash} DimDimKeyData for more details
 *
 * @see {this._showDeleteDimDimApptYesNoDlg}
 */
DimDimZimlet.prototype._checkIfItsDimDimUpdate =
function(params, DimDimKeyData) {
	if (!DimDimKeyData) {
		this._doSaveDimDimAppt(params);
	} else if (DimDimKeyData && DimDimKeyData.scheduleId) {
		params.DimDimKeyData = DimDimKeyData;
		this._showUpdateDimDimApptYesNoDlg(params);
	}
};

/**
 * Shows warning dialog.
 *
 * @param {hash}	params  a hash of parameters
 *
 * @see this._checkIfItsDimDimUpdate
 */
DimDimZimlet.prototype._showUpdateDimDimApptYesNoDlg =
function(params) {
	var dlg = appCtxt.getYesNoMsgDialog();
	dlg.registerCallback(DwtDialog.YES_BUTTON, this._updateYesButtonClicked, this, [dlg, params]);
	dlg.registerCallback(DwtDialog.NO_BUTTON, this._updateNoButtonClicked, this, dlg);
	dlg.setMessage(this.getMessage("DimDimZimlet_modifyApptAvoidDuplicateInfoWarning"), DwtMessageDialog.WARNING_STYLE);
	dlg.popup();
};

/**
 * Called when Yes is clicked on warning dialog.
 *
 * @param {ZmDialog} dlg	the dialog
 * @param {hash}	params  a hash of parameters
 *
 * @see this._checkIfItsDimDimUpdate
 */
DimDimZimlet.prototype._updateYesButtonClicked =
function(dlg, params) {
	dlg.popdown();
	this._doSaveDimDimAppt(params);
};

/**
 * Called when no button was clicked.
 *
 * @param {ZmDialog} dlg  a dialog box
 */
DimDimZimlet.prototype._updateNoButtonClicked =
function(dlg) {
	dlg.popdown();
};

/**
 * Saves DimDim appointment.
 *
 * @param {hash}	params  a hash of parameters
 *
 * @see this._checkIfItsDimDimUpdate
 */
DimDimZimlet.prototype._doSaveDimDimAppt = function(params) {
	var postCallback = new AjxCallback(this, this._doCreateOrUpdateMeeting, params);
	this._showSelectAccountDlg(postCallback, params.appt);
};

DimDimZimlet.prototype._loginToDimDim = function(accountNumber) {
	if(!this._accountNumberAndSessionIdsMap) {
		this._accountNumberAndSessionIdsMap = {};
	}
	if(this._accountNumberAndSessionIdsMap[accountNumber]) {
		return;
	}
	if(!accountNumber) {
		throw new AjxException("Account number is undefined", AjxException.INTERNAL_ERROR, "_loginToDimDim");
	}
	this._setCurrentAccntInfoFromAccntNumber(accountNumber);
	var user = this._currentDimDimAccount[DimDimZimlet.PROP_USERNAME.propId];
	var pwd = this._currentDimDimAccount[DimDimZimlet.PROP_PASSWORD.propId];
	var url = [this._getDimDimServerURL(),"/api/auth/login"].join("");
	var proxyUrl = [ZmZimletBase.PROXY, AjxStringUtil.urlComponentEncode(url)].join(""); 
	var jsonData = ["{\"request\":{\"account\": \"", AjxStringUtil.urlComponentEncode(user),"\",\"password\": \"", AjxStringUtil.urlComponentEncode(pwd),"\",\"group\": \"all\"}}"].join("");

	var hdrs = new Array();
	hdrs["Content-type"] = "application/x-www-form-urlencoded";
	hdrs["Content-length"] = jsonData.length; 
	hdrs["Connection"] = "close";
	var response = AjxRpc.invoke(jsonData, proxyUrl, hdrs, null, false);
	var respObj  = eval("("+ response.text + ")");
	if(!respObj || respObj.result != true || !respObj.response.authToken) {
		throw new AjxException("Could not login to DimDim", AjxException.INTERNAL_ERROR, "_doPost"); 
		return;
	}
	this._currentDimDimAccountNumber = accountNumber;//store this 
	this._accountNumberAndSessionIdsMap[accountNumber] = respObj.response.authToken;//store authToken
};

/**
 * Does HTTP post to DimDim server
 * @param {hash}	params  a hash of parameters
 * @param {string}	params.jsonData  All the data in JSON format
 * @param {string}	params.proxyUrl  URL for HTTP POST
 * @param {AjxCallback}	params.callback Optional. A callback.  If null, this does a synchronous request. 
 * @return {object} JSON object of the response
 */
DimDimZimlet.prototype._doPost = function(params) {
	var jsonData = params.jsonData;
	var proxyUrl  = params.proxyUrl;
	var callback = params.callback;
	var hdrs = new Array();
	hdrs["Content-type"] = "application/x-www-form-urlencoded";
	hdrs["Content-length"] = jsonData.length; 
	hdrs["Connection"] = "close";
	if(this._accountNumberAndSessionIdsMap && this._currentDimDimAccountNumber) {
		hdrs["X-Dimdim-Auth-Token"] = this._accountNumberAndSessionIdsMap[this._currentDimDimAccountNumber];
	}
	if(callback) {
		var callback1 =  new AjxCallback(this, this._handleDoPost, callback);
	}
	AjxRpc.invoke(jsonData, proxyUrl, hdrs, callback1, false);
};

/**
 * Handles HTTP POST to DimDim server
 * @param {AjxCallback} callback An AJAX callback to be called after we got the result
 * @param {response} Response for HTTP POST request
 */
DimDimZimlet.prototype._handleDoPost = function(callback, response) {
	var respObj = eval("("+response.text +")");
	if(!respObj || respObj.result != true) {
		var errMsg = params.errorMsg ? params.errorMsg : "DimDim Exception";
		if(respObj.message) {
			errMsg = [errMsg, "<br/><label style='color:red;font:bold'>", respObj.message, "</label>"].join("");
		}
		this._showErrorMessage(errMsg);
		return;
	}
	callback.run(respObj);
};

/**
 * Creates/Updates a DimDim meeting
 * @param {hash}	params  a hash of parameters
 * @see	this._checkIfItsDimDimUpdate
 */
DimDimZimlet.prototype._doCreateOrUpdateMeeting = function(params, accountNumber) {
	try{
		this._loginToDimDim(accountNumber);//make sure to login (if we havent already)
		params["manuallySelectedAccount"] = accountNumber;
		var postCallback = new AjxCallback(this, this._createOrUpdateMeeting, params);
		this._getGeneralPrefsMetaData(postCallback);
	} catch(ex) {
		this._showErrorMessage(ex);
	}
};

/**
 * Saves DimDim appointment.
 *
 * @param {hash}	params  a hash of parameters
 *
 * @see	this._checkIfItsDimDimUpdate
 */
DimDimZimlet.prototype._createOrUpdateMeeting = function(params) {
	var newParams = {};
	var appt = params.appt;
	try {
		if (params.manuallySelectedAccount) {
			this._setCurrentAccntInfoFromAccntNumber(params.manuallySelectedAccount);
		} else {
			this._setCurrentAccntInfoFromCalendar(appt.folderId);
		}
	} catch(ex) {
		this._showErrorMessage(ex);
		return;
	}
	var startDate = appt.startDate;
	var startHour =  appt.startDate.getHours();

	newParams["meetingName"] = appt.name;
	newParams["attendees"] = this._getEmailsFromContacts(appt.getAttendees(ZmCalBaseItem.PERSON));
	newParams["meetingLengthMinutes"] = (appt.endDate.getTime() - appt.startDate.getTime()) / 60000;
	newParams["timezone"] = this.convertTimeZone_Zimbra2DimDim(appt.timezone);
	newParams["attendeeKey"] = this._currentDimDimAccount[DimDimZimlet.PROP_MEETING_PASSWORD.propId];
	newParams["meetingRecurrance"] = this._getRecurrenceString(appt);	
	newParams["startDate"] = this._formatDateFromAppt(appt);
	newParams["startHour"] = startHour > 11 ? startHour -12 : startHour;
	newParams["startMinute"] = (Math.ceil(appt.startDate.getMinutes() / 15) * 15) % 60
	newParams["timeAMPM"] = startHour > 11 ? "PM" : "AM";
	newParams["tollfree"] = this._currentDimDimAccount[DimDimZimlet.PROP_TOLL_FREE_PHONE_NUMBER.propId];
	newParams["toll"] = this._currentDimDimAccount[DimDimZimlet.PROP_TOLL_PHONE_NUMBER.propId];
	newParams["internationalTollNumber"] = this._currentDimDimAccount[DimDimZimlet.PROP_INTL_PHONE_NUMBER.propId];
	newParams["attendeePhonePassCode"] = this._currentDimDimAccount[DimDimZimlet.PROP_PHONE_PASSCODE.propId];
	var scheduleId = null;
	if (params.DimDimKeyData && params.DimDimKeyData.scheduleId) {
		scheduleId = params.DimDimKeyData.scheduleId;
	}
	if (appt.viewMode != ZmCalItem.MODE_EDIT_SINGLE_INSTANCE) {//if its modify single-instance, dont use the same scheduleId
		newParams["scheduleId"] = scheduleId;
	} else {
		if (scheduleId) {
			params["seriesScheduleId"] = scheduleId;//store this to params(not newParams) so we can store this
		}
	}
	var url = [this._getDimDimServerURL(),"/api/prtl/create_schedule"].join("");
	if(scheduleId) {
		url = [this._getDimDimServerURL(),"/api/prtl/edit_schedule"].join("");
	}
	var proxyUrl = [ZmZimletBase.PROXY, AjxStringUtil.urlComponentEncode(url)].join(""); 
	var jsonData = this._getCreateOrModifyMeetingRequest(newParams);
	var postParams = {jsonData: jsonData, proxyUrl:proxyUrl, errorMsg: this.getMessage("DimDimZimlet_couldNotScheduleMeeting")};
	var callback = new AjxCallback(this, this._createOrUpdateMeetingResponseHdlr, params);
	postParams.callback = callback;
	this._doPost(postParams);
};

/**
 * Saves DimDim appointment.
 *
 * @param {ZmContacts} contacts	 An Array of contacts
 */
DimDimZimlet.prototype._getEmailsFromContacts = function(contacts) {
	var emls = [];
	for (var i = 0; i < contacts.length; i++) {
		var a = contacts[i];
		var e = a.getEmail ? a.getEmail() : (a.getAddress ? a.getAddress() : "");
		if (e == "" || e == undefined) {
			continue;
		}
		emls.push(e);
	}
	return emls.join(",");
};

/**
 * Saves DimDim appointment.
 *
 * @param {ZmAppt} appt	 an appointment
 */
DimDimZimlet.prototype._getRecurrenceString = function(appt) {
	var rec = appt.getRecurrence();
	var repeatType = rec.repeatType;
	if (repeatType == "NON" || appt.viewMode == ZmCalItem.MODE_EDIT_SINGLE_INSTANCE) {//non recurring or modify instance, skip creating recurring
		return "";
	} else if (repeatType == "WEE") {
		return "WEEKLY";
	} else if (repeatType == "MON") {
		return "MONTHLY";
	} else if (repeatType == "DAI") {
		return "DAILY";
	} else if(repeatType == "YEA") {
		return "YEARLY";
	}
	return "";
};


/**
 * Create create or modify DimDim meeting request.
 *
 * @param {hash} params	a hash of parameters
 * @param {string} params.subject meeting subject
 * @param {string} params.loc meeting location
 * @param {string} params.emails meeting invitees
 * @param {string} params.duration meeting duration
 * @param {int} params.timeZoneID DimDim timeZone Id
 * @param {string} params.pwd Meeting password
 * @param {string} params.startDate Start date string
 * @param {int} params.meetingkey meeting key
 *
 * @return {string} a request string
 */
DimDimZimlet.prototype._getCreateOrModifyMeetingRequest = function(params) {
	params["groupName"] =  "all";
	params["accountName"] =   this._currentDimDimAccount[DimDimZimlet.PROP_USERNAME.propId];
	params["roomName"] = "default";
	var request = [];
	for(var el in params) {
		if(params[el] != "" && params[el] != null && params[el] != undefined && params[el] != "N/A") {
			request.push([" \"", el, "\" : \"", params[el], "\""].join(""));
		}
	}
	request =  request.join(",");
	request = ["{\"request\":{", request, "}}"].join("");
//{"request":{"groupName": "all","accountName": "raja","roomName": "default","startDate": "May 13, 2010","startHour": "12","startMinute": "0","timeAMPM": "AM","agenda": "","meetingName": "someName","attendees": "attendee1@zimbra.com,zttendee2@zimbra.com","timezone": "Asia/Calcutta","whiteboardEnabled": "false","cobrowserEnabled": "false"}}
	return request;
};

/**
 * Handles create or modify meeting response.
 *
 * @param {hash} params			a hash of parameters
 * @param {object} result		the DimDim response	
 *
 * @see this._showDeleteDimDimApptYesNoDlg
 */
DimDimZimlet.prototype._createOrUpdateMeetingResponseHdlr = function(params, respObj) {	
	params["joinMeetingUrl"] = [this._getDimDimServerURL(),"/all/",this._currentDimDimAccount[DimDimZimlet.PROP_USERNAME.propId],"/default/?clientId=zimbra"].join("");
	params["scheduleId"] = respObj.response.scheduleId;
	this._params = params;
	this._appt = params.appt;
	this._updateMeetingBodyAndSave(params);
};

/**
 * Called by framework.
 */
DimDimZimlet.prototype.onSaveApptSuccess = function(controller, calItem, result) {
	if (!this._params) {
		return;
	}
	var match = calItem.name.indexOf(this._appt.name);
	if (this._appt.name != calItem.name && (match == -1 || match > 0)) {
		return;
	}

	this._saveApptIdsHashToServer(result.apptId, this._params.scheduleId, this._params.seriesScheduleId);
	this._params = null;//make sure to set this to null
};

/**
 * Appends meeting body and saves meeting.
 *
 * @param {hash} params		 a hash of parameters
 *
 * @see this._showDeleteDimDimApptYesNoDlg
 */
DimDimZimlet.prototype._updateMeetingBodyAndSave = function(params) {
	var composeView = params.apptComposeView;
	var editorType = "HTML";
	if (composeView.getComposeMode() != "text/html") {
		editorType = "PLAIN_TEXT";
	}
	var DimDimBodyStr = this._getDimDimBodyString(params.joinMeetingUrl, editorType);

	var currentContent = composeView.getHtmlEditor().getContent();
	if (editorType == "HTML") {
		var lastIndx = currentContent.lastIndexOf("</body></html>");
		var tmp = currentContent.substr(0, lastIndx);
		var newContent = [tmp, DimDimBodyStr, "</body></html>"].join("");
	} else {
		var newContent = [currentContent, DimDimBodyStr].join("");
	}
	if (this._appendToSubjectOrLocation("SUBJECT")) {
		var additionalStr = this._getAddionalStringToAppend("SUBJECT");
		if (additionalStr != "") {
			var subjectField = composeView._apptEditView._subjectField;
			var newVal = [subjectField.getValue()," [", additionalStr,"]"].join("");
			subjectField.setValue(newVal);
		}
	}
	if (this._appendToSubjectOrLocation("LOCATION")) {
		var additionalStr = this._getAddionalStringToAppend("LOCATION");
		if (additionalStr != "") {
			var locationField = composeView._apptEditView._attInputField[ZmCalBaseItem.LOCATION];
			var newVal = [locationField.getValue()," [", additionalStr,"]"].join("");
			locationField.setValue(newVal);
		}
	}
	composeView.getHtmlEditor().setContent(newContent);
	params.apptController._saveListener();
	appCtxt.getAppController().setStatusMsg(this.getMessage("DimDimZimlet_successfullyCreatedDimDim"), ZmStatusView.LEVEL_INFO);
};

/**
 * Returns true if we need to append metting info to Subject or location
 * @param {string} type LOCATION or SUBJECT
 * @returns <code>true</code> if we need to append metting info to Subject or location
 */
DimDimZimlet.prototype._appendToSubjectOrLocation = function(type) {
	if (!this._DimDimZimletGeneralPreferences) {
		false;
	}
	for (var el in this._DimDimZimletGeneralPreferences) {
		if (this._DimDimZimletGeneralPreferences[el] == "BOTH" || this._DimDimZimletGeneralPreferences[el] == type) {
			return true;
		}
	}
	return false;
};

/**
 * Gets meeting informations string to be appended to location or subject.
 *
 * @param {string} type  LOCATION or SUBJECT
 */
DimDimZimlet.prototype._getAddionalStringToAppend = function(type) {
	if (this._DimDimZimletGeneralPreferences == undefined) {
		return "";
	}
	var str = [];
	//	var currentVal = this._DimDimZimletGeneralPreferences[DimDimZimlet.PROP_APPEND_DimDim_URL.propId];
	//	if (currentVal == "BOTH" || currentVal == type) {
	//		str.push([this.getMessage("DimDimZimlet_DimDimUrl")," ", joinMeetingUrl].join(""));
	//	}

	var currentVal = this._DimDimZimletGeneralPreferences[DimDimZimlet.PROP_APPEND_DimDim_MEETING_PWD.propId];
	if (currentVal == "BOTH" || currentVal == type) {
		str.push([this.getMessage("DimDimZimlet_meetingPwd")," ", this._currentDimDimAccount[DimDimZimlet.PROP_MEETING_PASSWORD.propId]].join(""));
	}

	currentVal = this._DimDimZimletGeneralPreferences[DimDimZimlet.PROP_APPEND_TOLL_FREE_PHONE_NUMBER.propId];
	if (currentVal == "BOTH" || currentVal == type) {
		str.push([this.getMessage("DimDimZimlet_tollFreeNumber")," ", this._currentDimDimAccount[DimDimZimlet.PROP_TOLL_FREE_PHONE_NUMBER.propId]].join(""));
	}

	currentVal = this._DimDimZimletGeneralPreferences[DimDimZimlet.PROP_APPEND_TOLL_PHONE_NUMBER.propId];
	if (currentVal == "BOTH" || currentVal == type) {
		str.push([this.getMessage("DimDimZimlet_tollNumber")," ", this._currentDimDimAccount[DimDimZimlet.PROP_TOLL_PHONE_NUMBER.propId]].join(""));
	}

	currentVal = this._DimDimZimletGeneralPreferences[DimDimZimlet.PROP_APPEND_PHONE_PASSCODE.propId];
	if (currentVal == "BOTH" || currentVal == type) {
		str.push([this.getMessage("DimDimZimlet_phonePasscode"), " ", this._currentDimDimAccount[DimDimZimlet.PROP_PHONE_PASSCODE.propId]].join(""));
	}
	return str.join(", ");
};

/**
 * Gets the DimDim body string to append.
 *
 * @param {string} joinMeetingUrl		the DimDim join meeting url
 * @param {string} editorType  "HTML" | "PLAIN_TEXT" | "FIELD" (Field is for edit-fields)
 * @param {boolean} telephoneOnly		 if <code>true</code>, then only telephone information is returned
 * @param {boolean} noHeader		if <code>true</code>, then html for header is ignored
 */
DimDimZimlet.prototype._getDimDimBodyString = function(joinMeetingUrl, editorType, telephoneOnly, noHeader) {
	var html = [];
	html.push(noHeader ? "" : this._getMeetingDetailshdr("Teleconference Details:", editorType));
	if (editorType == "HTML") {
		html.push("<table cellpadding='0' cellspacing='0' border='0' width=94% align=center><tr><td>");
		html.push("<div style='border-bottom: 1px solid #6E6E6E; border-right: 1px solid #6E6E6E; border-left: 1px solid #CECECE;'>");
		html.push("<table cellpadding='4' width=100% cellspacing='0'>");
	}
	var isRowOdd = true;
	for (var i = 0; i < DimDimZimlet.DimDim_TELECONF_PROPS.length; i++) {
		var obj = DimDimZimlet.DimDim_TELECONF_PROPS[i];
		if (i == DimDimZimlet.DimDim_TELECONF_PROPS.length - 1) {//dont add delimiter for last item
			html.push(this._getMeetingDetailsRow(this.getMessage(obj.label), this._currentDimDimAccount[obj.propId], editorType, true, isRowOdd));
		} else {
			html.push(this._getMeetingDetailsRow(this.getMessage(obj.label), this._currentDimDimAccount[obj.propId], editorType, false, isRowOdd));
		}
		isRowOdd = !isRowOdd;
	}
	if (editorType == "HTML") {
		html.push("</table>");
		html.push("</div>");
		html.push("</td></tr></table>");
	}
	var telephoneStr = html.join("");
	if (telephoneOnly) {
		return telephoneStr;
	}

	var pwd = this._currentDimDimAccount[DimDimZimlet.PROP_MEETING_PASSWORD.propId];
	if (pwd == "" || pwd == "N/A") {
		pwd = this.getMessage("DimDimZimlet_passwordNotRequired");
	}
	var html = [];
	html.push(noHeader ? "" : this._getMeetingDetailshdr("DimDim  Details:", editorType));
	if (editorType == "HTML") {
		html.push("<table cellpadding='0' cellspacing='0' border='0' width=94% align=center><tr><td>");
		html.push("<div style='border-bottom: 1px solid #6E6E6E; border-right: 1px solid #6E6E6E; border-left: 1px solid #CECECE;'>");
		html.push("<table cellpadding='4' width=100% cellspacing='0'>");
	}
	html.push(this._getMeetingDetailsRow(this.getMessage("DimDimZimlet_DimDimUrl"), joinMeetingUrl, editorType, false, true));
	html.push(this._getMeetingDetailsRow(this.getMessage("DimDimZimlet_userName"), this.getMessage("DimDimZimlet_enterYourName"), editorType, false, false));
	html.push(this._getMeetingDetailsRow(this.getMessage("DimDimZimlet_email"), this.getMessage("DimDimZimlet_enterYourEmail"), editorType, false, true));
	html.push(this._getMeetingDetailsRow(this.getMessage("DimDimZimlet_meetingPwd"), pwd, editorType, true, false));
	if (editorType == "HTML") {
		html.push("</table>");
		html.push("</div>");
		html.push("</td></tr></table>");
	}
	var DimDimStr = html.join("");

	return  DimDimStr + telephoneStr;
};

/**
 * Gets meeting html row.
 *
 * @param {string} name		 the name/label of the item
 * @param {string} val		  the value string
 * @param {string} editorType	 the type of the editor
 * @param {boolean} noDelimiter	if <code>true</code>, delimiter is no appended
 * @param {boolean} isRowOdd	if <code>true</code>, colors the row
 */
DimDimZimlet.prototype._getMeetingDetailsRow = function(name, val, editorType, noDelimiter, isRowOdd) {
	if (val == "") {//dont return empty rows
		return;
	}
	var txtSeperator = "";
	if (editorType == "PLAIN_TEXT") {
		txtSeperator = "\n";
	} else if (editorType == "FIELD") {//for location/subject/DimDim OTHER field
		txtSeperator = " | ";
	}
	if (editorType == "HTML") {
		val = val.replace(/\n/g, "<br/>");//make sure to replace newLine to br
		var rStyle = !isRowOdd ? " style='background-color:#FBF9F4' " : " style='background-color:#FEFDFC' ";
		return ["<tr ",rStyle,"><td width=20%><b>",name,"</b> </td></td> ", val, "</span></td></tr>"].join("");
	} else {
		if (noDelimiter) {
			return [name, val].join("");
		} else {
			val = val.replace(/\n/g, txtSeperator);//for other-phones, you get bunch of phones with /n
			return [name, " ",val, txtSeperator].join("");
		}
	}
};
/**
 * Gets meeting  section header
 *
 * @param {string} hdrName	 the name of the section header
 * @param {string} editorType	the editor type
 */
DimDimZimlet.prototype._getMeetingDetailshdr = function(hdrName, editorType) {
	if (editorType == "HTML") {
		return	["<br/>",
			"<table cellpadding='0' cellspacing='0' border='0' width=94% align=center><tr><td>",
			"<div style='background: #808080; border-bottom: 1px solid #6E6E6E;border-right: 1px solid #6E6E6E;font-weight:bold;color:white;padding:2px;'>",
			"<b>",hdrName,"</b>",
			"</div>", "</tr></td></table>"].join("");
	} else {
		return	["\n--------------------------------------------------------------------",
			,"\n",hdrName,
			"\n--------------------------------------------------------------------\n"].join("");
	}
};

/**
 * Formats the date.
 *
 * @param	{ZmAppt}	appt An appointment
 */
DimDimZimlet.prototype._formatDateFromAppt = function(appt) {
	var d = appt.startDate;
	return this._formatDate(d);
};

/**
 * Formats the date.
 *
 * @param	{object}	dateObj A Date object
 */
DimDimZimlet.prototype._formatDate = function(dateObj) {
	var date = new Date();
	date.setMonth(dateObj.getMonth());
	date.setDate(dateObj.getDate());
	date.setYear(dateObj.getFullYear());
	date.setHours(parseInt(dateObj.getHours()));
	date.setMinutes((Math.ceil(dateObj.getMinutes() / 15) * 15) % 60);
	date.setSeconds(0);
	date.setMilliseconds(0);

	var month = AjxDateUtil.MONTH_MEDIUM[date.getMonth()];
	var year = date.getFullYear();
	var day = date.getDate();
	return [month, " ",day,", ", year].join(""); 
};

/**
 * Asks user to right-click for more option.
 *
 */
DimDimZimlet.prototype.singleClicked = function() {
	var dlg = appCtxt.getMsgDialog();
	dlg.reset();//reset dialog since we could be using it
	dlg.setMessage(this.getMessage("DimDimZimlet_rightClickForOptions"), DwtMessageDialog.INFO_STYLE);
	dlg.popup();
};

/**
 * Calls singleClicked when doubleClicked on panel item.
 *
 */
DimDimZimlet.prototype.doubleClicked = function() {
	this.singleClicked();
};

/**
 * Called by the Zimbra framework when a menu item is selected
 * dispatch the call, ensuring the DimDim configuration is set.
 *
 */
DimDimZimlet.prototype.menuItemSelected = function(itemId) {

	switch (itemId) {
		case "ACCOUNT_PREFERENCES":
			this._displayAccntPrefsDialog();
			break;
		case "GENERAL_PREFERENCES":
			this._displayGeneralPrefsDlg();
			break;
		case "START_JOIN_MEETING":
			var postCallback = new AjxCallback(this, this._showAppointmentsList);
			this._showSelectAccountDlg(postCallback);
			break;
		case "START_QUICK_MEETING":
			this._showOneClickDlg();
			break;
	}
};

/**
 * Displays Account preferences dialog.
 */
DimDimZimlet.prototype._displayAccntPrefsDialog =
function() {
	if (this.accPrefsDlg) {
		this._addAccntPrefsTabControl();
		this.accPrefsDlg.popup();
		return;
	}
	this._accPrefsDlgView = new DwtComposite(this.getShell());
	this._accPrefsDlgView.setSize("530", "400");
	//this._accPrefsDlgView.getHtmlElement().style.background = "white";
	this._accPrefsDlgView.getHtmlElement().style.overflow = "auto";
	this._accPrefsDlgView.getHtmlElement().innerHTML = this._createAccPrefsView();
	this.accPrefsDlg = new ZmDialog({parent:this.getShell(), title:this.getMessage("DimDimZimlet_manageUpto5Accnts"), view:this._accPrefsDlgView, standardButtons:[DwtDialog.OK_BUTTON, DwtDialog.CANCEL_BUTTON]});
	this.accPrefsDlg.setButtonListener(DwtDialog.OK_BUTTON, new AjxListener(this, this._accPrefsOkBtnListner));
	this._addTestAccountButtons();
	this._addHelpLinkListeners();
	var postCallback = new AjxCallback(this, this._setDataToAccPrefsDlg);
	this._getAccPrefsMetaData(postCallback);
};

/**
 * Adds tab control for Account Preferences' fields
 */
DimDimZimlet.prototype._addAccntPrefsTabControl =
function() {
	this.accPrefsDlg._tabGroup.removeAllMembers();
	for (var i = 0; i < this._accntPrefsObjsHash.length; i++) {
		var obj = document.getElementById(this._accntPrefsObjsHash[i]);
		if (obj) {
			this.accPrefsDlg._tabGroup.addMember(obj);
		}
	}
	document.getElementById(this._accntPrefsObjsHash[0]).focus();
};

/**
 * Adds listeners to "help" links
 */
DimDimZimlet.prototype._addHelpLinkListeners =
function() {
	for(var i =0; i < this._accntPrefsHelpObjsHash.length; i++) {
		var obj = this._accntPrefsHelpObjsHash[i];
		document.getElementById(obj.helpLinkId).onclick = AjxCallback.simpleClosure(this._accPrefsShowHelpdialog, this, obj.propId);
	}
};

/**
 * Shows a message dialog with help
 */
DimDimZimlet.prototype._accPrefsShowHelpdialog =
function(propId) {
	var msg = "";
	if(propId == DimDimZimlet.PROP_COMPANY_ID.propId) {
		var msg = this.getMessage("DimDimZimlet_companyIdExample");
	}
	var dlg = appCtxt.getMsgDialog();
	dlg.reset();//reset dialog since we could be using it
	dlg.setMessage(msg, DwtMessageDialog.INFO_STYLE);
	dlg.popup();
};

/**
 * Adds test DimDim account buttons.
 */
DimDimZimlet.prototype._addTestAccountButtons =
function() {
	for (var i = 1; i < 6; i++) {
		var btn = new DwtButton({parent:this.getShell()});
		btn.setText(AjxMessageFormat.format(this.getMessage("DimDimZimlet_validateDimDimAccntNumber"), i));
		btn.setImage("DimDim-panelIcon");
		btn.addSelectionListener(new AjxListener(this, this._testDimDimAccount, [i]));
		document.getElementById("DimDimZimlet_TestAccountBtn" + i).appendChild(btn.getHtmlElement());
	}
};

/**
 * Gets Account preferences meta data.
 *
 * @param {AjxCallback} postCallback  a callback
 */
DimDimZimlet.prototype._getAccPrefsMetaData =
function(postCallback) {
	this.metaData.get("DimDimZimletAccountPreferences", null, new AjxCallback(this, this._handleGetAccPrefsMetaData, postCallback));
};

/**
 * Checks if at least one Account is configured
 *
 * @return {AjxException} If none, it throws exception
 */
DimDimZimlet.prototype._isAtLeastOneAccountConfigured = function() {
	if (this._DimDimZimletAccountPreferences == undefined) {
		throw new AjxException(this.getMessage("DimDimZimlet_noDimDimAccount"), AjxException.INTERNAL_ERROR, this.getMessage("DimDimZimlet_label"));
	}
	for(var i = 1; i < 6; i++) {
		if(this._DimDimZimletAccountPreferences[DimDimZimlet.PROP_USERNAME.propId + i] != "" && 
			this._DimDimZimletAccountPreferences[DimDimZimlet.PROP_USERNAME.propId + i] != "N/A"
			&& this._DimDimZimletAccountPreferences[DimDimZimlet.PROP_PASSWORD.propId + i] != "" && 
			this._DimDimZimletAccountPreferences[DimDimZimlet.PROP_PASSWORD.propId + i] != "N/A"
			&& this._DimDimZimletAccountPreferences[DimDimZimlet.PROP_COMPANY_ID.propId + i] != "" && 
			this._DimDimZimletAccountPreferences[DimDimZimlet.PROP_COMPANY_ID.propId + i] != "N/A") {
			return true;
		}
	}
	throw new AjxException(this.getMessage("DimDimZimlet_noDimDimAccount"), AjxException.INTERNAL_ERROR, this.getMessage("DimDimZimlet_label"));
};

/**
 * Handles Account preferences metadata callback.
 *
 * @param {AjxCallback} postCallback  a callback
 * @param {object} result	 the response
 */
DimDimZimlet.prototype._handleGetAccPrefsMetaData =
function(postCallback, result) {
	this._DimDimZimletAccountPreferences = null; //nullify old data
	try {
		this._DimDimZimletAccountPreferences = result.getResponse().BatchResponse.GetMailboxMetadataResponse[0].meta[0]._attrs;
		if (postCallback) {
			postCallback.run(this);
		} else {
			return this._DimDimZimletAccountPreferences;
		}
	} catch(ex) {
		this._showErrorMessage(ex);
	}
};

/**
 * Sets current account information based on associated calendar.
 *
 * @param {string} id Calendar id
 */
DimDimZimlet.prototype._setCurrentAccntInfoFromCalendar = function(id) {
	var accntNumber = this._getAccountNumberFromFolderId(id);
	this._currentDimDimAccount = [];
	for (var i = 0; i < DimDimZimlet.ALL_ACCNT_PROPS.length; i++) {
		var prop = DimDimZimlet.ALL_ACCNT_PROPS[i].propId;
		this._currentDimDimAccount[prop] = this._DimDimZimletAccountPreferences[prop + accntNumber];
	}
	this._currentDimDimAccountNumber = accntNumber;
	this._validateCurrentAccount(accntNumber);
};

/**
 * Gets the account number based on associated calendar.
 *
 * @param {string} id Calendar id
 * @param {int} Account number
 */
DimDimZimlet.prototype._getAccountNumberFromFolderId = function(id) {
	this._isAtLeastOneAccountConfigured();
	var accntNumber = 1;
	for (var i = 1; i < 6; i++) {
		if (this._DimDimZimletAccountPreferences[DimDimZimlet.PROP_ASSOCIATED_CALENDAR.propId + i] == id) {
			accntNumber = i;
			break;
		}
	}
	return accntNumber;
};

/**
 * Sets an account active based on account number.
 *
 * @param {number} accntNumber	the account number
 */
DimDimZimlet.prototype._setCurrentAccntInfoFromAccntNumber = function(accntNumber) {
	this._isAtLeastOneAccountConfigured();
	this._currentDimDimAccount = [];
	for (var i = 0; i < DimDimZimlet.ALL_ACCNT_PROPS.length; i++) {
		var prop = DimDimZimlet.ALL_ACCNT_PROPS[i].propId;
		this._currentDimDimAccount[prop] = this._DimDimZimletAccountPreferences[prop + accntNumber];
	}
	this._validateCurrentAccount(accntNumber);
	this._currentDimDimAccountNumber = accntNumber;
};

/**
 * validates current account.
 *
 * @param {number} accntNumber	the account number
 */
DimDimZimlet.prototype._validateCurrentAccount = function(accntNumber) {
	var userName = this._currentDimDimAccount[DimDimZimlet.PROP_USERNAME.propId];
	var pwd = this._currentDimDimAccount[DimDimZimlet.PROP_PASSWORD.propId];
	var cId = this._currentDimDimAccount[DimDimZimlet.PROP_COMPANY_ID.propId];
	if (userName == "" || userName == "N/A" || pwd == "" || pwd == "N/A") {
		var label = this.getMessage("DimDimZimlet_accountNumberNotSetup").replace("{0}", accntNumber);
		throw new AjxException(label, AjxException.INTERNAL_ERROR, "_validateCurrentAccount");
	}
};

/**
 * Sets data to account preferences.
 *
 */
DimDimZimlet.prototype._setDataToAccPrefsDlg =
function() {
	try {
		var useDefaultVals = false;
		if (this._DimDimZimletAccountPreferences == undefined) {
			useDefaultVals = true;
		}
		for (var indx = 1; indx < 6; indx++) {
			for (var i = 0; i < DimDimZimlet.ALL_ACCNT_PROPS.length; i++) {
				var objId = DimDimZimlet.ALL_ACCNT_PROPS[i].propId;
				var key = objId + indx;

				var val;
				if (useDefaultVals) {
					val = DimDimZimlet.ALL_ACCNT_PROPS[i].defaultVal;
				} else {
					val = this._DimDimZimletAccountPreferences[key];
				}
				if (val == "N/A" || val == "" || val == undefined || val == "undefined") {
					continue;
				}
				if (objId.indexOf(DimDimZimlet.PROP_ASSOCIATED_CALENDAR.propId) == -1) {
					document.getElementById(key).value = val;
				} else {
					this._setMenuValue(key, val);
				}
			}
		}
		this.accPrefsDlg.popup();
		this._addAccntPrefsTabControl();
	} catch(ex) {
		this._showErrorMessage(ex);
		return;
	}
};

/**
 * Sets html select value.
 *
 * @param {string} menuId Menu id
 * @param {string} val Menu value to match and select
 */
DimDimZimlet.prototype._setMenuValue =
function(menuId, val) {
	var optn = document.getElementById(menuId).options;
	for (var i = 0; i < optn.length; i++) {
		if (optn[i].value == val) {
			optn[i].selected = true;
			break;
		}
	}
};

/**
 * Creates Account preferences view
 */
DimDimZimlet.prototype._createAccPrefsView =
function() {
	this._accntPrefsObjsHash = [];
	this._accntPrefsHelpObjsHash = [];
	var html = [];
	if (!AjxEnv.isIE) {//w/o this, stupid IE will break
		html.push("<table width=96% align=center><tr><td>");
	}
	for (var indx = 1; indx < 6; indx++) {
		if (indx == 1) {
			var notes = this.getMessage("DimDimZimlet_account1UsedAsDefaultForUnAssociatedCal");
		} else {
			var notes = "";
		}
		html.push(this._getAccountPrefsHtml(indx, notes));
	}
	if (!AjxEnv.isIE) {//w/o this, stupid IE will break
		html.push("</td></tr></table>");
	}
	return html.join("");
};

/**
 * Get account preferences dialog html.
 *
 * @param {string} indx	  the account number
 * @param {string} notes	 the string with some additional notes
 */
DimDimZimlet.prototype._getAccountPrefsHtml =
function(indx, notes) {
	var html = [];
	var j = 0;
	var prefLabel = this.getMessage("DimDimZimlet_accountNumber").replace("{0}", indx);
	html.push("<div class='DimDimZimlet_YellowBold '>");
	html.push("<div class='DimDimZimlet_grayAccntHdr'>", prefLabel, "</div>")
	html.push("<div class='DimDimZimlet_lightGray'>", this.getMessage("DimDimZimlet_accountSettings"), "</div>");

	html.push("<table class='DimDimZimlet_table'>");
	for (var i = 0; i < DimDimZimlet.SINGLE_DimDim_ACCNT_PROPS.length; i++) {
		var obj = DimDimZimlet.SINGLE_DimDim_ACCNT_PROPS[i];
		var type = obj.objType ? obj.objType : "text";
		var id = [obj.propId , indx].join("");
		this._accntPrefsObjsHash.push(id);
		if (obj.propId == DimDimZimlet.PROP_COMPANY_ID.propId) {
			var helpLinkId = Dwt.getNextId();
			this._accntPrefsHelpObjsHash.push({helpLinkId:helpLinkId, propId:obj.propId});
			html.push("<tr><td>", this.getMessage(obj.label), "</td><td><input id='", id, "'  type='", type, "'/>",
				"&nbsp; <a href=# id='",helpLinkId,"' style='color:darkBlue;text-decoration:underline'>help</a></td></tr>");
		} else {
			html.push("<tr><td>", this.getMessage(obj.label), "</td><td><input id='", id, "'  type='", type, "'/>",
				"<label style='color:gray'>", this.getMessage(obj.extraLabel), "</label></td></tr>");
		}
	}

	html.push("<tr><td></td><td id='DimDimZimlet_TestAccountBtn", indx, "' ></td></tr>");
	html.push("</table>");

	html.push("<br/><div class='DimDimZimlet_lightGray'>", this.getMessage("DimDimZimlet_teleConfSettings"), "</div>");
	html.push("<table class='DimDimZimlet_table'>");
	for (var i = 0; i < DimDimZimlet.DimDim_TELECONF_PROPS.length; i++) {
		var obj = DimDimZimlet.DimDim_TELECONF_PROPS[i];
		var id = [obj.propId , indx].join("");
		this._accntPrefsObjsHash.push(id);
		html.push("<tr><td>", this.getMessage(obj.label), "</td><td><input id='", id,"' ></input>", 
				"<label style='color:gray'>", this.getMessage(obj.extraLabel), "</label></td></tr>");		
	}
	html.push("</table>");

	html.push("<br/><div class='DimDimZimlet_lightGray'>", this.getMessage("DimDimZimlet_associateCalendarHdr"), "</div>");
	var obj = DimDimZimlet.PROP_ASSOCIATED_CALENDAR;
	html.push("<div style='padding:5px'><label style='color:blue;font-weight:bold'>", this.getMessage(obj.label),
			"</label><label style='color:gray'>", this._getCalendarFoldersList(indx), "<label style='color:gray'>", this.getMessage(obj.extraLabel),
			"</label></div></div><br/><br/>");

	return html.join("");
};

/**
 * Tests DimDim account
 *
 * @param {number} indx	the account number
 */
DimDimZimlet.prototype._testDimDimAccount =
function(indx) {
	var userName = document.getElementById(DimDimZimlet.PROP_USERNAME.propId + indx).value;
	var pwd = document.getElementById(DimDimZimlet.PROP_PASSWORD.propId + indx).value;
	var cId = document.getElementById(DimDimZimlet.PROP_COMPANY_ID.propId + indx).value;
  	if(userName.length == 0 || pwd.length == 0 || (cId.length == 0 && cId != "zimlet.synchrolive.com")) {//synchrolive is dimdim's test server
		appCtxt.getAppController().setStatusMsg(this.getMessage("DimDimZimlet_userPwdCIdRequired"), ZmStatusView.LEVEL_WARNING);
		return;
	}
	appCtxt.getAppController().setStatusMsg(this.getMessage("DimDimZimlet_pleaseWait"), ZmStatusView.LEVEL_INFO);

	var url = [this._getDimDimServerURL(cId),"/api/auth/login"].join("");
	var proxyUrl = [ZmZimletBase.PROXY, AjxStringUtil.urlComponentEncode(url)].join(""); 
	var jsonData = ["{\"request\":{\"account\": \"", AjxStringUtil.urlComponentEncode(userName),"\",\"password\": \"", AjxStringUtil.urlComponentEncode(pwd),
		"\",\"group\": \"all\"}}"].join("");

	var postParams = {jsonData: jsonData, proxyUrl:proxyUrl, errorMsg: this.getMessage("DimDimZimlet_testFailed")};
	var callback = new AjxCallback(this, this._testValidAccountSuccessful);
	postParams.callback = callback;
	this._doPost(postParams);
};

DimDimZimlet.prototype._testValidAccountSuccessful =
function() {
	var dlg = appCtxt.getMsgDialog();
	dlg.reset();//reset dialog since we could be using it
	dlg.setMessage(this.getMessage("DimDimZimlet_testPassed"));
	dlg.popup();
};

/**
 * Listener to Account Preferences dialog.
 */
DimDimZimlet.prototype._accPrefsOkBtnListner =
function() {
	var keyValArray = [];
	for (var indx = 1; indx < 6; indx++) {
		for (var i = 0; i < DimDimZimlet.ALL_ACCNT_PROPS.length; i++) {
			var key = DimDimZimlet.ALL_ACCNT_PROPS[i].propId + indx;
			var val = document.getElementById(key).value;
			val = val == "" ? "N/A" : val;
			keyValArray[key] = val;
		}
	}
	this.metaData.set("DimDimZimletAccountPreferences", keyValArray, null, new AjxCallback(this, this._saveAccPrefsHandler));
};

/**
 * Saves Account preferences.
 */
DimDimZimlet.prototype._saveAccPrefsHandler =
function() {
	this._DimDimZimletAccountPreferences = null;//nullify
	appCtxt.getAppController().setStatusMsg(this.getMessage("DimDimZimlet_PrefSaved"), ZmStatusView.LEVEL_INFO);
	this.accPrefsDlg.popdown();
};

/**
 * Gets html-select containing all calendars.
 *
 * @param {number} idNumber	the account number
 * @return {string} Html-Select
 */
DimDimZimlet.prototype._getCalendarFoldersList =
function(idNumber) {
	var id = [DimDimZimlet.PROP_ASSOCIATED_CALENDAR.propId,idNumber].join("");
	this._accntPrefsObjsHash.push(id);
	var j = 0;
	var html = new Array();

	html[j++] = ["<SELECT id='",id,"'>"].join("");
	html[j++] = this._getCalendarsOptionsHtml();
	html[j++] = "</SELECT>";
	return html.join("");
};

/**
 * Gets Calendar appointment html.
 *
 * @return {string}	the calendar options html
 */
DimDimZimlet.prototype._getCalendarsOptionsHtml =
function() {
	if (this._calendarOptionsHtml) {
		return this._calendarOptionsHtml;
	}
	var j = 0;
	var html = new Array();

	var soapDoc = AjxSoapDoc.create("GetFolderRequest", "urn:zimbraMail");
	var folderNode = soapDoc.set("folder");
	folderNode.setAttribute("l", appCtxt.getFolderTree().root.id);

	var command = new ZmCsfeCommand();
	var top = command.invoke({soapDoc: soapDoc}).Body.GetFolderResponse.folder[0];
	var arry1 = top.folder ? top.folder : [];
	var arry2 = top.link ? top.link : [];
	var folders = arry1.concat(arry2);
	if (folders) {
		html[j++] = ["<option value='ALL'>",this.getMessage("DimDimZimlet_allCalendars"),"</option>"].join("");
		for (var i = 0; i < folders.length; i++) {
			var f = folders[i];
			if (f && f.view == "appointment") {
				var id = f.id;
				if(f.zid && f.rid) {
					id = [f.zid,":",f.rid].join("");
				}
				html[j++] = ["<option value='", id,"'>", f.name , "</option>"].join("");

			}
		}
	}
	this._calendarOptionsHtml = html.join("");
	return this._calendarOptionsHtml;
};

/**
 * Display general preferences dialog.
 *
 */
DimDimZimlet.prototype._displayGeneralPrefsDlg =
function() {
	if (this.generalPrefsDlg) {
		this.generalPrefsDlg.popup();
		return;
	}
	var view = new DwtComposite(this.getShell());
	view.getHtmlElement().style.overflow = "auto";
	view.getHtmlElement().innerHTML = this._createGeneralPrefsView();
	//this.pView.setSize("510", "350");
	//this.pView.getHtmlElement().style.background = "white";
	this.generalPrefsDlg = new ZmDialog({parent: this.getShell(), title:this.getMessage("DimDimZimlet_generalPreferences"), view:view, standardButtons:[DwtDialog.OK_BUTTON, DwtDialog.CANCEL_BUTTON]});
	this.generalPrefsDlg.setButtonListener(DwtDialog.OK_BUTTON, new AjxListener(this, this._generalPrefsOkBtnListner));
	var postCallback = new AjxCallback(this, this._setDataToGeneralPrefsDlg);
	this._getGeneralPrefsMetaData(postCallback);
};

/**
 * Gets General metadata.
 *
 * @param {AjxCallback} postCallback A callback
 */
DimDimZimlet.prototype._getGeneralPrefsMetaData =
function(postCallback) {
	this.metaData.get("DimDimZimletGeneralPreferences", null, new AjxCallback(this, this._handleGetGeneralPrefsMetaData, postCallback));
};

/**
 * Sets default general preferences.
 */
DimDimZimlet.prototype._setDefaultGeneralPreferences = function() {
	this._DimDimZimletGeneralPreferences = {};
	for (var i = 0; i < DimDimZimlet.ALL_GENERAL_PROPS.length; i++) {
		var obj = DimDimZimlet.ALL_GENERAL_PROPS[i];
		this._DimDimZimletGeneralPreferences[obj.propId] = obj.defaultVal;
	}
};

/**
 * Handles getMetadata response for general preferences.
 *
 * @param {AjxCallback} postCallback A Callback
 * @param {object} result	 a response
 */
DimDimZimlet.prototype._handleGetGeneralPrefsMetaData =
function(postCallback, result) {
	try {
		this._DimDimZimletGeneralPreferences = null;//nullify
		var response = result.getResponse().BatchResponse.GetMailboxMetadataResponse[0];
		if (response.meta && response.meta[0]) {
			this._DimDimZimletGeneralPreferences = response.meta[0]._attrs;
		}
		if (this._DimDimZimletGeneralPreferences == undefined) {
			this._setDefaultGeneralPreferences();
		}
		if (postCallback) {
			postCallback.run(this);
		} else {
			return this._DimDimZimletGeneralPreferences;
		}
	} catch(ex) {
		this._showErrorMessage(ex);
		return;
	}
};

/**
 * Sets default data to general preferences dialog.
 *
 */
DimDimZimlet.prototype._setDataToGeneralPrefsDlg =
function() {
	try {
		var useDefaultVals = false;
		if (this._DimDimZimletGeneralPreferences == undefined) {
			var useDefaultVals = true;
		}
		for (var i = 0; i < DimDimZimlet.ALL_GENERAL_PROPS.length; i++) {
			var key = DimDimZimlet.ALL_GENERAL_PROPS[i].propId;
			var val;
			if (useDefaultVals) {
				val = DimDimZimlet.ALL_GENERAL_PROPS[i].defaultVal;
			} else {
				val = this._DimDimZimletGeneralPreferences[key];
			}
			if (val == "N/A") {
				continue;
			}
			var optn = document.getElementById(key).options;
			for (var n = 0; n < optn.length; n++) {
				if (optn[n].value == val || optn[n].text == val) {
					optn[n].selected = true;
					break;
				}
			}
		}

		this.generalPrefsDlg.popup();
	} catch(ex) {
		this._showErrorMessage(ex);
	}
};

/**
 * Creates Zimlets Preferences view.
 *
 * @returns {string} html
 */
DimDimZimlet.prototype._createGeneralPrefsView =
function() {
	var html = [];
	html.push("<div class='DimDimZimlet_YellowBold '>");
	html.push("<div class='DimDimZimlet_gray'>", this.getMessage("DimDimZimlet_appendMeetingInfoToSubOrLoc"), "</div>");
	html.push("<table cellpadding=0 cellspacing=4>");
	for (var i = 0; i < DimDimZimlet.APPEND_SUB_OPTIONS.length; i++) {
		var obj = DimDimZimlet.APPEND_SUB_OPTIONS[i];
		html.push("<tr><td>", this.getMessage(obj.label), "</td><td>", this._getAppendSelectListMenuHtml(obj.propId), "</td></tr>");
	}
	html.push("</table>");
	html.push("<br/>");
	return html.join("");
};

/**
 * Gets html select menu.
 *
 * @param {number} id		the account number
 * @return {string} html
 */
DimDimZimlet.prototype._getAppendSelectListMenuHtml = function(id) {
	var html = [];
	html.push("<select id='", id, "'>");
	html.push("<option value='NONE'>", this.getMessage("DimDimZimlet_none"), "</option>");
	html.push("<option value='SUBJECT'>", this.getMessage("DimDimZimlet_subject"), "</option>");
	html.push("<option value='LOCATION'>", this.getMessage("DimDimZimlet_location"), "</option>");
	html.push("<option value='BOTH'>", this.getMessage("DimDimZimlet_subjectAndLocation"), "</option>");

	html.push("</select>");
	return html.join("");
};

/**
 * Handles OK button in General Preferences dialog and saves General Preferences.
 *
 */
DimDimZimlet.prototype._generalPrefsOkBtnListner =
function() {
	var keyValArray = [];
	try {
		for (var i = 0; i < DimDimZimlet.ALL_GENERAL_PROPS.length; i++) {
			var key = DimDimZimlet.ALL_GENERAL_PROPS[i].propId;
			var lst = document.getElementById(key);
			var val = lst.options[lst.selectedIndex].value;

			val = val == "" ? "N/A" : val;
			keyValArray[key] = val;
		}
		this.metaData.set("DimDimZimletGeneralPreferences", keyValArray, null, new AjxCallback(this, this._saveGeneralPrefsHandler));
	} catch(ex) {
		this._showErrorMessage(ex);
	}
};

/**
 * Saves General preferences.
 */
DimDimZimlet.prototype._saveGeneralPrefsHandler =
function() {
	this._DimDimZimletGeneralPreferences = null;
	appCtxt.getAppController().setStatusMsg(this.getMessage("DimDimZimlet_PrefSaved"), ZmStatusView.LEVEL_INFO);
	this.generalPrefsDlg.popdown();
};

/**
 * Gets appointment id and meeting data.
 *
 * @param {string} key Appointment id
 * @param {AjxCallback} postCallback  A callback
 */
DimDimZimlet.prototype._getApptIdsHashMetaData =
function(key, postCallback) {
	this._currentMetaData = new ZmMetaData(appCtxt.getActiveAccount(), key);
	this._currentMetaData.get("DimDimZimletApptIdsHash", null, new AjxCallback(this, this._handleGetApptIdsHashMetaData, postCallback));
};

/**
 * Handles appointment and DimDim response.
 *
 * @param {AjxCallback} postCallback A callback
 * @param {object} result Custom metadata response
 */
DimDimZimlet.prototype._handleGetApptIdsHashMetaData =
function(postCallback, result) {
	this._apptIdsMetaData = null;//nullify old data
	try {
		var response = result.getResponse().BatchResponse.GetCustomMetadataResponse[0];
		if (response.meta && response.meta[0]) {
			this._apptIdsMetaData = response.meta[0]._attrs;
		}
		if (postCallback) {
			postCallback.run(this._apptIdsMetaData);
		} else {
			return this._apptIdsMetaData;
		}
	} catch(ex) {
		this._showErrorMessage(ex);
		return;
	}
};

/**
 * Saves appointment-id and DimDim meeting key information using customMetaData api.
 *
 * @param {string} key Appointment id
 * @param {string} value Meeting key
 * @param {string} seriesScheduleId  Series meeting key
 */
DimDimZimlet.prototype._saveApptIdsHashToServer =
function(key, value, seriesScheduleId) {
	this._currentMetaData = new ZmMetaData(appCtxt.getActiveAccount(), key);
	var keyValArry = [];

	if (this._appt.viewMode == ZmCalItem.MODE_EDIT_SINGLE_INSTANCE) {
		keyValArry["exceptionMeetingKey"] = value;
		if (seriesScheduleId && seriesScheduleId != "") {
			keyValArry["scheduleId"] = seriesScheduleId;
		}
	} else {
		keyValArry["scheduleId"] = value;
	}
	keyValArry["hostName"] = this._currentDimDimAccount[DimDimZimlet.PROP_USERNAME.propId];
	keyValArry["hostPwd"] = this._currentDimDimAccount[DimDimZimlet.PROP_PASSWORD.propId];
	keyValArry["companyId"] = this._currentDimDimAccount[DimDimZimlet.PROP_COMPANY_ID.propId];
	this._currentMetaData.set("DimDimZimletApptIdsHash", keyValArry, null, null);
};

/**
 * Shows Select Account dialog.
 *
 * @param {AjxCallback} postCallback2 A callback
 * @param {ZmAppt}	appt An Appointment
 */
DimDimZimlet.prototype._showSelectAccountDlg =
function(postCallback2, appt) {
	var postCallback = new AjxCallback(this, this._doShowSelectAccountDlg, [postCallback2, appt]);
	this._getAccPrefsMetaData(postCallback);
};

/**
 * Shows Select Account dialog.
 *
 * @param {AjxCallback} postCallback A callback
 */
DimDimZimlet.prototype._doShowSelectAccountDlg =
function(postCallback, appt) {
	if (appt && appt.folderId) {
		var accountNumber = this._getAccountNumberFromFolderId(appt.folderId);
	}
	if (this._showSelectAccntsDlg) {
		this._showSelectAccntsDlg._postCallback = postCallback;
		if (accountNumber) {
			this._setMenuValue("DimDimZimlet_accountsToSelectList", accountNumber);
		}
		this._showSelectAccntsDlg.popup();
		return;
	}
	try {
		var selectHtml = this._getAccountsSelectListMenuHtml("DimDimZimlet_accountsToSelectList");
	} catch (ex) {
		this._showErrorMessage(ex);
		return;
	}
	this._showSelectAccntDlgView = new DwtComposite(this.getShell());
	this._showSelectAccntDlgView.getHtmlElement().style.overflow = "auto";
	this._showSelectAccntDlgView.getHtmlElement().innerHTML = this._createShowSelectAccntView(selectHtml);
	this._showSelectAccntsDlg = new ZmDialog({parent: this.getShell(), title:this.getMessage("DimDimZimlet_selectAccntToUse"), view:this._showSelectAccntDlgView, standardButtons:[DwtDialog.OK_BUTTON, DwtDialog.CANCEL_BUTTON]});
	this._showSelectAccntsDlg._postCallback = postCallback;
	this._showSelectAccntsDlg.setButtonListener(DwtDialog.OK_BUTTON, new AjxListener(this, this._showSelectAccntDlgOkBtnListner));
	if (accountNumber) {
		this._setMenuValue("DimDimZimlet_accountsToSelectList", accountNumber);
	}
	this._showSelectAccntsDlg.popup();
};

/**
 * Creates select account html view.
 *
 * @return {string} html
 */
DimDimZimlet.prototype._createShowSelectAccntView =
function(selectHtml) {
	try {
		var html = [];
		html.push("<div style='padding:5px'>", this.getMessage("DimDimZimlet_DimDimAccntToUse"), " <span>", selectHtml, "</span></div>");
		return html.join("");
	} catch(ex) {
		this._showErrorMessage(ex);
	}
};

/**
 * OK button listener for Select accounts dialog.
 */
DimDimZimlet.prototype._showSelectAccntDlgOkBtnListner =
function() {
	appCtxt.getAppController().setStatusMsg(this.getMessage("DimDimZimlet_pleaseWait"), ZmStatusView.LEVEL_INFO);
	this._showSelectAccntsDlg.popdown();
	this._showSelectAccntsDlg._postCallback.run(document.getElementById("DimDimZimlet_accountsToSelectList").value);
};


/**
 * Shows DimDim appointments list for a given account number.
 * @param {number} accountNumber	the account number
 */
DimDimZimlet.prototype._showAppointmentsList =
function(accountNumber) {
	if (!this._DimDimZimletAccountPreferences) {
		//var postCallback = new AjxCallback(this, this._getMeetingsList, accountNumber);
		var postCallback = new AjxCallback(this, this._showMeetingListDlg, [accountNumber, "TODAY"]);
		this._getAccPrefsMetaData(postCallback);
	} else {
		this._showMeetingListDlg(accountNumber, "TODAY");
		//this._getMeetingsList(accountNumber);
	}
};

/**
 * Gets DimDim appointment list
 *
 * @param {number} accountNumber	the account number
 * @param {string} listType Specifies what constraints should be put for the list. If null, then "TODAY" is used
 */
DimDimZimlet.prototype._getMeetingsList =
function(accountNumber, listType) {
	try {
		this._setCurrentAccntInfoFromAccntNumber(accountNumber);
	} catch(ex) {
		this._showErrorMessage(ex);
		return;
	}
	var today = new Date();
	var tomorrow = new Date(today.getTime() + (1 * 24 * 3600 * 1000));
	var seventhDay = new Date(today.getTime() + (7 * 24 * 3600 * 1000));
	if (listType == "TOMORROW") {
		var startDate = this._formatDate(tomorrow);
		var endDate = this._formatDate(tomorrow);
	} else if (listType == "NEXT_7_DAYS") {
		var startDate = this._formatDate(tomorrow);
		var endDate = this._formatDate(seventhDay);
	} else {//today
		listType = "TODAY";
		var startDate = this._formatDate(today);
		var endDate = this._formatDate(today);
	}
	try{
		this._loginToDimDim(accountNumber);//make sure to login (if we havent already)
	} catch(ex) {
		this._showErrorMessage(ex);
		return;
	}
	var url = [this._getDimDimServerURL(),"/api/prtl/get_schedules"].join("");
	var proxyUrl = [ZmZimletBase.PROXY, AjxStringUtil.urlComponentEncode(url)].join(""); 
	var jsonData = ["{\"request\":{\"startDate\": \"",startDate,"\",\"endDate\": \"",endDate,"\", \"groupName\": \"all\", \"numberOfMeetings\":\"25\", \"accountName\": \"", this._currentDimDimAccount[DimDimZimlet.PROP_USERNAME.propId],
					"\",\"roomName\": \"default\"}}"].join("");
	var params = {jsonData: jsonData, proxyUrl:proxyUrl, errorMsg: this.getMessage("DimDimZimlet_couldNotGetSchedules")};
	var callback = new AjxCallback(this, this._setMeetingListView, listType);
	params.callback = callback;
	this._doPost(params);
};

/**
 * Shows DimDim meeting list dialog.
 * @param {int} accountNumber DimDim Account Number
 * @param {string} listType Specifies what constraints should be put for the list. If null, then "TODAY" is used
 */
DimDimZimlet.prototype._showMeetingListDlg =
function(accountNumber, listType) {
	if (this._meetingLstDlg) {
		this._meetingListDlgView.getHtmlElement().innerHTML = ["<label style='font-weight:bold;font-size:12px;color:blue;'>",this.getMessage("DimDimZimlet_pleaseWait"),"</div>"].join("");
		this._meetingLstDlg.popup();
		this._getMeetingsList(accountNumber, listType);
		return;
	}
	this._meetingListDlgView = new DwtComposite(this.getShell());
	this._meetingListDlgView.setSize("570", "200");
	this._meetingListDlgView.getHtmlElement().style.background = "white";
	this._meetingListDlgView.getHtmlElement().style.overflow = "auto";
	//this._setMeetingListView(this._meetingListDlgView.getHtmlElement(), objResult, listType);
	this._meetingLstDlg = new ZmDialog({parent: this.getShell(), title:this.getMessage("DimDimZimlet_startOrJoinDimDimMeeting") , view:this._meetingListDlgView, standardButtons:[DwtDialog.CANCEL_BUTTON]});
	//this._addShowMeetingListListeners();
	this._meetingListDlgView.getHtmlElement().innerHTML = ["<label style='font-weight:bold;font-size:12px;color:blue;'>",this.getMessage("DimDimZimlet_pleaseWait"),"</div>"].join("");
	this._meetingLstDlg.popup();
	this._getMeetingsList(accountNumber, listType);
};

/**
 * Adds meeting listeners to links that opens DimDim meetings.
 */
DimDimZimlet.prototype._addShowMeetingListListeners = function() {	
	var callback = AjxCallback.simpleClosure(this._meetingListTypesMenuHandler, this);
	document.getElementById("DimDimZimlet_meetingsListTypesMenu").onchange = callback;
};

DimDimZimlet.prototype._meetingListTypesMenuHandler = function() {
	var type = document.getElementById("DimDimZimlet_meetingsListTypesMenu").value;
	this._getMeetingsList(this._currentDimDimAccountNumber, type);
};

/**
 * Creates Meeting list view.
 * @param {string} listType	 Type of the list
 * @param {object} objResult	The Object with list of DimDim meetings
 */
DimDimZimlet.prototype._setMeetingListView = function(listType, objResult) {
	var mtgs = objResult.response.meetings;
	if (!mtgs) {
		mtgs = [];
	} else if (!(mtgs instanceof Array)) {
		mtgs = [mtgs];
	}
	var html = [];
	html.push("<div class='DimDimZimlet_lightGray'>", "Show meetings: ", this._getMeetingListTypesMenu(listType), "</div>");
	html.push("<table class='DimDim_hoverTable' cellspacing=0px width=100%>");
	html.push("<tr align=left><th>", this.getMessage("DimDimZimlet_host"), "</th><th width=50%>", this.getMessage("DimDimZimlet_meetingName"),
			"</th><th>", this.getMessage("DimDimZimlet_startTime"), "</th><th>", this.getMessage("DimDimZimlet_action"), "</th></tr>");
	var isOdd = true;
	for (var i = 0; i < mtgs.length; i++) {
		var cls = "RowEven";
		var mtg = mtgs[i];
		var startLinkId = Dwt.getNextId();
		if (!isOdd) {
			cls = "RowOdd";
		}
		var startMeetingUrl = [this._getDimDimServerURL(),"/all/",mtg.accountName,"/",mtg.roomName,"/?action=start&scheduleId=",mtg.scheduleId].join("");
		var startHour = mtg.startHour < 10 ? "0"+mtg.startHour : mtg.startHour;
		html.push("<tr class='", cls, "'>",
				"<td>", mtg.accountName, "</td>",
				"<td>", AjxStringUtil.urlComponentDecode(mtg.meetingName), "</td>",
				"<td>", mtg.startDate, " ",startHour,":", mtg.startMinute, " ", mtg.timeAMPM, 
				"<br/><label style='color:gray;font-size:10px'>", mtg.timezone, "</label></td>",
				"<td><a href='",startMeetingUrl,"' target='_blank' >", this.getMessage("DimDimZimlet_startJoin"), "</a></td>",
				"</tr>");
		isOdd = !isOdd;
	}
	html.push("</table>");

	this._meetingListDlgView.getHtmlElement().innerHTML = html.join("");
	this._addShowMeetingListListeners();

};

DimDimZimlet.prototype._getMeetingListTypesMenu =
function(listType) {
	if (!listType) {
		listType = "TODAY";
	}
	var items = {"TODAY" : this.getMessage("DimDimZimlet_today"), 
				"TOMORROW" : this.getMessage("DimDimZimlet_tomorrow"), "NEXT_7_DAYS" : this.getMessage("DimDimZimlet_next7days")}
	var html = [];
	html.push("<select id='DimDimZimlet_meetingsListTypesMenu'>");
	for (var el in items) {
		var sltdStr = el == listType ? " selected " : "";
		html.push("<option value='", el, "' ", sltdStr, " >", items[el], "</option>");
	}
	html.push("</select>");
	return html.join("");
};

/**
 * Shows one click dialog.
 * @param {string} attendees A string with email addresses
 */
DimDimZimlet.prototype._showOneClickDlg =
function(attendees) {
	if(!attendees) {
		attendees = "";
	}
	if (this._oneClickDlg) {
		this._setAttendeesToOneClickDlg(attendees);
		this._oneClickDlg.popup();
		return;
	}
	
	var postCallback = new AjxCallback(this, this._doShowOneClickDlg, attendees);
	this._getAccPrefsMetaData(postCallback);
};

/**
 * Shows one click dialog.
 */
DimDimZimlet.prototype._doShowOneClickDlg =
function(attendees) {
	try {
		var selectHtml = this._getAccountsSelectListMenuHtml("DimDimZimlet_oneClickAccountSelect");
	} catch (ex) {
		this._showErrorMessage(ex);
		return;
	}
	this._oneClickDlgView = new DwtComposite(this.getShell());
	this._oneClickDlgView.getHtmlElement().style.overflow = "auto";
	this._oneClickDlgView.getHtmlElement().innerHTML = this._createOneClickMeetingView(selectHtml);
	this._oneClickDlg = new ZmDialog({parent: this.getShell(), title:this.getMessage("DimDimZimlet_startQuickDimDimMeeting"), view:this._oneClickDlgView, standardButtons:[DwtDialog.OK_BUTTON, DwtDialog.CANCEL_BUTTON]});
	this._oneClickDlg.setButtonListener(DwtDialog.OK_BUTTON, new AjxListener(this, this._oneClickDlgOkBtnListner));
	this._addAutoCompleteHandler();
	this._setAttendeesToOneClickDlg(attendees);
	this._oneClickDlg.popup();
};

/**
 *Sets attendees to one click dialog
 *@param {string} attendees Attendees
 */
DimDimZimlet.prototype._setAttendeesToOneClickDlg =
function(attendees) {
	if (!attendees || attendees == "") {
		attendees = "";
	}
	document.getElementById("DimDimZimlet_oneClickAttendeesField").value = attendees;
};

DimDimZimlet.prototype._addAutoCompleteHandler =
function() {
	if (appCtxt.get(ZmSetting.CONTACTS_ENABLED) || appCtxt.get(ZmSetting.GAL_ENABLED)) {
		var params = {
			dataClass: appCtxt.getAutocompleter(),
			matchValue: ZmAutocomplete.AC_VALUE_EMAIL,
			compCallback: (new AjxCallback(this, this._handleCompletionData, [this])),
			keyUpCallback: (new AjxCallback(this, this._acKeyUpListener))
		};
		this._acAddrSelectList = new ZmAutocompleteListView(params);
		this._acAddrSelectList.handle(document.getElementById("DimDimZimlet_oneClickAttendeesField"));
	}
};

DimDimZimlet.prototype._acKeyUpListener =
function(event, aclv, result) {
	//ZmSharePropsDialog._enableFieldsOnEdit(this);
};

DimDimZimlet.prototype._handleCompletionData =
function (control, text, element) {
	element.value = text;
	try {
		if (element.fireEvent) {
			element.fireEvent("onchange");
		} else if (document.createEvent) {
			var ev = document.createEvent("UIEvents");
			ev.initUIEvent("change", false, window, 1);
			element.dispatchEvent(ev);
		}
	}
	catch (ex) {
		// ignore -- TODO: what to do with this error?
	}
};
/**
 * Creates oneclick html view.
 *
 * @returns  {string} html
 */
DimDimZimlet.prototype._createOneClickMeetingView =
function(selectHtml) {

	var html = [];
	html.push("<table class='DimDimZimlet_table' width=100%>");
	html.push("<tr><td>", this.getMessage("DimDimZimlet_DimDimAccntToUse"), " </td><td>", selectHtml, "</td></tr>");
	html.push("<tr><td>", this.getMessage("DimDimZimlet_addAttendees"), "</td><td><input  style='width:300px' type='text' id='DimDimZimlet_oneClickAttendeesField'> </input></td></tr>");
	html.push("</table>");
	return html.join("");
};

/**
 * Adds OK button listener.
 *
 */
DimDimZimlet.prototype._oneClickDlgOkBtnListner =
function() {
	this._oneClickDlg.popdown();
	var accntNumber = document.getElementById("DimDimZimlet_oneClickAccountSelect").value;
	var attendees = document.getElementById("DimDimZimlet_oneClickAttendeesField").value;
	var params = {accntNumber:accntNumber, attendees:attendees};


	var postCallback2 = new AjxCallback(this, this._createOneClickMeetingAndLaunch, params);
	var postCallback = new AjxCallback(this, this._getGeneralPrefsMetaData, postCallback2);
	this._getAccPrefsMetaData(postCallback);
};

/**
 * Creates one click meeting and launches DimDim.
 *
 * @param {hash} params a hash of parameters
 * @param {string} params.accntNumber	 the account number
 * @param {string} params.attendees		the attendee emails
 *
 */
DimDimZimlet.prototype._createOneClickMeetingAndLaunch =
function(params) {
	try {
		this._setCurrentAccntInfoFromAccntNumber(params.accntNumber);
	} catch(ex) {
		this._showErrorMessage(ex);
		return;
	}
	try{
		this._loginToDimDim(params.accntNumber);//make sure to login (if we havent already)
	} catch(ex) {
		this._showErrorMessage(ex);
		return;
	}
	var newParams = {};
	var startDate = new Date();
	var startHour = startDate.getHours();

	newParams["meetingName"] =  this.getMessage("DimDimZimlet_quickMeetingSubject");
	if (params.attendees && params.attendees != "") {
		newParams["attendees"] = AjxEmailAddress.parseEmailString(params.attendees).good.getArray().join(",");
	} else {
		newParams["attendees"] = [];
	}
	newParams["meetingLengthMinutes"] = 60;
	var zTimeZone = "";
	try { 
		zTimeZone = appCtxt.getActiveAccount().settings.getInfoResponse.prefs._attrs.zimbraPrefTimeZoneId;
	} catch(e) {
		zTimeZone = "America/Los_Angeles";//default
	}

	newParams["timezone"] = this.convertTimeZone_Zimbra2DimDim(zTimeZone);
	newParams["attendeeKey"] = this._currentDimDimAccount[DimDimZimlet.PROP_MEETING_PASSWORD.propId];
	newParams["meetingRecurrance"] = "";	
	newParams["startDate"] = this._formatDate(startDate);
	newParams["startHour"] = startHour > 11 ? startHour -12 : startHour;
	newParams["startMinute"] = (Math.ceil(startDate.getMinutes() / 15) * 15) % 60
	newParams["timeAMPM"] = startHour > 11 ? "PM" : "AM";
	newParams["tollfree"] = this._currentDimDimAccount[DimDimZimlet.PROP_TOLL_FREE_PHONE_NUMBER.propId];
	newParams["toll"] = this._currentDimDimAccount[DimDimZimlet.PROP_TOLL_PHONE_NUMBER.propId];
	newParams["internationalTollNumber"] = this._currentDimDimAccount[DimDimZimlet.PROP_INTL_PHONE_NUMBER.propId];
	newParams["attendeePhonePassCode"] = this._currentDimDimAccount[DimDimZimlet.PROP_PHONE_PASSCODE.propId];
	newParams["scheduleId"] = null;

	var url = [this._getDimDimServerURL(),"/api/prtl/create_schedule"].join("");
	var proxyUrl = [ZmZimletBase.PROXY, AjxStringUtil.urlComponentEncode(url)].join(""); 
	var jsonData = this._getCreateOrModifyMeetingRequest(newParams);
	var postParams = {jsonData: jsonData, proxyUrl:proxyUrl, errorMsg: this.getMessage("DimDimZimlet_couldNotScheduleMeeting")};
	var callback = new AjxCallback(this, this._openOneClickMeetingWindow);
	postParams.callback = callback;
	this._doPost(postParams);
};

DimDimZimlet.prototype._openOneClickMeetingWindow = function(respObj) {
	window.open([this._getDimDimServerURL(),"/all/",this._currentDimDimAccount[DimDimZimlet.PROP_USERNAME.propId],"/default/?action=start&scheduleId=",
		respObj.response.scheduleId].join(""));
};

DimDimZimlet.prototype._getDimDimServerURL = function(companyId) {
	if(!companyId && companyId != "" && this._currentDimDimAccount) {
		companyId = this._currentDimDimAccount[DimDimZimlet.PROP_COMPANY_ID.propId];
	}

	if(!companyId || companyId == "" || companyId == "N/A" || companyId == "zimlet.synchrolive.com"){
		return "https://zimlet.synchrolive.com";//this is a test server
	} else {
		return ["https://",companyId,".dimdim.com"].join("");
	}
};

/**
 * Displays error message.
 *
 * @param {string} expnMsg Exception message string
 */
DimDimZimlet.prototype._showErrorMessage =
function(expnMsg) {
	var msg = "";
	if (expnMsg instanceof AjxException) {
		msg = expnMsg.msg;
	} else {
		msg = expnMsg;
	}
	var dlg = appCtxt.getMsgDialog();
	dlg.reset();
	dlg.setMessage(msg, DwtMessageDialog.WARNING_STYLE);
	dlg.popup();
};

/**
 * Gets html select menu.
 *
 * @param {number} id		the account number
 * @return {string} html
 */
DimDimZimlet.prototype._getAccountsSelectListMenuHtml = function(id) {
	this._isAtLeastOneAccountConfigured();
	var html = [];
	html.push("<select id='", id, "'>");
	for (var i = 1; i < 6; i++) {
		var userName = this._DimDimZimletAccountPreferences["DimDimZimlet_username" + i];
		if (userName == "" || userName == "N/A") {
			userName = this.getMessage("DimDimZimlet_notConfigured");
		}
		var label = AjxMessageFormat.format(this.getMessage("DimDimZimlet_accntNumberAndName"), [i, userName]);
		html.push("<option value='", i, "'>", label, "</option>");
	}
	html.push("</select>");
	return html.join("");
};


/**
 * Handles object drop
 *
 */
DimDimZimlet.prototype.doDrop = function(obj) {
	switch (obj.TYPE) {
		case "ZmContact":
			this.contactDropped(obj);
			break;
		case "ZmAppt":
			this.apptDropped(obj);
			break;
		case "ZmConv":
		case "ZmMailMsg":
			this.mailDropped(obj);
			break;
	}
};

/**
 * when an appointment  is dropped on the zimlet,
 * pop up the quick create dialog with the contacts as attendees
 * @param {Obj} ZmAppt
 */
DimDimZimlet.prototype.apptDropped = function(obj) {
	var attendees = obj.srcObj.getAttendees(ZmCalBaseItem.PERSON);
	var emails = [];
	for (var i = 0; i < attendees.length; i++) {
		emails.push(attendees[i].getEmail());
	}
	this._showOneClickDlg(emails.join(","));
}

/**
 * when a conversation or msg is dropped on the zimlet,
 * pop up the quick create dialog with the contacts as attendees
 * @param {Obj} ZmConv | ZmMailMsg
 */
DimDimZimlet.prototype.mailDropped = function(msgObj) {
	this.srcMsgObj = msgObj.srcObj;
	if (this.srcMsgObj.type == "CONV") {
		this.srcMsgObj = this.srcMsgObj.getFirstHotMsg();
	}
	var emails = this.srcMsgObj.getEmails().getArray().join(",");
	this._showOneClickDlg(emails);
};

/**
 * when a contact or list of contacts is dropped on the zimlet,
 * pop up the quick create dialog with the contacts as attendees
 * @param {ZmContact} objContact Contact
 */
DimDimZimlet.prototype.contactDropped = function(objContact) {
	if (!(objContact instanceof Array)) {
		objContact = [objContact];
	}
	var emails = []
	for (var i = 0; i < objContact.length; i++) {
		var c = objContact[i];
		var e = c.email ? c.email : (c.email2 ? c.email2 : (c.email3 ? c.email3 : ""));
		if (e != "" && e != undefined && e != "undefined") {
			emails.push(e);
		}
	}
	this._showOneClickDlg(emails.join(","));
};

