/*
 * ***** BEGIN LICENSE BLOCK *****
 * Version: ZPL 1.2
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.2 ("License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.zimbra.com/license
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See
 * the License for the specific language governing rights and limitations
 * under the License.
 * 
 * The Original Code is: Zimbra Collaboration Suite Web Client
 * 
 * The Initial Developer of the Original Code is Zimbra, Inc.
 * Portions created by Zimbra are Copyright (C) 2005, 2006 Zimbra, Inc.
 * All Rights Reserved.
 * 
 * Contributor(s):
 * 
 * ***** END LICENSE BLOCK *****
 */

function ZaNewAccountXWizard (parent, app) {
	ZaXWizardDialog.call(this, parent, app, null, ZaMsg.NCD_NewAccTitle, "700px", "300px","ZaNewAccountXWizard");
	this.accountStatusChoices = [
		{value:ZaAccount.ACCOUNT_STATUS_ACTIVE, label:ZaAccount._ACCOUNT_STATUS[ZaAccount.ACCOUNT_STATUS_ACTIVE]}, 
		{value:ZaAccount.ACCOUNT_STATUS_CLOSED, label:ZaAccount._ACCOUNT_STATUS[ZaAccount.ACCOUNT_STATUS_CLOSED]},
		{value:ZaAccount.ACCOUNT_STATUS_LOCKED, label: ZaAccount._ACCOUNT_STATUS[ZaAccount.ACCOUNT_STATUS_LOCKED]},
		{value:ZaAccount.ACCOUNT_STATUS_MAINTENANCE, label:ZaAccount._ACCOUNT_STATUS[ZaAccount.ACCOUNT_STATUS_MAINTENANCE]}
	];	
//	this.accountStatusChoices = [ZaAccount.ACCOUNT_STATUS_ACTIVE, ZaAccount.ACCOUNT_STATUS_MAINTENANCE, ZaAccount.ACCOUNT_STATUS_LOCKED, ZaAccount.ACCOUNT_STATUS_CLOSED];		

	this.stepChoices = [
		{label:ZaMsg.TABT_GeneralPage, value:ZaNewAccountXWizard.GENERAL_STEP},
		{label:ZaMsg.TABT_ContactInfo, value:ZaNewAccountXWizard.CONTACT_STEP}
	];
	var stepCounter = 2;	
	if(ZaSettings.ACCOUNTS_ALIASES_ENABLED) {
		stepCounter++;
		ZaNewAccountXWizard.ALIASES_STEP = stepCounter;
		this.stepChoices.push({value:ZaNewAccountXWizard.ALIASES_STEP, label:ZaMsg.TABT_Aliases});
	}
	if(ZaSettings.ACCOUNTS_FORWARDING_ENABLED) {
		stepCounter++;
		ZaNewAccountXWizard.FORWARDING_STEP = stepCounter;		
		this.stepChoices.push({value:ZaNewAccountXWizard.FORWARDING_STEP, label:ZaMsg.TABT_Forwarding});
	}
	if(ZaSettings.ACCOUNTS_FEATURES_ENABLED) {
		stepCounter++;
		ZaNewAccountXWizard.FEATURES_STEP = stepCounter;		
		this.stepChoices.push({value:ZaNewAccountXWizard.FEATURES_STEP, label:ZaMsg.TABT_Features});
	}				
	if(ZaSettings.ACCOUNTS_PREFS_ENABLED) {
		stepCounter++;
		ZaNewAccountXWizard.PREFS_STEP = stepCounter;	
		this.stepChoices.push({value:ZaNewAccountXWizard.PREFS_STEP, label:ZaMsg.TABT_Preferences});
	}
	if(ZaSettings.SKIN_PREFS_ENABLED) {
		stepCounter++;
		ZaNewAccountXWizard.SKINS_STEP = stepCounter;		
		this.stepChoices.push({value:ZaNewAccountXWizard.SKINS_STEP, label:ZaMsg.TABT_Themes});
	}
	
	if(ZaSettings.ZIMLETS_ENABLED) {
		stepCounter++;
		ZaNewAccountXWizard.ZIMLETS_STEP = stepCounter;			
		this.stepChoices.push({value:ZaNewAccountXWizard.ZIMLETS_STEP, label:ZaMsg.TABT_Zimlets});
	}
		
	if(ZaSettings.ACCOUNTS_ADVANCED_ENABLED) {
		stepCounter++;
		ZaNewAccountXWizard.ADVANCED_STEP = stepCounter;			
		this.stepChoices.push({value:ZaNewAccountXWizard.ADVANCED_STEP, label:ZaMsg.TABT_Advanced});
	}
	this._lastStep = this.stepChoices.length;


	this.initForm(ZaAccount.myXModel,this.getMyXForm());	
//    DBG.timePt(AjxDebug.PERF, "finished initForm");
   
	this._localXForm.setController(this._app);	
	this._localXForm.addListener(DwtEvent.XFORMS_FORM_DIRTY_CHANGE, new AjxListener(this, ZaNewAccountXWizard.prototype.handleXFormChange));
	this._localXForm.addListener(DwtEvent.XFORMS_VALUE_ERROR, new AjxListener(this, ZaNewAccountXWizard.prototype.handleXFormChange));	
	this._helpURL = ZaNewAccountXWizard.helpURL;
}
ZaNewAccountXWizard.GENERAL_STEP = 1;
ZaNewAccountXWizard.CONTACT_STEP = 2;
ZaNewAccountXWizard.ALIASES_STEP = 3;
ZaNewAccountXWizard.FORWARDING_STEP = 4;
ZaNewAccountXWizard.FEATURES_STEP = 5;
ZaNewAccountXWizard.PREFS_STEP = 6;
ZaNewAccountXWizard.SKINS_STEP = 7;
ZaNewAccountXWizard.ZIMLETS_STEP = 8;
ZaNewAccountXWizard.ADVANCED_STEP = 9;

ZaNewAccountXWizard.prototype = new ZaXWizardDialog;
ZaNewAccountXWizard.prototype.constructor = ZaNewAccountXWizard;
ZaXDialog.XFormModifiers["ZaNewAccountXWizard"] = new Array();
ZaNewAccountXWizard.helpURL = "/zimbraAdmin/adminhelp/html/WebHelp/managing_accounts/create_an_account.htm";
ZaNewAccountXWizard.prototype.handleXFormChange = 
function () {
	if(this._localXForm.hasErrors()) {
		this._button[DwtWizardDialog.FINISH_BUTTON].setEnabled(false);
	} else {
		if(this._containedObject.attrs[ZaAccount.A_lastName] && this._containedObject[ZaAccount.A_name].indexOf("@") > 0)
			this._button[DwtWizardDialog.FINISH_BUTTON].setEnabled(true);
	}
}

/*
ZaNewAccountXWizard.onNameFieldChanged = 
function (value, event, form) {
	if(value && value.length > 0) {
		form.parent._button[DwtWizardDialog.FINISH_BUTTON].setEnabled(true);
	} else {
		form.parent._button[DwtWizardDialog.FINISH_BUTTON].setEnabled(false);
	}
	this.setInstanceValue(value);
	return value;
}*/

/**
* Overwritten methods that control wizard's flow (open, go next,go previous, finish)
**/
ZaNewAccountXWizard.prototype.popup = 
function (loc) {
	ZaXWizardDialog.prototype.popup.call(this, loc);
	this._button[DwtWizardDialog.NEXT_BUTTON].setEnabled(true);
	this._button[DwtWizardDialog.FINISH_BUTTON].setEnabled(false);
	this._button[DwtWizardDialog.PREV_BUTTON].setEnabled(false);	
}

