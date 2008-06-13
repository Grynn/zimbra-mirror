if (AjxEnv.hasFirebug) console.log ("Starting loading domainadmin.js");

function ZaDomainAdmin() {};

var soapDoc = AjxSoapDoc.create("GetInfoRequest", "urn:zimbraAccount", null);	
//var resp = ZmCsfeCommand.invoke(soapDoc, null, null, null, false);
var command = new ZmCsfeCommand();
var params = new Object();
params.soapDoc = soapDoc;	
params.noSession = true;
var resp = command.invoke(params);	
var info = resp.Body.GetInfoResponse;
var adminName = resp.Body.GetInfoResponse.name;
if(adminName) {
	var emailChunks = adminName.split("@");
	var tmpDomain = new ZaDomain();
	if(emailChunks.length > 1 ) {
		tmpDomain.name = emailChunks[1];
		ZaSettings.myDomainName = emailChunks[1];
		EmailAddr_XFormItem.domainChoices.setChoices([tmpDomain]);
		EmailAddr_XFormItem.domainChoices.dirtyChoices();							    
	} else {
	//	throw new AjxException(ZaMsg.ERROR_PARSE_LOGIN_NAME, AjxException.UNKNOWN, "domainadmin.js");
	}	
}
if (info.attrs && info.attrs.attr) {
	var attr = info.attrs.attr;
	for (var i = 0; i < attr.length; i++) {
		if (attr[i].name == 'zimbraIsDomainAdminAccount') {
			DBG.println(AjxDebug.DBG1,"found zimbraIsDomainAdminAccount " + attr[i]._content);
			ZaSettings.isDomainAdmin = attr[i]._content == 'TRUE';
		}
		
		if (attr[i].name == "zimbraDomainAdminMaxMailQuota") {
			var v = attr[i]._content ;
			if (v != null && v >= 0) {
				v = v / 1048576 ;
				if(v != Math.round(v)) {
					v= Number(v).toFixed(2);
  				}
			}
			ZaDomainAdmin.domainAdminMaxMailQuota = v ;
		}
	}
} else if (info.attrs && info.attrs._attrs) {
	var attrs = info.attrs._attrs;
	if(attrs['zimbraIsDomainAdminAccount']) {
		DBG.println(AjxDebug.DBG1,"found zimbraIsDomainAdminAccount " + attrs["zimbraIsDomainAdminAccount"]);
		ZaSettings.isDomainAdmin = attrs["zimbraIsDomainAdminAccount"] == 'TRUE';
	}
		
	if(attrs["zimbraDomainAdminMaxMailQuota"]) {
		var v = attrs["zimbraDomainAdminMaxMailQuota"];
		if (v != null && v >= 0) {
			v = v / 1048576 ;
			if(v != Math.round(v)) {
				v= Number(v).toFixed(2);
			}
		}
		ZaDomainAdmin.domainAdminMaxMailQuota = v ;
	} 
}



ZaDomainAdmin.validateQuota =
function (value, event, form) {
	if (ZaDomainAdmin.domainAdminMaxMailQuota > 0 && value > ZaDomainAdmin.domainAdminMaxMailQuota) {
		this.setError (AjxMessageFormat.format (com_zimbra_yahoosmb.tt_domainAdminMaxQuota, [ZaDomainAdmin.domainAdminMaxMailQuota]));
	} else if (ZaDomainAdmin.domainAdminMaxMailQuota > 0 && (value == 0 || !value)) {
		this.setError (AjxMessageFormat.format (com_zimbra_yahoosmb.tt_domainAdminUnlimitedQuota, [ZaDomainAdmin.domainAdminMaxMailQuota]));
	}
	if (form.parent instanceof ZaAccountXFormView) {
		form.parent.setDirty(true);	
	}
	this.setInstanceValue(value);
}

ZaDomainAdmin.onDomainAdminMailQuotaAllowed4account =
function (value, event, form){
	DBG.println(AjxDebug.DBG1, "The new value = " + value) ;
	var instance = form.getInstance ();
	if (value == 'TRUE') {
		instance.attrs[ZaAccount.A_zimbraDomainAdminMaxMailQuota] = 0;
	}else if (value == 'FALSE'){
		instance.attrs[ZaAccount.A_zimbraDomainAdminMaxMailQuota] = -1;
	}
	
	if (form.parent instanceof ZaAccountXFormView) {
		form.parent.setDirty(true);	
	}
	
	this.setInstanceValue (value) ;
	form.refresh();
}

ZaDomainAdmin.onDomainAdminMailQuotaAllowed4cos =
function (value, event, form){
	DBG.println(AjxDebug.DBG1, "The new value = " + value) ;
	var instance = form.getInstance ();
	if (value == 'TRUE') {
		instance.attrs[ZaCos.A_zimbraDomainAdminMaxMailQuota] = 0;
	}else{
		instance.attrs[ZaCos.A_zimbraDomainAdminMaxMailQuota] = -1;
	}
	if (form.parent instanceof ZaCosXFormView) {
		form.parent.setDirty(true);	
	}
	
	this.setInstanceValue (value) ;
	form.refresh();
}
				
