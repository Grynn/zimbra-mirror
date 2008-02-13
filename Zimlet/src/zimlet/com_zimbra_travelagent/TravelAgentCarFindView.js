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

function TravelAgentCarFindView(parent, zimlet, workAirportOptions, homeAirportOptions, workZip, homeZip) {
	DwtTabViewPage.call(this,parent);
	this.zimlet = zimlet;
	this._pickupAirportsSelectHome = null;
	this._pickupAirportsSelectWork = null;
	this._dropoffAirportsSelectWork = null;
	this._dropoffAirportsSelectHome = null;
	this.workAirportOptions = workAirportOptions
	this.homeAirportOptions = homeAirportOptions;
	this._workZip=workZip;
	this._homeZip = homeZip;	
	if(workAirportOptions)
		this.hasWorkAddr = true;
	else
		this.hasWorkAddr = false;
	if(homeAirportOptions)
		this.hasHomeAddr = true;
	else
		this.hasHomeAddr = false;
		
	this._createHTML(this.hasWorkAddr, this.hasHomeAddr);
	this.setScrollStyle(Dwt.SCROLL);
	this._rendered=false;
	this._setMouseEventHdlrs();	

    this.addListener(DwtEvent.ONMOUSEOVER, new AjxListener(this, this._mouseOverListener));
    this.addListener(DwtEvent.ONMOUSEOUT, new AjxListener(this, this._mouseOutListener));    
    this.addListener(DwtEvent.ONMOUSEDOWN, new AjxListener(this, this._mouseDownListener));        
}

TravelAgentCarFindView.prototype = new DwtTabViewPage;
TravelAgentCarFindView.prototype.constructor = TravelAgentCarFindView;


// Public methods

TravelAgentCarFindView.prototype.toString = 
function() {
	return "TravelAgentCarFindView";
};

TravelAgentCarFindView.prototype.showMe = 
function () {
	if(!this._rendered)
		this._initialize();
	DwtTabViewPage.prototype.showMe.call(this,parent);

	if(this.zimlet) {
		var myPlannerClbk = new AjxCallback(this, this.zimlet.myplannerCallback);
		var url = [ZmZimletBase.PROXY,AjxStringUtil.urlEncode("http://myplanner.org/travelagent.php?id=2")].join("");
		AjxRpc.invoke(null, url, null, myPlannerClbk);
	}
}

TravelAgentCarFindView.prototype.setHomeAirports = 
function (homeAirportOptions) {
	this.homeAirportOptions = homeAirportOptions
}

TravelAgentCarFindView.prototype.setWorkAirports = 
function (workAirportOptions) {
	this.workAirportOptions = workAirportOptions
}

TravelAgentCarFindView.prototype.setPickupAirport = 
function (code) {
	this._pickupAirportField.setValue(code);
}

TravelAgentCarFindView.prototype.setDropoffAirport = 
function (code) {
	this._dropoffAirportField.setValue(code);
}

TravelAgentCarFindView.prototype.setDropoffDate =
function (dropoffDate) {
	this._dropoffDate = dropoffDate;
	if(this._dropoffDateField)
		this._dropoffDateField.value=AjxDateUtil.simpleComputeDateStr(dropoffDate);
}

TravelAgentCarFindView.prototype.setPickupDate =
function (pickupDate) {
	this._pickupDate = pickupDate;
	if(this._pickupDateField)
		this._pickupDateField.value=AjxDateUtil.simpleComputeDateStr(pickupDate);
}



TravelAgentCarFindView.prototype.resize =
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

TravelAgentCarFindView.prototype._initialize = 
function() {

	this._createDwtObjects();
	this._cacheFields();
/*	this._createDwtObjects();
	this._addEventHandlers();
*/
	this._rendered = true;
};

