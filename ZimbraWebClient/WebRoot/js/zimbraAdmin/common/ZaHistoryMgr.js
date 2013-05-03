/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2011, 2012 VMware, Inc.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.3 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * 
 * ***** END LICENSE BLOCK *****
 */
/**
 * Created by IntelliJ IDEA.
 * User: mingzhang
 * Date: 9/19/11
 * Time: 11:27 PM
 * To change this template use File | Settings | File Templates.
 */
ZaHistoryMgr = function() {
    this._history = new AjxVector();
    this._historyObj = new AjxVector();
    this._evtMgr = new AjxEventMgr();
    this._currentLocation = 0;
}

ZaHistoryMgr.MAX_HISTROY_OBJ = 5;

ZaHistoryMgr.prototype.toString =
function() {
    return "ZaHistoryMgr";
}

ZaHistoryMgr.prototype.constructor = ZaHistoryMgr;

ZaHistoryMgr.prototype.addHistory =
function (historyObject) {
    var last = this._history.getLast();
    if (last && last.equal(historyObject))
        return;
    if (window.console && window.console.log)
        window.console.log("Add History " + historyObject.path);
    this._history.add(historyObject);
    // Here we always relocates the currentLocation to the current page.
    this._currentLocation = this._history.size() -1;
    this._evtMgr.notifyListeners(ZaEvent.L_MODIFY, this._history);
}

ZaHistoryMgr.prototype.findHistoryByName=
function(displayName){
    var obj = new AjxVector();
    var num = 0;
    for(var i = this._history.size()-1; i >= 0 && num < 20; i--) {
        if(!this._history.get(i).isShowInHistory)
                continue;

        if(this._history.get(i).displayName == displayName )
            obj.add(this._history.get(i));
        num++;
    }
    return obj;
}

ZaHistoryMgr.prototype.deleteHistoryObjByName =
function(displayName){
    for(var i = 0; i < this._historyObj.size(); i++) {
        if(this._historyObj.get(i).displayName == displayName )
            return this._historyObj.removeAt(i);
    }
}

ZaHistoryMgr.prototype.refreshHistory =
function () {
    this._evtMgr.notifyListeners(ZaEvent.L_MODIFY, this._history);
}

ZaHistoryMgr.prototype.addHistoryObj =
function (historyObject) {
    if (AjxUtil.isEmpty(historyObject.type))
        return;

    if (!ZaHistoryMgr.isAllowedRecentedObject(historyObject.path))
        return;

    var lastObj = this._historyObj.get(this._historyObj.size() - 1);
    if(lastObj && lastObj.path == historyObject.path)
        return;
    var loc =  this.findHistoryObj(historyObject);
    if(loc > -1) this._historyObj.removeAt(loc);

    if(this._historyObj.size()+1 > ZaHistoryMgr.MAX_HISTROY_OBJ) {
        this._historyObj.removeAt(0);
    }
    this._historyObj.add(historyObject);
}

ZaHistoryMgr.prototype.findHistoryObj =
function(historyObject) {
    if(!historyObject) return -1;
    for(var i = 0; i < this._historyObj._array.length; i++) {
        if(this._historyObj._array[i].displayName == historyObject.displayName)
            return i;
    }
    return -1;
}

ZaHistoryMgr.isAllowedRecentedObject =
function (srcPath) {
    var ret = false;
    var srcPathArr = ZaTree.getPathItems(srcPath);
    var destPathArr;
    var allowedObject= ZaHistoryMgr.getAllowedRecenctObject();
    for (var i = 0; i < allowedObject.length; i++ ) {
        destPathArr =  ZaTree.getPathItems( allowedObject[i]);
        for (var j = 0; j < destPathArr.length; j++) {
            if (srcPathArr[j] != destPathArr[j]) {
                break;
            }
        }

        if (j == destPathArr.length) {
            ret = true;
            break;
        }
    }
    return ret;
}

ZaHistoryMgr.getAllowedRecenctObject = function() {
    if (!ZaHistoryMgr._allowedRecentObject) {

        ZaHistoryMgr._allowedRecentObject = [
            [ZaMsg.OVP_home, ZaMsg.OVP_manageAccounts].join(ZaTree.SEPERATOR),
            [ZaMsg.OVP_home, ZaMsg.OVP_configure].join(ZaTree.SEPERATOR)
        ];
    }
    return ZaHistoryMgr._allowedRecentObject;
}
ZaHistoryMgr.prototype.removeHistory =
function() {
    var currentSize = this._history.size();
    if (currentSize == 1)
        return false;
    for (var i = 1; i < currentSize; i++)
		this._history._array[i] = null;
    this._history._array.length = 1;
    this._currentLocation = 0;
    this._evtMgr.notifyListeners(ZaEvent.L_MODIFY, this._history);
    return true;
}

ZaHistoryMgr.prototype.getAllHistory =
function() {
    return this._history;
}

ZaHistoryMgr.prototype.getAllHistoryObj =
function() {
    return this._historyObj;
}

ZaHistoryMgr.prototype.getPrevious =
function () {
    if (!this.isPrevious())
        return "";
    return this._history.get(--this._currentLocation);
}

ZaHistoryMgr.prototype.getNext =
function () {
    if (!this.isNext())
        return "";
    return this._history.get(++this._currentLocation);
}

ZaHistoryMgr.prototype.isPrevious =
function () {
    if (this._currentLocation == 0 )
        return false;
    else
        return true;
}

ZaHistoryMgr.prototype.isNext =
function () {
    if (this._currentLocation == this._history.size() - 1 )
        return false;
    else
        return true;
}

ZaHistoryMgr.prototype.getLatestHistory =
function () {
    return  this._history.getLast();
}

ZaHistoryMgr.prototype.getCurrentyHistory =
function () {
    return  this._history.get(this._currentLocation);
}

ZaHistoryMgr.prototype.addChangeListener =
function(listener) {
	return this._evtMgr.addListener(ZaEvent.L_MODIFY, listener);
}

ZaHistoryMgr.prototype.removeChangeListener =
function(listener) {
	return this._evtMgr.removeListener(ZaEvent.L_MODIFY, listener);
}

/*
 * Path: current path of item
 * dispalyName: name showed in Tree Header
 * type: This is attribute used for related object and recent object account/dl...etc
 * isShowInHistory: is show this history in Tree Header
 */
ZaHistory = function (path, displayName, type, isShowInHistory, viewMethod) {
    this.path = path;
    this.displayName = displayName;
    this.type = type;
    this.isShowInHistory = (isShowInHistory === undefined) ? true: isShowInHistory;
    this.viewMethod = (viewMethod instanceof AjxCallback) ? viewMethod : new AjxCallback(this, this.defaultGoToView);
    this.enabled = true;
}

ZaHistory.prototype.goToView =
function(refresh) {
    if (this.viewMethod) {
        this.viewMethod.run(refresh);
    }
}

ZaHistory.prototype.defaultGoToView = function(refresh) {
    var tree = ZaZimbraAdmin.getInstance().getOverviewPanelController().getOverviewPanel().getFolderTree();
    tree.setSelectionByPath(this.path, false, undefined, undefined, undefined, refresh);
}

ZaHistory.prototype.equal = function (newObj) {
    return this.path == newObj.path;
}

ZaHistory.prototype.setEnabled =
function(enabled){
    this.enabled = enabled;
}

ZaHistory.prototype.getEnabled =
function(){
    return this.enabled;
}