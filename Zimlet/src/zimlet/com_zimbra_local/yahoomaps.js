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

YahooMaps = function(parent, controller) {
	if (arguments.length == 0) { return; }

	// create composite to place the YahooMap
	DwtComposite.call(this, {parent:parent, className:"YahooMap", posStyle:Dwt.ABSOLUTE_STYLE, id:"YahooMap"});

	this._controller = controller;

	this.getMap();
	this.addSearchLocalListener();
	this.addTrafficSearchListener();
};

YahooMaps.prototype = new DwtComposite;
YahooMaps.prototype.constructor = new YahooMaps;

// Constants
YahooMaps.APPID					= "ZimbraMail";
YahooMaps.VERSION				= "3.7";
YahooMaps.DEFAULT_ZOOMLEVEL		= 5;
YahooMaps.QUERY_RESULTS			= 10;
YahooMaps.DEFAULT_TRAFFICRADIUS	= 10;
YahooMaps.LOC_MARKER_IMAGE 		= 'http://us.i1.yimg.com/us.yimg.com/i/us/tr/fc/map/nightlife_bubble_w.png';


YahooMaps.prototype.prepareMap =
function(params) {
	var map = this.getMap();

	if (params.clean) {
		map.removeMarkersAll();
	}

	if (params.panControl) {
		map.addPanControl();
	} else {
		map.addPanControl();
		map.removePanControl();
	}

	if (params.zoomControl) {
		if (params.zoomControl == "long") {
			map.addZoomLong();
		} else {
			map.addZoomShort();
		}
	} else {
		map.removeZoomControl()
	}

	if (params.zoomScale) {
		map.addZoomScale();
	} else {
		map.removeZoomScale();
	}

	if (params.typeControl) {
		map.addTypeControl();
	}
	if (params.defaultLat && params.defaultLon) {
		var pt = this.getGeoPoint(params.defaultLat, params.defaultLon);
		map.drawZoomAndCenter(pt);
		this.setLocMarker(params.defaultLat, params.defaultLon, "You are here.", YahooMaps.LOC_MARKER_IMAGE);
	} else if (params.defaultLocation) {
		map.drawZoomAndCenter(params.defaultLocation);
	}
	map.setZoomLevel(params.zoomLevel || YahooMaps.DEFAULT_ZOOMLEVEL);
};

//Local Search
YahooMaps.prototype.searchLocal =
function(params) {
	if (!params.query) { return; }

	var map = this.getMap();
	var geoPt = (params.defaultLat && params.defaultLon)
		? this.getGeoPoint(params.defaultLat, params.defaultLon)
		: map.getCenterLatLon();

	params.radius = params.radius || YahooMaps.DEFAULT_RADIUS;
	params.nresults = params.nresults || YahooMaps.QUERY_RESULTS;
	map.searchLocal(geoPt, params.query, params.radius, params.nresults);

	this._searchQuery = params.query;
	this._searchLat = params.defaultLat;
	this._searchLon = params.defaultLon;
};

YahooMaps.prototype.addSearchLocalListener =
function() {
	var self = this;
	function processLocalSearchResponse(ev) {
		if (!ev.Data) { return; }
		var map = self.getMap();
		for (var a in ev.Data.ITEMS) {
			var result = ev.Data.ITEMS[a];
			if (result.TITLE) {
				var addrCard = self.constructLocalResult(result);
				var m = self.getMarker(self.getGeoPoint(result.LATITUDE, result.LONGITUDE), addrCard);
				map.addOverlay(m);
			}
		}
	};

	YEvent.Capture(this.getMap(), EventsList.onEndLocalSearch, processLocalSearchResponse);
};

