/*
 * ***** BEGIN LICENSE BLOCK *****
 *
 * Zimbra Collaboration Suite Zimlets
 * Copyright (C) 2005, 2006, 2007 Zimbra, Inc.
 *
 * The contents of this file are subject to the Yahoo! Public License
 * Version 1.0 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 *
 * ***** END LICENSE BLOCK *****
 */

//////////////////////////////////////////////////////////////
//  Zimlet to handle scheduling of calendar                  //
//  @author Sathishkumar Sugumaran                          //
//////////////////////////////////////////////////////////////
function Com_Zimbra_CalScheduler() {
this._toolbar = {};

this._listeners = {};
this._listeners[ZmOperation.CLOSE] = new AjxListener(this, this._closeListener);
this._listeners[ZmOperation.NEW_APPT] = new AjxListener(this, this._newListener);

}

Com_Zimbra_CalScheduler.prototype = new ZmZimletBase();
Com_Zimbra_CalScheduler.prototype.constructor = Com_Zimbra_CalScheduler;

Com_Zimbra_CalScheduler.prototype.init =
function() {

};

Com_Zimbra_CalScheduler.prototype.showScheduleView =
function(symbol, result) {

	var appViewMgr = appCtxt.getAppViewMgr();

	var resultView  = this._scheduleView;

	if(!this._scheduleView){
		resultView  = this._scheduleView =this._createScheduleView();
	}else {
        this._scheduleView.cleanup();
        this._scheduleView.resetAppt();
        appViewMgr.pushView(ZmId.VIEW_CAL_SCHEDULE_ZIMLET);
    }

    resultView.showMe();
    resultView._updateDays();
    resultView._setAttendees(resultView._organizer, resultView._attendees);
    return resultView;
};

Com_Zimbra_CalScheduler.prototype._createScheduleView =
function() {
		var appViewMgr = appCtxt.getAppViewMgr();

		ZmId.VIEW_CAL_SCHEDULE_ZIMLET = "CLSZ";
		this._initToolbar();

		var resultView = new CalSchedulerView(appCtxt.getShell(), this);
		var elements = {};
		elements[ZmAppViewMgr.C_APP_CONTENT] = resultView;
		elements[ZmAppViewMgr.C_TOOLBAR_TOP] = this._toolbar[ZmId.VIEW_CAL_SCHEDULE_ZIMLET];
		appViewMgr.createView(ZmId.VIEW_CAL_SCHEDULE_ZIMLET, null, elements);

		appViewMgr.pushView(ZmId.VIEW_CAL_SCHEDULE_ZIMLET);
		resultView._initColumns();
		return resultView;
};

Com_Zimbra_CalScheduler.prototype._initToolbar =
function() {
	var buttons = [ZmOperation.NEW_APPT, ZmOperation.CLOSE];
	this._toolbar[ZmId.VIEW_CAL_SCHEDULE_ZIMLET] = new ZmButtonToolBar({parent: appCtxt.getShell(), buttons: buttons});
	this._addSelectionListeners(this._toolbar[ZmId.VIEW_CAL_SCHEDULE_ZIMLET]);
};

Com_Zimbra_CalScheduler.prototype._addSelectionListeners =
function(toolbar) {

	var buttons = toolbar.opList;
	for (var i = 0; i < buttons.length; i++) {
		var button = buttons[i];
		if (this._listeners[button]) {
			toolbar.addSelectionListener(button, this._listeners[button]);
		}
	}

};


Com_Zimbra_CalScheduler.prototype.singleClicked =
function() {
	this.showScheduleView();
	return;
};

Com_Zimbra_CalScheduler.prototype.doDrop =
function(obj) {
	switch (obj.TYPE) {
	    case "ZmMailMsg":
	    case "ZmConv":
		this.newApptFromMailItem(obj, new Date());
		break;
	}
};

Com_Zimbra_CalScheduler.prototype.newApptFromMailItem =
function(mailItem, date) {

	//select the first mail item if multiple items are dropped
	if(mailItem && (mailItem instanceof Array) && (mailItem.length > 0)) {
		mailItem = mailItem[0];
	}
	var subject = mailItem.subject || "";

	if(mailItem.TYPE == "ZmMailMsg") {
		var respCallback = new AjxCallback(this, this._msgLoadedCallback, [date, subject]);
		ZmMailMsg.fetchMsg({sender:appCtxt.getAppController(), msgId:mailItem.id, getHtml:false, callback:respCallback});
	}else {
		//create appt from conversation instance
		var conv = new ZmConv(mailItem.id, new ZmList(ZmItem.CONV));
		var respCallback = new AjxCallback(this, this._setAppt, [date, subject, conv]);
		conv.load({getFirstMsg: true}, respCallback);
	}
};

Com_Zimbra_CalScheduler.prototype._convLoadedCallback =
function(date, subject, conv) {
	var respCallback = new AjxCallback(this, this._setAppt, [date, subject]);
	conv.getFirstHotMsg({}, respCallback);
};

Com_Zimbra_CalScheduler.prototype._msgLoadedCallback =
function(date, subject, result) {

	if(!result) { return; }

	var node = result.getResponse().GetMsgResponse.m[0];

	if(!node) { return; }

	var msg = new ZmMailMsg(node.id, null, true);
	msg._loadFromDom(node);

	this._setAppt(date, subject, msg);
};

Com_Zimbra_CalScheduler.prototype._setAppt =
function(date, subject, msg) {

	if(!msg){ return; }

	var appViewMgr = appCtxt.getAppViewMgr();
	var calController = AjxDispatcher.run("GetCalController");

	if(msg instanceof ZmConv) {
		msg = msg.getFirstHotMsg();
	}

	var newAppt = calController._newApptObject(date);
	newAppt.setFromMailMessage(msg, subject);

	var resultView  = this._scheduleView
	if(!this._scheduleView){
		resultView  = this._scheduleView =this._createScheduleView();
	}else {
        resultView.cleanup();
        appViewMgr.pushView(ZmId.VIEW_CAL_SCHEDULE_ZIMLET);
    }
	resultView.setAppt(newAppt);
    resultView.showMe();
    resultView._updateDays();
    resultView._setAttendees(resultView._organizer, resultView._attendees);

};

// Panel Zimlet Methods
// Called by the Zimbra framework when the Ymaps panel item was double clicked
Com_Zimbra_CalScheduler.prototype.doubleClicked =
function() {
	this.singleClicked();
};


Com_Zimbra_CalScheduler.prototype.menuItemSelected = function(itemId) {
	switch (itemId) {
	    case "PREFERENCES":
		this.createPropertyEditor();
		break;

	}
};


Com_Zimbra_CalScheduler.prototype._closeListener =
function() {
	appCtxt.getAppViewMgr().popView(true);
};

Com_Zimbra_CalScheduler.prototype._newListener =
function() {
	appCtxt.getAppViewMgr().popView(true);

	this._scheduleView.createAppt();
};


CalSchedulerView = function(parent, controller) {
	if (arguments.length == 0) { return; }

	DwtComposite.call(this, {parent:parent, className:"CalSchedulerView", posStyle:Dwt.ABSOLUTE_STYLE});

	this.addControlListener(new AjxListener(this, this._controlListener));

	var dateInfo = this._dateInfo = {};

	var sd = new Date();
	var calController = AjxDispatcher.run("GetCalController");
	var appt = this._appt = calController._newApptObject(sd, AjxDateUtil.MSEC_PER_HALF_HOUR);
	dateInfo.timezone = appt.getTimezone();
	dateInfo.showTime = true;
	dateInfo.startHourIdx = dateInfo.startMinuteIdx = dateInfo.startAmPmIdx =
	dateInfo.endHourIdx = dateInfo.endMinuteIdx = dateInfo.endAmPmIdx = null;
    dateInfo.isAllDay = false;
	dateInfo.startDate = AjxDateUtil.simpleComputeDateStr(appt.startDate);
	dateInfo.endDate = AjxDateUtil.simpleComputeDateStr(appt.endDate);
    dateInfo.timezone = appt.getTimezone();


 	var organizer = this._organizer = new ZmContact(null);
	organizer.initFromEmail(ZmApptViewHelper.getOrganizerEmail(), true);

	// centralized attendee data
	this._attendees = {};
	this._attendees[ZmCalBaseItem.PERSON]	= new AjxVector();	// list of ZmContact
	this._attendees[ZmCalBaseItem.LOCATION]	= new AjxVector();	// list of ZmResource
	this._attendees[ZmCalBaseItem.EQUIPMENT]= new AjxVector();	// list of ZmResource

	this._controller = controller;
	this._dateInfo = dateInfo;

	//this._editView = parent.getTabPage(ZmApptComposeView.TAB_APPOINTMENT).getEditView();

	this._rendered = false;
	this._emailToIdx = {};
	this._schedTable = [];
	this._allAttendees = [];
	this._allAttendeesStatus = [];
	this._allAttendeesSlot = null;

	this._attTypes = [ZmCalBaseItem.PERSON];
	if (appCtxt.get(ZmSetting.GAL_ENABLED)) {
		this._attTypes.push(ZmCalBaseItem.LOCATION);
		this._attTypes.push(ZmCalBaseItem.EQUIPMENT);
	}

	this._fbCallback = new AjxCallback(this, this._handleResponseFreeBusy);
	this._kbMgr = appCtxt.getShell().getKeyboardMgr();

	//view related var
	this._columns = {};
	this._dateToDayIndex = {};
    this._currentFreeSlotIdx = 0;

};


CalSchedulerView.prototype = new DwtComposite;
CalSchedulerView.prototype.constructor = CalSchedulerView;


// Consts
CalSchedulerView.FREEBUSY_NUM_CELLS		= 48;

// Hold on to this one separately because we use it often
CalSchedulerView.FREE_CLASS = "ZmScheduler-free";

CalSchedulerView._DAY_HEADING_HEIGHT = 25;
CalSchedulerView.HOUR_COLUMN_WIDTH = 15;
CalSchedulerView.DAY_COLUMN_WIDTH = CalSchedulerView.HOUR_COLUMN_WIDTH*24;


// Public methods

CalSchedulerView.prototype.toString =
function() {
	return "CalSchedulerView";
};

CalSchedulerView.prototype.getStatusString =
function(status) {
	switch(status) {
		case ZmSchedTabViewPage.STATUS_FREE: return ZmMsg.free;
		case ZmSchedTabViewPage.STATUS_BUSY: return ZmMsg.busy;
		case ZmSchedTabViewPage.STATUS_TENTATIVE: return ZmMsg.tentative;
		case ZmSchedTabViewPage.STATUS_OUT: return ZmMsg.outOfOffice;
		case ZmSchedTabViewPage.STATUS_UNKNOWN: return ZmMsg.unknown;
	};
	return ZmMsg.unknown;
};


