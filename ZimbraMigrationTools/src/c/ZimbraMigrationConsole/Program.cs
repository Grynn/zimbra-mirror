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
class Program
{
    static void Main(string[] args)
    {
        Migration Test = new Migration();

        CssLib.CSMigrationwrapper TestObj = new CSMigrationwrapper();
        // Test.test();
        // Test.MigrationClient();
        if (args.Count() == 2)
        {
            string ConfigXmlFile = args[0];
            string UserMapFile = args[1];
            if (File.Exists(ConfigXmlFile) && File.Exists(UserMapFile))
            {
                // if (File.Exists(@"C:\Temp\ZimbraAdminOverView.xml") && File.Exists(@"C:\Temp\UserMap.csv"))

                // XmlConfig myXmlConfig = new XmlConfig(@"C:\Temp\ZimbraAdminOverview.xml", @"C:\Temp\UserMap.csv");
                XmlConfig myXmlConfig = new XmlConfig(ConfigXmlFile, UserMapFile);

                myXmlConfig.InitializeConfig();

                myXmlConfig.GetUserList();
                if (myXmlConfig.UserList.Count > 0)
                {
                    if (myXmlConfig.ConfigObj.OutlookProfile != "")
                    {
                        // profile migration
                        TestObj.Initalize(myXmlConfig.ConfigObj.zimbraServer.ZimbraHostname,
                                myXmlConfig.ConfigObj.zimbraServer.Port,
                                myXmlConfig.ConfigObj.zimbraServer.ZimbraAdminID,
                                myXmlConfig.ConfigObj.OutlookProfile, "",
                                "");
                    }
                    else
                    {
                        TestObj.Initalize(myXmlConfig.ConfigObj.zimbraServer.ZimbraHostname,
                                myXmlConfig.ConfigObj.zimbraServer.Port,
                                myXmlConfig.ConfigObj.zimbraServer.ZimbraAdminID, "",
                                myXmlConfig.ConfigObj.mailServer.SourceHostname,
                                myXmlConfig.ConfigObj.mailServer.SourceAdminID);
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
                        ProgressUtil.RenderConsoleProgress(
                                30, '\u2591', ConsoleColor.Green,
                                "Connecting to to Zimbra Server \n   ");
                        System.Console.WriteLine();

                        ZimbraAPI zimbraAPI = new ZimbraAPI();

                        int stat = zimbraAPI.Logon(
                                myXmlConfig.ConfigObj.zimbraServer.ZimbraHostname,
                                myXmlConfig.ConfigObj.zimbraServer.Port,
                                myXmlConfig.ConfigObj.zimbraServer.ZimbraAdminID,
                                myXmlConfig.ConfigObj.zimbraServer.ZimbraAdminPwd, true);
                        if (stat != 0)
                        {
                            zimbraAPI.LastError.Count();

                            System.Console.WriteLine();
                            ProgressUtil.RenderConsoleProgress(
                                    30, '\u2591', ConsoleColor.Red,
                                    "Logon to to Zimbra Server  for adminAccount failed " +
                                    myXmlConfig.ConfigObj.zimbraServer.ZimbraAdminID);
                            System.Console.WriteLine("......... \n");
                            System.Console.WriteLine();
                            Thread.Sleep(2000);
                            // return;
                        }
                        // userAcct.InitializeMigration(myXmlConfig.ConfigObj.zimbraServer.ZimbraHostname, myXmlConfig.ConfigObj.zimbraServer.Port, myXmlConfig.ConfigObj.zimbraServer.ZimbraAdminID,user.UserName);

                        string acctName = user.UserName + '@' +
                                myXmlConfig.ConfigObj.UserProvision.Domain;
                        if (zimbraAPI.GetAccount(acctName) == 0)
                        {
                            System.Console.WriteLine();
                            System.Console.WriteLine();
                            ProgressUtil.RenderConsoleProgress(
                                    30, '\u2591', ConsoleColor.Green,
                                    " Migration to Zimbra Started  for UserAccount " +
                                    user.UserName);
                            System.Console.WriteLine();
                            System.Console.WriteLine();

                            // userAcct.StartMigration(user.UserName, myXmlConfig.ConfigObj.importOptions.Mail.ToString());
                            Test.test(acctName, TestObj, user.UserName);

                            // /////////////////

                            // ///////////////////
                            Thread.Sleep(15000);
                        }
                        else
                        {
                            System.Console.WriteLine();
                            ProgressUtil.RenderConsoleProgress(
                                    30, '\u2591', ConsoleColor.Yellow,
                                    " User is not provisioned on Zimbra Server " +
                                    user.UserName);

                            System.Console.WriteLine();
                            System.Console.WriteLine();

                            ProgressUtil.RenderConsoleProgress(
                                    30, '\u2591', ConsoleColor.Green, " Provisioning user" +
                                    user.UserName);
                            System.Console.WriteLine();
                            System.Console.WriteLine();
                            if (zimbraAPI.CreateAccount(acctName,
                                        myXmlConfig.ConfigObj.UserProvision.DefaultPWD,
                                        myXmlConfig.ConfigObj.UserProvision.COS) == 0)
                            {
                                System.Console.WriteLine();
                                ProgressUtil.RenderConsoleProgress(
                                        30, '\u2591', ConsoleColor.Green,
                                        " Provisioning useraccount success " + user.UserName);

                                System.Console.WriteLine();
                                System.Console.WriteLine();
                                ProgressUtil.RenderConsoleProgress(
                                        30, '\u2591', ConsoleColor.Green,
                                        " Migration to Zimbra Started  for UserAccount  " +
                                        user.UserName);
                                System.Console.WriteLine();
                                System.Console.WriteLine();
                                // userAcct.StartMigration(user.UserName, myXmlConfig.ConfigObj.importOptions.Mail.ToString());
                                Test.test(acctName, TestObj, user.UserName);
                                System.Console.WriteLine("......... \n");
                                Thread.Sleep(9000);
                            }
                            else
                            {
                                System.Console.WriteLine();

                                ProgressUtil.RenderConsoleProgress(
                                        30, '\u2591', ConsoleColor.Red,
                                        " error provisioning user " + user.UserName);
                                System.Console.WriteLine();
                                System.Console.WriteLine();
                            }
                        }
                        string final = user.StatusMessage;
                    }
                }
                else
                {
                    System.Console.WriteLine();
                    ProgressUtil.RenderConsoleProgress(
                            30, '\u2591', ConsoleColor.Red,
                            " There are no user accounts to be migrated in the usermap file \n");
                    System.Console.WriteLine();
                }
            }
            else
            {
                System.Console.WriteLine();
                ProgressUtil.RenderConsoleProgress(
                        30, '\u2591', ConsoleColor.Red,
                        " There are no configuration or usermap files.make sure the xml and CSV files are at temp folder \n");
                System.Console.WriteLine();
            }
            // Thread.Sleep(18000);
        }
        else
        {
            System.Console.WriteLine();
            ProgressUtil.RenderConsoleProgress(
                    30, '\u2591', ConsoleColor.Red,
                    " Make sure the correct arguments (2) are passed \n");
            System.Console.WriteLine();
        }
    }
}
}
