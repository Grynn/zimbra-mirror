/**
* The list view with a check box at the leftmost
*
* Originally planned for the admin roles, however, it may cause the
* thousands of roles issue. We use _REPEAT_ with _DYNSELECT_ items
        **/



ZaCheckBoxListView = function(parent, className, posStyle, headerList) {
    if (arguments.length == 0) return;
	var className = className || null;
	var posStyle = posStyle || DwtControl.STATIC_STYLE;
	var headerList = headerList || ZaCheckBoxListView._getHeaderList();

	ZaListView.call(this, parent, className, posStyle, headerList);
	this._appCtxt = this.shell.getData(ZaAppCtxt.LABEL);
}

ZaCheckBoxListView.prototype = new ZaListView;
ZaCheckBoxListView.prototype.constructor = ZaCheckBoxListView;

ZaCheckBoxListView.prototype.toString =
function() {
	return "ZaCheckBoxListView";
}

ZaCheckBoxListView.prototype._createItemHtml =
function(item, now, isDragProxy) {
	var html = new Array(50);
	var	div = document.createElement("div");
	div[DwtListView._STYLE_CLASS] = "Row";
	div[DwtListView._SELECTED_STYLE_CLASS] = div[DwtListView._STYLE_CLASS] + "-" + DwtCssStyle.SELECTED;
	div.className = div[DwtListView._STYLE_CLASS];
	this.associateItemWithElement(item, div, DwtListView.TYPE_LIST_ITEM);

	var idx = 0;
	html[idx++] = "<table width='100%' cellspacing='2' cellpadding='0'>";

    //add the checkboxes
    html[idx++] = "<tr align='left' width=20><td>" ;
    html[idx++] = "<input type='checkbox' />"  ;
    html[idx++] = "</td></tr>" ;

    //end of the checkboxes

    html[idx++] = "<tr>";
	var cnt = this._headerList.length;
	for(var i = 0; i < cnt; i++) {
		var field = this._headerList[i]._field;

        html[idx++] = "<td align='left' width=" + this._headerList[i]._width + "><nobr>";
        if (field == "admin_role_name") {
            html[idx++] = AjxStringUtil.htmlEncode(item) ;
        }else if (field == ZaAccount.A_description)   {
            html[idx++] = AjxStringUtil.htmlEncode(item) ;
        }
        html[idx++] = "</nobr></td>" ;
	}
	html[idx++] = "</tr></table>";
	div.innerHTML = html.join("");
	return div;
}

ZaCheckBoxListView._getHeaderList =
function() {
	var headerList = [];
//idPrefix, label, iconInfo, width, sortable, sortField, resizeable, visible
//	var sortable=1;
	headerList[0] = new ZaListHeaderItem("admin_role_name", com_zimbra_delegatedadmin.Col_comp_name,
            null, 200, null, "admin_role_name", true, true);
	headerList[1] = new ZaListHeaderItem(ZaAccount.A_description, ZaMsg.ALV_Description_col,
            null, null, null  , ZaAccount.A_description, true, true);

    return headerList;
}

ZaCheckBoxListView.onSelectionListener = function () {

    //TODO update the checkbox state
    var instance = this.getForm().getInstance () ;
    var selectedComps = this.widget.getSelection () ;
    this.getModel().setInstanceValue (instance,
           "ZaCheckBoxList_Selected_Items", selectedComps) ;
}