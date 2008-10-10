/*
 * Copyright 2008 Yahoo! Inc. All rights reserved.
 */


// A Messenger class for managing chat sessions.


/**
 * @class
 * SessionManager
 * Keeps track of all ongoing chat sessions.
 */
YMSGR.SessionManager = function () {};

YMSGR.SessionManager.prototype = {
	m_sessions: {},
	m_lastStatus: {}, // For suppressing redundant events

	/**
	 * @ignore
	 */
	init: function () {
		var evm = YMSGR.sdk.eventManager;
		if (evm) {
			evm.registerBCListener(this);
			evm.registerListener(this, YMSGR.CONST.YES_USER_HAS_MSG);
			evm.registerListener(this, YMSGR.CONST.YES_USER_HAS_SAVED_MSG);
			evm.registerListener(this, YMSGR.CONST.YES_USER_SEND_MESG);
		}
	},

	makeKey: function (profile, buddy, cloud) {
		if (!cloud)
			cloud = "0";
		
		return profile + "~" + buddy + "~" + cloud;
	},


// There is no longer a provided session object class.  Applications
// must create and register the session objects themselves.  See
// immediately past this class definition for a sample session object
// declaration. -- kevykev
//	
//	/**
//	 * Create a new IM session, or return one if it already exists.
//	 * @param _buddyId Buddy ID
//	 * @return YMSGR.Session object
//	 */
//	createSession: function (_selfId, _buddyId, _cloudId) {
//		if (!_selfId || !_buddyId)
//			return null;
//
//		var sid = this.makeKey(_selfId, _buddyId, _cloudId);
//		if (!this.m_sessions[sid]) {
//			this.m_sessions[sid] = new YMSGR.Session(_selfId, _buddyId, _cloudId);
//		}
//
//		return this.m_sessions[sid];
//	},


	/**
	 * Get a stored IM session, returning null if none exists.
	 * @param _buddyId Buddy ID
	 * @return Session object
	 */
	getSession: function (_selfId, _buddyId, _cloudId) {
		if (!_selfId || !_buddyId)
			return null;
		_cloudId = _cloudId || "0";

		return this.m_sessions[ this.makeKey(_selfId, _buddyId, _cloudId) ];
	},


	/**
	 * Get the object with all stored IM sessions. Use for iteration.
	 * @return Session object store
	 */
	getSessionStore: function () {
		return this.m_sessions;
	},


	/**
	 * Store a new IM session.
	 * @param _buddyId Buddy ID
	 * @return Session object
	 */
	storeSession: function (_selfId, _buddyId, _cloudId, session) {
		if (!_selfId || !_buddyId)
			return;

		var sid = this.makeKey(_selfId, _buddyId, _cloudId || "0");

		this.m_sessions[ sid ] = session;
		delete this.m_lastStatus[ sid ];
	},

	/**
	 * Remove a session associated with a given buddy ID
	 * when shutting down an IM session.
	 * @param _buddyId Buddy ID
	 */
	removeSession: function (_selfId, _buddyId, _cloudId) {
		if (!_selfId || !_buddyId)
			return;

		var sid = this.makeKey(_selfId, _buddyId, _cloudId || "0");

		var session = this.m_sessions[sid];

		delete this.m_sessions[sid];
		delete this.m_lastStatus[sid];

		return session;
	},
	
	/**
	 * @ignore
	 */
	onEvent: function (_nEvent, _params) {
		// YMSGR.sdk.log ( "sMgr event: " + _nEvent );

		var Const = YMSGR.CONST;
		switch (_nEvent) {
			case Const.YES_BUDDY_INFO:
				this._onBuddyLoginData(_params, _nEvent);
				break;
			case Const.YES_USER_LOGOFF_NOTIFY:
				this._onBuddyStatusChange(_params, _nEvent);
				break;
			case Const.YES_USER_LOGIN:
			case Const.YES_SET_AWAY_STATUS:
				this._onBuddyStatusChange(_params, _nEvent);
				break;
			case Const.YES_USER_HAS_MSG:
				this._onReceiveIM(_params);
				break;
			case Const.YES_USER_HAS_SAVED_MSG:
				this._onReceiveSavedIM(_params);
				break;
			case Const.YES_USER_SEND_MESG:
				if (_params.appname == "TYPING")
					this._onReceiveTypingNotification(_params);
				break;
			default:
				break;
		}
	},

	/**
	 * @ignore
	 */
	_onBuddyLoginData: function(_params, _nEvent) {
		if (_params.buddy_info_list && _params.buddy_info_list.records)
		{
			var userInfoList = _params.buddy_info_list.records;
			var nLen = userInfoList.length;
			for (var n=0; n<nLen; n++) {
				this._onBuddyStatusChange(userInfoList[n], _nEvent);
			}
		}
	},

	/**
	 * @ignore
	 */
	_onBuddyStatusChange: function(_params, _nEvent) {
		var thisSid = this.makeKey("", _params.name, _params.cloud_id);
			// Note this is a suffix that will match all open sessions
			// (possibly against multiple profiles)

		var sessions = this.m_sessions;
		var lastStatus = this.m_lastStatus;

		// Send the status change to all potential listeners.
		// (Conversations -- messages back and forth -- take place
		// with one session.  Buddy status changes can affect more
		// than one [due to profiles]).

		for (var sid in sessions)
		{
			if ((sid.indexOf(thisSid) > 0) && sessions[sid]) {

				if ( !sessions[sid].onBuddyStatus ) continue;

				// This suppresses redundant events.  I preserved it from the
				// original YSession class, which no longer exists, but I am
				// not sure it's really necessary. It may only exist as a 
				// convenience for an earlier version of Candygram, which
				// would display each one without overwriting an older one if
				// present. -- kevykev
				//
				if (lastStatus[sid] && _nEvent == lastStatus[sid].event) {
					if (_nEvent != YMSGR.CONST.YES_BUDDY_INFO && 
						_nEvent != YMSGR.CONST.YES_SET_AWAY_STATUS &&
						_nEvent != YMSGR.CONST.YES_USER_LOGIN) {
						return;
					}
					if (_params.away_status == lastStatus[sid].params.away_status &&
							_params.away_msg == lastStatus[sid].params.away_msg) {
						return;
					}
				}
				lastStatus[sid] = {event: _nEvent, params:_params};

				sessions[sid].onBuddyStatus(_params, _nEvent);
			}
		}
	},

	/**
	 * @ignore
	 */
	_onReceiveIM: function (_params) {
		var session = this.getSession(_params.target_user /*self*/, _params.sender || _params.current_id /*buddy*/, _params.cloud_id);

		if ( session && session.onReceiveIM ) {
			session.onReceiveIM( _params );

		} else {

			// If no session has been registered, pass the event out
			// to the application (through the SDK).  We only store
			// sessions as directed by the application (we never
			// create them ourselves).
			// 
			// The sdk.onEvent will itself broadcast to event listeners,
			// including ourselves: we avoid infinite regress (esp. possible
			// with asynchronous loading of event handlers) by passing a
			// different message here (YES_NEW_INCOMING_SESSION vs
			// YES_USER_HAS_MSG). 
			
			YMSGR.sdk.onEvent(YMSGR.CONST.YES_NEW_INCOMING_SESSION, _params);
		}
	},
	
	/**
	 * @ignore
	 */
	_onReceiveSavedIM: function (_params) {
		var session = this.getSession(_params.target_user, _params.sender, _params.cloud_id);

		if ( session && session.onReceiveSavedIM ) {
			// Console.log ( "sesMgr offline msg: session found", null, _params );
			session.onReceiveSavedIM( _params );

		} else {

			// See comments in _onReceiveIM about why we send this:
			// Console.log ( "sesMgr offline msg: sending new offline event to SDK", null, _params );

			YMSGR.sdk.onEvent(YMSGR.CONST.YES_NEW_SAVED_SESSION, _params);
		}

	},

	/**
	 * @ignore
	 */
	_onReceiveTypingNotification: function (_params) {
		YMSGR.sdk.log("typing : " + _params.sender);
		var session = this.getSession(_params.target_user, _params.sender, _params.cloud_id);
		if (session && session.onReceiveTyping) {
			session.onReceiveTyping(_params.flag == "1");
		}
	}
};


// Objects registered with the session manager should implement the following API.
//
// TODO: Resolve with the SMS API. -- kevykev
//	
//	YMSGR.Session = function (_selfId, _buddyId, _cloudId) {
//		this.init(_selfId, _buddyId, _cloudId);
//	};
//	
//	YMSGR.Session.prototype = {
//		m_buddyId:"",
//		m_cloudId:"0",
//		m_selfId:"",
//	
//		init: function (_selfId, _buddyId, _cloudId) {
//			this.m_buddyId = _buddyId;
//			this.m_cloudId = _cloudId;
//			this.m_selfId = _selfId;
//		},
//	
//		/**
//		 * Handle a new incoming IM message sent to this session.
//		 */	
//		onReceiveIM: function (_params) {
//		},
//	
//		/**
//		 * Receiving a saved IM (one received while offline)
//		 */
//		onReceiveSavedIM: function (_params) {
//		},
//	
//		/**
//		 * Receiving a typing notification.
//		 * @param _isTyping true if typing, or false if typing is stopped.
//		 */
//		onReceiveTyping: function (_isTyping) {
//		},
//	
//		/**
//		 * Receiving a buddy status change notification
//		 */
//		onBuddyStatus: function (_params, _nEvent) {
//		}
//	};


