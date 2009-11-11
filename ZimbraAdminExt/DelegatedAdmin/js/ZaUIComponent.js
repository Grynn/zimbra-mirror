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
ZaUIComponent.A_inheritedUIComponents = "inheritedUIComponents";

//zimbraAdminConsoleUIComponents   view in account permission view
ZaUIComponent.accountTargetXFormModifier = function (xFormObject, entry) {

    //check if the UI component is enabled
    //if (! ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.CARTE_BLANCHE_UI]) {
        var uiEnabled  = ZaTabView.isTAB_ENABLED(entry,[ZaAccount.A_zimbraAdminConsoleUIComponents], []);
        
        /*if (this instanceof ZaAccountXFormView) {
            uiEnabled = ZaSettings.ENABLED_UI_COMPONENTS [ZaSettings.ACCOUNT_UI_COMP_TAB] ;
        } else if (this instanceof ZaDLXFormView) {
            uiEnabled = ZaSettings.ENABLED_UI_COMPONENTS [ZaSettings.DL_UI_COMP_TAB] ;
        } */

        if (!uiEnabled) return ;
    //}

    var tabBar, switchGroup, tabIx ;
    for (var i=0; i < xFormObject.items.length; i ++) {
        if (xFormObject.items[i].type == _TAB_BAR_) {
            tabBar = xFormObject.items[i] ;
        }

        if (xFormObject.items[i].type == _SWITCH_) {
            switchGroup = xFormObject.items[i]
        }
    }

    if (tabBar && switchGroup) {
        tabIx = ++this.TAB_INDEX;
        tabBar.choices.push({value:tabIx, label: com_zimbra_delegatedadmin.Tab_ui_components}) ;
    }

    var caseItem = {type:_ZATABCASE_, id:"target_form_ui_comp_tab", numCols:1, colSizes:["800px"],
            caseKey:  tabIx,
            items:[
                {type: _SPACER_, height: 10},    
                { type: _DWT_ALERT_, width: "98%",
				    style: DwtAlert.INFORMATION, iconVisible: false,
                    content: com_zimbra_delegatedadmin.HELP_NOTES_UI_COMP },
                {type:_TOP_GROUPER_, label: com_zimbra_delegatedadmin.Label_ui_comp, id:"ui_comp_grouper",
                    colSizes:["800px"],numCols:1 ,
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

ZaUIComponent.InheritedUIComponentsItem = {
    id: ZaUIComponent.A_inheritedUIComponents, ref: ZaUIComponent.A_inheritedUIComponents,
    outputType: _LIST_,    //it is important to set the attr value in OSELECT_XFormItem
    type: _LIST_, listItems: { type: _STRING_ }
};

ZaUIComponent.getUIComponentsXFormItem  = function (choiceWidth) {
    var w = choiceWidth || 400 ;
    var list =
        { type: _GROUP_, colSpan:"*", numCols: 2, colSize: [w, w], items: [
               {type: _OUTPUT_, value: com_zimbra_delegatedadmin.tLabel_direct_ui_comp,
                   cssStyle: "font-size:12px;",
                   align: _LEFT_ },
               {type: _OUTPUT_, value: com_zimbra_delegatedadmin.tLabel_indirect_ui_comp,
                   cssStyle: "font-size:12px;",
                   align: _LEFT_ },
               {type:_ZIMLET_SELECT_, numCols: 1, colSizes: [ w], choicesWidth: w,
                    selectRef:ZaAccount.A_zimbraAdminConsoleUIComponents,
                    ref:ZaAccount.A_zimbraAdminConsoleUIComponents,
                    choices:ZaSettings.ALL_UI_COMPONENTS
               } ,
               { type:_ZIMLET_SELECT_, numCols: 1, colSizes: [ w], choicesWidth: w,
//                    ref:ZaUIComponent.A_inheritedUIComponents,
                    selectRef:ZaUIComponent.A_inheritedUIComponents,
                    enableDisableChecks: false,
                    choices:ZaSettings.ALL_UI_COMPONENTS
               }
            ]
        };

    return [list];
}

/**
 * set the  ZaUIComponent.A_inheritedUIComponents
 */

ZaUIComponent.accountObjectModifer =
function () {
    var inheritedUIComps = [];
    var comps = ZaSettings.getUIComponents (this._containedObject) ;
     for(var i=0;i<comps.length;i++) {
        if (comps[i].inherited) {
            inheritedUIComps.push(comps[i]._content) ;
        }
    }

    this._containedObject [ZaUIComponent.A_inheritedUIComponents] = inheritedUIComps ;
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
    ZaAccount.myXModel.items.push(ZaUIComponent.InheritedUIComponentsItem);
}

if (ZaItem.ObjectModifiers["ZaAccount"]){
    ZaItem.ObjectModifiers["ZaAccount"].push(ZaUIComponent.uiCompObjectModifer) ;
}

if (ZaTabView.ObjectModifiers["ZaAccountXFormView"]){
    ZaTabView.ObjectModifiers["ZaAccountXFormView"].push(ZaUIComponent.accountObjectModifer) ;
}

if (ZaTabView.ObjectModifiers["ZaDLXFormView"]){
    ZaTabView.ObjectModifiers["ZaDLXFormView"].push(ZaUIComponent.accountObjectModifer) ;
}


if (ZaTabView.XFormModifiers["ZaAccountXFormView"]){
    ZaSettings.ACCOUNT_UI_COMP_TAB = "accountUIComponentsTab" ;
//    ZaSettings.ALL_UI_COMPONENTS.push({ value: ZaSettings.ACCOUNT_UI_COMP_TAB, label: com_zimbra_delegatedadmin.UI_Comp_AccountsUICompTab });
    ZaTabView.XFormModifiers["ZaAccountXFormView"].push(ZaUIComponent.accountTargetXFormModifier);
}

if (ZaDistributionList) {
    ZaDistributionList.myXModel.items.push(ZaUIComponent.UIComponentsItem);
    ZaDistributionList.myXModel.items.push(ZaUIComponent.InheritedUIComponentsItem);
}

if (ZaItem.ObjectModifiers["ZaDistributionList"]){
    ZaItem.ObjectModifiers["ZaDistributionList"].push(ZaUIComponent.uiCompObjectModifer) ;
}

if (ZaTabView.XFormModifiers["ZaDLXFormView"]){
    ZaSettings.DL_UI_COMP_TAB = "dlUIComponentsTab" ;
//    ZaSettings.ALL_UI_COMPONENTS.push({ value: ZaSettings.DL_UI_COMP_TAB, label: com_zimbra_delegatedadmin.UI_Comp_dlUICompTab });
    ZaTabView.XFormModifiers["ZaDLXFormView"].push(ZaUIComponent.accountTargetXFormModifier);
}

