using System;
using System.IO;
using System.Diagnostics;
using System.ComponentModel;
using System.Windows;
using System.Windows.Input;
using System.Collections;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using MVVM.Model;
using Misc;

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
            this.SaveCommand = new ActionCommand(this.Save, () => true);
            this.NextCommand = new ActionCommand(this.Next, () => true);
            this.scheduleViewModel = scheduleViewModel;
            this.Username = username;
            this.MappedName = mappedname;
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
                                }
                            }
                            else
                            {
                                MessageBox.Show("There is no userinformation stored.Please enter some user info", "Zimbra Migration", MessageBoxButton.OK, MessageBoxImage.Error);
                            }
                        }
                        catch (Exception e)
                        {

                        }

                       // for (int i = 1; i < parsedData.Count; i++)
                        {
                            string[] strres = new string[parsedData.Count];
                            Users tempuser = new Users();

                            for (int j = 1; j < parsedData.Count; j++)
                            {
                                strres = parsedData[j];
                               
                                tempuser.UserName = strres[0];
                                tempuser.MappedName = strres[1];
                                tempuser.ChangePWD = Convert.ToBoolean(strres[2]);
                                tempuser.PWDdefault = strres[3];
                                //string result = tempuser.UserName + "," + tempuser.MappedName +"," + tempuser.ChangePWD + "," + tempuser.PWDdefault;
                                string result = tempuser.Username + "," + tempuser.MappedName;
                                Username = strres[0];
                                MappedName = strres[1];
                                UsersList.Add(new UsersViewModel(null, Username, MappedName));
                                scheduleViewModel.SchedList.Add(Username);
                            }
                            EnableNext = (UsersList.Count > 0);

                        }
                        scheduleViewModel.EnableMigrate = (scheduleViewModel.SchedList.Count > 0);
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
            scheduleViewModel.SchedList.Add(name);
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

        public ICommand SaveCommand
        {
            get;
            private set;
        }

        private void Save()
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

            System.IO.File.WriteAllText(@"UserMap.csv", resultcsv);
            MessageBox.Show("Users saved", "Zimbra Migration", MessageBoxButton.OK, MessageBoxImage.Exclamation);
        }

        public ICommand NextCommand
        {
            get;
            private set;
        }

        private void Next()
        {
            lb.SelectedIndex = 3;
        }
        ////////////////////////

        private ObservableCollection<UsersViewModel> userslist = new ObservableCollection<UsersViewModel>();
        public ObservableCollection<UsersViewModel> UsersList
        {
            get { return userslist; }
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
