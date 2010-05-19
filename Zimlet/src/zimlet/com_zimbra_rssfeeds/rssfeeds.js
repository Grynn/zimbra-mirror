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

function com_zimbra_rssfeeds_HandlerObject() {
}

com_zimbra_rssfeeds_HandlerObject.prototype = new ZmZimletBase();
com_zimbra_rssfeeds_HandlerObject.prototype.constructor = com_zimbra_rssfeeds_HandlerObject;

/**
 * Simplify handler object
 *
 */
var RssFeedsZimlet = com_zimbra_rssfeeds_HandlerObject;

RssFeedsZimlet.VIEW = "message";

/**
 * Defines the "RSS feeds folder" user property.
 */
RssFeedsZimlet.USER_PROPERTY_RSS_FEEDS_FOLDER = "rssFeedsFolder";

/**
 * Defines the "preferences" menu id.
 */
RssFeedsZimlet.MENU_ID_PREFERENCES = "MENU_ID_PREFERENCES";

/**
 * Defines the default RSS feeds folder name.
 */
RssFeedsZimlet.DEFAULT_RSS_FEEDS_FOLDER = "RSS Feeds";

RssFeedsZimlet.prototype._loadFeeds =
function() {
	var topStoriesCat = this.getMessage("RssFeedsZimlet_category_topStories");
	var financeCat = this.getMessage("RssFeedsZimlet_category_finance");
	var technologyCat = this.getMessage("RssFeedsZimlet_category_technology");
	var politicsCat = this.getMessage("RssFeedsZimlet_category_politics");

	//
	// TOP STORIES
	//
	var bbcNewsTitle = this.getMessage("RssFeedsZimlet_topStories_bbcNews_title");
	var bbcNewsFolder = this.getMessage("RssFeedsZimlet_topStories_bbcNews_folder");
	var cnnTitle = this.getMessage("RssFeedsZimlet_topStories_cnn_title");
	var cnnFolder = this.getMessage("RssFeedsZimlet_topStories_cnn_folder");
	var cbsNewsTitle = this.getMessage("RssFeedsZimlet_topStories_cbsNews_title");
	var cbsNewsFolder = this.getMessage("RssFeedsZimlet_topStories_cbsNews_folder");
	var cbcTitle = this.getMessage("RssFeedsZimlet_topStories_cbc_title");
	var cbcFolder = this.getMessage("RssFeedsZimlet_topStories_cbc_folder");
	var consortiumNewsTitle = this.getMessage("RssFeedsZimlet_topStories_consortiumNews_title");
	var consortiumNewsFolder = this.getMessage("RssFeedsZimlet_topStories_consortiumNews_folder");
	var foxNewsTitle = this.getMessage("RssFeedsZimlet_topStories_foxNews_title");
	var foxNewsFolder = this.getMessage("RssFeedsZimlet_topStories_foxNews_folder");
	var googleNewsTitle = this.getMessage("RssFeedsZimlet_topStories_googleNews_title");
	var googleNewsFolder = this.getMessage("RssFeedsZimlet_topStories_googleNews_folder");
	var guardianTitle = this.getMessage("RssFeedsZimlet_topStories_guardian_title");
	var guardianFolder = this.getMessage("RssFeedsZimlet_topStories_guardian_folder");
	var msnbcTitle = this.getMessage("RssFeedsZimlet_topStories_msnbc_title");
	var msnbcFolder = this.getMessage("RssFeedsZimlet_topStories_msnbc_folder");
	var nytTitle = this.getMessage("RssFeedsZimlet_topStories_nyt_title");
	var nytFolder = this.getMessage("RssFeedsZimlet_topStories_nyt_folder");
	var reutersTitle = this.getMessage("RssFeedsZimlet_topStories_reuters_title");
	var reutersFolder = this.getMessage("RssFeedsZimlet_topStories_reuters_folder");
	var timeTitle = this.getMessage("RssFeedsZimlet_topStories_time_title");
	var timeFolder = this.getMessage("RssFeedsZimlet_topStories_time_folder");
	var usatodayTitle = this.getMessage("RssFeedsZimlet_topStories_usatoday_title");
	var usatodayFolder = this.getMessage("RssFeedsZimlet_topStories_usatoday_folder");
	var wsjTitle = this.getMessage("RssFeedsZimlet_topStories_wsj_title");
	var wsjFolder = this.getMessage("RssFeedsZimlet_topStories_wsj_folder");
	var wnTitle = this.getMessage("RssFeedsZimlet_topStories_wn_title");
	var wnFolder = this.getMessage("RssFeedsZimlet_topStories_wn_folder");
	var yahooTitle = this.getMessage("RssFeedsZimlet_topStories_yahoo_title");
	var yahooFolder = this.getMessage("RssFeedsZimlet_topStories_yahoo_folder");

	//
	// FINANCE
	//
	var businessWeekTitle = this.getMessage("RssFeedsZimlet_finance_businessWeek_title");
	var businessWeekFolder = this.getMessage("RssFeedsZimlet_finance_businessWeek_folder");
	var bizjournalsTitle = this.getMessage("RssFeedsZimlet_finance_bizjournals_title");
	var bizjournalsFolder = this.getMessage("RssFeedsZimlet_finance_bizjournals_folder");
	var bankrateTitle = this.getMessage("RssFeedsZimlet_finance_bankrate_title");
	var bankrateFolder = this.getMessage("RssFeedsZimlet_finance_bankrate_folder");
	var cnnmoneyTitle = this.getMessage("RssFeedsZimlet_finance_cnnmoney_title");
	var cnnmoneyFolder = this.getMessage("RssFeedsZimlet_finance_cnnmoney_folder");
	var cnbcTitle = this.getMessage("RssFeedsZimlet_finance_cnbc_title");
	var cnbcFolder = this.getMessage("RssFeedsZimlet_finance_cnbc_folder");
	var entrepreneurTitle = this.getMessage("RssFeedsZimlet_finance_entrepreneur_title");
	var entrepreneurFolder = this.getMessage("RssFeedsZimlet_finance_entrepreneur_folder");
	var ftTitle = this.getMessage("RssFeedsZimlet_finance_ft_title");
	var ftFolder = this.getMessage("RssFeedsZimlet_finance_ft_folder");
	var forbesTitle = this.getMessage("RssFeedsZimlet_finance_forbes_title");
	var forbesFolder = this.getMessage("RssFeedsZimlet_finance_forbes_folder");
	var foolTitle = this.getMessage("RssFeedsZimlet_finance_fool_title");
	var foolFolder = this.getMessage("RssFeedsZimlet_finance_fool_folder");
	var marketWatchTitle = this.getMessage("RssFeedsZimlet_finance_marketWatch_title");
	var marketWatchFolder = this.getMessage("RssFeedsZimlet_finance_marketWatch_folder");
	var msnMoneyTitle = this.getMessage("RssFeedsZimlet_finance_msnMoney_title");
	var msnMoneyFolder = this.getMessage("RssFeedsZimlet_finance_msnMoney_folder");
	var theStreetTitle = this.getMessage("RssFeedsZimlet_finance_theStreet_title");
	var theStreetFolder = this.getMessage("RssFeedsZimlet_finance_theStreet_folder");
	var seekingAlphaTitle = this.getMessage("RssFeedsZimlet_finance_seekingAlpha_title");
	var seekingAlphaFolder = this.getMessage("RssFeedsZimlet_finance_seekingAlpha_folder");
	var yahooNewsBusinessTitle = this.getMessage("RssFeedsZimlet_finance_yahooNewsBusiness_title");
	var yahooNewsBusinessFolder = this.getMessage("RssFeedsZimlet_finance_yahooNewsBusiness_folder");
	var yahooNewsStockTitle = this.getMessage("RssFeedsZimlet_finance_yahooNewsStock_title");
	var yahooNewsStockFolder = this.getMessage("RssFeedsZimlet_finance_yahooNewsStock_folder");
	var yahooNewsEuroBusinessTitle = this.getMessage("RssFeedsZimlet_finance_yahooNewsEuroBusiness_title");
	var yahooNewsEuroBusinessFolder = this.getMessage("RssFeedsZimlet_finance_yahooNewsEuroBusiness_folder");

	//
	// TECHNOLOGY
	//
	var allthingsdTitle = this.getMessage("RssFeedsZimlet_technology_allthingsd_title");
	var allthingsdFolder = this.getMessage("RssFeedsZimlet_technology_allthingsd_folder");
	var crunchGearTitle = this.getMessage("RssFeedsZimlet_technology_crunchGear_title");
	var crunchGearFolder = this.getMessage("RssFeedsZimlet_technology_crunchGear_folder");
	var diggTitle = this.getMessage("RssFeedsZimlet_technology_digg_title");
	var diggFolder = this.getMessage("RssFeedsZimlet_technology_digg_folder");
	var engadgetTitle = this.getMessage("RssFeedsZimlet_technology_engadget_title");
	var engadgetFolder = this.getMessage("RssFeedsZimlet_technology_engadget_folder");
	var gigaomTitle = this.getMessage("RssFeedsZimlet_technology_gigaom_title");
	var gigaomFolder = this.getMessage("RssFeedsZimlet_technology_gigaom_folder");
	var googleTitle = this.getMessage("RssFeedsZimlet_technology_google_title");
	var googleFolder = this.getMessage("RssFeedsZimlet_technology_google_folder");
	var mashableTitle = this.getMessage("RssFeedsZimlet_technology_mashable_title");
	var mashableFolder = this.getMessage("RssFeedsZimlet_technology_mashable_folder");
	var pcworldTitle = this.getMessage("RssFeedsZimlet_technology_pcworld_title");
	var pcworldFolder = this.getMessage("RssFeedsZimlet_technology_pcworld_folder");
	var readwritewebTitle = this.getMessage("RssFeedsZimlet_technology_readwriteweb_title");
	var readwritewebFolder = this.getMessage("RssFeedsZimlet_technology_readwriteweb_folder");
	var siliconAlleyTitle = this.getMessage("RssFeedsZimlet_technology_siliconAlley_title");
	var siliconAlleyFolder = this.getMessage("RssFeedsZimlet_technology_siliconAlley_folder");
	var slashdotTitle = this.getMessage("RssFeedsZimlet_technology_slashdot_title");
	var slashdotFolder = this.getMessage("RssFeedsZimlet_technology_slashdot_folder");
	var techcrunchTitle = this.getMessage("RssFeedsZimlet_technology_techcrunch_title");
	var techcrunchFolder = this.getMessage("RssFeedsZimlet_technology_techcrunch_folder");
	var techRepublicTitle = this.getMessage("RssFeedsZimlet_technology_techRepublic_title");
	var techRepublicFolder = this.getMessage("RssFeedsZimlet_technology_techRepublic_folder");
	var techSpotTitle = this.getMessage("RssFeedsZimlet_technology_techSpot_title");
	var techSpotFolder = this.getMessage("RssFeedsZimlet_technology_techSpot_folder");
	var valleywagTitle = this.getMessage("RssFeedsZimlet_technology_valleywag_title");
	var valleywagFolder = this.getMessage("RssFeedsZimlet_technology_valleywag_folder");
	var ventureBeatTitle = this.getMessage("RssFeedsZimlet_technology_ventureBeat_title");
	var ventureBeatFolder = this.getMessage("RssFeedsZimlet_technology_ventureBeat_folder");
	var wiredTopStoriesTitle = this.getMessage("RssFeedsZimlet_technology_wiredTopStories_title");
	var wiredTopStoriesFolder = this.getMessage("RssFeedsZimlet_technology_wiredTopStories_folder");
	var yodelTitle = this.getMessage("RssFeedsZimlet_technology_yodel_title");
	var yodelFolder = this.getMessage("RssFeedsZimlet_technology_yodel_folder");

	//
	// POLITICS
	//
	var csmTitle = this.getMessage("RssFeedsZimlet_politics_csm_title");
	var csmFolder = this.getMessage("RssFeedsZimlet_politics_csm_folder");
	var crooksTitle = this.getMessage("RssFeedsZimlet_politics_crooks_title");
	var crooksFolder = this.getMessage("RssFeedsZimlet_politics_crooks_folder");
	var dailyKosTitle = this.getMessage("RssFeedsZimlet_politics_dailyKos_title");
	var dailyKosFolder = this.getMessage("RssFeedsZimlet_politics_dailyKos_folder");
	var freeRepublicTitle = this.getMessage("RssFeedsZimlet_politics_freeRepublic_title");
	var freeRepublicFolder = this.getMessage("RssFeedsZimlet_politics_freeRepublic_folder");
	var hotAirTitle = this.getMessage("RssFeedsZimlet_politics_hotAir_title");
	var hotAirFolder = this.getMessage("RssFeedsZimlet_politics_hotAir_folder");
	var thinkProgressTitle = this.getMessage("RssFeedsZimlet_politics_thinkProgress_title");
	var thinkProgressFolder = this.getMessage("RssFeedsZimlet_politics_thinkProgress_folder");
	var huffingtonTitle = this.getMessage("RssFeedsZimlet_politics_huffington_title");
	var huffingtonFolder = this.getMessage("RssFeedsZimlet_politics_huffington_folder");
	var newsmaxTitle = this.getMessage("RssFeedsZimlet_politics_newsmax_title");
	var newsmaxFolder = this.getMessage("RssFeedsZimlet_politics_newsmax_folder");
	var nationalReviewTitle = this.getMessage("RssFeedsZimlet_politics_nationalReview_title");
	var nationalReviewFolder = this.getMessage("RssFeedsZimlet_politics_nationalReview_folder");
	var politicoTitle = this.getMessage("RssFeedsZimlet_politics_politico_title");
	var politicoFolder = this.getMessage("RssFeedsZimlet_politics_politico_folder");
	var rightWingTitle = this.getMessage("RssFeedsZimlet_politics_rightWing_title");
	var rightWingFolder = this.getMessage("RssFeedsZimlet_politics_rightWing_folder");
	var realClearWorldTitle = this.getMessage("RssFeedsZimlet_politics_realClearWorld_title");
	var realClearWorldFolder = this.getMessage("RssFeedsZimlet_politics_realClearWorld_folder");
	var salonTitle = this.getMessage("RssFeedsZimlet_politics_salon_title");
	var salonFolder = this.getMessage("RssFeedsZimlet_politics_salon_folder");
	var theHillTitle = this.getMessage("RssFeedsZimlet_politics_theHill_title");
	var theHillFolder = this.getMessage("RssFeedsZimlet_politics_theHill_folder");
	var worldNetDailyTitle = this.getMessage("RssFeedsZimlet_politics_worldNetDaily_title");
	var worldNetDailyFolder = this.getMessage("RssFeedsZimlet_politics_worldNetDaily_folder");


	this.feeds = [];
	//c = category, d=domain, t=title(in the dialog), fn=(folderName)
	
	//
	// TOP STORIES
	//
	this.feeds["http://newsrss.bbc.co.uk/rss/newsonline_world_edition/front_page/rss.xml"] = {c:topStoriesCat, d:"www.bbc.com", t: bbcNewsTitle, fn:bbcNewsFolder};
	this.feeds["http://rss.cnn.com/rss/cnn_topstories.rss"] = {c:topStoriesCat, d:"www.cnn.com",  t:cnnTitle, fn:cnnFolder};
	this.feeds["http://www.cbsnews.com/feeds/rss/main.rss"] = {c:topStoriesCat, d:"www.cbsnews.com",  t:cbsNewsTitle, fn:cbsNewsFolder};
	this.feeds["http://rss.cbc.ca/lineup/topstories.xml"] = {c:topStoriesCat, d:"www.cbc.ca",  t:cbcTitle, fn:cbcFolder};
	this.feeds["http://feeds2.feedburner.com/Consortiumnewscom"] = {c:topStoriesCat, d:"Consortiumnews.com",  t:consortiumNewsTitle, fn:consortiumNewsFolder};
	this.feeds["http://feeds.foxnews.com/foxnews/latest?format=xml"] = {c:topStoriesCat, d:"www.foxnews.com",  t:foxNewsTitle, fn:foxNewsFolder};
	this.feeds["http://news.google.com/news?pz=1&ned=us&hl=en&output=rss"] = {c:topStoriesCat, d:"www.google.com",  t:googleNewsTitle, fn:googleNewsFolder };
	this.feeds["http://feeds.guardian.co.uk/theguardian/rss"] = {c:topStoriesCat, d:"www.guardian.co.uk",  t:guardianTitle, fn:guardianFolder };
	this.feeds["http://rss.msnbc.msn.com/id/3032091/device/rss/rss.xml"] = {c:topStoriesCat, d:"www.msnbc.com", t:msnbcTitle, fn:msnbcFolder};
	this.feeds["http://www.nytimes.com/services/xml/rss/nyt/HomePage.xml"] = {c:topStoriesCat, d:"www.nytimes.com", t:nytTitle, fn:nytFolder};
	this.feeds["http://feeds.reuters.com/reuters/topNews"] = {c:topStoriesCat, d:"www.reuters.com", t:reutersTitle, fn:reutersFolder};
	this.feeds["http://feeds2.feedburner.com/time/topstories?format=xml"] = {c:topStoriesCat, d:"www.time.com", t: timeTitle, fn:timeFolder};
	this.feeds["http://rssfeeds.usatoday.com/usatoday-NewsTopStories"] = {c:topStoriesCat, d:"www.usatoday.com", t:usatodayTitle, fn:usatodayFolder};	
	this.feeds["http://feeds.wsjonline.com/wsj/xml/rss/3_7011.xml"] = {c:topStoriesCat, d:"www.wsj.com", t:wsjTitle, fn:wsjFolder};
	this.feeds["http://rss.wn.com/English/top-stories"] = {c:topStoriesCat, d:"www.worldnews.com", t:wnTitle, fn:wnFolder};
	this.feeds["http://rss.news.yahoo.com/rss/topstories"] = {c:topStoriesCat, d:"www.yahoo.com", t:yahooTitle, fn:yahooFolder};

	//
	// FINANCE
	//	
	this.feeds["http://rss.businessweek.com/bw_rss/bwdaily"] =  {c:financeCat, d:"www.businessweek.com", t: businessWeekTitle, fn:businessWeekFolder };
 	this.feeds["http://feeds2.feedburner.com/bizj_national"] = {c:financeCat, d:"www.bizjournals.com", t: bizjournalsTitle, fn:bizjournalsFolder};
	this.feeds["http://www.bankrate.com/rss/Bankrate_TopStory_brm_rss.xml"] = {c:financeCat, d:"www.bankrate.com", t: bankrateTitle, fn:bankrateFolder};
	this.feeds["http://rss.cnn.com/rss/money_topstories.rss"] = {c:financeCat, d:"www.cnn.com", t: cnnmoneyTitle, fn:cnnmoneyFolder};
	this.feeds["http://www.cnbc.com/id/19746125/device/rss/rss.xml"] = {c:financeCat, d:"www.cnbc.com", t: cnbcTitle, fn:cnbcFolder};
	this.feeds["http://www.entrepreneur.com/feeds/latest.html"] = {c:financeCat, d:"www.Entrepreneur.com", t: entrepreneurTitle, fn:entrepreneurFolder};
	this.feeds["http://www.ft.com/rss/home/us"] = {c:financeCat, d:"www.ft.com", t: ftTitle, fn:ftFolder};
	this.feeds["http://www.forbes.com/news/index.xml"] = {c:financeCat, d:"www.forbes.com", t: forbesTitle, fn:forbesFolder};
	this.feeds["http://feeds.fool.com/usmf/foolwatch"] = {c:financeCat, d:"www.fool.com", t: foolTitle, fn:foolFolder };
	this.feeds["http://feeds.marketwatch.com/marketwatch/topstories/"] = {c:financeCat, d:"www.marketwatch.com", t: marketWatchTitle, fn:marketWatchFolder };
	this.feeds["http://articles.moneycentral.msn.com/Feeds/RSS/latestrss.aspx"] = {c:financeCat, d:"www.msn.com", t: msnMoneyTitle, fn:msnMoneyFolder};
	this.feeds["http://feeds.thestreet.com/tsc/feeds/rss/top-read-stories"] = {c:financeCat, d:"www.thestreet.com", t: theStreetTitle, fn:theStreetFolder};
	this.feeds["http://seekingalpha.com/feed.xml"] = {c:financeCat, d:"www.SeekingAlpha.com", t: seekingAlphaTitle, fn:seekingAlphaFolder};
	this.feeds["http://rss.news.yahoo.com/rss/business"] = {c:financeCat, d:"www.yahoo.com", t: yahooNewsBusinessTitle, fn:yahooNewsBusinessFolder};
	this.feeds["http://rss.news.yahoo.com/rss/stocks"] = {c:financeCat, d:"www.yahoo.com", t: yahooNewsStockTitle, fn:yahooNewsStockFolder};
	this.feeds["http://rss.news.yahoo.com/rss/eurobiz"] = {c:financeCat, d:"www.yahoo.com", t: yahooNewsEuroBusinessTitle, fn:yahooNewsEuroBusinessFolder};

	//
	// TECHNOLOGY
	//
	this.feeds["http://allthingsd.com/feed/"] =  {c:technologyCat, d:"www.allthingsd.com", t:allthingsdTitle, fn:allthingsdFolder};
	this.feeds["http://feedproxy.google.com/CrunchGear"] =  {c:technologyCat, d:"www.CrunchGear.com", t: crunchGearTitle, fn:crunchGearFolder};
	this.feeds["http://feeds.digg.com/digg/container/technology/popular.rss"] =  {c:technologyCat, d:"www.digg.com", t: diggTitle, fn:diggFolder};
	this.feeds["http://www.engadget.com/rss.xml"] =  {c:technologyCat, d:"www.Engadget.com", t: engadgetTitle, fn:engadgetFolder};
	this.feeds["http://feeds2.feedburner.com/ommalik"] =  {c:technologyCat, d:"www.gigaom.com", t: gigaomTitle, fn:gigaomFolder};
	this.feeds["http://googleblog.blogspot.com/"] =  {c:technologyCat, d:"www.google.com", t: googleTitle, fn:googleFolder};
	this.feeds["http://feeds2.feedburner.com/Mashable"] =  {c:technologyCat, d:"www.mashable.com", t: mashableTitle, fn:mashableFolder};
	this.feeds["http://feeds.pcworld.com/pcworld/latestnews"] =  {c:technologyCat, d:"www.pcworld.com", t: pcworldTitle, fn:pcworldFolder};
	this.feeds["http://feeds2.feedburner.com/readwriteweb"] =  {c:technologyCat, d:"www.ReadWriteWeb.com", t: readwritewebTitle, fn:readwritewebFolder};
	this.feeds["http://feedproxy.google.com/typepad/alleyinsider/silicon_alley_insider"] =  {c:technologyCat, d:"www.businessinsider.com", t: siliconAlleyTitle, fn:siliconAlleyFolder};
	this.feeds["http://rss.slashdot.org/Slashdot/slashdot"] =  {c:technologyCat, d:"www.Slashdot.org", t: slashdotTitle, fn:slashdotFolder};
	this.feeds["http://feedproxy.google.com/TechCrunch"] =  {c:technologyCat, d:"www.techcrunch.com", t: techcrunchTitle, fn:techcrunchFolder};
	this.feeds["http://blogs.techrepublic.com.com/wp-rss2.php"] =  {c:technologyCat, d:"www.techrepublic.com", t: techRepublicTitle, fn:techRepublicFolder};
	this.feeds["http://feedproxy.google.com/techspot/news"] =  {c:technologyCat, d:"www.techspot.com", t: techSpotTitle,  fn:techSpotFolder};
	this.feeds["http://valleywag.gawker.com/tag/valleywag/index.xml"] =  {c:technologyCat, d:"valleywag.gawker.com", t: valleywagTitle,  fn:valleywagFolder};
	this.feeds["http://feeds2.feedburner.com/venturebeat"] =  {c:technologyCat, d:"venturebeat.com", t: ventureBeatTitle,  fn:ventureBeatFolder};
	this.feeds["http://feeds.wired.com/wired/index"] =  {c:technologyCat, d:"www.wired.com", t: wiredTopStoriesTitle,  fn:wiredTopStoriesFolder};
	this.feeds["http://feeds2.feedburner.com/yodelanecdotal"] =  {c:technologyCat, d:"www.yahoo.com", t: yodelTitle,  fn:yodelFolder};

	//
	// POLITICS
	//
	this.feeds["http://rss.csmonitor.com/feeds/top"] =  {c:politicsCat, d:"www.csmonitor.com", t: csmTitle, fn:csmFolder};
	this.feeds["http://feeds2.feedburner.com/crooksandliars/YaCP"] =  {c:politicsCat, d:"www.crooksandliars.com", t: crooksTitle, fn:crooksFolder};
	this.feeds["http://feeds.dailykos.com/dailykos/index.xml"] =  {c:politicsCat, d:"www.dailykos.com", t: dailyKosTitle, fn:dailyKosFolder};
	this.feeds["http://www.freerepublic.com/tag/*/feed.rss"] =  {c:politicsCat, d:"www.freerepublic.com", t: freeRepublicTitle, fn:freeRepublicFolder};
	this.feeds["http://feeds2.feedburner.com/hotair/main"] =  {c:politicsCat, d:"www.hotair.com", t: hotAirTitle, fn:hotAirFolder};
	this.feeds["http://thinkprogress.org/feed/"] =  {c:politicsCat, d:"www.thinkprogress.org", t: thinkProgressTitle, fn:thinkProgressFolder};
	this.feeds["http://feeds.huffingtonpost.com/huffingtonpost/raw_feed"] =  {c:politicsCat, d:"www.huffingtonpost.com", t: huffingtonTitle, fn:huffingtonFolder};
	this.feeds["http://www.newsmax.com/xml/newsfront.xml"] =  {c:politicsCat, d:"www.newsmax.com", t: newsmaxTitle, fn:newsmaxFolder};
	this.feeds["http://www.nationalreview.com/index.xml"] =  {c:politicsCat, d:"www.nationalreview.com", t: nationalReviewTitle, fn:nationalReviewFolder};
	this.feeds["http://www.politico.com/rss/politicopicks.xml"] =  {c:politicsCat, d:"www.politico.com", t: politicoTitle, fn:politicoFolder};
	this.feeds["http://rightwingnuthouse.com/feed/"] =  {c:politicsCat, d:"www.rightwingnuthouse.com", t: rightWingTitle, fn:rightWingFolder};
	this.feeds["http://feeds.feedburner.com/realclearpolitics/qlMj"] =  {c:politicsCat, d:"www.realclearpolitics.com", t: realClearWorldTitle, fn:realClearWorldFolder};
	this.feeds["http://feeds.salon.com/salon/news"] =  {c:politicsCat, d:"www.salon.com", t: salonTitle, fn:salonFolder};
	this.feeds["http://www.thehill.com/index.php?option=com_rd_rss&id=1"] =  {c:politicsCat, d:"www.thehill.com", t: theHillTitle, fn:theHillFolder};	
	this.feeds["http://wnd.com/?ol=0&fa=PAGE.rss"] =  {c:politicsCat, d:"www.wnd.com", t: worldNetDailyTitle, fn:worldNetDailyFolder};
};

