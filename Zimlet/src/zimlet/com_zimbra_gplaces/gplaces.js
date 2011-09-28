/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Zimlets
 * Copyright (C) 2005, 2006, 2007, 2008, 2009, 2010 Zimbra, Inc.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.3 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 */

/**
 * @author Piyush Waradpande
 */

function Com_Zimbra_Gplaces() {
}
Com_Zimbra_Gplaces.prototype = new ZmZimletBase();
Com_Zimbra_Gplaces.prototype.constructor = Com_Zimbra_Gplaces;
Com_Zimbra_Gplaces.DEFAULT_LAT = 37;
Com_Zimbra_Gplaces.DEFAULT_LNG = -100;
Com_Zimbra_Gplaces.DEFAULT_ZOOM_LEVEL = 3;
Com_Zimbra_Gplaces.MEDIUM_ZOOM_LEVEL = 12;
Com_Zimbra_Gplaces.MEDIUM_HIGH_ZOOM_LEVEL = 14;
Com_Zimbra_Gplaces.HIGH_ZOOM_LEVEL = 16;
Com_Zimbra_Gplaces.INCREMENT_ZOOM = 2;
Com_Zimbra_Gplaces.SUGGESTIONS_TAB_WIDTH = 200;
Com_Zimbra_Gplaces.ZIMLET_BUTTON_INDEX = 6;


// Initialize the toolbar
Com_Zimbra_Gplaces.prototype.initializeToolbar = function(app, toolbar, controller, viewId) {

    this._composeController = controller;

    // storing viewId in a new variable
    this.viewId = viewId;
	var viewType = controller.getCurrentViewType();
    if (viewType == ZmId.VIEW_APPOINTMENT) {
        if (toolbar.getOp("APPT_LOCATION_TOOLBAR_BUTTON")) {
            return;
        }
        //get the index of View menu so we can display it after that.
        var buttonIndex = Com_Zimbra_Gplaces.ZIMLET_BUTTON_INDEX;

        //create params obj with button details
        var buttonArgs = {
            text    : this.getMessage("buttonText"),
            tooltip: this.getMessage("tooltip"),
            index: buttonIndex, //position of the button
            image: "zimbraicon" //icon
        };

        //toolbar.createOp api creates the button with some id and  params containing button details.
        var button = toolbar.createOp("APPT_LOCATION_TOOLBAR_BUTTON", buttonArgs);
        button.noMenuBar = true;

        button.removeAllListeners();
        // call the _showMapsDialog method when the toolbar button is clicked
        button.addSelectionListener(new AjxListener(this, this._showMapsDialog, [button]));
    }
};

Com_Zimbra_Gplaces.prototype._showMapsDialog = function(button, menu) {
    if (!this.mapsDialog) {
        this.mapsDialog = this._getMapsDialog();
    }
    this.mapsDialog.popup();
};

Com_Zimbra_Gplaces.prototype._getMapsDialog = function() {
    return new ZmMapsDialog({id:"ZmMapsDialog", composeController: this._composeController, zimlet: this});
};

ZmMapsDialog = function(params) {
    var parent = appCtxt.getShell();
    var id = params.id || "ZmMapsDialog";
    this._composeController = params.composeController;
    this.zimlet = params.zimlet;
    //var cancel = new DwtDialog_ButtonDescriptor(ZmMapsDialog.CANCEL_BUTTON, ZmMsg.cancel, DwtDialog.ALIGN_RIGHT);
    ZmDialog.call(this, {id:id, parent:parent});
    //this.setTitle("Select Location on Map");
    this.setTitle(this.zimlet.getMessage("mapsDialogTitle"));
    this._loadMapsApi();
    this.radius = this.zimlet.getConfig("radius");
    this.catagories1 = this.zimlet.getConfig("catagories1").split(" ");
    this.catagories2 = this.zimlet.getConfig("catagories2").split(" ");

};

ZmMapsDialog.prototype = new ZmDialog;
ZmMapsDialog.prototype.constructor = ZmMapsDialog;

