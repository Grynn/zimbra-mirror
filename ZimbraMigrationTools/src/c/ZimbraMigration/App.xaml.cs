using CssLib;
using System;
using System.Windows;

namespace ZimbraMigration
{
public partial class App: Application
{
    private void Application_Exit(object sender, ExitEventArgs e)
    {
        CSMigrationWrapper mw = (CSMigrationWrapper)Properties["mw"];

        if (mw != null)
        {
            string s = mw.GlobalUninit();

            if (s.Length > 0)
                MessageBox.Show(s, "Shutdown error", MessageBoxButton.OK,
                    MessageBoxImage.Error);
        }
    }
}
}
