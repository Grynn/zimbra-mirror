using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Collections;
using System.Diagnostics;
using System.Windows.Controls;
using System.Windows.Documents;
using System.Windows.Input;
using System.Windows.Markup;
using System.Windows.Media;
using System.Windows.Navigation;
using System.Windows;

using MVVM.Model;
using MVVM.ViewModel;

namespace MVVM.View
{
// / <summary>
// / The main view window
// / </summary>
public partial class MainViewA
{
    private BaseViewModel m_baseViewModel;
    private IntroViewModel m_introViewModel;

    public MainViewA()
    {
        InitializeComponent();

        Application.Current.Properties["migrationmode"] = "server";

        m_baseViewModel = new BaseViewModel();

        m_introViewModel = new IntroViewModel(lbMode);
        m_introViewModel.Name = "IntroViewModel";
        m_introViewModel.ViewTitle = "Welcome";
        m_introViewModel.lb = lbMode;
        m_introViewModel.isBrowser = false;

        Intro intro = new Intro();

        m_introViewModel.WelcomeMsg = intro.WelcomeMsg;
        m_introViewModel.InstallDir = intro.InstallDir;
        m_introViewModel.SetupViews(false);
        m_introViewModel.AddViews(false);

        lbMode.SelectedIndex = 0;
        DataContext = m_introViewModel;
    }

    private void ViewListTB_MouseDown(object sender, MouseButtonEventArgs e)
    {
        if (m_introViewModel.mw == null)
            m_introViewModel.Next();

        else if (m_introViewModel.mw.MailClient == null)
            m_introViewModel.Next();
        TextBlock tb = (TextBlock)sender;
        if (tb.Text == "Migrate")
        {
            UsersViewModel usersViewModel = m_introViewModel.GetUsersViewModel();

            usersViewModel.ValidateUsersList(false);
        }
    }
}
}
