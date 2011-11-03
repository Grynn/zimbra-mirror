namespace MVVM.Model
{
using System;

public class Base
{
    internal Base(string name, string title)
    {
        this.Name = name;
        this.Title = title;
    }
    public string Name {
        get;
        set;
    }
    public string Title {
        get;
        set;
    }
}
}
