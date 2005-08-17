/**
* Creates a new message dialog.
* @constructor
* @class
* This class represents a reusable message dialog box. Messages can be informational, warning, or
* critical.
*/
function ZaMsgDialog(parent, className, buttons, app) {
	this._app = app;
 	DwtMessageDialog.call(this, parent, className, buttons);
}

ZaMsgDialog.prototype = new DwtMessageDialog;
ZaMsgDialog.prototype.constructor = ZaMsgDialog;

ZaMsgDialog.prototype.setApp = 
function(app) {
	this._app=app;
}

