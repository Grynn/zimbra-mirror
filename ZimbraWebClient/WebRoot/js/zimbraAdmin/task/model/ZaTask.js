/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2011, 2012, 2013 Zimbra Software, LLC.
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
 * Created by IntelliJ IDEA.
 * User: mingzhang
 * Date: 9/5/11
 * Time: 1:38 AM
 * To change this template use File | Settings | File Templates.
 */
ZaTask = function(noInit) {
	if (noInit) return;
	ZaItem.call(this, "ZaTask");
	this._init();
	this.type = "Task";
}

ZaTask.prototype = new ZaItem;
ZaTask.prototype.constructor = ZaTask;

ZaItem.loadMethods["ZaTask"] = new Array();
ZaItem.initMethods["ZaTask"] = new Array();

//object attributes
ZaTask.A_workingInProcess = "workingInProcess";
ZaTask.A_runningTask = "runningTask";
ZaTask.A2_isExpanded = "expanded";
ZaTask.A2_isWIPExpanded = "WIPExpanded";
ZaTask.A2_isRTExpanded = "RTExpaneded";
ZaTask.A2_isServerExpaned = "ServerExpaned";
ZaTask.A2_notificationCount = "notificationCount";

ZaTask.postLoadDataFunction = new Array();

ZaTask.loadMethod =
function(by, val) {
    this.attrs = new Object();
    this.attrs[ZaTask.A_workingInProcess] = [];
    this.attrs[ZaTask.A_runningTask] = [];
    this.schedulePostLoading();
}
ZaItem.loadMethods["ZaTask"].push(ZaTask.loadMethod);

ZaTask.initMethod = function () {
	this[ZaTask.A2_isExpanded] = false;
    this[ZaTask.A2_isWIPExpanded] = true;
    this[ZaTask.A2_isRTExpanded] = true;
    this[ZaTask.A2_isServerExpaned] = true;
}
ZaItem.initMethods["ZaTask"].push(ZaTask.initMethod);

ZaTask.myXModel = {
    items: [
        {id:ZaTask.A_workingInProcess, ref:"attrs/" + ZaTask.A_workingInProcess, type:_LIST_, listItem:{type:_OBJECT_}},
        {id:ZaTask.A_runningTask, ref:"attrs/" + ZaTask.A_runningTask, type:_LIST_},
        {id:ZaTask.A2_isExpanded, ref:ZaTask.A2_isExpanded, type:_ENUM_, choices:ZaModel.BOOLEAN_CHOICES},
        {id:ZaTask.A2_isWIPExpanded, ref:ZaTask.A2_isWIPExpanded, type:_ENUM_, choices:ZaModel.BOOLEAN_CHOICES},
        {id:ZaTask.A2_isRTExpanded, ref:ZaTask.A2_isRTExpanded, type:_ENUM_, choices:ZaModel.BOOLEAN_CHOICES},
        {id:ZaTask.A2_isServerExpaned, ref:ZaTask.A2_isServerExpaned, type:_ENUM_, choices:ZaModel.BOOLEAN_CHOICES},
        {id:ZaTask.A2_notificationCount, ref:ZaTask.A2_notificationCount, type:_NUMBER_, defaultValue:0}
    ]
};

// type 1: for working in process, 2 for running task
ZaTaskItem = function(viewForPopup, cacheName, title, data, position, type, displayName, finishCallback, cacheDialog, dialogType, editData) {
    this.viewForPopup = viewForPopup;
    this.cacheName = cacheName;
    this._title = title;
    this.data = data;
    this.position = position;
    this.type = type || 1;
    this.displayName = displayName;
    this.finishCallback = finishCallback;
    this.cacheDialog = cacheDialog;
}

ZaTaskItem.minDisplayName = new Object();
ZaTaskItem.prototype.getMinDisplayName =
function (title) {
    var ret ;
    if (!ZaTaskItem.minDisplayName[title]) {
        ZaTaskItem.minDisplayName[title] = 1;
        ret = title;
    } else {
        ret  = title + " " + ZaTaskItem.minDisplayName[title];
        ZaTaskItem.minDisplayName[title] ++;
    }
    return ret;
}

ZaTaskItem.prototype.toString = function() {
    if (!this.displayName)
        this.displayName = this.getMinDisplayName(this._title);
    return this.displayName;
}

ZaTaskItem.prototype.getData = function() {
    return this.data._uuid;
}

ZaTask.prototype.schedulePostLoading = function () {
    // Don't disturbe the task view rendering process, when view is realy, start to update data.
    var act = new AjxTimedAction(this, ZaTask.prototype.startPostLoading);
	AjxTimedAction.scheduleAction(act, 100);
}

ZaTask.prototype.startPostLoading = function () {
    for (var i = 0; i < ZaTask.postLoadDataFunction.length; i++) {
        ZaTask.postLoadDataFunction[i].call(this);
    }
}