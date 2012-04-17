using CssLib;
using MVVM;
using System.Collections.Generic;
using System.ComponentModel;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading;
using System;

namespace ZimbraMigrationConsole
{
    class CommandLineArgs
    {
        public static CommandLineArgs I
        {
            get
            {
                
                return m_instance;
            }
        }

        public string argAsString(string argName)
        {
            if (m_args.ContainsKey(argName))
            {
                return m_args[argName];
            }
            else return "";
        }

        public long argAsLong(string argName)
        {
            if (m_args.ContainsKey(argName))
            {
                return Convert.ToInt64(m_args[argName]);
            }
            else return 0;
        }
        public int argAsInt(string argName)
        {
            if (m_args.ContainsKey(argName))
            {
                return Convert.ToInt32(m_args[argName]);
            }
            else return 4;
        }

        public double argAsDouble(string argName)
        {
            if (m_args.ContainsKey(argName))
            {
                return Convert.ToDouble(m_args[argName]);
            }
            else return 0;
        }
        public bool argAsBool(string argName)
        {
            if (m_args.ContainsKey(argName))
            {
                return Convert.ToBoolean(m_args[argName]);
            }
            else return false;
        }

        public void parseArgs(string[] args, string defaultArgs)
        {
              m_args = new Dictionary<string, string>();
                parseDefaults(defaultArgs);

                foreach (string arg in args)
                {
                    string[] words = arg.Split('=');
                    m_args[words[0]] = words[1];
                }
            
        }

        private void parseDefaults(string defaultArgs)
        {
            if (defaultArgs == "") return;
            string[] args = defaultArgs.Split(';');

            foreach (string arg in args)
            {
                string[] words = arg.Split('=');
                m_args[words[0]] = words[1];
            }
        }

        private Dictionary<string, string> m_args = null;
        static readonly CommandLineArgs m_instance = new CommandLineArgs();
    } 
 

class Program
{
    private static bool keepRunning = false;

    private static Account userAccts = new Account();

    public static CountdownEvent countdownEvent;


    /*******************************************************************************/
    #region GLOBAL VARS   
    private static readonly Mutex mutex = new Mutex(true, System.Reflection.Assembly.GetExecutingAssembly().GetName().CodeBase);   
    private static bool _userRequestExit = false;   
    //private static bool _doIStop = false;   
    static HandlerRoutine consoleHandler;  
    #endregion   
    
    [System.Runtime.InteropServices.DllImport("Kernel32")]       
    public static extern bool SetConsoleCtrlHandler(HandlerRoutine Handler, bool Add);      
    // A delegate type to be used as the handler routine for SetConsoleCtrlHandler.      
    public delegate bool HandlerRoutine(CtrlTypes CtrlType);       
    // An enumerated type for the control messages sent to the handler routine.      
    public enum CtrlTypes  
    {            CTRL_C_EVENT = 0,    
        CTRL_BREAK_EVENT,        
        CTRL_CLOSE_EVENT,    
        CTRL_LOGOFF_EVENT = 5,  
        CTRL_SHUTDOWN_EVENT        }     
    /// <summary>        ///         /// </summary>        /// <param name="ctrlType"></param>        /// <returns></returns>      
    /// 
    
