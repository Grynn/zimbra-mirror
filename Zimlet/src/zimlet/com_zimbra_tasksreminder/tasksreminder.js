/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Zimlets
 * Copyright (C) 2009, 2010 Zimbra, Inc.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.3 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 * Author: Raja Rao DV (rrao@zimbra.com)
 */


com_zimbra_tasksreminder_handlerObj = function() {
	ZmZimletBase.call(this);
};
com_zimbra_tasksreminder_handlerObj.prototype = new ZmZimletBase;
com_zimbra_tasksreminder_handlerObj.prototype.constructor = com_zimbra_tasksreminder_handlerObj;


var TaskReminderZimlet = com_zimbra_tasksreminder_handlerObj;

TaskReminderZimlet.prototype.toString = function() {
	return "TaskReminderZimlet";
};


TaskReminderZimlet.prototype.init = function() {
	this.taskreminder_lastShownDate = this.getUserProperty("taskreminder_lastShownDate");
	var todayStr = this._getTodayStr();
	if(this.taskreminder_lastShownDate != todayStr) {
		this._searchField = appCtxt.getSearchController().getSearchToolbar().getSearchField();
		this.prevSearchFieldVal =this._searchField.value;
		this.setSearcholders();
		this.runSearch();
		this.setUserProperty("taskreminder_lastShownDate", todayStr, true);
		this._searchField.value = this.prevSearchFieldVal; //reset search-field's value
	}else {
		return;
	}		
};

TaskReminderZimlet.prototype.setSearcholders =
function() {
	var tmpArry = new Array();
	tmpArry.push("is:local");

	var aCtxt = appCtxt.isChildWindow ? parentAppCtxt : appCtxt;
	var tasksApp = aCtxt.getApp(ZmApp.TASKS);
	AjxDispatcher.run("GetTaskController");

	tasksApp._createDeferredFolders();
	var folders = appCtxt.getFolderTree().root.children.getArray();
	if (folders) {
		for (var i = 0; i < folders.length; i++) {
			var f = folders[i];			
			if (f && f.type == "TASK" && f.isMountpoint) {
				tmpArry.push("inid:"+f.nId);
			}
		}
	}
	if(tmpArry.length == 1)
		this.searchInFolders = "";
	else
		this.searchInFolders = " ("+tmpArry.join(" OR ") + ")";
};

TaskReminderZimlet.prototype.runSearch = function(response) {
	this.taskreminder_beforedays = parseInt(this.getUserProperty("taskreminder_beforedays"));
	this.taskreminder_afterdays = parseInt(this.getUserProperty("taskreminder_afterdays"));
	var callbck = new AjxCallback(this, this._handleSearchResponse);
	var _types = new AjxVector();
	_types.add("TASK");
	var todayDate = new Date();
	var todayStart = new Date(todayDate.getFullYear(),todayDate.getMonth(), todayDate.getDate());
	var daysback200 = new Date(todayStart.getTime() - (200 * 24 * 3600 * 1000));
	var daysback200_normalized = this._normalizeDate(daysback200.getMonth()+1,  daysback200.getDate(), daysback200.getFullYear());
	appCtxt.getSearchController().search({query: "after:\""+daysback200_normalized+"\" and (not in:\"trash\")"+ this.searchInFolders, userText: true, limit:200, types:_types, noRender:true,  callback:callbck});
};

