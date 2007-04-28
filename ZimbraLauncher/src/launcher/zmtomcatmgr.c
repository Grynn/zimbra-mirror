/* -*- Mode: c; c-basic-offset: 4 -*- */
/*
 * ***** BEGIN LICENSE BLOCK *****
 * Version: MPL 1.1
 * 
 * The contents of this file are subject to the Mozilla Public License
 * Version 1.1 ("License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.zimbra.com/license
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See
 * the License for the specific language governing rights and limitations
 * under the License.
 * 
 * The Original Code is: Zimbra Collaboration Suite Server.
 * 
 * The Initial Developer of the Original Code is Zimbra, Inc.
 * Portions created by Zimbra are Copyright (C) 2006 Zimbra, Inc.
 * All Rights Reserved.
 * 
 * Contributor(s):
 * 
 * ***** END LICENSE BLOCK *****
 */

#define _GNU_SOURCE /* see <features.h> on Linux; we use this for > 2gb file support in open(2) */

#include <sys/types.h>
#include <sys/stat.h>
#include <sys/ioctl.h>
#include <sys/wait.h>
#include <errno.h>
#include <fcntl.h>
#include <signal.h>
#include <stdarg.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <syslog.h>
#include <pwd.h>
#include <time.h>
#ifdef DARWIN
#include <malloc/malloc.h>
#else
#include <malloc.h>
#endif


/* We pass through only a limited set of environment variables.  By
 * comparison, sudo strips only LD_LIBRARY_PATH.  We are being overly
 * cautious - intentionally - JDK implementation is very sensitive to
 * JAVA_HOME in particular, but there are a lot of VM/JDK back door
 * environment variables.
 */
static const char *AllowedEnv[] = {
    "HOSTNAME",
    "DISPLAY",
    "HOME",
    "LANG",
    "PATH",
    "LOGNAME",
    "USER"
};

extern char **environ;

static int
IsAllowedEnv(const char *env)
{
    int alsize =  sizeof(AllowedEnv) / sizeof(char *);
    int i;
    for (i = 0; i < alsize; i++) {
	int compareLen = strlen(AllowedEnv[i]);
	if (strncmp(env, AllowedEnv[i], compareLen) == 0) {
	    if (env[compareLen] == '=') {
		return 1;
	    }
	}
    }
    return 0;
}

static void
StripEnv()
{
    int i, currentEnvSize = 0;
    char **newEnv, **iter;

    iter = environ;
    while (*iter != NULL) {
	currentEnvSize++;
	iter++;
    }

    /* +1 for terminator NULL */
    newEnv = (char **)calloc(currentEnvSize+1, sizeof(*newEnv));
    if (newEnv == NULL) {
	environ = NULL;
	return;
    }

    int newEnvNextLocation = 0;
    for (i = 0; i < currentEnvSize; i++) {
	if (IsAllowedEnv(environ[i])) {
	    newEnv[newEnvNextLocation++] = environ[i];
	}
    }

    environ = newEnv;
}

#ifndef UNRESTRICTED_JVM_ARGS
/* Allow only known safe VM options.  This might to too conservative -
 * we could just disallow the potentially harmful ones such as
 * -classpath and -Xrun, ie anything that causes code to be loaded
 * from specified path.  For now maintain an allowed list, which is
 * the safer choice.
 */
