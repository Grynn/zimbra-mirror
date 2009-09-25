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

/**
 * @author Greg Solovyev
 */
public class VersionCheck extends AdminDocumentHandler {
	public static String E_UPDATES= "updates";
	public static String E_UPDATE = "update";
	public static String E_VERSION_CHECK = "versionCheck";
	public static String A_VERSION_CHECK_STATUS = "status";
	public static String A_UPDATE_TYPE = "type";
	public static String A_CRITICAL = "critical";
	public static String UPDATE_TYPE_MAJOR = "major";
	public static String UPDATE_TYPE_MINOR = "minor";
	public static String A_UPDATE_URL = "updateURL";
	public static String A_DESCRIPTION = "description";
	public static String A_SHORT_VERSION = "shortversion";
	public static String A_VERSION = "version";
	public static String A_RELEASE = "release";
	
	@Override
	public Element handle(Element request, Map<String, Object> context)	throws ServiceException {
        ZimbraSoapContext zc = getZimbraSoapContext(context);
        
        checkRight(zc, context, null, AdminRight.PR_SYSTEM_ADMIN_ONLY);      
        String action = request.getAttribute(MailConstants.E_ACTION);
    	Element response = zc.createElement(VersionCheckService.VC_RESPONSE);
        if(action == VersionCheckService.VERSION_CHECK_CHECK) {
        	//check if we are the correct host
        	checkVersion();

        } else if(action == VersionCheckService.VERSION_CHECK_STATUS) {
			try {
	        	Provisioning prov = Provisioning.getInstance();
	            Config config = prov.getConfig();
	        	String resp = config.getAttr(Provisioning.A_zimbraVersionCheckLastResponse);

	        	Element respDoc = Element.parseXML(resp);

				boolean hasUpdates = respDoc.getAttributeBool(A_VERSION_CHECK_STATUS, false);
				Element elRespVersionCheck = response.addElement(E_VERSION_CHECK);
				elRespVersionCheck.addAttribute(A_VERSION_CHECK_STATUS, hasUpdates);
				if(hasUpdates) {
					Element eUpdates = respDoc.getElement(E_UPDATES);
					Element elRespUpdates = elRespVersionCheck.addElement(E_UPDATES);
		            for (Iterator<Element> iter = eUpdates.elementIterator(E_UPDATE); iter.hasNext(); ) {
		                Element eUpdate = iter.next();
		                String updateType = eUpdate.getAttribute(A_UPDATE_TYPE);
		                boolean isCritical = eUpdate.getAttributeBool(A_CRITICAL,false);
		                String detailsUrl = eUpdate.getAttribute(A_UPDATE_URL);
		                String description = eUpdate.getAttribute(A_DESCRIPTION);
		                String version = eUpdate.getAttribute(A_VERSION);
		                String release = eUpdate.getAttribute(A_RELEASE);
		                String shortVersion = eUpdate.getAttribute(A_SHORT_VERSION);
		                
		                Element elRespUpdate = elRespUpdates.addElement(E_UPDATE);
		                elRespUpdate.addAttribute(A_UPDATE_TYPE,updateType);
		                elRespUpdate.addAttribute(A_CRITICAL,isCritical);
		                elRespUpdate.addAttribute(A_UPDATE_URL,detailsUrl);
		                elRespUpdate.addAttribute(A_UPDATE_URL,detailsUrl);
		                elRespUpdate.addAttribute(A_DESCRIPTION,description);
		                elRespUpdate.addAttribute(A_SHORT_VERSION,shortVersion);
		                elRespUpdate.addAttribute(A_RELEASE,release);
		                elRespUpdate.addAttribute(A_VERSION,version);
		            }					
				}

			} catch (DocumentException e) {
				throw ServiceException.FAILURE("error parsing  zimbraVersionCheckLastResponse config attribute", e);
			}
            
        }
    	return response;
	}

	
	public static void checkVersion () throws ServiceException {
		Provisioning prov = Provisioning.getInstance();
		Config config = prov.getConfig();
		String url = config.getAttr(Provisioning.A_zimbraVersionCheckURL);
		GetMethod method = new GetMethod(url);
		HttpClient client = new HttpClient( );
		boolean checkSuccess=false;
		String resp = null;
		String query = String.format("%s=%s&%s=%s&%s=%s&%s=%s&%s=%s",
				AdminConstants.A_VERSION_INFO_MAJOR,BuildInfo.MAJORVERSION,
				AdminConstants.A_VERSION_INFO_MINOR,BuildInfo.MINORVERSION,
				AdminConstants.A_VERSION_INFO_MICRO,BuildInfo.MICROVERSION,
				AdminConstants.A_VERSION_INFO_PLATFORM,BuildInfo.PLATFORM,
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
 * <update type="minor" shortversion = "6.0.19" version = "6.0.19_GA_1841.RHEL4.NETWORK" release="20090921024654" critical="0|1" detailsURL="URL" description="text"/>
 * <update type="major" shortversion = "7.0.2" version = "7.0.2_GA_4045.RHEL4.NETWORK" release="20090921024654" critical="0|1" detailsURL="URL" description="text"/>
 * </updates>
 * </versionCheck>
 **/
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
