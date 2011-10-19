using System;
using System.IO;
using System.Diagnostics;
using System.ComponentModel;
using System.Windows;
using System.Windows.Input;
using System.Collections;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Threading;
using MVVM.Model;
using CssLib;
using Misc;

namespace MVVM.ViewModel
{
    public class ScheduleViewModel : BaseViewModel
    {
        readonly Schedule m_schedule = new Schedule(0, "", false);
        string m_configFile;
        string m_usermapFile;
        bool m_isPreview;   // temporary

        public ScheduleViewModel()
        {
            this.ScheduleTaskCommand = new ActionCommand(this.ScheduleTask, () => true);
            this.PreviewCommand = new ActionCommand(this.Preview, () => true);
            this.BackCommand = new ActionCommand(this.Back, () => true);
            this.MigrateCommand = new ActionCommand(this.Migrate, () => true);
            m_configFile = "";
            m_usermapFile = "";
            m_isPreview = false;
        }

        public string GetConfigFile()
        {
            return m_configFile;
        }

        public void SetConfigFile(string configFile)
        {
            this.m_configFile = configFile;
        }

        public void SetUsermapFile(string usermapFile)
        {
            this.m_usermapFile = usermapFile;
        }

        // Commands

        public ICommand ScheduleTaskCommand
        {
            get;
            private set;
        }

        private void ScheduleTask()
        {
            /*
            OperatingSystem os = System.Environment.OSVersion;
            Version v = os.Version;
            string strTaskScheduler = Environment.GetEnvironmentVariable("SYSTEMROOT");

            if (v.Major >= 6)
            {
                strTaskScheduler += "\\system32\\taskschd.msc";
                System.Diagnostics.Process.Start(@strTaskScheduler);
            }
            else
            {
                strTaskScheduler += "\\system32\\control";
                System.Diagnostics.Process proc = new System.Diagnostics.Process();
                proc.StartInfo.FileName = strTaskScheduler;
                proc.StartInfo.Arguments = "schedtasks";
                proc.Start();
            }
            */

            if ((m_configFile.Length == 0) || (m_usermapFile.Length == 0))
            {
                MessageBox.Show("There must be a config file and usermap file", "Zimbra Migration", MessageBoxButton.OK, MessageBoxImage.Error);
                return;
            }

            OperatingSystem os = System.Environment.OSVersion;
            Version v = os.Version;

            System.Diagnostics.Process proc = new System.Diagnostics.Process();
            proc.StartInfo.FileName = "c:\\windows\\system32\\schtasks.exe";

            // set up date, time, and name for task scheduler
            string dtStr = Convert.ToDateTime(this.ScheduleDate).ToString("MM/dd/yyyy");  // formatting in C# is nuts -- only way to get this to work
            string dtTime = MakeTimeStr();
            string dtName = "Migrate" + dtTime.Substring(0, 2) + dtTime.Substring(3, 2);
            //

            proc.StartInfo.Arguments = "/Create /SC ONCE /TR ";
            proc.StartInfo.Arguments += @"""";
            proc.StartInfo.Arguments += @"\";
            proc.StartInfo.Arguments += @"""";
            proc.StartInfo.Arguments += ((IntroViewModel)ViewModelPtrs[(int)ViewType.INTRO]).InstallDir;
            proc.StartInfo.Arguments += @"\";
            proc.StartInfo.Arguments += "ZimbraMigrationConsole.exe";
            proc.StartInfo.Arguments += @"\";
            proc.StartInfo.Arguments += @"""";
            proc.StartInfo.Arguments += " ";

            proc.StartInfo.Arguments += m_configFile + " ";
            proc.StartInfo.Arguments += m_usermapFile + " ";
            proc.StartInfo.Arguments += @"""";
            if (v.Major >= 6)
            {
                proc.StartInfo.Arguments += "/F /Z /V1";
            }
            proc.StartInfo.Arguments += " /TN " + dtName + " /SD " + dtStr + " /ST " + dtTime;

            proc.Start();
        }

        // Commands
        public ICommand BackCommand
        {
            get;
            private set;
        }

        private void Back()
        {
            lb.SelectedIndex = 4;
        }

        public ICommand PreviewCommand
        {
            get;
            private set;
        }

