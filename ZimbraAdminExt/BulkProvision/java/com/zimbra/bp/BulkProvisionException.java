package com.zimbra.bp;

import com.zimbra.common.service.ServiceException;

/**
 * Created by IntelliJ IDEA.
 * User: ccao
 * Date: Oct 8, 2008
 * Time: 4:03:04 PM
 * To change this template use File | Settings | File Templates.
 */
public class BulkProvisionException extends ServiceException {
    public static final String BP_TOO_MANY_ACCOUNTS = "bulkprovision.BP_TOO_MANY_ACCOUNTS";

    private BulkProvisionException(String message, String code, boolean isReceiversFault) {
        super(message, code, isReceiversFault);
    }

     public static BulkProvisionException BP_TOO_MANY_ACCOUNTS (String desc) {
        return new BulkProvisionException("too many accounts: " + desc, BP_TOO_MANY_ACCOUNTS, SENDERS_FAULT);
    }
}
