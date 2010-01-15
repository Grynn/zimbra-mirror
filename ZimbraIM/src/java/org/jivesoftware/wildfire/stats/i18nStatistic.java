/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2006, 2007, 2009, 2010 Zimbra, Inc.
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
package org.jivesoftware.wildfire.stats;

import org.jivesoftware.util.LocaleUtils;

/**
 *  A convience class to build statistic parameters out of a resource bundle.
 *
 * @author Alexander Wenckus
 */
public abstract class i18nStatistic implements Statistic {
    private String resourceKey;
    private String pluginName;
    private Type statisticType;

    public i18nStatistic(String resourceKey, Statistic.Type statisticType) {
        this(resourceKey, null, statisticType);
    }

    public i18nStatistic(String resourceKey, String pluginName, Statistic.Type statisticType) {
        this.resourceKey = resourceKey;
        this.pluginName = pluginName;
        this.statisticType = statisticType;
    }

    public final String getName() {
        return retrieveValue("name");
    }

    public final Type getStatType() {
        return statisticType;
    }

    public final String getDescription() {
        return retrieveValue("desc");
    }

    public final String getUnits() {
        return retrieveValue("units");
    }

    private String retrieveValue(String key) {
        String wholeKey = "stat." + resourceKey + "." + key;
        if(pluginName != null) {
            return LocaleUtils.getLocalizedString(wholeKey, pluginName);
        }
        else {
            return LocaleUtils.getLocalizedString(wholeKey);
        }
    }
}