YahooMaps.prototype.constructLocalResult =
function(result) {
	var html = [];
	var idx = 0;

	html[idx++] = "<div class='YMapAddress'>";
	html[idx++] = "<div style='font-weight:bold;'><a target='_blank' href='";
	html[idx++] = result.BUSINESSURL;
	html[idx++] = "'>";
	html[idx++] = result.TITLE;
	html[idx++] = "</a></div>";
	html[idx++] = "<div style='font-style:italic;'>";
	html[idx++] = result.ADDRESS;
	html[idx++] = ",";
	html[idx++] = result.CITY;
	html[idx++] = ",";
	html[idx++] = result.STATE;
	html[idx++] = "</div>";
	if (result.PHONE) {
		html[idx++] = "<div><img width='16' hight='16' src='";
		html[idx++] = this.getResource('phone.gif');
		html[idx++] = "'>";
		html[idx++] = result.PHONE;
		html[idx++] = "</div>";
	}
	html[idx++] = "<div>";
	html[idx++] = result.DISTANCE;
	html[idx++] = " miles away!</div>";
	var rating = result.RATING.AVERAGERATING;
	if (!isNaN(rating)) {
		html[idx++] = "<div>";
		var count = 1;
		while (count <= rating) {
			html[idx++] = "<img width='16' height='16' src='";
			html[idx++] = this.getResource('star_on.gif');
			html[idx++] = "'>";
			count++;
		}
		while (count <= 5) {
			html[idx++] = "<img width='16' height='16' src='";
			html[idx++] = this.getResource('star_off.gif');
			html[idx++] = "'>";
			count++;
		}
		html[idx++] = "</div>";
	}
	var title   = result.TITLE.replace("'","");
	var addr    = result.ADDRESS.replace("'","");
	html[idx++] = "<div><a href=\"#\" onclick=\"YahooMaps._addContact({";
	html[idx++] = " title:'";
	html[idx++] = title;
	html[idx++] = "', addr:'";
	html[idx++] = addr;
	html[idx++] = "', city:'";
	html[idx++] = result.CITY;
	html[idx++] = "', state:'";
	html[idx++] = result.STATE;
	html[idx++] = "', phone:'";
	html[idx++] = result.PHONE;
	html[idx++] = "', bizurl:'";
	html[idx++] = result.BUSINESSURL;
	html[idx++] = "', url:'";
	html[idx++] = result.URL;
	html[idx++] = "'});\">+Contact</a>";

	if (YahooMaps._getZimlet("com_zimbra_asterisk")) {
		html[idx++] = "&nbsp;|&nbsp;<a href='#' onclick=\"YahooMaps._phoneCall('";
		html[idx++] = result.PHONE;
		html[idx++] = "');\">Call</a>";
	}

	if (YahooMaps._getZimlet("com_zimbra_sms")) {
		//html[idx++] = "&nbsp;|&nbsp;<a href='#' onclick=\"YahooMaps._sendSMS('"+result.PHONE+"');\">SMS</a>";
		html[idx++] = "&nbsp;|&nbsp;<a href='#' onclick=\"YahooMaps._sendSMS({";
		html[idx++] = " title:'";
		html[idx++] = title;
		html[idx++] = "', addr:'";;
		html[idx++] = addr;
		html[idx++] = "', city:'";
		html[idx++] = result.CITY;
		html[idx++] = "', state:'";
		html[idx++] = result.STATE;
		html[idx++] = "', phone:'";
		html[idx++] = result.PHONE;
		html[idx++] = "', bizurl:'";
		html[idx++] = result.BUSINESSURL;
		html[idx++] = "', url:'";
		html[idx++] = result.URL;
		html[idx++] = "'});\">SMS</a>";
	}

	html[idx++] = "&nbsp;|&nbsp;<a href=\"#\" onclick=\"YahooMaps._sendLocalResult({";
	html[idx++] = " title:'";
	html[idx++] = title;
	html[idx++] = "', addr:'";
	html[idx++] = addr;
	html[idx++] = "', city:'";
	html[idx++] = result.CITY;
	html[idx++] = "', state:'";
	html[idx++] = result.STATE;
	html[idx++] = "', phone:'";
	html[idx++] = result.PHONE;
	html[idx++] = "', bizurl:'";
	html[idx++] = result.BUSINESSURL;
	html[idx++] = "', url:'";
	html[idx++] = result.URL;
	html[idx++] = "'});\">Email</a>";

	html[idx++] = "</div>";
	html[idx++] = "<div><a href='"+result.URL+"' target='_blank'>more >></a></div>";
	html[idx++] = "</div>";

	return html.join("");
};

