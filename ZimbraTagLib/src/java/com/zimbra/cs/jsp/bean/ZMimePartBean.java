/*
 * ***** BEGIN LICENSE BLOCK *****
 * Version: MPL 1.1
 *
 * The contents of this file are subject to the Mozilla Public License
 * Version 1.1 ("License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.zimbra.com/license
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See
 * the License for the specific language governing rights and limitations
 * under the License.
 *
 * The Original Code is: Zimbra Collaboration Suite Server.
 *
 * The Initial Developer of the Original Code is Zimbra, Inc.
 * Portions created by Zimbra are Copyright (C) 2006 Zimbra, Inc.
 * All Rights Reserved.
 *
 * Contributor(s):
 *
 * ***** END LICENSE BLOCK *****
 */

package com.zimbra.cs.jsp.bean;

import com.zimbra.cs.zclient.ZMessage.ZMimePart;
import com.zimbra.cs.mime.Mime;

import java.util.List;

public class ZMimePartBean {

    private ZMimePart mMimePart;

    public ZMimePartBean(ZMimePart mimePart) { mMimePart = mimePart; }

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
        return BeanUtils.displaySize(getSize());
    }

    public boolean getIsImage() {
        return getContentType().toLowerCase().startsWith("image");
    }

    public boolean getIsAudio() {
        return getContentType().toLowerCase().startsWith("audio");
    }

    public boolean getIsVideo() {
        return getContentType().toLowerCase().startsWith("video");
    }

    public boolean getIsOctectStream() {
        return getContentType().equalsIgnoreCase(Mime.CT_APPLICATION_OCTET_STREAM);
    }

}
