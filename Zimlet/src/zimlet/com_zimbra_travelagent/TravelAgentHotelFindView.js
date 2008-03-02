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

function TravelAgentHotelFindView(parent, zimlet,addr) {
	DwtTabViewPage.call(this,parent);
	this.zimlet = zimlet;
	this._airportsSelectHome = null;
	this._airportsSelectWork = null;
	this._addr = addr;
	this._createHTML(this.hasWorkAddr, this.hasHomeAddr);
	this.setScrollStyle(Dwt.SCROLL);
	this._rendered=false;
}

TravelAgentHotelFindView.prototype = new DwtTabViewPage;
TravelAgentHotelFindView.prototype.constructor = TravelAgentHotelFindView;


// Public methods

TravelAgentHotelFindView.prototype.toString = 
function() {
	return "TravelAgentHotelFindView";
};

TravelAgentHotelFindView.prototype.showMe = 
function () {
	if(!this._rendered)
		this._initialize();
	DwtTabViewPage.prototype.showMe.call(this,parent);
	
	if(this.zimlet) {
		var myPlannerClbk = new AjxCallback(this, this.zimlet.myplannerCallback);
		var url = [ZmZimletBase.PROXY,AjxStringUtil.urlEncode("http://myplanner.org/travelagent.php?id=3")].join("");
		AjxRpc.invoke(null, url, null, myPlannerClbk);
	}
}

TravelAgentHotelFindView.prototype.setAddress =
function(addr) {
	this._addr = addr;
	if(this._checkinAddrField)
		this._checkinAddrField.setValue(addr);
}

TravelAgentHotelFindView.prototype.setCheckoutDate =
function (checkoutDate) {
	this._checkoutDate = checkoutDate;
	if(this._checkoutDateField)
		this._checkoutDateField.value=AjxDateUtil.simpleComputeDateStr(checkoutDate);
}

TravelAgentHotelFindView.prototype.setCheckinDate =
function (checkinDate) {
	this._checkinDate = checkinDate;
	if(this._checkinDateField)
		this._checkinDateField.value=AjxDateUtil.simpleComputeDateStr(checkinDate);
}



TravelAgentHotelFindView.prototype.resize =
function(newWidth, newHeight) {
	if (!this._rendered) return;

	if (newWidth) {
		this.setSize(newWidth);
		Dwt.setSize(this.getHtmlElement().firstChild, newWidth);
	}

	if (newHeight) {
		this.setSize(Dwt.DEFAULT, newHeight - 30);
		Dwt.setSize(this.getHtmlElement().firstChild, Dwt.DEFAULT, newHeight - 30);
	}
};


// Private / protected methods

TravelAgentHotelFindView.prototype._initialize = 
function() {

	this._createDwtObjects();
	this._cacheFields();
/*	this._createDwtObjects();
	this._addEventHandlers();
*/
	this._rendered = true;
};

