/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2007, 2008, 2009, 2010 Zimbra, Inc.
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
AjxPackage.require("zimbraAdmin.common.ZaUtil");
AjxPackage.require("zimbraAdmin.common.ZaEvent");
AjxPackage.require("zimbraAdmin.common.ZaModel");
AjxPackage.require("zimbraAdmin.common.ZaItem");
AjxPackage.require("zimbraAdmin.common.ZaId");
AjxPackage.require("zimbraAdmin.common.ZaIPUtil");
AjxPackage.require("zimbraAdmin.common.Lifetime_XFormItem");
AjxPackage.require("zimbraAdmin.config.settings.ZaSettings");
AjxPackage.require("zimbraAdmin.common.ZaAppCtxt");
AjxPackage.require("zimbraAdmin.common.ZaAuthenticate");
AjxPackage.require("zimbraAdmin.common.ZaPopupMenu");
AjxPackage.require("zimbraAdmin.common.ZaAppViewMgr");
AjxPackage.require("zimbraAdmin.common.ZaLoginDialog");
AjxPackage.require("zimbraAdmin.common.ZaController");
AjxPackage.require("zimbraAdmin.common.ZaXFormViewController");
AjxPackage.require("zimbraAdmin.common.ZaListViewController");
AjxPackage.require("zimbraAdmin.common.ZaItemVector");
AjxPackage.require("zimbraAdmin.common.ZaItemList");
AjxPackage.require("zimbraAdmin.common.ZaListView");
AjxPackage.require("zimbraAdmin.common.ZaToolBar");
AjxPackage.require("zimbraAdmin.common.ZaToolBarLabel");
AjxPackage.require("zimbraAdmin.common.ZaToolBarButton");
AjxPackage.require("zimbraAdmin.common.ZaOverviewPanel");
AjxPackage.require("zimbraAdmin.common.ZaClientCmdHandler");
AjxPackage.require("zimbraAdmin.common.ZaApp");
AjxPackage.require("zimbraAdmin.common.ZaAboutDialog");
AjxPackage.require("zimbraAdmin.common.ZaMsgDialog");
AjxPackage.require("zimbraAdmin.common.ZaErrorDialog");
AjxPackage.require("zimbraAdmin.common.ZaTabView");
AjxPackage.require("zimbraAdmin.common.ZaXDialog");
AjxPackage.require("zimbraAdmin.common.ZaXWizardDialog");
AjxPackage.require("zimbraAdmin.common.LDAPURL_XFormItem");
AjxPackage.require("zimbraAdmin.common.HostPort_XFormItem");
AjxPackage.require("zimbraAdmin.common.MailQuota_XModelItem");
AjxPackage.require("zimbraAdmin.common.ZaSelectRadioXFormItem");
AjxPackage.require("zimbraAdmin.common.ZaZimletSelectXFormItem");
AjxPackage.require("zimbraAdmin.common.ZaCheckBoxListXFormItem");
AjxPackage.require("zimbraAdmin.common.Signature_XFormItem");
AjxPackage.require("zimbraAdmin.common.Super_XFormItems");
AjxPackage.require("zimbraAdmin.common.ZaSplashScreen");
AjxPackage.require("zimbraAdmin.common.ZaCurrentAppToolBar");
AjxPackage.require("zimbraAdmin.common.ZaCrtAppTreeHeader");
AjxPackage.require("zimbraAdmin.common.ZaServerVersionInfo");
AjxPackage.require("zimbraAdmin.common.MenuButton_XFormItem");
AjxPackage.require("zimbraAdmin.common.ZaAutoCompleteListView");
AjxPackage.require("zimbraAdmin.common.AutoComplete_XFormItem");
AjxPackage.require("zimbraAdmin.common.ZaKeyMap");
AjxPackage.require("zimbraAdmin.common.ACLXFormItem");
AjxPackage.require("zimbraAdmin.common.ZaSkinPoolChooser");
AjxPackage.require("zimbraAdmin.common.ZaZimletPoolChooser");
AjxPackage.require("zimbraAdmin.common.ZaXProgressDialog");
AjxPackage.require("zimbraAdmin.common.ZaAppTabGroup");
AjxPackage.require("zimbraAdmin.common.ZaAppTab");
AjxPackage.require("zimbraAdmin.common.ZaRequestMgr");
AjxPackage.require("zimbraAdmin.common.ZaActionStatusView");
AjxPackage.require("zimbraAdmin.common.ZaTreeItem");
AjxPackage.require("zimbraAdmin.common.ZaTree");
AjxPackage.require("zimbraAdmin.common.ZaCurrentAppBar");
AjxPackage.require("zimbraAdmin.common.ZaHistoryMgr");

