/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Zimbra Collaboration Suite Web Client
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
 */

YSymbolsDialog = function(shell, className, parent) {
	className = className || "YSymbolsDialog";
	this._zimlet = parent;
	var title = "Stock polling options"; 
	DwtDialog.call(this, shell, className, title);
	this.setButtonListener(DwtDialog.OK_BUTTON, new AjxListener(this, this._symbolSelected));
	this._createSearchHtml();
	DBG.println("user prop:"+this._zimlet.getUserProperty("stockSymbols"));
};

YSymbolsDialog.prototype = new DwtDialog;
YSymbolsDialog.prototype.constructor = YSymbolsDialog;

YSymbolsDialog.prototype._lookupCallback;

YSymbolsDialog.prototype._createSearchHtml = function() {

	this._textObj = new DwtInputField(this);
	this._searchBtn = new DwtButton({parent:this});
	this._searchBtn.setText("Add Symbol");		
	this._searchBtn.addSelectionListener(new AjxListener(this, this._searchButtonListener));						

	var symbols = this._zimlet.getUserProperty("stockSymbols");
	if(symbols){
		this._textObj.setValue(symbols);
	}
	var pollInterval = this._zimlet.getUserProperty("pollInterval");

	this._pollInterval = new DwtSelect({parent:this, options:[ 
	new DwtSelectOption("", (pollInterval==""), "None"),
	new DwtSelectOption("1", (pollInterval=="1"), "1 minute"),
	new DwtSelectOption("2", (pollInterval=="2"), "2 minutes"),
	new DwtSelectOption("3", (pollInterval=="3"), "3 minutes"),
	new DwtSelectOption("4", (pollInterval=="4"), "4 minutes"),
	new DwtSelectOption("5", (pollInterval=="5"), "5 minutes"),
	new DwtSelectOption("10",(pollInterval=="10"), "10 minutes"),
	new DwtSelectOption("20",(pollInterval=="20"), "20 minutes"),
	new DwtSelectOption("30",(pollInterval=="30"), "30 minutes")
	]});

	var table = document.createElement("TABLE");
	table.border = 0;
	table.cellPadding = 0;
	table.cellSpacing = 4;

	var row = table.insertRow(-1);
	var cell = row.insertCell(-1);
	cell.colSpan = 2;
	cell.innerHTML = "Enter stock symbol to be checked periodically";

	row = table.insertRow(-1);
	cell = row.insertCell(-1);
	cell.appendChild(this._textObj.getHtmlElement());
	cell = row.insertCell(-1);
	cell.appendChild(this._searchBtn.getHtmlElement());

	row = table.insertRow(-1);
	cell = row.insertCell(-1);
	cell.colSpan = 2;	
	cell.innerHTML = "Poll Interval :";
	cell.appendChild(this._pollInterval.getHtmlElement());

	var element = this._getContentDiv();
	element.appendChild(table);
//	element.appendChild(this._textObj.getHtmlElement());
//	element.appendChild(this._searchBtn.getHtmlElement());
};

YSymbolsDialog.prototype._searchButtonListener =
function(){
	this._symbolDialog = new YSymbolLookupDialog(appCtxt._shell, null, this._zimlet);	
	this._symbolDialog.popup(null,new AjxCallback(this, this._symbolSelectionHandler));
};

YSymbolsDialog.prototype._symbolSelectionHandler =
function(symbol){
	if(!symbol){ 
		return; 
	}
	var z  = this._textObj.getValue();
	z += ",";
	if(z.indexOf(symbol)<0){
		this._textObj.setValue( ((z==",")?"":z) + symbol);
	}	
};


YSymbolsDialog.prototype.popup = function(name, callback) {
	
	this._lookupCallback = callback;

	this.setTitle("Ziya - Stock polling options");

	
	// enable buttons
	this.setButtonEnabled(DwtDialog.OK_BUTTON, true);
	this.setButtonEnabled(DwtDialog.CANCEL_BUTTON, true);	
	
	// show
	DwtDialog.prototype.popup.call(this);
};

YSymbolsDialog.prototype.popdown = 
function() {
	ZmDialog.prototype.popdown.call(this);
};

YSymbolsDialog.prototype._symbolSelected =
function(){
	this._zimlet.setUserProperty("stockSymbols", this._textObj.getValue());
	this._zimlet.setUserProperty("pollInterval", this._pollInterval.getValue(), true);
	this.popdown();
	if(this._pollInterval.getValue()!=""){
		this._zimlet._displayStockStatus();
	}
};

