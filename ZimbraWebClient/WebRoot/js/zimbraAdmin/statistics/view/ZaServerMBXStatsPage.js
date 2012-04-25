/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2006, 2007, 2008, 2009, 2010, 2011 VMware, Inc.
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
* Display the Mailbox disk usage statistics per serer.
* 1) Top diskspace consumers
* 2) Top quota consumers
* 
* @class ZaServerMBXStatsPage
* @contructor ZaServerMBXStatsPage
* @param parent
* @param app
* @author Charles Cao
**/
		
ZaServerMBXStatsPage = function(parent) {
	DwtTabViewPage.call(this, parent);
    this.setScrollStyle(Dwt.SCROLL_Y);
	this._rendered = false;
	this._initialized = false ;
	this._hide = true ; //indicate that the Mbx Quota Tab is hidden
	this._prevSortBy = null ;
	this._prevAscending = null ;
}

ZaServerMBXStatsPage.prototype = new DwtTabViewPage;
ZaServerMBXStatsPage.prototype.constructor = ZaServerMBXStatsPage;

ZaServerMBXStatsPage.MBX_DISPLAY_LIMIT = ZaSettings.RESULTSPERPAGE; 
ZaServerMBXStatsPage.XFORM_ITEM_ACCOUNT = "account";
ZaServerMBXStatsPage.XFORM_ITEM_DISKUSAGE = "diskUsage";
ZaServerMBXStatsPage.XFORM_ITEM_QUOTAUSAGE = "quotaUsage";
ZaServerMBXStatsPage.XFORM_ITEM_QUOTA = "quota";
ZaServerMBXStatsPage.XFORM_ITEM_DISKMBX = "diskMbx";
ZaServerMBXStatsPage.XFORM_ITEM_QUOTAMBX = "quotaMbx";
ZaServerMBXStatsPage.TAB_KEY = 0;
ZaServerMBXStatsPage._offset = 0;
ZaServerMBXStatsPage._totalPage = 0;
ZaServerMBXStatsPage._currentPage = 0;

ZaServerMBXStatsPage.prototype.toString = function() {
	return "ZaServerMBXStatsPage";
};

ZaServerMBXStatsPage.prototype.setObject = function (item) {	
	this._server = item;
	DBG.println ("Set the new Server Name = " + this._server.name);
	this._render(item);
};

ZaServerMBXStatsPage.prototype._render = function (server) {
	if (!this._rendered) {		
		var modelData = {
			getMbxPool: function (model, instance) {
				return instance.mbxPool;
			},
			setMbxPool: function (value, instance, parentValue, ref) {
				instance.mbxPool = value;
			},
			items: [
				{id: "mbxPool", type:_LIST_, setter:"setMbxPool", setterScope:_MODEL_, 
											 getter: "getMbxPool", getterScope:_MODEL_}
			]		
		};
		var model = new XModel (modelData);
		var instance = new Array();
	    this._view = new XForm(this._getXForm(), model, instance, this);
		this._view.setController(this); 
		
		this._view.draw();
		this._rendered = true;
	} else{
		this.showMe(true); //always refresh when user click on the server list
	}
};