        private void Preview()
        {
            m_isPreview = true;
            Migrate();
        }

        public ICommand MigrateCommand
        {
            get;
            private set;
        }

        public void Migrate()
        {
            if (isServer)
            {
                if (CurrentCOSSelection == -1)
                {
                    CurrentCOSSelection = 0;
                }

                UsersViewModel usersViewModel = ((UsersViewModel)ViewModelPtrs[(int)ViewType.USERS]);
                ZimbraAPI zimbraAPI = new ZimbraAPI();

                if (ZimbraValues.zimbraValues.AuthToken.Length == 0)
                {
                    MessageBox.Show("You must log on to the Zimbra server", "Zimbra Migration", MessageBoxButton.OK, MessageBoxImage.Error);
                    return;
                }

                ConfigViewModelS sourceModel = ((ConfigViewModelS)ViewModelPtrs[(int)ViewType.SVRSRC]);
                if (!sourceModel.IsMailServerInitialized)
                {
                    MessageBox.Show("You must log on to Exchange", "Zimbra Migration", MessageBoxButton.OK, MessageBoxImage.Error);
                    return;
                }

                string domainName = usersViewModel.ZimbraDomain;
                string defaultPWD = DefaultPWD;
                string tempMessage = "";
                bool bProvision = false;
                MessageBoxImage mbi = MessageBoxImage.Information;
                for (int i = 0; i < SchedList.Count; i++)
                {
                    string userName = (usersViewModel.UsersList[i].MappedName.Length > 0) ? usersViewModel.UsersList[i].MappedName : usersViewModel.UsersList[i].Username;
                    string accountName = userName + "@" + domainName;
                    if (!SchedList[i].isProvisioned)
                    {
                        bProvision = true;
                        if (defaultPWD.Length == 0)
                        {
                            MessageBox.Show("Please provide an initial password", "Zimbra Migration", MessageBoxButton.OK, MessageBoxImage.Exclamation);
                            return;
                        }
                        string cosID = CosList[CurrentCOSSelection].CosID;
                        if (zimbraAPI.CreateAccount(accountName, defaultPWD, cosID) == 0)
                        {
                            tempMessage += string.Format("{0} Provisioned", userName) + "\n";
                            //MessageBox.Show(string.Format("{0} Provisioned", userName), "Zimbra Migration", MessageBoxButton.OK, MessageBoxImage.Information);
                        }
                        else
                        {
                            //MessageBox.Show(string.Format("Provision unsuccessful for {0}: {1}", userName, zimbraAPI.LastError), "Zimbra Migration", MessageBoxButton.OK, MessageBoxImage.Error);
                            tempMessage += string.Format("Provision unsuccessful for {0}: {1}", userName, zimbraAPI.LastError) + "\n";
                            mbi = MessageBoxImage.Error;
                        }
                    }
                }
                if (bProvision)
                {
                    MessageBox.Show(tempMessage, "Zimbra Migration", MessageBoxButton.OK, mbi);
                }

                // Make sure account name is set
                string usrName = (usersViewModel.UsersList[0].MappedName.Length > 0) ? usersViewModel.UsersList[0].MappedName : usersViewModel.UsersList[0].Username;
                string acctName = usrName + "@" + domainName;
                zimbraAPI.AccountName = acctName;
                //

                lb.SelectedIndex = 6;
            }
            else
            {
                lb.SelectedIndex = 4;
            }

            AccountResultsViewModel accountResultsViewModel = ((AccountResultsViewModel)ViewModelPtrs[(int)ViewType.RESULTS]);
            accountResultsViewModel.AccountResultsList.Clear();
            EnableMigrate = false;
            accountResultsViewModel.EnableStop = !EnableMigrate;

            int num = 0;
            foreach (SchedUser su in SchedList)
            {
                accountResultsViewModel.AccountResultsList.Add(new AccountResultsViewModel(this, num++, 0, "", "", su.username, 0, "", 0, 0, accountResultsViewModel.EnableStop));
            }

            num = 0;
            foreach (SchedUser su in SchedList)
            {
                BackgroundWorker bgw = new System.ComponentModel.BackgroundWorker();
                bgw.DoWork += new System.ComponentModel.DoWorkEventHandler(worker_DoWork);
                bgw.ProgressChanged += new System.ComponentModel.ProgressChangedEventHandler(worker_ProgressChanged);
                bgw.WorkerReportsProgress = true;
                bgw.WorkerSupportsCancellation = true;
                bgw.RunWorkerCompleted += new System.ComponentModel.RunWorkerCompletedEventHandler(worker_RunWorkerCompleted);
                bgw.RunWorkerAsync(num++);
                bgwlist.Add(bgw);
            }
        }
        ////////////////////////

