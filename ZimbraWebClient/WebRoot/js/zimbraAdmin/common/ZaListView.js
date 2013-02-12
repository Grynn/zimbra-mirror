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
* @class ZaListView
* @constructor ZaListView
* @param parent
* @ className
* @ posStyle
* @ headerList
* Abstract class list views. All the List views in the Admin panel extend this class.
* @author Greg Solovyev
**/

ZaListView = function(params) {
	if (arguments.length == 0) return;
	params = Dwt.getParams(arguments, ZaListView.PARAMS);
	var id = (params.view) ? DwtId.getListViewId(params.view,params.id) : DwtId.getListViewId(params.id);
	params.id = id;
	DwtListView.call(this, params);

	//bug: 18787
	//Set the ListView Div DwtControl.SCROLL(overflow: auto) And the Rows Dwt.VISIBLE
    //In this way, the view of lists can be controlled by the scroll of the li st view
    // At the same time, no list row content will be hidden
    if (params.scrollLoading) {
		Dwt.setHandler(this._getScrollDiv(), DwtEvent.ONSCROLL, ZaListView.handleScroll);
	}

    this.setScrollStyle(DwtControl.SCROLL);
	if (this._listDiv) this._listDiv.style.overflow = "visible";

    this.scrollSearchParams=null;
    this.scrollHasMore=false;
}

ZaListView.PARAMS = ["parent", "className", "posStyle", "headerList", "view", "id", "scrollLoading"];

ZaListView.prototype = new DwtListView;
ZaListView.prototype.constructor = ZaListView;

ZaListView.prototype.toString = 
function() {
	return "ZaListView";
}

ZaListView.ITEM_FLAG_CLICKED = DwtListView._LAST_REASON + 1;

// default implementation
ZaListView.prototype._createItemHtml = function(item) {
	DwtListView.prototype._createItemHtml.call(this,item);
}

ZaListView.prototype.createHeaderHtml = function (defaultColumnSort) {
    DwtListView.prototype.createHeaderHtml.call(this, defaultColumnSort, true);
}

ZaListView.prototype.getTitle =
function () {
	return	"";
}

ZaListView.prototype.getTabToolTip =
function () {
	return	this.getTitle ();
}

ZaListView.prototype.getTabIcon = 
function () {
	return "" ;
}

ZaListView.prototype.getTabTitle =
function () {
	return this.getTitle() ;
}

ZaListView.prototype._mouseOverAction =
function(ev, div) {
	var _type = this._getItemData(div,"type");
	if (_type == DwtListView.TYPE_HEADER_ITEM) {
		if(this._headerList[this._getItemData(div,"index")]._sortable) {
			div.className = "DwtListView-Column DwtListView-ColumnHover";		
			this.setToolTipContent(AjxMessageFormat.format(ZaMsg.LST_ClickToSort_tt, [this._headerList[this._getItemData(div,"index")].getLabel()]));	
		} else {
			this.setToolTipContent(null);
		}
	} else if (_type == DwtListView.TYPE_HEADER_SASH) {
		div.style.cursor = AjxEnv.isIE ? "col-resize" : "e-resize";
    } else if (_type == DwtListView.TYPE_LIST_ITEM){
		var item = this.getItemFromElement(div);
		if (item && item.getToolTip) {
			var tt_content = "" ;
			try {	
				 //if(window.console && window.console.log) console.log("Item: " + item.name) ;
				 tt_content = item.getToolTip() ;
			}catch (e) {
				 tt_content = e.msg ;
			}
			this.setToolTipContent(tt_content);
        }
    }
	return true;
}


ZaListView.prototype._mouseOutAction = 
function(mouseEv, div) {
	var _type = this._getItemData(div,"type");
	if (_type == DwtListView.TYPE_HEADER_ITEM) {
		if(this._headerList[this._getItemData(div,"index")]._sortable) {
			div.className = (div.id != this._currentColId) ? "DwtListView-Column" : "DwtListView-Column DwtListView-ColumnActive"
		}
	}else if (_type == DwtListView.TYPE_HEADER_SASH) {
		div.style.cursor = "auto";
	}
	return true;
}

// Tooltip will be based on cursor
ZaListView.prototype.getTooltipBase =
function (hoverEv) {
    return null;
}

