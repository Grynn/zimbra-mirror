/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2011 Zimbra, Inc.
 *
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.3 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 */
package com.zimbra.cs.mailbox;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.google.common.annotations.VisibleForTesting;
import com.zimbra.common.service.ServiceException;
import com.zimbra.common.soap.Element;
import com.zimbra.common.soap.MailConstants;
import com.zimbra.common.util.LruMap;
import com.zimbra.cs.account.DataSource;
import com.zimbra.cs.account.offline.OfflineAccount;
import com.zimbra.cs.account.offline.OfflineProvisioning;
import com.zimbra.cs.datasource.DataSourceDbMapping;
import com.zimbra.cs.datasource.DataSourceMapping;
import com.zimbra.cs.db.DbDataSource.DataSourceItem;
import com.zimbra.cs.mailbox.MailServiceException.NoSuchItemException;
import com.zimbra.cs.offline.OfflineLog;
import com.zimbra.soap.admin.type.DataSourceType;

public class TagSync {

    private static final String dsName = "zcs7tag";
    private boolean mappingRequired = false;
    private DataSource tagDs = null;
    private final ZcsMailbox mbox;

    private LruMap<Integer, Integer> localIdFromRemote;
    private LruMap<Integer, Integer> remoteIdFromLocal;
    private LruMap<String, Integer>  localIdsByName;

    public TagSync(ZcsMailbox mbox) {
        this.mbox = mbox;
        //TODO: will need inverse implementation for ZD 8 to work with ZCS 7; underpinnings are here but need to work out details
        try {
            if (!mbox.getRemoteServerVersion().isAtLeast8xx()) {
                enableTagDataSource(mbox);
            }
        } catch (ServiceException e) {
            OfflineLog.offline.error("Unable to intialize tag datasource due to exception.",e);
        }
    }

    private void enableTagDataSource(ZcsMailbox mbox) throws ServiceException {
        localIdFromRemote = new LruMap<Integer, Integer>(64);
        remoteIdFromLocal = new LruMap<Integer, Integer>(64);
        localIdsByName    = new LruMap<String, Integer>(64);
        mappingRequired = true;
        initTagDataSource(mbox.getOfflineAccount());
    }

    private void initTagDataSource(OfflineAccount account) throws ServiceException {
        OfflineProvisioning prov = OfflineProvisioning.getOfflineInstance();
        tagDs = account.getDataSourceByName(dsName);
        if (tagDs == null) {
            OfflineLog.offline.debug("initializing tag datasource");
            tagDs = prov.createDataSource(account, DataSourceType.tagmap, dsName, new HashMap<String, Object>());
            //initially any previously existing local tags also have same ID as remote.
            List<Tag> tags = mbox.getTagList(null);
            for (Tag tag : tags) {
                mapTag(tag.getId(), tag.getId());
            }
        }
    }

    static int TAG_ID_OFFSET = 64, MAX_TAG_COUNT = 63;

    static boolean validateId(int tagId) {
        return true; //TODO: cleanup 7.x backward compat. preemptive validation doesn't work in reverse; 7.x will always create tag IDs that are valid in 8.0
//        return tagId >= TAG_ID_OFFSET && tagId < TAG_ID_OFFSET + MAX_TAG_COUNT;
    }

    /**
     * Returns comma-separated list of tag ids from element.
     * If the remote server is less than 8.0 this is read directly from 't' attribute
     * If the remote server is 8.0 or greater this is mapped from 'tn' attribute to local ids
     * @param elt
     * @param mbox
     * @throws ServiceException
     */
    public String localTagsFromElement(Element elt, String defaultVal) throws ServiceException {
        String tags = elt.getAttribute(MailConstants.A_TAGS, defaultVal);
        if (mappingRequired) {
            tags = localTagsFromRemote(tags);
        }
        return tags;
    }

    @VisibleForTesting
    String localTagsFromRemote(String remoteTags) throws ServiceException {
        if (remoteTags == null || remoteTags.length() == 0) {
            return null;
        }

        StringBuilder sb = new StringBuilder();
        for (String remoteTag : remoteTags.split(",")) {
            int localId = localTagId(Integer.valueOf(remoteTag));
            if (!validateId(localId)) {
                continue;
            } else {
                sb.append(localId).append(",");
            }
        }
        if (sb.length() > 0) {
            sb.setLength(sb.length() -1);
        }
        String local =  sb.toString();
        OfflineLog.offline.debug("mapped remote tags %s to local %s",remoteTags, local);
        return local;
    }

    /**
     * Extract comma-separated list of local tagIds from remote tags or tagnames
     * @throws ServiceException
     */
    public String localTagsFromHeader(Map<String, String> headers) throws ServiceException {
        if (mappingRequired) {
            String names = headers.get("X-Zimbra-TagNames");
            return localTagsFromNames(names, ",", ",");
        } else {
            return headers.get("X-Zimbra-Tags");
        }
    }

