using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using CssLib;

namespace ZimbraMigrationConsole
{
    class Program
    {
        static void Main(string[] args)
        {
            CssLib.CSMigrationwrapper TestObj = new CSMigrationwrapper();
            TestObj.ConfigXMLFile = @"C:\Temp\ZimbraAdminOverview.xml";
            TestObj.UserMapFile = @"UserMap3.csv";
            TestObj.MailClient = "MAPI";
            TestObj.Initalize();
        }
    }
}
