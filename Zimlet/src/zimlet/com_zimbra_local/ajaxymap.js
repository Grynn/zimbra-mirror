var YAHOO=window.YAHOO||{}; 
YAHOO.namespace=function(_1){ 
	if(!_1||!_1.length){ 
		return null; 
	} 
	var _2=_1.split("."); 
	var _3=YAHOO; 
	for(var i=(_2[0]=="YAHOO")?1:0;i<_2.length;++i){ 
		_3[_2[i]]=_3[_2[i]]||{}; 
		_3=_3[_2[i]]; 
	} 
	return _3; 
}; 

YAHOO.namespace("util"); 
YAHOO.namespace("widget"); 
YAHOO.namespace("example"); 
var YMAPPID = "ZimbraMail"; 
