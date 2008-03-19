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

function FlightStatusDlg(parent, className, zimlet) {
	//var buttons = [ DwtDialog.OK_BUTTON, DwtDialog.CANCEL_BUTTON ];
	
	ZmDialog.call(this, {parent:parent, className:className, title:"Flight Status"});
	
	this.zimlet = zimlet;
	var contentEl = this._createContentEl();
	var contentDiv = this._getContentDiv();
	contentDiv.appendChild(contentEl);

	this.currentAirline = null;
	this.currentFlightNum = null;
	this._setMouseEventHdlrs();
	this._objectManager = new ZmObjectManager(this);	
	
	this._flightNumInputField = new DwtInputField({parent:this, type:DwtInputField.STRING,
											initialValue:"", size:null, maxLen:null,
											errorIconStyle:DwtInputField.ERROR_ICON_NONE,
											validationStyle:DwtInputField.CONTINUAL_VALIDATION,validatorCtxtObj:this,validator:FlightStatusDlg._onChange});
	
	Dwt.setSize(this._flightNumInputField.getInputElement(), "50px", "22px");	
	this._flightNumInputField.reparentHtmlElement(this.flightNumInputCellid);

	this._flightCodeInputField = new DwtInputField({parent:this, type:DwtInputField.STRING,
											initialValue:"", size:null, maxLen:null,
											errorIconStyle:DwtInputField.ERROR_ICON_NONE,
											validationStyle:DwtInputField.CONTINUAL_VALIDATION});
	
	Dwt.setSize(this._flightCodeInputField.getInputElement(), "50px", "22px");	
	this._flightCodeInputField.reparentHtmlElement(this.flightCodeInputCellid);
			
	this._tabGroup.addMember(this._flightNumInputField);
	this._tabGroup.addMember(this._flightCodeInputField);	
}

FlightStatusDlg.prototype = new ZmDialog;
FlightStatusDlg.prototype.constructor = FlightStatusDlg;
FlightStatusDlg.BLANK_IMG = "http://www.jplrecclubs.caltech.edu/hiking/photos/Earth.jpg";
FlightStatusDlg.YMAP_URL="http://maps.yahoo.com/maps_result";
//FlightStatusDlg.YMAP_URL="http://maps.yahoo.com/maps_result?addr=SFO&csz=San+Francisco%2C+CA&new=1&name=&qty="

FlightStatusDlg.prototype.toString = 
function() {
	return "FlightStatusDlg";
}

// Public methods

FlightStatusDlg.prototype.popup =
function(loc) {
	ZmDialog.prototype.popup.call(this, loc);
	this.resetFields("N/A");
//	this.shell.getKeyboardMgr().enable(false);
}

FlightStatusDlg.prototype._okButtonListener = function () {
	this.popdown();
};

FlightStatusDlg.prototype.popdown =
function() {
//	this.shell.getKeyboardMgr().enable(true);
	appCtxt.getShell().setCursor("default");
	ZmDialog.prototype.popdown.call(this);
}
// Protected methods

FlightStatusDlg.prototype.setZimlet = 
function(zimlet) {
	this.zimlet = zimlet;
}

FlightStatusDlg.prototype.setFlight = 
function(code,airline,airlinecode,number) {
    DBG.println(AjxDebug.DBG3, "FlightStatusDlg.setFlight code:" + code + " air: " + airline + " aircode: " + airlinecode + " num: " + number);
    if(airlinecode && airlinecode.length==2 && Com_Flightexplorer_Fasttrack.mapIATA2ICAO[airlinecode]) {
		airlinecode = Com_Flightexplorer_Fasttrack.mapIATA2ICAO[airlinecode];
	}
    // No flight number? Pull it from the code.
    if (!number || number < 0) {
        number = parseInt(code.replace(/[a-z]/ig, ""), 10);
    }
    this.setAirline(airline,airlinecode);
	this.setFlightNumber(number);
	this.setFlightCode(airlinecode+number);
	this.airlineValueCell.innerHTML = airline;
	this.resetFields("Please wait. Loading data...");	
	this.zimlet.getFlightDataAndImage(new AjxCallback(this, this.dataClbk),code);
}

