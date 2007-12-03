/* Photoset management for Yahoo Flickr Zimlet */

// YFlickrPhotoset constructor
function YFlickrPhotoset ()
{
}

YFlickrPhotoset.prototype = new Object();
YFlickrPhotoset.prototype.constructor = YFlickrPhotoset;

/* Returns a (hopefully) unique ID by which this photoset may be referred */
YFlickrPhotoset.prototype.getId = function()
{
    return YFlickr_getIndexableName (this.getDisplayName());
}

/* Get the Human Readable Display Name */
YFlickrPhotoset.prototype.getDisplayName = function()
{
    if (this.getKeywordScope() != "global") { return this.getKeywordBy() + ":" + this.getKeywordTerm(); }
    else { return this.getKeywordBy() + ":" + this.getKeywordTerm() + " " + this.getKeywordScope(); }
}

/* which search by will get us this ? */
YFlickrPhotoset.prototype.getKeywordBy = function()
{
    return "none";
}

/* which search by will get us this ? */
YFlickrPhotoset.prototype.getKeywordTerm = function()
{
    return "none";
}

YFlickrPhotoset.prototype.getKeywordScope = function()
{
    return "local";
}

/* Indicate whether this photoset is at least partially loaded 
   Returns true if at least one photo has already been fetched
 */
YFlickrPhotoset.prototype.isPartlyLoaded = function()
{
    if (this.total == -1) { return false; }
    else { return true; }
}

/* Indicate whether this set is fully loaded */
YFlickrPhotoset.prototype.isFullyLoaded = function()
{
    if (!this.isPartlyLoaded()) { return false; }
    else if (this.photos.length < this.total) { return false; }
    else { return true; }
}

/* Add a new <photo> object to the end of the photo collection */
YFlickrPhotoset.prototype.addPhoto = function(photo)
{
    this.photos.push (photo);
}

/* Plural -- add more than one <photo> object to the end of the photo collection */
YFlickrPhotoset.prototype.addPhotos = function (photos)
{
    for (var i=0; i<photos.length; ++i) { this.addPhoto (photos[i]); }
}

/* Get the Flickr API that will be required to fetch this photoset 
   return value is a list of pairs
 */
YFlickrPhotoset.prototype.getApiArgs = function()
{
    return [];
}

/* Return a list of arguments that will be required to send to Flickr in order
   to return the next batch of photos
 */
YFlickrPhotoset.prototype.getFetcherArgs = function()
{
    // Different types of photosets have use different APIs to fetch photos
    var aargs = this.getApiArgs();

    // we need to calculate the page number that we need to fetch (1-based index)
    var perpage = FLICKR_PHOTOSPERPAGE;
    var page = Math.ceil(this.photos.length/perpage) + 1;
    var pargs = [["per_page", perpage], ["page", page]];
    var eargs = [["extras", "icon_server"]];

    return aargs.concat(pargs).concat(eargs);
}

YFlickrPhotoset.prototype.getActiveSlide = function()
{
    return this.slides[this.active_slide];
}

/* Build the photo collection from the result of the Flickr API call */
YFlickrPhotoset.prototype.buildPhotoList = function (flickrResponse)
{
    this.addPhotos (this.photosFromFlickrResponse (flickrResponse));
}

/* Extract a list of <photo> objects from an HTTP response 
   @httpResponse    The HTTP response object signifying the result of a 
                    previously executed HTTP request
 */
YFlickrPhotoset.prototype.photosFromHTTPResponse = function (httpResponse)
{
    var photos = [];

    if (httpResponse)
    {
        try {
            var xmlo = new AjxXmlDoc.createFromXml (httpResponse.text);
            var rsp_stat = flickrapi_responsestatus (xmlo);
            var jso = xmlo.toJSObject (false,false,true);
            photos = this.photosFromFlickrResponse (jso);
        } catch (e) {
            this.debug ("Cannot parse HTTP response");
            this.debug ("<xmp>" + httpResponse.text + "</xmp>");
            photos = [];
        }
    }

    return photos;
}