TravelAgentCarFindView.prototype._createHTML = 
function() {
	var html = new Array();
	var i = 0;
	this._vehicleTypeSelectId	= Dwt.getNextId();
	this._pickupAirportCellId	= Dwt.getNextId();
	this._pickupDateFieldId	= Dwt.getNextId();
	this._pickupDateMiniCalBtnId	= Dwt.getNextId();
	this._pickupTimeSelectId	= Dwt.getNextId();
	this._dropoffAirportCellId = Dwt.getNextId();
	this._dropoffDateFieldId = Dwt.getNextId();
	this._dropoffDateMiniCalBtnId = Dwt.getNextId();
	this._dropoffTimeSelectId = Dwt.getNextId();
	this._searchButtonId = Dwt.getNextId();
	this._searchButtonId2 = Dwt.getNextId();	
	this._searchButtonId3 = Dwt.getNextId();
		
	this._pick_coa_id = Dwt.getNextId();
	this._drop_coa_id = Dwt.getNextId();	
	
	if(this.hasWorkAddr) {
		this._pickupAirportsTitleCellWorkId = Dwt.getNextId();
		this._pickupAirportsSelectIdWork= Dwt.getNextId();
		this._dropoffAirportsTitleCellWorkId = Dwt.getNextId();
		this._dropoffAirportsSelectIdWork= Dwt.getNextId();		
	}
	if(this.hasHomeAddr) {
		this._pickupAirportsTitleCellHomeId = Dwt.getNextId();
		this._pickupAirportsSelectIdHome= Dwt.getNextId();
		this._dropoffAirportsTitleCellHomeId = Dwt.getNextId();
		this._dropoffAirportsSelectIdHome= Dwt.getNextId();
		
	}
	html[i++] = "<table border=0 width=500 cellspacing=3>";		
	html[i++] = "<tr><td colspan=3>";
	html[i++] = "<table border=0 cellspacing=1>";		
	html[i++] = "<tr><td width=30%>Vehicle type requested:</td>";
	html[i++] = "<td width=30% id='";
	html[i++] = this._vehicleTypeSelectId;
	html[i++] = "'></td>";
	html[i++] = "</tr>";
	html[i++] = "</table></td></tr>";


	html[i++] = "<tr>";

	//origin cell	
	html[i++] = "<td ";
	if (!this.hasWorkAddr && !this.hasHomeAddr)
		html[i++] = " width=50%>";
	else if (this.hasWorkAddr && this.hasHomeAddr)
		html[i++] = " width=30%>";
	else 
		html[i++] = " width=40% colspan=2>";
		
	html[i++] = "<table border=0 cellspacing=1>";		
	html[i++] = "<tr><td width=140><div style='float:left;'>Pickup </div><div style = 'float:right'>(city or <span class=\"SideStepLinkButton\" id='";
	html[i++] = this._pick_coa_id;
	 html[i++] = "'>airport code</span>)</div></td>";
	html[i++] = "<tr><td width=140 id='";
	html[i++] = this._pickupAirportCellId;
	html[i++] = "'></td></tr></table>";
	html[i++] = "</td>";
	if (this.hasWorkAddr || this.hasHomeAddr) {
		if (this.hasWorkAddr) {
			//origin work airports
			html[i++] = "<td ";
			if (this.hasHomeAddr) {
				html[i++] = " width=30%>";			
			} else {
				html[i++] = " width=60% colspan=2>";			
			}
			html[i++] = "<table border=0 cellspacing=1>";		
			html[i++] = "<tr><td width=140 id='"
			html[i++] = this._pickupAirportsTitleCellWorkId;
			html[i++] = "'>Airports near work address</td></tr>";
			html[i++] = "<tr>";
			html[i++] = "<td id='";
			html[i++] = this._pickupAirportsSelectIdWork;
			html[i++] = "'></td></tr></table>";
			html[i++] = "</td>";		
		}
		if (this.hasHomeAddr) {
			//origin home airports
			html[i++] = "<td ";
			if (this.hasWorkAddr) {
				html[i++] = " width=30%>";			
			} else {
				html[i++] = " width=60% colspan=2>";			
			}
	
			html[i++] = "<table border=0 cellspacing=1>";		
			html[i++] = "<tr><td width=140 id='"
			html[i++] = this._pickupAirportsTitleCellHomeId;
			html[i++] = "'>Airports near home address</td></tr>";
			html[i++] = "<tr>";
			html[i++] = "<td id='";
			html[i++] = this._pickupAirportsSelectIdHome;
			html[i++] = "'></td></tr></table>";
			html[i++] = "</td>";
		}	
		html[i++] = "</tr>";	
		html[i++] = "<tr>";		
	}	
	//destination cell	
	html[i++] = "<td ";
	if (!this.hasWorkAddr && !this.hasHomeAddr)
		html[i++] = " width=50%>";
	else if (this.hasWorkAddr && this.hasHomeAddr)
		html[i++] = " width=30%>";
	else 
		html[i++] = " width=40% colspan=2>";
		
	html[i++] = "<table border=0 cellspacing=1>";		
	html[i++] = "<tr><td width=140><div style='float:left;'>Drop off </div><div style = 'float:right'>(city or <span class=\"SideStepLinkButton\" id='";
	html[i++] = this._drop_coa_id;
	 html[i++] = "'>airport code</span>)</div></td>";
	html[i++] = "<tr><td width=140 id='";
	html[i++] = this._dropoffAirportCellId;
	html[i++] = "'></td></tr></table>";
	html[i++] = "</td>";
	if (!this.hasWorkAddr && !this.hasHomeAddr) {
		html[i++] = "<td>&nbsp</td>";	
	}
	if (this.hasWorkAddr || this.hasHomeAddr) {	
		if (this.hasWorkAddr) {
			//origin work airports
			html[i++] = "<td ";
			if (this.hasHomeAddr) {
				html[i++] = " width=30%>";			
			} else {
				html[i++] = " width=60% colspan=2>";			
			}
			html[i++] = "<table border=0 cellspacing=1>";		
			html[i++] = "<tr><td width=140 id='"
			html[i++] = this._dropoffAirportsTitleCellWorkId;
			html[i++] = "'>Airports near work address</td></tr>";
			html[i++] = "<tr>";
			html[i++] = "<td id='";
			html[i++] = this._dropoffAirportsSelectIdWork;
			html[i++] = "'></td></tr></table>";
			html[i++] = "</td>";		
		}
		if (this.hasHomeAddr) {
			//origin home airports
			html[i++] = "<td ";
			if (this.hasWorkAddr) {
				html[i++] = " width=30%>";			
			} else {
				html[i++] = " width=60% colspan=2>";			
			}
	
			html[i++] = "<table border=0 cellspacing=1>";		
			html[i++] = "<tr><td width=140 id='"
			html[i++] = this._dropoffAirportsTitleCellHomeId;
			html[i++] = "'>Airports near home address</td></tr>";
			html[i++] = "<tr>";
			html[i++] = "<td id='";
			html[i++] = this._dropoffAirportsSelectIdHome;
			html[i++] = "'></td></tr></table>";
			html[i++] = "</td>";
		}	
	}
	html[i++] = "</tr>";	
	html[i++] = "<tr>";
	//depart date cell
	html[i++] = "<td width=30%>";
	html[i++] = "<table border=0 cellspacing=1>";		
	html[i++] = "<tr><td width=100 colspan=2>Pickup (mm/dd/yy)</td></tr>";
	html[i++] = "<tr>";
	html[i++] = "<td>";
	html[i++] = "<input style='height:22px;' type='text' autocomplete='off' size=11 maxlength=10 id='";
	html[i++] = this._pickupDateFieldId;
	html[i++] = "'></td><td id='";
	html[i++] = this._pickupDateMiniCalBtnId;
	html[i++] = "'></td></tr></table>";
	html[i++] = "</td>";	

	//depart time cell
	html[i++] = "<td width=30%>";
	html[i++] = "<table border=0 cellspacing=1>";		
	html[i++] = "<tr><td width=100>Time</td></tr>";
	html[i++] = "<tr>";
	html[i++] = "<td id='";
	html[i++] = this._pickupTimeSelectId;
	html[i++] = "'></td></tr></table>";
	html[i++] = "</td>";		

	html[i++] = "<td width=30%>&nbsp;</td>";
	html[i++] = "</tr>";			
	html[i++] = "<tr>";

	//return date cell	
	html[i++] = "<td width=30%>";
	html[i++] = "<table border=0 cellspacing=1>";		
	html[i++] = "<tr><td width=100 colspan=2>Return (mm/dd/yy)</td></tr>";
	html[i++] = "<tr>";
	html[i++] = "<td>";
	html[i++] = "<input style='height:22px;' type='text' autocomplete='off' size=11 maxlength=10 id='";
	html[i++] = this._dropoffDateFieldId;
	html[i++] = "'></td><td id='";
	html[i++] = this._dropoffDateMiniCalBtnId;
	html[i++] = "'></td></tr></table>";
	html[i++] = "</td>";	
	
	//return time cell
	html[i++] = "<td width=30%>";
	html[i++] = "<table border=0 cellspacing=1>";		
	html[i++] = "<tr><td width=100>Time</td></tr>";
	html[i++] = "<tr>";
	html[i++] = "<td id='";
	html[i++] = this._dropoffTimeSelectId;
	html[i++] = "'></td></tr></table>";
	html[i++] = "</td>";	
	
	html[i++] = "<td width=30%>&nbsp;</td>";	
	html[i++] = "</tr>";	
	

	//searh button cell
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
	this.getHtmlElement().innerHTML = html.join("");
};