/**
 * Gets the RSS feeds folder.
 * 
 * @return	{string}	the folder name
 * 
 * @see		DEFAULT_RSS_FEEDS_FOLDER
 */
RssFeedsZimlet.prototype.getRssFeedsFolderName = 
function() {
	var folderName = this.getUserProperty(RssFeedsZimlet.USER_PROPERTY_RSS_FEEDS_FOLDER);
	
	if (folderName == null || folderName.length <= 0)
		return	DEFAULT_RSS_FEEDS_FOLDER
		
	return	folderName;
};

/**
 * Initializes the main feeds folder.
 * 
 * @param	{AjxCallback}	callback		the callback
 */
RssFeedsZimlet.prototype._initializeMainRSSFeedsFolderId =
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
			if (f && f.name == this.getRssFeedsFolderName() && f.view == RssFeedsZimlet.VIEW) {
				this.mainRssFeedFldrId = f.id;
				break;
			}
		}
	}
	if(this.mainRssFeedFldrId) {
		if (callback)
			callback.run(this);
	} else {
		this._createMainRSSFeedsFolder(callback);	//there is no such folder, so create one.
	}
};

/**
 * Creates the main folder.
 * 
 * @param	{AjxCallback}	postCallback		the callback
 */
RssFeedsZimlet.prototype._createMainRSSFeedsFolder =
function(postCallback) {
	var params = {
			color	: null,
			name	: this.getRssFeedsFolderName(),
			url		: null,
			view	: RssFeedsZimlet.VIEW,
			l		: "1",
			postCallback	: postCallback
		};
	
	this._createFolder(params, postCallback);
};

