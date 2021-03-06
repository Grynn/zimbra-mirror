/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2004, 2005, 2006, 2007, 2008, 2009, 2010, 2011, 2012, 2013 Zimbra Software, LLC.
 *
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.4 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 */

package com.zimbra.soap;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.eclipse.jetty.continuation.ContinuationSupport;

import com.zimbra.common.localconfig.LC;
import com.zimbra.common.service.ServiceException;
import com.zimbra.common.soap.Element;
import com.zimbra.common.soap.HeaderConstants;
import com.zimbra.common.soap.MailConstants;
import com.zimbra.common.soap.SoapFaultException;
import com.zimbra.common.soap.SoapParseException;
import com.zimbra.common.soap.SoapProtocol;
import com.zimbra.common.soap.SoapTransport;
import com.zimbra.common.soap.XmlParseException;
import com.zimbra.common.soap.ZimbraNamespace;
import com.zimbra.common.util.Log;
import com.zimbra.common.util.LogFactory;
import com.zimbra.common.util.ZimbraLog;
import com.zimbra.cs.account.AccessManager;
import com.zimbra.cs.account.Account;
import com.zimbra.cs.account.AccountServiceException;
import com.zimbra.cs.account.AccountServiceException.AuthFailedServiceException;
import com.zimbra.cs.account.AuthToken;
import com.zimbra.cs.account.GuestAccount;
import com.zimbra.cs.account.Provisioning;
import com.zimbra.cs.mailbox.Mailbox;
import com.zimbra.cs.mailbox.MailboxManager;
import com.zimbra.cs.redolog.RedoLogProvider;
import com.zimbra.cs.service.AuthProvider;
import com.zimbra.cs.service.admin.AdminAccessControl;
import com.zimbra.cs.service.admin.AdminDocumentHandler;
import com.zimbra.cs.session.Session;
import com.zimbra.cs.session.SessionCache;
import com.zimbra.cs.session.SoapSession;
import com.zimbra.cs.stats.ZimbraPerf;
import com.zimbra.cs.util.AccountUtil;
import com.zimbra.cs.util.BuildInfo;
import com.zimbra.cs.util.Zimbra;
import com.zimbra.soap.ZimbraSoapContext.SessionInfo;

/**
 * The soap engine.
 */
public class SoapEngine {
    private static final Log LOG = LogFactory.getLog(SoapEngine.class);

    // attribute used to correlate requests and responses
    public static final String A_REQUEST_CORRELATOR = "requestId";

    public static final String ZIMBRA_CONTEXT = "zimbra.context";
    public static final String ZIMBRA_ENGINE  = "zimbra.engine";
    public static final String ZIMBRA_SESSION = "zimbra.session";

    /** context name of request IP
     *
     *  It can be the IP of the SOAP client, or in the presence of a
     *  real origin IP address http header(X-ORIGINATING-IP) the IP
     *  of the real origin client if the SOAP client is in a trusted
     *  network.
     */
    public static final String REQUEST_IP = "request.ip";

    /** context name of IP of the SOAP client */
    public static final String SOAP_REQUEST_IP = "soap.request.ip";

    /** set to true if we log the soap request */
    public static final String SOAP_REQUEST_LOGGED = "soap.request.logged";

    /** context name of IP of the origin client */
    public static final String ORIG_REQUEST_IP = "orig.request.ip";

    private final DocumentDispatcher dispatcher = new DocumentDispatcher();

    SoapEngine() {
        SoapTransport.setDefaultUserAgent(SoapTransport.DEFAULT_USER_AGENT_NAME, BuildInfo.VERSION);
    }

    /**
     * wraps an exception in a soap fault and returns a SOAP envelope with the soap fault in the body
     * (without throwing the exception)
     */
    private Element soapFaultEnv(SoapProtocol soapProto, String msg, ServiceException e) {
        logFault(msg, e);
        return soapProto.soapEnvelope(soapProto.soapFault(e));
    }

    /**
     * wrap an exception in a soap fault and returns the soap fault (without throwing the exception)
     */
    private Element soapFault(SoapProtocol soapProto, String msg, ServiceException e) {
        logFault(msg, e);
        return soapProto.soapFault(e);
    }