static const char *AllowedJVMArgs[] = {

    "-XX:SoftRefLRUPolicyMSPerMB",
    "-XX:+AggressiveOpts",
    "-XX:+AggressiveHeap",
    "-XX:+AllowUserSignalHandlers",
    "-XX:+Print",
    "-XX:+HeapDumpOnOutOfMemoryError",
    "-XX:HeapDumpPath",
    "-XX:ErrorFile",
    "-XX:+TraceClassLoading",
    "-XX:+TraceClassUnloading",
    "-XX:+DisableExplicitGC",
    "-XX:+UseParallelGC",
    "-XX:+UseParallelOldGC",
    "-XX:ParallelGCThreads",
    "-XX:+UseAdaptiveSizePolicy",
    "-XX:+UseConcMarkSweepGC",
    "-XX:+UseParNewGC",
    "-XX:+UseCMSCompactAtFullCollection",
    "-XX:+CMSParallelRemarkEnabled",
    "-XX:CMSInitiatingOccupancyFraction",
    "-XX:+UseCMSInitiatingOccupancyOnly",
    "-XX:CMSFullGCsBeforeCompaction",
    "-XX:MinHeapFreeRatio",
    "-XX:MaxHeapFreeRatio",
    "-XX:NewRatio",
    "-XX:NewSize",
    "-XX:MaxNewSize",
    "-XX:PermSize",
    "-XX:MaxPermSize",
    "-XX:SurvivorRatio",
    "-XX:TargetSurvivorRatio",
    "-XX:MaxTenuringThreshold",
    "-XX:PretenureSizeThreshold",
    "-XX:+UseSpinning",
    "-XX:PreBlockSpin",
    "-XX:+UseBiasedLocking",
    "-XX:+UseThreadPriorities",
    "-XX:+UseTLAB",
    "-XX:+ResizeTLAB",
    "-XX:+UseISM",
    "-XX:+UseLargePages",
    "-XX:LargePageSizeInBytes",
    "-XX:CompileThreshold",
    "-XX:ReservedCodeCacheSize",
    "-XX:-OmitStackTraceInFastThrow",
    "-Xcomp",
    "-Xconcgc",
    "-Xincgc",
    "-Xnoincgc",
    "-Xloggc",
    "-Xmn",
    "-Xms",
    "-Xmx",
    "-Xrs",
    "-Xrunhprof",
    "-Xss",
    "-client",
    "-d32",
    "-d64",
    "-da",
    "-dsa",
    "-ea",
    "-esa",
    "-fullversion",
    "-server",
    "-showversion",
    "-verbose",
    "-version",
    "-Djava.awt.headless"
};
#endif

static char **newArgv;
static int newArgCount = 0;
static int newArgCapacity = 0;

static int
IsAllowedJVMArg(const char *arg)
{
#ifndef UNRESTRICTED_JVM_ARGS
    int alsize =  sizeof(AllowedJVMArgs) / sizeof(char *);
    int i;
    for (i = 0; i < alsize; i++) {
        int compareLen = strlen(AllowedJVMArgs[i]);
        if (strncmp(arg, AllowedJVMArgs[i], compareLen) == 0) {
            return 1;
        }
    }
    return 0;
#else
    return 1;
#endif
}

static void
NewArgEnsureCapacity(int thisManyMore)
{
    if (newArgCapacity == 0) {
	newArgCapacity = 32;
	newArgv = (char **)malloc(sizeof(*newArgv) * newArgCapacity);
    }
  
    /* the -1 here is so we have room to stick a NULL to terminate newArgv */
    if ((newArgCapacity - 1) <= (newArgCount + thisManyMore)) {
	newArgCapacity = newArgCapacity * 2;
	newArgv = (char **)realloc(newArgv, newArgCapacity);
    }
}

static void 
AddArgInternal(char *arg)
{
    NewArgEnsureCapacity(1);
    newArgv[newArgCount++] = arg;
    newArgv[newArgCount] = NULL;
}

static void
AddArg(const char *fmt, ...)
#ifdef __GNUC__
    __attribute__((format(printf, 1, 2)))
#endif
    ;

extern void
syslog(int priority, const char *message, ...)
#ifdef __GNUC__
    __attribute__((format(printf, 2, 3)))
#endif
    ;

static void
AddArg(const char *fmt, ...)
{
    char buf[1024];
    va_list ap;
    va_start(ap, fmt);
    vsnprintf(buf, sizeof(buf), fmt, ap);
    va_end(ap);
    AddArgInternal(strdup(buf));
}

static void
ShowNewArgs()
{
    char **e = newArgv;
    int i = 0;
    while (*e != NULL) {
	syslog(LOG_DEBUG, "tomcat/JVM arg: [%2d] %s", i, *e);
	i++;
	e++;
    }
}

static void
ShowNewEnv()
{
    char **e = environ;
    while (*e != NULL) {
	syslog(LOG_DEBUG, "tomcat/JVM env: %s", *e);
	e++;
    }
}

/*
 * Grace Interval: amount of time in seconds we wait for the
 * tomcat/JVM process to respond to SIGTERM before sending a SIGKILL.
 * Also, after a SIGKILL has been sent (because there was no response
 * to SIGTERM), if another grace interval has elapsed and the
 * tomcat/JVM process has still not shutdown, the manager process
 * exits after logging a severe error.  Default is 60 seconds.
 */
#define GRACE_INTERVAL_OPTION "--grace-interval="
static int GraceInterval = 60;