ZaListView.prototype._setListEvent =
function (ev, listEv, clickedEl) {
	DwtListView.prototype._setListEvent.call(this, ev, listEv, clickedEl);
	var parts = ev.target.id.split(DwtId.SEP);
	listEv.field = parts && parts[2];
	return true;
}

ZaListView.prototype._sortColumn = 
function(columnItem, bSortAsc) {
	if (bSortAsc) {
		this._list.sort(ZaItem.compareNamesAsc);
	} else {
    	this._list.sort(ZaItem.compareNamesDesc);
	}
	this.setUI();
}

ZaListView.prototype._setNoResultsHtml =
function () {
	if (ZaSearch.TOO_MANY_RESULTS_FLAG ){
		var htmlArr = new Array(3);
		var idx = 0;
	
		htmlArr[idx++] = "<table width='100%' cellspacing='0' cellpadding='1'><tr><td class='NoResults'><br>";
		htmlArr[idx++] = ZaMsg.TooManyResults;
		htmlArr[idx++] = "</td></tr></table>";
	
		var	div = document.createElement("div");
		div.innerHTML = htmlArr.join("");
		this._addRow(div);
	}else{
		DwtListView.prototype._setNoResultsHtml.call (this) ;
	}
}

ZaListView.prototype._getScrollDiv =
function() {
	return this.getHtmlElement();
};


ZaListView.prototype.setScrollSearchParams=
function(searchParams) {
    this.scrollSearchParams=searchParams;
}

ZaListView.prototype.setScrollHasMore=
function(hasMore) {
    this.scrollHasMore=hasMore;
}

ZaListView._getSearchKeyWord =
function(query) {
	var keyword = "";
	var sw = "zimbraDomainName=*";
        var domflag = "@";
	var ew = "*";
	if(!query) return keyword;
	var start = query.indexOf(sw);
	if(start < 0 || start > query.length-1)
	    return [];
	var end = query.indexOf(ew,start+sw.length);
	if(end > query.length-1)
	    return [];
	keyword = query.substr(start+sw.length,end-start-sw.length);
        // handle the case of "str@str"
        start = keyword.indexOf(domflag);
	if(start < 0 || start > query.length-1)
	    return [keyword];
	return [keyword.substr(start+1,keyword.length),keyword.substr(0,start)];
}

ZaListView.prototype.searchAliasDomain =
function (value, searchCtl,searchQueryList, offset, limit) {
        var busyId = Dwt.getNextId();
        var controller = searchCtl;

        var query = "(" + ZaDomain.A_domainName;
        if(value.length > 0) query += "=" + value[0] + "*)";
        else query = "";

        var callback = new AjxCallback(this, this.searchAliasDomainCallback,
            {busyId:busyId, searchQueryList:searchQueryList, childQueries:value, offset:offset, limit:limit});
        var searchParams = {
                        query: query,
                        types: [ZaSearch.DOMAINS],
                        sortBy: ZaDomain.A_domainName,
                        attrs: [ZaDomain.A_domainName],
                        callback:callback,
                        controller: controller,
                        showBusy:true,
                        busyId:busyId,
                        busyMsg:ZaMsg.BUSY_SEARCHING_DOMAINS,
			skipCallbackIfCancelled:false
        }
        ZaSearch.searchDirectory(searchParams);

}

ZaListView.prototype.searchAliasDomainCallback =
function(params,resp) {

        if(params.busyId)
                   ZaApp.getInstance().getAppCtxt().getShell().setBusy(false, params.busyId);
        try {
                if(!resp) {
                        throw(new AjxException(ZaMsg.ERROR_EMPTY_RESPONSE_ARG, AjxException.UNKNOWN, "ZaListView.searchAliasDomainCallback"));
                }
                if(resp.isException()) {
                        throw(resp.getException());
                } else {
                        var response = resp.getResponse().Body.SearchDirectoryResponse;
			var domainArr = new Array();
                        if (response.domain && response.domain.length > 0) {
                                for (var i =0; i < response.domain.length; i ++) {
                                       var domain = response.domain[i].name ;
						domainArr.push(domain);
                                }

                        }
			this._batchSearchforDomain(domainArr,params.searchQueryList, params.childQueries, params.offset, params.limit);
                }
        } catch (ex) {
		this._handleException(ex, "ZaListView.searchAliasDomainCallback", null, false);
	}

}

