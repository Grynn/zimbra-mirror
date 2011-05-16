using System.Diagnostics;
using System.Windows.Navigation;
using System.Windows.Controls;
using System.Windows.Markup;
using System.Windows;
using System.Collections;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Windows.Documents;

using MVVM.Model;
using MVVM.ViewModel;

namespace MVVM.View
{
    /// <summary>
    /// The main view window
    /// </summary>
    public partial class MainViewW
    {
        public ObservableCollection<object> TheViews { get; set; }
        TextBlock m_introTB = new TextBlock();
        TextBlock m_helpTB = new TextBlock();
        TextBlock m_licenseTB = new TextBlock();
        Button m_StartButton = new Button();
        RadioButton m_RadioButtonU = new RadioButton();
        RadioButton m_RadioButtonS = new RadioButton();
        private ConfigViewModelS m_configViewModelS;
        private ConfigViewModelU m_configViewModelU;
        private OptionsViewModel m_optionsViewModel;
        private UsersViewModel m_usersViewModel;
        private ScheduleViewModel m_scheduleViewModel;
        private AccountResultsViewModel m_resultsViewModel;
        private bool m_serverMigration = true;

        public MainViewW()
        {
            InitializeComponent();

            // Let's try to get this out of here into a ViewModel of some kind

            m_introTB.Margin = new Thickness(0, 40, 0, 0);
            m_introTB.HorizontalAlignment = System.Windows.HorizontalAlignment.Center;
            m_introTB.VerticalAlignment = VerticalAlignment.Top;
            m_introTB.TextWrapping = TextWrapping.Wrap;
            m_introTB.MaxWidth = 350;

            Intro intro = new Intro();
            m_introTB.Text = intro.Msg;

            IAddChild container = contentGrid;
            container.AddChild(m_introTB);

            m_licenseTB.Margin = new Thickness(0, 220, 0, 0);
            m_licenseTB.HorizontalAlignment = System.Windows.HorizontalAlignment.Center;
            Hyperlink linkL = new Hyperlink();
            linkL.NavigateUri = new System.Uri("http://files.zimbra.com/website/docs/zimbra_network_la.pdf");
            linkL.Inlines.Add("VMware Zimbra License");
            linkL.RequestNavigate += new RequestNavigateEventHandler(Hyperlink_IntroNavigate);
            m_licenseTB.Inlines.Add(linkL);
            container.AddChild(m_licenseTB);

            m_RadioButtonU.Margin = new Thickness(66, 270, 0, 0);
            m_RadioButtonU.Content = "User Migration";
            m_RadioButtonU.IsEnabled = true;
            m_RadioButtonU.IsChecked = false;
            m_RadioButtonU.Click += new RoutedEventHandler(OnRadioButtonUClick);
            container.AddChild(m_RadioButtonU);

            m_RadioButtonS.Margin = new Thickness(200, 270, 0, 0);
            m_RadioButtonS.Content = "Server Migration";
            m_RadioButtonS.IsEnabled = true;
            m_RadioButtonS.IsChecked = true;
            m_RadioButtonS.Click += new RoutedEventHandler(OnRadioButtonSClick);
            container.AddChild(m_RadioButtonS);

            m_helpTB.Margin = new Thickness(66, 330, 0, 0);
            m_helpTB.HorizontalAlignment = System.Windows.HorizontalAlignment.Left;
            m_helpTB.FontSize = 12;
            m_helpTB.FontFamily = new System.Windows.Media.FontFamily(new System.Uri("pack://application:,,,/"), "#Times New Roman");
            Hyperlink linkH = new Hyperlink();
            linkH.NavigateUri = new System.Uri("http://10.20.140.218/intro.html");
            linkH.Inlines.Add("Help");
            linkH.RequestNavigate += new RequestNavigateEventHandler(Hyperlink_IntroNavigate);    
            m_helpTB.Inlines.Add(linkH);
            container.AddChild(m_helpTB);

            m_StartButton.Margin = new Thickness(260, 280, 0, 0);
            m_StartButton.HorizontalAlignment = System.Windows.HorizontalAlignment.Center;
            m_StartButton.MinWidth = 75;
            m_StartButton.MaxHeight = 20;
            m_StartButton.Content = "Start";
            m_StartButton.IsEnabled = false;
            m_StartButton.Click += new RoutedEventHandler(OnStartButtonClick);
            container.AddChild(m_StartButton);

            m_configViewModelS = new ConfigViewModelS();
            m_configViewModelS.Name = "ConfigViewModelS";
            m_configViewModelS.ViewTitle = "Configuration";
            m_configViewModelS.ImageName = "Images/CreateSpaceImage.jpg";
            m_configViewModelS.lb = lbMode;
            m_configViewModelS.isBrowser = true;
            m_configViewModelS.ExchangeProfile = "";
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
            m_configViewModelU.lb = lbMode;
            m_configViewModelU.isBrowser = false;
            m_configViewModelU.OutlookProfile = "";
            m_configViewModelU.ZimbraPort = "";
            m_configViewModelU.PSTFile = "";
            m_configViewModelU.ZimbraAdmin = "";
            m_configViewModelU.ZimbraAdminPasswd = "";
            m_configViewModelU.ZimbraDomain = "";
            m_configViewModelU.OutlookProfile = "";

            m_optionsViewModel = new OptionsViewModel();
            m_optionsViewModel.Name = "OptionsViewModel";
            m_optionsViewModel.ViewTitle = "Options";
            m_optionsViewModel.ImageName = "Images/DMR_120.jpg";
            m_optionsViewModel.lb = lbMode;
            m_optionsViewModel.isServer = true;
            m_optionsViewModel.isBrowser = true;
            m_optionsViewModel.ImportMailOptions = false;
            m_optionsViewModel.ImportTaskOptions = false;
            m_optionsViewModel.ImportCalendarOptions = false;
            m_optionsViewModel.ImportContactOptions = false;
            m_optionsViewModel.ImportJunkOptions = false;
            m_optionsViewModel.ImportDeletedItemOptions = false;
            m_optionsViewModel.ImportSentOptions = false;
            m_optionsViewModel.ImportRuleOptions = false;

            m_scheduleViewModel = new ScheduleViewModel();
            m_scheduleViewModel.Name = "Schedule";
            m_scheduleViewModel.ViewTitle = "Schedule";
            m_scheduleViewModel.ImageName = "Images/Penguins.jpg";
            m_scheduleViewModel.lb = lbMode;
            m_scheduleViewModel.isBrowser = true;

            m_usersViewModel = new UsersViewModel(m_scheduleViewModel); // needs scheduleviewmodel so schedlist will be in sync
            m_usersViewModel.Name = "Users";
            m_usersViewModel.ViewTitle = "Users";
            m_usersViewModel.ImageName = "Images/UnknownPerson_dataNotFound.jpg";
            m_usersViewModel.lb = lbMode;
            m_usersViewModel.isBrowser = true;
            m_usersViewModel.CurrentUserSelection = -1;

            m_resultsViewModel = new AccountResultsViewModel(m_scheduleViewModel, 0, "", "", 0, 0, 0, false);
            m_resultsViewModel.Name = "Results";
            m_resultsViewModel.ViewTitle = "Results";
            m_resultsViewModel.ImageName = "Images/NikiBest.jpg";
            m_resultsViewModel.isBrowser = true;
            m_resultsViewModel.CurrentAccountSelection = -1;

            m_scheduleViewModel.SetUserModel(m_usersViewModel);
            m_scheduleViewModel.SetResultsModel(m_resultsViewModel);

            m_optionsViewModel.SetScheduleModel(m_scheduleViewModel);

            TheViews = new ObservableCollection<object>();
            TheViews.Add(m_configViewModelS);
            TheViews.Add(m_optionsViewModel);
            TheViews.Add(m_usersViewModel);
            TheViews.Add(m_scheduleViewModel);
            TheViews.Add(m_resultsViewModel);
            DataContext = this;
            m_StartButton.IsEnabled = true;
            DataContext = this;
        }

