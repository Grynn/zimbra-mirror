/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2005, 2006, 2007, 2008, 2009, 2010 Zimbra, Inc.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.3 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 */

/**
* @class ZaServerXFormView creates an view of one Server object
* @contructor
* @param parent {DwtComposite}
* @param app {@link ZaApp}
* @author Greg Solovyev
**/
ZaServerXFormView = function(parent, entry) {
	ZaTabView.call(this, {
		parent:parent, 
		iKeyName:"ZaServerXFormView",
		contextId:ZaId.TAB_SERVER_EDIT
	});	
	this.TAB_INDEX = 0;
	this.initForm(ZaServer.myXModel,this.getMyXForm(entry), null);
	this._localXForm.setController(ZaApp.getInstance());
}

ZaServerXFormView.prototype = new ZaTabView();
ZaServerXFormView.prototype.constructor = ZaServerXFormView;
ZaTabView.XFormModifiers["ZaServerXFormView"] = new Array();
ZaServerXFormView.setObjectMethods = new Array();
ZaServerXFormView.indexVolChoices = new XFormChoices([], XFormChoices.OBJECT_LIST, ZaServer.A_VolumeId, ZaServer.A_VolumeName);
ZaServerXFormView.messageVolChoices = new XFormChoices([], XFormChoices.OBJECT_LIST,ZaServer.A_VolumeId, ZaServer.A_VolumeName);
ZaServerXFormView.onFormFieldChanged = 
function (value, event, form) {
	//DBG.println (AjxDebug.DBG1, "On Form Field Changed ...");
	
	form.parent.setDirty(true);
	this.setInstanceValue(value);
	return value;
}

ZaServerXFormView.onReverseLookupTargetFieldChanged = 
function (value, event, form) {
	DBG.println (AjxDebug.DBG1, "On Form Field Changed ...");
	
	form.parent.setDirty(true);
	this.setInstanceValue(value);
	if(value=="TRUE") {
		this.setInstanceValue("TRUE","/attrs/"+ZaServer.A_ImapCleartextLoginEnabled);
		this.setInstanceValue("TRUE","/attrs/"+ZaServer.A_Pop3CleartextLoginEnabled);
	}
	return value;
}


ZaServerXFormView.prototype.setObject = 
function (entry) {
    this.entry = entry;
	this._containedObject = {attrs:{}};
	this._containedObject[ZaServer.A_showVolumes] = entry[ZaServer.A_showVolumes];
    
    this._containedObject[ZaServer.A_ServiceHostname] = entry[ZaServer.A_ServiceHostname];
	this._containedObject.name = entry.name;
	this._containedObject.type = entry.type ;

	if(entry.rights)
		this._containedObject.rights = entry.rights;
	
	if(entry.setAttrs)
		this._containedObject.setAttrs = entry.setAttrs;
	
	if(entry.getAttrs)
		this._containedObject.getAttrs = entry.getAttrs;
		
	if(entry._defaultValues)
		this._containedObject._defaultValues = entry._defaultValues;
		
	if(entry.id) this._containedObject.id = entry.id;

	this._containedObject[ZaServer.A_Volumes] = [];
	if(entry.attrs) {
        for (var a in entry.attrs) {
            var modelItem = this._localXForm.getModel().getItem(a) ;
            if ((modelItem != null && modelItem.type == _LIST_)
               || (entry.attrs[a] != null && entry.attrs[a] instanceof Array))
            {  //need deep clone
                this._containedObject.attrs [a] =
                        ZaItem.deepCloneListItem (entry.attrs[a]);
            } else {
                this._containedObject.attrs[a] = entry.attrs[a];
            }
        }
	}

	if(ZaItem.modelExtensions["ZaServer"]) {
		for(var i = 0; i< ZaItem.modelExtensions["ZaServer"].length;i++) {
			var ext = ZaItem.modelExtensions["ZaServer"][i];
			if(entry[ext]) {
				this._containedObject[ext] = {};
		        for (var a in entry[ext]) {
		            var modelItem = this._localXForm.getModel().getItem(a) ;
		            if ((modelItem != null && modelItem.type == _LIST_)
		               || (entry[ext][a] != null && entry[ext][a] instanceof Array))
		            {  //need deep clone
		                this._containedObject[ext][a] =
		                        ZaItem.deepCloneListItem (entry[ext][a]);
		            } else {
		                this._containedObject[ext][a] = entry[ext][a];
		            }
		        }
			}
			
		}
	}
	
	if(entry[ZaServer.A_Volumes]) {
		for(var a in entry[ZaServer.A_Volumes]) {
			this._containedObject[ZaServer.A_Volumes][a] = {};
			if(entry[ZaServer.A_Volumes][a]) {
				for(var v in entry[ZaServer.A_Volumes][a]) {
					this._containedObject[ZaServer.A_Volumes][a][v] = entry[ZaServer.A_Volumes][a][v];
				}
			}
		}		
	}
	if(!entry[ZaModel.currentTab])
		this._containedObject[ZaModel.currentTab] = "1";
	else
		this._containedObject[ZaModel.currentTab] = entry[ZaModel.currentTab];

	if(entry[ZaServer.A_showVolumes] && this._containedObject[ZaServer.A_Volumes])	{
		this._containedObject[ZaServer.A_Volumes].sort(ZaServer.compareVolumesByName);		
		this._containedObject[ZaServer.A_Volumes]._version=entry[ZaServer.A_Volumes]._version ? entry[ZaServer.A_Volumes]._version : 1;
		var cnt = this._containedObject[ZaServer.A_Volumes].length;
		var indexArr = [];
		var msgArr = [];
		for(var i=0;i<cnt;i++) {
			if(this._containedObject[ZaServer.A_Volumes][i][ZaServer.A_VolumeType]==ZaServer.INDEX) {
				indexArr.push(this._containedObject[ZaServer.A_Volumes][i]);
			} else if(this._containedObject[ZaServer.A_Volumes][i][ZaServer.A_VolumeType] == ZaServer.MSG) {
				msgArr.push(this._containedObject[ZaServer.A_Volumes][i])
			}
		}
	}
	ZaServerXFormView.indexVolChoices.setChoices(indexArr);
	ZaServerXFormView.indexVolChoices.dirtyChoices();	
	
	ZaServerXFormView.messageVolChoices.setChoices(msgArr);
	ZaServerXFormView.messageVolChoices.dirtyChoices();	
	
	//Instrumentation code start
	if(ZaServerXFormView.setObjectMethods) {
		var methods = ZaServerXFormView.setObjectMethods;
		var cnt = methods.length;
		for(var i = 0; i < cnt; i++) {
			if(typeof(methods[i]) == "function") {
				try {
					methods[i].call(this,entry);
				} catch (ex) {
					this._handleException(ex, "ZaServerXFormView.prototype.setObject");
					break;
				}
			}
		}
	}
	//Instrumentation code end	
	
	for(var key in ZaServer.currentkeys) {
		if(entry[ZaServer.currentkeys[key]]) {
			this._containedObject[ZaServer.currentkeys[key]] = entry[ZaServer.currentkeys[key]];
		}
	}
	this._containedObject.newVolID=-1;			

	this._localXForm.setInstance(this._containedObject);	
	
	this.formDirtyLsnr = new AjxListener(ZaApp.getInstance().getCurrentController(), ZaXFormViewController.prototype.handleXFormChange);
	this._localXForm.addListener(DwtEvent.XFORMS_FORM_DIRTY_CHANGE, this.formDirtyLsnr);
	this._localXForm.addListener(DwtEvent.XFORMS_VALUE_ERROR, this.formDirtyLsnr);	
}


ZaServerXFormView.getTLSEnabled = function () {
	var value = this.getModel().getInstanceValue(this.getInstance(),ZaServer.A_zimbraMtaSaslAuthEnable);
	return value == 'yes';
}

ZaServerXFormView.getIMAPEnabled = function () {
	var value = this.getModel().getInstanceValue(this.getInstance(),ZaServer.A_ImapServerEnabled);
	return value == 'TRUE';
}

ZaServerXFormView.getIMAPSSLEnabled = function () {
	var value = this.getModel().getInstanceValue(this.getInstance(),ZaServer.A_ImapSSLServerEnabled);	
	return (value == 'TRUE' && ZaServerXFormView.getIMAPEnabled.call(this));
}

ZaServerXFormView.getPOP3Enabled = function () {
	var value = this.getModel().getInstanceValue(this.getInstance(),ZaServer.A_Pop3ServerEnabled);
	return value == 'TRUE';
}

ZaServerXFormView.getPOP3SSLEnabled = function () {
	var value = this.getModel().getInstanceValue(this.getInstance(),ZaServer.A_Pop3SSLServerEnabled);
	return (value == 'TRUE' && ZaServerXFormView.getPOP3Enabled.call(this));
}

ZaServerXFormView.getMailboxEnabled = function () {
	var value = this.getModel().getInstanceValue(this.getInstance(),ZaServer.A_showVolumes);
	return value;
}

ZaServerXFormView.getIsReverseProxyLookupTarget = function () {
	return (this.getModel().getInstanceValue(this.getInstance(),ZaServer.A_zimbraReverseProxyLookupTarget) == "TRUE");
}

ZaServerXFormView.volumeSelectionListener = 
function (ev) {
	//var instance = this.getInstance();

	var arr = this.widget.getSelection();	
	if(arr && arr.length) {
		arr.sort(ZaServer.compareVolumesByName);
		this.getModel().setInstanceValue(this.getInstance(), ZaServer.A2_volume_selection_cache, arr);
		//instance.volume_selection_cache = arr;
	} else {
		this.getModel().setInstanceValue(this.getInstance(), ZaServer.A2_volume_selection_cache, null);
		//instance.volume_selection_cache = null;
	}	

	if (ev.detail == DwtListView.ITEM_DBL_CLICKED) {
		ZaServerXFormView.editButtonListener.call(this);
	}	
}

ZaServerXFormView.isEditVolumeEnabled = function () {
	return (!AjxUtil.isEmpty(this.getInstanceValue(ZaServer.A2_volume_selection_cache)) && this.getInstanceValue(ZaServer.A2_volume_selection_cache).length==1);
}

