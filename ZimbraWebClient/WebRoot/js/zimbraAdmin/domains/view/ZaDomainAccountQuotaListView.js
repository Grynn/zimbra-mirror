/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2004, 2005, 2006, 2007, 2008, 2009, 2010 Zimbra, Inc.
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
* @constructor
* @class ZaDomainAccountQuotaListView
* @param parent
* @author Ming Zhang
**/

ZaDomainAccountQuotaListView = function(parent) {

	var className = null;
	var posStyle = DwtControl.ABSOLUTE_STYLE;

	var headerList = this._getHeaderList();

	ZaListView.call(this, {
		parent:parent,
		className:className,
		posStyle:posStyle,
		headerList:headerList,
		id: ZaId.TAB_DOMAIN_MANAGE,
		scrollLoading:true
	});

    this.setLocation(0, 0);
	this._appCtxt = this.shell.getData(ZaAppCtxt.LABEL);
}

ZaDomainAccountQuotaListView.prototype = new ZaListView;
ZaDomainAccountQuotaListView.prototype.constructor = ZaDomainAccountQuotaListView;

ZaDomainAccountQuotaListView.prototype.toString =
function() {
	return "ZaDomainAccountQuotaListView";
}

/**
* Renders a single item as a DIV element.
*/
ZaDomainAccountQuotaListView.prototype._createItemHtml =
function(mbx, now, isDragProxy) {
	var html = new Array(50);
	var	div = document.createElement("div");
	div[DwtListView._STYLE_CLASS] = "Row";
	div[DwtListView._SELECTED_STYLE_CLASS] = div[DwtListView._STYLE_CLASS] + "-" + DwtCssStyle.SELECTED;
	div.className = div[DwtListView._STYLE_CLASS];
	this.associateItemWithElement(mbx, div, DwtListView.TYPE_LIST_ITEM);

	var idx = 0;
	html[idx++] = "<table width='100%'  cellspacing='0' cellpadding='0'>";

	html[idx++] = "<tr>";
	if(this._headerList) {
		var cnt = this._headerList.length;
		var progressBar = null ;
		var progressCssClass = null ;
		var wholeCssClass = null ;
		var percent = null ;
		var percentInt = null ;
		for(var i = 0; i < cnt; i++) {
			var field = this._headerList[i]._field;
			if(field == ZaAccountQuota.A2_name) {
				// account
				html[idx++] = "<td width=" + this._headerList[i]._width + "><nobr>";
				html[idx++] = AjxStringUtil.htmlEncode(mbx[ZaAccountQuota.A2_name]);
				html[idx++] = "</nobr></td>";
			} else if (field == ZaAccountQuota.A2_quotaUsage){ //this must before the QUOTA
				// quota usage
				html[idx++] = "<td width=" + this._headerList[i]._width + ">";
				progressCssClass = "mbxprogressused";
				wholeCssClass = "mbxprogressbar" ;
				progressBar = new DwtProgressBar(this);
				percent = mbx[ZaAccountQuota.A2_quotaUsage] ;
				percentInt = parseInt(percent) ;
				if ( percentInt > 85 ) {
					progressCssClass += "Critical" ;
				}else if (percentInt > 65 ) {
					progressCssClass += "Warning" ;
				}

				progressBar.setProgressCssClass(progressCssClass);
				progressBar.setWholeCssClass(wholeCssClass);
				progressBar.setLabel (percent, true) ;
				progressBar.setValueByPercent (percent);

				html[idx++] = progressBar.getHtmlElement().innerHTML	;
				html[idx++] = "</td>";
				progressBar.dispose ();
			} else if(field == ZaAccountQuota.A2_quota) {
				// quota
				html[idx++] = "<td width=" + this._headerList[i]._width + ">";
				html[idx++] = AjxStringUtil.htmlEncode(mbx[ZaAccountQuota.A2_quota]);
				html[idx++] = "</td>";
			} else if (field == ZaAccountQuota.A2_diskUsage) {
				// mbx size
				html[idx++] = "<td width=" + this._headerList[i]._width + ">";
				html[idx++] = AjxStringUtil.htmlEncode(mbx[ZaAccountQuota.A2_diskUsage]);
				html[idx++] = "</td>";
			}
		}
	} else {
		html[idx++] = "<td width=100%><nobr>";
		html[idx++] = AjxStringUtil.htmlEncode(mbx[ZaAccountQuota.A2_name]);
		html[idx++] = "</nobr></td>";
	}

	html[idx++] = "</tr></table>";
	div.innerHTML = html.join("");
	return div;
}


ZaDomainAccountQuotaListView.prototype._setNoResultsHtml = function() {
	var buffer = new AjxBuffer();
	var	div = document.createElement("div");

	buffer.append("<table width='100%' cellspacing='0' cellpadding='1'>",
				  "<tr><td class='NoResults'>",
				  AjxStringUtil.htmlEncode(ZaMsg.MBXStats_NoMbx),
				  "</td></tr></table>");

	div.innerHTML = buffer.toString();
	this._addRow(div);
};