FlightStatusDlg.prototype.setAirline = 
function(airline,airlinecode) {
	this.currentAirline = airline;
	if(this._airlineSelect && airlinecode && airlinecode.length==3) {
		this._airlineSelect.setSelectedValue(airlinecode);
	}
}

FlightStatusDlg.prototype.setFlightNumber = 
function(flight) {
	this.currentFlightNum = flight;
	if(this._flightNumInputField) {
		this._flightNumInputField.setValue(flight);
	}
}

FlightStatusDlg.prototype.setFlightCode = 
function(code) {
	this.currentFlightCode = code;
	this.detailValueCell.innerHTML = ("(" + code + ")");
	if(this._flightCodeInputField) {
		this._flightCodeInputField.setValue(code);
	}
}

FlightStatusDlg.prototype._createContentEl = 
function() {
	// create controls
	this._airlineSelect = new DwtSelect({parent:this});
	for (var i in Com_Flightexplorer_Fasttrack.airlines3) {
		this._airlineSelect.addOption(Com_Flightexplorer_Fasttrack.airlines3[i], i == this.currentAirline, i);
	}
	
	var table = document.createElement("TABLE");
	table.border = 0;
	table.cellSpacing = 3;
	table.cellPadding = 0;

	var row1 = table.insertRow(table.rows.length);
	var airlineLabelCell = row1.insertCell(row1.cells.length);
	airlineLabelCell.className = "Label";
	airlineLabelCell.innerHTML = "Airline: ";
	var airlineInputCell = row1.insertCell(row1.cells.length);
	airlineInputCell.appendChild(this._airlineSelect.getHtmlElement());	
	this._airlineSelect.addChangeListener(new AjxListener(this, this.flightChanged));

	/*this._flightNumInputEl = document.createElement("INPUT");
	this._flightNumInputEl.autocomplete = "OFF";
	this._flightNumInputEl.type = "text";
	this._flightNumInputEl.size=5;
	var fldgId = AjxCore.assignId(this);
	this._flightNumInputEl._fdlgId = fldgId;	
	Dwt.setHandler(this._flightNumInputEl, DwtEvent.ONKEYUP, FlightStatusDlg._onChange);
	*/
											
	/*this._flightCodeInputEl = document.createElement("INPUT");
	this._flightCodeInputEl.autocomplete = "OFF";
	this._flightCodeInputEl.type = "text";
	this._flightCodeInputEl.size=5;
	*/
		
	
	var flightNumLabelCell = row1.insertCell(row1.cells.length);
	flightNumLabelCell.className = "Label";
	flightNumLabelCell.innerHTML = "Flight number:";

	this.flightNumInputCellid = Dwt.getNextId();
	var flightNumInputCell = row1.insertCell(row1.cells.length);
	flightNumInputCell.id = this.flightNumInputCellid;
	//flightNumInputCell.appendChild(this._flightNumInputEl);


	var flightCodeLabelCell = row1.insertCell(row1.cells.length);
	flightCodeLabelCell.className = "Label";
	flightCodeLabelCell.innerHTML = "Flight code:";

	this.flightCodeInputCellid = Dwt.getNextId();
	var flightCodeInputCell = row1.insertCell(row1.cells.length);
	flightCodeInputCell.id = this.flightCodeInputCellid;
	//flightCodeInputCell.appendChild(this._flightCodeInputEl);
			
	var flightSearchBtnCell = row1.insertCell(row1.cells.length);		
	var searchButton = new DwtButton({parent:this});
	searchButton.setText("Track Flight");
	searchButton.setToolTipContent("Click this button to get the status of the flight");
	searchButton.addSelectionListener(new AjxListener(this, this._searchButtonListener));
	flightSearchBtnCell.appendChild(searchButton.getHtmlElement());
				
	var row2 = table.insertRow(table.rows.length);
	var contentCell = row2.insertCell(row2.cells.length);
	contentCell.colSpan=7;
	var contentTable = document.createElement("TABLE");
	
	var row21 = contentTable.insertRow(contentTable.rows.length);

	this.leftColumn = row21.insertCell(row21.cells.length);
	this.rightColumn = row21.insertCell(row21.cells.length);

	var leftTable = document.createElement("TABLE");
	
	var lr1 = leftTable.insertRow(leftTable.rows.length);
	this.detailLabelCell = lr1.insertCell(lr1.cells.length);
//	this.detailLabelCell.colSpan=2;
	this.detailLabelCell.className = "Label";
	this.detailLabelCell.innerHTML = "Flight details";

		
	var detailActCell = lr1.insertCell(lr1.cells.length);
		var detailTable = document.createElement("TABLE");
		var detailrow1 = detailTable.insertRow(detailTable.rows.length);
		this.detailValueCell = detailrow1.insertCell(detailrow1.cells.length);
		this.detailValueCell.className = "Label";
	
		var add2CalCell = detailrow1.insertCell(detailrow1.cells.length);
		var add2CalButton = new DwtButton({parent:this, className:"TBButton"});
		add2CalButton.setImage("NewAppointment");
		add2CalButton.addSelectionListener(new AjxListener(this, this._add2Cal));
		add2CalButton.setToolTipContent("Add this flight to your calendar");
		add2CalCell.appendChild(add2CalButton.getHtmlElement());
	detailActCell.appendChild(detailTable);
	
	detailActCell.colSpan = 2;
			
	var lrStatus = leftTable.insertRow(leftTable.rows.length);
	this.statusLabelCell = lrStatus.insertCell(lrStatus.cells.length);
	this.statusLabelCell.className = "search-results-table-hdr";
	this.statusLabelCell.innerHTML = "Status:";
	this.statusValueCell = lrStatus.insertCell(lrStatus.cells.length);	
	this.statusValueCell.className = "search-results-table-data";

	this.statusValueCell.colSpan = 2;
	
	var lrAirline = leftTable.insertRow(leftTable.rows.length);
	this.airlineLabelCell = lrAirline.insertCell(lrAirline.cells.length);
	this.airlineLabelCell.className = "search-results-table-hdr";
	this.airlineLabelCell.innerHTML = "Airline:";
	this.airlineValueCell = lrAirline.insertCell(lrAirline.cells.length);	
	this.airlineValueCell.className = "search-results-table-data";
	
	this.airlineValueCell.colSpan = 2;
	
	var lrAircraft = leftTable.insertRow(leftTable.rows.length);
	this.aircraftLabelCell = lrAircraft.insertCell(lrAircraft.cells.length);
	this.aircraftLabelCell.className = "search-results-table-hdr";
	this.aircraftLabelCell.innerHTML = "Aircraft:";
	this.aircraftValueCell = lrAircraft.insertCell(lrAircraft.cells.length);	
	this.aircraftValueCell.className = "search-results-table-data";
		
	this.aircraftValueCell.colSpan = 2;		
	
	var lrDuration = leftTable.insertRow(leftTable.rows.length);
	this.durationLabelCell = lrDuration.insertCell(lrDuration.cells.length);
	this.durationLabelCell.className = "search-results-table-hdr";
	this.durationLabelCell.innerHTML = "Duration:";
	this.durationValueCell = lrDuration.insertCell(lrDuration.cells.length);	
	this.durationValueCell.className = "search-results-table-data";
	
	this.durationValueCell.colSpan = 2;	
	
	var lr2 = leftTable.insertRow(leftTable.rows.length);
	this.departureLabelCell = lr2.insertCell(lr2.cells.length);
//	this.departureLabelCell.colSpan=2;
	this.departureLabelCell.className = "Label";
	this.departureLabelCell.innerHTML = "Departure";
	
	var depActCell = lr2.insertCell(lr2.cells.length);
		var depTable = document.createElement("TABLE");
		var deprow1 = depTable.insertRow(depTable.rows.length);
		var depMapBtnCell = deprow1.insertCell(deprow1.cells.length);
		var getDepYMapButton = new DwtButton({parent:this, className:"TBButton"});
		getDepYMapButton.setText("map of the airport");
		getDepYMapButton.setImage("YMAP-Icon");
		getDepYMapButton.setToolTipContent("Get the map of the airport");
		getDepYMapButton.addSelectionListener(new AjxListener(this, this._getDepYMap));
		depMapBtnCell.appendChild(getDepYMapButton.getHtmlElement());
	
/*		var depAdd2CalCell = deprow1.insertCell(deprow1.cells.length);
		var depAdd2CalButton = new DwtButton({parent:this});
		depAdd2CalButton.setImage("NewAppointment");
		depAdd2CalButton.addSelectionListener(new AjxListener(this, this._depAdd2Cal));
		depAdd2CalCell.appendChild(depAdd2CalButton.getHtmlElement());
*/
	depActCell.appendChild(depTable);
	
	depActCell.colSpan = 2;
	
	var lrFromAirport = leftTable.insertRow(leftTable.rows.length);
	this.fromAirportLabelCell = lrFromAirport.insertCell(lrFromAirport.cells.length);
	this.fromAirportLabelCell.className = "search-results-table-hdr";
	this.fromAirportLabelCell.innerHTML = "Airport:";
	this.fromAirportValueCell = lrFromAirport.insertCell(lrFromAirport.cells.length);	
	this.fromAirportValueCell.className = "search-results-table-data";
	
	this.fromAirportValueCell.colSpan = 2;
	
	var lrPDTime = leftTable.insertRow(leftTable.rows.length);
	this.PDTLabelCell = lrPDTime.insertCell(lrPDTime.cells.length);
	this.PDTLabelCell.className = "search-results-table-hdr";
	this.PDTLabelCell.innerHTML = "Planned time:";
	this.PDTValueCell = lrPDTime.insertCell(lrPDTime.cells.length);	
	this.PDTValueCell.className = "search-results-table-data";
	
	var fromWeatherPictureCell = lrPDTime.insertCell(lrPDTime.cells.length);
	fromWeatherPictureCell.rowSpan = 3;
	this.fromWeatherImg = document.createElement("IMG");
	if(this.zimlet)
		this.fromWeatherImg.src=this.zimlet.getResource("na.gif");
	fromWeatherPictureCell.appendChild(this.fromWeatherImg);
	
	var lrEDTime = leftTable.insertRow(leftTable.rows.length);
	this.EDTLabelCell = lrEDTime.insertCell(lrEDTime.cells.length);
	this.EDTLabelCell.className = "search-results-table-hdr";
	this.EDTLabelCell.innerHTML = "Actual time:";
	this.EDTValueCell = lrEDTime.insertCell(lrEDTime.cells.length);	
	this.EDTValueCell.className = "search-results-table-data";
		
	var lrDWeat = leftTable.insertRow(leftTable.rows.length);
	this.DWeatLabelCell = lrDWeat.insertCell(lrDWeat.cells.length);
	this.DWeatLabelCell.className = "search-results-table-hdr";
	this.DWeatLabelCell.innerHTML = "Weather:";
	this.DWeatValueCell = lrDWeat.insertCell(lrDWeat.cells.length);	
	this.DWeatValueCell.className = "search-results-table-data";
				
	var lr3 = leftTable.insertRow(leftTable.rows.length);
	
	this.arrivalLabelCell = lr3.insertCell(lr3.cells.length);
//	this.arrivalLabelCell.colSpan=2;
	this.arrivalLabelCell.className = "Label";
	this.arrivalLabelCell.innerHTML = "Arrival";	

	var arrActCell = lr3.insertCell(lr3.cells.length);
		var arrTable = document.createElement("TABLE");
		var arrrow1 = arrTable.insertRow(arrTable.rows.length);
		var arMapBtnCell = arrrow1.insertCell(arrrow1.cells.length);
		var getArrYMapButton = new DwtButton({parent:this, className:"TBButton"});
		getArrYMapButton.setText("map of the airport");
		getArrYMapButton.setImage("YMAP-Icon");
		getArrYMapButton.setToolTipContent("Get the map of the airport");
		getArrYMapButton.addSelectionListener(new AjxListener(this, this._getArrYMap));
		arMapBtnCell.appendChild(getArrYMapButton.getHtmlElement());
		
	/*	var arrAdd2CalCell = arrrow1.insertCell(arrrow1.cells.length);
		var arrAdd2CalButton = new DwtButton({parent:this});
		arrAdd2CalButton.setImage("NewAppointment");
		arrAdd2CalButton.addSelectionListener(new AjxListener(this, this._arrAdd2Cal));
		arrAdd2CalCell.appendChild(arrAdd2CalButton.getHtmlElement());
		*/
	arrActCell.appendChild(arrTable);
	
	arrActCell.colSpan = 2;
	
	var lrToAirport = leftTable.insertRow(leftTable.rows.length);
	this.toAirportLabelCell = lrToAirport.insertCell(lrToAirport.cells.length);
	this.toAirportLabelCell.className = "search-results-table-hdr";
	this.toAirportLabelCell.innerHTML = "Airport:";
	this.toAirportValueCell = lrToAirport.insertCell(lrToAirport.cells.length);	
	this.toAirportValueCell.className = "search-results-table-data";
	
	this.toAirportValueCell.colSpan = 2;
	
	var lrPATime = leftTable.insertRow(leftTable.rows.length);
	this.PTALabelCell = lrPATime.insertCell(lrPATime.cells.length);
	this.PTALabelCell.className = "search-results-table-hdr";
	this.PTALabelCell.innerHTML = "Planned time:";
	this.PTAValueCell = lrPATime.insertCell(lrPATime.cells.length);	
	this.PTAValueCell.className = "search-results-table-data";

	var toWeatherPictureCell = lrPATime.insertCell(lrPATime.cells.length);
	toWeatherPictureCell.rowSpan = 3;
	this.toWeatherImg = document.createElement("IMG");
	if(this.zimlet)
		this.toWeatherImg.src=this.zimlet.getResource("na.gif");
	toWeatherPictureCell.appendChild(this.toWeatherImg);
		
	var lrETAime = leftTable.insertRow(leftTable.rows.length);
	this.ETALabelCell = lrETAime.insertCell(lrETAime.cells.length);
	this.ETALabelCell.className = "search-results-table-hdr";
	this.ETALabelCell.innerHTML = "Estimated time:";
	this.ETAValueCell = lrETAime.insertCell(lrETAime.cells.length);	
	this.ETAValueCell.className = "search-results-table-data";	

	var lrEWeat = leftTable.insertRow(leftTable.rows.length);
	this.EWeatLabelCell = lrEWeat.insertCell(lrEWeat.cells.length);
	this.EWeatLabelCell.className = "search-results-table-hdr";
	this.EWeatLabelCell.innerHTML = "Weather:";
	this.EWeatValueCell = lrEWeat.insertCell(lrEWeat.cells.length);	
	this.EWeatValueCell.className = "search-results-table-data";
	
	this.leftColumn.appendChild(leftTable);
	this.flightImg = document.createElement("IMG");
	if(this.zimlet)
		this.flightImg.src=this.zimlet.getResource("blank.gif");
	else 
		this.flightImg.src=FlightStatusDlg.BLANK_IMG;
		
	this.rightColumn.appendChild(this.flightImg);
	contentCell.appendChild(contentTable);
	return table;
};