ZaServerXFormView.isDeleteVolumeEnabled = function () {
	if(!AjxUtil.isEmpty(this.getInstanceValue(ZaServer.A2_volume_selection_cache))) {
		var arr = this.getInstanceValue(ZaServer.A2_volume_selection_cache);
		for(var i = 0; i < arr.length;i++) {
			for(a in ZaServer.currentkeys) {
				if(arr[i][ZaServer.A_VolumeId]==this.getInstanceValue(ZaServer.currentkeys[a]))
					return false;			
			}
		}
		return true;
	} else 
		return false;
}

ZaServerXFormView.updateVolume = function () {
	if(this.parent.editVolumeDlg) {
		this.parent.editVolumeDlg.popdown();		
		var obj = this.parent.editVolumeDlg.getObject();
		var soapDoc = AjxSoapDoc.create("ModifyVolumeRequest", ZaZimbraAdmin.URN, null);		
		soapDoc.getMethod().setAttribute(ZaServer.A_VolumeId, obj[ZaServer.A_VolumeId]);	
		var elVolume = soapDoc.set("volume", null);
		elVolume.setAttribute("type", obj[ZaServer.A_VolumeType]);
		elVolume.setAttribute("name", obj[ZaServer.A_VolumeName]);	
		elVolume.setAttribute("rootpath", obj[ZaServer.A_VolumeRootPath]);		
		elVolume.setAttribute("compressBlobs", obj[ZaServer.A_VolumeCompressBlobs]);		
		elVolume.setAttribute("compressionThreshold", obj[ZaServer.A_VolumeCompressionThreshold]);
		var callback = new AjxCallback(this,ZaServerXFormView.prototype.modifyVolumeCallback);
		var params = {
			soapDoc: soapDoc,
			targetServer: this.getInstanceValue(ZaItem.A_zimbraId),
			asyncMode: true,
			callback:callback
		}
		
		var reqMgrParams = {
			controller : ZaApp.getInstance().getCurrentController(),
			busyMsg : ZaMsg.BUSY_CREATE_VOL
		}
		ZaRequestMgr.invoke(params, reqMgrParams) ;		

		/*
		var instance = this.getInstance();
		var volumes = [];
		var cnt = instance[ZaServer.A_Volumes].length;
		for (var i=0; i< cnt; i++) {
			volumes[i] = instance[ZaServer.A_Volumes][i];
		}
		var dirty = false;
		
		if(volumes[obj._index]) {
			if(volumes[obj._index][ZaServer.A_VolumeName] != obj[ZaServer.A_VolumeName]) {
				volumes[obj._index][ZaServer.A_VolumeName] = obj[ZaServer.A_VolumeName];
				dirty=true;
			}
			if(volumes[obj._index][ZaServer.A_VolumeRootPath] != obj[ZaServer.A_VolumeRootPath]) {
				volumes[obj._index][ZaServer.A_VolumeRootPath] = obj[ZaServer.A_VolumeRootPath];
				dirty=true;
				if(volumes[obj._index][ZaServer.A_isCurrent]) {
					ZaApp.getInstance().getCurrentController().popupWarningDialog(ZaMsg.VM_Warning_Changing_CurVolumePath);
				}
			}
			if(volumes[obj._index][ZaServer.A_VolumeCompressBlobs] != obj[ZaServer.A_VolumeCompressBlobs]) {
				volumes[obj._index][ZaServer.A_VolumeCompressBlobs] = obj[ZaServer.A_VolumeCompressBlobs];
				dirty=true;
			}
			if(volumes[obj._index][ZaServer.A_VolumeCompressionThreshold] != obj[ZaServer.A_VolumeCompressionThreshold]) {
				volumes[obj._index][ZaServer.A_VolumeCompressionThreshold] = obj[ZaServer.A_VolumeCompressionThreshold];
				dirty=true;
			}
			if(volumes[obj._index][ZaServer.A_VolumeType] != obj[ZaServer.A_VolumeType]) {
				volumes[obj._index][ZaServer.A_VolumeType] = obj[ZaServer.A_VolumeType];
				dirty=true;
			}					
		}

		if(dirty) {
			var indexArr = [];
			var msgArr = [];
			for(var i=0;i<cnt;i++) {
				if(volumes[i][ZaServer.A_VolumeType]==ZaServer.MSG) {
					msgArr.push(volumes[i])
				} else if(volumes[i][ZaServer.A_VolumeType]==ZaServer.INDEX) {
					indexArr.push(volumes[i]);
				}
			}			
			ZaServerXFormView.indexVolChoices.setChoices(indexArr);
			ZaServerXFormView.indexVolChoices.dirtyChoices();	
			ZaServerXFormView.messageVolChoices.setChoices(msgArr);
			ZaServerXFormView.messageVolChoices.dirtyChoices();	
			volumes._version = instance[ZaServer.A_Volumes]+1;
			this.getModel().setInstanceValue(this.getInstance(), ZaServer.A_Volumes, volumes);
			this.getModel().setInstanceValue(this.getInstance(), ZaServer.A2_volume_selection_cache, new Array());
			this.parent.setDirty(dirty);	
		}	*/	
	}
}

ZaServerXFormView.prototype.modifyVolumeCallback = function(resp) {
	if(resp && resp.isException()) {
		ZaApp.getInstance().getCurrentController()._handleException(resp.getException(), "ZaServerXFormView.createVolumeCallback");
	} else {
		ZaApp.getInstance().getCurrentController().popupMsgDialog(ZaMsg.UPDATE_VOLUME_CONFIRMATION);
		var callback = new AjxCallback(this,ZaServerXFormView.loadVolumesCallback);
		ZaServer.prototype.loadVolumes.call(this.parent.getObject(),callback);
	}
}

ZaServerXFormView.prototype.createVolumeCallback = function(resp) {
	if(resp && resp.isException()) {
		ZaApp.getInstance().getCurrentController()._handleException(resp.getException(), "ZaServerXFormView.createVolumeCallback");
	} else {
		this.addVolumeDlg.popdown();
		var response = resp.getResponse().Body.CreateVolumeResponse;
		var volumes = this._localXForm.getInstanceValue(ZaServer.A_Volumes);
		
		var newVolumes = [];
		for(var i=0;i<volumes.length;i++) {
			newVolumes.push(volumes[i]);
		}
		newVolumes.push(response.volume[0]);
		this._localXForm.setInstanceValue(newVolumes,ZaServer.A_Volumes);
		ZaApp.getInstance().getCurrentController().popupMsgDialog(AjxMessageFormat.format(ZaMsg.VolumeCreated,[response.volume[0][ZaServer.A_VolumeRootPath]]));
	}
}

ZaServerXFormView.prototype.doAddVolume = function(obj) {
	ZaApp.getInstance().dialogs["confirmMessageDialog"].popdown();
	var soapDoc = AjxSoapDoc.create("CreateVolumeRequest", ZaZimbraAdmin.URN, null);		
	var elVolume = soapDoc.set("volume", null);
	elVolume.setAttribute("type", obj[ZaServer.A_VolumeType]);
	elVolume.setAttribute("name", obj[ZaServer.A_VolumeName]);	
	elVolume.setAttribute("rootpath", obj[ZaServer.A_VolumeRootPath]);		
	elVolume.setAttribute("compressBlobs", obj[ZaServer.A_VolumeCompressBlobs]);		
	elVolume.setAttribute("compressionThreshold", obj[ZaServer.A_VolumeCompressionThreshold]);
	var callback = new AjxCallback(this,ZaServerXFormView.prototype.createVolumeCallback);
	var params = {
		soapDoc: soapDoc,
		targetServer: this._containedObject.id,
		asyncMode: true,
		callback:callback
	}
	
	var reqMgrParams = {
		controller : ZaApp.getInstance().getCurrentController(),
		busyMsg : ZaMsg.BUSY_CREATE_VOL
	}
	ZaRequestMgr.invoke(params, reqMgrParams) ;		
}

ZaServerXFormView.addVolume  = function () {
	if(this.parent.addVolumeDlg) {
		var obj = this.parent.addVolumeDlg.getObject();
		ZaApp.getInstance().dialogs["confirmMessageDialog"] = new ZaMsgDialog(ZaApp.getInstance().getAppCtxt().getShell(), null, [DwtDialog.YES_BUTTON, DwtDialog.NO_BUTTON], null, ZaId.VIEW_STATUS + "_confirmMessage");
		ZaApp.getInstance().dialogs["confirmMessageDialog"].setMessage(AjxMessageFormat.format(ZaMsg.Q_CREATE_VOLUME,[obj[ZaServer.A_VolumeRootPath]]),DwtMessageDialog.INFO_STYLE );
		ZaApp.getInstance().dialogs["confirmMessageDialog"].registerCallback(DwtDialog.YES_BUTTON, ZaServerXFormView.prototype.doAddVolume, this.parent, [obj]);
		ZaApp.getInstance().dialogs["confirmMessageDialog"].popup();		
		
		/*var instance = this.getInstance();
		var volArr = [];
		var oldArr = this.getModel().getInstanceValue(this.getInstance(),ZaServer.A_Volumes);
		var cnt = oldArr.length;
		for (var i=0; i< cnt; i++) {
			volArr[i] = oldArr[i];
		}		
		this.getModel().setInstanceValue(this.getInstance(),ZaServer.A2_volume_selection_cache,[]);
		
		volArr.push(obj);
		volArr._version = oldArr._version+1;

		volArr.sort(ZaServer.compareVolumesByName);		
		var cnt = volArr.length;
		var indexArr = [];
		var msgArr = [];
		for(var i=0;i<cnt;i++) {
			if(volArr[i][ZaServer.A_VolumeType]==ZaServer.INDEX) {
				indexArr.push(instance[ZaServer.A_Volumes][i]);
			} else if(volArr[i][ZaServer.A_VolumeType] == ZaServer.MSG) {
				msgArr.push(instance[ZaServer.A_Volumes][i]);
			}
		}
				
		ZaServerXFormView.indexVolChoices.setChoices(indexArr);
		ZaServerXFormView.indexVolChoices.dirtyChoices();	
	
		ZaServerXFormView.messageVolChoices.setChoices(msgArr);
		ZaServerXFormView.messageVolChoices.dirtyChoices();
	
		this.getModel().setInstanceValue(this.getInstance(),ZaServer.A_Volumes,volArr);
		this.parent.setDirty(true);*/
	}
}