//data instance of the xform
ZaServerMBXStatsPage.prototype.getMbxes = function ( targetServer, offset, sortBy, sortAscending, callback){
	var result = { totalPage: 0, curPage:0, hasMore: false, mbxes: new Array() };
	var soapDoc = AjxSoapDoc.create("GetQuotaUsageRequest", ZaZimbraAdmin.URN, null);
	
	this._prevAscending = sortAscending ;
	this._prevSortBy = sortBy ;
	
	if (sortBy == null || sortBy == ZaServerMBXStatsPage.XFORM_ITEM_DISKUSAGE) {
		sortBy = "totalUsed" ;
	}else if (sortBy == ZaServerMBXStatsPage.XFORM_ITEM_QUOTAUSAGE){
		sortBy = "percentUsed" ;
	}else if (sortBy == ZaServerMBXStatsPage.XFORM_ITEM_QUOTA ){
		sortBy = "quotaLimit";
	}else if (sortBy == ZaServerMBXStatsPage.XFORM_ITEM_ACCOUNT ){
		sortBy = "account";
	} else {
		sortBy = "totalUsed";
	}	
	soapDoc.getMethod().setAttribute("sortBy", sortBy );
	if (sortAscending) {
		soapDoc.getMethod().setAttribute("sortAscending", sortAscending);
	}
	soapDoc.getMethod().setAttribute("offset", offset);
	soapDoc.getMethod().setAttribute("limit", ZaServerMBXStatsPage.MBX_DISPLAY_LIMIT);
	soapDoc.getMethod().setAttribute("refresh", "1");
	//use refresh="1" to force server side re-calculating quota and ignore cached data.

	var params = new Object ();
	params.soapDoc = soapDoc ;
	params.targetServer = targetServer ;
    var isAsyncMode = callback? true: false;
    if (isAsyncMode) {
        params.asyncMode = true;
        params.callback = callback;
    }
	var reqMgrParams = {
		controller : ZaApp.getInstance().getCurrentController(),
		busyMsg : ZaMsg.BUSY_GET_QUOTA
	}
	var resp = ZaRequestMgr.invoke(params, reqMgrParams);
    if (isAsyncMode) {
        return resp;
    } else {
        resp = resp.Body.GetQuotaUsageResponse;
    }
	
	if ((resp.account && resp.account.length > 0) && (resp.searchTotal && resp.searchTotal > 0)){	
		result.hasMore = resp.more ;
		var totalMbxes = resp.searchTotal;
		
		result.totalPage = parseInt (Math.ceil(totalMbxes / ZaServerMBXStatsPage.MBX_DISPLAY_LIMIT ));
		result.curPage = offset / ZaServerMBXStatsPage.MBX_DISPLAY_LIMIT + 1 ;
		
		var accounts = resp.account ;		
		var quotaLimit = 0;
		var percentage = 0 ;
		var diskUsed = 0;
		var _1MB = 1048576 ;
		var accountArr = new Array ();
		
		for (var i=0; i<accounts.length; i ++){
			diskUsed = ( accounts[i].used / _1MB ).toFixed(2) ;
			
			if (accounts[i].limit == 0 ){
				quotaLimit = ZaMsg.Unlimited;
				percentage = 0 ;	
			}else{			
				if (accounts[i].limit >= _1MB) {
					quotaLimit = ( accounts[i].limit / _1MB ).toFixed() ;						
				}else{ //quota limit is too small, we set it to 1MB. And it also avoid the NaN error when quotaLimit = 0
					quotaLimit = 1 ;
				}
				percentage = ((diskUsed * 100) / quotaLimit).toFixed() ;
			}
							    
			accountArr [i] = { 	account : accounts[i].name,
								diskUsage :  AjxMessageFormat.format (ZaMsg.MBXStats_DISK_MSB, [diskUsed]),
								quotaUsage : percentage + "\%" ,
								quota: quotaLimit + " MB"				 
								};
			//need to override the toString method, so when XForm does the element comparison, it will return the correct result
			//it is required when xform list needs to be update.
			accountArr[i].toString = function (){ return this.account ; };
		}
		
		result.mbxes = accountArr ;
	}	
	
	return result;	
};