YahooMaps._sendLocalResult =
function(params) {
	var subject = appCtxt.get(ZmSetting.USERNAME) + " shared an address using Yahoo! Local";
	var addrFormat = [
		params.title, "\n",
		params.addr, "\n",
		params.city, ",",
		params.state,"\n",
		"Phone:", params.phone, "\n",
		"Business URL:", params.bizurl, "\n",
		"For Reviews & more info view ", params.url, "\n"
	].join("");

	var body = addrFormat + "\n\n" + "\n\nThis email was sent to you by a user on Yahoo Local (local.yahoo.com)."

	if (appCtxt.get(ZmSetting.HTML_COMPOSE_ENABLED) &&
		appCtxt.get(ZmSetting.COMPOSE_AS_FORMAT) == ZmSetting.COMPOSE_HTML)
	{
		body = AjxStringUtil.nl2br(body);
	}

	var params = {
		action:ZmOperation.NEW_MESSAGE,
		subjOverride: subject,
		extraBodyText: body
	};

	var cc = AjxDispatcher.run("GetComposeController");
	cc.doAction(params);
};

YahooMaps._sendSMS =
function(params) {
	var smsZimlet = YahooMaps._getZimlet("com_zimbra_sms");
	var addrFormat = [
		params.title, "\n",
		params.addr, "\n",
		params.city, ",",
		params.state, "\n",
		"Phone:", params.phone, "\n",
		"Business URL:", params.bizurl, "\n",
		"For Reviews & more info view ", params.url, "\n"
	].join("");
	addrFormat = addrFormat + "\n\nYahoo! Local (local.yahoo.com)";
	smsZimlet.callHandler("singleClicked",[params.phone,addrFormat]);
};

YahooMaps._phoneCall =
function(phone) {
	var astrZimlet = YahooMaps._getZimlet("com_zimbra_asterisk");
	astrZimlet.callHandler("setupCall", [phone]);
};

YahooMaps._addContact =
function(params) {
	var loadCallback = new AjxCallback(YahooMaps._handleLoadContact, params);
	AjxDispatcher.require(["ContactsCore", "Contacts"], false, loadCallback, null, true);
};

YahooMaps._handleLoadContact =
function(params) {
	var contact = new ZmContact();
	if (params.title) {
		contact.setAttr(ZmContact.F_firstName, params.title);
		contact.setAttr(ZmContact.F_company, params.title);
	}

	if (params.addr)	contact.setAttr(ZmContact.F_workStreet, params.addr);
	if (params.city)	contact.setAttr(ZmContact.F_workCity, params.city);
	if (params.state)	contact.setAttr(ZmContact.F_workCountry, params.state);
	if (params.phone)	contact.setAttr(ZmContact.F_workPhone, params.phone);
	if (params.bizurl)	contact.setAttr(ZmContact.F_workURL, params.bizurl);

	AjxDispatcher.run("GetContactController").show(contact);
};

YahooMaps.prototype.getState =
function() {
	return {
		query: this._searchQuery,
		lat: this._searchLat,
		lon: this._searchLon,
		zoom: YahooMaps.DEFAULT_ZOOMLEVEL
	};
};

// upcoming events search
YahooMaps.prototype.searchUpcoming =
function(params) {
	params.callback = new AjxCallback(this, this._processUpcomingResponse);
	this.getUpcoming().searchEvents(params);
};

YahooMaps.prototype._processUpcomingResponse =
function(events) {
	if (!events) { return; }

	var map = this.getMap();
	for (var i = 0; i < events.length; i++) {
		var event = events[i];
		if (event.name) {
			var html = this._constructEventResult(event);
			var m = this.getMarker(this.getGeoPoint(event.latitude, event.longitude), html);
			map.addOverlay(m);
		}
	}
};

