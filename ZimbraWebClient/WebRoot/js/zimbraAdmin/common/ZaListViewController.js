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
function(list, openInNewTab, openInSearchTab) {
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
	}
	this._removeList = new Array();
	this.changeActionsState();
	
	var s_result_start_n = (this._currentPageNum - 1) * this.RESULTSPERPAGE + 1;
	var s_result_end_n = this._currentPageNum  * this.RESULTSPERPAGE;
	if(this.numPages <= this._currentPageNum) {
		s_result_end_n = this._searchTotal ;
		this._toolbar.enable([ZaOperation.PAGE_FORWARD], false);
	} else {
		this._toolbar.enable([ZaOperation.PAGE_FORWARD], true);
	}
	if(this._currentPageNum == 1) {
		this._toolbar.enable([ZaOperation.PAGE_BACK], false);
	} else {
		this._toolbar.enable([ZaOperation.PAGE_BACK], true);
	}
	
	//update the search result number count now
	var srCountBt = this._toolbar.getButton (ZaOperation.SEARCH_RESULT_COUNT) ;
	if (srCountBt ) {
		if  (this._searchTotal == 0) {
			s_result_end_n = 0;
			s_result_start_n = 0;
		}
		srCountBt.setText ( AjxMessageFormat.format (ZaMsg.searchResultCount, 
				[s_result_start_n + " - " + s_result_end_n, this._searchTotal]));
	}
}

ZaListViewController.prototype.closeButtonListener =
function(ev, noPopView, func, obj, params) {
	if (noPopView) {
		func.call(obj, params) ;
	} else{
		ZaApp.getInstance().popView () ;
	}
	this._UICreated = false;
	if(this._toolbar) {
		this._toolbar.dispose();
		this._toolbar = null;
	}
	if(this._contentView) {
		this._contentView.dispose();
		this._contentView = null;
	}
	
	if(this._actionMenu) {
		this._actionMenu.dispose();
		this._actionMenu = null;
	}
	this._toolbarOperations = [];
	this._popupOperations = [];
	this._toolbarOrder = [];	
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
	
				this._searchTotal += resp.searchTotal;
			}
		        if(ZaZimbraAdmin.currentAdminAccount.attrs[ZaAccount.A_zimbraIsAdminAccount] != 'TRUE') {
		                var act = new AjxTimedAction(this._list, ZaItemList.prototype.loadEffectiveRights, null);
		                AjxTimedAction.scheduleAction(act, 150)
		        }

		        var limit = preParams.limit ? preParams.limit : this.RESULTSPERPAGE;
		        this.numPages = Math.ceil(this._searchTotal/preParams.limit);

                        if(preParams.show)
                                this._show(this._list, preParams.openInNewTab, preParams.openInSearchTab);
                        else
                                this._updateUI(this._list, preParams.openInNewTab, preParams.openInSearchTab);

		}
	}
}



ZaListViewController.prototype.searchCallback =
function(params, resp) {
	try {
		if(params.busyId)
			ZaApp.getInstance().getAppCtxt().getShell().setBusy(false, params.busyId);
			
		if(!resp && !this._currentRequest.cancelled) {
			throw(new AjxException(ZaMsg.ERROR_EMPTY_RESPONSE_ARG, AjxException.UNKNOWN, "ZaListViewController.prototype.searchCallback"));
		}
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
			this._list = new ZaItemList(params.CONS);
			this._searchTotal = 0;
			if(resp && !resp.isException()) {
				var response = resp.getResponse().Body.SearchDirectoryResponse;
				this._list.loadFromJS(response);
				if(ZaZimbraAdmin.currentAdminAccount.attrs[ZaAccount.A_zimbraIsAdminAccount] != 'TRUE') {
					var act = new AjxTimedAction(this._list, ZaItemList.prototype.loadEffectiveRights, null);
					AjxTimedAction.scheduleAction(act, 150)
				}	
				this._searchTotal = response.searchTotal;
				var limit = params.limit ? params.limit : this.RESULTSPERPAGE; 
				this.numPages = Math.ceil(this._searchTotal/params.limit);
			}
			if(params.show)
				this._show(this._list, params.openInNewTab, params.openInSearchTab);			
			else
				this._updateUI(this._list, params.openInNewTab, params.openInSearchTab);
		}
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

