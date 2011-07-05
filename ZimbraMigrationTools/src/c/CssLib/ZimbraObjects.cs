using System.Collections.Generic;
using System.Xml.Linq;
using System.Linq;
using System.Text;
using System.Xml;
using System.IO;

namespace CssLib
{
    public class ZimbraContact
    {
        public string firstName;
        public string lastName;
        public string company;
        public string workPhone;
        public string jobTitle;
        public string fileAs;
        public string email;

        public ZimbraContact()
        {
            firstName = "";
            lastName = "";
            company = "";
            workPhone = "";
            jobTitle = "";
            fileAs = "";
            email = "";
        }
    }
}

