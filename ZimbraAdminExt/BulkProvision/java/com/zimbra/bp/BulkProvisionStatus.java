package com.zimbra.bp;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.util.ZimbraLog;
import com.zimbra.cs.fb.FreeBusy;

import java.util.Hashtable;
import java.util.Enumeration;
import java.lang.reflect.Method;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import au.com.bytecode.opencsv.CSVWriter;

/**
 * This object is used to keep the provision status for download
 *
 * All the provision status are kept in Memory as a Hashtable
 *
 * Key: attachment Id
 * Value: another hashtable with accountName as the key and a String array as the value
 *        each String array has four values in order, (one entry of the CSV file),
 *          accountName, dispplay name, password, provision status
 *
 *
 *
 * User: ccao
 * Date: Sep 30, 2008
 * Time: 4:45:59 PM
 * To change this template use File | Settings | File Templates.
 */
public class BulkProvisionStatus {
    private static Hashtable <String, Hashtable> BP_STATUS_HASH = new Hashtable <String, Hashtable>() ;

    public static final int INDEX_ACCT_NAME = 0 ;
    public static final int INDEX_DISPLAY_NAME = 1 ;
    public static final int INDEX_PASSWD = 2 ;
    public static final int INDEX_STATUS = 3 ;
    
    //add one complete entry, if there is one in the hash , the value will be completely replaced with the new hash
    public static void addBpStatus (String aid, Hashtable<String, String []> allAccountProvisionStatus) 
    {
        ZimbraLog.extensions.debug ("Add the bulk provision status to hash with key " + aid) ;
        BP_STATUS_HASH.put(aid, allAccountProvisionStatus) ;
    }

    //add status only to the entry with accountName as the key.
    //Exception will be throw if no accountName key is found
    public static void addBpStatus (String aid, String accountName, String status) throws ServiceException{
        Hashtable <String, String []> bpStatus = getBpStatus(aid) ;
        String [] entry = bpStatus.get(accountName) ;
        if (entry == null || entry.length <=0) {
            String msg = "Can't find the account provision information for " + accountName ;
            throw ServiceException.FAILURE(msg, new Exception(msg)) ;
        }

        entry [INDEX_STATUS] = status ;
    }

    //get the complete status
    public static Hashtable<String, String []> getBpStatus (String aid) throws ServiceException {
        ZimbraLog.extensions.debug("Retrieving Bulk Provision Information for uploaded file: " + aid) ;
        if (BP_STATUS_HASH.get(aid) == null) {
            String msg = "Can't retrieve the bulk provision status for key " + aid ;
            throw ServiceException.FAILURE(msg,  new Exception (msg)) ;
        }
        
        return BP_STATUS_HASH.get(aid) ;
    }

    public static void writeBpStatusOutputStream (OutputStream out, String aid) throws ServiceException {
        ZimbraLog.extensions.debug("Download Bulk Provision Information for uploaded file: " + aid) ;
        try {
            //CSVWriter csv = new CSVWriter(new OutputStreamWriter (out));
            Hashtable<String, String[]> bpStatusHash = getBpStatus (aid) ;
            Enumeration<String> acctNames = bpStatusHash.keys();
            String acctName ;
            while (acctNames.hasMoreElements()) {
                acctName = acctNames.nextElement() ;
                ZimbraLog.extensions.debug("Add entry: " + acctName) ;
                String[] entry = (String []) bpStatusHash.get(acctName) ;
                StringBuffer sb = new StringBuffer();

                for (int i=0; i < entry.length; i ++) {
                   sb.append(entry[i]) ;
                   if (i == entry.length - 1) { //last one
                        sb.append("\n");                       
                   }else{
                       sb.append(",") ;
                   }
                }
//                ZimbraLog.extensions.debug("Adding entry content : " + content );

                out.write(sb.toString().getBytes()) ;
                //csv.writeNext(entry); //CSVWriter doesn't work well

            }
        }catch (Exception e) {
            ZimbraLog.extensions.error(e);
            throw ServiceException.FAILURE(e.getMessage(), e) ;
        }
    }
    /*
    public static void main (String [] args) {
        Hashtable<String, String []> ht = new Hashtable<String, String []> ()  ;
        String [] entry = new String [] {"a@test.com", "Test", "test123", "succeeded"} ;
        String [] entry1 = new String [] {"b@test.com", "TestB", "testB123", "Bsucceeded"} ;
        ht.put("a@test.com", entry) ;
        ht.put("b@test.com", entry1) ;
        addBpStatus("test_aid", ht); ;
        try {
            Class c = Class.forName("com.zimbra.bp.BulkProvisionStatus") ;
            Class [] params = new Class [1] ;
            params[0] = Class.forName("java.lang.String") ;
            Method m = c.getMethod("getBpStatus", params) ;
            String [] paramValue = new String [] {"test_aid"} ;
            
            Hashtable<String, String[]> newHt =(Hashtable<String, String[]>) m.invoke(c, paramValue) ;
            Enumeration<String> keys = newHt.keys();
            String key ;
            while (keys.hasMoreElements()) {
                key = keys.nextElement() ;
                String[] value = (String [] )newHt.get(key) ;
                String entryValue = "" ;
                for (int i=0; i < value.length; i ++) {
                    entryValue += value[i] + "," ;
                }
                System.out.println(key + ": " + entryValue) ;


            }
        }catch(Exception e) {
            e.printStackTrace();
        }
   }  */
}

