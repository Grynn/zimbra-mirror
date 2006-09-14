/*
 * ***** BEGIN LICENSE BLOCK *****
 * Version: ZPL 1.1
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.1 ("License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.zimbra.com/license
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See
 * the License for the specific language governing rights and limitations
 * under the License.
 * 
 * The Original Code is: Zimbra Collaboration Suite Web Client
 * 
 * The Initial Developer of the Original Code is Zimbra, Inc.
 * Portions created by Zimbra are Copyright (C) 2006 Zimbra, Inc.
 * All Rights Reserved.
 * 
 * Contributor(s):
 * 
 * ***** END LICENSE BLOCK *****
 */

//////////////////////////////////////////////////////////////
//  Calendar Reminder Zimlet.                               //
//  @author Kevin Henrikson                                 //
//////////////////////////////////////////////////////////////

function Com_Zimbra_Reminder() {
}

Com_Zimbra_Reminder.prototype = new ZmZimletBase();
Com_Zimbra_Reminder.prototype.constructor = Com_Zimbra_Reminder;
Com_Zimbra_Reminder._reminders = [];

Com_Zimbra_Reminder.prototype.init =
function() {
	this._setReminders();
	buttons = [DwtDialog.OK_BUTTON];
	this._msgDialog = new DwtDialog(this._appCtxt.getShell(),null, null, buttons);
	this._dialog = new DwtDialog(this._appCtxt.getShell(),null, null, buttons);
	this._reloadAction = new AjxTimedAction(this, this._setReminders, true);
	var reloadMS = (this.getUserProperty("reload") ? this.getUserProperty("reload") : 60) * 60000;
	AjxTimedAction.scheduleAction(this._reloadAction, reloadMS);
};

Com_Zimbra_Reminder.prototype.singleClicked = 
function() {
	this._dialog.setTitle(this.getMessage('list_reminders_title'));
	if(Com_Zimbra_Reminder._reminders.length < 1) {
		this._dialog.setContent("<b>this.getMessage('no_reminders')</b>");
	} else {
		var i = 0;
		var html = [];
		html[i++] = "<table id=\"reminderTable\" style=\"width: 300px;border: 1px solid grey; padding: 0.5em;\"><tr><td><img src=\"/img/loRes/common/CloseDis.gif\"/></td><td><b>";
		html[i++] = this.getMessage('appointment');
		html[i++] = "</b></td><td><b>";
		html[i++] = this.getMessage('reminder');
		html[i++] = "</b></td></tr>";
		for(var j=0;j<Com_Zimbra_Reminder._reminders.length;j++) {
			html[i++] = "<tr id=\"remind" + j + "\"><td><img onclick=\"Com_Zimbra_Reminder._cancelReminder(" + j;
			html[i++] = ")\" src=\"/img/loRes/common/Close.gif\"/></td><td>";
			html[i++] = Com_Zimbra_Reminder._reminders[j].name;
			html[i++] = "</td><td>";
			html[i++] = Com_Zimbra_Reminder._reminders[j].remindTime;
			html[i++] = " (";
			html[i++] = parseInt((Com_Zimbra_Reminder._reminders[j].remindTime - (new Date()).getTime())/60000,10);
			html[i++] = ") </td></tr>";
		}
		html[i++] = "</table>";
		this._dialog.setContent(html.join(""));
	}
	this._dialog.popup();
};

Com_Zimbra_Reminder.prototype.menuItemSelected = 
function(itemId) {
	switch (itemId) {
		case "PREFERENCES":
		this.createPropertyEditor();
		break;
		case "GET_NEW":
		this._setReminders(true);
		break;
	}
};

Com_Zimbra_Reminder.prototype._remind = 
function(eventInfo) {
	var evInfo = eventInfo ? eventInfo : "Test Reminder Appt";
	DBG.println(AjxDebug.DBG3, "Reminder Zimlet REMINDING: " + evInfo);
	this._msgDialog.reset();
	this._msgDialog.setContent("<pre>" + evInfo + "</pre>");
	this._msgDialog.setTitle(this.getMessage('remind_title'));
	this._msgDialog.popup();
	Com_Zimbra_Reminder._reminders.shift();
};

Com_Zimbra_Reminder._cancelReminder = 
function(id) {
	DBG.println("Reminder Zimlet Remove: '" + Com_Zimbra_Reminder._reminders[id].name + "'");
	AjxTimedAction.cancelAction(Com_Zimbra_Reminder._reminders[id].action);
	Com_Zimbra_Reminder._reminders[id] = null;
	Com_Zimbra_Reminder._reminders = AjxUtil.collapseList(Com_Zimbra_Reminder._reminders);
	var table = document.getElementById("reminderTable");
	table.deleteRow(id+1);	
};

Com_Zimbra_Reminder.prototype._setReminders = 
function(reload) {
	if(reload) {
		for(var j=0;j<Com_Zimbra_Reminder._reminders.length;j++) {
			AjxTimedAction.cancelAction(Com_Zimbra_Reminder._reminders[j].action);
		}
		Com_Zimbra_Reminder._reminders = [];
	}
	// Default to 5 min if no user property is set.
	var alarmBeforeMS = (this.getUserProperty("remind") ? this.getUserProperty("remind") : 5) * 60000;
	var cc = this._appCtxt.getApp(ZmZimbraMail.CALENDAR_APP).getCalController();
	try {
		var now = new Date();
		// Get any events in the next 5 hours
		var result = cc.getApptSummaries(now.getTime(), now.getTime()+(AjxDateUtil.MSEC_PER_HOUR * 5), true, cc.getCheckedCalendarFolderIds());
		var array = result.getArray();
		for(var i=0; i < array.length; i++) {
			// NOW - startTime - alarmBefore = delta from now to alarm.
			var deltaMSec = array[i].startDate.getTime() - now.getTime();
			deltaMSec = deltaMSec - alarmBeforeMS;
			if (deltaMSec < 0) {continue;}
			DBG.println(AjxDebug.DBG3, "Setting reminder: '" + array[i].getName() + "' in: " + parseInt(deltaMSec/60000,10) + " min");
			var reminderAction = new AjxTimedAction(this, this._remind, array[i].getTextSummary());
			AjxTimedAction.scheduleAction(reminderAction, deltaMSec);
			var remindTime = AjxDateUtil.computeTimeString(new Date(now.getTime() + deltaMSec));
			Com_Zimbra_Reminder._reminders.push({action: reminderAction, name: array[i].getName(), remindTime: remindTime});
		}
	} catch (ex) {
		DBG.println("Reminder Zimlet Exception: " + ex);
	}
};