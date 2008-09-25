package com.zimbra.bp;

import com.zimbra.cs.service.admin.AdminDocumentHandler;
import com.zimbra.cs.service.FileUploadServlet;
import com.zimbra.cs.account.Provisioning;
import com.zimbra.common.soap.Element;
import com.zimbra.common.service.ServiceException;
import com.zimbra.common.util.ZimbraLog;
import com.zimbra.soap.ZimbraSoapContext;

import java.util.Map;
import java.io.InputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import au.com.bytecode.opencsv.CSVReader;

/**
 * Created by IntelliJ IDEA.
 * User: ccao
 * Date: Sep 23, 2008
 * Time: 11:16:06 AM
 * To change this template use File | Settings | File Templates.
 */
public class GetBulkProvisionAccounts extends AdminDocumentHandler {

    public static final String A_accountName = "accountName" ;
    public static final String A_displayName = "displayName" ;
    public static final String A_password = "password" ;
    public static final String A_status = "status" ;
    public static final String A_isValid = "isValid" ;

    public static final String ERROR_INVALID_ACCOUNT_NAME = "Invalid account name. " ;
    public boolean domainAuthSufficient(Map context) {
        return true;
    }

	public Element handle(Element request, Map<String, Object> context) throws ServiceException {

        String aid = request.getElement("aid").getText();
        //String aid = request.getAttribute("aid");
        ZimbraLog.extensions.debug("Uploaded CSV file id = " + aid) ;

        ZimbraSoapContext lc = getZimbraSoapContext(context);
//	    Provisioning prov = Provisioning.getInstance();

        Element response = lc.createElement(ZimbraBulkProvisionService.GET_BULK_PROVISION_ACCOUNTS_RESPONSE);
        response.addElement("aid").addText(aid) ;

        FileUploadServlet.Upload up = FileUploadServlet.fetchUpload(lc.getAuthtokenAccountId(), aid, lc.getAuthToken());
        if (up == null)
           throw ServiceException.FAILURE("Uploaded CSV file with id " + aid + " was not found.", null);

        try {
            InputStream in = up.getInputStream() ;
            CSVReader reader = new CSVReader(new InputStreamReader(in)) ;
            String [] nextLine ;
            String accountName, displayName, password, status ;
            boolean isValidEntry = true;
            Element el ;
            ZimbraLog.extensions.debug("Read CSV file content.")  ;
            while ((nextLine = reader.readNext()) != null) {
                isValidEntry = true ;
                accountName = nextLine [0] ;
                displayName = nextLine [1] ;
                password = nextLine [2] ;
                status = "" ;

                if (accountName == null || accountName.length() <= 0 || !canAccessEmail(lc, accountName)) {
                    isValidEntry = false ;
                    status += ERROR_INVALID_ACCOUNT_NAME ;
                }

                el = response.addElement("account") ;
                el.addKeyValuePair(A_accountName, accountName) ;
                el.addKeyValuePair(A_displayName, displayName) ;
                el.addKeyValuePair(A_password, password) ;
                
                if (isValidEntry) {
                    el.addKeyValuePair(A_isValid, "TRUE");
                }else{
                    el.addKeyValuePair(A_isValid, "FALSE");
                }
                
                if ((status != null) && (status.length()>0)) {
                    el.addKeyValuePair(A_status, status) ;
                }
            }

            in.close();
           
        }catch (IOException e) {
            ZimbraLog.extensions.error(e);
        }

        return response;
	}
}