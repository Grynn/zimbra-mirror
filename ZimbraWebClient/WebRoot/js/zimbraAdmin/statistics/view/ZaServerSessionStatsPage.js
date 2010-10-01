
/**
 * This page displays the current server's session statistics.
 * 
 * @author Charles Cao
 */
ZaServerSessionStatsPage = function(parent) {
	DwtTabViewPage.call(this, parent);

	//The response objects
	this._adminSessResp = {} ;
	this._imapSessResp = {} ;
	this._soapSessResp = {} ;
	
	this._offset = {
		"soap": 0,
		"admin" : 0,
		"imap" : 0
	} ; //to record the offset value of the current request
	
	this._sortBy = {
		"soap" : "nameAsc",
		"admin" : "nameAsc",
		"imap" : "nameAsc"
	} ;	
	
	this._pageObj = { } ;
	this._pageObj["soap"] = { curPage: 1 , 	totalPage: 1 };
	this._pageObj["admin"] = { curPage: 1 , 	totalPage: 1 };
	this._pageObj["imap"] = { curPage: 1 , 	totalPage: 1 };
			
	this._rendered = false ;
}

ZaServerSessionStatsPage.PAGE_LIMIT = 25;

ZaServerSessionStatsPage.prototype = new DwtTabViewPage;
ZaServerSessionStatsPage.prototype.constructor = ZaServerSessionStatsPage;

ZaServerSessionStatsPage.prototype.setObject =
function (currentServer) {
	this._server = currentServer ;
}

ZaServerSessionStatsPage.prototype.setSortBy =
function (sortBy) {
	var instance =	this._localXForm.getInstance () ;
	var currentTabId = instance[ZaModel.currentTab] ;
	
	if (currentTabId == ZaServerSessionStatsPage.SOAP_TAB_ID) {
		this._sortBy["soap"] = sortBy;
	}else if (currentTabId == ZaServerSessionStatsPage.ADMIN_TAB_ID) {
		this._sortBy["admin"] = sortBy;
	}else if (currentTabId == ZaServerSessionStatsPage.IMAP_TAB_ID) {
		this._sortBy["imap"] = sortBy ;
	}
}

ZaServerSessionStatsPage.prototype.setOffset =
function (offset) {
	var instance =	this._localXForm.getInstance () ;
	var currentTabId = instance[ZaModel.currentTab] ;
	
	if (currentTabId == ZaServerSessionStatsPage.SOAP_TAB_ID) {
		this._offset["soap"] = offset;
	}else if (currentTabId == ZaServerSessionStatsPage.ADMIN_TAB_ID) {
		this._offset["admin"] = offset;
	}else if (currentTabId == ZaServerSessionStatsPage.IMAP_TAB_ID) {
		this._offset["imap"] = offset ;
	}
}

ZaServerSessionStatsPage.prototype._createHtml =
function () {
	//if(window.console && window.console.log) console.debug("Create the session stats page") ;
	DwtTabViewPage.prototype._createHtml.call(this);
	//this.getHtmlElement().innerHTML = "Session Information" ;
}

/**
 * refresh : 0, no refresh
 * 			 1, refresh on the client side
 * 			 > 1, refresh on the server side by bypassing the cache		
 */
ZaServerSessionStatsPage.prototype.showMe = 
function (refresh){
	//if(window.console && window.console.log) console.debug("show the session stats page") ;
	
	if (!this._rendered) {
		DwtTabViewPage.prototype.showMe.call(this);
		var instance = {currentTab:ZaServerSessionStatsPage.SOAP_TAB_ID}; 
		var xModelObj = new XModel({id:"currentTab", type:_UNTYPED_});
		this._localXForm = this._view = new XForm(this._getXForm(), xModelObj, instance, this);
		this._view.setController(this);
		this._view.draw();
	}
	
	var params = {} ;
	if (refresh && refresh > 1) params.refresh = "1" ;
	//params.limit = 25 ;
	
	if (!this._rendered || refresh > 0) { //load all the tabs in one time. Should have a way to load the current tab only
		this.getSessions({type: "soap"}) ;
		this.getSessions({type: "admin"}) ;
		this.getSessions({type: "imap"}) ;
	}
	
	/*
	if (refresh) {
		this.getSessions() ;	
	} */
	
	this._rendered = true;
}

