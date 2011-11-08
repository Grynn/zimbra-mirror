/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2011 Zimbra, Inc.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.3 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 */

package com.zimbra.cs.mailbox;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.SocketTimeoutException;

import junit.framework.Assert;

import org.junit.BeforeClass;
import org.junit.Test;

import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.offline.OfflineLC;
import com.zimbra.cs.offline.util.RecoverableException;


public class SyncExceptionHandlerTest {

    @BeforeClass
    public static void init() throws Exception {
//        MockProvisioning prov = new MockProvisioning();
//        prov.createAccount("test@zimbra.com", "secret",
//                Collections.<String, Object>singletonMap(Provisioning.A_zimbraId, "0-0-0"));
//        Provisioning.setInstance(prov);
//        MailboxManager.setInstance(new MockOfflineMailboxManager());
        //TODO: eventually uncomment once we have hsqldb setup correctly for offline
    }

    @Test
    public void singleIoException() throws Exception {
        DesktopMailbox dmbox = new MockDesktopMailbox();
        SyncExceptionHandler.handleIOException(dmbox, 1, "some message", new IOException());
    }

    @Test(expected=ServiceException.class)
    public void ioExceptionCountLimit() throws Exception {
        DesktopMailbox dmbox = new MockDesktopMailbox();
        for (int i=0;i<OfflineLC.zdesktop_sync_io_exception_limit.intValue();i++) {
            SyncExceptionHandler.handleIOException(dmbox, i, "some message", new IOException());
        }
    }
    
    @Test
    public void ioExceptionCountUnderLimit() throws Exception {
        DesktopMailbox dmbox = new MockDesktopMailbox();
        for (int i=0;i<OfflineLC.zdesktop_sync_io_exception_limit.intValue()-1;i++) {
            SyncExceptionHandler.handleIOException(dmbox, i, "some message", new IOException());
        }
    }

    @Test
    public void ioExceptionCountWithPermanentFailure() throws Exception {
        DesktopMailbox dmbox = new MockDesktopMailbox();
        SyncExceptionHandler.handleIOException(dmbox, 1, "some message", new IOException());
        SyncExceptionHandler.handleIOException(dmbox, 1, "some message", new IOException());
        SyncExceptionHandler.handleIOException(dmbox, 1, "some message", new IOException());
        for (int i=0;i<OfflineLC.zdesktop_sync_io_exception_limit.intValue()-1;i++) {
            SyncExceptionHandler.handleIOException(dmbox, i, "some message", new IOException());
        }
    }


    @Test
    public void ioExceptionRateWithManyPermanentFailures() throws Exception {
        DesktopMailbox dmbox = new MockDesktopMailbox();
        for (int i=0; i<100; i++) {
            for (int j=0; j<10; j++) {
                SyncExceptionHandler.handleIOException(dmbox, i, "some message", new IOException());
            }
        }
        SyncExceptionHandler.checkIOExceptionRate(dmbox, 1);
    }

    @Test
    public void ioExceptionRateWithManyPermanentFailuresOneBad() throws Exception {
        DesktopMailbox dmbox = new MockDesktopMailbox();
        for (int i=0; i<100; i++) {
            for (int j=0; j<10; j++) {
                SyncExceptionHandler.handleIOException(dmbox, i, "some message", new IOException());
            }
        }
        SyncExceptionHandler.handleIOException(dmbox, 99999, "some message", new IOException());
        try {
            SyncExceptionHandler.checkIOExceptionRate(dmbox, 1002);
            Assert.fail("Did not encounter expected exception");
        } catch (ServiceException se) {
            Assert.assertTrue(se.getCause() instanceof RecoverableException);
        }
    }
    
    @Test
    public void ioExceptionCountWithRecurringFailure() throws Exception {
        DesktopMailbox dmbox = new MockDesktopMailbox();
        SyncExceptionHandler.handleIOException(dmbox, 1, "some message", new IOException());
        for (int i=0;i<OfflineLC.zdesktop_sync_io_exception_limit.intValue()-2;i++) {
            SyncExceptionHandler.handleIOException(dmbox, i, "some message", new IOException());
        }
        try {
            SyncExceptionHandler.handleIOException(dmbox, 999, "some message", new IOException());
            Assert.fail("Did not encounter expected exception");
        } catch (ServiceException se) {
            Assert.assertTrue(se.getCause() instanceof RecoverableException);
        }
    }
    