ZmMapsDialog.prototype._okButtonListener = function() {

    var addressString = document.getElementById('map_search_query').value;
    var composeView = this._composeController._composeView;
    if (composeView && addressString) {
        var apptEditView = composeView.getApptEditView();
        var locationInputField = apptEditView ? apptEditView._locationInputField : null;
        if (locationInputField) {
            locationInputField.setValue(addressString.replace(",,", ","));
        }
    }
    this.popdown();
};

ZmMapsDialog.prototype._contentHtml = function() {
    var dialogHtml = new Array();
    var i = 0;
    dialogHtml[i++] = '<table id="map_dialog_table" cellspacing="0" cellpadding="0"> <tr>';
    dialogHtml[i++] = '<td>  <div id="map_canvas" style="width:500px; height:300px" class="dialogOverview"></div></td>';
    dialogHtml[i++] = '<td>  <div id="suggestions_tab" style="width:1px; height: 300px;overflow: auto;display:none;" class="dialogOverview" >';
    dialogHtml[i++] = '<h3 id="heading">&nbsp;&nbsp;';
    dialogHtml[i++] = this.zimlet.getMessage("nearbyPlaces");
    dialogHtml[i++] = '</h3>  <div id="suggestions_div">  </div>  </div></td>';
    dialogHtml[i++] = '</tr>  <tr>';
    dialogHtml[i++] = '<td colspan=2><input id="map_search_query" type="text" style="width:100%"> </td>';
    dialogHtml[i++] = '<tr><td colspan=2 align="right"><input type="button" id="geolocate" value="';
    dialogHtml[i++] = this.zimlet.getMessage("geolocationButtonText");
    dialogHtml[i++] = '"></td></tr>';
    dialogHtml[i++] = '</tr> </table>';
    return dialogHtml.join("");
};

ZmMapsDialog.prototype._initializeMaps = function() {

    var lat = Com_Zimbra_Gplaces.DEFAULT_LAT;
    var lng = Com_Zimbra_Gplaces.DEFAULT_LNG;
    var myLatlng = new google.maps.LatLng(lat, lng);
    var myOptions = {
        zoom: Com_Zimbra_Gplaces.DEFAULT_ZOOM_LEVEL,
        center: myLatlng,
        mapTypeId: google.maps.MapTypeId.ROADMAP,
        overviewMapControl: true,
        overviewMapControlOptions: {
            opened: false
        }
    };
    // alert("Inside initialize maps");
    this._map = new google.maps.Map(document.getElementById("map_canvas"), myOptions);
    ZmMapsDialog._geocoder = new google.maps.Geocoder();
    this._service = new google.maps.places.PlacesService(this._map);

    var input = document.getElementById('map_search_query');
    this._autocomplete = new google.maps.places.Autocomplete(input);
    this._autocomplete.bindTo('bounds', this._map);

    google.maps.event.addListener(this._autocomplete, 'place_changed', AjxCallback.simpleClosure(this._viewPlace, this));
    google.maps.event.addListener(this._map, 'click', AjxCallback.simpleClosure(this._addMarker, this));
    var geo_button = document.getElementById('geolocate');
    geo_button.onclick = this._geolocate.bind(this);

    // Going to your location by default
    this._geolocate();
};

ZmMapsDialog.prototype._geolocate = function() {

    if (navigator.geolocation) {
        navigator.geolocation.getCurrentPosition(AjxCallback.simpleClosure(this._geoSuccess, this), AjxCallback(this._geoError, this));
    }
    else {
        //DBG.println("geolocation not supported");
        return;
    }

};

ZmMapsDialog.prototype._geoSuccess = function(position) {

    var myLatLng = new google.maps.LatLng(position.coords.latitude, position.coords.longitude);
    var toAddMarker = {
        latLng: myLatLng
    };
    this._addMarker(toAddMarker);
    this._map.setZoom(Com_Zimbra_Gplaces.MEDIUM_ZOOM_LEVEL);
};

// When something goes wrong with the geolocation
ZmMapsDialog.prototype._geoError = function(msg) {
    var geo_button = document.getElementById('geolocate');
    //remove the geolocation button
    geo_button.style.display = "none";
    return msg;
};


