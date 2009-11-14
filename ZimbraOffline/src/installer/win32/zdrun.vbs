
' * ***** BEGIN LICENSE BLOCK *****
' * 
' * Zimbra Desktop
' * Copyright (C) 2009 Zimbra, Inc.
' * 
' * The contents of this file are subject to the Yahoo! Public License
' * Version 1.0 ("License"); you may not use this file except in
' * compliance with the License.  You may obtain a copy of the License at
' * http://www.zimbra.com/license.
' * 
' * Software distributed under the License is distributed on an "AS IS"
' * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
' * 
' * ***** END LICENSE BLOCK *****
' */
'
' ZD runner
'

Sub FindAndReplace(sFile, oTokens)
    Dim oFso, oInFile, oOutFile, sTmpFile
    
    Set oFso = CreateObject("Scripting.FileSystemObject")
    sTmpFile = sFile & ".tmp"
    
    On Error Resume Next
    Set oInFile = oFso.OpenTextFile(sFile, 1, false)
    If Err.number <> 0 Then
        WScript.StdOut.WriteLine("failed to open file: " & sFile)
        Exit Sub
    End If
    Set oOutFile = oFso.OpenTextFile(sTmpFile, 2, true)
    If Err.number <> 0 Then
        WScript.StdOut.WriteLine("failed to open file: " & sTmpFile)
        Exit Sub   
    End If
    
    Do Until oInFile.AtEndOfStream
        Dim sLine, sKey
        sLine = oInFile.ReadLine
        For Each sKey in oTokens.Keys
            sLine = Replace(sLine, sKey, oTokens.Item(sKey))
        Next
        oOutFile.WriteLine(sLine)       
    Loop
   
    oInFile.Close
    oOutFile.Close
    oFso.DeleteFile sFile, true
    oFso.MoveFile sTmpFile, sFile
End Sub

Function GetRandomId
    Set oTypeLib = CreateObject("Scriptlet.TypeLib")
    GetRandomId = LCase(Mid(oTypeLib.GUID, 2, 36))
End Function

Sub LaunchPrism()
    Dim oWMI, oStartup, oCfg, oProc, iPid
    
    Set oWMI = GetObject("winmgmts:{impersonationLevel=impersonate}!\\.\root\cimv2")
    Set oStartup = oWMI.Get("Win32_ProcessStartup")
    Set oCfg = oStartup.SpawnInstance_
    oCfg.ShowWindow = 1 ' SW_NORMAL

    sCmd = ""
    
    Set oProc = oWMI.Get("Win32_Process")
    oProc.Create sCmd, Null, oCfg, iPid             
    WScript.Quit
End Sub

'------------------------------- main ---------------------------------

Dim oFso, oShell, oShellApp, sScriptPath, sScriptDir, oTokens, sAppRoot, sDataRoot
Dim sLocalAppDir, bIsUpgrade, sTmpDir, sAppTimeStamp

'Set oShell = WScript.CreateObject("WScript.Shell")
Set oFso = CreateObject("Scripting.FileSystemObject")
Set oShellApp = CreateObject("Shell.Application")

sAppTimeStamp = "@INSTALL.APP.TIMESTAMP@"
bIsUpgrade = false
sTmpDir = oFso.GetSpecialFolder(2).Path & "\zdtmp"
sScriptPath = WScript.ScriptFullName
sScriptDir = Left(sScriptPath, InStrRev(sScriptPath, WScript.ScriptName) - 2)
sAppRoot = oFso.GetParentFolderName(sScriptDir)
sLocalAppDir = oShellApp.Namespace(&H1c&).Self.Path
sDataRoot = sLocalAppDir & "\Zimbra\Zimbra Desktop"

' copy data files
If Not oFso.FolderExists(sLocalAppDir & "\Zimbra") Then
    oFso.CreateFolder sLocalAppDir & "\Zimbra"
End If
If Not oFso.FolderExists(sLocalAppDir & "\Zimbra\Zimbra Desktop") Then
    oFso.CreateFolder sLocalAppDir & "\Zimbra\Zimbra Desktop"
End If
oFso.CopyFolder sAppRoot & "\data\*", sDataRoot & "\", true

' fix data files
Set oTokens = CreateObject("Scripting.Dictionary")
oTokens.Add "@install.app.root@", sAppRoot
oTokens.Add "@install.data.root@", sDataRoot
oTokens.Add "@install.key@", GetRandomId()

FindAndReplace sDataRoot & "\conf\localconfig.xml", oTokens
FindAndReplace sDataRoot & "\jetty\etc\jetty.xml", oTokens
FindAndReplace sDataRoot & "\zdesktop.webapp\webapp.ini", oTokens
FindAndReplace sDataRoot & "\bin\zdesktop.ini", oTokens
FindAndReplace sDataRoot & "\bin\zdctl.vbs", oTokens

LaunchPrism()
