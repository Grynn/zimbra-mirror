/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Zimlets
 * Copyright (C) 2009, 2010 Zimbra, Inc.
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
 * @author ssutar@zimbra.com
 */

/**
 * Object created for the gdocs zimlet
 * @constructor
 */

function com_zimbra_gdocs() {
}

com_zimbra_gdocs.prototype = new ZmZimletBase();
com_zimbra_gdocs.prototype.constructor = com_zimbra_gdocs;

/**
 * Required method inplemented for the initialization of the gdocs zimlet.
 *
 * It gets the object of attachment dialog from app context. It creates the instance of the @see GoogleDocsTabView and adds it to the attachment dialog.
 * 
 */
com_zimbra_gdocs.prototype.init = function() {
    var attachDialog = this._attachDialog = appCtxt.getAttachDialog(),
	    tabview = attachDialog ? attachDialog.getTabView() : null,
        tabLabel = 'Google Docs', //this.getMessage("AttachMailZimlet_tab_label"),
        tabkey,
        callback;

	this.gdView = new GoogleDocsTabView(tabview, this);
	this.gdView.attachDialog = attachDialog;
    tabkey = attachDialog.addTab("gdocs", tabLabel, this.gdView);
	callback = new AjxCallback(this.gdView, this.gdView.uploadFiles);
	attachDialog.addOkListener(tabkey, callback);
};
/**
 * Called by the Zimbra framework when the panel item is double clicked.
 */
com_zimbra_gdocs.prototype.doubleClicked = function() {
	this.singleClicked();
};
/**
 * Called by the Zimbra framework when the panel item is single clicked.
 */
com_zimbra_gdocs.prototype.singleClicked = function() {
    
};

/**
 * Open the window in the center of the screen with the passed URL, called when opening the Google's OAuth page.
 * @param {String} url
 */
com_zimbra_gdocs.prototype.openCenteredWindow = function (url) {
	var width = 800,
	    height = 600,
	    left = parseInt((screen.availWidth / 2) - (width / 2)),
	    top = parseInt((screen.availHeight / 2) - (height / 2)),
	    windowFeatures = "width=" + width + ",height=" + height + ",status,resizable,left=" + left + ",top=" + top + "screenX=" + left + ",screenY=" + top,
	    win = window.open(url, "subWind", windowFeatures);
    
	if (!win) {
		this._showWarningMsg(ZmMsg.popupBlocker);
	}
};

com_zimbra_gdocs.prototype.accessor = {
                consumerKey   : "anonymous",
                consumerSecret: "anonymous",
                serviceProvider: {
                    signatureMethod     : "HMAC-SHA1",
                    scope               : "http://docs.google.com/feeds/ https://docs.google.com/feeds/ http://docs.googleusercontent.com/ https://docs.googleusercontent.com/",
                    requestTokenURL     : "https://www.google.com/accounts/OAuthGetRequestToken",
                    userAuthorizationURL: "https://www.google.com/accounts/OAuthAuthorizeToken",
                    accessTokenURL      : "https://www.google.com/accounts/OAuthGetAccessToken",
                    docListURL          : "http://docs.google.com/feeds/documents/private/full",
                    docExportURL        : "https://docs.google.com/feeds/download/documents/Export"
                }
            };

com_zimbra_gdocs.prototype.oauthHandlerJSP = "oauth3.jsp"

/**
 * Object definition for tab view object.
 * 
 * @constructor
 * @param parent
 * @param zimlet
 * @param className
 */
function GoogleDocsTabView(parent, zimlet, className) {
    this.zimlet = zimlet;
	DwtTabViewPage.call(this, parent, className, Dwt.STATIC_STYLE);
	this.setScrollStyle(Dwt.SCROLL);

}

GoogleDocsTabView.prototype = new DwtTabViewPage;
GoogleDocsTabView.prototype.constructor = GoogleDocsTabView;

/**
 * Called by Zimbra framework when the tab is viewed. It will generate the view depending on the User's linked status, if its not loaded already
 */
