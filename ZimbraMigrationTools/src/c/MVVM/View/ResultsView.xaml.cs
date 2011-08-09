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

            // set up the grid's rows
            RowDefinition rowDef1 = new RowDefinition();
            RowDefinition rowDef2 = new RowDefinition();
            rowDef1.MaxHeight = 250;
            rowDef2.Height = GridLength.Auto;
            urGrid.RowDefinitions.Add(rowDef1);
            urGrid.RowDefinitions.Add(rowDef2);
            //

            // Set up the ListView
            ListView urListView = new ListView();
            urListView.FontSize = 11;
            urListView.SetValue(Grid.RowProperty, 0);
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
            gvc2H.Width = 200;
            gvc2H.Content = "Type";
            gvc2H.HorizontalContentAlignment = System.Windows.HorizontalAlignment.Left;
            gvc2.DisplayMemberBinding = new Binding("TypeName");
            gvc2.Header = gvc2H;
            urGridView.Columns.Add(gvc2);

            GridViewColumn gvc3 = new GridViewColumn();
            GridViewColumnHeader gvc3H = new GridViewColumnHeader();
            gvc3H.FontSize = 11;
            gvc3H.Width = 120;
            gvc3H.Content = "Progress";
            gvc3H.HorizontalContentAlignment = System.Windows.HorizontalAlignment.Left;
            gvc3.DisplayMemberBinding = new Binding("UserProgressMsg");
            gvc3.Header = gvc3H;
            urGridView.Columns.Add(gvc3);

            urListView.View = urGridView;

            urGrid.Children.Add(urListView);
            //

            // now create Listbox for errors
            ListBox lbErrors = new ListBox();
            lbErrors.FontSize = 11;
            lbErrors.SetValue(Grid.RowProperty, 1);
            lbErrors.Margin = new Thickness(5, 5, 5, 5);
            lbErrors.MinHeight = 120;
            lbErrors.MaxHeight = 120;
            lbErrors.MinWidth = 450;
            lbErrors.HorizontalAlignment = System.Windows.HorizontalAlignment.Stretch;
            lbErrors.VerticalAlignment = System.Windows.VerticalAlignment.Top;
            urGrid.Children.Add(lbErrors);
            //

            userItem.Content = urGrid;

            tabCtrl.Items.Add(userItem);
            userItem.IsSelected = true;

            // Now read a .csv file to add fake data
            try
            {
                string[] textTokens = new string[40];
                textTokens[0] = "Inbox";
                textTokens[1] = "Message";
                textTokens[2] = "8 of 95";
                textTokens[3] = "Calendar";
                textTokens[4] = "Calendar";
                textTokens[5] = "3 of 28";
                textTokens[6] = "OtherCal";
                textTokens[7] = "Calendar";
                textTokens[8] = "1 of 5";
                textTokens[9] = "Contacts";
                textTokens[10] = "Contacts";
                textTokens[11] = "22 of 185";
                textTokens[12] = "Tasks";
                textTokens[13] = "Task";
                textTokens[14] = "4 of 8";
                textTokens[15] = "MyTasks";
                textTokens[16] = "Task";
                textTokens[17] = "5 of 23";
                textTokens[18] = "Folder1";
                textTokens[19] = "Messages";
                textTokens[20] = "13 of 117";
                textTokens[21] = "Folder2";
                textTokens[22] = "Messages";
                textTokens[23] = "24 of 128";
                textTokens[24] = "Trash";
                textTokens[25] = "Trash";
                textTokens[26] = "14 of 88";
                textTokens[27] = "Inbox";
                textTokens[28] = "Messages";
                textTokens[29] = "103 of 267";
                textTokens[30] = "Mybox";
                textTokens[31] = "Messages";
                textTokens[32] = "32 of 75";
                textTokens[33] = "Error: Subj: Message4 - Invalid UID";
                textTokens[34] = "Error: Subj: TestMessage - Invalid attachment";
                textTokens[35] = "Error: Subj: StatusMeeting - Invalid recipient";
                textTokens[36] = "Error: Name: BugzillaRule - Unsupported condition";
                textTokens[37] = "Warning: Subj: MyTask - Date is in the past";
                textTokens[38] = "Error: Name: Address has an unsupported format";
                textTokens[39] = "Error: Subj: YetAnotherMsg - Attachment too large";

                int iToken;

                int sel = ViewModel.CurrentAccountSelection;
                ViewModel.PBValue = ViewModel.AccountResultsList[sel].PBValue;
                ViewModel.UserPBMsgValue = ViewModel.AccountResultsList[sel].PBMsgValue;

                switch (tabCtrl.Items.Count)
                {
                    case 2:
                        iToken = tabCtrl.Items.Count - 2;
                        for (int i = 0; i < 2; i++)
                        {
                            UserResults ur = new UserResults(textTokens[iToken++], textTokens[iToken++],textTokens[iToken++]);
                            userResults.Add(ur);
                            if (i == 0)
                            {
                                ListBoxItem item = new ListBoxItem();   // hack for now -- will do it right with binding later
                                item.Content = textTokens[33];
                                lbErrors.Items.Add(item);
                            }
                            if (i == 1)
                            {
                                ListBoxItem item = new ListBoxItem();   // hack for now -- will do it right with binding later
                                item.Content = textTokens[34];
                                lbErrors.Items.Add(item);
                            }
                        }
                        break;

                    case 3:
                        iToken = 27;
                        for (int i = 0; i < 2; i++)
                        {
                            UserResults ur = new UserResults(textTokens[iToken++], textTokens[iToken++], textTokens[iToken++]);
                            userResults.Add(ur);
                            if (i == 1)
                            {
                                ListBoxItem item = new ListBoxItem();   // hack for now -- will do it right with binding later
                                item.Content = textTokens[35];
                                lbErrors.Items.Add(item);
                            }
                        }
                        break;

                    case 4:
                        iToken = tabCtrl.Items.Count + 2;
                        for (int i = 0; i < 3; i++)
                        {
                            UserResults ur = new UserResults(textTokens[iToken++], textTokens[iToken++], textTokens[iToken++]);
                            userResults.Add(ur);
                        }
                        break;

                    case 5:
                        iToken = tabCtrl.Items.Count + 10;
                        for (int i = 0; i < 4; i++)
                        {
                            UserResults ur = new UserResults(textTokens[iToken++], textTokens[iToken++], textTokens[iToken++]);
                            userResults.Add(ur);
                            ListBoxItem item = new ListBoxItem();   // hack for now -- will do it right with binding later
                            item.Content = textTokens[i+36];
                            lbErrors.Items.Add(item);
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

            int accountnum = -1;
            if (hdr != "Accounts")
            {
                for (int i = 0; i < ViewModel.AccountResultsList.Count; i++)
                {
                    if (hdr == ViewModel.AccountResultsList[i].AccountName)
                    {
                        accountnum = ViewModel.AccountResultsList[i].GetAccountNum();
                        break;
                    }
                }
            }
            if (accountnum != -1)
            {
                ViewModel.PBValue = ViewModel.AccountResultsList[accountnum].PBValue;
                ViewModel.UserPBMsgValue = ViewModel.AccountResultsList[accountnum].PBMsgValue;
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
