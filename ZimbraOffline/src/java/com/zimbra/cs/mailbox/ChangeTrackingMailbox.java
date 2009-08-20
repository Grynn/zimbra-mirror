package com.zimbra.cs.mailbox;

import java.util.List;
import java.util.Map;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.util.Pair;
import com.zimbra.cs.db.DbOfflineMailbox;
import com.zimbra.cs.mailbox.MailServiceException.NoSuchItemException;
import com.zimbra.cs.mailbox.ZcsMailbox.OfflineContext;
import com.zimbra.cs.mailbox.util.TypedIdList;

public abstract class ChangeTrackingMailbox extends SyncMailbox {

	public ChangeTrackingMailbox(MailboxData data) throws ServiceException {
		super(data);
	}
	
	
    @Override boolean isTrackingSync() {
        return !(getOperationContext() instanceof OfflineContext);
    }

    @Override public boolean isTrackingImap() {
        return false;
    }

    @Override public boolean checkItemChangeID(int modMetadata, int modContent) {
        return true;
    }
    
    @Override void itemCreated(MailItem item, boolean inArchive) throws ServiceException {};
    
    synchronized boolean isPendingDelete(OperationContext octxt, int itemId, byte type) throws ServiceException {
        boolean success = false;
        try {
            beginTransaction("isPendingDelete", octxt);

            boolean result = DbOfflineMailbox.isTombstone(this, itemId, type);
            success = true;
            return result;
        } finally {
            endTransaction(success);
        }
    }

    synchronized void removePendingDelete(OperationContext octxt, int itemId, byte type) throws ServiceException {
        boolean success = false;
        try {
            beginTransaction("removePendingDelete", octxt);

            DbOfflineMailbox.removeTombstone(this, itemId, type);
            success = true;
        } finally {
            endTransaction(success);
        }
    }

    synchronized TypedIdList getLocalChanges(OperationContext octxt) throws ServiceException {
        boolean success = false;
        try {
            beginTransaction("getLocalChanges", octxt);

            TypedIdList result = DbOfflineMailbox.getChangedItems(this);
            success = true;
            return result;
        } finally {
            endTransaction(success);
        }
    }
    
    public synchronized Map<Integer, Pair<Integer, Integer>> getChangeMasksAndFolders(OperationContext octxt) throws ServiceException {
        boolean success = false;
        try {
            beginTransaction("getChangeMasksAndFolders", octxt);

            Map<Integer, Pair<Integer, Integer>> result = DbOfflineMailbox.getChangeMasksAndFolders(this);
            success = true;
            return result;
        } finally {
            endTransaction(success);
        }
    }
    
    synchronized Map<Integer, Pair<Integer, Integer>> getChangeMasksAndFlags(OperationContext octxt) throws ServiceException {
        boolean success = false;
        try {
            beginTransaction("getChangeMasksAndFlags", octxt);

            Map<Integer, Pair<Integer, Integer>> result = DbOfflineMailbox.getChangeMasksAndFlags(this);
            success = true;
            return result;
        } finally {
            endTransaction(success);
        }
    }
    
    synchronized List<Pair<Integer, Integer>> getSimpleUnreadChanges(OperationContext octxt, boolean isUnread) throws ServiceException {
        boolean success = false;
        try {
            beginTransaction("getSimpleUnreadChanges", octxt);

            List<Pair<Integer, Integer>> result = DbOfflineMailbox.getSimpleUnreadChanges(this, isUnread);
            success = true;
            return result;
        } finally {
            endTransaction(success);
        }
    }
    
    synchronized Map<Integer, List<Pair<Integer, Integer>>> getFolderMoveChanges(OperationContext octxt) throws ServiceException {
        boolean success = false;
        try {
            beginTransaction("getFolderMoveChanges", octxt);

            Map<Integer, List<Pair<Integer, Integer>>> result = DbOfflineMailbox.getFolderMoveChanges(this);
            success = true;
            return result;
        } finally {
            endTransaction(success);
        }
    }
    
    synchronized Map<Integer, Integer> getItemModSequences(OperationContext octxt, int[] ids) throws ServiceException {
        boolean success = false;
        try {
            beginTransaction("getItemModSequences", octxt);

            Map<Integer, Integer> result = DbOfflineMailbox.getItemModSequences(this, ids);
            success = true;
            return result;
        } finally {
            endTransaction(success);
        }
    }
    
    synchronized Map<Integer, Integer> getItemFolderIds(OperationContext octxt, int[] ids) throws ServiceException {
        boolean success = false;
        try {
            beginTransaction("getItemFolderIds", octxt);

            Map<Integer, Integer> result = DbOfflineMailbox.getItemFolderIds(this, ids);
            success = true;
            return result;
        } finally {
            endTransaction(success);
        }
    }

    public synchronized int getChangeMask(OperationContext octxt, int id, byte type) throws ServiceException {
        boolean success = false;
        try {
            beginTransaction("getChangeMask", octxt);

            MailItem item = getItemById(id, type);
            int mask = DbOfflineMailbox.getChangeMask(item);
            success = true;
            return mask;
        } catch (NoSuchItemException nsie) {
            return 0;
        } finally {
            endTransaction(success);
        }
    }

    public synchronized void setChangeMask(OperationContext octxt, int id, byte type, int mask) throws ServiceException {
        //TODO: make this call always non-tracking
        boolean success = false;
        try {
            beginTransaction("setChangeMask", octxt);

            MailItem item = getItemById(id, type);
            DbOfflineMailbox.setChangeMask(item, mask);
            success = true;
        } finally {
            endTransaction(success);
        }
    }

    public synchronized void clearTombstones(OperationContext octxt, int token) throws ServiceException {
        boolean success = false;
        try {
            beginTransaction("clearTombstones", octxt);
            DbOfflineMailbox.clearTombstones(this, token);
            success = true;
        } finally {
            endTransaction(success);
        }
    }
}
