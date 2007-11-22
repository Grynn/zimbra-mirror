Com_Zimbra_Local = function(){
};

Com_Zimbra_Local.prototype = new ZmZimletBase;
Com_Zimbra_Local.prototype.constructor = Com_Zimbra_Local;

Com_Zimbra_Local.prototype.init = function(){
    //Initialize YahooLocal controller.
    this._controller = new YahooLocalController(this);
    //Add "Search Local" to the Search toolbar.
    this.addLocalSearchToolBar((new AjxListener(this,this._localSearchListener)));
};

//Add "Search Local" button the existing
Com_Zimbra_Local.prototype.addLocalSearchToolBar = function(listener){
     var searchToolBar = this._searchToolBar = appCtxt.getSearchController().getSearchToolbar();
     //Add Custom Button to the Search Toolbar
     var searchMenuBtnTd = document.getElementById(searchToolBar._htmlElId+"_searchMenuButton");
     var td = searchMenuBtnTd.parentNode.insertCell(searchMenuBtnTd.cellIndex+1);
     td.id = searchToolBar._htmlElId + "_searchLocal";
     td.className  =  'ZmSearchToolbarCell';
     var localSearchButton = searchToolBar._addButton({ buttonId:"_searchLocal", lbl:"Local Search", icon:"YLogo", tooltip:ZmMsg.searchTooltip});
     localSearchButton.addSelectionListener(listener);
};

Com_Zimbra_Local.prototype._localSearchListener = function(ev){
     var query = this._searchToolBar.getSearchFieldValue();
     //Empty search query, do nothing
     if(!query || AjxStringUtil.trim(query) == "") return;
     this._controller.searchLocal(query);
};

Com_Zimbra_Local.prototype.menuItemSelected = function(itemId) {
    switch(itemId){
        case "MY_LOCATION":
            this._controller.markMe();
            break;
        case "SEARCH":
            this._controller.searchQuery();
            break;
        case "SEARCH_ADDR":
            this._controller.searchAddr();
            break;
        case "TRAFFIC":
            this._controller.searchTraffic();
            break;
        case "UPCOMING":
            this._controller.searchUpcoming();
            break;
        case "MANUAL_LOCAION":
            this._controller.changeLocation();
            break;
        case "PREFERENCES":
            this.createPropertyEditor();
            break;
        default:
            this.createPropertyEditor();    
            break;
    }
};

Com_Zimbra_Local.prototype.singleClicked = function(){
     this._controller.markMe();
};

/*XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX*/

YahooLocalController = function(zimlet){

    if(arguments.length == 0) return;
    
    ZmController.call(this, appCtxt.getShell());
    ZmMsg.traffic = "Traffic";
    ZmMsg.trafficTooltip = "Search for Local Traffic";
    ZmMsg.upcoming = "Upcoming";
    ZmMsg.upcomingTooltip = "Search for Local Events";
    ZmOperation.registerOp("TRAFFIC", {textKey:"traffic", tooltipKey:"trafficTooltip", image:"YLogo"});
    ZmOperation.registerOp("UPCOMING",{textKey:"upcoming", tooltipKey:"upcomingTooltip", image:"ULogo"});
    this._listeners = {};
    this._listeners[ZmOperation.TRAFFIC] = new AjxListener(this,this._trafficListener);
    this._listeners[ZmOperation.CANCEL] = new AjxListener(this,this._cancelListener);
    this._listeners[ZmOperation.SEND] = new AjxListener(this,this._sendListener);
    this._listeners[ZmOperation.UPCOMING] = new AjxListener(this,this._upcomingListener);
    this._listeners[ZmOperation.SEARCH] = new AjxListener(this,this.searchAddr);

    this._zimlet = zimlet;

    //Set Default Location to Sunneyvale
    //this.setLocation("37.3878","-122.0195");
};

YahooLocalController.prototype = new ZmController;
YahooLocalController.prototype.constructor = YahooLocalController;

