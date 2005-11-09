/*
 * ***** BEGIN LICENSE BLOCK *****
 * Version: ZPL 1.1
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.1 ("License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.zimbra.com/license
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See
 * the License for the specific language governing rights and limitations
 * under the License.
 * 
 * The Original Code is: Zimbra Collaboration Suite Web Client
 * 
 * The Initial Developer of the Original Code is Zimbra, Inc.
 * Portions created by Zimbra are Copyright (C) 2005 Zimbra, Inc.
 * All Rights Reserved.
 * 
 * Contributor(s):
 * 
 * ***** END LICENSE BLOCK *****
 */

/**
* @class ZaAccount
* @contructor ZaAccount
* @param ZaApp app
* this class is a model for zimbraAccount ldap objects
* @author Roland Schemers
* @author Greg Solovyev
**/
function ZaAccount(app) {
	ZaItem.call(this, app);
	this.attrs = new Object();
	this.id = "";
	this.name="";
	this.attrs[ZaAccount.A_zimbraMailAlias] = new Array();
}

ZaAccount.prototype = new ZaItem;
ZaAccount.prototype.constructor = ZaAccount;

//object attributes
ZaAccount.A_name = "name";
ZaAccount.A_uid = "uid";
ZaAccount.A_accountName = "cn"; //contact name
ZaAccount.A_firstName = "givenName"; //first name
ZaAccount.A_lastName = "sn"; //last name
ZaAccount.A_mail = "mail";
ZaAccount.A_password = "password";
ZaAccount.A_description = "description";
ZaAccount.A_telephoneNumber = "telephoneNumber";
ZaAccount.A_displayname = "displayName";
ZaAccount.A_country = "co"; //country
ZaAccount.A_company = "company";
ZaAccount.A_initials = "initials"; //middle initial
ZaAccount.A_city = "l";
ZaAccount.A_orgUnit = "ou";
ZaAccount.A_office = "physicalDeliveryOfficeName";
ZaAccount.A_postalAddress = "postalAddress";
ZaAccount.A_zip = "postalCode";
ZaAccount.A_state = "st";
ZaAccount.A_mailDeliveryAddress = "zimbraMailDeliveryAddress";
ZaAccount.A_accountStatus = "zimbraAccountStatus";
ZaAccount.A_notes = "zimbraNotes";
ZaAccount.A_zimbraMailQuota = "zimbraMailQuota";
ZaAccount.A_mailHost = "zimbraMailHost";
ZaAccount.A_COSId = "zimbraCOSId";
ZaAccount.A_isAdminAccount = "zimbraIsAdminAccount";
ZaAccount.A_zimbraMinPwdLength="zimbraPasswordMinLength";
ZaAccount.A_zimbraMaxPwdLength="zimbraPasswordMaxLength";
ZaAccount.A_zimbraMinPwdAge="zimbraPasswordMinAge";
ZaAccount.A_zimbraMaxPwdAge="zimbraPasswordMaxAge";
ZaAccount.A_zimbraEnforcePwdHistory="zimbraPasswordEnforceHistory";
ZaAccount.A_zimbraMailAlias="zimbraMailAlias";
ZaAccount.A_zimbraMailForwardingAddress="zimbraMailForwardingAddress";
ZaAccount.A_zimbraPasswordMustChange="zimbraPasswordMustChange";
ZaAccount.A_zimbraPasswordLocked="zimbraPasswordLocked";
ZaAccount.A_zimbraDomainName = "zimbraDomainName";
ZaAccount.A_zimbraContactMaxNumEntries = "zimbraContactMaxNumEntries";
ZaAccount.A_zimbraAttachmentsBlocked = "zimbraAttachmentsBlocked";
ZaAccount.A_zimbraAttachmentsViewInHtmlOnly = "zimbraAttachmentsViewInHtmlOnly";
ZaAccount.A_zimbraAuthTokenLifetime = "zimbraAuthTokenLifetime";
ZaAccount.A_zimbraMailMessageLifetime = "zimbraMailMessageLifetime";
ZaAccount.A_zimbraMailSpamLifetime = "zimbraMailSpamLifetime";
ZaAccount.A_zimbraMailTrashLifetime = "zimbraMailTrashLifetime";

//prefs
ZaAccount.A_zimbraPrefCalendarAlwaysShowMiniCal = "zimbraPrefCalendarAlwaysShowMiniCal";
ZaAccount.A_zimbraPrefCalendarUseQuickAdd = "zimbraPrefCalendarUseQuickAdd";
ZaAccount.A_prefSaveToSent="zimbraPrefSaveToSent";
ZaAccount.A_prefMailSignature="zimbraPrefMailSignature";
ZaAccount.A_prefMailSignatureEnabled="zimbraPrefMailSignatureEnabled";
ZaAccount.A_zimbraPrefSentMailFolder = "zimbraPrefSentMailFolder";
ZaAccount.A_zimbraPrefGroupMailBy = "zimbraPrefGroupMailBy";
ZaAccount.A_zimbraPrefIncludeSpamInSearch = "zimbraPrefIncludeSpamInSearch";
ZaAccount.A_zimbraPrefIncludeTrashInSearch = "zimbraPrefIncludeTrashInSearch";
ZaAccount.A_zimbraPrefMailInitialSearch = "zimbraPrefMailInitialSearch";
ZaAccount.A_zimbraPrefMailItemsPerPage = "zimbraPrefMailItemsPerPage";
ZaAccount.A_zimbraPrefMailPollingInterval = "zimbraPrefMailPollingInterval";
ZaAccount.A_zimbraPrefOutOfOfficeReply = "zimbraPrefOutOfOfficeReply";
ZaAccount.A_zimbraPrefOutOfOfficeReplyEnabled = "zimbraPrefOutOfOfficeReplyEnabled";
ZaAccount.A_zimbraPrefReplyToAddress = "zimbraPrefReplyToAddress";
ZaAccount.A_zimbraPrefUseKeyboardShortcuts = "zimbraPrefUseKeyboardShortcuts";
ZaAccount.A_zimbraPrefContactsPerPage = "zimbraPrefContactsPerPage";
ZaAccount.A_zimbraMemberOf = "zimbraMemberOf";
ZaAccount.A_zimbraPrefComposeInNewWindow = "zimbraPrefComposeInNewWindow";
ZaAccount.A_zimbraPrefForwardReplyInOriginalFormat = "zimbraPrefForwardReplyInOriginalFormat";
ZaAccount.A_zimbraPrefAutoAddAddressEnabled = "zimbraPrefAutoAddAddressEnabled";
ZaAccount.A_zimbraPrefComposeFormat = "zimbraPrefComposeFormat";
ZaAccount.A_zimbraPrefMessageViewHtmlPreferred = "zimbraPrefMessageViewHtmlPreferred";
ZaAccount.A_zimbraPrefNewMailNotificationAddress = "zimbraPrefNewMailNotificationAddress";
ZaAccount.A_zimbraPrefNewMailNotificationEnabled = "zimbraPrefNewMailNotificationEnabled";
ZaAccount.A_zimbraPrefOutOfOfficeReply = "zimbraPrefOutOfOfficeReply";
ZaAccount.A_zimbraPrefOutOfOfficeReplyEnabled = "zimbraPrefOutOfOfficeReplyEnabled";
ZaAccount.A_zimbraPrefShowSearchString = "zimbraPrefShowSearchString";
ZaAccount.A_zimbraPrefMailSignatureStyle = "zimbraPrefMailSignatureStyle";
ZaAccount.A_zimbraPrefUseTimeZoneListInCalendar = "zimbraPrefUseTimeZoneListInCalendar";
ZaAccount.A_zimbraPrefImapSearchFoldersEnabled = "zimbraPrefImapSearchFoldersEnabled";

