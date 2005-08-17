/**
* Creates an empty event of the given type.
* @constructor
* @class
* @parameter type - the type of the source of the event
* This class represents an event that encapsulates some sort of change to a model (data).
* The event has a data type (eg conversation), an event type (eg delete), a source (the
* data object generating the event), and a hash of arbitrary information (details).
*/
function ZaEvent(type) {

	this.type = type; //source type
	this.event = null; //event type
	this.source = null;
	this._details = new Object();
}

// Listener types
ZaEvent.L_MODIFY = 1;
ZaEvent.L_PICKER = 2;

// Source types (note: there are not separate types for list models)
var i = 1;
ZaEvent.S_FOLDER		= i++;
ZaEvent.S_TAG			= i++;
ZaEvent.S_CONV			= i++;
ZaEvent.S_MSG			= i++;
ZaEvent.S_ATT			= i++;
ZaEvent.S_CONTACT		= i++;
ZaEvent.S_APPT			= i++;
ZaEvent.S_NOTE			= i++;
ZaEvent.S_PICKER		= i++;
ZaEvent.S_SEARCH		= i++;
ZaEvent.S_SETTING		= i++;

//Source types for admin
ZaEvent.S_ACCOUNT		= i++;
ZaEvent.S_COS			= i++;
ZaEvent.S_DOMAIN		= i++;
ZaEvent.S_SERVER		= i++;
ZaEvent.S_GLOBALCONFIG	= i++;
ZaEvent.S_STATUS	= i++;

// Event types
i = 1;
ZaEvent.E_CREATE		= i++;
ZaEvent.E_DELETE		= i++;
ZaEvent.E_MODIFY		= i++;
ZaEvent.E_LOAD			= i++;
ZaEvent.E_REMOVE		= i++;
ZaEvent.E_REMOVE_ALL	= i++;
ZaEvent.E_RENAME		= i++;
ZaEvent.E_MOVE			= i++;
ZaEvent.E_FLAGS			= i++;
ZaEvent.E_ADD_TAG		= i++;
ZaEvent.E_REMOVE_TAG	= i++;

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
	this._details[field] = value;
}

/**
* Returns an arbitrary bit of info from the event.
*
* @param field		the detail's name
*/
ZaEvent.prototype.getDetail =
function(field) {
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
