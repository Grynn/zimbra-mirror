using System;
using System.IO;
using System.Diagnostics;
using System.ComponentModel;
using System.Windows;
using System.Windows.Input;
using System.Collections;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;
using MVVM.Model;
using Misc;
using CssLib;

namespace MVVM.ViewModel
{
    public class UsersViewModel : BaseViewModel
    {
        readonly Users m_users = new Users("", "", -1);
        ScheduleViewModel scheduleViewModel;

        public UsersViewModel(ScheduleViewModel scheduleViewModel, string username, string mappedname)
        {
            this.GetUsersHelpCommand = new ActionCommand(this.GetUsersHelp, () => true);
            this.ObjectPickerCommand = new ActionCommand(this.ObjectPicker, () => true);
            this.QueryBuilderCommand = new ActionCommand(this.QueryBuilder, () => true);
            this.UserMapCommand = new ActionCommand(this.UserMap, () => true);
            this.PublicFolderCommand = new ActionCommand(this.PublicFolder, () => true);
            this.AddCommand = new ActionCommand(this.Add, () => true);
            this.RemoveCommand = new ActionCommand(this.Remove, () => true);
            this.SaveCSVCommand = new ActionCommand(this.SaveCSV, () => true);
            this.BackCommand = new ActionCommand(this.Back, () => true);
            this.NextCommand = new ActionCommand(this.Next, () => true);
            this.scheduleViewModel = scheduleViewModel;
            this.Username = username;
            this.MappedName = mappedname;
            this.IsProvisioned = false;
        }


        // Commands
        public ICommand GetUsersHelpCommand
        {
            get;
            private set;
        }

        private void GetUsersHelp()
        {
            string urlString = (isBrowser) ? "http://10.20.140.218/users.html" : "file:///C:/depot/main/ZimbraMigrationTools/src/c/Misc/Help/users.html";
            Process.Start(new ProcessStartInfo(urlString));
        }

        public ICommand ObjectPickerCommand
        {
            get;
            private set;
        }

        private void ObjectPicker()
        {
            MessageBox.Show("Object Picker", "Zimbra Migration", MessageBoxButton.OK, MessageBoxImage.Exclamation);
        }

        public ICommand QueryBuilderCommand
        {
            get;
            private set;
        }

        private void QueryBuilder()
        {
            MessageBox.Show("Query Builder", "Zimbra Migration", MessageBoxButton.OK, MessageBoxImage.Exclamation);
        }

        public ICommand UserMapCommand
        {
            get;
            private set;
        }

        private void UserMap()
        {
            bool bCSV = false;
            Microsoft.Win32.OpenFileDialog fDialog = new Microsoft.Win32.OpenFileDialog();
            fDialog.Filter = "User Map Files|*.xml;*.csv";
            fDialog.CheckFileExists = true;
            fDialog.Multiselect = false;
            if (fDialog.ShowDialog() == true)
            {
                int lastDot = fDialog.FileName.LastIndexOf(".");
                if (lastDot != -1)
                {
                    string substr = fDialog.FileName.Substring(lastDot, 4);
                    if (substr == ".csv")
                    {
                        bCSV = true;
                        /*try
                        {
                            string names = File.ReadAllText(fDialog.FileName);
                            string[] nameTokens = names.Split(',');
                            foreach (string name in nameTokens)
                            {
                                UsersList.Add(name);
                                scheduleViewModel.SchedList.Add(name);
                            }
                        }
                        catch (IOException ex)
                        {
                            MessageBox.Show(ex.Message, "Zimbra Migration", MessageBoxButton.OK, MessageBoxImage.Exclamation);
                        }*/
                        List<string[]> parsedData = new List<string[]>();
                        try
                        {
                            if (File.Exists(fDialog.FileName))
                            {
                                using (StreamReader readFile = new StreamReader(fDialog.FileName))
                                {
                                    string line;
                                    string[] row;

                                    while ((line = readFile.ReadLine()) != null)
                                    {
                                        row = line.Split(',');
                                        parsedData.Add(row);
                                    }
                                    readFile.Close();
                                }
                            }
                            else
                            {
                                MessageBox.Show("There is no user information stored.Please enter some user info", "Zimbra Migration", MessageBoxButton.OK, MessageBoxImage.Error);
                            }
                        }
                        catch (Exception e)
                        {

                            string message = e.Message;
                        }

                       // for (int i = 1; i < parsedData.Count; i++)
                        {
                            string[] strres = new string[parsedData.Count];
                            Users tempuser = new Users();

                            for (int j = 0; j < parsedData.Count; j++)
                            {
                                strres = parsedData[j];
                                if (strres[j].Contains("#"))
                                    continue;
                               
                                tempuser.UserName = strres[0];
                                tempuser.MappedName = strres[1];
                                //tempuser.ChangePWD = Convert.ToBoolean(strres[2]);
                                //tempuser.PWDdefault = strres[3];
                                //string result = tempuser.UserName + "," + tempuser.MappedName +"," + tempuser.ChangePWD + "," + tempuser.PWDdefault;
                                string result = tempuser.Username + "," + tempuser.MappedName;
                                Username = strres[0];
                                MappedName = strres[1];
                                UsersList.Add(new UsersViewModel(null, Username, MappedName));
                                scheduleViewModel.SchedList.Add(new SchedUser(Username, false));
                            }
                            EnableNext = (UsersList.Count > 0);

                        }
                        scheduleViewModel.EnableMigrate = (scheduleViewModel.SchedList.Count > 0);

                        ///
                        //Domain information is stored in the xml and not in  the usermap.
                        //will have to revisit 

                        System.Xml.Serialization.XmlSerializer reader =
         new System.Xml.Serialization.XmlSerializer(typeof(Config));
                        if (File.Exists(@"C:\Temp\ZimbraAdminOverView.xml"))
                        {
                            System.IO.StreamReader fileRead = new System.IO.StreamReader(
                               @"C:\Temp\ZimbraAdminOverView.xml");
                            Config Z11 = new Config();
                            Z11 = (Config)reader.Deserialize(fileRead);
                            fileRead.Close();
                            ZimbraDomain = Z11.UserProvision.Domain;
                            if (DomainList.Count > 0)
                            {
                                CurrentDomainSelection = (ZimbraDomain == null) ? 0 : DomainList.IndexOf(ZimbraDomain);
                            }
                            else
                            DomainList.Add(ZimbraDomain);
                           
                        }
                    }
                }
                if (!bCSV)
                {
                    MessageBox.Show("Only CSV files are supported", "Zimbra Migration", MessageBoxButton.OK, MessageBoxImage.Exclamation);
                }
            }
        }

