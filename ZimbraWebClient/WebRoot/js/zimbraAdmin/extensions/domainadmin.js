function ZaDomainAdmin() {

}
if(ZaAccount) {
	ZaAccount.A_zimbraIsDomainAdminAccount = "zimbraIsDomainAdminAccount";
	ZaAccount.myXModel.items.push({id:ZaAccount.A_zimbraIsDomainAdminAccount, type:_ENUM_, choices:ZaModel.BOOLEAN_CHOICES, ref:"attrs/"+ZaAccount.A_zimbraIsDomainAdminAccount});
}

if(ZaSettings) {
	ZaSettings.ADMIN_TYPE_COOKIE ="ZA_ADMIN_TYPE_COOKIE";
	ZaSettings.isDomainAdmin = false;

	ZaDomainAdmin.initSettings = function() {
		var adminType = AjxCookie.getCookie(document, ZaSettings.ADMIN_TYPE_COOKIE);
		if(adminType == "domain") { //TODO: Move this code to an external file
		    ZaSettings.isDomainAdmin = true;
		    ZaSettings.MONITORING_ENABLED = false;
		    ZaSettings.SYSTEM_CONFIG_ENABLED = false;
		    ZaSettings.SERVERS_ENABLED = false;
		    ZaSettings.SERVER_STATS_ENABLED = false;
			ZaSettings.STATUS_ENABLED = false;
		    ZaSettings.COSES_ENABLED= false;
		    ZaSettings.DOMAINS_ENABLED= false;
		    ZaSettings.ACCOUNTS_FEATURES_ENABLED = false;
		    ZaSettings.ACCOUNTS_RESTORE_ENABLED = false;
		    ZaSettings.ACCOUNTS_PREFS_ENABLED = false;
		    ZaSettings.ACCOUNTS_REINDEX_ENABLED = true;
		    ZaSettings.ACCOUNTS_ADVANCED_ENABLED = false;	
		    ZaSettings.ACCOUNTS_VIEW_MAIL_ENABLED = false;	
		    ZaSettings.GLOBAL_CONFIG_ENABLED = false;
		}	 
	}
	if(ZaSettings.initMethods)
		ZaSettings.initMethods.push(ZaDomainAdmin.initSettings); 
}
if(ZaTabView.XFormModifiers["ZaAccountXFormView"]) {
	ZaDomainAdmin.AccountXFormModifier = function(xFormObject) {
		//remove "Is Adminitrator checkbox" from first tab
		var domainAdminChkBx = {ref:ZaAccount.A_zimbraIsDomainAdminAccount,type:_CHECKBOX_, 
								msgName:ZaMsg.NAD_IsDomainAdminAccount,label:ZaMsg.NAD_IsDomainAdminAccount,labelLocation:_LEFT_, 
								trueValue:"TRUE", falseValue:"FALSE",
								labelCssClass:"xform_label",
								align:_LEFT_,
								relevantBehavior:_DISABLE_,
								relevant:"instance.attrs[ZaAccount.A_isAdminAccount]!=\'TRUE\'",
								onChange:ZaTabView.onFormFieldChanged
							};
		var tmpItems = xFormObject.items[2].items[0].items;
		var cnt = tmpItems.length;
		for(var i = 0; i < cnt; i ++) { 
			if(tmpItems[i] && tmpItems[i].ref == ZaAccount.A_isAdminAccount) {
				if(ZaSettings.isDomainAdmin) {
					//remove "Administrator" checkbox from Domain Admin's view
					xFormObject.items[2].items[0].items.splice(i,1);
				} else {
					//make sure Domain Admin and Administrator checkboxes are mutualy exclusive
					tmpItems[i].relevant = "!ZaSettings.isDomainAdmin";
					tmpItems[i].elementChanged = 
						function(elementValue,instanceValue, event) {
							if(elementValue == "TRUE") {
								this.setInstanceValue("FALSE", ZaAccount.A_zimbraIsDomainAdminAccount);
								}
								this.getForm().itemChanged(this, elementValue, event);
						};
				}
				//add Domain Admin checkbox
				xFormObject.items[2].items[0].items.splice(i,0,domainAdminChkBx);
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

if(ZaAuthenticate.processResponseMethods) {
	ZaDomainAdmin.processAuthResponse = function(resp) {
		var els = resp.childNodes;
		var len = els.length;
		for (var i = 0; i < len; i++) {
			var el = els[i];
			if (el.nodeName=="a") { 
				if(ZaAccount.A_zimbraIsDomainAdminAccount == el.getAttribute("n")) {
					var value = el.firstChild.nodeValue;
					if(value=="true") {
						AjxCookie.setCookie(document, ZaSettings.ADMIN_TYPE_COOKIE, "domain", null, "/");			    	
					} else {
						AjxCookie.setCookie(document, ZaSettings.ADMIN_TYPE_COOKIE, "super", null, "/");			    	
					}					
				}
			}
		}	
	}
	
	ZaAuthenticate.processResponseMethods.push(ZaDomainAdmin.processAuthResponse);
}

if(EmailAddr_XFormItem) {
	EmailAddr_XFormItem.prototype.items.splice(2,0,
		{type:_OUTPUT_,ref:".",relevant:"ZaSettings.isDomainAdmin", relevantBehavior:_HIDE_,
			choices:EmailAddr_XFormItem.domainChoices,
			getDisplayValue:function (itemVal){
				var val = null;
				if(itemVal) {
					var emailChunks = itemVal.split("@");
				
					if(emailChunks.length > 1 ) {
						val = emailChunks[1];
					} 
				}
				if(!val) {
					val = this.getChoices()._choiceObject[0].name;
				}	
				this.getParentItem()._domainPart = val;
				
				return val;
			}	
		});
	EmailAddr_XFormItem.prototype.items[3].relevant="!ZaSettings.isDomainAdmin";
}