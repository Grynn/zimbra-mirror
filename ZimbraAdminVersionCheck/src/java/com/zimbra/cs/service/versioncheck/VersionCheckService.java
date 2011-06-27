package com.zimbra.cs.service.versioncheck;

import com.zimbra.soap.DocumentDispatcher;
import com.zimbra.soap.DocumentService;

import com.zimbra.common.soap.AdminConstants;
/**
 * @author Greg Solovyev
 */
public class VersionCheckService implements DocumentService {
    public void registerHandlers(DocumentDispatcher dispatcher) {
        dispatcher.registerHandler(AdminConstants.VC_REQUEST, new VersionCheck());
    }
}
