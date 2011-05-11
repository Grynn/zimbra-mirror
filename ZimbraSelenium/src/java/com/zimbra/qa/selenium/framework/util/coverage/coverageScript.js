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
