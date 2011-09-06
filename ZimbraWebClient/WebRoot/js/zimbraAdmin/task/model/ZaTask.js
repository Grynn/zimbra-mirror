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

ZaTask.loadMethod =
function(by, val) {
    this.attrs = new Object();
}
ZaItem.loadMethods["ZaTask"].push(ZaTask.loadMethod);

ZaTask.initMethod = function () {
	this.attrs = new Object();
}
ZaItem.initMethods["ZaTask"].push(ZaTask.initMethod);

ZaTask.myXModel = {
    items: [
        {id:ZaTask.A_workingInProcess, ref:"attrs/" + ZaTask.A_workingInProcess, type:_LIST_},
        {id:ZaTask.A_runningTask, ref:"attrs/" + ZaTask.A_runningTask, type:_LIST_}
    ]
};

