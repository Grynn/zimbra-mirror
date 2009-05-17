/*
 * ***** BEGIN LICENSE BLOCK *****
 *
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2007, 2008 Zimbra, Inc.
 *
 * The contents of this file are subject to the Yahoo! Public License
 * Version 1.0 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 *
 * ***** END LICENSE BLOCK *****
 */
package com.zimbra.tarformatter;

import com.zimbra.utils.*;
import com.zimbra.zcsprov.ZmProvGenericException;
import com.zimbra.zcsprov.ZMSoapSession;
import com.zimbra.zcsprov.HttpSession;
import com.zimbra.zcsprov.ZCSACProvision;
import com.zimbra.auth.AuthTokens;
import com.zimbra.common.ZCSProvParams;

import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.ArrayList;
import java.io.*;

class AccountsList
{
    private static ArrayList<String> AccountList;
    public static synchronized void SetAccountList( ArrayList<String> List)
    {
        AccountList = List;
    }

    public static synchronized String GetNextAccount()
    {
        String account="";
        if(AccountList.size()>0)
        {
           account= AccountList.remove(0); 
        }
        return account;
    }

    public static synchronized void AddAccount(String account)
    {
        AccountList.add(account);
    }
}

class tarMigrator implements Runnable
{

    private static final String ztozlogFile="ztozlog";
    private static final String soapuri="/service/admin/soap";
    private static final String httpuri="/service/home/";
    private static final String https="https://";
    private static final String http="http://";
    private static int ExcThreadCount;
    private static int migErrCount=0;
    private static int paccountscount=0;
    private static Logger print_menu_logger;
    private static double TotalMigTime;
    private static boolean debugmsg;
    private ZtoZImportParams tarMigparams;
    private double tarMigtime ; //in miliseconds
    private ZCSPLogger tarMig_Logger;
    private Logger tarmig_log;

    private ZMSoapSession zmsession;
    private HttpSession httpsession;
    private ZCSACProvision zcsacprov;
    private String dest_accountid;

    public tarMigrator(ZtoZImportParams migparams,ZCSPLogger tarmig_logger)
    {
        tarMigparams= migparams;
        tarMig_Logger = tarmig_logger;
        ExcThreadCount=0;
        tarMigtime=0;
        debugmsg=false;
        dest_accountid="";
    }

    public static synchronized void incr_exec_count()
    {
        ExcThreadCount++;
    }
    public static synchronized void decr_exec_count()
    {
        ExcThreadCount--;
    }

    public static synchronized int get_exec_count()
    {
        return ExcThreadCount;
    }

    public static synchronized void incr_err_count()
    {
        migErrCount++;
    }

    public static synchronized int get_err_count()
    {
        return migErrCount;
    }

    public static synchronized void incr_processed_account_count()
    {
        paccountscount++;
    }

    public static synchronized int get_processed_account_count()
    {
        return paccountscount;
    }

    public double GetMigrationTime()
    {
        return tarMigtime;
    }

    public static double GetTotalMigTime()
    {
        return TotalMigTime;
    }

    private void debug_msg(Logger logger,String debug_msg)
    {
        if(tarMigparams.debug_mig)
        {
            logger.log(Level.INFO, debug_msg);
        }
    }
    
    public boolean Init() throws ZmProvGenericException
    {
        boolean retval=false;
        tarmig_log = tarMig_Logger.get_logger(ztozlogFile);
        print_menu_logger=tarmig_log;
        tarMigparams.SourceServerURI=https+tarMigparams.SourceZCSServer+":"+tarMigparams.SrcZCSPort+soapuri;
        zmsession = new ZMSoapSession(tarMigparams.SourceServerURI,
                tarMigparams.SrcAdminUser, tarMigparams.SrcAdminPwd,
                ZMSoapSession.AUTH_TYPE_ADMIN, tarmig_log);
        retval=zmsession.check_auth();
        if(!retval)
        {
            throw new ZmProvGenericException("Source Server"+"("+tarMigparams.SourceZCSServer+")"+"Authentication Failed");
        }
        ZMSoapSession zmtrgtsession;
        tarMigparams.TrgtServerURI=https+tarMigparams.TargetZCSServer+":"+tarMigparams.TrgtZCSPort+soapuri;
        zmtrgtsession = new ZMSoapSession(tarMigparams.TrgtServerURI,
                tarMigparams.TrgtAdminUser, tarMigparams.TrgtAdminPwd,
                ZMSoapSession.AUTH_TYPE_ADMIN_DEST, tarmig_log);

        retval=zmtrgtsession.check_auth();
        if (!retval)
        {
            throw new ZmProvGenericException("Destination Server"+"("+tarMigparams.TargetZCSServer+")"+"Authentication Failed");
        }
        if(tarMigparams.debug_mig)
        {
            zmsession.enable_dump_all();
        }
        ZCSUtils.set_logger(tarmig_log);
        httpsession = new HttpSession();

        return retval;
    }

