/*
 * Copyright (C) 2006, The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


/**
* @class
* This static class  allows you to draw "interesting" borders
*		(eg: borders that are composed of multiple images)
*
*	Note that images for the border are used in the same style as AjxImg.
*
* @author Owen Williams
*/

function DwtBorder() {
}

DwtBorder._borderTemplates = {};

DwtBorder.getBorderTemplate = 
function(style) {
	return this._borderTemplates[style];
};

DwtBorder.getBorderHtml = 
function (style, substitutions, innerDivId) {
	return AjxBuffer.append(
				this.getBorderStartHtml(style, substitutions),
				(innerDivId ? "<div id=" + innerDivId + "></div>" : ""),
				this.getBorderEndHtml(style, substitutions)
			);
};

DwtBorder.getBorderStartHtml = 
function(style, substitutions) {
	var template = this._borderTemplates[style];
	if (template == null) {
		DBG.println("DwtBorder.getBorderStartHtml(",style,"): no border template found.");
		return "";
	}

	var html = template.start;
	if (substitutions != null) {
		html = DwtBorder.performSubstitutions(html, substitutions);
	}
	return html;
};

DwtBorder.getBorderEndHtml = 
function(style, substitutions) {
	var template = this._borderTemplates[style];
	if (template == null || template == "") return "";

	var html = template.end;
	if (substitutions != null) {
		html = DwtBorder.performSubstitutions(html, substitutions);
	}
	return html;
};

DwtBorder.getBorderHeight = 
function(style) {
	var template = this._borderTemplates[style];
	return template ? template.height : 0;
};

DwtBorder.getBorderWidth = 
function(style) {
	var template = this._borderTemplates[style];
	return template ? template.width : 0;
};

DwtBorder.performSubstitutions = 
function (html, substitutions) {
	for (var prop in substitutions) {
		var str = "<!--$" + prop + "-->";
		if (html.indexOf(str)) {
			html = html.split(str).join(substitutions[prop]);
		}
		// MOW: Why is this here?  This will make substitution twice as slow... do we need it?
		var str = "{$"+prop+"}";
		if (html.indexOf(str)) {
			html = html.split(str).join(substitutions[prop]);
		}
	}
	return html;
};

DwtBorder.registerBorder = 
function (style, template) {
	this._borderTemplates[style] = template;
};

DwtBorder.registerBorder(
	"1pxBlack",
	{
		start:"<div style='border:1px solid black'>",
		end:"</div>",
		width:2,
		height:2
	}
);	


DwtBorder.registerBorder(
	"DwtDialog", 
	{
		start: AjxBuffer.concat(
			"<div class='DwtDialog WindowOuterContainer'>",
				"<table cellspacing=0 cellpadding=0 style='cursor:move;'>",
					"<tr id='{$titleId}'>",
						"<td class='minWidth'><!--$icon--><\/td>",
						"<td id='{$titleTextId}' class='DwtDialogTitle'>{$title}</td>",
						"<td class='minWidth'><div class='{$closeIcon2}'></div></td>",
						"<td class='minWidth'><div class='{$closeIcon1}'></div></td>",
					"</tr>",
					"<tr>",
						"<td class='DwtDialogBody WindowInnerContainer' colspan='3'>"
		),
		
		end: AjxBuffer.concat(
						"</td>",
					"</tr>",
				"</table>",
			"</div>"
		),
		width:20,
		height:32
	}
);

DwtBorder.registerBorder(
	"DwtSemiModalDialog", 
	{
		start: AjxBuffer.concat(
			"<div class='DwtDialog LightWindowOuterContainer'>",
				"<table class='full_size' cellspacing=0 cellpadding=0 style='cursor:move;'>",
					"<tr id='{$id}'>",
						"<td class='minWidth'><!--$icon--></td>",
						"<td id='{$id}_title' class='DwtDialogTitle' width='*'>{$title}</td>",
						"<td id='{$id}_close' class='minWidth'></td>",
					"</tr>",
					"<tr>",
						"<td id='{$id}_contents' class='DwtDialogBody LIghtWindowInnerContainer full_size' colspan='3'>"
		),
		
		end: AjxBuffer.concat(
						"</td>",
					"</tr>",
				"</table>",
			"</div>"
		),
		width:20,
		height:32
	}
);