if(ZaAccount) {
	ZaAccount.A_zimbraIsDomainAdminAccount = "zimbraIsDomainAdminAccount";
	ZaAccount.myXModel.items.push({id:ZaAccount.A_zimbraIsDomainAdminAccount, type:_ENUM_, choices:ZaModel.BOOLEAN_CHOICES, ref:"attrs/"+ZaAccount.A_zimbraIsDomainAdminAccount});
	ZaAccount.A_zimbraDomainAdminMaxMailQuota = "zimbraDomainAdminMaxMailQuota" ;
	ZaAccount.A2_zimbraDomainAdminMailQuotaAllowed = "zimbraDomainAdminMailQuotaAllowed" ;
	var domainAdminMaxMailQuotaItem = {id:ZaAccount.A_zimbraDomainAdminMaxMailQuota, type:_COS_MAILQUOTA_, ref:"attrs."+ZaAccount.A_zimbraDomainAdminMaxMailQuota} ;
	var domainAdminMailQuotaAllowedItem = {id:ZaAccount.A2_zimbraDomainAdminMailQuotaAllowed, type:_COS_ENUM_, choices:ZaModel.BOOLEAN_CHOICES, ref:ZaAccount.A2_zimbraDomainAdminMailQuotaAllowed} ;
	if (ZaSettings.isDomainAdmin) {
		domainAdminMaxMailQuotaItem.type = _MAILQUOTA_ ;	
		domainAdminMailQuotaAllowedItem.type = _ENUM_;
	}
	ZaAccount.myXModel.items.push(domainAdminMaxMailQuotaItem); 	
	ZaAccount.myXModel.items.push(domainAdminMailQuotaAllowedItem); 	
}

if (ZaSearchOption) {
	ZaSearchOption.A_objTypeAccountDomainAdmin = "zimbraIsDomainAdminAccount";
}

if (ZaCos) {
	ZaCos.A_zimbraDomainAdminMaxMailQuota = "zimbraDomainAdminMaxMailQuota" ;
	ZaCos.A2_zimbraDomainAdminMailQuotaAllowed = "zimbraDomainAdminMailQuotaAllowed" ;
	ZaCos.myXModel.items.push({id:ZaCos.A2_zimbraDomainAdminMailQuotaAllowed, type:_ENUM_, choices:ZaModel.BOOLEAN_CHOICES, ref:ZaCos.A2_zimbraDomainAdminMailQuotaAllowed}); 	
	ZaCos.myXModel.items.push({id:ZaCos.A_zimbraDomainAdminMaxMailQuota, type:_MAILQUOTA_, ref:"attrs."+ZaCos.A_zimbraDomainAdminMaxMailQuota}); 	
}

if(ZaSettings) {
	ZaDomainAdmin.initSettings = function() {
	    if (AjxEnv.hasFirebug) console.log("domainadmin.js is modifying ZaSettings");
        var domainAdmin = ZaSettings.isDomainAdmin;
		if(domainAdmin)
			DBG.println(AjxDebug.DBG1,"I am domain admin");
		else
			DBG.println(AjxDebug.DBG1,"I am global admin");			

		ZaSettings.TOOLS_ENABLED = !domainAdmin;	
	    ZaSettings.MONITORING_ENABLED = !domainAdmin;
	    ZaSettings.SYSTEM_CONFIG_ENABLED = !domainAdmin;
	    ZaSettings.SERVERS_ENABLED = !domainAdmin;
	    ZaSettings.SERVER_STATS_ENABLED = !domainAdmin;
		ZaSettings.STATUS_ENABLED = !domainAdmin;
	    ZaSettings.COSES_ENABLED= !domainAdmin;
	    ZaSettings.DOMAINS_ENABLED= !domainAdmin;
	    ZaSettings.ACCOUNTS_FEATURES_ENABLED = !domainAdmin;
	    ZaSettings.ACCOUNTS_RESTORE_ENABLED = !domainAdmin;
	    ZaSettings.ACCOUNTS_PREFS_ENABLED = !domainAdmin;
	    ZaSettings.SKIN_PREFS_ENABLED = !domainAdmin;
	    ZaSettings.ACCOUNTS_REINDEX_ENABLED = !domainAdmin;
	    ZaSettings.ACCOUNTS_ADVANCED_ENABLED = !domainAdmin;	
	    ZaSettings.ACCOUNTS_VIEW_MAIL_ENABLED = !domainAdmin;	
	    ZaSettings.GLOBAL_CONFIG_ENABLED = !domainAdmin;
	    ZaSettings.ZIMLETS_ENABLED = !domainAdmin;
	    ZaSettings.ADMIN_ZIMLETS_ENABLED = !domainAdmin;	    
	    //bug: 9794 ZaSettings.LICENSE_ENABLED = !domainAdmin ;
	    if(domainAdmin) {
			if(ZaXDialog) {
				ZaXDialog.helpURL = location.pathname + "help/delegated/da_about_zimbra_collaboration_suite.htm?locid=" + AjxEnv.DEFAULT_LOCALE;
			}
			if(ZaController) {
				ZaController.helpURL = location.pathname + "help/delegated/da_about_zimbra_collaboration_suite.htm?locid=" + AjxEnv.DEFAULT_LOCALE;
			}
			if(ZaAccountListController) {
				ZaAccountListController.helpURL = location.pathname + "help/delegated/da_domain_administrator_responsibilities.htm?locid=" + AjxEnv.DEFAULT_LOCALE;
			}
			if(ZaAccountViewController) {
				ZaAccountViewController.helpURL = location.pathname + "help/delegated/da_editing_an_account.htm?locid=" + AjxEnv.DEFAULT_LOCALE;
			}
			if(ZaNewAccountXWizard) {
				ZaNewAccountXWizard.helpURL = location.pathname + "help/delegated/da_creating_an_account.htm?locid=" + AjxEnv.DEFAULT_LOCALE;
			}	    
	    }
	}
	if(ZaSettings.initMethods)
		ZaSettings.initMethods.push(ZaDomainAdmin.initSettings);
}