CalSchedulerView.prototype.createAppt =
function() {

	var appt = this._appt;
	var calController = AjxDispatcher.run("GetCalController");

	appt.setStartDate(this.getSelectedStartDate());
	appt.setEndDate(this.getSelectedEndDate());

	appt._attendees[ZmCalBaseItem.PERSON]	= [];
	appt._attendees[ZmCalBaseItem.LOCATION]	= [];
	appt._attendees[ZmCalBaseItem.EQUIPMENT]= [];

	for(var i in this._schedTable) {
		var sched = this._schedTable[i];
		if(sched && sched.attendee && sched.attType && !sched.isOrganizer) {
			appt._attendees[sched.attType].push(sched.attendee);
		}
	}

	calController.newAppointment(appt, ZmCalItem.MODE_NEW);
};

CalSchedulerView.prototype.resetAppt =
function() {
	var sd = new Date();
	var calController = AjxDispatcher.run("GetCalController");
	if(sd) {
		this._appt = calController._newApptObject(sd, AjxDateUtil.MSEC_PER_HALF_HOUR);
	}
};

CalSchedulerView.prototype.showMe =
function() {

	//ZmApptViewHelper.getDateInfo(this._editView, this._dateInfo);
	this._dateBorder = this._getBordersFromDateInfo(this._dateInfo);

	if (!this._rendered) {
		this._initialize();
	}

	this.set(this._dateInfo, this._organizer, this._attendees);
	this._updateTimeGrid(true);
};

CalSchedulerView.prototype.tabBlur =
function(useException) {
	if (this._activeInputIdx != null) {
		var inputEl = this._schedTable[this._activeInputIdx].inputObj.getInputElement();
		this._handleAttendeeField(inputEl, null, useException);
		this._activeInputIdx = null;
	}
	if (this._activeDateField) {
		this._handleDateChange(this._activeDateField == this._startDateField);
	}
};

CalSchedulerView.prototype.initialize =
function(appt, mode) {
	this._appt = appt;
	this._mode = mode;
};

CalSchedulerView.prototype.set =
function(dateInfo, organizer, attendees) {
	this._startDateField.value = dateInfo.startDate;
	this._endDateField.value = dateInfo.endDate;
	if (dateInfo.showTime) {
		this._allDayCheckbox.checked = false;
		this._showTimeFields(true);
		this._startTimeSelect.setSelected(dateInfo.startHourIdx, dateInfo.startMinuteIdx, dateInfo.startAmPmIdx);
		this._endTimeSelect.setSelected(dateInfo.endHourIdx, dateInfo.endMinuteIdx, dateInfo.endAmPmIdx);
	} else {
		this._allDayCheckbox.checked = true;
		this._showTimeFields(false);
	}
	this._resetFullDateField();


    this._initTzSelect();
    this._resetTimezoneSelect(dateInfo);

	this._outlineAppt(this._dateInfo);
};

CalSchedulerView.prototype.setAppt =
function(appt) {
	var dateInfo = this._dateInfo = {};

	var calController = AjxDispatcher.run("GetCalController");
	this._appt = appt;

	dateInfo.timezone = appt.getTimezone();
	dateInfo.showTime = true;
	dateInfo.startHourIdx = dateInfo.startMinuteIdx = dateInfo.startAmPmIdx =
	dateInfo.endHourIdx = dateInfo.endMinuteIdx = dateInfo.endAmPmIdx = null;
    dateInfo.isAllDay = false;
	dateInfo.startDate = AjxDateUtil.simpleComputeDateStr(appt.startDate);
	dateInfo.endDate = AjxDateUtil.simpleComputeDateStr(appt.endDate);
    dateInfo.timezone = appt.getTimezone();

 	var organizer = this._organizer = new ZmContact(null);
	organizer.initFromEmail(ZmApptViewHelper.getOrganizerEmail(appt.organizer), true);

	// centralized attendee data
	this._attendees = {};
	this._attendees[ZmCalBaseItem.PERSON]	= AjxVector.fromArray(appt.getAttendees(ZmCalBaseItem.PERSON));
	this._attendees[ZmCalBaseItem.LOCATION]	= AjxVector.fromArray(appt.getAttendees(ZmCalBaseItem.LOCATION));
	this._attendees[ZmCalBaseItem.EQUIPMENT]= AjxVector.fromArray(appt.getAttendees(ZmCalBaseItem.EQUIPMENT));

};

CalSchedulerView.prototype.cleanup =
function() {
	if (!this._rendered) return;

	// remove all but first two rows (header and All Attendees)
	while (this._attendeesTable.rows.length > 2) {
		this._removeAttendeeRow(2);
	}
	this._activeInputIdx = null;

    for (var i in this._emailToIdx) {
		delete this._emailToIdx[i];
	}

	this._curValStartDate = "";
	this._curValEndDate = "";

    // reset autocomplete lists
	if (this._acContactsList) {
		this._acContactsList.reset();
		this._acContactsList.show(false);
	}
	if (this._acEquipmentList) {
		this._acEquipmentList.reset();
		this._acEquipmentList.show(false);
	}

    this._cleanupAllAttendeeSlots();
   	// centralized attendee data
	this._attendees = {};
	this._attendees[ZmCalBaseItem.PERSON]	= new AjxVector();	// list of ZmContact
	this._attendees[ZmCalBaseItem.LOCATION]	= new AjxVector();	// list of ZmResource
	this._attendees[ZmCalBaseItem.EQUIPMENT]= new AjxVector();	// list of ZmResource

};

CalSchedulerView.prototype.isDirty =
function() {
	return false;
};

CalSchedulerView.prototype.isValid =
function() {
	return true;
};


CalSchedulerView.prototype._controlListener =
function(ev) {

	if (!this._rendered) return;

	if (ev.newWidth == Dwt.DEFAULT && ev.newHeight == Dwt.DEFAULT) return;
	try {
		if ((ev.oldWidth != ev.newWidth) || (ev.oldHeight != ev.newHeight)) {
			this._updateTimeGrid(true);
			this._refreshFreeBusyUI();
		}
	} catch(ex) {
		DBG.dumpObj(ex);
	}
};

CalSchedulerView.prototype.toggleAllDayField =
function() {
	var el = this._allDayCheckbox;
	el.checked = !el.checked;
	this._showTimeFields(!el.checked);
	//this._editView.updateAllDayField(el.checked);
	this._outlineAppt();
};

// Private / protected methods

CalSchedulerView.prototype._initialize =
function() {
	this._createHTML();
	this._initAutocomplete();
	this._createDwtObjects();
	this._addEventHandlers();

	this._rendered = true;
};

CalSchedulerView.prototype._createHTML =
function() {
	this._startDateFieldId 	= this._htmlElId + "_startDateField";
	this._startMiniCalBtnId = this._htmlElId + "_startMiniCalBtn";
	this._startTimeSelectId = this._htmlElId + "_startTimeSelect";
	this._startTimeAtLblId	= this._htmlElId + "_startTimeAtLbl";
	this._allDayCheckboxId 	= this._htmlElId + "_allDayCheckbox";
	this._endDateFieldId 	= this._htmlElId + "_endDateField";
	this._endMiniCalBtnId 	= this._htmlElId + "_endMiniCalBtn";
	this._endTimeSelectId 	= this._htmlElId + "_endTimeSelect";
    this._endTimeAtLblId	= this._htmlElId + "_endTimeAtLbl";
    this._tzoneSelectId	    = this._htmlElId + "_tzoneSelect";
	this._navToolbarId		= this._htmlElId + "_navToolbar";
	this._attendeesTableId	= this._htmlElId + "_attendeesTable";
	this._autoPickBtnId		= this._htmlElId + "_autoPickCell";
	this._autoPickBtnId		= this._htmlElId + "_autoPickCell";
	this._durationSelectId	= this._htmlElId + "_durationCell";

	this._schedTable[0] = null;	// header row has no attendee data

	var subs = { id:this._htmlElId, isAppt: true, showTZSelector: appCtxt.get(ZmSetting.CAL_SHOW_TIMEZONE) };
	this.getHtmlElement().innerHTML = AjxTemplate.expand("com_zimbra_calscheduler.templates.CalScheduler#CalSchedulerView_Main", subs);

	//this._initColumns();
};

CalSchedulerView.prototype._initAutocomplete =
function() {
	var shell = appCtxt.getShell();
	var acCallback = new AjxCallback(this, this._autocompleteCallback);
	var keyUpCallback = new AjxCallback(this, this._autocompleteKeyUpCallback);
	this._acList = {};

	// autocomplete for attendees
	if (appCtxt.get(ZmSetting.CONTACTS_ENABLED)) {
		var contactsClass = appCtxt.getApp(ZmApp.CONTACTS);
		var contactsLoader = contactsClass.getContactList;
		var params = {parent: shell, dataClass: contactsClass, dataLoader: contactsLoader, separator: "",
					  matchValue: ZmContactsApp.AC_VALUE_NAME, keyUpCallback: keyUpCallback, compCallback: acCallback, smartPos: true};
		this._acContactsList = new ZmAutocompleteListView(params);
		this._acList[ZmCalBaseItem.PERSON] = this._acContactsList;
	}
	// autocomplete for locations/equipment
	if (appCtxt.get(ZmSetting.GAL_ENABLED)) {
		var resourcesClass = appCtxt.getApp(ZmApp.CALENDAR);
		var params = {parent: shell, dataClass: resourcesClass, dataLoader: resourcesClass.getLocations, separator: "",
					  matchValue: ZmContactsApp.AC_VALUE_NAME, compCallback: acCallback, smartPos: true};
		this._acLocationsList = new ZmAutocompleteListView(params);
		this._acList[ZmCalBaseItem.LOCATION] = this._acLocationsList;
		params.dataLoader = resourcesClass.getEquipment;
		this._acEquipmentList = new ZmAutocompleteListView(params);
		this._acList[ZmCalBaseItem.EQUIPMENT] = this._acEquipmentList;
	}
};

// Add the attendee, then create a new empty slot since we've now filled one.
CalSchedulerView.prototype._autocompleteCallback =
function(text, el, match) {
	if (match && match.item) {
		if (match.item.isGroup()) {
			var members = match.item.getGroupMembers().good.getArray();
			for (var i = 0; i < members.length; i++) {
				el.value = members[i].address;
				var index = this._handleAttendeeField(el);

				if (index && ((i+1) < members.length)) {
					el = this._schedTable[index].inputObj.getInputElement();
				}
			}
		} else {
			this._handleAttendeeField(el, match.item);
		}
	}
};

