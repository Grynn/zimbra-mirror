/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2011 Zimbra, Inc.
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
package com.zimbra.cs.offline.util;

import java.io.IOException;

import junit.framework.Assert;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.dom4j.DocumentException;
import org.junit.Test;

import com.zimbra.common.httpclient.HttpClientUtil;
import com.zimbra.common.service.ServiceException;
import com.zimbra.common.soap.Element;
import com.zimbra.common.util.ZimbraHttpConnectionManager;
import com.zimbra.cs.offline.OfflineLog;

public class AutoUpdateTest {

    static final String BASE_UPDATE_URL = "http://localhost/update.php"; //for local testing
//    static final String BASE_UPDATE_URL = "https://www.zimbra.com/aus/zdesktop2/update.php"; //real update site; only updated once build is RTM

    static HttpClient httpClient = ZimbraHttpConnectionManager.getExternalHttpConnMgr().newHttpClient();

    static final String MEDIA_MAC = "_macos_intel.dmg";
    static final String MEDIA_WIN = "_win32.msi";
    static final String MEDIA_LINUX = "_linux_i686.tgz";
    
    //these values change with every release
    static final String EXPECTED_FILE_VERSION = "7_1_2_ga";
    static final String EXPECTED_A_VERSION = "7.1.2";
    static final String EXPECTED_TYPE = "minor";

    //these values change with every build
    static final int EXPECTED_BUILD = 10978;
    static final String HASH_MAC = "0204af2635a4a6444b0f7e880ccce7c9";
    static final int SIZE_MAC = 75922756;
    static final String HASH_WIN = "d9943a891b194fdeea18ff1c7c908b50";
    static final int SIZE_WIN = 95958528;
    static final String HASH_LINUX = "2add86bf0469e9e0b3f1e66d2507e064";
    static final int SIZE_LINUX = 110755660;
    
    static final String EXPECTED_FILE_PREFIX = "zdesktop_"+EXPECTED_FILE_VERSION+"_b"+EXPECTED_BUILD;
    
    static final String PARAM_CHN = "chn";
    static final String PARAM_VER = "ver";
    static final String PARAM_BID = "bid";
    static final String PARAM_BOS = "bos";
    
    static final String E_UPDATES = "updates";
    static final String E_UPDATE  = "update";
    static final String E_PATCH   = "patch";
    static final String A_URL     = "URL";
    static final String A_TYPE    = "type";
    static final String A_VERSION = "version";
    static final String A_HASH    = "hashValue";
    static final String A_SIZE    = "size";
    
    String send(NameValuePair[] params) throws HttpException, IOException {
        GetMethod httpMethod = new GetMethod(BASE_UPDATE_URL);
        httpMethod.setQueryString(params);
        int code = HttpClientUtil.executeMethod(httpClient, httpMethod);
        Assert.assertEquals(200, code);
        return httpMethod.getResponseBodyAsString();
    }
    
    void verifyExpectedUpdate(String response, String fileSuffix, String expectedHash, int expectedSize) throws DocumentException, ServiceException {
        Element xml = Element.parseXML(response);
        Assert.assertEquals(E_UPDATES,xml.getName());
        Element update = xml.getElement(E_UPDATE);
        Assert.assertNotNull(update);
        Assert.assertEquals(EXPECTED_TYPE,update.getAttribute(A_TYPE));
        Assert.assertEquals(EXPECTED_A_VERSION,update.getAttribute(A_VERSION));
        Element patch = update.getElement(E_PATCH);
        String url = patch.getAttribute(A_URL);
        String filename = url.substring(url.lastIndexOf("/")+1);
        Assert.assertEquals(EXPECTED_FILE_PREFIX+fileSuffix, filename);
        Assert.assertEquals(expectedHash, patch.getAttribute(A_HASH));
        Assert.assertEquals(expectedSize, Integer.parseInt(patch.getAttribute(A_SIZE)));
    }
    
    void verifyNoUpdate(String response) throws DocumentException, ServiceException {
        Element xml = Element.parseXML(response);
        Assert.assertEquals(E_UPDATES,xml.getName());
        Assert.assertEquals(0, xml.listElements(E_UPDATE).size());
    }
    
    void sendAndVerify(String chn, String ver, int bid, String os) throws HttpException, IOException, DocumentException, ServiceException {
        NameValuePair[] nvp = new NameValuePair[4];
        nvp[0] = new NameValuePair(PARAM_CHN, chn);
        nvp[1] = new NameValuePair(PARAM_VER, ver);
        nvp[2] = new NameValuePair(PARAM_BID, bid+"");
        nvp[3] = new NameValuePair(PARAM_BOS, os);
        String response = send(nvp);
//        OfflineLog.offline.info("\r\n"+response);
        if (bid < EXPECTED_BUILD) {
            String fileSuffix = null;
            String hash = null;
            int size = 0;
            if (os.equals("macos")) {
                fileSuffix = MEDIA_MAC;
                hash = HASH_MAC;
                size = SIZE_MAC;
            } else if (os.equals("win32")) {
                fileSuffix = MEDIA_WIN;
                hash = HASH_WIN;
                size = SIZE_WIN;
            } else if (os.equals("linux")) {
                fileSuffix = MEDIA_LINUX;
                hash = HASH_LINUX;
                size = SIZE_LINUX;
            } else {
                Assert.fail("Unexpected platform type");
            }
            verifyExpectedUpdate(response, fileSuffix, hash, size);
            OfflineLog.offline.info("Expected update received for %s %s %d", os, ver, bid);
        } else {
            verifyNoUpdate(response);
            OfflineLog.offline.info("No update expected for %s %s %d", os, ver, bid);
        }
    }
    
    String[] platforms = {"macos", "linux", "win32"};
    
    @Test
    public void ga201() throws HttpException, IOException, DocumentException, ServiceException {
        for (String platform: platforms) {
            sendAndVerify("release", "2.0.1", 10659, platform);
        }
    }

    @Test
    public void ga701() throws HttpException, IOException, DocumentException, ServiceException {
        for (String platform: platforms) {
            sendAndVerify("release", "7.0.1", 10791, platform);
        }
    }
    
    @Test
    public void beta711() throws HttpException, IOException, DocumentException, ServiceException {
        for (String platform: platforms) {
            sendAndVerify("beta", "7.1.1", 10867, platform);
        }
    }

    @Test
    public void ga711() throws HttpException, IOException, DocumentException, ServiceException {
        for (String platform: platforms) {
            sendAndVerify("ga", "7.1.1", 10917, platform);
        }
    }

    @Test
    public void alreadyUpdated() throws HttpException, IOException, DocumentException, ServiceException {
        for (String platform: platforms) {
            sendAndVerify("release", EXPECTED_A_VERSION, EXPECTED_BUILD, platform);
        }
    }

}