//Fine Lon/Lat and other details for the IPAddress.
//ThankYou: www.maxmind.com
YahooLocalController.prototype.getLocal = function(){

    if(! this._ylocal){

        this._countryCode = geoip_country_code();
        this._countryName = geoip_country_name();
        this._city        = geoip_city();
        this._region      = geoip_region();
        this._latitude    = geoip_latitude();
        this._longitude   = geoip_longitude();
        this._ylocal = {
            countryCode:    this._countryCode,
            countryName:    this._countryName,
            city:           this._city,
            region:         this._region,
            latitude:       this._latitude,
            longitude:      this._longitude
        };

        this._ylocalTmp = {
            countryCode:    this._countryCode,
            countryName:    this._countryName,
            city:           this._city,
            region:         this._region,
            latitude:       this._latitude,
            longitude:      this._longitude
        }
    }
    var manLoc = this._zimlet.getUserProperty("manuallocation");
    if(manLoc && manLoc.match(/true/i)){
        this._ylocalTmp.latitude = this._zimlet.getUserProperty("latitude");
        this._ylocalTmp.longitude = this._zimlet.getUserProperty("longitude");
        return this._ylocalTmp;
    }
    return this._ylocal;
};

YahooLocalController.prototype.setLocation = function(lat,lon, callback){
    this._zimlet.setUserProperty("manuallocation", "true");
    this._zimlet.setUserProperty("latitude",lat);
    this._zimlet.setUserProperty("longitude",lon);
    this._zimlet.saveUserProperties(callback);
};

//Search
YahooLocalController.prototype.searchLocal = function(query){
    
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

     this.getMapsView().searchLocal({
          query: query,
          defaultLat: cord.latitude,
          defaultLon: cord.longitude
     });
};

YahooLocalController.prototype.searchQuery = function(){
     var editorProps = [{
        label:  "Search for ",
        name:   "query",
        type:   "string"
     }];

    var view = new DwtComposite(appCtxt.getShell());
    var propEditor = new DwtPropertyEditor(view,true);
    propEditor.initProperties(editorProps);

    var dlg = new ZmDialog({
        title: "Search Yahoo Local",
        view: view,
        parent: appCtxt.getShell()
    });
    propEditor.setFixedLabelWidth();
    propEditor.setFixedFieldWidth();
    dlg.setButtonListener(DwtDialog.OK_BUTTON, new AjxListener(this, function(){
          var query = propEditor.getProperties().query;
          if(query == "") return;
          this.searchLocal(query);
          dlg.popdown();
          dlg.dispose();
    }));
    dlg.popup();
};

YahooLocalController.prototype.searchUpcoming = function(){
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

    this.getMapsView().searchUpcoming({
        latitude : cord.latitude,
        longitude: cord.longitude  
    });

};

YahooLocalController.prototype.searchTraffic = function(){
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

    this.getMapsView().searchTraffic({
        latitude:   cord.latitude,
        longitude:  cord.longitude
    });
};

YahooLocalController.prototype.markMe = function(){
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

    this.getMapsView().markMe(cord.latitude,cord.longitude);
};

YahooLocalController.prototype.searchAddr = function(ev){
    var editorProps = [
		{ label 		 : "Address",
		  name           : "address",
		  type           : "string",
		  minLength      : 10,
		  maxLength      : 200
		}
		];
	if (!this._dlg_propertyEditor) {
		var view = new DwtComposite(appCtxt.getShell());
		this._propertyEditor = new DwtPropertyEditor(view, true);
		var pe = this._propertyEditor;
		pe.initProperties(editorProps);
		var dialog_args = {
			title : "Yahoo Maps: Enter Address",
			view  : view,
            parent : appCtxt.getShell()
        };

        this._dlg_propertyEditor = new ZmDialog(dialog_args);
		var dlg = this._dlg_propertyEditor;
		pe.setFixedLabelWidth();
		pe.setFixedFieldWidth();
		dlg.setButtonListener(DwtDialog.OK_BUTTON,
				      new AjxListener(this, function() {
				          if (!pe.validateData()) {return;}
                          this._dlg_propertyEditor.popdown();
                          this.displayAddr(this._propertyEditor.getProperties().address);
                          this._dlg_propertyEditor.dispose();
	                      this._dlg_propertyEditor = null;
                      }));
	}
	this._dlg_propertyEditor.popup();
};

