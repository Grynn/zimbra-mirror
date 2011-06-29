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
        private IntroViewModel m_introViewModel;

        public MainViewW()
        {
            InitializeComponent();

            m_introViewModel = new IntroViewModel(lbMode);
            m_introViewModel.Name = "IntroViewModel";
            m_introViewModel.ViewTitle = "Intro";
            m_introViewModel.ImageName = "Images/CongifSource.jpg"; // doesn't matter since LB is hidden
            m_introViewModel.lb = lbMode;
            m_introViewModel.isBrowser = true;
            Intro intro = new Intro();
            m_introViewModel.WelcomeMsg = intro.WelcomeMsg;
            m_introViewModel.SetupViews(true);

            lbMode.SelectedIndex = 0;
            DataContext = m_introViewModel;
        }
    }
}
