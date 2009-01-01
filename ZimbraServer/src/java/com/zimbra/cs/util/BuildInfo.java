/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2005, 2007 Zimbra, Inc.
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

package com.zimbra.cs.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.util.StringUtil;
import com.zimbra.cs.db.Versions;

public class BuildInfo {
    
    public static final String VERSION;
    public static final String TYPE;
    public static final String RELEASE;
    public static final String DATE;
    public static final String HOST;
    
    public static final String FULL_VERSION;

    static {
        String version = "unknown";
        String type = "unknown";
        String release = "unknown";
        String date = "unknown";
        String host = "unknown";
        try {
            Class clz = Class.forName("com.zimbra.cs.util.BuildInfoGenerated");
            version = (String) clz.getField("VERSION").get(null);
            type = (String) clz.getField("TYPE").get(null);
            release = (String) clz.getField("RELEASE").get(null);
            date = (String) clz.getField("DATE").get(null);
            host = (String) clz.getField("HOST").get(null);
        } catch (Exception e) {
            System.err.println("Exception occurred during introspecting; version information incomplete");
            e.printStackTrace();
        }
        VERSION = version;
        TYPE = type;
        RELEASE = release;
        DATE = date;
        HOST = host;
        
        if (TYPE != null && TYPE.length() > 0) {
        	FULL_VERSION = VERSION + " " + RELEASE + " " + DATE + " " + TYPE;
        } else {
        	FULL_VERSION = VERSION + " " + RELEASE + " " + DATE;
        }
    }
    
    public static class Version {
        
        public static final String FUTURE = "future";
        
        private static Pattern mPattern = Pattern.compile("([a-zA-Z]+)(\\d*)");
        
        enum Release {
            BETA, M, RC,GA;
            
            public static Release fromString(String rel) throws ServiceException {
                try {
                    return Release.valueOf(rel);
                } catch (IllegalArgumentException e) {
                    throw ServiceException.INVALID_REQUEST("unknown release: " + rel, e);
                }
            }
        }
        
        private boolean mFuture;
        private int mMajor;
        private int mMinor;
        private int mPatch;
        private Release mRel;
        private int mRelNum;
        private String mVersion;
        
        /**
         * 
         * @param version String in the format of {major number}.{minor number}.{patch number}_{release}{release number}
         * 
         * e.g.
         *     6
         *     6.0
         *     6.0.0
         *     6.0.0_BETA1
         *     6.0.0_RC1
         *     6.0.0_GA
         */
        public Version(String version) throws ServiceException {
            mVersion = version;
            if (FUTURE.equalsIgnoreCase(version)) {
                mFuture = true;
                return;
            }
                
            String ver = version;
            int underscoreAt = version.lastIndexOf('_');
            if (underscoreAt != -1) {
                ver = version.substring(0, underscoreAt);
                String rel = version.substring(underscoreAt+1);
                Matcher matcher = mPattern.matcher(version);
                if (matcher.find()) {
                    mRel = Release.fromString(matcher.group(1));
                    String relNum = matcher.group(2);
                    if (!StringUtil.isNullOrEmpty(relNum))
                        mRelNum = Integer.parseInt(relNum);
                }
            }
            
            String[] parts = ver.split("\\.");
            
            try {
                if (parts.length == 1)
                    mMajor = Integer.parseInt(parts[0]);
                else if (parts.length == 2) {
                    mMajor = Integer.parseInt(parts[0]);
                    mMinor = Integer.parseInt(parts[1]);
                } else if (parts.length == 3) {
                    mMajor = Integer.parseInt(parts[0]);
                    mMinor = Integer.parseInt(parts[1]);
                    mPatch = Integer.parseInt(parts[2]);
                } else
                    throw ServiceException.FAILURE("invalid version format:" + version, null); 
            } catch (NumberFormatException e) {
                throw ServiceException.FAILURE("invalid version format:" + version, e); 
            }
            
        }
        
        /**
         * Compares the two versions
         * 
         * e.g. 
         *     Version.compare("5.0.10", "5.0.9")  returns > 0
         *     Version.compare("5.0.10", "5.0.10") returns == 0
         *     Version.compare("5.0", "5.0.9")     returns < 0
         *     Version.compare("5.0.10_RC1", "5.0.10_BETA3") returns > 0
         *     Version.compare("5.0.10_GA", "5.0.10_RC2") returns > 0
         * 
         * @param versionX
         * @param versionY
         * 
         * @return a negative integer, zero, or a positive integer as versionX is older than, equal to, or newer than the versionY.
         */
        public static int compare(String versionX, String versionY) throws ServiceException {
            Version x = new Version(versionX);
            Version y = new Version(versionY);
            return x.compare(y);
        }
        
        /**
         * Compares this object with the specified version.
         * 
         * @param version
         * @return a negative integer, zero, or a positive integer as this object is older than, equal to, or newer than the specified version.
         */
        public int compare(String version) throws ServiceException  {
            Version other = new Version(version);
            return compare(other);
        }
        
        /**
         * Compares this object with the specified version.
         * 
         * @param version
         * @return a negative integer, zero, or a positive integer as this object is older than, equal to, or newer than the specified version.
         */
        public int compare(Version version) throws ServiceException  {
            if (mFuture) {
                if (version.mFuture)
                    return 0;
                else
                    return 1;
            } else if (version.mFuture)
                return -1;
            
            int r = mMajor - version.mMajor;
            if (r != 0)
                return r;
            
            r = mMinor - version.mMinor;
            if (r != 0)
                return r;
            
            r = mPatch - version.mPatch;
            if (r != 0)
                return r;
            
            if (mRel != null) {
                if (version.mRel != null) {
                    r = mRel.ordinal() - version.mRel.ordinal();
                    if (r != 0)
                        return r;
                    
                    return mRelNum - version.mRelNum;
                } else
                    return 1;
            } else if (version.mRel != null) {
                return -1;
            } else
                return 0;
        }
        
        public boolean isFuture() {
            return mFuture;
        }

        public String toString() {
            return mVersion;
        }
    }

    public static void main(String[] args) {
        System.out.println("Version: " + VERSION);
        System.out.println("Release: " + RELEASE);
        System.out.println("Build Date: " + DATE);
        System.out.println("Build Host: " + HOST);
        System.out.println("Full Version: " + FULL_VERSION);
        System.out.println("DB Version: " + Versions.DB_VERSION);
        System.out.println("Index Version: " + Versions.INDEX_VERSION);
    }
}
