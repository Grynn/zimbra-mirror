/*
 * ***** BEGIN LICENSE BLOCK *****
 * Version: ZPL 1.1
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.1 ("License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.zimbra.com/license
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See
 * the License for the specific language governing rights and limitations
 * under the License.
 * 
 * The Original Code is: Zimbra Collaboration Suite Web Client
 * 
 * The Initial Developer of the Original Code is Zimbra, Inc.
 * Portions created by Zimbra are Copyright (C) 2006 Zimbra, Inc.
 * All Rights Reserved.
 * 
 * Contributor(s): Rajesh Segu
 * 
 * ***** END LICENSE BLOCK *****
 */

//////////////////////////////////////////////////////////////
//  VIDEO Zimlet                                              //
//  @author Rajesh Segu                                     //
//////////////////////////////////////////////////////////////

function Com_Zimbra_Video() {
}
Com_Zimbra_Video.prototype = new ZmZimletBase();
Com_Zimbra_Video.prototype.constructor = Com_Zimbra_Video;


Com_Zimbra_Video.WIDTH = 155;
Com_Zimbra_Video.HEIGHT = 135;

//Get GSEARCH API Key from http://www.google.com/uds/solutions/videosearch/index.html
//Com_Zimbra_Video.GSEARCH_API="ABQIAAAAkNWUSwZ-uNWZyDl7ZB5yWRRrb41JoWUZrahluOk81y-6F6GuWhRX6cnm3kCGqCYTz8xq-MLsN76TPA";

Com_Zimbra_Video.GSEARCH_APIKEY = null;
Com_Zimbra_Video.GSEARCH_APIURL = "http://code.google.com/apis/ajaxsearch/signup.html";

Com_Zimbra_Video.prototype.RECENTLY_PLAYED = [];

Com_Zimbra_Video.prototype.init = function(){

	this._videoDialog = false;
    
    var calController = AjxDispatcher.run("GetCalController");
    this._miniCal = calController ? calController.getMiniCalendar().getHtmlElement() : null;
    
    this.WIDTH = Com_Zimbra_Video.WIDTH;
    this.HEIGHT = Com_Zimbra_Video.HEIGHT;
    
    if(AjxEnv.isIE){
    	this.WIDTH += 5;
    	
    }
    
    Com_Zimbra_Video.GSEARCH_APIKEY = this._zimletContext.getConfig("googleAPI");
    
    this._loadGoogleSearchIncludes();
};

Com_Zimbra_Video.prototype.menuItemSelected = function(itemId) {
	
    switch (itemId) {
    	
        case "VIDEO":
            this.playURL();
            break;
        
        case "SEARCH":
        	this.search();
        	break;
        	
        case "PLAY":
        	this._playVideo();
        	break;
        	
        case "RECENTLY_PLAYED":
        	this._recentlyPlayedVideos();
        	break;
    }
};

Com_Zimbra_Video.prototype.doDrop = 
function(obj) {
	switch (obj.TYPE) {
	    case "ZmMailMsg":
	    case "ZmConv":
	   	this._parseMessageForVideo(obj);
		break;
		
	    default:
		this.displayErrorMessage("You somehow managed to drop a \"" + obj.TYPE + "\" but however this Zimlet does't support it for drag'n'drop.");
	}
};

Com_Zimbra_Video.prototype.doubleClicked = function() {
    this.singleClicked();
};

Com_Zimbra_Video.prototype.singleClicked = function() {
    this._startVideo();
};


//Method: Search functionality

Com_Zimbra_Video.prototype._loadGoogleSearchIncludes = function(){
	
	if(!Com_Zimbra_Video.GSEARCH_APIKEY){
		return;
	}
	
	window._uds_vsw_donotrepair = true;
	this._loadObject("http://www.google.com/uds/api?file=uds.js&v=1.0&source=uds-vsw&key="+Com_Zimbra_Video.GSEARCH_APIKEY);
	this._loadObject("http://www.google.com/uds/css/gsearch.css");
	this._loadObject("http://www.google.com/uds/solutions/videosearch/gsvideosearch.js?mode=new");
	this._loadObject("http://www.google.com/uds/solutions/videosearch/gsvideosearch.css");
	
};

