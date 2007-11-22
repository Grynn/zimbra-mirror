UpComingEvents = function(){
    this.RESTAPI = UpComingEvents.RESTAPI //+ "?api_key="+ UpComingEvents.API_KEY + "&";
    this.APIKEY = "api_key=" + UpComingEvents.API_KEY;
};

UpComingEvents.API_KEY = "ae7d801cfb";
UpComingEvents.CMD_EVENTSEARCH = "event.search";

UpComingEvents.RESTAPI = "http://upcoming.yahooapis.com/services/rest/";
UpComingEvents.DEFAULT_RADIUS = 50;

UpComingEvents.prototype.searchEvents = function(params){

    var reqParams = [];
    reqParams.push(this.APIKEY);
    reqParams.push("method="+UpComingEvents.CMD_EVENTSEARCH);
    if(params.query){
        reqParams.push("search_text="+params.query);
    }
    if(params.latitude && params.longitude){
        reqParams.push("location="+params.latitude+","+params.longitude);
    }
    reqParams.push("radius="+(params.radius || UpComingEvents.DEFAULT_RADIUS));
    if(params.mindate) reqParams.push("min_date="+params.mindate);
    else {
        var date = new Date();
        reqParams.push("min_date="+date.getFullYear()+"-"+(date.getMonth() < 9 ? "0"+(date.getMonth()+1) : (date.getMonth()+1))+"-"+(date.getDate() < 10 ? "0"+date.getDate() : date.getDate()));
    }
    if(params.page) reqParams.push("page="+params.page);

    reqParams = reqParams.join("&");
    //console.log(reqParams);
    
    var callback = new AjxCallback(this,this._processSearchEventsResponse,params.callback);
    var proxyURL = ZmZimletBase.PROXY + AjxStringUtil.urlComponentEncode((this.RESTAPI + "?" + reqParams)) ;
    AjxRpc.invoke(reqParams, proxyURL, null, callback, true);
};

UpComingEvents.prototype._processSearchEventsResponse = function(callback,result){
     result = this.xmlToObject(result);
     //if(result.rsp.stat != "ok") return;
     var events = result.event;
     events = events.length ? events : [events];
     if(callback){
         callback.run(events);
     }
};

UpComingEvents.prototype.xmlToObject = function(result) {
    try {
        var xd = new AjxXmlDoc.createFromDom(result.xml).toJSObject(true, false, true);
    } catch(ex) {
        //this.displayErrorMessage(ex, result.text, "Problem contacting Snapfish");
    }
    return xd;
};