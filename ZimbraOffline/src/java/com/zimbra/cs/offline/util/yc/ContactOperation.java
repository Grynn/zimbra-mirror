package com.zimbra.cs.offline.util.yc;

import com.zimbra.cs.mime.ParsedContact;

public interface ContactOperation extends Comparable<ContactOperation> {

    public String getRemoteId();
    public Action getOp();
    public ParsedContact getParsedContact();
    public Contact getYContact();
    public boolean isPushOperation();
    public void setItemId(int id);
    public int getItemId();
}
