/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2005, 2006, 2007, 2008, 2009, 2010, 2011, 2012 VMware, Inc.
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
* @class GlobalConfigXFormView
* @contructor
* @param parent
* @param app
* @author Greg Solovyev
**/
GlobalConfigXFormView = function(parent, entry) {
	ZaTabView.call(this, {
		parent:parent, 
		iKeyName:"GlobalConfigXFormView",
		contextId:ZaId.TAB_GSET_EDIT
	});
	this.TAB_INDEX = 0;	
	this.initForm(ZaGlobalConfig.myXModel,this.getMyXForm(entry), null);
}

GlobalConfigXFormView.prototype = new ZaTabView();
GlobalConfigXFormView.prototype.constructor = GlobalConfigXFormView;
ZaTabView.XFormModifiers["GlobalConfigXFormView"] = new Array();
ZaTabView.XFormSetObjectMethods["GlobalConfigXFormView"] = new Array();

GlobalConfigXFormView.prototype.getTitle =
function () {
	return ZaMsg.GlobalConfig_view_title;
}

GlobalConfigXFormView.onRepeatRemove =
function (index, form) {
	var list = this.getInstanceValue();
	if (list == null || typeof(list) == "string" || index >= list.length || index<0) return;
	list.splice(index, 1);
	form.parent.setDirty(true);
}

GlobalConfigXFormView.prototype.getTabIcon =
function () {
	return "GlobalSettings";
}

GlobalConfigXFormView.prototype.getTabTitle =
function () {
	return this.getTitle();
}

GlobalConfigXFormView.prototype.getTabToolTip =
function () {
	return this.getTitle ();
}


GlobalConfigXFormView.blockedExtSelectionListener = function () {
	var arr = this.widget.getSelection();
	if(arr && arr.length) {
		arr.sort();
		this.getModel().setInstanceValue(this.getInstance(), ZaGlobalConfig.A2_blocked_extension_selection, arr);
	} else {
		this.getModel().setInstanceValue(this.getInstance(), ZaGlobalConfig.A2_blocked_extension_selection, null);
	}
}

GlobalConfigXFormView.commonExtSelectionListener = function () {
	var arr = this.widget.getSelection();
	if(arr && arr.length) {
		arr.sort();
		this.getModel().setInstanceValue(this.getInstance(), ZaGlobalConfig.A2_common_extension_selection, arr);
	} else {
		this.getModel().setInstanceValue(this.getInstance(), ZaGlobalConfig.A2_common_extension_selection, null);
	}
}

GlobalConfigXFormView.shouldEnableRemoveAllButton = function () {
	return (!AjxUtil.isEmpty(this.getInstanceValue(ZaGlobalConfig.A_zimbraMtaBlockedExtension)));
}

GlobalConfigXFormView.shouldEnableRemoveButton = function () {
	return (!AjxUtil.isEmpty(this.getInstanceValue(ZaGlobalConfig.A2_blocked_extension_selection)));
}

GlobalConfigXFormView.shouldEnableAddButton = function () {
	return (!AjxUtil.isEmpty(this.getInstanceValue(ZaGlobalConfig.A2_common_extension_selection)));
}

GlobalConfigXFormView.shouldEnableAddAllButton = function () {
	return (!AjxUtil.isEmpty(this.getInstanceValue(ZaGlobalConfig.A_zimbraMtaCommonBlockedExtension)));
}

GlobalConfigXFormView.removeExt = function () {
	var blockedExtArray = this.getInstanceValue(ZaGlobalConfig.A_zimbraMtaBlockedExtension);
	var selectedExtArray = this.getInstanceValue(ZaGlobalConfig.A2_blocked_extension_selection);
	var newBlockedExtArray = AjxUtil.arraySubstract(blockedExtArray,selectedExtArray);
	this.setInstanceValue(newBlockedExtArray,ZaGlobalConfig.A_zimbraMtaBlockedExtension);
	this.getForm().parent.setDirty(true);	
}

GlobalConfigXFormView.removeAllExt = function () {
	this.setInstanceValue([],ZaGlobalConfig.A_zimbraMtaBlockedExtension);
	this.setInstanceValue([],ZaGlobalConfig.A2_blocked_extension_selection);
	this.getForm().parent.setDirty(true);	
}

GlobalConfigXFormView.addCommonExt = function () {
	var commonExtArr = this.getInstanceValue(ZaGlobalConfig.A_zimbraMtaBlockedExtension);
	var newExtArr = this.getInstanceValue(ZaGlobalConfig.A2_common_extension_selection);
	commonExtArr = AjxUtil.isEmpty(commonExtArr) ? [] : commonExtArr;
	newExtArr = AjxUtil.isEmpty(newExtArr) ? [] : newExtArr;	
	this.setInstanceValue(AjxUtil.mergeArrays(commonExtArr,newExtArr),ZaGlobalConfig.A_zimbraMtaBlockedExtension);	
	this.getForm().parent.setDirty(true);	
}

GlobalConfigXFormView.addAllCommonExt = function () {
	var commonExtArr = this.getInstanceValue(ZaGlobalConfig.A_zimbraMtaBlockedExtension);
	var newExtArr = this.getInstanceValue(ZaGlobalConfig.A_zimbraMtaCommonBlockedExtension);
	commonExtArr = AjxUtil.isEmpty(commonExtArr) ? [] : commonExtArr;
	newExtArr = AjxUtil.isEmpty(newExtArr) ? [] : newExtArr;
	this.setInstanceValue(AjxUtil.mergeArrays(commonExtArr,newExtArr),ZaGlobalConfig.A_zimbraMtaBlockedExtension);
	this.getForm().parent.setDirty(true);			
}

GlobalConfigXFormView.addNewExt = function() {
	var extStr = this.getInstanceValue(ZaGlobalConfig.A_zimbraNewExtension);
	if(AjxUtil.isEmpty(extStr))
		return;
		
	var commonExtArr = this.getInstanceValue(ZaGlobalConfig.A_zimbraMtaBlockedExtension);
	commonExtArr = AjxUtil.isEmpty(commonExtArr) ? [] : commonExtArr;	
	var newExtArr = extStr.split(/[\s,;]+/);
	if(AjxUtil.isEmpty(newExtArr))
		return;
	
	this.setInstanceValue(AjxUtil.mergeArrays(commonExtArr,newExtArr),ZaGlobalConfig.A_zimbraMtaBlockedExtension);
	this.setInstanceValue(null,ZaGlobalConfig.A_zimbraNewExtension);
	this.getForm().parent.setDirty(true);
}

GlobalConfigXFormView.retentionSelectionListener = function(ev) {
    this.getForm().setInstanceValue(this.getSelection(),ZaGlobalConfig.A2_retentionPoliciesKeep_Selection);
}

GlobalConfigXFormView.purgeSelectionListener = function() {
    this.getForm().setInstanceValue(this.getSelection(),ZaGlobalConfig.A2_retentionPoliciesPurge_Selection);
}

GlobalConfigXFormView.deleteButtonListener = function(isPurge) {
    var selected = this.getForm().getInstanceValue(ZaGlobalConfig.A2_retentionPoliciesKeep_Selection);

    if (isPurge) {
        selected = this.getForm().getInstanceValue(ZaGlobalConfig.A2_retentionPoliciesPurge_Selection);
    }

    if (!selected) {
        return;
    }

    if(!ZaApp.getInstance().dialogs["deleteGlobalRetionPolicyConfirmMessageDialog"])
        ZaApp.getInstance().dialogs["deleteGlobalRetionPolicyConfirmMessageDialog"] = new ZaMsgDialog(ZaApp.getInstance().getAppCtxt().getShell(), null, [DwtDialog.YES_BUTTON, DwtDialog.NO_BUTTON], null, ZaId.CTR_PREFIX + "DELETE_RETENTION_POLICY_ConfirmMessage");

    ZaApp.getInstance().dialogs["deleteGlobalRetionPolicyConfirmMessageDialog"].setMessage(ZaMsg.Q_Delete_RetentionPolicy_Confirm,
        DwtMessageDialog.WARNING_STYLE);
    ZaApp.getInstance().dialogs["deleteGlobalRetionPolicyConfirmMessageDialog"].registerCallback(DwtDialog.YES_BUTTON, GlobalConfigXFormView.deleteRetentionPolicies, this, [isPurge]);
    ZaApp.getInstance().dialogs["deleteGlobalRetionPolicyConfirmMessageDialog"].popup();
}

GlobalConfigXFormView.deleteRetentionPolicies = function(isPurge) {
    if(ZaApp.getInstance().dialogs["deleteGlobalRetionPolicyConfirmMessageDialog"])
        ZaApp.getInstance().dialogs["deleteGlobalRetionPolicyConfirmMessageDialog"].popdown();

    var selected = this.getForm().getInstanceValue(ZaGlobalConfig.A2_retentionPoliciesKeep_Selection);
    var all = this.getForm().getInstanceValue(ZaGlobalConfig.A2_retentionPoliciesKeep);

    if (isPurge) {
        selected = this.getForm().getInstanceValue(ZaGlobalConfig.A2_retentionPoliciesPurge_Selection);
        all = this.getForm().getInstanceValue(ZaGlobalConfig.A2_retentionPoliciesPurge);
    }

    if (!selected) {
        return;
    }
    for (var i = 0; i <selected.length; i++ ) {
        selected[i].deletePolicy();
        AjxUtil.arrayRemove(all, selected[i]);
    }

    if (isPurge) {
        this.getForm().setInstanceValue([],ZaGlobalConfig.A2_retentionPoliciesPurge);
        this.getForm().setInstanceValue(all,ZaGlobalConfig.A2_retentionPoliciesPurge);
        this.getForm().setInstanceValue([],ZaGlobalConfig.A2_retentionPoliciesPurge_Selection);
    } else {
        this.getForm().setInstanceValue([],ZaGlobalConfig.A2_retentionPoliciesKeep);
        this.getForm().setInstanceValue(all,ZaGlobalConfig.A2_retentionPoliciesKeep);
        this.getForm().setInstanceValue([],ZaGlobalConfig.A2_retentionPoliciesKeep_Selection);
    }
}

GlobalConfigXFormView.addButtonListener = function (isPurge) {
    var policy;
    if (isPurge) {
        policy = new ZaRetentionPolicy(null, null, null, ZaRetentionPolicy.TYPE_PURGE);
    } else {
        policy = new ZaRetentionPolicy();
    }

    if(!ZaApp.getInstance().dialogs["newGlobalRetentionPolicyDialog"]) {
        ZaApp.getInstance().dialogs["newGlobalRetentionPolicyDialog"] =
            new ZaRetentionPolicyDlg(ZaApp.getInstance().getAppCtxt().getShell(), "500px","100px", ZaMsg.TTL_Policy_Add);
        ZaApp.getInstance().dialogs["newGlobalRetentionPolicyDialog"].registerCallback(DwtDialog.OK_BUTTON,
            GlobalConfigXFormView.createRetentionPolicy,
            this, ZaApp.getInstance().dialogs["newGlobalRetentionPolicyDialog"]._localXForm);
    }

    ZaApp.getInstance().dialogs["newGlobalRetentionPolicyDialog"].setObject(policy);
    ZaApp.getInstance().dialogs["newGlobalRetentionPolicyDialog"].popup();
}

