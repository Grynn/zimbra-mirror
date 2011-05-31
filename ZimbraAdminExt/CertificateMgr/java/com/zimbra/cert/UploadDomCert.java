

package com.zimbra.cert;

import java.io.*;
import java.util.List;
import java.util.Map;
import java.util.Date;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import com.zimbra.common.localconfig.LC;
import com.zimbra.common.service.ServiceException;
import com.zimbra.common.soap.AdminConstants;
import com.zimbra.common.soap.Element;
import com.zimbra.common.util.ByteUtil;
import com.zimbra.common.util.ZimbraLog;
import com.zimbra.common.util.StringUtil;
import com.zimbra.cs.account.Provisioning;
import com.zimbra.cs.account.Server;
import com.zimbra.cs.account.Provisioning.ServerBy;
import com.zimbra.cs.account.accesscontrol.AdminRight;
import com.zimbra.cs.account.accesscontrol.Rights.Admin;
import com.zimbra.cs.rmgmt.RemoteCommands;
import com.zimbra.cs.rmgmt.RemoteManager;
import com.zimbra.cs.rmgmt.RemoteResult;
import com.zimbra.cs.rmgmt.RemoteResultParser;
import com.zimbra.cs.service.FileUploadServlet;
import com.zimbra.cs.service.FileUploadServlet.Upload;
import com.zimbra.cs.service.admin.AdminDocumentHandler;
import com.zimbra.cs.service.admin.AdminRightCheckPoint;
import com.zimbra.soap.ZimbraSoapContext;

public class UploadDomCert extends AdminDocumentHandler {
    private final static String CERT_AID = "cert.aid" ;
    private final static String KEY_AID = "key.aid" ;
    private final static String CERT_NAME = "cert.filename" ;
    private final static String KEY_NAME = "key.filename" ;

   	public Element handle(Element request, Map<String, Object> context) throws ServiceException {
   		ZimbraSoapContext lc = getZimbraSoapContext(context);
        Element response = lc.createElement(ZimbraCertMgrService.UPLOAD_DOMCERT_RESPONSE);

        String attachId = null;
        String filename = null;
        Upload up = null ;

		try {
            attachId = request.getAttribute(CERT_AID) ;
            filename = request.getAttribute(CERT_NAME) ;
            ZimbraLog.security.debug("Found certificate Filename  = " + filename + "; attid = " + attachId );

            up = FileUploadServlet.fetchUpload(lc.getAuthtokenAccountId(), attachId, lc.getAuthToken());
            if (up == null)
                throw ServiceException.FAILURE("Uploaded file " + filename + " with " + attachId + " was not found.", null);

            byte [] blob = ByteUtil.getContent(up.getInputStream(),-1) ;
            if(blob.length > 0)
                response.addAttribute("cert_content", new String(blob));
		}catch (IOException ioe) {
			throw ServiceException.FAILURE("Can not get uploaded certificate content", ioe);
		}finally {
            FileUploadServlet.deleteUpload(up);
        }

        try {
            attachId = request.getAttribute(KEY_AID) ;
            filename = request.getAttribute(KEY_NAME) ;
            ZimbraLog.security.debug("Found certificate Filename  = " + filename + "; attid = " + attachId );

            up = FileUploadServlet.fetchUpload(lc.getAuthtokenAccountId(), attachId, lc.getAuthToken());
            if (up == null)
                throw ServiceException.FAILURE("Uploaded file " + filename + " with " + attachId + " was not found.", null);

            byte [] blob = ByteUtil.getContent(up.getInputStream(),-1) ;
            if(blob.length > 0)
                response.addAttribute("key_content", new String(blob));
		}catch (IOException ioe) {
			throw ServiceException.FAILURE("Can not get uploaded key content", ioe);
		}finally {
            FileUploadServlet.deleteUpload(up);
        }

        return response;


   	}
}
