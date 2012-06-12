namespace CssLib
{
public class TagInfo
{
    public string TagName {
        get;
        set;
    }
    public string TagID {
        get;
        set;
    }
    public TagInfo(string tagname, string tagid)
    {
        TagName = tagname;
        TagID = tagid;
    }
}
}
