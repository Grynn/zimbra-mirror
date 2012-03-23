/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite, Network Edition.
 * Copyright (C) 2011 Zimbra, Inc.  All Rights Reserved.
 * ***** END LICENSE BLOCK *****
 */
package com.zimbra.clientuploader;

import javax.servlet.http.HttpServletRequest;
import java.io.*;

/**
 * Response code for client software uploade extension
 *
 * @author Dongwei Feng
 * @since 2012.3.18
 */
public class ZClientUploadManager {
    private static final String LIB_PATH = "java.library.path";
    private static final String COMMAND_PATH = "libexec/zmupdatedownload";

    /**
     * Contains 2 steps:
     *    1. Save the uploaded file;
     *    2. Call the script to update download index page.
     * @param req
     * @throws ZClientUploaderException
     */
    public void uploadClient(HttpServletRequest req) throws ZClientUploaderException {
        ZClientUploader uploader = new ZClientUploader();
        uploader.upload(req);
        this.updateLinks();
    }

    private void updateLinks() throws ZClientUploaderException {
        String libPath = System.getProperty(LIB_PATH);
        if (libPath == null) {
            throw new ZClientUploaderException(ZClientUploaderRespCode.MISSING_LIB_PATH);
        }

        String command = libPath.substring(0, libPath.lastIndexOf(File.separator) + 1) + COMMAND_PATH;
        ProcessBuilder pb = new ProcessBuilder(command);
        pb.redirectErrorStream(true);
        try {
            Process p = pb.start();
            int exitStatus = p.waitFor();
            if (exitStatus != 0) {
                throw new ZClientUploaderException(ZClientUploaderRespCode.UPDATE_LINK_FAILED);
            }
            logOutput(p);
        } catch (ZClientUploaderException e) {
            throw e;
        } catch (Exception e) {
            throw new ZClientUploaderException(ZClientUploaderRespCode.UPDATE_LINK_FAILED);
        }
    }

    private void logOutput(Process p) throws IOException {
        InputStream is = p.getInputStream();
        if (is != null) {
            Writer writer = new StringWriter();
            char[] buffer = new char[1024];
            try {
                Reader reader = new BufferedReader(new InputStreamReader(is,"UTF-8"));
                int n;
                while ((n = reader.read(buffer)) != -1) {
                    writer.write(buffer, 0, n);
                }
            } catch (IOException e) {
                Log.clientUploader.error("Failed to log the output", e);
            } finally {
                is.close();
            }
            Log.clientUploader.debug(writer.toString());
        }
    }
}
