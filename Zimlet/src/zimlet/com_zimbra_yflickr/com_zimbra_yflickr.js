// The Yahoo Flickr Zimlet (CZ,KK,PJ,MP)

// Constants used by this Zimlet

// Endpoints exposed by Flickr
var gRestEndpoint = "http://api.flickr.com/services/rest/";
var gAuthzEndpoint = "http://flickr.com/services/auth/";

// (hard coded) Flick API key and Secret
var YFLICKR_APIKEY = "2f8c5324f526d933cd25b8cfd1ec4192";
var YFLICKR_APISECRET = "d199b8f1251101ce";

// Stages of Flickr authentication
var FLICKR_AUTHSTAGE_NONE = 1;
var FLICKR_AUTHSTAGE_VERIFIED = 2;
var FLICKR_AUTHSTAGE_GOTFROB = 3;
var FLICKR_AUTHSTAGE_AUTHORIZED = 4;
var FLICKR_AUTHSTAGE_GOTTOKEN = 5;
var FLICKR_AUTHSTAGE_GOTPHOTOSETS = 6;
var FLICKR_AUTHSTAGE_GOTTAGS = 7;

/* Key codes */
var YFLICKR_KC_UP = 38;
var YFLICKR_KC_DOWN = 40;
var YFLICKR_KC_ENTER = 13;
var YFLICKR_KC_ESC = 27;
var YFLICKR_KC_BKSP = 8;
var YFLICKR_KC_TAB = 9;

// Constants related to photo management

// Fetch 10 photos per api call (increase or decrease this value depending upon bandwidth constraints)
var FLICKR_PHOTOSPERPAGE = 10;

// Constants related to photo display
var FLICKRDISP_COLUMNSPERSLIDE = 4;
var FLICKRDISP_ROWSPERSLIDE = 2;
var FLICKRDISP_PHOTOSPERSLIDE = FLICKRDISP_COLUMNSPERSLIDE * FLICKRDISP_ROWSPERSLIDE;

// Other constants used by this program
var YFLICKR_BUSYIMGURL = "img/animated/Imgwait_32.gif";

/* YFlickr zimlet object */
function Com_Zimbra_Yflickr()
{
    this.api_key = YFLICKR_APIKEY;              // API key (can be chosen roundrobin from a list
    this.api_secret = YFLICKR_APISECRET;        // API secret
    this.authStage = FLICKR_AUTHSTAGE_NONE;     // Indicates Authentication Progress
    this.frob = null;                           // Flickr Frob
    this.token = null;                          // Flickr Auth Token
    this.yphotosets = new Object();             // YFlickrPhotoset objects (::_none, or ::set${setid})
    this.attemptToken = false;                  // Do *NOT* Opportunistically attempt to fetch a flickr token
    this.tags = [];                             // Flickr tags defined by the user
    this.tagnames = [];                         // Flickr tag names defined by the user

    // keyword maps for rapid searches
    this.kwmap = new Object ();
    this.kwmap.notinset = new Object ();
    this.kwmap.set = new Object ();
    this.kwmap.tag = new Object ();

    // suggestions for search
    this.suggestedSearches = [];

    // a window object that will indicate the browser window opened for authorization
    this.authWin = null;

    /* Various dialog boxes pre-created for convenience */
    this.authDlg = null;
    this.uploadDlg = null;

    /* data members related to flickr photo management
     */

    /* view state management (for attach files dialog) */
    this.viewstate_div = null;          /* view state (photoset selection, pagination, etc) */

    /* (attachment state) variables */
    this.attach_current = -1;           // current img being attached
    this.attach_photos = [];            // list of <img>s to be attached
}

Com_Zimbra_Yflickr.prototype = new ZmZimletBase();
Com_Zimbra_Yflickr.prototype.constructor = Com_Zimbra_Yflickr;

// initializer function (automatically called by zimlet framework)
Com_Zimbra_Yflickr.prototype.init = function()
{
    // connect to flickr, verify the api_key, get a frob
    this.connect ();

    // add a property page to the `attach files' dialog
    this.addFlickrTabToAttachDialog ();

    // add 'Save to Flickr' link
    this.addAttachmentHandler ();

    // set up the flickr authorization required dialog
    this.authDlg = new DwtDialog (appCtxt.getShell(),null,"Flickr Authorization Required",[DwtDialog.OK_BUTTON]);
    this.authDlg.setContent ("<span style=\"text-align:center;\">" +
                "A new browser window has been created for you to authorize the Flickr zimlet to access your photo albums." +
                "<br/>" +
                "Please log in using your Yahoo!/Flickr account, complete the authorization process, and then click OK to proceed" +
                "<br/>" +
                "</span>"
                );

    this.uploadDlg = new DwtDialog (appCtxt.getShell(),null,"Upload Photo(s) to Flickr",[DwtDialog.OK_BUTTON,DwtDialog.CANCEL_BUTTON]);

    // assign self to window object because we need to execute some code in window context
    window.YFlickr_widget = this;
};

/* Utility functions for debugging */
Com_Zimbra_Yflickr.prototype.debug = function(msg) {
    DBG.println ("[yflickr] " + msg);
}

Com_Zimbra_Yflickr.prototype.info = function(msg) {
    this.displayStatusMessage (msg);
    this.debug (msg);
}

/* Routines related to manipulating photosets */

Com_Zimbra_Yflickr.prototype.addPhotoset = function (ps)
{
    // first assign a the set by id, to yphotosets (this is the original)
    eval ("this.yphotosets." + ps.getId() + " = ps");

    // also build keyword maps to be able to access quickly during search
    var iby = YFlickr_getIndexableName (ps.getKeywordBy());
    var iterm = YFlickr_getIndexableName (ps.getKeywordTerm());

    // attach the photoset to the keyword-map for faster access
    eval ("this.kwmap." + iby + "." + iterm + " = " + "\"" + ps.getId() + "\"");

    // append the display name to the suggestion list 
    this.suggestedSearches.push (ps.getDisplayName());
    this.suggestedSearches.sort ();             // XXX: how to avoid doing this all the time ?
}

/* Suggest a search term (autocompletion)
   @input       the text (manually) typed in by the user
   @current     the text that is currently in the search control
 */
