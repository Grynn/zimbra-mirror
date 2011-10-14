/*
 * ***** BEGIN LICENSE BLOCK *****
 *
 * Zimbra Collaboration Suite Zimlets
 * Copyright (C) 2006, 2007 Zimbra, Inc.
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
 *@Author Raja Rao DV
 */

function ZmMultiTimezoneZimlet() {
}
ZmMultiTimezoneZimlet.prototype = new ZmZimletBase();
ZmMultiTimezoneZimlet.prototype.constructor = ZmMultiTimezoneZimlet;

ZmMultiTimezoneZimlet.BTN_SUFFIX = "_mtz_btn_suffix";
ZmMultiTimezoneZimlet.DIV_SUFFIX = "_mtz_div_suffix";

ZmMultiTimezoneZimlet.WORKING_HR_START = 8;  // 8:00 AM is the default Zimbra setting
ZmMultiTimezoneZimlet.WORKING_HR_END = 17.5; //use 5:30 as we dont highlight 30 mins

ZmMultiTimezoneZimlet.prototype.init =
function() {
	this._viewIdAndButtonMap = [];
	this._viewIdAndTZRowIdsMap = [];
	this._viewIdAndTableGridMap = [];
	this._viewIdAndWorkingHrsMap = [];
	//this.popupDialog();
};

ZmMultiTimezoneZimlet.prototype.onShowView =
		function(viewId) {
			if(viewId.indexOf("APPT") == 0) {
				this._viewIdAndWorkingHrsMap[viewId] = [];
				//this._toggleByViewId(viewId, true);
			}
		};

ZmMultiTimezoneZimlet.prototype._popupDialog =
		function(aev, viewId) {
			if (!this._dialog) {
				this._currentTZ = AjxTimezone.getClientId(AjxTimezone.DEFAULT);
				this._checkboxIdAndItemMap = [];
				this._divIdAndDisplayTextMap = [];				
				this._date = new Date();
				var view = new DwtComposite(this.getShell());
				view.setSize("300", "300");
				//view.getHtmlElement().style.overflow = "auto";
				
				view.getHtmlElement().innerHTML = this._getDialogContent();
				this._dialog = new ZmDialog({title:"Select Timezones", view:view, standardButtons:[DwtDialog.OK_BUTTON, DwtDialog.CANCEL_BUTTON], parent:this.getShell()});

				this._addDialogHandlers();
			}

			this._resetFilterField();
			this._resetDialogRowsAndCheckboxes();
			this._dialog.aev = aev;
			this._dialog.viewId = viewId;
			this._dialog.popup();
		};

ZmMultiTimezoneZimlet.prototype._addDialogHandlers =
function() {
	this._dialog.setButtonListener(DwtDialog.OK_BUTTON,new AjxListener(this,this._dialogOKBtnListener));
};

ZmMultiTimezoneZimlet.prototype._handleFilterFieldKeys = function(ev) {
	var currentVal = this._filterField.value.toLowerCase();
	var initialFilterText = this._initialFilterText.toLowerCase();
	if (currentVal == initialFilterText) {
		this._filterField.value = "";
		this._filterField.style.color = "black";
		return;
	}
	for(var divId in this._divIdAndDisplayTextMap) {
		var text = this._divIdAndDisplayTextMap[divId].toLowerCase();
		var div = document.getElementById(divId);
		if(currentVal == "" || text.indexOf(currentVal) >= 0) {
			div.style.display = "block";
		} else {
			div.style.display = "none";
		}
	}
};

ZmMultiTimezoneZimlet.prototype._dialogOKBtnListener =
function() {
	var selectedItems = [];
	for(var checkboxId in this._checkboxIdAndItemMap){
		var checkbox = document.getElementById(checkboxId);
		if(checkbox && checkbox.checked) {
			var item = this._checkboxIdAndItemMap[checkboxId];
			var timeDiff = this._gettimeDiff(item.value);
			selectedItems.push({timeDiff: timeDiff, label: item.displayValue});
		}
	}
	this._showTimeZones(this._dialog.aev, this._dialog.viewId, selectedItems);
	this._dialog.popdown();
};

