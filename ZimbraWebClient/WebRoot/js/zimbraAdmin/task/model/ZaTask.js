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
ZaTask.A_serverStatus = "serverStatus";
ZaTask.A2_isExpanded = "expanded";
ZaTask.A2_isWIPExpanded = "WIPExpanded";
ZaTask.A2_isRTExpanded = "RTExpaneded";
ZaTask.A2_isServerExpaned = "ServerExpaned";

ZaTask.postLoadDataFunction = new Array();

ZaTask.loadMethod =
function(by, val) {
    this.attrs = new Object();
    this.attrs[ZaTask.A_workingInProcess] = [];
    this.attrs[ZaTask.A_runningTask] = [];
    this.attrs[ZaTask.A_serverStatus] = [];
    this.schedulePostLoading();
}
ZaItem.loadMethods["ZaTask"].push(ZaTask.loadMethod);

ZaTask.initMethod = function () {
	this[ZaTask.A2_isExpanded] = true;
    this[ZaTask.A2_isWIPExpanded] = true;
    this[ZaTask.A2_isRTExpanded] = true;
    this[ZaTask.A2_isServerExpaned] = true;
}
ZaItem.initMethods["ZaTask"].push(ZaTask.initMethod);

ZaTask.myXModel = {
    items: [
        {id:ZaTask.A_workingInProcess, ref:"attrs/" + ZaTask.A_workingInProcess, type:_LIST_, listItem:{type:_OBJECT_}},
        {id:ZaTask.A_runningTask, ref:"attrs/" + ZaTask.A_runningTask, type:_LIST_},
        {id:ZaTask.A_serverStatus, ref:"attrs/" + ZaTask.A_serverStatus, type:_LIST_},
        {id:ZaTask.A2_isExpanded, ref:ZaTask.A2_isExpanded, type:_ENUM_, choices:ZaModel.BOOLEAN_CHOICES},
        {id:ZaTask.A2_isWIPExpanded, ref:ZaTask.A2_isWIPExpanded, type:_ENUM_, choices:ZaModel.BOOLEAN_CHOICES},
        {id:ZaTask.A2_isRTExpanded, ref:ZaTask.A2_isRTExpanded, type:_ENUM_, choices:ZaModel.BOOLEAN_CHOICES},
        {id:ZaTask.A2_isServerExpaned, ref:ZaTask.A2_isServerExpaned, type:_ENUM_, choices:ZaModel.BOOLEAN_CHOICES}
    ]
};

// type 1: for working in process, 2 for running task
ZaTaskItem = function(constructor, cacheName, title, data, position, type, displayName, finishCallback, cacheDialog, dialogType, editData) {
    this.constructor = constructor;
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