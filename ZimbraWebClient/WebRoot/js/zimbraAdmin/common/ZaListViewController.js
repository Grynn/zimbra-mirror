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
* @class ZaListViewController base class for all Za***ListControllers (for list views)
* @extends ZaController
* @contructor 
* @param appCtxt
* @param container
* @param app
* @param iKeyName
* @author Greg Solovyev
* @see ZaAccountListController
* @see ZDomainListController
**/
ZaListViewController = function(appCtxt, container,iKeyName) {
	if (arguments.length == 0) return;
	this._currentPageNum = 1;	
	//this.pages = new Object();
	this._currentSortOrder = "1";
	ZaController.call(this, appCtxt, container,iKeyName);
	this.RESULTSPERPAGE = ZaSettings.RESULTSPERPAGE; 
	this.MAXSEARCHRESULTS = ZaSettings.MAXSEARCHRESULTS;
}

ZaListViewController.prototype = new ZaController();
ZaListViewController.prototype.constructor = ZaListViewController;



ZaListViewController.prototype._nextPageListener = 
function (ev) {
	if(this._currentPageNum < this.numPages) {
		this._currentPageNum++;
		this.show();
	} 
}

ZaListViewController.prototype._prevPageListener = 
function (ev) {
	if(this._currentPageNum > 1) {
		this._currentPageNum--;
		/*if(this.pages[this._currentPageNum]) {
			this.show(this.pages[this._currentPageNum])
		} else {*/
			this.show();
		//}
	} 
}

/**
* @return ZaItemList - the list currently displaid in the list view
**/
ZaListViewController.prototype.getList = 
function() {
	return this._list;
}

ZaListViewController.prototype._updateUI = 
function(list, openInNewTab, openInSearchTab, hasMore) {
    if (!this._UICreated) {
		this._createUI(openInNewTab, openInSearchTab);
	} 
	if (list) {
		var tmpArr = new Array();
		var cnt = list.getArray().length;
		for(var ix = 0; ix < cnt; ix++) {
			tmpArr.push(list.getArray()[ix]);
		}
		if(cnt < 1) {
			//if the list is empty - go to the previous page
		}
		//add the default column sortable
		this._contentView._bSortAsc = (this._currentSortOrder=="1");
		this._contentView.set(AjxVector.fromArray(tmpArr), this._contentView._defaultColumnSortable);
        this._contentView.setScrollSearchParams(this.scrollSearchParams);
        this._contentView.setScrollHasMore(hasMore);
	}
	this._removeList = new Array();
	this.changeActionsState();

}

ZaListViewController.prototype.closeButtonListener =
function(ev, noPopView, func, obj, params) {
	if (noPopView) {
		func.call(obj, params) ;
	} else{
		ZaApp.getInstance().popView () ;
	}
	this._UICreated = false;
	if(this._contentView) {
		this._contentView.dispose();
		this._contentView = null;
	}
	
	if(this._actionMenu) {
		this._actionMenu.dispose();
		this._actionMenu = null;
	}
	this._popupOperations = [];
}

ZaListViewController.prototype.multipleSearchCallback =
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
		controller:this,
		busyMsg:ZaMsg.BUSY_REQUESTING_ACCESS_RIGHTS
	}
	
	var respObj = ZaRequestMgr.invoke(params, reqMgrParams);
    //Count statistics that will show in search tree
    var resultStats;
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
			this._searchTotal = 0;
			this._list = null;
            var hasmore=false;
			for(var i = 0; i < cnt2; i++) {
				resp = batchResp.SearchDirectoryResponse[i];
				var subList = new ZaItemList(preParams.CONS);

		                subList.loadFromJS(resp);
				//combine the search results
				if(!this._list) this._list = subList;
				else {
					if(this._list instanceof ZaItemList && subList.size() > 0) {
						var listVec = subList.getVector();
						for(var j = 0; j < listVec.size(); j++) {
							var item = listVec.get(j);
							if(!this._list.getVector().contains(item))
								this._list.add(item);
						}
					}
				}
	            hasmore= resp.more|hasmore;
				this._searchTotal += resp.searchTotal;
                resultStats = this.getSearchResultStats(resp, resultStats);
			}
		        if(ZaZimbraAdmin.currentAdminAccount.attrs[ZaAccount.A_zimbraIsAdminAccount] != 'TRUE') {
		                var act = new AjxTimedAction(this._list, ZaItemList.prototype.loadEffectiveRights, null);
		                AjxTimedAction.scheduleAction(act, 150)
		        }

		        var limit = preParams.limit ? preParams.limit : this.RESULTSPERPAGE;
		        this.numPages = Math.ceil(this._searchTotal/preParams.limit);

                        if(preParams.show)
                                this._show(this._list, preParams.openInNewTab, preParams.openInSearchTab,hasmore,preParams.isShowBubble);
                        else
                                this._updateUI(this._list, preParams.openInNewTab, preParams.openInSearchTab,hasmore);

		}
	}
    ZaZimbraAdmin.getInstance().getOverviewPanelController().fireSearchEvent(resultStats);
}