FlightStatusDlg.prototype._getSeparatorTemplate = function() {
	return "";
};


FlightStatusDlg.prototype.dataClbk = function(flightInfo) {
	if(!flightInfo)
		return;
	this.flightInfo = flightInfo;
	this.statusValueCell.innerHTML = flightInfo.Status;	
	var airphone = null;
	if(!flightInfo.AirlineName)
		flightInfo.AirlineName = this.currentAirline;

	try {
		if(Com_Flightexplorer_Fasttrack.airphones[flightInfo.AirlineName]) {
			airphone = Com_Flightexplorer_Fasttrack.airphones[flightInfo.AirlineName];
		} else if (Com_Flightexplorer_Fasttrack.airphones[this.currentAirline]) {
			airphone = Com_Flightexplorer_Fasttrack.airphones[this.currentAirline];	
		} else if(Com_Flightexplorer_Fasttrack.airphones[flightInfo.AirlineName+" Airlines"]) {
			airphone = Com_Flightexplorer_Fasttrack.airphones[flightInfo.AirlineName+" Airlines"];
		}
		
	
		this.airlineValueCell.innerHTML = flightInfo.AirlineName;
		if(airphone) {
			this.airlineValueCell.innerHTML += [" (", this._objectManager.findObjects(airphone, false,ZmObjectManager.PHONE)," )"].join("");
		}
	} catch (ex) {
		//
	}
	var airwww = null;
	try {
		if(Com_Flightexplorer_Fasttrack.airwww[flightInfo.AirlineName]) {
			airwww = Com_Flightexplorer_Fasttrack.airwww[flightInfo.AirlineName];
		} else if (Com_Flightexplorer_Fasttrack.airwww[this.currentAirline]) {
			airwww = Com_Flightexplorer_Fasttrack.airwww[this.currentAirline];	
		} else if(Com_Flightexplorer_Fasttrack.airwww[flightInfo.AirlineName+" Airlines"]) {
			airwww = Com_Flightexplorer_Fasttrack.airwww[flightInfo.AirlineName+" Airlines"];
		}
		if(airwww) {
			this.airlineValueCell.innerHTML += [" (", this._objectManager.findObjects(airwww, false)," )"].join("");
		}
	} catch (ex) {
	//
	}
	try {
		if(flightInfo.Duration) {
			durationMS = flightInfo.Duration;
			var hours =  Math.floor(durationMS / AjxDateUtil.MSEC_PER_HOUR);		
			var minutes =  Math.floor((durationMS-hours*AjxDateUtil.MSEC_PER_HOUR) / 60000);
				
			var durationStr = "";				
			if(hours) {
				durationStr += hours +" hours ";
			}
			if(minutes) {
				durationStr += minutes +" minutes";
			}
			this.durationValueCell.innerHTML = durationStr;
		}
	} catch (ex) {
		//
	}
	this.aircraftValueCell.innerHTML = flightInfo.AircraftName;
	this.OriginLocation = flightInfo.OriginLocation;
	this.Origin = flightInfo.Origin;
	this.DestinationLocation = flightInfo.DestinationLocation;
	this.Destination = flightInfo.Destination;	
	this.fromAirportValueCell.innerHTML = flightInfo.OriginName + " ("+flightInfo.Origin+")<br>" + flightInfo.OriginLocation;
	this.toAirportValueCell.innerHTML = flightInfo.DestinationName + " ("+flightInfo.Destination+")<br>" + flightInfo.DestinationLocation;	
	this.PDTValueCell.innerHTML = this._objectManager.findObjects(flightInfo.PDT, false);
	this.PTAValueCell.innerHTML = this._objectManager.findObjects(flightInfo.PTA, false);
	this.EDTValueCell.innerHTML = this._objectManager.findObjects(flightInfo.EDT, false);	
	this.ETAValueCell.innerHTML = this._objectManager.findObjects(flightInfo.ETA, false);	


	try {
		var originWeather = "";
		if(flightInfo.OriginTemp) {
			originWeather += (flightInfo.OriginTemp + " F");
		}	
		if(flightInfo.OriginWeatherDesc) {
			if(flightInfo.OriginTemp)
				originWeather += ", "
				
			originWeather += flightInfo.OriginWeatherDesc;
		}	
		this.DWeatValueCell.innerHTML = originWeather;
		this.setWeatherImg(this.fromWeatherImg, flightInfo.OriginWeatherDesc)
	} catch (ex) {
		//something went wrong, need error handling
	}
	
	try {
		var destWeather = "";
		if(flightInfo.DestinationTemp) {
			destWeather += (flightInfo.DestinationTemp + " F");
		}
		
		if(flightInfo.DestinationWeatherDesc) {
			if(flightInfo.DestinationTemp)
				destWeather += ", "
				
			destWeather += flightInfo.DestinationWeatherDesc;
		}	
		this.EWeatValueCell.innerHTML =	destWeather;
		this.setWeatherImg(this.toWeatherImg, flightInfo.DestinationWeatherDesc)				
	} catch (ex) {
		//something went wrong, need error handling
	}
	
	try {
		if(!flightInfo.ImagePath || flightInfo.ImagePath.length<2)		
			this.flightImg.src = this.zimlet.getResource("imgna.gif");
		else
			this.flightImg.src = flightInfo.ImagePath;			
	} catch (ex) {
		//something went wrong, need error handling
	}

	this.flightImg.width=300;
	this.flightImg.height=300;	
	this.PDT = flightInfo.PDT;
	this.PTA = flightInfo.PTA;	
	this.EDT = flightInfo.EDT;		
	this.ETA = flightInfo.ETA;	

	
}

