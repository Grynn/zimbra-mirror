@ECHO OFF

REM --------------------------------------------------------------------------------------------
REM File    : SetBuildEnv.Bat
REM
REM Abstract: This batch file will set up the system variables.
REM           If you have not installed Visual Studio and the Platform SDK to the default
REM           folders, you may need to update the initial paths 
REM 
REM --------------------------------------------------------------------------------------------

REM --------------------------------------------------------------------------------------------
REM Make sure that these paths are valid for your installation
REM This should be the case if you installed the default installation folders
SET MSSdk=C:\Program Files (x86)\Microsoft SDKs\Windows\v7.0A
SET VSINSTALLDIR=C:\Program Files (x86)\Microsoft Visual Studio 10.0\Common7\IDE
SET VCINSTALLDIR=C:\Program Files (x86)\Microsoft Visual Studio 10.0
SET FrameworkSDKDir=C:\Program Files (x86)\Microsoft Visual Studio 10.0\SDK\v3.5
SET FrameworkDir=C:\Windows\Microsoft.NET\Framework
SET Framework64Dir=C:\Windows\Microsoft.NET\Framework64
REM --------------------------------------------------------------------------------------------

REM Don't really know what this is used for but it was part of vsvars.bat
SET FrameworkVersion=v4.0.30319
SET DevEnvDir=%VSINSTALLDIR%
SET MSVCDir=%VCINSTALLDIR%\VC

SET ORGPATH=%PATH%
SET ORGINCLUDE=%INCLUDE%
SET ORGLIB=%LIB%
SET PATH=%DevEnvDir%;%MSVCDir%\BIN;%VCINSTALLDIR%\Common7\Tools;%VCINSTALLDIR%\Common7\Tools\bin\prerelease;%VCINSTALLDIR%\Common7\Tools\bin;%FrameworkSDKDir%\bin;%FrameworkDir%\%FrameworkVersion%;%ORGPATH%;
SET INCLUDE=%MSSdk%\include;%MSVCDir%\ATLMFC\INCLUDE;%MSVCDir%\INCLUDE;%MSVCDir%\PlatformSDK\include\prerelease;%MSVCDir%\PlatformSDK\include;%FrameworkSDKDir%\include;%ORGINCLUDE%
SET LIB=%MSSdk%\lib;%MSVCDir%\ATLMFC\LIB;%MSVCDir%\LIB;%MSVCDir%\PlatformSDK\lib\prerelease;%MSVCDir%\PlatformSDK\lib;%FrameworkSDKDir%\lib;%ORGLIB%

SET BASE=%~dp0

REM remove all old binaries and intermediate files
rmdir /s /q %BASE%\Win32\
rmdir /s /q %BASE%\x64\

cd %BASE%
dir *.sln

echo "dbg|Win32"
echo --------------
MSBuild Migration.sln /t:Build /verbosity:m /p:Configuration=dbg;Platform=Win32
IF ERRORLEVEL 1 exit /B 1 

echo "rtl|Win32"
echo --------------
MSBuild Migration.sln /t:Build /verbosity:m /p:Configuration=rtl;Platform=Win32
IF ERRORLEVEL 1 exit /B 1 

SET PATH=%DevEnvDir%;%MSVCDir%\BIN\x86_amd64;%VCINSTALLDIR%\Common7\Tools;%VCINSTALLDIR%\Common7\Tools\bin\prerelease;%VCINSTALLDIR%\Common7\Tools\bin;%FrameworkSDKDir%\bin\x64;%Framework64Dir%\%FrameworkVersion%;%ORGPATH%;
SET INCLUDE=%MSSdk%\include;%MSVCDir%\ATLMFC\INCLUDE;%MSVCDir%\INCLUDE;%MSVCDir%\PlatformSDK\include\prerelease;%MSVCDir%\PlatformSDK\include;%FrameworkSDKDir%\include;%ORGINCLUDE%
SET LIB=%MSSdk%\lib\x64;%MSVCDir%\ATLMFC\LIB\amd64;%MSVCDir%\LIB\amd64;%MSVCDir%\PlatformSDK\lib\prerelease;%MSVCDir%\PlatformSDK\lib;%FrameworkSDKDir%\lib;%ORGLIB%

echo "dbg|x64"
echo --------------
MSBuild Migration.sln /t:Build /verbosity:m /p:Configuration=dbg;Platform=x64
IF ERRORLEVEL 1 exit /B 1 

echo "rtl|x64"
echo --------------
MSBuild Migration.sln /t:Build /verbosity:m /p:Configuration=rtl;Platform=x64
IF ERRORLEVEL 1 exit /B 1 

REM add source server info
ECHO Source Indexing the Project...
ECHO Starting Point is %BASE%

cd %BASE%
call ssindex.cmd -system=p4
IF ERRORLEVEL 1 exit /B 1 

REM add the latest stuff to the symbol server
ECHO Adding Binaries to the Symbol Server...
ECHO symstore add /f %BASE%\Win32\rtl\*.* /s \\%INDEX_HOST%\Zbuild3\symbols\Migration /t "Zimbra Migration Tools Modules" /v "%BUILD_VERSION%" /c "%BUILD_DATE%"
symstore add /f %BASE%\Win32\*.* /s \\%INDEX_HOST%\Zbuild3\symbols\Migration /t "Zimbra Migration Tools x86 Modules" /v "%BUILD_VERSION%" /c "%BUILD_DATE%"
IF ERRORLEVEL 1 exit /B 1 
symstore add /f %BASE%\x64\rtl\*.* /s \\%INDEX_HOST%\Zbuild3\symbols\Migration /t "Zimbra Migration Tools x64 Modules" /v "%BUILD_VERSION%" /c "%BUILD_DATE%"
IF ERRORLEVEL 1 exit /B 1 

