using System.Windows;

namespace MVVM.View
{
public partial class OptionsView
{
    public OptionsView()
    {
        InitializeComponent();
        string sdp = (string)Application.Current.Properties["sdp"];
        if (sdp.StartsWith("d"))
        {
            datePickerItems.Visibility = System.Windows.Visibility.Hidden;
            Datebox.Visibility = System.Windows.Visibility.Visible;
            DateboxLbl.Visibility = System.Windows.Visibility.Visible;
        }
    }

}
}
