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
* @class ZaNewResourceXWizard
* @contructor ZaNewResourceXWizard
* @param parent
* @param ZaApp app
* This class defines the New Resource Wazards in XForm
* @author Charles Cao
**/
ZaNewResourceXWizard = function(parent) {
	ZaXWizardDialog.call(this, parent,null, ZaMsg.NCD_NewResTitle, "700px", "300px","ZaNewResourceXWizard", null, ZaId.DLG_NEW_RES);
	


	this.TAB_INDEX = 0;
	ZaNewResourceXWizard.step1 = ++this.TAB_INDEX;
	ZaNewResourceXWizard.step2 = ++this.TAB_INDEX;
	if(!ZaResource.accountStatusChoices) {
		ZaResource.accountStatusChoices = [
			{value:ZaResource.ACCOUNT_STATUS_ACTIVE, label:ZaResource.getAccountStatusLabel(ZaResource.ACCOUNT_STATUS_ACTIVE)}, 
			{value:ZaResource.ACCOUNT_STATUS_CLOSED, label:ZaResource.getAccountStatusLabel(ZaResource.ACCOUNT_STATUS_CLOSED)}
			//{value:ZaResource.ACCOUNT_STATUS_LOCKED, label: ZaResource.getAccountStatusLabel(ZaResource.ACCOUNT_STATUS_LOCKED)},
			//{value:ZaResource.ACCOUNT_STATUS_MAINTENANCE, label:ZaResource.getAccountStatusLabel(ZaResource.ACCOUNT_STATUS_MAINTENANCE)}
		];		
	}
	this.stepChoices = [
		{label:ZaMsg.TABT_ResourceProperties, value:ZaNewResourceXWizard.step1},
		{label:ZaMsg.TABT_ResLocationContact, value:ZaNewResourceXWizard.step2}
	];
	this._lastStep = this.stepChoices.length;	
	this.initForm(ZaResource.myXModel,this.getMyXForm());	
   
	this._localXForm.setController(ZaApp.getInstance());	
	this._localXForm.addListener(DwtEvent.XFORMS_FORM_DIRTY_CHANGE, new AjxListener(this, ZaNewResourceXWizard.prototype.handleXFormChange));
	this._localXForm.addListener(DwtEvent.XFORMS_VALUE_ERROR, new AjxListener(this, ZaNewResourceXWizard.prototype.handleXFormChange));	
	this._helpURL = ZaNewResourceXWizard.helpURL;
	
	this._domains = {} ;
}


ZaNewResourceXWizard.prototype = new ZaXWizardDialog;
ZaNewResourceXWizard.prototype.constructor = ZaNewResourceXWizard;
ZaXDialog.XFormModifiers["ZaNewResourceXWizard"] = new Array();
ZaNewResourceXWizard.helpURL = location.pathname + ZaUtil.HELP_URL + "managing_accounts/managing_resource.htm?locid="+AjxEnv.DEFAULT_LOCALE;

ZaNewResourceXWizard.prototype.handleXFormChange = 
function () {
	//Enable/disable the finish button
	if(this._localXForm.hasErrors()) {
		this._button[DwtWizardDialog.FINISH_BUTTON].setEnabled(false);
	} else {
		if(this._containedObject.attrs[ZaResource.A_displayname] && this._containedObject[ZaResource.A_name].indexOf("@") > 0)
			this._button[DwtWizardDialog.FINISH_BUTTON].setEnabled(true);
	}
}

/**
* Overwritten methods that control wizard's flow (open, go next,go previous, finish)
**/
ZaNewResourceXWizard.prototype.popup = 
function (loc) {
	ZaXWizardDialog.prototype.popup.call(this, loc);
	this._button[DwtWizardDialog.NEXT_BUTTON].setEnabled(true);
	this._button[DwtWizardDialog.FINISH_BUTTON].setEnabled(false);
	this._button[DwtWizardDialog.PREV_BUTTON].setEnabled(false);	
}

