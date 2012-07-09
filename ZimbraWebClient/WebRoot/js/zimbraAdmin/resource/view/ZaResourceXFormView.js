/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2006, 2007, 2008, 2009, 2010 Zimbra, Inc.
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
* This class describes a view of a single resource Account, it will be called during the edit mode
* @class ZaResourceXFormView
* @contructor
* @param parent {DwtComposite}
* @param app {ZaApp}
* @author Greg Solovyev
**/
ZaResourceXFormView = function(parent, entry) {
	ZaTabView.call(this, {
		parent:parent,
		iKeyName:"ZaResourceXFormView",
		contextId:ZaId.TAB_RES_EDIT
	});	
	this.TAB_INDEX = 0;		
	if(!ZaResource.accountStatusChoices) {
		ZaResource.accountStatusChoices = [
	   		{value:ZaResource.ACCOUNT_STATUS_ACTIVE, label:ZaResource.getAccountStatusLabel(ZaResource.ACCOUNT_STATUS_ACTIVE)}, 
	   		{value:ZaResource.ACCOUNT_STATUS_CLOSED, label:ZaResource.getAccountStatusLabel(ZaResource.ACCOUNT_STATUS_CLOSED)}
		//{value:ZaResource.ACCOUNT_STATUS_LOCKED, label: ZaResource.getAccountStatusLabel(ZaResource.ACCOUNT_STATUS_LOCKED)},
		//{value:ZaResource.ACCOUNT_STATUS_MAINTENANCE, label:ZaResource.getAccountStatusLabel(ZaResource.ACCOUNT_STATUS_MAINTENANCE)}
	   	];		
	}
	this.cosChoices = new XFormChoices([], XFormChoices.OBJECT_LIST, "id", "name");
    this.signatureChoices = new XFormChoices([], XFormChoices.OBJECT_LIST, "id", "name");
	this.initForm(ZaResource.myXModel,this.getMyXForm(entry), null);
	this._localXForm.setController(ZaApp.getInstance());	
	this._helpURL = ZaResourceXFormView.helpURL;
}

ZaResourceXFormView.prototype = new ZaTabView();
ZaResourceXFormView.prototype.constructor = ZaResourceXFormView;
ZaTabView.XFormModifiers["ZaResourceXFormView"] = new Array();
ZaResourceXFormView.TAB_INDEX=0;
ZaResourceXFormView.helpURL = location.pathname + ZaUtil.HELP_URL + "managing_accounts/managing_resource.htm?locid="+AjxEnv.DEFAULT_LOCALE;

ZaResourceXFormView.prototype.getTabIcon =
function () {
	if (this._containedObject && this._containedObject.attrs && this._containedObject.attrs[ZaResource.A_zimbraCalResType] == ZaResource.RESOURCE_TYPE_LOCATION){
		return "Location" ;	
	}else {
		return "Resource" ;
	}
}

/**
* Sets the object contained in the view
* @param entry - {ZaResource} object to display
**/
ZaResourceXFormView.prototype.setObject =
function(entry) {
	this._containedObject = new Object();
    this._containedObject.attrs = new Object();

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
	this._containedObject.name = entry.name;
	this._containedObject.type = entry.type ;
	if(entry.id)
		this._containedObject.id = entry.id;

	if(entry.rights)
		this._containedObject.rights = entry.rights;
	
	if(entry.setAttrs)
		this._containedObject.setAttrs = entry.setAttrs;
	
	if(entry.getAttrs)
		this._containedObject.getAttrs = entry.getAttrs;
		
	if(entry._defaultValues)
		this._containedObject._defaultValues = entry._defaultValues;
		
	
	if(this._containedObject.attrs[ZaResource.A_COSId]) {	
		this._containedObject[ZaResource.A2_autoCos] = "FALSE" ;		
	}
	if(!this._containedObject.attrs[ZaResource.A_COSId]) {
		this._containedObject[ZaResource.A2_autoCos] = "TRUE" ;
	}
	if(this._containedObject.setAttrs[ZaResource.A_COSId]) {
		var cos = ZaCos.getCosById(this._containedObject.attrs[ZaResource.A_COSId]);	
		this.cosChoices.setChoices([cos]);
		this.cosChoices.dirtyChoices();
	}		

    if(entry[ZaResource.A2_signatureList]) {
        this._containedObject[ZaResource.A2_signatureList] = entry[ZaResource.A2_signatureList];

    } else {
        this._containedObject[ZaResource.A2_signatureList] = [];
    }

    this.signatureChoices.setChoices(ZaSignature.getSignatureChoices(this._containedObject[ZaResource.A2_signatureList]));
    this.signatureChoices.dirtyChoices();

   	this._containedObject[ZaResource.A2_autodisplayname] = "FALSE";
   	this._containedObject[ZaResource.A2_autoLocationName] = entry[ZaResource.A2_autoLocationName];
   	
   	//set the value of the A_schedulePolicy
   	ZaResource.prototype.setSchedulePolicyFromLdapAttrs.call (this._containedObject) ;
   	   				
	if(!entry[ZaModel.currentTab])
		this._containedObject[ZaModel.currentTab] = "1";
	else
		this._containedObject[ZaModel.currentTab] = entry[ZaModel.currentTab];
		
	this._localXForm.setInstance(this._containedObject);
	
	//enforce the dirty = false, so the save button after the save can be disabled.
	this.setDirty(false);
	this.formDirtyLsnr = new AjxListener(ZaApp.getInstance().getCurrentController(), ZaResourceController.prototype.handleXFormChange);
	this._localXForm.addListener(DwtEvent.XFORMS_FORM_DIRTY_CHANGE, this.formDirtyLsnr);
	this._localXForm.addListener(DwtEvent.XFORMS_VALUE_ERROR, this.formDirtyLsnr);	

    if(!appNewUI)
	    this.updateTab();
}

