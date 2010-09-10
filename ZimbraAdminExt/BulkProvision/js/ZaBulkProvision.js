function ZaBulkProvision () {
    if (arguments.length == 0) return;
    ZaItem.call (this, "ZaBulkProvision") ;
	this._init();
	this.type = ZaItem.BULK_PROVISION;
}

ZaItem.BULK_PROVISION = "BulkProvsion";
ZaItem.BULK_PROVISION_TASK = "BulkProvsionTask";
ZaBulkProvision.URN = "urn:zimbraAdminExt" ;

ZaBulkProvision.prototype = new ZaItem;
ZaBulkProvision.prototype.constructor = ZaBulkProvision;

ZaBulkProvision.A_aid = "aid" ; //uploaded csv file attachment id
ZaBulkProvision.A_csv_aid = "csv_aid" ;
ZaBulkProvision.A_provision_accounts = "provision_accounts";
ZaBulkProvision.A_mustChangePassword = "mustChangePassword"; //the value of the must change password check box
ZaBulkProvision.A_isValidCSV = "isValidCSV" ;

ZaBulkProvision.A2_fileFormat = "fileFormat";
ZaBulkProvision.A2_op = "op";
ZaBulkProvision.A2_isToProvision = "isToProvision" ;
ZaBulkProvision.A2_accountName = "accountName" ;
ZaBulkProvision.A2_displayName = "displayName" ;
ZaBulkProvision.A2_status = "status" ;
ZaBulkProvision.A2_isValid = "isValid" ;
ZaBulkProvision.A2_password = "password";
ZaBulkProvision.A2_confirmPassword = "confirmPassword";
ZaBulkProvision.A2_generatePassword = "generatePassword" ;
ZaBulkProvision.A2_genPasswordLength = "genPasswordLength" ;
ZaBulkProvision.A2_accountLimit = "accountLimit" ;
ZaBulkProvision.A2_provAction = "provAction";
ZaBulkProvision.A2_generatedFileLink = "generatedFileLink";
ZaBulkProvision.A2_TargetDomainName = "TargetDomainName";
ZaBulkProvision.A2_completedAccountsFileLink = "completedAccountsFileLink";
ZaBulkProvision.A2_failedAccountsFileLink = "failedAccountsFileLink";

//provisioning options
ZaBulkProvision.A2_errorReportFileLink = "errorReportFileLink";
ZaBulkProvision.A2_successReportFileLink = "successReportFileLink";
ZaBulkProvision.A2_skippedCount = "skippedCount";
ZaBulkProvision.A2_errorCount = "errorCount";
ZaBulkProvision.A2_provisionedCount = "provisionedCount";
ZaBulkProvision.A2_totalCount = "totalCount";
ZaBulkProvision.A2_runningCount = "runningCount";
ZaBulkProvision.A2_idleCount = "idleCount";
ZaBulkProvision.A2_finishedCount = "finishedCount";
ZaBulkProvision.A2_progress = "progress";
ZaBulkProvision.A2_fileToken = "fileToken";
ZaBulkProvision.A2_createDomains = "createDomains";
ZaBulkProvision.A2_domainCount = "domainCount";
ZaBulkProvision.A2_skippedAccountCount = "skippedAccountCount";
ZaBulkProvision.A2_skippedDomainCount = "skippedDomainCount";
ZaBulkProvision.A2_SMTPHost = "SMTPHost";
ZaBulkProvision.A2_SMTPPort = "SMTPPort";
ZaBulkProvision.A2_sourceType = "sourceType";
ZaBulkProvision.A2_account = "account";
ZaBulkProvision.A2_accountPool = "accountPool";
ZaBulkProvision.A2_activateSearch = "activateSearch";
ZaBulkProvision.A2_poolNumPages = "poolNumPages";
ZaBulkProvision.A2_poolPagenum = "poolPagenum";
ZaBulkProvision.A2_src_acct_selection_pool = "src_acct_selection_pool";
ZaBulkProvision.A2_tgt_acct_selection_pool = "tgt_acct_selection_pool";

//LDAP import options
ZaBulkProvision.A2_maxResults = "maxResults";
ZaBulkProvision.A2_GalLdapURL = "zimbraGalLdapURL";
ZaBulkProvision.A2_GalLdapSearchBase = "zimbraGalLdapSearchBase";
ZaBulkProvision.A2_GalLdapBindDn = "zimbraGalLdapBindDn";
ZaBulkProvision.A2_GalLdapBindPassword = "zimbraGalLdapBindPassword";
ZaBulkProvision.A2_GalLdapConfirmBindPassword = "zimbraGalLdapConfirmBindPassword";
ZaBulkProvision.A2_GalLdapFilter = "zimbraGalLdapFilter";

//IMAP Migration options
ZaBulkProvision.A2_sourceServerType = "sourceServerType";
ZaBulkProvision.A2_IMAPHost = "IMAPHost";
ZaBulkProvision.A2_IMAPPort = "IMAPPort";
ZaBulkProvision.A2_useAdminLogin = "UseAdminLogin";
ZaBulkProvision.A2_IMAPAdminLogin = "IMAPAdminLogin";
ZaBulkProvision.A2_IMAPAdminPassword = "IMAPAdminPassword";
ZaBulkProvision.A2_IMAPAdminPasswordConfirm = "IMAPAdminPasswordConfirm";
ZaBulkProvision.A2_connectionType = "ConnectionType";

