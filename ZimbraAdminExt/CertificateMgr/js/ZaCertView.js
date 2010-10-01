if(window.console && window.console.log) console.debug("Loaded ZaCertView.js");

function ZaCertView (parent,className) {
	if (arguments.length == 0) return ;
	className = className || "ZaCertView" ;
	var posStyle = DwtControl.ABSOLUTE_STYLE;
	DwtComposite.call (this, parent, className, posStyle) ;
	
	this._certInstallStatus = new DwtAlert (this) ;
	this._certInstallStatus.setIconVisible(false) ;
	this.setScrollStyle (Dwt.SCROLL);
	this._app = ZaApp.getInstance() ;
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
	return com_zimbra_cert_manager.Cert_view_title + ": "  + this.getTargetServerName();
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
	html.push("<h2>" + com_zimbra_cert_manager.CERT_server_name + this.getTargetServerName() + "</h2>");

	if (certs && certs.cert) {
		for (var i=0; i < certs.cert.length; i ++) {
			var currentCert = certs.cert[i];
			var certType = currentCert.type ;
			var certInfo = this.getCertTable(currentCert) ;
			var title = AjxMessageFormat.format(com_zimbra_cert_manager.Cert_Service_title, certType) ;
			html.push("<h3>" + title + "</h3>") ;
			html.push(certInfo) ;
		}
	}else{
		html.push(com_zimbra_cert_manager.Cert_Info_Unavailable);
	}
	html.push("</div>") ;
	this._certContent.getHtmlElement().innerHTML = html.join("") ;	
}

ZaCertView.prototype.getCertTable = function (cert) {
	var html = [] ;	
	if (cert) {
		html.push("<table><colgroup><col width=160 /><col width='*' /></colgroup>") ;
		if (cert[ZaCert.A_subject] && cert[ZaCert.A_subject][0]) {
			html.push("<tr><td><strong>" + com_zimbra_cert_manager.CERT_INFO_SUBJECT + "</strong> " 
				+ "</td><td>" + cert[ZaCert.A_subject][0]._content 
				+ "</td></tr>") ;
		}
		if (cert.issuer && cert.issuer[0]) {
			html.push("<tr><td><strong>" + com_zimbra_cert_manager.CERT_INFO_ISSUER+ "</strong>" + "</td><td>" 
			+ cert.issuer[0]._content + "</td></tr>") ;
		}
		
		if (cert.notBefore && cert.notBefore[0] && cert.notAfter && cert.notAfter[0]) {
			html.push("<tr><td><strong>" + com_zimbra_cert_manager.CERT_INFO_VALIDATION_DAYS +"</strong>" 
					+ "</td><td> " + cert.notBefore[0]._content + " - " + cert.notAfter[0]._content + "</td></tr>") ;
		}
		
		if (cert[ZaCert.A_subject_alt] && cert[ZaCert.A_subject_alt][0]) {
			html.push("<tr><td><strong>" + com_zimbra_cert_manager.CERT_INFO_SubjectAltName + " </strong>" + "</td><td> " 
			+ cert[ZaCert.A_subject_alt][0]._content +  "</td></tr>") ;
		}
		html.push("</table>") ;
	}else{
		html.push (com_zimbra_cert_manager.Cert_Service_Unavailable);
	}
	return html.join("");
}                             

ZaCertView.prototype.set = function (certs, targetServerId) {
	if(window.console && window.console.log) console.log ("Set the certs") ;
    //this._containedObject is used to check if the item tab exists already
    if (!this._containedObject) this._containedObject = {} ;
    this._containedObject.certs = certs ;
    this._containedObject.id = targetServerId ;

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