TravelAgentHotelFindView.prototype._createHTML = 
function() {
	var html = new Array();
	var i = 0;
	this._checkinAddrCellId	= Dwt.getNextId();
	this._checkinDateFieldId	= Dwt.getNextId();
	this._checkinDateMiniCalBtnId	= Dwt.getNextId();
	this._checkinTimeSelectId	= Dwt.getNextId();
	this._countrySelectId = Dwt.getNextId();
	this._stateSelectId = Dwt.getNextId();
	
	this._checkoutDateFieldId = Dwt.getNextId();
	this._checkoutDateMiniCalBtnId = Dwt.getNextId();
	this._checkoutTimeSelectId = Dwt.getNextId();

	this._adultsSelectId = Dwt.getNextId();
	this._roomsSelectId = Dwt.getNextId();
	this._searchButtonId = Dwt.getNextId();
	this._searchButtonId2 = Dwt.getNextId();	
	this._searchButtonId3 = Dwt.getNextId();	
	this._searchButtonId4 = Dwt.getNextId();		

	html[i++] = "<table border=0 width=450 cellspacing=3>";		
	html[i++] = "<tr>";

	//country	
	html[i++] = "<td width=50%>"
	html[i++] = "<table border=0 cellspacing=1 width=100%>";		
	html[i++] = "<tr><td width=100%><div style='float:left;'>\"Country\"</div></td>";
	html[i++] = "<tr><td width=100% id='";
	html[i++] = this._countrySelectId;
	html[i++] = "'></td></tr></table>";
	html[i++] = "</td>";

	//address cell	
	html[i++] = "<td width=50%>"
	html[i++] = "<table border=0 cellspacing=1 width=100%>";		
	html[i++] = "<tr><td width=100%><div style='float:left;'>\"City, State\"</div></td>";
	html[i++] = "<tr><td width=100% id='";
	html[i++] = this._checkinAddrCellId;
	html[i++] = "'></td></tr></table>";
	html[i++] = "</td>";

	html[i++] = "</tr>";	
	html[i++] = "<tr>";	

	//checkin date cell
	html[i++] = "<td width=50%>";
	html[i++] = "<table border=0 cellspacing=1>";		
	html[i++] = "<tr><td width=100 colspan=2>Checkin (mm/dd/yy)</td></tr>";
	html[i++] = "<tr>";
	html[i++] = "<td>";
	html[i++] = "<input style='height:22px;' type='text' autocomplete='off' size=11 maxlength=10 id='";
	html[i++] = this._checkinDateFieldId;
	html[i++] = "'></td><td id='";
	html[i++] = this._checkinDateMiniCalBtnId;
	html[i++] = "'></td></tr></table>";
	html[i++] = "</td>";	

	//checkout date cell	
	html[i++] = "<td width=50%>";
	html[i++] = "<table border=0 cellspacing=1>";		
	html[i++] = "<tr><td width=100 colspan=2>Checkout (mm/dd/yy)</td></tr>";
	html[i++] = "<tr>";
	html[i++] = "<td>";
	html[i++] = "<input style='height:22px;' type='text' autocomplete='off' size=11 maxlength=10 id='";
	html[i++] = this._checkoutDateFieldId;
	html[i++] = "'></td><td id='";
	html[i++] = this._checkoutDateMiniCalBtnId;
	html[i++] = "'></td></tr></table>";
	html[i++] = "</td>";	
	html[i++] = "</tr>";	
	
	//travelers	
	//adults cell
	html[i++] = "<tr>";
	html[i++] = "<td width=50%>";
	html[i++] = "<table border=0 cellspacing=1>";	
	html[i++] = "<tr><td>Adults (age 18+)</td></tr>";
	html[i++] = "<tr><td id='";
	html[i++] = this._adultsSelectId;
	html[i++] = "'></td></tr></table>";
	html[i++] = "</td>";
	//rooms cell
	html[i++] = "<td width=50%>";
	html[i++] = "<table border=0 cellspacing=1>";	
	html[i++] = "<tr><td>Rooms</td></tr>";
	html[i++] = "<tr><td id='";
	html[i++] = this._roomsSelectId;
	html[i++] = "'></td></tr></table>";
	html[i++] = "</td>";
	html[i++] = "</tr>";	

	//searh buttons
	html[i++] = "<tr>";
	html[i++] = "<td colspan=2 width=100%>";
	html[i++] = "<table border=0 cellspacing=1>"
	html[i++] = "<tr>";
	html[i++] = "<td align='center' id='";
	html[i++] = this._searchButtonId
	html[i++] = "'></td>";

	html[i++] = "<td align='center' id='";
	html[i++] = this._searchButtonId2
	html[i++] = "'></td>";

	html[i++] = "<td align='center' id='";
	html[i++] = this._searchButtonId3
	html[i++] = "'></td>";

	html[i++] = "</tr>";
	html[i++] = "</table>";
	html[i++] = "</td>";
	html[i++] = "</tr>";	

	html[i++] = "</table>";
	this.getHtmlElement().innerHTML = html.join("");
};

