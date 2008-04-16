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

function Com_Zimbra_Blog() {
this.note={};
this.blogid =  null;
};

Com_Zimbra_Blog.TITLE_WARNING = "Cannot post to blog. You must enter a title for the new blog post.";	
Com_Zimbra_Blog.UNABLE_TO_FETCH = "Unable to fetch blog information";
Com_Zimbra_Blog.UNABLE_TO_FETCH_CATEGORY = "Unable to fetch category information";

Com_Zimbra_Blog.prototype = new ZmZimletBase();
Com_Zimbra_Blog.prototype.constructor = Com_Zimbra_Blog;

Com_Zimbra_Blog.prototype.init = function(){
	DBG.println(AjxDebug.DBG1,"Blog  Zimlet Initialized");
};

Com_Zimbra_Blog.prototype.onShowView = function(viewId, isNewView) {
    if (viewId == ZmId.VIEW_NOTEBOOK_PAGE_EDIT && !this._toolbar){
        this._initPageEditToolbar();
    }
};

Com_Zimbra_Blog.prototype._initPageEditToolbar =
function() {
    try
	{
        if(!appCtxt.get(ZmSetting.NOTEBOOK_ENABLED)) this._toolbar = true;

        if(this._toolbar) { return; }
        
        this._composerCtrl = AjxDispatcher.run("GetPageEditController");
		if (!this._composerCtrl) { return; }
	    this._composerCtrl._blogPost = this;
    	if(!this._composerCtrl._toolbar[ZmId.VIEW_NOTEBOOK_PAGE_EDIT]) {
	      // initialize the compose controller's toolbar
	      this._composerCtrl._initializeToolBar(ZmId.VIEW_NOTEBOOK_PAGE_EDIT);
    	}

    	this._toolbar = this._composerCtrl._toolbar[ZmId.VIEW_NOTEBOOK_PAGE_EDIT];	

		ZmMsg.blogPost = "Post to Blog";
    	ZmMsg.blogPostTooltip = "Post this content to blog";
	    ZmMsg["blogPost"] = "Post to Blog";

	    var op = {textKey: "blogPost", tooltipKey: "blogPostTooltip", image: "Blog-panelIcon"};
    	var opDesc = ZmOperation.defineOperation(null, op);
    	ZmOperation.addOperation(this._toolbar, opDesc.id, this._toolbar._buttons, 1);

	    this._toolbar.addSelectionListener(opDesc.id, new AjxListener(this, this._postToBlog));
	}
	catch(ex)
	{
		DBG.println(AjxDebug.DBG1,"exception in blog init:"+ex);
	}
};

Com_Zimbra_Blog.prototype._postToBlog = function(ev) {
	
	this._createBlogHandler();
	
	var pageEditor = this._composerCtrl._pageEditView._pageEditor;
	var content = pageEditor.getContent();	
	var name = this._composerCtrl._pageEditView.getPageName();
		
	this.note={subject:name,body:content};
	
	var user = this.getUserProperty("user");
	var passwd = this.getUserProperty("passwd");
	var blogurl = this.getUserProperty("blogurl");
	
	
	if(user==null || user=="" || passwd==null || passwd=="" || blogurl==null || blogurl=="")
	{
		this.createPropertyEditor(new AjxCallback(this, this._postToBlog));
		return;
	}
	
	var params = {
		username: this.getUserProperty("user"),
		password: this.getUserProperty("passwd"),
		url: this.getUserProperty("blogurl"),
		callback: new AjxCallback(this, this._handleBlogInfo)
	};
	this.currentBlog._getBlogList(params);
	
};


Com_Zimbra_Blog.prototype.singleClicked = function(myElement, myNumber) {
	
	//this.showCategories(['BlogRoll','Uncategorized','Web2.0']);		

};

Com_Zimbra_Blog.prototype.checkPreferences=function(callback,args)
{
	var user = this.getUserProperty("user");
	var passwd = this.getUserProperty("passwd");
	var blogurl = this.getUserProperty("blogurl");

	if(callback==null){
		callback=this.checkPreferences;
	}
	
	if(args==null){
		args=[];
	}
	
	if(user==null || user=="" || passwd==null || passwd=="" || blogurl==null || blogurl=="")
	{
	this.createPropertyEditor(new AjxCallback(this, callback,args));
	return false;
	}else{
	}

	return true;

};

