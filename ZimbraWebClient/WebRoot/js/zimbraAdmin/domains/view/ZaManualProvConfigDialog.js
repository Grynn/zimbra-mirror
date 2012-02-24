/**
 * Created by IntelliJ IDEA.
 * User: qinan
 * Date: 8/11/11
 * Time: 4:44 PM
 * To change this template use File | Settings | File Templates.
 */


ZaManualProvConfigDialog = function(parent, w, h, title) {
    if (arguments.length == 0) return;
    this._standardButtons = [DwtDialog.OK_BUTTON, DwtDialog.CANCEL_BUTTON];
    ZaXDialog.call(this, parent, null, title, w, h,null, ZaId.DLG_AUTPROV_MANUAL);
    this._containedObject = {};
    this.initForm(ZaDomain.myXModel,this.getMyXForm());
    this._helpURL = ZaManualProvConfigDialog.helpURL;
}

ZaManualProvConfigDialog.prototype = new ZaXDialog;
ZaManualProvConfigDialog.prototype.constructor = ZaManualProvConfigDialog;
ZaManualProvConfigDialog.helpURL = location.pathname + ZaUtil.HELP_URL + "managing_domain/autoprov_manual_config.htm?locid="+AjxEnv.DEFAULT_LOCALE;

ZaManualProvConfigDialog.prototype.setObject = function(entry) {
    entry[ZaDomain.A2_zimbraAutoProvSearchActivated] = "TRUE";
    this._button[DwtDialog.OK_BUTTON].setEnabled(false);
    ZaXDialog.prototype.setObject.call(this,entry);
}

