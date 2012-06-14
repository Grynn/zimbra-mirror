using CssLib;
using MVVM.Model;
using Misc;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Collections;
using System.ComponentModel;
using System.Diagnostics;
using System.Globalization;
using System.IO;
using System.Threading;
using System.Windows.Input;
using System.Windows;
using System;

namespace MVVM.ViewModel
{
public class ScheduleViewModel: BaseViewModel
{
    readonly Schedule m_schedule = new Schedule(false);
    string m_configFile;
    string m_usermapFile;
    bool m_isPreview;
    bool m_isComplete;

    public ScheduleViewModel()
    {
        this.ScheduleTaskCommand = new ActionCommand(this.ScheduleTask, () => true);
        this.PreviewCommand = new ActionCommand(this.Preview, () => true);
        this.BackCommand = new ActionCommand(this.Back, () => true);
        this.MigrateCommand = new ActionCommand(this.Migrate, () => true);
        m_configFile = "";
        m_usermapFile = "";
        m_isPreview = false;
        m_isComplete = false;
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

    public bool IsPreviewMode()
    {
        return m_isPreview;
    }

    public bool IsComplete()
    {
        return m_isComplete;
    }

    // Commands
    public ICommand ScheduleTaskCommand {
        get;
        private set;
    }
    private void ScheduleTask()
    {
        /*
         * OperatingSystem os = System.Environment.OSVersion;
         * Version v = os.Version;
         * string strTaskScheduler = Environment.GetEnvironmentVariable("SYSTEMROOT");
         *
         * if (v.Major >= 6)
         * {
         *  strTaskScheduler += "\\system32\\taskschd.msc";
         *  System.Diagnostics.Process.Start(@strTaskScheduler);
         * }
         * else
         * {
         *  strTaskScheduler += "\\system32\\control";
         *  System.Diagnostics.Process proc = new System.Diagnostics.Process();
         *  proc.StartInfo.FileName = strTaskScheduler;
         *  proc.StartInfo.Arguments = "schedtasks";
         *  proc.Start();
         * }
         */
        const int TR_MAX_SIZE = 261;

        if ((m_configFile.Length == 0) || (m_usermapFile.Length == 0))
        {
            MessageBox.Show("There must be a config file and usermap file", "Zimbra Migration",
                MessageBoxButton.OK, MessageBoxImage.Error);
            return;
        }

        OperatingSystem os = System.Environment.OSVersion;
        Version v = os.Version;

        System.Diagnostics.Process proc = new System.Diagnostics.Process();
        proc.StartInfo.FileName = "c:\\windows\\system32\\schtasks.exe";

        // set up date, time, and name for task scheduler
        // FBS Bug 74232 -- need to get the right format for Schtasks SD parameter (dtStr)
        // Has to be either MM/DD/YYYY, DD/MM/YYYY, or YYYY/MM/DD
        // C# formatting stuff and schtasks are both very fussy.  Only MM capitalized (because of minutes)
        string dtStr = Convert.ToDateTime(this.ScheduleDate).ToString("MM/dd/yyyy");
        CultureInfo currentCulture = Thread.CurrentThread.CurrentCulture;
        String shortDatePattern = currentCulture.DateTimeFormat.ShortDatePattern; 
        if ((shortDatePattern.StartsWith("d")) || (shortDatePattern.StartsWith("D")))   // being safe with "D"
        {
            dtStr = Convert.ToDateTime(this.ScheduleDate).ToString("dd/MM/yyyy");
        }
        else
        if ((shortDatePattern.StartsWith("y")) || (shortDatePattern.StartsWith("Y")))   // being safe with "Y"
        {
            dtStr = Convert.ToDateTime(this.ScheduleDate).ToString("yyyy/MM/dd");
        }
        if (v.Major < 6)    // XP is a pain -- you have to make sure is has slashes.  W7 doesn't care
        {
            if (dtStr.Contains("."))
            {
                dtStr = dtStr.Replace(".", "/");
            }
            else
            if (dtStr.Contains("-"))
            {
                dtStr = dtStr.Replace("-", "/");
            }
        }
        //
        //

        string dtTime = MakeTimeStr();
        string dtName = "Migrate" + dtTime.Substring(0, 2) + dtTime.Substring(3, 2);

        //

        // FBS Bug 75004 -- 6/6/12
        string userEntry = dtStr + " " + dtTime;
        DateTime userDT = Convert.ToDateTime(userEntry);
        DateTime nowDT = DateTime.Now;
        if (DateTime.Compare(userDT, nowDT) < 0)
        {
            MessageBox.Show("You can't schedule a task in the past", "Zimbra Migration",
                MessageBoxButton.OK, MessageBoxImage.Error);
            return;
        }
        //

        proc.StartInfo.Arguments = "/Create /SC ONCE /TR ";
        proc.StartInfo.Arguments += @"""";
        proc.StartInfo.Arguments += @"\";
        proc.StartInfo.Arguments += @"""";
        proc.StartInfo.Arguments +=
            ((IntroViewModel)ViewModelPtrs[(int)ViewType.INTRO]).InstallDir;
        proc.StartInfo.Arguments += @"\";
        proc.StartInfo.Arguments += "ZimbraMigrationConsole.exe";
        proc.StartInfo.Arguments += @"\";
        proc.StartInfo.Arguments += @"""";
        proc.StartInfo.Arguments += " ";

        // FBS bug 74232 -- 6/1/12 -- have to put \" around arguments since they might have spaces
        proc.StartInfo.Arguments += "\\\"" + "ConfigxmlFile="  + m_configFile + "\\\"" + " ";
        proc.StartInfo.Arguments += "\\\"" + "Users=" + m_usermapFile + "\\\"";
        proc.StartInfo.Arguments += @"""";

        // FBS bug 74232 -- make sure value for /TR option does not exceed 261 characters
        int trLen = proc.StartInfo.Arguments.Length - 21;  // 21 is length of "/Create /SC ONCE /TR "
        if (trLen > TR_MAX_SIZE)
        {
            MessageBox.Show("Taskrun argument string exceeds 261 characters.  Please use config files with smaller path sizes.",
                "Zimbra Migration", MessageBoxButton.OK, MessageBoxImage.Error);
            return;
        }

        if (v.Major >= 6)
            proc.StartInfo.Arguments += " /F /Z /V1";
        proc.StartInfo.Arguments += " /TN " + dtName + " /SD " + dtStr + " /ST " + dtTime;

        proc.Start();
    }

    // Commands
    public ICommand BackCommand {
        get;
        private set;
    }
    private void Back()
    {
        lb.SelectedIndex = 4;
    }
    public ICommand PreviewCommand {
        get;
        private set;
    }
    private void Preview()
    {
        m_isPreview = true;
        DoMigrate(m_isPreview);
    }
    public ICommand MigrateCommand {
        get;
        private set;
    }
    private void Migrate()
    {
        m_isPreview = false;
        DoMigrate(m_isPreview);
    }

    private int AvailableThread()
    {
        int iThreadNum = -1;
        for (int i = 0; i < bgwlist.Count; i++)
        {
            if (!bgwlist[i].IsBusy)
            {
                iThreadNum = i;
                break;
            }
        }
        return iThreadNum;
    }

    public void DoMigrate(bool isPreview)
    {
        bgwlist.Clear();
        if (isServer)
        {
            if (CurrentCOSSelection == -1)
                CurrentCOSSelection = 0;
            UsersViewModel usersViewModel =
                ((UsersViewModel)ViewModelPtrs[(int)ViewType.USERS]);

            if (ZimbraValues.zimbraValues.AuthToken.Length == 0)
            {
                MessageBox.Show("You must log on to the Zimbra server", "Zimbra Migration",
                    MessageBoxButton.OK, MessageBoxImage.Error);
                return;
            }

            ConfigViewModelS sourceModel =
                ((ConfigViewModelS)ViewModelPtrs[(int)ViewType.SVRSRC]);

            if (!sourceModel.IsMailServerInitialized)
            {
                MessageBox.Show("You must log on to Exchange", "Zimbra Migration",
                    MessageBoxButton.OK, MessageBoxImage.Error);
                return;
            }

            string domainName = usersViewModel.ZimbraDomain;
            string defaultPWD = DefaultPWD;
            string tempMessage = "";
            bool bProvision = false;
            MessageBoxImage mbi = MessageBoxImage.Information;

            for (int i = 0; i < SchedList.Count; i++)
            {
                string userName = (usersViewModel.UsersList[i].MappedName.Length > 0) ?
                    usersViewModel.UsersList[i].MappedName :
                    usersViewModel.UsersList[i].Username;
                string accountName = userName + "@" + domainName;

                if (!SchedList[i].isProvisioned)
                {
                    if (!isPreview)
                    {
                        bProvision = true;
                        if (defaultPWD.Length == 0)
                        {
                            MessageBox.Show("Please provide an initial password",
                                "Zimbra Migration", MessageBoxButton.OK,
                                MessageBoxImage.Exclamation);
                            return;
                        }

                        string cosID = CosList[CurrentCOSSelection].CosID;
                        ZimbraAPI zimbraAPI = new ZimbraAPI(isServer);

                        // FBS bug 71646 -- 3/26/12
                        string displayName = "";
                        string givenName = "";
                        string sn = "";
                        string zfp = "";

                        // FBS bug 73395 -- 4/25/12
                        ObjectPickerInfo opinfo = usersViewModel.GetOPInfo();
                        if (opinfo.DisplayName.Length > 0)
                        {
                            displayName = opinfo.DisplayName;
                        }
                        if (opinfo.GivenName.Length > 0)
                        {
                            givenName = opinfo.GivenName;
                        }
                        if (opinfo.Sn.Length > 0)
                        {
                            sn = opinfo.Sn;
                        }
                        if (opinfo.Zfp.Length > 0)
                        {
                            zfp = opinfo.Zfp;
                        }
                        // end 73395
                        // end 71646

                        string historyfile = Path.GetTempPath() + accountName.Substring(0, accountName.IndexOf('@')) + "history.log";
                        if (File.Exists(historyfile))
                        {
                            try
                            {

                                File.Delete(historyfile);
                            }
                            catch (Exception e)
                            {
                                string msg = "exception in deleteing the Histroy file " + e.Message;
                                System.Console.WriteLine(msg);
                            }

                        }

                        bool mustChangePW = usersViewModel.UsersList[i].MustChangePassword;
                        if (zimbraAPI.CreateAccount(accountName, displayName, givenName, sn, zfp, defaultPWD, mustChangePW, cosID) == 0)
                        {
                            tempMessage += string.Format("{0} Provisioned", userName) + "\n";
                            // MessageBox.Show(string.Format("{0} Provisioned", userName), "Zimbra Migration", MessageBoxButton.OK, MessageBoxImage.Information);
                        }
                        else
                        {
                            // MessageBox.Show(string.Format("Provision unsuccessful for {0}: {1}", userName, zimbraAPI.LastError), "Zimbra Migration", MessageBoxButton.OK, MessageBoxImage.Error);
                            tempMessage += string.Format("Provision unsuccessful for {0}: {1}",
                                userName, zimbraAPI.LastError) + "\n";
                            mbi = MessageBoxImage.Error;
                        }
                    }
                }
            }
            if (bProvision)
            {
                MessageBox.Show(tempMessage, "Zimbra Migration", MessageBoxButton.OK, mbi);
            }
            if (mbi == MessageBoxImage.Error)
            {
                return;
            }
            lb.SelectedIndex = 6;
        }
        else
        {
            lb.SelectedIndex = 4;
        }

        AccountResultsViewModel accountResultsViewModel =
            ((AccountResultsViewModel)ViewModelPtrs[(int)ViewType.RESULTS]);

        accountResultsViewModel.AccountResultsList.Clear();
        if (isServer)
        {
            EnableMigrate = false;
            EnablePreview = false;
        }
        else
        {
            ((OptionsViewModel)ViewModelPtrs[(int)ViewType.OPTIONS]).OEnableNext = false;
        }
        accountResultsViewModel.EnableStop = !EnableMigrate;

        int num = 0;

        foreach (SchedUser su in SchedList)
        {
            accountResultsViewModel.AccountResultsList.Add(new AccountResultsViewModel(this,
                num++, 0, "", "", su.username, 0, "", 0, 0,
                accountResultsViewModel.EnableStop));
        }
        accountResultsViewModel.OpenLogFileEnabled = true;

        // FBS bug 71048 -- 4/16/12 -- use the correct number of threads.
        // If MaxThreadCount not specified, default to 4.  If fewer users than MaxThreadCount, numThreads = numUsers
        OptionsViewModel ovm = ((OptionsViewModel)ViewModelPtrs[(int)ViewType.OPTIONS]);
        int maxThreads = (ovm.MaxThreadCount > 0) ? ovm.MaxThreadCount : 4;
        maxThreads = Math.Min(maxThreads, 8);   // let's make 8 the limit for now
        int numUsers = SchedList.Count;
        int numThreads = Math.Min(numUsers, maxThreads);
        for (int i = 0; i < numUsers; i++)
        {
            if (i < numThreads)
            {
                UserBW bgw = new UserBW(i);
                bgw.DoWork += new System.ComponentModel.DoWorkEventHandler(worker_DoWork);
                bgw.ProgressChanged += new System.ComponentModel.ProgressChangedEventHandler(
                    worker_ProgressChanged);
                bgw.WorkerReportsProgress = true;
                bgw.WorkerSupportsCancellation = true;
                bgw.RunWorkerCompleted += new System.ComponentModel.RunWorkerCompletedEventHandler(
                    worker_RunWorkerCompleted);
                bgw.usernum = i;
                bgw.RunWorkerAsync(i);
                bgwlist.Add(bgw);
            }
            else
            {
                overflowList.Add(i);
            }
        }; 
    }

    // //////////////////////

    private ObservableCollection<UserBW> bgwlist =
        new ObservableCollection<UserBW>();
    public ObservableCollection<UserBW> BGWList
    {
        get { return bgwlist; }
        set { bgwlist = value; }
    }
    private ObservableCollection<int> overflowList =
        new ObservableCollection<int>();
    public ObservableCollection<int> OverflowList
    {
        get { return overflowList; }
        set { overflowList = value; }
    }
    private ObservableCollection<DoWorkEventArgs> eventArglist =
        new ObservableCollection<DoWorkEventArgs>();
    public ObservableCollection<DoWorkEventArgs> EventArgList {
        get { return eventArglist; }
        set { eventArglist = value; }
    }
    private ObservableCollection<SchedUser> schedlist = new ObservableCollection<SchedUser>();
    public ObservableCollection<SchedUser> SchedList {
        get
        {
            schedlist.Clear();

            UsersViewModel usersViewModel =
                ((UsersViewModel)ViewModelPtrs[(int)ViewType.USERS]);

            foreach (UsersViewModel obj in usersViewModel.UsersList)
            {
                string NameToCheck = (obj.MappedName.Length > 0) ? obj.MappedName :
                    obj.Username;
                int idx = NameToCheck.IndexOf("@");
                string NameToAdd = (idx != -1) ? NameToCheck.Substring(0, idx) : NameToCheck;

                schedlist.Add(new SchedUser(NameToAdd, obj.IsProvisioned));
            }
            return schedlist;
        }
    }
    public string COS {
        get { return m_config.UserProvision.COS; }
        set
        {
            if (value == m_config.UserProvision.COS)
                return;
            m_config.UserProvision.COS = value;

            OnPropertyChanged(new PropertyChangedEventArgs("COS"));
        }
    }
    public string DefaultPWD {
        get { return m_config.UserProvision.DefaultPWD; }
        set
        {
            if (value == m_config.UserProvision.DefaultPWD)
                return;
            m_config.UserProvision.DefaultPWD = value;

            OnPropertyChanged(new PropertyChangedEventArgs("DefaultPWD"));
        }
    }
    public bool EnableMigrate {
        get { return m_schedule.EnableMigrate; }
        set
        {
            if (value == m_schedule.EnableMigrate)
                return;
            m_schedule.EnableMigrate = m_isComplete ? false : value;
            OnPropertyChanged(new PropertyChangedEventArgs("EnableMigrate"));
        }
    }
    public bool EnablePreview
    {
        get { return m_schedule.EnablePreview; }
        set
        {
            if (value == m_schedule.EnablePreview)
                return;
            m_schedule.EnablePreview = m_isComplete ? false : value;
            OnPropertyChanged(new PropertyChangedEventArgs("EnablePreview"));
        }
    }
    public bool EnableProvGB
    {
        get { return m_schedule.EnableProvGB; }
        set
        {
            if (value == m_schedule.EnableProvGB)
                return;
            m_schedule.EnableProvGB = value;
            OnPropertyChanged(new PropertyChangedEventArgs("EnableProvGB"));
        }
    }
    private int cosSelection;
    public int CurrentCOSSelection {
        get { return cosSelection; }
        set
        {
            cosSelection = value;

            OnPropertyChanged(new PropertyChangedEventArgs("CurrentCOSSelection"));
        }
    }
    public string ScheduleDate {
        get { return m_schedule.ScheduleDate.ToShortDateString(); }
        set
        {
            if (value == m_schedule.ScheduleDate.ToShortDateString())
                return;
            try
            {
                m_schedule.ScheduleDate = Convert.ToDateTime(value);
            }
            catch (Exception)
            {
                MessageBox.Show("Please enter a valid date in the indicated format", "Zimbra Migration", MessageBoxButton.OK, MessageBoxImage.Error);
                return;
            }

            OnPropertyChanged(new PropertyChangedEventArgs("ScheduleDate"));
        }
    }
    private string dateFormatLabelContent2;
    public string DateFormatLabelContent2
    {
        get { return dateFormatLabelContent2; }
        set
        {
            if (value == dateFormatLabelContent2)
                return;
            dateFormatLabelContent2 = value;

            OnPropertyChanged(new PropertyChangedEventArgs("DateFormatLabelContent2"));
        }
    }
    public int HrSelection {
        get { return m_schedule.HrSelection; }
        set
        {
            if (value == m_schedule.HrSelection)
                return;
            m_schedule.HrSelection = value;
            OnPropertyChanged(new PropertyChangedEventArgs("HrSelection"));
        }
    }
    public int MinSelection {
        get { return m_schedule.MinSelection; }
        set
        {
            if (value == m_schedule.MinSelection)
                return;
            m_schedule.MinSelection = value;
            OnPropertyChanged(new PropertyChangedEventArgs("MinSelection"));
        }
    }
    public int AMPMSelection {
        get { return m_schedule.AMPMSelection; }
        set
        {
            if (value == m_schedule.AMPMSelection)
                return;
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
        case 0:
            retval = (bAdd12) ? "13:" : "01:";
            break;
        case 1:
            retval = (bAdd12) ? "14:" : "02:";
            break;
        case 2:
            retval = (bAdd12) ? "15:" : "03:";
            break;
        case 3:
            retval = (bAdd12) ? "16:" : "04:";
            break;
        case 4:
            retval = (bAdd12) ? "17:" : "05:";
            break;
        case 5:
            retval = (bAdd12) ? "18:" : "06:";
            break;
        case 6:
            retval = (bAdd12) ? "19:" : "07:";
            break;
        case 7:
            retval = (bAdd12) ? "20:" : "08:";
            break;
        case 8:
            retval = (bAdd12) ? "21:" : "09:";
            break;
        case 9:
            retval = (bAdd12) ? "22:" : "10:";
            break;
        case 10:
            retval = (bAdd12) ? "23:" : "11:";
            break;
        case 11:
            retval = (bAdd12) ? "12:" : "00:";
            break;
        default:
            retval = "00:";
            break;
        }
        switch (MinSelection)
        {
        case 0:
            retval += "00:00";
            break;
        case 1:
            retval += "10:00";
            break;
        case 2:
            retval += "20:00";
            break;
        case 3:
            retval += "30:00";
            break;
        case 4:
            retval += "40:00";
            break;
        case 5:
            retval += "50:00";
            break;
        default:
            retval += "00:00";
            break;
        }
        return retval;
    }

    private MigrationOptions SetOptions()
    {
        MigrationOptions importOpts = new MigrationOptions();
        ItemsAndFoldersOptions itemFolderFlags = ItemsAndFoldersOptions.None;
        OptionsViewModel ovm = ((OptionsViewModel)ViewModelPtrs[(int)ViewType.OPTIONS]);

        if (ovm.ImportCalendarOptions)
            itemFolderFlags = itemFolderFlags | ItemsAndFoldersOptions.Calendar;
        if (ovm.ImportTaskOptions)
            itemFolderFlags = itemFolderFlags | ItemsAndFoldersOptions.Tasks;
        if (ovm.ImportContactOptions)
            itemFolderFlags = itemFolderFlags | ItemsAndFoldersOptions.Contacts;
        if (ovm.ImportMailOptions)
            itemFolderFlags = itemFolderFlags | ItemsAndFoldersOptions.Mail;
        if (ovm.ImportSentOptions)
            itemFolderFlags = itemFolderFlags | ItemsAndFoldersOptions.Sent;
        if (ovm.ImportDeletedItemOptions)
            itemFolderFlags = itemFolderFlags | ItemsAndFoldersOptions.DeletedItems;
        if (ovm.ImportJunkOptions)
            itemFolderFlags = itemFolderFlags | ItemsAndFoldersOptions.Junk;
        if (ovm.ImportRuleOptions)
            itemFolderFlags = itemFolderFlags | ItemsAndFoldersOptions.Rules;
        if (ovm.ImportOOOOptions)
            itemFolderFlags = itemFolderFlags | ItemsAndFoldersOptions.OOO;
        importOpts.ItemsAndFolders = itemFolderFlags;
        importOpts.DateFilter = (ovm.IsOnOrAfter) ? ovm.MigrateONRAfter : null;
        importOpts.MessageSizeFilter = (ovm.IsMaxMessageSize) ? ovm.MaxMessageSize : null;
        importOpts.SkipFolders = (ovm.IsSkipFolders) ? ovm.FoldersToSkip : null;
        importOpts.SkipPrevMigrated = ovm.IsSkipPrevMigratedItems;
        importOpts.MaxErrorCnt = ovm.MaxErrorCount;
         switch(ovm.LogLevel)
                {
                case"Debug":
                     importOpts.VerboseOn = LogLevel.Debug;
                    break;
                case "Info":
                    importOpts.VerboseOn = LogLevel.Info;
                    break;
                case "Trace":
                    importOpts.VerboseOn = LogLevel.Trace;
                    break;

                default:
                    importOpts.VerboseOn = LogLevel.Info;
                    break;
                }
        
        return importOpts;
    }

    private string GetFolderTypeForUserResults(string containerClass)
    {
        string retval = "Message";

        if (containerClass == "IPF.Contact")
        {
            retval = "Contact";
        }
        else if (containerClass == "IPF.Appointment")
        {
            retval = "Appointment";
        }
        else if (containerClass == "IPF.Task")
        {
            retval = "Task";
        }
        else if (containerClass == "OOO")
        {
            retval = "OOO";
        }
        else if (containerClass == "All Rules")
        {
            retval = "All Rules";
        }
        return retval;
    }

    private string FormatTheLastMsg(MigrationFolder lastFolder, bool isOOOorRules)
    // FBS 4/13/12 -- rewrite to fix bug 71048
    {
        string retval = (isOOOorRules) ? "1 of 1" : ""; // if it's Out of Office or Rules, just say 1 of 1
        if (!isOOOorRules)
        {
            string msg = "{0} of {1}";
            retval = String.Format(msg, lastFolder.CurrentCountOfItems, lastFolder.TotalCountOfItems);
        }
        return retval;
    }

    private void FormatGlobalMsg(AccountResultsViewModel ar)
    {
        string msg = "{0} of {1} ({2}%)";
        string msgG = String.Format(msg, ar.TotalItemsToMigrate, ar.TotalItemsToMigrate, 100);
        ar.GlobalAcctProgressMsg = msgG;
    }

    private ObservableCollection<CosInfo> coslist = new ObservableCollection<CosInfo>();
    public ObservableCollection<CosInfo> CosList {
        get { return coslist; }
        set { coslist = value; }
    }
    // Background thread stuff
    private void worker_DoWork(object sender, System.ComponentModel.DoWorkEventArgs e)
    {
        eventArglist.Add(e);

        int num = (int)e.Argument;
        MigrationAccount MyAcct = new MigrationAccount();
        UsersViewModel usersViewModel = ((UsersViewModel)ViewModelPtrs[(int)ViewType.USERS]);
        AccountResultsViewModel accountResultsViewModel =
            ((AccountResultsViewModel)ViewModelPtrs[(int)ViewType.RESULTS]);    // main one
        string accountname = accountResultsViewModel.AccountResultsList[num].AccountName;
        string accountid = "";

        if (isServer)
        {
            accountname = accountname + "@" + usersViewModel.ZimbraDomain;
            accountid = usersViewModel.UsersList[num].Username;

            int idx = accountid.IndexOf("@");

            if (idx != -1)                      // domain would be Exchange domain, not Zimbra domain
                accountid = accountid.Substring(0, idx);
        }
        else
        {
            ConfigViewModelU sourceModel =
                ((ConfigViewModelU)ViewModelPtrs[(int)ViewType.USRSRC]);
            ConfigViewModelUDest destModel =
                ((ConfigViewModelUDest)ViewModelPtrs[(int)ViewType.USRDEST]);

            accountname = ZimbraValues.GetZimbraValues().AccountName;//accountname + "@" + destModel.ZimbraServerHostName;
            accountid = (sourceModel.IspST) ? sourceModel.PSTFile :
                sourceModel.ProfileList[sourceModel.CurrentProfileSelection];
        }
        MyAcct.AccountName = accountname;
        MyAcct.AccountID = accountid;
        MyAcct.AccountNum = num;
        MyAcct.OnChanged += new MigrationObjectEventHandler(Acct_OnAcctChanged);

        MigrationFolder MyFolder = new MigrationFolder();

        MyFolder.AccountNum = num;
        MyFolder.OnChanged += new MigrationObjectEventHandler(Folder_OnChanged);

        MyAcct.migrationFolder = MyFolder;

        CSMigrationWrapper mw = ((IntroViewModel)ViewModelPtrs[(int)ViewType.INTRO]).mw;
        MigrationOptions importOpts = SetOptions();
        bool isVerbose = ((OptionsViewModel)ViewModelPtrs[(int)ViewType.OPTIONS]).LoggingVerbose;
        bool doRulesAndOOO = ((OptionsViewModel)ViewModelPtrs[(int)ViewType.OPTIONS]).OEnableRulesAndOOO;
        

        if (isVerbose)
        {
            if (importOpts.VerboseOn < LogLevel.Debug)
            {
                importOpts.VerboseOn = LogLevel.Debug;
            }

        }

        //mw.StartMigration(MyAcct, importOpts, isServer, (isVerbose ? (LogLevel.Debug):(LogLevel.Info)), m_isPreview);
        mw.StartMigration(MyAcct, importOpts, isServer, importOpts.VerboseOn, m_isPreview, doRulesAndOOO);

        // special case to format last user progress message
        int count = accountResultsViewModel.AccountResultsList[num].UserResultsList.Count;
        if (count > 0)
        {
            if (!m_isPreview)
            {
                string lastmsg = accountResultsViewModel.AccountResultsList[num].UserResultsList[count - 1].UserProgressMsg;
                int len = lastmsg.Length;
                bool isOOOorRules = ((MyFolder.FolderView == "OOO") || (MyFolder.FolderView == "All Rules"));
                accountResultsViewModel.AccountResultsList[num].UserResultsList[count - 1].UserProgressMsg = FormatTheLastMsg(MyFolder, isOOOorRules);
                accountResultsViewModel.AccountResultsList[num].PBValue = 100;  // to make sure
                if (accountResultsViewModel.AccountResultsList[num].CurrentItemNum != accountResultsViewModel.AccountResultsList[num].TotalItemsToMigrate)
                {
                    FormatGlobalMsg(accountResultsViewModel.AccountResultsList[num]);
                }
            }
            else
            {   // For preview, take the "foldername (n items)" message we constructed, extract the n, and make "Total n"
                string msg = "";
                string lastmsg = accountResultsViewModel.AccountResultsList[num].PBMsgValue;
                int idxParen = lastmsg.IndexOf("(");
                int idxItems = lastmsg.IndexOf("items");
                if ((idxParen != -1) && (idxItems != -1))
                {
                    int numLen = idxItems - idxParen - 2;   // for the paren and the space
                    string numStr = lastmsg.Substring(idxParen + 1, numLen);
                    msg = "Total: " + numStr;
                    accountResultsViewModel.AccountResultsList[num].UserResultsList[count - 1].UserProgressMsg = (msg.Length > 0) ? msg : "";
                }
            }
        }
        /////

        if (!m_isPreview)
        {
            accountResultsViewModel.AccountResultsList[num].PBMsgValue = "Migration complete";
            accountResultsViewModel.AccountResultsList[num].AcctProgressMsg = "Complete";
        }
        else
        {
            string msg = "Total items: {0}";
            accountResultsViewModel.AccountResultsList[num].PBMsgValue = String.Format(msg, accountResultsViewModel.AccountResultsList[num].TotalItemsToMigrate);
        }
    }

    private void worker_ProgressChanged(object sender,
        System.ComponentModel.ProgressChangedEventArgs e)
    {
    }
    
    private void worker_RunWorkerCompleted(object sender,
        System.ComponentModel.RunWorkerCompletedEventArgs e)
    {
        AccountResultsViewModel accountResultsViewModel =
            ((AccountResultsViewModel)ViewModelPtrs[(int)ViewType.RESULTS]);

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
            if (!m_isPreview)
            {
                accountResultsViewModel.PBMsgValue = "Migration complete";
                if (overflowList.Count == 0)
                {
                    SchedList.Clear();
                    UsersViewModel usersViewModel = ((UsersViewModel)ViewModelPtrs[(int)ViewType.USERS]);
                    usersViewModel.UsersList.Clear();
                }
            }
            accountResultsViewModel.EnableStop = false;
        }
        if (!m_isPreview)
        {
            m_isComplete = true;
        }
        EnablePreview = EnableMigrate = !m_isComplete;
        if (overflowList.Count > 0)
        {
            int usernum = overflowList[0];
            int threadnum = AvailableThread();
            if (threadnum != -1)
            {
                bgwlist[threadnum].usernum = usernum;
                bgwlist[threadnum].RunWorkerAsync(usernum);
            }
            overflowList.RemoveAt(0);
        }
    }