ZaNewResourceXWizard.prototype.createDomainAndAccount = function(domainName) {
	try {
		var newDomain = new ZaDomain();
		newDomain.name=domainName;
		newDomain.attrs[ZaDomain.A_domainName] = domainName;
		var domain = ZaItem.create(newDomain,ZaDomain,"ZaDomain");
		if(domain != null) {
			ZaApp.getInstance().getCurrentController().closeCnfrmDelDlg();
			this.finishWizard();
		}
	} catch(ex) {
		ZaApp.getInstance().getCurrentController()._handleException(ex, "ZaNewResourceXWizard.prototype.createDomainAndAccount", null, false);	
	}
}

ZaNewResourceXWizard.prototype.finishWizard = 
function() {
	try {		
		if(!ZaResource.checkValues(this._containedObject)) {
			return false;
		}
		var resource = ZaItem.create(this._containedObject, ZaResource, "ZaResource");
		if(resource != null) {
			ZaApp.getInstance().getResourceController().fireCreationEvent(resource);
			this.popdown();
		}
	} catch (ex) {
		switch(ex.code) {		
			case ZmCsfeException.ACCT_EXISTS:
				ZaApp.getInstance().getCurrentController().popupErrorDialog(ZaMsg.ERROR_ACCOUNT_EXISTS);
			break;
			case ZmCsfeException.ACCT_INVALID_PASSWORD:
				ZaApp.getInstance().getCurrentController().popupErrorDialog(ZaMsg.ERROR_PASSWORD_INVALID, ex);
				ZaApp.getInstance().getAppCtxt().getErrorDialog().showDetail(true);
			break;
			case ZmCsfeException.NO_SUCH_COS:
				ZaApp.getInstance().getCurrentController().popupErrorDialog(AjxMessageFormat.format(ZaMsg.ERROR_NO_SUCH_COS,[this._containedObject.attrs[ZaAccount.A_COSId]]), ex);
		    break;			
			case ZmCsfeException.NO_SUCH_DOMAIN:
				ZaApp.getInstance().dialogs["confirmMessageDialog2"].setMessage(AjxMessageFormat.format(ZaMsg.CreateDomain_q,[ZaAccount.getDomain(this._containedObject.name)]), DwtMessageDialog.WARNING_STYLE);
				ZaApp.getInstance().dialogs["confirmMessageDialog2"].registerCallback(DwtDialog.YES_BUTTON, this.createDomainAndAccount, this, [ZaAccount.getDomain(this._containedObject.name)]);		
				ZaApp.getInstance().dialogs["confirmMessageDialog2"].registerCallback(DwtDialog.NO_BUTTON, ZaController.prototype.closeCnfrmDelDlg, ZaApp.getInstance().getCurrentController(), null);				
				ZaApp.getInstance().dialogs["confirmMessageDialog2"].popup();  				
			break;
			default:
				ZaApp.getInstance().getCurrentController()._handleException(ex, "ZaNewResourceXWizard.prototype.finishWizard", null, false);
			break;		
		}
	}
}

ZaNewResourceXWizard.prototype.goNext = 
function() {
	if (this._containedObject[ZaModel.currentStep] == 1) {
		//check if passwords match
		if(this._containedObject.attrs[ZaResource.A_password]) {
			if(this._containedObject.attrs[ZaResource.A_password] != this._containedObject[ZaResource.A2_confirmPassword]) {
				ZaApp.getInstance().getCurrentController().popupErrorDialog(ZaMsg.ERROR_PASSWORD_MISMATCH);
				return false;
			}
		}
		this._button[DwtWizardDialog.PREV_BUTTON].setEnabled(true);

		//check if account exists
		var params = { 	query: ["(|(uid=",this._containedObject[ZaResource.A_name],")(cn=",this._containedObject[ZaResource.A_name],")(sn=",this._containedObject[ZaResource.A_name],")(gn=",this._containedObject[ZaResource.A_name],")(mail=",this._containedObject[ZaResource.A_name],")(zimbraMailDeliveryAddress=",this._containedObject[ZaResource.A_name],"))"].join(""),
						limit : 2,
						applyCos: 0,
						controller: ZaApp.getInstance().getCurrentController(),
						types: [ZaSearch.DLS,ZaSearch.ALIASES,ZaSearch.ACCOUNTS,ZaSearch.RESOURCES]
					 };
					
		var resp = ZaSearch.searchDirectory(params).Body.SearchDirectoryResponse;		
		var list = new ZaItemList();	
		list.loadFromJS(resp);	
		if(list.size() > 0) {
			var acc = list.getArray()[0];
			if(acc.type==ZaItem.ALIAS) {
				ZaApp.getInstance().getCurrentController().popupErrorDialog(ZaMsg.ERROR_aliasWithThisNameExists);
			} else if (acc.type==ZaItem.RESOURCE) {
				ZaApp.getInstance().getCurrentController().popupErrorDialog(ZaMsg.ERROR_resourceWithThisNameExists);
			} else if (acc.type==ZaItem.DL) {
				ZaApp.getInstance().getCurrentController().popupErrorDialog(ZaMsg.ERROR_dlWithThisNameExists);
			} else {
				ZaApp.getInstance().getCurrentController().popupErrorDialog(ZaMsg.ERROR_accountWithThisNameExists);
			}
			return false;
		} 
		this._button[DwtWizardDialog.PREV_BUTTON].setEnabled(true);		
	} 	
	
	this.goPage(this._containedObject[ZaModel.currentStep] + 1);
	if(this._containedObject[ZaModel.currentStep] == this._lastStep) {
		this._button[DwtWizardDialog.NEXT_BUTTON].setEnabled(false);
	}	
}