ZaManualProvConfigDialog.prototype.getMyXForm =
function() {
	var xFormObject = {
		numCols:1,
		items:[
          {type:_GROUP_,isTabGroup:true, items: [
                { type: _DWT_ALERT_,
                    containerCssStyle: "padding-bottom:0px",
                    style: DwtAlert.INFO,
                    iconVisible: false,
                    content: ZaMsg.MSG_AUTOPROV_MANUAL,
                    visibilityChecks:[],
                    colSpan:"2"
                },
                {type: _SPACER_, height: 10 },
                {type:_GROUP_, colSpan:2, numCols:3, width:"100%", colSizes:["180px","85px","180px"],cellspacing:"5px",
                    items:[
                        {type:_TEXTFIELD_, cssClass:"admin_xform_name_input",width:"185px", ref:ZaSearch.A_query, label:null,
                            elementChanged: function(elementValue,instanceValue, event) {
                              var charCode = event.charCode;
                              if (charCode == 13 || charCode == 3) {
                                  ZaManualProvConfigDialog.srchButtonHndlr.call(this);
                              } else {
                                  this.getForm().itemChanged(this, elementValue, event);
                              }
                            },
                            visibilityChecks:[],enableDisableChecks:[]
                        },
                        {type:_DWT_BUTTON_, label:ZaMsg.DLXV_ButtonSearch, width:"80px",
                            onActivate:ZaManualProvConfigDialog.srchButtonHndlr,align:_CENTER_,
                            enableDisableChecks:[[XForm.checkInstanceValue,ZaDomain.A2_zimbraAutoProvSearchActivated,"TRUE"]],
                            enableDisableChangeEventSources:[ZaDomain.A2_zimbraAutoProvSearchActivated]
                        },
                        {type:_OUTPUT_, value:ZaMsg.LBL_ManualProvAccount,visibilityChecks:[]},
                        {ref:ZaDomain.A2_zimbraAutoProvAccountPool, type:_DWT_LIST_, height:"200px", width:"180px",
                            cssClass: "DLSource",
                            widgetClass:ZaAccMiniListView,
                            rowSpan:4,
                            onSelection:ZaManualProvConfigDialog.accPoolSelectionListener,
                            visibilityChecks:[],enableDisableChecks:[]
                        },
                        {type:_DWT_BUTTON_, label:AjxMsg.addAll, width:"80px",
                            onActivate:ZaManualProvConfigDialog.addAllButtonHndlr,
                            enableDisableChecks:[[XForm.checkInstanceValueNotEmty,ZaDomain.A2_zimbraAutoProvAccountPool]],
                            enableDisableChangeEventSources:[ZaDomain.A2_zimbraAutoProvAccountPool]
                        },
                        {ref: ZaDomain.A2_zimbraAutoProvAccountTargetPool, type:_DWT_LIST_, height:"200px", width:"180px",
                            cssClass: "DLSource",
                            widgetClass:ZaAccMiniListView,
                            rowSpan:4,
                            onSelection:ZaManualProvConfigDialog.accTargetSelectionListener,
                            visibilityChecks:[],enableDisableChecks:[]
                        },
                        {type:_DWT_BUTTON_, label:AjxMsg.add, width:"80px",
                           onActivate:ZaManualProvConfigDialog.addButtonHndlr,
                           enableDisableChecks:[[XForm.checkInstanceValueNotEmty,ZaDomain.A2_zimbraAutoProvAccountSrcSelectedPool]],
                           enableDisableChangeEventSources:[ZaDomain.A2_zimbraAutoProvAccountSrcSelectedPool]
                        },
                        {type:_DWT_BUTTON_, label:AjxMsg.remove, width:"80px",
                           onActivate:ZaManualProvConfigDialog.removeButtonHndlr,
                           enableDisableChecks:[[XForm.checkInstanceValueNotEmty,ZaDomain.A2_zimbraAutoProvAccountTgtSelectedPool]],
                           enableDisableChangeEventSources:[ZaDomain.A2_zimbraAutoProvAccountTgtSelectedPool]
                        },
                        {type:_DWT_BUTTON_, label:AjxMsg.removeAll, width:"80px",
                            onActivate:ZaManualProvConfigDialog.removeAllButtonHndlr,
                            enableDisableChecks:[[XForm.checkInstanceValueNotEmty,ZaDomain.A2_zimbraAutoProvAccountTargetPool]],
                            enableDisableChangeEventSources:[ZaDomain.A2_zimbraAutoProvAccountTargetPool]
                        },
                        {type:_GROUP_,numCols:3,colSizes:["90px","*","90px"],
                            items:[
                                {type:_SPACER_, colSpan:3},
                                {type:_DWT_BUTTON_, label:ZaMsg.Previous, width:75,
                                   id:"backButton", icon:"LeftArrow", disIcon:"LeftArrowDis",
                                   onActivate:ZaManualProvConfigDialog.backPoolButtonHndlr,align:_CENTER_,
                                   enableDisableChecks:[ZaManualProvConfigDialog.backBtnEnabled],
                                   enableDisableChangeEventSources:[ZaDomain.A2_zimbraAutoProvAccountPoolPageNum,ZaDomain.A2_zimbraAutoProvSearchActivated]
                                },
                                {type:_CELLSPACER_},
                                {type:_DWT_BUTTON_, label:ZaMsg.Next, width:75,
                                   id:"fwdButton", icon:"RightArrow", disIcon:"RightArrowDis",
                                   onActivate:ZaManualProvConfigDialog.fwdPoolButtonHndlr,align:_CENTER_,
                                   enableDisableChecks:[ZaManualProvConfigDialog .forwardBtnEnabled],
                                   enableDisableChangeEventSources:[ZaDomain.A2_zimbraAutoProvAccountPoolPageNum,ZaDomain.A2_zimbraAutoProvSearchActivated]
                                }
                            ]
                        },
                        {type:_CELLSPACER_}
                    ]
                }
            ]
          }
        ]
    };
    return xFormObject;
}