//
// Admin UI Specific components
//

// controllers
AjxPackage.require("zimbraAdmin.common.ZaOverviewPanelController");
AjxPackage.require("zimbraAdmin.common.ZaOperation");
AjxPackage.require("zimbraAdmin.home.controller.ZaHomeController");
AjxPackage.require("zimbraAdmin.accounts.controller.ZaAccountListController");
AjxPackage.require("zimbraAdmin.accounts.controller.ZaAccountViewController");
//AjxPackage.require("zimbraAdmin.accounts.controller.ZaAccAliasesController");
AjxPackage.require("zimbraAdmin.cos.controller.ZaCosListController");
AjxPackage.require("zimbraAdmin.cos.controller.ZaCosController");
AjxPackage.require("zimbraAdmin.domains.controller.ZaDomainListController");
AjxPackage.require("zimbraAdmin.servers.controller.ZaServerListController");
AjxPackage.require("zimbraAdmin.servers.controller.ZaServerController");
AjxPackage.require("zimbraAdmin.adminext.controller.ZaAdminExtListController");
AjxPackage.require("zimbraAdmin.zimlets.controller.ZaZimletListController");
AjxPackage.require("zimbraAdmin.zimlets.controller.ZaZimletViewController");
AjxPackage.require("zimbraAdmin.domains.controller.ZaDomainController");
AjxPackage.require("zimbraAdmin.status.controller.ZaStatusViewController");
AjxPackage.require("zimbraAdmin.statistics.controller.ZaGlobalStatsController");
AjxPackage.require("zimbraAdmin.statistics.controller.ZaServerStatsController");
AjxPackage.require("zimbraAdmin.globalconfig.controller.ZaGlobalConfigViewController");
AjxPackage.require("zimbraAdmin.dl.controller.ZaDLController");
AjxPackage.require("zimbraAdmin.resource.controller.ZaResourceController");
AjxPackage.require("zimbraAdmin.helpdesk.controller.ZaHelpViewController");
AjxPackage.require("zimbraAdmin.helpdesk.controller.ZaMWizController");
AjxPackage.require("zimbraAdmin.mta.controller.ZaMTAListController");
AjxPackage.require("zimbraAdmin.mta.controller.ZaMTAController");
AjxPackage.require("zimbraAdmin.search.controller.ZaSearchListController");
AjxPackage.require("zimbraAdmin.search.controller.ZaSearchBuilderController");
AjxPackage.require("zimbraAdmin.task.controller.ZaTaskController");

// model
AjxPackage.require("zimbraAdmin.home.model.ZaHome");
AjxPackage.require("zimbraAdmin.accounts.model.ZaDataSource");
AjxPackage.require("zimbraAdmin.accounts.model.ZaAccount");
AjxPackage.require("zimbraAdmin.dl.model.ZaDistributionList");
AjxPackage.require("zimbraAdmin.resource.model.ZaSignature");
AjxPackage.require("zimbraAdmin.resource.model.ZaResource");
AjxPackage.require("zimbraAdmin.resource.model.ZaContactList");
AjxPackage.require("zimbraAdmin.accounts.model.ZaAlias");
AjxPackage.require("zimbraAdmin.accounts.model.ZaForwardingAddress");
AjxPackage.require("zimbraAdmin.accounts.model.ZaFp");
AjxPackage.require("zimbraAdmin.cos.model.ZaCos");
AjxPackage.require("zimbraAdmin.domains.model.ZaDomain");
AjxPackage.require("zimbraAdmin.search.model.ZaSearch");
AjxPackage.require("zimbraAdmin.search.model.ZaSearchOption");
AjxPackage.require("zimbraAdmin.servers.model.ZaServer");
AjxPackage.require("zimbraAdmin.zimlets.model.ZaZimlet");
AjxPackage.require("zimbraAdmin.rp.model.ZaRetentionPolicy");
AjxPackage.require("zimbraAdmin.globalconfig.model.ZaGlobalConfig");
AjxPackage.require("zimbraAdmin.status.model.ZaStatus");
AjxPackage.require("zimbraAdmin.mta.model.ZaMTA");
AjxPackage.require("zimbraAdmin.task.model.ZaTask");

