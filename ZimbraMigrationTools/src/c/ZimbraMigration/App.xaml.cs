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
        string mode = Properties["migrationmode"].ToString();
        CSMigrationwrapper mw = (CSMigrationwrapper)Properties["mw"];

        if (mw != null)
        {
            mw.MailClient = "MAPI";
            if (mode == "server")
            {
                string s = mw.UninitializeMailClient();

                if (s.Length > 0)
                    MessageBox.Show(s, "Shutdown error", MessageBoxButton.OK,
                        MessageBoxImage.Error);
            }
            else
            {
                mw.EndUserMigration();
            }
        }
    }
}
}
