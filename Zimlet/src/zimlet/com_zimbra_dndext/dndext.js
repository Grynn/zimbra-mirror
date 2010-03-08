function Com_Zimbra_DnDExt() {
}

Com_Zimbra_DnDExt.prototype = new ZmZimletBase();
Com_Zimbra_DnDExt.prototype.constructor = Com_Zimbra_DnDExt;

Com_Zimbra_DnDExt.MAXFILE_SIZE = "" ;

Com_Zimbra_DnDExt.prototype.init = function () {

};

Com_Zimbra_DnDExt.prototype.onShowView = function(viewId, isNewView) {
    if (viewId == ZmId.VIEW_COMPOSE || viewId.indexOf('COMPOSE') != -1 && AjxEnv.isFirefox3_6up)
	{
        var curView = appCtxt.getAppViewMgr().getCurrentView();
		var el = curView.getHtmlElement();

        var ifrEl = el.getElementsByTagName("iframe");
        this._addHandlers(el);
        var dndTooltip = document.getElementById(el.id + '_zdnd_tooltip');
        dndTooltip.style.display = "block";
	}
};

Com_Zimbra_DnDExt.prototype._addHandlers = function(el) {
    Dwt.setHandler(el,"ondragenter",this._onDragEnter);
    Dwt.setHandler(el,"ondragover",this._onDragOver);
    Dwt.setHandler(el,"ondrop",this._onDrop);    
};

Com_Zimbra_DnDExt.prototype._onDragEnter = function(ev) {
    ev.stopPropagation();
    ev.preventDefault();
    return ev.dataTransfer.types.contains("application/x-moz-file");
};

Com_Zimbra_DnDExt.prototype._onDragOver = function(ev) {
    ev.stopPropagation();
    ev.preventDefault();
};

Com_Zimbra_DnDExt.prototype._onDrop = function(ev) {
    ev.stopPropagation();
    ev.preventDefault();
    Com_Zimbra_DnDExt._doDrop(ev);
};

Com_Zimbra_DnDExt._doDrop = function(ev) {

    var dt = ev.dataTransfer;
    var files = dt.files;                                                                                  

    if(files.length > 0) {
        Com_Zimbra_DnDExt._uploadFiles(files);
    }
    
};

Com_Zimbra_DnDExt._uploadFiles = function(files) {

    var boundary = "AJAX-----------------------" + (new Date).getTime();

    try {
        var req = new XMLHttpRequest();

        req.open("POST", appCtxt.get(ZmSetting.CSFE_UPLOAD_URI)+"&fmt=extended,raw", true);
        req.setRequestHeader("Content-Type", "multipart/form-data; boundary=" + boundary);

        var tempThis = req;
        req.onreadystatechange = function() {

            Com_Zimbra_DnDExt._handleResponse(tempThis);
        }

        var reqData = Com_Zimbra_DnDExt._buildMultipleUploads(files,boundary);
        req.sendAsBinary(reqData);
    } catch(exp) {
        var msgDlg = appCtxt.getMsgDialog();
        msgDlg.setMessage(ZmMsg.importErrorUpload, DwtMessageDialog.CRITICAL_STYLE);
        msgDlg.popup();
        return false;
    }
};


Com_Zimbra_DnDExt._handleErrorResponse = function(respCode) {

    var warngDlg = appCtxt.getMsgDialog();
    var style = DwtMessageDialog.CRITICAL_STYLE;
    if (respCode == '200') {
        return true;
    } else if(respCode == '413') {
        warngDlg.setMessage(ZmMsg.errorAttachmentTooBig, style);
    } else {
       var msg = AjxMessageFormat.format(ZmMsg.errorAttachment, (respCode || AjxPost.SC_NO_CONTENT)); 
       warngDlg.setMessage(msg, style); 
    }
    warngDlg.popup();
    return false;
    
};

Com_Zimbra_DnDExt._handleResponse = function(req) {

    var attachment_ids = [];

    if(req) {
        if(req.readyState == 4 && req.status == 200) {
            var resp = eval("["+req.responseText+"]");

            Com_Zimbra_DnDExt._handleErrorResponse(resp[0]);
            
            if(resp.length > 2) {
                var respObj = resp[2];
                for (var i = 0; i < respObj.length; i++) {
                    if(respObj[i].aid != "undefinied") {
                        attachment_ids.push(respObj[i].aid);
                    }
                }

                // build up the attachment list
                if(attachment_ids.length > 0) {

                    // locate the compose controller and set up the callback handler
                    var cc = appCtxt.getApp(ZmApp.MAIL).getComposeController(appCtxt.getApp(ZmApp.MAIL).getCurrentSessionId(ZmId.VIEW_COMPOSE));
                    var callback = new AjxCallback (cc,cc._handleResponseSaveDraftListener);

                    attachment_list = attachment_ids.join(",");
                    cc.sendMsg(attachment_list,ZmComposeController.DRAFT_TYPE_MANUAL,callback);
                }
            }
        }
    }
};


Com_Zimbra_DnDExt._buildMultipleUploads = function(files, boundary) {

    var CRLF  = "\r\n";
    var parts = [];

    for (var i = 0; i < files.length; i++) {
        var part = "";
        var file = files[i];

        var fieldName = "drag_drop_ext" + i;
        var fileName  = file.name;

        /*
         * Content-Disposition header contains name of the field
         * used to upload the file and also the name of the file as
         * it was on the user's computer.
         */
        part += 'Content-Disposition: form-data; ';
        part += 'name="' + fieldName + '"; ';
        part += 'filename="'+ fileName + '"' + CRLF;

        /*
         * Content-Type header contains the mime-type of the file
         * to send. Although we could build a map of mime-types
         * that match certain file extensions, we'll take the easy
         * approach and send a general binary header:
         *      application/octet-stream
         */
        part += "Content-Type: application/octet-stream";
        part += CRLF + CRLF; // marks end of the headers part

        /*
         * File contents read as binary data
         */
        part += file.getAsBinary() + CRLF;

        parts.push(part);

    }


    var request = "--" + boundary + CRLF;
    request+= parts.join("--" + boundary + CRLF);
    request+= "--" + boundary + "--" + CRLF;

    return request;
};