ZaServerMBXStatsPage.prototype._getXForm = function () {
	if (this._xform != null) return this._xform;
	var sortable = 1;
	var sourceHeaderList = new Array();
	var defaultColumnSortable = 1;
											//idPrefix, label, 
											//iconInfo, width, sortable, sortField, resizeable, visible
	sourceHeaderList[0] = new ZaListHeaderItem(ZaServerMBXStatsPage.XFORM_ITEM_ACCOUNT, 	ZaMsg.MBXStats_ACCOUNT, 	
												null, 250, sortable++, ZaServerMBXStatsPage.XFORM_ITEM_ACCOUNT, true, true);
	sourceHeaderList[1] = new ZaListHeaderItem(ZaServerMBXStatsPage.XFORM_ITEM_QUOTA,   	ZaMsg.MBXStats_QUOTA,   	
												null, 120,  sortable++,  ZaServerMBXStatsPage.XFORM_ITEM_QUOTA, true, true);												
	defaultColumnSortable = sortable ;
	sourceHeaderList[2] = new ZaListHeaderItem(ZaServerMBXStatsPage.XFORM_ITEM_DISKUSAGE, 	ZaMsg.MBXStats_DISKUSAGE,	
												null, 200,  sortable++,  ZaServerMBXStatsPage.XFORM_ITEM_DISKUSAGE, true, true);
	sourceHeaderList[3] = new ZaListHeaderItem(ZaServerMBXStatsPage.XFORM_ITEM_QUOTAUSAGE,	ZaMsg.MBXStats_QUOTAUSAGE, 	
												null, "auto",  sortable++, ZaServerMBXStatsPage.XFORM_ITEM_QUOTAUSAGE, false, true);
	
	var ffTableStyle = "width:100%;overflow:visible;" ;
	var tableStyle = 	AjxEnv.isIE  ? ffTableStyle + "height:100%;" : ffTableStyle ;
	
	this._xform = {		
	    numCols:1, 
		tableCssStyle: tableStyle,
					
	    items:[
	      	//Convert to the listview
		   	{ref:"mbxPool", type:_DWT_LIST_, id: "mbxPool", width:"100%",  cssClass: "MBXList", defaultColumnSortable: defaultColumnSortable,
                getCustomWidth:ZaServerMBXStatsPage.getCustomWidth, getCustomHeight:ZaServerMBXStatsPage.getCustomHeight,
				forceUpdate: true, widgetClass:ZaServerMbxListView, headerList:sourceHeaderList}
		]	    
	};		   

	return this._xform;
};

ZaServerMBXStatsPage.getCustomWidth = function () {
    var page = this.getForm().parent;
    if (page._rendered) {
        var bounds = page.getBounds();
        return bounds.width;
    }
    return "100%"
}

ZaServerMBXStatsPage.getCustomHeight = function () {
    var page = this.getForm().parent;
    if (page._rendered) {
        var bounds = page.getBounds();
        return bounds.height;
    }
    return "100%"
}

//this function is called when user switch to the mbx quota tab
ZaServerMBXStatsPage.prototype.showMe = 
function (refresh){
	this.setZIndex(DwtTabView.Z_ACTIVE_TAB);

	if (this.parent.getHtmlElement().offsetHeight > 26) { 						// if parent visible, use offsetHeight
		this._contentEl.style.height=this.parent.getHtmlElement().offsetHeight-26;
	} else {
		var parentHeight = parseInt(this.parent.getHtmlElement().style.height);	// if parent not visible, resize page to fit parent
		var units = AjxStringUtil.getUnitsFromSizeString(this.parent.getHtmlElement().style.height);
		if (parentHeight > 26) {
			this._contentEl.style.height = (Number(parentHeight-26).toString() + units);
		}
	}

	this._contentEl.style.width = this.parent.getHtmlElement().style.width;	// resize page to fit parent

	var instance = null ;

	if ( !this._initialized || refresh) {
		//check whether the targetServer has the zimbra store enabled
		var serverAttrs = this._server.attrs ;
		var mbxesObj = {};
		if (serverAttrs && (!(serverAttrs[ZaServer.A_zimbraMailboxServiceInstalled] && serverAttrs[ZaServer.A_zimbraMailboxServiceEnabled]))){
			mbxesObj.mbxes = [] ;
		}else{
			//reserve the previous sort and ascending information. so the list hearder can display currectly
			//mbxesObj.mbxes = [] ;
			mbxesObj = this.getMbxes( this._server.id, 0 , this._prevSortBy, this._prevAscending) ;
		}
		
		instance = { 	serverid: this._server.id,
						offset:  0,	
						sortBy : this._prevSortBy , //reserve the previous sort and ascending information.
						sortAscending: this._prevAscending, //so the list hearder can display currectly
						curPage: mbxesObj.curPage ,
						totalPage: mbxesObj.totalPage,					
						mbxPool: mbxesObj.mbxes };
		
		
		var xform = this._view ;
		xform.setInstance( instance );

        if (!this._initialized) {
            var parentBounds = this.parent.getBounds();
            this.setSize(parentBounds.width, parentBounds.height);
        }

        this.mbxPoolWidget = xform.getItemById(xform.getId()+ "_mbxPool").getWidget();
        this.mbxPoolWidget.setScrollHasMore(mbxesObj.hasMore);
		this._initialized = true ;
	}else{
		instance = this._view.getInstance();
	}
	this.updateToolbar ( instance.curPage, instance.totalPage);	
	this._hide = false ;
};

