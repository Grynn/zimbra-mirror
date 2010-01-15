/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2006, 2007, 2009 Zimbra, Inc.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.2 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 */

package com.zimbra.cs.taglib.bean;

import com.zimbra.cs.zclient.ZMessage.ZMimePart;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ZMimePartBean {


    public static final String CT_APP               = "application";
    public static final String CT_APP_ADOBE_PDF	    = "application/pdf";
    public static final String CT_APP_ADOBE_PS	    = "application/postscript";
    public static final String CT_APP_APPLE_DOUBLE 	= "application/applefile";		// IGNORE
    public static final String CT_APP_EXE           = "application/exe";
    public static final String CT_APP_MS_DOWNLOAD	= "application/x-msdownload";
    public static final String CT_APP_MS_EXCEL		= "application/vnd.ms-excel";
    public static final String CT_APP_MS_PPT		= "application/vnd.ms-powerpoint";
    public static final String CT_APP_MS_PROJECT	= "application/vnd.ms-project";
    public static final String CT_APP_MS_TNEF		= "application/ms-tnef"; 		// IGNORE
    public static final String CT_APP_MS_TNEF2 		= "application/vnd.ms-tnef"; 	// IGNORE (added per bug 2339)
    public static final String CT_APP_MS_VISIO		= "application/vnd.visio";
    public static final String CT_APP_MS_WORD		= "application/msword";
    public static final String CT_APP_OCTET_STREAM	= "application/octet-stream";
    public static final String CT_APP_ZIP			= "application/zip";
    public static final String CT_APP_ZIP2			= "application/x-zip-compressed";
    public static final String CT_AUDIO				= "audio";
    public static final String CT_AUDIO_WAV			= "audio/x-wav";
    public static final String CT_AUDIO_MP3			= "audio/mpeg";
    public static final String CT_IMG				= "image";
    public static final String CT_IMG_GIF			= "image/gif";
    public static final String CT_IMG_JPEG			= "image/jpeg";
    public static final String CT_IMG_PNG			= "image/png";
    public static final String CT_IMG_TIFF			= "image/tiff";
    public static final String CT_MSG_RFC822		= "message/rfc822";
    public static final String CT_MULTI			    = "multipart";
    public static final String CT_MULTI_ALT			= "multipart/alternative"; 		// IGNORE
    public static final String CT_MULTI_MIXED		= "multipart/mixed"; 			// IGNORE
    public static final String CT_MULTI_RELATED		= "multipart/related"; 			// IGNORE
    public static final String CT_MULTI_APPLE_DBL 	= "multipart/appledouble"; 		// IGNORE
    public static final String CT_TEXT				= "text";
    public static final String CT_TEXT_RTF			= "text/enriched";
    public static final String CT_TEXT_HTML			= "text/html";
    public static final String CT_TEXT_CAL			= "text/calendar"; 				// IGNORE
    public static final String CT_TEXT_JAVA			= "text/x-java";
    public static final String CT_TEXT_VCARD		= "text/x-vcard";
    public static final String CT_TEXT_PLAIN		= "text/plain";
    public static final String CT_TEXT_XML			= "text/xml";
    public static final String CT_VIDEO				= "video";
    public static final String CT_VIDEO_WMV			= "video/x-ms-wmv";
    public static final String CT_XML_ZIMBRA_SHARE	= "xml/x-zimbra-share";
    public static final String CT_PLAIN_TEXT		= "plain/text"; // strange, saw this type...

    private static final Set<String> sIgnoredTypes =
            new HashSet<String>(Arrays.asList(
                    CT_APP_APPLE_DOUBLE,
                    CT_APP_MS_TNEF,
                    CT_APP_MS_TNEF2,
                    CT_MULTI_ALT,
                    CT_MULTI_MIXED,
                    CT_MULTI_RELATED,
                    CT_MULTI_APPLE_DBL,
                    CT_TEXT_CAL
            ));

    public static boolean isIgnoredPArt(ZMimePart part) {
        if (part.getContentType().equalsIgnoreCase(CT_APP_APPLE_DOUBLE)) {
            if (part.getParent() != null) {
                return part.getParent().getContentType().equalsIgnoreCase(CT_MULTI_APPLE_DBL);
            } else {
                return false;
            }
        } else {
            return sIgnoredTypes.contains(part.getContentType());
        }
    }

    private static final Map<String,String> sTypeToImage = new HashMap<String, String>();

    static {
        sTypeToImage.put(CT_APP,               "doctypes/ImgExeDoc.gif");
        sTypeToImage.put(CT_APP_ADOBE_PDF,     "doctypes/ImgPDFDoc.gif");
        sTypeToImage.put(CT_APP_ADOBE_PS,      "doctypes/ImgGenericDoc.gif");
        sTypeToImage.put(CT_APP_EXE,           "doctypes/ImgExeDoc.gif");

        sTypeToImage.put(CT_APP_MS_DOWNLOAD,   "doctypes/ImgExeDoc.gif");
        sTypeToImage.put(CT_APP_MS_EXCEL,		"doctypes/ImgMSExcelDoc.gif");
        sTypeToImage.put(CT_APP_MS_PPT,			"doctypes/ImgMSPowerpointDoc.gif");
        sTypeToImage.put(CT_APP_MS_PROJECT,		"doctypes/ImgMSProjectDoc.gif");
        sTypeToImage.put(CT_APP_MS_VISIO,		"doctypes/ImgMSVisioDoc.gif");
        sTypeToImage.put(CT_APP_MS_WORD,        "doctypes/ImgMSWordDoc.gif");
        sTypeToImage.put(CT_APP_OCTET_STREAM,	"doctypes/ImgUnknownDoc.gif");
        sTypeToImage.put(CT_APP_ZIP,            "doctypes/ImgZipDoc.gif");
        sTypeToImage.put(CT_APP_ZIP2,			"doctypes/ImgZipDoc.gif");
        sTypeToImage.put(CT_AUDIO,				"doctypes/ImgAudioDoc.gif");
        sTypeToImage.put(CT_VIDEO,				"doctypes/ImgVideoDoc.gif");
        sTypeToImage.put(CT_IMG,                "doctypes/ImgImageDoc.gif");
        sTypeToImage.put(CT_MSG_RFC822,			"doctypes/ImgMessageDoc.gif");
        sTypeToImage.put(CT_TEXT,				"doctypes/ImgGenericDoc.gif");
        sTypeToImage.put(CT_PLAIN_TEXT,			"doctypes/ImgGenericDoc.gif");
        sTypeToImage.put(CT_TEXT_HTML, 			"doctypes/ImgHtmlDoc.gif");
    }

    private ZMimePart mMimePart;

    public ZMimePartBean(ZMimePart mimePart) { mMimePart = mimePart; }

    public ZMimePart getMimePart() { return mMimePart; }
    
    /** @return "" means top-level part, 1 first part, 1.1 first part of a multipart inside of 1. */
    public String getPartName() { return mMimePart.getPartName(); }

    /** @return name attribute from the Content-Type param list */
    public String getName() { return mMimePart.getName(); }

    /** @return MIME Content-Type */
    public String getContentType() { return mMimePart.getContentType(); }

    /** @return MIME Content-Disposition */
    public String getContentDispostion() { return mMimePart.getContentDispostion(); }

    /** @return filename attribute from the Content-Disposition param list */
    public String getFileName() { return mMimePart.getFileName(); }

    /** @return MIME Content-ID (for display of embedded images) */
    public String getContentId() { return mMimePart.getContentId(); }

    /** @return MIME/Microsoft Content-Location (for display of embedded images) */
    public String getContentLocation() { return mMimePart.getContentLocation(); }

    /** @return MIME Content-Description.  Note cont-desc is not currently used in the code. */
    public String getContentDescription() { return mMimePart.getContentDescription(); }

    /** @return content of the part, if requested */
    public String getContent() { return mMimePart.getContent(); }

    /** @return set to 1, if this part is considered to be the "body" of the message for display purposes */
    public boolean isBody() { return mMimePart.isBody(); }

    /** @return get child parts */
    public List<ZMimePart> getChildren() { return mMimePart.getChildren(); }

    public long getSize() { return mMimePart.getSize(); }

    public String getTextContentAsHtml() {
        return BeanUtils.textToHtml(getContent());
    }

    public String getDisplayName() {
        return getName() != null ? getName() : getFileName();
    }

    public String getDisplaySize() {
        return BeanUtils.displaySize(null, getSize());
    }

    public boolean getIsImage() {
        return getContentType().toLowerCase().startsWith(CT_IMG);
    }

    public boolean getIsAudio() {
        return getContentType().toLowerCase().startsWith(CT_AUDIO);
    }

    public boolean getIsVideo() {
        return getContentType().toLowerCase().startsWith(CT_VIDEO);
    }

    public boolean getIsOctectStream() {
        return getContentType().equalsIgnoreCase(CT_APP_OCTET_STREAM);
    }

    public boolean getIsViewAsHtmlTarget() {
        return getContentType().toLowerCase().startsWith(CT_APP);
    }

    public boolean getIsMssage() {
        return getContentType().equalsIgnoreCase(CT_MSG_RFC822);
    }

    public boolean getIsMultipart() {
        return getContentType().toLowerCase().startsWith(CT_MULTI);
    }

    public boolean getIsApp() {
        return getContentType().toLowerCase().startsWith(CT_APP);   
    }

    public boolean getIsTextPlain() {
        return getContentType().equalsIgnoreCase(CT_TEXT_PLAIN);
    }

    public boolean getIsTextHtml() {
        return getContentType().equalsIgnoreCase(CT_TEXT_HTML);
    }

    public String getImage() {
        String ct = getContentType();
        String image = sTypeToImage.get(ct);
        if (image == null) {
            int index = ct.indexOf('/');
            if (index != -1) {
                ct = ct.substring(0, index);
                image = sTypeToImage.get(ct);
            }
        }
        if (image == null) image = sTypeToImage.get(CT_APP_OCTET_STREAM);
        return image;
    }

    public String toString() {
        return mMimePart.toString();
    }
}