Com_Zimbra_Yflickr.prototype.suggestSearch = function(input, current)
{
    var idx = this.suggestedSearches.indexOf (current);
    var suggestion = "";

    if (idx == -1) {
        // The currently present text does not exist in the search
        var cidx = -1;
        for (var i = 0; i < this.suggestedSearches.length; i++) {
            if (this.suggestedSearches[i].indexOf (current) != -1) {
                cidx = i;
                suggestion = this.suggestedSearches[i];
                break;
            }
        }
        if (cidx == -1) {
            // no suggestion
            suggestion = "";
        }
    } else {
        // the currently typed in text matches a search
        // so move on to the next one if possible, else return the original text

        if (idx == (this.suggestedSearches.length -1)) {
            suggestion = input;
        } else {
            suggestion = this.suggestedSearches[idx+1];
        }
    }

    return suggestion;
}

Com_Zimbra_Yflickr.prototype.hasPhotoset = function (ps)
{
    var x = eval ("this.yphotosets." + ps.getId());
    if (x != null) { return true; }
    else { return false; }
}

// Return all photosets as an array
Com_Zimbra_Yflickr.prototype.getPhotosets = function ()
{
    var s = []
    for (var t in this.yphotosets) {
        s.push (eval("this.yphotosets." + t));
    }
    return s;
}

/* Get a photoset by set|tag|id */
Com_Zimbra_Yflickr.prototype.getPhotosetBy = function(by, term)
{
    if (by == "id") { return eval ("this.yphotosets." + term); }
    else if (by == "set") {
        var id = eval ("this.kwmap.set." + YFlickr_getIndexableName(term));
        if (id) {
            return this.getPhotosetBy ("id", id);
        } else {
            return null;
        }
    } else if (by == "tag") {
        var id = eval ("this.kwmap.tag." + YFlickr_getIndexableName(term));
        if (id) {
            return this.getPhotosetBy ("id", id);
        } else {
            return null;
        }
    } else if (by == "notinset") { 
        var id = this.kwmap.notinset;
        if (id) {
            return this.getPhotosetBy ("id", id);
        } else {
            return null;
        }
    }
}