//options for Exchange Migration wizard
ZaBulkProvision.A2_importMails = "importMails";
ZaBulkProvision.A2_importContacts = "importContacts";
ZaBulkProvision.A2_importTasks = "importTasks";
ZaBulkProvision.A2_importCalendar = "importCalendar";
ZaBulkProvision.A2_importDeletedItems = "importDeletedItems";
ZaBulkProvision.A2_importJunk = "importJunk";
ZaBulkProvision.A2_ignorePreviouslyImported = "ignorePreviouslyImported";
ZaBulkProvision.A2_InvalidSSLOk = "InvalidSSLOk";
ZaBulkProvision.A2_MapiProfile = "MapiProfile";
ZaBulkProvision.A2_MapiServer = "MapiServer";
ZaBulkProvision.A2_MapiLogonUserDN = "MapiLogonUserDN";
ZaBulkProvision.A2_ZimbraAdminPassword = "ZimbraAdminPassword";
ZaBulkProvision.A2_ZimbraAdminLogin = "ZimbraAdminLogin";
ZaBulkProvision.A2_ZimbraAdminPasswordConfirm = "ZimbraAdminPasswordConfirm";
ZaBulkProvision.A2_provisionUsers = "provisionUsers";
ZaBulkProvision.A2_importEmail = "importEmail";

ZaBulkProvision.iSTATUS_NOT_RUNNING = -1;
ZaBulkProvision.iSTATUS_IDLE = 0;
ZaBulkProvision.iSTATUS_STARTED = 1;
ZaBulkProvision.iSTATUS_STARTING = 2;
ZaBulkProvision.iSTATUS_CREATING_ACCOUNTS = 3;
ZaBulkProvision.iSTATUS_FINISHED = 4;
ZaBulkProvision.iSTATUS_ABORT = 5;
ZaBulkProvision.iSTATUS_ABORTED = 6;
ZaBulkProvision.iSTATUS_ERROR = 100;

ZaBulkProvision.ACTION_IMPORT_CSV = 1;
ZaBulkProvision.ACTION_IMPORT_XML = 2;
ZaBulkProvision.ACTION_IMPORT_LDAP = 3;
ZaBulkProvision.ACTION_GENERATE_MIG_XML = 4;
ZaBulkProvision.ACTION_GENERATE_BULK_XML = 5;
ZaBulkProvision.ACTION_GENERATE_BULK_CSV = 6;
ZaBulkProvision.ACTION_IMPORT_AD = 7;

ZaBulkProvision.FILE_FORMAT_PREVIEW = "preview";
ZaBulkProvision.FILE_FORMAT_MIGRATION_XML = "migrationxml";
ZaBulkProvision.FILE_FORMAT_BULK_XML = "bulkxml";
ZaBulkProvision.FILE_FORMAT_BULK_CSV = "csv";

ZaBulkProvision.SOURCE_TYPE_ZIMBRA = "zimbra";
ZaBulkProvision.SOURCE_TYPE_LDAP = "ldap";
ZaBulkProvision.SOURCE_TYPE_AD = "ad";
ZaBulkProvision.SOURCE_TYPE_XML = "bulkxml";
ZaBulkProvision.SOURCE_TYPE_REUSE_XML = "reusebulkxml";
ZaBulkProvision.SOURCE_TYPE_CSV = "csv";

ZaBulkProvision.MAIL_SOURCE_TYPE_IMAP = "IMAP";
ZaBulkProvision.MAIL_SOURCE_TYPE_EXCHANGE = "EXCHANGE";

ZaBulkProvision.OP_PREVIEW = "preview";
ZaBulkProvision.OP_GET_STATUS = "getStatus";
ZaBulkProvision.OP_PREVIEW_ACTIVE_IMPORTS = "previewActiveImports";
ZaBulkProvision.OP_START_IMPORT = "startImport";
ZaBulkProvision.OP_ABORT_IMPORT = "abortImport";
ZaBulkProvision.OP_DISMISS_IMPORT = "dismissImport";

ZaBulkProvision.CONNECTION_SSL = "ssl";
ZaBulkProvision.CONNECTION_CLEARTEXT = "cleartext";
ZaBulkProvision.CONNECTION_TLS = "tls";
ZaBulkProvision.CONNECTION_TLS_IF_AVAILABLE = "tls_if_available";

ZaBulkProvision.TOO_MANY_ACCOUNTS = "bulkprovision.BP_TOO_MANY_ACCOUNTS";
ZaBulkProvision.BP_INVALID_SEARCH_FILTER = "bulkprovision.BP_INVALID_SEARCH_FILTER";
ZaBulkProvision.BP_NAMING_EXCEPTION = "bulkprovision.BP_NAMING_EXCEPTION";

