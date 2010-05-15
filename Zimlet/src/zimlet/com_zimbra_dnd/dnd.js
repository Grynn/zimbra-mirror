/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Zimlets
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

function Com_Zimbra_DnD() {
}

Com_Zimbra_DnD.prototype = new ZmZimletBase();
Com_Zimbra_DnD.prototype.constructor = Com_Zimbra_DnD;

Com_Zimbra_DnD.attachment_ids = null;
Com_Zimbra_DnD.flength = null;

Com_Zimbra_DnD.prototype.init = function () {

    this.isHTML5 = false;
    this.checkHTML5Dnd();
    if (this.isHTML5 && !AjxEnv.isIE) {
       this._initHTML5();
    } else if(!AjxEnv.isIE) {
       this._initNonHTM5();  
    }

};

Com_Zimbra_DnD.prototype.isDndSupported = function (evntname) {

    var element = document.createElement('div');
    evntname = 'on' + evntname;

    var isSupported = (evntname in element);

    element = null;

    return isSupported;
};

Com_Zimbra_DnD.prototype.checkHTML5Dnd = function () {

    if(!this.isHTML5) {
        this.isHTML5 = this.isDndSupported('drag')
                && this.isDndSupported('dragstart')
                && this.isDndSupported('dragenter')
                && this.isDndSupported('dragover')
                && this.isDndSupported('dragleave')
                && this.isDndSupported('dragend')
                && this.isDndSupported('drop');
    }

};

Com_Zimbra_DnD.prototype._initNonHTM5 = function () {

    var outerEl = document.getElementById("skin_outer");
	var filesEl = document.getElementById("zdnd_files");
	if (outerEl && !filesEl) {
		var fileSpan = document.createElement("span");
		fileSpan.id = "zdnd_files";
		fileSpan.style.display = "none";
		fileSpan.innerHTML = this.getConfig("dnd-uploadform");
		outerEl.appendChild(fileSpan);
	}

	if (document.getElementById("zdnd_files")) {
		var uploadUri = appCtxt.get(ZmSetting.CSFE_UPLOAD_URI);
		var zDnDUploadFrm = document.getElementById("zdnd_form");
		zDnDUploadFrm.setAttribute("action", uploadUri);
	}

    var cmd = window.newWindowCommand;
    if(cmd == 'compose' || cmd == 'msgViewDetach') {
            var self = this;
            setTimeout(AjxCallback.simpleClosure(function(cmd) {
                var curView = appCtxt.getAppViewMgr().getCurrentView();
                var el = curView.getHtmlElement();
                var doc = el.ownerDocument;
                var filesEl = doc.getElementById("zdnd_files");
                if (!filesEl) {
                    var fileSpan = doc.createElement("span");
                    fileSpan.id = "zdnd_files";
                    fileSpan.style.display = "none";
                    fileSpan.innerHTML = window.opener.document.getElementById("zdnd_files").innerHTML;
                    el.appendChild(fileSpan);
                }

                if (doc.getElementById("zdnd_files")) {
                    var uploadUri = appCtxt.get(ZmSetting.CSFE_UPLOAD_URI);
                    var zDnDUploadFrm = doc.getElementById("zdnd_form");
                    zDnDUploadFrm.setAttribute("action", uploadUri);
                }
                if(cmd == 'compose') {
                    var ev = document.createEvent("Events");
                    ev.initEvent("ZimbraDnD", true, false);
                    curView._resetBodySize();
                    el.dispatchEvent(ev);
                }
            }, this, cmd), 1000);
    }
    
};

Com_Zimbra_DnD.prototype._initHTML5 = function () {

    /*var cmd = window.newWindowCommand;
    if(cmd == 'compose' || cmd == 'msgViewDetach') {
        setTimeout(AjxCallback.simpleClosure(function() {
            var curView = appCtxt.getAppViewMgr().getCurrentView();
            var el = curView.getHtmlElement();
            alert(el);
            this._addHandlers(el);
            var dndTooltip = document.getElementById(el.id + '_zdnd_tooltip');
            alert(dndTooltip);
            dndTooltip.style.display = "block";
        },this),1000);
    }*/
    
};

Com_Zimbra_DnD.prototype.onShowView =
function(viewId, isNewView) {
    var isWindowsSafari = (AjxEnv.isWindows && AjxEnv.isSafari);
    if(this.isHTML5 && !AjxEnv.isIE && !isWindowsSafari) {
        if (viewId == ZmId.VIEW_COMPOSE || viewId.indexOf(ZmId.VIEW_COMPOSE) != -1) {
            var curView = appCtxt.getAppViewMgr().getCurrentView();
            var el = curView.getHtmlElement();

            var ifrEl = el.getElementsByTagName("iframe");
            this._addHandlers(el);
            var dndTooltip = document.getElementById(el.id + '_zdnd_tooltip');
            dndTooltip.style.display = "block";
        }
    } else if ("createEvent" in document && document.getElementById("zdnd_files") && !AjxEnv.isIE && !isWindowsSafari) {
        if (viewId == ZmId.VIEW_COMPOSE ||
			viewId == ZmId.VIEW_BRIEFCASE_COLUMN ||
			viewId == ZmId.VIEW_BRIEFCASE ||
			viewId == ZmId.VIEW_BRIEFCASE_DETAIL || viewId.indexOf(ZmId.VIEW_COMPOSE) != -1) {

			var ev = document.createEvent("Events");
			ev.initEvent("ZimbraDnD", true, false);

			var curView = appCtxt.getAppViewMgr().getCurrentView();

			if (viewId == ZmId.VIEW_COMPOSE || viewId.indexOf(ZmId.VIEW_COMPOSE) != -1) {
				curView._resetBodySize();
			}
            var el = curView.getHtmlElement();
			el.dispatchEvent(ev);
		}
	}
};

