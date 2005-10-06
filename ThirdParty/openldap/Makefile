OPENLDAP_ROOT := $(shell pwd)
P4_ROOT ?= $(shell cd $(OPENLDAP_ROOT)/../..; pwd)

BUILD_PLATFORM ?= $(shell sh $(P4_ROOT)/ZimbraBuild/rpmconf/Build/get_plat_tag.sh)

BDB_ROOT ?= $(P4_ROOT)/ThirdParty/sleepycat

BDB_VERSION ?= 4.2.52

ifeq ($(BUILD_PLATFORM), )
	BUILD_PLATFORM := "UNKNOWN"
endif

ZIMBRA_HOME ?= /opt/zimbra
LDAP_VERSION ?= 2.2.28

all: build tar

build:
	(tar xzf openldap-$(LDAP_VERSION).tgz; \
	cd openldap-$(LDAP_VERSION); \
	export LD_LIBRARY_PATH=/opt/zimbra/sleepycat-$(BDB_VERSION)/lib; \
	export CPPFLAGS=-I/opt/zimbra/sleepycat-$(BDB_VERSION)/include; \
	export LDFLAGS=-L/opt/zimbra/sleepycat-$(BDB_VERSION)/lib; \
	cp $(OPENLDAP_ROOT)/configure.works $(OPENLDAP_ROOT)/openldap-$(LDAP_VERSION); \
	./configure.works --prefix=$(ZIMBRA_HOME)/openldap-$(LDAP_VERSION); \
	make; make install)

tar:
	mkdir -p $(OPENLDAP_ROOT)/builds/$(BUILD_PLATFORM)
	(cd $(ZIMBRA_HOME); tar czf $(OPENLDAP_ROOT)/builds/$(BUILD_PLATFORM)/openldap-$(LDAP_VERSION).tgz openldap-$(LDAP_VERSION))

clean:
	/bin/rm -rf openldap-$(LDAP_VERSION)

allclean: clean
	/bin/rm -rf $(ZIMBRA_HOME)/openldap-$(LDAP_VERSION)
