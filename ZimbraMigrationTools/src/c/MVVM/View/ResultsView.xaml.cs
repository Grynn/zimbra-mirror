using CssLib;
using MVVM.Model;
using Misc;
using MVVM.View.CTI;
using MVVM.ViewModel;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Collections;
using System.IO;
using System.Windows.Controls;
using System.Windows.Data;
using System.Windows.Input;
using System.Windows.Markup;
using System.Windows.Media;
using System.Windows;
using System;

namespace MVVM.View
{
public partial class ResultsView
{
    ListView[] urListView = new ListView[16];
    ListBox[] lbErrors = new ListBox[16];

    ProgressBar userProgressBar = null;
    Label userStatusMsg = null;

    int iTabCount = 0;

    public ResultsView()
    {
        InitializeComponent();
        this.AddHandler(CloseableTabItem.CloseTabEvent, new RoutedEventHandler(this.CloseTab));
    }
    private AccountResultsViewModel ViewModel {
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
            MessageBox.Show(string.Format("Only 16 tabs may be open at a time",
                MessageBoxButton.OK, MessageBoxImage.Error));
            return;
        }

        ListViewItem lvi = sender as ListViewItem;
        var content = lvi.Content as AccountResultsViewModel;
        TabControl tabCtrl = FindParent(lvi, typeof (TabControl)) as TabControl;
        CloseableTabItem userItem = new CloseableTabItem();

        userItem.Header = content.AccountName;

        // get accountnum so we can keep the listboxes and listviews straight
        int accountnum = GetAcctNum((string)userItem.Header);
        AccountResultsViewModel ar = ViewModel.AccountResultsList[accountnum];

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
        RowDefinition rowDef3 = new RowDefinition();
        RowDefinition rowDef4 = new RowDefinition();

        rowDef1.MaxHeight = 145;
        rowDef2.MaxHeight = 145;
        rowDef3.Height = GridLength.Auto;
        rowDef4.Height = GridLength.Auto;
        urGrid.Height = 330;                    // so we'll get  Vertical scrollviewer
        urGrid.RowDefinitions.Add(rowDef1);
        urGrid.RowDefinitions.Add(rowDef2);
        urGrid.RowDefinitions.Add(rowDef3);
        urGrid.RowDefinitions.Add(rowDef4);
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

        // Now set up the progressbar and message status in another grid
        userProgressBar = new ProgressBar();
        userProgressBar.SetValue(Grid.RowProperty, 2);
        userProgressBar.SetValue(Grid.ColumnProperty, 0);
        userProgressBar.SetValue(Grid.ColumnSpanProperty, 2);
        userProgressBar.IsIndeterminate = false;
        userProgressBar.Orientation = Orientation.Horizontal;
        userProgressBar.Width = 412;
        userProgressBar.Height = 18;
        userProgressBar.Margin = new Thickness(36, 0, 0, 0);
        userProgressBar.HorizontalAlignment = System.Windows.HorizontalAlignment.Left;
        userProgressBar.Foreground = Brushes.DodgerBlue;    // Get rid of the green

        // Here's how to set Brushes.DodgerBlue with Color values
        //SolidColorBrush mySolidColorBrush = new SolidColorBrush();
        //mySolidColorBrush.Color = Color.FromArgb(255, 30, 144, 255);    // #FF1E90FF
        //userProgressBar.Foreground = mySolidColorBrush;
        //

        Binding upbBinding = new Binding("PBValue");
        upbBinding.Source = ar;
        userProgressBar.SetBinding(ProgressBar.ValueProperty, upbBinding);
        urGrid.Children.Add(userProgressBar);

        userStatusMsg = new Label();
        userStatusMsg.Visibility = System.Windows.Visibility.Visible;
        userStatusMsg.SetValue(Grid.RowProperty, 3);
        userStatusMsg.SetValue(Grid.ColumnProperty, 0);
        userStatusMsg.SetValue(Grid.ColumnSpanProperty, 2);
        userStatusMsg.MinWidth = 300;
        userStatusMsg.Margin = new Thickness(70, 0, 0, 0);
        userStatusMsg.HorizontalAlignment = System.Windows.HorizontalAlignment.Left;
        userStatusMsg.FontStyle = FontStyles.Italic;
        Binding usmBinding = new Binding("PBMsgValue");
        usmBinding.Source = ar;
        userStatusMsg.SetBinding(Label.ContentProperty, usmBinding);
        urGrid.Children.Add(userStatusMsg);
        //////////////

        userItem.Content = urGrid;

        tabCtrl.Items.Add(userItem);
        userItem.IsSelected = true;

        Binding binding = new Binding();

        // wrap in NotifyCollectionChangedWrapper so we can update collection from a different thread
        binding.Source = new NotifyCollectionChangedWrapper<UserResultsViewModel>(ar.UserResultsList);
        //

        BindingOperations.SetBinding(urListView[accountnum], ListView.ItemsSourceProperty,
            binding);

    }

    protected void HandleGotFocus(object sender, EventArgs e)
    {
        TabItem ti = sender as TabItem;
        string hdr = ti.Header.ToString();

        ViewModel.SelectedTab = hdr;

        // Need to figure this out
        int accountnum = -1;
        if (hdr != "Accounts")
            accountnum = GetAcctNum(hdr);
        if (accountnum != -1)
        {
            AccountResultsViewModel ar = ViewModel.AccountResultsList[accountnum];
            lbErrors[accountnum].Items.Clear();
            for (int i = 0; i < ar.AccountProblemsList.Count; i++)
            {
                ProblemInfo problemInfo = ar.AccountProblemsList[i];

                if (problemInfo != null)
                {
                    ListBoxItem item = new ListBoxItem();       // hack for now -- will do it right with binding later

                    item.Content = problemInfo.FormattedMsg;
                    lbErrors[accountnum].Items.Add(item);
                }
            }
        }
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