//features
ZaAccount.A_zimbraFeatureContactsEnabled="zimbraFeatureContactsEnabled";
ZaAccount.A_zimbraFeatureCalendarEnabled="zimbraFeatureCalendarEnabled";
ZaAccount.A_zimbraFeatureTaggingEnabled="zimbraFeatureTaggingEnabled";
ZaAccount.A_zimbraFeatureAdvancedSearchEnabled="zimbraFeatureAdvancedSearchEnabled";
ZaAccount.A_zimbraFeatureSavedSearchesEnabled="zimbraFeatureSavedSearchesEnabled";
ZaAccount.A_zimbraFeatureConversationsEnabled="zimbraFeatureConversationsEnabled";
ZaAccount.A_zimbraFeatureChangePasswordEnabled="zimbraFeatureChangePasswordEnabled";
ZaAccount.A_zimbraFeatureInitialSearchPreferenceEnabled="zimbraFeatureInitialSearchPreferenceEnabled";
ZaAccount.A_zimbraFeatureFiltersEnabled="zimbraFeatureFiltersEnabled";
ZaAccount.A_zimbraFeatureGalEnabled="zimbraFeatureGalEnabled";
ZaAccount.A_zimbraAttachmentsIndexingEnabled = "zimbraAttachmentsIndexingEnabled";
ZaAccount.A_zimbraFeatureHtmlComposeEnabled = "zimbraFeatureHtmlComposeEnabled";
ZaAccount.A_zimbraImapEnabled = "zimbraImapEnabled";
ZaAccount.A_zimbraPop3Enabled = "zimbraPop3Enabled";

//readonly
ZaAccount.A_zimbraLastLogonTimestamp = "zimbraLastLogonTimestamp";
ZaAccount.A_zimbraPasswordModifiedTime = "zimbraPasswordModifiedTime";


ZaAccount.ACCOUNT_STATUS_ACTIVE = "active";
ZaAccount.ACCOUNT_STATUS_MAINTENANCE = "maintenance";
ZaAccount.ACCOUNT_STATUS_LOCKED = "locked";
ZaAccount.ACCOUNT_STATUS_CLOSED = "closed";

//this attributes are not used in the XML object, but is used in the model
ZaAccount.A2_confirmPassword = "confirmPassword";
ZaAccount.A2_mbxsize = "mbxSize";
ZaAccount.A2_quota = "quota2";
ZaAccount.A2_autodisplayname = "autodisplayname";
ZaAccount.A2_autoMailServer = "automailserver";
ZaAccount.A2_myCOS = "mycos";
ZaAccount.A2_newAlias = "newalias";
//ZaAccount.A2_newForward = "newforward";
ZaAccount.A2_aliases = "aliases";
ZaAccount.A2_forwarding = "forwardings";

ZaAccount.MAXSEARCHRESULTS = "500";
ZaAccount.RESULTSPERPAGE = "25";

