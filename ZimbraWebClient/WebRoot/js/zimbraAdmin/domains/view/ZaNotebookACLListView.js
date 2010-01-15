/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2006, 2007, 2008, 2009, 2010 Zimbra, Inc.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.3 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 */
 
 /**
 * @author Greg Solovyev
 **/
ZaNotebookACLListView = function(parent, className, posStyle, headerList) {
	//var headerList = this._getHeaderList();
	ZaListView.call(this, parent, className, posStyle, headerList);
}

ZaNotebookACLListView.prototype = new ZaListView;
ZaNotebookACLListView.prototype.constructor = ZaNotebookACLListView;

ZaNotebookACLListView.prototype.toString = function() {
	return "ZaNotebookACLListView";
};

ZaNotebookACLListView.prototype._createItemHtml =
function(item) {
	var html = new Array(50);
	var	div = document.createElement("div");
	div[DwtListView._STYLE_CLASS] = "Row";
	div[DwtListView._SELECTED_STYLE_CLASS] = div[DwtListView._STYLE_CLASS] + "-" + DwtCssStyle.SELECTED;
	div.className = div[DwtListView._STYLE_CLASS];
	this.associateItemWithElement(item, div, DwtListView.TYPE_LIST_ITEM);
	
	var idx = 0;
	html[idx++] = "<table width='100%' cellspacing='0' cellpadding='0'>";

	html[idx++] = "<tr>";
	if(this._headerList) {
		var cnt = this._headerList.length;
		for(var i = 0; i < cnt; i++) {
			var field = this._headerList[i]._field;
			if(field == "gt") {
				// type
				html[idx++] = "<td align=left height=20px width=" + this._headerList[i]._width + "><nobr>";
				switch(item.gt) {
					case ZaDomain.A_NotebookPublicACLs:
						html[idx++] = ZaMsg.ACL_Public;
					break;
					case ZaDomain.A_NotebookAllACLs:
						html[idx++] = ZaMsg.ACL_All;
					break;		
					case ZaDomain.A_NotebookDomainACLs:
						html[idx++] = ZaMsg.ACL_Dom;
					break;		
					case ZaDomain.A_NotebookGroupACLs:
						html[idx++] = ZaMsg.ACL_Grp;
					break;		
					case ZaDomain.A_NotebookUserACLs:
						html[idx++] = ZaMsg.ACL_User;
					break;	
					case ZaDomain.A_NotebookGuestACLs:
						html[idx++] = ZaMsg.ACL_Guest;
					break;																				
				}
				
				html[idx++] = "</nobr></td>";
			} else if(field == "name") {
				// name
				html[idx++] = "<td align=left height=20px width=" + this._headerList[i]._width + "><nobr>";
				html[idx++] = item.name;
				html[idx++] = "</nobr></td>";
			} else if(field == "acl") {
				// name
				html[idx++] = "<td align=left height=20px width=" + this._headerList[i]._width + "><nobr>";
				var aclsList = [];
				for(var a in item.acl) {
					if(item.acl[a]==1) {
						aclsList.push(ZaDomain.ACLLabels[a]);
					}
				}
				html[idx++] = aclsList.join(",");
				html[idx++] = "</nobr></td>";
			} 	
		}
	} else {
		html[idx++] = "<td width=100%><nobr>";
		html[idx++] = AjxStringUtil.htmlEncode(item);
		html[idx++] = "</nobr></td>";
	}
	
	html[idx++] = "</tr></table>";
	div.innerHTML = html.join("");
	return div;
}


ZaNotebookACLListView.prototype._setNoResultsHtml = function() {
	var buffer = new AjxBuffer();
	var	div = document.createElement("div");
	
	buffer.append("<table width='100%' cellspacing='0' cellpadding='1'>",
				  "<tr><td class='NoResults'><br>&nbsp",
				  "</td></tr></table>");
	
	div.innerHTML = buffer.toString();
	this._addRow(div);
};

ZaNotebookACLListView.prototype._sortColumn = function (columnItem, bSortAsc){
	if (bSortAsc) {
		var comparator = function (a, b) {
			return (a < b)? 1 :((a > b)? -1 : 0);
		};
		this.getList().sort(comparator);
	} else {
		this.getList().sort();
	}
};
