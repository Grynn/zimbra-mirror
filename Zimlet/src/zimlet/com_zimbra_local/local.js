/*
 * ***** BEGIN LICENSE BLOCK *****
 *
 * Zimbra Collaboration Suite Zimlets
 * Copyright (C) 2005, 2006, 2007 Zimbra, Inc.
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

Com_Zimbra_Local = function() {
};

Com_Zimbra_Local.prototype = new ZmZimletBase;
Com_Zimbra_Local.prototype.constructor = Com_Zimbra_Local;

Com_Zimbra_Local.prototype.toString =
function() {
	return "Com_Zimbra_Local";
};

Com_Zimbra_Local.prototype.init =
function() {
	this._controller = new YahooLocalController(this);
	//Add "Search Local" to the Search toolbar.
	//this.addLocalSearchToolBar((new AjxListener(this,this._localSearchListener))); //Commented as it was confusing existing users.
};

/*
// Add "Search Local" button the existing
Com_Zimbra_Local.prototype.addLocalSearchToolBar =
function(listener) {
	var searchToolBar = this._searchToolBar = appCtxt.getSearchController().getSearchToolbar();
	//Add Custom Button to the Search Toolbar
	var searchMenuBtnTd = document.getElementById(searchToolBar._htmlElId+"_searchMenuButton");
	var td = searchMenuBtnTd.parentNode.insertCell(searchMenuBtnTd.cellIndex+1);
	td.id = searchToolBar._htmlElId + "_searchLocal";
	td.className  =  'ZmSearchToolbarCell';
	var localSearchButton = searchToolBar._addButton({ buttonId:"_searchLocal", lbl:"Local Search", icon:"YLogo", tooltip:ZmMsg.searchTooltip});
	localSearchButton.addSelectionListener(listener);
};
*/

Com_Zimbra_Local.prototype._localSearchListener =
function(ev){
	var query = AjxStringUtil.trim(this._searchToolBar.getSearchFieldValue());
	if (query && query.length) {
		this._controller.searchLocal(query);
	}
};

Com_Zimbra_Local.prototype.menuItemSelected =
function(itemId) {
	switch (itemId) {
		case "MY_LOCATION":			this._controller.markMe();				break;
		case "SEARCH":				this._controller.searchQuery();			break;
		case "SEARCH_ADDR":			this._controller.searchAddress();		break;
		case "TRAFFIC":				this._controller.searchTraffic();		break;
		case "UPCOMING":			this._controller.searchUpcoming();		break;
		case "MANUAL_LOCAION":		this._controller.changeLocation();		break;
		case "MANULA_LOCATION_ZIP":	this._controller.changeLocationByZip();	break;
		case "PREFERENCES":			this.createPropertyEditor();			break;
		default:					this.createPropertyEditor();			break;
	}
};

Com_Zimbra_Local.prototype.singleClicked =
function() {
	this._controller.searchQuery();
};


/**
 * YahooLocalController
 * @param zimlet
 */
YahooLocalController = function(zimlet) {

	if (arguments.length == 0) { return; }

	ZmController.call(this, appCtxt.getShell());

	ZmOperation.registerOp("TRAFFIC", {textKey:"traffic", tooltipKey:"trafficTooltip", image:"YLogo"});
	ZmOperation.registerOp("UPCOMING",{textKey:"upcoming", tooltipKey:"upcomingTooltip", image:"ULogo"});
	this._listeners = {};
	this._listeners[ZmOperation.TRAFFIC] = new AjxListener(this, this._trafficListener);
	this._listeners[ZmOperation.CANCEL] = new AjxListener(this, this._cancelListener);
	this._listeners[ZmOperation.SEND] = new AjxListener(this, this._sendListener);
	this._listeners[ZmOperation.UPCOMING] = new AjxListener(this, this._upcomingListener);
	this._listeners[ZmOperation.SEARCH] = new AjxListener(this, this.searchAddress);

	this._zimlet = zimlet;

	this._searchOkListener = new AjxListener(this, this._handleSearchListener);
	this._searchAddrOkListener = new AjxListener(this, this._handleSearchAddrListener);
	this._changeLocationOkListener = new AjxListener(this, this._handleChangeLocationListener);

//	this.setLocation("37.3878","-122.0195"); // set default location to Sunnyvale
};

