package com.zimbra.cs.offline.util.yc;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.zimbra.cs.mime.ParsedContact;

public class ContactRemove implements ContactOperation {

    private int itemId;
    private String remoteId;

    public ContactRemove(String contactId) {
        this.remoteId = contactId;
    }

    @Override
    public String getRemoteId() {
        return this.remoteId;
    }

    @Override
    public Action getOp() {
        return Action.REMOVE;
    }

    @Override
    public ParsedContact getParsedContact() {
        throw new UnsupportedOperationException("remove contact operation doesnt need parse contact");
    }

    @Override
    public boolean isPushOperation() {
        throw new UnsupportedOperationException("remove contact operation is push neutral");
    }

    @Override
    public Contact getYContact() {
        throw new UnsupportedOperationException("remove contact operation doesnt need ycontact");
    }

    @Override
    public int compareTo(ContactOperation contactOp) {
        return this.itemId - contactOp.getItemId();
    }

    @Override
    public String toString() {
        ToStringHelper helper = Objects.toStringHelper(this);
        return helper.add("remoteId", this.remoteId).add("itemId", this.itemId).add("Op", this.getOp().name())
                .toString();
    }

    @Override
    public void setItemId(int id) {
        this.itemId = id;
    }

    @Override
    public int getItemId() {
        return this.itemId;
    }
}
