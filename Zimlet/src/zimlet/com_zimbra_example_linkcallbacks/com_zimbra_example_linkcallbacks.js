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
function com_zimbra_example_linkcallbacks_HandlerObject() {
};

/**
 * Makes the Zimlet class a subclass of ZmZimletBase.
 *
 */
com_zimbra_example_linkcallbacks_HandlerObject.prototype = new ZmZimletBase();
com_zimbra_example_linkcallbacks_HandlerObject.prototype.constructor = com_zimbra_example_linkcallbacks_HandlerObject;

/**
 * This method gets called by the Zimlet framework when the zimlet loads.
 *  
 */
com_zimbra_example_linkcallbacks_HandlerObject.prototype.init =
function() {

};

/**
 * This method gets called when the zimlet is double-clicked.
 *  
 */
com_zimbra_example_linkcallbacks_HandlerObject.prototype.doubleClicked =
function() {
	this.singleClicked();
};

/**
 * This method gets called when the zimlet is single-clicked.
 *  
 */
com_zimbra_example_linkcallbacks_HandlerObject.prototype.singleClicked =
function() {
	this._displayDialog();
};

/**
 * Displays the dialog.
 * 
 */
com_zimbra_example_linkcallbacks_HandlerObject.prototype._displayDialog = 
function() {
	if (this._dialog) { //if zimlet dialog already exists...
		this._dialog.popup(); // simply popup the dialog
		return;
	}
		
	var view = new DwtComposite(this.getShell()); // creates an empty div as a child of main shell div
	view.setSize("250", "150"); // set width and height
	view.getHtmlElement().style.overflow = "auto"; // adds scrollbar
	view.getHtmlElement().innerHTML = this._createDialogView(); // insert HTML to the dialog
	
	// pass the title and view information to create dialog box
	this._dialog = new ZmDialog( { title:"Dialog Title", view:view, parent:this.getShell(), standardButtons:[DwtDialog.OK_BUTTON] } );

	// set listener for "OK" button events
	this._dialog.setButtonListener(DwtDialog.OK_BUTTON, new AjxListener(this, this._okBtnListener)); 

	// set link listener and pass no arguments
	var link1El = document.getElementById("simpledlg_someLink1");
	link1El.onclick = AjxCallback.simpleClosure(this._link1Listener, this);

	// set link listener for with two arguments
	var link2El = document.getElementById("simpledlg_someLink2");
	var link2Arg1 = "pass this info to link1";
	var link2Arg2 = {
			prop1: "prop1...a test",
			prop2: "another prop2",
			prop3: "here is prop3"
	};
	
	link2El.onclick = AjxCallback.simpleClosure(this._link2Listener, this, link2Arg1, link2Arg2);

	//show the dialog
	this._dialog.popup();
};

/**
 * Creates the dialog view.
 * 
 */
com_zimbra_example_linkcallbacks_HandlerObject.prototype._createDialogView =
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
	html[i++] = "<input id='simpledlg_someField1' type='text' value='my test field value'/>";
	html[i++] = "</td>";
	html[i++] = "</tr>";
	html[i++] = "<tr>";
	html[i++] = "<td>";
	html[i++] = "Links:";
	html[i++] = "</td>";
	html[i++] = "<td>";
	html[i++] = "<a href=\"#\" id=\"simpledlg_someLink1\">LINK #1</a>";
	html[i++] = "<br>";
	html[i++] = "<a href=\"#\" id=\"simpledlg_someLink2\">LINK #2</a>";
	html[i++] = "</td>";
	html[i++] = "</tr>";
	html[i++] = "</table>";
	
	return html.join("");
};

/**
 * This method is called when the dialog "OK" button is clicked.
 * 
 */
com_zimbra_example_linkcallbacks_HandlerObject.prototype._okBtnListener =
function() {

	var field1El = document.getElementById("simpledlg_someField1");

	var statusMsg = "OK button clicked...field is \""+field1El.value+"\"";
	appCtxt.getAppController().setStatusMsg(statusMsg);

	this._dialog.popdown(); // hide the dialog
};

/**
 * This method is called when the LINK #1 link is clicked.
 * 
 */
com_zimbra_example_linkcallbacks_HandlerObject.prototype._link1Listener =
function() {
	appCtxt.getAppController().setStatusMsg("LINK #1 clicked with no args");
};

/**
 * This method is called when the LINK #2 link is clicked.
 * 
 */
com_zimbra_example_linkcallbacks_HandlerObject.prototype._link2Listener =
function(arg1, arg2) {

	var html = new Array();
	var i = 0;
	html[i++] = "LINK #2 clicked with arguments [arg1=\"";
	html[i++] = arg1;
	html[i++] = "\"] [arg2=\"";
	html[i++] = arg2;
	html[i++] = "] [arg2.prop1=\"";
	html[i++] = arg2.prop1;
	html[i++] = "\", arg2.prop2=\"";
	html[i++] = arg2.prop2;
	html[i++] = "\", arg2.prop3=\"";
	html[i++] = arg2.prop3;
	html[i++] = "\"]";

	var statusMsg = html.join("");		

	appCtxt.getAppController().setStatusMsg(statusMsg);
};
