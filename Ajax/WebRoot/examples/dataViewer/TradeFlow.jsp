<%@ page language="java" 
         import="java.lang.*, java.util.*" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <title>Trade Flow</title>
    <style type="text/css">
      <!--
        @import url(../common/img/hiRes/dwtimgs.css);
        @import url(config/img/hiRes/imgs.css);
        @import url(config/style/dv.css);
       -->
    </style>
	<script language="JavaScript">
    	DwtConfigPath = "js/dwt/config";
    </script>
    
    <jsp:include page="../Messages.jsp"/>
    <jsp:include page="../LiquidAjax.jsp"/>
    
	<script type="text/javascript" src="config/msgs/DvMsg_en.js"/></script>
	<script type="text/javascript" src="model/DvModel.js"></script>
	<script type="text/javascript" src="model/DvEvent.js"></script>
	<script type="text/javascript" src="model/DvList.js"></script>
	<script type="text/javascript" src="model/DvItem.js"></script>
	<script type="text/javascript" src="model/DvItemList.js"></script>
	<script type="text/javascript" src="model/DvAttr.js"></script>
	<script type="text/javascript" src="model/DvAttrList.js"></script>
	<script type="text/javascript" src="view/DvFilterPanel.js"></script>
	<script type="text/javascript" src="view/DvListViewActionMenu.js"></script>
	<script type="text/javascript" src="view/DvListView.js"></script>
	<script type="text/javascript" src="view/DvTabView.js"></script>
	<script type="text/javascript" src="controller/DvController.js"></script>

	<script language="JavaScript">
		function launch() {
			DBG = new LsDebug(LsDebug.NONE, null, false);
			if (location.search && (location.search.indexOf("debug=") != -1)) {
				var m = location.search.match(/debug=(\d+)/);
				if (m.length) {
					var num = parseInt(m[1]);
					var level = LsDebug.DBG[num];
					if (level)
						DBG.setDebugLevel(level);
				}
	   		}
			LsImg.setMode(LsImg.SINGLE_IMG);
			var useAlt = (location.search && (location.search.indexOf("useAlt") != -1));

			var attrs = getSampleAttrs();
			var data = getSampleTrades();
			var displayAttrs = useAlt ?
				["Deal Id", "Counterparty", "Trader", "Trade Date", "Effort Status"] :
				["Deal Id", "Product", "Counterparty", "Trade Date", "Price", "Rate", "Trader"];
			var filterAttrs = useAlt ?
				["Product", "Deal Id", "Counterparty", "Trader", "Trade Date", "Effort Status"] :
				["Deal Id", "Product", "Trade Amt", "Counterparty", "Rate",	"Trade Date", "Price", "Next Action",
				 "Doc Status", "Effort Type", "Maturity Date", "Lehman Entity", "Trader", "Has Annotation"];
			var users = ["Kilgore", "Sherilynn"];
			
			var tradeFlow = new DvController(attrs, data, users, displayAttrs, filterAttrs);
		}
		
		// Samples attributes based on sample data set and a couple of screenshots.
		function getSampleAttrs() {

			var attrs = new Array();
			var i = 1;
			
			attrs[i++] = ["Deal Id", "Number", 55];
			attrs[i++] = ["Effort Id", "Number", 60];
			attrs[i++] = ["Counterparty", "StringContains", 210];
			attrs[i++] = ["Next Action", "SingleSelect", 70, ["Create Contact", "Contact Counterparty", "Contact Technology"]];
			attrs[i++] = ["Next Action Date", "DateRange", 70];
		//	attrs[i++] = ["Last Update", "DateRange", 70];
			attrs[i++] = ["Trade Date", "DateRange", 70];
		//	attrs[i++] = ["Creation Date", "DateRange", 70];
			attrs[i++] = ["Effort Type", "SingleSelect", 70, ["New Deal", "Partial Termination", "Full Termination"]];
			attrs[i++] = ["Effort Status", "SingleSelect", 120, ["In progress"]];
			attrs[i++] = ["Doc Status", "SingleSelect", 70, ["DTCC Not Matched", "DTCC Not Matched-Error", "Confirmation Not Sent"]];
			attrs[i++] = ["Maturity Date", "DateRange", 70];
		//	attrs[i++] = ["Option Exp Date", "DateRange", 70];
		//	attrs[i++] = ["Other Deal Id", "Number", 70];
			attrs[i++] = ["Owner", "StringExact", 50];
			attrs[i++] = ["User Group", "StringExact", 50];
			attrs[i++] = ["Title", "StringContains", 70];
			attrs[i++] = ["Lehman Entity", "SingleSelect", 100, ["Lehman Brothers Special Financing Inc.", "Lehman Brothers International (Europe)"]];
			attrs[i++] = ["Business", "StringContains", 70];
			attrs[i++] = ["Trader", "StringExact", 70];
			attrs[i++] = ["Has Annotation", "Boolean", 50];
		//	attrs[i++] = ["Has Log", "Boolean", 50];
		//	attrs[i++] = ["Risk Loc", "StringExact", 20];
		//	attrs[i++] = ["Orig Loc", "StringExact", 20];
		//	attrs[i++] = ["DE Annotation ID", "Number", 10];
		//	attrs[i++] = ["DE Log ID", "Number", 10];
		//	attrs[i++] = ["Effort Status ID", "Number", 10];
		//	attrs[i++] = ["Legacy Status", "SingleSelect", 30, ["In Progress"]];
		//	attrs[i++] = ["DE Owner ID", "Number", 10];
		//	attrs[i++] = ["DE Owner User Group ID", "Number", 10];
		//	attrs[i++] = ["DE Locator", "StringExact", 40];

			// from screenshots	
			attrs[i++] = ["Product", "MultipleSelect", 120];
			attrs[i++] = ["Trade Amt", "NumberRange", 70];
			attrs[i++] = ["Rate", "NumberRangeBounded", 55, [0, 11]];
			attrs[i++] = ["Price", "NumberRange", 60];

			return attrs;
		}

		// Trades from the sample data set. A few fields (product, trade amt, rate, and price) have been
		// added to support the alternate view.
		function getSampleTrades() {

			var trades = new Array();
			var i = 0;

			trades[i++] = [2098507,590848,"Bank of America  N.A. - Head Office","Contact Counterparty","3/7/2005","3/4/2005","New Deal","In progress","DTCC Not Matched",,"Gabriel  Jamie","DTCC-NY","New Deal","Lehman Brothers Special Financing Inc.","SCT","maaspesi","Y","Asset Swap",500000,6.375,120.0305];
			trades[i++] = [2054995,591650,"JP Morgan Chase Bank  NA - London",,"3/9/2005","1/7/2005","Partial Termination","In progress","DTCC Not Matched",,"Gabriel  Jamie","DTCC-NY","Partial Termination","Lehman Brothers Special Financing Inc.","SCT","magunara","Y","Asset Swap",300000,6.875,45.9165];
					trades[i++] = [2103168,594223,"Merrill Lynch International - Head Office","Contact Technology","3/11/2005","3/10/2005","New Deal","In progress","DTCC Not Matched-Error",,"Gabriel  Jamie","DTCC-NY","New Deal","Lehman Brothers International (Europe)","SCT","pejenkin","Y","CD",350000,7.1,45.9165];
			trades[i++] = [2104009,594959,"Merrill Lynch International - Head Office","Contact Counterparty","3/14/2005","1/28/2005","New Deal","In progress","DTCC Not Matched",,"Gabriel  Jamie","DTCC-NY","New Deal","Lehman Brothers Special Financing Inc.","SCT","maaspesi","Y","Cap/Floor",350000,7.25,143.2465];
			trades[i++] = [2104006,595023,"Merrill Lynch International - Head Office","Contact Counterparty","3/14/2005","1/28/2005","New Deal","In progress","DTCC Not Matched",,"Gabriel  Jamie","DTCC-NY","New Deal","Lehman Brothers Special Financing Inc.","SCT","maaspesi","Y","CD",600000,7.1,105.3775];
			trades[i++] = [2106642,597085,"Merrill Lynch International - Head Office","Contact Counterparty","3/16/2005","3/15/2005","New Deal","In progress","DTCC Not Matched",,"Gabriel  Jamie","DTCC-NY","New Deal","Lehman Brothers Special Financing Inc.","SCT","kvandam","Y","Asset Swap",700000,10.7,116.1665];
			trades[i++] = [2106643,597332,"JP Morgan Chase Bank  NA - Head Office","Contact Counterparty",,"3/15/2005","New Deal","In progress","DTCC Not Matched",,"Gabriel  Jamie","DTCC-NY","New Deal","Lehman Brothers Special Financing Inc.",,"mnozuki","Y","CD",600000,0,105.3775];
			trades[i++] = [2107776,597996,"JP Morgan Chase Bank  NA - Head Office","Contact Counterparty","3/17/2005","3/16/2005","New Deal","In progress","DTCC Not Matched",,"Gabriel  Jamie","DTCC-NY","New Deal","Lehman Brothers Special Financing Inc.","SCT","kvandam","Y","Convertible Spread",600000,7.25,45.9165];
			trades[i++] = [595268,598020,"Bank of America  N.A. - Head Office","Contact Counterparty","3/17/2005","10/4/2004","Full Termination","In progress","DTCC Not Matched",,"Gabriel  Jamie","DTCC-NY","Full Termination","Lehman Brothers Special Financing Inc.","SCT","lturnof","Y","Asset Swap",700000,5.875,116.1665];
			trades[i++] = [2113111,602359,"Merrill Lynch International - Head Office","Contact Counterparty","3/23/2005","3/22/2005","New Deal","In progress","DTCC Not Matched",,"Gabriel  Jamie","DTCC-NY","New Deal","Lehman Brothers Special Financing Inc.","SCT","bsekino","N","Convertible Spread",600000,6.875,45.9165];
			trades[i++] = [2113137,602364,"JP Morgan Chase Bank  NA - Head Office","Contact Technology","3/23/2005","3/21/2005","New Deal","In progress","DTCC Not Matched-Error",,"Gabriel  Jamie","DTCC-NY","New Deal","Lehman Brothers Special Financing Inc.","SCT","kvandam","Y","Asset Swap",700000,7.1,106.1325];
			trades[i++] = [2115796,604618,"JP Morgan Chase Bank  NA - Head Office","Contact Counterparty","3/25/2005","3/24/2005","New Deal","In progress","DTCC Not Matched",,"Gabriel  Jamie","DTCC-NY","New Deal","Lehman Brothers Special Financing Inc.","SCT","magunara","Y","Convertible Spread",1000000,7.125,102];
			trades[i++] = [2116164,604964,"Merrill Lynch International - Head Office","Contact Counterparty","3/29/2005","9/13/2004","New Deal","In progress","DTCC Not Matched",,"Gabriel  Jamie","DTCC-NY","New Deal","Lehman Brothers Special Financing Inc.","SCT","kanderse","Y","Convertible Spread",700000,7.25,120.0305];
			trades[i++] = [2116165,604969,"Merrill Lynch International - Head Office","Contact Counterparty","3/29/2005","9/13/2004","New Deal","In progress","DTCC Not Matched",,"Gabriel  Jamie","DTCC-NY","New Deal","Lehman Brothers Special Financing Inc.","SCT","maaspesi","N","CD",600000,10.7,45.9165];
			trades[i++] = [2118664,606840,"Merrill Lynch International - Head Office","Contact Counterparty","3/31/2005","3/30/2005","New Deal","In progress","DTCC Not Matched",,"Gabriel  Jamie","DTCC-NY","New Deal","Lehman Brothers Special Financing Inc.","SCT","maaspesi","Y","Cap/Floor",500000,6.875,120.0305];
			trades[i++] = [2119037,608128,"Merrill Lynch International - Head Office","Contact Counterparty","4/1/2005","3/29/2005","New Deal","In progress","DTCC Not Matched",,"Gabriel  Jamie","DTCC-NY","New Deal","Lehman Brothers Special Financing Inc.","SCT","qgambrel","Y","Asset Swap",700000,10.7,105.3775];
			trades[i++] = [2120550,608227,"Merrill Lynch International - Head Office","Contact Counterparty","4/1/2005","3/31/2005","New Deal","In progress","DTCC Not Matched",,"Gabriel  Jamie","DTCC-NY","New Deal","Lehman Brothers Special Financing Inc.","SCT","maaspesi","Y","Asset Swap",300000,6.5,102];
			trades[i++] = [2121443,608826,"Bank of America  N.A. - Head Office","Contact Technology","4/4/2005","4/1/2005","New Deal","In progress","DTCC Not Matched-Error",,"Gabriel  Jamie","DTCC-NY","New Deal","Lehman Brothers Special Financing Inc.","SCT","kanderse","Y","CD",350000,7.1,102];
			trades[i++] = [2121811,609321,"Millennium Partners  L.P. - Head Office","Contact Counterparty","4/4/2005","4/1/2005","New Deal","In progress","DTCC Not Matched",,"Gabriel  Jamie","DTCC-NY","New Deal","Lehman Brothers Special Financing Inc.","SCT","qgambrel","Y","Cap/Floor",500000,6.5,45.9165];
			trades[i++] = [2122850,610164,"JP Morgan Chase Bank  NA - Head Office","Contact Counterparty","4/5/2005","4/4/2005","New Deal","In progress","DTCC Not Matched",,"Gabriel  Jamie","DTCC-NY","New Deal","Lehman Brothers Special Financing Inc.","SCT","magunara","Y","Convertible Spread",6750000,7.125,120.0305];
			trades[i++] = [2122854,610165,"Merrill Lynch International - Head Office","Contact Counterparty","4/5/2005","4/4/2005","New Deal","In progress","DTCC Not Matched",,"Gabriel  Jamie","DTCC-NY","New Deal","Lehman Brothers Special Financing Inc.","SCT","qgambrel","N","Convertible Spread",6750000,6.375,102];
			trades[i++] = [2124304,611407,"Merrill Lynch International - Head Office","Contact Counterparty","4/6/2005","4/5/2005","New Deal","In progress","DTCC Not Matched",,"Gabriel  Jamie","DTCC-NY","New Deal","Lehman Brothers Special Financing Inc.","SCT","magunara","Y","Asset Swap",6750000,7.1,116.1665];
			trades[i++] = [2125088,612168,"JP Morgan Chase Bank  NA - Head Office","Contact Counterparty","4/7/2005","4/6/2005","New Deal","In progress","DTCC Not Matched",,"Gabriel  Jamie","DTCC-NY","New Deal","Lehman Brothers Special Financing Inc.","SCT","magunara","Y","Asset Swap",1000000,6.375,143.2465];
			trades[i++] = [2125296,612289,"Merrill Lynch International - Head Office","Contact Counterparty","4/7/2005","4/6/2005","New Deal","In progress","DTCC Not Matched",,"Gabriel  Jamie","DTCC-NY","New Deal","Lehman Brothers Special Financing Inc.","SCT","magunara","N","CD",350000,7.125,116.1665];
			trades[i++] = [2125219,612301,"Merrill Lynch International - Head Office","Contact Counterparty","4/7/2005","4/6/2005","New Deal","In progress","DTCC Not Matched",,"Gabriel  Jamie","DTCC-NY","New Deal","Lehman Brothers Special Financing Inc.","SCT","abenson","Y","Convertible Spread",400000,6.875,120.0305];
			trades[i++] = [2126013,612883,"JP Morgan Chase Bank  NA - London","Contact Technology","4/8/2005","4/7/2005","New Deal","In progress","DTCC Not Matched-Error",,"Gabriel  Jamie","DTCC-NY","New Deal","Lehman Brothers Special Financing Inc.","SCT","pejenkin","Y","Convertible Spread",1000000,5.875,143.2465];
			trades[i++] = [2126251,613329,"JP Morgan Chase Bank  NA - Head Office","Contact Counterparty","4/8/2005","4/7/2005","New Deal","In progress","DTCC Not Matched",,"Gabriel  Jamie","DTCC-NY","New Deal","Lehman Brothers Special Financing Inc.","SCT","magunara","N","Cap/Floor",500000,6.5,106.1325];
			trades[i++] = [2126682,613457,"Merrill Lynch International - Head Office","Contact Counterparty","4/8/2005","4/7/2005","New Deal","In progress","DTCC Not Matched",,"Gabriel  Jamie","DTCC-NY","New Deal","Lehman Brothers Special Financing Inc.","SCT","pejenkin","Y","Asset Swap",700000,7.25,116.1665];
			trades[i++] = [2128257,614951,"Bank of America  N.A. - Head Office","Contact Counterparty","4/12/2005","4/11/2005","New Deal","In progress","DTCC Not Matched",,"Gabriel  Jamie","DTCC-NY","New Deal","Lehman Brothers Special Financing Inc.","SCT","kanderse","Y","Cap/Floor",700000,6.375,105.3775];
			trades[i++] = [2130909,616979,"JP Morgan Chase Bank  NA - Head Office","Contact Counterparty","4/14/2005","4/13/2005","New Deal","In progress","DTCC Not Matched",,"Gabriel  Jamie","DTCC-NY","New Deal","Lehman Brothers Special Financing Inc.","SCT","gshavel","Y","Cap/Floor",6750000,6.375,143.2465];
			trades[i++] = [2018704,617039,"JP Morgan Chase Bank  NA - Head Office","Contact Counterparty","4/14/2005","11/5/2004","Full Termination","In progress","DTCC Not Matched",,"Gabriel  Jamie","DTCC-NY","Full Termination","Lehman Brothers Special Financing Inc.","SCT","magunara","Y","Asset Swap",6750000,0,120.0305];
			trades[i++] = [2131091,617111,"JP Morgan Chase Bank  NA - Head Office","Contact Counterparty","4/14/2005","4/13/2005","New Deal","In progress","DTCC Not Matched",,"Gabriel  Jamie","DTCC-NY","New Deal","Lehman Brothers Special Financing Inc.","SCT","kanderse","Y","Asset Swap",1000000,7.125,120.0305];
			trades[i++] = [2131179,617276,"Bank of America  N.A. - Head Office","Contact Counterparty","4/14/2005","4/13/2005","New Deal","In progress","DTCC Not Matched",,"Gabriel  Jamie","DTCC-NY","New Deal","Lehman Brothers Special Financing Inc.","SCT","gshavel","Y","CD",350000,6.375,143.2465];
			trades[i++] = [2131421,617352,"Merrill Lynch International - Head Office","Contact Counterparty","4/14/2005","3/11/2005","New Deal","In progress","DTCC Not Matched",,"Gabriel  Jamie","DTCC-NY","New Deal","Lehman Brothers Special Financing Inc.","SCT","kvandam","Y","Convertible Spread",500000,6.5,143.2465];
			trades[i++] = [2132459,618149,"JP Morgan Chase Bank  NA - Head Office","Contact Counterparty","4/15/2005","4/14/2005","New Deal","In progress","DTCC Not Matched",,"Gabriel  Jamie","DTCC-NY","New Deal","Lehman Brothers Special Financing Inc.","SCT","phickman","Y","Cap/Floor",300000,0,106.1325];
			trades[i++] = [2131777,618264,"JP Morgan Chase Bank (Capital Arbitrage Desk) - Head Office","Contact Counterparty","4/15/2005","4/11/2005","New Deal","In progress","DTCC Not Matched",,"Gabriel  Jamie","DTCC-NY","New Deal","Lehman Brothers Special Financing Inc.","SCT","kanderse","Y","Asset Swap",600000,0,105.3775];
			trades[i++] = [2060702,618313,"JP Morgan Chase Bank (Capital Arbitrage Desk) - Head Office","Contact Technology","4/15/2005","1/14/2005","Full Termination","In progress","DTCC Not Matched-Error",,"Gabriel  Jamie","DTCC-NY","Full Termination","Lehman Brothers Special Financing Inc.","SCT","maaspesi","Y","CD",600000,6.375,45.9165];
			trades[i++] = [2110152,618339,"JP Morgan Chase Bank (Capital Arbitrage Desk) - Head Office","Contact Technology","4/15/2005","3/18/2005","Full Termination","In progress","DTCC Not Matched-Error",,"Gabriel  Jamie","DTCC-NY","Full Termination","Lehman Brothers Special Financing Inc.","SCT","maaspesi","Y","Convertible Spread",600000,6.375,102];
			trades[i++] = [2072471,618340,"JP Morgan Chase Bank (Capital Arbitrage Desk) - Head Office","Contact Technology","4/15/2005","1/31/2005","Full Termination","In progress","DTCC Not Matched-Error",,"Gabriel  Jamie","DTCC-NY","Full Termination","Lehman Brothers Special Financing Inc.","SCT","maaspesi","Y","CD",400000,9.8,102];
			trades[i++] = [2078459,618341,"JP Morgan Chase Bank (Capital Arbitrage Desk) - Head Office","Contact Technology","4/15/2005","2/7/2005","Full Termination","In progress","DTCC Not Matched-Error",,"Gabriel  Jamie","DTCC-NY","Full Termination","Lehman Brothers Special Financing Inc.","SCT","maaspesi","Y","Convertible Spread",500000,0,45.9165];
			trades[i++] = [2096050,618342,"JP Morgan Chase Bank (Capital Arbitrage Desk) - Head Office","Contact Technology","4/15/2005","3/2/2005","Full Termination","In progress","DTCC Not Matched-Error",,"Gabriel  Jamie","DTCC-NY","Full Termination","Lehman Brothers Special Financing Inc.","SCT","maaspesi","Y","Convertible Spread",500000,0,45.9165];
			trades[i++] = [2098188,618343,"JP Morgan Chase Bank (Capital Arbitrage Desk) - Head Office","Contact Technology","4/15/2005","3/4/2005","Full Termination","In progress","DTCC Not Matched-Error",,"Gabriel  Jamie","DTCC-NY","Full Termination","Lehman Brothers Special Financing Inc.","SCT","maaspesi","Y","Convertible Spread",700000,5.875,116.1665];
			trades[i++] = [2099362,618344,"JP Morgan Chase Bank (Capital Arbitrage Desk) - Head Office","Contact Technology","4/15/2005","3/7/2005","Full Termination","In progress","DTCC Not Matched-Error",,"Gabriel  Jamie","DTCC-NY","Full Termination","Lehman Brothers Special Financing Inc.","SCT","maaspesi","Y","Convertible Spread",400000,7.125,120.0305];
			trades[i++] = [2110473,618345,"JP Morgan Chase Bank (Capital Arbitrage Desk) - Head Office","Contact Technology","4/15/2005","3/18/2005","Full Termination","In progress","DTCC Not Matched-Error",,"Gabriel  Jamie","DTCC-NY","Full Termination","Lehman Brothers Special Financing Inc.","SCT","maaspesi","Y","Cap/Floor",500000,7.125,105.3775];
			trades[i++] = [2108450,618346,"JP Morgan Chase Bank (Capital Arbitrage Desk) - Head Office","Contact Technology","4/15/2005","3/17/2005","Full Termination","In progress","DTCC Not Matched-Error",,"Gabriel  Jamie","DTCC-NY","Full Termination","Lehman Brothers Special Financing Inc.","SCT","maaspesi","Y","CD",1000000,7.25,143.2465];
			trades[i++] = [2108387,618347,"JP Morgan Chase Bank (Capital Arbitrage Desk) - Head Office","Contact Technology","4/15/2005","3/17/2005","Full Termination","In progress","DTCC Not Matched-Error",,"Gabriel  Jamie","DTCC-NY","Full Termination","Lehman Brothers Special Financing Inc.","SCT","maaspesi","Y","CD",400000,6.375,102];
			trades[i++] = [2109101,618348,"JP Morgan Chase Bank (Capital Arbitrage Desk) - Head Office","Contact Technology","4/15/2005","3/17/2005","Full Termination","In progress","DTCC Not Matched-Error",,"Gabriel  Jamie","DTCC-NY","Full Termination","Lehman Brothers Special Financing Inc.","SCT","maaspesi","Y","Convertible Spread",400000,7.125,102];
			trades[i++] = [2132333,618368,"Bank of America  N.A. - Head Office","Contact Counterparty","4/15/2005","4/14/2005","New Deal","In progress","DTCC Not Matched",,"Gabriel  Jamie","DTCC-NY","New Deal","Lehman Brothers Special Financing Inc.","SCT","maaspesi","Y","CD",500000,6.375,143.2465];
			trades[i++] = [2132821,618885,"Merrill Lynch International - Head Office","Contact Counterparty","4/18/2005","4/14/2005","New Deal","In progress","DTCC Not Matched",,"Gabriel  Jamie","DTCC-NY","New Deal","Lehman Brothers Special Financing Inc.","SCT","kvandam","Y","CD",600000,5.875,105.3775];
			trades[i++] = [2133748,619705,"Bank of America  N.A. - Head Office","Contact Counterparty","4/19/2005","4/15/2005","New Deal","In progress","DTCC Not Matched",,"Gabriel  Jamie","DTCC-NY","New Deal","Lehman Brothers Special Financing Inc.","SCT","maaspesi","Y","CD",700000,7.125,102];
			trades[i++] = [2134127,619717,"Merrill Lynch International - Head Office","Contact Counterparty","4/19/2005","4/15/2005","New Deal","In progress","DTCC Not Matched",,"Gabriel  Jamie","DTCC-NY","New Deal","Lehman Brothers Special Financing Inc.","SCT","maaspesi","Y","CD",1000000,7.25,120.0305];
			trades[i++] = [2134132,619718,"Merrill Lynch International - Head Office","Contact Counterparty","4/19/2005","4/15/2005","New Deal","In progress","DTCC Not Matched",,"Gabriel  Jamie","DTCC-NY","New Deal","Lehman Brothers Special Financing Inc.","SCT","maaspesi","Y","Asset Swap",300000,10.7,106.1325];
			trades[i++] = [2134340,619815,"Merrill Lynch International - Head Office","Contact Counterparty","4/19/2005","4/15/2005","New Deal","In progress","DTCC Not Matched",,"Gabriel  Jamie","DTCC-NY","New Deal","Lehman Brothers Special Financing Inc.","SCT","kvandam","Y","Convertible Spread",300000,7.1,106.1325];
			trades[i++] = [2134389,619830,"Merrill Lynch International - Head Office","Contact Counterparty","4/19/2005","4/15/2005","New Deal","In progress","DTCC Not Matched",,"Gabriel  Jamie","DTCC-NY","New Deal","Lehman Brothers Special Financing Inc.","SCT","maaspesi","Y","CD",300000,7.1,116.1665];
			trades[i++] = [2134398,619832,"Merrill Lynch International - Head Office","Contact Counterparty","4/19/2005","4/15/2005","New Deal","In progress","DTCC Not Matched",,"Gabriel  Jamie","DTCC-NY","New Deal","Lehman Brothers Special Financing Inc.","SCT","maaspesi","Y","Asset Swap",500000,6.375,105.3775];
			trades[i++] = [2134461,619881,"JP Morgan Chase Bank  NA - Head Office","Contact Counterparty","4/19/2005","4/15/2005","New Deal","In progress","DTCC Not Matched",,"Gabriel  Jamie","DTCC-NY","New Deal","Lehman Brothers Special Financing Inc.","SCT","kvandam","Y","CD",6750000,7.1,120.0305];
			trades[i++] = [2134462,619882,"JP Morgan Chase Bank  NA - Head Office","Contact Counterparty","4/19/2005","4/15/2005","New Deal","In progress","DTCC Not Matched",,"Gabriel  Jamie","DTCC-NY","New Deal","Lehman Brothers Special Financing Inc.","SCT","kvandam","Y","CD",1000000,10.7,116.1665];
			trades[i++] = [2134571,620384,"Merrill Lynch International - Head Office","Contact Counterparty","4/19/2005","4/15/2005","New Deal","In progress","DTCC Not Matched",,"Gabriel  Jamie","DTCC-NY","New Deal","Lehman Brothers Special Financing Inc.","SCT","kvandam","Y","Convertible Spread",350000,6.375,45.9165];
			trades[i++] = [2074307,620593,"Bank of America  N.A. - Head Office","Contact Counterparty","4/19/2005","2/1/2005","Full Termination","In progress","DTCC Not Matched",,"Gabriel  Jamie","DTCC-NY","Full Termination","Lehman Brothers Special Financing Inc.","SCT","magunara","Y","Cap/Floor",1000000,9.8,106.1325];
			trades[i++] = [2135337,620791,"Bank of America  N.A. - Head Office","Contact Counterparty","4/19/2005","4/18/2005","New Deal","In progress","DTCC Not Matched",,"Gabriel  Jamie","DTCC-NY","New Deal","Lehman Brothers Special Financing Inc.","SCT","maaspesi","Y","Cap/Floor",350000,6.875,116.1665];
			trades[i++] = [2135667,620878,"JP Morgan Chase Bank  NA - Head Office","Contact Counterparty","4/19/2005","4/18/2005","New Deal","In progress","DTCC Not Matched",,"Gabriel  Jamie","DTCC-NY","New Deal","Lehman Brothers Special Financing Inc.","SCT","kvandam","Y","CD",6750000,7.1,102];
			trades[i++] = [2137053,621892,"JP Morgan Chase Bank  NA - Head Office","Contact Counterparty","4/20/2005","4/19/2005","New Deal","In progress","DTCC Not Matched",,"Gabriel  Jamie","DTCC-NY","New Deal","Lehman Brothers Special Financing Inc.","SCT","maaspesi","Y","Cap/Floor",1000000,10.7,105.3775];
			trades[i++] = [2137185,621976,"Bank of America  N.A. - Head Office","Contact Counterparty","4/20/2005","4/19/2005","New Deal","In progress","DTCC Not Matched",,"Gabriel  Jamie","DTCC-NY","New Deal","Lehman Brothers Special Financing Inc.","SCT","phickman","Y","Cap/Floor",1000000,7.1,105.3775];
			trades[i++] = [2136996,622027,"JP Morgan Chase Bank  NA - Head Office","Contact Counterparty","4/20/2005","4/19/2005","New Deal","In progress","DTCC Not Matched",,"Gabriel  Jamie","DTCC-NY","New Deal","Lehman Brothers Special Financing Inc.","SCT","pejenkin","Y","CD",1000000,6.5,45.9165];
			trades[i++] = [2137121,622062,"Bank of America  N.A. - Head Office","Contact Technology","4/20/2005","4/19/2005","New Deal","In progress","DTCC Not Matched-Error",,"Gabriel  Jamie","DTCC-NY","New Deal","Lehman Brothers Special Financing Inc.","SCT","maaspesi","Y","Asset Swap",400000,6.875,45.9165];
			trades[i++] = [2138636,622957,"JP Morgan Chase Bank  NA - Head Office","Contact Counterparty","4/21/2005","4/20/2005","New Deal","In progress","DTCC Not Matched",,"Gabriel  Jamie","DTCC-NY","New Deal","Lehman Brothers Special Financing Inc.","SCT","maaspesi","Y","Cap/Floor",300000,10.7,116.1665];
			trades[i++] = [2138204,622983,"Merrill Lynch International - Head Office","Contact Counterparty","4/21/2005","4/20/2005","New Deal","In progress","DTCC Not Matched",,"Gabriel  Jamie","DTCC-NY","New Deal","Lehman Brothers Special Financing Inc.","SCT","maaspesi","Y","Cap/Floor",6750000,7.25,116.1665];
			trades[i++] = [2138206,622985,"Merrill Lynch International - Head Office","Contact Counterparty","4/21/2005","4/20/2005","New Deal","In progress","DTCC Not Matched",,"Gabriel  Jamie","DTCC-NY","New Deal","Lehman Brothers Special Financing Inc.","SCT","maaspesi","Y","Cap/Floor",400000,7.125,106.1325];
			trades[i++] = [2138530,622994,"JP Morgan Chase Bank  NA - Head Office","Contact Counterparty","4/21/2005","4/20/2005","New Deal","In progress","DTCC Not Matched",,"Gabriel  Jamie","DTCC-NY","New Deal","Lehman Brothers Special Financing Inc.","SCT","maaspesi","Y","Cap/Floor",350000,5.875,105.3775];
			trades[i++] = [2138788,623341,"Bank of America  N.A. - Head Office","Contact Counterparty",,"4/20/2005","New Deal","In progress","DTCC Not Matched",,"Gabriel  Jamie","DTCC-NY","New Deal","Lehman Brothers Special Financing Inc.","SCT","pejenkin","Y","Convertible Spread",300000,6.375,106.1325];
			trades[i++] = [2138790,623342,"Bank of America  N.A. - Head Office","Contact Counterparty","4/21/2005","4/20/2005","New Deal","In progress","DTCC Not Matched",,"Gabriel  Jamie","DTCC-NY","New Deal","Lehman Brothers Special Financing Inc.","SCT","pejenkin","Y","CD",1000000,7.25,120.0305];
			trades[i++] = [2138909,623363,"Blue Mountain Credit Alternatives  Master Fund LP - Head Office","Contact Counterparty","4/21/2005","4/20/2005","New Deal","In progress","DTCC Not Matched",,"Gabriel  Jamie","DTCC-NY","New Deal","Lehman Brothers International (Europe)","SCT","maaspesi","Y","Cap/Floor",300000,7.125,120.0305];
			trades[i++] = [2138999,623484,"JP Morgan Chase Bank  NA - Head Office","Contact Counterparty","4/21/2005","4/20/2005","New Deal","In progress","DTCC Not Matched",,"Gabriel  Jamie","DTCC-NY","New Deal","Lehman Brothers Special Financing Inc.","SCT","kvandam","Y","Cap/Floor",500000,0,120.0305];
			trades[i++] = [2137836,623495,"Bank of America  N.A. - Head Office","Contact Counterparty","4/21/2005","4/20/2005","New Deal","In progress","DTCC Not Matched",,"Gabriel  Jamie","DTCC-NY","New Deal","Lehman Brothers Special Financing Inc.","SCT","dkaufman","Y","Asset Swap",1000000,5.875,105.3775];
			trades[i++] = [2137841,623500,"Bank of America  N.A. - Head Office","Contact Counterparty","4/21/2005","4/20/2005","New Deal","In progress","DTCC Not Matched",,"Gabriel  Jamie","DTCC-NY","New Deal","Lehman Brothers Special Financing Inc.","SCT","maaspesi","Y","Convertible Spread",6750000,7.125,102];
			trades[i++] = [2138168,623548,"Blue Mountain Credit Alternatives  Master Fund LP - Head Office","Contact Counterparty","4/21/2005","4/20/2005","New Deal","In progress","DTCC Not Matched",,"Gabriel  Jamie","DTCC-NY","New Deal","Lehman Brothers International (Europe)","SCT","pejenkin","Y","CD",500000,5.875,45.9165];
			trades[i++] = [2137036,623868,"JP Morgan Chase Bank  NA - Head Office","Contact Counterparty","4/22/2005","4/19/2005","Full Termination","In progress","DTCC Not Matched",,"Gabriel  Jamie","DTCC-NY","Full Termination","Lehman Brothers Special Financing Inc.","SCT","magunara","Y","Cap/Floor",500000,5.875,102];
			trades[i++] = [2139769,624086,"JP Morgan Chase Bank  NA - Head Office","Contact Counterparty","4/22/2005","4/21/2005","New Deal","In progress","DTCC Not Matched",,"Gabriel  Jamie","DTCC-NY","New Deal","Lehman Brothers Special Financing Inc.","SCT","maaspesi","Y","Asset Swap",1000000,5.875,45.9165];
			trades[i++] = [2139928,624139,"Merrill Lynch International - Head Office","Contact Counterparty","4/22/2005","4/21/2005","New Deal","In progress","DTCC Not Matched",,"Gabriel  Jamie","DTCC-NY","New Deal","Lehman Brothers Special Financing Inc.","SCT","magunara","Y","Asset Swap",350000,9.8,102];
			trades[i++] = [2138854,624326,"Merrill Lynch International - Head Office","Contact Counterparty","4/22/2005","4/20/2005","New Deal","In progress","DTCC Not Matched",,"Gabriel  Jamie","DTCC-NY","New Deal","Lehman Brothers Special Financing Inc.","SCT","kvandam","Y","Convertible Spread",500000,10.7,120.0305];
			trades[i++] = [2134424,624420,"Millennium Partners  L.P. - Head Office","Contact Counterparty","4/22/2005","4/15/2005","Full Termination","In progress","DTCC Not Matched",,"Gabriel  Jamie","DTCC-NY","Full Termination","Lehman Brothers Special Financing Inc.","SCT","dkaufman","Y","CD",350000,6.375,106.1325];
			trades[i++] = [2140195,624484,"Merrill Lynch International - Head Office","Contact Technology","4/22/2005","4/21/2005","New Deal","In progress","DTCC Not Matched-Error",,"Gabriel  Jamie","DTCC-NY","New Deal","Lehman Brothers Special Financing Inc.","SCT","pejenkin","Y","Cap/Floor",350000,7.125,120.0305];
			trades[i++] = [2140214,624491,"JP Morgan Chase Bank  NA - Head Office","Contact Technology","4/22/2005","4/21/2005","New Deal","In progress","DTCC Not Matched-Error",,"Gabriel  Jamie","DTCC-NY","New Deal","Lehman Brothers Special Financing Inc.","SCT","maaspesi","Y","Cap/Floor",600000,7.25,102];
			trades[i++] = [2139644,624547,"Bank of America  N.A. - Head Office","Contact Counterparty","4/22/2005","12/15/2004","New Deal","In progress","DTCC Not Matched",,"Gabriel  Jamie","DTCC-NY","New Deal","Lehman Brothers Special Financing Inc.","SCT","qgambrel","Y","Cap/Floor",6750000,6.375,116.1665];
			trades[i++] = [2140213,624582,"JP Morgan Chase Bank  NA - Head Office","Contact Technology","4/22/2005","4/21/2005","New Deal","In progress","DTCC Not Matched-Error",,"Gabriel  Jamie","DTCC-NY","New Deal","Lehman Brothers Special Financing Inc.","SCT","pejenkin","Y","Asset Swap",400000,10.7,120.0305];
			trades[i++] = [2140391,624588,"Merrill Lynch International - Head Office","Contact Counterparty","4/22/2005","4/21/2005","New Deal","In progress","DTCC Not Matched",,"Gabriel  Jamie","DTCC-NY","New Deal","Lehman Brothers Special Financing Inc.","SCT","maaspesi","Y","Asset Swap",6750000,6.875,105.3775];
			trades[i++] = [2140394,624589,"Merrill Lynch International - Head Office","Contact Counterparty","4/22/2005","4/21/2005","New Deal","In progress","DTCC Not Matched",,"Gabriel  Jamie","DTCC-NY","New Deal","Lehman Brothers Special Financing Inc.","SCT","lturnof","Y","CD",300000,7.1,45.9165];
			trades[i++] = [2140268,624632,"JP Morgan Chase Bank (Capital Arbitrage Desk) - Head Office","Contact Technology","4/22/2005","4/21/2005","New Deal","In progress","DTCC Not Matched-Error",,"Gabriel  Jamie","DTCC-NY","New Deal","Lehman Brothers Special Financing Inc.","SCT","kvandam","Y","Cap/Floor",350000,7.25,102];
			trades[i++] = [2140349,624639,"Bank of America  N.A. - Head Office","Contact Technology","4/22/2005","4/21/2005","New Deal","In progress","DTCC Not Matched-Error",,"Gabriel  Jamie","DTCC-NY","New Deal","Lehman Brothers Special Financing Inc.","SCT","pejenkin","Y","Convertible Spread",700000,6.875,120.0305];
			trades[i++] = [2140282,624651,"JP Morgan Chase Bank (Capital Arbitrage Desk) - Head Office","Contact Technology","4/22/2005","4/21/2005","New Deal","In progress","DTCC Not Matched-Error",,"Gabriel  Jamie","DTCC-NY","New Deal","Lehman Brothers Special Financing Inc.","SCT","kvandam","Y","CD",700000,6.375,106.1325];
			trades[i++] = [2140487,624654,"JP Morgan Chase Bank  NA - Head Office","Contact Technology","4/22/2005","4/21/2005","New Deal","In progress","DTCC Not Matched-Error",,"Gabriel  Jamie","DTCC-NY","New Deal","Lehman Brothers Special Financing Inc.","SCT","pejenkin","Y","Convertible Spread",700000,6.875,105.3775];
			trades[i++] = [2140516,624662,"JP Morgan Chase Bank  NA - Head Office","Contact Technology","4/22/2005","4/21/2005","New Deal","In progress","DTCC Not Matched-Error",,"Gabriel  Jamie","DTCC-NY","New Deal","Lehman Brothers Special Financing Inc.","SCT","pejenkin","Y","Asset Swap",700000,6.375,106.1325];
			trades[i++] = [2140490,624665,"Bank of America  N.A. - Head Office","Contact Technology","4/22/2005","4/21/2005","New Deal","In progress","DTCC Not Matched-Error",,"Gabriel  Jamie","DTCC-NY","New Deal","Lehman Brothers Special Financing Inc.","SCT","pejenkin","Y","Cap/Floor",1000000,0,105.3775];
			trades[i++] = [2140492,624667,"Bank of America  N.A. - Head Office","Contact Technology","4/22/2005","4/21/2005","New Deal","In progress","DTCC Not Matched-Error",,"Gabriel  Jamie","DTCC-NY","New Deal","Lehman Brothers Special Financing Inc.","SCT","pejenkin","Y","CD",1000000,7.25,116.1665];
			trades[i++] = [2140859,624975,"Bank of America  N.A. - Head Office","Contact Counterparty","4/25/2005","4/22/2005","New Deal","In progress","DTCC Not Matched",,"Gabriel  Jamie","DTCC-NY","New Deal","Lehman Brothers Special Financing Inc.","SCT","maaspesi","Y","Asset Swap",400000,7.25,102];
			trades[i++] = [2141572,625576,"Bank of America  N.A. - Head Office","Contact Counterparty","4/25/2005","4/22/2005","New Deal","In progress","DTCC Not Matched",,"Gabriel  Jamie","DTCC-NY","New Deal","Lehman Brothers Special Financing Inc.","SCT","bsekino","Y","Cap/Floor",6750000,6.875,45.9165];
			trades[i++] = [2141580,625582,"Merrill Lynch International - Head Office","Contact Counterparty","4/25/2005","4/22/2005","New Deal","In progress","DTCC Not Matched",,"Gabriel  Jamie","DTCC-NY","New Deal","Lehman Brothers Special Financing Inc.","SCT","maaspesi","Y","Convertible Spread",1000000,10.7,120.0305];
			trades[i++] = [2141655,625636,"Merrill Lynch International - Head Office","Contact Counterparty","4/25/2005","4/22/2005","New Deal","In progress","DTCC Not Matched",,"Gabriel  Jamie","DTCC-NY","New Deal","Lehman Brothers Special Financing Inc.","SCT","bsekino","Y","CD",350000,6.875,106.1325];
			trades[i++] = [2141350,625646,"JP Morgan Chase Bank  NA - Head Office","Contact Counterparty","4/25/2005","4/22/2005","New Deal","In progress","DTCC Not Matched",,"Gabriel  Jamie","DTCC-NY","New Deal","Lehman Brothers Special Financing Inc.","SCT","kvandam","Y","Convertible Spread",600000,0,105.3775];
			trades[i++] = [2141351,625647,"JP Morgan Chase Bank  NA - Head Office","Contact Counterparty","4/25/2005","4/22/2005","New Deal","In progress","DTCC Not Matched",,"Gabriel  Jamie","DTCC-NY","New Deal","Lehman Brothers Special Financing Inc.","SCT","kvandam","Y","CD",300000,9.8,106.1325];
			trades[i++] = [2141352,625648,"JP Morgan Chase Bank  NA - Head Office","Contact Counterparty","4/25/2005","4/22/2005","New Deal","In progress","DTCC Not Matched",,"Gabriel  Jamie","DTCC-NY","New Deal","Lehman Brothers Special Financing Inc.","SCT","kvandam","Y","Convertible Spread",400000,7.25,120.0305];
			trades[i++] = [2141365,625656,"JP Morgan Chase Bank  NA - Head Office","Contact Counterparty","4/25/2005","4/22/2005","New Deal","In progress","DTCC Not Matched",,"Gabriel  Jamie","DTCC-NY","New Deal","Lehman Brothers Special Financing Inc.","SCT","cmontalv","Y","Asset Swap",6750000,10.7,45.9165];
			trades[i++] = [2141370,625659,"JP Morgan Chase Bank  NA - Head Office","Contact Counterparty","4/25/2005","4/22/2005","New Deal","In progress","DTCC Not Matched",,"Gabriel  Jamie","DTCC-NY","New Deal","Lehman Brothers Special Financing Inc.","SCT","cmontalv","Y","CD",500000,10.7,120.0305];
			trades[i++] = [2141374,625662,"JP Morgan Chase Bank  NA - Head Office","Contact Counterparty","4/25/2005","4/22/2005","New Deal","In progress","DTCC Not Matched",,"Gabriel  Jamie","DTCC-NY","New Deal","Lehman Brothers Special Financing Inc.","SCT","cmontalv","Y","Convertible Spread",6750000,10.7,102];
			trades[i++] = [2141915,625824,"Blue Mountain Credit Alternatives  Master Fund LP - Head Office","Contact Counterparty","4/26/2005","4/25/2005","New Deal","In progress","DTCC Not Matched",,"Gabriel  Jamie","DTCC-NY","New Deal","Lehman Brothers International (Europe)","SCT","qgambrel","Y","Cap/Floor",400000,5.875,120.0305];
			trades[i++] = [2142220,626101,"JP Morgan Chase Bank  NA - Head Office","Contact Counterparty","4/26/2005","4/22/2005","New Deal","In progress","DTCC Not Matched",,"Gabriel  Jamie","DTCC-NY","New Deal","Lehman Brothers Special Financing Inc.","SCT","kanderse","Y","Convertible Spread",600000,7.1,102];
			trades[i++] = [2142156,626158,"JP Morgan Chase Bank  NA - Head Office","Contact Counterparty","4/26/2005","4/25/2005","New Deal","In progress","DTCC Not Matched",,"Gabriel  Jamie","DTCC-NY","New Deal","Lehman Brothers International (Europe)","SCT","kanderse","Y","CD",6750000,9.8,45.9165];
			trades[i++] = [2142154,626212,"JP Morgan Chase Bank  NA - Head Office","Contact Counterparty","4/26/2005","4/25/2005","New Deal","In progress","DTCC Not Matched",,"Gabriel  Jamie","DTCC-NY","New Deal","Lehman Brothers Special Financing Inc.","SCT","pejenkin","Y","Convertible Spread",6750000,9.8,116.1665];
			trades[i++] = [2142095,626262,"JP Morgan Chase Bank (Capital Arbitrage Desk) - Head Office","Contact Counterparty","4/26/2005","4/25/2005","New Deal","In progress","DTCC Not Matched",,"Gabriel  Jamie","DTCC-NY","New Deal","Lehman Brothers Special Financing Inc.","SCT","maaspesi","Y","CD",500000,0,116.1665];
			trades[i++] = [2142381,626326,"Merrill Lynch International - Head Office","Contact Counterparty","4/26/2005","4/25/2005","New Deal","In progress","DTCC Not Matched",,"Gabriel  Jamie","DTCC-NY","New Deal","Lehman Brothers Special Financing Inc.","SCT","kvandam","Y","Cap/Floor",400000,7.25,143.2465];
			trades[i++] = [2142083,626364,"JP Morgan Chase Bank (Capital Arbitrage Desk) - Head Office","Contact Counterparty","4/26/2005","4/25/2005","New Deal","In progress","DTCC Not Matched",,"Gabriel  Jamie","DTCC-NY","New Deal","Lehman Brothers Special Financing Inc.","SCT","kvandam","Y","CD",700000,7.1,106.1325];
			trades[i++] = [2142500,626371,"Bank of America  N.A. - Head Office","Contact Counterparty","4/26/2005","4/25/2005","New Deal","In progress","DTCC Not Matched",,"Gabriel  Jamie","DTCC-NY","New Deal","Lehman Brothers Special Financing Inc.","SCT","kvandam","Y","Cap/Floor",6750000,7.1,106.1325];
			trades[i++] = [2141320,626379,"Swiss Re Financial Products Corporation - Head Office","Contact Counterparty","4/26/2005","4/22/2005","Full Termination","In progress","DTCC Not Matched",,"Gabriel  Jamie","DTCC-NY","Full Termination","Lehman Brothers Special Financing Inc.","SCT","qgambrel","Y","Convertible Spread",400000,7.125,45.9165];
			trades[i++] = [2142614,626511,"Merrill Lynch International - Head Office","Contact Counterparty","4/26/2005","4/22/2005","New Deal","In progress","DTCC Not Matched",,"Gabriel  Jamie","DTCC-NY","New Deal","Lehman Brothers Special Financing Inc.","SCT","pejenkin","Y","CD",700000,5.875,45.9165];
			trades[i++] = [2142955,626933,"Merrill Lynch International - Head Office","Contact Counterparty","4/27/2005","4/26/2005","New Deal","In progress","DTCC Not Matched",,"Gabriel  Jamie","DTCC-NY","New Deal","Lehman Brothers Special Financing Inc.","SCT","maaspesi","Y","CD",300000,10.7,105.3775];
			trades[i++] = [2143291,627004,"Merrill Lynch International - Head Office","Contact Counterparty","4/27/2005","4/26/2005","New Deal","In progress","DTCC Not Matched",,"Gabriel  Jamie","DTCC-NY","New Deal","Lehman Brothers Special Financing Inc.","SCT","kanderse","Y","Cap/Floor",1000000,5.875,105.3775];
			trades[i++] = [2143294,627010,"Wachovia Bank  National Association - Head Office","Contact Technology","4/27/2005","4/26/2005","New Deal","In progress","DTCC Not Matched-Error",,"Gabriel  Jamie","DTCC-NY","New Deal","Lehman Brothers Special Financing Inc.","SCT","pejenkin","Y","CD",400000,6.875,120.0305];
			trades[i++] = [2143488,627276,"CQS Capital Structure Arbitrage Master Fd Limited - Head Office","Contact Counterparty","4/27/2005","4/26/2005","New Deal","In progress","DTCC Not Matched",,"Gabriel  Jamie","DTCC-NY","New Deal","Lehman Brothers International (Europe)","SCT","pejenkin","Y","Convertible Spread",600000,0,106.1325];
			trades[i++] = [2143581,627316,"UBS AG - London","Contact Counterparty","4/27/2005","4/26/2005","New Deal","In progress","DTCC Not Matched",,"Gabriel  Jamie","DTCC-NY","New Deal","Lehman Brothers Special Financing Inc.","SCT","kanderse","Y","CD",350000,6.375,116.1665];
			trades[i++] = [2130528,627345,"Swiss Re Financial Products Corporation - Head Office","Contact Counterparty","4/27/2005","4/13/2005","Full Termination","In progress","DTCC Not Matched",,"Gabriel  Jamie","DTCC-NY","Full Termination","Lehman Brothers Special Financing Inc.","SCT","qgambrel","Y","Asset Swap",500000,6.375,143.2465];
			trades[i++] = [2143595,627359,"HSBC Bank USA  National Association - Head Office","Contact Counterparty","4/27/2005","4/26/2005","New Deal","In progress","DTCC Not Matched",,"Gabriel  Jamie","DTCC-NY","New Deal","Lehman Brothers Special Financing Inc.","SCT","maaspesi","Y","Convertible Spread",700000,7.1,143.2465];
			trades[i++] = [2143554,627365,"Wachovia Bank  National Association - Head Office","Contact Counterparty","4/27/2005","4/26/2005","New Deal","In progress","DTCC Not Matched",,"Gabriel  Jamie","DTCC-NY","New Deal","Lehman Brothers Special Financing Inc.","SCT","maaspesi","Y","Cap/Floor",350000,6.375,106.1325];
			trades[i++] = [2143603,627366,"Wachovia Bank  National Association - Head Office","Contact Technology","4/27/2005","4/26/2005","New Deal","In progress","DTCC Not Matched-Error",,"Gabriel  Jamie","DTCC-NY","New Deal","Lehman Brothers Special Financing Inc.","SCT","kanderse","Y","Convertible Spread",500000,6.875,143.2465];
			trades[i++] = [2143387,627401,"Royal Bank of Scotland PLC - Head Office","Contact Counterparty","4/27/2005","4/26/2005","New Deal","In progress","DTCC Not Matched",,"Gabriel  Jamie","DTCC-NY","New Deal","Lehman Brothers Special Financing Inc.","SCT","dkaufman","Y","Convertible Spread",350000,7.25,106.1325];
			trades[i++] = [2143571,627416,"Citibank N.A. - Head Office","Contact Counterparty","4/27/2005","4/26/2005","New Deal","In progress","DTCC Not Matched",,"Gabriel  Jamie","DTCC-NY","New Deal","Lehman Brothers Special Financing Inc.","SCT","maaspesi","Y","CD",6750000,10.7,106.1325];
			trades[i++] = [2143694,627422,"Wachovia Bank  National Association - Head Office","Contact Technology","4/27/2005","4/26/2005","New Deal","In progress","DTCC Not Matched-Error",,"Gabriel  Jamie","DTCC-NY","New Deal","Lehman Brothers Special Financing Inc.","SCT","maaspesi","Y","CD",350000,7.25,120.0305];
			trades[i++] = [2143378,627474,"Wachovia Bank  National Association - Head Office","Contact Technology","4/27/2005","4/26/2005","New Deal","In progress","DTCC Not Matched-Error",,"Gabriel  Jamie","DTCC-NY","New Deal","Lehman Brothers Special Financing Inc.","SCT","kanderse","Y","Convertible Spread",700000,5.875,116.1665];
			trades[i++] = [2143371,627490,"Wachovia Bank  National Association - Head Office","Contact Counterparty","4/27/2005","4/26/2005","New Deal","In progress","DTCC Not Matched",,"Gabriel  Jamie","DTCC-NY","New Deal","Lehman Brothers Special Financing Inc.","SCT","maaspesi","Y","Convertible Spread",500000,9.8,102];
			trades[i++] = [2143862,627527,"Citibank N.A. - Head Office","Contact Counterparty","4/27/2005","4/26/2005","New Deal","In progress","DTCC Not Matched",,"Gabriel  Jamie","DTCC-NY","New Deal","Lehman Brothers Special Financing Inc.","SCT","rbrown","Y","CD",600000,10.7,143.2465];
			trades[i++] = [2143699,627598,"JP Morgan Chase Bank  NA - Head Office","Contact Technology","4/27/2005","4/26/2005","New Deal","In progress","DTCC Not Matched-Error",,"Gabriel  Jamie","DTCC-NY","New Deal","Lehman Brothers Special Financing Inc.","SCT","kvandam","Y","Convertible Spread",400000,0,102];
			trades[i++] = [2144000,627610,"Wachovia Bank  National Association - Head Office","Contact Technology","4/27/2005","4/26/2005","New Deal","In progress","DTCC Not Matched-Error",,"Gabriel  Jamie","DTCC-NY","New Deal","Lehman Brothers Special Financing Inc.","SCT","maaspesi","Y","CD",1000000,7.125,116.1665];
			trades[i++] = [2144022,627778,"Citibank N.A. - Head Office","Contact Counterparty","4/28/2005","4/26/2005","New Deal","In progress","DTCC Not Matched",,"Gabriel  Jamie","DTCC-NY","New Deal","Lehman Brothers Special Financing Inc.","SCT","maaspesi","Y","Asset Swap",400000,6.5,116.1665];
			trades[i++] = [2144455,627950,"Swiss Re Financial Products Corporation - Head Office","Contact Counterparty","4/28/2005","4/27/2005","New Deal","In progress","DTCC Not Matched",,"Gabriel  Jamie","DTCC-NY","New Deal","Lehman Brothers Special Financing Inc.","SCT","kanderse","Y","Asset Swap",300000,6.5,45.9165];
			trades[i++] = [2144353,627952,"UBS AG - London","Contact Counterparty","4/28/2005","4/27/2005","New Deal","In progress","DTCC Not Matched",,"Gabriel  Jamie","DTCC-NY","New Deal","Lehman Brothers Special Financing Inc.","SCT","pejenkin","Y","Asset Swap",700000,7.125,45.9165];
			trades[i++] = [2144478,627975,"Wachovia Bank  National Association - Head Office","Contact Technology","4/28/2005","4/27/2005","New Deal","In progress","DTCC Not Matched-Error",,"Gabriel  Jamie","DTCC-NY","New Deal","Lehman Brothers Special Financing Inc.","SCT","pejenkin","Y","Asset Swap",1000000,7.1,120.0305];
			trades[i++] = [2144482,627977,"Wachovia Bank  National Association - Head Office","Contact Technology","4/28/2005","4/27/2005","New Deal","In progress","DTCC Not Matched-Error",,"Gabriel  Jamie","DTCC-NY","New Deal","Lehman Brothers Special Financing Inc.","SCT","pejenkin","Y","Convertible Spread",500000,6.5,102];
			trades[i++] = [2144392,628032,"JP Morgan Chase Bank  NA - Head Office","Contact Counterparty","4/28/2005","4/27/2005","New Deal","In progress","DTCC Not Matched",,"Gabriel  Jamie","DTCC-NY","New Deal","Lehman Brothers Special Financing Inc.","SCT","phickman","Y","Asset Swap",700000,7.125,143.2465];
			trades[i++] = [2144616,628220,"JP Morgan Chase Bank  NA - Head Office","Contact Counterparty","4/28/2005","4/27/2005","New Deal","In progress","DTCC Not Matched",,"Gabriel  Jamie","DTCC-NY","New Deal","Lehman Brothers Special Financing Inc.","SCT","kanderse","Y","CD",6750000,6.875,116.1665];
			trades[i++] = [2144564,628221,"UBS AG - London","Contact Counterparty","4/28/2005","4/27/2005","New Deal","In progress","DTCC Not Matched",,"Gabriel  Jamie","DTCC-NY","New Deal","Lehman Brothers Special Financing Inc.","SCT","qgambrel","Y","Cap/Floor",300000,6.5,116.1665];
			trades[i++] = [2144625,628227,"Wachovia Bank  National Association - Head Office","Contact Counterparty","4/28/2005","4/27/2005","New Deal","In progress","DTCC Not Matched",,"Gabriel  Jamie","DTCC-NY","New Deal","Lehman Brothers Special Financing Inc.","SCT","qgambrel","Y","Cap/Floor",6750000,5.875,105.3775];
			trades[i++] = [2144788,628280,"JP Morgan Chase Bank  NA - Head Office","Contact Counterparty","4/28/2005","4/27/2005","New Deal","In progress","DTCC Not Matched",,"Gabriel  Jamie","DTCC-NY","New Deal","Lehman Brothers Special Financing Inc.","SCT","kanderse","Y","CD",500000,7.1,105.3775];
			trades[i++] = [2143690,628329,"Citibank N.A. - Head Office","Contact Counterparty","4/28/2005","4/26/2005","New Deal","In progress","DTCC Not Matched",,"Gabriel  Jamie","DTCC-NY","New Deal","Lehman Brothers Special Financing Inc.","SCT","kvandam","Y","Asset Swap",1000000,0,106.1325];
			trades[i++] = [2144776,628331,"UBS AG - London","Contact Counterparty","4/28/2005","4/27/2005","New Deal","In progress","DTCC Not Matched",,"Gabriel  Jamie","DTCC-NY","New Deal","Lehman Brothers Special Financing Inc.","SCT","pejenkin","Y","Asset Swap",6750000,6.5,143.2465];
			trades[i++] = [2144963,628367,"Wachovia Bank  National Association - Head Office","Contact Technology","4/28/2005","4/27/2005","New Deal","In progress","DTCC Not Matched-Error",,"Gabriel  Jamie","DTCC-NY","New Deal","Lehman Brothers Special Financing Inc.","SCT","kanderse","Y","CD",500000,6.5,120.0305];
			trades[i++] = [2144707,628455,"Swiss Re Financial Products Corporation - Head Office","Contact Technology","4/28/2005","4/27/2005","New Deal","In progress","DTCC Not Matched-Error",,"Gabriel  Jamie","DTCC-NY","New Deal","Lehman Brothers Special Financing Inc.","SCT","kvandam","Y","Cap/Floor",6750000,5.875,105.3775];
			trades[i++] = [2145157,628492,"JP Morgan Chase Bank  NA - Head Office","Contact Counterparty","4/28/2005","4/27/2005","New Deal","In progress","DTCC Not Matched",,"Gabriel  Jamie","DTCC-NY","New Deal","Lehman Brothers Special Financing Inc.","SCT","kanderse","Y","Cap/Floor",600000,7.25,116.1665];
			trades[i++] = [2145031,628573,"HSBC Bank USA  National Association - Head Office","Contact Counterparty","4/28/2005","4/27/2005","New Deal","In progress","DTCC Not Matched",,"Gabriel  Jamie","DTCC-NY","New Deal","Lehman Brothers Special Financing Inc.","SCT","kanderse","Y","Cap/Floor",400000,9.8,102];
			trades[i++] = [2127652,628603,"UBS AG - London","Contact Counterparty","4/28/2005","4/8/2005","Full Termination","In progress","DTCC Not Matched",,"Gabriel  Jamie","DTCC-NY","Full Termination","Lehman Brothers Special Financing Inc.","SCT","qgambrel","Y","Convertible Spread",400000,7.125,120.0305];
			trades[i++] = [2145232,628634,"Citibank N.A. - Head Office","Contact Counterparty","4/28/2005","4/27/2005","New Deal","In progress","DTCC Not Matched",,"Gabriel  Jamie","DTCC-NY","New Deal","Lehman Brothers Special Financing Inc.","SCT","kanderse","Y","Convertible Spread",6750000,7.125,102];
			trades[i++] = [2145241,628635,"Citibank N.A. - Head Office","Contact Counterparty","4/28/2005","4/27/2005","New Deal","In progress","DTCC Not Matched",,"Gabriel  Jamie","DTCC-NY","New Deal","Lehman Brothers Special Financing Inc.","SCT","kanderse","Y","Asset Swap",400000,0,120.0305];
			trades[i++] = [2145240,628636,"Citibank N.A. - Head Office","Contact Counterparty","4/28/2005","4/27/2005","New Deal","In progress","DTCC Not Matched",,"Gabriel  Jamie","DTCC-NY","New Deal","Lehman Brothers Special Financing Inc.","SCT","bmaggio","Y","Convertible Spread",400000,6.5,106.1325];
			trades[i++] = [2144817,628651,"UBS AG - London","Contact Counterparty","4/28/2005","4/27/2005","New Deal","In progress","DTCC Not Matched",,"Gabriel  Jamie","DTCC-NY","New Deal","Lehman Brothers Special Financing Inc.","SCT","gshavel","Y","CD",6750000,7.125,120.0305];
			trades[i++] = [599241,628655,"Citadel Equity Fund Ltd. - Head Office","Contact Counterparty","4/28/2005","10/8/2004","Full Termination","In progress","DTCC Not Matched",,"Gabriel  Jamie","DTCC-NY","Full Termination","Lehman Brothers International (Europe)","SCT","maaspesi","Y","Cap/Floor",300000,7.1,105.3775];
			trades[i++] = [2145138,629025,"UBS AG - London","Contact Counterparty","4/29/2005","4/27/2005","New Deal","In progress","DTCC Not Matched",,"Gabriel  Jamie","DTCC-NY","New Deal","Lehman Brothers Special Financing Inc.","SCT","maaspesi","Y","CD",350000,5.875,143.2465];
			trades[i++] = [2145744,629056,"JP Morgan Chase Bank  NA - Head Office","Contact Counterparty","4/29/2005","4/27/2005","New Deal","In progress","DTCC Not Matched",,"Gabriel  Jamie","DTCC-NY","New Deal","Lehman Brothers Special Financing Inc.","SCT","kanderse","Y","Convertible Spread",500000,7.1,143.2465];
			trades[i++] = [2145754,629063,"UBS AG - London","Contact Counterparty","4/29/2005","4/28/2005","New Deal","In progress","DTCC Not Matched",,"Gabriel  Jamie","DTCC-NY","New Deal","Lehman Brothers Special Financing Inc.","SCT","bsekino","Y","CD",1000000,6.875,105.3775];
			trades[i++] = [2145788,629083,"HSBC Bank USA  National Association - Head Office","Contact Counterparty","4/29/2005","4/28/2005","New Deal","In progress","DTCC Not Matched",,"Gabriel  Jamie","DTCC-NY","New Deal","Lehman Brothers Special Financing Inc.","SCT","qgambrel","Y","Cap/Floor",6750000,9.8,120.0305];
			trades[i++] = [2145917,629144,"UBS AG - London","Contact Counterparty","4/29/2005","4/27/2005","New Deal","In progress","DTCC Not Matched",,"Gabriel  Jamie","DTCC-NY","New Deal","Lehman Brothers Special Financing Inc.","SCT","kanderse","Y","Asset Swap",700000,0,120.0305];
			trades[i++] = [2145920,629150,"UBS AG - London","Contact Counterparty","4/29/2005","4/27/2005","New Deal","In progress","DTCC Not Matched",,"Gabriel  Jamie","DTCC-NY","New Deal","Lehman Brothers Special Financing Inc.","SCT","qgambrel","Y","Cap/Floor",600000,7.1,45.9165];
			trades[i++] = [2121031,609439,"Merrill Lynch International - Head Office","Contact Counterparty","4/6/2005","3/31/2005","New Deal","In progress","DTCC Not Matched",,"Gabriel  Jamie","DTCC-NY","New Deal","Lehman Brothers Special Financing Inc.","SCT","kvandam","Y","CD",300000,5.875,102];
			trades[i++] = [2121032,609440,"Merrill Lynch International - Head Office","Contact Counterparty","4/6/2005","3/31/2005","New Deal","In progress","DTCC Not Matched",,"Gabriel  Jamie","DTCC-NY","New Deal","Lehman Brothers Special Financing Inc.","SCT","kvandam","Y","Cap/Floor",6750000,6.375,102];
			trades[i++] = [2121033,609442,"Merrill Lynch International - Head Office","Contact Counterparty","4/6/2005","3/31/2005","New Deal","In progress","DTCC Not Matched",,"Gabriel  Jamie","DTCC-NY","New Deal","Lehman Brothers Special Financing Inc.","SCT","kvandam","Y","Convertible Spread",600000,9.8,102];
			trades[i++] = [2121034,609443,"Merrill Lynch International - Head Office","Contact Counterparty","4/6/2005","3/31/2005","New Deal","In progress","DTCC Not Matched",,"Gabriel  Jamie","DTCC-NY","New Deal","Lehman Brothers Special Financing Inc.","SCT","kvandam","Y","Convertible Spread",300000,6.875,143.2465];
			trades[i++] = [2126673,613453,"HSBC Bank USA  National Association - Head Office","Contact Counterparty","4/11/2005","4/7/2005","New Deal","In progress","DTCC Not Matched",,"Gabriel  Jamie","NY Vanilla SCT","New Deal","Lehman Brothers Special Financing Inc.","SCT","phickman","Y","Convertible Spread",500000,6.875,116.1665];
			trades[i++] = [2141420,628621,"Wachovia Bank  National Association - Head Office","Create Contract","4/29/2005","4/22/2005","Partial Termination","In progress","Confirmation Not Sent",,"Gabriel  Jamie","DTCC-NY","Partial Termination","Lehman Brothers Special Financing Inc.","SCT","qgambrel","Y","CD",700000,7.125,143.2465];

			return trades;
		}

	    LsCore.addOnloadListener(launch);
	</script>

</head>
    <body>
    </body>
</html>