ZaAccount.checkValues = 
function(tmpObj, app) {
	/**
	* check values
	**/

	if(tmpObj.name == null || tmpObj.name.length < 1) {
		//show error msg
		app.getCurrentController().popupErrorDialog(ZaMsg.ERROR_ACCOUNT_NAME_REQUIRED);
		return false;
	}
	
	if(tmpObj.attrs[ZaAccount.A_lastName] == null || tmpObj.attrs[ZaAccount.A_lastName].length < 1) {
		//show error msg
		app.getCurrentController().popupErrorDialog(ZaMsg.ERROR_ACCOUNT_LAST_NAME_REQUIRED);
		return false;
	}

	var emailRegEx = /^([a-zA-Z0-9_\-])+((\.)?([a-zA-Z0-9_\-])+)*@(([a-zA-Z0-9\-])+\.)+([a-zA-Z0-9]{2,4})+$/;
	if(!emailRegEx.test(tmpObj.name) ) {
		//show error msg
		app.getCurrentController().popupErrorDialog(ZaMsg.ERROR_ACCOUNT_NAME_INVALID);
		return false;
	}
	
	var myCos = null;
	var maxPwdLen = Number.POSITIVE_INFINITY;
	var minPwdLen = 1;	
	
	//find out what is this account's COS
	var cosList = app.getCosList().getArray();
	for(var ix in cosList) {
		if(cosList[ix].id == tmpObj.attrs[ZaAccount.A_COSId]) {
			myCos = cosList[ix];
			break;
		}
	}
	//if the account did not have a valid cos id - pick the first COS
	if(!myCos && cosList.length > 0) {
		myCos = cosList[0];
		tmpObj.attrs[ZaAccount.A_COSId] = cosList[0].id;
	}		
	//validate password length against this account's COS setting
	if(tmpObj.attrs[ZaAccount.A_zimbraMinPwdLength] != null) {
		minPwdLen = tmpObj.attrs[ZaAccount.A_zimbraMinPwdLength];
	} else {
		if(myCos) {
			if(myCos.attrs[ZaCos.A_zimbraMinPwdLength] > 0) {
				minPwdLen = myCos.attrs[ZaCos.A_zimbraMinPwdLength];
			}
		}
	}
	
	if(tmpObj.attrs[ZaAccount.A_zimbraMaxPwdLength] != null) {
		maxPwdLen = tmpObj.attrs[ZaAccount.A_zimbraMaxPwdLength];
	} else {
		if(myCos) {
			if(myCos.attrs[ZaCos.A_zimbraMaxPwdLength] > 0) {
				maxPwdLen = myCos.attrs[ZaCos.A_zimbraMaxPwdLength];
			}		
		}
	}
	//if there is a password - validate it
	if(tmpObj.attrs[ZaAccount.A_password]!=null || tmpObj[ZaAccount.A2_confirmPassword]!=null) {
		if(tmpObj.attrs[ZaAccount.A_password] != tmpObj[ZaAccount.A2_confirmPassword]) {
			//show error msg
			app.getCurrentController().popupErrorDialog(ZaMsg.ERROR_PASSWORD_MISMATCH);
			return false;
		} 			
		if(tmpObj.attrs[ZaAccount.A_password].length < minPwdLen || AjxStringUtil.trim(tmpObj.attrs[ZaAccount.A_password]).length < minPwdLen) { 
			//show error msg
			app.getCurrentController().popupErrorDialog(ZaMsg.ERROR_PASSWORD_TOOSHORT + "<br>" + String(ZaMsg.NAD_passMinLengthMsg).replace("{0}",minPwdLen));
			return false;		
		}
		
		if(AjxStringUtil.trim(tmpObj.attrs[ZaAccount.A_password]).length > maxPwdLen) { 
			//show error msg
			app.getCurrentController().popupErrorDialog(ZaMsg.ERROR_PASSWORD_TOOLONG+ "<br>" + String(ZaMsg.NAD_passMaxLengthMsg).replace("{0}",maxPwdLen));
			return false;		
		}
	} 		
	if(tmpObj.attrs[ZaAccount.A_zimbraMailQuota] != "" && tmpObj.attrs[ZaAccount.A_zimbraMailQuota] !=null && !AjxUtil.isNonNegativeInteger(tmpObj.attrs[ZaAccount.A_zimbraMailQuota])) {
		//show error msg
		app.getCurrentController().popupErrorDialog(ZaMsg.ERROR_INVALID_VALUE + ": " + ZaMsg.NAD_MailQuota + " ! ");
		return false;
	}

	if(tmpObj.attrs[ZaAccount.A_zimbraContactMaxNumEntries] != "" && tmpObj.attrs[ZaAccount.A_zimbraContactMaxNumEntries] !=null && !AjxUtil.isNonNegativeInteger(tmpObj.attrs[ZaAccount.A_zimbraContactMaxNumEntries])) {
		//show error msg
		app.getCurrentController().popupErrorDialog(ZaMsg.ERROR_INVALID_VALUE + ": " + ZaMsg.NAD_ContactMaxNumEntries + " ! ");
		return false;
	}
	
	if(tmpObj.attrs[ZaAccount.A_zimbraContactMaxNumEntries])
		tmpObj.attrs[ZaAccount.A_zimbraContactMaxNumEntries] = parseInt	(tmpObj.attrs[ZaAccount.A_zimbraContactMaxNumEntries]);
	
	if(tmpObj.attrs[ZaAccount.A_zimbraMinPwdLength] != "" && tmpObj.attrs[ZaAccount.A_zimbraMinPwdLength] !=null && !AjxUtil.isNonNegativeInteger(tmpObj.attrs[ZaAccount.A_zimbraMinPwdLength])) {
		//show error msg
		app.getCurrentController().popupErrorDialog(ZaMsg.ERROR_INVALID_VALUE + ": " + ZaMsg.NAD_passMinLength + " ! ");
		return false;
	}
	
	if(tmpObj.attrs[ZaAccount.A_zimbraMaxPwdLength] != "" && tmpObj.attrs[ZaAccount.A_zimbraMaxPwdLength] !=null && !AjxUtil.isNonNegativeInteger(tmpObj.attrs[ZaAccount.A_zimbraMaxPwdLength])) {
		//show error msg
		app.getCurrentController().popupErrorDialog(ZaMsg.ERROR_INVALID_VALUE + ": " + ZaMsg.NAD_passMaxLength + " ! ");
		return false;
	}	
	
	if(parseInt(tmpObj.attrs[ZaAccount.A_zimbraMaxPwdLength]) < parseInt(tmpObj.attrs[ZaAccount.A_zimbraMinPwdLength]) && parseInt(tmpObj.attrs[ZaAccount.A_zimbraMaxPwdLength]) > 0) {
		//show error msg
		app.getCurrentController().popupErrorDialog(ZaMsg.ERROR_MAX_MIN_PWDLENGTH);

		return false;
	}	
	if(tmpObj.attrs[ZaAccount.A_zimbraMaxPwdLength])
		tmpObj.attrs[ZaAccount.A_zimbraMaxPwdLength] = parseInt(tmpObj.attrs[ZaAccount.A_zimbraMaxPwdLength]);
	
	if(tmpObj.attrs[ZaAccount.A_zimbraMinPwdLength])
		tmpObj.attrs[ZaAccount.A_zimbraMinPwdLength] = parseInt(tmpObj.attrs[ZaAccount.A_zimbraMinPwdLength]);
		
	if(tmpObj.attrs[ZaAccount.A_zimbraMinPwdAge] != "" && tmpObj.attrs[ZaAccount.A_zimbraMinPwdAge] !=null && !AjxUtil.isNonNegativeInteger(tmpObj.attrs[ZaAccount.A_zimbraMinPwdAge])) {
		//show error msg
		app.getCurrentController().popupErrorDialog(ZaMsg.ERROR_INVALID_VALUE + ": " + ZaMsg.NAD_passMinAge + " ! ");

		return false;
	}		
	
	if(tmpObj.attrs[ZaAccount.A_zimbraMaxPwdAge] != "" && tmpObj.attrs[ZaAccount.A_zimbraMaxPwdAge] !=null && !AjxUtil.isNonNegativeInteger(tmpObj.attrs[ZaAccount.A_zimbraMaxPwdAge])) {
		//show error msg
		app.getCurrentController().popupErrorDialog(ZaMsg.ERROR_INVALID_VALUE + ": " + ZaMsg.NAD_passMaxAge + " ! ");

		return false;
	}		
	
	if(parseInt(tmpObj.attrs[ZaCos.A_zimbraMaxPwdAge]) < parseInt(tmpObj.attrs[ZaAccount.A_zimbraMinPwdAge]) && parseInt(tmpObj.attrs[ZaCos.A_zimbraMaxPwdAge]) > 0) {
		//show error msg
		app.getCurrentController().popupErrorDialog(ZaMsg.ERROR_MAX_MIN_PWDAGE);

		return false;
	}
	if(tmpObj.attrs[ZaAccount.A_zimbraMinPwdAge])
		tmpObj.attrs[ZaAccount.A_zimbraMinPwdAge] = parseInt(tmpObj.attrs[ZaAccount.A_zimbraMinPwdAge]);
	
	if(tmpObj.attrs[ZaCos.A_zimbraMaxPwdAge])
		tmpObj.attrs[ZaCos.A_zimbraMaxPwdAge] = parseInt(tmpObj.attrs[ZaCos.A_zimbraMaxPwdAge]);
	
	if(tmpObj.attrs[ZaAccount.A_zimbraAuthTokenLifetime] != "" && tmpObj.attrs[ZaAccount.A_zimbraAuthTokenLifetime] !=null && !AjxUtil.isLifeTime(tmpObj.attrs[ZaAccount.A_zimbraAuthTokenLifetime])) {
		//show error msg
		app.getCurrentController().popupErrorDialog(ZaMsg.ERROR_INVALID_VALUE + ": " + ZaMsg.NAD_AuthTokenLifetime + " ! ");

		return false;
	}
	
	if(tmpObj.attrs[ZaAccount.A_zimbraMailMessageLifetime] != "" && tmpObj.attrs[ZaAccount.A_zimbraMailMessageLifetime] !=null && !AjxUtil.isLifeTime(tmpObj.attrs[ZaAccount.A_zimbraMailMessageLifetime])) {
		//show error msg
		app.getCurrentController().popupErrorDialog(ZaMsg.ERROR_INVALID_VALUE + ": " + ZaMsg.NAD_MailMessageLifetime + " ! ");

		return false;
	}			

	if(tmpObj.attrs[ZaAccount.A_zimbraMailTrashLifetime] != "" && tmpObj.attrs[ZaAccount.A_zimbraMailTrashLifetime] !=null && !AjxUtil.isLifeTime(tmpObj.attrs[ZaAccount.A_zimbraMailTrashLifetime])) {
		//show error msg
		app.getCurrentController().popupErrorDialog(ZaMsg.ERROR_INVALID_VALUE + ": " + ZaMsg.NAD_MailTrashLifetime + " ! ");

		return false;
	}	
	
	if(tmpObj.attrs[ZaAccount.A_zimbraMailSpamLifetime] != "" && tmpObj.attrs[ZaAccount.A_zimbraMailSpamLifetime] !=null && !AjxUtil.isLifeTime(tmpObj.attrs[ZaAccount.A_zimbraMailSpamLifetime])) {
		//show error msg
		app.getCurrentController().popupErrorDialog(ZaMsg.ERROR_INVALID_VALUE + ": " + ZaMsg.NAD_MailSpamLifetime + " ! ");
		
		return false;
	}		

	if(tmpObj.attrs[ZaAccount.A_zimbraPrefContactsPerPage] != "" && tmpObj.attrs[ZaAccount.A_zimbraPrefContactsPerPage] !=null && !AjxUtil.isNonNegativeInteger(tmpObj.attrs[ZaAccount.A_zimbraPrefContactsPerPage])) {
		//show error msg
		app.getCurrentController().popupErrorDialog(ZaMsg.ERROR_INVALID_VALUE + ": " + ZaMsg.NAD_PrefContactsPerPage + " ! ");

		return false;
	}	
	if(tmpObj.attrs[ZaAccount.A_zimbraPrefContactsPerPage])
		tmpObj.attrs[ZaAccount.A_zimbraPrefContactsPerPage] = parseInt(tmpObj.attrs[ZaAccount.A_zimbraPrefContactsPerPage]);

	if(tmpObj.attrs[ZaAccount.A_passEnforceHistory] != "" && tmpObj.attrs[ZaAccount.A_passEnforceHistory] !=null && !AjxUtil.isNonNegativeInteger(tmpObj.attrs[ZaAccount.A_passEnforceHistory])) {
		//show error msg
		app.getCurrentController().popupErrorDialog(ZaMsg.ERROR_INVALID_VALUE + ": " + ZaMsg.NAD_passEnforceHistory + " ! ");

		return false;
	}	
	if(tmpObj.attrs[ZaAccount.A_passEnforceHistory])
		tmpObj.attrs[ZaAccount.A_passEnforceHistory] = parseInt(tmpObj.attrs[ZaAccount.A_passEnforceHistory]);
	return true;
}
/**
* Creates a new ZaAccount. This method makes SOAP request to create a new account record. 
* @param attrs
* @param name 
* @param password
* @return ZaAccount
**/
ZaAccount.create =
function(tmpObj, app) {
	
	tmpObj.attrs[ZaAccount.A_mail] = tmpObj.name;	
		
	//create SOAP request
	var soapDoc = AjxSoapDoc.create("CreateAccountRequest", "urn:zimbraAdmin", null);
	soapDoc.set(ZaAccount.A_name, tmpObj.name);
	if(tmpObj.attrs[ZaAccount.A_password] && tmpObj.attrs[ZaAccount.A_password].length > 0)
		soapDoc.set(ZaAccount.A_password, tmpObj.attrs[ZaAccount.A_password]);
		
	if(tmpObj[ZaAccount.A2_autoMailServer] == "TRUE") {
		tmpObj.attrs[ZaAccount.A_mailHost] = null;
	}
	for (var aname in tmpObj.attrs) {
		if(aname == ZaAccount.A_password || aname == ZaAccount.A_zimbraMailAlias || aname == ZaItem.A_objectClass || aname == ZaAccount.A2_mbxsize || aname == ZaAccount.A_mail) {
			continue;
		}	
		
		if(tmpObj.attrs[aname] instanceof Array) {
			var cnt = tmpObj.attrs[aname].length;
			if(cnt) {
				for(var ix=0; ix <cnt; ix++) {
					var attr = soapDoc.set("a", tmpObj.attrs[aname][ix]);
					attr.setAttribute("n", aname);
				}
			} 
		} else {	
			if(tmpObj.attrs[aname] != null) {
				var attr = soapDoc.set("a", tmpObj.attrs[aname]);
				attr.setAttribute("n", aname);
			}
		}
	}
	try {
		var resp = ZmCsfeCommand.invoke(soapDoc, null, null, null, true).firstChild;
	} catch (ex) {
		switch(ex.code) {
			case ZmCsfeException.ACCT_EXISTS:
				app.getCurrentController().popupErrorDialog(ZaMsg.ERROR_ACCOUNT_EXISTS);
			break;
			case ZmCsfeException.ACCT_INVALID_PASSWORD:
				app.getCurrentController().popupErrorDialog(ZaMsg.ERROR_PASSWORD_INVALID);
			break;
			default:
				app.getCurrentController()._handleException(ex, "ZaAccount.create", null, false);
			break;
		}
		return null;
	}
	var account = new ZaAccount(app);
	account.initFromDom(resp.firstChild);
	
	//add aliases
	if(tmpObj.attrs[ZaAccount.A_zimbraMailAlias].length) {
		var tmpObjCnt = tmpObj.attrs[ZaAccount.A_zimbraMailAlias].length;
		var failedAliases = "";
		var failedAliasesCnt = 0;
		try {
			for(var ix=0; ix < tmpObjCnt; ix++) {
				try {
					account.addAlias(tmpObj.attrs[ZaAccount.A_zimbraMailAlias][ix]);
					account.attrs[ZaAccount.A_zimbraMailAlias].push(tmpObj.attrs[ZaAccount.A_zimbraMailAlias][ix]);
				} catch (ex) {
					if(ex.code == ZmCsfeException.ACCT_EXISTS) {
						//if failed because account exists just show a warning
						failedAliases += ("<br>" + tmpObj.attrs[ZaAccount.A_zimbraMailAlias][ix]);
						failedAliasesCnt++;
					} else {
						//if failed for another reason - jump out
						throw (ex);
					}
				}
			}
			if(failedAliasesCnt == 1) {
				app.getCurrentController().popupErrorDialog(ZaMsg.WARNING_ALIAS_EXISTS + failedAliases);
			} else if(failedAliasesCnt > 1) {
				app.getCurrentController().popupErrorDialog(ZaMsg.WARNING_ALIASES_EXIST + failedAliases);
			}
		} catch (ex) {
			app.getCurrentController().popupErrorDialog(ZaMsg.FAILED_ADD_ALIASES, ex);
			return null;
		}	
	}	
	return account;
}

