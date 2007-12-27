/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Zimbra Collaboration Suite Zimlets
 * Copyright (C) 2007 Zimbra, Inc.
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

////////////////////////////////////////////////////////////////
///  Zimlet to handle integration with Snapfish              ///
///  @author Rajesh Segu, <rajesh.segu@zimbra.com>           ///
////////////////////////////////////////////////////////////////


/*TO-DO's:
 *  Change to SNAPFISH API V 2.0 - its configurable , so its easy to change
 *  
 * 
 * 
 * 
 */

function Com_Zimbra_Snapfish(){
}

/// Zimlet handler objects, such as Com_Zimbra_Snapfish, must inherit from
/// ZmZimletBase.  The 2 lines below achieve this.
Com_Zimbra_Snapfish.prototype = new ZmZimletBase();
Com_Zimbra_Snapfish.prototype.constructor = Com_Zimbra_Snapfish;

Com_Zimbra_Snapfish.LOGIN_SERVER = "http://www.sfus7.qa.snapfish.com/externalapi/v2";

Com_Zimbra_Snapfish.SNAPFISH ="SNAPFISH";

//Used for testing. To test just uncomment this and few lines in Com_Zimbra_Snapfish.prototype.login
//Com_Zimbra_Snapfish.PABGUID = '230659332129052007Comcast.USR4JR';
//Com_Zimbra_Snapfish.USER = 'aa.resi.sit1@comcast.net';
// Shouldn't need this.
//Com_Zimbra_Snapfish.PWD = 'password';

ZmMsg.snapfishPhotoCenter = "Snapfish Photo Center";
ZmMsg.comcastPhotoCenter  = "Comcast Photo Center";


Com_Zimbra_Snapfish.prototype.init = function() {
    
    this.SERVER = this.getConfig("loginServer");
	
    // We MUST use the enterprise URN in order for certain "advanced"
    // features such as adding a contact to an account
    this.XMLNS = "http://www.snapfish.com/externalapi";
	
	//Uncomment this if you want "Save to Snapfish" links beside image attachments
	this.addAttachmentHandler();
	
	this._login = false;	
	window._snapfishCtxt = this;
	
	//this.login();
	this._addSnapfishTabToAttachDialog(ZmMsg.comcastPhotoCenter);
};

Com_Zimbra_Snapfish.prototype.singleClicked = function() {
	this.login();
};

Com_Zimbra_Snapfish.prototype.doubleClicked = function() {
  this.singleClicked(); 
};

/// Called by the Zimbra framework when some menu item that doesn't have an
/// <actionURL> was selected
Com_Zimbra_Snapfish.prototype.menuItemSelected = function(itemId) {
	switch (itemId) {
	    
	    case "LOGIN":
			this.login(this.showAlbums,true);
			break;
			
		case "REGISTER":		
			this.register();
			break;
	}
};

Com_Zimbra_Snapfish.prototype.doDrop = function(obj) {
	
	switch (obj.TYPE) {
		
	    case "ZmMailMsg":
	    //case "ZmConv":   //Need to handle it in ZmZimletContext
	   		this.msgDropped(obj);
			break;
		
	    default:
			this.displayErrorMessage("You somehow managed to drop a \"" + obj.TYPE + "\" but however this Zimlet does't support it for drag'n'drop.");
			break;
	}
};



//Snapfish: Add SnapfishTabView to AttachDialog

Com_Zimbra_Snapfish.prototype._addSnapfishTabToAttachDialog = function(title){
	
	var attachDialog = this._attachDialog = appCtxt.getAttachDialog();
	var tabView = attachDialog ? attachDialog.getTabView():null;
	
	title = title || ZmMsg.snapfishPhotoCenter;
	this._snapfishTabView = new SnapfishTabView(tabView, this);
	var tabKey = attachDialog.addTab("SNAPFISH",title,this._snapfishTabView);
	
	var okCallback = new AjxCallback(this,this._okListener);
	attachDialog.addOkListener(tabKey,okCallback);
};

Com_Zimbra_Snapfish.prototype.isInline = function(){
   return this._attachDialog.isInline(); 
};


Com_Zimbra_Snapfish.prototype._okListener = function(callback){
	
	var callback = false;
	if(appCtxt.getAppViewMgr()._currentView == ZmController.COMPOSE_VIEW){
		callback = new AjxCallback(this,this.composeDraftMsg,this.isInline());
	}
	//Handle Other Cases as you wish.
	this.attachPhotos(callback);
};

Com_Zimbra_Snapfish.prototype.composeDraftMsg = function(isInline){
		
	this._composerCtrl = appCtxt.getApp(ZmApp.MAIL).getComposeController();
	var ajxCallback = new AjxCallback(this, this._composerCtrl._handleResponseSaveDraftListener);
	this._composerCtrl.sendMsg(( isInline? this._attachmentIdsList : this._attachmentIdsList.join(",")),ZmComposeController.DRAFT_TYPE_MANUAL,ajxCallback);
	
	this.setStatusMsg(this._imagesToAttachIndex+" photos attached to the mail");
	
	this._snapfishTabView._clearImageSelection();
};

//Snapfish: Attach Photos to Mail
Com_Zimbra_Snapfish.prototype.attachPhotos = function(callback){
	
	var imagesToAttach = [];
	var images = this._snapfishTabView._selectionBoxIds;
	if(!images) return;
	for(i=0;i<images.length;i++){
		var image = document.getElementById(images[i].id);
		if(image.checked){
			imagesToAttach.push(images[i]);
		}
	}
	
	if(imagesToAttach.length == 0){
		this.setStatusMsg("<b>Select atleast one photo and then proceed.</b>")
		return;
	}
	
	//Display Progress Info
	this.setStatusMsg("Attaching "+ imagesToAttach.length +" photos");
	
	
	this._imagesToAttach = imagesToAttach;
	this._imagesToAttachIndex = 0;
	
	//Attaching the images to the mail recursively
	var image = this._imagesToAttach[this._imagesToAttachIndex];
	this._imagesToAttachIndex++;
	this._attachmentIdsList = [];
	var handleAttachCallback = new AjxCallback(this,this._handleAttachPhotos,[callback]);
	this.attachImage(image.url,image.caption,handleAttachCallback);
};

//Snapfish: Attach Images to mail recursively
Com_Zimbra_Snapfish.prototype._handleAttachPhotos = function(callback,attachmentId,imageURL,imageCaption){
	
	if(attachmentId) { 
		this._attachmentIdsList.push(attachmentId);
	}
	
	this.setStatusMsg(attachmentId  
					?	(this._imagesToAttachIndex+1)+" of " +this._imagesToAttach.length+" photos attached"
					:	"Failed to attach image <b>"+imageCaption+"</b>");
	
	if(this._imagesToAttach.length == this._imagesToAttachIndex){
		callback.run();
		return;
	}
	
	var image = this._imagesToAttach[this._imagesToAttachIndex];
	this._imagesToAttachIndex++;
	
	var handleAttachCallback = new AjxCallback(this,this._handleAttachPhotos,[callback]);
	this.attachImage(image.url,image.caption,handleAttachCallback);

};

