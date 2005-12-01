function ZaHSM() {

};

if(ZaGlobalConfig) {
	//constants
	ZaGlobalConfig.A_zimbraComponentAvailable_HSM = "_" + ZaGlobalConfig.A_zimbraComponentAvailable+"_HSM"
	ZaGlobalConfig.A_zimbraHsmAge = "zimbraHsmAge";
	//ZaGlobalConfig model
	if(ZaGlobalConfig.myXModel) {
		ZaGlobalConfig.myXModel.items.push(
		  	{ id:ZaGlobalConfig.A_zimbraHsmAge, ref:"attrs/" + ZaGlobalConfig.A_zimbraHsmAge, type: _STRING_}
		  	);
	}
}
if(ZaServer) {
	//constants
	ZaServer.A_zimbraHsmAge = "zimbraHsmAge";
	ZaServer.A_HSMstartDate = "startDate";
	ZaServer.A_HSMendDate = "endDate";
	ZaServer.A_HSMrunning = "running";
	ZaServer.A_HSMwasAborted = "aborted";
	ZaServer.A_HSMaborting = "aborting";
	ZaServer.A_HSMerror = "error";
	ZaServer.A_HSMnumBlobsMoved = "numBlobsMoved";
	ZaServer.A_HSMnumMailboxes = "numMailboxes";
	ZaServer.A_HSMtotalMailboxes = "totalMailboxes";
	ZaServer.A_HSMthreshold = "threshold";
	ZaServer.A_HSMremainingMailboxes = "remainingMailboxes"
	
	ZaServer.volumeTypeChoicesAll = new XFormChoices({1:ZaMsg.NAD_HSM_PrimaryMsg, 2:ZaMsg.NAD_HSM_SecMsg, 10:ZaMsg.NAD_VOLUME_Index}, XFormChoices.HASH);	
	ZaServer.volumeTypeChoicesHSM = new XFormChoices({1:ZaMsg.NAD_HSM_PrimaryMsg, 2:ZaMsg.NAD_HSM_SecMsg}, XFormChoices.HASH);
	ZaServer.HSM_StatusChoices = {0:ZaMsg.NAD_HSM_Idle,1:ZaMsg.NAD_HSM_Running};	
	
	if(ZaServer.myXModel) {
		ZaServer.myXModel.items.push({id:ZaServer.A_zimbraHsmAge, ref:"attrs/" + ZaServer.A_zimbraHsmAge, type:_COS_MLIFETIME_});
		ZaServer.myXModel.items.push({id:ZaServer.A_HSMstartDate, ref:"hsm/" + ZaServer.A_HSMstartDate});
		ZaServer.myXModel.items.push({id:ZaServer.A_HSMendDate, ref:"hsm/" + ZaServer.A_HSMendDate});
		ZaServer.myXModel.items.push({id:ZaServer.A_HSMrunning, ref:"hsm/" + ZaServer.A_HSMrunning, type:_NUMBER_});
		ZaServer.myXModel.items.push({id:ZaServer.A_HSMwasAborted, ref:"hsm/" + ZaServer.A_HSMwasAborted, type:_NUMBER_});
		ZaServer.myXModel.items.push({id:ZaServer.A_HSMaborting, ref:"hsm/" + ZaServer.A_HSMaborting, type:_ENUM_, choices:[false,true]});
		ZaServer.myXModel.items.push({id:ZaServer.A_HSMerror, ref:"hsm/" + ZaServer.A_HSMerror, type:_STRING_});
		ZaServer.myXModel.items.push({id:ZaServer.A_HSMnumBlobsMoved, ref:"hsm/" + ZaServer.A_HSMnumBlobsMoved, type:_NUMBER_});
		ZaServer.myXModel.items.push({id:ZaServer.A_HSMnumMailboxes, ref:"hsm/" + ZaServer.A_HSMnumMailboxes, type:_NUMBER_});
		ZaServer.myXModel.items.push({id:ZaServer.A_HSMtotalMailboxes, ref:"hsm/" + ZaServer.A_HSMtotalMailboxes, type:_NUMBER_});
		ZaServer.myXModel.items.push({id:ZaServer.A_HSMthreshold, ref:"hsm/" + ZaServer.A_HSMthreshold, type:_NUMBER_});
		ZaServer.myXModel.items.push({id:ZaServer.A_HSMremainingMailboxes, ref:"hsm/" + ZaServer.A_HSMremainingMailboxes, type:_NUMBER_});
		ZaServer.myXModel.items.push({id:"errorMsg", ref:"hsm/errorMsg", type:_STRING_});
		ZaServer.myXModel.items.push({id:"progressMsg", ref:"hsm/progressMsg", type:_STRING_});		
	}
	

	ZaServer.parseHSMStatusResponse = function (arg, respObj) {
		if (!respObj)
			respObj = new ZaReindexMailbox();
	
		if(!arg) {
			respObj.status = null;
			return;
		}
		if(!respObj.hsm) {
			respObj.hsm = new Object();
			respObj.hsm.pollInterval = 500;
		}	
		if(arg instanceof AjxException || arg instanceof ZmCsfeException || arg instanceof AjxSoapException) {
			respObj.hsm.errorMsg = arg.detail;
			respObj.hsm[ZaServer.A_HSMerror] = String(ZaMsg.FAILED_HSM).replace("{0}", arg.code);
			respObj.hsm[ZaServer.A_HSMrunning] = 0;	
		} else {
			var respNode;
			if(!arg) {
				return;
			}
			if(arg instanceof AjxSoapDoc) {
				if(!arg.getBody() || !arg.getBody().firstChild) {
					return;
				}	
				respNode = arg.getBody().firstChild;
			} else {
				if(!arg.firstChild) {
					return;
				}
				respNode = arg.firstChild;
			}
			if(respNode) {
				respObj.hsm.errorMsg = null;		
				respObj.hsm[ZaServer.A_HSMerror] = null;			
				if(respNode.nodeName == "HsmResponse") {
					respObj.hsm[ZaServer.A_HSMrunning] = 1;
					respObj.hsm[ZaServer.A_HSMwasAborted] = 0;
				} else if (respNode.nodeName == "AbortHsmResponse") {
					var tmpInt = parseInt(respNode.getAttribute(ZaServer.A_HSMwasAborted));			
					respObj.hsm[ZaServer.A_HSMwasAborted] = (tmpInt == NaN) ? 0 : tmpInt; 
					respObj.hsm[ZaServer.A_HSMrunning] = 0;	
				} else {
					respObj.hsm[ZaServer.A_HSMstartDate] = respNode.getAttribute(ZaServer.A_HSMstartDate);
					respObj.hsm[ZaServer.A_HSMendDate] = respNode.getAttribute(ZaServer.A_HSMendDate);
		
					var tmpInt = parseInt(respNode.getAttribute(ZaServer.A_HSMrunning));
					respObj.hsm[ZaServer.A_HSMrunning] = (tmpInt == NaN) ? 0 : tmpInt;
		
					tmpInt = parseInt(respNode.getAttribute(ZaServer.A_HSMwasAborted));			
					respObj.hsm[ZaServer.A_HSMwasAborted] = (tmpInt == NaN) ? 0 : tmpInt; 
		
					respObj.hsm[ZaServer.A_HSMaborting] = respNode.getAttribute(ZaServer.A_HSMaborting);			
					respObj.hsm[ZaServer.A_HSMerror] = respNode.getAttribute(ZaServer.A_HSMerror);						
					respObj.hsm[ZaServer.A_HSMnumBlobsMoved] = respNode.getAttribute(ZaServer.A_HSMnumBlobsMoved);
					respObj.hsm[ZaServer.A_HSMnumMailboxes] = respNode.getAttribute(ZaServer.A_HSMnumMailboxes);
					respObj.hsm[ZaServer.A_HSMtotalMailboxes] = respNode.getAttribute(ZaServer.A_HSMtotalMailboxes);
					respObj.hsm[ZaServer.A_HSMthreshold] = respNode.getAttribute(ZaServer.A_HSMthreshold);	
					respObj.hsm[ZaServer.A_HSMremainingMailboxes] = respObj.hsm[ZaServer.A_HSMtotalMailboxes] - respObj.hsm[ZaServer.A_HSMnumMailboxes];
					if(!respObj.hsm[ZaServer.A_HSMrunning]&& !respObj.hsm[ZaServer.A_HSMnumBlobsMoved]
						&& !respObj.hsm[ZaServer.A_HSMtotalMailboxes] && !respObj.hsm[ZaServer.A_HSMremainingMailboxes]) {
						respObj.hsm.progressMsg = String(ZaMsg.HSM_InfoUnavailable);
					} else {
						respObj.hsm.progressMsg = String(ZaMsg.HSM_ProcessStatus).replace("{0}", respObj.hsm[ZaServer.A_HSMnumBlobsMoved]).replace("{1}",respObj.hsm[ZaServer.A_HSMnumMailboxes]).replace("{2}", respObj.hsm[ZaServer.A_HSMremainingMailboxes]);
					}
				}
				if(respObj.hsm[ZaServer.A_HSMrunning] == 0 && respObj.hsm[ZaServer.A_HSMstartDate] && respObj.hsm[ZaServer.A_HSMendDate]) {
					respObj.hsm.progressMsg = String(ZaMsg.HSM_StatusReport).replace("{0}", 
						AjxBuffer.concat(
							AjxDateUtil.simpleComputeDateStr(new Date(parseInt(respObj.hsm[ZaServer.A_HSMstartDate]))),
							"&nbsp;",
							AjxDateUtil.computeTimeString(new Date(parseInt(respObj.hsm[ZaServer.A_HSMstartDate])))
						)
					).replace("{1}",
						AjxBuffer.concat(
							AjxDateUtil.simpleComputeDateStr(new Date(parseInt(respObj.hsm[ZaServer.A_HSMendDate]))),
							"&nbsp;",
							AjxDateUtil.computeTimeString(new Date(parseInt(respObj.hsm[ZaServer.A_HSMendDate])))
						)
					);
					
				}
			}
		}
	}

	ZaServer.getHSMStatus = function (serverid,callback) {
		var soapDoc = AjxSoapDoc.create("GetHsmStatusRequest", "urn:zimbraAdmin", null);
		var resp = null;
		try {
			if(callback) {
				var asynCommand = new ZmCsfeAsynchCommand();
				asynCommand.addInvokeListener(callback);
				asynCommand.invoke(soapDoc, null, null, serverid, true);			
				return asynCommand;
			} else {
				resp = ZmCsfeCommand.invoke(soapDoc, null, null, serverid, true);
			}
		} catch (ex) {
			resp=ex;
		}
		return resp;
	}

	ZaServer.abortHSMSession = function (serverid) {
		var soapDoc = AjxSoapDoc.create("AbortHsmRequest", "urn:zimbraAdmin", null);
		soapDoc.getMethod().setAttribute("action", "cancel");
		
		var resp;
		try {
			resp = ZmCsfeCommand.invoke(soapDoc, null, null, serverid, true);
		} catch (ex) {
			resp = ex;
		}
		return resp;
	}


	ZaServer.startHSMSession = function (serverid) {
		var soapDoc = AjxSoapDoc.create("HsmRequest", "urn:zimbraAdmin", null);
		var resp;
		try {
			resp = ZmCsfeCommand.invoke(soapDoc, null, null, serverid, true);
		} catch (ex) {
			resp = ex;
		}
		return resp;
	}
}

