/**
 * Created by IntelliJ IDEA.
 * User: jxy
 * Date: 10/19/11
 * Time: 11:07 PM
 * To change this template use File | Settings | File Templates.
 */
ZaNewCosXWizard = function(parent, entry) {
    ZaXWizardDialog.call(this, parent, null, ZaMsg.COSTBB_New_tt, "740px", "300px","ZaNewCosXWizard",null,ZaId.DLG_NEW_COS);
	this.TAB_INDEX = 0;	
	this.initForm(ZaCos.myXModel,this.getMyXForm(entry), null);

    this._localXForm.addListener(DwtEvent.XFORMS_VALUE_CHANGED, new AjxListener(this, ZaNewCosXWizard.prototype.handleXFormChange));
	this._localXForm.addListener(DwtEvent.XFORMS_VALUE_ERROR, new AjxListener(this, ZaNewCosXWizard.prototype.handleXFormChange));
}

ZaNewCosXWizard.prototype = new ZaXWizardDialog;
ZaNewCosXWizard.prototype.constructor = ZaNewCosXWizard;
ZaXDialog.XFormModifiers["ZaNewCosXWizard"] = new Array();
ZaNewCosXWizard.prototype.TAB_INDEX=0;
ZaNewCosXWizard.zimletChoices = new XFormChoices([], XFormChoices.SIMPLE_LIST);
ZaNewCosXWizard.themeChoices = new XFormChoices([], XFormChoices.SIMPLE_LIST);
/**
* @method setObject sets the object contained in the view
* @param entry - ZaDomain object to display
**/
ZaNewCosXWizard.prototype.toString = function() {
    return "ZaNewCosXWizard";
}
ZaNewCosXWizard.helpURL = location.pathname + ZaUtil.HELP_URL + "managing_accounts/create_an_account.htm?locid="+AjxEnv.DEFAULT_LOCALE;


ZaNewCosXWizard.prototype.handleXFormChange = function (ev) {
    if(ev && this._localXForm.hasErrors()) {
			this._button[DwtWizardDialog.FINISH_BUTTON].setEnabled(false);
	} else {
		if(this._containedObject.attrs[ZaCos.A_name]&& this._containedObject.attrs[ZaCos.A_name].length> 0){
			this._button[DwtWizardDialog.FINISH_BUTTON].setEnabled(true);
             if (this._containedObject[ZaModel.currentStep] != this._lastStep) {
                 this._button[DwtWizardDialog.NEXT_BUTTON].setEnabled(true);
            }
            if (this._containedObject[ZaModel.currentStep] != 1) {
                 this._button[DwtWizardDialog.PREV_BUTTON].setEnabled(true);
            }
        }
	}
}

ZaNewCosXWizard.prototype.popup =
function (loc) {
	ZaXWizardDialog.prototype.popup.call(this, loc);
	this._button[DwtWizardDialog.NEXT_BUTTON].setEnabled(false);
	this._button[DwtWizardDialog.FINISH_BUTTON].setEnabled(false);
	this._button[DwtWizardDialog.PREV_BUTTON].setEnabled(false);
}


ZaNewCosXWizard.prototype.createDomainAndAccount = function(domainName) {
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
		ZaApp.getInstance().getCurrentController()._handleException(ex, "ZaNewCosXWizard.prototype.createDomainAndAccount", null, false);
	}
}


ZaNewCosXWizard.prototype.finishWizard =
function() {
	try {
        if(!ZaCos.checkValues(this._containedObject)) {
			return false;
		}
		var cos = ZaItem.create(this._containedObject, ZaCos, "ZaCos");
		if(cos!=null) {
            cos.name= this._containedObject.attrs[ZaCos.A_name];
            cos.create(cos.name,this._containedObject.attrs);
            ZaApp.getInstance().getCosController().fireCreationEvent(cos);
			this.popdown();
            ZaApp.getInstance().getAppCtxt().getAppController().setActionStatusMsg(AjxMessageFormat.format(ZaMsg.CosCreated,[cos.name]));
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
            case ZmCsfeException.SIGNATURE_EXISTS:
                this.popdown();
                ZaApp.getInstance().getCurrentController()._handleException(ex, "ZaNewResourceXWizard.prototype.finishWizard", null, false);
            break;
			case ZmCsfeException.NO_SUCH_DOMAIN:
				ZaApp.getInstance().dialogs["confirmMessageDialog2"].setMessage(AjxMessageFormat.format(ZaMsg.CreateDomain_q,[ZaAccount.getDomain(this._containedObject.name)]), DwtMessageDialog.WARNING_STYLE);
				ZaApp.getInstance().dialogs["confirmMessageDialog2"].registerCallback(DwtDialog.YES_BUTTON, this.createDomainAndAccount, this, [ZaAccount.getDomain(this._containedObject.name)]);
				ZaApp.getInstance().dialogs["confirmMessageDialog2"].registerCallback(DwtDialog.NO_BUTTON, ZaController.prototype.closeCnfrmDelDlg, ZaApp.getInstance().getCurrentController(), null);
				ZaApp.getInstance().dialogs["confirmMessageDialog2"].popup();
			break;
			default:
				ZaApp.getInstance().getCurrentController()._handleException(ex, "ZaNewCosXWizard.prototype.finishWizard", null, false);
			break;
		}
	}
}

ZaNewCosXWizard.prototype.goNext =
function() {
	if (this._containedObject[ZaModel.currentStep] == 1) {
        this._button[DwtWizardDialog.PREV_BUTTON].setEnabled(true);
	}
	this.goPage(this._containedObject[ZaModel.currentStep] + 1);
	if(this._containedObject[ZaModel.currentStep] == this._lastStep) {
		this._button[DwtWizardDialog.NEXT_BUTTON].setEnabled(false);
	}
}

ZaNewCosXWizard.prototype.goPrev =
function() {
	if (this._containedObject[ZaModel.currentStep] == 2) {
		this._button[DwtWizardDialog.PREV_BUTTON].setEnabled(false);
	} else if(this._containedObject[ZaModel.currentStep] == this._lastStep) {
		this._button[DwtWizardDialog.NEXT_BUTTON].setEnabled(true);
	}
	this.goPage(this._containedObject[ZaModel.currentStep] - 1);
}


ZaNewCosXWizard.prototype.setObject =
function(entry) {
	//handle the special attributes to be displayed in xform
	entry.manageSpecialAttrs();

	this._containedObject = new ZaCos();
	this._containedObject.attrs = new Object();
    this._containedObject[ZaModel.currentStep] = entry[ZaModel.currentStep] || 1;
	
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
		
	if(entry.id)
		this._containedObject.id = entry.id;

    if(entry._uuid) {
        this._containedObject._uuid = entry._uuid;
    }

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

	
	if (entry.getAttrs) {
        if(entry.getAttrs[ZaCos.A_zimbraAvailableSkin] || entry.getAttrs.all) {
            var skins = entry.attrs[ZaCos.A_zimbraAvailableSkin];
            if(skins != null && skins != "") {
                if (AjxUtil.isString(skins))	 {
                    this._containedObject.attrs[ZaCos.A_zimbraAvailableSkin] = [skins];
                } else {
                    var cnt = skins.length;
                    this._containedObject.attrs[ZaCos.A_zimbraAvailableSkin] = [];
                    for(var i = 0; i < cnt; i ++) {
                        this._containedObject.attrs[ZaCos.A_zimbraAvailableSkin].push(skins[i]);
                    }
                }

            } else {
                this._containedObject.attrs[ZaCos.A_zimbraAvailableSkin] = null;
            }

            var skins = ZaApp.getInstance().getInstalledSkins();
            if(skins == null) {
                skins = [];
            } else if (AjxUtil.isString(skins))	 {
                skins = [skins];
            }

            ZaNewCosXWizard.themeChoices.setChoices(skins);
            ZaNewCosXWizard.themeChoices.dirtyChoices();
        }

        if(entry.getAttrs[ZaCos.A_zimbraZimletAvailableZimlets] || entry.getAttrs.all) {
            //get all Zimlets
            var allZimlets = ZaZimlet.getAll(ZaZimlet.EXCLUDE_EXTENSIONS);
            if(allZimlets == null) {
                allZimlets = [];
            }

            if(allZimlets instanceof ZaItemList || allZimlets instanceof AjxVector)
                allZimlets = allZimlets.getArray();

            //convert objects to strings
            var cnt = allZimlets.length;
            var _tmpZimlets = [];
            for(var i=0; i<cnt; i++) {
                var zimlet = allZimlets[i];
                _tmpZimlets.push(zimlet.name);
            }
            ZaNewCosXWizard.zimletChoices.setChoices(_tmpZimlets);
            ZaNewCosXWizard.zimletChoices.dirtyChoices();
        }
    }

    // update the domainlist
    ZaNewCosXWizard._domainList = ZaApp.getInstance().getDomainList(true).getArray();

	this._localXForm.setInstance(this._containedObject);
    if (!appNewUI)
	    this.updateTab();
}

ZaNewCosXWizard.gotSkins = function () {
	return ((ZaApp.getInstance().getInstalledSkins() != null) && (ZaApp.getInstance().getInstalledSkins().length > 0));
}

ZaNewCosXWizard.gotNoSkins = function () {
    return !ZaNewCosXWizard.gotSkins.call(this);
}

ZaNewCosXWizard.isPasswordLockoutEnabled = function () {
	return (this.getInstanceValue(ZaCos.A_zimbraPasswordLockoutEnabled) == 'TRUE');
}

ZaNewCosXWizard.isMailFeatureEnabled = function () {
	return (this.getInstanceValue(ZaCos.A_zimbraFeatureMailEnabled) == "TRUE");
}

ZaNewCosXWizard.isCalendarFeatureEnabled = function () {
	return this.getInstanceValue(ZaCos.A_zimbraFeatureCalendarEnabled)=="TRUE";
}

ZaNewCosXWizard.isMailForwardingEnabled = function () {
	return (this.getInstanceValue(ZaCos.A_zimbraFeatureMailForwardingEnabled) == "TRUE");
}

ZaNewCosXWizard.isMailFeatureEnabled = function () {
	return (this.getInstanceValue(ZaCos.A_zimbraFeatureMailEnabled) == "TRUE");
}

ZaNewCosXWizard.isBriefcaseFeatureEnabled = function () {
	return (this.getInstanceValue(ZaCos.A_zimbraFeatureBriefcasesEnabled) == "TRUE");
}

ZaNewCosXWizard.FEATURE_TAB_ATTRS = [ZaCos.A_zimbraFeatureMailEnabled,
	ZaCos.A_zimbraFeatureReadReceiptsEnabled,
	ZaCos.A_zimbraFeatureContactsEnabled,
	ZaCos.A_zimbraFeatureCalendarEnabled,
	ZaCos.A_zimbraFeatureTasksEnabled,
	//ZaCos.A_zimbraFeatureNotebookEnabled,
	ZaCos.A_zimbraFeatureBriefcasesEnabled,
	//ZaCos.A_zimbraFeatureIMEnabled,
	ZaCos.A_zimbraFeatureOptionsEnabled,
	ZaCos.A_zimbraFeatureTaggingEnabled,
	ZaCos.A_zimbraFeatureSharingEnabled,
	ZaCos.A_zimbraFeatureChangePasswordEnabled,
	ZaCos.A_zimbraFeatureSkinChangeEnabled,
	ZaCos.A_zimbraFeatureManageZimlets,
	ZaCos.A_zimbraFeatureHtmlComposeEnabled,
	//ZaCos.A_zimbraFeatureShortcutAliasesEnabled,
	ZaCos.A_zimbraFeatureGalEnabled,
	ZaCos.A_zimbraFeatureMAPIConnectorEnabled,
	ZaCos.A_zimbraFeatureGalAutoCompleteEnabled,
	ZaCos.A_zimbraFeatureMailPriorityEnabled,
	ZaCos.A_zimbraFeatureFlaggingEnabled,
	ZaCos.A_zimbraImapEnabled,
	ZaCos.A_zimbraPop3Enabled,
	ZaCos.A_zimbraFeatureImapDataSourceEnabled,
	ZaCos.A_zimbraFeaturePop3DataSourceEnabled,
	ZaCos.A_zimbraFeatureConversationsEnabled,
	ZaCos.A_zimbraFeatureFiltersEnabled,
	ZaCos.A_zimbraFeatureOutOfOfficeReplyEnabled,
	ZaCos.A_zimbraFeatureNewMailNotificationEnabled,
	ZaCos.A_zimbraFeatureMailPollingIntervalPreferenceEnabled,
	ZaCos.A_zimbraFeatureIdentitiesEnabled,
	ZaCos.A_zimbraFeatureGroupCalendarEnabled,
	//ZaCos.A_zimbraFeatureInstantNotify,
	ZaCos.A_zimbraFeaturePeopleSearchEnabled,
	ZaCos.A_zimbraFeatureAdvancedSearchEnabled,
	ZaCos.A_zimbraFeatureSavedSearchesEnabled,
	ZaCos.A_zimbraFeatureInitialSearchPreferenceEnabled,
	ZaCos.A_zimbraFeatureImportFolderEnabled,
    ZaCos.A_zimbraFeatureExportFolderEnabled,
	ZaCos.A_zimbraDumpsterEnabled,
	ZaCos.A_zimbraFeatureMailSendLaterEnabled,
	//ZaCos.A_zimbraFeatureFreeBusyViewEnabled,
    ZaCos.A_zimbraFeatureCalendarReminderDeviceEmailEnabled,
	ZaCos.A_zimbraFeatureSMIMEEnabled
];

