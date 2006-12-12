package com.zimbra.zme;

/**
 * User: rossd
 * Date: Nov 28, 2006
 * Time: 10:49:29 PM
 */

import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;

public class ZimbraME extends MIDlet implements CommandListener {
    private Command exitCommand;
    private Command refreshCommand;
    private TextBox tbox;
    private Form form;

    public ZimbraME () {
        exitCommand = new Command("Exit", Command.EXIT, 1);
        refreshCommand = new Command("Refresh", Command.BACK, 1);
        tbox = new TextBox("Hello world MIDlet", "Hello World!", 25, 0);
        tbox.addCommand(exitCommand);
        tbox.addCommand(refreshCommand);
        tbox.setCommandListener(this);
        form = new Form("FOO");
        form.append(new StringItem(null, "What me worry"));
    }

    protected void startApp() {
        Display.getDisplay(this).setCurrent(form);
    }

    protected void pauseApp() {}
    protected void destroyApp(boolean bool) {}

    public void commandAction(Command cmd, Displayable disp) {
        if (cmd == exitCommand) {
            destroyApp(false);
            notifyDestroyed();
        }
    }
}
