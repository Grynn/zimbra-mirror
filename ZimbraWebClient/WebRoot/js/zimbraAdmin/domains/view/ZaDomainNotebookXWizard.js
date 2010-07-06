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
* @class ZaDomainNotebookXWizard
* @contructor ZaDomainNotebookXWizard
* @author Greg Solovyev
* @param parent
* param app
**/
ZaDomainNotebookXWizard = function(parent, w, h) {
	ZaXWizardDialog.call(this, parent,null, ZaMsg.NDW_Title, "550px", "300px","ZaDomainNotebookXWizard");

	this.stepChoices = [
		{label:ZaMsg.TABT_GeneralPage, value:1},
		{label:ZaMsg.TABT_Domain_GlobalAcl, value:2},			
		{label:ZaMsg.TABT_Domain_AdvancedAcl, value:3},					
		{label:ZaMsg.TABT_NotebookConfigComplete, value:4}		
	];
	this.TAB_INDEX = 0;	
	this.initForm(ZaDomain.myXModel,this.getMyXForm());		
	this._localXForm.addListener(DwtEvent.XFORMS_FORM_DIRTY_CHANGE, new AjxListener(this, ZaDomainNotebookXWizard.prototype.handleXFormChange));
	this._localXForm.addListener(DwtEvent.XFORMS_VALUE_ERROR, new AjxListener(this, ZaDomainNotebookXWizard.prototype.handleXFormChange));	
	this.lastErrorStep=0;
	this._helpURL = location.pathname + ZaUtil.HELP_URL + "documents/managing_domain_documents.htm?locid="+AjxEnv.DEFAULT_LOCALE;
}

ZaDomainNotebookXWizard.prototype = new ZaXWizardDialog;
ZaDomainNotebookXWizard.prototype.constructor = ZaDomainNotebookXWizard;
ZaXDialog.XFormModifiers["ZaDomainNotebookXWizard"] = new Array();


ZaDomainNotebookXWizard.prototype.handleXFormChange = 
function () {
	if(this._localXForm.hasErrors()) {
		if(this.lastErrorStep < this._containedObject[ZaModel.currentStep])
			this.lastErrorStep=this._containedObject[ZaModel.currentStep];
	} else {
		this.lastErrorStep=0;
	}
	this.changeButtonStateForStep(this._containedObject[ZaModel.currentStep]);	
}

ZaDomainNotebookXWizard.prototype.goNext = 
function() {
	this.goPage(this._containedObject[ZaModel.currentStep] + 1);
	this.changeButtonStateForStep(this._containedObject[ZaModel.currentStep]);	
}

ZaDomainNotebookXWizard.prototype.goPrev =
function () {
	this.changeButtonStateForStep(this._containedObject[ZaModel.currentStep]-1);
	this.goPage(this._containedObject[ZaModel.currentStep]-1);
}

ZaDomainNotebookXWizard.prototype.changeButtonStateForStep = 
function(stepNum) {
	if(this.lastErrorStep == stepNum) {
		this._button[DwtWizardDialog.FINISH_BUTTON].setEnabled(false);
		this._button[DwtWizardDialog.NEXT_BUTTON].setEnabled(false);
		if(stepNum>1)
			this._button[DwtWizardDialog.PREV_BUTTON].setEnabled(true);
		else
			this._button[DwtWizardDialog.PREV_BUTTON].setEnabled(false);
	} else {
		if(stepNum == 1) {
			this._button[DwtWizardDialog.NEXT_BUTTON].setEnabled(true);
			this._button[DwtWizardDialog.PREV_BUTTON].setEnabled(false);
			this._button[DwtWizardDialog.FINISH_BUTTON].setEnabled(false);
		} else if(stepNum == 4) {
			this._button[DwtWizardDialog.PREV_BUTTON].setEnabled(true);
			this._button[DwtWizardDialog.NEXT_BUTTON].setEnabled(false);
			this._button[DwtWizardDialog.FINISH_BUTTON].setEnabled(true);
		} else {
			this._button[DwtWizardDialog.PREV_BUTTON].setEnabled(true);
			this._button[DwtWizardDialog.NEXT_BUTTON].setEnabled(true);
		}
	}
}