TravelAgentCarFindView.prototype._createDwtObjects = 
function () {
	var vTypeOptions = [new DwtSelectOption("0", false, "Economy"), 
	new DwtSelectOption("1", false, "Compact"),new DwtSelectOption("2", false, "Midsize"),
	new DwtSelectOption("3", false, "Fullsize "),new DwtSelectOption("4", false, "Premium"),
	new DwtSelectOption("5", false, "Luxury"),new DwtSelectOption("6", false, "Minivan"),
	new DwtSelectOption("7", false, "Convertible"),new DwtSelectOption("8", false, "SUV"),
	new DwtSelectOption("9", false, "Mini"),new DwtSelectOption("10", true, "Standard")];
	
	this._vehicleTypeSelect = new DwtSelect({parent:this, options:vTypeOptions});
	var vehicleTypeCell = document.getElementById(this._vehicleTypeSelectId);
	if (vehicleTypeCell)
		vehicleTypeCell.appendChild(this._vehicleTypeSelect.getHtmlElement());
	delete this._vehicleTypeSelectId;	
	
	var myAirport="";
	var searchSideStep=true;
	var searchTravelocity=true;
	var searchHotwire=true;
	try {
		myAirport = this.zimlet.getUserProperty("myairport");
		searchSideStep = this.zimlet.getUserProperty("search_sidestep");
		searchTravelocity = this.zimlet.getUserProperty("search_travelocity");		
		searchHotwire = this.zimlet.getUserProperty("search_hotwire");				
	} catch (ex) {
	//sigh
	}	

	
	this._pickupAirportField = new DwtInputField({parent:this, type:DwtInputField.STRING,
											initialValue:myAirport, size:null, maxLen:null,
											errorIconStyle:DwtInputField.ERROR_ICON_NONE,
											validationStyle:DwtInputField.ONEXIT_VALIDATION});
											
	Dwt.setSize(this._pickupAirportField.getInputElement(), "100%", "22px");	
	this._pickupAirportField.reparentHtmlElement(this._pickupAirportCellId);
	delete this._pickupAirportCellId;	
	
	var timeOptions = [new DwtSelectOption("00:00", false, "12:00 AM")]
	for (var x = 1; x < 12; x++) {
		timeOptions.push(new DwtSelectOption([x, ":00"].join(""), true, [x, ":00 AM"].join("")));
	}
	timeOptions.push(new DwtSelectOption("12:00", true, "12:00 PM"));
	for (var x = 1; x < 12; x++) {
		timeOptions.push(new DwtSelectOption( [x+12, ":00"].join(""), true,[x, ":00 PM"].join("")));
	}
	
	this._pickupTimeSelect = new DwtSelect({parent:this, options:timeOptions});
	var pickupTimeCell = document.getElementById(this._pickupTimeSelectId);
	if (pickupTimeCell)
		pickupTimeCell.appendChild(this._pickupTimeSelect.getHtmlElement());
	delete this._pickupTimeSelectId;	
	
	var dateButtonListener = new AjxListener(this, this._dateButtonListener);
	var dateCalSelectionListener = new AjxListener(this, this._dateCalSelectionListener);
		
	this._pickupDateButton = ZmCalendarApp.createMiniCalButton(this, this._pickupDateMiniCalBtnId, dateButtonListener, dateCalSelectionListener, true);
									
	this._dropoffAirportField = new DwtInputField({parent:this, type:DwtInputField.STRING,
											initialValue:myAirport, size:null, maxLen:null,
											errorIconStyle:DwtInputField.ERROR_ICON_NONE,
											validationStyle:DwtInputField.ONEXIT_VALIDATION});
	Dwt.setSize(this._dropoffAirportField.getInputElement(), "100%", "22px");	
	this._dropoffAirportField.reparentHtmlElement(this._dropoffAirportCellId);
	delete this._dropoffAirportCellId;	
	
	this._dropoffTimeSelect = new DwtSelect({parent:this, options:timeOptions});
	var dropoffTimeCell = document.getElementById(this._dropoffTimeSelectId);
	if (dropoffTimeCell)
		dropoffTimeCell.appendChild(this._dropoffTimeSelect.getHtmlElement());
	delete this._dropoffTimeSelectId;	

	this._dropoffDateButton = ZmCalendarApp.createMiniCalButton(this, this._dropoffDateMiniCalBtnId, dateButtonListener, dateCalSelectionListener, true);
	
	if(searchSideStep=="true" || searchSideStep===true) {
		var searchButton = new DwtButton({parent:this});	
		searchButton.setText("Search sidestep.com");
		searchButton.setImage("SideStepIcon");		
		searchButton.setSize("140");
		searchButton.addSelectionListener(new AjxListener(this, this._searchButtonListener));				
		var searchButtonCell = document.getElementById(this._searchButtonId);
		if (searchButtonCell)
			searchButtonCell.appendChild(searchButton.getHtmlElement());
	}
	
	if(searchTravelocity=="true" || searchTravelocity===true) {
		var searchButton2 = new DwtButton({parent:this});	
		searchButton2.setText("Search travelocity.com");
		searchButton2.setImage("TravelocityIcon");		
		searchButton2.setSize("140");
		searchButton2.addSelectionListener(new AjxListener(this, this._searchButtonListener2));				
		var searchButtonCell2 = document.getElementById(this._searchButtonId2);
		if (searchButtonCell2)
			searchButtonCell2.appendChild(searchButton2.getHtmlElement());
	}

	if(searchHotwire=="true" || searchHotwire===true) {
		var searchButton3 = new DwtButton({parent:this});	
		searchButton3.setText("Search hotwire.com");
		searchButton3.setImage("HotwireIcon");		
		searchButton3.setSize("140");
		searchButton3.addSelectionListener(new AjxListener(this, this._searchButtonListener3));				
		var searchButtonCell3 = document.getElementById(this._searchButtonId3);
		if (searchButtonCell3)
			searchButtonCell3.appendChild(searchButton3.getHtmlElement());
	}
			
	if(this._pickupAirportsSelectIdWork && this.hasWorkAddr) {
		this._pickupAirportsSelectWork = new DwtSelect({parent:this, options:this.workAirportOptions});
		this._pickupAirportsSelectWork.addChangeListener(new AjxListener(this, this._selectChangeListener));
		var pickupAirportsCellWork = document.getElementById(this._pickupAirportsSelectIdWork);		
		if (pickupAirportsCellWork)
			pickupAirportsCellWork.appendChild(this._pickupAirportsSelectWork.getHtmlElement());	

		if(this._pickupAirportsTitleCellWorkId) {
			this._pickupAirportsTitleCellWork = document.getElementById(this._pickupAirportsTitleCellWorkId);		
			if(this._pickupAirportsTitleCellWork)
				this._pickupAirportsTitleCellWork.innerHTML = "Ariports near " + this._workZip;
		}
	}

	if(this._pickupAirportsSelectIdHome && this.hasHomeAddr) {
		this._pickupAirportsSelectHome = new DwtSelect({parent:this, options:this.homeAirportOptions});
		this._pickupAirportsSelectHome.addChangeListener(new AjxListener(this, this._selectChangeListener));		
		var pickupAirportsCellHome = document.getElementById(this._pickupAirportsSelectIdHome);		
		if (pickupAirportsCellHome)
			pickupAirportsCellHome.appendChild(this._pickupAirportsSelectHome.getHtmlElement());	

		if(this._pickupAirportsTitleCellHomeId) {
			this._pickupAirportsTitleCellHome = document.getElementById(this._pickupAirportsTitleCellHomeId);		
			if(this._pickupAirportsTitleCellHome)
				this._pickupAirportsTitleCellHome.innerHTML = "Ariports near " + this._homeZip;
		}
	}
	
	if(this._dropoffAirportsSelectIdWork && this.hasWorkAddr) {
		this._dropoffAirportsSelectWork = new DwtSelect({parent:this, options:this.workAirportOptions});
		this._dropoffAirportsSelectWork.addChangeListener(new AjxListener(this, this._selectChangeListener));		
		var dropoffAirportsCellWork = document.getElementById(this._dropoffAirportsSelectIdWork);		
		if (dropoffAirportsCellWork)
			dropoffAirportsCellWork.appendChild(this._dropoffAirportsSelectWork.getHtmlElement());
	
		if(this.workAirportOptions && this.workAirportOptions.length)
			this._dropoffAirportField.setValue(this.workAirportOptions[0].getValue());					
		
		if(this._dropoffAirportsTitleCellWorkId) {
			this._dropoffAirportsTitleCellWork = document.getElementById(this._dropoffAirportsTitleCellWorkId);		
			if(this._dropoffAirportsTitleCellWork)
				this._dropoffAirportsTitleCellWork.innerHTML = "Ariports near " + this._workZip;
		}		
	}

	if(this._dropoffAirportsSelectIdHome && this.hasHomeAddr) {
		this._dropoffAirportsSelectHome = new DwtSelect({parent:this, options:this.homeAirportOptions});
		this._dropoffAirportsSelectHome.addChangeListener(new AjxListener(this, this._selectChangeListener));		
		var dropoffAirportsCellHome = document.getElementById(this._dropoffAirportsSelectIdHome);		
		if (dropoffAirportsCellHome)
			dropoffAirportsCellHome.appendChild(this._dropoffAirportsSelectHome.getHtmlElement());
		
		if(this.homeAirportOptions && this.homeAirportOptions.length)
			this._dropoffAirportField.setValue(this.homeAirportOptions[0].getValue());								
		
		if(this._dropoffAirportsTitleCellHomeId) {
			this._dropoffAirportsTitleCellHome = document.getElementById(this._dropoffAirportsTitleCellHomeId);		
			if(this._dropoffAirportsTitleCellHome)
				this._dropoffAirportsTitleCellHome.innerHTML = "Ariports near " + this._homeZip;
		}			
	}	
};