/*
 * Double Exit Interval: if they tomcat/JVM abnormally shuts down
 * twice in this interval, the manager process does not restart the
 * tomcat/JVM again and instead just exits.  If there is a fatal
 * configuration error that causes the tomcat/JVM to shutdown so
 * quickly, it does make sense to keep restarting it over and over
 * again and be a CPU hog.  Default is 60 seconds.
 */
#define DOUBLE_EXIT_INTERVAL_OPTION "--double-exit-interval="
static int DoubleExitInterval = 60;

#define VERBOSE_OPTION "--verbose"
static int Verbose = 0;

/*
 * Whether the signal received by the manager process was to shutdown
 * or to bounce the tomcat/JVM.
 */
static int ShutdownRequested = 0;
static int BounceRequested = 0;

/*
 * The process id of the tomcat/JVM process.
 */
static pid_t TomcatPid = 0;

static pid_t
GetPidOfRunningManagerInstance()
{
    FILE *fp = NULL;
    struct stat sb;
    pid_t pid;

    if (stat(MANAGER_PIDFILE, &sb) < 0) {
	if (errno == ENOENT) {
	    syslog(LOG_INFO, "file %s does not exist", MANAGER_PIDFILE);
	    goto NO_INSTANCE;
	}

	/* Failed for some other reason: perm denied, etc */
	syslog(LOG_ERR, "stat(%s) failed: %s", MANAGER_PIDFILE, strerror(errno));
	exit(1);
    }

    if (!S_ISREG(sb.st_mode)) {
	syslog(LOG_ERR, "%s is not a regular file: %s", MANAGER_PIDFILE, strerror(errno));
	exit(1);
    }
    
    fp = fopen(MANAGER_PIDFILE, "r");
    if (fp == NULL) {
	syslog(LOG_WARNING, "fopen(%s) failed: %s", MANAGER_PIDFILE, strerror(errno));
	goto NO_INSTANCE;
    }

    if (fscanf(fp, "%d", &pid) < 0) {
	syslog(LOG_WARNING, "did not find a number in %s", MANAGER_PIDFILE);
	goto NO_INSTANCE;
    }

    if (kill(pid, 0) < 0) {
	syslog(LOG_INFO, "stale pid %d found in %s: %s", pid, MANAGER_PIDFILE, strerror(errno));
	goto NO_INSTANCE;
    }

    /* PID found, and there is an associated process. */
    if (fp != NULL) {
	fclose(fp);
    }
    return pid;

 NO_INSTANCE:
    if (fp != NULL) {
	fclose(fp);
    }
    syslog(LOG_INFO, "assuming no other instance is running");
    return -1;
}

/* Note that the PID that is written is the PID of the launcher
 * process, not that of the JVM. */
static void
RecordManagerPid()
{
    int pidfd = -1;
    char buf[64];
    int len, wrote;
    pid_t pid;

    /* Reset the mask so the pid file has the exact permissions we say
     * it should have. */
    umask(0);
  
    pidfd = creat(MANAGER_PIDFILE, S_IRUSR | S_IWUSR | S_IRGRP | S_IROTH);
    if (pidfd < 0) {
	syslog(LOG_ERR, "could not create %s: %s", MANAGER_PIDFILE, strerror(errno));
	exit(1);
    }

    pid = getpid();
    len = snprintf(buf, sizeof(buf), "%d\n", pid);
    wrote = write(pidfd, buf, len);
    if (wrote != len) {
	syslog(LOG_ERR, "wrote only %d of %d to %s: %s", wrote, len, MANAGER_PIDFILE, strerror(errno));
	exit(1);
    }

    syslog(LOG_INFO, "wrote manager pid %d to %s", pid, MANAGER_PIDFILE);
    close(pidfd);
}