        public ICommand PublicFolderCommand
        {
            get;
            private set;
        }

        private void PublicFolder()
        {
            MessageBox.Show("Public Folder", "Zimbra Migration", MessageBoxButton.OK, MessageBoxImage.Exclamation);
        }

        public ICommand AddCommand
        {
            get;
            private set;
        }

        private void Add(object value)
        {
            var name = value as string;
            UsersList.Add(new UsersViewModel(null, "", ""));
            EnableNext = (UsersList.Count > 0);
            scheduleViewModel.SchedList.Add(new SchedUser(name, false));
            scheduleViewModel.EnableMigrate = (scheduleViewModel.SchedList.Count > 0);
        }

        public ICommand RemoveCommand
        {
            get;
            private set;
        }

        private void Remove()
        {
            UsersList.RemoveAt(CurrentUserSelection);
            EnableNext = (UsersList.Count > 0);
            scheduleViewModel.EnableMigrate = (scheduleViewModel.SchedList.Count > 0);
        }

        public ICommand SaveCSVCommand
        {
            get;
            private set;
        }

        private void SaveCSV()
        {
            List<Users> ListofUsers = new List<Users>();
            for (int i = 0; i < UsersList.Count; i++)
            {
                string users = UsersList[i].Username + ',' + UsersList[i].MappedName;

                string[] nameTokens = users.Split(',');

                Users tempUser = new Users();
                tempUser.UserName = nameTokens.GetValue(0).ToString();
                tempUser.MappedName = nameTokens.GetValue(1).ToString();
                //tempUser.ChangePWD = Convert.ToBoolean(nameTokens.GetValue(2).ToString());
               // tempUser.PWDdefault = nameTokens.GetValue(3).ToString();

                ListofUsers.Add(tempUser);



            }

            string resultcsv = Users.ToCsv<Users>(",", ListofUsers);

            Microsoft.Win32.SaveFileDialog fDialog = new Microsoft.Win32.SaveFileDialog();
            fDialog.Filter = "User Map Files|*.csv";
       
            //fDialog.CheckFileExists = true;
            //fDialog.Multiselect = false;
           
            if (fDialog.ShowDialog() == true)
            {
                string filename = fDialog.FileName;
                //+".csv";
                System.IO.File.WriteAllText(filename, resultcsv);
                MessageBox.Show("Users saved", "Zimbra Migration", MessageBoxButton.OK, MessageBoxImage.Exclamation);
            }

            ///Domain information gets stored in the xml 
            /////will have to revisit.
            SaveDomain();

        }

        public ICommand BackCommand
        {
            get;
            private set;
        }

        private void Back()
        {
            lb.SelectedIndex = 2;
        }