ZaDomainNotebookXWizard.prototype.setObject = function (entry) {
	this._containedObject = new Object();
	this._containedObject.attrs = new Object();

	for (var a in entry.attrs) {
		if(entry.attrs[a] instanceof Array) {
			this._containedObject.attrs[a] = new Array();
			var cnt = entry.attrs[a].length;
			for(var ix = 0; ix < cnt; ix++) {
				this._containedObject.attrs[a][ix]=entry.attrs[a][ix];
			}
		} else {
			this._containedObject.attrs[a] = entry.attrs[a];
		}
	}
	this._containedObject[ZaDomain.A_AuthUseBindPassword] = entry[ZaDomain.A_AuthUseBindPassword];
	
	if(!entry[ZaModel.currentTab])
		this._containedObject[ZaModel.currentTab] = "1";
	else
		this._containedObject[ZaModel.currentTab] = entry[ZaModel.currentTab];

	this._containedObject[ZaDomain.A_NotebookTemplateFolder]=entry[ZaDomain.A_NotebookTemplateFolder];
	this._containedObject[ZaDomain.A_NotebookTemplateDir]=entry[ZaDomain.A_NotebookTemplateDir];	


	this._containedObject.notebookAcls = {};

	if(entry.notebookAcls) {
		for(var gt in entry.notebookAcls) {
			if(!(entry.notebookAcls[gt] instanceof Array)) {
				this._containedObject.notebookAcls[gt] = {r:0,w:0,i:0,d:0,a:0,x:0};
				for (var a in entry.notebookAcls[gt]) {
					this._containedObject.notebookAcls[gt][a] = entry.notebookAcls[gt][a];
				}
			} else {
				this._containedObject.notebookAcls[gt] = [];
				var cnt = entry.notebookAcls[gt].length;
				for(var i = 0; i < cnt; i++) {
					var aclObj = entry.notebookAcls[gt][i];
					var _newAclObj = {};
					_newAclObj.name = aclObj.name;
					_newAclObj.acl = {r:0,w:0,i:0,d:0,a:0,x:0};
					for (var a in aclObj.acl) {
						_newAclObj.acl[a] = aclObj.acl[a];
					}					
					this._containedObject.notebookAcls[gt][i] = _newAclObj;
				}
			}
		}
	}	
			
	if(!this._containedObject[ZaDomain.A_zimbraNotebookAccount] && this._containedObject.attrs[ZaDomain.A_domainName])
		this._containedObject[ZaDomain.A_zimbraNotebookAccount] = ZaDomain.DEF_WIKI_ACC + "@" + this._containedObject.attrs[ZaDomain.A_domainName];

	
	this._containedObject[ZaModel.currentStep] = 1;
	this._localXForm.setInstance(this._containedObject);
	this.setTitle(ZaMsg.NDW_Title + " (" + entry.name + ")");	
}

ZaDomainNotebookXWizard.prototype.closeMe = 
function() {
	this.popdown();	
}