if(ZaTabView.XFormModifiers["ZaAccountXFormView"]) {
	ZaDomainAdmin.AccountXFormModifier = function(xFormObject) {
		//remove "Is Adminitrator checkbox" from first tab
		var domainAdminChkBx = {ref:ZaAccount.A_zimbraIsDomainAdminAccount,type:_CHECKBOX_, 
								msgName:com_zimbra_yahoosmb.NAD_IsDomainAdminAccount,label:com_zimbra_yahoosmb.NAD_IsDomainAdminAccount,
								trueValue:"TRUE", falseValue:"FALSE",
								relevantBehavior:_DISABLE_,
								relevant:"instance.attrs[ZaAccount.A_isAdminAccount]!=\'TRUE\'",
								onChange:ZaTabView.onFormFieldChanged
							};
		
		var domainAdminMailQuotaBx = {ref:ZaAccount.A2_zimbraDomainAdminMailQuotaAllowed,type:_SUPER_CHECKBOX_, 
								msgName:com_zimbra_yahoosmb.NAD_DomainAdminMailQuotaAllowed,
								label:null,checkBoxLabel:com_zimbra_yahoosmb.NAD_DomainAdminMailQuotaAllowed,
								resetToSuperLabel:ZaMsg.NAD_ResetToCOS,
								trueValue:"TRUE", falseValue:"FALSE",
								relevantBehavior:_HIDE_,
								relevant:"instance.attrs[ZaAccount.A_zimbraIsDomainAdminAccount]==\'TRUE\'",
								onChange:ZaDomainAdmin.onDomainAdminMailQuotaAllowed4account
							};
							
		var accountDomainAdminMaxQuotaField = 
			{ ref:ZaAccount.A_zimbraDomainAdminMaxMailQuota, type:_SUPER_TEXTFIELD_,
				type:_SUPER_TEXTFIELD_, resetToSuperLabel: ZaMsg.NAD_ResetToCOS,
				msgName:com_zimbra_yahoosmb.NAD_DomainAdminMaxMailQuota,
				onChange:ZaTabView.onFormFieldChanged,
				txtBoxLabel:com_zimbra_yahoosmb.NAD_DomainAdminMaxMailQuota,
				toolTipContent: com_zimbra_yahoosmb.tt_DomainAdminMaxMailQuota,
				relevantBehavior: _HIDE_,
				relevant: "instance.attrs[ZaAccount.A_zimbraIsDomainAdminAccount]==\'TRUE\' && (instance[ZaAccount.A2_zimbraDomainAdminMailQuotaAllowed]==\'TRUE\' || (!instance[ZaAccount.A2_zimbraDomainAdminMailQuotaAllowed] && instance.cos[ZaAccount.A2_zimbraDomainAdminMailQuotaAllowed]==\'TRUE\'))"
			};

		
		var domainAdminMailQuotaItem = 
			{ref:ZaAccount.A_zimbraMailQuota, type:_TEXTFIELD_, 
				label:ZaMsg.NAD_MailQuota+":", msgName:ZaMsg.NAD_MailQuota,labelLocation:_LEFT_, 
				textFieldCssClass:"admin_xform_number_input", 
				onChange:ZaDomainAdmin.validateQuota, 
				labelCssStyle:"width:250px;"
			};
		var mailForwardingEnabledChkBx = 
			{
				ref:ZaAccount.A_zimbraFeatureMailForwardingEnabled,
				type:_CHECKBOX_, 
				label:ZaMsg.NAD_zimbraFeatureMailForwardingEnabled,  
				trueValue:"TRUE", falseValue:"FALSE",
				onChange:ZaTabView.onFormFieldChanged
			};
		//change Forwarding tab
		var tabs = xFormObject.items[2].items;
		if(ZaSettings.isDomainAdmin) {
			var cntTabs = tabs.length;
			for(var i=0; i < cntTabs; i++) {
				if(tabs[i].id == "account_form_forwarding_tab") {
					var tabItems = tabs[i].items;
					var cntItems = tabItems.length;	
					for(var j = 0; j < cntItems; j++) {
						if(tabItems[j].id == "account_form_user_forwarding_addr" && tabItems[j].items) {
							tabItems[j].numCols = 2;
							tabItems[j].colSizes = ["275","*"];
							var tabGroupItems = tabItems[j].items;
							var cntGroupItems = tabGroupItems.length;	
							for(var k = 0; k < cntGroupItems; k++) {							
								if(tabGroupItems[j].ref && tabGroupItems[j].ref == ZaAccount.A_zimbraFeatureMailForwardingEnabled) {
									tabGroupItems[j] = mailForwardingEnabledChkBx;
									break;
								}
							}
							break;
						}
					}
					break;
				}
			}
		}
		//change General tab
		var tmpItems = tabs[0].items;
		var cnt = tmpItems.length;
		for(var i = 0; i < cnt; i ++) { 
			if(tmpItems[i].id == "account_form_setup_group" && tmpItems[i].items) {
				var tmpGrouperItems = tmpItems[i].items;
				var cnt2 = tmpGrouperItems.length;
				for(var j=0;j<cnt2;j++) {
					if(tmpGrouperItems[j] && tmpGrouperItems[j].ref == ZaAccount.A_isAdminAccount) {
						if(ZaSettings.isDomainAdmin) {
							//remove "Administrator" checkbox from Domain Admin's view
							xFormObject.items[2].items[0].items[i].items.splice(j,1);
							
						} else {
							//make sure Domain Admin and Administrator checkboxes are mutualy exclusive
							tmpGrouperItems[j].relevant = "!ZaSettings.isDomainAdmin";
							tmpGrouperItems[j].elementChanged = 
								function(elementValue,instanceValue, event) {
									if(elementValue == "TRUE") {
										this.setInstanceValue("FALSE", ZaAccount.A_zimbraIsDomainAdminAccount);
										}
										this.getForm().itemChanged(this, elementValue, event);
								};
						}
						//add Domain Admin checkbox and domain admin quota field
                        if (ZaSettings.isYahooPA) {

                        }else {
                            xFormObject.items[2].items[0].items[i].items.splice(j,0, domainAdminChkBx);

                            if (ZaSettings.isDomainAdmin ){
                                if (ZaDomainAdmin.domainAdminMaxMailQuota >= 0) {
                                    if (ZaDomainAdmin.domainAdminMaxMailQuota > 0){
                                        domainAdminMailQuotaItem.toolTipContent = AjxMessageFormat.format (com_zimbra_yahoosmb.tt_domainAdminMaxQuota, [ZaDomainAdmin.domainAdminMaxMailQuota]);
                                    }
                                    xFormObject.items[2].items[0].items[i].items.splice(j+1,0, domainAdminMailQuotaItem);
                                }
                            }else{
                                xFormObject.items[2].items[0].items[i].items.splice(j+1,0, domainAdminMailQuotaBx, accountDomainAdminMaxQuotaField);
                            }
                        }

                        break;
					}
				}
				break;
			}
		}
		
		for(var i=1;i<tabs.length;i++) {
			if(tabs[i].id=="account_form_advanced_tab") {
				var tmpItems = tabs[i].items;
				var cnt = tmpItems.length;
				for(var j = 0; j < cnt; j ++) { 
					if(tmpItems[j].id == "timeout_settings" && tmpItems[j].items) {
						var tmpGrouperItems = tmpItems[j].items;
						var cnt2 = tmpGrouperItems.length;
						for(var k=0;k<cnt2;k++) {						
							if(tmpGrouperItems[k] && tmpGrouperItems[k].ref == ZaAccount.A_zimbraAdminAuthTokenLifetime) {							
								tmpGrouperItems[k].relevant = "instance.attrs[ZaAccount.A_zimbraIsDomainAdminAccount]==\'TRUE\' || instance.attrs[ZaAccount.A_isAdminAccount]==\'TRUE\'";
								break;
							}
						}
						break;
					}
				}
				break;
			}
		}
		
				
		if(ZaSettings.isDomainAdmin) {
			//do not show zimbra ID in the header to domain admins
			tmpItems = xFormObject.items[0].items[0].items;
			var cnt = tmpItems.length;
			for(var i = 0; i < cnt; i ++) { 
				if(tmpItems[i] && tmpItems[i].ref == ZaItem.A_zimbraId) {
					xFormObject.items[0].items[0].items.splice(i,1);
					break;
				}
			}
		}	
	}

	ZaTabView.XFormModifiers["ZaAccountXFormView"].push(ZaDomainAdmin.AccountXFormModifier);
}
ZaDomainAdmin.domainQuotaFieldRelevant = function(instance,item) {
	
	return (instance.attrs[ZaAccount.A_zimbraIsDomainAdminAccount]=="TRUE" 
		&& item.getInstanceValue(ZaAccount.A2_zimbraDomainAdminMailQuotaAllowed)=="TRUE");
}

