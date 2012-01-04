using CssLib;
using MVVM.Model;
using Misc;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Collections;
using System.ComponentModel;
using System.Diagnostics;
using System.IO;
using System.Linq;
using System.Windows.Input;
using System.Windows;
using System;

namespace MVVM.ViewModel
{
public class UsersViewModel: BaseViewModel
{
    readonly Users m_users = new Users("", "", -1);

    public UsersViewModel(string username, string mappedname)
    {
        this.ObjectPickerCommand = new ActionCommand(this.ObjectPicker, () => true);
        this.LDAPBrowserCommand = new ActionCommand(this.LDAPBrowser, () => true);
        this.UserMapCommand = new ActionCommand(this.UserMap, () => true);
        this.AddCommand = new ActionCommand(this.Add, () => true);
        this.RemoveCommand = new ActionCommand(this.Remove, () => true);
        this.SaveCSVCommand = new ActionCommand(this.SaveCSV, () => true);
        this.BackCommand = new ActionCommand(this.Back, () => true);
        this.NextCommand = new ActionCommand(this.Next, () => true);
        this.Username = username;
        this.MappedName = mappedname;
        this.IsProvisioned = false;
        this.EnablePopButtons = true;
    }

    // Commands
    public ICommand ObjectPickerCommand {
        get;
        private set;
    }
    private void ObjectPicker()
    {
        EnablePopButtons = false;

        CSMigrationwrapper mw = ((IntroViewModel)ViewModelPtrs[(int)ViewType.INTRO]).mw;

        string[] users = mw.GetListFromObjectPicker();
        for (int i = 0; i < users.Length; i++)
        {
            // Later, we'll return a struct that will have username and displayname (as destination name)
            string displayname = "";

            if (users[i].IndexOf("@") != -1)
                displayname = users[i].Substring(0, users[i].IndexOf("@"));
            UsersList.Add(new UsersViewModel(users[i], displayname));

            ScheduleViewModel scheduleViewModel =
                ((ScheduleViewModel)ViewModelPtrs[(int)ViewType.SCHED]);

            scheduleViewModel.SchedList.Add(new SchedUser(Username, false));
            scheduleViewModel.EnableMigrate = (scheduleViewModel.SchedList.Count > 0);
            EnableNext = (UsersList.Count > 0);
        }
        EnablePopButtons = true;
    }
    public ICommand LDAPBrowserCommand {
        get;
        private set;
    }
    private void LDAPBrowser()
    {
        EnablePopButtons = false;

        QueryBuilderDlg qbDlg = new QueryBuilderDlg(this);

        qbDlg.Owner = Application.Current.MainWindow;
        qbDlg.ShowDialog();

        ScheduleViewModel scheduleViewModel =
            ((ScheduleViewModel)ViewModelPtrs[(int)ViewType.SCHED]);

        scheduleViewModel.SchedList.Add(new SchedUser(Username, false));
        scheduleViewModel.EnableMigrate = (scheduleViewModel.SchedList.Count > 0);
        EnableNext = (UsersList.Count > 0);
        EnablePopButtons = true;
    }
    public ICommand UserMapCommand {
        get;
        private set;
    }
    private void UserMap()
    {
        ScheduleViewModel scheduleViewModel =
            ((ScheduleViewModel)ViewModelPtrs[(int)ViewType.SCHED]);
        bool bCSV = false;

        Microsoft.Win32.OpenFileDialog fDialog = new Microsoft.Win32.OpenFileDialog();
        fDialog.Filter = "User Map Files|*.xml;*.csv";
        fDialog.CheckFileExists = true;
        fDialog.Multiselect = false;
        if (fDialog.ShowDialog() == true)
        {
            int lastDot = fDialog.FileName.LastIndexOf(".");

            if (lastDot != -1)
            {
                string substr = fDialog.FileName.Substring(lastDot, 4);

                if (substr == ".csv")
                {
                    bCSV = true;

                    /*try
                     * {
                     *  string names = File.ReadAllText(fDialog.FileName);
                     *  string[] nameTokens = names.Split(',');
                     *  foreach (string name in nameTokens)
                     *  {
                     *      UsersList.Add(name);
                     *      scheduleViewModel.SchedList.Add(name);
                     *  }
                     * }
                     * catch (IOException ex)
                     * {
                     *  MessageBox.Show(ex.Message, "Zimbra Migration", MessageBoxButton.OK, MessageBoxImage.Exclamation);
                     * }*/
                    List<string[]> parsedData = new List<string[]>();
                    try
                    {
                        if (File.Exists(fDialog.FileName))
                        {
                            using (StreamReader readFile = new StreamReader(fDialog.FileName)) {
                                string line;

                                string[] row;
                                while ((line = readFile.ReadLine()) != null)
                                {
                                    row = line.Split(',');
                                    parsedData.Add(row);
                                }
                                readFile.Close();
                            }
                        }
                        else
                        {
                            MessageBox.Show(
                                "There is no user information stored.Please enter some user info",
                                "Zimbra Migration", MessageBoxButton.OK,
                                MessageBoxImage.Error);
                        }
                    }
                    catch (Exception e)
                    {
                        string message = e.Message;
                    }
                    // for (int i = 1; i < parsedData.Count; i++)
                    {
                        string[] strres = new string[parsedData.Count];

                        Users tempuser = new Users();

                        try
                        {
                            for (int j = 0; j < parsedData.Count; j++)
                            {
                                strres = parsedData[j];
                                if (strres[j].Contains("#"))
                                    continue;
                                tempuser.UserName = strres[0];
                                tempuser.MappedName = strres[1];

                                // tempuser.ChangePWD = Convert.ToBoolean(strres[2]);
                                // tempuser.PWDdefault = strres[3];
                                // string result = tempuser.UserName + "," + tempuser.MappedName +"," + tempuser.ChangePWD + "," + tempuser.PWDdefault;
                                string result = tempuser.Username + "," + tempuser.MappedName;

                                Username = strres[0];
                                MappedName = strres[1];
                                UsersList.Add(new UsersViewModel(Username, MappedName));
                                scheduleViewModel.SchedList.Add(new SchedUser(Username, false));
                            }
                        }
                        catch (Exception)
                        {
                            MessageBox.Show("Incorrect .csv file format", "Zimbra Migration", MessageBoxButton.OK, MessageBoxImage.Error);
                            return;
                        }

                        EnableNext = (UsersList.Count > 0);
                    }
                    scheduleViewModel.EnableMigrate = (scheduleViewModel.SchedList.Count > 0);

                    // /
                    // Domain information is stored in the xml and not in  the usermap.
                    // will have to revisit

                    System.Xml.Serialization.XmlSerializer reader =
                        new System.Xml.Serialization.XmlSerializer(typeof (Config));
                    if (File.Exists(scheduleViewModel.GetConfigFile()))
                    {
                        System.IO.StreamReader fileRead = new System.IO.StreamReader(
                            scheduleViewModel.GetConfigFile());

                        Config Z11 = new Config();

                        Z11 = (Config)reader.Deserialize(fileRead);
                        fileRead.Close();
                        ZimbraDomain = Z11.UserProvision.Domain;
                        if (DomainList.Count > 0)
                            CurrentDomainSelection = (ZimbraDomain == null) ? 0 :
                                DomainList.IndexOf(ZimbraDomain);

                        else
                            DomainList.Add(ZimbraDomain);
                    }
                    scheduleViewModel.SetUsermapFile(fDialog.FileName);
                }
            }
            if (!bCSV)
                MessageBox.Show("Only CSV files are supported", "Zimbra Migration",
                    MessageBoxButton.OK, MessageBoxImage.Exclamation);
        }
    }
    public ICommand AddCommand {
        get;
        private set;
    }
    private void Add(object value)
    {
        var name = value as string;

        UsersList.Add(new UsersViewModel("", ""));
        EnableNext = (UsersList.Count > 0);

        ScheduleViewModel scheduleViewModel =
            ((ScheduleViewModel)ViewModelPtrs[(int)ViewType.SCHED]);

        scheduleViewModel.SchedList.Add(new SchedUser(name, false));
        scheduleViewModel.EnableMigrate = (scheduleViewModel.SchedList.Count > 0);
    }
    public ICommand RemoveCommand {
        get;
        private set;
    }
    private void Remove()
    {
        UsersList.RemoveAt(CurrentUserSelection);
        EnableNext = (UsersList.Count > 0);

        ScheduleViewModel scheduleViewModel =
            ((ScheduleViewModel)ViewModelPtrs[(int)ViewType.SCHED]);

        scheduleViewModel.EnableMigrate = (scheduleViewModel.SchedList.Count > 0);
    }
    public ICommand SaveCSVCommand {
        get;
        private set;
    }
    private void SaveCSV()
    {
        List<Users> ListofUsers = new List<Users>();
        for (int i = 0; i < UsersList.Count; i++)
        {
            string users = UsersList[i].Username + ',' + UsersList[i].MappedName;

            string[] nameTokens = users.Split(',');

            Users tempUser = new Users();

            tempUser.UserName = nameTokens.GetValue(0).ToString();
            tempUser.MappedName = nameTokens.GetValue(1).ToString();
            // tempUser.ChangePWD = Convert.ToBoolean(nameTokens.GetValue(2).ToString());
            // tempUser.PWDdefault = nameTokens.GetValue(3).ToString();

            ListofUsers.Add(tempUser);
        }

        string resultcsv = Users.ToCsv<Users>(",", ListofUsers);

        Microsoft.Win32.SaveFileDialog fDialog = new Microsoft.Win32.SaveFileDialog();
        fDialog.Filter = "User Map Files|*.csv";

        // fDialog.CheckFileExists = true;
        // fDialog.Multiselect = false;

        ScheduleViewModel scheduleViewModel =
            ((ScheduleViewModel)ViewModelPtrs[(int)ViewType.SCHED]);

        if (fDialog.ShowDialog() == true)
        {
            string filename = fDialog.FileName;

            // +".csv";
            System.IO.File.WriteAllText(filename, resultcsv);
            scheduleViewModel.SetUsermapFile(filename);
        }
        // /Domain information gets stored in the xml
        // ///will have to revisit.
        SaveDomain();
    }
    public ICommand BackCommand {
        get;
        private set;
    }
    private void Back()
    {
        lb.SelectedIndex = 3;
    }
    public ICommand NextCommand {
        get;
        private set;
    }
    private void Next()
    {
        if (!ValidateUsersList())
            return;
        ZimbraAPI zimbraAPI = new ZimbraAPI();
        if (ZimbraValues.zimbraValues.AuthToken.Length == 0)
        {
            MessageBox.Show("You must log on to the Zimbra server", "Zimbra Migration",
                MessageBoxButton.OK, MessageBoxImage.Error);
            return;
        }
        SaveDomain();

        ScheduleViewModel scheduleViewModel =
            ((ScheduleViewModel)ViewModelPtrs[(int)ViewType.SCHED]);

        for (int i = 0; i < UsersList.Count; i++)
        {
            string userName = (UsersList[i].MappedName.Length > 0) ? UsersList[i].MappedName :
                UsersList[i].Username;
            string acctName = userName + '@' + ZimbraDomain;

            if (zimbraAPI.GetAccount(acctName) == 0)
            {
                UsersList[i].IsProvisioned = true;
                scheduleViewModel.SchedList[i].isProvisioned = true;    // get (SchedList) in schedule view model will set again
            }
            else if (zimbraAPI.LastError.IndexOf("no such account") != -1)
            {
                UsersList[i].IsProvisioned = false;     // get (SchedList) in schedule view model will set again
                scheduleViewModel.SchedList[i].isProvisioned = false;
            }
            else
            {
                MessageBox.Show(string.Format("Error accessing account {0}: {1}", acctName,
                    zimbraAPI.LastError), "Zimbra Migration", MessageBoxButton.OK,
                    MessageBoxImage.Error);
            }
        }
        lb.SelectedIndex = 5;
    }

