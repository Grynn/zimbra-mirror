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
	ZaXWizardDialog.call(this, parent, app, null, ZaMsg.NCD_NewAccTitle, "550px", "300px","ZaNewAccountXWizard");
	this.accountStatusChoices = [
		{value:ZaAccount.ACCOUNT_STATUS_ACTIVE, label:ZaAccount._ACCOUNT_STATUS[ZaAccount.ACCOUNT_STATUS_ACTIVE]}, 
		{value:ZaAccount.ACCOUNT_STATUS_CLOSED, label:ZaAccount._ACCOUNT_STATUS[ZaAccount.ACCOUNT_STATUS_CLOSED]},
		{value:ZaAccount.ACCOUNT_STATUS_LOCKED, label: ZaAccount._ACCOUNT_STATUS[ZaAccount.ACCOUNT_STATUS_LOCKED]},
		{value:ZaAccount.ACCOUNT_STATUS_MAINTENANCE, label:ZaAccount._ACCOUNT_STATUS[ZaAccount.ACCOUNT_STATUS_MAINTENANCE]}
	];	
//	this.accountStatusChoices = [ZaAccount.ACCOUNT_STATUS_ACTIVE, ZaAccount.ACCOUNT_STATUS_MAINTENANCE, ZaAccount.ACCOUNT_STATUS_LOCKED, ZaAccount.ACCOUNT_STATUS_CLOSED];		
	this.stepChoices = [
		{label:ZaMsg.TABT_GeneralPage, value:1},
		{label:ZaMsg.TABT_ContactInfo, value:2}
	];
	
	if(ZaSettings.ACCOUNTS_ALIASES_ENABLED)
		this.stepChoices.push({value:3, label:ZaMsg.TABT_Aliases});

	if(ZaSettings.ACCOUNTS_FORWARDING_ENABLED)
		this.stepChoices.push({value:4, label:ZaMsg.TABT_Forwarding});

	if(ZaSettings.ACCOUNTS_FEATURES_ENABLED)
		this.stepChoices.push({value:5, label:ZaMsg.TABT_Features});
					
	if(ZaSettings.ACCOUNTS_PREFS_ENABLED)
		this.stepChoices.push({value:6, label:ZaMsg.TABT_Preferences});

	if(ZaSettings.SKIN_PREFS_ENABLED) {
		this.stepChoices.push({value:7, label:ZaMsg.TABT_Themes});
	}
	
	if(ZaSettings.ZIMLETS_ENABLED) {
		this.stepChoices.push({value:8, label:ZaMsg.TABT_Zimlets});
	}
		
	if(ZaSettings.ACCOUNTS_ADVANCED_ENABLED)
		this.stepChoices.push({value:9, label:ZaMsg.TABT_Advanced});

	this._lastStep = this.stepChoices.length;


	this.initForm(ZaAccount.myXModel,this.getMyXForm());	