TaskReminderZimlet.prototype._handleSearchResponse = function(response) {
	var tasks = response.getResponse().getResults("TASK").getArray();
	if(tasks.length == 0) {
		return;
	}
	var taskObjs = new Array();
	var tmp = new Date();
	var showReminder = false;
	var today = new Date(tmp.getFullYear(), tmp.getMonth(), tmp.getDate());
	for(var i =0; i< tasks.length; i++) {
		var task = tasks[i];
		var endDate = new Date(task.endDate.getFullYear(), task.endDate.getMonth(), task.endDate.getDate());
		var endDate_normalized =this._normalizeDate(endDate.getMonth()+1,  endDate.getDate(), endDate.getFullYear());
		var overdue = (endDate - today) / (3600 * 24 * 1000);
		if((overdue < 0 && (overdue *-1) <= this.taskreminder_afterdays) || (overdue >=0 && overdue <= this.taskreminder_beforedays) ){
			taskObjs.push(
				{overdue:overdue, 
				subject:task.name, 
				folder:task.getFolder().name,
				organizer:task.organizer, 
				fragment:task.fragment, 
				task:task, 
				endDateTxt:endDate_normalized, 
				pComplete:task.pComplete,
				status: ZmCalItem.getLabelForStatus(task.status)
			});
			showReminder =true;
		}		
	}

	if(showReminder) {
		this._showTasksReminderDialog(taskObjs);
	} 

};


TaskReminderZimlet.prototype._showTasksReminderDialog = function(taskObjs) {
	//if zimlet dialog already exists...
	if (this._tskReminderDialog) {
		this._tskReminderDialog.popup();
		return;
	}
	this._tskReminderDlgView = new DwtComposite(this.getShell());
	this._tskReminderDlgView.setSize("510", "400");
	this._tskReminderDlgView.getHtmlElement().style.overflow = "auto";
	this._tskReminderDlgView.getHtmlElement().innerHTML = this._createTasksReminderView(taskObjs);
	var sendPrefEmailBtnId = Dwt.getNextId();
	var sendPrefEmailBtn = new DwtDialog_ButtonDescriptor(sendPrefEmailBtnId, this.getMessage("sendEmail"), DwtDialog.ALIGN_LEFT);

	this._tskReminderDialog = this._createDialog({title:this.getMessage("tasksReminder"), view:this._tskReminderDlgView, standardButtons:[DwtDialog.CANCEL_BUTTON], extraButtons:[sendPrefEmailBtn]});
	this._tskReminderDialog.setButtonListener(sendPrefEmailBtnId, new AjxListener(this, this._sendEmailWithPrefInfo));
	this._tskReminderDialog.popup();
	this._searchField.value = this.prevSearchFieldVal; //reset search-field's value
};


TaskReminderZimlet.prototype._createTasksReminderView = function(taskObjs) {
	this.taskObjs = taskObjs.sort(sortTasksByOverDue);
	var html = new Array();
	var i = 0;
	for(var j =0; j < this.taskObjs.length; j++) {
		var to = this.taskObjs[j];
		html[i++] = "<DIV  class='tskreminder_cardHdrDiv'>";
		html[i++] = "<TABLE width='100%' CELLPADDING=3 >";
		html[i++] = "<TR>";
		html[i++] = "<TD>";
		html[i++] = "<span style=\"font-weight:bold;font-size:14px\"> " + to.subject + "</SPAN>";
		html[i++] = "<span style=\"color:gray\">  - " +to.status +", "+ to.pComplete + "% "+this.getMessage("complete")+"</SPAN>";

		html[i++] = "</TD>";
		html[i++] = "</TR>";
		html[i++] = "<TR>";
		html[i++] = "<TD>";
		html[i++] = "<span>"+this.getMessage("endDate")+" " + to.endDateTxt + "</SPAN>";
		var days = "days";
		if(to.overdue == 1 || to.overdue == -1) {
			days = "day";
		}

		if(to.pComplete == 100) {
			html[i++] = "<span style=\"font-weight:bold;color:green\"> ";
			html[i++] = this.getMessage("taskComplete");
			html[i++] = "</SPAN>";	
		} else if(to.overdue < 0 && to.pComplete < 100) {
			html[i++] = "<span style=\"font-weight:bold;color:red\"> "+(to.overdue * -1) + " "+days+" ";
			html[i++] = this.getMessage("overdue");
			html[i++] = "</SPAN>";
		} else {
			html[i++] = "<span style=\"font-weight:bold;color:orange\"> "+(to.overdue) + " "+days+" ";
			html[i++] = this.getMessage("left");
			html[i++] = "</SPAN>";
		}
		html[i++] = "</TD>";
		html[i++] = "<TR>";

		html[i++] = "</DIV>";
	}
	return html.join("");
}