// Enter listener. If the user types a return when no autocomplete list is showing,
// then go ahead and add a new empty slot.
CalSchedulerView.prototype._autocompleteKeyUpCallback =
function(ev, aclv, result) {
	var key = DwtKeyEvent.getCharCode(ev);
	if ((key == 3 || key == 13) && !aclv.getVisible()) {
		var el = DwtUiEvent.getTargetWithProp(ev, "id");
		this._handleAttendeeField(el);
	}
};

CalSchedulerView.prototype._addTabGroupMembers =
function(tabGroup) {
	for (var i = 0; i < this._schedTable.length; i++) {
		var sched = this._schedTable[i];
		if (sched && sched.inputObj) {
			tabGroup.addMember(sched.inputObj);
		}
	}
};

/*
* Adds a new, empty slot with a select for the attendee type, an input field, and
* cells for free/busy info.
*
* @param isAllAttendees		[boolean]*	if true, this is the "All Attendees" row
* @param organizer			[string]*	organizer
* @param drawBorder			[boolean]*	if true, draw borders to indicate appt time
* @param index				[int]*		index at which to add the row
* @param updateTabGroup		[boolean]*	if true, add this row to the tab group
* @param setFocus			[boolean]*	if true, set focus to this row's input field
*/
CalSchedulerView.prototype._addAttendeeRow =
function(isAllAttendees, organizer, drawBorder, index, updateTabGroup, setFocus) {
	index = index || this._attendeesTable.rows.length;

	// store some meta data about this table row
	var sched = {};
	var dwtId = Dwt.getNextId();	// container for input
	sched.dwtNameId		= dwtId + "_NAME_";			// TD that contains name
	sched.dwtTableId	= dwtId + "_TABLE_";		// TABLE with free/busy cells
	sched.dwtSelectId	= dwtId + "_SELECT_";		// TD that contains select menu
	sched.dwtInputId	= dwtId + "_INPUT_";		// input field
	sched.idx = index;
	sched._coloredCells = [];
	sched._statusDivs = [];
	sched.isAllAttendees = isAllAttendees;
	sched.isOrganizer = organizer;
	this._schedTable[index] = sched;

    var data = {
        id: dwtId,
        sched: sched,
        isAllAttendees: isAllAttendees,
        organizer: organizer,
        cellCount: CalSchedulerView.FREEBUSY_NUM_CELLS
    };

	var tr = this._attendeesTable.insertRow(index);
	var td = tr.insertCell(-1);
    td.innerHTML = AjxTemplate.expand("calendar.Appointment#AttendeeName", data);
	sched.attendeeCell = td;

	if(isAllAttendees) {
		this._allAttendeesSched = sched;
		this._allAttendeesHeadingCell = td;
	}

	// create DwtInputField and DwtSelect for the attendee slots, add handlers
	if (!isAllAttendees && !organizer) {
		// add DwtSelect
		var select;
		var selectId = sched.dwtSelectId;
		var selectDiv = document.getElementById(selectId);
		if (selectDiv) {
			select = new DwtSelect({parent:this});
			select.addOption(new DwtSelectOption(ZmCalBaseItem.PERSON, true, null, null, null, "Person"));
			select.addOption(new DwtSelectOption(ZmCalBaseItem.LOCATION, false, null, null, null, "Location"));
			select.addOption(new DwtSelectOption(ZmCalBaseItem.EQUIPMENT, false, null, null, null, "Resource"));
			select.reparentHtmlElement(selectId);
			select.addChangeListener(this._selectChangeListener);
			select.setSize("50");
			select._schedTableIdx = index;
			sched.selectObj = select;
		}
		// add DwtInputField
		var nameDiv = document.getElementById(sched.dwtNameId);
		if (nameDiv) {
			var dwtInputField = new DwtInputField({parent: this, type: DwtInputField.STRING, maxLen: 256});
			dwtInputField.setDisplay(Dwt.DISPLAY_INLINE);
			var inputEl = dwtInputField.getInputElement();
			inputEl.className = "ZmSchedulerInput";
			inputEl.id = sched.dwtInputId;
			sched.attType = inputEl._attType = ZmCalBaseItem.PERSON;
			sched.inputObj = dwtInputField;
			if (select) {
				select.dwtInputField = dwtInputField;
			}
			dwtInputField.reparentHtmlElement(sched.dwtNameId);
		}

		sched.ptstObj = document.getElementById(sched.dwtNameId+"_ptst");

		// set handlers
		var attendeeInput = document.getElementById(sched.dwtInputId);
		if (attendeeInput) {
			this._activeInputIdx = null;
			this._activeInputIdx = index;
			// handle focus moving to/from an enabled input
			Dwt.setHandler(attendeeInput, DwtEvent.ONFOCUS, CalSchedulerView._onFocus);
			Dwt.setHandler(attendeeInput, DwtEvent.ONBLUR, CalSchedulerView._onBlur);
			attendeeInput._schedViewPageId = this._svpId;
			attendeeInput._schedTableIdx = index;
			// default to person-based autocomplete handling
			if (this._acContactsList) {
				this._acContactsList.handle(attendeeInput);
			}
		}
	}

	if (drawBorder) {
		this._updateBorders(sched, isAllAttendees);
	}
	if (updateTabGroup) {
		//this._controller._setComposeTabGroup();
	}
	if (setFocus && sched.inputObj) {
		this._kbMgr.grabFocus(sched.inputObj);
	}

	this._updateTimeGrid();
	return index;
};

CalSchedulerView.prototype._removeAttendeeRow =
function(index, updateTabGroup) {
	var sched = this._schedTable[index];
	this._clearColoredCells(sched);
	this._attendeesTable.deleteRow(index);

	if(sched && sched.uid && this._emailToIdx[sched.uid]) {
		delete this._emailToIdx[sched.uid];
	}
	this._schedTable.splice(index, 1);

	this._updateTimeGrid();
	this._refreshFreeBusyUI();

	if (updateTabGroup) {
		//this._controller._setComposeTabGroup(true);
	}
};

CalSchedulerView.prototype._createDwtObjects =
function() {
    var timezoneListener = new AjxListener(this, this._timezoneListener);

    this._tzoneSelect = new DwtSelect({parent:this});
	this._tzoneSelect.reparentHtmlElement(this._tzoneSelectId);
    this._tzoneSelect.addChangeListener(timezoneListener);
    // NOTE: tzone select is initialized later
    delete this._tzoneSelectId;

	var timeSelectListener = new AjxListener(this, this._timeChangeListener);

	this._startTimeSelect = new ZmTimeSelect(this, ZmTimeSelect.START);
	this._startTimeSelect.reparentHtmlElement(this._startTimeSelectId);
	this._startTimeSelect.addChangeListener(timeSelectListener);
	delete this._startTimeSelectId;

	this._endTimeSelect = new ZmTimeSelect(this, ZmTimeSelect.END);
	this._endTimeSelect.addChangeListener(timeSelectListener);
	this._endTimeSelect.reparentHtmlElement(this._endTimeSelectId);
	delete this._endTimeSelectId;

	// create mini calendar buttons
	var dateButtonListener = new AjxListener(this, this._dateButtonListener);
	var dateCalSelectionListener = new AjxListener(this, this._dateCalSelectionListener);

	this._startDateButton = ZmCalendarApp.createMiniCalButton(this, this._startMiniCalBtnId, dateButtonListener, dateCalSelectionListener);
	this._endDateButton = ZmCalendarApp.createMiniCalButton(this, this._endMiniCalBtnId, dateButtonListener, dateCalSelectionListener);

	var navBarListener = new AjxListener(this, this._navBarListener);
	this._navToolbar = new ZmNavToolBar({parent:this, context:ZmId.VIEW_APPT_SCHEDULE});
	this._navToolbar._textButton.getHtmlElement().className = "ZmSchedulerDate";
	this._navToolbar.addSelectionListener(ZmOperation.PAGE_BACK, navBarListener);
	this._navToolbar.addSelectionListener(ZmOperation.PAGE_FORWARD, navBarListener);
	this._navToolbar.reparentHtmlElement(this._navToolbarId);
	delete this._navToolbarId;

	this._freeBusyDiv = document.getElementById(this._freeBusyDivId);
	delete this._freeBusyDivId;

	this._startDateField 	= document.getElementById(this._startDateFieldId);
	this._endDateField 		= document.getElementById(this._endDateFieldId);
	this._allDayCheckbox 	= document.getElementById(this._allDayCheckboxId);

	this._curValStartDate = "";
	this._curValEndDate = "";

	// add All Attendees row
	this._svpId = AjxCore.assignId(this);
	this._attendeesTable = document.getElementById(this._attendeesTableId);
	this._allAttendeesIndex = this._addAttendeeRow(true, null, false);
	this._allAttendeesSlot = this._schedTable[this._allAttendeesIndex];
	this._allAttendeesTable = document.getElementById(this._allAttendeesSlot.dwtTableId);

	this._selectChangeListener = new AjxListener(this, this._selectChangeListener);

	this._autoPickBtn = new DwtButton({parent:this});
	this._autoPickBtn.setText(ZmMsg.autoPick);
	this._autoPickBtn.setImage("GroupSchedule");
	this._autoPickBtn.addSelectionListener(new AjxListener(this, this._autoPickListener));
	this._autoPickBtn.reparentHtmlElement(this._autoPickBtnId);

	this._durationSelect = new DwtSelect({parent:this});
	this._durationSelect.reparentHtmlElement(this._durationSelectId);
	var durationListener = new AjxListener(this, this._durationListener);
    this._durationSelect.addChangeListener(durationListener);

	//todo: move this fully to zmmsg.properties
	var displayOptions = [AjxMsg.minutes, AjxMsg.minutes, AjxMsg.minutes, AjxMsg.minutes, AjxMsg.minutes, AjxMsg.hour, AjxMsg.hours, AjxMsg.hours, AjxMsg.hours];
	var	options = [10, 20, 30, 40, 50, 60, 120, 180, 240];
	var	labels = [10, 20, 30, 40, 50, 1, 2, 3, 4];

	for (var j = 0; j < options.length; j++) {
		var optLabel = ZmCalendarApp.__formatLabel("{0} " + displayOptions[j], labels[j]);
		this._durationSelect.addOption(optLabel, (60 == options[j]), options[j]);
	}
};

CalSchedulerView.prototype._initTzSelect = function() {
    // XXX: this seems like overkill, list all timezones!?
    var options = AjxTimezone.getAbbreviatedZoneChoices();
    if (options.length != this._tzCount) {
        this._tzCount = options.length;
        this._tzoneSelect.clearOptions();
        for (var i = 0; i < options.length; i++) {
            this._tzoneSelect.addOption(options[i]);
        }
    }
};

