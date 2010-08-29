/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2008, 2009, 2010 Zimbra, Inc.
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
* @class ZaBulkDataImportXWizard
* @contructor ZaBulkDataImportXWizard
* @param parent DwtShell
* @param entry ZaBulkProvision
* @author Greg Solovyev
**/
function ZaBulkDataImportXWizard (parent,entry) {
    var w = "650px" ;
    ZaXWizardDialog.call(this, parent, null, com_zimbra_bulkprovision.BP_Wizard_title_new, w, (AjxEnv.isIE ? "330px" :"320px"),"ZaBulkDataImportXWizard");

	ZaBulkDataImportXWizard.xmlUploadFormId = Dwt.getNextId();
    this.initForm(ZaBulkProvision.getMyXModel(),this.getMyXForm(entry),null);

  	this._helpURL = [location.pathname, ZaUtil.HELP_URL, ZaBulkDataImportXWizard.helpURL, "?locid=", AjxEnv.DEFAULT_LOCALE].join(""); 
  	
	
}
ZaBulkDataImportXWizard.STEP_INDEX = 1;
ZaBulkDataImportXWizard.STEP_1 = ZaBulkDataImportXWizard.STEP_INDEX++;
ZaBulkDataImportXWizard.STEP_ACCOUNT_SOURCE = ZaBulkDataImportXWizard.STEP_INDEX++;
ZaBulkDataImportXWizard.STEP_IMAP_OPTIONS = ZaBulkDataImportXWizard.STEP_INDEX++;
ZaBulkDataImportXWizard.STEP_REVIEW = ZaBulkDataImportXWizard.STEP_INDEX++;
ZaBulkDataImportXWizard.STEP_FINISH = ZaBulkDataImportXWizard.STEP_INDEX++;

ZaBulkDataImportXWizard.prototype = new ZaXWizardDialog;
ZaBulkDataImportXWizard.prototype.constructor = ZaBulkDataImportXWizard;

ZaXDialog.XFormModifiers["ZaBulkDataImportXWizard"] = new Array();
ZaBulkDataImportXWizard.helpURL = "appliance/zap_importing_accounts.htm";

/**
* @method setObject sets the object contained in the view
* @param entry -  object to display
**/
ZaBulkDataImportXWizard.prototype.setObject =
function(entry) {
	this._containedObject = new Object();
	this._containedObject = entry ;

	this._containedObject[ZaModel.currentStep] = ZaBulkDataImportXWizard.STEP_1;
    this._localXForm.setInstance(this._containedObject);
}