//This method is called when the user uses autocomplete feature to select some location
//The place is then mapped onto the map
ZmMapsDialog.prototype._viewPlace = function() {
    var place = this._autocomplete.getPlace();
    var location = place.geometry.location;
    var toAddMarker = {
        latLng: location
    };

    this._addMarker(toAddMarker);
    var zoom = this._map.getZoom();
    if (zoom < Com_Zimbra_Gplaces.MEDIUM_HIGH_ZOOM_LEVEL) {
        this._map.setZoom(zoom + Com_Zimbra_Gplaces.INCREMENT_ZOOM)
    }
};


// This function adds marker when passed with an object whose latLng property is a google.maps.LatLng property
ZmMapsDialog.prototype._addMarker = function(event) {

    var zoom = this._map.getZoom();
    if (!this._marker) {

        this._marker = new google.maps.Marker({
            position: event.latLng,
            draggable: true,
            map: this._map
        });

        if (zoom < Com_Zimbra_Gplaces.HIGH_ZOOM_LEVEL) {
            this._map.setZoom(zoom + Com_Zimbra_Gplaces.INCREMENT_ZOOM);
        }
        this._reverseGeocode(event.latLng);

        this._map.panTo(event.latLng);
        this._attachMarkerEvent();
        this._getSuggestions();
        return;
    }
    else {

        if (zoom < Com_Zimbra_Gplaces.HIGH_ZOOM_LEVEL) {
            this._map.setZoom(zoom + Com_Zimbra_Gplaces.INCREMENT_ZOOM);
        }
        this._marker.setPosition(event.latLng);
        if (!event.reverseGeocodeSupress) {
            this._reverseGeocode(event.latLng);
        }
        this._marker.setMap(this._map);
        this._map.panTo(event.latLng);

    }
    this._expandedResults = false;
    this._getSuggestions();
};

// This function is used to expand the suggestion tab towards the right
ZmMapsDialog.prototype._expandSuggestionsTab = function() {

    var x = document.getElementById('suggestions_tab');
    var width = parseInt(x.style.width);
    x.style.display = "block";

    if (width < Com_Zimbra_Gplaces.SUGGESTIONS_TAB_WIDTH) {
        // increase the width by 20% of current width and 2px
        x.style.width = width + (1.2 * width) + 2;
    }

    if (width < Com_Zimbra_Gplaces.SUGGESTIONS_TAB_WIDTH) {
        setTimeout(function() {
            this._expandSuggestionsTab();
        }.bind(this), 2);      // 2 millisecond timeout

    }
};

// This function is used to collapse the suggestions tab to hide it
ZmMapsDialog.prototype._collapseSuggestionsTab = function() {

    var x = document.getElementById('suggestions_tab');
    var width = parseInt(x.style.width);

    // boundary condition
    if (width == 2) {
        x.style.width = width - 1;
        x.style.display = "none";
    }

    if (width >= 2) {
        // reducing the width to half every time
        x.style.width = width - width / 2;
    }

    if (width > 1) {
        setTimeout(function() {
            this._collapseSuggestionsTab();
        }.bind(this), 1);      // 1 millisecond timeout

    }
};

// Gets the suggestion depending on the position of the marker
// "options" object with a property of expand = 1 will increase the domain of the search
ZmMapsDialog.prototype._getSuggestions = function (options) {

    var catagories = [];

    if (options) {
        if (options.expand == 1) {
            // less refined results
            catagories = this.catagories1;
            //['restaurant','cafe','food','amusement_park','movie_theater','bowling_alley','art_gallery','night_club','aquarium','bar','zoo','stadium','casino','city_hall','establishment'];
            this._expandedResults = true;
        }
    }
    else {
        // more refined results
        catagories = this.catagories2;
        // ['food','restaurant','cafe','movie_theater','amusement_park','bowling_alley','art_gallery','night_club','bar','zoo','stadium','casino','city_hall'];
        this._expandedResults = false;
    }

    var request = {
        location: this._marker.getPosition(),
        radius: this.radius,
        types: catagories
    };

    this._service.search(request, this._updateSuggestions.bind(this));


};

