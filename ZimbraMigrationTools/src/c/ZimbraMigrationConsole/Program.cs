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
    static void Main(string[] args)
    {
        CommandLineArgs.I.parseArgs(args, "myStringArg=defaultVal;someLong=12");
       // Console.WriteLine("Arg myStringArg  : '{0}' ", CommandLineArgs.I.argAsString("xmlfile"));

        //Console.WriteLine("Arg someLong     : '{0}' ", CommandLineArgs.I.argAsLong("users")); 

        string ConfigXmlFile = CommandLineArgs.I.argAsString("xmlfile");
        string UserMapFile = CommandLineArgs.I.argAsString("users");
        string MaxThreads = CommandLineArgs.I.argAsString("MaxThreads");
        string MaxErrors = CommandLineArgs.I.argAsString("MaxErrors");
        string MaxWarns = CommandLineArgs.I.argAsString("MaxWarn");
        string userid = CommandLineArgs.I.argAsString("userid");
        string Pstfile = CommandLineArgs.I.argAsString("pst");
        string ZCSHost = CommandLineArgs.I.argAsString("ZCS");

        bool Mail = CommandLineArgs.I.argAsBool("Mail");
        bool Calendar = CommandLineArgs.I.argAsBool("Calendar");
        bool Contacts = CommandLineArgs.I.argAsBool("Contacts");
        bool Sent = CommandLineArgs.I.argAsBool("Sent");
        bool DeletedItems = CommandLineArgs.I.argAsBool("DeletedItems");
        bool Junk = CommandLineArgs.I.argAsBool("Junk");
        bool Tasks = CommandLineArgs.I.argAsBool("Tasks");
        bool Rules = CommandLineArgs.I.argAsBool("Rules");
        bool OOO = CommandLineArgs.I.argAsBool("OOO");


        Migration Test = new Migration();

        CssLib.CSMigrationWrapper TestObj = new CSMigrationWrapper("MAPI");

        //if (args.Count() == 2)
        if( (ConfigXmlFile != "") &&(UserMapFile != ""))
        {
           // string ConfigXmlFile = args[0];
           // string UserMapFile = args[1];

            if (File.Exists(ConfigXmlFile) && File.Exists(UserMapFile))
            {
                // if (File.Exists(@"C:\Temp\ZimbraAdminOverView.xml") && File.Exists(@"C:\Temp\UserMap.csv"))

                // XmlConfig myXmlConfig = new XmlConfig(@"C:\Temp\ZimbraAdminOverview.xml", @"C:\Temp\UserMap.csv");
                XmlConfig myXmlConfig = new XmlConfig(ConfigXmlFile, UserMapFile);

                myXmlConfig.InitializeConfig();

                myXmlConfig.GetUserList();

                MigrationOptions importopts = new MigrationOptions();
                ItemsAndFoldersOptions itemFolderFlags = ItemsAndFoldersOptions.None;
                if (myXmlConfig.ConfigObj.importOptions.Calendar)
                {
                    itemFolderFlags = itemFolderFlags | ItemsAndFoldersOptions.Calendar;
                }
                if (myXmlConfig.ConfigObj.importOptions.Contacts)
                {
                    itemFolderFlags = itemFolderFlags | ItemsAndFoldersOptions.Contacts;
                }
                if (myXmlConfig.ConfigObj.importOptions.Mail)
                {
                    itemFolderFlags = itemFolderFlags | ItemsAndFoldersOptions.Mail;
                }
                if (myXmlConfig.ConfigObj.importOptions.Sent)
                {
                    itemFolderFlags = itemFolderFlags | ItemsAndFoldersOptions.Sent;
                }
                if (myXmlConfig.ConfigObj.importOptions.DeletedItems)
                {
                    itemFolderFlags = itemFolderFlags | ItemsAndFoldersOptions.DeletedItems;
                }
                if (myXmlConfig.ConfigObj.importOptions.Junk)
                {
                    itemFolderFlags = itemFolderFlags | ItemsAndFoldersOptions.Junk;
                }
                if (myXmlConfig.ConfigObj.importOptions.Tasks)
                {
                    itemFolderFlags = itemFolderFlags | ItemsAndFoldersOptions.Tasks;
                }
                if (myXmlConfig.ConfigObj.importOptions.Rules)
                {
                    itemFolderFlags = itemFolderFlags | ItemsAndFoldersOptions.Rules;
                }
                if (myXmlConfig.ConfigObj.importOptions.OOO)
                {
                    itemFolderFlags = itemFolderFlags | ItemsAndFoldersOptions.OOO;
                }

                importopts.ItemsAndFolders = itemFolderFlags;

                string retval = "";   
                if (myXmlConfig.UserList.Count > 0)
                {
                    if (myXmlConfig.ConfigObj.SourceServer.Profile != "")
                    {
                        // profile migration
                       /* TestObj.Initalize(myXmlConfig.ConfigObj.zimbraServer.ZimbraHostname,
                            myXmlConfig.ConfigObj.zimbraServer.Port,
                            myXmlConfig.ConfigObj.zimbraServer.ZimbraAdminID,
                            myXmlConfig.ConfigObj.OutlookProfile, "", "");*/
                        retval = TestObj.GlobalInit(myXmlConfig.ConfigObj.SourceServer.Profile, "", "");
                    }
                    else
                    {
                       /* TestObj.Initalize(myXmlConfig.ConfigObj.zimbraServer.ZimbraHostname,
                            myXmlConfig.ConfigObj.zimbraServer.Port,
                            myXmlConfig.ConfigObj.zimbraServer.ZimbraAdminID, "",
                            myXmlConfig.ConfigObj.mailServer.SourceHostname,
                            myXmlConfig.ConfigObj.mailServer.SourceAdminID);*/

                        retval = TestObj.GlobalInit(myXmlConfig.ConfigObj.SourceServer.AdminID, "", "");
                       /* TestObj.InitializeMailClient(myXmlConfig.ConfigObj.mailServer.SourceHostname, myXmlConfig.ConfigObj.mailServer.SourceAdminID,
                    "");*/
                    }

                    if (retval.Length > 0)
                    {
                        System.Console.WriteLine();
                        ProgressUtil.RenderConsoleProgress(30, '\u2591', ConsoleColor.Red,
                            " Error in Migration Initialization ");
                        System.Console.WriteLine("......... \n");
                        ProgressUtil.RenderConsoleProgress(30, '\u2591', ConsoleColor.Red,
                             retval);
                        System.Console.WriteLine("......... \n");
                        System.Console.WriteLine();
                        
                        return;
                    }

                    foreach (MVVM.Model.Users user in myXmlConfig.UserList)
                    {
                        // TestObj.InitializeMailClient(myXmlConfig.ConfigObj.mailServer.SourceHostname,myXmlConfig.ConfigObj.mailServer.SourceAdminID,myXmlConfig.ConfigObj.mailServer.SourceAdminID);

                        /***************************
                         *
                         *
                         *
                         *
                         *
                         * ////////////////////////////////////////*/

                        Account userAcct = new Account();

                        System.Console.WriteLine();
                        ProgressUtil.RenderConsoleProgress(30, '\u2591', ConsoleColor.Green,
                            "Connecting to to Zimbra Server \n   ");
                        System.Console.WriteLine();

                        ZimbraAPI zimbraAPI = new ZimbraAPI();
                        int stat = zimbraAPI.Logon(
                            myXmlConfig.ConfigObj.zimbraServer.Hostname,
                            myXmlConfig.ConfigObj.zimbraServer.Port,
                            myXmlConfig.ConfigObj.zimbraServer.AdminID,
                            myXmlConfig.ConfigObj.zimbraServer.AdminPwd, true);

                        if (stat != 0)
                        {
                            zimbraAPI.LastError.Count();

                            System.Console.WriteLine();
                            ProgressUtil.RenderConsoleProgress(30, '\u2591', ConsoleColor.Red,
                                "Logon to to Zimbra Server  for adminAccount failed " +
                                myXmlConfig.ConfigObj.zimbraServer.AdminID);
                            System.Console.WriteLine("......... \n");
                            System.Console.WriteLine();
                            Thread.Sleep(2000);
                            // return;
                        }

                        // userAcct.InitializeMigration(myXmlConfig.ConfigObj.zimbraServer.ZimbraHostname, myXmlConfig.ConfigObj.zimbraServer.Port, myXmlConfig.ConfigObj.zimbraServer.ZimbraAdminID,user.UserName);

                        string acctName = user.UserName + '@' +
                            myXmlConfig.ConfigObj.UserProvision.DestinationDomain;

                        if (zimbraAPI.GetAccount(acctName) == 0)
                        {
                            System.Console.WriteLine();
                            System.Console.WriteLine();
                            ProgressUtil.RenderConsoleProgress(30, '\u2591', ConsoleColor.Green,
                                " Migration to Zimbra Started  for UserAccount " +
                                user.UserName);
                            System.Console.WriteLine();
                            System.Console.WriteLine();

                           // userAcct.StartMigration(myXmlConfig.UserListuser.UserName, importopts);
                            //Test.test(acctName, TestObj, user.UserName, importopts,true);

                            // /////////////////

                            // ///////////////////
                            Thread.Sleep(15000);
                        }
                        else
                        {
                            System.Console.WriteLine();
                            ProgressUtil.RenderConsoleProgress(30, '\u2591',
                                ConsoleColor.Yellow,
                                " User is not provisioned on Zimbra Server " +
                                user.UserName);

                            System.Console.WriteLine();
                            System.Console.WriteLine();

                            ProgressUtil.RenderConsoleProgress(30, '\u2591', ConsoleColor.Green,
                                " Provisioning user" + user.UserName);
                            System.Console.WriteLine();
                            System.Console.WriteLine();
                            if (zimbraAPI.CreateAccount(acctName,
                                myXmlConfig.ConfigObj.UserProvision.DefaultPWD,
                                myXmlConfig.ConfigObj.UserProvision.COS) == 0)
                            {
                                System.Console.WriteLine();
                                ProgressUtil.RenderConsoleProgress(30, '\u2591',
                                    ConsoleColor.Green,
                                    " Provisioning useraccount success " + user.UserName);

                                System.Console.WriteLine();
                                System.Console.WriteLine();
                                ProgressUtil.RenderConsoleProgress(30, '\u2591',
                                    ConsoleColor.Green,
                                    " Migration to Zimbra Started  for UserAccount  " +
                                    user.UserName);
                                System.Console.WriteLine();
                                System.Console.WriteLine();
                                // userAcct.StartMigration(user.UserName, myXmlConfig.ConfigObj.importOptions.Mail.ToString());
                               // Test.test(acctName, TestObj, user.UserName, importopts,true);
                               // userAcct.StartMigration(user.UserName, importopts);
                                System.Console.WriteLine("......... \n");
                                Thread.Sleep(9000);
                            }
                            else
                            {
                                System.Console.WriteLine();

                                ProgressUtil.RenderConsoleProgress(30, '\u2591',
                                    ConsoleColor.Red, " error provisioning user " +
                                    user.UserName);
                                System.Console.WriteLine();
                                System.Console.WriteLine();
                            }
                        }

                        string final = user.StatusMessage;
                    }

                    var countdownEvent = new CountdownEvent(myXmlConfig.UserList.Count); 
                    Account userAccts = new Account();
                    
                    userAccts.StartMigration(myXmlConfig.UserList, myXmlConfig.ConfigObj.UserProvision.DestinationDomain, importopts, countdownEvent,TestObj);
                   // Thread.Sleep(129000);

                    countdownEvent.Wait();
                    Console.WriteLine("Finished."); 

                }
                else
                {

                    if ((myXmlConfig.ConfigObj.SourceServer.Profile != "") || (myXmlConfig.ConfigObj.SourceServer.DataFile != ""))
                    {
                        // ServerMigration = false;

                        // profile migration




                        string accountname = myXmlConfig.ConfigObj.zimbraServer.UserAccount;
                        accountname = accountname + "@" + myXmlConfig.ConfigObj.zimbraServer.Hostname;
                        string accountid = (myXmlConfig.ConfigObj.SourceServer.DataFile != "") ? myXmlConfig.ConfigObj.SourceServer.DataFile : myXmlConfig.ConfigObj.SourceServer.Profile;

                        if (myXmlConfig.ConfigObj.SourceServer.Profile != "")
                        {
                           /* TestObj.Initalize(myXmlConfig.ConfigObj.zimbraServer.ZimbraHostname,
                                   myXmlConfig.ConfigObj.zimbraServer.Port,
                                   myXmlConfig.ConfigObj.zimbraServer.UserAccount,
                                   myXmlConfig.ConfigObj.OutlookProfile, "",
                                   "");*/
                            retval = TestObj.GlobalInit(myXmlConfig.ConfigObj.SourceServer.Profile, "", "");
                            if (retval.Length > 0)
                            {
                                System.Console.WriteLine();
                                ProgressUtil.RenderConsoleProgress(30, '\u2591', ConsoleColor.Red,
                                    " Error in Migration Initialization ");
                                System.Console.WriteLine("......... \n");
                                ProgressUtil.RenderConsoleProgress(30, '\u2591', ConsoleColor.Red,
                                     retval);
                                System.Console.WriteLine("......... \n");
                                System.Console.WriteLine();
                                return;
                            }
                        }
                        else
                        {

                            ZimbraAPI zimbraAPI = new ZimbraAPI();

                            System.Console.WriteLine();
                            ProgressUtil.RenderConsoleProgress(
                                    30, '\u2591', ConsoleColor.Green,
                                    "Connecting to to Zimbra Server \n   ");
                            System.Console.WriteLine();

                            int stat = zimbraAPI.Logon(
                                    myXmlConfig.ConfigObj.zimbraServer.Hostname,
                                    myXmlConfig.ConfigObj.zimbraServer.Port,
                                    myXmlConfig.ConfigObj.zimbraServer.UserAccount,
                                    myXmlConfig.ConfigObj.zimbraServer.UserPassword, false);

                            if (stat != 0)
                            {
                                zimbraAPI.LastError.Count();

                                System.Console.WriteLine();
                                ProgressUtil.RenderConsoleProgress(
                                        30, '\u2591', ConsoleColor.Red,
                                        "Logon to to Zimbra Server  for userAccount failed " +
                                        myXmlConfig.ConfigObj.zimbraServer.AdminID);
                                System.Console.WriteLine("......... \n");
                                System.Console.WriteLine();
                                Thread.Sleep(2000);
                                // return;
                            }

                        }
                        System.Console.WriteLine();
                        ProgressUtil.RenderConsoleProgress(
                                30, '\u2591', ConsoleColor.Green,
                                " Migration to Zimbra Started  for Profile/PST  " +
                                accountid);
                        System.Console.WriteLine();
                        System.Console.WriteLine();

                      //  Test.test(accountname, TestObj, accountid, importopts, false);
                        var countdownEvent = new CountdownEvent(1);
                        Account userAccts = new Account();

                        userAccts.StartMigration(myXmlConfig.UserList, myXmlConfig.ConfigObj.UserProvision.DestinationDomain, importopts, countdownEvent,TestObj, false, accountname, accountid);
                        // Thread.Sleep(129000);

                        countdownEvent.Wait();
                        Console.WriteLine("Finished."); 


                    }
                    else
                    {
                        System.Console.WriteLine();
                        ProgressUtil.RenderConsoleProgress(30, '\u2591', ConsoleColor.Red,
                            " There are no user accounts to be migrated in the usermap file \n");
                        System.Console.WriteLine();
                    }
                }
            }
            else
            {
                System.Console.WriteLine();
                ProgressUtil.RenderConsoleProgress(30, '\u2591', ConsoleColor.Red,
                    " There are no configuration or usermap files.make sure the xml and CSV files are at temp folder \n");
                System.Console.WriteLine();
            }
            // Thread.Sleep(18000);

            
        }
        else
        {

            if ((userid != "") || (Pstfile != ""))
            {
                       
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

                        string accountname = userid;
                        accountname = accountname + "@" + ZCSHost;
                        string accountid = (Pstfile != "") ? Pstfile : userid;

                        if (userid != "")
                        {
                           /* TestObj.Initalize(myXmlConfig.ConfigObj.zimbraServer.ZimbraHostname,
                                   myXmlConfig.ConfigObj.zimbraServer.Port,
                                   myXmlConfig.ConfigObj.zimbraServer.UserAccount,
                                   myXmlConfig.ConfigObj.OutlookProfile, "",
                                   "");*/
                            string retval = TestObj.GlobalInit(userid, "", "");
                            if (retval.Length > 0)
                            {
                                System.Console.WriteLine();
                                ProgressUtil.RenderConsoleProgress(30, '\u2591', ConsoleColor.Red,
                                    " Error in Migration Initialization ");
                                System.Console.WriteLine("......... \n");
                                ProgressUtil.RenderConsoleProgress(30, '\u2591', ConsoleColor.Red,
                                     retval);
                                System.Console.WriteLine("......... \n");
                                System.Console.WriteLine();
                                return;
                            }
                        }
                        //else
                        {

                            ZimbraAPI zimbraAPI = new ZimbraAPI();

                            System.Console.WriteLine();
                            ProgressUtil.RenderConsoleProgress(
                                    30, '\u2591', ConsoleColor.Green,
                                    "Connecting to to Zimbra Server \n   ");
                            System.Console.WriteLine();

                            int stat = zimbraAPI.Logon(
                                    ZCSHost,
                                    "80",
                                    userid,
                                    "test123", false);

                            if (stat != 0)
                            {
                                zimbraAPI.LastError.Count();

                                System.Console.WriteLine();
                                ProgressUtil.RenderConsoleProgress(
                                        30, '\u2591', ConsoleColor.Red,
                                        "Logon to to Zimbra Server  for userAccount failed " +
                                        userid);
                                System.Console.WriteLine("......... \n");
                                System.Console.WriteLine();
                                Thread.Sleep(2000);
                                // return;
                            }

                        }
                        System.Console.WriteLine();
                        ProgressUtil.RenderConsoleProgress(
                                30, '\u2591', ConsoleColor.Green,
                                " Migration to Zimbra Started  for Profile/PST  " +
                                accountid);
                        System.Console.WriteLine();
                        System.Console.WriteLine();

                      //  Test.test(accountname, TestObj, accountid, importopts, false);
                        var countdownEvent = new CountdownEvent(1);
                        Account userAccts = new Account();

                        MVVM.Model.Users User = new MVVM.Model.Users();
                User.UserName = userid;
                
                List<MVVM.Model.Users> users = new List<MVVM.Model.Users>();
                        users.Add(User);
                        userAccts.StartMigration(users, ZCSHost, importopts, countdownEvent,TestObj, false, accountname, accountid);
                        // Thread.Sleep(129000);

                        countdownEvent.Wait();
                        Console.WriteLine("Finished."); 


                    }

            
            System.Console.WriteLine();
            ProgressUtil.RenderConsoleProgress(30, '\u2591', ConsoleColor.Red,
                " Make sure the correct arguments (2) are passed \n");
            System.Console.WriteLine();
        }
    }
}
}
