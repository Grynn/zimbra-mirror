/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Zimbra Collaboration Suite, Network Edition.
 * Copyright (C) 2007 Zimbra, Inc.  All Rights Reserved.
 * 
 * ***** END LICENSE BLOCK *****
 */


package com.zimbra.zme.ui;

import java.util.Vector;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;

import com.zimbra.zme.ResponseHdlr;
import com.zimbra.zme.Util;
import com.zimbra.zme.ZimbraME;
import com.zimbra.zme.client.Mailbox;

import de.enough.polish.ui.Style;
import de.enough.polish.util.Locale;
import de.enough.polish.util.TextUtil;

// DO NOT REMOVE STATEMENTS BELOW. SEE COMMENTS IN setStyle METHOD
//#if true
	//# import de.enough.polish.ui.StyleSheet;
	//# import de.enough.polish.ui.FramedForm;
//#endif

public class MsgItem extends MailItem implements ResponseHdlr {

	private static final String TO = Locale.get("msgItem.To");
	
	private static final int DEF_NUM_ADDRS = 5;
	
	private Font mFont;
	private int mFontHeight;
	private int mFontColor;

	public String mCId; // Conversation ID
	
	public boolean mInvite; // True: invite message
    public String mApptId;
	
	public String mSender;
	public String mSenderAddr;
	
	public String mFrom;
	public String mFromAddr;
	public String mFromStr;
	
	public String mReplyTo;
	public String mReplyToAddr;
	
	public boolean mLoaded; // If true msg item is loaded
	
	private String[] mToRecipients; // Recipients (To:) display names
	private String[] mToRecipientsAddr; // Recipients (To:) email addrs
	private int mNumToRecipients;
	private String mToRecipientsStr; // Display string (possibly elided)

	private String[] mCcRecipients; // Recipients (Cc:) display names
	private String[] mCcRecipientsAddr; // Recipients (Cc:) email addrs
	private int mNumCcRecipients;
	//private String mCcRecipientsStr; // Display string (possibly elided)

	public Vector mAttachments;
	private StringBuffer mBody; //  msg body
	private String[] mBodyLines;
	
	private boolean mExpanded; // If true then the msg item is expanded
	private boolean mExpanding; // True if expansion is in progress due to user action
	private boolean mStateChanged; // true if expand state changed
	//private int mApptBtnWidth; // Button width for appt buttons for invites
	
	//#ifdef polish.usePolishGui
		public MsgItem(ZimbraME m,
					   MailListView parentView,
					   Style style) {
			super(m, parentView, style);
		}
	//#else
		public MsgItem(ZimbraME m,
				   	   MailListView parentView) {
			super(m, parentView);
		}
	//#endif
	
	public MsgItem append2Body(String str) {
		if (mBody == null)
			mBody = new StringBuffer();
		mBody.append(str);
		mDirty = true;
		return this;
	}
	
	public StringBuffer getBody() {
		return mBody;
	}

	/**
	 * Sets the items from address
	 * @param from
	 */
	public void setMsgFrom(String fromAddr,
						   String fromDispName) {
		mFromAddr = fromAddr;
		mFrom = fromDispName;
		mDirty = true;	
		if (mVisible)
			repaint();
	}
	
	/**
	 * Sets the items sender address
	 * @param senderAddr sender's email address
	 * @param senderDispName sender's display name
	 */
	public void setMsgSender(String senderAddr,
			                 String senderDispName) {
		mSenderAddr = senderAddr;
		mSender = senderDispName;
		mDirty = true;	
		if (mVisible)
			repaint();
	}

	/**
	 * Sets the items replyto address
	 * @param replyToAddr replyTo address
	 * @param replyToDispName replyTo display name
	 */
	public void setMsgReplyTo(String replyToAddr,
			                  String replyToDispName) {
		mReplyToAddr = replyToAddr;
		mReplyTo = replyToDispName;
		/*mDirty = true;	
		if (mVisible)
			repaint();*/
	}
	

	public String[] getToRecipientsAddr() {
		return mToRecipientsAddr;
	}
	
	public String[] getToRecipients() {
		return mToRecipients;
	}

	public int getNumToRecipients() {
		return mNumToRecipients;
	}
	

