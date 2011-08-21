using System;
using System.Diagnostics;
using System.ComponentModel;
using System.Windows;
using System.Windows.Input;
using System.Collections;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Runtime.InteropServices;
using System.Text;
using MVVM.Model;
using Misc;
using CssLib;
using System.IO;

namespace MVVM.ViewModel
{
    public class ConfigViewModelS : BaseViewModel
    {
        public ConfigViewModelS()
        {
            this.GetConfigSourceHelpCommand = new ActionCommand(this.GetConfigSourceHelp, () => true);
            this.LoadCommand = new ActionCommand(this.Load, () => true);
            this.SaveCommand = new ActionCommand(this.Save, () => true);
            this.NextCommand = new ActionCommand(this.Next, () => true);
            IsmailServer = false;
            Isprofile = false;
        }

        public ICommand GetConfigSourceHelpCommand
        {
            get;
            private set;
        }

        private void GetConfigSourceHelp()
        {
            string urlString = (isBrowser) ? "http://10.20.140.218/cfgS.html" : "file:///C:/depot/main/ZimbraMigrationTools/src/c/Misc/Help/cfgS.html";
            Process.Start(new ProcessStartInfo(urlString));
        }

        public ICommand LoadCommand
        {
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
                {
                    CurrentProfileSelection = (OutlookProfile == null) ? 0 : ProfileList.IndexOf(OutlookProfile);
                }
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
                    ((ConfigViewModelSDest)ViewModelPtrs[(int)ViewType.SVRDEST]).LoadConfig(config);
                    ((OptionsViewModel)ViewModelPtrs[(int)ViewType.OPTIONS]).LoadConfig(config);                   
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
            UpdateXmlElement(XmlfileName, "OutlookProfile");
            UpdateXmlElement(XmlfileName, "PSTFile");
            UpdateXmlElement(XmlfileName, "mailServer");
        }

        private void Save()
        {
            if (CurrentProfileSelection > -1)
            {
                OutlookProfile = ProfileList[CurrentProfileSelection];
            }

            Microsoft.Win32.SaveFileDialog fDialog = new Microsoft.Win32.SaveFileDialog();
            fDialog.Filter = "Config Files|*.xml";
            if (fDialog.ShowDialog() == true)
            {
                if (File.Exists(fDialog.FileName))
                {
                    SaveConfig(fDialog.FileName);
                    ((ConfigViewModelSDest)ViewModelPtrs[(int)ViewType.SVRDEST]).SaveConfig(fDialog.FileName);
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

        public ICommand NextCommand
        {
            get;
            private set;
        }

        private void Next()
        {
            lb.SelectedIndex = 1;
        }

        private bool IsProfile;
        private bool IsMailServer;

        public bool IsmailServer
        {
            get { return IsMailServer; }
            set { IsMailServer = value;
            OnPropertyChanged(new PropertyChangedEventArgs("IsmailServer"));
            }
        }


        public bool Isprofile
        {
            get { return IsProfile; }
            set { IsProfile = value;
            OnPropertyChanged(new PropertyChangedEventArgs("Isprofile"));
            }
        }

        public string OutlookProfile
        {
            get { return m_config.OutlookProfile; }
            set
            {
                if (value == m_config.OutlookProfile)
                {
                    return;
                }
                m_config.OutlookProfile = value;
                OnPropertyChanged(new PropertyChangedEventArgs("OutlookProfile"));
            }
        }

        private ObservableCollection<string> profilelist = new ObservableCollection<string>();
        public ObservableCollection<string> ProfileList
        {
            get { return profilelist; }
            set
            {
                profilelist = value;

            }
        }

        public int CurrentProfileSelection
        {
            get { return profileselection; }
            set
            {

                profileselection = value;

                OnPropertyChanged(new PropertyChangedEventArgs("CurrentProfileSelection"));
            }
        }
        private int profileselection;

        public string MailServerHostName
        {
            get { return m_config.mailServer.SourceHostname; }
            set
            {
                if (value == m_config.mailServer.SourceHostname)
                {
                    return;
                }
                m_config.mailServer.SourceHostname = value;

                OnPropertyChanged(new PropertyChangedEventArgs("MailServerHostName"));
            }
        }

        public string MailServerAdminID
        {
            get { return m_config.mailServer.SourceAdminID; }
            set
            {
                if (value == m_config.mailServer.SourceAdminID)
                {
                    return;
                }
                m_config.mailServer.SourceAdminID = value;

                OnPropertyChanged(new PropertyChangedEventArgs("MailServerAdminID"));
            }
        }

        public string MailServerAdminPwd
        {
            get { return m_config.mailServer.SourceAdminPwd; }
            set
            {
                if (value == m_config.mailServer.SourceAdminPwd)
                {
                    return;
                }
                m_config.mailServer.SourceAdminPwd = value;

                OnPropertyChanged(new PropertyChangedEventArgs("MailServerAdminPwd"));
            }
        }
    }
}
