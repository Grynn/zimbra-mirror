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
function ZaListViewController(appCtxt, container, app, iKeyName) {
	if (arguments.length == 0) return;
	this._currentObject = null;
	ZaController.call(this, appCtxt, container, app, iKeyName);
}

ZaListViewController.prototype = new ZaController();
ZaListViewController.prototype.constructor = ZaListViewController;

ZaListViewController.prototype._nextPageListener = 
function (ev) {
	if(this._currentPageNum < this.pages[this._currentPageNum].numPages) {
		this._currentPageNum++;
		if(this.pages[this._currentPageNum]) {
			this.show(this.pages[this._currentPageNum])
		} else {
			this.show(ZaSearch.searchByQueryHolder(this._currentQuery,this._currentPageNum, this._currentSortField, this._currentSortOrder, this._app));	
		}
	} 
}

ZaListViewController.prototype._prevPageListener = 
function (ev) {
	if(this._currentPageNum > 1) {
		this._currentPageNum--;
		if(this.pages[this._currentPageNum]) {
			this.show(this.pages[this._currentPageNum])
		} else {
			this.show(ZaSearch.searchByQueryHolder(this._currentQuery,this._currentPageNum, this._currentSortField, this._currentSortOrder, this._app));
		}
	} 
}