YahooLocalController.prototype = new ZmController;
YahooLocalController.prototype.constructor = YahooLocalController;

/**
 * Find Lon/Lat and other details for the IPAddress. Thanks to maxmind.com
 */
YahooLocalController.prototype.getLocal =
function() {

	if (!this._ylocal) {
		this._countryCode = geoip_country_code();
		this._countryName = geoip_country_name();
		this._city = geoip_city();
		this._region = geoip_region();
		this._latitude = geoip_latitude();
		this._longitude = geoip_longitude();

		this._ylocal = {
			countryCode: this._countryCode,
			countryName: this._countryName,
			city: this._city,
			region: this._region,
			latitude: this._latitude,
			longitude: this._longitude
		};

		this._ylocalTmp = {
			countryCode: this._countryCode,
			countryName: this._countryName,
			city: this._city,
			region: this._region,
			latitude: this._latitude,
			longitude: this._longitude
		}
	}

	var manLoc = this._zimlet.getUserProperty("manuallocation");
	if (manLoc && manLoc.match(/true/i)) {
		this._ylocalTmp.latitude = this._zimlet.getUserProperty("latitude");
		this._ylocalTmp.longitude = this._zimlet.getUserProperty("longitude");
		return this._ylocalTmp;
	}
	return this._ylocal;
};

YahooLocalController.prototype.setLocation =
function(lat, lon, callback) {
    this._zimlet.setUserProperty("manuallocation", "true");
    this._zimlet.setUserProperty("latitude", lat);
    this._zimlet.setUserProperty("longitude", lon);
    this._zimlet.saveUserProperties(callback);
};

YahooLocalController.prototype.searchLocal =
function(query) {
	var cord = this._setDefaultView();

	this.getMapsView().searchLocal({
		query: query,
		defaultLat: cord.latitude,
		defaultLon: cord.longitude
	});
};

YahooLocalController.prototype.searchQuery =
function() {
	var title = this._zimlet.getMessage("searchYahooLocal");
	var inputLabel = this._zimlet.getMessage("searchFor");

	this._showInputDialog(title, inputLabel, this._searchOkListener);
};

YahooLocalController.prototype.searchAddress =
function(ev) {
	var title = this._zimlet.getMessage("enterAddress");
	var inputLabel = this._zimlet.getMessage("address");

	this._showInputDialog(title, inputLabel, this._searchAddrOkListener);
};

YahooLocalController.prototype.changeLocationByZip =
function(zip) {
	var title = this._zimlet.getMessage("changeLocation");
	var inputLabel = this._zimlet.getMessage("zipCode");

	this._showInputDialog(title, inputLabel, this._changeLocationOkListener);
};

YahooLocalController.prototype.searchUpcoming =
function() {
	var cord = this._setDefaultView();

	this.getMapsView().searchUpcoming({
		latitude : cord.latitude,
		longitude: cord.longitude
	});
};

YahooLocalController.prototype.searchTraffic =
function() {
	var cord = this._setDefaultView();

	this.getMapsView().searchTraffic({
		latitude:   cord.latitude,
		longitude:  cord.longitude
	});
};

YahooLocalController.prototype.markMe =
function() {
	var cord = this._setDefaultView();
	this.getMapsView().markMe(cord.latitude, cord.longitude);
};

YahooLocalController.prototype.displayAddress =
function(addr) {
	this.setView({
		clean: true,
		typeControl:true,
		panControl:false,
		zoomControl:"long",
		zoomLevel: 6,
		defaultLocation: addr
	});

	this.getMapsView().markAddr({defaultLocation: addr});
};

YahooLocalController.prototype.changeLocation =
function() {
	var cord = this._setDefaultView();

	this.getMapsView().changeLocation({
		latitude: cord.latitude,
		longitude: cord.longitude
	});
};

