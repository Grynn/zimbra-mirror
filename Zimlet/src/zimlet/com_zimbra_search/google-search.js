function Com_Zimbra_Search_Google(zimlet) {
	this.zimlet = zimlet;
	this.icon = "google";
	this.label = "Search Google";
};

Com_Zimbra_Search_Google.prototype.getSearchFormHTML =
function(query) {
	var zimlet = this.zimlet;
	var props = {
		acct  : zimlet.getConfig("google-account"),
		query : query
	};
	var code = zimlet.getConfig("google-search-code");
	code = zimlet.xmlObj().replaceObj(ZmZimletContext.RE_SCAN_PROP, code, props);
	return code;
};

Com_Zimbra_Search.registerHandler(Com_Zimbra_Search_Google);
