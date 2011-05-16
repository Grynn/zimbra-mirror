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
        readonly Users m_users = new Users("", -1);
        ScheduleViewModel scheduleViewModel;

        public UsersViewModel(ScheduleViewModel scheduleViewModel)
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
        }


        // Commands
        public ICommand GetUsersHelpCommand
        {
            get;
            private set;
        }

        private void GetUsersHelp()
        {
            string urlString = (isBrowser) ? "http://10.20.140.218/users.html" : "file:///C:/C:/depot/main/ZimbraMigrationTools/src/c/Misc/Help/users.html";
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
                        try
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
            UsersList.Add(name);
            UsernameEntered = "";
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
            int iSavidx = CurrentUserSelection; // because RemoveAt will make the index -1
            UsersList.RemoveAt(CurrentUserSelection);
            scheduleViewModel.SchedList.RemoveAt(iSavidx);
            scheduleViewModel.EnableMigrate = (scheduleViewModel.SchedList.Count > 0);
        }

        public ICommand SaveCommand
        {
            get;
            private set;
        }

        private void Save()
        {
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

        private ObservableCollection<string> userslist = new ObservableCollection<string>();
        public ObservableCollection<string> UsersList
        {
            get { return userslist; }
        }
        
        public string UsernameEntered
        {
            get { return m_users.UsernameEntered; }
            set
            {
                if (value == m_users.UsernameEntered)
                {
                    return;
                }
                m_users.UsernameEntered = value;
                OnPropertyChanged(new PropertyChangedEventArgs("UsernameEntered"));
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
    }
}
