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

var zUploadService = {
	fileInputName: "_attFile_",
	uploadFiles:  function(obj) {
		if (self) {
			try { self.focus(); } catch (e) {}
		}
		obj.click();
	},
	processFiles: function(evt, files) {
		try {
			var form = null;
			var inputName = this.fileInputName;
			var inputSize = 50;
			var ownDoc = evt.target.ownerDocument;

			var ulEle = ownDoc.getElementById('zdnd_ul');
			ulEle.innerHTML = '';

			if (files.length > 0) {
				for (var i = 0; i < files.length; i++) {
					var li = ulEle.ownerDocument.createElement('LI');
					var input = ulEle.ownerDocument.createElement('INPUT');
					input.type = 'file';
					input.name = inputName;
					input.size = inputSize;
					input.value = files[i];
					li.appendChild(input);
					ulEle.appendChild(li);
				}
			}
			zUploadService.uploadFiles(ownDoc.getElementById("zDnDBut"));
		} catch(e) {
			// do nothing
		}
	}
};

var zDnDService = {
	canHandleMultipleItems: true,
	onDragStart: function (evt , transferData, action) { },
	onDragOver: function (evt,flavour,session) { },
	onDrop: function (evt, transferData, session) {
		try {
			var td = transferData.flavour? transferData: transferData.first.first;
            if (td.flavour.contentType == "application/x-moz-file") {
				var files = [];
				if (transferData.dataList && transferData.dataList.length > 1) {
					for (var i = 0; i < transferData.dataList.length; i++) {
						var td = transferData.dataList[i];
						for (var j = 0; j < td.dataList.length; j++) {
							var fd = td.dataList[j];
							if (fd.flavour.contentType == "application/x-moz-file") {
								files.push(decodeURI(fd.data.path));
							}
						}
					}
				} else {
					files.push(decodeURI(td.data.path));
				}
				zUploadService.processFiles(evt, files);
			}
			else if (td.flavour.contentType == "text/unicode") {
				var files = [];
				if (transferData.dataList && transferData.dataList.length > 1) {
					for (var i = 0; i < transferData.dataList.length; i++) {
						var td = transferData.dataList[i];
						for(var j = 0; j < td.dataList.length; j++) {
							var fd = td.dataList[j];
							if (fd.flavour.contentType == "text/unicode") {
								var filePath = fd.data;
								var splitType = "file:///";
								if (filePath.substr(0,8) == "file:///") {
									splitType = "file:///";
								} else if(s.substr(0,7) == "file://") {
									splitType = "file://";
								}
								var filePaths = filePath.split(splitType);
								files.push(decodeURI(splitType+filePaths[i+1]));
							}
						}
					}
				} else {
                    files.push(decodeURI(td.data));
				}
				zUploadService.processFiles(evt, files);
			}
		}
		catch(e) {
			// do nothing
		}
	},
	getSupportedFlavours: function () {
		var flavours = new FlavourSet();
		flavours.appendFlavour("application/x-moz-file","nsIFile");
		flavours.appendFlavour("application/x2-moz-file");
		flavours.appendFlavour("text/unicode");
		return flavours;
	}
};

var ZimbraDnD = {
	butEle: null,
    evtAdded: false,
	init: function(e) {
        ZimbraDnD.evtAdded = false;
        var oZmCv = e.target;
		var el = e.target.ownerDocument.getElementById(oZmCv.id + '_zdnd_tooltip');
		if (el) {
			el.style.display = "block";
		}
        if (oZmCv.className) {
          oZmCv.addEventListener("dragdrop", ZimbraDnD.onDrop, false);
          if(!ZimbraDnD.evtAdded) {
            oZmCv.addEventListener("drop", ZimbraDnD.onDrop, false);
          }    
          oZmCv.addEventListener("dragenter", ZimbraDnD.checkDrag, false);
          oZmCv.addEventListener("dragover", ZimbraDnD.checkDrag, false);
        }
		zUploadService.fileInputName = "_attFile_";
	},
	onDrop: function(ev) {
        try {
			nsDragAndDrop.drop(ev, zDnDService);
            ZimbraDnD.evtAdded = true;
		} catch(ex) {
			// do nothing
		}
	},
    checkDrag: function(ev) {
        return true; //ev.dataTransfer.types.contains("application/x-moz-file");
    }
};

document.addEventListener("ZimbraDnD", function(e) { ZimbraDnD.init(e); }, false, true);
