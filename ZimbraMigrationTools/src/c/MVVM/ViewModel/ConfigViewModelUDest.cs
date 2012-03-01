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
public class ConfigViewModelUDest: BaseViewModel
{
    public ConfigViewModelUDest()
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
        ZimbraUser = config.zimbraServer.UserAccount;
        ZimbraUserPasswd = config.zimbraServer.UserPassword;
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
                    ((ConfigViewModelU)ViewModelPtrs[(int)ViewType.USRSRC]).LoadConfig(config);
                    ((OptionsViewModel)ViewModelPtrs[(int)ViewType.OPTIONS]).LoadConfig(config);
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
        try
        {
            System.Net.IPAddress address = System.Net.IPAddress.Parse(ZimbraServerHostName);
            MessageBox.Show("Please enter a valid host name rather than an IP address",
                "Zimbra Migration", MessageBoxButton.OK, MessageBoxImage.Error);
            return;
        }
        catch (Exception)
        {}
        ZimbraAPI zimbraAPI = new ZimbraAPI();
        int stat = zimbraAPI.Logon(this.ZimbraServerHostName, this.ZimbraPort, this.ZimbraUser,
            this.ZimbraUserPasswd, false);

        if (stat == 0)
        {
            string authToken = ZimbraValues.GetZimbraValues().AuthToken;

            if (authToken.Length > 0)
            {
                zimbraAPI.GetInfo();
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
    public string ZimbraUser {
        get { return m_config.zimbraServer.UserAccount; }
        set
        {
            if (value == m_config.zimbraServer.UserAccount)
                return;
            m_config.zimbraServer.UserAccount = value;

            OnPropertyChanged(new PropertyChangedEventArgs("ZimbraUser"));
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
    public string ZimbraUserPasswd {
        get { return m_config.zimbraServer.UserPassword; }
        set
        {
            if (value == m_config.zimbraServer.UserPassword)
                return;
            m_config.zimbraServer.UserPassword = value;

            OnPropertyChanged(new PropertyChangedEventArgs("ZimbraUserPasswd"));
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
