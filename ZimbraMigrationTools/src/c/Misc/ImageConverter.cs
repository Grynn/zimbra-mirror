namespace Misc
{
using System.Windows.Data;
using System;

public class ImageConverter: IValueConverter
{
    public object Convert(object value, Type targetType, object parameter,
            System.Globalization.CultureInfo culture)
    {
        SchedUser su = (SchedUser)value;

        if (su.isProvisioned)
            return "/MVVM;component/View/Images/userp.ico";
        else
            return "/MVVM;component/View/Images/usernp.ico";
    }
    public object ConvertBack(object value, Type targetType, object parameter,
            System.Globalization.CultureInfo culture)
    {
        throw new NotSupportedException();
    }
}
}
