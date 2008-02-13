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
 */

function TravelAgentAirportLookupDlg(parent, parentDlg, zimlet) {
	ZmDialog.call(this, {parent:parent, title:"Airport Code Lookup"});
	this.zimlet = zimlet;
	this.parentDlg=parentDlg;
	this.parentDlg = parentDlg;	
	var contentEl = this._createContentEl();
	var contentDiv = this._getContentDiv();
	contentDiv.appendChild(contentEl);	
}

TravelAgentAirportLookupDlg.prototype = new ZmDialog;
TravelAgentAirportLookupDlg.prototype.constructor = TravelAgentAirportLookupDlg;

TravelAgentAirportLookupDlg.prototype.toString = 
function() {
	return "TravelAgentAirportLookupDlg";
}

TravelAgentAirportLookupDlg.prototype.getSelectedAirport = 
function () {
	return this._airportSelect.getValue();
}

TravelAgentAirportLookupDlg.prototype._createContentEl = 
function () {
	this._airportSelect = new DwtSelect({parent:this});
	
	var table = document.createElement("TABLE");
	table.border = 0;
	table.cellSpacing = 3;
	table.cellPadding = 0;
	var row1 = table.insertRow(table.rows.length);
	var cityLabelCell = row1.insertCell(row1.cells.length);
//	cityLabelCell.className = "Label";
	cityLabelCell.innerHTML = "Enter city and state or zip code:";
	cityLabelCell.colSpan = 2;
	var row2 = table.insertRow(table.rows.length); 
	var cityNameCell = row2.insertCell(row2.cells.length);
	this._cityField = new DwtInputField({parent:this, type:DwtInputField.STRING,
											initialValue:"", size:null, maxLen:null,
											errorIconStyle:DwtInputField.ERROR_ICON_NONE,
											validationStyle:DwtInputField.ONEXIT_VALIDATION});	
	
	cityNameCell.appendChild(this._cityField.getHtmlElement());
	var searchBtnCell = row2.insertCell(row2.cells.length);		
	var searchButton = new DwtButton({parent:this});
	searchButton.setText("Search");
	searchButton.setToolTipContent("Click this button to find airports");
	searchButton.addSelectionListener(new AjxListener(this, this._searchButtonListener));
	searchBtnCell.appendChild(searchButton.getHtmlElement());		
									
	var row3 = table.insertRow(table.rows.length);
	var airportLabelCell = row3.insertCell(row3.cells.length);
	//airportLabelCell.className = "Label";
	airportLabelCell.innerHTML = "Airports:";	
	airportLabelCell.colSpan = 2;

	var row4 = table.insertRow(table.rows.length);
	var airportSelectCell = row4.insertCell(row4.cells.length);	
	airportSelectCell.colSpan = 2;
	airportSelectCell.appendChild(this._airportSelect.getHtmlElement());
	return table;
}

TravelAgentAirportLookupDlg.prototype._searchButtonListener =
function (ev) {
	var loc = this._cityField.getValue();
	var options = this.zimlet.findAirports(loc);
	var cnt = options.length;
	this._airportSelect.clearOptions();
	for(var i=0; i < cnt; i++) {
		this._airportSelect.addOption(options[i], i==0);
	}
}