	/**
	 * Add a To recipient to the message
	 * @param recipient
	 */
	public void addToRecipient(String toAddr,
			                   String toDispName) {
		if (mToRecipientsAddr == null) {
			mToRecipientsAddr = new String[DEF_NUM_ADDRS];
			mToRecipients = new String[DEF_NUM_ADDRS];
			mNumToRecipients = 0;
		} else if (mNumToRecipients == mToRecipientsAddr.length) {
			int newLen = mNumToRecipients + DEF_NUM_ADDRS;
			String[] newAddrArray = new String[newLen];
			String[] newDispNameArray = new String[newLen];
			for (int i = 0; i < mNumToRecipients; i++) {
				newAddrArray[i] = mToRecipientsAddr[i];
				newDispNameArray[i] = mToRecipients[i];
			}
			mToRecipientsAddr = newAddrArray;
			mToRecipients = newDispNameArray;
		}
		mToRecipientsAddr[mNumToRecipients] = toAddr;
		mToRecipients[mNumToRecipients++] = (toDispName != null) ? toDispName : "";
		
		mDirty = true;
		if (mVisible)
			repaint();	
	}
	
	public String[] getCcRecipientsAddr() {
		return mCcRecipientsAddr;
	}	

	public String[] getCcRecipients() {
		return mCcRecipients;
	}	
	
	public int getNumCcRecipients() {
		return mNumCcRecipients;
	}

	/**
	 * Add a To recipient to the message
	 * @param recipient
	 */
	public void addCCRecipient(String ccAddr,
			                   String ccDispName) {
		if (mCcRecipientsAddr == null) {
			mCcRecipientsAddr = new String[DEF_NUM_ADDRS];
			mCcRecipients = new String[DEF_NUM_ADDRS];
			mNumCcRecipients = 0;
		} else if (mNumCcRecipients == mCcRecipientsAddr.length) {
			int newLen = mNumCcRecipients + DEF_NUM_ADDRS;
			String[] newAddrArray = new String[newLen];
			String[] newDispNameArray = new String[newLen];
			for (int i = 0; i < mNumCcRecipients; i++) {
				newAddrArray[i] = mCcRecipientsAddr[i];
				newDispNameArray[i] = mCcRecipients[i];
			}
			mCcRecipientsAddr = newAddrArray;
			mCcRecipients = newDispNameArray;
		}
		mCcRecipientsAddr[mNumCcRecipients] = ccAddr;
		mCcRecipients[mNumCcRecipients++] = ccDispName;
		mDirty = true;
		if (mVisible)
			repaint();	
	}
	
	/** 
	 * Expand/Collapse the msg item. Exanding it will (a) load the message content if it has 
	 * not already been loaded (b) visually expands the item to show the content. If the content
	 * is already expanded, then it will be collapsed 
	 * 
	 * @param expanded true: expand item, false: collapse item
	 */
	public void setExpanded(boolean expanded) {		
		/* If we are already expanding (due to user action since that is how mExpanding is set!), then
		 * don't do anything */
		if (expanded == mExpanded || (mExpanding && !mLoaded))
			return;	
		
		mStateChanged = true;
		
		if (expanded) {
			if (!mLoaded) {
				//setUpdating(true, Locale.get("msgList.RetrievingMsg"));
				mMidlet.mMbox.loadMsg(this);
				Dialogs.popupWipDialog(mMidlet, mParentView, Locale.get("msgList.RetrievingMsg"));
				mExpanding = true;
			} else {
				mExpanded = true;
				invalidate();
			}	
		} else {
			mExpanded = false;
			invalidate();
		}
	}
	
	/**
	 * Indicates that the mail message has been loaded. This is typically called
	 * when a conversation is loaded and the first message is expanded. 
	 */
	public void setLoaded() {
		mLoaded = true;
		mDirty = true;
		mParentView.itemStateChanged(this, MailListView.MSG_LOADED);
	}

	/* This method is the response handler that is called by the Mailbox code 
	 * when the message is loaded */ 
	public void handleResponse(Object op, 
							   Object resp) {
		//#debug
		System.out.println("MsgItem.handleResponse");
		if (op == Mailbox.GETMSG && mExpanding) {
			mExpanding = false;
			//setUpdating(false, "");
			//repaint(); // To force the icon to repaint to the status icon
            if (resp instanceof Mailbox) {
                if (op == Mailbox.GETMSG) {
                    /* Note that loading the message causes the setLoaded() to be called
                     * which set's the dirty flag for this item. When the item is next displayed
                     * it will be refreshed accordingly
                     */
                    //#debug 
                    System.out.println("MsgItem.handleResponse: GetMsg successful");
                    // Dismiss WIP Dialog
                    mMidlet.mDisplay.setCurrent(mParentView.mView);
                    if (getUnread() != true)
                        mParentView.itemStateChanged(this, MailListView.UNREAD_CHANGED);
                } else {
                    super.handleResponse(op, resp);
                }   
            } else {
                mMidlet.handleResponseError(resp, mParentView);
            }
		} else if (op == Mailbox.INVITEREPLY) {
		    if (resp instanceof Mailbox)
                mParentView.itemStateChanged(this, MailListView.INVITE_REPLIED);
		    else
		        mMidlet.handleResponseError(resp, mParentView);
        }
	}
	
