var ZMTB_TBItem = function(zmtb)
{
	if(!zmtb)
		return;
	this._zmtb = zmtb;
	zmtb.addTBItem(this);
	this._requests = {};
	//Localization strings
	this._localstrings = this._zmtb.getLocalStrings();
}

ZMTB_TBItem.prototype.getRqObj = function(id)
{
	if(!id)
		return null;
	return this._requests[id];
}

ZMTB_TBItem.prototype.addRqObj = function(id, obj)
{
	if(!id)
		return null;
	if(!obj)
		obj = {};
	this._requests[id] = obj;
}

ZMTB_TBItem.prototype.removeRqObj = function(id)
{
	delete this._requests[id];
}

ZMTB_TBItem.prototype.enable = function()
{
}

ZMTB_TBItem.prototype.disable = function()
{
}