        private ObservableCollection<BackgroundWorker> bgwlist = new ObservableCollection<BackgroundWorker>();
        public ObservableCollection<BackgroundWorker> BGWList
        {
            get { return bgwlist; }
            set { bgwlist = value; }
        }

        private ObservableCollection<DoWorkEventArgs> eventArglist = new ObservableCollection<DoWorkEventArgs>();
        public ObservableCollection<DoWorkEventArgs> EventArgList
        {
            get { return eventArglist; }
            set { eventArglist = value; }
        }

        private ObservableCollection<SchedUser> schedlist = new ObservableCollection<SchedUser>();
        public ObservableCollection<SchedUser> SchedList
        {
            get
            {
                schedlist.Clear();
                UsersViewModel usersViewModel = ((UsersViewModel)ViewModelPtrs[(int)ViewType.USERS]);
                foreach (UsersViewModel obj in usersViewModel.UsersList)
                {
                    int idx = obj.Username.IndexOf("@");
                    string NameToAdd = (idx != -1) ? obj.Username.Substring(0, idx) : obj.Username;
                    schedlist.Add(new SchedUser(NameToAdd, obj.IsProvisioned));
                }
                return schedlist;
            }
        }
        public string COS
        {
            get { return m_config.UserProvision.COS; }
            set
            {
                if (value == m_config.UserProvision.COS)
                {
                    return;
                }
                m_config.UserProvision.COS = value;

                OnPropertyChanged(new PropertyChangedEventArgs("COS"));
            }
        }
        public string DefaultPWD
        {
            get { return m_config.UserProvision.DefaultPWD; }
            set
            {
                if (value == m_config.UserProvision.DefaultPWD)
                {
                    return;
                }
                m_config.UserProvision.DefaultPWD = value;

                OnPropertyChanged(new PropertyChangedEventArgs("DefaultPWD"));
            }
        }

        public int PBValue
        {
            get { return m_schedule.PBValue; }
            set
            {
                if (value == m_schedule.PBValue)
                {
                    return;
                }
                m_schedule.PBValue = value;
                OnPropertyChanged(new PropertyChangedEventArgs("PBValue"));
            }
        }

        public string PBMsgValue
        {
            get { return m_schedule.PBMsgValue; }
            set
            {
                if (value == m_schedule.PBMsgValue)
                {
                    return;
                }
                m_schedule.PBMsgValue = value;
                OnPropertyChanged(new PropertyChangedEventArgs("PBMsgValue"));
            }
        }

        public bool EnableMigrate
        {
            get { return m_schedule.EnableMigrate; }
            set
            {
                if (value == m_schedule.EnableMigrate)
                {
                    return;
                }
                m_schedule.EnableMigrate = value;
                OnPropertyChanged(new PropertyChangedEventArgs("EnableMigrate"));
            }
        }

        private int cosSelection;
        public int CurrentCOSSelection
        {
            get { return cosSelection; }
            set
            {

                cosSelection = value;

                OnPropertyChanged(new PropertyChangedEventArgs("CurrentCOSSelection"));
            }
        }

        public string ScheduleDate
        {
            get { return m_schedule.ScheduleDate.ToShortDateString(); }
            set
            {
                if (value == m_schedule.ScheduleDate.ToShortDateString())
                {
                    return;
                }
                m_schedule.ScheduleDate = Convert.ToDateTime(value);

                OnPropertyChanged(new PropertyChangedEventArgs("ScheduleDate"));
            }
        }

        public int HrSelection
        {
            get { return m_schedule.HrSelection; }
            set
            {
                if (value == m_schedule.HrSelection)
                {
                    return;
                }
                m_schedule.HrSelection = value;
                OnPropertyChanged(new PropertyChangedEventArgs("HrSelection"));
            }
        }

