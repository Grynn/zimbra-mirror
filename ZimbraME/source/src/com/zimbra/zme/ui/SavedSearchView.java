/*
 * ***** BEGIN LICENSE BLOCK *****
 * Version: ZPL 1.2
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.2 ("License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.zimbra.com/license
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See
 * the License for the specific language governing rights and limitations
 * under the License.
 * 
 * The Original Code is: Zimbra Collaboration Suite J2ME Client
 * 
 * The Initial Developer of the Original Code is Zimbra, Inc.
 * Portions created by Zimbra are Copyright (C) 2004, 2005, 2006, 2007 Zimbra, Inc.
 * All Rights Reserved.
 * 
 * Contributor(s):
 * 
 * ***** END LICENSE BLOCK *****
 */

package com.zimbra.zme.ui;

import java.util.Enumeration;
import java.util.Vector;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Item;
import javax.microedition.lcdui.StringItem;

import com.zimbra.zme.ResponseHdlr;
import com.zimbra.zme.ZimbraME;
import com.zimbra.zme.client.ItemFactory;
import com.zimbra.zme.client.Mailbox;

import de.enough.polish.ui.FramedForm;
import de.enough.polish.ui.Style;
import de.enough.polish.ui.TabbedForm;
import de.enough.polish.util.Locale;

public class SavedSearchView extends View implements ResponseHdlr {
	private ZmeStringItem mNoSearchesItem;

	private static final Command REFRESH = new Command(Locale.get("main.Refresh"), Command.ITEM, 1);

	public SavedSearchView(ZimbraME midlet) {
		super(midlet);
		//#if true
			//# mView = new FramedForm(null);
		//#endif
		init();		
	}

	public SavedSearchView(ZimbraME midlet,
					       Style style) {
		super(midlet);
		//#if true
			//# mView = new FramedForm(null, style);
		//#endif
		init();		
	}
	
	public void setSavedSearches(Vector savedSearches) {
		FramedForm f = null;
		//#if true
			//# f = (FramedForm)mView;
		//#endif
		f.deleteAll();
		if (savedSearches != null || savedSearches.size() > 0) {
			for (Enumeration e = savedSearches.elements(); e.hasMoreElements();)
				f.append((CollectionItem)e.nextElement());
		} else {
			if (mNoSearchesItem == null) {
				//#style NoResultItem
				mNoSearchesItem = new ZmeStringItem(mMidlet, this, Locale.get("savedSearchView.NoSearches"));
			}
			f.append(mNoSearchesItem);
		}
	}

	public void load() {
		mMidlet.mMbox.getSavedSearches(this, this);
		Dialogs.popupWipDialog(mMidlet, this, Locale.get("savedSearchView.GettingSavedSearches"));
	}

	public void commandAction(Command cmd, 
							  Displayable d) {
		if (d == mView) {
			if (cmd == BACK) {
				setNextCurrent();
			} else if (cmd == ZimbraME.SEARCH) {
				CollectionItem ss = null;
				//#if true
					//# FramedForm f = (FramedForm)mView;
					//# ss = (CollectionItem)f.getCurrentItem();
				//#endif
				mMidlet.execSearch(ss.mQuery, ss.mSortBy, ss.mTypes); 
			} else if (cmd == REFRESH) {
				load();
			}
		} else if (d == Dialogs.mWipD) {
			mMidlet.mMbox.cancelOp();
			mMidlet.mDisplay.setCurrent(mView);
		}
	}
	
	public void handleResponse(Object op, 
							   Object resp) {
		//#debug
		System.out.println("SavedSearchView.handleResponse");
		if (resp instanceof Mailbox) {
			setSavedSearches(mMidlet.mMbox.mSavedSearches);
			setCurrent();
		} else {
			mMidlet.handleResponseError(resp, this);			
		}
	}

	public void cancel() {
		mMidlet.mMbox.cancelOp();
		mMidlet.mDisplay.setCurrent(mView);
	}

	public CollectionItem createSavedSearchItem() {
		//#style SavedSearchItem
		CollectionItem c = new CollectionItem(mMidlet, CollectionItem.SAVED_SEARCH);
		return c;
	}

	private void init() {
		FramedForm f = null;
		//#if true
			//# f = (FramedForm)mView;
		//#endif
		
		f.setCommandListener(this);
		
		//#style SavedSearchViewHeader
		StringItem header = new StringItem(null, Locale.get("savedSearchView.SavedSearches"));			
		f.append(Graphics.TOP, header);
		
		f.addCommand(BACK);
		f.addCommand(ZimbraME.SEARCH);
		f.addCommand(REFRESH);
	}

}
