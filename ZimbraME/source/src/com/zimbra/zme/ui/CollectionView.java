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
import com.zimbra.zme.client.SavedSearch;
import com.zimbra.zme.client.Tag;

import de.enough.polish.ui.FramedForm;
import de.enough.polish.ui.Style;
import de.enough.polish.ui.TabbedForm;
import de.enough.polish.util.Locale;

public class CollectionView extends View implements ResponseHdlr {
	public static final int SAVEDSEARCH = 1;
	public static final int TAG = 2;
	public static final int FOLDER = 3;
	public static final int ATTACHMENTLIST = 4;
	
	private ZmeStringItem mNoData;
	private Vector mAttachmentList;
	private int mType;

	private static final Command OPEN = new Command(Locale.get("main.Open"), Command.CANCEL, 1);
	private static final Command REFRESH = new Command(Locale.get("main.Refresh"), Command.ITEM, 1);

	//#ifdef polish.usePolishGui
		public CollectionView(ZimbraME midlet,
							  int type,
			       			  Style style) {
			super(midlet);
			//#if true
				//# mView = new FramedForm(null, style);
			//#endif
			init(type);		
		}
	//#else
		public CollectionView(ZimbraME midlet, 
							  int type) {
			super(midlet);
		}
	//#endif

	public void render() {
		FramedForm f = null;
		//#if true
			//# f = (FramedForm)mView;
		//#endif
		f.deleteAll();
		if (mType == FOLDER) {
			f.append(mMidlet.mMbox.mRootFolder);
		} else if (mType == ATTACHMENTLIST) {
		} else {
			Vector collection = (mType == SAVEDSEARCH) ? mMidlet.mMbox.mSavedSearches : mMidlet.mMbox.mTags;
			if (collection != null && collection.size() > 0) {
				CollectionItem c;
				for (Enumeration e = collection.elements(); e.hasMoreElements();) {
					if (mType == SAVEDSEARCH) {
						//#style CollectionItem
						c = new CollectionItem(mMidlet, (SavedSearch)e.nextElement(), false);
					} else {
						//#style CollectionItem
						c = new CollectionItem(mMidlet, (Tag)e.nextElement(), false);
					}
					f.append(c);
				}
			} else {
				if (mNoData == null) {
					//#style NoResultItem
					mNoData = new ZmeStringItem(mMidlet, this, Locale.get("collectionView.NoData"));
				}
				f.append(mNoData);
			}
		}
	}
	
	public void load() {
		switch (mType) {
			case SAVEDSEARCH:
				mMidlet.mMbox.getSavedSearches(this);
				Dialogs.popupWipDialog(mMidlet, this, Locale.get("collectionView.GettingSavedSearches"));
				break;
				
			case FOLDER:
				mMidlet.mMbox.getFolders(mMidlet, this);
				Dialogs.popupWipDialog(mMidlet, this, Locale.get("collectionView.GettingFolders"));
				break;
				
			case TAG:
				mMidlet.mMbox.getTags(this);
				Dialogs.popupWipDialog(mMidlet, this, Locale.get("collectionView.GettingTags"));
				break;
		}
	}
	
	public void load(Vector attachmentList) {
		mAttachmentList = attachmentList;
	}

	public void commandAction(Command cmd, 
							  Displayable d) {
		if (d == mView) {
			if (cmd == BACK) {
				setNextCurrent();
			} else if (cmd == ZimbraME.SEARCH) {
				CollectionItem ci = null;
				//#if true
					//# FramedForm f = (FramedForm)mView;
					//# ci = (CollectionItem)f.getCurrentItem();
				//#endif
				switch (mType) {
					case SAVEDSEARCH:
						mMidlet.execSearch(ci.mSavedSearch.mQuery, ci.mSavedSearch.mSortBy, ci.mSavedSearch.mTypes); 
						break;
					case TAG:
						String query = "tag:\"" + ci.mTag.mName + "\"";
						mMidlet.execSearch(query, null, null); 
						break;
					}
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
		System.out.println("CollectionView.handleResponse");
		if (resp instanceof Mailbox) {
			render();
			setCurrent();
		} else {
			mMidlet.handleResponseError(resp, this);			
		}
	}

	public void cancel() {
		mMidlet.mMbox.cancelOp();
		mMidlet.mDisplay.setCurrent(mView);
	}

	private void init(int type) {
		mType = type;
		FramedForm f = null;
		//#if true
			//# f = (FramedForm)mView;
		//#endif
		
		f.setCommandListener(this);
		
		//#style CollectionViewHeader
		StringItem header = new StringItem(null, "");
		
		switch (mType) {
			case FOLDER:
				header.setText(Locale.get("collectionView.Folders"));
				break;
			case SAVEDSEARCH:
				header.setText(Locale.get("collectionView.SavedSearches"));
				break;
			case TAG:
				header.setText(Locale.get("collectionView.Tags"));
				break;
		}
		
		f.append(Graphics.TOP, header);
		
		f.addCommand(BACK);
		
		if (mType == ATTACHMENTLIST) {
			f.addCommand(OPEN);
		} else {
			f.addCommand(ZimbraME.SEARCH);
			f.addCommand(REFRESH);
		}
	}

}