    public boolean GetTarredMailBox(String username,Logger gtmbLog)
    {
        boolean retval=false;
        if(zmsession.check_auth())
        {
            debug_msg(gtmbLog,"Auth Token: "+tarMigparams.SourceZCSServer+": "+AuthTokens.get_admin_auth_token(tarMigparams.SourceServerURI));
            httpsession.SetPostMethod(false);
            //https://10.66.118.103:7071/service/home/test1?fmt=tgz&authToken=0_84af9241c9ed5864f6ffb11c6cf..23we32
            String uri=https+tarMigparams.SourceZCSServer+":"+tarMigparams.SrcZCSPort+httpuri+
                    username+"?fmt=tgz&authToken="+AuthTokens.get_admin_auth_token(tarMigparams.SourceServerURI);
            debug_msg(gtmbLog,"Download URL:"+uri);
            InputStream istr= httpsession.Send(uri);
            if (istr!=null)
            {
                incr_exec_count();
                try
                {
                    gtmbLog.log(Level.INFO,"Starting mailbox"+" ("+username+") "+"download...");
                    retval=writeToFile(tarMigparams.WorkingDirectory+username+".tgz",istr,true,
                            gtmbLog,get_exec_count());
                    gtmbLog.log(Level.INFO,"Finished mailbox"+" ("+username+") "+"download.");
                }
                catch(Exception ex)
                {
                    gtmbLog.log(Level.SEVERE,ZCSUtils.stack2string(ex));
                    gtmbLog.log(Level.SEVERE,"Exception in mailbox download"+" ("+username+") ");
                    retval=false;
                }
                decr_exec_count();
            }
            else
            {
                gtmbLog.log(Level.SEVERE,"FATAL:Source Mail box"+"("+username+")"+" stream couldn't be found.");
            }
        }
        else
        {
            gtmbLog.log(Level.SEVERE,"Source Server"+" ("+tarMigparams.SourceZCSServer+") "+"Authentication Failed.");
        }
        return retval;
    } 

    private boolean writeToFile(String fileName, InputStream iStream,
        boolean createDir,Logger wflog,int exc_count)
        throws IOException
    {
        boolean retval =true;
        String me = "FileUtils.WriteToFile";
        if (fileName == null)
        {
            throw new IOException(me + ": filename is null");
        }
        if (iStream == null)
        {
            throw new IOException(me + ": InputStream is null");
        }

        File theFile = new File(fileName);

        // Check if a file exists.
        if (theFile.exists())
        {
            String msg =
                theFile.isDirectory() ? "directory" :
                    (! theFile.canWrite() ? "not writable" : null);
            if (msg != null)
            {
                throw new IOException(me + ": file '" + fileName + "' is " + msg);
            }
        }

        // Create directory for the file, if requested.
        if (createDir && theFile.getParentFile() != null)
        {
            theFile.getParentFile().mkdirs();
        }

        // Save InputStream to the file.
        BufferedOutputStream fOut = null;
        double biTotBytesWritten=0;
        try
        {
            fOut = new BufferedOutputStream(new FileOutputStream(theFile));
            byte[] buffer = new byte[32 * 1024];
            int bytesRead = 0;
            while ((bytesRead = iStream.read(buffer)) != -1)
            {
                biTotBytesWritten+= bytesRead;
                fOut.write(buffer, 0, bytesRead);
                String outstr="";

                tarFormatter.exc_array[exc_count-1]=(biTotBytesWritten/1000);
                for (int j=0;j<tarFormatter.exc_array.length;j++)
                {
                    outstr=outstr+"  "+Double.toString(tarFormatter.exc_array[j]);
                }
                System.out.print("\rDownload (KBytes): "+outstr);
            }
            wflog.log(Level.SEVERE,"Download Finished("+fileName+")"+ ": Total KBytes downloaded: "+(biTotBytesWritten/1000));
        }
        catch (Exception e)
        {
            retval=false;
            wflog.log(Level.SEVERE,"Download error("+fileName+"): "+ e.toString()+
                    ": Total KBytes downloaded: "+(biTotBytesWritten/1000));
            throw new IOException(me + " failed, got: " + e.toString());
        }
        finally
        {
            close(iStream, fOut);
        }
        return retval;
    }


