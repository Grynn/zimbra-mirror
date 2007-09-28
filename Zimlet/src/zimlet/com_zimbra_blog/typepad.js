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

function TypePad(parent){
	
	this._WSSEHeader = [];
	this.controller = parent;
};

TypePad.URL="https://www.typepad.com/t/atom/weblog";

TypePad.prototype.getDisplayName = function(){
	return "TypePad";
};

TypePad.prototype._getBlogList = function(params) {
	
	DBG.println(AjxDebug.DBG1,'TypePad :: getBlogList');
	this.onHeaderInit = new AjxCallback(this,this._getBlogList1,params);
	this.generateWSSHeader(this.controller.getResource("wsse-header.jsp"),params.username,params.password);	
	//todo : header sync

};


TypePad.prototype._newPost = function(params) {

	var methodCall = this.newPostXmlDoc(params.subject,params.body,params.categories);

	var url = this.bloginfo[params.blogid].blogurl;

	DBG.println(AjxDebug.DBG1,'TypePad :: _newPost : url :'+url);
	
	DBG.println(AjxDebug.DBG1,'Sending new post request');
	
	DBG.dumpObj(methodCall);

	var newXml = methodCall.getDocXml();	
	newXml =  newXml.replace(/xmlns=""/g,"");
			
	var reqHeader = {"X-WSSE":this._WSSEHeader[params.username+"-"+params.password]};
	this.controller.sendRequest('<?xml version="1.0"?>'+newXml, url , reqHeader,  params.callback , false);

};

TypePad.prototype._getCategories = function(params) {

	var url = this.bloginfo[params.blogid].blogurl+"/svc=categories";

	var reqHeader = {"X-WSSE":this._WSSEHeader[params.username+"-"+params.password]};
	
	DBG.println(AjxDebug.DBG1,'TypePad :: _getCategories : url :'+url);
	
	this.categoryHandler = params.callback;
	this.controller.sendRequest('', url, reqHeader,  new AjxCallback(this,this.handleCategoryResponse), true);

};


TypePad.prototype.newPostXmlDoc = function(title,description,categories) {

	var entryN = AjxXmlDoc.createRoot("entry");
	var titleN = AjxXmlDoc.createElement("title",title);
	
	var contentN = AjxXmlDoc.createElement("content");
	var divN = AjxXmlDoc.createElement("div");

	entryN.root.setAttribute("xmlns","http://purl.org/atom/ns#");
	entryN.root.setAttribute("xmlns:dc","http://purl.org/dc/elements/1.1/");
//	contentN.root.setAttribute("type","application/xhtml+xml");
	contentN.root.setAttribute("type","text/html");
	contentN.root.setAttribute("mode","escaped");
	divN.root.setAttribute("xmlns","http://www.w3.org/1999/xhtml");
	


	entryN.appendChild(titleN);
	
	for(var i=0;i<categories.length;i++){
	var dcsubjectN = AjxXmlDoc.createElement("dc:subject",categories[i]);
	entryN.appendChild(dcsubjectN);
	}
	
	var data = divN.getDoc().createCDATASection("");
	data.nodeValue = description;
	divN.root.appendChild(data);
	
	contentN.appendChild(divN);
	entryN.appendChild(contentN);

	return entryN;
};

TypePad.prototype.generateWSSHeader = function(url,username,password)
{
		this.username = username;
		this.password = password;
		var params = "username="+username+"&password="+password;
		var reqHeader = {"Content-Type":"application/x-www-form-urlencoded"};
		AjxRpc.invoke(params, url, reqHeader,  new AjxCallback(this, this._headerCallback), false);	
};

TypePad.prototype._headerCallback = function(result){
	
	var t = result.text;
	t=AjxStringUtil.trim(t);
	this._WSSEHeader[this.username+"-"+this.password]=t;
	
	if(this.onHeaderInit)
	{
		this.onHeaderInit.run();
		this.onHeaderInit=null;
	}
	
};