ZaListViewController.prototype.getAppBarAction =
function () {
    if (AjxUtil.isEmpty(this._appbarOperation)) {
    	this._appbarOperation[ZaOperation.HELP]=new ZaOperation(ZaOperation.HELP,ZaMsg.TBB_Help, ZaMsg.TBB_Help_tt, "Help", "Help", new AjxListener(this, this._helpButtonListener));
    }

    return this._appbarOperation;
}

ZaListViewController.prototype.getAppBarOrder =
function () {
    if (AjxUtil.isEmpty(this._appbarOrder)) {
    	this._appbarOrder.push(ZaOperation.HELP);
    }

    return this._appbarOrder;
}


ZaListViewController.prototype.searchCallback =
function(params, resp) {
	try {
		if(params.busyId)
			ZaApp.getInstance().getAppCtxt().getShell().setBusy(false, params.busyId);
			
		if(!resp && !this._currentRequest.cancelled) {
			throw(new AjxException(ZaMsg.ERROR_EMPTY_RESPONSE_ARG, AjxException.UNKNOWN, "ZaListViewController.prototype.searchCallback"));
		}

        var resultStats;
		if(resp && resp.isException() && !this._currentRequest.cancelled) {
			ZaSearch.handleTooManyResultsException(resp.getException(), "ZaListViewController.prototype.searchCallback");
            this._list = new ZaItemList(params.CONS);

            this._searchTotal = 0;
			this.numPages = 0;
			if(params.show)
				this._show(this._list);			
			else
				this._updateUI(this._list);
		} else {
			ZaSearch.TOO_MANY_RESULTS_FLAG = false;
			this._list = null;
            var tempList = new ZaItemList(params.CONS);
			this._searchTotal = 0;
			if(resp && !resp.isException()) {
				var response = resp.getResponse().Body.SearchDirectoryResponse;

				this._list = new ZaItemList(params.CONS);

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
                                this._list.add(item);
                                break;
                            }
                        }

                    }
                } else  this._list = tempList;

				if(ZaZimbraAdmin.currentAdminAccount.attrs[ZaAccount.A_zimbraIsAdminAccount] != 'TRUE') {
					var act = new AjxTimedAction(this._list, ZaItemList.prototype.loadEffectiveRights, null);
					AjxTimedAction.scheduleAction(act, 150)
				}

				this._searchTotal = response.searchTotal;
				var limit = params.limit ? params.limit : this.RESULTSPERPAGE; 
				this.numPages = Math.ceil(this._searchTotal/params.limit);
                resultStats = this.getSearchResultStats(response);
			}
			if(params.show)
				this._show(this._list, params.openInNewTab, params.openInSearchTab,response.more,params.isShowBubble);
			else
				this._updateUI(this._list, params.openInNewTab, params.openInSearchTab,response.more);
		}
        ZaZimbraAdmin.getInstance().getOverviewPanelController().fireSearchEvent(resultStats);
	} catch (ex) {
		if (ex.code != ZmCsfeException.MAIL_QUERY_PARSE_ERROR) {
			this._handleException(ex, "ZaListViewController.prototype.searchCallback");	
		} else {
			this.popupErrorDialog(ZaMsg.queryParseError, ex);
			if(this._searchField)
				this._searchField.setEnabled(true);	
		}		
	}
}

/**
 * Get the count statistics of the search result.
 * @param resp response
 * @param orig Optional. The count statics will be added to <code>orig</code> if it is provided.
 * It is used for batch request.
 */
ZaListViewController.prototype.getSearchResultStats =
function(resp, orig) {
    var result = {};
    if (orig) {
        result = orig;
    }

    if (!resp || !resp.searchTotal) {
        return result;
    }

    if (result.searchTotal) {
        result.searchTotal += resp.searchTotal;
    } else {
        result.searchTotal = resp.searchTotal;
    }
    return result;
}

/**
* @param ev
* This listener is invoked by any other controller that can change an object in this controller
**/
ZaListViewController.prototype.handleChange = 
function (ev) {
	if(ev && this.objType && ev.type==this.objType) {
		if(ev.getDetails() && this._UICreated) {
			this.show(false);			
		}
	}
	ZaZimbraAdmin.getInstance().getOverviewPanelController().refreshRelatedTreeByEdit (ev.getDetails())
}

/**
* @param ev
* This listener is invoked by any other controller that can create an object in the view controlled by this controller
**/
ZaListViewController.prototype.handleCreation = 
function (ev) {
	if(ev && this.objType && ev.type==this.objType) {
		if(ev.getDetails() && this._UICreated) {
			this.show(false);			
		}
	}
    ZaZimbraAdmin.getInstance().getOverviewPanelController().refreshRelatedTree (ev.getDetails());
}

/**
* @param ev
* This listener is invoked by any other controller that can remove an object form the view controlled by this controller
**/
ZaListViewController.prototype.handleRemoval = 
function (ev) {
	if(ev &&  this.objType && ev.type==this.objType) {
		if(ev.getDetails() && this._UICreated) {
			this._currentPageNum = 1 ; //due to bug 12091, always go back to the first page after the deleting of items.
			this.show(false);			
		}
		ZaZimbraAdmin.getInstance().refreshHistoryTreeByDelete(ev.getDetails());
	}
}

ZaListViewController.prototype.setPageNum = 
function (pgnum) {
	this._currentPageNum = Number(pgnum);
}

ZaListViewController.prototype.getPageNum = 
function () {
	return this._currentPageNum;
}

