/*
 * Copyright 2008 Yahoo! Inc. All rights reserved.
 */


// A manager class for events sent by the Messenger SDK.
// 
// Dependencies: YMSGR core (_ymsgr.js)
// 
// TODO: Should this change to an instance of YUI.EventProvider? -- kevykev


/**
 * EventManager
 * @class
 * Dispatch events to all listeners.
 */
YMSGR.EventManager = function () {};

YMSGR.EventManager.prototype = {

	m_listeners: {},

// Not in use within Mail.
//	reset: function () {
//		this.m_listeners = {};
//	},

	/**
	 * Register to receive notifications.
	 * TODO: need some performance tuning.
	 * @param _listener The listener object
	 * @param _sMessage The message which the listener is interested in
	 */
	registerListener: function (_listener, _nEvent) {
		var msgEntry = this.m_listeners[_nEvent];
		if (!msgEntry) {
			msgEntry = this.m_listeners[_nEvent] = [];
		}
		
		var nLen = msgEntry.length;
		var nFirstEmptySlot = nLen;
		for (var n=0; n<nLen; n++) {
			if (msgEntry[n] == _listener) {
				// Entry already exists. Return;
				return;
			}
			if (!msgEntry[n] && nFirstEmptySlot < nLen) {
				nFirstEmptySlot = n;
			}
		}
		msgEntry[nFirstEmptySlot] = _listener;
	},	

	/**
	 * Register for the set of events that a typical application will care about.
	 * @param _listener The listener object
	 */
	registerAppListener: function (_listener) {
		this.registerBCListener(_listener);
		var Const = YMSGR.CONST;
		this.registerListener(_listener, Const.YES_USER_LOGOFF_ERR);
		this.registerListener(_listener, Const.YES_CONNECTION_FAILED);
		this.registerListener(_listener, Const.YM_NEW_INCOMING_SESSION);
		this.registerListener(_listener, Const.YM_NEW_SAVED_SESSION);
	},

	/**
	 * Register for the set of events that typify monitoring buddy status (e.g. buddy list).
	 * @param _listener The listener object
	 */
	registerBCListener: function (_listener) {
		var Const = YMSGR.CONST;
		this.registerListener(_listener, Const.YES_USER_LOGIN);
		this.registerListener(_listener, Const.YES_BUDDY_INFO);
		this.registerListener(_listener, Const.YES_SET_AWAY_STATUS);
		this.registerListener(_listener, Const.YES_USER_LOGOFF_NOTIFY);
	},

	/**
	 * Register for the set of events that a typical chat session will care about.
	 * @param _listener The listener object
	 */
// Might not keep this.
//	registerSession: function (_listener) {
//		var Const = YMSGR.CONST;
//		this.registerBCListener(this);
//		this.registerListener(_listener, Const.YES_USER_HAS_MSG);
//		this.registerListener(_listener, Const.YES_USER_HAS_SAVED_MSG);
//		this.registerListener(_listener, Const.YES_USER_SEND_MESG);
//	},

	/**
	 * Unregister for notifications.
	 * @param _listener The listener object
	 * @param _sMessage The message which the listener attempts to unregister
	 */
	unregisterListener: function (_listener, _nEvent) {
		if (!this.m_listeners) {
			return;
		}
		var msgEntry = this.m_listeners[_nEvent];
		if (!msgEntry) {
			return;
		}
		var nLen = msgEntry.length;
		for (var n=0; n<nLen; n++) {
			if (msgEntry[n] == _listener) {
				msgEntry[n] = null;
				msgEntry.splice(n, 1);
				nLen--;
				return;
			}
		}
	},

	/**
	 * Unregisters for all notifications regardless of the message.
	 * @param _listener The listener object
	 */
	unregisterForAllEvents: function (_listener) {
		if (!this.m_listeners) {
			return;
		}

		for (var nEvent in this.m_listeners) {
			this.unregisterListener(_listener, parseInt(nEvent));
		}
	},

	/**
	 * Send a notification to all objects registered for the given message.
	 * @param _nEvent The message which the listener attempts to unregister.
	 * @param _params The context data of the event.
	 */
	sendEvent: function (_nEvent, _params) {
		var msgEntry = this.m_listeners[_nEvent];

		// TODO: SAFARI: Happens on Safari during launch.
		// Ignoring for now. -- kevykev
		if ( msgEntry == null ) return; 

		var nLen = msgEntry.length;
		for (var n=0; n<nLen; n++) {
			YMSGR.sdk.log ("nEvent: "+_nEvent, null, "YMSGR.EventManager::sendEvent");
			if (msgEntry[n]){
				msgEntry[n].onEvent(_nEvent, _params);
			}
		}
	}
};


