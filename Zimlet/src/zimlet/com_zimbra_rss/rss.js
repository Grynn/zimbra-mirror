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
 * Contributor(s):
 * 
 * ***** END LICENSE BLOCK *****
 */

//////////////////////////////////////////////////////////////
//  RSS Zimlet                                              //
//  @author Raja Rao DV                                     //
//////////////////////////////////////////////////////////////

function Com_Zimbra_RSS() {
}
Com_Zimbra_RSS.prototype = new ZmZimletBase();
Com_Zimbra_RSS.prototype.constructor = Com_Zimbra_RSS;

Com_Zimbra_RSS.prototype.init = function() {
    this._visible = false;
    this._miniCal = this._appCtxt.getApp(ZmApp.CALENDAR).getCalController().getMiniCalendar().getHtmlElement();
};

Com_Zimbra_RSS.FEED_CACHE = {};
var count;
var ticker;
var rssURL;
var selectedDiv = 0;
var totalDivs = 0;
var timeOutID;

Com_Zimbra_RSS.prototype.doubleClicked = function() {
    this.singleClicked();
};

Com_Zimbra_RSS.prototype.singleClicked = function() {
    this._startRSSFeed();
};

Com_Zimbra_RSS.prototype._startRSSFeed = function() {
    this._visible = !this._visible;
    var feedNoToUse = this.getUserProperty("defaultRSSUrl");
    if (feedNoToUse && feedNoToUse < 5) {
        rssURL = this.getUserProperty("Feed" + feedNoToUse);
    } else {
        rssURL = "http://rss.news.yahoo.com/rss/tech";
    }
    var minicalDIV = document.getElementById("skin_container_tree_footer");

    if (this._visible)
    {
        if (!document.getElementById("RSS_DIV")) {
            var newDiv = document.createElement("div");
            var newdivID = "RSS_DIV";
            newDiv.style.position = "absolute";
            newDiv.style.width = 163;
            newDiv.style.height = 152;
            newDiv.id = newdivID;
            newDiv.style.zIndex = 900;
            var picID = Dwt.getNextId();

            newDiv.style.backgroundColor = "white";
            if (feedNoToUse && feedNoToUse < 5) {
                newDiv.innerHTML = "<dev class=\"loading\" top:75px >loading feed#" + feedNoToUse + " ..</div>";
            } else {
                newDiv.innerHTML = "<dev class=\"loading\" top:75px >loading default feed ..</div>";
            }
            minicalDIV.appendChild(newDiv);
        }
        // temporarily hide the mini calendar
        this._miniCal.style.visibility = "hidden";
        this.getRSS();

    } else {
        clearTimeout(timeOutID);
        //without cancelling timeOutID..the zimlet becomes crazy
        //clearInterval(intervalID);
        minicalDIV.innerHTML = "";
        totalDivs = 0;
        // show the mini calendar once again
        this._miniCal.style.visibility = "visible";
    }
};

Com_Zimbra_RSS.prototype.getRSS = function() {
    //cleanup all the previous cached info like timeout, timeinterval etc before proceeding
    clearTimeout(timeOutID);
    totalDivs = 0;
    if (Com_Zimbra_RSS.FEED_CACHE[rssURL]) {
        Com_Zimbra_RSS.formatFeed({0:rssURL,1:this}, Com_Zimbra_RSS.FEED_CACHE[rssURL]);
    } else {
        var url = ZmZimletBase.PROXY + AjxStringUtil.urlComponentEncode(rssURL);
        AjxRpc.invoke(null, url, null, new AjxCallback(this, Com_Zimbra_RSS.formatFeed, {0:rssURL,1:this}), true);
    }
};

