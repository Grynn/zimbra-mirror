/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2010, 2011, 2013 Zimbra Software, LLC.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.4 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 */

/**
* Creates up to three separate DwtSelects for the time (hour, minute, am|pm)
* Showing the AM|PM select widget is dependent on the user's locale
* 
* @author Parag Shah
*
* @param parent		[DwtComposite]	the parent widget
* @param id			[string]*		an ID that is propagated to component select objects
 *
 * @private
*/
DwtTimeSelect = function(parent, id) {
	DwtComposite.call(this, {parent:parent, className: 'DwtTimeSelect'});

	this.id = id;
	this._isLocale24Hour = true;
	this._createSelects();
};

// IDs for types of time selects
DwtTimeSelect.START	= 1;
DwtTimeSelect.END	= 2;

// IDs for time select components
DwtTimeSelect.HOUR	= 1;
DwtTimeSelect.MINUTE	= 2;
DwtTimeSelect.AMPM	= 3;

DwtTimeSelect.getDateFromFields =
function(hours, minutes, ampm, date) {
	hours = Number(hours);
	if (ampm) {
		if (ampm == "AM" || ampm === 0) {
			hours = (hours == 12) ? 0 : hours;
		} else if (ampm == "PM" || ampm == 1) {
			hours = (hours < 12) ? hours + 12 : hours;
		}
	}
	date = date ? date : new Date();
	date.setHours(hours, Number(minutes), 0, 0);
	return date;
};

DwtTimeSelect.parse =
function(timeString) {
    var date;
	var lTimeString = timeString.toLowerCase();
	if (lTimeString === AjxMsg.midnight.toLowerCase() || lTimeString === AjxMsg.noon.toLowerCase()) {
		date = new Date();
		date.setMinutes(0);
		date.setSeconds(0);
		date.setHours(lTimeString === AjxMsg.noon.toLowerCase() ? 12 : 0);
	} else {
		var timeFormatter = AjxDateFormat.getTimeInstance(AjxDateFormat.SHORT);    
		date = timeFormatter.parse(timeString) || AjxDateFormat.parseTime(timeString);
	}
    return date;
};

DwtTimeSelect.format =
function(date) {
	if (date.getHours() == 0 && date.getMinutes() == 0) {
		return AjxMsg.midnight;
	} else if (date.getHours() == 12 && date.getMinutes() == 0) {
		return AjxMsg.noon;
	} else {
		return AjxDateFormat.getTimeInstance(AjxDateFormat.SHORT).format(date);
	}
};

/**
* Adjust an appt's start or end based on changes to the other one. If the user changes
* the start time, change the end time so that the appt duration is maintained. If the
* user changes the end time, we leave things alone.
*
* @param ev					[Event]				UI event from a DwtSelect
* @param startSelect		[DwtTimeSelect]		start time select
* @param endSelect			[DwtTimeSelect]		end time select
* @param startDateField		[element]			start date field
* @param endDateField		[element]			end date field
*/
DwtTimeSelect.adjustStartEnd =
function(ev, startSelect, endSelect, startDateField, endDateField) {
	var select = ev._args.selectObj;
	var startDate = AjxDateUtil.simpleParseDateStr(startDateField.value);
	var endDate = AjxDateUtil.simpleParseDateStr(endDateField.value);
	var startDateOrig = startDateField.value;
	var endDateOrig = endDateField.value;
	if (select.id == DwtTimeSelect.START) {
		var hours = (select.compId == DwtTimeSelect.HOUR) ? ev._args.oldValue : startSelect.getHours();
		var minutes = (select.compId == DwtTimeSelect.MINUTE) ? ev._args.oldValue : startSelect.getMinutes();
		var ampm = (select.compId == DwtTimeSelect.AMPM) ? ev._args.oldValue : startSelect.getAmPm();
		var oldStartDateMs = DwtTimeSelect.getDateFromFields(hours, minutes, ampm, startDate).getTime();
		var newStartDateMs = DwtTimeSelect.getDateFromFields(startSelect.getHours(), startSelect.getMinutes(), startSelect.getAmPm(), startDate).getTime();
		var oldEndDateMs = DwtTimeSelect.getDateFromFields(endSelect.getHours(), endSelect.getMinutes(), endSelect.getAmPm(), endDate).getTime();
		var delta = oldEndDateMs - oldStartDateMs;
		if (!delta) return null;
		var newEndDateMs = newStartDateMs + delta;
		var newEndDate = new Date(newEndDateMs);
		endSelect.set(newEndDate);
		endDateField.value = AjxDateUtil.simpleComputeDateStr(newEndDate);
		if (endDateField.value != endDateOrig) {
			return endDateField;
		}
	} else {
		return null;
	}
};

