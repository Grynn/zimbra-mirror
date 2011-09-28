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

/**
 * Constructor.
 *
 */
function ZmPTOZimlet() {

}

ZmPTOZimlet.prototype = new ZmZimletBase();
ZmPTOZimlet.prototype.constructor = ZmPTOZimlet;

/**
 * Creates the PTO dialog.
 *
 */
ZmPTOZimlet.prototype._createPTODialog =
function() {
    //if zimlet dialog already exists...
    if (this._erDialog) {
        this._erDialog.popup();
        return;
    }
    AjxDispatcher.require(["CalendarCore", "Calendar"]);

    this.erView = new DwtComposite(this.getShell());
    this.erView.setSize("370", "345");
    // set width and height
    this.erView.getHtmlElement().style.overflow = "auto";
    // adds scrollbar
    this.erView.getHtmlElement().innerHTML = this._createErView();
    //this._getAbsHoursMenu();
    //this._setTodayToDateField();
    var dialog_args = {
        title: this.getMessage("dialogLabel"),
        view: this.erView,
        standardButtons: [DwtDialog.OK_BUTTON, DwtDialog.CANCEL_BUTTON],
        parent: this.getShell()
    };
    this._erDialog = new ZmDialog(dialog_args);
    this._erDialog.setButtonListener(DwtDialog.OK_BUTTON, new AjxListener(this, this._okBtnListener));

    this._createCalendarWidget("ptoZimlet_fromDateMenu", "ptoZimlet_fromDateField");
    this._createCalendarWidget("ptoZimlet_toDateMenu", "ptoZimlet_toDateField");
    this._addOOOCalendarMenu();
    this._addAutoCompleteHandler();
    this._setCcEmails();
    this._formatter = new AjxDateFormat("yyyyMMddHHmmss'Z'");
    this._toEmail = this.getConfig("ZmPtoZimletPayRollEmail");
    this._addMiscHandlers();
    this._erDialog.popup();
};

ZmPTOZimlet.prototype._addMiscHandlers =
function() {
    document.getElementById("ptoZimlet_autoReplyReadMeLink").onclick = AjxCallback.simpleClosure(this._toggleAutoReplyNotes, this);
};


ZmPTOZimlet.prototype._setPtoTypeName =
function() {
    this._ptoTypeName = this.getMessage("ptoType1Name");
    if (!document.getElementById("ptoZimlet_ptoType1RadioId").checked) {
        this._ptoTypeName = this.getMessage("ptoType2Name");
    }
};


ZmPTOZimlet.prototype._toggleAutoReplyNotes =
function() {
    var section = document.getElementById("ptoZimlet_autoReplyNotesSection");
    if (section.style.display == "block") {
        section.style.display = "none";
    } else {
        section.style.display = "block";
    }
};


ZmPTOZimlet.prototype._setCcEmails =
function() {
    var val = this.getUserProperty("ptoZimletCcEmails");
    document.getElementById("ptoZimlet_ccManagerField").value = (val && val != "" & val != "undefined") ? val: "";
};

ZmPTOZimlet.prototype._getStartAndEndDates =
function() {
    var startDateStr = document.getElementById("ptoZimlet_fromDateField").value;
    var endDateStr = document.getElementById("ptoZimlet_toDateField").value;
    var startDate = AjxDateUtil.simpleParseDateStr(startDateStr);
    var endDate = AjxDateUtil.simpleParseDateStr(endDateStr);
    if (!startDate || !endDate) {
        this.showWarningMsg(this.getMessage("invalidDates"));
    } else if (startDate.getTime() > endDate.getTime()) {
        this.showWarningMsg(this.getMessage("startDateGreaterThanEndDate"));
    } else {
        return {
            startDate: startDate,
            endDate: endDate,
            startDateStr: startDateStr,
            endDateStr: endDateStr
        };
    }
};

ZmPTOZimlet.prototype._sendPTORequest =
function(dates, accountInfo) {
    var ccEmails = this._getCcEmails();
    if (ccEmails) {
        this.setUserProperty("ptoZimletCcEmails", ccEmails.toString(), true);
    }
    var subject = [accountInfo.email, "  - PTO (", this._ptoTypeName, ") - ", dates.startDateStr, " ", dates.endDateStr].join("");
    var bodyPrefix = AjxMessageFormat.format(this.getMessage("bodyPrefix"), this._ptoTypeName);
    var bodySentance = [bodyPrefix, " ", this.getMessage("from"), " ", dates.startDateStr, " ", this.getMessage("to"), " ", dates.endDateStr].join("");
    var bodyText = [bodySentance, "\n\r\n\r", " - ", accountInfo.name, "\n\r", this.getMessage("sentUsingPTOZimlet")].join("");
    var bodyHtml = [bodySentance, "<br><br>", " - ", accountInfo.name, "<br>", this.getMessage("sentUsingPTOZimlet")].join("");
    this._sendEmail(subject, bodyHtml, bodyText, this._toEmail, ccEmails, dates, accountInfo);
};

