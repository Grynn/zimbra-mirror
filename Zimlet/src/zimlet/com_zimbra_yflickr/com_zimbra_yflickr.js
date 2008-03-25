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

var FLICKR_AUTHSTAGES = ["FLICKR_AUTHSTAGE_UNKNOWN", "FLICKR_AUTHSTAGE_NONE", "FLICKR_AUTHSTAGE_VERIFIED", "FLICKR_AUTHSTAGE_GOTFROB", "FLICKR_AUTHSTAGE_AUTHORIZED", "FLICKR_AUTHSTAGE_GOTTOKEN", "FLICKR_AUTHSTAGE_GOTPHOTOSETS", "FLICKR_AUTHSTAGE_GOTTAGS"];

/* Key codes */
var YFLICKR_KC_UP = 38;
var YFLICKR_KC_DOWN = 40;
var YFLICKR_KC_ENTER = 13;
var YFLICKR_KC_ESC = 27;
var YFLICKR_KC_BKSP = 8;
var YFLICKR_KC_TAB = 9;

// Constants related to photo management

// Constants related to photo display
var FLICKRDISP_COLUMNSPERSLIDE = 4;
var FLICKRDISP_ROWSPERSLIDE = 2;
var FLICKRDISP_PHOTOSPERSLIDE = FLICKRDISP_COLUMNSPERSLIDE * FLICKRDISP_ROWSPERSLIDE;
var FLICKR_PHOTOSPERPAGE = FLICKRDISP_PHOTOSPERSLIDE;           // fetch 4x2 photos per api call 

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
    //this.connect();

    // add a property page to the `attach files' dialog
    this.addFlickrTabToAttachDialog ();

    // add 'Save to Flickr' link
    this.addAttachmentHandler ();

    // assign self to window object because we need to execute some code in window context
    window.YFlickr_widget = this;
};

