using CssLib;
using MVVM.Model;
using Misc;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Collections;
using System.ComponentModel;
using System.Diagnostics;
using System.IO;
using System.Threading;
using System.Windows.Input;
using System.Windows;
using System.Windows.Threading;
using System;

namespace MVVM.ViewModel
{
public class AccountResultsViewModel: BaseViewModel
{
    readonly AccountResults m_accountResults = new AccountResults();
    ScheduleViewModel m_scheduleViewModel;
    int m_accountnum;
    int m_AccountOnTab;

    public AccountResultsViewModel(ScheduleViewModel scheduleViewModel, int accountNum, int
        pbValue, string pbMsgValue, string userpbMsgValue, string accountName, int
        accountProgress, string acctProgressMsg, int numErrs, int numWarns, bool enableStop)
    {
        this.m_scheduleViewModel = scheduleViewModel;
        this.m_accountnum = accountNum;
        this.PBValue = pbValue;
        this.PBMsgValue = pbMsgValue;
        this.UserPBMsgValue = userpbMsgValue;
        this.AccountName = accountName;
        this.AccountProgress = accountProgress;
        this.AcctProgressMsg = acctProgressMsg;
        this.NumErrs = numErrs;
        this.NumWarns = numWarns;
        this.EnableStop = enableStop;

        this.CurrentItemNum = 0;
        this.TotalItemsToMigrate = 0;

        this.SelectedTab = "";
        this.m_AccountOnTab = -1;

        this.OpenLogFileCommand = new ActionCommand(this.OpenLogFile, () => true);
        this.StopCommand = new ActionCommand(this.Stop, () => true);
        this.ExitAppCommand = new ActionCommand(this.ExitApp, () => true);
    }

    public int GetAccountNum()
    {
        return m_accountnum;
    }

    // Commands
    public ICommand OpenLogFileCommand {
        get;
        private set;
    }
    private void OpenLogFile()
    {
        string AcctName = ((SelectedTab == "Accounts") || (SelectedTab == "")) ?
            AccountResultsList[CurrentAccountSelection].AccountName : SelectedTab;
        string Logfile = Path.GetTempPath() + AcctName + ".log";
        Process.Start(Logfile);
    }
    public ICommand StopCommand {
        get;
        private set;
    }
    private void Stop()
    {
        for (int i = 0; i < m_scheduleViewModel.BGWList.Count; i++)
        {
            m_scheduleViewModel.BGWList[i].CancelAsync();
        }
        m_scheduleViewModel.EnableMigrate = true;

        // Don't uninitialize -- this should eventually set the static stop in csslib.
        // Go through each thread and stop each user?

        // CSMigrationwrapper mw = ((IntroViewModel)ViewModelPtrs[(int)ViewType.INTRO]).mw;
        // string ret = mw.UninitializeMailClient();

        EnableStop = !m_scheduleViewModel.EnableMigrate;
    }
    public ICommand ExitAppCommand {
        get;
        private set;
    }
    private void ExitApp()
    {
        Application.Current.Shutdown();
    }

    //

    private ObservableCollection<AccountResultsViewModel> accountResultsList =
        new ObservableCollection<AccountResultsViewModel>();
    public ObservableCollection<AccountResultsViewModel> AccountResultsList {
        get { return accountResultsList; }
    }
    private ObservableCollection<ProblemInfo> accountProblemsList =
        new ObservableCollection<ProblemInfo>();
    public ObservableCollection<ProblemInfo> AccountProblemsList {
        get { return accountProblemsList; }
    }
    private ObservableCollection<UserResultsViewModel> m_userResultsList =
        new ObservableCollection<UserResultsViewModel>();
    public ObservableCollection<UserResultsViewModel> UserResultsList
    {
        get { return m_userResultsList; }
    }