        public ICommand NextCommand
        {
            get;
            private set;
        }

        private void Next()
        {
            ZimbraAPI zimbraAPI = new ZimbraAPI();
            if (ZimbraValues.zimbraValues.AuthToken.Length == 0)
            {
                MessageBox.Show("You must log on to the Zimbra server", "Zimbra Migration", MessageBoxButton.OK, MessageBoxImage.Error);
                return;
            }

            SaveDomain();

            for (int i = 0; i < UsersList.Count; i++)
            {
                string userName = (UsersList[i].MappedName.Length > 0) ? UsersList[i].MappedName : UsersList[i].Username;
                string acctName = userName + '@' + ZimbraDomain;
                if (zimbraAPI.GetAccount(acctName) == 0)
                {
                    UsersList[i].IsProvisioned = true;
                    scheduleViewModel.SchedList[i].isProvisioned = true;    // get (SchedList) in schedule view model will set again
                }
                else
                if (zimbraAPI.LastError.IndexOf("no such account") != -1)
                {
                    UsersList[i].IsProvisioned = false;                     // get (SchedList) in schedule view model will set again
                    scheduleViewModel.SchedList[i].isProvisioned = false;
                }
                else
                {
                    MessageBox.Show(string.Format("Error accessing account {0}: {1}", acctName, zimbraAPI.LastError), "Zimbra Migration", MessageBoxButton.OK, MessageBoxImage.Error);
                }
            }

            lb.SelectedIndex = 4;
        }

        private void SaveDomain()
        {


            try
            {

                ZimbraDomain = DomainList[CurrentDomainSelection];
                if (File.Exists(@"C:\Temp\ZimbraAdminOverView.xml"))
                {
                    UpdateXmlElement(@"C:\Temp\ZimbraAdminOverView.xml", "UserProvision");
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
            }
            catch (ArgumentOutOfRangeException e)
            {
                string message = "CurrentDomainSelection " + e.Message;
                MessageBox.Show(message, "Zimbra Migration", MessageBoxButton.OK, MessageBoxImage.Warning);
            }
            
        }
        ////////////////////////

        private ObservableCollection<UsersViewModel> userslist = new ObservableCollection<UsersViewModel>();
        public ObservableCollection<UsersViewModel> UsersList
        {
            get { return userslist; }
        }

        private ObservableCollection<string> domainlist = new ObservableCollection<string>();
        public ObservableCollection<string> DomainList
        {
            get { return domainlist; }
            set
            {
                domainlist = value;
               
            }
        }

        public string Username
        {
            get { return m_users.Username; }
            set
            {
                if (value == m_users.Username)
                {
                    return;
                }
                m_users.Username = value;
                OnPropertyChanged(new PropertyChangedEventArgs("Username"));
            }
        }

        public string MappedName
        {
            get { return m_users.MappedName; }
            set
            {
                if (value == m_users.MappedName)
                {
                    return;
                }
                m_users.MappedName = value;
                OnPropertyChanged(new PropertyChangedEventArgs("MappedName"));
            }
        }

        public string ZimbraDomain
        {
            get { return m_config.UserProvision.Domain; }
            set
            {
                if (value == m_config.UserProvision.Domain)
                {
                    return;
                }
                m_config.UserProvision.Domain = value;

                OnPropertyChanged(new PropertyChangedEventArgs("ZimbraDomain"));
            }
        }

        public int CurrentUserSelection
        {
            get { return m_users.CurrentUserSelection; }
            set
            {
                if (value == m_users.CurrentUserSelection)
                {
                    return;
                }
                m_users.CurrentUserSelection = value;
                MinusEnabled = (value != -1);
                OnPropertyChanged(new PropertyChangedEventArgs("CurrentUserSelection"));
            }
        }

        public int CurrentDomainSelection
        {
            get { return domainselection; }
            set
            {

                domainselection = value;

                OnPropertyChanged(new PropertyChangedEventArgs("CurrentDomainSelection"));
            }
        }
        private int domainselection;

        private bool minusEnabled;
        public bool MinusEnabled
        {
            get { return minusEnabled; }
            set
            {
                minusEnabled = value;
                OnPropertyChanged(new PropertyChangedEventArgs("MinusEnabled"));
            }
        }

        private bool isProvisioned;
        public bool IsProvisioned
        {
            get { return isProvisioned; }
            set
            {
                isProvisioned = value;
                OnPropertyChanged(new PropertyChangedEventArgs("IsProvisioned"));
            }
        }

        private bool enableNext;
        public bool EnableNext
        {
            get { return enableNext; }
            set
            {
                enableNext = value;
                OnPropertyChanged(new PropertyChangedEventArgs("EnableNext"));
            }
        }
    }
}
