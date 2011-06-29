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
using System.Collections.Generic;

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
            this.BackCommand = new ActionCommand(this.Back, () => true);
            this.NextCommand = new ActionCommand(this.Next, () => true);
            Migratedateflag = false;
            Maxattachflag = false;
            Skipfolderflag = false;
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

        public ICommand BackCommand
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
                fileRead.Close();
                ImportMailOptions = Z11.importOptions.Mail;
                ImportCalendarOptions = Z11.importOptions.Calendar;
                ImportContactOptions = Z11.importOptions.Contacts;
                ImportDeletedItemOptions = Z11.importOptions.DeletedItems;
                ImportJunkOptions = Z11.importOptions.Junk;
                ImportTaskOptions = Z11.importOptions.Tasks;
                ImportSentOptions = Z11.importOptions.Sent;
                ImportRuleOptions = Z11.importOptions.Rules;

                MigrateONRAfter = Z11.AdvancedImportOptions.MigrateONRAfter.ToLongDateString();
                MaxAttachementSize = Z11.AdvancedImportOptions.MaxAttachementSize;

                string returnval = "";
                
                returnval = ConvertToCSV(Z11.AdvancedImportOptions.FoldersToSkip, ",");

                
             //  placeholderstring = returnval;
                FoldersToSkip = returnval;
             

               if (MigrateONRAfter != null) 
                    Migratedateflag = true;
               else
                    Migratedateflag = false;

                if((MaxAttachementSize != "")&&(MaxAttachementSize != null))
                     Maxattachflag = true;
                else
                    Maxattachflag = false;

                if ((placeholderstring != "")&&(placeholderstring != null))
                    Skipfolderflag = true;
                else
                    Skipfolderflag = false;


                
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
            {
                UpdateXmlElement(@"C:\Temp\ZimbraAdminOverView.xml", "importOptions");
                UpdateXmlElement(@"C:\Temp\ZimbraAdminOverView.xml", "AdvancedImportOptions");

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

           
            MessageBox.Show("Options information saved", "Zimbra Migration", MessageBoxButton.OK, MessageBoxImage.Exclamation);
        }

        private void Back()
        {
            lb.SelectedIndex = 1;
        }

        private void Next()
        {
            if (isServer)
            {
                lb.SelectedIndex = 3;
            }
            else
            {
                ConfigViewModelUDest configViewModelU = scheduleViewModel.GetConfigUDestModel();
                string name = scheduleViewModel.GetConfigUDestModel().ZimbraUser;
                scheduleViewModel.GetUsersViewModel().UsersList.Add(new UsersViewModel(null, name, ""));
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
        public string MigrateONRAfter
        {
            get { return m_config.AdvancedImportOptions.MigrateONRAfter.ToShortDateString(); }
            set
            {
                if (value == m_config.AdvancedImportOptions.MigrateONRAfter.ToShortDateString())
                {
                    return;
                }
                m_config.AdvancedImportOptions.MigrateONRAfter = Convert.ToDateTime(value);

                OnPropertyChanged(new PropertyChangedEventArgs("MigrateONRAfter"));
            }
        }
        public string MaxAttachementSize
        {
            get { return m_config.AdvancedImportOptions.MaxAttachementSize; }
            set
            {
                if (value == m_config.AdvancedImportOptions.MaxAttachementSize)
                {
                    return;
                }
                m_config.AdvancedImportOptions.MaxAttachementSize = value;

                OnPropertyChanged(new PropertyChangedEventArgs("MaxAttachementSize"));
            }
        }

        private string placeholderstring;

        public string Placeholderstring
        {
            get { return placeholderstring; }
            set { placeholderstring = value;
            OnPropertyChanged(new PropertyChangedEventArgs("Placeholderstring"));
            }
        }

        public string FoldersToSkip
        {
            get
            {
                return placeholderstring; 
            }
            set
            {
                placeholderstring = value;
                string[] nameTokens = value.Split(',');
                 int i;
                for (i = 0; i < nameTokens.Length; i++)
                {
                    Folder tempUser = new Folder();
                    tempUser.FolderName = nameTokens.GetValue(i).ToString();
                    m_config.AdvancedImportOptions.FoldersToSkip.SetValue(tempUser, i);

                }
                if (nameTokens.Length < 15)
                {
                    for (int rest= i ; rest <15; rest++)
                    m_config.AdvancedImportOptions.FoldersToSkip.SetValue(null, i);

                }

                OnPropertyChanged(new PropertyChangedEventArgs("FoldersToSkip"));
            }
        }

        private bool m_migratedateflag;

        public bool Migratedateflag
        {
            get { return m_migratedateflag; }
            set { m_migratedateflag = value;
            OnPropertyChanged(new PropertyChangedEventArgs("Migratedateflag"));
            }
        }
        private bool m_maxattachflag;

        public bool Maxattachflag
        {
            get { return m_maxattachflag; }
            set { m_maxattachflag = value;
            OnPropertyChanged(new PropertyChangedEventArgs("Maxattachflag"));
            }
        }
        private bool m_skipfolderflag;

        public bool Skipfolderflag
        {
            get { return m_skipfolderflag; }
            set { m_skipfolderflag = value;
            OnPropertyChanged(new PropertyChangedEventArgs("Skipfolderflag"));
            }
        }

        public string ConvertToCSV(Folder[]objectarray,string delimiter)
        {
            string result;
	        System.Text.StringBuilder sb = new System.Text.StringBuilder ();
            foreach (Folder i in objectarray)
	        {
		        if (i == null)
			        continue;
		        sb.Append (i.FolderName);
		        sb.Append (delimiter);
	        }
	        result = sb.ToString ();
            if (result.Length > 0)
                return (result.Substring(0, result.Length - delimiter.Length));
            else
                return "";




        }
    }
}