// connect to flickr and authenticate
Com_Zimbra_Yflickr.prototype.connect = function(auth_stage, result)
{
    if (!auth_stage) { 
        auth_stage = FLICKR_AUTHSTAGE_NONE;
    }

    if (auth_stage == FLICKR_AUTHSTAGE_NONE)
    {
        // verify the api key

        this.debug ("auth stage: FLICKR_AUTHSTAGE_NONE");
        this.debug("verifying api_key using api flickr.test.echo"); 
        this.debug("verifying api_key " + this.api_key);

        var callback = new AjxCallback (this, this.connect, [FLICKR_AUTHSTAGE_VERIFIED]);
        var url = flickrapi_getunsignedurl(gRestEndpoint, [["api_key",this.api_key],["method","flickr.test.echo"],["x","y"]]);

        var headers = null;
        var useGet = true;
        var passErrors = true;

        this.debug("url=" + url);

        this.sendRequest(null, url, headers, callback, useGet, passErrors);
        this.debug ("done auth stage: FLICKR_AUTHSTAGE_NONE");

        this.authStage = FLICKR_AUTHSTAGE_NONE;
    }
    else if (auth_stage == FLICKR_AUTHSTAGE_VERIFIED)
    {
        // this function has been invoked as a callback
        this.debug ("auth stage: FLICKR_AUTHSTAGE_VERIFIED");
        this.debug ("flickr api status: " + result.success);
        this.debug ("<xmp>" + result.text + "</xmp>");

        var xmlo = new AjxXmlDoc.createFromXml(result.text);
        var jso = xmlo.toJSObject (false,false,true);
        var rsp_stat = flickrapi_responsestatus (xmlo);

        this.debug ("flickr response status: " + rsp_stat);

        if (rsp_stat == "ok")
        {
            // api key has been verified, now we need a frob
            this.debug ("flickr credentials verified");
            this.debug ("api_key verified");
            this.debug ("getting frob using api flickr.auth.getFrob");

            var url = flickrapi_getsignedurl (gRestEndpoint, this.api_secret, [["api_key", this.api_key], ["method", "flickr.auth.getFrob"]]);
            var callback = new AjxCallback (this, this.connect, [FLICKR_AUTHSTAGE_GOTFROB]);

            this.debug ("url=" + url);
            this.sendRequest (null, url, null, callback, true, true);

            this.authStage = FLICKR_AUTHSTAGE_VERIFIED;

        } else
        {
            this.info ("Flickr API Key/Secret is invalid !" + flickrapi_geterrmsg (jso));
            this.authStage = FLICKR_AUTHSTAGE_NONE;
        }

        this.debug ("done auth stage: FLICKR_AUTHSTAGE_VERIFIED");
    }
    else if (auth_stage == FLICKR_AUTHSTAGE_GOTFROB)
    {
        // check if we have got the frob as we expect
        this.debug ("auth stage: FLICKR_AUTHSTAGE_GOTFROB");

        this.debug ("flickr api status: " + result.success);
        this.debug ("<xmp>" + result.text + "</xmp>");

        var xmlo = new AjxXmlDoc.createFromXml(result.text);
        var jso = xmlo.toJSObject (false,false,true);
        var rsp_stat = flickrapi_responsestatus (xmlo);

        if (rsp_stat == "ok")
        {
            this.debug ("Got frob " + jso.frob);
            this.authStage = FLICKR_AUTHSTAGE_GOTFROB;
            this.frob = jso.frob;

            // If the user is already logged in to Y!/Flickr, then we may
            // be able to get an authtoken without popping up the auth box
            // This is a window of opportunity that we must exploit

            if (this.attemptToken == true)
            {
                var authz_url = flickrapi_getsignedurl (
                    gAuthzEndpoint, this.api_secret, 
                    [["api_key", this.api_key],
                     ["perms", "write"], 
                     ["frob", this.frob]]);
                window.oppwin = window.open(authz_url,
                    "oppwin",
                    "toolbar=no,menubar=no,width=0.1px,height=0.1px");
                window.yflickrzimlet = this;
                if (window.oppwin == null) {
                    this.attemptToken = false;
                    this.connect (FLICKR_AUTHSTAGE_NONE, null);
                } else {
                    // setTimeout ("oppwin.close();delete oppwin;yflickrzimlet.connect(FLICKR_AUTHSTAGE_AUTHORIZED,null);delete yflickrzimlet;", 10000);
                    setTimeout ("yflickrzimlet.connect(FLICKR_AUTHSTAGE_AUTHORIZED,null);delete yflickrzimlet;", 10000);
                }
            }
            else
            {
                /* there isn't anything we can do until the user clicks `authorize' */
            }
        }
        else
        {
            this.info ("Cannot get Flickr Frob" + flickrapi_geterrmsg(jso));
            this.frob = null;

            this.authStage = FLICKR_AUTHSTAGE_VERIFIED;
        }

        this.debug ("done auth stage: FLICKR_AUTHSTAGE_GOTFROB");
    }
    else if (auth_stage == FLICKR_AUTHSTAGE_AUTHORIZED)
    {
        /* There are two distinct flows by which we can land here
           One is when we opportunistically attempt to acquire an auth-token,
           and one is when the user explicitly follows the process of auth
           and allows the flickr zimlet to access his/her photos

           In the former case, attemptToken is true, and in the latter, it is
           false
         */

        this.debug ("auth stage: FLICKR_AUTHSTAGE_AUTHORIZED");

        var url = flickrapi_getsignedurl (gRestEndpoint, this.api_secret,
                [["api_key", this.api_key],
                 ["frob", this.frob],
                 ["method", "flickr.auth.getToken"]
                ]);
        var callback = new AjxCallback (this, this.connect, [FLICKR_AUTHSTAGE_GOTTOKEN]);

        this.debug ("url=" + url);
        this.sendRequest (null, url, null, callback, true, true);

        this.debug ("done auth stage: FLICKR_AUTHSTAGE_AUTHORIZED");
    }
    else if (auth_stage == FLICKR_AUTHSTAGE_GOTTOKEN)
    {
        // check if we have got the frob as we expect
        this.debug ("auth stage: FLICKR_AUTHSTAGE_GOTTOKEN");

        this.debug ("flickr api status: " + result.success);
        this.debug ("<xmp>" + result.text + "</xmp>");

        var xmlo = new AjxXmlDoc.createFromXml(result.text);
        var jso = xmlo.toJSObject (false,false,true);
        var rsp_stat = flickrapi_responsestatus (xmlo);

        if (rsp_stat == "ok")
        {
            this.debug ("got auth token");
            this.authStage = FLICKR_AUTHSTAGE_GOTTOKEN;

            // flickr.auth.getToken returns an auth node
            this.token = jso.auth;
            this.debug ("got flickr token " + this.token.token + " with " + this.token.perms + " permissions for user " + this.token.user.username);

            // Get Flickr Photosets
            var url = flickrapi_getsignedurl (gRestEndpoint, this.api_secret, [["api_key", this.api_key], ["method", "flickr.photosets.getList"],["user_id",this.token.user.nsid],["auth_token",this.token.token]]);
            var callback = new AjxCallback (this, this.connect, [FLICKR_AUTHSTAGE_GOTPHOTOSETS]);

            this.debug ("url=" + url);
            this.sendRequest (null, url, null, callback, true, true);
        }
        else
        {
            this.info ("Not authorized to access User Photos" + flickrapi_geterrmsg(jso));

            /* There are two cases in which we can land here
               One is that we opportunistically attempted to authorize the zimlet,
               and the second is if the user genuinely changed his/her mind about
               authentication -- in the former case, we need another attempt at the
               auth-token
             */
            if (this.attemptToken == true) {
                this.attemptToken = false;
                this.connect (FLICKR_AUTHSTAGE_NONE, null);
            } 
            else {
                // we can't reuse a frob, so we need to re-wind the authstage to verified
                this.authStage = FLICKR_AUTHSTAGE_VERIFIED;
            }

            this.token = null;
        }

        this.debug ("done auth stage: FLICKR_AUTHSTAGE_GOTTOKEN");
    }
    else if (auth_stage == FLICKR_AUTHSTAGE_GOTPHOTOSETS)
    {
        this.debug ("auth stage: FLICKR_AUTHSTAGE_GOTPHOTOSETS");

        this.debug ("flickr api status: " + result.success);
        this.debug ("<xmp>" + result.text + "</xmp>");

        var xmlo = new AjxXmlDoc.createFromXml(result.text);
        var jso = xmlo.toJSObject (false,false,true);
        var rsp_stat = flickrapi_responsestatus (xmlo);

        /* Create a special photoset to hold those photos not in any set */
        var noneset = new YFlickrUnnamedPhotoset ();
        this.addPhotoset (noneset);

        this.FTV.setSelectedPhotoset (noneset);

        if (rsp_stat == "ok")
        {
            // store the photosets
            this.debug ("got flickr photosets");

            if (jso.photosets.photoset)
            {
                if (jso.photosets.photoset.length)
                {
                    for (var i = 0; i < jso.photosets.photoset.length; i++)
                    {
                        var pset = jso.photosets.photoset[i];
                        var ps = new YFlickrNamedPhotoset (pset.id, pset.title);
                        this.addPhotoset (ps);
                    }
                }
                else
                {
                    var pset = jso.photosets.photoset;
                    var ps = new YFlickrNamedPhotoset (pset.id, pset.title);
                    this.addPhotoset (ps);
                }
            }

            /* Fetch tags */
            var url = flickrapi_getsignedurl (gRestEndpoint, this.api_secret, 
                        [["api_key", this.api_key],
                         ["method", "flickr.tags.getListUser"],
                         ["user_id",this.token.user.nsid],
                         ["auth_token",this.token.token]]);

            var callback = new AjxCallback (this, this.connect, [FLICKR_AUTHSTAGE_GOTTAGS]);
            this.debug ("url=" + url);
            this.sendRequest (null, url, null, callback, true, true);

        }
        else
        {
            // could not get photosets
            this.info ("Could not get flickr photosets" + flickrapi_geterrmsg (jso));
            // this.photosets = [];
        }

        this.debug ("done auth stage: FLICKR_AUTHSTAGE_GOTPHOTOSETS");
    }
    else if (auth_stage == FLICKR_AUTHSTAGE_GOTTAGS)
    {
        this.debug ("auth stage: FLICKR_AUTHSTAGE_GOTTAGS");

        this.debug ("flickr api status: " + result.success);
        this.debug ("<xmp>" + result.text + "</xmp>");

        var xmlo = new AjxXmlDoc.createFromXml(result.text);
        var jso = xmlo.toJSObject (false,false,true);
        var rsp_stat = flickrapi_responsestatus (xmlo);

        if (rsp_stat == "ok")
        {
            this.tags = jso;
            this.tagnames = [];

            if ((jso.who) && (jso.who.tags) && (jso.who.tags.tag))
            {
                if (jso.who.tags.tag.length)
                {
                    /* more than one tag */
                    for (var i = 0; i < jso.who.tags.tag.length; i++)
                    {
                        this.tagnames.push (jso.who.tags.tag[i].toString());
                    }
                }
                else
                {
                    /* just one solitary tag */
                    this.tagnames.push (jso.who.tags.tag.toString());
                }
            }

            this.tagnames.sort();
            for (var t = 0; t < this.tagnames.length; t++) {
                var taggedps = new YFlickrTaggedPhotoset (this.tagnames[t]);
                this.addPhotoset (taggedps);
            }
        }
        else
        {
            this.tags = [];
            this.tagnames = [];
        }

        this.debug ("done auth stage: FLICKR_AUTHSTAGE_GOTTAGS");
    }
}

