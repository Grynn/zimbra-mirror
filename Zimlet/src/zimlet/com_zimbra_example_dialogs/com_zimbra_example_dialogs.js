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

/**
 * Defines the Zimlet handler class.
 *   
 */
function com_zimbra_example_dialogs_HandlerObject() {
};

/**
 * Makes the Zimlet class a subclass of ZmZimletBase.
 *
 */
com_zimbra_example_dialogs_HandlerObject.prototype = new ZmZimletBase();
com_zimbra_example_dialogs_HandlerObject.prototype.constructor = com_zimbra_example_dialogs_HandlerObject;

/**
 * This method gets called by the Zimlet framework when the zimlet loads.
 *  
 */
com_zimbra_example_dialogs_HandlerObject.prototype.init =
function() {

};

/**
 * This method gets called when the zimlet is double-clicked.
 *  
 */
com_zimbra_example_dialogs_HandlerObject.prototype.doubleClicked =
function() {
	this.singleClicked();
};

/**
 * This method gets called when the zimlet is single-clicked.
 *  
 */
com_zimbra_example_dialogs_HandlerObject.prototype.singleClicked =
function() {	
	var appController = appCtxt.getAppController();
		
	appController.setStatusMsg("Right click to see dialog box options", ZmStatusView.LEVEL_INFO);
};

/**
 * This method is called when a context menu item is selected.
 * 
 */
com_zimbra_example_dialogs_HandlerObject.prototype.menuItemSelected = 
function(itemId) {

	this._dialog =  null;
	var style = DwtMessageDialog.INFO_STYLE; //show info status by default

	switch (itemId) {
		case "com_zimbra_dialogs_msgInfodlg":
			this._dialog = appCtxt.getMsgDialog(); // returns DwtMessageDialog
			msg = "This is message dialog with info status";

			// set the button listeners
			this._dialog.setButtonListener(DwtDialog.OK_BUTTON, new AjxListener(this, this._okBtnListener)); // listens for OK button events
			break;
		case "com_zimbra_dialogs_msgWarndlg":
			this._dialog = appCtxt.getMsgDialog(); // returns DwtMessageDialog
			msg = "This is message dialog with warning status";
			style = DwtMessageDialog.WARNING_STYLE;

			// set the button listeners
			this._dialog.setButtonListener(DwtDialog.OK_BUTTON, new AjxListener(this, this._okBtnListener)); // listens for OK button events
			break;
		case "com_zimbra_dialogs_msgCriticaldlg":
			this._dialog = appCtxt.getMsgDialog(); // returns DwtMessageDialog
			msg = "This is message dialog with critical status";
			style = DwtMessageDialog.CRITICAL_STYLE;

			// set the button listeners
			this._dialog.setButtonListener(DwtDialog.OK_BUTTON, new AjxListener(this, this._okBtnListener)); // listens for OK button events
				break;
		case "com_zimbra_dialogs_errordlg":
			this._dialog = appCtxt.getErrorDialog(); // returns ZmErrorDialog
			msg = "This is an Error dialog";

			// set the button listeners
			this._dialog.setButtonListener(DwtDialog.OK_BUTTON, new AjxListener(this, this._okBtnListener)); // listens for OK button events
			break;
		case "com_zimbra_dialogs_yesnodlg":
			this._dialog = appCtxt.getYesNoMsgDialog(); // returns DwtMessageDialog
			msg = "This is a dialog with yes/no buttons ";

			// set the button listeners
			this._dialog.setButtonListener(DwtDialog.YES_BUTTON, new AjxListener(this, this._yesBtnListener)); // listens for YES button events
			this._dialog.setButtonListener(DwtDialog.NO_BUTTON, new AjxListener(this, this._noBtnListener)); // listens for NO button events
			break;
		case "com_zimbra_dialogs_yesnocanceldlg":
			this._dialog = appCtxt.getYesNoCancelMsgDialog(); // returns DwtMessageDialog
			msg = "This is a dialog with yes/no/cancel buttons ";
			
			// set the button listeners
			this._dialog.setButtonListener(DwtDialog.YES_BUTTON, new AjxListener(this, this._yesBtnListener)); // listens for YES button events
			this._dialog.setButtonListener(DwtDialog.NO_BUTTON, new AjxListener(this, this._noBtnListener)); // listens for NO button events
			this._dialog.setButtonListener(DwtDialog.CANCEL_BUTTON, new AjxListener(this, this._cancelBtnListener)); // listens for CANCEL button events
			break;
		
	}

	this._dialog.reset(); // reset dialog
	this._dialog.setMessage(msg, style);
	this._dialog.popup();
};

/**
 * This method is called when the "Yes" button is clicked.
 * 
 * @param	{DwtSelectionEvent}		event		the event
 */
com_zimbra_example_dialogs_HandlerObject.prototype._yesBtnListener = 
function(obj) {
	this._dialog.popdown(); // close the dialog
};

/**
 * This method is called when the "No" button is clicked.
 * 
 * @param	{DwtSelectionEvent}		event		the event
 */
com_zimbra_example_dialogs_HandlerObject.prototype._noBtnListener = 
function(obj) {
	this._dialog.popdown(); // close the dialog
};

/**
 * This method is called when the "Cancel" button is clicked.
 * 
 * @param	{DwtSelectionEvent}		event		the event
 */
com_zimbra_example_dialogs_HandlerObject.prototype._cancelBtnListener = 
function(obj) {
	this._dialog.popdown(); // close the dialog
};

/**
 * This method is called when the "OK" button is clicked.
 * 
 * @param	{DwtSelectionEvent}		event		the event
 */
com_zimbra_example_dialogs_HandlerObject.prototype._okBtnListener = 
function(obj) {
	this._dialog.popdown(); // close the dialog
};
