/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2005-2014 Zimbra Software, LLC.
 *
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.4 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 */

/**
 * Clipboard access singleton. Current implementation is built on ZeroClipboard.
 *
 * @class
 * @constructor
 */
AjxClipboard = function() {
	if (AjxClipboard.INSTANCE) {
		return AjxClipboard.INSTANCE;
	}
	AjxClipboard.INSTANCE = this;
	this._init();
};

/**
 * Returns true if clipboard access is supported.
 * @returns {Boolean}   true if clipboard access is supported
 */
AjxClipboard.isSupported = function() {
	// ZeroClipboard requires Flash
	return !!(window.ZeroClipboard && AjxPluginDetector.detectFlash());
};

AjxClipboard.prototype._init = function() {
	this._clients = {};
	ZeroClipboard.setMoviePath('/js/ajax/3rdparty/zeroclipboard/ZeroClipboard.swf');
};

/**
 * Adds a (ZeroClipboard) clipboard client with the given name, and ties it to the given operation (usually
 * a DwtMenuItem) with the given listeners. The listener set up pretty much ties us to a ZeroClipboard
 * implementation. If we ever switch to a different implementation, this API will probably have to change.
 *
 * @param {String}              name        a key to identify this client
 * @param {DwtControl}          op          widget that initiates copy (eg button or menu item)
 * @param {Object}              listeners   hash of events and (ZeroClipboard) callbacks
 */
AjxClipboard.prototype.addClient = function(name, op, listeners) {

	if (!op || this._clients[name]) {
		return;
	}

	// ZeroClipboard uses a transparent Flash movie to copy content to the clipboard. For security reasons,
	// the copy has to be user-initiated, so it click-jacks the user's press of the Copy menu item. We need
	// to make sure we propagate the mousedown and mouseup events so that the movie gets them.
	var clip = this._clients[name] = new ZeroClipboard.Client();
	op.setEventPropagation(true, [ DwtEvent.ONMOUSEDOWN, DwtEvent.ONMOUSEUP ]);
	op.removeListener(DwtEvent.ONMOUSEDOWN, op._listeners[DwtEvent.ONMOUSEDOWN]);
	op.removeListener(DwtEvent.ONMOUSEUP, op._listeners[DwtEvent.ONMOUSEUP]);

	// For some reason, superimposing the movie on just our menu item doesn't work, so we surround our
	// menu item HTML with a friendly container.
	var content = op.getContent();
	op.setContent('<div id="d_clip_container" style="position:relative"><div id="d_clip_button">' + content + '</div></div>');
	clip.glue('d_clip_button', 'd_clip_container');

	for (var event in listeners) {
		clip.addEventListener(event, listeners[event]);
	}
};

/**
 * Returns the (ZeroClipboard) client with the given name.
 * @param name
 * @returns {*}
 */
AjxClipboard.prototype.getClient = function(name) {
	return this._clients[name];
};