// Updates suggestions onto the right pane
ZmMapsDialog.prototype._updateSuggestions = function (results, status) {


    var i;
    var list;
    list = document.getElementById("suggestions_list");

    if ((results.length < 5) && !this._expandedResults) {
        this._getSuggestions({expand: 1});
        return;
    }

    /*if (!this._expandedResults) {
     DBG.println("_updateSuggestions(): Showing high refined results");
     }
     else {
     DBG.println("_updateSuggestions(): Showing low refined results");
     }*/

    if (status == google.maps.places.PlacesServiceStatus.OK) {

        if (list) {
            list.parentNode.removeChild(list);
        }

        list = document.createElement("ul");
        list.id = "suggestions_list";

        var x;
        var y;
        if (results.length > 0) {
            this._expandSuggestionsTab();
        }
        else {
            this._collapseSuggestionsTab();
        }
        for (i = 0; i < results.length; i++) {
            var list_item = document.createElement("li");
            x = document.createTextNode(results[i].name);
            y = document.createElement("a");
            y.href = "#";
            y.onclick = this._suggestionClicked.bind(this, results[i]);
            y.appendChild(x);
            list_item.appendChild(y);
            list.appendChild(list_item);
        }
        var suggestions_div = document.getElementById('suggestions_div');
        suggestions_div.appendChild(list);
    }
    else {
        this._collapseSuggestionsTab();
    }
};

// When a suggestion in clicked, this method uses the places api to get the details of the place
ZmMapsDialog.prototype._suggestionClicked = function (result) {

    var location = result.geometry.location;
    this._addMarker({latLng: location, reverseGeocodeSupress: true});
    this._map.setZoom(Com_Zimbra_Gplaces.HIGH_ZOOM_LEVEL);

    var request = {
        reference: result.reference
    };

    var service = new google.maps.places.PlacesService(this._map);
    service.getDetails(request, this._addressUpdate);
};

// Update the address in the search field
ZmMapsDialog.prototype._addressUpdate = function (place, status) {

    if (status == google.maps.places.PlacesServiceStatus.OK) {
        var phone = "";
        if (place.phone) {
            phone = this.zimlet.getMessage("phone") + ": " + place.phone;
        }
        var loc_address = place.name + ", " + place.formatted_address + phone;
        document.getElementById('map_search_query').value = loc_address.replace(",,", ","); // sometimes 2 commas appear
    }
    else {
        return status;
    }
};

// This function finds the approximate address of the location using the coordinates
// Used in a case when some location is not pre-marked on the map or zero suggestion are returned or postal address of the place is unknown
ZmMapsDialog.prototype._reverseGeocode = function(latLng) {

    ZmMapsDialog._geocoder.geocode({'latLng': latLng}, function(results, status) {
        if (status == google.maps.GeocoderStatus.OK) {
            if (results[0]) {
                document.getElementById('map_search_query').value = results[0].formatted_address;
            }
        } else {
            document.getElementById('map_search_query').value = "";
            //DBG.println("Geocoder failed due to: " + status);
            return status;
        }
    });
};

// When a user clicks some section of the map, a marker should be placed there, removing the previous marker
ZmMapsDialog.prototype._attachMarkerEvent = function() {
    google.maps.event.addListener(this._marker, 'dragend', AjxCallback.simpleClosure(this._onDragEnd, this));
};

// The function is invoked when the marker is dragged to some place then dropped
ZmMapsDialog.prototype._onDragEnd = function() {
    this._reverseGeocode(this._marker.getPosition());
    this._getSuggestions();
};


// The static properties are created so as to maintain the this context after callback on loading maps api
ZmMapsDialog.MapsLOADED = false;
ZmMapsDialog._setMapsLoaded = function () {
    ZmMapsDialog.MapsLOADED = true;
};

ZmMapsDialog.prototype._checkMapsLoaded = function () {
    if (ZmMapsDialog.MapsLOADED) {
        this._initializeMaps();
    }
    else {
        setTimeout(this._checkMapsLoaded.bind(this), 1000);
    }
};

// Loads the google maps api
ZmMapsDialog.prototype._loadMapsApi = function() {
    if (!document.getElementById('maps_script')) {
        var script = document.createElement("script");
        script.type = "text/javascript";
        script.src = "http://maps.googleapis.com/maps/api/js?libraries=places&sensor=false&callback=ZmMapsDialog._setMapsLoaded";
        script.id = "maps_script";
        document.body.appendChild(script);
        this._checkMapsLoaded();
    }
    else {
        this._initializeMaps();
    }
};