ZaBulkDataImportXWizard.prototype.previewCallback = function(params, resp) {
	if(resp && resp.isException()) {
		throw(resp.getException());
	} else {
		var response = resp.getResponse().Body.BulkIMAPDataImportResponse;
		var totalAccounts = 0;
		var idleAccounts = null;
		var finishedAccounts = null;
		var runningAccounts = null;
		var useAdminLogin = 0;
		if(response[ZaBulkProvision.A2_totalCount] && response[ZaBulkProvision.A2_totalCount][0] && response[ZaBulkProvision.A2_totalCount][0]._content) {
			totalAccounts = response[ZaBulkProvision.A2_totalCount][0]._content;
		}
		this._localXForm.setInstanceValue(totalAccounts,ZaBulkProvision.A2_totalCount);
		
		if(response[ZaBulkProvision.A2_runningCount] && response[ZaBulkProvision.A2_runningCount][0] && response[ZaBulkProvision.A2_runningCount][0]._content) {
			runningAccounts = response[ZaBulkProvision.A2_runningCount][0]._content;
			if(runningAccounts) {
				this._localXForm.setInstanceValue(runningAccounts,ZaBulkProvision.A2_runningCount);
			}
		}
		
		if(response[ZaBulkProvision.A2_idleCount] && response[ZaBulkProvision.A2_idleCount][0] && response[ZaBulkProvision.A2_idleCount][0]._content) {
			idleAccounts = response[ZaBulkProvision.A2_idleCount][0]._content;
			if(idleAccounts) {
				this._localXForm.setInstanceValue(idleAccounts,ZaBulkProvision.A2_idleCount);
			}
		}		
		
		if(response[ZaBulkProvision.A2_finishedCount] && response[ZaBulkProvision.A2_finishedCount][0] && response[ZaBulkProvision.A2_finishedCount][0]._content) {
			finishedAccounts = response[ZaBulkProvision.A2_finishedCount][0]._content;
			if(finishedAccounts) {
				this._localXForm.setInstanceValue(finishedAccounts,ZaBulkProvision.A2_finishedCount);
			}
		}				
		
		if(response[ZaBulkProvision.A2_IMAPHost] && response[ZaBulkProvision.A2_IMAPHost][0] && response[ZaBulkProvision.A2_IMAPHost][0]._content) {
			this._localXForm.setInstanceValue(response[ZaBulkProvision.A2_IMAPHost][0]._content,ZaBulkProvision.A2_IMAPHost);
		}		
		if(response[ZaBulkProvision.A2_IMAPPort] && response[ZaBulkProvision.A2_IMAPPort][0] && response[ZaBulkProvision.A2_IMAPPort][0]._content) {
			this._localXForm.setInstanceValue(response[ZaBulkProvision.A2_IMAPPort][0]._content,ZaBulkProvision.A2_IMAPPort);
		}			
		if(response[ZaBulkProvision.A2_connectionType] && response[ZaBulkProvision.A2_connectionType][0] && response[ZaBulkProvision.A2_connectionType][0]._content) {
			this._localXForm.setInstanceValue(response[ZaBulkProvision.A2_connectionType][0]._content,ZaBulkProvision.A2_connectionType);
		}
		if(response[ZaBulkProvision.A2_useAdminLogin] && response[ZaBulkProvision.A2_useAdminLogin][0] && response[ZaBulkProvision.A2_useAdminLogin][0]._content) {
			useAdminLogin = parseInt(response[ZaBulkProvision.A2_useAdminLogin][0]._content);
		}
		if(useAdminLogin > 0) {
			if(response[ZaBulkProvision.A2_IMAPAdminLogin] && response[ZaBulkProvision.A2_IMAPAdminLogin][0] && response[ZaBulkProvision.A2_IMAPAdminLogin][0]._content) {
				this._localXForm.setInstanceValue(response[ZaBulkProvision.A2_IMAPAdminLogin][0]._content,ZaBulkProvision.A2_IMAPAdminLogin);
			}
			if(response[ZaBulkProvision.A2_IMAPAdminPassword] && response[ZaBulkProvision.A2_IMAPAdminPassword][0] && response[ZaBulkProvision.A2_IMAPAdminPassword][0]._content) {
				this._localXForm.setInstanceValue(response[ZaBulkProvision.A2_IMAPAdminPassword][0]._content,ZaBulkProvision.A2_IMAPAdminPassword);
			}
			this._localXForm.setInstanceValue(1,ZaBulkProvision.A2_useAdminLogin);
		} else {
			this._localXForm.setInstanceValue(0,ZaBulkProvision.A2_useAdminLogin);
		}
		this.goPage(ZaMigrationXWizard.STEP_REVIEW);
	}
}

ZaBulkDataImportXWizard.getFileName = function (fullPath) {
    if (fullPath == null) return null ;

    var lastIndex = 0;
    if (AjxEnv.isWindows) {
        lastIndex = fullPath.lastIndexOf("\\") ;
    }else{
        lastIndex = fullPath.lastIndexOf("/") ;
    }

    return fullPath.substring(lastIndex + 1) ;
}

