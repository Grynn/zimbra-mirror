/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite CSharp Client
 * Copyright (C) 2012, 2013 Zimbra Software, LLC.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.4 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 */
// Copyright (c) 2001-2003 Quadralay Corporation.  All rights reserved.
//

function  WWHJavaScriptSettings_Object()
{
  this.mHoverText = new WWHJavaScriptSettings_HoverText_Object();

  this.mTabs      = new WWHJavaScriptSettings_Tabs_Object();
  this.mTOC       = new WWHJavaScriptSettings_TOC_Object();
  this.mIndex     = new WWHJavaScriptSettings_Index_Object();
  this.mSearch    = new WWHJavaScriptSettings_Search_Object();
  this.mFavorites = new WWHJavaScriptSettings_Favorites_Object();
}

function  WWHJavaScriptSettings_HoverText_Object()
{
  this.mbEnabled = true;

  this.mFontStyle = "font-family: Verdana, Arial, Helvetica, sans-serif ; font-size: 8pt";

  this.mWidth = 150;

  this.mForegroundColor = "#000000";
  this.mBackgroundColor = "#FFFFCC";
  this.mBorderColor     = "#999999";
}

function  WWHJavaScriptSettings_Tabs_Object()
{
  this.mFontStyle = "font-family: Verdana, Arial, Helvetica, sans-serif ; font-size: 9pt ; font-weight: normal";

  this.mSelectedTabForegroundColor = "#FFFFFF";

  this.mDefaultTabForegroundColor = "#466494";
}

function  WWHJavaScriptSettings_TOC_Object()
{
  this.mbShow = true;

  this.mFontStyle = "font-family: Verdana, Arial, Helvetica, sans-serif ; font-size: 8pt";

  this.mHighlightColor = "#CCCCCC";
  this.mEnabledColor   = "#315585";
  this.mDisabledColor  = "black";

  this.mIndent = 17;
}

function  WWHJavaScriptSettings_Index_Object()
{
  this.mbShow = false;

  this.mFontStyle = "font-family: Verdana, Arial, Helvetica, sans-serif ; font-size: 8pt";

  this.mHighlightColor = "#CCCCCC";
  this.mEnabledColor   = "#315585";
  this.mDisabledColor  = "black";

  this.mIndent = 17;

  this.mNavigationFontStyle      = "font-family: Verdana, Arial, Helvetica, sans-serif ; font-size: 7pt ; font-weight: bold";
  this.mNavigationCurrentColor   = "black";
  this.mNavigationHighlightColor = "#CCCCCC";
  this.mNavigationEnabledColor   = "#FFFFFF";
  this.mNavigationDisabledColor  = "#999999";
}

function  WWHJavaScriptSettings_Index_DisplayOptions(ParamIndexOptions)
{
  ParamIndexOptions.fSetThreshold(500);
  ParamIndexOptions.fSetSeperator(" - ");
}

function  WWHJavaScriptSettings_Search_Object()
{
  this.mbShow = true;

  this.mFontStyle = "font-family: Verdana, Arial, Helvetica, sans-serif ; font-size: 8pt";

  this.mHighlightColor = "#CCCCCC";
  this.mEnabledColor   = "#315585";
  this.mDisabledColor  = "black";

  this.mbResultsByBook = true;
  this.mbShowRank      = true;
}

function  WWHJavaScriptSettings_Favorites_Object()
{
  this.mbShow = false;

  this.mFontStyle = "font-family: Verdana, Arial, Helvetica, sans-serif ; font-size: 8pt";

  this.mHighlightColor = "#CCCCCC";
  this.mEnabledColor   = "#315585";
  this.mDisabledColor  = "black";
}
