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
package com.zimbra.cs.taglib.bean;

import org.apache.commons.fileupload.DiskFileUpload;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadBase;
import org.apache.commons.fileupload.FileUploadException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspTagException;
import java.util.List;

public class ZComposeUploaderBean {

    private static final long DEFAULT_MAX_SIZE = 10 * 1024 * 1024;
    private List<FileItem> mItems;

    public ZComposeUploaderBean(HttpServletRequest req) throws JspTagException {
            DiskFileUpload upload = getUploader();
            try {
                mItems = upload.parseRequest(req);

            } catch (FileUploadBase.SizeLimitExceededException e) {
                // at least one file was over max allowed size
                throw new JspTagException("max size limit exceeded", e);
            } catch (FileUploadBase.InvalidContentTypeException e) {
                // at least one file was of a type not allowed
                throw new JspTagException("invalid content type", e);
            } catch (FileUploadException e) {
            	// parse of request failed for some other reason
                throw new JspTagException("file upload failed", e);
            }
	}

    public List<FileItem> getItems() {
        return mItems;
    }

    private static DiskFileUpload getUploader() {
        // look up the maximum file size for uploads
        // TODO: get from config        
        long maxSize = DEFAULT_MAX_SIZE;


        DiskFileUpload upload = new DiskFileUpload();
        upload.setSizeThreshold(4096);     // in-memory limit
        upload.setSizeMax(maxSize);
        upload.setRepositoryPath(getTempDirectory());
        return upload;
    }

    private static String getTempDirectory() {
    	return System.getProperty("java.io.tmpdir", "/tmp");
    }
}
