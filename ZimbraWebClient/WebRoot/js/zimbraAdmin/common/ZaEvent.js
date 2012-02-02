/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2005, 2006, 2007, 2009, 2010 Zimbra, Inc.
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
* Creates an empty event of the given type.
* @constructor
* @class
* @parameter type - the type of the source of the event
* This class represents an event that encapsulates some sort of change to a model (data).
* The event has a data type (eg conversation), an event type (eg delete), a source (the
* data object generating the event), and a hash of arbitrary information (details).
*/
ZaEvent = function(type) {

	this.type = type; //source type
	this.event = null; //event type
	this.source = null;
	this._details = null;
}

// Listener types
ZaEvent.L_MODIFY = 1;
ZaEvent.L_PICKER = 2;

// Source types (note: there are not separate types for list models)
ZaEvent.EVENT_SOURCE_INDEX = 1;
ZaEvent.S_FOLDER		= ZaEvent.EVENT_SOURCE_INDEX++;
ZaEvent.S_TAG			= ZaEvent.EVENT_SOURCE_INDEX++;
ZaEvent.S_CONV			= ZaEvent.EVENT_SOURCE_INDEX++;
ZaEvent.S_MSG			= ZaEvent.EVENT_SOURCE_INDEX++;
ZaEvent.S_ATT			= ZaEvent.EVENT_SOURCE_INDEX++;
ZaEvent.S_CONTACT		= ZaEvent.EVENT_SOURCE_INDEX++;
ZaEvent.S_APPT			= ZaEvent.EVENT_SOURCE_INDEX++;
ZaEvent.S_NOTE			= ZaEvent.EVENT_SOURCE_INDEX++;
ZaEvent.S_PICKER		= ZaEvent.EVENT_SOURCE_INDEX++;
ZaEvent.S_SEARCH		= ZaEvent.EVENT_SOURCE_INDEX++;
ZaEvent.S_SETTING		= ZaEvent.EVENT_SOURCE_INDEX++;

//Source types for admin
ZaEvent.S_ACCOUNT		 = ZaEvent.EVENT_SOURCE_INDEX++;
ZaEvent.S_COS			 = ZaEvent.EVENT_SOURCE_INDEX++;
ZaEvent.S_DOMAIN		 = ZaEvent.EVENT_SOURCE_INDEX++;
ZaEvent.S_SERVER		 = ZaEvent.EVENT_SOURCE_INDEX++;
ZaEvent.S_GLOBALCONFIG	 = ZaEvent.EVENT_SOURCE_INDEX++;
ZaEvent.S_STATUS		 = ZaEvent.EVENT_SOURCE_INDEX++;
ZaEvent.S_CLUSTER_STATUS = ZaEvent.EVENT_SOURCE_INDEX++;
ZaEvent.S_DL 			 = ZaEvent.EVENT_SOURCE_INDEX++;
ZaEvent.S_MTA			 = ZaEvent.EVENT_SOURCE_INDEX++;
ZaEvent.S_ZIMLET		 = ZaEvent.EVENT_SOURCE_INDEX++;
ZaEvent.S_HOME		 = ZaEvent.EVENT_SOURCE_INDEX++;
// Event types
ZaEvent.EVENT_TYPES_INDEX = 1;
ZaEvent.E_CREATE		= ZaEvent.EVENT_TYPES_INDEX++;
ZaEvent.E_DELETE		= ZaEvent.EVENT_TYPES_INDEX++;
ZaEvent.E_MODIFY		= ZaEvent.EVENT_TYPES_INDEX++;
ZaEvent.E_LOAD			= ZaEvent.EVENT_TYPES_INDEX++;
ZaEvent.E_REMOVE		= ZaEvent.EVENT_TYPES_INDEX++;
ZaEvent.E_REMOVE_ALL	= ZaEvent.EVENT_TYPES_INDEX++;
ZaEvent.E_RENAME		= ZaEvent.EVENT_TYPES_INDEX++;
ZaEvent.E_MOVE			= ZaEvent.EVENT_TYPES_INDEX++;
ZaEvent.E_FLAGS			= ZaEvent.EVENT_TYPES_INDEX++;
ZaEvent.E_ADD_TAG		= ZaEvent.EVENT_TYPES_INDEX++;
ZaEvent.E_REMOVE_TAG	= ZaEvent.EVENT_TYPES_INDEX++;
ZaEvent.E_SEARCH  	    = ZaEvent.EVENT_TYPES_INDEX++;

// Public methods

ZaEvent.prototype.toString = 
function() {
	return "ZaEvent";
}

/**
* Sets the event type and source.
*
* @param event		event type
* @param source		object that generated the event (typically "this")
*/
ZaEvent.prototype.set =
function(event, source) {
	this.event = event; 
	this.source = source; 
}

/**
* Adds an arbitrary bit of info to the event.
*
* @param field		the detail's name
* @param value		the detail's value
*/
ZaEvent.prototype.setDetail =
function(field, value) {
	if(!this._details)
		this._details = new Object();
	this._details[field] = value;
}

/**
* Returns an arbitrary bit of info from the event.
*
* @param field		the detail's name
*/
ZaEvent.prototype.getDetail =
function(field) {
	if(!this._details)
		return null;
	else
		return this._details[field];
}

/**
* Sets the event details. Any existing details will be lost.
*
* @param details	a hash representing event details
*/
ZaEvent.prototype.setDetails =
function(details) {
	this._details = details ? details : new Object();
}

/**
* Returns the event details.
*/
ZaEvent.prototype.getDetails =
function() {
	return this._details;
}
