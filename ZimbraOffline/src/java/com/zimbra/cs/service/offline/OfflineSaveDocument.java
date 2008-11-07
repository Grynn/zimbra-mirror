package com.zimbra.cs.service.offline;

import java.util.Map;
import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import com.zimbra.common.util.ByteUtil;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpState;
import org.apache.commons.httpclient.methods.InputStreamRequestEntity;
import org.apache.commons.httpclient.methods.PostMethod;
import com.zimbra.common.service.ServiceException;
import com.zimbra.common.soap.Element;
import com.zimbra.common.soap.MailConstants;
import com.zimbra.cs.offline.common.OfflineConstants;
import com.zimbra.cs.service.util.ItemId;
import com.zimbra.cs.service.FileUploadServlet;
import com.zimbra.cs.account.Provisioning;
import com.zimbra.cs.account.Account;
import com.zimbra.cs.account.ZimbraAuthTokenEncoded;

public class OfflineSaveDocument extends OfflineDocumentHandlers.SaveDocument {
    
    @Override
    protected Element proxyRequest(Element request, Map<String, Object> context, ItemId iidRequested, ItemId iidResolved)
        throws ServiceException {
        uploadOfflineDocument(request, context, iidRequested, iidResolved);
        return super.proxyRequest(request, context, iidRequested, iidResolved);
    }
    
    private void uploadOfflineDocument(Element request, Map<String, Object> context, ItemId iidRequested, ItemId iidResolved)
        throws ServiceException {
        Element eDoc = request.getElement(MailConstants.E_DOC);
        Element eUpload = eDoc.getElement(MailConstants.E_UPLOAD);
        String id = eUpload.getAttribute(MailConstants.A_ID);
        FileUploadServlet.Upload upload =  FileUploadServlet.fetchUpload(OfflineConstants.LOCAL_ACCOUNT_ID, id, null);
        
        Provisioning prov = Provisioning.getInstance();
        String acctId = iidRequested.getAccountId();
        Account account = prov.get(Provisioning.AccountBy.id, acctId);
        String url = account.getAttr(OfflineConstants.A_offlineRemoteServerUri);
        if (url == null)
            throw ServiceException.FAILURE("not a zimbra account: " + account.getName(), null);
        url += "/service/upload";        
        String authToken = prov.getProxyAuthToken(acctId);
        
        PostMethod post = new PostMethod(url);;
        try {
            post.setRequestEntity(new InputStreamRequestEntity(upload.getInputStream(), upload.getContentType() + "; name=\"" + upload.getName() + "\""));        
            HttpState state = new HttpState();
            (new ZimbraAuthTokenEncoded(authToken)).encode(state, false, post.getURI().getHost());
            
            HttpClient client = new HttpClient();
            client.setState(state);
            int statusCode = -1;
            for (int retryCount = 3; statusCode == -1 && retryCount > 0; retryCount--) {
                statusCode = client.executeMethod(post);
            }
            if (statusCode == -1)
                throw ServiceException.FAILURE("failed to upload to " + url, null);
            
            InputStream resp = post.getResponseBodyAsStream();
            String body = new String(ByteUtil.readInput(resp, 0, 2048));
            Pattern pattern = Pattern.compile("\\w{8}-\\w{4}-\\w{4}-\\w{4}-\\w{12}:\\w{8}-\\w{4}-\\w{4}-\\w{4}-\\w{12}");
            Matcher matcher = pattern.matcher(body);
            if (!matcher.find())
                throw ServiceException.FAILURE("failed to upload to " + url, null);
            id = body.substring(matcher.start(), matcher.end());
            eUpload.addAttribute(MailConstants.A_ID, id);
        } catch (IOException e) {
            throw ServiceException.FAILURE("io error reading upload file: " + e.getMessage(), e);            
        } finally {
            post.releaseConnection();
        }
        
    }
}