//upload the file
ZaBulkDataImportXWizard.prototype.getUploadFrameId =
function() {
	if (!this._uploadManagerIframeId) {
		var iframeId = Dwt.getNextId();
		var html = [ "<iframe name='", iframeId, "' id='", iframeId,
			     "' src='", (AjxEnv.isIE && location.protocol == "https:") ? appContextPath+"/public/blank.html" : "javascript:\"\"",
			     "' style='position: absolute; top: 0; left: 0; visibility: hidden'></iframe>" ];
		var div = document.createElement("div");
		div.innerHTML = html.join("");
		document.body.appendChild(div.firstChild);
		this._uploadManagerIframeId = iframeId;
	}
	return this._uploadManagerIframeId;
};

ZaBulkDataImportXWizard.prototype.getUploadManager =
function() {
	return this._uploadManager;
};

/**
 * @params uploadManager is the AjxPost object
 */
ZaBulkDataImportXWizard.prototype.setUploadManager =
function(uploadManager) {
	this._uploadManager = uploadManager;
};

ZaBulkDataImportXWizard.prototype.goNext = function() {
	var cStep = this._containedObject[ZaModel.currentStep];
	if(cStep == ZaBulkDataImportXWizard.STEP_1) {
		this.goPage(ZaMigrationXWizard.STEP_ACCOUNT_SOURCE);
	} else if(cStep == ZaMigrationXWizard.STEP_ACCOUNT_SOURCE) {
		//if using a bulk file - upload the file, the callbacks will move to the next step
		if(this._containedObject[ZaBulkProvision.A2_sourceType] == ZaBulkProvision.SOURCE_TYPE_XML) {
	        //1. check if the file name are valid and exists
	        //2. upload the file
	        var formEl = document.getElementById(ZaBulkDataImportXWizard.xmlUploadFormId);
	        var inputEls = formEl.getElementsByTagName("input") ;

	        var filenameArr = [];
	        for (var i=0; i < inputEls.length; i++){
	            if (inputEls[i].type == "file") {
	                var n = inputEls[i].name ;
	                var v = ZaBulkDataImportXWizard.getFileName(inputEls[i].value) ;
	                if ( n == "xmlFile") {
	                    if (v == null || v.length <= 0) {
	                        this._app.getCurrentController().popupErrorDialog (
	                            com_zimbra_bulkprovision.error_no_bulk_file_specified
	                        );
	                        return ;
	                    }

	                    //have a file, ready to upload
	                    break ;
	                }
	            }
	        }

	        //2. Upload the files
	        this.setUploadManager(new AjxPost(this.getUploadFrameId()));
	        var xmlUploadCallback = new AjxCallback(this, this._uploadCallback);
	        var um = this.getUploadManager() ;
	        window._uploadManager = um;
	        try {
	            um.execute(xmlUploadCallback, document.getElementById (ZaBulkDataImportXWizard.xmlUploadFormId));
	        }catch (err) {
	            this._app.getCurrentController().popupErrorDialog(com_zimbra_bulkprovision.error_no_bulk_file_specified) ;
	        }

			
		} else if(this._containedObject[ZaBulkProvision.A2_sourceType] == ZaBulkProvision.SOURCE_TYPE_LDAP ||
				this._containedObject[ZaBulkProvision.A2_sourceType] == ZaBulkProvision.SOURCE_TYPE_AD) {
			//move on to entering IMAP options
		}
	}
}

