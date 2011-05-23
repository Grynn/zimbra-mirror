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
using Misc;

namespace MVVM.ViewModel
{
    public class ScheduleViewModel : BaseViewModel
    {
        readonly Schedule m_schedule = new Schedule(0, "", "", false);
        UsersViewModel usersViewModel;
        ConfigViewModelU configViewModelU;
        AccountResultsViewModel accountResultsViewModel;
        BackgroundWorker bgw;

        public ScheduleViewModel()
        {
            this.SaveTaskCommand = new ActionCommand(this.SaveTask, () => true);
            this.GetTaskSchedulerCommand = new ActionCommand(this.GetTaskScheduler, () => true);
            this.GetSchedHelpCommand = new ActionCommand(this.GetSchedHelp, () => true);
            this.LoadCommand = new ActionCommand(this.Load, () => true);
            this.SaveCommand = new ActionCommand(this.Save, () => true);
            this.MigrateCommand = new ActionCommand(this.Migrate, () => true);
            this.usersViewModel = null;
        }

        public ConfigViewModelU GetConfigUModel()
        {
            return configViewModelU;
        }

        public void SetConfigUModel(ConfigViewModelU configViewModelU)
        {
            this.configViewModelU = configViewModelU;
        }

        public void SetUserModel(UsersViewModel usersViewModel)
        {
            this.usersViewModel = usersViewModel;
        }

        public void SetResultsModel(AccountResultsViewModel accountResultsViewModel)
        {
            this.accountResultsViewModel = accountResultsViewModel;
        }

        public BackgroundWorker GetBGW()
        {
            return bgw;
        }

        // Commands
        public ICommand SaveTaskCommand
        {
            get;
            private set;
        }

        private void SaveTask()
        {
            MessageBox.Show("Save Task", "Zimbra Migration", MessageBoxButton.OK, MessageBoxImage.Exclamation);
        }

        public ICommand GetTaskSchedulerCommand
        {
            get;
            private set;
        }

        private void GetTaskScheduler()
        {
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
        }

        // Commands
        public ICommand GetSchedHelpCommand
        {
            get;
            private set;
        }

        private void GetSchedHelp()
        {
            string urlString = (isBrowser) ? "http://10.20.140.218/sched.html" : "file:///C:/depot/main/ZimbraMigrationTools/src/c/Misc/Help/sched.html";
            Process.Start(new ProcessStartInfo(urlString));
        }

        public ICommand LoadCommand
        {
            get;
            private set;
        }

        private void Load()
        {
            MessageBox.Show("Schedule information loaded", "Zimbra Migration", MessageBoxButton.OK, MessageBoxImage.Exclamation);
        }

        public ICommand SaveCommand
        {
            get;
            private set;
        }

        private void Save()
        {
            MessageBox.Show("Schedule information saved", "Zimbra Migration", MessageBoxButton.OK, MessageBoxImage.Exclamation);
        }

        public ICommand MigrateCommand
        {
            get;
            private set;
        }

        public void Migrate()
        {
            lb.SelectedIndex = (isServer) ? 4 : 2;
            accountResultsViewModel.AccountResultsList.Clear();
            EnableMigrate = false;
            accountResultsViewModel.EnableStop = !EnableMigrate;

            bgw = new System.ComponentModel.BackgroundWorker();

            foreach (string s in SchedList)
            {
                accountResultsViewModel.AccountResultsList.Add(new AccountResultsViewModel(this, 0, "", s, 0, 0, 0, accountResultsViewModel.EnableStop));
            }

            bgw.DoWork += new System.ComponentModel.DoWorkEventHandler(worker_DoWork);
            bgw.ProgressChanged += new System.ComponentModel.ProgressChangedEventHandler(worker_ProgressChanged);
            bgw.WorkerReportsProgress = true;
            bgw.WorkerSupportsCancellation = true;
            bgw.RunWorkerCompleted += new System.ComponentModel.RunWorkerCompletedEventHandler(worker_RunWorkerCompleted);
            bgw.RunWorkerAsync();
        }
        ////////////////////////