Com_Zimbra_Video.prototype.search = function(){
	
	if(!Com_Zimbra_Video.GSEARCH_APIKEY){
		this.displayErrorMessage("Google Search API Key is not available.<br> Contact your administrator to signup for a key @ <br>"+Com_Zimbra_Video.GSEARCH_APIURL);		
		return;
	}
	
	var view = new DwtComposite(this.getShell());
	var element = view.getHtmlElement();
	var searchParentTableId = Dwt.getNextId();
	var html = ["<table cellspacing=4 cellpadding=0 border=0 width='300' height='300'>",
				"<tr><td id='",searchParentTableId,"'>","</td></tr>",
				"</table>",
				].join("");
	
	element.innerHTML = html;
	
	this._searchParentTableDiv = document.getElementById(searchParentTableId);
	
	this._searchParentTableDiv.appendChild(this._getSearchDiv());
	
	var dialogTitle = 'Search Google Videos';
	var dialogArgs = {title : dialogTitle,view  : view ,standardButtons : [DwtDialog.OK_BUTTON]};
	var dlg = this._createDialog(dialogArgs);
	
	dlg.setButtonListener(
		DwtDialog.OK_BUTTON,
		new AjxListener(this, function() {
			
			dlg.popdown();
			
			this.getVideoSearchControl().stopVideo();
			this.getVideoSearchControl().twiddleMore();
			if(this._searchParentTableDiv.firstChild){
				this._searchDiv = this._searchParentTableDiv.removeChild(this._searchParentTableDiv.firstChild);
			}
			
			dlg.dispose();
			
		}));	
	
	dlg.popup();
	
	this.getVideoSearchControl().doRandomSearch();
	
	if(this._initVideoSearchControl){
	
		
		
		//Removing Upload Video Link
		var videoSearchControl = this.getVideoSearchControl();
		videoSearchControl.removeChildren(videoSearchControl.footerBox);
		
		//Adding "Play this in the SideBar" link
		var sideBarTargetLinkDiv = document.createElement("div");
		sideBarTargetLinkDiv.id = "sidebarTargetLink";
		sideBarTargetLinkDiv.className =  videoSearchControl.CLSS_MORE ;
		sideBarTargetLinkDiv.style.textAlign = "center";
	
	    var sideBarTargetLink = document.createElement("a");
	    sideBarTargetLink.href = "#";
	    sideBarTargetLink.appendChild(document.createTextNode("Play this in the sidebar")); 
	    sideBarTargetLink.onclick = AjxCallback.simpleClosure(this._handleSideBarTargetLink,this,dlg);
	 
	 	sideBarTargetLinkDiv.appendChild(sideBarTargetLink);
	 	
	 	var rootEl = this._getSearchDiv().firstChild;
	 	
	 	rootEl.insertBefore(sideBarTargetLinkDiv,rootEl.firstChild);
	 	
	 	this._initVideoSearchControl = false;
	}
};

Com_Zimbra_Video.prototype._handleSideBarTargetLink = function(dlg){
	
	
	var videoSearchControl = this.getVideoSearchControl();
	
	if(!videoSearchControl.player){
		this.displayErrorMessage("Please select a video and then play it in the sidebar.");
		return;
	}
	
	var videoURL = videoSearchControl.player.getAttribute("data") || videoSearchControl.player.getAttribute("src");
	
	if(!videoURL){
		this.displayErrorMessage("Please select a video and then play it in the sidebar.");
		return;
	}
	
	this.setUserProperty("videoURL",videoURL);
	
	this._startVideo(null,true);


	if(dlg != null){	
			
		videoSearchControl.stopVideo();
		videoSearchControl.twiddleMore();
		
		dlg.popdown();
		dlg.dispose();
	}
	
};

Com_Zimbra_Video.prototype.getVideoSearchControl = function(){
	
	if(!this._videoSearchControl){
		
		var options = { largeResultSet : true};
		
		var params = [{ query : "comedy"}, { query : "sports"}, { query : "music"}, { query : "tv show"},{query : "cartoons"}];
		
		this._videoSearchControl = new GSvideoSearchControl(document.getElementById("videoSearch"),params,null, null, options);        
		
		this._initVideoSearchControl = true;
	}
	
	return this._videoSearchControl;
	
};