    public int PBValue {
        get { return m_accountResults.PBValue; }
        set
        {
            if (value == m_accountResults.PBValue)
                return;
            m_accountResults.PBValue = value;
            OnPropertyChanged(new PropertyChangedEventArgs("PBValue"));
        }
    }
    public string PBMsgValue {
        get { return m_accountResults.PBMsgValue; }
        set
        {
            if (value == m_accountResults.PBMsgValue)
                return;
            m_accountResults.PBMsgValue = value;
            OnPropertyChanged(new PropertyChangedEventArgs("PBMsgValue"));
        }
    }
    public string UserPBMsgValue {
        get { return m_accountResults.UserPBMsgValue; }
        set
        {
            if (value == m_accountResults.UserPBMsgValue)
                return;
            m_accountResults.UserPBMsgValue = value;
            OnPropertyChanged(new PropertyChangedEventArgs("UserPBMsgValue"));
        }
    }
    public string AccountName {
        get { return m_accountResults.AccountName; }
        set
        {
            if (value == m_accountResults.AccountName)
                return;
            m_accountResults.AccountName = value;
            OnPropertyChanged(new PropertyChangedEventArgs("AccountName"));
        }
    }
    public int CurrentItemNum {
        get { return m_accountResults.CurrentItemNum; }
        set
        {
            if (value == m_accountResults.CurrentItemNum)
                return;
            m_accountResults.CurrentItemNum = value;
            OnPropertyChanged(new PropertyChangedEventArgs("CurrentItemNum"));
        }
    }
    public int TotalItemsToMigrate {
        get { return m_accountResults.TotalItemsToMigrate; }
        set
        {
            if (value == m_accountResults.TotalItemsToMigrate)
                return;
            m_accountResults.TotalItemsToMigrate = value;
            OnPropertyChanged(new PropertyChangedEventArgs("TotalItemsToMigrate"));
        }
    }
    public int AccountProgress {
        get { return m_accountResults.AccountProgress; }
        set
        {
            if (value == m_accountResults.AccountProgress)
                return;
            m_accountResults.AccountProgress = value;
            OnPropertyChanged(new PropertyChangedEventArgs("AccountProgress"));
        }
    }
    public string AcctProgressMsg {
        get { return m_accountResults.AcctProgressMsg; }
        set
        {
            if (value == m_accountResults.AcctProgressMsg)
                return;
            m_accountResults.AcctProgressMsg = value;
            OnPropertyChanged(new PropertyChangedEventArgs("AcctProgressMsg"));
        }
    }
    public int NumErrs {
        get { return m_accountResults.NumErrs; }
        set
        {
            if (value == m_accountResults.NumErrs)
                return;
            m_accountResults.NumErrs = value;
            OnPropertyChanged(new PropertyChangedEventArgs("NumErrs"));
        }
    }
    public int NumWarns {
        get { return m_accountResults.NumWarns; }
        set
        {
            if (value == m_accountResults.NumWarns)
                return;
            m_accountResults.NumWarns = value;
            OnPropertyChanged(new PropertyChangedEventArgs("NumWarns"));
        }
    }
    public int CurrentAccountSelection {
        get { return m_accountResults.CurrentAccountSelection; }
        set
        {
            if (value == m_accountResults.CurrentAccountSelection)
                return;
            m_accountResults.CurrentAccountSelection = value;
            OpenLogFileEnabled = (value != -1);
            OnPropertyChanged(new PropertyChangedEventArgs("CurrentAccountSelection"));
        }
    }
    private bool openLogFileEnabled;
    public bool OpenLogFileEnabled {
        get { return openLogFileEnabled; }
        set
        {
            openLogFileEnabled = value;
            OnPropertyChanged(new PropertyChangedEventArgs("OpenLogFileEnabled"));
        }
    }
    private bool enableStop;
    public bool EnableStop {
        get { return enableStop; }
        set
        {
            enableStop = value;
            OnPropertyChanged(new PropertyChangedEventArgs("EnableStop"));
        }
    }
    public string SelectedTab {
        get { return m_accountResults.SelectedTab; }
        set
        {
            if (value == m_accountResults.SelectedTab)
                return;
            m_accountResults.SelectedTab = value;
            OnPropertyChanged(new PropertyChangedEventArgs("SelectedTab"));
        }
    }
    public int AccountOnTab {
        get { return m_AccountOnTab; }
        set { m_AccountOnTab = value; }
    }
}
}