	protected int getMinContentHeight() {
		return MINIMUM_CONTENT_HEIGHT;
	}

	protected int getMinContentWidth() {
		return Display.getDisplay(mMidlet).getCurrent().getWidth();
	}

	protected int getPrefContentHeight(int width) {
		if (!mExpanded) {
			return mFontHeight;
		} else {
			int h = 0;
			if (mBodyLines != null) {
				// Spacing for the separator line and spacing on either side of it
				h = SPACING * 2 + 1;
				for (int i = 0; i < mBodyLines.length; i++)
					h += mFontHeight + SPACING;
			}
			// Font height for the From + SPACING 
			// Font height for the To + SPACING 
			// Body lines
			return mFontHeight + SPACING + mFontHeight + SPACING + 1 + SPACING + h; 
		}
	}

	protected int getPrefContentWidth(int height) {
		return Display.getDisplay(mMidlet).getCurrent().getWidth();
	}

	protected void paint(Graphics g, 
			 			 int w, 
			 			 int h) {
		
        boolean hasAttachment = (mFlags & ATTACHMENT) > 0 || mAttachments != null;
		if (mNumPendingUpdates > 0) {
			drawUpdating(g, w, h, mFont, mFontColor);
			return;
		}

		/* there is lots of room for optimization here.*/
		if (mDirty) {
			if (mInvite) {
				mIcon = INVITE_ICON;
				mIconWidth = mIcon.getWidth();
			} else if ((mFlags & UNREAD) == UNREAD) {
				mIcon = UNREADMSG_ICON;
				mIconWidth = mIcon.getWidth();
			} else {
				mIcon = READMSG_ICON;
				mIconWidth = mIcon.getWidth();
			}
			
			mDateWidth = mFont.stringWidth(mDateStr);
			int newWidth = w - mDateWidth - SPACING * 2; // minimum separation
			
			if (hasAttachment)
				newWidth -= (SPACING + ATTACH_ICON_WIDTH);
			
			if ((mFlags & FLAGGED) == FLAGGED)
				newWidth -= (SPACING + FLAG_ICON_WIDTH);
			
			newWidth -= (SPACING + mIconWidth);
			
			mToRecipientsStr = getParticipantsStr(mToRecipients, mNumToRecipients, newWidth, mFont);
			
			int fromDims = w - SPACING * 2;
			mFromStr = Util.elidString(mFrom, fromDims, mFont);
			
			if (mBody != null)
				mBodyLines = TextUtil.wrap(mBody.toString(), mFont, w, w);

			mDirty = false;
		}
		
		if (mStateChanged) {
			mStateChanged = false;
			setStyle(null);
			invalidate();
			repaint();
			return;
		}

		g.setFont(mFont);
		g.setColor(mFontColor);
	
		// Draw Icon
		g.drawImage(mIcon, SPACING, 0, Graphics.TOP | Graphics.LEFT);
		
		// Draw the From address
		g.drawString(mFromStr, SPACING * 2 + mIconWidth, 0, Graphics.TOP | Graphics.LEFT);
	
		int cursorX = w - SPACING * 2 - mDateWidth;
		
		// Draw the attachment icon right next to the dateif we have an attachment
		if (hasAttachment) {
			g.drawImage(ATTACH_ICON, cursorX, SPACING, Graphics.TOP | Graphics.RIGHT);
			cursorX -= (SPACING + ATTACH_ICON_WIDTH);
		}
		
		// Draw the flag icon to the right of the date (attachment icon) if the message is flagged
		if ((mFlags & FLAGGED) == FLAGGED)
			g.drawImage(FLAG_ICON, cursorX, SPACING, Graphics.TOP | Graphics.RIGHT);
		
		g.setFont(mFont);
	
		// Draw the date to right-aligned
		g.drawString(mDateStr, w - SPACING, 0, Graphics.TOP | Graphics.RIGHT);
	
		if (mExpanded) {
			// The second line of will display the status icon (e.g new, forwarded etc) and the subject.
			// It also shows if the message is flagged 
			// TODO show flagged
			int cursor = mFontHeight + SPACING;
			g.drawString(TO, SPACING, cursor, Graphics.TOP | Graphics.LEFT);
			g.drawString(mToRecipientsStr, SPACING * 2 + mFont.stringWidth(TO), cursor, Graphics.TOP | Graphics.LEFT);

			if (mInvite) {
				//TODO invite handling code - draw buttons etc...
			}
			
			if (mBodyLines != null) {
				cursor += SPACING + mFontHeight;
				g.drawLine(SPACING, cursor, w - SPACING, cursor);
				cursor += SPACING;
				for (int i = 0; i < mBodyLines.length; i++) {
					g.drawString(mBodyLines[i], SPACING, cursor, Graphics.TOP | Graphics.LEFT);
					cursor += SPACING + mFontHeight;
				}
			}
		}
	}

