@ECHO OFF
SET BASE=%~dp0

REM remove all old binaries and intermediate files
rmdir /s /q %BASE%\Win32\
rmdir /s /q %BASE%\x64\

cd %BASE%
dir *.sln

call "C:\Program Files (x86)\Microsoft Visual Studio 10.0\vc\bin\vcvars32.bat"

echo "dbg|Win32"
echo --------------
MSBuild Migration.sln /t:Build /verbosity:n /p:Configuration=dbg;Platform=Win32
IF ERRORLEVEL 1 exit /B 1 

echo "rtl|Win32"
echo --------------
MSBuild Migration.sln /t:Build /verbosity:n /p:Configuration=rtl;Platform=Win32
IF ERRORLEVEL 1 exit /B 1 

call "C:\Program Files (x86)\Microsoft Visual Studio 10.0\vc\bin\amd64\vcvars64.bat"

echo "dbg|x64"
echo --------------
MSBuild Migration.sln /t:Build /verbosity:n /p:Configuration=dbg;Platform=x64
IF ERRORLEVEL 1 exit /B 1 

echo "rtl|x64"
echo --------------
MSBuild Migration.sln /t:Build /verbosity:n /p:Configuration=rtl;Platform=x64
IF ERRORLEVEL 1 exit /B 1 

ECHO Source Indexing the Project...
ECHO Starting Point is %BASE%

cd %BASE%
call ssindex.cmd -system=p4
IF ERRORLEVEL 1 exit /B 1 

ECHO Adding Binaries to the Symbol Server...
ECHO symstore add /f %BASE%\src\c\*\rtl\*.* /s \\%INDEX_HOST%\Zbuild3\symbols\ZCO /t "Zimbra Migration Tools" /v "%BUILD_VERSION%" /c "%BUILD_DATE%"
symstore add /f %BASE%\src\c\Win32\rtl\*.* /s \\%INDEX_HOST%\Zbuild3\symbols\ZCO /t "Zimbra Migration Tools x86" /v "%BUILD_VERSION%" /c "%BUILD_DATE%"
IF ERRORLEVEL 1 exit /B 1 
symstore add /f %BASE%\src\c\x64\rtl\*.* /s \\%INDEX_HOST%\Zbuild3\symbols\ZCO /t "Zimbra Migration Tools x64" /v "%BUILD_VERSION%" /c "%BUILD_DATE%"
IF ERRORLEVEL 1 exit /B 1 

