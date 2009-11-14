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

Dim sScriptPath, sScriptDir, sZdOutFile, sZdPidFile, sZdStopPortFile, sZdCtlErrFile, sAppRoot, oWMI, oFso

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

' sDir: if Null, it's calling process's current dir
' iSW: 0 - hide, 1 - normal
Function RunCmd(sCmd, sDir, iSW, sPidFile, sErrFile) 
    Dim oStartup, oCfg, oProc, iRet, iPid, sErr, oFout
    
    Set oStartup = oWMI.Get("Win32_ProcessStartup")
    Set oCfg = oStartup.SpawnInstance_
    oCfg.ShowWindow = iSW

    Set oProc = oWMI.Get("Win32_Process")
    iRet = oProc.Create(sCmd, sDir, oCfg, iPid)         
    If iRet <> 0 Then
        sErr = "Process could not be created(" & iRet & "): " & sCmd        
        If IsNull(sErrFile) Then
            WScript.StdOut.WriteLine(sErr)
        Else
            WriteLineToFile sErrFile, sErr, false
        End If
        RunCmd = false
    Else
        If Not IsNull(sPidFile) Then
            WriteLineTofile sPidFile, iPid, false
        End If
        RunCmd = true
    End If
End Function

Sub StartServer()
    Dim bRet, sCmd, oFile, sLastMod, sStopPort, iWaitTime, iSize
    
    If IsRunning() Then
        WScript.StdOut.WriteLine("ZD service already running")
        WScript.Quit
    End If

    sLastMod = ""
    If oFso.FileExists(sZdOutFile) Then
        Set oFile = oFso.GetFile(sZdOutFile)
        sLastMod = oFile.DateLastModified
    End If
    
    sCmd = sScriptDir & "\zdesktop.exe"
    bRet = RunCmd(sCmd, sScriptDir, 0, sZdPidFile, sZdCtlErrFile)
    WScript.Sleep 1500
    
    If (bRet = false) Or (Not oFso.FileExists(sZdOutFile)) Then
        WriteLineToFile sZdCtlErrFile, "Failed to start ZD service", true
        Exit Sub
    End If
     
    iWaitTime = 10000 ' 10 seconds
    Do Until iWaitTime <= 0
        Set oFile = oFso.GetFile(sZdOutFile)
        iSize = oFile.Size
        If iSize > 100 Then
            Exit Do
        End If        
        WSCript.Sleep(1000)
        iWaitTime = iWaitTime - 1000    
    Loop
    
    If StrComp(sLastMod, oFile.DateLastModified) = 0 Then
        Exit Sub  ' second instance of zdesktop.exe quits
    End If
    
    sStopPort = Null
    Set oFile = oFso.OpenTextFile(sZdOutFile, 1, false)
    Do Until oFile.AtEndOfStream
        Dim sLine
        sLine = oFile.ReadLine
        If IsNumeric(sLine) Then
            sStopPort = sLine
            Exit Do
        End If   
    Loop
    oFile.Close
    
    If IsNull(sStopPort) Then
        WriteLineToFile sZdCtlErrFile, "Unable to get stop port", true
    Else
        WriteLineToFile sZdStopPortFile, sStopPort, false
    End If
End Sub

Sub StopServer()
    Dim sPid, sStopPort, sWaitTime, sCmd
    
    If Not IsRunning() Then
        WScript.StdOut.WriteLine("ZD service not running")
        WScript.Quit
    End If

    sWaitTime = 10000 ' 10 seconds
    sStopPort = ReadLineFromFile(sZdStopPortFile)
    If IsNull(sStopPort) Then
        WriteLineToFile sZdCtlErrFile, "Unable to read log\zdesktop.sp", true
        sWaitTime = 0
    Else
        sCmd = sAppRoot & "\win32\jre\bin\java.exe -DSTOP.PORT=" & sStopPort & _
            " -DSTOP.KEY=stop -jar " & Chr(34) & sAppRoot & _
            "\jetty\start.jar" & Chr(34) & " --stop"
        If RunCmd(sCmd, Null, 0, Null, sZdCtlErrFile) = false Then
            sWaitTime = 0
        End If   
    End If        

    sPid = ReadLineFromFile(sZdPidFile)
    If IsNull(sPid) Then
        WriteLineToFile sZdCtlErrFile, "Unable to read log\zdesktop.pid", true
        Exit Sub
    End If    

    Do Until sWaitTime <= 0
        Dim oProcs
        Set oProcs = oWMI.ExecQuery("SELECT Name FROM Win32_Process WHERE ProcessId = " & sPid)
        
        If oProcs.Count = 0 Then ' process quit
            oFso.DeleteFile sZdStopPortFile
            oFso.DeleteFile sZdPidFile
            Exit Sub 
        End If        
        WScript.Sleep 1000
        sWaitTime = sWaitTime - 1000
    Loop
    
    ' hard kill
    sCmd = "taskkill /F /PID " & sPid
    RunCmd sCmd, Null, 0, Null, Null
End Sub

'--------------------------------- main ---------------------------------
Dim oArgs
Set oArgs = WScript.Arguments
If oArgs.Count < 1 Then ' have to break them up here - vbs always evals ALL expressions
    Usage()
ElseIf oArgs.Item(0) <> "start" And oArgs.Item(0) <> "stop" Then
    Usage()
End If

sAppRoot = "@install.app.root@"
sScriptPath = WScript.ScriptFullName
sScriptDir = Left(sScriptPath, InStrRev(sScriptPath, WScript.ScriptName) - 2)
sZdOutFile = sScriptDir & "\..\log\zdesktop.out"
sZdPidFile = sScriptDir & "\..\log\zdesktop.pid"
sZdStopPortFile = sScriptDir & "\..\log\zdesktop.sp"
sZdCtlErrFile = sScriptDir & "\..\log\zdctl.err"
Set oWMI = GetObject("winmgmts:{impersonationLevel=impersonate}!\\.\root\cimv2")
Set oFso = CreateObject("Scripting.FileSystemObject")
        
If oArgs.Item(0) = "start" Then
    StartServer()
Else
    StopServer()
End If