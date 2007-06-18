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

import javax.microedition.io.ConnectionNotFoundException;
import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Item;
import javax.microedition.lcdui.StringItem;

import com.zimbra.zme.ResponseHdlr;
import com.zimbra.zme.ZimbraME;
import com.zimbra.zme.ZmeListener;
import com.zimbra.zme.client.Attachment;
import com.zimbra.zme.client.Mailbox;
import com.zimbra.zme.client.SavedSearch;
import com.zimbra.zme.client.Tag;

import de.enough.polish.ui.FramedForm;
import de.enough.polish.ui.Style;
import de.enough.polish.util.Locale;

public class CollectionView extends View implements ResponseHdlr {
	/* Different types of collection view. Trying to avoid creating a bunch
	 * of classes for each type as this seems to be a big no no with J2ME devlopment
	 */
	public static final int SAVEDSEARCH = 1;
	public static final int TAG_PICKER = 2;
	public static final int TAG_SEARCH = 3;
	public static final int FOLDER_SEARCH = 4;
	public static final int FOLDER_PICK = 5;
	public static final int ATTACHMENTLIST = 6;
	
	private ZmeStringItem mNoData;
	private Vector mAttachmentList;
	private String[] mTags;
	private int mType;
	private ZmeListener mListener;

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

	public int getType() {
		return mType;
	}
	
	public void render() {
		FramedForm f = null;
		//#if true
			//# f = (FramedForm)mView;
		//#endif
		f.deleteAll();
		if (mType == FOLDER_PICK || mType == FOLDER_SEARCH) {
			f.append(mMidlet.mMbox.mRootFolder);
			return;
		} else if (mType == ATTACHMENTLIST) {
			if (mAttachmentList != null && mAttachmentList.size() > 0) {
				CollectionItem c;
				for (Enumeration e = mAttachmentList.elements(); e.hasMoreElements();) {
					//#style CollectionItem
					c = new CollectionItem(mMidlet, this, (Attachment)e.nextElement(), false);
					f.append(c);
			}
				return;
			}
		} else {
			Vector collection = (mType == SAVEDSEARCH) ? mMidlet.mMbox.mSavedSearches : mMidlet.mMbox.mTags;
			if (collection != null && collection.size() > 0) {
				CollectionItem c;
				for (Enumeration e = collection.elements(); e.hasMoreElements();) {
					if (mType == SAVEDSEARCH) {
						//#style CollectionItem
						c = new CollectionItem(mMidlet, this, (SavedSearch)e.nextElement(), false);
					} else {
						//#style CollectionItem
						c = new CollectionItem(mMidlet, this, (Tag)e.nextElement(), true);
						
						if (mTags != null) {
							for (int i = 0; i < mTags.length; i++) {
								if (c.mTag.mId.compareTo(mTags[i]) == 0)
									c.setSelected(true);
							}
						}
					}
					f.append(c);
				}
				return;
			}
		}
		if (mNoData == null) {
			//#style NoResultItem
			mNoData = new ZmeStringItem(mMidlet, this, Locale.get("collectionView.NoData"));
		}
		f.append(mNoData);
	}
	
	public void load() {
		switch (mType) {
			case SAVEDSEARCH:
				mMidlet.mMbox.getSavedSearches(this);
				Dialogs.popupWipDialog(mMidlet, this, Locale.get("collectionView.GettingSavedSearches"));
				break;
				
			case FOLDER_PICK:
			case FOLDER_SEARCH:
				mMidlet.mMbox.getFolders(mMidlet, this);
				Dialogs.popupWipDialog(mMidlet, this, Locale.get("collectionView.GettingFolders"));
				break;
				
			case TAG_PICKER:
			case TAG_SEARCH:
				mMidlet.mMbox.getTags(this);
				Dialogs.popupWipDialog(mMidlet, this, Locale.get("collectionView.GettingTags"));
				break;
		}
	}
	
	public void setListener(ZmeListener listener) {
		mListener = listener;
	}
	
	public void load(Vector attachmentList) {
		mAttachmentList = attachmentList;
		render();
	}
	