CalSchedulerView.prototype._addEventHandlers =
function() {
	Dwt.setHandler(this._allDayCheckbox, DwtEvent.ONCLICK, CalSchedulerView._onClick);
	this._allDayCheckbox._schedViewPageId = this._svpId;

	Dwt.setHandler(this._startDateField, DwtEvent.ONCLICK, CalSchedulerView._onClick);
	Dwt.setHandler(this._endDateField, DwtEvent.ONCLICK, CalSchedulerView._onClick);
	Dwt.setHandler(this._startDateField, DwtEvent.ONBLUR, CalSchedulerView._onBlur);
	Dwt.setHandler(this._endDateField, DwtEvent.ONBLUR, CalSchedulerView._onBlur);
	this._startDateField._schedViewPageId = this._endDateField._schedViewPageId = this._svpId;
};

CalSchedulerView.prototype._showTimeFields =
function(show) {
	Dwt.setVisibility(this._startTimeSelect.getHtmlElement(), show);
	Dwt.setVisibility(this._endTimeSelect.getHtmlElement(), show);
    this._setTimezoneVisible(this._dateInfo);

	// also show/hide the "@" text
	Dwt.setVisibility(document.getElementById(this._startTimeAtLblId), show);
	Dwt.setVisibility(document.getElementById(this._endTimeAtLblId), show);
};

/*
* Called by ONBLUR handler for attendee input field.
*/
CalSchedulerView.prototype._handleAttendeeField =
function(inputEl, attendee, useException) {

	var idx = inputEl._schedTableIdx;
	if (idx != this._activeInputIdx) return;

	var sched = this._schedTable[idx];
	if (!sched) return;
	var input = sched.inputObj;
	if (!input) return;

	var value = input.getValue();
	if (value) {
		value = AjxStringUtil.trim(value.replace(/[;,]$/, ""));	// trim separator, white space
	}
	var curAttendee = sched.attendee;
	var type = sched.attType;

	if (value) {
		if (curAttendee) {
			// user edited slot with an attendee in it
			var attText = AjxStringUtil.trim(curAttendee.getAttendeeText(type, true));
			if (value == attText) {
				return;
			} else {
				this._resetRow(sched, false, type, true);
			}
		}
		attendee = attendee ? attendee : ZmApptViewHelper.getAttendeeFromItem(value, type, true);
		if (attendee) {
			var email = attendee.getEmail();

			//todo: confirm
			if(this._emailToIdx[email] != null) {
				var dialog = appCtxt.getMsgDialog();
				var msg = AjxMessageFormat.format(ZmMsg.duplicateAttendee, [email]);
				dialog.setMessage(msg, DwtMessageDialog.INFO_STYLE);
				dialog.popup();
				input.setValue("");
				return;
			}

			this._emailToIdx[email] = idx;
			// go get this attendee's free/busy info if we haven't already
			if (sched.uid != email) {
				this._getFreeBusyInfo(this._getStartTime(), email, this._fbCallback);
			}
			sched.attendee = attendee;
			this._setAttendeeToolTip(sched, attendee);
			//this.parent.updateAttendees(attendee, type, ZmApptComposeView.MODE_ADD);
			if (!curAttendee) {
				// user added attendee in empty slot
				return this._addAttendeeRow(false, null, true, null, true, true); // add new empty slot
			}
		} else {
			this._activeInputIdx = null;
		}
	} else if (curAttendee) {
		// user erased an attendee
		this._resetRow(sched, false, type);
		this._removeAttendeeRow(idx, true);
	}
};

CalSchedulerView.prototype._setAttendeeToolTip =
function(sched, attendee, type) {
	if (type != ZmCalBaseItem.PERSON) return;

	var name = attendee.getFullName();
	var email = attendee.getEmail();
	if (name && email) {
        var ptst = ZmMsg.attendeeStatusLabel + ZmCalItem.getLabelForParticipationStatus(attendee.getAttr("participationStatus") || "NE");
        sched.inputObj.setToolTipContent(email +"<br>"+ ptst);
	}
};

CalSchedulerView.prototype._getStartTime =
function() {
	var startDate = this._days[0].date;
	if (!this._allDayCheckbox.checked) {
		startDate.setHours(0, 0, 0, 0);
	}
	return startDate.getTime();
};

CalSchedulerView.prototype._getEndTime =
function() {
	// XXX: always get start date field value since we dont support multiday yet
	//var ed = this._endDateField.value;
	var endDate = AjxDateUtil.simpleParseDateStr(this._endDateField.value);
	if (!this._allDayCheckbox.checked) {
		endDate.setHours(23, 59, 0, 0);
	}
	return endDate.getTime();
};

CalSchedulerView.prototype._cleanupAllAttendeeSlots =
function() {
	DBG.println("<font color=red>clean up all attendee slots</font>");
	delete this._allAttendeeSlots;
	delete this._allAttendeeResolvedSlots;
};


CalSchedulerView.prototype._updateFreeBusy =
function() {
	this._updateDays();
	this._cleanupAllAttendeeSlots();
	// update the full date field
	this._resetFullDateField();

	// clear the schedules for existing attendees
	var uids = [];
	for (var i = 0; i < this._schedTable.length; i++) {
		var sched = this._schedTable[i];
		if (!sched) continue;
		if (sched.uid)
			uids.push(sched.uid);
		while (sched._coloredCells && sched._coloredCells.length > 0) {
			sched._coloredCells[0].className = CalSchedulerView.FREE_CLASS;
			sched._coloredCells.shift();
		}

	}

	if (uids.length) {
		var emails = uids.join(",");
		this._getFreeBusyInfo(this._getStartTime(), emails, this._fbCallback);
	}
};

// XXX: optimize later - currently we always update the f/b view :(
CalSchedulerView.prototype._setAttendees =
function(organizer, attendees) {

	this.cleanup();
	var emails = [];
	// create a slot for the organizer
	this._organizerIndex = this._addAttendeeRow(false, organizer.getAttendeeText(ZmCalBaseItem.PERSON, true), false);
	emails.push(this._setAttendee(this._organizerIndex, organizer, ZmCalBaseItem.PERSON, true));

	// create slots for each of the other attendees/resources
	for (var t = 0; t < this._attTypes.length; t++) {
		var type = this._attTypes[t];
		var att = attendees[type].getArray();
		for (var i = 0; i < att.length; i++) {
			var email = att[i] ? att[i].getEmail() : null;
			if (email && !this._emailToIdx[email]) {
				var index = this._addAttendeeRow(false, null, false); // create a slot for this attendee
				emails.push(this._setAttendee(index, att[i], type, false));
			}
		}
	}

	// make sure there's always an empty slot
	this._addAttendeeRow(false, null, false, null, true, true);

	if (emails.length) {
		this._getFreeBusyInfo(this._getStartTime(), emails.join(","), this._fbCallback);
	}
};

CalSchedulerView.prototype._setAttendee =
function(index, attendee, type, isOrganizer) {
	var sched = this._schedTable[index];
	if (!sched) return;

	sched.attendee = attendee;
	sched.attType = type;
	var input = sched.inputObj;
	if (input) {
		input.setValue(attendee.getAttendeeText(type, true), true);
		this._setAttendeeToolTip(sched, attendee, type);
	}

	var select = sched.selectObj;
	if (select) {
		select.setSelectedValue(type);
	}

    var ptst = attendee.getAttr("participationStatus") || "NE";
	var ptstCont = sched.ptstObj;
	if(ptstCont) {
		var ptstIcon = ZmCalItem.getParticipationStatusIcon(ptst);
		if(ptstIcon != "") {
			var ptstLabel = ZmMsg.attendeeStatusLabel + " " + ZmCalItem.getLabelForParticipationStatus(ptst);
			ptstCont.innerHTML = AjxImg.getImageHtml(ptstIcon);
			var imgDiv = ptstCont.firstChild;
			if(imgDiv && !imgDiv._schedViewPageId ){
				Dwt.setHandler(imgDiv, DwtEvent.ONMOUSEOVER, CalSchedulerView._onPTSTMouseOver);
				Dwt.setHandler(imgDiv, DwtEvent.ONMOUSEOUT, CalSchedulerView._onPTSTMouseOut);
				imgDiv._ptstLabel = ptstLabel;
				imgDiv._schedViewPageId = this._svpId;
				imgDiv._schedTableIdx = index;
			}
		}
	}

	var email = attendee.getEmail();
	this._emailToIdx[email] = index;

	return email;
};

/*
* Resets a row to its starting state. The input is cleared and removed, and
* the free/busy blocks are set back to their default color. Optionally, the
* select is set back to person.
*
* @param sched			[object]		info for this row
* @param resetSelect	[boolean]*		if true, set select to PERSON
* @param type			[constant]*		attendee type
* @param noClear		[boolean]*		if true, don't clear input field
*/
CalSchedulerView.prototype._resetRow =
function(sched, resetSelect, type, noClear) {

	var input = sched.inputObj;
	if (sched.attendee && type) {
		//this.parent.updateAttendees(sched.attendee, type, ZmApptComposeView.MODE_REMOVE);
		if (input) {
			input.setToolTipContent(null);
		}
		sched.attendee = null;
	}

	// clear input field
	if (input && !noClear) {
		input.setValue("", true);
	}

	// reset the row color to non-white
	var table = document.getElementById(sched.dwtTableId);
	if (table) {
		table.rows[0].className = "ZmSchedulerDisabledRow";
	}

	// remove the bgcolor from the cells that were colored
	this._clearColoredCells(sched);

	// reset the select to person
	if (resetSelect) {
		var select = AjxCore.objectWithId(sched.selectObjId);
		if (select) {
			select.setSelectedValue(ZmCalBaseItem.PERSON);
		}
	}

	sched.uid = null;
	this._activeInputIdx = null;
};

CalSchedulerView.prototype._resetTimezoneSelect =
function(dateInfo) {
    this._tzoneSelect.setSelectedValue(dateInfo.timezone);
};
CalSchedulerView.prototype._setTimezoneVisible =
function(dateInfo) {
    var showTimezone = !dateInfo.isAllDay;
    if (showTimezone) {
        showTimezone = appCtxt.get(ZmSetting.CAL_SHOW_TIMEZONE) ||
                       dateInfo.timezone != AjxTimezone.getServerId(AjxTimezone.DEFAULT);
    }
    Dwt.setVisibility(this._tzoneSelect.getHtmlElement(), showTimezone);
};

