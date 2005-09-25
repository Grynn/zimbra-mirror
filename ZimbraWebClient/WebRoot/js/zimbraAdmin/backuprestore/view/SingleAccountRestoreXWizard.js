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
 * The Original Code is: Zimbra Collaboration Suite.
 * 
 * The Initial Developer of the Original Code is Zimbra, Inc.
 * Portions created by Zimbra are Copyright (C) 2005 Zimbra, Inc.
 * All Rights Reserved.
 * 
 * Contributor(s):
 * 
 * ***** END LICENSE BLOCK *****
 */

function SingleAccountRestoreXWizard (parent, app) {
	ZaXWizardDialog.call(this, parent, null, ZaMsg.Restore_WizTitle, "550px", "300px");
	this.addPage("Account Name"); //1
	this.addPage("Select Servers"); //2
	this.addPage("Looking for backup labels");	//3
	this.addPage("Select Labels");	//4
	this.addPage("Restoring");	//5
	this.addPage("Result");	//6
	this._app = app;
	this.initForm(ZaRestore.myXModel,this.getMyXForm());		
}

SingleAccountRestoreXWizard.prototype = new ZaXWizardDialog;
SingleAccountRestoreXWizard.prototype.constructor = SingleAccountRestoreXWizard;

SingleAccountRestoreXWizard.labelChoices = new XFormChoices([], XFormChoices.OBJECT_LIST, "label", "label");

/**
* Callback function invoked by Asynchronous CSFE command when "BackupQueryRequest" call returns
**/
SingleAccountRestoreXWizard.prototype.backupQueryCallBack = 
function (arg) {
	if(arg instanceof AjxException || arg instanceof ZmCsfeException || arg instanceof AjxSoapException) {
		this._containedObject[ZaModel.ErrorCode] = arg.code;
		this._containedObject[ZaModel.ErrorMessage] = arg.detail;
		this._button[DwtWizardDialog.NEXT_BUTTON].setEnabled(false);
		this._button[DwtWizardDialog.PREV_BUTTON].setEnabled(true);
	} else {
		var nodes = arg.getBody().firstChild.childNodes;
		if(!nodes || nodes.length == 0) {
			this._containedObject[ZaModel.ErrorCode] = -1;
			this._containedObject[ZaModel.ErrorMessage] = ZaMsg.Restore_NoLabelsFound;
			this._button[DwtWizardDialog.NEXT_BUTTON].setEnabled(false);
			this._button[DwtWizardDialog.PREV_BUTTON].setEnabled(true);
		} else {
			var accountNode = nodes[0];
			if(!accountNode.childNodes || accountNode.childNodes.length < 1) {
				this._containedObject[ZaModel.ErrorCode] = -1;
				this._containedObject[ZaModel.ErrorMessage] = ZaMsg.Restore_NoLabelsFound;
				this._button[DwtWizardDialog.NEXT_BUTTON].setEnabled(false);
				this._button[DwtWizardDialog.PREV_BUTTON].setEnabled(true);
			} else {
				this._containedObject[ZaModel.ErrorCode] = 0;
				var backups = new Array();
				var backupNodes = accountNode.childNodes;
				for (var i = 0; i < backupNodes.length; i++) {
					var item = new ZaBackup(this._app);
					item.label = backupNodes[i].getAttribute("label");
					item.server = accountNode.getAttribute("server");				
					//add the list as change listener to the item
					backups.push(item);
				}	
				SingleAccountRestoreXWizard.labelChoices.setChoices(backups); 
				SingleAccountRestoreXWizard.labelChoices.dirtyChoices();
				this._button[DwtWizardDialog.NEXT_BUTTON].setEnabled(true);
				this._button[DwtWizardDialog.PREV_BUTTON].setEnabled(true);
				this._button[DwtWizardDialog.NEXT_BUTTON].setText(ZaMsg.Restore_Restore);
			}
		}
	}
	this.goPage(4);
}

