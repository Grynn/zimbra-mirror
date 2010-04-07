/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Zimlets
 * Copyright (C) 2007, 2009, 2010 Zimbra, Inc.
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
 * Constructor.
 * 
 */
function WordPress(parent) {
	this.controller = parent;
}

WordPress.prototype.constructor = WordPress;

WordPress.REQUEST_HEADER={"Content-Type":"text/xml"};

WordPress.prototype.getDisplayName = function(){
	return "WordPress";
};

WordPress.prototype.getUsersBlogsXmlDoc = function(user,passwd) {

	var methodCall = AjxXmlDoc.createRoot("methodCall");

	var methodName = AjxXmlDoc.createElement("methodName","blogger.getUsersBlogs");

	var params = AjxXmlDoc.createElement("params");

	methodCall.appendChild(methodName); 

	var param1=this._createParamNode("string","0123456789ABCDEF",false);
	var param2=this._createParamNode("string",user,false);
	var param3=this._createParamNode("string",passwd,false);

	methodCall.appendChild(param1);
	methodCall.appendChild(param2);
	methodCall.appendChild(param3);
	
	return methodCall;
}


WordPress.prototype.getCategoriesXmlDoc = function(blogid,username,password) {

	var methodCall = AjxXmlDoc.createRoot("methodCall");

	var methodName = AjxXmlDoc.createElement("methodName","metaWeblog.getCategories");

	var params = AjxXmlDoc.createElement("params");

	methodCall.appendChild(methodName); 

	var param1=this._createParamNode("string",blogid,false);
	var param2=this._createParamNode("string",username,false);
	var param3=this._createParamNode("string",password,false);

	
	params.appendChild(param1);
	params.appendChild(param2);
	params.appendChild(param3);
	
	methodCall.appendChild(params);
	
	return methodCall;
}


WordPress.prototype.newPostXmlDoc = function(blogid,username,password,title,description,categories) {

	var methodCall = AjxXmlDoc.createRoot("methodCall");

	var methodName = AjxXmlDoc.createElement("methodName","metaWeblog.newPost");

	var params = AjxXmlDoc.createElement("params");

	methodCall.appendChild(methodName); 

	var param1=this._createParamNode("string",blogid,false);
	var param2=this._createParamNode("string",username,false);
	var param3=this._createParamNode("string",password,false);

	var param4 = AjxXmlDoc.createElement("param");
        var value41 = AjxXmlDoc.createElement("value"); 
        var struct = AjxXmlDoc.createElement("struct"); 

	var member1=this._createMemberNode("title","string",title,false);
	var member2=this._createMemberNode("description","string",description,true);
	
	
	struct.appendChild(member1);
	struct.appendChild(member2);

	if(categories && categories.length > 0 ){
		var member3 = this._createCategoryMemberNode(categories);
		struct.appendChild(member3);
	}

	value41.appendChild(struct);

	param4.appendChild(value41);

	var param5=this._createParamNode("boolean","1",false);

	params.appendChild(param1);
	params.appendChild(param2);
	params.appendChild(param3);
	params.appendChild(param4);
	params.appendChild(param5);

	methodCall.appendChild(params);
	
	return methodCall;
};


WordPress.prototype._createParamNode = function(type,value,isCdata) {

	var param=AjxXmlDoc.createElement("param");
	var valueNode=AjxXmlDoc.createElement("value");

	var data=null;
	
	if(!isCdata){
		data = valueNode.getDoc().createTextNode("");
		data.nodeValue=value;
	}else{
		data = valueNode.getDoc().createCDATASection("");
		data.nodeValue=value;		
	}

	var valueNode1=AjxXmlDoc.createElement(type);		
	valueNode1.root.appendChild(data);
	valueNode.appendChild(valueNode1);

	param.appendChild(valueNode);

	return param;
};

WordPress.prototype._createCategoryMemberNode = function(categories)
{
		var member=AjxXmlDoc.createElement("member");
		var name=AjxXmlDoc.createElement("name","categories");
		var value=AjxXmlDoc.createElement("value");
		var array=AjxXmlDoc.createElement("array");
		var data=AjxXmlDoc.createElement("data");
		
		for(var i=0;i<categories.length;i++){
			var value1=AjxXmlDoc.createElement("value");
			var stringnode=AjxXmlDoc.createElement("string",categories[i]);
			value1.appendChild(stringnode);
			data.appendChild(value1);
		}
		
		array.appendChild(data);
		value.appendChild(array);
		member.appendChild(name);
		member.appendChild(value);
		
		return member;		
};


WordPress.prototype._createMemberNode = function(nameValue,type,value,isCdata) {

	var member=AjxXmlDoc.createElement("member");
	
	var name=AjxXmlDoc.createElement("name",nameValue);

	var valueNode=AjxXmlDoc.createElement("value");

	var data=null;
	
	if(!isCdata){
		data = valueNode.getDoc().createTextNode("");
		data.nodeValue=value;
	}else{
		data = valueNode.getDoc().createCDATASection("");
		data.nodeValue=value;		
	}

	var valueNode1=AjxXmlDoc.createElement(type);		
	valueNode1.root.appendChild(data);
	valueNode.appendChild(valueNode1);


	member.appendChild(name);
	member.appendChild(valueNode);

	return member;
};