/**
* @method modify
* Updates ZaAccount attributes (SOAP)
* @param mods set of modified attributes and their new values
*/
ZaAccount.prototype.modify =
function(mods) {
	//update the object
	var soapDoc = AjxSoapDoc.create("ModifyAccountRequest", "urn:zimbraAdmin", null);
	soapDoc.set("id", this.id);
	for (var aname in mods) {
		//multy value attribute
		if(mods[aname] instanceof Array) {
			var cnt = mods[aname].length;
			if(cnt) {
				for(var ix=0; ix <cnt; ix++) {
					if(mods[aname][ix]) { //if there is an empty element in the array - don't send it
						var attr = soapDoc.set("a", mods[aname][ix]);
						attr.setAttribute("n", aname);
					}
				}
			} else {
				var attr = soapDoc.set("a", "");
				attr.setAttribute("n", aname);
			}
		} else {
			var attr = soapDoc.set("a", mods[aname]);
			attr.setAttribute("n", aname);
		}
	}

	var resp = ZmCsfeCommand.invoke(soapDoc, null, null, null, true).firstChild;
	//update itseld
	this.initFromDom(resp.firstChild);
	this[ZaAccount.A2_confirmPassword] = null;
	return;
}


/**
* @param newAlias
* addAlias adds one alias to the account. Adding each alias takes separate Soap Request
**/
ZaAccount.prototype.addAlias = 
function (newAlias) {
	var soapDoc = AjxSoapDoc.create("AddAccountAliasRequest", "urn:zimbraAdmin", null);
	soapDoc.set("id", this.id);
	soapDoc.set("alias", newAlias);	
	ZmCsfeCommand.invoke(soapDoc, null, null, null, true);	
}

/**
* @param aliasToRemove
* addAlias adds one alias to the account. Adding each alias takes separate Soap Request
**/
ZaAccount.prototype.removeAlias = 
function (aliasToRemove) {
	var soapDoc = AjxSoapDoc.create("RemoveAccountAliasRequest", "urn:zimbraAdmin", null);
	soapDoc.set("id", this.id);
	soapDoc.set("alias", aliasToRemove);	
	ZmCsfeCommand.invoke(soapDoc, null, null, null, true);	
}



ZaAccount.getViewMailLink = 
function(accId) {
	var retVal={authToken:"", lifetime:0};
	var soapDoc = AjxSoapDoc.create("DelegateAuthRequest", "urn:zimbraAdmin", null);	
	var attr = soapDoc.set("account", accId);
	attr.setAttribute("by", "id");
	var resp = ZmCsfeCommand.invoke(soapDoc, null, null, null, true).firstChild;	
	var children = resp.childNodes;
	for (var i=0; i< children.length;  i++) {
		child = children[i];
		if(child.nodeName == "authToken") {
			if(child.firstChild != null)
				retVal.authToken = child.firstChild.nodeValue;
		} else if (child.nodeName == "lifetime") {
			if(child.firstChild != null)
				retVal.lifetime= child.firstChild.nodeValue;
		}
	}
	return retVal;
}

function ZaReindexMailbox() {
	this.status = null;
	this.numSucceeded = 0;
	this.numFailed = 0;	
	this.numRemaining = 0;	
	this.numTotal = 100;	
	this.numDone = 0;	
	this.progressMsg = ZaMsg.NAD_ACC_ReindexingNotRunning;
	this.mbxId = null;
	this.resultMsg = null;
	this.errorDetail = null;
	this.pollInterval = 500;	
}

ZaReindexMailbox.myXModel = {
	items: [
		{id:"status", ref:"status", type:_STRING_},						
		{id:"numSucceeded", ref:"numSucceeded", type:_NUMBER_},								
		{id:"numFailed", ref:"numFailed", type:_NUMBER_},										
		{id:"numRemaining", ref:"numRemaining", type:_NUMBER_},												
		{id:"mbxId", ref:"mbxId", type:_STRING_},														
		{id:"numTotal", ref:"numTotal", type:_NUMBER_},			
		{id:"numDone", ref:"numDone", type:_NUMBER_},					
		{id:"numDone", ref:"numDone", type:_NUMBER_},							
		{id:"pollInterval", ref:"pollInterval", type:_STRING_},
		{id:"progressMsg", ref:"progressMsg", type:_STRING_}
	]
};


ZaAccount.getReindexStatus = 
function (mbxId, callback) {
	var soapDoc = AjxSoapDoc.create("ReIndexRequest", "urn:zimbraAdmin", null);
	soapDoc.getMethod().setAttribute("action", "status");
	var attr = soapDoc.set("mbox", null);
	attr.setAttribute("id", mbxId);
	var resp = null;
	try {
		if(callback) {
			var asynCommand = new ZmCsfeAsynchCommand();
			asynCommand.addInvokeListener(callback);
			asynCommand.invoke(soapDoc, null, null, null, true);			
			return asynCommand;
		} else {
			resp = ZmCsfeCommand.invoke(soapDoc, null, null, null, true);
		}
	} catch (ex) {
		if(ex.code == "service.NOT_IN_PROGRESS") {
			resp = null;
		} else {
			throw (ex);
		}
	}
	return resp;
}

ZaAccount.startReindexMailbox = 
function (mbxId) {
	var soapDoc = AjxSoapDoc.create("ReIndexRequest", "urn:zimbraAdmin", null);
	soapDoc.getMethod().setAttribute("action", "start");
	var attr = soapDoc.set("mbox", null);
	attr.setAttribute("id", mbxId);
	
	var resp;
	try {
		resp = ZmCsfeCommand.invoke(soapDoc, null, null, null, true);
	} catch (ex) {
		resp = ex;
	}
	return resp;
}

ZaAccount.abortReindexMailbox = 
function (mbxId) {
	var soapDoc = AjxSoapDoc.create("ReIndexRequest", "urn:zimbraAdmin", null);
	soapDoc.getMethod().setAttribute("action", "cancel");
	var attr = soapDoc.set("mbox", null);
	attr.setAttribute("id", mbxId);
	var resp;
	try {
		resp = ZmCsfeCommand.invoke(soapDoc, null, null, null, true);
	} catch (ex) {
		resp = ex;
	}
	return resp;
}