FlightStatusDlg.prototype.setWeatherImg = function (img, weather) {
	switch(weather) {
		case "Partly Cloudy":
			img.src=this.zimlet.getResource("partlycloudy.gif");
		break;
		case "Mostly Cloudy":
			img.src=this.zimlet.getResource("mostlycloudy.gif");
		break;
		case "Rain":
			img.src=this.zimlet.getResource("rain.gif");
		break;			
		case "Overcast":
			img.src=this.zimlet.getResource("overcast.gif");
		break;			
		case "Clear":
			img.src=this.zimlet.getResource("clear.gif");
		break;			
		case "Thunderstorms":
			img.src=this.zimlet.getResource("thunderstorms.gif");
		break;	
		case "Snow":
			img.src=this.zimlet.getResource("snow.gif");
		break;
		case "Ice":
			img.src=this.zimlet.getResource("ice.gif");
		break;
		case "Hail":
			img.src=this.zimlet.getResource("hail.gif");
		break;
		case "Fog":
			img.src=this.zimlet.getResource("fog.gif");
		break;
		case "Dust/Sand":
			img.src=this.zimlet.getResource("dust.gif");
		break;
		case "Smoke":
			img.src=this.zimlet.getResource("smoke.gif");
		break;
		case "Haze ":
			img.src=this.zimlet.getResource("haze.gif");
		break;
	}
}