/**
 * Returns true if the start date/time is before the end date/time.
 *
 * @param ss				[DwtTimeSelect]		start time select
 * @param es				[DwtTimeSelect]		end time select
 * @param startDateField	[element]			start date field
 * @param endDateField		[element]			end date field
 */
DwtTimeSelect.validStartEnd =
function(startDateField, endDateField, ss, es) {
	var startDate = AjxDateUtil.simpleParseDateStr(startDateField.value);
	var endDate = AjxDateUtil.simpleParseDateStr(endDateField.value);

    if (startDate && endDate) {
        if((startDate.valueOf() > endDate.valueOf())){
            return false;
        }
        // bug fix #11329 - dont allow year to be more than the earth will be around :]
		if (startDate.getFullYear() > 9999 || endDate.getFullYear() > 9999) {
			return false;
		}
        if(ss && es){
            var startDateMs = DwtTimeSelect.getDateFromFields(ss.getHours(), ss.getMinutes(), ss.getAmPm(), startDate).getTime();
            var endDateMs = DwtTimeSelect.getDateFromFields(es.getHours(), es.getMinutes(), es.getAmPm(), endDate).getTime();
            if (startDateMs > endDateMs) {
                return false;
            }
        }
    } else {
		return false;
	}
	return true;
};

DwtTimeSelect.prototype = new DwtComposite;
DwtTimeSelect.prototype.constructor = DwtTimeSelect;
DwtTimeSelect.prototype.isDwtTimeSelect = true;

DwtTimeSelect.prototype.toString = function() {
    return 'DwtTimeSelect';
};

/**
* Sets the time select according to the given date.
*
* @param date	[Date]		a Date object
*/
DwtTimeSelect.prototype.set = 
function(date) {

	var hourIdx = 0, minuteIdx = 0, amPmIdx = 0;
	var isLocale24Hour = this.isLocale24Hour();

	var hours = date.getHours();
	if (!isLocale24Hour && hours > 12) {
		hourIdx = hours - 13;
	} else if (!isLocale24Hour && hours == 0) {
		hourIdx = this.getHourSelectSize() - 1;
	} else {
		hourIdx = isLocale24Hour ? hours : hours - 1;
	}

	minuteIdx = Math.floor(date.getMinutes() / 5);

	if (!isLocale24Hour) {
		amPmIdx = (date.getHours() >= 12) ? 1 : 0;
	}

	this.setSelected(hourIdx, minuteIdx, amPmIdx);
};


/**
 * Returns a date object with the hours and minutes set based on
 * the values of this time select.
 *
 * @param date [Date] Optional. If specified, the hour and minute
 *                    values will be set on the specified object;
 *                    else, a new <code>Date</code> object is created.
 */
DwtTimeSelect.prototype.getValue =
function(date) {
	return (DwtTimeSelect.getDateFromFields(this.getHours(), this.getMinutes(), this.getAmPm(), date));
};

DwtTimeSelect.prototype.getHours =
function() {
	return this._hourSelect.getValue();
};

DwtTimeSelect.prototype.getMinutes =
function() {
	return this._minuteSelect.getValue();
};

DwtTimeSelect.prototype.getAmPm =
function() {
	return this._amPmSelect ? this._amPmSelect.getValue() : null;
};

DwtTimeSelect.prototype.setSelected = 
function(hourIdx, minuteIdx, amPmIdx) {
	this._hourSelect.setSelected(hourIdx);
	this._minuteSelect.setSelected(minuteIdx);
	if (!this._isLocale24Hour) {
		this._amPmSelect.setSelected(amPmIdx);
	}
};

DwtTimeSelect.prototype.addChangeListener = 
function(listener) {
	this._hourSelect.addChangeListener(listener);
	this._minuteSelect.addChangeListener(listener);
	if (this._amPmSelect)
		this._amPmSelect.addChangeListener(listener);
};