ZmMultiTimezoneZimlet.prototype._gettimeDiff =
		function(reqTZ) {
			var offset1 = AjxTimezone.getOffset(this._currentTZ, this._date);
			var offset2 = AjxTimezone.getOffset(reqTZ, this._date);
			var timeDiff = (offset2 - offset1)/60;
			//timeDiff = 24 - timeDiff;
			//if(timeDiff < 0) {
			//	timeDiff = timeDiff * -1;
			//}
			return timeDiff;
		};

ZmMultiTimezoneZimlet.prototype._resetDialogRowsAndCheckboxes =
		function() {
			for(var checkboxId in this._checkboxIdAndItemMap){
				var checkbox = document.getElementById(checkboxId);
				if(checkbox) {
					checkbox.checked = false;
				}
			}

			for(var divId in this._divIdAndDisplayTextMap) {
				var div = document.getElementById(divId);
				if(div) {
					div.style.display = "block";
				}
			}
		};

ZmMultiTimezoneZimlet.prototype._resetFilterField =
function() {
	if(!this._filterField) {
		this._filterField = document.getElementById("multiTimezonesZimlet_FilterField");
		this._filterField.onclick = AjxCallback.simpleClosure(this._handleFilterFieldKeys, this);
		this._filterField.onkeyup = AjxCallback.simpleClosure(this._handleFilterFieldKeys, this);
	}
	this._filterField.value = this._initialFilterText;
	this._filterField.style.color = "gray";
};

ZmMultiTimezoneZimlet.prototype._getDialogContent =
		function() {
			var isRowOdd = true;
			var html = [];
			var idx = 0;
			var items = AjxTimezone.getAbbreviatedZoneChoices();
			var len = items.length;
			html[idx++] = "<div>";
			html[idx++] = this._getFilterHtml();
			html[idx++] = "</div>";

			html[idx++] = "<div style='height:10px;width:100%' />";

			html[idx++] = "<div style='overflow: auto; height: 250px;'>";
			for(var i = 0; i < len; i++) {
				var rowClass = (isRowOdd) ? "RowOdd" : "RowEven";
				var item = items[i];
				var text = item.displayValue;
				var chkId = "multiTimezonesZimlet_"+Dwt.getNextId();
				var divId = chkId + "_div";
				this._checkboxIdAndItemMap[chkId] = item;
				this._divIdAndDisplayTextMap[divId] = text;
				html[idx++] = this._getRowHtml({text:text, chkId:chkId, divId:divId, rowClass: rowClass});
				isRowOdd = !isRowOdd;
			}
			html[idx++] = "</div>";
			return html.join("");

		};

ZmMultiTimezoneZimlet.prototype._getFilterHtml =
		function() {
			this._initialFilterText = "Type here to filter timezones";
			var html = [];
			var idx = 0;
			html[idx++] = "<table align=center><tr><td>";
			html[idx++] = "<div class='multiTimezone_filterDiv'>";
			html[idx++] = "<input  class='multiTimezone_filterField' id='multiTimezonesZimlet_FilterField' type='text' />";
			html[idx++] = "</div>";
			html[idx++] = "</td></tr></table>";
			return html.join("");
		};

ZmMultiTimezoneZimlet.prototype._getRowHtml =
		function(params) {
			var html = [];
			var idx = 0;
			var rowClass = params.rowClass;
			var chkId = params.chkId;
			var divId = params.divId;
			var text = params.text;
			html[idx++] = "<div id='";
			html[idx++] = divId;
			html[idx++] = "' class='";
			html[idx++] = rowClass;
			html[idx++] = " multiTimezone_row'>";
			html[idx++] = "<table width=100%>";
			html[idx++] = "<tr>";
			html[idx++] = "<td width=16px><input id='";
			html[idx++] = chkId;
			html[idx++] = "' type='checkbox'/></td>";
			html[idx++] = "<td>";
			html[idx++] = text;
			html[idx++] = "</td></tr>";
			html[idx++] = "</table>";
			html[idx++] = "</div>";
			return html.join("");
		};

ZmMultiTimezoneZimlet.prototype.initializeToolbar =
function(app, toolbar, controller, viewId) {

	//only add this button for the following 3 views
	if ((viewId.indexOf("APPT") >= 0) && (viewId.indexOf("APPTRO") == -1)) {
		if(!this._viewIdAndButtonMap[viewId]) {
			this._addmultiTimesZonesButton(controller, toolbar, viewId)

		}
	}
};