ZaBulkProvision.getMyXModel = function () {
	ZaBulkProvision.ProvActionChoices = [
		{value:ZaBulkProvision.ACTION_IMPORT_LDAPL,label:com_zimbra_bulkprovision.ActionImportFromLDAP},
		{value:ZaBulkProvision.ACTION_IMPORT_CSV,label:com_zimbra_bulkprovision.ActionImportFromSCV},
		{value:ZaBulkProvision.ACTION_IMPORT_XML,label:com_zimbra_bulkprovision.ActionImportFromXML},
		{value:ZaBulkProvision.ACTION_GENERATE_MIG_XML,label:com_zimbra_bulkprovision.ActionGenerateMigXML},
		{value:ZaBulkProvision.ACTION_IMPORT_AD,label:com_zimbra_bulkprovision.ActionImportAccountsFromAD},
		{value:ZaBulkProvision.ACTION_GENERATE_BULK_XML,label:com_zimbra_bulkprovision.ActionGenerateBulkXML},
		{value:ZaBulkProvision.ACTION_GENERATE_BULK_CSV,label:com_zimbra_bulkprovision.ActionGenerateBulkCSV}
    ];
	
	ZaBulkProvision.SourceServerTypeChoices = [
        {label:com_zimbra_bulkprovision.SourceServerTypeIMAP, value:ZaBulkProvision.MAIL_SOURCE_TYPE_IMAP},
        {label:com_zimbra_bulkprovision.SourceServerTypeExchange, value:ZaBulkProvision.MAIL_SOURCE_TYPE_EXCHANGE} 
	];

	ZaBulkProvision.AccountListSourceTypeChoices = [
        {label:com_zimbra_bulkprovision.AccountListTypeLDAP, value:ZaBulkProvision.SOURCE_TYPE_LDAP},
        {label:com_zimbra_bulkprovision.AccountListTypeAD, value:ZaBulkProvision.SOURCE_TYPE_AD},
        {label:com_zimbra_bulkprovision.AccountListTypeXML, value:ZaBulkProvision.SOURCE_TYPE_XML}
    ];

	ZaBulkProvision.IMAPConnectionTypeChoices = [
        {label:com_zimbra_bulkprovision.IMAPConnectionCleartext, value:ZaBulkProvision.CONNECTION_CLEARTEXT},
        {label:com_zimbra_bulkprovision.IMAPConnectionSSL, value:ZaBulkProvision.CONNECTION_SSL},
        {label:com_zimbra_bulkprovision.IMAPConnectionTLS, value:ZaBulkProvision.CONNECTION_TLS},
        {label:com_zimbra_bulkprovision.IMAPConnectionTLSIfAvailable, value:ZaBulkProvision.CONNECTION_TLS_IF_AVAILABLE}
    ];

	var xmodel = {
        items: [
            {id:ZaBulkProvision.A2_activateSearch,ref:ZaBulkProvision.A2_activateSearch, type:_NUMBER_,defaultValue:1},
            {id:ZaBulkProvision.A2_poolNumPages,ref:ZaBulkProvision.A2_poolNumPages, type:_NUMBER_,defaultValue:1},
            {id:ZaBulkProvision.A2_poolPagenum,ref:ZaBulkProvision.A2_poolPagenum, type:_NUMBER_,defaultValue:1},
    		{id:ZaBulkProvision.A2_src_acct_selection_pool, ref:ZaBulkProvision.A2_src_acct_selection_pool, type:_LIST_},
    		{id:ZaBulkProvision.A2_tgt_acct_selection_pool, ref:ZaBulkProvision.A2_tgt_acct_selection_pool, type:_LIST_},
            {id:ZaBulkProvision.A_csv_aid, type: _STRING_, ref: ZaBulkProvision.A_csv_aid},
	        {id:ZaBulkProvision.A_aid, type: _STRING_, ref: ZaBulkProvision.A_aid},
	        {id:ZaBulkProvision.A_provision_accounts, ref: ZaBulkProvision.A_provision_accounts,type:_LIST_ , dataType: _STRING_ ,outputType:_LIST_ } ,
	        {id:ZaBulkProvision.A_mustChangePassword,ref: ZaBulkProvision.A_mustChangePassword,type:_ENUM_, choices:ZaModel.BOOLEAN_CHOICES },
	        {id:ZaBulkProvision.A_isValidCSV,ref: ZaBulkProvision.A_isValidCSV,type:_ENUM_, choices:ZaModel.BOOLEAN_CHOICES },
	        {id:ZaBulkProvision.A2_provAction, ref:ZaBulkProvision.A2_provAction, type:_ENUM_, choices:ZaBulkProvision.ProvActionChoices},
	        {id:ZaBulkProvision.A2_generatedFileLink, type:_STRING_, ref:ZaBulkProvision.A2_generatedFileLink},
	        {id:ZaBulkProvision.A2_failedAccountsFileLink, type:_STRING_, ref:ZaBulkProvision.A2_failedAccountsFileLink},
	        {id:ZaBulkProvision.A2_completedAccountsFileLink, type:_STRING_, ref:ZaBulkProvision.A2_completedAccountsFileLink},
	        {id:ZaBulkProvision.A2_maxResults, type:_NUMBER_, ref:ZaBulkProvision.A2_maxResults},
	        {id:ZaBulkProvision.A2_errorCount, type:_NUMBER_, ref:ZaBulkProvision.A2_errorCount},
	        {id:ZaBulkProvision.A2_provisionedCount, type:_NUMBER_, ref:ZaBulkProvision.A2_provisionedCount},
	        {id:ZaBulkProvision.A2_totalCount, type:_NUMBER_, ref:ZaBulkProvision.A2_totalCount},
	        {id:ZaBulkProvision.A2_idleCount, type:_NUMBER_, ref:ZaBulkProvision.A2_idleCount},
	        {id:ZaBulkProvision.A2_finishedCount, type:_NUMBER_, ref:ZaBulkProvision.A2_finishedCount},
	        {id:ZaBulkProvision.A2_runningCount, type:_NUMBER_, ref:ZaBulkProvision.A2_runningCount},
	        {id:ZaBulkProvision.A2_domainCount, type:_NUMBER_, ref:ZaBulkProvision.A2_domainCount},
	        {id:ZaBulkProvision.A2_skippedAccountCount, type:_NUMBER_, ref:ZaBulkProvision.A2_skippedAccountCount},
	        {id:ZaBulkProvision.A2_skippedDomainCount, type:_NUMBER_, ref:ZaBulkProvision.A2_skippedDomainCount},
	        {id:ZaBulkProvision.A2_progress, type:_STRING_, ref:ZaBulkProvision.A2_progress},
	        {id:ZaBulkProvision.A2_fileToken, type:_NUMBER_, ref:ZaBulkProvision.A2_fileToken},
	        {id:ZaBulkProvision.A2_skippedCount, type:_NUMBER_, ref:ZaBulkProvision.A2_skippedCount},
	        {id:ZaBulkProvision.A2_status, type:_NUMBER_, ref:ZaBulkProvision.A2_status},
	        {id:ZaBulkProvision.A2_GalLdapURL, type:_SHORT_URL_, ref:ZaBulkProvision.A2_GalLdapURL},
	        {id:ZaBulkProvision.A2_GalLdapSearchBase, type:_STRING_, ref:ZaBulkProvision.A2_GalLdapSearchBase},
	        {id:ZaBulkProvision.A2_GalLdapBindDn, type:_STRING_, ref:ZaBulkProvision.A2_GalLdapBindDn},
	        {id:ZaBulkProvision.A2_GalLdapBindPassword, type:_STRING_, ref:ZaBulkProvision.A2_GalLdapBindPassword},
	        {id:ZaBulkProvision.A2_GalLdapConfirmBindPassword, type:_STRING_, ref:ZaBulkProvision.A2_GalLdapConfirmBindPassword},
	        {id:ZaBulkProvision.A2_GalLdapFilter, type:_STRING_, ref:ZaBulkProvision.A2_GalLdapFilter},
	        {id:ZaBulkProvision.A2_password, type:_STRING_, ref:ZaBulkProvision.A2_password},
	        {id:ZaBulkProvision.A2_confirmPassword, type:_STRING_, ref:ZaBulkProvision.A2_confirmPassword},
	        {id:ZaBulkProvision.A2_generatePassword, type:_ENUM_, choices:ZaModel.BOOLEAN_CHOICES, ref:ZaBulkProvision.A2_generatePassword},
	        {id:ZaBulkProvision.A2_useAdminLogin, type:_ENUM_, choices:ZaModel.getBooleanChoices2(), ref:ZaBulkProvision.A2_useAdminLogin},
	        {id:ZaBulkProvision.A2_IMAPAdminLogin,type:_STRING_,ref:ZaBulkProvision.A2_IMAPAdminLogin},
	        {id:ZaBulkProvision.A2_IMAPAdminPassword,type:_STRING_,ref:ZaBulkProvision.A2_IMAPAdminPassword},
	        {id:ZaBulkProvision.A2_IMAPAdminPasswordConfirm,type:_STRING_,ref:ZaBulkProvision.A2_IMAPAdminPasswordConfirm},
	        {id:ZaBulkProvision.A2_genPasswordLength, type:_NUMBER_, ref:ZaBulkProvision.A2_genPasswordLength},
	        {id:ZaBulkProvision.A2_sourceServerType, type:_ENUM_, ref:ZaBulkProvision.A2_sourceServerType, choices:ZaBulkProvision.SourceServerTypeChoices},
	        {id:ZaBulkProvision.A2_sourceType, type:_ENUM_, ref:ZaBulkProvision.A2_sourceType, choices:ZaBulkProvision.AccountListSourceTypeChoices},
	        {id:ZaBulkProvision.A2_connectionType, type:_ENUM_, ref:ZaBulkProvision.A2_connectionType, choices:ZaBulkProvision.IMAPConnectionTypeChoices},
	        {id:ZaBulkProvision.A2_op, type:_STRING_, ref:ZaBulkProvision.A2_op},
	        {id:ZaBulkProvision.A2_account,type:_LIST_,ref:ZaBulkProvision.A2_account},
	        {id:ZaBulkProvision.A2_accountPool,type:_LIST_,ref:ZaBulkProvision.A2_accountPool},
	        //exchange migration wizard
	        {id:ZaBulkProvision.A2_TargetDomainName, type:_STRING_, ref:ZaBulkProvision.A2_TargetDomainName},
	        {id:ZaBulkProvision.A2_importMails, type:_ENUM_, choices:ZaModel.BOOLEAN_CHOICES, ref:ZaBulkProvision.A2_importMails},
	        {id:ZaBulkProvision.A2_importContacts, type:_ENUM_, choices:ZaModel.BOOLEAN_CHOICES, ref:ZaBulkProvision.A2_importContacts},
	        {id:ZaBulkProvision.A2_importTasks, type:_ENUM_, choices:ZaModel.BOOLEAN_CHOICES, ref:ZaBulkProvision.A2_importTasks},
	        {id:ZaBulkProvision.A2_importCalendar, type:_ENUM_, choices:ZaModel.BOOLEAN_CHOICES, ref:ZaBulkProvision.A2_importCalendar},
	        {id:ZaBulkProvision.A2_importDeletedItems, type:_ENUM_, choices:ZaModel.BOOLEAN_CHOICES, ref:ZaBulkProvision.A2_importDeletedItems},
	        {id:ZaBulkProvision.A2_importJunk, type:_ENUM_, choices:ZaModel.BOOLEAN_CHOICES, ref:ZaBulkProvision.A2_importJunk},
	        {id:ZaBulkProvision.A2_ignorePreviouslyImported, type:_ENUM_, choices:ZaModel.BOOLEAN_CHOICES, ref:ZaBulkProvision.A2_ignorePreviouslyImported},
	        {id:ZaBulkProvision.A2_InvalidSSLOk, type:_ENUM_, choices:ZaModel.BOOLEAN_CHOICES, ref:ZaBulkProvision.A2_InvalidSSLOk},
	        {id:ZaBulkProvision.A2_MapiProfile, type:_STRING_, ref:ZaBulkProvision.A2_MapiProfile},
	        {id:ZaBulkProvision.A2_MapiServer, type:_STRING_, ref:ZaBulkProvision.A2_MapiServer},
	        {id:ZaBulkProvision.A2_MapiLogonUserDN, type:_STRING_, ref:ZaBulkProvision.A2_MapiLogonUserDN},
	        {id:ZaBulkProvision.A2_ZimbraAdminPassword, type:_STRING_, ref:ZaBulkProvision.A2_ZimbraAdminPassword},
	        {id:ZaBulkProvision.A2_ZimbraAdminPasswordConfirm, type:_STRING_, ref:ZaBulkProvision.A2_ZimbraAdminPasswordConfirm},
	        {id:ZaBulkProvision.A2_ZimbraAdminLogin, type:_STRING_, ref:ZaBulkProvision.A2_ZimbraAdminLogin},
	        {id:ZaBulkProvision.A2_provisionUsers, type:_ENUM_, choices:ZaModel.BOOLEAN_CHOICES, ref:ZaBulkProvision.A2_provisionUsers},
	        {id:ZaBulkProvision.A2_importEmail, type:_ENUM_, choices:ZaModel.BOOLEAN_CHOICES, ref:ZaBulkProvision.A2_importEmail},
	        //direct import from LDAP
	        {id:ZaBulkProvision.A2_createDomains, type:_ENUM_, choices:ZaModel.BOOLEAN_CHOICES, ref:ZaBulkProvision.A2_createDomains}
        ]
    }

    return xmodel ;
}