    private static bool ConsoleCtrlCheck(CtrlTypes ctrlType)        {  
        // Put your own handler here         
        switch (ctrlType)            {       
        case CtrlTypes.CTRL_C_EVENT:     
            _userRequestExit = true;
            keepRunning = true;
            Console.WriteLine("CTRL+C received, shutting down");
            //Account a = new Account();
            
            userAccts.RequestStop();
            Console.WriteLine("User cancelled, shutting down");

            if (countdownEvent != null)
            {
                while ((countdownEvent.CurrentCount > 0))
                {
                    Console.WriteLine("signaling background workers");
                    countdownEvent.Signal(1);
                    Thread.Sleep(2000);


                }
            }
            

            Console.WriteLine("User cancelled, aborting down");
            System.Console.WriteLine("press an key to continue");
                Console.ReadKey(true);
                
           Thread.CurrentThread.Abort();
            
             break;                
        
        case CtrlTypes.CTRL_BREAK_EVENT:       
            _userRequestExit = true;      
            Console.WriteLine("CTRL+BREAK received, shutting down");    
            break;   
        
        case CtrlTypes.CTRL_CLOSE_EVENT:  
            _userRequestExit = true;    
            Console.WriteLine("Program being closed, shutting down"); 
            break;
        
        case CtrlTypes.CTRL_LOGOFF_EVENT: 
        case CtrlTypes.CTRL_SHUTDOWN_EVENT:  
        _userRequestExit = true;                
        Console.WriteLine("User is logging off!, shutting down");  
        break;          
        }            
        return true;       
    }
    /********************************************************************************************/
    static void Main(string[] args)
    {


        if (!mutex.WaitOne(TimeSpan.Zero, true)) 
        { 
            Console.WriteLine("Another instance already running"); 
            Thread.Sleep(5000); 
            return ; 
        }              
        //save a reference so it does not get GC'd     
        consoleHandler = new HandlerRoutine(ConsoleCtrlCheck);     
        //set our handler here that will trap exit          
        SetConsoleCtrlHandler(consoleHandler, true);
       
        
        //Account userAccts = new Account();
        while (!keepRunning)
        {

            
            if (args.Count() > 0)
            {

                if ((args[0].Equals("-Help", StringComparison.CurrentCultureIgnoreCase)) ||(args[0].Equals("-h", StringComparison.CurrentCultureIgnoreCase)))
                {
                    string builder = "Usage of ZimbraMigrationConsole.exe ConfigxmlFile=C:\\MyConfig.xml Users =C:\\users.csv \n";
                    builder += "\n";
                    builder += "ConfigxmlFile= location of the xml file \n";
                    builder += "\n";
                    builder += "Users= location of the csv file \n";
                    builder += "\n";
                    builder += "MaxThreads= Maximum number of threads by default it uses 4.\n";
                    builder += "\n";
                    builder += "MaxErrors= Maximum no of errors allowed for the each migration \n";
                    builder += "\n";
                    builder += "MaxWarn= Maximum no of warnings \n";
                    builder += "\n";
                    builder += " Profile= UserProfile to be migrated for user migration \n";
                    builder += "\n";
                    builder += "DataFile= PST file for the user to be migrated\n";
                    builder += "\n";
                    builder += "ZimbraHost= The Zimbra server hostname \n";
                    builder += "\n";
                    builder += "ZimbraPort= The Zimbra port \n";
                    builder += "\n";
                    builder += " ZimbraID= The Zimbra ID. For server migration it’s the admin id and for user migration it’s the userid on Zimbra\n";
                    builder += "\n";
                    builder += " ZimbraPwd= Pwd for Zimbra \n";
                    builder += "\n";
                    builder += "The Migration Item Options can be specified as Mail=True Calendar=True Contacts=True Sent=True DeletedItems=True Junk=True Tasks=True Rules=True OOO=True \n";
                    builder += " By default these options are false. Unless specified in the XML or as arguments \n";
                    builder += "\n";
                    builder += "Verbose= Debug|Info|Trace  .This option provides various levels of logging \n";
                    builder += "\n";
                    builder += "For more information see the help file distributed with the exe. \n";



                    System.Console.Write(builder);
                    keepRunning = true;
                    Console.ReadKey(true);

                    return;

                }
                
                try
                {
                CommandLineArgs.I.parseArgs(args, "myStringArg=defaultVal;someLong=12");
                   
                }
                catch (Exception e)
                {
                    System.Console.WriteLine("Incorrect format of CmdLine arguments" + e.Message);
                    keepRunning = true;
                    Console.ReadKey(true);
                    return;
                }
                string ConfigXmlFile = CommandLineArgs.I.argAsString("ConfigxmlFile");
                string UserMapFile = CommandLineArgs.I.argAsString("Users");
                int MaxThreads = CommandLineArgs.I.argAsInt("MaxThreads");
                string MaxErrors = CommandLineArgs.I.argAsString("MaxErrors");
                string MaxWarns = CommandLineArgs.I.argAsString("MaxWarn");
                string userid = CommandLineArgs.I.argAsString("Profile");
                string Pstfile = CommandLineArgs.I.argAsString("DataFile");
                string ZCSHost = CommandLineArgs.I.argAsString("ZimbraHost");
                string ZCSPort = CommandLineArgs.I.argAsString("ZimbraPort");
                string ZCSID = CommandLineArgs.I.argAsString("ZimbraID");
                string ZCSPwd = CommandLineArgs.I.argAsString("ZimbraPwd");

                bool Mail = CommandLineArgs.I.argAsBool("Mail");
                bool Calendar = CommandLineArgs.I.argAsBool("Calendar");
                bool Contacts = CommandLineArgs.I.argAsBool("Contacts");
                bool Sent = CommandLineArgs.I.argAsBool("Sent");
                bool DeletedItems = CommandLineArgs.I.argAsBool("DeletedItems");
                bool Junk = CommandLineArgs.I.argAsBool("Junk");
                bool Tasks = CommandLineArgs.I.argAsBool("Tasks");
                bool Rules = CommandLineArgs.I.argAsBool("Rules");
                bool OOO = CommandLineArgs.I.argAsBool("OOO");

                string Verbose = CommandLineArgs.I.argAsString("Verbose");

                bool ServerMigration = false;
                XmlConfig myXmlConfig = new XmlConfig();

                if ((ConfigXmlFile != "") && (File.Exists(ConfigXmlFile)))
                {
                    if ((UserMapFile != "") && (File.Exists(UserMapFile)))
                    {
                        myXmlConfig = new XmlConfig(ConfigXmlFile, UserMapFile);

                        myXmlConfig.InitializeConfig();

                        myXmlConfig.GetUserList();

                    }
                    else
                    {

                        myXmlConfig = new XmlConfig(ConfigXmlFile, "");

                        myXmlConfig.InitializeConfig();


                    }
                    if (myXmlConfig.UserList.Count > 0)
                    {

                        ServerMigration = true;
                        if (userid == "")
                            userid = (myXmlConfig.ConfigObj.SourceServer.AdminID != "") ? myXmlConfig.ConfigObj.SourceServer.AdminID : myXmlConfig.ConfigObj.SourceServer.Profile;

                        if (ZCSID == "")
                            ZCSID = myXmlConfig.ConfigObj.ZimbraServer.AdminID;

                        if (ZCSPwd == "")
                            ZCSPwd = myXmlConfig.ConfigObj.ZimbraServer.AdminPwd;

                    }
                    else
                    {

                        if (userid == "")
                            userid = myXmlConfig.ConfigObj.SourceServer.Profile;

                        if (ZCSID == "")
                            ZCSID = myXmlConfig.ConfigObj.ZimbraServer.UserAccount;

                        if (ZCSPwd == "")
                            ZCSPwd = myXmlConfig.ConfigObj.ZimbraServer.UserPassword;

                        if (Pstfile == "")
                            Pstfile = myXmlConfig.ConfigObj.SourceServer.DataFile;

                    }

                    if (ZCSHost == "")
                        ZCSHost = myXmlConfig.ConfigObj.ZimbraServer.Hostname;

                    if (ZCSPort == "")
                        ZCSPort = myXmlConfig.ConfigObj.ZimbraServer.Port;

                    if (Verbose == "")
                        Verbose = myXmlConfig.ConfigObj.GeneralOptions.LogLevel;

                    if (Mail == false)
                        Mail = myXmlConfig.ConfigObj.ImportOptions.Mail;

                    if (Calendar == false)
                        Calendar = myXmlConfig.ConfigObj.ImportOptions.Calendar;

                    if (Contacts == false)
                        Contacts = myXmlConfig.ConfigObj.ImportOptions.Contacts;

                    if (Sent == false)
                        Sent = myXmlConfig.ConfigObj.ImportOptions.Sent;

                    if (DeletedItems == false)
                        DeletedItems = myXmlConfig.ConfigObj.ImportOptions.DeletedItems;

                    if (Junk == false)
                        Junk = myXmlConfig.ConfigObj.ImportOptions.Junk;
                    if (Tasks == false)
                        Tasks = myXmlConfig.ConfigObj.ImportOptions.Tasks;
                    if (Rules == false)
                        Rules = myXmlConfig.ConfigObj.ImportOptions.Rules;
                    if (OOO == false)
                        OOO = myXmlConfig.ConfigObj.ImportOptions.OOO;


                }
                else
                {
                    if (ConfigXmlFile != "")
                    {
                        if (!File.Exists(ConfigXmlFile))
                            System.Console.WriteLine(" XML file not present.please check the file name or path");
                    }

                }

                MigrationOptions importopts = new MigrationOptions();
                ItemsAndFoldersOptions itemFolderFlags = ItemsAndFoldersOptions.None;
                if (Calendar)
                {
                    itemFolderFlags = itemFolderFlags | ItemsAndFoldersOptions.Calendar;
                }
                if (Contacts)
                {
                    itemFolderFlags = itemFolderFlags | ItemsAndFoldersOptions.Contacts;
                }
                if (Mail)
                {
                    itemFolderFlags = itemFolderFlags | ItemsAndFoldersOptions.Mail;
                }
                if (Sent)
                {
                    itemFolderFlags = itemFolderFlags | ItemsAndFoldersOptions.Sent;
                }
                if (DeletedItems)
                {
                    itemFolderFlags = itemFolderFlags | ItemsAndFoldersOptions.DeletedItems;
                }
                if (Junk)
                {
                    itemFolderFlags = itemFolderFlags | ItemsAndFoldersOptions.Junk;
                }
                if (Tasks)
                {
                    itemFolderFlags = itemFolderFlags | ItemsAndFoldersOptions.Tasks;
                }
                if (Rules)
                {
                    itemFolderFlags = itemFolderFlags | ItemsAndFoldersOptions.Rules;
                }
                if (OOO)
                {
                    itemFolderFlags = itemFolderFlags | ItemsAndFoldersOptions.OOO;
                }

                importopts.ItemsAndFolders = itemFolderFlags;
                switch(Verbose)
                {
                case"Debug":
                     importopts.VerboseOn = LogLevel.Debug;
                    break;
                case "Info":
                    importopts.VerboseOn = LogLevel.Info;
                    break;
                case "Trace":
                    importopts.VerboseOn = LogLevel.Trace;
                    break;

                default:
                    importopts.VerboseOn = LogLevel.Info;
                    break;
                }

                //importopts.VerboseOn = Verbose;

                Migration Test = new Migration();
                CssLib.CSMigrationWrapper TestObj;
                try
                {

                    TestObj = new CSMigrationWrapper("MAPI");
                }

                catch (Exception e)
                {

                    string error = " Migrationwrapper cannot be initialised ,Migration dll cannot be loaded";
                    error += e.Message;
                    System.Console.WriteLine();
                    System.Console.WriteLine(error);
                   /* ProgressUtil.RenderConsoleProgress(30, '\u2591', ConsoleColor.Red,
                        error);*/
                    System.Console.WriteLine("......... \n");
                    /*ProgressUtil.RenderConsoleProgress(30, '\u2591', ConsoleColor.Red,
                            "");*/
                    System.Console.WriteLine("......... \n");

                    return;

                }

                System.Console.WriteLine();
                System.Console.WriteLine("  Migration Initialization ");
               /* ProgressUtil.RenderConsoleProgress(30, '\u2591', ConsoleColor.Green,
                    "  Migration Initialization ");*/
                System.Console.WriteLine("......... \n");
               /* ProgressUtil.RenderConsoleProgress(30, '\u2591', ConsoleColor.Green,
                        "");*/
                System.Console.WriteLine("......... \n");

                if (userid != "")
                {

                    string retval = TestObj.GlobalInit(userid, "", "");


                    if (retval.Length > 0)
                    {
                        System.Console.WriteLine();
                        System.Console.WriteLine(" Error in Migration Initialization ");
                        /*ProgressUtil.RenderConsoleProgress(30, '\u2591', ConsoleColor.Red,
                            " Error in Migration Initialization ");*/
                        System.Console.WriteLine("......... \n");
                      /*  ProgressUtil.RenderConsoleProgress(30, '\u2591', ConsoleColor.Red,
                                retval);*/
                        System.Console.WriteLine("......... \n");
                        System.Console.WriteLine();

                        return;
                    }
                }
                                
                if (ServerMigration)
                {

                    foreach (MVVM.Model.Users user in myXmlConfig.UserList)
                    {
                
                        Account userAcct = new Account();

                        System.Console.WriteLine();
                        /*ProgressUtil.RenderConsoleProgress(30, '\u2591', ConsoleColor.Green,
                            "Connecting to to Zimbra Server \n   ");*/
                        System.Console.WriteLine("Connecting to to Zimbra Server \n   ");
                        System.Console.WriteLine();

                        ZimbraAPI zimbraAPI = new ZimbraAPI();
                        /*int stat = zimbraAPI.Logon(
                            myXmlConfig.ConfigObj.zimbraServer.Hostname,
                            myXmlConfig.ConfigObj.zimbraServer.Port,
                            myXmlConfig.ConfigObj.zimbraServer.AdminID,
                            myXmlConfig.ConfigObj.zimbraServer.AdminPwd, true);*/

                        int stat = zimbraAPI.Logon(
                           ZCSHost,
                           ZCSPort,
                          ZCSID,
                          ZCSPwd, true);


                        if (stat != 0)
                        {
                            zimbraAPI.LastError.Count();

                            System.Console.WriteLine();
                            string message = "Logon to to Zimbra Server  for adminAccount failed " +
                                myXmlConfig.ConfigObj.ZimbraServer.AdminID;
                            System.Console.WriteLine(message);
                         /*   ProgressUtil.RenderConsoleProgress(30, '\u2591', ConsoleColor.Red,
                                "Logon to to Zimbra Server  for adminAccount failed " +
                                myXmlConfig.ConfigObj.ZimbraServer.AdminID);*/
                            System.Console.WriteLine("......... \n");
                            System.Console.WriteLine();
                            Thread.Sleep(2000);
                            // return;
                        }

                        // userAcct.InitializeMigration(myXmlConfig.ConfigObj.zimbraServer.ZimbraHostname, myXmlConfig.ConfigObj.zimbraServer.Port, myXmlConfig.ConfigObj.zimbraServer.ZimbraAdminID,user.UserName);
                        string acctName;
                        if (user.MappedName == "")
                        {
                             acctName = user.UserName + '@' +
                                (myXmlConfig.ConfigObj.UserProvision.DestinationDomain == "" ? ZCSHost : myXmlConfig.ConfigObj.UserProvision.DestinationDomain);
                        }
                        else
                        {
                            acctName = user.MappedName + '@' +
                               (myXmlConfig.ConfigObj.UserProvision.DestinationDomain == "" ? ZCSHost : myXmlConfig.ConfigObj.UserProvision.DestinationDomain);

                        }

                        if (zimbraAPI.GetAccount(acctName) == 0)
                        {
                            System.Console.WriteLine();
                            System.Console.WriteLine();
                            string mesg = " Migration to Zimbra Started  for UserAccount " +
                                acctName;
                            System.Console.WriteLine(mesg);
                          /*  ProgressUtil.RenderConsoleProgress(30, '\u2591', ConsoleColor.Green,
                                " Migration to Zimbra Started  for UserAccount " +
                                acctName);*/
                            System.Console.WriteLine();
                            System.Console.WriteLine();

                            
                        }
                        else
                        {
                            System.Console.WriteLine();
                            string err = " User is not provisioned on Zimbra Server " +
                                acctName;
                            System.Console.WriteLine(err);
                           /* ProgressUtil.RenderConsoleProgress(30, '\u2591',
                                ConsoleColor.Yellow,
                                " User is not provisioned on Zimbra Server " +
                                acctName);*/

                            System.Console.WriteLine();
                            System.Console.WriteLine();
                            err = " Provisioning user" + acctName;
                           /* ProgressUtil.RenderConsoleProgress(30, '\u2591', ConsoleColor.Green,
                                " Provisioning user" + acctName);*/
                            System.Console.WriteLine(err);
                            System.Console.WriteLine();
                            System.Console.WriteLine();
                            string Defaultpwd = "";

                            /************************************///if csv file has a pwd use it else looks for the pwd in xml file.
                            if((user.PWDdefault != ""))
                                Defaultpwd = user.PWDdefault;
                            else
                                Defaultpwd = myXmlConfig.ConfigObj.UserProvision.DefaultPWD;


                            if (Defaultpwd == "")
                            {

                                System.Console.WriteLine();
                                string msg = @" Default Pwd for user provisioning is empty string.Please correct the csv/xml file \n";
                                    
                                System.Console.WriteLine(msg);
                                System.Console.WriteLine("Press any key to exit");
                                Console.ReadKey(true);
                
                                return;
                            }
                            if (zimbraAPI.CreateAccount(acctName,
                                "",
                                "",
                                "",
                                "",
                                Defaultpwd,
                                myXmlConfig.ConfigObj.UserProvision.COS) == 0)
                            {
                                System.Console.WriteLine();
                               /* ProgressUtil.RenderConsoleProgress(30, '\u2591',
                                    ConsoleColor.Green,
                                    " Provisioning useraccount success " + acctName);*/
                                err = " Provisioning useraccount success " + acctName;
                                System.Console.WriteLine(err);
                                System.Console.WriteLine();
                                System.Console.WriteLine();
                                /*ProgressUtil.RenderConsoleProgress(30, '\u2591',
                                    ConsoleColor.Green,
                                    " Migration to Zimbra Started  for UserAccount  " +
                                    user.UserName);*/
                                err = " Migration to Zimbra Started  for UserAccount  " +
                                    user.UserName;

                                System.Console.WriteLine(err);
                                System.Console.WriteLine();
                                System.Console.WriteLine("......... \n");
                                
                            }
                            else
                            {
                                System.Console.WriteLine();

                               /* ProgressUtil.RenderConsoleProgress(30, '\u2591',
                                    ConsoleColor.Red, " error provisioning user " +
                                    acctName);*/
                                err = " error provisioning user " +
                                    acctName;
                                System.Console.WriteLine();
                                System.Console.WriteLine();
                            }
                        }

                        string final = user.StatusMessage;
                    }

                    countdownEvent = new CountdownEvent(myXmlConfig.UserList.Count);
                    
                    userAccts.StartMigration(myXmlConfig.UserList, myXmlConfig.ConfigObj.UserProvision.DestinationDomain, importopts, countdownEvent, TestObj, MaxThreads);
                    countdownEvent.Wait();
                   
                    Console.WriteLine("Finished Migration");
                    keepRunning = true;

                }
                else
                {

                    if ((userid != "") || (Pstfile != ""))
                    {
                        string accountname = ZCSID;
                        accountname = accountname + "@" + ZCSHost;
                        string accountid = (Pstfile != "") ? Pstfile : userid;

                            ZimbraAPI zimbraAPI = new ZimbraAPI();

                            System.Console.WriteLine();
                           /* ProgressUtil.RenderConsoleProgress(
                                    30, '\u2591', ConsoleColor.Green,
                                    "Connecting to to Zimbra Server \n   ");*/
                            string err = "Connecting to to Zimbra Server \n   ";
                            System.Console.WriteLine(err);
                            System.Console.WriteLine();

                            int stat = zimbraAPI.Logon(
                                    ZCSHost,
                                    ZCSPort,
                                    ZCSID,
                                    ZCSPwd, false);

                            if (stat != 0)
                            {
                                zimbraAPI.LastError.Count();

                                System.Console.WriteLine();
                                /*ProgressUtil.RenderConsoleProgress(
                                        30, '\u2591', ConsoleColor.Red,
                                        "Logon to to Zimbra Server  for userAccount failed " +
                                        ZCSID);*/
                                err = "Logon to to Zimbra Server  for userAccount failed " +
                                        ZCSID;
                                System.Console.WriteLine(err);
                                System.Console.WriteLine("......... \n");
                                System.Console.WriteLine();
                                Thread.Sleep(2000);
                                return;
                            }

                        
                        System.Console.WriteLine();
                       /* ProgressUtil.RenderConsoleProgress(
                                30, '\u2591', ConsoleColor.Green,
                                " Migration to Zimbra Started  for Profile/PST  " +
                                accountid);*/
                        err = " Migration to Zimbra Started  for Profile/PST  " +
                                accountid;
                        System.Console.WriteLine(err);
                        System.Console.WriteLine();
                        System.Console.WriteLine();

                        //  Test.test(accountname, TestObj, accountid, importopts, false);
                        countdownEvent = new CountdownEvent(1);
                        //Account userAccts = new Account();

                        MVVM.Model.Users User = new MVVM.Model.Users();
                        User.UserName = userid;

                        List<MVVM.Model.Users> users = new List<MVVM.Model.Users>();
                        users.Add(User);
                        userAccts.StartMigration(users, ZCSHost, importopts, countdownEvent, TestObj, MaxThreads, false, accountname, accountid);
                        // Thread.Sleep(129000);

                        countdownEvent.Wait();
                        Console.WriteLine("Finished Migration");
                        Console.WriteLine();
                        keepRunning = true;


                    }



                }
            }
            else
            {
                System.Console.WriteLine();
                /*ProgressUtil.RenderConsoleProgress(30, '\u2591', ConsoleColor.Red,
                    " Make sure the correct arguments (2) are passed \n");*/
                System.Console.WriteLine(" Make sure the correct arguments (2) are passed . type Help for more information\n");
                System.Console.WriteLine();
                Console.ReadKey(true);
                return;

            }
        }


       /// Account userAccts = new Account();
        if (_userRequestExit)
        {
            keepRunning = true;
            //set flag to exit loop.  Other conditions could cause this too, which is why we use a seperate variable      
           // Console.WriteLine("Shutting down, user requested exit");
            
        }
        System.Console.WriteLine(" Press any key to continue \n");
        Console.ReadKey(true);
        return;
      }
}
}
