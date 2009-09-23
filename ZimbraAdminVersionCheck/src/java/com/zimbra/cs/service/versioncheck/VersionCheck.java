package com.zimbra.cs.service.versioncheck;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.zimbra.cs.account.Config;
import com.zimbra.cs.account.Provisioning;
import com.zimbra.common.service.ServiceException;
import com.zimbra.common.soap.AdminConstants;
import com.zimbra.common.soap.Element;
import com.zimbra.common.soap.MailConstants;
import com.zimbra.common.util.DateUtil;
import com.zimbra.common.util.StringUtil;
import com.zimbra.cs.account.accesscontrol.AdminRight;
import com.zimbra.cs.service.admin.AdminDocumentHandler;
import com.zimbra.cs.util.BuildInfo;
import com.zimbra.soap.ZimbraSoapContext;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.util.URIUtil;
import org.dom4j.DocumentException;

public class VersionCheck extends AdminDocumentHandler {
	public static String E_UPDATES= "updates";
	public static String E_UPDATE = "update";
	public static String E_VERSION_CHECK = "versionCheck";
	public static String A_VERSION_CHECK_STATUS = "status";
	public static String A_UPDATE_TYPE = "type";
	public static String UPDATE_TYPE_MAJOR = "major";
	public static String UPDATE_TYPE_MINOR = "minor";
	
	@Override
	public Element handle(Element request, Map<String, Object> context)	throws ServiceException {
        ZimbraSoapContext zc = getZimbraSoapContext(context);
        
        checkRight(zc, context, null, AdminRight.PR_SYSTEM_ADMIN_ONLY);      
        String action = request.getAttribute(MailConstants.E_ACTION);
        if(action == VersionCheckService.VERSION_CHECK_CHECK) {
        	//check if we are the correct host
        	checkVersion();
        } else if(action == VersionCheckService.VERSION_CHECK_STATUS) {
			try {
	        	Provisioning prov = Provisioning.getInstance();
	            Config config = prov.getConfig();
	        	String resp = config.getAttr(Provisioning.A_zimbraVersionCheckLastResponse);
				Element respDoc;

				respDoc = Element.parseXML(resp);
				Element eVersionCheck = respDoc.getElement(E_VERSION_CHECK);
				boolean hasUpdates = eVersionCheck.getAttributeBool(A_VERSION_CHECK_STATUS, false);
				if(hasUpdates) {
					
				}
				Element eUpdates = respDoc.getElement(E_UPDATES);
	            for (Iterator<Element> iter = eUpdates.elementIterator(E_UPDATE); iter.hasNext(); ) {
	                Element eUpdate = iter.next();
	                String updateType = eUpdate.getAttribute(A_UPDATE_TYPE);
	            }
			} catch (DocumentException e) {
				throw ServiceException.FAILURE("error parsing  zimbraVersionCheckLastResponse config attribute", e);
			}
            
        }
		return null;
	}

	private void checkVersion () throws ServiceException {
		Provisioning prov = Provisioning.getInstance();
		Config config = prov.getConfig();
		String url = config.getAttr(Provisioning.A_zimbraVersionCheckURL);
		GetMethod method = new GetMethod(url);
		HttpClient client = new HttpClient( );
		boolean checkSuccess=false;
		String resp = null;
		String query = String.format("%s=%s&%s=%s&%s=%s&%s=%s&%s=%s",
				AdminConstants.A_VERSION_INFO_VERSION,BuildInfo.VERSION,
				AdminConstants.A_VERSION_INFO_RELEASE,BuildInfo.RELEASE,
				AdminConstants.A_VERSION_INFO_DATE,BuildInfo.DATE,
				AdminConstants.A_VERSION_INFO_HOST,BuildInfo.HOST,
				AdminConstants.A_VERSION_INFO_TYPE,
				(StringUtil.isNullOrEmpty(BuildInfo.TYPE) ? "unknown" : BuildInfo.TYPE));
		
		try {
			method.setQueryString(URIUtil.encodeQuery(query));
			client.executeMethod( method );
			resp = method.getResponseBodyAsString();
			if(!StringUtil.isNullOrEmpty(resp)) {
				checkSuccess = true;
			}

/**
 * <?xml version="1.0"?>
 * <versionCheck status="1 - updates available| 0 - up to date">
 * <updates>
 * <update type="minor" version = "6.0.18" critical="0|1" detailsURL="URL" description="text"/>
 * <update type="minor" version = "6.0.19" critical="0|1" detailsURL="URL" description="text"/>
 * <update type="major" version = "7.0.2" critical="0|1" detailsURL="URL" description="text"/>
 * </updates>
 * </versionCheck>
 */
		} catch (URIException e) {
			throw ServiceException.FAILURE("Failed to create query string for version check.",e);
		} catch (HttpException e) {
			throw ServiceException.FAILURE("Failed to send HTTP request to version check script.",e);
		} catch (IOException e) {
			throw ServiceException.FAILURE("Failed to send HTTP request to version check script.",e);
		}  finally {
			String lastAttempt = DateUtil.toGeneralizedTime(new Date());
			Map<String, String> attrs = new HashMap<String, String>();
			attrs.put(Provisioning.A_zimbraVersionCheckLastAttempt, lastAttempt);
			if(checkSuccess) {
				attrs.put(Provisioning.A_zimbraVersionCheckLastSuccess, lastAttempt);
				attrs.put(Provisioning.A_zimbraVersionCheckLastResponse, resp);
			}
			prov.modifyAttrs(config, attrs, true);
			
			//send a notification
		}

		
	}
}
