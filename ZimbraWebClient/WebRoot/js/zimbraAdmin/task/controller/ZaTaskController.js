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
        }
        this._taskContentPanel = new ZaTaskContentView(this._container, entry);
        this._taskContentPanel.setObject(entry);
    }
    return this._taskContentPanel;
}

ZaTaskController.prototype.addTask = function(task) {
    var index= this._workingInProcess.indexOfLike(task, task.getData);
    if (index == -1) {
        this._workingInProcess.add(task, undefined, true);
        this._taskContentPanel._localXForm.setInstanceValue(this._workingInProcess.getArray(),ZaTask.A_workingInProcess);
    }else{
        this._workingInProcess.replace(index, task);
        this._taskContentPanel._localXForm.setInstanceValue(this._workingInProcess.getArray(),ZaTask.A_workingInProcess);
    }
}

ZaTaskController.prototype.setExpanded = function(isExpanded) {
    // TODO  remove this to view manager
    var width;
    if (isExpanded) {
        width = 100;
    } else {
        width = 20;
    }
    window.skin.setToolWidth(width);
    window.skin._reflowApp();

    this._taskContentPanel._localXForm.setInstanceValue(isExpanded, ZaTask.A2_isExpanded);
}