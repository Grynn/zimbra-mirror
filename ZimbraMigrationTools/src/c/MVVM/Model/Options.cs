using System.Collections.Generic;
using System.Linq;
using System.Text;
using System;

namespace MVVM.Model
{
public class ImportOptions
{
    private bool m_Mail;
    public bool Mail {
        get { return m_Mail; }
        set { m_Mail = value; }
    }
    private bool m_Contacts;
    public bool Contacts {
        get { return m_Contacts; }
        set { m_Contacts = value; }
    }
    private bool m_Calendar;
    public bool Calendar {
        get { return m_Calendar; }
        set { m_Calendar = value; }
    }
    private bool m_Tasks;
    public bool Tasks {
        get { return m_Tasks; }
        set { m_Tasks = value; }
    }
    private bool m_DeletedItems;
    public bool DeletedItems {
        get { return m_DeletedItems; }
        set { m_DeletedItems = value; }
    }
    private bool m_Junk;
    public bool Junk {
        get { return m_Junk; }
        set { m_Junk = value; }
    }
    private bool m_Sent;
    public bool Sent {
        get { return m_Sent; }
        set { m_Sent = value; }
    }
    private bool m_Rules;
    public bool Rules {
        get { return m_Rules; }
        set { m_Rules = value; }
    }
    private string m_NextButtonContent;
    public string NextButtonContent {
        get { return m_NextButtonContent; }
        set { m_NextButtonContent = value; }
    }
}

public class AdvancedImportOptions
{
    private DateTime m_MigrateONRAfter;
    public DateTime MigrateONRAfter {
        get { return m_MigrateONRAfter; }
        set { m_MigrateONRAfter = value; }
    }
    private string m_MaxAttachementSize;
    public string MaxAttachementSize {
        get { return m_MaxAttachementSize; }
        set { m_MaxAttachementSize = value; }
    }
    public Folder[] FoldersToSkip;
}

public class Folder
{
    private string m_FolderName;
    public string FolderName {
        get { return m_FolderName; }
        set { m_FolderName = value; }
    }
    /*  public List<string> FoldersToSkip
     * {
     *    get { return m_FoldersToSkip; }
     *    set { m_FoldersToSkip = value; }
     * }
     *
     */
}
}
