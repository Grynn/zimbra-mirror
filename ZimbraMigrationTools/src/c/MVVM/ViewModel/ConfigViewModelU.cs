using CssLib;
using MVVM.Model;
using Misc;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Collections;
using System.ComponentModel;
using System.Diagnostics;
using System.IO;
using System.Runtime.InteropServices;
using System.Text;
using System.Windows.Input;
using System.Windows;
using System;

namespace MVVM.ViewModel
{
public class ConfigViewModelU: BaseViewModel
{
    public ConfigViewModelU()
    {
        this.GetPSTCommand = new ActionCommand(this.GetPST, () => true);
        this.LoadCommand = new ActionCommand(this.Load, () => true);
        this.SaveCommand = new ActionCommand(this.Save, () => true);
        this.BackCommand = new ActionCommand(this.Back, () => true);
        this.NextCommand = new ActionCommand(this.Next, () => true);
        Isprofile = true;
        IspST = false;
        CSEnableNext = false;
    }
    public ICommand GetPSTCommand {
        get;
        private set;
    }
    private void GetPST()
    {
        Microsoft.Win32.OpenFileDialog pstDialog = new Microsoft.Win32.OpenFileDialog();
        pstDialog.Filter = "PST Files(*.pst)|*.pst";
        pstDialog.CheckFileExists = true;
        pstDialog.Multiselect = false;
        if (pstDialog.ShowDialog() == true)
        {
            string result = pstDialog.FileName;

            PSTFile = result;                   // update the UI
        }
    }
    public ICommand LoadCommand {
        get;
        private set;
    }
    public void LoadConfig(Config config)
    {
        if (config.SourceServer.UseProfile)
        {
            Isprofile = true;
            IspST = false;
            OutlookProfile = config.SourceServer.Profile;
            if (ProfileList.Count > 0)
            {
                CurrentProfileSelection = (OutlookProfile == null) ? 0 : ProfileList.IndexOf(
                    OutlookProfile);
            }
            else
            {
                ProfileList.Add(OutlookProfile);
            }
        }
        else
        {
            Isprofile = false;
            IspST = true;
            PSTFile = config.SourceServer.DataFile;
        }
    }

    private void Load()
    {
        System.Xml.Serialization.XmlSerializer reader =
            new System.Xml.Serialization.XmlSerializer(typeof (Config));

        Microsoft.Win32.OpenFileDialog fDialog = new Microsoft.Win32.OpenFileDialog();
        fDialog.Filter = "Config Files|*.xml";
        fDialog.CheckFileExists = true;
        fDialog.Multiselect = false;
        if (fDialog.ShowDialog() == true)
        {
            if (File.Exists(fDialog.FileName))
            {
                System.IO.StreamReader fileRead = new System.IO.StreamReader(fDialog.FileName);

                Config config = new Config();

                try
                {
                    config = (Config)reader.Deserialize(fileRead);
                }
                catch (Exception e)
                {
                    string temp = string.Format("Incorrect configuration file format.\n{0}", e.Message);
                    MessageBox.Show(temp, "Zimbra Migration", MessageBoxButton.OK, MessageBoxImage.Error);
                    fileRead.Close();
                    return;
                }

                fileRead.Close();
                try
                {
                    LoadConfig(config);
                }
                catch (Exception e)
                {
                    string temp = string.Format("Load error.\n{0}", e.Message);
                    MessageBox.Show(temp, "Zimbra Migration", MessageBoxButton.OK, MessageBoxImage.Error);;
                    return;
                }

                ((ConfigViewModelUDest)ViewModelPtrs[(int)ViewType.USRDEST]).LoadConfig(config);
                ((OptionsViewModel)ViewModelPtrs[(int)ViewType.OPTIONS]).LoadConfig(config);

                ((ScheduleViewModel)ViewModelPtrs[(int)ViewType.SCHED]).SetConfigFile(
                    fDialog.FileName);
            }
        }
    }
    public ICommand SaveCommand {
        get;
        private set;
    }

    private void Save()
    {
        Microsoft.Win32.SaveFileDialog fDialog = new Microsoft.Win32.SaveFileDialog();
        fDialog.Filter = "Config Files|*.xml";
        if (fDialog.ShowDialog() == true)
        {
            System.Xml.Serialization.XmlSerializer writer =
                new System.Xml.Serialization.XmlSerializer(typeof(Config));

            System.IO.StreamWriter file = new System.IO.StreamWriter(fDialog.FileName);
            PopulateConfig(isServer);
            writer.Serialize(file, m_config);
            file.Close();

            ((ScheduleViewModel)ViewModelPtrs[(int)ViewType.SCHED]).SetConfigFile(
                fDialog.FileName);
        }
    }
    public ICommand BackCommand {
        get;
        private set;
    }
    private void Back()
    {
        lb.SelectedIndex = 0;
    }
    public ICommand NextCommand {
        get;
        private set;
    }
    private bool IsProfile;
    public bool Isprofile {
        get { return IsProfile; }
        set
        {
            IsProfile = value;
            CSEnableNext = (IsProfile) ? ProfileList.Count > 0 : true;
            OnPropertyChanged(new PropertyChangedEventArgs("Isprofile"));
        }
    }
    private bool IsPST;
    public bool IspST {
        get { return IsPST; }
        set
        {
            IsPST = value;
            CSEnableNext = (IsPST) ? true : ProfileList.Count > 0;
            OnPropertyChanged(new PropertyChangedEventArgs("IspST"));
        }
    }
    private void Next()
    {
        if (IsPST)
        {
            if (PSTFile.Length == 0)
            {
                MessageBox.Show("Please enter a PST file", "Zimbra Migration", MessageBoxButton.OK, MessageBoxImage.Error);
                return;
            }
            else
            if (!File.Exists(PSTFile))
            {
                string temp = string.Format("{0} does not exist", PSTFile);
                MessageBox.Show(temp, "Zimbra Migration", MessageBoxButton.OK, MessageBoxImage.Error);
                return;
            }
        }

        lb.SelectedIndex = 2;
    }
    public string OutlookProfile {
        get { return m_config.SourceServer.Profile; }
        set
        {
            if (value == m_config.SourceServer.Profile)
                return;
            m_config.SourceServer.Profile = value;
            // m_config.mailServer.ProfileName= value;
            CSEnableNext = true;
            OnPropertyChanged(new PropertyChangedEventArgs("OutlookProfile"));
        }
    }
    private ObservableCollection<string> profilelist = new ObservableCollection<string>();
    public ObservableCollection<string> ProfileList {
        get { return profilelist; }
        set
        {
            profilelist = value;
        }
    }
    public int CurrentProfileSelection {
        get { return profileselection; }
        set
        {
            profileselection = value;

            OnPropertyChanged(new PropertyChangedEventArgs("CurrentProfileSelection"));
        }
    }
    private int profileselection;
    public string PSTFile {
        get { return m_config.SourceServer.DataFile; }
        set
        {
            if (value == m_config.SourceServer.DataFile)
                return;
            m_config.SourceServer.DataFile = value;
            OnPropertyChanged(new PropertyChangedEventArgs("PSTFile"));
        }
    }
    private bool csenableNext;
    public bool CSEnableNext {
        get { return csenableNext; }
        set
        {
            csenableNext = value;
            OnPropertyChanged(new PropertyChangedEventArgs("CSEnableNext"));
        }
    }
}
}
