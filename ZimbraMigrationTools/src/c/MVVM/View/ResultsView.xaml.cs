using System;
using System.IO;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Markup;
using System.Windows.Input;
using System.Collections;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Windows.Data;
using System.Windows.Media;
using MVVM.Model;
using MVVM.ViewModel;
using MVVM.View.CTI;
using CssLib;

namespace MVVM.View
{
    public partial class ResultsView
    {
        ListView[] urListView = new ListView[16];
        ListBox [] lbErrors = new ListBox[16];
        int iTabCount = 0;

        public ResultsView()
        {
            InitializeComponent();
            this.AddHandler(CloseableTabItem.CloseTabEvent, new RoutedEventHandler(this.CloseTab));
        }

        private AccountResultsViewModel ViewModel
        {
            get { return DataContext as AccountResultsViewModel; }
        }

        private int GetAcctNum(string hdr)
        {
            int accountnum = -1;
            for (int i = 0; i < ViewModel.AccountResultsList.Count; i++)
            {
                if (hdr == ViewModel.AccountResultsList[i].AccountName)
                {
                    accountnum = ViewModel.AccountResultsList[i].GetAccountNum();
                    break;
                }
            }
            return accountnum;
        }

        protected void HandleDoubleClick(object sender, MouseButtonEventArgs e)
        {
            if (iTabCount == 16)
            {
                MessageBox.Show(string.Format("Only 16 tabs may be open at a time", MessageBoxButton.OK, MessageBoxImage.Error));
                return;
            }

            ListViewItem lvi = sender as ListViewItem;
            var content = lvi.Content as AccountResultsViewModel;

            TabControl tabCtrl = FindParent(lvi, typeof(TabControl)) as TabControl;
            CloseableTabItem userItem = new CloseableTabItem();
            userItem.Header = content.AccountName;

            // get accountnum so we can keep the listboxes and listviews straight
            int accountnum = GetAcctNum((string)userItem.Header);
            if (urListView[accountnum] != null)
            {
                for (int i = 0; i < tabCtrl.Items.Count; i++)
                {
                    TabItem item = (TabItem)tabCtrl.Items[i];
                    if (item.Header.ToString() == content.AccountName)
                    {
                        tabCtrl.SelectedIndex = i;
                        break;
                    }
                }
                return;
            }

            iTabCount++;

            Grid urGrid = new Grid();

            // set up the grid's rows
            RowDefinition rowDef1 = new RowDefinition();
            RowDefinition rowDef2 = new RowDefinition();
            rowDef1.MaxHeight = 250;
            rowDef2.Height = GridLength.Auto;
            urGrid.Height = 280;    // so we'll get  Vertical scrollviewer
            urGrid.RowDefinitions.Add(rowDef1);
            urGrid.RowDefinitions.Add(rowDef2);
            //

            // Set up the ListView
            urListView[accountnum] = new ListView();
            urListView[accountnum].FontSize = 11;
            urListView[accountnum].SetValue(Grid.RowProperty, 0);
            urListView[accountnum].Margin = new Thickness(5);
            urListView[accountnum].Name = "lstUserResults";

            GridView urGridView = new GridView();

            GridViewColumn gvc1 = new GridViewColumn();

            // set up columns widths so we won't get a horizontal scrollbar
            GridViewColumnHeader gvc1H = new GridViewColumnHeader();
            gvc1H.FontSize = 11;
            gvc1H.Width = 195;
            gvc1H.Content = " Folder";
            gvc1H.HorizontalContentAlignment = System.Windows.HorizontalAlignment.Left;
            gvc1.DisplayMemberBinding = new Binding("FolderName");
            gvc1.Header = gvc1H;
            urGridView.Columns.Add(gvc1);

            GridViewColumn gvc2 = new GridViewColumn();
            GridViewColumnHeader gvc2H = new GridViewColumnHeader();
            gvc2H.FontSize = 11;
            gvc2H.Width = 130;
            gvc2H.Content = " Type";
            gvc2H.HorizontalContentAlignment = System.Windows.HorizontalAlignment.Left;
            gvc2.DisplayMemberBinding = new Binding("TypeName");
            gvc2.Header = gvc2H;
            urGridView.Columns.Add(gvc2);

            GridViewColumn gvc3 = new GridViewColumn();
            GridViewColumnHeader gvc3H = new GridViewColumnHeader();
            gvc3H.FontSize = 11;
            gvc3H.Width = 120;
            gvc3H.Content = " Progress";
            gvc3H.HorizontalContentAlignment = System.Windows.HorizontalAlignment.Left;
            gvc3.DisplayMemberBinding = new Binding("UserProgressMsg");
            gvc3.Header = gvc3H;
            urGridView.Columns.Add(gvc3);

            urListView[accountnum].View = urGridView;

            urGrid.Children.Add(urListView[accountnum]);
            //

            // now create Listbox for errors
            lbErrors[accountnum] = new ListBox();
            lbErrors[accountnum].FontSize = 11;
            lbErrors[accountnum].SetValue(Grid.RowProperty, 1);
            lbErrors[accountnum].Margin = new Thickness(5, 5, 5, 5);
            lbErrors[accountnum].MinHeight = 120;
            lbErrors[accountnum].MaxHeight = 120;
            lbErrors[accountnum].MinWidth = 450;
            lbErrors[accountnum].HorizontalAlignment = System.Windows.HorizontalAlignment.Stretch;
            lbErrors[accountnum].VerticalAlignment = System.Windows.VerticalAlignment.Top;
            urGrid.Children.Add(lbErrors[accountnum]);
            //

            userItem.Content = urGrid;

            tabCtrl.Items.Add(userItem);
            userItem.IsSelected = true;
        }

