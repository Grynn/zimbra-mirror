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
        cssClassName:"ZaTaskTabView DwtTabView",
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
function(selectedItem) {
    var cacheName = selectedItem.cacheName;
    var myConstructor = selectedItem.constructor;
    var entry = selectedItem.data;
    if(selectedItem.cacheDialog && !ZaTaskContentView._dialogCache[cacheName])
            ZaTaskContentView._dialogCache[cacheName] = ZaApp.getInstance().dialogs[cacheName];

    if(!selectedItem.cacheDialog ||!ZaTaskContentView._dialogCache[cacheName]){
          ZaTaskContentView._dialogCache[cacheName] = new myConstructor(ZaApp.getInstance().getAppCtxt().getShell(), entry);
          if (selectedItem.finishCallback) {
               if(selectedItem.dialogType == 2) {
                    selectedItem.finishCallback.callback.args = {
                    currentObject:selectedItem.editData,
                    currentWizard:ZaTaskContentView._dialogCache[cacheName]
                    }
               }
               ZaTaskContentView._dialogCache[cacheName].registerCallback(selectedItem.finishCallback.id, selectedItem.finishCallback.callback);
          }
    }

    return ZaTaskContentView._dialogCache[cacheName];
}

ZaTaskContentView.prototype.setObject =
function(entry) {

    this._containedObject = entry;

    this._localXForm.setInstance(this._containedObject);

	this.formDirtyLsnr = new AjxListener(ZaApp.getInstance().getCurrentController(), ZaXFormViewController.prototype.handleXFormChange);
	this._localXForm.addListener(DwtEvent.XFORMS_FORM_DIRTY_CHANGE, this.formDirtyLsnr);
	this._localXForm.addListener(DwtEvent.XFORMS_VALUE_ERROR, this.formDirtyLsnr);
}

ZaTaskContentView.taskItemSelectionListener =
function (ev) {
	var arr = this.widget.getSelection();
	if(arr && arr.length) {
		var selectedItem = arr[0];
        var dialog = ZaTaskContentView._getDialog(selectedItem);
        dialog.setObject(selectedItem.data);
        dialog.popup();
        var position = selectedItem.position;
        dialog.setBounds(position.x, position.y, position.width, position.height);
        if (dialog.handleXFormChange) {
            dialog.handleXFormChange();
        }
	}
}