DwtBorder.registerBorder(
	"DwtToolTip", 
	{
		start: AjxBuffer.concat(
			"<div class='DwtToolTip LightWindowOuterContainer'>",
				"<div id='{$id}TopPointer' class='DwtToolTipTopPointer'>",
					"<center>",
						"<div class='DwtToolTipPointerRow' style='width:1px;'>&nbsp;</div>",
						"<div class='DwtToolTipPointerRow' style='width:3px;'>&nbsp;</div>",
						"<div class='DwtToolTipPointerRow' style='width:5px;'>&nbsp;</div>",
						"<div class='DwtToolTipPointerRow' style='width:7px;'>&nbsp;</div>",
						"<div class='DwtToolTipPointerRow' style='width:9px;'>&nbsp;</div>",
						"<div class='DwtToolTipPointerRow' style='width:11px;'>&nbsp;</div>",
						"<div class='DwtToolTipPointerRow' style='width:13px;'>&nbsp;</div>",
						"<div class='DwtToolTipPointerRow' style='width:15px;'>&nbsp;</div>",
						"<div class='DwtToolTipPointerRow' style='width:17px;'>&nbsp;</div>",
						"<div class='DwtToolTipPointerRow' style='width:19px;'>&nbsp;</div>",
						"<div class='DwtToolTipPointerRow' style='width:21px;'>&nbsp;</div>",
					"</center>",
				"</div>",
				"<div id='{$id}Contents' class='DwtToolTipBody'>"
		),
		
		end: AjxBuffer.concat(
				"</div>",
				"<div id='{$id}BottomPointer' class='DwtToolTipBottomPointer'>",
					"<center>",
						"<div class='DwtToolTipPointerRow' style='width:21px;'>&nbsp;</div>",
						"<div class='DwtToolTipPointerRow' style='width:19px;'>&nbsp;</div>",
						"<div class='DwtToolTipPointerRow' style='width:17px;'>&nbsp;</div>",
						"<div class='DwtToolTipPointerRow' style='width:15px;'>&nbsp;</div>",
						"<div class='DwtToolTipPointerRow' style='width:13px;'>&nbsp;</div>",
						"<div class='DwtToolTipPointerRow' style='width:11px;'>&nbsp;</div>",
						"<div class='DwtToolTipPointerRow' style='width:9px;'>&nbsp;</div>",
						"<div class='DwtToolTipPointerRow' style='width:7px;'>&nbsp;</div>",
						"<div class='DwtToolTipPointerRow' style='width:5px;'>&nbsp;</div>",
						"<div class='DwtToolTipPointerRow' style='width:3px;'>&nbsp;</div>",
						"<div class='DwtToolTipPointerRow' style='width:1px;'>&nbsp;</div>",
					"</center>",
				"</div>",
			"</div>"
		),
		width:5,	// pointer should be at least this many pixels away from ends of the body
		height:5
	}
);


DwtBorder.registerBorder(
	"DwtVerticalSash",
	{	
		start: AjxBuffer.concat(
			"<div class='DwtVerticalSash'>",
				"<center>",
					"<table cellspacing=0 width=20><tr>",
						"<td class='DwtVerticalSashContents'>&deg;</td>",
						"<td class='DwtVerticalSashContents'>&deg;</td>",
						"<td class='DwtVerticalSashContents'>&deg;</td>",
						"<td class='DwtVerticalSashContents'>&deg;</td>",
						"<td class='DwtVerticalSashContents'>&deg;</td>",
					"</tr></table>",
				"</center>",
			"</div>"
			),
		end:"",
		width:0,
		height:0
	}
);


DwtBorder.registerBorder(
	"DwtHorizontalSash",
	{	
		start: AjxBuffer.concat(
			"<div class='DwtHorizontalSash'>",
				"<table cellspacing=0 cellpadding=1 height='90%'>",
					"<tr><td height='50%' class='DwtHorizonalSashContents'>&nbsp;</td></tr>",
					"<tr><td class='DwtHorizonalSashContents'>&deg;</td></tr>",
					"<tr><td class='DwtHorizonalSashContents'>&deg;</td></tr>",
					"<tr><td class='DwtHorizonalSashContents'>&deg;</td></tr>",
					"<tr><td class='DwtHorizonalSashContents'>&deg;</td></tr>",
					"<tr><td class='DwtHorizonalSashContents'>&deg;</td></tr>",
					"<tr><td height='50%' class='DwtHorizonalSashContents'>&nbsp;</td></tr>",
				"</table>",
			"</div>"
			),
		end:"",
		width:0,
		height:0
	}
);


		

		
	

