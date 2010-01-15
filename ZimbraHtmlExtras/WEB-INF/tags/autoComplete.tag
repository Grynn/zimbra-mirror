<%--
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2007, 2009 Zimbra, Inc.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.2 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
--%>
<%@ tag body-content="scriptless" %>
<%@ taglib prefix="zm" uri="com.zimbra.zm" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<script type="text/javascript" src="http://yui.yahooapis.com/2.2.2/build/yahoo-dom-event/yahoo-dom-event.js"></script>
<script type="text/javascript" src="http://yui.yahooapis.com/2.2.2/build/connection/connection-min.js"></script>
<script type="text/javascript" src="http://yui.yahooapis.com/2.2.2/build/autocomplete/autocomplete-min.js"></script>

<script type="text/javascript">
    var zhEncode = function(s) {return s == null ? '' : s.replace(/&/g, "&amp;").replace(/</g, "&lt;").replace(/>/g, "&gt;");}
    var zhStartsWith = function(str,query) { return str == null ? false : str.toLowerCase().indexOf(query.toLowerCase()) == 0; }
    var zhFmt = function(str,query,bold) {
        return bold ?
               ["<span class='ZhACB'>",zhEncode(str.substring(0, query.length)), "</span>", zhEncode(str.substring(query.length))].join("")
                : zhEncode(str);
    }
    var myacformat = function(aResultItem, query) {
        var sResult = aResultItem[0];
        var e = aResultItem[1];
        var f = aResultItem[2];
        var l = aResultItem[3];
        var g = aResultItem[4];
        var fs = zhStartsWith(f, query);
        var ls = fs ? false : zhStartsWith(l, query);
        var es = fs || ls ? false : zhStartsWith(e, query);
        var fls, fq, lq;

        if (!(es|ls|fs)) {
            var fl = f + " " + l;
            fls = zhStartsWith(fl, query);
            if (fls) {
                fs = true;
                fq = f;
                ls = true;
                lq = query.substring(fq.length+1);
            }
        }

        if(sResult) {
            return [
                    "\"",
                    zhFmt(f, fls ? fq : query, fs),
                    " ",
                    zhFmt(l, fls ? lq : query, ls),
                    "\"",
                    " &lt;",
                    zhFmt(e,query,es),
                    "&gt;"].join("");
        }
        else {
            return "";
        }
    };
    var myDataSource = new YAHOO.widget.DS_XHR("/zimbra/h/ac", ["Result","m","e","f","l","g"]);
    var initAuto = function(field,container) {
        var ac = new YAHOO.widget.AutoComplete(field, container, myDataSource);
        ac.delimChar = [",",";"];
        ac.queryDelay = 0.25;
        //ac.useShadow = true;
        ac.formatResult = myacformat;
        ac.queryMatchContains = true;
        ac.maxResultsDisplayed = 20;
        ac.allowBrowserAutocomplete = false;
    };

    <jsp:doBody/>

</script>