GoogleDocsTabView.prototype.showMe = function() {
    if(!this._viewLoaded) {    
        this._contentEl =  this.getContentHtmlElement();
        this._connected = this.zimlet.getUserProperty("gdocs_is_connected");
        this._accessToken = this.zimlet.getUserProperty("gdocs_access_token");
        this._accessTokenSecret = this.zimlet.getUserProperty("gdocs_access_token_secret");
        if(this._connected) {
            //get linked view
            this.getLinkedView();
        }
        else {
            this.getUnlinkedView();
        }
        this._viewLoaded = true;
    }
    DwtTabViewPage.prototype.showMe.call(this);
    this.setSize(Dwt.DEFAULT, "235");	
};
/**
 * Called by zimbra framework to generate the HTML content. This is called only once so used to set the flag _viewLoaded to false
 */
GoogleDocsTabView.prototype._createHtml = function() {
    this._viewLoaded = false;
};
/**
 * called by zimbra framework when the tab lost focus
 */
GoogleDocsTabView.prototype.hideMe = function() {
	DwtTabViewPage.prototype.hideMe.call(this);
};

GoogleDocsTabView.prototype.gotAttachments = function() {
	return false;
};


/**
 * Called when User click on the <strong>Link with Google!</strong> button.
 *
 * It makes a request to the zimlet's oauth page which will add OAuth params such as scope, display name etc, generate the signature.
 * Finally make a call to the Google's token provider page.
 */
GoogleDocsTabView.prototype.getRequestToken = function() {
    this._access_token = null;
    this._access_token_secret = null;
    var accessor = this.zimlet.accessor,
        pArray = [],
        jspUrl,
        message = {
            action: accessor.serviceProvider.requestTokenURL,
            method: "GET",
            parameters: [
                            ["oauth_callback", "oob"],
                            ["scope", accessor.serviceProvider.scope],                            
                            ["xoauth_displayname", "Zimbra"]
                        ]
            };
    OAuth.completeRequest(message, accessor);
    var requestBody = OAuth.formEncode(message.parameters);
    var authorizationHeader = OAuth.getAuthorizationHeader("", message.parameters);

    pArray.push("_action=reqToken");
    pArray.push("_scope="+AjxStringUtil.urlComponentEncode(accessor.serviceProvider.scope));
    pArray.push("_url="+AjxStringUtil.urlComponentEncode(accessor.serviceProvider.requestTokenURL));
    pArray.push("_auth="+AjxStringUtil.urlComponentEncode(authorizationHeader));
	jspUrl = this.zimlet.getResource(this.zimlet.oauthHandlerJSP) + "?" + pArray.join("&");
	AjxRpc.invoke(null, jspUrl, null, new AjxCallback(this, this.getRequestTokenCallback), true);
};
/**
 * Callback to handle the response from the getRequestToken.
 *
 * The zimlet's oauth page send the response in the JSON string containing the request token and request token secret and the authorization URL to which User should be redirected to.
 * This method parse the response and set the one time request tokens. It extracts the authorization URL from the response and open a popup with that URL.
 *
 * @param response
 */
GoogleDocsTabView.prototype.getRequestTokenCallback = function(response) {
    if(response.success) {
        var txt = response.text,
            authorizeUrl,
            jsonResponse = eval("(" + txt + ")"),
            tokens,
            pArray = [];
        
        if(jsonResponse.postResponse) {
            tokens = OAuth.decodeForm(jsonResponse.postResponse);
            this._requestToken = OAuth.getParameter(tokens, "oauth_token");
            this._requestTokenSecret = OAuth.getParameter(tokens, "oauth_token_secret");
            pArray.push("oauth_token="+AjxStringUtil.urlComponentEncode(this._requestToken));
            authorizeUrl = this.zimlet.accessor.serviceProvider.userAuthorizationURL + "?" + pArray.join("&");
            this.zimlet.openCenteredWindow(authorizeUrl);
        }
    }
    else {
        //show error here
    }

};
/**
 * Method to create the tab view when the User is linked with Google
 *
 * It create 2 lists one for folders i.e. labels in Google Docs, the other is the list of docs associated with each folder/label.
 * It calls the method @see getDocList to retrieve the list of docs.
 */
