/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite, Network Edition.
 * Copyright (C) 2011 Zimbra, Inc.  All Rights Reserved.
 * ***** END LICENSE BLOCK *****
 */
package com.zimbra.clientuploader;

import com.zimbra.common.account.Key;
import com.zimbra.common.service.ServiceException;
import com.zimbra.common.util.StringUtil;
import com.zimbra.cs.account.Account;
import com.zimbra.cs.account.AuthToken;

import com.zimbra.cs.account.Provisioning;
import com.zimbra.cs.account.accesscontrol.RightCommand;
import com.zimbra.cs.extension.ExtensionHttpHandler;

import com.zimbra.cs.servlet.ZimbraServlet;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * A handler to deal with uploading client software request.
 *
 * @author Dongwei Feng
 * @since 2012.3.14
 */
public class ClientUploadHandler extends ExtensionHttpHandler {
    public static final String HANDLER_PATH_NAME = "upload";
    private static final String TARGET_TYPE = "global";
    private static final String UPLOAD_PERMISSION = "uploadClientSoftware";

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String reqId = req.getParameter("requestId");
        ZClientUploadManager man = new ZClientUploadManager();
        try {
            AuthToken authToken = ZimbraServlet.getAdminAuthTokenFromCookie(req, resp);
            if (!authenticate(authToken, resp)){
                return;
            }
            checkRight(authToken);
            man.uploadClient(req);
            sendSuccess(resp, reqId);
        } catch (ZClientUploaderException e) {
            Log.clientUploader.error("",e);
            String msg;
            switch (e.getRespCode()) {
                case FILE_EXCEED_LIMIT:
                    msg = ZClientUploaderRespCode.FILE_EXCEED_LIMIT.getDescription();
                    break;
                case NOT_A_FILE:
                    msg = ZClientUploaderRespCode.NOT_A_FILE.getDescription();
                    break;
                case NO_PERMISSION:
                    msg = ZClientUploaderRespCode.NO_PERMISSION.getDescription();
                    break;
                case MISSING_LIB_PATH:
                case UPDATE_LINK_FAILED:
                    msg = ZClientUploaderRespCode.UPDATE_LINK_FAILED.getDescription();
                    break;
                default:
                    msg = ZClientUploaderRespCode.FAILED.getDescription();
            }
            sendError(resp, e.getRespCode().getCode(), reqId, msg);
        } catch (Exception e) {
            Log.clientUploader.error("Unexpected error", e);
            sendError(resp,ZClientUploaderRespCode.FAILED.getCode(), reqId,
                    ZClientUploaderRespCode.FAILED.getDescription());
        }
    }

    private boolean authenticate(AuthToken authToken, HttpServletResponse resp) throws IOException {
        if (authToken == null) {
            Log.clientUploader.warn("Auth failed");
            sendError(resp, HttpServletResponse.SC_FORBIDDEN, HttpServletResponse.SC_FORBIDDEN, "Auth failed");
            return false;
        }
        return true;
    }

    private void checkRight(AuthToken authToken) throws ZClientUploaderException {
        if (authToken.isAdmin()) {
            return;
        }

        if (authToken.isDomainAdmin() || authToken.isDelegatedAdmin()) {
            try {
                RightCommand.EffectiveRights rights = Provisioning.getInstance().getEffectiveRights(TARGET_TYPE, null, null,
                        Key.GranteeBy.id, authToken.getAccountId(),
                        false, false);
                List<String> preRights = rights.presetRights();
                for (String r : preRights) {
                    if (UPLOAD_PERMISSION.equalsIgnoreCase(r)) {
                        return;
                    }
                }
            } catch (Exception e) {
                Log.clientUploader.warn("Failed to check right.");
            }
        }

        throw new ZClientUploaderException(ZClientUploaderRespCode.NO_PERMISSION);
    }

    @Override
    public String getPath() {
        return super.getPath() + "/" + HANDLER_PATH_NAME;
    }

    private void sendError(HttpServletResponse resp, int sc, long respCode, String msg) throws IOException {
        Log.clientUploader.error("Failed to process request: " + msg);
        resp.sendError(sc, this.getResponseBody(respCode, null, msg));
    }

    private void sendError(HttpServletResponse resp, long respCode, String requestId, String msg) throws IOException {
        Log.clientUploader.error("Failed to process request: " + msg);
        resp.setStatus(HttpServletResponse.SC_OK);
        resp.getWriter().write(getResponseBody(respCode, requestId, msg));
        resp.getWriter().flush();
        resp.getWriter().close();
    }

    private void sendSuccess(HttpServletResponse resp, String requestId) throws IOException {
        resp.setStatus(HttpServletResponse.SC_OK);
        resp.getWriter().write(getResponseBody(ZClientUploaderRespCode.SUCCEEDED.getCode(), requestId));
        resp.getWriter().flush();
        resp.getWriter().close();
    }

    private String getResponseBody(long statusCode, String requestId) {
        return getResponseBody(statusCode, requestId, null);
    }

    private String getResponseBody(long statusCode, String requestId, String msg) {
        StringBuilder sb = new StringBuilder();
        sb.append("<html><head></head><body onload=\"window.parent._uploadManager.loaded(")
                .append(statusCode)
                .append(",'")
                .append(requestId != null ? StringUtil.jsEncode(requestId) : "null")
                .append("'")
                .append(");\">");
        if (msg != null && !msg.isEmpty()) {
             sb.append(msg);
        }
        sb.append("</body></html>");

        return sb.toString();
    }
}