Com_Zimbra_Snapfish.prototype.setStatusMsg = function(html){
	if(this._attachDialog)
		this._attachDialog.setFooter(html);
	else
		this.displayStatusMessage(html);
};

///------------------------------- SnapfishTabView -----------------------------------

SnapfishTabView = function(parent,zimlet,className,posStyle) {	
	
	if (arguments.length == 0) return;
	
	//className = className || "DwtTabViewPage";
	DwtTabViewPage.call(this,parent,className,Dwt.STATIC_STYLE);
	
	this.zimlet = zimlet;
};

SnapfishTabView.prototype = new DwtTabViewPage;
SnapfishTabView.prototype.constructor = SnapfishTabView;

SnapfishTabView.prototype.toString = function(){
	return "SnapfishTabView";
};

SnapfishTabView.prototype.gotAttachments = function() {
	if (this._selectionBoxIds) {
		return true;
	} else {
		return false;
	}
};

SnapfishTabView.prototype._createHtml = function(){
	
	var snapfishId = Dwt.getNextId();
	var snapTitleId = Dwt.getNextId();
	var snapInfoId = Dwt.getNextId();
	var snapSelectAllId = Dwt.getNextId();
	var snapContainerId = Dwt.getNextId();
	var snapFooterId = Dwt.getNextId();
	
	var html = [ 	"<div id='",snapfishId,"' class='SnapFish'>", 
						"<div class='SnapHead'>",
							"<div class='SnapAlbumName'>",
				  				"<span id='",snapTitleId,"'>All Albums</span>", "<br>",
				  				"<span><strong><span  id='",snapInfoId,"' class='SnapInfo'>Select an album below attach photos from Comcast Photo Center to your message</span></strong></span>",
				  				"<div id='",snapSelectAllId,"' class='SnapSelectAll'><a href='#'><span></span></a></div>",
							"</div>",
						"</div>",
						"<div id='",snapContainerId,"' class='SnapContainer'>","</div>",
					"</div>"
				].join("");
	
	this._contentEl =  this.getContentHtmlElement();
	this._contentEl.innerHTML = html;
	
	//Global References
	this._snapfishAlbumNameDiv = document.getElementById(snapTitleId);
	delete snapTitleId;
	this._snapfishAlbumInfoDiv = document.getElementById(snapInfoId);
	delete snapInfoId;
	this._snapfishSelectAllDiv = document.getElementById(snapSelectAllId);
	delete snapSelectAllId;
	this._snapfishContainerDiv = document.getElementById(snapContainerId);
	delete snapContainerId;
	
};



SnapfishTabView.prototype.cleanUp = function(){
	this._snapfishContainerDiv.innerHTML = "";
};

SnapfishTabView.prototype.showMe = function(){
	
	//CleanUp
	this.cleanUp();
	
	//Create ShowAlbums UI
	this.showAlbums();
	
	DwtTabViewPage.prototype.showMe.call(this,parent);
	this.setSize(Dwt.DEFAULT, "240");
	
};

SnapfishTabView.prototype.showAlbums = function(){
	if(!this.zimlet._login){
		var callback =  new AjxCallback(this,this.showAlbums);
		this.zimlet.login(callback);
		return;
	}
	var callback = new AjxCallback(this,this._populateAlbums);
	this.zimlet.getAlbums(callback);
};

SnapfishTabView.prototype._populateAlbums = function(ans){
	
	var div,imgdiv,adiv, html = "";
	
	//Clearing prior Content if any
	var container = this._snapfishContainerDiv;
	container.innerHTML = "";
	//this._snapfishFooterDiv.innerHTML = "";
	
	//All albums Container
	this._snapfishAlbumNameDiv.innerHTML = "All Albums";
	
	//Setting the Albums Info
	this._snapfishAlbumInfoDiv.innerHTML = "Click on an album to access photos inside.";
	
	/* Sample Container Div
	<div class="SnapPhoto albumCover">
	<div>
	<img src="snap.jpg" border="0" alt="PhotoTitle" title="Photo Title">
	<br>
	<a href="#">Photo Tite #3</a>
	</div>
	</div>
	*/
	
	var albums = ans.albums.album;
	albums = albums.length?albums:[albums];
	for(i=0;i<albums.length;i++){
		div = document.createElement("div");
		div.id = String(albums[i].id);
		div.className = "SnapPhoto albumCover";
		
		adiv = document.createElement("a");
		adiv.href = "#";
		adiv.onclick = AjxCallback.simpleClosure(this._showPhotos, this, String(albums[i].id));
		adiv.innerHTML = albums[i].name;
		
		imgdiv = document.createElement("img");
		imgdiv.border = "0";
		imgdiv.alt = imgdiv.title = String(albums[i].name);
		imgdiv.src = albums[i].firsttnurl;
		
		div.appendChild(imgdiv);
		div.appendChild(document.createElement("br"));
		div.appendChild(adiv);
		container.appendChild(div);
	}
	
	this._showSelectAll(false);
};

SnapfishTabView.prototype._showPhotos = function(albumId){
	var callback = new AjxCallback(this,this._populatePhotos,albumId);
	this.zimlet.getAlbumInfo(albumId,callback);
};

