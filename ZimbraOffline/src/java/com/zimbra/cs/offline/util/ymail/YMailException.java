package com.zimbra.cs.offline.util.ymail;

import javax.xml.ws.soap.SOAPFaultException;
import java.io.IOException;

public class YMailException extends IOException {
    public YMailException(String msg, Throwable cause) {
        super(msg);
        initCause(cause);
    }

    public YMailException(String msg) {
        super(msg);
    }
}
