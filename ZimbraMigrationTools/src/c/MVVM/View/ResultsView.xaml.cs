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

namespace MVVM.View
{
    public partial class ResultsView
    {
        public ResultsView()
        {
            InitializeComponent();
            this.AddHandler(CloseableTabItem.CloseTabEvent, new RoutedEventHandler(this.CloseTab));
        }

        private AccountResultsViewModel ViewModel
        {
            get { return DataContext as AccountResultsViewModel; }
        }

        protected void HandleDoubleClick(object sender, MouseButtonEventArgs e)
        {
            ListViewItem lvi = sender as ListViewItem;
            var content = lvi.Content as AccountResultsViewModel;

            TabControl tabCtrl = FindParent(lvi, typeof(TabControl)) as TabControl;
            CloseableTabItem userItem = new CloseableTabItem();
            userItem.Header = content.AccountName;

            Grid urGrid = new Grid();

            ListView urListView = new ListView();
            urListView.FontSize = 11;
            urListView.Margin = new Thickness(5);
            urListView.Name = "lstUserResults";

            ObservableCollection<UserResults> userResults = new ObservableCollection<UserResults>();    // bind to dynamic data
            Binding binding = new Binding();
            binding.Source = userResults;
            BindingOperations.SetBinding(urListView, ListView.ItemsSourceProperty, binding);

            GridView urGridView = new GridView();

            GridViewColumn gvc1 = new GridViewColumn();

            GridViewColumnHeader gvc1H = new GridViewColumnHeader();
            gvc1H.FontSize = 11;
            gvc1H.Width = 140;
            gvc1H.Content = "Folder";
            gvc1H.HorizontalContentAlignment = System.Windows.HorizontalAlignment.Left;
            gvc1.DisplayMemberBinding = new Binding("FolderName");
            gvc1.Header = gvc1H;
            urGridView.Columns.Add(gvc1);

            GridViewColumn gvc2 = new GridViewColumn();
            GridViewColumnHeader gvc2H = new GridViewColumnHeader();
            gvc2H.FontSize = 11;
            gvc2H.Width = 140;
            gvc2H.Content = "Name";
            gvc2H.HorizontalContentAlignment = System.Windows.HorizontalAlignment.Left;
            gvc2.DisplayMemberBinding = new Binding("ObjName");
            gvc2.Header = gvc2H;
            urGridView.Columns.Add(gvc2);

            GridViewColumn gvc3 = new GridViewColumn();
            GridViewColumnHeader gvc3H = new GridViewColumnHeader();
            gvc3H.FontSize = 11;
            gvc3H.Width = 180;
            gvc3H.Content = "Error";
            gvc3H.HorizontalContentAlignment = System.Windows.HorizontalAlignment.Left;
            gvc3.DisplayMemberBinding = new Binding("TheErr");
            gvc3.Header = gvc3H;
            urGridView.Columns.Add(gvc3);

            urListView.View = urGridView;

            urGrid.Children.Add(urListView);

            userItem.Content = urGrid;

            tabCtrl.Items.Add(userItem);
            userItem.IsSelected = true;

            // Now read a .csv file to add fake data
            try
            {
                string[] textTokens = new string[27];
                textTokens[0] = "Inbox";
                textTokens[1] = "Msg1";
                textTokens[2] = "UID is incorrect";
                textTokens[3] = "Calendar";
                textTokens[4] = "Appt";
                textTokens[5] = "Invalid attachment";
                textTokens[6] = "Calendar";
                textTokens[7] = "Meeting3";
                textTokens[8] = "Invalid recipient";
                textTokens[9] = "Contacts";
                textTokens[10] = "C100";
                textTokens[11] = "Bad email address";
                textTokens[12] = "Contacts";
                textTokens[13] = "C150";
                textTokens[14] = "Bad email address";
                textTokens[15] = "Inbox";
                textTokens[16] = "Foobar";
                textTokens[17] = "Invalid character";
                textTokens[18] = "Inbox";
                textTokens[19] = "Testmsg";
                textTokens[20] = "Attachment too large";
                textTokens[21] = "Tasks";
                textTokens[22] = "Devtask";
                textTokens[23] = "Date out of range";
                textTokens[24] = "Trash";
                textTokens[25] = "Msg444";
                textTokens[26] = "Invalid character";

                int iToken;

                switch (tabCtrl.Items.Count)
                {
                    case 2:
                        iToken = tabCtrl.Items.Count - 2;
                        for (int i = 0; i < 2; i++)
                        {
                            userResults.Add(new UserResults(textTokens[iToken++], textTokens[iToken++],textTokens[iToken++]));
                        }
                        break;

                    case 4:
                        iToken = tabCtrl.Items.Count + 2;
                        for (int i = 0; i < 3; i++)
                        {
                            userResults.Add(new UserResults(textTokens[iToken++], textTokens[iToken++], textTokens[iToken++]));
                        }
                        break;

                    case 5:
                        iToken = tabCtrl.Items.Count + 10;
                        for (int i = 0; i < 4; i++)
                        {
                            userResults.Add(new UserResults(textTokens[iToken++], textTokens[iToken++], textTokens[iToken++]));
                        }
                        break;
                }
            }
            catch (IOException ex)
            {
                MessageBox.Show(ex.Message, "ZimbraMigration", MessageBoxButton.OK, MessageBoxImage.Exclamation);
            }
        }

        protected void HandleGotFocus(object sender, EventArgs e)
        {
            TabItem ti = sender as TabItem;
            string hdr = ti.Header.ToString();
            ViewModel.SelectedTab = hdr;
            System.Windows.Visibility swv = (hdr == "Accounts") ? System.Windows.Visibility.Hidden : System.Windows.Visibility.Visible;
            pbMigrationS.Visibility = swv;
            labelSchedInfo.Visibility = swv;
        }

        private void CloseTab(object source, RoutedEventArgs args)
        {
            TabItem tabItem = args.OriginalSource as CloseableTabItem;
            if (tabItem != null)
            {
                TabControl tabControl = tabItem.Parent as TabControl;
                if (tabControl != null)
                {
                    tabControl.Items.Remove(tabItem);
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
