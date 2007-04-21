/*
 * ***** BEGIN LICENSE BLOCK *****
 * Version: MPL 1.1
 * 
 * The contents of this file are subject to the Mozilla Public License
 * Version 1.1 ("License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.zimbra.com/license
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See
 * the License for the specific language governing rights and limitations
 * under the License.
 * 
 * The Original Code is: Zimbra Collaboration Suite Server.
 * 
 * The Initial Developer of the Original Code is Zimbra, Inc.
 * Portions created by Zimbra are Copyright (C) 2006, 2007 Zimbra, Inc.
 * All Rights Reserved.
 * 
 * Contributor(s): 
 * 
 * ***** END LICENSE BLOCK *****
 */
package com.zimbra.cs.account.offline;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.zimbra.cs.account.Account;
import com.zimbra.cs.account.Provisioning;
import com.zimbra.cs.offline.OfflineLog;

public class OfflineAccount extends Account {
	
	public class Version {
		
		private String versionStr;
		private int major;
		private int minor;
		private int maintenance;
		
		Version(String versionStr) {
			this.versionStr = versionStr;
			String[] digits = versionStr.split("\\.");
			try {
				if (digits.length > 0) {
					major = Integer.parseInt(digits[0]);
				}
				if (digits.length > 1) {
					minor = Integer.parseInt(digits[1]);
				}
				if (digits.length > 2) {
					maintenance = Integer.parseInt(digits[2]);
				}
			} catch (NumberFormatException x) {
				OfflineLog.offline.warn("unknown remote server version: " + versionStr);
			}
		}
		
		public int getMajor() {
			return major;
		}
		
		public int getMinor() {
			return minor;
		}
		
		public int getMaintenance() {
			return maintenance;
		}
		
		public String toString() {
			return versionStr;
		}
	}
	
	private Version mRemoteServerVersion;
	
	public Version getRemoteServerVersion() {
		if (mRemoteServerVersion == null) {
			mRemoteServerVersion = new Version(getAttr(OfflineProvisioning.A_offlineRemoteServerVersion));
		}
		return mRemoteServerVersion;
	}
	
	public void resetRemoteServerVersion() {
		mRemoteServerVersion = null;
	}
	
    public OfflineAccount(String name, String id, Map<String, Object> attrs, Map<String, Object> defaults) {
        super(name, id, attrs, defaults);
    }

    private static final String[] sDisabledFeatures = new String[] {
        Provisioning.A_zimbraFeatureCalendarEnabled,
        Provisioning.A_zimbraFeatureTasksEnabled,
        Provisioning.A_zimbraFeatureNotebookEnabled,
        Provisioning.A_zimbraFeatureIMEnabled,
        Provisioning.A_zimbraFeatureSharingEnabled,
        Provisioning.A_zimbraFeatureGalEnabled,
        Provisioning.A_zimbraFeatureGalAutoCompleteEnabled,
        Provisioning.A_zimbraFeatureViewInHtmlEnabled
    };

    private static final Set<String> sDisabledFeaturesSet = new HashSet<String>();
        static {
            for (String feature : sDisabledFeatures)
                sDisabledFeaturesSet.add(feature.toLowerCase());
        }

    @Override
    public String getAttr(String name, boolean applyDefaults) {
        // disable certain features here rather than trying to make the cached values and the remote values differ
        if (sDisabledFeaturesSet.contains(name.toLowerCase()))
            return "FALSE";
        return super.getAttr(name, applyDefaults);
    }

    @Override
    protected Map<String, Object> getRawAttrs() {
        Map<String, Object> attrs = new HashMap<String, Object>(super.getRawAttrs());
        for (String feature : sDisabledFeatures)
            attrs.put(feature, "FALSE");
        return attrs;
    }

    @Override
    public Map<String, Object> getAttrs(boolean applyDefaults) {
        Map<String, Object> attrs = new HashMap<String, Object>(super.getAttrs(applyDefaults));
        for (String feature : sDisabledFeatures)
            attrs.put(feature, "FALSE");
        return attrs;
    }
}