	protected void traverseOut() {
        super.traverseOut();
		//TODO do we actually need to call this give that it is called in paint and a traverse 
		// out probably triggers a paint
		setStyle(null);
	}
	
	protected void handleKeyPress(int keyCode) {
		int gameAction = getGameAction(keyCode);
		if (keyCode != Canvas.KEY_NUM5 && gameAction == Canvas.FIRE) {
			setExpanded(!mExpanded); 
		} else if (keyCode != Canvas.KEY_NUM8 && gameAction == Canvas.DOWN){
			MsgItem m = null;
			//#if true
				//# FramedForm f = (FramedForm)mParentView.mView;
				//# m = (MsgItem)f.get(f.size() - 1);
			//#endif
			if (m == this)
				mParentView.getMore(this);
		} else if (keyCode == Canvas.KEY_NUM9 && !mParentView.mFragmentShowing 
				   && mFragment != null && mFragment.length() > 0) {
				Dialogs.showStatusMsg(mFragment, mParentView.mView, false);
				mParentView.mFragmentShowing = true;
				return;
		} else {
			mParentView.keyPressed(keyCode, gameAction, this);
		}
		mParentView.mFragmentShowing = false;
		Dialogs.hideScreenInfo();
	}

	/**
	 * This is called by the J2ME Polish infrastructure to set the style on the item
	 * @param style
	 */
	public void setStyle(Style style) {

		/* The game we are playing here is by knowing that J2ME Polish pre-processing creates
		 * a StyleSheet class wherein each class in polish.css is realized into a public static
		 * attribute. So a css style MsgItem will become a public static attribute named msgitemStyle
		 * This is not the best solution, but I have tried several other strategies which just did
		 * not work, so I am left with this until a better way is found 
		 */ 
		
		// Fool J2ME Polish into adding the styles to the StyleSheet class by referecing them. Note this code
		// will be compiled out by the Java Compiler
		if (false) {
			//#style MsgItemExpandedFocused
			MsgItem m = new MsgItem(mMidlet, mParentView);
			//#style MsgItemExpanded
			m = new MsgItem(mMidlet, mParentView);
			//#style MsgItem
			m = new MsgItem(mMidlet, mParentView);
			//#style MsgItemFocused
			m = new MsgItem(mMidlet, mParentView);
		}

		//#if true
			//# if (mExpanded)
				//# style = (mSelected) ? StyleSheet.msgitemexpandedfocusedStyle
				//#					: StyleSheet.msgitemexpandedStyle;		
			//# else
				//# style = (mSelected) ? StyleSheet.msgitemfocusedStyle
				//#					: StyleSheet.msgitemStyle;		
			//# super.setStyle(style);
		//#endif
		
		if (mFont != style.font) {
			mFont = style.font;
			mFontHeight = mFont.getHeight();	
			//mApptBtnWidth = Math.min(mFont.stringWidth(ACCEPT), 
			//						 Math.min(mFont.stringWidth(DECLINE), mFont.stringWidth(TENTATIVE)));
		}

		mFontColor = style.getFontColor();
		invalidate();
	}

	protected void doSetState(boolean newState,
		  	 				  int stateBit) {
		mFlags = (newState) ? mFlags | stateBit : mFlags & ~stateBit;
		mDirty = true;
		invalidate();
		if (mVisible)
			repaint();
    }

    public void replyInvite(String action) {
        Dialogs.popupWipDialog(mMidlet, mParentView, Locale.get("appt.SendingReply"));
        mMidlet.mMbox.sendInviteReply(mId, null, null, action, this);
    }
}
