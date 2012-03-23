/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite, Network Edition.
 * Copyright (C) 2011 Zimbra, Inc.  All Rights Reserved.
 * ***** END LICENSE BLOCK *****
 */
package com.zimbra.clientuploader;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadBase;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.Iterator;
import java.util.List;

/**
 * <code>ZClientUploader</code> saves the uploaded files according to configurations.
 *
 * @author Dongwei Feng
 * @since 2012.3.14
 */
public class ZClientUploader {
    private static final String ENCODING = "utf-8";
    private static final int BUFFER_SIZE = 32 * 1024;
    private static final String TEMP_DIR_SUFFIX = "client_uploader_tmp/";

    //Repository directory
    private static String CLIENT_UPLOAD_REPO_DIR;
    //Directory for temp files
    private static String ClIENT_UPLOAD_REPO_TMP_DIR;
    static {
        CLIENT_UPLOAD_REPO_DIR = ClientUploaderLC.client_repository_location.value();
        if (!CLIENT_UPLOAD_REPO_DIR.endsWith(File.separator)) {
            CLIENT_UPLOAD_REPO_DIR += File.separator;
        }
        ClIENT_UPLOAD_REPO_TMP_DIR = CLIENT_UPLOAD_REPO_DIR + TEMP_DIR_SUFFIX;
    }

    /**
     * Save the uploaded file from request
     * @param req
     * @throws ZClientUploaderException if fails to save
     */
    public void upload(HttpServletRequest req) throws ZClientUploaderException {
        if (!ServletFileUpload.isMultipartContent(req)) {
            throw new ZClientUploaderException(ZClientUploaderRespCode.NOT_A_FILE);
        }

        DiskFileItemFactory factory = new DiskFileItemFactory(BUFFER_SIZE,
                getClientRepoTmp());

        ServletFileUpload upload = new ServletFileUpload(factory);
        upload.setSizeMax(ClientUploaderLC.client_software_max_size.longValue());
        upload.setHeaderEncoding(ENCODING);

        File parent = getClientRepo();
        try {
            List<FileItem> items = upload.parseRequest(req);
            if (items == null || items.size() <= 0) {
                throw new ZClientUploaderException(ZClientUploaderRespCode.NOT_A_FILE);
            }

            Iterator iter = items.iterator();
            int count = 0;
            while (iter.hasNext()) {
                FileItem item = (FileItem) iter.next();

                if (!item.isFormField()) {
                    String fileName = item.getName();
                    if (fileName != null && !fileName.isEmpty()) {
                        item.write(new File(parent, fileName));
                        count ++;
                    }
                }
            }

            if (count == 0) {
                throw new ZClientUploaderException(ZClientUploaderRespCode.NOT_A_FILE);
            }
        } catch (ZClientUploaderException e) {
            throw e;
        } catch (FileUploadBase.SizeLimitExceededException e) {
            throw new ZClientUploaderException(ZClientUploaderRespCode.FILE_EXCEED_LIMIT, e);
        } catch (FileUploadException e) {
            throw new ZClientUploaderException(ZClientUploaderRespCode.PARSE_REQUEST_ERROR, e);
        } catch (Exception e) {
            throw new ZClientUploaderException(ZClientUploaderRespCode.SAVE_ERROR, e);
        }
    }

    /*
     * Get the directory for repository
     * @return
     * @throws ZClientUploaderException if the directory does not exist, cannot be created,
     *  or no write permission on it.
     */
    private static File getClientRepo() throws ZClientUploaderException {
        return getDirectory(CLIENT_UPLOAD_REPO_DIR);
    }


    /*
     * Get the directory for temp files
     * @return a File object
     * @throws ZClientUploaderException if the directory does not exist, cannot be created,
     *  or no write permission on it.
     */
    private static File getClientRepoTmp() throws ZClientUploaderException{
        return getDirectory(ClIENT_UPLOAD_REPO_TMP_DIR);
    }

    private static File getDirectory(String name) throws ZClientUploaderException{
        File file;
        try {
            file = new File(name);
            if (!file.exists()) {
                file.mkdirs();
            }
            if (!file.isDirectory()) {
                throw new ZClientUploaderException(ZClientUploaderRespCode.REPO_INVALID,"Not a directory");
            }
            if (!file.canWrite()) {
                throw new ZClientUploaderException(ZClientUploaderRespCode.REPO_NO_WRITE,"No write permission on repository directory");
            }
        } catch (ZClientUploaderException e) {
            throw e;
        } catch (Exception e) {
            throw new ZClientUploaderException(ZClientUploaderRespCode.REPO_INVALID,
                    "Failed to create directory for client repository", e);
        }


        return file;
    }

}
