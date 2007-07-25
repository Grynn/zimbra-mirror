if (AjxEnv.hasFirebug) console.debug("Loaded ZaCertView.js");

function ZaCertView (parent, app, className) {
	if (arguments.length == 0) return ;
	className = className || "ZaCertView" ;
	var posStyle = DwtControl.ABSOLUTE_STYLE;
	DwtComposite.call (this, parent, className, posStyle) ;
	
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
	return ZaMsg.Cert_view_title;
}

ZaCertView.prototype.getTabIcon = 
function () {
	return "Backup" ;
}

ZaCertView.prototype.getTabTitle = 
function () {
	return ZaMsg.Cert_view_title;
}

ZaCertView.prototype.getTabToolTip =
function () {
	return	this.getTitle ();
}

ZaCertView.prototype._setUI = function (certs) {
	
	var html = [] ;
	if (certs.mailbox) {
		html.push("Certificates for the mailbox: <br />") ;
		html.push(this.getCertTable(certs.mailbox[0])) ;
	}
	
	if (certs.server) {
		html.push("Certificates for the server: <br />") ;
		html.push(this.getCertTable(certs.server[0])) ;
	}
	this.getHtmlElement().innerHTML = html.join("") ;
	
}

ZaCertView.prototype.getCertTable = function (cert) {
	var html = [] ;
	html.push("<table>") ;
	for (var n in cert) {
		html.push("<tr><td>" + n + "</td><td>" + cert[n] + "</td></tr>");
	}
	html.push("</table>") ;
	return html.join("");
}

ZaCertView.prototype.set = function (certs) {
	if (AjxEnv.hasFirebug) console.log ("Set the certs") ;	
	this._setUI (certs);
}