ZaHSM.serverLoad = function (by, val, withConfig) {
	if(this.attrs[ZaServer.A_zimbraMailboxServiceEnabled]) {
		if(this.cos.attrs[ZaGlobalConfig.A_zimbraComponentAvailable_HSM]) {
			var statusResp = ZaServer.getHSMStatus(this.id);
			if(statusResp instanceof AjxException || statusResp instanceof ZmCsfeException || statusResp instanceof AjxSoapException) {
				this._app.getCurrentController()._handleException(statusResp);
				return;
			}
			ZaServer.parseHSMStatusResponse(statusResp,this);
		}
	}
}

if(ZaItem.loadMethods["ZaServer"]) {
	ZaItem.loadMethods["ZaServer"].push(ZaHSM.serverLoad);
}

ZaHSM.serverInit = function (app) {
	this.hsm = new Object();
	this.hsm.pollInterval = 500;
}

if(ZaItem.initMethods["ZaServer"]) {
	ZaItem.initMethods["ZaServer"].push(ZaHSM.serverInit);
}


/**
* @method _hsmButtonListener
**/
ZaHSM._hsmButtonListener = function (ev) {
	try {
		if(!this._hsmWizard) {
			this._hsmWizard = new HSMProgressXDialog(this._container, this._app);	
			this._hsmWizard.registerCallback(DwtDialog.OK_BUTTON, ZaServerController.prototype._hsmOkButtonListener, this);
		}
		this._hsmWizard.setObject(this._currentObject);
		this._hsmWizard.popup();
	} catch (ex) {
		this._handleException(ex, "ZaServerController.prototype._hsmButtonListener", null, false);
	}
}
	
