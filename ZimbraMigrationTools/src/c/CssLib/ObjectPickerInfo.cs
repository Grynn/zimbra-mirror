namespace CssLib
{
public class ObjectPickerInfo
{
    public string DisplayName
    {
        get;
        set;
    }
    public string GivenName{
        get;
        set;
    }
    public string Sn{
        get;
        set;
    }
    public string Zfp{
        get;
        set;
    }
    public ObjectPickerInfo(string displayname, string givenname, string sn, string zfp)
    {
        DisplayName = displayname;
        GivenName = givenname;
	Sn = sn;
        Zfp = zfp;
    }
}
}
