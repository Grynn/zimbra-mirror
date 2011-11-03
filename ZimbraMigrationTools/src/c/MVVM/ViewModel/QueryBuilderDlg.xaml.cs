using System.Collections.Generic;
using System.DirectoryServices;
using System.Windows.Controls;
using System.Windows.Input;
using System.Windows.Media.Imaging;
using System.Windows;
using System;

namespace MVVM.ViewModel
{
public partial class QueryBuilderDlg: Window
{
    UsersViewModel uvm;
    TreeViewItem tviTop;
    String selectedPath;

    string[] accts;                             // array of display names and usernames (they alternate)

    public QueryBuilderDlg(UsersViewModel theModel)
    {
        uvm = theModel;
        selectedPath = "";
        InitializeComponent();
        InitializeTV();
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

    void queryButton_Click(object sender, RoutedEventArgs e)
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

    void okButton_Click(object sender, RoutedEventArgs e)
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
        this.DialogResult = true;
    }

    void cancelButton_Click(object sender, RoutedEventArgs e)
    {
        this.DialogResult = false;
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
