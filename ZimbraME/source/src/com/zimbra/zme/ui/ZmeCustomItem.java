package com.zimbra.zme.ui;

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
        handleKeyPress(keyCode);
    }
    
    protected abstract void handleKeyPress(int keyCode);
}