ZaBulkProvision.bulkDataIMport = function(obj, callback) {
	var soapDoc = AjxSoapDoc.create("BulkIMAPDataImportRequest", ZaBulkProvision.URN, null);
	var aid = obj [ZaBulkProvision.A_csv_aid] ;
	soapDoc.set(ZaBulkProvision.A2_op, obj[ZaBulkProvision.A2_op]);
	var controller = ZaApp.getInstance().getCurrentController();
	if(obj[ZaBulkProvision.A2_sourceType] == ZaBulkProvision.SOURCE_TYPE_XML) {
		if (obj[ZaBulkProvision.A_aid] != null) {
	    	soapDoc.set("aid", obj[ZaBulkProvision.A_aid]) ;
	    } else{
	    	controller.popupErrorDialog(com_zimbra_bulkprovision.error_no_aid) ;
	    	return ;
	    }
	}
	if(obj[ZaBulkProvision.A2_sourceType] == ZaBulkProvision.SOURCE_TYPE_ZIMBRA) {
		var cnt = obj[ZaBulkProvision.A2_account].length;
		for(var i = 0; i < cnt; i ++) {
			var accEl = soapDoc.set(ZaBulkProvision.A2_account, "");
			accEl.setAttribute("name", obj[ZaBulkProvision.A2_account][i]);
		}
	}
	attr = soapDoc.set(ZaBulkProvision.A2_sourceType, obj[ZaBulkProvision.A2_sourceType]);
	attr = soapDoc.set(ZaBulkProvision.A2_useAdminLogin,obj[ZaBulkProvision.A2_useAdminLogin]);
	if(obj[ZaBulkProvision.A2_IMAPAdminPassword])
		attr = soapDoc.set(ZaBulkProvision.A2_IMAPAdminPassword,obj[ZaBulkProvision.A2_IMAPAdminPassword]);
	
	if(obj[ZaBulkProvision.A2_IMAPAdminLogin]) {
		attr = soapDoc.set(ZaBulkProvision.A2_IMAPAdminLogin,obj[ZaBulkProvision.A2_IMAPAdminLogin]);
	}
	if(obj[ZaBulkProvision.A2_IMAPHost]) {
		attr = soapDoc.set(ZaBulkProvision.A2_IMAPHost,obj[ZaBulkProvision.A2_IMAPHost]);
	}
	if(obj[ZaBulkProvision.A2_IMAPPort]) {
		attr = soapDoc.set(ZaBulkProvision.A2_IMAPPort,obj[ZaBulkProvision.A2_IMAPPort]);
	}
	if(obj[ZaBulkProvision.A2_connectionType]) {
		attr = soapDoc.set(ZaBulkProvision.A2_connectionType,obj[ZaBulkProvision.A2_connectionType]);
	}
	                                                   	
	var csfeParams = new Object();
	csfeParams.soapDoc = soapDoc;
	csfeParams.asyncMode = true;
	csfeParams.callback = callback;

	var reqMgrParams = {} ;
	reqMgrParams.controller = controller;
	reqMgrParams.busyMsg = com_zimbra_bulkprovision.BUSY_START_PROVISION_ACCOUNTS;
	ZaRequestMgr.invoke(csfeParams, reqMgrParams);	
}