ZaTaskContentView.myXFormModifier = function(xFormObject, entry) {
    var cases = [];
    var workingInProcessHeader = new Array();
    workingInProcessHeader[0] = new ZaListHeaderItem(ZaTask.A_workingInProcess, ZaMsg.MSG_WorkingTask, null, "auto", null, ZaTask.A_workingInProcess, false, true);

    var runningTaskHeader = new Array();
    runningTaskHeader[0] = new ZaListHeaderItem(ZaTask.A_runningTask, ZaMsg.MSG_RunningTask, null, "auto", null, ZaTask.A_runningTask, false, true);

    var serverStatusHeader = new Array();
    serverStatusHeader[0] = new ZaListHeaderItem(ZaTask.A_serverStatus, ZaMsg.MSG_ServerStatus, null, "auto", null, ZaTask.A_serverStatus, false, true);

    var case1 = {type:_ZATABCASE_, numCols: 1, caseKey:1,
                paddingStyle: "", width: "100%", cellpadding: 0,
                getCustomWidth: ZaTaskContentView.prototype.getCustomWidth,
                getCustomHeight: ZaTaskContentView.prototype.getCustomHeight,
                items:[
                    {type:_AJX_IMAGE_, src: "WorkInProgress", label:null, containerCssStyle:"text-align:center;",
                        visibilityChecks:[[XForm.checkInstanceValue, ZaTask.A2_isExpanded, false]],
                        visibilityChangeEventSources:[ZaTask.A2_isExpanded]},
                    {ref:ZaTask.A_workingInProcess, type:_OUTPUT_,  bmolsnr: true, value:0,
                        visibilityChecks:[[XForm.checkInstanceValue, ZaTask.A2_isExpanded, false]],
                        visibilityChangeEventSources:[ZaTask.A2_isExpanded], containerCssStyle:"text-align:center;",
                        getDisplayValue: function(newValue) {
                           return newValue.length;
                        }
                    },
                    {ref:ZaTask.A_workingInProcess, type:_DWT_LIST_, height:160,
                       forceUpdate: true, preserveSelection:false, multiselect:false,
                       headerList:workingInProcessHeader,
                       visibilityChecks:[[XForm.checkInstanceValue, ZaTask.A2_isExpanded, true]],
                       visibilityChangeEventSources:[ZaTask.A2_isExpanded],
                       onSelection:ZaTaskContentView.taskItemSelectionListener
                    },
                    {type:_AJX_IMAGE_, src: "TaskViewWaiting", label:null,containerCssStyle:"text-align:center;",
                        visibilityChecks:[[XForm.checkInstanceValue, ZaTask.A2_isExpanded, false]],
                        visibilityChangeEventSources:[ZaTask.A2_isExpanded]},
                    {ref:ZaTask.A_runningTask, type:_OUTPUT_, bmolsnr: true, value:0,
                        visibilityChecks:[[XForm.checkInstanceValue, ZaTask.A2_isExpanded, false]],
                        visibilityChangeEventSources:[ZaTask.A2_isExpanded], containerCssStyle:"text-align:center;",
                        getDisplayValue: function(newValue) {
                            return newValue.length;
                        }
                    },
                    {ref:ZaTask.A_runningTask, type:_DWT_LIST_, height:160,
                       forceUpdate: true, preserveSelection:false, multiselect:false,
                       headerList:runningTaskHeader,
                       visibilityChecks:[[XForm.checkInstanceValue, ZaTask.A2_isExpanded, true]],
                       visibilityChangeEventSources:[ZaTask.A2_isExpanded],
                       onSelection:ZaTaskContentView.taskItemSelectionListener
                    },
                    {type:_AJX_IMAGE_, src: "Status", label:null, containerCssStyle:"text-align:center;",
                        visibilityChecks:[[XForm.checkInstanceValue, ZaTask.A2_isExpanded, false]],
                        visibilityChangeEventSources:[ZaTask.A2_isExpanded]},
                    {ref:ZaTask.A_serverStatus, type:_OUTPUT_, bmolsnr: true, value:0,
                        visibilityChecks:[[XForm.checkInstanceValue, ZaTask.A2_isExpanded, false]],
                        visibilityChangeEventSources:[ZaTask.A2_isExpanded], containerCssStyle:"text-align:center;",
                        getDisplayValue: function(newValue) {
                            return newValue.length;
                        }
                    },
                    {ref:ZaTask.A_serverStatus, type:_DWT_LIST_, height:160,
                       forceUpdate: true, preserveSelection:false, multiselect:false,
                       headerList:serverStatusHeader,
                       visibilityChecks:[[XForm.checkInstanceValue, ZaTask.A2_isExpanded, true]],
                       visibilityChangeEventSources:[ZaTask.A2_isExpanded]
                    },
                    {type:_SPACER_, height:"100%"}
        ]
    }
    cases.push(case1);

    xFormObject.tableCssStyle="width:100%;";
	xFormObject.items = [
			{type:_SWITCH_, align:_LEFT_, valign:_TOP_, items:cases}
	];
}

ZaTaskContentView.prototype.getCustomWidth = function () {
    return "100%";
}

ZaTaskContentView.prototype.getCustomHeight = function () {
    return "100%";
}

ZaTaskContentView.getImgText = function(imageName, label) {
    var     html = [
                "<div class='", "Img", imageName, "' style='text-align:center'>",label,"</div>"
            ].join("");
    return html;
}
ZaTabView.XFormModifiers["ZaTaskContentView"].push(ZaTaskContentView.myXFormModifier);