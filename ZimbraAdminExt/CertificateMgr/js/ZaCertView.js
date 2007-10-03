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
	return zimbra_cert_manager.Cert_view_title;
}

ZaCertView.prototype.getTabIcon = 
function () {
	return "Backup" ;
}

ZaCertView.prototype.getTabTitle = 
function () {
	return zimbra_cert_manager.Cert_view_title;
}

ZaCertView.prototype.getTabToolTip =
function () {
	return	this.getTitle ();
}

ZaCertView.prototype._setUI = function (certs) {
	this._certContent = new DwtComposite (this, null, DwtControl.ABSOLUTE_STYLE) ;
	//Cert Install Status
	if (ZaCertWizard.INSTALL_STATUS < 0) {
		this._certInstallStatus.setDisplay (Dwt.DISPLAY_NONE) ;
	}else {
		this._certInstallStatus.setDisplay (Dwt.DISPLAY_BLOCK) ;
	}
	
	//Cert Content
	var html = [] ;
	html.push("<div style='padding-left:10px;'>") ;
	if (certs.mailbox) {
		html.push("<h2>Certificates for the mailbox: </h2>") ;
		html.push(this.getCertTable(certs.mailbox[0])) ;
	}
	
	if (certs.server) {
		html.push("<h2>Certificates for the server: </h2>") ;
		html.push(this.getCertTable(certs.server[0])) ;
	}
	html.push("</div>") ;
	this._certContent.getHtmlElement().innerHTML = html.join("") ;	
}

ZaCertView.prototype.getCertTable = function (cert) {
	var html = [] ;
	html.push("<table><colgroup><col width=100 /><col width='*' /></colgroup>") ;
	/*
	for (var n in cert) {
		html.push("<tr><td>" + n + "</td><td>" + cert[n] + "</td></tr>");
	}*/
	
	html.push("<tr><td><strong>Subject:</strong> " + "</td><td>" + cert.subject + "</td></tr>") ;
	html.push("<tr><td><strong>Issuer:</strong>" + "</td><td>" + cert.issuer + "</td></tr>") ;
	html.push("<tr><td><strong>Validation days: </strong>" + "</td><td> " + cert.notBefore + " - " + cert.notAfter + "</td></tr>") ;
	
	html.push("</table>") ;
	return html.join("");
}

ZaCertView.prototype.set = function (certs) {
	if (AjxEnv.hasFirebug) console.log ("Set the certs") ;	
	this._setUI (certs);
}