ZaResourceXFormView.deleteCalFwdAddrButtonListener = function () {
	var instance = this.getInstance();	
	if(instance[ZaResource.A2_calFwdAddr_selection_cache] != null) {
		var cnt = instance[ZaResource.A2_calFwdAddr_selection_cache].length;
		if(cnt && instance.attrs[ZaResource.A_zimbraPrefCalendarForwardInvitesTo]) {
			var arr = instance.attrs[ZaResource.A_zimbraPrefCalendarForwardInvitesTo];
			for(var i=0;i<cnt;i++) {
				var cnt2 = arr.length-1;				
				for(var k=cnt2;k>=0;k--) {
					if(arr[k]==instance[ZaResource.A2_calFwdAddr_selection_cache][i]) {
						arr.splice(k,1);
						break;	
					}
				}
			}
			this.getModel().setInstanceValue(instance, ZaResource.A_zimbraPrefCalendarForwardInvitesTo, arr);
			this.getModel().setInstanceValue(instance, ZaResource.A2_calFwdAddr_selection_cache, []);	
		}
	}
	this.getForm().parent.setDirty(true);
}

ZaResourceXFormView.calFwdAddrSelectionListener = 
function (ev) {
	var arr = this.widget.getSelection();	
	if(arr && arr.length) {
		arr.sort();
		this.getModel().setInstanceValue(this.getInstance(), ZaResource.A2_calFwdAddr_selection_cache, arr);	
	} else {
		this.getModel().setInstanceValue(this.getInstance(), ZaResource.A2_calFwdAddr_selection_cache, []);
	}	
	if (ev.detail == DwtListView.ITEM_DBL_CLICKED) {
		ZaResourceXFormView.editCalFwdAddrButtonListener.call(this);
	}	
}

ZaResourceXFormView.editCalFwdAddrButtonListener =
function () {
	var instance = this.getInstance();
	if(instance[ZaResource.A2_calFwdAddr_selection_cache] && instance[ZaResource.A2_calFwdAddr_selection_cache][0]) {	
		var formPage = this.getForm().parent;
		if(!formPage.editCalFwdAddrDlg) {
			formPage.editCalFwdAddrDlg = new ZaEditFwdAddrXDialog(ZaApp.getInstance().getAppCtxt().getShell(),"400px", "150px",ZaMsg.Edit_FwdAddr_Title);
			formPage.editCalFwdAddrDlg.registerCallback(DwtDialog.OK_BUTTON, ZaAccountXFormView.updateCalFwdAddr, this.getForm(), null);						
		}
		var obj = {};
		obj[ZaAccount.A_name] = instance[ZaAccount.A2_calFwdAddr_selection_cache][0];
		var cnt = instance.attrs[ZaResource.A_zimbraPrefCalendarForwardInvitesTo].length;
		for(var i=0;i<cnt;i++) {
			if(instance[ZaResource.A2_calFwdAddr_selection_cache][0]==instance.attrs[ZaResource.A_zimbraPrefCalendarForwardInvitesTo][i]) {
				obj[ZaAlias.A_index] = i;
				break;		
			}
		}
		
		formPage.editCalFwdAddrDlg.setObject(obj);
		formPage.editCalFwdAddrDlg.popup();		
	}
}

ZaResourceXFormView.updateCalFwdAddr = function () {
	if(this.parent.editCalFwdAddrDlg) {
		this.parent.editCalFwdAddrDlg.popdown();
		var obj = this.parent.editCalFwdAddrDlg.getObject();
		var instance = this.getInstance();
		var arr = instance.attrs[ZaResource.A_zimbraPrefCalendarForwardInvitesTo];
		if(obj[ZaAlias.A_index] >=0 && arr[obj[ZaAlias.A_index]] != obj[ZaResource.A_name] ) {
			this.getModel().setInstanceValue(this.getInstance(), ZaResource.A2_calFwdAddr_selection_cache, []);
			arr[obj[ZaAlias.A_index]] = obj[ZaResource.A_name];
			this.getModel().setInstanceValue(instance, ZaResource.A_zimbraPrefCalendarForwardInvitesTo, arr);
			this.parent.setDirty(true);	
		}
	}
}

ZaResourceXFormView.addCalFwdAddrButtonListener =
function () {
	var instance = this.getInstance();
	var formPage = this.getForm().parent;
	if(!formPage.addCalFwdAddrDlg) {
		formPage.addCalFwdAddrDlg = new ZaEditFwdAddrXDialog(ZaApp.getInstance().getAppCtxt().getShell(), "400px", "150px",ZaMsg.Add_FwdAddr_Title);
		formPage.addCalFwdAddrDlg.registerCallback(DwtDialog.OK_BUTTON, ZaResourceXFormView.addCalFwdAddr, this.getForm(), null);						
	}
	
	var obj = {};
	obj[ZaAccount.A_name] = "";
	obj[ZaAlias.A_index] = - 1;
	formPage.addCalFwdAddrDlg.setObject(obj);
	formPage.addCalFwdAddrDlg.popup();		
}

ZaResourceXFormView.addCalFwdAddr  = function () {
	if(this.parent.addCalFwdAddrDlg) {
		this.parent.addCalFwdAddrDlg.popdown();
		var obj = this.parent.addCalFwdAddrDlg.getObject();
		if(obj[ZaResource.A_name] && obj[ZaResource.A_name].length>1) {
			var arr = this.getInstance().attrs[ZaResource.A_zimbraPrefCalendarForwardInvitesTo];
			arr.push(obj[ZaResource.A_name]);
			this.getModel().setInstanceValue(this.getInstance(), ZaResource.A_zimbraPrefCalendarForwardInvitesTo, arr);
			this.getModel().setInstanceValue(this.getInstance(), ZaResource.A2_calFwdAddr_selection_cache, []);
			this.parent.setDirty(true);
		}
	}
}

ZaResourceXFormView.isEditCalFwdAddrEnabled = function () {
	return (!AjxUtil.isEmpty(this.getInstanceValue(ZaResource.A2_calFwdAddr_selection_cache)) && this.getInstanceValue(ZaResource.A2_calFwdAddr_selection_cache).length==1);
}

ZaResourceXFormView.isDeleteCalFwdAddrEnabled = function () {
	return (!AjxUtil.isEmpty(this.getInstanceValue(ZaResource.A2_calFwdAddr_selection_cache)));
}

ZaResourceXFormView.isAutoDisplayname = function () {
    return(this.getInstanceValue(ZaResource.A2_autoLocationName)=="FALSE");
}