    public const int TYPE_USERNAME = 1;
    public const int TYPE_MAPNAME = 2;
    private bool isDuplicate(string nam, int type)
    {
        bool bRetval = false;
        int iHitCount = 0;

        for (int i = 0; i < UsersList.Count; i++)
        {
            string nam2 = (type == TYPE_USERNAME) ? UsersList[i].Username :
                UsersList[i].MappedName;

            if (nam == nam2)
            {
                iHitCount++;
                if (iHitCount == 2)
                {
                    bRetval = true;
                    break;
                }
            }
        }
        return bRetval;
    }

    public bool ValidateUsersList()
    {
        // Make sure there are no blanks or duplicates in the list; remove them if there are.
        // If we get down to no items, disable the Next button.
        for (int i = UsersList.Count - 1; i >= 0; i--)
        {
            if (UsersList[i].Username.Length == 0)
            {
                UsersList.RemoveAt(i);
            }
            else if (isDuplicate(UsersList[i].Username, TYPE_USERNAME))
            {
                UsersList.RemoveAt(i);
            }
            else if (UsersList[i].MappedName.Length > 0)
            {
                if (isDuplicate(UsersList[i].MappedName, TYPE_MAPNAME))
                    UsersList.RemoveAt(i);
            }
        }
        if (UsersList.Count == 0)
        {
            EnableNext = false;
            return false;
        }
        return true;
    }