//    DBG.timePt(AjxDebug.PERF, "finished initForm");
   
	this._localXForm.setController(this._app);	
	this._localXForm.addListener(DwtEvent.XFORMS_FORM_DIRTY_CHANGE, new AjxListener(this, ZaNewAccountXWizard.prototype.handleXFormChange));
	this._localXForm.addListener(DwtEvent.XFORMS_VALUE_ERROR, new AjxListener(this, ZaNewAccountXWizard.prototype.handleXFormChange));	
	this._helpURL = ZaNewAccountXWizard.helpURL;
}


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
		var _tmpSkinMap = {};
		var skins = entry.attrs[ZaAccount.A_zimbraAvailableSkin];
		_tmpSkins = [];
		if(skins == null) {
			skins = [];
		} else if (AjxUtil.isString(skins))	 {
			skins = [skins];
		}
		
		for(var i=0; i<skins.length; i++) {
			var skin = skins[i];
			_tmpSkins[i] = new String(skin);
			_tmpSkins[i].id = "id_"+skin;
			_tmpSkinMap[skin] = _tmpSkins[i];		
		}
		this._containedObject.attrs[ZaAccount.A_zimbraAvailableSkin] = _tmpSkins;
		
		//convert strings to objects
		var skins = this._app.getInstalledSkins();
		var _tmpSkins = [];
		if(skins == null) {
			skins = [];
		} else if (AjxUtil.isString(skins))	 {
			skins = [skins];
		}
		
		for(var i=0; i<skins.length; i++) {
			var skin = skins[i];
			if(_tmpSkinMap[skin])		
				continue;
				
			_tmpSkins[i] = new String(skin);
			_tmpSkins[i].id = "id_"+skin;
		}
		this._containedObject[ZaAccount.A_zimbraInstalledSkinPool] = _tmpSkins;
		
		//convert strings to objects
		var skins = this._containedObject.cos.attrs[ZaAccount.A_zimbraAvailableSkin];
		_tmpSkins = [];
		if(skins == null) {
			skins = [];
		} else if (AjxUtil.isString(skins))	 {
			skins = [skins];
		}
		
		for(var i=0; i<skins.length; i++) {
			var skin = skins[i];
			_tmpSkins[i] = new String(skin);
			_tmpSkins[i].id = "id_"+skin;
		}
		this._containedObject.cos.attrs[ZaAccount.A_zimbraAvailableSkin] = _tmpSkins;	
	}
	if(ZaSettings.ZIMLETS_ENABLED) {
		var zimlets = entry.attrs[ZaAccount.A_zimbraZimletAvailableZimlets];
		if(zimlets != null && zimlets != "") {
			_tmpZimlets = [];
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
		var zimlets = this._containedObject.cos.attrs[ZaCos.A_zimbraZimletAvailableZimlets];
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
		this._containedObject.cos.attrs[ZaCos.A_zimbraZimletAvailableZimlets] = _tmpZimlets;
					
		//convert strings to objects
		var zimlets = ZaZimlet.getAll(this._app, "extension");
		var _tmpZimlets = [];
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
	


	var case1 = {type:_CASE_, numCols:1, relevant:"instance[ZaModel.currentStep] == 1", align:_LEFT_, valign:_TOP_};
	var case1Items = [{ref:ZaAccount.A_name, type:_EMAILADDR_, msgName:ZaMsg.NAD_AccountName,label:ZaMsg.NAD_AccountName,
						labelLocation:_LEFT_, 
						onChange: function(value, event, form) {	
								//set the cos if domain changed
								ZaAccount.setEmailChanged.call (this, value, form) ;																										
								this.setInstanceValue(value);		
							}						
						}];
	if(ZaSettings.COSES_ENABLED) {
		case1Items.push(
			{ref:ZaAccount.A_COSId, type:_OSELECT1_, editable:true, msgName:ZaMsg.NAD_ClassOfService,
				label:ZaMsg.NAD_ClassOfService, labelLocation:_LEFT_, editable:true,
				choices:this._app.getCosListChoices(), onChange:ZaNewAccountXWizard.onCOSChanged
			}
		);

	}
	case1Items.push({ref:ZaAccount.A_password, type:_SECRET_, msgName:ZaMsg.NAD_Password,label:ZaMsg.NAD_Password, labelLocation:_LEFT_, cssClass:"admin_xform_name_input"});
	case1Items.push({ref:ZaAccount.A2_confirmPassword, type:_SECRET_, msgName:ZaMsg.NAD_ConfirmPassword,label:ZaMsg.NAD_ConfirmPassword, labelLocation:_LEFT_, cssClass:"admin_xform_name_input"});														
	case1Items.push({ref:ZaAccount.A_zimbraPasswordMustChange,align:_LEFT_, type:_CHECKBOX_,msgName:ZaMsg.NAD_MustChangePwd,label:ZaMsg.NAD_MustChangePwd+":",labelLocation:_LEFT_, trueValue:"TRUE", falseValue:"FALSE",labelCssClass:"xform_label"});
	case1Items.push({ref:ZaAccount.A_isAdminAccount,labelCssClass:"xform_label", type:_CHECKBOX_, 
								msgName:ZaMsg.NAD_IsAdmin,label:ZaMsg.NAD_IsAdmin,labelLocation:_LEFT_, 
								align:_LEFT_,
								trueValue:"TRUE", falseValue:"FALSE",relevantBehavior:_HIDE_,
								labelCssClass:"xform_label"
					});
	case1Items.push({ref:ZaAccount.A_zimbraHideInGal,align:_LEFT_, type:_CHECKBOX_,msgName:ZaMsg.NAD_zimbraHideInGal,label:ZaMsg.NAD_zimbraHideInGal,labelLocation:_LEFT_, trueValue:"TRUE", falseValue:"FALSE",labelCssClass:"xform_label"});
	case1Items.push({ref:ZaAccount.A_firstName, type:_TEXTFIELD_, msgName:ZaMsg.NAD_FirstName,label:ZaMsg.NAD_FirstName, labelLocation:_LEFT_, cssClass:"admin_xform_name_input",
							elementChanged: function(elementValue,instanceValue, event) {
								if(this.getInstance()[ZaAccount.A2_autodisplayname]=="TRUE") {
									ZaAccountXFormView.generateDisplayName(this.getInstance(), elementValue, this.getInstance().attrs[ZaAccount.A_lastName],this.getInstance().attrs[ZaAccount.A_initials] );
								}
								this.getForm().itemChanged(this, elementValue, event);
							}
						});
	case1Items.push({ref:ZaAccount.A_initials, type:_TEXTFIELD_, msgName:ZaMsg.NAD_Initials,label:ZaMsg.NAD_Initials, labelLocation:_LEFT_, cssClass:"admin_xform_name_input",
							elementChanged: function(elementValue,instanceValue, event) {
								if(this.getInstance()[ZaAccount.A2_autodisplayname]=="TRUE") {
									ZaAccountXFormView.generateDisplayName(this.getInstance(), this.getInstance().attrs[ZaAccount.A_firstName], this.getInstance().attrs[ZaAccount.A_lastName],elementValue);
								}
								this.getForm().itemChanged(this, elementValue, event);
							}
						});	
	case1Items.push({ref:ZaAccount.A_lastName, type:_TEXTFIELD_, msgName:ZaMsg.NAD_LastName,label:ZaMsg.NAD_LastName, labelLocation:_LEFT_, required:true, cssClass:"admin_xform_name_input",
							elementChanged: function(elementValue,instanceValue, event) {
								if(this.getInstance()[ZaAccount.A2_autodisplayname]=="TRUE") {
									ZaAccountXFormView.generateDisplayName(this.getInstance(), this.getInstance().attrs[ZaAccount.A_firstName], elementValue ,this.getInstance().attrs[ZaAccount.A_initials]);
								}
								this.getForm().itemChanged(this, elementValue, event);
							}
						});
	case1Items.push({type:_GROUP_, numCols:3, nowrap:true,  msgName:ZaMsg.NAD_DisplayName,label:ZaMsg.NAD_DisplayName+":", labelLocation:_LEFT_,
							items: [
								{ref:ZaAccount.A_displayname, type:_TEXTFIELD_,	cssClass:"admin_xform_name_input",  label:null,
									relevant:"instance[ZaAccount.A2_autodisplayname] == \"FALSE\"",
									relevantBehavior:_DISABLE_
								},
								{ref:ZaAccount.A2_autodisplayname, type:_CHECKBOX_, msgName:ZaMsg.NAD_Auto,label:ZaMsg.NAD_Auto,labelLocation:_RIGHT_,trueValue:"TRUE", falseValue:"FALSE",
									elementChanged: function(elementValue,instanceValue, event) {
										if(elementValue=="TRUE") {
											if(ZaAccountXFormView.generateDisplayName(this.getInstance(), this.getInstance().attrs[ZaAccount.A_firstName], this.getInstance().attrs[ZaAccount.A_lastName],this.getInstance().attrs[ZaAccount.A_initials])) {
												this.getForm().itemChanged(this, elementValue, event);
											}
										}
										this.getForm().itemChanged(this, elementValue, event);
									}
								}
							]
						});
	case1Items.push({ref:ZaAccount.A_accountStatus, type:_OSELECT1_, msgName:ZaMsg.NAD_AccountStatus, editable:false, label:ZaMsg.NAD_AccountStatus, labelLocation:_LEFT_, choices:this.accountStatusChoices});
	case1Items.push({ref:ZaAccount.A_description, type:_INPUT_, msgName:ZaMsg.NAD_Description,label:ZaMsg.NAD_Description, labelLocation:_LEFT_, cssClass:"admin_xform_name_input"});
	if(ZaSettings.SERVERS_ENABLED) {
		case1Items.push({type:_GROUP_, numCols:3, nowrap:true, label:ZaMsg.NAD_MailServer, labelLocation:_LEFT_,
							items: [
								{ ref: ZaAccount.A_mailHost, type: _OSELECT1_, label: null, editable:false, choices: this._app.getServerListChoices2(), 
									relevant:"instance[ZaAccount.A2_autoMailServer]==\"FALSE\" && form.getController().getServerListChoices2().getChoices().values.length != 0",
									relevantBehavior:_DISABLE_
							  	},
								{ref:ZaAccount.A2_autoMailServer, type:_CHECKBOX_, msgName:ZaMsg.NAD_Auto,label:ZaMsg.NAD_Auto,labelLocation:_RIGHT_,trueValue:"TRUE", falseValue:"FALSE"}
							]
						});
	}
	case1.items = case1Items;
	cases.push(case1);
	var case2={type:_CASE_, numCols:1, relevant:"instance[ZaModel.currentStep] == 2",
					items: [
						{ref:ZaAccount.A_telephoneNumber, type:_TEXTFIELD_, msgName:ZaMsg.NAD_telephoneNumber,label:ZaMsg.NAD_telephoneNumber, labelLocation:_LEFT_, width:150},
						{ref:ZaAccount.A_company, type:_TEXTFIELD_, msgName:ZaMsg.NAD_company,label:ZaMsg.NAD_company, labelLocation:_LEFT_, width:150},
						{ref:ZaAccount.A_orgUnit, type:_TEXTFIELD_, msgName:ZaMsg.NAD_orgUnit,label:ZaMsg.NAD_orgUnit, labelLocation:_LEFT_, width:150},														
						{ref:ZaAccount.A_office, type:_TEXTFIELD_, msgName:ZaMsg.NAD_office,label:ZaMsg.NAD_office, labelLocation:_LEFT_, width:150},
						{ref:ZaAccount.A_street, type:_TEXTFIELD_, msgName:ZaMsg.NAD_street,label:ZaMsg.NAD_street, labelLocation:_LEFT_, width:150},
						{ref:ZaAccount.A_city, type:_TEXTFIELD_, msgName:ZaMsg.NAD_city,label:ZaMsg.NAD_city, labelLocation:_LEFT_, width:150},
						{ref:ZaAccount.A_state, type:_TEXTFIELD_, msgName:ZaMsg.NAD_state,label:ZaMsg.NAD_state, labelLocation:_LEFT_, width:150},
						{ref:ZaAccount.A_zip, type:_TEXTFIELD_, msgName:ZaMsg.NAD_zip,label:ZaMsg.NAD_zip, labelLocation:_LEFT_, width:150},
						{ref:ZaAccount.A_country, type:_TEXTFIELD_, msgName:ZaMsg.NAD_country,label:ZaMsg.NAD_country, labelLocation:_LEFT_, width:150}
					]
				};
	cases.push(case2);

	if(ZaSettings.ACCOUNTS_ALIASES_ENABLED) {
		cases.push({type:_CASE_, numCols:1, relevant:"instance[ZaModel.currentStep] == 3",
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
	if(ZaSettings.ACCOUNTS_FORWARDING_ENABLED) {
		cases.push({type:_CASE_, numCols:2, relevant:"instance[ZaModel.currentStep] == 4",
					items: [
						{ref:ZaAccount.A_zimbraFeatureMailForwardingEnabled, type:_SUPER_CHECKBOX_,
							resetToSuperLabel:ZaMsg.NAD_ResetToCOS,
							msgName:ZaMsg.NAD_zimbraFeatureMailForwardingEnabled,
							label:ZaMsg.NAD_zimbraFeatureMailForwardingEnabled,labelLocation:_LEFT_, 
							trueValue:"TRUE", falseValue:"FALSE",labelCssClass:"xform_label"
						},
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
		cases.push({type:_CASE_,id:"account_form_features_step", relevant:"instance[ZaModel.currentStep] == 5",
					items: [
						{ref:ZaAccount.A_zimbraFeatureContactsEnabled,labelCssStyle:"width:190px;", type:_SUPER_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_FeatureContactsEnabled,label:ZaMsg.NAD_FeatureContactsEnabled, labelLocation:_LEFT_, trueValue:"TRUE", falseValue:"FALSE"},							
						{ref:ZaAccount.A_zimbraFeatureCalendarEnabled,labelCssStyle:"width:190px;",type:_SUPER_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_FeatureCalendarEnabled,label:ZaMsg.NAD_FeatureCalendarEnabled, labelLocation:_LEFT_, trueValue:"TRUE", falseValue:"FALSE"},														
						{ref:ZaAccount.A_zimbraFeatureTaggingEnabled,labelCssStyle:"width:190px;", type:_SUPER_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_FeatureTaggingEnabled,label:ZaMsg.NAD_FeatureTaggingEnabled, labelLocation:_LEFT_, trueValue:"TRUE", falseValue:"FALSE"},
						{ref:ZaAccount.A_zimbraFeatureAdvancedSearchEnabled,labelCssStyle:"width:190px;", type:_SUPER_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_FeatureAdvancedSearchEnabled,label:ZaMsg.NAD_FeatureAdvancedSearchEnabled, labelLocation:_LEFT_, trueValue:"TRUE", falseValue:"FALSE"},
						{ref:ZaAccount.A_zimbraFeatureSavedSearchesEnabled,labelCssStyle:"width:190px;", type:_SUPER_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_FeatureSavedSearchesEnabled,label:ZaMsg.NAD_FeatureSavedSearchesEnabled, labelLocation:_LEFT_, trueValue:"TRUE", falseValue:"FALSE"},
						{ref:ZaAccount.A_zimbraFeatureConversationsEnabled,labelCssStyle:"width:190px;", type:_SUPER_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_FeatureConversationsEnabled,label:ZaMsg.NAD_FeatureConversationsEnabled, labelLocation:_LEFT_, trueValue:"TRUE", falseValue:"FALSE"},
						{ref:ZaAccount.A_zimbraFeatureChangePasswordEnabled,labelCssStyle:"width:190px;", type:_SUPER_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_FeatureChangePasswordEnabled,label:ZaMsg.NAD_FeatureChangePasswordEnabled, labelLocation:_LEFT_, trueValue:"TRUE", falseValue:"FALSE"},
						{ref:ZaAccount.A_zimbraFeatureInitialSearchPreferenceEnabled,labelCssStyle:"width:190px;", type:_SUPER_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_FeatureInitialSearchPreferenceEnabled,label:ZaMsg.NAD_FeatureInitialSearchPreferenceEnabled, labelLocation:_LEFT_, trueValue:"TRUE", falseValue:"FALSE"},
						{ref:ZaAccount.A_zimbraFeatureFiltersEnabled,labelCssStyle:"width:190px;", type:_SUPER_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_FeatureFiltersEnabled,label:ZaMsg.NAD_FeatureFiltersEnabled, labelLocation:_LEFT_, trueValue:"TRUE", falseValue:"FALSE"},
						{ref:ZaAccount.A_zimbraFeatureHtmlComposeEnabled,labelCssStyle:"width:190px;", type:_SUPER_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_zimbraFeatureHtmlComposeEnabled,label:ZaMsg.NAD_zimbraFeatureHtmlComposeEnabled, labelLocation:_LEFT_, trueValue:"TRUE", falseValue:"FALSE"},							
						{ref:ZaAccount.A_zimbraFeatureGalEnabled,labelCssStyle:"width:190px;", type:_SUPER_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_FeatureGalEnabled,label:ZaMsg.NAD_FeatureGalEnabled, labelLocation:_LEFT_, trueValue:"TRUE", falseValue:"FALSE"},
						{ref:ZaAccount.A_zimbraImapEnabled,labelCssStyle:"width:190px;", type:_SUPER_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_zimbraImapEnabled,label:ZaMsg.NAD_zimbraImapEnabled, labelLocation:_LEFT_, trueValue:"TRUE", falseValue:"FALSE"},
						{ref:ZaAccount.A_zimbraPop3Enabled,labelCssStyle:"width:190px;", type:_SUPER_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_zimbraPop3Enabled,label:ZaMsg.NAD_zimbraPop3Enabled, labelLocation:_LEFT_, trueValue:"TRUE", falseValue:"FALSE"},								
						{ref:ZaAccount.A_zimbraFeatureSharingEnabled,labelCssStyle:"width:190px;", type:_SUPER_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_zimbraFeatureSharingEnabled,label:ZaMsg.NAD_zimbraFeatureSharingEnabled, labelLocation:_LEFT_, trueValue:"TRUE", falseValue:"FALSE"},
						{ref:ZaAccount.A_zimbraFeatureNotebookEnabled,labelCssStyle:"width:190px;", type:_SUPER_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_zimbraFeatureNotebookEnabled,label:ZaMsg.NAD_zimbraFeatureNotebookEnabled, labelLocation:_LEFT_, trueValue:"TRUE", falseValue:"FALSE"},
						{ref:ZaAccount.A_zimbraFeatureGalAutoCompleteEnabled,labelCssStyle:"width:190px;", type:_SUPER_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_zimbraFeatureGalAutoCompleteEnabled,label:ZaMsg.NAD_zimbraFeatureGalAutoCompleteEnabled, labelLocation:_LEFT_, trueValue:"TRUE", falseValue:"FALSE"},												
						{ref:ZaAccount.A_zimbraFeatureOutOfOfficeReplyEnabled,labelCssStyle:"width:190px;", type:_SUPER_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_zimbraFeatureOutOfOfficeReplyEnabled,label:ZaMsg.NAD_zimbraFeatureOutOfOfficeReplyEnabled, labelLocation:_LEFT_, trueValue:"TRUE", falseValue:"FALSE"},
						{ref:ZaAccount.A_zimbraFeatureNewMailNotificationEnabled,labelCssStyle:"width:190px;", type:_SUPER_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_zimbraFeatureNewMailNotificationEnabled,label:ZaMsg.NAD_zimbraFeatureNewMailNotificationEnabled, labelLocation:_LEFT_, trueValue:"TRUE", falseValue:"FALSE"},
						{ref:ZaAccount.A_zimbraFeatureSkinChangeEnabled,labelCssStyle:"width:190px;", type:_SUPER_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_zimbraFeatureSkinChangeEnabled,label:ZaMsg.NAD_zimbraFeatureSkinChangeEnabled, labelLocation:_LEFT_, trueValue:"TRUE", falseValue:"FALSE"}						
					]
				});
	}	
	if(ZaSettings.ACCOUNTS_PREFS_ENABLED) {
		var prefItems = [
						{ref:ZaAccount.A_prefSaveToSent,labelCssStyle:"width:190px;", type:_SUPER_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_prefSaveToSent,label:ZaMsg.NAD_prefSaveToSent, labelLocation:_LEFT_, trueValue:"TRUE", falseValue:"FALSE"},
						{ref:ZaAccount.A_zimbraPrefMessageViewHtmlPreferred,labelCssStyle:"width:190px;", type:_SUPER_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_zimbraPrefMessageViewHtmlPreferred,label:ZaMsg.NAD_zimbraPrefMessageViewHtmlPreferred, labelLocation:_LEFT_, trueValue:"TRUE", falseValue:"FALSE"},
						{ref:ZaAccount.A_zimbraPrefComposeInNewWindow,labelCssStyle:"width:190px;", type:_SUPER_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_zimbraPrefComposeInNewWindow,label:ZaMsg.NAD_zimbraPrefComposeInNewWindow, labelLocation:_LEFT_, trueValue:"TRUE", falseValue:"FALSE"},							
						{ref:ZaAccount.A_zimbraPrefForwardReplyInOriginalFormat,labelCssStyle:"width:190px;", type:_SUPER_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_zimbraPrefForwardReplyInOriginalFormat,label:ZaMsg.NAD_zimbraPrefForwardReplyInOriginalFormat, labelLocation:_LEFT_, trueValue:"TRUE", falseValue:"FALSE"},														
						{ref:ZaAccount.A_zimbraPrefComposeFormat,
							labelCssStyle:"width:190px;", 
							type:_SUPER_SELECT1_, 
							resetToSuperLabel:ZaMsg.NAD_ResetToCOS, 
							msgName:ZaMsg.NAD_zimbraPrefComposeFormat,
							label:ZaMsg.NAD_zimbraPrefComposeFormat, 
							labelLocation:_LEFT_
						},							
						{ref:ZaAccount.A_zimbraPrefAutoAddAddressEnabled,labelCssStyle:"width:190px;", type:_SUPER_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_zimbraPrefAutoAddAddressEnabled,label:ZaMsg.NAD_zimbraPrefAutoAddAddressEnabled, labelLocation:_LEFT_, trueValue:"TRUE", falseValue:"FALSE"},							
						{ref:ZaAccount.A_zimbraPrefGalAutoCompleteEnabled, labelCssStyle:"width:190px;", type:_SUPER_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_zimbraPrefGalAutoCompleteEnabled,label:ZaMsg.NAD_zimbraPrefGalAutoCompleteEnabled, labelLocation:_LEFT_, trueValue:"TRUE", falseValue:"FALSE"},																				
						{type:_SEPARATOR_},
						{ref:ZaAccount.A_zimbraPrefGroupMailBy,labelCssStyle:"width:190px;", type:_SUPER_SELECT1_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_zimbraPrefGroupMailBy,label:ZaMsg.NAD_zimbraPrefGroupMailBy, labelLocation:_LEFT_},							
						{ref:ZaAccount.A_zimbraPrefContactsPerPage,
							labelCssStyle:"width:190px;", type:_SUPER_TEXTFIELD_, 
							msgName:ZaMsg.NAD_PrefContactsPerPage,label:ZaMsg.NAD_PrefContactsPerPage+":", 
							labelLocation:_LEFT_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, 
							textFieldCssClass:"admin_xform_number_input", 
							valueLabel:null, 
							anchorCssStyle:"width:100px"
						},							
						{ref:ZaAccount.A_zimbraPrefMailItemsPerPage,labelCssStyle:"width:190px;", type:_SUPER_TEXTFIELD_, msgName:ZaMsg.NAD_zimbraPrefMailItemsPerPage,label:ZaMsg.NAD_zimbraPrefMailItemsPerPage, labelLocation:_LEFT_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, textFieldCssClass:"admin_xform_number_input", valueLabel:null},
						{ref:ZaAccount.A_zimbraPrefMailInitialSearch,labelCssStyle:"width:190px;", type:_SUPER_TEXTFIELD_, msgName:ZaMsg.NAD_zimbraPrefMailInitialSearch,label:ZaMsg.NAD_zimbraPrefMailInitialSearch, labelLocation:_LEFT_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, textFieldCssClass:"admin_xform_name_input", valueLabel:null},
						{ref:ZaAccount.A_zimbraPrefMailPollingInterval,labelCssStyle:"width:190px;", type:_SUPER_LIFETIME_, msgName:ZaMsg.NAD_zimbraPrefMailPollingInterval,label:ZaMsg.NAD_zimbraPrefMailPollingInterval+":", labelLocation:_LEFT_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS},						
						{ref:ZaAccount.A_zimbraPrefCalendarApptReminderWarningTime,labelCssStyle:"width:190px;", type:_SUPER_SELECT1_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_zimbraPrefCalendarApptReminderWarningTime,label:ZaMsg.NAD_zimbraPrefCalendarApptReminderWarningTime+":", labelLocation:_LEFT_},							
						{ref:ZaAccount.A_zimbraPrefShowSearchString,labelCssStyle:"width:190px;", type:_SUPER_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_zimbraPrefShowSearchString,label:ZaMsg.NAD_zimbraPrefShowSearchString, labelLocation:_LEFT_, trueValue:"TRUE", falseValue:"FALSE"},
						{ref:ZaAccount.A_zimbraPrefCalendarAlwaysShowMiniCal,labelCssStyle:"width:190px;", type:_SUPER_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_alwaysShowMiniCal,label:ZaMsg.NAD_alwaysShowMiniCal, labelLocation:_LEFT_, trueValue:"TRUE", falseValue:"FALSE"},							
						{ref:ZaAccount.A_zimbraPrefCalendarUseQuickAdd,labelCssStyle:"width:190px;", type:_SUPER_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_useQuickAdd,label:ZaMsg.NAD_useQuickAdd, labelLocation:_LEFT_, trueValue:"TRUE", falseValue:"FALSE"},							
						{ref:ZaAccount.A_zimbraPrefUseTimeZoneListInCalendar,labelCssStyle:"width:190px;", type:_SUPER_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_zimbraPrefUseTimeZoneListInCalendar,label:ZaMsg.NAD_zimbraPrefUseTimeZoneListInCalendar, labelLocation:_LEFT_, trueValue:"TRUE", falseValue:"FALSE"},							
						{ref:ZaAccount.A_zimbraPrefImapSearchFoldersEnabled, labelCssStyle:"width:190px;", type:_SUPER_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_zimbraPrefImapSearchFoldersEnabled,label:ZaMsg.NAD_zimbraPrefImapSearchFoldersEnabled, labelLocation:_LEFT_, trueValue:"TRUE", falseValue:"FALSE"},														
						{ref:ZaAccount.A_zimbraPrefMailLocalDeliveryDisabled,labelCssStyle:"width:190px;", type:_CHECKBOX_,msgName:ZaMsg.NAD_zimbraPrefMailLocalDeliveryDisabled,label:ZaMsg.NAD_zimbraPrefMailLocalDeliveryDisabled, labelLocation:_LEFT_, trueValue:"TRUE", falseValue:"FALSE",labelCssClass:"xform_label", align:_LEFT_},						
						{type:_SEPARATOR_},							
						{ref:ZaAccount.A_zimbraPrefNewMailNotificationEnabled,labelCssStyle:"width:190px;", type:_CHECKBOX_, msgName:ZaMsg.NAD_zimbraPrefNewMailNotificationEnabled,label:ZaMsg.NAD_zimbraPrefNewMailNotificationEnabled, labelLocation:_LEFT_, trueValue:"TRUE", falseValue:"FALSE",labelCssClass:"xform_label", align:_LEFT_},
						{ref:ZaAccount.A_zimbraPrefNewMailNotificationAddress,labelCssStyle:"width:190px;", type:_TEXTFIELD_, msgName:ZaMsg.NAD_zimbraPrefNewMailNotificationAddress,label:ZaMsg.NAD_zimbraPrefNewMailNotificationAddress, labelLocation:_LEFT_, cssClass:"admin_xform_name_input"},							
						{type:_SEPARATOR_},
						{ref:ZaAccount.A_prefMailSignatureEnabled,labelCssStyle:"width:190px;", type:_CHECKBOX_, msgName:ZaMsg.NAD_prefMailSignatureEnabled,label:ZaMsg.NAD_prefMailSignatureEnabled, labelLocation:_LEFT_, trueValue:"TRUE", falseValue:"FALSE",labelCssClass:"xform_label", align:_LEFT_},
						{ref:ZaAccount.A_zimbraPrefMailSignatureStyle,labelCssStyle:"width:190px;", type:_SUPER_SELECT1_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_zimbraPrefMailSignatureStyle,label:ZaMsg.NAD_zimbraPrefMailSignatureStyle, labelLocation:_LEFT_},
						{ref:ZaAccount.A_prefMailSignature,labelCssStyle:"width:190px;", type:_TEXTAREA_, msgName:ZaMsg.NAD_prefMailSignature,label:ZaMsg.NAD_prefMailSignature, labelLocation:_LEFT_, labelCssStyle: "vertical-align:top"},
						{type:_SEPARATOR_},
						{ref:ZaAccount.A_zimbraPrefOutOfOfficeReplyEnabled,labelCssStyle:"width:190px;", type:_CHECKBOX_, msgName:ZaMsg.NAD_zimbraPrefOutOfOfficeReplyEnabled,label:ZaMsg.NAD_zimbraPrefOutOfOfficeReplyEnabled, labelLocation:_LEFT_, trueValue:"TRUE", falseValue:"FALSE",labelCssClass:"xform_label", align:_LEFT_},
						{ref:ZaAccount.A_zimbraPrefOutOfOfficeReply,labelCssStyle:"width:190px;", type:_TEXTAREA_, msgName:ZaMsg.NAD_zimbraPrefOutOfOfficeReply,label:ZaMsg.NAD_zimbraPrefOutOfOfficeReply, labelLocation:_LEFT_, labelCssStyle:"vertical-align:top", cssStyle:"width:120px"},
					];
		cases.push({type:_CASE_, relevant:"instance[ZaModel.currentStep] == 6", 
					items :prefItems});
	}		

	if(ZaSettings.SKIN_PREFS_ENABLED) {
		cases.push({type:_CASE_,id:"account_form_themes_step", numCols:1, width:"100%", relevant:"instance[ZaModel.currentStep]==7", 
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
								{ref:ZaAccount.A_zimbraPrefSkin, type:_SUPER_SELECT1_, 
									resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_zimbraPrefSkin,label:ZaMsg.NAD_zimbraPrefSkin, labelLocation:_LEFT_, 
									choices:this._app.getInstalledSkins(),
									relevant:"ZaAccountXFormView.gotSkins.call(this)"}
								] 
							}							
						]
		});			
	}
	
	if(ZaSettings.SKIN_PREFS_ENABLED) {
		cases.push({type:_CASE_,id:"account_form_zimlets_step", numCols:1, width:"100%", relevant:"instance[ZaModel.currentStep]==8", 
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
		cases.push({type:_CASE_,id:"account_form_advanced_step", numCols:1, width:"100%", relevant:"instance[ZaModel.currentStep]==9", 
						items: [
							{type:_GROUP_, width:"100%", id:"account_attachment_settings",
								items :[
									{ref:ZaAccount.A_zimbraAttachmentsBlocked,labelCssStyle:"width:160px;", type:_SUPER_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_RemoveAllAttachments,label:ZaMsg.NAD_RemoveAllAttachments, labelLocation:_LEFT_, trueValue:"TRUE", falseValue:"FALSE"}
								]
							},
							{type:_SEPARATOR_, colSpan:"*"},
							{type:_GROUP_, width:"100%", 
								items: [
									{ref:ZaAccount.A_zimbraMailQuota,labelCssStyle:"width:160px;", type:_SUPER_TEXTFIELD_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_MailQuota,label:ZaMsg.NAD_MailQuota+":", labelLocation:_LEFT_, textFieldCssClass:"admin_xform_number_input", resetToSuperLabel:ZaMsg.NAD_ResetToCOS},
									{ref:ZaAccount.A_zimbraContactMaxNumEntries,labelCssStyle:"width:160px;", type:_SUPER_TEXTFIELD_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_ContactMaxNumEntries,label:ZaMsg.NAD_ContactMaxNumEntries+":", labelLocation:_LEFT_, textFieldCssClass:"admin_xform_number_input"}
								]
							},
							{type:_SEPARATOR_, colSpan:"*"},
							{type:_GROUP_, width:"100%",id:"account_password_settings",
								items: [
									{ref:ZaAccount.A_zimbraMinPwdLength,labelCssStyle:"width:160px;", type:_SUPER_TEXTFIELD_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_passMinLength,label:ZaMsg.NAD_passMinLength+":", labelLocation:_LEFT_, textFieldCssClass:"admin_xform_number_input"},
									{ref:ZaAccount.A_zimbraMaxPwdLength,labelCssStyle:"width:160px;", type:_SUPER_TEXTFIELD_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_passMaxLength,label:ZaMsg.NAD_passMaxLength+":", labelLocation:_LEFT_, textFieldCssClass:"admin_xform_number_input"},
									
									{ref:ZaAccount.A_zimbraPasswordMinUpperCaseChars,labelCssStyle:"width:160px;", type:_SUPER_TEXTFIELD_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_zimbraPasswordMinUpperCaseChars,label:ZaMsg.NAD_zimbraPasswordMinUpperCaseChars+":", labelLocation:_LEFT_, textFieldCssClass:"admin_xform_number_input"},
									{ref:ZaAccount.A_zimbraPasswordMinLowerCaseChars,labelCssStyle:"width:160px;", type:_SUPER_TEXTFIELD_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_zimbraPasswordMinLowerCaseChars,label:ZaMsg.NAD_zimbraPasswordMinLowerCaseChars+":", labelLocation:_LEFT_, textFieldCssClass:"admin_xform_number_input"},
									{ref:ZaAccount.A_zimbraPasswordMinPunctuationChars,labelCssStyle:"width:160px;", type:_SUPER_TEXTFIELD_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_zimbraPasswordMinPunctuationChars,label:ZaMsg.NAD_zimbraPasswordMinPunctuationChars+":", labelLocation:_LEFT_, textFieldCssClass:"admin_xform_number_input"},
									{ref:ZaAccount.A_zimbraPasswordMinNumericChars,labelCssStyle:"width:160px;", type:_SUPER_TEXTFIELD_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_zimbraPasswordMinNumericChars,label:ZaMsg.NAD_zimbraPasswordMinNumericChars+":", labelLocation:_LEFT_, textFieldCssClass:"admin_xform_number_input"},
									
									{ref:ZaAccount.A_zimbraMinPwdAge,labelCssStyle:"width:160px;", type:_SUPER_TEXTFIELD_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_passMinAge,label:ZaMsg.NAD_passMinAge+":", labelLocation:_LEFT_, textFieldCssClass:"admin_xform_number_input"},
									{ref:ZaAccount.A_zimbraMaxPwdAge,labelCssStyle:"width:160px;", type:_SUPER_TEXTFIELD_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_passMaxAge,label:ZaMsg.NAD_passMaxAge+":", labelLocation:_LEFT_, textFieldCssClass:"admin_xform_number_input"},
									{ref:ZaAccount.A_zimbraEnforcePwdHistory,labelCssStyle:"width:160px;", type:_SUPER_TEXTFIELD_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_passEnforceHistory,label:ZaMsg.NAD_passEnforceHistory+":", labelLocation:_LEFT_, textFieldCssClass:"admin_xform_number_input"},
									{ref:ZaAccount.A_zimbraPasswordLocked,labelCssStyle:"width:160px;", type:_SUPER_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_PwdLocked,label:ZaMsg.NAD_PwdLocked, labelLocation:_LEFT_, trueValue:"TRUE", falseValue:"FALSE"}
								]
							},
							{type:_SEPARATOR_, colSpan:"*"},							
							{type:_GROUP_, id:"password_lockout_settings",
								items :[
									{ref:ZaAccount.A_zimbraPasswordLockoutEnabled, type:_SUPER_CHECKBOX_, 
										resetToSuperLabel:ZaMsg.NAD_ResetToCOS, 
										msgName:ZaMsg.NAD_zimbraPasswordLockoutEnabled,
										label:ZaMsg.NAD_zimbraPasswordLockoutEnabled, 
										labelLocation:_LEFT_, 
										labelCssStyle:"width:190px;", trueValue:"TRUE", falseValue:"FALSE"
									},
									{ref:ZaAccount.A_zimbraPasswordLockoutMaxFailures, type:_SUPER_TEXTFIELD_, 
										relevant: "instance.attrs[ZaAccount.A_zimbraPasswordLockoutEnabled] == 'TRUE'",
									 	relevantBehavior: _DISABLE_,
										resetToSuperLabel:ZaMsg.NAD_ResetToCOS, 
										label:ZaMsg.NAD_zimbraPasswordLockoutMaxFailures+":",
										subLabel:ZaMsg.NAD_zimbraPasswordLockoutMaxFailuresSub,
										msgName:ZaMsg.NAD_zimbraPasswordLockoutMaxFailures,
										labelLocation:_LEFT_, 
										textFieldCssClass:"admin_xform_number_input", 
										resetToSuperLabel:ZaMsg.NAD_ResetToCOS,
										labelCssStyle:"width:160px;"
									},
									{ref:ZaAccount.A_zimbraPasswordLockoutDuration, type:_SUPER_LIFETIME_, 
										relevant: "instance.attrs[ZaAccount.A_zimbraPasswordLockoutEnabled] == 'TRUE'",
										relevantBehavior: _DISABLE_,
										resetToSuperLabel:ZaMsg.NAD_ResetToCOS, 
										label:ZaMsg.NAD_zimbraPasswordLockoutDuration+":",
										subLabel:ZaMsg.NAD_zimbraPasswordLockoutDurationSub,
										msgName:ZaMsg.NAD_zimbraPasswordLockoutDuration,
										labelLocation:_LEFT_, 
										textFieldCssClass:"admin_xform_number_input", 
										resetToSuperLabel:ZaMsg.NAD_ResetToCOS,
										labelCssStyle:"width:190px;"
									},
									{ref:ZaAccount.A_zimbraPasswordLockoutFailureLifetime, type:_SUPER_LIFETIME_, 
										relevant: "instance.attrs[ZaAccount.A_zimbraPasswordLockoutEnabled] == 'TRUE'",
										relevantBehavior: _DISABLE_,								
										resetToSuperLabel:ZaMsg.NAD_ResetToCOS, 
										label:ZaMsg.NAD_zimbraPasswordLockoutFailureLifetime+":",
										subLabel:ZaMsg.NAD_zimbraPasswordLockoutFailureLifetimeSub,
										msgName:ZaMsg.NAD_zimbraPasswordLockoutFailureLifetime,
										labelLocation:_LEFT_, 
										textFieldCssClass:"admin_xform_number_input", 
										resetToSuperLabel:ZaMsg.NAD_ResetToCOS,
										labelCssStyle:"width:190px;white-space:normal",
										nowrap:false,labelWrap:true
									}																		
								]
							},														
							{type:_SEPARATOR_, colSpan:"*"},							
							{type:_GROUP_, width:"100%", 
								items: [
									{ref:ZaAccount.A_zimbraAuthTokenLifetime,labelCssStyle:"width:160px;", type:_SUPER_LIFETIME_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_AuthTokenLifetime,label:ZaMsg.NAD_AuthTokenLifetime+":",labelLocation:_LEFT_},								
									{ref:ZaAccount.A_zimbraMailIdleSessionTimeout,labelCssStyle:"width:160px;", type:_SUPER_LIFETIME_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_MailIdleSessionTimeout,label:ZaMsg.NAD_MailIdleSessionTimeout+":",labelLocation:_LEFT_},								
									{ref:ZaAccount.A_zimbraMailMessageLifetime,labelCssStyle:"width:160px;", type:_SUPER_LIFETIME1_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_MailMessageLifetime,label:ZaMsg.NAD_MailMessageLifetime+":",labelLocation:_LEFT_},
									{ref:ZaAccount.A_zimbraMailTrashLifetime,labelCssStyle:"width:160px;", type:_SUPER_LIFETIME1_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_MailTrashLifetime,label:ZaMsg.NAD_MailTrashLifetime+":", labelLocation:_LEFT_},
									{ref:ZaAccount.A_zimbraMailSpamLifetime,labelCssStyle:"width:160px;", type:_SUPER_LIFETIME1_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_MailSpamLifetime,label:ZaMsg.NAD_MailSpamLifetime, labelLocation:_LEFT_}
								]
							}
						]
					});									
	}
	xFormObject.items = [
			{type:_OUTPUT_, colSpan:2, align:_CENTER_, valign:_TOP_, ref:ZaModel.currentStep, choices:this.stepChoices},
			{type:_SEPARATOR_, align:_CENTER_, valign:_TOP_},
			{type:_SPACER_,  align:_CENTER_, valign:_TOP_},
			{type:_SWITCH_, width:450, align:_LEFT_, valign:_TOP_, items:cases}
		];
};
ZaXDialog.XFormModifiers["ZaNewAccountXWizard"].push(ZaNewAccountXWizard.myXFormModifier);