ZaServerMBXStatsPage.prototype.hideMe = 
function (){
	DwtTabViewPage.prototype.hideMe.call(this);	
	this.updateToolbar(null, null, true);
	this._hide = true ;
};

//update the mbx list items based ont the offset which is changed when page back/forward
ZaServerMBXStatsPage.prototype.updateMbxLists =
function (curInstance, serverid, offset, sortBy, sortAscending) {
	if (curInstance) {
		if (serverid == null ) serverid = curInstance.serverid ;
		if (offset == null) offset = curInstance.offset;
		if (sortBy == null) sortBy = curInstance.sortBy ;
		if (sortAscending == null) sortAscending = curInstance.sortAscending ;	
	}
		
	var mbxesObj = this.getMbxes (serverid, offset, sortBy, sortAscending);
	
	curInstance = { 	serverid: serverid,
						offset:  offset,	
						sortBy : sortBy ,
						sortAscending: sortAscending,
						curPage: mbxesObj.curPage ,
						totalPage: mbxesObj.totalPage,					
						mbxPool: mbxesObj.mbxes };
	
	var xform = this._view ;
	xform.parent.updateToolbar(curInstance.curPage, curInstance.totalPage);
	xform.setInstance(curInstance) ;
    this.mbxPoolWidget.setScrollHasMore(mbxesObj.hasMore);
}; 

ZaServerMBXStatsPage.prototype.updateToolbar = 
function (curPage, totalPage, hide ){
	var controller = ZaApp.getInstance().getCurrentController();
	try {
		//enable the page back/forward button
		if ( controller instanceof ZaServerStatsController ){
			var toolBar = controller.getToolBar();			
			if (toolBar){
				if (! hide) {
					if (curPage > 1 ){ 
						toolBar.enable([ZaOperation.PAGE_BACK, ZaOperation.LABEL], true);
					} else {
						toolBar.enable([ZaOperation.PAGE_BACK], false);
					}
					
					if (curPage < totalPage  ){ 
						toolBar.enable([ZaOperation.PAGE_FORWARD, ZaOperation.LABEL], true);
					} else {
						toolBar.enable([ZaOperation.PAGE_FORWARD], false);
					}
					
					if (curPage && totalPage) {
						toolBar.getButton("PageInfo").setText(AjxMessageFormat.format (ZaMsg.MBXStats_PAGEINFO, [curPage, totalPage]));
					} 
					
					//update the help link for the Mbx Stats
					controller._helpURL = location.pathname + ZaUtil.HELP_URL + "managing_servers/viewing_mailbox_quotas.htm?locid="+AjxEnv.DEFAULT_LOCALE;
					controller._helpButtonText = ZaMsg.helpViewMailboxQuotas;
				}else {
					toolBar.enable([ZaOperation.PAGE_FORWARD, ZaOperation.PAGE_BACK, ZaOperation.LABEL], false);
					toolBar.getButton("PageInfo").setText(AjxMessageFormat.format (ZaMsg.MBXStats_PAGEINFO, [1,1]));
					//change the help link back
					controller._helpURL = location.pathname + ZaUtil.HELP_URL + "monitoring/checking_usage_statistics.htm?locid="+AjxEnv.DEFAULT_LOCALE;
					controller._helpButtonText = ZaMsg.helpCheckStatistics;
				}
			}
		}
	}catch (ex){
		controller._handleException (ex, "ZaServerMBXStatsPage.updateToolbar", null, false)
	}
};

