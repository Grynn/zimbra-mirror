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
import javax.microedition.lcdui.ItemStateListener;
import javax.microedition.lcdui.StringItem;
import javax.microedition.lcdui.TextField;

import com.zimbra.zme.ResponseHdlr;
import com.zimbra.zme.ZimbraME;
import com.zimbra.zme.ZmeListener;
import com.zimbra.zme.client.Attachment;
import com.zimbra.zme.client.Contact;
import com.zimbra.zme.client.Folder;
import com.zimbra.zme.client.Mailbox;
import com.zimbra.zme.client.MailboxItem;
import com.zimbra.zme.client.SavedSearch;
import com.zimbra.zme.client.ZmeSvcException;

import de.enough.polish.ui.FramedForm;
import de.enough.polish.ui.Style;
import de.enough.polish.util.Locale;

public class CollectionView extends View implements ResponseHdlr, ItemStateListener {
	
	public static final int SAVEDSEARCH = 1;
    public static final int SAVEDSEARCH_PICKER = 2;
	public static final int TAG_PICKER = 3;
	public static final int TAG_SEARCH = 4;
	public static final int FOLDER_SEARCH = 5;
	public static final int FOLDER_PICKER = 6;
	public static final int ATTACHMENTLIST = 7;
	public static final int CONTACT = 8;
	
	protected ZmeStringItem mNoData;
	protected Vector mAttachmentList;
	protected String[] mTags;
	protected int mType;
	protected ZmeListener mListener;
    protected Folder mSelected;
    protected StringItem mHeader;
    protected SelectedItems mSelection;
    protected TextField mInputField;

	private static final Command OPEN = new Command(Locale.get("main.Open"), Command.ITEM, 1);
	private static final Command REFRESH = new Command(Locale.get("main.Refresh"), Command.ITEM, 1);
	private static final Command NEW = new Command(Locale.get("addressPicker.New"), Command.ITEM, 1);
	private static final Command DONE = new Command(Locale.get("addressPicker.Done"), Command.CANCEL, 1);

