function Com_Zimbra_Search() {
	this._divID = Dwt.getNextId();
};

Com_Zimbra_Search.prototype = new ZmZimletBase;
Com_Zimbra_Search.prototype.constructor = Com_Zimbra_Search;

Com_Zimbra_Search.HANDLERS = [];

Com_Zimbra_Search.registerHandler = function(ctor) {
	Com_Zimbra_Search.HANDLERS.push(ctor);
};

Com_Zimbra_Search.prototype.init = function() {
	var a = Com_Zimbra_Search.HANDLERS;
	for (var i = 0; i < a.length; ++i) {
		var ctor = a[i];
		var h = a[i] = new ctor(this);
		this.addSearchDomainItem(h.icon, h.label,
					 new AjxListener(this, this.selectListener, h));
	}
};

Com_Zimbra_Search.prototype.selectListener = function(handler) {
	var query = AjxStringUtil.trim(this.getSearchQuery(), true);
	if (query != "") {
		var code = handler.getSearchFormHTML(query);
		if (code) {
			var div = document.getElementById(this._divID);
			if (!div) {
				div = document.createElement("div");
				div.id = this._divID;
				div.style.position = "absolute";
				div.style.left = "-30000px";
				div.style.top = "-30000px";
				document.body.appendChild(div);
			}
			div.innerHTML = code;
			var form = div.getElementsByTagName("form")[0];
			if (/^get$/i.test(form.method)) {
				this._windowOpen(form);
			} else {
				form.submit();
			}
			setTimeout(function() {
				div.removeChild(form);
				form = null;
				div = null;
			}, 1000);
		}
	}
};

Com_Zimbra_Search.prototype._windowOpen = function(form) {
	var fields = form.elements;
	var url = form.action;
	var args = [];
	for (var i = 0; i < fields.length; ++i) {
		var f = fields[i];
		args.push(AjxStringUtil.urlEncode(f.name)
			  + "=" +
			  AjxStringUtil.urlEncode(f.value));
	}
	url = url + "?" + args.join("&");
	window.open(url, "_blank");
};
