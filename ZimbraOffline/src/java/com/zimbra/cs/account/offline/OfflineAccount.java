/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Portions created by Zimbra are Copyright (C) 2006 Zimbra, Inc.
 * All Rights Reserved.
 * 
 * The Original Code is: Zimbra Network
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

public class OfflineAccount extends Account {
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