ZaResourceXFormView.SignatureSelectionListener =
function (ev) {
	var arr = this.widget.getSelection();
	if(arr && arr.length) {
		arr.sort();
		this.getModel().setInstanceValue(this.getInstance(), ZaResource.A2_signature_selection_cache, arr);
	} else {
		this.getModel().setInstanceValue(this.getInstance(), ZaResource.A2_signature_selection_cache, []);
	}
	if (ev.detail == DwtListView.ITEM_DBL_CLICKED) {
		ZaResourceXFormView.editSignatureButtonListener.call(this);
	}
}


ZaResourceXFormView.editSignatureButtonListener =
function () {
    try {
        var instance = this.getInstance();
        if(instance[ZaResource.A2_signature_selection_cache] && instance[ZaResource.A2_signature_selection_cache][0]) {
            var formPage = this.getForm().parent;
            if(!formPage.editSignatureDlg) {
                formPage.editSignatureDlg = new ZaEditSignatureDialog(ZaApp.getInstance().getAppCtxt().getShell(),"400px", "150px",ZaMsg.Title_EditSignature);
                formPage.editSignatureDlg.registerCallback(DwtDialog.OK_BUTTON, ZaResourceXFormView.updateSignature, this.getForm(), null);
            }
            var obj  = ZaUtil.deepCloneObject(instance[ZaResource.A2_signature_selection_cache][0]);

            formPage.editSignatureDlg.setObject(obj);
            formPage.editSignatureDlg.popup();
        }
    } catch(ex) {
        ZaApp.getInstance().getCurrentController()._handleException(ex, "ZaResourceXFormView.editSignatureButtonListener", null, false);
    }
}

ZaResourceXFormView.updateSignature = function () {
   try {
        if(this.parent.editSignatureDlg) {
            this.parent.editSignatureDlg.popdown();
            var obj = this.parent.editSignatureDlg.getObject();
            var instance = this.getInstance();
            var arr = instance[ZaResource.A2_signatureList];
            var index = ZaUtil.findValueInObjArrByPropertyName(arr, obj[ZaSignature.A2_id], ZaSignature.A2_id);
            if(index != -1 && !ZaSignature.compareObject(obj, arr[index])) {
                ZaSignature.ModifySignature.call(obj, "id", instance.id);
                this.getModel().setInstanceValue(this.getInstance(), ZaResource.A2_signature_selection_cache, []);
                arr[index] = obj;
                this.getModel().setInstanceValue(instance, ZaResource.A2_signatureList, arr);
            }
        }
   } catch(ex) {
        ZaApp.getInstance().getCurrentController()._handleException(ex, "ZaResourceXFormView.updateSignature", null, false);
   }
}


ZaResourceXFormView.addSignatureButtonListener =
function () {
    try{
        var instance = this.getInstance();
        var formPage = this.getForm().parent;
        if(!formPage.addSignatureDlg) {
            formPage.addSignatureDlg = new ZaEditSignatureDialog(ZaApp.getInstance().getAppCtxt().getShell(), "400px", "150px",ZaMsg.Title_CreateSignature);
            formPage.addSignatureDlg.registerCallback(DwtDialog.OK_BUTTON, ZaResourceXFormView.addSignature, this.getForm(), null);
        }

        var obj = new ZaSignature();
        formPage.addSignatureDlg.setObject(obj);
        formPage.addSignatureDlg.popup();
    } catch(ex) {
        ZaApp.getInstance().getCurrentController()._handleException(ex, "ZaResourceXFormView.addSignatureButtonListener", null, false);
    }
}

ZaResourceXFormView.addSignature  = function () {
    try {
        if(this.parent.addSignatureDlg) {
            this.parent.addSignatureDlg.popdown();
            var obj = this.parent.addSignatureDlg.getObject();
            if(obj[ZaSignature.A2_name] && obj[ZaSignature.A2_name].length>0) {
                var instance = this.getInstance();
                var arr = instance[ZaResource.A2_signatureList];
                var index = ZaUtil.findValueInObjArrByPropertyName(arr, obj[ZaSignature.A2_name], ZaSignature.A2_name);
                if(index == -1) {
                obj =  ZaSignature.CreateSignature.call(obj, "id", instance.id);
                arr.push(obj);
                this.getModel().setInstanceValue(this.getInstance(), ZaResource.A2_signatureList, arr);
                this.getModel().setInstanceValue(this.getInstance(), ZaResource.A2_signature_selection_cache, []);
                } else {
                    var warningMsg = AjxMessageFormat.format(ZaMsg.SignatureExist, obj[ZaSignature.A2_name]);
                    ZaApp.getInstance().getCurrentController().popupErrorDialog(warningMsg);
                }
            } else {
                ZaApp.getInstance().getCurrentController().popupErrorDialog(ZaMsg.CreateWarningMsg);
            }
        }
    } catch (ex) {
        ZaApp.getInstance().getCurrentController()._handleException(ex, "ZaResourceXFormView.addSignature", null, false);
    }
}


