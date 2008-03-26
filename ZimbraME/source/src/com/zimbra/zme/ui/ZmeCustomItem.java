/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite J2ME Client
 * Copyright (C) 2007, 2008 Zimbra, Inc.
 * 
 * The contents of this file are subject to the Yahoo! Public License
 * Version 1.0 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 */
package com.zimbra.zme.ui;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.CustomItem;

import com.zimbra.zme.ZimbraME;

import de.enough.polish.ui.Style;

public abstract class ZmeCustomItem extends CustomItem {
    protected ZimbraME mMidlet;
    
    //#ifdef polish.usePolishGui
    public ZmeCustomItem(ZimbraME midlet, Style style) {
        //#if true
            //# super("", style);
        //#else
            super("");
        //#endif
        mMidlet = midlet;
    }
    //#else
    public ZmeCustomItem(ZimbraME midlet) {
        super("");
        mMidlet = midlet;
    }
    //#endif
    
    protected final void keyPressed(int keyCode) {
        if (!mMidlet.handleCustomShortcut(this, keyCode))
            handleKeyPress(keyCode);
    }
    
    protected abstract void handleKeyPress(int keyCode);
    
    protected boolean hasTrackBallNavigation() {
        boolean ret = false;
        
        //#ifdef polish.hasTrackBallEvents
        //# ret = true;
        //#endif
        
        return ret;
    }
}
