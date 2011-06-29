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
    public class ConfigViewModelUDest : BaseViewModel
    {
        [DllImport("CppLib.dll")]
        public static extern int DisplayProfiles([MarshalAs(UnmanagedType.LPArray)] byte[] buffer);

        /*readonly*/ // public Config m_config = new Config("", "", "", "", "", "","","","","",false);

        public ConfigViewModelUDest()
        {
            this.GetConfigDestHelpCommand = new ActionCommand(this.GetConfigDestHelp, () => true);
            this.LoadCommand = new ActionCommand(this.Load, () => true);
            this.SaveCommand = new ActionCommand(this.Save, () => true);
            this.BackCommand = new ActionCommand(this.Back, () => true);
            this.NextCommand = new ActionCommand(this.Next, () => true);
        }

        public ICommand GetConfigDestHelpCommand
        {
            get;
            private set;
        }

        private void GetConfigDestHelp()
        {
            string urlString = (isBrowser) ? "http://10.20.140.218/cfgUDest.html" : "file:///C:/depot/main/ZimbraMigrationTools/src/c/Misc/Help/cfgUDest.html";
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
                ZimbraServerHostName = Z11.zimbraServer.HostName;
                ZimbraPort = Z11.zimbraServer.Port;
                ZimbraUser = Z11.zimbraServer.UserAccount;
                ZimbraUserPasswd = Z11.zimbraServer.UserPassword;
                ZimbraSSL = Z11.zimbraServer.UseSSL;
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
                UpdateXmlElement(@"C:\Temp\ZimbraAdminOverView.xml", "zimbraServer");            
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

            int stat = zimbraAPI.Logon(this.ZimbraServerHostName, this.ZimbraPort, this.ZimbraUser,this.ZimbraUserPasswd, false);
            if (stat == 0)
            {
                string authToken = ZimbraValues.GetZimbraValues().AuthToken;
                if (authToken.Length > 0)
                {
                    zimbraAPI.GetInfo();
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