Com_Zimbra_Blog.prototype._handleBlogInfo = function(bloginfo) {


	try{
	
	if(typeof bloginfo.length=="undefined" || bloginfo.length == 1)	{
		
		var blogid = null;
		
		if(bloginfo.length==1){
			blogid = bloginfo[0].blogid;
		}else{
			blogid = bloginfo["blogid"];
		}
		
		if(blogid)
		{
	 	this.blogid = blogid;
	 	this.blogurl = this.getUserProperty("blogurl");
	 
	 	var params = {
	 		blogid: blogid,
		 	url: this.getUserProperty("blogurl"),
		 	username: this.getUserProperty("user"),
	 		password: this.getUserProperty("passwd"),	 	
		 	callback: new AjxCallback(this, this.showCategories)
		 };
	 
 	 	this.currentBlog._getCategories(params);
	 
		}else{		
		this.displayErrorMessage("Unable to get blog list. Please check username, password and api url.");	
		}

	}else{
		this.showBlogs(bloginfo);
	}
	
	}catch(ex){
		DBG.println(AjxDebug.DBG1,'unable to post blog :'+ex);
	}	
};

Com_Zimbra_Blog.prototype._resultCallback1 = function(result) {
	var r = result.text;

	try{

	this.displayStatusMessage('Posted in '+this.currentBlog.getDisplayName()); 
	
	DBG.dumpObj(r);
 

	}catch(ex){
		DBG.println(AjxDebug.DBG1,"unable to post to blog :"+ex);
	}	
};

Com_Zimbra_Blog.prototype.doDrop = function(obj) {
	
	switch (obj.TYPE) {
	    case "ZmMailMsg":
	    case "ZmConv":
		this.noteDropped(obj);
		break;
	    default:
		if(!obj.TYPE){ obj.TYPE = 'Unknown Object'; }
		this.displayErrorMessage("You somehow managed to drop a \"" + obj.TYPE
					 + "\" but however the Blog Zimlet does't support it for drag'n'drop.");
	}

	
};

Com_Zimbra_Blog.prototype.noteDropped = function(note) {


	var user = this.getUserProperty("user");
	var passwd = this.getUserProperty("passwd");
	var blogurl = this.getUserProperty("blogurl");
	
	if(user==null || user=="" || passwd==null || passwd=="" || blogurl==null || blogurl=="")
	{
	this.createPropertyEditor(new AjxCallback(this, this.noteDropped, [ note ]));
	return;
	}
	
	if(!note) {return;}
	this.displayStatusMessage('Posting ...'); 
	DBG.println(AjxDebug.DBG1,"not dropped");       
	this.note=note;
	var params = {
		username: user,
		password: passwd,
		url: blogurl,
		callback: new AjxCallback(this, this._handleBlogInfo)
	};

	this._createBlogHandler();
	this.currentBlog._getBlogList(params);

};

Com_Zimbra_Blog.prototype.showCategories = function(categories) {

if(categories.length == 0){

return;
}

var view = new DwtComposite(this.getShell());

var cat_dialog = new DwtPropertyEditor(view, true);
	
  	    var name = this.note.subject;
  	    
  	    if(name==""){
  	    	name = "Untitled";
  	    }
	
        var tmp = [
        {   label     : "Post Title",
            name      : "_title",
            type      : "string",
            value     : name
        },
     	{   label     : "Categories",
            name      : "_category",
            type      : "checkboxgroup",
            checkBox  : []
        }
        ];                
        
         
         for(var i=0;i<categories.length;i++)
         {
         	var catOption = {
         		label	:	categories[i],
         		name	:	"_cat_"+categories[i],
         		type      : "boolean",
         		value	:	categories[i]         		
         	};
         	tmp[1].checkBox.push(catOption);
         }        

		DBG.dumpObj(tmp);
	
		cat_dialog.initProperties(tmp);

		var dialog_args = {
		view  : view,
		title : this.currentBlog.getDisplayName()+" - Select Category"
		};

		var dlg = this._createDialog(dialog_args);
		//var okButton = dlg.getButton(DwtDialog.OK_BUTTON);
		cat_dialog.setFixedLabelWidth();
	 	cat_dialog.setFixedFieldWidth();
		dlg.popup();

		this.categoryDialog = cat_dialog;
		this.popupDialog = dlg;
		dlg.setButtonListener(DwtDialog.OK_BUTTON,new AjxListener(this,this.onOKPress));
	

};
Com_Zimbra_Blog.prototype.onOKPress = function()
{

	var dlg = this.popupDialog;
	dlg.popdown();
	var info = this.categoryDialog.getProperties();
	var category = info._category;
	var title = info._title;	
	
	if(title == null || title == ""){		
		this.showWarningMsg(Com_Zimbra_Blog.TITLE_WARNING,new AjxListener(this,this.onWarningAccepted));
	}else{
		
		if(this.warningDialog){
			this.warningDialog.dispose();
		}
		if(this.popupDialog){
			this.popupDialog.dispose();
		}		
		
		this.note.subject = title;
		
		this.note.body  = this.note.body.replace(/\n/g,' ');
		
		var params = {
			blogid:	this.blogid,
			url:	this.blogurl,
			username:	this.getUserProperty("user"),
			password:	this.getUserProperty("passwd"),
			categories:	category,
			subject:	this.note.subject,
			body: this.note.body,
			callback:	new AjxCallback(this,this._resultCallback1)
		};
		this.currentBlog._newPost(params);
	}

		
};
		

