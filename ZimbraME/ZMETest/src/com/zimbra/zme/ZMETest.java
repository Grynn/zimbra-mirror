/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Zimbra Collaboration Suite J2ME Client
 * Copyright (C) 2007 Zimbra, Inc.
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
package com.zimbra.zme;

import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.StringItem;
import javax.microedition.lcdui.TextField;
import javax.microedition.midlet.MIDlet;

//#ifdef polish.usePolishGui
//# import de.enough.polish.ui.FramedForm;
//#endif

public class ZMETest extends MIDlet implements CommandListener {

    private String req1 = 
        "<soap:Envelope xmlns:soap=\"http://www.w3.org/2003/05/soap-envelope\">"+
        "<soap:Header><context xmlns=\"urn:zimbra\"><nosession/>"+
        "<userAgent name=\"zmetest\" version=\"1.0\">1.0</userAgent></context></soap:Header>"+
        "<soap:Body><AuthRequest xmlns=\"urn:zimbraAccount\">"+
        "<account by=\"name\">test-jylee</account><password>test123</password>"+
        "</AuthRequest></soap:Body></soap:Envelope>";
    private String req2 = 
        "<soap:Envelope xmlns:soap=\"http://www.w3.org/2003/05/soap-envelope\">"+
        "<soap:Header><context xmlns=\"urn:zimbra\"><nosession/>"+
        "<userAgent name=\"zmetest\" version=\"1.0\">1.0</userAgent></context></soap:Header>"+
        "<soap:Body><AuthRequest xmlns=\"urn:zimbraAccount\">"+
        "<account by=\"name\">jtest</account><password>test123</password>"+
        "</AuthRequest></soap:Body></soap:Envelope>";
    
    private String auth(String url, String req) {
        HttpConnection conn = null;
        try {
            conn = (HttpConnection)Connector.open(url);
            conn.setRequestMethod(HttpConnection.POST);
            conn.setRequestProperty("User-Agent", "ZMETest/1.0");
            java.io.OutputStream out = conn.openOutputStream();
            out.write(req.getBytes());
            int rc = conn.getResponseCode();
            java.io.InputStream in = conn.openInputStream();
            byte[] buf = new byte[1024];
            in.read(buf);
            String resp = new String(buf, "UTF-8");
            if (resp.indexOf("authToken") > 0)
                return "OK";
            else if (resp.indexOf("soap:Fault") > 0) {
                int i1 = resp.indexOf("<soap:Reason>");
                int i2 = resp.indexOf("<soap:Text>", i1);
                int i3 = resp.indexOf("</soap:Text>", i2);
                return "Error: " + resp.substring(i2+11, i3);
            }
            if (rc != 200) {
                return "HTTP error: "+rc;
            }
        } catch (Exception e) {
            return "Exception: "+e.getMessage();
        } finally {
            if (conn != null)
                try {
                    conn.close();
                } catch (Exception e) {}
        }
        return "Invalid response";
    }
    private String connectToUrl(String url) {
        HttpConnection conn = null;
        try {
            conn = (HttpConnection)Connector.open(url);
            conn.setRequestMethod(HttpConnection.GET);
            conn.setRequestProperty("User-Agent", "ZMETest/1.0");
            int rc = conn.getResponseCode();
            if (rc != 200) {
                return "HTTP error: "+rc;
            }
        } catch (Exception e) {
            return "Exception: "+e.getMessage();
        } finally {
            if (conn != null)
                try {
                    conn.close();
                } catch (Exception e) {}
        }
        return "OK";
    }
    private void runDiag() {
        Form diagForm = new Form("diag");
        Display.getDisplay(this).setCurrent(diagForm);
        diagForm.append(new StringItem("Width: ", Integer.toString(mainForm.getWidth())));
        diagForm.append(new StringItem("Height: ", Integer.toString(mainForm.getHeight())));
        diagForm.append(new StringItem("Connect to yahoo: ", connectToUrl("http://www.yahoo.com/favicon.ico")));
        diagForm.append(new StringItem("Connect to dogfood: ", connectToUrl("https://dogfood.zimbra.com/favicon.ico")));
        diagForm.append(new StringItem("Connect to roadshow: ", connectToUrl("http://roadshow.zimbra.com/favicon.ico")));
        diagForm.append(new StringItem("Auth to dogfood: ", auth("https://dogfood.zimbra.com/service/soap", req1)));
        diagForm.append(new StringItem("Auth to roadshow: ", auth("http://roadshow.zimbra.com/service/soap", req2)));
        diagForm.setCommandListener(this);
        diagForm.addCommand(BACK);
    }
    private void populateForm() {
        //#ifdef polish.usePolishGui
        //#style View
        //# FramedForm ff = new FramedForm("ZMETest");
        //# mainForm = ff;
        //# ff.addCommand(OPTIONS);
        //# ff.addSubCommand(ONE, OPTIONS);
        //# ff.addSubCommand(TWO, ONE);
        //# ff.addSubCommand(THREE, OPTIONS);
        //#endif
        if (mainForm == null)
            mainForm = new Form("ZMETest");
        mainForm.append(new StringItem(null, "One"));
        //#style InputField
        mainForm.append(new TextField("", null, 64, TextField.ANY));
        mainForm.append(new StringItem(null, "Two"));
        //#style InputField
        mainForm.append(new TextField("", null, 64, TextField.ANY));
        mainForm.append(new StringItem(null, "Three"));
        //#style InputField
        mainForm.append(new TextField("", null, 64, TextField.ANY));
        mainForm.append(new StringItem(null, "Four"));
        //#style InputField
        mainForm.append(new TextField("", null, 64, TextField.ANY));
        mainForm.append(new StringItem(null, "Five"));
        //#style InputField
        mainForm.append(new TextField("", null, 64, TextField.ANY));
        mainForm.append(new StringItem(null, "Six"));
        //#style InputField
        mainForm.append(new TextField("", null, 64, TextField.ANY));
        mainForm.addCommand(RUN_DIAG);
        mainForm.setCommandListener(this);
        Display.getDisplay(this).setCurrent(mainForm);
    }

    private Form mainForm;
    
    protected void startApp() {
        populateForm();
    }
    protected void pauseApp() {
    }
    protected void destroyApp(boolean a) {
    }
    
    private static final Command RUN_DIAG = new Command("Run Diagnostics", Command.ITEM, 1);
    private static final Command BACK = new Command("Back", Command.BACK, 1);
    private static final Command OPTIONS = new Command("Options", Command.ITEM, 1);
    private static final Command ONE = new Command("One", Command.ITEM, 1);
    private static final Command TWO = new Command("Two", Command.ITEM, 1);
    private static final Command THREE = new Command("Three", Command.ITEM, 1);
    
    public void commandAction(Command cmd, Displayable d) {
        if (cmd == RUN_DIAG) {
            runDiag();
        } else if (cmd == BACK) {
            Display.getDisplay(this).setCurrent(mainForm);
        }
    }
}