    private void logFault(String msg, ServiceException e) {
        if (e.getCode().equals(ServiceException.AUTH_EXPIRED) || e.getCode().equals(ServiceException.AUTH_REQUIRED)) {
            StackTraceElement[] s = Thread.currentThread().getStackTrace();
            StackTraceElement callSite = s[4]; // third frame from top is the caller
            e.setIdLabel(callSite);
            LOG.warn("%s%s", e.getMessage(), (msg == null ? "" : ": " + msg)); // do not log stack
            LOG.debug(msg, e); // log stack
        } else
            LOG.warn(msg, e);
    }

    private void logRequest(Map<String, Object> context, Element envelope) {
        if (ZimbraLog.soap.isTraceEnabled() && !context.containsKey(SoapEngine.SOAP_REQUEST_LOGGED)) {
            HttpServletRequest servletRequest = (HttpServletRequest) context.get(SoapServlet.SERVLET_REQUEST);
            boolean isResumed = !ContinuationSupport.getContinuation(servletRequest).isInitial();
            ZimbraLog.soap.trace(!isResumed ? "C:\n%s" : "C: (resumed)\n%s", envelope.prettyPrint(true));
            context.put(SOAP_REQUEST_LOGGED, Boolean.TRUE);
        }
    }

    private void logUnparsableRequest(Map<String, Object> context, byte[] soapMessage, String parseError) {
        if (context.containsKey(SoapEngine.SOAP_REQUEST_LOGGED)) {
            return;
        }
        if (ZimbraLog.soap.isInfoEnabled()) {
            HttpServletRequest servletRequest = (HttpServletRequest) context.get(SoapServlet.SERVLET_REQUEST);
            boolean isResumed = !ContinuationSupport.getContinuation(servletRequest).isInitial();
            if (ZimbraLog.soap.isTraceEnabled()) {
                ZimbraLog.soap.trace(!isResumed ? "C: (ParseError:%s)\n%s" : "C: (resumed) (ParseError:%s)\n%s",
                        parseError, new String(soapMessage));
            } else if (soapMessage.length < 2000 /* limit max length to avoid filling log file */) {
                ZimbraLog.soap.info(!isResumed ? "C: (ParseError:%s)\n%s" : "C: (resumed) (ParseError:%s)\n%s",
                        parseError, new String(soapMessage));
            }
            context.put(SOAP_REQUEST_LOGGED, Boolean.TRUE);
        }
    }

    /**
     * Bug 77304 - If the XML for a Soap Request was bad, look at it to see if enough of it is valid to be able
     * to determine the desired response protocol.
     * Use StAX parsing so that we can stop looking at the XML once we have got past the Envelope Header context.
     */
    private SoapProtocol chooseFaultProtocolFromBadXml(InputStream in) {
        SoapProtocol soapProto = SoapProtocol.Soap12; /* Default */
        XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
        XMLStreamReader xmlReader = null;
        int depth = 0;
        boolean inEnvelope = false;
        boolean inHeader = false;
        boolean inContext = false;
        String localName;
        try {
            xmlReader = xmlInputFactory.createXMLStreamReader(in);
            boolean scanningForFormat = true;
            while (scanningForFormat && xmlReader.hasNext()) {
                int eventType = xmlReader.next();
                switch (eventType) {
                    case XMLStreamReader.START_ELEMENT:
                        localName = xmlReader.getLocalName();
                        depth++;
                        if ((depth == 1) && ("Envelope".equals(localName))) {
                            inEnvelope = true;
                            String ns = xmlReader.getNamespaceURI();
                            if (SoapProtocol.Soap11.getNamespace().getStringValue().equals(ns)) {
                                soapProto = SoapProtocol.Soap11; // new default
                            }
                        } else if (inEnvelope && (depth == 2) && ("Header".equals(localName))) {
                            inHeader = true;
                        } else if (inHeader && (depth == 3) && ("context".equals(localName))) {
                            inContext = true;
                        } else if (inContext && (depth == 4) && ("format".equals(localName))) {
                            String respType = xmlReader.getAttributeValue(null, "type");
                            if (respType != null) {
                                if (HeaderConstants.TYPE_JAVASCRIPT.equals(respType)) {
                                    soapProto = SoapProtocol.SoapJS;
                                }
                                scanningForFormat = false;
                            }
                        }
                        break;
                    case XMLStreamReader.END_ELEMENT:
                        localName = xmlReader.getLocalName();
                        if ((depth == 1) && ("Envelope".equals(localName))) {
                            inEnvelope = false;
                            scanningForFormat = false;  /* it wasn't specified, so default it */
                        } else if (inEnvelope && (depth == 2) && ("Header".equals(localName))) {
                            inHeader = false;
                            scanningForFormat = false;  /* it wasn't specified, so default it */
                        } else if (inHeader && (depth == 3) && ("context".equals(localName))) {
                            inContext = false;
                            scanningForFormat = false;  /* it wasn't specified, so default it */
                        }
                        depth--;
                        break;
                }
            }
        } catch (XMLStreamException e) {
            ZimbraLog.soap.debug("Problem trying to determine response protocol from request XML", e);
        } finally {
            if (xmlReader != null) {
                try {
                    xmlReader.close();
                } catch (XMLStreamException e) {
                }
            }
        }
        try {
            in.close();
        } catch (IOException e) {
        }
        return soapProto;
    }

