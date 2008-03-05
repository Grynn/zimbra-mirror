/* ***** BEGIN LICENSE BLOCK *****
 * Version: MPL 1.1/GPL 2.0/LGPL 2.1
 *
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * The Original Code is Mozilla Application Update.
 *
 * The Initial Developer of the Original Code is
 * Benjamin Smedberg <benjamin@smedbergs.us>
 *
 * Portions created by the Initial Developer are Copyright (C) 2005
 * the Initial Developer. All Rights Reserved.
 *
 * Contributor(s):
 *  Darin Fisher <darin@meer.net>
 *
 * Alternatively, the contents of this file may be used under the terms of
 * either the GNU General Public License Version 2 or later (the "GPL"), or
 * the GNU Lesser General Public License Version 2.1 or later (the "LGPL"),
 * in which case the provisions of the GPL or the LGPL are applicable instead
 * of those above. If you wish to allow use of your version of this file only
 * under the terms of either the GPL or the LGPL, and not to allow others to
 * use your version of this file under the terms of the MPL, indicate your
 * decision by deleting the provisions above and replace them with the notice
 * and other provisions required by the GPL or the LGPL. If you do not delete
 * the provisions above, a recipient may use your version of this file under
 * the terms of any one of the MPL, the GPL or the LGPL.
 *
 * ***** END LICENSE BLOCK ***** */

#if defined(XP_WIN)
# include <windows.h>
# include <direct.h>
# include <io.h>
# define F_OK 00
# define W_OK 02
# define R_OK 04
# define access _access
# define putenv _putenv
# define snprintf _snprintf
# define fchmod(a,b)
# define mkdir(path, perms) _mkdir(path)

# define NS_T(str) L ## str
# define NS_tfprintf fwprintf
# define NS_tsnprintf _snwprintf
# define NS_tstrrchr wcsrchr
# define NS_tchdir _wchdir
# define NS_tremove _wremove
# define NS_trename _wrename
# define NS_taccess _waccess
# define NS_topen _wopen
# define NS_tfopen _wfopen
# define NS_tatoi _wtoi64
# define NS_main wmain
typedef WCHAR NS_tchar;
#else
# include <sys/wait.h>
# include <unistd.h>

# define NS_T(str) str
# define NS_tfprintf fprintf
# define NS_tsnprintf snprintf
# define NS_tstrrchr strrchr
# define NS_tchdir chdir
# define NS_tremove remove
# define NS_trename rename
# define NS_taccess access
# define NS_topen open
# define NS_tfopen fopen
# define NS_tatoi atoi
# define NS_main main
typedef char NS_tchar;
#endif

// We use the NSPR types, but we don't link with NSPR
#include "prtypes.h"

#include "errors.h"

#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <stdarg.h>

#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <limits.h>
#include <errno.h>

#if defined(XP_MACOSX)
// This function is defined in launchchild_osx.mm
void LaunchChild(int argc, char **argv);
#endif

#ifndef _O_BINARY
# define _O_BINARY 0
#endif

#ifndef NULL
# define NULL (0)
#endif

#ifndef SSIZE_MAX
# define SSIZE_MAX LONG_MAX
#endif

#ifndef MAXPATHLEN
# ifdef MAX_PATH
#  define MAXPATHLEN MAX_PATH
# elif defined(_MAX_PATH)
#  define MAXPATHLEN _MAX_PATH
# elif defined(CCHMAXPATH)
#  define MAXPATHLEN CCHMAXPATH
# else
#  define MAXPATHLEN 1024
# endif
#endif

// We want to use execv to invoke the callback executable on platforms where
// we were launched using execv.  See nsUpdateDriver.cpp.
#if defined(XP_UNIX) && !defined(XP_MACOSX)
#define USE_EXECV
#endif

//-----------------------------------------------------------------------------
// LOGGING

static FILE *gLogFP = NULL;

static void LogInit(NS_tchar *path)
{
  if (gLogFP)
    return;

  NS_tchar logFile[MAXPATHLEN];
  NS_tsnprintf(logFile, MAXPATHLEN, NS_T("%s/update.log"), path);

  gLogFP = NS_tfopen(logFile, NS_T("w"));
}

static void LogFinish()
{
  if (!gLogFP)
    return;

  fclose(gLogFP);
  gLogFP = NULL;
}