YahooMaps.prototype._constructEventResult =
function(event) {
	var html =[];
	var idx = 0;

	html[idx++] = "<div class='YMapAddress'>";
	html[idx++] = "<div style='font-weight:bold;'><a href='"+event.url+"' target='_blank'>"+event.name+"</a></div>";
	html[idx++] = "<div style='font-weight:italic;'> When:"+ event.start_date +","+ event.start_time +" </div>";
	html[idx++] = "<div style='font-weight:italic;'>Venue: "+event.venue_name+"</div>";
	html[idx++] = "<div style='font-style:italic;'>"+event.venue_address+","+event.venue_city+","+event.venue_state_name+"</div>";
	html[idx++] = "<div>"+event.distance+" miles away!</div";
	var name    = event.name.replace("'","");
	var addr    = event.venue_address.replace("'","");
	//var desc    = AjxStringUtil.nl2br(event.description.replace("'",""));
	html[idx++] = "<div><a href=\"#\" onclick=\"YahooMaps._addAppt({" +
		"name:'"+name+"'," +
		//"description:'"+desc+"',"+
		"addr:'"+addr+"'," +
		"city:'"+event.venue_city+"'," +
		"state:'"+ event.venue_state_name+"'," +
		"startdate:'"+event.start_date+"',"+
		"starttime:'"+event.start_time+"',"+
		"enddate:'"+event.end_date+"',"+
		"endtime:'"+event.end_time+"',"+
		"bizurl:'"+event.url+"'," +
		"url:'http://upcoming.yahoo.com/event/"+ event.id +"/'" +
		"});\">+Calander</a>";
		html[idx++] = "&nbsp;|&nbsp;<a href=\"#\" onclick=\"YahooMaps._sendEvent({" +
		"name:'"+name+"'," +
		//"description:'"+desc+"',"+
		"addr:'"+addr+"'," +
		"city:'"+event.venue_city+"'," +
		"state:'"+ event.venue_state_name+"'," +
		"startdate:'"+event.start_date+"',"+
		"starttime:'"+event.start_time+"',"+
		"enddate:'"+event.end_date+"',"+
		"endtime:'"+event.end_time+"',"+
		"bizurl:'"+event.url+"'," +
		"url:'http://upcoming.yahoo.com/event/"+ event.id +"/'" +
		"});\">Email</a>";
	html[idx++] = "</div>";
	html[idx++] = "<div><a target='_blank' href='http://upcoming.yahoo.com/event/"+ event.id +"/'>more >></a></div>";
	html[idx++] = "</div>";

	return html.join("");
};

YahooMaps._addAppt =
function(params){
	var appt = new ZmAppt();
	appt.setName(params.name);
	appt.setStartDate(YahooMaps._parseDate(params.startdate));
	appt.setEndDate(YahooMaps._parseDate(params.enddate));
	var directions = [
		"Direction:\n\n",
		params.addr, "\n",
		params.city, ",",
		params.state,
		"\n\nBusiness URL:",
		params.bizurl, "\n",
		"For reviews & more info visit ",
		params.url, "\n"
	].join("");
	appt.setTextNotes((params.description||"") + directions);

	var apptCC = AjxDispatcher.run("GetApptComposeController");
	apptCC.show(appt);
};

YahooMaps._parseDate =
function(dateStr) {
    if (dateStr) {
		var str = dateStr.split("-");
		return (new Date(str[0],str[1]-1,str[2]));
	}
	return null;
};