    public Element dispatch(String path, byte[] soapMessage, Map<String, Object> context) {
        if (soapMessage == null || soapMessage.length == 0) {
            SoapProtocol soapProto = SoapProtocol.Soap12;
            return soapFaultEnv(soapProto, "SOAP exception",
                    ServiceException.PARSE_ERROR("empty request payload", null));
        }

        InputStream in = new ByteArrayInputStream(soapMessage);
        Element document = null;
        try {
            if (soapMessage[0] == '<') {
                document = Element.parseXML(in);
            } else {
                document = Element.parseJSON(in);
            }
        } catch (SoapParseException e) {
            SoapProtocol soapProto = SoapProtocol.SoapJS;
            logUnparsableRequest(context, soapMessage, e.getMessage());
            return soapFaultEnv(soapProto, "SOAP exception", ServiceException.PARSE_ERROR(e.getMessage(), e));
        } catch (XmlParseException e) {
            logUnparsableRequest(context, soapMessage, e.getMessage());
            SoapProtocol soapProto = chooseFaultProtocolFromBadXml(new ByteArrayInputStream(soapMessage));
            return soapFaultEnv(soapProto, "SOAP exception", e);
        }
        Element resp = dispatch(path, document, context);

        /*
         * For requests(e.g. AuthRequest) that don't have account info in time when they
         * are normally added to the logging context in dispatch after zsc is established
         * from the SOAP request header.  Thus account logging for zimbra.soap won't be
         * effective when the SOAP request is logged in TRACE level normally.
         *
         * For AuthRequest, we call Account.addAccountToLogContext from the handler as
         * soon as the account, which is only available in the SOAP body, is discovered.
         * Account info should be available after dispatch() so account logger can be
         * triggered.
         */
        logRequest(context, document);

        return resp;
    }