ZaServerXFormView.editButtonListener =
function () {
	var instance = this.getInstance();
	if(instance.volume_selection_cache && instance.volume_selection_cache[0]) {	
		var formPage = this.getForm().parent;
		if(!formPage.editVolumeDlg) {
			formPage.editVolumeDlg = new ZaEditVolumeXDialog(ZaApp.getInstance().getAppCtxt().getShell(), "550px", "150px",ZaMsg.VM_Edit_Volume_Title);
			formPage.editVolumeDlg.registerCallback(DwtDialog.OK_BUTTON, ZaServerXFormView.updateVolume, this.getForm(), null);						
		}
		var obj = {};
		obj[ZaServer.A_VolumeId] = instance.volume_selection_cache[0][ZaServer.A_VolumeId];
		obj[ZaServer.A_VolumeName] = instance.volume_selection_cache[0][ZaServer.A_VolumeName];
		obj[ZaServer.A_VolumeRootPath] = instance.volume_selection_cache[0][ZaServer.A_VolumeRootPath];
		obj[ZaServer.A_VolumeCompressBlobs] = instance.volume_selection_cache[0][ZaServer.A_VolumeCompressBlobs];
		obj[ZaServer.A_VolumeCompressionThreshold] = instance.volume_selection_cache[0][ZaServer.A_VolumeCompressionThreshold];
		obj[ZaServer.A_VolumeType] = instance.volume_selection_cache[0][ZaServer.A_VolumeType];		
		
		var volArr = this.getModel().getInstanceValue(this.getInstance(),ZaServer.A_Volumes);
		
		var cnt = volArr.length;
		for(var i=0; i < cnt; i++) {
			if(volArr[i][ZaServer.A_VolumeId]==obj[ZaServer.A_VolumeId] || 
				(!volArr[i][ZaServer.A_VolumeId] && (volArr[i][ZaServer.A_VolumeName] == obj[ZaServer.A_VolumeName])
					&& (volArr[i][ZaServer.A_VolumeRootPath] == obj[ZaServer.A_VolumeRootPath]))) {
				obj._index = i;
				break;
			}
		}
		


		formPage.editVolumeDlg.setObject(obj);
		formPage.editVolumeDlg.popup();		
	}
}

ZaServerXFormView.loadVolumesCallback = function(resp) {
	if(resp && resp.isException()) {
		ZaApp.getInstance().getCurrentController()._handleException(resp.getException(), "ZaServerXFormView.loadVolumesCallback");
	} else {
		var response = resp.getResponse().Body.GetAllVolumesResponse;
		var newVolumes = [];
		var volumes = response.volume;
		if(volumes) {
			var cnt = volumes.length;
			for (var i=0; i< cnt;  i++) {
				newVolumes.push(volumes[i]);	
			}
		}
		this.setInstanceValue(newVolumes,ZaServer.A_Volumes);
	}	
}

ZaServerXFormView.doDeleteVolume = function(selArr, deletedVolumes, respObj) {
	ZaApp.getInstance().dialogs["confirmMessageDialog"].popdown();
	if(respObj != null && respObj.isException && respObj.isException()) {
		ZaApp.getInstance().getCurrentController()._handleException(respObj.getException(), "ZaServerXFormView.doDeleteVolume");
	} else {
		if(AjxUtil.isEmpty(selArr)) {
			if(!AjxUtil.isEmpty(deletedVolumes)) {
				ZaApp.getInstance().getCurrentController().popupMsgDialog(ZaMsg.DELETED_VOLUMES_CONFIRMATION);
				var callback = new AjxCallback(this,ZaServerXFormView.loadVolumesCallback);
				ZaServer.prototype.loadVolumes.call(this.getForm().parent.getObject(),callback);
			}
			return;
		}
		var nextVolume = selArr.shift();
		if(!deletedVolumes) {
			deletedVolumes = [];
		}
		deletedVolumes.push(nextVolume);
		var callback = new AjxCallback(this,ZaServerXFormView.doDeleteVolume,[selArr,deletedVolumes]);
		ZaServer.prototype.deleteVolume.call(this.getForm().parent.getObject(),nextVolume[ZaServer.A_VolumeId],callback);
	}
}

ZaServerXFormView.deleteButtonListener = function () {
	var instance = this.getInstance();
	var volArr = [];
	if(!instance.volume_selection_cache) {
		return;
	}
	var selArr = this.getInstanceValue(ZaServer.A2_volume_selection_cache);
	
	ZaApp.getInstance().dialogs["confirmMessageDialog"] = new ZaMsgDialog(ZaApp.getInstance().getAppCtxt().getShell(), null, [DwtDialog.YES_BUTTON, DwtDialog.NO_BUTTON], null, ZaId.VIEW_STATUS + "_confirmMessage");
	ZaApp.getInstance().dialogs["confirmMessageDialog"].setMessage(ZaMsg.Q_DELETE_VOLUMES,  DwtMessageDialog.WARNING_STYLE);
	ZaApp.getInstance().dialogs["confirmMessageDialog"].registerCallback(DwtDialog.YES_BUTTON, ZaServerXFormView.doDeleteVolume, this, [selArr,[]]);
	ZaApp.getInstance().dialogs["confirmMessageDialog"].popup();		
	
	
	/*var oldArr = this.getInstanceValue(ZaServer.A_Volumes);
	var cnt2 = oldArr.length;
	for (var i=0; i< cnt2; i++) {
		volArr[i] = oldArr[i];
	}		
	
	var removedArr = this.getInstanceValue(ZaServer.A_RemovedVolumes);
	if(AjxUtil.isEmpty(removedArr))
		removedArr = new Array();
		
	var cnt = selArr.length;
	if(cnt && volArr) {
		for(var i=0;i<cnt;i++) {
			cnt2--;				
			for(var k=cnt2;k>=0;k--) {
				if(volArr[k][ZaServer.A_VolumeId]==selArr[i][ZaServer.A_VolumeId]) {
					removedArr.push(volArr[k]);
					volArr.splice(k,1);
					break;	
				}
			}
		}
			
	}
	
	volArr.sort(ZaServer.compareVolumesByName);	
	volArr._version = oldArr._version+1;	
	var cnt3 = volArr.length;
	var indexArr = [];
	var msgArr = [];
	for(var i=0;i<cnt3;i++) {
		if(volArr[i][ZaServer.A_VolumeType]==ZaServer.INDEX) {
			indexArr.push(volArr[i]);
		} else if(volArr[i][ZaServer.A_VolumeType] == ZaServer.MSG) {
			msgArr.push(volArr[i])
		}
	}

	
	ZaServerXFormView.indexVolChoices.setChoices(indexArr);
	ZaServerXFormView.indexVolChoices.dirtyChoices();	

	ZaServerXFormView.messageVolChoices.setChoices(msgArr);
	ZaServerXFormView.messageVolChoices.dirtyChoices();	
	
	this.setInstanceValue(volArr,ZaServer.A_Volumes);
	this.setInstanceValue([],ZaServer.A2_volume_selection_cache);
	this.setInstanceValue(removedArr,ZaServer.A_RemovedVolumes);
	this.getForm().parent.setDirty(true);*/
}

ZaServerXFormView.addButtonListener =
function () {
	var instance = this.getInstance();
	var formPage = this.getForm().parent;
	if(!formPage.addVolumeDlg) {
		formPage.addVolumeDlg = new ZaEditVolumeXDialog(ZaApp.getInstance().getAppCtxt().getShell(), "550px", "150px",ZaMsg.VM_Add_Volume_Title);
		formPage.addVolumeDlg.registerCallback(DwtDialog.OK_BUTTON, ZaServerXFormView.addVolume, this.getForm(), null);						
	}
	
	var obj = {};
	obj[ZaServer.A_VolumeId] = instance.newVolID--;
	obj[ZaServer.A_VolumeName] = "";
	obj[ZaServer.A_VolumeRootPath] = "/opt/zimbra";
	obj[ZaServer.A_VolumeCompressBlobs] = false;
	obj[ZaServer.A_VolumeCompressionThreshold] = 4096;
	obj[ZaServer.A_VolumeType] = ZaServer.MSG;		
	obj.current = false;		
	
	formPage.addVolumeDlg.setObject(obj);
	formPage.addVolumeDlg.popup();		
}

ZaServerXFormView.SERVICE_TAB_ATTRS = [ZaServer.A_zimbraLdapServiceEnabled, ZaServer.A_zimbraMailboxServiceEnabled,ZaServer.A_zimbraMailProxyServiceEnabled,
	ZaServer.A_zimbraMtaServiceEnabled, ZaServer.A_zimbraSnmpServiceEnabled, ZaServer.A_zimbraAntiSpamServiceEnabled,
	ZaServer.A_zimbraAntiVirusServiceEnabled, ZaServer.A_zimbraSpellServiceEnabled, ZaServer.A_zimbraLoggerServiceEnabled];
ZaServerXFormView.SERVICE_TAB_RIGHTS = [];

ZaServerXFormView.MTA_TAB_ATTRS = [ZaServer.A_zimbraMtaSaslAuthEnable, ZaServer.A_zimbraMtaTlsAuthOnly, ZaServer.A_zimbraSmtpHostname,
	ZaServer.A_SmtpPort, ZaServer.A_zimbraMtaRelayHost, ZaServer.A_SmtpTimeout, ZaServer.A_zimbraMtaMyNetworks, ZaServer.A_zimbraMtaDnsLookupsEnabled];
ZaServerXFormView.MTA_TAB_RIGHTS = [];

ZaServerXFormView.AUTH_TAB_ATTRS = [ZaServer.A_zimbraSpnegoAuthPrincipal, ZaServer.A_zimbraSpnegoAuthTargetName];
ZaServerXFormView.AUTH_TAB_RIGHTS = [];

