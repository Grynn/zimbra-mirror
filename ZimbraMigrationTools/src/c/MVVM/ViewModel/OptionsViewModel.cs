using System;
using System.Diagnostics;
using System.ComponentModel;
using System.Windows;
using System.Windows.Input;
using Misc;
using MVVM.Model;
using System.Xml.Serialization;
using System.Xml.Linq;
using System.Xml;
using System.IO;

namespace MVVM.ViewModel
{
    public class OptionsViewModel : BaseViewModel
    {
        ScheduleViewModel scheduleViewModel;
       // public Config m_config = new Config("", "", "", "", "", "", "", "", "", "", false);
        public OptionsViewModel()
        {
            this.GetOptionsHelpCommand = new ActionCommand(this.GetOptionsHelp, () => true);
            this.LoadCommand = new ActionCommand(this.Load, () => true);
            this.SaveCommand = new ActionCommand(this.Save, () => true);
            this.NextCommand = new ActionCommand(this.Next, () => true);
        }

        public void SetScheduleModel(ScheduleViewModel scheduleViewModel)
        {
            this.scheduleViewModel = scheduleViewModel;
        }

        public ICommand GetOptionsHelpCommand
        {
            get;
            private set;
        }

        public ICommand LoadCommand
        {
            get;
            private set;
        }

        public ICommand SaveCommand
        {
            get;
            private set;
        }

        public ICommand NextCommand
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
                ImportMailOptions = Z11.importOptions.Mail;
                ImportCalendarOptions = Z11.importOptions.Calendar;
                ImportContactOptions = Z11.importOptions.Contacts;
                ImportDeletedItemOptions = Z11.importOptions.DeletedItems;
                ImportJunkOptions = Z11.importOptions.Junk;
                ImportTaskOptions = Z11.importOptions.Tasks;
                ImportSentOptions = Z11.importOptions.Sent;
                ImportRuleOptions = Z11.importOptions.Rules;
                //MessageBox.Show("Options information loaded", "Zimbra Migration", MessageBoxButton.OK, MessageBoxImage.Exclamation);
            }
            else
            {
                MessageBox.Show("There is no options configuration stored.Please enter some options info", "Zimbra Migration", MessageBoxButton.OK, MessageBoxImage.Error);
            }



           
           // MessageBox.Show("Options information loaded", "Zimbra Migration", MessageBoxButton.OK, MessageBoxImage.Exclamation);
        }

        private void Save()
        {
            if (File.Exists(@"C:\Temp\ZimbraAdminOverView.xml"))
             UpdateXmlElement(@"C:\Temp\ZimbraAdminOverView.xml", "importOptions");
            else
            {   System.Xml.Serialization.XmlSerializer writer =
                new System.Xml.Serialization.XmlSerializer(typeof(Config));

                System.IO.StreamWriter file = new System.IO.StreamWriter(
                    @"C:\Temp\ZimbraAdminOverView.xml");
                writer.Serialize(file, m_config);
                file.Close();
            }

           
            MessageBox.Show("Options information saved", "Zimbra Migration", MessageBoxButton.OK, MessageBoxImage.Exclamation);
        }

        private void Next()
        {
            if (isServer)
            {
                lb.SelectedIndex = 2;
            }
            else
            {
                scheduleViewModel.SchedList.Add(scheduleViewModel.GetConfigUModel().ZimbraAdmin);
                scheduleViewModel.Migrate();
            }
        }

        private void GetOptionsHelp()
        {
            string urlString = (isBrowser) ? "http://10.20.140.218/options.html" : "file:///C:/depot/main/ZimbraMigrationTools/src/c/Misc/Help/options.html";
            Process.Start(new ProcessStartInfo(urlString));
        }

        public bool ImportMailOptions
        {
            get { return m_config.importOptions.Mail; }
            set
            {
                if (value == m_config.importOptions.Mail)
                {
                    return;
                }
                m_config.importOptions.Mail = value;

                OnPropertyChanged(new PropertyChangedEventArgs("ImportMailOptions"));
            }
        }

        public bool ImportTaskOptions
        {
            get { return m_config.importOptions.Tasks; }
            set
            {
                if (value == m_config.importOptions.Tasks)
                {
                    return;
                }
                m_config.importOptions.Tasks = value;

                OnPropertyChanged(new PropertyChangedEventArgs("ImportTaskOptions"));
            }
        }

        public bool ImportCalendarOptions
        {
            get { return m_config.importOptions.Calendar; }
            set
            {
                if (value == m_config.importOptions.Calendar)
                {
                    return;
                }
                m_config.importOptions.Calendar = value;

                OnPropertyChanged(new PropertyChangedEventArgs("ImportCalendarOptions"));
            }
        }

        public bool ImportContactOptions
        {
            get { return m_config.importOptions.Contacts; }
            set
            {
                if (value == m_config.importOptions.Contacts)
                {
                    return;
                }
                m_config.importOptions.Contacts = value;

                OnPropertyChanged(new PropertyChangedEventArgs("ImportContactOptions"));
            }
        }

        public bool ImportDeletedItemOptions
        {
            get { return m_config.importOptions.DeletedItems; }
            set
            {
                if (value == m_config.importOptions.DeletedItems)
                {
                    return;
                }
                m_config.importOptions.DeletedItems = value;

                OnPropertyChanged(new PropertyChangedEventArgs("ImportDeletedItemOptions"));
            }
        }

        public bool ImportJunkOptions
        {
            get { return m_config.importOptions.Junk; }
            set
            {
                if (value == m_config.importOptions.Junk)
                {
                    return;
                }
                m_config.importOptions.Junk = value;

                OnPropertyChanged(new PropertyChangedEventArgs("ImportJunkOptions"));
            }
        }

        public bool ImportSentOptions
        {
            get { return m_config.importOptions.Sent; }
            set
            {
                if (value == m_config.importOptions.Sent)
                {
                    return;
                }
                m_config.importOptions.Sent = value;

                OnPropertyChanged(new PropertyChangedEventArgs("ImportSentOptions"));
            }
        }
        public bool ImportRuleOptions
        {
            get { return m_config.importOptions.Rules; }
            set
            {
                if (value == m_config.importOptions.Rules)
                {
                    return;
                }
                m_config.importOptions.Rules = value;

                OnPropertyChanged(new PropertyChangedEventArgs("ImportRuleOptions"));
            }
        }

        public string ImportNextButtonContent
        {
            get { return m_config.importOptions.NextButtonContent; }
            set
            {
                if (value == m_config.importOptions.NextButtonContent)
                {
                    return;
                }
                m_config.importOptions.NextButtonContent = value;

                OnPropertyChanged(new PropertyChangedEventArgs("ImportNextButtonContent"));
            }
        }
    }
}