ZaListView.prototype._batchSearchforDomain =
function (domainArr, searchQueryList, childQueries, offset, limit) {
	var paramsArr;
    var searchTypes = ZaSearch.ALIASES;
    var searchQuery = "(uid=*";
    if(!childQueries) searchQuery += ")";
    else if(childQueries && !(childQueries instanceof Array)) searchQuery += childQueries + "*)";
    else if(childQueries instanceof Array && childQueries.length == 1) searchQuery += childQueries[0] + "*)";
    else if(childQueries instanceof Array && childQueries.length > 1)  searchQuery += childQueries[1] + ")";

    var controller = ZaApp.getInstance().getSearchListController();

	if(searchQueryList && searchQueryList instanceof Array)
		paramsArr = searchQueryList;
	else paramsArr = new Array();
        var busyId = Dwt.getNextId();
	var inParams = {limit:controller.RESULTSPERPAGE,show:true, openInSearchTab: true,busyId:busyId};
        var callback = new AjxCallback(this, this.searchCallback, inParams);
	for(var i =0; i < domainArr.length; i++) {
	        var searchParams = {
                            offset:offset,
                            limit :limit,
        	                query:searchQuery,
                	        types:searchTypes,
	                        showBusy:true,
        	                busyId:busyId,
	                        busyMsg:ZaMsg.BUSY_SEARCHING,
        	                skipCallbackIfCancelled:false,
	                        sortBy:controller._currentSortField,
        	                attrs:ZaSearch.standardAttributes,
                	        callback:callback,
	                        controller: controller,
        	                domain: domainArr[i]
	        }
		paramsArr.push(searchParams);
	}
	this.multipleSearchCallback(inParams, paramsArr);

}


