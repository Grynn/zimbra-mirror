
ZaProxyCAUpload = function() {}

ZaProxyCAUpload.myXFormModifier = function(xFormObject) {
    var uploadCertUI =
        {type:_GROUP_,  numCols: 3, colSizes: ["275px","250px","100px"], colSpan: "*",
            items: [
            {type:_OUTPUT_, value: com_zimbra_cert_manager.certFile,
                containerCssClass:(appNewUI?"gridGroupBodyLabel":"xform_label"),
                containerCssStyle:(appNewUI?"text-align:left;border-right: 1px solid":_UNDEFINED_)
            },
            {type:_OUTPUT_, value: ZaProxyCAUpload.getUploadFormHtml(this) },
            {type: _DWT_BUTTON_ , label: com_zimbra_cert_manager.CERT_UploadButton, width: "9em",
               onActivate: ZaProxyCAUpload.uploadCertKeyFile
            }
        ]};
    //var bottomSpace = {type:_SPACER_, height:"10"};
    var tempItems;
    var i = 0;
    for(; i < xFormObject.items[1].choices.length; i ++) {
        var label =  xFormObject.items[1].choices[i].label;
        if(label == ZaMsg.NAD_Tab_AUTH) break;
    }

    if(xFormObject.items[2].items.length >=i && xFormObject.items[2].items[i]) {
        tempItems = xFormObject.items[2].items[i].items;
        for(var j = 0; j < tempItems.length; j ++) {
            if(tempItems[j].label == ZaMsg.NAD_AUTH_ClientConfigure) {
                for(var k = 0; k < tempItems[j].items.length; k++) {
                    if(tempItems[j].items[k].label == ZaMsg.NAD_zimbraReverseProxyClientCertCA) {
                        tempItems[j].items.splice(k+1,0,uploadCertUI);
                        break;
                    }
                }
            }
        }
    }

}

ZaTabView.XFormModifiers["GlobalConfigXFormView"].push(ZaProxyCAUpload.myXFormModifier);
ZaTabView.XFormModifiers["ZaServerXFormView"].push(ZaProxyCAUpload.myXFormModifier);
ZaTabView.XFormModifiers["ZaDomainXFormView"].push(ZaProxyCAUpload.myXFormModifier);

ZaProxyCAUpload.myDlgXFormModifier = function(xFormObject) {
    var uploadCertUI =
        {type:_GROUP_,  numCols: 2, colSizes: ["250px","100px"], colSpan: "*",
            cssStyle: "margin-bottom: 10px;margin-left: 195px",
            items: [
            {type:_OUTPUT_, value: ZaProxyCAUpload.getUploadFormHtml(this) },
            {type: _DWT_BUTTON_ , label: com_zimbra_cert_manager.CERT_UploadButton, width: "9em",
               onActivate: ZaProxyCAUpload.uploadCertKeyFile
            }
        ]};
    var tempItems;
    if(xFormObject.items[3].items.length >=9 && xFormObject.items[3].items[9]) {
        tempItems = xFormObject.items[3].items[9].items;
        tempItems[2].items.splice(4,0,uploadCertUI);
    }

}

ZaXDialog.XFormModifiers["ZaNewDomainXWizard"].push(ZaProxyCAUpload.myDlgXFormModifier);

ZaProxyCAUpload.uploadCertFormIdForConfig = Dwt.getNextId();
ZaProxyCAUpload.uploadiFrameIdForConfig = Dwt.getNextId();
ZaProxyCAUpload.uploadCertFormIdForServer = Dwt.getNextId();
ZaProxyCAUpload.uploadiFrameIdForServer = Dwt.getNextId();
ZaProxyCAUpload.uploadCertFormIdForDomain = Dwt.getNextId();
ZaProxyCAUpload.uploadiFrameIdForDomain = Dwt.getNextId();
ZaProxyCAUpload.uploadCertFormIdForDlg = Dwt.getNextId();
ZaProxyCAUpload.uploadiFrameIdForDlg = Dwt.getNextId();

ZaProxyCAUpload.uploadCertFormId = Dwt.getNextId();
ZaProxyCAUpload.uploadiFrameId = Dwt.getNextId();

ZaProxyCAUpload.getUploadCertFormId = function (caller) {
    if(caller instanceof GlobalConfigXFormView)
        return  ZaProxyCAUpload.uploadCertFormIdForConfig;
    else if(caller instanceof ZaServerXFormView)
        return ZaProxyCAUpload.uploadCertFormIdForServer;
    else if(caller instanceof ZaDomainXFormView)
        return ZaProxyCAUpload.uploadCertFormIdForDomain;
    else if(caller instanceof ZaNewDomainXWizard)
        return ZaProxyCAUpload.uploadCertFormIdForDlg;
    else // others, give a valid dwt id
        return ZaProxyCAUpload.uploadCertFormIdForConfig;
}

ZaProxyCAUpload.getUploadiFrameId = function (caller) {
    if(caller instanceof GlobalConfigXFormView)
        return  ZaProxyCAUpload.uploadiFrameIdForConfig;
    else if(caller instanceof ZaServerXFormView)
        return ZaProxyCAUpload.uploadiFrameIdForServer;
    else if(caller instanceof ZaDomainXFormView)
        return ZaProxyCAUpload.uploadiFrameIdForDomain;
    else if(caller instanceof ZaNewDomainXWizard)
        return ZaProxyCAUpload.uploadiFrameIdForDlg;
    else  // others, give a valid dwt id
        return ZaProxyCAUpload.uploadiFrameIdForConfig;
}

