/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2005, 2006, 2007, 2009, 2010 Zimbra, Inc.
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
 * static class that wraps around AjxEventManager
 * 
 * @private
 */
DwtEventManager = function() {
};

DwtEventManager._instance = new AjxEventMgr();

DwtEventManager._domEventToDwtMap = {
	'ondblclick': DwtEvent.ONDBLCLICK,
	'onmousedown': DwtEvent.ONMOUSEDOWN ,
	'onmouseup': DwtEvent.ONMOUSEUP,
	'onmousemove': DwtEvent.ONMOUSEMOVE,
	'onmouseout': DwtEvent.ONMOUSEOUT,
	'onmouseover': DwtEvent.ONMOUSEOVER,
	'onselectstart': DwtEvent.ONSELECTSTART,
	'onchange': DwtEvent.ONCHANGE
};

DwtEventManager.addListener = 
function(eventType, listener) {
	DwtEventManager._instance.addListener(eventType, listener);
};

DwtEventManager.notifyListeners = 
function(eventType, event) {
	DwtEventManager._instance.notifyListeners(eventType, event);
};

DwtEventManager.removeListener = 
function(eventType, listener) {
	DwtEventManager._instance.removeListener(eventType, listener);
};
