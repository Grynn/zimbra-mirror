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

    string m_AccountID;
    public string AccountID
    {
        get { return m_AccountID; }
        set { m_AccountID = value; }
    }
    
    CssLib.CSMigrationWrapper TestObj;

    MVVM.Model.Users Currentuser;
    CssLib.MigrationOptions Mailoptions;
    int num = 0;
    int Numoferrors = 0;
    int NumofWarns = 0;

    bool serverMigration = true;

    CountdownEvent countdown;

    public CountdownEvent Countdown
    {
        get { return countdown; }
        set { countdown = value; }
    }
   
    
    private volatile bool _shouldStop;

    public void RequestStop()
    {
        _shouldStop = true;
    }

    
    public void StartMigration(System.Collections.Generic.List<MVVM.Model.Users> userlist, string Domainname, CssLib.MigrationOptions MailOptions, CountdownEvent countdown, object wrapper, int maxThreads = 2, bool ServerMigrationflag = true, string pstaccountname = "", string pstfile = "")
    {
        int number = 0;
       
        if (ServerMigrationflag)
        {
            // InitializeBackgoundWorkers();

            Account[] AccountArray = new Account[maxThreads];
            for (int j = 0; j < maxThreads; j++)
            {
                AccountArray[j] = new Account();
            }
            

            for (int f = 0; f < userlist.Count; f++)
            {
                //Use the thread array to process ech iteration  
                //choose the first unused thread.    

                Account myAccount = new Account();
                string uname = (userlist[f].MappedName != "") ? userlist[f].MappedName : userlist[f].UserName;

                myAccount.AccountName = uname +"@" + Domainname;// AcctName;
                myAccount.AccountID = userlist[f].UserName;
                myAccount.Countdown = countdown;
                Currentuser = new MVVM.Model.Users();
                Currentuser.UserName = userlist[f].UserName;
                myAccount.Currentuser = Currentuser;
                myAccount.TestObj = (CssLib.CSMigrationWrapper)wrapper;

                myAccount.serverMigration = ServerMigrationflag;
                myAccount.Mailoptions = MailOptions;
                number = number + 1;
                myAccount.num = number;


                bool fileProcessed = false;
                while ((!fileProcessed) && (!_shouldStop))
                {
                    for (int threadNum = 0; threadNum < maxThreads; threadNum++)
                    {
                        if (!AccountArray[threadNum].IsBusy)
                        {   // This thread is available     

                            System.Console.WriteLine("Starting worker thread: " + threadNum + "account" + myAccount.AccountName);

                            AccountArray[threadNum] = myAccount;
                            AccountArray[threadNum].DoWork += new DoWorkEventHandler(accountToMigrate_DoWork);
                            AccountArray[threadNum].RunWorkerCompleted += new RunWorkerCompletedEventHandler(accountToMigrate_RunWorkerCompleted);
                            //AccountArray[threadNum].ProgressChanged += new ProgressChangedEventHandler(accountToMigrate_ProgressChanged);
                            AccountArray[threadNum].WorkerReportsProgress = true; 
                            AccountArray[threadNum].WorkerSupportsCancellation = true;
                            AccountArray[threadNum].RunWorkerAsync(myAccount);

                            fileProcessed = true;
                            break;
                        }
                    }
                    //If all threads are being used, sleep awhile before checking again  
                    if (!fileProcessed)
                    {
                         Thread.Sleep(500);
                    }
                }
                if (_shouldStop)
                {
                    
                    for (int i = 0; i < maxThreads; i++)
                    {
                        System.Console.WriteLine("cancelling in main callback");
                        countdown.Signal();
                        AccountArray[i].CancelAsync();
                    }
                    
                }
            }
        }
        else
        {
            Account myAccount = new Account();
            myAccount.AccountName = pstaccountname;// AcctName;
            myAccount.AccountID = pstfile;
            myAccount.Countdown = countdown;
            Currentuser = new MVVM.Model.Users();
            Currentuser.UserName = pstfile;
            myAccount.Currentuser = Currentuser;

            myAccount.serverMigration = ServerMigrationflag;
            myAccount.TestObj = (CssLib.CSMigrationWrapper)wrapper;
            number = number + 1;
            myAccount.num = number;

            myAccount.DoWork += new DoWorkEventHandler(accountToMigrate_DoWork);
            myAccount.RunWorkerCompleted += new RunWorkerCompletedEventHandler(
                 accountToMigrate_RunWorkerCompleted);
            //myAccount.ProgressChanged += new ProgressChangedEventHandler(accountToMigrate_ProgressChanged);
            myAccount.WorkerReportsProgress = true;
            myAccount.WorkerSupportsCancellation = true;
            myAccount.Mailoptions = MailOptions;
            myAccount.RunWorkerAsync(myAccount);
        }
              
        
    }

    private void accountToMigrate_DoWork(object sender, System.ComponentModel.DoWorkEventArgs e)
    {

       
            // Get argument from DoWorkEventArgs argument.  Can use any type here with cast   
            // int myProcessArguments = (int)e.Argument;      
            // "ProcessFile" is the name of my method that does the main work.  Replace with your own method!       
            // Can return reulsts from this method, i.e. a status (OK, FAIL etc)   
            //e.Result = ProcessFile(myProcessArgument); 
            BackgroundWorker worker = sender as BackgroundWorker;

            // Assign the result of the computation
            // to the Result property of the DoWorkEventArgs
            // object. This is will be available to the
            // RunWorkerCompleted eventhandler.
            // e.Result = Accounworker, e);
            //int num = 0;

            Account argumentTest = e.Argument as Account;
            //while (!_shouldStop)
            {

            CssLib.MigrationAccount MyAcct = new CssLib.MigrationAccount();
            MyAcct.AccountName = argumentTest.AccountName;
            MyAcct.AccountID = argumentTest.AccountID;


            MyAcct.AccountNum = argumentTest.num;

            MyAcct.OnChanged += new CssLib.MigrationObjectEventHandler(Acct_OnAcctChanged);

            CssLib.MigrationFolder MyFolder = new CssLib.MigrationFolder();

            //MyFolder.AccountNum = argumentTest.num;
            MyFolder.AccountID = argumentTest.AccountID;
            MyFolder.OnChanged += new CssLib.MigrationObjectEventHandler(Folder_OnChanged);

            MyAcct.migrationFolder = MyFolder;
                

            CssLib.CSMigrationWrapper mw = argumentTest.TestObj;

            if (argumentTest.CancellationPending)
            {
                e.Cancel = true;
            }
            else
            {
                // TestObj.Migrate(MigrateOptions);

                mw.StartMigration(MyAcct, argumentTest.Mailoptions, argumentTest.serverMigration);
               
            }
        }
        if((_shouldStop))
        {
        worker.CancelAsync();
        argumentTest.CancelAsync();
        argumentTest.countdown.Signal();
        e.Cancel = true;
        }
    }

    public void Acct_OnAcctChanged(object sender, CssLib.MigrationObjectEventArgs e)
    {
       // while (!_shouldStop)
        {
            //string msg = "";
            CssLib.MigrationAccount a = (CssLib.MigrationAccount)sender;

            if (e.PropertyName == "TotalItems")
            {
                /*System.Console.WriteLine();

                ProgressUtil.RenderConsoleProgress(30, '\u2591', ConsoleColor.Yellow,
                    "TotalItems to Migrate For UserAccount   " + a.AccountID.ToString() + " is " + e.NewValue.ToString());
                System.Console.WriteLine();*/

                Currentuser.StatusMessage = "TotalItems to Migrate For UserAccount   " + a.AccountID + " is " + e.NewValue.ToString();
                /*System.Console.WriteLine();
                System.Console.WriteLine();*/

            }
            if (e.PropertyName == "TotalErrors")
            {

                Numoferrors = (int)a.TotalErrors + 1;      // this happens first
                System.Console.WriteLine();

                ProgressUtil.RenderConsoleProgress(30, '\u2591', ConsoleColor.Yellow,
                    "TotalErrors For UserAccount   " + a.AccountID.ToString() + Numoferrors.ToString());

                Currentuser.StatusMessage = "TotalErrors For UserAccount   " + a.AccountID.ToString() + Numoferrors.ToString();
                System.Console.WriteLine();
                System.Console.WriteLine();

            }
            else if (e.PropertyName == "TotalWarnings")
            {
                NumofWarns = (int)a.TotalWarnings + 1;
                System.Console.WriteLine();

                ProgressUtil.RenderConsoleProgress(30, '\u2591', ConsoleColor.Yellow,
                    "TotalWarnings For UserAccount   " + a.AccountID.ToString() + NumofWarns.ToString());

                Currentuser.StatusMessage = "TotalWarnings For UserAccount   " + a.AccountID.ToString() + NumofWarns.ToString();
                System.Console.WriteLine();
                System.Console.WriteLine();

            }
            else
            {
               /* System.Console.WriteLine();

                ProgressUtil.RenderConsoleProgress(30, '\u2591', ConsoleColor.Yellow,
                    "TotalItems to Migrate For UserAccount   " + a.AccountName.ToString() + " is " + e.NewValue.ToString());
                System.Console.WriteLine();

                msg = "Begin {0} Migration";

                string msgF = String.Format(msg, a.AccountName);
                System.Console.WriteLine(msgF);*/

            }
        }
       
    }

    public void Folder_OnChanged(object sender, CssLib.MigrationObjectEventArgs e)
    {
        //while (!_shouldStop)
        {
            CssLib.MigrationFolder f = (CssLib.MigrationFolder)sender;
          

            if (e.PropertyName == "CurrentCountOfItems")
            {
                if (f.FolderName != null)
                {
                    if (e.NewValue.ToString() != "0")
                    {
                        string msg1 = "{0} of {1} for account" + f.AccountNum.ToString();
                        string msgF = String.Format(msg1, f.CurrentCountOfItems, f.TotalCountOfItems);
                        System.Console.WriteLine(msgF);

                    }
                }
            }
            if (e.PropertyName == "TotalCountOfItems")      // finish up with the last folder
            {
                if (f.FolderName != null)
                {
                    string msg2 = "{0} of {1}";
                    string msgF = String.Format(msg2, f.CurrentCountOfItems, f.TotalCountOfItems);

                    System.Console.WriteLine(msgF);

                }
            }
            if (e.PropertyName == "FolderName")
            {
                if (e.NewValue != null)
                {
                    string folderName = e.NewValue.ToString();
                    //  string folderType = GetFolderTypeForUserResults(f.FolderView);
                    string msg3 = "Migrating {0}" + "For " + f.AccountID.ToString();

                    /*ar.PBMsgValue = */
                    string msgF = String.Format(msg3, folderName);
                    System.Console.WriteLine(msgF);
                }
                if (_shouldStop)
                {
                    System.Console.WriteLine();
                    System.Console.WriteLine();
                    ProgressUtil.RenderConsoleProgress(30, '\u2591', ConsoleColor.Red,
                    "Migration For UserAccount    Cancelled");

                    System.Console.WriteLine();
                    System.Console.WriteLine();
                    Countdown.Signal();
                    CancelAsync();
                    Thread.CurrentThread.Abort();
                   
                }
            }
        }
        
    }

    /*private void accountToMigrate_ProgressChanged(object sender, ProgressChangedEventArgs e)
    {
        Account argumentTest = sender as Account;
        argumentTest.AccountStatus = e.ProgressPercentage.ToString();
        if (e.ProgressPercentage == 10)
        {
            // System.Console.WriteLine("Migrating messages For UserAccount   " + AccountName.ToString());
            System.Console.WriteLine();

            ProgressUtil.RenderConsoleProgress(30, '\u2591', ConsoleColor.Yellow,
                "Migrating messages For UserAccount   " + argumentTest.AccountName.ToString());

            Currentuser.StatusMessage = "Migrating messages For UserAccount   " +
                argumentTest.AccountName.ToString();
            System.Console.WriteLine();
            System.Console.WriteLine();
        }
        if (e.ProgressPercentage == 40)
        {
            // System.Console.WriteLine("Migrating appointments For UserAccount   " + AccountName.ToString());
            System.Console.WriteLine();
            ProgressUtil.RenderConsoleProgress(40, '\u2591', ConsoleColor.Green,
                "Migrating appointments For UserAccount   " + argumentTest.AccountName.ToString());
            Currentuser.StatusMessage = "Migrating appointments For UserAccount   " +
                argumentTest.AccountName.ToString();
            System.Console.WriteLine();
            System.Console.WriteLine();
        }
        if (e.ProgressPercentage == 60)
        {
            // System.Console.WriteLine("Migrating contacts For UserAccount   " + AccountName.ToString());
            System.Console.WriteLine();
            ProgressUtil.RenderConsoleProgress(60, '\u2591', ConsoleColor.Yellow,
                "Migrating contacts For UserAccount   " + argumentTest.AccountName.ToString());
            Currentuser.StatusMessage = "Migrating Contacts For UserAccount   " +
                argumentTest.AccountName.ToString();
            System.Console.WriteLine();
            System.Console.WriteLine();
        }
        if (e.ProgressPercentage == 80)
        {
            // System.Console.WriteLine("Migrating rules For UserAccount   " + AccountName.ToString());
            System.Console.WriteLine();
            ProgressUtil.RenderConsoleProgress(60, '\u2591', ConsoleColor.Green,
                "Migrating Rules For UserAccount   " + argumentTest.AccountName.ToString());
            Currentuser.StatusMessage = "Migrating  Rules For UserAccount   " +
                argumentTest.AccountName.ToString();
            System.Console.WriteLine();
            System.Console.WriteLine();
        }
    }*/

    private void accountToMigrate_RunWorkerCompleted(object sender, RunWorkerCompletedEventArgs
        e)
    {

        

        Account argumentTest = sender as Account;
        // First, handle the case where an exception was thrown.
        if (e.Error != null)
        {
            argumentTest.AccountStatus = (e.Error.Message);
        }
        else if (e.Cancelled)
        {
            // Next, handle the case where the user canceled
            // the operation.
            // Note that due to a race condition in
            // the DoWork event handler, the Cancelled
            // flag may not have been set, even though
            // CancelAsync was called.
            argumentTest.AccountStatus = "Canceled";
            System.Console.WriteLine();
            System.Console.WriteLine();
            ProgressUtil.RenderConsoleProgress(30, '\u2591', ConsoleColor.Red,
            "Migration For UserAccount   " + argumentTest.AccountName + " Cancelled");

            System.Console.WriteLine();
            System.Console.WriteLine();

            argumentTest.Countdown.Signal();
        }
        else
        {
            // Finally, handle the case where the operation
            // succeeded.
                     
        //signal the countdown event for the main thread exit.
            {
                System.Console.WriteLine();
                System.Console.WriteLine();
                ProgressUtil.RenderConsoleProgress(30, '\u2591', ConsoleColor.Red,
                "TotalErrors For UserAccount   " + argumentTest.AccountName + " are" + Numoferrors.ToString());

                System.Console.WriteLine();
                System.Console.WriteLine();

                argumentTest.Countdown.Signal();
            }
            argumentTest.AccountStatus = "Completed";        // e.Result.ToString();
        }
    }
}
}