    @Test
    public void ioExceptionAllFailure() throws Exception {
        DesktopMailbox dmbox = new MockDesktopMailbox();
        SyncExceptionHandler.handleIOException(dmbox, 1, "some message", new IOException());
        SyncExceptionHandler.handleIOException(dmbox, 2, "some message", new IOException());
        SyncExceptionHandler.handleIOException(dmbox, 3, "some message", new IOException());
        try {
            //100% failure rate
            SyncExceptionHandler.checkIOExceptionRate(dmbox, 3);
            Assert.fail("Did not encounter expected exception");
        } catch (ServiceException se) {
            Assert.assertTrue(se.getCause() instanceof RecoverableException);
        }
    }

    @Test
    public void ioExceptionHalfFailureRate() throws Exception {
        DesktopMailbox dmbox = new MockDesktopMailbox();
        SyncExceptionHandler.handleIOException(dmbox, 1, "some message", new IOException());
        SyncExceptionHandler.handleIOException(dmbox, 2, "some message", new IOException());
        SyncExceptionHandler.handleIOException(dmbox, 3, "some message", new IOException());
        SyncExceptionHandler.handleIOException(dmbox, 4, "some message", new IOException());
        try {
            //50% failure rate
            SyncExceptionHandler.checkIOExceptionRate(dmbox, 4);
            Assert.fail("Did not encounter expected exception");
        } catch (ServiceException se) {
            Assert.assertTrue(se.getCause() instanceof RecoverableException);
        }
    }

    @Test
    public void ioExceptionStuck() throws Exception {
        //id 1 is 'bad'. after 3rd failure it is silently ignored
        DesktopMailbox dmbox = new MockDesktopMailbox();
        
        for (int i=0; i < OfflineLC.zdesktop_sync_item_io_exception_limit.intValue()-1; i++) {
            SyncExceptionHandler.handleIOException(dmbox, 1, "some message", new IOException());
            try {
                SyncExceptionHandler.checkIOExceptionRate(dmbox, 300);
                Assert.fail("Did not encounter expected exception");
            } catch (ServiceException se) {
                Assert.assertTrue(se.getCause() instanceof RecoverableException);
            }
        }

        //this should hit limit and be silently skipped over
        SyncExceptionHandler.handleIOException(dmbox, 1, "some message", new IOException());
        try {
            SyncExceptionHandler.checkIOExceptionRate(dmbox, 300);
        } catch (ServiceException se) {
            Assert.fail("Encountered unexpected exception, should have silently failed after retries");
        }

    }

    @Test
    public void ioExceptionOneStuckOneNew() throws Exception {
        //id 1 is 'bad'. after 3rd failure it is silently ignored
        DesktopMailbox dmbox = new MockDesktopMailbox();
        
        for (int i=0; i < OfflineLC.zdesktop_sync_item_io_exception_limit.intValue()-1; i++) {
            SyncExceptionHandler.handleIOException(dmbox, 1, "some message", new IOException());
            try {
                SyncExceptionHandler.checkIOExceptionRate(dmbox, 300);
                Assert.fail("Did not encounter expected exception");
            } catch (ServiceException se) {
                Assert.assertTrue(se.getCause() instanceof RecoverableException);
            }
        }

        SyncExceptionHandler.handleIOException(dmbox, 1, "some message", new IOException());
        SyncExceptionHandler.handleIOException(dmbox, 2, "some message", new IOException());
        try {
            SyncExceptionHandler.checkIOExceptionRate(dmbox, 300);
            Assert.fail("Did not encounter expected exception");
        } catch (ServiceException se) {
            Assert.assertTrue(se.getCause() instanceof RecoverableException);
        }

    }

    private static class FakeException extends Exception {
        public FakeException(Throwable cause) {
            super(cause);
        }
    }

    @Test
    public void testIsCausedBy() {
        FakeException fakeException = new FakeException(new FileNotFoundException());
        Assert.assertEquals(true, SyncExceptionHandler.isCausedBy(fakeException, FileNotFoundException.class));
        Assert.assertEquals(false, SyncExceptionHandler.isCausedBy(fakeException, IOException.class));

        fakeException = new FakeException(new IOException());
        Assert.assertEquals(true, SyncExceptionHandler.isCausedBy(fakeException, IOException.class));
        Assert.assertEquals(false, SyncExceptionHandler.isCausedBy(fakeException, Exception.class));
        Assert.assertEquals(true, SyncExceptionHandler.isCausedBy(fakeException, FileNotFoundException.class));
        Assert.assertEquals(true, SyncExceptionHandler.isCausedBy(fakeException, SocketTimeoutException.class));
    }
}