ZmMultiTimezoneZimlet.prototype._addmultiTimesZonesButton =
function(controller, toolbar, viewId) {
	/*
	if (!toolbar.getButton("MULTIPLE_TIMEZONES_ZIMLET")) {
		var buttonIndex = toolbar.opList.length + 1;
		var button = toolbar.createOp("MULTIPLE_TIMEZONES_ZIMLET", {image:"Calendar", text:"Multiple timezones", tooltip:this.getMessage("showsMultipleTimezones"), index:buttonIndex});
		toolbar.addOp("MULTIPLE_TIMEZONES_ZIMLET", buttonIndex);
		this._composerCtrl = controller;
		this._composerCtrl._multipleTimezonesZimlet = this;
		var aev =  appCtxt.getCurrentView().getApptEditView();
		button.addSelectionListener(new AjxListener(this, this._popupDialog, [aev, viewId]));
	} */

	try {
		var aev =  appCtxt.getCurrentView().getApptEditView();
		if(!aev) {
			return;
		}
		 this._overrideScheduleLinkListeners(aev, viewId);
	} catch(e) {
		//ignore
	}


};
/**
 * This function overrides ZmApptEditView's schedule button and schedule image's default behavior.
 * Default behavior is to simply call ZmCalItemEditView._onClick listener. The overriding functions
 * calls the same listeners but also calls toggleButton function of the Zimlet.
 *
 * We are overriding only to toggle hide/visible of Zimlet's Timezone button its own-view
 * @param aev
 */
ZmMultiTimezoneZimlet.prototype._overrideScheduleLinkListeners =
function(aev, viewId) {
	if(!aev) {
		return;
	}
	if(aev._schButton) {
		Dwt.setHandler(aev._schButton, DwtEvent.ONCLICK, AjxCallback.simpleClosure(this._toggleViews, this, aev, viewId));
	}
	if(aev._schImage) {
		Dwt.setHandler(aev._schImage, DwtEvent.ONCLICK,AjxCallback.simpleClosure(this._toggleViews, this, aev, viewId));
	}

};

ZmMultiTimezoneZimlet.prototype._toggleViews =
function(aev, viewId, ev) {
	//call calItem's onclick
	ZmCalItemEditView._onClick(ev);

	if(!this._viewIdAndButtonMap[viewId]) {
		 this._createButton(aev, viewId);
	}
	this._toggleByViewId(aev, viewId, false);

};

ZmMultiTimezoneZimlet.prototype._toggleByViewId =
function(aev, viewId, forceHide) {
	var zimletView = document.getElementById(viewId+ZmMultiTimezoneZimlet.DIV_SUFFIX);
	if(zimletView) {
		if(aev._schedulerOpened) {
			zimletView.style.display = "block";
		} else {
			zimletView.style.display = "none";
		}
	}
	/*
	if(zimletView) {
		if(forceHide) {
			zimletView.style.display = "none";
			return;
		}
		if(zimletView.style.display == "none") {
			zimletView.style.display = "block";
		} else {
			zimletView.style.display = "none";
		}
	}*/
};

ZmMultiTimezoneZimlet.prototype._getTableGrid =
function(aev, viewId) {
	 if(this._viewIdAndTableGridMap[viewId]) {
		 return this._viewIdAndTableGridMap[viewId];
	 }
	var attendeesTable = aev.getScheduleView()._attendeesTable;
	var table = document.createElement("table");
	var firstChild = attendeesTable.parentNode.firstChild;
	attendeesTable.parentNode.insertBefore(table, firstChild);
	table.width = "100%";
	table.cellPadding = "0";
	table.cellSpacing = "0";
	//table.innerHTML = "<colgroup><col style='width:165px' /><col style='width:626px' /></colgroup>";
	this._viewIdAndTableGridMap[viewId] = table;
	return table;
};

ZmMultiTimezoneZimlet.prototype._showTimeZones =
function(aev, viewId, timeDiffsMapArray) {

	for(var j = 0; j < timeDiffsMapArray.length; j++) {
		var obj = timeDiffsMapArray[j];
		var time = obj.timeDiff;
		var label = obj.label;
		if(time == undefined || !label) {
			continue;
		}
		var html = this._getTZCellsHtml(viewId, label, time);
		var row  = this._getTZRow(aev, viewId);
		if(row) {
			row.innerHTML = html;
		}
	}
	var workingHrsRow = this._getWorkingHrsRow(aev, viewId);
	workingHrsRow.innerHTML = this._getWorkingHrsHtml(aev, viewId);
	var lnk = document.getElementById(viewId + ZmMultiTimezoneZimlet.LNK_SUFFIX);
	if(lnk) {
		lnk.style.display = "block";
	}
};

