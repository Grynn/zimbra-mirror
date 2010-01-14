package com.zimbra.cs.mailbox;

import java.io.IOException;
import java.io.InputStream;

import com.zimbra.common.service.ServiceException;

public interface ExchangeHelper {
    public void doSendMail(InputStream in, long size, boolean saveToSent) throws ServiceException, IOException;
}
