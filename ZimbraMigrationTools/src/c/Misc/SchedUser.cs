namespace Misc
{
    using System;
    using System.Windows.Data;

    public class SchedUser
    {
        public string username
        {
            get;
            set;
        }

        public bool isProvisioned
        {
            get;
            set;
        }

        public SchedUser(string uname, bool provisioned)
        {
            username = uname;
            isProvisioned = provisioned;
        }
    }
}