    public void LoadDomain(Config config)
    {
        CurrentDomainSelection = 0;

        string d = config.UserProvision.Domain;

        if (DomainList.Count > 0)
        {
            for (int i = 0; i < DomainList.Count; i++)
            {
                if (d == DomainList[i])
                {
                    CurrentDomainSelection = i;
                    break;
                }
            }
        }
        else
        {
            DomainList.Add(d);
        }
    }

    private void SaveDomain()
    {
        try
        {
            ScheduleViewModel scheduleViewModel =
                ((ScheduleViewModel)ViewModelPtrs[(int)ViewType.SCHED]);

            ZimbraDomain = DomainList[CurrentDomainSelection];
            if (scheduleViewModel.GetConfigFile().Length > 0)
            {
                if (File.Exists(scheduleViewModel.GetConfigFile()))
                {
                    UpdateXmlElement(scheduleViewModel.GetConfigFile(), "UserProvision");
                }
                else
                {
                    System.Xml.Serialization.XmlSerializer writer =
                        new System.Xml.Serialization.XmlSerializer(typeof (Config));

                    System.IO.StreamWriter file = new System.IO.StreamWriter(
                        scheduleViewModel.GetConfigFile());
                    writer.Serialize(file, m_config);
                    file.Close();
                }
            }
        }
        catch (ArgumentOutOfRangeException e)
        {
            string message = "CurrentDomainSelection " + e.Message;

            MessageBox.Show(message, "Zimbra Migration", MessageBoxButton.OK,
                MessageBoxImage.Warning);
        }
    }

