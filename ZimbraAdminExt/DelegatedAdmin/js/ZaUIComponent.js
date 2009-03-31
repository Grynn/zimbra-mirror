/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2009 Zimbra, Inc.
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
function ZaUIComponent () {}

//zimbraAdminConsoleUIComponents   view in account permission view
ZaUIComponent.accountTargetXFormModifier = function (xFormObject) {

    //check if the UI component is enabled
    if (! ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.CARTE_BLANCHE_UI]) {
        var uiEnabled  = false ;
        if (this instanceof ZaAccountXFormView) {
            uiEnabled = ZaSettings.ENABLED_UI_COMPONENTS [ZaSettings.ACCOUNT_UI_COMP_TAB] ;
        } else if (this instanceof ZaDLXFormView) {
            uiEnabled = ZaSettings.ENABLED_UI_COMPONENTS [ZaSettings.DL_UI_COMP_TAB] ;
        } 

        if (!uiEnabled) return ;
    }

    var tabBar, switchGroup ;
    for (var i=0; i < xFormObject.items.length; i ++) {
        if (xFormObject.items[i].type == _TAB_BAR_) {
            tabBar = xFormObject.items[i] ;
        }

        if (xFormObject.items[i].type == _SWITCH_) {
            switchGroup = xFormObject.items[i]
        }
    }

    if (tabBar && switchGroup) {
        var tabIx = tabBar.choices.length + 1;
        tabBar.choices.push({value:tabIx, label: com_zimbra_delegatedadmin.Tab_ui_components}) ;
    }

    var caseItem = {type:_ZATABCASE_, id:"target_form_ui_comp_tab", numCols:1, colSizes:["700px"],
            caseKey:  tabIx,
            items:[
                {type:_TOP_GROUPER_, label: com_zimbra_delegatedadmin.Label_ui_comp, id:"ui_comp_grouper",
                    colSizes:["700px"],numCols:1 ,
                    items : ZaUIComponent.getUIComponentsXFormItem ()
                }
            ]
    };

    switchGroup.items.push(caseItem);
    return ;
}

ZaUIComponent.UIComponentsItem = {
    id: ZaAccount.A_zimbraAdminConsoleUIComponents, ref: "attrs/" + ZaAccount.A_zimbraAdminConsoleUIComponents,
    outputType: _LIST_,    //it is important to set the attr value in OSELECT_XFormItem
    type: _LIST_, listItems: { type: _STRING_ }
};


ZaUIComponent.getUIComponentsXFormItem  = function () {

    var list = {type:_ZIMLET_SELECT_, numCols: 1, colSizes: [ 400], choicesWidth: 400,
                    selectRef:ZaAccount.A_zimbraAdminConsoleUIComponents,
                    ref:ZaAccount.A_zimbraAdminConsoleUIComponents,
                    choices:ZaSettings.ALL_UI_COMPONENTS
                };

    return [list];
}

ZaUIComponent.uiCompObjectModifer = function () {
    if (this.attrs[ZaAccount.A_zimbraAdminConsoleUIComponents]) {
        if(!(this.attrs[ZaAccount.A_zimbraAdminConsoleUIComponents] instanceof Array)) {
            this.attrs[ZaAccount.A_zimbraAdminConsoleUIComponents] = [this.attrs[ZaAccount.A_zimbraAdminConsoleUIComponents]];
        }
    }
}

if (ZaAccount) {
    ZaAccount.myXModel.items.push(ZaUIComponent.UIComponentsItem);
}

if (ZaItem.ObjectModifiers["ZaAccount"]){
    ZaItem.ObjectModifiers["ZaAccount"].push(ZaUIComponent.uiCompObjectModifer) ;
}

if (ZaTabView.XFormModifiers["ZaAccountXFormView"]){
    ZaSettings.ACCOUNT_UI_COMP_TAB = "accountUIComponentsTab" ;
    ZaSettings.ALL_UI_COMPONENTS.push({ value: ZaSettings.ACCOUNT_UI_COMP_TAB, label: com_zimbra_delegatedadmin.UI_Comp_AccountsUICompTab });
    ZaTabView.XFormModifiers["ZaAccountXFormView"].push(ZaUIComponent.accountTargetXFormModifier);
}


if (ZaDistributionList) {
    ZaDistributionList.myXModel.items.push(ZaUIComponent.UIComponentsItem);
}

if (ZaItem.ObjectModifiers["ZaDistributionList"]){
    ZaItem.ObjectModifiers["ZaDistributionList"].push(ZaUIComponent.uiCompObjectModifer) ;
}

if (ZaTabView.XFormModifiers["ZaDLXFormView"]){
    ZaSettings.DL_UI_COMP_TAB = "dlUIComponentsTab" ;
    ZaSettings.ALL_UI_COMPONENTS.push({ value: ZaSettings.DL_UI_COMP_TAB, label: com_zimbra_delegatedadmin.UI_Comp_dlUICompTab });
    ZaTabView.XFormModifiers["ZaDLXFormView"].push(ZaUIComponent.accountTargetXFormModifier);
}