SnapfishTabView.prototype._populatePhotos = function(albumId,ans){
	
	var div,imgdiv,adiv, span,input,html = "";
	
	//Clearing prior Content if any
	var container = this._snapfishContainerDiv;
	container.innerHTML = "";
	//this._snapfishFooterDiv.innerHTML = "";
	
	//All Albums Container
	//<a href="#">All albums</a> >> Album #1
	this._snapfishAlbumNameDiv.innerHTML = "";
	span = document.createElement("span");
	adiv = document.createElement("a");
	adiv.href="#";
	adiv.onclick = AjxCallback.simpleClosure(this.showAlbums,this);
	adiv.innerHTML = "All Albums";
	span.appendChild(adiv);
	this._snapfishAlbumNameDiv.appendChild(span);
	
	span = document.createElement("span");
	span.innerHTML = " >> " + String(ans.name);
	this._snapfishAlbumNameDiv.appendChild(span);
	
	//Album Info Container
	this._snapfishAlbumInfoDiv.innerHTML = "Select photo(s) and click Attach";
	
	/*
	<div class="SnapPhoto">
	<img src="snap.jpg" border="0" alt="PhotoTitle" title="Photo Title">
	<input type="checkbox"checked>
	<a href="#">Photo Tite #1</a>
	</div>
	*/
	var selectionBoxIds = [];
	var selectAllBoxIds =[];
	var pictures = ans.pictures.picture;
	pictures = pictures.length?pictures:[pictures];
	
	for(i=0;i<pictures.length;i++){
	
		div = document.createElement("div");
		div.id = String(pictures[i].id)+"_div";
		div.className = "SnapPhoto";
		
		adiv = document.createElement("a");
		adiv.href = "#";
		adiv.onclick = AjxCallback.simpleClosure(this.showPhotoInNewWindow, this, String(pictures[i].srurl),String(pictures.caption));
		adiv.innerHTML = pictures[i].caption;
		
		imgdiv = document.createElement("img");
		imgdiv.border = "0";
		imgdiv.alt = imgdiv.title = String(pictures[i].caption);
		imgdiv.src = pictures[i].tnurl;
		
		input = document.createElement("input");
		input.type = "checkbox";
		input.id = String(pictures[i].id);
		input.name = "image";
		input.onclick = AjxCallback.simpleClosure(this._updatePhotoSelectCount,this,input.id);
		
		div.appendChild(imgdiv);
		div.appendChild(document.createElement("br"));
		div.appendChild(input);
		div.appendChild(adiv);
		container.appendChild(div);
		
		selectionBoxIds.push({id:String(pictures[i].id),url:String(pictures[i].srurl),caption:String(pictures[i].caption)});
		selectAllBoxIds.push(String(pictures[i].id));
	}
	
	this._selectionBoxIds = selectionBoxIds;
	
	this._selectedImageCount = 0;
	
	this._showSelectAll(true,selectAllBoxIds);
};



//Utility Funtions
SnapfishTabView.prototype._showSelectAll = function(enable,checkboxIds){
	
	if(typeof enable == "undefined"){
		enable = true;
	}

	if(this._snapfishSelectAllDiv){
		if(enable){
			this._snapfishSelectAllDiv.innerHTML = "";
			var adiv = document.createElement("a");
			adiv.href = "#";
			adiv.onclick =  AjxCallback.simpleClosure(this._handleSelectAll,this,checkboxIds);
			var span = document.createElement("span");
			span.innerHTML = "Select All"
			
			adiv.appendChild(span);
			this._snapfishSelectAllDiv.appendChild(adiv);
				
		}else{
			this._snapfishSelectAllDiv.innerHTML = "";
		}
	}
};

SnapfishTabView.prototype._handleSelectAll = function(checkboxIds){
	
	if(!checkboxIds){
		return;
	}
	
	var checkbox;
	
	for(i=0; i<checkboxIds.length ;i++){
		checkbox = document.getElementById(checkboxIds[i]);
		checkbox? checkbox.checked = true: "" ;
	}
	
	this._selectedImageCount = checkboxIds.length;
	
	var message = (this._selectedImageCount == 0)?"": (this._selectedImageCount+" photos selected");
	this.zimlet.setStatusMsg(message);
	
};

SnapfishTabView.prototype._clearImageSelection = function(){
	var images = this._selectionBoxIds;
	for(i=0;i<images.length;i++){
		var image = document.getElementById(images[i].id);
		image.checked = false;
	}
	this._selectedImageCount = 0;
};


SnapfishTabView.prototype._updatePhotoSelectCount = function(selected){
	
	var imageSelectBox = document.getElementById(selected);
	var newImageCount = (imageSelectBox && imageSelectBox.checked)? ++this._selectedImageCount : --this._selectedImageCount;	
	var message = (newImageCount == 0)?"": (newImageCount+" photos selected");

	this.zimlet.setStatusMsg(message);
	
};

SnapfishTabView.prototype.showPhotoInNewWindow = function(imageUrl,imageName){
	if(!imageName){
		imageName = "Image";
	}
	window.open(imageUrl,"Snapfish:"+imageName,"height=420,width=420,toolbar=no,scrollbars=yes,menubar=no");
};

SnapfishTabView.prototype._handleInline = function(inline) {};

//--------------------------- Snapfish: End of SnapfishTabView -----------------------------

//Snapfish: Add "Save to Snapfish" links
Com_Zimbra_Snapfish.prototype.addAttachmentHandler = function(){
	this._msgController = AjxDispatcher.run("GetMsgController");
	this._msgController._initializeListView(ZmController.MSG_VIEW);
	this._msgController._listView[ZmController.MSG_VIEW].addAttachmentLinkHandler(ZmMimeTable.IMG_JPEG,"snapfish",this.addSaveToSnapfishLink);
};

Com_Zimbra_Snapfish.prototype.addSaveToSnapfishLink = function(attachment){	
	var html = [
		"<a href='#' class='AttLink' style='text-decoration:underline;' onclick=\"Com_Zimbra_Snapfish.saveToSnapfishLinkClicked('",attachment.ct,"','",attachment.label,"','",attachment.url,"');\">",
		"Save to Comcast photo center",
		"</a>"
	].join("");
	return html;
};

Com_Zimbra_Snapfish.saveToSnapfishLinkClicked = function(contentType,imageLabel,imageUrl){
	var attachment = [{
		ct:contentType,
		url:imageUrl,
		label:imageLabel		
	}];
	window._snapfishCtxt.msgDropped({attlinks:attachment});
};

//SnapFish:	Handle Mail Drop

Com_Zimbra_Snapfish.prototype.msgDropped = function(mailMsg){
	
	if(!this._login){
		var callback = new AjxCallback(this,this.msgDropped,mailMsg);	
		this.login(callback);
		return;
	}
	
	var attLinks = mailMsg.attlinks;
	if(attLinks && attLinks.length != 0){
		this._attLinks = attLinks;
		var showAttLinksCallback = new AjxCallback(this,this._showAttLinks)
		this.getAlbums(showAttLinksCallback);
	}else{
		this.displayErrorMessage("<b>No attachment images found</b>");
	}
};