    /**
     * dispatch to the given serviceName the specified document,
     * which should be a soap envelope containing a document to
     * execute.
     *
     * @param path  the path (i.e., /service/foo) of the service to dispatch to
     * @param envelope the top-level element of the message
     * @param context user context parameters
     * @return an XmlObject which is a SoapEnvelope containing the response
     *
     */
    private Element dispatch(String path, Element envelope, Map<String, Object> context) {
        SoapProtocol soapProto = SoapProtocol.determineProtocol(envelope);
        if (soapProto == null) {
            // FIXME: have to pick 1.1 or 1.2 since we can't parse any
            soapProto = SoapProtocol.Soap12;
            return soapFaultEnv(soapProto, "SOAP exception",
                    ServiceException.INVALID_REQUEST("unable to determine SOAP version", null));
        }

        Element doc = soapProto.getBodyElement(envelope);
        if (doc == null) {
            return soapFaultEnv(soapProto, "SOAP exception", ServiceException.INVALID_REQUEST("No SOAP body", null));
        }

        ZimbraSoapContext zsc = null;
        Element ectxt = soapProto.getHeader(envelope, HeaderConstants.CONTEXT);
        try {
            zsc = new ZimbraSoapContext(ectxt, doc.getQName(), context, soapProto);
        } catch (ServiceException e) {
            return soapFaultEnv(soapProto, "unable to construct SOAP context", e);
        }
        SoapProtocol responseProto = zsc.getResponseProtocol();

        String rid = zsc.getRequestedAccountId();
        String proxyAuthToken = null;
        if (rid != null) {
            Provisioning prov = Provisioning.getInstance();
            AccountUtil.addAccountToLogContext(prov, rid, ZimbraLog.C_NAME, ZimbraLog.C_ID, zsc.getAuthToken());
            String aid = zsc.getAuthtokenAccountId();
            if (aid != null && !rid.equals(aid)) {
                AccountUtil.addAccountToLogContext(prov, aid, ZimbraLog.C_ANAME, ZimbraLog.C_AID, zsc.getAuthToken());
            } else if (zsc.getAuthToken() != null && zsc.getAuthToken().getAdminAccountId() != null) {
                AccountUtil.addAccountToLogContext(prov, zsc.getAuthToken().getAdminAccountId(), ZimbraLog.C_ANAME,
                        ZimbraLog.C_AID, zsc.getAuthToken());
            }
            try {
                Mailbox mbox = MailboxManager.getInstance().getMailboxByAccountId(rid, false);
                if (mbox != null) {
                    ZimbraLog.addMboxToContext(mbox.getId());
                }
            } catch (ServiceException ignore) {
            }

            try {
                AuthToken at = zsc.getAuthToken();
                if (at != null) {
                    proxyAuthToken = prov.getProxyAuthToken(rid, context);
                    at.setProxyAuthToken(proxyAuthToken);
                }
            } catch (ServiceException e) {
                LOG.warn("failed to set proxy auth token: %s", e.getMessage());
            }
        }

        if (zsc.getUserAgent() != null) {
            ZimbraLog.addUserAgentToContext(zsc.getUserAgent());
        }
        if (zsc.getVia() != null) {
            ZimbraLog.addViaToContext(zsc.getVia());
        }

        logRequest(context, envelope);

        context.put(ZIMBRA_CONTEXT, zsc);
        context.put(ZIMBRA_ENGINE, this);

        HttpServletRequest servletRequest = (HttpServletRequest) context.get(SoapServlet.SERVLET_REQUEST);
        boolean isResumed = !ContinuationSupport.getContinuation(servletRequest).isInitial();

        Element responseBody = null;
        if (!zsc.isProxyRequest()) {
            // if the client's told us that they've seen through notification block 50, we can drop old notifications up to that point
            acknowledgeNotifications(zsc);

            if (doc.getQName().equals(ZimbraNamespace.E_BATCH_REQUEST)) {
                boolean contOnError = doc.getAttribute(ZimbraNamespace.A_ONERROR, ZimbraNamespace.DEF_ONERROR).equals("continue");
                responseBody = zsc.createElement(ZimbraNamespace.E_BATCH_RESPONSE);
                if (!isResumed) {
                    ZimbraLog.soap.info(doc.getName());
                }
                for (Element req : doc.listElements()) {
                    String id = req.getAttribute(A_REQUEST_CORRELATOR, null);
                    long start = System.currentTimeMillis();
                    Element br = dispatchRequest(req, context, zsc);
                    if (!isResumed) {
                        ZimbraLog.soap.info("(batch) %s elapsed=%d", req.getName(), System.currentTimeMillis() - start);
                    }
                    if (id != null) {
                        br.addAttribute(A_REQUEST_CORRELATOR, id);
                    }
                    responseBody.addElement(br);
                    if (!contOnError && responseProto.isFault(br)) {
                        break;
                    }
                    if (proxyAuthToken != null) {
                        zsc.getAuthToken().setProxyAuthToken(proxyAuthToken); //requests will invalidate it when proxying locally; make sure it's set for each sub-request in batch
                    }
                }
            } else {
                String id = doc.getAttribute(A_REQUEST_CORRELATOR, null);
                long start = System.currentTimeMillis();
                responseBody = dispatchRequest(doc, context, zsc);
                if (!isResumed) {
                    ZimbraLog.soap.info("%s elapsed=%d", doc.getName(), System.currentTimeMillis() - start);
                }
                if (id != null) {
                    responseBody.addAttribute(A_REQUEST_CORRELATOR, id);
                }
            }
        } else {
            // Proxy the request to target server.  Proxy dispatcher
            // discards any session information with remote server.
            // We stick to local server's session when talking to the
            // client.
            try {
                // Detach doc from its current parent, because it will be
                // added as a child element of a new SOAP envelope in the
                // proxy dispatcher.  IllegalAddException will be thrown
                // if we don't detach it first.
                doc.detach();
                ZimbraSoapContext zscTarget = new ZimbraSoapContext(zsc, zsc.getRequestedAccountId()).disableNotifications();
                long start = System.currentTimeMillis();
                responseBody = zsc.getProxyTarget().dispatch(doc, zscTarget);
                ZimbraLog.soap.info("%s proxy=%s,elapsed=%d", doc.getName(), zsc.getProxyTarget(),
                        System.currentTimeMillis() - start);
                responseBody.detach();
            } catch (SoapFaultException e) {
                responseBody = e.getFault() != null ? e.getFault().detach() : responseProto.soapFault(e);
                LOG.debug("proxy handler exception", e);
            } catch (ServiceException e) {
                responseBody = responseProto.soapFault(e);
                LOG.info("proxy handler exception", e);
            } catch (Throwable e) {
                responseBody = responseProto.soapFault(ServiceException.FAILURE(e.toString(), e));
                if (e instanceof OutOfMemoryError) {
                    Zimbra.halt("proxy handler exception", e);
                }
                LOG.warn("proxy handler exception", e);
            }
        }

        // put notifications (new sessions and incremental change notifications) to header...
        Element responseHeader = generateResponseHeader(zsc);
        // ... and return the composed response
        return responseProto.soapEnvelope(responseBody, responseHeader);
    }