ZaManualProvConfigDialog.srchButtonHndlr = function() {
	var instance = this.getForm().getInstance();
	var formParent = this.getForm().parent;
    var soapDoc = AjxSoapDoc.create("SearchAutoProvDirectoryRequest", ZaZimbraAdmin.URN, null);
    soapDoc.getMethod().setAttribute("keyAttr","name");
	var attr = soapDoc.set("domain", instance.id);
	attr.setAttribute("by", "id");

    var query = "";
	if(instance[ZaSearch.A_query]) {
		query = ZaSearch.getSearchByNameQuery (instance[ZaSearch.A_query]);
	}
    soapDoc.set("query", query);
    var limit = ZaSettings.RESULTSPERPAGE;
	if(!instance[ZaDomain.A2_zimbraAutoProvAccountPoolPageNum]) {
		instance[ZaDomain.A2_zimbraAutoProvAccountPoolPageNum] = 0;
	}
	var offset = instance[ZaDomain.A2_zimbraAutoProvAccountPoolPageNum]*ZaSettings.RESULTSPERPAGE;
	var attrs = [ZaAccount.A_name, ZaAccount.A_mail, ZaItem.A_zimbraId,ZaAccount.A_displayname].join(",");
    soapDoc.getMethod().setAttribute("keyAttr","name");
	soapDoc.getMethod().setAttribute("offset", offset);
	soapDoc.getMethod().setAttribute("limit", limit);
    soapDoc.getMethod().setAttribute("attrs", attrs);
    soapDoc.getMethod().setAttribute("refresh", "1");

	this.getModel().setInstanceValue(this.getInstance(),ZaDomain.A2_zimbraAutoProvSearchActivated,"FALSE");

    var params = {};
    params.soapDoc = soapDoc;
    params.asyncMode = false;
    var reqMgrParams = {
        controller : ZaApp.getInstance().getCurrentController(),
        busyMsg : ZaMsg.BUSY_AUTOPROV_GETACCT
    }

    try {
        var resp = ZaRequestMgr.invoke(params, reqMgrParams);
        this.getModel().setInstanceValue(this.getInstance(),ZaDomain.A2_zimbraAutoProvSearchActivated,"TRUE");
        if(!resp || resp.Body.SearchAutoProvDirectoryResponse.Fault)
            return;
        if(!resp.Body.SearchAutoProvDirectoryResponse || !resp.Body.SearchAutoProvDirectoryResponse.entry)
            return;
        var provAcctList = [];
        var objs = resp.Body.SearchAutoProvDirectoryResponse.entry;
        var searchTotal = resp.Body.SearchAutoProvDirectoryResponse.searchTotal;
        for(var i = 0; objs && i < objs.length; i++) {
            var obj = objs[i];
            var acct = new Object();
            acct.dn = obj.dn;
            var len = obj.a.length;
            acct.attrs = new Array();
            for(var ix = 0; ix < len; ix ++) {
                if(!acct.attrs[[obj.a[ix].n]]) {
                    acct.attrs[[obj.a[ix].n]] = obj.a[ix]._content;
                } else {
                    if(!(acct.attrs[[obj.a[ix].n]] instanceof Array)) {
                        acct.attrs[[obj.a[ix].n]] = [acct.attrs[[obj.a[ix].n]]];
                    }
                    acct.attrs[[obj.a[ix].n]].push(obj.a[ix]._content);
                }
            }
            acct.name = acct.attrs[ZaAccount.A_mail];
            provAcctList.push(acct);
        }
        this.getModel().setInstanceValue(this.getInstance(),ZaDomain.A2_zimbraAutoProvAccountPool,provAcctList);
        var poolTotalPages = Math.ceil(searchTotal/ZaSettings.RESULTSPERPAGE);
        this.getModel().setInstanceValue(this.getInstance(),ZaDomain.A2_zimbraAutoProvAccountPoolPageTotal,poolTotalPages);
    } catch(ex) {
        ZaApp.getInstance().getCurrentController()._handleException(ex, "ZaManualProvConfigDialog.srchButtonHndlr", null, false);
    }
}

ZaManualProvConfigDialog.accPoolSelectionListener = function() {
	var arr = this.widget.getSelection();
	if(arr && arr.length) {
		arr.sort();
		this.getModel().setInstanceValue(this.getInstance(), ZaDomain.A2_zimbraAutoProvAccountSrcSelectedPool, arr);
	} else {
		this.getModel().setInstanceValue(this.getInstance(), ZaDomain.A2_zimbraAutoProvAccountSrcSelectedPool, null);
	}
}