///////////////////////////////////////////////////////////////////////////////////////////////////////
// This is the list view for the display of mbx accounts
///////////////////////////////////////////////////////////////////////////////////////////////////////

ZaServerMbxListView = function(parent, className, posStyle, headerList) {
	var posStyle = DwtControl.ABSOLUTE_STYLE;
	ZaListView.call(this, parent, className, posStyle, headerList, undefined, undefined, true);
    // For IE fix scroll everywhere issue;
    this.setLocation(0,0);
}

ZaServerMbxListView.prototype = new ZaListView;
ZaServerMbxListView.prototype.constructor = ZaServerMbxListView;

ZaServerMbxListView.prototype.toString = function() {
	return "ZaServerMbxListView";
};

ZaServerMbxListView.prototype._createItemHtml =
function(mbx, now, isDragProxy) {
	var html = new Array(50);
	var	div = document.createElement("div");
	div[DwtListView._STYLE_CLASS] = "Row";
	div[DwtListView._SELECTED_STYLE_CLASS] = div[DwtListView._STYLE_CLASS] + "-" + DwtCssStyle.SELECTED;
	div.className = div[DwtListView._STYLE_CLASS];
	this.associateItemWithElement(mbx, div, DwtListView.TYPE_LIST_ITEM);
	div.style.height = "20";
	
	var idx = 0;
	html[idx++] = "<table width='100%'  cellspacing='2' cellpadding='0'>";

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
			if(field == ZaServerMBXStatsPage.XFORM_ITEM_ACCOUNT) {
				// account
				html[idx++] = "<td width=" + this._headerList[i]._width + "><nobr>";
				html[idx++] = AjxStringUtil.htmlEncode(mbx[ZaServerMBXStatsPage.XFORM_ITEM_ACCOUNT]);				
				html[idx++] = "</nobr></td>";
			} else if (field == ZaServerMBXStatsPage.XFORM_ITEM_QUOTAUSAGE){ //this must before the QUOTA
				// quota usage
				html[idx++] = "<td width=" + this._headerList[i]._width + ">";
				//html[idx++] = AjxStringUtil.htmlEncode(mbx[ZaServerMBXStatsPage.XFORM_ITEM_QUOTAUSAGE]);
				//add the progress bar
				progressCssClass = "mbxprogressused";
				wholeCssClass = "mbxprogressbar" ;
				progressBar = new DwtProgressBar(this);
				percent = mbx[ZaServerMBXStatsPage.XFORM_ITEM_QUOTAUSAGE] ;
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
//				html[idx++] = "<div>Add the progress bar</div>";
				html[idx++] = "</td>";	
				progressBar.dispose ();		
			} else if(field == ZaServerMBXStatsPage.XFORM_ITEM_QUOTA) {
				// quota
				html[idx++] = "<td width=" + this._headerList[i]._width + ">";
				html[idx++] = AjxStringUtil.htmlEncode(mbx[ZaServerMBXStatsPage.XFORM_ITEM_QUOTA]);
				html[idx++] = "</td>";
			} else if (field == ZaServerMBXStatsPage.XFORM_ITEM_DISKUSAGE) {
				// mbx size
				html[idx++] = "<td width=" + this._headerList[i]._width + ">";
				html[idx++] = AjxStringUtil.htmlEncode(mbx[ZaServerMBXStatsPage.XFORM_ITEM_DISKUSAGE]);
				html[idx++] = "</td>";	
			} 
		}
	} else {
		html[idx++] = "<td width=100%><nobr>";
		html[idx++] = AjxStringUtil.htmlEncode(mbx[ZaServerMBXStatsPage.XFORM_ITEM_ACCOUNT]);
		html[idx++] = "</nobr></td>";
	}
	
	html[idx++] = "</tr></table>";
	div.innerHTML = html.join("");
	return div;
}