ZaListView.prototype.multipleSearchCallback =
function(preParams, paramsArr) {
	var sortBy, limit, offset, sortAscending, soapDoc, cnt;
	var paramList = null;
	if(!paramsArr) return;
	if(paramsArr instanceof Array && paramsArr.length > 0)
		paramList = paramsArr;
	else {
		paramList = new Array();
		paramList.push(paramsArr);
	}

	cnt = paramList.length;
	soapDoc = AjxSoapDoc.create("BatchRequest", "urn:zimbra");
	soapDoc.setMethodAttribute("onerror", "continue");

	for(var i = 0; i < cnt; i++) {
		var getSearchDirDoc = soapDoc.set("SearchDirectoryRequest", null, null, ZaZimbraAdmin.URN);
		var squery = soapDoc.set("query", paramList[i].query, getSearchDirDoc);

                sortBy = (paramList[i].sortBy != undefined)? paramList[i].sortBy: ZaAccount.A_name;
                limit = (paramList[i].limit != undefined)? paramList[i].limit: ZaAccount.RESULTSPERPAGE;
                offset = (paramList[i].offset != undefined) ? paramList[i].offset : "0";
                sortAscending = (paramList[i].sortAscending != null)? paramList[i].sortAscending : "1";


                getSearchDirDoc.setAttribute("offset", offset);
                getSearchDirDoc.setAttribute("limit", limit);
                getSearchDirDoc.setAttribute("sortBy", sortBy);
                getSearchDirDoc.setAttribute("sortAscending", sortAscending);

                if(paramList[i].applyCos)
                        getSearchDirDoc.setAttribute("applyCos", paramList[i].applyCos);
                else
                        getSearchDirDoc.setAttribute("applyCos", false);


                if(paramList[i].applyConfig)
                        getSearchDirDoc.setAttribute("applyConfig", paramList[i].applyConfig);
                else
                        getSearchDirDoc.setAttribute("applyConfig", "false");

                if(paramList[i].domain)  {
                        getSearchDirDoc.setAttribute("domain", paramList[i].domain);

                }
                if(paramList[i].attrs && paramList[i].attrs.length>0)
                        getSearchDirDoc.setAttribute("attrs", paramList[i].attrs.toString());

                if(paramList[i].types && paramList[i].types.length>0)
                        getSearchDirDoc.setAttribute("types", paramList[i].types.toString());

                if(paramList[i].maxResults) {
                        getSearchDirDoc.setAttribute("maxResults", paramList[i].maxResults.toString());
                }


	}

	var params = new Object();
	params.soapDoc = soapDoc;
	var reqMgrParams ={
		//controller:this,
		busyMsg:ZaMsg.BUSY_REQUESTING_ACCESS_RIGHTS
	}

	var respObj = ZaRequestMgr.invoke(params, reqMgrParams);
	if(respObj.isException && respObj.isException()) {
		ZaApp.getInstance().getCurrentController()._handleException(respObj.getException(), "ZaListViewController.prototype.multipleSearchCallback", null, false);
	} else if(respObj.Body.BatchResponse.Fault) {
		var fault = respObj.Body.BatchResponse.Fault;
		if(fault instanceof Array)
			fault = fault[0];

		if (fault) {
			var ex = ZmCsfeCommand.faultToEx(fault);
			ZaApp.getInstance().getCurrentController()._handleException(ex,"ZaListViewController.prototype.multipleSearchCallback", null, false);
		}
	} else {
		var batchResp = respObj.Body.BatchResponse;
		if(batchResp.SearchDirectoryResponse && batchResp.SearchDirectoryResponse instanceof Array) {
			var cnt2 = batchResp.SearchDirectoryResponse.length;
			ZaSearch.TOO_MANY_RESULTS_FLAG = false;

            var tempResultList = null;
            var hasmore=false;
            var searchTotal = 0;
			for(var i = 0; i < cnt2; i++) {
				var resp = batchResp.SearchDirectoryResponse[i];
				var subList = new ZaItemList(preParams.CONS);

		        subList.loadFromJS(resp);
				//combine the search results
				if(!tempResultList) tempResultList = subList;
				else {
					if(tempResultList instanceof ZaItemList && subList.size() > 0) {
						var listVec = subList.getVector();
						for(var j = 0; j < listVec.size(); j++) {
							var item = listVec.get(j);
							if(!tempResultList.getVector().contains(item))
								tempResultList.add(item);
						}
					}
				}

				searchTotal +=  (resp.searchTotal ? resp.searchTotal : 0);
                hasmore= resp.more|hasmore;
			}
		        if(ZaZimbraAdmin.currentAdminAccount.attrs[ZaAccount.A_zimbraIsAdminAccount] != 'TRUE') {
		                var act = new AjxTimedAction(this._list, ZaItemList.prototype.loadEffectiveRights, null);
		                AjxTimedAction.scheduleAction(act, 150)
		        }


            if(tempResultList){
                var tmpArr = new Array();
		        var cnt = tempResultList.getArray().length;
		        for(var ix = 0; ix < cnt; ix++) {
                    var flag=false;
                    for (var i = 0; i < this._list.size(); i++) {
		                if (this._list.get(i).id == tempResultList.getArray()[ix].id)
                        {
                            flag=true;
                            break;
                        }
	                }
                    if(!flag)
			        tmpArr.push(tempResultList.getArray()[ix]);
		        }
            }
            this.replenish(AjxVector.fromArray(tmpArr));
            this.setScrollHasMore(hasmore);

            if (this.scrollSearchParams && this.scrollSearchParams.postCallback) {
                this.scrollSearchParams.postCallback.run(searchTotal, hasmore, this.getList());
            }

		}
	}
}



ZaListView.prototype._loadMsg =
function (params) {
		//this.show(null,params);
        var busyId = Dwt.getNextId();
	    var callback = new AjxCallback(this, this.searchCallback, {CONS:null,busyId:busyId});
        var searchParams=this.scrollSearchParams;
        searchParams.offset=params.offset;
        searchParams.limit=params.limit;
        searchParams.callback=callback;
        searchParams.busyId=busyId;

    if(searchParams&&searchParams.scrollType=="isAliasSearch"){
        var searchQueryList = new Array();
        searchQueryList.push(searchParams);
		var keyword = ZaListView._getSearchKeyWord(searchParams.query);
		this.searchAliasDomain(keyword,searchParams.controller,searchQueryList,params.offset,params.limit);
    }
    else{

	    ZaSearch.searchDirectory(searchParams);
    }
}