Com_Zimbra_Snapfish.prototype._showAttLinks = function(ans){
	
	var attachment,div,imgdiv,adiv, span,input,html = "";
	
	/*
	<div class="SnapPhoto">
	<img src="snap.jpg" border="0" alt="PhotoTitle" title="Photo Title">
	<input type="checkbox"checked>
	<a href="#">Photo Tite #1</a>
	</div>
	*/
	var albums = ans.albums.album;
	albums = albums.length ? albums : [albums];
	
	var view = new DwtComposite(this.getShell());
	
	var snapContainerId = Dwt.getNextId();
	var snapSelectAlbumId = Dwt.getNextId();
	var snapFooterId = Dwt.getNextId();

	var html = [ 	"<div class='SnapFish'>", 
						"<div class='SnapHead'>",
							"<div class='SnapAlbumName'>",
				  				"<span>Available Photos</span>", "<br>",
				  				"<span><strong><span  class='SnapInfo'>Select the photos you wish to add to Comcast Photo Center</span></strong></span>",
				  				/*"<div  class='SnapSelectAll'>Add to Album:<span></span></div>",*/
							"</div>",
						"</div>",
						"<div id='",snapContainerId,"' class='SnapContainer'>","</div>",
						"<div>Add to Album: <span id='",snapSelectAlbumId,"'>","</span></div>",
						"<div id='",snapFooterId,"' class='SnapFooter'>","</div>",
					"</div>"
				].join("");

	view.getHtmlElement().innerHTML = html;
	var snapContainer = document.getElementById(snapContainerId);
	delete snapContainerId;
	var selectAlbumContainer = document.getElementById(snapSelectAlbumId);
	delete snapSelectAlbumId;
	var footerContainer = document.getElementById(snapFooterId);
	delete snapFooterId;
	
	//Construct Album SelectionId
	var albumOptions = [];
	albumOptions.push(new DwtSelectOption(String(albums[0].id),true,String(albums[0].name)));
	for(var i=1; i< albums.length; i++){
		albumOptions.push(new DwtSelectOption(String(albums[i].id),false,String(albums[i].name)));
	}
	var albumSelect = new DwtSelect(view,albumOptions);
	
	selectAlbumContainer.appendChild(albumSelect.getHtmlElement());

	//Consturct UI
	var selectionBoxIds = [];
	var attLinks = this._attLinks;
	for(i=0;i<attLinks.length;i++){
		
		attachment = attLinks[i];	
		if(attachment.ct.indexOf("jpeg") == -1)
				continue;
		
		var imageId = Dwt.getNextId();
		var checkboxId = Dwt.getNextId();
		
		div = document.createElement("div");
		//div.id = String(pictures[i].id)+"_div";
		div.className = "SnapPhoto";
		
		adiv = document.createElement("a");
		adiv.href = "#";
		//adiv.onclick = AjxCallback.simpleClosure(this.showPhotoInNewWindow, this, String(attachment.url),String(attachment.label));
		adiv.innerHTML = attachment.label;
		
		imgdiv = document.createElement("img");
		imgdiv.id = imageId;
		imgdiv.border = "0";
		imgdiv.width = 100;
		imgdiv.height = 100;
		imgdiv.alt = imgdiv.title = String(attachment.label);
		imgdiv.src = attachment.url;
		
		input = document.createElement("input");
		input.type = "checkbox";
		input.id = checkboxId;
		input.value = imageId;
		input.name = "image";
		//input.onclick = AjxCallback.simpleClosure(this._updatePhotoSelectCount,this,input.id);
		
		div.appendChild(imgdiv);
		div.appendChild(document.createElement("br"));
		div.appendChild(input);
		div.appendChild(adiv);
		snapContainer.appendChild(div);
		
		selectionBoxIds.push(checkboxId);
	}

	if(selectionBoxIds.length == 0){
			snapContainer.innerHTML = "<center> <b>No JPEG/JPG images found in your mail. </b></center>";
	}
	
	var dlgArgs = {title : "Save to Comcast Photo Center",view  : view };
	var dlg = this._createDialog(dlgArgs);
	dlg.popup();
	
	this._selectedAttchImageCount = 0;

	dlg.setButtonListener(DwtDialog.OK_BUTTON, new AjxListener(this,function(){
		
		var selectedAlbum = albumSelect.getSelectedOption();
		var albumId = selectedAlbum.getValue();
		var albumName = selectedAlbum.getDisplayValue();
		
		this._selectedAlbumId = albumId;
		this._selectedAlbumName = albumName;
		this._imageLinks = [];
  		 this._imageLabels = [];
  		 var src,caption,checkbox;
	     for(i=0;i<selectionBoxIds.length;i++){
	      	checkbox = document.getElementById(selectionBoxIds[i]);
	      	if(checkbox && checkbox.checked){
	      		var imageDiv = document.getElementById(checkbox.value);
	      		this._imageLinks.push(imageDiv.src);
	      		imageDiv.title? this._imageLabels.push(imageDiv.title):this._imageLabels.push("");
	      	}
	     }
		
		if(this._imageLinks.length == 0){
			this.displayErrorMessage("<b>Select images to upload to Snapfish.</b>");
			return;
		}
		
		this._imageLinksIndex = 0;
		this._saveDialog = dlg;
		this._footer = footerContainer;
		this._footer.innerHTML = "<center>Adding "+ this._imageLinks.length +" to album "+ albumName +"</center>";
		var successCallback = new AjxCallback(this,this._done_uploadToSnapfish);
		var recursiveCallback = new AjxCallback(this,this._handleRecursiveUploadToSnapfish,successCallback);
		this.addImage(recursiveCallback,albumId,this._imageLabels[this._imageLinksIndex],this._imageLinks[this._imageLinksIndex]);
		this._imageLinksIndex++;
		
	}));
		
		
	dlg.setButtonListener(
			DwtDialog.CANCEL_BUTTON,
			new AjxListener(this, function() {
				dlg.popdown();
				dlg.dispose();
			}));
};


Com_Zimbra_Snapfish.prototype._handleRecursiveUploadToSnapfish = function(callback,albumId,imageUrl,result){
	
	if(this._imageLinks.length == this._imageLinksIndex){
		
		if(callback)
			callback.run(albumId);
		return;
		
	}
	this._footer.innerHTML = "<center>Added "+this._imageLinksIndex+" photos </center>";
	var recursiveCallback = new AjxCallback(this,this._handleRecursiveUploadToSnapfish,callback);
	this.addImage(recursiveCallback,albumId,this._imageLabels[this._imageLinksIndex],this._imageLinks[this._imageLinksIndex++]);
};

Com_Zimbra_Snapfish.prototype._done_uploadToSnapfish = function(){
	
	var soap = this._makeEnvelope("e:GetAlbumURL");  // 
	soap.set("AlbumId", this._selectedAlbumId);
	soap.set("authcode", this.authCode);
	this.rpc(soap, new AjxCallback(this, this._done_uploadAlbumUrl), true);
};