// set up a window for the user to authorize the frob
Com_Zimbra_Yflickr.prototype.authorize = function()
{
    if (this.authStage < FLICKR_AUTHSTAGE_GOTFROB)
    {
        // TODO: be more verbose in showing this error
        this.debug ("cannot proceed to authorization without frob");

        // var dlg = appCtxt.getMsgDialog ();
        var dlg = new DwtDialog (appCtxt.getShell(), null, "Cannot authorize", [DwtDialog.OK_BUTTON]);
        dlg.setContent ("No frob has yet been issued for authorization. This means that either the api_key and/or secret is invalid, or that the flickr.com website cannot be accessed. Please correct your configuration and try again");
        dlg.popup();
    }
    else if (this.authStage > FLICKR_AUTHSTAGE_AUTHORIZED) {
        var dlg = new DwtDialog (appCtxt.getShell(), null, "Already authorized", [DwtDialog.OK_BUTTON]);
        dlg.setContent ("The Flickr Zimlet is already authorized to access your Flickr Account");
        dlg.popup();
    }
    else
    {
        // construct an authorization url and direct a new window there
        var authz_url = flickrapi_getsignedurl (gAuthzEndpoint, this.api_secret, [["api_key", this.api_key], ["perms", "write"], ["frob", this.frob]]);
        this.authWin = window.open (authz_url, "yflickr_authz", "toolbar=no,menubar=no,width=800,height=600");

        if (this.authDlg)
        {
            var listener = new AjxListener (this, this.get_token);
            this.authDlg.setButtonListener (DwtDialog.OK_BUTTON, listener);
            this.authDlg.popup();
        }

        this.debug ("opened flickr authorization window");
    }
}

// this function is (should be) called after the user has finished the manual process of authorizing the flickr zimlet to connect
Com_Zimbra_Yflickr.prototype.get_token = function()
{
    if (this.authDlg && this.authDlg.isPoppedUp()) {
        this.authDlg.popdown();
        this.connect (FLICKR_AUTHSTAGE_AUTHORIZED);
    }
}

/*  Recursively Fetch (and build) the photos of a given photoset
    @yphotoset       The yflickr wrapper around a flickr photoset (instance of YFlickrPhotoset)
                     This object contains enough state to be able to fetch photos incrementally
    @callback        The callback function to execute after fetching the photos of the set
 */
Com_Zimbra_Yflickr.prototype.fetchPhotos = function (yphotoset, callback)
{
    // TODO: check whether we are authorized, if not, then return back

    // first see whether we need to fetch any more photos

    if ((yphotoset.total != -1) && 
        (yphotoset.photos.length >= yphotoset.total)
       )
    {
        this.debug ("all photos in this have been fetched, total=" + yphotoset.total);
        yphotoset.buildSlides ();
        callback.run();
    }
    else
    {
        // there are two different flickr APIs to get photos from a set
        var args = yphotoset.getApiArgs();

        // flickr requires a 1-based page number to fetch photos
        var pagenum = yphotoset.pages_fetched + 1;

        var url = flickrapi_getsignedurl (gRestEndpoint, this.api_secret, 
                                            [["api_key", this.api_key],
                                             ["user_id", this.token.user.nsid],
                                             ["page", pagenum], 
                                             ["per_page", FLICKR_PHOTOSPERPAGE],
                                             ["auth_token",this.token.token],
                                             ["extras","icon_server"]].concat(args)
                                         );

        var cb = new AjxCallback (this, this.buildPhotoset, [yphotoset, callback]);
        this.debug ("url=" + url);
        this.sendRequest (null, url, null, cb, true, true);
    }
}

/*  Callback function to build up the contents of a photoset with one or more <photo>
    objects received by a Flickr API call
 */
Com_Zimbra_Yflickr.prototype.buildPhotoset = function (yphotoset, callback, result)
{
    if (result)
    {
        var xmlo = new AjxXmlDoc.createFromXml (result.text);
        var rsp_stat = flickrapi_responsestatus (xmlo);
        var jso = xmlo.toJSObject (false,false,true);

        this.debug ("<xmp>" + result.text + "</xmp>");

        if (rsp_stat == "ok")
        {
            yphotoset.buildPhotoList (jso);
            yphotoset.pages_fetched = yphotoset.pages_fetched + 1;
            this.fetchPhotos (yphotoset, callback);
        }
        else
        {
            // TODO: need a suitable warning message
            this.info ("Cannot fetch photos from set " + yphotoset.getId() + flickrapi_geterrmsg (jso));
            callback.run();
        }
    }

}



// handler for menu items that do not have <actionURL> 
// (see xml file for details on the menu items)
Com_Zimbra_Yflickr.prototype.menuItemSelected = function(itemId)
{
	switch (itemId) {
        case "YflickrAuthorize":
        this.authorize();
        break;
		case "YflickrHelp":
		this.displayFlickrHelp();
		break;
        case "YflickrCredits":
        this.displayCredits();
        break;
		case "YflickrAbout":
		this.displayAboutFlickrZimlet();
		break;
        case "YflickrReconnect":
        this.connect();
        break;
	}
}

Com_Zimbra_Yflickr.prototype.displayFlickrHelp = function() {
}

Com_Zimbra_Yflickr.prototype.displayAboutFlickrZimlet = function()
{
    var view = new DwtComposite (this.getShell());
    var args = {title: "About Yflickr Zimlet", view: view};
    var dlg = this._createDialog (args);
    var aboutText = 
        "The Yflickr Zimlet for Zimbra Collaboration Suite 5.0 is brought to you by Zimbra, a Yahoo! company" + "<br/>" +
        "Copyright (c) 2007. All rights reserved. Flickr, Yahoo! and Zimbra are trademarks owned by Yahoo!, Inc." + "<br/>" +
        "This software is distributed as-is without any warranties whatsoever, either direct or implied.";
    view.getHtmlElement().innerHTML = aboutText;
    dlg.setButtonListener (DwtDialog.OK_BUTTON, new AjxListener(this,function() { dlg.popdown(); dlg.dispose(); }));
    dlg.setButtonListener (DwtDialog.CANCEL_BUTTON, new AjxListener(this,function() { dlg.popdown(); dlg.dispose(); }));
    dlg.popup();
}

