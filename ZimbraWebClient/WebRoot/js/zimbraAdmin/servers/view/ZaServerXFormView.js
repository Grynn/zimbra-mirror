/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2005, 2006, 2007 Zimbra, Inc.
 * 
 * The contents of this file are subject to the Yahoo! Public License
 * Version 1.0 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * 
 * ***** END LICENSE BLOCK *****
 */

/**
* @class ZaServerXFormView creates an view of one Server object
* @contructor
* @param parent {DwtComposite}
* @param app {@link ZaApp}
* @author Greg Solovyev
**/
ZaServerXFormView = function(parent, app) {
	ZaTabView.call(this, parent, app,"ZaServerXFormView");	
	this.initForm(ZaServer.myXModel,this.getMyXForm());
	this._localXForm.setController(this._app);
}

ZaServerXFormView.prototype = new ZaTabView();
ZaServerXFormView.prototype.constructor = ZaServerXFormView;
ZaTabView.XFormModifiers["ZaServerXFormView"] = new Array();
ZaServerXFormView.indexVolChoices = new XFormChoices([], XFormChoices.OBJECT_LIST, ZaServer.A_VolumeId, ZaServer.A_VolumeName);
ZaServerXFormView.messageVolChoices = new XFormChoices([], XFormChoices.OBJECT_LIST,ZaServer.A_VolumeId, ZaServer.A_VolumeName);
ZaServerXFormView.onFormFieldChanged = 
function (value, event, form) {
	DBG.println (AjxDebug.DBG1, "On Form Field Changed ...");
	
	form.parent.setDirty(true);
	this.setInstanceValue(value);
	return value;
}

