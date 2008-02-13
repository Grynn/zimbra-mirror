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

YSymbolLookupDialog = function(shell, className, parent) {
	className = className || "YSymbolLookupDialog";
	var title = "Company Symbol Lookup"; 
	DwtDialog.call(this, {parent:shell, className:className, title:title});
	this.setButtonListener(DwtDialog.OK_BUTTON, new AjxListener(this, this._symbolSelected));
	this._createSearchHtml();
	this._parent = parent;
};

YSymbolLookupDialog.prototype = new DwtDialog;
YSymbolLookupDialog.prototype.constructor = YSymbolLookupDialog;

YSymbolLookupDialog.prototype._lookupCallback;

YSymbolLookupDialog.prototype._createSearchHtml = function() {

	this._textObj = new DwtInputField(this);
	this._searchBtn = new DwtButton({parent:this});
	this._searchBtn.setText("Search");		
	//this._searchBtn.setSize(100,Dwt.DEFAULT);
	this._searchBtn.addSelectionListener(new AjxListener(this, this._searchButtonListener));						

	this._searchResults = new DwtListView(this);
	this._searchResults.setSize(200,150);	

	var container = document.createElement("DIV");
	container.style.marginLeft = "1em";
	container.style.marginBottom = "0.5em";
	
	var table = document.createElement("TABLE");
	table.border = 0;
	table.cellPadding = 0;
	table.cellSpacing = 4;

	var row = table.insertRow(-1);
	var cell = row.insertCell(-1);
	cell.colSpan = 2;
	cell.innerHTML = "Enter company name to search stock symbol";

	row = table.insertRow(-1);
	cell = row.insertCell(-1);
	cell.appendChild(this._textObj.getHtmlElement());
	cell = row.insertCell(-1);
	cell.appendChild(this._searchBtn.getHtmlElement());

	row = table.insertRow(-1);
	cell = row.insertCell(-1);
	cell.colSpan = 2;
	cell.appendChild(this._searchResults.getHtmlElement());

	var element = this._getContentDiv();
	element.appendChild(table);
};

YSymbolLookupDialog.prototype._searchButtonListener =
function(){
	
	var obj  = this._textObj.getValue();	
	
	if(!obj){
		this._parent.displayErrorMessage("Company name cannot be empty for searching");
		return;
	}

	//hack
	/*var z = new Object;
	z.text = 'YAHOO.Finance.SymbolSuggest.ssCallback({"ResultSet":{"Query":"yahoo inc","Result":[{"symbol":"YHOO.BA","name": "YAHOO INC.","exch": "BUE","type": "S","exchDisp":"Buenos Aires"}]}})';
	z.success = true;
	this._lookupResponseHandler(obj,z)*/
	this._lookup(obj);	
};

YSymbolLookupDialog.prototype._lookup = function(obj) {

	obj = AjxStringUtil.trim(obj);
	obj = obj.replace(/\s/ig,"+");

	//todo: cache mechanism
	
	var financeURL = "http://d.yimg.com/autoc.finance.yahoo.com/autoc?callback=YAHOO.Finance.SymbolSuggest.ssCallback&query=" + obj;
	var url = ZmZimletBase.PROXY + AjxStringUtil.urlComponentEncode(financeURL);
	
	AjxRpc.invoke(null, url, null, new AjxCallback(this, this._lookupResponseHandler, obj), true);
};

YSymbolLookupDialog.prototype._deserialize = 
function(str) {
	try {
		var foo;
		eval([ 'foo=', str ].join(""));
		return foo;
	} catch(ex) {
		throw new DwtException("Can't deserialize stock symbol info: malformed data\n[ "
			       + ex + " ]");
	}
};


YSymbolLookupDialog.prototype._lookupResponseHandler =
function(obj, result) {

	var r = result.text;

	if(!result.success || !r) {
		this._parent.displayErrorMessage("Unable to locate stock symbol for this company");
		return;
	}
	
	r = r.replace(/^YAHOO.Finance.SymbolSuggest.ssCallback\(/ig,"");
	r = r.replace(/\)$/ig,"");
	
	
	var info  = this._deserialize(r);
	DBG.dumpObj(info);

	var symbolVal = "";
	
	var yResultSet = info ? info.ResultSet : null;
	var yResult = yResultSet ? yResultSet.Result : null;
	
	var symbolInfo = [];
	
	if(yResult instanceof Array){
		for(var i in yResult){
			symbolInfo.push(yResult[i].symbol);
		}
	}else{
			symbolInfo.push(yResult.symbol)
	}
	
	var symbolVector = AjxVector.fromArray(symbolInfo);
	this._searchResults.set(symbolVector);
	
	if(this._symbolsCallback){
		this._symbolsCallback.run(symbolVector)
	}
	
	return symbolInfo;
};

YSymbolLookupDialog.prototype.setSymbolsCallback =
function(callback){
	this._symbolsCallback = callback;
};

YSymbolLookupDialog.prototype._symbolSelected =
function(){
	var list = this._searchResults;
	var items = list.getSelectedItems();
	var selectedItem = null;
	if(items){
		var sel = items.getArray() ? items.getArray()[0] : null;
		if(sel){
			var item = AjxCore.objectWithId(Dwt.getAttr(sel, "_itemIndex"));
			selectedItem = item;
		}
	}else{
		this._parent.displayErrorMessage("Stock symbol not selected");
		return;
	}
	if(this._lookupCallback){
		this._lookupCallback.run(selectedItem);
	}
	this.popdown();	
}
// Public methods

YSymbolLookupDialog.prototype.popup = function(name, callback) {
	
	this._lookupCallback = callback;

	this.setTitle("Ziya - Company Stock Symbol Search");

	
	// enable buttons
	this.setButtonEnabled(DwtDialog.OK_BUTTON, true);
	this.setButtonEnabled(DwtDialog.CANCEL_BUTTON, true);
	
	
	// show
	DwtDialog.prototype.popup.call(this);
};

YSymbolLookupDialog.prototype.popdown = function() {
	ZmDialog.prototype.popdown.call(this);
};
