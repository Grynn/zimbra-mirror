package com.zimbra.yahoosmb;

import com.zimbra.cs.service.admin.AdminDocumentHandler;
import com.zimbra.cs.service.admin.GetConfig;
import com.zimbra.cs.account.Provisioning;
import com.zimbra.common.soap.Element;
import com.zimbra.common.soap.AdminConstants;
import com.zimbra.common.service.ServiceException;
import com.zimbra.soap.ZimbraSoapContext;

import java.util.Map;
import java.util.Iterator;

/**
 * Created by IntelliJ IDEA.
 * User: ccao
 * Date: May 2, 2008
 * Time: 4:17:33 PM
 * To change this template use File | Settings | File Templates.
 */
public class GetAllSmbConfig extends AdminDocumentHandler {

	public boolean domainAuthSufficient(Map context) {
        return true;
    }

    public Element handle(Element request, Map<String, Object> context) throws ServiceException {

        ZimbraSoapContext lc = getZimbraSoapContext(context);
	    Provisioning prov = Provisioning.getInstance();

	    Map attrs = prov.getConfig().getUnicodeAttrs();

	    Element response = lc.createElement(ZimbraYahooSmbService.GET_ALL_SMB_CONFIG_RESPONSE);

        for (Iterator mit = attrs.entrySet().iterator(); mit.hasNext(); ) {
            Map.Entry entry = (Map.Entry) mit.next();
            String name = (String) entry.getKey();
            Object value = entry.getValue();
            if (value instanceof String[]) {
                String sv[] = (String[]) value;
                GetConfig.doConfig(response, name, sv);
            } else if (value instanceof String){
                GetConfig.doConfig(response, name, (String) value);
            }
        }
	    return response;
	}

}
