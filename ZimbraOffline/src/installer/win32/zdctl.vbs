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
' ZD service control
'

Dim sScriptPath, sScriptDir, sZdOutFile, sZdPidFile, sZdCtlErrFile, oWMI, oShell, oFso, sCurrUser

Sub Usage()
    WScript.StdOut.WriteLine("Usage: zdctl.vbs <start|stop>")
    WScript.Quit
End Sub

Function IsRunning()
    Dim sOutFile
            
    On Error Resume Next   
    oFso.OpenTextFile sZdOutFile, 8, false    
    If Err.number = 70 Then
        IsRunning = true
    Else
        IsRunning = false
    End If
End Function

Sub WriteLineToFile(sFile, sLine, bAppend)
    Dim oFout, iMode
        
    If bAppend Then
        iMode = 8
    Else
        iMode = 2
    End If
    
    On Error Resume Next
    Set oFout = oFso.OpenTextFile(sFile, iMode, true)
    If Err.number = 0 Then
        oFout.WriteLine(sLine)
    End If
    oFout.Close
End Sub

Function ReadLineFromFile(sFile)
    Dim oFin
    
    ReadLineFromFile = Null
    On Error Resume Next
    Set oFin = oFso.OpenTextFile(sFile, 1, false)
    If Err.number = 0 Then
        ReadLineFromFile = oFin.ReadLine()
    End If
    oFin.Close
End Function

Sub RunCmd(sCmd, sArgs, sImgName, sPidFile, sErrFile) 
    oShell.Run sCmd & " " & sArgs, 0, false
    WScript.Sleep 2000 

    If (Not IsNull(sImgName)) And (Not IsNull(sPidFile)) Then
    	Dim oProcs, oProc
		Set oProcs = oWMI.ExecQuery("SELECT ProcessId, CommandLine FROM Win32_Process WHERE Name = '" & sImgName & "' ")
    	For Each oProc In oProcs
    		If InStr(1, oProc.CommandLine, sArgs, 1) > 0 Then
				WriteLineToFile sPidFile, oProc.ProcessId, false
				Exit Sub
			End If
		Next
    End If

	If (Not IsNull(sErrFile)) Then
		WriteLineToFile sErrFile, "Unable to get process id"
	End If
End Sub

Sub StartServer()
    Dim sArgs, oFile, iWaitTime 
    
    If IsRunning() Then
        WScript.StdOut.WriteLine("ZD service already running")
        WScript.Quit
    End If

    WScript.StdOut.WriteLine("Starting background process. Please wait...")

    If oFso.FileExists(sZdOutFile) Then
        oFso.DeleteFile sZdOutFile
    End If
    
    sArgs = "/k " & Chr(34) & sScriptDir & "\zdesktop.exe" & Chr(34)
    RunCmd "%comspec%", sArgs, "cmd.exe", sZdPidFile, Null 

   	iWaitTime = 20000 
   	Do Until iWaitTime <= 0
       	If oFso.FileExists(sZdOutFile) Then
           	Exit Do
       	End If
       	WSCript.Sleep(1000)
       	iWaitTime = iWaitTime - 1000
   	Loop
    
    If Not oFso.FileExists(sZdOutFile) Then
        WriteLineToFile sZdCtlErrFile, "Failed to start ZD service", true
    End If
End Sub

Sub StopServer()
    Dim sPid, sZdPid, iWaitTime, sTaskKill, oProcs, oProc
    
    If Not IsRunning() Then
        WScript.StdOut.WriteLine("ZD service not running")
        WScript.Quit
    End If

    WScript.StdOut.WriteLine("Stopping background process. Please wait...")

	sTaskKill = Chr(34) & oFso.GetSpecialFolder(1).Path & "\taskkill.exe" & Chr(34)
    iWaitTime = 10000 ' 10 seconds

    sPid = ReadLineFromFile(sZdPidFile)
    If IsNull(sPid) Then
        WriteLineToFile sZdCtlErrFile, "Unable to read log\zdesktop.pid", true
        iWaitTime = 0 
	Else
		RunCmd sTaskKill, "/PID " & sPid, Null, Null, Null
    End If    

	sZdPid = Null
    Set oProcs = oWMI.ExecQuery("SELECT ProcessId FROM Win32_Process WHERE Name = 'zdesktop.exe' ")
	For Each oProc In oProcs
		Dim sUser, sDomain
		If oProc.GetOwner(sUser, sDomain) = 0 Then
			If StrComp(sUser, sCurrUser) = 0 Then
				sZdPid = oProc.ProcessId	
				Exit For
			End If
		End If		
	Next

	If IsNull(sZdPid) Then
    	oFso.DeleteFile sZdPidFile
		Exit Sub			
	End If

    Do Until iWaitTime <= 0
		Set oProcs = oWMI.ExecQuery("SELECT Name FROM Win32_Process WHERE ProcessId = " & sZdPid) 
        If oProcs.Count = 0 Then
            oFso.DeleteFile sZdPidFile
            Exit Sub 
        End If        
        WScript.Sleep 1000
        iWaitTime = iWaitTime - 1000
    Loop
    
    ' hard kill
   	RunCmd sTaskKill, "/F /PID " & sZdPid, Null, Null, Null
	On Error Resume Next
    oFso.DeleteFile sZdPidFile
End Sub

'--------------------------------- main ---------------------------------
Dim oArgs, oWN
Set oArgs = WScript.Arguments
If oArgs.Count < 1 Then ' have to break them up here - vbs always evals ALL expressions
    Usage()
ElseIf oArgs.Item(0) <> "start" And oArgs.Item(0) <> "stop" Then
    Usage()
End If

sScriptPath = WScript.ScriptFullName
sScriptDir = Left(sScriptPath, InStrRev(sScriptPath, WScript.ScriptName) - 2)
sZdOutFile = sScriptDir & "\..\log\zdesktop.out"
sZdPidFile = sScriptDir & "\..\log\zdesktop.pid"
sZdCtlErrFile = sScriptDir & "\..\log\zdctl.err"
Set oWMI = GetObject("winmgmts:{impersonationLevel=impersonate}!\\.\root\cimv2")
Set oShell = CreateObject("WScript.Shell")
Set oFso = CreateObject("Scripting.FileSystemObject")
Set oWN = WScript.CreateObject("WScript.Network")
sCurrUser = oWN.UserName
        
If oArgs.Item(0) = "start" Then
    StartServer()
Else
    StopServer()
End If
