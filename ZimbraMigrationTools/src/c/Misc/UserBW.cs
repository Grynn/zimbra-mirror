namespace Misc
{
using System.Windows.Data;
using System.ComponentModel;
using System;

public class UserBW : BackgroundWorker
{
    public int usernum {
        get;
        set;
    }
    public int threadnum {
        get;
        set;
    }
    public UserBW(int tnum)
    {
        usernum = -1;
        threadnum = tnum;
    }
}
}
