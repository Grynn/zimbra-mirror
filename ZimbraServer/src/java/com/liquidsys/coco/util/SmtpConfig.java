/*
 * Created on Dec 23, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.liquidsys.coco.util;

import com.liquidsys.coco.account.Provisioning;
import com.liquidsys.coco.account.Server;
import com.liquidsys.coco.service.ServiceException;
import com.liquidsys.coco.util.Config;

/**
 * @author schemers
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SmtpConfig {

    private int mTimeout;
    private int mPort;
    private String mHostname;
    
    public SmtpConfig() throws ServiceException {
        reload();
    }
    
    public void reload() throws ServiceException {
        Server config = Provisioning.getInstance().getLocalServer();
        mTimeout = config.getIntAttr(Provisioning.A_liquidSmtpTimeout, Config.D_SMTP_TIMEOUT);
        mPort = config.getIntAttr(Provisioning.A_liquidSmtpPort, Config.D_SMTP_PORT);
        mHostname = config.getAttr(Provisioning.A_liquidSmtpHostname, null);
        if (mHostname == null) {
            throw ServiceException.FAILURE("no value for "+Provisioning.A_liquidSmtpHostname, null);
        }
    }
    /**
     * @return Returns the hostname.
     */
    public String getHostname() {
        return mHostname;
    }
    /**
     * @return Returns the port.
     */
    public int getPort() {
        return mPort;
    }

    /**
     * @return Returns the timeout in seconds.
     */
    public int getTimeout() {
        return mTimeout;
    }
    
    /**
     * @return Returns the timeout in milliseconds.
     */
    public int getTimeoutMS() {
        return mTimeout*1000;
    }
}