static void
StartTomcat()
{
    int fd;
    struct passwd *pw;

    if ((TomcatPid = fork()) != 0) {
	/* In parent process (manager) */
	return;
    }

    /* In child process (tomcat/JVM) */

    /* Redirect tomcat stdout and stderr to catalina.out */
    fd = open(TOMCAT_OUTFILE, O_APPEND | O_LARGEFILE);
    if (fd >= 0) {
	dup2(fd, fileno(stdout));
	dup2(fd, fileno(stderr));

	/* Change catalina.out ownership */
	pw = getpwnam(ZIMBRA_USER);
	if (pw) {
	    fchown(fd, pw->pw_uid, pw->pw_gid);
	} else {
	    syslog(LOG_WARNING, "can't change ownership of %s: user %s not found: %s", TOMCAT_OUTFILE, ZIMBRA_USER, strerror(errno));
	}

	close(fd);
    } else {
	syslog(LOG_WARNING, "opening output file %s failed: %s", TOMCAT_OUTFILE, strerror(errno));
    }

    fclose(stdin);

    /* It is customary to not inherit umask and to clear the umask
       completely so applications can set whatever exact permissions it
       is that they want. However, Java programs can not set permissions
       for new files, so we default the mask to something reasonable. */
    umask(027);
  
#ifdef DARWIN
    {
	int tfd;
	setpgrp(0, getpid());
	if ((tfd = open("/dev/tty", O_RDWR)) >= 0) {
	    ioctl(tfd, TIOCNOTTY, (char *)0); /* lose control tty */
	    close(tfd);
	}
    }
#else
    setpgrp();
#endif

    execv(JAVA_BINARY, newArgv);
}

/*
 * Try to gracefully stop tomcat/JVM by sending it a SIGTERM.  If that
 * fails, send it a SIGKILL.  If even that fails, then abort the
 * manager process, something has occurred.
 */
static void
StopTomcat()
{
    int i = 0;

    if (kill(TomcatPid, SIGTERM) < 0) {
	syslog(LOG_INFO, "tomcat/JVM process is not running (kill: %s)", strerror(errno));
	return;
    }

    for (i = 0; i < GraceInterval; i++) {
	pid_t res;
	int status;
	sleep(1);
	res = waitpid(TomcatPid, &status, WNOHANG);
	if (res == -1) {
	    syslog(LOG_INFO, "tomcat/JVM process not running (waitpid: %s)", strerror(errno));
	    return;
	} else if (res > 0) {
	    syslog(LOG_INFO, "tomcat/JVM process exited (waitpid expected %d got %d)", TomcatPid, res);
	    return;
	}
	/* Loop to wait another second ... */
    }

    syslog(LOG_INFO, "tomcat/JVM process did not exit after %d seconds, sending SIGKILL", GraceInterval);
    if (kill(TomcatPid, SIGKILL) < 0) {
	syslog(LOG_INFO, "tomcat/JVM process is not running (kill: %s)", strerror(errno));
	return;
    }

    for (i = 0; i < GraceInterval; i++) {
	pid_t res;
	int status;
	sleep(1);
	res = waitpid(TomcatPid, &status, WNOHANG);
	if (res == -1) {
	    syslog(LOG_INFO, "tomcat/JVM process not running (waitpid: %s)", strerror(errno));
	    return;
	} else if (res > 0) {
	    syslog(LOG_INFO, "tomcat/JVM process exited (waitpid expected %d got %d)", TomcatPid, res);
	    return;
	}
	/* Loop and wait another second ... */
    }
	
    /* Not sure what to do here, even SIGKILL failed. */
    syslog(LOG_ERR, "manager exiting: tomcat/JVM process could not be killed");
    exit(1);
}

static void
CheckJavaBinaryExists()
{
    struct stat sb;

    if (stat(JAVA_BINARY, &sb) < 0) {
	syslog(LOG_ERR, "stat failed for java binary: %s: %s", JAVA_BINARY, strerror(errno));
	exit(1);
    }

    if (sb.st_uid == getuid()) {
	if (!(sb.st_mode & S_IXUSR)) {
	    syslog(LOG_ERR, "java binary is not executable by user: %s", JAVA_BINARY);
	    exit(1);
	}
    } else {
	if (!(sb.st_mode & S_IXOTH)) {
	    syslog(LOG_ERR, "java binary is not executable by other: %s", JAVA_BINARY);
	    exit(1);
	}
    }
}

/* This signal handler shuts down the tomcat/JVM process.  When it
 * returns the tomcat/JVM process should not be running. */
static void
StopHandler(int signal) 
{
    if (signal == SIGTERM) {
	ShutdownRequested = 1;
	syslog(LOG_INFO, "shutdown requested, sending TERM signal to %d", TomcatPid);
    } else {
	BounceRequested = 1;
	syslog(LOG_INFO, "bounce requested, sending TERM signal to %d", TomcatPid);
    }

    StopTomcat();
}

static void
ThreadDumpHandler(int signal)
{
    syslog(LOG_INFO, "sending SIQUIT to tomcat/JVM process %d", TomcatPid);
    if (kill(TomcatPid, SIGQUIT) < 0) {
	syslog(LOG_INFO, "tomcat/JVM process is not running (kill: %s)", strerror(errno));
	return;
    }
}

