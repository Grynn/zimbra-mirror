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

function Com_Zimbra_TravelAgent() {
	/*if(!ZmOperation.MSG_KEY[201]) {
		ZmOperation.MSG_KEY[201] = "travelAgent";
		var op = new Object();
		op.id = 201;
		op.label = "Travel agent";
		op.image = "TravelAgent-panelIcon";
		op.disImage = "TravelAgent-panelIcon";
		op.toolTip = "Book Flight, Car or Hotel on the selected day";
		ZmZimlet.actionMenus["ZmCalViewController"].push(op);
		ZmZimlet.listeners["ZmCalViewController"][201] = Com_Zimbra_TravelAgent.launchMe;
	}*/
	Com_Zimbra_TravelAgent._instance = this;
}

Com_Zimbra_TravelAgent.getInstance = function () {
	return Com_Zimbra_TravelAgent._instance;
}
Com_Zimbra_TravelAgent.prototype = new ZmZimletBase;
Com_Zimbra_TravelAgent.prototype.constructor = Com_Zimbra_TravelAgent;
//Map of airline codes 

Com_Zimbra_TravelAgent.ZIPCODE_CACHE = [];

Com_Zimbra_TravelAgent.prototype.singleClicked =
function () {
	this.showSideStepDlg();
}

Com_Zimbra_TravelAgent.prototype.menuItemSelected = function(itemId) {
	switch (itemId) {
	    case "PREFERENCES":
			this.createPropertyEditor();
		break;
		case "BOOKAFLIGHT":
			this.showSideStepDlg();
			this.tabView.switchToTab(this.tabkeys[0]);
		break;
		case "BOOKACAR":
			this.showSideStepDlg();
			this.tabView.switchToTab(this.tabkeys[1]);
		break;
		case "BOOKAHOTEL":
			this.showSideStepDlg();
			this.tabView.switchToTab(this.tabkeys[2]);
		break;		
   }
};

Com_Zimbra_TravelAgent.prototype.showSideStepDlg = 
function (homeOptions, workOptions, workZip, homeZip,addr) {
	var view = new DwtComposite(appCtxt.getShell());	
	this.tabView = new DwtTabView(view,"SideStepTabView");
	this.flightPage = new TravelAgentFlightFindView(this.tabView, this,homeOptions, workOptions, workZip, homeZip);
	this.carPage = new TravelAgentCarFindView(this.tabView, this,homeOptions, workOptions, workZip, homeZip);	
	this.hotelPage = new TravelAgentHotelFindView(this.tabView, this,addr);		
	view.setSize("550px", "400px");
	this.tabView.setSize("550px", "400px");	
	this.flightPage.setSize("550px", "400px");	
	this.carPage.setSize("550px", "400px");	
	this.hotelPage.setSize("550px", "400px");	
	this.tabkeys = [];
	this.tabkeys.push(this.tabView.addTab("Flight", this.flightPage));
	this.tabkeys.push(this.tabView.addTab("Car", this.carPage));	
	this.tabkeys.push(this.tabView.addTab("Hotel", this.hotelPage));		
	var canvas = new TravelDialog(appCtxt.getShell(),  "Search travel reservations across multiple engines",view);
//	canvas.setButtonListener(DwtDialog.OK_BUTTON, new AjxListener(canvas, canvas.popdown));
	canvas.popup();
		
	this.tabView.getTabButton(this.tabkeys[0]).setImage("SideStep-air");
	this.tabView.getTabButton(this.tabkeys[1]).setImage("SideStep-car");	
	this.tabView.getTabButton(this.tabkeys[2]).setImage("SideStep-hotel");	
}

Com_Zimbra_TravelAgent.prototype.findAirports = 
function (zipcode) {
	if(Com_Zimbra_TravelAgent.ZIPCODE_CACHE[zipcode]) {
		return Com_Zimbra_TravelAgent.ZIPCODE_CACHE[zipcode];
	}
	//zipcode = AjxStringUtil.urlEncode(zipcode).replace(/\+/g, "%20");
	
	var url = [ZmZimletBase.PROXY,AjxStringUtil.urlComponentEncode("http://www.airnav.com/cgi-bin/airport-search?fieldtypes=a&length=5000&paved=Y&mindistance=0&maxdistance=40&distanceunits=mi&use=u&place="), AjxStringUtil.urlComponentEncode(zipcode)].join("");
	var options = new Array();
	try {
		var result=AjxRpc.invoke(null, url, null, null, true);
		if(result.success) {
//			var myReg = /(<TR><TH width=20><A href=\"\/airport\/)([A-Z]{3})(\"><IMG src=\"http:\/\/[0-9.]+\/airfield\-icons\/a.gif\" width=20 height=20 alt=\"a \"><\/A><\/TH><TD align=center>[A-Z]{3}&nbsp;&nbsp;<\/TD> <TD align=left>)([A-Za-z0-9\s,\-]+)(&nbsp;&nbsp;<\/TD> <TD align=left>)([A-Za-z0-9\s,\-]+)(&nbsp;&nbsp;<\/TD> <TD align=right nowrap>)([0-9a-z\s,]+)([A-Z\s]+)(<BR><\/TD><\/TR>)/g;	
			var myReg = /(<TD align=center>)([A-Z]{3})(&nbsp;&nbsp;<\/TD> )(<TD align=left>)([A-Z\s,\/\-]+)(&nbsp;&nbsp;<\/TD>)/g;
			//var matches = result.text.match(RegExp);
			var matches;
			try {
				while ((matches = myReg.exec(result.text)) != null) {
					var airportName = matches[5];
					var airportCode = matches[2];
					var option = new DwtSelectOption(airportCode, false, airportName + " ("+airportCode+")");
					options.push(option);
				}
			} catch (ex) {
				//
			}			
		}
	} catch (ex){
			//
	}
	// Cache Zip Lookup
	Com_Zimbra_TravelAgent.ZIPCODE_CACHE[zipcode] = options;
	return options;	
}