// view
AjxPackage.require("zimbraAdmin.home.view.ZaHomeXFormView");
AjxPackage.require("zimbraAdmin.accounts.view.ZaAccountXFormView");
AjxPackage.require("zimbraAdmin.accounts.view.ZaAccChangePwdXDlg");
AjxPackage.require("zimbraAdmin.accounts.view.ZaEditAliasXDialog");
AjxPackage.require("zimbraAdmin.accounts.view.ZaEditFwdAddrXDialog");
AjxPackage.require("zimbraAdmin.accounts.view.ZaEditFpXDialog");
AjxPackage.require("zimbraAdmin.accounts.view.ZaAccountListView");
AjxPackage.require("zimbraAdmin.accounts.view.ZaNewAccountXWizard");
AjxPackage.require("zimbraAdmin.accounts.view.MoveAliasXDialog");
AjxPackage.require("zimbraAdmin.accounts.view.ReindexMailboxXDialog");
AjxPackage.require("zimbraAdmin.accounts.view.DeleteAcctsPgrsDlg");
AjxPackage.require("zimbraAdmin.servers.view.ZaServerVolumesListView");
AjxPackage.require("zimbraAdmin.servers.view.ZaEditVolumeXDialog");
AjxPackage.require("zimbraAdmin.servers.view.ZaProxyPortWarningXDialog");
AjxPackage.require("zimbraAdmin.servers.view.ZaServerListView");
AjxPackage.require("zimbraAdmin.servers.view.ZaServerMiniListView");
AjxPackage.require("zimbraAdmin.servers.view.ZaFlushCacheXDialog");
AjxPackage.require("zimbraAdmin.servers.view.ZaServerXFormView");
AjxPackage.require("zimbraAdmin.adminext.view.ZaAdminExtListView");
AjxPackage.require("zimbraAdmin.zimlets.view.ZaZimletListView");
AjxPackage.require("zimbraAdmin.zimlets.view.ZaZimletXFormView");
AjxPackage.require("zimbraAdmin.zimlets.view.ZaZimletDeployXWizard");
AjxPackage.require("zimbraAdmin.domains.view.ZaDomainListView");
AjxPackage.require("zimbraAdmin.domains.view.ZaDomainXFormView");
AjxPackage.require("zimbraAdmin.domains.view.ZaNewDomainXWizard");
AjxPackage.require("zimbraAdmin.domains.view.ZaDomainAliasWizard");
AjxPackage.require("zimbraAdmin.domains.view.ZaGALConfigXWizard");
AjxPackage.require("zimbraAdmin.domains.view.ZaAuthConfigXWizard");
AjxPackage.require("zimbraAdmin.domains.view.ZaTaskAuthConfigWizard");
AjxPackage.require("zimbraAdmin.domains.view.ZaTaskAutoProvDialog");
AjxPackage.require("zimbraAdmin.domains.view.ZaManualProvConfigDialog");
AjxPackage.require("zimbraAdmin.domains.view.AddrACL_XFormItem");
AjxPackage.require("zimbraAdmin.domains.view.ZaEditDomainAclXDialog");
AjxPackage.require("zimbraAdmin.domains.view.ZaAddDomainAclXDialog");
AjxPackage.require("zimbraAdmin.domains.view.ZaGalObjMiniListView");
AjxPackage.require("zimbraAdmin.domains.view.ZaDomainAccountQuotaListView");
AjxPackage.require("zimbraAdmin.cos.view.ZaCosListView");
AjxPackage.require("zimbraAdmin.cos.view.ZaCosXFormView");
AjxPackage.require("zimbraAdmin.cos.view.ZaNewCosXWizard");
AjxPackage.require("zimbraAdmin.search.view.ZaSearchToolBar");
AjxPackage.require("zimbraAdmin.search.view.ZaSearchField");
AjxPackage.require("zimbraAdmin.search.view.ZaSearchListView");
AjxPackage.require("zimbraAdmin.search.view.ZaSearchBuilderToolbarView");
AjxPackage.require("zimbraAdmin.search.view.ZaSearchOptionView");
AjxPackage.require("zimbraAdmin.search.view.ZaSearchBuilderView");
AjxPackage.require("zimbraAdmin.search.view.ZaSearchOptionDialog");
AjxPackage.require("zimbraAdmin.search.view.ZaSearchBubbleList");
AjxPackage.require("zimbraAdmin.status.view.ZaServicesListView");
AjxPackage.require("zimbraAdmin.statistics.view.ZaGlobalStatsView");
AjxPackage.require("zimbraAdmin.statistics.view.ZaGlobalMessageVolumePage");
AjxPackage.require("zimbraAdmin.statistics.view.ZaGlobalMessageCountPage");
AjxPackage.require("zimbraAdmin.statistics.view.ZaGlobalSpamActivityPage");
AjxPackage.require("zimbraAdmin.statistics.view.ZaGlobalAdvancedStatsPage");