Com_Zimbra_Video.prototype._getSearchDiv = function(){
	if(!this._searchDiv){
		var searchDiv = document.createElement("div");
		searchDiv.id = 'videoSearch';
		this._searchDiv = searchDiv;
	}
	return this._searchDiv;
};



//Video: PlayURL

Com_Zimbra_Video.prototype.playURL = function(callback){
	
	var view = new DwtComposite(this.getShell());
	var container = document.createElement("DIV");
	var urlId = Dwt.getNextId();
	container.innerHTML = ["<table cellspacing=4 cellpadding=0 border=0>",
							"<tr><td>","Enter a valid video (Google | YouTube) URL","</td></tr>",
							"<tr><td>","<input id='",urlId,"' type='text' size=40>","</td></tr>",
							"</table>",
						].join("");
	var element = view.getHtmlElement();
	element.appendChild(container);
	
	var dialogTitle = 'Video Live';
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
			var videoLink = document.getElementById(urlId).value;
			if(!videoLink || !this.isValidVideoURL(videoLink)){
					this.displayErrorMessage("Please enter a valid link (Google or YouTube video links only)");
			}else{
			   	this.setUserProperty("videoURL",videoLink);
			   	this._startVideo(callback, true);
			}
			dlg.popdown();
			dlg.dispose();
		}));			
	dlg.popup();
};

//Video: Handle Msg Drop

Com_Zimbra_Video.prototype._parseMessageForVideo = function(zmObject,callback){
	
	if(!zmObject.body){
		this.displayErrorMessage("Message body is empty!!")
		return;
	}
	
	var msgBody = AjxStringUtil.htmlEncode(zmObject.body);
    var videoLinks = this.getValidVideoLinksFromText(msgBody);
    if(videoLinks == null || videoLinks.length == 0){
    	this.displayErrorMessage("No Google or YouTube video links found in the mail.");
    	return;
    }
    
	this._createHtml(videoLinks,callback);
};

Com_Zimbra_Video.prototype._createHtml = function(videoLinks,callback){
	
	var view = new DwtComposite(this.getShell());
	
	var table = document.createElement("table");
	table.border = 0;
	table.cellSpacing = 3;
	table.cellPadding = 0;
	var selectionBoxIds = [];
	for(i=0;i<videoLinks.length;i++){
		var linkId = Dwt.getNextId();
		var checkedId = Dwt.getNextId();
		var row = table.insertRow(table.rows.length);
		var radio = document.createElement("input");
		radio.type = "radio";
		radio.id = checkedId;
		radio.value = linkId;
		radio.name = 'radio';
		if(i==0){
			radio.checked = true;
		}
		var videoSelectCell = row.insertCell(row.cells.length);
		videoSelectCell.appendChild(radio);
		var videoLinkCell = row.insertCell(row.cells.length);
		videoLinkCell.innerHTML = "<div id='"+linkId+"'>"+videoLinks[i]+"</div>";
		selectionBoxIds.push(checkedId);
	}
	
	var rootEl = view.getHtmlElement();
	rootEl.appendChild(table);
	
	var dialog_args = {
		view  : view,
		title : "Video links in the message"
	};
	var dlg = this._createDialog(dialog_args);
	dlg.popup();
	
		dlg.setButtonListener(DwtDialog.OK_BUTTON,
			      new AjxListener(this, function() {
			      	var radio=null;
			      	var videoLink = null;
				      for(i=0;i<selectionBoxIds.length;i++){
				      	radio = document.getElementById(selectionBoxIds[i]);
				      	if(radio.checked){
				      		videoLink = (document.getElementById(radio.value)).innerHTML;
				      		break;
				      	}
				      }
				      if(videoLink){
				      	this.setUserProperty("videoURL",videoLink);
				      	this._startVideo(callback, true);
				      }
				      dlg.popdown();
				      dlg.dispose();
			      }));
			      
		dlg.setButtonListener(DwtDialog.CANCEL_BUTTON,
			      new AjxListener(this, function() {
				      dlg.popdown();
				      dlg.dispose();
			      }));			      
};