YahooMaps._sendEvent =
function(params){
	var subject = appCtxt.get(ZmSetting.USERNAME) + " shared an event using Yahoo! Local";
	var addrFormat = [
		params.name, "\n",
		params.addr, "\n",
		params.city, ",",
		params.state, "\n",
		"Business URL:", params.bizurl, "\n",
		"For Reviews & more info view ", params.url, "\n"
	].join("");
	var footer = "\n\nThis email was sent to you by a user on Upcoming (upcoming.com)."
	var bodyText = params.description + "\n\n" + addrFormat + footer;
	if (appCtxt.get(ZmSetting.HTML_COMPOSE_ENABLED) &&
		appCtxt.get(ZmSetting.COMPOSE_AS_FORMAT) == ZmSetting.COMPOSE_HTML)
	{
		bodyText = AjxStringUtil.nl2br(bodyText);
	}

	var params = {
		action:ZmOperation.NEW_MESSAGE,
		subjOverride: subject,
		extraBodyText: bodyText
	};

	var cc = AjxDispatcher.run("GetComposeController");
	cc.doAction(params);
};

// traffic search
YahooMaps.prototype.addTrafficSearchListener =
function() {
	var self = this;
	function processTrafficSearchResponse(ev) {
		if (!ev.Data) { return; }

		var map = self.getMap();
		for (a in ev.Data.ITEMS) {
			var l = ev.Data.ITEMS[a];
			if (l.TITLE) {
				var html = self._constructTrafficResult(l);
				var m = self.getMarker(self.getGeoPoint(l.LATITUDE, l.LONGITUDE), html);
				map.addOverlay(m);
			}
		}
	};

	YEvent.Capture(this.getMap(), EventsList.onEndTrafficSearch, processTrafficSearchResponse);
};

YahooMaps.prototype._constructTrafficResult =
function(result){
	var html = [];
	var idx = 0;

	html[idx++] = "<div>";
	html[idx++] = result.TITLE;
	html[idx++] = "</div>";
	html[idx++] = "<div><a href=\"#\" onclick=\"YahooMaps._sendTrafficResult({";
	html[idx++] = " title:'";
	html[idx++] = result.TITLE.replace("'", "");
	html[idx++] = "'";
	html[idx++] = "});\">Email</a>";
	html[idx++] = "</div>";

	return html.join("");
};

YahooMaps._sendTrafficResult =
function(params) {
	var subject = appCtxt.get(ZmSetting.USERNAME) + " shared a traffic info. using Yahoo! Local";
	var bodyText = params.title;

	if (appCtxt.get(ZmSetting.HTML_COMPOSE_ENABLED) &&
		appCtxt.get(ZmSetting.COMPOSE_AS_FORMAT) == ZmSetting.COMPOSE_HTML)
	{
		bodyText = AjxStringUtil.nl2br(bodyText);
	}

	var params = {
		action:ZmOperation.NEW_MESSAGE,
		subjOverride: subject,
		extraBodyText: bodyText
	};

	var cc = AjxDispatcher.run("GetComposeController");
	cc.doAction(params);
};

YahooMaps.prototype.searchTraffic =
function(params){
	var map = this.getMap();
	var geoPt = this.getGeoPoint(params.latitude, params.longitude);
	params.radius = params.radius || YahooMaps.DEFAULT_TRAFFICRADIUS;
	map.searchTraffic(geoPt, params.radius);
};

YahooMaps.prototype.changeLocation =
function(params) {
	if (params.latitude && params.longitude) {
		this.setLocMarker(params.latitude, params.longitude,"<b>You are here!</b><br>Please select your new location to make it your default location", YahooMaps.LOC_MARKER_IMAGE);
		YEvent.Capture(this.getMap(), EventsList.MouseClick, reportPosition);
	}

	var self = this;
	function reportPosition(_e,_c) {
		self.getMap().removeMarkersAll();
		var marker = self.setLocMarker(_c.Lat, _c.Lon,"<b>Click here to make this your default location.</b><br>Or, Please select a new location.",YahooMaps.LOC_MARKER_IMAGE);
		YEvent.Capture(marker, EventsList.MouseClick, updateLocation);

		function updateLocation(_e,_cord) {
			YEvent.Remove(self.getMap(), EventsList.MouseClick, reportPosition);
			self._controller.setLocation(_c.Lat,_c.Lon);
			self.markMe(_c.Lat,_c.Lon);
		};
	};

	if (params.newLatitude && params.newLongitude) {
		reportPosition(null, {Lat:params.newLatitude,Lon:params.newLongitude});
	}
};