    private void close(InputStream iStream, OutputStream oStream)
        throws IOException
    {
        try
        {
            if (iStream != null)
            {
                iStream.close();
            }
        }
        finally
        {
            if (oStream != null)
            {
                oStream.close();
            }
        }
    }

    public boolean UploadTarredMailBox(String destAdminuname,String destAdminpwd,String tarfile,
                                    String destServer,String destPort,String destmailbox,
                                    Logger utmbLog,boolean debug)
    {
/*        String zcs_nonssl_url=http+ztozparams.SourceZCSServer;

        try
        {
            zmsession.DoDelegateAuth(userid);
        }
        catch (Exception ex)
        {
            ztoz_log.log(Level.SEVERE,ex.getMessage());
        }
        
        String content_aid=zmsession.Upload_FileToZCS(zcs_nonssl_url, ztozparams.WorkingDirectory+"mailbox.tgz");
        if (content_aid !=null)
        {
            ZCSImportFile zcsimpfile =new ZCSImportFile(zmsession,userid ,content_aid, ztoz_log);
            boolean ret=zcsimpfile.UploadFile();
            if(ret)
            {
                //
            }
        }
*/
        boolean retval=false;
        Runtime r = Runtime.getRuntime();
        try
        {
            String cmdarr[]=new String[7];
            cmdarr[0]="curl";
            cmdarr[1]="--insecure";
            cmdarr[2]="-u";
            cmdarr[3]=destAdminuname+":"+destAdminpwd;
            cmdarr[4]="--data-binary";
            //"@C:\\Zimbra_Work\\YZYMigration\\ZCSProvisioning\\zcsprov\\mailboxdumps\\test11.tgz";
            cmdarr[5]="@"+tarfile;
            //"https://10.66.118.237:7071/service/home/test13@in.zimbra.com?fmt=tgz";
            cmdarr[6]="https://"+destServer+":"+destPort+"/service/home/"+destmailbox+"?fmt=tgz"; 
            
            String completeURL="";
            for (int i=0;i<cmdarr.length;i++)
            {
                completeURL +=" "+cmdarr[i];
            }
            utmbLog.log(Level.INFO,"Upload URL: "+completeURL);

            Process process = r.exec(cmdarr);
            InputStream errStrm = process.getErrorStream();
            InputStream inStrm = process.getInputStream();
            OutputStream outStrm = process.getOutputStream();
            Logger itmpL=null;
            if(debug)
            {
                itmpL =utmbLog;
            }
            StreamGobbler s1 = new StreamGobbler("INFO :",inStrm ,itmpL);
            StreamGobbler s2 = new StreamGobbler ("INFO:", errStrm,itmpL);
            s2.start ();
            s1.start ();
            try
            {
                process.waitFor();
            }
            catch (InterruptedException e)
            {
                utmbLog.log(Level.SEVERE, "UploadTarredMailBox Exception:"+e.getMessage());
                utmbLog.log(Level.SEVERE,ZCSUtils.stack2string(e));
            }
            int exitVal=process.exitValue();
            utmbLog.log(Level.INFO, "File Upload Exit value: " + exitVal);
            BufferedReader reader = new BufferedReader(
                                new InputStreamReader(inStrm));
            String s = reader.readLine();
            utmbLog.log(Level.INFO,"File Upload Finished."+s);
            retval= (exitVal==0);

        }
        catch (IOException e)
        {
            utmbLog.log(Level.SEVERE,e.getMessage());
        }
        return retval;
    }

