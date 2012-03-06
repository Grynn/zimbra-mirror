using MVVM.Model;
using Misc;
using System.Collections.Generic;
using System.ComponentModel;
using System.Diagnostics;
using System.IO;
using System.Windows.Input;
using System.Windows;
using System.Xml.Linq;
using System.Xml.Serialization;
using System.Xml;
using System;

namespace MVVM.ViewModel
{
public class OptionsViewModel: BaseViewModel
{
    public OptionsViewModel()
    {
        this.LoadCommand = new ActionCommand(this.Load, () => true);
        this.SaveCommand = new ActionCommand(this.Save, () => true);
        this.BackCommand = new ActionCommand(this.Back, () => true);
        this.NextCommand = new ActionCommand(this.Next, () => true);
    }
    public ICommand LoadCommand {
        get;
        private set;
    }
    public ICommand SaveCommand {
        get;
        private set;
    }
    public ICommand BackCommand {
        get;
        private set;
    }
    public ICommand NextCommand {
        get;
        private set;
    }
    public void LoadConfig(Config config)
    {
        ImportMailOptions = config.ImportOptions.Mail;
        ImportCalendarOptions = config.ImportOptions.Calendar;
        ImportContactOptions = config.ImportOptions.Contacts;
        ImportDeletedItemOptions = config.ImportOptions.DeletedItems;
        ImportJunkOptions = config.ImportOptions.Junk;
        ImportTaskOptions = config.ImportOptions.Tasks;
        ImportSentOptions = config.ImportOptions.Sent;
        ImportRuleOptions = config.ImportOptions.Rules;
        ImportOOOOptions = config.ImportOptions.OOO;
        SetNextState();

        MigrateONRAfter = config.AdvancedImportOptions.MigrateOnOrAfter.ToLongDateString();
        IsOnOrAfter = config.AdvancedImportOptions.IsOnOrAfter;
        MaxMessageSize = config.AdvancedImportOptions.MaxMessageSize;
        IsMaxMessageSize = config.AdvancedImportOptions.IsMaxMessageSize;
        IsSkipFolders = config.AdvancedImportOptions.IsSkipFolders;

        if (config.GeneralOptions != null)  // so old config files will work
        {
            LoggingVerbose = config.GeneralOptions.Verbose;
        }

        string returnval = "";

        returnval = ConvertToCSV(config.AdvancedImportOptions.FoldersToSkip, ",");
        FoldersToSkip = returnval;
  
    }

    private void Load()
    {
        System.Xml.Serialization.XmlSerializer reader =
            new System.Xml.Serialization.XmlSerializer(typeof (Config));

        Microsoft.Win32.OpenFileDialog fDialog = new Microsoft.Win32.OpenFileDialog();
        fDialog.Filter = "Config Files|*.xml";
        fDialog.CheckFileExists = true;
        fDialog.Multiselect = false;
        if (fDialog.ShowDialog() == true)
        {
            if (File.Exists(fDialog.FileName))
            {
                System.IO.StreamReader fileRead = new System.IO.StreamReader(fDialog.FileName);

                Config config = new Config();

                try
                {
                    config = (Config)reader.Deserialize(fileRead);
                }
                catch (Exception e)
                {
                    string temp = string.Format("Incorrect configuration file format.\n{0}", e.Message);
                    MessageBox.Show(temp, "Zimbra Migration", MessageBoxButton.OK, MessageBoxImage.Error);
                    fileRead.Close();
                    return;
                }

                fileRead.Close();

                try
                {
                    LoadConfig(config);
                    if (isServer)
                    {
                        ((ConfigViewModelS)ViewModelPtrs[(int)ViewType.SVRSRC]).LoadConfig(config);
                        ((ConfigViewModelSDest)ViewModelPtrs[(int)ViewType.SVRDEST]).LoadConfig(
                            config);
                        ((UsersViewModel)ViewModelPtrs[(int)ViewType.USERS]).LoadDomain(config);
                    }
                    else
                    {
                        ((ConfigViewModelU)ViewModelPtrs[(int)ViewType.USRSRC]).LoadConfig(config);
                        ((ConfigViewModelUDest)ViewModelPtrs[(int)ViewType.USRDEST]).LoadConfig(
                            config);
                    }
                    ((ScheduleViewModel)ViewModelPtrs[(int)ViewType.SCHED]).SetConfigFile(
                        fDialog.FileName);
                }
                catch (Exception e)
                {
                    DisplayLoadError(e);
                    return;
                }
            }
            else
            {
                MessageBox.Show(
                    "There is no options configuration stored.  Please enter some options info",
                    "Zimbra Migration", MessageBoxButton.OK, MessageBoxImage.Error);
            }
        }
    }