ZaNewResourceXWizard.prototype.goPrev = 
function() {
	if (this._containedObject[ZaModel.currentStep] == 2) {
		this._button[DwtWizardDialog.PREV_BUTTON].setEnabled(false);
	}
	
	this._button[DwtWizardDialog.NEXT_BUTTON].setEnabled(true);
	
	this.goPage(this._containedObject[ZaModel.currentStep] - 1);
}

/**
* @method setObject sets the object contained in the view
* @param entry - ZaResource object to display
**/
ZaNewResourceXWizard.prototype.setObject =
function(entry) {
	this._containedObject = new Object();
	this._containedObject.attrs = new Object();

	for (var a in entry.attrs) {
		this._containedObject.attrs[a] = entry.attrs[a];
	}
	this._containedObject.name = "";
	if(entry.rights)
		this._containedObject.rights = entry.rights;

	if(entry.setAttrs)
		this._containedObject.setAttrs = entry.setAttrs;
	
	if(entry.getAttrs)
		this._containedObject.getAttrs = entry.getAttrs;
		
	if(entry._defaultValues)
		this._containedObject._defaultValues = entry._defaultValues;

	this._containedObject.id = null;

	//set the default value of resource type and schedule policy
	this._containedObject.attrs[ZaResource.A_zimbraCalResType] = ZaResource.RESOURCE_TYPE_LOCATION;
	this._containedObject[ZaResource.A2_schedulePolicy] = ZaResource.SCHEDULE_POLICY_TT;
	this._containedObject.attrs[ZaResource.A_accountStatus] = ZaResource.ACCOUNT_STATUS_ACTIVE;
	this._containedObject[ZaResource.A2_autodisplayname] = "TRUE";
	this._containedObject[ZaResource.A2_autoMailServer] = "TRUE";
	this._containedObject[ZaResource.A2_autoCos] = "TRUE";
	this._containedObject[ZaResource.A2_autoLocationName] = "TRUE";	
	this._containedObject[ZaResource.A2_confirmPassword] = null;
	this._containedObject[ZaModel.currentStep] = 1;
	var domainName;
	
	if(!domainName) {
		//find out what is the default domain
		try {
			domainName = ZaApp.getInstance().getGlobalConfig().attrs[ZaGlobalConfig.A_zimbraDefaultDomainName];
		} catch (ex) {
			if(ex.code != ZmCsfeException.SVC_PERM_DENIED) {
				throw (ex);
			}
		}
	}

 
	if(!domainName) {
		domainName =  ZaSettings.myDomainName;
	}
	this._containedObject[ZaResource.A_name] = "@" + domainName;
	this._localXForm.setInstance(this._containedObject);
}

ZaNewResourceXWizard.onCOSChanged = 
function(value, event, form) {
	if(ZaItem.ID_PATTERN.test(value))  {
		form.getInstance()._defaultValues = ZaCos.getCosById(value, form.parent._app);
		this.setInstanceValue(value);
	} else {
		form.getInstance()._defaultValues = ZaCos.getCosByName(value, form.parent._app);
		if(form.getInstance().cos) {
			//value = form.getInstance()._defaultValues.id;
			value = form.getInstance()._defaultValues.id;
		} 
	}
	this.setInstanceValue(value);
    form.parent._isCosChanged = true ;

    //if cos is changed,  update the account type information
 
    return value;
}