TravelAgentHotelFindView.prototype._createDwtObjects = 
function () {
	this._checkinAddrField = new DwtInputField({parent:this, type:DwtInputField.STRING,
											initialValue:this._addr, size:null, maxLen:null,
											errorIconStyle:DwtInputField.ERROR_ICON_NONE,
											validationStyle:DwtInputField.ONEXIT_VALIDATION});
											
	Dwt.setSize(this._checkinAddrField.getInputElement(), "100%", "22px");	
	this._checkinAddrField.reparentHtmlElement(this._checkinAddrCellId);
	delete this._checkinAddrCellId;	
	

	var dateButtonListener = new AjxListener(this, this._dateButtonListener);
	var dateCalSelectionListener = new AjxListener(this, this._dateCalSelectionListener);
		
	this._checkinDateButton = ZmCalendarApp.createMiniCalButton(this, this._checkinDateMiniCalBtnId, dateButtonListener, dateCalSelectionListener);
									
	this._checkoutDateButton = ZmCalendarApp.createMiniCalButton(this, this._checkoutDateMiniCalBtnId, dateButtonListener, dateCalSelectionListener);
	

	var searchSideStep=true;
	var searchTravelocity=true;
	var searchHotwire=true;
	try {
		searchSideStep = this.zimlet.getUserProperty("search_sidestep")
		searchTravelocity = this.zimlet.getUserProperty("search_travelocity")		
		searchHotwire = this.zimlet.getUserProperty("search_hotwire")				
	} catch (ex) {
	//sigh
	}	
	
	if(searchSideStep=="true") {
		var searchButton = new DwtButton({parent:this});	
		searchButton.setText("Search SideStep.com");
		searchButton.setImage("SideStepIcon");		
		searchButton.setSize("140");
		searchButton.addSelectionListener(new AjxListener(this, this._searchButtonListener));				
		var searchButtonCell = document.getElementById(this._searchButtonId);
		if (searchButtonCell)
			searchButtonCell.appendChild(searchButton.getHtmlElement());
	}
	
	if(searchTravelocity=="true") {
		var searchButton2 = new DwtButton({parent:this});	
		searchButton2.setText("Search travelocity.com");
		searchButton2.setImage("TravelocityIcon");			
		searchButton2.setSize("140");
		searchButton2.addSelectionListener(new AjxListener(this, this._searchButtonListener2));				
		var searchButtonCell2 = document.getElementById(this._searchButtonId2);
		if (searchButtonCell2)
			searchButtonCell2.appendChild(searchButton2.getHtmlElement());
	}
	
	if(searchHotwire=="true") {
		var searchButton3 = new DwtButton({parent:this});	
		searchButton3.setText("Search hotwire.com");
		searchButton3.setImage("HotwireIcon");		
		searchButton3.setSize("140");
		searchButton3.addSelectionListener(new AjxListener(this, this._searchButtonListener3));				
		var searchButtonCell3 = document.getElementById(this._searchButtonId3);
		if (searchButtonCell3)
			searchButtonCell3.appendChild(searchButton3.getHtmlElement());
	}
	
	this._countrySelect = new DwtSelect({parent:this, options:[	new DwtSelectOption("AG", false, "Antigua and Barbuda"),new DwtSelectOption("AW",false,"Aruba"),
		new DwtSelectOption("AU",false,"Australia"),new DwtSelectOption("AT",false,"Austria"),
		new DwtSelectOption("BS",false,"Bahamas"),new DwtSelectOption("BB",false,"Barbados"),new DwtSelectOption("BE",false,"Belgium"),
		new DwtSelectOption("BM",false,"Bermuda"),new DwtSelectOption("ANB",false,"Bonaire"),new DwtSelectOption("BR",false,"Brazil"),new DwtSelectOption("CA",false,"Canada"),
		new DwtSelectOption("KY",false,"Cayman Islands"),new DwtSelectOption("CN",false,"China"),new DwtSelectOption("ANC",false,"Curacao"),new DwtSelectOption("CZ",false,"Czech Republic"),
		new DwtSelectOption("DK",false,"Denmark"),new DwtSelectOption("DO",false,"Dominican Republic"),new DwtSelectOption("GBE",false,"England"),new DwtSelectOption("FI",false,"Finland"),
		new DwtSelectOption("FR",false,"France"),new DwtSelectOption("DE",false,"Germany"),new DwtSelectOption("GR",false,"Greece"),new DwtSelectOption("HU",false,"Hungary"),
		new DwtSelectOption("IE",false,"Ireland"),new DwtSelectOption("IT",false,"Italy"),new DwtSelectOption("JM",false,"Jamaica"),new DwtSelectOption("MX",false,"Mexico"),
		new DwtSelectOption("NL",false,"Netherlands"),new DwtSelectOption("AN",false,"Netherlands Antilles"),new DwtSelectOption("NO",false,"Norway"),new DwtSelectOption("PL",false,"Poland"),
		new DwtSelectOption("PT",false,"Portugal"),new DwtSelectOption("PR",false,"Puerto Rico"),new DwtSelectOption("ANS",false,"Saba"),new DwtSelectOption("LC",false,"Saint Lucia"),
		new DwtSelectOption("GBS",false,"Scotland"),new DwtSelectOption("SG",false,"Singapore"),new DwtSelectOption("ES",false,"Spain and Canary Islands"),new DwtSelectOption("KN",false,"St Kitts"),
		new DwtSelectOption("ANE",false,"St. Eustacius"),new DwtSelectOption("ANM",false,"St. Martin/St. Maarten"),new DwtSelectOption("SE",false,"Sweden"),new DwtSelectOption("CH",false,"Switzerland"),
		new DwtSelectOption("TW",false,"Taiwan"),new DwtSelectOption("TH",false,"Thailand"),new DwtSelectOption("TT",false,"Trinidad and Tobago"),new DwtSelectOption("TC",false,"Turks and Caicos Islands"),
		new DwtSelectOption("GB",false,"United Kingdom"),new DwtSelectOption("US",true,"United States"),new DwtSelectOption("VG",false,"Virgin Islands British"),new DwtSelectOption("VI",false,"Virgin Islands US"),
		new DwtSelectOption(null, false, "All Other Destinations")	
	]});
	var countryCell = document.getElementById(this._countrySelectId);
	if (countryCell)
		countryCell.appendChild(this._countrySelect.getHtmlElement());	
	
	this._adultSelect = new DwtSelect({parent:this, options:[new DwtSelectOption("1", true, "1"), 
	new DwtSelectOption("2", false, "2"),
	new DwtSelectOption("3", false, "3"),
	new DwtSelectOption("4", false, "4")]});
	var adultCell = document.getElementById(this._adultsSelectId);
	if (adultCell)
		adultCell.appendChild(this._adultSelect.getHtmlElement());	
		
	this._roomsSelect = new DwtSelect({parent:this, options:[new DwtSelectOption("0", true, "0"),
	new DwtSelectOption("1", true, "1"), 
	new DwtSelectOption("2", false, "2"),
	new DwtSelectOption("3", false, "3"),
	new DwtSelectOption("4", false, "4")]});
	var roomsCell = document.getElementById(this._roomsSelectId);
	if (roomsCell)
		roomsCell.appendChild(this._roomsSelect.getHtmlElement());		
};

