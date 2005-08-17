/**
* @class ZaRestore
* @contructor ZaRestore
* @param ZaApp app
* this class is a model for doing backup and restore operations
* @author Greg Solovyev
**/
function ZaRestore(app) {
	ZaItem.call(this, app);
	this[ZaModel.currentStep] = 1;
	this.restoreRequest = new Object();
	this.accounts = new Array();
	this.restoreRequest[ZaRestore.A_accountName] = "";
	this.restoreRequest[ZaRestore.A_accountNames] = new Array();
	this.restoreRequest[ZaRestore.A_restoreMethod] = "mb";
	this.restoreRequest[ZaRestore.A_includeIncrementals] = "FALSE";	//soap servlet uses 'TRUE'/'FALSE' literal values everywhere else
	this.restoreRequest[ZaRestore.A_prefix] = "restored_";		
	this.restoreRequest[ZaRestore.A_toServer] = "";	
	this.restoreRequest[ZaRestore.A_originalServer] = "";			
	this.restoreRequest[ZaRestore.A_target] = "/opt/zimbra/backup";				
}

ZaRestore.prototype = new ZaItem;
ZaRestore.prototype.constructor = ZaRestore;

ZaRestore.MB = "mb";
ZaRestore.RA = "ra";
ZaRestore.CA = "ca";

ZaRestore.RESTORE_CREATE_CHOICES = [{value:ZaRestore.MB, label:"Restore mailbox only"}, {value:ZaRestore.RA, label:"Restore mailbox and LDAP record"}, {value:ZaRestore.CA, label:"Restore mailbox into a new account"}];

ZaRestore.A_accountNames = "accountNames";
ZaRestore.A_accountName = "accountName";
ZaRestore.A_label = "label";
ZaRestore.A_prefix = "prefix";
ZaRestore.A_target="target";
ZaRestore.A_restoreMethod = "method";
ZaRestore.A_toServer="toServer";
ZaRestore.A_originalServer="originalServer";
ZaRestore.A_includeIncrementals = "includeIncrementals";

ZaRestore.myXModel = new Object();
ZaRestore.myXModel.items = new Array();
ZaRestore.myXModel.items.push({id:ZaRestore.A_includeIncrementals, ref:"restoreRequest/" + ZaRestore.A_includeIncrementals, type:_ENUM_, choices:ZaModel.BOOLEAN_CHOICES});
ZaRestore.myXModel.items.push({id:ZaRestore.A_accountName, ref:"restoreRequest/" + ZaRestore.A_accountName, type:_STRING_, pattern:/^([a-zA-Z0-9_\-])+((\.)?([a-zA-Z0-9_\-])+)*@(([a-zA-Z0-9\-])+\.)+([a-zA-Z0-9]{2,4})+$/});
ZaRestore.myXModel.items.push({id:ZaRestore.A_accountNames, type:_LIST_, ref:"restoreRequest/" + ZaRestore.A_accountNames, listItem:{type:_STRING_}});
ZaRestore.myXModel.items.push({id:ZaRestore.A_restoreMethod, type:_ENUM_, ref:"restoreRequest/" + ZaRestore.A_restoreMethod, choices:ZaRestore.RESTORE_CREATE_CHOICES});
ZaRestore.myXModel.items.push({id:ZaRestore.A_target, type:_STRING_, ref:"restoreRequest/"+ZaRestore.A_target});
ZaRestore.myXModel.items.push({id:ZaRestore.A_prefix, type:_STRING_, ref:"restoreRequest/"+ZaRestore.A_prefix});
ZaRestore.myXModel.items.push({id:ZaRestore.A_label, type:_STRING_, ref:"restoreRequest/"+ZaRestore.A_label});
ZaRestore.myXModel.items.push({id:ZaRestore.A_toServer, type:_STRING_, ref:"restoreRequest/"+ZaRestore.A_toServer});
ZaRestore.myXModel.items.push({id:ZaRestore.A_originalServer, type:_STRING_, ref:"restoreRequest/"+ZaRestore.A_originalServer});
ZaRestore.myXModel.items.push({id:ZaModel.currentStep, type:_NUMBER_, ref:ZaModel.currentStep});

/**
* @method static restoreAccount
* @param method:string  - mb/ra/ca
* @param includeIncrementals:booelan 
* @param label:string 
* @param target:string - path to the location of backups
* @param prefix:string 
* @param accounts:Array - array of account names 
* @param serverId:string - zimbraId of the server to which the SOAP request will be sent
* @param callback:AjxCallback - callback that will be invoked by AjxCsfeAsynchCommand
**/
ZaRestore.restoreAccount = 
function (method, includeIncrementals, label, target, prefix, accounts, serverId, callback) {
	var soapDoc = AjxSoapDoc.create("RestoreRequest", "urn:zimbraAdmin", null);
	var restoreEl = soapDoc.set("restore", "");
	if(!method) {
		throw(new AjxException("method parameter cannot be null", AjxException.INVALID_PARAM, "ZaRestore.restoreAccount", ZaMsg.ERROR_RESTORE_3));
	}
	if(!accounts) {
		throw(new AjxException("accounts parameter cannot be null", AjxException.INVALID_PARAM, "ZaRestore.restoreAccount", ZaMsg.ERROR_RESTORE_2));
	} 
	if(method == ZaRestore.CA && (!prefix || prefix.length < 1)) {
		throw(new AjxException("accounts parameter cannot be null", AjxException.INVALID_PARAM, "ZaRestore.restoreAccount", ZaMsg.ERROR_RESTORE_1));
	}
	restoreEl.setAttribute("method", method);
	if(label) {
		restoreEl.setAttribute("label", label);
	}
	if(target) {
		restoreEl.setAttribute("target", target);
	}
	if(prefix) {
		restoreEl.setAttribute("prefix", prefix);
	}
	
	if(includeIncrementals == "TRUE") {
		restoreEl.setAttribute("includeIncrementals", "1");
	} else {
		restoreEl.setAttribute("includeIncrementals", "0");
	}
	var cnt = accounts.length;
	var el = null;
	for(var i = 0; i < cnt; i ++) {
		el = soapDoc.set("a", "", restoreEl);
		el.setAttribute("name", accounts[i]);
	}
	var asynCommand = new AjxCsfeAsynchCommand();
	asynCommand.addInvokeListener(callback);
	asynCommand.invoke(soapDoc, false, null, serverId, true);	
}