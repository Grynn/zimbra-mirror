package com.zimbra.bp;

import com.zimbra.common.service.ServiceException;
import com.zimbra.soap.SoapServlet;
import com.zimbra.cs.extension.ZimbraExtension;

/**
 * Created by IntelliJ IDEA.
 * User: ccao
 * Date: Sep 11, 2008
 * Time: 10:56:57 AM
 * To change this template use File | Settings | File Templates.
 */
public class ZimbraBulkProvisionExt implements ZimbraExtension {

    public static final String EXTENSION_NAME_BULKPROVISION = "com_zimbra_bulkprovision";

    public void destroy() {
    }

    public String getName() {
        return EXTENSION_NAME_BULKPROVISION ;
    }

    public void init() throws ServiceException {
        //need to add the service calls to the admin soap calls
        SoapServlet.addService("AdminServlet", new ZimbraBulkProvisionService());
    }
               
    
}