TravelAgentHotelFindView.prototype._cacheFields = 
function() {
	this._checkinDateField 	= document.getElementById(this._checkinDateFieldId);
	if(this._checkinDate)
		this._checkinDateField.value=AjxDateUtil.simpleComputeDateStr(this._checkinDate);
	
	delete this._checkinDateFieldId;
	this._checkoutDateField = document.getElementById(this._checkoutDateFieldId);	
	if(this._checkoutDate)
		this._checkoutDateField.value=AjxDateUtil.simpleComputeDateStr(this._checkoutDate);
		
	delete this._checkoutDateFieldId;

};


TravelAgentHotelFindView.prototype._dateButtonListener = function(ev) {
	var calDate = ev.item == this._checkinDateButton
		? AjxDateUtil.simpleParseDateStr(this._checkinDateField.value)
		: AjxDateUtil.simpleParseDateStr(this._checkoutDateField.value);

	// if date was input by user and its foobar, reset to today's date
	if (isNaN(calDate) || !calDate) {
		calDate = new Date();
		var field = ev.item == this._checkinDateButton
			? this._checkinDateField : this._checkoutDateField;
		field.value = AjxDateUtil.simpleComputeDateStr(calDate);
	}

	// always reset the date to current field's date
	var menu = ev.item.getMenu();
	var cal = menu.getItem(0);
	cal.setDate(calDate, true);
	ev.item.popup();
};