Com_Zimbra_TravelAgent.launchMe =
function () {
	var d = this._minicalMenu ? this._minicalMenu.__detail : null;
	if (d != null) 
		delete this._minicalMenu.__detail;
	else 
		d = this._viewMgr ? this._viewMgr.getDate() : null;

	if (d == null) d = new Date();
	
	Com_Zimbra_TravelAgent.getInstance().showSideStepDlg();
	Com_Zimbra_TravelAgent.getInstance().flightPage.setDepartDate(d);
	Com_Zimbra_TravelAgent.getInstance().carPage.setPickupDate(d);
	Com_Zimbra_TravelAgent.getInstance().hotelPage.setCheckinDate(d);
}

Com_Zimbra_TravelAgent.prototype.myplannerCallback = 
function () {
	//nothing yet
}

Com_Zimbra_TravelAgent.prototype.doDrop = 
function(obj) {
	if (obj.TYPE == "ZmContact") {
		var workOptions = null;
		var homeOptions = null;	
		var addr = "";	
		var isWorkUS = false;
		if(!obj.workCountry || String(obj.workCountry).toUpperCase()=="US" || String(obj.workCountry).toUpperCase()=="UNITED STATES" || String(obj.workCountry).toUpperCase()=="USA") {
			isWorkUS = true;
		}
		
		var isHomeUS = false;
		if(!obj.homeCountry || String(obj.homeCountry).toUpperCase()=="US" || String(obj.homeCountry).toUpperCase()=="UNITED STATES"  || String(obj.homeCountry).toUpperCase()=="USA") {
			isHomeUS = true;
		}
		var isOtherUS = false;
		if(!obj.otherCountry || String(obj.otherCountry).toUpperCase()=="US" || String(obj.otherCountry).toUpperCase()=="UNITED STATES" || String(obj.otherCountry).toUpperCase()=="USA") {
			isOtherUS = true;
		}		
		if(obj.homePostalCode && isHomeUS) {
			homeOptions = this.findAirports(obj.homePostalCode);
		} else if (obj.otherPostalCode && isOtherUS) {
			homeOptions = this.findAirports(obj.otherPostalCode);
		}
		if(obj.workPostalCode && isWorkUS) {
			workOptions = this.findAirports(obj.workPostalCode);
		}	
	
		if(obj.workCity && obj.workState && isWorkUS) {
			addr = obj.workCity + ", " + obj.workState + ", US";
		} else if(obj.homeCity && obj.homeState && isHomeUS) {
			addr = obj.homeCity + ", " + obj.homeState + ", US";
		} else if(obj.otherCity && obj.otherState && isOtherUS) {
			addr = obj.otherCity + ", " + obj.otherState + ", US";
		} else if(obj.workCity && !isWorkUS) {
			addr = obj.workCity + ", " + obj.workCountry;
		} else if(obj.homeCity && !isHomeUS) {
			addr = obj.homeCity + ", " + obj.homeCountry;
		} else if(obj.otherCity && !isOtherUS) {
			addr = obj.otherCity + ", " + obj.otherCountry;
		} 
		
		this.showSideStepDlg(workOptions,homeOptions,obj.workPostalCode, (obj.homePostalCode ? obj.homePostalCode : obj.otherPostalCode),addr);
		

//		this.hotelPage.setAddress(addr)
	} else if(obj.TYPE == "ZmAppt") {
		this.showSideStepDlg();
		var startDate = new Date(obj.startDate.getTime()-AjxDateUtil.MSEC_PER_DAY);
		var endDate = obj.endDate;
		this.flightPage.setDepartDate(startDate);
		this.flightPage.setReturnDate(endDate);	
		this.carPage.setPickupDate(pickupDate);
		this.carPage.setDropoffDate(dropoffDate);
		this.hotelPage.setCheckinDate(startDate);
		this.hotelPage.setCheckoutDate(endDate);	
		
	}
};

function TravelDialog(parent,title,  view) {
	if (arguments.length == 0) return;
	DwtDialog.call(this, {parent:parent, title:title, standardButtons:[DwtDialog.CANCEL_BUTTON ]});
	if (!view) {
		this.setContent(this._contentHtml());
	} else {
		this.setView(view);
	}

	this._treeView = {};
	this._opc = appCtxt.getOverviewController();
};

TravelDialog.prototype = new ZmDialog;
TravelDialog.prototype.constructor = TravelDialog;
