' * ***** BEGIN LICENSE BLOCK *****
' * Zimbra Collaboration Suite Server
' * Copyright (C) 2009, 2010 Zimbra, Inc.
' * 
' * The contents of this file are subject to the Zimbra Public License
' * Version 1.2 ("License"); you may not use this file except in
' * compliance with the License.  You may obtain a copy of the License at
' * http://www.zimbra.com/license.
' * 
' * Software distributed under the License is distributed on an "AS IS"
' * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
' * ***** END LICENSE BLOCK *****
' */
'
' ZD service control
'

Dim sAppRoot, sScriptPath, sScriptDir, sZdLogFile, sZdOutFile, sZdAnchorFile, sZdCtlErrFile, oWMI, oShell, oFso, sCurrUser

Sub Usage()
    WScript.StdOut.WriteLine("Usage: zdctl.vbs <start|stop|shutdown>")
    WScript.Quit
End Sub

Function IsRunning()
    Dim sOutFile
            
    On Error Resume Next   
    oFso.OpenTextFile sZdLogFile, 8, false    
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

Sub RunCmd(sCmd, iDelay) 
	oShell.Run sCmd, 0, false
	If iDelay > 0 Then
		WScript.Sleep iDelay
	End If 
End Sub

Sub RunCmd2(sCmd, sArgs, sImgName, sPidFile, sErrFile) 
	RunCmd sCmd & " " & sArgs, 2000

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

Function FindProcess(sImageName)
	Dim oProcs, oProc
	
	FindProcess = Null
    Set oProcs = oWMI.ExecQuery("SELECT ProcessId FROM Win32_Process WHERE Name = '" & sImageName & "' ")
	For Each oProc In oProcs
		Dim sUser, sDomain
		If oProc.GetOwner(sUser, sDomain) = 0 Then
			If StrComp(sUser, sCurrUser) = 0 Then
				FindProcess = oProc.ProcessId	
				Exit Function
			End If
		End If		
	Next
End Function

Sub WaitAndTerm(iPid, iWaitTime)
    Dim sTaskKill, oProcs

    Do Until iWaitTime <= 0
        Set oProcs = oWMI.ExecQuery("SELECT Name FROM Win32_Process WHERE ProcessId = " & iPid) 
        If oProcs.Count = 0 Then ' done
            Exit Sub 
        End If        
        WScript.Sleep 1000
        iWaitTime = iWaitTime - 1000
    Loop
    
    ' hard kill
    sTaskKill = Chr(34) & oFso.GetSpecialFolder(1).Path & "\taskkill.exe" & Chr(34)
    RunCmd sTaskKill & " /F /PID " & iPid, 0
End Sub

Sub StartServer()
    Dim sCmd, oFile, iWaitTime 
    
    If IsRunning() Then
        WScript.StdOut.WriteLine("ZD service already running")
        WScript.Quit
    End If

    WScript.StdOut.WriteLine("Starting background process. Please wait...")

    If oFso.FileExists(sZdOutFile) Then
        oFso.DeleteFile sZdOutFile
    End If
    If oFso.FileExists(sZdAnchorFile) Then
        oFso.DeleteFile sZdAnchorFile
    End If
    
    sCmd = Chr(34) & sAppRoot & "\win32\zdesktop.exe" & Chr(34) & " " & Chr(34) & sScriptDir & "\..\conf\zdesktop.conf" & Chr(34)
    RunCmd sCmd, 2000

   	iWaitTime = 20000 
   	Do Until iWaitTime <= 0
       	If oFso.FileExists(sZdAnchorFile) Then
           	Exit Sub
       	End If
       	WSCript.Sleep(1000)
       	iWaitTime = iWaitTime - 1000
   	Loop
    
    WriteLineToFile sZdCtlErrFile, "Failed to start ZD service", true
End Sub

Sub StopServer()
    Dim iZdPid, iWaitTime
    
    If Not IsRunning() Then
        WScript.StdOut.WriteLine("ZD service not running")
        WScript.Quit
    End If

    WScript.StdOut.WriteLine("Stopping background process. Please wait...")

	iZdPid = FindProcess("zdesktop.exe")
	If IsNull(iZdPid) Then ' no running zdesktop instance found
		Exit Sub			
	End If

	' remove anchor to trigger graceful shutdown
	If oFso.FileExists(sZdAnchorFile) Then
		oFso.DeleteFile sZdAnchorFile
    	iWaitTime = 15000 ' 15 seconds
	Else
		iWaitTime = 0
	End If

	WaitAndTerm iZdPid, iWaitTime
End Sub

Sub Shutdown()
    Dim iPrismPid, sCmd
	
    StopServer()
	
    iPrismPid = FindProcess("zdclient.exe")
    If IsNull(iPrismPid) Then
	    Exit Sub
    End If

    sCmd = Chr(34) & sAppRoot & "\win32\prism\zdclient.exe" & Chr(34) & " -close"
    RunCmd sCmd, 0
    WaitAndTerm iPrismPid, 5000
End Sub

'--------------------------------- main ---------------------------------
Dim oArgs, oWN
Set oArgs = WScript.Arguments
If oArgs.Count < 1 Then ' have to break them up here - vbs always evals ALL expressions
    Usage()
ElseIf oArgs.Item(0) <> "start" And oArgs.Item(0) <> "stop" And oArgs.Item(0) <> "shutdown" Then
    Usage()
End If

sAppRoot = "@install.app.root@"
sScriptPath = WScript.ScriptFullName
sScriptDir = Left(sScriptPath, InStrRev(sScriptPath, WScript.ScriptName) - 2)
sZdLogFile = sScriptDir & "\..\log\zdesktop.log"
sZdOutFile = sScriptDir & "\..\log\wrapper.log"
sZdAnchorFile = sScriptDir & "\..\log\zdesktop.pid"
sZdCtlErrFile = sScriptDir & "\..\log\zdctl.err"
Set oWMI = GetObject("winmgmts:{impersonationLevel=impersonate}!\\.\root\cimv2")
Set oShell = CreateObject("WScript.Shell")
Set oFso = CreateObject("Scripting.FileSystemObject")
Set oWN = WScript.CreateObject("WScript.Network")
sCurrUser = oWN.UserName
        
If oArgs.Item(0) = "start" Then
    StartServer()
ElseIf oArgs.Item(0) = "stop" Then
    StopServer()
Else
    Shutdown()
End If