ZaServerSessionStatsPage.prototype.hideMe = 
function (){
	DwtTabViewPage.prototype.hideMe.call(this);	
	this.updateToolbar(null, true);
	this._hide = true ;
};

ZaServerSessionStatsPage.prototype._pageListener =
function (isPrevPage) {
	var type = this.getType () ;
	var params = {} ;
	var curPage = this._pageObj[type]['curPage'] ;
	var totalPage = this._pageObj[type]['totalPage'] ;
	
	if (isPrevPage) {
		params.offset = (curPage - 2)* ZaServerSessionStatsPage.PAGE_LIMIT ;
	}else{
		params.offset = curPage * ZaServerSessionStatsPage.PAGE_LIMIT ;
	}
	params.type = type ;
	this.getSessions(params) ;
}

ZaServerSessionStatsPage.prototype.getType =
function (tabId) {
	if (! tabId) { //get the current type
		var instance =	this._localXForm.getInstance () ;
		tabId = instance[ZaModel.currentTab] ;
	}
	
	var type; 
	if (tabId == ZaServerSessionStatsPage.SOAP_TAB_ID) {
		type = "soap";
	}else if (tabId == ZaServerSessionStatsPage.ADMIN_TAB_ID) {
		type = "admin" ;
	}else if (tabId == ZaServerSessionStatsPage.IMAP_TAB_ID) {
		type = "imap" ;
	}
	return type ;
}
ZaServerSessionStatsPage.prototype.updateToolbar = 
function (tabId, hide ){
	var controller = ZaApp.getInstance().getCurrentController();
	try {
		//enable the page back/forward button
		if ( controller instanceof ZaServerStatsController ){
			if (! this._localXForm) {
				return ;
			}
			var instance =	this._localXForm.getInstance () ;
			var currentTabId = instance[ZaModel.currentTab] ;			
			var type = this.getType(currentTabId) ;
			
			var toolBar = controller.getToolBar();		
			var curPage = this._pageObj [type]["curPage"] ;
			var totalPage = this._pageObj [type]["totalPage"] ;	
			
			if (toolBar){
				if ((! hide) && tabId && (tabId == currentTabId)) { //only update the toolbar for the current TAB
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
					
					//TODO update the help link for the Session Stats
					controller._helpURL = location.pathname + ZaUtil.HELP_URL + "managing_servers/viewing_mailbox_quotas.htm?locid="+AjxEnv.DEFAULT_LOCALE;
				}else if (hide){
					toolBar.enable([ZaOperation.PAGE_FORWARD, ZaOperation.PAGE_BACK, ZaOperation.LABEL], false);
					toolBar.getButton("PageInfo").setText(AjxMessageFormat.format (ZaMsg.MBXStats_PAGEINFO, [1,1]));
					//change the help link back
					controller._helpURL = location.pathname + ZaUtil.HELP_URL + "monitoring/checking_usage_statistics.htm?locid="+AjxEnv.DEFAULT_LOCALE;
				}
			}
		}
	}catch (ex){
		controller._handleException (ex, "ZaServerSessionStatsPage.updateToolbar", null, false)
	}
};

ZaServerSessionStatsPage.prototype.dumpSession =
function () {
	var soapDoc = AjxSoapDoc.create("DumpSessionsRequest", ZaZimbraAdmin.URN, null);
	//TODO need to provide the ability to customize the attributes
	var listSession = "1" ;
	var groupByAccount = "1" ;
	soapDoc.getMethod().setAttribute("listSessions", listSession);
	soapDoc.getMethod().setAttribute("groupByAccount", groupByAccount);
	var dumpSessCmd = new ZmCsfeCommand ();
	var params = {} ;
	params.soapDoc = soapDoc ;
	params.targetServer = this._server.id ;
	params.asyncMode = true ;
	params.callback = new AjxCallback (this, this.dumpSessionCallback) ;
	//if(window.console && window.console.log) console.debug("Send DumpSessionsRequest") ;
	dumpSessCmd.invoke(params) ;
}