DwtBorder.registerBorder(
	"calendar_appt",
	{	
		start:AjxBuffer.concat(	
			"<div id='<!--$id-->_body' class='appt_body <!--$bodyColor-->'>",
				"<table style='width:100%;height:100%'cellspacing=0 cellpadding=2>",
				"<tr class='<!--$headerColor-->'>",
					"<td class=appt<!--$newState-->_time id='<!--$id-->_st'><!--$starttime--></td>",
					"<td class=appt_status-<!--$statusKey--> style='text-align:right'><!--$status--></td>",
//					"<td class=appt<!--$newState-->_tag><!--$tag--></td>",
				"</tr>",
				"<tr valign=top>",
					"<td colspan=2 class=appt<!--$newState-->_name style='height:100%'>",
						"<!--$name-->",
						"<BR>",
						"<!--$location-->",
					"</td>",
				"<tr>",
					"<td colspan=2 class=appt_end_time id='<!--$id-->_et'><!--$endtime--></td>",
				"</tr>",
				"</table>",
//				"<div style='position:absolute; bottom:0; right:0;' class=appt_end_time id='<!--$id-->_et'><!--$endtime--></div>",				
			"</div>"
			),
		end: "",
		width:10,	//NOT ACCURATE
		height:7
	}
);

DwtBorder.registerBorder(
	"calendar_appt_bottom_only",
	{	
		start:AjxBuffer.concat(	
			"<div id='<!--$id-->_body' class='appt_body <!--$bodyColor-->'>",
				"<table style='width:100%;height:100%'cellspacing=0 cellpadding=2>",
				"<tr valign=top>",
					"<td colspan=2 class=appt<!--$newState-->_name style='height:100%'>",
						"<!--$name-->",
						"<BR>",
						"<!--$location-->",
					"</td>",
				"<tr>",
					"<td colspan=2 class=appt_end_time id='<!--$id-->_et'><!--$endtime--></td>",
				"</tr>",
				"</table>",
			"</div>"
			),
		end: "",
		width:10,	//NOT ACCURATE
		height:7
	}
);

DwtBorder.registerBorder(
	"calendar_appt_30",
	{	
		start:AjxBuffer.concat(
			"<div id='<!--$id-->_body' class='appt_30_body <!--$headerColor-->'>",
				"<table width=100% cellspacing=0 cellpadding=2>",
				"<tr>",
					"<td class=appt_30<!--$newState-->_name><!--$name--></td>",
//					"<td class=appt<!--$newState-->_tag><!--$tag--></td>",
				"</tr>",
				"</table>",
			"</div>"
		),
		end:	"",
		width:4,
		height:4
	}
);

DwtBorder.registerBorder(
	"calendar_appt_allday",
	{	
		start:AjxBuffer.concat(
			"<div id='<!--$id-->_body' <!--$body_style--> class='appt_allday_body <!--$headerColor-->'>",
				"<table width=100% cellspacing=0 cellpadding=2>",
				"<tr>",
					"<td class=appt_allday<!--$newState-->_name><!--$name--></td>",
//					"<td class=appt<!--$newState-->_tag><!--$tag--></td>",
				"</tr>",
				"</table>",
			"</div>"
		),
		end:"",
		width:4,
		height:4
	}
);



DwtBorder.registerBorder( 
	"SplashScreen", 
	{ 
		start: AjxBuffer.concat(
				 "<table class='DialogTable LightWindowOuterContainer' cellspacing=0 cellpadding='0' Xborder=1>",
					// top edge
					"<tr><td><div style='position:relative' class='SplashScreen'>"+
							"<div class='ImgSplashScreen_blank'></div>",
							"<div class=SplashScreenUrl><!--$url--></div>",
							"<div class=SplashScreenShortVersion><!--$shortVersion--></div>",
							"<div class=SplashScreenAppName><!--$appName--></div>",
							"<div class=SplashScreenVersion><!--$version--></div>",
							"<div class=SplashScreenContents><!--$contents--></div>",
							"<div class=SplashScreenLicense><!--$license--></div>",
							"<div class=SplashScreenOKButton id='<!--$buttonId-->'><!--$button--></div>",
						"</div></td>",
					"</tr>"
				),

		end: AjxBuffer.concat(
			     "</table>"
				)
	}
);

DwtBorder.registerBorder( 
	"LoginBanner", 
	{ 
		start: AjxBuffer.concat(
				 "<table class='DialogTable' cellpadding='0' Xborder=1>",
					// top edge
					"<tr><td><div style='position:relative'>"+
							"<div class='ImgLoginBanner_blank'></div>",
							"<div id=LoginBannerUrl><!--$url--></div>",
							"<div id=LoginBannerShortVersion><!--$shortVersion--></div>",
							"<div id=LoginBannerAppName><!--$appName--></div>",
							"<div id=LoginBannerVersion><!--$version--></div>",
						"</div></td>",
					"</tr>",
			     "</table>"
				),
		end:""
	}
);
