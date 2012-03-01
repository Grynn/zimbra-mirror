using CssLib;
using MVVM.Model;
using Misc;
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
public class ConfigViewModelSDest: BaseViewModel
{
    public ConfigViewModelSDest()
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
    public void LoadConfig(Config config)
    {
        ZimbraServerHostName = config.zimbraServer.Hostname;
        ZimbraPort = config.zimbraServer.Port;
        ZimbraAdmin = config.zimbraServer.AdminID;
        ZimbraAdminPasswd = config.zimbraServer.AdminPwd;
        ZimbraSSL = config.zimbraServer.UseSSL;
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
                    ((ConfigViewModelS)ViewModelPtrs[(int)ViewType.SVRSRC]).LoadConfig(config);
                    ((OptionsViewModel)ViewModelPtrs[(int)ViewType.OPTIONS]).LoadConfig(config);
                    ((UsersViewModel)ViewModelPtrs[(int)ViewType.USERS]).LoadDomain(config);
                    ((ScheduleViewModel)ViewModelPtrs[(int)ViewType.SCHED]).SetConfigFile(fDialog.FileName);
                }
                catch (Exception e)
                {
                    DisplayLoadError(e);
                    return;
                }                    
            }
        }
    }
    public ICommand SaveCommand {
        get;
        private set;
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
    public ICommand BackCommand {
        get;
        private set;
    }
    private void Back()
    {
        lb.SelectedIndex = 1;
    }
    public ICommand NextCommand {
        get;
        private set;
    }
    private void Next()
    {
        if ((this.ZimbraServerHostName.Length == 0) || (this.ZimbraPort.Length == 0))
        {
            MessageBox.Show("Please fill in the host name and port", "Zimbra Migration",
                MessageBoxButton.OK, MessageBoxImage.Error);
            return;
        }

        ZimbraAPI zimbraAPI = new ZimbraAPI();
        int stat = zimbraAPI.Logon(this.ZimbraServerHostName, this.ZimbraPort, this.ZimbraAdmin,
            this.ZimbraAdminPasswd, true);

        if (stat == 0)
        {
            string authToken = ZimbraValues.GetZimbraValues().AuthToken;

            if (authToken.Length > 0)
            {
                UsersViewModel usersViewModel =
                    ((UsersViewModel)ViewModelPtrs[(int)ViewType.USERS]);
                ScheduleViewModel scheduleViewModel =
                    ((ScheduleViewModel)ViewModelPtrs[(int)ViewType.SCHED]);
                string currentDomain = (usersViewModel.DomainList.Count > 0) ?
                    usersViewModel.DomainList[usersViewModel.CurrentDomainSelection] : "";

                usersViewModel.DomainList.Clear();
                scheduleViewModel.CosList.Clear();
                zimbraAPI.GetAllDomains();
                for (int i = 0; i < ZimbraValues.GetZimbraValues().Domains.Count; i++)
                {
                    string s = ZimbraValues.GetZimbraValues().Domains[i];

                    usersViewModel.DomainList.Add(s);
                    // if we've loaded a config file where the domain was specified, then set it as selected
                    if (currentDomain != null)
                    {
                        if (currentDomain.Length > 0)
                        {
                            if (s == currentDomain)
                                usersViewModel.CurrentDomainSelection = i;
                        }
                    }
                }
                zimbraAPI.GetAllCos();
                foreach (CosInfo cosinfo in ZimbraValues.GetZimbraValues().COSes)
                {
                    scheduleViewModel.CosList.Add(new CosInfo(cosinfo.CosName, cosinfo.CosID));
                }
                lb.SelectedIndex = 3;
            }
        }
        else
        {
            MessageBox.Show(string.Format("Logon Unsuccessful: {0}", zimbraAPI.LastError),
                "Zimbra Migration", MessageBoxButton.OK, MessageBoxImage.Error);
        }
    }
    public string ZimbraPort {
        get { return m_config.zimbraServer.Port; }
        set
        {
            if (value == m_config.zimbraServer.Port)
                return;
            m_config.zimbraServer.Port = value;

            OnPropertyChanged(new PropertyChangedEventArgs("ZimbraPort"));
        }
    }
    public string ZimbraServerHostName {
        get { return m_config.zimbraServer.Hostname; }
        set
        {
            if (value == m_config.zimbraServer.Hostname)
                return;
            m_config.zimbraServer.Hostname = value;

            OnPropertyChanged(new PropertyChangedEventArgs("ZimbraServerHostName"));
        }
    }
    public string ZimbraAdmin {
        get { return m_config.zimbraServer.AdminID; }
        set
        {
            if (value == m_config.zimbraServer.AdminID)
                return;
            m_config.zimbraServer.AdminID = value;

            OnPropertyChanged(new PropertyChangedEventArgs("ZimbraAdmin"));
        }
    }
    public string ZimbraAdminPasswd {
        get { return m_config.zimbraServer.AdminPwd; }
        set
        {
            if (value == m_config.zimbraServer.AdminPwd)
                return;
            m_config.zimbraServer.AdminPwd = value;

            OnPropertyChanged(new PropertyChangedEventArgs("ZimbraAdminPasswd"));
        }
    }
    public bool ZimbraSSL {
        get { return m_config.zimbraServer.UseSSL; }
        set
        {
            if (value == m_config.zimbraServer.UseSSL)
                return;
            m_config.zimbraServer.UseSSL = value;

            OnPropertyChanged(new PropertyChangedEventArgs("ZimbraSSL"));
        }
    }
}
}