        protected void HandleGotFocus(object sender, EventArgs e)
        {
            TabItem ti = sender as TabItem;
            string hdr = ti.Header.ToString();
            ViewModel.SelectedTab = hdr;
            int accountnum = -1;

            if (hdr != "Accounts")
            {
                accountnum = GetAcctNum(hdr);
            }
            if (accountnum != -1)
            {
                ViewModel.PBValue = ViewModel.AccountResultsList[accountnum].PBValue;
                ViewModel.UserPBMsgValue = ViewModel.AccountResultsList[accountnum].PBMsgValue;
                ViewModel.AccountOnTab = accountnum;

                ObservableCollection<UserResults> userResultsList = new ObservableCollection<UserResults>();
                Binding binding = new Binding();
                binding.Source = userResultsList;
                BindingOperations.SetBinding(urListView[accountnum], ListView.ItemsSourceProperty, binding);

                AccountResultsViewModel ar = ViewModel.AccountResultsList[accountnum];
                int count = ar.AccountFolderInfoList.Count;
                for (int i = 0; i < ar.AccountFolderInfoList.Count; i++)
                {
                    FolderInfo folderInfo = ar.AccountFolderInfoList[i];
                    if (folderInfo != null)
                    {
                        string msg = (i == (count - 1)) ? ar.AcctProgressMsg : folderInfo.FolderProgress;
                        UserResults ur = new UserResults(folderInfo.FolderName, folderInfo.FolderType, msg);
                        userResultsList.Add(ur);
                    }
                }

                lbErrors[accountnum].Items.Clear();
                for (int i = 0; i < ar.AccountProblemsList.Count; i++)
                {
                    ProblemInfo problemInfo = ar.AccountProblemsList[i];
                    if (problemInfo != null)
                    {
                        ListBoxItem item = new ListBoxItem();   // hack for now -- will do it right with binding later
                        item.Content = problemInfo.FormattedMsg;
                        lbErrors[accountnum].Items.Add(item);
                    }
                }
            }
            

            // show the progress bar if an account tab has the focus
            System.Windows.Visibility swv = (hdr == "Accounts") ? System.Windows.Visibility.Hidden : System.Windows.Visibility.Visible;
            pbMigrationS.Visibility = swv;
            labelSchedInfo.Visibility = swv;
            //
        }

        private void CloseTab(object source, RoutedEventArgs args)
        {
            TabItem tabItem = args.OriginalSource as CloseableTabItem;
            if (tabItem != null)
            {
                TabControl tabControl = tabItem.Parent as TabControl;
                if (tabControl != null)
                {
                    int accountnum = GetAcctNum((string)tabItem.Header);
                    tabControl.Items.Remove(tabItem);
                    urListView[accountnum] = null;
                    lbErrors[accountnum] = null;
                    iTabCount--;
                }
            }
        }

        public DependencyObject FindParent(DependencyObject o, Type parentType)
        {
            DependencyObject parent = o;
            while (parent != null)
            {
                if (parent.GetType() == parentType)
                    break;
                else
                    parent = VisualTreeHelper.GetParent(parent);
            }

            return parent;
        }
    }       

}
