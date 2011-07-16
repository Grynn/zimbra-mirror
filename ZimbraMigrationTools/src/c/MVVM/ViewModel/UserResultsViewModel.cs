using System;
using System.IO;
using System.Diagnostics;
using System.ComponentModel;
using System.Windows;
using System.Windows.Input;
using System.Collections;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Threading;
using MVVM.Model;

namespace MVVM.ViewModel
{
    public class UserResultsViewModel : BaseViewModel
    {
        readonly UserResults m_userResults = new UserResults("", "", "");

        public UserResultsViewModel()
        {

        }

        private ObservableCollection<UserResultsViewModel> userResultsList = new ObservableCollection<UserResultsViewModel>();
        public ObservableCollection<UserResultsViewModel> UserResultsViewModelList
        {
            get { return userResultsList; }
        }

        public string FolderName
        {
            get { return m_userResults.FolderName; }
            set
            {
                if (value == m_userResults.FolderName)
                {
                    return;
                }
                m_userResults.FolderName = value;
                OnPropertyChanged(new PropertyChangedEventArgs("FolderName"));
            }
        }

        public string TypeName
        {
            get { return m_userResults.TypeName; }
            set
            {
                if (value == m_userResults.TypeName)
                {
                    return;
                }
                m_userResults.TypeName = value;
                OnPropertyChanged(new PropertyChangedEventArgs("TypeName"));
            }
        }

        public string UserProgressMsg
        {
            get { return m_userResults.UserProgressMsg; }
            set
            {
                if (value == m_userResults.UserProgressMsg)
                {
                    return;
                }
                m_userResults.UserProgressMsg = value;
                OnPropertyChanged(new PropertyChangedEventArgs("UserProgressMsg"));
            }
        }

        public string ObjName
        {
            get { return m_userResults.ObjName; }
            set
            {
                if (value == m_userResults.ObjName)
                {
                    return;
                }
                m_userResults.ObjName = value;
                OnPropertyChanged(new PropertyChangedEventArgs("ObjName"));
            }
        }

        public string TheErr
        {
            get { return m_userResults.TheErr; }
            set
            {
                if (value == m_userResults.TheErr)
                {
                    return;
                }
                m_userResults.TheErr = value;
                OnPropertyChanged(new PropertyChangedEventArgs("TheErr"));
            }
        }
    }
}