ZaResourceXFormView.deleteSignatureButtonListener = function () {
    try {
        var instance = this.getInstance();
        if(instance[ZaResource.A2_signature_selection_cache] != null) {
            var cnt = instance[ZaResource.A2_signature_selection_cache].length;
            if(cnt && instance[ZaResource.A2_signatureList]) {
                var i;
                var isUsed = false;
                var warningMsg;
                for (i = 0; i < cnt; i++) {
                    var currentId = instance[ZaResource.A2_signature_selection_cache][i][ZaSignature.A2_id];
                    if(this.getInstanceValue(ZaResource.A_zimbraPrefCalendarAutoAcceptSignatureId) == currentId){
                        isUsed = true;
                        warningMsg = AjxMessageFormat.format(ZaMsg.DeleteWarningAcceptMsg, instance[ZaResource.A2_signature_selection_cache][i][ZaSignature.A2_name]);
                        break;
                    }

                    if(this.getInstanceValue(ZaResource.A_zimbraPrefCalendarAutoDenySignatureId) == currentId){
                        isUsed = true;
                        warningMsg = AjxMessageFormat.format(ZaMsg.DeleteWarningDenyMsg, instance[ZaResource.A2_signature_selection_cache][i][ZaSignature.A2_name]);
                        break;
                    }

                    if(this.getInstanceValue(ZaResource.A_zimbraPrefCalendarAutoDeclineSignatureId) == currentId){
                        warningMsg = AjxMessageFormat.format(ZaMsg.DeleteWarningDeclineMsg, instance[ZaResource.A2_signature_selection_cache][i][ZaSignature.A2_name]);
                        isUsed = true;
                        break;
                    }
                }

                if (!isUsed) {
                    var arr = instance[ZaResource.A2_signatureList];
                    for(i=0;i<cnt;i++) {
                        var cnt2 = arr.length-1;
                        for(var k=cnt2;k>=0;k--) {
                            if(arr[k][ZaSignature.A2_id]==instance[ZaResource.A2_signature_selection_cache][i][ZaSignature.A2_id]) {
                                ZaSignature.DeleteSignature.call(instance[ZaResource.A2_signature_selection_cache][i], "id", instance.id);
                                arr.splice(k,1);
                                break;
                            }
                        }
                    }
                    this.getModel().setInstanceValue(instance, ZaResource.A2_signatureList, arr);
                    this.getModel().setInstanceValue(instance, ZaResource.A2_signature_selection_cache, []);
                } else {
                    ZaApp.getInstance().getCurrentController().popupErrorDialog(warningMsg);
                }
            }
        }
    } catch(ex) {
        ZaApp.getInstance().getCurrentController()._handleException(ex, "ZaResourceXFormView.deleteSignatureButtonListener", null, false);
    }
}

ZaResourceXFormView.isEditSignatureEnabled = function () {
	return (!AjxUtil.isEmpty(this.getInstanceValue(ZaResource.A2_signature_selection_cache)) && this.getInstanceValue(ZaResource.A2_signature_selection_cache).length==1);
}

ZaResourceXFormView.isDeleteSignatureEnabled = function () {
	return (!AjxUtil.isEmpty(this.getInstanceValue(ZaResource.A2_signature_selection_cache)));
}

ZaResourceXFormView.isSignatureSelectionEnabled = function() {
	return (!AjxUtil.isEmpty(this.getInstanceValue(ZaResource.A2_signatureList)));
}

ZaResourceXFormView.CONTACT_TAB_ATTRS = [ZaResource.A_zimbraCalResContactName,
		ZaResource.A_zimbraCalResContactEmail, 
		ZaResource.A_zimbraCalResContactPhone, 
		ZaResource.A_contactInfoAutoComplete, 
		ZaResource.A_locationDisplayName,
		ZaResource.A2_autoLocationName,
		ZaResource.A_zimbraCalResSite,
		ZaResource.A_zimbraCalResBuilding,
		ZaResource.A_zimbraCalResFloor,
		ZaResource.A_zimbraCalResRoom,
		ZaResource.A_zimbraCalResCapacity,
		ZaResource.A_street,
		ZaResource.A_city,
		ZaResource.A_state,
		ZaResource.A_country,
		ZaResource.A_zip];

ZaResourceXFormView.CONTACT_TAB_RIGHTS = [];

