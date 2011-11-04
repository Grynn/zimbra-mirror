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
        Migratedateflag = false;
        Maxmessageflag = false;
        Skipfolderflag = false;
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
        ImportMailOptions = config.importOptions.Mail;
        ImportCalendarOptions = config.importOptions.Calendar;
        ImportContactOptions = config.importOptions.Contacts;
        ImportDeletedItemOptions = config.importOptions.DeletedItems;
        ImportJunkOptions = config.importOptions.Junk;
        ImportTaskOptions = config.importOptions.Tasks;
        ImportSentOptions = config.importOptions.Sent;
        ImportRuleOptions = config.importOptions.Rules;

        MigrateONRAfter = config.AdvancedImportOptions.MigrateONRAfter.ToLongDateString();
        MaxMessageSize = config.AdvancedImportOptions.MaxMessageSize;

        string returnval = "";

        returnval = ConvertToCSV(config.AdvancedImportOptions.FoldersToSkip, ",");

        // placeholderstring = returnval;
        FoldersToSkip = returnval;
        if (MigrateONRAfter != null)
            Migratedateflag = true;
        else
            Migratedateflag = false;
        if ((MaxMessageSize != "") && (MaxMessageSize != null))
            Maxmessageflag = true;
        else
            Maxmessageflag = false;
        if ((placeholderstring != "") && (placeholderstring != null))
            Skipfolderflag = true;
        else
            Skipfolderflag = false;
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

                config = (Config)reader.Deserialize(fileRead);
                fileRead.Close();
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
            else
            {
                MessageBox.Show(
                    "There is no options configuration stored.  Please enter some options info",
                    "Zimbra Migration", MessageBoxButton.OK, MessageBoxImage.Error);
            }
        }
    }

    public void SaveConfig(string XmlfileName)
    {
        UpdateXmlElement(XmlfileName, "importOptions");
        UpdateXmlElement(XmlfileName, "AdvancedImportOptions");
    }

    private void Save()
    {
        Microsoft.Win32.SaveFileDialog fDialog = new Microsoft.Win32.SaveFileDialog();
        fDialog.Filter = "Config Files|*.xml";
        if (fDialog.ShowDialog() == true)
        {
            if (File.Exists(fDialog.FileName))
            {
                SaveConfig(fDialog.FileName);
                if (isServer)
                {
                    ((ConfigViewModelS)ViewModelPtrs[(int)ViewType.SVRSRC]).SaveConfig(
                        fDialog.FileName);
                    ((ConfigViewModelSDest)ViewModelPtrs[(int)ViewType.SVRDEST]).SaveConfig(
                        fDialog.FileName);
                }
                else
                {
                    ((ConfigViewModelU)ViewModelPtrs[(int)ViewType.USRSRC]).SaveConfig(
                        fDialog.FileName);
                    ((ConfigViewModelUDest)ViewModelPtrs[(int)ViewType.USRDEST]).SaveConfig(
                        fDialog.FileName);
                }
            }
            else
            {
                System.Xml.Serialization.XmlSerializer writer =
                    new System.Xml.Serialization.XmlSerializer(typeof (Config));

                System.IO.StreamWriter file = new System.IO.StreamWriter(fDialog.FileName);
                writer.Serialize(file, m_config);
                file.Close();
            }
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
        get { return m_config.importOptions.Mail; }
        set
        {
            if (value == m_config.importOptions.Mail)
                return;
            m_config.importOptions.Mail = value;

            OnPropertyChanged(new PropertyChangedEventArgs("ImportMailOptions"));
        }
    }
    public bool ImportTaskOptions {
        get { return m_config.importOptions.Tasks; }
        set
        {
            if (value == m_config.importOptions.Tasks)
                return;
            m_config.importOptions.Tasks = value;

            OnPropertyChanged(new PropertyChangedEventArgs("ImportTaskOptions"));
        }
    }
    public bool ImportCalendarOptions {
        get { return m_config.importOptions.Calendar; }
        set
        {
            if (value == m_config.importOptions.Calendar)
                return;
            m_config.importOptions.Calendar = value;

            OnPropertyChanged(new PropertyChangedEventArgs("ImportCalendarOptions"));
        }
    }
    public bool ImportContactOptions {
        get { return m_config.importOptions.Contacts; }
        set
        {
            if (value == m_config.importOptions.Contacts)
                return;
            m_config.importOptions.Contacts = value;

            OnPropertyChanged(new PropertyChangedEventArgs("ImportContactOptions"));
        }
    }
    public bool ImportDeletedItemOptions {
        get { return m_config.importOptions.DeletedItems; }
        set
        {
            if (value == m_config.importOptions.DeletedItems)
                return;
            m_config.importOptions.DeletedItems = value;

            OnPropertyChanged(new PropertyChangedEventArgs("ImportDeletedItemOptions"));
        }
    }
    public bool ImportJunkOptions {
        get { return m_config.importOptions.Junk; }
        set
        {
            if (value == m_config.importOptions.Junk)
                return;
            m_config.importOptions.Junk = value;

            OnPropertyChanged(new PropertyChangedEventArgs("ImportJunkOptions"));
        }
    }
    public bool ImportSentOptions {
        get { return m_config.importOptions.Sent; }
        set
        {
            if (value == m_config.importOptions.Sent)
                return;
            m_config.importOptions.Sent = value;

            OnPropertyChanged(new PropertyChangedEventArgs("ImportSentOptions"));
        }
    }
    public bool ImportRuleOptions {
        get { return m_config.importOptions.Rules; }
        set
        {
            if (value == m_config.importOptions.Rules)
                return;
            m_config.importOptions.Rules = value;

            OnPropertyChanged(new PropertyChangedEventArgs("ImportRuleOptions"));
        }
    }
    public string ImportNextButtonContent {
        get { return m_config.importOptions.NextButtonContent; }
        set
        {
            if (value == m_config.importOptions.NextButtonContent)
                return;
            m_config.importOptions.NextButtonContent = value;

            OnPropertyChanged(new PropertyChangedEventArgs("ImportNextButtonContent"));
        }
    }
    public string MigrateONRAfter {
        get { return m_config.AdvancedImportOptions.MigrateONRAfter.ToShortDateString(); }
        set
        {
            if (value == m_config.AdvancedImportOptions.MigrateONRAfter.ToShortDateString())
                return;
            m_config.AdvancedImportOptions.MigrateONRAfter = Convert.ToDateTime(value);

            OnPropertyChanged(new PropertyChangedEventArgs("MigrateONRAfter"));
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
            string[] nameTokens = value.Split(',');

            int i;

            for (i = 0; i < nameTokens.Length; i++)
            {
                Folder tempUser = new Folder();

                tempUser.FolderName = nameTokens.GetValue(i).ToString();
                m_config.AdvancedImportOptions.FoldersToSkip.SetValue(tempUser, i);
            }
            if (nameTokens.Length < 15)
            {
                for (int rest = i; rest < 15; rest++)
                    m_config.AdvancedImportOptions.FoldersToSkip.SetValue(null, i);
            }
            OnPropertyChanged(new PropertyChangedEventArgs("FoldersToSkip"));
        }
    }
    private bool m_migratedateflag;
    public bool Migratedateflag {
        get { return m_migratedateflag; }
        set
        {
            m_migratedateflag = value;
            OnPropertyChanged(new PropertyChangedEventArgs("Migratedateflag"));
        }
    }
    private bool m_maxmessageflag;
    public bool Maxmessageflag {
        get { return m_maxmessageflag; }
        set
        {
            m_maxmessageflag = value;
            OnPropertyChanged(new PropertyChangedEventArgs("Maxmessageflag"));
        }
    }
    private bool m_skipfolderflag;
    public bool Skipfolderflag {
        get { return m_skipfolderflag; }
        set
        {
            m_skipfolderflag = value;
            OnPropertyChanged(new PropertyChangedEventArgs("Skipfolderflag"));
        }
    }
    public string ConvertToCSV(Folder[] objectarray, string delimiter)
    {
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
}
}
