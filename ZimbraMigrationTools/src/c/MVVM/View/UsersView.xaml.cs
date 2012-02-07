using System.Collections.Generic;
using System.DirectoryServices;
using System.Windows.Controls;
using System.Windows.Input;
using System.Windows.Media.Imaging;
using System.Windows;
using System;
using MVVM.ViewModel;

namespace MVVM.View
{
public partial class UsersView
{
    TreeViewItem tviTop;
    String selectedPath;
    string[] accts;     // array of display names and usernames (they alternate) 
     
    public UsersView()
    {
        InitializeComponent();
    }

    private UsersViewModel uvm
    {
        get { return DataContext as UsersViewModel; }
    }

    private void InitializeTV()
    {
        Label l;
        StackPanel sp;
        TreeViewItem tvi;
        DirectoryEntry de = new DirectoryEntry();
        String top = de.Name.Substring(3);      // remove DC=

        tviTop = new TreeViewItem();
        tviTop.IsExpanded = true;

        Image imageTopLevel = new Image();

        imageTopLevel.Source = new BitmapImage(new Uri(
            @"/MVVM;component/ViewModel/images/computer.ico", UriKind.Relative));
        imageTopLevel.Height = 12;
        l = new Label();
        l.Content = top;
        sp = new StackPanel();
        sp.Orientation = Orientation.Horizontal;
        sp.Children.Add(imageTopLevel);
        sp.Children.Add(l);
        tviTop.Header = sp;
        QBTreeView.Items.Clear();
        QBTreeView.Items.Add(tviTop);

        DirectoryEntries children = de.Children;

        foreach (DirectoryEntry childEntry in children)
        {
            String schemaClass = childEntry.SchemaClassName;

            if ((schemaClass == "container") || (schemaClass == "organizationalUnit"))
            {
                tvi = new TreeViewItem();
                l = new Label();
                l.Content = childEntry.Name.Substring(3);
                sp = new StackPanel();
                sp.Orientation = Orientation.Horizontal;

                Image image = new Image();

                image.Source = new BitmapImage(new Uri(
                    @"/MVVM;component/ViewModel/images/button.ico", UriKind.Relative));
                image.Height = 18;
                sp.Children.Add(image);
                sp.Children.Add(l);
                tvi.Header = sp;
                tvi.Tag = (String)childEntry.Path;
                tvi.Margin = new Thickness(-20, -4, -4, -4);
                tviTop.Items.Add(tvi);
            }
        }
    }

        private int GetAcctIdx(string acct)
        {
            int i;

            for (i = 0; i < accts.Length; i += 2)
            {
                if (accts[i] == acct)
                    return i + 1;
            }
            return -1;
        }

        void LDB_Query(object sender, RoutedEventArgs e)
        {
            lbQBUsers.Items.Clear();

            DirectorySearcher ds = new DirectorySearcher();

            ds.SearchRoot = new DirectoryEntry(selectedPath);       // start searching from whatever was selectted
            ds.Filter = (tbFilter.Text.Length > 0) ? String.Format(
                "(|(&(objectCategory=user)(name={0})))", tbFilter.Text) :
                "(|(&(objectCategory=user)(name=*)))";

            ds.PropertiesToLoad.Add("sAMAccountName");
            if (cbEntireSubt.IsChecked == false)
                ds.SearchScope = SearchScope.OneLevel;
            SearchResultCollection src = ds.FindAll();

            try
            {
                int arraySiz = (src.Count) * 2;

                accts = new string[arraySiz];

                int k = 0;

                foreach (SearchResult sr in src)
                {
                    DirectoryEntry de = sr.GetDirectoryEntry();

                    lbQBUsers.Items.Add(de.Name.Substring(3));
                    foreach (String property in ds.PropertiesToLoad)
                    {
                        foreach (Object myCollection in sr.Properties[property])
                        {
                            if (property == "sAMAccountName")
                            {
                                accts[k++] = de.Name.Substring(3);
                                accts[k++] = myCollection.ToString();
                                break;
                            }
                        }
                    }
                }
            }
            catch (Exception ex)
            {
                MessageBox.Show(ex.Message);
            }
            src.Dispose();
            ds.Dispose();
        }

        private void LDB_Click(object sender, RoutedEventArgs e)
        {
            LDB.Visibility = Visibility.Visible;
            UsersGrid.Visibility = Visibility.Collapsed;
            InitializeTV();
        }

        private void LDB_OK(object sender, RoutedEventArgs e)
        {
            int idx = 0;

            // There is no selected indices array like there is in Windows Forms.
            // Probably a better way to do this, but for now, just go through the acct array
            if (lbQBUsers.SelectedItems.Count == 0)
            {
                foreach (String item in lbQBUsers.Items)
                {
                    uvm.UsersList.Add(new UsersViewModel(item, accts[idx]));
                    idx += 2;
                }
            }
            else
            {
                foreach (String item in lbQBUsers.SelectedItems)
                {
                    idx = GetAcctIdx(item);
                    if (idx != -1)                  // it should always be something, but just in case ...
                        uvm.UsersList.Add(new UsersViewModel(item, accts[idx]));
                }
            }
            uvm.EnableNext = (uvm.UsersList.Count > 0);
            uvm.svm.EnableMigrate = (uvm.svm.SchedList.Count > 0);
            LDB.Visibility = Visibility.Collapsed;
            UsersGrid.Visibility = Visibility.Visible;
        }

    private void LDB_Cancel(object sender, RoutedEventArgs e)
    {
        LDB.Visibility = Visibility.Collapsed;
        UsersGrid.Visibility = Visibility.Visible;
    }
 
    void HandleTreeViewItemSelected(object sender, RoutedEventArgs e)
    {
        // When the TreeViewItem was created, we stored the path in the tag
        // This will set the class member variable, and we can use it when creating the de
        TreeViewItem item = e.OriginalSource as TreeViewItem;
        String tag = (String)item.Tag;

        selectedPath = (tag != null) ? tag : "";
    }

}
}
