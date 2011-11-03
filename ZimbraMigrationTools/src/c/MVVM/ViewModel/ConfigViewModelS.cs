using CssLib;
using MVVM.Model;
using Misc;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Collections;
using System.ComponentModel;
using System.Diagnostics;
using System.IO;
using System.Runtime.InteropServices;
using System.Text;
using System.Windows.Input;
using System.Windows;
using System;

namespace MVVM.ViewModel
{
public class ConfigViewModelS: BaseViewModel
{
    internal const int PROFILE_MODE = 1;
    internal const int EXCHSVR_MODE = 2;

    public ConfigViewModelS()
    {
        this.LoadCommand = new ActionCommand(this.Load, () => true);
        this.SaveCommand = new ActionCommand(this.Save, () => true);
        this.BackCommand = new ActionCommand(this.Back, () => true);
        this.NextCommand = new ActionCommand(this.Next, () => true);
        Isprofile = true;
        IsmailServer = false;
        CSEnableNext = false;
        iMailSvrInitialized = -1;
    }
    public ICommand LoadCommand {
        get;
        private set;
    }
    public void LoadConfig(Config config)
    {
        if (config.mailServer.SourceHostname.Length == 0)
        {
            Isprofile = true;
            IsmailServer = false;
            OutlookProfile = config.OutlookProfile;
            if (ProfileList.Count > 0)
                CurrentProfileSelection = (OutlookProfile == null) ? 0 : ProfileList.IndexOf(
                    OutlookProfile);

            else
                ProfileList.Add(OutlookProfile);
        }
        else
        {
            Isprofile = false;
            IsmailServer = true;
            MailServerHostName = config.mailServer.SourceHostname;
            MailServerAdminID = config.mailServer.SourceAdminID;
            MailServerAdminPwd = config.mailServer.SourceAdminPwd;
        }
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
                ((ConfigViewModelSDest)ViewModelPtrs[(int)ViewType.SVRDEST]).LoadConfig(config);
                ((OptionsViewModel)ViewModelPtrs[(int)ViewType.OPTIONS]).LoadConfig(config);
                ((UsersViewModel)ViewModelPtrs[(int)ViewType.USERS]).LoadDomain(config);
            }
        }
    }
    public ICommand SaveCommand {
        get;
        private set;
    }
    public void SaveConfig(string XmlfileName)
    {
        UpdateXmlElement(XmlfileName, "OutlookProfile");
        UpdateXmlElement(XmlfileName, "PSTFile");
        UpdateXmlElement(XmlfileName, "mailServer");
    }

    private void Save()
    {
        if (CurrentProfileSelection > -1)
        {
            if (ProfileList.Count > 0)
                OutlookProfile = ProfileList[CurrentProfileSelection];
        }
        Microsoft.Win32.SaveFileDialog fDialog = new Microsoft.Win32.SaveFileDialog();
        fDialog.Filter = "Config Files|*.xml";
        if (fDialog.ShowDialog() == true)
        {
            if (File.Exists(fDialog.FileName))
            {
                SaveConfig(fDialog.FileName);
                ((ConfigViewModelSDest)ViewModelPtrs[(int)ViewType.SVRDEST]).SaveConfig(
                    fDialog.FileName);
                ((OptionsViewModel)ViewModelPtrs[(int)ViewType.OPTIONS]).SaveConfig(
                    fDialog.FileName);
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
    public ICommand BackCommand {
        get;
        private set;
    }
    private void Back()
    {
        lb.SelectedIndex = 0;
    }
    public ICommand NextCommand {
        get;
        private set;
    }
    private void Next()
    {
        string ret = "";
        CSMigrationwrapper mw = ((IntroViewModel)ViewModelPtrs[(int)ViewType.INTRO]).mw;

        if (IsProfile)
        {
            if (iMailSvrInitialized == EXCHSVR_MODE)
            {
                MessageBox.Show("You are already logged in via Exchange Server credentials",
                    "Zimbra Migration", MessageBoxButton.OK, MessageBoxImage.Error);
                return;
            }
            if (iMailSvrInitialized == -1)
                ret = mw.InitializeMailClient(ProfileList[CurrentProfileSelection], "", "");
        }
        else
        {
            if (iMailSvrInitialized == PROFILE_MODE)
            {
                MessageBox.Show("You are already logged in via an Outlook Profile",
                    "Zimbra Migration", MessageBoxButton.OK, MessageBoxImage.Error);
                return;
            }
            if (iMailSvrInitialized == -1)
            {
                if ((MailServerHostName.Length == 0) || (MailServerAdminID.Length == 0) ||
                    (MailServerAdminPwd.Length == 0))
                {
                    MessageBox.Show("Please enter all source mail server credentials",
                        "Zimbra Migration", MessageBoxButton.OK, MessageBoxImage.Error);
                    return;
                }
                ret = mw.InitializeMailClient(MailServerHostName, MailServerAdminID,
                    MailServerAdminPwd);
            }
        }
        if (ret.Length > 0)
        {
            MessageBox.Show(ret, "Zimbra Migration", MessageBoxButton.OK,
                MessageBoxImage.Error);
            return;
        }
        iMailSvrInitialized = (IsProfile) ? PROFILE_MODE : EXCHSVR_MODE;

        lb.SelectedIndex = 2;
    }

    private int iMailSvrInitialized;
    private bool IsProfile;
    private bool IsMailServer;
    public bool IsMailServerInitialized {
        get { return iMailSvrInitialized != -1; }
    }
    public bool IsmailServer {
        get { return IsMailServer; }
        set
        {
            IsMailServer = value;
            CSEnableNext = true;
            OnPropertyChanged(new PropertyChangedEventArgs("IsmailServer"));
        }
    }
    public bool Isprofile {
        get { return IsProfile; }
        set
        {
            IsProfile = value;
            CSEnableNext = true;
            OnPropertyChanged(new PropertyChangedEventArgs("Isprofile"));
        }
    }
    public string OutlookProfile {
        get { return m_config.OutlookProfile; }
        set
        {
            if (value == m_config.OutlookProfile)
                return;
            m_config.OutlookProfile = value;
            OnPropertyChanged(new PropertyChangedEventArgs("OutlookProfile"));
        }
    }
    private ObservableCollection<string> profilelist = new ObservableCollection<string>();
    public ObservableCollection<string> ProfileList {
        get { return profilelist; }
        set
        {
            profilelist = value;
        }
    }
    public int CurrentProfileSelection {
        get { return profileselection; }
        set
        {
            profileselection = value;

            OnPropertyChanged(new PropertyChangedEventArgs("CurrentProfileSelection"));
        }
    }
    private int profileselection;
    public string MailServerHostName {
        get { return m_config.mailServer.SourceHostname; }
        set
        {
            if (value == m_config.mailServer.SourceHostname)
                return;
            m_config.mailServer.SourceHostname = value;

            OnPropertyChanged(new PropertyChangedEventArgs("MailServerHostName"));
        }
    }
    public string MailServerAdminID {
        get { return m_config.mailServer.SourceAdminID; }
        set
        {
            if (value == m_config.mailServer.SourceAdminID)
                return;
            m_config.mailServer.SourceAdminID = value;

            OnPropertyChanged(new PropertyChangedEventArgs("MailServerAdminID"));
        }
    }
    public string MailServerAdminPwd {
        get { return m_config.mailServer.SourceAdminPwd; }
        set
        {
            if (value == m_config.mailServer.SourceAdminPwd)
                return;
            m_config.mailServer.SourceAdminPwd = value;

            OnPropertyChanged(new PropertyChangedEventArgs("MailServerAdminPwd"));
        }
    }
    private bool csenableNext;
    public bool CSEnableNext {
        get { return csenableNext; }
        set
        {
            csenableNext = value;
            OnPropertyChanged(new PropertyChangedEventArgs("CSEnableNext"));
        }
    }
}
}
