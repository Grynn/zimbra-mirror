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