/**
* This method is added to the map {@link ZaTabView#XFormModifiers}
* @param xFormObject {Object} a definition of the form. This method adds/removes/modifies xFormObject to construct
* an Account view. 
**/
ZaResourceXFormView.myXFormModifier = function(xFormObject, entry) {	
	ZaResource.resTypeChoices = [
   		{value:ZaResource.RESOURCE_TYPE_LOCATION, label:ZaMsg.resType_location}, 
   		{value:ZaResource.RESOURCE_TYPE_EQUIPMENT, label:ZaMsg.resType_equipment}
   	];	
  		                     	
  	ZaResource.schedulePolicyChoices = [
   		{value:ZaResource.SCHEDULE_POLICY_TT, label:ZaMsg.resScheduleTT},
   		{value:ZaResource.SCHEDULE_POLICY_FT, label:ZaMsg.resScheduleFT},
   		{value:ZaResource.SCHEDULE_POLICY_TF, label:ZaMsg.resScheduleTF},
   		{value:ZaResource.SCHEDULE_POLICY_FF, label:ZaMsg.resScheduleFF}
   	];		

	var domainName;
	domainName = ZaSettings.myDomainName;

	//get the image according to the type
	var imgChoices = [ 	{value:ZaResource.RESOURCE_TYPE_LOCATION, label: "Location_32"},
						{value:ZaResource.RESOURCE_TYPE_EQUIPMENT, label: "Resource_32"}   ];
						
	var headerItems = [	{type:_AJX_IMAGE_, ref:ZaResource.A_zimbraCalResType, src:"Resource_32", label:null, rowSpan:3, choices: imgChoices, cssStyle:"margin:auto;"},
						{type:_OUTPUT_, ref:ZaResource.A_displayname, label:null,cssClass:"AdminTitle", height:"auto", width:350, rowSpan:3, cssStyle:"word-wrap:break-word;overflow:hidden;",
                             visibilityChecks:[ZaItem.hasReadPermission]}];
						
	/*headerItems.push({type:_OUTPUT_, ref:ZaResource.A_COSId, labelLocation:_LEFT_, label:ZaMsg.NAD_ClassOfService, 
		choices:this.cosChoices,getDisplayValue:function(newValue) {
			if(ZaItem.ID_PATTERN.test(newValue)) {
				var cos = ZaCos.getCosById(newValue, this.getForm().parent._app);
				if(cos)
					newValue = cos.name;
				} 
				if (newValue == null) {
					newValue = "";
				} else {
					newValue = "" + newValue;
				}
				return newValue;
			},
	 visibilityChecks:[ZaItem.hasReadPermission]});*/
	
    if (ZaItem.hasReadPermission(ZaItem.A_zimbraId, entry))
        headerItems.push({type:_OUTPUT_,  ref:ZaItem.A_zimbraId, label:ZaMsg.NAD_ZimbraID});
	
    if (ZaItem.hasReadPermission(ZaItem.A_zimbraCreateTimestamp, entry))
        headerItems.push({type:_OUTPUT_, ref:ZaItem.A_zimbraCreateTimestamp,
						 label:ZaMsg.LBL_zimbraCreateTimestamp, labelLocation:_LEFT_,
						 getDisplayValue:function() {
						 var val = ZaItem.formatServerTime(this.getInstanceValue());
						 if(!val)
						 return ZaMsg.Server_Time_NA;
						 else
						 return val;
						 }	
						 });
	
    if (ZaItem.hasReadPermission(ZaResource.A_mailHost, entry)) {
	    headerItems.push({type:_OUTPUT_, ref:ZaResource.A_mailHost, labelLocation:_LEFT_,label:ZaMsg.NAD_MailServer});
    }
	
    if (ZaItem.hasReadPermission(ZaResource.A_name, entry))
    	headerItems.push({type:_OUTPUT_, ref:ZaResource.A_name, label:ZaMsg.NAD_Email, labelLocation:_LEFT_, required:false});

    if (ZaItem.hasReadPermission(ZaResource.A_accountStatus, entry))
	    headerItems.push({type:_OUTPUT_,  ref:ZaResource.A_accountStatus, label:ZaMsg.NAD_ResourceStatus, labelLocation:_LEFT_, choices:ZaResource.accountStatusChoices});

    if (ZaItem.hasReadPermission(ZaResource.A_zimbraCalResType, entry))
        headerItems.push({type:_OUTPUT_, ref:ZaResource.A_zimbraCalResType, label:ZaMsg.NAD_ResType, labelLocation:_LEFT_, required:false,
						getDisplayValue: ZaResource.getResTypeLabel });	

	this.tabChoices = new Array();
	var _tab2;
	var _tab1 = ++this.TAB_INDEX;
    this.tabChoices.push({value:_tab1, label:ZaMsg.TABT_ResourceProperties});
    this.helpMap = {};
    this.helpMap[_tab1] = [location.pathname, ZaUtil.HELP_URL, "managing_accounts/managing_resource.htm", "?locid=", AjxEnv.DEFAULT_LOCALE].join("");
	if(ZaTabView.isTAB_ENABLED(entry,ZaResourceXFormView.CONTACT_TAB_ATTRS, ZaResourceXFormView.CONTACT_TAB_RIGHTS)) {
		_tab2 = ++this.TAB_INDEX;
		this.tabChoices.push({value:_tab2, label:ZaMsg.TABT_ResLocationContact});	
	}

    var cases = [];

    var nameGroup = {type:_TOP_GROUPER_, label:ZaMsg.NAD_ResourceNameGrouper, id:"resource_form_name_group",
            colSizes:["275px","*"],numCols:2,items:[
            {ref:ZaResource.A_displayname, type:_TEXTFIELD_, msgName:ZaMsg.NAD_ResourceName,
                label:ZaMsg.NAD_ResourceName, labelLocation:_LEFT_, width: "200px" },
            {ref:ZaResource.A_name, type:_EMAILADDR_, msgName:ZaMsg.NAD_ResAccountName,label:ZaMsg.NAD_ResAccountName,
                labelLocation:_LEFT_,enableDisableChecks:[[XFormItem.prototype.hasRight,ZaResource.RENAME_CALRES_RIGHT]],
				visibilityChecks:[]
            }]
    };
    var setupGroup = {type:_TOP_GROUPER_, label:ZaMsg.NAD_ResourceSetupGrouper, id:"resource_form_setup_group",
        colSizes:["275px","*"],numCols:2,items:[
        {ref:ZaResource.A_zimbraCalResType, type:_OSELECT1_, msgName:ZaMsg.NAD_ResType,
            label:ZaMsg.NAD_ResType, labelLocation:_LEFT_,
            choices:ZaResource.resTypeChoices
    }]};
       setupGroup.items.push(
          {type:_GROUP_, numCols:3, nowrap:true, label:ZaMsg.NAD_ClassOfService, labelLocation:_LEFT_,
          	visibilityChecks:[[ZaItem.hasWritePermission,ZaResource.A_COSId]],
            items: [
              {ref:ZaResource.A_COSId, type:_DYNSELECT_,label: null, choices:this.cosChoices,
                   inputPreProcessor:ZaAccountXFormView.preProcessCOS,
                   emptyText:ZaMsg.enterSearchTerm,
                   visibilityChecks:[],
                   enableDisableChecks:[ [XForm.checkInstanceValue,ZaResource.A2_autoCos,"FALSE"]],
                   enableDisableChangeEventSources:[ZaResource.A2_autoCos],
                   dataFetcherMethod:ZaSearch.prototype.dynSelectSearchCoses,
                   toolTipContent:ZaMsg.tt_StartTypingCOSName,
                   onChange:ZaAccount.setCosChanged,
                   dataFetcherClass:ZaSearch,editable:true,getDisplayValue:function(newValue) {
                       if(ZaItem.ID_PATTERN.test(newValue)) {
                           var cos = ZaCos.getCosById(newValue);
                           if(cos)
                               newValue = cos.name;
                       }
                       if (newValue == null) {
                          newValue = "";
                       } else {
                           newValue = "" + newValue;
                       }
                       return newValue;
                   }
               },
               {ref:ZaResource.A2_autoCos, type:_CHECKBOX_,
                   msgName:ZaMsg.NAD_Auto,label:ZaMsg.NAD_Auto,labelLocation:_RIGHT_,subLabel: "",
                   trueValue:"TRUE", falseValue:"FALSE" ,
                   elementChanged: function(elementValue,instanceValue, event) {
                       this.getForm().parent.setDirty(true);
                       if(elementValue=="TRUE") {
                           ZaAccount.setDefaultCos(this.getInstance(), this.getForm().parent._app);
                       }
                       this.getForm().itemChanged(this, elementValue, event);
                   },
                   enableDisableChecks:[ [ZaItem.hasWritePermission,ZaAccount.A_COSId]],
                   visibilityChecks:[]                   
               }
           ]
       }
    );
    
    setupGroup.items.push({ref:ZaResource.A_accountStatus, type:_OSELECT1_, editable:false,
        msgName:ZaMsg.NAD_ResourceStatus,label:ZaMsg.NAD_ResourceStatus,
        labelLocation:_LEFT_, choices:ZaResource.accountStatusChoices});

    setupGroup.items.push({ref:ZaResource.A_zimbraCalResAutoDeclineRecurring, type:_CHECKBOX_,
        msgName:ZaMsg.NAD_DeclineRecurring,label:ZaMsg.NAD_DeclineRecurring,
        trueValue:"TRUE", falseValue:"FALSE"});
        
    setupGroup.items.push({ref:ZaResource.A2_schedulePolicy, type:_OSELECT1_,
            msgName:ZaMsg.NAD_ResType,label:ZaMsg.NAD_SchedulePolicy,
            visibilityChecks:[[ZaItem.hasWritePermission,ZaResource.A_zimbraCalResAutoAcceptDecline],[ZaItem.hasWritePermission,ZaResource.A_zimbraCalResAutoDeclineIfBusy]],
            enableDisableChecks:[[ZaItem.hasReadPermission,ZaResource.A_zimbraCalResAutoAcceptDecline],[ZaItem.hasReadPermission,ZaResource.A_zimbraCalResAutoDeclineIfBusy]],
            labelLocation:_LEFT_, width:"320px", choices:ZaResource.schedulePolicyChoices});

    setupGroup.items.push({ref:ZaResource.A_zimbraCalResMaxNumConflictsAllowed, type:_TEXTFIELD_,
        msgName:ZaMsg.zimbraCalResMaxNumConflictsAllowed, label:ZaMsg.zimbraCalResMaxNumConflictsAllowed,
        enableDisableChecks:[ZaResource.isAutoDeclineEnabled,[XForm.checkInstanceValueNot,ZaResource.A_zimbraCalResAutoDeclineRecurring,"TRUE"]],
        enableDisableChangeEventSources:[ZaResource.A2_schedulePolicy,ZaResource.A_zimbraCalResAutoDeclineRecurring],
        labelLocation:_LEFT_, cssClass:"admin_xform_number_input"});

    setupGroup.items.push({ref:ZaResource.A_zimbraCalResMaxPercentConflictsAllowed, type:_TEXTFIELD_,
        msgName:ZaMsg.zimbraCalResMaxPercentConflictsAllowed, label:ZaMsg.zimbraCalResMaxPercentConflictsAllowed,
        enableDisableChecks:[ZaResource.isAutoDeclineEnabled,[XForm.checkInstanceValueNot,ZaResource.A_zimbraCalResAutoDeclineRecurring,"TRUE"]],
        enableDisableChangeEventSources:[ZaResource.A2_schedulePolicy,ZaResource.A_zimbraCalResAutoDeclineRecurring],
        labelLocation:_LEFT_, cssClass:"admin_xform_number_input"});


	var fwdInvitesGrpr = {type:_GROUP_, id:"resource_form_forwarding_group",
							numCols:2,label:null,colSizes:["275px","425px"], colSpan: "*",
							visibilityChecks:[[ZaItem.hasReadPermission,ZaResource.A_zimbraPrefCalendarForwardInvitesTo]],
							items :[
								{ref:ZaResource.A_zimbraPrefCalendarForwardInvitesTo, type:_DWT_LIST_, height:"100", width:"350px",
									forceUpdate: true, preserveSelection:false, multiselect:true,cssClass: "DLSource", 
									headerList:null,onSelection:ZaResourceXFormView.calFwdAddrSelectionListener,label:ZaMsg.zimbraPrefCalendarForwardInvitesTo,
                                    visibilityChecks:[ZaItem.hasReadPermission],
                                    labelCssClass:"gridGroupBodyLabel",
                                    labelCssStyle:(appNewUI?"text-align:left;border-right:1px solid;":_UNDEFINED_)
								},
								{type:_GROUP_, numCols:6, width:"625px",colSizes:["275","100px","auto","100px","auto","100px"], colSpan:2,
									cssStyle:"margin:10px;padding-bottom:0;",
									items: [
										{type:_CELLSPACER_},
										{type:_DWT_BUTTON_, label:ZaMsg.TBB_Delete,width:"100px",
											onActivate:"ZaResourceXFormView.deleteCalFwdAddrButtonListener.call(this);",
											enableDisableChecks:[ZaResourceXFormView.isDeleteCalFwdAddrEnabled,[ZaItem.hasWritePermission,ZaResource.A_zimbraPrefCalendarForwardInvitesTo]],
											enableDisableChangeEventSources:[ZaResource.A2_calFwdAddr_selection_cache]
										},
										{type:_CELLSPACER_},
										{type:_DWT_BUTTON_, label:ZaMsg.TBB_Edit,width:"100px",
											onActivate:"ZaResourceXFormView.editCalFwdAddrButtonListener.call(this);",
											enableDisableChecks:[ZaResourceXFormView.isEditCalFwdAddrEnabled,[ZaItem.hasWritePermission,ZaResource.A_zimbraPrefCalendarForwardInvitesTo]],
											enableDisableChangeEventSources:[ZaResource.A2_calFwdAddr_selection_cache]
										},
										{type:_CELLSPACER_},
	                                       {type:_DWT_BUTTON_, label:ZaMsg.NAD_Add,width:"100px",
											enableDisableChecks:[[ZaItem.hasWritePermission,ZaResource.A_zimbraPrefCalendarForwardInvitesTo]],                                        
											onActivate:"ZaResourceXFormView.addCalFwdAddrButtonListener.call(this);"
										}
									]
								}]};
	 setupGroup.items.push(fwdInvitesGrpr);							

    var passwordGroup = {type:_TOP_GROUPER_, label:ZaMsg.NAD_PasswordGrouper, id:"resource_form_password_group",
        visibilityChecks:[[XFormItem.prototype.hasAnyRight,[ZaResource.SET_CALRES_PASSWORD_RIGHT, ZaResource.CHANGE_CALRES_PASSWORD_RIGHT]]],
        colSizes:["275px","*"],numCols:2,items:[
        {ref:ZaResource.A_password, type:_SECRET_,
                msgName:ZaMsg.NAD_Password,label:ZaMsg.NAD_Password, labelLocation:_LEFT_,
                cssClass:"admin_xform_name_input", visibilityChecks:[],enableDisableChecks:[]
        },
        {ref:ZaResource.A2_confirmPassword, type:_SECRET_,
            msgName:ZaMsg.NAD_ConfirmPassword,label:ZaMsg.NAD_ConfirmPassword, labelLocation:_LEFT_,
            cssClass:"admin_xform_name_input",visibilityChecks:[],enableDisableChecks:[]
        }
    ]};

    var signatureGroup = {type:_TOP_GROUPER_, label:ZaMsg.NAD_SignatureGrouper, id:"resource_form_signature_group",
        colSizes:["275px", "*"],numCols:2,items:[
        {type:_GROUP_, id:"resource_form_allsignature_group",
		    numCols:2,label:null,colSizes:["275px","*"], colSpan: "*",
            items:[
             {ref:ZaResource.A2_signatureList, type:_DWT_LIST_, height:"100", width:"350px",
            forceUpdate: true, preserveSelection:false, multiselect:true,cssClass: "DLSource",
            headerList:null,label:ZaMsg.NAD_AllSignature,
            labelCssClass:(appNewUI?"gridGroupBodyLabel":"xform_label"),
            labelCssStyle:(appNewUI?"text-align:left;border-right:1px solid;":_UNDEFINED_),
            onSelection:ZaResourceXFormView.SignatureSelectionListener,
            bmolsnr: true,
            getDisplayValue: function(value){
                var form = this.getForm().parent;
                var instance = this.getInstance();
                var tempChoice = ZaSignature.getSignatureChoices(instance[ZaResource.A2_signatureList]);
                form.signatureChoices.setChoices(tempChoice);
                form.signatureChoices.dirtyChoices();
                return value;
            }
        },
        {type:_GROUP_, numCols:6, width:"625px",colSizes:["275","100px","auto","100px","auto","100px"], colSpan:2,
            cssStyle:"margin:10px;padding-bottom:0;",
            items: [
                {type:_CELLSPACER_},
                {type:_DWT_BUTTON_, label:ZaMsg.TBB_Delete,width:"100px",
                    onActivate:"ZaResourceXFormView.deleteSignatureButtonListener.call(this);",
                    enableDisableChecks:[ZaResourceXFormView.isDeleteSignatureEnabled],
                    enableDisableChangeEventSources:[ZaResource.A2_signature_selection_cache]
                },
                {type:_CELLSPACER_},
                {type:_DWT_BUTTON_, label:ZaMsg.TBB_Edit,width:"100px",
                    onActivate:"ZaResourceXFormView.editSignatureButtonListener.call(this);",
                    enableDisableChecks:[ZaResourceXFormView.isEditSignatureEnabled],
                    enableDisableChangeEventSources:[ZaResource.A2_signature_selection_cache]
                },
                {type:_CELLSPACER_},
                   {type:_DWT_BUTTON_, label:ZaMsg.NAD_Add,width:"100px",
                    enableDisableChecks:[[ZaItem.hasWritePermission,ZaResource.A_zimbraPrefCalendarForwardInvitesTo]],
                    onActivate:"ZaResourceXFormView.addSignatureButtonListener.call(this);"
                }
            ]
        }
            ]
        },
        {ref:ZaResource.A_zimbraPrefCalendarAutoAcceptSignatureId, type:_OSELECT1_,
            msgName:ZaMsg.NAD_zimbraPrefCalendarAutoAcceptSignatureId,
            width: "280px",
            label:ZaMsg.NAD_zimbraPrefCalendarAutoAcceptSignatureId, labelLocation:_LEFT_,
            visibilityChecks:[],
            enableDisableChecks:[ZaResourceXFormView.isSignatureSelectionEnabled],
            enableDisableChangeEventSources:[ZaResource.A2_signatureList],
            valueChangeEventSources:[ZaResource.A2_signatureList],
            choices:this.signatureChoices
        },
        {ref:ZaResource.A_zimbraPrefCalendarAutoDeclineSignatureId, type:_OSELECT1_,
            msgName:ZaMsg.NAD_zimbraPrefCalendarAutoDeclineSignatureId,
            width: "280px",
            label:ZaMsg.NAD_zimbraPrefCalendarAutoDeclineSignatureId, labelLocation:_LEFT_,
            visibilityChecks:[],
            enableDisableChecks:[ZaResourceXFormView.isSignatureSelectionEnabled],
            enableDisableChangeEventSources:[ZaResource.A2_signatureList],
            valueChangeEventSources:[ZaResource.A2_signatureList],
            choices:this.signatureChoices
        },
        {ref:ZaResource.A_zimbraPrefCalendarAutoDenySignatureId, type:_OSELECT1_,
            msgName:ZaMsg.NAD_zimbraPrefCalendarAutoDenySignatureId,
            width: "280px",
            label:ZaMsg.NAD_zimbraPrefCalendarAutoDenySignatureId, labelLocation:_LEFT_,
            visibilityChecks:[],
            enableDisableChecks:[ZaResourceXFormView.isSignatureSelectionEnabled],
            enableDisableChangeEventSources:[ZaResource.A2_signatureList],
            valueChangeEventSources:[ZaResource.A2_signatureList],
            choices:this.signatureChoices
        }
        ]
    };

    var notesGroup = {type:_TOP_GROUPER_, label:ZaMsg.NAD_NotesGrouper, id:"resource_form_notes_group",
        colSizes:["275px","*"],numCols:2,items:[
        ZaItem.descriptionXFormItem,
        /*{ref:ZaResource.A_description, type:_INPUT_, width: "300px",
            msgName:ZaMsg.NAD_Description,label:ZaMsg.NAD_Description,
            labelLocation:_LEFT_, cssClass:"admin_xform_name_input"
        },*/
        {ref:ZaResource.A_notes, type:_TEXTAREA_, width: "300px",
            msgName:ZaMsg.NAD_Notes,label:ZaMsg.NAD_Notes,
            labelLocation:_LEFT_
        }
    ]};

    var case1 = {type:_ZATABCASE_, numCols:1,  caseKey:_tab1,
        paddingStyle:(appNewUI? "padding-left:15px;":null), width:(appNewUI? "98%":"100%"), cellpadding:(appNewUI?2:0),
//        height:"400px",  align:_LEFT_, valign:_TOP_,
        items:[nameGroup,setupGroup,passwordGroup,signatureGroup,notesGroup]
    };

    cases.push(case1);


    var defaultWidth = 200 ;
	if(_tab2) {
		this.helpMap[_tab2] = [location.pathname, ZaUtil.HELP_URL, "managing_accounts/managing_resource.htm", "?locid=", AjxEnv.DEFAULT_LOCALE].join("");
        var case2={type:_ZATABCASE_, numCols:1, caseKey:_tab2,
             paddingStyle:(appNewUI? "padding-left:15px;":null), width:(appNewUI? "98%":"100%"), cellpadding:(appNewUI?2:0),
            items: [
                {type:_ZA_TOP_GROUPER_, label:ZaMsg.NAD_ContactInfo, items:[
                    {ref:ZaResource.A_zimbraCalResContactName, type:_TEXTFIELD_, msgName:ZaMsg.NAD_ContactName,label:ZaMsg.NAD_ContactName, labelLocation:_LEFT_, width:defaultWidth},
                    {ref:ZaResource.A_zimbraCalResContactEmail, type:_TEXTFIELD_, msgName:ZaMsg.NAD_ContactEmail,label:ZaMsg.NAD_ContactEmail, labelLocation:_LEFT_, width:defaultWidth},
                    {ref:ZaResource.A_zimbraCalResContactPhone, type:_TEXTFIELD_, msgName:ZaMsg.NAD_ContactPhone,label:ZaMsg.NAD_ContactPhone, labelLocation:_LEFT_, width:defaultWidth},
                    {ref:ZaResource.A_contactInfoAutoComplete, type: _AUTO_COMPLETE_LIST_,
                        matchValue:ZaContactList.matchValue, matchText: ZaContactList.matchText,
                        dataLoaderClass: ZaContactList , dataLoaderMethod: ZaContactList.prototype.getContactList ,
                        compCallback: ZaContactList.prototype._autocompleteCallback,
                        inputFieldElementId: ZaResource.A_zimbraCalResContactName
                    }

                ]},
                {type:_ZA_TOP_GROUPER_, label:ZaMsg.NAD_LocationInfo, colSizes:["275px","*"],numCols:2, items:[
                    {type:_GROUP_, numCols:3,colSizes:["156px","22px","100px"], nowrap:true, msgName:ZaMsg.NAD_LocationDisplayName, label:ZaMsg.NAD_LocationDisplayName, labelLocation:_LEFT_,
                        items: [
                            {ref:ZaResource.A_locationDisplayName, type:_TEXTFIELD_, label:null, cssClass:"admin_xform_name_input", width:defaultWidth,
				enableDisableChecks:[ZaResourceXFormView.isAutoDisplayname],
                                enableDisableChangeEventSources:[ZaResource.A2_autoLocationName],bmolsnr:true
                            },
                            {ref:ZaResource.A2_autoLocationName, type:_CHECKBOX_, msgName:ZaMsg.NAD_Auto,
                                subLabel:"",
                                label:ZaMsg.NAD_Auto,labelLocation:_RIGHT_,
                                trueValue:"TRUE", falseValue:"FALSE",
                                elementChanged: ZaResource.setAutoLocationName
                            }
                    ]},
                    {ref:ZaResource.A_zimbraCalResSite, type:_TEXTFIELD_, msgName:ZaMsg.NAD_Site,label:ZaMsg.NAD_Site,
                        labelLocation:_LEFT_, width:defaultWidth,
                        elementChanged: ZaResource.setAutoLocationName
                    },
                    {ref:ZaResource.A_zimbraCalResBuilding, type:_TEXTFIELD_, msgName:ZaMsg.NAD_Building,
                        label:ZaMsg.NAD_Building, labelLocation:_LEFT_,
                        width:defaultWidth, elementChanged: ZaResource.setAutoLocationName
                    },
                    {ref:ZaResource.A_zimbraCalResFloor, type:_TEXTFIELD_,
                        msgName:ZaMsg.NAD_Floor,label:ZaMsg.NAD_Floor,
                        labelLocation:_LEFT_, width:defaultWidth,
                        elementChanged: ZaResource.setAutoLocationName
                    },
                    {ref:ZaResource.A_zimbraCalResRoom, type:_TEXTFIELD_,
                        msgName:ZaMsg.NAD_Room,label:ZaMsg.NAD_Room,
                        labelLocation:_LEFT_, width:defaultWidth,
                        elementChanged: ZaResource.setAutoLocationName
                    },
                    {ref:ZaResource.A_zimbraCalResCapacity, type:_TEXTFIELD_,
                        msgName:ZaMsg.NAD_Capacity,label:ZaMsg.NAD_Capacity,
                        labelLocation:_LEFT_, width:defaultWidth,
                        visibilityChecks:[ZaResource.isLocation],
                        visibilityChangeEventSources:[ZaResource.A_zimbraCalResType]
                    }
                ]},
                {type:_ZA_TOP_GROUPER_, label:ZaMsg.NAD_Address, items:ZaAccountXFormView.getAddressFormItem()
                 }
            ]
        };
        cases.push(case2);
    }
    
    xFormObject.tableCssStyle="width:100%;";
	xFormObject.items = [
			{type:_GROUP_, cssClass:"ZmSelectedHeaderBg", colSpan:"*", id:"xform_header", 
				items: [
					{type:_GROUP_,	numCols:4,colSizes:["60px","*","80px","*"],items:headerItems}
				]
			},
			{type:_TAB_BAR_,  ref:ZaModel.currentTab,choices:this.tabChoices,cssClass:"ZaTabBar", cssStyle:(appNewUI?"display:none;":""), id:"xform_tabbar"},
			{type:_SWITCH_, align:_LEFT_, valign:_TOP_, items:cases}
	];
};
ZaTabView.XFormModifiers["ZaResourceXFormView"].push(ZaResourceXFormView.myXFormModifier);

ZaResourceXFormView.prototype.getTabChoices =
function() {
    return this.tabChoices;
}