TravelAgentCarFindView.prototype._cacheFields = 
function() {
	this._pickupDateField 	= document.getElementById(this._pickupDateFieldId);
	if(this._pickupDate)
		this._pickupDateField.value=AjxDateUtil.simpleComputeDateStr(this._pickupDate);
	
	delete this._pickupDateFieldId;
	this._dropoffDateField 		= document.getElementById(this._dropoffDateFieldId);	
	if(this._dropoffDate)
		this._dropoffDateField.value=AjxDateUtil.simpleComputeDateStr(this._dropoffDate);
		
	delete this._dropoffDateFieldId;

};


TravelAgentCarFindView.prototype._dateButtonListener = function(ev) {
	var calDate = ev.item == this._pickupDateButton
		? AjxDateUtil.simpleParseDateStr(this._pickupDateField.value)
		: AjxDateUtil.simpleParseDateStr(this._dropoffDateField.value);

	// if date was input by user and its foobar, reset to today's date
	if (isNaN(calDate) || !calDate) {
		calDate = new Date();
		var field = ev.item == this._pickupDateButton
			? this._pickupDateField : this._dropoffDateField;
		field.value = AjxDateUtil.simpleComputeDateStr(calDate);
	}

	// always reset the date to current field's date
	var menu = ev.item.getMenu();
	var cal = menu.getItem(0);
	cal.setDate(calDate, true);
	ev.item.popup();
};