ZaServerXFormView.IMAP_TAB_ATTRS = [ZaServer.A_ImapServerEnabled, ZaServer.A_ImapSSLServerEnabled, ZaServer.A_ImapCleartextLoginEnabled,
	ZaServer.A_zimbraImapNumThreads, ZaServer.A_zimbraImapBindPort, ZaServer.A_ImapSSLBindPort, ZaServer.A_zimbraImapProxyBindPort,
	ZaServer.A_zimbraImapSSLProxyBindPort];
ZaServerXFormView.IMAP_TAB_RIGHTS = [];

ZaServerXFormView.POP_TAB_ATTRS = [ZaServer.A_Pop3ServerEnabled, ZaServer.A_Pop3SSLServerEnabled, ZaServer.A_Pop3CleartextLoginEnabled,
	ZaServer.A_zimbraPop3NumThreads, ZaServer.A_Pop3AdvertisedName, ZaServer.A_Pop3BindAddress, ZaServer.A_zimbraPop3BindPort,
	ZaServer.A_zimbraPop3SSLBindPort,ZaServer.A_zimbraPop3ProxyBindPort,ZaServer.A_zimbraPop3SSLProxyBindPort];
ZaServerXFormView.POP_TAB_RIGHTS = [];

ZaServerXFormView.VOLUMES_TAB_ATTRS = [];
ZaServerXFormView.VOLUMES_TAB_RIGHTS = [ZaServer.MANAGE_VOLUME_RIGHT];

ZaServerXFormView.MTA_NETWORK_GROUP_ATTRS = [ZaServer.A_zimbraImapBindPort,
								ZaServer.A_ImapSSLBindPort,ZaServer.A_zimbraImapProxyBindPort,ZaServer.A_zimbraImapProxyBindPort,
								ZaServer.A_zimbraImapSSLProxyBindPort];

ZaServerXFormView.MTA_SERVICE_GROUP_ATTRS = [ZaServer.A_ImapServerEnabled, ZaServer.A_ImapSSLServerEnabled,
	ZaServer.A_ImapCleartextLoginEnabled,ZaServer.A_zimbraImapNumThreads];

ZaServerXFormView.BIND_IP_TAB_ATTRS = [ZaServer.A_zimbraMailBindAddress, ZaServer.A_zimbraMailSSLBindAddress,
									ZaServer.A_zimbraMailSSLClientCertBindAddress, ZaServer.A_zimbraAdminBindAddress];
