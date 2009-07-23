/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2007, 2008 Zimbra, Inc.
 * 
 * The contents of this file are subject to the Yahoo! Public License
 * Version 1.0 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * 
 * ***** END LICENSE BLOCK *****
 */
package com.zimbra.cs.offline;

import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.db.DbPool;
import com.zimbra.cs.store.file.Volume;
import com.zimbra.cs.util.ZimbraApplication;

public class OfflineApplication extends ZimbraApplication {

    @Override
    public String getId() {
        return OfflineLC.zdesktop_app_id.value();
    }

    @Override
    public String getInstallId() {
        return OfflineLC.zdesktop_installation_key.value();
    }

    @Override
    public boolean supports(String className) {
        return false;
    }

    @Override
    public void initialize(boolean forMailboxd) {
        long threshold = OfflineLC.zdesktop_volume_compression_threshold.longValue();
        Volume vol = Volume.getCurrentMessageVolume();
        
        try {
            Volume.update(vol.getId(), vol.getType(), vol.getName(),
                vol.getRootPath(), vol.getMboxGroupBits(), vol.getMboxBits(),
                vol.getFileGroupBits(), vol.getFileBits(), threshold != 0,
                threshold == 0 ? vol.getCompressionThreshold() : threshold, true);
            Volume.reloadVolumes();
        } catch (ServiceException e) {
            OfflineLog.offline.warn("Unable to update volume compression", e);
        }
    }

    @Override
    public void shutdown() {
        super.shutdown();
        try {
            DbPool.shutdown();
        } catch (Exception x) {
            OfflineLog.offline.warn("Exception during shutdown", x);
        }
    }
}