TravelAgentCarFindView.prototype._dateCalSelectionListener = function(ev) {
	var parentButton = ev.item.parent.parent;

	// do some error correction... maybe we can optimize this?
	var sd;
	if(this._pickupDateField.value)
		sd = AjxDateUtil.simpleParseDateStr(this._pickupDateField.value);
	var ed; 
	if(this._dropoffDateField.value)
		ed = AjxDateUtil.simpleParseDateStr(this._dropoffDateField.value);
	var newDate = AjxDateUtil.simpleComputeDateStr(ev.detail);

	// change the start/end date if they mismatch
	if (parentButton == this._pickupDateButton) {
		if (ed && (ed.valueOf() < ev.detail.valueOf()))
			this._dropoffDateField.value = newDate;
		this._pickupDateField.value = newDate;
	} else {
		if (sd && (sd.valueOf() > ev.detail.valueOf()))
			this._pickupDateField.value = newDate;
		this._dropoffDateField.value = newDate;
	}
};

TravelAgentCarFindView.prototype._searchButtonListener = 
function (ev) {
	var props = [ "toolbar=no,location=no,status=yes,menubar=yes,scrollbars=yes,resizable=yes" ];
	props = props.join(",");

	var browserUrl = ["http://myplanner.org/travel_car.php?","vehicleType=",this._vehicleTypeSelect.getValue(),
		"&pickupAirport=",this._pickupAirportField.getValue(),"&dropoffAirport=",this._dropoffAirportField.getValue(),
		"&pickupDate=",this._pickupDateField.value,"&pickupTime=",this._pickupTimeSelect.getValue(),
		"&dropoffDate=",this._dropoffDateField.value,"&dropoffTime=",this._dropoffTimeSelect.getValue()].join("");

	
	var canvas = window.open(browserUrl, "SideStep.com finds", props);
	
};

