/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite, Network Edition.
 * Copyright (C) 2007, 2008 Zimbra, Inc.  All Rights Reserved.
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
import com.zimbra.zme.client.Folder;
import com.zimbra.zme.client.Mailbox;
import com.zimbra.zme.client.MailboxItem;
import com.zimbra.zme.client.SavedSearch;
import com.zimbra.zme.client.ZmeSvcException;

import de.enough.polish.ui.FramedForm;
import de.enough.polish.ui.Style;
import de.enough.polish.util.Locale;

public class CollectionView extends View implements ResponseHdlr {
	/* Different types of collection view. Trying to avoid creating a bunch
	 * of classes for each type as this seems to be a big no no with J2ME devlopment
	 */
	public static final int SAVEDSEARCH = 1;
    public static final int SAVEDSEARCH_PICK = 2;
	public static final int TAG_PICKER = 3;
	public static final int TAG_SEARCH = 4;
	public static final int FOLDER_SEARCH = 5;
	public static final int FOLDER_PICK = 6;
	public static final int ATTACHMENTLIST = 7;
	
	private ZmeStringItem mNoData;
	private Vector mAttachmentList;
	private String[] mTags;
	private int mType;
	private ZmeListener mListener;
    private Folder mSelected;
    private StringItem mHeader;

