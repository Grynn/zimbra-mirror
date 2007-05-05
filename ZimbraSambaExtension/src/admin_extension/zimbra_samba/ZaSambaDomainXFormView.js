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
* @class ZaSambaDomainXFormView
* @contructor
* @param parent
* @param app
* @author Greg Solovyev
**/
function ZaSambaDomainXFormView (parent, app) {
	ZaTabView.call(this, parent, app,"ZaSambaDomainXFormView");	
		
	this.initForm(ZaSambaDomain.myXModel,this.getMyXForm());
	this._localXForm.setController(this._app);
}

ZaSambaDomainXFormView.prototype = new ZaTabView();
ZaSambaDomainXFormView.prototype.constructor = ZaSambaDomainXFormView;
ZaTabView.XFormModifiers["ZaSambaDomainXFormView"] = new Array();


ZaSambaDomainXFormView.prototype.setObject = 
function (entry) {
	//this._localXForm.setInstance(new ZaBackup(this._app));
	this._containedObject = new ZaSambaDomain(this._app);
	this._containedObject.attrs = new Object();

	if(entry.id)
		this._containedObject.id = entry.id;
		
	if(entry.name)
		this._containedObject.name = entry.name;

	for (var a in entry.attrs) {
		if(entry.attrs[a] instanceof Array) {
			this._containedObject.attrs[a] = new Array();
			var cnt = entry.attrs[a].length;
			for(var ix = 0; ix < cnt; ix++) {
				this._containedObject.attrs[a][ix]=entry.attrs[a][ix];
			}
		} else {
			this._containedObject.attrs[a] = entry.attrs[a];
		}
	}
	
	if(!entry[ZaModel.currentTab])
		this._containedObject[ZaModel.currentTab] = "1";
	else
		this._containedObject[ZaModel.currentTab] = entry[ZaModel.currentTab];
		
	this._localXForm.setInstance(this._containedObject);		
}

ZaSambaDomainXFormView.prototype.getTitle = 
function () {
	return "Samba Domains";
}

ZaSambaDomainXFormView.myXFormModifier = function(xFormObject) {	
	xFormObject.tableCssStyle="width:100%;overflow:auto;";
	
	xFormObject.items = [
		{type:_GROUP_, cssClass:"ZmSelectedHeaderBg", colSpan: "*", id:"xform_header", 
			items: [
				{type:_GROUP_,	numCols:4,colSizes:["32px","350px","100px","250px"],
					items: [
						{type:_AJX_IMAGE_, src:"Domain_32", label:null},
						{type:_OUTPUT_, ref:ZaSambaDomain.A_sambaDomainName, label:null,cssClass:"AdminTitle", rowSpan:2},				
						{type:_OUTPUT_, ref:ZaSambaDomain.A_sambaSID, label:"sambaSID"}
					]
				}
			],
			cssStyle:"padding-top:5px; padding-left:2px; padding-bottom:5px"
		},	
		{type:_TAB_BAR_,  ref:ZaModel.currentTab,
			choices:[
				{value:1, label:ZaMsg.Domain_Tab_General}				
			],cssClass:"ZaTabBar", id:"xform_tabbar"
		},
		{type:_SWITCH_, 
			items:[
				{type:_ZATABCASE_, relevant:"instance[ZaModel.currentTab] == 1", 
					colSizes:["250px","*"],
					items:[
						{ ref: ZaSambaDomain.A_sambaDomainName, type:_TEXTFIELD_, 
						  label:ZaMsg.Domain_DomainName,onChange:ZaTabView.onFormFieldChanged
						},
						{ ref: ZaSambaDomain.A_sambaSID, type:_TEXTFIELD_, 
						  label:"sambaSID", width:300,
						  onChange:ZaTabView.onFormFieldChanged
					  	},
						{ ref: ZaSambaDomain.A_sambaAlgorithmicRidBase, type:_TEXTFIELD_, 
						  label:"sambaAlgorithmicRidBase", cssClass:"admin_xform_number_input",
						  onChange:ZaTabView.onFormFieldChanged
					  	}						
					]
				}
			]
		}	
	];
}

ZaTabView.XFormModifiers["ZaSambaDomainXFormView"].push(ZaSambaDomainXFormView.myXFormModifier);