ZaNewAccountXWizard.prototype.finishWizard = 
function() {
	try {
		
		if(!ZaAccount.checkValues(this._containedObject, this._app)) {
			return false;
		}
		var account = ZaItem.create(this._containedObject,ZaAccount,"ZaAccount", this._app);
		if(account != null) {
			//if creation took place - fire an DomainChangeEvent
			this._app.getAccountViewController().fireCreationEvent(account);
			this.popdown();		
		}
	} catch (ex) {
		switch(ex.code) {		
			case ZmCsfeException.ACCT_EXISTS:
				this._app.getCurrentController().popupErrorDialog(ZaMsg.ERROR_ACCOUNT_EXISTS);
			break;
			case ZmCsfeException.ACCT_INVALID_PASSWORD:
				this._app.getCurrentController().popupErrorDialog(ZaMsg.ERROR_PASSWORD_INVALID);
			break;
			default:
				this._app.getCurrentController()._handleException(ex, "ZaNewAccountXWizard.prototype.finishWizard", null, false);
			break;		
		}
	}
}

ZaNewAccountXWizard.prototype.goNext = 
function() {
	if (this._containedObject[ZaModel.currentStep] == 1) {
		//check if passwords match
		if(this._containedObject.attrs[ZaAccount.A_password]) {
			if(this._containedObject.attrs[ZaAccount.A_password] != this._containedObject[ZaAccount.A2_confirmPassword]) {
				this._app.getCurrentController().popupErrorDialog(ZaMsg.ERROR_PASSWORD_MISMATCH);
				return false;
			}
		}
		this._button[DwtWizardDialog.PREV_BUTTON].setEnabled(true);
		
	} 
	this.goPage(this._containedObject[ZaModel.currentStep] + 1);
	if(this._containedObject[ZaModel.currentStep] == this._lastStep) {
		this._button[DwtWizardDialog.NEXT_BUTTON].setEnabled(false);
	}	
}

ZaNewAccountXWizard.prototype.goPrev = 
function() {
	if (this._containedObject[ZaModel.currentStep] == 2) {
		this._button[DwtWizardDialog.PREV_BUTTON].setEnabled(false);
	} else if(this._containedObject[ZaModel.currentStep] == this._lastStep) {
		this._button[DwtWizardDialog.NEXT_BUTTON].setEnabled(true);
	}
	this.goPage(this._containedObject[ZaModel.currentStep] - 1);
}

/**
* @method setObject sets the object contained in the view
* @param entry - ZaAccount object to display
**/
ZaNewAccountXWizard.prototype.setObject =
function(entry) {
	this._containedObject = new Object();
	this._containedObject.attrs = new Object();

	for (var a in entry.attrs) {
		this._containedObject.attrs[a] = entry.attrs[a];
	}
	this._containedObject.name = "";

	this._containedObject.id = null;
	if(ZaSettings.COSES_ENABLED) {
		var cosList = this._app.getCosList().getArray();
		for(var ix in cosList) {
			if(cosList[ix].name == "default") {
				this._containedObject.attrs[ZaAccount.A_COSId] = cosList[ix].id;
				this._containedObject.cos = cosList[ix];
				break;
			}
		}
	
		if(!this._containedObject.cos) {
			this._containedObject.cos = cosList[0];
			this._containedObject.attrs[ZaAccount.A_COSId] = cosList[0].id;
		}
	} else {
		this._containedObject.cos = new ZaCos(this._app);
	}
	this._containedObject.attrs[ZaAccount.A_accountStatus] = ZaAccount.ACCOUNT_STATUS_ACTIVE;
	this._containedObject[ZaAccount.A2_autodisplayname] = "TRUE";
	this._containedObject[ZaAccount.A2_autoMailServer] = "TRUE";
	this._containedObject[ZaAccount.A2_confirmPassword] = null;
	this._containedObject[ZaModel.currentStep] = 1;
	this._containedObject.attrs[ZaAccount.A_zimbraMailAlias] = new Array();
//	var domainName = this._app._appCtxt.getAppController().getOverviewPanelController().getCurrentDomain();
	var domainName;
	if(ZaSettings.GLOBAL_CONFIG_ENABLED) {
		if(!domainName) {
			//find out what is the default domain
			domainName = this._app.getGlobalConfig().attrs[ZaGlobalConfig.A_zimbraDefaultDomainName];
			if(!domainName && ZaSettings.DOMAINS_ENABLED) {
				domainName = this._app.getDomainList().getArray()[0].name;
			}
		}
		this._containedObject.globalConfig = this._app.getGlobalConfig();
	} 
	if(!domainName) {
		domainName =  ZaSettings.myDomainName;
	}
	this._containedObject[ZaAccount.A_name] = "@" + domainName;
	if(ZaSettings.SKIN_PREFS_ENABLED) {
		//convert strings to objects
		//var _tmpSkinMap = {};
		
		var installedSkins = this._app.getInstalledSkins();
		var _tmpSkins = [];
		if(installedSkins == null) {
			installedSkins = [];
		} else if (AjxUtil.isString(installedSkins))	 {
			installedSkins = [installedSkins];
		}
			
		var cnt = installedSkins.length;
		for(var i=0; i<cnt; i++) {
			var skin = installedSkins[i];
			_tmpSkins[i] = new String(skin);
			_tmpSkins[i].id = "id_"+skin;
		}
		
		this._containedObject[ZaAccount.A_zimbraInstalledSkinPool] = _tmpSkins;
		
		var availableSkin;
		_tmpSkins = [];
		if(availableSkin != null) {
		 	if (AjxUtil.isString(availableSkin))	 {
				availableSkin = [availableSkin];
			}
		
			for(var i=0; i<availableSkin.length; i++) {
				var skin = availableSkin[i];
				_tmpSkins[i] = new String(skin);
				_tmpSkins[i].id = "id_"+skin;
			}
			this._containedObject.attrs[ZaAccount.A_zimbraAvailableSkin] = _tmpSkins;
	
		}
	}
	if(ZaSettings.ZIMLETS_ENABLED) {
		var _tmpZimlets = [];
		var zimlets = entry.attrs[ZaAccount.A_zimbraZimletAvailableZimlets];
		if(zimlets != null && zimlets != "") {
			if (AjxUtil.isString(zimlets))	 {
				zimlets = [zimlets];
			}
			
			var cnt = zimlets.length;
			for(var i=0; i<cnt; i++) {
				var zimlet = zimlets[i];
				_tmpZimlets[i] = new String(zimlet);
				_tmpZimlets[i].id = "id_"+zimlet;
			}
			
			this._containedObject.attrs[ZaAccount.A_zimbraZimletAvailableZimlets] = _tmpZimlets;
		} else
			this._containedObject.attrs[ZaAccount.A_zimbraZimletAvailableZimlets] = null;		
		
		
		//convert strings to objects
		var zimlets;
		if(ZaSettings.COSES_ENABLED) {	
			 zimlets = this._containedObject.cos.attrs[ZaCos.A_zimbraZimletAvailableZimlets];
		} else {
			 zimlets = this._containedObject.attrs[ZaCos.A_zimbraZimletAvailableZimlets];
		}
		_tmpZimlets = [];
		if(zimlets == null) {
			zimlets = [];
		} else if (AjxUtil.isString(zimlets))	 {
			zimlets = [zimlets];
		}
		
		for(var i=0; i<zimlets.length; i++) {
			var zimlet = zimlets[i];
			_tmpZimlets[i] = new String(zimlet);
			_tmpZimlets[i].id = "id_"+zimlet;
		}
		if(ZaSettings.COSES_ENABLED) {			
			this._containedObject.cos.attrs[ZaCos.A_zimbraZimletAvailableZimlets] = _tmpZimlets;
		} else {
			this._containedObject.attrs[ZaCos.A_zimbraZimletAvailableZimlets] = _tmpZimlets;
		}
					
		//convert strings to objects
		var zimlets = ZaZimlet.getAll(this._app, "extension");
		_tmpZimlets = [];
		if(zimlets == null) {
			zimlets = [];
		} 
		
		if(zimlets instanceof ZaItemList || zimlets instanceof AjxVector)
			zimlets = zimlets.getArray();
			
		var cnt = zimlets.length;
		//convert strings to objects	
		for(var i=0; i<cnt; i++) {
			var zimlet = zimlets[i];
			_tmpZimlets[i] = new String(zimlet.name);
			_tmpZimlets[i].id = "id_"+zimlet.name;
		}
		this._containedObject[ZaAccount.A_zimbraInstalledZimletPool] = _tmpZimlets;		
	}	
	this._localXForm.setInstance(this._containedObject);
}

