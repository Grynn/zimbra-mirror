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
 *@Author Raja Rao DV
 */

function com_zimbra_rssfeeds() {
}
com_zimbra_rssfeeds.prototype = new ZmZimletBase();
com_zimbra_rssfeeds.prototype.constructor = com_zimbra_rssfeeds;
com_zimbra_rssfeeds.view = "message";
com_zimbra_rssfeeds.rssFeedsFolder = 'RSS Feeds';

com_zimbra_rssfeeds.prototype._loadFeeds =
function() {
	this.feeds = [];
	//c = category, d=domain, t=title(in the dialog), fn=(folderName)
	this.feeds["http://feedproxy.google.com/AbcNews_TopStories"] = {c:"Top Stories", d:"www.abc.com",  t:"ABC News: Home Page", fn:"ABC News"};
	this.feeds["http://newsrss.bbc.co.uk/rss/newsonline_world_edition/front_page/rss.xml"] = {c:"Top Stories", d:"www.bbc.com", t: "BBC News | News Front Page | World Edition", fn:"BBC News"};
	this.feeds["http://rss.cnn.com/rss/cnn_topstories.rss"] = {c:"Top Stories", d:"www.cnn.com",  t:"CNN.com", fn:"CNN.com"};
	this.feeds["http://www.cbsnews.com/feeds/rss/main.rss"] = {c:"Top Stories", d:"www.cbsnews.com",  t:"CBSNews.com: Breaking News", fn:"CBSNews.com"};
	this.feeds["http://rss.cbc.ca/lineup/topstories.xml"] = {c:"Top Stories", d:"www.cbc.ca",  t:"CBC | Top Stories News", fn:"CBC News"};
	this.feeds["http://feeds2.feedburner.com/Consortiumnewscom"] = {c:"Top Stories", d:"Consortiumnews.com",  t:"Consortiumnews.com", fn:"Consortiumnews"};
	this.feeds["http://feeds.foxnews.com/foxnews/latest?format=xml"] = {c:"Top Stories", d:"www.foxnews.com",  t:"foxnews.com", fn:"Fox News"};
	this.feeds["http://news.google.com/news?pz=1&ned=us&hl=en&output=rss"] = {c:"Top Stories", d:"www.google.com",  t:"Google News", fn:"Google News" };
	this.feeds["http://feeds.guardian.co.uk/theguardian/rss"] = {c:"Top Stories", d:"www.guardian.co.uk",  t:"guardian.co.uk", fn:"Guardian.co.uk" };
	this.feeds["http://rss.msnbc.msn.com/id/3032091/device/rss/rss.xml"] = {c:"Top Stories", d:"www.msnbc.com", t:"msnbc.com: Top msnbc.com headlines", fn:"MSNBC.com"};
	this.feeds["http://www.nytimes.com/services/xml/rss/nyt/HomePage.xml"] = {c:"Top Stories", d:"www.nytimes.com", t:"NYT > Home Page", fn:"NewYorkTimes.com"};
	this.feeds["http://feeds.reuters.com/reuters/topNews"] = {c:"Top Stories", d:"www.reuters.com", t:"Reuters: Top News", fn:"Reuters"};
	this.feeds["http://feeds2.feedburner.com/time/topstories?format=xml"] = {c:"Top Stories", d:"www.time.com", t: "TIME.com: Top Stories", fn:"Time.com"};
	this.feeds["http://rssfeeds.usatoday.com/usatoday-NewsTopStories"] = {c:"Top Stories", d:"www.usatoday.com", t:"USATODAY.com News - Top Stories", fn:"USATODAY.com"};	
	this.feeds["http://feeds.wsjonline.com/wsj/xml/rss/3_7011.xml"] = {c:"Top Stories", d:"www.wsj.com", t:"WSJ.com: What's News US", fn:"WSJ.com"};
	this.feeds["http://rss.wn.com/English/top-stories"] = {c:"Top Stories", d:"www.worldnews.com", t:"WN.com - Top English Stories", fn:"WN.com"};
	this.feeds["http://rss.news.yahoo.com/rss/topstories"] = {c:"Top Stories", d:"www.yahoo.com", t:"Yahoo! News: Top Stories", fn:"Yahoo News"};


	this.feeds["http://rss.businessweek.com/bw_rss/bwdaily"] =  {c:"Finance", d:"www.businessweek.com", t: "BusinessWeek - Top News", fn:"BusinessWeek" };
 	this.feeds["http://feeds2.feedburner.com/bizj_national"] = {c:"Finance", d:"www.bizjournals.com", t: "bizjournals | National Business News - Local Business News ", fn:"bizjournals"};
	this.feeds["http://www.bankrate.com/rss/Bankrate_TopStory_brm_rss.xml"] = {c:"Finance", d:"www.bankrate.com", t: "Bankrate.com: Top stories", fn:"Bankrate.com"};
	this.feeds["http://rss.cnn.com/rss/money_topstories.rss"] = {c:"Finance", d:"www.cnn.com", t: "Business and financial news - CNNMoney.com", fn:"CNNMoney.com"};
	this.feeds["http://www.cnbc.com/id/19746125/device/rss/rss.xml"] = {c:"Finance", d:"www.cnbc.com", t: "CNBC Top News and Analysis", fn:"CNBC"};
	this.feeds["http://www.entrepreneur.com/feeds/latest.html"] = {c:"Finance", d:"www.Entrepreneur.com", t: "Entrepreneur.com: Latest Articles", fn:"Entrepreneur.com"};
	this.feeds["http://www.ft.com/rss/home/us"] = {c:"Finance", d:"www.ft.com", t: "Financial Times - US homepage", fn:"Financial Times"};
	this.feeds["http://www.forbes.com/news/index.xml"] = {c:"Finance", d:"www.forbes.com", t: "Forbes.com: News", fn:"Forbes.com"};
	this.feeds["http://feeds.fool.com/usmf/foolwatch"] = {c:"Finance", d:"www.fool.com", t: "Fool.com: The Motley Fool", fn:"Fool.com" };
	this.feeds["http://feeds.marketwatch.com/marketwatch/topstories/"] = {c:"Finance", d:"www.marketwatch.com", t: "MarketWatch.com - Top Stories", fn:"MarketWatch.com" };
	this.feeds["http://articles.moneycentral.msn.com/Feeds/RSS/latestrss.aspx"] = {c:"Finance", d:"www.msn.com", t: "MSN Money Latest Articles", fn:"MSN Money"};
	this.feeds["http://feeds.thestreet.com/tsc/feeds/rss/top-read-stories"] = {c:"Finance", d:"www.thestreet.com", t: "TheStreet.com | Top Read Stories", fn:"TheStreet.com"};
	this.feeds["http://seekingalpha.com/feed.xml"] = {c:"Finance", d:"www.SeekingAlpha.com", t: "SeekingAlpha.com: Home Page", fn:"MSN Money", fn:"SeekingAlpha.com"};
	this.feeds["http://rss.news.yahoo.com/rss/business"] = {c:"Finance", d:"www.yahoo.com", t: "Yahoo! News: Business", fn:"Yahoo Business News"};
	this.feeds["http://rss.news.yahoo.com/rss/stocks"] = {c:"Finance", d:"www.yahoo.com", t: "Yahoo! News: Stocks", fn:"Yahoo Stocks News"};
	this.feeds["http://rss.news.yahoo.com/rss/eurobiz"] = {c:"Finance", d:"www.yahoo.com", t: "Yahoo! News: EuroBiz", fn:"Yahoo EuroBiz News"};



	this.feeds["http://allthingsd.com/feed/"] =  {c:"Technology", d:"www.allthingsd.com", t: "All Things Digital", fn:"All Things Digital"};
	this.feeds["http://feedproxy.google.com/CrunchGear"] =  {c:"Technology", d:"www.CrunchGear.com", t: "CrunchGear.com", fn:"CrunchGear.com"};
	this.feeds["http://feeds.digg.com/digg/container/technology/popular.rss"] =  {c:"Technology", d:"www.digg.com", t: "digg.com: Stories / Technology / Popular", fn:"digg.com"};
	this.feeds["http://www.engadget.com/rss.xml"] =  {c:"Technology", d:"www.Engadget.com", t: "Engadget.com", fn:"Engadget.com"};
	this.feeds["http://feeds2.feedburner.com/ommalik"] =  {c:"Technology", d:"www.gigaom.com", t: "GigaOM", fn:"GigaOM.com"};
	this.feeds["http://googleblog.blogspot.com/"] =  {c:"Technology", d:"www.google.com", t: "The Official Google Blog", fn:"Google Blog"};
	this.feeds["http://feeds2.feedburner.com/Mashable"] =  {c:"Technology", d:"www.mashable.com", t: "Mashable!", fn:"Mashable"};
	this.feeds["http://feeds.pcworld.com/pcworld/latestnews"] =  {c:"Technology", d:"www.pcworld.com", t: "PC World Latest Technology News", fn:"pcworld"};
	this.feeds["http://feeds2.feedburner.com/readwriteweb"] =  {c:"Technology", d:"www.ReadWriteWeb.com", t: "ReadWriteWeb", fn:"ReadWriteWeb"};
	this.feeds["http://feedproxy.google.com/typepad/alleyinsider/silicon_alley_insider"] =  {c:"Technology", d:"www.businessinsider.com", t: "Silicon Alley Insider", fn:"Silicon Alley Insider"};
	this.feeds["http://rss.slashdot.org/Slashdot/slashdot"] =  {c:"Technology", d:"www.Slashdot.org", t: "Slashdot", fn:"Slashdot"};
	this.feeds["http://feedproxy.google.com/TechCrunch"] =  {c:"Technology", d:"www.techcrunch.com", t: "TechCrunch", fn:"TechCrunch"};
	this.feeds["http://blogs.techrepublic.com.com/wp-rss2.php"] =  {c:"Technology", d:"www.techrepublic.com", t: "TechRepublic Blogs",  fn:"TechRepublic Blogs"};
	this.feeds["http://feedproxy.google.com/techspot/news"] =  {c:"Technology", d:"www.techspot.com", t: "TechSpot",  fn:"TechSpot"};
	this.feeds["http://valleywag.gawker.com/tag/valleywag/index.xml"] =  {c:"Technology", d:"valleywag.gawker.com", t: "Valleywag",  fn:"Valleywag"};
	this.feeds["http://feeds2.feedburner.com/venturebeat"] =  {c:"Technology", d:"venturebeat.com", t: "VentureBeat",  fn:"VentureBeat"};
	this.feeds["http://feeds.wired.com/wired/index"] =  {c:"Technology", d:"www.wired.com", t: "Wired Top Stories",  fn:"Wired Top Stories"};
	this.feeds["http://feeds2.feedburner.com/yodelanecdotal"] =  {c:"Technology", d:"www.yahoo.com", t: "Yodel Anecdotal |Yahoo!'s Official blog",  fn:"Yahoo Official Blog"};


	this.feeds["http://rss.csmonitor.com/feeds/top"] =  {c:"Politics", d:"www.csmonitor.com", t: "Christian Science Monitor | Top Stories", fn:"Christian Science Monitor"};
	this.feeds["http://feeds2.feedburner.com/crooksandliars/YaCP"] =  {c:"Politics", d:"www.crooksandliars.com", t: "Crooks and Liars", fn:"Crooks and Liars"};
	this.feeds["http://feeds.dailykos.com/dailykos/index.xml"] =  {c:"Politics", d:"www.dailykos.com", t: "Daily Kos", fn:"Daily Kos"};
	this.feeds["http://www.freerepublic.com/tag/*/feed.rss"] =  {c:"Politics", d:"www.freerepublic.com", t: "Free Republic | Latest Articles", fn:"Free Republic"};
	this.feeds["http://feeds2.feedburner.com/hotair/main"] =  {c:"Politics", d:"www.hotair.com", t: "Hot Air » Top Picks", fn:"Hot Air"};
	this.feeds["http://thinkprogress.org/feed/"] =  {c:"Politics", d:"www.thinkprogress.org", t: "Think Progress", fn:"Think Progress"};
	this.feeds["http://feeds.huffingtonpost.com/huffingtonpost/raw_feed"] =  {c:"Politics", d:"www.huffingtonpost.com", t: "The Full Feed from HuffingtonPost.com", fn:"huffingtonpost.com"};
	this.feeds["http://www.newsmax.com/xml/newsfront.xml"] =  {c:"Politics", d:"www.newsmax.com", t: "Newsmax - Newsfront", fn:"Newsmax"};
	this.feeds["http://www.nationalreview.com/index.xml"] =  {c:"Politics", d:"www.nationalreview.com", t: "National Review Online", fn:"National Review"};
	this.feeds["http://www.politico.com/rss/politicopicks.xml"] =  {c:"Politics", d:"www.politico.com", t: "Politico Top Stories", fn:"politico.com"};
	this.feeds["http://rightwingnuthouse.com/feed/"] =  {c:"Politics", d:"www.rightwingnuthouse.com", t: "Right Wing Nut House", fn:"Right Wing Nut House"};
	this.feeds["http://feeds.feedburner.com/realclearpolitics/qlMj"] =  {c:"Politics", d:"www.realclearpolitics.com", t: "RealClearWorld", fn:"RealClearWorld"};
	this.feeds["http://feeds.salon.com/salon/news"] =  {c:"Politics", d:"www.salon.com", t: "Salon: News & Politics", fn:"Salon"};
	this.feeds["http://www.thehill.com/index.php?option=com_rd_rss&id=1"] =  {c:"Politics", d:"www.thehill.com", t: "TheHill.com: Top News", fn:"thehill"};	
	this.feeds["http://wnd.com/?ol=0&fa=PAGE.rss"] =  {c:"Politics", d:"www.wnd.com", t: "WorldNetDaily", fn:"WorldNetDaily"};


};