ZaBulkProvision.getBulkProvisionAccounts = function (app, obj) {
    var controller = ZaApp.getInstance().getCurrentController();
    var soapDoc = AjxSoapDoc.create("GetBulkProvisionAccountsRequest", ZaBulkProvision.URN, null);
    var aid = obj [ZaBulkProvision.A_csv_aid] ;
    if (aid != null) {
    	soapDoc.set("aid", aid) ;
    } else{
    	controller.popupErrorDialog(com_zimbra_bulkprovision.error_no_aid) ;
    	return ;
    }

    var accountLimit = obj [ZaBulkProvision.A2_accountLimit] ;

    if (accountLimit != null) {
        soapDoc.setMethodAttribute(ZaBulkProvision.A2_accountLimit, accountLimit) ;
    }
    
    var csfeParams = new Object();
	csfeParams.soapDoc = soapDoc;
	var reqMgrParams = {} ;
	reqMgrParams.controller = controller ;
	reqMgrParams.busyMsg = com_zimbra_bulkprovision.BUSY_GET_PROVISION_ACCOUNTS ;

    /*
    if (callback) {
		csfeParams.callback = callback;
		csfeParams.asyncMode = true ;
	}*/
    
    var resp = ZaRequestMgr.invoke(csfeParams, reqMgrParams).Body.GetBulkProvisionAccountsResponse ;
    var accounts = [] ;
    var aid, isValidCSV ;
    if (resp && resp.account ) {
        for (var i=0; i < resp.account.length ; i ++) {
            accounts.push (resp.account[i]._attrs) ;            
        }
    }

    if (resp && resp.aid) {
        aid = resp.aid[0]._content ;
    }

    if (resp && resp[ZaBulkProvision.A_isValidCSV])    {
        isValidCSV = resp[ZaBulkProvision.A_isValidCSV][0]._content ;        
    }
    return { accounts: accounts, aid: aid, isValidCSV: isValidCSV  } ;
}