ZaServerSessionStatsPage.prototype.getSessions =
function (params) {
	var soapDoc = AjxSoapDoc.create("GetSessionsRequest", ZaZimbraAdmin.URN, null);
	if (!params) params = {} ;
	var instance =	this._localXForm.getInstance () ;
	var currentTabId = instance[ZaModel.currentTab] ;
	if (!params.type) {
		params.type = this.getType (currentTabId) ;
	}
	soapDoc.getMethod().setAttribute("type", params.type);

	if (params.refresh) {
		soapDoc.getMethod().setAttribute("refresh", params.fresh);
	}
	//if (params.limit) {
		soapDoc.getMethod().setAttribute("limit", ZaServerSessionStatsPage.PAGE_LIMIT);
	//}
	if (!params.offset) {
		params.offset = this._offset[params.type] ;
	}
	soapDoc.getMethod().setAttribute("offset", params.offset);
	 
	if (!params.sortBy) {
		params.sortBy = this._sortBy[params.type] ;
	}
	soapDoc.getMethod().setAttribute("sortBy", params.sortBy);
	
	var getSessCmd = new ZmCsfeCommand ();
	params.soapDoc = soapDoc ;
	params.targetServer = this._server.id ;
	params.asyncMode = true ;
	params.callback = new AjxCallback (this, this.getSessionsCallback, [params]) ;
	//if(window.console && window.console.log) console.debug("Send GetSessionsRequest") ;
	getSessCmd.invoke(params) ;
}

ZaServerSessionStatsPage.prototype.getSessionsCallback =
function (reqParams, resp) {
	//if(window.console && window.console.log) console.debug("GetSessionCallback is called. And process the response now ...");
	if (resp._data.Body) {
		var sessionStats = resp._data.Body.GetSessionsResponse ;
		var instance = this._localXForm.getInstance();
		
		instance[reqParams.type + "_total"] = sessionStats.total ;
		
		var sessionList = new AjxVector() ; 
		if (sessionStats.total > 0 && sessionStats.s) {
			this._processGetSessResp ( sessionStats.s, sessionList );
		}
		
		instance[reqParams.type] = sessionList.getArray() ;
		instance[reqParams.type].join = ZaServerSessionStatsPage._objArrJoin ; //to make sure the _DWT_LIST_ item will update the view
		
		//this._localXForm.refresh();
		//update the tab bar text
		var tabBar = this._localXForm.getItemsById("xform_tabbar")[0];
		var dwtTabBar = tabBar.getWidget();
		if (reqParams.type == "soap") {
			dwtTabBar.getItem(ZaServerSessionStatsPage.SOAP_TAB_ID - 1)
				.setText(ZaMsg.TABT_SessStatsSoap + " (" + instance[reqParams.type + "_total"] + ") ") ;
		}else if (reqParams.type == "admin") {
			dwtTabBar.getItem(ZaServerSessionStatsPage.ADMIN_TAB_ID - 1)
				.setText(ZaMsg.TABT_SessStatsAdmin + " (" + instance[reqParams.type + "_total"] + ") ") ;
		}else if (reqParams.type == "imap") {
			dwtTabBar.getItem(ZaServerSessionStatsPage.IMAP_TAB_ID - 1)
				.setText(ZaMsg.TABT_SessStatsImap + " (" + instance[reqParams.type + "_total"] + ") ") ;
		}
		
		this._localXForm.setInstance(instance) ;
	}
	this._updatePageObj(reqParams, instance[reqParams.type + "_total"]);
	//update the toolbar for the current Tab only
	var currentTabId = instance[ZaModel.currentTab] ;
	if ((reqParams.type == "soap" && currentTabId == ZaServerSessionStatsPage.SOAP_TAB_ID)
		|| (reqParams.type == "admin" && currentTabId == ZaServerSessionStatsPage.ADMIN_TAB_ID)
		|| (reqParams.type == "imap" && currentTabId == ZaServerSessionStatsPage.IMAP_TAB_ID)) {
		this.updateToolbar( currentTabId  , false);
	}
}

