using System;
using System.Diagnostics;
using System.ComponentModel;
using System.Windows;
using System.Windows.Input;
using System.Windows.Controls;
using System.Collections;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Runtime.InteropServices;
using System.Text;
using MVVM.Model;
using Misc;
using System.IO;

namespace MVVM.ViewModel
{
    public class IntroViewModel : BaseViewModel
    {
        Intro m_intro = new Intro();
        public ObservableCollection<object> TheViews { get; set; }

        private ConfigViewModelS m_configViewModelS;
        private ConfigViewModelU m_configViewModelU;
        private OptionsViewModel m_optionsViewModel;
        private UsersViewModel m_usersViewModel;
        private ScheduleViewModel m_scheduleViewModel;
        private AccountResultsViewModel m_resultsViewModel;
        public IntroViewModel(ListBox lbMode)
        {
            lb = lbMode;
            this.GetIntroLicenseCommand = new ActionCommand(this.GetIntroLicense, () => true);
            this.GetIntroHelpCommand = new ActionCommand(this.GetIntroHelp, () => true);
            this.GetIntroUserMigCommand = new ActionCommand(this.GetIntroUserMig, () => true);
            this.GetIntroServerMigCommand = new ActionCommand(this.GetIntroServerMig, () => true);
        }

        public ICommand GetIntroLicenseCommand
        {
            get;
            private set;
        }

        private void GetIntroLicense()
        {
            string urlString = "http://files.zimbra.com/website/docs/zimbra_network_la.pdf";
            Process.Start(new ProcessStartInfo(urlString));
        }

        public ICommand GetIntroHelpCommand
        {
            get;
            private set;
        }

        private void GetIntroHelp()
        {
            string urlString = (isBrowser) ? "http://10.20.140.218/intro.html" : "file:///C:/depot/main/ZimbraMigrationTools/src/c/Misc/Help/intro.html";
            Process.Start(new ProcessStartInfo(urlString));
        }

        public ICommand GetIntroUserMigCommand
        {
            get;
            private set;
        }

        private void GetIntroUserMig()
        {
            m_optionsViewModel.isServer = false;
            m_optionsViewModel.ImportNextButtonContent = "Migrate";
            m_scheduleViewModel.isServer = false;
            TheViews.RemoveAt(0);
            TheViews.Add(m_configViewModelU);
            TheViews.Add(m_optionsViewModel);
            TheViews.Add(m_resultsViewModel);
            lb.Visibility = Visibility.Visible;
            lb.IsEnabled = true;
            lb.SelectedIndex = 0;
        }

        public ICommand GetIntroServerMigCommand
        {
            get;
            private set;
        }

        private void GetIntroServerMig()
        {
            m_optionsViewModel.isServer = true;
            m_optionsViewModel.ImportNextButtonContent = "Next";
            m_scheduleViewModel.isServer = true;
            TheViews.RemoveAt(0);
            TheViews.Add(m_configViewModelS);
            TheViews.Add(m_optionsViewModel);
            TheViews.Add(m_usersViewModel);
            TheViews.Add(m_scheduleViewModel);
            TheViews.Add(m_resultsViewModel);
            lb.Visibility = Visibility.Visible;
            lb.IsEnabled = true;
            lb.SelectedIndex = 0;
        }

        public string WelcomeMsg
        {
            get { return m_intro.WelcomeMsg; }
            set
            {
                if (value == m_intro.WelcomeMsg)
                {
                    return;
                }
                m_intro.WelcomeMsg = value;

                OnPropertyChanged(new PropertyChangedEventArgs("WelcomeMsg"));
            }
        }