    // //////////////////////

    private ObservableCollection<UsersViewModel> userslist =
        new ObservableCollection<UsersViewModel>();
    public ObservableCollection<UsersViewModel> UsersList {
        get { return userslist; }
    }
    private ObservableCollection<string> domainlist = new ObservableCollection<string>();
    public ObservableCollection<string> DomainList {
        get { return domainlist; }
        set
        {
            domainlist = value;
        }
    }
    public string Username {
        get { return m_users.Username; }
        set
        {
            if (value == m_users.Username)
                return;
            m_users.Username = value;
            OnPropertyChanged(new PropertyChangedEventArgs("Username"));
        }
    }
    public string MappedName {
        get { return m_users.MappedName; }
        set
        {
            if (value == m_users.MappedName)
                return;
            m_users.MappedName = value;
            OnPropertyChanged(new PropertyChangedEventArgs("MappedName"));
        }
    }
    public string ZimbraDomain {
        get { return m_config.UserProvision.Domain; }
        set
        {
            if (value == m_config.UserProvision.Domain)
                return;
            m_config.UserProvision.Domain = value;

            OnPropertyChanged(new PropertyChangedEventArgs("ZimbraDomain"));
        }
    }
    public int CurrentUserSelection {
        get { return m_users.CurrentUserSelection; }
        set
        {
            if (value == m_users.CurrentUserSelection)
                return;
            m_users.CurrentUserSelection = value;
            MinusEnabled = (value != -1);
            OnPropertyChanged(new PropertyChangedEventArgs("CurrentUserSelection"));
        }
    }
    public int CurrentDomainSelection {
        get { return domainselection; }
        set
        {
            domainselection = value;

            OnPropertyChanged(new PropertyChangedEventArgs("CurrentDomainSelection"));
        }
    }
    private int domainselection;
    private bool minusEnabled;
    public bool MinusEnabled {
        get { return minusEnabled; }
        set
        {
            minusEnabled = value;
            OnPropertyChanged(new PropertyChangedEventArgs("MinusEnabled"));
        }
    }
    private bool isProvisioned;
    public bool IsProvisioned {
        get { return isProvisioned; }
        set
        {
            isProvisioned = value;
            OnPropertyChanged(new PropertyChangedEventArgs("IsProvisioned"));
        }
    }
    private bool enableNext;
    public bool EnableNext {
        get { return enableNext; }
        set
        {
            enableNext = value;
            OnPropertyChanged(new PropertyChangedEventArgs("EnableNext"));
        }
    }
    private bool enablePopButtons;
    public bool EnablePopButtons {
        get { return enablePopButtons; }
        set
        {
            enablePopButtons = value;
            OnPropertyChanged(new PropertyChangedEventArgs("EnablePopButtons"));
        }
    }
}
}
