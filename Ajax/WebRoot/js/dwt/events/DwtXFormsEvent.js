function DwtXFormsEvent(form, formItem, details) {
	if (arguments.length == 0) return;
	this.form = form;
	this.formItem = formItem;
	this.details = details;
}

DwtEvent.prototype.toString = function() {
	return "DwtXFormsEvent";
}