ZaAccount.parseReindexResponse = 
function (arg, respObj) {
//	var numFailed, numSucceeded,numRemaining,numTotal,numDone,resultMsg,errorDetail,status;
	if (!respObj)
		respObj = new ZaReindexMailbox();

	if(!arg) {
		respObj.status = null;
		return;
	}
		
	if(arg instanceof AjxException || arg instanceof ZmCsfeException || arg instanceof AjxSoapException) {
		if(arg.code && arg.code == "service.NOT_IN_PROGRESS") {
			respObj.status = null;
			respObj.errorDetail = "";
			respObj.resultMsg = "";	
			respObj.progressMsg = ZaMsg.NAD_ACC_ReindexingNotRunning;
			if(respObj.numRemaining > 0) {
				respObj.numDone = respObj.numTotal;
			}
		} else {
			respObj.resultMsg = String(ZaMsg.FAILED_REINDEX).replace("{0}", arg.code);
			respObj.errorDetail = arg.detail;
			respObj.status = "error";	
		}
	} else {
		var node;
		if(arg instanceof AjxSoapDoc) {
			node = arg.getBody().firstChild;
		} else {
			node = arg.firstChild;
		}
		var status = node.getAttribute("status");
		respObj.status = status;
		if(status == "cancelled") {
			respObj.progressMsg = ZaMsg.NAD_ACC_ReindexingCancelled;
		}		
		if(node.firstChild) {
			respObj.numFailed = parseInt(node.firstChild.getAttribute("numFailed"));
			respObj.numSucceeded = parseInt(node.firstChild.getAttribute("numSucceeded"));
			respObj.numRemaining = parseInt(node.firstChild.getAttribute("numRemaining"));
			respObj.numTotal = respObj.numRemaining + respObj.numFailed + respObj.numSucceeded;
			respObj.numDone  = respObj.numFailed + respObj.numSucceeded;	
			respObj.progressMsg = String(ZaMsg.NAD_ACC_ReindexingStatus).replace("{0}", respObj.numSucceeded).replace("{1}",respObj.numRemaining).replace("{2}", respObj.numFailed);
			if(status == "cancelled") {
				respObj.progressMsg = respObj.progressMsg + "<br>" + ZaMsg.NAD_ACC_ReindexingCancelled;
			}			
			if(respObj.numRemaining == 0) {
				respObj.numDone = respObj.numTotal;
			}
			//temp fix 
			/*if (respObj.numRemaining > 0)
				respObj.status = "running";
			*/
		}
	}
}

ZaAccount.prototype.initFromDom =
function(node) {
	this.name = node.getAttribute("name");
	this.id = node.getAttribute("id");
	this.attrs[ZaAccount.A_zimbraMailAlias] = new Array();
	this.attrs[ZaAccount.A_zimbraMailForwardingAddress] = new Array();
	var children = node.childNodes;
	for (var i=0; i< children.length;  i++) {
		child = children[i];
		if (child.nodeName != 'a') continue;
		var name = child.getAttribute("n");
		if (child.firstChild != null) {
			var value = child.firstChild.nodeValue;
			if (name in this.attrs) {
				var vc = this.attrs[name];
				if ((typeof vc) == "object") {
					vc.push(value);
				} else {
					this.attrs[name] = [vc, value];
				}
			} else {
				this.attrs[name] = value;
			}
		}
	}

}



/**
* Returns HTML for a tool tip for this account.
*/
ZaAccount.prototype.getToolTip =
function() {
	// update/null if modified
	if (!this._toolTip) {
		var html = new Array(20);
		var idx = 0;
		html[idx++] = "<table cellpadding='0' cellspacing='0' border='0'>";
		html[idx++] = "<tr valign='center'><td colspan='2' align='left'>";
		html[idx++] = "<div style='border-bottom: 1px solid black; white-space:nowrap; overflow:hidden;width:350' >";
		html[idx++] = "<table cellpadding='0' cellspacing='0' border='0' style='width:100%;'>";
		html[idx++] = "<tr valign='center'>";
		html[idx++] = "<td><b>" + AjxStringUtil.htmlEncode(this.name) + "</b></td>";
		html[idx++] = "<td align='right'>";
		html[idx++] = AjxImg.getImageHtml("Account");		
		html[idx++] = "</td>";
		html[idx++] = "</table></div></td></tr>";
		html[idx++] = "<tr></tr>";
		idx = this._addRow(ZaItem._attrDesc(ZaAccount.A_accountStatus), 
						ZaAccount._accountStatus(this.attrs[ZaAccount.A_accountStatus]), html, idx);
		// TODO: COS
		idx = this._addAttrRow(ZaAccount.A_displayname, html, idx);
		idx = this._addAttrRow(ZaItem.A_zimbraId, html, idx);
		idx = this._addAttrRow(ZaAccount.A_mailHost, html, idx);
		html[idx++] = "</table>";
		this._toolTip = html.join("");
	}
	return this._toolTip;
}

ZaAccount.prototype.remove = 
function() {
	var soapDoc = AjxSoapDoc.create("DeleteAccountRequest", "urn:zimbraAdmin", null);
	soapDoc.set("id", this.id);

	//find out which server I am on
	var myServer = this._app.getServerByName(this.attrs[ZaAccount.A_mailHost]);

	ZmCsfeCommand.invoke(soapDoc, null, null, myServer.id, true);	
}


ZaAccount.prototype.load = 
function(by, val, withCos) {
	var soapDoc = AjxSoapDoc.create("GetAccountRequest", "urn:zimbraAdmin", null);
	if(withCos) {
		soapDoc.getMethod().setAttribute("applyCos", "1");	
	} else {
		soapDoc.getMethod().setAttribute("applyCos", "0");		
	}
	var elBy = soapDoc.set("account", val);
	elBy.setAttribute("by", by);
/*
	var cmd = new ZmCsfeCommand();
	var params = {"soapDoc":soapDoc, "useXml":false};
	var resp = cmd.invoke(params);
	//var resp = cmd.invoke(params).firstChild;
*/
/*
	var cmd = new ZmCsfeCommand();
	var params = {"soapDoc":soapDoc, "useXml":true, "returnXml":true};
	var resp = cmd.invoke(params);
	this.initFromDom(resp.getBody().firstChild.firstChild);
*/	

	var resp = ZmCsfeCommand.invoke(soapDoc, false, null, null, true).firstChild;	
	this.attrs = new Object();
	this.initFromDom(resp.firstChild);
	
	var soapDoc = AjxSoapDoc.create("GetMailboxRequest", "urn:zimbraAdmin", null);
	var mbox = soapDoc.set("mbox", "");
	mbox.setAttribute("id", this.attrs[ZaItem.A_zimbraId]);
	//find out which server I am on
	var myServer = this._app.getServerByName(this.attrs[ZaAccount.A_mailHost]);
				
	var resp = ZmCsfeCommand.invoke(soapDoc, false, null, myServer.id, true);
	if(resp && resp.firstChild && resp.firstChild.firstChild) {
		this.attrs[ZaAccount.A2_mbxsize] = resp.firstChild.firstChild.getAttribute("s");
	}
	this[ZaAccount.A2_confirmPassword] = null;
	
	var autoDispName;
	if(this.attrs[ZaAccount.A_firstName])
		autoDispName = this.attrs[ZaAccount.A_firstName];
	else
		autoDispName = "";
		
	if(this.attrs[ZaAccount.A_initials]) {
		autoDispName += " ";
		autoDispName += this.attrs[ZaAccount.A_initials];
		autoDispName += ".";
	}
	if(this.attrs[ZaAccount.A_lastName]) {
		if(autoDispName.length > 0)
			autoDispName += " ";
			
	    autoDispName += this.attrs[ZaAccount.A_lastName];
	} 	
	
	if( autoDispName == this.attrs[ZaAccount.A_displayname]) {
		this[ZaAccount.A2_autodisplayname] = "TRUE";
	} else {
		this[ZaAccount.A2_autodisplayname] = "FALSE";
	}
}

ZaAccount.prototype.refresh = 
function(withCos) {
	this.load("id", this.id, withCos);
	
}

/**
* public rename; sends RenameAccountRequest soap request
**/
ZaAccount.prototype.rename = 
function (newName) {
	var soapDoc = AjxSoapDoc.create("RenameAccountRequest", "urn:zimbraAdmin", null);
	soapDoc.set("id", this.id);
	soapDoc.set("newName", newName);	
	ZmCsfeCommand.invoke(soapDoc, null, null, null, true);	
}