/**
 * Called by framework on menu item select.
 */
RssFeedsZimlet.prototype.menuItemSelected =
function(itemId) {
	switch (itemId) {
	case RssFeedsZimlet.MENU_ID_PREFERENCES:
		this.createPropertyEditor();
		break;
	}
};

/**
 * Called on double-click.
 */
RssFeedsZimlet.prototype.doubleClicked =
function() {		
	this.singleClicked();
};

/**
 * Called on single-click.
 */
RssFeedsZimlet.prototype.singleClicked =
function() {		
	this._initializeDlg();
};

/**
 * Creates the folder.
 * 
 * @param	{hash}	params		a hash of folder params
 */
RssFeedsZimlet.prototype._createFolder =
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

/**
 * Handles the create folder callback.
 * 
 * @see		_createFolder
 */
RssFeedsZimlet.prototype._createFldrCallback =
function(params, response) {
	if(params.name == this.getRssFeedsFolderName()) {
		this.mainRssFeedFldrId = response.getResponse().CreateFolderResponse.folder[0].id;
		if (params.postCallback){
			params.postCallback.run(this);
		}
	} else {
		var transitions = [ZmToast.FADE_IN, ZmToast.PAUSE, ZmToast.FADE_OUT];
		appCtxt.getAppController().setStatusMsg(this.getMessage("RssFeedsZimlet_successCreateFeed"), ZmStatusView.LEVEL_INFO, null, transitions);
	}
};