/**
* Callback function invoked by Asynchronous CSFE command when "RestoreRequest" call returns
**/
SingleAccountRestoreXWizard.prototype.restoreCallBack = 
function (arg) {
	if(arg instanceof AjxException || arg instanceof ZmCsfeException || arg instanceof AjxSoapException) {
		this._containedObject[ZaModel.ErrorCode] = arg.code;
		this._containedObject[ZaModel.ErrorMessage] = arg.detail;
		this._button[DwtWizardDialog.PREV_BUTTON].setEnabled(true);		
	} else {
		this._containedObject[ZaModel.ErrorCode] = 0;
		this._containedObject[ZaModel.ErrorMessage] = "";
		this._button[DwtWizardDialog.PREV_BUTTON].setEnabled(false);			
	}
	this._button[DwtWizardDialog.NEXT_BUTTON].setEnabled(false);
	this._button[DwtWizardDialog.FINISH_BUTTON].setEnabled(true);
	if(this._containedObject.restoreRequest[ZaRestore.A_restoreMethod] == ZaRestore.CA) {
		this._app.getAccountViewController()._fireAccountCreationEvent(null);
	}
	this.goPage(6);
}
/**
* @method setObject sets the object contained in the view
* @param entry - ZaRestore object to display
**/
SingleAccountRestoreXWizard.prototype.setObject =
function(entry) {
	this._containedObject = entry;
	this._containedObject.restoreRequest[ZaRestore.A_toServer] = this._app.getServerList().getArray()[0].id;
	this._containedObject[ZaModel.currentStep] = 1;
	this._localXForm.setInstance(this._containedObject);		
}
/**
* Overwritten methods that control wizard's flow (open, go next,go previous, finish)
**/
SingleAccountRestoreXWizard.prototype.popup = 
function (loc) {
	ZaXWizardDialog.prototype.popup.call(this, loc);
	this._button[DwtWizardDialog.NEXT_BUTTON].setEnabled(true);
	this._button[DwtWizardDialog.FINISH_BUTTON].setEnabled(false);
	this._button[DwtWizardDialog.PREV_BUTTON].setEnabled(false);	
}

SingleAccountRestoreXWizard.prototype.goNext = 
function() {
	
	if (this._containedObject[ZaModel.currentStep] == 1) {
		if(!this._containedObject.restoreRequest[ZaRestore.A_accountName] || this._containedObject.restoreRequest[ZaRestore.A_accountName].length < 3) {
			this._app.getCurrentController().popupMsgDialog(ZaMsg.ERROR_EMAIL_ADDR_REQUIRED);
			return false;			
		}
		this._button[DwtWizardDialog.PREV_BUTTON].setEnabled(true);
	} else if(this._containedObject[ZaModel.currentStep] == 2) { //query backups
		this._button[DwtWizardDialog.NEXT_BUTTON].setEnabled(false);
		this._button[DwtWizardDialog.PREV_BUTTON].setEnabled(false);
		this._button[DwtWizardDialog.FINISH_BUTTON].setEnabled(false);
		var accounts = new Array();
		accounts.push(this._containedObject.restoreRequest[ZaRestore.A_accountName]);
		try {
			ZaBackup.queryAccountBackup(this._containedObject.restoreRequest[ZaRestore.A_toServer], this._containedObject.restoreRequest[ZaRestore.A_target], accounts, new AjxCallback(this, this.backupQueryCallBack));	
		} catch (ex) {
			this._app.getCurrentController()._handleException(ex, "SingleAccountRestoreXWizard.prototype.goNext", null, false);
		}
	}  else if(this._containedObject[ZaModel.currentStep] == 4 ) { //restore
		this._button[DwtWizardDialog.NEXT_BUTTON].setEnabled(false);
		this._button[DwtWizardDialog.PREV_BUTTON].setEnabled(false);
		this._button[DwtWizardDialog.FINISH_BUTTON].setEnabled(false);
		var accounts = [this._containedObject.restoreRequest[ZaRestore.A_accountName]];
		try {
			ZaRestore.restoreAccount(this._containedObject.restoreRequest[ZaRestore.A_restoreMethod], 
			   this._containedObject.restoreRequest[ZaRestore.A_includeIncrementals], 
			   this._containedObject.restoreRequest[ZaRestore.A_label],
			   this._containedObject.restoreRequest[ZaRestore.A_target], 
			   this._containedObject.restoreRequest[ZaRestore.A_prefix], 
			   accounts, this._containedObject.restoreRequest[ZaRestore.A_toServer], 
			   new AjxCallback(this, this.restoreCallBack));
		} catch (ex) {
			this._app.getCurrentController()._handleException(ex, "SingleAccountRestoreXWizard.prototype.goNext", null, false);
		}		   
	}
	this.goPage(this._containedObject[ZaModel.currentStep] + 1);
}