Com_Zimbra_Yflickr.prototype.displayCredits = function()
{
    var view = new DwtComposite (this.getShell());
    var args = {title: "About Yflickr Zimlet", view: view};
    var dlg = this._createDialog (args);
    var credits =
    "<span style=\"text-align: center; display:block; margin-left: auto; margin-right: auto;\">" + 
    "Contributors:" + "<p>" +
    "Poonam Jaiswal <poonam@zimbra.com>" + "<br/>" +
    "Mansoor Peerbhoy <mansoor@zimbra.com>" + "<br/>" +
    "Krishna Kumar Sure <krishnakumar@zimbra.com>" + "<br/>" +
    "Chintan Zaveri <czaveri@zimbra.com>" +
    "</p>" +
    "Special Thanks to: " + "<p>" +
    "Naveen Prakash" + "<br/>" + 
    "Rajesh Segu" + "<br/>" +
    "Satish Sugumaran" + "<br/>" +
    "The Entire Zimbra Team" +
    "</p>"
    "</span>";

    view.getHtmlElement().innerHTML = credits;
    dlg.setButtonListener (DwtDialog.OK_BUTTON, new AjxListener(this,function() { dlg.popdown(); dlg.dispose(); }));
    dlg.setButtonListener (DwtDialog.CANCEL_BUTTON, new AjxListener(this,function() { dlg.popdown(); dlg.dispose(); }));
    dlg.popup();
}



// add the flickr photo selection dialog box to the attach files page
Com_Zimbra_Yflickr.prototype.addFlickrTabToAttachDialog = function()
{
    var attachdlg = this._attachdlg = appCtxt.getAttachDialog ();
    var tabview = attachdlg ? attachdlg.getTabView () : null;
    this.FTV = new FlickrTabView (tabview, this);
    var tabkey = attachdlg.addTab ("flickr", "Flickr Photos", this.FTV);

    var callback = new AjxCallback (this, this.onAttachPhotos);
    attachdlg.addOkListener (tabkey, callback);
}

Com_Zimbra_Yflickr.prototype.isInline = function()
{
    return this._attachdlg.isInline();
}

// (event handler) called when flickr photos are selected for attachment
Com_Zimbra_Yflickr.prototype.onAttachPhotos = function()
{
    this.attach_photos = this.getSelectedPhotos();
    this.attach_current = -1;
    this.attachment_ids = [];

    this.FTV.showAttachingPhotos ();      /* display progress */
    var callback = new AjxCallback (this, this.doneAttachPhotos);
    this.attachPhoto(callback);
}

/* get all <img> nodes selected for attachment */
Com_Zimbra_Yflickr.prototype.getSelectedPhotos = function()
{
    var imgs = [];
    var sets = this.getPhotosets();
    for (var s=0; s<sets.length; s++) {
        imgs = imgs.concat (sets[s].getSelectedPhotos());
    }
    return imgs;
}

/* deselect all photos (= <img> nodes) from all sets that were previously selected for attachment */
Com_Zimbra_Yflickr.prototype.deselectAllPhotos = function()
{
    var sets = this.getPhotosets();
    for (var s=0; s<sets.length; s++) {
        sets[s].deselectAllPhotos();
    }
}

/* Invoked (as a callback) when all images have been uploaded to the server
   Now just attach the images to the composer window
 */
Com_Zimbra_Yflickr.prototype.doneAttachPhotos = function ()
{
    // locate the composer control and set up the callback handler
    var composer = appCtxt.getApp(ZmApp.MAIL).getComposeController();
    var callback = new AjxCallback (this,composer._handleResponseSaveDraftListener);

    // build up the attachment list 
    attachment_list = this.attachment_ids.join(",");
    composer.sendMsg(attachment_list,ZmComposeController.DRAFT_TYPE_MANUAL,callback);

    // and clean up all the photosets
    this.deselectAllPhotos();

    // also clear up the attach view
    this.attach_photos = [];
    this.attach_current = -1;
    this.attachment_ids = [];
}

// upload a photo to the zimbra file-upload servlet
Com_Zimbra_Yflickr.prototype.attachPhoto = function (callback)
{
    var i = this.attach_current;
    var l = this.attach_photos.length;
    if (i == (l-1)) {
        // we have finished attaching all photos
        this.debug ("Attached " + l + " Flickr photos");
        callback.run ();
    }
    else
    {
        i = i+1; // starts at -1, so ++ for 0-based index
        var img = this.attach_photos[i];
        var src = img.getAttribute ("flickr_photo_src");
        var id = img.getAttribute ("flickr_photo_id");
        var filename = id + ".jpg";

        var params = ["upload=1","&","fmt=raw","&","filename=",filename].join("");
        var server_url = 
            ZmZimletBase.PROXY + 
            AjxStringUtil.urlComponentEncode (src) + 
            "&" + params;
        var cb = new AjxCallback (this,this.doneAttachPhoto, [callback]);
        AjxRpc.invoke (params, server_url, null, cb, true);
    }
}

// invoked as a callback when a single photo has been attached
Com_Zimbra_Yflickr.prototype.doneAttachPhoto = function (callback, result)
{
    var re = new RegExp("'([^']+)'", "m");
    var re_id = new RegExp ("^[0-9a-f:-]+$","im");

    this.attach_current = this.attach_current + 1;
    this.debug ("<xmp>" + result.text + "</xmp>");

    if (!result.text) {
    } else {
        this.FTV.showAttachProgress ();
        // result.text is some html code with embedded strings inside ''
        var s = result.text;
        for (var i=s.search(re); (i!=-1) && (s.length>0); i=s.search(re)) {
            var m = re.exec (s);
            if (!m) { break; }
            if (m[1].match(re_id)) { this.attachment_ids.push (m[1]); }
            s = s.substring(i+m[0].length);
        }
    }

    this.attachPhoto (callback);
}

/* For uploading photos to flickr */
Com_Zimbra_Yflickr.prototype.addAttachmentHandler = function()
{
    this._msgController = AjxDispatcher.run("GetMsgController");
    this._msgController._initializeListView(ZmController.MSG_VIEW);
    this._msgController._listView[ZmController.MSG_VIEW].addAttachmentLinkHandler (ZmMimeTable.IMG_JPEG,"flickr",this.addSaveToFlickrLink);
}

