ZaRightSelectionDialog = function(parent,  app, title) {
    if (arguments.length == 0) return;
    this._standardButtons = [ DwtDialog.CANCEL_BUTTON, DwtDialog.OK_BUTTON];
    ZaXDialog.call(this, parent,null,  title, "400px", "400px");
    this._containedObject = {};
    
    this.initForm(ZaRightSelectionDialog.myXModel, this.getMyXForm());
}

ZaRightSelectionDialog.prototype = new ZaXDialog;
ZaRightSelectionDialog.prototype.constructor = ZaRightSelectionDialog;


ZaRightSelectionDialog.myXModel = {
	items: [
        {id: ZaRight.A_id, ref: ZaRight.A_id, type: _STRING_},
        {id: ZaRight.A_name, ref: ZaRight.A_name, type: _STRING_},
        {id: ZaRight.A_type, ref: ZaRight.A_type, type: _ENUM_, choices: ZaZimbraRights.type },
            //TODO: have a new choice list xform item to display the targetType
        {id: ZaRight.A_targetType, ref: ZaRight.A_targetType, type: _LIST_, choices: ZaZimbraRights.targetType },
//        {id: ZaRight.A_desc, ref: ZaRight.A_desc, type: _STRING_ },
        {id: ZaRight.A_definedBy, ref: ZaRight.A_definedBy, type: _ENUM_, choices: ZaZimbraRights.definedBy },
//        {id: ZaRight.A_attrs,  ref: ZaRight.A_attrs, type: _LIST_, listItem:{type:_STRING_}} ,
        {id: ZaRight.A_rights,  ref: ZaRight.A_rights, type: _LIST_, listItem:{type:_STRING_}} ,
        {id: ZaRight.A2_selected_rights,  ref: ZaRight.A2_selected_rights, type: _LIST_, listItem:{type:_STRING_}}
    ]
};

ZaRightSelectionDialog.prototype.getMyXForm =
function() {
    var xFormObject = {
        numCols:1,
        items:[
             {type:_GROUP_,isTabGroup:true, numCols:2, colSizes: [100, "*"], items: [ //allows tab key iteration
               { type: _SPACER_ },
               { ref: ZaRight.A_name, type: _TEXTFIELD_ , label: com_zimbra_delegatedadmin.Col_right_name+ ": " },
               { ref: ZaRight.A_type, type:_OSELECT1_, label: com_zimbra_delegatedadmin.Col_right_type + ": ",
                   labelLocation:_LEFT_, choices: ZaZimbraRights.type  ,
                   onChange: ZaRightSelectionDialog.filterChanged
               },
               { type: _GROUP_,
                    label: com_zimbra_delegatedadmin.Label_target_type, nowrap: true, labelLocation:_LEFT_,
                    //show two columns of the target types
                    colSpan: "*", numCols:4, colSizes: [20, 100, 20, "*"],
                    items: [
        //                { ref: ZaRight.A_targetType  }
        //TODO:     hardcode the prototype first
                        {type: _CHECKBOX_, label: "Account",labelLocation:_RIGHT_ } ,
                        {type: _CHECKBOX_, label: "Calendar Resource",labelLocation:_RIGHT_ } ,
                        {type: _CHECKBOX_, label: "Cos",labelLocation:_RIGHT_ } ,
                        {type: _CHECKBOX_, label: "Distribution List",labelLocation:_RIGHT_ } ,
                        {type: _CHECKBOX_, label: "Domain",labelLocation:_RIGHT_ } ,
                        {type: _CHECKBOX_, label: "Global Config",labelLocation:_RIGHT_ } ,
                        {type: _CHECKBOX_, label: "Global Grant",labelLocation:_RIGHT_ } ,
                        {type: _CHECKBOX_, label: "Right",labelLocation:_RIGHT_ } ,
                        {type: _CHECKBOX_, label: "Server",labelLocation:_RIGHT_ } ,
                        {type: _CHECKBOX_, label: "XMPP Component",labelLocation:_RIGHT_ } ,
                        {type: _CHECKBOX_, label: "Zimlet",labelLocation:_RIGHT_ }
                    ]
                },

                 {type:_SPACER_, height: "10px" },
                 //Rights View

                { type:_GROUP_, colSpan: "*", colSizes: ["100px", "*"], numCols: 2,
                 items: [
                   {type:_OUTPUT_, value: com_zimbra_delegatedadmin.Label_rights_to_add,  width: 100, 
                       valign: _CENTER_, align: _RIGHT_ },
                   {ref:ZaRight.A_rights, type:_DWT_LIST_, height:200, width:"250px",
                         forceUpdate: true, cssClass: "DLSource",
                         widgetClass: ZaRightsMiniListView,
                //                        headerList:acctLimitsHeaderList,
                         onSelection: ZaRightSelectionDialog.getSelectedRights,
                         hideHeader: false
                     }
                 ]
                }
              ]
            }
        ]
    };
    return xFormObject;
}

ZaRightSelectionDialog.filterChanged = function (value, event, form) {
    var instance = form.getInstance () ;
    this.setInstanceValue (value) ;
    var rightsFilter = instance ;
    
    var availableRights = ZaZimbraRights.getRights(rightsFilter) ;
    this.setInstanceValue (availableRights, ZaRight.A_rights) ;

}

ZaRightSelectionDialog.getSelectedRights = function (ev) {
   var arr = this.widget.getSelection();
   this.setInstanceValue(arr, ZaRight.A2_selected_rights) ;
}