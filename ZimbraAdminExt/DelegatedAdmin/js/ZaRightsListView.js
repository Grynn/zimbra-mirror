

ZaRightsListView = function(parent) {
    if (arguments.length == 0) return;
	var className = null;
	var posStyle = DwtControl.ABSOLUTE_STYLE;
	var headerList = ZaRightsListView._getHeaderList();

	ZaListView.call(this, parent, className, posStyle, headerList);

	this._appCtxt = this.shell.getData(ZaAppCtxt.LABEL);
}

ZaRightsListView.prototype = new ZaListView;
ZaRightsListView.prototype.constructor = ZaRightsListView;

ZaRightsListView.prototype.toString =
function() {
	return "ZaRightsListView";
}

ZaRightsListView.prototype.getTitle =
function () {
	return com_zimbra_delegatedadmin.manage_rights_title ;
}

ZaRightsListView.prototype.getTabIcon =
function () {
	return "Server";
}

ZaRightsListView.prototype._createItemHtml =
function(right, now, isDragProxy) {
	var html = new Array(50);
	var	div = document.createElement("div");
	div[DwtListView._STYLE_CLASS] = "Row";
	div[DwtListView._SELECTED_STYLE_CLASS] = div[DwtListView._STYLE_CLASS] + "-" + DwtCssStyle.SELECTED;
	div.className = div[DwtListView._STYLE_CLASS];
	this.associateItemWithElement(right, div, DwtListView.TYPE_LIST_ITEM);

	var idx = 0;
	html[idx++] = "<table width='100%' cellspacing='2' cellpadding='0'>";
	html[idx++] = "<tr>";
	var cnt = this._headerList.length;
	for(var i = 0; i < cnt; i++) {
		var field = this._headerList[i]._field;

        html[idx++] = "<td align='left' width=" + this._headerList[i]._width + "><nobr>";
        html[idx++] = AjxStringUtil.htmlEncode(right.attrs [field]) ;
        html[idx++] = "</nobr></td>" ;
	}
	html[idx++] = "</tr></table>";
	div.innerHTML = html.join("");
	return div;
}

ZaRightsListView._getHeaderList =
function() {

	var headerList = [];
//idPrefix, label, iconInfo, width, sortable, sortField, resizeable, visible
	var sortable=1;
	headerList[0] = new ZaListHeaderItem(ZaRight.A_name, com_zimbra_delegatedadmin.Col_right_name,
            null, 200, sortable++, ZaRight.A_name, true, true);

	headerList[1] = new ZaListHeaderItem(ZaRight.A_type, com_zimbra_delegatedadmin.Col_right_type,
            null, 100, sortable ++  , ZaRight.A_type, true, true);

    headerList[2] = new ZaListHeaderItem(ZaRight.A_desc, com_zimbra_delegatedadmin.Col_right_desc,
                null, null, null , ZaRight.A_desc, true, true);

    return headerList;
}


//ZaRightsMiniListView only show the name of the rights
ZaRightsMiniListView = function(parent) {
    if (arguments.length == 0) return;
	var className = null;
	var posStyle = null;
	var headerList = ZaRightsMiniListView._getHeaderList();
    ZaListView.call(this, parent, className, posStyle, headerList);

	this._appCtxt = this.shell.getData(ZaAppCtxt.LABEL);
}

ZaRightsMiniListView.prototype = new ZaRightsListView;
ZaRightsMiniListView.prototype.constructor = ZaRightsMiniListView;

ZaRightsMiniListView.prototype.toString =
function() {
	return "ZaRightsMiniListView";
}

ZaRightsMiniListView._getHeaderList =
function() {

	var headerList = [];
//idPrefix, label, iconInfo, width, sortable, sortField, resizeable, visible
//	var sortable=1;
	headerList[0] = new ZaListHeaderItem(ZaRight.A_name, com_zimbra_delegatedadmin.Col_right_name,
            null, 200, null, null, true, true);

    return headerList;
}

ZaRightsMiniListView.prototype._createItemHtml =
function(right) {
	var html = new Array(50);
	var	div = document.createElement("div");
	div[DwtListView._STYLE_CLASS] = "Row";
	div[DwtListView._SELECTED_STYLE_CLASS] = div[DwtListView._STYLE_CLASS] + "-" + DwtCssStyle.SELECTED;
	div.className = div[DwtListView._STYLE_CLASS];
	this.associateItemWithElement(right, div, DwtListView.TYPE_LIST_ITEM);

	var idx = 0;
	html[idx++] = "<table width='100%' cellspacing='2' cellpadding='0'>";
	html[idx++] = "<tr>";

    html[idx++] = "<td align='left'><nobr>";
//        html[idx++] = AjxStringUtil.htmlEncode(right.attrs [field]) ;
    html[idx++] = AjxStringUtil.htmlEncode(right) ;
    html[idx++] = "</nobr></td>" ;

	html[idx++] = "</tr></table>";
	div.innerHTML = html.join("");
	return div;
}


//ZaRightsAttrsListView only show the name of the rights
ZaRightsAttrsListView = function(parent) {
    if (arguments.length == 0) return;
	var className = null;
	var posStyle = null;
	var headerList = ZaRightsAttrsListView._getHeaderList();
	ZaListView.call(this, parent, className, posStyle, headerList);

	this._appCtxt = this.shell.getData(ZaAppCtxt.LABEL);
}

ZaRightsAttrsListView.prototype = new ZaRightsListView;
ZaRightsAttrsListView.prototype.constructor = ZaRightsAttrsListView;

ZaRightsAttrsListView.prototype.toString =
function() {
	return "ZaRightsAttrsListView";
}

ZaRightsAttrsListView._getHeaderList =
function() {

	var headerList = [];
//idPrefix, label, iconInfo, width, sortable, sortField, resizeable, visible
//	var sortable=1;
	headerList[0] = new ZaListHeaderItem(ZaRight.A_attrs, com_zimbra_delegatedadmin.Col_right_attr_name,
            null, 200, null, null, true, true);

    return headerList;
}