	public void setTags(String[] tags) {
		if (mType != TAG_SEARCH && mType != TAG_PICKER)
			return;
		mTags = tags;
		int sz = mView.size();
		
		//If we have an empty list return
		if (sz == 1 && (mView.get(0) instanceof ZmeStringItem))
			return;
		
		CollectionItem ci = null;
		for (int i = 0; i < sz; i++) {
			ci = (CollectionItem)mView.get(i);
			ci.setSelected(false);
			if (tags != null) {
				for (int j = 0; j < tags.length; j++) {
					if (ci.mTag.mId.compareTo(tags[j]) == 0) {
						ci.setSelected(true);
						break;
					}
				}
			}
		}
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
				execSearch(ci);
			} else if (cmd == REFRESH) {
				load();
			} else if (cmd == OPEN) {
				CollectionItem ci = null;
				//#if true
					//# FramedForm f = (FramedForm)mView;
					//# ci = (CollectionItem)f.getCurrentItem();
				//#endif
				try {
					mMidlet.platformRequest(mMidlet.mServerUrl + "service/home/~/?id="
								+ ci.mAttachment.mMsgId + "&part=" + ci.mAttachment.mPart + "&view=html");
				} catch (ConnectionNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else if (cmd == ZimbraME.CANCEL) {
				// Selecteable item scenario
				setNextCurrent();
			} else if (cmd == ZimbraME.OK) {
				//selectable item scenario
				if (mType == TAG_PICKER) {
					String[]tagList = computeTags();
					if (tagList != null && mListener != null) {
						mListener.action(this, tagList);
					}
					setNextCurrent();
				}
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

	protected void keyPressed(int keyCode,
		   	  				  int gameAction,
		   	  				  Item item) {
		CollectionItem ci = (CollectionItem)item;
		if (keyCode != Canvas.KEY_NUM5 && gameAction == Canvas.FIRE) {
			switch (mType) {
				case SAVEDSEARCH:
				case FOLDER_SEARCH:
					execSearch((CollectionItem)item);
					break;
				case ATTACHMENTLIST:
					try {
						mMidlet.platformRequest(mMidlet.mServerUrl + "/service/home/~/?id="
									+ ci.mAttachment.mMsgId + "&part=" + ci.mAttachment.mPart + "&view=html");
					} catch (ConnectionNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					break;
			}
		}
	}

	//TODO HANDLER MULTIPLY CHECKED ITEMS (FOLDER/TAG)
	private void execSearch(CollectionItem ci) {
		switch (mType) {
			case FOLDER_SEARCH:
				//TODO
				break;
			case SAVEDSEARCH:
				mMidlet.execSearch(ci.mSavedSearch.mQuery, ci.mSavedSearch.mSortBy, ci.mSavedSearch.mTypes); 
				break;
			case TAG_SEARCH:
			case TAG_PICKER:
				StringBuffer query = null;
				CollectionItem ci2;
				int sz = mView.size();
				for (int i = 0; i < sz; i++) {
					ci2 = (CollectionItem)mView.get(i);
					if (ci2.getSelected()) {
						if (query == null)
							query = new StringBuffer();
						else
							query.append(" AND ");
						query.append("tag:\"").append(ci2.mTag.mName).append("\"");
					}
				}
				if (query != null)
					mMidlet.execSearch(query.toString(), null, null); 
				break;
			}
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
			case FOLDER_PICK:
				header.setText(Locale.get("collectionView.Folders"));
				f.addCommand(REFRESH);
				f.addCommand(ZimbraME.OK);
				f.addCommand(ZimbraME.CANCEL);
				break;
			case FOLDER_SEARCH:
				header.setText(Locale.get("collectionView.Folders"));
				f.addCommand(ZimbraME.SEARCH);
				f.addCommand(REFRESH);
				f.addCommand(BACK);
				break;
			case SAVEDSEARCH:
				header.setText(Locale.get("collectionView.SavedSearches"));
				f.addCommand(ZimbraME.SEARCH);
				f.addCommand(REFRESH);
				f.addCommand(BACK);
				break;
			case TAG_SEARCH:
				header.setText(Locale.get("collectionView.Tags"));
				f.addCommand(ZimbraME.SEARCH);
				f.addCommand(REFRESH);
				f.addCommand(BACK);
				break;
			case TAG_PICKER:
				header.setText(Locale.get("collectionView.Tags"));
				f.addCommand(ZimbraME.SEARCH);
				f.addCommand(REFRESH);
				f.addCommand(ZimbraME.OK);
				f.addCommand(ZimbraME.CANCEL);
				break;
			case ATTACHMENTLIST:
				header.setText(Locale.get("collectionView.Attachments"));
				f.addCommand(OPEN);
				f.addCommand(BACK);
				break;

		}
		
		f.append(Graphics.TOP, header);
	}

	private String[] computeTags() {
		String[] tagIds = null;
		CollectionItem ci;
		boolean tagsChanged = false;
		int cnt = 0;
		int sz = mView.size();
		for (int i = 0; i < sz; i++) {
			ci = (CollectionItem)mView.get(i);
			if (ci.getSelected()) {
				cnt++;
				if (!tagsChanged) {
					// tag is selected. If it is not in mTags, then it is a new tag that has
					// been added to the item. Indicate that the tag set has changed
					if (mTags == null) {
						tagsChanged = true;
					} else {
						boolean found = false;
						for (int j = 0; j < mTags.length; j++) {
							if (mTags[j].compareTo(ci.mTag.mId) == 0) {
								found = true;
								break;
							}
						}
						if (!found)
							tagsChanged = true;
					}
				}
			} else {
				if (!tagsChanged) {
					// tag not selected. If the tag was in mTags, then it has now been removed so
					// indicate that the tag set has been changed  
					if (mTags != null) {
						boolean found = false;
						for (int j = 0; j < mTags.length; j++) {
							if (mTags[j].compareTo(ci.mTag.mId) == 0) {
								found = true;
								break;
							}
						}
						if (found)
							tagsChanged = true;
					}
				}
			}
		}
		
		if (!tagsChanged) {
			return null;
		} else {
			// Build the new tag list
			if (cnt > 0) {
				int idx = 0;
				tagIds = new String[cnt];
				for (int i = 0; i < sz; i++) {
					ci = (CollectionItem)mView.get(i);
					if (ci.getSelected())
						tagIds[idx++] = ci.mTag.mId;
				}
			} else {
				// Empty tag list
				tagIds = new String[1];
				tagIds[0] = "";
			}
			return tagIds;
		}
	}
	
}