Com_Zimbra_Yflickr.prototype._getNoAuthDlg = function(){

    if(!this.noauthDlg){
        this.noauthDlg = new DwtDialog (appCtxt.getShell(),null,"Flickr Authorization Required",[DwtDialog.OK_BUTTON]);
        this.noauthDlg.setContent ("<span style=\"text-align:center;\">" +
                                   "The Flickr Zimlet has not yet been authorized to access your photos." +
                                   "<br/>" +
                                   "Please click 'Authorize' from the Flickr Zimlet context menu, and complete the authorization process first." +
                                   "<br/>" +
                                   "</span>"
                );
    }
    return this.noauthDlg;
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

    // append the display name to the suggestion list 
    this.suggestedSearches.push (ps.getDisplayName());
    this.suggestedSearches.sort ();             // XXX: how to avoid doing this all the time ?

    return ps;
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

// Get a photoset by id
Com_Zimbra_Yflickr.prototype.getPhotosetById = function (id)
{
    return eval ("this.yphotosets." + id);
}

/* Split a search term into an n-tuple
   @s   the search term (example "notinset:", "set:cosmic", etc)
   Returns
        [searchby, searchterm, searchscope]
 */
Com_Zimbra_Yflickr.prototype.splitSearchTerm = function (s)
{
    var parts = s.split (" ");  // TODO: use regex ?
    var by = "";
    var term = "";
    var scope = "";

    for (var i = 0; i < parts.length; i++) {
        var x = parts[i].indexOf(":");
        if (x == -1) {
            scope = parts[i];
        } else {
            by = parts[i].substr(0,x) || "";
            term = parts[i].substr(x+1) || "";
        }
    }

    return [by,term,scope];
}

Com_Zimbra_Yflickr.prototype.canonicalizeSearchTerm = function (s)
{
    var searchparts = this.splitSearchTerm(s);
    var canon = "";
    if (searchparts[0].length > 0) { canon = canon + searchparts[0] + ":"; }
    if (searchparts[1].length > 0) { canon = canon + searchparts[1]; }
    if (searchparts[2].length > 0) { if (canon.length > 0) { canon = canon + " "; } canon = canon + searchparts[2]; }

    return canon;
}

/* search the cached photosets by search term -- return null if the search term is not cached */
Com_Zimbra_Yflickr.prototype.searchPhotoset = function (s)
{
    var cs = this.canonicalizeSearchTerm (s);
    var id = YFlickr_getIndexableName (cs);
    return this.getPhotosetById (id);
}

/* get a rest url (signed or unsigned), and (own or not-own)
   @params  a list of tuples, each tuple is a name/value argument pair
   @signed  (boolean) indicates whether a signed url is required
   @iself   (boolean) indicates whether the auth-token and nsid should be passed
 */
Com_Zimbra_Yflickr.prototype.getRESTUrl = function (params, signed, iself)
{
    var url;
    var p = [["api_key", this.api_key]];

    if (signed)
    {
        for (var i=0; i<params.length; i++) { p.push (params[i]); }

        if (iself) {
            p.push (["user_id",this.token.user.nsid]);
            p.push (["auth_token",this.token.token]);
        }

        url = flickrapi_getsignedurl (gRestEndpoint, this.api_secret, p);
    }
    else
    {
        for (var i=0; i<params.length; i++) { p.push (params[i]); }

        url = flickrapi_getunsignedurl (gRestEndpoint, p);
    }

    return url;
}

// connect to flickr and authenticate
Com_Zimbra_Yflickr.prototype.connect = function(auth_stage, result)
{
    if (!auth_stage) { 
        auth_stage = FLICKR_AUTHSTAGE_NONE;
    }

    this.debug ("Entering Flickr Stage: " + FLICKR_AUTHSTAGES[auth_stage]);

    var xmlo = null;
    var jso = null;
    var rsp_stat = "";

    if (result != null)
    {
        try {
            xmlo = new AjxXmlDoc.createFromXml (result.text);
            jso = xmlo.toJSObject (false, false, true);
            rsp_stat = flickrapi_responsestatus (xmlo);
        } catch (e) {
            xmlo = null;
            jso = null;
            rsp_stat = "";
        }

        this.debug ("(Callback) HTTP Response Status - " + result.success);
        this.debug ("(Callback) Flickr API Status - " + rsp_stat);
        this.debug ("(Callback) Flickr API Response - ");
        this.debug ("<xmp>" + result.text + "</xmp>");
    }


    if (auth_stage == FLICKR_AUTHSTAGE_NONE)
    {
        // verify the api key
        this.debug("Verifying api_key using api flickr.test.echo - " + this.api_key); 

        var callback = new AjxCallback (this, this.connect, [FLICKR_AUTHSTAGE_VERIFIED]);
        // var url = flickrapi_getunsignedurl(gRestEndpoint, [["api_key",this.api_key],["method","flickr.test.echo"],["x","y"]]);
        var url = this.getRESTUrl ([["method","flickr.test.echo"],["x","y"]],false,false);

        var headers = null;
        var useGet = true;
        var passErrors = true;

        this.debug("url=" + url);

        this.sendRequest(null, url, headers, callback, useGet, passErrors);
        this.authStage = FLICKR_AUTHSTAGE_NONE;
    }
    else if (auth_stage == FLICKR_AUTHSTAGE_VERIFIED)
    {
        // this function has been invoked as a callback
        if (rsp_stat == "ok")
        {
            // api key has been verified, now we need a frob
            this.debug ("Flickr api_key verified");
            this.debug ("Getting frob using flickr.auth.getFrob");

            // var url = flickrapi_getsignedurl (gRestEndpoint, this.api_secret, [["api_key", this.api_key], ["method", "flickr.auth.getFrob"]]);
            var url = this.getRESTUrl ([["method", "flickr.auth.getFrob"]], true, false);
            var callback = new AjxCallback (this, this.connect, [FLICKR_AUTHSTAGE_GOTFROB]);

            this.debug ("url=" + url);
            this.sendRequest (null, url, null, callback, true, true);

            this.authStage = FLICKR_AUTHSTAGE_VERIFIED;

        } else
        {
            this.info ("Flickr API Key/Secret is invalid !" + flickrapi_geterrmsg (jso));
            this.authStage = FLICKR_AUTHSTAGE_NONE;
        }
    }
    else if (auth_stage == FLICKR_AUTHSTAGE_GOTFROB)
    {
        // check if we have got the frob as we expect
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
                 if(this.authorizeCallback){
                     this.triedConnectingOnce = true;
                     this.authorizeCallback.run();
                     this.authorizeCallback = null;
                     this.triedConnectingOnce = false;
                 }
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

        var url = this.getRESTUrl ([["frob", this.frob],["method", "flickr.auth.getToken"]], true, false);
        var callback = new AjxCallback (this, this.connect, [FLICKR_AUTHSTAGE_GOTTOKEN]);

        this.debug ("url=" + url);
        this.sendRequest (null, url, null, callback, true, true);
    }
    else if (auth_stage == FLICKR_AUTHSTAGE_GOTTOKEN)
    {
        // check if we have got the frob as we expect
        if (rsp_stat == "ok")
        {
            this.authStage = FLICKR_AUTHSTAGE_GOTTOKEN;

            // flickr.auth.getToken returns an auth node
            this.token = jso.auth;
            this.debug ("Got Flickr token " + this.token.token + " with " + this.token.perms + " permissions for user " + this.token.user.username);

            // Get Flickr Photosets
            var url = this.getRESTUrl ([["method", "flickr.photosets.getList"]], true, true);
            var callback = new AjxCallback (this, this.connect, [FLICKR_AUTHSTAGE_GOTPHOTOSETS]);

            this.debug ("url=" + url);
            this.sendRequest (null, url, null, callback, true, true);
        }
        else
        {
            this.info ("Cannot get Flickr Auth Token -- not authorized to access User Photos" + flickrapi_geterrmsg(jso));

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
    }
    else if (auth_stage == FLICKR_AUTHSTAGE_GOTPHOTOSETS)
    {
        /* Create a special photoset to hold those photos not in any set */
        var noneset = new YFlickrUnnamedPhotoset ();
        this.addPhotoset (noneset);

        this.FTV.setSelectedPhotoset (noneset);

        if (rsp_stat == "ok")
        {
            // store the photosets
            this.debug ("Got flickr photosets");

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

            var url = this.getRESTUrl ([["method", "flickr.tags.getListUser"]], true, true);
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
    }
    else if (auth_stage == FLICKR_AUTHSTAGE_GOTTAGS)
    {
        if (rsp_stat == "ok")
        {
            this.debug ("Got tags for user");

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
    }

    this.debug ("Leaving Flickr Stage: " + FLICKR_AUTHSTAGES[auth_stage]);
}

Com_Zimbra_Yflickr.prototype._getAuthDlg = function(){

    if(!this.authDlg){
        this.authDlg = new DwtDialog (appCtxt.getShell(),null,"Flickr Authorization Required",[DwtDialog.OK_BUTTON]);
        this.authDlg.setContent ("<span style=\"text-align:center;\">" +
                                 "A new browser window has been created for you to authorize the Flickr zimlet to access your photo albums." +
                                 "<br/>" +
                                 "Please log in using your Yahoo!/Flickr account, complete the authorization process, and then click OK to proceed" +
                                 "<br/>" +
                                 "</span>"
                );
    }
    return this.authDlg;
};

// set up a window for the user to authorize the frob
Com_Zimbra_Yflickr.prototype.authorize = function()
{
    if (this.authStage < FLICKR_AUTHSTAGE_GOTFROB)
    {
        if(!this.triedConnectingOnce){
            this.authorizeCallback = new AjxCallback(this, this.authorize);
            this.connect();
            return;
        }
        
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

        var authDlg = this._getAuthDlg();
        if (authDlg)
        {
            var listener = new AjxListener (this, this.get_token);
            authDlg.setButtonListener (DwtDialog.OK_BUTTON, listener);
            authDlg.popup();
        }

        this.debug ("opened flickr authorization window");
    }
}

// this function is (should be) called after the user has finished the manual process of authorizing the flickr zimlet to connect
Com_Zimbra_Yflickr.prototype.get_token = function()
{
    var authDlg = this._getAuthDlg();
    if (authDlg && authDlg.isPoppedUp()) {
        authDlg.popdown();
        this.connect (FLICKR_AUTHSTAGE_AUTHORIZED);
    }
}

/*  Recursively Fetch (and build) the photos of a given photoset
    @fargs           Flickr API arguments
    @scope           "local" or "global" depending on whether you want to search only your own photos or everyone else's
    @callback        The callback function to execute after fetching the photos of the set
 */
Com_Zimbra_Yflickr.prototype.fetchPhotos = function (fargs, scope, callback)
{
    // TODO: check whether we are authorized, if not, then return back

    var sendToken = true;
    if (scope == "global") { sendToken = false; }

    var url = this.getRESTUrl (fargs, true, sendToken);

    this.debug ("Fetching Photos from URL " + url);
    this.sendRequest (null, url, null, callback, true, true);
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
    if (this.authStage < FLICKR_AUTHSTAGE_GOTTOKEN) {
        this._getNoAuthDlg().popup();
        return;
    }
    var uploadDlg = this._getUploadDlg();
    var d = uploadDlg._getContentDiv (); /* Initialize the Upload Dialog */
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


    uploadDlg.setButtonListener (DwtDialog.OK_BUTTON, new AjxListener (this, function() { this.onConfirmSaveToFlickr (ct, label, src, titleI.value, tagsI.value); }));
    uploadDlg.setButtonListener (DwtDialog.CANCEL_BUTTON, new AjxListener (this, function() { uploadDlg.popdown(); }));

    uploadDlg.popup();
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

    var uploadDlg = this._getUploadDlg();
    var d = uploadDlg._getContentDiv();
    YFlickr_clearElement (d);

    d.appendChild (busy);

    uploadDlg.setButtonEnabled (DwtDialog.OK_BUTTON, false);
    uploadDlg.setButtonEnabled (DwtDialog.CANCEL_BUTTON, false);

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
                 "tags=" + AjxStringUtil.urlEncode (tags)
                ].join ("&");

    var callback = new AjxCallback (this,this.onDoneSaveToFlickr);
    AjxRpc.invoke(params,url+"?"+params,null,callback,false);
}

Com_Zimbra_Yflickr.prototype._getUploadDlg = function(){
    if(!this.uploadDlg){
        this.uploadDlg = new DwtDialog (appCtxt.getShell(),null,"Upload Photo(s) to Flickr",[DwtDialog.OK_BUTTON,DwtDialog.CANCEL_BUTTON]);
    }
    return this.uploadDlg;
};

/* Callback function after a photo has been uploaded to Flickr 
   @result  contains the result of the Flickr upload operation 
 */
Com_Zimbra_Yflickr.prototype.onDoneSaveToFlickr = function(result)
{
    var uploadDlg = this._getUploadDlg();
    
    var d = uploadDlg._getContentDiv();
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

    uploadDlg.setButtonEnabled (DwtDialog.OK_BUTTON, true);
    uploadDlg.setButtonEnabled (DwtDialog.CANCEL_BUTTON, true);

    uploadDlg.setButtonListener (DwtDialog.OK_BUTTON, new AjxListener (this, function() { uploadDlg.popdown(); }));
    uploadDlg.setButtonListener (DwtDialog.CANCEL_BUTTON, new AjxListener (this, function() { uploadDlg.popdown(); }));
    if (!uploadDlg.isPoppedUp()) { uploadDlg.popup(); }
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

FlickrTabView.prototype._createProgressDivs = function(){

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
    this.approgressDiv = approgressDiv;
};

FlickrTabView.prototype.getApprogressDiv = function(){
    if(!this.approgressDiv){
        this._createProgressDivs();
    }
    return this.approgressDiv;
};

FlickrTabView.prototype.getApDiv = function(){
    if(!this.apDiv){
        this._createProgressDivs();
    }
    return this.apDiv;
};

FlickrTabView.prototype._createHtml = function()
{ 
    this._contentEl = this.getContentHtmlElement ();
    this._contentEl.innerHTML = "";

    this.noauthDiv = document.createElement ("div");
    this.noauthDiv.className = "Yflickr_busyMsg";
    this.noauthDiv.appendChild (document.createTextNode ("The Flickr Zimlet has not yet been authorized to access your photos. Please click 'Authorize' from the Flickr Zimlet context menu, and complete the authorization process first."));

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

    this.paginator = paginator;

    // set up a Photo Fetcher for the photosets to pull in the flickr photos on demand
    this.photoFetcher = new AjxCallback (this.zimlet, this.zimlet.fetchPhotos);

    // set up the saved searches here (this is a hash-table, keyed by the [indexable] display name of the photoset)
    this.savedSearches = new Object();

    // the photoset being currently displayed (the active set)
    this.activeSetId = null;
}

/* Utility functions to show various stages of progress when the tab-view is visible */
FlickrTabView.prototype.showAttachingPhotos = function ()
{
    this.showElement (this.getApDiv());
}

/* Updates the view of attaching photos */
FlickrTabView.prototype.showAttachProgress = function ()
{
    YFlickr_clearElement (this.getApprogressDiv());
    this.getApprogressDiv().appendChild (document.createTextNode ("Attached " + (this.zimlet.attach_current + 1) + " of " + this.zimlet.attach_photos.length + " photos"));
}

FlickrTabView.prototype.resetAttachProgress = function ()
{
    YFlickr_clearElement (this.getApprogressDiv());
    this.getApprogressDiv().appendChild (document.createTextNode ("Please wait while your photos are being attached"));
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
    this.resetAttachProgress ();

    if (this.zimlet.authStage < FLICKR_AUTHSTAGE_GOTTOKEN)
    {
        this.showElement (this.noauthDiv);
        DwtTabViewPage.prototype.showMe.call (this,parent);
        return;
    }

    /* Find out which photoset has been selected for display, and then go display it */
    var ps = this.getSelectedPhotoset();
    ps.showActiveSlide (this.paginator, this.photoFetcher);
    this.displayPhotos ();

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
    if (this.FTV.userinput == null) { this.FTV.userinput = this.value; }

    if (ev.keyCode == YFLICKR_KC_DOWN) {
        var ss = this.FTV.zimlet.suggestSearch (this.FTV.userinput, this.value);
        if (ss.length != 0) {
            this.value = ss;
        }
        return;
    } else if (ev.keyCode != YFLICKR_KC_ENTER) {
        this.FTV.userinput = this.value;
        return;
    } else if (ev.keyCode == YFLICKR_KC_ENTER) {
        this.FTV.doSearch (this.value);
    }
}

/* Perform the search */
FlickrTabView.prototype.doSearch = function (s)
{
    /* search syntax is "searchby:term [extra]" */

    var ps = this.zimlet.searchPhotoset (s);
    if (ps != null)
    {
        this.setSelectedPhotoset (ps);
        this.showMe ();
    }
    else
    {
        var parts = s.split (" ");
        if (parts.length > 2) {
            this.zimlet.info ("too many search terms");
        } else {
            var parts = this.zimlet.splitSearchTerm(this.zimlet.canonicalizeSearchTerm (s));
            var by = parts[0];
            var term = parts[1];
            var scope = parts[2];

            if (by == "set") { this.zimlet.info ("no such set: " + term); }
            else if (by == "tag") { 
                var ps = this.zimlet.addPhotoset (new YFlickrTaggedPhotoset(term,scope));
                this.setSelectedPhotoset (ps);
                this.showMe ();
            } else { 
                this.zimlet.info ("unsupported search - " + s);
            }
        }
    }
}


/* Gets the YFlickrPhotoset object that is being currently displayed in the attachbar */
FlickrTabView.prototype.getSelectedPhotoset = function()
{
    return this.zimlet.getPhotosetById (this.zimlet.activeSetId);
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
    // we always have at least one slide in the photoset, even if it is empty
    var view = this.tabview;
    var set = view.getSelectedPhotoset ();
    set.showPrevSlide(view.paginator, view.photoFetcher);
    view.showMe();
}

/* Event handler for displaying the next slide of the active photoset (right arrow) */
function FlickrPhotoset_onRight ()
{
    // we always have at least one slide in the photoset, even if it is empty
    var view = this.tabview;
    var set = view.getSelectedPhotoset ();
    set.showNextSlide(view.paginator, view.photoFetcher);
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

    // alter the paginator properties as necessary

	this._contentEl.style.position = "static";
    this._contentEl.appendChild (this.zimlet.viewstate_div);
    this._contentEl.appendChild (slide);
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
        if (img.parentNode != null) {
            img.parentNode.className = "Yflickr_img_container_selected";
        }
    }
}

/* deselect an <img> node from attachment */
Yflickr_unselect_img = function (img)
{
    if (img.tagName.toLowerCase() == "img") {
        img.className = "Yflickr_unselected";
        if (img.parentNode != null) {
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
    return "S" + hex_md5 (s);
}