ZaServerSessionStatsPage.prototype._updatePageObj =
function (params, total) {
	var type = params.type ;
	var start = params.offset ;
	this._pageObj[type]["curPage"] = start / ZaServerSessionStatsPage.PAGE_LIMIT + 1;
	this._pageObj[type]["totalPage"] = Math.ceil( total / ZaServerSessionStatsPage.PAGE_LIMIT ) || 1;
}

ZaServerSessionStatsPage._objArrJoin =
function () {
	var arr = []
	for (var i = 0; i < this.length; i++) {
		arr.push(this[i].sid) ;
	}
	return arr.join();
}

ZaServerSessionStatsPage.prototype._processGetSessResp =
function ( sessResp, sessList) {
	for (var i=0; i < sessResp.length ; i ++) {
		var cSessions = sessResp[i] ;
		sessList.add( new ZaServerSession(
				cSessions.name, cSessions.zid, cSessions.sid, cSessions.cd, cSessions.ld )) ;		
	}
}

ZaServerSessionStatsPage.prototype.dumpSessionCallback =
function (resp) {
	//if(window.console && window.console.log) console.debug("DumpSessionCallback is called. And process the response now ...");
	var sessionStats = resp._data.Body.DumpSessionsResponse ;
	this._activeSessions = sessionStats.activeSessions ;
	//this._activeAccounts = sessionStats.activeAccounts ;
	this._activeAdminSessions = this._activeAdminAccounts = 0;
	this._activeSoapSessions = this._activeSoapAccounts = 0;
	this._activeImapSessions = this._activeImapAccounts = 0;
	
	//converted list objects
	this._adminSessList = new AjxVector();
	this._imapSessList = new AjxVector() ;
	this._soapSessList = new AjxVector() ;
	
	if (sessionStats.admin) {
		this._adminSessResp = sessionStats.admin[0]
		this._processResponse(this._adminSessResp, this._adminSessList) ;
		this._activeAdminSessions = this._adminSessResp.activeSessions ;
		this._activeAdminAccounts = this._adminSessResp.activeAccounts ;
	}
	
	if (sessionStats.imap) {
		this._imapSessResp = sessionStats.imap[0];
		this._processResponse(this._imapSessResp, this._imapSessList) ;
		this._activeImapSessions = this._imapSessResp.activeSessions ;
		this._activeImapAccounts = this._imapSessResp.activeAccounts ;
	}
	
	if (sessionStats.soap) {
		this._soapSessResp = sessionStats.soap[0];
		this._processResponse( this._soapSessResp, this._soapSessList) ;		
		this._activeSoapSessions = this._soapSessResp.activeSessions ;
		this._activeSoapAccounts = this._soapSessResp.activeAccounts ;
	}
	
	//show the lists
	//this._adminListView = new ZaServerSessionListView(this) ;
	//this._adminListView.set(this._adminSessList) ;
	if (this._localXForm) {
		var instance = this._localXForm.getInstance();
		instance[ZaServerSession.A_activeSessions] = this._activeSessions ;
		//instance[ZaServerSession.A_activeAccounts] = this._activeAccounts ;
		instance[ZaServerSession.A_activeAdminAccounts] = this._activeAdminAccounts ;
		instance[ZaServerSession.A_activeAdminSessions] = this._activeAdminSessions ;
		instance[ZaServerSession.A_activeSoapSessions] = this._activeSoapSessions ;
		instance[ZaServerSession.A_activeSoapAccounts] = this._activeSoapAccounts ;
		instance[ZaServerSession.A_activeImapAccounts] = this._activeImapAccounts ;
		instance[ZaServerSession.A_activeImapSessions] = this._activeImapSessions ;
		
		instance["admin"] = this._adminSessList.getArray() ;
		instance["soap"] = this._soapSessList.getArray() ;
		instance["imap"] = this._imapSessList.getArray() ;
		this._localXForm.setInstance(instance) ;
	}
}



