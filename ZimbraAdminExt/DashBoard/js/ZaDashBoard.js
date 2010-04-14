ZaDashBoard = function() {
	
}
ZaSettings.DASHBOARD_VIEW = "dashboard_view";

ZaApp.prototype.getDashBoardController =
function(viewId) {
	if(!this._dashBoardViewController) {
		this._dashBoardViewController = new ZaDashBoardController(this._appCtxt, this._container, this);
	} 
	return this._dashBoardViewController;
}
ZaDashBoard.searchResults = "searchResults";
ZaDashBoard.settingsTab = "settingsTab";
ZaDashBoard.myXModel = {
		items: [
	       {id:ZaDashBoard.settingsTab,type:_NUMBER_},
	       
	       //config
	       {id:ZaGlobalConfig.A_zimbraMtaRelayHost, ref:ZaGlobalConfig.A_zimbraMtaRelayHost, type:_LIST_, listItem:{ type: _HOSTNAME_OR_IP_, maxLength: 256 }},
	       {id:ZaGlobalConfig.A_zimbraAttachmentsBlocked, ref:"attrs/" + ZaGlobalConfig.A_zimbraAttachmentsBlocked, type:_ENUM_, choices:ZaModel.BOOLEAN_CHOICES},
	       {id:ZaGlobalConfig.A_zimbraMtaBlockedExtensionWarnRecipient, ref:"attrs/" + ZaGlobalConfig.A_zimbraMtaBlockedExtensionWarnRecipient, type: _ENUM_, choices: ZaModel.BOOLEAN_CHOICES},
	       {id:ZaGlobalConfig.A_zimbraMtaBlockedExtension, ref:"attrs/" + ZaGlobalConfig.A_zimbraMtaBlockedExtension, type: _LIST_, dataType: _STRING_ },
	       {id:ZaGlobalConfig.A_zimbraMtaCommonBlockedExtension, ref:"attrs/" + ZaGlobalConfig.A_zimbraMtaCommonBlockedExtension, type: _LIST_, dataType: _STRING_ },
	       //search field
	       {id:ZaSearch.A_query, type:_STRING_},
	       {id:ZaDashBoard.searchResults, type:_LIST_, listItem:{type:_OBJECT_}},
	       //services
	       {id:ZaStatus.SVC_MAILBOX,ref:"serviceMap/"+ZaStatus.SVC_MAILBOX,type:_OBJECT_,items:[{id:"status",type:_NUMBER_},{id:"timestamp",type:_NUMBER_}]},
	       {id:ZaStatus.SVC_SPELL,ref:"serviceMap/"+ZaStatus.SVC_SPELL,type:_OBJECT_,items:[{id:"status",type:_NUMBER_},{id:"timestamp",type:_NUMBER_}]},
	       {id:ZaStatus.SVC_LOGGER,ref:"serviceMap/"+ZaStatus.SVC_LOGGER,type:_OBJECT_,items:[{id:"status",type:_NUMBER_},{id:"timestamp",type:_NUMBER_}]},
	       {id:ZaStatus.SVC_MTA,ref:"serviceMap/"+ZaStatus.SVC_MTA,type:_OBJECT_,items:[{id:"status",type:_NUMBER_},{id:"timestamp",type:_NUMBER_}]},
	       {id:ZaStatus.SVC_LDAP,ref:"serviceMap/"+ZaStatus.SVC_LDAP,type:_OBJECT_,items:[{id:"status",type:_NUMBER_},{id:"timestamp",type:_NUMBER_}]},
	       {id:ZaStatus.SVC_MEMCACHED,ref:"serviceMap/"+ZaStatus.SVC_MEMCACHED,type:_OBJECT_,items:[{id:"status",type:_NUMBER_},{id:"timestamp",type:_NUMBER_}]},
	       {id:ZaStatus.SVC_CONVERTD,ref:"serviceMap/"+ZaStatus.SVC_CONVERTD,type:_OBJECT_,items:[{id:"status",type:_NUMBER_},{id:"timestamp",type:_NUMBER_}]},
	       {id:ZaStatus.SVC_IMAPPROXY,ref:"serviceMap/"+ZaStatus.SVC_IMAPPROXY,type:_OBJECT_,items:[{id:"status",type:_NUMBER_},{id:"timestamp",type:_NUMBER_}]},
	       {id:ZaStatus.SVC_STATS,ref:"serviceMap/"+ZaStatus.SVC_STATS,type:_OBJECT_,items:[{id:"status",type:_NUMBER_},{id:"timestamp",type:_NUMBER_}]},
	       {id:ZaStatus.SVC_AS,ref:"serviceMap/"+ZaStatus.SVC_AS,type:_OBJECT_,items:[{id:"status",type:_NUMBER_},{id:"timestamp",type:_NUMBER_}]},
	       {id:ZaStatus.SVC_AV,ref:"serviceMap/"+ZaStatus.SVC_AV,type:_OBJECT_,items:[{id:"status",type:_NUMBER_},{id:"timestamp",type:_NUMBER_}]}
	       
	    ]
};