/**
* private _changePassword; sends SetPasswordRequest soap request
* @param newPassword
**/
ZaAccount.prototype.changePassword = 
function (newPassword) {
	var soapDoc = AjxSoapDoc.create("SetPasswordRequest", "urn:zimbraAdmin", null);
	soapDoc.set("id", this.id);
	soapDoc.set("newPassword", newPassword);	
	ZmCsfeCommand.invoke(soapDoc, null, null, null, true);
}

/*
function ZaAccountQuery (queryString, byDomain, byVal) {
	this.query = queryString;
	this.isByDomain = byDomain;
	this.byValAttr = byVal;
}*/

/**
* ZaAccount.myXModel - XModel for XForms
**/
ZaAccount.myXModel = new Object();
ZaAccount.myXModel.items = new Array();
ZaAccount.myXModel.items.push({id:ZaAccount.A_name, type:_STRING_, ref:"name", required:true, pattern:/^([a-zA-Z0-9_\-])+((\.)?([a-zA-Z0-9_\-])+)*@(([a-zA-Z0-9\-])+\.)+([a-zA-Z0-9]{2,4})+$/});
ZaAccount.myXModel.items.push({id:ZaItem.A_zimbraId, type:_STRING_, ref:"attrs/" + ZaItem.A_zimbraId});

ZaAccount.myXModel.items.push({id:ZaAccount.A_uid, type:_STRING_, ref:"attrs/"+ZaAccount.A_uid});
ZaAccount.myXModel.items.push({id:ZaAccount.A_accountName, type:_STRING_, ref:"attrs/"+ZaAccount.A_accountName});
ZaAccount.myXModel.items.push({id:ZaAccount.A_firstName, type:_STRING_, ref:"attrs/"+ZaAccount.A_firstName});
ZaAccount.myXModel.items.push({id:ZaAccount.A_lastName, type:_STRING_, ref:"attrs/"+ZaAccount.A_lastName, required:true});
ZaAccount.myXModel.items.push({id:ZaAccount.A_mail, type:_STRING_, ref:"attrs/"+ZaAccount.A_mail});
ZaAccount.myXModel.items.push({id:ZaAccount.A_password, type:_STRING_, ref:"attrs/"+ZaAccount.A_password});
ZaAccount.myXModel.items.push({id:ZaAccount.A2_confirmPassword, type:_STRING_});
ZaAccount.myXModel.items.push({id:ZaAccount.A_description, type:_STRING_, ref:"attrs/"+ZaAccount.A_description});
ZaAccount.myXModel.items.push({id:ZaAccount.A_telephoneNumber, type:_STRING_, ref:"attrs/"+ZaAccount.A_telephoneNumber});
ZaAccount.myXModel.items.push({id:ZaAccount.A_displayname, type:_STRING_, ref:"attrs/"+ZaAccount.A_displayname});
ZaAccount.myXModel.items.push({id:ZaAccount.A_country, type:_STRING_, ref:"attrs/"+ZaAccount.A_country});
ZaAccount.myXModel.items.push({id:ZaAccount.A_company, type:_STRING_, ref:"attrs/"+ZaAccount.A_company});
ZaAccount.myXModel.items.push({id:ZaAccount.A_initials, type:_STRING_, ref:"attrs/"+ZaAccount.A_initials});
ZaAccount.myXModel.items.push({id:ZaAccount.A_city, type:_STRING_, ref:"attrs/"+ZaAccount.A_city});
ZaAccount.myXModel.items.push({id:ZaAccount.A_orgUnit, type:_STRING_, ref:"attrs/"+ZaAccount.A_orgUnit});
ZaAccount.myXModel.items.push({id:ZaAccount.A_office, type:_STRING_, ref:"attrs/"+ZaAccount.A_office});
ZaAccount.myXModel.items.push({id:ZaAccount.A_postalAddress, type:_STRING_, ref:"attrs/"+ZaAccount.A_postalAddress});
ZaAccount.myXModel.items.push({id:ZaAccount.A_zip, type:_STRING_, ref:"attrs/"+ZaAccount.A_zip});
ZaAccount.myXModel.items.push({id:ZaAccount.A_state, type:_STRING_, ref:"attrs/"+ZaAccount.A_state});
ZaAccount.myXModel.items.push({id:ZaAccount.A_mailDeliveryAddress, type:_STRING_, ref:"attrs/"+ZaAccount.A_mailDeliveryAddress});
ZaAccount.myXModel.items.push({id:ZaAccount.A_accountStatus, type:_STRING_, ref:"attrs/"+ZaAccount.A_accountStatus});
ZaAccount.myXModel.items.push({id:ZaAccount.A_notes, type:_STRING_, ref:"attrs/"+ZaAccount.A_notes});
ZaAccount.myXModel.items.push({id:ZaAccount.A_zimbraMailQuota, type:_COS_MAILQUOTA_, ref:"attrs/"+ZaAccount.A_zimbraMailQuota});
ZaAccount.myXModel.items.push({id:ZaAccount.A_mailHost, type:_STRING_, ref:"attrs/"+ZaAccount.A_mailHost});
ZaAccount.myXModel.items.push({id:ZaAccount.A_COSId, type:_STRING_, ref:"attrs/" + ZaAccount.A_COSId});
ZaAccount.myXModel.items.push({id:ZaAccount.A_isAdminAccount, type:_ENUM_, choices:ZaModel.BOOLEAN_CHOICES, ref:"attrs/"+ZaAccount.A_isAdminAccount});
ZaAccount.myXModel.items.push({id:ZaAccount.A_zimbraMaxPwdLength, type:_COS_NUMBER_, ref:"attrs/"+ZaAccount.A_zimbraMaxPwdLength, maxInclusive:2147483647, minInclusive:0});
ZaAccount.myXModel.items.push({id:ZaAccount.A_zimbraMinPwdLength, type:_COS_NUMBER_, ref:"attrs/"+ZaAccount.A_zimbraMinPwdLength, maxInclusive:2147483647, minInclusive:0});
ZaAccount.myXModel.items.push({id:ZaAccount.A_zimbraMinPwdAge, type:_COS_NUMBER_, ref:"attrs/"+ZaAccount.A_zimbraMinPwdAge, maxInclusive:2147483647, minInclusive:0});
ZaAccount.myXModel.items.push({id:ZaAccount.A_zimbraMaxPwdAge, type:_COS_NUMBER_, ref:"attrs/"+ZaAccount.A_zimbraMaxPwdAge, maxInclusive:2147483647, minInclusive:0});
ZaAccount.myXModel.items.push({id:ZaAccount.A_zimbraEnforcePwdHistory, type:_COS_NUMBER_, ref:"attrs/"+ZaAccount.A_zimbraEnforcePwdHistory, maxInclusive:2147483647, minInclusive:0});
ZaAccount.myXModel.items.push({id:ZaAccount.A_zimbraMailAlias, type:_LIST_, ref:"attrs/"+ZaAccount.A_zimbraMailAlias, listItem:{type:_STRING_}});
ZaAccount.myXModel.items.push({id:ZaAccount.A_zimbraMailForwardingAddress, type:_LIST_, ref:"attrs/"+ZaAccount.A_zimbraMailForwardingAddress, listItem:{type:_STRING_, pattern:/^([a-zA-Z0-9_\-])+((\.)?([a-zA-Z0-9_\-])+)*@(([a-zA-Z0-9\-])+\.)+([a-zA-Z0-9]{2,4})+$/}});
ZaAccount.myXModel.items.push({id:ZaAccount.A_zimbraPasswordMustChange, type:_ENUM_, choices:ZaModel.BOOLEAN_CHOICES, ref:"attrs/"+ZaAccount.A_zimbraPasswordMustChange}); 
ZaAccount.myXModel.items.push({id:ZaAccount.A_zimbraPasswordLocked, type:_COS_ENUM_, ref:"attrs/"+ZaAccount.A_zimbraPasswordLocked, choices:ZaModel.BOOLEAN_CHOICES}); 
ZaAccount.myXModel.items.push({id:ZaAccount.A_zimbraDomainName, type:_STRING_, ref:"attrs/"+ZaAccount.A_zimbraDomainName});
ZaAccount.myXModel.items.push({id:ZaAccount.A_zimbraContactMaxNumEntries, type:_COS_NUMBER_, ref:"attrs/"+ZaAccount.A_zimbraContactMaxNumEntries, maxInclusive:2147483647, minInclusive:0});
ZaAccount.myXModel.items.push({id:ZaAccount.A_zimbraAttachmentsBlocked, type:_COS_ENUM_, ref:"attrs/"+ZaAccount.A_zimbraAttachmentsBlocked, choices:ZaModel.BOOLEAN_CHOICES}); 
ZaAccount.myXModel.items.push({id:ZaAccount.A_zimbraAttachmentsViewInHtmlOnly, type:_COS_ENUM_, ref:"attrs/"+ZaAccount.A_zimbraAttachmentsViewInHtmlOnly, choices:ZaModel.BOOLEAN_CHOICES}); 
ZaAccount.myXModel.items.push({id:ZaAccount.A_zimbraAuthTokenLifetime, type:_COS_MLIFETIME_, ref:"attrs/"+ZaAccount.A_zimbraAuthTokenLifetime});
ZaAccount.myXModel.items.push({id:ZaAccount.A_zimbraMailMessageLifetime, type:_COS_MLIFETIME_, ref:"attrs/" + ZaAccount.A_zimbraMailMessageLifetime});
ZaAccount.myXModel.items.push({id:ZaAccount.A_zimbraMailSpamLifetime, type:_COS_MLIFETIME_, ref:"attrs/" + ZaAccount.A_zimbraMailSpamLifetime});
ZaAccount.myXModel.items.push({id:ZaAccount.A_zimbraMailTrashLifetime, type:_COS_MLIFETIME_, ref:"attrs/"+ZaAccount.A_zimbraMailTrashLifetime});


