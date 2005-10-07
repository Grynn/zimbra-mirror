function ZmCsfeResult() {
	this._data = null;
	this._isException = false;
}

ZmCsfeResult.prototype.set =
function(data, isException) {
	this._data = data;
	this._isException = isException;
}

ZmCsfeResult.prototype.getResponse =
function() {
	if (this._isException)
		throw this._data;
	else
		return this._data;
}

ZmCsfeResult.prototype.getException =
function() {
	return this._isException ? this._data : null;
}