Com_Zimbra_RSS.formatFeed = function(args, result) {
    rssURL = args[0];
    self = args[1];
    if (result && result.xml) {
        var xmldata = result.xml;
    } else {
        this.displayErrorMessage("Unable to load RSS Feed: " + rssURL);
        self._miniCal.style.visibility = "visible";
        return;
    }

    if (!Com_Zimbra_RSS.FEED_CACHE[rssURL]) {
        Com_Zimbra_RSS.FEED_CACHE[rssURL] = result;
    }

    var feeditems = xmldata.getElementsByTagName("item");
    var tempStr;
    var innerhtmlStr = "";
    //Cycle through RSS XML object and store each peice of an item inside a corresponding array
    for (var i = 0; i < feeditems.length; i++) {
        var desc = "";
        var hrf = "";
        var title = "";
        var anch = "";
        var publishedDate = "";
        var wordsArry = "";
        var str;
        var img;
        var str1,desc1;
        try {
            if ((feeditems[i].getElementsByTagName("description") ).constructor.toString().indexOf("function Object") >= 0) {
                if (feeditems[i].getElementsByTagName("description")[0].firstChild) {
                    desc = feeditems[i].getElementsByTagName("description")[0].firstChild.nodeValue;
                } else if (feeditems[i].getElementsByTagName("description").nodeValue) {
                    desc = feeditems[i].getElementsByTagName("description").nodeValue;
                }
            }
        }
        catch(e) {
            desc = "";
        }
        try {
            if ((feeditems[i].getElementsByTagName("link")).constructor.toString().indexOf("function Object") >= 0) {
                if (feeditems[i].getElementsByTagName("link")[0].firstChild) {
                    hrf = feeditems[i].getElementsByTagName("link")[0].firstChild.nodeValue;
                } else if (feeditems[i].getElementsByTagName("link").nodeValue) {
                    hrf = feeditems[i].getElementsByTagName("link").nodeValue;
                }
            }
        } catch(e) {
            hrf = "";
        }
        try {
            if ((feeditems[i].getElementsByTagName("title")).constructor.toString().indexOf("function Object") >= 0) {
                if (feeditems[i].getElementsByTagName("title")[0].firstChild) {
                    title = feeditems[i].getElementsByTagName("title")[0].firstChild.nodeValue;
                } else if (feeditems[i].getElementsByTagName("title").nodeValue) {
                    title = feeditems[i].getElementsByTagName("title").nodeValue;
                }
            }
        } catch(e) {
            title = "";
        }
        try {
            if ((feeditems[i].getElementsByTagName("pubDate")).constructor.toString().indexOf("function Object") >= 0) {
                if (feeditems[i].getElementsByTagName("pubDate")[0].firstChild) {
                    publishedDate = feeditems[i].getElementsByTagName("pubDate")[0].firstChild.nodeValue;
                } else if (feeditems[i].getElementsByTagName("pubDate").nodeValue) {
                    publishedDate = feeditems[i].getElementsByTagName("pubDate").nodeValue;
                }
            }
        } catch(e) {
            publishedDate = "";
        }

        //remove links and images
        if (desc) {
            while (desc.indexOf("<img") >= 0) {
                if (desc.indexOf("</img>") >= 0) {
                    img = desc.substring(desc.indexOf("<img scr=") + 10, desc.indexOf("</img>")).split(" ")[0];
                    desc = desc.replace(desc.substring(desc.indexOf("<img"), desc.indexOf("</img>") + 6), "");
                } else {
                    img = desc.substring(desc.indexOf("<img scr=") + 10, desc.indexOf("/>")).split(" ")[0];
                    desc = desc.replace(desc.substring(desc.indexOf("<img"), desc.indexOf("/>") + 2), "");
                }
            }

            while (desc.indexOf("<a") >= 0) {
                if (desc.indexOf("</a>") >= 0) {
                    desc = desc.replace(desc.substring(desc.indexOf("<a"), desc.indexOf("</a>") + 4), "");
                } else {
                    desc = desc.replace(desc.substring(desc.indexOf("<a"), desc.indexOf("/>") + 2), "");
                }
            }

            //remove para
            while (desc.indexOf("<p>") >= 0) {
                desc = desc.replace("<p>", "");
                desc = desc.replace("</p>", "");
                desc = desc.replace("<P>", "");
                desc = desc.replace("</P>", "");
            }

            //take only upto 15 words of the desc
            wordsArry = desc.split(" ");
            if (wordsArry.length > 15)
            {
                str = wordsArry[0];
                for (j = 1; j <= 15; j++) {
                    str = str + " " + wordsArry[j];
                }
                desc = str + "...";
            }
        } else {
            desc = "";
        }

        tempStr = ["<div id=\"dropmsg" + i + "\" class=\"dropcontent\">",
                "<div class=\"rsstitle\" ><u><A onclick=\"(Com_Zimbra_RSS.openNewWindow('" + hrf + "'))\">" + title + "</A></u></div>",
                "<div class=\"rssdate\" >" + publishedDate + "</div>",
                "<div class=\"rssdescription\">" + desc + "</div>",
                "</div>"].join("");
        innerhtmlStr = innerhtmlStr + tempStr;
        tempStr = "";
        desc = "";
        hrf = "";
    }
    document.getElementById("RSS_DIV").innerHTML = innerhtmlStr;
    Com_Zimbra_RSS.startscroller();
};

Com_Zimbra_RSS.openNewWindow = function(url) {
    window.open(url, 'open_window');
};

Com_Zimbra_RSS.prototype.menuItemSelected = function(itemId) {
    switch (itemId) {
        case "PREFERENCES":
            this.createPropertyEditor();
            break;
    }
};

/***********************************************
 * ProHTML Ticker script- © Dynamic Drive (www.dynamicdrive.com)
 * This notice must stay intact for use
 * Visit http://www.dynamicdrive.com/ for full source code
 * Code modified/updated by: Raja Rao(Zimbra)
 ***********************************************/

var tickspeed = 4000;
var enablesubject = 0;

Com_Zimbra_RSS.contractall = function () {
    var inc = 0;
    while (document.getElementById("dropmsg" + inc) !== null) {
        document.getElementById("dropmsg" + inc).style.display = "none";
        inc++;
    }
};

Com_Zimbra_RSS.expandone = function () {
    var selectedDivObj = document.getElementById("dropmsg" + selectedDiv);
    Com_Zimbra_RSS.contractall();
    selectedDivObj.style.display = "block";

    if ((selectedDiv < totalDivs - 1)) {
        selectedDiv = selectedDiv + 1;
        timeOutID = setTimeout(Com_Zimbra_RSS.expandone, tickspeed);
    } else {
        clearTimeout(timeOutID);
        //without cancelling timeOutID..the zimlet becomes crazy
        selectedDiv = 0;
        Com_Zimbra_RSS.expandone();
    }
};

Com_Zimbra_RSS.startscroller = function () {
    while (document.getElementById("dropmsg" + totalDivs)) {
        totalDivs++;
    }
    Com_Zimbra_RSS.expandone();
};