/*
 * ***** BEGIN LICENSE BLOCK *****
 * Version: ZPL 1.2
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.2 ("License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.zimbra.com/license
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See
 * the License for the specific language governing rights and limitations
 * under the License.
 * 
 * The Original Code is: Zimbra Collaboration Suite Web Client
 * 
 * The Initial Developer of the Original Code is Zimbra, Inc.
 * Portions created by Zimbra are Copyright (C) 2006 Zimbra, Inc.
 * All Rights Reserved.
 * 
 * Contributor(s):
 * 
 * ***** END LICENSE BLOCK *****
 */
 
 /**
 * @author Greg Solovyev
 **/
function ZaServerVolumesListView(parent, className, posStyle, headerList) {
	//var headerList = this._getHeaderList();
	ZaListView.call(this, parent, className, posStyle, headerList);
}

ZaServerVolumesListView.prototype = new ZaListView;
ZaServerVolumesListView.prototype.constructor = ZaServerVolumesListView;

ZaServerVolumesListView.prototype.toString = function() {
	return "ZaServerVolumesListView";
};

ZaServerVolumesListView.prototype._createItemHtml =
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
			var id = this._headerList[i]._id;
			if(id.indexOf(ZaServer.A_isCurrentVolume)==0) {
				html[idx++] = "<td align=left height=20px width=" + this._headerList[i]._width + ">";				
				var isCurrent=false;
				if(this.parent && this.parent.instance && this.parent.instance[ZaServer.A_CurrentMsgVolumeId]) {
					if(this.parent.instance[ZaServer.A_CurrentMsgVolumeId] == item[ZaServer.A_VolumeId]) {
						isCurrent=true;
					} 
				} 
				
				if (!isCurrent && this.parent && this.parent.instance && this.parent.instance[ZaServer.A_CurrentIndexVolumeId]) {
					if(this.parent.instance[ZaServer.A_CurrentIndexVolumeId] == item[ZaServer.A_VolumeId]) {
						isCurrent=true;						
					}	
				} 
				
				if(isCurrent)
					html[idx++] = AjxImg.getImageHtml("Check");
					
				html[idx++] = "</td>";
			} else if(id.indexOf(ZaServer.A_VolumeName)==0) {
				html[idx++] = "<td align=left height=20px width=" + this._headerList[i]._width + ">";
				html[idx++] = item[ZaServer.A_VolumeName];
				html[idx++] = "</td>";
			} else if(id.indexOf(ZaServer.A_VolumeRootPath)==0) {
				html[idx++] = "<td align=left height=20px width=" + this._headerList[i]._width + ">";
				html[idx++] = item[ZaServer.A_VolumeRootPath];
				html[idx++] = "</td>";
			} else if(id.indexOf(ZaServer.A_VolumeType)==0) {
				html[idx++] = "<td align=left height=20px width=" + this._headerList[i]._width + ">";
				html[idx++] = ZaServer.volumeTypeChoices.getChoiceByValue(item[ZaServer.A_VolumeType]);
				html[idx++] = "</td>";
			} else if(id.indexOf(ZaServer.A_VolumeCompressBlobs)==0) {
				html[idx++] = "<td align=left height=20px width=" + this._headerList[i]._width + ">";
				if(item[ZaServer.A_VolumeCompressBlobs])
					html[idx++] = ZaMsg.Yes;
				else
					html[idx++] = ZaMsg.No;
					
				html[idx++] = "</td>";
			} else if(id.indexOf(ZaServer.A_VolumeCompressionThreshold)==0) {
				html[idx++] = "<td align=left height=20px width=" + this._headerList[i]._width + ">";
				html[idx++] = AjxMessageFormat.format (ZaMsg.VM_VolumeCompressThresholdBytes, [item[ZaServer.A_VolumeCompressionThreshold]]);
				html[idx++] = "</td>";
			}
		}
	} else {
		html[idx++] = "<td width=100%>";
		html[idx++] = AjxStringUtil.htmlEncode(item);
		html[idx++] = "</td>";
	}
	
	html[idx++] = "</tr></table>";
	div.innerHTML = html.join("");
	return div;
}


ZaServerVolumesListView.prototype._setNoResultsHtml = function() {
	var buffer = new AjxBuffer();
	var	div = document.createElement("div");
	
	buffer.append("<table width='100%' cellspacing='0' cellpadding='1'>",
				  "<tr><td class='NoResults'><br>&nbsp",
				  "</td></tr></table>");
	
	div.innerHTML = buffer.toString();
	this._addRow(div);
};

ZaServerVolumesListView.prototype._sortColumn = function (columnItem, bSortAsc){
	if (bSortAsc) {
		var comparator = function (a, b) {
			return (a < b)? 1 :((a > b)? -1 : 0);
		};
		this.getList().sort(comparator);
	} else {
		this.getList().sort();
	}
};
