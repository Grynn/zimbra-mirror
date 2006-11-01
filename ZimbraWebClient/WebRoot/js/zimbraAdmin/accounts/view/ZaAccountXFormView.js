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

/**
* This class describes a view of a single email Account
* @class ZaAccountXFormView
* @contructor
* @param parent {DwtComposite}
* @param app {ZaApp}
* @author Greg Solovyev
**/
function ZaAccountXFormView (parent, app) {
	ZaTabView.call(this, parent, app, "ZaAccountXFormView");	
	this.accountStatusChoices = [
		{value:ZaAccount.ACCOUNT_STATUS_ACTIVE, label:ZaAccount._ACCOUNT_STATUS[ZaAccount.ACCOUNT_STATUS_ACTIVE]}, 
		{value:ZaAccount.ACCOUNT_STATUS_CLOSED, label:ZaAccount._ACCOUNT_STATUS[ZaAccount.ACCOUNT_STATUS_CLOSED]},
		{value:ZaAccount.ACCOUNT_STATUS_LOCKED, label: ZaAccount._ACCOUNT_STATUS[ZaAccount.ACCOUNT_STATUS_LOCKED]},
		{value:ZaAccount.ACCOUNT_STATUS_MAINTENANCE, label:ZaAccount._ACCOUNT_STATUS[ZaAccount.ACCOUNT_STATUS_MAINTENANCE]}
	];
	this.initForm(ZaAccount.myXModel,this.getMyXForm());
}

