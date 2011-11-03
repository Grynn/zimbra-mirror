using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text;
using System.Xml.Linq;
using System.Xml;

namespace CssLib
{
public class ZimbraMessage
{
    public string filePath;
    public string folderId;
    public string flags;
    public string tags;
    public string rcvdDate;

    public ZimbraMessage()
    {
        folderId = "";
        flags = "";
        tags = "";
        rcvdDate = "";
    }

    public ZimbraMessage(string FilePath, string FolderId, string Flags, string Tags, string
        RcvdDate)
    {
        filePath = FilePath;
        folderId = FolderId;
        flags = Flags;
        tags = Tags;
        rcvdDate = RcvdDate;
    }
}

public class ZimbraFolder
{
    public string name;
    public string parent;
    public string view;
    public string color;
    public string flags;

    public ZimbraFolder()
    {
        name = "";
        parent = "";
        view = "";
        color = "";
        flags = "";
    }

    public ZimbraFolder(string Name, string Parent, string View, string Color, string Flags)
    {
        name = Name;
        parent = Parent;
        view = View;
        color = Color;
        flags = Flags;
    }
}
}