//Video: Video Controls [ play, stop, start ]

Com_Zimbra_Video.prototype._playVideo = function(){
	var videoURL = this._actionObject.toString();
	this.setUserProperty("videoURL",videoURL);
	this._startVideo(null,true);
};

Com_Zimbra_Video.prototype._stopVideo = function(){
	var minicalDIV = document.getElementById("skin_container_tree_footer");
	minicalDIV.innerHTML = "";
	this._miniCal.style.visibility = "visible";
	
};

Com_Zimbra_Video.prototype._startVideo = function(callback,force){
	
	if(typeof force == "undefined"){
		force = false;
	}
	
	var videoURL = this.getUserProperty("videoURL");
	if(!videoURL ){
		this.playURL();
		return;
	}
	
	this._videoDialog = !this._videoDialog;
	
	this.addToRencentlyPlayedVideos(videoURL);
	
	if(!callback) callback = false;
	
	var minicalDIV = document.getElementById("skin_container_tree_footer");
	if (this._videoDialog || force)
    {
    	if(force){
    		minicalDIV.innerHTML = "";
    	}
        if (!document.getElementById("videoDIV")) {
    		var newDiv = document.createElement("div");
            newDiv.id = "videoDIV";;
            newDiv.style.margin = '0px';
            newDiv.innerHTML = "<div id='innerVideoDiv' class=\"loading\" style=\"margin: 0px\">loading video ..</div>";
            minicalDIV.appendChild(newDiv);
        }
        this._miniCal.style.visibility = "hidden";
        this._addVideo(videoURL);	
    }else{
    		minicalDIV.innerHTML = "";
    		this._miniCal.style.visibility = "visible";
    }
};

//Add Video to the side panel
Com_Zimbra_Video.prototype._addVideo = function(videoURL){
	
	if(videoURL.indexOf("youtube.com/watch?v=") != -1){
		var youtubeID = videoURL.substring(videoURL.indexOf("?v=")+3,videoURL.length);
        videoURL = 	"http://youtube.com/v/"+youtubeID;
	}else if(videoURL.indexOf("video.google.com/videoplay?docid=") != -1){
		var googleID = videoURL.substring(videoURL.indexOf("?docid=")+7,videoURL.length);
		videoURL = "http://video.google.com/googleplayer.swf?docId="+googleID+"&hl=en&loop=true&playerMode=simple";
	}
	
	var html;
	
	if(AjxEnv.isOpera || (AjxEnv.isMac && ( videoURL.indexOf("youtube.com") != -1 ))){
		html = [
			"<object type='application/x-shockwave-flash' ",
			"style='width:",this.WIDTH,"px; height:",this.HEIGHT,"px;'",
			"data='",videoURL,"'></object>"
		].join("");
	}else{
		html = [ 
			"<embed style='width:",this.WIDTH,"px; height:",this.HEIGHT,"px;'",
			" type='application/x-shockwave-flash' ",
			"src='",videoURL,"' ",
			(videoURL.indexOf("google") != -1 )?"bgcolor='#000000'":"wmode='transparent'",
			" >","</embed>"
		].join("");
	}
	
	document.getElementById("innerVideoDiv").innerHTML = html;
	return;
	
	/* Code for reference, please do not delete this.
	if(videoURL.indexOf("youtube.com/watch?v=") != -1){
		var youtubeID = videoURL.substring(videoURL.indexOf("?v=")+3,videoURL.length);
		html = ["<object classid='clsid:D27CDB6E-AE6D-11cf-96B8-444553540000' codebase='http://download.macromedia.com/pub/shockwave/cabs/flash/swflash.cab#version=6,0,40,0' width='",this.WIDTH,"px' height='",this.HEIGHT,"px'>",
				"<param name='movie' value='http://youtube.com/v/",youtubeID,"'>",
				"<param name='quality' value='high'>",
				"<param name='bgcolor' value='#FFFFFF'>",
				"<!--[if !IE]> <-->",
				"<object data='http://youtube.com/v/",youtubeID,"' width='",this.WIDTH,"px' height='",this.HEIGHT,"px' type='application/x-shockwave-flash'>",
				"<param name='quality' value='high'>",
				"<param name='bgcolor' value='#FFFFFF'>",
				"<param name='pluginurl' value='http://www.macromedia.com/go/getflashplayer'>",
				"FAIL (the browser should render some flash content, not this).",
				"</object>",
				"<!--> <![endif]-->"].join("");
	}else if(videoURL.indexOf("video.google.com/videoplay?docid=") != -1){
		var googleID = videoURL.substring(videoURL.indexOf("?docid=")+7,videoURL.length);
		html = [
			"<embed style='width:",this.WIDTH,"px; height:",this.HEIGHT,"px;' id='VideoPlayback' type='application/x-shockwave-flash'",
			"src='http://video.google.com/googleplayer.swf?docId=",googleID,"&hl=en&loop=true&playerMode=simple' flashvars=''>", 
			"</embed>"
		].join("");
	}
	document.getElementById("innerVideoDiv").innerHTML = html;
	*/
};

