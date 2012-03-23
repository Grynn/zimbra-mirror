ZaClientUploadXFormView = function(parent, entry) {
    if (arguments.length == 0) return;
    ZaTabView.call(this, parent,"ZaClientUploadXFormView");
    ZaTabView.call(this, {
        parent:parent,
        iKeyName:"ZaClientUploadXFormView",
        contextId:"CLIENT_UPLOAD"
    });
    this.setScrollStyle(Dwt.SCROLL);
    this.initForm(ZaClientUploader.myXModel,this.getMyXForm(entry), null);
}

ZaClientUploadXFormView.ClientUploadFormId = null;
ZaClientUploadXFormView.ClientUploadInputId = null;

ZaClientUploadXFormView.prototype = new ZaTabView();
ZaClientUploadXFormView.prototype.constructor = ZaClientUploadXFormView;
ZaTabView.XFormModifiers["ZaClientUploadXFormView"] = new Array();

ZaClientUploadXFormView.prototype.getTabIcon =
    function () {
        return "ClientUpload" ;
    }

ZaClientUploadXFormView.prototype.getTabTitle =
    function () {
        return com_zimbra_clientuploader.Client_upload_title;
    }

ZaClientUploadXFormView.prototype.getTitle =
    function () {
        return com_zimbra_clientuploader.Client_upload_title;
    }

ZaClientUploadXFormView.prototype.setObject =
    function(entry) {
        this._containedObject = new Object();
        this._containedObject.attrs = new Object();

        if(!entry[ZaModel.currentTab])
            this._containedObject[ZaModel.currentTab] = "1";
        else
            this._containedObject[ZaModel.currentTab] = entry[ZaModel.currentTab];

        if (entry[ZaClientUploader.A2_isFileSelected]) {
            this._containedObject[ZaClientUploader.A2_isFileSelected] = entry[ZaClientUploader.A2_isFileSelected];
        } else {
            this._containedObject[ZaClientUploader.A2_isFileSelected] = false;
        }

        if (entry[ZaClientUploader.A2_uploadReponseMsg]) {
            this._containedObject[ZaClientUploader.A2_uploadReponseMsg] = entry[ZaClientUploader.A2_uploadReponseMsg];
        }

        this._containedObject[ZaClientUploader.A2_uploadStatus] = entry[ZaClientUploader.A2_uploadStatus];
        this._localXForm.setInstance(this._containedObject);
    }

ZaClientUploadXFormView.myXFormModifier = function(xFormObject) {
    xFormObject.tableCssStyle="width:100%;overflow:auto;";
    xFormObject.itemDefaults = {_SEPARATOR_: {containerCssStyle:"padding-right:3px;padding-left:3px;"}};
    xFormObject.items = [
        {type:_SWITCH_, align:_LEFT_, valign:_TOP_, items:[{type:_ZATABCASE_,id:"client_upload_view_tab",  numCols:1, width:"100%", caseKey:1,
            paddingStyle:(appNewUI? "padding-left:15px;":null), width:(appNewUI? "98%":"100%"), cellpadding:(appNewUI?2:0),
            items: [
                {type:_OUTPUT_, label:null, value:com_zimbra_clientuploader.Client_upload_title, colSpan:"*", cssStyle:"font-size:12pt;	font-weight: bold;"},
                {type:_OUTPUT_, label:null, value:com_zimbra_clientuploader.Client_upload_desc, colSpan:"*", cssStyle:"font-size:12px;"},
                {type: _DWT_ALERT_,style: DwtAlert.INFORMATION,
                    iconVisible: true,
                    content: null,
                    ref:ZaClientUploader.A2_uploadReponseMsg,
                    visibilityChecks:[[XForm.checkInstanceValue, ZaClientUploader.A2_uploadStatus, ZaClientUploader.STATUS_SUCCEEDED]],
                    visibilityChangeEventSources:[ZaClientUploader.A2_uploadStatus],
                    width:"95%",
                    align:_CENTER_
                },
                {type: _DWT_ALERT_,style: DwtAlert.CRITICAL,
                    iconVisible: true,
                    content: null,
                    ref:ZaClientUploader.A2_uploadReponseMsg,
                    visibilityChecks:[[XForm.checkInstanceValue, ZaClientUploader.A2_uploadStatus, ZaClientUploader.STATUS_FAILED]],
                    width:"95%",
                    visibilityChangeEventSources:[ZaClientUploader.A2_uploadStatus],
                    align:_CENTER_
                },
                {type: _DWT_ALERT_,style: DwtAlert.WARNING,
                    iconVisible: true,
                    content: com_zimbra_clientuploader.Client_upload_in_process,
                    visibilityChecks:[[XForm.checkInstanceValue, ZaClientUploader.A2_uploadStatus, ZaClientUploader.STATUS_PROGRESS]],
                    visibilityChangeEventSources:[ZaClientUploader.A2_uploadStatus],
                    width:"95%",
                    align:_CENTER_
                },
                {type:_SPACER_, colSpan:"*"},
                {type:_OUTPUT_, value:this.getUploadFormHtml(), colSpan:"*", cssStyle:"font-size:10pt;font-weight: bold;"},

                {type:_SPACER_, colSpan:"*"},

                {type: _SPACER_, height: 10 },
                {type:_GROUP_, colSpan:"*", items: [
                    {type:_DWT_BUTTON_, id: "upload_button", label:com_zimbra_clientuploader.BTN_upload,
                        enableDisableChecks:[[XForm.checkInstanceValue,ZaClientUploader.A2_isFileSelected,true]],
                        enableDisableChangeEventSources:[ZaClientUploader.A2_isFileSelected],
                        onActivate:"this.getForm().parent.upload()", width:"100px"}
                ]
                }
            ]}]}];

}
ZaTabView.XFormModifiers["ZaClientUploadXFormView"].push(ZaClientUploadXFormView.myXFormModifier);

