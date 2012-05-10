package com.zimbra.examples.extns.storemanager;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.util.FileUtil;
import com.zimbra.common.util.ZimbraLog;
import com.zimbra.cs.extension.ExtensionException;
import com.zimbra.cs.extension.ZimbraExtension;
import com.zimbra.cs.mailbox.Mailbox;
import com.zimbra.cs.store.external.ExternalStoreManager;

public class ExampleStoreManager extends ExternalStoreManager implements ZimbraExtension {

    String directory = "/tmp/examplestore/blobs";

    @Override
    public void startup() throws IOException, ServiceException {
        super.startup();
        ZimbraLog.store.info("Using ExampleStoreManager. If you are seeing this in production you have done something WRONG!");
        FileUtil.mkdirs(new File(directory));
    }

    @Override
    public void shutdown() {
        super.shutdown();
    }

    private String dirName(Mailbox mbox) {
        return directory + "/" + mbox.getAccountId();
    }

    private File getNewFile(Mailbox mbox) throws IOException {
        String baseName = dirName(mbox);
        FileUtil.mkdirs(new File(baseName));
        baseName += "/zimbrablob";
        String name = baseName;
        synchronized (this) {
            int count = 1;
            File file = new File(name+".msg");
            while (file.exists()) {
                name = baseName+"_"+count++;
                file = new File(name+".msg");
            }
            if (file.createNewFile()) {
                ZimbraLog.store.debug("writing to new file %s",file.getName());
                return file;
            } else {
                throw new IOException("unable to create new file");
            }
        }
    }

    @Override
    public String writeStreamToStore(InputStream in, long actualSize, Mailbox mbox) throws IOException {
        File destFile = getNewFile(mbox);
        FileUtil.copy(in, false, destFile);
        return destFile.getCanonicalPath();
    }

    @Override
    public InputStream readStreamFromStore(String locator, Mailbox mbox) throws IOException {
        return new FileInputStream(locator);
    }

    @Override
    public boolean deleteFromStore(String locator, Mailbox mbox) throws IOException {
        File deleteFile = new File(locator);
        return deleteFile.delete();
    }

    @Override
    public boolean supports(StoreFeature feature) {
        if (feature == StoreFeature.CENTRALIZED) {
            return false;
        } else {
            return super.supports(feature);
        }
    }

    
    //ZimbraExtension stub so class can be loaded by ExtensionUtil.
    @Override
    public String getName() {
        return "StoreManagerExtension";
    }

    @Override
    public void init() throws ExtensionException, ServiceException {
    }

    @Override
    public void destroy() {
    }
}
