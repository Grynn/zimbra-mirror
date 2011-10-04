using System;
using System.Diagnostics;
using System.ComponentModel;
using System.Windows;
using System.Windows.Input;
using System.Runtime.InteropServices;
using System.Text;
using MVVM.Model;
using Misc;
using CssLib;
using System.IO;

namespace MVVM.ViewModel
{
    public class ConfigViewModelSDest : BaseViewModel
    {
        public ConfigViewModelSDest()
        {
            this.LoadCommand = new ActionCommand(this.Load, () => true);
            this.SaveCommand = new ActionCommand(this.Save, () => true);
            this.BackCommand = new ActionCommand(this.Back, () => true);
            this.NextCommand = new ActionCommand(this.Next, () => true);
        }

        public ICommand LoadCommand
        {
            get;
            private set;
        }

        public void LoadConfig(Config config)
        {
            ZimbraServerHostName = config.zimbraServer.ZimbraHostname;
            ZimbraPort = config.zimbraServer.Port;
            ZimbraAdmin = config.zimbraServer.ZimbraAdminID;
            ZimbraAdminPasswd = config.zimbraServer.ZimbraAdminPwd;
            ZimbraSSL = config.zimbraServer.UseSSL;
        }

        private void Load()
        {
            System.Xml.Serialization.XmlSerializer reader =
            new System.Xml.Serialization.XmlSerializer(typeof(Config));

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
                    ((ConfigViewModelS)ViewModelPtrs[(int)ViewType.SVRSRC]).LoadConfig(config);
                    ((OptionsViewModel)ViewModelPtrs[(int)ViewType.OPTIONS]).LoadConfig(config);
                    ((ScheduleViewModel)ViewModelPtrs[(int)ViewType.SCHED]).SetConfigFile(fDialog.FileName);
                }
            }                
         }

        public ICommand SaveCommand
        {
            get;
            private set;
        }

        public void SaveConfig(string XmlfileName)
        {
            UpdateXmlElement(XmlfileName, "zimbraServer");
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
                    ((ConfigViewModelS)ViewModelPtrs[(int)ViewType.SVRSRC]).SaveConfig(fDialog.FileName);
                    ((OptionsViewModel)ViewModelPtrs[(int)ViewType.OPTIONS]).SaveConfig(fDialog.FileName);
                }
                else
                {
                    System.Xml.Serialization.XmlSerializer writer =
                    new System.Xml.Serialization.XmlSerializer(typeof(Config));

                    System.IO.StreamWriter file = new System.IO.StreamWriter(fDialog.FileName);
                    writer.Serialize(file, m_config);
                    file.Close();
                }
                ((ScheduleViewModel)ViewModelPtrs[(int)ViewType.SCHED]).SetConfigFile(fDialog.FileName);
            }
        }

        public ICommand BackCommand
        {
            get;
            private set;
        }

        private void Back()
        {
            lb.SelectedIndex = 0;
        }

        public ICommand NextCommand
        {
            get;
            private set;
        }

        private void Next()
        {
            if ((this.ZimbraServerHostName.Length == 0) || (this.ZimbraPort.Length == 0))
            {
                MessageBox.Show("Please fill in the host name and port", "Zimbra Migration", MessageBoxButton.OK, MessageBoxImage.Error);
                return;
            }

            ZimbraAPI zimbraAPI = new ZimbraAPI();

            int stat = zimbraAPI.Logon(this.ZimbraServerHostName, this.ZimbraPort, this.ZimbraAdmin,this.ZimbraAdminPasswd, true);
            if (stat == 0)
            {
                string authToken = ZimbraValues.GetZimbraValues().AuthToken;
                if (authToken.Length > 0)
                {
                    UsersViewModel usersViewModel = ((UsersViewModel)ViewModelPtrs[(int)ViewType.USERS]);
                    ScheduleViewModel scheduleViewModel = ((ScheduleViewModel)ViewModelPtrs[(int)ViewType.SCHED]);
                    usersViewModel.DomainList.Clear();
                    scheduleViewModel.CosList.Clear();
                    zimbraAPI.GetAllDomains();
                    foreach (string s in ZimbraValues.GetZimbraValues().Domains)
                    {
                        usersViewModel.DomainList.Add(s);
                    }
                    zimbraAPI.GetAllCos();
                    foreach (CosInfo cosinfo in ZimbraValues.GetZimbraValues().COSes)
                    {
                        scheduleViewModel.CosList.Add(new CosInfo(cosinfo.CosName, cosinfo.CosID));
                    }
                    lb.SelectedIndex = 2;
                }
            }
            else
            {
                MessageBox.Show(string.Format("Logon Unsuccessful: {0}", zimbraAPI.LastError), "Zimbra Migration", MessageBoxButton.OK, MessageBoxImage.Error);
            }
        }

        public string ZimbraPort
        {
            get { return m_config.zimbraServer.Port; }
            set
            {
                if (value == m_config.zimbraServer.Port)
                {
                    return;
                }
                m_config.zimbraServer.Port = value;

                OnPropertyChanged(new PropertyChangedEventArgs("ZimbraPort"));
            }
        }

        public string ZimbraServerHostName
        {
            get { return m_config.zimbraServer.ZimbraHostname; }
            set
            {
                if (value == m_config.zimbraServer.ZimbraHostname)
                {
                    return;
                }
                m_config.zimbraServer.ZimbraHostname = value;

                OnPropertyChanged(new PropertyChangedEventArgs("ZimbraServerHostName"));
            }
        }
        public string ZimbraAdmin
        {
            get { return m_config.zimbraServer.ZimbraAdminID; }
            set
            {
                if (value == m_config.zimbraServer.ZimbraAdminID)
                {
                    return;
                }
                m_config.zimbraServer.ZimbraAdminID = value;

                OnPropertyChanged(new PropertyChangedEventArgs("ZimbraAdmin"));
            }
        }

        public string ZimbraAdminPasswd
        {
            get { return m_config.zimbraServer.ZimbraAdminPwd; }
            set
            {
                if (value == m_config.zimbraServer.ZimbraAdminPwd)
                {
                    return;
                }
                m_config.zimbraServer.ZimbraAdminPwd = value;

                OnPropertyChanged(new PropertyChangedEventArgs("ZimbraAdminPasswd"));
            }
        }

       

        public bool ZimbraSSL
        {

            get { return m_config.zimbraServer.UseSSL; }
            set
            {
                if (value == m_config.zimbraServer.UseSSL)
                {
                    return;
                }
                m_config.zimbraServer.UseSSL = value;

                OnPropertyChanged(new PropertyChangedEventArgs("ZimbraSSL"));
            }

        }
       

      
    }
}
