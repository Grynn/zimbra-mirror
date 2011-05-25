namespace Misc
{
    using System;
    using System.Windows.Data;

    public class ForegroundConverter : IValueConverter
    {
        public object Convert(object value, Type targetType, object parameter,
            System.Globalization.CultureInfo culture)
        {
            string s = value.ToString();
            if ((s.StartsWith("F") || s.StartsWith("B") || s.StartsWith("T") || s.StartsWith("C")))
            {
                return "Red";
            }
            else
            {
                return "Black";
            }
        }

        public object ConvertBack(object value, Type targetType, object parameter,
            System.Globalization.CultureInfo culture)
        {
            throw new NotSupportedException();
        }
    }
}