com_zimbra_rssfeeds.prototype.getMainFolderId =
function(callback) {
	if(this.mainRssFeedFldrId)
		return;
	var soapDoc = AjxSoapDoc.create("GetFolderRequest", "urn:zimbraMail");
	var folderNode = soapDoc.set("folder");
	folderNode.setAttribute("l", appCtxt.getFolderTree().root.id);

	var command = new ZmCsfeCommand();
	var top = command.invoke({soapDoc: soapDoc}).Body.GetFolderResponse.folder[0];

	var folders = top.folder;
	if (folders) {
		for (var i = 0; i < folders.length; i++) {
			var f = folders[i];
			if (f && f.name == com_zimbra_rssfeeds.rssFeedsFolder && f.view == com_zimbra_rssfeeds.view) {
				this.mainRssFeedFldrId = f.id;
				break;
			}
		}
	}
	if(this.mainRssFeedFldrId) {
		if (callback)
			callback.run(this);
	} else {
		this.createMainFolder(callback);	//there is no such folder, so create one.
	}
};

com_zimbra_rssfeeds.prototype.createMainFolder =
function(postCallback) {
	var params = {color:null, name:com_zimbra_rssfeeds.rssFeedsFolder, url:null, view:com_zimbra_rssfeeds.view, l:"1", postCallback:postCallback};
	this._createFolder(params, postCallback);
};