ZmPTOZimlet.prototype._getAccountInfo =
function() {
    var identity = appCtxt.getIdentityCollection().defaultIdentity;
    var mainAcct = appCtxt.accountList.mainAccount;
    var email = identity.sendFromAddress || mainAcct.getEmail();
    var name = identity.sendFromDisplay;
    return {
        name: name,
        email: email
    };
};

ZmPTOZimlet.prototype._getCcEmails = function() {
    var el = document.getElementById("ptoZimlet_ccManagerField");
    if (el) {
        var val = el.value;
        if (val != "") {
            var parsed = AjxEmailAddress.parseEmailString(val);
            var badAddrs = parsed.bad.getArray().toString();
            if (badAddrs != "") {
                this.showWarningMsg(this.getMessage("invalidAddressesInCcField"));
                return;
            }
            var ccEmails = parsed.good.size() ? parsed.good.getArray() : parsed.all.getArray();
        }
    }
    return ccEmails;
};

ZmPTOZimlet.prototype.showWarningMsg = function(message) {
    if (message.length > 1000) {
        message = message.substring(0, 999) + "...";
    }
    var style = DwtMessageDialog.WARNING_STYLE;
    var dialog = appCtxt.getMsgDialog();
    this.warningDialog = dialog;
    dialog.setMessage(message, style);
    dialog.popup();
};

/**
 * Listener for the reminder dialog OK button.
 *
 * @see		_createPTODialog
 */
ZmPTOZimlet.prototype._okBtnListener =
function() {
    this._setPtoTypeName();
    var dates = this._getStartAndEndDates();
    var accountInfo = this._getAccountInfo();
    if (!dates) {
        return;
    }
    this._sendPTORequest(dates, accountInfo);
};

ZmPTOZimlet.prototype._createOOOAppt =
function(dates, accountInfo) {
    var option = this._folderSelect.getSelectedOption();
    if (option.getDisplayValue() == this.getMessage("pleaseSelect")) {
        return;
    }
    try {
        var appt = new ZmAppt();
        appt.setStartDate(dates.startDate);
        appt.setEndDate(dates.endDate);
        appt.setName([accountInfo.name, " - ", this.getMessage("ooo")].join(""));
        appt.freeBusy = "O";
        appt.transparency = "O";
        appt.allDayEvent = "1";
        appt.setFolderId(option.getValue());
        //set calendar folder id
        appt.save();
    } catch(e) {
        return;
    }
    appCtxt.getAppController().setStatusMsg(this.getMessage("apptCreated"), ZmStatusView.LEVEL_INFO);
};

/**
 * Creates an appointment.
 *
 * @param	{Date}		startDate		the start date
 * @param	{Date}		endDate			the end date
 * @param	{string}	subject			the subject
 */
ZmPTOZimlet.prototype._createAppt =
function(startDate, endDate, subject) {
    var reminderMinutes = appCtxt.getSettings().getSetting("CAL_REMINDER_WARNING_TIME").value;
    try {
        var appt = new ZmAppt();
        appt.setStartDate(startDate);
        appt.setEndDate(endDate);
        appt.setName(subject);
        appt.setTextNotes(this._notesPrefix + this._notes);
        appt.setReminderMinutes(reminderMinutes);
        appt.freeBusy = "O";
        appt.transparency = "O";
        appt.allDayEvent = "1";
        appt.setFolderId(this.emailFollowupFolderId);
        appt.save();
    } catch(e) {
        return;
    }
    var transitions = [ZmToast.FADE_IN, ZmToast.PAUSE, ZmToast.FADE_OUT];
    appCtxt.getAppController().setStatusMsg(this.getMessage("EmailReminder_status_created"), ZmStatusView.LEVEL_INFO, null, transitions);
};