WordPress.prototype.filterNodes = function(xmlDoc) {

var names = xmlDoc.getElementsByTagName("name");
var result=new Array();

	for(var i=0;i<names.length;i++){
		var node = names[i];
		if(node.nodeName=="name"){
		var name = node.firstChild.nodeValue;
		var typeN=node.nextSibling;	
			if(typeN){
				if(typeN.hasChildNodes()){
					var valueN=typeN.firstChild;
					if(valueN) {
						var value = valueN.firstChild.nodeValue;
						result[name]=value;
						DBG.println(AjxDebug.DBG1,name+"="+value);
					}	
				}				
			}
		}		
	}

return result;

};

WordPress.prototype.handleBlogListResponse = function(result) {
	
	var r = result.text;

	try{	
		
	var xmlDoc = AjxXmlDoc.createFromXml(r);

	var url,blogid,blogName=null;

	var struct = xmlDoc.getDoc().getElementsByTagName("struct");
	
	var binfo = [];
			
			DBG.println(AjxDebug.DBG1,"struct:"+struct.length);
			
			for(var i=0;i<struct.length;i++)
			{
				var node = struct[i];
				
				var result = this.filterNodes(node);	
				
				var burl = result['url'];
	
				burl = this.getAPIURL(burl);		
			
				binfo.push({blogid:result['blogid'],blogname:result['blogName'],url:burl});
			}

	/*
	var bloginfo = this.filterNodes(xmlDoc,"name");

	DBG.println(AjxDebug.DBG1,"ID:"+bloginfo["blogid"]);
	*/

	this.blogInfoHandler.run(binfo);
	
	}catch(ex){
		DBG.println(AjxDebug.DBG1,'unable to post blog :'+ex);
		this.controller.showWarningMsg(Com_Zimbra_Blog.UNABLE_TO_FETCH);
	}	
	
};

WordPress.prototype._handleCategoryResponse = function(result) {
	
	var r = result.text;
	var categories = new Array();

	try{
			var xmlDoc = AjxXmlDoc.createFromXml(r);
			
			var struct = xmlDoc.getDoc().getElementsByTagName("struct");
			
			for(var i=0;i<struct.length;i++)
			{
				var node = struct[i];
				
				var result = this.filterNodes(node);	
			
				categories.push(result['categoryName']);
			}
	
			this.categoryHandler.run(categories);			
			//this.showCategories(categories);
	}catch(ex){
		DBG.println(AjxDebug.DBG1,"unable to post to blog : "+ex);
		this.controller.showWarningMsg(Com_Zimbra_Blog.UNABLE_TO_FETCH_CATEGORY);
	}	
	
	return categories;
};



WordPress.prototype._getBlogList = function(params) {

	params.url = this.getAPIURL(params.url);

	var methodCall = this.getUsersBlogsXmlDoc(params.username,params.password);

	var reqHeader = WordPress.REQUEST_HEADER;
	
	DBG.println(AjxDebug.DBG1,'sending request 2');

	this.blogInfoHandler = params.callback;
	
	this.controller.sendRequest('<?xml version="1.0"?>'+methodCall.getDocXml(), params.url, reqHeader,  new AjxCallback(this,this.handleBlogListResponse), false);

};

WordPress.prototype._getCategories = function(params) {

	params.url = this.getAPIURL(params.url);

	var methodCall = this.getCategoriesXmlDoc(params.blogid,params.username,params.password);

	var reqHeader = WordPress.REQUEST_HEADER;
	
	DBG.println(AjxDebug.DBG1,'sending request 2');
	
	this.categoryHandler = params.callback;

	this.controller.sendRequest('<?xml version="1.0"?>'+methodCall.getDocXml(), params.url, reqHeader,  new AjxCallback(this,this._handleCategoryResponse), false);

};

WordPress.prototype._newPost = function(params) {

	params.url = this.getAPIURL(params.url);

	var methodCall = this.newPostXmlDoc(params.blogid,params.username,params.password,params.subject,params.body,params.categories);

	DBG.println(AjxDebug.DBG1,'sending request 2');
	
	DBG.dumpObj(methodCall);

	var reqHeader = WordPress.REQUEST_HEADER;

	this.controller.sendRequest('<?xml version="1.0"?>'+methodCall.getDocXml(), params.url , reqHeader,  params.callback , false);

};

WordPress.prototype.getAPIURL = function(burl){

	if(burl.indexOf('xmlrpc.php')<0){
		burl = burl + ((burl.charAt(burl.length-1)=='/')?'':'/')+ "xmlrpc.php";
	}
				
	return burl;
};
