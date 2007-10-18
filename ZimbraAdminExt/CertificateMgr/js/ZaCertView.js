if (AjxEnv.hasFirebug) console.debug("Loaded ZaCertView.js");

function ZaCertView (parent, app, className) {
	if (arguments.length == 0) return ;
	className = className || "ZaCertView" ;
	var posStyle = DwtControl.ABSOLUTE_STYLE;
	DwtComposite.call (this, parent, className, posStyle) ;
	
	this._certInstallStatus = new DwtAlert (this) ;
	this._certInstallStatus.setIconVisible(false) ;
	
	this._app = app ;
}

ZaCertView.prototype = new DwtComposite ;
ZaCertView.prototype.constructor = ZaCertView ; 

ZaCertView.prototype.toString = 
function() {
	return "ZaCertView";
}

ZaCertView.prototype.getTitle = 
function () {
	return com_zimbra_cert_manager.Cert_view_title;
}

ZaCertView.prototype.getTabIcon = 
function () {
	return "ViewCertificate" ;
}

ZaCertView.prototype.getTabTitle = 
function () {
	return com_zimbra_cert_manager.Cert_view_title;
}

ZaCertView.prototype.getTabToolTip =
function () {
	return	this.getTitle ();
}

ZaCertView.prototype._setUI = function (certs) {
	if (!this._certContent) {
		this._certContent = new DwtComposite (this, null, DwtControl.ABSOLUTE_STYLE) ;
	}
	//Cert Install Status
	if (ZaCertWizard.INSTALL_STATUS < 0) {
		this._certInstallStatus.setDisplay (Dwt.DISPLAY_NONE) ;
	}else {
		this._certInstallStatus.setDisplay (Dwt.DISPLAY_BLOCK) ;
	}
	
	//Cert Content
	var html = [] ;
	
	html.push("<div style='padding-left:10px;'>") ;
	html.push("<h2>Server Name: " + this.getTargetServerName() + "</h2>");
	if (certs.mailbox) {
		var mailboxCert = this.getCertTable(certs.mailbox[0]) ;
		html.push("<h3>Certificates for Zimbra mailboxd Service: </h3>") ;
		html.push(mailboxCert) ;
	}
	
	if (certs.server) {
		var serverCert = this.getCertTable(certs.server[0]) ;
		html.push("<h3>Certificates for Zimbra MTA Service: </h3>") ;
		html.push(serverCert) ;
		
		html.push("<h3>Certificates for Zimbra LDAP Service: </h3>") ;
		html.push(serverCert) ;
		
		html.push("<h3>Certificates for Zimbra POP/IMAP Service: </h3>") ;
		html.push(serverCert) ;
	}
	html.push("</div>") ;
	this._certContent.getHtmlElement().innerHTML = html.join("") ;	
}

ZaCertView.prototype.getCertTable = function (cert) {
	var html = [] ;
	html.push("<table><colgroup><col width=100 /><col width='*' /></colgroup>") ;
	html.push("<tr><td><strong>Subject:</strong> " + "</td><td>" + cert.subject + "</td></tr>") ;
	html.push("<tr><td><strong>Issuer:</strong>" + "</td><td>" + cert.issuer + "</td></tr>") ;
	html.push("<tr><td><strong>Validation days: </strong>" + "</td><td> " + cert.notBefore + " - " + cert.notAfter + "</td></tr>") ;
	
	html.push("</table>") ;
	return html.join("");
}

ZaCertView.prototype.set = function (certs, targetServerId) {
	if (AjxEnv.hasFirebug) console.log ("Set the certs") ;
	this.setTargetServerId(targetServerId);
	this._setUI (certs);
}

ZaCertView.prototype.setTargetServerId = function (targetServerId) {
	if (!targetServerId) 
		throw new AjxException (com_zimbra_cert_manager.NO_TARGET_SERVER_ERROR, "ZaCertView.prototype.setTargetServerId");
	this._targetServerId = targetServerId ;	
}

ZaCertView.prototype.getTargetServerId = function () {
	return this._targetServerId ;	
}

ZaCertView.prototype.getTargetServerName = function() {
	for (var i=0; i < ZaCert.TARGET_SERVER_CHOICES.length; i ++) {
		if (this.getTargetServerId() == ZaCert.TARGET_SERVER_CHOICES[i].value){
			return ZaCert.TARGET_SERVER_CHOICES[i].label ;
		}
	}
	
	return this.getTargetServerId() ;
}