    private boolean isEmpty(String str)
    {
        return str == null || str.length() == 0;
    }
    public void run()
    {
        String userAccount;
        while (!isEmpty(userAccount=AccountsList.GetNextAccount()))
        {
            tarmig_log.log(Level.INFO,"Processing Account: "+ userAccount);
            String userInfoArr[]=userAccount.split("@");
            if(userInfoArr.length==2)
            {
                tarMigtime=System.currentTimeMillis();
                Logger accountLog=tarMig_Logger.get_logger(userAccount);
                
                String targetDomain=userInfoArr[1];
                if(tarMigparams.DomainMap.containsKey(userInfoArr[1]))
                {
                    targetDomain= tarMigparams.DomainMap.get(userInfoArr[1]);
                }
                //
                tarmig_log.log(Level.INFO,"Going to download TarredMailBox "+"("+userAccount+")");
                if((IsAccountExists(userInfoArr[0]+"@"+targetDomain,accountLog))&&
                   (GetTarredMailBox(userAccount,accountLog)))
                {
                    tarmig_log.log(Level.INFO,"Download TarredMailBox Finsihed "+"("+userAccount+").");

                    tarmig_log.log(Level.INFO,"Upload TarredMailBox Started... "+"("+userAccount+")");
                    boolean uploadRet=false;

                    uploadRet=UploadTarredMailBox(tarMigparams.TrgtAdminUser,tarMigparams.TrgtAdminPwd,
                        tarMigparams.WorkingDirectory +userAccount+".tgz",
                        tarMigparams.TargetZCSServer,tarMigparams.TrgtZCSPort,
                        userInfoArr[0]+"@"+targetDomain,accountLog,tarMigparams.debug_mig);

                    if(uploadRet)
                    {
                        tarmig_log.log(Level.INFO,"Upload TarredMailBox Finsihed "+"("+userAccount+").");
                        //delete tarred mailbox file?
                        if(tarMigparams.KeepSuccessFiles.compareToIgnoreCase("FALSE")==0)
                        {
                            File f1 = new File(tarMigparams.WorkingDirectory +userAccount+".tgz");
                            if (f1.exists())
                            {
                                if(f1.delete())
                                {
                                    tarmig_log.log(Level.INFO,"Deleted "+tarMigparams.WorkingDirectory +userAccount+".tgz");
                                }
                            }
                            else
                            {
                                tarmig_log.log(Level.INFO,"File does not exists "+tarMigparams.WorkingDirectory +userAccount+".tgz");
                            }
                        }
                        else //move to SUCCESS directory
                        {
                            //if not empty, modify ZimbraMailTransport
                            if(!(isEmpty(tarMigparams.ZimbraMailTransport)))
                            {
                                if(!zcsacprov.ModifyAccountMailTransport(dest_accountid,tarMigparams.ZimbraMailTransport))
                                {
                                    tarmig_log.log(Level.SEVERE,"ModifyAccountMailTransport Failed: "+userInfoArr[0]+"@"+targetDomain);
                                }
                                else
                                {
                                    tarmig_log.log(Level.INFO,"ModifyAccountMailTransport Succeeded: "+userInfoArr[0]+"@"+targetDomain);
                                }
                            }
                            ZCSUtils.move_file_to(tarMigparams.WorkingDirectory,
                                    tarMigparams.SuccessDirectory ,userAccount+".tgz");
                             tarmig_log.log(Level.INFO,"Moved "+tarMigparams.WorkingDirectory +userAccount+".tgz"+
                             " to "+tarMigparams.SuccessDirectory+userAccount+".tgz");
                        }
                    }
                    else
                    {
                        tarmig_log.log(Level.INFO,"Upload TarredMailBox Error "+"("+userAccount+").");
                        ZCSUtils.move_file_to(tarMigparams.WorkingDirectory,
                                    tarMigparams.FailedDirectory ,userAccount+".tgz");
                        tarmig_log.log(Level.INFO,"Moved "+tarMigparams.WorkingDirectory +userAccount+".tgz"+
                             " to "+tarMigparams.FailedDirectory+userAccount+".tgz");
                        incr_err_count();
                    }

                }
                else
                {
                    tarmig_log.log(Level.SEVERE,"Download TarredMailBox Error"+"("+userAccount+").");
                    if(dest_accountid==null)
                    {
                        tarmig_log.log(Level.SEVERE,"User "+userAccount+" does not exists.");   
                    }
                    incr_err_count();
                }
                incr_processed_account_count();
                tarMigtime = (System.currentTimeMillis()-tarMigtime);
                TotalMigTime += tarMigtime;
                accountLog.log(Level.INFO,"Total Time Taken (in seconds): "+(tarMigtime/1000));
                tarMig_Logger.close(userAccount);
            }
        }
    }