CalSchedulerView.prototype._clearColoredCells =
function(sched) {
	if(false && sched.isAllAttendees) {
		delete this._allAttendeeSlots;
		delete this._allAttendeeResolvedSlots;
	}

	while(sched._statusDivs && sched._statusDivs.length > 0) {
		var div = sched._statusDivs.shift();
		if(div && div.parentNode) {
			div.parentNode.removeChild(div);
		}
	}
};;

CalSchedulerView.prototype._resetFullDateField =
function() {
	var formatter = AjxDateFormat.getDateInstance(AjxDateFormat.MEDIUM);
	this._navToolbar.setText(formatter.format(AjxDateUtil.simpleParseDateStr(this._startDateField.value)));
};

CalSchedulerView.prototype._handleDateChange =
function(isStartDate, skipCheck) {
	var start = this._startDateField.value;
	var end = this._endDateField.value;
	if ((isStartDate && (start == this._curValStartDate)) ||
		(!isStartDate && (end == this._curValEndDate))) {
		return;
	}

	if (isStartDate) {
		this._curValStartDate = start;
	} else {
		this._curValEndDate = end;
	}
	var needsUpdate = ZmApptViewHelper.handleDateChange(this._startDateField, this._endDateField, isStartDate, skipCheck);
	if (needsUpdate) {
		this._updateFreeBusy();
	}
	// finally, update the appt tab view page w/ new date(s)
	//this._editView.updateDateField(this._startDateField.value, this._endDateField.value);
};

// Listeners

// XXX: refactor this code since ZmApptTabViewPage uses similar?
CalSchedulerView.prototype._dateButtonListener =
function(ev) {
	var calDate = ev.item == this._startDateButton
		? AjxDateUtil.simpleParseDateStr(this._startDateField.value)
		: AjxDateUtil.simpleParseDateStr(this._endDateField.value);

	// if date was input by user and its foobar, reset to today's date
	if (isNaN(calDate)) {
		calDate = new Date();
		var field = ev.item == this._startDateButton
			? this._startDateField : this._endDateField;
		field.value = AjxDateUtil.simpleComputeDateStr(calDate);
	}

	// always reset the date to current field's date
	var menu = ev.item.getMenu();
	var cal = menu.getItem(0);
	cal.setDate(calDate, true);
	ev.item.popup();
};

// XXX: refactor this code since ZmApptTabViewPage uses similar?
CalSchedulerView.prototype._dateCalSelectionListener =
function(ev) {
	var parentButton = ev.item.parent.parent;

	// update the appropriate field w/ the chosen date
	var field = (parentButton == this._startDateButton)
		? this._startDateField : this._endDateField;
	field.value = AjxDateUtil.simpleComputeDateStr(ev.detail);

	this._clearFreeSelections();
	// change the start/end date if they mismatch
	this._handleDateChange(parentButton == this._startDateButton, true);
};

CalSchedulerView.prototype._navBarListener =
function(ev) {
	var op = ev.item.getData(ZmOperation.KEY_ID);

	var sd = AjxDateUtil.simpleParseDateStr(this._startDateField.value);
	var ed = AjxDateUtil.simpleParseDateStr(this._endDateField.value);

	var newSd = op == ZmOperation.PAGE_BACK ? sd.getDate()-1 : sd.getDate()+1;
	var newEd = op == ZmOperation.PAGE_BACK ? ed.getDate()-1 : ed.getDate()+1;

	sd.setDate(newSd);
	ed.setDate(newEd);

	this._startDateField.value = AjxDateUtil.simpleComputeDateStr(sd);
	this._endDateField.value = AjxDateUtil.simpleComputeDateStr(ed);

	var numDays = this.getNumDays();
	var startTime = this._days[0].date.getTime();
	var endTime = this._days[numDays-1].endDate.getTime();

	if(sd.getTime() < startTime || sd.getTime() > endTime) {
		this._updateFreeBusy();
	}else{
		this._resetFullDateField();
	}

	// finally, update the appt tab view page w/ new date(s)
	//this._editView.updateDateField(this._startDateField.value, this._endDateField.value);
};

CalSchedulerView.prototype._timeChangeListener =
function(ev) {
	this._activeDateField = ZmTimeSelect.adjustStartEnd(ev, this._startTimeSelect, this._endTimeSelect,
														this._startDateField, this._endDateField);
	ZmApptViewHelper.getDateInfo(this, this._dateInfo);
	this._dateBorder = this._getBordersFromDateInfo(this._dateInfo);
	this._outlineAppt(this._dateInfo);
	this._clearFreeSelections();
	//this._editView.updateTimeField(this._dateInfo);
};

CalSchedulerView.prototype._timezoneListener =
function(ev) {
    ZmApptViewHelper.getDateInfo(this, this._dateInfo);
    this._dateBorder = this._getBordersFromDateInfo(this._dateInfo);
    this._outlineAppt(this._dateInfo);
  	this._clearFreeSelections();
    //this._editView.updateTimezone(this._dateInfo);
    this._updateFreeBusy();
};

CalSchedulerView.prototype._durationListener =
function(ev) {
	if(this._currentFreeSlot) {
		var slot = this._currentFreeSlot;
		slot.e = slot.s + (this._durationSelect.getValue()*60*1000);

		var numDays = this.getNumDays();
		var startTime = this._days[0].date.getTime();
		var endTime = this._days[numDays-1].endDate.getTime();

		this._positionFreeStatusDiv(slot, startTime, endTime);
	}
};

CalSchedulerView.prototype._selectChangeListener =
function(ev) {
	var select = ev._args.selectObj;
	if (!select) return;

	var svp = select.parent;
	var type = select.getValue();
	var sched = svp._schedTable[select._schedTableIdx];
	if (sched.attType == type) return;

	// reset row
	var input = sched.inputObj;
	input.setValue("", true);
	svp._clearColoredCells(sched);

	// if we wiped out an attendee, make sure it's reflected in master list
	if (sched.attendee) {
		//this.parent.updateAttendees(sched.attendee, sched.attType, ZmApptComposeView.MODE_REMOVE);
		sched.attendee = null;
	}
	sched.attType = type;

	// reset autocomplete handler
	var inputEl = input.getInputElement();
	if (type == ZmCalBaseItem.PERSON && svp._acContactsList) {
		svp._acContactsList.handle(inputEl);
	} else if (type == ZmCalBaseItem.LOCATION && svp._acLocationsList) {
		svp._acLocationsList.handle(inputEl);
	} else if (type == ZmCalBaseItem.EQUIPMENT && svp._acEquipmentList) {
		svp._acEquipmentList.handle(inputEl);
	}
};


CalSchedulerView.prototype._createStatusDiv =
function(sched, fbStatus) {
	var div = document.createElement("div");
	Dwt.setPosition(div, Dwt.ABSOLUTE_STYLE);
	div.className = this._getClassForStatus(fbStatus);
	div._schedViewPageId = this._svpId

	sched._statusDivs.push(div);
	this._timeGrid.appendChild(div);
	return div;
};

CalSchedulerView.prototype._colorSchedule =
function(sched, slots, status) {
		DBG.println("<u>colorSchedule:</u>"+sched.uid);
		var className = this._getClassForStatus(status);

		var gridLoc = Dwt.toWindow(this._timeGrid, null, null, this.getHtmlElement());
		var gridSize = Dwt.getSize(this._timeGrid);
		var numDays = this.getNumDays();
		var startTime = this._days[0].date.getTime();
		var endTime = this._days[numDays-1].endDate.getTime();
		var diffTime = endTime - startTime;

        if(!slots) { return; }

        for (var i = 0; i < slots.length; i++) {
			var div = this._createStatusDiv(sched, status ? status : slots[i].status);
			if(slots[i].s >= startTime) {
				var deltaTime = slots[i].s - startTime;
				var deltaX = (CalSchedulerView.DAY_COLUMN_WIDTH * numDays) * (deltaTime/diffTime);

				if(!sched.attendeeCell) { continue; }

				var attendeeLoc = Dwt.toWindow(sched.attendeeCell, null, null, this.getHtmlElement());
				var attendeeSize = Dwt.getSize(sched.attendeeCell);
				Dwt.setLocation(div, deltaX, attendeeLoc.y-gridLoc.y);
				var width = (CalSchedulerView.DAY_COLUMN_WIDTH*numDays)*((slots[i].e - slots[i].s)/diffTime);
				Dwt.setSize(div, width, attendeeSize.y);

				div.s = slots[i].s;
				div.e = slots[i].e;
				div._fbStatus = status ? status : slots[i].status;
				div._schedTableIdx = sched.idx;

				if(!sched.isAllAttendees) {
					DBG.println("<u>updateAllAttendeeSchedule</u>:"+slots[i].status + ", " + new Date(slots[i].s) + " - " + new Date(slots[i].e));
					this.updateAllAttendeeSchedule(slots[i], status, startTime, endTime);
				}
			}
		};
};

/*
* Draws a dark border for the appt's start and end times.
*
* @param index		[object]		start and end indexes
*/
CalSchedulerView.prototype._outlineAppt =
function(dateInfo) {
	this._updateBorders(this._allAttendeesSlot, true);
	for (var j = 1; j < this._schedTable.length; j++) {
		this._updateBorders(this._schedTable[j]);
	}
};

/*
* Outlines the times of the current appt for the given row.
*
* @param sched			[sched]			info for this row
* @param isAllAttendees	[boolean]*		if true, this is the All Attendees row
*/
CalSchedulerView.prototype._updateBorders =
function(sched, isAllAttendees) {
	return;
	if (!sched) return;

	var div, curClass, newClass;

	// if start time is midnight, mark right border of attendee div
	div = document.getElementById(sched.dwtNameId);
	if (div) {
		curClass = div.className;
		newClass = (this._dateBorder.start == -1)
			? "ZmSchedulerNameTdBorder"
			: "ZmSchedulerNameTd";
		if (curClass != newClass) {
			div.className = newClass;
		}
	}

	// mark right borders of appropriate f/b table cells
	var normalClassName = "ZmSchedulerGridDiv",
		halfHourClassName = normalClassName + "-halfHour",
		startClassName = normalClassName + "-start",
		endClassName = normalClassName + "-end"


	var table = document.getElementById(sched.dwtTableId);
	var row = table.rows[0];
	if (row) {
		for (var i = 0; i < CalSchedulerView.FREEBUSY_NUM_CELLS; i++) {
			var td = row.cells[i];
			div = td ? td.getElementsByTagName("*")[0] : null;
			if (div) {
				curClass = div.className;
				newClass = normalClassName;
				if (i == this._dateBorder.start) {
					newClass = startClassName;
				} else if (i == this._dateBorder.end) {
					newClass = endClassName;
				} else if (i % 2 == 0) {
					newClass = halfHourClassName;
				}
				if (curClass != newClass) {
					div.className = newClass;
				}
			}
		}
	}
};

