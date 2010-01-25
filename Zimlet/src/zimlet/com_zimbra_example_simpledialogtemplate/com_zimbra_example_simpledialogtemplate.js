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

com_zimbra_example_simpledialogtemplate_HandlerObject = function() {
};
com_zimbra_example_simpledialogtemplate_HandlerObject.prototype = new ZmZimletBase;
com_zimbra_example_simpledialogtemplate_HandlerObject.prototype.constructor = com_zimbra_example_simpledialogtemplate_HandlerObject;

/**
 * Double clicked.
 */
com_zimbra_example_simpledialogtemplate_HandlerObject.prototype.doubleClicked =
function() {
	this.singleClicked();
};

/**
 * Single clicked.
 */
com_zimbra_example_simpledialogtemplate_HandlerObject.prototype.singleClicked =
function() {
	this._displayDialog();
};

/**
 * Displays the dialog.
 * 
 */
com_zimbra_example_simpledialogtemplate_HandlerObject.prototype._displayDialog = 
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
	
	// pass the title, view & buttons information to create dialog box
	this.pbDialog = new ZmDialog({title:sDialogTitle, view:this.pView, parent:this.getShell(), standardButtons:[DwtDialog.DISMISS_BUTTON]});

	this.pbDialog.setButtonListener(DwtDialog.DISMISS_BUTTON, new AjxListener(this, this._dismissBtnListener)); 

	this.pbDialog.popup(); //show the dialog

	appCtxt.getAppController().setStatusMsg(sStatusMsg);
};

/**
 * Creates the dialog view.
 * 
 */
com_zimbra_example_simpledialogtemplate_HandlerObject.prototype._createDialogView =
function() {
	var html = AjxTemplate.expand("com_zimbra_example_simpledialogtemplate.templates.Simple#Main");		
	return html;
	};

/**
 * The "DISMISS" button listener.
 * 
 */
	com_zimbra_example_simpledialogtemplate_HandlerObject.prototype._dismissBtnListener =
function() {
		
	this.pbDialog.popdown(); //hide the dialog
	};
