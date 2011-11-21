/**
 * Created by IntelliJ IDEA.
 * User: qinan
 * Date: 5/24/11
 * Time: 2:49 PM
 * To change this template use File | Settings | File Templates.
 */


ZaDomainCertUpload = function() {}

ZaDomainCertUpload.myXFormModifier = function(xFormObject) {
    var uploadCertUI =
    {type:_GROUP_,  colSpan: "*", numCols: 2, colSizes: ["500px","*"],
        cssStyle: "margin-top: 10px; margin-left: 12px", items: [
        {type:_OUTPUT_, value: ZaDomainCertUpload.getUploadFormHtml() },
        {type: _DWT_BUTTON_ , colSpan: "*", label: com_zimbra_cert_manager.CERT_UploadButton, width: "10em",
           onActivate: ZaDomainCertUpload.uploadCertKeyFile
        }
    ]};
    var bottomSpace = {type:_SPACER_, height:"10"};
    var tempItems;
    if(xFormObject.items[2].items.length >=9 && xFormObject.items[2].items[9]) {
        tempItems = xFormObject.items[2].items[9].items;
        tempItems.splice(3,0,uploadCertUI, bottomSpace);
    } else {  // support 7.0.0, place the UI items as the last one
        var tablen = xFormObject.items[2].items.length;
        tempItems = xFormObject.items[2].items[tablen>0?(tablen-1):0].items;
        var itemlen = tempItems.length;
        tempItems.splice(itemlen,0,uploadCertUI, bottomSpace);
    }
}

ZaTabView.XFormModifiers["ZaDomainXFormView"].push(ZaDomainCertUpload.myXFormModifier);

ZaDomainCertUpload.uploadCertFormId = Dwt.getNextId();
ZaDomainCertUpload.uploadiFrameId = Dwt.getNextId();

ZaDomainCertUpload.getUploadFormHtml = function() {
	var uri = appContextPath + "/../service/upload?fmt=extended";
	var html = [];
	var idx = 0;
    var width = '210';
    if(AjxEnv.isIE) width = '150';
	html[idx++] = "<div><form method='POST' action='";
	html[idx++] = uri;
	html[idx++] = "' id='";
	html[idx++] = ZaDomainCertUpload.uploadCertFormId;
	html[idx++] = "' enctype='multipart/form-data'>" ;
	html[idx++] = "<div><table border=0 cellspacing=0 cellpadding=2 style='table-layout: fixed;'> " ;
	html[idx++] = "<colgroup><col width=" + width + "/><col width='*' /><col width=50 /></colgroup>";

	html[idx++] = "<tbody><tr><td>" + ZaMsg.NAD_DomainSSLCertificate + ":</td>";
	html[idx++] = "<td><input type=file  name='certFile' size='40'></input></td><td></td></tr>";

	html[idx++] = "<tr><td>" + ZaMsg.NAD_DomainSSLPrivateKey + ":</td>";
	html[idx++] = "<td><input type=file  name='keyFile' size='40'></input></td><td></td></tr>";

	html[idx++] = "</tbody></table></div>";
	html[idx++] = "</form></div>";

	return html.join("");
}

ZaDomainCertUpload.uploadCertKeyFile = function() {

    var formEl = document.getElementById(ZaDomainCertUpload.uploadCertFormId);
    var inputEls = formEl.getElementsByTagName("input") ;

    ZaDomainCertUpload.uploadInputs = {
        certFile : null ,
        keyFile : null
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
                    ZaDomainCertUpload.uploadInputs["certFile"] = v ;
                }
            }else if (n == "keyFile") {
                if (v == null || v.length == 0 ) {
                    ZaApp.getInstance().getCurrentController().popupErrorDialog(com_zimbra_cert_manager.noKeyFileError);
                    return ;
                }else{
                    ZaDomainCertUpload.uploadInputs["keyFile"] = v ;
                }
            }
        }
    }

    var certUploadCallback = new AjxCallback(this, ZaDomainCertUpload._uploadCallback);
    var um = new AjxPost(ZaDomainCertUpload.getUploadFrameId());
    window._uploadManager = um;
    try {
        um.execute(certUploadCallback, document.getElementById (ZaDomainCertUpload.uploadCertFormId));
        return ;
    }catch (err) {
        ZaApp.getInstance().getCurrentController().popupErrorDialog(com_zimbra_cert_manager.certFileNameError) ;
        return ;
    }
}

