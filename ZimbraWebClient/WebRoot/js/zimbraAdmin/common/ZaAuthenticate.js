function ZaAuthenticate(appCtxt) {
	if (arguments.length == 0) return;
	this._appCtxt = appCtxt;
}

ZaAuthenticate._isAdmin = true;

ZaAuthenticate.prototype.toString = 
function() {
	return "ZaAuthenticate";
}

ZaAuthenticate.prototype.execute =
function (uname, pword, isPublic) {
	var soapDoc = AjxSoapDoc.create("AuthRequest", "urn:zimbraAdmin", null);
	
//	var header = soapDoc.createHeaderElement();
//	var context = soapDoc.set("context", null, header);
//	context.setAttribute("xmlns", "urn:zimbraAdmin");
//	soapDoc.set("nosession", null, context);
		
	soapDoc.set("name", uname);
	soapDoc.set("password", pword);
	var resp = AjxCsfeCommand.invoke(soapDoc, true, null, null, true).firstChild;
	this._setAuthToken(resp);	
}

ZaAuthenticate.prototype._setAuthToken =
function(resp) {
	var els = resp.childNodes;
	var len = els.length;
	var el, authToken, lifetime, sessionId;
	for (var i = 0; i < len; i++) {
		el = els[i];
		if (el.nodeName == "authToken")
			authToken = el.firstChild.nodeValue;
		else if (el.nodeName == "lifetime")
			lifetime = el.firstChild.nodeValue;
		else if (el.nodeName=="sessionId")
			sessionId = el.firstChild.nodeValue;
	}
	AjxCsfeCommand.setAuthToken(authToken, lifetime, sessionId);
}