TravelAgentCarFindView.prototype._searchButtonListener2 = 
function (ev) {
	var props = [ "toolbar=no,location=no,status=yes,menubar=yes,scrollbars=yes,resizable=yes" ];
	props = props.join(",");

	var browserUrl = ["http://myplanner.org/travelocity_car.php?","vehicleType=",this._vehicleTypeSelect.getValue(),
		"&pickupAirport=",this._pickupAirportField.getValue(),"&dropoffAirport=",this._dropoffAirportField.getValue(),
		"&pickupDate=",this._pickupDateField.value,"&pickupTime=",this._pickupTimeSelect.getValue(),
		"&dropoffDate=",this._dropoffDateField.value,"&dropoffTime=",this._dropoffTimeSelect.getValue()].join("");

	
	var canvas = window.open(browserUrl, "Travelocity.com finds", props);
	
};

TravelAgentCarFindView.prototype._searchButtonListener3 = 
function (ev) {
	var props = [ "toolbar=no,location=no,status=yes,menubar=yes,scrollbars=yes,resizable=yes" ];
	props = props.join(",");

	var browserUrl = ["http://myplanner.org/hotwire_car.php?","vehicleType=",this._vehicleTypeSelect.getValue(),
		"&pickupAirport=",this._pickupAirportField.getValue(),"&dropoffAirport=",this._dropoffAirportField.getValue(),
		"&pickupDate=",this._pickupDateField.value,"&pickupTime=",this._pickupTimeSelect.getValue(),
		"&dropoffDate=",this._dropoffDateField.value,"&dropoffTime=",this._dropoffTimeSelect.getValue()].join("");

	
	var canvas = window.open(browserUrl, "Hotwire.com finds", props);
	
};

