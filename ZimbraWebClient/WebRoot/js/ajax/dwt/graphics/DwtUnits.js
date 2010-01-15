/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2005, 2006, 2007, 2009, 2010 Zimbra, Inc.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.3 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 */


DwtUnits = function() {
}

DwtUnits.PIXEL_UNIT = "px";
DwtUnits.CM_UNIT = "cm";
DwtUnits.MM_UNIT = "mm";
DwtUnits.INCH_UNIT = "in";
DwtUnits.PCT_UNIT = "%";
DwtUnits.POINT = "pt";

// pixel widths
DwtUnits.WIDTH_EM = AjxEnv.isIE ? 9 : 11; // width of "m"
DwtUnits.WIDTH_SEP = AjxEnv.isIE ? 6 : 8; // width of ", "
DwtUnits.WIDTH_ELLIPSIS = 15;			 // width of " ... "
