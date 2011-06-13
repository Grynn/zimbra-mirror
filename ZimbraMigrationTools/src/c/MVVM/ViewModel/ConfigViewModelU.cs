using System;
using System.Diagnostics;
using System.ComponentModel;
using System.Windows;
using System.Windows.Input;
using System.Runtime.InteropServices;
using System.Text;
using MVVM.Model;
using Misc;
using System.IO;

namespace MVVM.ViewModel
{
    public class ConfigViewModelU : BaseViewModel
    {
        [DllImport("CppLib.dll")]
        public static extern int DisplayProfiles([MarshalAs(UnmanagedType.LPArray)] byte[] buffer);

        /*readonly*/ // public Config m_config = new Config("", "", "", "", "", "","","","","",false);

        public ConfigViewModelU()
        {
            this.GetConfigSourceHelpCommand = new ActionCommand(this.GetConfigSourceHelp, () => true);
            this.GetProfilesCommand = new ActionCommand(this.GetProfiles, () => true);
            this.GetPSTCommand = new ActionCommand(this.GetPST, () => true);
            this.LoadCommand = new ActionCommand(this.Load, () => true);
            this.SaveCommand = new ActionCommand(this.Save, () => true);
            this.NextCommand = new ActionCommand(this.Next, () => true);
        }

        public ICommand GetConfigSourceHelpCommand
        {
            get;
            private set;
        }

        private void GetConfigSourceHelp()
        {
            string urlString = (isBrowser) ? "http://10.20.140.218/cfgU.html" : "file:///C:/depot/main/ZimbraMigrationTools/src/c/Misc/Help/cfgU.html";
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

        public ICommand GetPSTCommand
        {
            get;
            private set;
        }

        private void GetPST()
        {
            Microsoft.Win32.OpenFileDialog pstDialog = new Microsoft.Win32.OpenFileDialog();
            pstDialog.Filter = "PST Files(*.pst)|*.pst";
            pstDialog.CheckFileExists = true;
            pstDialog.Multiselect = false;
            if (pstDialog.ShowDialog() == true)
            {
                string result = pstDialog.FileName;
                PSTFile = result;  // update the UI
            }
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
                ZimbraUser = Z11.zimbraServer.UserAccount;
                ZimbraUserPasswd = Z11.zimbraServer.UserPassword;
               // ZimbraDomain = Z11.zimbraServer.Domain;
                ZimbraSSL = Z11.zimbraServer.UseSSL;
                if (Z11.OutlookProfile.Length == 0)
                {
                    Isprofile = false;
                    IspST = true;

                    PSTFile = Z11.PSTFile;
                }
                else
                {
                    Isprofile = true;
                    IspST = false;
                    OutlookProfile = Z11.OutlookProfile;

                }
                
                
            }
            else
            {

                MessageBox.Show("There is no configuration stored.Please enter some configuration info", "Zimbra Migration", MessageBoxButton.OK, MessageBoxImage.Error);
            }
           



            
            //MessageBox.Show("Configuration information loaded", "Zimbra Migration", MessageBoxButton.OK, MessageBoxImage.Exclamation);
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
                UpdateXmlElement(@"C:\Temp\ZimbraAdminOverView.xml", "OutlookProfile");
                 UpdateXmlElement(@"C:\Temp\ZimbraAdminOverView.xml", "PSTFile");
                UpdateXmlElement(@"C:\Temp\ZimbraAdminOverView.xml", "zimbraServer");
                UpdateXmlElement(@"C:\Temp\ZimbraAdminOverView.xml", "mailServer");
                
                
            }

            else
            {
                System.Xml.Serialization.XmlSerializer writer =
                new System.Xml.Serialization.XmlSerializer(typeof(Config));

                /*if (System.IO.Directory.Exists(@"C:\Temp\") == false)
                    System.IO.Directory.CreateDirectory(@"C:\Temp\");*/

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
        private bool IsProfile;

        public bool Isprofile
        {
            get { return IsProfile; }
            set { IsProfile = value;
            OnPropertyChanged(new PropertyChangedEventArgs("Isprofile"));
            }
        }
        private bool IsPST;

        public bool IspST
        {
            get { return IsPST; }
            set
            {
                IsPST = value;
                OnPropertyChanged(new PropertyChangedEventArgs("IspST"));
            }
        }
       

        private void Next()
        {
            lb.SelectedIndex = 1;
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
               // m_config.mailServer.ProfileName= value; 
                OnPropertyChanged(new PropertyChangedEventArgs("OutlookProfile"));
            }
        }

        public string PSTFile
        {
            get { return m_config.PSTFile; }
            set
            {
                if (value == m_config.PSTFile)
                {
                    return;
                }
                m_config.PSTFile = value;
                //m_config.mailServer.PSTFile = value;
                OnPropertyChanged(new PropertyChangedEventArgs("PSTFile"));
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

        public string ZimbraUser
        {
            get { return m_config.zimbraServer.UserAccount; }
            set
            {
                if (value == m_config.zimbraServer.UserAccount)
                {
                    return;
                }
                m_config.zimbraServer.UserAccount = value;

                OnPropertyChanged(new PropertyChangedEventArgs("ZimbraUser"));
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
        public string ZimbraUserPasswd
        {
            get { return m_config.zimbraServer.UserPassword; }
            set
            {
                if (value == m_config.zimbraServer.UserPassword)
                {
                    return;
                }
                m_config.zimbraServer.UserPassword = value;

                OnPropertyChanged(new PropertyChangedEventArgs("ZimbraUserPasswd"));
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