ZaBulkDataImportXWizard.prototype._uploadCallback = function (status, uploadResults) {
	var cStep = this._containedObject[ZaModel.currentStep] ;
	if (AjxEnv.hasFirebug)
		console.log("Provisioning File Upload: status = " + status);
	if ((status == AjxPost.SC_OK) && (uploadResults != null) && (uploadResults.length > 0)) {
    	var v = uploadResults[0] ;
        if (v.aid != null && v.aid.length > 0) {
           this._containedObject [ZaBulkProvision.A_csv_aid] =  v.aid ;
        } else {
           this._app.getCurrentController().popupErrorDialog(com_zimbra_bulkprovision.error_upload_csv_no_aid);
           return ;
        }
        //File is uploaded successfully
        try {
            var resp = ZaBulkProvision.getBulkProvisionAccounts(this._app, this._containedObject );
            if (resp.aid == this._containedObject[ZaBulkProvision.A_csv_aid]) {
            	//generate a preview of options, skip STEP_IMAP_OPTIONS, because these options should be in the XML
            	this._containedObject[ZaBulkProvision.A2_op] = ZaBilkProvision.OP_PREVIEW;
            	this._containedObject[ZaBulkProvision.A2_sourceType] = ZaBulkProvision.SOURCE_TYPE_XML;
        		var callback = new AjxCallback(this, ZaBulkDataImportXWizard.prototype.previewCallback,{});
            	ZaBulkProvision.bulkDataIMport(this._containedObject,callback);
            }else{
                throw new AjxException(com_zimbra_bulkprovision.error_unmatching_aid) ;
            }
        } catch (ex) {
            this._app.getCurrentController()._handleException(ex) ;
            return ;
        }
	} else {
		// handle errors during attachment upload.
		var msg = AjxMessageFormat.format(com_zimbra_bulkprovision.error_upload_bulk, [status]);
		this._app.getCurrentController().popupErrorDialog(msg);
	}
}

ZaBulkDataImportXWizard.isAccountSourceLDAP = function() {
	var val = this.getModel().getInstanceValue(this.getInstance(),ZaBulkProvision.A2_sourceType);
	return (val == ZaBulkProvision.SOURCE_TYPE_LDAP || val == ZaBulkProvision.SOURCE_TYPE_AD)
}

ZaBulkDataImportXWizard.getUploadFormHtml = function (){
	var uri = appContextPath + "/../service/upload?fmt=extended";
	var html = [];
	var idx = 0;
	html[idx++] = "<div style='height:50px;width: 500px; overflow:auto;'><form method='POST' action='";
	html[idx++] = uri;
	html[idx++] = "' id='";
	html[idx++] = ZaBulkDataImportXWizard.xmlUploadFormId;
	html[idx++] = "' enctype='multipart/form-data'>" ;
	html[idx++] = "<div><table border=0 cellspacing=0 cellpadding=2 style='table-layout: fixed;'> " ;
	html[idx++] = "<tbody><tr><td width=65>" + com_zimbra_bulkprovision.XML_Upload_file + "</td>";
	html[idx++] = "<td><input type=file  name='xmlFile' size='45'></input></td></tr>";
	html[idx++] = "</tbody></table></div>";
	html[idx++] = "</form></div>";
	return html.join("");
}

