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
package org.jivesoftware.wildfire.stats;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Stores statistics being tracked by the server.
 */
public class StatisticsManager {
    private static StatisticsManager instance = new StatisticsManager();

    public static StatisticsManager getInstance() {
        return instance;
    }

    private final Map<String, Statistic> statistics = new ConcurrentHashMap<String, Statistic>();

    private final Map<String, List<String>> multiStatGroups = new ConcurrentHashMap<String, List<String>>();

    private final Map<String, String> keyToGroupMap = new ConcurrentHashMap<String, String>();

    private StatisticsManager() {}

    /**
     * Adds a stat to be tracked to the StatManager.
     *
     * @param definition The statistic to be tracked.
     */
    public void addStatistic(String statKey, Statistic definition) {
        statistics.put(statKey, definition);
    }

    /**
     * Returns a statistic being tracked by the StatManager.
     *
     * @param statKey The key of the definition.
     * @return Returns the related stat.
     */
    public Statistic getStatistic(String statKey) {
        return statistics.get(statKey);
    }

    public void addMultiStatistic(String statKey, String groupName, Statistic statistic) {
        addStatistic(statKey, statistic);
        List<String> group = multiStatGroups.get(groupName);
        if(group == null) {
            group = new ArrayList<String>();
            multiStatGroups.put(groupName, group);
        }
        group.add(statKey);
        keyToGroupMap.put(statKey, groupName);
    }

    public List<String> getStatGroup(String statGroup) {
        return multiStatGroups.get(statGroup);
    }

    public String getMultistatGroup(String statKey) {
        return keyToGroupMap.get(statKey);
    }

    /**
     * Returns all statistics that the StatManager is tracking.
     * @return Returns all statistics that the StatManager is tracking.
     */
    public Set<Map.Entry<String, Statistic>> getAllStatistics() {
        return statistics.entrySet();
    }

    /**
     * Removes a statistic from the server.
     *
     * @param statKey The key of the stat to be removed.
     */
    public void removeStatistic(String statKey) {
        statistics.remove(statKey);
    }

}