com_zimbra_rssfeeds.prototype.doubleClicked =
function() {		
	this.singleClicked();
};

com_zimbra_rssfeeds.prototype.singleClicked =
	function() {		
		this._initializeDlg();
	};

com_zimbra_rssfeeds.prototype._createFolder =
function(params) {

	var jsonObj = {CreateFolderRequest:{_jsns:"urn:zimbraMail"}};
	var folder = jsonObj.CreateFolderRequest.folder = {};
	for (var i in params) {
		if (i == "callback" || i == "errorCallback" || i == "postCallback") { 
			continue; 
		}

		var value = params[i];
		if (value) {
			folder[i] = value;
		}
	}
	var _createFldrCallback =  new AjxCallback(this, this._createFldrCallback, params);
	var _createFldrErrCallback = new AjxCallback(this, this._createFldrErrCallback, params);
	return appCtxt.getAppController().sendRequest({jsonObj:jsonObj, asyncMode:true, errorCallback:_createFldrErrCallback, callback:_createFldrCallback});
}

com_zimbra_rssfeeds.prototype._createFldrCallback =
function(params, response) {
	if(params.name == com_zimbra_rssfeeds.rssFeedsFolder) {
		this.mainRssFeedFldrId = response.getResponse().CreateFolderResponse.folder[0].id;
		if (params.postCallback){
			params.postCallback.run(this);
		}
	} else {
		var transitions = [ZmToast.FADE_IN, ZmToast.PAUSE, ZmToast.FADE_OUT];
		appCtxt.getAppController().setStatusMsg("RSS Feed Created", ZmStatusView.LEVEL_INFO, null, transitions);
	}
};

