package com.zimbra.yahoosmb;

import com.zimbra.cs.service.admin.AdminDocumentHandler;
import com.zimbra.cs.service.admin.AdminService;
import com.zimbra.cs.service.account.ToXML;
import com.zimbra.cs.account.Provisioning;
import com.zimbra.cs.account.AttributeManager;
import com.zimbra.cs.account.Account;
import com.zimbra.common.soap.Element;
import com.zimbra.common.soap.AdminConstants;
import com.zimbra.common.service.ServiceException;
import com.zimbra.common.util.ZimbraLog;
import com.zimbra.soap.ZimbraSoapContext;

import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: ccao
 * Date: Jun 10, 2008
 * Time: 12:45:50 PM
 * To change this template use File | Settings | File Templates.
 */
public class SendInvitation extends AdminDocumentHandler {
    public final static String E_SENDTO = "sendto" ;
    public final static String E_ID = "id" ;

    public boolean domainAuthSufficient(Map context) {
        return true;
    }
    
    public Element handle(Element request, Map<String, Object> context) throws ServiceException {

        ZimbraSoapContext lc = getZimbraSoapContext(context);
	    Provisioning prov = Provisioning.getInstance();

        String name = request.getAttribute(AdminConstants.E_NAME).toLowerCase();
	    String sendto = request.getAttribute(E_SENDTO);
	    String accountId = request.getAttribute(E_ID);
        //TODO:  we should have a way to generate the URL based on the ID
        String registrationURL = "http://integrationserver/" + accountId ;

        if (!canAccessEmail(lc, name))
            throw ServiceException.PERM_DENIED("can not access account:"+name);

        ZimbraLog.security.info("Sending the invitation email to " + sendto);

        Element response = lc.createElement(ZimbraYahooSmbService.SEND_INVITATION_RESPONSE);

        Element e = response.addElement(E_SENDTO) ;
        e.setText(sendto) ;
        
        return response;
	}
}
