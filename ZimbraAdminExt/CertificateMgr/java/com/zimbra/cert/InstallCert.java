package com.zimbra.cert;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.soap.AdminConstants;
import com.zimbra.common.soap.Element;
import com.zimbra.cs.account.Provisioning;
import com.zimbra.cs.account.Server;
import com.zimbra.cs.account.Provisioning.ServerBy;
import com.zimbra.cs.rmgmt.RemoteCommands;
import com.zimbra.cs.rmgmt.RemoteManager;
import com.zimbra.cs.rmgmt.RemoteResult;
import com.zimbra.cs.rmgmt.RemoteResultParser;
import com.zimbra.cs.service.admin.AdminDocumentHandler;
import com.zimbra.soap.ZimbraSoapContext;


public class InstallCert extends AdminDocumentHandler {
    public Element handle(Element request, Map<String, Object> context) throws ServiceException {
        ZimbraSoapContext lc = getZimbraSoapContext(context);
        Provisioning prov = Provisioning.getInstance();
        
        //get a server
        List<Server> serverList =  prov.getAllServers();
        Server server = ZimbraCertMgrExt.getCertServer(serverList);
        
        if (server == null) {
            throw ServiceException.INVALID_REQUEST("No valid server was found", null);
        }
        
        RemoteManager rmgr = RemoteManager.getRemoteManager(server);
        rmgr.execute(ZimbraCertMgrExt.INSTALL_CERT_CMD);
        Element response = lc.createElement(ZimbraCertMgrService.INSTALL_CERT_RESPONSE);
        
        return response;    
    }
}
