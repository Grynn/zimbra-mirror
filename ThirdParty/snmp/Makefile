APP_ROOT := $(shell pwd)
P4_ROOT ?= $(shell cd $(APP_ROOT)/../..; pwd)

BUILD_PLATFORM ?= $(shell sh $(P4_ROOT)/ZimbraBuild/rpmconf/Build/get_plat_tag.sh)
ZIMBRA_HOME ?= /opt/zimbra
APP_NAME=net-snmp
APP_VERSION ?= 5.4.1
APP_TGZ_TARGET := $(P4_ROOT)/ThirdPartyBuilds/$(BUILD_PLATFORM)/snmp/$(APP_NAME)-$(APP_VERSION).tgz
INSTALL_PREFIX := $(ZIMBRA_HOME)/$(APP_NAME)-$(APP_VERSION)

ifeq ($(BUILD_PLATFORM), )
	BUILD_PLATFORM := "UNKNOWN"
endif

ifeq ($(BUILD_PLATFORM), MACOSXx86)
	ENVMOD := env LIBS="-lresolv" 
endif
ifeq ($(BUILD_PLATFORM), MACOSX)
	ENVMOD := env LIBS="-lresolv" 
endif
ifeq ($(BUILD_PLATFORM), RHEL4_64)
	ENVMOD := env LIBS="-L/usr/lib64"
endif
ifeq ($(BUILD_PLATFORM), CentOS4_64)
	ENVMOD := env LIBS="-L/usr/lib64"
endif
ifeq ($(BUILD_PLATFORM), RHEL5_64)
	ENVMOD := env LIBS="-L/usr/lib64"
endif
ifeq ($(BUILD_PLATFORM), CentOS5_64)
	ENVMOD := env LIBS="-L/usr/lib64"
endif

all: build tar

build: $(APP_NAME)-$(APP_VERSION)
	(cd $(APP_NAME)-$(APP_VERSION); \
	$(ENVMOD) CFLAGS="-g -O2" ./configure --prefix=$(INSTALL_PREFIX) \
	--with-default-snmp-version=3 --with-sys-contact="admin" \
	--with-sys-location="unknown" --with-logfile="$(ZIMBRA_HOME)/log/snmpd.log" \
	--with-persistent-directory="$(INSTALL_PREFIX)/var"; \
	make; make install)
	(cp -f $(APP_NAME)-$(APP_VERSION)/COPYING $(INSTALL_PREFIX)/LICENSE)
	(mkdir -p $(INSTALL_PREFIX)/var)
	(cd $(ZIMBRA_HOME); ln -s $(APP_NAME)-$(APP_VERSION) $(APP_NAME))

$(APP_NAME)-$(APP_VERSION): 
	tar xzf src/$(APP_NAME)-$(APP_VERSION).tar.gz

tar:
	mkdir -p $(P4_ROOT)/ThirdPartyBuilds/$(BUILD_PLATFORM)/snmp
	(cd $(ZIMBRA_HOME); tar czf $(APP_TGZ_TARGET) $(APP_NAME)-$(APP_VERSION))

p4edit: $(APP_TGZ_TARGET)
	p4 add $(APP_TGZ_TARGET)
	p4 edit $(APP_TGZ_TARGET)
	

clean:
	/bin/rm -rf $(APP_NAME)-$(APP_VERSION)

allclean: clean
	/bin/rm -rf $(ZIMBRA_HOME)/$(APP_NAME)-$(APP_VERSION)
	/bin/rm -rf $(ZIMBRA_HOME)/$(APP_NAME)