ZaNewCosXWizard.FEATURE_TAB_RIGHTS = [];

ZaNewCosXWizard.PREFERENCES_TAB_ATTRS = [
	ZaCos.A_zimbraPrefMailSendReadReceipts,
	ZaCos.A_zimbraPrefUseTimeZoneListInCalendar,
	ZaCos.A_zimbraPrefCalendarUseQuickAdd,
	ZaCos.A_zimbraPrefCalendarAlwaysShowMiniCal,
	ZaCos.A_zimbraPrefCalendarApptReminderWarningTime,
	ZaCos.A_zimbraPrefTimeZoneId,
	ZaCos.A_zimbraPrefGalAutoCompleteEnabled,
	ZaCos.A_zimbraPrefAutoAddAddressEnabled,
	ZaCos.A_zimbraMailSignatureMaxLength,
	ZaCos.A_zimbraPrefForwardReplyInOriginalFormat,
	ZaCos.A_zimbraPrefHtmlEditorDefaultFontColor,
	ZaCos.A_zimbraPrefHtmlEditorDefaultFontFamily,
	ZaCos.A_zimbraPrefHtmlEditorDefaultFontSize,
	ZaCos.A_zimbraPrefComposeFormat,
	ZaCos.A_zimbraPrefComposeInNewWindow,
	ZaCos.A_zimbraAllowAnyFromAddress,
	ZaCos.A_zimbraMailMinPollingInterval,
	ZaCos.A_zimbraPrefMailPollingInterval,
	ZaCos.A_zimbraPrefAutoSaveDraftInterval,
	ZaCos.A_zimbraPrefMailDefaultCharset,
	ZaCos.A_zimbraMaxMailItemsPerPage,
	ZaCos.A_zimbraPrefMailItemsPerPage,
	ZaCos.A_zimbraPrefGroupMailBy,
	ZaCos.A_zimbraPrefDisplayExternalImages,
	ZaCos.A_zimbraPrefMessageViewHtmlPreferred,
	ZaCos.A_zimbraPrefLocale,
	ZaCos.A_zimbraJunkMessagesIndexingEnabled,
	ZaCos.A_zimbraPrefShowSelectionCheckbox,
	ZaCos.A_zimbraPrefWarnOnExit,
	ZaCos.A_zimbraPrefAdminConsoleWarnOnExit,
    ZaCos.A_zimbraPrefUseKeyboardShortcuts,
	ZaCos.A_zimbraPrefImapSearchFoldersEnabled,
	ZaCos.A_zimbraPrefShowSearchString,
	ZaCos.A_zimbraPrefMailInitialSearch,
	ZaCos.A_zimbraPrefClientType,
	ZaCos.A_zimbraPrefCalendarInitialView,
	ZaCos.A_zimbraPrefCalendarFirstDayOfWeek,
	ZaCos.A_zimbraPrefCalendarReminderFlashTitle,
	ZaCos.A_zimbraPrefCalendarAllowCancelEmailToSelf,
	ZaCos.A_zimbraPrefCalendarAllowPublishMethodInvite,
	ZaCos.A_zimbraPrefCalendarToasterEnabled,
	ZaCos.A_zimbraPrefCalendarShowPastDueReminders,
	ZaCos.A_zimbraPrefAppleIcalDelegationEnabled,
	ZaCos.A_zimbraPrefMandatorySpellCheckEnabled
];
ZaNewCosXWizard.PREFERENCES_TAB_RIGHTS = [];

ZaNewCosXWizard.SKIN_TAB_ATTRS = [ZaCos.A_zimbraPrefSkin,ZaCos.A_zimbraAvailableSkin];
ZaNewCosXWizard.SKIN_TAB_RIGHTS = [];

ZaNewCosXWizard.ZIMLET_TAB_ATTRS = [ZaCos.A_zimbraZimletAvailableZimlets];
ZaNewCosXWizard.ZIMLET_TAB_RIGHTS = [ZaCos.RIGHT_GET_ZIMLET, ZaCos.RIGHT_LIST_ZIMLET];

ZaNewCosXWizard.SERVERPOOL_TAB_ATTRS = [ZaCos.A_zimbraMailHostPool];
ZaNewCosXWizard.SERVERPOOL_TAB_RIGHTS = [ZaCos.RIGHT_GET_HOSTNAME];

ZaNewCosXWizard.ADVANCED_TAB_ATTRS = [ZaCos.A_zimbraAttachmentsBlocked,
	ZaCos.A_zimbraMailQuota,
	ZaCos.A_zimbraContactMaxNumEntries,
	ZaCos.A_zimbraQuotaWarnPercent,
	ZaCos.A_zimbraQuotaWarnInterval,
	ZaCos.A_zimbraQuotaWarnMessage,
	ZaCos.A_zimbraPasswordLocked,
	ZaCos.A_zimbraMinPwdLength,
	ZaCos.A_zimbraMaxPwdLength,
	ZaCos.A_zimbraPasswordMinUpperCaseChars,
	ZaCos.A_zimbraPasswordMinLowerCaseChars,
	ZaCos.A_zimbraPasswordMinPunctuationChars,
	ZaCos.A_zimbraPasswordMinNumericChars,
	ZaCos.A_zimbraPasswordMinDigitsOrPuncs,
	ZaCos.A_zimbraMinPwdAge,
	ZaCos.A_zimbraMaxPwdAge,
	ZaCos.A_zimbraEnforcePwdHistory,
	ZaCos.A_zimbraPasswordLockoutEnabled,
	ZaCos.A_zimbraPasswordLockoutMaxFailures,
	ZaCos.A_zimbraPasswordLockoutDuration,
	ZaCos.A_zimbraPasswordLockoutFailureLifetime,
	ZaCos.A_zimbraAdminAuthTokenLifetime,
	ZaCos.A_zimbraAuthTokenLifetime,
	ZaCos.A_zimbraMailIdleSessionTimeout,
	ZaCos.A_zimbraMailMessageLifetime,
	ZaCos.A_zimbraMailTrashLifetime,
	ZaCos.A_zimbraMailSpamLifetime,
	ZaCos.A_zimbraFreebusyExchangeUserOrg,
        ZaCos.A_zimbraDataSourcePop3PollingInterval,
        ZaCos.A_zimbraDataSourceImapPollingInterval,
        ZaCos.A_zimbraDataSourceCalendarPollingInterval,
        ZaCos.A_zimbraDataSourceRssPollingInterval,
        ZaCos.A_zimbraDataSourceCaldavPollingInterval,
	ZaCos.A_zimbraDataSourceMinPollingInterval

];
ZaNewCosXWizard.ADVANCED_TAB_RIGHTS = [];

