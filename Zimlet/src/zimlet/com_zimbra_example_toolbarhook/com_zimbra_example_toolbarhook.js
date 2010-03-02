/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Zimlets
 * Copyright (C) 2005, 2006, 2007, 2008, 2009, 2010 Zimbra, Inc.
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

com_zimbra_example_toolbarhook_HandlerObject = function() {
};
com_zimbra_example_toolbarhook_HandlerObject.prototype = new ZmZimletBase;
com_zimbra_example_toolbarhook_HandlerObject.prototype.constructor = com_zimbra_example_toolbarhook_HandlerObject;

/**
 * This method gets called by the Zimlet framework when a toolbar is created.
 * 
 * http://files.zimbra.com/docs/zimlet/zcs/6.0/jsdocs/symbols/ZmZimletBase.html#initializeToolbar
 */
com_zimbra_example_toolbarhook_HandlerObject.prototype.initializeToolbar =
function(app, toolbar, controller, viewId) {

    if (viewId == ZmId.VIEW_CONVLIST || viewId == ZmId.VIEW_TRAD) {
        // get the index of "View" menu so we can display the button after that
        var buttonIndex = 0;
        for (var i = 0; i < toolbar.opList.length; i++) {
                if (toolbar.opList[i] == ZmOperation.VIEW_MENU) {
                        buttonIndex = i + 1;
                        break;
                }
        }

        var buttonParams = {
                text    : "Toolbar Button",
                tooltip: "This button shows up in Conversation view, traditional view, and in convlist view",
                index: buttonIndex, // position of the button
                image: "zimbraicon" // icon
        };

        // creates the button with an id and params containing the button details
        var button = toolbar.createOp("HELLOTEST_ZIMLET_TOOLBAR_BUTTON", buttonParams);
        button.addSelectionListener(new AjxListener(this, this._showSelectedMail, controller));   
    }
};

/**
 * Shows the selected mail.
 * 
 */
com_zimbra_example_toolbarhook_HandlerObject.prototype._showSelectedMail =
function(controller) {

	var message = controller.getMsg();

	appCtxt.getAppController().setStatusMsg("Subject:"+ message.subject);
};