/**
* @method _hsmOkButtonListener
**/
ZaHSM._hsmOkButtonListener = function () {
	var obj = this._hsmWizard.getObject();
	var obj2 = this._view.getObject();
	obj2.hsm = obj.hsm;
	this._view.setObject(obj2);
	this._hsmWizard.popdown();
}

ZaHSM.initToolbar = function () {
	//TODO: Move this code to an external file
	if(this._app.getGlobalConfig().attrs[ZaGlobalConfig.A_zimbraComponentAvailable_HSM]) {
		this._toolbarOperations.push(new ZaOperation(ZaOperation.HSM, ZaMsg.SRVTBB_HSM, ZaMsg.SRVTBB_HSM_tt, "ReadMailbox", "ReadMailboxDis", new AjxListener(this, this._hsmButtonListener)));										
	}	
}

if(ZaController.initToolbarMethods["ZaServerController"]) {
	ZaController.initToolbarMethods["ZaServerController"].push(ZaHSM.initToolbar);
}

ZaHSM.setViewMethod = function (entry) {
	if(entry.cos.attrs[ZaGlobalConfig.A_zimbraComponentAvailable_HSM]) {	
		if(entry[ZaServer.A_showVolumes]) {
			this._toolBar.enable([ZaOperation.HSM], true);
		} else {
			this._toolBar.enable([ZaOperation.HSM], false);
		}
	}
}
if(ZaController.setViewMethods["ZaServerController"]) {
	ZaController.setViewMethods["ZaServerController"].push(ZaHSM.setViewMethod);
}