Com_Zimbra_DnD.uploadDnDFiles =
function() {
	var viewId = appCtxt.getAppViewMgr().getCurrentViewId();
	if (viewId == ZmId.VIEW_COMPOSE ||
		viewId == ZmId.VIEW_BRIEFCASE_COLUMN ||
		viewId == ZmId.VIEW_BRIEFCASE ||
		viewId == ZmId.VIEW_BRIEFCASE_DETAIL || viewId.indexOf('COMPOSE') != -1)
	{
		var curView = appCtxt.getAppViewMgr().getCurrentView();
        /*if(window.newWindowCommand == 'compose'){
           window.opener.document.getElementById("zdnd_files").innerHTML = document.getElementById("zdnd_files").innerHTML;
        }*/
		if (curView && curView.uploadFiles) {
			curView.uploadFiles();
		}
	}
};

Com_Zimbra_DnD.prototype._addHandlers = function(el) {
    Dwt.setHandler(el,"ondragenter",this._onDragEnter);
    Dwt.setHandler(el,"ondragover",this._onDragOver);
    Dwt.setHandler(el,"ondrop",this._onDrop);
};

Com_Zimbra_DnD.prototype._onDragEnter = function(ev) {
    ev.stopPropagation();
    ev.preventDefault();
    if(ev.dataTransfer && ev.dataTransfer.types && (ev.dataTransfer.types.indexOf("Files") != -1 || ev.dataTransfer.types.indexOf("application/x-moz-file"))) {
        return true;
    } else {
        return false;
    }
};

Com_Zimbra_DnD.prototype._onDragOver = function(ev) {
    return false;
};

Com_Zimbra_DnD.prototype._onDrop = function(ev) {
    ev.stopPropagation();
    ev.preventDefault();

    var dt = ev.dataTransfer;
    var files = dt.files;

    if(files) {
        Com_Zimbra_DnD.attachment_ids = [];
        Com_Zimbra_DnD.flength = files.length;
        for (var i = 0; i < files.length; i++) {
            var file = files[i];
            Com_Zimbra_DnD._uploadFiles(file);
        }
    }

};

Com_Zimbra_DnD._uploadFiles = function(file) {

    try {

        var req = new XMLHttpRequest();
        var fileName = null;

        req.open("POST", appCtxt.get(ZmSetting.CSFE_UPLOAD_URI)+"&fmt=extended,raw", true);
        req.setRequestHeader("Cache-Control", "no-cache");
        req.setRequestHeader("X-Requested-With", "XMLHttpRequest");
        req.setRequestHeader("Content-Type", "application/octet-stream;");
        req.setRequestHeader("Content-Disposition", "attachment; filename="+encodeURIComponent(file.fileName));

        var tempThis = req;
        req.onreadystatechange = function() {
            Com_Zimbra_DnD._handleResponse(tempThis);
        }

        req.send(file);

        delete req;

    } catch(exp) {
        var msgDlg = appCtxt.getMsgDialog();
        msgDlg.setMessage(ZmMsg.importErrorUpload, DwtMessageDialog.CRITICAL_STYLE);
        msgDlg.popup();
        return false;
    }
};


Com_Zimbra_DnD._handleErrorResponse = function(respCode) {

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

};

Com_Zimbra_DnD._handleResponse = function(req) {
    if(req) {
        if(req.readyState == 4 && req.status == 200) {
            var resp = eval("["+req.responseText+"]");

            Com_Zimbra_DnD._handleErrorResponse(resp[0]);

            if(resp.length > 2) {
                var respObj = resp[2];
                for (var i = 0; i < respObj.length; i++) {
                    if(respObj[i].aid != "undefined") {
                        Com_Zimbra_DnD.attachment_ids.push(respObj[i].aid);
                    }
                }

                if(Com_Zimbra_DnD.attachment_ids.length > 0 && Com_Zimbra_DnD.attachment_ids.length == Com_Zimbra_DnD.flength) {

                    // locate the compose controller and set up the callback handler
                    var cc = appCtxt.getApp(ZmApp.MAIL).getComposeController(appCtxt.getApp(ZmApp.MAIL).getCurrentSessionId(ZmId.VIEW_COMPOSE));
                    var callback = new AjxCallback (cc,cc._handleResponseSaveDraftListener);

                    attachment_list = Com_Zimbra_DnD.attachment_ids.join(",");
                    cc.sendMsg(attachment_list,ZmComposeController.DRAFT_TYPE_MANUAL,callback);
                }
            }
        }
    }
};