GoogleDocsTabView.prototype.getLinkedView = function() {
    var html = [],
        i = 0,
        button;

    html[i++] = '<div id="gdocs_list_loader" class="GDocsListLoader">Loading the docs, <br>please wait...</div>';
    html[i++] = '<table id="gdocs_list_container" class="GDocsTabView_Table" width="100%"><tbody><tr>';
    html[i++] = '<td id="gdocs_folder_list" width="30%" height="100%" valign="top"></td>';
    html[i++] = '<td id="gdocs_doc_list" width="70%" height="100%" valign="top"></td>';
    html[i++] = '</tr></tbody></table>';
    html[i++] = '<div id="gdocs_refresh_btn" class="GDocsRefreshBtn"></div>';    

    this.getContentHtmlElement().innerHTML = html.join('');
    
    button = new DwtButton({
                        parent:this,
                        parentElement:document.getElementById('gdocs_refresh_btn')
                    });
	button.setText("Refresh the list");
	button.addSelectionListener(new AjxListener(this, this.getDocList, button));

    this._listView = new GoogleDocsListView({
                                        parent: appCtxt.getShell(),
                                        className: "GDocsTabBox GDocsList",
                                        posStyle: DwtControl.RELATIVE_STYLE,
                                        view: ZmId.VIEW_BRIEFCASE_ICON,
                                        type: ZmItem.ATT
                                    });
    this._listView.reparentHtmlElement("gdocs_doc_list");
    Dwt.setPosition(this._listView.getHtmlElement(), Dwt.RELATIVE_STYLE);


    this._folderListView = new GoogleDocsListView({
                                        parent: appCtxt.getShell(),
                                        className: "GDocsTabBox GDocsList",
                                        posStyle: DwtControl.RELATIVE_STYLE,
                                        view: ZmId.VIEW_BRIEFCASE_ICON,
                                        type: ZmItem.ATT
                                    });
    this._folderListView.reparentHtmlElement("gdocs_folder_list");
    Dwt.setPosition(this._folderListView.getHtmlElement(), Dwt.RELATIVE_STYLE);

    //get the doc list
    this.getDocList();

};
/**
 * Called to get the Google Docs' list
 *
 * It makes a request to zimlet's oauth page to get the list of docs
 *  
 * @param button
 */
GoogleDocsTabView.prototype.getDocList = function(button) {
    if(!this._accessToken || !this._accessTokenSecret) {
        this.unlinkFromGoogle();
        this.getUnlinkedView();
        return;
    }
    var accessor = this.zimlet.accessor,
        pArray = [],
        jspUrl,
        refreshBtn = document.getElementById("gdocs_refresh_btn"),
        message;

    document.getElementById("gdocs_list_loader").style.display = "block";
    document.getElementById("gdocs_list_container").style.display = "none";
    if(refreshBtn) {
        refreshBtn.style.display = "none";
    }
    message = {
            method: "get",
            action: accessor.serviceProvider.docListURL,
            parameters: [["showfolders", "true"]]
    };
    OAuth.completeRequest(message,
                            {
                                consumerKey   : accessor.consumerKey,
                                consumerSecret: accessor.consumerSecret,
                                token         : this._accessToken,
                                tokenSecret   : this._accessTokenSecret
                            });
    
    pArray.push("_url=" + AjxStringUtil.urlComponentEncode(accessor.serviceProvider.docListURL));
    pArray.push("_auth=" + AjxStringUtil.urlComponentEncode(OAuth.getAuthorizationHeader("", message.parameters)));
    pArray.push("_action=docList");

    jspUrl = this.zimlet.getResource(this.zimlet.oauthHandlerJSP) + "?" + pArray.join("&");

    AjxRpc.invoke(null, jspUrl, null, new AjxCallback(this, this.getDocListCallback), true);

};
/**
 * Called to parse the server's response for doc list
 *
 * It is called from the docList callback when the successful response is received from the server.
 * It iterates through the response and creates a data structure containing the docs and folders list.
 *  
 * @param docListItems
 */
