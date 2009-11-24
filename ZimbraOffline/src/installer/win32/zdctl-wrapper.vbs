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
' A script that helps prism hide console window when calling zdctl.vbs
' This is to workaround a limitation of XPCOM's nsIProcess

Dim oFso, oShell, sCScript, sScriptPath, sZdCtl, sCmd

Set oFso = CreateObject("Scripting.FileSystemObject")
sCScript = Chr(34) & oFso.GetSpecialFolder(1).Path & "\cscript.exe" & Chr(34)

sScriptPath = WScript.ScriptFullName
sZdCtl = Chr(34) & Left(sScriptPath, InStrRev(sScriptPath, WScript.ScriptName) - 2) & "\zdctl.vbs" & Chr(34)

Set oShell = CreateObject("WScript.Shell")
sCmd = sCScript & " " & sZdCtl & " " & WScript.Arguments.Item(0)
oShell.Run sCmd, 0, false
