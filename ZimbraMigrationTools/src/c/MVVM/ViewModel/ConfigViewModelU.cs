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
            string urlString = (isBrowser) ? "http://10.20.140.218/cfg.html" : "file:///C:/depot/main/ZimbraMigrationTools/src/c/Misc/Help/cfg.html";
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
                PSTFile = pstDialog.FileName;   // update the UI
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
            System.IO.StreamReader fileRead = new System.IO.StreamReader(
               @"ZimbraAdminOverView.xml");
            Config Z11 = new Config();
            Z11 = (Config)reader.Deserialize(fileRead);
            ZimbraPort = Z11.zimbraServer.Port;
            ZimbraAdmin = Z11.zimbraServer.AdminAccount;
            ZimbraAdminPasswd = Z11.zimbraServer.AdminPassword;
            ZimbraDomain = Z11.zimbraServer.Domain;
            OutlookProfile = Z11.mailServer.ProfileName;



            
            //MessageBox.Show("Configuration information loaded", "Zimbra Migration", MessageBoxButton.OK, MessageBoxImage.Exclamation);
        }

        public ICommand SaveCommand
        {
            get;
            private set;
        }

        private void Save()
        {
            if (File.Exists(@"ZimbraAdminOverView.xml"))
            {
                UpdateXmlElement(@"ZimbraAdminOverView.xml", "zimbraServer");
            }

            else
            {
                System.Xml.Serialization.XmlSerializer writer =
                new System.Xml.Serialization.XmlSerializer(typeof(Config));

                System.IO.StreamWriter file = new System.IO.StreamWriter(
                    @"ZimbraAdminOverView.xml");
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
                m_config.mailServer.ProfileName= value; 
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

        public string ZimbraDomain
        {
            get { return m_config.zimbraServer.Domain; }
            set
            {
                if (value == m_config.zimbraServer.Domain)
                {
                    return;
                }
                m_config.zimbraServer.Domain = value;

                OnPropertyChanged(new PropertyChangedEventArgs("ZimbraDomain"));
            }
        }

       

      
    }
}