ZaBulkProvision.updateBulkProvisionStatus = function (app, instance) {
    var controller = app.getCurrentController();
    var soapDoc = AjxSoapDoc.create("UpdateBulkProvisionStatusRequest", ZaBulkProvision.URN, null);
    var aid =  instance [ZaBulkProvision.A_csv_aid] ;

    if (aid != null) {
        soapDoc.set("aid", aid) ;
    }else{
         controller.popupErrorDialog(com_zimbra_bulkprovision.error_no_aid) ;
         return ;
    }

    var accounts = instance[ZaBulkProvision.A_provision_accounts] ;
    for (var i=0; i < accounts.length; i ++) {
        soapDoc.set("account", {
            name: accounts[i][ZaBulkProvision.A2_accountName],
            status: accounts[i][ZaBulkProvision.A2_status]
        })
    }
    
    var csfeParams = new Object();
	csfeParams.soapDoc = soapDoc;
	var reqMgrParams = {} ;
	reqMgrParams.controller = controller ;
	reqMgrParams.busyMsg = com_zimbra_bulkprovision.BUSY_UPDATE_BP_STATUS ;

    ZaRequestMgr.invoke(csfeParams, reqMgrParams).Body.UpdateBulkProvisionStatusResponse ;
}

ZaBulkProvision.initProvisionAccounts = function (accounts) {
    for (var i=0; i < accounts.length; i ++) {
        if (accounts[i][ZaBulkProvision.A2_isValid] == "TRUE") {
            accounts[i][ZaBulkProvision.A2_isToProvision] = true ;
        }
    }

    return accounts ;
}

ZaBulkProvision.abortImportThread = function(callback) {
	var soapDoc = AjxSoapDoc.create("BulkImportAccountsRequest",ZaBulkProvision.URN, null);
	soapDoc.getMethod().setAttribute(ZaBulkProvision.A2_op, ZaBulkProvision.OP_ABORT_IMPORT);
	var csfeParams = new Object();
	csfeParams.soapDoc = soapDoc;
	csfeParams.asyncMode = true;
	csfeParams.callback = callback;

	var reqMgrParams = {} ;
	reqMgrParams.controller = ZaApp.getInstance().getCurrentController();
	reqMgrParams.busyMsg = com_zimbra_bulkprovision.BUSY_ABORTING_IMPORT_THREAD;
	ZaRequestMgr.invoke(csfeParams, reqMgrParams);	
}

ZaBulkProvision.getImportStatus = function(callback) {
	var soapDoc = AjxSoapDoc.create("BulkImportAccountsRequest",ZaBulkProvision.URN, null);
	soapDoc.getMethod().setAttribute(ZaBulkProvision.A2_op, ZaBulkProvision.OP_GET_STATUS);
	var csfeParams = new Object();
	csfeParams.soapDoc = soapDoc;
	if(callback) {
		csfeParams.asyncMode = true;
		csfeParams.callback = callback;
	}
	var reqMgrParams = {} ;
	reqMgrParams.controller = ZaApp.getInstance().getCurrentController();
	reqMgrParams.busyMsg = com_zimbra_bulkprovision.BUSY_GETTING_PROVISIONING_STATUS;
	return ZaRequestMgr.invoke(csfeParams, reqMgrParams);	
}

ZaBulkProvision.importAccountsFromFile = function (obj, callback) {
	var soapDoc = AjxSoapDoc.create("BulkImportAccountsRequest",ZaBulkProvision.URN, null);
	
	soapDoc.getMethod().setAttribute(ZaBulkProvision.A2_op, obj[ZaBulkProvision.A2_op]);
	var attr = soapDoc.set(ZaBulkProvision.A2_sourceType,obj[ZaBulkProvision.A2_sourceType]);
	attr = soapDoc.set(ZaBulkProvision.A2_createDomains,obj[ZaBulkProvision.A2_createDomains]);
	attr = soapDoc.set(ZaBulkProvision.A_aid,obj[ZaBulkProvision.A_aid]);
	
	if(obj[ZaBulkProvision.A2_SMTPHost]) {
		attr = soapDoc.set(ZaBulkProvision.A2_SMTPHost,obj[ZaBulkProvision.A2_SMTPHost]);
	}
	if(obj[ZaBulkProvision.A2_SMTPPort]) {
		attr = soapDoc.set(ZaBulkProvision.A2_SMTPPort,obj[ZaBulkProvision.A2_SMTPPort]);
	}
	
	var csfeParams = new Object();
	csfeParams.soapDoc = soapDoc;
	csfeParams.asyncMode = true;
	csfeParams.callback = callback;

	var reqMgrParams = {} ;
	reqMgrParams.controller = ZaApp.getInstance().getCurrentController();
	reqMgrParams.busyMsg = com_zimbra_bulkprovision.BUSY_START_PROVISION_ACCOUNTS;
	ZaRequestMgr.invoke(csfeParams, reqMgrParams);	
}

