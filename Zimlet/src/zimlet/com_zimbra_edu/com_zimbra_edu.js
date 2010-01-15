/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Zimlets
 * Copyright (C) 2007, 2009 Zimbra, Inc.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.2 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 */

function Com_Zimbra_Edu() {
  ;
}

Com_Zimbra_Edu.prototype = new ZmZimletBase();
Com_Zimbra_Edu.prototype.constructor = Com_Zimbra_Edu;

Com_Zimbra_Edu.prototype.init =
function() {
};

Com_Zimbra_Edu.NOT_ENROLLED = "Not Enrolled";
Com_Zimbra_Edu.ENROLLED = "Currently Enrolled";
Com_Zimbra_Edu.TAKEN = "Already Taken";

Com_Zimbra_Edu.COURSE_DATA = {
	"ZEDU-PSYCH-101": { prof: "Steve Patterson", title: "Intro to Pyschology, general", schedule: "M/W/F, 3-4:00pm", state: Com_Zimbra_Edu.NOT_ENROLLED },
	"ZEDU-PHYS-103": { prof: "Jeanine Martin", title: "Intro to Physics, technical majors", schedule: "Not Available", state: Com_Zimbra_Edu.TAKEN },
	"ZEDU-CS-100": { prof: "Arlene Johnson", title: "Intro to Computer Science", schedule: "T/R, 11:00am-12:30", state: Com_Zimbra_Edu.ENROLLED },
	"ZEDU-CS-103": { prof: "Arlene Johnson", title: "Data Structures and Algorithms", schedule: "M/W/F, 11:00am-12:30", state: Com_Zimbra_Edu.NOT_ENROLLED }
};

Com_Zimbra_Edu.prototype.toolTipPoppedUp =
function(spanElement, obj, context, canvas) {
	var course = this._getCourseData(obj);
	var html = new Array(20);
	var idx = 0;
	html[idx++] = "<table cellpadding='0' cellspacing='0' border='0'>";
	html[idx++] = "<tr valign='center'><td colspan='2' align='left'>";
	html[idx++] = "<div style='border-bottom: 1px solid black; white-space:nowrap;'>";
	html[idx++] = "<table cellpadding='0' cellspacing='0' border='0' width=100%;'>";
	html[idx++] = "<tr valign='center'>";
	html[idx++] = "<td>";
	html[idx++] = "<b>" + AjxStringUtil.htmlEncode("Course: " + obj) + "</b>";
	html[idx++] = "</td>";
	html[idx++] = "<td align='right'>";
	//html[idx++] = AjxImg.getImageHtml("Task");
	html[idx++] = "<b>" + AjxStringUtil.htmlEncode(course.state) + "</b>";
	html[idx++] = "</td>";
	html[idx++] = "</table>";
	html[idx++] = "</div>";
	html[idx++] = "</td></tr>";
	idx = this._addEntryRow("Professor", course.prof, html, idx);
	idx = this._addEntryRow("Title", course.title, html, idx);
	idx = this._addEntryRow("Schedule", course.schedule, html, idx);
	html[idx++] = "</table>";
	canvas.innerHTML = html.join("");
};

//Com_Zimbra_Edu.prototype.toolTipPoppedDown =
//function(spanElement, obj, context, canvas) {
// alert("down");
//}
//
Com_Zimbra_Edu.prototype.menuItemSelected = 
function(menuItemId) {
	switch (menuItemId) {
		case "ENROLL":
			this._enrollListener()
			break;
		case "DROP":
			this._rejectListener()
			break;
		case "COURSES":
			// do something
			break;
		case "TRANSCRIPT":
			// do something
			break;
		case "ADD_TO_CALENDAR":
			// do something
			break;
		case "CLASSMATES":
			// do something
			break;
		case "STUDENT_MANAGER":
			// do something
			break;
		case "COURSE_MANAGER":
			// do something
			break;
	}
};

Com_Zimbra_Edu.prototype._getCourseData =
function(obj) {
	var course = Com_Zimbra_Edu.COURSE_DATA[obj];
	if (course == null)
		course = Com_Zimbra_Edu.COURSE_DATA[0];
	return course;
};

Com_Zimbra_Edu.prototype._addEntryRow =
function(field, data, html, idx) {
	html[idx++] = "<tr valign='top'><td align='right' style='padding-right: 5px;'><b>";
	html[idx++] = AjxStringUtil.htmlEncode(field) + ":";
	html[idx++] = "</b></td><td align='left' style='width:50%;'><div style='white-space:nowrap;'>";
	html[idx++] = AjxStringUtil.htmlEncode(data);
	html[idx++] = "</div></td></tr>";
	return idx;
};

Com_Zimbra_Edu.prototype._getStyle =
function(obj) {
	var course = this._getCourseData(obj);
	switch (course.state) {
		case Com_Zimbra_Edu.APPROVED: return "green";
		case Com_Zimbra_Edu.REJECTED: return "red";
		default: return "blue";
	}
};

Com_Zimbra_Edu.prototype._enrollListener =
function() {
	var obj = this._actionObject;
	var course = this._getCourseData(obj);
	if (course.state == Com_Zimbra_Edu.ENROLLED) {
	  alert("You are currently enrolled in " + obj);
	  return;
	}
	if (course.state == Com_Zimbra_Edu.TAKEN) {
	  alert("You have already completed " + obj);
	  return;
	}
	course.state = Com_Zimbra_Edu.ENROLLED;
	this._actionSpan.style.color = this._getStyle(obj);
};

Com_Zimbra_Edu.prototype._rejectListener =
function() {
	var obj = this._actionObject;
	var course = this._getCourseData(obj);

	if (course.state == Com_Zimbra_Edu.TAKEN) {
	  alert("You have already completed " + obj);
	  return;
	}
	if (course.state != Com_Zimbra_Edu.ENROLLED) {
	  alert("You are not currently enrolled in " + obj);
	  return;
	}
	course.state = Com_Zimbra_Edu.NOT_ENROLLED;
	this._actionSpan.style.color = this._getStyle(obj);
};