/**
 * Created by IntelliJ IDEA.
 * User: mingzhang
 * Date: 9/5/11
 * Time: 11:58 PM
 * To change this template use File | Settings | File Templates.
 */
ZaBaseDialog = function(parent,className, title, w, h,iKeyName, extraButtons, contextId) {
    if (arguments.length == 0) return;
    ZaXWizardDialog.call(this, parent,className,title, w, h,iKeyName, this._contextId);
    this.addMiniIcon();
    this.addPopdownListener(new AjxListener(this, this.popdownHookListner));
}

ZaBaseDialog.prototype = new ZaXWizardDialog;
ZaBaseDialog.prototype.constructor = ZaBaseDialog;

ZaBaseDialog.prototype.TEMPLATE = "admin.Widgets#ZaBaseDialog";

ZaBaseDialog.prototype._createHtmlFromTemplate =
function(templateId, data) {
	DwtDialog.prototype._createHtmlFromTemplate.call(this, templateId, data);
    this._minEl =  document.getElementById(data.id+"_minimize");
};

ZaBaseDialog.prototype.initForm =
function (xModelMetaData, xFormMetaData,entry) {
	if(xModelMetaData == null || xFormMetaData == null)
		throw new AjxException(ZaMsg.ERROR_METADATA_NOT_DEFINED, AjxException.INVALID_PARAM, "ZaXWizardDialog.prototype.initForm");
	// Hook here. Replace the _OUTPUT_ items
    var newXFormMetaData = {items:[]};
    var stepChoices = xFormMetaData.items[0];
    stepChoices.type = _STEPCHOICE_;
    stepChoices.colSpan = 1;
    var content = xFormMetaData.items[3];
    newXFormMetaData.numCols = 2;
    newXFormMetaData.colSizes = ["100px", "*"];
    newXFormMetaData.items =[
                stepChoices,
                content
            ];
	this._localXModel = new XModel(xModelMetaData);
	this._localXForm = new XForm(newXFormMetaData, this._localXModel, entry, this, ZaId.getDialogViewId(this._contextId));
	this._localXForm.setController(ZaApp.getInstance());
	this._localXForm.draw(this._pageDiv);
	this._drawn = true;
}

ZaBaseDialog.prototype.addMiniIcon =
function () {
    if (this._minEl) {
        this._minEl.innerHTML = AjxImg.getImageHtml("Help");
	this._minEl.onclick = AjxCallback.simpleClosure(ZaBaseDialog.__handleMinClick, this);
    }
}

ZaBaseDialog.prototype.getTask =
function () {
    if (!this._task) {
        this._task = new ZaWorkingProcess(this.constructor, this.toString(), this.getObject());
    } else {
        this._task.data = this.getObject();
    }
    return this._task;
}

ZaBaseDialog.prototype.popdownHookListner = function() {
    if (!this._inMin) {
        ZaZimbraAdmin.getInstance().getTaskController().removeTask(this.getTask());
    }
}

ZaBaseDialog.__handleMinClick = 
function () {
    var task = this.getTask();
    ZaZimbraAdmin.getInstance().getTaskController().addTask(task);
    this._inMin = true;
    this.popdown();
    this._inMin = false;
}
	

