/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2008, 2009, 2010 Zimbra, Inc.
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
package com.zimbra.bp;

import com.zimbra.common.service.ServiceException;

/**
 * Created by IntelliJ IDEA.
 * User: ccao
 * Date: Oct 8, 2008
 * Time: 4:03:04 PM
 * To change this template use File | Settings | File Templates.
 */
public class BulkProvisionException extends ServiceException {
    public static final String BP_TOO_MANY_ACCOUNTS = "bulkprovision.BP_TOO_MANY_ACCOUNTS";
    public static final String BP_TOO_MANY_THREADS = "bulkprovision.BP_TOO_MANY_THREADS";
    public static final String BP_IMPORT_THREAD_NOT_INITIALIZED = "bulkprovision.BP_IMPORT_THREAD_NOT_INITIALIZED";
    public static final String BP_NO_ACCOUNTS_TO_IMPORT = "bulkprovision.BP_NO_ACCOUNTS_TO_IMPORT";
    public static final String BP_IMPORT_ALREADY_RUNNING = "bulkprovision.BP_IMPORT_ALREADY_RUNNING";
    public static final String BP_IMPORT_TOO_MANY_FAILURES = "bulkprovision.BP_IMPORT_TOO_MANY_FAILURES";
    public static final String BP_INVALID_SEARCH_FILTER = "bulkprovision.BP_INVALID_SEARCH_FILTER";
    public static final String BP_NAMING_EXCEPTION = "bulkprovision.BP_NAMING_EXCEPTION";
    public static final String EMPTY_ACCOUNT_ID = "bulkprovision.EMPTY_ACCOUNT_ID";
    public static final String EMPTY_DATASOURCE_ID = "bulkprovision.EMPTY_DATASOURCE_ID";
    public static final String IMPORT_QUEUE_NOT_INITIALIZED = "bulkprovision.IMPORT_QUEUE_NOT_INITIALIZED";
    public static final String EMPTY_IMPORT_QUEUE = "bulkprovision.EMPTY_IMPORT_QUEUE";

    private BulkProvisionException(String message, String code, boolean isReceiversFault) {
        super(message, code, isReceiversFault);
    }

    private BulkProvisionException(String message, String code, boolean isReceiversFault, Throwable cause) {
        super(message, code, isReceiversFault, cause);
    }
    
    public static BulkProvisionException BP_TOO_MANY_ACCOUNTS (String desc) {
        return new BulkProvisionException("too many accounts: " + desc, BP_TOO_MANY_ACCOUNTS, SENDERS_FAULT);
    }
    
    public static BulkProvisionException BP_TOO_MANY_THREADS (int numThreads) {
        return new BulkProvisionException("Reached the maximum number of simultaneous provisioning threads: " + Integer.toString(numThreads), BP_TOO_MANY_THREADS, SENDERS_FAULT);
    }    
    
    public static BulkProvisionException BP_IMPORT_THREAD_NOT_INITIALIZED () {
        return new BulkProvisionException("Bulk provisioning thread was not properly initialized", BP_IMPORT_THREAD_NOT_INITIALIZED, SENDERS_FAULT);
    }    
    
    public static BulkProvisionException BP_NO_ACCOUNTS_TO_IMPORT () {
        return new BulkProvisionException("Did not find any accounts to import", BP_NO_ACCOUNTS_TO_IMPORT, SENDERS_FAULT);
    }
    
    public static BulkProvisionException BP_IMPORT_ALREADY_RUNNING () {
        return new BulkProvisionException("A bulk provisioning task is already running", BP_IMPORT_ALREADY_RUNNING, SENDERS_FAULT);
    }
    
    public static BulkProvisionException BP_IMPORT_TOO_MANY_FAILURES (int failNum) {
        return new BulkProvisionException(String.format("Bulk provisioning task encountered too many errors. Failed to import at least %d accounts",failNum), BP_IMPORT_TOO_MANY_FAILURES, SENDERS_FAULT);
    }    
    
    public static BulkProvisionException BP_INVALID_SEARCH_FILTER (Throwable cause) {
        return new BulkProvisionException(cause.getMessage(), BP_INVALID_SEARCH_FILTER, SENDERS_FAULT, cause);
    }
    
    public static BulkProvisionException BP_NAMING_EXCEPTION (Throwable cause) {
        return new BulkProvisionException(cause.getMessage(), BP_NAMING_EXCEPTION, SENDERS_FAULT, cause);
    }
    
    public static BulkProvisionException EMPTY_ACCOUNT_ID () {
        return new BulkProvisionException("Empty account ID in the task", EMPTY_ACCOUNT_ID, SENDERS_FAULT);
    }    
    public static BulkProvisionException EMPTY_DATASOURCE_ID () {
        return new BulkProvisionException("Empty datasource ID in the task", EMPTY_DATASOURCE_ID, SENDERS_FAULT);
    }        
    
    public static BulkProvisionException IMPORT_QUEUE_NOT_INITIALIZED () {
        return new BulkProvisionException("Import queue is not initialized", IMPORT_QUEUE_NOT_INITIALIZED, SENDERS_FAULT);
    }   
    
    public static BulkProvisionException EMPTY_IMPORT_QUEUE () {
        return new BulkProvisionException("Import queue is empty", EMPTY_IMPORT_QUEUE, SENDERS_FAULT);
    }       
}