    boolean IsAccountExists(String fqusername,Logger accountLog)
    {
        boolean retval=true;
        ZCSProvParams zcsprovparams;
        zcsprovparams=new ZCSProvParams();
        zcsprovparams.zcsurl =tarMigparams.TargetZCSServer;
        zcsprovparams.adminname = tarMigparams.TrgtAdminUser;
        zcsprovparams.adminpwd = tarMigparams.TrgtAdminPwd;
        zcsprovparams.zcsport = tarMigparams.TrgtZCSPort;
        zcsacprov= new ZCSACProvision(zcsprovparams,accountLog);
        zcsacprov.Init();
        if(tarMigparams.debug_mig)
        {
            zcsacprov.enable_dump_all();
        }
        dest_accountid=zcsacprov.GetAccountIDByName(fqusername);
        if(dest_accountid ==null)
        {
            retval=false;    
        }
        debug_msg(tarmig_log,"AccountID: "+dest_accountid);
        return retval;
    }

    public static void print_summary()
    {
        print_menu_logger.log(Level.INFO, "****************SUMMARY**************************");
        print_menu_logger.log(Level.INFO, "Total Accounts processed         :    "+get_processed_account_count());
        print_menu_logger.log(Level.INFO, "Successfull Accounts             :    "+(get_processed_account_count()-get_err_count()));
        print_menu_logger.log(Level.INFO, "Failed accounts                  :    "+get_err_count());
        print_menu_logger.log(Level.INFO, "Total Migration Time(seconds)    :    "+(GetTotalMigTime()/1000));
        print_menu_logger.log(Level.INFO, "*************************************************");
    }
}

public class tarFormatter implements EventNotifier
{
    private static final String ztozlogFile="ztozlog";
    private static final String tarMigVersion="1.0";
    private static final String ztozconfigFile="zmztozmig.conf";
    private static final String ztoz_default_configpath="/opt/zimbra/conf/";
    private String configFile="";
    private ZCSPLogger tarformatter_Logger;
    private Logger tarfmt_log;
    private ZtoZImportParams tarfmtparams;
    private static int ztozparamcounter=0;
    public static double[] exc_array;
    public tarFormatter()
    {
        //
    }