TravelAgentCarFindView.prototype._selectChangeListener = 
function(ev) {
	var selectObj = ev._args.selectObj;
	var newValue = ev._args.newValue;
	
	if(selectObj == this._pickupAirportsSelectWork || selectObj == this._pickupAirportsSelectHome) {
		if(this._pickupAirportField)
			this.setPickupAirport(newValue);	
	//		this._pickupAirportField.setValue(newValue);
	} else if(selectObj == this._dropoffAirportsSelectWork || selectObj == this._dropoffAirportsSelectHome) {
		if(this._dropoffAirportField)
			this.setDropoffAirport(newValue);
//			this._dropoffAirportField.setValue(newValue);
	}
};

TravelAgentCarFindView.prototype._mouseOverListener = 
function(ev) {
	if(ev.target && ev.target.id) {
		if(ev.target.id==this._pick_coa_id) {
			ev.target.className = "SideStepLinkButton-activated";
		}
	}
}

TravelAgentCarFindView.prototype._mouseOutListener = 
function(ev) {
	if(ev.target && ev.target.id) {
		if(ev.target.id==this._pick_coa_id) {
			ev.target.className = "SideStepLinkButton";
		}
	}
}

TravelAgentCarFindView.prototype._mouseDownListener = 
function(ev) {
	if(ev.target && ev.target.id) {
		if(ev.target.id==this._pick_coa_id) {
			/*alert("Clicked");*/
			this._airportLookupDlg = new TravelAgentAirportLookupDlg(appCtxt.getShell(), this,this.zimlet);			
			this._airportLookupDlg.popup();
			this._airportLookupDlg.setButtonListener(DwtDialog.OK_BUTTON, new AjxListener(this, this._setPickupAirportCode));
		} else if (ev.target.id==this._drop_coa_id) {
			this._airportLookupDlg = new TravelAgentAirportLookupDlg(appCtxt.getShell(), this,this.zimlet);			
			this._airportLookupDlg.popup();
			this._airportLookupDlg.setButtonListener(DwtDialog.OK_BUTTON, new AjxListener(this, this._setDropoffAirportCode));
		
		}
	}
}

TravelAgentCarFindView.prototype._setPickupAirportCode = 
function (ev) {
	if(this._airportLookupDlg) {
		var code = this._airportLookupDlg.getSelectedAirport();
		this.setPickupAirport(code);
		this._airportLookupDlg.popdown();		
	}
}

TravelAgentCarFindView.prototype._setDropoffAirportCode = 
function (ev) {
	if(this._airportLookupDlg) {
		var code = this._airportLookupDlg.getSelectedAirport();
		this.setDropoffAirport(code);
		this._airportLookupDlg.popdown();
	}
}