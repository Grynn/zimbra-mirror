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
* @class ZaPosixGroupXFormView
* @contructor
* @param parent
* @param app
* @author Greg Solovyev
**/
function ZaPosixGroupXFormView (parent, app) {
	ZaTabView.call(this, parent, app,"ZaPosixGroupXFormView");	
		
	this.initForm(ZaPosixGroup.myXModel,this.getMyXForm());
	this._localXForm.setController(this._app);
}

ZaPosixGroupXFormView.prototype = new ZaTabView();
ZaPosixGroupXFormView.prototype.constructor = ZaPosixGroupXFormView;
ZaTabView.XFormModifiers["ZaPosixGroupXFormView"] = new Array();


ZaPosixGroupXFormView.prototype.setObject = 
function (entry) {
	//this._localXForm.setInstance(new ZaBackup(this._app));
	this._containedObject = new ZaPosixGroup(this._app);
	this._containedObject.attrs = new Object();

	for(var a in entry) {
		if(typeof(entry[a])=="object") 
			continue;
		else
			this._containedObject[a] = entry[a];
	}

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

ZaPosixGroupXFormView.prototype.getTitle = 
function () {
	return "Posix Groups";
}

ZaPosixGroupXFormView.myXFormModifier = function(xFormObject) {	
	xFormObject.tableCssStyle="width:100%;overflow:auto;";
	
	xFormObject.items = [
		{type:_GROUP_, cssClass:"ZmSelectedHeaderBg", colSpan: "*", id:"xform_header", 
			items: [
				{type:_GROUP_,	numCols:4,colSizes:["32px","350px","100px","250px"],
					items: [
						{type:_AJX_IMAGE_, src:"Domain_32", label:null},
						{type:_OUTPUT_, ref:ZaPosixGroup.A_PosixGroupName, label:null,cssClass:"AdminTitle", rowSpan:2},				
						{type:_OUTPUT_, ref:ZaPosixGroup.A_sambaSID, label:"sambaSID"}
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
						{ ref: ZaPosixGroup.A_cn, type:_TEXTFIELD_, 
						  label:"Name:",onChange:ZaTabView.onFormFieldChanged
						},
						{ref: ZaPosixGroup.A_gidNumber, type:_TEXTFIELD_, 
						  	label:"gidNumber", cssClass:"admin_xform_number_input",
						  	onChange:ZaTabView.onFormFieldChanged,
							getDisplayValue:function () {
								var val = this.getInstanceValue();
								if(!val) {
									val = ZaPosixGroup.getNextGid();
									this.setInstanceValue(val);
								}	
								return val;
							}						  
					  	},
						{ref:ZaPosixGroup.A_description, type:_TEXTFIELD_, msgName:ZaMsg.NAD_Description,
							label:ZaMsg.NAD_Description, labelLocation:_LEFT_,
							cssClass:"admin_xform_name_input", 
							onChange:ZaTabView.onFormFieldChanged
						},
						{ref:ZaPosixGroup.A_memberUid,
							type:_REPEAT_,
							label:ZaPosixGroup.A_memberUid,
							labelLocation:_LEFT_, 
							align:_LEFT_,
							repeatInstance:"0", 
							showAddButton:true, 
							showRemoveButton:true, 
							showAddOnNextRow:true, 
							items: [
								{ref:".", type:_TEXTFIELD_, label:null, onChange:ZaTabView.onFormFieldChanged,cssClass:"admin_xform_number_input"}
							],
							onRemove:ZaAccountXFormView.onRepeatRemove,
						 	relevantBehavior: _HIDE_									
						}						
					]
				}
			]
		}	
	];
}

ZaTabView.XFormModifiers["ZaPosixGroupXFormView"].push(ZaPosixGroupXFormView.myXFormModifier);