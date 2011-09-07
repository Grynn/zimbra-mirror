/**
 * Created by IntelliJ IDEA.
 * User: mingzhang
 * Date: 9/5/11
 * Time: 1:42 AM
 * To change this template use File | Settings | File Templates.
 */

ZaTaskContentView = function(parent, entry) {
    ZaTabView.call(this, {
		parent:parent,
		iKeyName:"ZaTaskContentView",
		contextId:"TabContent"
	});
    this.initForm(ZaTask.myXModel,this.getMyXForm(entry), null);
}

ZaTaskContentView.prototype = new ZaTabView();
ZaTaskContentView.prototype.constructor = ZaTaskContentView;
ZaTabView.XFormModifiers["ZaTaskContentView"] = new Array();

ZaTaskContentView._dialogCache = new Array();
ZaTaskContentView._getDialog =
function(type, myConstructor) {
    if (!ZaTaskContentView._dialogCache[type]) {
        ZaTaskContentView._dialogCache[type] = new myConstructor(ZaApp.getInstance().getAppCtxt().getShell());
    }
    return ZaTaskContentView._dialogCache[type];
}
ZaTaskContentView.prototype.setObject =
function(entry) {

    this._containedObject = entry;

    this._localXForm.setInstance(this._containedObject);

	this.formDirtyLsnr = new AjxListener(ZaApp.getInstance().getCurrentController(), ZaXFormViewController.prototype.handleXFormChange);
	this._localXForm.addListener(DwtEvent.XFORMS_FORM_DIRTY_CHANGE, this.formDirtyLsnr);
	this._localXForm.addListener(DwtEvent.XFORMS_VALUE_ERROR, this.formDirtyLsnr);
}

ZaTaskContentView.workingInProcessSelectionListener =
function (ev) {
	var arr = this.widget.getSelection();
	if(arr && arr.length) {
		var selectedItem = arr[0];
        var dialog = ZaTaskContentView._getDialog(selectedItem.type, selectedItem.constructor);
        dialog.setObject(selectedItem.data);
        dialog.popup();
	}
}

ZaTaskContentView.myXFormModifier = function(xFormObject, entry) {
    var workingInProcessHeader = new Array();
    workingInProcessHeader[0] = new ZaListHeaderItem(ZaTask.A_workingInProcess, ZaMsg.MSG_WorkingTask, null, "auto", null, ZaTask.A_workingInProcess, false, true);

    var runningTaskHeader = new Array();
    runningTaskHeader[0] = new ZaListHeaderItem(ZaTask.A_runningTask, ZaMsg.MSG_RunningTask, null, "auto", null, ZaTask.A_runningTask, false, true);
    var items = {
        type:_GROUP_, numCols:1,  items:[
            {ref:ZaTask.A_workingInProcess, type:_DWT_LIST_, height:100,
               forceUpdate: true, preserveSelection:false, multiselect:false,
               headerList:workingInProcessHeader,
               visibilityChecks:[[XForm.checkInstanceValue, ZaTask.A2_isExpanded, true]],
               visibilityChangeEventSources:[ZaTask.A2_isExpanded],
               onSelection:ZaTaskContentView.workingInProcessSelectionListener
            },
            {ref:ZaTask.A_runningTask, type:_DWT_LIST_, height:100,
               forceUpdate: true, preserveSelection:false, multiselect:false,
               headerList:runningTaskHeader,
               visibilityChecks:[[XForm.checkInstanceValue, ZaTask.A2_isExpanded, true]],
               visibilityChangeEventSources:[ZaTask.A2_isExpanded]
            },
            {type:_SPACER_, height:"100%"}
        ]
    }
    xFormObject.tableCssStyle="width:100%;";
    xFormObject.items = [items];
}

ZaTabView.XFormModifiers["ZaTaskContentView"].push(ZaTaskContentView.myXFormModifier);