ZaServerMbxListView.prototype._setNoResultsHtml = function() {
	var buffer = new AjxBuffer();
	var	div = document.createElement("div");
	
	buffer.append("<table width='100%' cellspacing='0' cellpadding='1'>",
				  "<tr><td class='NoResults'>",
				  AjxStringUtil.htmlEncode(ZaMsg.MBXStats_NoMbx),
				  "</td></tr></table>");
	
	div.innerHTML = buffer.toString();
	this._addRow(div);
};

ZaServerMbxListView.prototype._sortColumn = function (columnItem, bSortAsc){
	var sortAscending = bSortAsc ? 1 : 0 ;
	var sortBy = columnItem._sortField ;
	var xform = this.parent ;
	var curInst = xform.getInstance();
	var mbxPage = xform.parent ;
	mbxPage.updateMbxLists(curInst, null, 0, sortBy, sortAscending );
};

ZaServerMbxListView.prototype._loadMsg = function(params) {
    var offset = params.offset;
    var instance = this.parent.getInstance();
    var server = instance.serverid;
    var sortBy = instance.sortBy;
    var sortAscending = instance.sortAscending;
    var limit = params.limit;
    var updateCallback = new AjxCallback(this, this.updateMoreItems);
    ZaServerMBXStatsPage.prototype.getMbxes.call(this.parent.parent, server, offset, sortBy, sortAscending, updateCallback);

}

ZaServerMbxListView.prototype.updateMoreItems = function(resp) {
    if (resp && !resp.isException()) {
        resp = resp.getResponse().Body.GetQuotaUsageResponse;

        if ((resp.account && resp.account.length > 0) && (resp.searchTotal && resp.searchTotal > 0)){
            var hasMore = resp.more ;
            var totalMbxes = resp.searchTotal;

            var accounts = resp.account ;
            var quotaLimit = 0;
            var percentage = 0 ;
            var diskUsed = 0;
            var _1MB = 1048576 ;
            var accountArr = new Array ();

            for (var i=0; i<accounts.length; i ++){
                diskUsed = ( accounts[i].used / _1MB ).toFixed(2) ;

                if (accounts[i].limit == 0 ){
                    quotaLimit = ZaMsg.Unlimited;
                    percentage = 0 ;
                }else{
                    if (accounts[i].limit >= _1MB) {
                        quotaLimit = ( accounts[i].limit / _1MB ).toFixed() ;
                    }else{ //quota limit is too small, we set it to 1MB. And it also avoid the NaN error when quotaLimit = 0
                        quotaLimit = 1 ;
                    }
                    percentage = ((diskUsed * 100) / quotaLimit).toFixed() ;
                }

                accountArr[i] = { 	account : accounts[i].name,
                                    diskUsage :  AjxMessageFormat.format (ZaMsg.MBXStats_DISK_MSB, [diskUsed]),
                                    quotaUsage : percentage + "\%" ,
                                    quota: quotaLimit + " MB"
                                    };
                //need to override the toString method, so when XForm does the element comparison, it will return the correct result
                //it is required when xform list needs to be update.
                accountArr[i].toString = function (){ return this.account ; };
            }
            this.replenish(AjxVector.fromArray(accountArr));
            this.setScrollHasMore(hasMore);
        }
    }
}