Com_Zimbra_Yflickr.prototype.addSaveToFlickrLink = function (attachment)
{
    var html = 
    "<a href='#' class='AttLink' style='text-decoration:underline;' " +
    "onClick=\"window.YFlickr_widget.onSaveToFlickr('" + 
    attachment.ct + "','" + attachment.label + "','" + attachment.url +
    "');\">" +
    "Upload to Flickr" + 
    "</a>";
    return html;
}

/* Handle 'Upload to Flickr' action */
Com_Zimbra_Yflickr.prototype.onSaveToFlickr = function(ct,label,src)
{
    var d = this.uploadDlg._getContentDiv (); /* Initialize the Upload Dialog */
    YFlickr_clearElement (d);

    var div = document.createElement ("div");
    div.className = "Yflickr_hCenter";

    var imgI = document.createElement ("img");
    imgI.setAttribute ("src", src);

    var titleS = document.createElement ("span");
    titleS.className = "Yflickr_hLeft";
    titleS.appendChild (document.createTextNode ("Title (Optional): "));
    var titleI = document.createElement ("input");
    titleS.appendChild (titleI);

    var tagsS = document.createElement ("span");
    tagsS.className = "Yflickr_hLeft";
    tagsS.appendChild (document.createTextNode ("Tags (Optional): "));
    var tagsI = document.createElement ("input");
    tagsS.appendChild (tagsI);

    div.appendChild (imgI);
    div.appendChild (titleS);
    div.appendChild (tagsS);
    d.appendChild (div);

    this.uploadDlg.setButtonListener (DwtDialog.OK_BUTTON, new AjxListener (this, function() { this.onConfirmSaveToFlickr (ct, label, src, titleI.value, tagsI.value); }));
    this.uploadDlg.setButtonListener (DwtDialog.CANCEL_BUTTON, new AjxListener (this, function() { this.uploadDlg.popdown(); }));

    this.uploadDlg.popup();
}

/* Upload a single Photo to Flickr */
Com_Zimbra_Yflickr.prototype.onConfirmSaveToFlickr = function (ct, label, src, title, tags)
{
    /* Show a busy message indicating that the file is being uploaded */
    var busy = document.createElement ("div");
    busy.className = "Yflickr_hCenter";

    var busyImgS = document.createElement ("span");
    busyImgS.className = "Yflickr_hCenter";
    var busyImg = document.createElement ("img");
    busyImg.setAttribute ("src", YFLICKR_BUSYIMGURL);
    busyImgS.appendChild (busyImg);

    var busyTextS = document.createElement ("span");
    busyTextS.className = "Yflickr_hCenter";
    busyTextS.appendChild (document.createTextNode ("Please wait while the photo is being uploaded"));

    busy.appendChild (busyImgS);
    busy.appendChild (busyTextS);

    var d = this.uploadDlg._getContentDiv();
    YFlickr_clearElement (d);

    d.appendChild (busy);

    this.uploadDlg.setButtonEnabled (DwtDialog.OK_BUTTON, false);
    this.uploadDlg.setButtonEnabled (DwtDialog.CANCEL_BUTTON, false);

    title = title || "";
    tags = tags || "";

    /* Make a call to yflickr.jsp to upload the selected photo to Flickr */

    var url = this.getResource("yflickr.jsp");
    var flickrparams = [["api_key",this.api_key], ["user_id",this.token.user.nsid], ["auth_token",this.token.token]];
    if (title.length >0) { flickrparams.push (["title", title]); }
    if (tags.length >0) { flickrparams.push (["tags", tags]); }
    var flickrsig = flickrapi_getapisig (this.api_secret, flickrparams);

    var params= ["src=" + AjxStringUtil.urlComponentEncode(src),
                 "user_id=" + this.token.user.nsid,
                 "api_key=" + this.api_key,
                 "auth_token=" + this.token.token,
                 "api_sig=" + flickrsig,
                 "title=" + AjxStringUtil.urlEncode (title),
                 "tags=" + AjxStringUtil.urlEncode (tags),
                ].join ("&");

    var callback = new AjxCallback (this,this.onDoneSaveToFlickr);
    AjxRpc.invoke(params,url+"?"+params,null,callback,false);
}

/* Callback function after a photo has been uploaded to Flickr 
   @result  contains the result of the Flickr upload operation 
 */
Com_Zimbra_Yflickr.prototype.onDoneSaveToFlickr = function(result)
{
    var d = this.uploadDlg._getContentDiv();
    YFlickr_clearElement (d);

    var xmlo = null;
    var jso = null;
    var flickrstatus = null;

    try {
        xmlo = new AjxXmlDoc.createFromXml (result.text);
        jso = xmlo.toJSObject (false,false,true);
        flickrstatus = flickrapi_responsestatus (xmlo);
        this.debug ("Flickr Image Upload - status=" + result.success);
        this.debug ("Flickr Image Upload - result=")
        this.debug ("<xmp>" + result.text + "</xmp>");
    } catch (e) {
        this.debug ("Flickr Image Upload - no valid xml received:");
        this.debug (e.toString());
    }

    var statusS = document.createElement ("span");
    statusS.className = "Yflickr_hCenter";
    var detailS = document.createElement ("span");
    detailS.className = "Yflickr_hCenter";

    if (result.success) {
        statusS.appendChild (document.createTextNode ("Upload to Flickr succeeded"));
        var photoid;
        if (jso && jso.photoid) { photoid = jso.photoid.toString(); }
        else { photoid = ""; }
        detailS.appendChild (document.createTextNode ("Photo Id: " + photoid));
    } else {
        statusS.appendChild (document.createTextNode ("Upload to Flickr failed"));
        this.debug ("<xmp>" + result.text + "</xmp>");
    }

    d.appendChild (statusS);
    d.appendChild (detailS);

    this.uploadDlg.setButtonEnabled (DwtDialog.OK_BUTTON, true);
    this.uploadDlg.setButtonEnabled (DwtDialog.CANCEL_BUTTON, true);

    this.uploadDlg.setButtonListener (DwtDialog.OK_BUTTON, new AjxListener (this, function() { this.uploadDlg.popdown(); }));
    this.uploadDlg.setButtonListener (DwtDialog.CANCEL_BUTTON, new AjxListener (this, function() { this.uploadDlg.popdown(); }));
    if (!this.uploadDlg.isPoppedUp()) { this.uploadDlg.popup(); }
}

Com_Zimbra_Yflickr.prototype.msgDropped = function(msg)
{
    var links = msg.attLinks;
    if ((links != null) && (links.length != 0)) {
        this.attLinks = links;
    }
}