        public void SetupViews()
        {
            m_configViewModelS = new ConfigViewModelS();
            m_configViewModelS.Name = "ConfigViewModelS";
            m_configViewModelS.ViewTitle = "Configuration";
            m_configViewModelS.ImageName = "Images/CreateSpaceImage.jpg";
            m_configViewModelS.lb = lb;
            m_configViewModelS.isBrowser = false;
           // m_configViewModelS.ExchangeProfile = "";
            m_configViewModelS.OutlookProfile = "";
            m_configViewModelS.ZimbraServerHostName = "";
            m_configViewModelS.ZimbraPort = "";
            m_configViewModelS.MailServerHostName = "";
            m_configViewModelS.ZimbraAdmin = "";
            m_configViewModelS.ZimbraAdminPasswd = "";
            m_configViewModelS.ZimbraDomain = "";
            m_configViewModelS.OutlookProfile = "";
            

            m_configViewModelU = new ConfigViewModelU();
            m_configViewModelU.Name = "ConfigViewModelU";
            m_configViewModelU.ViewTitle = "Configuration";
            m_configViewModelU.ImageName = "Images/CreateSpaceImage.jpg";
            m_configViewModelU.lb = lb;
            m_configViewModelU.isBrowser = false;
            m_configViewModelU.OutlookProfile = "";
            m_configViewModelU.ZimbraPort = "";
            m_configViewModelU.PSTFile = "";
            m_configViewModelU.ZimbraUser = "";
            m_configViewModelU.ZimbraUserPasswd = "";
            m_configViewModelU.ZimbraDomain = "";
            m_configViewModelU.OutlookProfile = "";

            m_optionsViewModel = new OptionsViewModel();
            m_optionsViewModel.Name = "OptionsViewModel";
            m_optionsViewModel.ViewTitle = "Options";
            m_optionsViewModel.ImageName = "Images/DMR_120.jpg";
            m_optionsViewModel.lb = lb;
            m_optionsViewModel.isServer = true;
            m_optionsViewModel.isBrowser = false;
            m_optionsViewModel.ImportMailOptions = false;
            m_optionsViewModel.ImportTaskOptions = false;
            m_optionsViewModel.ImportCalendarOptions = false;
            m_optionsViewModel.ImportContactOptions = false;
            m_optionsViewModel.ImportJunkOptions = false;
            m_optionsViewModel.ImportDeletedItemOptions = false;
            m_optionsViewModel.ImportSentOptions = false;
            m_optionsViewModel.ImportRuleOptions = false;
            m_optionsViewModel.MigrateONRAfter = DateTime.UtcNow.ToShortDateString();

            m_scheduleViewModel = new ScheduleViewModel();
            m_scheduleViewModel.Name = "Schedule";
            m_scheduleViewModel.ViewTitle = "Schedule";
            m_scheduleViewModel.ImageName = "Images/Penguins.jpg";
            m_scheduleViewModel.lb = lb;
            m_scheduleViewModel.isBrowser = false;
           m_scheduleViewModel.COS = "default";
            m_scheduleViewModel.DefaultPWD = "";

            m_usersViewModel = new UsersViewModel(m_scheduleViewModel,"",""); // needs scheduleviewmodel so schedlist will be in sync
            m_usersViewModel.Name = "Users";
            m_usersViewModel.ViewTitle = "Users";
            m_usersViewModel.ImageName = "Images/UnknownPerson_dataNotFound.jpg";
            m_usersViewModel.lb = lb;
            m_usersViewModel.isBrowser = false;
            m_usersViewModel.CurrentUserSelection = -1;

            m_resultsViewModel = new AccountResultsViewModel(m_scheduleViewModel, 0, "", "", 0, 0, 0, false);
            m_resultsViewModel.Name = "Results";
            m_resultsViewModel.ViewTitle = "Results";
            m_resultsViewModel.ImageName = "Images/NikiBest.jpg";
            m_resultsViewModel.isBrowser = false;
            m_resultsViewModel.CurrentAccountSelection = -1;

            m_scheduleViewModel.SetConfigUModel(m_configViewModelU);
            m_scheduleViewModel.SetUserModel(m_usersViewModel);
            m_scheduleViewModel.SetResultsModel(m_resultsViewModel);

            m_optionsViewModel.SetScheduleModel(m_scheduleViewModel);

            TheViews = new ObservableCollection<object>();
            TheViews.Add(this);
        }

       

      
    }
}