ZaClientUploadXFormView.prototype.upload = function(){
    this.setUploadManager(new AjxPost(this.getUploadFrameId()));
    var clientUploadCallback = new AjxCallback(this, this.uploadCallback);
    var um = this.getUploadManager() ;
    window._uploadManager = um;
    try {
        um.execute(clientUploadCallback, document.getElementById (ZaClientUploadXFormView.ClientUploadFormId));
        this._localXForm.setInstanceValue(ZaClientUploader.STATUS_PROGRESS, ZaClientUploader.A2_uploadStatus);
    } catch (ex) {
        ZaApp.getInstance().getCurrentController().popupErrorDialog(ZaMsg.ZMLT_zimletFileNameError) ;
    }
}

ZaClientUploadXFormView.prototype.uploadCallback = function (status, reqId) {
    //we use explorer's 'submit form' mechanism to upload the file, so we cannot upload multi files in the same time,
    //but only one after one. thus this function will be called back in sequence.
    try {
        if (!(ZaApp.getInstance().getCurrentController() instanceof ZaClientUploadController)) {
            if (status == 1) {
                ZaApp.getInstance().getCurrentController().popupMsgDialog(ZaClientUploadXFormView.getResponseMsg(status)) ;
            } else {
                ZaApp.getInstance().getCurrentController().popupErrorDialog(ZaClientUploadXFormView.getResponseMsg(status)) ;
            }
            return;
        }
        var instance = this._localXForm.getInstance();
        if (status == 1) {
            instance[ZaClientUploader.A2_uploadStatus] = ZaClientUploader.STATUS_SUCCEEDED;
        } else {
            instance[ZaClientUploader.A2_uploadStatus] = ZaClientUploader.STATUS_FAILED;
        }
        instance[ZaClientUploader.A2_isFileSelected] = false;
        instance[ZaClientUploader.A2_uploadReponseMsg] = ZaClientUploadXFormView.getResponseMsg(status);
    } catch (ex) {
        ZaApp.getInstance().getCurrentController()._handleException(ex, "ZaClientUploadXFormView.prototype.uploadCallback");
    }

    this._localXForm.setInstance(instance);
}

ZaClientUploadXFormView.prototype.getUploadFrameId = function() {
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

/**
 * @params uploadManager is the AjxPost object
 */
ZaClientUploadXFormView.prototype.setUploadManager = function(uploadManager) {
    this._uploadManager = uploadManager;
};

ZaClientUploadXFormView.prototype.getUploadManager = function() {
    return this._uploadManager;
};

ZaClientUploadXFormView.prototype.getUploadFormHtml = function () {
    ZaClientUploadXFormView.ClientUploadFormId = Dwt.getNextId();
    ZaClientUploadXFormView.ClientUploadInputId = Dwt.getNextId();
    var uri = appContextPath + "/../service/extension/clientUploader/upload/";
    DBG.println("upload uri = " + uri);
    var html = [];
    var idx = 0;
    html[idx++] = "<div style='overflow:hidden'><form method='POST' action='";
    html[idx++] = uri;
    html[idx++] = "' id='";
    html[idx++] = ZaClientUploadXFormView.ClientUploadFormId;
    html[idx++] = "' enctype='multipart/form-data'><input id='";
    html[idx++] = ZaClientUploadXFormView.ClientUploadInputId;
    html[idx++] = "' type=file  name='clientFile' onChange=\"ZaClientUploadXFormView.changeUploadBtnState(this,event||window.event,'" +this.getHTMLElId() +"')\"></input>";
    html[idx++] = "</form></div>";
    return html.join("");
}

ZaClientUploadXFormView.changeUploadBtnState = function (obj, ev, DwtObjId) {
    var view = DwtControl.ALL_BY_ID[DwtObjId];
    if(view) {
        if(obj.value) {
            view._localXForm.setInstanceValue(true, ZaClientUploader.A2_isFileSelected);
        } else {
            view._localXForm.setInstanceValue(false, ZaClientUploader.A2_isFileSelected);
        }
    }
}

ZaClientUploadXFormView._respMsg = {
    1:com_zimbra_clientuploader.Client_upload_succeeded,
    20000006:com_zimbra_clientuploader.Client_upload_too_large,
    30000001:com_zimbra_clientuploader.Client_upload_update_failed,
    30000002:com_zimbra_clientuploader.Client_upload_update_failed,
    40000001:com_zimbra_clientuploader.Client_upload_no_permission
}
ZaClientUploadXFormView.getResponseMsg = function (status) {
    var msg = ZaClientUploadXFormView._respMsg[status];

    if (!msg) {
        msg = com_zimbra_clientuploader.Client_upload_failed;
    }
    return msg;
}
