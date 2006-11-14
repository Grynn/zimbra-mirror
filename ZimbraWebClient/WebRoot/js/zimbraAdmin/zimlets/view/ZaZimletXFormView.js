/*
 * ***** BEGIN LICENSE BLOCK *****
 * Version: ZPL 1.2
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.2 ("License"); you may not use this file except in
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
 * Portions created by Zimbra are Copyright (C) 2006 Zimbra, Inc.
 * All Rights Reserved.
 * 
 * Contributor(s):
 * 
 * ***** END LICENSE BLOCK *****
 */

/**
* This class describes a view of a single Zimlet
* @class ZaZimletXFormView
* @contructor
* @param parent {DwtComposite}
* @param app {ZaApp}
* @author Greg Solovyev
**/
function ZaZimletXFormView (parent, app) {
	ZaTabView.call(this, parent, app, "ZaZimletXFormView");	
	this.initForm(ZaZimlet.myXModel,this.getMyXForm());
}

ZaZimletXFormView.prototype = new ZaTabView();
ZaZimletXFormView.prototype.constructor = ZaZimletXFormView;
ZaTabView.XFormModifiers["ZaZimletXFormView"] = new Array();
ZaZimletXFormView.TAB_INDEX=0;

/**
* Sets the object contained in the view
* @param entry - {ZaZimlet} object to display
**/
ZaZimletXFormView.prototype.setObject =
function(entry) {
	this._containedObject = new Object();
	this._containedObject.attrs = new Object();


	for (var a in entry.attrs) {
		if(entry.attrs[a] instanceof Array) {
			this._containedObject.attrs[a] = new Array();
			for(var aa in entry.attrs[a]) {
				this._containedObject.attrs[a][aa] = entry.attrs[a][aa];
			}
		} else {
			this._containedObject.attrs[a] = entry.attrs[a];
		}
	}
	this._containedObject.name = entry.name;
	if(entry.id)
		this._containedObject.id = entry.id;
}
ZaZimletXFormView.myXFormModifier = function(xFormObject) {	
};
ZaTabView.XFormModifiers["ZaZimletXFormView"].push(ZaZimletXFormView.myXFormModifier);