AjxPackage.require("zimbraAdmin.statistics.view.ZaServerStatsView");
AjxPackage.require("zimbraAdmin.statistics.view.ZaServerMessageVolumePage");
AjxPackage.require("zimbraAdmin.statistics.view.ZaServerMessageCountPage");
AjxPackage.require("zimbraAdmin.statistics.view.ZaServerSpamActivityPage");
AjxPackage.require("zimbraAdmin.statistics.view.ZaServerDiskStatsPage");
AjxPackage.require("zimbraAdmin.statistics.view.ZaServerMBXStatsPage");
AjxPackage.require("zimbraAdmin.statistics.view.ZaServerSessionStatsPage");

AjxPackage.require("zimbraAdmin.rp.view.ZaRetentionPolicyListView");
AjxPackage.require("zimbraAdmin.rp.view.ZaRetentionPolicyDlg");

AjxPackage.require("zimbraAdmin.globalconfig.view.GlobalConfigXFormView");
AjxPackage.require("zimbraAdmin.accounts.view.ZaAccMiniListView");
AjxPackage.require("zimbraAdmin.dl.view.ZaDLXFormView");
AjxPackage.require("zimbraAdmin.dl.view.ZaNewDLXWizard");
AjxPackage.require("zimbraAdmin.resource.view.ZaResourceXFormView");
AjxPackage.require("zimbraAdmin.resource.view.ZaNewResourceXWizard");
AjxPackage.require("zimbraAdmin.resource.view.ZaSignatureDlg");
AjxPackage.require("zimbraAdmin.ZaZimbraAdmin");
AjxPackage.require("zimbraAdmin.helpdesk.view.ZaHelpView");
AjxPackage.require("zimbraAdmin.helpdesk.view.ZaMWizView");
AjxPackage.require("zimbraAdmin.mta.view.ZaQSummaryListView");
AjxPackage.require("zimbraAdmin.mta.view.ZaQMessagesListView");
AjxPackage.require("zimbraAdmin.mta.view.ZaMTAListView");
AjxPackage.require("zimbraAdmin.mta.view.ZaMTAXFormView");
AjxPackage.require("zimbraAdmin.mta.view.ZaMTAActionDialog");
AjxPackage.require("zimbraAdmin.accounts.view.ZaAccountMemberOfListView");
AjxPackage.require("zimbraAdmin.task.view.ZaTaskHeaderPanel");
AjxPackage.require("zimbraAdmin.task.view.ZaTaskContentView");

AjxPackage.require("zimbraAdmin.common.EmailAddr_FormItem");