ZaAccountXFormView.ADVANCED_TAB_ATTRS = [ZaAccount.A_zimbraAttachmentsBlocked,
                                     	ZaAccount.A_zimbraQuotaWarnPercent,
                                     	ZaAccount.A_zimbraQuotaWarnInterval,
                                     	ZaAccount.A_zimbraQuotaWarnMessage,
                                     	ZaAccount.A_zimbraPasswordLocked,
                                     	ZaAccount.A_zimbraMinPwdLength,
                                     	ZaAccount.A_zimbraMaxPwdLength,
                                     	ZaAccount.A_zimbraPasswordMinUpperCaseChars,
                                     	ZaAccount.A_zimbraPasswordMinLowerCaseChars,
                                     	ZaAccount.A_zimbraPasswordMinPunctuationChars,
                                     	ZaAccount.A_zimbraPasswordMinNumericChars,
                                     	ZaAccount.A_zimbraMinPwdAge,
                                     	ZaAccount.A_zimbraMaxPwdAge,
                                     	ZaAccount.A_zimbraEnforcePwdHistory,
                                     	ZaAccount.A_zimbraPasswordLockoutEnabled,
                                     	ZaAccount.A_zimbraPasswordLockoutMaxFailures,
                                     	ZaAccount.A_zimbraPasswordLockoutDuration,
                                     	ZaAccount.A_zimbraPasswordLockoutFailureLifetime,
                                     	ZaAccount.A_zimbraAdminAuthTokenLifetime,
                                     	ZaAccount.A_zimbraAuthTokenLifetime,
                                     	ZaAccount.A_zimbraMailIdleSessionTimeout,
                                     	ZaAccount.A_zimbraMailMessageLifetime,
                                     	ZaAccount.A_zimbraMailTrashLifetime,
                                     	ZaAccount.A_zimbraMailSpamLifetime,
                                     	ZaAccount.A_zimbraFreebusyExchangeUserOrg];
ZaMsg.COS_view_title = com_zimbra_dashboard.COS_view_title;
ZaMsg.COSTBB_New_tt = com_zimbra_dashboard.COSTBB_New_tt;
ZaMsg.COSTBB_Edit_tt = com_zimbra_dashboard.COSTBB_Edit_tt;
ZaMsg.COSTBB_Delete_tt = com_zimbra_dashboard.COSTBB_Delete_tt;
ZaMsg.COSTBB_Duplicate_tt = com_zimbra_dashboard.COSTBB_Duplicate_tt;
ZaMsg.COSTBB_Save_tt = com_zimbra_dashboard.COSTBB_Save_tt;
ZaMsg.Search_view_title = com_zimbra_dashboard.Search_view_title;
ZaMsg.NAD_ResetToCOS = com_zimbra_dashboard.NAD_ResetToCOS;
ZaMsg.Domain_DefaultCOS = com_zimbra_dashboard.Domain_DefaultCOS;
ZaMsg.NAD_ClassOfService =  com_zimbra_dashboard.NAD_ClassOfService;
ZaMsg.GlobalConfig_view_title = com_zimbra_dashboard.GlobalConfig_view_title;
ZaMsg.Domain_DefaultCOS = com_zimbra_dashboard.Domain_DefaultCOS;