ZaProxyCAUpload.getUploadFormHtml = function(caller) {
	var uri = appContextPath + "/../service/upload?fmt=extended";
	var html = [];
	var idx = 0;
	html[idx++] = "<div><form method='POST' action='";
	html[idx++] = uri;
	html[idx++] = "' id='";
	html[idx++] = ZaProxyCAUpload.getUploadCertFormId(caller);// ZaProxyCAUpload.uploadCertFormId; //
	html[idx++] = "' enctype='multipart/form-data'>" ;
	html[idx++] = "<div><table border=0 cellspacing=0 cellpadding=2 style='table-layout: fixed;'> " ;
	html[idx++] = "<colgroup><col width='250' /><col width=50 /></colgroup>";

	html[idx++] = "<tbody><td><input type=file  name='certFile' size='30'></input></td><td></td></tr>";

	html[idx++] = "</tbody></table></div>";
	html[idx++] = "</form></div>";

	return html.join("");
}

ZaProxyCAUpload.uploadCertKeyFile = function() {
    var certFormId = ZaProxyCAUpload.getUploadCertFormId(this.getForm().parent);
    var formEl = document.getElementById(certFormId);
    var inputEls = formEl.getElementsByTagName("input") ;

    ZaProxyCAUpload.uploadInputs = {
        certFile : null
    };

    var filenameArr = [];
    for (var i=0; i < inputEls.length; i++){
        if (inputEls[i].type == "file") {
            var n = inputEls[i].name ;
            var v = ZaCertWizard.getFileName(inputEls[i].value) ;
            if (v != null && v.length != 0) {
                if (ZaUtil.findValueInArray(filenameArr, v) != -1) {
                    ZaApp.getInstance().getCurrentController().popupErrorDialog (
                        com_zimbra_cert_manager.dupFileNameError + v
                    );
                    return ;
                }
                filenameArr.push (v);
            }

            if ( n == "certFile") {
                if (v == null ||  v.length == 0) {
                    ZaApp.getInstance().getCurrentController().popupErrorDialog(com_zimbra_cert_manager.noCertFileError);
                    return ;
                }else{
                    ZaProxyCAUpload.uploadInputs["certFile"] = v ;
                }
            }
        }
    }

    var certUploadCallback = new AjxCallback(this, ZaProxyCAUpload._uploadCallback);
    var um = new AjxPost(ZaProxyCAUpload.getUploadFrameId(this.getForm().parent));
    window._uploadManager = um;
    try {
        um.execute(certUploadCallback, document.getElementById (certFormId));
        return ;
    }catch (err) {
        ZaApp.getInstance().getCurrentController().popupErrorDialog(com_zimbra_cert_manager.certFileNameError) ;
        return ;
    }
}

ZaProxyCAUpload.getUploadFrameId =
function(caller) {
    var iframeId = ZaProxyCAUpload.getUploadiFrameId(caller);
	if (!document.getElementById(iframeId)) {
		//var iframeId = iFrameId;
		var html = [ "<iframe name='", iframeId, "' id='", iframeId,
			     "' src='", (AjxEnv.isIE && location.protocol == "https:") ? appContextPath+"/public/blank.html" : "javascript:\"\"",
			     "' style='position: absolute; top: 0; left: 0; visibility: hidden'></iframe>" ];
		var div = document.createElement("div");
		div.innerHTML = html.join("");
		document.body.appendChild(div.firstChild);
	}
	return iframeId;
}

ZaProxyCAUpload._uploadCallback =
function (status, uploadResults) {
	if(window.console && window.console.log)
        console.log("Proxy CA File Upload: status = " + status);
    var form = this.getForm() ;
    var instance = form.getInstance () ;
    if ((status == AjxPost.SC_OK) && (uploadResults != null) && (uploadResults.length > 0)) {
        var uploadFiles = {};
		for (var i=0; i < uploadResults.length ; i ++) {
			var v = uploadResults[i] ;
			var certType = ZaProxyCAUpload.getFiletypeFromUploadInputs(v.filename) ;
			if (certType == "certFile") {
				uploadFiles = {
					aid: v.aid,
					filename: v.filename
				}
			}
        }
	    var soapDoc = AjxSoapDoc.create("UploadProxyCARequest", "urn:zimbraAdmin", null);
        if(uploadFiles.aid)
            soapDoc.set("cert.aid", uploadFiles.aid);
        if(uploadFiles.filename)
            soapDoc.set("cert.filename", uploadFiles.filename);

        var csfeParams = new Object();
        csfeParams.soapDoc = soapDoc;
        try {
                var reqMgrParams = {} ;
                reqMgrParams.controller = ZaApp.getInstance().getCurrentController();
                reqMgrParams.busyMsg = com_zimbra_cert_manager.BUSY_UPLOAD_CERTKEY;
                var resp = ZaRequestMgr.invoke(csfeParams, reqMgrParams ).Body.UploadProxyCAResponse;
                if(resp && resp.cert_content) {
                    var ref = ZaGlobalConfig.A_zimbraReverseProxyClientCertCA;
                    if(this.getForm().parent instanceof ZaServerXFormView)
                        ref = ZaServer.A_zimbraReverseProxyClientCertCA;
                    else if(this.getForm().parent instanceof ZaDomainXFormView || this.getForm().parent instanceof ZaNewDomainXWizard)
                        ref = ZaDomain.A_zimbraReverseProxyClientCertCA;
                    form.getModel().setInstanceValue(instance, ref, resp.cert_content);
                    form.parent.setDirty(true);
                    form.refresh () ;
                }
        }catch (ex) {
                ZaApp.getInstance().getCurrentController()._handleException(ex, "ZaProxyCAUpload._uploadCallback", null, false);
        }
    }
}

ZaProxyCAUpload.getFiletypeFromUploadInputs = function (filename) {
	for (var n in ZaProxyCAUpload.uploadInputs) {
			if (filename == ZaProxyCAUpload.uploadInputs[n]) {
				return n ;
            }
	}
}