if(ZaXDialog.XFormModifiers["ZaNewAccountXWizard"]) {
	ZaDomainAdmin.NewAccountWizXFormModifier = function(xFormObject) {
		//remove "Is Adminitrator checkbox" from first tab
		var domainAdminChkBx = {ref:ZaAccount.A_zimbraIsDomainAdminAccount,type:_CHECKBOX_, 
							msgName:com_zimbra_yahoosmb.NAD_IsDomainAdminAccount,label:com_zimbra_yahoosmb.NAD_IsDomainAdminAccount,
							trueValue:"TRUE", falseValue:"FALSE",
							relevantBehavior:_DISABLE_,
							relevant:"instance.attrs[ZaAccount.A_isAdminAccount]!=\'TRUE\'"
						};
						
		var accountDomainAdminMaxQuotaField = 
				{ ref:ZaAccount.A_zimbraDomainAdminMaxMailQuota, type:_SUPERWIZ_TEXTFIELD_,
					msgName:com_zimbra_yahoosmb.NAD_DomainAdminMaxMailQuota,
					label:null, txtBoxLabel: com_zimbra_yahoosmb.NAD_DomainAdminMaxMailQuota,
					labelLocation:_LEFT_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS,
					toolTipContent: com_zimbra_yahoosmb.tt_DomainAdminMaxMailQuota,
					relevantBehavior: _HIDE_,
					relevant: "ZaDomainAdmin.domainQuotaFieldRelevant.call(this,instance,item)"
				};
		
		var domainAdminMailQuotaBx = {ref:ZaAccount.A2_zimbraDomainAdminMailQuotaAllowed,
								type:_SUPER_WIZ_CHECKBOX_, 
								msgName:com_zimbra_yahoosmb.NAD_DomainAdminMailQuotaAllowed ,
								checkBoxLabel:com_zimbra_yahoosmb.NAD_DomainAdminMailQuotaAllowed ,
								trueValue:"TRUE", falseValue:"FALSE",
								onChange:ZaDomainAdmin.onDomainAdminMailQuotaAllowed4account,
								relevantBehavior:_HIDE_,resetToSuperLabel:ZaMsg.NAD_ResetToCOS,
								relevant:"instance.attrs[ZaAccount.A_zimbraIsDomainAdminAccount]==\'TRUE\'"
							};
							
		var domainAdminMailQuotaItem = 
				{ref:ZaAccount.A_zimbraMailQuota, type:_TEXTFIELD_, 
					label:ZaMsg.NAD_MailQuota+":", msgName:ZaMsg.NAD_MailQuota,labelLocation:_LEFT_, 
					textFieldCssClass:"admin_xform_number_input", 
					onChange:ZaDomainAdmin.validateQuota, labelCssStyle:"width:250px;"
				};
	
		var mailForwardingEnabledChkBx = 
			{
				ref:ZaAccount.A_zimbraFeatureMailForwardingEnabled,
				type:_CHECKBOX_, 
				label:ZaMsg.NAD_zimbraFeatureMailForwardingEnabled,  
				trueValue:"TRUE", falseValue:"FALSE"
			};	
		//change Forwarding step
		var steps = xFormObject.items[3].items;
		if(ZaSettings.isDomainAdmin) {
			var cntTabs = steps.length;
			for(var i=0; i < cntTabs; i++) {
				if(steps[i].id == "account_form_forwarding_step") {
					var stepItems = steps[i].items;
					var cntItems = stepItems.length;	
					for(var j = 0; j < cntItems; j++) {
						if(stepItems[j].ref && stepItems[j].ref == ZaAccount.A_zimbraFeatureMailForwardingEnabled) {
							stepItems[j] = mailForwardingEnabledChkBx;
							break;
						}
					}
					break;
				}
			}
		}			
		var tmpItems = steps[0].items;
		var cnt = tmpItems.length;
		for(var i = 0; i < cnt; i ++) { 
			if(tmpItems[i].id == "account_wiz_setup_group" && tmpItems[i].items) {
				var tmpGrouperItems = tmpItems[i].items;
				var cnt2 = tmpGrouperItems.length;
				for(var j=0;j<cnt2;j++) {
					if(tmpGrouperItems[j] && tmpGrouperItems[j].ref == ZaAccount.A_isAdminAccount) {
						if(ZaSettings.isDomainAdmin) {
							//remove "Administrator" checkbox from Domain Admin's view
							xFormObject.items[3].items[0].items[i].items.splice(j,1);
							
						} else {
							//make sure Domain Admin and Administrator checkboxes are mutualy exclusive
							tmpGrouperItems[j].relevant = "!ZaSettings.isDomainAdmin";
							tmpGrouperItems[j].elementChanged = 
								function(elementValue,instanceValue, event) {
									if(elementValue == "TRUE") {
										this.setInstanceValue("FALSE", ZaAccount.A_zimbraIsDomainAdminAccount);
										}
										this.getForm().itemChanged(this, elementValue, event);
								};
						}
						//add Domain Admin checkbox and domain admin quota field
						//NOT required for PA 
                        if (ZaSettings.isYahooPA) {

                        }   else {
                            xFormObject.items[3].items[0].items[i].items.splice(j,0, domainAdminChkBx);

                            if (ZaSettings.isDomainAdmin ){
                                if (ZaDomainAdmin.domainAdminMaxMailQuota >= 0) {
                                    if (ZaDomainAdmin.domainAdminMaxMailQuota > 0){
                                        domainAdminMailQuotaItem.toolTipContent = AjxMessageFormat.format (com_zimbra_yahoosmb.tt_domainAdminMaxQuota, [ZaDomainAdmin.domainAdminMaxMailQuota]);
                                    }
                                    xFormObject.items[3].items[0].items[i].items.splice(j+1,0, domainAdminMailQuotaItem);
                                }
                            }else{
                                xFormObject.items[3].items[0].items[i].items.splice(j+1,0, domainAdminMailQuotaBx, accountDomainAdminMaxQuotaField);
                            }
                        }

                        break;
					}
				}
				break;
			}
		}
		
		for(var i=0;i<steps.length;i++) {
			if(steps[i] && steps[i].id=="account_form_advanced_step" && steps[i].items) {
				var stepItems = steps[i].items;
				var cnt = stepItems.length;
				for(var j=0; j< cnt; j++) {
					if(stepItems[j] && stepItems[j].items && stepItems[j].id=="timeout_settings") {
						var tmpGrouperItems = stepItems[j].items;
						var cnt2 = tmpGrouperItems.length;
						for(var k=0;k<cnt2;k++) {						
							if(tmpGrouperItems[k] && tmpGrouperItems[k].ref == ZaAccount.A_zimbraAdminAuthTokenLifetime) {							
								tmpGrouperItems[k].relevant = "instance.attrs[ZaAccount.A_zimbraIsDomainAdminAccount]==\'TRUE\' || instance.attrs[ZaAccount.A_isAdminAccount]==\'TRUE\'";
								break;
							}
						}
						break;
					}
				}
				break;
			}
		}
	}

	ZaXDialog.XFormModifiers["ZaNewAccountXWizard"].push(ZaDomainAdmin.NewAccountWizXFormModifier);
}