ZaServerXFormView.BIND_IP_TAB_RIGHTS = [];
/**
* This method is added to the map {@link ZaTabView#XFormModifiers}
* @param xFormObject {Object} a definition of the form. This method adds/removes/modifies xFormObject to construct
* a Server view. 
**/
ZaServerXFormView.myXFormModifier = function(xFormObject, entry) {	
	var headerList = new Array();
	headerList[0] = new ZaListHeaderItem(ZaServer.A_VolumeName, ZaMsg.VM_VolumeName, null, "100px", false, null, false, true);
	headerList[1] = new ZaListHeaderItem(ZaServer.A_VolumeRootPath, ZaMsg.VM_VolumeRootPath, null,"200px", false, null, false, true);
	headerList[2] = new ZaListHeaderItem(ZaServer.A_VolumeType, ZaMsg.VM_VolumeType, null, "120px", null, null, false, true);							
	headerList[3] = new ZaListHeaderItem(ZaServer.A_VolumeCompressBlobs, ZaMsg.VM_VolumeCompressBlobs, null, "120px", null, null, false, true);								
	headerList[4] = new ZaListHeaderItem(ZaServer.A_VolumeCompressionThreshold, ZaMsg.VM_VolumeCompressThreshold, null, "120px", null, null, false, true);									
	headerList[5] = new ZaListHeaderItem(ZaServer.A_isCurrentVolume, ZaMsg.VM_CurrentVolume, null, "auto", null, null, false, true);										

	var _tab1, _tab2, _tab3, _tab4, _tab5, _tab6, _tab7, _tab8, _tab9;

    var tabBarChoices = [] ;
    this.helpMap = {};
	_tab1 = ++this.TAB_INDEX;
    tabBarChoices.push ({value:_tab1, label:ZaMsg.TABT_GeneralPage});
    this.helpMap[_tab1] = [location.pathname, ZaUtil.HELP_URL, "managing_servers/managing_servers.htm", "?locid=", AjxEnv.DEFAULT_LOCALE].join("");
    
    if(ZaTabView.isTAB_ENABLED(entry,ZaServerXFormView.SERVICE_TAB_ATTRS, ZaServerXFormView.SERVICE_TAB_RIGHTS)) {
    	_tab2 = ++this.TAB_INDEX;
         tabBarChoices.push ({value:_tab2, label:ZaMsg.NAD_Tab_Services});
         this.helpMap[_tab2] = [location.pathname, ZaUtil.HELP_URL, "managing_servers/managing_servers.htm", "?locid=", AjxEnv.DEFAULT_LOCALE].join("");
    }

    if(ZaTabView.isTAB_ENABLED(entry,ZaServerXFormView.MTA_TAB_ATTRS, ZaServerXFormView.MTA_TAB_RIGHTS)) {
    	_tab3 = ++this.TAB_INDEX;
         tabBarChoices.push ({value:_tab3, label:ZaMsg.NAD_Tab_MTA});
         this.helpMap[_tab3] = [location.pathname, ZaUtil.HELP_URL, "managing_servers/configuring_the_mta_tab_for_servers.htm", "?locid=", AjxEnv.DEFAULT_LOCALE].join("");
    }

    /* bug 71233, bug 71234, remove SPNEGO & 2-way SSL
    if(ZaTabView.isTAB_ENABLED(entry,ZaServerXFormView.AUTH_TAB_ATTRS, ZaServerXFormView.AUTH_TAB_RIGHTS)) {
    	_tab4 = ++this.TAB_INDEX;
         tabBarChoices.push ({value:_tab4, label:ZaMsg.NAD_Tab_AUTH});
    } */

    if(ZaTabView.isTAB_ENABLED(entry,ZaServerXFormView.IMAP_TAB_ATTRS, ZaServerXFormView.IMAP_TAB_RIGHTS)) {
        _tab5 = ++this.TAB_INDEX;
        tabBarChoices.push ({value:_tab5, label:ZaMsg.NAD_Tab_IMAP});
        this.helpMap[_tab5] = [location.pathname, ZaUtil.HELP_URL, "managing_servers/configuring_imap_settings.htm", "?locid=", AjxEnv.DEFAULT_LOCALE].join("");
    }

    if(ZaTabView.isTAB_ENABLED(entry,ZaServerXFormView.POP_TAB_ATTRS, ZaServerXFormView.POP_TAB_RIGHTS)) {
        _tab6 = ++this.TAB_INDEX;
        tabBarChoices.push ({value:_tab6, label:ZaMsg.NAD_Tab_POP});
        this.helpMap[_tab6] = [location.pathname, ZaUtil.HELP_URL, "managing_server/configuring_pop_Settings.htm", "?locid=", AjxEnv.DEFAULT_LOCALE].join("");
    }    
        

    if(ZaTabView.isTAB_ENABLED(entry,ZaServerXFormView.VOLUMES_TAB_ATTRS, ZaServerXFormView.VOLUMES_TAB_RIGHTS)) {
    	_tab7 = ++this.TAB_INDEX;
        tabBarChoices.push ({value:_tab7, label:ZaMsg.NAD_Tab_VolumeMgt});
        this.helpMap[_tab7] = [location.pathname, ZaUtil.HELP_URL, "managing_servers/volume_management.htm", "?locid=", AjxEnv.DEFAULT_LOCALE].join("");
    }

    if(ZaTabView.isTAB_ENABLED(entry,ZaServerXFormView.BIND_IP_TAB_ATTRS, ZaServerXFormView.BIND_IP_TAB_RIGHTS)) {
        _tab8 = ++this.TAB_INDEX;
        tabBarChoices.push ({value:_tab8, label:ZaMsg.NAD_Tab_Bind_IP});
        this.helpMap[_tab8] = [location.pathname, ZaUtil.HELP_URL, "managing_servers/setting_up_ip_address_binding.htm", "?locid=", AjxEnv.DEFAULT_LOCALE].join("");
    }
    var switchItems = [];

    var case1 =  {
        type:_ZATABCASE_, numCols:1, caseKey:_tab1,
            id:"server_general_tab",
            paddingStyle:"padding-left:15px;", width:"98%", cellpadding:2,
            items:[
                //{type:_ZAGROUP_, cssStyle:"padding-left:0;padding-right:0;", width:"100%", items:[
                {type:_ZA_TOP_GROUPER_, width:"100%", numCols:2,colSizes: ["275px","100%"],
                    label:ZaMsg.TABT_GeneralPage,
                    items:[
                    {ref:ZaServer.A_name, type:_OUTPUT_, label:ZaMsg.NAD_DisplayName, labelLocation:_LEFT_},
                    ZaItem.descriptionXFormItem,
                    { ref: ZaServer.A_ServiceHostname, type:_OUTPUT_,
                      label:ZaMsg.LBL_ServiceHostname, cssClass:"admin_xform_name_input"
                    },
                    { ref: ZaServer.A_LmtpAdvertisedName, type:_INPUT_,
                      label: ZaMsg.NAD_LmtpAdvertisedName, cssClass:"admin_xform_name_input",
                      onChange: ZaServerXFormView.onFormFieldChanged
                    },
                    { ref: ZaServer.A_LmtpBindAddress, type:_INPUT_,
                      label:ZaMsg.NAD_LmtpBindAddress, cssClass:"admin_xform_name_input",
                      onChange:ZaServerXFormView.onFormFieldChanged
                    },
                    { ref: ZaServer.A_zimbraScheduledTaskNumThreads,
                        labelWrap: true,
                        type:_INPUT_,
                        label:ZaMsg.NAD_zimbraScheduledTaskNumThreads,
                        cssClass:"admin_xform_name_input",
                      onChange:ZaServerXFormView.onFormFieldChanged
                    },
                    {ref:ZaServer.A_zimbraMailPurgeSleepInterval, type:_SUPER_LIFETIME_,
                            labelCssStyle:"text-align:left;background-color:#DEE5F1 !important;padding-left:10px;border-right:1px solid;",
                            resetToSuperLabel:ZaMsg.NAD_ResetToGlobal,
                            msgName:ZaMsg.MSG_zimbraMailPurgeSleepInterval,
                            txtBoxLabel:ZaMsg.LBL_zimbraMailPurgeSleepInterval,
			                colSpan: 2,
                            onChange:ZaServerXFormView.onFormFieldChanged
                    },
                    {ref:ZaServer.A_zimbraReverseProxyLookupTarget,
                        type:_SUPER_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToGlobal,
			//bug fix 33189
			//super_lifetime_ has 4 cols, super_checkbox has 3 cols.
			//this table only set two cols in width by _ZA_PLAIN_GROUPER_.
			//It works well in FF or Chrome, each row can extend its cell's width
			//But in IE, the checkbox will be cutoff for only 75%(3/4) of the table's width.
			conSpan: 4,
                        msgName:ZaMsg.NAD_zimbraReverseProxyLookupTarget,
                        checkBoxLabel:ZaMsg.NAD_zimbraReverseProxyLookupTarget,
			colSpan: 2, colSizes: ["275px","275px","*"],
                        trueValue:"TRUE", falseValue:"FALSE", onChange:ZaServerXFormView.onReverseLookupTargetFieldChanged},
                    { ref: ZaServer.A_notes, type:_TEXTAREA_,
                      label: ZaMsg.NAD_Notes, labelCssStyle: "vertical-align:top;", 
		      width: "30em", 
                      onChange:ZaServerXFormView.onFormFieldChanged
                    }
                ]}
            ]
        };
    switchItems.push (case1);

    if(_tab2) {
        var case2 ={type:_ZATABCASE_, colSizes:["auto"],numCols:1, id:"server_services_tab", caseKey:_tab2,
					items:[
						{ type: _ZA_TOP_GROUPER_, label: ZaMsg.NAD_Service_EnabledServices,
						  items: [
						  	{ ref: ZaServer.A_zimbraLdapServiceEnabled, type: _CHECKBOX_,
						  	  enableDisableChangeEventSources:[ZaServer.A_zimbraLdapServiceInstalled],
						  	  enableDisableChecks:[[XForm.checkInstanceValue,ZaServer.A_zimbraLdapServiceInstalled,true],
						  	  [ZaItem.hasWritePermission,ZaServer.A_zimbraServiceEnabled]],
						  	  label: ZaMsg.NAD_Service_LDAP,
					  	      onChange: ZaServerXFormView.onFormFieldChanged
						  	},
						  	{ ref: ZaServer.A_zimbraMailboxServiceEnabled, type: _CHECKBOX_,
						  	  enableDisableChangeEventSources:[ZaServer.A_zimbraMailboxServiceInstalled],
						  	  enableDisableChecks:[[XForm.checkInstanceValue,ZaServer.A_zimbraMailboxServiceInstalled,true],
						  	  [ZaItem.hasWritePermission,ZaServer.A_zimbraServiceEnabled]],
						  	  label: ZaMsg.NAD_Service_Mailbox,
					  	      onChange: ZaServerXFormView.onFormFieldChanged
						  	},
						  	{ ref: ZaServer.A_zimbraMailProxyServiceEnabled, type: _CHECKBOX_,
						  	 // even if proxy is not installed, the server can also be lookup target
						  	 // enableDisableChangeEventSources:[ZaServer.A_zimbraMailProxyServiceInstalled], 
						  	  enableDisableChecks:[/*[XForm.checkInstanceValue,ZaServer.A_zimbraMailProxyServiceInstalled,true], */
						  	  [ZaItem.hasWritePermission,ZaServer.A_zimbraServiceEnabled]],
						  	  label: ZaMsg.NAD_Service_Imapproxy,
					  	      onChange: ZaServerXFormView.onFormFieldChanged
						  	},
						  	{ ref: ZaServer.A_zimbraMtaServiceEnabled, type: _CHECKBOX_,
						  	  enableDisableChangeEventSources:[ZaServer.A_zimbraMtaServiceInstalled],
						  	  enableDisableChecks:[[XForm.checkInstanceValue,ZaServer.A_zimbraMtaServiceInstalled,true],
						  	  [ZaItem.hasWritePermission,ZaServer.A_zimbraServiceEnabled]],
						  	  label: ZaMsg.NAD_Service_MTA,
					  	      onChange: ZaServerXFormView.onMtaServiceChanged
						  	},
						  	{ ref: ZaServer.A_zimbraPolicydServiceEnabled, type: _CHECKBOX_,
							  	  enableDisableChangeEventSources:[ZaServer.A_zimbraPolicydServiceInstalled],
							  	  enableDisableChecks:[[XForm.checkInstanceValue,ZaServer.A_zimbraPolicydServiceInstalled,true],
							  	  [ZaItem.hasWritePermission,ZaServer.A_zimbraServiceEnabled]],
							  	  label: ZaMsg.NAD_Service_Policyd,
						  	      onChange: ZaServerXFormView.onMtaServiceChanged
							 },
						  	{ ref: ZaServer.A_zimbraSnmpServiceEnabled, type: _CHECKBOX_,
						  	  enableDisableChangeEventSources:[ZaServer.A_zimbraSnmpServiceInstalled],
						  	  enableDisableChecks:[[XForm.checkInstanceValue,ZaServer.A_zimbraSnmpServiceInstalled,true],
						  	  [ZaItem.hasWritePermission,ZaServer.A_zimbraServiceEnabled]],
						  	  label: ZaMsg.NAD_Service_SNMP,
					  	      onChange: ZaServerXFormView.onFormFieldChanged
						  	},
						  	{ ref: ZaServer.A_zimbraAntiSpamServiceEnabled, type: _CHECKBOX_,
						  	  enableDisableChangeEventSources:[ZaServer.A_zimbraAntiSpamServiceInstalled],
						  	  enableDisableChecks:[[XForm.checkInstanceValue,ZaServer.A_zimbraAntiSpamServiceInstalled,true],
						  	  [ZaItem.hasWritePermission,ZaServer.A_zimbraServiceEnabled]],
						  	  label: ZaMsg.NAD_Service_AntiSpam,
					  	      onChange: ZaServerXFormView.onFormFieldChanged
						  	},
						  	{ ref: ZaServer.A_zimbraAntiVirusServiceEnabled, type: _CHECKBOX_,
						  	  enableDisableChangeEventSources:[ZaServer.A_zimbraAntiVirusServiceInstalled],
						  	  enableDisableChecks:[[XForm.checkInstanceValue,ZaServer.A_zimbraAntiVirusServiceInstalled,true],
						  	  [ZaItem.hasWritePermission,ZaServer.A_zimbraServiceEnabled]],
						  	  label: ZaMsg.NAD_Service_AntiVirus,
					  	      onChange: ZaServerXFormView.onFormFieldChanged
						  	},
						  	{ ref: ZaServer.A_zimbraSpellServiceEnabled, type: _CHECKBOX_,
						  	  enableDisableChangeEventSources:[ZaServer.A_zimbraSpellServiceInstalled],
						  	  enableDisableChecks:[[XForm.checkInstanceValue,ZaServer.A_zimbraSpellServiceInstalled,true],
						  	  [ZaItem.hasWritePermission,ZaServer.A_zimbraServiceEnabled]],
						  	  label: ZaMsg.NAD_Service_Spell,
					  	      onChange: ZaServerXFormView.onFormFieldChanged
						  	},
						  	{ ref: ZaServer.A_zimbraLoggerServiceEnabled, type: _CHECKBOX_,
						  	  enableDisableChangeEventSources:[ZaServer.A_zimbraLoggerServiceInstalled],
						  	  enableDisableChecks:[[XForm.checkInstanceValue,ZaServer.A_zimbraLoggerServiceInstalled,true],
						  	  [ZaItem.hasWritePermission,ZaServer.A_zimbraServiceEnabled]],
						  	  label: ZaMsg.NAD_Service_Logger,
					  	      onChange: ZaServerXFormView.onFormFieldChanged
						  	},
                            				{ ref: ZaServer.A_zimbraVmwareHAServiceEnabled, type: _CHECKBOX_,
						  	  enableDisableChangeEventSources:[ZaServer.A_zimbraVmwareHAServiceInstalled],
						  	  enableDisableChecks:[[XForm.checkInstanceValue,ZaServer.A_zimbraVmwareHAServiceInstalled,true],
						  	  [ZaItem.hasWritePermission,ZaServer.A_zimbraServiceEnabled]],
						  	  label: ZaMsg.NAD_Service_VmwareHA,
					  	      onChange: ZaServerXFormView.onFormFieldChanged
						  	}
						]}
					]
				};
        switchItems.push(case2) ;
    }

    if(_tab3) {
     var case3 = { type: _ZATABCASE_, id:"server_mta_tab", caseKey:_tab3,
					colSizes:["auto"],numCols:1,
					items: [
						{type:_ZA_TOP_GROUPER_, colSizes:["auto"],numCols:1,label:ZaMsg.Global_MTA_AuthenticationGrp,
					      items: [
						      	{ ref:ZaServer.A_zimbraMtaSaslAuthEnable, type: _SUPER_CHECKBOX_,
						      	  trueValue: "yes", falseValue: "no",
						      	  onChange: ZaServerXFormView.onFormFieldChanged,
						      	  resetToSuperLabel:ZaMsg.NAD_ResetToGlobal,
						      	  checkBoxLabel:ZaMsg.NAD_MTA_Authentication
					      	    },
						      	{ ref:ZaServer.A_zimbraMtaTlsAuthOnly, type: _SUPER_CHECKBOX_,
						      	  enableDisableChangeEventSources:[ZaServer.A_zimbraMtaSaslAuthEnable],
						      	  enableDisableChecks:[ZaServerXFormView.getTLSEnabled],
						      	  trueValue: "TRUE", falseValue: "FALSE",
						      	  onChange: ZaServerXFormView.onFormFieldChanged,
						      	  resetToSuperLabel:ZaMsg.NAD_ResetToGlobal,
						      	  checkBoxLabel:ZaMsg.NAD_MTA_TlsAuthenticationOnly
					      	    }
				      	    ]
						},
				      {type:_ZA_TOP_GROUPER_, colSizes:["275", "100%"], numCols:2,label:ZaMsg.Global_MTA_NetworkGrp,
					      items: [
					      	{type:_SUPER_REPEAT_, ref:ZaServer.A_zimbraSmtpHostname, 
					      		label:ZaMsg.LBL_zimbraSmtpHostname,
					            colSizes:["310px","150px","*"], colSpan:2,
								resetToSuperLabel:ZaMsg.NAD_ResetToGlobal,
								repeatInstance:"", 
								showAddButton:true, 
								showRemoveButton:true, 
								showAddOnNextRow:true,
								addButtonLabel:ZaMsg.Add_zimbraSmtpHostname, 
								removeButtonLabel:ZaMsg.Remove_zimbraSmtpHostname,
								removeButtonCSSStyle: "margin-left:5px;",
								bmolsnr:true,
					      		repeatItems:[
								{ 
								  type:_TEXTFIELD_,ref:".",
								  toolTipContent: ZaMsg.tt_zimbraSmtpHostname,
								  cssClass:"admin_xform_name_input",
								  enableDisableChecks:[],
								  visibilityChecks:[],
								  bmolsnr:true,
								  elementChanged: function(elementValue,instanceValue, event) {
									this.getForm().itemChanged(this, elementValue, event);
									this.getForm().itemChanged(this.getParentItem(), elementValue, event);
								  }
								}]
					      	},

							{ref:ZaServer.A_SmtpPort, type:_OUTPUT_, label:ZaMsg.NAD_MTA_WebMailPort, width:"4em"},

							{
								ref:ZaServer.A_zimbraMtaRelayHost, type:_SUPER_HOSTPORT_,
								label:ZaMsg.NAD_MTA_RelayMTA,
                                colSpan: 1,
							    onClick: "ZaController.showTooltip",
								toolTipContent: ZaMsg.tt_MTA_RelayMTA,resetToSuperLabel:ZaMsg.NAD_ResetToGlobal,
							    bmolsnr:true,
							    elementChanged: function(elementValue,instanceValue, event) {
									this.getForm().itemChanged(this, elementValue, event);
									this.getForm().itemChanged(this.getParentItem(), elementValue, event);
						  		}
				      		},

                            {ref:ZaServer.A_SmtpTimeout, type:_TEXTFIELD_,
                              label:ZaMsg.NAD_MTA_WebMailTimeout, width: "4em",
                              onChange: ZaServerXFormView.onFormFieldChanged
                            },

                           {ref:ZaServer.A_zimbraMtaMyNetworks,
                                txtBoxLabel:ZaMsg.NAD_MTA_MyNetworks,
                                msgName:ZaMsg.NAD_MTA_MyNetworks,
                                type:_SUPER_TEXTAREA_,
                                labelCssClass:"gridGroupBodyLabel",
                                labelCssStyle:"text-align:left;border-right:1px solid;",
                                colSpan: 2,
                                resetToSuperLabel:ZaMsg.NAD_ResetToGlobal,
                                onChange: ZaServerXFormView.onFormFieldChanged,
                                textAreaWidth:"220px"
                            },

					        { ref: ZaServer.A_zimbraMtaDnsLookupsEnabled,
                              type:_SUPER_CHECKBOX_,
                              colSpan: 2,
					      	  checkBoxLabel:ZaMsg.NAD_MTA_DnsLookups,
					      	  trueValue: "TRUE", falseValue: "FALSE",
					      	  onChange: ZaServerXFormView.onFormFieldChanged,
					      	  resetToSuperLabel:ZaMsg.NAD_ResetToGlobal
				      	    }
						]
				      },
				  
				     	{type:_ZA_TOP_GROUPER_, colSizes:["275px","100%"], numCols:2, label:ZaMsg.Global_MTA_MilterServer,
                           items: [
                            { ref:ZaServer.A_zimbraMilterServerEnabled, type: _SUPER_CHECKBOX_,
                              trueValue: "TRUE", falseValue: "FALSE",
                              onChange: ZaServerXFormView.onFormFieldChanged,
                              resetToSuperLabel:ZaMsg.NAD_ResetToGlobal,
                              checkBoxLabel:ZaMsg.NAD_MTA_MilterServerEnabled
                            },
							
							{type:_REPEAT_, ref:ZaServer.A_zimbraMilterBindAddress,
					      		label:ZaMsg.NAD_MTA_MilterBindAddress,
								resetToSuperLabel:ZaMsg.NAD_ResetToGlobal,
								repeatInstance:"", 
								showAddButton:true, 
								showRemoveButton:true, 
								showAddOnNextRow:true,
								addButtonLabel:ZaMsg.NAD_MTA_AddBindAddress , 
								removeButtonLabel:ZaMsg.NAD_MTA_RemoveBindAddress ,
								removeButtonCSSStyle: "margin-left:50px;",
								bmolsnr:true,
					      	    items:[
								{ type:_TEXTFIELD_,ref:".",
								   enableDisableChecks:[],
								   visibilityChecks:[],
								   bmolsnr:true,
								   elementChanged: function(elementValue,instanceValue, event) {
									this.getForm().itemChanged(this, elementValue, event);
									this.getForm().itemChanged(this.getParentItem(), elementValue, event);
								   }
								}
								]
					      		},
						
							{ref:ZaServer.A_zimbraMilterBindPort, type:_OUTPUT_, label:ZaMsg.NAD_MTA_MilterBindPort}
                           ]
                        }
                        /*
                        {type:_ZA_TOP_GROUPER_, colSizes:["275px","*"], numCols:2, label:ZaMsg.NAD_AutoProvision_Setting,
                            items:[
                                {ref:ZaServer.A_zimbraAutoProvPollingInterval, type:_SUPER_LIFETIME_,

                                    txtBoxLabel:ZaMsg.LBL_zimbraAutoProvPollingInterval,
                                    resetToSuperLabel:ZaMsg.NAD_ResetToCOS,colSpan:2,
                                    useParentTable: false,
                                    nowrap:false,labelWrap:true
                                },
                                {type:_REPEAT_, ref:ZaServer.A_zimbraAutoProvScheduledDomains,
                                    label:ZaMsg.LBL_zimbraAutoProvScheduledDomains,
                                    repeatInstance:"",
                                    showAddButton:true,
                                    showRemoveButton:true,
                                    showAddOnNextRow:true,
                                    addButtonLabel:ZaMsg.NAD_Add ,
                                    removeButtonLabel:ZaMsg.NAD_Remove ,
                                    removeButtonCSSStyle: "margin-left:50px;",
                                    bmolsnr:true,
                                    items:[
                                    { type:_TEXTFIELD_,ref:".",
                                       enableDisableChecks:[],
                                       visibilityChecks:[],
                                       bmolsnr:true
                                    }
                                    ]
					      		}
                            ]
                        } */
				    ]
				};
        switchItems.push (case3) ;
    }

    /* bug 71234, bug 71233, remove SPNEGO & 2-way SSL
    if(_tab4) {
     var case4 = { type: _ZATABCASE_, id:"server_auth_tab", caseKey:_tab4,
					colSizes:["auto"],numCols:1,
					items: [
                        {type:_ZA_TOP_GROUPER_, colSizes:["275px","*"], numCols:2, label:ZaMsg.NAD_SPNEGO_Configure,
                            items:[
                              {ref:ZaServer.A_zimbraSpnegoAuthPrincipal, type:_TEXTFIELD_,
                               label:ZaMsg.NAD_MTA_SpnegoAuthPrincipal, width: "20em",
                               onChange: ZaServerXFormView.onFormFieldChanged
                              },
                              {ref:ZaServer.A_zimbraSpnegoAuthTargetName, type:_TEXTFIELD_,
                               label:ZaMsg.NAD_MTA_SpnegoAuthTargetName, width: "20em",
                               onChange: ZaServerXFormView.onFormFieldChanged
                              }
                            ]
                        },
                        {type:_ZA_TOP_GROUPER_, colSizes:["275px","*"], numCols:2, label:ZaMsg.NAD_AUTH_ClientConfigure,
                            items:[
                                {ref:ZaServer.A_zimbraMailSSLClientCertMode, type:_SUPER_SELECT1_,
                                  label:ZaMsg.NAD_zimbraMailSSLClientCertMode,
                                  labelLocation:_LEFT_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS
                                },
                                { ref: ZaServer.A_zimbraMailSSLClientCertPort, type:_TEXTFIELD_,
                                  label: ZaMsg.NAD_zimbraMailSSLClientCertPort,
                                  labelLocation:_LEFT_,
                                  textFieldCssClass:"admin_xform_number_input",
                                  onChange:ZaServerXFormView.onFormFieldChanged
                                },
                                {type: _DWT_ALERT_, cssClass: "DwtTabTable", containerCssStyle: "padding-bottom:0;",
                                  style: DwtAlert.WARNING, iconVisible: false, content: ZaMsg.Alert_Ngnix,
                                  id:"xform_header_ngnix"
                                },
                                {ref:ZaServer.A_zimbraReverseProxyClientCertMode, type:_SUPER_SELECT1_,
                                  label:ZaMsg.NAD_zimbraReverseProxyClientCertMode,
                                  labelLocation:_LEFT_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS
                                },
                                {ref:ZaServer.A_zimbraReverseProxyMailMode, type:_SUPER_SELECT1_,
                                  label:ZaMsg.NAD_zimbraReverseProxyMailMode,
                                  labelLocation:_LEFT_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS
                                },
                                { ref: ZaServer.A_zimbraMailSSLProxyClientCertPort, type:_TEXTFIELD_,
                                  label: ZaMsg.NAD_zimbraMailSSLProxyClientCertPort,
                                  labelLocation:_LEFT_,
                                  textFieldCssClass:"admin_xform_number_input",
                                  onChange:ZaServerXFormView.onFormFieldChanged
                                },
                                {ref: ZaServer.A_zimbraReverseProxyClientCertCA, type:_TEXTAREA_,
                                    label:ZaMsg.NAD_zimbraReverseProxyClientCertCA, width: 370,
                                    onChange:ZaServerXFormView.onFormFieldChanged
                                }
                            ]
                        }
				    ]
				};
        switchItems.push (case4) ;
    } */

	if(_tab5) {
		var case5 = {type:_ZATABCASE_, colSizes:["auto"],numCols:1, caseKey:_tab5,
					id:"server_imap_tab",
					items:[
						{ type: _DWT_ALERT_,
						  containerCssStyle: "padding-bottom:0;",
						  style: DwtAlert.INFO,
						  iconVisible: false,
						  content: ZaMsg.Alert_ServerRestart
						}
					]
     	};
     	if(ZAGroup_XFormItem.isGroupVisible(entry,ZaServerXFormView.MTA_SERVICE_GROUP_ATTRS,[])) {
			case5.items.push({type:_ZA_TOP_GROUPER_, colSizes:["auto"],numCols:1,label:ZaMsg.Global_IMAP_ServiceGrp,
					      items: [
						      	{ ref: ZaServer.A_ImapServerEnabled, type: _SUPER_CHECKBOX_,
						      	  checkBoxLabel:ZaMsg.IMAP_Service,
						      	  trueValue: "TRUE", falseValue: "FALSE",
						      	  onChange: ZaServerXFormView.onFormFieldChanged,
						      	  resetToSuperLabel:ZaMsg.NAD_ResetToGlobal
						  	    },
						  	    {ref: ZaServer.A_ImapSSLServerEnabled, type: _SUPER_CHECKBOX_,
								  checkBoxLabel:ZaMsg.IMAP_SSLService,
							      trueValue: "TRUE", falseValue: "FALSE",
							      onChange: ZaServerXFormView.onFormFieldChanged,
							      resetToSuperLabel:ZaMsg.NAD_ResetToGlobal

						      	},
						  	    { ref: ZaServer.A_ImapCleartextLoginEnabled, type: _SUPER_CHECKBOX_,
						      	  checkBoxLabel:ZaMsg.IMAP_CleartextLoginEnabled,
						      	  //enableDisableChangeEventSources:[ZaServer.A_zimbraReverseProxyLookupTarget],
						      	  //enableDisableChecks:[[XForm.checkInstanceValue,ZaServer.A_zimbraReverseProxyLookupTarget,"FALSE"], ZaItem.hasWritePermission],
						      	  trueValue: "TRUE", falseValue: "FALSE",
						      	  onChange: ZaServerXFormView.onFormFieldChanged,
						      	  resetToSuperLabel:ZaMsg.NAD_ResetToGlobal

					      	    },
					      	    { ref: ZaServer.A_zimbraImapNumThreads, type:_SUPER_TEXTFIELD_,
								  txtBoxLabel: ZaMsg.IMAP_NumThreads, width: "5em",
								  onChange: ZaServerXFormView.onFormFieldChanged,
						      	  resetToSuperLabel:ZaMsg.NAD_ResetToGlobal
								}
						   ]
						});
     	}
     	if(ZAGroup_XFormItem.isGroupVisible(entry,ZaServerXFormView.MTA_NETWORK_GROUP_ATTRS,[])) {
			case5.items.push({type:_ZA_TOP_GROUPER_, label:ZaMsg.Global_IMAP_NetworkGrp,
						      items: [
								{ ref: ZaServer.A_zimbraImapBindPort, type:_TEXTFIELD_,
								  enableDisableChecks:[ZaServerXFormView.getIMAPEnabled,ZaItem.hasReadPermission],
								  enableDisableChangeEventSources:[ZaServer.A_ImapServerEnabled],
								  label: ZaMsg.LBL_IMAP_Port, width: "5em",
								  onChange: ZaServerXFormView.onFormFieldChanged/*,
						      	  resetToSuperLabel:ZaMsg.NAD_ResetToGlobal*/
								},
								{ ref: ZaServer.A_ImapSSLBindPort, type:_TEXTFIELD_,
								  enableDisableChecks:[ZaServerXFormView.getIMAPSSLEnabled,ZaItem.hasReadPermission],
								  enableDisableChangeEventSources:[ZaServer.A_ImapServerEnabled, ZaServer.A_ImapSSLServerEnabled],
								  label: ZaMsg.LBL_IMAP_SSLPort, width: "5em",
								  onChange: ZaServerXFormView.onFormFieldChanged
								}
								]
							});
   		}
   	
   		switchItems.push (case5) ;
   }

   if(_tab6) {
       var case6 = 	{type:_ZATABCASE_, caseKey:_tab6,
					id:"server_pop_tab", colSizes:["auto"],numCols:1,
					items:[
						{ type: _DWT_ALERT_,
						  containerCssStyle: "padding-bottom:0;",
						  style: DwtAlert.INFO,
						  iconVisible: false,
						  content: ZaMsg.Alert_ServerRestart
						},
						{type: _ZA_TOP_GROUPER_, label:ZaMsg.Global_POP_ServiceGrp,
						  items: [
					      	{ ref: ZaServer.A_Pop3ServerEnabled, type: _SUPER_CHECKBOX_,
					      	  trueValue: "TRUE", falseValue: "FALSE",
					      	  onChange: ZaServerXFormView.onFormFieldChanged,
					      	  resetToSuperLabel:ZaMsg.NAD_ResetToGlobal,
					      	  checkBoxLabel:ZaMsg.NAD_POP_Service
				      	    },
				      	    { ref: ZaServer.A_Pop3SSLServerEnabled, type: _SUPER_CHECKBOX_,
					      	  checkBoxLabel:ZaMsg.NAD_POP_SSL,
					      	  trueValue: "TRUE", falseValue: "FALSE",
					      	  onChange: ZaServerXFormView.onFormFieldChanged,
					      	  resetToSuperLabel:ZaMsg.NAD_ResetToGlobal
				      	    },
				      	    { ref: ZaServer.A_Pop3CleartextLoginEnabled, type: _SUPER_CHECKBOX_,
					      	  checkBoxLabel:ZaMsg.NAD_POP_CleartextLoginEnabled,
					      	  //enableDisableChangeEventSources:[ZaServer.A_zimbraReverseProxyLookupTarget],
					      	  //enableDisableChecks:[ZaItem.hasWritePermission,[XForm.checkInstanceValue,ZaServer.A_zimbraReverseProxyLookupTarget,"FALSE"]],
					      	  trueValue: "TRUE", falseValue: "FALSE",
					      	  onChange: ZaServerXFormView.onFormFieldChanged,
					      	  resetToSuperLabel:ZaMsg.NAD_ResetToGlobal
				      	    },
				      	    { ref: ZaServer.A_zimbraPop3NumThreads, type:_SUPER_TEXTFIELD_,
					      	  //enableDisableChangeEventSources:[ZaServer.A_zimbraReverseProxyLookupTarget],
					      	  //enableDisableChecks:[ZaItem.hasWritePermission,[XForm.checkInstanceValue,ZaServer.A_zimbraReverseProxyLookupTarget,"FALSE"]],
							  labelLocation:_LEFT_,
							  textFieldCssClass:"admin_xform_number_input",
							  txtBoxLabel: ZaMsg.NAD_POP_NumThreads,
							  onChange: ZaServerXFormView.onFormFieldChanged,
					      	  resetToSuperLabel:ZaMsg.NAD_ResetToGlobal
							}
						]
						},
						{type:_ZA_TOP_GROUPER_, label:ZaMsg.Global_POP_NetworkGrp,
						  items: [
						  	{type:_GROUP_,numCols:2,colSpan:2,colSizes:["275px","*"],//["275px","275px","150px"],
						      	enableDisableChangeEventSources:[ZaServer.A_Pop3ServerEnabled],
						      	enableDisableChecks:[ZaServerXFormView.getPOP3Enabled],
						  		items:[
									{ ref: ZaServer.A_Pop3AdvertisedName, type:_TEXTFIELD_,
									  labelLocation:_LEFT_, label: ZaMsg.NAD_POP_AdvertisedName,
									  onChange: ZaServerXFormView.onFormFieldChanged,
                                      labelCssClass:"gridGroupBodyLabel",
                                        labelCssStyle:"text-align:left;border-right:1px solid;"
									}
								]
						  	},
							{type:_GROUP_,numCols:2,colSpan:2,colSizes:["275px","*"],
						      	enableDisableChangeEventSources:[ZaServer.A_Pop3ServerEnabled],
						      	enableDisableChecks:[ZaServerXFormView.getPOP3Enabled],
						  		items:[
									{ ref: ZaServer.A_Pop3BindAddress, type:_TEXTFIELD_,
                                      labelCssClass:"gridGroupBodyLabel",
                                      labelCssStyle:"text-align:left;border-right:1px solid;",
									 	label:ZaMsg.NAD_POP_Address,
									  	onChange:ZaServerXFormView.onFormFieldChanged
								  	},
									{type:_OUTPUT_,ref:".",label:"",
                                         labelCssClass:"gridGroupBodyLabel",
                                        labelCssStyle:"text-align:left;border-right:1px solid;",
                                        labelLocation:_LEFT_, value: ZaMsg.NAD_POP_Address_NOTE}
							  ]
						  	},
							{ ref: ZaServer.A_zimbraPop3BindPort, type:_TEXTFIELD_,
						      enableDisableChangeEventSources:[ZaServer.A_Pop3ServerEnabled],
						      enableDisableChecks:[ZaServerXFormView.getPOP3Enabled,ZaItem.hasWritePermission],

							  label: ZaMsg.LBL_POP_Port,
							  labelLocation:_LEFT_,
							  textFieldCssClass:"admin_xform_number_input",
							  onChange:ZaServerXFormView.onFormFieldChanged
						  	},

							{ ref: ZaServer.A_zimbraPop3SSLBindPort, type:_TEXTFIELD_,
							  visibilityChecks:[ZaServerXFormView.getPOP3SSLEnabled,ZaItem.hasReadPermission],
							  visibilityChangeEventSources:[ZaServer.A_Pop3SSLServerEnabled, ZaServer.A_Pop3ServerEnabled],
							  labelLocation:_LEFT_,
							  label: ZaMsg.LBL_POP_SSL_Port,
							  onChange:ZaServerXFormView.onFormFieldChanged
						  	}
				      	]
						}
					]
				};
       switchItems.push(case6);
   }

   if(_tab7) {
       var case7 = 	{type:_ZATABCASE_,width:"100%", id:"server_form_volumes_tab", caseKey:_tab7,
					visibilityChangeEventSources:[ZaModel.currentTab],
					visibilityChecks:[Case_XFormItem.prototype.isCurrentTab,ZaServerXFormView.getMailboxEnabled],

					numCols:1,
					items:[

						{type:_ZA_TOP_GROUPER_, id:"server_form_volumes_group",width:"98%",
							numCols:1,colSizes:["auto"],label:ZaMsg.VM_VolumesGrpTitle,
							cssStyle:"margin:10px;padding-bottom:0;",
							items: [
								{ref:ZaServer.A_Volumes, type:_DWT_LIST_, height:"200", width:"99%",
									 	preserveSelection:false, multiselect:true,cssClass: "DLSource",
									 	headerList:headerList, widgetClass:ZaServerVolumesListView,
									 	onSelection:ZaServerXFormView.volumeSelectionListener,
									 	valueChangeEventSources:[ZaServer.A_Volumes, ZaServer.A_CurrentMsgVolumeId, ZaServer.A_CurrentIndexVolumeId,ZaServer.A_RemovedVolumes]
								},
								{type:_GROUP_, numCols:5, colSizes:["100px","auto","100px","auto","100px"], width:"350px",
									cssStyle:"margin:10px;padding-bottom:0;",
									items: [
										{type:_DWT_BUTTON_, label:ZaMsg.TBB_Delete,width:"100px",
											onActivate:"ZaServerXFormView.deleteButtonListener.call(this);",
						      				enableDisableChangeEventSources:[ZaServer.A2_volume_selection_cache],
						      				enableDisableChecks:[ZaServerXFormView.isDeleteVolumeEnabled]

										},
										{type:_CELLSPACER_},
										{type:_DWT_BUTTON_, label:ZaMsg.TBB_Edit,width:"100px",
											onActivate:"ZaServerXFormView.editButtonListener.call(this);",
						      				enableDisableChangeEventSources:[ZaServer.A2_volume_selection_cache],
						      				enableDisableChecks:[ZaServerXFormView.isEditVolumeEnabled]

										},
										{type:_CELLSPACER_},
										{type:_DWT_BUTTON_, label:ZaMsg.NAD_Add,width:"100px",
											onActivate:"ZaServerXFormView.addButtonListener.call(this);"
										}
									]
								}
							]
						},
						{type:_ZA_TOP_GROUPER_,label:ZaMsg.VM_CurrentVolumesGrpTitle,id:"server_form_current_vol_group", items:[
							{type:_OSELECT1_, editable:false,
								valueChangeEventSources:[ZaServer.A_Volumes, ZaServer.A_RemovedVolumes],
                                enableDisableChecks:[],
                                visibilityChecks:[],
								ref:ZaServer.A_CurrentMsgVolumeId,
								choices:ZaServerXFormView.messageVolChoices,
								label:ZaMsg.LBL_VM_CurrentMessageVolume
							},
							{type:_OSELECT1_, editable:false,
								valueChangeEventSources:[ZaServer.A_Volumes, ZaServer.A_RemovedVolumes],
                                enableDisableChecks:[],
                                visibilityChecks:[],
								ref:ZaServer.A_CurrentIndexVolumeId,
								choices:ZaServerXFormView.indexVolChoices,
								label:ZaMsg.LBL_VM_CurrentIndexVolume
							}
						]}

					]
				};

       switchItems.push (case7) ;

       var case7_2 = 	{type:_ZATABCASE_, caseKey:_tab7,
					visibilityChangeEventSources:[ZaModel.currentTab],
					visibilityChecks:[Case_XFormItem.prototype.isCurrentTab,[XForm.checkInstanceValue,ZaServer.A_showVolumes,false]],

					items: [
						{ type: _DWT_ALERT_,
						  cssClass: "DwtTabTable",
						  containerCssStyle: "padding-bottom:0;",
						  style: DwtAlert.WARNING,
						  iconVisible: true,
						  content:ZaMsg.Alert_MbxSvcNotInstalled,
						  colSpan:"*"
						}
					]

				};
       switchItems.push (case7_2) ;
   }
	if(_tab8) {
		var case8 = {	type: _ZATABCASE_, id:"server_bind_ip_tab", caseKey:_tab8,
						colSizes:["auto"],numCols:1,
						items: [
							{type: _DWT_ALERT_,
									containerCssStyle: "padding-bottom:0;",
									style: DwtAlert.INFO,
									iconVisible: false,
									content: ZaMsg.MSG_ConfigIpAddressBindings
							},
							{type:_ZA_TOP_GROUPER_, colSizes:["275px","*"], numCols:2, label:ZaMsg.NAD_IpAddressBindingsForWebClient,
								items:[
									{ ref: ZaServer.A_zimbraMailBindAddress, type:_TEXTFIELD_,
										containerCssStyle: "padding-top:6px;padding-bottom:6px;",
										labelLocation:_LEFT_,
										label: ZaMsg.NAD_zimbraMailBindAddress,
										onChange: ZaServerXFormView.onFormFieldChanged
									},
									{ ref: ZaServer.A_zimbraMailSSLBindAddress, type:_TEXTFIELD_,
										containerCssStyle: "padding-top:6px;padding-bottom:6px;",
										labelLocation:_LEFT_,
										label: ZaMsg.NAD_zimbraMailSSLBindAddress,
										onChange: ZaServerXFormView.onFormFieldChanged
									},
									{ ref: ZaServer.A_zimbraMailSSLClientCertBindAddress, type:_TEXTFIELD_,
										containerCssStyle: "padding-top:6px;padding-bottom:6px;",
										labelLocation:_LEFT_,
										label: ZaMsg.NAD_zimbraMailSSLClientCertBindAddress,
										onChange: ZaServerXFormView.onFormFieldChanged
									}
								]
							},
							{type:_ZA_TOP_GROUPER_, colSizes:["275px","*"], numCols:2, label:ZaMsg.NAD_IpAddressBindingsForAdminConsole,
								items:[
									{ ref: ZaServer.A_zimbraAdminBindAddress, type:_TEXTFIELD_,
										containerCssStyle: "padding-top:6px;padding-bottom:6px;",
										labelLocation:_LEFT_,
										label: ZaMsg.NAD_zimbraAdminBindAddress,
										onChange: ZaServerXFormView.onFormFieldChanged
									}
								]
							}
						]
					};
			switchItems.push (case8) ;
		}


    xFormObject.tableCssStyle="width:100%;position:static;overflow:auto;";

    this.tabBarChoices = tabBarChoices;
    xFormObject.items = [
		{type:_GROUP_, cssClass:"ZmSelectedHeaderBg", colSpan:"*", id:"xform_header", 
			items: [
				{type:_GROUP_,	numCols:4,colSizes:["32px","*","80px","*"],
					items: [
						{type:_AJX_IMAGE_, src:"Server_32", label:null, rowSpan:3},
						{type:_OUTPUT_, ref:ZaServer.A_name, label:null,cssClass:"AdminTitle",
                            visibilityChecks:[ZaItem.hasReadPermission], height: 32, rowSpan:3},				
						{type:_OUTPUT_, ref:ZaServer.A_ServiceHostname, label:ZaMsg.NAD_ServiceHostname+":"},
						{type:_OUTPUT_, ref:ZaItem.A_zimbraId, label:ZaMsg.NAD_ZimbraID},
						{type:_OUTPUT_, ref:ZaItem.A_zimbraCreateTimestamp, 
							label:ZaMsg.LBL_zimbraCreateTimestamp, labelLocation:_LEFT_,
							getDisplayValue:function() {
								var val = ZaItem.formatServerTime(this.getInstanceValue());
								if(!val)
									return ZaMsg.Server_Time_NA;
								else
									return val;
							},
							visibilityChecks:[ZaItem.hasReadPermission]	
						}						
					]
				}
			]
		},
		{type:_TAB_BAR_, ref:ZaModel.currentTab,
			containerCssStyle: "padding-top:0;",
			choices: tabBarChoices ,
            cssStyle:"display:none;",
			cssClass:"ZaTabBar", id:"xform_tabbar"
		},
		{type:_SWITCH_, items:switchItems }

    ];
};
ZaTabView.XFormModifiers["ZaServerXFormView"].push(ZaServerXFormView.myXFormModifier);