/* Extract a list of <photo> objects from a Flickr Response
   @flickrResponse  The Flickr response encoded as a JSON object
 */
YFlickrPhotoset.prototype.photosFromFlickrResponse = function (flickrResponse)
{
    /* OVERRIDDEN */
    return [];
}

/* show the active slide */
YFlickrPhotoset.prototype.showActiveSlide = function (paginator, fetcher)
{
    var slide = this.getActiveSlide ();
    this.showSlide (slide, paginator, fetcher);
}

/* display the requested slide -- we may need to fetch the contents of the slide
 */
YFlickrPhotoset.prototype.showSlide = function (slide, paginator, fetcher)
{
    var start = eval (slide.getAttribute ("startidx"));
    var end = eval (slide.getAttribute ("endidx"));

    this.manageViewState (slide, paginator);
    if (end == -1) {
        // this slide hasn't ever been fetched, so fetch it and build it

        var callback = new AjxCallback (this, this.buildSlide, [slide, paginator]);
        fetcher.run (this.getFetcherArgs(), this.getKeywordScope(), callback);
    } else {
        // nothing to do here 
    }
}

YFlickrPhotoset.prototype.buildSlide = function (slide, paginator, httpResponse)
{
    var photos = this.photosFromHTTPResponse (httpResponse);

    var start = eval (slide.getAttribute ("startidx"));
    var end = start + photos.length;
    slide.setAttribute ("endidx", end.toString());

    YFlickr_clearElement (slide);

    if (photos.length == 0)
    {
        // no photos in response
        this.debug ("Zero photos in HTTP response - ");
        this.debug ("HTTP Response: ");
        this.debug ("<xmp>" + httpResponse.text + "</xmp>");

        slide.appendChild (document.createTextNode ("no photos"));
    }
    else
    {
        // calculate the number of rows required
        var R = Math.ceil((end - start) / FLICKRDISP_COLUMNSPERSLIDE);
        var P = photos.length;
        var p = 0;
        for (var r=0; r<R; ++r)
        {
            var row = this.pushNewSlideRow (slide);

            var q = Math.min (p+FLICKRDISP_COLUMNSPERSLIDE, photos.length);
            while (p < q) {
                this.pushNewSlideRowImg (row, photos[p]);
                ++p;
            }
        }

        // push the photos for future reference
        this.addPhotos (photos);
    }

    this.manageViewState (slide, paginator);
}

/* creates an empty slide element and appends it to the existing slides */
YFlickrPhotoset.prototype.pushNewSlide = function()
{
    /* push a new slide at the end of the existing slides */

    var S = document.createElement ("div");
    S.className = "Yflickr_view_attach";

    var start = this.photos.length;
    var end = -1;

    S.setAttribute ("startidx", start.toString());
    S.setAttribute ("endidx", end.toString());

    var fpDiv = document.createElement ("div");
    fpDiv.className = "Yflickr_busyMsg";
    var fpbusyDiv = document.createElement ("div");
    var fpbusyImg = document.createElement ("img");

    fpbusyImg.setAttribute ("src", YFLICKR_BUSYIMGURL);
    fpbusyDiv.appendChild (fpbusyImg);
    fpDiv.appendChild (fpbusyDiv);
    var fpText = document.createElement ("div");
    fpText.appendChild (document.createTextNode("Please wait while the photos are fetched"));
    fpDiv.appendChild (fpText);

    S.appendChild (fpDiv);

    this.slides.push (S);
    return S;
}

/* creates a new slide row and pushes it into the parent slide */
YFlickrPhotoset.prototype.pushNewSlideRow = function(slide)
{
    var R = document.createElement ("div");
    R.className = "Yflickr_sliderow";

    slide.appendChild (R);
    return R;
}

