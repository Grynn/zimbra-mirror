/**
* Creates a new message dialog.
* @constructor
* @class
* This class represents a reusable message dialog box. Messages can be informational, warning, or
* critical.
*/
function DwtMessageDialog(parent, className, buttons) {
	if (arguments.length == 0) return;
 	this._msgCellId = Dwt.getNextId();
 	buttons = buttons ? buttons : [DwtDialog.OK_BUTTON, DwtDialog.DETAIL_BUTTON];
 	DwtDialog.call(this, parent, className, null, buttons);
	this.setContent(this._contentHtml());
 	this._msgCell = Dwt.getDomObj(this.getDocument(), this._msgCellId);
	this.addEnterListener(new LsListener(this, this._enterListener));
}

DwtMessageDialog.prototype = new DwtDialog;
DwtMessageDialog.prototype.constructor = DwtMessageDialog;

DwtMessageDialog.CRITICAL_STYLE = 1;
DwtMessageDialog.INFO_STYLE = 2;
DwtMessageDialog.WARNING_STYLE = 3;

DwtMessageDialog.TITLE = new Object();
DwtMessageDialog.TITLE[DwtMessageDialog.CRITICAL_STYLE] = DwtMsg.criticalMsg;
DwtMessageDialog.TITLE[DwtMessageDialog.INFO_STYLE] = DwtMsg.infoMsg
DwtMessageDialog.TITLE[DwtMessageDialog.WARNING_STYLE] = DwtMsg.warningMsg;

DwtMessageDialog.ICON = new Object();
DwtMessageDialog.ICON[DwtMessageDialog.CRITICAL_STYLE] = DwtImg.CRITICAL;
DwtMessageDialog.ICON[DwtMessageDialog.INFO_STYLE] = DwtImg.INFORMATION;
DwtMessageDialog.ICON[DwtMessageDialog.WARNING_STYLE] = DwtImg.WARNING;


// Public methods

DwtMessageDialog.prototype.toString = 
function() {
	return "DwtMessageDialog";
}

/**
* Sets the message style (info/warning/critical) and content.
*
* @param msgStr		message text
* @param detailStr	additional text to show via Detail button
* @param style		style (info/warning/critical)
* @param title		dialog box title
*/
DwtMessageDialog.prototype.setMessage =
function(msgStr, detailStr, style, title) {
	style = style ? style : DwtMessageDialog.INFO_STYLE;
	title = title ? title : DwtMessageDialog.TITLE[style];
	this.setTitle(title);
	if (msgStr) {
		var html = new Array();
		var i = 0;
		html[i++] = "<table cellspacing='0' cellpadding='0' border='0'><tr>";
		html[i++] = "<td valign='top'>";
		html[i++] = LsImg.getImageHtml(DwtMessageDialog.ICON[style]);
		html[i++] = "</td><td class='DwtMsgArea'>";
		html[i++] = msgStr;
		html[i++] = "</td></tr></table>";
		this._msgCell.innerHTML = html.join("");
	} else {
		this._msgCell.innerHTML = "";
	}

	this.setDetailString(detailStr);
}

/**
* Resets the message dialog so it can be reused.
*/
DwtMessageDialog.prototype.reset = 
function() {
	this._msgCell.innerHTML = "";
	DwtDialog.prototype.reset.call(this);
}

// Private methods

DwtMessageDialog.prototype._contentHtml = 
function() {
	return "<div id='" + this._msgCellId + "' class='DwtMsgDialog'></div>";
}

DwtMessageDialog.prototype._enterListener =
function(ev) {
	this._runEnterCallback();
}