if(ZaTabView.XFormModifiers["ZaHelpView"]) {
	ZaDomainAdmin.HelpViewXFormModifier = function(xFormObject) {
			var helpItems = xFormObject.items[3].items ;
			var networkHelpItems = [
				{type:_GROUP_,numCols:2, items: [
						{type:_OUTPUT_, value:AjxImg.getImageHtml("PDFDoc")},
						{type:_ANCHOR_, cssStyle:"font-size:12px;", showInNewWindow:true, labelLocation:_NONE_, label:com_zimbra_yahoosmb.HELP_OTHER_GUIDES_ISYNC,
							href:(location.pathname + "help/admin/pdf/ZCS_Apple_iSync.pdf?locid=" + AjxEnv.DEFAULT_LOCALE)},
						{type:_OUTPUT_, value:AjxImg.getImageHtml("PDFDoc")},
						{type:_ANCHOR_, cssStyle:"font-size:12px;", showInNewWindow:true, labelLocation:_NONE_, label:com_zimbra_yahoosmb.HELP_OTHER_GUIDES_OUTLOOK,
							href:(location.pathname + "help/admin/pdf/ZCS%20Connector%20for%20Outlook.pdf?locid=" + AjxEnv.DEFAULT_LOCALE)},
						
				 	]	
				},
				
				{type:_GROUP_,numCols:2, id: "HelpOtherGuides",
					items: [
						{type:_OUTPUT_, value:AjxImg.getImageHtml("PDFDoc")},
						//{type:_OUTPUT_, value: ZaMsg.HELP_OTHER_GUIDES},
						{type:_ANCHOR_, cssStyle:"font-size:12px;", showInNewWindow:true, labelLocation:_NONE_, label:ZaMsg.HELP_OTHER_GUIDES_IMPORT,href:(location.pathname + "help/admin/pdf/Import_Wizard_Outlook.pdf?locid=" + AjxEnv.DEFAULT_LOCALE)}
					]
				},
				{type:_SPACER_, colSpan:"*"},	
				{type:_OUTPUT_, cssStyle:"font-size:12px;", label:null, value:ZaMsg.HELP_OTHER_GUIDES_CONNNECTOR_INFO, 
				 	cssStyle:"padding-right:10px;padding-left:10px;"},
				 {type:_OUTPUT_, cssStyle:"font-size:12px;", label:null, value:ZaMsg.HELP_OTHER_GUIDES_IMPORT_INFO, 
					 cssStyle:"padding-right:10px;padding-left:10px;"},
				{type:_SEPARATOR_, colSpan:1, cssClass:"helpSeparator"},
				{type:_SEPARATOR_, colSpan:1, cssClass:"helpSeparator"}
			];
			for (var i=0; i< helpItems.length; i++) {
				if (helpItems[i].id == "HelpOtherGuides") {
					//helpItems[i].items = helpItems[i].items.concat (networkHelpItems) ;
					helpItems.splice (i,7) ; //remove the old HelpOtherGuides column
					for (var j=0; j < networkHelpItems.length; j ++) {
						helpItems.splice (i+j,0,networkHelpItems[j]) ;
					}
					break;
				}
			}
		//}
	}
	ZaTabView.XFormModifiers["ZaHelpView"].push(ZaDomainAdmin.HelpViewXFormModifier);	
}