ZaManualProvConfigDialog.addButtonHndlr = function (ev) {
	var form = this.getForm();
	var instance = form.getInstance();
	var sourceListItems = form.getItemsById(ZaDomain.A2_zimbraAutoProvAccountPool);
	if(sourceListItems && (sourceListItems instanceof Array) && sourceListItems[0] && sourceListItems[0].widget) {
		var selection = sourceListItems[0].widget.getSelection();
		var currentTargetList = instance[ZaDomain.A2_zimbraAutoProvAccountTargetPool] ? instance[ZaDomain.A2_zimbraAutoProvAccountTargetPool] : [];
		var list = (selection instanceof AjxVector) ? selection.getArray() : (selection instanceof Array) ? selection : [selection];
		if(list) {
			list.sort(ZaItem.compareNamesDesc);
			var tmpTargetList = [];
			var cnt2 = currentTargetList.length;
			for(var i=0;i<cnt2;i++)
				tmpTargetList.push(currentTargetList[i]);

			tmpTargetList.sort(ZaItem.compareNamesDesc);

			var tmpList = [];
			var cnt = list.length;
			for(var i=cnt-1; i>=0; i--) {
				var dup = false;
				cnt2 = tmpTargetList.length;
				for(var j = cnt2-1; j >=0; j--) {
					if(list[i].name==tmpTargetList[j].name) {
						dup=true;
						tmpTargetList.splice(j,cnt2-j);
						break;
					}
				}
				if(!dup) {
					currentTargetList.push(list[i])
				}
			}
			this.getModel().setInstanceValue(this.getInstance(), ZaDomain.A2_zimbraAutoProvAccountTargetPool, currentTargetList);
		}
	}
	if(currentTargetList.length > 0) {
		form.parent._button[DwtDialog.OK_BUTTON].setEnabled(true);
	} else {
		form.parent._button[DwtDialog.OK_BUTTON].setEnabled(false);
	}
}

ZaManualProvConfigDialog.accTargetSelectionListener = function() {
	var arr = this.widget.getSelection();
	if(arr && arr.length) {
		arr.sort();
		this.getModel().setInstanceValue(this.getInstance(), ZaDomain.A2_zimbraAutoProvAccountTgtSelectedPool, arr);
	} else {
		this.getModel().setInstanceValue(this.getInstance(), ZaDomain.A2_zimbraAutoProvAccountTgtSelectedPool, null);
	}
}

ZaManualProvConfigDialog.addAllButtonHndlr = function (ev) {
	var form = this.getForm();
	var instance = form.getInstance();
	var oldArr = instance[ZaDomain.A2_zimbraAutoProvAccountTargetPool] ? instance[ZaDomain.A2_zimbraAutoProvAccountTargetPool]  : [];
	var arr = instance[ZaDomain.A2_zimbraAutoProvAccountPool];
	var arr2 = new Array();
	if(arr) {
		var cnt = arr.length;
		var oldCnt = oldArr.length;
		for(var ix=0; ix< cnt; ix++) {
			var found = false;
			for(var j = oldCnt-1;j>=0;j-- ) {
				if(oldArr[j].name == arr[ix].name) {
					found = true;
					break;
				}
			}
			if(!found)
				arr2.push(arr[ix]);
		}
	}
	arr2 = arr2.concat(oldArr);
	this.getModel().setInstanceValue(this.getInstance(), ZaDomain.A2_zimbraAutoProvAccountTargetPool, arr2);
	//this.getModel().setInstanceValue(this.getInstance(), ZaDomain.A2_zimbraAutoProvAccountPool, new Array());
	var instance = form.getInstance();
	var currentTargetList = instance[ZaDomain.A2_zimbraAutoProvAccountTargetPool] ? instance[ZaDomain.A2_zimbraAutoProvAccountTargetPool] : [];
	if(currentTargetList.length > 0) {
		form.parent._button[DwtDialog.OK_BUTTON].setEnabled(true);
	} else {
		form.parent._button[DwtDialog.OK_BUTTON].setEnabled(false);
	}
}

ZaManualProvConfigDialog.removeButtonHndlr = function (ev) {
	var form = this.getForm();
	var instance = form.getInstance();
	var targetListItems = form.getItemsById(ZaDomain.A2_zimbraAutoProvAccountTargetPool);
	if(targetListItems && (targetListItems instanceof Array) && targetListItems[0] && targetListItems[0].widget) {
		var selection = targetListItems[0].widget.getSelection();

		var currentTargetList = instance[ZaDomain.A2_zimbraAutoProvAccountTargetPool] ? instance[ZaDomain.A2_zimbraAutoProvAccountTargetPool] : [];
		currentTargetList.sort(ZaItem.compareNamesDesc);
		var tmpTargetList = [];
		var list = (selection instanceof AjxVector) ? selection.getArray() : (selection instanceof Array) ? selection : [selection];
		if(list) {
			list.sort(ZaItem.compareNamesDesc);
			var cnt = list.length;
			var cnt2 = currentTargetList.length;
			for(var i=0;i<cnt2;i++)
				tmpTargetList.push(currentTargetList[i]);

			for(var i=cnt-1; i>=0; i--) {
				var cnt2 = tmpTargetList.length;
				for(var j = cnt2-1; j >=0; j--) {
					if(list[i].name==tmpTargetList[j].name) {
						currentTargetList.splice(j,1);
						tmpTargetList.splice(j,cnt2-j);
					}
				}
			}
			this.getModel().setInstanceValue(this.getInstance(), ZaDomain.A2_zimbraAutoProvAccountTargetPool, currentTargetList);
		}
	}
	if(currentTargetList.length > 0) {
		form.parent._button[DwtDialog.OK_BUTTON].setEnabled(true);
	} else {
		form.parent._button[DwtDialog.OK_BUTTON].setEnabled(false);
	}
}

