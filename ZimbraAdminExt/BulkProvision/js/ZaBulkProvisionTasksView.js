/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2006, 2007, 2008, 2009, 2010 Zimbra, Inc.
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
* @constructor
* @class ZaBulkProvisionTasksView
* @param parent
* @author Greg Solovyev
**/

ZaBulkProvisionTasksView = function(parent) {

	var className = null;
	var posStyle = DwtControl.ABSOLUTE_STYLE;
	
	var headerList = this._getHeaderList();
	
	ZaListView.call(this, parent, className, posStyle, headerList);

	this._appCtxt = this.shell.getData(ZaAppCtxt.LABEL);
	
}

ZaBulkProvisionTasksView.prototype = new ZaListView;
ZaBulkProvisionTasksView.prototype.constructor = ZaBulkProvisionTasksView;
ZaBulkProvisionTasksView.prototype.toString = 
function() {
	return "ZaBulkProvisionTasksView";
}

ZaBulkProvisionTasksView.prototype.getTitle = 
function () {
	return com_zimbra_bulkprovision.BP_view_title;
}

ZaBulkProvisionTasksView.prototype.getTabIcon = 
function () {
	return "BulkProvision" ;
}

/**
* Renders a single item as a DIV element.
*/
ZaBulkProvisionTasksView.prototype._createItemHtml =
function(task, now, isDragProxy) {
	var html = new Array(50);
	var	div = document.createElement("div");
	div[DwtListView._STYLE_CLASS] = "Row";
	div[DwtListView._SELECTED_STYLE_CLASS] = div[DwtListView._STYLE_CLASS] + "-" + DwtCssStyle.SELECTED;
	div.className = div[DwtListView._STYLE_CLASS];
	this.associateItemWithElement(task, div, DwtListView.TYPE_LIST_ITEM);
	
	var idx = 0;
	html[idx++] = "<table width='100%' cellspacing='0' cellpadding='0'>";
	html[idx++] = "<tr>";
	var cnt = this._headerList.length;
	for(var i = 0; i < cnt; i++) {
		var field = this._headerList[i]._field;
		if(field == ZaBulkProvisionTask.A_totalTasks) {	
			// name
			html[idx++] = "<td align='left' width=" + this._headerList[i]._width + ">";
			html[idx++] = AjxStringUtil.htmlEncode(task.attrs[ZaBulkProvisionTask.A_totalTasks]);
			html[idx++] = "</td>";
		} else if(field == ZaBulkProvisionTask.A_finishedTasks) {	
			// description
			html[idx++] = "<td align='left' width=" + this._headerList[i]._width + ">";
			html[idx++] = AjxStringUtil.htmlEncode(task.attrs[ZaBulkProvisionTask.A_finishedTasks]);
			html[idx++] = "</td>";
		} else if(field == ZaBulkProvisionTask.A_owner) {	
			// description
			html[idx++] = "<td align='left' width=" + this._headerList[i]._width + ">";
			html[idx++] = AjxStringUtil.htmlEncode(task.attrs[ZaBulkProvisionTask.A_owner]);
			html[idx++] = "</td>";
		} else if(field == ZaBulkProvisionTask.A_status) {	
			// description
			html[idx++] = "<td align='left' width=" + this._headerList[i]._width + ">";
			if(task.attrs[ZaBulkProvisionTask.A_finishedTasks] == task.attrs[ZaBulkProvisionTask.A_totalTasks]) {
				html[idx++] = AjxStringUtil.htmlEncode(com_zimbra_bulkprovision.TaskComplete);	
			} else {
				html[idx++] = AjxStringUtil.htmlEncode(com_zimbra_bulkprovision.TaskInProgress);
			}
			
			html[idx++] = "</td>";
		}
	}
	html[idx++] = "</tr></table>";
	div.innerHTML = html.join("");
	return div;
}

ZaBulkProvisionTasksView.prototype._getHeaderList =
function() {

	var headerList = new Array();
//idPrefix, label, iconInfo, width, sortable, sortField, resizeable, visible
	var sortable=1;
	headerList[0] = new ZaListHeaderItem(ZaBulkProvisionTask.A_totalTasks, com_zimbra_bulkprovision.NumTotalAccts, null, "120px", null, null, true, true);
	headerList[1] = new ZaListHeaderItem(ZaBulkProvisionTask.A_finishedTasks, com_zimbra_bulkprovision.NumCompletedAccts, null, "120px", null, null, true, true);
	headerList[2] = new ZaListHeaderItem(ZaBulkProvisionTask.A_status, com_zimbra_bulkprovision.TaskStatus, null, "120px", null, null, true, true);
	headerList[3] = new ZaListHeaderItem(ZaBulkProvisionTask.A_owner, com_zimbra_bulkprovision.TaskOwner, null, "auto", null, null, true, true);
	return headerList;
}