TravelAgentHotelFindView.prototype._dateCalSelectionListener = function(ev) {
	var parentButton = ev.item.parent.parent;

	// do some error correction... maybe we can optimize this?
	var sd;
	if(this._checkinDateField.value)
		sd = AjxDateUtil.simpleParseDateStr(this._checkinDateField.value);
	var ed; 
	if(this._checkoutDateField.value)
		ed = AjxDateUtil.simpleParseDateStr(this._checkoutDateField.value);
	var newDate = AjxDateUtil.simpleComputeDateStr(ev.detail);

	// change the start/end date if they mismatch
	if (parentButton == this._checkinDateButton) {
		if (ed && (ed.valueOf() < ev.detail.valueOf()))
			this._checkoutDateField.value = newDate;
		this._checkinDateField.value = newDate;
	} else {
		if (sd && (sd.valueOf() > ev.detail.valueOf()))
			this._checkinDateField.value = newDate;
		this._checkoutDateField.value = newDate;
	}
};

TravelAgentHotelFindView.prototype._searchButtonListener = 
function (ev) {
	var props = [ "toolbar=no,location=no,status=yes,menubar=yes,scrollbars=yes,resizable=yes" ];
	props = props.join(",");
	var country = this._countrySelect.getValue();
	
	var browserUrl = ["http://myplanner.org/travel_hotel.php?","tripType=city",
	"&checkinDate=",this._checkinDateField.value,"&checkoutDate=",this._checkoutDateField.value,
	"&numberOfAdults=",this._adultSelect.getValue(),"&numberOfRooms=",this._roomsSelect.getValue(),
	"&city=",this._checkinAddrField.getValue(),(country ? (","+country) : " ") ].join("");

	var canvas = window.open(browserUrl, "SideStep.com finds", props);

};

TravelAgentHotelFindView.prototype._searchButtonListener2 = 
function (ev) {
	var props = [ "toolbar=no,location=no,status=yes,menubar=yes,scrollbars=yes,resizable=yes" ];
	props = props.join(",");
	var country = this._countrySelect.getValue();
	var city = this._checkinAddrField.getValue();
	var state = ""
	if(String(city).indexOf(",")>0) {
		var parts = city.split(",")
		if(parts.length>0) {
			state = parts[1];
			city = parts[0];
		}
	}
	
	var browserUrl = ["http://myplanner.org/travelocity_hotel.php?",
	"&checkinDate=",this._checkinDateField.value,"&checkoutDate=",this._checkoutDateField.value,
	"&numberOfAdults=",this._adultSelect.getValue(),"&city=",city,
	"&country=",country,"&state=",state].join("");

	var canvas = window.open(browserUrl, "Travelocity.com finds", props);

};

TravelAgentHotelFindView.prototype._searchButtonListener3 = 
function (ev) {
	var props = [ "toolbar=no,location=no,status=yes,menubar=yes,scrollbars=yes,resizable=yes" ];
	props = props.join(",");
	var country = this._countrySelect.getValue();
	
	var browserUrl = ["http://myplanner.org/hotwire_hotel.php?","tripType=city",
	"&checkinDate=",this._checkinDateField.value,"&checkoutDate=",this._checkoutDateField.value,
	"&numberOfAdults=",this._adultSelect.getValue(),"&numberOfRooms=",this._roomsSelect.getValue(),
	"&city=",this._checkinAddrField.getValue(),(country ? (","+country) : " ") ].join("");

	var canvas = window.open(browserUrl, "Hotwire.com finds", props);

};

