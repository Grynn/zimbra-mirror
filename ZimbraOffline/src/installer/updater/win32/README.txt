This is a hack to turn downloaded update.mar into update.msi and install it. 

updater.cpp is based on mozilla/toolkit/mozapps/update/src/updater/updater.cpp.  

To build updater.exe:

** Prerequisites

Install these packages on the Windows build machine:

- Visual C++ 2008 Express Edition (http://www.microsoft.com/express/Downloads/#2008-Visual-CPP)

- Windows Server 2003 R2 Platform SDK (http://www.microsoft.com/downloads/en/details.aspx?FamilyID=484269e2-3b89-47e3-8eb7-1f2be6d7123a&displaylang=en)

- Mozilla Build (p4 depot: ThirdParty/windows/mozilla/1.9.2/MozillaBuildSetup-1.5.1.exe)

** Steps

1) Untar source code to an empty directory ($TOPDIR)
   p4 depot: ThirdParty/windows/mozilla/1.9.2/mozilla-1-9-2-5b942ca87e90.tar.bz2

2) Run start-msvc9.bat from where Mozilla Build is installed (e.g. c:\mozilla-build)

3) Change directory to $TOPDIR, vi .mozconfig, add the following lines:

   ac_add_options --with-windows-version=600
   ac_add_options --enable-debug
   ac_add_options --disable-javaxpcom
   ac_add_options --disable-accessibility
   ac_add_options --enable-application=browser

4) Replace "$TOPDIR/mozilla/toolkit/mozapps/update/src/updater/updater.cpp" with "ZimbraOffline/src/installer/updater/win32/updater.cpp"

5) Run the following command from $TOPDIR to kick off a build. updater.exe can be found at $TOPDIR/dist/bin when build finishes.

   make -f client.mk

 

