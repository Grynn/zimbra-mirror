/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2013 VMware, Inc.
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
 * <p>Abstract class from which <b><code>ALL</code></b> model classes inherit. Defines the
 * basic functions and provides the necessary default values.</p>
 *
 * @constructor
 * @class
 *
 * @author Mohammed Shaik Hussain Ali
 *
 * @this {ZaBaseModel}
 *
 * @param init
 *
 */
ZaBaseModel = function(init) {
    // Needed to make the class inheritable
    if (arguments.length == 0) {
        return;
    }

    this._eventManager = new AjxEventMgr();
}

ZaBaseModel.prototype.isZaBaseModel = true;

ZaBaseModel.prototype.toString = function() {
    return "ZaBaseModel";
}

ZaBaseModel.prototype.addChangeListener = function(listener) {
    return this._eventManager.addListener(ZaEvent.L_MODIFY, listener);
}

ZaBaseModel.prototype.removeChangeListener = function(listener) {
    return this._eventManager.removeListener(ZaEvent.L_MODIFY, listener);
}