/*
* Calculate index of the cell that covers the given time. A start time on a half-hour border
* covers the corresponding time block, whereas an end time does not. For example, an appt with
* a start time of 5:00 causes the 5:00 - 5:30 block to be marked. The end time of 5:30 does not
* cause the 5:30 - 6:00 block to be marked.
*
* @param time	[Date or int]		time
* @param isEnd	[boolean]*			if true, this is an appt end time
* @param adjust [boolean]           (Optional) Specify whether the time should
*                                   be adjusted based on timezone selector. If
*                                   not specified, assumed to be true.
*/
CalSchedulerView.prototype._getIndexFromTime =
function(time, isEnd, adjust) {
	var d = (time instanceof Date) ? time : new Date(time);
    var hourmin = d.getHours() * 60 + d.getMinutes();
    adjust = adjust != null ? adjust : true;
    if (adjust && this._dateInfo.timezone != AjxTimezone.getServerId(AjxTimezone.DEFAULT)) {
        var offset1 = AjxTimezone.getOffset(AjxTimezone.DEFAULT, d);
        var offset2 = AjxTimezone.getOffset(AjxTimezone.getClientId(this._dateInfo.timezone), d);
        hourmin += offset2 - offset1;
    }
	var idx = Math.floor(hourmin / 60) * 2;
	var minutes = hourmin % 60;
	if (minutes >= 30) {
		idx++;
	}
	// end times don't mark blocks on half-hour boundary
	if (isEnd && (minutes == 0 || minutes == 30)) {
		//block even if it exceeds 1 second
		var s = d.getSeconds();
		if(s == 0){
		idx--;
		}
	}

	return idx;
};

CalSchedulerView.prototype._getBordersFromDateInfo =
function(dateInfo) {
	var index = {start: -99, end: -99};
	if (dateInfo.showTime) {
		var idx = AjxDateUtil.isLocale24Hour() ? 0 : 1;
		var startDate = ZmTimeSelect.getDateFromFields(dateInfo.startHourIdx + idx, dateInfo.startMinuteIdx * 5,
													   dateInfo.startAmPmIdx,
													   AjxDateUtil.simpleParseDateStr(dateInfo.startDate));
		var endDate = ZmTimeSelect.getDateFromFields(dateInfo.endHourIdx + idx, dateInfo.endMinuteIdx * 5,
													 dateInfo.endAmPmIdx,
													 AjxDateUtil.simpleParseDateStr(dateInfo.endDate));
		// subtract 1 from index since we're marking right borders
		index.start = this._getIndexFromTime(startDate, null, false) - 1;
		if (dateInfo.endDate == dateInfo.startDate) {
			index.end = this._getIndexFromTime(endDate, true, false);
		}
	}
	return index;
};

CalSchedulerView.prototype._getClassForStatus =
function(status) {
	return ZmSchedTabViewPage.STATUS_CLASSES[status];
};

CalSchedulerView.prototype._getClassForParticipationStatus =
function(status) {
	return ZmSchedTabViewPage.PSTATUS_CLASSES[status];
};

// Callbacks

CalSchedulerView.prototype._handleResponseFreeBusy =
function(result) {
	var args = result.getResponse().GetFreeBusyResponse.usr;
	this._fbResponse = args;
	this._updateFreeBusySchedule(args);
	this._refreshFreeBusyUI();
};

CalSchedulerView.prototype._refreshFreeBusyUI =
function() {
	delete this._allAttendeeSlots;
	delete this._allAttendeeResolvedSlots;
    delete this._freeSlots;
    this._currentFreeSlotIdx = 0;

    if(this._freeStatusDiv) {
        this._freeStatusDiv.parentNode.removeChild(this._freeStatusDiv);
        delete this._freeStatusDiv;
    }

    this._clearColoredCells(this._allAttendeesSched);
	for(var i in this._schedTable) {
		var sched = this._schedTable[i];
		if(!sched || !sched.usr) { continue; }

		var usr = sched.usr;
		this._clearColoredCells(sched);

		// next, for each free/busy status, color the row for given start/end times
		if (usr.n) this._colorSchedule(sched, usr.n, ZmSchedTabViewPage.STATUS_UNKNOWN);
		if (usr.t) this._colorSchedule(sched, usr.t, ZmSchedTabViewPage.STATUS_TENTATIVE);
		if (usr.b) this._colorSchedule(sched, usr.b, ZmSchedTabViewPage.STATUS_BUSY);
		if (usr.u) this._colorSchedule(sched, usr.u, ZmSchedTabViewPage.STATUS_OUT);
	}

	//resolve conflict between slots
	sched = this._allAttendeesSched;
	this._resolveConflictSlots();

	//mark all attendee status
	this._colorSchedule(sched, this._allAttendeeSlots);
	this._colorSchedule(sched, this._allAttendeeResolvedSlots);
};

CalSchedulerView.prototype._updateFreeBusySchedule =
function(args) {
	for (var i = 0; i < args.length; i++) {
		var usr = args[i];
		if(!this._emailToIdx[usr.id]) { continue; }

		var sched = this._schedTable[this._emailToIdx[usr.id]];
		if(!sched) { continue; }

		sched.uid = usr.id;
		sched.usr = usr;
	}
};

CalSchedulerView.prototype._emailValidator =
function(value) {
	var str = AjxStringUtil.trim(value);
	if (str.length > 0 && !AjxEmailAddress.isValid(value)) {
		throw ZmMsg.errorInvalidEmail;
	}

	return value;
};

CalSchedulerView.prototype._getDefaultFocusItem =
function() {
	for (var i = 0; i < this._schedTable.length; i++) {
		var sched = this._schedTable[i];
		if (sched && sched.inputObj && !sched.inputObj.disabled) {
			return sched.inputObj;
		}
	}
	return null;
};

CalSchedulerView.prototype._getFreeBusyInfo =
function(startTime, emailList, callback) {
	var numDays = this.getNumDays();
	var endTime = startTime + numDays*AjxDateUtil.MSEC_PER_DAY;
	var controller = AjxDispatcher.run("GetApptComposeController");
	controller.getFreeBusyInfo(startTime, endTime, emailList, callback);
};

CalSchedulerView.prototype.showFreeBusyToolTip =
function() {

	var fbInfo = this._fbToolTipInfo;
	if(!fbInfo) return;

	var x = fbInfo.x;
	var y = fbInfo.y;
	var sched = fbInfo.sched;
	var el = fbInfo.el;

	var attendee = sched.attendee;
	var table = sched ? document.getElementById(sched.dwtTableId) : null;
	if(attendee){
		var email = attendee.getEmail();
		var attendeeName = (sched && sched.attendee) ? sched.attendee.getAttendeeText() : "";

		if(sched.inputObj) {
			attendeeName = sched.inputObj.getValue()
		}

		//todo: to be moved to zmmsg.properties
		var tooltipContent =  "<b>" + ZmMsg.attendeeStatusLabel + "</b> " + this.getStatusString(el._fbStatus);
		tooltipContent += "<br><b>" + (sched.isOrganizer ? ZmMsg.organizerLabel : ZmMsg.attendeesLabel) + "</b>  " + attendeeName;

		var pattern = ZmMsg.apptTimeInstance;
		tooltipContent += "<br><b>" + ZmMsg.whenLabel + "</b> " + AjxMessageFormat.format(pattern, [new Date(el.s), new Date(el.e), ""]);


		var shell = DwtShell.getShell(window);
		var tooltip = shell.getToolTip();
		tooltip.setContent(tooltipContent, true);
		tooltip.popup(x, y, true);
	}
	this._fbToolTipInfo = null;
};

// Static methods

CalSchedulerView._onClick =
function(ev) {
	var el = DwtUiEvent.getTarget(ev);
	var svp = AjxCore.objectWithId(el._schedViewPageId);
	if (!svp) return;
	// figure out which object was clicked
	if (el.id == svp._allDayCheckboxId) {
        ZmApptViewHelper.getDateInfo(svp, svp._dateInfo);
		svp._showTimeFields(!el.checked);
		//svp._editView.updateAllDayField(el.checked);
        svp._dateBorder = svp._getBordersFromDateInfo(svp._dateInfo);
		svp._outlineAppt();
	} else if (el.id == svp._startDateFieldId || el.id == svp._endDateFieldId) {
		svp._activeDateField = el;
	}
};

CalSchedulerView._onFocus =
function(ev) {
	var el = DwtUiEvent.getTarget(ev);
	var svp = AjxCore.objectWithId(el._schedViewPageId);
	if (!svp) return;
	var sched = svp._schedTable[el._schedTableIdx];
	if (sched) {
		svp._activeInputIdx = el._schedTableIdx;
	}
};

CalSchedulerView._onBlur =
function(ev) {
	var el = DwtUiEvent.getTarget(ev);
	var svp = AjxCore.objectWithId(el._schedViewPageId);
	if (!svp) return;
	if (el.id == svp._startDateFieldId || el.id == svp._endDateFieldId) {
		svp._handleDateChange(el == svp._startDateField);
		svp._activeDateField = null;
	} else {
		svp._handleAttendeeField(el);
	}
};

CalSchedulerView._onPTSTMouseOver =
function(ev) {
	ev = DwtUiEvent.getEvent(ev);
	var el = DwtUiEvent.getTarget(ev);
	var svp = AjxCore.objectWithId(el._schedViewPageId);
	if (!svp) return;
	var sched = svp._schedTable[el._schedTableIdx];
	if (sched) {
		var shell = DwtShell.getShell(window);
		var tooltip = shell.getToolTip();
		tooltip.setContent(el._ptstLabel, true);
		tooltip.popup((ev.pageX || ev.clientX), (ev.pageY || ev.clientY), true);
	}
};


CalSchedulerView._onPTSTMouseOut =
function(ev) {
	ev = DwtUiEvent.getEvent(ev);
	var el = DwtUiEvent.getTarget(ev);
	var svp = AjxCore.objectWithId(el._schedViewPageId);
	if (!svp) return;
	var sched = svp._schedTable[el._schedTableIdx];
	if (sched) {
		var shell = DwtShell.getShell(window);
		var tooltip = shell.getToolTip();
		tooltip.popdown();
	}
};

CalSchedulerView._onFreeBusyMouseOver =
function(ev) {
	ev = DwtUiEvent.getEvent(ev);
	var fbDiv = DwtUiEvent.getTarget(ev);
	if(!fbDiv) return;

	var svp = AjxCore.objectWithId(fbDiv._schedViewPageId);
	if(!svp) return;

	var sched = svp._schedTable[fbDiv._schedTableIdx];

	if(svp && sched && fbDiv.s && fbDiv.e && fbDiv._fbStatus){
		svp._fbToolTipInfo={x: (ev.pageX || ev.clientX), y: (ev.pageY || ev.clientY), el: fbDiv, sched: sched};
		//avoid redundant request to server
		AjxTimedAction.scheduleAction(new AjxTimedAction(svp, svp.showFreeBusyToolTip),1000);
	}

};