GoogleDocsTabView.prototype.parseDocListResponse = function(docListItems) {
    var numOfDocs = docListItems.length,
        i,
        j,
        k,
        rawItem,
        item,
        labels,
        folderItem,
        defaultFolder,
        docList = [],
        resourceId;

    this._folderList = [];
    
    for(i=0; i<numOfDocs; i++) {
        rawItem = docListItems[i];
        resourceId = rawItem["gd:resourceId"].__msh_content;
        item = {
            id : resourceId,
            title: rawItem.title.__msh_content,
            size: rawItem["gd:quotaBytesUsed"].__msh_content,
            url: rawItem.content.src,
            type: rawItem.content.type,
            labels: [],
            isFolder: resourceId.indexOf("folder:") !== -1 ? true: false
        };
        if(!item.isFolder && rawItem.category && rawItem.category.length>2) {
            for(j=0; j<rawItem.category.length; j++) {
                if(rawItem.category[j].term.indexOf("http://") === -1) {
                    item.labels.push({title: rawItem.category[j].label});
                }
            }
        }
        if(item.isFolder == true) {
            this._folderList.push(item);
        }
        else {
            docList.push(item);
        }
    }

    for(i=0; i<docList.length; i++) {
        item = docList[i];
        if(item.labels) {
            labels = item.labels;
            for(j=0; j<labels.length; j++) {
                for(k=0; k<this._folderList.length; k++) {
                    folderItem = this._folderList[k];
                    if(folderItem.title == labels[j].title) {
                        if(!folderItem.contents) {
                            folderItem.contents = [];
                        }
                        folderItem.contents.push(item);
                        break;
                    }
                }
            }
        }
    }
    //create root folder object for 0th position
    defaultFolder = {
        title: "Root Folder",
        contents: docList,
        url: null,
        id: "folder:root",
        isFolder: true
    };
    //add the root folder at 0th position
    this._folderList.unshift(defaultFolder);

};
/**
 * Adds items to the list
 * 
 * @param {ZmList} list
 * @param {Array} items
 */
GoogleDocsTabView.prototype.addItemsToList = function(list, items) {
    for(var i=0; i<items.length; i++) {
        list.add(items[i], i);
    }

};
/**
 * Callback to handle the doc list response
 *
 * If response is not received or error occured it will show the unlinked view.
 * If successful response is received it calls the @see parseDocListResponse and adds the items to the list placeholders created.
 * 
 * @param response
 */
GoogleDocsTabView.prototype.getDocListCallback = function(response) {
    var docResponse = eval("(" + response.text + ")"),
        docList = AjxXmlDoc.createFromXml(docResponse.xml).toJSObject(false, false, true),
        numOfDocs,
        tableContainer = document.getElementById("gdocs_list_container"),
        folderList,
        loader = document.getElementById("gdocs_list_loader"),
        refreshBtn = document.getElementById("gdocs_refresh_btn"),
        list;
    
    if(!docResponse.success) {
        if(docResponse.statusCode == 401) {
            this.unlinkFromGoogle();
            this.getUnlinkedView(docResponse.statusCode);
            return;
        }
        else {
            appCtxt.getAttachDialog().setFooter("<strong>We could not retrieve the docs from Google, please try again later.</strong>");
        }
    }
    if(loader) {
        loader.style.display = "none";
    }
    if(tableContainer) {
        tableContainer.style.display = "block";
    }
    if(refreshBtn) {
        refreshBtn.style.display = "block";
    }
    
    numOfDocs = docList.entry.length;
    
    //parse the docs list
    this.parseDocListResponse(docList.entry);

    list = new ZmList(ZmItem.BRIEFCASE_ITEM);
    //the 0th element in folder list is ALWAYS the root folder which contains the list of all the docs
    this.addItemsToList(list, this._folderList[0].contents);
    this._listView.set(list);


    folderList = new ZmList(ZmItem.BRIEFCASE_ITEM);
    this.addItemsToList(folderList, this._folderList);
    this._folderListView.set(folderList);

    tableContainer.onclick = AjxCallback.simpleClosure(this.showFolderContents, this);
    this.setSize(Dwt.DEFAULT, "235");
};

/**
 * Called when folder item is clicked
 *
 * It shows the docs associated with the clicked folder item in the docs' list view.
 * 
 * @param {Event} e
 */
GoogleDocsTabView.prototype.showFolderContents = function(e) {
    var target = DwtUiEvent.getTarget(e),
        cssClass = target.className,
        isFolder = cssClass.indexOf("folder") != -1 ? true : false,
        k,
        folderItem,
        list,
        targetId = target.id;

    if(isFolder) {
        for(k=0; k<this._folderList.length; k++) {
            folderItem = this._folderList[k];
            if(folderItem.id == targetId) {
                if(!folderItem.contents) {
                    folderItem.contents = [];
                }
                list = new ZmList(ZmItem.BRIEFCASE_ITEM);
                this.addItemsToList(list, folderItem.contents);
                this._listView.set(list);
                break;
            }
        }
        
    }
    //this return is required to stop the event propagation :)
    return false;
};
/**
 * Callback to attach the files
 *
 * It gets the list of selected files and makes a request to the zimlet's oauth page to download the file as bytes and upload as attachment.
 * Successful response will contain the resource id of the attachment. This method is called once for each of the files selected to attach.
 *
 * @param {ZmAttachDialog} attachDialog
 * @param {Array} docIds
 * @param {int} index
 * @param response
 */