ZaServerSessionStatsPage.prototype._processResponse =
function ( sessResp, sessList) {
	for (var i=0; i < sessResp.zid.length ; i ++) {
		var cAccount = sessResp.zid [i] ;
		var cSessions = cAccount.s ;
		for (var j=0; j < cSessions.length; j++) {
			sessList.add(
				new ZaServerSession(cAccount.name, cAccount.id, cSessions[j].sid, cSessions[j].cd, cSessions[j].ld)) ;		
		}
	}
}

ZaServerSessionStatsPage.prototype.getStatCountsOutput =
function (itemType) {
	var output = new AjxBuffer() ;
	var instance = this._localXForm.getInstance(); 
	
	if (itemType == ZaServerSession.A_activeSessions) {
		var totalSessions = instance["soap_total"] + instance["admin_total"] + instance["imap_total"] ;
		output.append(ZaMsg.SessStats_ActiveSessions, " ", totalSessions ) ;
	}
	/*
	else if (itemType == ZaServerSession.A_activeAccounts) {
		output.append(ZaMsg.SessStats_ActiveAccounts, " ", 
					 "soap: ", instance[ZaServerSession.A_activeSoapAccounts], ", ",
					 "admin: ", instance[ZaServerSession.A_activeAdminAccounts], ", ",
					 "IMAP: ", instance[ZaServerSession.A_activeImapAccounts] ) ;
	}*/
	
	return output.join("") ;
}

ZaServerSessionStatsPage.SOAP_TAB_ID = 1;
ZaServerSessionStatsPage.ADMIN_TAB_ID = 2;
ZaServerSessionStatsPage.IMAP_TAB_ID = 3; 

ZaServerSessionStatsPage.prototype._getXForm = function () {
	if (this._xform != null) return this._xform;
	var headerList1 = ZaServerSessionListView._getHeaderList();
	var headerList2 = ZaServerSessionListView._getHeaderList();
	var headerList3 = ZaServerSessionListView._getHeaderList();
	
	this._xform = {
		x_showBorder:1,
	    numCols:1, 
	    cssClass:"ZaServerSessionStatsPage", 
		tableCssStyle:"width:100%",
	    itemDefaults:{ },
	    items:[
		   {type:_SPACER_, height:"10px", colSpan:"*",id:"xform_header" },
		   {ref: ZaServerSession.A_activeSessions, type:_OUTPUT_, height: "15px", colSpan:"*", 
		   		getDisplayValue:"return this.getFormController().getStatCountsOutput(ZaServerSession.A_activeSessions)"},	
		  /*
		   {ref: ZaServerSession.A_activeAccounts, type:_OUTPUT_, height: "15px", colSpan:"*", 
		   		getDisplayValue:"return this.getFormController().getStatCountsOutput(ZaServerSession.A_activeAccounts)"},	
		   */
		   {type:_SPACER_, height:"10px", colSpan:"*", id:"xform_header" },
		   
		   {type:_TAB_BAR_,  ref:ZaModel.currentTab, colSpan:"*", 
		   		onChange: ZaServerSessionStatsPage.tabChanged,
		   		choices:[
			     {value:ZaServerSessionStatsPage.SOAP_TAB_ID, label:ZaMsg.TABT_SessStatsSoap},
			     {value:ZaServerSessionStatsPage.ADMIN_TAB_ID, label:ZaMsg.TABT_SessStatsAdmin},
			     {value:ZaServerSessionStatsPage.IMAP_TAB_ID, label:ZaMsg.TABT_SessStatsImap}
			    ],
		    cssClass:"ZaTabBar", id:"xform_tabbar"
		   },

		   {type:_SWITCH_, align:_LEFT_, valign:_TOP_, 
		    items:[
			   {type:_ZATABCASE_, caseKey:1, align:_LEFT_, valign:_TOP_, 
			   		cssStyle: "position: absolute; overflow: auto;",
			    items:[
				   {ref: "soap", type:_DWT_LIST_ , width:"100%",  cssClass: "MBXList",     	
						   		forceUpdate: true, widgetClass:ZaServerSessionListView, 
						   		headerList:headerList1, defaultColumnSortable: 1}
				   ]
			   },
			   {type:_ZATABCASE_,  caseKey:2, align:_LEFT_, valign:_TOP_, 
			    	cssStyle: "position: absolute; overflow: auto;",
			    items:[
				    {ref: "admin", type:_DWT_LIST_ , width:"100%",  cssClass: "MBXList",     	
						   		forceUpdate: true, widgetClass:ZaServerSessionListView, 
						   		headerList:headerList2, defaultColumnSortable: 1}
				   ]
			   },

			   {type:_ZATABCASE_, caseKey:3, align:_LEFT_, valign:_TOP_, 
			    	cssStyle: "position: absolute; overflow: auto;",
			    items:[
				   {ref: "imap", type:_DWT_LIST_ , width:"100%",  cssClass: "MBXList",     	
						   		forceUpdate: true, widgetClass:ZaServerSessionListView, 
						   		headerList:headerList3, defaultColumnSortable: 1}
				   ]
			   }
			 ]
		   }
		   ]
	};
		  
	return this._xform;
};


