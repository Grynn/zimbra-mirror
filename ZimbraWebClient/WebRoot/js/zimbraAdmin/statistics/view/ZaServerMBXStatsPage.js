/*
 * ***** BEGIN LICENSE BLOCK *****
 * Version: ZPL 1.1
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.1 ("License"); you may not use this file except in
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
 * Portions created by Zimbra are Copyright (C) 2005 Zimbra, Inc.
 * All Rights Reserved.
 * 
 * Contributor(s):
 * 
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
		
function ZaServerMBXStatsPage (parent, app) {
	DwtTabViewPage.call(this, parent);
	this._app = app;
	this._rendered = false;
	this._initialized = false ;
}

ZaServerMBXStatsPage.prototype = new DwtTabViewPage;
ZaServerMBXStatsPage.prototype.constructor = ZaServerMBXStatsPage;

ZaServerMBXStatsPage.MBX_DISPLAY_LIMIT = 20; //default 20
ZaServerMBXStatsPage.XFORM_ITEM_ACCOUNT = "account";
ZaServerMBXStatsPage.XFORM_ITEM_DISKUSAGE = "diskUsage";
ZaServerMBXStatsPage.XFORM_ITEM_QUOTAUSAGE = "quotaUsage";
ZaServerMBXStatsPage.XFORM_ITEM_QUOTA = "quota";
ZaServerMBXStatsPage.XFORM_ITEM_DISKMBX = "diskMbx";
ZaServerMBXStatsPage.XFORM_ITEM_QUOTAMBX = "quotaMbx";

ZaServerMBXStatsPage._offset = 0;
ZaServerMBXStatsPage._totalPage = 0;
ZaServerMBXStatsPage._currentPage = 0;

ZaServerMBXStatsPage.prototype.toString = function() {
	return "ZaServerMBXStatsPage";
};

ZaServerMBXStatsPage.prototype.setObject = function (item) {
	this._server = item;
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
	} 
};

//data instance of the xform
ZaServerMBXStatsPage.getMbxes = function ( targetServer, offset, sortBy, sortAscending  ){
	var result = { totalPage: 0, curPage:0, mbxes: new Array() };
	var soapDoc = AjxSoapDoc.create("GetQuotaUsageRequest", "urn:zimbraAdmin", null);
	
	if (sortBy == null || sortBy == ZaServerMBXStatsPage.XFORM_ITEM_DISKUSAGE) {
		sortBy = "totalUsed" ;
	}else if (sortBy == ZaServerMBXStatsPage.XFORM_ITEM_QUOTAUSAGE){
		sortBy = "percentUsed" ;
	}else if (sortBy == ZaServerMBXStatsPage.XFORM_ITEM_QUOTA ){
		sortBy = "quotaLimit";
	}else {
		sortBy = "totalUsed";
	}	
	soapDoc.getMethod().setAttribute("sortBy", sortBy );
	if (sortAscending) {
		soapDoc.getMethod().setAttribute("sortAscending", sortAscending);
	}
	soapDoc.getMethod().setAttribute("offset", offset);
	soapDoc.getMethod().setAttribute("limit", ZaServerMBXStatsPage.MBX_DISPLAY_LIMIT);
	
	var resp = ZmCsfeCommand.invoke(soapDoc, null, null, targetServer, true).firstChild; 
	var more = resp.getAttribute("more");
	var totalMbxes = resp.getAttribute("searchTotal") ;
	result.totalPage = parseInt (Math.ceil(totalMbxes / ZaServerMBXStatsPage.MBX_DISPLAY_LIMIT ));
	result.curPage = offset / ZaServerMBXStatsPage.MBX_DISPLAY_LIMIT + 1 ;
	
	var nodes = resp.childNodes ;
	var accountArr = new Array ();
	var respArr = new Array ();
	var quotaLimit = 0;
	var percentage = 0 ;
	var diskUsed = 0;

	for (var i=0; i<nodes.length; i ++){
		respArr [i] = {  	name: nodes[i].getAttribute("name") ,
							used: nodes[i].getAttribute("used") ,
							limit: nodes[i].getAttribute("limit") 
						    };	
		
		diskUsed = ( respArr[i].used / 1048576 ).toFixed(2) ;
		
		if (respArr[i].limit == 0 ){
			quotaLimit = "unlimited" ;
			percentage = 0 ;	
		}else{
			quotaLimit = ( respArr[i].limit / 1048576 ).toFixed() ;
			percentage = ((diskUsed * 100) / quotaLimit).toFixed() ;
		}
						    
		accountArr [i] = { 	account : respArr[i].name,
							diskUsage :  AjxMessageFormat.format (ZaMsg.MBXStats_DISK_MSB, [diskUsed]),
							quotaUsage : percentage + "\%" ,
							quota: quotaLimit + " MB"				 
							};
		//need to override the toString method, so when XForm does the element comparison, it will return the correct result
		//it is required when xform list needs to be update.
		accountArr[i].toString = function (){ return this.account ; };
	}
	result.mbxes = accountArr ;
	
	return result;	
};

ZaServerMBXStatsPage.prototype._getXForm = function () {
	if (this._xform != null) return this._xform;
	
	var sourceHeaderList = new Array();
											//idPrefix, label, 
											//iconInfo, width, sortable, sortField, resizeable, visible
	sourceHeaderList[0] = new ZaListHeaderItem(ZaServerMBXStatsPage.XFORM_ITEM_ACCOUNT, 	ZaMsg.MBXStats_ACCOUNT, 	
												null, 300, false, null, true, true);
	sourceHeaderList[1] = new ZaListHeaderItem(ZaServerMBXStatsPage.XFORM_ITEM_QUOTA,   	ZaMsg.MBXStats_QUOTA,   	
												null, 120,  true,  ZaServerMBXStatsPage.XFORM_ITEM_QUOTA, true, true);
	sourceHeaderList[2] = new ZaListHeaderItem(ZaServerMBXStatsPage.XFORM_ITEM_DISKUSAGE, 	ZaMsg.MBXStats_DISKUSAGE,	
												null, 120,  true,  ZaServerMBXStatsPage.XFORM_ITEM_DISKUSAGE, true, true);
	sourceHeaderList[3] = new ZaListHeaderItem(ZaServerMBXStatsPage.XFORM_ITEM_QUOTAUSAGE,	ZaMsg.MBXStats_QUOTAUSAGE, 	
												null, null,  true, ZaServerMBXStatsPage.XFORM_ITEM_QUOTAUSAGE, true, true);
		
	this._xform = {
		x_showBorder:1,
	    numCols:1, 
	    tableCssStyle:"width:100%;overflow:auto;",
						
	    itemDefaults:{ },
	    items:[
	    	//Convert to the listview
	    	{ref:"mbxPool", type:_DWT_LIST_, height:"600", width:"100%", cssClass: "MBXList", 	    	
						   		forceUpdate: true, widgetClass:ZaServerMbxListView, headerList:sourceHeaderList}
		]	    
	};		   

	return this._xform;
};

//this function is called when user switch to the mbx quota tab
ZaServerMBXStatsPage.prototype.showMe = 
function (){
	DwtTabViewPage.prototype.showMe.call(this);
	var instance = null ;

	if ( !this._initialized ) {
		var mbxesObj = ZaServerMBXStatsPage.getMbxes( this._server.id, 0 ) ;
		instance = { 	serverid: this._server.id,
						offset:  0,	
						sortBy : null ,
						sortAscending: null,
						curPage: mbxesObj.curPage ,
						totalPage: mbxesObj.totalPage,					
						mbxPool: mbxesObj.mbxes };
		
		var xform = this._view ;
		xform.setInstance( instance );
		this._initialized = true ;
	}else{
		instance = this._view.getInstance();
	}
	this.updateToolbar ( instance.curPage, instance.totalPage);	
};

ZaServerMBXStatsPage.prototype.hideMe = 
function (){
	DwtTabViewPage.prototype.hideMe.call(this);	
	this.updateToolbar(null, null, true);
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
	
	var mbxesObj = ZaServerMBXStatsPage.getMbxes (serverid, offset, sortBy, sortAscending);
	
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
}; 

ZaServerMBXStatsPage.prototype.updateToolbar = 
function (curPage, totalPage, hide ){
	var controller = this._app.getCurrentController();
	try {
		//enable the page back/forward button
		if ( controller instanceof ZaServerStatsController ){
			var toolBar = controller.getToolbar();			
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
						toolBar.getButton("mbxPageInfo").setText(AjxMessageFormat.format (ZaMsg.MBXStats_PAGEINFO, [curPage, totalPage]));
					} 
				}else {
					toolBar.enable([ZaOperation.PAGE_FORWARD, ZaOperation.PAGE_BACK, ZaOperation.LABEL], false);
					toolBar.getButton("mbxPageInfo").setText(AjxMessageFormat.format (ZaMsg.MBXStats_PAGEINFO, [1,1]));
				}
			}
		}
	}catch (ex){
	
	}
};

///////////////////////////////////////////////////////////////////////////////////////////////////////
// This is the list view for the display of mbx accounts
///////////////////////////////////////////////////////////////////////////////////////////////////////

function ZaServerMbxListView(parent, className, posStyle, headerList) {
	ZaListView.call(this, parent, className, posStyle, headerList);
}

ZaServerMbxListView.prototype = new ZaListView;
ZaServerMbxListView.prototype.constructor = ZaServerMbxListView;

ZaServerMbxListView.prototype.toString = function() {
	return "ZaServerMbxListView";
};

ZaServerMbxListView.prototype._createItemHtml =
function(mbx, now, isDndIcon) {
	var html = new Array(50);
	var	div = document.createElement("div");
	div._styleClass = "Row";
	div._selectedStyleClass = div._styleClass + "-" + DwtCssStyle.SELECTED;
	div.className = div._styleClass;
	this.associateItemWithElement(mbx, div, DwtListView.TYPE_LIST_ITEM);
	
	var idx = 0;
	html[idx++] = "<table width='100%' cellspacing='2' cellpadding='0'>";

	html[idx++] = "<tr>";
	if(this._headerList) {
		var cnt = this._headerList.length;
		for(var i = 0; i < cnt; i++) {
			var id = this._headerList[i]._id;
			if(id.indexOf(ZaServerMBXStatsPage.XFORM_ITEM_ACCOUNT) == 0) {
				// account
				html[idx++] = "<td width=" + this._headerList[i]._width + ">";
				html[idx++] = AjxStringUtil.htmlEncode(mbx[ZaServerMBXStatsPage.XFORM_ITEM_ACCOUNT]);				
				html[idx++] = "</td>";
			} else if (id.indexOf(ZaServerMBXStatsPage.XFORM_ITEM_QUOTAUSAGE) == 0){ //this must before the QUOTA
				// quota usage
				html[idx++] = "<td width=" + this._headerList[i]._width + ">";
				html[idx++] = AjxStringUtil.htmlEncode(mbx[ZaServerMBXStatsPage.XFORM_ITEM_QUOTAUSAGE]);
				html[idx++] = "</td>";			
			} else if(id.indexOf(ZaServerMBXStatsPage.XFORM_ITEM_QUOTA) == 0) {
				// quota
				html[idx++] = "<td width=" + this._headerList[i]._width + ">";
				html[idx++] = AjxStringUtil.htmlEncode(mbx[ZaServerMBXStatsPage.XFORM_ITEM_QUOTA]);
				html[idx++] = "</td>";
			} else if (id.indexOf(ZaServerMBXStatsPage.XFORM_ITEM_DISKUSAGE) == 0) {
				// mbx size
				html[idx++] = "<td width=" + this._headerList[i]._width + ">";
				html[idx++] = AjxStringUtil.htmlEncode(mbx[ZaServerMBXStatsPage.XFORM_ITEM_DISKUSAGE]);
				html[idx++] = "</td>";	
			} 
		}
	} else {
		html[idx++] = "<td width=100%>";
		html[idx++] = AjxStringUtil.htmlEncode(mbx[ZaServerMBXStatsPage.XFORM_ITEM_ACCOUNT]);
		html[idx++] = "</td>";
	}
	
	html[idx++] = "</tr></table>";
	div.innerHTML = html.join("");
	return div;
}


ZaServerMbxListView.prototype._setNoResultsHtml = function() {
	var buffer = new AjxBuffer();
	var	div = document.createElement("div");
	
	buffer.append("<table width='100%' cellspacing='0' cellpadding='1'>",
				  "<tr><td class='NoResults'><br>&nbsp",
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