ZmPTOZimlet.prototype._setAutoReplyMsg =
function(dates) {
	var settings = appCtxt.getSettings();
	var message = document.getElementById("ptoZimlet_setAutoReplyMsg").value;
	if (message == "") {
        return;
    }

    var startDateStr = ZmPref.dateLocal2GMT(this._formatter.format(dates.startDate));
    var endDateStr = ZmPref.dateLocal2GMT(this._formatter.format(dates.endDate));

    var soapDoc = AjxSoapDoc.create("ModifyPrefsRequest", "urn:zimbraAccount");

    var node = soapDoc.set("pref", "TRUE");
    node.setAttribute("name", settings.getSetting(ZmSetting.VACATION_MSG_ENABLED).name);

    var node = soapDoc.set("pref", message);
    node.setAttribute("name", settings.getSetting(ZmSetting.VACATION_MSG).name);

    var node = soapDoc.set("pref", startDateStr);
    node.setAttribute("name", settings.getSetting(ZmSetting.VACATION_FROM).name);

    var node = soapDoc.set("pref", endDateStr);
    node.setAttribute("name", settings.getSetting(ZmSetting.VACATION_UNTIL).name);
    var respCallback = new AjxCallback(this, this._handleSetAutoReplyMsgResponse);
    appCtxt.getAppController().sendRequest({
        soapDoc: soapDoc,
        asyncMode: true,
        noBusyOverlay: true,
        callback: respCallback
    });
};

ZmPTOZimlet.prototype._handleSetAutoReplyMsgResponse =
function(response) {
    appCtxt.getAppController().setStatusMsg(this.getMessage("autoReplyMsgSet"));
};

/**
 * Handles the mail flag event.
 *
 *
 */
ZmPTOZimlet.prototype.onMailFlagClick =
function(msgs, on) {
    if (this._allowFlag && this.ZmPTOZimletON) {
        this._msgObj = msgs[0];
        if (on == null || on == undefined) {
            //when on is null, delay this so we can figure out if the item was flagged or unflagged
            setTimeout(AjxCallback.simpleClosure(this._showDlg, this), 1000);
        } else if (on == true) {
            this._showDlg(true);
        }
    }
};

/**
 * Creates the error view.
 *
 * @see		_createPTODialog
 */
ZmPTOZimlet.prototype._createErView =
function() {
    var html = new Array();
    html.push(
    "<table width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\"><tbody><tr class=\"ZOptionsHeaderRow\"><td class=\"ZOptionsHeaderL\"><div class=\"ImgPrefsHeader_L\"></div></td><td class='ImgPrefsHeader' style='width:100%;font-size:11px;font-weight:bold;'>", this.getMessage("createPTORequest"), "</td><td class=\"ZOptionsHeaderR\"><div class=\"ImgPrefsHeader_R\"></div></td></tr></tbody></table>",

    "<div class='ZOptionsSectionMain'>",

    "<div>", this.getMessage("selectPTOType"), "</div>",
    "<table><tr>",
    "<td><input id='ptoZimlet_ptoType1RadioId' type=\"radio\" name=\"ptoZimlet_ptoTypeRadio\" checked value='", this.getMessage("ptoType1Name"), "' /></td>",
    "<td>", this.getMessage("ptoType1Name"), "</td>",
    "<td width='50px'></td>",
    "<td><input id='ptoZimlet_ptoType2RadioId' type=\"radio\" name=\"ptoZimlet_ptoTypeRadio\" value='", this.getMessage("ptoType2Name"), "' /></td>",
    "<td>", this.getMessage("ptoType2Name"), "</td>",
    "</tr></table>",
    "<br/>",
    "<div>", this.getMessage("sendPTORequestMsg"), "</div>",

    "<div>",
    "<table><tr><td>", this.getMessage("from"), "</td>",
    "<td><input id='ptoZimlet_fromDateField' type=text SIZE=9 /></td>",
    "<td id='ptoZimlet_fromDateMenu'></td>",
    "<td width=50px></td>",
    "<td>", this.getMessage("to"), "</td>",
    "<td><input id='ptoZimlet_toDateField' type=text SIZE=9 /></td>",
    "<td id='ptoZimlet_toDateMenu'></td>",
    "</tr></table>",
    "</div>",

    "</div>",

    "<br>",
    "<table width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\"><tbody><tr class=\"ZOptionsHeaderRow\"><td class=\"ZOptionsHeaderL\"><div class=\"ImgPrefsHeader_L\"></div></td><td class='ImgPrefsHeader' style='width:100%;font-size:11px;font-weight:bold;'>", this.getMessage("ccEmail"), "</td><td class=\"ZOptionsHeaderR\"><div class=\"ImgPrefsHeader_R\"></div></td></tr></tbody></table>",
    "<div class='ZOptionsSectionMain'><table><tr><td>", this.getMessage("enterEmails"), "</td>",
    "<td><input style='width:230px' id='ptoZimlet_ccManagerField' type='text' /></td>",
    "</tr></table></div>",
    "<br/>",
    "<table width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\"><tbody><tr class=\"ZOptionsHeaderRow\"><td class=\"ZOptionsHeaderL\"><div class=\"ImgPrefsHeader_L\"></div></td><td class='ImgPrefsHeader' style='width:100%;font-size:11px;font-weight:bold;'>", this.getMessage("oooAppt"), "</td><td class=\"ZOptionsHeaderR\"><div class=\"ImgPrefsHeader_R\"></div></td></tr></tbody></table>",
    "<div class='ZOptionsSectionMain'><table><tr><td>", this.getMessage("createOOOAppt"), "</td>",
    "<td id='ptoZimlet_OOOCalendarMenu'></td></tr></table></div>",
    "<br>",
    "<table width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\"><tbody><tr class=\"ZOptionsHeaderRow\"><td class=\"ZOptionsHeaderL\"><div class=\"ImgPrefsHeader_L\"></div></td><td class='ImgPrefsHeader' style='width:100%;font-size:11px;font-weight:bold;'>", this.getMessage("setAutoReplyMsg"), " ", "<a href='javascript:void(0);' id='ptoZimlet_autoReplyReadMeLink'>", this.getMessage("readMe"), "</a>", "</td><td class=\"ZOptionsHeaderR\"><div class=\"ImgPrefsHeader_R\"></div></td></tr></tbody></table>",
    "<div class='ZOptionsSectionMain'>",
    "<div id='ptoZimlet_autoReplyNotesSection' style='display:none;'>", this.getMessage("autoReplyReadMe"), "</div><textarea cols='10' rows='30' style='height:50px;resize:none;' id='ptoZimlet_setAutoReplyMsg'></textarea></div>");

    return html.join("");
};

