function ZaForwardingAddress() {
	ZaItem.call(this, ZaEvent.S_ACCOUNT);
	this.attrs = new Object();
	this.id = "";
	this.name="";
}

ZaForwardingAddress.prototype = new ZaItem;
ZaForwardingAddress.prototype.constructor = ZaForwardingAddress;