ZaBulkProvision.importAccountsFromLDAP = function (obj, callback) {
	var soapDoc = AjxSoapDoc.create("BulkImportAccountsRequest",ZaBulkProvision.URN, null);
	soapDoc.getMethod().setAttribute(ZaBulkProvision.A2_op, ZaBulkProvision.OP_START_IMPORT);

	var attr = soapDoc.set("a", ZaDomain.GAL_Mode_external);
	attr.setAttribute("n", ZaDomain.A_zimbraGalMode);

	attr = soapDoc.set("a", obj[ZaBulkProvision.A2_GalLdapURL]);
	attr.setAttribute("n", ZaBulkProvision.A2_GalLdapURL);	
	
	attr = soapDoc.set("a", obj[ZaBulkProvision.A2_GalLdapSearchBase]);
	attr.setAttribute("n", ZaBulkProvision.A2_GalLdapSearchBase);	

	attr = soapDoc.set("a", obj[ZaBulkProvision.A2_GalLdapFilter]);
	attr.setAttribute("n", ZaBulkProvision.A2_GalLdapFilter);	

	attr = soapDoc.set("a", obj[ZaBulkProvision.A2_GalLdapBindDn]);
	attr.setAttribute("n", ZaBulkProvision.A2_GalLdapBindDn);

	attr = soapDoc.set("a", obj[ZaBulkProvision.A2_GalLdapBindPassword]);
	attr.setAttribute("n", ZaBulkProvision.A2_GalLdapBindPassword);
	
	attr = soapDoc.set(ZaBulkProvision.A2_sourceType,ZaBulkProvision.SOURCE_TYPE_LDAP);
	if(obj[ZaBulkProvision.A2_password]) {
		attr = soapDoc.set(ZaBulkProvision.A2_password,obj[ZaBulkProvision.A2_password]);
	}
	if(obj[ZaBulkProvision.A2_SMTPHost]) {
		attr = soapDoc.set(ZaBulkProvision.A2_SMTPHost,obj[ZaBulkProvision.A2_SMTPHost]);
	}
	if(obj[ZaBulkProvision.A2_SMTPPort]) {
		attr = soapDoc.set(ZaBulkProvision.A2_SMTPPort,obj[ZaBulkProvision.A2_SMTPPort]);
	}
	
	attr = soapDoc.set(ZaBulkProvision.A2_generatePassword,obj[ZaBulkProvision.A2_generatePassword]);
	attr = soapDoc.set(ZaBulkProvision.A2_genPasswordLength,obj[ZaBulkProvision.A2_genPasswordLength]);
	attr = soapDoc.set(ZaBulkProvision.A_mustChangePassword,obj[ZaBulkProvision.A_mustChangePassword]);	
	attr = soapDoc.set(ZaBulkProvision.A2_maxResults,obj[ZaBulkProvision.A2_maxResults]);
	attr = soapDoc.set(ZaBulkProvision.A2_createDomains,obj[ZaBulkProvision.A2_createDomains]);
	
	var csfeParams = new Object();
	csfeParams.soapDoc = soapDoc;
	csfeParams.asyncMode = true;
	csfeParams.callback = callback;

	var reqMgrParams = {} ;
	reqMgrParams.controller = ZaApp.getInstance().getCurrentController();
	reqMgrParams.busyMsg = com_zimbra_bulkprovision.BUSY_START_PROVISION_ACCOUNTS;
	ZaRequestMgr.invoke(csfeParams, reqMgrParams);	
}

ZaBulkProvision.generateBulkProvisionPreview = function(obj, callback) {
	var soapDoc = AjxSoapDoc.create("GenerateBulkProvisionFileFromLDAPRequest",ZaBulkProvision.URN, null);
	var attr = soapDoc.set("a", ZaDomain.GAL_Mode_external);
	attr.setAttribute("n", ZaDomain.A_zimbraGalMode);

	attr = soapDoc.set("a", obj[ZaBulkProvision.A2_GalLdapURL]);
	attr.setAttribute("n", ZaBulkProvision.A2_GalLdapURL);	
	
	attr = soapDoc.set("a", obj[ZaBulkProvision.A2_GalLdapSearchBase]);
	attr.setAttribute("n", ZaBulkProvision.A2_GalLdapSearchBase);	

	attr = soapDoc.set("a", obj[ZaBulkProvision.A2_GalLdapFilter]);
	attr.setAttribute("n", ZaBulkProvision.A2_GalLdapFilter);	

	attr = soapDoc.set("a", obj[ZaBulkProvision.A2_GalLdapBindDn]);
	attr.setAttribute("n", ZaBulkProvision.A2_GalLdapBindDn);

	attr = soapDoc.set("a", obj[ZaBulkProvision.A2_GalLdapBindPassword]);
	attr.setAttribute("n", ZaBulkProvision.A2_GalLdapBindPassword);

	if(obj[ZaBulkProvision.A2_password]) {
		attr = soapDoc.set(ZaBulkProvision.A2_password,obj[ZaBulkProvision.A2_password]);
	}
	attr = soapDoc.set(ZaBulkProvision.A2_maxResults,obj[ZaBulkProvision.A2_maxResults]);
	attr = soapDoc.set(ZaBulkProvision.A2_fileFormat,ZaBulkProvision.FILE_FORMAT_PREVIEW);
	attr = soapDoc.set(ZaBulkProvision.A2_generatePassword,obj[ZaBulkProvision.A2_generatePassword]);
	attr = soapDoc.set(ZaBulkProvision.A2_genPasswordLength,obj[ZaBulkProvision.A2_genPasswordLength]);
	attr = soapDoc.set(ZaBulkProvision.A_mustChangePassword,obj[ZaBulkProvision.A_mustChangePassword]);

	var csfeParams = new Object();
	csfeParams.soapDoc = soapDoc;
	csfeParams.asyncMode = true;
	csfeParams.callback = callback;

	var reqMgrParams = {} ;
	reqMgrParams.controller = ZaApp.getInstance().getCurrentController();
	reqMgrParams.busyMsg = com_zimbra_bulkprovision.BUSY_GENERATING_BULK_FILE;
	ZaRequestMgr.invoke(csfeParams, reqMgrParams );
};

