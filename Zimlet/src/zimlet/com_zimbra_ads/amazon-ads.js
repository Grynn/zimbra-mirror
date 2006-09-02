// Zimlet that displays ads from the Amazon Store
// @author Mihai Bazon

function Com_Zimbra_Ads() {};

Com_Zimbra_Ads.prototype = new ZmZimletBase;
Com_Zimbra_Ads.prototype.constructor = Com_Zimbra_Ads;

Com_Zimbra_Ads.prototype.init = function() {
        this.updateAds();
};

Com_Zimbra_Ads.prototype.menuItemSelected = function(itemId) {
	switch (itemId) {
	    case "PREFERENCES":
	        this.showPrefs();
	        break;
	}
};

Com_Zimbra_Ads.prototype.showPrefs = function() {
	if (!this._dlg_amazonPrefs) {
		var view = new DwtComposite(this.getShell());

		var ids = {};
		var xml = this.xmlObj();

		var html = [ "<table>",

			     "<tr><td style='text-align:right; font-weight:bold'>", xml.getProp("category").label, ":</td>",
			     "<td id='", ids.category = Dwt.getNextId(), "'></td></tr>",

			     "<tr><td style='text-align:right; font-weight:bold'>", xml.getProp("subcategory").label, ":</td>",
			     "<td id='", ids.subcat = Dwt.getNextId(), "'></td></tr>",

                             "<tr><td style='text-align:right; font-weight:bold'><input type='checkbox' id='",
                             ids.useKeywords = Dwt.getNextId(), "' /></td>",
                             "<td style='font-weight:bold'><label for='", ids.useKeywords, "'>",
                             xml.getProp("useKeywords").label, "</label></td></tr>",

                             "<tr><td></td><td><input disabled size='30' id='", ids.keywords = Dwt.getNextId(), "' /></td></tr>",

			     "</table>" ].join("");

                this._idUseKeywords = ids.useKeywords;
                this._idKeywords = ids.keywords;

		view.getHtmlElement().innerHTML = html;

                document.getElementById(ids.useKeywords).onclick =
                        AjxCallback.simpleClosure(this.updateWidgets, this, true);

		// create category drop-down

		var options = [];
		var current = this.getUserProperty("category");
		var subcat = null;
		for (var i in Com_Zimbra_Ads.AMAZON_CATEGORIES) {
			var o = Com_Zimbra_Ads.AMAZON_CATEGORIES[i];
			options.push(new DwtSelectOption(i, current == i, o.label));
			if (current == i)
				subcat = o.subcat;
		}
		options.sort(Com_Zimbra_Ads.dwtOptionSort);
		var sel = new DwtSelect(view, options);
		sel.reparentHtmlElement(ids.category);
		this._wCategory = sel;
		sel.addChangeListener(new AjxListener(this, this.updateSubcategories));

		// subcategory drop-down

		var options = [];
		var current = this.getUserProperty("subcategory");
		for (var i in subcat)
			options.push(new DwtSelectOption(i, current == i, subcat[i]));
		options.sort(Com_Zimbra_Ads.dwtOptionSort);
		var sel = new DwtSelect(view, options);
		sel.reparentHtmlElement(ids.subcat);
		this._wSubcategory = sel;

		var dialog_args = {
		        title : "Configure the Amazon Store Ads",
		        view  : view
		};
		var dlg = this._dlg_amazonPrefs = this._createDialog(dialog_args);
		dlg.setButtonListener(DwtDialog.OK_BUTTON, new AjxListener(this, this.savePrefs));

                this.updateWidgets();
	}
	this._dlg_amazonPrefs.popup();
};

Com_Zimbra_Ads.prototype.updateWidgets = function(useKwToggled) {
        var useK = document.getElementById(this._idUseKeywords);
        var kw = document.getElementById(this._idKeywords);
        if (!useKwToggled)
                useK.checked = /true/i.test(this.getUserProperty("useKeywords"));
        kw.disabled = !useK.checked;
        this._wSubcategory.setEnabled(kw.disabled);
        if (!useKwToggled)
                kw.value = this.getUserProperty("keywords");
};

Com_Zimbra_Ads.prototype.updateSubcategories = function() {
	var cat = this._wCategory.getValue();
	var sel = this._wSubcategory;
	sel.clearOptions();
	var o = Com_Zimbra_Ads.AMAZON_CATEGORIES[cat].subcat;
	var options = [];
	for (var i in o)
		options.push(new DwtSelectOption(i, /bestseller/i.test(o[i]), o[i]));
	options.sort(Com_Zimbra_Ads.dwtOptionSort);
	for (var i = 0; i < options.length; ++i)
		sel.addOption(options[i]);
};

Com_Zimbra_Ads.prototype.makeSidebarAmazonHTML = function(cat, subcat, useKeywords, keywords) {
        var html;
        if (!useKeywords || !keywords || !/\S/.test(keywords)) {
                html = this.getConfig("templateSidebar");
        } else {
                html = this.getConfig("templateSidebarK");
        }
        var x = this.xmlObj();
        var props = { acct        : this.getConfig("amazonAccount"),
                      category    : cat,
                      subcategory : subcat,
                      keywords    : keywords };
        return x.replaceObj(ZmZimletContext.RE_SCAN_PROP, html, props);
};

Com_Zimbra_Ads.prototype.makeTopAmazonHTML = function(cat, subcat, useKeywords, keywords) {
        var html;
        if (!useKeywords || !keywords || !/\S/.test(keywords)) {
                html = this.getConfig("templateTop");
        } else {
                html = this.getConfig("templateTopK");
        }
        var x = this.xmlObj();
        var props = { acct        : this.getConfig("amazonAccount"),
                      category    : cat,
                      subcategory : subcat,
                      keywords    : keywords };
        return x.replaceObj(ZmZimletContext.RE_SCAN_PROP, html, props);
};

Com_Zimbra_Ads.prototype.savePrefs = function() {
        this.setUserProperty("category", this._wCategory.getValue());
        this.setUserProperty("subcategory", this._wSubcategory.getValue());
        this.setUserProperty("useKeywords", document.getElementById(this._idUseKeywords).checked);
        this.setUserProperty("keywords", document.getElementById(this._idKeywords).value);
        this.saveUserProperties();
	this._dlg_amazonPrefs.popdown();
        this.updateAds();
};

Com_Zimbra_Ads.prototype.updateAds = function() {
        var args = [ this.getUserProperty("category"),
                     this.getUserProperty("subcategory"),
                     /true/i.test(this.getUserProperty("useKeywords")),
                     this.getUserProperty("keywords") ];

	skin.getSidebarAdContainer().innerHTML =
                this.makeSidebarAmazonHTML.apply(this, args);

        skin.getTopAdContainer().innerHTML =
                this.makeTopAmazonHTML.apply(this, args);
};

Com_Zimbra_Ads.prototype.singleClicked = Com_Zimbra_Ads.prototype.showPrefs;

Com_Zimbra_Ads.dwtOptionSort = function(a, b) {
	a = a._displayValue.toLowerCase();
	b = b._displayValue.toLowerCase();
	return a < b ? -1 : (a > b ? 1 : 0);
};