GlobalConfigXFormView.createRetentionPolicy = function (form) {
    if(ZaApp.getInstance().dialogs["newGlobalRetentionPolicyDialog"]) {
        var obj = form.getInstance();
        var all;
        if (obj[ZaRetentionPolicy.A2_type] == ZaRetentionPolicy.TYPE_KEEP) {
            all = this.getForm().getInstanceValue(ZaGlobalConfig.A2_retentionPoliciesKeep);
        } else {
            all = this.getForm().getInstanceValue(ZaGlobalConfig.A2_retentionPoliciesPurge);
        }
        if (!all) {
            all = [];
        }

        if (!ZaRetentionPolicy.checkValues(obj, all)) {
            return false;
        }
        ZaApp.getInstance().dialogs["newGlobalRetentionPolicyDialog"].popdown();

        obj.createPolicy();

        all.push(obj);

        if (obj[ZaRetentionPolicy.A2_type] == ZaRetentionPolicy.TYPE_KEEP) {
            this.getForm().setInstanceValue([], ZaGlobalConfig.A2_retentionPoliciesKeep);
            this.getForm().setInstanceValue(all, ZaGlobalConfig.A2_retentionPoliciesKeep);
            this.getForm().setInstanceValue([],ZaGlobalConfig.A2_retentionPoliciesKeep_Selection);
        } else {
            this.getForm().setInstanceValue([], ZaGlobalConfig.A2_retentionPoliciesPurge);
            this.getForm().setInstanceValue(all, ZaGlobalConfig.A2_retentionPoliciesPurge);
            this.getForm().setInstanceValue([],ZaGlobalConfig.A2_retentionPoliciesPurge_Selection);
        }
    }
}

GlobalConfigXFormView.editButtonListener = function (isPurge) {
    var policy;
    if (isPurge) {
        policy = this.getForm().getInstanceValue(ZaGlobalConfig.A2_retentionPoliciesPurge_Selection)[0];
    } else {
        policy = this.getForm().getInstanceValue(ZaGlobalConfig.A2_retentionPoliciesKeep_Selection)[0];
    }

    if(!ZaApp.getInstance().dialogs["editGlobalRetentionPolicyDialog"]) {
        ZaApp.getInstance().dialogs["editGlobalRetentionPolicyDialog"] =
            new ZaRetentionPolicyDlg(ZaApp.getInstance().getAppCtxt().getShell(), "500px","100px", ZaMsg.TTL_Policy_Edit);
        ZaApp.getInstance().dialogs["editGlobalRetentionPolicyDialog"].registerCallback(DwtDialog.OK_BUTTON,
            GlobalConfigXFormView.updateRetentionPolicy,
            this, ZaApp.getInstance().dialogs["editGlobalRetentionPolicyDialog"]._localXForm);
    }

    ZaApp.getInstance().dialogs["editGlobalRetentionPolicyDialog"].setObject(policy);
    ZaApp.getInstance().dialogs["editGlobalRetentionPolicyDialog"].popup();
}

GlobalConfigXFormView.updateRetentionPolicy = function (form) {
    if(ZaApp.getInstance().dialogs["editGlobalRetentionPolicyDialog"]) {
        var obj = form.getInstance();
        var all;
        if (obj[ZaRetentionPolicy.A2_type] == ZaRetentionPolicy.TYPE_KEEP) {
            all = this.getForm().getInstanceValue(ZaGlobalConfig.A2_retentionPoliciesKeep);
        } else {
            all = this.getForm().getInstanceValue(ZaGlobalConfig.A2_retentionPoliciesPurge);
        }
        if (!all) {
            all = [];
        }
        if (!ZaRetentionPolicy.checkValues(obj, all)) {
            return false;
        }

        ZaApp.getInstance().dialogs["editGlobalRetentionPolicyDialog"].popdown();

        obj.modifyPolicy();

        var index = AjxUtil.indexOf(all, obj);
        AjxUtil.arrayRemove(all, obj);
        AjxUtil.arrayAdd(all, obj, index);

        if (obj[ZaRetentionPolicy.A2_type] == ZaRetentionPolicy.TYPE_KEEP) {
            this.getForm().setInstanceValue([], ZaGlobalConfig.A2_retentionPoliciesKeep);
            this.getForm().setInstanceValue(all, ZaGlobalConfig.A2_retentionPoliciesKeep);
            this.getForm().setInstanceValue([],ZaGlobalConfig.A2_retentionPoliciesKeep_Selection);
        } else {
            this.getForm().setInstanceValue([], ZaGlobalConfig.A2_retentionPoliciesPurge);
            this.getForm().setInstanceValue(all, ZaGlobalConfig.A2_retentionPoliciesPurge);
            this.getForm().setInstanceValue([],ZaGlobalConfig.A2_retentionPoliciesPurge_Selection);
        }
    }
}

GlobalConfigXFormView.GENERAL_TAB_ATTRS = [ZaGlobalConfig.A_zimbraMailPurgeSleepInterval, ZaGlobalConfig.A_zimbraFileUploadMaxSize, ZaGlobalConfig.A_zimbraGalMaxResults, ZaGlobalConfig.A_zimbraDefaultDomainName,ZaGlobalConfig.A_zimbraScheduledTaskNumThreads];
GlobalConfigXFormView.GENERAL_TAB_RIGHTS = [];

GlobalConfigXFormView.ATTACHMENTS_TAB_ATTRS = [ZaGlobalConfig.A_zimbraMtaBlockedExtensionWarnRecipient, ZaGlobalConfig.A_zimbraMtaBlockedExtension,ZaGlobalConfig.A_zimbraMtaCommonBlockedExtension];
GlobalConfigXFormView.ATTACHMENTS_TAB_RIGHTS = [];

GlobalConfigXFormView.MTA_TAB_ATTRS = [
    ZaGlobalConfig.A_zimbraMtaSaslAuthEnable,
    ZaGlobalConfig.A_zimbraMtaTlsAuthOnly,
    ZaGlobalConfig.A_zimbraSmtpHostname,
    ZaGlobalConfig.A_zimbraSmtpPort,
    ZaGlobalConfig.A_zimbraMtaRelayHost,
    ZaGlobalConfig.A_zimbraMtaFallbackRelayHost,
    ZaGlobalConfig.A_zimbraMtaMyNetworks,
    ZaGlobalConfig.A_zimbraMtaDnsLookupsEnabled,
    ZaGlobalConfig.A_zimbraMilterServerEnabled,
    ZaGlobalConfig.A_zimbraMilterBindPort
];
GlobalConfigXFormView.MTA_TAB_RIGHTS = [];

GlobalConfigXFormView.IMAP_TAB_ATTRS = [ZaGlobalConfig.A_zimbraImapServerEnabled, ZaGlobalConfig.A_zimbraImapSSLServerEnabled, ZaGlobalConfig.A_zimbraImapCleartextLoginEnabled,
	ZaGlobalConfig.A_zimbraImapNumThreads];
GlobalConfigXFormView.IMAP_TAB_RIGHTS = [];

GlobalConfigXFormView.POP_TAB_ATTRS = [ZaGlobalConfig.A_zimbraPop3ServerEnabled, ZaGlobalConfig.A_zimbraPop3SSLServerEnabled, ZaGlobalConfig.A_zimbraPop3CleartextLoginEnabled,
	ZaGlobalConfig.A_zimbraPop3NumThreads];
GlobalConfigXFormView.POP_TAB_RIGHTS = [];

GlobalConfigXFormView.ASAV_TAB_ATTRS = [ZaGlobalConfig.A_zimbraSpamKillPercent, ZaGlobalConfig.A_zimbraSpamTagPercent, ZaGlobalConfig.A_zimbraSpamSubjectTag,
	ZaGlobalConfig.A_zimbraVirusDefinitionsUpdateFrequency, ZaGlobalConfig.A_zimbraVirusBlockEncryptedArchive, ZaGlobalConfig.A_zimbraVirusWarnRecipient];
GlobalConfigXFormView.ASAV_TAB_RIGHTS = [];

GlobalConfigXFormView.INTEROP_TAB_ATTRS = [ZaGlobalConfig.A_zimbraFreebusyExchangeURL, ZaGlobalConfig.A_zimbraFreebusyExchangeAuthScheme, 
	ZaGlobalConfig.A_zimbraFreebusyExchangeServerType, ZaGlobalConfig.A_zimbraFreebusyExchangeAuthUsername,
	ZaGlobalConfig.A_zimbraFreebusyExchangeAuthPassword, ZaGlobalConfig.A_zimbraFreebusyExchangeUserOrg];
GlobalConfigXFormView.INTEROP_TAB_RIGHTS = [ZaGlobalConfig.CHECK_EXCHANGE_AUTH_CONFIG_RIGHT];

GlobalConfigXFormView.AUTH_TAB_ATTRS = [ZaGlobalConfig.A_zimbraSpnegoAuthEnabled, ZaGlobalConfig.A_zimbraSpnegoAuthRealm,
    ZaGlobalConfig.A_zimbraSpnegoAuthErrorURL, ZaGlobalConfig.A_zimbraWebClientLoginURL,
    ZaGlobalConfig.A_zimbraWebClientLogoutURL, ZaGlobalConfig.A_zimbraWebClientLoginURLAllowedUA,
    ZaGlobalConfig.A_zimbraWebClientLogoutURLAllowedUA, ZaGlobalConfig.A_zimbraWebClientLoginURLAllowedIP,
    ZaGlobalConfig.A_zimbraWebClientLogoutURLAllowedIP];
GlobalConfigXFormView.AUTH_TAB_RIGHTS = [];

GlobalConfigXFormView.SKIN_TAB_ATTRS = [ZaGlobalConfig.A_zimbraSkinForegroundColor, ZaGlobalConfig.A_zimbraSkinBackgroundColor,ZaGlobalConfig.A_zimbraSkinSecondaryColor,
	ZaGlobalConfig.A_zimbraSkinSelectionColor, ZaGlobalConfig.A_zimbraSkinLogoURL, ZaGlobalConfig.A_zimbraSkinLogoLoginBanner, ZaGlobalConfig.A_zimbraSkinLogoAppBanner ];

GlobalConfigXFormView.SKIN_TAB_RIGHTS = [];