GoogleDocsTabView.prototype.uploadFiles = function(attachDialog, docIds, index, response) {
    var accessor = this.zimlet.accessor,
        pArray = [],
        jspUrl,
        docId,
        items,
        message,
        extension,
        exportDocId,
        exportUrl,
        currentItem,
        cc,
        callback;
    
    items = this._listView.getSelection();
    if (!items || (items.length == 0)) {
        appCtxt.getAttachDialog().setFooter(ZmMsg.attachSelectMessage);
        return;
    }

    if(!docIds) {
        docIds = [];
    }

    if(!index) {
        index = 0;
    }
    if(response && response.text) {
        docId = this.getUploadIdFromResponse(response.text);
        if(docId) {
            docIds.push(docId);
        }
    }
    if(index < items.length) {
        appCtxt.getAttachDialog().setFooter("Attaching "+(index+1)+" of "+items.length+" files. Please wait.");
        //document.getElementById("gdocs_attachment_status").innerHTML = "Attaching "+(index+1)+" of "+items.length+" files. Please wait.";
        docId = false;
        currentItem = items[index];
        if(currentItem.title.lastIndexOf(".") === -1) {
            extension = "html";
        }
        else {
            extension = currentItem.title.substring(Number(currentItem.title.lastIndexOf("."))+1);
        }
        exportDocId = currentItem.id.substring(Number(currentItem.id.indexOf(":"))+1);
        exportUrl = "https"+currentItem.url.substring(currentItem.url.indexOf(":")) + "&format="+extension;
        message = {
            method: "get",
            action: exportUrl,
            parameters: []
        };
        OAuth.completeRequest(message,
                                {
                                    consumerKey   : accessor.consumerKey,
                                    consumerSecret: accessor.consumerSecret,
                                    token         : this._accessToken,
                                    tokenSecret   : this._accessTokenSecret
                                });
        pArray.push("_action=postResource");
        pArray.push("_url=" + AjxStringUtil.urlComponentEncode(exportUrl)); //"https"+currentItem.url.substring(currentItem.url.indexOf(":")) + "&format="+extension));//accessor.serviceProvider.docExportURL+ "?format="+extension+"&id="+exportDocId));
        pArray.push("_auth=" + AjxStringUtil.urlComponentEncode(OAuth.getAuthorizationHeader("", message.parameters)));
        pArray.push("_fid=" + AjxStringUtil.urlComponentEncode(currentItem.title));
        pArray.push("_uploader=" + AjxStringUtil.urlComponentEncode(appCtxt.get(ZmSetting.CSFE_ATTACHMENT_UPLOAD_URI)+"?fmt=raw&upload=1"));

        jspUrl = this.zimlet.getResource(this.zimlet.oauthHandlerJSP) + "?" + pArray.join("&");
        AjxRpc.invoke(null, jspUrl, null, new AjxCallback(this, this.uploadFiles, [attachDialog, docIds, ++index]), true);
    }
    else {
        //document.getElementById("gdocs_attachment_status").innerHTML = "";
        cc = appCtxt.getApp(ZmApp.MAIL).getComposeController(appCtxt.getApp(ZmApp.MAIL).getCurrentSessionId(ZmId.VIEW_COMPOSE));
        callback = new AjxCallback (cc,cc._handleResponseSaveDraftListener);

        cc.sendMsg(docIds.join(","), ZmComposeController.DRAFT_TYPE_MANUAL,callback);
    }
};
/**
 * Called from callback @see uploadFiles to extract the resource id of the attached doc from the response.
 * 
 * @param {String} responseText
 */
GoogleDocsTabView.prototype.getUploadIdFromResponse = function(responseText) {
    var re = new RegExp("'([^']+)'", "m"),
        re_id = new RegExp ("^[0-9a-f:-]+$","im"),
        s,
        i,
        m;

    if (!responseText) {
        return false;
    }
    // responseText is some html code with embedded strings inside ''
    s = responseText;
    for (i=s.search(re); (i!=-1) && (s.length>0); i=s.search(re)) {
        m = re.exec (s);
        if (!m) {
            break;
        }
        if (m[1].match(re_id)) {
            return m[1];
        }
        s = s.substring(i+m[0].length);
    }

};
/**
 * Called to remove the tokens from User settings
 */