TypePad.prototype.handleBlogListResponse = function(result) {
	
	var r = result.text;
	r= AjxStringUtil.trim(r);
	var binfo = [];
	this.bloginfo = [];
	try{
	
			var xmlDoc = AjxXmlDoc.createFromXml(r);
			var feedNode = this.getFeed(xmlDoc);
			var cNodes = feedNode.childNodes;
			var len = cNodes.length;
			for(var i=0;i<len;i++){
					var cnode = cNodes[i];
					if( cnode && (cnode.nodeType == AjxUtil.ELEMENT_NODE ) && (cnode.tagName!=null) && (cnode.tagName.toLowerCase() == "link") ){
					var nType = cnode.nodeType
					var rel = cnode.getAttribute('rel');
					
					if(rel == "service.post"){				
					
					var burl = cnode.getAttribute("href");
					var bid = null;
					if(burl && burl.indexOf("blog_id=")>0){
						bid= burl.substring(burl.indexOf("blog_id=")+8);
					}
					var param = {blogid: bid, blogurl: burl ,blogname: cnode.getAttribute("title")};
					binfo.push(param);				
					this.bloginfo[bid]=param;
					}
					}		
			}
			this.blogInfoHandler.run(binfo);		
	
			/*
			var xmlDoc = result.xml;
			var linkNode = xmlDoc.getElementsByTagName("link");			
						
			for(var i=0;i<linkNode.length;i++)
			{
				
				var node = linkNode[i];
				var rel = node.getAttribute("rel");
				
				if(rel == "service.post"){
				
				var burl = node.getAttribute("href");
				var bid = null;
				if(burl && burl.indexOf("blog_id=")>0){
					bid= burl.substring(burl.indexOf("blog_id=")+8);
				}
				var param = {blogid: bid, blogurl: burl ,blogname: node.getAttribute("title")};
				binfo.push(param);
				
				this.bloginfo[bid]=param;
				}			

				
			}
		
		this.blogInfoHandler.run(binfo);
		*/	
	}catch(ex){
		DBG.println(AjxDebug.DBG1,'unable to post blog :'+ex);
		this.controller.showWarningMsg(Com_Zimbra_Blog.UNABLE_TO_FETCH);
	}	
	
};


TypePad.prototype._getBlogList1 = function(params){

	DBG.println(AjxDebug.DBG1,'TypePad :: _getBlogList1');

	var reqHeader = this._WSSEHeader[params.username+"-"+params.password];
	this.blogInfoHandler = params.callback;	
	reqHeader =  AjxStringUtil.trim(reqHeader);
	var header = {"X-WSSE":reqHeader};

	DBG.println(AjxDebug.DBG1,"Request Header :["+reqHeader+"]");

	this.controller.sendRequest('', TypePad.URL , header ,  new AjxCallback(this,this.handleBlogListResponse), true);
};

TypePad.prototype.handleCategoryResponse = function(result) {
	
	var r = result.text;
	
	var categories = [];

	try{
	   
	   var xmlDoc = AjxXmlDoc.createFromXml(r);
	   var feedNode = this.getFeed(xmlDoc);
		for (var i = feedNode.firstChild; i; i = i.nextSibling) {
			var node=i;
			if( (node.nodeType == AjxUtil.ELEMENT_NODE) && (node.tagName=="subject") ){
				categories.push(node.firstChild.nodeValue);
			}
		}		 

	}catch(ex){
		DBG.println(AjxDebug.DBG1,'unable to post blog :'+ex);
		this.controller.showWarningMsg(Com_Zimbra_Blog.UNABLE_TO_FETCH_CATEGORY);
	}	

	this.categoryHandler.run(categories);			
};

TypePad.prototype.getFeed = function(xmlDoc){
	
	return AjxEnv.isIE? xmlDoc._doc.childNodes[1] : xmlDoc._doc.childNodes[0];
	
};