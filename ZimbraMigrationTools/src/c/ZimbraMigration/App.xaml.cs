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
            if (mw.UmUser != null)  // FBS bug 71047 -- 3/20/12
            {
                mw.UmUser.Uninit();
            }
            string s = mw.GlobalUninit();

	    if (s.Length > 0)
	    {
		bool retval = mw.AvoidInternalErrors(s);
		if (!retval)
		{
		    MessageBox.Show(s, "Shutdown error", MessageBoxButton.OK,
			MessageBoxImage.Error);
		}
	    }
        }
    }
}
}
