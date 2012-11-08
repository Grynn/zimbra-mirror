/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2007, 2008, 2009, 2010 Zimbra, Inc.
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
 * Creates a sound plugin control.
 * @constructor
 * @class
 * This class represents a widget that plays sounds. It uses a plugin such as Quick Time
 * or Windows Media to play the sounds and to display player controls. Do not invoke the
 * constructor directly. Instead use the create() method, which will choose the right
 * concrete class based on available plugins.
 *
 * @param	{hash}	params		a hash of parameters
 * @param {DwtControl}	params.parent	 the parent widget
 * @param {number}	params.width		the width of player
 * @param {number}	params.height		the height of player
 * @param {number}	params.volume		volume on a scale of 0 - {@link DwtSoundPlugin.MAX_VOLUME}
 * @param {string}	params.url		{String} the sound url
 * @param {boolean}	[params.offscreen]	{Boolean} if <code>true</code>, the player is initially offscreen. Use an appropriate position style
 * 							  if you set this to <code>true</code>. (This reduces flicker, and a tendency for the QT player
 * 							  to float in the wrong place when it's first created)
 * @param {string}	[params.className] the CSS class
 * @param {constant}	[params.posStyle=DwtControl.STATIC_STYLE] 	the positioning style (see {@link DwtControl})
 * 
 * @extends		DwtControl
 */
DwtSoundPlugin = function(params) {
	if (arguments.length == 0) return;
	params.className = params.className || "DwtSoundPlugin";
	DwtControl.call(this, {parent:params.parent, className:params.className, posStyle:params.posStyle});
	this._width = params.width || 200;
	this._height = params.height || 18;
	if (params.offscreen) {
		this.setLocation(Dwt.LOC_NOWHERE, Dwt.LOC_NOWHERE);
	}
};

DwtSoundPlugin.prototype = new DwtControl;
DwtSoundPlugin.prototype.constructor = DwtSoundPlugin;

/**
 * Defines the "max" volume.
 */
DwtSoundPlugin.MAX_VOLUME = 256;

// Status codes.
DwtSoundPlugin.WAITING = 1;
DwtSoundPlugin.LOADING = 2;
DwtSoundPlugin.PLAYABLE = 3;
DwtSoundPlugin.ERROR = 4;

/**
 * Factory method. Creates an appropriate sound player for whatever plugins are or are not installed.
 *
 * @param	{hash}	params		a hash of parameters
 * @param {DwtControl}	params.parent	 the parent widget
 * @param {number}	params.width		the width of player
 * @param {number}	params.height		the height of player
 * @param {number}	params.volume		volume on a scale of 0 - {@link DwtSoundPlugin.MAX_VOLUME}
 * @param {string}	params.url		{String} the sound url
 * @param {boolean}	[params.offscreen]	{Boolean} if <code>true</code>, the player is initially offscreen. Use an appropriate position style
 * 							  if you set this to <code>true</code>. (This reduces flicker, and a tendency for the QT player
 * 							  to float in the wrong place when it's first created)
 * @param {string}	[params.className] the CSS class
 * @param {constant}	[params.posStyle=DwtControl.STATIC_STYLE] 	the positioning style (see {@link DwtControl})
 */
DwtSoundPlugin.create =
function(params) {
	var pluginClass = this._getPluginClass();
	DBG.println("DwtSoundPlugin.create class= " + pluginClass.prototype.toString() + " url=" + params.url);
	return new pluginClass(params);
};

/**
 * Checks if the plugin is missing.
 * 
 * @return	{boolean}	<code>true</code> if plugin is missing
 */
DwtSoundPlugin.isPluginMissing =
function() {
	var pluginClass = this._getPluginClass();
	return pluginClass._pluginMissing;
};

/**
 * Checks if scripting is broken.
 * 
 * @return	{boolean}	<code>true</code> if scripting is broken
 */
DwtSoundPlugin.isScriptingBroken =
function() {
	var pluginClass = this._getPluginClass();
	return pluginClass._isScriptingBroken;
};

DwtSoundPlugin._getPluginClass =
function() {
	if (!DwtSoundPlugin._pluginClass) {
		if (AjxEnv.isIE) {
            var version;
            try {
                version = AjxPluginDetector.getQuickTimeVersion();
            } catch (e) {
            }

            //Use Quicktime for IE 8, as IE8 windows media player does not work with httpOnly cookie attribute
            //TODO: Currently if Quick time is not installed, users will not get any prompt to install it.
            if (AjxEnv.isIE8 && version) {
                DwtSoundPlugin._pluginClass = DwtQTSoundPlugin;
            } else if (AjxPluginDetector.detectWindowsMedia()) {
                //IE8 windows media player does not work with httpOnly cookie attribute
                DwtSoundPlugin._pluginClass = DwtWMSoundPlugin;
            }

		} 
		else if (AjxEnv.isSafari5up && !AjxEnv.isChrome && !AjxEnv.isWindows) {
			//safari quicktime does not work with httpOnly cookie attribute
			DwtSoundPlugin._pluginClass = DwtHtml5SoundPlugin;
		}
		else {
			var version = AjxPluginDetector.getQuickTimeVersion();
			if (version) {
				DBG.println("DwtSoundPlugin: QuickTime version=" + version);
				if (DwtQTSoundPlugin.checkVersion(version) && DwtQTSoundPlugin.checkScripting()) {
					DwtSoundPlugin._pluginClass = DwtQTSoundPlugin;
				} else {
					DwtSoundPlugin._pluginClass = DwtQTBrokenSoundPlugin;
				}
			} else {
				if (window.DBG && !DBG.isDisabled()) {
					DBG.println("DwtSoundPlugin: unable to get QuickTime version. Checking if QuickTime is installed at all...");
					AjxPluginDetector.detectQuickTime(); // Called only for logging purposes.
				}
			}
		}
		if (!DwtSoundPlugin._pluginClass) {
			DwtSoundPlugin._pluginClass = DwtMissingSoundPlugin;
		}
		DBG.println("DwtSoundPlugin: plugin class = " + DwtSoundPlugin._pluginClass.prototype.toString());
	}
	return DwtSoundPlugin._pluginClass;
};

// "Abstract" methods.
/**
 * Plays the sound.
 * 
 */
DwtSoundPlugin.prototype.play =
function() {
};

/**
 * Pauses the sound.
 * 
 */
DwtSoundPlugin.prototype.pause =
function() {
};

/**
 * Rewinds the sound.
 * 
 */
DwtSoundPlugin.prototype.rewind =
function() {
};

/**
 * Sets the current time in milliseconds.
 * 
 * @param	{number}	time		the time (in milliseconds)
 */
DwtSoundPlugin.prototype.setTime =
function(time) {
};

/**
 * Sets the volume.
 *
 * @param {number}	volume	the volume
 * 
 * @see		DwtSoundPlugin.MAX_VOLUME
 */
DwtSoundPlugin.prototype.setVolume =
function(volume) {
};

/*
 * Fills in the event with the following status information:
 * - status, a constant representing the loaded state of the sound
 * - duration, the length of the sound
 * - time, the current time of the sound
 * Returns true to continue monitoring status
 */
DwtSoundPlugin.prototype._resetEvent =
function(event) {
	return false;
};

DwtSoundPlugin.prototype.dispose =
function() {
	DwtControl.prototype.dispose.call(this);
	this._ignoreStatus();
};

/**
 * Adds a change listener to monitor the status of the sound being played.
 * The listener will be passed an event object with the following fields:
 * <ul>
 * <li>status, a constant representing the loaded state of the sound</li>
 * <li>duration, the length of the sound</li>
 * <li>time, the current time of the sound</li>
 * </ul>
 * 
 * @param {AjxListener}	listener	the listener
 */
DwtSoundPlugin.prototype.addChangeListener =
function(listener) {
    this.addListener(DwtEvent.ONCHANGE, listener);
    this._monitorStatus();
};

DwtSoundPlugin.prototype._monitorStatus =
function() {
	if (this.isListenerRegistered(DwtEvent.ONCHANGE)) {
		if (!this._statusAction) {
			this._statusAction = new AjxTimedAction(this, this._checkStatus);
		}
		this._statusActionId = AjxTimedAction.scheduleAction(this._statusAction, 250);
	}
};

DwtSoundPlugin.prototype._ignoreStatus =
function() {
	if (this._statusActionId) {
		AjxTimedAction.cancelAction(this._statusActionId);
	}
};

DwtSoundPlugin.prototype._checkStatus =
function() {
	this._statusActionId = 0;
	if (!this._changeEvent) {
		this._changeEvent = new DwtEvent(true);
		this._changeEvent.dwtObj = this;
	}
	var keepChecking = this._resetEvent(this._changeEvent);
    this.notifyListeners(DwtEvent.ONCHANGE, this._changeEvent);
    if (keepChecking) {
		this._monitorStatus();
    }
};

DwtSoundPlugin.prototype._createQTHtml =
function(params) {
	// Adjust volume because the html parameter is in [0 - 100], while the
	// javascript method takes [0 - 256].
	var volume = params.volume * 100 / 256;
	var html = [
		"<embed classid='clsid:02BF25D5-8C17-4B23-BC80-D3488ABDDC6B' ",
		"id='", this._playerId,
		"' width='", this._width,
		"' height='", this._height,
		"' src='", params.url,
		"' volume='", volume,
		"' enablejavascript='true' type='audio/wav'/>"
	];
	this.getHtmlElement().innerHTML = html.join("");
};

/**
 * Sound player that goes through the QuickTime (QT) plugin.
 * @class
 * Some useful references when dealing with quick time:
 * <ul>
 * <li>Quick Time script reference
 * <a href="http://developer.apple.com/documentation/QuickTime/Conceptual/QTScripting_JavaScript/bQTScripting_JavaScri_Document/chapter_1000_section_5.html" target="_blank">http://developer.apple.com/documentation/QuickTime/Conceptual/QTScripting_JavaScript/bQTScripting_JavaScri_Document/chapter_1000_section_5.html</a>
 * </li>
 * <li>Quick Time embed tag attributes tutorial
 * <a href="http://www.apple.com/quicktime/tutorials/embed2.html" target="_blank">http://www.apple.com/quicktime/tutorials/embed2.html</a>
 * </li>
 * </ul>
 * 
 * @param	{hash}	params		a hash of parameters
 * 
 * @extends		DwtSoundPlugin
 * 
 * @private
 */
DwtQTSoundPlugin = function(params) {
	if (arguments.length == 0) return;
	params.className = params.className || "DwtSoundPlugin";
	DwtSoundPlugin.call(this, params);

	this._playerId = Dwt.getNextId();
	this._createHtml(params);
};

DwtQTSoundPlugin.prototype = new DwtSoundPlugin;
DwtQTSoundPlugin.prototype.constructor = DwtQTSoundPlugin;

DwtQTSoundPlugin.prototype.toString =
function() {
	return "DwtQTSoundPlugin";
};

/**
 * Checks the QuickTime version.
 * 
 * @param	{array}	version	the version as an array (for example: 7.1.6 is [7, 1, 6] )
 * @return	{boolean}	<code>true</code> if version is OK
 */
DwtQTSoundPlugin.checkVersion =
function(version) {
	if (AjxEnv.isFirefox) {
		// Quicktime 7.1.6 introduced a nasty bug in Firefox that can't be worked around by
		// the checkScripting() routine below. I'm going to disable all QT versions that
		// are greater than 7.1.6. We should change this check when QT is fixed. More info:
		// http://lists.apple.com/archives/quicktime-users/2007/May/msg00016.html
		var badVersion = "716";
		var currentVersion = "";
		for(var i = 0; i < version.length; i++) {
			currentVersion += version[i];
		}
		return currentVersion != badVersion;
	} 
	return true;
};

/**
 * Checks scripting.
 * 
 * @return	{boolean}	<code>true</code> if scripting is OK
 */
DwtQTSoundPlugin.checkScripting =
function() {
	var success = false;
	var shell = DwtControl.fromElementId(window._dwtShellId);
	var args = {
		parent: shell,
		width: 200,
		height: 16,
		offscreen: true,
		posStyle: DwtControl.RELATIVE_STYLE,
		url: "/public/sounds/im/alert.wav", // Not a valid url.
		volume: 0
	};
	var test = new DwtQTSoundPlugin(args);
	try {
		var element = test._getPlayer();
		success = element.GetQuickTimeVersion && element.GetQuickTimeVersion();
		if (!success) {
			DBG.println("The QuickTime plugin in this browser does not support JavaScript.");
		}
	} catch (e) {
		DBG.println("An exception was thrown while checking QuickTime: " + e.toString());
	} finally {
		test.dispose();
	}
	return success;
};

DwtQTSoundPlugin.prototype.play =
function() {
	var player = this._getPlayer();
    try {
	    player.Play();
    }catch(e) {
    }
	this._monitorStatus();
};

DwtQTSoundPlugin.prototype.pause =
function() {
	try {
		var player = this._getPlayer();
		player.Stop();
	} catch (e) {
		// Annoying: QT gets all messed up if you stop it before it's loaded.
		// I could try and do more here...check the status, if it's waiting then
		// set some flag and then if I somehow knew when the sound loaded, I
		// could prevent it from playing.
		DBG.println("Failed to stop QuickTime player.");
	}
};

DwtQTSoundPlugin.prototype.rewind =
function() {
	try {
		var player = this._getPlayer();
		player.Rewind();
	} catch (e) {
		// Grrr. Same problem here as described in pause();
		DBG.println("Failed to rewind QuickTime player.");
	}
};

DwtQTSoundPlugin.prototype.setTime =
function(time) {
	var player = this._getPlayer();
	try {
		var scale = 1000 / player.GetTimeScale(); // Converts to milliseconds.
		player.SetTime(time / scale);
	} catch (e) {
		// Grrr. Same problem here as described in pause();
		DBG.println("Failed to rewind QuickTime player.");
	}
};

DwtQTSoundPlugin.prototype.setVolume =
function(volume) {
	var player = this._getPlayer();
	player.SetVolume(volume);
};

DwtQTSoundPlugin.prototype._resetEvent =
function(event) {
	var keepChecking = true;
	var player = this._getPlayer();
	event.finished = false;
	var valid = false;
	if (player) {
		var status = player.GetPluginStatus();
		switch (status) {
			case "Waiting":
				event.status = DwtSoundPlugin.LOADING;
				break;
			case "Loading":
				event.status = DwtSoundPlugin.LOADING;
				break;
			case "Playable":
			case "Complete":
				valid = true;
				event.status = DwtSoundPlugin.PLAYABLE;
				break;
			default :
				event.status = DwtSoundPlugin.ERROR;
				event.errorDetail = status;
				keepChecking = false;
				break;
		}
	}
	if (valid) {
		var scale = 1000 / player.GetTimeScale(); // Converts to milliseconds.
		event.time = player.GetTime() * scale;
		event.duration = player.GetDuration() * scale;
	} else {
		event.status = DwtSoundPlugin.WAITING;
		event.time = 0;
		event.duration = 100;
	}
	if (event.status == DwtSoundPlugin.PLAYABLE && event.time == event.duration) {
		event.time = 0;
		event.finished = true;
		keepChecking = false;
	}
	return keepChecking;
};

DwtQTSoundPlugin.prototype._createHtml =
function(params) {
	this._createQTHtml(params);
};

DwtQTSoundPlugin.prototype._getPlayer =
function() {
	return document.getElementById(this._playerId);
};

//////////////////////////////////////////////////////////////////////////////
// Sound player that uses the QuickTime (QT) plugin, but does not
// make script calls to the plugin. This handles the bad quicktime
// installs all over the place.
//
//////////////////////////////////////////////////////////////////////////////
DwtQTBrokenSoundPlugin = function(params) {
	if (arguments.length == 0) return;
	params.className = params.className || "DwtSoundPlugin";
	DwtSoundPlugin.call(this, params);

	this._playerId = Dwt.getNextId();
	this._createHtml(params);
};

DwtQTBrokenSoundPlugin.prototype = new DwtSoundPlugin;
DwtQTBrokenSoundPlugin.prototype.constructor = DwtQTBrokenSoundPlugin;

DwtQTBrokenSoundPlugin.prototype.toString =
function() {
	return "DwtQTBrokenSoundPlugin";
};

DwtQTBrokenSoundPlugin._isScriptingBroken = true;

DwtQTBrokenSoundPlugin.prototype._resetEvent =
function(event) {
	// Make up some fake event data
	event.time = 0;
	event.duration = 100;
	event.status = DwtSoundPlugin.PLAYABLE;
	event.finished = true; // Allows messages to be marked as read.
	return false; // Stop checking status.
};

DwtQTBrokenSoundPlugin.prototype._createHtml =
function(params) {
	this._createQTHtml(params);
};

/**
 * Sound player that goes through the HTML5 audio tag
 * @class
 * This class provides and HTML5 audio widget
 *
 * @param	{hash}	params		a hash of parameters
 *
 * @extends		DwtSoundPlugin
 *
 * @private
 */
DwtHtml5SoundPlugin = function(params) {
	if (arguments.length == 0) return;
	params.className = params.className || "DwtSoundPlugin";
	DwtSoundPlugin.call(this, params);

	this._playerId = Dwt.getNextId();
    this._retryLoadAudio = 0;
	this._createHtml(params);	
};

DwtHtml5SoundPlugin.prototype = new DwtSoundPlugin;
DwtHtml5SoundPlugin.prototype.constructor = DwtHtml5SoundPlugin;

DwtHtml5SoundPlugin.prototype.toString = 
function() {
	return "DwtHtml5SoundPlugin";
};

DwtHtml5SoundPlugin.prototype._createHtml = 
function(params) {
    if (AjxEnv.isSafari) {
        AjxRpc.invoke(null, params.url, { 'X-Zimbra-Encoding': 'x-base64' },
            this._setSource.bind(this), AjxRpcRequest.HTTP_GET);
    } else {
        this.getHtmlElement().innerHTML = this._getAudioHtml(params.url);
    }
};

DwtHtml5SoundPlugin.prototype._getAudioHtml =
function(source) {
    var html = [
     "<audio autoplay='yes' ",
     "id='", this._playerId,
     "' preload ",
     "><source src='", source,
     "' type='", ZmVoiceApp.audioType, "' />",
     "</audio>"
     ];
    return html.join("");
};

DwtHtml5SoundPlugin.prototype._setSource =
function(response) {
    if(response.success){
        this.getHtmlElement().innerHTML = this._getAudioHtml('data:' + ZmVoiceApp.audioType +';base64,' + response.text);
    }
};

DwtHtml5SoundPlugin.prototype.play =
function() {
	var player = this._getPlayer();
    if (player && player.readyState){
        try {
	        this._monitorStatus();
            player.play();
        } catch (ex){
            DBG.println("Exception in DwtHtml5SoundPlugin.prototype.play: "+ ex);
        }
    }
};

DwtHtml5SoundPlugin.prototype.pause =
function() {
	var player = this._getPlayer();
    if (player && player.readyState){
        try {
            player.pause();
        } catch (ex){
            DBG.println("Exception in DwtHtml5SoundPlugin.prototype.pause: "+ ex);
        }
    }
};

DwtHtml5SoundPlugin.prototype._getPlayer =
function() {
	return document.getElementById(this._playerId);
};


DwtHtml5SoundPlugin.prototype.rewind =
function() {
	this.setTime(0);
};

DwtHtml5SoundPlugin.prototype.setTime =
function(time) {
    if (isNaN(time)){
        time = 0;
    }
    time = (time > 0) ?  (time / 1000) : 0;
	var player = this._getPlayer();
    try {
        if (player && player.readyState && player.currentTime != time ){
            player.currentTime = time;
            if (player.controls){
                player.controls.currentPosition =  time;
            }
        }
    } catch(ex){
        DBG.println("Exception in DwtHtml5SoundPlugin.prototype.setTime: "+ ex);
    }
};

DwtHtml5SoundPlugin.prototype._resetEvent =
function(event) {
	var keepChecking = true;
	var player = this._getPlayer();
	event.finished = false;
	/*
	use HTML5 canPlay event instead
	var valid = false;
	if (player) {
		var status = player.duration;
		switch (status) {
			case "NaN":
				event.status = DwtSoundPlugin.LOADING;
				break;
			default :
				event.status = DwtSoundPlugin.PLAYABLE;
				valid = true;
				keepChecking = false;
				break;
		}
	} */
	/*if (valid) {
		var scale = 1000; // Converts to milliseconds.
		event.time = player.currentTime * scale;
		event.duration = player.duration * scale;
	} */
	if(!valid) {
		event.status = DwtSoundPlugin.WAITING;
		event.time = 0;
		event.duration = 100;
	}
	if (event.status == DwtSoundPlugin.PLAYABLE && event.time == event.duration) {
		event.time = 0;
		event.finished = true;
		keepChecking = false;
	}
	return keepChecking;
};

/**
 * Adds a change listener to monitor the status of the sound being played.
 * Handles the HTML5 event timeupdate 
 * The listener will be passed an event object with the following fields:
 * <ul>
 * <li>status, a constant representing the loaded state of the sound</li>
 * <li>duration, the length of the sound</li>
 * <li>time, the current time of the sound</li>
 * </ul>
 *
 * @param {AjxListener}	listener	the listener
 */
DwtHtml5SoundPlugin.prototype.addChangeListener =
function(listener) {
	var player = this._getPlayer();
    if (!player){
        if (this._retryLoadAudio < 10){
            setTimeout(this.addChangeListener.bind(this,listener), 1000);
        }
        this._retryLoadAudio++;
        return;
    }
	var obj = this;
	player.addEventListener("timeupdate", function(e) { 
			listener.handleEvent({time: player.currentTime * 1000, duration: player.duration * 1000, status: DwtSoundPlugin.PLAYABLE});}, false);
	player.addEventListener("ended", function(e) {
			player.pause(); 
			obj.setTime(0);
			listener.obj.setFinished();
	});
	player.addEventListener("canplay", function(event) {
		var scale = 1000; // Converts to milliseconds.
		event.time = player.currentTime * scale;
		event.duration = player.duration * scale;
		player.play();
	});
	this._monitorStatus();
};
//////////////////////////////////////////////////////////////////////////////
// Sound player that goes through the Windows Media (WM) plugin.
//
// Some useful references when dealing with wmp:
// Adding Windows Media to Web Pages - Adding Scripting
//   http://msdn2.microsoft.com/en-us/library/ms983653.aspx#adding_scripting__yhbx
// Parameters supported by Windows Media Player
//   http://www.mioplanet.com/rsc/embed_mediaplayer.htm
// WM Object Model Reference:
//   http://msdn2.microsoft.com/en-us/library/bb249259.aspx
//////////////////////////////////////////////////////////////////////////////
DwtWMSoundPlugin = function(params) {
	if (arguments.length == 0) return;
	params.className = params.className || "DwtSoundPlugin";
	DwtSoundPlugin.call(this, params);

	this._playerId = Dwt.getNextId();
	this._createHtml(params);
};

DwtWMSoundPlugin.prototype = new DwtSoundPlugin;
DwtWMSoundPlugin.prototype.constructor = DwtWMSoundPlugin;

DwtWMSoundPlugin.prototype.toString =
function() {
	return "DwtWMSoundPlugin";
};

DwtWMSoundPlugin.prototype.play =
function() {
	var player = this._getPlayer();
	player.controls.play();
	this._monitorStatus();
};

DwtWMSoundPlugin.prototype.pause =
function() {
	var player = this._getPlayer();
	player.controls.pause();
};

DwtWMSoundPlugin.prototype.rewind =
function() {
	this.setTime(0);
};

DwtWMSoundPlugin.prototype.setTime =
function(time) {
	var player = this._getPlayer();
	player.controls.currentPosition = time / 1000;
};

DwtWMSoundPlugin.prototype.setVolume =
function(volume) {
	var volume = volume * 100 / 256;
	var player = this._getPlayer();
	player.settings.volume = volume;
};

DwtWMSoundPlugin.prototype._resetEvent =
function(event) {
	var keepChecking = true;
	var player = this._getPlayer();
	var error = player.currentMedia.error;
	if (error) {
		event.status = DwtSoundPlugin.ERROR;
		event.errorDetail = error.errorDescription;
		keepChecking = false;
	} else if (!player.controls.isAvailable("currentPosition")) { // if (!is loaded)
		// Whatever....fake data.
		event.status = DwtSoundPlugin.LOADING;
		event.time = 0;
		event.duration = 100;
	} else {
		event.status = DwtSoundPlugin.PLAYABLE;
		event.time = player.controls.currentPosition * 1000;
		event.duration = player.currentMedia.duration * 1000 || event.time + 100; // Make sure max > min in slider
		if (!event.time) {
			event.finished = true;
			keepChecking = false;
			player.close();
		}
	}
	return keepChecking;
};

//TODO: Take out all the AjxEnv stuff in here, unless we find a way to use WMP in Firefox.
DwtWMSoundPlugin.prototype._createHtml =
function(params) {
	var volume = params.volume * 100 / 256;

	var html = [];
	var i = 0;
	if (AjxEnv.isIE) {
		html[i++] = "<object classid='CLSID:6BF52A52-394A-11d3-B153-00C04F79FAA6' id='";
		html[i++] = this._playerId;
		html[i++] = "'>";
	} else {
		html[i++] = "<embed classid='CLSID:6BF52A52-394A-11d3-B153-00C04F79FAA6' id='";
		html[i++] = this._playerId;
		html[i++] = "' ";
	}
	var pluginArgs = {
		width: this._width,
		height: this._height,
		url: params.url,
		volume: volume,
		enablejavascript: "true" };
	for (var name in pluginArgs) {
		if (AjxEnv.isIE) {
			html[i++] = "<param name='";
			html[i++] = name;
			html[i++] = "' value='";
			html[i++] = pluginArgs[name];
			html[i++] = "'/>";
		} else {
			html[i++] = name;
			html[i++] = "='";
			html[i++] = pluginArgs[name];
			html[i++] = "' ";
		}
	}
	if (AjxEnv.isIE) {
		html[i++] = "</object>";
	} else {
		html[i++] = " type='application/x-mplayer2'/>";
	}

	this.getHtmlElement().innerHTML = html.join("");
	DBG.printRaw(html.join(""));
};

DwtWMSoundPlugin.prototype._getPlayer =
function() {
	return document.getElementById(this._playerId);
};

//////////////////////////////////////////////////////////////////////////////
// Sound player for browsers without a known sound plugin.
//////////////////////////////////////////////////////////////////////////////
DwtMissingSoundPlugin = function(params) {
	if (arguments.length == 0) return;
	params.className = params.className || "DwtSoundPlugin";
	DwtSoundPlugin.call(this, params);

    var args = { };
    this.getHtmlElement().innerHTML = AjxTemplate.expand("dwt.Widgets#DwtMissingSoundPlayer", args);

    this._setMouseEventHdlrs();
};

DwtMissingSoundPlugin.prototype = new DwtSoundPlugin;
DwtMissingSoundPlugin.prototype.constructor = DwtMissingSoundPlugin;

DwtMissingSoundPlugin.prototype.toString =
function() {
	return "DwtMissingSoundPlugin";
};

DwtMissingSoundPlugin._pluginMissing = true;

DwtMissingSoundPlugin.prototype.addHelpListener =
function(listener) {
	this.addListener(DwtEvent.ONMOUSEDOWN, listener);
};
