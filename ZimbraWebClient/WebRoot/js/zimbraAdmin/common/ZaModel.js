
function ZaModel(init) {
 	if (arguments.length == 0) return;

	this._evtMgr = new AjxEventMgr();
}

ZaModel.prototype.toString = 
function() {
	return "ZaModel";
}

ZaModel.prototype.addChangeListener = 
function(listener) {
	return this._evtMgr.addListener(ZaEvent.L_MODIFY, listener);
}

ZaModel.prototype.removeChangeListener = 
function(listener) {
	return this._evtMgr.removeListener(ZaEvent.L_MODIFY, listener);    	
}

ZaModel.BOOLEAN_CHOICES= [{value:"TRUE", label:"Yes"}, {value:"FALSE", label:"No"}, {value:null, label:"No"}];
ZaModel.BOOLEAN_CHOICES1= [{value:1, label:"Yes"}, {value:0, label:"No"}, {value:null, label:"No"}];

ZaModel.COMPOSE_FORMAT_CHOICES = [{value:"text", label:"Text"}, {value:"html", label:"HTML"}];
ZaModel.GROUP_MAIL_BY_CHOICES = [{value:"conversation", label:"Conversation"}, {value:"message", label:"Message"}];
ZaModel.SIGNATURE_STYLE_CHOICES = [{value:"outlook", label:"Outlook"}, {value:"internet", label:"Internet"}];
ZaModel.ErrorCode = "code";
ZaModel.ErrorMessage = "error_message";
ZaModel.currentStep = "currentStep";
ZaModel.currentTab = "currentTab";