CalSchedulerView._onFreeBusyMouseOut =
function(ev) {

	ev = DwtUiEvent.getEvent(ev);
	var el = DwtUiEvent.getTarget(ev);
	var svp = AjxCore.objectWithId(el._schedViewPageId);
	if (!svp) return;
	svp._fbToolTipInfo = null;
	var sched = svp._schedTable[el._schedTableIdx];
	if (sched) {
		var shell = DwtShell.getShell(window);
		var tooltip = shell.getToolTip();
		tooltip.popdown();
	}

};


CalSchedulerView._onTimeGridClick =
function(ev) {
	var el = DwtUiEvent.getTarget(ev);

	while(el && (el.className != "CalScheduler_TimeGrid")) {
		el = el.parentNode;
	}
	if(!el){ return; }

	var svp = AjxCore.objectWithId(el._schedViewPageId);
	if (!svp) return;

	svp.handleTimeGridClick(ev);
};

CalSchedulerView.prototype.handleTimeGridClick =
function(ev) {

	ev = DwtUiEvent.getEvent(ev);
	var el = DwtUiEvent.getTarget(ev);

	var numDays = this.getNumDays();
	var startTime = this._days[0].date.getTime();
	var endTime = this._days[numDays-1].endDate.getTime();
	var diffTime = endTime-startTime;

	var el = DwtUiEvent.getTarget(ev);

	if(el.className != "sched_grid" && el.className != "sched_grid_eod") {
		return;
	}

	var newX = Dwt.toWindow(el,null,null,this._timeGrid).x;
	var time = startTime + (diffTime*(newX/(CalSchedulerView.DAY_COLUMN_WIDTH * numDays)));
	time = Math.ceil(time);

	var dur = this._durationSelect ? (this._durationSelect.getValue()*60*1000) : AjxDateUtil.MSEC_PER_HOUR;

	this._clearFreeSelections();

	var date = new Date(time);

    var newStartDate = null;
    if((date.getMinutes()==0) && (date.getHours()==0) && (date.getSeconds()==0)) {
        newStartDate = date;
    } else {
        this.setDate({s: time, e: time + dur});
        newStartDate = this.getSelectedStartDate();
    }

    var newSlot = { s: newStartDate.getTime(), e: newStartDate.getTime() + dur};

	this._currentFreeSlot = newSlot;
	this._positionFreeStatusDiv(newSlot, startTime, endTime);
};

CalSchedulerView.prototype.updateAllAttendeeSchedule =
function(slot, newStatus, gridStartTime, gridEndTime) {

	if(!this._allAttendeeSlots && newStatus != ZmSchedTabViewPage.STATUS_FREE) {
		this._allAttendeeSlots = [{status: newStatus, s: slot.s, e: slot.e}];
		return;
	}

	if(!this._allAttendeeSlots) {
		this._allAttendeeSlots = [];
	}

	var startTime =  (slot.s < gridStartTime)? gridStartTime : slot.s;
	var endTime =  (slot.e > gridEndTime)? gridEndTime : slot.e;

	var count = this._count  = 0;

	if(this._allAttendeeSlots) {
		var foundConflict = false;
		for(var j in this._allAttendeeSlots) {
			var cslot = this._allAttendeeSlots[j];
			var cstatus = cslot.status

			var isStartInRange =(startTime >= cslot.s && startTime < cslot.e);
			var isEndInRange =(endTime > cslot.s && endTime <= cslot.e);

			count++;
			if(isStartInRange || isEndInRange || (cslot.s>=startTime && cslot.e <=endTime)) {
				foundConflict = true;
				minStart = (startTime < cslot.s) ? startTime : cslot.s;
				maxEnd = (endTime > cslot.e) ? endTime : cslot.e;

				if(cslot._conflictTimes == null) {
					cslot._conflictTimes = new AjxVector();
					cslot._conflictTimes.add(cslot.s);
					cslot._conflictTimes.add(cslot.e);
				}

				if(cslot._conflictSlots == null) {
					cslot._conflictSlots = new AjxVector();
					cslot._conflictSlots.add({status: cslot.status, s:cslot.s, e: cslot.e});
				}

				cslot._conflictTimes.add(startTime);
				cslot._conflictTimes.add(endTime);
				cslot.s = minStart;
				cslot.e = maxEnd;
				cslot._conflictSlots.add({status: newStatus, s: startTime, e: endTime});
			}
		}

		if(!foundConflict && newStatus != ZmSchedTabViewPage.STATUS_FREE) {
			//DBG.println("@ 2 push:" + new Date(startTime) + " to " + new Date(endTime) + " : status = " + this._getClassForStatus(newStatus));
			this._allAttendeeSlots.push({status: newStatus, s: startTime, e: endTime});
		}
	}
};

CalSchedulerView.prototype._resolveConflictSlots =
function() {
	//calculate overlapping free busy status and resolve them

	var resolvedSlots = this._allAttendeeResolvedSlots = [];

	var count = this._countConflict = 0;
	DBG.println("resolve conflict slots ...");

	if(!this._allAttendeeSlots) {
		this._allAttendeeSlots = [];
	}

	var size  = this._allAttendeeSlots.length;

	for(var i=0; i<size; i++) {
		var cslot = this._allAttendeeSlots[i];
		if(cslot && cslot._conflictTimes && cslot._conflictSlots) {
			var conflictTimes = cslot._conflictTimes;
			var conflictSlots = cslot._conflictSlots;
			conflictTimes.sort();
			this.accumulateResolvedSlots(resolvedSlots, conflictTimes, conflictSlots)

			cslot._conflictSlots.removeAll();
			cslot._conflictTimes.removeAll();

			delete cslot._conflictSlots;
			delete cslot._conflictTimes;
		}

	}

	//todo: remove this line
	DBG.println("<b>resolveConflicts</b>:"+this._countConflict);
	return resolvedSlots;
};

CalSchedulerView.prototype.accumulateResolvedSlots =
function(resolvedSlots, conflictTimes, conflictSlots) {

	var noOfConflict = conflictTimes.size();
	for(var k=0;k<noOfConflict; k++) {
			if(k+1 < noOfConflict) {
					var startTime = conflictTimes.get(k);
					var endTime = conflictTimes.get(k+1);

					DBG.println("<font color=grey>TIME: " +  new Date(startTime) + " : "  + new Date(endTime) + "</font>");
					if(startTime == endTime) { continue; }

					var newSlot = this.searchConflictSlots(conflictSlots, startTime, endTime);

					if(newSlot) {
						resolvedSlots.push(newSlot);
					}else {
						DBG.println("<font color=grey>NO SLOT FOUND TIME: " +  new Date(startTime) + " : "  + new Date(endTime) + " NO SLOT FOUND</font>");
					}

			}
	}

};

CalSchedulerView.prototype.searchConflictSlots =
function(conflictSlots, startTime, endTime) {
	var newSlot = null;
	var cStatus = null;

	var len = conflictSlots.size();

	for(var j=0; j<len; j++) {
		this._countConflict++;
		var slot = conflictSlots.get(j);
		if(startTime >= slot.s && endTime <= slot.e ) {
			if(newSlot == null) {
				cStatus = slot.status;
			}else if(newSlot.status != slot.status){
				if(slot.status != ZmSchedTabViewPage.STATUS_UNKNOWN && slot.status != ZmSchedTabViewPage.STATUS_FREE) {
					cStatus = ZmSchedTabViewPage.STATUS_BUSY;
				}
			}
			newSlot = {status: cStatus, s: startTime, e: endTime}
		}
	}
	return newSlot;
};

CalSchedulerView.prototype.updateAllAttendeeCellStatus =
function(idx, status) {
	if(!this._allAttendeesStatus[idx]){
		this._allAttendeesStatus[idx] = status;
	}else if(status!= this._allAttendeesStatus[idx]) {
		if(status != ZmSchedTabViewPage.STATUS_UNKNOWN && status != ZmSchedTabViewPage.STATUS_FREE) {
			this._allAttendeesStatus[idx] = ZmSchedTabViewPage.STATUS_BUSY;;
		}
	}
};

CalSchedulerView.prototype.getAllAttendeeStatus =
function(idx) {
	return this._allAttendeesStatus[idx] ? this._allAttendeesStatus[idx] : ZmSchedTabViewPage.STATUS_FREE;
};


CalSchedulerView.prototype._updateTimeGrid =
function(resize) {
	var el = this._allAttendeesHeadingCell;
	var loc = Dwt.toWindow(el,null, null, this.getHtmlElement());
	var size = Dwt.getSize(el);
	var x = loc.x + size.x;
	var needsTemplateContent = false;

	if(!this._timeGrid) {
		this._timeGrid = document.createElement("div");
		needsTemplateContent = true;
		Dwt.setPosition(this._timeGrid, Dwt.ABSOLUTE_STYLE);
		this.getHtmlElement().appendChild(this._timeGrid);
		this._timeGrid.className = "CalScheduler_TimeGrid";
		this._timeGrid._schedViewPageId = this._svpId;
		Dwt.setHandler(this._timeGrid, DwtEvent.ONCLICK, CalSchedulerView._onTimeGridClick);
		Dwt.setHandler(this._timeGrid, DwtEvent.ONMOUSEOVER, CalSchedulerView._onFreeBusyMouseOver);
		Dwt.setHandler(this._timeGrid, DwtEvent.ONMOUSEOUT, CalSchedulerView._onFreeBusyMouseOut);
	}

	Dwt.setLocation(this._timeGrid, x, loc.y - 2*CalSchedulerView._DAY_HEADING_HEIGHT+2);
	//if(resize) {
	Dwt.setSize(this._timeGrid, this.getSize().x-x, this.getSize().y-loc.y+2*CalSchedulerView._DAY_HEADING_HEIGHT);
	//}

	if(needsTemplateContent) {
		var params = {
			dayHeadingHeight: CalSchedulerView._DAY_HEADING_HEIGHT,
			dayColumnWidth: CalSchedulerView.DAY_COLUMN_WIDTH,
			hourWidth: CalSchedulerView.HOUR_COLUMN_WIDTH,
			columns: this._columns,
			numDays: this.getNumDays()
		};
		this._timeGrid.innerHTML = AjxTemplate.expand("com_zimbra_calscheduler.templates.CalScheduler#CalScheduler_TimeGrid", params);
	}

	this._updateSeparators();
};

