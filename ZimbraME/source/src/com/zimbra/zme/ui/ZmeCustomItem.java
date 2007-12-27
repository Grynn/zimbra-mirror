/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Zimbra Collaboration Suite, Network Edition.
 * Copyright (C) 2007 Zimbra, Inc.  All Rights Reserved.
 * 
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
    
    public int getGameAction(int keyCode) {
        int ga = super.getGameAction(keyCode);
        if (hasTrackBallNavigation() && (ga == Canvas.LEFT || ga == Canvas.RIGHT))
            return 0;

        return ga;
    }
}