/* FlickrTabView -- a class that implements the dialog box for attaching photos from flickr */

FlickrTabView = function (parent, zimlet)
{
    // initialize the `zimlet' member to point to the yflickr zimlet
    this.zimlet = zimlet;
    DwtTabViewPage.call (this,parent);
}

FlickrTabView.prototype = new DwtTabViewPage;
FlickrTabView.prototype.constructor = FlickrTabView;

FlickrTabView.prototype.toString = function() {
    return "FlickrTabView";
}

FlickrTabView.prototype.gotAttachments = function() {
    return (this.zimlet.getSelectedPhotos().length > 0);
}

FlickrTabView.prototype._createHtml = function()
{ 
    this._contentEl = this.getContentHtmlElement ();
    this._contentEl.innerHTML = "";

    this.noauthDiv = document.createElement ("div");
    this.noauthDiv.className = "Yflickr_busyMsg";
    this.noauthDiv.appendChild (document.createTextNode ("The Flickr Zimlet has not yet been authorized to access your photos. Please click 'Authorize' from the Flickr Zimlet context menu, and complete the authorization process first."));

    var apDiv = document.createElement ("div");
    apDiv.className = "Yflickr_busyMsg";

    /* the 'work in progress' image */
    var apbusyDiv = document.createElement ("div");
    var busyimg = document.createElement ("img");
    busyimg.setAttribute ("src", YFLICKR_BUSYIMGURL);
    apbusyDiv.appendChild (busyimg);
    apDiv.appendChild (apbusyDiv);

    /* the progress text div */
    var approgressDiv = document.createElement ("div");
    approgressDiv.appendChild (document.createTextNode ("Please wait while your photos are being attached"));
    apDiv.appendChild (approgressDiv);

    this.apDiv = apDiv;
    this.apbusyDiv = apbusyDiv;
    this.approgressDiv = approgressDiv;

    /* fetch progress div */
    var fpDiv = document.createElement ("div");
    fpDiv.className = "Yflickr_busyMsg";
    var fpbusyDiv = document.createElement ("div");
    var fpbusyImg = document.createElement ("img");
    fpbusyImg.setAttribute ("src", YFLICKR_BUSYIMGURL);
    fpbusyDiv.appendChild (fpbusyImg);
    fpDiv.appendChild (fpbusyDiv);
    var fpText = document.createElement ("div");
    fpText.appendChild (document.createTextNode("Please wait while your flickr photos are fetched"));
    fpDiv.appendChild (fpText);

    this.fpDiv = fpDiv;

    /* One time initialization (which was present in showMe () has been shifted here */

    /*  viewstate_div - The top portion of display containing the search context */
    this.zimlet.viewstate_div = document.createElement ("div");
    this.zimlet.viewstate_div.className = "Yflickr_viewstate_header";

    /* Text Label */
    var psmsg = document.createElement ("span");
    psmsg.className = "Yflickr_viewstate_photosetmsg";
    psmsg.appendChild (document.createTextNode ("Search Photos:"));
    this.zimlet.viewstate_photosetmsg = psmsg;
    this.zimlet.viewstate_div.appendChild (psmsg);

    /* Search box */
    var input = document.createElement ("input");
    input.setAttribute ("type", "text");
    input.className = "Yflickr_viewstate_searchbytag";
    /* set up the event handlers for the input field -- in particular we need to pay attention to [Enter], [Up], and [Down] */
    input.onkeyup = YFlickr_search;
    input.FTV = this;
    this.zimlet.viewstate_searchbytag = input;
    this.zimlet.viewstate_div.appendChild (input);


    /* Initialize the pagination controls */
    var paginator = document.createElement ("span");
    paginator.className = "Yflickr_viewstate_paginator";

    // append the paginator to the viewstate div
    this.zimlet.viewstate_div.appendChild (paginator);

    var left = document.createElement ("div");
    left.className = "ImgLeftArrow"; // "ImgLeftArrow ZDisabledImage"
    left.tabview = this;
    left.onclick = FlickrPhotoset_onLeft;

    var numbersdiv = document.createElement ("span");
    numbersdiv.className = "Yflickr_viewstate_paginator_numbers";
    numbersdiv.appendChild (document.createTextNode ("0 - 0"));

    var right = document.createElement ("div");
    right.className = "ImgRightArrow";  // "ImgRightArrow ZDisabledImage"
    right.tabview = this;
    right.onclick = FlickrPhotoset_onRight;

    var leftdiv = document.createElement ("span");
    leftdiv.className = "Yflickr_viewstate_paginator_left";

    var rightdiv = document.createElement ("span");
    rightdiv.className = "Yflickr_viewstate_paginator_right";

    leftdiv.appendChild (left);
    rightdiv.appendChild (right);

    paginator.appendChild (leftdiv);
    paginator.appendChild (numbersdiv);
    paginator.appendChild (rightdiv);

    paginator.left = left;
    paginator.numbersdiv = numbersdiv;
    paginator.right = right;

    this.zimlet.viewstate_paginator = paginator;

    // set up the saved searches here (this is a hash-table, keyed by the [indexable] display name of the photoset)
    this.savedSearches = new Object();

    // the photoset being currently displayed (the active set)
    this.activeSetId = null;
}

/* Utility functions to show various stages of progress when the tab-view is visible */
FlickrTabView.prototype.showAttachingPhotos = function ()
{
    this.showElement (this.apDiv);
}

/* Updates the view of attaching photos */
FlickrTabView.prototype.showAttachProgress = function ()
{
    YFlickr_clearElement (this.approgressDiv);
    this.approgressDiv.appendChild (document.createTextNode ("Attached " + (this.zimlet.attach_current + 1) + " of " + this.zimlet.attach_photos.length + " photos"));
}

FlickrTabView.prototype.resetAttachProgress = function ()
{
    YFlickr_clearElement (this.approgressDiv);
    this.approgressDiv.appendChild (document.createTextNode ("Please wait while your photos are being attached"));
}

// Utility function to show custom text in the attachment dialog. Useful when something else needs to be shown
FlickrTabView.prototype.showElement = function (el)
{
    YFlickr_clearElement (this._contentEl);
    this._contentEl.appendChild (el);
}