//pref
ZaAccount.myXModel.items.push({id:ZaAccount.A_prefSaveToSent, type:_COS_ENUM_, ref:"attrs/"+ZaAccount.A_prefSaveToSent, choices:ZaModel.BOOLEAN_CHOICES});
ZaAccount.myXModel.items.push({id:ZaAccount.A_prefMailSignature, type:_STRING_, ref:"attrs/"+ZaAccount.A_prefMailSignature});
ZaAccount.myXModel.items.push({id:ZaAccount.A_prefMailSignatureEnabled, type:_ENUM_, choices:ZaModel.BOOLEAN_CHOICES, ref:"attrs/"+ZaAccount.A_prefMailSignatureEnabled});
ZaAccount.myXModel.items.push({id:ZaAccount.A_zimbraPrefSentMailFolder, type:_STRING_, ref:"attrs/"+ZaAccount.A_zimbraPrefSentMailFolder});
ZaAccount.myXModel.items.push({id:ZaAccount.A_zimbraPrefIncludeSpamInSearch, type:_COS_ENUM_, ref:"attrs/"+ZaAccount.A_zimbraPrefIncludeSpamInSearch, choices:ZaModel.BOOLEAN_CHOICES});
ZaAccount.myXModel.items.push({id:ZaAccount.A_zimbraPrefIncludeTrashInSearch, type:_COS_ENUM_, ref:"attrs/"+ZaAccount.A_zimbraPrefIncludeTrashInSearch, choices:ZaModel.BOOLEAN_CHOICES});
ZaAccount.myXModel.items.push({id:ZaAccount.A_zimbraPrefMailInitialSearch, type:_COS_STRING_, ref:"attrs/"+ZaAccount.A_zimbraPrefMailInitialSearch});
ZaAccount.myXModel.items.push({id:ZaAccount.A_zimbraPrefMailItemsPerPage, type:_COS_NUMBER_, ref:"attrs/"+ZaAccount.A_zimbraPrefMailItemsPerPage, choices:[10,25,50,100]});
ZaAccount.myXModel.items.push({id:ZaAccount.A_zimbraPrefMailPollingInterval, type:_COS_MLIFETIME_, ref:"attrs/"+ZaAccount.A_zimbraPrefMailPollingInterval});
ZaAccount.myXModel.items.push({id:ZaAccount.A_zimbraPrefOutOfOfficeReply, type:_STRING_, ref:"attrs/"+ZaAccount.A_zimbraPrefOutOfOfficeReply});
ZaAccount.myXModel.items.push({id:ZaAccount.A_zimbraPrefOutOfOfficeReplyEnabled, type:_ENUM_, choices:ZaModel.BOOLEAN_CHOICES, ref:"attrs/"+ZaAccount.A_zimbraPrefOutOfOfficeReplyEnabled});
ZaAccount.myXModel.items.push({id:ZaAccount.A_zimbraPrefReplyToAddress, type:_STRING_, ref:"attrs/"+ZaAccount.A_zimbraPrefReplyToAddress});
ZaAccount.myXModel.items.push({id:ZaAccount.A_zimbraPrefUseKeyboardShortcuts, type:_COS_ENUM_, ref:"attrs/"+ZaAccount.A_zimbraPrefUseKeyboardShortcuts, choices:ZaModel.BOOLEAN_CHOICES});
ZaAccount.myXModel.items.push({id:ZaAccount.A_zimbraPrefContactsPerPage, type:_COS_NUMBER_, ref:"attrs/"+ZaAccount.A_zimbraPrefContactsPerPage, choices:[10,25,50,100]});
ZaAccount.myXModel.items.push({id:ZaAccount.A_zimbraPrefComposeInNewWindow, type:_COS_ENUM_, ref:"attrs/"+ZaAccount.A_zimbraPrefComposeInNewWindow, choices:ZaModel.BOOLEAN_CHOICES});				
ZaAccount.myXModel.items.push({id:ZaAccount.A_zimbraPrefForwardReplyInOriginalFormat, type:_COS_ENUM_, ref:"attrs/"+ZaAccount.A_zimbraPrefForwardReplyInOriginalFormat, choices:ZaModel.BOOLEAN_CHOICES});						
ZaAccount.myXModel.items.push({id:ZaAccount.A_zimbraPrefAutoAddAddressEnabled, type:_COS_ENUM_, ref:"attrs/"+ZaAccount.A_zimbraPrefAutoAddAddressEnabled, choices:ZaModel.BOOLEAN_CHOICES});
ZaAccount.myXModel.items.push({id:ZaAccount.A_zimbraPrefComposeFormat, type:_COS_ENUM_, ref:"attrs/"+ZaAccount.A_zimbraPrefComposeFormat, choices:ZaModel.COMPOSE_FORMAT_CHOICES});
ZaAccount.myXModel.items.push({id:ZaAccount.A_zimbraPrefGroupMailBy, type:_COS_ENUM_, ref:"attrs/"+ZaAccount.A_zimbraPrefGroupMailBy, choices:ZaModel.GROUP_MAIL_BY_CHOICES});					
ZaAccount.myXModel.items.push({id:ZaAccount.A_zimbraPrefMessageViewHtmlPreferred, type:_COS_ENUM_, ref:"attrs/"+ZaAccount.A_zimbraPrefMessageViewHtmlPreferred, choices:ZaModel.BOOLEAN_CHOICES});
ZaAccount.myXModel.items.push({id:ZaAccount.A_zimbraPrefNewMailNotificationAddress, type:_STRING_, ref:"attrs/"+ZaAccount.A_zimbraPrefNewMailNotificationAddress});
ZaAccount.myXModel.items.push({id:ZaAccount.A_zimbraPrefNewMailNotificationEnabled, type:_ENUM_, choices:ZaModel.BOOLEAN_CHOICES, ref:"attrs/"+ZaAccount.A_zimbraPrefNewMailNotificationEnabled});
ZaAccount.myXModel.items.push({id:ZaAccount.A_zimbraPrefOutOfOfficeReply, type:_STRING_, ref:"attrs/"+ZaAccount.A_zimbraPrefOutOfOfficeReply});
ZaAccount.myXModel.items.push({id:ZaAccount.A_zimbraPrefOutOfOfficeReplyEnabled, type:_ENUM_, choices:ZaModel.BOOLEAN_CHOICES, ref:"attrs/"+ZaAccount.A_zimbraPrefOutOfOfficeReplyEnabled});
ZaAccount.myXModel.items.push({id:ZaAccount.A_zimbraPrefShowSearchString, type:_COS_ENUM_, ref:"attrs/"+ZaAccount.A_zimbraPrefShowSearchString, choices:ZaModel.BOOLEAN_CHOICES});
ZaAccount.myXModel.items.push({id:ZaAccount.A_zimbraPrefMailSignatureStyle, type:_COS_ENUM_, ref:"attrs/"+ZaAccount.A_zimbraPrefMailSignatureStyle, choices:ZaModel.SIGNATURE_STYLE_CHOICES});
ZaAccount.myXModel.items.push({id:ZaAccount.A_zimbraPrefUseTimeZoneListInCalendar, type:_COS_ENUM_, ref:"attrs/"+ZaAccount.A_zimbraPrefUseTimeZoneListInCalendar, choices:ZaModel.BOOLEAN_CHOICES});
ZaAccount.myXModel.items.push({id:ZaAccount.A_zimbraPrefImapSearchFoldersEnabled, type:_COS_ENUM_, ref:"attrs/"+ZaAccount.A_zimbraPrefImapSearchFoldersEnabled, choices:ZaModel.BOOLEAN_CHOICES});
ZaAccount.myXModel.items.push({id:ZaAccount.A_zimbraPrefCalendarUseQuickAdd, type:_COS_ENUM_, ref:"attrs/"+ZaAccount.A_zimbraPrefCalendarUseQuickAdd, choices:ZaModel.BOOLEAN_CHOICES});
ZaAccount.myXModel.items.push({id:ZaAccount.A_zimbraPrefCalendarAlwaysShowMiniCal, type:_COS_ENUM_, ref:"attrs/"+ZaAccount.A_zimbraPrefCalendarAlwaysShowMiniCal, choices:ZaModel.BOOLEAN_CHOICES});
//features
ZaAccount.myXModel.items.push({id:ZaAccount.A_zimbraFeatureContactsEnabled, type:_COS_ENUM_, ref:"attrs/"+ZaAccount.A_zimbraFeatureContactsEnabled, choices:ZaModel.BOOLEAN_CHOICES});
ZaAccount.myXModel.items.push({id:ZaAccount.A_zimbraFeatureCalendarEnabled, type:_COS_ENUM_, ref:"attrs/"+ZaAccount.A_zimbraFeatureCalendarEnabled, choices:ZaModel.BOOLEAN_CHOICES});
ZaAccount.myXModel.items.push({id:ZaAccount.A_zimbraFeatureTaggingEnabled, type:_COS_ENUM_, ref:"attrs/"+ZaAccount.A_zimbraFeatureTaggingEnabled, choices:ZaModel.BOOLEAN_CHOICES});
ZaAccount.myXModel.items.push({id:ZaAccount.A_zimbraFeatureAdvancedSearchEnabled, type:_COS_ENUM_, ref:"attrs/"+ZaAccount.A_zimbraFeatureAdvancedSearchEnabled, choices:ZaModel.BOOLEAN_CHOICES});
ZaAccount.myXModel.items.push({id:ZaAccount.A_zimbraFeatureSavedSearchesEnabled, type:_COS_ENUM_, ref:"attrs/"+ZaAccount.A_zimbraFeatureSavedSearchesEnabled, choices:ZaModel.BOOLEAN_CHOICES});
ZaAccount.myXModel.items.push({id:ZaAccount.A_zimbraFeatureConversationsEnabled, type:_COS_ENUM_, ref:"attrs/"+ZaAccount.A_zimbraFeatureConversationsEnabled, choices:ZaModel.BOOLEAN_CHOICES});
ZaAccount.myXModel.items.push({id:ZaAccount.A_zimbraFeatureChangePasswordEnabled, type:_COS_ENUM_, ref:"attrs/"+ZaAccount.A_zimbraFeatureChangePasswordEnabled, choices:ZaModel.BOOLEAN_CHOICES});
ZaAccount.myXModel.items.push({id:ZaAccount.A_zimbraFeatureInitialSearchPreferenceEnabled, type:_COS_ENUM_, ref:"attrs/"+ZaAccount.A_zimbraFeatureInitialSearchPreferenceEnabled, choices:ZaModel.BOOLEAN_CHOICES});
ZaAccount.myXModel.items.push({id:ZaAccount.A_zimbraFeatureFiltersEnabled, type:_COS_ENUM_, ref:"attrs/"+ZaAccount.A_zimbraFeatureFiltersEnabled, choices:ZaModel.BOOLEAN_CHOICES});
ZaAccount.myXModel.items.push({id:ZaAccount.A_zimbraFeatureGalEnabled, type:_COS_ENUM_, ref:"attrs/"+ZaAccount.A_zimbraFeatureGalEnabled, choices:ZaModel.BOOLEAN_CHOICES});
ZaAccount.myXModel.items.push({id:ZaAccount.A_zimbraAttachmentsIndexingEnabled, type:_COS_ENUM_, ref:"attrs/"+ZaAccount.A_zimbraAttachmentsIndexingEnabled, choices:ZaModel.BOOLEAN_CHOICES});
ZaAccount.myXModel.items.push({id:ZaAccount.A_zimbraFeatureHtmlComposeEnabled, type:_COS_ENUM_, ref:"attrs/"+ZaAccount.A_zimbraFeatureHtmlComposeEnabled, choices:ZaModel.BOOLEAN_CHOICES});