ZaHSM.GlobalConfigXFormModifier = function (xFormObject) {
	xFormObject.items[1].relevant = "!instance.attrs[ZaGlobalConfig.A_zimbraComponentAvailable_HSM]";
	xFormObject.items.splice(1,0,
	{type:_TAB_BAR_,  ref:ZaModel.currentTab,relevantBehavior:_HIDE_,
	 	containerCssStyle: "padding-top:0px",
	 	relevant:"instance.attrs[ZaGlobalConfig.A_zimbraComponentAvailable_HSM]",
		choices:[
			{value:1, label:ZaMsg.NAD_Tab_General},
			{value:2, label:ZaMsg.NAD_Tab_Attachments},
			{value:3, label:ZaMsg.NAD_Tab_MTA},
			{value:4, label:ZaMsg.NAD_Tab_POP},
			{value:5, label:ZaMsg.NAD_Tab_IMAP},
			{value:6, label:ZaMsg.NAD_Tab_AntiSpam},
			{value:7, label:ZaMsg.NAD_Tab_AntiVirus},
			{value:8, label:ZaMsg.NAD_Tab_HSM}					
		]
	});
	xFormObject.items[3].items.push({type:_CASE_, relevant: "instance[ZaModel.currentTab] == 8 && instance.attrs[ZaGlobalConfig.A_zimbraComponentAvailable_HSM]", 
			items: [
				{ref:ZaGlobalConfig.A_zimbraHsmAge, type:_LIFETIME_, 
					msgName:ZaMsg.NAD_HSM_Threshold,label:ZaMsg.NAD_HSM_Threshold, 
					labelLocation:_LEFT_, 
					onChange:ZaTabView.onFormFieldChanged
				}
			]
		});
}
ZaTabView.XFormModifiers["GlobalConfigXFormView"].push(ZaHSM.GlobalConfigXFormModifier);


