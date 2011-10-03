using System.ComponentModel;
using System.Threading;
using System;

namespace ZimbraMigrationConsole
{
class Account: BackgroundWorker
{
    string m_AccountName;
    public string AccountName {
        get { return m_AccountName; }
        set { m_AccountName = value; }
    }
    string m_AccountStatus;
    public string AccountStatus {
        get { return m_AccountStatus; }
        set { m_AccountStatus = value; }
    }
    string m_AccountOptions;
    public string AccountOptions {
        get { return m_AccountOptions; }
        set { m_AccountOptions = value; }
    }
    long highestPercentageReached;
    public long HighestPercentageReached {
        get { return highestPercentageReached; }
        set { highestPercentageReached = value; }
    }
    CssLib.CSMigrationwrapper TestObj;
    MVVM.Model.Users Currentuser;
    private string migrateOptions;
    public string MigrateOptions {
        get { return migrateOptions; }
        set { migrateOptions = value; }
    }
    public void InitializeMigration(string Hostname, string Port, String Adminacct,
            string UserID)
    {
        TestObj = new CssLib.CSMigrationwrapper();
        TestObj.MailClient = "MAPI";
        // TestObj.Initalize(Hostname, Port,Adminacct,UserID);
        Currentuser = new MVVM.Model.Users();
    }
    public void StartMigration(string AcctName, string MailOptions)
    {
        // Account myAccount = new Account();
        AccountName = AcctName;

        Currentuser.UserName = AcctName;
        DoWork +=
                new DoWorkEventHandler(accountToMigrate_DoWork);
        RunWorkerCompleted +=
                new RunWorkerCompletedEventHandler(
                accountToMigrate_RunWorkerCompleted);
        ProgressChanged +=
                new ProgressChangedEventHandler(
                accountToMigrate_ProgressChanged);
        WorkerReportsProgress = true;
        WorkerSupportsCancellation = true;
        MigrateOptions = MailOptions;
        RunWorkerAsync(MigrateOptions);
    }
    private void accountToMigrate_DoWork(object sender, System.ComponentModel.DoWorkEventArgs e)
    {
        BackgroundWorker worker = sender as BackgroundWorker;

        // Assign the result of the computation
        // to the Result property of the DoWorkEventArgs
        // object. This is will be available to the
        // RunWorkerCompleted eventhandler.
        // e.Result = Accounworker, e);
        if (worker.CancellationPending)
        {
            e.Cancel = true;
        }
        else
        {
            TestObj.Migrate(MigrateOptions);
            for (int i = 1; (i <= 10); i++)
            {
                if ((worker.CancellationPending == true))
                {
                    e.Cancel = true;
                    break;
                }
                else
                {
                    // Perform a time consuming operation and report progress.
                    // TestObj.Migrate(MigrateOptions);

                    System.Threading.Thread.Sleep(700);
                    worker.ReportProgress((i * 10));
                }
            }
        }
    }
    private void accountToMigrate_ProgressChanged(object sender, ProgressChangedEventArgs e)
    {
        AccountStatus = e.ProgressPercentage.ToString();
        if (e.ProgressPercentage == 10)
        {
            // System.Console.WriteLine("Migrating messages For UserAccount   " + AccountName.ToString());
            System.Console.WriteLine();

            ProgressUtil.RenderConsoleProgress(
                    30, '\u2591', ConsoleColor.Yellow,
                    "Migrating messages For UserAccount   " +
                    AccountName.ToString());

            Currentuser.StatusMessage = "Migrating messages For UserAccount   " +
                    AccountName.ToString();
            System.Console.WriteLine();
            System.Console.WriteLine();
        }
        if (e.ProgressPercentage == 40)
        {
            // System.Console.WriteLine("Migrating appointments For UserAccount   " + AccountName.ToString());
            System.Console.WriteLine();
            ProgressUtil.RenderConsoleProgress(
                    40, '\u2591', ConsoleColor.Green,
                    "Migrating appointments For UserAccount   " +
                    AccountName.ToString());
            Currentuser.StatusMessage = "Migrating appointments For UserAccount   " +
                    AccountName.ToString();
            System.Console.WriteLine();
            System.Console.WriteLine();
        }
        if (e.ProgressPercentage == 60)
        {
            // System.Console.WriteLine("Migrating contacts For UserAccount   " + AccountName.ToString());
            System.Console.WriteLine();
            ProgressUtil.RenderConsoleProgress(
                    60, '\u2591', ConsoleColor.Yellow,
                    "Migrating contacts For UserAccount   " +
                    AccountName.ToString());
            Currentuser.StatusMessage = "Migrating Contacts For UserAccount   " +
                    AccountName.ToString();
            System.Console.WriteLine();
            System.Console.WriteLine();
        }
        if (e.ProgressPercentage == 80)
        {
            // System.Console.WriteLine("Migrating rules For UserAccount   " + AccountName.ToString());
            System.Console.WriteLine();
            ProgressUtil.RenderConsoleProgress(
                    60, '\u2591', ConsoleColor.Green, "Migrating Rules For UserAccount   " +
                    AccountName.ToString());
            Currentuser.StatusMessage = "Migrating  Rules For UserAccount   " +
                    AccountName.ToString();
            System.Console.WriteLine();
            System.Console.WriteLine();
        }
    }
    private void accountToMigrate_RunWorkerCompleted(object sender,
            RunWorkerCompletedEventArgs e)
    {
        // First, handle the case where an exception was thrown.
        if (e.Error != null)
        {
            AccountStatus = (e.Error.Message);
        }
        else if (e.Cancelled)
        {
            // Next, handle the case where the user canceled
            // the operation.
            // Note that due to a race condition in
            // the DoWork event handler, the Cancelled
            // flag may not have been set, even though
            // CancelAsync was called.
            AccountStatus = "Canceled";
        }
        else
        {
            // Finally, handle the case where the operation
            // succeeded.
            AccountStatus = "Completed";    // e.Result.ToString();
        }
    }
}
}