if(ZaTabView.XFormModifiers["ZaMigrationWizView"]) {
	ZaDomainAdmin.MigViewXFormModifier = function(xFormObject) {
		var cnt = xFormObject.items.length;
		for(var ix=0;ix<cnt;ix++) {
			if(xFormObject.items[ix].zName == "DownloadsForAdmin") {
				xFormObject.items[ix].items = xFormObject.items[ix].items.concat([
					//MSI Customizer for ZCO
					{type:_GROUP_,numCols:2,
						items: [
							{type:_OUTPUT_, value:AjxImg.getImageHtml("MigrationWiz")},
							{type:_OUTPUT_, cssStyle:"font-size:12px;", labelLocation:_NONE_, label:null,
                                value: ZaMigrationWizView.getDownloadLink(ZaMsg.CONNECTOR_MSI_DOWNLOAD_LINK, ZaMsg.CONNECTOR_MSI_DOWNLOAD_LINK_MSG)
                            }						
						]
					},
					{type:_OUTPUT_, cssClass:"ZaDownloadText", label: null, value:com_zimbra_yahoosmb.CONNECTOR_MSI_DOWNLOAD_TEXT}
				]);
			}
			
			if(xFormObject.items[ix].zName == "DownloadsForUser") {
				xFormObject.items[ix].items = xFormObject.items[ix].items.concat([
					//ZCO
					{type:_GROUP_,numCols:3,
						items: [
							{type:_OUTPUT_, value:AjxImg.getImageHtml("MigrationWiz")},
							{type:_OUTPUT_, cssStyle:"font-size:12px;", labelLocation:_NONE_, label:null,
                                value: ZaMigrationWizView.getDownloadLink(ZaMsg.CONNECTOR_DOWNLOAD_LINK, ZaMsg.CONNECTOR_DOWNLOAD_LINK_MSG)
                            },
							{type:_OUTPUT_, cssStyle:"font-size:12px;", labelLocation:_NONE_, label:null,
                                value:["(<A target='_blank' onclick='ZaZimbraAdmin.unloadHackCallback();' HREF='",location.pathname,"help/admin/pdf/User%20Instructions%20Connector%20for%20Outlook.pdf?locid=",AjxEnv.DEFAULT_LOCALE,"'>",ZaMsg.CONNECTOR_DOWNLOAD_HELP,"</a>)"].join("")}
						]
					},
					{type:_OUTPUT_, cssClass:"ZaDownloadText", label: null, value:com_zimbra_yahoosmb.CONNECTOR_DOWNLOAD_TEXT},
					
					//iSync					
					{type:_GROUP_,numCols:2,
						items: [
							{type:_OUTPUT_, value:AjxImg.getImageHtml("MigrationWiz")},
							{type:_OUTPUT_, cssStyle:"font-size:12px;", labelLocation:_NONE_, label:null,
                                value:ZaMigrationWizView.getDownloadLink(ZaMsg.CONNECTOR_ISYNC_DOWNLOAD_LINK, ZaMsg.CONNECTOR_ISYNC_DOWNLOAD_LINK_MSG)
                            }						
						]
					},
					{type:_OUTPUT_, cssClass:"ZaDownloadText", label: null, value:com_zimbra_yahoosmb.CONNECTOR_ISYNC_DOWNLOAD_TEXT}
				]);
			}
		}
	}
	ZaTabView.XFormModifiers["ZaMigrationWizView"].push(ZaDomainAdmin.MigViewXFormModifier);
}