ZaManualProvConfigDialog.removeAllButtonHndlr = function (ev) {
	var form = this.getForm();
	var instance = form.getInstance();

	instance[ZaDomain.A2_zimbraAutoProvAccountTargetPool] = new Array();
	var currentTargetList = instance[ZaDomain.A2_zimbraAutoProvAccountTargetPool] ? instance[ZaDomain.A2_zimbraAutoProvAccountTargetPool] : [];
	if(currentTargetList.length > 0) {
		form.parent._button[DwtDialog.OK_BUTTON].setEnabled(true);
	} else {
		form.parent._button[DwtDialog.OK_BUTTON].setEnabled(false);
	}
	this.getForm().setInstance(instance);
}

ZaManualProvConfigDialog.backPoolButtonHndlr =
function(evt) {
	var currentPageNum = parseInt(this.getInstanceValue("/poolPagenum"))-1;
	this.setInstanceValue(currentPageNum,"/poolPagenum");
	ZaManualProvConfigDialog.srchButtonHndlr.call(this, evt);
}

ZaManualProvConfigDialog.fwdPoolButtonHndlr =
function(evt) {
	var currentPageNum = parseInt(this.getInstanceValue("/poolPagenum"));
	this.setInstanceValue(currentPageNum+1,"/poolPagenum");
	ZaManualProvConfigDialog.srchButtonHndlr.call(this, evt);
}

ZaManualProvConfigDialog.forwardBtnEnabled = function () {
	return (parseInt(this.getInstanceValue(ZaDomain.A2_zimbraAutoProvAccountPoolPageNum)) < (parseInt(this.getInstanceValue(ZaDomain.A2_zimbraAutoProvAccountPoolPageTotal))-1)
            && this.getInstanceValue(ZaDomain.A2_zimbraAutoProvSearchActivated)=="TRUE");
};

ZaManualProvConfigDialog.backBtnEnabled = function () {
	return (parseInt(this.getInstanceValue(ZaDomain.A2_zimbraAutoProvAccountPoolPageNum)) > 0
            && this.getInstanceValue(ZaDomain.A2_zimbraAutoProvSearchActivated)== "TRUE");
};

ZaManualProvConfigDialog.finishConfig = function () {
	if(!this.parent.handleManualProvDlg)
        return;

    this.parent.handleManualProvDlg.popdown();
    var obj = this.parent.handleManualProvDlg.getObject();
    var instance = this.getInstance();

    var acctlist = this.getModel().getInstanceValue(instance,ZaDomain.A2_zimbraAutoProvAccountTargetPool);
    var soapDoc = AjxSoapDoc.create("BatchRequest", "urn:zimbra");
    soapDoc.setMethodAttribute("onerror", "continue");

    for(var i = 0; i < acctlist.length; i++) {
		var autoProvDoc = soapDoc.set("AutoProvAccountRequest", null, null, ZaZimbraAdmin.URN);

        var attr = soapDoc.set("domain", instance.id, autoProvDoc);
        attr.setAttribute("by", "id");

        attr = soapDoc.set("principal", acctlist[i].dn, autoProvDoc);
        attr.setAttribute("by", "dn");
    }

    var params = new Object();
    params.soapDoc = soapDoc;
    var reqMgrParams ={
        controller:ZaApp.getInstance().getCurrentController(),
        busyMsg : ZaMsg.BUSY_CREATING_GALDS,
        showBusy:true
    }
    ZaRequestMgr.invoke(params, reqMgrParams);
}