SingleAccountRestoreXWizard.prototype.goPrev = 
function() {
	if (this._containedObject[ZaModel.currentStep] == 2) {
		this._button[DwtWizardDialog.PREV_BUTTON].setEnabled(false);
	} else if(this._containedObject[ZaModel.currentStep] == 4) {
		this._containedObject[ZaModel.currentStep]--;
		this._button[DwtWizardDialog.NEXT_BUTTON].setEnabled(true);
		this._button[DwtWizardDialog.FINISH_BUTTON].setEnabled(false);
		this._button[DwtWizardDialog.NEXT_BUTTON].setText(AjxMsg._next);
	} else if(this._containedObject[ZaModel.currentStep] == 6) {
		this._containedObject[ZaModel.currentStep]--;
		this._button[DwtWizardDialog.NEXT_BUTTON].setText(ZaMsg.Restore_Restore);
	}
	this.goPage(this._containedObject[ZaModel.currentStep] - 1);
}

SingleAccountRestoreXWizard.prototype.getMyXForm = function() {	
	var xFormObject = {
		items: [
			{type:_SWITCH_, width:450, align:_LEFT_, valign:_TOP_, 
				items:[
					{type:_CASE_, numCols:2, relevant:"instance[ZaModel.currentStep] == 1", align:_LEFT_, valign:_TOP_, 
						items:[
							{type:_OUTPUT_, value:ZaMsg.Restore_AccountName, colSpan:2},
							{ref:ZaRestore.A_accountName, type:_TEXTFIELD_, label:ZaMsg.Restore_EmailAddress+":", width:200}
						]
					}, 
					{type:_CASE_, numCols:2, relevant:"instance[ZaModel.currentStep] == 2",
						items: [
							{type:_OUTPUT_, value:ZaMsg.Restore_SelectServer, colSpan:2}, 
							{ref:ZaRestore.A_toServer, type:_OSELECT1_, label:ZaMsg.Restore_TargetServer, choices:this._app.getServerListChoices()},
							{type:_SPACER_, colSpan:2},
							{type:_OUTPUT_, value:ZaMsg.Restore_SelectPath, colSpan:2}, 							
							{ref:ZaRestore.A_target, type:_TEXTFIELD_, label:ZaMsg.Restore_TargetPath, width:500, labelLocation:_TOP_, colSpan:2}
						]
					},
					{type:_CASE_, numCols:2,relevant:"instance[ZaModel.currentStep] == 3", 
						items :[
							{type:_OUTPUT_, value:ZaMsg.Restore_LookingForLabels}
						]
					}, 
					{type:_CASE_, numCols:2, relevant:"instance[ZaModel.currentStep] == 4", //select label
						items: [
							{type:_SWITCH_,
								items: [
									{type:_CASE_, relevant:"instance[ZaModel.ErrorCode] == 0",
										items: [
											{ref:ZaRestore.A_label, type:_OSELECT1_, label:ZaMsg.Restore_Label, choices:SingleAccountRestoreXWizard.labelChoices},
											{ref:ZaRestore.A_includeIncrementals, type:_CHECKBOX_, label:ZaMsg.Restore_IncludeIncrementals, trueValue:"TRUE", falseValue:"FALSE", labelLocation:_LEFT_},
											{ref:ZaRestore.A_prefix, type:_TEXTFIELD_, label:ZaMsg.Restore_Prefix},
											{ref:ZaRestore.A_restoreMethod, type:_OSELECT1_, label:ZaMsg.Restore_method}
										]
									},
									{type:_CASE_, relevant:"instance[ZaModel.ErrorCode] != 0",
										items: [
											{type:_OUTPUT_, value:ZaMsg.Restore_LabelsProblem, relevant:"instance[ZaModel.ErrorCode] > 0"},
											{type:_OUTPUT_, ref:ZaModel.ErrorMessage}
										]
									}
								]
							}
						]
					},
					{type:_CASE_, numCols:2,relevant:"instance[ZaModel.currentStep] == 5", //restoring
						items :[
							{type:_OUTPUT_, value:ZaMsg.Restore_Restoring}
						]
					}, 
					{type:_CASE_, numCols:2,relevant:"instance[ZaModel.currentStep] == 6", 
						items :[
							{type:_SWITCH_,
								items: [
									{type:_CASE_, relevant:"instance[ZaModel.ErrorCode] == 0",
										items: [
											{type:_OUTPUT_, value:ZaMsg.Restore_RestoreSuccess}	
										]									
									},
									{type:_CASE_, relevant:"instance[ZaModel.ErrorCode] != 0",
										items: [
											{type:_OUTPUT_, value:ZaMsg.Restore_LabelsProblem, relevant:"instance[ZaModel.ErrorCode] > 0"},
											{type:_OUTPUT_, ref:ZaModel.ErrorMessage}
										]
									}
								]
							}
						]
					} 
				]
			}	
		]
	};
	return xFormObject;
};