ZaNewResourceXWizard.myXFormModifier = function(xFormObject) {
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

	var emptyAlias = "@" + domainName;

	var cases = new Array();
	this.cosChoices = new XFormChoices([], XFormChoices.OBJECT_LIST, "id", "name");
	var nameGroup = {type:_ZAWIZ_TOP_GROUPER_, label:ZaMsg.NAD_ResourceNameGrouper, id:"resource_wiz_name_group",numCols:2,
		items:[
			{ref:ZaResource.A_displayname, type:_TEXTFIELD_, msgName:ZaMsg.NAD_ResourceName,
				label:ZaMsg.NAD_ResourceName, labelLocation:_LEFT_, 
				elementChanged: function(elementValue,instanceValue, event) {
					//auto fill the account name when autodisplayname is true
					if(this.getInstance()[ZaResource.A2_autodisplayname]=="TRUE") {
						try {
							
							var oldAccName = this.getInstanceValue(ZaResource.A_name);
							var regEx = /[^a-zA-Z0-9_\-\.]/g ;
							var newName = elementValue.replace(regEx, "") + oldAccName.substring(oldAccName.indexOf("@")) ;	
							this.getModel().setInstanceValue(this.getInstance(),ZaResource.A_name,newName);
							this.getModel().setInstanceValue(this.getInstance(),ZaResource.A2_autodisplayname,"TRUE");
						} catch (ex) {
							ZaApp.getInstance().getCurrentController()._handleException(ex, "XForm." + ZaResource.A_displayname + ".elementChanged", null, false);
						}
					}
					this.getForm().itemChanged(this, elementValue, event);
				}
			},			
/*			{ref:ZaResource.A_zimbraCalResType, type:_OSELECT1_, msgName:ZaMsg.NAD_ResType,label:ZaMsg.NAD_ResType, 
				labelLocation:_LEFT_, choices:ZaResource.resTypeChoices
			},	*/	
			{ref:ZaResource.A_name, type:_EMAILADDR_, msgName:ZaMsg.NAD_ResAccountName,label:ZaMsg.NAD_ResAccountName, 
				labelLocation:_LEFT_,id:"resource_email_addr", bmolsnr: true, 
				onChange: function(value, event, form) {
					//disable the autodisplayname whenever user does some action on the account name
					this.getModel().setInstanceValue(this.getInstance(),ZaResource.A2_autodisplayname,"FALSE");							
					this.setInstanceValue(value);	
				},visibilityChecks:[],enableDisableChecks:[]
			}				
		]
	}
	
	var setupGroup = {type:_ZAWIZ_TOP_GROUPER_, label:ZaMsg.NAD_ResourceSetupGrouper, id:"resource_wiz_name_group",numCols:2,
		items:[
			{ref:ZaResource.A_zimbraCalResType, type:_OSELECT1_, msgName:ZaMsg.NAD_ResType,label:ZaMsg.NAD_ResType, 
				labelLocation:_LEFT_, choices:ZaResource.resTypeChoices,visibilityChecks:[],enableDisableChecks:[]
			}		
		]
	}	


	setupGroup.items.push(
		{type:_GROUP_, numCols:3, nowrap:true, label:ZaMsg.NAD_ClassOfService, labelLocation:_LEFT_,
			visibilityChecks:[[ZaItem.hasWritePermission,ZaAccount.A_COSId]],
			items: [
				{ref:ZaResource.A_COSId, type:_DYNSELECT_,label: null, 
					inputPreProcessor:ZaAccountXFormView.preProcessCOS,
					visibilityChecks:[],
					emptyText:ZaMsg.enterSearchTerm,
					enableDisableChecks:[[XForm.checkInstanceValue,ZaResource.A2_autoCos,"FALSE"]],
					enableDisableChangeEventSources:[ZaResource.A2_autoCos],
					dataFetcherMethod:ZaSearch.prototype.dynSelectSearchCoses,
					toolTipContent:ZaMsg.tt_StartTypingCOSName,
					onChange:ZaAccount.setCosChanged,
					choices:this.cosChoices,
					dataFetcherClass:ZaSearch,
					editable:true,
					getDisplayValue:function(newValue) {
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
					msgName:ZaMsg.NAD_Auto,label:ZaMsg.NAD_Auto,labelLocation:_RIGHT_,
					trueValue:"TRUE", falseValue:"FALSE" ,
					elementChanged: function(elementValue,instanceValue, event) {
						if(elementValue=="TRUE") {
							ZaAccount.setDefaultCos(this.getInstance(), this.getForm().parent._app);	
						}
						this.getForm().itemChanged(this, elementValue, event);
					},
					visibilityChecks:[],enableDisableChecks:[ [ZaItem.hasWritePermission,ZaAccount.A_COSId]]
				}
			]
		}
	);

	
	setupGroup.items.push({ref:ZaResource.A_accountStatus, type:_OSELECT1_, editable:false, msgName:ZaMsg.NAD_ResourceStatus,
					  label:ZaMsg.NAD_ResourceStatus, labelLocation:_LEFT_, choices:ZaResource.accountStatusChoices,
					  visibilityChecks:[[ZaItem.hasWritePermission,ZaResource.A_accountStatus]],
					  enableDisableChecks:[]
					  });
		
	setupGroup.items.push({ref:ZaResource.A_zimbraCalResAutoDeclineRecurring, type:_CHECKBOX_, 
						msgName:ZaMsg.NAD_DeclineRecurring,label:ZaMsg.NAD_DeclineRecurring, 
						labelCssClass:"xform_label", align:_LEFT_,labelLocation:_LEFT_,trueValue:"TRUE", falseValue:"FALSE"});

	setupGroup.items.push({ref:ZaResource.A2_schedulePolicy, type:_OSELECT1_, msgName:ZaMsg.NAD_ResType,
						visibilityChecks:[[ZaItem.hasWritePermission,ZaResource.A_zimbraCalResAutoAcceptDecline],[ZaItem.hasWritePermission,ZaResource.A_zimbraCalResAutoDeclineIfBusy]],
						enableDisableChecks:[],
						label:ZaMsg.NAD_SchedulePolicy, labelLocation:_LEFT_, width: "300px", 
						choices:ZaResource.schedulePolicyChoices});	
						
	setupGroup.items.push({ref:ZaResource.A_zimbraCalResMaxNumConflictsAllowed, type:_TEXTFIELD_,
		msgName:ZaMsg.zimbraCalResMaxNumConflictsAllowed, label:ZaMsg.zimbraCalResMaxNumConflictsAllowed,
		enableDisableChecks:[ZaResource.isAutoDeclineEnabled,[XForm.checkInstanceValueNot,ZaResource.A_zimbraCalResAutoDeclineRecurring,"TRUE"]],
		enableDisableChangeEventSources:[ZaResource.A_zimbraCalResAutoDeclineRecurring,ZaResource.A2_schedulePolicy],			
		labelLocation:_LEFT_, cssClass:"admin_xform_number_input"});		
		
	setupGroup.items.push({ref:ZaResource.A_zimbraCalResMaxPercentConflictsAllowed, type:_TEXTFIELD_,
		msgName:ZaMsg.zimbraCalResMaxPercentConflictsAllowed, label:ZaMsg.zimbraCalResMaxPercentConflictsAllowed,
		enableDisableChecks:[ZaResource.isAutoDeclineEnabled,[XForm.checkInstanceValueNot,ZaResource.A_zimbraCalResAutoDeclineRecurring,"TRUE"]],
		enableDisableChangeEventSources:[ZaResource.A_zimbraCalResAutoDeclineRecurring,ZaResource.A2_schedulePolicy],			
		labelLocation:_LEFT_, cssClass:"admin_xform_number_input"});	
								
	setupGroup.items.push({type:_GROUP_, numCols:3, nowrap:true, label:ZaMsg.NAD_MailServer, labelLocation:_LEFT_,
						visibilityChecks:[[ZaItem.hasWritePermission,ZaAccount.A_mailHost]],
						items: [
							{ ref: ZaResource.A_mailHost, type: _OSELECT1_, label: null, editable:false, 
								choices: ZaApp.getInstance().getServerListChoices(), 
								visibilityChecks:[],
								enableDisableChecks:[ZaAccount.isAutoMailServer],
								enableDisableChangeEventSources:[ZaResource.A2_autoMailServer]									
						  	},
							{ref:ZaResource.A2_autoMailServer, type:_CHECKBOX_, msgName:ZaMsg.NAD_Auto,
								visibilityChecks:[],enableDisableChecks:[],
								label:ZaMsg.NAD_Auto,labelLocation:_RIGHT_,trueValue:"TRUE", falseValue:"FALSE"}
						]
					}); 

	setupGroup.items.push({ref:ZaResource.A_zimbraPrefCalendarForwardInvitesTo, type:_REPEAT_,
							label:ZaMsg.zimbraPrefCalendarForwardInvitesTo, labelLocation:_LEFT_,labelCssClass:"xform_label",
							repeatInstance:emptyAlias, 
							showAddButton:true, showRemoveButton:true, 
							addButtonLabel:ZaMsg.NAD_AddAddress, 
							showAddOnNextRow:true,
							removeButtonLabel:ZaMsg.NAD_RemoveAddress,
							nowrap:false,labelWrap:true,
							items: [
								{ref:".", type:_TEXTFIELD_, label:null, width:250,visibilityChecks:[], enableDisableChecks:[]}
							]
						});
						
	var passwordGroup = {type:_ZAWIZ_TOP_GROUPER_, label:ZaMsg.NAD_PasswordGrouper,id:"account_wiz_password_group", 
		numCols:2,visibilityChecks:[],
		items:[
			{ref:ZaResource.A_password, type:_SECRET_, msgName:ZaMsg.NAD_Password,label:ZaMsg.NAD_Password, visibilityChecks:[],enableDisableChecks:[], labelLocation:_LEFT_, cssClass:"admin_xform_name_input"},
			{ref:ZaResource.A2_confirmPassword, type:_SECRET_, msgName:ZaMsg.NAD_ConfirmPassword,label:ZaMsg.NAD_ConfirmPassword, visibilityChecks:[],enableDisableChecks:[], labelLocation:_LEFT_, cssClass:"admin_xform_name_input"}
		]
		
	}

	var notesGroup = {type:_ZAWIZ_TOP_GROUPER_, label:ZaMsg.NAD_NotesGrouper, id:"account_wiz_notes_group",
		numCols:2,
	 	items:[
			{ref:ZaResource.A_description, type:_INPUT_, msgName:ZaMsg.NAD_Description,
					label:ZaMsg.NAD_Description, labelLocation:_LEFT_, width: "300px", cssClass:"admin_xform_name_input"},
			{ref:ZaResource.A_notes, type:_TEXTAREA_, msgName:ZaMsg.NAD_Notes,label:ZaMsg.NAD_Notes, labelLocation:_LEFT_}
		]
	};
	var case1 = {type:_CASE_, numCols:1, caseKey:ZaNewResourceXWizard.step1, align:_LEFT_, valign:_TOP_,
		items:[nameGroup,setupGroup,passwordGroup,notesGroup]
	
	};	
	

	cases.push(case1);

		
	var defaultWidth = 250;	
	var case2={type:_CASE_, numCols:1,  caseKey:ZaNewResourceXWizard.step2,
					items: [
					   {type:_ZAWIZGROUP_, 
							items:[
								{ref:ZaResource.A_zimbraCalResContactName, type:_TEXTFIELD_, msgName:ZaMsg.NAD_ContactName,
									label:ZaMsg.NAD_ContactName, labelLocation:_LEFT_, width:defaultWidth},
								{ref:ZaResource.A_zimbraCalResContactEmail, type:_TEXTFIELD_, msgName:ZaMsg.NAD_ContactEmail,
									label:ZaMsg.NAD_ContactEmail, labelLocation:_LEFT_, width:defaultWidth},
								{ref:ZaResource.A_zimbraCalResContactPhone, type:_TEXTFIELD_, msgName:ZaMsg.NAD_ContactPhone,
									label:ZaMsg.NAD_ContactPhone, labelLocation:_LEFT_, width:defaultWidth}
							]
						},
						{type:_ZAWIZGROUP_, colSizes:["200px","300px"],
							items:[
								{type:_GROUP_, numCols:3, nowrap:true, width:200, msgName:ZaMsg.NAD_LocationDisplayName,label:ZaMsg.NAD_LocationDisplayName, labelLocation:_LEFT_, 
									items: [
										{ref:ZaResource.A_locationDisplayName, type:_TEXTFIELD_, 
											label:null,	width:defaultWidth,  
											enableDisableChecks:[ [XForm.checkInstanceValue,ZaAccount.A2_autodisplayname,"FALSE"] ],
											enableDisableChangeEventSources:[ZaAccount.A2_autodisplayname]
										},
										{ref:ZaResource.A2_autoLocationName, type:_CHECKBOX_, msgName:ZaMsg.NAD_Auto,label:ZaMsg.NAD_Auto,labelLocation:_RIGHT_,trueValue:"TRUE", falseValue:"FALSE",
											elementChanged: ZaResource.setAutoLocationName
										}
									]
								},								
								{ref:ZaResource.A_zimbraCalResSite, type:_TEXTFIELD_, msgName:ZaMsg.NAD_Site,label:ZaMsg.NAD_Site, 
										labelLocation:_LEFT_, width:defaultWidth, elementChanged: ZaResource.setAutoLocationName},
								{ref:ZaResource.A_zimbraCalResBuilding, type:_TEXTFIELD_, msgName:ZaMsg.NAD_Building,label:ZaMsg.NAD_Building, 
										labelLocation:_LEFT_, width:defaultWidth, elementChanged: ZaResource.setAutoLocationName},						
								{ref:ZaResource.A_zimbraCalResFloor, type:_TEXTFIELD_, msgName:ZaMsg.NAD_Floor,label:ZaMsg.NAD_Floor, 
										labelLocation:_LEFT_, width:defaultWidth, elementChanged: ZaResource.setAutoLocationName},						
								{ref:ZaResource.A_zimbraCalResRoom, type:_TEXTFIELD_, msgName:ZaMsg.NAD_Room,label:ZaMsg.NAD_Room, 
										labelLocation:_LEFT_, width:defaultWidth, elementChanged: ZaResource.setAutoLocationName},
								{ref:ZaResource.A_zimbraCalResCapacity, type:_TEXTFIELD_, msgName:ZaMsg.NAD_Capacity,label:ZaMsg.NAD_Capacity, 
									labelLocation:_LEFT_, width:defaultWidth,
									visibilityChecks:[ZaResourceXFormView.isLocation],
									visibilityChangeEventSources:[ZaResource.A_zimbraCalResType]
								}
							]
						},											
						{type:_ZAWIZGROUP_, 
							items:[
								{ref:ZaResource.A_street, type:_TEXTFIELD_, msgName:ZaMsg.NAD_Street,label:ZaMsg.NAD_Street, 
									labelLocation:_LEFT_, width:defaultWidth},
								{ref:ZaResource.A_city, type:_TEXTFIELD_, msgName:ZaMsg.NAD_city ,label:ZaMsg.NAD_city, 
									labelLocation:_LEFT_, width:defaultWidth},
								{ref:ZaResource.A_state, type:_TEXTFIELD_, msgName:ZaMsg.NAD_state ,label:ZaMsg.NAD_state, 
									labelLocation:_LEFT_, width:defaultWidth},
								{ref:ZaResource.A_country, type:_TEXTFIELD_, msgName:ZaMsg.country ,label:ZaMsg.NAD_country, 
									labelLocation:_LEFT_, width:defaultWidth},
								{ref:ZaResource.A_zip, type:_TEXTFIELD_, msgName:ZaMsg.zip ,label:ZaMsg.NAD_zip, 
									labelLocation:_LEFT_, width:defaultWidth}
							]
						}
					]
				};
	cases.push(case2);

	xFormObject.items = [
			{type:_OUTPUT_, colSpan:2, align:_CENTER_, valign:_TOP_, ref:ZaModel.currentStep, choices:this.stepChoices,valueChangeEventSources:[ZaModel.currentStep]},
			{type:_SEPARATOR_, align:_CENTER_, valign:_TOP_},
			{type:_SPACER_,  align:_CENTER_, valign:_TOP_},
			{type:_SWITCH_, width:650, align:_LEFT_, valign:_TOP_, items:cases}
		];
};
ZaXDialog.XFormModifiers["ZaNewResourceXWizard"].push(ZaNewResourceXWizard.myXFormModifier);
