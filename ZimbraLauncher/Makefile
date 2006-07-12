# Build tomcat launcher and manager process.  Note that paths you
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

ifeq ($(BUILD_PLATFORM), MACOSXx86)
MACDEF := -DDARWIN
JAVA_BINARY = /usr/bin/java
endif

all: $(BUILD)/zimbra-launcher.jar $(BUILD)/zmtomcatmgr $(BUILD)/zmtomcatmgr.unrestricted

$(BUILD)/zimbra-launcher.jar: FORCE
	ant jar

JAVA_BINARY ?= /opt/zimbra/java/bin/java
MANAGER_PIDFILE ?= /opt/zimbra/log/zmtomcatmgr.pid
TOMCAT_HOME ?= /opt/zimbra/tomcat
TOMCAT_OUTFILE ?= /opt/zimbra/tomcat/logs/catalina.out
ZIMBRA_LIB ?= /opt/zimbra/lib
ZIMBRA_USER ?= zimbra

LAUNCHER_CFLAGS = \
	-DJAVA_BINARY='"$(JAVA_BINARY)"' \
	-DMANAGER_PIDFILE='"$(MANAGER_PIDFILE)"' \
	-DTOMCAT_HOME='"$(TOMCAT_HOME)"' \
	-DTOMCAT_OUTFILE='"$(TOMCAT_OUTFILE)"' \
	-DZIMBRA_LIB='"$(ZIMBRA_LIB)"' \
	-DZIMBRA_USER='"$(ZIMBRA_USER)"'

$(BUILD)/zmtomcatmgr: $(SRC)/launcher/zmtomcatmgr.c
	gcc $(MACDEF) $(LAUNCHER_CFLAGS) -Wall -Wmissing-prototypes -o $@ $<

$(BUILD)/zmtomcatmgr.unrestricted: $(SRC)/launcher/zmtomcatmgr.c
	gcc $(MACDEF) $(LAUNCHER_CFLAGS) -DUNRESTRICTED_JVM_ARGS -Wall -Wmissing-prototypes -o $@ $<

#
# Clean
#
clean:
	$(RM) -r $(BUILD)

FORCE: ;

