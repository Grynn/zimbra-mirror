namespace MVVM.Model
{
using System;

public class Schedule
{
    internal Schedule(int pbValue, string pbMsgValue, bool EnableMigrate)
    {
        this.PBValue = pbValue;
        this.PBMsgValue = pbMsgValue;
        this.EnableMigrate = EnableMigrate;
    }
    public int PBValue {
        get;
        set;
    }
    public string PBMsgValue {
        get;
        set;
    }
    public bool EnableMigrate {
        get;
        set;
    }
    public DateTime ScheduleDate {
        get;
        set;
    }
    public int HrSelection {
        get;
        set;
    }
    public int MinSelection {
        get;
        set;
    }
    public int AMPMSelection {
        get;
        set;
    }
}
}