//modify the cos UI
if(ZaTabView.XFormModifiers["ZaCosXFormView"]) {
	ZaDomainAdmin.CosXFormModifier = function(xFormObject) {
		//add the zimbraDomainAdminMaxMailQuota
		var seperator  = {type:_SEPARATOR_, colSpan:"*"};
		var cosDomainAdminMaxQuotaField = { 
					ref:ZaCos.A_zimbraDomainAdminMaxMailQuota, type:_TEXTFIELD_, 
					msgName:com_zimbra_yahoosmb.NAD_DomainAdminMaxMailQuota,label:com_zimbra_yahoosmb.NAD_DomainAdminMaxMailQuota,
					labelLocation:_LEFT_, 
					cssClass:"admin_xform_number_input", 
					relevant:"instance[ZaAccount.A2_zimbraDomainAdminMailQuotaAllowed]==\'TRUE\'",
					onChange:ZaTabView.onFormFieldChanged
				};
		
		var cosDomainAdminMailQuotaBx = {ref:ZaCos.A2_zimbraDomainAdminMailQuotaAllowed,type:_CHECKBOX_, 
							msgName:com_zimbra_yahoosmb.NAD_DomainAdminMailQuotaAllowed ,label:com_zimbra_yahoosmb.NAD_DomainAdminMailQuotaAllowed ,
							trueValue:"TRUE", falseValue:"FALSE",
							onChange:ZaDomainAdmin.onDomainAdminMailQuotaAllowed4cos
						};
		

		var tabCases = xFormObject.items[2].items;
		var advCase = null;
		var cnt = tabCases.length;
		for(var i=0; i<cnt;i++ ) {
			if(tabCases[i].id == "cos_form_advanced_tab") { 
				advCase = tabCases[i];
				break;
			}
		}
		if(advCase) {
			var tmpItems = advCase.items;
			var cnt2 = tmpItems.length;
			for(var i = 0; i < cnt2; i ++) { 
				if(tmpItems[i].id == "cos_quota_settings" && tmpItems[i].items) {
					var tmpGrouperItems = tmpItems[i].items;
					var cnt2 = tmpGrouperItems.length;
					for(var j=0;j<cnt2;j++) {
						if(tmpGrouperItems[j] && tmpGrouperItems[j].ref == ZaCos.A_zimbraMailQuota) {	
							tmpGrouperItems.splice(j+1, 0, cosDomainAdminMailQuotaBx,cosDomainAdminMaxQuotaField);
							break;
						}
					}
					break;
				}
			}	
		}
	}

	ZaTabView.XFormModifiers["ZaCosXFormView"].push(ZaDomainAdmin.CosXFormModifier) ;
	
}
/*
ZaDomainAdmin.cosViewMethod =
function () {
	DBG.println(AjxDebug.DBG1, "The cos view modification method is called for domain admin");
	var xform = this._view._localXForm ;
	var instance  = xform.getInstance ();
	if (instance.attrs[ZaCos.A_zimbraDomainAdminMaxMailQuota] >= 0) {
		instance [ZaCos.A2_zimbraDomainAdminMailQuotaAllowed] = 'TRUE';
	}else{
		instance [ZaCos.A2_zimbraDomainAdminMailQuotaAllowed] = 'FALSE' ;
	}
	xform.refresh ();
}

if (ZaController.setViewMethods["ZaCosController"]) {
	ZaController.setViewMethods["ZaCosController"].push(ZaDomainAdmin.cosViewMethod);
}

ZaDomainAdmin.cosLoadMethod =
function (){
	DBG.println(AjxDebug.DBG1, "The cos object modification method is called for domain admin");
	if (this.attrs[ZaCos.A_zimbraDomainAdminMaxMailQuota] >= 0) {
		this[ZaCos.A2_zimbraDomainAdminMailQuotaAllowed] = 'TRUE';
	}else{
		this[ZaCos.A2_zimbraDomainAdminMailQuotaAllowed] = 'FALSE' ;
	}
}

if (ZaItem.loadMethods["ZaCos"]) {
 	ZaItem.loadMethods["ZaCos"].push(ZaDomainAdmin.cosLoadMethod);
}*/

