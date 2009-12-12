
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

Dim oFso, oShellApp, sScriptPath, sScriptDir, oTokens, sAppRoot, sDataRoot
Dim sLocalAppDir, bIsUpgrade, sTmpDir, aUserDirs, aUserFiles

Sub FindAndReplace(sFile, oTokens)
    Dim oFso, oInFile, oOutFile, sTmpFile
    
    Set oFso = CreateObject("Scripting.FileSystemObject")
    sTmpFile = sFile & ".tmp"
    
    On Error Resume Next
    Set oInFile = oFso.OpenTextFile(sFile, 1, false)
    If Err.number <> 0 Then
        WScript.StdOut.WriteLine "failed to open file: " & sFile
        Exit Sub
    End If
    Set oOutFile = oFso.OpenTextFile(sTmpFile, 2, true)
    If Err.number <> 0 Then
        WScript.StdOut.WriteLine "failed to open file: " & sTmpFile
        Exit Sub   
    End If
    
    Do Until oInFile.AtEndOfStream
        Dim sLine, sKey
        sLine = oInFile.ReadLine
        For Each sKey In oTokens.Keys
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

Sub CopyIfExists(sSrc, sDest, bOW)
	If oFso.FileExists(sSrc) Then
		oFso.CopyFile sSrc, sDest, bOW
	End If
End Sub

Sub LaunchPrism()
    Dim oShell, sCmd

    Set oShell = CreateObject("WScript.Shell")
    sCmd = Chr(34) & sAppRoot & "\win32\prism\zdclient.exe" & Chr(34) & " -override " & _
        Chr(34) & sDataRoot & "\zdesktop.webapp\override.ini" & Chr(34)  
    oShell.Run sCmd, 1, false 
    WScript.Quit
End Sub

Sub BackupData()
	If oFso.FolderExists(sTmpDir) Then
		oFso.DeleteFolder sTmpDir, true
	End If
	oFso.CreateFolder sTmpDir
	
	Dim sDir
	For Each sDir In aUserDirs
		oFso.MoveFolder sDataRoot & "\" & sDir, sTmpDir & "\" & sDir
	Next
	
	oFso.CreateFolder sTmpDir & "\profile"
	Dim sFile
	For Each sFile In aUserFiles
		CopyIfExists sDataRoot & "\" & sFile, sTmpDir & "\" & sFile, true 
	Next
	
	oFso.MoveFolder sDataRoot & "\zimlets\backup", sTmpDir & "\zimlets"	
	
	oFso.DeleteFolder sDataRoot, true
End Sub

Sub RestoreData()
	Dim sDir
	For Each sDir In aUserDirs
		If oFso.FolderExists(sDataRoot & "\" & sDir) Then 
			oFso.DeleteFolder sDataRoot & "\" & sDir, true
		End If
		oFso.MoveFolder sTmpDir & "\" & sDir, sDataRoot & "\" & sDir 
	Next
	
	Dim sFile
	For Each sFile In aUserFiles
		CopyIfExists sTmpDir & "\" & sFile, sDataRoot & "\" & sFile, true
	Next
	
	' restore zimlets, but don't overwrite at dest
	Dim oZLFolder, oFiles, oFile
	Set oZLFolder = oFso.GetFolder(sTmpDir & "\zimlets")
	Set oFiles = oZLFolder.Files
	For Each oFile In oFiles
		If Not oFso.FileExists(sDataRoot & "\zimlets\" & oFile.Name) Then 
			oFso.CopyFile sTmpDir & "\zimlets\" & oFile.Name, _
				sDataRoot & "\zimlets\" & oFile.Name, true
		End If
	Next
	
	oFso.DeleteFolder sTmpDir, true
End Sub

'------------------------------- main ---------------------------------

Set oFso = CreateObject("Scripting.FileSystemObject")
Set oShellApp = CreateObject("Shell.Application")

aUserDirs = Array("index", "store", "sqlite", "log", "zimlets-properties")
aUserFiles = Array("profile\prefs.js", "profile\persdict.dat", "profile\localstore.json")
sScriptPath = WScript.ScriptFullName
sScriptDir = Left(sScriptPath, InStrRev(sScriptPath, WScript.ScriptName) - 2)
sAppRoot = oFso.GetParentFolderName(sScriptDir)
sLocalAppDir = oShellApp.Namespace(&H1c&).Self.Path
sDataRoot = sLocalAppDir & "\Zimbra\Zimbra Desktop"
sTmpDir = sDataRoot & ".tmp"
bIsUpgrade = false

If oFso.FolderExists(sDataRoot) Then
	Dim sAppDataRoot, oDataDir, oAppDataDir
	sAppDataRoot = sAppRoot & "\data"
	Set oDataDir = oFso.GetFolder(sDataRoot)
	Set oAppDataDir = oFso.GetFolder(sAppDataRoot)
	
	If DateDiff("s", oDataDir.DateLastModified, oAppDataDir.DateLastModified) > 0 Then
		bIsUpgrade = true
	Else
		LaunchPrism
	End If
End If

WScript.StdOut.WriteLine "Initializing. Please wait..."

If bIsUpgrade Then
	BackupData
End If

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
oTokens.Add "@install.locale@", "en-US"

FindAndReplace sDataRoot & "\bin\zdctl.vbs", oTokens
FindAndReplace sDataRoot & "\conf\localconfig.xml", oTokens
FindAndReplace sDataRoot & "\conf\zdwrapper.conf", oTokens
FindAndReplace sDataRoot & "\jetty\etc\jetty.xml", oTokens
FindAndReplace sDataRoot & "\zdesktop.webapp\webapp.ini", oTokens
FindAndReplace sDataRoot & "\zdesktop.webapp\override.ini", oTokens
FindAndReplace sDataRoot & "\profile\user.js", oTokens

If bIsUpgrade Then
	RestoreData
End If

LaunchPrism