/* push a new image into a slide row */
YFlickrPhotoset.prototype.pushNewSlideRowImg = function (row, photo)
{
    /* Single image container (S) */
    var S = document.createElement ("span");
    S.className = "Yflickr_img_container_unselected";

    var img = document.createElement ("img");
    img.className = "Yflickr_unselected";
    img.onclick = Yflickr_toggle_img_selection;

    var photo_url_s = "http://farm" + photo.farm + ".static.flickr.com/" + photo.server + "/" + photo.id + "_" + photo.secret + "_s.jpg";
    var photo_url_m = "http://farm" + photo.farm + ".static.flickr.com/" + photo.server + "/" + photo.id + "_" + photo.secret + "_m.jpg";
    var photo_url = "http://farm" + photo.farm + ".static.flickr.com/" + photo.server + "/" + photo.id + "_" + photo.secret + ".jpg";

    img.setAttribute ("src", photo_url_s);
    img.setAttribute ("flickr_photo_id", photo.id);
    img.setAttribute ("flickr_photo_src", photo_url_m);
    if (photo.title) { img.setAttribute ("alt", photo.title); }

    S.appendChild (img);
    row.appendChild (S);

    return S;
}

/* manipulate the paginator controls according to current view state */
YFlickrPhotoset.prototype.manageViewState = function (slide, paginator)
{
    var txt;
    var start = eval (slide.getAttribute ("startidx")) + 1; // +1 for human readability
    var end = eval (slide.getAttribute ("endidx"));
    var total = this.total;
    var left = paginator.left;
    var right = paginator.right;
    var numbersdiv = paginator.numbersdiv;

    if (start > total) { start = total; } /* unlikely */
    if (end > total) { end = total; }

    if (start <= 1) { left.className = "ImgLeftArrow ZDisabledImage"; }
    else { left.className = "ImgLeftArrow"; }

    if (end == total) { right.className = "ImgRightArrow ZDisabledImage"; }
    else { right.className = "ImgRightArrow"; }

    if (end < 0) { txt = "(loading)"; }
    else if ((start == end) && (end == 0)) { txt = "no photos"; }
    else { txt = "" + start + " to " + end + " of " + total; }

    YFlickr_clearElement (numbersdiv);
    numbersdiv.appendChild (document.createTextNode (txt));
}

/* Build <div> elements containing a slide-full of photos to be selected for attachment */
YFlickrPhotoset.prototype.buildSlides = function ()
{
}

/* returns a list of photos (<img>) that have been selected for attachment */
YFlickrPhotoset.prototype.getSelectedPhotos = function()
{
    var simgs = new Array();

    for (var s=0; s<this.slides.length; s++)
    {
        var slide = this.slides[s];
        var imgs = slide.getElementsByTagName ("img");
        for (var i=0; i<imgs.length;i++) {
            if (imgs[i].className == "Yflickr_selected") { simgs.push (imgs[i]); }
        }
    }
    return simgs;
}

/* causes all images to be deselected */
YFlickrPhotoset.prototype.deselectAllPhotos = function()
{
    for (var s=0; s<this.slides.length; s++) {
        var imgs = this.slides[s].getElementsByTagName ("img");
        for (var i=0; i<imgs.length; i++) { Yflickr_unselect_img (imgs[i]); }
    }
}

/* move on to the next slide, if possible */
YFlickrPhotoset.prototype.showNextSlide = function(paginator, fetcher)
{
    // we always have at least one slide in the photoset, even if it is empty

    if (this.active_slide < (this.slides.length -1)) {
        // there are more slides to the right
        this.active_slide = this.active_slide +1;
    } else {
        // see if we need one more slide
        var L = this.slides [this.slides.length -1];
        var end = eval(L.getAttribute ("endidx"));

        if ((end != -1) && (end < this.total)) {
            this.pushNewSlide ();
            this.active_slide = this.active_slide +1;
        }
    }
    this.showActiveSlide (paginator, fetcher);
}