YahooLocalController.prototype.displayAddr = function(addr){
      this.setView({
         clean: true,
         typeControl:true,
         panControl:false,
         zoomControl:"long",
         zoomLevel: 6,
         defaultLocation: addr
     });

    this.getMapsView().markAddr({
         defaultLocation: addr
    });
};

YahooLocalController.prototype.changeLocation = function(){
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

    this.getMapsView().changeLocation({
        latitude:   cord.latitude,
        longitude:  cord.longitude
    });

};

//View
ZmController.YMAPS_VIEW = "YAHOOMAPS";
YahooLocalController.prototype.getMapsView = function(){
   if(!this._mapsView){
      this._mapsView = new YahooMaps(appCtxt.getShell(),null,this);
   }
   return this._mapsView;
};

YahooLocalController.prototype.setView = function(params){
    //Toolbar
    this._initializeToolBar();
    this._toolbar.enableAll(true);
    //YahooMapsView
    this._createMapView(params);
    //Show View
    this.showView(params);
};

YahooLocalController.prototype.showView = function(params){
    this._mapsView.prepareMap(params);
    appCtxt.getAppViewMgr().pushView(ZmController.YMAPS_VIEW);
    //Fit to Container, since the height and width needs to be set for this view
    appCtxt.getAppViewMgr()._fitToContainer([ZmAppViewMgr.C_APP_CONTENT]);
};

YahooLocalController.prototype.hideView  = function(){
    appCtxt.getAppViewMgr().popView(true, ZmController.YMAPS_VIEW);
};

YahooLocalController.prototype._createView = function(){
    var elements = {};
    elements[ZmAppViewMgr.C_TOOLBAR_TOP] = this._toolbar;
	elements[ZmAppViewMgr.C_APP_CONTENT] = this._mapsView;
    appCtxt.getAppViewMgr().createView(ZmController.YMAPS_VIEW, null, elements);
};

YahooLocalController.prototype._createMapView = function(params){
    if (this._mapsView) return;
    this.getMapsView(); //Creating Map View
    this._createView();
};

YahooLocalController.prototype._initializeToolBar = function(){
     if (this._toolbar) return;
     var buttons = [ZmOperation.SEND,ZmOperation.UPCOMING,ZmOperation.TRAFFIC,ZmOperation.SEARCH,ZmOperation.CANCEL];
     this._toolbar = new ZmButtonToolBar({parent:appCtxt.getShell(), buttons:buttons, className:"ZmAppToolBar ImgSkin_Toolbar"});
     //To Add Listeners to the Operations
     for (var i = 0; i < this._toolbar.opList.length; i++) {
		var button = this._toolbar.opList[i];
		if (this._listeners[button]) {
			this._toolbar.addSelectionListener(button, this._listeners[button]);
		}
	 }
};

//Listeners
YahooLocalController.prototype._sendListener = function(ev){
    var cc = AjxDispatcher.run("GetComposeController");
    var mapObject = this._getMapObject();

    var url = "http://maps.yahoo.com/#tt="+mapObject.query+"&lon="+mapObject.lon+"&lat="+mapObject.lat+"&mag="+mapObject.zoom+"&mvt=m&tp=1";
    var params = {
        action:ZmOperation.NEW_MESSAGE,
        subjOverride: "Shared Yahoo! Map",
		extraBodyText: "Hi,\n Your friend has shared you a Yahoo Map overlayed with results regarding \""+mapObject.query+"\". Please access this url:\t\n\n" + url
    };
	cc.doAction(params);
};

YahooLocalController.prototype._getMapObject = function(){
    return this._mapsView.getState();
};

YahooLocalController.prototype._upcomingListener = function(ev){
    this.searchUpcoming();
};

YahooLocalController.prototype._trafficListener = function(ev){
	//alert("Traffic Clicked");
    this.searchTraffic();
};

YahooLocalController.prototype._cancelListener = function(ev){
	this.hideView();
};


