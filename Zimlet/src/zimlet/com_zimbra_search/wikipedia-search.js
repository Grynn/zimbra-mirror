function Com_Zimbra_Search_Wikipedia(zimlet) {
	this.zimlet = zimlet;
	this.icon = "Wikipedia-Icon";
	this.label = "Search Wikipedia";
};

Com_Zimbra_Search_Wikipedia.prototype.getSearchFormHTML = function(query) {
	var zimlet = this.zimlet;
	var code = zimlet.getConfig("wikipedia-search-code");
	code = zimlet.xmlObj().replaceObj(ZmZimletContext.RE_SCAN_PROP, code, { query: query });
	return code;
};

Com_Zimbra_Search.registerHandler(Com_Zimbra_Search_Wikipedia);
