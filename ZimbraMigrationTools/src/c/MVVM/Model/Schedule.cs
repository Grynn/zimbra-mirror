namespace MVVM.Model
{
using System;

public class Schedule
{
    internal Schedule(bool EnableMigrate)
    {
        this.EnableMigrate = EnableMigrate;
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