    /**
     * Get the local ID corresponding to remote tagId
     * @throws ServiceException
     */
    public int localTagId(int id) throws ServiceException {
        if (!isMappingRequired(id)) {
            return id;
        }
        try {
            if (localIdFromRemote.containsKey(id)) {
                return localIdFromRemote.get(id);
            }
            DataSourceMapping mapping = new DataSourceMapping(tagDs, id + "");
            OfflineLog.offline.debug("got localId %d from remote %d",mapping.getItemId(), id);
            localIdFromRemote.put(id, mapping.getItemId());
            return mapping.getItemId();
        } catch (ServiceException se) {
            if (MailServiceException.NO_SUCH_ITEM.equals(se.getCode())) {
                return -1;
            }
            throw se;
        }
    }

    /**
     * Translate a delimted list of tag names to a delimited list of correponding local tagIds
     * @throws ServiceException
     */
    public String localTagsFromNames(String tagNames, String inDelim, String outDelim) throws ServiceException {
        if (tagNames != null && tagNames.length() > 0) {
            StringBuilder sb = new StringBuilder();
            String[] names = tagNames.split(inDelim);
            for (String name : names) {
                if (name.trim().length() <= 0) {
                    continue;
                }
                Integer tagId = localIdsByName.get(name);
                if (tagId == null) {
                    try {
                        Tag tag = mbox.getTagByName(null, name);
                        tagId = tag.getId();
                        localIdsByName.put(name, tagId);
                    } catch (MailServiceException mse) {
                        if (MailServiceException.NO_SUCH_TAG.equals(mse.getCode())) {
                            OfflineLog.offline.debug("message has tag ["+name+"] which is not visible locally");
                            continue;
                        } else {
                            throw mse;
                        }
                    }
                }
                sb.append(tagId).append(outDelim);
            }
            if (sb.length() >= outDelim.length()) {
                sb.setLength(sb.length() - outDelim.length());
            }
            return sb.toString();
        } else {
            return tagNames;
        }
    }

    /**
     * Extract local tagId from XML element representing remote tag. Currently reads from A_ID, but could map from name in the future
     * @throws ServiceException
     */
    public int localTagId(Element elt) throws ServiceException {
        return localTagId((int) elt.getAttributeLong(MailConstants.A_ID));
    }

    /**
     * Get the remoteId corresponding to local tagId
     * @throws ServiceException
     */
    public int remoteTagId(int id) throws ServiceException {
        if (mappingRequired) {
            if (remoteIdFromLocal.containsKey(id)) {
                return remoteIdFromLocal.get(id);
            }
            try {
                DataSourceMapping mapping = new DataSourceMapping(tagDs, id);
                int remoteId = Integer.valueOf(mapping.getRemoteId());
                OfflineLog.offline.debug("got remoteId %d from localId %d", remoteId, id);
                remoteIdFromLocal.put(id, remoteId);
                return remoteId;
            } catch (ServiceException se) {
                if (MailServiceException.NO_SUCH_ITEM.equals(se.getCode())) {
                    return -1;
                }
                throw se;
            }
        } else {
            return id;
        }
    }

    /**
     * Get a list of remoteIds corresponding to a list of localIds
     * @throws ServiceException
     */
    public List<Integer> remoteIds(List<Integer> localIds) throws ServiceException {
        //get list of the remote ids for local
        //if a mapping is not present warn and go ahead
        if (mappingRequired && localIds != null) {
            List<Integer> remoteIds = new ArrayList<Integer>();
            for (Integer localId : localIds) {
                int remoteId = remoteTagId(localId);
                if (remoteId > 0) {
                    remoteIds.add(remoteId);
                } else {
                    OfflineLog.offline.warn("no remoteId for local %d",localId);
                }
            }
            return remoteIds;
        } else {
            return localIds;
        }
    }

    @VisibleForTesting
    String remoteAttrFromTags(String tagStr) throws ServiceException {
        if (mappingRequired) {
            StringBuilder sb = new StringBuilder();
            for (String tag : tagStr.split(",")) {
                if (tag.length() > 0) {
                    sb.append(remoteTagId(Integer.valueOf(tag))).append(",");
                }
            }
            if (sb.length() > 0) {
                sb.setLength(sb.length() -1);
            }

            String remote = sb.toString();
            OfflineLog.offline.debug("mapped local tags %s to remote %s",tagStr, remote);
            tagStr = remote;
        }
        return tagStr;
    }

