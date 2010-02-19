package com.zimbra.examples.extns.httphandler;

import com.zimbra.cs.extension.ExtensionHttpHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * This HTTP handler handles GET requests at "/service/extension/dummyHandler" custom URI.
 *
 * @author vmahajan
 */
public class DummyHttpHandler extends ExtensionHttpHandler {

    /**
     * The path under which the handler is registered for an extension.
     *
     * @return
     */
    @Override
    public String getPath() {
        return "/dummyHandler";
    }

    /**
     * Processes HTTP GET requests.
     *
     * @param req
     * @param resp
     * @throws java.io.IOException
     * @throws javax.servlet.ServletException
     */
    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        resp.getOutputStream().print("This is a dummy http handler!");
    }
}