// View
ZmController.YMAPS_VIEW = "YAHOOMAPS";

YahooLocalController.prototype.getMapsView =
function() {
	if (!this._mapsView) {
		this._mapsView = new YahooMaps(appCtxt.getShell(), null, this);
	}
	return this._mapsView;
};

YahooLocalController.prototype.setView =
function(params) {
	this._initializeToolBar();
	this._toolbar.enableAll(true);
	this._createMapView(params); // YahooMapsView
	this.showView(params);
};

YahooLocalController.prototype.showView =
function(params) {
	this._mapsView.prepareMap(params);
	appCtxt.getAppViewMgr().pushView(ZmController.YMAPS_VIEW);
	// fit to container, since the height and width needs to be set for this view
	appCtxt.getAppViewMgr()._fitToContainer([ZmAppViewMgr.C_APP_CONTENT]);
};

YahooLocalController.prototype.hideView =
function() {
	appCtxt.getAppViewMgr().popView(true, ZmController.YMAPS_VIEW);
};

YahooLocalController.prototype._createView =
function() {
	var elements = {};
	elements[ZmAppViewMgr.C_TOOLBAR_TOP] = this._toolbar;
	elements[ZmAppViewMgr.C_APP_CONTENT] = this._mapsView;
	appCtxt.getAppViewMgr().createView(ZmController.YMAPS_VIEW, null, elements);
};

YahooLocalController.prototype._createMapView =
function(params) {
	if (this._mapsView) { return; }

	// Creating Map View
	this.getMapsView();
	this._createView();
};

YahooLocalController.prototype._initializeToolBar =
function() {
	if (this._toolbar) { return; }

	var buttons = [
		ZmOperation.SEND,
		ZmOperation.UPCOMING,
		ZmOperation.TRAFFIC,
		ZmOperation.SEARCH,
		ZmOperation.CANCEL
	];
	this._toolbar = new ZmButtonToolBar({parent:appCtxt.getShell(), buttons:buttons, className:"ZmAppToolBar ImgSkin_Toolbar"});

	// add listeners to the operations
	for (var i = 0; i < this._toolbar.opList.length; i++) {
		var button = this._toolbar.opList[i];
		if (this._listeners[button]) {
			this._toolbar.addSelectionListener(button, this._listeners[button]);
		}
	}
};

YahooLocalController.prototype._showInputDialog =
function(title, inputLabel, okListener) {
	if (!this._inputDialog) {
		this._inputDialog = new ZmDialog({parent:appCtxt.getShell()});

		// create content
		var html = [];
		var i = 0;
		html[i++] = "<table cellpadding=2 cellspacing=2><tr><td class='ZmFieldLabelRight' id='";
		html[i++] = this._inputDialog._htmlElId;
		html[i++] = "_label'></td><td><input type='text' size=20 maxlength=255 id='";
		html[i++] = this._inputDialog._htmlElId;
		html[i++] = "_input'></td></tr></table>";

		this._inputDialog.setContent(html.join(""));

		// cache the input element and label for easy access
		this._inputEl = document.getElementById(this._inputDialog._htmlElId + "_input");
		this._inputLabel = document.getElementById(this._inputDialog._htmlElId + "_label");
	}

	this._inputDialog.setTitle(title);
	this._inputDialog.setButtonListener(DwtDialog.OK_BUTTON, okListener);
	this._inputDialog.setEnterListener(okListener);

	this._inputEl.value = "";
	this._inputLabel.innerHTML = inputLabel;

	this._inputDialog.popup();

	this._inputEl.focus();
};

YahooLocalController.prototype._setDefaultView =
function() {
	var cord = this.getLocal();
	this.setView({
		clean: true,
		typeControl:true,
		panControl:false,
		zoomControl:"long",
		zoomLevel: 6,
		defaultLat: cord.latitude,
		defaultLon: cord.longitude
	});

	return cord;
};


// Listeners

YahooLocalController.prototype._handleSearchListener =
function() {
	var query = AjxStringUtil.trim(this._inputEl.value);
	if (query.length) {
		this._inputDialog.popdown();
		this.searchLocal(query);
	}
};

