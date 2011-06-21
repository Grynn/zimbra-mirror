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
    public class ConfigViewModelS : BaseViewModel
    {
        [DllImport("CppLib.dll")]
        public static extern int DisplayProfiles([MarshalAs(UnmanagedType.LPArray)] byte[] buffer);

        /*readonly*/ // public Config m_config = new Config("", "", "", "", "", "","","","","",false);

        ScheduleViewModel scheduleViewModel;
        UsersViewModel usersViewModel;

        public ConfigViewModelS()
        {
            this.GetConfigSourceHelpCommand = new ActionCommand(this.GetConfigSourceHelp, () => true);
            this.GetProfilesCommand = new ActionCommand(this.GetProfiles, () => true);
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

        public ICommand GetProfilesCommand
        {
            get;
            private set;
        }

        private void GetProfiles()
        {
            byte[] buffer = new byte[40];
            int iRetval = DisplayProfiles(buffer);
            string result = Encoding.ASCII.GetString(buffer, 0, iRetval);
            OutlookProfile = result;
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
                ZimbraServerHostName = Z11.zimbraServer.HostName;
                ZimbraPort = Z11.zimbraServer.Port;
                ZimbraAdmin = Z11.zimbraServer.AdminAccount;
                ZimbraAdminPasswd = Z11.zimbraServer.AdminPassword;
                //ZimbraDomain = Z11.zimbraServer.Domain;
                ZimbraSSL = Z11.zimbraServer.UseSSL;

                if (Z11.mailServer.Hostname.Length == 0)
                {
                    Isprofile = true;
                    IsmailServer = false;
                    OutlookProfile = Z11.OutlookProfile;
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
            if (File.Exists(@"C:\Temp\ZimbraAdminOverView.xml"))
            {
                UpdateXmlElement(@"C:\Temp\ZimbraAdminOverView.xml", "mailServer");
                UpdateXmlElement(@"C:\Temp\ZimbraAdminOverView.xml", "OutlookProfile");
                UpdateXmlElement(@"C:\Temp\ZimbraAdminOverView.xml", "PSTFile");
                UpdateXmlElement(@"C:\Temp\ZimbraAdminOverView.xml", "zimbraServer");
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
            if ((this.ZimbraServerHostName.Length == 0) || (this.ZimbraPort.Length == 0))
            {
                MessageBox.Show("Please fill in the host name and port", "Zimbra Migration", MessageBoxButton.OK, MessageBoxImage.Error);
                return;
            }

            ZimbraAPI zimbraAPI = new ZimbraAPI();

            int stat = zimbraAPI.Logon(this.ZimbraServerHostName, this.ZimbraPort, this.ZimbraAdmin, this.ZimbraAdminPasswd, true);
            if (stat == 0)
            {
                string authToken = ZimbraValues.GetZimbraValues().AuthToken;
                if (authToken.Length > 0)
                {
                    usersViewModel.DomainList.Clear();
                    scheduleViewModel.CosList.Clear();
                    zimbraAPI.GetAllDomains();
                    foreach (string s in ZimbraValues.GetZimbraValues().Domains)
                    {
                        usersViewModel.DomainList.Add(s);
                    }
                    zimbraAPI.GetAllCos();
                    foreach (string s in ZimbraValues.GetZimbraValues().COSes)
                    {
                        scheduleViewModel.CosList.Add(s);
                    }
                    lb.SelectedIndex = 1;
                }
            }
            else
            {
                MessageBox.Show(string.Format("Logon Unsuccessful: {0}", zimbraAPI.LastError), "Zimbra Migration", MessageBoxButton.OK, MessageBoxImage.Error);
            }
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
        //we donot need exchangeprofile -cleanup
       /* public string ExchangeProfile
        {
            get { return m_config.ExchangeProfile; }
            set
            {
                if (value == m_config.ExchangeProfile)
                {
                    return;
                }
                m_config.ExchangeProfile = value;
                OnPropertyChanged(new PropertyChangedEventArgs("ExchangeProfile"));
            }
        }*/

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
        public string ZimbraServerHostName
        {
            get { return m_config.zimbraServer.HostName; }
            set
            {
                if (value == m_config.zimbraServer.HostName)
                {
                    return;
                }
                m_config.zimbraServer.HostName = value;

                OnPropertyChanged(new PropertyChangedEventArgs("ZimbraServerHostName"));
            }
        }
        public string ZimbraAdmin
        {
            get { return m_config.zimbraServer.AdminAccount; }
            set
            {
                if (value == m_config.zimbraServer.AdminAccount)
                {
                    return;
                }
                m_config.zimbraServer.AdminAccount = value;

                OnPropertyChanged(new PropertyChangedEventArgs("ZimbraAdmin"));
            }
        }

        public string ZimbraAdminPasswd
        {
            get { return m_config.zimbraServer.AdminPassword; }
            set
            {
                if (value == m_config.zimbraServer.AdminPassword)
                {
                    return;
                }
                m_config.zimbraServer.AdminPassword = value;

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
