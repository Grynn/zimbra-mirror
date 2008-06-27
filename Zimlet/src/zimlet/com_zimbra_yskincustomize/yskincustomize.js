/*
 * ***** BEGIN LICENSE BLOCK *****
 *
 * Zimbra Collaboration Suite Zimlets
 * Copyright (C) 2005, 2006, 2007 Zimbra, Inc.
 *
 * The contents of this file are subject to the Yahoo! Public License
 * Version 1.0 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 *
 * ***** END LICENSE BLOCK *****
 */

//////////////////////////////////////////////////////////////
//  Zimlet to customize Yahoo! skin for small biz           //
//  @author Rajendra Patil                                  //
//////////////////////////////////////////////////////////////

function Com_Zimbra_YSkinCustomize() {
}

Com_Zimbra_YSkinCustomize.prototype = new ZmZimletBase();
Com_Zimbra_YSkinCustomize.prototype.constructor = Com_Zimbra_YSkinCustomize;
//!TODO hard coded
Com_Zimbra_YSkinCustomize.adminUrl = "http://ecp.smallbusiness.zimbra.yahoo.com";//

Com_Zimbra_YSkinCustomize.prototype.init =
function() {
    var skinName = Com_Zimbra_YSkinCustomize.getActiveSkinName();
    if(skinName && skinName == 'yahoo'){
        Com_Zimbra_YSkinCustomize.hideMyYahoo();
        if(Com_Zimbra_YSkinCustomize.isUserDomainAdmin()){
           var options = document.getElementById("skin_yahoo_options");
           Com_Zimbra_YSkinCustomize.reLink(options,Com_Zimbra_YSkinCustomize.adminUrl,this.getMessage("adminLinkLabel"));             
        }else{
           Com_Zimbra_YSkinCustomize.hideOptions();             
        }
    }
};

Com_Zimbra_YSkinCustomize.getActiveSkinName = function(){
    return skin.hints.name || appCtxt.get(ZmSetting.SKIN_NAME);
};

Com_Zimbra_YSkinCustomize.isUserDomainAdmin = function(){
  //!TODO is this optimized and the only way to check if user is domain admin?
   return appCtxt.getSettings().getInfoResponse.attrs._attrs["zimbraIsAdminAccount"];//ZmAuthenticate._isAdmin;
};

Com_Zimbra_YSkinCustomize.hideMyYahoo = function(){
    var myyahoo = document.getElementById("skin_yahoo_myyahoo");
    Com_Zimbra_YSkinCustomize.hide(myyahoo);
};

Com_Zimbra_YSkinCustomize.reLink = function(hrefElem,newLink,newLabel){
    if(hrefElem){
        hrefElem.href = newLink;
        hrefElem.innerHTML = newLabel;
    }
};

Com_Zimbra_YSkinCustomize.hideOptions = function(){
    var optionsSpan = document.getElementById("skin_yahoo_options_span");
    Com_Zimbra_YSkinCustomize.hide(optionsSpan);
};

Com_Zimbra_YSkinCustomize.hide = function(elem){
    if(elem){
        elem.style.display = 'none';
    }
};