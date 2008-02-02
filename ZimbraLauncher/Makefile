# Build mailboxd launcher and manager process.  Note that paths you
# specify here must not be owned in the install by less privileged
# user who could then hijack this launcher binary.  The defaults are
# bad bad bad, as those symlinks might not be owned by root.

SRC     = src

BUILD   = build

BUILD_ROOT := $(shell pwd)

BUILD_PLATFORM := $(shell sh $(BUILD_ROOT)/../ZimbraBuild/rpmconf/Build/get_plat_tag.sh)

ifeq ($(BUILD_PLATFORM), MACOSX)
MACDEF := -DDARWIN
JAVA_BINARY = /usr/bin/java
endif

ifeq (MACOSXx86,$(findstring MACOSXx86,$(BUILD_PLATFORM)))   
ifeq ($(BUILD_PLATFORM), MACOSXx86)
MACDEF := -DDARWIN
else
MACDEF := -DDARWIN9
endif
JAVA_BINARY = /usr/bin/java
endif


all: $(BUILD) $(BUILD)/zmmailboxdmgr $(BUILD)/zmmailboxdmgr.unrestricted

$(BUILD):
	mkdir $(BUILD)

JAVA_BINARY ?= /opt/zimbra/java/bin/java
MANAGER_PIDFILE ?= /opt/zimbra/log/zmmailboxd.pid
MAILBOXD_CWD ?= /opt/zimbra/log
MAILBOXD_HOME ?= /opt/zimbra/mailboxd
MAILBOXD_OUTFILE ?= /opt/zimbra/log/zmmailboxd.out
ZIMBRA_LIB ?= /opt/zimbra/lib
ZIMBRA_USER ?= zimbra
ZIMBRA_CONFIG ?= /opt/zimbra/conf/localconfig.xml

LAUNCHER_CFLAGS = \
	-DJAVA_BINARY='"$(JAVA_BINARY)"' \
	-DMANAGER_PIDFILE='"$(MANAGER_PIDFILE)"' \
	-DMAILBOXD_CWD='"$(MAILBOXD_CWD)"' \
	-DMAILBOXD_HOME='"$(MAILBOXD_HOME)"' \
	-DMAILBOXD_OUTFILE='"$(MAILBOXD_OUTFILE)"' \
	-DZIMBRA_LIB='"$(ZIMBRA_LIB)"' \
	-DZIMBRA_USER='"$(ZIMBRA_USER)"' \
	-DZIMBRA_CONFIG='"$(ZIMBRA_CONFIG)"'

ifeq ($(ZIMBRA_USE_TOMCAT), 1)
LAUNCHER_CFLAGS += -DZIMBRA_USE_TOMCAT=1
endif

$(BUILD)/zmmailboxdmgr: $(SRC)/launcher/zmmailboxdmgr.c
	gcc $(MACDEF) $(LAUNCHER_CFLAGS) -Wall -Wmissing-prototypes -o $@ $<

$(BUILD)/zmmailboxdmgr.unrestricted: $(SRC)/launcher/zmmailboxdmgr.c
	gcc $(MACDEF) $(LAUNCHER_CFLAGS) -DUNRESTRICTED_JVM_ARGS -Wall -Wmissing-prototypes -o $@ $<

#
# Clean
#
clean:
	$(RM) -r $(BUILD)

FORCE: ;