    public int GetThreadNum(int usernum)
    {
        int ct = bgwlist.Count;
        for (int i = 0; i < ct; i++)
        {
            if (bgwlist[i].usernum == usernum)
            {
                return bgwlist[i].threadnum;
            }
        }
        return -1;
    }

    public void Acct_OnAcctChanged(object sender, MigrationObjectEventArgs e)
    {
        string msg = "";
        MigrationAccount a = (MigrationAccount)sender;
        AccountResultsViewModel accountResultsViewModel =
            ((AccountResultsViewModel)ViewModelPtrs[(int)ViewType.RESULTS]);    // main one
        AccountResultsViewModel ar = accountResultsViewModel.AccountResultsList[a.AccountNum];

        if (e.PropertyName == "TotalItems")
        {
            ar.TotalItemsToMigrate = Int32.Parse(e.NewValue.ToString());
        }
        if (e.PropertyName == "TotalErrors")
        {
            ar.NumErrs = (int)a.TotalErrors + 1;      // this happens first
            ar.AccountProblemsList.Add(a.LastProblemInfo);
            OptionsViewModel ovm = ((OptionsViewModel)ViewModelPtrs[(int)ViewType.OPTIONS]);
            if (ovm.MaxErrorCount > 0)
            {
                if (ar.NumErrs > ovm.MaxErrorCount)
                {
                    for (int i = 0; i < this.BGWList.Count; i++)
                    {
                        this.BGWList[i].CancelAsync();
                    }
                }
            }
        }
        else if (e.PropertyName == "TotalWarnings")
        {
            ar.NumWarns = (int)a.TotalWarnings + 1;   // this happens first
            ar.AccountProblemsList.Add(a.LastProblemInfo);
        }
        else
        {
            msg = "Begin {0} Migration";
            ar.PBMsgValue = String.Format(msg, a.AccountName);
            accountResultsViewModel.PBMsgValue = String.Format(msg, a.AccountName);     // for the user results window
        }
    }