// Overridden function to draw the (contents of the) Flickr Photos tab in the Attach Files dialog box
FlickrTabView.prototype.showMe = function ()
{
    // clear the main view prior to displaying anything
    YFlickr_clearElement (this._contentEl);

    if (this.zimlet.authStage < FLICKR_AUTHSTAGE_GOTTOKEN)
    {
        this.showElement (this.noauthDiv);
        DwtTabViewPage.prototype.showMe.call (this,parent);
        return;
    }

    // display a wait message while we fetch the photos from flickr
    this._contentEl.appendChild (this.fpDiv);
    this.resetAttachProgress ();

    /* Find out which photoset has been selected for display, and then go display it */
    var ps = this.getSelectedPhotoset();

    var callback = new AjxCallback (this, this.displayPhotos);
    this.zimlet.fetchPhotos (ps, callback);

    // Train the user to understand which search term got him this set
    this.zimlet.viewstate_searchbytag.value = ps.getDisplayName();
    this.zimlet.viewstate_searchbytag.focus();

    DwtTabViewPage.prototype.showMe.call(this,parent);
   	this.setSize(Dwt.DEFAULT, "240");
}

/* This is the search function that can search -
   notinset:                    // not in any set
   set:setname                  // search by set name
   tag:tagname                  // search by tag name
 */
function YFlickr_search (ev)
{
    if (!ev) { ev = window.event; }
    if (this.userinput == null) { this.userinput = this.value; }

    // try to capture tab key
    if ((ev.keyCode == null) || (ev.keyCode == 0)) {
        // (cheap attempt to retain focus for tab)
        var ss = this.FTV.zimlet.suggestSearch (this.userinput, this.value);
        if (ss.length != 0) {
            this.value = ss;
        }
        window.yflickrzimlet = this.FTV.zimlet;
        setTimeout ("yflickrzimlet.viewstate_searchbytag.focus();delete yflickrzimlet;",0);
    } else // autocomplete
    if (ev.keyCode == YFLICKR_KC_DOWN) {
        var ss = this.FTV.zimlet.suggestSearch (this.userinput, this.value);
        if (ss.length != 0) {
            this.value = ss;
        }
        return;
    } else if (ev.keyCode != YFLICKR_KC_ENTER) {
        this.userinput = this.value;
        return;
    } else if (ev.keyCode == YFLICKR_KC_ENTER) {
        this.FTV.doSearch (this.value);
    }
}

/* Perform the search */
FlickrTabView.prototype.doSearch = function (s)
{
    var searchBy = "";
    var searchTerm = "";

    if (s.indexOf ("set:") == 0) {
        searchBy = "set";
        searchTerm = s.substring (4);
    } else if (s.indexOf ("tag:") == 0) {
        searchBy = "tag";
        searchTerm = s.substring (4);
    } else {
        searchTerm = s;
    }

    var ps = this.zimlet.getPhotosetBy (searchBy, searchTerm);

    if (ps) {
        this.setSelectedPhotoset (ps);
        this.showMe ();
    } else {
        /* the set does not exist yet -- so we must add it */
        if (searchBy == "set") {
            this.zimlet.info ("No such photoset: " + searchTerm);
        } else if (searchBy == "tag") {
            var ps = new YFlickrTaggedPhotoset (searchTerm);
            this.zimlet.addPhotoset (ps);
            this.setSelectedPhotoset (ps);
            this.showMe ();
        } else if (s == "notinset:") {
            this.setSelectedPhotoset (this.zimlet.getPhotosetBy("id", "notinset"));
            this.showMe ();
        } else {
            this.zimlet.info ("Unsupported search term: " + s);
        }
    }
}


/* Gets the YFlickrPhotoset object that is being currently displayed in the attachbar */
FlickrTabView.prototype.getSelectedPhotoset = function()
{
    return this.zimlet.getPhotosetBy ("id", this.zimlet.activeSetId);
}

/* Set the photoset that represents the target of the search 
   @ps      Instance of YFlickrPhotoset
 */
FlickrTabView.prototype.setSelectedPhotoset = function(ps)
{
    this.zimlet.activeSetId = ps.getId();
}

/* Event handler for displaying the previous slide of the active photoset (left arrow) */
function FlickrPhotoset_onLeft ()
{
    var view = this.tabview;
    var set = view.getSelectedPhotoset ();
    set.prevSlide();
    view.showMe();
}

/* Event handler for displaying the next slide of the active photoset (right arrow) */
function FlickrPhotoset_onRight ()
{
    var view = this.tabview;
    var set = view.getSelectedPhotoset ();
    set.nextSlide();
    view.showMe();
}

// this function displays the photo selection for the 'flickr photos' tab in 'attach files'
// the view state is taken from the (parent) zimlet
FlickrTabView.prototype.displayPhotos = function ()
{
    // clear off the page -- we will need to redraw it in a mo
    YFlickr_clearElement (this._contentEl);

    var set = this.getSelectedPhotoset();
    var slide = set.getActiveSlide();
    var paginator = this.zimlet.viewstate_paginator;
    set.manageViewState (paginator.left, paginator.numbersdiv, paginator.right);

    // alter the paginator properties as necessary

	this._contentEl.style.position = "static";
    this._contentEl.appendChild (this.zimlet.viewstate_div);
    if (slide != null) { this._contentEl.appendChild (slide); }
}

// event that can handle images being clicked on the attach dialog
Yflickr_toggle_img_selection = function ()
{
    if (this.className == "Yflickr_selected") {
        Yflickr_unselect_img (this);
    }
    else {
        Yflickr_select_img (this);
    }
}

/* select an <img> node for attachment */
Yflickr_select_img = function (img)
{
    if (img.tagName.toLowerCase() == "img") {
        img.className = "Yflickr_selected";
        if (img.parentNode.tagName.toLowerCase() == "div") {
            img.parentNode.className = "Yflickr_img_container_selected";
        }
    }
}

/* deselect an <img> node from attachment */
Yflickr_unselect_img = function (img)
{
    if (img.tagName.toLowerCase() == "img") {
        img.className = "Yflickr_unselected";
        if (img.parentNode.tagName.toLowerCase() == "div") {
            img.parentNode.className = "Yflickr_img_container_unselected";
        }
    }
}

/* removes all child nodes of a dom element */
function YFlickr_clearElement (el)
{
    if (!el) { return; }
    while (el.childNodes.length > 0)
    {
        var firstchild = el.childNodes[0];
        el.removeChild (firstchild);
        firstchild = null;
    }
}

function YFlickr_getIndexableName (s)
{
    var specialchars = [":", " ", "(", ")"];
    var d = s.toString();
    for (var c = 0; c < specialchars.length; c++) {
        d = d.replace (specialchars[c],"");
    }
    return d;
}