//return string like: 10/20/2009
TaskReminderZimlet.prototype._getTodayStr = function() {
	var todayDate = new Date();
	var todayStart = new Date(todayDate.getFullYear(),todayDate.getMonth(), todayDate.getDate());
	return this._normalizeDate(todayStart.getMonth()+1,  todayStart.getDate(), todayStart.getFullYear());
};

function sortTasksByOverDue(a, b) {
	var x = a.overdue;
	var y = b.overdue;
	if(b.pComplete == 100)
		return 0;

	return ((x < y) ? -1 : ((x > y) ? 1 : 0));
}


TaskReminderZimlet.prototype._normalizeDate =
function(month, day, year) {
	var fString = [];
	var ds = I18nMsg.formatDateShort.toLowerCase();
	var arry = [];
	var separator = ds.replace("d", "").replace("y","").replace("m","").substring(0,1);
	arry.push({name:"m", indx:ds.indexOf("m")});
	arry.push({name:"yy", indx:ds.indexOf("yy")});
	arry.push({name:"d", indx:ds.indexOf("d")});
	var sArry = arry.sort(taskReminder_sortTimeObjs);
	for(var i = 0; i < sArry.length; i++) {
		var name = sArry[i].name;
		if(name == "m") {
			fString.push(month);
		} else if(name == "yy") {
			fString.push(year);
		}  else if(name == "d") {
			fString.push(day);
		} 
	}
	return fString.join(separator);
};

function taskReminder_sortTimeObjs(a, b) {
	var x = parseInt(a.indx);
	var y = parseInt(b.indx);
	return ((x > y) ? 1 : ((x < y) ? -1 : 0));
}

TaskReminderZimlet.prototype._sendEmailWithPrefInfo = function() {
	var action = ZmOperation.NEW_MESSAGE;
	var msg = new ZmMailMsg();
	var toOverride = null;

	var subjOverride = this.getMessage("tasksReminder");
	var extraBodyText = this._constructEmailBdy();
	AjxDispatcher.run("Compose", {action: action, inNewWindow: false, msg: msg,
		toOverride: toOverride, subjOverride: subjOverride,
		extraBodyText: extraBodyText});
	if (this._tskReminderDialog) {
		this._tskReminderDialog.popdown();
	}

};

TaskReminderZimlet.prototype._constructEmailBdy =
function() {
	var newLine = "";
	if (appCtxt.getSettings().getSetting("COMPOSE_AS_FORMAT").value == "text") {
		newLine = "\r\n";
	} else {
		newLine = "<BR/>";
	}

	var html = new Array();
	var i = 0;
	for(var j =0; j < this.taskObjs.length; j++) { 
		var to = this.taskObjs[j];
		var days = this.getMessage("days");
		if(to.overdue == 1 || to.overdue == -1) {
			days = this.getMessage("day");
		}
		var taskCompleteStr = "";
		if(to.pComplete == 100) {
			 taskCompleteStr =" "+this.getMessage("taskComplete");	
		} else if(to.overdue < 0 && to.pComplete < 100) {
				taskCompleteStr =[(to.overdue * -1) , " ", days ," overdue"].join("");
		} else {
			taskCompleteStr =[(to.overdue), " ", days, " left"].join("");
		}

		html[i++] = ["-----------",this.getMessage("task")," (",taskCompleteStr ,")-----------------" , newLine].join("");
		html[i++] = [this.getMessage("subject")," ", to.subject, newLine].join("");
		html[i++] = [this.getMessage("status")," ", to.status, ,newLine, "  ",this.getMessage("percentComplete")," ", to.pComplete, "%",newLine].join("");
		html[i++] = [this.getMessage("endDate"), " ",to.endDateTxt, newLine, newLine, newLine].join("");
	}
	return html.join("");
};