    public void Folder_OnChanged(object sender, MigrationObjectEventArgs e)
    {
        MigrationFolder f = (MigrationFolder)sender;
        AccountResultsViewModel accountResultsViewModel =
            ((AccountResultsViewModel)ViewModelPtrs[(int)ViewType.RESULTS]);    // main one
        AccountResultsViewModel ar = accountResultsViewModel.AccountResultsList[f.AccountNum];

        int tnum = GetThreadNum(f.AccountNum);
        if (bgwlist[tnum].CancellationPending)
        {
            eventArglist[f.AccountNum].Cancel = true;
            return;
        }
        if (e.PropertyName == "CurrentCountOfItems")
        {
            if (f.FolderName != null)
            {
                if (e.NewValue.ToString() != "0")
                {
                    string msg1 = "{0} of {1}";
                    string msgF = String.Format(msg1, f.CurrentCountOfItems, f.TotalCountOfItems);
                    ar.AcctProgressMsg = msgF;

                    int count = ar.UserResultsList.Count;
                    ar.UserResultsList[count - 1].UserProgressMsg = msgF;
                    accountResultsViewModel.PBValue = accountResultsViewModel.AccountResultsList[f.AccountNum].PBValue;
                    accountResultsViewModel.UserPBMsgValue = accountResultsViewModel.AccountResultsList[f.AccountNum].PBMsgValue;
                    ar.CurrentItemNum++;
                    ar.PBValue = (int)Math.Round(((Decimal)ar.CurrentItemNum /
                        (Decimal)ar.TotalItemsToMigrate) * 100);

                    // FBS bug 74960 -- 6/1/12
                    string msg2 = "{0} of {1} ({2}%)";
                    string msgG = String.Format(msg2, ar.CurrentItemNum, ar.TotalItemsToMigrate, ar.PBValue);
                    ar.GlobalAcctProgressMsg = msgG;

                    bgwlist[tnum].ReportProgress(ar.PBValue, f.AccountNum);
                }
            }
        }
        if (e.PropertyName == "TotalCountOfItems")      // finish up with the last folder
        {
            if (f.FolderName != null)
            {
                string msg2 = "";
                string msgF = "";
                if (!m_isPreview)
                {
                    msg2 = "{0} of {1}";
                    msgF = String.Format(msg2, f.CurrentCountOfItems, f.TotalCountOfItems);
                    ar.AcctProgressMsg = msgF;
                }
                else
                {
                    msg2 = "Total: {0}";
                    msgF = String.Format(msg2, f.TotalCountOfItems);
                }
                int count = ar.UserResultsList.Count;
                ar.UserResultsList[count - 1].UserProgressMsg = msgF;
                accountResultsViewModel.PBValue = accountResultsViewModel.AccountResultsList[f.AccountNum].PBValue;
                accountResultsViewModel.UserPBMsgValue = accountResultsViewModel.AccountResultsList[f.AccountNum].PBMsgValue;
            }
        }
        if (e.PropertyName == "FolderName")
        {
            if (e.NewValue != null)
            {
                string folderName = e.NewValue.ToString();
                string folderType = GetFolderTypeForUserResults(f.FolderView);
                string msg3 = "";
                if (!m_isPreview)
                {
                    msg3 = "Migrating {0}";
                    ar.PBMsgValue = String.Format(msg3, folderName);
                    accountResultsViewModel.PBMsgValue = String.Format(msg3, folderName);   // for the user results window
                }
                else
                {
                    msg3 = "{0} ({1} items)";
                    ar.PBMsgValue = String.Format(msg3, folderName, f.TotalCountOfItems);
                    accountResultsViewModel.PBMsgValue = String.Format(msg3, folderName, f.TotalCountOfItems);   // for the user results window
                    System.Threading.Thread.Sleep(500);    // to see the message
                }

                f.LastFolderInfo = new FolderInfo(e.NewValue.ToString(), folderType,
                    string.Format("{0} of {1}", f.CurrentCountOfItems,
                    f.TotalCountOfItems));

                ar.UserResultsList.Add(new UserResultsViewModel(folderName, folderType, ar.AcctProgressMsg));
            }
        }
    }
}
}