ZaHSM.getVolumeTypeDisplayValue = 
function(val) {
	if(!val)
		return "";
		
	var value = parseInt(val);
	switch(value ) {
		case ZaServer.PRI_MSG :
			if(this.getInstance().cos.attrs[ZaGlobalConfig.A_zimbraComponentAvailable_HSM]) {
				return ZaMsg.NAD_HSM_PrimaryMsg;
			} else {
				return ZaMsg.NAD_VOLUME_Msg;
			}
		case ZaServer.SEC_MSG:
			return ZaMsg.NAD_HSM_SecMsg;
		case ZaServer.INDEX:
			return ZaMsg.NAD_VOLUME_Index;
		default :
			return val;
	}
}

ZaHSM.whichVolumeTypeSelect = function() {
	var model = this.getModel();
	var instance = this.getInstance();
	if(instance.cos.attrs[ZaGlobalConfig.A_zimbraComponentAvailable_HSM]) {
		//HSM is installed
		if(model.getInstanceValue(instance, (this.__parentItem.refPath + '/' + ZaServer.A_VolumeId))) {
			//volume exists
			if(this.getInstanceValue() == ZaServer.INDEX) {
				//if its an index volume - don't allow changing its type
		 		return 1;				
			} else if(ZaServerXFormView.isCurrent.call(this)) {
				//if its a current volume - don't allow changing its type
		 		return 1;
			} else {
				//allow changing its type to Primary/Secondary
				return 4;
			}
		} else {
			//allow changing its type to Primary/Secondary/Index
			return 3;
		}
	} else {
		//HSM not installed, volume can be either Primary msg or Index or new
	 	if(model.getInstanceValue(instance, (this.__parentItem.refPath + '/' + ZaServer.A_VolumeId))) {
		 	//volume exists => don't allow changing its type
	 		return 1;
	 	} else {
	 		//allow changing its type to Primary/Index
	 		return 2;
	 	}
	}
}

ZaHSM.ServerXFormModifier = function(xFormObject) {
	var HSMXGroup = {type:_GROUP_, numCols:3,
						relevant:"instance.cos.attrs[ZaGlobalConfig.A_zimbraComponentAvailable_HSM] && instance[ZaServer.A_showVolumes]",
						relevantBehavior:_HIDE_,
						items: [
							{ref:ZaServer.A_zimbraHsmAge, type:_SUPER_LIFETIME_, 
								msgName:ZaMsg.NAD_HSM_Threshold,
								label:ZaMsg.NAD_HSM_Threshold, 
								resetToSuperLabel:ZaMsg.NAD_ResetToGlobal,
								labelLocation:_LEFT_, labelCssStyle:"width:190px;",
								onChange:ZaTabView.onFormFieldChanged
							},
							{type:_SPACER_, colSpan:"*"},	
							{type:_DWT_ALERT_, ref:"progressMsg",content: null,
								relevant:"instance.hsm.progressMsg!=null",
								colSpan:"*",
								relevantBehavior:_HIDE_,
									iconVisible: true,
								align:_CENTER_,				
								style: DwtAlert.INFORMATION
							}									
						]
					};		
	xFormObject.items[2].items[5].items.splice(0,0,HSMXGroup,{type:_SPACER_, colSpan:"*"});
	xFormObject.items[2].items[5].items[4].items[2].relevant="ZaHSM.whichVolumeTypeSelect.call(item)==2";
	xFormObject.items[2].items[5].items[4].items[3].relevant="ZaHSM.whichVolumeTypeSelect.call(item)==1";	
	xFormObject.items[2].items[5].items[4].items[3].getDisplayValue = ZaHSM.getVolumeTypeDisplayValue;
	xFormObject.items[2].items[5].items[4].items.splice(3,0,
								{ref:ZaServer.A_VolumeType, type:_OSELECT1_, choices:ZaServer.volumeTypeChoicesAll,width:"135px", label:null,
									relevant:"ZaHSM.whichVolumeTypeSelect.call(item)==3",									
									relevantBehavior:_HIDE_, onChange: ZaServerXFormView.onFormFieldChanged
								},
								{ref:ZaServer.A_VolumeType, type:_OSELECT1_, choices:ZaServer.volumeTypeChoicesHSM,width:"135px", label:null,
									relevant:"ZaHSM.whichVolumeTypeSelect.call(item)==4",
									relevantBehavior:_HIDE_, onChange: ZaServerXFormView.onFormFieldChanged
								});
}
ZaTabView.XFormModifiers["ZaServerXFormView"].push(ZaHSM.ServerXFormModifier);