        public int MinSelection
        {
            get { return m_schedule.MinSelection; }
            set
            {
                if (value == m_schedule.MinSelection)
                {
                    return;
                }
                m_schedule.MinSelection = value;
                OnPropertyChanged(new PropertyChangedEventArgs("MinSelection"));
            }
        }

        public int AMPMSelection
        {
            get { return m_schedule.AMPMSelection; }
            set
            {
                if (value == m_schedule.AMPMSelection)
                {
                    return;
                }
                m_schedule.AMPMSelection = value;
                OnPropertyChanged(new PropertyChangedEventArgs("AMPMSelection"));
            }
        }

        private string MakeTimeStr()
        {
            string retval = "";
            bool bAdd12 = (AMPMSelection == 1);

            switch (HrSelection)
            {
                case 0:  retval = (bAdd12) ? "13:" : "01:"; break;
                case 1:  retval = (bAdd12) ? "14:" : "02:"; break;
                case 2:  retval = (bAdd12) ? "15:" : "03:"; break;
                case 3:  retval = (bAdd12) ? "16:" : "04:"; break;
                case 4:  retval = (bAdd12) ? "17:" : "05:"; break;
                case 5:  retval = (bAdd12) ? "18:" : "06:"; break;
                case 6:  retval = (bAdd12) ? "19:" : "07:"; break;
                case 7:  retval = (bAdd12) ? "20:" : "08:"; break;
                case 8:  retval = (bAdd12) ? "21:" : "09:"; break;
                case 9:  retval = (bAdd12) ? "22:" : "10:"; break;
                case 10: retval = (bAdd12) ? "23:" : "11:"; break;
                case 11: retval = (bAdd12) ? "12:" : "00:"; break;
                default: retval = "00:";                    break;
            }

            switch (MinSelection)
            {
                case 0:     retval += "00:00"; break;
                case 1:     retval += "10:00"; break;
                case 2:     retval += "20:00"; break;
                case 3:     retval += "30:00"; break;
                case 4:     retval += "40:00"; break;
                case 5:     retval += "50:00"; break;
                default:    retval += "00:00"; break;
            }

            return retval;
        }

        private ObservableCollection<CosInfo> coslist = new ObservableCollection<CosInfo>();
        public ObservableCollection<CosInfo> CosList
        {
            get { return coslist; }
            set { coslist = value; }
        }

        //Background thread stuff
        private void worker_DoWork(object sender, System.ComponentModel.DoWorkEventArgs e)
        {
            eventArglist.Add(e);

            int num = (int)e.Argument;
            MigrationAccount MyAcct = new MigrationAccount();
            AccountResultsViewModel accountResultsViewModel = ((AccountResultsViewModel)ViewModelPtrs[(int)ViewType.RESULTS]);
            MyAcct.Accountname = accountResultsViewModel.AccountResultsList[num].AccountName;
            MyAcct.AccountID = MyAcct.Accountname;
            MyAcct.Accountnum = num;
            MyAcct.OnChanged += new MigrationObjectEventHandler(Acct_OnAcctChanged);

            MigrationFolder MyFolder = new MigrationFolder();
            MyFolder.Accountnum = num;
            MyFolder.OnChanged += new MigrationObjectEventHandler(Folder_OnChanged);

            MyAcct.migrationFolders.Insert(0, MyFolder);
            CSMigrationwrapper mw = new CSMigrationwrapper();

            mw.StartMigration(MyAcct, m_isPreview);
            accountResultsViewModel.AccountResultsList[num].PBMsgValue = "Migration complete";
            accountResultsViewModel.AccountResultsList[num].AcctProgressMsg = "Complete";
        }

        private void worker_ProgressChanged(object sender, System.ComponentModel.ProgressChangedEventArgs e)
        {
            // We want to update the main accountResultViewModel, but we have to make sure we only do it for
            // the account whose tab has the focus.  That's why we check against accountResultsViewModel.AccountOnTab
            AccountResultsViewModel accountResultsViewModel = ((AccountResultsViewModel)ViewModelPtrs[(int)ViewType.RESULTS]);
            if ((int)e.UserState == accountResultsViewModel.AccountOnTab)
            {
                accountResultsViewModel.AccountProgress = e.ProgressPercentage;
                accountResultsViewModel.PBValue = e.ProgressPercentage;
            }
        }

