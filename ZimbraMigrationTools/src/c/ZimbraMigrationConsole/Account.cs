using System;
using System.ComponentModel;

namespace ZimbraMigrationConsole
{
    class Account : BackgroundWorker
    {
        string m_AccountName;

        public string AccountName
        {
            get { return m_AccountName; }
            set { m_AccountName = value; }
        }
        string m_AccountStatus;

        public string AccountStatus
        {
            get { return m_AccountStatus; }
            set { m_AccountStatus = value; }
        }
        string m_AccountOptions;

        public string AccountOptions
        {
            get { return m_AccountOptions; }
            set { m_AccountOptions = value; }
        }

        long highestPercentageReached;

        public long HighestPercentageReached
        {
            get { return highestPercentageReached; }
            set { highestPercentageReached = value; }
        }

        CssLib.CSMigrationwrapper TestObj;
        private string migrateOptions;

        public string MigrateOptions
        {
            get { return migrateOptions; }
            set { migrateOptions = value; }
        }
        public void InitializeMigration(string Hostname,string Port,String Adminacct)
        {
            TestObj = new CssLib.CSMigrationwrapper();
            TestObj.MailClient = "MAPI";
            TestObj.Initalize(Hostname, Port,Adminacct);


        }
        public void StartMigration(string AcctName,string MailOptions)
        {

          //  Account myAccount = new Account();
            AccountName = AcctName;
            DoWork +=
                new DoWorkEventHandler(accountToMigrate_DoWork);
            RunWorkerCompleted +=
                new RunWorkerCompletedEventHandler(
            accountToMigrate_RunWorkerCompleted);
            ProgressChanged +=
                new ProgressChangedEventHandler(
            accountToMigrate_ProgressChanged);
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
                //MailWrapper.ImportMailOptions(ImportOptions.Mail.ToString());

                // Report progress as a percentage of the total task.
                int percentcomplete = (int)HighestPercentageReached + 5;
                HighestPercentageReached = percentcomplete;
                worker.ReportProgress(percentcomplete);
            }


        }

        private void accountToMigrate_ProgressChanged(object sender,
            ProgressChangedEventArgs e)
        {
            AccountStatus = e.ProgressPercentage.ToString();
        }

        private void accountToMigrate_RunWorkerCompleted(
            object sender, RunWorkerCompletedEventArgs e)
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
                AccountStatus = e.Result.ToString();
            }


        }


    }
}