ZaServerXFormView.prototype.setObject = 
function (entry) {
	this.entry = entry;
	this._containedObject = {attrs:{}};
	this._containedObject.cos = entry.cos;
	this._containedObject[ZaServer.A_showVolumes] = entry[ZaServer.A_showVolumes];
	
	
	this._containedObject[ZaServer.A_ServiceHostname] = entry[ZaServer.A_ServiceHostname];
	this._containedObject.name = entry.name;
	this._containedObject.type = entry.type ;
	if(entry.id) this._containedObject.id = entry.id;
	//this._containedObject = AjxUtil.createProxy(entry,3);

	this._containedObject[ZaServer.A_Volumes] = [];
	if(entry.attrs) {
		for(var a in entry.attrs) {
			this._containedObject.attrs[a] = entry.attrs[a];
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
	
	for(var key in ZaServer.currentkeys) {
		if(entry[ZaServer.currentkeys[key]]) {
			this._containedObject[ZaServer.currentkeys[key]] = entry[ZaServer.currentkeys[key]];
		}
	}
	this._containedObject.newVolID=-1;			
	this._localXForm.setInstance(this._containedObject);	
	this.updateTab();
}


ZaServerXFormView.getTLSEnabled = function () {
	var value = this.getModel().getInstanceValue(this.getInstance(),ZaServer.A_zimbraMtaAuthEnabled);
	return value == 'TRUE';
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

ZaServerXFormView.getMailProxyInstalled = function () {
	return this.getModel().getInstanceValue(this.getInstance(),ZaServer.A_zimbraMailProxyServiceInstalled);
}

ZaServerXFormView.getMailProxyEnabled = function () {
	return this.getModel().getInstanceValue(this.getInstance(),ZaServer.A_zimbraMailProxyServiceEnabled) && ZaServerXFormView.getMailProxyInstalled.call(this);	
}

ZaServerXFormView.getIMAPSSLProxyEnabled = function () {
	return (ZaServerXFormView.getMailProxyEnabled.call(this) && ZaServerXFormView.getIMAPSSLEnabled.call(this));
}

ZaServerXFormView.getPOP3ProxyEnabled = function () {
	return (ZaServerXFormView.getPOP3Enabled.call(this) && ZaServerXFormView.getMailProxyEnabled.call(this));
}

ZaServerXFormView.getPOP3SSLProxyEnabled = function () {
	return (ZaServerXFormView.getMailProxyEnabled.call(this) && ZaServerXFormView.getPOP3SSLEnabled.call(this));
}

ZaServerXFormView.volumeSelectionListener = 
function (ev) {
	var instance = this.getInstance();

	var arr = this.widget.getSelection();	
	if(arr && arr.length) {
		arr.sort(ZaServer.compareVolumesByName);
		instance.volume_selection_cache = arr;
	} else 
		instance.volume_selection_cache = null;
		
	this.getForm().refresh();
	if (ev.detail == DwtListView.ITEM_DBL_CLICKED) {
		ZaServerXFormView.editButtonListener.call(this);
	}	
}

ZaServerXFormView.isEditVolumeEnabled = function () {
	return (this.instance.volume_selection_cache != null && this.instance.volume_selection_cache.length==1);
}

ZaServerXFormView.isDeleteVolumeEnabled = function () {
	if(this.instance.volume_selection_cache != null && this.instance.volume_selection_cache.length>0) {
		for(var i = 0; i < this.instance.volume_selection_cache.length;i++) {
			for(a in ZaServer.currentkeys) {
				if(this.instance.volume_selection_cache[i][ZaServer.A_VolumeId]==this.instance[ZaServer.currentkeys[a]])
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
		var instance = this.getInstance();
		var dirty = false;
		if(instance.volume_selection_cache[0][ZaServer.A_VolumeId]==obj[ZaServer.A_VolumeId]) {
			if(instance.volume_selection_cache[0][ZaServer.A_VolumeName] != obj[ZaServer.A_VolumeName]) {
				instance.volume_selection_cache[0][ZaServer.A_VolumeName] = obj[ZaServer.A_VolumeName];
				dirty=true;
			}
			if(instance.volume_selection_cache[0][ZaServer.A_VolumeRootPath] != obj[ZaServer.A_VolumeRootPath]) {
				instance.volume_selection_cache[0][ZaServer.A_VolumeRootPath] = obj[ZaServer.A_VolumeRootPath];
				dirty=true;
			}
			if(instance.volume_selection_cache[0][ZaServer.A_VolumeCompressBlobs] != obj[ZaServer.A_VolumeCompressBlobs]) {
				instance.volume_selection_cache[0][ZaServer.A_VolumeCompressBlobs] = obj[ZaServer.A_VolumeCompressBlobs];
				dirty=true;
			}
			if(instance.volume_selection_cache[0][ZaServer.A_VolumeCompressionThreshold] != obj[ZaServer.A_VolumeCompressionThreshold]) {
				instance.volume_selection_cache[0][ZaServer.A_VolumeCompressionThreshold] = obj[ZaServer.A_VolumeCompressionThreshold];
				dirty=true;
			}
			if(instance.volume_selection_cache[0][ZaServer.A_VolumeType] != obj[ZaServer.A_VolumeType]) {
				instance.volume_selection_cache[0][ZaServer.A_VolumeType] = obj[ZaServer.A_VolumeType];
				dirty=true;
			}						

			/*if(obj[ZaServer.A_isCurrentVolume] && 
				!(instance[ZaServer.A_CurrentPrimaryMsgVolumeId] == obj[ZaServer.A_VolumeId] || 
					instance[ZaServer.A_CurrentSecondaryMsgVolumeId] == obj[ZaServer.A_VolumeId] ||
					instance[ZaServer.A_CurrentIndexVolumeId] == obj[ZaServer.A_VolumeId]
				)
			) {
				switch(obj[ZaServer.A_VolumeType]) {
					case ZaServer.PRI_MSG:
						instance[ZaServer.A_CurrentPrimaryMsgVolumeId] = obj[ZaServer.A_VolumeId];
					break;
					case ZaServer.SEC_MSG:
						instance[ZaServer.A_CurrentSecondaryMsgVolumeId] = obj[ZaServer.A_VolumeId];
					break;
					case ZaServer.INDEX:
						instance[ZaServer.A_CurrentIndexVolumeId] = obj[ZaServer.A_VolumeId];
					break;
				}
				dirty=true;
			}*/
		}

		if(dirty) {
			var indexArr = [];
			var msgArr = [];
			var cnt = instance[ZaServer.A_Volumes].length;
			for(var i=0;i<cnt;i++) {
				if(!instance[ZaServer.A_Volumes][i][ZaServer.A_VolumeId])
					continue;
					
				if(instance[ZaServer.A_Volumes][i][ZaServer.A_VolumeType]==ZaServer.MSG) {
					msgArr.push(instance[ZaServer.A_Volumes][i])
				} else if(instance[ZaServer.A_Volumes][i][ZaServer.A_VolumeType]==ZaServer.INDEX) {
					indexArr.push(instance[ZaServer.A_Volumes][i]);
				}
			}			
			ZaServerXFormView.indexVolChoices.setChoices(indexArr);
			ZaServerXFormView.indexVolChoices.dirtyChoices();	
			ZaServerXFormView.messageVolChoices.setChoices(msgArr);
			ZaServerXFormView.messageVolChoices.dirtyChoices();	
			instance.volume_selection_cache = new Array();
			instance[ZaServer.A_Volumes]._version++;
			this.parent.setDirty(dirty);	
		}
		this.refresh();				
	}
}

ZaServerXFormView.addVolume  = function () {
	if(this.parent.addVolumeDlg) {
		this.parent.addVolumeDlg.popdown();
		var obj = this.parent.addVolumeDlg.getObject();
		var instance = this.getInstance();
		instance.volume_selection_cache = new Array();
		instance[ZaServer.A_Volumes].push(obj);
		instance[ZaServer.A_Volumes]._version++;

		instance[ZaServer.A_Volumes].sort(ZaServer.compareVolumesByName);		
		var cnt = instance[ZaServer.A_Volumes].length;
		var indexArr = [];
		var msgArr = [];
		for(var i=0;i<cnt;i++) {
			if(instance[ZaServer.A_Volumes][i][ZaServer.A_VolumeType]==ZaServer.INDEX) {
				indexArr.push(instance[ZaServer.A_Volumes][i]);
			} else if(instance[ZaServer.A_Volumes][i][ZaServer.A_VolumeType] == ZaServer.MSG) {
				msgArr.push(instance[ZaServer.A_Volumes][i])
			}
		}

		
		ZaServerXFormView.indexVolChoices.setChoices(indexArr);
		ZaServerXFormView.indexVolChoices.dirtyChoices();	
	
		ZaServerXFormView.messageVolChoices.setChoices(msgArr);
		ZaServerXFormView.messageVolChoices.dirtyChoices();
	
		this.parent.setDirty(true);
		this.refresh();	
	}
}

ZaServerXFormView.editButtonListener =
function () {
	var instance = this.getInstance();
	if(instance.volume_selection_cache && instance.volume_selection_cache[0]) {	
		var formPage = this.getForm().parent;
		if(!formPage.editVolumeDlg) {
			formPage.editVolumeDlg = new ZaEditVolumeXDialog(formPage._app.getAppCtxt().getShell(), formPage._app,"550px", "150px",ZaMsg.VM_Edit_Volume_Title);
			formPage.editVolumeDlg.registerCallback(DwtDialog.OK_BUTTON, ZaServerXFormView.updateVolume, this.getForm(), null);						
		}
		var obj = {};
		obj[ZaServer.A_VolumeId] = instance.volume_selection_cache[0][ZaServer.A_VolumeId];
		obj[ZaServer.A_VolumeName] = instance.volume_selection_cache[0][ZaServer.A_VolumeName];
		obj[ZaServer.A_VolumeRootPath] = instance.volume_selection_cache[0][ZaServer.A_VolumeRootPath];
		obj[ZaServer.A_VolumeCompressBlobs] = instance.volume_selection_cache[0][ZaServer.A_VolumeCompressBlobs];
		obj[ZaServer.A_VolumeCompressionThreshold] = instance.volume_selection_cache[0][ZaServer.A_VolumeCompressionThreshold];
		obj[ZaServer.A_VolumeType] = instance.volume_selection_cache[0][ZaServer.A_VolumeType];		
		
		/*obj[ZaServer.A_isCurrentVolume] = (instance[ZaServer.A_CurrentPrimaryMsgVolumeId] == obj[ZaServer.A_VolumeId] || 
			instance[ZaServer.A_CurrentSecondaryMsgVolumeId] == obj[ZaServer.A_VolumeId] ||
			instance[ZaServer.A_CurrentIndexVolumeId] == obj[ZaServer.A_VolumeId])
		*/

		formPage.editVolumeDlg.setObject(obj);
		formPage.editVolumeDlg.popup();		
	}
}

ZaServerXFormView.deleteButtonListener = function () {
	var instance = this.getInstance();
	var path = ZaServer.A_Volumes;

	if(!this.getInstance()[ZaServer.A_RemovedVolumes]) {
		this.getInstance()[ZaServer.A_RemovedVolumes] = new Array();
	}

	if(instance.volume_selection_cache != null) {
		var cnt = instance.volume_selection_cache.length;
		if(cnt && instance[ZaServer.A_Volumes] && instance[ZaServer.A_Volumes]) {
			for(var i=0;i<cnt;i++) {
				var cnt2 = instance[ZaServer.A_Volumes].length-1;				
				for(var k=cnt2;k>=0;k--) {
					if(instance[ZaServer.A_Volumes][k][ZaServer.A_VolumeId]==instance.volume_selection_cache[i][ZaServer.A_VolumeId]) {
						instance[ZaServer.A_RemovedVolumes].push(instance[ZaServer.A_Volumes][k]);
						instance[ZaServer.A_Volumes].splice(k,1);
						break;	
					}
				}
			}
				
		}
	}
	
	instance[ZaServer.A_Volumes].sort(ZaServer.compareVolumesByName);		
	var cnt = instance[ZaServer.A_Volumes].length;
	var indexArr = [];
	var msgArr = [];
	for(var i=0;i<cnt;i++) {
		if(instance[ZaServer.A_Volumes][i][ZaServer.A_VolumeType]==ZaServer.INDEX) {
			indexArr.push(instance[ZaServer.A_Volumes][i]);
		} else if(instance[ZaServer.A_Volumes][i][ZaServer.A_VolumeType] == ZaServer.MSG) {
			msgArr.push(instance[ZaServer.A_Volumes][i])
		}
	}

	
	ZaServerXFormView.indexVolChoices.setChoices(indexArr);
	ZaServerXFormView.indexVolChoices.dirtyChoices();	

	ZaServerXFormView.messageVolChoices.setChoices(msgArr);
	ZaServerXFormView.messageVolChoices.dirtyChoices();	
	
	this.getForm().parent.setDirty(true);
	this.getForm().refresh();
}

ZaServerXFormView.addButtonListener =
function () {
	var instance = this.getInstance();
	var formPage = this.getForm().parent;
	if(!formPage.addVolumeDlg) {
		formPage.addVolumeDlg = new ZaEditVolumeXDialog(formPage._app.getAppCtxt().getShell(), formPage._app,"550px", "150px",ZaMsg.VM_Add_Volume_Title);
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

ZaServerXFormView.currentVolumeChanged = function (value, event, form) {
	this.getInstance()[ZaServer.A_Volumes]._version++;
	ZaTabView.onFormFieldChanged.call(this, value, event, form);
}
/**
* This method is added to the map {@link ZaTabView#XFormModifiers}
* @param xFormObject {Object} a definition of the form. This method adds/removes/modifies xFormObject to construct
* a Server view. 
**/
ZaServerXFormView.myXFormModifier = function(xFormObject) {	
	var headerList = new Array();
	headerList[0] = new ZaListHeaderItem(ZaServer.A_VolumeName, ZaMsg.VM_VolumeName, null, "100px", false, null, false, true);
	headerList[1] = new ZaListHeaderItem(ZaServer.A_VolumeRootPath, ZaMsg.VM_VolumeRootPath, null,"200px", false, null, false, true);
	headerList[2] = new ZaListHeaderItem(ZaServer.A_VolumeType, ZaMsg.VM_VolumeType, null, "120px", null, null, false, true);							
	headerList[3] = new ZaListHeaderItem(ZaServer.A_VolumeCompressBlobs, ZaMsg.VM_VolumeCompressBlobs, null, "120px", null, null, false, true);								
	headerList[4] = new ZaListHeaderItem(ZaServer.A_VolumeCompressionThreshold, ZaMsg.VM_VolumeCompressThreshold, null, "120px", null, null, false, true);									
	headerList[5] = new ZaListHeaderItem(ZaServer.A_isCurrentVolume, ZaMsg.VM_CurrentVolume, null, "50px", null, null, false, true);										



						
	
	xFormObject.tableCssStyle="width:100%;position:static;overflow:auto;";
	
	xFormObject.items = [
		{type:_GROUP_, cssClass:"ZmSelectedHeaderBg", colSpan: "*", id:"xform_header", 
			items: [
				{type:_GROUP_,	numCols:4,colSizes:["32px","350px","100px","250px"],
					items: [
						{type:_AJX_IMAGE_, src:"Server_32", label:null, rowSpan:2},
						{type:_OUTPUT_, ref:ZaServer.A_name, label:null,cssClass:"AdminTitle", rowSpan:2},				
						{type:_OUTPUT_, ref:ZaServer.A_ServiceHostname, label:ZaMsg.NAD_ServiceHostname+":"},
						{type:_OUTPUT_, ref:ZaItem.A_zimbraId, label:ZaMsg.NAD_ZimbraID}
					]
				}
			],
			cssStyle:"padding-top:5px; padding-bottom:5px"
		},
		{type:_TAB_BAR_, ref:ZaModel.currentTab,
			relevantBehavior:_HIDE_,
			containerCssStyle: "padding-top:0px",
			choices:[
				{value:1, label:ZaMsg.NAD_Tab_General},
				{value:2, label:ZaMsg.NAD_Tab_Services},
				{value:3, label:ZaMsg.NAD_Tab_MTA},
				{value:4, label:ZaMsg.NAD_Tab_IMAP},					
				{value:5, label:ZaMsg.NAD_Tab_POP},
				{value:6, label:ZaMsg.NAD_Tab_VolumeMgt}										
			],
			cssClass:"ZaTabBar", id:"xform_tabbar"
		},
		{type:_SWITCH_, items:[
				{type:_ZATABCASE_, colSizes:["auto"],numCols:1, relevant:"instance[ZaModel.currentTab] == 1", 
					id:"server_general_tab",
					items:[
						{type:_ZAGROUP_,items:[
							{ref:ZaServer.A_name, type:_OUTPUT_, label:ZaMsg.NAD_DisplayName+":", labelLocation:_LEFT_},
							{ ref: ZaServer.A_description, type:_INPUT_, 
							  label:ZaMsg.NAD_Description,cssClass:"admin_xform_name_input",
							  onChange:ZaServerXFormView.onFormFieldChanged
							},
							{ ref: ZaServer.A_ServiceHostname, type:_OUTPUT_, 
							  label:ZaMsg.NAD_ServiceHostname+":", cssClass:"admin_xform_name_input"/*,
							  onChange:ZaServerXFormView.onFormFieldChanged*/
							},
							{ ref: ZaServer.A_LmtpAdvertisedName, type:_INPUT_, 
							  label: ZaMsg.NAD_LmtpAdvertisedName, cssClass:"admin_xform_name_input",
							  onChange: ZaServerXFormView.onFormFieldChanged
							},
							{ ref: ZaServer.A_LmtpBindAddress, type:_INPUT_, 
							  label:ZaMsg.NAD_LmtpBindAddress, cssClass:"admin_xform_name_input",
							  onChange:ZaServerXFormView.onFormFieldChanged
							},
							{ ref: ZaServer.A_zimbraScheduledTaskNumThreads, type:_INPUT_, 
							  label:ZaMsg.NAD_zimbraScheduledTaskNumThreads, cssClass:"admin_xform_name_input",
							  onChange:ZaServerXFormView.onFormFieldChanged
							},
							{ ref: ZaServer.A_notes, type:_TEXTAREA_, 
							  label: ZaMsg.NAD_Notes, labelCssStyle: "vertical-align:top", width: "30em",
							  onChange:ZaServerXFormView.onFormFieldChanged
						    }
						]}
					]
				},
				{type:_ZATABCASE_, colSizes:["auto"],numCols:1, id:"server_services_tab", relevant:"instance[ZaModel.currentTab] == 2", 
					items:[
						{ type: _ZA_TOP_GROUPER_, label: ZaMsg.NAD_Service_EnabledServices, 
						  items: [
						  	{ ref: ZaServer.A_zimbraLdapServiceEnabled, type: _CHECKBOX_,
						  	  relevant: "instance.attrs[ZaServer.A_zimbraLdapServiceInstalled]", 
						  	  relevantBehavior: _DISABLE_,
						  	  label: ZaMsg.NAD_Service_LDAP,
					  	      onChange: ZaServerXFormView.onFormFieldChanged
						  	},
						  	{ ref: ZaServer.A_zimbraMailboxServiceEnabled, type: _CHECKBOX_,
						  	  relevant: "instance.attrs[ZaServer.A_zimbraMailboxServiceInstalled]", 
						  	  relevantBehavior: _DISABLE_,
						  	  label: ZaMsg.NAD_Service_Mailbox,
					  	      onChange: ZaServerXFormView.onFormFieldChanged
						  	},
						  	{ ref: ZaServer.A_zimbraMailProxyServiceEnabled, type: _CHECKBOX_,
						  	  relevant: "instance.attrs[ZaServer.A_zimbraMailProxyServiceInstalled]", 
						  	  relevantBehavior: _DISABLE_,
						  	  label: ZaMsg.NAD_Service_Imapproxy,
					  	      onChange: ZaServerXFormView.onFormFieldChanged
						  	},						  	
						  	{ ref: ZaServer.A_zimbraMtaServiceEnabled, type: _CHECKBOX_,
						  	  relevant: "instance.attrs[ZaServer.A_zimbraMtaServiceInstalled]", 
						  	  relevantBehavior: _DISABLE_,
						  	  label: ZaMsg.NAD_Service_MTA,
					  	      onChange: ZaServerXFormView.onFormFieldChanged
						  	},
						  	{ ref: ZaServer.A_zimbraSnmpServiceEnabled, type: _CHECKBOX_,
						  	  relevant: "instance.attrs[ZaServer.A_zimbraSnmpServiceInstalled]", 
						  	  relevantBehavior: _DISABLE_,
						  	  label: ZaMsg.NAD_Service_SNMP,
					  	      onChange: ZaServerXFormView.onFormFieldChanged
						  	},
						  	{ ref: ZaServer.A_zimbraAntiSpamServiceEnabled, type: _CHECKBOX_,
						  	  relevant: "instance.attrs[ZaServer.A_zimbraAntiSpamServiceInstalled]", 
						  	  relevantBehavior: _DISABLE_,
						  	  label: ZaMsg.NAD_Service_AntiSpam,
					  	      onChange: ZaServerXFormView.onFormFieldChanged
						  	},
						  	{ ref: ZaServer.A_zimbraAntiVirusServiceEnabled, type: _CHECKBOX_,
						  	  relevant: "instance.attrs[ZaServer.A_zimbraAntiVirusServiceInstalled]", 
						  	  relevantBehavior: _DISABLE_,
						  	  label: ZaMsg.NAD_Service_AntiVirus,
					  	      onChange: ZaServerXFormView.onFormFieldChanged
						  	},
						  	{ ref: ZaServer.A_zimbraSpellServiceEnabled, type: _CHECKBOX_,
						  	  relevant: "instance.attrs[ZaServer.A_zimbraSpellServiceInstalled]", 
						  	  relevantBehavior: _DISABLE_,
						  	  label: ZaMsg.NAD_Service_Spell,
					  	      onChange: ZaServerXFormView.onFormFieldChanged
						  	},
						  	{ ref: ZaServer.A_zimbraLoggerServiceEnabled, type: _CHECKBOX_,
						  	  relevant: "instance.attrs[ZaServer.A_zimbraLoggerServiceInstalled]", 
						  	  relevantBehavior: _DISABLE_,
						  	  label: ZaMsg.NAD_Service_Logger,
					  	      onChange: ZaServerXFormView.onFormFieldChanged
						  	}							  	
						]}
					]
				}, 
				{ type: _ZATABCASE_, id:"server_mta_tab", relevant: "instance[ZaModel.currentTab] == 3",
					colSizes:["auto"],numCols:1,
					items: [
						{type:_ZA_TOP_GROUPER_, colSizes:["auto"],numCols:1,label:ZaMsg.Global_MTA_AuthenticationGrp,
					      items: [
						      	{ ref:ZaServer.A_zimbraMtaAuthEnabled, type: _SUPER_CHECKBOX_,
						      	  trueValue: "TRUE", falseValue: "FALSE",
						      	  onChange: ZaServerXFormView.onFormFieldChanged,
						      	  resetToSuperLabel:ZaMsg.NAD_ResetToGlobal,
						      	  checkBoxLabel:ZaMsg.NAD_MTA_Authentication
					      	    },
						      	{ ref:ZaServer.A_zimbraMtaTlsAuthOnly, type: _SUPER_CHECKBOX_,
						      	  relevant:"ZaServerXFormView.getTLSEnabled.call(item)",
						      	  relevantBehavior: _DISABLE_,
						      	  trueValue: "TRUE", falseValue: "FALSE",
						      	  onChange: ZaServerXFormView.onFormFieldChanged,
						      	  resetToSuperLabel:ZaMsg.NAD_ResetToGlobal,
						      	  checkBoxLabel:ZaMsg.NAD_MTA_TlsAuthenticationOnly
					      	    }
				      	    ]
						},
				      {type:_ZA_TOP_GROUPER_, colSizes:["auto"],numCols:1,label:ZaMsg.Global_MTA_NetworkGrp,
					      items: [			      
							{ref:ZaServer.A_SmtpHostname, type:_SUPER_TEXTFIELD_, 
							  txtBoxLabel:ZaMsg.NAD_MTA_WebMailHostname,
							  resetToSuperLabel:ZaMsg.NAD_ResetToGlobal,
							  onChange: ZaServerXFormView.onFormFieldChanged,
							  toolTipContent: ZaMsg.tt_MTA_WebMailHostname,
							  textFieldCssClass:"admin_xform_name_input"
							},
							{type:_GROUP_,numCols:3,colSpan:3,colSizes:["275px","275px","150px"], 
						  		items:[					  	
									{ref:ZaServer.A_SmtpPort, type:_OUTPUT_, label:ZaMsg.NAD_MTA_WebMailPort, width:"4em"},
								  	{type:_SPACER_}								
								]
						  	},
							{ ref:ZaServer.A_zimbraMtaRelayHost, type:_SUPER_HOSTPORT_,
							    textBoxLabel: ZaMsg.NAD_MTA_RelayMTA, 
							    onChange: ZaServerXFormView.onFormFieldChanged,
							    onClick: "ZaController.showTooltip",
								toolTipContent: ZaMsg.tt_MTA_RelayMTA,
								onMouseout: "ZaController.hideTooltip",
							    resetToSuperLabel:ZaMsg.NAD_ResetToGlobal
							},
							{type:_GROUP_,numCols:3,colSpan:3,colSizes:["275px","275px","150px"], 
						  		items:[					  	
									{ref:ZaServer.A_SmtpTimeout, type:_TEXTFIELD_, 
									  label:ZaMsg.NAD_MTA_WebMailTimeout, width: "4em",
									  onChange: ZaServerXFormView.onFormFieldChanged
									},
								  	{type:_SPACER_},
									{ref:ZaServer.A_zimbraMtaMyNetworks,label:ZaMsg.NAD_MTA_MyNetworks,
										type:_TEXTFIELD_, 
										onChange: ZaServerXFormView.onFormFieldChanged,
										toolTipContent: ZaMsg.tt_MTA_MyNetworks,
										textFieldCssClass:"admin_xform_name_input"
									},						
							  		{type:_SPACER_}		  									
								]
						  	},
					        { ref: ZaServer.A_zimbraMtaDnsLookupsEnabled, type:_SUPER_CHECKBOX_,
					      	  checkBoxLabel:ZaMsg.NAD_MTA_DnsLookups,
					      	  trueValue: "TRUE", falseValue: "FALSE",
					      	  onChange: ZaServerXFormView.onFormFieldChanged,
					      	  resetToSuperLabel:ZaMsg.NAD_ResetToGlobal
				      	    }
						]
				      }		
				    ]
				},
				{type:_ZATABCASE_, colSizes:["auto"],numCols:1, relevant:"instance[ZaModel.currentTab] == 4",
					id:"server_imap_tab", 
					items:[
						{ type: _DWT_ALERT_,
						  containerCssStyle: "padding-bottom:0px",
						  style: DwtAlert.WARNING,
						  iconVisible: false, 
						  content: ZaMsg.Alert_ServerRestart
						},	
						{type:_ZA_TOP_GROUPER_, colSizes:["auto"],numCols:1,label:ZaMsg.Global_IMAP_ServiceGrp,
					      items: [						
						      	{ ref: ZaServer.A_ImapServerEnabled, type: _SUPER_CHECKBOX_,
						      	  checkBoxLabel:ZaMsg.IMAP_Service,
						      	  trueValue: "TRUE", falseValue: "FALSE",
						      	  onChange: ZaServerXFormView.onFormFieldChanged,
						      	  resetToSuperLabel:ZaMsg.NAD_ResetToGlobal
						  	    },	
						  	    {ref: ZaServer.A_ImapSSLServerEnabled, type: _SUPER_CHECKBOX_,
								  checkBoxLabel:ZaMsg.IMAP_SSLService,
							      relevant:"ZaServerXFormView.getIMAPEnabled.call(item)",
							      trueValue: "TRUE", falseValue: "FALSE",
							      onChange: ZaServerXFormView.onFormFieldChanged,
							      resetToSuperLabel:ZaMsg.NAD_ResetToGlobal,
							      relevantBehavior:_DISABLE_
						      	},
						  	    { ref: ZaServer.A_ImapCleartextLoginEnabled, type: _SUPER_CHECKBOX_,
						      	  checkBoxLabel:ZaMsg.IMAP_CleartextLoginEnabled,
						      	  relevant:"ZaServerXFormView.getIMAPEnabled.call(item)",
						      	  trueValue: "TRUE", falseValue: "FALSE",
						      	  onChange: ZaServerXFormView.onFormFieldChanged,
						      	  resetToSuperLabel:ZaMsg.NAD_ResetToGlobal,
						      	  relevantBehavior:_DISABLE_
					      	    },
					      	    { ref: ZaServer.A_zimbraImapNumThreads, type:_SUPER_TEXTFIELD_, 
								  relevant: "ZaServerXFormView.getIMAPEnabled.call(item)",
								  relevantBehavior: _HIDE_,
								  txtBoxLabel: ZaMsg.IMAP_NumThreads, width: "5em",
								  onChange: ZaServerXFormView.onFormFieldChanged,
						      	  resetToSuperLabel:ZaMsg.NAD_ResetToGlobal
								}
						   ]
						},
						{type:_ZA_TOP_GROUPER_, label:ZaMsg.Global_IMAP_NetworkGrp,
					      items: [
							{ ref: ZaServer.A_zimbraImapBindPort, type:_TEXTFIELD_, 
							  relevant: "ZaServerXFormView.getIMAPEnabled.call(item)",
							  relevantBehavior: _HIDE_,
							  label: ZaMsg.IMAP_Port+":", width: "5em",
							  onChange: ZaServerXFormView.onFormFieldChanged/*,
					      	  resetToSuperLabel:ZaMsg.NAD_ResetToGlobal*/
							},
							{ ref: ZaServer.A_ImapSSLBindPort, type:_TEXTFIELD_, 
							  relevant: "ZaServerXFormView.getIMAPSSLEnabled.call(item)",
							  relevantBehavior: _HIDE_,
							  label: ZaMsg.IMAP_SSLPort+":", width: "5em",
							  onChange: ZaServerXFormView.onFormFieldChanged/*,
						      resetToSuperLabel:ZaMsg.NAD_ResetToGlobal*/
							},		
							{ ref: ZaServer.A_zimbraImapProxyBindPort, type:_TEXTFIELD_, 
							  relevant: "ZaServerXFormView.getMailProxyEnabled.call(item)",
							  relevantBehavior: _HIDE_,
							  label: ZaMsg.IMAP_Proxy_Port+":", width: "5em",
							  onChange: ZaServerXFormView.onFormFieldChanged/*,
					      	  resetToSuperLabel:ZaMsg.NAD_ResetToGlobal*/
							},							
							{ ref: ZaServer.A_zimbraImapSSLProxyBindPort, type:_TEXTFIELD_, 
							  relevant: "ZaServerXFormView.getIMAPSSLProxyEnabled.call(item)",
							  relevantBehavior: _HIDE_,
							  label: ZaMsg.IMAP_SSL_Proxy_Port+":", width: "5em",
							  onChange: ZaServerXFormView.onFormFieldChanged/*,
					      	  resetToSuperLabel:ZaMsg.NAD_ResetToGlobal*/
							}
							]
						}										      	
					]
				},
				{type:_ZATABCASE_, relevant:"instance[ZaModel.currentTab] == 5",
					id:"server_pop_tab", colSizes:["auto"],numCols:1,
					items:[
						{ type: _DWT_ALERT_,
						  containerCssStyle: "padding-bottom:0px",
						  style: DwtAlert.WARNING,
						  iconVisible: false, 
						  content: ZaMsg.Alert_ServerRestart
						},
						{type: _ZA_TOP_GROUPER_, label:ZaMsg.Global_POP_ServiceGrp, 
						  items: [
					      	{ ref: ZaServer.A_Pop3ServerEnabled, type: _SUPER_CHECKBOX_,
					      	  trueValue: "TRUE", falseValue: "FALSE",
					      	  onChange: ZaServerXFormView.onFormFieldChanged,
					      	  resetToSuperLabel:ZaMsg.NAD_ResetToGlobal,
					      	  checkBoxLabel:ZaMsg.NAD_POP_Service, 
					      	  relevantBehavior:_DISABLE_
				      	    },
				      	    { ref: ZaServer.A_Pop3SSLServerEnabled, type: _SUPER_CHECKBOX_,
					      	  checkBoxLabel:ZaMsg.NAD_POP_SSL,
				      		  relevant:"ZaServerXFormView.getPOP3Enabled.call(item)",
				    	  	  relevantBehavior: _DISABLE_,
					      	  trueValue: "TRUE", falseValue: "FALSE",
					      	  onChange: ZaServerXFormView.onFormFieldChanged,
					      	  resetToSuperLabel:ZaMsg.NAD_ResetToGlobal,
					      	  relevantBehavior:_DISABLE_
				      	    },
				      	    { ref: ZaServer.A_Pop3CleartextLoginEnabled, type: _SUPER_CHECKBOX_,
					      	  checkBoxLabel:ZaMsg.NAD_POP_CleartextLoginEnabled,
				      		  relevant:"ZaServerXFormView.getPOP3Enabled.call(item)",
				    	  	  relevantBehavior: _DISABLE_,
					      	  trueValue: "TRUE", falseValue: "FALSE",
					      	  onChange: ZaServerXFormView.onFormFieldChanged,
					      	  resetToSuperLabel:ZaMsg.NAD_ResetToGlobal,
					      	  relevantBehavior:_DISABLE_
				      	    },
				      	    { ref: ZaServer.A_zimbraPop3NumThreads, type:_SUPER_TEXTFIELD_, 
							  relevant: "ZaServerXFormView.getPOP3Enabled.call(item)",
							  relevantBehavior: _HIDE_,
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
						  	{type:_GROUP_,numCols:3,colSpan:3,colSizes:["275px","275px","150px"], 
							  	relevant:"ZaServerXFormView.getPOP3Enabled.call(item)", relevantBehavior:_DISABLE_,
						  		items:[					  	
									{ ref: ZaServer.A_Pop3AdvertisedName, type:_TEXTFIELD_, 
									  labelLocation:_LEFT_, label: ZaMsg.NAD_POP_AdvertisedName, 
									  onChange: ZaServerXFormView.onFormFieldChanged
									},
								  	{type:_SPACER_}								
								]
						  	},		
							{type:_GROUP_,numCols:3,colSpan:3,colSizes:["275px","275px","150px"],
								relevant:"ZaServerXFormView.getPOP3Enabled.call(item)",relevantBehavior:_DISABLE_,
						  		items:[
									{ ref: ZaServer.A_Pop3BindAddress, type:_TEXTFIELD_, 
																	
									 	label:ZaMsg.NAD_POP_Address,
									  	onChange:ZaServerXFormView.onFormFieldChanged
								  	},
								  	{type:_SPACER_}
							  ]
						  	},						  	
							{ ref: ZaServer.A_zimbraPop3BindPort, type:_TEXTFIELD_, 
							  relevant:"ZaServerXFormView.getPOP3Enabled.call(item)",
							  relevantBehavior: _DISABLE_,
							  label: ZaMsg.NAD_POP_Port+":",
							  labelLocation:_LEFT_, 
							  textFieldCssClass:"admin_xform_number_input", 
							  onChange:ZaServerXFormView.onFormFieldChanged/*,
					      	  resetToSuperLabel:ZaMsg.NAD_ResetToGlobal*/
						  	},	
						  	
							{ ref: ZaServer.A_zimbraPop3SSLBindPort, type:_TEXTFIELD_,
							  relevant:"ZaServerXFormView.getPOP3SSLEnabled.call(item)",
							  relevantBehavior: _HIDE_,
							  labelLocation:_LEFT_, 
							  label: ZaMsg.NAD_POP_SSL_Port+":",
							  onChange:ZaServerXFormView.onFormFieldChanged/*,
					      	  resetToSuperLabel:ZaMsg.NAD_ResetToGlobal*/
						  	},	
							{ ref: ZaServer.A_zimbraPop3ProxyBindPort, type:_TEXTFIELD_,
							  relevant:"ZaServerXFormView.getPOP3ProxyEnabled.call(item)",
							  relevantBehavior: _HIDE_,
							  labelLocation:_LEFT_, 
							  textFieldCssClass:"admin_xform_number_input", 
							  label: ZaMsg.NAD_POP_proxy_Port+":",
							  onChange:ZaServerXFormView.onFormFieldChanged/*,
					      	  resetToSuperLabel:ZaMsg.NAD_ResetToGlobal*/
						  	},
							{ ref: ZaServer.A_zimbraPop3SSLProxyBindPort, type:_TEXTFIELD_, 
							  relevant:"ZaServerXFormView.getPOP3SSLProxyEnabled.call(item)",
							  relevantBehavior: _HIDE_,
							  labelLocation:_LEFT_, 
							  label: ZaMsg.NAD_POP_SSL_proxy_Port+":",
							  textFieldCssClass:"admin_xform_number_input", 
							  onChange:ZaServerXFormView.onFormFieldChanged/*,
					      	  resetToSuperLabel:ZaMsg.NAD_ResetToGlobal*/
							}
				      	]
						}					  
					]
				},
				{type:_ZATABCASE_,width:"100%", id:"server_form_volumes_tab", relevant:"((instance[ZaModel.currentTab] == 6) && ZaServerXFormView.getMailboxEnabled.call(item))", 
					numCols:1,
					items:[
						
						{type:_ZA_TOP_GROUPER_, id:"server_form_volumes_group",width:"98%", 
							numCols:1,colSizes:["auto"],label:ZaMsg.VM_VolumesGrpTitle,
							cssStyle:"margin-top:10px;margin-bottom:10px;padding-bottom:0px;margin-left:10px;margin-right:10px;",
							items: [
								{ref:ZaServer.A_Volumes, type:_DWT_LIST_, height:"200", width:"100%", 
									 	forceUpdate: true, preserveSelection:false, multiselect:true,cssClass: "DLSource", 
									 	headerList:headerList, widgetClass:ZaServerVolumesListView,
									 	onSelection:ZaServerXFormView.volumeSelectionListener
								},
								{type:_GROUP_, numCols:5, colSizes:["100px","auto","100px","auto","100px"], width:"350px",
									cssStyle:"margin-bottom:10px;padding-bottom:0px;margin-top:10px;pxmargin-left:10px;margin-right:10px;",
									items: [
										{type:_DWT_BUTTON_, label:ZaMsg.TBB_Delete,width:"100px",
											onActivate:"ZaServerXFormView.deleteButtonListener.call(this);",
											relevant:"ZaServerXFormView.isDeleteVolumeEnabled.call(this)", relevantBehavior:_DISABLE_
										},
										{type:_CELLSPACER_},
										{type:_DWT_BUTTON_, label:ZaMsg.TBB_Edit,width:"100px",
											onActivate:"ZaServerXFormView.editButtonListener.call(this);",
											relevant:"ZaServerXFormView.isEditVolumeEnabled.call(this)", relevantBehavior:_DISABLE_
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
							{type:_OSELECT1_, editable:false,forceUpdate: true,
							ref:ZaServer.A_CurrentMsgVolumeId,
							choices:ZaServerXFormView.messageVolChoices,
							onChange:ZaServerXFormView.currentVolumeChanged,
							label:ZaMsg.VM_CurrentMessageVolume+":"},
							{type:_OSELECT1_, editable:false,forceUpdate: true,
							ref:ZaServer.A_CurrentIndexVolumeId,
							choices:ZaServerXFormView.indexVolChoices,
							onChange:ZaServerXFormView.currentVolumeChanged,
							label:ZaMsg.VM_CurrentIndexVolume+":"}
						]}						
						
					]
				},
				{type:_ZATABCASE_, relevant:"((instance[ZaModel.currentTab] == 6) && !instance[ZaServer.A_showVolumes])", 					
					items: [
						{ type: _DWT_ALERT_,
						  cssClass: "DwtTabTable",
						  containerCssStyle: "padding-bottom:0px",
						  style: DwtAlert.WARNING,
						  iconVisible: true, 
						  content:ZaMsg.Alert_MbxSvcNotInstalled,
						  colSpan:"*"
						}						
					]
				
				}
			]
		}	
	];
};
ZaTabView.XFormModifiers["ZaServerXFormView"].push(ZaServerXFormView.myXFormModifier);