ZaServerSessionStatsPage.tabChanged =
function (value, event, form) {
	//if(window.console && window.console.log) console.log("The tabs in the session page is switched. Update the toolbar ...") ; 	
	//set the instance value
	this.setInstanceValue (value) ;
	form.parent.updateToolbar(value, false) ;
}

ZaServerSession = function(name, zid, sid, cd, ld) {
	this.name = name ;
	this.zid = zid ;
	this.sid = sid ;
	this.cd = cd ;
	this.ld = ld ; 
}

ZaServerSession.A_activeSessions = "activeSessions" ;
ZaServerSession.A_activeSoapSessions = "activeSoapSessions" ;
ZaServerSession.A_activeImapSessions = "activeImapSessions" ;
ZaServerSession.A_activeAdminSessions = "activeAdminSessions" ;
ZaServerSession.A_activeAccounts = "activeAccounts" ;
ZaServerSession.A_activeAdminAccounts = "activeAdminAccounts" ;
ZaServerSession.A_activeImapAccounts = "activeImapAccounts" ;
ZaServerSession.A_activeSoapAccounts = "activeSoapAccounts" ;

ZaServerSession.getDate =
function (time) {
	var date = new Date();
	date.setTime(time) ;
	//use AjxDateFormat
	return AjxDateFormat.format("MM/dd/yyyy HH:mm:ss", date);
}

ZaServerSessionListView = function(parent, cssClass, posStyle, headerList) {
	this._app = parent.parent._app ;
	var className = null;
	var posStyle = DwtControl.ABSOLUTE_STYLE;
	
	//var headerList = this._getHeaderList();
	
	ZaListView.call(this, parent, className, posStyle, headerList);
	this._bSortAsc = true; //default is ascending
}

ZaServerSessionListView.prototype = new ZaListView;
ZaServerSessionListView.prototype.constructor = ZaServerSessionListView;

ZaServerSessionListView._getHeaderList =
function() {

	var headerList = new Array();
	var idx = 0 ;
	var sortable = 1 ;
	//idPrefix, label, iconInfo, width, sortable, sortField, resizeable, visible
	headerList[idx ++] = new ZaListHeaderItem("name", ZaMsg.h_account_name, null, "150px",  sortable ++ , "name", true, true);
	//headerList[idx ++] = new ZaListHeaderItem("zid", ZaMsg.h_account_id, null, "250px", null, null, null, true);
	headerList[idx ++] = new ZaListHeaderItem("sid", ZaMsg.h_session_id, null, "150px",  null, "sid", true,  true);
	headerList[idx ++] = new ZaListHeaderItem("cd", ZaMsg.h_sess_cd, null, "150px",   sortable ++, "created", true,  true);
	headerList[idx ++] = new ZaListHeaderItem("ld", ZaMsg.h_sess_ld, null, "auto",  sortable ++, "accessed", true,  true);	
		
	return headerList;
}