ZaBulkDataImportXWizard.myXFormModifier = function(xFormObject,entry) {
	var cases = new Array();
	var case1 = {type:_CASE_,numCols:2,colSizes:["250px","380px"],
		tabGroupKey:ZaBulkDataImportXWizard.STEP_1,caseKey:ZaBulkImportXWizard.STEP_1,
		items:[
		       {type:_DWT_ALERT_,colSpan:2,style:DwtAlert.INFO, iconVisible:false, content:com_zimbra_bulkprovision.DataImportWizardOverview,visibilityChecks:[]},
		       {ref:ZaBulkProvision.A2_sourceServerType, type:_OSELECT1_, label:com_zimbra_bulkprovision.SourceServerType,labelLocation:_LEFT_,visibilityChecks:[],enableDisableChecks:[]},
		       {type:_DWT_ALERT_,colSpan:2,style:DwtAlert.INFO, iconVisible:false, content:com_zimbra_bulkprovision.AccountListTypeNote,visibilityChecks:[]},
		       {ref:ZaBulkProvision.A2_sourceType, type:_OSELECT1_, label:com_zimbra_bulkprovision.AccountListType,labelLocation:_LEFT_,visibilityChecks:[],enableDisableChecks:[]}
       ]
	};
	cases.push(case1);
	
	var case_account_source = {type:_CASE_,numCols:2,colSizes:["250px","380px"],tabGroupKey:ZaBulkDataImportXWizard.STEP_ACCOUNT_SOURCE,
		caseKey:ZaBulkImportXWizard.STEP_ACCOUNT_SOURCE,
		items:[
		       {type:_GROUP_,useParentTable:true,visibilityChecks:[ZaBulkDataImportXWizard.isAccountSourceLDAP], visibilityChangeEventSources:[ZaBulkProvision.A2_sourceType],
		    	   items:[
		  		       	{type:_GROUP_, numCols:6, colSpan:2,label:"   ",labelLocation:_LEFT_,
							visibilityChecks:[[XForm.checkInstanceValue,ZaBulkProvision.A2_sourceType,ZaBulkProvision.SOURCE_TYPE_LDA]],
							visibilityChangeEventSources:[ZaBulkProvision.A2_sourceType],
							items: [
								{type:_OUTPUT_, label:null, labelLocation:_NONE_, value:" ", width:"35px"},
								{type:_OUTPUT_, label:null, labelLocation:_NONE_, value:com_zimbra_bulkprovision.ADServerName, width:"200px"},
								{type:_OUTPUT_, label:null, labelLocation:_NONE_, value:" ", width:"5px"},									
								{type:_OUTPUT_, label:null, labelLocation:_NONE_, value:com_zimbra_bulkprovision.ADServerPort,  width:"40px"},	
								{type:_OUTPUT_, label:null, labelLocation:_NONE_, value:com_zimbra_bulkprovision.ADUseSSL, width:"*"}									
							]
						},	
						{type:_GROUP_, numCols:6, colSpan:2,label:"   ",labelLocation:_LEFT_,
							visibilityChecks:[[XForm.checkInstanceValue,ZaBulkProvision.A2_sourceType,ZaBulkProvision.SOURCE_TYPE_AD]],
							visibilityChangeEventSources:[ZaBulkProvision.A2_sourceType],
							items: [	
							        {type:_OUTPUT_, label:null, labelLocation:_NONE_, value:" ", width:"35px"},
									{type:_OUTPUT_, label:null, labelLocation:_NONE_, value:com_zimbra_bulkprovision.LDAP_GALServerName, width:"200px"},
									{type:_OUTPUT_, label:null, labelLocation:_NONE_, value:" ", width:"5px"},									
									{type:_OUTPUT_, label:null, labelLocation:_NONE_, value:com_zimbra_bulkprovision.LDAP_GALServerPort,  width:"40px"},	
									{type:_OUTPUT_, label:null, labelLocation:_NONE_, value:com_zimbra_bulkprovision.LDAP_GALUseSSL, width:"*"}									
								]
						},		       
						{ref:ZaBulkProvision.A2_GalLdapURL, type:_LDAPURL_, label:com_zimbra_bulkprovision.LDAPUrl,ldapSSLPort:"3269",ldapPort:"3268",  labelLocation:_LEFT_,
							visibilityChecks:[],enableDisableChecks:[]
						},
						{ref:ZaBulkProvision.A2_GalLdapBindDn, type:_TEXTFIELD_, width:"380px", label:ZaMsg.Domain_GalLdapBindDn, labelLocation:_LEFT_, 
							enableDisableChecks:[],visibilityChecks:[],bmolsnr:true				
						},
						{ref:ZaBulkProvision.A2_GalLdapBindPassword, type:_SECRET_, label:ZaMsg.Domain_GalLdapBindPassword, labelLocation:_LEFT_, 
							enableDisableChecks:[],visibilityChecks:[]				
						},
						{ref:ZaBulkProvision.A2_GalLdapConfirmBindPassword, type:_SECRET_, label:ZaMsg.Domain_GalLdapBindPasswordConfirm, labelLocation:_LEFT_, 
							enableDisableChecks:[],visibilityChecks:[]				
						},				
						{ref:ZaBulkProvision.A2_GalLdapFilter, type:_TEXTAREA_, width:380, height:40, 
							label:ZaMsg.Domain_GalLdapFilter, labelLocation:_LEFT_, 
							enableDisableChecks:[],visibilityChecks:[]
						},
						{ref:ZaBulkProvision.A2_GalLdapSearchBase, type:_TEXTAREA_, width:380, height:40, label:ZaMsg.Domain_GalLdapSearchBase, 
							labelLocation:_LEFT_, enableDisableChecks:[],visibilityChecks:[],bmolsnr:true
						}
		    	   ]
		       },
		       {type:_GROUP_,useParentTable:true,visibilityChecks:[[XForm.checkInstanceValue,ZaBulkProvision.A2_sourceType,ZaBulkProvision.SOURCE_TYPE_XML]], 
		    	   visibilityChangeEventSources:[ZaBulkProvision.A2_sourceType],
                    {type:_OUTPUT_, value: ZaBulkDataImportXWizard.getUploadFormHtml(), colSpan:2}
		       }
		]
	};
	cases.push(case_account_source);
	
	var case_imap_options = {type:_CASE_, numCols:2, colSizes:["250px","380px"],tablGroupKey:ZaBulkDataImportXWizard.STEP_IMAP_OPTIONS,caseKey:ZaBulkDataImportXWizard.STEP_IMAP_OPTIONS,
			items:[
			       {type:_TEXTFIELD_,label:com_zimbra_bulkprovision.IMAPHost, ref:ZaBulkProvision.A2_IMAPHost, visibilityChecls:[],enableDisableChecks:[]},
			       {type:_TEXTFIELD_,label:com_zimbra_bulkprovision.IMAPPost, ref:ZaBulkProvision.A2_IMAPPort, visibilityChecls:[],enableDisableChecks:[]},
			       {ref:ZaBulkProvision.A2_connectionType, type:_OSELECT1_, label:com_zimbra_bulkprovision.IMAPConnectionType,labelLocation:_LEFT_,
			    	   visibilityChecks:[],enableDisableChecks:[]
			       },
			       {ref:ZaBulkProvision.A2_useAdminLogin,  type:_CHECKBOX_,  
			    	   label:com_zimbra_bulkprovision.UseIMAPAdminCredentials,trueValue:"1", falseValue:"0",visibilityChecks:[],enableDisableChecks:[]
			       },			       
			       {type:_TEXTFIELD_,label:com_zimbra_bulkprovision.IMAPAdminLogin, ref:ZaBulkProvision.A2_IMAPAdminLogin, visibilityChecls:[],
			    	   enableDisableChecks:[XForm.checkInstanceValue,ZaBulkProvision.A2_useAdminLogin,"1"],
			    	   enableDisableChangeEventSources:[ZaBulkProvision.A2_useAdminLogin]
			       },
			       {type:_SECRET_,label:com_zimbra_bulkprovision.IMAPAdminPassword, ref:ZaBulkProvision.A2_IMAPAdminPassword, visibilityChecls:[],
			    	   enableDisableChecks:[XForm.checkInstanceValue,ZaBulkProvision.A2_useAdminLogin,"1"],
			    	   enableDisableChangeEventSources:[ZaBulkProvision.A2_useAdminLogin]
			       },
			       {type:_SECRET_,label:com_zimbra_bulkprovision.IMAPAdminPasswordConfirm, ref:ZaBulkProvision.A2_IMAPAdminPasswordConfirm, visibilityChecls:[],
			    	   enableDisableChecks:[XForm.checkInstanceValue,ZaBulkProvision.A2_useAdminLogin,"1"],
			    	   enableDisableChangeEventSources:[ZaBulkProvision.A2_useAdminLogin]
			       }
			]
	};
	cases.push(case_imap_options);

	
	var case_review = {type:_CASE_, numCols:2, colSizes:["250px","380px"],tablGroupKey:ZaBulkDataImportXWizard.STEP_REVIEW,caseKey:ZaBulkDataImportXWizard.STEP_REVIEW,
			items:[
			       {type:_OUTPUT_,label:com_zimbra_bulkprovision.TotalMailboxes, ref:ZaBulkProvision.A2_totalCount, visibilityChecks:[]},
			       //TODO: May want to show a warning saying that these accounts will be skipped
			       {type:_OUTPUT_,label:com_zimbra_bulkprovision.RunningMailboxes, ref:ZaBulkProvision.A2_runningCount, visibilityChecks:[XForm.checkInstanceValueNotEmty,ZaBulkProvision.A2_runningCount],
			    	   visibilityChangeEventSources:[ZaBulkProvision.A2_runningCount]
			       },
			       {type:_OUTPUT_,label:com_zimbra_bulkprovision.IdleMailboxes, ref:ZaBulkProvision.A2_idleCount, visibilityChecks:[XForm.checkInstanceValueNotEmty,ZaBulkProvision.A2_idleCount],
				    	   visibilityChangeEventSources:[ZaBulkProvision.A2_idleCount]
			       },
			       //TODO: May want to show a warning and ask if they want to re-run import on finished accounts
			       {type:_OUTPUT_,label:com_zimbra_bulkprovision.FinishedMaiboxes, ref:ZaBulkProvision.A2_finishedCount, visibilityChecks:[XForm.checkInstanceValueNotEmty,ZaBulkProvision.A2_finishedCount],
			    	   visibilityChangeEventSources:[ZaBulkProvision.A2_finishedCount]
			       },
			       {type:_TEXTFIELD_,label:com_zimbra_bulkprovision.IMAPHost, ref:ZaBulkProvision.A2_IMAPHost, visibilityChecls:[],enableDisableChecks:[]},
			       {type:_TEXTFIELD_,label:com_zimbra_bulkprovision.IMAPPort, ref:ZaBulkProvision.A2_IMAPPort, visibilityChecls:[],enableDisableChecks:[]},
			       {ref:ZaBulkProvision.A2_connectionType, type:_OSELECT1_, label:com_zimbra_bulkprovision.IMAPConnectionType,labelLocation:_LEFT_,
			    	   visibilityChecks:[],enableDisableChecks:[]
			       },
			       {ref:ZaBulkProvision.A2_useAdminLogin,  type:_CHECKBOX_,  
			    	   label:com_zimbra_bulkprovision.UseIMAPAdminCredentials,trueValue:"1", falseValue:"0",visibilityChecks:[],enableDisableChecks:[]
			       },			       
			       {type:_TEXTFIELD_,label:com_zimbra_bulkprovision.IMAPAdminLogin, ref:ZaBulkProvision.A2_IMAPAdminLogin, visibilityChecls:[],
			    	   enableDisableChecks:[XForm.checkInstanceValue,ZaBulkProvision.A2_useAdminLogin,"1"],
			    	   enableDisableChangeEventSources:[ZaBulkProvision.A2_useAdminLogin]
			       },
			       {type:_SECRET_,label:com_zimbra_bulkprovision.IMAPAdminPassword, ref:ZaBulkProvision.A2_IMAPAdminPassword, visibilityChecls:[],
			    	   enableDisableChecks:[XForm.checkInstanceValue,ZaBulkProvision.A2_useAdminLogin,"1"],
			    	   enableDisableChangeEventSources:[ZaBulkProvision.A2_useAdminLogin]
			       }
			]
	};
	cases.push(case_review);
	
	var case_finish = {type:_CASE_, numCols:2, colSizes:["250px","380px"],tablGroupKey:ZaBulkDataImportXWizard.STEP_FINISH,caseKey:ZaBulkDataImportXWizard.STEP_FINISH,
			items:[
			       {type:_DWT_ALERT_,colSpan:2,style:DwtAlert.INFO, iconVisible:false, content:com_zimbra_bulkprovision.DataImportStarted,visibilityChecks:[]}
			]
	};
	cases.push(case_finish);
    var contentW = 630;
    xFormObject.items = [
			{type:_SWITCH_, width:contentW, align:_LEFT_, valign:_TOP_, items:cases}
		];
	
}