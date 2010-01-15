/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2007, 2009, 2010 Zimbra, Inc.
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
package com.zimbra.cs.taglib.bean;

import com.zimbra.cs.zclient.ZGrant;

/**
 * Created by IntelliJ IDEA.
 * User: akanjila
 * Date: Nov 20, 2007
 * Time: 2:40:03 PM
 */
public class ZGrantBean {

    private String mArgs;
    private String mGranteeName;
    private String mGranteeId;
    private ZGrant.GranteeType mGranteeType;
    private String mPermissions;

    public String getArgs() {
        return mArgs;
    }

    public void setArgs(String mArgs) {
        this.mArgs = mArgs;
    }

    public String getGranteeName() {
        return mGranteeName;
    }

    public void setGranteeName(String mGranteeName) {
        this.mGranteeName = mGranteeName;
    }

    public String getGranteeId() {
        return mGranteeId;
    }

    public void setGranteeId(String mGranteeId) {
        this.mGranteeId = mGranteeId;
    }

    public ZGrant.GranteeType getGranteeType() {
        return mGranteeType;
    }

    public void setGranteeType(ZGrant.GranteeType mGranteeType) {
        this.mGranteeType = mGranteeType;
    }

    public String getPermissions() {
        return mPermissions;
    }

    public void setPermissions(String mPermissions) {
        this.mPermissions = mPermissions;
    }
}