ZaListView.prototype.searchCallback =
function(params, resp) {
	try {
		if(params.busyId)
			ZaApp.getInstance().getAppCtxt().getShell().setBusy(false, params.busyId);

		if(!resp && !this._currentRequest.cancelled) {
			throw(new AjxException(ZaMsg.ERROR_EMPTY_RESPONSE_ARG, AjxException.UNKNOWN, "ZaListView.prototype.searchCallback"));
		}
		if(resp && resp.isException() && !this._currentRequest.cancelled) {
			ZaSearch.handleTooManyResultsException(resp.getException(), "ZaListView.prototype.searchCallback");
		} else {
			ZaSearch.TOO_MANY_RESULTS_FLAG = false;

            var tempList = new ZaItemList(params.CONS);
            var tempResultList = new ZaItemList(params.CONS);
			var hasmore=false;

			if(resp && !resp.isException()) {
				var response = resp.getResponse().Body.SearchDirectoryResponse;

                tempList.loadFromJS(response);
                // filter the search result
                if(params.resultFilter && tempList.size() > 0) {
                    var listVec = tempList.getVector();
                    for(var i = 0; i < listVec.size(); i++) {
                        var item = listVec.get(i);
                        var target = null;
                        if(item.type == ZaItem.ALIAS)
                            target = item.getAliasTargetObj();
                        else target = item;
                        for (var f in params.resultFilter) {
                            if(target.attrs[f].indexOf(params.resultFilter[f]) >= 0) {
                                tempResultList.add(item);
                                break;
                            }
                        }

                    }
                } else {
                    tempResultList = tempList;
                }
				if(ZaZimbraAdmin.currentAdminAccount.attrs[ZaAccount.A_zimbraIsAdminAccount] != 'TRUE') {
					var act = new AjxTimedAction(this._list, ZaItemList.prototype.loadEffectiveRights, null);
					AjxTimedAction.scheduleAction(act, 150)
				}
                 hasmore= response.more;

			}
             if(tempResultList){
                var tmpArr = new Array();
		        var cnt = tempResultList.getArray().length;
		        for(var ix = 0; ix < cnt; ix++) {
			        tmpArr.push(tempResultList.getArray()[ix]);
		        }
                this.replenish(AjxVector.fromArray(tmpArr));
                this.setScrollHasMore(hasmore);
                if (this.scrollSearchParams && this.scrollSearchParams.postCallback) {
                    this.scrollSearchParams.postCallback.run(response.searchTotal, response.more, this.getList());
                }
             }

		}
	} catch (ex) {
		if (ex.code != ZmCsfeException.MAIL_QUERY_PARSE_ERROR) {
			this._handleException(ex, "ZaListView.prototype.searchCallback");
		} else {
			this.popupErrorDialog(ZaMsg.queryParseError, ex);
			if(this._searchField)
				this._searchField.setEnabled(true);
		}
	}
}

ZaListView.handleScroll =
function(ev) {

	var target = DwtUiEvent.getTarget(ev);
	var lv = DwtControl.findControl(target);
	if (lv) {
		lv._checkItemCount();
	}

};


ZaListView.prototype._checkItemCount =
function() {
	var itemsNeeded =  this._getItemsNeeded();
	if (itemsNeeded) {
        this.setScrollHasMore(false);
        var params = {
                    offset:this._list.size(),
                    limit:itemsNeeded
				  };;
        this. _loadMsg(params);
	}
};


ZaListView.prototype._getItemsNeeded =
function(skipMoreCheck) {

	if (!skipMoreCheck) {
		if (!this.scrollHasMore || !this._list) { return 0; }
	}

    this._setRowHeight();
    //if (!this._rendered || !this._rowHeight) { return 0; }
     if ( !this._rowHeight) { return 0; }
	DBG.println(AjxDebug.DBG2, "List view: checking item count");

	var sbCallback = new AjxCallback(null, AjxTimedAction.scheduleAction, [new AjxTimedAction(this, this._resetColWidth), 10]);
	var params = {scrollDiv:	this._getScrollDiv(),
				  rowHeight:	this._rowHeight,
				  threshold:	this.getPagelessThreshold(),
				  limit:		this.getLimit(1),
				  listSize:		this._list.size(),
				  sbCallback:	sbCallback};
	return ZaListView.getRowsNeeded(params);
};