GlobalConfigXFormView.BC_TAB_ATTRS = [ZaGlobalConfig.A_zimbraBasicAuthRealm,ZaGlobalConfig.A_zimbraMailAddressValidationRegex];
GlobalConfigXFormView.BC_TAB_RIGHTS = [];

GlobalConfigXFormView.AUTO_PROV_TAB_ATTRS = [ZaGlobalConfig.A_zimbraAutoProvNotificationBody, ZaGlobalConfig.A_zimbraAutoProvNotificationSubject];
GlobalConfigXFormView.AUTO_PROV_TAB_RIGHTS = [];

GlobalConfigXFormView.RETENTION_POLICY_TAB_ATTRS = [];
GlobalConfigXFormView.RETENTION_POLICY_TAB_RIGHTS = [];

GlobalConfigXFormView.prototype.loadRetentionPolicies = function () {
    var result = ZaRetentionPolicy.getRetentionPolicies();

    if (result) {
        this.getForm().setInstanceValue(result[ZaRetentionPolicy.TYPE_KEEP],ZaGlobalConfig.A2_retentionPoliciesKeep);
        this.getForm().setInstanceValue(result[ZaRetentionPolicy.TYPE_PURGE],ZaGlobalConfig.A2_retentionPoliciesPurge);
    }
}

GlobalConfigXFormView.prototype.setObject =
function(entry) {
	
	ZaTabView.prototype.setObject.call(this, entry);
        // execute other init methods
    if(ZaTabView.XFormSetObjectMethods["GlobalConfigXFormView"]) {
            var methods = ZaTabView.XFormSetObjectMethods["GlobalConfigXFormView"];
            var cnt = methods.length;
            var containedObj = this._containedObject;
            for(var i = 0; i < cnt; i++) {
                    if(typeof(methods[i]) == "function")
                            containedObj = methods[i].call(this, containedObj, entry);
            }
            this._containedObject = containedObj;
    }

	this._localXForm.setInstance(this._containedObject);
}