    /**
     * Handles individual requests, either direct or from a batch
     */
    Element dispatchRequest(Element request, Map<String, Object> context, ZimbraSoapContext zsc) {
        long startTime = System.currentTimeMillis();
        SoapProtocol soapProto = zsc.getResponseProtocol();

        if (request == null) {
            return soapFault(soapProto, "cannot dispatch request",
                    ServiceException.INVALID_REQUEST("no document specified", null));
        }
        DocumentHandler handler = dispatcher.getHandler(request);
        if (handler == null) {
            return soapFault(soapProto, "cannot dispatch request",
                    ServiceException.UNKNOWN_DOCUMENT(request.getQualifiedName(), null));
        }
        if (RedoLogProvider.getInstance().isSlave() && !handler.isReadOnly()) {
            return soapFault(soapProto, "cannot dispatch request", ServiceException.NON_READONLY_OPERATION_DENIED());
        }
        AuthToken at = zsc.getAuthToken();
        boolean needsAuth = handler.needsAuth(context);
        boolean needsAdminAuth = handler.needsAdminAuth(context);
        if ((needsAuth || needsAdminAuth) && at == null) {
            return soapFault(soapProto, "cannot dispatch request", ServiceException.AUTH_REQUIRED());
        }
        Element response = null;
        SoapTransport.setVia(zsc.getNextVia());
        try {
            Provisioning prov = Provisioning.getInstance();
            if (!prov.getLocalServer().getBooleanAttr(Provisioning.A_zimbraUserServicesEnabled, true) &&
                    !(handler instanceof AdminDocumentHandler)) {
                return soapFault(soapProto, "cannot dispatch request", ServiceException.TEMPORARILY_UNAVAILABLE());
            }
            if (needsAdminAuth) {
                AdminAccessControl aac = AdminAccessControl.getAdminAccessControl(at);
                if (!aac.isSufficientAdminForSoap(context, handler)) {
                    return soapFault(soapProto, "cannot dispatch request",
                            ServiceException.PERM_DENIED("need adequate admin token"));
                }
            }

            String acctId = null;
            boolean isGuestAccount = true;
            boolean delegatedAuth = false;
            if (at != null) {
                acctId = at.getAccountId();
                isGuestAccount = acctId.equals(GuestAccount.GUID_PUBLIC);
                delegatedAuth = at.isDelegatedAuth();
            }

            if (!isGuestAccount) {
                if (needsAuth || needsAdminAuth) {
                    try {
                        AuthProvider.validateAuthToken(prov, at, false);
                    } catch (ServiceException e) {
                        return soapFault(soapProto, null, e);
                    }

                    // also, make sure that the target account (if any) is active
                    if (zsc.isDelegatedRequest() && !handler.isAdminCommand()) {
                        Account target = DocumentHandler.getRequestedAccount(zsc);

                        // treat the account as inactive if (a) it doesn't exist, (b) it's in maintenance mode, or (c) we're non-admins and it's not "active"
                        boolean inactive = target == null || Provisioning.ACCOUNT_STATUS_MAINTENANCE.equals(
                                target.getAccountStatus(prov));
                        if (!inactive && (!at.isAdmin() || !AccessManager.getInstance().canAccessAccount(at, target))) {
                            inactive = !target.getAccountStatus(prov).equals(Provisioning.ACCOUNT_STATUS_ACTIVE);
                        }
                        if (inactive) {
                            return soapFault(soapProto, "target account is not active",
                                    AccountServiceException.ACCOUNT_INACTIVE(target == null ?
                                            zsc.getRequestedAccountId() : target.getName()));
                        }
                    }
                }

                // fault in a session for this handler (if necessary) before executing the command
                context.put(ZIMBRA_SESSION, handler.getSession(zsc));

                // try to proxy the request if necessary (don't proxy commands that don't require auth)
                if (needsAuth || needsAdminAuth) {
                    response = handler.proxyIfNecessary(request, context);
                }
            }

            // if no proxy, execute the request locally
            if (response == null) {
                if (delegatedAuth) {
                    handler.logAuditAccess(at.getAdminAccountId(), acctId, acctId);
                }
                response = handler.handle(request, context);
                ZimbraPerf.SOAP_TRACKER.addStat(getStatName(request), startTime);
                long duration = System.currentTimeMillis() - startTime;
                if (LC.zimbra_slow_logging_enabled.booleanValue() && duration > LC.zimbra_slow_logging_threshold.longValue() &&
                        !request.getQName().getName().equals(MailConstants.SYNC_REQUEST.getName())) {
                    ZimbraLog.soap.warn("Slow SOAP request (start=" + startTime + "):\n" + request.prettyPrint(true));
                    ZimbraLog.soap.warn("Slow SOAP response (time=" + duration + "):\n" + response.prettyPrint());
                }
            }
        } catch (SoapFaultException e) {
            response = e.getFault() != null ? e.getFault().detach() : soapProto.soapFault(ServiceException.FAILURE(e.toString(), e));
            if (!e.isSourceLocal()) {
                LOG.debug("handler exception", e);
            }
        } catch (AuthFailedServiceException e) {
            response = soapProto.soapFault(e);

            if (LOG.isDebugEnabled()) {
                LOG.debug("handler exception: %s%s", e.getMessage(), e.getReason(", %s"), e);
            } else {
                // Don't log stack trace for auth failures, since they commonly happen
                LOG.info("handler exception: %s%s", e.getMessage(), e.getReason(", %s"));
            }
        } catch (ServiceException e) {
            response = soapProto.soapFault(e);
            LOG.info("handler exception", e);
            // XXX: if the session was new, do we want to delete it?
        } catch (Throwable e) {
            // don't interfere with Jetty Continuations -- pass the exception on up
            if (e.getClass().getName().equals("org.eclipse.jetty.continuation.ContinuationThrowable")) {
                throw (Error) e;
            }
            // TODO: better exception stack traces during develope?
            response = soapProto.soapFault(ServiceException.FAILURE(e.toString(), e));
            if (e instanceof OutOfMemoryError) {
                Zimbra.halt("handler exception", e);
            }
            LOG.warn("handler exception", e);
            // XXX: if the session was new, do we want to delete it?
        } finally {
            SoapTransport.clearVia();
        }
        return response;
    }