Com_Zimbra_Snapfish.prototype._done_uploadAlbumUrl = function(result){

	// TODO - add link to "go to album now"
	var ans = this.xmlToObject(result);

	this._saveDialog.getButton(DwtDialog.CANCEL_BUTTON).setEnabled(false);

	var snapContainerId = Dwt.getNextId();
	var snapSelectAlbumId = Dwt.getNextId();
	var snapFooterId = Dwt.getNextId();

	if (ans && ans.Body && ans.Body.GetAlbumURLResponse) {

		ans = ans.Body.GetAlbumURLResponse;
		var url = String(ans.albumUrl);
		url = url + "/u_="+this.authCode;
		this._saveDialog.setButtonListener(
			DwtDialog.OK_BUTTON,
			new AjxListener(this, function() {
			this._saveDialog.popdown();
			this._saveDialog.dispose();
			}));

		var html = [    
			"<div class='SnapFish'>",
				"<div class='SnapHead'>",
					"<div class='SnapInfo'>",
						"<span><strong>Your photo has been added to "+this._selectedAlbumName+"</strong></span>", "<br>",
						"<span><strong><span  class='SnapInfo'><a target='_new' href='"+url+"'>Open Album Now</a></span></strong></span>",
					"</div>",
				"</div>",
				"<div id='",snapContainerId,"' class='SnapContainer'>","</div>",
				"<div id='",snapFooterId,"' class='SnapFooter'>","</div>",
			"</div>"
			].join("");

		this._saveDialog._contentDiv.innerHTML = html;
		var snapContainer = document.getElementById(snapContainerId);
		delete snapContainerId;

		//Construct UI
		//var selectionBoxIds = [];
		var attLinks = this._attLinks;
		for(i=0;i<attLinks.length;i++){
			
			attachment = attLinks[i];	
			if(attachment.ct.indexOf("jpeg") == -1)
					continue;
			
			var imageId = Dwt.getNextId();
			//var checkboxId = Dwt.getNextId();
			
			div = document.createElement("div");
			//div.id = String(pictures[i].id)+"_div";
			div.className = "SnapPhoto";
			
			adiv = document.createElement("a");
			adiv.href = "#";
			//adiv.onclick = AjxCallback.simpleClosure(this.showPhotoInNewWindow, this, String(attachment.url),String(attachment.label));
			adiv.innerHTML = attachment.label;
			
			imgdiv = document.createElement("img");
			imgdiv.id = imageId;
			imgdiv.border = "0";
			imgdiv.width = 100;
			imgdiv.height = 100;
			imgdiv.alt = imgdiv.title = String(attachment.label);
			imgdiv.src = attachment.url;
			
			//input = document.createElement("input");
			//input.type = "checkbox";
			//input.id = checkboxId;
			//input.value = imageId;
			//input.name = "image";
			//input.onclick = AjxCallback.simpleClosure(this._updatePhotoSelectCount,this,input.id);
			
			div.appendChild(imgdiv);
			//div.appendChild(document.createElement("br"));
			//div.appendChild(input);
			div.appendChild(adiv);
			snapContainer.appendChild(div);
			
			//selectionBoxIds.push(checkboxId);
		}

    } else {
		// If no URL, just don't display the link.
		this._imageLinks = [];
		this._imageLabels = [];
		this._selectedAlbumId = '';
		this._selectedAlbumName = '';
		this._imageLinksIndex = 0;
		this._saveDialog.popdown();
	}
};


//--------------------------------------------- BASIC FRAMEWORK --------------------------------//

// Snapfish: Login

/// Login to Snapfish.  The given callback will be called in the case of a
/// successful login.  Note that callback is a plain function (not AjxCallback)

Com_Zimbra_Snapfish.prototype.login = function(callback,force) {
	
	if(!force){
		force = false;
	}
	
	var authMethod = this.getConfig("authMethod");
	if (!authMethod || authMethod == "standard") {
		var user = this.getUserProperty("username");
		var pwd = this.getUserProperty("password");
		// Uncomment to test.
		//user = "prakash.segu@gmail.com";
		//pwd = "prakash";
		if (!user || user == "" || !pwd || pwd == "" || force ) {
			this.displayStatusMessage("Please fill your Snapfish Login credentials first");
			this.createPropertyEditor(new AjxCallback(this, this.login, [ callback ]));
		} else {
			var soap = this._makeEnvelope("e:Login");
			soap.set("subscriberid","1000000");
			soap.set("email", user);
			soap.set("password", pwd);
			this.rpc(soap, new AjxCallback(this, this.done_login, [ callback ]), true);
		}
	} else if (authMethod == "comcast") {
		var guid = this.getUserProperty("guid");
		var user = appCtxt.get("USERNAME");
		if (guid == null || guid == "") {
			this.displayStatusMessage("User GUID not set");
			this.failed_login();
		}
		// Uncomment to test.
		//cn = Com_Zimbra_Snapfish.PABGUID;
		//user = Com_Zimbra_Snapfish.USER;
		var soap = this._makeEnvelope("e:ComcastLogin");  // e:Comcast or Comcast?
		soap.set("email", user);
		soap.set("guid", guid);
		this.rpc(soap, new AjxCallback(this, this.done_login, [ callback ]), true);
	} else {
		this.displayStatusMessage("Invalid auth method: "+authMethod);
	}
};

Com_Zimbra_Snapfish.prototype.done_login = function(callback, result) {
	
	if (!callback) {
		callback = false;
    }

	var ans = this.xmlToObject(result);
	// Std login returns LoginResponse, otherwise ComcastLoginResponse
	if (ans && ans.Body && (ans.Body.LoginResponse || ans.Body.ComcastLoginResponse)) {
		ans = ans.Body.LoginResponse?ans.Body.LoginResponse:ans.Body.ComcastLoginResponse;
		this.authCode = String(ans.authcode);
		this.podHost = String(ans.podhost);
		this.adHost = String(ans.adhost);
		this.smartHost = String(ans.smarthost);
		this.priceVersion = String(ans.priceversion);
		//this.displayStatusMessage("SnapFish: logged in.");
		this.userName = this.getUserProperty("username");
		
		this._login = true;

		if (this._snapfishTabView._contentEl._savedHTML) {
			this._snapfishTabView._contentEl.innerHTML = this._snapfishTabView._contentEl._savedHTML;
		}
		
		if (callback)
			callback.run();
			
    } else {
        var fault = "";
        if (ans && ans.Body && ans.Body.Fault && ans.Body.Fault.faultstring) {
            fault = ans.Body.Fault.faultstring + "<br />";
        }
        this.setUserProperty("username","",true);
        this.setUserProperty("password","",true);
        //var user = this.getUserProperty("username");
		//var passwd = this.getUserProperty("password");
		var authMethod = this.getConfig("authMethod");
        if (authMethod == "comcast") {
			this.failed_login();
		} else {
			this.displayErrorMessage("<b>Snapfish login failed!</b><br />&nbsp;&nbsp;&nbsp;&nbsp;" + fault + 
				"<br />Check your internet connection and review your preferences.");
		}
	}

};

