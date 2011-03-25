package com.zimbra.cs.convert;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.zimbra.cs.extension.ExtensionHttpHandler;

/**
 * skeleton converter for zd.
 *
 */
public class OfflineConvertHandler extends ExtensionHttpHandler {

    @Override
    public String getPath() {
        return "/convertd";
    }

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ServletException {
        throw new ConversionUnsupportedException("offline convert handler is unavailable");
    }

}