    public boolean Init(String[] args)
    {
        boolean retval=true;
        tarfmtparams = new ZtoZImportParams();
        // Initiate the arguments engine.
        ArgsEngine engine = new ArgsEngine();
        // Configure the switches/options. Use true for valued options
        engine.add("-h", "--help");
        engine.add("-v", "--version");
        engine.add("-f", "--ConfigFile",true);
        engine.add("-d", "--debug");

        // Perform the parsing. The 'args' is the String[] received by main method.
        try
        {
            engine.parse(args);
        }
        catch(Exception ex)
        {
            System.out.println("[Error]: "+ ex.getMessage());
            print_menu();
            return false;
        }

        if(engine.getBoolean("-h"))
        {
            print_menu();
            return false;
        }
        else if(engine.getBoolean("-v"))
        {
            System.out.println("version: "+tarMigVersion);
            return false;
        }
        else if(engine.getBoolean("-f"))
        {
            configFile = engine.getString("-f");
        }
        else if(engine.getBoolean("-d"))
        {
            tarfmtparams.debug_mig=true;
        }
        if (configFile != null && configFile.length() == 0)
        {
            configFile=ZCSUtils.GetSlashedDir(ztoz_default_configpath)+ztozconfigFile;
        }

        retval=GetInputFromConfigFile(configFile);
        if(retval)
        {           
            exc_array = new double[tarfmtparams.Threads];
            String curdir=ZCSUtils.getCurrentDirectory();
            String logfqn= tarfmtparams.LogDirectory;//curdir+"/logs/";
            ZCSUtils.check_dir(logfqn);
            tarformatter_Logger = new ZCSPLogger(logfqn);
            tarfmt_log = tarformatter_Logger.get_logger(ztozlogFile);
            tarfmt_log.log(Level.INFO,"ConfigFile: "+configFile);
            tarfmt_log.log(Level.INFO,"Version: "+tarMigVersion);
        }
        return retval;
    }
    
    public void CloseTarFormatter()
    {
        tarformatter_Logger.closeAll();        
    }

    public void Execute()
    {
        int THREAD_COUNT=1;
        try
        {
            THREAD_COUNT=tarfmtparams.Threads;
            tarfmt_log.log(Level.INFO,"Thread count: "+THREAD_COUNT);
        }
        catch(Exception ex)
        {
            tarfmt_log.log(Level.WARNING,"no thread count found setting it to 1");
            THREAD_COUNT=1;
        }
        ThreadGroup tg = new ThreadGroup("ztozMigThreadGroup");
        Thread LocThreadPool[]= new Thread[THREAD_COUNT];

        try
        {
            for (int t=0;t<THREAD_COUNT;t++)
            {
                tarMigrator tarmigrator= new tarMigrator(tarfmtparams,tarformatter_Logger);
                tarmigrator.Init();

                LocThreadPool[t]  = new Thread(tg,tarmigrator,"ZTOZThread"+(t+1));
                LocThreadPool[t].start();
            }

            int i;
            for (i=0; i <THREAD_COUNT; i++)
            {
                try
                {
                    LocThreadPool[i].join();
                }
                catch (InterruptedException e)
                {
                    System.out.print("Join interrupted\n");
                }
            }
        }
        catch(ZmProvGenericException zex)
        {
            tarfmt_log.log(Level.SEVERE,"Error: "+zex.getMessage());
        }
        catch(Exception ex)
        {
            tarfmt_log.log(Level.SEVERE,"Error: "+ex.getMessage());    
        }
        tarMigrator.print_summary();
    }

    private boolean GetInputFromConfigFile(String cfgfile)
    {
        boolean retval=true;
        try
        {
            CSVReader cr=null;
            try
            {
                cr= new CSVReader(cfgfile,1,-1);
            }
            catch (FileNotFoundException fex)
            {
                System.out.println("CSVReader error: file not found in current directory"+fex.getMessage());
                retval= false;
            }
            
            if(retval)
            {
                cr.set_eventnotifier(this);
                cr.ProcessFile();
            }

        }
        catch(ZmProvGenericException yzex)
        {
            System.out.println(yzex.getMessage());
            yzex.printStackTrace();
            retval=false;
        }
        return retval;
    }
    