CalSchedulerView.prototype._createSeparatorDiv =
function() {
	var sep =document.createElement("div");
	sep.className = "calendar_day_separator";
	Dwt.setPosition(sep, Dwt.ABSOLUTE_STYLE);
	this._timeGrid.appendChild(sep);
	return sep;
};


CalSchedulerView.prototype._updateSeparators =
function() {
	var sch = this._schedTable;
	for (var i = 0; i < this._schedTable.length; i++) {
		var sched = this._schedTable[i];
		if (sched && sched.attendeeCell) {
			this._updateSeparator(sched, sched.attendeeCell);
		}
	}


};

CalSchedulerView.prototype._updateSeparator =
function(sched, attendeeCell) {
	var loc = Dwt.toWindow(attendeeCell, null, null, this.getHtmlElement());
	var size = Dwt.getSize(attendeeCell);

	//create horizontal status separator
	var div = sched.separatorEl;
	if(!sched.separatorEl) {
		div = sched.separatorEl = document.createElement("div");
		div.style.padding = "0px";
		div.className = "calendar_day_separator schedule_grid_separator";
		Dwt.setPosition(div, Dwt.ABSOLUTE_STYLE);
		this._timeGrid.appendChild(div);
	}

	var gridSize = Dwt.getSize(this._timeGrid);
	var gridLoc = Dwt.toWindow(this._timeGrid, null, null, this.getHtmlElement());
	Dwt.setBounds(div, 0, loc.y-gridLoc.y+size.y, this._timeGrid.scrollWidth, 1);
};


CalSchedulerView.prototype._updateDays =
function() {
	var numDays = this.getNumDays();
	var sd = AjxDateUtil.simpleParseDateStr(this._startDateField.value);
	var d = new Date(sd.getTime());
	d.setHours(0,0,0,0);
	var fdow = this.firstDayOfWeek();
	var	dow = d.getDay();
	if ((numDays!=1)  && (dow != fdow)) {
		d.setDate(d.getDate()-((dow+(7-fdow))%7));
	}

	this._days = {};

	var today = new Date();
	today.setHours(0,0,0,0);

	var lastDay = numDays - 1;

	for (var i=0; i < numDays; i++) {
		var day = this._days[i] = {};
		day.index = i;
		day.date = new Date(d);
		day.endDate = new Date(d);
		day.endDate.setHours(23,59,59,999);
		day.isToday = day.date.getTime() == today.getTime();
		this._dateToDayIndex[this._dayKey(day.date)] = day;
		var id = this._columns[i].titleId;
 		var te = document.getElementById(id);
 		if(te) {
			te.innerHTML = this._dayTitle(d);
			te.className ='calendar_heading ' +  (day.isToday ? 'calendar_heading_day_today' : 'calendar_heading_day');
 		}
		d.setDate(d.getDate()+1);
	}

};

CalSchedulerView.prototype._dayKey =
function(date) {
	return (date.getFullYear()+"/"+date.getMonth()+"/"+date.getDate());
};


CalSchedulerView.prototype._dayTitle =
function(date) {
	var formatter = this.getNumDays() == 1
				? DwtCalendar.getDateLongFormatter()
				: DwtCalendar.getDateFormatter();
	return formatter.format(date);
};


CalSchedulerView.prototype._initColumns =
function() {
	var numDays = this.getNumDays();
	for (var i =0; i < numDays; i++) {
		this._columns[i] = {
			index: i,
			dayIndex: i,
			titleId: Dwt.getNextId()
		};
	}
};

CalSchedulerView.prototype.firstDayOfWeek =
function() {
	return appCtxt.get(ZmSetting.CAL_FIRST_DAY_OF_WEEK) || 0;
};

CalSchedulerView.prototype.getNumDays =
function() {
	return 7;
};

CalSchedulerView.prototype.autoPickFreeTime =
function(startTime, endTime) {
	var slots = AjxVector.fromArray(this._allAttendeeSlots);

	var resolvedList = AjxVector.fromArray(this._allAttendeeResolvedSlots);
	slots.addList(resolvedList);

	slots.sort(CalSchedulerView._slotComparator);

	var html = [];

	var freeSlots = new AjxVector();
	var size = slots.size();
	for(var i=0;i<size;i++) {
		var slot1 = slots.get(i);
		if((i==0) && (slot1.s != startTime)) {
			freeSlots.add({status: ZmSchedTabViewPage.STATUS_FREE, s: startTime, e: slot1.s});
		}

		if(i+1 < size) {
			var slot2 = slots.get(i+1);
			if(slot1.e != slot2.s) {
				freeSlots.add({status: ZmSchedTabViewPage.STATUS_FREE, s: slot1.e, e: slot2.s});
			}
		}

		if((i+1 == size) && (slot1.e != endTime)) {
			freeSlots.add({status: ZmSchedTabViewPage.STATUS_FREE, s: slot1.e, e: endTime});
		}
	}

	return freeSlots;
};

CalSchedulerView._slotComparator =
function(slot1, slot2) {
	return slot1.s < slot2.s ? -1 : (slot1.s > slot2.s ? 1 : 0);
};

CalSchedulerView.prototype.getSelectedStartDate =
function() {
	var idx = AjxDateUtil.isLocale24Hour() ? 0 : 1;
	ZmApptViewHelper.getDateInfo(this, this._dateInfo);
	return ZmTimeSelect.getDateFromFields(this._dateInfo.startHourIdx + idx, this._dateInfo.startMinuteIdx * 5,
													   this._dateInfo.startAmPmIdx,
													   AjxDateUtil.simpleParseDateStr(this._dateInfo.startDate));
};

CalSchedulerView.prototype.getSelectedEndDate =
function() {
	var idx = AjxDateUtil.isLocale24Hour() ? 0 : 1;
	ZmApptViewHelper.getDateInfo(this, this._dateInfo);
	return ZmTimeSelect.getDateFromFields(this._dateInfo.endHourIdx + idx, this._dateInfo.endMinuteIdx * 5,
													 this._dateInfo.endAmPmIdx,
													 AjxDateUtil.simpleParseDateStr(this._dateInfo.endDate));
};

CalSchedulerView.prototype.setDate =
function(slot) {
	var startDate = new Date(slot.s);
	var endDate = new Date(slot.e);

	this._startDateField.value = AjxDateUtil.simpleComputeDateStr(startDate);
	this._endDateField.value = AjxDateUtil.simpleComputeDateStr(endDate);

	this._startTimeSelect.set(startDate);
	this._endTimeSelect.set(endDate);
};

CalSchedulerView.prototype._clearFreeSelections =
function(clearSlots) {
	this._currentFreeSlot = null;
	this._currentFreeSlotIdx = 0;

	if(clearSlots) {
		this._freeSlots = null;
	}
};

CalSchedulerView.prototype._autoPickListener =
function() {
	var numDays = this.getNumDays();
	var startTime = this._days[0].date.getTime();
	var endTime = this._days[numDays-1].endDate.getTime();
	var diffTime = endTime - startTime;

    var freeSlots = this._freeSlots;
    if(!freeSlots) {
        freeSlots =  this._freeSlots = this.autoPickFreeTime(startTime, endTime);
    }

	var dur = this._durationSelect ? (this._durationSelect.getValue()*60*1000) : AjxDateUtil.MSEC_PER_HOUR;
	var slot = null;

	if(!this._currentFreeSlot) {
		var startDate = this.getSelectedStartDate();
		var newSlot = {s: startDate.getTime() , e: startDate.getTime() + dur};
		slot = this.getFreeSlot(newSlot, startTime, endTime);
	}else {
		var newSlot = {s: this._currentFreeSlot.e, e: this._currentFreeSlot.e+dur};
		slot = this.getFreeSlot(newSlot, startTime, endTime);
	}


	if(slot == null) {
		return;
	}

	DBG.println("@ <font color=blue><b>Current Free Slot:</b></font>");
	DBG.println("<br>"+new Date(slot.s)+" to "+new Date(slot.e));
	this.setDate(slot);

	this._positionFreeStatusDiv(slot, startTime, endTime);
};

CalSchedulerView.prototype.getFreeSlot =
function(newSlot, startTime, endTime) {

	var slot = null;

	while(newSlot.s < endTime) {
		for(var i=0; i<this._freeSlots.size(); i++) {
			var nextFreeSlot = this._freeSlots.get(i);
			if(nextFreeSlot && (newSlot.s >= nextFreeSlot.s) && (newSlot.e <= nextFreeSlot.e)){
				slot = this._currentFreeSlot = newSlot;
				this._currentFreeSlotIdx = i;
				return slot;
			}
		}
		newSlot = {s: newSlot.s+AjxDateUtil.MSEC_PER_FIFTEEN_MINUTES, e: newSlot.e+AjxDateUtil.MSEC_PER_FIFTEEN_MINUTES};
	}
	return slot;

};

CalSchedulerView.prototype._positionFreeStatusDiv =
function(slot, startTime, endTime) {

	var numDays = this.getNumDays();
	var diffTime = endTime - startTime;
	var dur = this._durationSelect ? (this._durationSelect.getValue()*60*1000) : AjxDateUtil.MSEC_PER_HOUR;

	var div = this._freeStatusDiv;
    if(!div) {
        div = this._freeStatusDiv = document.createElement("div");
	    Dwt.setPosition(div, Dwt.ABSOLUTE_STYLE);
	    div.className = "ScheduleView_FreeRange"
        this._timeGrid.appendChild(div);
    }

	var gridLoc = Dwt.toWindow(this._timeGrid, null, null, this.getHtmlElement());
	var gridSize = Dwt.getSize(this._timeGrid);

	if(slot.s >= startTime) {
		var deltaTime = slot.s - startTime;
		var deltaX = (CalSchedulerView.DAY_COLUMN_WIDTH * numDays) * (deltaTime/diffTime);

		var attendeeLoc = Dwt.toWindow(this._allAttendeesHeadingCell, null, null, this.getHtmlElement());
		var attendeeSize = Dwt.getSize(this._allAttendeesHeadingCell);
		Dwt.setLocation(div, deltaX, attendeeLoc.y-gridLoc.y);
		var width = (CalSchedulerView.DAY_COLUMN_WIDTH*numDays)*((dur)/diffTime);
		Dwt.setSize(div, AjxEnv.isIE? width : (width-6), this._timeGrid.scrollHeight);

        if((this._timeGrid.scrollLeft + gridSize.x) < deltaX){
            var diffX = deltaX - (gridSize.x) + 100;
            if(diffX > 0) {
                this._timeGrid.scrollLeft = diffX;
            }
        }
    }

};

