/*
 * Copyright 2008 Yahoo! Inc. All rights reserved.
 */


// The core API for applications to load and talk to the Messenger SDK.

// Dependencies: YMSGR namespace (_ymsgr.js)
//				 YUI core (yahoo.js) (optional - for logging)

// window.websdk namespace: required by websdk2_js.swf (? verify this)
// Application clients should talk through the YMSGR.sdk API.
// window.WEBSDK2: id of the websdk2_js.swf component 


var websdk = function() {
	var _app = null;
	var _sdk = null;
	var _loaded = false;
	var _cancelBuddyInfo = true;

	var eventManager = YMSGR.EventManager ? new YMSGR.EventManager() : null;
	
	// This might not be available until later:
	var sessionManager = YMSGR.SessionManager ? new YMSGR.SessionManager() : null;

	var _api = {
		"eventManager": eventManager,
		"sessionManager": sessionManager,
		"load": load,
		"onLoaded": onLoaded,
		"onEvent": onEvent,
		"initEvents": initEvents,
		"initSessions": initSessions,
		"getPrimaryId": getPrimaryId,
		"log": log,
		"login": login,
		"logoff": logoff,
		"send": send,
		"setVisibility": setVisibility,
		"setStatus": setStatus,
		"setCustomStatus": setCustomStatus,
		"addBuddyToGroup": addBuddyToGroup,
// See function declaration - not in use in mail right now
//		"setStealthStatus": setStealthStatus,
		"sendSubscribe": sendSubscribe,
		"getBuddies": getBuddies,
		"getPresenceList": getPresenceList,
		"getBlockList": getBlockList,
// See function declaration - not in use in mail right now
//		"getSubscribeList": getSubscribeList,
		"getInvisibleToList": getInvisibleToList,
		"getBuddyInfo": getBuddyInfo,
		"isBlockedUser": isBlockedUser,
		"getSelf": getSelf,
		"handleSelectEvents": handleSelectEvents,
		"removeBuddy": removeBuddy,
		"eov": ""		// end of API lists
	};


	// These former member functions have moved into a new
	// helper library YMSGR.Send, to allow bringing them
	// down separately, after launch:
//		"sendIM": sendIM,
//		"sendTyping": sendTyping,
//		"sendSMS": sendSMS,
//		"sendBuddyAuthorize": sendBuddyAuthorize,
//		"ignoreUser": ignoreUser,
//		"reportSpim": reportSpim,
//		"smsValidate": smsValidate,

	
	function load( app, sdkSrc ) {
		var buf = [
				'<object classid="clsid:D27CDB6E-AE6D-11cf-96B8-444553540000"',
				'id="WEBSDK2" width="1" height="1"',
				'codebase="http://download.macromedia.com/pub/shockwave/cabs/flash/swflash.cab">',
				'<param name="movie" value="' + sdkSrc + '" />',
				'<param name="quality" value="high" />',
				'<param name="bgcolor" value="#869ca7" />',
				'<param name="allowScriptAccess" value="always" />',
				'<embed src="' + sdkSrc + '"',
				'	quality="high" bgcolor="#869ca7"',
				'	width="1" height="1" name="WEBSDK2" align="middle"',
				'	play="true"',
				'	loop="false"',
				'	quality="high"',
				'	allowScriptAccess="always"',
				'	type="application/x-shockwave-flash"',
				'	pluginspage="http://www.macromedia.com/go/getflashplayer">',
				'</embed>',
				'</object>'
				].join(" ");
		
		_app = app;

		var el = document.createElement("DIV");
		document.body.appendChild(el);	
		el.innerHTML = buf;
	}
		
	function onLoaded(params) {
		
		// TODO: why double loading?
		if (!_loaded) {
			_loaded = true;
			
			// Not currently in use in CG.  Deprecated?
			// YEvents = params.YEvents;
			
			// window.YCONST is deprecated - removing as soon as 
			// the IM team agrees -- kevykev
			YMSGR.CONST = YCONST = params.YCONST;

			YMSGR.sdk.log("onLoaded");

			if (navigator.appName.indexOf("Microsoft") != -1) {
		        _sdk = window["WEBSDK2"];
		    } else {
		        _sdk = document["WEBSDK2"];
		    }
			
			// wait until YCONST is defined before initializing listeners
			this.initSessions();
			this.initEvents();
			
			this._sdk = _sdk;
			
			if ( _app )
				setTimeout( function(){ 
								_app.onLoaded(); 
							}, 10 );
		}
	}

	function initEvents () {
		if ( this.eventManager )
			return true;

		// Event manager support might be loaded after launch.
		if ( !YMSGR.EventManager )
			return false;

		this.eventManager = new YMSGR.EventManager();

		return true;
	}

	function initSessions () {
		if ( this.sessionManger )
			return true;

		// Session manager support might be loaded after launch.
		if ( !_loaded || !YMSGR.SessionManager )
			return false;

		this.sessionManager = new YMSGR.SessionManager();
		this.sessionManager.init();

		return true;
	}
	
	function getPrimaryId () {
		return _app ? _app.getPrimaryId() : "";
	}
	
	function login( cfg ) {
		_sdk.login( cfg );
	}
	
	function logoff() {
		// Console.log ( "YMSGR.logoff 1" );
		// alert ( "YMSGR.logoff 1" );
		_sdk.logoff();
		// Console.log ( "YMSGR.logoff 2" );
		// alert ( "YMSGR.logoff 2" );
	}
	
	function send(type, payload) {
		_sdk.send(type, payload);
	}
	
	function handleSelectEvents(eventArr, bUseDefault)
	{
		_sdk.handleSelectEvents(eventArr, bUseDefault);
	}

	function setVisibility( bVisible ) {
		var x = YMSGR.j2x( { flag: (bVisible ? 1 : 2) } );
		_sdk.send("setVisibility", x);
	}
	
	function setStatus( nStatus, bNoIdle ) {
		var x = YMSGR.j2x( { 	away_status: nStatus, 
			   			no_idle_time: (bNoIdle ? 1 : 0)
					 } );
		_sdk.send("setStatus", x);
	}
	
	/**
	 * Set user's custom message status.
	 * @param nDndFlag	0: Available, 1: Busy, 2: Idle
	 * @param nLinkType 0: Not a link, 1: webcam hotlink, 2: LaunchCast hotlink, 3. Y! Game session
	 * @param sCustomMsg Custom message
	 * @param bNoIdle Not to show buddies how long user has been idle
	 */
	function setCustomStatus( nDndFlag, nLinkType, sCustomMsg, bNoIdle) {
		sCustomMsg = sCustomMsg.replace(/\"/g, "&quot;").replace(/\'/g, "&apos;").replace(/</g, "&lt;").replace(/>/g, "&gt;");
		var x = YMSGR.j2x( { 	away_status: 99,
						no_idle_time: (bNoIdle ? 1 : 0),
						away_msg: sCustomMsg,
						custom_dnd_status: nDndFlag,
						status_link_type: nLinkType
					 } );
		_sdk.send("setStatus", x);
	}
	
	/**
     * @ignore
     */
// Not in use in CG.  Commenting out for now, rather than moving,
// as it is more rightfully an "avail. at login" function, rather
// than on-demand. -- kevykev
//    function setStealthStatus( sUser, bStealth, nCloudId ) {
//    	YMSGR.sdk.log( "Set stealth status : " + sBuddy + " " + !!bStealth);
//    	var x = YMSGR.j2x( {	current_id: _app.getPrimaryId(),
//    					command: (bStealth ? 1 : 2),
//    					flag: 2,
//    					buddy: sUser,
//    					cloud_id: nCloudId || 0
//    				 } );
//    	_sdk.send("stealthStatus", x);
//    }
    
    function addBuddyToGroup( sSelf, arrBuddies, nCloudId, sGroup ) 
    {    	
    	var arrRecs = [];
    	if (!nCloudId)
    		nCloudId = 0;
    	
    	for (var i=0; i<arrBuddies.length; i++)
    		arrRecs.push( { buddy: arrBuddies[i], cloud_id: nCloudId } );
    	
    	var x = YMSGR.j2x( { 	current_id: sSelf,
    					buddy_grp_name: sGroup || "Friends",
    					buddy_record_list: {
    						records: arrRecs
    					}	
    				 } );    				 
    	_sdk.send("addBuddyToGroup", x);
    }
    
    function removeBuddy( sSelf, sBuddy, nCloudId )
    {
    	_sdk.removeBuddy( sSelf, sBuddy, nCloudId );
    }
    
    function getSelf() {
    	var oSelf = _sdk.getSelf();
    	if (oSelf && oSelf.profiles)
    		return oSelf.profiles;
    	
    	return null;
    }
    
    function getBuddies() {
    	return _sdk.getBuddies();
    }
    
    function sendSubscribe( subscribers, bSubscribe, bAll ) {

    	var sCmd = "subscribe";
    	var x = [ "<payload current_id='",
    	          _app.getPrimaryId(),
    	          "'" ];
    	
    	if (!bSubscribe)
    	{
    		sCmd = "unsubscribe";
    		x.push(" flag='" + (bAll ? "1" : "0") + "'");
    	}
    	
    	x.push(">");
    	
       	if (subscribers && subscribers.length > 0)
       	{
       		x.push("<target_user>");
	    	for (var i=0; i<subscribers.length; i++)
	    		x.push("<record target_user='" + subscribers[i] + "'/>" );
	    	x.push("</target_user>");
       	}
    	
    	x.push("</payload>");
    	_sdk.sendSubscribe(sCmd, x.join(""));
    }

	function getPresenceList() {
		return _sdk.getPresenceList();
	}
	
	function getBlockList() {
		return _sdk.getBlockList();
	}

// Not in use in Candygram.  Should it be?
//	function getSubscribeList() {
//		var list = _sdk.getSubscribeList();
//		var listRecords = list.stranger_record_list;
//		if (!listRecords || !listRecords.records || !listRecords.records.length)
//			return null;
//		
//		return listRecords.records;
//	}
	
	function getInvisibleToList() {
		return _sdk.getInvisibleToList();
	}
	
	function getBuddyInfo( sBuddy, nCloudId, bStranger ) {
		var x = YMSGR.j2x( { 	name: sBuddy,
						cloud_id: nCloudId || 0
					 }, "record" );
		return _sdk.getBuddyInfo( x, bStranger );
	}
	
	function isBlockedUser( sUser, nCloudId ) {
		var x = YMSGR.j2x( { 	name: sUser,
						cloud_id: nCloudId || 0
		 			 }, "record" );
		return _sdk.isBlockedUser( x );
	}
	
	function log(s) {
		if ( window.YAHOO && YAHOO.log )
			YAHOO.log(s);
	}
	
	function onEvent(event, params) {

		// YMSGR.sdk.log("YMSGR.sdk.onEvent(): " + event);
		// Console.log("YMSGR.sdk.onEvent : " + event, null, params);

		var Const = YMSGR.CONST;

		switch (event) {

			// special handlers for offline case to simplify app handlers
			//
			case Const.YES_USER_LOGOFF_NOTIFY:
				params.away_status = "-1";
				break;
			case Const.YES_LOGGED_IN:
				_cancelBuddyInfo = true;
				break;

			// If the session manager is not loaded yet,
			// we must transmute these signals ourselves;
			// the app must then load the session code
			// if it wants to take advantage of it.  Once
			// the sessionManager is registered it will
			// take care of this for unhandled messages:
			// 
			case Const.YES_USER_HAS_MSG:
				if ( !this.sessionManager && _app ) {
					_app.onEvent(Const.YES_NEW_INCOMING_SESSION, params);
					return;
				}
				break;
			case Const.YES_USER_HAS_SAVED_MSG:
				if ( !this.sessionManager && _app ) {
					_app.onEvent(Const.YES_NEW_SAVED_SESSION, params);
					return;
				}
				break;
		}

		// TODO: If we ever do session resumption, need to revisit this - DOK
		// The first buddy_info event should go to the app,
		// All subsequent ones are sent to the event listeners
		//
		// ADDENDUM: This code has been modified to deal with the event manager
		// moving to on-demand.  The original design seems like a bad one to
		// me (mostly due to arbitrariness), and should be revisited.  We now
		// alway send BUDDY_INFO to the app and manage the "only do this once"
		// handling there. -- kevykev

//		if (_app && !(event == Const.YES_BUDDY_INFO && !_cancelBuddyInfo))
//		{
			_app.onEvent(event, params);
//		}
				
		if (this.eventManager)
		{

// ADDENDUM 2: As noted in Dennis' comment, this interferes with
// session resumption, which Candygram has had for a while.  In
// practice this meant that if the offline or conversation tabs
// were already open and you re-logged in, they would miss the
// first status change event for their user.  Commenting this
// out, and expecting it to go away entirely at some point. -- kevykev
//			if (event == Const.YES_BUDDY_INFO && _cancelBuddyInfo)
//			{
//				_cancelBuddyInfo = false;
//				return;
//			}

			this.eventManager.sendEvent(event, params);
		}
	}

	return _api;
}();


// Make a formal entry point in the YMSGR namespace.
// The sdk SWF depends on window.websdk, but JS clients
// should talk to this:

YMSGR.sdk = websdk;