static void
Start(int nextArg, int argc, char *argv[])
{
    int i;
    time_t lastExit = 0;
    pid_t otherInstance;

    CheckJavaBinaryExists();

    syslog(LOG_INFO, "checking if another instance of manager is already running");
    otherInstance = GetPidOfRunningManagerInstance();
    if (otherInstance != -1) {
	syslog(LOG_ERR, "another instance of manager is already running (pid=%d)", otherInstance);
	exit(1);
    }

    /* first argument must be name of binary */
    AddArg(JAVA_BINARY);
    
    for (i = nextArg; i < argc; i++) {
	if (IsAllowedJVMArg(argv[i])) {
	    AddArg(argv[i]);
	} else {
	    syslog(LOG_ERR, "JVM option: %s: not allowed\n", argv[i]);
	    exit(1);
	}
    }
    
    /* REMIND: Do we need this?  Seems applicable only when -jar option
     * is present?
     * AddArg("-jre-no-restrict-search");
     */
    
    AddArg("-Dcatalina.base=%s", TOMCAT_HOME);
    AddArg("-Dcatalina.home=%s", TOMCAT_HOME);
    AddArg("-Djava.io.tmpdir=%s/temp", TOMCAT_HOME); 
    AddArg("-Djava.library.path=%s", ZIMBRA_LIB);
    AddArg("-Djava.endorsed.dirs=%s/common/endorsed", TOMCAT_HOME);
    AddArg("-classpath");
    AddArg("%s/bin/bootstrap.jar:%s/bin/commons-logging-api.jar:%s/jars/zimbra-launcher.jar", 
	   TOMCAT_HOME, TOMCAT_HOME, ZIMBRA_LIB);
    AddArg("com.zimbra.cs.launcher.TomcatLauncher");

    if (Verbose) {
	ShowNewEnv();
	ShowNewArgs();
    }
    
    /* Now daemonize the manager process. */
    if (fork() != 0) {
	exit(0);
    }

    /* We are in manager child process. */

    setsid();

    chdir(TOMCAT_HOME);
  
    RecordManagerPid();
    
    /* On SIGTERM, we set ShutdownRequested to true. */
    signal(SIGTERM, StopHandler);
    
    /* On SIGHUP, we go ahead and shutdown tomcat, but do not set
       ShutdownRequested to true. */
    signal(SIGHUP, StopHandler);
    
    /* On SIGQUIT, we forward the SIGQUIT on to the tomcat/JVM
       process. */
    signal(SIGQUIT, ThreadDumpHandler);

    while (1) {
	StartTomcat();
	
	syslog(LOG_INFO, "manager started tomcat/JVM with pid %d", TomcatPid);
	wait(NULL);
	syslog(LOG_INFO, "manager woke up from wait on tomcat/JVM with pid %d", TomcatPid);
	if (ShutdownRequested) {
	    unlink(MANAGER_PIDFILE);
	    break;
	}
	
	if (BounceRequested) {
	    BounceRequested = 0;
	    lastExit = 0;
	    /* Pretend as though the process has never crashed before,
	       by not setting lastExit. */
	    continue;
	}
	
	if (lastExit > 0) {
	    time_t now = time(NULL);
	    if ((now - lastExit) < DoubleExitInterval) {
		syslog(LOG_ERR, "tomcat/JVM exited twice in %d seconds (tolerance=%d)",
		       (int)(now - lastExit), DoubleExitInterval);
		exit(1);
	    }
	    /* Any subsequent time child crashed ... */
	    lastExit = now;
	} else {
	    /* First time child crashed... */
	    lastExit = time(NULL);
	}
    }
}

static void
Usage(const char *progname) {
    syslog(LOG_ERR, "Incorrect arguments. Usage: %s [%sseconds] [%sseconds] [%s] { start | stop | restart | status | threaddump } [allowed JVM options ... ]  ",
	   progname, DOUBLE_EXIT_INTERVAL_OPTION, GRACE_INTERVAL_OPTION, VERBOSE_OPTION);
    exit(1);
}

