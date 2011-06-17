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
            /*CssLib.CSMigrationwrapper TestObj = new CSMigrationwrapper();
           
            TestObj.ConfigXMLFile = @"C:\Temp\ZimbraAdminOverview.xml";
            TestObj.UserMapFile = @"UserMap3.csv";
            TestObj.MailClient = "MAPI";*/


            

            

            


            XmlConfig myXmlConfig = new XmlConfig(@"C:\Temp\ZimbraAdminOverview.xml",@"C:\Temp\UserMap.csv");

            myXmlConfig.InitializeConfig();

           
           /* TestObj.Initalize(myXmlConfig.ConfigObj.zimbraServer.HostName,myXmlConfig.ConfigObj.zimbraServer.Port,myXmlConfig.ConfigObj.zimbraServer.AdminAccount);
           
            Thread.Sleep(500);*/
           // string test = myXmlConfig.ConfigObj.zimbraServer.AdminAccount;

            myXmlConfig.GetUserList();
            foreach( MVVM.Model.Users user in  myXmlConfig.UserList)
            {
                Account userAcct = new Account();
                
                System.Console.WriteLine("Connecting to to Zimbra Server \n");
                System.Console.WriteLine("......... \n");
                userAcct.InitializeMigration(myXmlConfig.ConfigObj.zimbraServer.HostName,myXmlConfig.ConfigObj.zimbraServer.Port,myXmlConfig.ConfigObj.zimbraServer.AdminAccount);
                System.Console.WriteLine("Migration to Zimbra Started  for UserAccount " + user.UserName);
                userAcct.StartMigration(user.UserName, myXmlConfig.ConfigObj.importOptions.Mail.ToString());
                System.Console.WriteLine("......... \n");
                Thread.Sleep(9000);
                
            }
            //Thread.Sleep(18000);
        }
         
        }
           
         



        }
 