ZaServerXFormView.prototype.getTabChoices = function () {
    return this.tabBarChoices;
}

ZaServerXFormView.showMtaServiceEnableRelatedNotice = function( isMtaEnable ){
	
	var notice;
	var style;
	if( isMtaEnable ){	
		notice = ZaMsg.NAD_MTA_notice_related_statistics_tabs_enable;
		style  = DwtMessageDialog.WARNING_STYLE;
	}
	else {
		notice = ZaMsg.NAD_MTA_notice_related_statistics_tabs_disable;	
		style  = DwtMessageDialog.INFO_STYLE
	}
	
	//ZaApp.getInstance().dialogs["confirmMessageDialog"] = new ZaMsgDialog(ZaApp.getInstance().getAppCtxt().getShell(), null, [DwtDialog.OK_BUTTON]);
		
	ZaApp.getInstance().dialogs["msgDialog"].setMessage( notice, style );
	////ZaApp.getInstance().dialogs["confirmMessageDialog"].registerCallback(DwtDialog.YES_BUTTON, ZaServer.FlushMtaServiceData, this);		
	ZaApp.getInstance().dialogs["msgDialog"].popup();
	
	//var msgDialog = appCtxt.getMsgDialog();
	//msgDialog.setMessage(notice, DwtMessageDialog.INFO_STYLE);
	//msgDialog.popup();
}

ZaServerXFormView.onMtaServiceChanged = function (value, event, form) {
	
	ZaServerXFormView.showMtaServiceEnableRelatedNotice( value );
	
	form.parent.setDirty(true);
	this.setInstanceValue(value);
	return value;
	//return ZaServerXFormView.onFormFieldChanged( value, event, form );
}