	private static final Command OPEN = new Command(Locale.get("main.Open"), Command.ITEM, 1);
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
        f.append(Graphics.TOP, mHeader);
		if (mType == FOLDER_PICK || mType == FOLDER_SEARCH) {
            Folder selected = mMidlet.mMbox.mRootFolder;
            if (mSelected != null)
                selected = mSelected;
            Vector folders = selected.mSubfolders;
            if (folders != null && folders.size() > 0) {
                for (Enumeration e = folders.elements(); e.hasMoreElements();) {
                    Folder currentFolder = (Folder)e.nextElement();
                    if (!currentFolder.showThisFolder())
                        continue;
                    //#style CollectionItem
                    CollectionItem c = new CollectionItem(mMidlet, this, currentFolder, false);
                    f.append(c);
                }
            }
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
			Vector collection = (mType == SAVEDSEARCH || mType == SAVEDSEARCH_PICK) ? mMidlet.mMbox.mSavedSearches : mMidlet.mMbox.mTags;
			if (collection != null && collection.size() > 0) {
				CollectionItem c;
                boolean selectable = (mType == TAG_SEARCH || mType == TAG_PICKER);
				for (Enumeration e = collection.elements(); e.hasMoreElements();) {
				    //#style CollectionItem
				    c = new CollectionItem(mMidlet, this, (MailboxItem)e.nextElement(), selectable);

				    if (mTags != null) {
				        for (int i = 0; i < mTags.length; i++) {
				            if (c.mItem.mId.compareTo(mTags[i]) == 0)
				                c.setSelected(true);
				        }
				    }
					f.append(c);
				}
				return;
			}
		}
		if (mNoData == null) {
			switch (mType) {
				case FOLDER_PICK:
				case FOLDER_SEARCH:
					//#style NoResultItem
					mNoData = new ZmeStringItem(mMidlet, this, Locale.get("collectionView.NoFolders"));
					break;
					
				case SAVEDSEARCH:
                case SAVEDSEARCH_PICK:
					//#style NoResultItem
					mNoData = new ZmeStringItem(mMidlet, this, Locale.get("collectionView.NoSavedSearches"));
					break;
					
				case TAG_PICKER:
				case TAG_SEARCH:
					//#style NoResultItem
					mNoData = new ZmeStringItem(mMidlet, this, Locale.get("collectionView.NoTags"));
					break;
					
				default:
					//#style NoResultItem
					mNoData = new ZmeStringItem(mMidlet, this, Locale.get("collectionView.NoData"));
					break;
			}
		}
		f.append(mNoData);
	}
	
	public void load() {
		switch (mType) {
			case SAVEDSEARCH:
            case SAVEDSEARCH_PICK:
				mMidlet.mMbox.getSavedSearches(this);
				Dialogs.popupWipDialog(mMidlet, this, Locale.get("collectionView.GettingSavedSearches"));
				break;
				
			case FOLDER_PICK:
			case FOLDER_SEARCH:
				mMidlet.mMbox.getFolders(this);
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
		//render();
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
					if (ci.mItem.mId.compareTo(tags[j]) == 0) {
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
                if (ci.mItem.mItemType != MailboxItem.ATTACHMENT)
                    return;
                Attachment att = (Attachment) ci.mItem;
				try {
                    mMidlet.openAttachment(att.mMsgId, att.mPart);
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
				    MailboxItem[]tags = computeTags();
					if (tags != null && mListener != null) {
						mListener.action(this, tags);
					}
					setNextCurrent();
				} else if (mType == FOLDER_PICK || mType == SAVEDSEARCH_PICK) {
                    CollectionItem ci = null;
                    //#if true
                        //# FramedForm f = (FramedForm)mView;
                        //# ci = (CollectionItem)f.getCurrentItem();
                    //#endif
                    mListener.action(this, ci.mItem);
                    setNextCurrent();
                }
			} else {
				super.commandAction(cmd, d, false);
			}
		} else if (d == Dialogs.mWipD) {
			mMidlet.mMbox.cancelOp();
			mMidlet.mDisplay.setCurrent(mView);
		} else if (d == Dialogs.mErrorD) {
			mMidlet.mDisplay.setCurrent(mView);
		} else {
			super.commandAction(cmd, d, false);
		}
	}
	
	public void handleResponse(Object op, 
							   Object resp) {
		//#debug
		System.out.println("CollectionView.handleResponse");
		if (resp instanceof Mailbox) {
			if (op == Mailbox.DELETEITEM) {
				CollectionItem ci = null;
				//#ifdef polish.usePolishGui
					//# ci = (CollectionItem)mView.getCurrentItem();
				//#endif
				if (ci.mItem.mItemType == MailboxItem.SAVEDSEARCH)
					mMidlet.mMbox.mSavedSearches.removeElement(ci.mItem);
				else if (ci.mItem.mItemType == MailboxItem.TAG)
					mMidlet.mMbox.mTags.removeElement(ci.mItem);
			}
			render();
			setCurrent();
		} else { //SZmeSvcException
			//#debug
			System.out.println("CollectionView.handleResponse: Fault from server");
			String ec = ((ZmeSvcException)resp).mErrorCode;
				
			if (ec == ZmeSvcException.MAIL_ALREADYEXISTS) {
				mMidlet.mDisplay.setCurrent(mView);
				Dialogs.popupErrorDialog(mMidlet, this, Locale.get("collectionView.ItemSameNameInTrash"));
			} else {
				mMidlet.handleResponseError(resp, this);
			}		
		}
	}

	public void cancel() {
		mMidlet.mMbox.cancelOp();
		mMidlet.mDisplay.setCurrent(mView);
	}

	protected void keyPressed(int keyCode,
		   	  				  int gameAction,
		   	  				  Item item) {
		
		if (item instanceof ZmeStringItem)
			return;
		
		CollectionItem ci = (CollectionItem)item;
		if (keyCode != Canvas.KEY_NUM5 && gameAction == Canvas.FIRE) {
			switch (mType) {
				case SAVEDSEARCH:
				case FOLDER_SEARCH:
					execSearch((CollectionItem)item);
					break;
				case ATTACHMENTLIST:
                    if (ci.mItem.mItemType != MailboxItem.ATTACHMENT)
                        break;
                    Attachment att = (Attachment) ci.mItem;
                    try {
                        mMidlet.openAttachment(att.mMsgId, att.mPart);
					} catch (ConnectionNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					break;
			}
        } else if (gameAction == Canvas.RIGHT && keyCode != Canvas.KEY_NUM6 && ci.mItem instanceof Folder) {
            //#debug
            System.out.println("RIGHT: keycode = "+keyCode+", gameAction = "+gameAction);
            Folder f = (Folder)ci.mItem;
            if (f != null && f.hasChildren()) {
                this.mSelected = f;
                render();
            }
        } else if (gameAction == Canvas.LEFT && keyCode != Canvas.KEY_NUM4 && ci.mItem instanceof Folder) {
            //#debug
            System.out.println("LEFT:  keycode = "+keyCode+", gameAction = "+gameAction);
            Folder f = (Folder)ci.mItem;
            if (f != null && f.mParent != null && f.mParent.mParent != null) {
                this.mSelected = f.mParent.mParent;
                render();
            }
		} else if (keyCode == Canvas.KEY_NUM7) {
			if (confirmDeletes())
				Dialogs.popupConfirmDialog(mMidlet, this, Locale.get("main.DeleteConfirm"));
			else 
				deleteItemConfirmed();
		}
	}

	protected void deleteItemConfirmed() {
		CollectionItem ci = null;
		//#ifdef polish.usePolishGui
			//# ci = (CollectionItem)mView.getCurrentItem();
		//#endif
		String id = ci.mItem.mId;
		mMidlet.mMbox.deleteItem(id, this);
	}
	
	private void execSearch(CollectionItem ci) {
		switch (mType) {
			case FOLDER_SEARCH:
                if (ci.mItem instanceof Folder)
                    mMidlet.gotoFolder(((Folder)ci.mItem).getPath().toString());
				break;
			case SAVEDSEARCH:
                SavedSearch ss = (SavedSearch) ci.mItem;
				mMidlet.execSearch(ss.mQuery, ss.mSortBy, ss.mTypes); 
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
						query.append("tag:\"").append(ci2.mItem.mName).append("\"");
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
		mHeader = new StringItem(null, "");
		
		switch (mType) {
			case FOLDER_PICK:
				mHeader.setText(Locale.get("collectionView.Folders"));
				f.addCommand(ZimbraME.OK);
				f.addCommand(REFRESH);
				f.addCommand(ZimbraME.CANCEL);
				break;
			case FOLDER_SEARCH:
				mHeader.setText(Locale.get("collectionView.Folders"));
				f.addCommand(ZimbraME.SEARCH);
				f.addCommand(REFRESH);
				f.addCommand(BACK);
				break;
			case SAVEDSEARCH:
				mHeader.setText(Locale.get("collectionView.SavedSearches"));
				f.addCommand(ZimbraME.SEARCH);
				f.addCommand(REFRESH);
				f.addCommand(DELETE);
				f.addCommand(BACK);
				break;
			case TAG_SEARCH:
				mHeader.setText(Locale.get("collectionView.Tags"));
				f.addCommand(ZimbraME.SEARCH);
				f.addCommand(REFRESH);
				f.addCommand(BACK);
				break;
			case TAG_PICKER:
				mHeader.setText(Locale.get("collectionView.Tags"));
				f.addCommand(ZimbraME.OK);
				f.addCommand(REFRESH);
				f.addCommand(ZimbraME.CANCEL);
				break;
            case SAVEDSEARCH_PICK:
                mHeader.setText(Locale.get("collectionView.SavedSearches"));
                f.addCommand(ZimbraME.OK);
                f.addCommand(REFRESH);
                f.addCommand(BACK);
                break;
			case ATTACHMENTLIST:
				mHeader.setText(Locale.get("collectionView.Attachments"));
				f.addCommand(OPEN);
				f.addCommand(BACK);
				break;

		}
		
		f.append(Graphics.TOP, mHeader);
	}

	private MailboxItem[] computeTags() {
		MailboxItem[] tags = null;
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
							if (mTags[j].compareTo(ci.mItem.mId) == 0) {
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
							if (mTags[j].compareTo(ci.mItem.mId) == 0) {
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
				tags = new MailboxItem[cnt];
				for (int i = 0; i < sz; i++) {
					ci = (CollectionItem)mView.get(i);
					if (ci.getSelected())
						tags[idx++] = ci.mItem;
				}
			} else {
				// Empty tag list
				tags = new MailboxItem[0];
			}
			return tags;
		}
	}
	
}
