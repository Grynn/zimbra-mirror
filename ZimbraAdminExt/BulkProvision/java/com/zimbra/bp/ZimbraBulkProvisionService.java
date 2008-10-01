package com.zimbra.bp;

import com.zimbra.soap.DocumentService;
import com.zimbra.soap.DocumentDispatcher;

import org.dom4j.Namespace;
import org.dom4j.QName;

/**
 * Created by IntelliJ IDEA.
 * User: ccao
 * Date: Sep 11, 2008
 * Time: 10:59:29 AM
 * To change this template use File | Settings | File Templates.
 */
public class ZimbraBulkProvisionService  implements DocumentService {
    public static final String NAMESPACE_STR = "urn:zimbraAdminExt";
    public static final Namespace NAMESPACE = Namespace.get(NAMESPACE_STR);

    public static final QName GET_BULK_PROVISION_ACCOUNTS_REQUEST = QName.get("GetBulkProvisionAccountsRequest", NAMESPACE) ;
    public static final QName GET_BULK_PROVISION_ACCOUNTS_RESPONSE = QName.get("GetBulkProvisionAccountsResponse", NAMESPACE) ;

    public static final QName UPDATE_BULK_PROVISION_STATUS_REQUEST = QName.get("UpdateBulkProvisionStatusRequest", NAMESPACE) ;
    public static final QName UPDATE_BULK_PROVISION_STATUS_RESPONSE = QName.get("UpdateBulkProvisionStatusResponse", NAMESPACE) ;

    public void registerHandlers(DocumentDispatcher dispatcher) {
        dispatcher.registerHandler(GET_BULK_PROVISION_ACCOUNTS_REQUEST, new GetBulkProvisionAccounts());
        dispatcher.registerHandler(UPDATE_BULK_PROVISION_STATUS_REQUEST, new UpdateBulkProvisionStatus());
    }
}