ZaAccount.myXModel.items.push({id:ZaAccount.A_zimbraImapEnabled, type:_COS_ENUM_, ref:"attrs/"+ZaAccount.A_zimbraImapEnabled, choices:ZaModel.BOOLEAN_CHOICES});
ZaAccount.myXModel.items.push({id:ZaAccount.A_zimbraPop3Enabled, type:_COS_ENUM_, ref:"attrs/"+ZaAccount.A_zimbraPop3Enabled, choices:ZaModel.BOOLEAN_CHOICES});		
ZaAccount.myXModel.items.push({id:ZaModel.currentStep, type:_NUMBER_, ref:ZaModel.currentStep});
ZaAccount.myXModel.items.push({id:ZaAccount.A2_newAlias, type:_STRING_});
//ZaAccount.myXModel.items.push({id:ZaAccount.A2_newForward, type:_STRING_});
ZaAccount.myXModel.items.push({id:ZaAccount.A2_aliases, type:_LIST_,listItem:{type:_STRING_}});
ZaAccount.myXModel.items.push({id:ZaAccount.A2_forwarding, type:_LIST_,listItem:{type:_STRING_}});
ZaAccount.myXModel.items.push({id:ZaAccount.A2_mbxsize, type:_NUMBER_, ref:"attrs/"+ZaAccount.A2_mbxsize});
ZaAccount.myXModel.items.push({id:ZaAccount.A2_quota, type:_MAILQUOTA_2_, ref:"attrs/"+ZaAccount.A_zimbraMailQuota});
ZaAccount.myXModel.items.push({id:ZaAccount.A2_autodisplayname, type:_ENUM_, choices:ZaModel.BOOLEAN_CHOICES});
ZaAccount.myXModel.items.push({id:ZaAccount.A2_autoMailServer, type:_ENUM_, choices:ZaModel.BOOLEAN_CHOICES});

ZaItem._ATTR[ZaAccount.A_displayname] = ZaMsg.attrDesc_accountName;
ZaItem._ATTR[ZaAccount.A_description] = ZaMsg.attrDesc_description;
ZaItem._ATTR[ZaAccount.A_firstName] = ZaMsg.attrDesc_firstName;
ZaItem._ATTR[ZaAccount.A_lastName] = ZaMsg.attrDesc_lastName;
ZaItem._ATTR[ZaAccount.A_accountStatus] = ZaMsg.attrDesc_accountStatus;
ZaItem._ATTR[ZaAccount.A_mailHost] =  ZaMsg.attrDesc_mailHost;
ZaItem._ATTR[ZaAccount.A_zimbraMailQuota] = ZaMsg.attrDesc_zimbraMailQuota;
ZaItem._ATTR[ZaAccount.A_notes] = ZaMsg.attrDesc_notes;

ZaAccount._accountStatus = 
function(val) {
	var desc = ZaAccount._ACCOUNT_STATUS[val];
	return (desc == null) ? val : desc;
}

/* Translation of Account status values into screen names */
ZaAccount._ACCOUNT_STATUS = new Object ();
ZaAccount._ACCOUNT_STATUS[ZaAccount.ACCOUNT_STATUS_ACTIVE] = ZaMsg.accountStatus_active;
ZaAccount._ACCOUNT_STATUS[ZaAccount.ACCOUNT_STATUS_MAINTENANCE] = ZaMsg.accountStatus_maintenance;
ZaAccount._ACCOUNT_STATUS[ZaAccount.ACCOUNT_STATUS_LOCKED] = ZaMsg.accountStatus_locked;
ZaAccount._ACCOUNT_STATUS[ZaAccount.ACCOUNT_STATUS_CLOSED] = ZaMsg.accountStatus_closed;
