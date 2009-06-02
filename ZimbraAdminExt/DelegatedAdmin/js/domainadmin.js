/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2009 Zimbra, Inc.
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

if(ZaAccount) {
	ZaAccount.A_zimbraDomainAdminMaxMailQuota = "zimbraDomainAdminMaxMailQuota" ;
	ZaAccount.A2_zimbraDomainAdminMailQuotaAllowed = "zimbraDomainAdminMailQuotaAllowed" ;
	var domainAdminMaxMailQuotaItem = {id:ZaAccount.A_zimbraDomainAdminMaxMailQuota, type:_COS_MAILQUOTA_, ref:"attrs."+ZaAccount.A_zimbraDomainAdminMaxMailQuota} ;
	var domainAdminMailQuotaAllowedItem = {id:ZaAccount.A2_zimbraDomainAdminMailQuotaAllowed, type:_COS_ENUM_, choices:ZaModel.BOOLEAN_CHOICES, ref:ZaAccount.A2_zimbraDomainAdminMailQuotaAllowed} ;
//	if (ZaSettings.isDomainAdmin) {
		domainAdminMaxMailQuotaItem.type = _COS_MAILQUOTA_ ;
		domainAdminMailQuotaAllowedItem.type = _ENUM_;
//	}
	ZaAccount.myXModel.items.push(domainAdminMaxMailQuotaItem);
	ZaAccount.myXModel.items.push(domainAdminMailQuotaAllowedItem);
}


if (ZaCos) {
	ZaCos.A_zimbraDomainAdminMaxMailQuota = "zimbraDomainAdminMaxMailQuota" ;
	ZaCos.A2_zimbraDomainAdminMailQuotaAllowed = "zimbraDomainAdminMailQuotaAllowed" ;
	ZaCos.myXModel.items.push({id:ZaCos.A2_zimbraDomainAdminMailQuotaAllowed, type:_ENUM_, choices:ZaModel.BOOLEAN_CHOICES, ref:ZaCos.A2_zimbraDomainAdminMailQuotaAllowed});
	ZaCos.myXModel.items.push({id:ZaCos.A_zimbraDomainAdminMaxMailQuota, type:_MAILQUOTA_, ref:"attrs."+ZaCos.A_zimbraDomainAdminMaxMailQuota});
}

if(ZaSettings) {

    ZaSettings.DOMAIN_ACCT_LIMIT_TAB = "domainAccountLimitsTab";
    ZaSettings.ALL_UI_COMPONENTS.push({ value: ZaSettings.DOMAIN_ACCT_LIMIT_TAB, label: com_zimbra_delegatedadmin.UI_Comp_domainAcctLimitsTab});

    ZaDomainAdmin.initSettings = function() {
	    if (AjxEnv.hasFirebug) console.log("domainadmin.js is modifying ZaSettings");
                               
        /*
	    if(domainAdmin) {                                        
	    	ZaUtil.HELP_URL = "help/delegated/";
			if(ZaHelpView)
				ZaHelpView.mainHelpPage = "da_about_zimbra_collaboration_suite.htm";

			if(ZaXDialog) {
				ZaXDialog.helpURL = location.pathname + "help/delegated/delegated_admin/da_about_zimbra_collaboration_suite.htm?locid=" + AjxEnv.DEFAULT_LOCALE;
			}
			if(ZaController) {
				ZaController.helpURL = location.pathname + "help/delegated/delegated_admin/da_about_zimbra_collaboration_suite.htm?locid=" + AjxEnv.DEFAULT_LOCALE;
			}
			if(ZaAccountListController) {
				ZaAccountListController.helpURL = location.pathname + "help/delegated/delegated_admin/da_domain_administrator_responsibilities.htm?locid=" + AjxEnv.DEFAULT_LOCALE;
			}
			if(ZaAccountViewController) {
				ZaAccountViewController.helpURL = location.pathname + "help/delegated/delegated_admin/da_editing_an_account.htm?locid=" + AjxEnv.DEFAULT_LOCALE;
			}
			if(ZaNewAccountXWizard) {
				ZaNewAccountXWizard.helpURL = location.pathname + "help/delegated/delegated_admin/da_creating_an_account.htm?locid=" + AjxEnv.DEFAULT_LOCALE;
			}
	    }*/
	}
	if(ZaSettings.initMethods)
		ZaSettings.initMethods.push(ZaDomainAdmin.initSettings);
}

