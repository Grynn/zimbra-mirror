//-------------------------------------------------------------------------------------------------------
//List View for the zimbraDomainCOSMaxAccounts

ZaBulkProvisionAccountsListView = function(parent, className, posStyle, headerList) {
	if (arguments.length == 0) return;
	ZaListView.call(this, parent, className, posStyle, headerList);
	this.hideHeader = true;
    this._app = this.parent.parent._app ;

}

ZaBulkProvisionAccountsListView.prototype = new ZaListView;
ZaBulkProvisionAccountsListView.prototype.constructor = ZaBulkProvisionAccountsListView;

ZaBulkProvisionAccountsListView.prototype.toString = function() {
	return "ZaBulkProvisionAccountsListView";
};

ZaBulkProvisionAccountsListView.prototype.createHeaderHtml = function (defaultColumnSort) {
	if(!this.hideHeader) {
		DwtListView.prototype.createHeaderHtml.call(this,defaultColumnSort);
	}
}


ZaBulkProvisionAccountsListView.prototype._createItemHtml =
function(item) {
	var html = new Array(50);
	var	div = document.createElement("div");
	div[DwtListView._STYLE_CLASS] = "Row";
    div[DwtListView._SELECTED_STYLE_CLASS] = div[DwtListView._STYLE_CLASS] + "-" + DwtCssStyle.SELECTED;
	div.className = div[DwtListView._STYLE_CLASS];



    var id = this.associateItemWithElement(item, div, DwtListView.TYPE_LIST_ITEM);

    var idx = 0;
	html[idx++] = "<table width='100%' cellspacing='2' cellpadding='0'" ;
    if (item[ZaBulkProvision.A2_isValid] != "TRUE") {
        html[idx++] = " style='background: #ee1122;' ";
    }
    html[idx++] = ">";
    html[idx++] = "<tr>";

    var cnt = this._headerList.length;
    for(var i = 0; i < cnt; i++) {
        var field = this._headerList[i]._field;

        html[idx++] = "<td align='left' width=" + this._headerList[i]._width + "><nobr>";
        html[idx++] = AjxStringUtil.htmlEncode(item[field]);
        html[idx++] = "</nobr></td>";
    }

    html[idx++] = "</tr></table>";
	div.innerHTML = html.join("");
	return div;
}


ZaBulkProvisionAccountsListView.prototype._setNoResultsHtml = function() {
	var buffer = new AjxBuffer();
	var	div = document.createElement("div");

	buffer.append("<table width='100%' cellspacing='0' cellpadding='1'>",
				  "<tr><td class='NoResults'><br />",
                  com_zimbra_bulkprovision.no_accounts,
                  "</td></tr></table>");

	div.innerHTML = buffer.toString();
	this._addRow(div);
};