/**
 * Creates a mini-calendar widget.
 * @menuId String DOM id of the date menu
 * @fieldId String DOM id of the date field
 * @see			_createPTODialog
 */
ZmPTOZimlet.prototype._createCalendarWidget =
function(menuId, fieldId) {
    document.getElementById(fieldId).value = AjxDateUtil.simpleComputeDateStr(new Date());
    var dateButtonListener = new AjxListener(this, this._dateButtonListener, fieldId);
    var dateCalSelectionListener = new AjxListener(this, this._dateCalSelectionListener, fieldId);
    this._startDateButton = ZmCalendarApp.createMiniCalButton(this.erView, menuId, dateButtonListener, dateCalSelectionListener);
};

ZmPTOZimlet.prototype._addOOOCalendarMenu =
function() {
    this._folderSelect = new DwtSelect({
        parent: this._erDialog,
        parentElement: ("ptoZimlet_OOOCalendarMenu")
    });
    this._appt = new ZmAppt();

    ZmApptViewHelper.populateFolderSelect(this._folderSelect, null, {},
    this._appt);
    var option = new DwtSelectOption(id, true, this.getMessage("pleaseSelect"), null, null, "");
    this._folderSelect.addOption(option, true);
};


/**
 * The date button listener.
 * @param fieldId String DOM ID of the Date field
 * @see			_createCalendarWidget
 */
ZmPTOZimlet.prototype._dateButtonListener =
function(fieldId, ev) {
    var el = document.getElementById(fieldId);
    if (!el) {
        return;
    }
    var calDate = AjxDateUtil.simpleParseDateStr(el.value);

    // always reset the date to current field's date
    var menu = ev.item.getMenu();
    var cal = menu.getItem(0);
    cal.setDate(calDate, true);
    ev.item.popup();
};

/**
 * The date calendar selection listener.
 *
 * @see			_createCalendarWidget
 */
ZmPTOZimlet.prototype._dateCalSelectionListener =
function(fieldId, ev) {
    var val = AjxDateUtil.simpleComputeDateStr(ev.detail);
    document.getElementById(fieldId).value = val;
    if (fieldId == "ptoZimlet_fromDateField") {
        //set to-date same as from-date
        document.getElementById("ptoZimlet_toDateField").value = val;
    }
};

/**
 * Called when the panel is double-clicked.
 */
ZmPTOZimlet.prototype.doubleClicked = function() {
    this.singleClicked();
};

/**
 * Called when the panel is single-clicked.
 */
ZmPTOZimlet.prototype.singleClicked = function() {
    this._createPTODialog();
};