ZaDomainNotebookXWizard.myXFormModifier = function(xFormObject) {
	var _tab1 = ++this.TAB_INDEX;
	var _tab2 = ++this.TAB_INDEX;
	var _tab3 = ++this.TAB_INDEX;	
	var _tab4 = ++this.TAB_INDEX;
	
	xFormObject.items = [
		{type:_OUTPUT_, colSpan:2, align:_CENTER_, valign:_TOP_, ref:ZaModel.currentStep, choices:this.stepChoices,valueChangeEventSources:[ZaModel.currentStep]},
		{type:_SEPARATOR_, align:_CENTER_, valign:_TOP_},
		{type:_SPACER_,  align:_CENTER_, valign:_TOP_},		
		{type: _SWITCH_,width:500,
			items: [
				{type:_CASE_,caseKey:_tab1,
					items: [
						{ref:ZaDomain.A_zimbraNotebookAccount, type:_TEXTFIELD_, label:ZaMsg.Domain_NotebookAccountName, labelLocation:_LEFT_,visibilityChecks:[],enableDisableChecks:[]},						
						{ref:ZaDomain.A_NotebookAccountPassword, type:_SECRET_, label:ZaMsg.Domain_NotebookAccountPassword, labelLocation:_LEFT_,visibilityChecks:[],enableDisableChecks:[]},
						{ref:ZaDomain.A_NotebookAccountPassword2, type:_SECRET_, label:ZaMsg.NAD_ConfirmPassword, labelLocation:_LEFT_,visibilityChecks:[],enableDisableChecks:[]}
					]
				},
				{type:_CASE_, caseKey:_tab2,
				   items:[								
						{ref:ZaDomain.A_NotebookDomainACLs, type:_ACL_, label:ZaMsg.LBL_ACL_Dom,labelLocation:_LEFT_,
							visibleBoxes:{r:true,w:true,a:false,i:true,d:true,x:false},
							visibilityChecks:[],enableDisableChecks:[]
						},							
						{type:_SPACER_, height:10},
						{ref:ZaDomain.A_NotebookAllACLs, type:_ACL_, label:ZaMsg.LBL_ACL_All,labelLocation:_LEFT_,
							visibleBoxes:{r:true,w:true,a:false,i:true,d:true,x:false},
							visibilityChecks:[],enableDisableChecks:[]
						},
						{type:_SPACER_, height:10},
						{ref:ZaDomain.A_NotebookPublicACLs, type:_ACL_, label:ZaMsg.LBL_ACL_Public,labelLocation:_LEFT_,
							visibleBoxes:{r:true,w:false,a:false,i:false,d:false,x:false},
							visibilityChecks:[],enableDisableChecks:[]
						}
					]
				},
				{type:_CASE_, caseKey:_tab3,
				   items:[	
						{type:_ZAWIZ_TOP_GROUPER_, numCols:1,colSpan:2,label:ZaMsg.Domain_PerGrp_Acl,							
							items:[
								{type:_REPEAT_, ref:ZaDomain.A_NotebookGroupACLs,
									label:null,
									visibilityChecks:[],enableDisableChecks:[],
									repeatInstance:{name:"test@test.com",acl:{r:0,w:0,i:0,d:0,a:0,x:0}}, 
									showAddButton:true, showRemoveButton:true, 
									addButtonLabel:ZaMsg.Domain_AddGrpAcl, 
									addButtonWidth: 100,
									showAddOnNextRow:true,
									removeButtonLabel:ZaMsg.Domain_REPEAT_REMOVE,								
									items: [
										{ref:".", type:_ADDR_ACL_, label:null, labelLocation:_NONE_,
											visibilityChecks:[],enableDisableChecks:[],
											visibleBoxes:{r:true,w:true,a:false,i:true,d:true,x:false},
											onChange:null,
											//forceUpdate:true,
											dataFetcherMethod:ZaSearch.prototype.dynSelectSearchGroups
										}
									]
								}
							]
						},
						{type:_SPACER_, height:10},
						{type:_ZAWIZ_TOP_GROUPER_, numCols:1,colSpan:2,label:ZaMsg.Domain_PerUsr_Acl,													
							items:[
								{type:_SPACER_, height:10},
								{type:_REPEAT_, ref:ZaDomain.A_NotebookUserACLs,
									label:null,
									visibilityChecks:[],enableDisableChecks:[], 
									repeatInstance:{name:"test@test.com",acl:{r:0,w:0,i:0,d:0,a:0,x:0}}, 
									showAddButton:true, showRemoveButton:true, 
									addButtonLabel:ZaMsg.Domain_AddUsrAcl, 
									addButtonWidth: 100,
									showAddOnNextRow:true,
									removeButtonLabel:ZaMsg.Domain_REPEAT_REMOVE,								
									items: [
										{ref:".", type:_ADDR_ACL_, 
											visibilityChecks:[],enableDisableChecks:[],
											label:null, labelLocation:_NONE_,
											visibleBoxes:{r:true,w:true,a:false,i:true,d:true,x:false},
											onChange:null,
											forceUpdate:true,
											dataFetcherTypes:[ZaSearch.ACCOUNTS],
											dataFetcherAttrs:[ZaItem.A_zimbraId, ZaItem.A_cn, ZaAccount.A_name, ZaAccount.A_displayname, ZaAccount.A_mail],
											dataFetcherMethod:ZaSearch.prototype.dynSelectSearch
										}
									]
								},
								{type:_SPACER_, height:10}
							]
						}							
				   ]
				},
				{type:_CASE_, caseKey:_tab4,
					items: [
						{type:_OUTPUT_, value:ZaMsg.Domain_Documents_Config_Complete,visibilityChecks:[],enableDisableChecks:[]}
					]
				}					
			]
		}
	]
}
ZaXDialog.XFormModifiers["ZaDomainNotebookXWizard"].push(ZaDomainNotebookXWizard.myXFormModifier);