// my location
YahooMaps.prototype.markMe =
function(lat, lon) {
	this.setLocMarker(lat, lon, "You are here!", YahooMaps.LOC_MARKER_IMAGE);
};

// mark addrsess
YahooMaps.prototype.markAddr =
function(params) {
	this.getMap().drawZoomAndCenter(params.defaultLocation);
	var latlon = this.getMap().getCenterLatLon();
	var m = this.getMarker(this.getGeoPoint(latlon.LAT, latlon.LON), params.defaultLocation);
	this.getMap().addOverlay(m);
};

// utilities
YahooMaps.prototype.getMap =
function() {
	if (!this._map) {
		try {
			this._map = new YMap(this.getHtmlElement());
		} catch(ex) {
			// check if ymaps api has been loaded. Possibly due to lack of net connection.
			alert("Unable to load map. YMap missing.");
			return;
		}
	}
	return this._map;
};

YahooMaps.prototype.getGeoPoint =
function(lat, lon) {
	return (new YGeoPoint(lat, lon));
};

YahooMaps.prototype.getMarker =
function(geoPt, info, imgSrc) {
	var m = new YMarker(geoPt);
	if (imgSrc) {
		var new_image = new YImage();
		imgSrc = ZmZimletBase.PROXY + AjxStringUtil.urlComponentEncode(imgSrc);
		new_image.src = imgSrc;
		m.changeImage(new_image);
	}
	m.addAutoExpand(info);
	return m;
};

YahooMaps.prototype.getUpcoming =
function() {
	if (!this._upcoming) {
		this._upcoming = new UpComingEvents();
	}
	return this._upcoming;
};

YahooMaps.prototype.setLocMarker =
function(lat, lon, text, imgSrc) {
	var m = this.getMarker(this.getGeoPoint(lat, lon), text, imgSrc);
	this.getMap().addOverlay(m);
	return m;
};

YahooMaps.prototype.mark =
function(params) {
	var m = this.getMarker(this.getGeoPoint(params.latitude,params.longitude), params.text || "",params.imgsrc);
	this.getMap().addOverlay(m);
	return m;
};

YahooMaps.prototype.getResource =
function(resrc) {
	return this._controller._zimlet.getResource(resrc);
};

YahooMaps._getZimlet =
function(name) {
	var zimlets = appCtxt.getZimletMgr().getZimlets();
	for (var i = 0; i < zimlets.length; i++) {
		var zimlet = zimlets[i];
		if (zimlet.name == name) {
			return zimlet;
		}
	}
	return null;
};

// load external JS API
YahooMaps._loadYAPI =
function(file) {
	var fileref=document.createElement('script');
	fileref.setAttribute("type","text/javascript");
	fileref.setAttribute("src", ( ZmZimletBase.PROXY + AjxStringUtil.urlComponentEncode(file) ));
	document.getElementsByTagName("head").item(0).appendChild(fileref);
};

YahooMaps._loadYMapsAPI =
function(file) {
	var callback = new AjxCallback(YahooMaps._postLoadYMapsAPI);
	var serverURL = ZmZimletBase.PROXY + AjxStringUtil.urlComponentEncode(file);
	AjxRpc.invoke(null,serverURL,null,callback,true);
};

YahooMaps._postLoadYMapsAPI =
function(result) {
	var js = result.text;
	js = js.replace(/<!--.*-->/,"") + "function _ywjs(inc){YahooMaps._loadYAPI(inc)};"
	try{
		AjxPackage.eval(js);
	} catch(ex) {
		alert('Failed to load Yahoo! Maps API.');
	}
};

YahooMaps._loadYAPI("http://j.maxmind.com/app/geoip.js");
YahooMaps._loadYMapsAPI("http://api.maps.yahoo.com/ajaxymap?v="+YahooMaps.VERSION+"&appid="+YahooMaps.APPID);
