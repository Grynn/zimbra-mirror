====================================================================
Zimbra Desktop README
====================================================================

@version@
March, 2007

====================================================================
GETTING STARTED
====================================================================

--------------------------------------------------------------------
HARDWARE REQUIREMENTS
--------------------------------------------------------------------

At least 200MB free disk space is required to install the software.
Additional disk space is required to download mailbox data.

At least 256MB free memory is required to run Zimbra Desktop.

The computer must have network connectivity at account setup.
Network connectivity is also required to synchronize local mailbox
with server.

--------------------------------------------------------------------
PLATFORM SUPPORTED
--------------------------------------------------------------------

This release supports the following 3 platforms:

Windows XP SP2 and above
Mac OS X (Intel) 10.4.8 and above
Linux (x86) 2.6.x and above

--------------------------------------------------------------------
SOFTWARE DEPENDENCIES
--------------------------------------------------------------------

Java Runtime Environment (JRE) 1.5.x must be installed on Mac OS X
prior to installing Zimbra Desktop.

Windows and Linux installers are bundled with JRE.

--------------------------------------------------------------------
INSTALLATION INSTRUCTIONS
--------------------------------------------------------------------

1. Run the GUI installer for the specific operating system.  Follow
   the instructions.

2. Make sure the location where you install the software has enough
   disk space.  If you plan to sync with a large mailbox, it is
   important to allocation enough space to download all your mailbox
   data. The data will be save under where you install the software.
   You must also have sufficient priviledge to install to the
   selected folder.

3. On Windows or Mac OS X you can choose to create a launcher icon on
   desktop which makes it easy to start up Zimbra Desktop UI.

4. On Windows or Mac OS X you can optionally choose to install Zimbra
   Toaster, a new mail notifier.  If selected, on Windows the Toaster
   installer will walk you through the steps; on Mac the Toaster
   application will be installed on your desktop.

5. On Windows the Zimbra Desktop backend will be installed as a
   Windows service.  This way the backend will be automatically
   started upon reboot.

6. After the successful installation of all files, the installer will
   start the Zimbra Desktop backend.  On Windows and Mac OS X the
   installer will launch your default Web browser and go to the setup
   page.  On Linux you need to start the browser to te following URL:
   
       http://localhost:7633/zimbra/mail

   This URL works on all platforms.

7. Setup an account to sync with a Zimbra Server.  You must have
   network connectivity with the server you want to sync with when
   setting up the account.

8. Once the account is successfully setup, Zimbra Desktop starts to
   download your entire mailbox when remote server.  Depending on how
   big your mailbox is, this initial sync process can take hours. You
   can still use Zimbra Desktop when initial sync is in progress, but
   you may not see all items.  To start using Zimbra Desktop, press
   the "Launch" button at the last step of setup.

--------------------------------------------------------------------
USING THE SOFTWARE
--------------------------------------------------------------------

On Windows or Mac OS X if you have chosen to install a desktop icon
during installation, you can lauch Zimbra Desktop UI any time by
double clicking the icon.  On Mac OS X you can drag the Zimbra
Desktop icon to the Dock.

Alternatively, you can always point your Web browser to the
following URL:

    http://localhost:7633/zimbra/mail

You can use Zimbra Desktop regardless of network connectivity.  If
you don't have network connectivity however, you will not see the
latest incoming emails and your outgoing emails will not be
immediately delivered.  Incoming and outgoing messages will start
to flow again once you reconnect to a network.

You can close the browser window any time.  That only dismissed the
Zimbra Desktop UI.  The Zimbra Desktop backend will continue to
sync with the remote server as long as there's network connectivity.

On Windows or Mac OS X, if you have chosen to install Zimbra Toaster
you can run it any time.  On Windows Toaster sits in the system
tray; on Mac OS X Toaster resides in the system menu bar.

You need to configure Zimbra Toaster properly in order to receive
new message notifications.  On Windows use "localhost:7633" as server
name and don't check the "Use Secure Connection" box; on Mac OS X use
"http://localhost:7633" as the Server URL.  You must also fill out
the email address and password as you do when setting up the Zimbra
Desktop account.

During initial sync Zimbra Desktop will download all your existing
messages from remote server one by one.  If you run Zimbra Toaster
while initial sync is in progress you will receive too many
notifications.  Therefore it is recommended you not run Toaster
until initial sync is complete.

------------------------------------------------------------------
UNINSTALLATION
------------------------------------------------------------------

You can uninstall Zimbra Desktop by running the uninstaller.  The
uninstaller is located under the folder where you installed the
software.  On Windows you can also use "Add or Remove Programs" to
uninstall.

If Zimbra Desktop backend is running, the unistaller will stop it.

The uninstaller will not delete any account or mailbox data.  This
allows you to install Zimbra Desktop software, either the same or
newer version, over the existing data in the future.  That way you
don't have to sync the entire mailbox from the beginning.

If you want to remove the account and mailbox data to recover the
disk space, you can manually delete the folder where you installed
the software after uninstallation.

--------------------------------------------------------------------
WHEN SOMETHING GOES WRONG
--------------------------------------------------------------------

1. Sometimes it helps to hit the "Refresh" button of your Web
   browser.

2. Make sure the Zimbra Desktop backend is running.  On Windows you
   can use Task Manager to check the zdesktop.exe process.  On Mac
   OS X and Linux it's a java process.

3. Sometimes it helps to restart the Zimbra Desktop backend.  To
   restart without rebooting the operating system follow these
   steps:

   On Windows use Control Panel -> Administrative Tools -> Services
   to stop and then start the Zimbra Desktop Service.
  
   On Mac OS X or Linux, run Terminal and change directory to the
   folder where you installed the software.  Run the following
   commands on the command line:

       ./zdesktop stop
       ./zdesktop start
   
4. Zimbra Desktop backend writes debug log to the following file:

       <install>/log/mailbox.log

   where <install> is the folder where you installed the software.

5. If you would like to start over with a new installation, follow
   these steps:

   - Run uninstaller.
   - Make sure Zimbra Desktop backend process is no longer running.
     The uninstaller terminates the backend process automatically,
     but it doesn't hurt to double check.
   - If you want a complete clean install, delete the folder where
     you previously installed the software.  This will remove all
     downloaded data from your computer disk.
   - Run installer.

====================================================================
SOURCE CODE INFORMATION
====================================================================

Zimbra Desktop is an open source project.  Source code is available
at http://sourceforge.net/projects/zimbra/

====================================================================
KNOWN LIMITATIONS 
====================================================================

1. DONOT install under any folder name with spaces.

2. The Web UI does not show any indication of whether initial sync
   is complete.  However usually it is easy to tell as you will
   have a lot of disk activity during initial sync.

====================================================================
CONTACT INFORMATION AND WEBSITE
====================================================================

Please use Zimbra Forums to post your questions and comments about
Zimbra Desktop.

http://www.zimbra.com/forums/

====================================================================
