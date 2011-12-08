/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2007, 2008, 2009, 2010 Zimbra, Inc.
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
* @class ZaEditVolumeXDialog
* @contructor ZaEditVolumeXDialog
* @author Greg Solovyev
* @param parent
* param app
**/
ZaEditVolumeXDialog = function(parent, w, h, title) {
	if (arguments.length == 0) return;
	this._standardButtons = [DwtDialog.OK_BUTTON, DwtDialog.CANCEL_BUTTON];	
	ZaXDialog.call(this, parent,null, title, w, h);
	this._containedObject = {};
	this.initForm(ZaServer.volumeObjModel,this.getMyXForm());
	this._helpURL = ZaEditVolumeXDialog.helpURL;
}

ZaEditVolumeXDialog.prototype = new ZaXDialog;
ZaEditVolumeXDialog.prototype.constructor = ZaEditVolumeXDialog;
ZaEditVolumeXDialog.helpURL = location.pathname + ZaUtil.HELP_URL + "managing_servers/adding_a_new_storage_volume_to_the_server.htm?locid="+AjxEnv.DEFAULT_LOCALE;

ZaEditVolumeXDialog.prototype.getMyXForm = 
function() {	
	var xFormObject = {
		numCols:1,
		items:[
			{type:_ZAWIZGROUP_, isTabGroup:true,
				items:[
					{ref:ZaServer.A_VolumeName, type:_TEXTFIELD_, label:ZaMsg.LBL_VM_VolumeName, labelLocation:_LEFT_, width:250, visibilityChecks:[],enableDisableChecks:[]},
					{ref:ZaServer.A_VolumeRootPath, type:_TEXTFIELD_, label:ZaMsg.LBL_VM_VolumeRootPath, labelLocation:_LEFT_, width:250, visibilityChecks:[],enableDisableChecks:[]},
					{ref:ZaServer.A_VolumeType, type:_OSELECT1_, choices:ZaServer.volumeTypeChoices,width:250, label:ZaMsg.LBL_VM_VolumeType, visibilityChecks:[],enableDisableChecks:[]},
					{ref:ZaServer.A_VolumeCompressBlobs,
						type:_WIZ_CHECKBOX_, label:ZaMsg.VM_VolumeCompressBlobs,
						trueValue:true, falseValue:false, visibilityChecks:[],enableDisableChecks:[]
					},
					{type:_GROUP_,numCols:3,colSpan:2,colSizes:["200px","150px","125px"],
						items:[
							{ref:ZaServer.A_VolumeCompressionThreshold, type:_TEXTFIELD_, label:ZaMsg.LBL_VM_VolumeCompressThreshold, labelLocation:_LEFT_, visibilityChecks:[],enableDisableChecks:[]},
							{type:_OUTPUT_,label:null,labelLocation:_NONE_,value:ZaMsg.NAD_bytes,align:_LEFT_, visibilityChecks:[],enableDisableChecks:[]}
						]
					}
					
				]
			}
		]
	};
	return xFormObject;
}