Com_Zimbra_Blog.prototype.showWarningMsg=function(message,listener)
{
		var style = DwtMessageDialog.WARNING_STYLE;
		var dialog = appCtxt.getMsgDialog();
		this.warningDialog = dialog;
		dialog.setMessage(message, style);
		if(listener){
		dialog.setButtonListener(DwtDialog.OK_BUTTON,listener);
		}
		dialog.popup();			
};
	
Com_Zimbra_Blog.prototype.onWarningAccepted = function()
{
	this.warningDialog.popdown();
	this.popupDialog.popup();	
};

Com_Zimbra_Blog.prototype.showBlogs = function(blogs) {

if(blogs.length == 0){

return;
}

var view = new DwtComposite(this.getShell());

var blogs_dialog = new DwtPropertyEditor(view, true);
	
  	    var name = this.note.subject;
	
        var tmp = [
        {   label     : "Blog",
            name      : "_blog",
            type      : "enum",
			item	  : [],
			value	  : blogs[0].blogid
        }        
        ];
        
        this.blogsList = [];
        
        for(var i=0;i<blogs.length;i++)
        {     
       		DBG.println(AjxDebug.DBG1,"MENUBOX:"+blogs[i].blogid);   	//cremove
        	tmp[0].item.push({label:blogs[i].blogname,value:blogs[i].blogid+""});        
        	this.blogsList[blogs[i].blogid] = blogs[i];
        	
        }//for
         
		blogs_dialog.initProperties(tmp);
		
		var dialog_args = {
		view  : view,
		title : "Select Blog" 
		};
		var dlg = this._createDialog(dialog_args);
		//var okButton = dlg.getButton(DwtDialog.OK_BUTTON);
		blogs_dialog.setFixedLabelWidth();
	 	blogs_dialog.setFixedFieldWidth();
		dlg.popup();
		
		this.blogsDialog = dlg;
		this.blogsProperties = blogs_dialog;
		dlg.setButtonListener(DwtDialog.OK_BUTTON,new AjxListener(this,this.handleBlogSelection));
	
};

Com_Zimbra_Blog.prototype.handleBlogSelection = function(){
	
	var dlg = this.blogsDialog;
	dlg.popdown();
	
	var info = this.blogsProperties.getProperties();
	var bid = info._blog;
	this.blogid = bid;
	var burl = this.getUserProperty("blogurl");
	
	if(this.blogsList[bid] && this.blogsList[bid].url){
		burl = this.blogsList[bid].url;
	}
	
	this.blogurl = burl;
	
	 	var params = {
	 		blogid: bid,
		 	url: burl,
		 	username: this.getUserProperty("user"),
	 		password: this.getUserProperty("passwd"),	 	
		 	callback: new AjxCallback(this, this.showCategories)
		 };
	
	 	this.currentBlog._getCategories(params);
};
	
Com_Zimbra_Blog.prototype._createBlogHandler = function(){
	
	var blogtype = this.getUserProperty("blogtype");
	
	if(blogtype == "TypePad"){
	this.currentBlog = new TypePad(this);		
	}else{
	this.currentBlog = new WordPress(this);			
	}
		
};

Com_Zimbra_Blog.prototype.menuItemSelected = function(itemId) {
	switch (itemId) {
	    case "PREFERENCES":
		this.createPropertyEditor();
		break;	    
	}
};