Com_Zimbra_Snapfish.prototype.failed_login = function(){
	
	/*
	var snapfishId = Dwt.getNextId();
	var snapTitleId = Dwt.getNextId();
	var snapInfoId = Dwt.getNextId();
	var snapSelectAllId = Dwt.getNextId();
	var snapContainerId = Dwt.getNextId();
	var snapFooterId = Dwt.getNextId();
	*/

	// TODO - handle failed login from save to photocenter link
	var signUpLink = this.getConfig("signUpLink");
	var params = {
		signUpLink: signUpLink
	};

	if (!this._snapfishTabView._contentEl._savedHTML) {
		this._snapfishTabView._contentEl._savedHTML = this._snapfishTabView._contentEl.innerHTML;
	}
	this._snapfishTabView._contentEl.innerHTML =  AjxTemplate.expand("com_zimbra_snapfish.templates.snapfish#accountNotFound",params);
	// TODO - find out how to make this visible on window popdown
	// this._attachDialog.getButton(2).setVisibility(false);
	
};

///Snapfish: Get Albums

Com_Zimbra_Snapfish.prototype.getAlbums = function(callback){
	
	var soap = this._makeEnvelope("e:GetAlbums");
	soap.set("authcode",this.authCode);
	soap.set("type","0");
	if (callback == null)
		callback = false;
	this.rpc(soap, new AjxCallback(this, this.done_getAlbums, [ callback ]), true);
};

Com_Zimbra_Snapfish.prototype.done_getAlbums = function(callback,result){
	
	var ans = this.xmlToObject(result);
	if (ans && ans.Body && ans.Body.GetAlbumsResponse) {
		ans = ans.Body.GetAlbumsResponse;

		
	if (callback){
		callback.run(ans);
	}
	
    } else {
        var fault = "";
        if (ans && ans.Body && ans.Body.Fault && ans.Body.Fault.faultstring) {
            fault = ans.Body.Fault.faultstring + "<br />";
        }
        this.displayErrorMessage("<b>Snapfish: Fetching albums failed!</b><br />&nbsp;&nbsp;&nbsp;&nbsp;" + fault + "<br />Check your internet connection and review your preferences.");
    }
};

///Snapfish: Get Album Info

Com_Zimbra_Snapfish.prototype.getAlbumInfo = function(albumID,callback){
	var soap = this._makeEnvelope("e:GetAlbumInfo");
	soap.set("authcode",this.authCode);
	soap.set("AlbumId",albumID);
	if (callback == null || !callback)
		callback = false;
	this.rpc(soap, new AjxCallback(this, this.done_getAlbumInfo, [ callback ]), true);
};

Com_Zimbra_Snapfish.prototype.done_getAlbumInfo = function(callback,result){
	var ans = this.xmlToObject(result);
	if (ans && ans.Body && ans.Body.GetAlbumInfoResponse) {
		ans = ans.Body.GetAlbumInfoResponse;
		
		if(callback){
			callback.run(ans);
		}
		//this.displayStatusMessage("SnapFish: "+ans.name+" Album fetched.");
	}else {
        var fault = "";
        if (ans && ans.Body && ans.Body.Fault && ans.Body.Fault.faultstring) {
            fault = ans.Body.Fault.faultstring + "<br />";
        }
        this.displayErrorMessage("<b>Snapfish Get Album Info failed!</b><br />&nbsp;&nbsp;&nbsp;&nbsp;" + fault + "<br />Check your internet connection and review your preferences.");
    }
};

//Snapfish: Upload Image to Zimbra as Attachments

Com_Zimbra_Snapfish.prototype.attachImage = function(imageURL,imageCaption,callback){
	
	if(!callback){
		callback = false;
	}
	
	var reqParams = [
			"upload=1","&",
            "fmt=raw","&",
            "filename=",imageCaption
	].join("");


    var serverURL = ZmZimletBase.PROXY + AjxStringUtil.urlComponentEncode(imageURL) + "&" + reqParams;

    var ajxCallback = new AjxCallback(this,this.done_attachImage,[callback,imageURL,imageCaption,this.isInline()]);
    AjxRpc.invoke(reqParams,serverURL,null,ajxCallback,true);
};

Com_Zimbra_Snapfish.prototype.done_attachImage = function(callback,imageURL,imageCaption,isInline,result){

    var uploadResponse = result.text;
	
	if(!uploadResponse){
		this.displayErrorMessage("Failed to add the image <b>"+imageCaption+"</b> to this mail");
		callback.call(this,null,imageURL,imageCaption);
		return;
	}
	var response = AjxStringUtil.split(uploadResponse,',');
	if(response.length <= 2){
		this.displayErrorMessage("Failed to add the image <b>"+imageCaption+"</b> to this mail");
		callback.call(this,null,imageURL,imageCaption);
		return;
	}

    var len = response[2].length;
    var attachmentId = response[2];
    //Yuck: In my local I got response attachmentId as "'23234.....2333'\r\n" and thus the fix
    if(attachmentId.indexOf("\r\n") != -1){
        attachmentId = attachmentId.substring(0,len-2);
        len = attachmentId.length;
    }
    attachmentId = attachmentId.substring(1,len-1);

    if(isInline){ //YUCK: Tmp. hack for Inline attachments. Just to make it look like extended response format.
       attachmentId = { aid:attachmentId, ct:"image/snapfish" };
    }

    if(callback){
		callback.run(attachmentId,imageURL,imageCaption);	
	}
};

///Snapfish: Add Image to Snapfish

Com_Zimbra_Snapfish.prototype.addAlbum = function(callback,albumName,imageCaption,imageUrl){
	
	this._albumName = albumName;
	if(imageUrl){
		this.uploadImageFromURL(callback,null,imageCaption,imageUrl);
	}else{
		this.uploadImageFromLocal(callback,null,imageCaption);
	}
};

Com_Zimbra_Snapfish.prototype.addImage = function(callback,albumId,imageCaption,imageURL){
	if(imageURL){
		this.uploadImageFromURL(callback,albumId,imageCaption,imageURL);
	}else{
		this.uploadImageFromLocal(callback,albumId,imageCaption);
	}
};

Com_Zimbra_Snapfish.prototype.uploadImageFromURL = function(callback,albumId,imageCaption,imageUrl){
	this.uploadImage(callback,albumId,imageCaption,imageUrl);
};

Com_Zimbra_Snapfish.prototype.uploadImageFromLocal = function(callback,albumId,imageCaption){
	this.uploadImage(callback,albumId,imageCaption);
};

Com_Zimbra_Snapfish.prototype.uploadImage = function(callback,albumId,imageCaption,imageUrl){
	
	var startUploadSessionURL = this.smartHost+"/startsession.suup";
	
	var reqParams = ["authcode=",this.authCode,"&ExpectedImages=1&Src=TST"];
	
	if(albumId){
		reqParams.push("&AlbumID="+albumId);
	}else{
		if(this._albumName && this._albumName != ""){
			reqParams.push("&AlbumCaption="+this._albumName);
		}else{
			reqParams.push("&AlbumCaption=Album_"+Dwt.getNextId());
		}
	}
	reqParams = reqParams.join("");
	
	var url = [startUploadSessionURL,"?",reqParams].join("");
	
	callback = new AjxCallback(this,this.startUploadImage,[callback,albumId,imageCaption,imageUrl]);
	
	this.sendRequest(reqParams, url , null , callback, false);
};