com_zimbra_rssfeeds.prototype._createFldrErrCallback =
function(params, ex) {
	if (!params.url && !params.name) {
		return false; 
	}
	
	var msg;
	if (params.name && (ex.code == ZmCsfeException.MAIL_ALREADY_EXISTS)) {
		msg = AjxMessageFormat.format(ZmMsg.errorAlreadyExists, [params.name]);
	} else if (params.url) {
		var errorMsg = (ex.code == ZmCsfeException.SVC_RESOURCE_UNREACHABLE) ? ZmMsg.feedUnreachable : ZmMsg.feedInvalid;
		msg = AjxMessageFormat.format(errorMsg, params.url);
	}
		var transitions = [ZmToast.FADE_IN, ZmToast.PAUSE, ZmToast.FADE_OUT];
		appCtxt.getAppController().setStatusMsg("Could Not Create RSS Feed", ZmStatusView.LEVEL_WARNING, null, transitions);
	if (msg) {
		this._showErrorMsg(msg);
		return true;
	}

	return false;
};

com_zimbra_rssfeeds.prototype._showErrorMsg =
function(msg) {
	var msgDialog = appCtxt.getMsgDialog();
	msgDialog.reset();
	msgDialog.setMessage(msg, DwtMessageDialog.CRITICAL_STYLE);
	msgDialog.popup();
};