GlobalConfigXFormView.myXFormModifier = function(xFormObject, entry) {
    var headerListKeep = new Array();
    var sortable = 1;
    var i = 0 ;
    //idPrefix, label, iconInfo, width, sortable, sortField, resizeable, visible
    headerListKeep[i++] = new ZaListHeaderItem(ZaRetentionPolicy.A2_name, ZaMsg.CLV_Policy_Name_col, null, "200px", sortable, ZaRetentionPolicy.A2_name, true, true);
    headerListKeep[i++] = new ZaListHeaderItem(ZaRetentionPolicy.A2_lifetime, ZaMsg.CLV_Policy_Retention_col, null, "auto", null, null, true, true);

    var headerListPurge = new Array();
    i = 0 ;
    //idPrefix, label, iconInfo, width, sortable, sortField, resizeable, visible
    headerListPurge[i++] = new ZaListHeaderItem(ZaRetentionPolicy.A2_name, ZaMsg.CLV_Policy_Name_col, null, "200px", sortable++, ZaRetentionPolicy.A2_name, true, true);
    headerListPurge[i++] = new ZaListHeaderItem(ZaRetentionPolicy.A2_lifetime, ZaMsg.CLV_Policy_Purge_col, null, "auto", null, null, true, true);

	xFormObject.tableCssStyle = "width:100%;overflow:auto;";
	var _tab1, _tab2, _tab3, _tab4, _tab5, _tab6, _tab7, _tab8, _tab9, _tab10, _tab11;
	
    var tabBarChoices = [];
    var switchItems = [];
    this.helpMap = {};
    if(ZaTabView.isTAB_ENABLED(entry,GlobalConfigXFormView.GENERAL_TAB_ATTRS, GlobalConfigXFormView.GENERAL_TAB_RIGHTS)) {
    	_tab1 = ++this.TAB_INDEX;
    	this.helpMap[_tab1] = [location.pathname, ZaUtil.HELP_URL, "managing_global_settings/global_settings.htm", "?locid=", AjxEnv.DEFAULT_LOCALE].join("");
    	var case1 = {type:_ZATABCASE_, caseKey:_tab1,
				colSizes:["auto"],numCols:1,paddingStyle:"padding-left:15px;", width:"98%", cellpadding:2,
				items:[
					{type:_ZA_TOP_GROUPER_,numCols:2,colSizes: ["275px","auto"],
                        label:ZaMsg.TABT_GeneralPage ,
						items:[
							{ref: ZaGlobalConfig.A_zimbraGalMaxResults, type:_TEXTFIELD_,
							  label: ZaMsg.LBL_zimbraGalMaxResults, msgName:ZaMsg.MSG_zimbraGalMaxResults, 
							  labelLocation:_LEFT_, cssClass:"admin_xform_number_input"
							},
						{ref:ZaGlobalConfig.A_zimbraDefaultDomainName, type:_DYNSELECT_,
								label: ZaMsg.NAD_DefaultDomainName,
								toolTipContent:ZaMsg.tt_StartTypingDomainName,
								dataFetcherMethod:ZaSearch.prototype.dynSelectSearchDomains,
								dataFetcherClass:ZaSearch,editable:true
							},
							{ref: ZaGlobalConfig.A_zimbraScheduledTaskNumThreads, type:_TEXTFIELD_,
							  label: ZaMsg.NAD_zimbraScheduledTaskNumThreads, labelLocation:_LEFT_, cssClass:"admin_xform_number_input"
							},
							{ref: ZaGlobalConfig.A_zimbraMailPurgeSleepInterval, type:_LIFETIME_,
							  label: ZaMsg.LBL_zimbraMailPurgeSleepInterval, width: "5em"
							} ,
                          { ref: ZaGlobalConfig.A_zimbraFileUploadMaxSize, type: _TEXTFIELD_,
								  label: ZaMsg.NAD_DOC_MaxUploadSize, labelLocation:_LEFT_, cssClass:"admin_xform_number_input"
	  						},
							// help URL
							//{type: _SPACER_, height: 10},
							{ ref: ZaGlobalConfig.A_zimbraHelpAdminURL, type: _TEXTFIELD_,
                                                                  label: ZaMsg.Domain_zimbraHelpAdminURL, labelLocation:_LEFT_, width:200
                                                        },
							{ ref: ZaGlobalConfig.A_zimbraHelpDelegatedURL, type: _TEXTFIELD_,
                                                                  label: ZaMsg.Domain_zimbraHelpDelegatedURL, labelLocation:_LEFT_, width: 200
                                                        }
                        ]
					}
				]
			};    
   			switchItems.push(case1);
   			tabBarChoices.push({value:_tab1, label:ZaMsg.TABT_GeneralPage});
    }
    if(ZaTabView.isTAB_ENABLED(entry,GlobalConfigXFormView.ATTACHMENTS_TAB_ATTRS, GlobalConfigXFormView.ATTACHMENTS_TAB_RIGHTS)) {
    	_tab2 = ++this.TAB_INDEX;
    	this.helpMap[_tab2] = [location.pathname, ZaUtil.HELP_URL, "managing_global_settings/blocking_attachments_to_email.htm", "?locid=", AjxEnv.DEFAULT_LOCALE].join("");
        tabBarChoices.push ({value:_tab2, label:ZaMsg.NAD_Tab_Attachments});
        var case2 = 	
        {type:_ZATABCASE_, caseKey:_tab2, id:"gs_form_attachment_tab",     paddingStyle:"padding-left:15px;", width:"98%", cellpadding:2, numCols:2, colSizes: ["40%","60%"], items:[
 				{type: _GROUP_,  id:"attachment_settings", width: "98%", numCols: 2, colSpan:2, colSizes:[250, "*"], items: [
					{ref:ZaGlobalConfig.A_zimbraAttachmentsBlocked, type: _CHECKBOX_,
				  		label: ZaMsg.NAD_GlobalRemoveAllAttachments,
				  		trueValue: "TRUE", falseValue: "FALSE"
					},
					{ ref: ZaGlobalConfig.A_zimbraMtaBlockedExtensionWarnRecipient, type: _CHECKBOX_,
				  		label: ZaMsg.LBL_zimbraMtaBlockedExtensionWarnRecipient,
				  		trueValue:"TRUE", falseValue:"FALSE"
					}					
				]},
				{type:_GROUP_, width: "98%", numCols: 1,
					items:[
					    {type:_SPACER_, height:"10"},
        				 {type:_ZACENTER_GROUPER_, numCols:1, width: "100%", label:ZaMsg.NAD_GlobalBlockedExtensions,
							items:[
								{ref:ZaGlobalConfig.A_zimbraMtaBlockedExtension, type:_DWT_LIST_, height:"200", width:"98%",
									cssClass: "DLTarget", cssStyle:"margin-left: 5px; ",
									onSelection:GlobalConfigXFormView.blockedExtSelectionListener
								},
								{type:_SPACER_, height:"5"},
								{type:_GROUP_, width:"100%", numCols:4, colSizes:[125,10, 125,"*"],
									items:[
										{type:_DWT_BUTTON_, label:ZaMsg.DLXV_ButtonRemoveAll, width:120,
											onActivate:"GlobalConfigXFormView.removeAllExt.call(this)",
										   	enableDisableChecks:[GlobalConfigXFormView.shouldEnableRemoveAllButton,[ZaItem.hasWritePermission,ZaGlobalConfig.A_zimbraMtaBlockedExtension]],
									   		enableDisableChangeEventSources:[ZaGlobalConfig.A_zimbraMtaBlockedExtension,ZaGlobalConfig.A_zimbraMtaBlockedExtension]
										},
										{type:_CELLSPACER_},
										{type:_DWT_BUTTON_, label:ZaMsg.DLXV_ButtonRemove, width:120,
										   	onActivate:"GlobalConfigXFormView.removeExt.call(this)",
										   	enableDisableChecks:[GlobalConfigXFormView.shouldEnableRemoveButton,[ZaItem.hasWritePermission,ZaGlobalConfig.A_zimbraMtaBlockedExtension]],
									   		enableDisableChangeEventSources:[ZaGlobalConfig.A2_blocked_extension_selection,ZaGlobalConfig.A_zimbraMtaBlockedExtension]
									    },
										{type:_CELLSPACER_}
									]
								}
							]
        				}
					]
				 },
				 {type: _GROUP_, width: "98%", numCols: 1,
					items: [
					    {type:_SPACER_, height:"10"},
						{type:_ZACENTER_GROUPER_, numCols:1, width: "100%", label:ZaMsg.NAD_GlobalCommonExtensions,
							items:[
								{ref:ZaGlobalConfig.A_zimbraMtaCommonBlockedExtension, type:_DWT_LIST_, height:"200", width:"98%", cssClass: "DLSource",
									onSelection:GlobalConfigXFormView.commonExtSelectionListener
								},
							    {type:_SPACER_, height:"5"},
							    {type:_GROUP_, width:"98%", numCols:7, colSizes:[95,10,70,10,90,60,70],
									items: [
									   	{type:_DWT_BUTTON_, label:ZaMsg.DLXV_ButtonAddSelected, width:95,
											onActivate:"GlobalConfigXFormView.addCommonExt.call(this)",
											enableDisableChecks:[GlobalConfigXFormView.shouldEnableAddButton,[ZaItem.hasWritePermission,ZaGlobalConfig.A_zimbraMtaBlockedExtension]],
											enableDisableChangeEventSources:[ZaGlobalConfig.A2_common_extension_selection,ZaGlobalConfig.A_zimbraMtaBlockedExtension]
										},
									    {type:_CELLSPACER_},
									    {type:_DWT_BUTTON_, label:ZaMsg.DLXV_ButtonAddAll, width:70,
											onActivate:"GlobalConfigXFormView.addAllCommonExt.call(this)",
											enableDisableChecks:[GlobalConfigXFormView.shouldEnableAddAllButton,[ZaItem.hasWritePermission,ZaGlobalConfig.A_zimbraMtaBlockedExtension]],
											enableDisableChangeEventSources:[ZaGlobalConfig.A_zimbraMtaCommonBlockedExtension,ZaGlobalConfig.A_zimbraMtaBlockedExtension]
										},
										{type:_CELLSPACER_},
										{type:_TEXTFIELD_, cssStyle:"width:60px;", ref:ZaGlobalConfig.A_zimbraNewExtension,
											label:ZaMsg.NAD_Attach_NewExtension,
											visibilityChecks:[],
											enableDisableChecks:[[ZaItem.hasWritePermission,ZaGlobalConfig.A_zimbraMtaBlockedExtension]],
											enableDisableChangeEventSources:[ZaGlobalConfig.A_zimbraMtaBlockedExtension]
										},
										{type:_DWT_BUTTON_, label:ZaMsg.NAD_Attach_AddExtension, width:70,
											onActivate:"GlobalConfigXFormView.addNewExt.call(this)",
											enableDisableChecks:[[XForm.checkInstanceValueNotEmty,ZaGlobalConfig.A_zimbraNewExtension],[ZaItem.hasWritePermission,ZaGlobalConfig.A_zimbraMtaBlockedExtension]],
											enableDisableChangeEventSources:[ZaGlobalConfig.A_zimbraNewExtension,ZaGlobalConfig.A_zimbraMtaBlockedExtension]
										}
								  	]
							    }
							]
						  }
				    	]
				    }
				]};
        switchItems.push (case2) ;
    }
    
    if(ZaTabView.isTAB_ENABLED(entry,GlobalConfigXFormView.MTA_TAB_ATTRS, GlobalConfigXFormView.MTA_TAB_RIGHTS)) {
    	_tab3 = ++this.TAB_INDEX;
    	this.helpMap[_tab3] = [location.pathname, ZaUtil.HELP_URL, "managing_global_settings/mta_smtp_restrictions.htm", "?locid=", AjxEnv.DEFAULT_LOCALE].join("");
        tabBarChoices.push ({value:_tab3, label:ZaMsg.NAD_Tab_MTA});
        var case3 = 		{type:_ZATABCASE_, caseKey:_tab3,         paddingStyle:"padding-left:15px;", width:"98%", cellpadding:2,
					colSizes:["auto"],numCols:1,id:"global_mta_tab",
					items: [
						{type:_ZA_TOP_GROUPER_,label:ZaMsg.Global_MTA_AuthenticationGrp,
							visibilityChecks:[[ZATopGrouper_XFormItem.isGroupVisible,
								[ZaGlobalConfig.A_zimbraMtaSaslAuthEnable,
								ZaGlobalConfig.A_zimbraMtaTlsAuthOnly]]],
							visibilityChangeEventSources:[ZaGlobalConfig.A_zimbraMtaSaslAuthEnable,
								ZaGlobalConfig.A_zimbraMtaTlsAuthOnly],
							items:[
							  	{ ref: ZaGlobalConfig.A_zimbraMtaSaslAuthEnable, type: _CHECKBOX_,
							   	  label:ZaMsg.NAD_MTA_Authentication,
							   	  trueValue: "yes", falseValue: "no"
						   	    },
						   	    { ref: ZaGlobalConfig.A_zimbraMtaTlsAuthOnly, type: _CHECKBOX_,
						  	  		enableDisableChangeEventSources:[ZaGlobalConfig.A_zimbraMtaSaslAuthEnable,ZaGlobalConfig.A_zimbraMtaTlsAuthOnly],
						  	  		enableDisableChecks:[[ZaItem.hasWritePermission,ZaGlobalConfig.A_zimbraMtaTlsAuthOnly],[XForm.checkInstanceValue,ZaGlobalConfig.A_zimbraMtaSaslAuthEnable,"yes"]],

				   	    		  label: ZaMsg.NAD_MTA_TlsAuthenticationOnly,
						   	      trueValue: "TRUE", falseValue: "FALSE"

							   	}
							 ]
						},
						{
                            type: _ZA_TOP_GROUPER_,
                            label: ZaMsg.Global_MTA_NetworkGrp,
                            id: "mta_network_group",
                            visibilityChecks: [
                                [
                                    ZATopGrouper_XFormItem.isGroupVisible,
                                    [
                                        ZaGlobalConfig.A_zimbraSmtpHostname,
                                        ZaGlobalConfig.A_zimbraSmtpPort,
                                        ZaGlobalConfig.A_zimbraMtaRelayHost,
                                        ZaGlobalConfig.A_zimbraMtaFallbackRelayHost,
                                        ZaGlobalConfig.A_zimbraDNSCheckHostname,
                                        ZaGlobalConfig.A_zimbraMtaMyNetworks,
                                        ZaGlobalConfig.A_zimbraMtaDnsLookupsEnabled
                                    ]
                                ]
                            ],
                            visibilityChangeEventSources: [
                                ZaGlobalConfig.A_zimbraSmtpHostname,
                                ZaGlobalConfig.A_zimbraSmtpPort,
                                ZaGlobalConfig.A_zimbraMtaRelayHost,
                                ZaGlobalConfig.A_zimbraMtaFallbackRelayHost,
                                ZaGlobalConfig.A_zimbraDNSCheckHostname,
                                ZaGlobalConfig.A_zimbraMtaMyNetworks,
                                ZaGlobalConfig.A_zimbraMtaDnsLookupsEnabled
                            ],
							items:[
								{ ref: ZaGlobalConfig.A_zimbraSmtpHostname, type: _REPEAT_,
						  	  		label: ZaMsg.LBL_zimbraSmtpHostname,
							  		labelLocation:_LEFT_,
							  		align:_LEFT_,
							  		repeatInstance:"",
									showAddButton:true, 
									showRemoveButton:true, 
									showAddOnNextRow:true,
									addButtonLabel:ZaMsg.Add_zimbraSmtpHostname, 
									removeButtonLabel:ZaMsg.Remove_zimbraSmtpHostname,
									removeButtonCSSStyle: "margin-left: 50px",
                                    visibilityChecks:[ZaItem.hasReadPermission],
							  		items: [
										{ ref:".", type: _TEXTFIELD_, label:null,labelLocation:_NONE_,
								  			toolTipContent: ZaMsg.tt_zimbraSmtpHostname,
                                            visibilityChecks:[ZaItem.hasReadPermission]
										}
							  		]
						  		},
								{ ref: ZaGlobalConfig.A_zimbraSmtpPort, type: _OUTPUT_,
								  label: ZaMsg.NAD_MTA_WebMailPort,
                                  visibilityChecks:[ZaItem.hasReadPermission]
							    },
								{
                                    ref: ZaGlobalConfig.A_zimbraMtaRelayHost,
                                    label: ZaMsg.NAD_MTA_RelayMTA,
                                    labelLocation: _LEFT_,
                                    type: _HOSTPORT_,
                                    onClick: "ZaController.showTooltip",
                                    toolTipContent: ZaMsg.tt_MTA_RelayMTA,
                                    visibilityChecks: [ZaItem.hasReadPermission],
                                    onMouseout: "ZaController.hideTooltip"
                                },
                                {
                                    ref: ZaGlobalConfig.A_zimbraMtaFallbackRelayHost,
                                    label: ZaMsg.NAD_MTA_FallbackRelay,
                                    labelLocation: _LEFT_,
                                    type: _HOSTPORT_,
                                    onClick: "ZaController.showTooltip",
                                    toolTipContent: ZaMsg.tt_MTA_FallbackRelay,
                                    visibilityChecks: [ZaItem.hasReadPermission],
                                    onMouseout: "ZaController.hideTooltip"
                                },
								{ type: _DWT_ALERT_,
									containerCssStyle: "padding-bottom:0px",
									style: DwtAlert.INFO,
									iconVisible: true,
									content: ZaMsg.Domain_InboundSMTPNote,
									colSpan:"*"
								},
								{ ref: ZaGlobalConfig.A_zimbraDNSCheckHostname, type: _TEXTFIELD_,
								  label:ZaMsg.Domain_zimbraDNSCheckHostname,
								  toolTipContent: ZaMsg.Domain_zimbraDNSCheckHostname
								},
							  	                                                                							      { ref: ZaGlobalConfig.A_zimbraMtaMyNetworks, type: _TEXTAREA_,
                                                                  label:ZaMsg.NAD_MTA_MyNetworks,
								  msgName:ZaMsg.NAD_MTA_MyNetworks,
                                                                  width:250
                                                                },

								{ ref: ZaGlobalConfig.A_zimbraMtaDnsLookupsEnabled, type: _CHECKBOX_,
							  	  label: ZaMsg.NAD_MTA_DnsLookups,
							  	  trueValue: "TRUE", falseValue: "FALSE"
							  	}
							]
						},
					       
					 	{type:_ZA_TOP_GROUPER_,label:ZaMsg.Global_MTA_MilterServer,
                                                        visibilityChecks:[[ZATopGrouper_XFormItem.isGroupVisible,
                                                                [ZaGlobalConfig.A_zimbraMilterBindPort,
                                                                ZaGlobalConfig.A_zimbraMilterServerEnabled]]],
                                                        visibilityChangeEventSources:[ZaGlobalConfig.A_zimbraMilterBindPort,
                                                                ZaGlobalConfig.A_zimbraMilterServerEnabled
                                                        ],
                                                        items:[
                                                                { ref: ZaGlobalConfig.A_zimbraMilterBindPort, type: _OUTPUT_,
                                                                  label: ZaMsg.NAD_MTA_MilterBindPort
                                                            	},
                                                                { ref: ZaGlobalConfig.A_zimbraMilterServerEnabled, type: _CHECKBOX_,
                                                                        label: ZaMsg.NAD_MTA_MilterServerEnabled, trueValue: "TRUE", falseValue: "FALSE"
                    						}
                                                        ]
                                                },

						{type:_ZA_TOP_GROUPER_,label:ZaMsg.Global_MTA_Messages,
                                                        visibilityChecks:[[ZATopGrouper_XFormItem.isGroupVisible,
                                                                [ZaGlobalConfig.A_zimbraMtaMaxMessageSize,
                                                                ZaGlobalConfig.A_zimbraSmtpSendAddOriginatingIP]]],
                                                        visibilityChangeEventSources:[ZaGlobalConfig.A_zimbraMtaMaxMessageSize,
								ZaGlobalConfig.A_zimbraSmtpSendAddOriginatingIP
                                                        ],
							items:[
								{ ref: ZaGlobalConfig.A_zimbraMtaMaxMessageSize, type: _TEXTFIELD_,
								  label: ZaMsg.NAD_MTA_MaxMsgSize, width: "6em"
		  						},
	  							{ ref: ZaGlobalConfig.A_zimbraSmtpSendAddOriginatingIP, type: _CHECKBOX_,
									label: ZaMsg.NAD_add_x_orginate_IP, trueValue: "TRUE", falseValue: "FALSE"
								}
							]
						},

                        {type:_ZA_TOP_GROUPER_,label: ZaMsg.NAD_MTA_PolicyServiceChecks,
                                visibilityChecks:[[ZATopGrouper_XFormItem.isGroupVisible,
                                        [ZaGlobalConfig.A_zimbraMtaPolicyService]]
                                ],
                                items:[
                                    { ref: ZaGlobalConfig.A_zimbraMtaPolicyService, type: _REPEAT_,
                                      label: ZaMsg.NAD_MTA_policy_service,
                                      labelLocation:_LEFT_,
                                      align:_LEFT_,
                                      repeatInstance:"",
                                      showAddButton:true,
                                      showRemoveButton:true,
                                      showAddOnNextRow:true,
                                      items: [
                                        {ref:".", type:_TEXTFIELD_, label:null, visibilityChecks:[], enableDisableChecks:[] }
                                      ]
                                    }
                                ]
                        },

						{type:_ZA_TOP_GROUPER_,label: ZaMsg.NAD_MTA_ProtocolChecks,
                            visibilityChecks:[[ZATopGrouper_XFormItem.isGroupVisible,
                                    [ZaGlobalConfig.A_zimbraMtaRejectInvalidHostname,
                                    ZaGlobalConfig.A_zimbraMtaRejectNonFqdnHostname,
                                    ZaGlobalConfig.A_zimbraMtaRejectNonFqdnSender]]],
                            visibilityChangeEventSources:[ZaGlobalConfig.A_zimbraMtaRejectUnknownClient,
                                    ZaGlobalConfig.A_zimbraMtaRejectUnknownHostname,
                                    ZaGlobalConfig.A_zimbraMtaRejectUnknownSenderDomain
                            ],
							items:[
						  	{ ref: ZaGlobalConfig.A_zimbraMtaRejectInvalidHostname, type: _CHECKBOX_,
						  	  label: ZaMsg.NAD_MTA_reject_invalid_hostname

						  	},
						  	{ ref: ZaGlobalConfig.A_zimbraMtaRejectNonFqdnHostname, type: _CHECKBOX_,
						  	  label: ZaMsg.NAD_MTA_reject_non_fqdn_hostname
						  	},
						  	{ ref: ZaGlobalConfig.A_zimbraMtaRejectNonFqdnSender, type: _CHECKBOX_,
						  	  label: ZaMsg.NAD_MTA_reject_non_fqdn_sender
						  	}
						]},
						{ type: _ZA_TOP_GROUPER_, label: ZaMsg.NAD_MTA_DnsChecks,
                        				visibilityChecks:[[ZATopGrouper_XFormItem.isGroupVisible,
                                                		[ZaGlobalConfig.A_zimbraMtaRejectUnknownClient,
                                                		ZaGlobalConfig.A_zimbraMtaRejectUnknownHostname,
                                                		ZaGlobalConfig.A_zimbraMtaRejectUnknownSenderDomain]]],
							visibilityChangeEventSources:[ZaGlobalConfig.A_zimbraMtaRejectUnknownClient,
								ZaGlobalConfig.A_zimbraMtaRejectUnknownHostname,
								ZaGlobalConfig.A_zimbraMtaRejectUnknownSenderDomain
						],
						  items: [
						  	{ ref: ZaGlobalConfig.A_zimbraMtaRejectUnknownClient, type: _CHECKBOX_,
						  	  label: ZaMsg.NAD_MTA_reject_unknown_client
						  	},
						  	{ ref: ZaGlobalConfig.A_zimbraMtaRejectUnknownHostname, type: _CHECKBOX_,
						  	  label: ZaMsg.NAD_MTA_reject_unknown_hostname
						  	},
						  	{ ref: ZaGlobalConfig.A_zimbraMtaRejectUnknownSenderDomain, type: _CHECKBOX_,
						  	  label: ZaMsg.NAD_MTA_reject_unknown_sender_domain
						  	},
                            { ref: ZaGlobalConfig.A_zimbraMtaRejectRblClient, type: _REPEAT_,
						  	  label: ZaMsg.NAD_MTA_reject_rbl_client,
							  labelLocation:_LEFT_,
							  align:_LEFT_,
							  repeatInstance:"",
							  showAddButton:true,
							  showRemoveButton:true,
							  showAddOnNextRow:true,
							  items: [
								{ref:".", type:_TEXTFIELD_, label:null}
							  ]/*,
							  onRemove:GlobalConfigXFormView.onRepeatRemove*/
						  	}
						]},/*
                        { type:_ZA_TOP_GROUPER_, label:ZaMsg.NAD_AutoProvision_Setting, colSizes:["275px","100%"],
                            visibilityChecks:[[ZATopGrouper_XFormItem.isGroupVisible,
								[ZaGlobalConfig.A_zimbraAutoProvBatchSize,
								 ZaGlobalConfig.A_zimbraAutoProvPollingInterval]]],
                        items :[
                            {ref:ZaGlobalConfig.A_zimbraAutoProvBatchSize, type:_TEXTFIELD_, label:ZaMsg.LBL_zimbraAutoProvBatchSize,
                                autoSaveValue:true, labelLocation:_LEFT_,
                                cssClass:"admin_xform_number_input"
                            },
                            {ref:ZaGlobalConfig.A_zimbraAutoProvPollingInterval, type:_LIFETIME_,
                                colSizes:["70px","70px","*"],
                                label:ZaMsg.LBL_zimbraAutoProvPollingInterval,
                                colSpan:2,nowrap:false,labelWrap:true
                            }
                        ]
                        },*/
                        {type: _SPACER_, height: 10 }
                ]
            };
        switchItems.push (case3) ;
    }

    if(ZaTabView.isTAB_ENABLED(entry,GlobalConfigXFormView.IMAP_TAB_ATTRS, GlobalConfigXFormView.IMAP_TAB_RIGHTS)) {
    	_tab4 = ++this.TAB_INDEX;
    	this.helpMap[_tab4] = [location.pathname, ZaUtil.HELP_URL, "managing_global_settings/imap_and_pop_proxy_server.htm", "?locid=", AjxEnv.DEFAULT_LOCALE].join("");
        tabBarChoices.push ({value:_tab4, label:ZaMsg.NAD_Tab_IMAP});
        var case4 ={type:_ZATABCASE_, caseKey:_tab4,         paddingStyle:"padding-left:15px;", width:"98%", cellpadding:2,
					colSizes:["auto"],numCols:1,id:"global_imap_tab",
					items: [
						{ type: _DWT_ALERT_,
						  containerCssStyle: "padding-bottom:0px",
						  style: DwtAlert.WARNING,
						  iconVisible: true,
						  content: ZaMsg.Alert_ServerRestart
						},
						{type: _ZA_TOP_GROUPER_, label:ZaMsg.Global_IMAP_ServiceGrp,
						  items: [
							{ ref: ZaGlobalConfig.A_zimbraImapServerEnabled, type:_CHECKBOX_,
							  label: ZaMsg.IMAP_Service,
							  trueValue:"TRUE", falseValue:"FALSE"
	  						},
						  	{ ref: ZaGlobalConfig.A_zimbraImapSSLServerEnabled, type:_CHECKBOX_,
						  	  label: ZaMsg.IMAP_SSLService,
						  	  trueValue:"TRUE", falseValue:"FALSE"
					  	  	},
							{ ref: ZaGlobalConfig.A_zimbraImapCleartextLoginEnabled, type:_CHECKBOX_,
							  label: ZaMsg.IMAP_CleartextLoginEnabled,
							  trueValue:"TRUE", falseValue:"FALSE"
						  	},
							{ ref: ZaGlobalConfig.A_zimbraImapNumThreads, type:_TEXTFIELD_,
							  label: ZaMsg.IMAP_NumThreads,
							  width: "5em"
						  	}
						  ]
						}/*,
						{type:_ZA_TOP_GROUPER_, label:ZaMsg.Global_IMAP_NetworkGrp,
						  items: [
							{ ref: ZaGlobalConfig.A_zimbraImapBindPort, type:_TEXTFIELD_,
							  label: ZaMsg.LBL_IMAP_Port,
							  width: "5em"
						  	},
							{ ref: ZaGlobalConfig.A_zimbraImapSSLBindPort, type:_TEXTFIELD_,
							  label: ZaMsg.LBL_IMAP_SSLPort,
							  width: "5em"
						  	},
							{ ref: ZaGlobalConfig.A_zimbraImapProxyBindPort, type:_TEXTFIELD_,
							  label: ZaMsg.LBL_IMAP_Proxy_Port,
							  width: "5em"
						  	},
							{ ref: ZaGlobalConfig.A_zimbraImapSSLProxyBindPort, type:_TEXTFIELD_,
							  label: ZaMsg.LBL_IMAP_SSL_Proxy_Port,
							  width: "5em"
						  	}
						  ]
						}*/
					]
				};
        switchItems.push (case4) ;
    }

    if(ZaTabView.isTAB_ENABLED(entry,GlobalConfigXFormView.POP_TAB_ATTRS, GlobalConfigXFormView.POP_TAB_RIGHTS)) {
    	_tab5 = ++this.TAB_INDEX;
    	this.helpMap[_tab5] = [location.pathname, ZaUtil.HELP_URL, "managing_global_settings/configuring_pop.htm", "?locid=", AjxEnv.DEFAULT_LOCALE].join("");
        tabBarChoices.push ({value:_tab5, label:ZaMsg.NAD_Tab_POP});
        var case5 = 		{type:_ZATABCASE_, caseKey:_tab5,         paddingStyle:"padding-left:15px;", width:"98%", cellpadding:2,
					colSizes:["auto"],numCols:1,id:"global_pop_tab",
					items: [
						{ type: _DWT_ALERT_,
						  containerCssStyle: "padding-bottom:0px",
						  style: DwtAlert.WARNING,
						  iconVisible: true,
						  content: ZaMsg.Alert_ServerRestart
						},
						{type: _ZA_TOP_GROUPER_, label:ZaMsg.Global_POP_ServiceGrp,
						  items: [
							{ ref: ZaGlobalConfig.A_zimbraPop3ServerEnabled, type: _CHECKBOX_,
							  label: ZaMsg.NAD_POP_Service,
							  trueValue: "TRUE", falseValue: "FALSE"
						  	},
						  	{ ref: ZaGlobalConfig.A_zimbraPop3SSLServerEnabled, type: _CHECKBOX_,
							  label: ZaMsg.NAD_POP_SSL,
							  trueValue: "TRUE", falseValue: "FALSE"
						  	},
						  	{ ref: ZaGlobalConfig.A_zimbraPop3CleartextLoginEnabled, type: _CHECKBOX_,
						  	  label: ZaMsg.NAD_POP_CleartextLoginEnabled,
						  	  trueValue: "TRUE", falseValue: "FALSE"
					  	  	},
					  	  	{ ref: ZaGlobalConfig.A_zimbraPop3NumThreads, type:_TEXTFIELD_,
							  label: ZaMsg.NAD_POP_NumThreads, width: "5em"
							}
						]}/*,
						{type:_ZA_TOP_GROUPER_, label:ZaMsg.Global_POP_NetworkGrp,
						  items: [
							{ ref: ZaGlobalConfig.A_zimbraPop3BindPort, type:_TEXTFIELD_,
							  label: ZaMsg.LBL_POP_Port+":",
							  width: "5em"
						  	},
							{ ref: ZaGlobalConfig.A_zimbraPop3SSLBindPort, type:_TEXTFIELD_,
							  label: ZaMsg.LBL_POP_SSL_Port,
							 width: "5em"
						  	},
							{ ref: ZaGlobalConfig.A_zimbraPop3ProxyBindPort, type:_TEXTFIELD_,
							  label: ZaMsg.LBL_POP_proxy_Port,
							  width: "5em"
						  	},
							{ ref: ZaGlobalConfig.A_zimbraPop3SSLProxyBindPort, type:_TEXTFIELD_,
							  label: ZaMsg.LBL_POP_SSL_proxy_Port,
							 width: "5em"
						  	}
						  ]}*/
					]
				};
        switchItems.push (case5) ;
    }

	if(ZaTabView.isTAB_ENABLED(entry,GlobalConfigXFormView.ASAV_TAB_ATTRS, GlobalConfigXFormView.ASAV_TAB_RIGHTS)) {
    	_tab6 = ++this.TAB_INDEX;
    	this.helpMap[_tab6] = [location.pathname, ZaUtil.HELP_URL, "managing_global_settings/anti-spam_protection.htm", "?locid=", AjxEnv.DEFAULT_LOCALE].join("");
        tabBarChoices.push ({value:_tab6, label:ZaMsg.NAD_Tab_ASAV});
        var case6 = 	// anti-spam
				{type: _ZATABCASE_, caseKey:_tab6,         paddingStyle:"padding-left:15px;", width:"98%", cellpadding:2,
					colSizes:["auto"],numCols:1,id:"global_asav_tab",
				 	items: [
						{type:_ZA_TOP_GROUPER_, label:ZaMsg.NAD_AS_Settings,
						  items: [
                            { type: _DWT_ALERT_,
                              containerCssStyle: "padding-bottom:0px",
                              style: DwtAlert.WARNING,
                              iconVisible: true,
                              content: ZaMsg.Alert_AmavisdRestart
                            },
						  	{ ref: ZaGlobalConfig.A_zimbraSpamKillPercent, type: _INPUT_,
						  	  label: ZaMsg.NAD_Spam_KillPercent, width: "4em"
						  	},
						  	{ ref: ZaGlobalConfig.A_zimbraSpamTagPercent, type: _INPUT_,
						  	  label: ZaMsg.NAD_Spam_TagPercent, width: "4em"
						  	},
						  	{ ref: ZaGlobalConfig.A_zimbraSpamSubjectTag, type: _INPUT_,
						  	  label: ZaMsg.NAD_Spam_SubjectPrefix, width: "20em"
						  	}
						  ]
						},
						{type:_ZA_TOP_GROUPER_, label:ZaMsg.NAD_AV_Settings,
						  items: [
					  	    {ref: ZaGlobalConfig.A_zimbraVirusDefinitionsUpdateFrequency, type: _INPUT_,
					  	     label: ZaMsg.NAD_Virus_DefUpdateFreq, width: "3em",
					  	     getDisplayValue: function(value) { return parseInt(value); },
					  	     elementChanged: function(elementValue, instanceValue, event) {
						     instanceValue = elementValue+"h";
							   	this.getForm().itemChanged(this, instanceValue, event);
							 }
					  	    },
				  	    	{ ref: ZaGlobalConfig.A_zimbraVirusBlockEncryptedArchive, type: _CHECKBOX_,
					   	      label: ZaMsg.NAD_Virus_BlockEncrypted,
							  trueValue:"TRUE", falseValue:"FALSE"
					  	    },
						  	{ ref: ZaGlobalConfig.A_zimbraVirusWarnRecipient, type: _CHECKBOX_,
						  	  label: ZaMsg.NAD_Virus_NotifyRecipient,
							  trueValue:"TRUE", falseValue:"FALSE"
						  	}
						  ]
						}
					]
				} 
			;
        switchItems.push (case6) ;
    }

  	if(ZaTabView.isTAB_ENABLED(entry,GlobalConfigXFormView.INTEROP_TAB_ATTRS, GlobalConfigXFormView.INTEROP_TAB_RIGHTS)) {
    	_tab7 = ++this.TAB_INDEX;
    	this.helpMap[_tab7] = [location.pathname, ZaUtil.HELP_URL, "managing_global_settings/making_free_busy_view__available_.htm", "?locid=", AjxEnv.DEFAULT_LOCALE].join("");
        tabBarChoices.push ({value:_tab7, label:ZaMsg.TABT_Interop});
        var case7 = 		// Interop
				{type: _ZATABCASE_, caseKey:_tab7,         paddingStyle:"padding-left:15px;", width:"98%", cellpadding:2,
					colSizes:["auto"],numCols:1,id:"global_interop_tab",
				 	items: [
						{type:_ZA_TOP_GROUPER_, label:ZaMsg.NAD_Exchange_Settings,
						  items: [
						  	{ ref: ZaGlobalConfig.A_zimbraFreebusyExchangeURL, type: _TEXTFIELD_,
						  	  label: ZaMsg.NAD_Exchange_URL, width: "30em"
						  	},
                            { ref: ZaGlobalConfig.A_zimbraFreebusyExchangeAuthScheme, type: _OSELECT1_,
						  	  label: ZaMsg.NAD_Exchange_Auth_Schema
						  	},
			      { ref: ZaGlobalConfig.A_zimbraFreebusyExchangeServerType, type: _OSELECT1_,
                                                          label: ZaMsg.NAD_Exchange_Server_Type
                                                        },
                              { ref: ZaGlobalConfig.A_zimbraFreebusyExchangeAuthUsername, type: _TEXTFIELD_,
						  	  label: ZaMsg.NAD_Exchange_Auth_User, width: "20em"
						  	},
						  	{ ref: ZaGlobalConfig.A_zimbraFreebusyExchangeAuthPassword, type: _PASSWORD_,
						  	  label: ZaMsg.NAD_Exchange_Auth_Password, width: "20em"
						  	},
                            { ref: ZaGlobalConfig.A_zimbraFreebusyExchangeUserOrg, type: _TEXTFIELD_,
						  	  label: ZaMsg.LBL_zimbraFreebusyExchangeUserOrg, width: "20em"
						  	},
                              {type: _GROUP_, colSpan:2, numCols:5, colSizes: ["120px", "10px", "170px", "20px", "160px" ], items :[
                                  {   type:_CELLSPACER_ },
                                  {
                                      type: _DWT_BUTTON_ , colSpan: 2, label: ZaMsg.Check_Settings, autoPadding: false,
                                      onActivate: ZaItem.checkInteropSettings, enableDisableChecks:[[ZaItem.hasRight,ZaGlobalConfig.CHECK_EXCHANGE_AUTH_CONFIG_RIGHT,ZaZimbraAdmin.currentAdminAccount]]
                                  } ,
                                  {   type:_CELLSPACER_ } ,
                                  {
                                      type: _DWT_BUTTON_ , colSpan: 2, label: ZaMsg.Clear_Settings,autoPadding: false,
                                      onActivate: ZaItem.clearInteropSettings,
                                      enableDisableChecks:[[ZaItem.hasRight,ZaGlobalConfig.CHECK_EXCHANGE_AUTH_CONFIG_RIGHT,ZaZimbraAdmin.currentAdminAccount]]
                                  },
                                  {   type:_CELLSPACER_ }
                                 ]
                              }

                          ]
						}
					]
				};
        switchItems.push (case7) ;
    }

  	if(ZaTabView.isTAB_ENABLED(entry,GlobalConfigXFormView.SKIN_TAB_ATTRS, GlobalConfigXFormView.SKIN_TAB_RIGHTS)) {
    	_tab8 = ++this.TAB_INDEX;
    	this.helpMap[_tab8] = [location.pathname, ZaUtil.HELP_URL, "cos/customizing_ui_themes.htm", "?locid=", AjxEnv.DEFAULT_LOCALE].join("");
        tabBarChoices.push ({value:_tab8, label:ZaMsg.TABT_Themes});
        var case8 =             //skin properties
                {type: _ZATABCASE_, caseKey:_tab8,         paddingStyle:"padding-left:15px;", width:"98%", cellpadding:2,
					colSizes:["auto"],numCols:1,id:"global_skin_tab",
				 	items: [
                        {type:_ZA_TOP_GROUPER_,  label:ZaMsg.NAD_Skin_Color_Settings,
                            items: [
								{ type: _DWT_ALERT_,
									style: DwtAlert.WARNING,
									iconVisible: true, 
									content: ZaMsg.Alert_Flush_Theme_Cache,
									colSpan:2,
									visibilityChecks:[],ref:null
								},                            
                                {ref:ZaGlobalConfig.A_zimbraSkinForegroundColor,
                                    type:_DWT_COLORPICKER_,
                                    label:ZaMsg.NAD_zimbraSkinForegroundColor,
                                    labelLocation:_LEFT_,
                                    buttonImage: "Color", width: "50px"
                                }  ,
                                {ref:ZaGlobalConfig.A_zimbraSkinBackgroundColor,
                                    type:_DWT_COLORPICKER_,
                                    label:ZaMsg.NAD_zimbraSkinBackgroundColor,
                                    labelLocation:_LEFT_,
                                    buttonImage: "Color", width: "50px"
                                }  ,
                                {ref:ZaGlobalConfig.A_zimbraSkinSecondaryColor,
                                    type:_DWT_COLORPICKER_,
                                    label:ZaMsg.NAD_zimbraSkinSecondaryColor,
                                    labelLocation:_LEFT_,
                                    buttonImage: "Color", width: "50px"
                                },
                                {ref:ZaGlobalConfig.A_zimbraSkinSelectionColor,
                                    type:_DWT_COLORPICKER_,
                                    label:ZaMsg.NAD_zimbraSkinSelectionColor,
                                    buttonImage: "Color", width: "50px" ,
                                    labelLocation:_LEFT_
                                },
                                {type:_GROUP_,  colSpan: 2, cssStyle: "margin-top: 10px; margin-left: 200px", items: [
                                        {type: _DWT_BUTTON_,  label: ZaMsg.bt_ResetAllSkinColor,
                                            onActivate: ZaDomainXFormView.resetAllColorThemes }
                                   ]
                                }
                            ]
                        }
                    ]
                };
        switchItems.push (case8) ;
    }

    if(ZaTabView.isTAB_ENABLED(entry,GlobalConfigXFormView.BC_TAB_ATTRS, GlobalConfigXFormView.BC_TAB_RIGHTS)) {
    	_tab9 = ++this.TAB_INDEX;
    	this.helpMap[_tab9] = [location.pathname, ZaUtil.HELP_URL, "managing_global_settings/account_email_validation.htm", "?locid=", AjxEnv.DEFAULT_LOCALE].join("");
        tabBarChoices.push ({value:_tab9, label:ZaMsg.Domain_Tab_Advanced});
        var case9 = 
                {type: _ZATABCASE_, caseKey:_tab9,         paddingStyle:"padding-left:15px;", width:"98%", cellpadding:2,
		            colSizes:["auto"],numCols:1,id:"global_ad_tab",
		            items: 	[
                        { type:_ZA_TOP_GROUPER_, label:ZaMsg.Domain_BC_ShareConf,
                        items :[
                            { ref: ZaGlobalConfig.A_zimbraBasicAuthRealm,
                                type: _TEXTFIELD_,
                                label: ZaMsg.Domain_zimbraBasicAuthRealm
                            }
                        ]
                        },
                        { type:_ZA_TOP_GROUPER_, label:ZaMsg.Domain_AD_EmailValidate,
                        	visibilityChecks:[[ZaItem.hasReadPermission,ZaGlobalConfig.A_zimbraMailAddressValidationRegex]],
                        	visibilityChangeEventSources:[ZaGlobalConfig.A_zimbraMailAddressValidationRegex],
                            items :[
					        { ref: ZaGlobalConfig.A_zimbraMailAddressValidationRegex, type: _REPEAT_,
                                nowrap:false,labelWrap:true,
                                label: ZaMsg.LBL_EmailValidate,
                                labelLocation:_LEFT_,
                                align:_LEFT_,
                                repeatInstance:"",
                                showAddButton:true,
                                showRemoveButton:true,
                                showAddOnNextRow:true,addButtonLabel:ZaMsg.NAD_AddRegex,
                                removeButtonLabel:ZaMsg.NAD_RemoveRegex,
                                removeButtonCSSStyle: "margin-left: 50px",
                                items: [
					                { ref:".", type: _TEXTFIELD_, label:null
					                }
					            ]
					        }
                            ]
                        }
                    ]
                };
        switchItems.push (case9) ;
    }

    if(ZaTabView.isTAB_ENABLED(entry,GlobalConfigXFormView.AUTH_TAB_ATTRS, GlobalConfigXFormView.AUTH_TAB_RIGHTS)) {
    	_tab10 = ++this.TAB_INDEX;
    	this.helpMap[_tab10] = [location.pathname, ZaUtil.HELP_URL, "managing_global_settings/managing_single_sign_on_as_a_global_setting.htm", "?locid=", AjxEnv.DEFAULT_LOCALE].join("");
        tabBarChoices.push ({value:_tab10, label:ZaMsg.NAD_Tab_AUTH});
        var case10 =
                {type: _ZATABCASE_, caseKey:_tab10,         paddingStyle:"padding-left:15px;", width:"98%", cellpadding:2,
		            colSizes:["auto"],numCols:1,id:"global_auth_tab",
		            items:[
                        	/* bug 71234, remove SPNEGO from UI 
                        	{ type:_ZA_TOP_GROUPER_, label:ZaMsg.NAD_SPNEGO_Configure,
                                  items:[
							  	          { ref: ZaGlobalConfig.A_zimbraSpnegoAuthEnabled, type: _CHECKBOX_,
							   	            label:ZaMsg.NAD_Enable_spnego, width: "200px",
							   	            trueValue: "TRUE", falseValue: "FALSE"
						   	              },
                                          { ref: ZaGlobalConfig.A_zimbraSpnegoAuthRealm,
                                            type: _TEXTFIELD_, width: "200px",
                                            label: ZaMsg.LBL_zimbraSpnegoAuthRealm,
                                            enableDisableChangeEventSources:[ZaGlobalConfig.A_zimbraSpnegoAuthEnabled],
                                            enableDisableChecks:[[XForm.checkInstanceValue,ZaGlobalConfig.A_zimbraSpnegoAuthEnabled,'TRUE']]
                                          },
                                          { ref: ZaGlobalConfig.A_zimbraSpnegoAuthErrorURL,
                                            type: _TEXTFIELD_, width: "200px",
                                            label: ZaMsg.LBL_zimbraSpnegoAuthErrorURL,
                                            enableDisableChangeEventSources:[ZaGlobalConfig.A_zimbraSpnegoAuthEnabled],
                                            enableDisableChecks:[[XForm.checkInstanceValue,ZaGlobalConfig.A_zimbraSpnegoAuthEnabled,'TRUE']]
                                          }
                                  ]
                            }, */
                        	/* bug 71233, remove 2-way SSL auth
                            { type:_ZA_TOP_GROUPER_, label: ZaMsg.NAD_AUTH_ClientConfigure,
                                  items:[
                                        {ref:ZaGlobalConfig.A_zimbraMailSSLClientCertMode, type:_SELECT1_,
                                          colSizes:["275px","*"],
                                          label:ZaMsg.NAD_zimbraMailSSLClientCertMode,
                                          labelLocation:_LEFT_
                                        },
                                        { ref: ZaGlobalConfig.A_zimbraMailSSLClientCertPort, type:_TEXTFIELD_,
                                          label: ZaMsg.NAD_zimbraMailSSLClientCertPort
                                        },
                                        { ref: ZaGlobalConfig.A_zimbraMailSSLClientCertPrincipalMap, type:_TEXTAREA_,
                                            label:ZaMsg.NAD_zimbraMailSSLClientCertPrincipalMap, labelCssStyle:"vertical-align:top", width:250
                                        },
                                        {type: _DWT_ALERT_, cssClass: "DwtTabTable", containerCssStyle: "padding-bottom:0px",
                                          style: DwtAlert.WARNING, iconVisible: false, content: ZaMsg.Alert_Ngnix,
                                          id:"xform_header_ngnix"
                                        },
                                        {ref:ZaGlobalConfig.A_zimbraReverseProxyClientCertMode, type:_SELECT1_,
                                          colSizes:["275px","*"],
                                          label:ZaMsg.NAD_zimbraReverseProxyClientCertMode,
                                          labelLocation:_LEFT_
                                        },
                                        {ref:ZaGlobalConfig.A_zimbraReverseProxyMailMode, type:_SELECT1_,
                                          colSizes:["275px","*"],
                                          label:ZaMsg.NAD_zimbraReverseProxyMailMode,
                                          labelLocation:_LEFT_
                                        },
                                        { ref: ZaGlobalConfig.A_zimbraMailSSLProxyClientCertPort, type:_TEXTFIELD_,
                                          label: ZaMsg.NAD_zimbraMailSSLProxyClientCertPort
                                        },
                                        {ref: ZaGlobalConfig.A_zimbraReverseProxyClientCertCA, type:_TEXTAREA_,
                                            label:ZaMsg.NAD_zimbraReverseProxyClientCertCA, width: 400
                                        },
                                        { ref: ZaGlobalConfig.A_zimbraReverseProxyAdminIPAddress,
                                            type:_REPEAT_,
                                            nowrap:false,labelWrap:true,
                                            label:ZaMsg.LBL_zimbraReverseProxyAdminIPAddress,
                                            msgName:ZaMsg.MSG_zimbraReverseProxyAdminIPAddress,
                                            labelLocation:_LEFT_,
                                            addButtonLabel:ZaMsg.NAD_Add,
                                            align:_LEFT_,
                                            showAddButton:true,
                                            showRemoveButton:true,
                                            showAddOnNextRow:true,
                                            removeButtonLabel:ZaMsg.NAD_Remove,
                                            items: [
                                                {
                                                    ref:".", type:_TEXTFIELD_, label:null,width:"200px"
                                                }
                                            ]
                                        }
                                  ]
                            }, */
                            { type:_ZA_TOP_GROUPER_, label: ZaMsg.NAD_WEBCLIENT_Configure,
                                  items:[
                                          { ref: ZaGlobalConfig.A_zimbraWebClientLoginURL,
                                            type:_TEXTFIELD_, width:"200px",
                                            label: ZaMsg.LBL_zimbraWebClientLoginURL
                                          },
                                          { ref: ZaGlobalConfig.A_zimbraWebClientLogoutURL,
                                            type:_TEXTFIELD_, width:"200px",
                                            label: ZaMsg.LBL_zimbraWebClientLogoutURL
                                          },
                                          { ref: ZaGlobalConfig.A_zimbraWebClientLoginURLAllowedUA,
									        type:_REPEAT_,
                                            nowrap:false,labelWrap:true,
                                            label:ZaMsg.LBL_zimbraWebClientLoginURLAllowedUA,
                                            msgName:ZaMsg.MSG_zimbraWebClientLoginURLAllowedUA,
                                            labelLocation:_LEFT_,
                                            addButtonLabel:ZaMsg.NAD_Add,
                                            align:_LEFT_,
                                            showAddButton:true,
                                            showRemoveButton:true,
                                            showAddOnNextRow:true,
                                            removeButtonLabel:ZaMsg.NAD_Remove,
                                            items: [
                                                {
                                                    ref:".", type:_TEXTFIELD_, label:null,width:"200px"
                                                }
                                            ]
                                          },
                                          { ref: ZaGlobalConfig.A_zimbraWebClientLogoutURLAllowedUA,
									        type:_REPEAT_,
                                            nowrap:false,labelWrap:true,
                                            label:ZaMsg.LBL_zimbraWebClientLogoutURLAllowedUA,
                                            msgName:ZaMsg.MSG_zimbraWebClientLogoutURLAllowedUA,
                                            labelLocation:_LEFT_,
                                            addButtonLabel:ZaMsg.NAD_Add,
                                            align:_LEFT_,
                                            showAddButton:true,
                                            showRemoveButton:true,
                                            showAddOnNextRow:true,
                                            removeButtonLabel:ZaMsg.NAD_Remove,
                                            items: [
                                                {
                                                    ref:".", type:_TEXTFIELD_, label:null,width:"200px"
                                                }
                                            ]
                                          },
                                          { ref: ZaGlobalConfig.A_zimbraWebClientLoginURLAllowedIP,
                                              type:_REPEAT_,
                                              nowrap:false,labelWrap:true,
                                              label:ZaMsg.LBL_zimbraWebClientLoginURLAllowedIP,
                                              msgName:ZaMsg.MSG_zimbraWebClientLoginURLAllowedIP,
                                              labelLocation:_LEFT_,
                                              addButtonLabel:ZaMsg.NAD_Add,
                                              align:_LEFT_,
                                              showAddButton:true,
                                              showRemoveButton:true,
                                              showAddOnNextRow:true,
                                              removeButtonLabel:ZaMsg.NAD_Remove,
                                              items: [
                                                  {
                                                      ref:".", type:_TEXTFIELD_, label:null,width:"200px"
                                                  }
                                              ]
                                            },
                                            { ref: ZaGlobalConfig.A_zimbraWebClientLogoutURLAllowedIP,
                                              type:_REPEAT_,
                                              nowrap:false,labelWrap:true,
                                              label:ZaMsg.LBL_zimbraWebClientLogoutURLAllowedIP,
                                              msgName:ZaMsg.MSG_zimbraWebClientLogoutURLAllowedIP,
                                              labelLocation:_LEFT_,
                                              addButtonLabel:ZaMsg.NAD_Add,
                                              align:_LEFT_,
                                              showAddButton:true,
                                              showRemoveButton:true,
                                              showAddOnNextRow:true,
                                              removeButtonLabel:ZaMsg.NAD_Remove,
                                              items: [
                                                  {
                                                      ref:".", type:_TEXTFIELD_, label:null,width:"200px"
                                                  }
                                              ]
                                            }
                                  ]
                            }
                    ]
                };
        switchItems.push (case10) ;
    }

    if(ZaTabView.isTAB_ENABLED(entry,GlobalConfigXFormView.RETENTION_POLICY_TAB_ATTRS, GlobalConfigXFormView.RETENTION_POLICY_TAB_RIGHTS)) {
        _tab11 = ++this.TAB_INDEX;
        this.helpMap[_tab11] = [location.pathname, ZaUtil.HELP_URL, "managing_global_settings/retention_policies.htm", "?locid=", AjxEnv.DEFAULT_LOCALE].join("");
        tabBarChoices.push ({value:_tab11, label:ZaMsg.TABT_RetentionPolicy});
        var case11 =
        {type: _SUPER_TABCASE_, caseKey:_tab11,
            paddingStyle:"padding-left:15px;", width:"98%", cellpadding:2,
            colSizes:["100%"],numCols:1,id:"global_retentionpolicy_tab",
            loadDataMethods: [GlobalConfigXFormView.prototype.loadRetentionPolicies],
            items: [
                {type:_ZA_TOP_GROUPER_, id:"global_form_keep_p_group",width:"98%",
                    numCols:1,colSizes:["auto"],label:ZaMsg.Glb_RetentionPolicies,
                    cssStyle:"margin:10px;padding-bottom:0;",
                    items: [
                        {ref:ZaGlobalConfig.A2_retentionPoliciesKeep, type:_DWT_LIST_, height:"200", width:"99%",
                            preserveSelection:false, multiselect:true,cssClass: "DLSource",
                            headerList:headerListKeep, widgetClass:ZaRetentionPolicyListView,
                            onSelection:GlobalConfigXFormView.retentionSelectionListener,
                            valueChangeEventSources:[ZaGlobalConfig.A2_retentionPoliciesKeep]
                        },
                        {type:_GROUP_, numCols:5, colSizes:["100px","auto","100px","auto","100px"], width:"350px",
                            cssStyle:"margin:10px;padding-bottom:0;",
                            items: [
                                {type:_DWT_BUTTON_, label:ZaMsg.TBB_Delete,width:"100px",
                                    onActivate:"GlobalConfigXFormView.deleteButtonListener.call(this);",
                                    enableDisableChangeEventSources:[ZaGlobalConfig.A2_retentionPoliciesKeep_Selection],
                                    enableDisableChecks:[function() {
                                        var sel = this.getForm().getInstanceValue(ZaGlobalConfig.A2_retentionPoliciesKeep_Selection);
                                        return sel && sel.length > 0;
                                    }]
                                },
                                {type:_CELLSPACER_},
                                {type:_DWT_BUTTON_, label:ZaMsg.TBB_Edit,width:"100px",
                                    onActivate:"GlobalConfigXFormView.editButtonListener.call(this);",
                                    enableDisableChangeEventSources:[ZaGlobalConfig.A2_retentionPoliciesKeep_Selection],
                                    enableDisableChecks:[function() {
                                        var sel = this.getForm().getInstanceValue(ZaGlobalConfig.A2_retentionPoliciesKeep_Selection);
                                        return sel && sel.length == 1;
                                    }]
                                },
                                {type:_CELLSPACER_},
                                {type:_DWT_BUTTON_, label:ZaMsg.NAD_Add,width:"100px",
                                    onActivate:"GlobalConfigXFormView.addButtonListener.call(this);"
                                }
                            ]
                        }
                    ]
                },
                {type:_ZA_TOP_GROUPER_, id:"global_form_purge_p_group",width:"98%",
                    numCols:1,colSizes:["auto"],label:ZaMsg.Glb_DisposalPolicies,
                    cssStyle:"margin:10px;padding-bottom:0;",
                    items: [
                        {ref:ZaGlobalConfig.A2_retentionPoliciesPurge, type:_DWT_LIST_, height:"200", width:"99%",
                            preserveSelection:false, multiselect:true,cssClass: "DLSource",
                            headerList:headerListPurge, widgetClass:ZaRetentionPolicyListView,
                            onSelection:GlobalConfigXFormView.purgeSelectionListener,
                            valueChangeEventSources:[ZaGlobalConfig.A2_retentionPoliciesPurge]
                        },
                        {type:_GROUP_, numCols:5, colSizes:["100px","auto","100px","auto","100px"], width:"350px",
                            cssStyle:"margin:10px;padding-bottom:0;",
                            items: [
                                {type:_DWT_BUTTON_, label:ZaMsg.TBB_Delete,width:"100px",
                                    onActivate:"GlobalConfigXFormView.deleteButtonListener.call(this, 1);",
                                    enableDisableChangeEventSources:[ZaGlobalConfig.A2_retentionPoliciesPurge_Selection],
                                    enableDisableChecks:[function() {
                                        var sel = this.getForm().getInstanceValue(ZaGlobalConfig.A2_retentionPoliciesPurge_Selection);
                                        return sel && sel.length > 0;
                                    }]
                                },
                                {type:_CELLSPACER_},
                                {type:_DWT_BUTTON_, label:ZaMsg.TBB_Edit,width:"100px",
                                    onActivate:"GlobalConfigXFormView.editButtonListener.call(this, 1);",
                                    enableDisableChangeEventSources:[ZaGlobalConfig.A2_retentionPoliciesPurge_Selection],
                                    enableDisableChecks:[function() {
                                        var sel = this.getForm().getInstanceValue(ZaGlobalConfig.A2_retentionPoliciesPurge_Selection);
                                        return sel && sel.length == 1;
                                    }]
                                },
                                {type:_CELLSPACER_},
                                {type:_DWT_BUTTON_, label:ZaMsg.NAD_Add,width:"100px",
                                    onActivate:"GlobalConfigXFormView.addButtonListener.call(this,1);"
                                }
                            ]
                        }
                    ]
                }
            ]
        };
        switchItems.push (case11) ;
    }

    /* bug 71235, remove AUTO Provistioning
    if(ZaTabView.isTAB_ENABLED(entry,GlobalConfigXFormView.AUTO_PROV_TAB_ATTRS, GlobalConfigXFormView.AUTO_PROV_TAB_RIGHTS)) {
    	_tab11 = ++this.TAB_INDEX;

        tabBarChoices.push ({value:_tab11, label:ZaMsg.TABT_Provision});
        var case11 =
                {type: _ZATABCASE_, caseKey:_tab11,         paddingStyle:"padding-left:15px;", width:"98%", cellpadding:2,
		            colSizes:["auto"],numCols:1,id:"global_auto_prov_tab",
		            items: 	[
                        { type:_ZA_TOP_GROUPER_, label:ZaMsg.TTL_zimbraAutoProvEmailSetting,
                        items :[
                            { ref: ZaGlobalConfig.A_zimbraAutoProvNotificationSubject,
                                type: _TEXTFIELD_,width: "400",
                                label: ZaMsg.LBL_zimbraAutoProvEmailSubject
                            },
                            { ref: ZaGlobalConfig.A_zimbraAutoProvNotificationBody,
                                type: _TEXTAREA_, width: 400,
                                label: ZaMsg.LBL_zimbraAutoProvEmailBody
                            }
                        ]
                        }
                    ]
                };
        switchItems.push (case11) ;
    } */

    this.tabChoices = tabBarChoices;
   
    xFormObject.items = [
        { type: _DWT_ALERT_,
          cssClass: "DwtTabTable",
          containerCssStyle: "padding-bottom:0px",
          style: DwtAlert.INFO,
          iconVisible: false,
          content: ZaMsg.Alert_GlobalConfig,
          id:"xform_header"
        },
	    {type:_TAB_BAR_,  ref:ZaModel.currentTab,id:"xform_tabbar", height:"0px",
	 	    containerCssStyle: "padding-top:0px; ", cssStyle:"display:none;",
		    choices: tabBarChoices
	    },
        {type:_SWITCH_, align:_LEFT_, valign:_TOP_, items:switchItems}
    ];
};
ZaTabView.XFormModifiers["GlobalConfigXFormView"].push(GlobalConfigXFormView.myXFormModifier);

GlobalConfigXFormView.prototype.getTabChoices =
function() {
    return this.tabChoices;
}
