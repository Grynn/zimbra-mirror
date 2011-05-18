using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace MVVM.Model
{
    public class ImportOptions
    {
        private bool m_Mail;

        public bool Mail
        {
            get { return m_Mail; }
            set { m_Mail = value; }
        }
        private bool m_Contacts;

        public bool Contacts
        {
            get { return m_Contacts; }
            set { m_Contacts = value; }
        }
        private bool m_Calendar;

        public bool Calendar
        {
            get { return m_Calendar; }
            set { m_Calendar = value; }
        }
        private bool m_Tasks;

        public bool Tasks
        {
            get { return m_Tasks; }
            set { m_Tasks = value; }
        }


        private bool m_DeletedItems;


        public bool DeletedItems
        {
            get { return m_DeletedItems; }
            set { m_DeletedItems = value; }
        }
        private bool m_Junk;

        public bool Junk
        {
            get { return m_Junk; }
            set { m_Junk = value; }
        }

        private bool m_Sent;

        public bool Sent
        {
            get { return m_Sent; }
            set { m_Sent = value; }
        }

        private bool m_Rules;

        public bool Rules
        {
            get { return m_Rules; }
            set { m_Rules = value; }
        }

        private string m_NextButtonContent;

        public string NextButtonContent
        {
            get { return m_NextButtonContent; }
            set { m_NextButtonContent = value; }
        }


    }
}
