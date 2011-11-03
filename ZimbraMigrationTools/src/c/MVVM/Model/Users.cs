namespace MVVM.Model
{
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System;

public class Users
{
    public Users() {}
    internal Users(string username, string mappedname, int currentUserSelection)
    {
        this.Username = username;
        this.MappedName = mappedname;
        this.CurrentUserSelection = currentUserSelection;
    }
    public string Username {
        get;
        set;
    }
    public string mappedName {
        get;
        set;
    }
    public int CurrentUserSelection {
        get;
        set;
    }
    public bool MinusEnabled {
        get;
        set;
    }
    public bool IsProvisioned {
        get;
        set;
    }
    public bool EnableNext {
        get;
        set;
    }
    public static string ToCsv<T>(string separator, IEnumerable<T> objectlist)
    {
        Type t = typeof (T);

        System.Reflection.FieldInfo[] fields = t.GetFields();

        string header = "#" + String.Join(separator + "#", fields.Select(
            f => f.Name).ToArray());
        StringBuilder csvdata = new StringBuilder();

        csvdata.AppendLine(header);
        foreach (var o in objectlist)
            csvdata.AppendLine(ToCsvFields(separator, fields, o));
        return csvdata.ToString();
    }

    public static string ToCsvFields(string separator, System.Reflection.FieldInfo[] fields,
        object o)
    {
        StringBuilder linie = new StringBuilder();

        foreach (var f in fields)
        {
            if (linie.Length > 0)
                linie.Append(separator);

            var x = f.GetValue(o);

            if (x != null)
                linie.Append(x.ToString());
        }
        return linie.ToString();
    }

    public string UserName;
    public string MappedName;
    public bool ChangePWD;
    public string PWDdefault;
    private string statusMessage;
    public string StatusMessage {
        get { return statusMessage; }
        set { statusMessage = value; }
    }
}
}