ZaDomainAccountQuotaListView.prototype.setDomainName = function (domainName) {
    this._domainName = domainName;
}

ZaDomainAccountQuotaListView.prototype.setSortBy = function (sortBy) {
    this._sortBy = sortBy;
}

ZaDomainAccountQuotaListView.prototype.setSortAscending = function (sortAscending) {
    this._sortAscending = sortAscending;
}

ZaDomainAccountQuotaListView.prototype._getHeaderList =
function() {

	var headerList = new Array();
	var sortable = 1;
	var i = 0 ;

	headerList[i++] = new ZaListHeaderItem(ZaAccountQuota.A2_name, 	ZaMsg.MBXStats_ACCOUNT,
												null, 250, sortable++, ZaAccountQuota.A2_name, true, true);
	headerList[i++] = new ZaListHeaderItem(ZaAccountQuota.A2_diskUsage, ZaMsg.MBXStats_DISKUSAGE,
												null, 120,  sortable++,  ZaAccountQuota.A2_diskUsage,  true, true);
	headerList[i++] = new ZaListHeaderItem(ZaAccountQuota.A2_quotaUsage,  ZaMsg.MBXStats_QUOTAUSAGE,
												null, 200,  sortable++,  ZaAccountQuota.A2_quotaUsage, true, true);
	headerList[i++] = new ZaListHeaderItem(ZaAccountQuota.A2_quota,  ZaMsg.MBXStats_QUOTA,
												null, "auto",  sortable++, ZaAccountQuota.A2_quota, false, true);
	return headerList;
}

ZaDomainAccountQuotaListView.prototype._sortColumn = function (columnItem, bSortAsc){
	var sortAscending = bSortAsc ? 1 : 0 ;
	var sortBy = columnItem._sortField ;
	var xform = this.parent;
    var domainName = this._domainName ? this._domainName: "";
    var params = {};
    params.domainName = domainName;
    params.sortBy = sortBy;
    params.sortAscending =  sortAscending;
    var updateCallback = new AjxCallback(xform, ZaDomainXFormView.updateUserQuota, params);
    ZaDomain.getAccountQuota(domainName, 0, 50, sortBy, sortAscending, updateCallback);
};

ZaDomainAccountQuotaListView.prototype._loadMsg = function(params) {
    var offset = params.offset;
    var domainName = this._domainName ? this._domainName: "";
    var sortBy = this._sortBy ? this._sortBy:  null;
    var sortAscending = this._sortAscending ? this._sortAscending: null;
    var limit = params.limit;
    var updateCallback = new AjxCallback(this, this.updateMoreItems);
    ZaDomain.getAccountQuota(domainName, offset, limit, sortBy, sortAscending, updateCallback);
}

ZaDomainAccountQuotaListView.prototype.updateMoreItems = function(resp) {
    if (resp && !resp.isException()) {
        resp = resp.getResponse().Body.GetQuotaUsageResponse;
        if ((resp.account && resp.account.length > 0) && (resp.searchTotal && resp.searchTotal > 0)){
            var accounts = resp.account ;
            var accountArr = new Array ();

            for (var i=0; i<accounts.length; i ++){
                accountArr[i] = new ZaAccountQuota(accounts[i]);
            }

            this.replenish(AjxVector.fromArray(accountArr));
            this.setScrollHasMore(resp.more);
        }
    }
}

ZaAccountQuota = function (accountInfo) {
    this._init(accountInfo);
}

ZaAccountQuota.BytePerMB = 1048576;
ZaAccountQuota.prototype.constructor = ZaAccountQuota;
ZaAccountQuota.prototype.toString = function () {
    return this.name;
}

ZaAccountQuota.A2_name = "name";
ZaAccountQuota.A2_diskUsage = "diskUsage";
ZaAccountQuota.A2_quotaUsage = "quotaUsage";
ZaAccountQuota.A2_quota = "quota";
ZaAccountQuota.prototype._init = function (accountInfo) {
    var quotaLimit = 0;
    var percentage = 0 ;
    var diskUsed = 0;

    diskUsed = ( accountInfo.used / ZaAccountQuota.BytePerMB ).toFixed(2) ;

    if (accountInfo.limit == 0 ){
        quotaLimit = ZaMsg.Unlimited;
        percentage = 0 ;
    }else{
        if (accountInfo.limit >= ZaAccountQuota.BytePerMB) {
            quotaLimit = ( accountInfo.limit / ZaAccountQuota.BytePerMB ).toFixed() ;
        }else{ //quota limit is too small, we set it to 1MB. And it also avoid the NaN error when quotaLimit = 0
            quotaLimit = 1 ;
        }
        percentage = ((diskUsed * 100) / quotaLimit).toFixed() ;
    }

    this.name =  accountInfo.name;
    this.diskUsage = AjxMessageFormat.format (ZaMsg.MBXStats_DISK_MSB, [diskUsed]);
    this.quotaUsage = percentage + "\%" ;
    this.quota = quotaLimit + " MB";
}

