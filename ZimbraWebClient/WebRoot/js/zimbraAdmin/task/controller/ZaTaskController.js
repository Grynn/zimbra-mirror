/**
 * Created by IntelliJ IDEA.
 * User: mingzhang
 * Date: 9/5/11
 * Time: 12:27 AM
 * To change this template use File | Settings | File Templates.
 */
ZaTaskController = function(appCtxt, container) {
	ZaController.call(this, appCtxt, container,"ZaTaskController");
    this._workingInProcess = new AjxVector();
    this._workingInProcess.getArray()._version = 1;
    this._runningTask = new AjxVector();
    this._runningTask.getArray()._version = 1;
}

ZaTaskController.prototype = new ZaController();
ZaTaskController.prototype.constructor = ZaTaskController;


ZaTaskController.prototype.getTaskHeaderPanel =
function() {
    if (!this._taskHeadPanel) {
        this._taskHeadPanel = new ZaTaskHeaderPanel(this._container);
    }
    return this._taskHeadPanel;
}

ZaTaskController.prototype.getTaskContentPanel =
function(entry) {
    if (!this._taskContentPanel) {
        if(!entry) {
            entry = new ZaTask();
            entry.load(false, false, true);
            entry[ZaModel.currentTab] = "1";
            entry[ZaModel.currentStep] = 1;
        }
        this._taskContentPanel = new ZaTaskContentView(this._container, entry);
        this._taskContentPanel.setObject(entry);
    }
    return this._taskContentPanel;
}

ZaTaskController.prototype.addTask = function(task) {
    var taskArray;
    var modelItem;
    if (task.type ==1) {
        taskArray =  this._workingInProcess;
        modelItem = ZaTask.A_workingInProcess;
    } else {
        taskArray =  this._runningTask;
        modelItem = ZaTask.A_runningTask;
    }
    var index= taskArray.indexOfLike(task, task.getData);
    taskArray.getArray()._version = taskArray.getArray()._version + 1;
    if (index == -1) {
        taskArray.add(task, undefined, true);
        this._taskContentPanel._localXForm.setInstanceValue(taskArray.getArray(),modelItem);
    }else{
        var currentTask = taskArray.get(index);
        task.displayName = currentTask.displayName;
        taskArray.replace(index, task);
        this._taskContentPanel._localXForm.setInstanceValue(taskArray.getArray(),modelItem);
    }
}

ZaTaskController.prototype.removeTask = function(task) {
    var taskArray;
    var modelItem;
    if (task.type ==1) {
        taskArray =  this._workingInProcess;
        modelItem = ZaTask.A_workingInProcess;
    } else {
        taskArray =  this._runningTask;
        modelItem = ZaTask.A_runningTask;
    }
    var index= taskArray.indexOfLike(task, task.getData);
    if (index != -1) {
        taskArray.getArray()._version = taskArray.getArray()._version + 1;
        taskArray.removeAt(index);
        this._taskContentPanel._localXForm.setInstanceValue(taskArray.getArray(),modelItem);
    }
}

ZaTaskController.prototype.setExpanded = function(isExpanded) {
    // TODO  remove this to view manager
    var width;
    if (isExpanded) {
        width = 220;
    } else {
        width = 20;
    }
    window.skin.setToolWidth(width);
    window.skin._reflowApp();

    this._taskContentPanel._localXForm.setInstanceValue(isExpanded, ZaTask.A2_isExpanded);
}

ZaTaskController.prototype.setInstanceValue = function(value, ref){
    if( this._taskContentPanel && this._taskContentPanel._localXForm){
        this._taskContentPanel._localXForm.setInstanceValue(value, ref);
    }
}