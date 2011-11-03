using MVVM.ViewModel;
using System.Windows;

namespace MVVM.View
{
public partial class ConfigViewUDest
{
    public ConfigViewUDest()
    {
        InitializeComponent();
    }

    // Kind of a drag that we have to put these next 2 methods in here, but PasswordBox is not a dependency property,
    // so we can't bind to the model.  Doing this for now -- should probably use an attached property later
    private ConfigViewModelUDest ViewModel {
        get { return DataContext as ConfigViewModelUDest; }
    }
    private void pb_PasswordChanged(object sender, RoutedEventArgs e)
    {
        ViewModel.ZimbraUserPasswd = passwordBox1.Password;
    }
}
}