GoogleDocsTabView.prototype.unlinkFromGoogle = function() {
    this.zimlet.setUserProperty("gdocs_is_connected", false);
    this.zimlet.setUserProperty("gdocs_access_token", "");
    this.zimlet.setUserProperty("gdocs_access_token_secret", "");
    this.zimlet.saveUserProperties();
    this._viewLoaded = false;
    this._connected = false;
};
/**
 * Called to get the view in unlinked state
 */
GoogleDocsTabView.prototype.getUnlinkedView = function(statusCode) {
    var html = [],
        i = 0,
        button,
        okBtn;
    if(statusCode) {
        appCtxt.getAttachDialog().setFooter('<strong> An authentication error occurred maybe because the tokens are expired or invalid. Please link your Google account again to access your docs.</strong>');
    }
    html[i++] = '<ol id="gdocs_linking_steps">';
    html[i++] = '<li> Click on the <strong>Link with Google!</strong> button to open Google\'s authentication/authorization page. <div id="gdocs_link_btn" class="GDocsRefreshBtn"></div></li>';
    html[i++] = '<li> Enter your Google account information there.</li>';
    html[i++] = '<li> Press Grant Access button.</li>';
    html[i++] = '<li> Google will provide a verification code.</li>';
    html[i++] = '<li> Copy and paste that code below and press the <strong>OK</strong> button</li>';
    html[i++] = '<li><div id="gdocs_verify_form" class="GDocsVerifyForm"><strong>Enter Verification Code :</strong> <input type="text" id="gdocs_act_code" value=""><div class="GDocsVerifyBtn" id="gdocs_verify_okbtn"></div></li>';
    html[i++] = '</ol>';
    this.getContentHtmlElement().innerHTML = html.join('');

    button = new DwtButton({parent:this, parentElement:document.getElementById('gdocs_link_btn')});
	button.setText("Link with Google!");
	button.addSelectionListener(new AjxListener(this, this.getRequestToken, button));

    okBtn = new DwtButton({parent:this, parentElement:document.getElementById('gdocs_verify_okbtn')});
	okBtn.setText("OK");
	okBtn.addSelectionListener(new AjxListener(this, this.getAccessToken, okBtn));

};
/**
 * Called to get the acccess token
 *
 * It makes request to the zimlet's oauth page with the request tokens and verification code.
 */
GoogleDocsTabView.prototype.getAccessToken = function() {
    var accessor = this.zimlet.accessor,
        code = AjxStringUtil.trim(document.getElementById('gdocs_act_code').value),
        pArray = [],
        jspUrl,
        message = {};

    if(code == undefined || code == "") {
        return;
    }
    message = {
            method: "post",
            action: accessor.serviceProvider.accessTokenURL,
            parameters: [["oauth_verifier", code]]
        };
    OAuth.completeRequest(message, {
                                consumerKey   : accessor.consumerKey,
                                consumerSecret: accessor.consumerSecret,
                                token         : this._requestToken,
                                tokenSecret   : this._requestTokenSecret
                            });
    pArray.push("_action=accessToken");
    pArray.push("_vc=" + AjxStringUtil.urlComponentEncode(code));
    pArray.push("_url=" + AjxStringUtil.urlComponentEncode(accessor.serviceProvider.accessTokenURL));
    pArray.push("_auth=" + AjxStringUtil.urlComponentEncode(OAuth.getAuthorizationHeader("", message.parameters)));

    jspUrl = this.zimlet.getResource(this.zimlet.oauthHandlerJSP) + "?" + pArray.join("&");
	AjxRpc.invoke(null, jspUrl, null, new AjxCallback(this, this.getAccessTokenCallback), true);
};
/**
 * Callback to handle the access token response
 *
 * If successful it parses the response and retrieves the access token and access token secret.
 * It also saves the tokens to the corresponding user properties and loads the linked view.
 * 
 * @param response
 */
