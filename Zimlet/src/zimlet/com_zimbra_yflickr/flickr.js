// auxiliary functions for dealing with flickr data

// returns the value of the `stat' attribute of the `rsp' node within an xml document
// @xml     an xml document containing the rsp node (usually as the root element)
function flickrapi_responsestatus (xml)
{
    var stat = "";
    var docelem = xml.getDoc().documentElement;

    if ((docelem) && (docelem.tagName == "rsp") && (docelem.attributes.length > 0)) {
        var stat_attr = docelem.attributes.getNamedItem ("stat");
        if (stat_attr) {
            stat = stat_attr.nodeValue;
        }
    }

    return stat;
}

// returns an unsigned url suitable for HTTP GET 
// @endpoint    the base endpoint of the url (eg: a REST endpoint)
// @args        an array of pairs, each pair is an argument name/value
function flickrapi_getunsignedurl (endpoint, args)
{
    var url = endpoint;

    if (args.length > 0) {
        for (i =0; i < args.length; ++i) {
            if (i == 0) { url = url + "?"; }
            else { url = url + "&"; }
            url = url + args[i][0] + "=" + args[i][1];
        }
    }

    return url;
}

// get a signed flickr url
function flickrapi_getsignedurl (endpoint, secret, args)
{
    var url = endpoint;
    var extra = secret;

    args.sort();

    if (args.length > 0) {
        for (i = 0; i < args.length; ++i) {
            if (i == 0) { url = url + "?"; }
            else { url = url + "&"; }
            url = url + args[i][0] + "=" + args[i][1];
            extra = extra + args[i][0] + args[i][1];
        }

        extra = hex_md5(extra);
        url = url + "&api_sig=" + extra;
    }

    return url;
}

// get an api signature
function flickrapi_getapisig (secret,args)
{
    var sig = secret;

    args.sort();
    if (args.length > 0)
    {
        for (var i=0; i<args.length; i++) {
            sig=sig+args[i][0] + args[i][1];
        }

        sig = hex_md5(sig);
    }

    return sig;
}

/* get a human readable error message from a flickr api response object 
   (useful for logging)
 */
function flickrapi_geterrmsg (jso)
{
    var errmsg = "";

    if ((jso != null) && (jso.err != null)) {
        errmsg = errmsg + " Error: " + jso.err.code + ": " + "(" + jso.err.msg + ")";
    }

    return errmsg;
}
