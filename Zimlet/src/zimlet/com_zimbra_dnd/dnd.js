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

Com_Zimbra_DnD.prototype.init =
function () {
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
    if(cmd == 'compose') {
            var self = this;
            setTimeout(function() {
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
                
                var ev = document.createEvent("Events");
			    ev.initEvent("ZimbraDnD", true, false);
                curView._resetBodySize();
                el.dispatchEvent(ev);

            }, 1000);
    }

};

Com_Zimbra_DnD.prototype.onShowView =
function(viewId, isNewView) {
    if ("createEvent" in document && document.getElementById("zdnd_files")) {
        if (viewId == ZmId.VIEW_COMPOSE ||
			viewId == ZmId.VIEW_BRIEFCASE_COLUMN ||
			viewId == ZmId.VIEW_BRIEFCASE ||
			viewId == ZmId.VIEW_BRIEFCASE_DETAIL || viewId.indexOf('COMPOSE') != -1)
		{
			var ev = document.createEvent("Events");
			ev.initEvent("ZimbraDnD", true, false);

			var curView = appCtxt.getAppViewMgr().getCurrentView();

			if (viewId == ZmId.VIEW_COMPOSE || viewId == viewId.indexOf('COMPOSE') != -1) {
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