    /**
     * Returns the SOAP command name.  In most cases this is just the name of the given
     * request element.  If this request is an <tt>XXXActionRequest</tt>, appends the
     * operation to the request name.
     */
    private String getStatName(Element request) {
        if (request == null) {
            return null;
        }
        String statName = request.getName();
        if (statName.endsWith("ActionRequest")) {
            Element action = request.getOptionalElement(MailConstants.E_ACTION);
            if (action != null) {
                String op = action.getAttribute(MailConstants.A_OPERATION, null);
                if (op != null) {
                    statName = String.format("%s.%s", statName, op);
                }
            }
        }
        return statName;
    }

    public DocumentDispatcher getDocumentDispatcher() {
        return dispatcher;
    }

    private void acknowledgeNotifications(ZimbraSoapContext zsc) {
        SessionInfo sinfo = zsc.getSessionInfo();
        if (sinfo != null) {
            Session session = SessionCache.lookup(sinfo.sessionId, zsc.getAuthtokenAccountId());
            // acknowledge ntfn sets up to sinfo.sequence; unset value means acknowledge all sent ntfns
            if (session instanceof SoapSession) {
                ((SoapSession) session).acknowledgeNotifications(sinfo.sequence);
            }
        }
    }

    /** Creates a <tt>&lt;context></tt> element for the SOAP Header containing
     *  session information and change notifications.<p>
     *
     *  Sessions -- those passed in the SOAP request <tt>&lt;context></tt>
     *  block and those created during the course of processing the request -- are
     *  listed:<p>
     *     <tt>&lt;sessionId [type="admin"] id="12">12&lt;/sessionId></tt>
     *
     * @return A new <tt>&lt;context></tt> {@link Element}, or <tt>null</tt>
     *         if there is no relevant information to encapsulate. */
    private Element generateResponseHeader(ZimbraSoapContext zsc) {
        String authAccountId = zsc.getAuthtokenAccountId();
        String requestedAccountId = zsc.getRequestedAccountId();

        Element ctxt = zsc.createElement(HeaderConstants.CONTEXT);
        boolean requiresChangeHeader = requestedAccountId != null;
        try {
            SessionInfo sinfo = zsc.getSessionInfo();
            Session session = sinfo == null ? null : SessionCache.lookup(sinfo.sessionId, authAccountId);
            if (session != null) {
                // session ID is valid, so ping it back to the client:
                ZimbraSoapContext.encodeSession(ctxt, session.getSessionId(), session.getSessionType());

                if (session instanceof SoapSession) {
                    SoapSession soap = (SoapSession) session;
                    if (session.getTargetAccountId().equals(requestedAccountId)) {
                        requiresChangeHeader = false;
                    }
                    // put <refresh> blocks back for any newly-created SoapSession objects
                    if (sinfo.created || soap.requiresRefresh(sinfo.sequence)) {
                        ZimbraLog.session.debug("returning refresh block; reason=%s",
                                sinfo.created ? "new session" : "sequence-based");
                        soap.putRefresh(ctxt, zsc);
                    }
                    // put <notify> blocks back for any SoapSession objects
                    soap.putNotifications(ctxt, zsc, sinfo.sequence);
                    // add any extension headers
                    SoapContextExtension.addExtensionHeaders(ctxt, zsc, soap);
                }
            }

            // bug: 17481 if <nosession> is specified, then the SessionInfo list will be empty...but
            // we still want to encode the <change> element at least for the directly-requested accountId...
            // so encode it here as a last resort
            if (requiresChangeHeader) {
                try {
                    String explicitAcct = requestedAccountId.equals(authAccountId) ? null : requestedAccountId;
                    // send the <change> block
                    // <change token="555" [acct="4f778920-1a84-11da-b804-6b188d2a20c4"]/>
                    Mailbox mbox = DocumentHandler.getRequestedMailbox(zsc, false);
                    if (mbox != null) {
                        ctxt.addUniqueElement(HeaderConstants.E_CHANGE)
                            .addAttribute(HeaderConstants.A_CHANGE_ID, mbox.getLastChangeID())
                            .addAttribute(HeaderConstants.A_ACCOUNT_ID, explicitAcct);
                    }
                } catch (ServiceException e) {
                    // eat error for right now
                }
            }
            return ctxt;
        } catch (ServiceException e) {
            ZimbraLog.session.info("ServiceException while putting soap session refresh data", e);
            return null;
        }
    }

}
