
//add the bulk provision account toolbar button
if (ZaController.initToolbarMethods["ZaAccountListController"]) {
    ZaOperation.BULK_PROVISION = ++ ZA_OP_INDEX ;
    ZaAccountListController.initExtraToolbarMethod = function () {
		var showBulkProvision = false;
		if(ZaSettings.HAVE_MORE_DOMAINS || ZaZimbraAdmin.currentAdminAccount.attrs[ZaAccount.A_zimbraIsAdminAccount] == 'TRUE') {
			showBulkProvision = true;
		} else {
			var domainList = ZaApp.getInstance().getDomainList().getArray();
			var cnt = domainList.length;
			for(var i = 0; i < cnt; i++) {
				if(ZaItem.hasRight(ZaDomain.RIGHT_CREATE_ACCOUNT,domainList[i])) {
					showBulkProvision = true;
					break;
				}	
			}
		}	
		if(showBulkProvision) {    	
	        this._toolbarOperations [ZaOperation.BULK_PROVISION] = 
	                new ZaOperation(ZaOperation.BULK_PROVISION, com_zimbra_bulkprovision.ACTBB_BulkProvision,
	                        com_zimbra_bulkprovision.ACTBB_BulkProvision_tt, "BulkProvision", "BulkProvisionDis", 
	                        new AjxListener(this, ZaAccountListController.prototype._bulkProvisionListener)
	                        );
	        // only add the bulk provision for account list view.
	        if (this._defaultType == ZaItem.ACCOUNT) {
	           this._toolbarOrder.push(ZaOperation.BULK_PROVISION) ;
	        }
	
	    }
	}
    ZaController.initToolbarMethods["ZaAccountListController"].push(ZaAccountListController.initExtraToolbarMethod);
}

 ZaAccountListController.prototype._bulkProvisionListener =
 function (ev) {
     try {
		if(!ZaApp.getInstance().dialogs["bulkProvisionWizard"]) {
			ZaApp.getInstance().dialogs["bulkProvisionWizard"] = new ZaBulkProvisionWizard(this._container);
		}
		ZaApp.getInstance().dialogs["bulkProvisionWizard"].setObject(new ZaBulkProvision());
		ZaApp.getInstance().dialogs["bulkProvisionWizard"].popup();
	} catch (ex) {
		this._handleException(ex, "ZaAccountListController.prototype._bulkProvisionListener", null, false);
	}
 }


 //add download the accounts to searchListView
 if (ZaController.initToolbarMethods["ZaSearchListController"]) {
    ZaOperation.DOWNLOAD_ACCOUNTS = ++ ZA_OP_INDEX ;
    ZaSearchListController.initExtraToolbarMethod = function () {
        this._toolbarOperations [ZaOperation.DOWNLOAD_ACCOUNTS] =
                new ZaOperation(ZaOperation.DOWNLOAD_ACCOUNTS, com_zimbra_bulkprovision.ACTBB_DownloadAccounts,
                        com_zimbra_bulkprovision.ACTBB_DownloadAccounts_tt, "DownloadGlobalConfig", "DownloadGlobalConfigDis",
                        new AjxListener(this, ZaSearchListController.prototype._downloadAccountsListener)
                        );

        for (var i=0; i < this._toolbarOrder.length; i ++) {
            if (this._toolbarOrder[i] == ZaOperation.NONE) {
                this._toolbarOrder.splice(i,0,ZaOperation.DOWNLOAD_ACCOUNTS) ;
                break ;
            }
        }
    }

    ZaController.initToolbarMethods["ZaSearchListController"].push(ZaSearchListController.initExtraToolbarMethod);
}

ZaSearchListController.prototype._downloadAccountsListener =
 function (ev) {
     //TODO: need to filter out non account items, such as domain, etc.
     if (AjxEnv.hasFirebug) console.log("Download all the search result accounts ...") ;
     var queryString = "?action=getSR";
     if (this._currentQuery) {
        queryString += "&q=" + AjxStringUtil.urlEncode(this._currentQuery) ;
     }

     if (ZaSearch._domain && AjxUtil.isDomainName(ZaSearch._domain)) {
        queryString += "&domain=" + AjxStringUtil.urlEncode(ZaSearch._domain) ;        
     }

     if (this.searchTypes) {
         queryString +="&types=" + AjxStringUtil.urlEncode(this.searchTypes.join(","));
     }

     window.open("/service/afd/" + queryString);
 }