Com_Zimbra_Snapfish.prototype.startUploadImage = function(callback,albumId,imageCaption,imagePath,result){
	
	if(typeof imagePath == "undefined" || imagePath == null){
		imagePath = "";
	}
	
	var ans = this.xmlToObject(result);
	if (ans && ans.Body && ans.Body.StartSessionResults ) {
		ans = ans.Body.StartSessionResults.SessionParams;
		this.uploadSessionId = String(ans.SessionId);
		this.uploadAlbumId = String(ans.AlbumId);
		
		//this.displayStatusMessage("Snapfish: Upload session initiated");
		//Start Upload
		var snapfishProxyURL = this.getResource('snapfish.jsp');
		var targetURL = [this.smartHost,"/uploadimage.suup"].join("");
		var mode="uploadLocal";
		if(imagePath.match(/((telnet:)|((https?|ftp|gopher|news|file):\/\/)|(www\.[\w\.\_\-]+))[^\s\xA0\(\)\&lt;\>\[\]\{\}\'\"]*/) != null){
			mode = "uploadURL";
			imagePath = AjxStringUtil.urlComponentEncode(imagePath);
		}
		//Handle UploadURL
		if("uploadURL" == mode){
		
			var reqParams = [	"mode=",mode,"&",
								"url=",targetURL,"&",
								"imagePath=",imagePath,"&",
								"Src=","TST","&",
								"authcode=",this.authCode,"&",
								"AlbumID=",this.uploadAlbumId ,"&",
								"SessionId=",this.uploadSessionId,"&",
								"SequenceNumber=","1"
						];
						
			if(imageCaption && imageCaption != ""){
				reqParams.push("&caption="+imageCaption);
			}
			
			reqParams = reqParams.join("");
			
			var url = [	snapfishProxyURL,"?",reqParams].join("");
		
			callback = new AjxCallback(this,this.done_uploadImage,[callback,albumId,imageCaption,imagePath]);
			AjxRpc.invoke(reqParams, url, null, callback, false);
		}else{		
			this._createUploadHtml(callback,albumId,imageCaption);
		}
	}else{
		var fault = "";
        if (ans && ans.Body && ans.Body.Fault && ans.Body.Fault.faultstring) {
            fault = ans.Body.Fault.faultstring + "<br />";
        }
        this.displayErrorMessage("<b>Snapfish Upload failed!</b><br />&nbsp;&nbsp;&nbsp;&nbsp;" + fault + "<br />Check your internet connection and review your preferences.");
	}
};

Com_Zimbra_Snapfish.prototype._createUploadHtml = function(callback,albumId,imageCaption) {
	
	var imagePath = "Local File System";
	
	var targetURL = [this.smartHost,"/uploadimage.suup"].join("");

	var reqParams = [	"mode=uploadLocal","&",
						"url=",targetURL,"&",
						"imagePath=",imagePath,"&",
						"Src=","TST","&",
						"authcode=",this.authCode,"&",
						"AlbumID=",this.uploadAlbumId,"&",
						"SessionId=",this.uploadSessionId,"&",
						"SequenceNumber=","1" 
						];
	
	if(imageCaption && imageCaption != ""){
		reqParams.push("&caption="+imageCaption);
	}
			
	reqParams = reqParams.join("");
	
	var snapfishProxyURL = this.getResource('snapfish.jsp') + "?" + reqParams;
	
	var container = document.createElement("DIV");
	container.style.marginLeft = "1em";
	container.style.marginBottom = "0.5em";
	
	var formId = Dwt.getNextId();
	
	container.innerHTML = [
			"<form enctype='multipart/form-data' target='",this.getUploadFrameId(),"' id='",formId,"' method='POST' action='",snapfishProxyURL,"' >",
			"<table cellspacing='4' cellpadding='0' border='0'>",
			"<tr><td>","<input name='localfile' type='file' size='30'>","</td></tr>",
			"</table>",	
			"</form>"
	].join("");
	
	var view = new DwtComposite(this.getShell());
	
	var element = view.getHtmlElement();
	element.appendChild(container);
	
	var dialogTitle = 'Upload to Snapfish';
	var dialogArgs = {title : dialogTitle,view  : view };
	var dlg = this._createDialog(dialogArgs);
	
	dlg.setButtonListener(
			DwtDialog.CANCEL_BUTTON,
			new AjxListener(this, function() {
				dlg.popdown();
				dlg.dispose();
			}));
	
	dlg.setButtonListener(
		DwtDialog.OK_BUTTON,
		new AjxListener(this, function() {
		
		this._uploadForm = document.getElementById(formId);	
		window._snapfish = AjxCallback.simpleClosure(this.done_uploadImage,this,callback,albumId,imageCaption,imagePath);		

		this._uploadForm.submit();

		dlg.popdown();
		dlg.dispose();
		}));			
	dlg.popup();
};

Com_Zimbra_Snapfish.prototype.done_uploadImage = function(callback,albumId,imageCaption,imagePath,result){
	
	//alert(document.getElementById(this.getUploadFrameId()).contentWindow.document.documentElement.firstChild.childNodes[1].innerHTML);
	
	if(callback == null){
		callback = false;
	}
	var ans,images,success=false;
	var fault = "";

    if(typeof result == "string"){

        ans = AjxXmlDoc.createFromXml(result).toJSObject(true, false);
		if(ans && ans.body && ans.body.uploadimageresults){
			ans = ans.body.uploadimageresults.sessionparams;
			images = ans.successfulimagenames.name;
			success = true;
		}else{
			if (ans && ans.body && ans.body.fault && ans.body.fault.faultstring) {
            	fault = ans.body.fault.faultstring + "<br />";
        	}
		}
	}else{
		ans = this.xmlToObject(result);
		
		if (ans && ans.Body && ans.Body.UploadImageResults) {
			ans = ans.Body.UploadImageResults.SessionParams;
			images = ans.SuccessfulImageNames.Name;
			//DBG.dumpObj(images);
			success = true;
		}else{
			if (ans && ans.Body && ans.Body.Fault && ans.Body.Fault.faultstring) {
            	fault = ans.Body.Fault.faultstring + "<br />";
        	}
		}
	}

	if(callback && success){
		callback.run(albumId,imagePath,images);
	}
	
	if(!success){   
		if(fault == ""){
			fault = " Server Exception during file upload ";
		}
        this.displayErrorMessage("<b>Snapfish Upload failed!</b><br />&nbsp;&nbsp;&nbsp;&nbsp;" + fault + "<br />Check your internet connection and review your preferences.");
	}
};

//Snapfish: Get User Info

Com_Zimbra_Snapfish.prototype.getUserInfo = function(callback){
	var soap = this._makeEnvelope("e:GetUserInfo");
	soap.set("authcode", this.authCode);
	this.rpc(soap, new AjxCallback(this, this.done_getUserInfo, [ callback ]), true);
};

Com_Zimbra_Snapfish.prototype.done_getUserInfo = function(callback,result){
	
	if (!callback) {
		callback = false;
    }
	
	var ans = this.xmlToObject(result);
	if (ans && ans.Body && ans.Body.UserInfoResponse) {
		
		ans = ans.Body.UserInfoResponse;
		
		this.userName = ans.userInfo.email;
		this.fName    = ans.userInfo.fname;
		this.lName	  = ans.userInfo.lname;
		
		if(callback)
			callback.run();
	}else{
		var fault = "";
        if (ans && ans.Body && ans.Body.Fault && ans.Body.Fault.faultstring) {
            fault = ans.Body.Fault.faultstring + "<br />";
        }
        this.displayErrorMessage("<b>Snapfish login failed!</b><br />&nbsp;&nbsp;&nbsp;&nbsp;" + fault + "<br />Check your internet connection and review your preferences.");
	}
	
};

//SOAP METHOD: register
///Just in case if SnapFish needs a registration process.
Com_Zimbra_Snapfish.prototype.register = function(callback){
	if(!callback){
		callback = false;
	}
	
	var view = new DwtComposite(this.getShell());
	var acctEditor = new DwtPropertyEditor(view, true);
	
	var acct_props = [

		{ label    : "Email Id",
		  name     : "email",
		  type     : "string",
		  value    : "",
		  required : true },

		{ label    : "Password",
		  name     : "pwd",
		  type     : "password",
		  value    : "",
		  required : true },
		  
		{ label    : "Password",
		  name     : "cpwd",
		  type     : "password",
		  value    : "",
		  required : true },  

		{ label    : "First Name",
		  name     : "fname",
		  type     : "string",
		  value    : "",
		  required : true },
		  
		{ label    : "Last Name",
		  name     : "lname",
		  type     : "string",
		  value    : "",
		  required : true } 
	];
	
	acctEditor.initProperties(acct_props);
	
	var dialogTitle = "Create Account in Snapfish";						
	var dialog_args = {title : dialogTitle,view  : view};
	var dlg = this._createDialog(dialog_args);
	acctEditor.setFixedLabelWidth();
 	acctEditor.setFixedFieldWidth();
	dlg.popup();
	
	dlg.setButtonListener(
		DwtDialog.OK_BUTTON,
		new AjxListener(this, function() {
			
			if(!acctEditor.validateData()){
				this.displayErrorMessage("<b>Please fill in all the required fields for registration.</b>");
				return;
			}
			
			var acct = acctEditor.getProperties();
			
			if(acct.pwd != acct.cpwd){
				this.displayErrorMessage("<b>Please enter the same passwords.</b>");
				return;
			}
			
			var soap = this._makeEnvelope("e:RegisterUser");
			soap.set("subscriberid","1000000");
			soap.set("cobrandid","1000");
			soap.set("deviceid","TST");
			var userData = {};
			userData["email"]= acct.email;
			userData["fname"] = acct.fname;
			userData["lname"] = acct.lname;
			userData["password"] = acct.pwd;
			soap.set("UserInfo",userData);
			
			this.setUserProperty("username",acct.email);
			this.setUserProperty("password",acct.pwd);
			this.rpc(soap, new AjxCallback(this, this.done_register, [ callback ]), true);
			
			dlg.popdown();
			dlg.dispose();
		}));
		
		dlg.setButtonListener(
			DwtDialog.CANCEL_BUTTON,
			new AjxListener(this, function() {
				dlg.popdown();
				dlg.dispose();
			}));
};

Com_Zimbra_Snapfish.prototype.done_register = function(callback,result){
	
	var ans = this.xmlToObject(result);
	if (ans && ans.Body && ans.Body.RegisterUserResponse) {
		ans = ans.Body.RegisterUserResponse;
		this.authCode = String(ans.authcode);
		this.podHost = String(ans.podhost);
		this.adHost = String(ans.adhost);
		this.smartHost = String(ans.smarthost);
		this.priceVersion = String(ans.priceversion);
		this.displayStatusMessage("SnapFish: logged in.");
		if (callback)
			callback.call(this);
    } else {
        var fault = "";
        if (ans && ans.Body && ans.Body.Fault && ans.Body.Fault.faultstring) {
            fault = ans.Body.Fault.faultstring + "<br />";
        }
        this.displayErrorMessage("<b>Snapfish registration failed!</b><br />&nbsp;&nbsp;&nbsp;&nbsp;" + fault + "<br />Check your internet connection and review your preferences.");
    }
};

///Snapfish: Utilities

Com_Zimbra_Snapfish.prototype.getUploadFrameId = function(){
	if(!this._uploadFrame){
		this.getUploadFrame();
	}
	return this._uploadFrameId;
};

Com_Zimbra_Snapfish.prototype.getUploadFrame = function() {
	if (!this._uploadFrame) {
		var iframeId = Dwt.getNextId();
		var html = [ "<iframe name='", iframeId, "' id='", iframeId,
			     "' src='", (AjxEnv.isIE && location.protocol == "https:") ? appContextPath+"/public/blank.html" : "javascript:\"\"",
			     "' style='position: absolute; top: 0; left: 0; visibility: hidden'></iframe>" ];
		var div = document.createElement("div");
		div.innerHTML = html.join("");
		document.body.appendChild(div.firstChild);
		this._uploadFrameId = iframeId;
		this._uploadFrame = document.getElementById(iframeId);
	}
	return this._uploadFrame;
};


Com_Zimbra_Snapfish.prototype.toString = function(){
	return "Com_Zimbra_Snapfish";
};

Com_Zimbra_Snapfish.prototype._makeEnvelope = function(method) {
	var soap = AjxSoapDoc.create(method, this.XMLNS,"e","http://schemas.xmlsoap.org/soap/envelope/");
	var envEl = soap.getDoc().firstChild;
	envEl.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
	envEl.setAttribute("xmlns:xsd", "http://www.w3.org/2001/XMLSchema");
	return soap;
};

Com_Zimbra_Snapfish.prototype.xmlToObject = function(result) {
    try {
        var xd = new AjxXmlDoc.createFromDom(result.xml).toJSObject(true, false);
    } catch(ex) {
        //this.displayErrorMessage(ex, result.text, "Problem contacting Snapfish");
    }
    return xd;
};

/// Utility function that calls the SForce server with the given SOAP data
Com_Zimbra_Snapfish.prototype.rpc = function(soap, callback, passErrors) {
	this.sendRequest(soap, this.SERVER, {SOAPAction: "m", "Content-Type": "text/xml"}, callback, false, passErrors);
};