    private void Save()
    {
        Microsoft.Win32.SaveFileDialog fDialog = new Microsoft.Win32.SaveFileDialog();
        fDialog.Filter = "Config Files|*.xml";
        if (fDialog.ShowDialog() == true)
        {
            System.Xml.Serialization.XmlSerializer writer =
                new System.Xml.Serialization.XmlSerializer(typeof(Config));

            System.IO.StreamWriter file = new System.IO.StreamWriter(fDialog.FileName);
            PopulateConfig(isServer);
            writer.Serialize(file, m_config);
            file.Close();

            ((ScheduleViewModel)ViewModelPtrs[(int)ViewType.SCHED]).SetConfigFile(
                fDialog.FileName);
        }
    }

    private void Back()
    {
        lb.SelectedIndex = 2;
    }

    private void Next()
    {
        if (isServer)
        {
            lb.SelectedIndex = 4;
        }
        else
        {
            ConfigViewModelUDest configViewModelUDest =
                ((ConfigViewModelUDest)ViewModelPtrs[(int)ViewType.USRDEST]);
            UsersViewModel usersViewModel =
                ((UsersViewModel)ViewModelPtrs[(int)ViewType.USERS]);
            ScheduleViewModel scheduleViewModel =
                ((ScheduleViewModel)ViewModelPtrs[(int)ViewType.SCHED]);
            string name = configViewModelUDest.ZimbraUser;

            usersViewModel.UsersList.Add(new UsersViewModel(name, ""));
            scheduleViewModel.Migrate();
        }
    }
    public bool ImportMailOptions {
        get { return m_config.ImportOptions.Mail; }
        set
        {
            if (value == m_config.ImportOptions.Mail)
                return;
            m_config.ImportOptions.Mail = value;

            OnPropertyChanged(new PropertyChangedEventArgs("ImportMailOptions"));
            SetNextState();
        }
    }
    public bool ImportTaskOptions {
        get { return m_config.ImportOptions.Tasks; }
        set
        {
            if (value == m_config.ImportOptions.Tasks)
                return;
            m_config.ImportOptions.Tasks = value;

            OnPropertyChanged(new PropertyChangedEventArgs("ImportTaskOptions"));
            SetNextState();
        }
    }
    public bool ImportCalendarOptions {
        get { return m_config.ImportOptions.Calendar; }
        set
        {
            if (value == m_config.ImportOptions.Calendar)
                return;
            m_config.ImportOptions.Calendar = value;

            OnPropertyChanged(new PropertyChangedEventArgs("ImportCalendarOptions"));
            SetNextState();
        }
    }
    public bool ImportContactOptions {
        get { return m_config.ImportOptions.Contacts; }
        set
        {
            if (value == m_config.ImportOptions.Contacts)
                return;
            m_config.ImportOptions.Contacts = value;

            OnPropertyChanged(new PropertyChangedEventArgs("ImportContactOptions"));
            SetNextState();
        }
    }
    public bool ImportDeletedItemOptions {
        get { return m_config.ImportOptions.DeletedItems; }
        set
        {
            if (value == m_config.ImportOptions.DeletedItems)
                return;
            m_config.ImportOptions.DeletedItems = value;

            OnPropertyChanged(new PropertyChangedEventArgs("ImportDeletedItemOptions"));
        }
    }
    public bool ImportJunkOptions {
        get { return m_config.ImportOptions.Junk; }
        set
        {
            if (value == m_config.ImportOptions.Junk)
                return;
            m_config.ImportOptions.Junk = value;

            OnPropertyChanged(new PropertyChangedEventArgs("ImportJunkOptions"));
        }
    }
    public bool ImportSentOptions {
        get { return m_config.ImportOptions.Sent; }
        set
        {
            if (value == m_config.ImportOptions.Sent)
                return;
            m_config.ImportOptions.Sent = value;

            OnPropertyChanged(new PropertyChangedEventArgs("ImportSentOptions"));
        }
    }
    public bool ImportRuleOptions {
        get { return m_config.ImportOptions.Rules; }
        set
        {
            if (value == m_config.ImportOptions.Rules)
                return;
            m_config.ImportOptions.Rules = value;

            OnPropertyChanged(new PropertyChangedEventArgs("ImportRuleOptions"));
            SetNextState();
        }       
    }
    public bool ImportOOOOptions
    {
        get { return m_config.ImportOptions.OOO; }
        set
        {
            if (value == m_config.ImportOptions.OOO)
                return;
            m_config.ImportOptions.OOO = value;

            OnPropertyChanged(new PropertyChangedEventArgs("ImportOOOOptions"));
            SetNextState();
        }
    }
    private string importnextbuttoncontent;
    public string ImportNextButtonContent {
        get { return importnextbuttoncontent; }
        set
        {
            if (value == importnextbuttoncontent)
                return;
            importnextbuttoncontent = value;

            OnPropertyChanged(new PropertyChangedEventArgs("ImportNextButtonContent"));
        }
    }
    public string MigrateONRAfter {
        get { return m_config.AdvancedImportOptions.MigrateOnOrAfter.ToShortDateString(); }
        set
        {
            if (value == m_config.AdvancedImportOptions.MigrateOnOrAfter.ToShortDateString())
                return;
            m_config.AdvancedImportOptions.MigrateOnOrAfter = Convert.ToDateTime(value);

            OnPropertyChanged(new PropertyChangedEventArgs("MigrateONRAfter"));
        }
    }
    public bool IsOnOrAfter
    {
        get { return m_config.AdvancedImportOptions.IsOnOrAfter; }
        set
        {
            if (value == m_config.AdvancedImportOptions.IsOnOrAfter)
                return;
            m_config.AdvancedImportOptions.IsOnOrAfter = value;

            OnPropertyChanged(new PropertyChangedEventArgs("IsOnOrAfter"));
        }
    }
    public string MaxMessageSize
    {
        get { return m_config.AdvancedImportOptions.MaxMessageSize; }
        set
        {
            if (value == m_config.AdvancedImportOptions.MaxMessageSize)
                return;
            m_config.AdvancedImportOptions.MaxMessageSize = value;

            OnPropertyChanged(new PropertyChangedEventArgs("MaxMessageSize"));
        }
    }
    public bool IsMaxMessageSize
    {
        get { return m_config.AdvancedImportOptions.IsMaxMessageSize; }
        set
        {
            if (value == m_config.AdvancedImportOptions.IsMaxMessageSize)
                return;
            m_config.AdvancedImportOptions.IsMaxMessageSize = value;

            OnPropertyChanged(new PropertyChangedEventArgs("IsMaxMessageSize"));
        }
    }
    private string placeholderstring;
    public string Placeholderstring {
        get { return placeholderstring; }
        set
        {
            placeholderstring = value;
            OnPropertyChanged(new PropertyChangedEventArgs("Placeholderstring"));
        }
    }
    public string FoldersToSkip {
        get
        {
            return placeholderstring;
        }
        set
        {
            placeholderstring = value;
            if (value != null)
            {
                string[] nameTokens = value.Split(',');
                int numFolders = nameTokens.Length;
                m_config.AdvancedImportOptions.FoldersToSkip = new Folder[numFolders];
                int i;
                for (i = 0; i < numFolders; i++)
                {
                    Folder tempUser = new Folder();
                    tempUser.FolderName = nameTokens.GetValue(i).ToString();
                    m_config.AdvancedImportOptions.FoldersToSkip.SetValue(tempUser, i);
                }
            }
            OnPropertyChanged(new PropertyChangedEventArgs("FoldersToSkip"));
        }
    }
    public bool IsSkipFolders
    {
        get { return m_config.AdvancedImportOptions.IsSkipFolders; }
        set
        {
            if (value == m_config.AdvancedImportOptions.IsSkipFolders)
                return;
            m_config.AdvancedImportOptions.IsSkipFolders = value;

            OnPropertyChanged(new PropertyChangedEventArgs("IsSkipFolders"));
        }
    }
    public bool LoggingVerbose
    {
        get { return m_config.GeneralOptions.Verbose; }
        set
        {
            if (value == m_config.GeneralOptions.Verbose)
                return;
            m_config.GeneralOptions.Verbose = value;

            OnPropertyChanged(new PropertyChangedEventArgs("LoggingVerbose"));
        }
    }
    private bool oenableNext;
    public bool OEnableNext
    {
        get { return oenableNext; }
        set
        {
            oenableNext = value;
            OnPropertyChanged(new PropertyChangedEventArgs("OEnableNext"));
        }
    }
    public string ConvertToCSV(Folder[] objectarray, string delimiter)
    {
        if (objectarray == null)
        {
            return null;
        }

        string result;

        System.Text.StringBuilder sb = new System.Text.StringBuilder();
        foreach (Folder i in objectarray)
        {
            if (i == null)
                continue;
            sb.Append(i.FolderName);
            sb.Append(delimiter);
        }
        result = sb.ToString();
        if (result.Length > 0)
            return result.Substring(0, result.Length - delimiter.Length);
        else
            return "";
    }
    private void SetNextState()
    {
        OEnableNext = ImportMailOptions     || ImportCalendarOptions ||
                      ImportContactOptions  || ImportTaskOptions     ||
                      ImportRuleOptions     || ImportOOOOptions;
    }
}
}