ZaListView.getRowsNeeded =
function(params) {
     var div = params.scrollDiv;
	var sh = div.scrollHeight, st = div.scrollTop, rh = params.rowHeight;

	// view (porthole) height - everything measured relative to its top
	// prefer clientHeight since (like scrollHeight) it doesn't include borders
	var h = div.clientHeight || Dwt.getSize(div).y;

	// where we'd like bottom of list view to be (with extra hidden items at bottom)
	var target = h + (params.threshold * rh);

	// where bottom of list view is (including hidden items)
	var bottom = sh - st;

	if (bottom == h) {
		// handle cases where there's no scrollbar, but we have more items (eg tall browser, or replenishment)
		bottom = (params.listSize * rh) - st;
		if (st == 0 && params.sbCallback) {
			// give list view a chance to fix width since it may be getting a scrollbar
			params.sbCallback.run();
		}
	}

	var rowsNeeded = 0;
	if (bottom < target) {
		// buffer below visible bottom of list view is not full
		rowsNeeded = Math.max(Math.floor((target - bottom) / rh), params.limit);
	}
	return rowsNeeded;
};

ZaListView.prototype.getLimit =
function(offset) {
	var limit = ZaSettings.RESULTSPERPAGE;
	return offset ? limit : 2 * limit;

};

ZaListView.prototype.getPagelessThreshold =
function() {
	return Math.ceil(this.getLimit() / 5);
};


ZaListView.prototype.setHeaderList = function(headerList) {
	this._headerList = headerList;
	this.headerColCreated = false;
}

ZaListHeaderItem = function(idPrefix, text, iconInfo, width, sortable, sortField, resizeable, visible) {
	DwtListHeaderItem.call(this, {field:idPrefix, text:text, icon:iconInfo, width:width, sortable:sortable,
								  resizeable:resizeable, visible:visible});
	this._sortField = sortField;	
	this._initialized = false;
}

ZaListHeaderItem.prototype = new DwtListHeaderItem;
ZaListHeaderItem.prototype.constructor = ZaListHeaderItem;


ZaListHeaderItem.prototype.getSortField = 
function() {
	return this._sortField;
}

ZaListHeaderItem.prototype.getLabel = 
function () {
	return this._label;
}


//ZaListView's subclasses can use it to override orginally DwtListView.prototype._handleColHeaderResize(),
//due to in the latter, the header sash will not work well in some cases
ZaListView._handleColHeaderResize =
function(ev) {
	if (!this._headerSash) {
		this._headerSash = document.createElement("div");

		var headerHeight = Dwt.getSize(this._listColDiv).y;
		var thislistViewHeight = this.getSize().y;
		var sashHeight = thislistViewHeight - 4; //make its height shorten a little

		Dwt.setSize(this._headerSash, Dwt.DEFAULT, sashHeight);
		Dwt.setPosition(this._headerSash, Dwt.ABSOLUTE_STYLE);
		Dwt.setZIndex(this._headerSash, Dwt.Z_DND);

		//as this._listDiv's sub node, headerSash will use 'absolute position',
		//setting this._listDiv's position style to relative
		//will make headerSash's position based on it.
		Dwt.setPosition(this._listDiv, Dwt.RELATIVE_STYLE);
		Dwt.setLocation(this._listDiv, 0, 0);
		this._headerSash.className = "DwtListView-ColumnSash";
		this._listDiv.appendChild(this._headerSash);

		//always set the y-position to -headerHeight(based on _listDiv) to align to the header(_listColDiv) not _listColDiv
		var sashY = -headerHeight;
		Dwt.setLocation(this._headerSash, Dwt.DEFAULT, sashY);

		//always remember initial mouse x-position
		this._headerSashX = ev.docX;
	}

	//always update the sash's x-position
	var thislistViewX = Dwt.getLocation(this._listDiv).x;
	var sashX = ev.docX - thislistViewX;

	Dwt.setLocation(this._headerSash, sashX);
};