DwtTimeSelect.prototype.isLocale24Hour = 
function() {
	return this._isLocale24Hour;
};

DwtTimeSelect.prototype.getHourSelectSize = 
function() {	
	return this._hourSelect.size();
};

DwtTimeSelect.prototype.getMinuteSelectSize = 
function() {	
	return this._minuteSelect.size();
};

DwtTimeSelect.prototype.getSelectedHourIdx = 
function() {
	return this._hourSelect.getSelectedIndex();
};

DwtTimeSelect.prototype.getSelectedMinuteIdx = 
function() {
	return this._minuteSelect.getSelectedIndex();
};

DwtTimeSelect.prototype.getSelectedAmPmIdx = 
function() {
	return this._amPmSelect ? this._amPmSelect.getSelectedIndex() : 0;
};

DwtTimeSelect.prototype.setEnabled =
function(enabled) {
   DwtComposite.prototype.setEnabled.call(this, enabled);

   this._hourSelect.setEnabled(enabled);
   this._minuteSelect.setEnabled(enabled);
   if (this._amPmSelect) this._amPmSelect.setEnabled(enabled);
};

DwtTimeSelect.prototype._createSelects =
function() {
	this._hourSelectId = Dwt.getNextId();
	this._minuteSelectId = Dwt.getNextId();
	this._amPmSelectId = Dwt.getNextId();

	// get the time formatter for the user's locale
	var timeFormatter = AjxDateFormat.getTimeInstance(AjxDateFormat.SHORT);
	var hourSegmentIdx = 0;
	var minuteSegmentIdx = 0;

	var html = [];
	var i = 0;

	html[i++] = "<table border=0 cellpadding=0 cellspacing=0><tr>";

	// walk time formatter's segments array to render each segment part in the right order
	for (var j = 0; j < timeFormatter._segments.length; j++) {
		var segmentStr = timeFormatter._segments[j]._s;

		if (timeFormatter._segments[j] instanceof AjxFormat.TextSegment) {
			var trimStr = AjxStringUtil.trim(segmentStr);
			if (trimStr.length) {
				html[i++] = "<td class='TextPadding ZmFieldLabel'>"
				html[i++] = segmentStr;
				html[i++] = "</td>";
			}
		} else if (segmentStr.charAt(0) == "h" || segmentStr.charAt(0) == "H") {
			hourSegmentIdx = j;
			html[i++] = "<td width=42 id='"
			html[i++] = this._hourSelectId;
			html[i++] = "'></td>";
		} else if (segmentStr.charAt(0) == "m") {
			minuteSegmentIdx = j;
			html[i++] = "<td width=42 id='"
			html[i++] = this._minuteSelectId;
			html[i++] = "'></td>";
		} else if (segmentStr == "a") {	
			this._isLocale24Hour = false;
			html[i++] = "<td width=42 id='"
			html[i++] = this._amPmSelectId;
			html[i++] = "'></td>";
		}
	}
	
	html[i++] = "</tr></table>";

	// append html template to DOM
	this.getHtmlElement().innerHTML = html.join("");

	// init vars for adding hour DwtSelect
	var now = new Date();
	var start = this._isLocale24Hour ? 0 : 1;
	var limit = this._isLocale24Hour ? 24 : 13;

	// create new DwtSelect for hour slot
	this._hourSelect = new DwtSelect({parent:this});
	this._hourSelect.id = this.id;
	this._hourSelect.compId = DwtTimeSelect.HOUR;
	for (var i = start; i < limit; i++) {
		now.setHours(i);
		var label = timeFormatter._segments[hourSegmentIdx].format(now);
		this._hourSelect.addOption(label, false, i);
	}
	this._hourSelect.reparentHtmlElement(this._hourSelectId);
	delete this._hourSelectId;

	// create new DwtSelect for minute slot
	this._minuteSelect = new DwtSelect({parent:this});
	this._minuteSelect.id = this.id;
	this._minuteSelect.compId = DwtTimeSelect.MINUTE;
	for (var i = 0; i < 60; i = i + 5) {
		now.setMinutes(i);
		var label = timeFormatter._segments[minuteSegmentIdx].format(now);
		this._minuteSelect.addOption(label, false, i);
	}
	this._minuteSelect.reparentHtmlElement(this._minuteSelectId);
	delete this._minuteSelectId;

	// if locale is 12-hour time, add AM|PM DwtSelect
	if (!this._isLocale24Hour) {
		this._amPmSelect = new DwtSelect({parent:this});
		this._amPmSelect.id = this.id;
		this._amPmSelect.compId = DwtTimeSelect.AMPM;
		this._amPmSelect.addOption(I18nMsg["periodAm"], false, "AM");
		this._amPmSelect.addOption(I18nMsg["periodPm"], false, "PM");
		this._amPmSelect.reparentHtmlElement(this._amPmSelectId);
		delete this._amPmSelectId;
	}
};

