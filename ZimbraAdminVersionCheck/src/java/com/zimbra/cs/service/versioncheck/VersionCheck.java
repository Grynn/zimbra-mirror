package com.zimbra.cs.service.versioncheck;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.zimbra.cs.account.Account;
import com.zimbra.cs.account.AuthToken;
import com.zimbra.cs.account.AuthTokenException;
import com.zimbra.cs.account.Config;
import com.zimbra.cs.account.Provisioning;
import com.zimbra.cs.account.Server;
import com.zimbra.common.localconfig.LC;
import com.zimbra.common.service.ServiceException;
import com.zimbra.common.soap.AdminConstants;
import com.zimbra.common.soap.Element;
import com.zimbra.common.soap.MailConstants;
import com.zimbra.common.util.DateUtil;
import com.zimbra.common.util.StringUtil;
import com.zimbra.common.util.ZimbraLog;
import com.zimbra.cs.account.Provisioning.AccountBy;
import com.zimbra.cs.account.Provisioning.ServerBy;
import com.zimbra.cs.account.accesscontrol.AdminRight;
import com.zimbra.cs.service.AuthProvider;
import com.zimbra.cs.service.admin.AdminDocumentHandler;
import com.zimbra.cs.servlet.ZimbraServlet;
import com.zimbra.cs.util.AccountUtil;
import com.zimbra.cs.util.BuildInfo;
import com.zimbra.cs.zclient.ZEmailAddress;
import com.zimbra.cs.zclient.ZMailbox;
import com.zimbra.cs.zclient.ZMailbox.Options;
import com.zimbra.cs.zclient.ZMailbox.ZOutgoingMessage;
import com.zimbra.cs.zclient.ZMailbox.ZOutgoingMessage.MessagePart;
import com.zimbra.soap.ZimbraSoapContext;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.util.URIUtil;
import org.dom4j.DocumentException;
import com.zimbra.cs.httpclient.URLUtil;
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
	public static String A_PLATFORM = "platform";
	public static String A_BUILDTYPE = "buildtype";
	
	@Override
	public Element handle(Element request, Map<String, Object> context)	throws ServiceException {
        ZimbraSoapContext zc = getZimbraSoapContext(context);
    	Provisioning prov = Provisioning.getInstance();        
        Config config = prov.getConfig();
    	checkRight(zc, context, null, AdminRight.PR_SYSTEM_ADMIN_ONLY);      
        String action = request.getAttribute(MailConstants.E_ACTION);
    	Element response = zc.createElement(VersionCheckService.VC_RESPONSE);
        if(action.equalsIgnoreCase(VersionCheckService.VERSION_CHECK_CHECK)) {
        	//check if we need to proxy to the updater server
        	String updaterServerId = config.getAttr(Provisioning.A_zimbraVersionCheckServer);

            if (updaterServerId != null) {
                Server server = prov.get(ServerBy.id, updaterServerId);
                if (server != null && !getLocalHostId().equalsIgnoreCase(server.getId()))
                    return proxyRequest(request, context, server);
            }
            
        	//perform the version check
        	checkVersion();
        	
        	
        	// check if there are any emails to notify of a new version
			boolean sendNotification = false;
			String emails = config.getAttr(Provisioning.A_zimbraVersionCheckNotificationEmail);
			if (emails != null && emails.length() > 0) {
				sendNotification = true;
			}
			if (sendNotification) {
				String resp = config.getAttr(Provisioning.A_zimbraVersionCheckLastResponse);
				try {
					Element respDoc = Element.parseXML(resp);
					boolean hasUpdates = respDoc.getAttributeBool(A_VERSION_CHECK_STATUS, false);
					boolean hasCritical = false;
					if (hasUpdates) {
						String msg = "Found the following zimbra updates:\n";
						Element eUpdates = respDoc.getElement(E_UPDATES);
						for (Iterator<Element> iter = eUpdates.elementIterator(E_UPDATE); iter.hasNext();) {
							Element eUpdate = iter.next();
//							String updateType = eUpdate.getAttribute(A_UPDATE_TYPE);
							boolean isCritical = eUpdate.getAttributeBool(A_CRITICAL, false);
							if (isCritical)
								hasCritical = true;

							String detailsUrl = eUpdate.getAttribute(A_UPDATE_URL);
//							String description = eUpdate.getAttribute(A_DESCRIPTION);
							String version = eUpdate.getAttribute(A_VERSION);
//							String release = eUpdate.getAttribute(A_RELEASE);
							String platform = eUpdate.getAttribute(A_PLATFORM);
//							String buildtype = eUpdate.getAttribute(A_BUILDTYPE);
//							String shortVersion = eUpdate.getAttribute(A_SHORT_VERSION);
							if (sendNotification) {
								if (isCritical) {
									msg += String.format(
													"Version: %s; platform: %s, detailsUrl %s. This update is critical!\n",
													version, platform,
													detailsUrl);
								} else {
									msg += String.format(
													"Version: %s; platform: %s, detailsUrl %s. \n",
													version, platform,
													detailsUrl);
								}
							}
						}
						try {
							AuthToken at = zc.getAuthToken();
							String acctId = at.getAccountId();
							// use ZMailbox to add messages to target mailbox
							Account targetAccount = Provisioning.getInstance().get(AccountBy.id, acctId);
							String accountSOAPURI = AccountUtil.getSoapUri(targetAccount);
							AuthToken targetAuth = AuthProvider.getAuthToken(targetAccount,	System.currentTimeMillis() + 3600 * 1000);
							Options options = new Options();
							options.setAuthToken(targetAuth.getEncoded());
							options.setTargetAccount(acctId);
							options.setTargetAccountBy(AccountBy.id);
							if(accountSOAPURI == null) {
								accountSOAPURI =  URLUtil.getSoapURL(prov.getLocalServer(),true);
							}
							options.setUri(accountSOAPURI);
							options.setNoSession(true);
							ZMailbox zmbox = ZMailbox.getMailbox(options);
							ZOutgoingMessage m = new ZOutgoingMessage();
							List<ZEmailAddress> addrs = new ArrayList<ZEmailAddress>();
							addrs.addAll(ZEmailAddress.parseAddresses(emails,ZEmailAddress.EMAIL_TYPE_TO));
							if (hasCritical)
								m.setSubject("Critical Zimbra update is available.");
							else
								m.setSubject("Zimbra update is available.");

							m.setAddresses(addrs);
							m.setMessagePart(new MessagePart(
											"text/plain", msg));
							zmbox.sendMessage(m, null, false);
						} catch (Exception e) {
							ZimbraLog.extensions.error(
											"Version check extension failed to send notifications.",
											this, e);
						}
					}
				} catch (DocumentException e) {
					throw ServiceException.FAILURE(
									"error parsing  zimbraVersionCheckLastResponse config attribute",
									e);
				}
			}
        	
        } else if(action.equalsIgnoreCase(VersionCheckService.VERSION_CHECK_STATUS)) {
			try {

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
		                String platform = eUpdate.getAttribute(A_PLATFORM);
		                String buildtype = eUpdate.getAttribute(A_BUILDTYPE);
		                String shortVersion = eUpdate.getAttribute(A_SHORT_VERSION);
		                
		                Element elRespUpdate = elRespUpdates.addElement(E_UPDATE);
		                elRespUpdate.addAttribute(A_UPDATE_TYPE,updateType);
		                elRespUpdate.addAttribute(A_CRITICAL,isCritical);
		                elRespUpdate.addAttribute(A_UPDATE_URL,detailsUrl);
		                elRespUpdate.addAttribute(A_DESCRIPTION,description);
		                elRespUpdate.addAttribute(A_SHORT_VERSION,shortVersion);
		                elRespUpdate.addAttribute(A_RELEASE,release);
		                elRespUpdate.addAttribute(A_VERSION,version);
		                elRespUpdate.addAttribute(A_BUILDTYPE,buildtype);
		                elRespUpdate.addAttribute(A_PLATFORM,platform);		                
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
