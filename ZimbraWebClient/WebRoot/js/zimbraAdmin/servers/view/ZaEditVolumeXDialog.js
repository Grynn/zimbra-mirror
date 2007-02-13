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
* @class ZaEditVolumeXDialog
* @contructor ZaEditVolumeXDialog
* @author Greg Solovyev
* @param parent
* param app
**/
function ZaEditVolumeXDialog(parent,  app, w, h, title) {
	if (arguments.length == 0) return;
	this._standardButtons = [DwtDialog.OK_BUTTON, DwtDialog.CANCEL_BUTTON];	
	ZaXDialog.call(this, parent, app, null, title, w, h);
	this._containedObject = {};
	this.initForm(ZaServer.volumeObjModel,this.getMyXForm());
}

ZaEditVolumeXDialog.prototype = new ZaXDialog;
ZaEditVolumeXDialog.prototype.constructor = ZaEditVolumeXDialog;

ZaEditVolumeXDialog.prototype.getMyXForm = 
function() {	
	var xFormObject = {
		numCols:1,
		items:[
			{type:_ZAWIZGROUP_, 
				items:[
					{ref:ZaServer.A_VolumeName, type:_TEXTFIELD_, label:ZaMsg.VM_VolumeName+":", labelLocation:_LEFT_, width:250},
					{ref:ZaServer.A_VolumeRootPath, type:_TEXTFIELD_, label:ZaMsg.VM_VolumeRootPath+":", labelLocation:_LEFT_, width:250},
					{ref:ZaServer.A_VolumeType, type:_OSELECT1_, choices:ZaServer.volumeTypeChoices,width:250, label:ZaMsg.VM_VolumeType+":"},
					{ref:ZaServer.A_VolumeCompressBlobs,
						type:_ZA_CHECKBOX_, label:ZaMsg.VM_VolumeCompressBlobs,
						trueValue:true, falseValue:false
					},
					{type:_GROUP_,numCols:3,colSpan:2,colSizes:["200px","150px","125px"],
						items:[
							{ref:ZaServer.A_VolumeCompressionThreshold, type:_TEXTFIELD_, label:ZaMsg.VM_VolumeCompressThreshold+":", labelLocation:_LEFT_},
							{type:_OUTPUT_,label:null,labelLocation:_NONE_,value:ZaMsg.NAD_bytes,align:_LEFT_}
						]
					}
					
				]
			}
		]
	};
	return xFormObject;
}
