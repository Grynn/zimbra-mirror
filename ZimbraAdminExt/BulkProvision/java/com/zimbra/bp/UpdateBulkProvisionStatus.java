package com.zimbra.bp;

import com.zimbra.cs.service.admin.AdminDocumentHandler;
import com.zimbra.common.soap.Element;
import com.zimbra.common.service.ServiceException;
import com.zimbra.common.util.ZimbraLog;
import com.zimbra.soap.ZimbraSoapContext;

import java.util.Map;
import java.util.Hashtable;

/**
 * Created by IntelliJ IDEA.
 * User: ccao
 * Date: Oct 1, 2008
 * Time: 12:45:51 PM
 * To change this template use File | Settings | File Templates.
 */
public class UpdateBulkProvisionStatus  extends AdminDocumentHandler {

    public static final String A_name = "name" ;
    public static final String A_status = "status" ;

    public boolean domainAuthSufficient(Map context) {
        return true;
    }

	public Element handle(Element request, Map<String, Object> context) throws ServiceException {
        String aid = request.getElement("aid").getTextTrim()  ;
        Hashtable<String, String []> ht = BulkProvisionStatus.getBpStatus(aid) ;
        for (Element e : request.listElements("account")) {
            String name = e.getElement(A_name).getTextTrim() ;
            String status = e.getElement(A_status).getTextTrim() ;
            if (status != null && status.length() > 0){
                ZimbraLog.extensions.debug("Update the privision status for account " + name) ;
                String [] entry = ht.get(name) ;
                if ((entry != null) && (entry.length > 0))
                    entry [BulkProvisionStatus.INDEX_STATUS] = status ;
            }
        }

        ZimbraSoapContext lc = getZimbraSoapContext(context);
        Element response = lc.createElement(ZimbraBulkProvisionService.UPDATE_BULK_PROVISION_STATUS_RESPONSE);

        return response;
	}
}