ZaNewCosXWizard.myXFormModifier = function(xFormObject, entry) {
    this.stepChoices = [];
	var _tab1 = ++this.TAB_INDEX;
	var _tab2, _tab3, _tab4, _tab5, _tab6, _tab7;

	var headerItems = [	{type:_AJX_IMAGE_, src:"COS_32", label:null,rowSpan:2},
							{type:_OUTPUT_, ref:ZaCos.A_name, label:null,cssClass:"AdminTitle",
                                visibilityChecks:[ZaItem.hasReadPermission], height: 32, rowSpan:2},
							{type:_OUTPUT_, ref:ZaItem.A_zimbraId, label:ZaMsg.NAD_ZimbraID,visibilityChecks:[ZaItem.hasReadPermission]},
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
					 		}];

	this.stepChoices.push({value:_tab1, label:ZaMsg.TABT_GeneralPage});

    if(ZaTabView.isTAB_ENABLED(entry,ZaNewCosXWizard.FEATURE_TAB_ATTRS, ZaNewCosXWizard.FEATURE_TAB_RIGHTS)) {
        _tab2 = ++this.TAB_INDEX;
        this.stepChoices.push({value:_tab2, label:ZaMsg.TABT_Features});
    }

    if(ZaTabView.isTAB_ENABLED(entry,ZaNewCosXWizard.PREFERENCES_TAB_ATTRS, ZaNewCosXWizard.PREFERENCES_TAB_RIGHTS)) {
    	_tab3 = ++this.TAB_INDEX;
        this.stepChoices.push({value:_tab3, label:ZaMsg.TABT_Preferences});
    }

    if(ZaTabView.isTAB_ENABLED(entry,ZaNewCosXWizard.SKIN_TAB_ATTRS, ZaNewCosXWizard.SKIN_TAB_RIGHTS)) {
       	_tab4 = ++this.TAB_INDEX;
        this.stepChoices.push({value:_tab4, label:ZaMsg.TABT_Themes});
    }

    var allZimlets = ZaZimlet.getAll(ZaZimlet.EXCLUDE_EXTENSIONS);
    if(allZimlets != null && !AjxUtil.isEmpty(allZimlets.getArray()) && ZaTabView.isTAB_ENABLED(entry,ZaNewCosXWizard.ZIMLET_TAB_ATTRS, ZaNewCosXWizard.ZIMLET_TAB_RIGHTS)) {
		_tab5 = ++this.TAB_INDEX;
        this.stepChoices.push({value:_tab5, label:ZaMsg.TABT_Zimlets});
    }

    if(ZaTabView.isTAB_ENABLED(entry,ZaNewCosXWizard.SERVERPOOL_TAB_ATTRS, ZaNewCosXWizard.SERVERPOOL_TAB_RIGHTS)) {
    	_tab6 = ++this.TAB_INDEX;
        this.stepChoices.push({value:_tab6, label:ZaMsg.TABT_ServerPool});
    }

    if(ZaTabView.isTAB_ENABLED(entry,ZaNewCosXWizard.ADVANCED_TAB_ATTRS, ZaNewCosXWizard.ADVANCED_TAB_RIGHTS)) {
    	_tab7 = ++this.TAB_INDEX;
        this.stepChoices.push({value:_tab7, label:ZaMsg.TABT_Advanced});
    }


    var cases = [];
	var case1 = {type:_CASE_,caseKey:_tab1,numCols:1,
        width:"100%", cellpadding:0
    };

    var case1Items = [
		{type:_GROUP_, numCols:2,colSizes: ["200px","auto"],
			items:[
				{ref:ZaCos.A_name, type:_INPUT_,
					enableDisableChecks:[[ZaItem.hasRight,ZaCos.RENAME_COS_RIGHT]],
					msgName:ZaMsg.NAD_DisplayName,label:ZaMsg.NAD_DisplayName, labelLocation:_LEFT_,
					cssClass:"admin_xform_name_input", required:true, width: "30em"
				},
				{ref:"description",  msgName:ZaMsg.NAD_Description,
					label:ZaMsg.NAD_Description, labelLocation:_LEFT_, //cssClass:"admin_xform_name_input" ,
					labelCssStyle:"vertical-align:top;",
					type:_TEXTFIELD_,
					align:_LEFT_,
					enableDisableChecks:[ZaItem.hasWritePermission],
					visibilityChecks:[ZaItem.hasReadPermission],
					width:"30em"
				},
				{ref:ZaCos.A_zimbraNotes, type:_TEXTAREA_,
					msgName:ZaMsg.NAD_Notes,label:ZaMsg.NAD_Notes, labelLocation:_LEFT_,
					labelCssStyle:"vertical-align:top;", width: "30em"
				}
			]
		}
	];

	case1.items = case1Items;
	cases.push(case1);

    if(_tab2) {
        var case2 = {type:_CASE_,caseKey:_tab2,numCols:1,colSizes:["auto"],
            width:"100%", cellpadding:0,
            id:"cos_form_features_tab"};

        var case2Items = [
            {type:_ZAWIZ_TOP_GROUPER_,  label:ZaMsg.NAD_zimbraMajorFeature, id:"cos_form_features_major",
            	visibilityChecks:[[ZATopGrouper_XFormItem.isGroupVisible,
					[
						ZaCos.A_zimbraFeatureMailEnabled,
						ZaCos.A_zimbraFeatureContactsEnabled,
						ZaCos.A_zimbraFeatureCalendarEnabled,
						ZaCos.A_zimbraFeatureTasksEnabled,
						ZaCos.A_zimbraFeatureBriefcasesEnabled,
						ZaCos.A_zimbraFeatureOptionsEnabled
					]]
				],
                items:[
                    {ref:ZaCos.A_zimbraFeatureMailEnabled, type:_WIZ_CHECKBOX_, msgName:ZaMsg.LBL_zimbraFeatureMailEnabled,label:ZaMsg.LBL_zimbraFeatureMailEnabled,trueValue:"TRUE", falseValue:"FALSE"},
                    {ref:ZaCos.A_zimbraFeatureContactsEnabled, type:_WIZ_CHECKBOX_, msgName:ZaMsg.LBL_zimbraFeatureContactsEnabled,label:ZaMsg.LBL_zimbraFeatureContactsEnabled,trueValue:"TRUE", falseValue:"FALSE"},
                    {ref:ZaCos.A_zimbraFeatureCalendarEnabled, type:_WIZ_CHECKBOX_, msgName:ZaMsg.LBL_zimbraFeatureCalendarEnabled,label:ZaMsg.LBL_zimbraFeatureCalendarEnabled,  trueValue:"TRUE", falseValue:"FALSE"},
                    {ref:ZaCos.A_zimbraFeatureTasksEnabled, type:_WIZ_CHECKBOX_, msgName:ZaMsg.LBL_zimbraFeatureTaskEnabled,label:ZaMsg.LBL_zimbraFeatureTaskEnabled,  trueValue:"TRUE", falseValue:"FALSE"},
                    //{ref:ZaCos.A_zimbraFeatureNotebookEnabled, type:_WIZ_CHECKBOX_, msgName:ZaMsg.LBL_zimbraFeatureNotebookEnabled,label:ZaMsg.LBL_zimbraFeatureNotebookEnabled,  trueValue:"TRUE", falseValue:"FALSE"},
                    {ref:ZaCos.A_zimbraFeatureBriefcasesEnabled, type:_WIZ_CHECKBOX_, msgName:ZaMsg.LBL_zimbraFeatureBriefcasesEnabled,label:ZaMsg.LBL_zimbraFeatureBriefcasesEnabled,  trueValue:"TRUE", falseValue:"FALSE"},
                    //{ref:ZaCos.A_zimbraFeatureIMEnabled, type:_WIZ_CHECKBOX_, msgName:ZaMsg.LBL_zimbraFeatureIMEnabled,label:ZaMsg.LBL_zimbraFeatureIMEnabled,  trueValue:"TRUE", falseValue:"FALSE"},
                    {ref:ZaCos.A_zimbraFeatureOptionsEnabled, type:_WIZ_CHECKBOX_, msgName:ZaMsg.LBL_zimbraFeatureOptionsEnabled,label:ZaMsg.LBL_zimbraFeatureOptionsEnabled,  trueValue:"TRUE", falseValue:"FALSE"}
                    //zimbraMobile from the extension
                ]
            },
            {type:_ZAWIZ_TOP_GROUPER_,  label:ZaMsg.NAD_zimbraGeneralFeature, id:"cos_form_features_general",
                visibilityChecks:[[ZATopGrouper_XFormItem.isGroupVisible,
					[
						ZaCos.A_zimbraFeatureTaggingEnabled,
						ZaCos.A_zimbraFeatureSharingEnabled,
						ZaCos.A_zimbraFeatureChangePasswordEnabled,
						ZaCos.A_zimbraFeatureSkinChangeEnabled,
						ZaCos.A_zimbraFeatureManageZimlets,
						ZaCos.A_zimbraFeatureHtmlComposeEnabled,
						ZaCos.A_zimbraFeatureGalEnabled,
						ZaCos.A_zimbraFeatureMAPIConnectorEnabled,
						ZaCos.A_zimbraFeatureGalAutoCompleteEnabled,
						ZaCos.A_zimbraFeatureImportFolderEnabled,
                        ZaCos.A_zimbraFeatureExportFolderEnabled,
						ZaCos.A_zimbraDumpsterEnabled
					]]
				],
                items:[
                    {ref:ZaCos.A_zimbraFeatureTaggingEnabled, type:_WIZ_CHECKBOX_, msgName:ZaMsg.LBL_zimbraFeatureTaggingEnabled,label:ZaMsg.LBL_zimbraFeatureTaggingEnabled,trueValue:"TRUE", falseValue:"FALSE"},
                    {ref:ZaCos.A_zimbraFeatureSharingEnabled, type:_WIZ_CHECKBOX_, msgName:ZaMsg.LBL_zimbraFeatureSharingEnabled,label:ZaMsg.LBL_zimbraFeatureSharingEnabled,trueValue:"TRUE", falseValue:"FALSE"},
                    {ref:ZaCos.A_zimbraFeatureChangePasswordEnabled, type:_WIZ_CHECKBOX_, msgName:ZaMsg.LBL_zimbraFeatureChangePasswordEnabled,label:ZaMsg.LBL_zimbraFeatureChangePasswordEnabled, trueValue:"TRUE", falseValue:"FALSE"},
                    {ref:ZaCos.A_zimbraFeatureSkinChangeEnabled, type:_WIZ_CHECKBOX_, msgName:ZaMsg.LBL_zimbraFeatureSkinChangeEnabled,label:ZaMsg.LBL_zimbraFeatureSkinChangeEnabled,  trueValue:"TRUE", falseValue:"FALSE"},
                    {ref:ZaCos.A_zimbraFeatureManageZimlets, type:_WIZ_CHECKBOX_, msgName:ZaMsg.LBL_zimbraFeatureManageZimlets,label:ZaMsg.LBL_zimbraFeatureManageZimlets,  trueValue:"TRUE", falseValue:"FALSE"},
                    {ref:ZaCos.A_zimbraFeatureHtmlComposeEnabled, type:_WIZ_CHECKBOX_, msgName:ZaMsg.LBL_zimbraFeatureHtmlComposeEnabled,label:ZaMsg.LBL_zimbraFeatureHtmlComposeEnabled, trueValue:"TRUE", falseValue:"FALSE"},
                    //{ref:ZaCos.A_zimbraFeatureShortcutAliasesEnabled, type:_WIZ_CHECKBOX_, msgName:ZaMsg.LBL_zimbraFeatureShortcutAliasesEnabled,label:ZaMsg.LBL_zimbraFeatureShortcutAliasesEnabled, trueValue:"TRUE", falseValue:"FALSE"},
                    {ref:ZaCos.A_zimbraFeatureGalEnabled, type:_WIZ_CHECKBOX_, msgName:ZaMsg.LBL_zimbraFeatureGalEnabled,label:ZaMsg.LBL_zimbraFeatureGalEnabled, trueValue:"TRUE", falseValue:"FALSE"},
                    {ref:ZaCos.A_zimbraFeatureMAPIConnectorEnabled, type:_WIZ_CHECKBOX_, msgName:ZaMsg.LBL_zimbraFeatureMAPIConnectorEnabled,label:ZaMsg.LBL_zimbraFeatureMAPIConnectorEnabled, trueValue:"TRUE", falseValue:"FALSE"},
		    {ref:ZaCos.A_zimbraFeatureGalAutoCompleteEnabled, type:_WIZ_CHECKBOX_, msgName:ZaMsg.LBL_zimbraFeatureGalAutoCompleteEnabled,label:ZaMsg.LBL_zimbraFeatureGalAutoCompleteEnabled, trueValue:"TRUE", falseValue:"FALSE"},
                    {ref:ZaCos.A_zimbraFeatureImportFolderEnabled, type:_WIZ_CHECKBOX_, msgName:ZaMsg.LBL_zimbraFeatureImportFolderEnabled,label:ZaMsg.LBL_zimbraFeatureImportFolderEnabled, trueValue:"TRUE", falseValue:"FALSE"},
		    		{ref:ZaCos.A_zimbraFeatureExportFolderEnabled, type:_WIZ_CHECKBOX_, msgName:ZaMsg.LBL_zimbraFeatureExportFolderEnabled,label:ZaMsg.LBL_zimbraFeatureExportFolderEnabled, trueValue:"TRUE", falseValue:"FALSE"},
					{ref:ZaCos.A_zimbraDumpsterEnabled, type:_WIZ_CHECKBOX_, msgName:ZaMsg.LBL_zimbraDumpsterEnabled,label:ZaMsg.LBL_zimbraDumpsterEnabled, trueValue:"TRUE", falseValue:"FALSE"}
                ]
            },
            {type:_ZAWIZ_TOP_GROUPER_,  label:ZaMsg.NAD_zimbraMailFeature, id:"cos_form_features_mail",
                enableDisableChecks:[ZaNewCosXWizard.isMailFeatureEnabled],
                enableDisableChangeEventSources:[ZaCos.A_zimbraFeatureMailEnabled],
                visibilityChecks:[[ZATopGrouper_XFormItem.isGroupVisible,
					[
						ZaCos.A_zimbraFeatureMailPriorityEnabled,
						ZaCos.A_zimbraFeatureFlaggingEnabled,
						ZaCos.A_zimbraImapEnabled,
						ZaCos.A_zimbraPop3Enabled,
						ZaCos.A_zimbraFeatureImapDataSourceEnabled,
						ZaCos.A_zimbraFeaturePop3DataSourceEnabled,
						ZaCos.A_zimbraFeatureMailForwardingEnabled,
						ZaCos.A_zimbraFeatureMailSendLaterEnabled,
						ZaCos.A_zimbraFeatureConversationsEnabled,
						ZaCos.A_zimbraFeatureFiltersEnabled,
						ZaCos.A_zimbraFeatureOutOfOfficeReplyEnabled,
						ZaCos.A_zimbraFeatureNewMailNotificationEnabled,
						ZaCos.A_zimbraFeatureMailPollingIntervalPreferenceEnabled,
						ZaCos.A_zimbraFeatureIdentitiesEnabled,
						ZaCos.A_zimbraFeatureReadReceiptsEnabled
					]]
				],
                items:[
                        {ref:ZaCos.A_zimbraFeatureMailPriorityEnabled, type:_WIZ_CHECKBOX_, msgName:ZaMsg.LBL_zimbraFeatureMailPriorityEnabled,label:ZaMsg.LBL_zimbraFeatureMailPriorityEnabled,  trueValue:"TRUE", falseValue:"FALSE"},
                        {ref:ZaCos.A_zimbraFeatureFlaggingEnabled, type:_WIZ_CHECKBOX_, msgName:ZaMsg.LBL_zimbraFeatureFlaggingEnabled,label:ZaMsg.LBL_zimbraFeatureFlaggingEnabled,  trueValue:"TRUE", falseValue:"FALSE"},
                        {ref:ZaCos.A_zimbraImapEnabled, type:_WIZ_CHECKBOX_, msgName:ZaMsg.LBL_zimbraImapEnabled,label:ZaMsg.LBL_zimbraImapEnabled,trueValue:"TRUE", falseValue:"FALSE"},
                        {ref:ZaCos.A_zimbraPop3Enabled, type:_WIZ_CHECKBOX_, msgName:ZaMsg.LBL_zimbraPop3Enabled,label:ZaMsg.LBL_zimbraPop3Enabled,trueValue:"TRUE", falseValue:"FALSE"},
                        {ref:ZaCos.A_zimbraFeatureImapDataSourceEnabled, type:_WIZ_CHECKBOX_, msgName:ZaMsg.LBL_zimbraExternalImapEnabled,label:ZaMsg.LBL_zimbraExternalImapEnabled, trueValue:"TRUE", falseValue:"FALSE"},
                        {ref:ZaCos.A_zimbraFeaturePop3DataSourceEnabled, type:_WIZ_CHECKBOX_, msgName:ZaMsg.LBL_zimbraExternalPop3Enabled,label:ZaMsg.LBL_zimbraExternalPop3Enabled, trueValue:"TRUE", falseValue:"FALSE"},
                        {ref:ZaCos.A_zimbraFeatureMailForwardingEnabled, type:_WIZ_CHECKBOX_, msgName:ZaMsg.LBL_zimbraFeatureMailForwardingEnabled,label:ZaMsg.LBL_zimbraFeatureMailForwardingEnabled, trueValue:"TRUE", falseValue:"FALSE"},
                        {ref:ZaCos.A_zimbraFeatureMailSendLaterEnabled, type:_WIZ_CHECKBOX_, msgName:ZaMsg.LBL_zimbraFeatureMailSendLaterEnabled, label:ZaMsg.LBL_zimbraFeatureMailSendLaterEnabled, trueValue:"TRUE", falseValue:"FALSE"},
			{ref:ZaCos.A_zimbraFeatureConversationsEnabled, type:_WIZ_CHECKBOX_, msgName:ZaMsg.LBL_zimbraFeatureConversationsEnabled,label:ZaMsg.LBL_zimbraFeatureConversationsEnabled,trueValue:"TRUE", falseValue:"FALSE"},
                        {ref:ZaCos.A_zimbraFeatureFiltersEnabled, type:_WIZ_CHECKBOX_, msgName:ZaMsg.LBL_zimbraFeatureFiltersEnabled,label:ZaMsg.LBL_zimbraFeatureFiltersEnabled,trueValue:"TRUE", falseValue:"FALSE"},
                        {ref:ZaCos.A_zimbraFeatureOutOfOfficeReplyEnabled, type:_WIZ_CHECKBOX_, msgName:ZaMsg.LBL_zimbraFeatureOutOfOfficeReplyEnabled,label:ZaMsg.LBL_zimbraFeatureOutOfOfficeReplyEnabled, trueValue:"TRUE", falseValue:"FALSE"},
                        {ref:ZaCos.A_zimbraFeatureNewMailNotificationEnabled, type:_WIZ_CHECKBOX_, msgName:ZaMsg.LBL_zimbraFeatureNewMailNotificationEnabled,label:ZaMsg.LBL_zimbraFeatureNewMailNotificationEnabled, trueValue:"TRUE", falseValue:"FALSE"},
                        {ref:ZaCos.A_zimbraFeatureMailPollingIntervalPreferenceEnabled, type:_WIZ_CHECKBOX_, msgName:ZaMsg.LBL_zimbraFeatureMailPollingIntervalPreferenceEnabled,label:ZaMsg.LBL_zimbraFeatureMailPollingIntervalPreferenceEnabled, trueValue:"TRUE", falseValue:"FALSE"},
                        {ref:ZaCos.A_zimbraFeatureIdentitiesEnabled, type:_WIZ_CHECKBOX_, msgName:ZaMsg.LBL_zimbraFeatureIdentitiesEnabled,label:ZaMsg.LBL_zimbraFeatureIdentitiesEnabled,trueValue:"TRUE", falseValue:"FALSE"},
                        {ref:ZaCos.A_zimbraFeatureReadReceiptsEnabled, type:_WIZ_CHECKBOX_,label:ZaMsg.LBL_zimbraFeatureReadReceiptsEnabled,trueValue:"TRUE", falseValue:"FALSE"}

                ]
            },
            {type:_ZAWIZ_TOP_GROUPER_,  label:ZaMsg.NAD_zimbraCalendarFeature, id:"cos_form_features_calendar",
                enableDisableChecks:[ZaNewCosXWizard.isCalendarFeatureEnabled],
                enableDisableChangeEventSources:[ZaCos.A_zimbraFeatureCalendarEnabled],
				visibilityChecks:[[ZATopGrouper_XFormItem.isGroupVisible,
					[
						ZaCos.A_zimbraFeatureGroupCalendarEnabled,
						//ZaCos.A_zimbraFeatureFreeBusyViewEnabled,
                        ZaCos.A_zimbraFeatureCalendarReminderDeviceEmailEnabled
					]]
				],
                items:[
                    {ref:ZaCos.A_zimbraFeatureGroupCalendarEnabled, type:_WIZ_CHECKBOX_, msgName:ZaMsg.LBL_zimbraFeatureGroupCalendarEnabled,label:ZaMsg.LBL_zimbraFeatureGroupCalendarEnabled, trueValue:"TRUE", falseValue:"FALSE"},
		            //{ref:ZaCos.A_zimbraFeatureFreeBusyViewEnabled, type:_WIZ_CHECKBOX_, msgName:ZaMsg.LBL_zimbraFeatureFreeBusyViewEnabled, label:ZaMsg.LBL_zimbraFeatureFreeBusyViewEnabled, trueValue:"TRUE", falseValue:"FALSE"},
                    {ref:ZaCos.A_zimbraFeatureCalendarReminderDeviceEmailEnabled, type:_WIZ_CHECKBOX_, msgName:ZaMsg.LBL_zimbraFeatureCalendarReminderDeviceEmailEnabled, label:ZaMsg.LBL_zimbraFeatureCalendarReminderDeviceEmailEnabled, trueValue:"TRUE", falseValue:"FALSE"}
                ]
            },
            //{type:_ZA_TOP_GROUPER_,  label:ZaMsg.NAD_zimbraIMFeature, id:"cos_form_features_im",
            //    visibilityChecks:[ZaNewCosXWizard.isIMFeatureEnabled],
            //    visibilityChangeEventSources:[ZaCos.A_zimbraFeatureIMEnabled],
            //    visibilityChecks:[[ZATopGrouper_XFormItem.isGroupVisible,
	//				[
	//					ZaCos.A_zimbraFeatureInstantNotify
	//				]]
	//			],
          //      items:[
          //        {ref:ZaCos.A_zimbraFeatureInstantNotify,
          //           type:_WIZ_CHECKBOX_,
          //           msgName:ZaMsg.LBL_zimbraFeatureInstantNotify,
          //           label:ZaMsg.LBL_zimbraFeatureInstantNotify,
          //           trueValue:"TRUE",
          //           falseValue:"FALSE"}

          //      ]
          //  },
            {type:_ZAWIZ_TOP_GROUPER_,  label:ZaMsg.NAD_zimbraSearchFeature, id:"cos_form_features_search",
                visibilityChecks:[[ZATopGrouper_XFormItem.isGroupVisible,
					[
						ZaCos.A_zimbraFeatureAdvancedSearchEnabled,
						ZaCos.A_zimbraFeatureSavedSearchesEnabled,
						ZaCos.A_zimbraFeatureInitialSearchPreferenceEnabled,
						ZaCos.A_zimbraFeaturePeopleSearchEnabled
					]]
				],
                items:[
                    {ref:ZaCos.A_zimbraFeatureAdvancedSearchEnabled, type:_WIZ_CHECKBOX_, msgName:ZaMsg.LBL_zimbraFeatureAdvancedSearchEnabled,label:ZaMsg.LBL_zimbraFeatureAdvancedSearchEnabled,trueValue:"TRUE", falseValue:"FALSE"},
                    {ref:ZaCos.A_zimbraFeatureSavedSearchesEnabled, type:_WIZ_CHECKBOX_, msgName:ZaMsg.LBL_zimbraFeatureSavedSearchesEnabled,label:ZaMsg.LBL_zimbraFeatureSavedSearchesEnabled,trueValue:"TRUE", falseValue:"FALSE"},
                    {ref:ZaCos.A_zimbraFeatureInitialSearchPreferenceEnabled, type:_WIZ_CHECKBOX_, msgName:ZaMsg.LBL_zimbraFeatureInitialSearchPreferenceEnabled,label:ZaMsg.LBL_zimbraFeatureInitialSearchPreferenceEnabled, trueValue:"TRUE", falseValue:"FALSE"},
                    {ref:ZaCos.A_zimbraFeaturePeopleSearchEnabled, type:_WIZ_CHECKBOX_, msgName:ZaMsg.LBL_zimbraFeaturePeopleSearchEnabled,label:ZaMsg.LBL_zimbraFeaturePeopleSearchEnabled, trueValue:"TRUE", falseValue:"FALSE"}
                ]
            },
            {type:_ZAWIZ_TOP_GROUPER_,  label:ZaMsg.NAD_zimbraSMIMEFeature, id:"cos_form_features_smime",
                visibilityChecks:[[ZATopGrouper_XFormItem.isGroupVisible,
					[
						ZaCos.A_zimbraFeatureSMIMEEnabled
					]]
				],
                items:[
                    {ref:ZaCos.A_zimbraFeatureSMIMEEnabled, type:_WIZ_CHECKBOX_, msgName:ZaMsg.LBL_zimbraFeatureSMIMEEnabled,label:ZaMsg.LBL_zimbraFeatureSMIMEEnabled, trueValue:"TRUE", falseValue:"FALSE"}
                ]
            }


        ];

        case2.items = case2Items;
        cases.push(case2);
    }

    if(_tab3) {
        var case3 = {type:_CASE_,caseKey:_tab3, id:"cos_for_prefs_tab",
            width:"100%", cellpadding:0,
            numCols:1};
        var case3Items = [
            {type:_ZAWIZ_TOP_GROUPER_, id:"account_prefs_general",
                label:ZaMsg.NAD_GeneralOptions,
                visibilityChecks:[[ZATopGrouper_XFormItem.isGroupVisible,
					[
						ZaCos.A_zimbraPrefClientType,
						ZaCos.A_zimbraPrefShowSearchString,
						ZaCos.A_zimbraPrefMailInitialSearch,
						ZaCos.A_zimbraPrefImapSearchFoldersEnabled,
						ZaCos.A_zimbraPrefUseKeyboardShortcuts,
						ZaCos.A_zimbraPrefWarnOnExit,
						ZaCos.A_zimbraPrefAdminConsoleWarnOnExit,
						ZaCos.A_zimbraPrefShowSelectionCheckbox,
						//ZaCos.A_zimbraPrefIMAutoLogin,
						ZaCos.A_zimbraJunkMessagesIndexingEnabled,
						ZaCos.A_zimbraPrefLocale
					]]
                ],
                items :[
                {ref:ZaCos.A_zimbraPrefClientType, type:_OSELECT1_, msgName:ZaMsg.MSG_zimbraPrefClientType,
                    label:ZaMsg.LBL_zimbraPrefClientType, labelLocation:_LEFT_
                },
                {ref:ZaCos.A_zimbraPrefShowSearchString, type:_WIZ_CHECKBOX_,
                    msgName:ZaMsg.LBL_zimbraPrefShowSearchString,
                    label:ZaMsg.LBL_zimbraPrefShowSearchString, trueValue:"TRUE", falseValue:"FALSE"
                },
                {ref:ZaCos.A_zimbraPrefMailInitialSearch, type:_TEXTFIELD_, cssClass:"admin_xform_name_input",
                    msgName:ZaMsg.LBL_zimbraPrefMailInitialSearch,label:ZaMsg.LBL_zimbraPrefMailInitialSearch,
                    labelLocation:_LEFT_
                },
                {ref:ZaCos.A_zimbraPrefImapSearchFoldersEnabled, type:_WIZ_CHECKBOX_,
                    msgName:ZaMsg.LBL_zimbraPrefImapSearchFoldersEnabled,
                    label:ZaMsg.LBL_zimbraPrefImapSearchFoldersEnabled, trueValue:"TRUE", falseValue:"FALSE"
                },
                {ref:ZaCos.A_zimbraPrefUseKeyboardShortcuts, type:_WIZ_CHECKBOX_,
                    msgName:ZaMsg.LBL_zimbraPrefUseKeyboardShortcuts,label:ZaMsg.LBL_zimbraPrefUseKeyboardShortcuts,
                    trueValue:"TRUE", falseValue:"FALSE"
                },
                {ref:ZaCos.A_zimbraPrefWarnOnExit, type:_WIZ_CHECKBOX_,
                    msgName:ZaMsg.LBL_zimbraPrefWarnOnExit,label:ZaMsg.LBL_zimbraPrefWarnOnExit,
                    trueValue:"TRUE", falseValue:"FALSE"
                },
                {ref:ZaCos.A_zimbraPrefAdminConsoleWarnOnExit, type:_WIZ_CHECKBOX_,
                    msgName:ZabMsg.LBL_zimbraPrefAdminConsoleWarnOnExit,label:ZabMsg.LBL_zimbraPrefAdminConsoleWarnOnExit,
                    trueValue:"TRUE", falseValue:"FALSE"
                },
                {ref:ZaCos.A_zimbraPrefShowSelectionCheckbox, type:_WIZ_CHECKBOX_,
                    msgName:ZaMsg.LBL_zimbraPrefShowSelectionCheckbox,label:ZaMsg.LBL_zimbraPrefShowSelectionCheckbox,
                    trueValue:"TRUE", falseValue:"FALSE"
                },
                //{ref:ZaCos.A_zimbraPrefIMAutoLogin, type:_WIZ_CHECKBOX_,
                //    msgName:ZaMsg.LBL_zimbraPrefIMAutoLogin,label:ZaMsg.LBL_zimbraPrefIMAutoLogin,
                //    trueValue:"TRUE", falseValue:"FALSE"
                //},
                {ref:ZaCos.A_zimbraJunkMessagesIndexingEnabled, type:_WIZ_CHECKBOX_,
                    msgName:ZaMsg.LBL_zimbraJunkMessagesIndexingEnabled,
                    label:ZaMsg.LBL_zimbraJunkMessagesIndexingEnabled,
                    trueValue:"TRUE", falseValue:"FALSE"
                },
                {ref:ZaCos.A_zimbraPrefLocale, type:_OSELECT1_,
                     msgName:ZaMsg.LBL_zimbraPrefLocale,label:ZaMsg.LBL_zimbraPrefLocale,
                     labelLocation:_LEFT_,
                     labelCssStyle:"white-space:normal;",nowrap:false,labelWrap:true,
                     choices: ZaSettings.getLocaleChoices()
                }
            ]},
            {type:_ZAWIZ_TOP_GROUPER_, id:"cos_prefs_standard_client",label:ZaMsg.NAD_MailOptionsStandardClient,
            	visibilityChecks:[[ZATopGrouper_XFormItem.isGroupVisible,
					[
						ZaCos.A_zimbraMaxMailItemsPerPage,
						ZaCos.A_zimbraPrefMailItemsPerPage
					]]
				],
            	items :[
					{ref:ZaCos.A_zimbraMaxMailItemsPerPage, type:_OSELECT1_, msgName:ZaMsg.MSG_zimbraMaxMailItemsPerPage,
	                    label:ZaMsg.LBL_zimbraMaxMailItemsPerPage, labelLocation:_LEFT_, choices:[10,25,50,100,250,500,1000], editable:true,
	                    inputSize:4
	                },
	                {ref:ZaCos.A_zimbraPrefMailItemsPerPage, type:_OSELECT1_, msgName:ZaMsg.MSG_zimbraPrefMailItemsPerPage,
	                    label:ZaMsg.LBL_zimbraPrefMailItemsPerPage, labelLocation:_LEFT_
	                }
            	]
            },
            {type:_ZAWIZ_TOP_GROUPER_, id:"cos_prefs_mail_general",
                label:ZaMsg.NAD_MailOptions,
                visibilityChecks:[[ZATopGrouper_XFormItem.isGroupVisible,
					[
						ZaCos.A_zimbraPrefMessageViewHtmlPreferred,
						ZaCos.A_zimbraPrefDisplayExternalImages,
						ZaCos.A_zimbraPrefGroupMailBy,
						ZaCos.A_zimbraPrefMailDefaultCharset,
						ZaCos.A_zimbraPrefCalendarToasterEnabled,
                                                ZaCos.A_zimbraPrefItemsPerVirtualPage
					]]
				],
                items :[
                {ref:ZaCos.A_zimbraPrefMessageViewHtmlPreferred, type:_WIZ_CHECKBOX_,
                    msgName:ZaMsg.LBL_zimbraPrefMessageViewHtmlPreferred,
                    label:ZaMsg.LBL_zimbraPrefMessageViewHtmlPreferred,
                    trueValue:"TRUE", falseValue:"FALSE"
                },
                {ref:ZaCos.A_zimbraPrefDisplayExternalImages, type:_WIZ_CHECKBOX_,
                    msgName:ZaMsg.LBL_zimbraPrefDisplayExternalImages,
                    label:ZaMsg.LBL_zimbraPrefDisplayExternalImages,
                    trueValue:"TRUE", falseValue:"FALSE"
                },
                {ref:ZaCos.A_zimbraPrefGroupMailBy, type:_OSELECT1_, msgName:ZaMsg.LBL_zimbraPrefGroupMailBy,
                    label:ZaMsg.LBL_zimbraPrefGroupMailBy, labelLocation:_LEFT_
                },
                {ref:ZaCos.A_zimbraPrefMailDefaultCharset, type:_OSELECT1_,
                     msgName:ZaMsg.LBL_zimbraPrefMailDefaultCharset,label:ZaMsg.LBL_zimbraPrefMailDefaultCharset,
                     labelLocation:_LEFT_,
                     labelCssStyle:"white-space:normal;",nowrap:false,labelWrap:true
                },
		{ref:ZaCos.A_zimbraPrefMailToasterEnabled, type:_WIZ_CHECKBOX_,
                    msgName:ZaMsg.LBL_zimbraPrefMailToasterEnabled,
                    label:ZaMsg.LBL_zimbraPrefMailToasterEnabled,
                    trueValue:"TRUE", falseValue:"FALSE"
                },
		        {ref:ZaCos.A_zimbraPrefMessageIdDedupingEnabled, type:_WIZ_CHECKBOX_,
                    msgName:ZaMsg.LBL_zimbraPrefMessageIdDedupingEnabled,
                    label:ZaMsg.LBL_zimbraPrefMessageIdDedupingEnabled,
                    trueValue:"TRUE", falseValue:"FALSE"
                },
                {ref:ZaCos.A_zimbraPrefItemsPerVirtualPage,
                type:_TEXTFIELD_, colSizes:["200px","*"],colSpan:2,
                msgName:ZaMsg.LBL_zimbraPrefItemsPerVirtualPage,
                label:ZaMsg.LBL_zimbraPrefItemsPerVirtualPage,
                labelLocation:_LEFT_
                }

            ]},
            {type:_ZAWIZ_TOP_GROUPER_, id:"cos_prefs_mail_receiving",label:ZaMsg.NAD_MailOptionsReceiving,
            	visibilityChecks:[[ZATopGrouper_XFormItem.isGroupVisible,
					[
						ZaCos.A_zimbraPrefMailSoundsEnabled,
						ZaCos.A_zimbraPrefMailFlashIcon,
						ZaCos.A_zimbraPrefMailFlashTitle,
						ZaCos.A_zimbraPrefMailPollingInterval,
						ZaCos.A_zimbraMailMinPollingInterval,
						ZaCos.A_zimbraPrefOutOfOfficeCacheDuration,
						ZaCos.A_zimbraPrefMailSendReadReceipts
					]]
				],
            	items :[
                {ref:ZaCos.A_zimbraPrefMailSoundsEnabled,
                    type:_WIZ_CHECKBOX_,
                    msgName:ZaMsg.LBL_playSound,
                    label:ZaMsg.LBL_playSound,
                    trueValue:"TRUE", falseValue:"FALSE"
                },
                {ref:ZaCos.A_zimbraPrefMailFlashIcon,
                    type:_WIZ_CHECKBOX_,
                    msgName:ZaMsg.LBL_flashIcon,
                    label:ZaMsg.LBL_flashIcon,
                    trueValue:"TRUE", falseValue:"FALSE"
                },
                {ref:ZaCos.A_zimbraPrefMailFlashTitle,
                    type:_WIZ_CHECKBOX_,
                    msgName:ZaMsg.LBL_flashTitle,
                    label:ZaMsg.LBL_flashTitle,
                    trueValue:"TRUE", falseValue:"FALSE"
                },
                {ref:ZaCos.A_zimbraPrefMailPollingInterval, type:_OSELECT1_,
                    msgName:ZaMsg.MSG_zimbraPrefMailPollingInterval,
                    label:ZaMsg.LBL_zimbraPrefMailPollingInterval, labelLocation:_LEFT_,
                    onChange:ZaNewCosXWizard.validatePollingInterval,
                    labelCssStyle:"white-space:normal;",nowrap:false,labelWrap:true
                },
                {ref:ZaCos.A_zimbraMailMinPollingInterval, type:_LIFETIME_,
                    msgName:ZaMsg.MSG_zimbraMailMinPollingInterval,
                    label:ZaMsg.LBL_zimbraMailMinPollingInterval, labelLocation:_LEFT_,
                    onChange:ZaNewCosXWizard.validatePollingInterval,
                    labelCssStyle:"white-space:normal;",nowrap:false,labelWrap:true
                },
                {ref:ZaCos.A_zimbraPrefOutOfOfficeCacheDuration, type:_LIFETIME_,
                    msgName:ZaMsg.MSG_zimbraPrefOutOfOfficeCacheDuration,
                    label:ZaMsg.LBL_zimbraPrefOutOfOfficeCacheDuration, labelLocation:_LEFT_,
                    labelCssStyle:"white-space:normal;",nowrap:false,labelWrap:true
                },
				{ref:ZaCos.A_zimbraPrefMailSendReadReceipts, type:_OSELECT1_, label:ZaMsg.LBL_zimbraPrefMailSendReadReceipts,labelLocation:_LEFT_,nowrap:false,labelWrap:true}
            ]},
            {type:_ZAWIZ_TOP_GROUPER_, id:"cos_prefs_mail_sending",borderCssClass:"LowPaddedTopGrouperBorder",label:ZaMsg.NAD_MailOptionsSending,
            	visibilityChecks:[[ZATopGrouper_XFormItem.isGroupVisible,
					[
						ZaCos.A_zimbraPrefSaveToSent,
						ZaCos.A_zimbraAllowAnyFromAddress
					]]
				],
            	items :[
                {ref:ZaCos.A_zimbraPrefSaveToSent, type:_WIZ_CHECKBOX_, msgName:ZaMsg.LBL_zimbraPrefSaveToSent,label:ZaMsg.LBL_zimbraPrefSaveToSent,
                    trueValue:"TRUE", falseValue:"FALSE"
                },
                {ref:ZaCos.A_zimbraAllowAnyFromAddress, type:_WIZ_CHECKBOX_, msgName:ZaMsg.LBL_zimbraAllowAnyFromAddress,
                	label:ZaMsg.LBL_zimbraAllowAnyFromAddress,
                    trueValue:"TRUE", falseValue:"FALSE"
                }
            ]},
            {type:_ZAWIZ_TOP_GROUPER_, id:"cos_prefs_mail_sending",borderCssClass:"LowPaddedTopGrouperBorder",label:ZaMsg.NAD_MailOptionsComposing,
            	visibilityChecks:[[ZATopGrouper_XFormItem.isGroupVisible,
					[
						ZaCos.A_zimbraPrefComposeInNewWindow,
						ZaCos.A_zimbraPrefComposeFormat,
						ZaCos.A_zimbraPrefHtmlEditorDefaultFontFamily,
						ZaCos.A_zimbraPrefHtmlEditorDefaultFontSize,
						ZaCos.A_zimbraPrefHtmlEditorDefaultFontColor,
						ZaCos.A_zimbraPrefForwardReplyInOriginalFormat,
						ZaCos.A_zimbraPrefMandatorySpellCheckEnabled,
						ZaCos.A_zimbraMailSignatureMaxLength,
						ZaCos.A_zimbraPrefAutoSaveDraftInterval

					]]
				],
            	items :[
                {ref:ZaCos.A_zimbraPrefComposeInNewWindow, type:_WIZ_CHECKBOX_, msgName:ZaMsg.LBL_zimbraPrefComposeInNewWindow,
                    label:ZaMsg.LBL_zimbraPrefComposeInNewWindow, trueValue:"TRUE", falseValue:"FALSE"
                },
                {ref:ZaCos.A_zimbraPrefComposeFormat, type:_OSELECT1_, msgName:ZaMsg.LBL_zimbraPrefComposeFormat,label:ZaMsg.LBL_zimbraPrefComposeFormat, labelLocation:_LEFT_},
                {ref:ZaCos.A_zimbraPrefHtmlEditorDefaultFontFamily, type:_OSELECT1_, msgName:ZaMsg.LBL_zimbraPrefHtmlEditorDefaultFontFamily,
                    label:ZaMsg.LBL_zimbraPrefHtmlEditorDefaultFontFamily, labelLocation:_LEFT_
                },
                {ref:ZaCos.A_zimbraPrefHtmlEditorDefaultFontSize, type:_OSELECT1_, msgName:ZaMsg.LBL_zimbraPrefHtmlEditorDefaultFontSize,
                    label:ZaMsg.LBL_zimbraPrefHtmlEditorDefaultFontSize, labelLocation:_LEFT_
                },
                {ref:ZaCos.A_zimbraPrefHtmlEditorDefaultFontColor, type:_DWT_COLORPICKER_, msgName:ZaMsg.LBL_zimbraPrefHtmlEditorDefaultFontColor,
                    height: "25px",
                    label:ZaMsg.LBL_zimbraPrefHtmlEditorDefaultFontColor, labelLocation:_LEFT_
                },
                {ref:ZaCos.A_zimbraPrefForwardReplyInOriginalFormat, type:_WIZ_CHECKBOX_,
                    msgName:ZaMsg.LBL_zimbraPrefForwardReplyInOriginalFormat,
                    label:ZaMsg.LBL_zimbraPrefForwardReplyInOriginalFormat, trueValue:"TRUE", falseValue:"FALSE"
                },
                {ref:ZaCos.A_zimbraPrefMandatorySpellCheckEnabled, type:_WIZ_CHECKBOX_,
                	msgName:ZaMsg.LBL_zimbraPrefMandatorySpellCheckEnabled,
                	label:ZaMsg.LBL_zimbraPrefMandatorySpellCheckEnabled,
                    trueValue:"TRUE", falseValue:"FALSE"
                },
               /* {ref:ZaCos.A_zimbraPrefMailSignatureStyle, type:_WIZ_CHECKBOX_,
                    msgName:ZaMsg.LBL_zimbraPrefMailSignatureStyle,
                    label:ZaMsg.LBL_zimbraPrefMailSignatureStyle,
                    trueValue:"internet", falseValue:"outlook"
                },*/
                {ref:ZaCos.A_zimbraMailSignatureMaxLength, type:_TEXTFIELD_,
                    msgName:ZaMsg.LBL_zimbraMailSignatureMaxLength,
                    label:ZaMsg.LBL_zimbraMailSignatureMaxLength, labelLocation:_LEFT_,
                    cssClass:"admin_xform_number_input"},
                {ref:ZaCos.A_zimbraPrefAutoSaveDraftInterval, type:_LIFETIME_,
                    msgName:ZaMsg.MSG_zimbraPrefAutoSaveDraftInterval,
                    label:ZaMsg.LBL_zimbraPrefAutoSaveDraftInterval, labelLocation:_LEFT_,
                    onChange:ZaNewCosXWizard.validatePollingInterval,
                    labelCssStyle:"white-space:normal;",nowrap:false,labelWrap:true
                }
            ]},
            {type:_ZAWIZ_TOP_GROUPER_, id:"cos_prefs_contacts_general",
                label: ZaMsg.NAD_ContactsOptions,
                visibilityChecks:[[ZATopGrouper_XFormItem.isGroupVisible,
					[
						ZaCos.A_zimbraPrefAutoAddAddressEnabled,
						ZaCos.A_zimbraPrefGalAutoCompleteEnabled
					]]
				],
                items :[
                {ref:ZaCos.A_zimbraPrefAutoAddAddressEnabled, type:_WIZ_CHECKBOX_,
                    msgName:ZaMsg.LBL_zimbraPrefAutoAddAddressEnabled,
                    label:ZaMsg.LBL_zimbraPrefAutoAddAddressEnabled, trueValue:"TRUE", falseValue:"FALSE"
                },
                {ref:ZaCos.A_zimbraPrefGalAutoCompleteEnabled, type:_WIZ_CHECKBOX_,
                    msgName:ZaMsg.LBL_zimbraPrefGalAutoCompleteEnabled,
                    label:ZaMsg.LBL_zimbraPrefGalAutoCompleteEnabled, trueValue:"TRUE", falseValue:"FALSE"
                }
            ]},
            {type:_ZAWIZ_TOP_GROUPER_, id:"cos_prefs_calendar_general",
                label:ZaMsg.NAD_CalendarOptions,
                visibilityChecks:[[ZATopGrouper_XFormItem.isGroupVisible,
					[
						ZaCos.A_zimbraPrefTimeZoneId,
						ZaCos.A_zimbraPrefCalendarApptReminderWarningTime,
						ZaCos.A_zimbraPrefCalendarInitialView,
						ZaCos.A_zimbraPrefCalendarFirstDayOfWeek,
						ZaCos.A_zimbraPrefCalendarApptVisibility,
						ZaCos.A_zimbraPrefAppleIcalDelegationEnabled,
						ZaCos.A_zimbraPrefCalendarShowPastDueReminders,
						ZaCos.A_zimbraPrefCalendarToasterEnabled,
						ZaCos.A_zimbraPrefCalendarAllowCancelEmailToSelf,
						ZaCos.A_zimbraPrefCalendarAllowPublishMethodInvite,
						ZaCos.A_zimbraPrefCalendarAllowForwardedInvite,
						ZaCos.A_zimbraPrefCalendarReminderFlashTitle,
						ZaCos.A_zimbraPrefCalendarReminderSoundsEnabled,
						ZaCos.A_zimbraPrefCalendarSendInviteDeniedAutoReply,
						ZaCos.A_zimbraPrefCalendarAutoAddInvites,
						ZaCos.A_zimbraPrefCalendarNotifyDelegatedChanges,
						ZaCos.A_zimbraPrefCalendarAlwaysShowMiniCal,
						ZaCos.A_zimbraPrefCalendarUseQuickAdd,
						ZaCos.A_zimbraPrefUseTimeZoneListInCalendar
					]]
				],
                items :[
                {ref:ZaCos.A_zimbraPrefTimeZoneId, type:_OSELECT1_,
                     msgName:ZaMsg.MSG_zimbraPrefTimeZoneId,label:ZaMsg.LBL_zimbraPrefTimeZoneId,
                     labelLocation:_LEFT_,
                     labelCssStyle:"white-space:normal;",nowrap:false,labelWrap:true
                },
                {ref:ZaCos.A_zimbraPrefCalendarApptReminderWarningTime, type:_OSELECT1_,
                     msgName:ZaMsg.MSG_zimbraPrefCalendarApptReminderWarningTime,
                     label:ZaMsg.LBL_zimbraPrefCalendarApptReminderWarningTime,
                     labelLocation:_LEFT_,
                     labelCssStyle:"white-space:normal;",nowrap:false,labelWrap:true
                },
                {ref:ZaCos.A_zimbraPrefCalendarInitialView, type:_OSELECT1_, msgName:ZaMsg.MSG_zimbraPrefCalendarInitialView,
                    label:ZaMsg.LBL_zimbraPrefCalendarInitialView, labelLocation:_LEFT_
                },
				{ref:ZaCos.A_zimbraPrefCalendarFirstDayOfWeek, type:_OSELECT1_,
                    msgName:ZaMsg.LBL_zimbraPrefCalendarFirstDayOfWeek,
                    label:ZaMsg.LBL_zimbraPrefCalendarFirstDayOfWeek, labelLocation:_LEFT_,
                    labelCssStyle:"white-space:normal;",nowrap:false,labelWrap:true
                },
				{ref:ZaCos.A_zimbraPrefCalendarApptVisibility, type:_OSELECT1_,
                    msgName:ZaMsg.LBL_zimbraPrefCalendarApptVisibility,
                    label:ZaMsg.LBL_zimbraPrefCalendarApptVisibility, labelLocation:_LEFT_,
                    labelCssStyle:"white-space:normal;",nowrap:false,labelWrap:true
                },
                {ref:ZaCos.A_zimbraPrefAppleIcalDelegationEnabled,
                	type:_WIZ_CHECKBOX_,
                    msgName:ZaMsg.MSG_zimbraPrefAppleIcalDelegationEnabled,
                    label:ZaMsg.LBL_zimbraPrefAppleIcalDelegationEnabled,
                    trueValue:"TRUE", falseValue:"FALSE",
                    labelCssStyle:"white-space:normal;",
                    nowrap:false,labelWrap:true
                },
                {ref:ZaCos.A_zimbraPrefCalendarShowPastDueReminders,
                	type:_WIZ_CHECKBOX_,
                    msgName:ZaMsg.MSG_zimbraPrefCalendarShowPastDueReminders,
                    label:ZaMsg.LBL_zimbraPrefCalendarShowPastDueReminders,
                    trueValue:"TRUE", falseValue:"FALSE",
                    labelCssStyle:"white-space:normal;",
                    nowrap:false,labelWrap:true
                },
                {ref:ZaCos.A_zimbraPrefCalendarToasterEnabled,
                	type:_WIZ_CHECKBOX_,
                    msgName:ZaMsg.MSG_zimbraPrefCalendarToasterEnabled,
                    label:ZaMsg.LBL_zimbraPrefCalendarToasterEnabled,
                    trueValue:"TRUE", falseValue:"FALSE",
                    labelCssStyle:"white-space:normal;",
                    nowrap:false,labelWrap:true
                },
                {ref:ZaCos.A_zimbraPrefCalendarAllowCancelEmailToSelf,
                	type:_WIZ_CHECKBOX_,
                    msgName:ZaMsg.MSG_zimbraPrefCalendarAllowCancelEmailToSelf,
                    label:ZaMsg.LBL_zimbraPrefCalendarAllowCancelEmailToSelf,
                    trueValue:"TRUE", falseValue:"FALSE",
                    labelCssStyle:"white-space:normal;",
                    nowrap:false,labelWrap:true
                },
                {ref:ZaCos.A_zimbraPrefCalendarAllowPublishMethodInvite,
                	type:_WIZ_CHECKBOX_,
                    msgName:ZaMsg.MSG_zimbraPrefCalendarAllowPublishMethodInvite,
                    label:ZaMsg.LBL_zimbraPrefCalendarAllowPublishMethodInvite,
                    trueValue:"TRUE", falseValue:"FALSE",
                    labelCssStyle:"white-space:normal;",
                    nowrap:false,labelWrap:true
                },
                {ref:ZaCos.A_zimbraPrefCalendarAllowForwardedInvite,
                	type:_WIZ_CHECKBOX_,
                    msgName:ZaMsg.MSG_zimbraPrefCalendarAllowForwardedInvite,
                    label:ZaMsg.LBL_zimbraPrefCalendarAllowForwardedInvite,
                    trueValue:"TRUE", falseValue:"FALSE",
                    labelCssStyle:"white-space:normal;",
                    nowrap:false,labelWrap:true
                },
                {ref:ZaCos.A_zimbraPrefCalendarReminderFlashTitle,
                	type:_WIZ_CHECKBOX_,
                    msgName:ZaMsg.MSG_zimbraPrefCalendarReminderFlashTitle,
                    label:ZaMsg.LBL_zimbraPrefCalendarReminderFlashTitle,
                    trueValue:"TRUE", falseValue:"FALSE",
                    labelCssStyle:"white-space:normal;",
                    nowrap:false,labelWrap:true
                },
                {ref:ZaCos.A_zimbraPrefCalendarReminderSoundsEnabled,
                	type:_WIZ_CHECKBOX_,
                    msgName:ZaMsg.MSG_zimbraPrefCalendarReminderSoundsEnabled,
                    label:ZaMsg.LBL_zimbraPrefCalendarReminderSoundsEnabled,
                    trueValue:"TRUE", falseValue:"FALSE",
                    labelCssStyle:"white-space:normal;",
                    nowrap:false,labelWrap:true
                },
                {ref:ZaCos.A_zimbraPrefCalendarSendInviteDeniedAutoReply, type:_WIZ_CHECKBOX_,
                    msgName:ZaMsg.MSG_zimbraPrefCalendarSendInviteDeniedAutoReply,
                    label:ZaMsg.LBL_zimbraPrefCalendarSendInviteDeniedAutoReply,
                    trueValue:"TRUE", falseValue:"FALSE",
                    labelCssStyle:"white-space:normal;",
                    nowrap:false,labelWrap:true
                },
                {ref:ZaCos.A_zimbraPrefCalendarAutoAddInvites, type:_WIZ_CHECKBOX_,
                    msgName:ZaMsg.LBL_zimbraPrefCalendarAutoAddInvites,label:ZaMsg.LBL_zimbraPrefCalendarAutoAddInvites,
                    trueValue:"TRUE", falseValue:"FALSE",
                    labelCssStyle:"white-space:normal;",nowrap:false,labelWrap:true
                },
                {ref:ZaCos.A_zimbraPrefCalendarNotifyDelegatedChanges, type:_WIZ_CHECKBOX_,
                    msgName:ZaMsg.LBL_zimbraPrefCalendarNotifyDelegatedChanges,label:ZaMsg.LBL_zimbraPrefCalendarNotifyDelegatedChanges,
                    trueValue:"TRUE", falseValue:"FALSE",
                    labelCssStyle:"white-space:normal;",nowrap:false,labelWrap:true
                },
                {ref:ZaCos.A_zimbraPrefCalendarAlwaysShowMiniCal, type:_WIZ_CHECKBOX_,
                    msgName:ZaMsg.LBL_zimbraPrefCalendarAlwaysShowMiniCal,label:ZaMsg.LBL_zimbraPrefCalendarAlwaysShowMiniCal,
                    trueValue:"TRUE", falseValue:"FALSE",
                    labelCssStyle:"white-space:normal;",nowrap:false,labelWrap:true
                },
                {ref:ZaCos.A_zimbraPrefCalendarUseQuickAdd, type:_WIZ_CHECKBOX_, msgName:ZaMsg.NAD_useQuickAdd,
                    label:ZaMsg.LBL_zimbraPrefCalendarUseQuickAdd, trueValue:"TRUE", falseValue:"FALSE"
                },
                {ref:ZaCos.A_zimbraPrefUseTimeZoneListInCalendar, type:_WIZ_CHECKBOX_,
                    msgName:ZaMsg.LBL_zimbraPrefUseTimeZoneListInCalendar,
                    label:ZaMsg.LBL_zimbraPrefUseTimeZoneListInCalendar, trueValue:"TRUE", falseValue:"FALSE"
                }
            ]}
        ];

        case3.items = case3Items;
        cases.push(case3);
    }

    if(_tab4) {
        var case4 = {type:_CASE_, numCols:1, caseKey:_tab4};
        var case4Items = [
            {type:_GROUP_,numCols:2,colSizes:["275","auto"],items:[							{
                ref:ZaCos.A_zimbraPrefSkin, type:_OSELECT1_,
                msgName:ZaMsg.LBL_zimbraPrefSkin,label:ZaMsg.LBL_zimbraPrefSkin, labelLocation:_LEFT_,choices:ZaApp.getInstance().getInstalledSkins(),
                visibilityChecks:[ZaNewCosXWizard.gotSkins]
            }]},
            {type:_GROUP_,
                items:[
                    {type:_OUTPUT_,ref:ZaCos.A_zimbraPrefSkin,label:ZaMsg.LBL_zimbraPrefSkin, labelLocation:_LEFT_,
                        visibilityChecks:[ZaNewCosXWizard.gotNoSkins]
                    }
                ]
            },
            {type:_GROUP_, numCols:1,colSizes:["auto"],
                items: [
                    {type:_ZASELECT_RADIO_,
                        selectRef:ZaCos.A_zimbraAvailableSkin,
                        ref:ZaCos.A_zimbraAvailableSkin,
                        choices:ZaNewCosXWizard.themeChoices,
                        visibilityChecks:[Case_XFormItem.prototype.isCurrentTab, ZaNewCosXWizard.gotSkins],
                        visibilityChangeEventSources:[ZaModel.currentStep],
                        caseKey:_tab4, caseVarRef:ZaModel.currentStep,
                        radioBoxLabel1:ZaMsg.COS_DontLimitThemes,
                        radioBoxLabel2:ZaMsg.COS_LimitThemesTo
                    },
                    {type:_DWT_ALERT_,style: DwtAlert.WARNING, iconVisible:true,
                        visibilityChecks:[ZaNewCosXWizard.gotNoSkins],
                        value:ZaMsg.ERROR_CANNOT_FIND_SKINS_FOR_COS
                    },
                    {type:_DWT_ALERT_,style: DwtAlert.WARNING, iconVisible:true,
                        visibilityChecks:[ZaCosXFormView.gotNoSkins, [function(){return !ZaZimbraAdmin.isGlobalAdmin()}]],
                        value:ZaMsg.ERROR_CANNOT_FIND_SKINS_FOR_COS_OR_NO_PERM
                    }
                ]
            }
        ];

        case4.items=case4Items;
        cases.push(case4);
    }

    if(_tab5) {
        var case5 = {type:_CASE_, caseKey:_tab5};
        var case5Items = [
            {type:_GROUP_, numCols:1,colSizes:["auto"],
                items: [
                    {type: _OUTPUT_, value: ZaMsg.COS_LimitZimletsTo,cssStyle:"margin-left:0;" },
                    {type:_ZA_ZIMLET_SELECT_COMBO_,
                        selectRef:ZaCos.A_zimbraZimletAvailableZimlets,
                        ref:ZaCos.A_zimbraZimletAvailableZimlets,
                        choices:ZaNewCosXWizard.zimletChoices,
                       visibilityChecks:[Case_XFormItem.prototype.isCurrentTab],
                        visibilityChangeEventSources:[ZaModel.currentStep],
                        caseKey:_tab5, caseVarRef:ZaModel.currentStep,
                        selectLabel:"",selectLabelLocation:_NONE_
                    }
                ]
            }
        ];

        case5.items = case5Items;
        cases.push(case5);
    }

    if(_tab6) {
        var case6 = {type:_CASE_, numCols:1,caseKey:_tab6};
        var case6Items = [
            {type:_GROUP_, numCols:1,colSizes:["auto"],
                items: [
                    {type:_ZASELECT_RADIO_,
                        selectRef:ZaCos.A_zimbraMailHostPool,
                        ref:ZaCos.A_zimbraMailHostPool,
                        choices:ZaApp.getInstance().getServerIdListChoices(),
                        visibilityChecks:[Case_XFormItem.prototype.isCurrentTab],
                        visibilityChangeEventSources:[ZaModel.currentStep],
                        caseKey:_tab6, caseVarRef:ZaModel.currentStep,
                        radioBoxLabel1:ZaMsg.ServerPool_Donotlimit,
                        radioBoxLabel2:ZaMsg.COS_LimitServersTo
                    }
                ]
            }
        ];

        case6.items = case6Items;
        cases.push(case6);
    }

    if(_tab7) {
        var case7 = {type:_CASE_, numCols:1, colSizes:["auto"], caseKey:_tab7,
             width:"100%", cellpadding:0,
            id:"cos_form_advanced_tab"};
        var case7Items = [
            { type: _DWT_ALERT_,
              style: DwtAlert.WARNING,
	      iconVisible: false,
	      content: ZaMsg.NAD_ZERO_UNLIMETED
	    },      
            {type:_ZAWIZ_TOP_GROUPER_, id:"cos_attachment_settings",
                label:ZaMsg.NAD_AttachmentsGrouper,visibilityChecks:[[ZATopGrouper_XFormItem.isGroupVisible,
                                                          			[ZaCos.A_zimbraAttachmentsBlocked]]],
                items :[
                    {ref:ZaCos.A_zimbraAttachmentsBlocked, type:_WIZ_CHECKBOX_,  msgName:ZaMsg.NAD_RemoveAllAttachments,label:ZaMsg.NAD_RemoveAllAttachments, labelLocation:_LEFT_, trueValue:"TRUE", falseValue:"FALSE",labelCssClass:"xform_label",  align:_LEFT_}
                ]
            },
            {type:_ZAWIZ_TOP_GROUPER_, id:"cos_quota_settings",
                label:ZaMsg.NAD_QuotaGrouper,
                visibilityChecks:[[ZATopGrouper_XFormItem.isGroupVisible,
					[
                        ZaCos.A_zimbraMailForwardingAddressMaxLength,
                        ZaCos.A_zimbraMailForwardingAddressMaxNumAddrs,
                        ZaCos.A_zimbraMailQuota,
                        ZaCos.A_zimbraContactMaxNumEntries,
                        ZaCos.A_zimbraQuotaWarnPercent,
						ZaCos.A_zimbraQuotaWarnInterval,
                        ZaCos.A_zimbraQuotaWarnMessage
					]]
				],
                items: [
                    {ref:ZaCos.A_zimbraMailForwardingAddressMaxLength, type:_TEXTFIELD_, 
                    	msgName:ZaMsg.MSG_zimbraMailForwardingAddressMaxLength,
                    	label:ZaMsg.LBL_zimbraMailForwardingAddressMaxLength, 
                    	labelLocation:_LEFT_, 
                    	cssClass:"admin_xform_number_input"
                    },
                    {ref:ZaCos.A_zimbraMailForwardingAddressMaxNumAddrs, type:_TEXTFIELD_, 
                    	msgName:ZaMsg.MSG_zimbraMailForwardingAddressMaxNumAddrs,
                    	label:ZaMsg.LBL_zimbraMailForwardingAddressMaxNumAddrs, 
                    	labelLocation:_LEFT_, 
                    	cssClass:"admin_xform_number_input"
                    },                
                    {ref:ZaCos.A_zimbraMailQuota, type:_TEXTFIELD_, 
                    	msgName:ZaMsg.MSG_zimbraMailQuota,
                    	label:ZaMsg.LBL_zimbraMailQuota, labelLocation:_LEFT_, cssClass:"admin_xform_number_input"
                    },
                    {ref:ZaCos.A_zimbraContactMaxNumEntries, type:_TEXTFIELD_, 
                    	msgName:ZaMsg.MSG_zimbraContactMaxNumEntries,
                    	label:ZaMsg.LBL_zimbraContactMaxNumEntries, 
                    	labelLocation:_LEFT_, 
                    	cssClass:"admin_xform_number_input"
                    },
                    {ref:ZaCos.A_zimbraQuotaWarnPercent, type:_INPUT_, msgName:ZaMsg.MSG_zimbraQuotaWarnPercent,
                    	label:ZaMsg.LBL_zimbraQuotaWarnPercent, labelLocation:_LEFT_, cssClass:"admin_xform_number_input"
                    },
                    {ref:ZaCos.A_zimbraQuotaWarnInterval, type:_LIFETIME_, msgName:ZaMsg.MSG_zimbraQuotaWarnInterval,
                    	label:ZaMsg.LBL_zimbraQuotaWarnInterval, labelLocation:_LEFT_
                    },
                    {ref:ZaCos.A_zimbraQuotaWarnMessage, type:_TEXTAREA_, msgName:ZaMsg.MSG_zimbraQuotaWarnMessage,
                    	label:ZaMsg.LBL_zimbraQuotaWarnMessage, labelLocation:_LEFT_, labelCssStyle:"vertical-align:top;",width: "30em"
                    }
                ]
            },

            {type:_ZAWIZ_TOP_GROUPER_, id:"cos_datasourcepolling_settings",
                visibilityChecks:[[ZATopGrouper_XFormItem.isGroupVisible,
					[
                        ZaCos.A_zimbraDataSourceMinPollingInterval,
                        ZaCos.A_zimbraDataSourcePop3PollingInterval,
                        ZaCos.A_zimbraDataSourceImapPollingInterval,
                        ZaCos.A_zimbraDataSourceCalendarPollingInterval,
                        ZaCos.A_zimbraDataSourceRssPollingInterval,
						ZaCos.A_zimbraDataSourceCaldavPollingInterval
					]]
				],
                label:ZaMsg.NAD_DataSourcePolling,
                items: [
                    {ref:ZaCos.A_zimbraDataSourceMinPollingInterval, type:_LIFETIME_,
                        msgName:ZaMsg.MSG_zimbraDataSourceMinPollingInterval,
                        label:ZaMsg.LBL_zimbraDataSourceMinPollingInterval, labelLocation:_LEFT_,
                        onChange:ZaNewCosXWizard.validatePollingInterval,
                        labelCssStyle:"white-space:normal;",nowrap:false,labelWrap:true
                    },
                    {ref:ZaCos.A_zimbraDataSourcePop3PollingInterval, type:_LIFETIME_,
                        msgName:ZaMsg.MSG_zimbraDataSourcePop3PollingInterval,
                        label:ZaMsg.LBL_zimbraDataSourcePop3PollingInterval, labelLocation:_LEFT_,
                        onChange:ZaNewCosXWizard.validatePollingInterval,
                        labelCssStyle:"white-space:normal;",nowrap:false,labelWrap:true
                    },
                    {ref:ZaCos.A_zimbraDataSourceImapPollingInterval, type:_LIFETIME_,
                        msgName:ZaMsg.MSG_zimbraDataSourceImapPollingInterval,
                        label:ZaMsg.LBL_zimbraDataSourceImapPollingInterval, labelLocation:_LEFT_,
                        onChange:ZaNewCosXWizard.validatePollingInterval,
                        labelCssStyle:"white-space:normal;",nowrap:false,labelWrap:true
                    },
                    {ref:ZaCos.A_zimbraDataSourceCalendarPollingInterval, type:_LIFETIME_,
                        msgName:ZaMsg.MSG_zimbraDataSourceCalendarPollingInterval,
                        label:ZaMsg.LBL_zimbraDataSourceCalendarPollingInterval, labelLocation:_LEFT_,
                        onChange:ZaNewCosXWizard.validatePollingInterval,
                        labelCssStyle:"white-space:normal;",nowrap:false,labelWrap:true
                    },
                    {ref:ZaCos.A_zimbraDataSourceRssPollingInterval, type:_LIFETIME_,
                        msgName:ZaMsg.MSG_zimbraDataSourceRssPollingInterval,
                        label:ZaMsg.LBL_zimbraDataSourceRssPollingInterval, labelLocation:_LEFT_,
                        onChange:ZaNewCosXWizard.validatePollingInterval,
                        labelCssStyle:"white-space:normal;",nowrap:false,labelWrap:true
                    },
                    {ref:ZaCos.A_zimbraDataSourceCaldavPollingInterval, type:_LIFETIME_,
                        msgName:ZaMsg.MSG_zimbraDataSourceCaldavPollingInterval,
                        label:ZaMsg.LBL_zimbraDataSourceCaldavPollingInterval, labelLocation:_LEFT_,
                        onChange:ZaNewCosXWizard.validatePollingInterval,
                        labelCssStyle:"white-space:normal;",nowrap:false,labelWrap:true
                    }
                ]
            },
            
            {type:_ZAWIZ_TOP_GROUPER_, id:"cos_proxyalloweddomain_settings",
                label: ZaMsg.NAD_ProxyAllowedDomains,
                visibilityChecks:[[ZATopGrouper_XFormItem.isGroupVisible,
					[
						ZaCos.A_zimbraProxyAllowedDomains
					]]
				],
             	items:[
		            {ref: ZaCos.A_zimbraProxyAllowedDomains,
			        msgName:ZaMsg.MSG_zimbraProxyAllowedDomains,
    			    label:ZaMsg.LBL_zimbraProxyAllowedDomains,
                    labelLocation:_LEFT_,
   			        labelCssStyle:"vertical-align:top;",
    			    type:_REPEAT_,
    			    align:_LEFT_,
    			    repeatInstance:"",
                    addButtonLabel:ZaMsg.NAD_ProxyAddAllowedDomain ,
                    removeButtonLabel: ZaMsg.NAD_ProxyRemoveAllowedDomain,
    			    showAddButton:true,
    			    showRemoveButton:true,
    			    showAddOnNextRow:true,
        		    items: [
                		{ref:".", type:_TEXTFIELD_,
                         enableDisableChecks:[] ,
                         visibilityChecks:[],
                         width: "15em"}
                        ]
                    }
             	]
            },

            {type:_ZAWIZ_TOP_GROUPER_,id:"cos_password_settings",
                label:ZaMsg.NAD_PasswordGrouper,
                items: [ 
                    { type: _DWT_ALERT_,
                      containerCssStyle: "padding-bottom:0;",
                      style: DwtAlert.WARNING,
                      iconVisible:  false,
                      content: ((ZaNewCosXWizard.isAllAuthfromInternal())?ZaMsg.Alert_InternalPassword:ZaMsg.Alert_ExternalPassword)
                    },
                    {ref:ZaCos.A_zimbraPasswordLocked, type:_WIZ_CHECKBOX_,
                        msgName:ZaMsg.NAD_PwdLocked,
                        label:ZaMsg.NAD_PwdLocked,
                        trueValue:"TRUE", falseValue:"FALSE",
			visibilityChecks:[],enableDisableChecks:[[ZaNewCosXWizard.isAllAuthfromInternal]]
                    },
                    {ref:ZaCos.A_zimbraMinPwdLength, 
			type:_TEXTFIELD_, msgName:ZaMsg.MSG_zimbraMinPwdLength,
			label:ZaMsg.LBL_zimbraMinPwdLength, labelLocation:_LEFT_, cssClass:"admin_xform_number_input",
			visibilityChecks:[ZaItem.hasWritePermission],enableDisableChecks:[[ZaNewCosXWizard.isAllAuthfromInternal]]
		    },
                    {ref:ZaCos.A_zimbraMaxPwdLength, 
			type:_TEXTFIELD_, msgName:ZaMsg.MSG_zimbraMaxPwdLength,
			label:ZaMsg.LBL_zimbraMaxPwdLength, labelLocation:_LEFT_, cssClass:"admin_xform_number_input",
			visibilityChecks:[ZaItem.hasWritePermission],enableDisableChecks:[[ZaNewCosXWizard.isAllAuthfromInternal]]
		    },

                    {ref:ZaCos.A_zimbraPasswordMinUpperCaseChars, 
			type:_TEXTFIELD_, msgName:ZaMsg.MSG_zimbraPasswordMinUpperCaseChars,
			label:ZaMsg.LBL_zimbraPasswordMinUpperCaseChars, labelLocation:_LEFT_, cssClass:"admin_xform_number_input",
			visibilityChecks:[ZaItem.hasWritePermission],enableDisableChecks:[[ZaNewCosXWizard.isAllAuthfromInternal]]
		    },
                    {ref:ZaCos.A_zimbraPasswordMinLowerCaseChars, 
			type:_TEXTFIELD_, msgName:ZaMsg.MSG_zimbraPasswordMinLowerCaseChars,
			label:ZaMsg.LBL_zimbraPasswordMinLowerCaseChars, labelLocation:_LEFT_, cssClass:"admin_xform_number_input",
			visibilityChecks:[ZaItem.hasWritePermission],enableDisableChecks:[[ZaNewCosXWizard.isAllAuthfromInternal]]
		    },
                    {ref:ZaCos.A_zimbraPasswordMinPunctuationChars, 
			type:_TEXTFIELD_, msgName:ZaMsg.MSG_zimbraPasswordMinPunctuationChars,
			label:ZaMsg.LBL_zimbraPasswordMinPunctuationChars, labelLocation:_LEFT_, cssClass:"admin_xform_number_input",
			visibilityChecks:[ZaItem.hasWritePermission],enableDisableChecks:[[ZaNewCosXWizard.isAllAuthfromInternal]]
	 	    },
                    {ref:ZaCos.A_zimbraPasswordMinNumericChars, 
			type:_TEXTFIELD_, msgName:ZaMsg.MSG_zimbraPasswordMinNumericChars,
			label:ZaMsg.LBL_zimbraPasswordMinNumericChars, labelLocation:_LEFT_, cssClass:"admin_xform_number_input",
			visibilityChecks:[ZaItem.hasWritePermission],enableDisableChecks:[[ZaNewCosXWizard.isAllAuthfromInternal]]
		    },
                    {ref:ZaCos.A_zimbraPasswordMinDigitsOrPuncs, 
			type:_TEXTFIELD_, msgName:ZaMsg.MSG_zimbraPasswordMinDigitsOrPuncs,
			label:ZaMsg.LBL_zimbraPasswordMinDigitsOrPuncs, labelLocation:_LEFT_, cssClass:"admin_xform_number_input",
			visibilityChecks:[ZaItem.hasWritePermission],enableDisableChecks:[[ZaCosXFormView.isAllAuthfromInternal]]
		    },

                    {ref:ZaCos.A_zimbraMinPwdAge, 
			type:_TEXTFIELD_, msgName:ZaMsg.MSG_passMinAge,
			label:ZaMsg.LBL_passMinAge, labelLocation:_LEFT_, cssClass:"admin_xform_number_input",
			visibilityChecks:[],enableDisableChecks:[[ZaNewCosXWizard.isAllAuthfromInternal]]
		    },
                    {ref:ZaCos.A_zimbraMaxPwdAge, 
			type:_TEXTFIELD_, msgName:ZaMsg.MSG_passMaxAge,
			label:ZaMsg.LBL_passMaxAge, labelLocation:_LEFT_, cssClass:"admin_xform_number_input",
			visibilityChecks:[],enableDisableChecks:[[ZaNewCosXWizard.isAllAuthfromInternal]]
		    },
                    {ref:ZaCos.A_zimbraEnforcePwdHistory, 
			type:_TEXTFIELD_, msgName:ZaMsg.MSG_zimbraEnforcePwdHistory,
			label:ZaMsg.LBL_zimbraEnforcePwdHistory, labelLocation:_LEFT_, cssClass:"admin_xform_number_input",
			visibilityChecks:[],enableDisableChecks:[[ZaNewCosXWizard.isAllAuthfromInternal]]
		    }
                ]
            },
            {type:_ZAWIZ_TOP_GROUPER_, id:"cos_password_lockout_settings",
                label:ZaMsg.NAD_FailedLoginGrouper,
                items :[

                    {ref:ZaCos.A_zimbraPasswordLockoutEnabled, type:_WIZ_CHECKBOX_,
                        msgName:ZaMsg.LBL_zimbraPasswordLockoutEnabled,
                        label:ZaMsg.LBL_zimbraPasswordLockoutEnabled,
                        trueValue:"TRUE", falseValue:"FALSE"
                    },
                    {ref:ZaCos.A_zimbraPasswordLockoutMaxFailures, type:_TEXTFIELD_,
                        enableDisableChecks: [ZaNewCosXWizard.isPasswordLockoutEnabled],
                        enableDisableChangeEventSources:[ZaCos.A_zimbraPasswordLockoutEnabled],
                        label:ZaMsg.LBL_zimbraPasswordLockoutMaxFailures,
                        subLabel:ZaMsg.TTP_zimbraPasswordLockoutMaxFailuresSub,
                        msgName:ZaMsg.MSG_zimbraPasswordLockoutMaxFailures,
                        labelLocation:_LEFT_,
                        cssClass:"admin_xform_number_input"
                    },
                    {ref:ZaCos.A_zimbraPasswordLockoutDuration, type:_LIFETIME_,
                        enableDisableChecks: [ZaNewCosXWizard.isPasswordLockoutEnabled],
                        enableDisableChangeEventSources:[ZaCos.A_zimbraPasswordLockoutEnabled],
                        label:ZaMsg.LBL_zimbraPasswordLockoutDuration,
                        subLabel:ZaMsg.TTP_zimbraPasswordLockoutDurationSub,
                        msgName:ZaMsg.MSG_zimbraPasswordLockoutDuration,
                        labelLocation:_LEFT_,
                        textFieldCssClass:"admin_xform_number_input"
                    },
                    {ref:ZaCos.A_zimbraPasswordLockoutFailureLifetime, type:_LIFETIME_,
                        enableDisableChecks: [ZaNewCosXWizard.isPasswordLockoutEnabled],
                        enableDisableChangeEventSources:[ZaCos.A_zimbraPasswordLockoutEnabled],
                        label:ZaMsg.LBL_zimbraPasswordLockoutFailureLifetime,
                        subLabel:ZaMsg.TTP_zimbraPasswordLockoutFailureLifetimeSub,
                        msgName:ZaMsg.MSG_zimbraPasswordLockoutFailureLifetime,
                        labelLocation:_LEFT_,
                        textFieldCssClass:"admin_xform_number_input",
                        labelCssStyle:"white-space:normal;",
                        nowrap:false,labelWrap:true
                    }
                ]
            },
            {type:_ZAWIZ_TOP_GROUPER_,
                label:ZaMsg.NAD_TimeoutGrouper,
                items: [
                    {ref:ZaCos.A_zimbraAdminAuthTokenLifetime, type:_LIFETIME_, msgName:ZaMsg.MSG_zimbraAdminAuthTokenLifetime,label:ZaMsg.LBL_zimbraAdminAuthTokenLifetime,labelLocation:_LEFT_},
                    {ref:ZaCos.A_zimbraAuthTokenLifetime, type:_LIFETIME_, msgName:ZaMsg.MSG_zimbraAuthTokenLifetime,label:ZaMsg.LBL_zimbraAuthTokenLifetime,labelLocation:_LEFT_},
                    {ref:ZaCos.A_zimbraMailIdleSessionTimeout, type:_LIFETIME_, msgName:ZaMsg.MSG_zimbraMailIdleSessionTimeout,label:ZaMsg.LBL_zimbraMailIdleSessionTimeout,labelLocation:_LEFT_}
                ]
            },
            {type:_ZAWIZ_TOP_GROUPER_,
                label:ZaMsg.NAD_MailRetentionGrouper,
                items: [
                    { type: _DWT_ALERT_,
                      containerCssStyle: "padding-bottom:0;",
                      style: DwtAlert.WARNING,
                      iconVisible: false,
                      content: ZaMsg.Alert_MailRetention
                    },
                    {ref:ZaCos.A_zimbraMailMessageLifetime, type:_LIFETIME2_, msgName:ZaMsg.MSG_zimbraMailMessageLifetime,label:ZaMsg.LBL_zimbraMailMessageLifetime,labelLocation:_LEFT_},
                    {ref:ZaCos.A_zimbraMailTrashLifetime, type:_LIFETIME1_, msgName:ZaMsg.MSG_zimbraMailTrashLifetime,label:ZaMsg.LBL_zimbraMailTrashLifetime, labelLocation:_LEFT_},
                    {ref:ZaCos.A_zimbraMailSpamLifetime, type:_LIFETIME1_, msgName:ZaMsg.MSG_zimbraMailSpamLifetime,label:ZaMsg.LBL_zimbraMailSpamLifetime, labelLocation:_LEFT_}
                ]
            },
            {type:_ZAWIZ_TOP_GROUPER_,
                label:ZaMsg.NAD_InteropGrouper,visibilityChecks:[[ZATopGrouper_XFormItem.isGroupVisible,
                                                      			[ZaCos.A_zimbraFreebusyExchangeUserOrg]]],
                items: [
                    {ref:ZaCos.A_zimbraFreebusyExchangeUserOrg, type:_TEXTFIELD_,
                        msgName:ZaMsg.LBL_zimbraFreebusyExchangeUserOrg, width: "250px",
                        label:ZaMsg.LBL_zimbraFreebusyExchangeUserOrg,labelLocation:_LEFT_
                    }
                ]
            } ,
            {type: _SPACER_ , height: "10px" }  //add some spaces at the bottom of the page

        ];

        case7.items = case7Items;
        cases.push(case7);
    }
    
    xFormObject.tableCssStyle = "width:100%;overflow:auto;";

    this._lastStep = this.stepChoices.length;

	xFormObject.items = [
		{type:_OUTPUT_, colSpan:2, align:_CENTER_, valign:_TOP_, ref:ZaModel.currentStep, choices:this.stepChoices,valueChangeEventSources:[ZaModel.currentStep]},
	    {type:_SEPARATOR_, align:_CENTER_, valign:_TOP_},
		{type:_SPACER_,  align:_CENTER_, valign:_TOP_},
		{type:_SWITCH_, width:635, align:_LEFT_, valign:_TOP_, items:cases}


	];		
};
ZaXDialog.XFormModifiers["ZaNewCosXWizard"].push(ZaNewCosXWizard.myXFormModifier);

