function ZaAlias() {
	ZaItem.call(this, ZaEvent.S_ACCOUNT);
	this.attrs = new Object();
	this.id = "";
	this.name="";
}

ZaAlias.prototype = new ZaItem;
ZaAlias.prototype.constructor = ZaAlias;