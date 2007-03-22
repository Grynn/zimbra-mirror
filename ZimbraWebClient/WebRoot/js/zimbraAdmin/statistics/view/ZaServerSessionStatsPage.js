
/**
 * This page displays the current server's session statistics.
 * 
 * @author Charles Cao
 */
function ZaServerSessionStatsPage (parent, app) {
	DwtTabViewPage.call(this, parent);
	this._app = app;
	//The response objects
	this._adminSessResp = {} ;
	this._imapSessResp = {} ;
	this._soapSessResp = {} ;
		
	this._rendered = false ;
}

ZaServerSessionStatsPage.prototype = new DwtTabViewPage;
ZaServerSessionStatsPage.prototype.constructor = ZaServerSessionStatsPage;

ZaServerSessionStatsPage.prototype.setObject =
function (currentServer) {
	this._server = currentServer ;
}

ZaServerSessionStatsPage.prototype._createHtml =
function () {
	if (AjxEnv.hasFirebug) console.debug("Create the session stats page") ;
	DwtTabViewPage.prototype._createHtml.call(this);
	//this.getHtmlElement().innerHTML = "Session Information" ;
}

ZaServerSessionStatsPage.prototype.showMe = 
function (refresh){
	if (AjxEnv.hasFirebug) console.debug("show the session stats page") ;
	
	if (!this._rendered) {
		DwtTabViewPage.prototype.showMe.call(this);
		var instance = {currentTab:1}; 
		var xModelObj = new XModel({id:"currentTab", type:_UNTYPED_});
		this._localXForm = this._view = new XForm(this._getXForm(), xModelObj, instance, this);
		this._view.setController(this);
		this._view.draw();
	}
	
	if (!this._rendered || refresh) {
		this.dumpSession() ;
	}
	
	this._rendered = true;
}

ZaServerSessionStatsPage.prototype.dumpSession =
function () {
	var soapDoc = AjxSoapDoc.create("DumpSessionsRequest", "urn:zimbraAdmin", null);
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
	if (AjxEnv.hasFirebug) console.debug("Send DumpSessionsRequest") ;
	dumpSessCmd.invoke(params) ;
}

ZaServerSessionStatsPage.prototype.dumpSessionCallback =
function (resp) {
	if (AjxEnv.hasFirebug) console.debug("DumpSessionCallback is called. And process the response now ...");
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
	//this._adminListView = new ZaServerSessionListView(this, this._app) ;
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
				new ZaServerSession(cAccount.id, cSessions[j].sid, cSessions[j].cd, cSessions[j].ld)) ;		
		}
	}
}

ZaServerSessionStatsPage.prototype.getStatCountsOutput =
function (itemType) {
	var output = new AjxBuffer() ;
	var instance = this._localXForm.getInstance(); 
	/*
	switch (itemType) {
		case ZaServerSession.A_activeAccounts :
			output = ZaMsg.SessStats_ActiveAccounts + " " + instance[itemType] ; break ;
		case ZaServerSession.A_activeSessions :
			output = ZaMsg.SessStats_ActiveSessions + " " + instance[itemType] ; break ;
		case ZaServerSession.A_activeSoapAccounts :
			output = ZaMsg.SessStats_ActiveSoapAccounts + " " + instance[itemType] ; break ;
		case ZaServerSession.A_activeImapAccounts :
			output = ZaMsg.SessStats_ActiveImapAccounts + " " + instance[itemType] ; break ;
		case ZaServerSession.A_activeAdminAccounts :
			output = ZaMsg.SessStats_ActiveAdminAccounts + " " + instance[itemType] ; break ;
		case ZaServerSession.A_activeSoapSessions :
			output = ZaMsg.SessStats_ActiveSoapSessions + " " + instance[itemType] ; break ;
		case ZaServerSession.A_activeImapSessions :
			output = ZaMsg.SessStats_ActiveImapSessions + " " + instance[itemType] ; break ;
		case ZaServerSession.A_activeAdminSessions :
			output = ZaMsg.SessStats_ActiveAdminSessions + " " + instance[itemType] ; break ;
		default:
			break ;
	}*/
	
	if (itemType == ZaServerSession.A_activeSessions) {
		output.append(ZaMsg.SessStats_ActiveSessions, " ", instance[ZaServerSession.A_activeSessions],
				" (", "soap: ", instance[ZaServerSession.A_activeSoapSessions], 
				", ", "admin: ", instance[ZaServerSession.A_activeAdminSessions],
				", ", "IMAP: ", instance[ZaServerSession.A_activeImapSessions] ,
				")" ) ;
	}else if (itemType == ZaServerSession.A_activeAccounts) {
		output.append(ZaMsg.SessStats_ActiveAccounts, " ", 
					 "soap: ", instance[ZaServerSession.A_activeSoapAccounts], ", ",
					 "admin: ", instance[ZaServerSession.A_activeAdminAccounts], ", ",
					 "IMAP: ", instance[ZaServerSession.A_activeImapAccounts] ) ;
	}
	
	return output.join("") ;
}