ZmMultiTimezoneZimlet.prototype._getWorkingHrsRow =
		function(aev, viewId) {
			var row = document.getElementById(viewId + "_mtz_workingHrsRow");
			if(row) {
				var  parentNode = row.parentNode;
				parentNode.removeChild(row);
			}
			var row = this._getRow(aev, viewId);
			var rowId = viewId + "_mtz_workingHrsRow";
			row.id = rowId;
			this._viewIdAndTZRowIdsMap[viewId].push(rowId);
			return row;
		};

ZmMultiTimezoneZimlet.prototype._getTZRow =
		function(aev, viewId) {
			var row = this._getRow(aev, viewId);
			var rowId = viewId + "_mtzRow_" + Dwt.getNextId();
			row.id = rowId;
			this._viewIdAndTZRowIdsMap[viewId].push(rowId);
			return row;
		};


ZmMultiTimezoneZimlet.prototype._getRow =
		function(aev, viewId) {
			if(!this._viewIdAndTZRowIdsMap[viewId]) {
				this._viewIdAndTZRowIdsMap[viewId] = [];
			}
			var table = this._getTableGrid(aev, viewId);
			if(!table) {
				return;
			}
			return table.insertRow(0);
		};

ZmMultiTimezoneZimlet.prototype._getWorkingHrsHtml =
		function(aev, viewId) {
			var timeWidth = ((626/24) - 1);
			var origArry = this._viewIdAndWorkingHrsMap[viewId];
			var modifiedArry = [];
			//copy
			for(var i = 0; i < origArry.length; i++) {
				modifiedArry.push(origArry[i]);
			}
			modifiedArry = modifiedArry.sort();
			modifiedArry = modifiedArry.reverse();
			var highestNumber =  modifiedArry[0];
			var bestMatchFound = false;
			if(highestNumber > 0) {
				bestMatchFound = true;
			}
 			var html = [];
			html.push("<td width='165px' align='right' style='text-align: right;padding-right: 3px'>",
					"<label style=\"background-color:lightyellow;padding:3px;color:green;font-weight:bold;\">",this.getMessage("bestTimes"),"</label></td>");
			for(var i = 0; i < 24; i++) {
				var classStr = "";
				var currentMatch = origArry[i];
				//if(i == 0) {
				//	classStr = "multiTimezone_firstCell";
				//}
				//we also check current-timezone's 8-16 for us to show arrow
				if(bestMatchFound && currentMatch == highestNumber && i >= ZmMultiTimezoneZimlet.WORKING_HR_START && i <= ZmMultiTimezoneZimlet.WORKING_HR_END) {
					classStr = "multiTimezone_downArrow";
				}
				html.push("<td 	align='center' valign='top' class='",classStr,"' width='",timeWidth,"px'></td>");
			}
			return html.join("");
		};

ZmMultiTimezoneZimlet.prototype._getTZCellsHtml =
		function(viewId, label, time) {
			var pmStr = this.getMessage("pm");
			var timeWidth = ((626/24) - 1);
			var workingHoursStyle = "background:white;";
			var timeCellStyle = "border-top:1px solid #909090;border-bottom:1px solid #909090;height:22px;border-right:1px solid #909090;";

			if(label.length > 30) {
				label = label.substring(0, 27) + "...";
			}
			var html = [];
			html.push("<td width='165px' align='right' style='text-align: right;padding-right: 3px'>",label,"</td>");
			for(var i = 0; i < 24; i++) {
				var isWorkingHour = false;
				var isPM = false;
				var cellStyle = timeCellStyle;
				var timeStr = "";
				if(time > 12 && time < 24) {
					timeStr = (time  - 12);
					if(time <= ZmMultiTimezoneZimlet.WORKING_HR_END) {
						isWorkingHour = true;
					}
					isPM = true;
				} else if(time >= 24) {
					timeStr = (time - 24);
					time = time - 24;
					if(timeStr >= ZmMultiTimezoneZimlet.WORKING_HR_START) {
						isWorkingHour = true;
					}
				} else {
					timeStr = time;
					if(time >= ZmMultiTimezoneZimlet.WORKING_HR_START) {
						isWorkingHour = true;
					}
				}
				timeStr = timeStr == 0  ? "12" : (timeStr == 0.5 ? "12.5" : timeStr);
				timeStr = "" + timeStr;

				timeStr = timeStr.replace(".5", "<label style='font-size: 7px'>:30</label>");
				if(isPM) {
					timeStr = timeStr + "<br><label style='font-size: 7px'>"+pmStr+"</label>" ;
				}

				if(isWorkingHour) {
					cellStyle = cellStyle + workingHoursStyle;
					this._setWorkingHrs(viewId, i, true);
				} else {
					this._setWorkingHrs(viewId, i, false);
				}
				if(i == 0) {
					cellStyle = cellStyle + "border-left:1px solid #909090;";
				}
				var cellWidth = timeWidth;
				html.push("<td 	valign='top'  style='",cellStyle,"' width='",cellWidth,"px'>",timeStr,"</td>");
				time++;
			}
			return html.join("");
		};

