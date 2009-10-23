
Timeout = 20

While Timeout > 0
  strComputer = "."
  Set objWMIService = GetObject("winmgmts:\\" & strComputer & "\root\cimv2")
  Set colProcs = objWMIService.ExecQuery("SELECT ProcessId FROM Win32_Process WHERE Name = 'zdesktop.exe' ")

  If colProcs.Count = 0 Then
    Wscript.Quit
  End If
    
  Wscript.Sleep(1000)
  Timeout = Timeout - 1
Wend

For Each Proc in colProcs
  Set WshShell = CreateObject("WScript.Shell")
  WshShell.Exec("taskkill /f /pid " & Proc.ProcessId)
Next