if(ZaTabView.XFormModifiers["ZaAccountXFormView"]) {
	ZaDomainAdmin.AccountXFormModifier = function(xFormObject) {
		var accountDomainAdminMaxQuotaField =
			{ ref:ZaAccount.A_zimbraDomainAdminMaxMailQuota, type:_SUPER_TEXTFIELD_,
				type:_SUPER_TEXTFIELD_, resetToSuperLabel: ZaMsg.NAD_ResetToCOS,
				msgName:com_zimbra_delegatedadmin.NAD_DomainAdminMaxMailQuota,
				onChange:ZaTabView.onFormFieldChanged,
				txtBoxLabel:com_zimbra_delegatedadmin.NAD_DomainAdminMaxMailQuota,
				toolTipContent: com_zimbra_delegatedadmin.tt_DomainAdminMaxMailQuota,
                visibilityChecks:["instance.attrs[ZaAccount.A_zimbraIsDelegatedAdminAccount]==\'TRUE\' "],
                visibilityChangeEventSources: [ZaAccount.A_zimbraIsDelegatedAdminAccount]
                //				relevantBehavior: _HIDE_,
//				relevant: "instance.attrs[ZaAccount.A_zimbraIsDomainAdminAccount]==\'TRUE\' && (instance[ZaAccount.A2_zimbraDomainAdminMailQuotaAllowed]==\'TRUE\' || (!instance[ZaAccount.A2_zimbraDomainAdminMailQuotaAllowed] && instance.cos[ZaAccount.A2_zimbraDomainAdminMailQuotaAllowed]==\'TRUE\'))"
			};

        var tabs = xFormObject.items[2].items;

		//change General tab
		var tmpItems = tabs[0].items;
		var cnt = tmpItems.length;
		for(var i = 0; i < cnt; i ++) {
			if(tmpItems[i].id == "account_form_setup_group" && tmpItems[i].items) {
				var tmpGrouperItems = tmpItems[i].items;
				var cnt2 = tmpGrouperItems.length;
				for(var j=0;j<cnt2;j++) {
					if(tmpGrouperItems[j] && tmpGrouperItems[j].ref == ZaAccount.A2_adminRoles ) {
						//add domain admin quota field
					    xFormObject.items[2].items[0].items[i].items.splice(j+1,0, accountDomainAdminMaxQuotaField);
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
								tmpGrouperItems[k].visibilityChecks = ["instance.attrs[ZaAccount.A_zimbraIsDelegatedAdminAccount]==\'TRUE\' || instance.attrs[ZaAccount.A_zimbraIsAdminAccount]==\'TRUE\'"];
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

	ZaTabView.XFormModifiers["ZaAccountXFormView"].push(ZaDomainAdmin.AccountXFormModifier);
}


if(ZaXDialog.XFormModifiers["ZaNewAccountXWizard"]) {
	ZaDomainAdmin.NewAccountWizXFormModifier = function(xFormObject) {
		var accountDomainAdminMaxQuotaField =
				{ ref:ZaAccount.A_zimbraDomainAdminMaxMailQuota, type:_SUPERWIZ_TEXTFIELD_,
					msgName:com_zimbra_delegatedadmin.NAD_DomainAdminMaxMailQuota,
					label:null, txtBoxLabel: com_zimbra_delegatedadmin.NAD_DomainAdminMaxMailQuota,
					labelLocation:_LEFT_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS,
					toolTipContent: com_zimbra_delegatedadmin.tt_DomainAdminMaxMailQuota,
					visibilityChecks: []
				};

        var steps = xFormObject.items[3].items;
        var tmpItems = steps[0].items;
		var cnt = tmpItems.length;
		for(var i = 0; i < cnt; i ++) {
			if(tmpItems[i].id == "account_wiz_setup_group" && tmpItems[i].items) {
				var tmpGrouperItems = tmpItems[i].items;
				var cnt2 = tmpGrouperItems.length;
				for(var j=0;j<cnt2;j++) {
					if(tmpGrouperItems[j] && tmpGrouperItems[j].ref == ZaAccount.A2_adminRoles) {
						xFormObject.items[3].items[0].items[i].items.splice(j+1,0, accountDomainAdminMaxQuotaField);
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
								tmpGrouperItems[k].visibilityChecks = ["instance.attrs[ZaAccount.A_zimbraIsDelegatedAdminAccount]==\'TRUE\' || instance.attrs[ZaAccount.A_zimbraIsAdminAccount]==\'TRUE\'"];
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
			var networkHelpItems = [
				{type:_GROUP_,numCols:2, items: [
						{type:_OUTPUT_, value:AjxImg.getImageHtml("PDFDoc")},
						{type:_ANCHOR_, cssStyle:"font-size:12px;", showInNewWindow:true, labelLocation:_NONE_, label:com_zimbra_delegatedadmin.HELP_OTHER_GUIDES_ISYNC,
							href:(location.pathname + "help/admin/pdf/ZCS_Apple_iSync.pdf?locid=" + AjxEnv.DEFAULT_LOCALE)},
						{type:_OUTPUT_, value:AjxImg.getImageHtml("PDFDoc")},
						{type:_ANCHOR_, cssStyle:"font-size:12px;", showInNewWindow:true, labelLocation:_NONE_, label:com_zimbra_delegatedadmin.HELP_OTHER_GUIDES_OUTLOOK,
							href:(location.pathname + "help/admin/pdf/ZCS%20Connector%20for%20Outlook.pdf?locid=" + AjxEnv.DEFAULT_LOCALE)}

				 	]
				},
				{type:_CELL_SPACER_},
                {type:_SPACER_, colSpan:"*"},
				{type:_OUTPUT_, cssStyle:"font-size:12px;", label:null, value:ZaMsg.HELP_OTHER_GUIDES_CONNNECTOR_INFO,
				 	cssStyle:"padding-right:10px;padding-left:10px;"},
			    {type:_CELL_SPACER_},
                {type:_SEPARATOR_, colSpan:1, cssClass:"helpSeparator"}
			];
            var helpItems = xFormObject.items ;
			for (var i=0; i< helpItems.length; i++) {
                //insert teh networkHelpItems before the About button
                if (helpItems[i].id == "ZimbraHelpPageDownloadItems") {
					helpItems [i].items = helpItems[i].items.concat(networkHelpItems) ;
                    break ;
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
					{type:_OUTPUT_, cssClass:"ZaDownloadText", label: null, value:com_zimbra_delegatedadmin.CONNECTOR_MSI_DOWNLOAD_TEXT}
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
					{type:_OUTPUT_, cssClass:"ZaDownloadText", label: null, value:com_zimbra_delegatedadmin.CONNECTOR_DOWNLOAD_TEXT},

					//iSync
					{type:_GROUP_,numCols:2,
						items: [
							{type:_OUTPUT_, value:AjxImg.getImageHtml("MigrationWiz")},
							{type:_OUTPUT_, cssStyle:"font-size:12px;", labelLocation:_NONE_, label:null,
                                value:ZaMigrationWizView.getDownloadLink(ZaMsg.CONNECTOR_ISYNC_DOWNLOAD_LINK, ZaMsg.CONNECTOR_ISYNC_DOWNLOAD_LINK_MSG)
                            }
						]
					},
					{type:_OUTPUT_, cssClass:"ZaDownloadText", label: null, value:com_zimbra_delegatedadmin.CONNECTOR_ISYNC_DOWNLOAD_TEXT}
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
					msgName:com_zimbra_delegatedadmin.NAD_DomainAdminMaxMailQuota,label:com_zimbra_delegatedadmin.NAD_DomainAdminMaxMailQuota,
					labelLocation:_LEFT_,
					cssClass:"admin_xform_number_input",
//					relevant:"instance[ZaAccount.A2_zimbraDomainAdminMailQuotaAllowed]==\'TRUE\'",
					onChange:ZaTabView.onFormFieldChanged
				};

		
        var cosDomainAdminMailQuotaBx = {ref:ZaCos.A2_zimbraDomainAdminMailQuotaAllowed,type:_CHECKBOX_,
							msgName:com_zimbra_delegatedadmin.NAD_DomainAdminMailQuotaAllowed ,label:com_zimbra_delegatedadmin.NAD_DomainAdminMailQuotaAllowed ,
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


if (ZaDomain) {
	ZaDomain.A_domainMaxAccounts = "zimbraDomainMaxAccounts";
	ZaDomain.myXModel.items.push({id:ZaDomain.A_domainMaxAccounts, type:_INT_, ref:"attrs/" + ZaDomain.A_domainMaxAccounts,minInclusive:0});
}


if (ZaTabView.XFormModifiers["ZaDomainXFormView"]) {

    //---------------------------- Start Account Limits Edit Codes -------------------
    ZaAL = function() {
        ZaItem.call(this, "ZaAL");
    }

    ZaAL.prototype = new ZaItem;
    ZaAL.prototype.constructor = ZaAL;

    ZaAL.getXModel = function ()
    {
        var model = { items:
          [
            {id:"cos", type:_STRING_, ref:"cos"},
            {id:"limits", type:_INT_, ref:"limits",minInclusive:0}
          ]};
        return model ;
    }

    ZaAL.addAl = function (alInstance, domainInstance) {

        var newAl =  alInstance.cos + ":" + alInstance.limits ;

        if (!domainInstance.attrs[ZaDomain.A_zimbraDomainCOSMaxAccounts]) {
           domainInstance.attrs[ZaDomain.A_zimbraDomainCOSMaxAccounts] = [] ;
        }
        var done = false ;
        for (var i=0; i < domainInstance.attrs[ZaDomain.A_zimbraDomainCOSMaxAccounts].length; i ++) {
            if (alInstance.cos == domainInstance.attrs[ZaDomain.A_zimbraDomainCOSMaxAccounts][i].split(":")[0]) {
                domainInstance.attrs[ZaDomain.A_zimbraDomainCOSMaxAccounts][i] = newAl ;
                done = true ;
            }
        }

        if (!done)
            domainInstance.attrs[ZaDomain.A_zimbraDomainCOSMaxAccounts].push (newAl) ;
    }

    ZaEditAlXDialog = function(parent,  app, title) {
        if (arguments.length == 0) return;
        this._standardButtons = [ DwtDialog.CANCEL_BUTTON, DwtDialog.OK_BUTTON];
        ZaXDialog.call(this, parent, null, title,"350px", "100px");
        this._containedObject = {};
        this.cosChoices = new XFormChoices([], XFormChoices.OBJECT_LIST, "id", "name");
        this.initForm(ZaAL.getXModel(),this.getMyXForm());
    }

    ZaEditAlXDialog.prototype = new ZaXDialog;
    ZaEditAlXDialog.prototype.constructor = ZaEditAlXDialog;

    ZaEditAlXDialog.prototype.getMyXForm =
    function() {
        var xFormObject = {
            numCols:1,
            items:[
                 {type:_GROUP_,isTabGroup:true, items: [ //allows tab key iteration
                    { type: _SPACER_ },
                    //{ ref:"cos", type: _INPUT_, label: ZaMsg.Col_Cos + ": " },
                    {ref:"cos", type:_DYNSELECT_,label: ZaMsg.NAD_ClassOfService,
                            visibilityChecks:[],
                            enableDisableChecks:true,
                            //onChange: ZaEditAlXDialog.onCOSChanged,
                            emptyText:ZaMsg.enterSearchTerm,
                            dataFetcherMethod:ZaSearch.prototype.dynSelectSearchCoses,
                            choices:this.cosChoices,
                            dataFetcherClass:ZaSearch,
                            editable:true,
                            getDisplayValue:function(newValue) {
                                    // dereference through the choices array, if provided
                                    //newValue = this.getChoiceLabel(newValue);
                                    if(ZaItem.ID_PATTERN.test(newValue)) {
                                        var cos = ZaCos.getCosById(newValue, ZaApp.getInstance());
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
                    { ref: "limits", visibilityChecks:[], enableDisableChecks:true,
                        type:_INPUT_, label:ZaMsg.Col_Limit + ": " }
                  ]
                }
            ]
        };
        return xFormObject;
    }

    ZaDomainXFormView.isEditAlEnabled = function (attrName) {
        return (this.getInstance().al_selection_cache != null && this.getInstance().al_selection_cache.length==1
                && this.getInstance().attrs[attrName].length > 0);
    }

    ZaDomainXFormView.isDeleteAlEnabled = function (attrName) {
        return (this.getInstance().al_selection_cache != null && this.getInstance().al_selection_cache.length>0
                 && this.getInstance().attrs[attrName].length > 0);
//                  && this.instance.attrs[ZaDomain.A_zimbraDomainCOSMaxAccounts].length > 0);
    }

     ZaDomainXFormView.alSelectionListener = function (ev) {
        var instance = this.getInstance();

        var arr = this.widget.getSelection();
        if(arr && arr.length) {
            arr.sort();
            instance.al_selection_cache = arr;
        } else
            instance.al_selection_cache = null;

        this.getForm().refresh();
        if (ev.detail == DwtListView.ITEM_DBL_CLICKED) {
            ZaDomainXFormView.editAlButtonListener.call(this);
        }
     } ;

    ZaDomainXFormView.deleteAlButtonListener = function (attrName) {
           var instance = this.getInstance();
           if(instance.al_selection_cache != null) {
               var cnt = instance.al_selection_cache.length;
               if(cnt && instance.attrs[attrName]) {
                   for(var i=0;i<cnt;i++) {
                       var cnt2 = instance.attrs[attrName].length-1;
                       for(var k=cnt2;k>=0;k--) {
                           if(instance.attrs[attrName][k]==instance.al_selection_cache[i]) {
                               instance.attrs[attrName].splice(k,1);
                               break;
                           }
                       }
                   }

               }
           }
           this.getForm().parent.setDirty(true);
           this.getForm().refresh();
       }

       ZaDomainXFormView.editAlButtonListener =
       function () {
           var instance = this.getInstance();
           if(instance.al_selection_cache && instance.al_selection_cache[0]) {
               var formPage = this.getForm().parent;
               if(!formPage.addAlDlg) {
                   formPage.addAlDlg = new ZaEditAlXDialog(ZaApp.getInstance().getAppCtxt().getShell(), ZaApp.getInstance(), com_zimbra_delegatedadmin.Edit_Al_Title);
                   formPage.addAlDlg.registerCallback(DwtDialog.OK_BUTTON, ZaDomainXFormView.addAl, this.getForm(), null);
               }  else {
                   formPage.addAlDlg.setTitle (com_zimbra_delegatedadmin.Edit_Al_Title) ;
               }
               var obj = {} ;
               obj.cos = instance.al_selection_cache[0].split(":")[0] ;
               obj.limits = instance.al_selection_cache[0].split(":")[1] ;

               formPage.addAlDlg.setObject(obj);
               formPage.addAlDlg.popup();
           }
       }


       ZaDomainXFormView.addAlButtonListener =
       function () {
           var instance = this.getInstance();
           var formPage = this.getForm().parent;

           if(!formPage.addAlDlg) {
               formPage.addAlDlg = new ZaEditAlXDialog(ZaApp.getInstance().getAppCtxt().getShell(), ZaApp.getInstance(), com_zimbra_delegatedadmin.Add_Al_Title);
               formPage.addAlDlg.registerCallback(DwtDialog.OK_BUTTON, ZaDomainXFormView.addAl, this.getForm(), null);
           }  else {
               formPage.addAlDlg.setTitle (com_zimbra_delegatedadmin.Add_Al_Title) ;
           }

           var obj = {
               cos: "",
               limits: ""
           }

           formPage.addAlDlg.setObject(obj);
           formPage.addAlDlg.popup();
       }

       ZaDomainXFormView.addAl  = function () {
           if(this.parent.addAlDlg ) {
               var app = ZaApp.getInstance() ;
               var obj = this.parent.addAlDlg._localXForm.getInstance ();

               if ((obj.cos == null) ||
                   ((ZaCos.getCosById(obj.cos, app) == null) && (ZaCos.getCosByName(obj.cos, app) == null))) {
                   app.getCurrentController().popupErrorDialog(
                           AjxMessageFormat.format(ZaMsg.ERROR_INVALID_VALUE_FOR, [ZaMsg.NAD_COS])) ;
                   return ;
               }

               if (parseInt(obj.limits) !=  obj.limits) {
                   app.getCurrentController().popupErrorDialog(
                           AjxMessageFormat.format(ZaMsg.ERROR_INVALID_VALUE_FOR, [ZaMsg.Col_Limit])) ;
                   return ;
               }

               var instance = this.getInstance();
               ZaAL.addAl (obj, instance) ;

               if (this.parent.addAlDlg)
                   this.parent.addAlDlg.popdown();

               this.parent.setDirty(true);
               this.refresh();
           }
       }

    ZaDomainXFormView.isAccountLimitWarningEnabled = function () {
            var domainMax = this.getInstance().attrs[ZaDomain.A_domainMaxAccounts] ;
            var cosMax = ZaDomain.getTotalLimitsPerAccountTypes (this.getInstance().attrs[ZaDomain.A_zimbraDomainCOSMaxAccounts]) ;
            if (domainMax && cosMax && (domainMax < cosMax))  {
                //customize the message doesn't work!
                //var warning = this.getItemsById ("_domain_cos_account_limits_alert_") [0] ;
                //warning.getWidget().setContent(AjxMessageFormat.format(com_zimbra_delegatedadmin.WARNING_TOTAL_LIMIT_OUT_OF_SYNC, [domainMax, cosMax]));
                return true ;
            }
            return false ;
        }


    //Edit Feature Account Limits
    ZaFeatureAL = function() {
        ZaItem.call(this, "ZaFeatureAL");
    }

    ZaFeatureAL.prototype = new ZaItem;
    ZaFeatureAL.prototype.constructor = ZaFeatureAL;

    ZaFeatureAL.getXModel = function ()
    {
        var model = { items:
          [
            {id:"feature", type:_STRING_, ref:"feature",
                    choices:ZaCos.MAJOR_FEATURES_CHOICES},
            {id:"limits", type:_INT_, ref:"limits" ,minInclusive:0}
          ]};
        return model ;
    }

    ZaFeatureAL.addAl = function (alInstance, domainInstance) {

        var newAl =  alInstance.feature + ":" + alInstance.limits ;

        if (!domainInstance.attrs[ZaDomain.A_zimbraDomainFeatureMaxAccounts]) {
           domainInstance.attrs[ZaDomain.A_zimbraDomainFeatureMaxAccounts] = [] ;
        }
        var done = false ;
        for (var i=0; i < domainInstance.attrs[ZaDomain.A_zimbraDomainFeatureMaxAccounts].length; i ++) {
            if (alInstance.feature == domainInstance.attrs[ZaDomain.A_zimbraDomainFeatureMaxAccounts][i].split(":")[0]) {
                domainInstance.attrs[ZaDomain.A_zimbraDomainFeatureMaxAccounts][i] = newAl ;
                done = true ;
            }
        }

        if (!done)
            domainInstance.attrs[ZaDomain.A_zimbraDomainFeatureMaxAccounts].push (newAl) ;
    }


    ZaEditFeatureAlXDialog = function(parent,  app, title) {
        if (arguments.length == 0) return;
        this._standardButtons = [ DwtDialog.CANCEL_BUTTON, DwtDialog.OK_BUTTON];
        ZaXDialog.call(this, parent, null, title,"300px", "100px");
        this._containedObject = {};
        this.initForm(ZaFeatureAL.getXModel(),this.getMyXForm());
    }

    ZaEditFeatureAlXDialog.prototype = new ZaXDialog;
    ZaEditFeatureAlXDialog.prototype.constructor = ZaEditFeatureAlXDialog;

    ZaEditFeatureAlXDialog.prototype.getMyXForm =
    function() {
        var xFormObject = {
            numCols:1,
            items:[
                 {type:_GROUP_,isTabGroup:true, items: [ //allows tab key iteration
                    { type: _SPACER_ },
                    { ref:"feature", type:_OSELECT1_,label: ZaMsg.NAD_Feature, width: 200 },
                    { ref: "limits", type:_INPUT_, label:ZaMsg.Col_Limit + ": ", width: 200 }
                  ]
                }
            ]
        };
        return xFormObject;
    }

    ZaDomainXFormView.alFeatureSelectionListener = function (ev) {
            var instance = this.getInstance();

            var arr = this.widget.getSelection();
            if(arr && arr.length) {
                arr.sort();
                instance.feature_al_selection_cache = arr;
            } else
                instance.feature_al_selection_cache = null;

            this.getForm().refresh();
            if (ev.detail == DwtListView.ITEM_DBL_CLICKED) {
                ZaDomainXFormView.editFeatureAlButtonListener.call(this);
            }
         } ;


    ZaDomainXFormView.isEditFeatureAlEnabled = function (attrName) {
        return (this.instance.feature_al_selection_cache != null && this.instance.feature_al_selection_cache.length==1
                && this.instance.attrs[attrName].length > 0);
    }

    ZaDomainXFormView.isDeleteFeatureAlEnabled = function (attrName) {
        return (this.instance.feature_al_selection_cache != null && this.instance.feature_al_selection_cache.length>0
                 && this.instance.attrs[attrName].length > 0);
//                  && this.instance.attrs[ZaDomain.A_zimbraDomainCOSMaxAccounts].length > 0);
    }

    ZaDomainXFormView.editFeatureAlButtonListener =
       function () {
           var instance = this.getInstance();
           if(instance.feature_al_selection_cache && instance.feature_al_selection_cache[0]) {
               var formPage = this.getForm().parent;
               if(!formPage.addFeatureAlDlg) {
                   formPage.addFeatureAlDlg = new ZaEditFeatureAlXDialog(ZaApp.getInstance().getAppCtxt().getShell(),
                           ZaApp.getInstance(), com_zimbra_delegatedadmin.Edit_FeatureAl_Title);
                   formPage.addFeatureAlDlg.registerCallback(DwtDialog.OK_BUTTON, ZaDomainXFormView.addFeatureAl,
                           this.getForm(), null);
               }  else {
                   formPage.addFeatureAlDlg.setTitle (com_zimbra_delegatedadmin.Edit_FeatureAl_Title) ;
               }
               var obj = {} ;
               obj.feature = instance.feature_al_selection_cache[0].split(":")[0] ;
               obj.limits = instance.feature_al_selection_cache[0].split(":")[1] ;

               formPage.addFeatureAlDlg.setObject(obj);
               formPage.addFeatureAlDlg.popup();
           }
       }

    ZaDomainXFormView.deleteFeatureAlButtonListener = function (attrName) {
               var instance = this.getInstance();
               if(instance.feature_al_selection_cache != null) {
                   var cnt = instance.feature_al_selection_cache.length;
                   if(cnt && instance.attrs[attrName]) {
                       for(var i=0;i<cnt;i++) {
                           var cnt2 = instance.attrs[attrName].length-1;
                           for(var k=cnt2;k>=0;k--) {
                               if(instance.attrs[attrName][k]==instance.feature_al_selection_cache[i]) {
                                   instance.attrs[attrName].splice(k,1);
                                   break;
                               }
                           }
                       }

                   }
               }
               this.getForm().parent.setDirty(true);
               this.getForm().refresh();
           }


     ZaDomainXFormView.addFeatureAlButtonListener =
       function () {
           var instance = this.getInstance();
           var formPage = this.getForm().parent;

           if(!formPage.addFeatureAlDlg) {
               formPage.addFeatureAlDlg = new ZaEditFeatureAlXDialog(ZaApp.getInstance().getAppCtxt().getShell(),
                       ZaApp.getInstance(), com_zimbra_delegatedadmin.Add_FeatureAl_Title);
               formPage.addFeatureAlDlg.registerCallback(DwtDialog.OK_BUTTON, ZaDomainXFormView.addFeatureAl,
                       this.getForm(), null);
           }  else {
               formPage.addFeatureAlDlg.setTitle (com_zimbra_delegatedadmin.Add_FeatureAl_Title) ;
           }

           var obj = {
               feature: "",
               limits: ""
           }

           formPage.addFeatureAlDlg.setObject(obj);
           formPage.addFeatureAlDlg.popup();
       }

       ZaDomainXFormView.addFeatureAl  = function () {
           if(this.parent.addFeatureAlDlg ) {
               var app = ZaApp.getInstance() ;
               var obj = this.parent.addFeatureAlDlg._localXForm.getInstance ();

               if (obj.feature == null || obj.feature.length <= 0 ) {
                    app.getCurrentController().popupErrorDialog(
                            AjxMessageFormat.format(ZaMsg.ERROR_INVALID_VALUE_FOR, [ZaMsg.TABT_Feature])) ;
                    return ;
                }

                if (parseInt(obj.limits) !=  obj.limits) {
                    app.getCurrentController().popupErrorDialog(
                            AjxMessageFormat.format(ZaMsg.ERROR_INVALID_VALUE_FOR, [ZaMsg.Col_Limit])) ;
                    return ;
                }
               var instance = this.getInstance();
               ZaFeatureAL.addAl (obj, instance) ;

               if (this.parent.addFeatureAlDlg)
                   this.parent.addFeatureAlDlg.popdown();

               this.parent.setDirty(true);
               this.refresh();
           }
       }

    //skin logo properties
    ZaDomainXFormView.LogoAppBannerPreviewId = Dwt.getNextId() ;

    ZaDomainXFormView.onAppLogoURLChange = function (value, event, form) {
        ZaTabView.onFormFieldChanged.call (this, value, event, form) ;

        var appBannerPreviewItem = form.getItemsById (ZaDomainXFormView.LogoAppBannerPreviewId) [0];
        appBannerPreviewItem.updateElement(ZaDomainXFormView.getAppLogoPreview.call(this)) ;
    }

    ZaDomainXFormView.getAppLogoPreview = function () {
        var width =  120 ;
        var height = 35 ;
        var form = this.getForm ();
        var instance = form.getInstance () ;
        var src = instance.attrs [ZaDomain.A_zimbraSkinLogoAppBanner] ;

        if (src == null && instance.cos != null)
             var src = instance.cos.attrs [ZaDomain.A_zimbraSkinLogoAppBanner];
        /*
        var out = AjxBuffer.concat ("<img width=", width, " height=", height,
                    " alt='", com_zimbra_delegatedadmin.AppBannerAlt , "' src=\"", src, "\"",
                ">") */
        var out = AjxBuffer.concat ("<div style='border: inset 1px; height:", height, "; width:", width, ";",
                "background-image:url(",   src ,")",
                "'>", "</div>");
        return out ;
    }

    ZaDomainXFormView.LogoLoginBannerPreviewId = Dwt.getNextId() ;

    ZaDomainXFormView.onLoginLogoURLChange = function (value, event, form) {
        ZaTabView.onFormFieldChanged.call (this, value, event, form) ;

        var loginBannerPreviewItem = form.getItemsById (ZaDomainXFormView.LogoLoginBannerPreviewId) [0];
        loginBannerPreviewItem.updateElement(ZaDomainXFormView.getLoginLogoPreview.call(this)) ;
    }

    ZaDomainXFormView.getLoginLogoPreview = function () {
        var width =  450 ;
        var height = 100 ;
        var form = this.getForm ();
        var instance = form.getInstance () ;
        var src = instance.attrs [ZaDomain.A_zimbraSkinLogoLoginBanner]  ;
        if (src == null && instance.cos != null)
                 var src =  instance.cos.attrs [ZaDomain.A_zimbraSkinLogoLoginBanner];
        /*
        var out = AjxBuffer.concat ("<img width=", width, " height=", height,
                    " alt='", com_zimbra_delegatedadmin.LoginBannerAlt , "' src=\"", src, "\"",
                ">")
         */
        //use the same html element and style as real element
        var out = AjxBuffer.concat ("<div style='border: inset 1px;height:", height, "; width:", width, ";",
                "background-image:url(",   src ,")",
                "'>", "</div>");

        return out ;
    }


    //---------------------------- End Account Limits Edit Codes -------------------

    ZaDomainAdmin.domainXFormModifer = function (xFormObject) {
		var enableMXCheckCheckbox = { ref: ZaDomain.A_zimbraAdminConsoleDNSCheckEnabled, type: _SUPER_CHECKBOX_,
							  	  resetToSuperLabel:ZaMsg.NAD_ResetToGlobal,
						      	  checkBoxLabel:com_zimbra_delegatedadmin.ENABLE_DOM_CONSOLE_DNS_CHECK,
							  	  trueValue: "TRUE", falseValue: "FALSE",
								  onChange:ZaDomainXFormView.onFormFieldChanged};

		var tempItems = xFormObject.items[2].items[0].items;
		var simpleCount=0;
		for (var i=0; i < tempItems.length; i++) {
			if(tempItems[i].id=="dns_check_group") {
				if(!ZaSettings.DOMAINS_ARE_READONLY)
					tempItems[i].items.push(enableMXCheckCheckbox);

				simpleCount++;
			}
			if(simpleCount==1)
				break;
		}

        //add account limits to a new Tab
        var tabBar, switchGroup ;
        for (var i=0; i < xFormObject.items.length; i ++) {
            if (xFormObject.items[i].type == _TAB_BAR_) {
                tabBar = xFormObject.items[i] ;
            }

            if (xFormObject.items[i].type == _SWITCH_) {
                switchGroup = xFormObject.items[i]
            }
        }

        var domainXformMaxAccountItem , accountLimitsItems, domainFeatureMaxAccountItem;
        var acctLimitsHeaderList = new Array();
        var featureMaxHeaderList = [] ;
        var acct_type_col_display = ZaMsg.Col_Cos ;

        if (ZaSettings.isDomainAdmin) {
                acct_type_col_display = com_zimbra_delegatedadmin.Col_account_type ;
        }
        acctLimitsHeaderList[0] = new ZaListHeaderItem("cos", acct_type_col_display, null, 200, null, null, true, true);
        acctLimitsHeaderList[1] = new ZaListHeaderItem("limits", ZaMsg.Col_Limit, null, null, null, null, true, true);

        featureMaxHeaderList[0] = new ZaListHeaderItem("feature",  ZaMsg.TABT_Features , null, 200, null, null, true, true);
        featureMaxHeaderList[1] = new ZaListHeaderItem("limits", ZaMsg.Col_Limit, null, null, null, null, true, true);



        if (ZaSettings.isDomainAdmin) {  //the items are not modifiable
           domainXformMaxAccountItem =
                        {ref:ZaDomain.A_domainMaxAccounts, type:_OUTPUT_,
                            label:com_zimbra_delegatedadmin.NAD_DomainMaxAccounts, width:50,
                            getDisplayValue: function (v) {
                                if (!v) v = ZaMsg.Unlimited ;
                                return v ;
                            }
                        } ;
            //add the ZimbraDomainCosMaxAccounts information
            accountLimitsItems = { type:_GROUP_, colSpan: "*", colSizes: ["300px", "*"], numCols: 2,
                    visibilityChecks:[ "((instance.attrs[ZaDomain.A_zimbraDomainCOSMaxAccounts] != null) && "
                                + " (instance.attrs[ZaDomain.A_zimbraDomainCOSMaxAccounts].length > 0)) " ],
                    items: [
                      {type:_OUTPUT_, value: com_zimbra_delegatedadmin.accountLimitsByAccountType, align: _RIGHT_ },
                      {ref:ZaDomain.A_zimbraDomainCOSMaxAccounts, type:_DWT_LIST_, height:"200", width:"300px",
                            forceUpdate: true, cssClass: "DLSource",
                            widgetClass: ZaDomainCOSMaxAccountsListView,
                            headerList:acctLimitsHeaderList, hideHeader: false
                        }
                    ]
                };

            //feature account limits
            //add the ZimbraDomainFeatureMaxAccounts information
            domainFeatureMaxAccountItem = { type:_GROUP_, colSpan: "*", colSizes: ["300px", "*"], numCols: 2,
                    visibilityChecks: ["((instance.attrs[ZaDomain.A_zimbraDomainFeatureMaxAccounts] != null) && "
                                + " (instance.attrs[ZaDomain.A_zimbraDomainFeatureMaxAccounts].length > 0)) "] ,
                    items: [
                      {type:_OUTPUT_, value: com_zimbra_delegatedadmin.accountLimitsByFeature, align: _RIGHT_ },
                      {ref:ZaDomain.A_zimbraDomainFeatureMaxAccounts, type:_DWT_LIST_, height:"200", width:"300px",
                            forceUpdate: true, cssClass: "DLSource",
                            widgetClass: ZaDomainFeatureMaxAccountsListView,
                            headerList:featureMaxHeaderList, hideHeader: false
                        }
                    ]
                };

        } else {
            domainXformMaxAccountItem =
                        {ref:ZaDomain.A_domainMaxAccounts, type:_INPUT_,
                            label:com_zimbra_delegatedadmin.NAD_DomainMaxAccounts, width:50,
                            onChange:ZaDomainXFormView.onFormFieldChanged
                        } ;


           var warningItem = {type: _DWT_ALERT_, id: "_domain_cos_account_limits_alert_" ,
                    visibilityChecks:[ZaDomainXFormView.isAccountLimitWarningEnabled],

                    containerCssStyle: "width:400px;",
                    style: DwtAlert.WARNING, iconVisible: true,
                    content: com_zimbra_delegatedadmin.WARNING_TOTAL_LIMIT_OUT_OF_SYNC
           }

            accountLimitsItems = { type:_GROUP_, colSpan: "*", colSizes: ["300px", "*"], numCols: 2,
                    items: [
                      {type:_OUTPUT_, value: com_zimbra_delegatedadmin.accountLimitsByCos, align: _RIGHT_ },
                      {ref:ZaDomain.A_zimbraDomainCOSMaxAccounts, type:_DWT_LIST_, height:"200", width:"300px",
                            forceUpdate: true, preserveSelection:false, multiselect:true,cssClass: "DLSource",
                            widgetClass: ZaDomainCOSMaxAccountsListView,
                            onSelection:ZaDomainXFormView.alSelectionListener,
                            headerList:acctLimitsHeaderList, hideHeader: false
                        } ,
                       {type:_CELLSPACER_},
                       {type:_GROUP_, numCols:5, width:"300px", colSizes:["80px","auto","80px","auto","80px"],
                            cssStyle:"margin-bottom:10px;padding-bottom:0px;margin-top:10px;pxmargin-left:10px;margin-right:10px;",
                            items: [
                                {type:_DWT_BUTTON_, label:ZaMsg.TBB_Delete,width:"100px",
                                    onActivate:"ZaDomainXFormView.deleteAlButtonListener.call(this, ZaDomain.A_zimbraDomainCOSMaxAccounts);",
                                    enableDisableChecks:[[ZaDomainXFormView.isDeleteAlEnabled, ZaDomain.A_zimbraDomainCOSMaxAccounts]]

                                },
                                {type:_CELLSPACER_},
                                {type:_DWT_BUTTON_, label:ZaMsg.TBB_Edit,width:"100px",
                                    onActivate:"ZaDomainXFormView.editAlButtonListener.call(this);",
                                    enableDisableChecks:[[ZaDomainXFormView.isEditAlEnabled, ZaDomain.A_zimbraDomainCOSMaxAccounts]]
                                },
                                {type:_CELLSPACER_},
                                {type:_DWT_BUTTON_, label:ZaMsg.NAD_Add,width:"100px",
                                    onActivate:"ZaDomainXFormView.addAlButtonListener.call(this);"
                                }
                            ]
                        }
                    ]
                } ;

            //add the ZimbraDomainFeatureMaxAccounts information
            domainFeatureMaxAccountItem = {
                    type:_GROUP_, colSpan: "*", colSizes: ["300px", "*"], numCols: 2,
                    items: [
                       {type:_CELLSPACER_},
                      {type: _DWT_ALERT_, containerCssStyle: "width:300px;",
                            style: DwtAlert.WARNING, iconVisible: true,
                            content: com_zimbra_delegatedadmin.WARNING_FEATURE_MAX_ACCOUNTS
                     },
                      {type:_OUTPUT_, value: com_zimbra_delegatedadmin.accountLimitsByFeature, align: _RIGHT_ },
                      {ref:ZaDomain.A_zimbraDomainFeatureMaxAccounts, type:_DWT_LIST_, height:"200", width:"300px",
                            forceUpdate: true, preserveSelection:false, multiselect:true,cssClass: "DLSource",
                            widgetClass: ZaDomainFeatureMaxAccountsListView,
                            onSelection: ZaDomainXFormView.alFeatureSelectionListener,
                            headerList:featureMaxHeaderList, hideHeader: false
                        } ,
                       {type:_CELLSPACER_},
                       {type:_GROUP_, numCols:5, width:"300px", colSizes:["80px","auto","80px","auto","80px"],
                            cssStyle:"margin-bottom:10px;padding-bottom:0px;margin-top:10px;pxmargin-left:10px;margin-right:10px;",
                            items: [
                                {type:_DWT_BUTTON_, label:ZaMsg.TBB_Delete,width:"100px",
                                    onActivate:"ZaDomainXFormView.deleteFeatureAlButtonListener.call(this, ZaDomain.A_zimbraDomainFeatureMaxAccounts);",
                                    enableDisableChecks:[[ZaDomainXFormView.isDeleteFeatureAlEnabled, ZaDomain.A_zimbraDomainFeatureMaxAccounts]]
                                },
                                {type:_CELLSPACER_},
                                {type:_DWT_BUTTON_, label:ZaMsg.TBB_Edit,width:"100px",
                                    onActivate:"ZaDomainXFormView.editFeatureAlButtonListener.call(this);",
                                    enableDisableChecks: [[ZaDomainXFormView.isEditFeatureAlEnabled, ZaDomain.A_zimbraDomainFeatureMaxAccounts]]
                                },
                                {type:_CELLSPACER_},
                                {type:_DWT_BUTTON_, label:ZaMsg.NAD_Add,width:"100px",
                                    onActivate:"ZaDomainXFormView.addFeatureAlButtonListener.call(this);"
                                }
                            ]
                        }
                    ]
                } ;

        }

        if (tabBar && switchGroup) {

            if(ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.DOMAIN_ACCT_LIMIT_TAB] || ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.CARTE_BLANCHE_UI]) {
                var tabIx = ++this.TAB_INDEX;
                tabBar.choices.push({value:tabIx, label: com_zimbra_delegatedadmin.TT_account_limits}) ;

                var caseItem =
                    {type:_ZATABCASE_, id:"account_form_account_limits_tab", numCols:2, colSizes:["300px","*"],
    //                    relevant:("instance[ZaModel.currentTab] == " + tabIx),
                        caseKey: tabIx,
                        items:[
                           {type:_SPACER_, height: "10px" },
                           warningItem,
                           domainXformMaxAccountItem,
                           {type:_SPACER_, height: "10px" },
                           accountLimitsItems
                            /** Disable the feature max accounts see bug: 32921
                            ,
                           {type:_SPACER_, height: "10px" },
                           domainFeatureMaxAccountItem
                               **/
                        ]
                    }
                switchGroup.items.push(caseItem);
            }
            
            //add the Skin Logo properties
            var skinTab = null;
            for (var i=0; i < switchGroup.items.length; i ++) {
                if (switchGroup.items[i].id == "domain_form_skin_tab") {
                    skinTab =  switchGroup.items[i] ;
                    break ;
                }
            }

            if (skinTab) {
                var domainSkinLogoItem = {
                    type:_ZA_TOP_GROUPER_,  label:com_zimbra_delegatedadmin.NAD_Skin_Logo_Settings,
                    colSizes:["275px","450px"],
                    //colSizes:["175px","*"],
                        items: [
                           {ref:ZaDomain.A_zimbraSkinLogoURL,
                                type:ZaSettings.isDomainAdmin ? _TEXTFIELD_ : _SUPER_TEXTFIELD_,  textFieldWidth: "200px",
                                label:com_zimbra_delegatedadmin.NAD_zimbraSkinLogoURL,
                                labelLocation:_LEFT_, resetToSuperLabel:ZaMsg.NAD_ResetToGlobal,
                                onChange:ZaTabView.onFormFieldChanged
                            },
                            {ref:ZaDomain.A_zimbraSkinLogoAppBanner,
                                type: ZaSettings.isDomainAdmin ? _TEXTFIELD_ : _SUPER_TEXTFIELD_,  textFieldWidth: "200px",
                                label:com_zimbra_delegatedadmin.NAD_zimbraSkinLogoAppBanner,
                                labelLocation:_LEFT_, resetToSuperLabel:ZaMsg.NAD_ResetToGlobal,
                                onChange: ZaDomainXFormView.onAppLogoURLChange
    //                            onChange:ZaTabView.onFormFieldChanged
                            },
                            { type:_SPACER_, height: 5 },
                            {type:_OUTPUT_, id:ZaDomainXFormView.LogoAppBannerPreviewId,
                                label:com_zimbra_delegatedadmin.NAD_zimbraLogoAppBannerPreview,
                                getDisplayValue: ZaDomainXFormView.getAppLogoPreview,
                                labelLocation:_LEFT_
                            },
                            { type:_SPACER_, height: 5 },
                            {ref:ZaDomain.A_zimbraSkinLogoLoginBanner,
                                type: ZaSettings.isDomainAdmin ? _TEXTFIELD_ : _SUPER_TEXTFIELD_,  textFieldWidth: "200px",
                                label:com_zimbra_delegatedadmin.NAD_zimbraSkinLogoLoginBanner,
                                labelLocation:_LEFT_, resetToSuperLabel:ZaMsg.NAD_ResetToGlobal,
                                onChange: ZaDomainXFormView.onLoginLogoURLChange
                            },
                            { type:_SPACER_, height: 5 },
                            {type:_OUTPUT_, id:ZaDomainXFormView.LogoLoginBannerPreviewId,
                                label:com_zimbra_delegatedadmin.NAD_zimbraLogoLoginBannerPreview,
                                getDisplayValue: ZaDomainXFormView.getLoginLogoPreview,
                                labelLocation:_LEFT_
                            } ,
                            {type:_GROUP_,  colSpan: 2, cssStyle: "margin-top: 10px; margin-left: 200px", items: [
                                    {type: _DWT_BUTTON_,  label: com_zimbra_delegatedadmin.bt_ResetAllSkinLogo,
                                        onActivate: ZaDomainXFormView.resetAllLogoThemes }
                               ]
                            }
                        ]
                    } ;
                skinTab.items.push(domainSkinLogoItem)  ;
            }
        }
    }
	ZaTabView.XFormModifiers["ZaDomainXFormView"].push(ZaDomainAdmin.domainXFormModifer);

    ZaDomainXFormView.resetAllLogoThemes = function () {
        var form = this.getForm() ;
    //    var instance = form.getInstance () ;
        this.setInstanceValue ("", ZaDomain.A_zimbraSkinLogoURL) ;
        this.setInstanceValue ("", ZaDomain.A_zimbraSkinLogoAppBanner) ;
        this.setInstanceValue ("", ZaDomain.A_zimbraSkinLogoLoginBanner) ;

        var appBannerPreviewItem = form.getItemsById (ZaDomainXFormView.LogoAppBannerPreviewId) [0];
        appBannerPreviewItem.updateElement(ZaDomainXFormView.getAppLogoPreview.call(this)) ;
        var loginBannerPreviewItem = form.getItemsById (ZaDomainXFormView.LogoLoginBannerPreviewId) [0];
        loginBannerPreviewItem.updateElement(ZaDomainXFormView.getLoginLogoPreview.call(this)) ;

        form.parent.setDirty(true);
        form.refresh () ;
    }

}

if (ZaXDialog.XFormModifiers["ZaNewDomainXWizard"]) {
	ZaDomainAdmin.domainWizardModifer = function (xFormObject) {
		var domainWizardMaxAccountItem = {ref:ZaDomain.A_domainMaxAccounts,
				type:_TEXTFIELD_, label:com_zimbra_delegatedadmin.NAD_DomainMaxAccounts,
				labelLocation:_LEFT_, width:50};

		var enableMXCheckCheckbox = { ref: ZaDomain.A_zimbraAdminConsoleDNSCheckEnabled, type: _SUPER_WIZ_CHECKBOX_,
				resetToSuperLabel:ZaMsg.NAD_ResetToGlobal,
				checkBoxLabel:com_zimbra_delegatedadmin.ENABLE_DOM_CONSOLE_DNS_CHECK,
				trueValue: "TRUE", falseValue: "FALSE"};

		var tempItems = xFormObject.items[3].items[0].items;

		var simpleCount=0;

		if(ZaSettings.DOMAINS_ARE_READONLY)
			simpleCount++;

		for (var i=0; i < tempItems.length; i++) {
			if (tempItems[i].ref == ZaDomain.A_description){
				tempItems.splice(i+1, 0, domainWizardMaxAccountItem);
				simpleCount++;
			} else if(tempItems[i].id=="dns_check_group") {
				if(!ZaSettings.DOMAINS_ARE_READONLY)
					tempItems[i].items.push(enableMXCheckCheckbox);

				simpleCount++;
			}
			if(simpleCount==2)
				break;
		}
		/*for (var i=0; i < tempItems.length; i++) {
			if (tempItems[i].ref == ZaDomain.A_description){
				tempItems.splice(i+1, 0, domainWizardMaxAccountItem);
				if(!ZaSettings.DOMAINS_ARE_READONLY)
					tempItems.splice(i+1, 0, enableMXCheckCheckbox);

				break;
			}
		}*/
	}
	ZaXDialog.XFormModifiers["ZaNewDomainXWizard"].push(ZaDomainAdmin.domainWizardModifer);
}

if (GlobalConfigXFormView)  {
    GlobalConfigXFormView.LogoAppBannerPreviewId = Dwt.getNextId() ;

    GlobalConfigXFormView.onAppLogoURLChange = function (value, event, form) {
        ZaTabView.onFormFieldChanged.call (this, value, event, form) ;

        var appBannerPreviewItem = form.getItemsById (GlobalConfigXFormView.LogoAppBannerPreviewId) [0];
        appBannerPreviewItem.updateElement(ZaDomainXFormView.getAppLogoPreview.call(this)) ;
    }

    GlobalConfigXFormView.LogoLoginBannerPreviewId = Dwt.getNextId() ;

    GlobalConfigXFormView.onLoginLogoURLChange = function (value, event, form) {
        ZaTabView.onFormFieldChanged.call (this, value, event, form) ;

        var loginBannerPreviewItem = form.getItemsById (GlobalConfigXFormView.LogoLoginBannerPreviewId) [0];
        loginBannerPreviewItem.updateElement(ZaDomainXFormView.getLoginLogoPreview.call(this)) ;
    }
}


ZaDomainAdmin.GlobalConfigXFormModifier = function (xFormObject) {
    var enableMXCheckCheckbox = { ref: ZaGlobalConfig.A_zimbraAdminConsoleDNSCheckEnabled, type: _CHECKBOX_,
							  	  label: com_zimbra_delegatedadmin.ENABLE_GLOB_CONSOLE_DNS_CHECK,
							  	  trueValue: "TRUE", falseValue: "FALSE",
								  onChange:ZaTabView.onFormFieldChanged
							  	};

    var tabCases = xFormObject.items[2].items;
	var mtaCase = null;
    var skinTab = null ;
    var cnt = tabCases.length;
	for(var i=0; i<cnt;i++ ) {
		if(tabCases[i].id == "global_mta_tab") {
			mtaCase = tabCases[i];
		}else if(tabCases[i].id == "global_skin_tab") {
			skinTab = tabCases[i];
		}
    }

    if(mtaCase) {
		var tmpItems = mtaCase.items;
		var cnt2 = tmpItems.length;
		for(var i = 0; i < cnt2; i ++) {
			if(tmpItems[i].id == "mta_network_group" && tmpItems[i].items) {
				tmpItems[i].items.push(enableMXCheckCheckbox);
				break;
			}
		}
	}

    //add skin logo item
    if (skinTab) {
       var globalSkinLogoItems = {
           type:_ZA_TOP_GROUPER_,  label:com_zimbra_delegatedadmin.NAD_Skin_Logo_Settings,//colSizes:["175px","*"],
                items: [
                    {ref:ZaGlobalConfig.A_zimbraSkinLogoURL,
                        type:_TEXTFIELD_,
                        label:com_zimbra_delegatedadmin.NAD_zimbraSkinLogoURL,
                        labelLocation:_LEFT_, width: 200,
                        onChange:ZaTabView.onFormFieldChanged
                    } ,
                    {ref:ZaGlobalConfig.A_zimbraSkinLogoAppBanner,
                        type:_TEXTFIELD_,
                        label:com_zimbra_delegatedadmin.NAD_zimbraSkinLogoAppBanner,
                        labelLocation:_LEFT_, width: 200,
                         onChange: GlobalConfigXFormView.onAppLogoURLChange
                    },
                    { type:_SPACER_, height: 5 },
                    {type:_OUTPUT_, id:GlobalConfigXFormView.LogoAppBannerPreviewId,
                        label:com_zimbra_delegatedadmin.NAD_zimbraLogoAppBannerPreview,
                        getDisplayValue: ZaDomainXFormView.getAppLogoPreview,
                        labelLocation:_LEFT_
                    },
                    { type:_SPACER_, height: 5 },
                    {ref:ZaGlobalConfig.A_zimbraSkinLogoLoginBanner,
                        type:_TEXTFIELD_,
                        label:com_zimbra_delegatedadmin.NAD_zimbraSkinLogoLoginBanner,
                        labelLocation:_LEFT_, width: 200,
                        onChange: GlobalConfigXFormView.onLoginLogoURLChange
                    } ,
                    { type:_SPACER_, height: 5 },
                    { type:_OUTPUT_, id:GlobalConfigXFormView.LogoLoginBannerPreviewId,
                        label:com_zimbra_delegatedadmin.NAD_zimbraLogoLoginBannerPreview,
                        getDisplayValue: ZaDomainXFormView.getLoginLogoPreview,
                        labelLocation:_LEFT_
                    } 
                ]
            };

        skinTab.items.push(globalSkinLogoItems);
    }
}
if(ZaTabView.XFormModifiers["GlobalConfigXFormView"]) {
	ZaTabView.XFormModifiers["GlobalConfigXFormView"].push(ZaDomainAdmin.GlobalConfigXFormModifier);
}
if (AjxEnv.hasFirebug) console.log ("Loaded domainadmin.js");
