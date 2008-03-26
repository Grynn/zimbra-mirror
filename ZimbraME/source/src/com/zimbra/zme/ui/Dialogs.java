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

import java.util.Timer;
import java.util.TimerTask;

import javax.microedition.lcdui.ChoiceGroup;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Gauge;
import javax.microedition.lcdui.Screen;
import javax.microedition.lcdui.StringItem;

import com.zimbra.zme.ZimbraME;

import de.enough.polish.ui.FramedForm;
import de.enough.polish.ui.ScreenInfo;
import de.enough.polish.util.Locale;

public class Dialogs {
	
	public static final Command OK = new Command(Locale.get("main.Ok"), Command.OK, 1);
	public static final Command CANCEL = new Command(Locale.get("main.Cancel"), Command.CANCEL, 1);
	public static final Command YES = new Command(Locale.get("main.Yes"), Command.OK, 1);
	public static final Command NO = new Command(Locale.get("main.No"), Command.CANCEL, 1);
	

	/*************************************************************************************************
	 * Confirm Dialog
	 *************************************************************************************************/
	public static FramedForm mConfirmD;
	private static StringItem mConfirmDText;

	public static void popupConfirmDialog(ZimbraME m,
										  CommandListener c,
										  String msgTxt) {
		if (mConfirmD == null)
			initConfirmD();
		
		mConfirmDText.setText(msgTxt);
		
		mConfirmD.setCommandListener(c);
		m.mDisplay.setCurrent(mConfirmD);
	}
	
	private static void initConfirmD() {
		//#style Dialog
		mConfirmD = new FramedForm(null);
		
		//#style DialogItem
		mConfirmDText = new StringItem(null, null);
		mConfirmD.append(mConfirmDText);
				
		mConfirmD.addCommand(YES);
		mConfirmD.addCommand(NO);
	}

	/*************************************************************************************************
	 * Error Dialog
	 *************************************************************************************************/
	public static FramedForm mErrorD;
	private static StringItem mErrorDText;

	public static void popupErrorDialog(ZimbraME m,
										CommandListener c,
										String msgTxt) {
		if (mErrorD == null)
			initErrorD();
		
		mErrorDText.setText(msgTxt);
		
		mErrorD.setCommandListener(c);
		m.mDisplay.setCurrent(mErrorD);
	}
	
	private static void initErrorD() {
		//#style ErrorDialog
		mErrorD = new FramedForm(null);
		
		//#style ErrorDialogItem
		mErrorDText = new StringItem(null, null);
		mErrorD.append(mErrorDText);
				
		mErrorD.addCommand(OK);
        //#ifdef polish.debugEnabled
        mErrorD.addCommand(ZimbraME.SHOW_LOG);
        //#endif
	}

	/*************************************************************************************************
	 * Work In Progress (WIP) Dialog
	 *************************************************************************************************/

	public static Form mWipD;
	private static StringItem mWipText;
	
	public static void popupWipDialog(ZimbraME m,
			   						  CommandListener c,
			   						  String text) {
		if (mWipD == null)
			initWipD();
	
		mWipText.setText(text);
		mWipD.setCommandListener(c);
		m.mDisplay.setCurrent(mWipD);
	}

	private static void initWipD() {
		//#style WipDialog
		mWipD = new Form(null);
		
		//#style WipLabel
		mWipText = new StringItem("", "");
		mWipD.append(mWipText);

		//#style WipDialogGauge
		mWipD.append(new Gauge("", false, Gauge.INDEFINITE, Gauge.CONTINUOUS_RUNNING));
		
		mWipD.addCommand(ZimbraME.CANCEL);		
	}
	
	/*************************************************************************************************
	 * Instance or Series Dialog
	 *************************************************************************************************/

	public static FramedForm mInstOrSeriesD;
	private static ChoiceGroup mInstOrSeriesCG;

	public static void popupInstOrSeriesDialog(ZimbraME m,
											   CommandListener c) {
		
		if (mInstOrSeriesD == null)
			initInstOrSeriesD();
		
		mInstOrSeriesCG.setSelectedIndex(0, true);
		mInstOrSeriesD.setCommandListener(c);
		m.mDisplay.setCurrent(mInstOrSeriesD);
	}
	
	public static boolean instSelected() {
		return mInstOrSeriesCG.isSelected(0);
	}
	
	private static void initInstOrSeriesD() {
		//#style Dialog
		mInstOrSeriesD = new FramedForm(null);
		
		//#style DialogItem
		mInstOrSeriesD.append(Locale.get("calendar.ApplyToInstanceOrSeries"));
		
		//#style DialogItem
		mInstOrSeriesCG = new ChoiceGroup(null, ChoiceGroup.EXCLUSIVE, 
										  new String[]{Locale.get("calendar.Instance"), Locale.get("calendar.Series")}, 
										  null);
		mInstOrSeriesD.append(mInstOrSeriesCG);
		
		mInstOrSeriesD.addCommand(OK);
		mInstOrSeriesD.addCommand(CANCEL);
	}
	
	/*************************************************************************************************
	 * ScreenInfo hider
	 *************************************************************************************************/

	static class ScreenInfoHider extends TimerTask{
		public void run() {
			try {
				hideScreenInfo();
			} catch (Throwable e) { //don't let exceptions kill the timer
				//#debug
				System.out.println("Dialogs.ScreenInfoHider caught exception " + e); 
			}
		}	
	}
	private static ScreenInfoHider mScreenInfoHider = new ScreenInfoHider();
	private static Timer mTimer = new Timer();
	
	//#style StatusMsg
	private static StringItem mStatusMsg = new StringItem(null, "");
	
	public static void showStatusMsg(String msg,
									 Screen screen,
									 boolean autoDismiss) {
		mStatusMsg.setText(msg);
		ScreenInfo.setItem(mStatusMsg);
		if (screen != null) {
			//#if true
				//# ScreenInfo.setScreen(screen);
			//#endif
		}
		ScreenInfo.setVisible(true);
		if (autoDismiss)
			Dialogs.hideStatusMsg(2500);						
	}
	
	public static void hideScreenInfo() {
		// Need to first set an item, then set false or else the ScreenInfo does not get
		// hidden
		ScreenInfo.setItem(mStatusMsg);
		ScreenInfo.setVisible(false);		
	}
	
	private static void hideStatusMsg(long delay) {
		mTimer.schedule(mScreenInfoHider, delay);
	}
}