ZmMultiTimezoneZimlet.prototype._setWorkingHrs =
function(viewId, cellNumber, isWorkingHour) {
	var currentVal = 0;
	if(!this._viewIdAndWorkingHrsMap[viewId][cellNumber]) {
		this._viewIdAndWorkingHrsMap[viewId][cellNumber] = 0;
	}else {
		currentVal = this._viewIdAndWorkingHrsMap[viewId][cellNumber];
	}
	if(isWorkingHour) {
		this._viewIdAndWorkingHrsMap[viewId][cellNumber] = ++currentVal;
	}
};

ZmMultiTimezoneZimlet.prototype._createButton =
function(aev, viewId) {
		var div = document.createElement("div");
		div.id = viewId + ZmMultiTimezoneZimlet.DIV_SUFFIX;
		div.style.display = "none";
		div.style.width = "300px";
		div.style.padding = "2px";
		div.innerHTML = this._getRemoveLinkHtml(viewId);
		var scheduleViewEl = aev.getScheduleView().getHtmlElement();

		var btn = new DwtButton({parent:this.getShell()});
		btn.setText("Show multiple timezones");
		btn.addSelectionListener(new AjxListener(this, this._popupDialog, [aev, viewId]));
		if(scheduleViewEl.parentNode) {
			var firstChild = scheduleViewEl.parentNode.firstChild;
			scheduleViewEl.parentNode.insertBefore(div, firstChild);
			var btnDiv = document.getElementById(viewId+ZmMultiTimezoneZimlet.BTN_SUFFIX);
			btnDiv.appendChild(btn.getHtmlElement());
			var lnk = document.getElementById(viewId + ZmMultiTimezoneZimlet.LNK_SUFFIX);
			if(lnk) {
				lnk.onclick = AjxCallback.simpleClosure(this._removeTZRows, this, viewId);
			}
			this._viewIdAndButtonMap[viewId] = true;
		}
};

ZmMultiTimezoneZimlet.prototype._removeTZRows =
function(viewId) {
   var rowIds = this._viewIdAndTZRowIdsMap[viewId];
	var parentNode;
	for(var i = 0; i < rowIds.length; i++) {
	   var row = document.getElementById(rowIds[i]);
		if(row) {
			if(!parentNode) {
				parentNode = row.parentNode;
			}
			if(parentNode) {
				parentNode.removeChild(row);
			}
		}
	}
	var lnk = document.getElementById(viewId + ZmMultiTimezoneZimlet.LNK_SUFFIX);
	if(lnk) {
		lnk.style.display = "none";
	}
	this._viewIdAndTZRowIdsMap[viewId] = [];
	this._viewIdAndWorkingHrsMap[viewId] =  [];
};

ZmMultiTimezoneZimlet.prototype._getRemoveLinkHtml =
function(viewId) {
	var html = [];
	var i = 0;
	html[i++] = "<table><tr><td>";
	html[i++] = "<div id='";
	html[i++] = viewId+ZmMultiTimezoneZimlet.BTN_SUFFIX;
	html[i++] = "'></div></td>";
	html[i++] = "<td><label style='display:none; color:#00008b;text-decoration: underline;cursor: pointer' id='";
	html[i++] =  viewId + ZmMultiTimezoneZimlet.LNK_SUFFIX;
	html[i++] = "' >Remove Timezones</label></td>";
	html[i++] = "</tr></table>";

	return html.join("");

};