/**
* Creates up to three separate DwtSelects for the time (hour, minute, am|pm)
* Showing the AM|PM select widget is dependent on the user's locale
*
* @author Parag Shah
*
* @param parent		[DwtComposite]	the parent widget
* @param id			[string]*		an ID that is propagated to component select objects
 *
 * @private
*/
DwtTimeInput = function(parent, id, parentElement, interval) {
    var params = {parent:parent, id: "DwtTimeInput", className: 'DwtTimeInput'};
    if(parentElement) {
        params.parentElement = parentElement;
    }
	DwtComposite.call(this, params);

    this._interval = interval || DwtTimeInput.FIFTEEN_MIN_INTERVAL;
	this.id = id;
	this._isLocale24Hour = true;
	this._createSelects();
    this._useTextInput = true;
};

DwtTimeInput.THIRTY_MIN_INTERVAL = 30;
DwtTimeInput.FIFTEEN_MIN_INTERVAL = 15;

// IDs for types of time selects
DwtTimeInput.START	= 1;
DwtTimeInput.END	= 2;

// IDs for time select components
DwtTimeInput.HOUR	= 1;
DwtTimeInput.MINUTE	= 2;
DwtTimeInput.AMPM	= 3;

DwtTimeInput.ROWS	= 8; // Show 8 rows at a time
DwtTimeInput.DEFAULT_TOP_ROW	= 8; // Make row 8 (8 AM) the initial topmost visible row unless overridden

DwtTimeInput.getDateFromFields =
function(timeStr, date) {
    var formattedDate = DwtTimeSelect.parse(timeStr);
    date = date || new Date();
    date.setHours(formattedDate.getHours(), formattedDate.getMinutes(), 0, 0);
    return date;
};

/**
* Adjust an appt's start or end based on changes to the other one. If the user changes
* the start time, change the end time so that the appt duration is maintained. If the
* user changes the end time, we leave things alone.
*
* @param ev					[Event]				UI event from a DwtSelect
* @param startSelect		[DwtTimeInput]		start time select
* @param endSelect			[DwtTimeInput]		end time select
* @param startDateField		[element]			start date field
* @param endDateField		[element]			end date field
* @param dateInfo		    [object]			date info used to calculate the old time before changing this
* @param id		            [string]			an ID which got changed 
*/
DwtTimeInput.adjustStartEnd =
function(ev, startSelect, endSelect, startDateField, endDateField, dateInfo, id) {
    var startDate = AjxDateUtil.simpleParseDateStr(startDateField.value);
    var endDate = AjxDateUtil.simpleParseDateStr(endDateField.value);
    var startDateOrig = startDateField.value;
    var endDateOrig = endDateField.value;
    if (id == DwtTimeInput.START) {
        var timeStr = dateInfo ? dateInfo.startTimeStr : startSelect.getTimeString();
        var oldStartDateMs = DwtTimeInput.getDateFromFields(timeStr, startDate).getTime();
        var newStartDateMs = DwtTimeInput.getDateFromFields(startSelect.getTimeString(), startDate).getTime();
        var oldEndDateMs = DwtTimeInput.getDateFromFields(endSelect.getTimeString(), endDate).getTime();

        var delta = oldEndDateMs - oldStartDateMs;
        if (!delta) return null;

        var newEndDateMs = newStartDateMs + delta;
        var newEndDate = new Date(newEndDateMs);

        startSelect.set(new Date(newStartDateMs));
        endSelect.set(newEndDate);
        endDateField.value = AjxDateUtil.simpleComputeDateStr(newEndDate);

        if (endDateField.value != endDateOrig) {
            return endDateField;
        }
    } else if (id == DwtTimeInput.END){
        var timeStr = dateInfo ? dateInfo.endTimeStr : endSelect.getTimeString();
        var oldEndDateMs = DwtTimeInput.getDateFromFields(timeStr, endDate).getTime();
        var newEndDateMs = DwtTimeInput.getDateFromFields(endSelect.getTimeString(), endDate).getTime();
        var oldStartDateMs = DwtTimeInput.getDateFromFields(startSelect.getTimeString(), startDate).getTime();

        var delta = oldEndDateMs - oldStartDateMs;
        if (!delta) return null;

        //adjust start date only when the end date falls earlier than start date
        if(newEndDateMs < oldStartDateMs) {
            var newStartDateMs = newEndDateMs - delta;
            var newStartDate = new Date(newStartDateMs);

            startSelect.set(newStartDate);
            endSelect.set(new Date(newEndDateMs));
            startDateField.value = AjxDateUtil.simpleComputeDateStr(newStartDate);
            endDateField.value = AjxDateUtil.simpleComputeDateStr(new Date(newEndDateMs));
        }

        if (startDateField.value != startDateOrig) {
            return startDateField;
        }

    } else {
        return null;
    }
};

