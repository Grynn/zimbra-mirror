using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using CssLib;
using System.Threading;
using MVVM;
using System.IO;
using System.ComponentModel;

namespace ZimbraMigrationConsole
{
    class Program
    {
        static void Main(string[] args)
        {
            if (File.Exists(@"C:\Temp\ZimbraAdminOverView.xml") && File.Exists(@"C:\Temp\UserMap.csv"))
            {

                XmlConfig myXmlConfig = new XmlConfig(@"C:\Temp\ZimbraAdminOverview.xml", @"C:\Temp\UserMap.csv");

                myXmlConfig.InitializeConfig();

                myXmlConfig.GetUserList();
                if (myXmlConfig.UserList.Count > 0)
                {
                    foreach (MVVM.Model.Users user in myXmlConfig.UserList)
                    {
                        Account userAcct = new Account();

                        System.Console.WriteLine("Connecting to to Zimbra Server \n");
                        System.Console.WriteLine("......... \n");
                        ZimbraAPI zimbraAPI = new ZimbraAPI();

                        int stat = zimbraAPI.Logon(myXmlConfig.ConfigObj.zimbraServer.HostName, myXmlConfig.ConfigObj.zimbraServer.Port, myXmlConfig.ConfigObj.zimbraServer.AdminAccount, myXmlConfig.ConfigObj.zimbraServer.AdminPassword, true);
                        if (stat != 0)
                        {
                            zimbraAPI.LastError.Count();
                            System.Console.WriteLine("Logon to to Zimbra Server  for adminAccount failed ");
                        }

                        userAcct.InitializeMigration(myXmlConfig.ConfigObj.zimbraServer.HostName, myXmlConfig.ConfigObj.zimbraServer.Port, myXmlConfig.ConfigObj.zimbraServer.AdminAccount);


                        string acctName = user.UserName + '@' + myXmlConfig.ConfigObj.UserProvision.Domain;
                        if (zimbraAPI.GetAccount(acctName) == 0)
                        {
                            System.Console.WriteLine("Migration to Zimbra Started  for UserAccount " + user.UserName);
                            userAcct.StartMigration(user.UserName, myXmlConfig.ConfigObj.importOptions.Mail.ToString());
                            System.Console.WriteLine("......... \n");
                            Thread.Sleep(9000);
                        }
                        else
                        {

                            System.Console.WriteLine("User is not provisioned on Zimbra Server " + user.UserName);
                            System.Console.WriteLine("provisioning user " + user.UserName);
                            if (zimbraAPI.CreateAccount(acctName, myXmlConfig.ConfigObj.UserProvision.DefaultPWD, myXmlConfig.ConfigObj.UserProvision.COS) == 0)
                            {
                                System.Console.WriteLine("provisioning user success " + user.UserName);
                                System.Console.WriteLine("Migration to Zimbra Started  for UserAccount " + user.UserName);
                                userAcct.StartMigration(user.UserName, myXmlConfig.ConfigObj.importOptions.Mail.ToString());
                                System.Console.WriteLine("......... \n");
                                Thread.Sleep(9000);
                            }
                            else
                                System.Console.WriteLine("error provisioning user " + user.UserName);



                        }

                    }
                }
                else
                {
                    System.Console.WriteLine("There are no user accounts to be migrated in the usermap file ");
                }
            }
            else
            {
                System.Console.WriteLine("There are no configuration or usermap files.make sure the xml and CSV files are at temp folder ");
            }
             //Thread.Sleep(18000);
        }
         
        }
           
         



        }
 

