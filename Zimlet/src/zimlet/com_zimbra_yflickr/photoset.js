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
    return this.psid;
}

/* Get the Human Readable Display Name */
YFlickrPhotoset.prototype.getDisplayName = function()
{
    return (this.getKeywordBy() + ":" + this.getKeywordTerm());
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

/* Add a new <photo> object to the end of the photo collection */
YFlickrPhotoset.prototype.addPhoto = function(photo)
{
    this.photos.push (photo);
}

/* Get the Flickr API that will be required to fetch this photoset 
   return value is a list of pairs
 */
YFlickrPhotoset.prototype.getApiArgs = function()
{
    return [];
}

YFlickrPhotoset.prototype.getActiveSlide = function()
{
    if (this.view_initialized == false) {
        this.debug ("Warning! Uninitialized photoset");
        return null;
    } else {
        return this.slides[this.active_slide];
    }
}

/* Build the photo collection from the result of the Flickr API call */
YFlickrPhotoset.prototype.buildPhotoList = function (jso)
{
    /* OVERRIDDEN */
    return;
}

/* manipulate the paginator controls according to current view state */
YFlickrPhotoset.prototype.manageViewState = function (left,numbersdiv,right)
{
    var txt;
    var start,end,total;

    total = this.total;
    start = (this.active_slide * FLICKRDISP_PHOTOSPERSLIDE) + 1;
    end = (this.active_slide + 1) * FLICKRDISP_PHOTOSPERSLIDE;

    if (start > total) { start = total; } /* unlikely */
    if (end > total) { end = total; }

    if (start <= 1) { left.className = "ImgLeftArrow ZDisabledImage"; }
    else { left.className = "ImgLeftArrow"; }

    if (end == total) { right.className = "ImgRightArrow ZDisabledImage"; }
    else { right.className = "ImgRightArrow"; }

    txt = "" + start + " to " + end + " of " + total;

    YFlickr_clearElement (numbersdiv);
    numbersdiv.appendChild (document.createTextNode (txt));
}

/* Build <div> elements containing a slide-full of photos to be selected for attachment */
YFlickrPhotoset.prototype.buildSlides = function ()
{
    if (this.view_initialized)
    {
        return;
    }

    // Compute the number of slides
    var numslides = Math.ceil(this.total/FLICKRDISP_PHOTOSPERSLIDE);
    this.slides = [];
    this.active_slide = 0;

    // if there are no photos in the set, then we need a dummy slide
    if (numslides <= 0)
    {
        var dummyslide = document.createElement ("div");
        dummyslide.className = "Yflickr_view_attach";
        var noimagesinsetNode = document.createTextNode ("No images in this photoset");
        dummyslide.appendChild (noimagesinsetNode);
        this.slides.push (dummyslide);
    }

    /* Start building the photo slides */
    for (var i=0; i<numslides; i++)
    {
        // start and end indicate the start and end indices of the images in this slide
        var start = i*FLICKRDISP_PHOTOSPERSLIDE;
        var end = start + FLICKRDISP_PHOTOSPERSLIDE;
        end = Math.min (end, this.photos.length);

        var slide = document.createElement ("div");
        slide.className = "Yflickr_view_attach";

        var numrows = Math.ceil((end - start) / FLICKRDISP_COLUMNSPERSLIDE);

        for (var r=0; r<numrows; r++)
        {
            var row = document.createElement ("div");
            row.className = "Yflickr_sliderow";

            var rstart = start + (r * FLICKRDISP_COLUMNSPERSLIDE);
            var rend = rstart + FLICKRDISP_COLUMNSPERSLIDE;
            rend = Math.min (rend, end);

            for (var j = rstart; j < rend; j ++)
            {
                var photo = this.photos[j];
                var photo_url_s = "http://farm" + photo.farm + ".static.flickr.com/" + photo.server + "/" + photo.id + "_" + photo.secret + "_s.jpg";
                var photo_url_m = "http://farm" + photo.farm + ".static.flickr.com/" + photo.server + "/" + photo.id + "_" + photo.secret + "_m.jpg";
                var photo_url = "http://farm" + photo.farm + ".static.flickr.com/" + photo.server + "/" + photo.id + "_" + photo.secret + ".jpg";

                // create a div representing this slide
                var div = document.createElement ("div");
                div.className = "Yflickr_img_container_unselected";

                var img = document.createElement ("img");
                img.setAttribute ("src", photo_url_s);
                img.setAttribute ("flickr_photo_id", photo.id);
                img.setAttribute ("flickr_photo_src", photo_url_m);
                img.className = "Yflickr_unselected";
                img.onclick = Yflickr_toggle_img_selection;
                if (photo.title) { img.setAttribute ("alt", photo.title); }

                div.appendChild (img);
                row.appendChild (div);
            }
            slide.appendChild (row);
        }

        this.slides.push (slide);
    }

    this.view_initialized = true;
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
YFlickrPhotoset.prototype.nextSlide = function()
{
    if (this.view_initialized == true) {
        if (this.active_slide < (this.slides.length -1)) {
            this.active_slide = this.active_slide +1;
        }
    }
}

/* move back to the previous slide, if possible */
YFlickrPhotoset.prototype.prevSlide = function()
{
    if (this.view_initialized == true) {
        if (this.active_slide > 0) {
            this.active_slide = this.active_slide -1;
        }
    }
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
    this.active_slide = 0;              // the slide number being shown
    this.view_initialized = false;      // status of slideview initialization

    /* YFlickrUnnamedPhotoset initialization */
    this.psid = "notinset";
}

YFlickrUnnamedPhotoset.prototype = new YFlickrPhotoset();
YFlickrUnnamedPhotoset.prototype.constructor = YFlickrUnnamedPhotoset;

/* Return a set of <photo> objects that are contained within a flickr api result
 */
YFlickrUnnamedPhotoset.prototype.buildPhotoList = function (jso)
{
    if (jso && jso.photos && (jso.photos.total != null))
    {
        if (this.total == -1) { this.total = eval(jso.photos.total); }
        if (this.total != eval(jso.photos.total)) { this.debug ("Photoset contents changed mid-course !"); }

        if (jso.photos.photo) {
            if (jso.photos.photo.length == null) {
                // single photo
                this.addPhoto (jso.photos.photo);
            } else {
                for (var p = 0; p < jso.photos.photo.length; p ++) {
                    this.addPhoto (jso.photos.photo[p]);
                }
            }
        }
    }
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
    this.active_slide = 0;              // the slide number being shown
    this.view_initialized = false;      // status of slideview initialization

    /* YFlickrNamedPhotoset initialization */
    this._id  = id;
    this.psid = "set" + id;
    this.title = title;
}

YFlickrNamedPhotoset.prototype = new YFlickrPhotoset();
YFlickrNamedPhotoset.prototype.constructor = YFlickrNamedPhotoset;

/* Build thine self from the Flickr API result */
YFlickrNamedPhotoset.prototype.buildPhotoList = function (jso)
{
    if (jso && jso.photoset && (jso.photoset.total != null))
    {
        if (this.total == -1) { this.total = eval(jso.photoset.total); }
        if (this.total != eval(jso.photoset.total)) { this.debug ("Photoset contents changed mid-course !"); }

        if (jso.photoset.photo) {
            if (jso.photoset.photo.length == null) {
                // single photo
                this.addPhoto (jso.photoset.photo);
            } else {
                for (var p = 0; p < jso.photoset.photo.length; p ++) {
                    this.addPhoto (jso.photoset.photo[p]);
                }
            }
        }
    }
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
function YFlickrTaggedPhotoset(tag)
{
    /* Common Initialization of YPhotoset superclass */
    this.pages_fetched = 0;             // FLICKR_PHOTOSPERPAGE photos per page
    this.total = -1;                    // total photos in this photoset
    this.photos = [];                   // an array of <photo> objects
    this.slides = [];                   // array of slides, each slide is <div>
    this.active_slide = 0;              // the slide number being shown
    this.view_initialized = false;      // status of slideview initialization


    /* YFlickrTaggedPhotoset initialization */
    this.tag = tag;
    this.psid = "tag" + this.tag;
}

YFlickrTaggedPhotoset.prototype = new YFlickrPhotoset();
YFlickrTaggedPhotoset.prototype.constructor = YFlickrTaggedPhotoset;

YFlickrTaggedPhotoset.prototype.getApiArgs = function()
{
    return [["method", "flickr.photos.search"],
            ["tags", this.tag]
           ];
}

/* Build thine self from the Flickr API result */
YFlickrTaggedPhotoset.prototype.buildPhotoList = function (jso)
{
    if (jso && jso.photos && (jso.photos.total != null))
    {
        if (this.total == -1) { this.total = eval(jso.photos.total); }

        if (jso.photos.photo) {
            if (jso.photos.photo.length == null) {
                // single photo
                this.addPhoto (jso.photos.photo);
            } else {
                for (var p = 0; p < jso.photos.photo.length; p ++) {
                    this.addPhoto (jso.photos.photo[p]);
                }
            }
        }
    }
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