ZaDomainAdmin.accountLoadMethod =
function () {
	DBG.println(AjxDebug.DBG1, "The account object modification method is called for domain admin");
	if (this.attrs[ZaAccount.A_zimbraDomainAdminMaxMailQuota] >= 0) {
		this[ZaAccount.A2_zimbraDomainAdminMailQuotaAllowed] = 'TRUE';
	}else{
		this[ZaAccount.A2_zimbraDomainAdminMailQuotaAllowed] = null ;
	}
}

if (ZaItem.loadMethods["ZaAccount"]) {
	ZaItem.loadMethods["ZaAccount"].push(ZaDomainAdmin.accountLoadMethod);
}


ZaDomainAdmin.accountViewMethod =
function () {
	DBG.println(AjxDebug.DBG1, "The account view modification method is called for domain admin");
	var xform = this._view._localXForm ;
	var instance  = xform.getInstance ();
		if (instance) {
		if (instance.attrs[ZaAccount.A_zimbraDomainAdminMaxMailQuota] >= 0) {
			instance [ZaAccount.A2_zimbraDomainAdminMailQuotaAllowed] = 'TRUE';
		}else {
			instance [ZaAccount.A2_zimbraDomainAdminMailQuotaAllowed] = null ; //null will allow the cos value to be shown
		}
		xform.refresh ();
	}
}

if (ZaController.setViewMethods["ZaAccountViewController"]) {
	ZaController.setViewMethods["ZaAccountViewController"].push(ZaDomainAdmin.accountViewMethod);
}

if (ZaDomain) {
	ZaDomain.A_domainMaxAccounts = "zimbraDomainMaxAccounts";
	ZaDomain.myXModel.items.push({id:ZaDomain.A_domainMaxAccounts, type:_NUMBER_, ref:"attrs/" + ZaDomain.A_domainMaxAccounts});
}


if (ZaTabView.XFormModifiers["ZaDomainXFormView"]) {
	ZaDomainAdmin.domainXFormModifer = function (xFormObject) {
		var domainXformMaxAccountItem = {ref:ZaDomain.A_domainMaxAccounts, type:_INPUT_, 
							label:com_zimbra_yahoosmb.NAD_MaxAccounts, width:50,
						  	onChange:ZaTabView.onFormFieldChanged
						};	
						
		var tempItems = xFormObject.items[2].items[0].items;	
		for (var i=0; i < tempItems.length; i++) {
			if (tempItems[i].ref == ZaDomain.A_description){
				tempItems.splice(i+1, 0, domainXformMaxAccountItem);					
				break;
			}
		}
	}
	ZaTabView.XFormModifiers["ZaDomainXFormView"].push(ZaDomainAdmin.domainXFormModifer);
}

if (ZaXDialog.XFormModifiers["ZaNewDomainXWizard"]) {
	ZaDomainAdmin.domainWizardModifer = function (xFormObject) {
		var domainWizardMaxAccountItem = {ref:ZaDomain.A_domainMaxAccounts, 
				type:_TEXTFIELD_, label:com_zimbra_yahoosmb.NAD_MaxAccounts,
				labelLocation:_LEFT_, width:50};
						
		var tempItems = xFormObject.items[3].items[0].items;	
		for (var i=0; i < tempItems.length; i++) {
			if (tempItems[i].ref == ZaDomain.A_description){
				tempItems.splice(i+1, 0, domainWizardMaxAccountItem);					
				break;
			}
		}
	}
	ZaXDialog.XFormModifiers["ZaNewDomainXWizard"].push(ZaDomainAdmin.domainWizardModifer);
}
if (AjxEnv.hasFirebug) console.log ("Loaded domainadmin.js");