/* move back to the previous slide, if possible */
YFlickrPhotoset.prototype.showPrevSlide = function(paginator, fetcher)
{
    // we always have at least one slide in the photoset, even if it is empty
    // since we will always build slides on demand, the set will start off with
    // just one slide, and we will have to build as we go along

    if (this.active_slide > 0) {
        this.active_slide = this.active_slide -1;
    }

    this.showActiveSlide (paginator, fetcher);
}

YFlickrPhotoset.prototype.debug = function(msg)
{
    DBG.println ("[YFlickrPhotoset] " + msg);
}

// ------------------------------------------------------------------------------------------------- //

/* Subclass to handle photos not in any set (i.e. unnamed set) */
function YFlickrUnnamedPhotoset()
{
    /* Common Initialization of YPhotoset superclass */
    this.pages_fetched = 0;             // FLICKR_PHOTOSPERPAGE photos per page
    this.total = -1;                    // total photos in this photoset
    this.photos = [];                   // an array of <photo> objects
    this.slides = [];                   // array of slides, each slide is <div>

    /* YFlickrUnnamedPhotoset initialization */
    this.psid = "notinset";

    this.pushNewSlide ();               // push a dummy slide even though we have no photos
    this.active_slide = 0;              // the slide number being shown
}

YFlickrUnnamedPhotoset.prototype = new YFlickrPhotoset();
YFlickrUnnamedPhotoset.prototype.constructor = YFlickrUnnamedPhotoset;

/* Extract a list of <photo> objects from a Flickr Response
   @flickrResponse  The Flickr response encoded as a JSON object
 */
YFlickrUnnamedPhotoset.prototype.photosFromFlickrResponse = function (flickrResponse)
{
    var photos = [];

    if (flickrResponse && flickrResponse.photos && (flickrResponse.photos.total != null))
    {
        if (this.total == -1) { this.total = eval(flickrResponse.photos.total); }
        if (this.total != eval(flickrResponse.photos.total)) { this.debug ("Photoset contents changed mid-course !"); }

        if (flickrResponse.photos.photo) {
            if (flickrResponse.photos.photo.length == null) {
                // single photo
                photos.push (flickrResponse.photos.photo);
            } else {
                for (var p = 0; p < flickrResponse.photos.photo.length; p ++) {
                    photos.push (flickrResponse.photos.photo[p]);
                }
            }
        }
    }

    return photos;
}

/* Get the Human Readable Display Name */
YFlickrUnnamedPhotoset.prototype.getDisplayName = function()
{
    return "notinset:";
}

/* which search by will get us this ? */
YFlickrUnnamedPhotoset.prototype.getKeywordBy = function()
{
    return "notinset";
}

/* which search by will get us this ? */
YFlickrUnnamedPhotoset.prototype.getKeywordTerm = function()
{
    return "all";
}



/* overridden */
YFlickrUnnamedPhotoset.prototype.getApiArgs = function()
{
    return [["method", "flickr.photos.getNotInSet"]];
}


// ------------------------------------------------------------------------------------------------- //

/* Subclass to handle photos obtained by set (i.e. named set) */
function YFlickrNamedPhotoset(id, title)
{
    /* Common Initialization of YPhotoset superclass */
    this.pages_fetched = 0;             // FLICKR_PHOTOSPERPAGE photos per page
    this.total = -1;                    // total photos in this photoset
    this.photos = [];                   // an array of <photo> objects
    this.slides = [];                   // array of slides, each slide is <div>

    /* YFlickrNamedPhotoset initialization */
    this._id  = id;
    this.psid = "set" + id;
    this.title = title;

    this.pushNewSlide ();               // push a dummy slide even though we have no photos
    this.active_slide = 0;              // the slide number being shown
}

