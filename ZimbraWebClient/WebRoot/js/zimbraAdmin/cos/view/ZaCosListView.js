/**
* @constructor
* @class ZaCosListView
* @param parent
* @author Roland Schemers
* @author Greg Solovyev
**/

function ZaCosListView(parent) {
	var className = null;
	var posStyle = DwtControl.ABSOLUTE_STYLE;
	
	var headerList = this._getHeaderList();
	
	ZaListView.call(this, parent, className, posStyle, headerList);

	this._appCtxt = this.shell.getData(ZaAppCtxt.LABEL);
}

ZaCosListView.prototype = new ZaListView;
ZaCosListView.prototype.constructor = ZaCosListView;

ZaCosListView.prototype.toString = 
function() {
	return "ZaCosListView";
}

/**
* Renders a single item as a DIV element.
*/
ZaCosListView.prototype._createItemHtml =
function(cos, no, isDndIcon) {
	var html = new Array(50);
	var	div = this.getDocument().createElement("div");
	div._styleClass = "Row";
	div._selectedStyleClass = div._styleClass + "-" + DwtCssStyle.SELECTED;
	div.className = div._styleClass;
	this.associateItemWithElement(cos, div, DwtListView.TYPE_LIST_ITEM);
	
	var idx = 0;
	html[idx++] = "<table width='100%' cellspacing='2' cellpadding='0'>";
	html[idx++] = "<tr>";


	var cnt = this._headerList.length;
	for(var i = 0; i < cnt; i++) {
		var id = this._headerList[i]._id;
		if(id.indexOf(ZaCos.A_name)==0) {
		// name
			html[idx++] = "<td width=" + this._headerList[i]._width + ">";
			html[idx++] = AjxStringUtil.htmlEncode(cos.name);
			html[idx++] = "</td>";
			html[idx++] = "<td width=2></td>";	
		} else if (id.indexOf(ZaCos.A_description)==0) {
			// description
			html[idx++] = "<td width=" + this._headerList[i]._width + ">";
			html[idx++] = AjxStringUtil.htmlEncode(cos.attrs[ZaCos.A_description]);
			html[idx++] = "</td>";	
			html[idx++] = "<td width=2></td>";	
		}
	}
	html[idx++] = "</tr></table>";
	div.innerHTML = html.join("");
	return div;
}

ZaCosListView.prototype._getHeaderList =
function() {

	var headerList = new Array();
	//idPrefix, label, iconInfo, width, sortable, sortField, resizeable, visible
	headerList[0] = new ZaListHeaderItem(ZaCos.A_name, ZaMsg.CLV_Name_col, null, null, true, ZaCos.A_name, true, true);

	headerList[1] = new ZaListHeaderItem(ZaCos.A_description, ZaMsg.CLV_Description_col, null, null, false, null, true, true);
	
	return headerList;
}
