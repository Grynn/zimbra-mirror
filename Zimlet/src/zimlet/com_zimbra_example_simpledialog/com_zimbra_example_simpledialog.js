/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Zimlets
 * Copyright (C) 2005, 2006, 2007, 2008, 2009, 2010 Zimbra, Inc.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.3 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 */

com_zimbra_example_simpledialog_HandlerObject = function() {
};
com_zimbra_example_simpledialog_HandlerObject.prototype = new ZmZimletBase;
com_zimbra_example_simpledialog_HandlerObject.prototype.constructor = com_zimbra_example_simpledialog_HandlerObject;

/**
 * Double clicked.
 */
com_zimbra_example_simpledialog_HandlerObject.prototype.doubleClicked =
function() {
	this.singleClicked();
};

/**
 * Single clicked.
 */
com_zimbra_example_simpledialog_HandlerObject.prototype.singleClicked =
function() {
	this._displayDialog();
};

/**
 * Displays the dialog.
 * 
 */
com_zimbra_example_simpledialog_HandlerObject.prototype._displayDialog = 
function() {
	if (this.pbDialog) { //if zimlet dialog already exists...
		this.pbDialog.popup(); //simply popup the dialog
		return;
	}
	
	var sDialogTitle = this.getMessage("simpledialog_dialog_title");
	var sStatusMsg = this.getMessage("simpledialog_status_launch");
	
	this.pView = new DwtComposite(this.getShell()); //creates an empty div as a child of main shell div
	this.pView.setSize("250", "150"); // set width and height
	this.pView.getHtmlElement().style.overflow = "auto"; // adds scrollbar
	this.pView.getHtmlElement().innerHTML = this._createDialogView(); // insert html to the dialogbox
	
	// pass the title, view and buttons information and create dialog box
	this.pbDialog = new ZmDialog({title:sDialogTitle, view:this.pView, parent:this.getShell(), standardButtons:[DwtDialog.DISMISS_BUTTON]});

	this.pbDialog.setButtonListener(DwtDialog.DISMISS_BUTTON, new AjxListener(this, this._okBtnListener)); 

	this.pbDialog.popup(); //show the dialog

	appCtxt.getAppController().setStatusMsg(sStatusMsg);
};

/**
 * Creates the dialog view.
 * 
 */
com_zimbra_example_simpledialog_HandlerObject.prototype._createDialogView =
	function() {
		var html = new Array();
		var i = 0;
		html[i++] = "<table>";
		html[i++] = "<tr>";
		html[i++] = "<td colspan='2'>";
		html[i++] = "This is a sample dialog with HTML code";
		html[i++] = "</td>";
		html[i++] = "</tr>";
		html[i++] = "<tr>";
		html[i++] = "<td>";
		html[i++] = "Some field ONE:";
		html[i++] = "</td>";
		html[i++] = "<td>";
		html[i++] = "<input id='simpledlg_someField1'  type='text'/>";
		html[i++] = "</td>";
		html[i++] = "</tr>";
		html[i++] = "<tr>";
		html[i++] = "<td>";
		html[i++] = "Some field TWO:";
		html[i++] = "</td>";
		html[i++] = "<td>";
		html[i++] = "<input id='simpledlg_someField2'  type='text'/>";
		html[i++] = "</td>";
		html[i++] = "</tr>";
		html[i++] = "</table>";
		
		return html.join("");
	};

/**
 * The "OK" button listener.
 * 
 */
	com_zimbra_example_simpledialog_HandlerObject.prototype._okBtnListener =
function() {
		this.pbDialog.popdown(); // hide the dialog
	};