//Add AutoComplete to Cc field
ZmPTOZimlet.prototype._addAutoCompleteHandler =
function() {
    if (appCtxt.get(ZmSetting.CONTACTS_ENABLED) || appCtxt.get(ZmSetting.GAL_ENABLED)) {
        var params = {
            dataClass:		appCtxt.getAutocompleter(),
            matchValue:		ZmAutocomplete.AC_VALUE_EMAIL,
            keyUpCallback:	this._acKeyUpListener.bind(this),
			contextId:		this.name
        };
        this._acAddrSelectList = new ZmAutocompleteListView(params);
        this._acAddrSelectList.handle(document.getElementById("ptoZimlet_ccManagerField"));
    }
};

ZmPTOZimlet.prototype._acKeyUpListener =
function(event, aclv, result) {
    //ZmSharePropsDialog._enableFieldsOnEdit(this);
    };

/**
 * Sends the email.
 * @param	{string}	subject		the message subject
 * @param	{string}	bodyHtml	the message body in HTML
 * @param	{string}	bodyText	the message body in text
 * @param	{string}	to			to Email
 * @param	{Array}	 ccEmails	Cc Email(s)
 */
ZmPTOZimlet.prototype._sendEmail =
function(subject, bodyHtml, bodyText, to, ccEmails, dates, accountInfo) {
    var jsonObj = {
        SendMsgRequest: {
            _jsns: "urn:zimbraMail"
        }
    };
    var request = jsonObj.SendMsgRequest;
    request.suid = (new Date()).getTime();
    var msgNode = request.m = {};
    var identity = appCtxt.getIdentityCollection().defaultIdentity;
    msgNode.idnt = identity.id;

    var isPrimary = identity == null || identity.isDefault;
    var mainAcct = appCtxt.accountList.mainAccount.getEmail();
    var addr = identity.sendFromAddress || mainAcct;
    var displayName = identity.sendFromDisplay;
    var addrNodes = msgNode.e = [];
    var f_addrNode = {
        t: "f",
        a: addr
    };
    if (displayName) {
        f_addrNode.p = displayName;
    }
    addrNodes.push(f_addrNode);

    var t_addrNode = {
        t: "t",
        a: to,
        add: "0"
    };
    addrNodes.push(t_addrNode);
    if (ccEmails && (ccEmails instanceof Array)) {
        for (var i = 0; i < ccEmails.length; i++) {
            var ccEmail = ccEmails[i];
            var t_addrNode = {
                t: "c",
                a: ccEmail.address,
                add: "0"
            };
            addrNodes.push(t_addrNode);
        }
    }
    msgNode.su = {
        _content: subject
    };
    var topNode = {
        ct: "multipart/alternative"
    };
    msgNode.mp = [topNode];
    var partNodes = topNode.mp = [];

    //text part..
    var content = bodyText;
    var partNode = {
        ct: "text/plain"
    };
    partNode.content = {
        _content: content
    };
    partNodes.push(partNode);

    //html part..
    var content = ["<html><head><style type='text/css'>p { margin: 0; }</style></head>",
    "<body><div style='font-family: Times New Roman; font-size: 12pt; color: #000000'>",
    bodyHtml, "</div></body></html>"].join("");

    var partNode = {
        ct: "text/html"
    };
    partNode.content = {
        _content: content
    };
    partNodes.push(partNode);
    var callback = new AjxCallback(this, this._sendEmailCallack, [dates, accountInfo]);
    var errCallback = new AjxCallback(this, this._sendEmailErrCallback);
    return appCtxt.getAppController().sendRequest({
        jsonObj: jsonObj,
        asyncMode: true,
        noBusyOverlay: true,
        errorCallback: errCallback,
        callback: callback
    });
};

/**
 * Send email callback.
 *
 * @see		_sendEmail
 */
ZmPTOZimlet.prototype._sendEmailCallack =
function(dates, accountInfo, response) {
	this._erDialog.popdown();
    appCtxt.getAppController().setStatusMsg(this.getMessage("emailSentToPayroll"));
    this._createOOOAppt(dates, accountInfo);
    this._setAutoReplyMsg(dates);
};

/**
 * Send email error callback.
 *
 * @see		_sendEmail
 */
ZmPTOZimlet.prototype._sendEmailErrCallback =
function(response) {
    var expnMsg = "";
    try {
        expnMsg = response.Body.Fault.Reason.Text;
    } catch(e) {
        //ignore
        }
    this.showWarningMsg(this.getMessage("couldNotSendEmail") + "<BR>" + expnMsg);
};
