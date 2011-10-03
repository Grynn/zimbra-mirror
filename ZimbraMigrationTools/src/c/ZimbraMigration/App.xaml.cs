using CssLib;
using System.Collections.Generic;
using System.Configuration;
using System.Data;
using System.Linq;
using System.Windows;
using System;

namespace ZimbraMigration
{
// / <summary>
// / Interaction logic for App.xaml
// / </summary>
public partial class App: Application
{
    private void Application_Exit(object sender, ExitEventArgs e)
    {
        CSMigrationwrapper mw = new CSMigrationwrapper();

        mw.MailClient = "MAPI";
        string s = mw.UninitializeMailClient();
        if (s.Length > 0)
            MessageBox.Show(s, "Shutdown error", MessageBoxButton.OK, MessageBoxImage.Error);
    }
}
}