ZaBulkProvision.generateBulkProvisionFile = function(obj, callback) {
	var soapDoc = AjxSoapDoc.create("GenerateBulkProvisionFileFromLDAPRequest",ZaBulkProvision.URN, null);
	var attr = soapDoc.set("a", ZaDomain.GAL_Mode_external);
	attr.setAttribute("n", ZaDomain.A_zimbraGalMode);

	attr = soapDoc.set("a", obj[ZaBulkProvision.A2_GalLdapURL]);
	attr.setAttribute("n", ZaBulkProvision.A2_GalLdapURL);	
	
	attr = soapDoc.set("a", obj[ZaBulkProvision.A2_GalLdapSearchBase]);
	attr.setAttribute("n", ZaBulkProvision.A2_GalLdapSearchBase);	

	attr = soapDoc.set("a", obj[ZaBulkProvision.A2_GalLdapFilter]);
	attr.setAttribute("n", ZaBulkProvision.A2_GalLdapFilter);	

	attr = soapDoc.set("a", obj[ZaBulkProvision.A2_GalLdapBindDn]);
	attr.setAttribute("n", ZaBulkProvision.A2_GalLdapBindDn);

	attr = soapDoc.set("a", obj[ZaBulkProvision.A2_GalLdapBindPassword]);
	attr.setAttribute("n", ZaBulkProvision.A2_GalLdapBindPassword);

	if(obj[ZaBulkProvision.A2_password]) {
		attr = soapDoc.set(ZaBulkProvision.A2_password,obj[ZaBulkProvision.A2_password]);
	}
	attr = soapDoc.set(ZaBulkProvision.A2_generatePassword,obj[ZaBulkProvision.A2_generatePassword]);
	attr = soapDoc.set(ZaBulkProvision.A2_genPasswordLength,obj[ZaBulkProvision.A2_genPasswordLength]);
	attr = soapDoc.set(ZaBulkProvision.A_mustChangePassword,obj[ZaBulkProvision.A_mustChangePassword]);
	attr = soapDoc.set(ZaBulkProvision.A2_maxResults,obj[ZaBulkProvision.A2_maxResults]);
	
	if(obj[ZaBulkProvision.A2_provAction] == ZaBulkProvision.ACTION_GENERATE_MIG_XML) {
		attr = soapDoc.set(ZaBulkProvision.A2_fileFormat,ZaBulkProvision.FILE_FORMAT_MIGRATION_XML);
		attr = soapDoc.set(ZaBulkProvision.A2_importMails,obj[ZaBulkProvision.A2_importMails]);
		attr = soapDoc.set(ZaBulkProvision.A2_importContacts,obj[ZaBulkProvision.A2_importContacts]);
		attr = soapDoc.set(ZaBulkProvision.A2_importTasks,obj[ZaBulkProvision.A2_importTasks]);
		attr = soapDoc.set(ZaBulkProvision.A2_importCalendar,obj[ZaBulkProvision.A2_importCalendar]);
		attr = soapDoc.set(ZaBulkProvision.A2_importDeletedItems,obj[ZaBulkProvision.A2_importDeletedItems]);
		attr = soapDoc.set(ZaBulkProvision.A2_importJunk,obj[ZaBulkProvision.A2_importJunk]);
		attr = soapDoc.set(ZaBulkProvision.A2_ignorePreviouslyImported,obj[ZaBulkProvision.A2_ignorePreviouslyImported]);
		attr = soapDoc.set(ZaBulkProvision.A2_InvalidSSLOk,obj[ZaBulkProvision.A2_InvalidSSLOk]);
		attr = soapDoc.set(ZaBulkProvision.A2_TargetDomainName,obj[ZaBulkProvision.A2_TargetDomainName]);
		attr = soapDoc.set(ZaBulkProvision.A2_ZimbraAdminLogin,obj[ZaBulkProvision.A2_ZimbraAdminLogin]);
		attr = soapDoc.set(ZaBulkProvision.A2_ZimbraAdminPassword,obj[ZaBulkProvision.A2_ZimbraAdminPassword]);
		attr = soapDoc.set(ZaBulkProvision.A2_MapiProfile,obj[ZaBulkProvision.A2_MapiProfile]);
		attr = soapDoc.set(ZaBulkProvision.A2_MapiServer,obj[ZaBulkProvision.A2_MapiServer]);
		attr = soapDoc.set(ZaBulkProvision.A2_MapiLogonUserDN,obj[ZaBulkProvision.A2_MapiLogonUserDN]);
		attr = soapDoc.set(ZaBulkProvision.A2_provisionUsers,obj[ZaBulkProvision.A2_provisionUsers]);
	} else if(obj[ZaBulkProvision.A2_provAction] == ZaBulkProvision.ACTION_GENERATE_BULK_XML) {
		attr = soapDoc.set(ZaBulkProvision.A2_fileFormat,ZaBulkProvision.FILE_FORMAT_BULK_XML);
	} else if(obj[ZaBulkProvision.A2_provAction] == ZaBulkProvision.ACTION_GENERATE_BULK_CSV) {
		attr = soapDoc.set(ZaBulkProvision.A2_fileFormat,ZaBulkProvision.FILE_FORMAT_BULK_CSV);
	}
	
	var csfeParams = new Object();
	csfeParams.soapDoc = soapDoc;
	csfeParams.asyncMode = true;
	csfeParams.callback = callback;

	var reqMgrParams = {} ;
	reqMgrParams.controller = ZaApp.getInstance().getCurrentController();
	reqMgrParams.busyMsg = com_zimbra_bulkprovision.BUSY_GENERATING_BULK_FILE;
	ZaRequestMgr.invoke(csfeParams, reqMgrParams );
};

ZaBulkProvision.getBulkDataImportTasks = function() {
	var soapDoc = AjxSoapDoc.create("GetBulkIMAPImportTaskListRequest",ZaBulkProvision.URN, null);	
	var params = new Object();
	params.soapDoc = soapDoc;
	params.asyncMode=false;
	var reqMgrParams = {
		controller : ZaApp.getInstance().getCurrentController(),
		busyMsg : com_zimbra_bulkprovision.BUSY_GET_BULK_TASKS
	}
	var resp = ZaRequestMgr.invoke(params, reqMgrParams).Body.GetBulkIMAPImportTaskListResponse;	
	var list = new ZaItemList(ZaBulkProvisionTask);
	list.loadFromJS(resp);	
	return list;
}

ZaBulkProvisionTask = function () {
	ZaItem.call(this, "ZaBulkProvisionTask");
	this._init();
	//The type is required. The application tab uses it to show the right icon
	this.type = ZaItem.BULK_PROVISION_TASK; 
}

ZaBulkProvisionTask.prototype = new ZaItem;
ZaBulkProvisionTask.prototype.constructor = ZaBulkProvisionTask;

ZaBulkProvisionTask.A_owner = "owner";
ZaBulkProvisionTask.A_totalTasks = "totalTasks";
ZaBulkProvisionTask.A_finishedTasks = "finishedTasks";
ZaBulkProvisionTask.A_status = "status";