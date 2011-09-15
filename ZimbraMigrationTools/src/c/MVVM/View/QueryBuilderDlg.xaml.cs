using System.Windows; 
using System.Windows.Controls; // Validation
using System.Windows.Input; // Keyboard

namespace MVVM.ViewModel
{
    public partial class QueryBuilderDlg : Window
    {
        public QueryBuilderDlg()
        {
            InitializeComponent();
        }

        void okButton_Click(object sender, RoutedEventArgs e)
        {
            this.DialogResult = true;
        }

        void cancelButton_Click(object sender, RoutedEventArgs e)
        {
            this.DialogResult = false;
        }
    }
}