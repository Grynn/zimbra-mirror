namespace CssLib
{
public class CosInfo
{
    public string CosName {
        get;
        set;
    }
    public string CosID {
        get;
        set;
    }
    public CosInfo(string cosname, string cosid)
    {
        CosName = cosname;
        CosID = cosid;
    }
}
}