        private void OnRadioButtonUClick(object sender, RoutedEventArgs e)
        {
            m_serverMigration = false;
            m_StartButton.IsEnabled = true;
            ListBoxItem lbiUsersItem = (ListBoxItem)(lbMode.ItemContainerGenerator.ContainerFromIndex(2));
            lbiUsersItem.IsEnabled = false;
            ListBoxItem lbiSchedItem = (ListBoxItem)(lbMode.ItemContainerGenerator.ContainerFromIndex(3));
            lbiSchedItem.IsEnabled = false;
            m_optionsViewModel.isServer = false;
            TheViews.RemoveAt(0);
            TheViews.Insert(0, m_configViewModelU);
        }

        private void OnRadioButtonSClick(object sender, RoutedEventArgs e)
        {
            m_serverMigration = true;
            m_StartButton.IsEnabled = true;
            m_optionsViewModel.isServer = true;
            TheViews.RemoveAt(0);
            TheViews.Insert(0, m_configViewModelS);
        }

        private void OnStartButtonClick(object sender, RoutedEventArgs e)
        {
            lbMode.IsEnabled = true;
            lbMode.SelectedIndex = 0;
        }

        private void Hyperlink_IntroNavigate(object sender, RequestNavigateEventArgs e)
        {
            Process.Start(new ProcessStartInfo(e.Uri.AbsoluteUri));
            e.Handled = true;
        }

        private void lbMode_SelectionChanged(object sender, SelectionChangedEventArgs e)
        {
            m_introTB.Text = "";
            m_StartButton.Visibility = System.Windows.Visibility.Hidden;
            m_RadioButtonU.Visibility = System.Windows.Visibility.Hidden;
            m_RadioButtonS.Visibility = System.Windows.Visibility.Hidden;
            m_helpTB.Text = "";
            m_licenseTB.Text = "";
        }
    }
}