ZaNewAccountXWizard.onCOSChanged = 
function(value, event, form) {
	if(!ZaSettings.COSES_ENABLED)
		return;
		
	var cosList = form.getController().getCosList().getArray();
	var cnt = cosList.length;
	for(var i = 0; i < cnt; i++) {
		if(cosList[i].id == value) {
			form.getInstance().cos = cosList[i];
			break;
		}
	}
	this.setInstanceValue(value);
	return value;
}

ZaNewAccountXWizard.myXFormModifier = function(xFormObject) {	
	var domainName;
	if(ZaSettings.DOMAINS_ENABLED)
		domainName = this._app.getDomainList().getArray()[0].name;
	else 
		domainName = ZaSettings.myDomainName;

	var emptyAlias = "@" + domainName;
	var cases = new Array();
	


	var case1 = {type:_CASE_, numCols:1, relevant:"instance[ZaModel.currentStep] == ZaNewAccountXWizard.GENERAL_STEP", align:_LEFT_, valign:_TOP_};
	var case1Items = [
		{type:_ZAWIZ_TOP_GROUPER_, label:ZaMsg.NAD_AccountNameGrouper, id:"account_wiz_name_group",numCols:2,
			items:[
			{ref:ZaAccount.A_name, type:_EMAILADDR_, msgName:ZaMsg.NAD_AccountName,label:ZaMsg.NAD_AccountName,
							 labelLocation:_LEFT_,forceUpdate:true},
			{ref:ZaAccount.A_firstName, type:_TEXTFIELD_, msgName:ZaMsg.NAD_FirstName,label:ZaMsg.NAD_FirstName, 
				labelLocation:_LEFT_, cssClass:"admin_xform_name_input", width:150,
				elementChanged: function(elementValue,instanceValue, event) {
					if(this.getInstance()[ZaAccount.A2_autodisplayname]=="TRUE") {
						ZaAccountXFormView.generateDisplayName(this.getInstance(), elementValue, this.getInstance().attrs[ZaAccount.A_lastName],this.getInstance().attrs[ZaAccount.A_initials] );
					}
					this.getForm().itemChanged(this, elementValue, event);
				}
			},
			{ref:ZaAccount.A_initials, type:_TEXTFIELD_, msgName:ZaMsg.NAD_Initials,label:ZaMsg.NAD_Initials, labelLocation:_LEFT_, cssClass:"admin_xform_name_input", width:50,
				elementChanged: function(elementValue,instanceValue, event) {
					if(this.getInstance()[ZaAccount.A2_autodisplayname]=="TRUE") {
						ZaAccountXFormView.generateDisplayName(this.getInstance(), this.getInstance().attrs[ZaAccount.A_firstName], this.getInstance().attrs[ZaAccount.A_lastName],elementValue);
					}
					this.getForm().itemChanged(this, elementValue, event);
				}
			},
			{ref:ZaAccount.A_lastName, type:_TEXTFIELD_, msgName:ZaMsg.NAD_LastName,label:ZaMsg.NAD_LastName, labelLocation:_LEFT_, cssClass:"admin_xform_name_input", width:150,
				elementChanged: function(elementValue,instanceValue, event) {
					if(this.getInstance()[ZaAccount.A2_autodisplayname]=="TRUE") {
						ZaAccountXFormView.generateDisplayName(this.getInstance(), this.getInstance().attrs[ZaAccount.A_firstName], elementValue ,this.getInstance().attrs[ZaAccount.A_initials]);
					}
					this.getForm().itemChanged(this, elementValue, event);
				}
			},
			{type:_GROUP_, numCols:3, nowrap:true, width:200, msgName:ZaMsg.NAD_DisplayName,label:ZaMsg.NAD_DisplayName+":", labelLocation:_LEFT_, 
				items: [
					{ref:ZaAccount.A_displayname, type:_TEXTFIELD_, label:null,	cssClass:"admin_xform_name_input", width:150, 
						relevant:"instance[ZaAccount.A2_autodisplayname] == \"FALSE\"",
						relevantBehavior:_DISABLE_
					},
					{ref:ZaAccount.A2_autodisplayname, type:_CHECKBOX_, msgName:ZaMsg.NAD_Auto,label:ZaMsg.NAD_Auto,labelLocation:_RIGHT_,trueValue:"TRUE", falseValue:"FALSE",
						elementChanged: function(elementValue,instanceValue, event) {
							if(elementValue=="TRUE") {
								if(ZaAccountXFormView.generateDisplayName(this.getInstance(), this.getInstance().attrs[ZaAccount.A_firstName], this.getInstance().attrs[ZaAccount.A_lastName],this.getInstance().attrs[ZaAccount.A_initials])) {
								//	this.getForm().itemChanged(this, elementValue, event);
									this.getForm().parent.setDirty(true);
								}
							}
							this.getForm().itemChanged(this, elementValue, event);
						}
					}
				]
			},
			{ref:ZaAccount.A_zimbraMailCanonicalAddress, type:_TEXTFIELD_,width:250,
				msgName:ZaMsg.NAD_CanonicalFrom,label:ZaMsg.NAD_CanonicalFrom, labelLocation:_LEFT_, align:_LEFT_
			},
			{ref:ZaAccount.A_zimbraHideInGal, type:_CHECKBOX_,
			  msgName:ZaMsg.NAD_zimbraHideInGal,label:ZaMsg.NAD_zimbraHideInGal, trueValue:"TRUE", falseValue:"FALSE"
			}
		]}
	];

	var setupGroup = {type:_ZAWIZ_TOP_GROUPER_, label:ZaMsg.NAD_AccountSetupGrouper, id:"account_wiz_setup_group", 
		numCols:2,
		items: [
			{ref:ZaAccount.A_accountStatus, type:_OSELECT1_, msgName:ZaMsg.NAD_AccountStatus,
				label:ZaMsg.NAD_AccountStatus, 
				labelLocation:_LEFT_, choices:this.accountStatusChoices
			}
		]
	}
		
	if(ZaSettings.COSES_ENABLED) {
		setupGroup.items.push({ref:ZaAccount.A_COSId, type:_OSELECT1_, msgName:ZaMsg.NAD_ClassOfService,label:ZaMsg.NAD_ClassOfService, labelLocation:_LEFT_, choices:this._app.getCosListChoices(), onChange:ZaNewAccountXWizard.onCOSChanged });
	}
	setupGroup.items.push({ref:ZaAccount.A_isAdminAccount, type:_CHECKBOX_, 
							msgName:ZaMsg.NAD_IsAdmin,label:ZaMsg.NAD_IsAdmin,
							trueValue:"TRUE", falseValue:"FALSE",
							relevantBehavior:_HIDE_
						});
	if(ZaSettings.SERVERS_ENABLED) {
		setupGroup.items.push({type:_GROUP_, numCols:3, nowrap:true, label:ZaMsg.NAD_MailServer, labelLocation:_LEFT_,
							items: [
								{ ref: ZaAccount.A_mailHost, type: _OSELECT1_, label: null, editable:false, choices: this._app.getServerListChoices(), 
									relevant:"instance[ZaAccount.A2_autoMailServer]==\"FALSE\" && form.getController().getServerListChoices().getChoices().values.length != 0",
									relevantBehavior:_DISABLE_
							  	},
								{ref:ZaAccount.A2_autoMailServer, type:_CHECKBOX_, msgName:ZaMsg.NAD_Auto,label:ZaMsg.NAD_Auto,labelLocation:_RIGHT_,trueValue:"TRUE", falseValue:"FALSE"}
							]
						});
	}						
	case1Items.push(setupGroup);
	
	var passwordGroup = {type:_ZAWIZ_TOP_GROUPER_, label:ZaMsg.NAD_PasswordGrouper,id:"account_wiz_password_group", 
		numCols:2,
		items:[
		{ref:ZaAccount.A_password, type:_SECRET_, msgName:ZaMsg.NAD_Password,
			label:ZaMsg.NAD_Password, labelLocation:_LEFT_, 
			cssClass:"admin_xform_name_input"
		},
		{ref:ZaAccount.A2_confirmPassword, type:_SECRET_, msgName:ZaMsg.NAD_ConfirmPassword,
			label:ZaMsg.NAD_ConfirmPassword, labelLocation:_LEFT_, 
			cssClass:"admin_xform_name_input"
		},
		{ref:ZaAccount.A_zimbraPasswordMustChange,  type:_CHECKBOX_,  
			msgName:ZaMsg.NAD_MustChangePwd,label:ZaMsg.NAD_MustChangePwd,trueValue:"TRUE", falseValue:"FALSE"}
		]
	};
	case1Items.push(passwordGroup);														
	
	var notesGroup = {type:_ZAWIZ_TOP_GROUPER_, label:ZaMsg.NAD_NotesGrouper, id:"account_wiz_notes_group",
		numCols:2,
	 	items:[

		{ref:ZaAccount.A_description, type:_INPUT_, msgName:ZaMsg.NAD_Description,
			label:ZaMsg.NAD_Description, labelLocation:_LEFT_, cssClass:"admin_xform_name_input"
		},
		{ref:ZaAccount.A_notes, type:_TEXTAREA_, msgName:ZaMsg.NAD_Notes,
			label:ZaMsg.NAD_Notes, labelLocation:_LEFT_, labelCssStyle:"vertical-align:top", width:"30em"
		}
		]
	};

	case1Items.push(notesGroup);
	case1.items = case1Items;
	cases.push(case1);
	var case2={type:_CASE_, numCols:1, relevant:"instance[ZaModel.currentStep] == ZaNewAccountXWizard.CONTACT_STEP",
					items: [
						{type:_ZAWIZGROUP_, 
							items:[
								{ref:ZaAccount.A_telephoneNumber, type:_TEXTFIELD_, msgName:ZaMsg.NAD_telephoneNumber,label:ZaMsg.NAD_telephoneNumber, labelLocation:_LEFT_, width:250}
							]
						},
						{type:_ZAWIZGROUP_, 
							items:[					
								{ref:ZaAccount.A_company, type:_TEXTFIELD_, msgName:ZaMsg.NAD_company,label:ZaMsg.NAD_company, labelLocation:_LEFT_, width:250},
								{ref:ZaAccount.A_orgUnit, type:_TEXTFIELD_, msgName:ZaMsg.NAD_orgUnit,label:ZaMsg.NAD_orgUnit, labelLocation:_LEFT_, width:250},														
								{ref:ZaAccount.A_office, type:_TEXTFIELD_, msgName:ZaMsg.NAD_office,label:ZaMsg.NAD_office, labelLocation:_LEFT_, width:250}
							]
						},
						{type:_ZAWIZGROUP_, 
							items:[						
								{ref:ZaAccount.A_street, type:_TEXTAREA_, msgName:ZaMsg.NAD_street,label:ZaMsg.NAD_street, labelLocation:_LEFT_, width:250},
								{ref:ZaAccount.A_city, type:_TEXTFIELD_, msgName:ZaMsg.NAD_city,label:ZaMsg.NAD_city, labelLocation:_LEFT_, width:250},
								{ref:ZaAccount.A_state, type:_TEXTFIELD_, msgName:ZaMsg.NAD_state,label:ZaMsg.NAD_state, labelLocation:_LEFT_, width:250},
								{ref:ZaAccount.A_zip, type:_TEXTFIELD_, msgName:ZaMsg.NAD_zip,label:ZaMsg.NAD_zip, labelLocation:_LEFT_, width:100},
								{ref:ZaAccount.A_country, type:_TEXTFIELD_, msgName:ZaMsg.NAD_country,label:ZaMsg.NAD_country, labelLocation:_LEFT_, width:250}
							]
						}							
					]
				};
	cases.push(case2);

	if(ZaSettings.ACCOUNTS_ALIASES_ENABLED) {
		cases.push({type:_CASE_, numCols:1, relevant:"instance[ZaModel.currentStep] == ZaNewAccountXWizard.ALIASES_STEP",
					items: [
						{type:_OUTPUT_, value:ZaMsg.NAD_AccountAliases},
						{ref:ZaAccount.A_zimbraMailAlias, type:_REPEAT_, label:null, repeatInstance:emptyAlias, showAddButton:true, showRemoveButton:true, 
							addButtonLabel:ZaMsg.NAD_AddAlias, 
							showAddOnNextRow:true,
							removeButtonLabel:ZaMsg.NAD_RemoveAlias,
							items: [
								{ref:".", type:_EMAILADDR_, label:null}
							]
						}
					]
				});								
	}
							
	var zimbraFeatureMailForwardingEnabledItem = 
				{ref:ZaAccount.A_zimbraFeatureMailForwardingEnabled, 
					msgName:ZaMsg.NAD_zimbraFeatureMailForwardingEnabled,
					trueValue:"TRUE", falseValue:"FALSE"
				}

	if (ZaSettings.isDomainAdmin || !ZaSettings.COSES_ENABLED) {
		zimbraFeatureMailForwardingEnabledItem.type = _CHECKBOX_ ;
		zimbraFeatureMailForwardingEnabledItem.label = ZaMsg.NAD_zimbraFeatureMailForwardingEnabled ;
	}else{
		zimbraFeatureMailForwardingEnabledItem.type = _SUPER_WIZ_CHECKBOX_ ;
		zimbraFeatureMailForwardingEnabledItem.resetToSuperLabel = ZaMsg.NAD_ResetToCOS;
		zimbraFeatureMailForwardingEnabledItem.labelCssStyle  = "width:190px;";
		zimbraFeatureMailForwardingEnabledItem.checkBoxLabel = ZaMsg.NAD_zimbraFeatureMailForwardingEnabled;
		zimbraFeatureMailForwardingEnabledItem.labelLocation = _LEFT_; 
	}
	
	if(ZaSettings.ACCOUNTS_FORWARDING_ENABLED) {
		cases.push({type:_CASE_, numCols:2, relevant:"instance[ZaModel.currentStep] == ZaNewAccountXWizard.FORWARDING_STEP",
					items: [
						zimbraFeatureMailForwardingEnabledItem,
						{ref:ZaAccount.A_zimbraPrefMailForwardingAddress,width:250,
							labelCssClass:"xform_label",
							type:_TEXTFIELD_, msgName:ZaMsg.NAD_zimbraPrefMailForwardingAddress,
							label:ZaMsg.NAD_zimbraPrefMailForwardingAddress+":", labelLocation:_LEFT_, 
							cssClass:"admin_xform_name_input",
							relevantBehavior:_DISABLE_, 
							relevant:"this.getModel().getInstanceValue(this.getInstance(),ZaAccount.A_zimbraFeatureMailForwardingEnabled) == \"TRUE\""
						},		
						{type:_SPACER_},
						{type:_SEPARATOR_,colSpan:2},											
						{ref:ZaAccount.A_zimbraMailForwardingAddress, type:_REPEAT_, colSpan:2, 
							label:ZaMsg.NAD_EditFwdGroup, labelLocation:_LEFT_,labelCssClass:"xform_label",
							repeatInstance:emptyAlias, 
							showAddButton:true, showRemoveButton:true, 
							addButtonLabel:ZaMsg.NAD_AddAddress, 
							showAddOnNextRow:true,
							removeButtonLabel:ZaMsg.NAD_RemoveAddress,
						
							items: [
								{ref:".", type:_TEXTFIELD_, label:null, width:250}
							]
						}
					]
				});				
	}	
	if(ZaSettings.ACCOUNTS_FEATURES_ENABLED) {
		cases.push({type:_CASE_,id:"account_form_features_step",
				numCols:1, width:"100%",
				relevant:"instance[ZaModel.currentStep] == ZaNewAccountXWizard.FEATURES_STEP",
				items: [
					{ type: _DWT_ALERT_,
					  containerCssStyle: "padding-top:20px;width:400px;",
					  style: DwtAlert.WARNING,
					  iconVisible: false, 
					  content: ZaMsg.NAD_CheckFeaturesInfo
					},				
					{type:_ZAWIZGROUP_, id:"account_form_features_group1", colSizes:["auto"],numCols:1,
						items:[	
							{ref:ZaAccount.A_zimbraFeatureContactsEnabled,type:_SUPER_WIZ_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_FeatureContactsEnabled,checkBoxLabel:ZaMsg.NAD_FeatureContactsEnabled, labelLocation:_LEFT_, trueValue:"TRUE", falseValue:"FALSE"},							
							{ref:ZaAccount.A_zimbraFeatureCalendarEnabled,
								type:_SUPER_WIZ_CHECKBOX_, 
								resetToSuperLabel:ZaMsg.NAD_ResetToCOS, 
								msgName:ZaMsg.NAD_FeatureCalendarEnabled,
								checkBoxLabel:ZaMsg.NAD_FeatureCalendarEnabled,  
								trueValue:"TRUE", falseValue:"FALSE"},														
							{ref:ZaAccount.A_zimbraFeatureNotebookEnabled, type:_SUPER_WIZ_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_zimbraFeatureNotebookEnabled,checkBoxLabel:ZaMsg.NAD_zimbraFeatureNotebookEnabled,  trueValue:"TRUE", falseValue:"FALSE"}
						]
					},	
					{type:_ZAWIZGROUP_, id:"account_form_features_group2", colSizes:["auto"],numCols:1,
						items:[							
							{ref:ZaAccount.A_zimbraFeatureTaggingEnabled, type:_SUPER_WIZ_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_FeatureTaggingEnabled,checkBoxLabel:ZaMsg.NAD_FeatureTaggingEnabled, trueValue:"TRUE", falseValue:"FALSE"},
							{ref:ZaAccount.A_zimbraFeatureSharingEnabled, type:_SUPER_WIZ_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_zimbraFeatureSharingEnabled,checkBoxLabel:ZaMsg.NAD_zimbraFeatureSharingEnabled,trueValue:"TRUE", falseValue:"FALSE"},
							{ref:ZaAccount.A_zimbraFeatureChangePasswordEnabled, type:_SUPER_WIZ_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_FeatureChangePasswordEnabled,checkBoxLabel:ZaMsg.NAD_FeatureChangePasswordEnabled,trueValue:"TRUE", falseValue:"FALSE"},
							{ref:ZaAccount.A_zimbraFeatureSkinChangeEnabled, type:_SUPER_WIZ_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_zimbraFeatureSkinChangeEnabled,checkBoxLabel:ZaMsg.NAD_zimbraFeatureSkinChangeEnabled, trueValue:"TRUE", falseValue:"FALSE"}	
						]
					},	
					{type:_ZAWIZGROUP_, id:"account_form_features_group3", colSizes:["auto"],numCols:1,
						items:[													
							{ref:ZaAccount.A_zimbraFeatureAdvancedSearchEnabled, type:_SUPER_WIZ_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_FeatureAdvancedSearchEnabled,checkBoxLabel:ZaMsg.NAD_FeatureAdvancedSearchEnabled, trueValue:"TRUE", falseValue:"FALSE"},
							{ref:ZaAccount.A_zimbraFeatureSavedSearchesEnabled, type:_SUPER_WIZ_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_FeatureSavedSearchesEnabled,checkBoxLabel:ZaMsg.NAD_FeatureSavedSearchesEnabled,  trueValue:"TRUE", falseValue:"FALSE"},
							{ref:ZaAccount.A_zimbraFeatureInitialSearchPreferenceEnabled, type:_SUPER_WIZ_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_FeatureInitialSearchPreferenceEnabled,checkBoxLabel:ZaMsg.NAD_FeatureInitialSearchPreferenceEnabled, trueValue:"TRUE", falseValue:"FALSE"}
						]
					},
					{type:_ZAWIZGROUP_, id:"account_form_features_group4", colSizes:["auto"],numCols:1,
						items:[						
							{ref:ZaAccount.A_zimbraImapEnabled, type:_SUPER_WIZ_CHECKBOX_, 
								resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_zimbraImapEnabled,
								checkBoxLabel:ZaMsg.NAD_zimbraImapEnabled,  trueValue:"TRUE", falseValue:"FALSE"},
							{ref:ZaAccount.A_zimbraPop3Enabled, type:_SUPER_WIZ_CHECKBOX_, 
								resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_zimbraPop3Enabled,
								checkBoxLabel:ZaMsg.NAD_zimbraPop3Enabled,  trueValue:"TRUE", falseValue:"FALSE"},		
							{ref:ZaAccount.A_zimbraFeaturePop3DataSourceEnabled, type:_SUPER_WIZ_CHECKBOX_, 
								resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_zimbraExternalPop3Enabled,
								checkBoxLabel:ZaMsg.NAD_zimbraExternalPop3Enabled,  trueValue:"TRUE", falseValue:"FALSE"},		
							{ref:ZaAccount.A_zimbraFeatureConversationsEnabled, type:_SUPER_WIZ_CHECKBOX_, 
								resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_FeatureConversationsEnabled,
								checkBoxLabel:ZaMsg.NAD_FeatureConversationsEnabled, trueValue:"TRUE", falseValue:"FALSE"},
							{ref:ZaAccount.A_zimbraFeatureFiltersEnabled, type:_SUPER_WIZ_CHECKBOX_, 
								resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_FeatureFiltersEnabled,
								checkBoxLabel:ZaMsg.NAD_FeatureFiltersEnabled,trueValue:"TRUE", falseValue:"FALSE"},
							{ref:ZaAccount.A_zimbraFeatureHtmlComposeEnabled, type:_SUPER_WIZ_CHECKBOX_, 
								resetToSuperLabel:ZaMsg.NAD_ResetToCOS, 
								msgName:ZaMsg.NAD_zimbraFeatureHtmlComposeEnabled,checkBoxLabel:ZaMsg.NAD_zimbraFeatureHtmlComposeEnabled, 
								trueValue:"TRUE", falseValue:"FALSE"},							
							{ref:ZaAccount.A_zimbraFeatureOutOfOfficeReplyEnabled, type:_SUPER_WIZ_CHECKBOX_, 
								resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_zimbraFeatureOutOfOfficeReplyEnabled,
								checkBoxLabel:ZaMsg.NAD_zimbraFeatureOutOfOfficeReplyEnabled, 
								trueValue:"TRUE", falseValue:"FALSE"},
							{ref:ZaAccount.A_zimbraFeatureNewMailNotificationEnabled, 
								type:_SUPER_WIZ_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, 
								msgName:ZaMsg.NAD_zimbraFeatureNewMailNotificationEnabled,
								checkBoxLabel:ZaMsg.NAD_zimbraFeatureNewMailNotificationEnabled, 
								trueValue:"TRUE", falseValue:"FALSE"},
							{ref:ZaAccount.A_zimbraFeatureIdentitiesEnabled,
								type:_SUPER_WIZ_CHECKBOX_, 
								resetToSuperLabel:ZaMsg.NAD_ResetToCOS, 
								msgName:ZaMsg.NAD_FeatureIdentitiesEnabled,
								checkBoxLabel:ZaMsg.NAD_FeatureIdentitiesEnabled,  
								trueValue:"TRUE", falseValue:"FALSE"
							}							
						]
					},
					{type:_ZAWIZGROUP_, id:"account_form_features_group5", colSizes:["auto"],numCols:1,
						items:[						
							{ref:ZaAccount.A_zimbraFeatureGalEnabled, type:_SUPER_WIZ_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_FeatureGalEnabled,checkBoxLabel:ZaMsg.NAD_FeatureGalEnabled, trueValue:"TRUE", falseValue:"FALSE"},
							{ref:ZaAccount.A_zimbraFeatureGalAutoCompleteEnabled, type:_SUPER_WIZ_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_zimbraFeatureGalAutoCompleteEnabled,checkBoxLabel:ZaMsg.NAD_zimbraFeatureGalAutoCompleteEnabled,  trueValue:"TRUE", falseValue:"FALSE"}
						]
					}
				]
				});
	}	
		if(ZaSettings.ACCOUNTS_PREFS_ENABLED) {
		var prefItems = [
				{ref:ZaAccount.A_prefSaveToSent,  type:_SUPER_WIZ_CHECKBOX_, colSpan:2,
							resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_prefSaveToSent,
							checkBoxLabel:ZaMsg.NAD_prefSaveToSent,trueValue:"TRUE", falseValue:"FALSE"},
						{ref:ZaAccount.A_zimbraPrefMessageViewHtmlPreferred, type:_SUPER_WIZ_CHECKBOX_, colSpan:2,
							resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_zimbraPrefMessageViewHtmlPreferred,
							checkBoxLabel:ZaMsg.NAD_zimbraPrefMessageViewHtmlPreferred, trueValue:"TRUE", falseValue:"FALSE"},
						{ref:ZaAccount.A_zimbraPrefComposeInNewWindow, type:_SUPER_WIZ_CHECKBOX_, 
							resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_zimbraPrefComposeInNewWindow,checkBoxLabel:ZaMsg.NAD_zimbraPrefComposeInNewWindow,trueValue:"TRUE", falseValue:"FALSE"},							
						{ref:ZaAccount.A_zimbraPrefForwardReplyInOriginalFormat, type:_SUPER_WIZ_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_zimbraPrefForwardReplyInOriginalFormat,checkBoxLabel:ZaMsg.NAD_zimbraPrefForwardReplyInOriginalFormat, trueValue:"TRUE", falseValue:"FALSE"},														
						{ref:ZaAccount.A_zimbraPrefComposeFormat, type:_SUPERWIZ_SELECT1_,
							resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_zimbraPrefComposeFormat,
							label:ZaMsg.NAD_zimbraPrefComposeFormat, labelLocation:_LEFT_},							
						{ref:ZaAccount.A_zimbraPrefAutoAddAddressEnabled, type:_SUPER_WIZ_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_zimbraPrefAutoAddAddressEnabled,checkBoxLabel:ZaMsg.NAD_zimbraPrefAutoAddAddressEnabled, trueValue:"TRUE", falseValue:"FALSE"},							
						{ref:ZaAccount.A_zimbraPrefGalAutoCompleteEnabled, type:_SUPER_WIZ_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_zimbraPrefGalAutoCompleteEnabled,checkBoxLabel:ZaMsg.NAD_zimbraPrefGalAutoCompleteEnabled, trueValue:"TRUE", falseValue:"FALSE"},																				
						{type:_SEPARATOR_},
						{ref:ZaAccount.A_zimbraPrefGroupMailBy, type:_SUPERWIZ_SELECT1_,
							resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_zimbraPrefGroupMailBy,
							label:ZaMsg.NAD_zimbraPrefGroupMailBy, labelLocation:_LEFT_},							
						{ref:ZaAccount.A_zimbraPrefContactsPerPage, type:_SUPERWIZ_SELECT1_,
							msgName:ZaMsg.NAD_PrefContactsPerPage,label:ZaMsg.NAD_PrefContactsPerPage+":", 
							labelLocation:_LEFT_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, valueLabel:null},							
						{ref:ZaAccount.A_zimbraPrefMailItemsPerPage, type:_SUPERWIZ_SELECT1_,
							msgName:ZaMsg.NAD_zimbraPrefMailItemsPerPage,label:ZaMsg.NAD_zimbraPrefMailItemsPerPage, labelLocation:_LEFT_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, valueLabel:null},
						{ref:ZaAccount.A_zimbraPrefMailInitialSearch, type:_SUPERWIZ_TEXTFIELD_, 
							msgName:ZaMsg.NAD_zimbraPrefMailInitialSearch,
							txtBoxLabel:ZaMsg.NAD_zimbraPrefMailInitialSearch, 
							labelLocation:_LEFT_, 
							resetToSuperLabel:ZaMsg.NAD_ResetToCOS
						},
						{ref:ZaAccount.A_zimbraPrefMailPollingInterval, type:_SUPER_LIFETIME_,
							colSizes:["200px","130px","120px","150px"],
							msgName:ZaMsg.NAD_zimbraPrefMailPollingInterval,
							label:ZaMsg.NAD_zimbraPrefMailPollingInterval+":", labelLocation:_LEFT_, 
							resetToSuperLabel:ZaMsg.NAD_ResetToCOS},
						{ref:ZaAccount.A_zimbraMailMinPollingInterval, type:_SUPER_LIFETIME_,colSizes:["200px","130px","120px","150px"], msgName:ZaMsg.NAD_zimbraMailMinPollingInterval,label:ZaMsg.NAD_zimbraMailMinPollingInterval+":", labelLocation:_LEFT_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS},														
						{ref:ZaAccount.A_zimbraPrefCalendarApptReminderWarningTime, type:_SUPERWIZ_SELECT1_, msgName:ZaMsg.NAD_zimbraPrefCalendarApptReminderWarningTime,label:ZaMsg.NAD_zimbraPrefCalendarApptReminderWarningTime+":", labelLocation:_LEFT_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS},													
						{ref:ZaAccount.A_zimbraPrefShowSearchString, type:_SUPER_WIZ_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_zimbraPrefShowSearchString,checkBoxLabel:ZaMsg.NAD_zimbraPrefShowSearchString,trueValue:"TRUE", falseValue:"FALSE"},
						{ref:ZaAccount.A_zimbraPrefCalendarAlwaysShowMiniCal, type:_SUPER_WIZ_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_alwaysShowMiniCal,checkBoxLabel:ZaMsg.NAD_alwaysShowMiniCal, trueValue:"TRUE", falseValue:"FALSE"},							
						{ref:ZaAccount.A_zimbraPrefCalendarUseQuickAdd, type:_SUPER_WIZ_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_useQuickAdd,checkBoxLabel:ZaMsg.NAD_useQuickAdd, trueValue:"TRUE", falseValue:"FALSE"},							
						{ref:ZaAccount.A_zimbraPrefUseTimeZoneListInCalendar, type:_SUPER_WIZ_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_zimbraPrefUseTimeZoneListInCalendar,checkBoxLabel:ZaMsg.NAD_zimbraPrefUseTimeZoneListInCalendar,trueValue:"TRUE", falseValue:"FALSE"},							
						{ref:ZaAccount.A_zimbraPrefImapSearchFoldersEnabled, type:_SUPER_WIZ_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_zimbraPrefImapSearchFoldersEnabled,checkBoxLabel:ZaMsg.NAD_zimbraPrefImapSearchFoldersEnabled,  trueValue:"TRUE", falseValue:"FALSE"},														
						{ref:ZaAccount.A_zimbraPrefMailLocalDeliveryDisabled, type:_CHECKBOX_, msgName:ZaMsg.NAD_zimbraPrefMailLocalDeliveryDisabled,label:ZaMsg.NAD_zimbraPrefMailLocalDeliveryDisabled, trueValue:"TRUE", falseValue:"FALSE"},
						{type:_SEPARATOR_},	
						{ref:ZaAccount.A_zimbraPrefUseKeyboardShortcuts, type:_SUPER_WIZ_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS,checkBoxLabel:ZaMsg.NAD_prefKeyboardShort, trueValue:"TRUE", falseValue:"FALSE"},			
						{type:_SEPARATOR_},							
						{ref:ZaAccount.A_zimbraPrefNewMailNotificationEnabled, type:_CHECKBOX_, msgName:ZaMsg.NAD_zimbraPrefNewMailNotificationEnabled,label:ZaMsg.NAD_zimbraPrefNewMailNotificationEnabled,trueValue:"TRUE", falseValue:"FALSE"},
						{ref:ZaAccount.A_zimbraPrefNewMailNotificationAddress, type:_TEXTFIELD_, msgName:ZaMsg.NAD_zimbraPrefNewMailNotificationAddress,label:ZaMsg.NAD_zimbraPrefNewMailNotificationAddress, labelLocation:_LEFT_},							
						{type:_SEPARATOR_},
						{ref:ZaAccount.A_prefMailSignatureEnabled, type:_CHECKBOX_, msgName:ZaMsg.NAD_prefMailSignatureEnabled,label:ZaMsg.NAD_prefMailSignatureEnabled,  trueValue:"TRUE", falseValue:"FALSE"},
						{ref:ZaAccount.A_zimbraPrefMailSignatureStyle, type:_SUPER_WIZ_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_zimbraPrefMailSignatureStyle,checkBoxLabel:ZaMsg.NAD_zimbraPrefMailSignatureStyle,trueValue:"internet", falseValue:"outlook"},
						{ref:ZaAccount.A_prefMailSignature, type:_TEXTAREA_, msgName:ZaMsg.NAD_prefMailSignature,label:ZaMsg.NAD_prefMailSignature, labelLocation:_LEFT_, labelCssStyle:"vertical-align:top", colSpan:3, width:"30em"},
						{type:_SEPARATOR_},
						{ref:ZaAccount.A_zimbraPrefOutOfOfficeReplyEnabled, type:_CHECKBOX_, msgName:ZaMsg.NAD_zimbraPrefOutOfOfficeReplyEnabled,label:ZaMsg.NAD_zimbraPrefOutOfOfficeReplyEnabled, trueValue:"TRUE", falseValue:"FALSE"},
						{ref:ZaAccount.A_zimbraPrefOutOfOfficeReply, type:_TEXTAREA_, msgName:ZaMsg.NAD_zimbraPrefOutOfOfficeReply,label:ZaMsg.NAD_zimbraPrefOutOfOfficeReply, labelLocation:_LEFT_, labelCssStyle:"vertical-align:top", colSpan:3, width:"30em"}
					];
		cases.push({type:_CASE_, relevant:"instance[ZaModel.currentStep] == ZaNewAccountXWizard.PREFS_STEP", 
					numCols:2, colSizes:["200px","400px"],
					items :prefItems});
	}	

	if(ZaSettings.SKIN_PREFS_ENABLED) {
		cases.push({type:_CASE_,id:"account_form_themes_step", numCols:1, width:"100%", relevant:"instance[ZaModel.currentStep]==ZaNewAccountXWizard.SKINS_STEP", 
						items: [	
							{type:_SPACER_},
							{sourceRef: ZaAccount.A_zimbraInstalledSkinPool, 
								ref:ZaAccount.A_zimbraAvailableSkin, 
								type:_SUPER_DWT_CHOOSER_, sorted:true, 
								resetToSuperLabel:ZaMsg.NAD_ResetToCOS,
								forceUpdate:true,widgetClass:ZaSkinPoolChooser,
								relevant:"ZaAccountXFormView.gotSkins.call(this)",
								width:"100%"
							},
							{type:_SPACER_},
							{type:_GROUP_, 
								items:[
								{ref:ZaAccount.A_zimbraPrefSkin, type:_SUPERWIZ_SELECT1_, 
									resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_zimbraPrefSkin,label:ZaMsg.NAD_zimbraPrefSkin, labelLocation:_LEFT_, 
									choices:this._app.getInstalledSkins(),
									relevant:"ZaAccountXFormView.gotSkins.call(this)"}
								] 
							}							
						]
		});			
	}
	
	if(ZaSettings.ZIMLETS_ENABLED) {
		cases.push({type:_CASE_,id:"account_form_zimlets_step", numCols:1, width:"100%", relevant:"instance[ZaModel.currentStep]==ZaNewAccountXWizard.ZIMLETS_STEP", 
						items: [	
							{type:_SPACER_},
							{sourceRef: ZaAccount.A_zimbraInstalledZimletPool, 
								ref:ZaAccount.A_zimbraZimletAvailableZimlets, 
								type:_SUPER_DWT_CHOOSER_, sorted:true, 
								resetToSuperLabel:ZaMsg.NAD_ResetToCOS,
								forceUpdate:true,widgetClass:ZaZimletPoolChooser,
								width:"100%"
							}							
						]
		});			
	}
		
	if(ZaSettings.ACCOUNTS_ADVANCED_ENABLED) {
		cases.push({type:_CASE_,id:"account_form_advanced_step", numCols:1, width:"100%", relevant:"instance[ZaModel.currentStep]==ZaNewAccountXWizard.ADVANCED_STEP", 
						items: [
						{type:_ZAWIZ_TOP_GROUPER_, id:"account_attachment_settings",colSizes:["auto"],numCols:1,
							label:ZaMsg.NAD_AttachmentsGrouper,						
							items :[
								{ref:ZaAccount.A_zimbraAttachmentsBlocked, type:_SUPER_WIZ_CHECKBOX_, 
									resetToSuperLabel:ZaMsg.NAD_ResetToCOS, 
									msgName:ZaMsg.NAD_RemoveAllAttachments,
									checkBoxLabel:ZaMsg.NAD_RemoveAllAttachments, 
									trueValue:"TRUE", falseValue:"FALSE"
								}
							]
						},
						{type:_ZAWIZ_TOP_GROUPER_, id:"account_quota_settings",colSizes:["auto"],numCols:1,
							label:ZaMsg.NAD_QuotaGrouper,						
							items: [
								{ref:ZaAccount.A_zimbraMailQuota, type:_SUPERWIZ_TEXTFIELD_, 
									resetToSuperLabel:ZaMsg.NAD_ResetToCOS, 
									txtBoxLabel:ZaMsg.NAD_MailQuota+":", msgName:ZaMsg.NAD_MailQuota,labelLocation:_LEFT_, 
									textFieldCssClass:"admin_xform_number_input", 
									resetToSuperLabel:ZaMsg.NAD_ResetToCOS
								},
								{ref:ZaAccount.A_zimbraContactMaxNumEntries, type:_SUPERWIZ_TEXTFIELD_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_ContactMaxNumEntries,txtBoxLabel:ZaMsg.NAD_ContactMaxNumEntries+":", labelLocation:_LEFT_, textFieldCssClass:"admin_xform_number_input"}
							]
						},
						{type:_ZAWIZ_TOP_GROUPER_,id:"account_password_settings",colSizes:["auto"],numCols:1,
							label:ZaMsg.NAD_PasswordGrouper,				
							items: [
								{ref:ZaAccount.A_zimbraPasswordLocked, type:_SUPER_WIZ_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_PwdLocked,checkBoxLabel:ZaMsg.NAD_PwdLocked, 
								 trueValue:"TRUE", falseValue:"FALSE"},								
								{ref:ZaAccount.A_zimbraMinPwdLength, 
									type:_SUPERWIZ_TEXTFIELD_, 
									resetToSuperLabel:ZaMsg.NAD_ResetToCOS, 
									msgName:ZaMsg.NAD_passMinLength,
									txtBoxLabel:ZaMsg.NAD_passMinLength+":", 
									labelLocation:_LEFT_, 
									textFieldCssClass:"admin_xform_number_input"
								},
								{ref:ZaAccount.A_zimbraMaxPwdLength, type:_SUPERWIZ_TEXTFIELD_, 
									resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_passMaxLength,
									txtBoxLabel:ZaMsg.NAD_passMaxLength+":", labelLocation:_LEFT_, 
									textFieldCssClass:"admin_xform_number_input"},
								{ref:ZaAccount.A_zimbraPasswordMinUpperCaseChars, 
									type:_SUPERWIZ_TEXTFIELD_, 
									resetToSuperLabel:ZaMsg.NAD_ResetToCOS, 
									msgName:ZaMsg.NAD_zimbraPasswordMinUpperCaseChars,
									txtBoxLabel:ZaMsg.NAD_zimbraPasswordMinUpperCaseChars+":", labelLocation:_LEFT_, 
									textFieldCssClass:"admin_xform_number_input"
								},
								{ref:ZaAccount.A_zimbraPasswordMinLowerCaseChars,
									type:_SUPERWIZ_TEXTFIELD_, 
									resetToSuperLabel:ZaMsg.NAD_ResetToCOS, 
									msgName:ZaMsg.NAD_zimbraPasswordMinLowerCaseChars,
									txtBoxLabel:ZaMsg.NAD_zimbraPasswordMinLowerCaseChars+":", labelLocation:_LEFT_, 
									textFieldCssClass:"admin_xform_number_input"
								},
								{ref:ZaAccount.A_zimbraPasswordMinPunctuationChars,  
									type:_SUPERWIZ_TEXTFIELD_, 
									resetToSuperLabel:ZaMsg.NAD_ResetToCOS, 
									msgName:ZaMsg.NAD_zimbraPasswordMinPunctuationChars,
									txtBoxLabel:ZaMsg.NAD_zimbraPasswordMinPunctuationChars+":", labelLocation:_LEFT_, 
									textFieldCssClass:"admin_xform_number_input"
								},
								{ref:ZaAccount.A_zimbraPasswordMinNumericChars, 
									type:_SUPERWIZ_TEXTFIELD_, 
									resetToSuperLabel:ZaMsg.NAD_ResetToCOS, 
									msgName:ZaMsg.NAD_zimbraPasswordMinNumericChars,
									txtBoxLabel:ZaMsg.NAD_zimbraPasswordMinNumericChars+":", labelLocation:_LEFT_, 
									textFieldCssClass:"admin_xform_number_input"
								},
																
								{ref:ZaAccount.A_zimbraMinPwdAge, type:_SUPERWIZ_TEXTFIELD_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_passMinAge,txtBoxLabel:ZaMsg.NAD_passMinAge+":", labelLocation:_LEFT_, textFieldCssClass:"admin_xform_number_input"},
								{ref:ZaAccount.A_zimbraMaxPwdAge, type:_SUPERWIZ_TEXTFIELD_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_passMaxAge,txtBoxLabel:ZaMsg.NAD_passMaxAge+":", labelLocation:_LEFT_, textFieldCssClass:"admin_xform_number_input"},
								{ref:ZaAccount.A_zimbraEnforcePwdHistory, type:_SUPERWIZ_TEXTFIELD_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_passEnforceHistory,txtBoxLabel:ZaMsg.NAD_passEnforceHistory+":", labelLocation:_LEFT_, textFieldCssClass:"admin_xform_number_input"}
							]
						},
						{type:_ZAWIZ_TOP_GROUPER_, id:"password_lockout_settings",colSizes:["250px","200px","150px"],numCols:3,
							label:ZaMsg.NAD_FailedLoginGrouper,							
							items :[

								{ref:ZaAccount.A_zimbraPasswordLockoutEnabled, type:_SUPER_WIZ_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, 
									msgName:ZaMsg.NAD_zimbraPasswordLockoutEnabled,checkBoxLabel:ZaMsg.NAD_zimbraPasswordLockoutEnabled, 
									labelCssStyle:"width:250px;", trueValue:"TRUE", falseValue:"FALSE"
								},
								{ref:ZaAccount.A_zimbraPasswordLockoutMaxFailures, type:_SUPERWIZ_TEXTFIELD_, 
									relevant: "ZaAccountXFormView.isPasswordLockoutEnabled.call(this)",
								 	relevantBehavior: _DISABLE_,
									txtBoxLabel:ZaMsg.NAD_zimbraPasswordLockoutMaxFailures+":",
									toolTipContent:ZaMsg.NAD_zimbraPasswordLockoutMaxFailuresSub,
									msgName:ZaMsg.NAD_zimbraPasswordLockoutMaxFailures,
									labelLocation:_LEFT_, 
									textFieldCssClass:"admin_xform_number_input", 
									resetToSuperLabel:ZaMsg.NAD_ResetToCOS,
									labelCssStyle:"width:250px;"
								},
								{ref:ZaAccount.A_zimbraPasswordLockoutDuration, type:_SUPERWIZ_LIFETIME_, 
									relevant: "ZaAccountXFormView.isPasswordLockoutEnabled.call(this)",
									relevantBehavior: _DISABLE_,
									txtBoxLabel:ZaMsg.NAD_zimbraPasswordLockoutDuration+":",
									toolTipContent:ZaMsg.NAD_zimbraPasswordLockoutDurationSub,
									msgName:ZaMsg.NAD_zimbraPasswordLockoutDuration,
									textFieldCssClass:"admin_xform_number_input", 
									resetToSuperLabel:ZaMsg.NAD_ResetToCOS
								},
								{ref:ZaAccount.A_zimbraPasswordLockoutFailureLifetime, type:_SUPERWIZ_LIFETIME_, 
									relevant: "ZaAccountXFormView.isPasswordLockoutEnabled.call(this)",
									relevantBehavior: _DISABLE_,								
									txtBoxLabel:ZaMsg.NAD_zimbraPasswordLockoutFailureLifetime+":",
									toolTipContent:ZaMsg.NAD_zimbraPasswordLockoutFailureLifetimeSub,
									msgName:ZaMsg.NAD_zimbraPasswordLockoutFailureLifetime,
									textFieldCssClass:"admin_xform_number_input", 
									resetToSuperLabel:ZaMsg.NAD_ResetToCOS,
									labelCssStyle:"white-space:normal;",
									nowrap:false,labelWrap:true
								}								
							]
						},
						{type:_ZAWIZ_TOP_GROUPER_, colSizes:["auto"],numCols:1,
							label:ZaMsg.NAD_TimeoutGrouper,						
							items: [
								{ref:ZaAccount.A_zimbraAuthTokenLifetime,
									type:_SUPERWIZ_LIFETIME_, 
									resetToSuperLabel:ZaMsg.NAD_ResetToCOS, 
									msgName:ZaMsg.NAD_AuthTokenLifetime,
									txtBoxLabel:ZaMsg.NAD_AuthTokenLifetime+":"},								
								{ref:ZaAccount.A_zimbraMailIdleSessionTimeout, 
									type:_SUPERWIZ_LIFETIME_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, 
									msgName:ZaMsg.NAD_MailIdleSessionTimeout,
									txtBoxLabel:ZaMsg.NAD_MailIdleSessionTimeout+":"},																
								{ref:ZaAccount.A_zimbraMailMessageLifetime, type:_SUPERWIZ_LIFETIME1_, 
									resetToSuperLabel:ZaMsg.NAD_ResetToCOS, 
									msgName:ZaMsg.NAD_MailMessageLifetime,
									txtBoxLabel:ZaMsg.NAD_MailMessageLifetime+":"},
								{ref:ZaAccount.A_zimbraMailTrashLifetime, type:_SUPERWIZ_LIFETIME1_, 
									resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_MailTrashLifetime,
									txtBoxLabel:ZaMsg.NAD_MailTrashLifetime+":"},
								{ref:ZaAccount.A_zimbraMailSpamLifetime, type:_SUPERWIZ_LIFETIME1_, 
									resetToSuperLabel:ZaMsg.NAD_ResetToCOS, 
									msgName:ZaMsg.NAD_MailSpamLifetime,
									txtBoxLabel:ZaMsg.NAD_MailSpamLifetime}
							]
						}					
			
					]
					});									
	}
	xFormObject.items = [
			{type:_OUTPUT_, colSpan:2, align:_CENTER_, valign:_TOP_, ref:ZaModel.currentStep, choices:this.stepChoices},
			{type:_SEPARATOR_, align:_CENTER_, valign:_TOP_},
			{type:_SPACER_,  align:_CENTER_, valign:_TOP_},
			{type:_SWITCH_, width:650, align:_LEFT_, valign:_TOP_, items:cases}
		];
};
ZaXDialog.XFormModifiers["ZaNewAccountXWizard"].push(ZaNewAccountXWizard.myXFormModifier);