/**
 * Returns true if the start date/time is before the end date/time.
 *
 * @param ss				[DwtTimeInput]		start time select
 * @param es				[DwtTimeInput]		end time select
 * @param startDateField	[element]			start date field
 * @param endDateField		[element]			end date field
 */
DwtTimeInput.validStartEnd =
function(startDateField, endDateField, ss, es) {
	var startDate = AjxDateUtil.simpleParseDateStr(startDateField.value);
	var endDate = AjxDateUtil.simpleParseDateStr(endDateField.value);

	if (startDate && endDate) {
		if((startDate.valueOf() > endDate.valueOf())) {
			return false;
		}
		// bug fix #11329 - dont allow year to be more than the earth will be around :]
		if (startDate.getFullYear() > 9999 || endDate.getFullYear() > 9999) {
			return false;
		}
		if (ss && es) {
			var startTime = ss.getTimeString();
			var endTime = es.getTimeString();
			if (startTime && endTime) {
				var startDateMs = DwtTimeInput.getDateFromFields(startTime, startDate).getTime();
				var endDateMs = DwtTimeInput.getDateFromFields(endTime, endDate).getTime();
				if (startDateMs > endDateMs) {
					return false;
				}
			}
		}
	} else {
		return false;
	}
	return true;
};

DwtTimeInput.prototype = new DwtComposite;
DwtTimeInput.prototype.constructor = DwtTimeInput;
DwtTimeInput.prototype.isDwtTimeInput = true;

DwtTimeInput.prototype.toString = function() {
    return 'DwtTimeInput';
};

/**
* Sets the time select according to the given date.
*
* @param date	[Date]		a Date object
*/
DwtTimeInput.prototype.set =
function(date) {
    var timeStr = DwtTimeSelect.format(date);
    this._originalTimeStr = timeStr;
    this._timeSelectInput.setValue(timeStr);
    this._scrollToValue(timeStr);
};

/**
* Sets the time string after validating it
*
* @param date	[Date]		a Date object
*/
DwtTimeInput.prototype.setValue =
function(str) {
    //sets only if the date is valid
    var date = DwtTimeSelect.parse(str);
    if (!date) str = "";
    this._originalTimeStr = str;
    this._timeSelectInput.setValue(str);
    this._scrollToValue(str);
};

DwtTimeInput.prototype._scrollToValue =
function(str) {
    var index = this.getTimeIndex(str);
    if (index !== null)
        this._hoursSelectMenu.setSelectedItem(index);
};

/**
 * Returns a date object with the hours and minutes set based on
 * the values of this time picker.
 *
 * @param date [Date] Optional. If specified, the hour and minute
 *                    values will be set on the specified object;
 *                    else, a new <code>Date</code> object is created.
 */