/**
 * Handles the create folder error callback.
 * 
 * @see		_createFolder
 */
RssFeedsZimlet.prototype._createFldrErrCallback =
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
		appCtxt.getAppController().setStatusMsg(this.getMessage("RssFeedsZimlet_errorCreateFeed"), ZmStatusView.LEVEL_WARNING, null, transitions);
	if (msg) {
		this._showErrorMsg(msg);
		return true;
	}

	return false;
};

/**
 * Shows an error message.
 * 
 * @param	{string}	msg		the message
 */
RssFeedsZimlet.prototype._showErrorMsg =
function(msg) {
	var msgDialog = appCtxt.getMsgDialog();
	msgDialog.reset();
	msgDialog.setMessage(msg, DwtMessageDialog.CRITICAL_STYLE);
	msgDialog.popup();
};

/**
 * Initializes the subscribe feed dialog.
 * 
 */
RssFeedsZimlet.prototype._initializeDlg =
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

	var dialogArgs = {
			title	: this.getMessage("RssFeedsZimlet_dialog_title"),
			view	: this._parentView,
			parent	:	this.getShell(),
			standardButtons : [DwtDialog.OK_BUTTON]
		};
	
	this.rssFeedDialog = new ZmDialog(dialogArgs);
	this._addBtnListeners();
	this.rssFeedDialog.popup();
};

