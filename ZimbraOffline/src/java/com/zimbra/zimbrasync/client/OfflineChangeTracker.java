package com.zimbra.zimbrasync.client;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.util.Pair;
import com.zimbra.common.util.ZimbraLog;
import com.zimbra.cs.account.DataSource;
import com.zimbra.cs.mailbox.ChangeTrackingMailbox;
import com.zimbra.cs.mailbox.MailItem;
import com.zimbra.cs.session.PendingModifications.Change;
import com.zimbra.zimbrasync.client.ChangeTracker;
import com.zimbra.zimbrasync.client.ExchangeItemMapping;

public class OfflineChangeTracker extends ChangeTracker {

    public OfflineChangeTracker(DataSource ds, Map<Integer, ExchangeFolderMapping> folderMappingsByClientId) throws ServiceException {
        super(ds, folderMappingsByClientId);
    }
    
    @Override
    protected void findClientChanges() throws ServiceException {
        ChangeTrackingMailbox ctmbox = (ChangeTrackingMailbox)mbox;
        Map<Integer, Pair<Integer, Integer>> changes = ctmbox.getChangeMasksAndFolders(getContext(false));
        addItemMappings(ExchangeItemMapping.getMappings(ds, changes.keySet()));
        for (Iterator<Entry<Integer, Pair<Integer, Integer>>> i = changes.entrySet().iterator(); i.hasNext();) {
            Entry<Integer, Pair<Integer, Integer>> entry = i.next();
            int id = entry.getKey();
            int mask = entry.getValue().getFirst();
            int folderId = entry.getValue().getSecond();
            
            if ((mask & Change.MODIFIED_CONFLICT) != 0) {
                getClientAdds(folderId).add(id);
            } else {
                if ((mask & Change.MODIFIED_FOLDER) != 0) {
                    int oldFldId = mappingByClientId.get(id).getFolderId();
                    if (oldFldId != folderId) {
                        ExchangeItemMapping eim = mappingByClientId.get(id);
                        assert eim != null; //this is not a new item
                        String remoteId = eim.getRemoteId();
                        String remoteSrcFldId = eim.getRemoteParentId();
                        String remoteDstFldId = null;
                        ExchangeFolderMapping efm = folderMappingsByClientId.get(folderId);
                        if (efm != null) {
                            remoteDstFldId = efm.getRemoteId();
                        } else { //the dst folder is new and hasn't been pushed up to server yet
                            ZimbraLog.xsync.debug("item=%d moved to new folder (id=%d) that hasn't been pushed to server", id, folderId);
                        }
                        addItemMove(new TrackerItemMove(remoteId, remoteSrcFldId, remoteDstFldId, id, oldFldId, folderId));
                    } else { //somehow item was moved but maybe moved back to the previous folder
                        clearItemMoved(id, folderId); //just clear the bit if item is still in there
                    }
                }
                if (((mask & Change.MODIFIED_CONTENT) | (mask & Change.MODIFIED_METADATA) | (mask & Change.MODIFIED_UNREAD)) != 0)
                    getClientChanges(folderId).add(id);
            }
        }
        
        List<Integer> tombstones = ctmbox.getTombstones(0).getAll();
        addItemMappings(ExchangeItemMapping.getMappings(ds, tombstones));
        for (int id : tombstones) {
            ExchangeItemMapping eim = mappingByClientId.get(id);
            if (eim != null)
                getClientDeletes(eim.getFolderId()).add(id);
        }
    }
    
    @Override
    protected void clearItemMoved(int id, int folderId) throws ServiceException {
        ChangeTrackingMailbox ctmbox = (ChangeTrackingMailbox)mbox;
        synchronized (ctmbox) {
            MailItem item = ctmbox.getItemById(getContext(false), id, MailItem.TYPE_UNKNOWN);
            if (folderId == item.getFolderId()) {
                int mask = ctmbox.getChangeMask(getContext(false), id, item.getType());
                mask &= ~Change.MODIFIED_FOLDER;
                ctmbox.setChangeMask(getContext(false), id, item.getType(), mask);
            }
        }
    }
    
    @Override
    protected void clearItemAdded(int id, int changeId) throws ServiceException {
        ChangeTrackingMailbox ctmbox = (ChangeTrackingMailbox)mbox;
        synchronized (ctmbox) {
            int mask = 0;
            MailItem item = ctmbox.getItemById(getContext(false), id, MailItem.TYPE_UNKNOWN);
            if (item.getModifiedSequence() != changeId) {
                mask = ctmbox.getChangeMask(getContext(false), id, item.getType());
                mask &= ~Change.MODIFIED_CONFLICT;
            }
            ctmbox.setChangeMask(getContext(false), id, item.getType(), mask);
        }
    }
    
    @Override
    protected void clearItemChanged(int id, int changeId) throws ServiceException {
        ChangeTrackingMailbox ctmbox = (ChangeTrackingMailbox)mbox;
        synchronized (ctmbox) {
            MailItem item = ctmbox.getItemById(getContext(false), id, MailItem.TYPE_UNKNOWN);
            if (item.getModifiedSequence() == changeId) {
                ctmbox.setChangeMask(getContext(false), id, item.getType(), 0); //TODO: well we may have to leave some bits for future implementaiton, like if we don't sync flags initially
            }
        }
    }
    
    @Override
    protected void clearItemDeleted(int id) throws ServiceException {
        //if we get one positive delete response, clear all tombstones as a shortcut as we don't plan to send deletes in multiple batches
        if (!tombstonesCleared) {
            ChangeTrackingMailbox ctmbox = (ChangeTrackingMailbox)mbox;
            ctmbox.clearTombstones(getContext(false), cutoffChangeId); //TODO: maybe need to skip temporary failures?
            tombstonesCleared = true;
        }
    }
}