TaskReminderZimlet.prototype.doubleClicked = function() {
	this.singleClicked();
};
TaskReminderZimlet.prototype.singleClicked = function() {
	this._showPreferenceDlg();
};


TaskReminderZimlet.prototype._showPreferenceDlg = function() {

	this.taskreminder_beforedays = this.getUserProperty("taskreminder_beforedays");
	this.taskreminder_afterdays = this.getUserProperty("taskreminder_afterdays");
	this.taskreminder_lastShownDate = this.getUserProperty("taskreminder_lastShownDate");

	//if zimlet dialog already exists...
	if (this._preferenceDialog) {
		this._preferenceDialog.popup();
		return;
	}
	this._preferenceView = new DwtComposite(this.getShell());
	this._preferenceView.getHtmlElement().innerHTML = this._createPrefView();
	var showAgainDlgBtnId = Dwt.getNextId();
	var showAgainDlgBtn = new DwtDialog_ButtonDescriptor(showAgainDlgBtnId, this.getMessage("showRemindersAgain"), DwtDialog.ALIGN_LEFT);
	this._preferenceDialog = this._createDialog({title:this.getMessage("preferences"), view:this._preferenceView, standardButtons:[DwtDialog.OK_BUTTON], extraButtons:[showAgainDlgBtn]});
	this._preferenceDialog.setButtonListener(showAgainDlgBtnId, new AjxListener(this, this._showAgainListener));
	this._preferenceDialog.setButtonListener(DwtDialog.OK_BUTTON, new AjxListener(this, this._okBtnListener));
	this._preferenceDialog.popup();
	this.setPrefValues();

};

TaskReminderZimlet.prototype._showAgainListener = function() {
		this._searchField = appCtxt.getSearchController().getSearchToolbar().getSearchField();
		this.prevSearchFieldVal =this._searchField.value;
		this.setSearcholders();
		this.runSearch();
		this._searchField.value = this.prevSearchFieldVal; //reset search-field's value
};

TaskReminderZimlet.prototype.setPrefValues = function() {
	document.getElementById("taskreminder_beforedays").value = this.taskreminder_beforedays;
	document.getElementById("taskreminder_afterdays").value = this.taskreminder_afterdays;
};

TaskReminderZimlet.prototype._createPrefView = function() {
	var html = new Array();
	var i = 0;
	var bField = "<input style=\"width: 30px\" id='taskreminder_beforedays'  type='text'/>";
	var eField = "<input style=\"width: 30px\"  id='taskreminder_afterdays'  type='text'/>";


	var remindMeStr = this.getMessage("remindMeStr").replace("{0}", bField).replace("{1}", eField);
	html[i++] = "<DIV style=\"font-weight:bold;font-size:14px\">";
	html[i++] = "Select Reminder Range:";
	html[i++] = "</DIV>";
	html[i++] = "<DIV>";
	html[i++] = remindMeStr;
	html[i++] = "</DIV>";
	html[i++] = "<BR/>";
	html[i++] = "<BR/>";
	html[i++] = "<DIV style=\"font-weight:bold;color:gray\">";
	html[i++] = this.getMessage("note");
	html[i++] = "</DIV>";
	return html.join("");
};

TaskReminderZimlet.prototype._okBtnListener = function() {
	this._preferenceDialog.popdown();
	if (document.getElementById("taskreminder_beforedays").value != this.taskreminder_beforedays) {
		this.setUserProperty("taskreminder_beforedays", document.getElementById("taskreminder_beforedays").value);
		this._mustSave = true;

	}
	if (document.getElementById("taskreminder_afterdays").value != this.taskreminder_afterdays) {
		this.setUserProperty("taskreminder_afterdays", document.getElementById("taskreminder_afterdays").value);
		this._mustSave = true;
	}
	if(this._mustSave) {
		this.saveUserProperties();
		appCtxt.getAppController().setStatusMsg(this.getMessage("propertiesSaved"), ZmStatusView.LEVEL_INFO);
	}
}; 