static void
Stop()
{
    int i;
    pid_t managerPid;

    managerPid = GetPidOfRunningManagerInstance();
    if (managerPid == -1) {
	syslog(LOG_ERR, "no manager process is running");
	exit(1);
    }
    if (kill(managerPid, SIGTERM) < 0) {
	syslog(LOG_ERR, "could not send SIGTERM to manager process (pid=%d): %s", managerPid, strerror(errno));
	exit(1);
    }

    syslog(LOG_INFO, "waiting for manager process %d to die", managerPid);
    for (i = 0; i < GraceInterval; i++) {
	sleep(1);
	if (kill(managerPid, 0) < 0) {
	    syslog(LOG_INFO, "manager process %d died, shutdown completed", managerPid);
	    return;
	}
    }
    syslog(LOG_ERR, "manager process %d could not died in %d seconds", managerPid, GraceInterval);
}

static void
Restart()
{
    pid_t managerPid = GetPidOfRunningManagerInstance();
    if (managerPid == -1) {
	syslog(LOG_ERR, "no manager process is running");
	exit(1);
    }
    if (kill(managerPid, SIGHUP) < 0) {
	syslog(LOG_ERR, "could not send SIGHUP to manager process (pid=%d): %s", managerPid, strerror(errno));
	exit(1);
    }
}

/* Exit 0 or 1 based on whether manager is running */
static void
Status()
{
    pid_t managerPid = GetPidOfRunningManagerInstance();
    if (managerPid == -1) {
	syslog(LOG_ERR, "no manager process is running");
	exit(1);
    }
    if (kill(managerPid, 0) < 0) {
	syslog(LOG_ERR, "could not send signal 0 to manager process (pid=%d): %s", managerPid, strerror(errno));
	exit(1);
    }
    syslog(LOG_INFO, "status OK");
    exit(0);
}

static void
ThreadDump()
{
    pid_t managerPid = GetPidOfRunningManagerInstance();
    if (managerPid == -1) {
	syslog(LOG_ERR, "no manager process is running");
	exit(1);
    }
    if (kill(managerPid, SIGQUIT) < 0) {
	syslog(LOG_ERR, "could not send SIGQUIT to manager process (pid=%d): %s", managerPid, strerror(errno));
	exit(1);
    }
}

int
main(int argc, char *argv[])
{
    int nextArg;
    const char *progname;
    const char *action;

    progname = (argc > 0 && argv[0] != NULL) ? argv[0] : "zmtomcatmgr";
    if (strrchr(progname, '/') > 0) {
	progname = strrchr(progname, '/') + 1;
	if (*progname == '\0') {
	    progname = "zmtomcatmgr";
	}
    }
    openlog(progname, LOG_PID, LOG_MAIL);

    StripEnv();

    /* TODO: warn if files are not owned by root */

    nextArg = 1;
    action = NULL;
    while (nextArg < argc) {
	if (strncmp(argv[nextArg], DOUBLE_EXIT_INTERVAL_OPTION, sizeof(DOUBLE_EXIT_INTERVAL_OPTION)) == 0) {
	    char *value = argv[nextArg] + sizeof(DOUBLE_EXIT_INTERVAL_OPTION);
	    DoubleExitInterval = atoi(value);
	    if (DoubleExitInterval < 1) {
		Usage(progname);
	    }
	} else if (strncmp(argv[nextArg], GRACE_INTERVAL_OPTION, sizeof(GRACE_INTERVAL_OPTION)) == 0) {
	    char *value = argv[nextArg] + sizeof(GRACE_INTERVAL_OPTION);
	    GraceInterval = atoi(value);
	    if (GraceInterval < 1) {
		Usage(progname);
	    }
	} else if (strcmp(argv[nextArg], VERBOSE_OPTION) == 0) {
	    Verbose = 1;
	} else {
	    action = argv[nextArg++];
	    break;
	}
	nextArg++;
    }
    if (action == NULL) { 
	Usage(progname);
    }

    if (strcmp(action, "start") == 0) {
	syslog(LOG_INFO, "start requested");
	Start(nextArg, argc, argv);
    } else if (strcmp(action, "stop") == 0) {
	syslog(LOG_INFO, "stop requested");
	Stop();
    } else if (strcmp(action, "restart") == 0) {
	syslog(LOG_INFO, "restart requested");
	Restart();
    } else  if (strcmp(action, "status") == 0) {
	syslog(LOG_INFO, "status requested");
	Status();
    } else  if (strcmp(action, "threaddump") == 0) {
	syslog(LOG_INFO, "threaddump requested");
	ThreadDump();
    } else {
	syslog(LOG_ERR, "unknown action %s requested", action);
	exit(1);
    }
    return 0;
}