        private void worker_RunWorkerCompleted(object sender, System.ComponentModel.RunWorkerCompletedEventArgs e)
        {
            AccountResultsViewModel accountResultsViewModel = ((AccountResultsViewModel)ViewModelPtrs[(int)ViewType.RESULTS]);
            if (e.Cancelled)
            {
                for (int i = 0; i < accountResultsViewModel.AccountResultsList.Count; i++)  // hate to set them all, but do it for now
                {
                    accountResultsViewModel.AccountResultsList[i].PBMsgValue = "Migration canceled";
                }
                accountResultsViewModel.PBMsgValue = "Migration canceled";
            }
            else if (e.Error != null)
            {
                accountResultsViewModel.PBMsgValue = "Migration exception: " + e.Error.ToString();
            }
            else
            {
                accountResultsViewModel.PBMsgValue = "Migration complete";
                EnableMigrate = false;
                accountResultsViewModel.EnableStop = false;
                SchedList.Clear();
                UsersViewModel usersViewModel = ((UsersViewModel)ViewModelPtrs[(int)ViewType.USERS]);
                usersViewModel.UsersList.Clear();
            }
        }


        public void Acct_OnAcctChanged(object sender, MigrationObjectEventArgs e)                         
        {
            string msg = "";
            MigrationAccount a = (MigrationAccount)sender;
            AccountResultsViewModel accountResultsViewModel = ((AccountResultsViewModel)ViewModelPtrs[(int)ViewType.RESULTS]);    // main one
            AccountResultsViewModel ar = accountResultsViewModel.AccountResultsList[a.Accountnum];
            if (e.PropertyName == "TotalNoErrors")
            {
                ar.NumErrs = (int)a.TotalNoErrors + 1;      // this happens first
                ar.AccountProblemsList.Add(a.LastProblemInfo);
            }
            else
            if (e.PropertyName == "TotalNoWarnings")
            {
                ar.NumWarns = (int)a.TotalNoWarnings + 1;   // this happens first
                ar.AccountProblemsList.Add(a.LastProblemInfo);
            }
            else
            {
                msg = "Begin {0} Migration";
                ar.PBMsgValue = String.Format(msg, a.Accountname);
                accountResultsViewModel.PBMsgValue = String.Format(msg, a.Accountname);  // for the user results window
            }
        }

        public void Folder_OnChanged(object sender, MigrationObjectEventArgs e)                                  
        {

            MigrationFolder f = (MigrationFolder)sender;
            AccountResultsViewModel accountResultsViewModel = ((AccountResultsViewModel)ViewModelPtrs[(int)ViewType.RESULTS]);    // main one
            AccountResultsViewModel ar = accountResultsViewModel.AccountResultsList[f.Accountnum];

            if (bgwlist[f.Accountnum].CancellationPending)
            {
                eventArglist[f.Accountnum].Cancel = true;
                return;
            }

            string msg1 = "Migrating {0}";
            ar.PBMsgValue = String.Format(msg1, f.FolderName);
            accountResultsViewModel.PBMsgValue = String.Format(msg1, f.FolderName); // for the user results window

            string msg2 = "{0} of {1}";
            ar.AcctProgressMsg = String.Format(msg2, f.CurrentCountOFItems, f.TotalCountOFItems);

            // temporary -- eventually, for each account, get totals of all, and figure out incr            
            int incr = 0;
            switch (f.Accountnum)
            {
                case 0:     incr = 5; break;
                case 1:     incr = 4; break;
                case 2:     incr = 4; break;
                case 3:     incr = 3; break;
                default:    incr = 5; break;
            }
            ////////

            accountResultsViewModel.AccountResultsList[f.Accountnum].PBValue += incr;
            bgwlist[f.Accountnum].ReportProgress(ar.PBValue, f.Accountnum);

            if (e.PropertyName == "FolderName")
            {
                if (e.OldValue != null)
                {
                    ar.AccountFolderInfoList.Add(f.LastFolderInfo);
                }
            }
        }
    }
}