GoogleDocsTabView.prototype.getAccessTokenCallback = function(response) {
    if(response.success) {
        var txt = response.text,
            jsonResponse = eval("(" + txt + ")"),
            tokens;

        if(jsonResponse.postResponse) {
            tokens = OAuth.decodeForm(jsonResponse.postResponse);
            this._accessToken = OAuth.getParameter(tokens, "oauth_token");
            this._accessTokenSecret = OAuth.getParameter(tokens, "oauth_token_secret");

        }
    }
    if(this._accessToken && this._accessTokenSecret) {
        this._connected = true;
        this.zimlet.setUserProperty("gdocs_is_connected", true);
        this.zimlet.setUserProperty("gdocs_access_token", this._accessToken);
        this.zimlet.setUserProperty("gdocs_access_token_secret", this._accessTokenSecret);
        this.zimlet.saveUserProperties();
        this.getLinkedView();
    }
};
/**
 * Overrridden method to set the size of the tab view.
 *
 * @param {Number} width
 * @param {Number} height
 */
GoogleDocsTabView.prototype.setSize =
function(width, height) {

    DwtTabViewPage.prototype.setSize.call(this, width, height);

    var size = this.getSize(),
        treeWidth = size.x * 0.40,
        listWidth = size.x - treeWidth,
        newHeight = height - 55;
	if (this._folderListView) {
		this._folderListView.setSize(treeWidth - 10, newHeight);
		this._listView.setSize(listWidth - 15, newHeight);
	}

    return this;
};



/**
 * @class
 * The Google docs list view.
 *
 * @extends		ZmListView
 */
GoogleDocsListView = function(params) {
	ZmListView.call(this, params);
	this._controller = new GoogleDocsController();
};

GoogleDocsListView.prototype = new ZmListView;
GoogleDocsListView.prototype.constructor = GoogleDocsListView;

GoogleDocsListView.prototype._getDivClass =
function(base, item, params) {
	return "";
};
/**
 * Overridden method to create the HTML for the doc list and folder list items
 * 
 * @param doc
 * @param params
 */
GoogleDocsListView.prototype._createItemHtml =
function(doc, params) {

	
	var div = document.createElement("div"),
        html = [],
	    j = 0,
        i,
        icon,
        labelHtml = '',
        mimeInfo,
        cssClass = doc.isFolder === true ? 'folder' : '';
    
    div.className = "GDocsItemSmall";
    html[j++] = "<table><tbody>";
    /*
    if(doc.labels) {
        labelHtml = "[";
        for(k=0; k<doc.labels.length; k++) {
            labelHtml += " <a id=\""+doc.labels[k].url+"\" href=\"javascript:void();\"><em>"+doc.labels[k].title+"</em></a> ";
        }
        labelHtml += "]";
    }*/
    html[j++] = "<td>";
    if(doc.isFolder) {
        html[j++] = '&nbsp;';
        icon = "Folder";
    }
    else {
        j = this._getImageHtml(html, j, "CheckboxUnchecked", this._getFieldId(doc, ZmItem.F_SELECTION));
        mimeInfo = doc.type ? ZmMimeTable.getInfo(doc.type) : null;
		icon = mimeInfo ? mimeInfo.image : "UnknownDoc" ;
    }
    html[j++] = "</td>";
    html[j++] = "<td><div class='Img";
	html[j++] = icon;
	html[j++] = "'></div></td>";
    html[j++] = "<td>";
    html[j++] = "<a id=\"" + doc.id + "\" class=\"" + cssClass + "\" href=\"" + doc.url + "\" class=\"GDocsDocLink\">";
    html[j++] = doc.title;
    html[j++] = "</a> ";
    html[j++] = "</td>";
    html[j++] = "<td>";
    html[j++] = labelHtml;
    html[j++] = "</td>";
    html[j++] = "</tbody></table>";
    //listContainer.innerHTML = html.join("");


	div.innerHTML = html.join("");

	this.associateItemWithElement(doc, div);
	return div;
};


/**
 * @class
 * The attach mail controller.
 *
 * @extends		ZmListController
 */
GoogleDocsController = function(container, app) {
	if (arguments.length == 0) { return; }

};

GoogleDocsController.prototype = new ZmListController;
GoogleDocsController.prototype.constructor = GoogleDocsController;

GoogleDocsController.prototype._resetToolbarOperations =
function() {
	// override to avoid js expn although we do not have a toolbar per se
};
