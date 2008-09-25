function ZaBulkProvision (app) {
    if (arguments.length == 0) return;
    ZaItem.call (this, app, "ZaBulkProvision") ;
	this._init(app);
	this.type = ZaItem.BULK_PROVISION;
}

ZaItem.BULK_PROVISION = "BulkProvsion" ;
ZaBulkProvision.URN = "urn:zimbraAdminExt" ;

ZaBulkProvision.prototype = new ZaItem;
ZaBulkProvision.prototype.constructor = ZaBulkProvision;

ZaBulkProvision.A_csv_aid = "csv_aid" ; //uploaded csv file attachment id
ZaBulkProvision.A_provision_accounts = "provision_accounts" ;

ZaBulkProvision.A2_isToProvision = "isToProvision" ;
ZaBulkProvision.A2_accountName = "accountName" ;
ZaBulkProvision.A2_displayName = "displayName" ;
ZaBulkProvision.A2_status = "status" ;
ZaBulkProvision.A2_isValid = "isValid" ;
ZaBulkProvision.A2_password = "password" ;

ZaBulkProvision.getMyXModel = function () {
    var xmodel = {
        items: [
            {id: ZaBulkProvision.A_csv_aid, type: _STRING_, ref: ZaBulkProvision.A_csv_aid},
            {id:ZaBulkProvision.A_provision_accounts, ref: ZaBulkProvision.A_provision_accounts ,
                           type:_LIST_ , dataType: _STRING_ ,outputType:_LIST_ }
        ]
    }

    return xmodel ;
}

ZaBulkProvision.getBulkProvisionAccounts = function (app, aid) {
    var controller = app.getCurrentController();
    var soapDoc = AjxSoapDoc.create("GetBulkProvisionAccountsRequest", ZaBulkProvision.URN, null);

    if (aid != null) {
     soapDoc.set("aid", aid) ;
    }else{
     controller.popupErrorDialog(com_zimbra_bulkprovision.error_no_aid) ;
     return ;
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
    var aid ;
    if (resp && resp.account ) {
        for (var i=0; i < resp.account.length ; i ++) {
            accounts.push (resp.account[i]._attrs) ;            
        }
    }

    if (resp && resp.aid) {
        aid = resp.aid[0]._content ;
    }
    return { accounts: accounts, aid: aid  } ;
}

ZaBulkProvision.initProvisionAccounts = function (accounts) {
    for (var i=0; i < accounts.length; i ++) {
        if (accounts[i][ZaBulkProvision.A2_isValid] == "TRUE") {
            accounts[i][ZaBulkProvision.A2_isToProvision] = true ;
        }
    }

    return accounts ;
}