ZaAccountXFormView.prototype = new ZaTabView();
ZaAccountXFormView.prototype.constructor = ZaAccountXFormView;
ZaTabView.XFormModifiers["ZaAccountXFormView"] = new Array();
ZaAccountXFormView.TAB_INDEX=0;
/**
* Sets the object contained in the view
* @param entry - {ZaAccount} object to display
**/
ZaAccountXFormView.prototype.setObject =
function(entry) {
	this._containedObject = new Object();
	this._containedObject.attrs = new Object();


	for (var a in entry.attrs) {
		if(entry.attrs[a] instanceof Array) {
			this._containedObject.attrs[a] = new Array();
			for(var aa in entry.attrs[a]) {
				this._containedObject.attrs[a][aa] = entry.attrs[a][aa];
			}
		} else {
			this._containedObject.attrs[a] = entry.attrs[a];
		}
	}
	this._containedObject.name = entry.name;
	if(entry.id)
		this._containedObject.id = entry.id;
	
	//add the member group
	this._containedObject[ZaAccount.A2_memberOf] = entry [ZaAccount.A2_memberOf];
	//add the memberList page information
	this._containedObject[ZaAccount.A2_directMemberList + "_offset"] = entry[ZaAccount.A2_directMemberList + "_offset"];
	this._containedObject[ZaAccount.A2_directMemberList + "_more"] = entry[ZaAccount.A2_directMemberList + "_more"];
	this._containedObject[ZaAccount.A2_indirectMemberList + "_offset"] = entry[ZaAccount.A2_indirectMemberList + "_offset"];
	this._containedObject[ZaAccount.A2_indirectMemberList + "_more"] = entry[ZaAccount.A2_indirectMemberList + "_more"];
	this._containedObject[ZaAccount.A2_nonMemberList + "_offset"] = entry[ZaAccount.A2_nonMemberList + "_offset"];
	this._containedObject[ZaAccount.A2_nonMemberList + "_more"] = entry[ZaAccount.A2_nonMemberList + "_more"];
	
	if ((typeof ZaDomainAdmin == "function")) {
		this._containedObject[ZaAccount.A2_zimbraDomainAdminMailQuotaAllowed] = entry [ZaAccount.A2_zimbraDomainAdminMailQuotaAllowed];
	}
	
					
	if(ZaSettings.COSES_ENABLED) {	
		var cosList = this._app.getCosList().getArray();
		
		/**
		* If this account does not have a COS assigned to it - assign default COS
		**/
		if(this._containedObject.attrs[ZaAccount.A_COSId]) {	
			for(var ix in cosList) {
				/**
				* Find the COS assigned to this account 
				**/
				if(cosList[ix].id == this._containedObject.attrs[ZaAccount.A_COSId]) {
					this._containedObject.cos = cosList[ix];
					break;
				}
			}
		}
		if(!this._containedObject.cos) {
			/**
			* We did not find the COS assigned to this account,
			* this means that the COS was deleted or wasn't assigned, therefore assign default COS to this account
			**/
			for(var i in cosList) {
				/**
				* Find the COS assigned to this account 
				**/
				if(cosList[i].name == "default") {
					this._containedObject.cos = cosList[i];
					this._containedObject.attrs[ZaAccount.A_COSId] = cosList[i].id;										
					break;
				}
			}
			if(!this._containedObject.cos) {
				//default COS was not found - just assign the first COS
				if(cosList && cosList.length > 0) {
					this._containedObject.cos = cosList[0];
					this._containedObject.attrs[ZaAccount.A_COSId] = cosList[0].id;					
				}
			}
		}
		if(!this._containedObject.cos) {
			this._containedObject.cos = cosList[0];
		}	
	}
	this._containedObject[ZaAccount.A2_autodisplayname] = entry[ZaAccount.A2_autodisplayname];
	this._containedObject[ZaAccount.A2_confirmPassword] = entry[ZaAccount.A2_confirmPassword];
	
	if(ZaSettings.GLOBAL_CONFIG_ENABLED) {
		this._containedObject.globalConfig = this._app.getGlobalConfig();
	}
   	
			
	if(!entry[ZaModel.currentTab])
		this._containedObject[ZaModel.currentTab] = "1";
	else
		this._containedObject[ZaModel.currentTab] = entry[ZaModel.currentTab];
	
	if(ZaSettings.SKIN_PREFS_ENABLED) {
		//convert strings to objects
		var skins = entry.attrs[ZaAccount.A_zimbraAvailableSkin];
		if(skins != null && skins != "") {
			_tmpSkins = [];
			if (AjxUtil.isString(skins))	 {
				skins = [skins];
			}
	
			for(var i=0; i<skins.length; i++) {
				var skin = skins[i];
				_tmpSkins[i] = new String(skin);
				_tmpSkins[i].id = "id_"+skin;
			}
			
			this._containedObject.attrs[ZaAccount.A_zimbraAvailableSkin] = _tmpSkins;
		} else
			this._containedObject.attrs[ZaAccount.A_zimbraAvailableSkin] =null;		

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
			_tmpSkins[i] = new String(skin);
			_tmpSkins[i].id = "id_"+skin;
		}
		this._containedObject[ZaAccount.A_zimbraInstalledSkinPool] = _tmpSkins;
		
		//convert strings to objects
		var skins;
		if(ZaSettings.COSES_ENABLED) {	
			skins = this._containedObject.cos.attrs[ZaAccount.A_zimbraAvailableSkin];
		} else {
			skins = this._containedObject.attrs[ZaAccount.A_zimbraAvailableSkin];
		}
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
		if(ZaSettings.COSES_ENABLED) {	
			this._containedObject.cos.attrs[ZaAccount.A_zimbraAvailableSkin] = _tmpSkins;
		} else {
			this._containedObject.attrs[ZaAccount.A_zimbraAvailableSkin] = _tmpSkins;
		}
	}
	if(ZaSettings.ZIMLETS_ENABLED) {
		var zimlets = entry.attrs[ZaAccount.A_zimbraZimletAvailableZimlets];
		if(zimlets != null && zimlets != "") {
			var _tmpZimlets = [];
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
		var _tmpZimlets = [];
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

ZaAccountXFormView.gotSkins = function () {
	if(!ZaSettings.SKIN_PREFS_ENABLED)
		return false;
	else 
		return ((this.parent._app.getInstalledSkins() != null) && (this.parent._app.getInstalledSkins().length > 0));
}

ZaAccountXFormView.generateDisplayName =
function (instance, firstName, lastName, initials) {
	var oldDisplayName = instance.attrs[ZaAccount.A_displayname];
	
	if(firstName)
		instance.attrs[ZaAccount.A_displayname] = firstName;
	else
		instance.attrs[ZaAccount.A_displayname] = "";
		
	if(initials) {
		instance.attrs[ZaAccount.A_displayname] += " ";
		instance.attrs[ZaAccount.A_displayname] += initials;
		instance.attrs[ZaAccount.A_displayname] += ".";
	}
	if(lastName) {
		if(instance.attrs[ZaAccount.A_displayname].length > 0)
			instance.attrs[ZaAccount.A_displayname] += " ";
			
	    instance.attrs[ZaAccount.A_displayname] += lastName;
	} 
	if(instance.attrs[ZaAccount.A_displayname] == oldDisplayName) {
		return false;
	} else {
		return true;
	}
}

ZaAccountXFormView.onCOSChanged = 
function(value, event, form) {
	var cosList = form.getController().getCosList().getArray();
	var cnt = cosList.length;
	for(var i = 0; i < cnt; i++) {
		if(cosList[i].id == value) {
			form.getInstance().cos = cosList[i];
			break;
		}
	}
	form.parent.setDirty(true);
	this.setInstanceValue(value);
	return value;
}

ZaAccountXFormView.onRepeatRemove = 
function (index, form) {
	var list = this.getInstanceValue();
	if (list == null || typeof(list) == "string" || index >= list.length || index<0) return;
	list.splice(index, 1);
	form.parent.setDirty(true);
}
/**
* This method is added to the map {@link ZaTabView#XFormModifiers}
* @param xFormObject {Object} a definition of the form. This method adds/removes/modifies xFormObject to construct
* an Account view. 
**/
ZaAccountXFormView.myXFormModifier = function(xFormObject) {	

	var domainName;
	if(ZaSettings.DOMAINS_ENABLED) {
		domainName = this._app.getGlobalConfig().attrs[ZaGlobalConfig.A_zimbraDefaultDomainName];
		if(!domainName)
			domainName = this._app.getDomainList().getArray()[0].name;
	} else 
		domainName = ZaSettings.myDomainName;

		
	var emptyAlias = " @" + domainName;
	var headerItems = [{type:_AJX_IMAGE_, src:"Person_32", label:null, rowSpan:2},{type:_OUTPUT_, ref:ZaAccount.A_displayname, label:null,cssClass:"AdminTitle", rowSpan:2}];
	if(ZaSettings.COSES_ENABLED) {
		headerItems.push({type:_OUTPUT_, ref:ZaAccount.A_COSId, labelLocation:_LEFT_, label:ZaMsg.NAD_ClassOfService, choices:this._app.getCosListChoices()});
	}
	if(ZaSettings.SERVERS_ENABLED) {
		headerItems.push({type:_OUTPUT_, ref:ZaAccount.A_mailHost, labelLocation:_LEFT_,label:ZaMsg.NAD_MailServer});
	}
	headerItems.push({type:_OUTPUT_,  ref:ZaAccount.A_accountStatus, label:ZaMsg.NAD_AccountStatus, labelLocation:_LEFT_, choices:this.accountStatusChoices});
	headerItems.push({type:_OUTPUT_, ref:ZaAccount.A_name, label:ZaMsg.NAD_Email, labelLocation:_LEFT_, required:false});
	headerItems.push({type:_OUTPUT_,  ref:ZaItem.A_zimbraId, label:ZaMsg.NAD_ZimbraID});
	headerItems.push({type:_OUTPUT_, ref:ZaAccount.A2_mbxsize, label:ZaMsg.usedQuota,
						getDisplayValue:function() {
							var val = this.getInstanceValue();
							if(!val) 
								val = "0 MB ";
							else {
								val = Number(val / 1048576).toFixed(3) + " MB ";
							}
							var quotaUsed = "";
							
							if(this.getInstance() != null)
								quotaUsed = this.getInstanceValue(ZaAccount.A2_quota);
								
							val += ZaMsg.Of + " " + quotaUsed + " MB";									
							return val;
						}
					});

	var tabChoices = new Array();
	var _tab1 = ++ZaAccountXFormView.TAB_INDEX;
	var _tab2 = ++ZaAccountXFormView.TAB_INDEX;	
	var _tab3 = ++ZaAccountXFormView.TAB_INDEX;	
	var _tab4 = ++ZaAccountXFormView.TAB_INDEX;	
	var _tab5 = ++ZaAccountXFormView.TAB_INDEX;		
	var _tab6 = ++ZaAccountXFormView.TAB_INDEX;			
	var _tab7 = ++ZaAccountXFormView.TAB_INDEX;	
	var _tab8 = ++ZaAccountXFormView.TAB_INDEX;			
	var _tab9 = ++ZaAccountXFormView.TAB_INDEX;		
	var _tab10 = ++ZaAccountXFormView.TAB_INDEX;			
	
	tabChoices.push({value:_tab1, label:ZaMsg.TABT_GeneralPage});
	tabChoices.push({value:_tab2, label:ZaMsg.TABT_ContactInfo});
	tabChoices.push({value:_tab3, label:ZaMsg.TABT_MemberOf});

	if(ZaSettings.ACCOUNTS_FEATURES_ENABLED)
		tabChoices.push({value:_tab4, label:ZaMsg.TABT_Features});
					
	if(ZaSettings.ACCOUNTS_PREFS_ENABLED)
		tabChoices.push({value:_tab5, label:ZaMsg.TABT_Preferences});

	if(ZaSettings.ACCOUNTS_ALIASES_ENABLED)
		tabChoices.push({value:_tab6, label:ZaMsg.TABT_Aliases});

	if(ZaSettings.ACCOUNTS_FORWARDING_ENABLED)
		tabChoices.push({value:_tab7, label:ZaMsg.TABT_Forwarding});

	if(ZaSettings.SKIN_PREFS_ENABLED) 
		tabChoices.push({value:_tab8, label:ZaMsg.TABT_Themes});	

	if(ZaSettings.ZIMLETS_ENABLED) 
		tabChoices.push({value:_tab9, label:ZaMsg.TABT_Zimlets});	
			
	if(ZaSettings.ACCOUNTS_ADVANCED_ENABLED)
		tabChoices.push({value:_tab10, label:ZaMsg.TABT_Advanced});


	var cases = [];
	var case1 = {type:_CASE_,  relevant:("instance[ZaModel.currentTab] == " + _tab1), height:"400px",  align:_LEFT_, valign:_TOP_};
	var case1Items = [{ref:ZaAccount.A_name, type:_EMAILADDR_, msgName:ZaMsg.NAD_AccountName,label:ZaMsg.NAD_AccountName,
						 labelLocation:_LEFT_,onChange:ZaTabView.onFormFieldChanged,forceUpdate:true}];
	if(ZaSettings.COSES_ENABLED) {
		case1Items.push({ref:ZaAccount.A_COSId, type:_OSELECT1_, msgName:ZaMsg.NAD_ClassOfService,label:ZaMsg.NAD_ClassOfService, labelLocation:_LEFT_, choices:this._app.getCosListChoices(), onChange:ZaAccountXFormView.onCOSChanged});
	}
	case1Items.push({ref:ZaAccount.A_password, type:_SECRET_, msgName:ZaMsg.NAD_Password,label:ZaMsg.NAD_Password, labelLocation:_LEFT_, cssClass:"admin_xform_name_input", onChange:ZaTabView.onFormFieldChanged});
	case1Items.push({ref:ZaAccount.A2_confirmPassword, type:_SECRET_, msgName:ZaMsg.NAD_ConfirmPassword,label:ZaMsg.NAD_ConfirmPassword, labelLocation:_LEFT_, cssClass:"admin_xform_name_input", onChange:ZaTabView.onFormFieldChanged});
	case1Items.push({ref:ZaAccount.A_zimbraPasswordMustChange, align:_LEFT_, type:_CHECKBOX_,  msgName:ZaMsg.NAD_MustChangePwd,label:ZaMsg.NAD_MustChangePwd+":",labelLocation:_LEFT_, trueValue:"TRUE", falseValue:"FALSE", onChange:ZaTabView.onFormFieldChanged,labelCssClass:"xform_label"});
	case1Items.push({ref:ZaAccount.A_isAdminAccount,labelCssClass:"xform_label", type:_CHECKBOX_, 
							msgName:ZaMsg.NAD_IsAdmin,label:ZaMsg.NAD_IsAdmin,labelLocation:_LEFT_, 
							align:_LEFT_,
							trueValue:"TRUE", falseValue:"FALSE",relevantBehavior:_HIDE_,
							labelCssClass:"xform_label",
							onChange:ZaTabView.onFormFieldChanged
						});
	case1Items.push({ref:ZaAccount.A_zimbraHideInGal, align:_LEFT_, type:_CHECKBOX_,  msgName:ZaMsg.NAD_zimbraHideInGal,label:ZaMsg.NAD_zimbraHideInGal,labelLocation:_LEFT_, trueValue:"TRUE", falseValue:"FALSE", onChange:ZaTabView.onFormFieldChanged,labelCssClass:"xform_label"});								
	case1Items.push({ref:ZaAccount.A_firstName, type:_TEXTFIELD_, msgName:ZaMsg.NAD_FirstName,label:ZaMsg.NAD_FirstName, labelLocation:_LEFT_, cssClass:"admin_xform_name_input", width:150, onChange:ZaTabView.onFormFieldChanged,
							elementChanged: function(elementValue,instanceValue, event) {
								if(this.getInstance()[ZaAccount.A2_autodisplayname]=="TRUE") {
									ZaAccountXFormView.generateDisplayName(this.getInstance(), elementValue, this.getInstance().attrs[ZaAccount.A_lastName],this.getInstance().attrs[ZaAccount.A_initials] );
								}
								this.getForm().itemChanged(this, elementValue, event);
							}
						});
	case1Items.push({ref:ZaAccount.A_initials, type:_TEXTFIELD_, msgName:ZaMsg.NAD_Initials,label:ZaMsg.NAD_Initials, labelLocation:_LEFT_, cssClass:"admin_xform_name_input", width:50,  onChange:ZaTabView.onFormFieldChanged,
							elementChanged: function(elementValue,instanceValue, event) {
								if(this.getInstance()[ZaAccount.A2_autodisplayname]=="TRUE") {
									ZaAccountXFormView.generateDisplayName(this.getInstance(), this.getInstance().attrs[ZaAccount.A_firstName], this.getInstance().attrs[ZaAccount.A_lastName],elementValue);
								}
								this.getForm().itemChanged(this, elementValue, event);
							}
						});
	case1Items.push({ref:ZaAccount.A_lastName, type:_TEXTFIELD_, msgName:ZaMsg.NAD_LastName,label:ZaMsg.NAD_LastName, labelLocation:_LEFT_, cssClass:"admin_xform_name_input", width:150, onChange:ZaTabView.onFormFieldChanged,
							elementChanged: function(elementValue,instanceValue, event) {
								if(this.getInstance()[ZaAccount.A2_autodisplayname]=="TRUE") {
									ZaAccountXFormView.generateDisplayName(this.getInstance(), this.getInstance().attrs[ZaAccount.A_firstName], elementValue ,this.getInstance().attrs[ZaAccount.A_initials]);
								}
								this.getForm().itemChanged(this, elementValue, event);
							}
						});
	case1Items.push({type:_GROUP_, numCols:3, nowrap:true, width:200, msgName:ZaMsg.NAD_DisplayName,label:ZaMsg.NAD_DisplayName+":", labelLocation:_LEFT_, 
							items: [
								{ref:ZaAccount.A_displayname, type:_TEXTFIELD_, label:null,	cssClass:"admin_xform_name_input", width:150, onChange:ZaTabView.onFormFieldChanged, 
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
						});
	case1Items.push({ref:ZaAccount.A_zimbraMailCanonicalAddress, type:_TEXTFIELD_,width:250,
						msgName:ZaMsg.NAD_CanonicalFrom,label:ZaMsg.NAD_CanonicalFrom, labelLocation:_LEFT_,  
						onChange:ZaTabView.onFormFieldChanged, align:_LEFT_
					});
	case1Items.push({ref:ZaAccount.A_accountStatus, type:_OSELECT1_, msgName:ZaMsg.NAD_AccountStatus,label:ZaMsg.NAD_AccountStatus, labelLocation:_LEFT_, choices:this.accountStatusChoices, onChange:ZaTabView.onFormFieldChanged});
	case1Items.push({ref:ZaAccount.A_description, type:_INPUT_, msgName:ZaMsg.NAD_Description,label:ZaMsg.NAD_Description, labelLocation:_LEFT_, cssClass:"admin_xform_name_input", onChange:ZaTabView.onFormFieldChanged});
	
	case1Items.push({ref:ZaAccount.A_notes, type:_TEXTAREA_, msgName:ZaMsg.NAD_Notes,label:ZaMsg.NAD_Notes, labelLocation:_LEFT_, labelCssStyle:"vertical-align:top", onChange:ZaTabView.onFormFieldChanged, width:"30em"});
	case1.items = case1Items;
	cases.push(case1);
	var case2={type:_CASE_, numCols:1, relevant:("instance[ZaModel.currentTab] == " + _tab2),
					items: [
						{ref:ZaAccount.A_telephoneNumber, type:_TEXTFIELD_, msgName:ZaMsg.NAD_telephoneNumber,label:ZaMsg.NAD_telephoneNumber, labelLocation:_LEFT_, onChange:ZaTabView.onFormFieldChanged, width:150},
						{ref:ZaAccount.A_company, type:_TEXTFIELD_, msgName:ZaMsg.NAD_company,label:ZaMsg.NAD_company, labelLocation:_LEFT_, onChange:ZaTabView.onFormFieldChanged, width:150},
						{ref:ZaAccount.A_orgUnit, type:_TEXTFIELD_, msgName:ZaMsg.NAD_orgUnit,label:ZaMsg.NAD_orgUnit, labelLocation:_LEFT_, onChange:ZaTabView.onFormFieldChanged, width:150},														
						{ref:ZaAccount.A_office, type:_TEXTFIELD_, msgName:ZaMsg.NAD_office,label:ZaMsg.NAD_office, labelLocation:_LEFT_, onChange:ZaTabView.onFormFieldChanged, width:150},
						{ref:ZaAccount.A_street, type:_TEXTFIELD_, msgName:ZaMsg.NAD_street,label:ZaMsg.NAD_street, labelLocation:_LEFT_, onChange:ZaTabView.onFormFieldChanged, width:150},
						{ref:ZaAccount.A_city, type:_TEXTFIELD_, msgName:ZaMsg.NAD_city,label:ZaMsg.NAD_city, labelLocation:_LEFT_, onChange:ZaTabView.onFormFieldChanged, width:150},
						{ref:ZaAccount.A_state, type:_TEXTFIELD_, msgName:ZaMsg.NAD_state,label:ZaMsg.NAD_state, labelLocation:_LEFT_, onChange:ZaTabView.onFormFieldChanged, width:150},
						{ref:ZaAccount.A_zip, type:_TEXTFIELD_, msgName:ZaMsg.NAD_zip,label:ZaMsg.NAD_zip, labelLocation:_LEFT_, onChange:ZaTabView.onFormFieldChanged, width:150},
						{ref:ZaAccount.A_country, type:_TEXTFIELD_, msgName:ZaMsg.NAD_country,label:ZaMsg.NAD_country, labelLocation:_LEFT_, onChange:ZaTabView.onFormFieldChanged, width:150}
					]
				};
	cases.push(case2);
	
	var directMemberOfHeaderList = new ZaAccountMemberOfsourceHeaderList(ZaAccountMemberOfsourceHeaderList.DIRECT);
	var indirectMemberOfHeaderList = new ZaAccountMemberOfsourceHeaderList(ZaAccountMemberOfsourceHeaderList.INDIRECT);
	var nonMemberOfHeaderList = new ZaAccountMemberOfsourceHeaderList(ZaAccountMemberOfsourceHeaderList.NON);
	
	//MemberOf Tab
	var case3={type:_CASE_, numCols:4, relevant:("instance[ZaModel.currentTab] == " + _tab3), colSizes: [450, 20, 420, 30],
					items: [
						//isgroup checkbox
						/*
						{type:_GROUP_, width: "100%", colSpan: 4, 
							items: [
								{type:_GROUP_, width: 120, numCols: 1,
									items: [
											{ref: ZaAccount.A2_isgroup, type: _CHECKBOX_, align:_LEFT_, colSpan: 3,msgName:ZaMsg.NAD_ShowGroupOnly,
												label:ZaMsg.NAD_ShowGroupOnly,labelLocation:_LEFT_, trueValue:"TRUE", falseValue:"FALSE",
												onChange:ZaAccountMemberOfListView.onShowGroupOnlyChanged,labelCssClass:"xform_label"}
										]
								}
							]
						}, */
													
						{type:_SPACER_, height:"10"},
						//layout rapper around the direct/indrect list						
						{type: _GROUP_, width: "98%", numCols: 1, //colSizes: ["auto", 20],
							items: [
								//direct member group
								{type:_GROUP_, numCols:1, cssClass: "RadioGrouperBorder", width: "100%", colSizes:["auto"], //height: 400,
									items:[
										{type:_GROUP_,  numCols:2, colSizes:["auto", "auto"],
									   		items: [
												{type:_OUTPUT_, value:ZaMsg.Account_DirectGroupLabel, cssClass:"RadioGrouperLabel"},
												{type:_CELLSPACER_}
											]
										},
										{ref: ZaAccount.A2_directMemberList, type: _S_DWT_LIST_, width: "100%", height: 200,
											cssClass: "DLSource", widgetClass: ZaAccountMemberOfListView, 
											headerList: directMemberOfHeaderList, defaultColumnSortable: 0,
											forceUpdate: true }	,
										{type:_SPACER_, height:"5"},
										{type:_GROUP_, width:"100%", numCols:8, colSizes:[85,5, 85,"100%",80,5,80,5], 
											items:[
												{type:_DWT_BUTTON_, label:ZaMsg.DLXV_ButtonRemoveAll, width:80, 
												   relevant:"ZaAccountMemberOfListView.shouldEnableAllButton.call(this, ZaAccount.A2_directMemberList)",
												   onActivate:"ZaAccountMemberOfListView.removeAllGroups.call(this,event, ZaAccount.A2_directMemberList)",
												   relevantBehavior:_DISABLE_},
												{type:_CELLSPACER_},
												{type:_DWT_BUTTON_, label:ZaMsg.DLXV_ButtonRemove, width:80, id:"removeButton",
											      onActivate:"ZaAccountMemberOfListView.removeGroups.call(this,event, ZaAccount.A2_directMemberList)",
											      relevant:"ZaAccountMemberOfListView.shouldEnableAddRemoveButton.call(this, ZaAccount.A2_directMemberList)",
											      relevantBehavior:_DISABLE_},
												{type:_CELLSPACER_},
												{type:_DWT_BUTTON_, label:ZaMsg.Previous, width:75, id:"backButton", icon:"LeftArrow", disIcon:"LeftArrowDis", 	
													onActivate:"ZaAccountMemberOfListView.backButtonHndlr.call(this,event, ZaAccount.A2_directMemberList)", 
													relevantBehavior:_DISABLE_, relevant:"ZaAccountMemberOfListView.shouldEnableBackButton.call(this, ZaAccount.A2_directMemberList)"
											    },								       
												{type:_CELLSPACER_},
												{type:_DWT_BUTTON_, label:ZaMsg.Next, width:75, id:"fwdButton", icon:"RightArrow", disIcon:"RightArrowDis",	
													onActivate:"ZaAccountMemberOfListView.fwdButtonHndlr.call(this,event, ZaAccount.A2_directMemberList)", 
													relevantBehavior:_DISABLE_, 
													relevant:"ZaAccountMemberOfListView.shouldEnableForwardButton.call(this, ZaAccount.A2_directMemberList)"
											    },								       
												{type:_CELLSPACER_}									
											]
										}		
									]
								},	
								//{type:_CELLSPACER_},	
								{type:_SPACER_, height:"10"},	
								//indirect member group
								{type:_GROUP_, numCols:1, cssClass: "RadioGrouperBorder", width: "100%", //colSizes:["auto"], height: "48%",
									items:[
										{type:_GROUP_,  numCols:2, colSizes:["auto", "auto"],
									   		items: [
												{type:_OUTPUT_, value:ZaMsg.Account_IndirectGroupLabel, cssClass:"RadioGrouperLabel"},
												{type:_CELLSPACER_}
											]
										},
										//{type:_SPACER_, height:"5"},
										{ref: ZaAccount.A2_indirectMemberList, type: _S_DWT_LIST_, width: "100%", height: 200,
											cssClass: "DLSource", widgetClass: ZaAccountMemberOfListView, 
											headerList: indirectMemberOfHeaderList, defaultColumnSortable: 0,
											forceUpdate: true }	,
										{type:_SPACER_, height:"5"},
										{type:_GROUP_, width:"100%", numCols:8, colSizes:[85,5, 85,"100%",80,5,80,5], 
											items:[
												{type:_CELLSPACER_},
												{type:_CELLSPACER_},
												{type:_CELLSPACER_},
												{type:_CELLSPACER_},
												{type:_DWT_BUTTON_, label:ZaMsg.Previous, width:75, id:"backButton", icon:"LeftArrow", disIcon:"LeftArrowDis", 	
													onActivate:"ZaAccountMemberOfListView.backButtonHndlr.call(this,event, ZaAccount.A2_indirectMemberList)", 
													relevantBehavior:_DISABLE_, relevant:"ZaAccountMemberOfListView.shouldEnableBackButton.call(this, ZaAccount.A2_indirectMemberList)"
											    },								       
												{type:_CELLSPACER_},
												{type:_DWT_BUTTON_, label:ZaMsg.Next, width:75, id:"fwdButton", icon:"RightArrow", disIcon:"RightArrowDis",	
													onActivate:"ZaAccountMemberOfListView.fwdButtonHndlr.call(this,event, ZaAccount.A2_indirectMemberList)", 
													relevantBehavior:_DISABLE_, 
													relevant:"ZaAccountMemberOfListView.shouldEnableForwardButton.call(this, ZaAccount.A2_indirectMemberList)"
											    },								       
												{type:_CELLSPACER_}									
											]
										}
									]
								}
								//{type:_CELLSPACER_}	
							]
						},
						{type: _GROUP_, width: "100%", items: [
								{type:_CELLSPACER_},
							]
						},
						//non member group
						{type:_GROUP_, numCols:1, cssClass: "RadioGrouperBorder", width: "98%", //colSizes:["auto"], height: "98%",
							items:[
								{type:_GROUP_,  numCols:2, colSizes:["auto", "auto"],
							   		items: [
										{type:_OUTPUT_, value:ZaMsg.Account_NonGroupLabel, cssClass:"RadioGrouperLabel"},
										{type:_CELLSPACER_}
									]
								},
								{type:_GROUP_, numCols:5, colSizes:[30, "auto",60, 150,15], width:"98%", 
								   items:[
								   		{type:_OUTPUT_, value:ZaMsg.DLXV_LabelFind, nowrap:true},
										{ref:"query", type:_TEXTFIELD_, width:"100%", cssClass:"admin_xform_name_input",  label:null,
									      elementChanged: function(elementValue,instanceValue, event) {
											  var charCode = event.charCode;
											  if (charCode == 13 || charCode == 3) {
											      ZaAccountMemberOfListView.prototype.srchButtonHndlr.call(this);
											  } else {
											      this.getForm().itemChanged(this, elementValue, event);
											  }
								      		}
										},
										{type:_DWT_BUTTON_, label:ZaMsg.DLXV_ButtonSearch, width:80,
										   onActivate:ZaAccountMemberOfListView.prototype.srchButtonHndlr
										},
										{ref: ZaAccount.A2_showSameDomain, type: _CHECKBOX_, align:_RIGHT_, msgName:ZaMsg.NAD_SearchSameDomain,
												label:AjxMessageFormat.format (ZaMsg.NAD_SearchSameDomain),
												//ZaMsg.NAD_SearchSameDomain,
												labelLocation:_LEFT_, trueValue:"TRUE", falseValue:"FALSE",
												labelCssClass:"xform_label", relevantBehavior: _HIDE_, 
												relevant: "ZaSettings.DOMAINS_ENABLED"
										}										
									]
						         },
						        {type:_SPACER_, height:"5"},
								
								{ref: ZaAccount.A2_nonMemberList, type: _S_DWT_LIST_, width: "100%", height: 455,
									cssClass: "DLSource", widgetClass: ZaAccountMemberOfListView, 
									headerList: nonMemberOfHeaderList, defaultColumnSortable: 0,
									//createPopupMenu: 
									forceUpdate: true },
									
								{type:_SPACER_, height:"5"},	
								//add action buttons
								{type:_GROUP_, width:"100%", numCols:8, colSizes:[85,5, 85,"100%",80,5,80,5],
									items: [
									   {type:_DWT_BUTTON_, label:ZaMsg.DLXV_ButtonAddFromList, width:80,
										onActivate:"ZaAccountMemberOfListView.addGroups.call(this,event, ZaAccount.A2_nonMemberList)",
										relevant:"ZaAccountMemberOfListView.shouldEnableAddRemoveButton.call(this, ZaAccount.A2_nonMemberList)",
										relevantBehavior:_DISABLE_},
									   {type:_CELLSPACER_},
									   {type:_DWT_BUTTON_, label:ZaMsg.DLXV_ButtonAddAll, width:80,
										onActivate:"ZaAccountMemberOfListView.addAllGroups.call(this,event, ZaAccount.A2_nonMemberList)",
										relevant:"ZaAccountMemberOfListView.shouldEnableAllButton.call(this, ZaAccount.A2_nonMemberList)",
										relevantBehavior:_DISABLE_},
										{type:_CELLSPACER_},
										{type:_DWT_BUTTON_, label:ZaMsg.Previous, width:75, id:"backButton", icon:"LeftArrow", disIcon:"LeftArrowDis",
											relevantBehavior:_DISABLE_, relevant:"ZaAccountMemberOfListView.shouldEnableBackButton.call(this, ZaAccount.A2_nonMemberList)",
											onActivate:"ZaAccountMemberOfListView.backButtonHndlr.call(this,event, ZaAccount.A2_nonMemberList)"
										},								       
										{type:_CELLSPACER_},
										{type:_DWT_BUTTON_, label:ZaMsg.Next, width:75, id:"fwdButton", icon:"RightArrow", disIcon:"RightArrowDis",
										 	relevantBehavior:_DISABLE_, relevant:"ZaAccountMemberOfListView.shouldEnableForwardButton.call(this, ZaAccount.A2_nonMemberList)",
											onActivate:"ZaAccountMemberOfListView.fwdButtonHndlr.call(this,event, ZaAccount.A2_nonMemberList)"									
										},								       
										{type:_CELLSPACER_}	
									  ]
							    }								
							]
						},
						{type: _GROUP_, width: "100%", items: [
								{type:_CELLSPACER_},
							]
						}
						
						//{type:_CELLSPACER_}		
					]
				};
	cases.push(case3);		
					
	if(ZaSettings.ACCOUNTS_FEATURES_ENABLED) {
		cases.push({type:_CASE_,id:"account_form_features_tab",  numCols:1, width:"100%", relevant:("instance[ZaModel.currentTab] == " + _tab4),
					items: [
						{ref:ZaAccount.A_zimbraFeatureContactsEnabled,labelCssStyle:"width:150px;", type:_SUPER_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_FeatureContactsEnabled,label:ZaMsg.NAD_FeatureContactsEnabled, labelLocation:_LEFT_, trueValue:"TRUE", falseValue:"FALSE", onChange:ZaTabView.onFormFieldChanged},							
						{ref:ZaAccount.A_zimbraFeatureCalendarEnabled, type:_SUPER_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_FeatureCalendarEnabled,label:ZaMsg.NAD_FeatureCalendarEnabled, labelLocation:_LEFT_, trueValue:"TRUE", falseValue:"FALSE", onChange:ZaTabView.onFormFieldChanged},														
						{ref:ZaAccount.A_zimbraFeatureTaggingEnabled, type:_SUPER_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_FeatureTaggingEnabled,label:ZaMsg.NAD_FeatureTaggingEnabled, labelLocation:_LEFT_, trueValue:"TRUE", falseValue:"FALSE", onChange:ZaTabView.onFormFieldChanged},
						{ref:ZaAccount.A_zimbraFeatureAdvancedSearchEnabled, type:_SUPER_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_FeatureAdvancedSearchEnabled,label:ZaMsg.NAD_FeatureAdvancedSearchEnabled, labelLocation:_LEFT_, trueValue:"TRUE", falseValue:"FALSE", onChange:ZaTabView.onFormFieldChanged},
						{ref:ZaAccount.A_zimbraFeatureSavedSearchesEnabled, type:_SUPER_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_FeatureSavedSearchesEnabled,label:ZaMsg.NAD_FeatureSavedSearchesEnabled, labelLocation:_LEFT_, trueValue:"TRUE", falseValue:"FALSE", onChange:ZaTabView.onFormFieldChanged},
						{ref:ZaAccount.A_zimbraFeatureConversationsEnabled, type:_SUPER_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_FeatureConversationsEnabled,label:ZaMsg.NAD_FeatureConversationsEnabled, labelLocation:_LEFT_, trueValue:"TRUE", falseValue:"FALSE", onChange:ZaTabView.onFormFieldChanged},
						{ref:ZaAccount.A_zimbraFeatureChangePasswordEnabled, type:_SUPER_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_FeatureChangePasswordEnabled,label:ZaMsg.NAD_FeatureChangePasswordEnabled, labelLocation:_LEFT_, trueValue:"TRUE", falseValue:"FALSE"},
						{ref:ZaAccount.A_zimbraFeatureInitialSearchPreferenceEnabled, type:_SUPER_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_FeatureInitialSearchPreferenceEnabled,label:ZaMsg.NAD_FeatureInitialSearchPreferenceEnabled, labelLocation:_LEFT_, trueValue:"TRUE", falseValue:"FALSE", onChange:ZaTabView.onFormFieldChanged},
						{ref:ZaAccount.A_zimbraFeatureFiltersEnabled, type:_SUPER_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_FeatureFiltersEnabled,label:ZaMsg.NAD_FeatureFiltersEnabled, labelLocation:_LEFT_, trueValue:"TRUE", falseValue:"FALSE", onChange:ZaTabView.onFormFieldChanged},
						{ref:ZaAccount.A_zimbraFeatureHtmlComposeEnabled, type:_SUPER_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_zimbraFeatureHtmlComposeEnabled,label:ZaMsg.NAD_zimbraFeatureHtmlComposeEnabled, labelLocation:_LEFT_, trueValue:"TRUE", falseValue:"FALSE", onChange:ZaTabView.onFormFieldChanged},							
						{ref:ZaAccount.A_zimbraFeatureGalEnabled, type:_SUPER_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_FeatureGalEnabled,label:ZaMsg.NAD_FeatureGalEnabled, labelLocation:_LEFT_, trueValue:"TRUE", falseValue:"FALSE", onChange:ZaTabView.onFormFieldChanged},
						{ref:ZaAccount.A_zimbraImapEnabled, type:_SUPER_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_zimbraImapEnabled,label:ZaMsg.NAD_zimbraImapEnabled, labelLocation:_LEFT_, trueValue:"TRUE", falseValue:"FALSE", onChange:ZaTabView.onFormFieldChanged},
						{ref:ZaAccount.A_zimbraPop3Enabled, type:_SUPER_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_zimbraPop3Enabled,label:ZaMsg.NAD_zimbraPop3Enabled, labelLocation:_LEFT_, trueValue:"TRUE", falseValue:"FALSE", onChange:ZaTabView.onFormFieldChanged},		
						{ref:ZaAccount.A_zimbraFeatureSharingEnabled, type:_SUPER_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_zimbraFeatureSharingEnabled,label:ZaMsg.NAD_zimbraFeatureSharingEnabled, labelLocation:_LEFT_, trueValue:"TRUE", falseValue:"FALSE", onChange:ZaTabView.onFormFieldChanged},
						{ref:ZaAccount.A_zimbraFeatureNotebookEnabled, type:_SUPER_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_zimbraFeatureNotebookEnabled,label:ZaMsg.NAD_zimbraFeatureNotebookEnabled, labelLocation:_LEFT_, trueValue:"TRUE", falseValue:"FALSE", onChange:ZaTabView.onFormFieldChanged},						
						{ref:ZaAccount.A_zimbraFeatureGalAutoCompleteEnabled, type:_SUPER_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_zimbraFeatureGalAutoCompleteEnabled,label:ZaMsg.NAD_zimbraFeatureGalAutoCompleteEnabled, labelLocation:_LEFT_, trueValue:"TRUE", falseValue:"FALSE", onChange:ZaTabView.onFormFieldChanged},
						{ref:ZaAccount.A_zimbraFeatureOutOfOfficeReplyEnabled, type:_SUPER_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_zimbraFeatureOutOfOfficeReplyEnabled,label:ZaMsg.NAD_zimbraFeatureOutOfOfficeReplyEnabled, labelLocation:_LEFT_, trueValue:"TRUE", falseValue:"FALSE", onChange:ZaTabView.onFormFieldChanged},
						{ref:ZaAccount.A_zimbraFeatureNewMailNotificationEnabled, type:_SUPER_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_zimbraFeatureNewMailNotificationEnabled,label:ZaMsg.NAD_zimbraFeatureNewMailNotificationEnabled, labelLocation:_LEFT_, trueValue:"TRUE", falseValue:"FALSE", onChange:ZaTabView.onFormFieldChanged},
						{ref:ZaAccount.A_zimbraFeatureSkinChangeEnabled, type:_SUPER_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_zimbraFeatureSkinChangeEnabled,label:ZaMsg.NAD_zimbraFeatureSkinChangeEnabled, labelLocation:_LEFT_, trueValue:"TRUE", falseValue:"FALSE", onChange:ZaTabView.onFormFieldChanged}						
					]
				});
	}
	if(ZaSettings.ACCOUNTS_PREFS_ENABLED) {
		var prefItems = [
						{ref:ZaAccount.A_prefSaveToSent,  type:_SUPER_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_prefSaveToSent,label:ZaMsg.NAD_prefSaveToSent, labelLocation:_LEFT_, trueValue:"TRUE", falseValue:"FALSE",onChange:ZaTabView.onFormFieldChanged},
						{ref:ZaAccount.A_zimbraPrefMessageViewHtmlPreferred, type:_SUPER_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_zimbraPrefMessageViewHtmlPreferred,label:ZaMsg.NAD_zimbraPrefMessageViewHtmlPreferred, labelLocation:_LEFT_, trueValue:"TRUE", falseValue:"FALSE",onChange:ZaTabView.onFormFieldChanged},
						{ref:ZaAccount.A_zimbraPrefComposeInNewWindow, type:_SUPER_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_zimbraPrefComposeInNewWindow,label:ZaMsg.NAD_zimbraPrefComposeInNewWindow, labelLocation:_LEFT_, trueValue:"TRUE", falseValue:"FALSE",onChange:ZaTabView.onFormFieldChanged},							
						{ref:ZaAccount.A_zimbraPrefForwardReplyInOriginalFormat, type:_SUPER_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_zimbraPrefForwardReplyInOriginalFormat,label:ZaMsg.NAD_zimbraPrefForwardReplyInOriginalFormat, labelLocation:_LEFT_, trueValue:"TRUE", falseValue:"FALSE",onChange:ZaTabView.onFormFieldChanged},														
						{ref:ZaAccount.A_zimbraPrefComposeFormat, type:_SUPER_SELECT1_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_zimbraPrefComposeFormat,label:ZaMsg.NAD_zimbraPrefComposeFormat, labelLocation:_LEFT_, onChange:ZaTabView.onFormFieldChanged},							
						{ref:ZaAccount.A_zimbraPrefAutoAddAddressEnabled, type:_SUPER_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_zimbraPrefAutoAddAddressEnabled,label:ZaMsg.NAD_zimbraPrefAutoAddAddressEnabled, labelLocation:_LEFT_, trueValue:"TRUE", falseValue:"FALSE",onChange:ZaTabView.onFormFieldChanged},							
						{ref:ZaAccount.A_zimbraPrefGalAutoCompleteEnabled, type:_SUPER_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_zimbraPrefGalAutoCompleteEnabled,label:ZaMsg.NAD_zimbraPrefGalAutoCompleteEnabled, labelLocation:_LEFT_, trueValue:"TRUE", falseValue:"FALSE",onChange:ZaTabView.onFormFieldChanged},																				
						{type:_SEPARATOR_},
						{ref:ZaAccount.A_zimbraPrefGroupMailBy, type:_SUPER_SELECT1_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_zimbraPrefGroupMailBy,label:ZaMsg.NAD_zimbraPrefGroupMailBy, labelLocation:_LEFT_, onChange:ZaTabView.onFormFieldChanged},							
						{ref:ZaAccount.A_zimbraPrefContactsPerPage, type:_SUPER_SELECT1_, msgName:ZaMsg.NAD_PrefContactsPerPage,label:ZaMsg.NAD_PrefContactsPerPage+":", labelLocation:_LEFT_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, valueLabel:null,onChange:ZaTabView.onFormFieldChanged},							
						{ref:ZaAccount.A_zimbraPrefMailItemsPerPage, type:_SUPER_SELECT1_, msgName:ZaMsg.NAD_zimbraPrefMailItemsPerPage,label:ZaMsg.NAD_zimbraPrefMailItemsPerPage, labelLocation:_LEFT_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, valueLabel:null,onChange:ZaTabView.onFormFieldChanged},
						{ref:ZaAccount.A_zimbraPrefMailInitialSearch, type:_SUPER_TEXTFIELD_, msgName:ZaMsg.NAD_zimbraPrefMailInitialSearch,label:ZaMsg.NAD_zimbraPrefMailInitialSearch, labelLocation:_LEFT_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS,  onChange:ZaTabView.onFormFieldChanged},
						{ref:ZaAccount.A_zimbraPrefMailPollingInterval, type:_SUPER_LIFETIME_, msgName:ZaMsg.NAD_zimbraPrefMailPollingInterval,label:ZaMsg.NAD_zimbraPrefMailPollingInterval+":", labelLocation:_LEFT_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS,  onChange:ZaTabView.onFormFieldChanged},							
						{ref:ZaAccount.A_zimbraPrefCalendarApptReminderWarningTime, type:_SUPER_SELECT1_, msgName:ZaMsg.NAD_zimbraPrefCalendarApptReminderWarningTime,label:ZaMsg.NAD_zimbraPrefCalendarApptReminderWarningTime+":", labelLocation:_LEFT_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS,  onChange:ZaTabView.onFormFieldChanged},													
						{ref:ZaAccount.A_zimbraPrefShowSearchString, type:_SUPER_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_zimbraPrefShowSearchString,label:ZaMsg.NAD_zimbraPrefShowSearchString, labelLocation:_LEFT_, trueValue:"TRUE", falseValue:"FALSE",onChange:ZaTabView.onFormFieldChanged},
						{ref:ZaAccount.A_zimbraPrefCalendarAlwaysShowMiniCal, type:_SUPER_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_alwaysShowMiniCal,label:ZaMsg.NAD_alwaysShowMiniCal, labelLocation:_LEFT_, trueValue:"TRUE", falseValue:"FALSE",onChange:ZaTabView.onFormFieldChanged},							
						{ref:ZaAccount.A_zimbraPrefCalendarUseQuickAdd, type:_SUPER_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_useQuickAdd,label:ZaMsg.NAD_useQuickAdd, labelLocation:_LEFT_, trueValue:"TRUE", falseValue:"FALSE",onChange:ZaTabView.onFormFieldChanged},							
						{ref:ZaAccount.A_zimbraPrefUseTimeZoneListInCalendar, type:_SUPER_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_zimbraPrefUseTimeZoneListInCalendar,label:ZaMsg.NAD_zimbraPrefUseTimeZoneListInCalendar, labelLocation:_LEFT_, trueValue:"TRUE", falseValue:"FALSE",onChange:ZaTabView.onFormFieldChanged},							
						{ref:ZaAccount.A_zimbraPrefImapSearchFoldersEnabled, type:_SUPER_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_zimbraPrefImapSearchFoldersEnabled,label:ZaMsg.NAD_zimbraPrefImapSearchFoldersEnabled, labelLocation:_LEFT_, trueValue:"TRUE", falseValue:"FALSE",onChange:ZaTabView.onFormFieldChanged},														
						{ref:ZaAccount.A_zimbraPrefMailLocalDeliveryDisabled, type:_CHECKBOX_, msgName:ZaMsg.NAD_zimbraPrefMailLocalDeliveryDisabled,label:ZaMsg.NAD_zimbraPrefMailLocalDeliveryDisabled, labelLocation:_LEFT_, trueValue:"TRUE", falseValue:"FALSE",onChange:ZaTabView.onFormFieldChanged,labelCssClass:"xform_label", align:_LEFT_},
						{type:_SEPARATOR_},	
						{ref:ZaAccount.A_zimbraPrefUseKeyboardShortcuts, type:_SUPER_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS,label:ZaMsg.NAD_prefKeyboardShort, labelLocation:_LEFT_, trueValue:"TRUE", falseValue:"FALSE",onChange:ZaTabView.onFormFieldChanged},			
						{type:_SEPARATOR_},							
						{ref:ZaAccount.A_zimbraPrefNewMailNotificationEnabled, type:_CHECKBOX_, msgName:ZaMsg.NAD_zimbraPrefNewMailNotificationEnabled,label:ZaMsg.NAD_zimbraPrefNewMailNotificationEnabled, labelLocation:_LEFT_, trueValue:"TRUE", falseValue:"FALSE",onChange:ZaTabView.onFormFieldChanged,labelCssClass:"xform_label", align:_LEFT_},
						{ref:ZaAccount.A_zimbraPrefNewMailNotificationAddress, type:_TEXTFIELD_, msgName:ZaMsg.NAD_zimbraPrefNewMailNotificationAddress,label:ZaMsg.NAD_zimbraPrefNewMailNotificationAddress, labelLocation:_LEFT_,  onChange:ZaTabView.onFormFieldChanged},							
						{type:_SEPARATOR_},
						{ref:ZaAccount.A_prefMailSignatureEnabled, type:_CHECKBOX_, msgName:ZaMsg.NAD_prefMailSignatureEnabled,label:ZaMsg.NAD_prefMailSignatureEnabled, labelLocation:_LEFT_, trueValue:"TRUE", falseValue:"FALSE",onChange:ZaTabView.onFormFieldChanged,labelCssClass:"xform_label", align:_LEFT_},
						{ref:ZaAccount.A_zimbraPrefMailSignatureStyle, type:_SUPER_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_zimbraPrefMailSignatureStyle,label:ZaMsg.NAD_zimbraPrefMailSignatureStyle, labelLocation:_LEFT_, onChange:ZaTabView.onFormFieldChanged,trueValue:"internet", falseValue:"outlook"},
						{ref:ZaAccount.A_prefMailSignature, type:_TEXTAREA_, msgName:ZaMsg.NAD_prefMailSignature,label:ZaMsg.NAD_prefMailSignature, labelLocation:_LEFT_, labelCssStyle:"vertical-align:top", onChange:ZaTabView.onFormFieldChanged, colSpan:3, width:"30em"},
						{type:_SEPARATOR_},
						{ref:ZaAccount.A_zimbraPrefOutOfOfficeReplyEnabled, type:_CHECKBOX_, msgName:ZaMsg.NAD_zimbraPrefOutOfOfficeReplyEnabled,label:ZaMsg.NAD_zimbraPrefOutOfOfficeReplyEnabled, labelLocation:_LEFT_, trueValue:"TRUE", falseValue:"FALSE",onChange:ZaTabView.onFormFieldChanged,labelCssClass:"xform_label", align:_LEFT_},
						{ref:ZaAccount.A_zimbraPrefOutOfOfficeReply, type:_TEXTAREA_, msgName:ZaMsg.NAD_zimbraPrefOutOfOfficeReply,label:ZaMsg.NAD_zimbraPrefOutOfOfficeReply, labelLocation:_LEFT_, labelCssStyle:"vertical-align:top", onChange:ZaTabView.onFormFieldChanged, colSpan:3, width:"30em"}
						
					];
		cases.push({type:_CASE_, width:"100%", relevant:("instance[ZaModel.currentTab] == " + _tab5),
					colSizes:["300px","450px"], items :prefItems});
	}


	if(ZaSettings.ACCOUNTS_ALIASES_ENABLED) {
		cases.push({type:_CASE_, numCols:1, relevant:("instance[ZaModel.currentTab] == " + _tab6),
					items: [
						{type:_OUTPUT_, value:ZaMsg.NAD_EditAliasesGroup},
						{ref:ZaAccount.A_zimbraMailAlias, type:_REPEAT_, label:null, repeatInstance:emptyAlias, showAddButton:true, showRemoveButton:true, 
							addButtonLabel:ZaMsg.NAD_AddAlias, 
							showAddOnNextRow:true,
							removeButtonLabel:ZaMsg.NAD_RemoveAlias,
							items: [
								{ref:".", type:_EMAILADDR_, label:null, onChange:ZaTabView.onFormFieldChanged}
							],
							onRemove:ZaAccountXFormView.onRepeatRemove
						}
					]
				});
	}
	
	var zimbraFeatureMailForwardingEnabledType = _SUPER_CHECKBOX_ ;
	if (ZaSettings.isDomainAdmin || !ZaSettings.COSES_ENABLED) {
		zimbraFeatureMailForwardingEnabledType = _CHECKBOX_ ;
	}
	
	if(ZaSettings.ACCOUNTS_FORWARDING_ENABLED) {
		cases.push({type:_CASE_, numCols:2, relevant:("instance[ZaModel.currentTab] == " + _tab7), 
					items: [
						{ref:ZaAccount.A_zimbraFeatureMailForwardingEnabled,resetToSuperLabel:ZaMsg.NAD_ResetToCOS, type:zimbraFeatureMailForwardingEnabledType, 
							label:ZaMsg.NAD_zimbraFeatureMailForwardingEnabled,  labelCssClass:"xform_label", labelLocation:_LEFT_, 
							trueValue:"TRUE", falseValue:"FALSE",
							onChange:ZaTabView.onFormFieldChanged, align:_LEFT_
						},
						{ref:ZaAccount.A_zimbraPrefMailForwardingAddress, type:_TEXTFIELD_,width:250,
							msgName:ZaMsg.NAD_zimbraPrefMailForwardingAddress,label:ZaMsg.NAD_zimbraPrefMailForwardingAddress+":", labelLocation:_LEFT_,  
							onChange:ZaTabView.onFormFieldChanged,
							relevantBehavior:_DISABLE_, align:_LEFT_,
							relevant:"this.getModel().getInstanceValue(this.getInstance(),ZaAccount.A_zimbraFeatureMailForwardingEnabled) == \"TRUE\""
						},
						{type:_SPACER_},
						{type:_SEPARATOR_,colSpan:2},
						{ref:ZaAccount.A_zimbraMailForwardingAddress,type:_REPEAT_,
							labelCssClass:"xform_label", label:ZaMsg.NAD_EditFwdGroup,colSpan:"*", labelLocation:_LEFT_, 
							addButtonLabel:ZaMsg.NAD_AddAddress, 
							align:_LEFT_,colSpan:"*",
							repeatInstance:emptyAlias, showAddButton:true, showRemoveButton:true, 
							showAddOnNextRow:true, 
							removeButtonLabel:ZaMsg.NAD_RemoveAddress,								
							items: [
								{ref:".", type:_TEXTFIELD_, label:null, onChange:ZaTabView.onFormFieldChanged, width:250}
							],
							onRemove:ZaAccountXFormView.onRepeatRemove
						}
					]
				});
	}

	if(ZaSettings.SKIN_PREFS_ENABLED) {
		cases.push({type:_CASE_, id:"account_form_themes_tab", numCols:1,relevant:("instance[ZaModel.currentTab] == " + _tab8),
			items:[
				{type:_SPACER_},
				{sourceRef: ZaAccount.A_zimbraInstalledSkinPool, 
					ref:ZaAccount.A_zimbraAvailableSkin, 
					type:_SUPER_DWT_CHOOSER_, sorted:true, 
					onChange: ZaTabView.onFormFieldChanged,
					resetToSuperLabel:ZaMsg.NAD_ResetToCOS,
					forceUpdate:true,widgetClass:ZaSkinPoolChooser,
					relevant:"ZaAccountXFormView.gotSkins.call(this)",
					width:"100%"
				},
				{type:_SPACER_},
				{type:_GROUP_, 
					items:[
					{ref:ZaAccount.A_zimbraPrefSkin, type:_SUPER_SELECT1_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_zimbraPrefSkin,label:ZaMsg.NAD_zimbraPrefSkin, labelLocation:_LEFT_, onChange:ZaTabView.onFormFieldChanged,choices:this._app.getInstalledSkins(),
						relevant:"ZaAccountXFormView.gotSkins.call(this)"}
					] 
				}
			] 
		});
	}	

	if(ZaSettings.ZIMLETS_ENABLED) {
		cases.push({type:_CASE_, id:"account_form_zimlets_tab", numCols:1,relevant:("instance[ZaModel.currentTab] == " + _tab9),
			items:[
				{type:_SPACER_},
				{sourceRef: ZaAccount.A_zimbraInstalledZimletPool, 
					ref:ZaAccount.A_zimbraZimletAvailableZimlets, 
					type:_SUPER_DWT_CHOOSER_, sorted:true, 
					onChange: ZaTabView.onFormFieldChanged,
					resetToSuperLabel:ZaMsg.NAD_ResetToCOS,
					forceUpdate:true,widgetClass:ZaZimletPoolChooser,
					width:"100%"
				}
			] 
		});
	}
	if(ZaSettings.ACCOUNTS_ADVANCED_ENABLED) {
		cases.push({type:_CASE_, id:"account_form_advanced_tab", numCols:1, relevant:("instance[ZaModel.currentTab] == " + _tab10),
					items: [
						{type:_GROUP_, id:"account_attachment_settings",
							items :[
								{ref:ZaAccount.A_zimbraAttachmentsBlocked, type:_SUPER_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, 
									msgName:ZaMsg.NAD_RemoveAllAttachments,label:ZaMsg.NAD_RemoveAllAttachments, 
									labelLocation:_LEFT_, 
									labelCssStyle:"width:250px;", trueValue:"TRUE", falseValue:"FALSE", 
									onChange:ZaTabView.onFormFieldChanged
								}
							]
						},
						{type:_SEPARATOR_, colSpan:"*"},
						{type:_GROUP_, id:"account_quota_settings",
							items: [
								{ref:ZaAccount.A_zimbraMailQuota, type:_SUPER_TEXTFIELD_, 
									resetToSuperLabel:ZaMsg.NAD_ResetToCOS, 
									label:ZaMsg.NAD_MailQuota+":", msgName:ZaMsg.NAD_MailQuota,labelLocation:_LEFT_, 
									textFieldCssClass:"admin_xform_number_input", 
									onChange:ZaTabView.onFormFieldChanged, 
									resetToSuperLabel:ZaMsg.NAD_ResetToCOS,
									labelCssStyle:"width:250px;"
								},
								{ref:ZaAccount.A_zimbraContactMaxNumEntries, type:_SUPER_TEXTFIELD_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_ContactMaxNumEntries,label:ZaMsg.NAD_ContactMaxNumEntries+":", labelLocation:_LEFT_, textFieldCssClass:"admin_xform_number_input", onChange:ZaTabView.onFormFieldChanged}
							]
						},
						{type:_SEPARATOR_, colSpan:"*"},
						{type:_GROUP_,id:"account_password_settings",
							items: [
								{ref:ZaAccount.A_zimbraMinPwdLength, labelCssStyle:"width:250px;", 
									type:_SUPER_TEXTFIELD_, 
									resetToSuperLabel:ZaMsg.NAD_ResetToCOS, 
									msgName:ZaMsg.NAD_passMinLength,label:ZaMsg.NAD_passMinLength+":", labelLocation:_LEFT_, 
									textFieldCssClass:"admin_xform_number_input", 
									onChange:ZaTabView.onFormFieldChanged
								},
								{ref:ZaAccount.A_zimbraMaxPwdLength, type:_SUPER_TEXTFIELD_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_passMaxLength,label:ZaMsg.NAD_passMaxLength+":", labelLocation:_LEFT_, textFieldCssClass:"admin_xform_number_input", onChange:ZaTabView.onFormFieldChanged},

								{ref:ZaAccount.A_zimbraPasswordMinUpperCaseChars, labelCssStyle:"width:250px;", 
									type:_SUPER_TEXTFIELD_, 
									resetToSuperLabel:ZaMsg.NAD_ResetToCOS, 
									msgName:ZaMsg.NAD_zimbraPasswordMinUpperCaseChars,label:ZaMsg.NAD_zimbraPasswordMinUpperCaseChars+":", labelLocation:_LEFT_, 
									textFieldCssClass:"admin_xform_number_input", 
									onChange:ZaTabView.onFormFieldChanged
								},
								{ref:ZaAccount.A_zimbraPasswordMinLowerCaseChars, labelCssStyle:"width:250px;", 
									type:_SUPER_TEXTFIELD_, 
									resetToSuperLabel:ZaMsg.NAD_ResetToCOS, 
									msgName:ZaMsg.NAD_zimbraPasswordMinLowerCaseChars,label:ZaMsg.NAD_zimbraPasswordMinLowerCaseChars+":", labelLocation:_LEFT_, 
									textFieldCssClass:"admin_xform_number_input", 
									onChange:ZaTabView.onFormFieldChanged
								},
								{ref:ZaAccount.A_zimbraPasswordMinPunctuationChars, labelCssStyle:"width:250px;", 
									type:_SUPER_TEXTFIELD_, 
									resetToSuperLabel:ZaMsg.NAD_ResetToCOS, 
									msgName:ZaMsg.NAD_zimbraPasswordMinPunctuationChars,label:ZaMsg.NAD_zimbraPasswordMinPunctuationChars+":", labelLocation:_LEFT_, 
									textFieldCssClass:"admin_xform_number_input", 
									onChange:ZaTabView.onFormFieldChanged
								},
								{ref:ZaAccount.A_zimbraPasswordMinNumericChars, labelCssStyle:"width:250px;", 
									type:_SUPER_TEXTFIELD_, 
									resetToSuperLabel:ZaMsg.NAD_ResetToCOS, 
									msgName:ZaMsg.NAD_zimbraPasswordMinNumericChars,label:ZaMsg.NAD_zimbraPasswordMinNumericChars+":", labelLocation:_LEFT_, 
									textFieldCssClass:"admin_xform_number_input", 
									onChange:ZaTabView.onFormFieldChanged
								},
																
								{ref:ZaAccount.A_zimbraMinPwdAge, type:_SUPER_TEXTFIELD_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_passMinAge,label:ZaMsg.NAD_passMinAge+":", labelLocation:_LEFT_, textFieldCssClass:"admin_xform_number_input", onChange:ZaTabView.onFormFieldChanged},
								{ref:ZaAccount.A_zimbraMaxPwdAge, type:_SUPER_TEXTFIELD_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_passMaxAge,label:ZaMsg.NAD_passMaxAge+":", labelLocation:_LEFT_, textFieldCssClass:"admin_xform_number_input", onChange:ZaTabView.onFormFieldChanged},
								{ref:ZaAccount.A_zimbraEnforcePwdHistory, type:_SUPER_TEXTFIELD_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_passEnforceHistory,label:ZaMsg.NAD_passEnforceHistory+":", labelLocation:_LEFT_, textFieldCssClass:"admin_xform_number_input", onChange:ZaTabView.onFormFieldChanged}
							]
						},
						{type:_SEPARATOR_, colSpan:"*"},
						{type:_GROUP_, id:"password_lockout_settings",
							items :[
								{ref:ZaAccount.A_zimbraPasswordLocked, type:_SUPER_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_PwdLocked,label:ZaMsg.NAD_PwdLocked, labelLocation:_LEFT_, trueValue:"TRUE", falseValue:"FALSE", onChange:ZaTabView.onFormFieldChanged},
								{ref:ZaAccount.A_zimbraPasswordLockoutEnabled, type:_SUPER_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, 
									msgName:ZaMsg.NAD_zimbraPasswordLockoutEnabled,label:ZaMsg.NAD_zimbraPasswordLockoutEnabled, 
									labelLocation:_LEFT_, 
									labelCssStyle:"width:250px;", trueValue:"TRUE", falseValue:"FALSE", 
									onChange:ZaTabView.onFormFieldChanged
								},
								{ref:ZaAccount.A_zimbraPasswordLockoutMaxFailures, type:_SUPER_TEXTFIELD_, 
									relevant: "instance.attrs[ZaAccount.A_zimbraPasswordLockoutEnabled] == 'TRUE'",
								 	relevantBehavior: _DISABLE_,
									label:ZaMsg.NAD_zimbraPasswordLockoutMaxFailures+":",
									subLabel:ZaMsg.NAD_zimbraPasswordLockoutMaxFailuresSub,
									msgName:ZaMsg.NAD_zimbraPasswordLockoutMaxFailures,
									labelLocation:_LEFT_, 
									textFieldCssClass:"admin_xform_number_input", 
									onChange:ZaTabView.onFormFieldChanged, 
									resetToSuperLabel:ZaMsg.NAD_ResetToCOS,
									labelCssStyle:"width:250px;"
								},
								{ref:ZaAccount.A_zimbraPasswordLockoutDuration, type:_SUPER_LIFETIME_, 
									relevant: "instance.attrs[ZaAccount.A_zimbraPasswordLockoutEnabled] == 'TRUE'",
									relevantBehavior: _DISABLE_,
									label:ZaMsg.NAD_zimbraPasswordLockoutDuration+":",
									subLabel:ZaMsg.NAD_zimbraPasswordLockoutDurationSub,
									msgName:ZaMsg.NAD_zimbraPasswordLockoutDuration,
									labelLocation:_LEFT_, 
									textFieldCssClass:"admin_xform_number_input", 
									onChange:ZaTabView.onFormFieldChanged, 
									resetToSuperLabel:ZaMsg.NAD_ResetToCOS,
									labelCssStyle:"width:250px;"
								},
								{ref:ZaAccount.A_zimbraPasswordLockoutFailureLifetime, type:_SUPER_LIFETIME_, 
									relevant: "instance.attrs[ZaAccount.A_zimbraPasswordLockoutEnabled] == 'TRUE'",
									relevantBehavior: _DISABLE_,								
									label:ZaMsg.NAD_zimbraPasswordLockoutFailureLifetime+":",
									subLabel:ZaMsg.NAD_zimbraPasswordLockoutFailureLifetimeSub,
									msgName:ZaMsg.NAD_zimbraPasswordLockoutFailureLifetime,
									labelLocation:_LEFT_, 
									textFieldCssClass:"admin_xform_number_input", 
									onChange:ZaTabView.onFormFieldChanged, 
									resetToSuperLabel:ZaMsg.NAD_ResetToCOS,
									labelCssStyle:"width:250px;white-space:normal;",
									nowrap:false,labelWrap:true
								}																		
							]
						},
						{type:_SEPARATOR_, colSpan:"*"},							
						{type:_GROUP_, 
							items: [
								{ref:ZaAccount.A_zimbraAuthTokenLifetime, labelCssStyle:"width:250px;", type:_SUPER_LIFETIME_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_AuthTokenLifetime,label:ZaMsg.NAD_AuthTokenLifetime+":",labelLocation:_LEFT_, onChange:ZaTabView.onFormFieldChanged},								
								{ref:ZaAccount.A_zimbraMailIdleSessionTimeout, labelCssStyle:"width:250px;", type:_SUPER_LIFETIME_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_MailIdleSessionTimeout,label:ZaMsg.NAD_MailIdleSessionTimeout+":",labelLocation:_LEFT_, onChange:ZaTabView.onFormFieldChanged},																
								{ref:ZaAccount.A_zimbraMailMessageLifetime, type:_SUPER_LIFETIME1_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_MailMessageLifetime,label:ZaMsg.NAD_MailMessageLifetime+":",labelLocation:_LEFT_, onChange:ZaTabView.onFormFieldChanged},
								{ref:ZaAccount.A_zimbraMailTrashLifetime, type:_SUPER_LIFETIME1_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_MailTrashLifetime,label:ZaMsg.NAD_MailTrashLifetime+":", labelLocation:_LEFT_, onChange:ZaTabView.onFormFieldChanged},
								{ref:ZaAccount.A_zimbraMailSpamLifetime, type:_SUPER_LIFETIME1_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_MailSpamLifetime,label:ZaMsg.NAD_MailSpamLifetime, labelLocation:_LEFT_, onChange:ZaTabView.onFormFieldChanged}
							]
						}					
			
					]
				});
	}
	
	xFormObject.tableCssStyle="width:100%;overflow:auto;";
	xFormObject.items = [
			{type:_GROUP_, cssClass:"ZmSelectedHeaderBg", colSpan: "*", 
				items: [
					{type:_GROUP_,	numCols:4,colSizes:["90px","350px","100px","200px"],items:headerItems}
				],
				cssStyle:"padding-top:5px; padding-bottom:5px"
			},
			{type:_TAB_BAR_,  ref:ZaModel.currentTab,choices:tabChoices,cssClass:"ZaTabBar"},
			{type:_SWITCH_, align:_LEFT_, valign:_TOP_, items:cases}
	];
};
ZaTabView.XFormModifiers["ZaAccountXFormView"].push(ZaAccountXFormView.myXFormModifier);