ZaNewCosXWizard.prototype.getTabChoices =
function() {
    return this.stepChoices;
}

ZaNewCosXWizard.isAllAuthfromInternal =
function() {
	var isAll = true;  // is all external?
	var domainList = ZaNewCosXWizard._domainList;
	if(!domainList) return isAll;
	for(var i = 0; i < domainList.length && isAll; i ++) {
		var dom = domainList[i];
		if(dom.attrs[ZaDomain.A_AuthMech] == ZaDomain.AuthMech_zimbra)
			isAll = false; 
	}
        return !isAll;
}

ZaNewCosXWizard.validatePollingInterval =
function (value, event, form) {
	DBG.println(AjxDebug.DBG3, "The polling interval = " + value);
	var instance = form.getInstance ();
	this.setInstanceValue(value);
	var prefPollingInterval = instance.attrs[ZaCos.A_zimbraPrefMailPollingInterval] ;
	var minPollingInterval = instance.attrs[ZaCos.A_zimbraMailMinPollingInterval] ;
	var prefPollingIntervalItem = form.getItemsById (ZaCos.A_zimbraPrefMailPollingInterval)[0];
	try {
		if (ZaUtil.getLifeTimeInSeconds(prefPollingInterval) < ZaUtil.getLifeTimeInSeconds(minPollingInterval)){
			prefPollingIntervalItem.setError (ZaMsg.tt_mailPollingIntervalError + minPollingInterval) ;
			form.parent.setDirty(false);	
		}else{
			prefPollingIntervalItem.clearError();	
			form.parent.setDirty(true);	
		}
	}catch (e){
		prefPollingIntervalItem.setError (e.message);
		form.parent.setDirty(false);
	}
}
