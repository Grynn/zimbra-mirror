/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2006, 2007, 2009, 2010 Zimbra, Inc.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.3 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 */
package org.jivesoftware.wildfire.net;

import org.jivesoftware.util.LocaleUtils;
import org.jivesoftware.util.Log;
import com.sun.net.ssl.X509TrustManager;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.X509Certificate;

/**
 * Trust manager which accepts certificates without any validation
 * except date validation.
 * <p/>
 * A skeleton placeholder for developers wishing to implement their own custom
 * trust manager. In future revisions we may expand the skeleton code if customers
 * request assistance in creating custom trust managers.
 * <p/>
 * You only need a trust manager if your server will require clients
 * to authenticated with the server (typically only the server authenticates
 * with the client).
 *
 * @author Iain Shigeoka
 */
public class SSLJiveTrustManager implements X509TrustManager {

    public void checkClientTrusted(X509Certificate[] chain, String authType) {

    }

    public void checkServerTrusted(X509Certificate[] chain, String authType) {
    }

    public boolean isClientTrusted(X509Certificate[] x509Certificates) {
        return true;
    }

    public boolean isServerTrusted(X509Certificate[] x509Certificates) {
        boolean trusted = true;
        try {
            x509Certificates[0].checkValidity();
        }
        catch (CertificateExpiredException e) {
            Log.error(LocaleUtils.getLocalizedString("admin.error"), e);
            trusted = false;
        }
        catch (CertificateNotYetValidException e) {
            Log.error(LocaleUtils.getLocalizedString("admin.error"), e);
            trusted = false;
        }
        return trusted;
    }

    public X509Certificate[] getAcceptedIssuers() {
        return new X509Certificate[0];
    }
}