DwtTimeInput.prototype.getValue =
function(date) {
	//return (DwtTimeInput.getDateFromFields(this.getHours(), this.getMinutes(), this.getAmPm(), date));
    var d = DwtTimeSelect.parse(this._timeSelectInput.getValue());
	if(!d) {
		d = new Date();
	}
    date = date || new Date();
    //daylight saving time
    if(AjxDateUtil.isDayShifted(date)) {
        AjxDateUtil.rollToNextDay(date);
    }
	
    date.setHours(d.getHours(), d.getMinutes(), 0, 0);
    return date;
};

DwtTimeInput.prototype.getHours =
function() {
    var d = this.getValue();
    return d ? d.getHours() : null;
};

DwtTimeInput.prototype.getMinutes =
function() {
    var d = this.getValue();
    return d ? d.getMinutes() : null;
};

DwtTimeInput.prototype.addChangeListener =
function(listener) {
    this._changeListener = listener;
    var callback = AjxCallback.simpleClosure(this.handleTimeChange, this, listener);
    this._timeSelectInput.setHandler(DwtEvent.ONFOCUS, callback);
    this._timeSelectInput.setHandler(DwtEvent.ONBLUR, callback);
};

DwtTimeInput.prototype.handleTimeChange =
function(listener, ev) {
    //restore old value if the new time is not in correct format
    var str = this._timeSelectInput.getValue();
    var d = DwtTimeSelect.parse(str);
    if(!d) {
        //TODO: Try to guess the time 
        /*var newDate = this.correctTimeString(str, DwtTimeSelect.parse(this._originalTimeStr));
        this.setValue(DwtTimeSelect.format(newDate) || "");*/
        this.setValue(this._originalTimeStr);
    } else {
        this._scrollToValue(str);
    }

    listener.run(ev, this.id);
};

DwtTimeInput.prototype.correctTimeString =
function(val, originalDate) {

    var segments = val.split(":");

    if(!segments) return originalDate;

    var hrs = (segments.length && segments[0] != null) ? parseInt(segments[0].replace(/\D/g, "")) : null;
    var mins = (segments.length > 1 && segments[1]!= null) ? parseInt(segments[1].replace(/\D/g, "")) : 0;

    if(!hrs) hrs = (hrs == 0) ? 0 : originalDate.getHours();
    if(!mins) mins = 0;

    originalDate.setHours(hrs, mins, 0, 0);

    return originalDate;

};

DwtTimeInput.prototype.isLocale24Hour =
function() {
	return this._isLocale24Hour;
};

DwtTimeInput.prototype.setEnabled =
function(enabled) {
   DwtComposite.prototype.setEnabled.call(this, enabled);
   this._timeSelectInput.setEnabled(enabled);
   this._timeSelectBtn.setEnabled(enabled);
};


DwtTimeInput.prototype._timeButtonListener =
function(ev) {
    if(!this._menuItemsAdded) {
        var j,
            k,
            mi,
            smi,
            text,
            maxMinutesItem,
            minutesSelectMenu,
            now = new Date(),
            timeSelectButton = this._timeSelectBtn,
            timeFormatter = AjxDateFormat.getTimeInstance(AjxDateFormat.SHORT),
            menuSelectionListener = new AjxListener(this, this._timeSelectionListener),
            defaultTopMenuItem;

        for (j = 0; j < 24; j++) {
            now.setHours(j);
            now.setMinutes(0);

            mi = new DwtMenuItem({parent: this._hoursSelectMenu, style: DwtMenuItem.NO_STYLE});
            text = timeFormatter.format(now); // Regular formatter, returns the I18nMsg formatted time
            this.putTimeIndex(text, j);

            if (j==0 || j==12) {
                text = DwtTimeSelect.format(now); // Specialized formatter, returns AjxMsg.midnight for midnight and AjxMsg.noon for noon
                this.putTimeIndex(text, j); // Both should go in the indexer
            }

            mi.setText(text);
            mi.setData("value", j*60);
            if (menuSelectionListener) mi.addSelectionListener(menuSelectionListener);
            if (j == DwtTimeInput.DEFAULT_TOP_ROW) defaultTopMenuItem = mi;

            maxMinutesItem = 60 / this._interval;
            minutesSelectMenu = new DwtMenu({parent:mi, style:DwtMenu.DROPDOWN_CENTERV_STYLE, layout:DwtMenu.LAYOUT_CASCADE, maxRows:maxMinutesItem, congruent: true});
            mi.setMenu(minutesSelectMenu, true);
            mi.setSelectableWithSubmenu(true);
            for (k = 1; k < maxMinutesItem; k++) {
                now.setMinutes(k*this._interval);
                smi = new DwtMenuItem({parent: minutesSelectMenu, style: DwtMenuItem.NO_STYLE});
                smi.setText(timeFormatter.format(now));
                smi.setData("value", j*60 + k*this._interval);
                if (menuSelectionListener) smi.addSelectionListener(menuSelectionListener);
            }
        }
        this._hoursSelectMenu.setWidth(timeSelectButton.getW() + this._timeSelectInput.getW());

	if (defaultTopMenuItem)
		this._hoursSelectMenu.scrollToItem(defaultTopMenuItem);
        this._scrollToValue(timeFormatter.format(this.getValue()));
        this._menuItemsAdded = true;
    }
	ev.item.popup();
};

