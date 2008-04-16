/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Zimbra Collaboration Suite Zimlets
 * Copyright (C) 2005, 2006, 2007 Zimbra, Inc.
 * 
 * The contents of this file are subject to the Yahoo! Public License
 * Version 1.0 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * 
 * ***** END LICENSE BLOCK *****
 */

function Com_Zimbra_DnD() {
}

Com_Zimbra_DnD.prototype = new ZmZimletBase();
Com_Zimbra_DnD.prototype.constructor = Com_Zimbra_DnD;

Com_Zimbra_DnD.prototype.init = function () {

   if(document.getElementById("skin_outer") && !document.getElementById("zdnd_files")) {
     var fileSpan = document.createElement("span");
     fileSpan.id = "zdnd_files";
     fileSpan.style.display = "none";
     fileSpan.innerHTML = this.getConfig("dnd-uploadform");
     document.getElementById("skin_outer").appendChild(fileSpan);
   }

   if(document.getElementById("zdnd_files")) {
       var uploadUri = appCtxt.get(ZmSetting.CSFE_UPLOAD_URI);
       var zDnDUploadFrm = document.getElementById("zdnd_form");
       zDnDUploadFrm.setAttribute("action",uploadUri);
   }
    
};

Com_Zimbra_DnD.prototype.onShowView = function(viewId, isNewView) {
  if("createEvent" in document && document.getElementById("zdnd_files")) {
      if(viewId == ZmId.VIEW_COMPOSE || viewId == ZmId.VIEW_BRIEFCASE_COLUMN || viewId == ZmId.VIEW_BRIEFCASE || viewId == ZmId.VIEW_BRIEFCASE_DETAIL) {
          var curView = appCtxt.getAppViewMgr().getCurrentView();
          var el = curView.getHtmlElement();
          var ev = document.createEvent("Events");
          ev.initEvent("ZimbraDnD", true, false);
          el.dispatchEvent(ev);
      }
  }
};

Com_Zimbra_DnD.uploadDnDFiles = function() {
    var viewId = appCtxt.getAppViewMgr().getCurrentViewId();
    if(viewId == ZmId.VIEW_COMPOSE || viewId == ZmId.VIEW_BRIEFCASE_COLUMN || viewId == ZmId.VIEW_BRIEFCASE || viewId == ZmId.VIEW_.BRIEFCASE_DETAIL) {
        var curView = appCtxt.getAppViewMgr().getCurrentView();
        if(curView) {
            curView.uploadFiles();
        }
    }

};
