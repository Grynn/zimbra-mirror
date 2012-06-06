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
    var dialog
    if (selectedItem.type == 1) {
        var cacheName = selectedItem.cacheName;
        var myConstructor = selectedItem.viewForPopup;
        var entry = selectedItem.data;
        if(selectedItem.cacheDialog && !ZaTaskContentView._dialogCache[cacheName])
                ZaTaskContentView._dialogCache[cacheName] = ZaApp.getInstance().dialogs[cacheName];

        if(!selectedItem.cacheDialog ||!ZaTaskContentView._dialogCache[cacheName]){
              ZaTaskContentView._dialogCache[cacheName] = ZaApp.getInstance().dialogs[cacheName] = new myConstructor(ZaApp.getInstance().getAppCtxt().getShell(), entry);
              if (selectedItem.finishCallback)
                   ZaTaskContentView._dialogCache[cacheName].registerCallback(selectedItem.finishCallback.id, selectedItem.finishCallback.callback);
        }

        dialog = ZaTaskContentView._dialogCache[cacheName];
        dialog.setObject(selectedItem.data);
    } else if  (selectedItem.type == 2) {
        dialog = selectedItem.viewForPopup;
    } else {
        // shouldn't go here
    }
    return dialog;
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
                        visibilityChangeEventSources:[ZaTask.A2_isExpanded],
                        containerCssStyle:"text-align:center;",
                        getDisplayValue: function(newValue) {
                           return newValue.length;
                        }
                    },

                    {type: _GROUP_, numCols: 1,width: "100%",
                        visibilityChecks:[[XForm.checkInstanceValue, ZaTask.A2_isExpanded, true]],
                        visibilityChangeEventSources:[ZaTask.A2_isExpanded],
                        cssClass: "ZaTaskListGroup",
                        items: [
                            { type:_COMPOSITE_, numCols:2, tableCssStyle:"width:100%",
                               visibilityChecks:[[XForm.checkInstanceValue, ZaTask.A2_isWIPExpanded, true]],
                               visibilityChangeEventSources:[ZaTask.A2_isWIPExpanded],
                                colSizes:["20px", "100%"],
                                items:[
                                    {type:_DWT_IMAGE_, value: "ImgNodeExpanded", cssStyle:"position:static;",
                                        onClick:function() {
                                            this.setInstanceValue(false, ZaTask.A2_isWIPExpanded);
                                        }
                                    },
                                    {type:_OUTPUT_, value: ZaMsg.MSG_WorkingTask}
                                ],
                                cssClass:"ZaTaskTitleNameHeader"
                            },
                            { type:_COMPOSITE_, numCols:2, tableCssStyle:"width:100%",
                               visibilityChecks:[[XForm.checkInstanceValue, ZaTask.A2_isWIPExpanded, false]],
                               visibilityChangeEventSources:[ZaTask.A2_isWIPExpanded],
                                colSizes:["20px", "100%"],
                                items:[
                                    {type:_DWT_IMAGE_, value: "ImgNodeCollapsed", cssStyle:"position:static;",
                                        onClick:function() {
                                            this.setInstanceValue(true, ZaTask.A2_isWIPExpanded);
                                        }
                                    },
                                    {type:_OUTPUT_, value: ZaMsg.MSG_WorkingTask}
                                ],
                                cssClass:"ZaTaskTitleNameHeader"
                            },
                            {ref:ZaTask.A_workingInProcess, type:_DWT_LIST_, cssClass: "ZaTaskListContent",
                               forceUpdate: true, preserveSelection:false, multiselect:false,
                               visibilityChecks:[[XForm.checkInstanceValue, ZaTask.A2_isWIPExpanded, true]],
                               visibilityChangeEventSources:[ZaTask.A2_isWIPExpanded],
                               onSelection:ZaTaskContentView.taskItemSelectionListener
                            }
                        ]
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

                    {type: _GROUP_, numCols: 1,width: "100%",
                        visibilityChecks:[[XForm.checkInstanceValue, ZaTask.A2_isExpanded, true]],
                        visibilityChangeEventSources:[ZaTask.A2_isExpanded],
                        cssClass: "ZaTaskListGroup",
                        items: [
                            { type:_COMPOSITE_, numCols:2, tableCssStyle:"width:100%",
                               visibilityChecks:[[XForm.checkInstanceValue, ZaTask.A2_isRTExpanded, true]],
                               visibilityChangeEventSources:[ZaTask.A2_isRTExpanded],
                                colSizes:["20px", "100%"],
                                items:[
                                    {type:_DWT_IMAGE_, value: "ImgNodeExpanded", cssStyle:"position:static;",
                                        onClick:function() {
                                            this.setInstanceValue(false, ZaTask.A2_isRTExpanded);
                                        }
                                    },
                                    {type:_OUTPUT_, value: ZaMsg.MSG_RunningTask}
                                ],
                                cssClass:"ZaTaskTitleNameHeader"
                            },
                            { type:_COMPOSITE_, numCols:2, tableCssStyle:"width:100%",
                               visibilityChecks:[[XForm.checkInstanceValue, ZaTask.A2_isRTExpanded, false]],
                               visibilityChangeEventSources:[ZaTask.A2_isRTExpanded],
                                colSizes:["20px", "100%"],
                                items:[
                                    {type:_DWT_IMAGE_, value: "ImgNodeCollapsed", cssStyle:"position:static;",
                                        onClick:function() {
                                            this.setInstanceValue(true, ZaTask.A2_isRTExpanded);
                                        }
                                    },
                                    {type:_OUTPUT_, value: ZaMsg.MSG_RunningTask}
                                ],
                                cssClass:"ZaTaskTitleNameHeader"
                            },
                            {ref:ZaTask.A_runningTask, type:_DWT_LIST_, cssClass: "ZaTaskListContent",
                               forceUpdate: true, preserveSelection:false, multiselect:false,
                               visibilityChecks:[[XForm.checkInstanceValue, ZaTask.A2_isRTExpanded, true]],
                               visibilityChangeEventSources:[ZaTask.A2_isRTExpanded],
                               onSelection:ZaTaskContentView.taskItemSelectionListener
                            }
                        ]
                    },

                    {type:_AJX_IMAGE_, src: "Status", label:null, containerCssStyle:"text-align:center;",
                        visibilityChecks:[[XForm.checkInstanceValue, ZaTask.A2_isExpanded, false]],
                        visibilityChangeEventSources:[ZaTask.A2_isExpanded]},
                    {ref:ZaTask.A2_notificationCount, type:_OUTPUT_, bmolsnr: true, value:0,
                        visibilityChecks:[[XForm.checkInstanceValue, ZaTask.A2_isExpanded, false]],
                        visibilityChangeEventSources:[ZaTask.A2_isExpanded], containerCssStyle:"text-align:center;",
                        getDisplayValue: function(newValue) {
                            if (!newValue || newValue < 0) {
                                return 0;
                            }
                            return newValue;
                        }
                    },

                    {type: _GROUP_, numCols: 1,width: "100%", height: "100%",
                        visibilityChecks:[[XForm.checkInstanceValue, ZaTask.A2_isExpanded, true]],
                        visibilityChangeEventSources:[ZaTask.A2_isExpanded],
                        cssClass: "ZaTaskListGroup",
                        items: [
                            { type:_COMPOSITE_, numCols:2, tableCssStyle:"width:100%",
                               visibilityChecks:[[XForm.checkInstanceValue, ZaTask.A2_isServerExpaned, true]],
                               visibilityChangeEventSources:[ZaTask.A2_isServerExpaned],
                                colSizes:["20px", "100%"],
                                items:[
                                    {type:_DWT_IMAGE_, value: "ImgNodeExpanded", cssStyle:"position:static;",
                                        onClick:function() {
                                            this.setInstanceValue(false, ZaTask.A2_isServerExpaned);
                                        }
                                    },
                                    {type:_OUTPUT_, value: ZaMsg.MSG_ServerStatus}
                                ],
                                cssClass:"ZaTaskTitleNameHeader"
                            },
                            { type:_COMPOSITE_, numCols:2, tableCssStyle:"width:100%",
                               visibilityChecks:[[XForm.checkInstanceValue, ZaTask.A2_isServerExpaned, false]],
                               visibilityChangeEventSources:[ZaTask.A2_isServerExpaned],
                                colSizes:["20px", "100%"],
                                items:[
                                    {type:_DWT_IMAGE_, value: "ImgNodeCollapsed", cssStyle:"position:static;",
                                        onClick:function() {
                                            this.setInstanceValue(true, ZaTask.A2_isServerExpaned);
                                        }
                                    },
                                    {type:_OUTPUT_, value: ZaMsg.MSG_ServerStatus}
                                ],
                                cssClass:"ZaTaskTitleNameHeader"
                            },
                            {ref:ZaTask.A_serverStatus, type:_GROUP_, numCols:1, width:"96%",
                                forceUpdate: true, preserveSelection:false, multiselect:false,
                                visibilityChecks:[[XForm.checkInstanceValue, ZaTask.A2_isServerExpaned, true]],
                                visibilityChangeEventSources:[ZaTask.A2_isServerExpaned],
                                onSelection:ZaTaskContentView.taskItemSelectionListener,
                                items:[ //will be appended by others as notification
								]
							}
                        ]
                    },

                    {type:_SPACER_, height:"10px"}
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


ZaTaskContentView.getNotificationBoard = function (taskContentViewXFormObj) {
    return taskContentViewXFormObj.items[0].items[0].items[8].items[2];
}