    /**
     * Remove mapping for a given localId
     * @throws ServiceException
     */
    public void removeTagMapping(int tagId) throws ServiceException {
        //delete the mapping if it was needed and exists
        if (mappingRequired) {
            try {
                DataSourceMapping mapping = new DataSourceMapping(tagDs, tagId);
                mapping.delete();
                localIdFromRemote.remove(Integer.valueOf(mapping.getRemoteId()));
                remoteIdFromLocal.remove(tagId);
                if (localIdsByName.containsValue(tagId)) {
                    Set<String> keysToRemove = new HashSet<String>(); //should only be one, but naming conflicts might end up with more..
                    for (Entry<String, Integer> entry : localIdsByName.entrySet()) {
                        if (entry.getValue().equals(tagId)) {
                            keysToRemove.add(entry.getKey());
                        }
                    }
                    for (String key : keysToRemove) {
                        localIdsByName.remove(key);
                    }
                }
                OfflineLog.offline.debug("removed tag mapping for localId %d",tagId);
                //TODO: consider the case where we have > 63 tags.
                //we can move one of the overflow tags into a free slot, but we also have to apply the tag to existing messages
            } catch (ServiceException se) {
                if (!MailServiceException.NO_SUCH_ITEM.equals(se.getCode())) {
                   throw se;
                }
            }
        }
    }

    /**
     * Get the localId corresponding to a given remoteId, or create a new mapping if non exists
     * @throws ServiceException
     */
    public int getOrMapLocalIdFromRemote(int remoteId, int localId) throws ServiceException {
        int existingLocalId = localTagId(remoteId);
        if (existingLocalId > 0) {
            return existingLocalId;
        } else {
            mapTag(remoteId, localId);
            return localId;
        }
    }

    /**
     * Return true if a mapping already exists for a given remoteId
     * @throws ServiceException
     */
    public boolean mappingExists(int remoteId) throws ServiceException {
        if (localIdFromRemote.get(remoteId) != null) {
            return true;
        }
        try {
            DataSourceMapping mapping = new DataSourceMapping(tagDs, remoteId+"");
            localIdFromRemote.put(remoteId, mapping.getItemId());
            return true;
        } catch (NoSuchItemException e) {
            return false;
        }
    }

    /**
     * Create a mapping from remote to local tagId
     * @throws ServiceException
     */
    public void mapTag(int remote, int id) throws ServiceException {
        if (!validateId(id)) {
            throw MailServiceException.NO_SUCH_TAG(id);
        }
        mapTagInternal(remote, id);
    }

    private void mapTagInternal(int remote, int id) throws ServiceException {
        DataSourceMapping mapping = new DataSourceMapping(tagDs, Mailbox.ID_FOLDER_TAGS, id, remote+"");
        mapping.add();
        localIdFromRemote.put(remote, id);
        remoteIdFromLocal.put(id, remote);
        OfflineLog.offline.debug("added mapping from remote %s to local %d",remote,id);
    }

    /**
     * Map a tag in the overflow range (i.e. > 127). These tags will never be visible in ZD but are tracked internally
     * @throws ServiceException
     */
    public void mapOverflowTag(int remote) throws ServiceException {
        int max = TAG_ID_OFFSET;
        Collection<DataSourceItem> items = DataSourceDbMapping.getInstance().getAllMappingsInFolder(tagDs, Mailbox.ID_FOLDER_TAGS);
        for (DataSourceItem item : items) {
            if (item.itemId > max) {
                max = item.itemId;
            }
        }
        int overflowId = max+1;
        mapTagInternal(remote, overflowId);
        localIdFromRemote.put(remote, overflowId);
        remoteIdFromLocal.put(overflowId, remote);
        OfflineLog.offline.debug("added mapping from remote %s to (hidden) local %d",remote,overflowId);
    }

    /**
     * Return true if mapping is required for the associated mailbox. In other words if remote server >= 8.0
     */
    public boolean isMappingRequired() {
        return mappingRequired;
    }

    /**
     * Return true if mapping is required for the associated mailbox. In other words if remote server >= 8.0.
     * Also initializes mapping if the remoteId is outside of the range. This is needed since mailbox sync can be the first component
     * which encounters upgraded server; the directory sync interval is separate from mailbox sync
     * @throws ServiceException
     */
    public boolean isMappingRequired(int remoteId) throws ServiceException {
        if (!mappingRequired && !validateId(remoteId)) {
            OfflineLog.offline.info("Detected new tag ID outside valid range; enabling tag mapping");
            enableTagDataSource(mbox);
        }
        return mappingRequired;
    }

    /**
     * Add the appropriate tags attribute depending on direction of request and remote version
     * @throws ServiceException
     */
    public Element addTagsAttr(Element req, Element resp,
            boolean outbound) throws ServiceException {
        if (outbound) {
            req = addOutboundTagsAttr(req, resp.getAttribute(MailConstants.A_TAGS, ""));
        } else {
            if (mappingRequired) {
                req.addAttribute(MailConstants.A_TAGS, localTagsFromElement(resp, ""));
            } else {
                req.addAttribute(MailConstants.A_TAGS, resp.getAttribute(MailConstants.A_TAGS, ""));
            }
        }
        return req;
    }

    /**
     * Add remote tags attribute corresponding to list of local tags
     * @throws ServiceException
     */
    public Element addOutboundTagsAttr(Element req, String tagStr) throws ServiceException {
        if (mappingRequired) {
            req.addAttribute(MailConstants.A_TAGS, remoteAttrFromTags(tagStr));
        } else {
            req.addAttribute(MailConstants.A_TAGS, tagStr);
        }
        return req;
    }
}