	//#ifdef polish.usePolishGui
		public CollectionView(ZimbraME midlet,
							  int type,
			       			  Style style) {
			super(midlet);
			//#if true
				//# mView = new FramedForm(null, style);
			//#endif
			init(type);
			mView.setItemStateListener(this);
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
	
	public static CollectionView folderSearchView(ZimbraME midlet) {
        //#style CollectionView
		CollectionView cv = new FolderSearchView(midlet, false);
		return cv;
	}
	public static CollectionView folderPickerView(ZimbraME midlet) {
        //#style CollectionView
		CollectionView cv = new FolderSearchView(midlet, true);
		return cv;
	}
	public static CollectionView savedSearchView(ZimbraME midlet) {
        //#style CollectionView
		return new SavedSearchView(midlet, false);
	}
	public static CollectionView savedSearchPickerView(ZimbraME midlet) {
        //#style CollectionView
		return new SavedSearchView(midlet, true);
	}
	public static CollectionView attachmentView(ZimbraME midlet) {
        //#style CollectionView
		return new AttachmentView(midlet);
	}
	public static CollectionView tagSearchView(ZimbraME midlet) {
        //#style CollectionView
		return new TagSearchView(midlet, false);
	}
	public static CollectionView tagPickerView(ZimbraME midlet) {
        //#style CollectionView
		return new TagSearchView(midlet, true);
	}
	public static CollectionView contactView(ZimbraME midlet) {
        //#style CollectionView
		return new ContactView(midlet);
	}
	
	private static class FolderSearchView extends CollectionView {
		//#ifdef polish.usePolishGui
		FolderSearchView(ZimbraME midlet, boolean picker, Style style) {
			super(midlet, picker ? FOLDER_PICKER : FOLDER_SEARCH, style);
		}
		//#else
		FolderSearchView(ZimbraME midlet, boolean picker) {
			super(midlet, picker ? FOLDER_PICKER : FOLDER_SEARCH);
		}
		//#endif
		protected Enumeration getItems() {
			Folder f = mSelected;
			if (f == null)
				f = mMidlet.mMbox.mRootFolder;
			return f.mSubfolders.elements();
		}
		public void load() {
			Dialogs.popupWipDialog(mMidlet, this, Locale.get("collectionView.GettingFolders"));
			mMidlet.mMbox.getFolders(this);
		}
		protected void addCommands(Displayable d) {
			if (mType == FOLDER_SEARCH)
				d.addCommand(ZimbraME.SEARCH);
			else
				d.addCommand(ZimbraME.OK);
			d.addCommand(REFRESH);
			d.addCommand(BACK);
		}
		protected String getNoDataItem() { return Locale.get("collectionView.NoFolders"); }
		protected String getHeader() { return Locale.get("collectionView.Folders"); }
	}
	
	private static class SavedSearchView extends CollectionView {
		//#ifdef polish.usePolishGui
		SavedSearchView(ZimbraME midlet, boolean picker, Style style) {
			super(midlet, picker ? SAVEDSEARCH_PICKER : SAVEDSEARCH, style);
		}
		//#else
		SavedSearchView(ZimbraME midlet, boolean picker) {
			super(midlet, picker ? SAVEDSEARCH_PICKER : SAVEDSEARCH);
		}
		//#endif
		protected Enumeration getItems() {
			return mMidlet.mMbox.mSavedSearches.elements();
		}
		public void load() {
			Dialogs.popupWipDialog(mMidlet, this, Locale.get("collectionView.GettingSavedSearches"));
			mMidlet.mMbox.getSavedSearches(this);
		}
		protected void addCommands(Displayable d) {
			if (mType == SAVEDSEARCH)
				d.addCommand(ZimbraME.SEARCH);
			else
				d.addCommand(ZimbraME.OK);
			d.addCommand(REFRESH);
			d.addCommand(DELETE);
			d.addCommand(BACK);
		}
		protected boolean hasSearchField() { return (mType == SAVEDSEARCH) ? true : false; }
		protected String getSearchField() { return Locale.get("main.NewSearch"); }
		protected String getNoDataItem() { return Locale.get("collectionView.NoSavedSearches"); }
		protected String getHeader() { return Locale.get("collectionView.SavedSearches"); }
	}

	private static class AttachmentView extends CollectionView {
		//#ifdef polish.usePolishGui
		AttachmentView(ZimbraME midlet, Style style) {
			super(midlet, ATTACHMENTLIST, style);
		}
		//#else
		AttachmentView(ZimbraME midlet) {
			super(midlet, ATTACHMENTLIST);
		}
		//#endif
		protected Enumeration getItems() {
			return mAttachmentList.elements();
		}
		protected void addCommands(Displayable d) {
			d.addCommand(OPEN);
			d.addCommand(BACK);
		}
		protected String getHeader() { return Locale.get("collectionView.Attachments"); }
	}
	
	private static class TagSearchView extends CollectionView {
		//#ifdef polish.usePolishGui
		TagSearchView(ZimbraME midlet, boolean picker, Style style) {
			super(midlet, picker ? TAG_PICKER : TAG_SEARCH, style);
		}
		//#else
		TagSearchView(ZimbraME midlet, boolean picker) {
			super(midlet, picker ? TAG_PICKER : TAG_SEARCH);
		}
		//#endif
		protected Enumeration getItems() {
			return mMidlet.mMbox.mTags.elements();
		}
		public void load() {
			Dialogs.popupWipDialog(mMidlet, this, Locale.get("collectionView.GettingTags"));
			mMidlet.mMbox.getTags(this);
		}
		protected void addCommands(Displayable d) {
			if (mType == TAG_SEARCH)
				d.addCommand(ZimbraME.SEARCH);
			else
				d.addCommand(ZimbraME.OK);
			d.addCommand(REFRESH);
			d.addCommand(BACK);
		}
		protected boolean isSelectable() { return true; }
		protected String getNoDataItem() { return Locale.get("collectionView.NoTags"); }
		protected String getHeader() { return Locale.get("collectionView.Tags"); }
	}
	
	private static class ContactView extends CollectionView {
		//#ifdef polish.usePolishGui
		ContactView(ZimbraME midlet, Style style) {
			super(midlet, CONTACT, style);
		}
		//#else
		ContactView(ZimbraME midlet) {
			super(midlet, CONTACT);
		}
		//#endif
		public void load() {
			Dialogs.popupWipDialog(mMidlet, this, Locale.get("main.LoadingContacts"));
			mMidlet.mMbox.getContacts(this);
		}
		public void commandAction(Command cmd, 
				  Displayable d) {
			if (d == mView) {
				if (cmd == DONE) {
					mSelection = new SelectedItems();
					mListener.action(this, mSelection.computeSelection());
					setNextCurrent();
				} else if (cmd == NEW) {
				}
			}
		}
		private Vector contacts;
		protected Enumeration getItems() {
			if (contacts == null) {
				contacts = new Vector();
				Enumeration e = mMidlet.mMbox.mContacts.elements();
				while (e.hasMoreElements())
					contacts.addElement(e.nextElement());
			}
			return contacts.elements();
		}
		protected void addCommands(Displayable d) {
			d.addCommand(NEW);
			d.addCommand(DONE);
		}
		public void itemStateChanged(Item item) {
			//#debug
			System.out.println("ContactView: itemStateChanged");
			if (item instanceof TextField) {
				String stem = ((TextField)item).getString();
				Enumeration e = mMidlet.mMbox.mContacts.elements();
				contacts.removeAllElements();
				while (e.hasMoreElements()) {
					Contact c = (Contact)e.nextElement();
					
					if ((c.mFirstName != null && (stem == null || c.mFirstName.toLowerCase().startsWith(stem)))
							|| (c.mLastName != null && (stem == null || c.mLastName.toLowerCase().startsWith(stem)))) {
						contacts.addElement(c);
					} else if ((stem == null || c.mEmail.toLowerCase().startsWith(stem))) {
						contacts.addElement(c);
					}
				}
				render();
			}
		}
		protected boolean hasSearchField() { return true; }
		protected String getSearchField() { return Locale.get("main.Search"); }
		protected boolean isSelectable() { return true; }
		protected String getNoDataItem() { return Locale.get("contactListView.NoContacts"); }
		protected String getHeader() { return Locale.get("contactListView.ContactPicker"); }
	}
	
	protected Enumeration getItems() {
		return new Enumeration() {
			public boolean hasMoreElements() { return false; }
			public Object nextElement() { return null; }
		};
	}
	
	protected boolean isSelectable() { return false; }
	protected boolean hasSearchField() { return false; }
	protected String getSearchField()  { return null; }
	protected String getNoDataItem() { return Locale.get("collectionView.NoData"); }
	protected String getHeader() { return ""; }
	protected void addCommands(Displayable d) { }
	
	public void render() {
		FramedForm f = null;
		//#if true
			//# f = (FramedForm)mView;
		//#endif
		f.deleteAll();
        f.append(Graphics.TOP, mHeader);
        if (hasSearchField()) {
        	if (mInputField == null) {
                //#style CollectionInputField
                mInputField = new TextField(getSearchField(), null, 128, TextField.ANY);
        	}
            f.append(mInputField);
        }
        boolean noItems = true;
        for (Enumeration e = getItems(); e.hasMoreElements();) {
            MailboxItem item = (MailboxItem)e.nextElement();
            
            if (item instanceof Folder && !((Folder)item).showThisFolder())
            	continue;
            
            //#style CollectionItem
            CollectionItem c = new CollectionItem(mMidlet, this, item, isSelectable());
            f.append(c);
            noItems = false;
        }
        if (noItems) {
			//#style NoResultItem
			mNoData = new ZmeStringItem(mMidlet, this, getNoDataItem());
			f.append(mNoData);
        }
	}
	
	public void setListener(ZmeListener listener) {
		mListener = listener;
	}
	
	public void load(Vector attachmentList) {
		mAttachmentList = attachmentList;
		//render();
	}
	
	private class SelectedItems {
		public void markSelection() {}
		public Vector computeSelection() {
			Vector v = new Vector();
			for (int i = 0; i < mView.size(); i++) {
				Object o = mView.get(i);
				if (!(o instanceof CollectionItem))
					continue;
				CollectionItem item = (CollectionItem) o;
				if (item.getSelected())
					v.addElement(item.mItem);
			}
			return v;
		}
		public boolean isChanged() { return false; }
	}
	
	private class SelectedTags extends SelectedItems {
		String[] mTags;
		public SelectedTags(String[] tagIds) {
			mTags = tagIds;
		}
		public void markSelection() {
			if (mView.size() == 1 && (mView.get(0) instanceof ZmeStringItem))
				return;
			for (int elem = 0; elem < mView.size(); elem++) {
				CollectionItem item = (CollectionItem)mView.get(elem);
				if (item.mItem.mItemType != MailboxItem.TAG) {
					continue;
				}
				boolean selected = false;
				for (int i = 0; i < mTags.length; i++) {
					if (item.mItem.mId.compareTo(mTags[i]) == 0) {
						selected = true;
						break;
					}
				}
				item.setSelected(selected);
			}
		}
		public boolean isChanged() {
			for (int i = 0; i < mView.size(); i++) {
				CollectionItem item = (CollectionItem) mView.get(i);
				boolean found = false;
				for (int t = 0; t < mTags.length; t++) {
					if (mTags[t].equals(item.mItem.mId))
						found = true;
				}
				if (item.getSelected() && !found)
					return true;
				else if (!item.getSelected() && found)
					return true;
			}
			return false;
		}
	}
	public void setTags(String[] tags) {
		mSelection = new SelectedTags(tags);
		mSelection.markSelection();
	}
	
	public void commandAction(Command cmd, 
							  Displayable d) {
		if (d == mView) {
			Item item = null;
			//#if true
				//# FramedForm f = (FramedForm)mView;
				//# item = (Item)f.getCurrentItem();
			//#endif
			if (cmd == BACK) {
				setNextCurrent();
			} else if (cmd == ZimbraME.SEARCH) {
				if (item != null) {
					if (item instanceof TextField) {
						String txt = ((TextField)item).getString();
						mMidlet.execSearch(txt, null, null); 
					} else if (item instanceof CollectionItem) {
						execSearch((CollectionItem)item);
					}
				}
			} else if (cmd == REFRESH) {
				load();
			} else if (cmd == OPEN) {
				CollectionItem ci = (CollectionItem)item;
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
			} else if (cmd == ZimbraME.OK || cmd == DONE) {
				//selectable item scenario
				if (mListener != null) {
					if (mType == TAG_PICKER || mType == CONTACT) {
						Vector selection = mSelection.computeSelection();
						if (selection.size() > 0) {
							mListener.action(this, selection);
						}
					} else if (mType == FOLDER_PICKER || mType == SAVEDSEARCH_PICKER) {
	                    CollectionItem ci = (CollectionItem)item;
	                    mListener.action(this, ci.mItem);
	                }
				}
				setNextCurrent();
			} else if (item instanceof CollectionItem) {
				super.commandAction(cmd, d, false);
			}
		} else if (d == Dialogs.mWipD) {
			mMidlet.mMbox.cancelOp();
			mMidlet.mDisplay.setCurrent(mView);
		} else if (d == Dialogs.mErrorD) {
			mMidlet.mDisplay.setCurrent(mView);
		} else if (d == Dialogs.mConfirmD && cmd == Dialogs.NO) {
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
		} else {
			super.keyPressed(keyCode, gameAction, item);
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
							query.append(" ");//query.append(" AND ");
						query.append("tag:\"").append(ci2.mItem.mName).append("\"");
					}
				}
				if (query != null)
					mMidlet.execSearch(query.toString(), null, null); 
				break;
			}
	}
	
	public void itemStateChanged(Item item) {
	}
	
	protected void init(int type) {
		mType = type;
		FramedForm f = null;
		//#if true
			//# f = (FramedForm)mView;
		//#endif
		
		f.setCommandListener(this);
		//#style CollectionViewHeader
		mHeader = new StringItem(null, getHeader());
		f.append(Graphics.TOP, mHeader);
		addCommands(f);
	}
}
