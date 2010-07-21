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
    
    this.WIDTH = Com_Zimbra_Video.WIDTH;
    this.HEIGHT = Com_Zimbra_Video.HEIGHT;
    
    if(AjxEnv.isIE){
    	this.WIDTH += 5;
    	
    }
    
    Com_Zimbra_Video.GSEARCH_APIKEY = this.getConfig("googleAPI");
    
    this._loadGoogleSearchIncludes();
};

Com_Zimbra_Video.prototype.menuItemSelected = function(itemId) {
	
    switch (itemId) {
    	
        case "VIDEO":
            this.playVideo(null,true);
            break;
        
        case "SEARCH":
        	this.search();
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
    this.switchVideo();
};

Com_Zimbra_Video.prototype.switchVideo = function(){
    this._videoActive ? this.stopVideo() : this.playVideo();
};

//Video: PlayURL
Com_Zimbra_Video.prototype.playVideo = function(url,forceAsk){
    url = url || this.getRecentlyPlayedVideo();
    if(!url || forceAsk){
        var callback = new AjxCallback(this,this.playVideo);
        this.playURLDialog(callback);
        return;
    }
    this.addToRencentlyPlayedVideos(url);
    var minicalDIV = document.getElementById("skin_container_tree_footer");
    minicalDIV.innerHTML = "";
    if (!document.getElementById("videoDIV")) {
    		var newDiv = document.createElement("div");
            newDiv.id = "videoDIV";;
            newDiv.style.margin = '0px';
            newDiv.innerHTML = "<div id='innerVideoDiv' class=\"loading\" style=\"margin: 0px\">loading video ..</div>";
            minicalDIV.appendChild(newDiv);
    }

    if(!this._miniCal && (appCtxt.get(ZmSetting.CAL_ALWAYS_SHOW_MINI_CAL))) {
        var calMgr = appCtxt.getCalManager();        
        this._miniCal = calMgr.getMiniCalendar().getHtmlElement();
    }
    if(this._miniCal) {
        this._miniCal.style.visibility = "hidden";
    }

    this._videoActive = true;
    this._addVideo(url); 
};

Com_Zimbra_Video.prototype.stopVideo = function(){
	var minicalDIV = document.getElementById("skin_container_tree_footer");
	minicalDIV.innerHTML = "";
    if(this._miniCal) {
        this._miniCal.style.visibility = "visible";
    }
    this._videoActive = false;
};

Com_Zimbra_Video.prototype.playURLDialog = function(callback){
	
	var view = new DwtComposite(this.getShell());
	var container = document.createElement("DIV");
	var urlId = Dwt.getNextId();
	container.innerHTML = ["<table cellspacing=4 cellpadding=0 border=0>",
							"<tr><td>","Enter a valid video (Google | YouTube) URL","</td></tr>",
							"<tr><td>","<input id='",urlId,"' type='text' size=40>","</td></tr>",
                            "<tr><td id='sampleURL'></td></tr>",
                            "</table>"
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
            var vLink = document.getElementById(urlId).value;
            if(this.isValidVideoURL(vLink)){
                callback.run(vLink);
                dlg.popdown();
			    dlg.dispose();
            }else{
                this.displayErrorMessage("Please enter a valid link (Google or YouTube video links only)");
            }
		}));
    this._populateSampleVideos();
    dlg.popup();
};

Com_Zimbra_Video.prototype.sampleVideoURL = [
          "http://youtube.com/watch?v=hS5UfTswufE",
          "http://video.google.com/videoplay?docid=4007016107763801953"
        ];
Com_Zimbra_Video.prototype._populateSampleVideos = function(){
    var samples = document.getElementById("sampleURL");
    samples.innerHTML = ["Eg:",
                         "&nbsp;","<a target='_blank' href='"+this.sampleVideoURL[0]+"'>",this.sampleVideoURL[0],"</a>","<br>",
                         "&nbsp;","<a target='_blank' href='"+this.sampleVideoURL[1]+"'>",this.sampleVideoURL[1],"</a>"
                        ].join("");
};

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
	
	this._createVideoLinksHtml(this.RECENTLY_PLAYED);
};

Com_Zimbra_Video.prototype.addToRencentlyPlayedVideos = function(videoURL){
	var recent = this.RECENTLY_PLAYED.join(",");
	if(recent.indexOf(videoURL) == -1){
		if(this.RECENTLY_PLAYED.length > 10 ){
			this.RECENTLY_PLAYED.pop();
		}
		this.RECENTLY_PLAYED.unshift(videoURL);
	}
};

