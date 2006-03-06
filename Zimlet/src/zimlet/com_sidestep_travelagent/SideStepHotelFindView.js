function SideStepHotelFindView(parent, appCtxt,zimlet,addr) {
	DwtTabViewPage.call(this,parent);
	this.zimlet = zimlet;
	this._appCtxt = appCtxt;
	this._airportsSelectHome = null;
	this._airportsSelectWork = null;
	this._addr = addr;
	this._createHTML(this.hasWorkAddr, this.hasHomeAddr);
	this.setScrollStyle(Dwt.SCROLL);
	this._rendered=false;
}

SideStepHotelFindView.prototype = new DwtTabViewPage;
SideStepHotelFindView.prototype.constructor = SideStepHotelFindView;


// Public methods

SideStepHotelFindView.prototype.toString = 
function() {
	return "SideStepHotelFindView";
};

SideStepHotelFindView.prototype.showMe = 
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

SideStepHotelFindView.prototype.setAddress =
function(addr) {
	this._addr = addr;
	if(this._checkinAddrField)
		this._checkinAddrField.setValue(addr);
}

SideStepHotelFindView.prototype.setCheckoutDate =
function (checkoutDate) {
	this._checkoutDate = checkoutDate;
	if(this._checkoutDateField)
		this._checkoutDateField.value=AjxDateUtil.simpleComputeDateStr(checkoutDate);
}

SideStepHotelFindView.prototype.setCheckinDate =
function (checkinDate) {
	this._checkinDate = checkinDate;
	if(this._checkinDateField)
		this._checkinDateField.value=AjxDateUtil.simpleComputeDateStr(checkinDate);
}



SideStepHotelFindView.prototype.resize =
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

SideStepHotelFindView.prototype._initialize = 
function() {

	this._createDwtObjects();
	this._cacheFields();
/*	this._createDwtObjects();
	this._addEventHandlers();
*/
	this._rendered = true;
};

SideStepHotelFindView.prototype._createHTML = 
function() {
	var html = new Array();
	var i = 0;
	this._checkinAddrCellId	= Dwt.getNextId();
	this._checkinDateFieldId	= Dwt.getNextId();
	this._checkinDateMiniCalBtnId	= Dwt.getNextId();
	this._checkinTimeSelectId	= Dwt.getNextId();

	this._checkoutDateFieldId = Dwt.getNextId();
	this._checkoutDateMiniCalBtnId = Dwt.getNextId();
	this._checkoutTimeSelectId = Dwt.getNextId();

	this._adultsSelectId = Dwt.getNextId();
	this._roomsSelectId = Dwt.getNextId();
	this._searchButtonId = Dwt.getNextId();


	html[i++] = "<table border=0 width=450 cellspacing=3>";		
	html[i++] = "<tr>";

	//address cell	
	html[i++] = "<td width=100% colSpan=2>"
	html[i++] = "<table border=0 cellspacing=1 width=100%>";		
	html[i++] = "<tr><td width=100%><div style='float:left;'>\"City, State, Country\" or \"City, Country\"</div></td>";
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
	html[i++] = "<tr><td>Youth (ages 12-17)</td></tr>";
	html[i++] = "<tr><td id='";
	html[i++] = this._roomsSelectId;
	html[i++] = "'></td></tr></table>";
	html[i++] = "</td>";
	html[i++] = "</tr>";	

	//searh button cell
	html[i++] = "<tr>";
	html[i++] = "<td align='center' colspan=2 id='";
	html[i++] = this._searchButtonId
	html[i++] = "'>";
	html[i++] = "</tr>";	

	html[i++] = "</table>";
	this.getHtmlElement().innerHTML = html.join("");
};

SideStepHotelFindView.prototype._createDwtObjects = 
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
		
	this._checkinDateButton = ZmApptViewHelper.createMiniCalButton(this, this._checkinDateMiniCalBtnId, dateButtonListener, dateCalSelectionListener, true);
									
	this._checkoutDateButton = ZmApptViewHelper.createMiniCalButton(this, this._checkoutDateMiniCalBtnId, dateButtonListener, dateCalSelectionListener, true);	
	
	var searchButton = new DwtButton(this);	
	searchButton.setText("Search multiple travel sites");
	searchButton.setSize("170");
	searchButton.addSelectionListener(new AjxListener(this, this._searchButtonListener));				
	var searchButtonCell = document.getElementById(this._searchButtonId);
	if (searchButtonCell)
		searchButtonCell.appendChild(searchButton.getHtmlElement());

	this._adultSelect = new DwtSelect(this,[new DwtSelectOption("1", true, "1"), 
	new DwtSelectOption("2", false, "2"),
	new DwtSelectOption("3", false, "3"),
	new DwtSelectOption("4", false, "4")]);
	var adultCell = document.getElementById(this._adultsSelectId);
	if (adultCell)
		adultCell.appendChild(this._adultSelect.getHtmlElement());	
		
	this._roomsSelect = new DwtSelect(this,[new DwtSelectOption("0", true, "0"),
	new DwtSelectOption("1", true, "1"), 
	new DwtSelectOption("2", false, "2"),
	new DwtSelectOption("3", false, "3"),
	new DwtSelectOption("4", false, "4")]);
	var roomsCell = document.getElementById(this._roomsSelectId);
	if (roomsCell)
		roomsCell.appendChild(this._roomsSelect.getHtmlElement());		
};

SideStepHotelFindView.prototype._cacheFields = 
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


SideStepHotelFindView.prototype._dateButtonListener = function(ev) {
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

SideStepHotelFindView.prototype._dateCalSelectionListener = function(ev) {
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

SideStepHotelFindView.prototype._searchButtonListener = 
function (ev) {
	var props = [ "toolbar=no,location=no,status=yes,menubar=yes,scrollbars=yes,resizable=yes" ];
	props = props.join(",");

	var browserUrl = ["http://myplanner.org/travel_hotel.php?","tripType=city",
	"&checkinDate=",this._checkinDateField.value,"&checkoutDate=",this._checkoutDateField.value,
	"&numberOfAdults=",this._adultSelect.getValue(),"&numberOfRooms=",this._roomsSelect.getValue(),
	"&city=",this._checkinAddrField.getValue()].join("");


	var canvas = window.open(browserUrl, "Travel finds", props);

};
