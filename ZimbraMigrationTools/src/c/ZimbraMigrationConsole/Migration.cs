using CssLib;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System;

namespace ZimbraMigrationConsole
{
class Migration
{
    public void test(string accountname, object Test, string accountid, MigrationOptions opts)
    {
        MigrationAccount MyAcct = new MigrationAccount();

        MyAcct.Accountname = accountname;
        MyAcct.AccountID = accountid;
        MyAcct.OnChanged +=
                new MigrationObjectEventHandler(i_OnChanged1);

        MigrationFolder MyFolder = new MigrationFolder();
        MyFolder.OnChanged += new MigrationObjectEventHandler(i_OnChanged12);

        MyAcct.migrationFolder = MyFolder;

        /* CSMigrationwrapper test = new CSMigrationwrapper();
         * test.StartMigration(MyAcct);*/

        

        CSMigrationwrapper test = (CSMigrationwrapper)Test;
        //test.StartMigration(MyAcct,opts);
       test.StartMigration(MyAcct, opts);
    }
    // the following method is not been used can comment it for now

    /*
     * public void MigrationClient()
     * {
     *
     *  MigrationAccount MyAcct = new MigrationAccount();
     *  MyAcct.OnChanged +=
     *     new MigrationObjectEventHandler(i_OnChanged1);
     *
     *
     *
     *  MyAcct.Accountname = "test";
     *
     *  foreach (MigrationFolder mt in MyAcct.migrationFolders)
     *  {
     *      mt.OnChanged +=
     *           new MigrationObjectEventHandler(i_OnChanged12);
     *  }
     *
     *  MigrationFolder MigFolder= new MigrationFolder();
     *  MigFolder.OnChanged +=
     *           new MigrationObjectEventHandler(i_OnChanged12);
     * MigFolder.FolderName = "Contacts";
     * MigFolder.TotalCountOFItems = 252;
     * MigFolder.CurrentCountOFItems = 0;
     *
     * MyAcct.migrationFolders.Insert(0, MigFolder);
     *
     *  MigrationFolder MigFolder1= new MigrationFolder();
     *  MigFolder1.OnChanged +=
     *           new MigrationObjectEventHandler(i_OnChanged12);
     * MigFolder1.FolderName = "Mail";
     * MigFolder1.TotalCountOFItems = 2000;
     * MigFolder1.CurrentCountOFItems = 0;
     *
     *  MyAcct.migrationFolders.Insert(1, MigFolder1);
     *
     *
     *
     * foreach(MigrationFolder mt in MyAcct.migrationFolders)
     * {
     *     mt.OnChanged +=
     *          new MigrationObjectEventHandler(i_OnChanged12);
     * }
     *
     * // MyAcct.migrationFolders = new MigrationFolder();
     * /*  MyAcct.migrationFolders.FolderName = "Contacts";
     *   MyAcct.migrationFolders.TotalCountOFItems = 252;
     *   MyAcct.migrationFolders.CurrentCountOFItems = 0;*/                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                             // ..this works

    // MyAcct.migrationFolders[0] = MigFolder;

    /*  MyAcct.OnChanged +=
     *    new MigrationObjectEventHandler(i_OnChanged1);*/

    // MyAcct.dateRaised = System.DateTime.Now;
    // }
    // the above method is not been used for now

    public void i_OnChanged1(object sender, MigrationObjectEventArgs e)
    {
        MigrationAccount i = (MigrationAccount)sender;
        string Message =
                " Migration started for user : {0} with TotalContacts  {1} ,TotalMails {2}, TotalRules {3}";

        Console.WriteLine(String.Format(Message, i.Accountname, i.TotalNoContacts,
                    i.TotalNoMails, i.TotalNoRules));
    }
    public void i_OnChanged12(object sender, MigrationObjectEventArgs e)
    {
        MigrationFolder i = (MigrationFolder)sender;

        string Message = "Migrating {0} folder \n " +
                "Migrating........................... {1} of {2} {0}";

        Console.WriteLine(String.Format(Message, i.FolderName, i.CurrentCountOFItems,
                    i.TotalCountOFItems));
    }
}
}