//Method: Recently Played Videos

Com_Zimbra_Video.prototype._recentlyPlayedVideos = function(){
	
	if(this.RECENTLY_PLAYED.length == 0){
		this.displayErrorMessage("Recently played videos is empty");
		return;
	}
	
	this._createHtml(this.RECENTLY_PLAYED);
};

Com_Zimbra_Video.prototype.addToRencentlyPlayedVideos = function(videoURL){
	var recent = this.RECENTLY_PLAYED.join(",");
	if(recent.indexOf(videoURL) == -1){
		if(this.RECENTLY_PLAYED.length > 10 ){
			this.RECENTLY_PLAYED.pop();
		}
		this.RECENTLY_PLAYED.push(videoURL);
	}
};


//Video: Utilities

Com_Zimbra_Video.prototype.isValidVideoURL = function(videoURL){
	
	var isVideoLink = videoURL.match(/(\b(((http | https)\:\/\/)?(www\.)?((youtube\.com\/watch\?v=)|(video\.google\.com\/videoplay\?docid=)|(video\.google\.com\/googleplayer\.swf\?docId=)|(youtube\.com\/v\/))((-)?[0-9a-zA-Z]+)?(&\w+=\w+)*)\b)/gi);
	
//	var isVideoLink = videoURL.match(/(\b(((http | https)\:\/\/)?(www\.)?((youtube\.com\/watch\?v=)|(video\.google\.com\/videoplay\?docid=))(-)?[0-9a-zA-Z]+)\b)/gi);
//	videoURL.match(/(\b(((http | https)\:\/\/)?(www\.)?((youtube\.com\/watch\?v=)|(video\.google\.com\/videoplay\?docid=)|(video\.google\.com\/googleplayer\.swf\?docId=)|(youtube\.com\/v\/))(-)?[0-9a-zA-Z]+)\b)/gi);
	return (isVideoLink == null)?false:true;
	
};

Com_Zimbra_Video.prototype.getValidVideoLinksFromText = function(text){
	
	//var videoLinks = text.match(/(\b(((http | https)\:\/\/)?(www\.)?((youtube\.com\/watch\?v=)|(video\.google\.com\/videoplay\?docid=))(-)?[0-9a-zA-Z]+)\b)/gi);
	var videoLinks = text.match(/(\b(((http | https)\:\/\/)?(www\.)?((youtube\.com\/watch\?v=)|(video\.google\.com\/videoplay\?docid=)|(video\.google\.com\/googleplayer\.swf\?docId=)|(youtube\.com\/v\/))((-)?[0-9a-zA-Z]+)?(&\w+=\w+)*)\b)/gi);
	return videoLinks;
};

Com_Zimbra_Video.prototype._loadObject = function(file){
	var fileref;
	if (file.indexOf(".js")!=-1){ //If object is a js file
		fileref=document.createElement('script');
		fileref.setAttribute("type","text/javascript");
		fileref.setAttribute("src", file);
	}
	else if (file.indexOf(".css")!=-1){ //If object is a css file
		fileref=document.createElement("link");
		fileref.setAttribute("rel", "stylesheet");
		fileref.setAttribute("type", "text/css");
		fileref.setAttribute("href", file);
	}
	document.getElementsByTagName("head").item(0).appendChild(fileref)
};
