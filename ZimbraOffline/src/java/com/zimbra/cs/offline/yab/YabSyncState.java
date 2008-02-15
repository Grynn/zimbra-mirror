/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Zimbra Collaboration Suite, Network Edition.
 * Copyright (C) 2006, 2007 Zimbra, Inc.  All Rights Reserved.
 * 
 * ***** END LICENSE BLOCK *****
 */
package com.zimbra.cs.offline.yab;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.zimbra.cs.mailbox.Mailbox;
import com.zimbra.cs.mailbox.Metadata;
import com.zimbra.cs.mailbox.OfflineServiceException;
import com.zimbra.cs.offline.OfflineLog;
import com.zimbra.common.service.ServiceException;

public class YabSyncState {

	
	private static final String CONFIG_KEY_YAB = "YAB";

	private static final String YAB_REV = "REV";
	private static final String MOD_SEQ = "SEQ";
	
	private static final String CID_MAP = "CID";
	private static final String CATID_MAP = "CATID";
	
	private Mailbox mbox;
	
	private long yabRev;
	private long modSeq;
	
	
	private Map<Long, Long> localRemoteCidMap = new HashMap<Long, Long>();
	private Map<Long, Long> remoteLocalCidMap = new HashMap<Long, Long>();
	private Map<Long, Long> localRemoteCatidMap = new HashMap<Long, Long>();
	private Map<Long, Long> remoteLocalCatidMap = new HashMap<Long, Long>();
	
	private boolean isDirty; //whether we need to save metadata
	
	public YabSyncState(Mailbox mbox) throws ServiceException {
		this.mbox = mbox;
		
		try {
			loadMetadata();
		} catch (Exception x) {
			OfflineLog.offline.warn("Failed loading sync state; will reset", x);
			reset();
			throw OfflineServiceException.OUT_OF_SYNC();
		}
	}
	
	private static void loadLocalRemoteMapping(Metadata meta, Map<Long, Long> map, Map<Long, Long> reverseMap) {
		Set<Map.Entry<String, Long>> entries = meta.asMap().entrySet();
		for (Map.Entry<String, Long> entry : entries) {
			long key = Long.valueOf(entry.getKey());
			long value = entry.getValue();
			map.put(key, value);
			reverseMap.put(value, key);
		}
	}
	
	private void loadMetadata() throws ServiceException {
		Metadata cData = mbox.getConfig(new Mailbox.OperationContext(mbox), CONFIG_KEY_YAB);
		cData = cData != null ? cData : new Metadata();

		yabRev = cData.getLong(YAB_REV, 0);
		modSeq = cData.getLong(MOD_SEQ, 0);
		
		Metadata meta = cData.getMap(CID_MAP, true);
		if (meta != null)
			loadLocalRemoteMapping(meta, localRemoteCidMap, remoteLocalCidMap);
		assert (localRemoteCidMap.size() == remoteLocalCidMap.size());
		meta = cData.getMap(CATID_MAP, true);
		if (meta != null)
			loadLocalRemoteMapping(meta, localRemoteCatidMap, remoteLocalCatidMap);
		assert (localRemoteCatidMap.size() == remoteLocalCatidMap.size());
	}
	
	private void saveMetadata() throws ServiceException {
		Metadata cData = new Metadata();
		cData.put(YAB_REV, Long.valueOf(yabRev));
		cData.put(MOD_SEQ, Long.valueOf(modSeq));
		
		assert (localRemoteCidMap.size() == remoteLocalCidMap.size());
		if (localRemoteCatidMap.size() > 0)
			cData.put(CID_MAP, new Metadata(localRemoteCidMap));
		assert (localRemoteCatidMap.size() == remoteLocalCatidMap.size());
		if (localRemoteCatidMap.size() > 0)
			cData.put(CATID_MAP, new Metadata(localRemoteCatidMap));

		mbox.setConfig(new Mailbox.OperationContext(mbox), CONFIG_KEY_YAB, cData);
		isDirty = false;
	}
	
	public void reset() throws ServiceException {
		yabRev = 0;
		modSeq = 0;
		localRemoteCidMap = new HashMap<Long, Long>();
		remoteLocalCidMap = new HashMap<Long, Long>();
		localRemoteCatidMap = new HashMap<Long, Long>();
		remoteLocalCatidMap = new HashMap<Long, Long>();
		mbox.setConfig(new Mailbox.OperationContext(mbox), CONFIG_KEY_YAB, null);
		isDirty = false;
	}
	
	public void checkResponseRevision(int yabRev) throws ServiceException {
		if (this.yabRev > yabRev) {
			reset();
			OfflineLog.offline.warn("out of sync with yab; must reset; myrev=" + this.yabRev + "; theirs=" + yabRev);
			throw OfflineServiceException.OUT_OF_SYNC();
		}
	}
	
	public int getYabRevision() {
		return (int)yabRev;
	}
	
	public int getModSequence() {
		return (int)modSeq;
	}
	
	public int getContactIdByYabCid(int yabCid) {
		Long contactId = remoteLocalCidMap.get((long)yabCid);
		if (contactId != null)
			return contactId.intValue();
		return -1;
	}
	
	public int getYabCidByContactId(int contactId) {
		Long yabCid = localRemoteCidMap.get((long)contactId);
		if (yabCid != null)
			return yabCid.intValue();
		return -1;
	}
	
	public int getCategoryIdByYabCatid(int yabCatid) {
		Long categoryId = remoteLocalCatidMap.get((long)yabCatid);
		if (categoryId != null)
			return categoryId.intValue();
		return -1;
	}
	
	public int getYabCatidByCategoryId(int categoryId) {
		Long yabCatid = localRemoteCatidMap.get((long)categoryId);
		if (yabCatid != null)
			return yabCatid.intValue();
		return -1;
	}
	
	public void addContact(int contactId, int yabCid) {
		localRemoteCidMap.put((long)contactId, (long)yabCid);
		remoteLocalCidMap.put((long)yabCid, (long)contactId);
	}
	
	public void addCategory(int categoryId, int yabCatid) {
		localRemoteCatidMap.put((long)categoryId, (long)yabCatid);
		remoteLocalCatidMap.put((long)yabCatid, (long)categoryId);
	}
	
	public void updateSyncState(int yabRev, int modSeq) throws ServiceException {
		if (this.yabRev != yabRev) {
			this.yabRev = yabRev;
			isDirty = true;
		}
		this.modSeq = modSeq;
	}
	
	public void commit() throws ServiceException {
		OfflineLog.offline.debug("SyncState: state" + (isDirty ? "saved" : "unchanged") + "; yabrev=" + yabRev + "; modseq=" + modSeq);
		if (isDirty)
			saveMetadata();
	}
}