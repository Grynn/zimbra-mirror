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
        ScheduleViewModel scheduleViewModel;
        UsersViewModel usersViewModel;

        public ConfigViewModelS()
        {
            this.GetConfigSourceHelpCommand = new ActionCommand(this.GetConfigSourceHelp, () => true);
            this.LoadCommand = new ActionCommand(this.Load, () => true);
            this.SaveCommand = new ActionCommand(this.Save, () => true);
            this.NextCommand = new ActionCommand(this.Next, () => true);
            IsmailServer = false;
            Isprofile = false;
        }

        public UsersViewModel GetUsersViewModel()
        {
            return usersViewModel;
        }

        public void SetUsersViewModel(UsersViewModel usersViewModel)
        {
            this.usersViewModel = usersViewModel;
        }

        public ScheduleViewModel GetScheduleViewModel()
        {
            return scheduleViewModel;
        }

        public void SetScheduleViewModel(ScheduleViewModel scheduleViewModel)
        {
            this.scheduleViewModel = scheduleViewModel;
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

        private void Load()
        {
            System.Xml.Serialization.XmlSerializer reader =
            new System.Xml.Serialization.XmlSerializer(typeof(Config));
            if (File.Exists(@"C:\Temp\ZimbraAdminOverView.xml"))
            {

                System.IO.StreamReader fileRead = new System.IO.StreamReader(
                       @"C:\Temp\ZimbraAdminOverView.xml");
                Config Z11 = new Config();
                Z11 = (Config)reader.Deserialize(fileRead);
                fileRead.Close();


                if (Z11.mailServer.Hostname.Length == 0)
                {
                    Isprofile = true;
                    IsmailServer = false;
                    OutlookProfile = Z11.OutlookProfile;
                   
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
                    MailServerHostName = Z11.mailServer.Hostname;
                    MailServerProfileName = Z11.mailServer.ProfileName;
                }
                //MessageBox.Show("Configuration information loaded", "Zimbra Migration", MessageBoxButton.OK, MessageBoxImage.Exclamation);
            }
            else
            {

                MessageBox.Show("There is no configuration stored.Please enter some configuration info", "Zimbra Migration", MessageBoxButton.OK, MessageBoxImage.Error);
            }
           
            
          }

        public ICommand SaveCommand
        {
            get;
            private set;
        }

        private void Save()
        {
            OutlookProfile = ProfileList[CurrentProfileSelection];

            if (File.Exists(@"C:\Temp\ZimbraAdminOverView.xml"))
            {
                UpdateXmlElement(@"C:\Temp\ZimbraAdminOverView.xml", "mailServer");
                UpdateXmlElement(@"C:\Temp\ZimbraAdminOverView.xml", "OutlookProfile");
                UpdateXmlElement(@"C:\Temp\ZimbraAdminOverView.xml", "PSTFile");
            }

            else
            {
                System.Xml.Serialization.XmlSerializer writer =
                new System.Xml.Serialization.XmlSerializer(typeof(Config));

                System.IO.StreamWriter file = new System.IO.StreamWriter(
                    @"C:\Temp\ZimbraAdminOverView.xml");
                writer.Serialize(file, m_config);
                file.Close();
            }
            MessageBox.Show("Configuration information saved", "Zimbra Migration", MessageBoxButton.OK, MessageBoxImage.Exclamation);
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
            get { return m_config.mailServer.Hostname; }
            set
            {
                if (value == m_config.mailServer.Hostname)
                {
                    return;
                }
                m_config.mailServer.Hostname = value;

                OnPropertyChanged(new PropertyChangedEventArgs("MailServerHostName"));
            }
        }

        public string MailServerProfileName
        {
            get { return m_config.mailServer.ProfileName; }
            set
            {
                if (value == m_config.mailServer.ProfileName)
                {
                    return;
                }
                m_config.mailServer.ProfileName = value;

                OnPropertyChanged(new PropertyChangedEventArgs("MailServerProfileName"));
            }
        }      
    }
}
