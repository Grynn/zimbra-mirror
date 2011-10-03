namespace Misc
{
using System.Windows.Data;
using System;

public class SchedUser
{
    public string username {
        get;
        set;
    }
    public bool isProvisioned {
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
