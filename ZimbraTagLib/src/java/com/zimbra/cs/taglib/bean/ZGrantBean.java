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
