/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2011 VMware, Inc.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.3 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * 
 * ***** END LICENSE BLOCK *****
 */
if (! window.jscoverage_report) {
  window.jscoverage_report = function jscoverage_report(dir) {
    if(window._$jscoverage == undefined) return "";
    var pad = function (s) {   
          return '0000'.substr(s.length) + s; 
   };
  var quote = function (s) {   
   return '"' + s.replace(/[\u0000-\u001f"\\\u007f-\uffff]/g, function (c) {  
      switch (c) {
        case '\b':
          return '\\b';
        case '\f':    
         return '\\f';
        case '\n': 
         return '\\n'; 
       case '\r':
          return '\\r'; 
       case '\t':
          return '\\t'; 
       case '"':     
         return '\\"'; 
       case '\\':
          return '\\\\';
       default:   
              return '\\u' + pad(c.charCodeAt(0).toString(16));
        }
      }) + '"';
    };

    var json = [];
    for (var file in window._$jscoverage) { 
     var coverage = window._$jscoverage[file];
      var array = []; 
     var length = coverage.length;
      for (var line = 0; line < length; line++) {
        var value = coverage[line];       
    if (value === undefined || value === null) {
          value = 'null';    
    }else{
          coverage[line] = 0; //stops double counting
        }
        array.push(value);}
      json.push(quote(file) + ':{"coverage":[' + array.join(',') + ']}');    } 
   json = '{' + json.join(',') + '}';
    return json;
  };
}; 
window.jscoverage_report()