YFlickrNamedPhotoset.prototype = new YFlickrPhotoset();
YFlickrNamedPhotoset.prototype.constructor = YFlickrNamedPhotoset;

/* Extract a list of <photo> objects from a Flickr Response
   @flickrResponse  The Flickr response encoded as a JSON object
 */
YFlickrNamedPhotoset.prototype.photosFromFlickrResponse = function (flickrResponse)
{
    var photos = [];
    if (flickrResponse && flickrResponse.photoset && (flickrResponse.photoset.total != null))
    {
        if (this.total == -1) { this.total = eval(flickrResponse.photoset.total); }
        if (this.total != eval(flickrResponse.photoset.total)) { this.debug ("Photoset contents changed mid-course !"); }

        if (flickrResponse.photoset.photo) {
            if (flickrResponse.photoset.photo.length == null) {
                // single photo
                photos.push (flickrResponse.photoset.photo);
            } else {
                for (var p = 0; p < flickrResponse.photoset.photo.length; p ++) {
                    photos.push (flickrResponse.photoset.photo[p]);
                }
            }
        }
    }
    return photos;
}

/* overridden */
YFlickrNamedPhotoset.prototype.getApiArgs = function()
{
    return [["method", "flickr.photosets.getPhotos"],
            ["photoset_id", this._id]
           ];
}

/* which search by will get us this ? */
YFlickrNamedPhotoset.prototype.getKeywordBy = function()
{
    return "set";
}

/* which search by will get us this ? */
YFlickrNamedPhotoset.prototype.getKeywordTerm = function()
{
    return this.title;
}


// ------------------------------------------------------------------------------------------------- //

/* Subclass to handle photos searched by tag (i.e. search photos by tag) */
function YFlickrTaggedPhotoset(tag,scope)
{
    /* Common Initialization of YPhotoset superclass */
    this.pages_fetched = 0;             // FLICKR_PHOTOSPERPAGE photos per page
    this.total = -1;                    // total photos in this photoset
    this.photos = [];                   // an array of <photo> objects
    this.slides = [];                   // array of slides, each slide is <div>

    this.scope = scope || "local";

    /* YFlickrTaggedPhotoset initialization */
    this.tag = tag;
    this.psid = "tag" + this.tag;

    this.pushNewSlide ();               // push a dummy slide even though we have no photos
    this.active_slide = 0;              // the slide number being shown
}

YFlickrTaggedPhotoset.prototype = new YFlickrPhotoset();
YFlickrTaggedPhotoset.prototype.constructor = YFlickrTaggedPhotoset;

YFlickrTaggedPhotoset.prototype.getApiArgs = function()
{
    return [["method", "flickr.photos.search"],
            ["tags", this.tag]
           ];
}

/* Extract a list of <photo> objects from a Flickr Response
   @flickrResponse  The Flickr response encoded as a JSON object
 */
YFlickrTaggedPhotoset.prototype.photosFromFlickrResponse = function (flickrResponse)
{
    var photos = [];

    if (flickrResponse && flickrResponse.photos && (flickrResponse.photos.total != null))
    {
        if (this.total == -1) { this.total = eval(flickrResponse.photos.total); }

        if (flickrResponse.photos.photo) {
            if (flickrResponse.photos.photo.length == null) {
                // single photo
                photos.push (flickrResponse.photos.photo);
            } else {
                for (var p = 0; p < flickrResponse.photos.photo.length; p ++) {
                    photos.push (flickrResponse.photos.photo[p]);
                }
            }
        }
    }

    return photos;
}

/* which search by will get us this ? */
YFlickrTaggedPhotoset.prototype.getKeywordBy = function()
{
    return "tag";
}

/* which search by will get us this ? */
YFlickrTaggedPhotoset.prototype.getKeywordTerm = function()
{
    return this.tag;
}

/* return the search scope for this set */
YFlickrPhotoset.prototype.getKeywordScope = function()
{
    return this.scope;
}