    public void notifCUyevent(String[] params)
    {
        String attr[]= params[0].split("=");
        if (attr[0].trim().compareToIgnoreCase("SourceZCSServer")==0)
        {
            tarfmtparams.SourceZCSServer = attr[1].trim();
            ztozparamcounter++;
        }
        else if (attr[0].trim().compareToIgnoreCase("SourceAdminUser")==0)
        {
            tarfmtparams.SrcAdminUser = attr[1].trim();
            ztozparamcounter++;
        }
        else if (attr[0].trim().compareToIgnoreCase("SourceAdminPwd")==0)
        {
            tarfmtparams.SrcAdminPwd = attr[1].trim();
            ztozparamcounter++;
        }
        else if (attr[0].trim().compareToIgnoreCase("SourceAdminPort")==0)
        {
            tarfmtparams.SrcZCSPort = attr[1].trim();
            ztozparamcounter++;
        }
        if (attr[0].trim().compareToIgnoreCase("TargetZCSServer")==0)
        {
            tarfmtparams.TargetZCSServer = attr[1].trim();
            ztozparamcounter++;
        }
        else if (attr[0].trim().compareToIgnoreCase("TargetAdminUser")==0)
        {
            tarfmtparams.TrgtAdminUser = attr[1].trim();
            ztozparamcounter++;
        }
        else if (attr[0].trim().compareToIgnoreCase("TargetAdminPwd")==0)
        {
            tarfmtparams.TrgtAdminPwd = attr[1].trim();
            ztozparamcounter++;
        }
        else if (attr[0].trim().compareToIgnoreCase("TargetAdminPort")==0)
        {
            tarfmtparams.TrgtZCSPort = attr[1].trim();
            ztozparamcounter++;
        }
        else if (attr[0].trim().compareToIgnoreCase("Threads")==0)
        {
            tarfmtparams.Threads = Integer.parseInt(attr[1].trim());
            ztozparamcounter++;
        }
        else if (attr[0].trim().compareToIgnoreCase("WorkingDirectory")==0)
        {
            tarfmtparams.WorkingDirectory = ZCSUtils.GetSlashedDir(attr[1].trim());
            ztozparamcounter++;
        }
        else if (attr[0].trim().compareToIgnoreCase("FailedDirectory")==0)
        {
            tarfmtparams.FailedDirectory = ZCSUtils.GetSlashedDir(attr[1].trim());
            ztozparamcounter++;
        }
        else if (attr[0].trim().compareToIgnoreCase("SuccessDirectory")==0)
        {
            tarfmtparams.SuccessDirectory = ZCSUtils.GetSlashedDir(attr[1].trim());
            ztozparamcounter++;
        }
        else if (attr[0].trim().compareToIgnoreCase("LogDirectory")==0)
        {
            tarfmtparams.LogDirectory = ZCSUtils.GetSlashedDir(attr[1].trim());
            ztozparamcounter++;
        }
        else if (attr[0].trim().compareToIgnoreCase("KeepSuccessFiles")==0)
        {
            tarfmtparams.KeepSuccessFiles = attr[1].trim();
            ztozparamcounter++;
        }
        else if (attr[0].trim().compareToIgnoreCase("ZimbraMailTransport")==0)
        {
            tarfmtparams.ZimbraMailTransport = attr[1].trim();
            ztozparamcounter++;
        }
        else if (attr[0].trim().compareToIgnoreCase("DomainMap")==0)
        {
            String strDomainMap=attr[1].trim();
            String dmattr[]= strDomainMap.split(" ");
            if(dmattr.length ==2)
            {
                tarfmtparams.DomainMap.put(dmattr[0].trim(),dmattr[1].trim());
            }
            else
            {
                System.out.println("Domain map is not in correct format.");
            }
        }
        else if(attr[0].trim().compareToIgnoreCase("Accounts")==0)
        {
            String AllAccounts=attr[1];
            String AccountsArr[]= AllAccounts.split(",");

            tarfmtparams.AccountsList.add(attr[1].trim());
            for(int i=1;i<params.length;i++)
            {
                tarfmtparams.AccountsList.add(params[i].trim());
            }
            AccountsList.SetAccountList(tarfmtparams.AccountsList);
        }
    }

    private void print_menu()
    {
        System.out.println("usage:");
        System.out.println("*********************");
        System.out.println("zmztozmig -[options]");
        System.out.println("Options details:");
        System.out.println("-v --version                    Prints version");
        System.out.println("-h --help                       Shows help");
        System.out.println("-f --ConfigFile                 Config file path");
        System.out.println("                                [default file -> /opt/zimbra/conf/zmztozmig.conf]");
        System.out.println("-d --debug                      prints versbose debug messages");        
    }
    
    public static void main(String[] args)
    {
        tarFormatter tarformatter = new tarFormatter();
        if(tarformatter.Init(args))
        {
            tarformatter.Execute();
            tarformatter.CloseTarFormatter();
        }
    }
}
