/*
 * Created on 2005. 6. 29.
 */
package com.liquidsys.coco.redolog;

import com.liquidsys.coco.account.Provisioning;
import com.liquidsys.coco.account.Server;
import com.liquidsys.coco.service.ServiceException;
import com.liquidsys.coco.util.Liquid;
import com.liquidsys.coco.util.LiquidLog;

/**
 * @author jhahm
 */
public abstract class RedoLogProvider {

    private static RedoLogProvider theInstance = null;

    static {
    	try {
			theInstance = loadProvider();
		} catch (ServiceException e) {
            Liquid.halt("Unable to initialize redolog provider", e);
		}
    }

    public static RedoLogProvider getInstance() {
    	return theInstance;
    }

    private static RedoLogProvider loadProvider()
    throws ServiceException {
        RedoLogProvider provider = null;
        Class providerClass = null;
        Server config = Provisioning.getInstance().getLocalServer();
        String className = config.getAttr(Provisioning.A_liquidRedoLogProvider);
        try {
            if (className != null) {
                providerClass = Class.forName(className);
            } else {
                providerClass = DefaultRedoLogProvider.class;
                LiquidLog.misc.info("Redolog provider name not specified.  Using default " +
                                    providerClass.getName());
            }
            provider = (RedoLogProvider) providerClass.newInstance();
        } catch (Throwable e) {
        	throw ServiceException.FAILURE("Unable to load redolog provider " + className, e);
        }
        return provider;
    }

    protected RedoLogManager mRedoLogManager;
    
    public abstract boolean isMaster();
    public abstract boolean isSlave();
    public abstract void startup() throws ServiceException;
    public abstract void shutdown() throws ServiceException;

    public abstract void initRedoLogManager();
    
    public RedoLogManager getRedoLogManager() {
        return mRedoLogManager;
    }
}