/**
 * Constructs the view for the subscribe dialog.
 * 
 * @see		_initializeDlg
 */
RssFeedsZimlet.prototype._constructView =
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
		html[i++] = "<TD width=10%><button id='rssf_btn"+idCnt+"' type=\"button\">";
		html[i++] = this.getMessage("RssFeedsZimlet_dialog_subscribeButton");
		html[i++] = "</button></TD>";
		html[i++] = "</TR>";
		html[i++] = "</TABLE>";
		html[i++] = "</div>";

		idCnt =  idCnt+1;
	}

	return html.join("");
};

/**
 * Adds the button listeners.
 * 
 * @see		_initializeDlg
 */
RssFeedsZimlet.prototype._addBtnListeners =
function() {
	for(var id in this._btnidAndUrl) {
		document.getElementById(id).onclick = AjxCallback.simpleClosure(this._onSubscribeClick, this, this._btnidAndUrl[id]);
	}
};

/**
 * Handles the subscribe button event.
 * 
 * @param	{hash}	params		a hash of parameters
 * 
 * @see		_initializeDlg
 */
RssFeedsZimlet.prototype._onSubscribeClick =
function(params) {
	if(this.mainRssFeedFldrId == undefined){
		this._initializeMainRSSFeedsFolderId(new AjxCallback(this, this._createRSSFolder, params));
	} else{
		this._createRSSFolder(params);
	}
};

/**
 * Creates the RSS feed folder.
 * 
 * @param	{hash}	params		a hash of parameters
 */
RssFeedsZimlet.prototype._createRSSFolder =
function(params) {
	var fldrName =  params.foldername + " - " + params.category;
	var transitions = [ ZmToast.FADE_IN, ZmToast.PAUSE, ZmToast.PAUSE, ZmToast.PAUSE, ZmToast.PAUSE,  ZmToast.FADE_OUT];

	var statusMsg = AjxMessageFormat.format(this.getMessage("RssFeedsZimlet_creatingFeed"), [this.getRssFeedsFolderName(), fldrName]);
	
	appCtxt.getAppController().setStatusMsg(statusMsg, ZmStatusView.LEVEL_INFO, null, transitions);

	var parentFldrId = this.mainRssFeedFldrId ? this.mainRssFeedFldrId : "1";
	var params = {color:null,name:fldrName, url:params.url,  view:RssFeedsZimlet.VIEW, l:parentFldrId};
	
	this._createFolder(params);
};

