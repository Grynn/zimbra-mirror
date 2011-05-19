/*
 * ***** BEGIN LICENSE BLOCK *****
 *
 * Zimbra Collaboration Suite Zimlets
 * Copyright (C) 2006, 2007 Zimbra, Inc.
 *
 * The contents of this file are subject to the Yahoo! Public License
 * Version 1.0 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 *
 * ***** END LICENSE BLOCK *****
 *@Author Raja Rao DV
 */

//Zimlet Class
function com_zimbra_example_jquery() {
}

//Make Zimlet class a subclass of ZmZimletBase class - this makes a Zimlet a Zimlet
com_zimbra_example_jquery.prototype = new ZmZimletBase();
com_zimbra_example_jquery.prototype.constructor = com_zimbra_example_jquery;

//Zimlet framework calls this when the overview panel icon is single clicked
com_zimbra_example_jquery.prototype.singleClicked =
function() {
	this._displayPrefDialog();
};

//Typically doubleclicked does the same thing as singleclicked
com_zimbra_example_jquery.prototype.doubleClicked =
function() {
	this.singleClicked();
};

com_zimbra_example_jquery.prototype._displayPrefDialog =
function() {
	//if zimlet dialog already exists...
	if (this.pbDialog) {
		this.pbDialog.popup();//simply popup the dialog
		return;
	}
	this.pView = new DwtComposite(this.getShell());//creates an empty div thats a child of main shell div
	//this.pView.setSize("400", "400");//set width and height
	//this.pView.getHtmlElement().style.overflow = "auto";//adds scrollbar
	this.pView.getHtmlElement().innerHTML = this._createPreferenceView();//insert  html for the dialogbox


	//pass the title, view, buttons and Extra Buttons information and create dialog box
	this.pbDialog = this._createDialog({title:"JQuery Example", view:this.pView, standardButtons:[DwtDialog.CANCEL_BUTTON]});

	this._addWidgetAndListeners();//add buttons

	//show the dialog
	this.pbDialog.popup();
};

com_zimbra_example_jquery.prototype._createPreferenceView =
function() {
	var html = new Array();
	var i = 0;
	html[i++] = "<DIV>Click handled by JQeury: <button id='jqueryExampleZimlet_button1'>JQuery</button></div>";
	html[i++] = "<br/>";
	html[i++] = "<DIV>Click handled by Zimbra:  <button id='jqueryExampleZimlet_button2'>Zimbra</button></div>";
	html[i++] = "<br/>";
	return html.join("");
};

com_zimbra_example_jquery.prototype._addWidgetAndListeners =
function() {
	//Using JQuery to listen to a button click - Method 1:
	var self = this;
	$("#jqueryExampleZimlet_button1").click(function() {
		self._buttonListener("Jquery Button1");
		});
	
	//Using JQuery to listen to a button click - Method 2:	
	//NOTE: You can also create a Closure using AjxCalback.simpleClosure and pass the callback to JQuery
	//For example:
	//var callback = AjxCallback.simpleClosure(this._buttonListener, this, "Jquery Button");
	//$("#jqueryExampleZimlet_button1").click(callback);
	
	//DWT way..
	var callback = AjxCallback.simpleClosure(this._buttonListener, this, "Zimbra Button");
	Dwt.setHandler(document.getElementById("jqueryExampleZimlet_button2"), DwtEvent.ONCLICK, callback);
};

com_zimbra_example_jquery.prototype._buttonListener =
function(buttonName) {
	appCtxt.getAppController().setStatusMsg(buttonName + " clicked", ZmStatusView.LEVEL_INFO);
};