FlightStatusDlg.prototype.resetFields = 
function(val) {
	if(!val)
		val = "N/A";
	this.statusValueCell.innerHTML = val;	
	this.aircraftValueCell.innerHTML = val;
	this.fromAirportValueCell.innerHTML = val;
	this.toAirportValueCell.innerHTML = val;	
	this.airlineValueCell.innerHTML = val;	
	this.PDTValueCell.innerHTML = val;
	this.PTAValueCell.innerHTML = val;	
	this.EDTValueCell.innerHTML = val;		
	this.ETAValueCell.innerHTML = val;	
}



FlightStatusDlg.prototype._searchButtonListener = 
function() {
	var code = this._flightCodeInputField.getValue();
	this.setFlightCode(code);
	this.resetFields("Please wait. Loading data...");	
	this.zimlet.getFlightDataAndImage(new AjxCallback(this, this.dataClbk),code);
	this.flightImg.src = this.zimlet.getResource("wait.gif");
}

FlightStatusDlg.prototype._getDepYMap =
function () {
	var url = [FlightStatusDlg.YMAP_URL, "?addr=", AjxStringUtil.urlEncode(this.Origin), "&new=1&name=&qty="].join("");
	window.open(url);
}

FlightStatusDlg.prototype._getArrYMap =
function () {
	var url = [FlightStatusDlg.YMAP_URL, "?addr=", AjxStringUtil.urlEncode(this.Destination), "&new=1&name=&qty="].join("");
	window.open(url);
}

FlightStatusDlg.prototype.flightChanged =
function (ev) {
	var code = this._airlineSelect.getValue();
	if(this._flightNumInputField.getValue()) {
		code +=this._flightNumInputField.getValue();
	}
	this.setFlightCode(code);
}


FlightStatusDlg._onChange = 
function (value) {
	this.flightChanged();
	return value;
	/*var el = DwtUiEvent.getTarget(ev);
	if(el) {
		var fdlg = AjxCore.objectWithId(el._fdlgId);
		if(fdlg) {
			fdlg.flightChanged();
		}
	}*/
}
FlightStatusDlg.prototype._add2Cal = 
function () {
	this.zimlet.add2CAL("ADD2CAL", this.flightInfo,this.currentFlightCode);
	this.popdown();
}