Com_Zimbra_Video.prototype.getRecentlyPlayedVideo = function(){
    return this.RECENTLY_PLAYED[0];
};

//Video: Handle Msg Drop

Com_Zimbra_Video.prototype._parseMessageForVideo = function(zmObject){

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

	this._createVideoLinksHtml(videoLinks);
};

Com_Zimbra_Video.prototype._createVideoLinksHtml = function(videoLinks){

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
        if(AjxEnv.isIE)
            radio.attachEvent("onclick",selectYouTubeURL)
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
                            this.playVideo(document.getElementById(radio.value).innerHTML);
                            break;
				      	}
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

function selectYouTubeURL(){
        var len = this.event.srcElement.parentElement.parentElement.parentElement.children.length;
        for(var i=0;i<len;i++){
            this.event.srcElement.parentElement.parentElement.parentElement.children[i].firstChild.firstChild.checked = false;
        }
        this.event.srcElement.checked=true;
    }


//Method: Search functionality
//Reference: http://www.google.com/uds/solutions/videosearch/index.html

Com_Zimbra_Video.prototype._loadGoogleSearchIncludes = function(){

	if(!Com_Zimbra_Video.GSEARCH_APIKEY){
		return;
	}

	window._uds_vsw_donotrepair = true;
	this._loadSearchAPI("http://www.google.com/uds/api?file=uds.js&v=1.0&key="+Com_Zimbra_Video.GSEARCH_APIKEY);
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

	var videoURL = videoSearchControl.player.childNodes[0].data || videoSearchControl.player.childNodes[0].src;

	if(!videoURL){
		this.displayErrorMessage("Please select a video and then play it in the sidebar.");
		return;
	}

	this.playVideo(videoURL);


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




//Video: Utilities

Com_Zimbra_Video.prototype.isValidVideoURL = function(videoURL){
	if(!videoURL) return false;
	var isVideoLink = videoURL.match(/(\b(((http | https)\:\/\/)?(www\.)?((youtube\.com\/watch\?v=)|(video\.google\.com\/videoplay\?docid=)|(video\.google\.com\/googleplayer\.swf\?docId=)|(youtube\.com\/v\/))((-)?[0-9a-zA-Z_]+)?(&\w+=\w+)*)\b)/gi);
	return (isVideoLink == null)?false:true;
//	var isVideoLink = videoURL.match(/(\b(((http | https)\:\/\/)?(www\.)?((youtube\.com\/watch\?v=)|(video\.google\.com\/videoplay\?docid=))(-)?[0-9a-zA-Z]+)\b)/gi);
//	videoURL.match(/(\b(((http | https)\:\/\/)?(www\.)?((youtube\.com\/watch\?v=)|(video\.google\.com\/videoplay\?docid=)|(video\.google\.com\/googleplayer\.swf\?docId=)|(youtube\.com\/v\/))(-)?[0-9a-zA-Z]+)\b)/gi);
};

Com_Zimbra_Video.prototype.getValidVideoLinksFromText = function(text){
	
	//var videoLinks = text.match(/(\b(((http | https)\:\/\/)?(www\.)?((youtube\.com\/watch\?v=)|(video\.google\.com\/videoplay\?docid=))(-)?[0-9a-zA-Z]+)\b)/gi);
	return text.match(/(\b(((http | https)\:\/\/)?(www\.)?((youtube\.com\/watch\?v=)|(video\.google\.com\/videoplay\?docid=)|(video\.google\.com\/googleplayer\.swf\?docId=)|(youtube\.com\/v\/))((-)?[0-9a-zA-Z_]+)?(&\w+=\w+)*)\b)/gi);
};

Com_Zimbra_Video.prototype._loadSearchAPI = function(file){
    var callback = new AjxCallback(this,this._postLoadSearchAPI);
    var serverURL = ZmZimletBase.PROXY + AjxStringUtil.urlComponentEncode(file);
    AjxRpc.invoke(null,serverURL,null,callback,true);
};

Com_Zimbra_Video.prototype._postLoadSearchAPI = function(result){

    var js = result.text;
    js = js.replace(/.compiled.js\"/i,".compiled.js\",true");
    try{
        AjxPackage.eval(js);
    }catch(ex){
        alert('Failed to load Google Video Search API.');
    }
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