DwtTimeInput.prototype._timeSelectionListener =
function(ev) {
    if(ev.item && ev.item instanceof DwtMenuItem){
       this._timeSelectInput.setValue(ev.item.getText());
       this._timeSelectValue = ev.item.getData("value");
       if(this._changeListener) this._changeListener.run(ev, this.id);
       return;
    }
};

DwtTimeInput.prototype.getTimeString =
function() {
    //validate and returns only valid time string
    var date = DwtTimeSelect.parse(this._timeSelectInput.getValue());
    return date ? this._timeSelectInput.getValue() : "";    
};

DwtTimeInput.prototype.getInputField =
function() {
    return this._timeSelectInput;
};

DwtTimeInput.prototype.putTimeIndex =
function(text, value) {
    this._timeIndex[text.replace(/\:\d\d/, ":00").replace(/\s/,"").toLowerCase()] = value;
};

DwtTimeInput.prototype.getTimeIndex =
function(text) {
    if (!text) return null;
    var index = this._timeIndex[text.replace(/\:\d\d/, ":00").replace(/\s/,"").toLowerCase()];
    return (index || index===0) ? index : null;
};

DwtTimeInput.prototype._createSelects =
function() {
	// get the time formatter for the user's locale

	this.getHtmlElement().innerHTML = AjxTemplate.expand("calendar.Appointment#ApptTimeInput", {id: this._htmlElId});

    var inputId = Dwt.getNextId("DwtTimeInputSelect_");
    if (this.id && this.id == DwtTimeSelect.START) {
       inputId += "_startTimeInput";
    }
    else if (this.id && this.id == DwtTimeSelect.END) {
        inputId += "_endTimeInput";
    }
    //create time select input field
    var params = {
        parent: this,
        parentElement: (this._htmlElId + "_timeSelectInput"),
        type: DwtInputField.STRING,
        errorIconStyle: DwtInputField.ERROR_ICON_NONE,
        validationStyle: DwtInputField.CONTINUAL_VALIDATION,
        inputId: inputId,
	    id: Dwt.getNextId("DwtTimeInputField_")
    };

    this._timeSelectInput = new DwtInputField(params);
    var timeInputEl = this._timeSelectInput.getInputElement();
    Dwt.setSize(timeInputEl, "80px", "2rem");
    timeInputEl.typeId = this.id;
    //listeners
    var buttonListener = new AjxListener(this, this._timeButtonListener);
    var buttonId = this._htmlElId + "_timeSelectBtn";
    //create time select drop down button
    var timeSelectButton = this._timeSelectBtn = new DwtButton({parent:this});
    timeSelectButton.addDropDownSelectionListener(buttonListener);

    timeSelectButton.setData(Dwt.KEY_ID, buttonId);
    timeSelectButton.setSize("20");
    
    this._timeIndex = {};
    // create menu for button
    this._hoursSelectMenu = new DwtMenu({parent:timeSelectButton, style:DwtMenu.DROPDOWN_STYLE, layout:DwtMenu.LAYOUT_SCROLL, maxRows:DwtTimeInput.ROWS});
    timeSelectButton.setMenu(this._hoursSelectMenu, true, false, false, true);
    this._menuItemsAdded = false;
    timeSelectButton.reparentHtmlElement(buttonId);
};