ZaDomainCertUpload.getUploadFrameId =
function() {
	if (!document.getElementById(ZaDomainCertUpload.uploadiFrameId)) {
		var iframeId = ZaDomainCertUpload.uploadiFrameId;
		var html = [ "<iframe name='", iframeId, "' id='", iframeId,
			     "' src='", (AjxEnv.isIE && location.protocol == "https:") ? appContextPath+"/public/blank.html" : "javascript:\"\"",
			     "' style='position: absolute; top: 0; left: 0; visibility: hidden'></iframe>" ];
		var div = document.createElement("div");
		div.innerHTML = html.join("");
		document.body.appendChild(div.firstChild);
	}
	return ZaDomainCertUpload.uploadiFrameId;
}

ZaDomainCertUpload._uploadCallback =
function (status, uploadResults) {
	if(window.console && window.console.log)
        console.log("Cert File Upload: status = " + status);
    var form = this.getForm() ;
    var instance = form.getInstance () ;
    if ((status == AjxPost.SC_OK) && (uploadResults != null) && (uploadResults.length > 0)) {
        var uploadFiles = {
			cert: {},
			key: {}
		}
		for (var i=0; i < uploadResults.length ; i ++) {
			var v = uploadResults[i] ;
			var certType = ZaDomainCertUpload.getFiletypeFromUploadInputs(v.filename) ;
			if (certType == "certFile") {
				uploadFiles.cert = {
					aid: v.aid,
					filename: v.filename
				}
			}else if (certType == "keyFile") {
				uploadFiles.key = {
					aid: v.aid,
					filename: v.filename
				}
			}
        }
	    var soapDoc = AjxSoapDoc.create("UploadDomCertRequest", "urn:zimbraAdmin", null);
        if(uploadFiles.cert.aid)
            soapDoc.set("cert.aid", uploadFiles.cert.aid);
        if(uploadFiles.cert.filename)
            soapDoc.set("cert.filename", uploadFiles.cert.filename);
        if(uploadFiles.key.aid)
            soapDoc.set("key.aid", uploadFiles.key.aid);
        if(uploadFiles.key.filename)
            soapDoc.set("key.filename", uploadFiles.key.filename);

        var csfeParams = new Object();
        csfeParams.soapDoc = soapDoc;
        try {
                var reqMgrParams = {} ;
                reqMgrParams.controller = ZaApp.getInstance().getCurrentController();
                reqMgrParams.busyMsg = com_zimbra_cert_manager.BUSY_UPLOAD_CERTKEY;
                var resp = ZaRequestMgr.invoke(csfeParams, reqMgrParams ).Body.UploadDomCertResponse;
                if(resp && resp.cert_content) {
                    form.getModel().setInstanceValue(instance, ZaDomain.A_zimbraSSLCertificate, resp.cert_content);
                    form.parent.setDirty(true);
                    form.refresh () ;
                }
                if (resp && resp.key_content) {
                    form.setInstanceValue(resp.key_content, ZaDomain.A_zimbraSSLPrivateKey);
                    form.parent.setDirty(true);
                    form.refresh () ;
                }

        }catch (ex) {
                ZaApp.getInstance().getCurrentController()._handleException(ex, "ZaDomainCertUpload._uploadCallback", null, false);
        }
    }
}

ZaDomainCertUpload.getFiletypeFromUploadInputs = function (filename) {
	for (var n in ZaDomainCertUpload.uploadInputs) {
			if (filename == ZaDomainCertUpload.uploadInputs[n]) {
				return n ;
            }
	}
}

ZaDomainCertUpload.postDomainChange =
function (ev) {
	if (ev) {
		var mods = ev.getDetails()["mods"];
		if(mods["zimbraSSLCertificate"] || mods["zimbraSSLPrivateKey"]) {
			ZaApp.getInstance().getCurrentController().popupMsgDialog(com_zimbra_cert_manager.Cert_Uploaded_Info);
		}
	}
}

ZaController.postChangeMethods["ZaDomainController"].push(ZaDomainCertUpload.postDomainChange);
