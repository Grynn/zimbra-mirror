/*
 * ***** BEGIN LICENSE BLOCK *****
 * Version: ZPL 1.2
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.2 ("License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.zimbra.com/license
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See
 * the License for the specific language governing rights and limitations
 * under the License.
 * 
 * The Original Code is: Zimbra Collaboration Suite Web Client
 * 
 * The Initial Developer of the Original Code is Zimbra, Inc.
 * Portions created by Zimbra are Copyright (C) 2006 Zimbra, Inc.
 * All Rights Reserved.
 * 
 * Contributor(s):
 * 
 * ***** END LICENSE BLOCK *****
 */

/**
* Utility Class for the Admin Console. 
* @class ZaUtil
* 
**/

function ZaUtil () {};

/*
 * @param v: all the valid life time value is end with smhd
 */
ZaUtil.getLifeTimeInSeconds =
function (v){
	if (AjxUtil.isLifeTime(v)) {
		var len = v.length ;
		var d = v.substr (0, len -1);
		var p = v.substr (len - 1, len);
		
		if (p == "s"){
			return d;
		}else if ( p == "m") {
			return d*60 ;
		}else if (p == "h"){
			return d*3600 ;
		}else if (p == "d") {
			return d*216000;
		}
	}else{
		throw (new AjxException(AjxMessageFormat.format(ZaMsg.UTIL_INVALID_LIFETIME,[v])));
	}
}
