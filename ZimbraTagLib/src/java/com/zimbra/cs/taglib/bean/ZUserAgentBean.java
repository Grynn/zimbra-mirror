/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2007 Zimbra, Inc.
 * 
 * The contents of this file are subject to the Yahoo! Public License
 * Version 1.0 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * 
 * ***** END LICENSE BLOCK *****
 */
package com.zimbra.cs.taglib.bean;

import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ZUserAgentBean {

    private String mUserAgent;

    // state
    Version browserVersion = new Version("-1");
    Version mozVersion = new Version("-1");
    boolean isOsMac = false;
    boolean isOsWindows = false;
    boolean isOsLinux = false;
    boolean isNav  = false;
    boolean isIE = false;
    boolean trueNs = false;
    boolean isFirefox = false;
    boolean isMozilla = false;
    boolean isSafari = false;
    boolean isGeckoBased = false;
    boolean isOpera = false;
    boolean isWebTv = false;
    boolean isHotJava = false;
    boolean isIPhone = false;

    public ZUserAgentBean(String userAgent) {
        mUserAgent = userAgent;
        if (mUserAgent != null) parseUserAgent(userAgent);
    }

    public String getUserAgent() { return mUserAgent; }
    
    private void parseUserAgent(String agent) {
        // parse user agent
        String agt = agent.toLowerCase();
        StringTokenizer agtArr = new StringTokenizer(agt, " ;()");
        int index = -1;
        boolean isSpoofer = false;

        double geckoDate = 0;
        boolean beginsWithMozilla = false;
        boolean isCompatible = false;
        if (agtArr.hasMoreTokens()) {
            String token = agtArr.nextToken();
            Pattern pattern = Pattern.compile("\\s*mozilla");
            Matcher mozilla = pattern.matcher(token);
            if (mozilla.find()){
                index = mozilla.start();
                beginsWithMozilla = true;
                browserVersion = new Version(token.substring(index + 8));
                isNav = true;
            }
            do {
                if (token.indexOf("compatible") != -1 ) {
                    isCompatible = true;
                    isNav = false;
                } else if ((token.indexOf("opera")) != -1){
                    isOpera = true;
                    isNav = false;
                    if ((index = token.indexOf("opera/")) != -1) {
                        browserVersion = new Version(token.substring(index + 6));
                    } else if (agtArr.hasMoreTokens()) {
                        browserVersion = new Version(agtArr.nextToken());
                    }
                } else if ((token.indexOf("spoofer")) != -1){
                    isSpoofer = true;
                    isNav = false;
                } else if ((token.indexOf("webtv")) != -1) {
                    isWebTv = true;
                    isNav = false;
                } else if ((token.indexOf("iphone")) != -1) {
                    isIPhone = true;
                } else if ((token.indexOf("hotjava")) != -1) {
                    isHotJava = true;
                    isNav = false;
                } else if ((index = token.indexOf("msie")) != -1) {
                    isIE = true;
                    if (agtArr.hasMoreTokens()) {
                        browserVersion = new Version(agtArr.nextToken());
                    }
                } else if ((index = token.indexOf("gecko/")) != -1){
                    isGeckoBased = true;
                    geckoDate = Float.parseFloat(token.substring(index + 6));
                } else if ((index = token.indexOf("rv:")) != -1){
                    mozVersion = new Version(token.substring(index + 3));
                    browserVersion = mozVersion;
                } else if ((index = token.indexOf("firefox/")) != -1){
                    isFirefox = true;
                    browserVersion = new Version(token.substring(index + 8));
                } else if ((index = token.indexOf("netscape6/")) != -1){
                    trueNs = true;
                    browserVersion = new Version(token.substring(index + 10));
                } else if ((index = token.indexOf("netscape/")) != -1){
                    trueNs = true;
                    browserVersion = new Version(token.substring(index + 9));
                } else if ((index = token.indexOf("safari/")) != -1){
                    isSafari = true;
                    browserVersion = new Version(token.substring(index + 7));
                } else if (token.indexOf("windows") != -1){
                    isOsWindows = true;
                } else if ((token.indexOf("macintosh") != -1) ||
                        (token.indexOf("mac_") != -1)){
                    isOsMac = true;
                } else if (token.indexOf("linux") != -1){
                    isOsLinux = true;
                }

                token = agtArr.hasMoreTokens() ? agtArr.nextToken() : null;
            } while (token != null);

            // Note: Opera and WebTV spoof Navigator.
            // We do strict client detection.
            isNav  = (beginsWithMozilla && !isSpoofer && !isCompatible &&
                    !isOpera && !isWebTv && !isHotJava &&
                    !isSafari);

            isIE = (isIE && !isOpera);

            isMozilla = ((isNav && mozVersion.getMajor() > -1 && isGeckoBased && (geckoDate != 0)));

            isFirefox = ((isMozilla && isFirefox));
        }
    }

    public Version getBrowserVersion() { return browserVersion; }

    public boolean getIsOsWindows() { return isOsWindows; }

    public boolean getIsOsMac() { return isOsMac; }

    public boolean getIsOsLinux() { return isOsLinux; }
    
    public boolean getIsOpera() { return isOpera; }
    
    public boolean getIsSafari() { return isSafari; }

	public boolean getIsSafari3Up() { return (isSafari && browserVersion.greaterOrEqual(3,0)); }

	public boolean getIsWebTv() { return isWebTv; }

    public boolean getIsNav() { return isNav; }

    public boolean getIsNav4() { return (isNav && (browserVersion.getMajor() == 4) && (!isIE)); }

    public boolean getIsNav6() { return (isNav && trueNs && (browserVersion.getMajor() >= 6) && (browserVersion.getMajor() < 7)); }

    public boolean getIsNav6up() { return (isNav && trueNs && (browserVersion.getMajor() >= 6)); }

    public boolean getIsNav7() { return (isNav && trueNs && (browserVersion.getMajor() == 7)); }

    public boolean getIsIE() { return isIE; }
    
    public boolean getIsIE3() { return (isIE && (browserVersion.getMajor() < 4)); }

    public boolean getIsIE4() { return (isIE && (browserVersion.equals(4,0))); }

    public boolean getIsIE4up() { return (isIE && (browserVersion.getMajor() >= 4)); }

    public boolean getIsIE5() { return (isIE && (browserVersion.equals(5,0))); }

    public boolean getIsIE5_5() { return (isIE && (browserVersion.equals(5,5))); }

    public boolean getIsIE5up() { return (isIE && (browserVersion.getMajor() > 5)); }

    public boolean getIsIE5_5up() { return (isIE && (browserVersion.greaterOrEqual(5,5))); }

    public boolean getIsIE6() { return (isIE && (browserVersion.equals(6,0))); }

    public boolean getIsIE6up() { return (isIE && (browserVersion.getMajor() >= 6)); }

    public boolean getIsIE7() { return (isIE && (browserVersion.equals(7, 0))); }

    public boolean getIsIE7up() { return (isIE && (browserVersion.greaterOrEqual(7, 0))); }

    public boolean getIsMozilla() { return isMozilla; }

    public boolean getIsMozilla1_4up() { return (isMozilla && (mozVersion.greaterOrEqual(1,4))); }

    public boolean getIsFirefox() { return isFirefox; }    

    public boolean getIsFirefox1up() { return (isFirefox && browserVersion.greaterOrEqual(1,0)); }

    public boolean getIsFirefox1_5up() { return (isFirefox && browserVersion.greaterOrEqual(1,5)); }

    public boolean getIsFirefox2up() { return (isFirefox && browserVersion.greaterOrEqual(2,0)); }
    
	public boolean getIsGecko1_8up() { return (isGeckoBased && browserVersion.greaterOrEqual(1,8)); }

    public boolean getIsGecko() { return isGeckoBased; }
    
    public boolean getIsHotJava() { return isHotJava; }

    public boolean getIsiPhone() { return isIPhone; } 

    public static class Version {
        
        private String mRawVersion;
        private String mVersion;
        private int mMajor = -1;
        private int mMinor = 0;

        public Version(String v) {
            mRawVersion = v;
            if (v == null || v.length() == 0) return;

            int d1 = v.indexOf('.');
            if (d1 != -1) {
                mMajor = parseInt(v.substring(0, d1), -1);
                int d2 = v.indexOf('.', d1+1);
                if (d2 != -1)
                    mMinor = parseInt(v.substring(d1+1, d2), 0);
                else
                    mMinor = parseInt(v.substring(d1+1), 0);
            } else {
                mMajor = parseInt(v, -1);
            }
            mVersion = mMajor + "." + mMinor;
        }

        private int parseInt(String s, int defaultVal) {
            try {
                return Integer.parseInt(s);
            } catch (NumberFormatException e) {
                return defaultVal;
            }
        }

        public boolean equals(int major, int minor) { return mMajor == major && mMinor == minor; }

        public boolean greaterOrEqual(int major, int minor) { 
            return (mMajor > major) || (mMajor == major && mMinor >= minor);
        }

        public String getRawVersion() { return mRawVersion; }
        public String getVersion() { return mVersion; }
        public int getMajor() { return mMajor; }
        public int getMinor() { return mMinor; }

        public String toString() { return mRawVersion; }
    }
}