ZaServerSessionListView.prototype._createItemHtml =
function (sess) {
	var html = new Array(50);
	var	div = document.createElement("div");
	div[DwtListView._STYLE_CLASS] = "Row";
	div[DwtListView._SELECTED_STYLE_CLASS] = div[DwtListView._STYLE_CLASS] + "-" + DwtCssStyle.SELECTED;
	div.className = div[DwtListView._STYLE_CLASS];
	this.associateItemWithElement(sess, div, DwtListView.TYPE_LIST_ITEM);
	div.style.height = "20";
	
	var idx = 0;
	html[idx++] = "<table width='100%' height='20' cellspacing='2' cellpadding='0'>";

	html[idx++] = "<tr>";
	if(this._headerList) {
		var cnt = this._headerList.length;
		
		for(var i = 0; i < cnt; i++) {
			var field = this._headerList[i]._field;
			if(field == "name") {
				// account id
				html[idx++] = "<td width=" + this._headerList[i]._width + "><nobr>";
				html[idx++] = AjxStringUtil.htmlEncode(sess["name"]);				
				html[idx++] = "</nobr></td>";
			}
			/*else if(field == "zid") {
				// account id
				html[idx++] = "<td width=" + this._headerList[i]._width + "><nobr>";
				html[idx++] = AjxStringUtil.htmlEncode(sess["zid"]);				
				html[idx++] = "</nobr></td>";
			}*/ else if (field == "sid"){
				// sid
				html[idx++] = "<td width=" + this._headerList[i]._width + ">";
				html[idx++] = AjxStringUtil.htmlEncode(sess["sid"]);	
				html[idx++] = "</td>";	
			} else if (field == "cd"){
				// cd
				html[idx++] = "<td width=" + this._headerList[i]._width + ">";
				html[idx++] = AjxStringUtil.htmlEncode(
							ZaServerSession.getDate(sess["cd"].toString()));	
				html[idx++] = "</td>";	
			} else if (field == "ld"){
				// ld
				html[idx++] = "<td width=" + this._headerList[i]._width + ">";
				html[idx++] = AjxStringUtil.htmlEncode(
							ZaServerSession.getDate(sess["ld"].toString()));	
				html[idx++] = "</td>";	
			}
		}
	} else {
		html[idx++] = "<td width=100%><nobr>";
		html[idx++] = AjxStringUtil.htmlEncode(sess["zid"]);
		html[idx++] = "</nobr></td>";
	}
	
	html[idx++] = "</tr></table>";
	div.innerHTML = html.join("");
	return div;
}

ZaServerSessionListView.prototype._setNoResultsHtml = function() {
	var buffer = new AjxBuffer();
	var	div = document.createElement("div");
	
	buffer.append("<table width='100%' cellspacing='0' cellpadding='1'>",
				  "<tr><td class='NoResults'>",
				  AjxStringUtil.htmlEncode(ZaMsg.sess_noSess),
				  "</td></tr></table>");
	
	div.innerHTML = buffer.toString();
	this._addRow(div);
};

ZaServerSessionListView.prototype._sortColumn = 
function(columnItem, bSortAsc) {
	var sortBy = columnItem._sortField + (bSortAsc ? "Asc": "Desc") ;
	//if(window.console && window.console.log) console.log("SortBy: " + sortBy) ;
	try {
		var controller = ZaApp.getInstance().getCurrentController() ;
		var sessStatsPage = controller._contentView._sessionPage ;
		sessStatsPage.setSortBy (sortBy) ;
		sessStatsPage.showMe(1);
	} catch (ex) {
		ZaApp.getInstance().getCurrentController()._handleException(ex);
	}
}
