/*
 * Created on Oct 15, 2004
 */
package com.liquidsys.coco.index;

import java.util.ArrayList;

import org.apache.lucene.document.DateField;
import org.apache.lucene.document.Document;

import com.liquidsys.coco.mailbox.Mailbox;
import com.liquidsys.coco.mailbox.MailItem;
import com.liquidsys.coco.mailbox.Message;
import com.liquidsys.coco.mailbox.Tag;
import com.liquidsys.coco.mime.ParsedAddress;
import com.liquidsys.coco.service.ServiceException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;



/**
 * @author tim
 * 
 * Efficient Read-access to a Message returned from a query. APIs mirror the
 * APIs on com.liquidsys.coco.mailbox.Message, but are read-only. The real
 * archive.mailbox.Message can be retrieved, but this should only be done if
 * write-access is necessary.
 */
public class MessageHit extends LiquidHit {

    private static Log mLog = LogFactory.getLog(MessageHit.class);
    
    private Document mDoc = null;

    private Message mMessage = null;

    private ArrayList mMatchedParts = null;

    private int mConversationId = 0;

    private int mMessageId = 0;

    private ConversationHit mConversationHit = null;

    protected MessageHit(LiquidQueryResultsImpl results, Mailbox mbx, Document d, float score) {
        super(results, mbx, score);
        mDoc = d;
        assert (d != null);
    }

    protected MessageHit(LiquidQueryResultsImpl results, Mailbox mbx, int id, float score) {
        super(results, mbx, score);
        mMessageId = id;
        assert (id != 0);
    }
    
    /* (non-Javadoc)
     * @see com.liquidsys.coco.index.LiquidHit#inTrashOrSpam()
     */
    boolean inMailbox() throws ServiceException {
        return getMessage().inMailbox();
    }
    boolean inTrash() throws ServiceException {
        return getMessage().inTrash();
    }
    boolean inSpam() throws ServiceException {
        return getMessage().inSpam();
    }
    
    
    int getFolderId() throws ServiceException {
        return getMessage().getFolderId();
    }

    public int getConversationId() throws ServiceException {
        if (mConversationId == 0) {
            mConversationId = getMessage().getConversationId();
        }
        return mConversationId;
    }

    public long getDate() throws ServiceException {
        if (mCachedDate == -1) {
            if (mMessage == null && mDoc != null) {
                String dateStr = mDoc.get(LuceneFields.L_DATE);
                if (dateStr != null) {
                    mCachedDate = DateField.stringToTime(dateStr);
                    
                    // Tim: 5/11/2005 - now that the DB has been changed to store dates in msec
                    // precision, we do NOT want to manually truncate the date here...
                       // fix for Bug 311 -- SQL truncates dates when it stores them
                       //mCachedDate = (mCachedDate /1000) * 1000;
                    return mCachedDate;
                }
            }
            mCachedDate = getMessage().getDate();
        }
        return mCachedDate;
    }

    public void addPart(MessagePartHit part) {
        if (mMatchedParts == null)
            mMatchedParts = new ArrayList();
        
        if (!mMatchedParts.contains(part)) {
            mMatchedParts.add(part);
        }
    }

    public ArrayList getMatchedMimePartNames() {
        return mMatchedParts;
    }

    public int getItemId() {
        if (mMessageId != 0) {
            return mMessageId;
        }
        String mbid = mDoc.get(LuceneFields.L_MAILBOX_BLOB_ID);
        try {
            if (mbid != null) {
                mMessageId = Integer.parseInt(mbid);
            }
            return mMessageId;
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return 0;
        }
    }
    
    public byte getItemType() throws ServiceException {
        return MailItem.TYPE_MESSAGE;
    }
    

    public String toString() {
        int convId = 0;
        try {
            convId = getConversationId();
        } catch (ServiceException e) {
            e.printStackTrace();
        }
        int size = 0;
        try {
            size = getSize();
        } catch (ServiceException e) {
            e.printStackTrace();
        }
        return "MS: " + super.toString() + " C" + convId + " M" + Integer.toString(getItemId()) + " S="+size;
    }

    public int getSize() throws ServiceException {
//        if (mDoc != null) {
        if (false) {
            String sizeStr = mDoc.get(LuceneFields.L_SIZE);
            return (int) LiquidAnalyzer.SizeTokenFilter.DecodeSize(sizeStr);
        } else {
            return (int) getMessage().getSize();
        }
    }

    public boolean isTagged(Tag tag) throws ServiceException {
        return getMessage().isTagged(tag);
    }
    
    void setItem(MailItem item) {
        mMessage = (Message)item;
    }
    
    boolean itemIsLoaded() {
        return mMessage != null;
    }
    
    public Message getMessage() throws ServiceException {
        if (mMessage == null) {
            Mailbox mbox = Mailbox.getMailboxById(getMailbox().getId());
            int messageId = getItemId();
            try {
                mMessage = mbox.getMessageById(messageId);
            } catch (ServiceException e) {
                mLog.error("Error getting message id="+messageId+" from mailbox "+mbox.getId(),e);
                e.printStackTrace();
                throw e;
            }
        }
        return mMessage;
    }

    public String getSubject() throws ServiceException {
        if (mCachedSubj == null) {
            if (mConversationHit != null) { 
                mCachedSubj = getConversationResult().getSubject();
            } else {
                if (mDoc != null) {
                    mCachedSubj = mDoc.get(LuceneFields.L_SORT_SUBJECT);
                } else {
                    mCachedSubj = getMessage().getSubject();
                }
            }
        }
        return mCachedSubj;
    }
    
    public String getName() throws ServiceException {
        if (mCachedName == null) {
            mCachedName = getSender();
        }
        return mCachedName;
    }

    public long getDateHeader() throws ServiceException {
        if (mMessage == null && mDoc != null) {
            String dateStr = mDoc.get(LuceneFields.L_DATE);
            //                mLog.info(toString() + " " + dateStr);
            if (dateStr != null) {
                return DateField.stringToTime(dateStr);
            } else {
                return 0;
            }
        }
        //            mLog.info(toString() + " Called getMessage().getDate()");
        return getMessage().getDate();
    }

    public String getSender() throws ServiceException {
        //		    if (mMessage == null && mDoc != null) {
        //            if (false) {
        //		        return mDoc.get(LuceneFields.L_H_FROM);
        //		    }
        ParsedAddress cn = new ParsedAddress(getMessage().getSender());
        return cn.getSortString();
    }

    ////////////////////////////////////////////////////
    //
    // Hierarchy access:
    //

    /**
     * @return a ConversationResult corresponding to this message's
     *         conversation
     * @throws ServiceException
     */
    public ConversationHit getConversationResult() throws ServiceException {
        if (mConversationHit == null) {
            Integer cid = new Integer(getConversationId());
            mConversationHit = getResults().getConversationHit(getMailbox(), cid, getScore());
            mConversationHit.addMessageHit(this);
        }
        return mConversationHit;
    }
}