ZaServerSessionStatsPage.prototype._getXForm = function () {
	if (this._xform != null) return this._xform;
	var headerList = ZaServerSessionListView._getHeaderList();
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
		   {ref: ZaServerSession.A_activeAccounts, type:_OUTPUT_, height: "15px", colSpan:"*", 
		   		getDisplayValue:"return this.getFormController().getStatCountsOutput(ZaServerSession.A_activeAccounts)"},	
		   {type:_SPACER_, height:"10px", colSpan:"*",id:"xform_header" },
		   
		   {type:_TAB_BAR_,  ref:ZaModel.currentTab, colSpan:"*",
		    choices:[
			     {value:1, label:ZaMsg.TABT_SessStatsSoap},
			     {value:2, label:ZaMsg.TABT_SessStatsAdmin},
			     {value:3, label:ZaMsg.TABT_SessStatsImap}
			    ],
		    cssClass:"ZaTabBar", id:"xform_tabbar"
		   },

		   {type:_SWITCH_, align:_LEFT_, valign:_TOP_, 
		    items:[
			   {type:_ZATABCASE_,  relevant:"instance[ZaModel.currentTab] == 1", align:_LEFT_, valign:_TOP_, 
			   		cssStyle: "position: absolute; overflow: auto;",
			    items:[
				   {ref: "soap", type:_DWT_LIST_ , width:"100%",  cssClass: "MBXList",     	
						   		forceUpdate: true, widgetClass:ZaServerSessionListView, 
						   		headerList:headerList}
				   ]
			   },
			   {type:_ZATABCASE_,  relevant:"instance[ZaModel.currentTab] == 2", align:_LEFT_, valign:_TOP_, 
			    	cssStyle: "position: absolute; overflow: auto;",
			    items:[
				    {ref: "admin", type:_DWT_LIST_ , width:"100%",  cssClass: "MBXList",     	
						   		forceUpdate: true, widgetClass:ZaServerSessionListView, 
						   		headerList:headerList}
				   ]
			   },

			   {type:_ZATABCASE_,  relevant:"instance[ZaModel.currentTab] == 3", align:_LEFT_, valign:_TOP_, 
			    	cssStyle: "position: absolute; overflow: auto;",
			    items:[
				   {ref: "imap", type:_DWT_LIST_ , width:"100%",  cssClass: "MBXList",     	
						   		forceUpdate: true, widgetClass:ZaServerSessionListView, 
						   		headerList:headerList}
				   ]
			   }
			 ]
		   }
		   ]
	};
		   

	return this._xform;
};



function ZaServerSession (zid, sid, cd, ld) {
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
	/*
	var m =	date.getMonth() + 1;
	var d = date.getDate() ;
	var y = date.getFullYear();
	var h = date.getHours ();
	var min = date.getMinutes() ;
	var s = date.getSeconds () ;
	return m + "/" + d + "/" + y + " " + h + ":" + min + ":" + s ; */
	//use AjxDateFormat
	return AjxDateFormat.format("MM/dd/yyyy hh:mm:ss", date);
	
}

function ZaServerSessionListView (parent, app, posStyle, headerList) {
	this._app = app ;
	var className = null;
	var posStyle = DwtControl.ABSOLUTE_STYLE;
	
	//var headerList = this._getHeaderList();
	
	ZaListView.call(this, parent, className, posStyle, headerList);
}

ZaServerSessionListView.prototype = new ZaListView;
ZaServerSessionListView.prototype.constructor = ZaServerSessionListView;

ZaServerSessionListView._getHeaderList =
function() {

	var headerList = new Array();
	var idx = 0 ;
	//idPrefix, label, iconInfo, width, sortable, sortField, resizeable, visible
	headerList[idx ++] = new ZaListHeaderItem("zid", ZaMsg.h_account_id, null, "250px", null, null, null, true);
	headerList[idx ++] = new ZaListHeaderItem("sid", ZaMsg.h_session_id, null, "150px", null, null, null, true);
	headerList[idx ++] = new ZaListHeaderItem("cd", ZaMsg.h_sess_cd, null, "150px", null, null, null, true);
	headerList[idx ++] = new ZaListHeaderItem("ld", ZaMsg.h_sess_ld, null, "auto", null, null, null, true);	
		
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
	
	var idx = 0;
	html[idx++] = "<table width='100%' cellspacing='2' cellpadding='0'>";

	html[idx++] = "<tr>";
	if(this._headerList) {
		var cnt = this._headerList.length;
		
		for(var i = 0; i < cnt; i++) {
			var id = this._headerList[i]._id;
			if(id.indexOf("zid") == 0) {
				// account id
				html[idx++] = "<td width=" + this._headerList[i]._width + "><nobr>";
				html[idx++] = AjxStringUtil.htmlEncode(sess["zid"]);				
				html[idx++] = "</nobr></td>";
			} else if (id.indexOf("sid") == 0){
				// sid
				html[idx++] = "<td width=" + this._headerList[i]._width + ">";
				html[idx++] = AjxStringUtil.htmlEncode(sess["sid"]);	
				html[idx++] = "</td>";	
			} else if (id.indexOf("cd") == 0){
				// cd
				html[idx++] = "<td width=" + this._headerList[i]._width + ">";
				html[idx++] = AjxStringUtil.htmlEncode(
							ZaServerSession.getDate(sess["cd"].toString()));	
				html[idx++] = "</td>";	
			} else if (id.indexOf("ld") == 0){
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
