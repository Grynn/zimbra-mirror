package com.zimbra.bp;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.util.ZimbraLog;
import com.zimbra.cs.account.Account;
import com.zimbra.cs.account.DataSource;
import com.zimbra.cs.account.Provisioning;
import com.zimbra.cs.datasource.DataSourceManager;
import com.zimbra.cs.datasource.ImportStatus;

public class BulkIMAPImportTaskManager {
    /*
     * admin ID is a key to a queue (List) of import tasks. Each import task
     * consists of an account ID and a Resource ID.
     */
    private static HashMap<String, Queue<HashMap<taskKeys, String>>> importQueues = new HashMap<String, Queue<HashMap<taskKeys, String>>>();
    private static HashMap<String, Queue<HashMap<taskKeys, String>>> runningQueues = new HashMap<String, Queue<HashMap<taskKeys, String>>>();
    private static HashMap<String, Queue<HashMap<taskKeys, String>>> finishedQueues = new HashMap<String, Queue<HashMap<taskKeys, String>>>();

    private static int MAX_THREADS = 10;

    public static enum taskKeys {
        accountID, dataSourceID
    }

    public static HashMap<String, Queue<HashMap<taskKeys, String>>> getImportQueues() {
        return importQueues;
    }
    
    public static Queue<HashMap<taskKeys, String>> getQueue(String adminID) {
        synchronized (importQueues) {
            if (importQueues.containsKey(adminID)) {
                return importQueues.get(adminID);
            } else {
                Queue<HashMap<taskKeys, String>> lst = new LinkedList<HashMap<taskKeys, String>>();
                importQueues.put(adminID, lst);
                return lst;
            }
        }
    }

    public static Queue<HashMap<taskKeys, String>> getRunningQueue(String adminID) {
        synchronized (runningQueues) {
            if (runningQueues.containsKey(adminID)) {
                return runningQueues.get(adminID);
            } else {
                Queue<HashMap<taskKeys, String>> lst = new LinkedList<HashMap<taskKeys, String>>();
                runningQueues.put(adminID, lst);
                return lst;
            }
        }
    }
       
    public static Queue<HashMap<taskKeys, String>> getFinishedQueue(String adminID) {
        synchronized (finishedQueues) {
            if (finishedQueues.containsKey(adminID)) {
                return finishedQueues.get(adminID);
            } else {
                Queue<HashMap<taskKeys, String>> lst = new LinkedList<HashMap<taskKeys, String>>();
                finishedQueues.put(adminID, lst);
                return lst;
            }
        }
    }    
    
    public static void purgeQueue(String adminID) throws BulkProvisionException, ServiceException {
        synchronized (importQueues) {
            if (!importQueues.containsKey(adminID)) {
                throw BulkProvisionException.EMPTY_IMPORT_QUEUE();
            }
            cleanTaskQueue(importQueues.get(adminID));
            importQueues.remove(adminID);            
        }   
        synchronized (finishedQueues) {
            if (importQueues.containsKey(adminID)) {
                cleanTaskQueue(finishedQueues.get(adminID));
                finishedQueues.remove(adminID);
            }
        }        
    }
    
    /**
     * TODO: abort running imports. Currently if we delete a task from the queue active data import continues to run
     * @param taskQueue
     * @throws ServiceException
     */
    private static void cleanTaskQueue ( Queue<HashMap<taskKeys, String>> taskQueue) throws ServiceException {
        synchronized(taskQueue) {
            if(!taskQueue.isEmpty()) {
                for(HashMap<taskKeys, String> task : taskQueue) {
                    String accountID = task.get(taskKeys.accountID);
                    String dataSourceID = task.get(taskKeys.dataSourceID);
                    if (accountID == null) {
                        ZimbraLog.extensions.error("Error in IMAP import task", BulkProvisionException.EMPTY_ACCOUNT_ID());
                        continue;
                    }
                    if (dataSourceID == null) {
                        ZimbraLog.extensions.error("Error in IMAP import task", BulkProvisionException.EMPTY_DATASOURCE_ID());
                        continue;
                    }   
                    Account acct = Provisioning.getInstance().getAccountById(accountID);
                    DataSourceManager.deleteManaged(accountID, dataSourceID);
                    Provisioning.getInstance().deleteDataSource(acct, dataSourceID);
                }
            }
        } 
    }
    
    public static void startImport(String adminID) throws BulkProvisionException {
        Queue<HashMap<taskKeys, String>> queue = null;
        synchronized (importQueues) {
            if (!importQueues.containsKey(adminID)) {
                throw BulkProvisionException.EMPTY_IMPORT_QUEUE();
            }
            queue = importQueues.get(adminID);
        }
        if (queue.isEmpty()) {
            throw BulkProvisionException.EMPTY_IMPORT_QUEUE();
        }
        int numThreads = queue.size() > MAX_THREADS ? MAX_THREADS : queue.size();
        for (int i = 0; i < numThreads; i++) {
            SingleIMAPIMportThread thread = new SingleIMAPIMportThread(adminID);
            thread.start();
        }
    }

    static class SingleIMAPIMportThread extends Thread {
        private String queueKey;

        public SingleIMAPIMportThread(String adminID) {
            queueKey = adminID;
        }

        public void run() {
            Queue<HashMap<taskKeys, String>> lst = null;
            synchronized (importQueues) {
                if (importQueues.containsKey(queueKey)) {
                    lst = importQueues.get(queueKey);
                }
            }
            
            Queue<HashMap<taskKeys, String>> finishedList = getFinishedQueue(queueKey);
            if (lst == null) {
                return;
            }
            while (true) {
                HashMap<taskKeys, String> task = null;

                synchronized (lst) {
                    if(lst.isEmpty()) {
                        return;
                    }
                    task = lst.remove();
                }
                if (task == null) {
                    return;
                }
                String accountID = task.get(taskKeys.accountID);
                String dataSourceID = task.get(taskKeys.dataSourceID);
                if (accountID == null) {
                    ZimbraLog.extensions.error("Error in IMAP import task", BulkProvisionException.EMPTY_ACCOUNT_ID());
                    return;
                }
                if (dataSourceID == null) {
                    ZimbraLog.extensions.error("Error in IMAP import task", BulkProvisionException.EMPTY_DATASOURCE_ID());
                    return;
                }
                try {
                    Account acct = Provisioning.getInstance().getAccountById(accountID);
                    DataSource importDS = acct.getDataSourceById(dataSourceID);
                    ImportStatus importStatus = DataSourceManager.getImportStatus(acct, importDS);
                    synchronized (importStatus) {
                        if (importStatus.isRunning()) {
                            ZimbraLog.extensions.error("Tried to import the same account twice");
                            return;
                        }
                    }
                    DataSourceManager.importData(importDS, true);
                    HashMap<taskKeys, String> finishedTask = new HashMap<taskKeys, String>();
                    finishedTask.put(taskKeys.accountID, accountID);
                    finishedTask.put(taskKeys.dataSourceID,dataSourceID);
                    synchronized(finishedList) {
                        finishedList.add(finishedTask);
                    }
                } catch (ServiceException e) {
                    ZimbraLog.extensions.error("Error in IMAP import task", e);
                    return;
                }
            }
        }
    }
}