static void LogPrintf(const char *fmt, ... )
{
  if (!gLogFP)
    return;

  va_list ap;
  va_start(ap, fmt);
  vfprintf(gLogFP, fmt, ap);
  va_end(ap);
}

#define LOG(args) LogPrintf args

//-----------------------------------------------------------------------------

#ifdef XP_WIN
#include "nsWindowsRestart.cpp"
#endif

static void
LaunchApp(const NS_tchar *workingDir, int argc, NS_tchar **argv)
{
  putenv("NO_EM_RESTART=");
  putenv("MOZ_LAUNCHED_CHILD=1");

  // Run from the specified working directory (see bug 312360).
  NS_tchdir(workingDir);

#if defined(USE_EXECV)
  execv(argv[0], argv);
#elif defined(XP_MACOSX)
  LaunchChild(argc, argv);
#elif defined(XP_WIN)
  WinLaunchChild(argv[0], argc, argv, -1);
#else
# warning "Need implementaton of LaunchCallbackApp"
#endif
}

static void
WriteStatusFile(NS_tchar* path, int status)
{
  // This is how we communicate our completion status to the main application.

  NS_tchar filename[MAXPATHLEN];
  NS_tsnprintf(filename, MAXPATHLEN, NS_T("%s/update.status"), path);

  int fd = NS_topen(filename, O_WRONLY | O_TRUNC | O_CREAT | _O_BINARY, 0644);
  if (fd < 0)
    return;

  const char *text;

  char buf[32];
  if (status == OK) {
    text = "succeeded\n";
  } else {
    snprintf(buf, sizeof(buf), "failed: %d\n", status);
    text = buf;
  }
  write(fd, text, strlen(text));
  close(fd);
}

static void
DoUpdate(NS_tchar *path)
{
  NS_tchar spath[MAXPATHLEN];
  NS_tchar dpath[MAXPATHLEN];
  NS_tchar upstatus[MAXPATHLEN];
  NS_tsnprintf(spath, MAXPATHLEN, NS_T("%s/update.mar"), path);
  NS_tsnprintf(dpath, MAXPATHLEN, NS_T("%s/update.exe"), path);
  NS_tsnprintf(upstatus, MAXPATHLEN, NS_T("%s/update.status"), path);

  int rv = NS_taccess(spath, F_OK | R_OK | W_OK);
  if (rv != OK) {
  	LOG(("failed: can't access update.mar (rv=%d)", rv));
	NS_tremove(spath);
	NS_tremove(upstatus);
	return;
  }

  NS_tremove(dpath);
  rv = NS_trename(spath, dpath);
  if (rv != OK) {
  	LOG(("failed: can't rename update.mar (rv=%d)", rv));
	NS_tremove(spath);
	NS_tremove(upstatus);
	return;
  }

  NS_tchar *exe = dpath;
  LaunchApp(path, 1, &exe);

  LOG(("succeeded\n"));
  //WriteStatusFile(path, rv);
  NS_tremove(upstatus);
}

int NS_main(int argc, NS_tchar **argv)
{
  // The updater command line consists of the directory path containing the
  // updater.mar file to process followed by the PID of the calling process.
  // The updater will wait on the parent process to exit if the PID is non-
  // zero.  This is leveraged on platforms such as Windows where it is
  // necessary for the parent process to exit before its executable image may
  // be altered.

  //for (int i = 0; i < argc; ++i) {
  //	NS_tfprintf(stderr, NS_T("arg[%d]=%s\n"), i, argv[i]);
  //}

  if (argc < 3) {
    fprintf(stderr, "Usage: updater <dir-path> <parent-pid> [working-dir callback args...]\n");
    return 1;
  }


  int pid = NS_tatoi(argv[2]);
  if (pid) {
#ifdef XP_WIN
    HANDLE parent = OpenProcess(SYNCHRONIZE, FALSE, (DWORD) pid);
    // May return NULL if the parent process has already gone away.
    // Otherwise, wait for the parent process to exit before starting the
    // update.
    if (parent) {
      DWORD result = WaitForSingleObject(parent, 5000);
      CloseHandle(parent);
      if (result != WAIT_OBJECT_0)
        return 1;
      // The process may be signaled before it releases the executable image.
      // This is a terrible hack, but it'll have to do for now :-(
      Sleep(50);
    }
#else
    int status;
    waitpid(pid, &status, 0);
#endif
  }

  LogInit(argv[1]);

  DoUpdate(argv[1]);

  LogFinish();

  return 0;
}