YahooLocalController.prototype._handleSearchAddrListener =
function() {
	var query = AjxStringUtil.trim(this._inputEl.value);
	if (query.length) {
		this._inputDialog.popdown();
		this.displayAddress(query);
	}
};

YahooLocalController.prototype._handleChangeLocationListener =
function() {
	var query = AjxStringUtil.trim(this._inputEl.value);
	if (query.length) {
		this._inputDialog.popdown();
		this._getLatLonForZip(query);
	}
};


YahooLocalController.prototype._getLatLonForZip =
function(zip) {
	var url = "http://www.csgnetwork.com/cgi-bin/zipcodes.cgi?Zipcode=";
	var callback = new AjxCallback(this, this._handleLatLonForZip, zip);
    var serverURL = ZmZimletBase.PROXY + AjxStringUtil.urlComponentEncode(url+zip);

	AjxRpc.invoke(null, serverURL, null, callback, true);
};

YahooLocalController.prototype._handleLatLonForZip =
function(zip, result) {
	if (result.text.match(/Zipcode not found!/i)) {
		appCtxt.setStatusMsg(this._zimlet.getMessage("zipCodeInvalid"), ZmStatusView.LEVEL_CRITICAL);
		return;
	}

	var lat = AjxStringUtil.trim((result.text.match(/<td><b>Latitude<\/b><\/td><td>.*(\-?[.\w]+)<\/td>/ig))[0].replace(/<\/?[^>]+>|Latitude/gi, ''));
	var lon = AjxStringUtil.trim((result.text.match(/<td><b>Longitude<\/b><\/td><td>.*(\-?[.\w]+)<\/td>/ig))[0].replace(/<\/?[^>]+>|Longitude/gi, ''));
	if (!(lat && lon)) {
		appCtxt.setStatusMsg(this._zimlet.getMessage("coordsNotFound"), ZmStatusView.LEVEL_CRITICAL);
		return;
	}

	var cord = this.getLocal();
	this.setView({
		clean: true,
		typeControl:true,
		panControl:false,
		zoomControl:"long",
		zoomLevel: 3,
		defaultLat: lat,
		defaultLon: lon
	});

	this.getMapsView().changeLocation({
		latitude:   cord.latitude,
		longitude:  cord.longitude,
		newLatitude: lat,
		newLongitude: lon
	});
};

YahooLocalController.prototype._sendListener =
function(ev) {

	var mapObject = this._mapsView.getState();

	var url = [
		"http://maps.yahoo.com/#tt=", mapObject.query,
		"&lon=", mapObject.lon,
		"&lat=", mapObject.lat,
		"&mag=", mapObject.zoom,
		"&mvt=m&tp=1"
	].join("");

	var body = "Hi,\n Your friend has shared you a Yahoo Map regarding \""+mapObject.query+"\". \n\nPlease access it @ \t\n\n";
	var footer = "\n\nThis email was sent to you by a user on Yahoo Maps (maps.yahoo.com)."
	var subject = appCtxt.get(ZmSetting.USERNAME) + " sent this Yahoo Maps.";

	if (appCtxt.get(ZmSetting.HTML_COMPOSE_ENABLED) &&
		appCtxt.get(ZmSetting.COMPOSE_AS_FORMAT) == ZmSetting.COMPOSE_HTML)
	{
		body = AjxStringUtil.nl2br(body);
		footer = AjxStringUtil.nl2br(footer);
	}

	var params = {
		action: ZmOperation.NEW_MESSAGE,
		subjOverride: subject,
		extraBodyText: (body + url + footer)
	};

	var cc = AjxDispatcher.run("GetComposeController");
	cc.doAction(params);
};

YahooLocalController.prototype._upcomingListener =
function(ev) {
	this.searchUpcoming();
};

YahooLocalController.prototype._trafficListener =
function(ev) {
	this.searchTraffic();
};

YahooLocalController.prototype._cancelListener =
function(ev) {
	this.hideView();
};