        private ObservableCollection<string> schedlist = new ObservableCollection<string>();
        public ObservableCollection<string> SchedList
        {
            get { return schedlist; }
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

        //Background thread stuff
        private void worker_DoWork(object sender, System.ComponentModel.DoWorkEventArgs e)
        {
            while (accountResultsViewModel.PBValue != 100)
            {
                if (bgw.CancellationPending)
                {
                    e.Cancel = true;
                    return;
                }
                accountResultsViewModel.PBValue += 2;
                bgw.ReportProgress(accountResultsViewModel.PBValue);
                Thread.Sleep(250);
                System.Windows.Threading.Dispatcher.CurrentDispatcher.Invoke(System.Windows.Threading.DispatcherPriority.Background,
                                          new System.Threading.ThreadStart(delegate { }));
            }
        }

        private void worker_ProgressChanged(object sender, System.ComponentModel.ProgressChangedEventArgs e)
        {
            if (e.ProgressPercentage == 2)
            {
                accountResultsViewModel.PBMsgValue = "Migrating messages";
            }
            if (e.ProgressPercentage == 30)
            {
                accountResultsViewModel.PBMsgValue = "Migrating appointments";
            }
            if (e.ProgressPercentage == 60)
            {
                accountResultsViewModel.PBMsgValue = "Migrating contacts";
            }
            if (e.ProgressPercentage == 80)
            {
                accountResultsViewModel.PBMsgValue = "Migrating rules";
            }

            int i = 0;           
            foreach (string s in SchedList)
            {
                AccountResultsViewModel ar = accountResultsViewModel.AccountResultsList[i];
                // some fake stuff
                switch (i)
                {
                    case 0:
                        ar.AccountProgress = e.ProgressPercentage;
                        if ((e.ProgressPercentage % 15) == 0)
                        {
                            ar.NumWarns++;
                        }
                        if ((e.ProgressPercentage % 40) == 0)
                        {
                            ar.NumErrs++;
                        }
                        break;

                    case 1:
                        if ((e.ProgressPercentage == 10) || (e.ProgressPercentage == 100))
                        {
                            ar.AccountProgress = e.ProgressPercentage;
                        }
                        if (e.ProgressPercentage == 50)
                        {
                            ar.AccountProgress = e.ProgressPercentage + 7;
                        }
                        break;

                    case 2:
                        if ((e.ProgressPercentage == 20) || (e.ProgressPercentage == 100))
                        {
                            ar.AccountProgress = e.ProgressPercentage;
                        }
                        if (e.ProgressPercentage == 30)
                        {
                            ar.AccountProgress = e.ProgressPercentage - 12;
                            ar.NumErrs++;
                        }
                        if (e.ProgressPercentage == 66)
                        {
                            ar.AccountProgress = e.ProgressPercentage - 1;
                            ar.NumErrs++;
                        }
                        if (e.ProgressPercentage == 82)
                        {
                            ar.AccountProgress = e.ProgressPercentage;
                            ar.NumErrs++;
                        }
                        break;

                    case 3:
                        if ((e.ProgressPercentage == 10) || (e.ProgressPercentage == 100))
                        {
                            ar.AccountProgress = e.ProgressPercentage;
                        }
                        if (e.ProgressPercentage == 30)
                        {
                            ar.AccountProgress = e.ProgressPercentage - 8;
                            ar.NumErrs++;
                        }
                        if (e.ProgressPercentage == 50)
                        {
                            ar.AccountProgress = e.ProgressPercentage - 1;
                            ar.NumErrs++;
                        }
                        if (e.ProgressPercentage == 70)
                        {
                            ar.AccountProgress = e.ProgressPercentage - 3;
                            ar.NumErrs++;
                        }
                        if (e.ProgressPercentage == 82)
                        {
                            ar.AccountProgress = e.ProgressPercentage;
                            ar.NumErrs++;
                        }
                        break;

                    default:
                        ar.AccountProgress = e.ProgressPercentage;
                        break;
                }

                i++;
            }
            
        }

        private void worker_RunWorkerCompleted(object sender, System.ComponentModel.RunWorkerCompletedEventArgs e)
        {
            if (e.Cancelled)
            {
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
                usersViewModel.UsersList.Clear();
            }
        }
    }
}