com_zimbra_rssfeeds.prototype._initializeDlg =
function() {
	if (this.rssFeedDialog) {
		this.rssFeedDialog.popup();
		return;
	}

	this._loadFeeds();
	this._parentView = new DwtComposite(this.getShell());
	this._parentView.setSize("550", "300");
	this._parentView.getHtmlElement().style.overflow = "auto";
	this._parentView.getHtmlElement().innerHTML = this._constructView();
	this.rssFeedDialog = this._createDialog({title:"Subscribe to top RSS feeds", view:this._parentView, standardButtons : [DwtDialog.OK_BUTTON]});
	this._addBtnListeners();
	this.rssFeedDialog.popup();
};

com_zimbra_rssfeeds.prototype._constructView =
function() {
	this._currentCategory = "";
	var html = new Array();
	var i = 0;
	var idCnt = 0;
	this._btnidAndUrl = [];

	for(var el in this.feeds) {

		var domain = this.feeds[el].d;
		var category = this.feeds[el].c;
		var title = this.feeds[el].t;
		var btnId = "rssf_btn"+idCnt;
		this._btnidAndUrl[btnId] = {url:el, foldername:this.feeds[el].fn, category:this.feeds[el].c};
		if(this._currentCategory !=category) {
			html[i++] = "<div class='rssf_HdrDiv'>";
			html[i++] = category;
			html[i++] = "</div>";
			this._currentCategory = category;
		}
		html[i++] = "<div class='rssf_sectionDiv'>";
		html[i++] = "<TABLE  cellpadding=5>";
		html[i++] = "<TR>";
		html[i++] = "<TD width=5%><IMG SRC=\"http://www.google.com/s2/favicons?domain="+domain+"\" WIDTH=\"16\" HEIGHT=\"16\" BORDER=\"0\" ALT=\"\"></TD>";
		html[i++] = "<TD width=80%>";
		html[i++] = "<B>";
		html[i++] =title;
		html[i++] = "</B>";
		html[i++] ="<BR>";
		html[i++] = el;
		html[i++] = "</TD>";
		html[i++] = "<TD width=10%><button id='rssf_btn"+idCnt+"' type=\"button\">Subscribe</button></TD>";
		html[i++] = "</TR>";
		html[i++] = "</TABLE>";
		html[i++] = "</div>";

		idCnt =  idCnt+1;
	}

	return html.join("");
};

com_zimbra_rssfeeds.prototype._addBtnListeners =
function() {
	for(var id in this._btnidAndUrl) {
		document.getElementById(id).onclick = AjxCallback.simpleClosure(this._onSubscribeClick, this, this._btnidAndUrl[id]);
	}
};

com_zimbra_rssfeeds.prototype._onSubscribeClick =
function(params) {
	if(this.mainRssFeedFldrId == undefined){
		this.getMainFolderId(new AjxCallback(this, this._createRSSFolder, params));
	} else{
		this._createRSSFolder(params);
	}
};

com_zimbra_rssfeeds.prototype._createRSSFolder =
function(params) {
	var fldrName =  params.foldername + " - " + params.category;
	var transitions = [ ZmToast.FADE_IN, ZmToast.PAUSE, ZmToast.PAUSE, ZmToast.PAUSE, ZmToast.PAUSE,  ZmToast.FADE_OUT];
	appCtxt.getAppController().setStatusMsg("Creating '"+ com_zimbra_rssfeeds.rssFeedsFolder +" > "+fldrName+"'...", ZmStatusView.LEVEL_INFO, null, transitions);
	var parentFldrId = this.mainRssFeedFldrId ? this.mainRssFeedFldrId : "1";
	var params = {color:null,name:fldrName, url:params.url,  view:com_zimbra_rssfeeds.view, l:parentFldrId};
	this._createFolder(params);
};