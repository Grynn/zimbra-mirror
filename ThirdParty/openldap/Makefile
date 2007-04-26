OPENLDAP_ROOT := $(shell pwd)
P4_ROOT ?= $(shell cd $(OPENLDAP_ROOT)/../..; pwd)

BUILD_PLATFORM ?= $(shell sh $(P4_ROOT)/ZimbraBuild/rpmconf/Build/get_plat_tag.sh)

BDB_ROOT ?= $(P4_ROOT)/ThirdParty/sleepycat

BDB_VERSION ?= 4.2.52.4

ifeq ($(BUILD_PLATFORM), )
	BUILD_PLATFORM := "UNKNOWN"
endif

ifeq ($(BUILD_PLATFORM), MACOSXx86)
	ENVMOD := env LIBS="-lresolv" 
endif
ifeq ($(BUILD_PLATFORM), MACOSX)
	ENVMOD := env LIBS="-lresolv" 
endif

ZIMBRA_HOME ?= /opt/zimbra
LDAP_VERSION ?= 2.3.34
SASL_VERSION ?= 2.1.21.ZIMBRA
LDAP_TGZ_TARGET := $(OPENLDAP_ROOT)/builds/$(BUILD_PLATFORM)/openldap-$(LDAP_VERSION).tgz

all: build tar

build:
	(tar xzf openldap-$(LDAP_VERSION).tgz; \
	cd openldap-$(LDAP_VERSION); \
	LD_LIBRARY_PATH=/opt/zimbra/sleepycat-$(BDB_VERSION)/lib \
	CPPFLAGS="-I/opt/zimbra/sleepycat-$(BDB_VERSION)/include -I/usr/kerberos/include -I/opt/zimbra/cyrus-sasl-$(SASL_VERSION)/include" \
	LDFLAGS="-L/opt/zimbra/sleepycat-$(BDB_VERSION)/lib -L/opt/zimbra/cyrus-sasl-$(SASL_VERSION)/lib" \
	$(ENVMOD) ./configure --prefix=$(ZIMBRA_HOME)/openldap-$(LDAP_VERSION) \
	--enable-overlays --enable-debug --enable-crypt; \
	LDFLAGS="-all-static -static" make; make install)
	(cd $(ZIMBRA_HOME); ln -s openldap-$(LDAP_VERSION) openldap)

tar:
	mkdir -p $(OPENLDAP_ROOT)/builds/$(BUILD_PLATFORM)
	(cd $(ZIMBRA_HOME); tar czf $(OPENLDAP_ROOT)/builds/$(BUILD_PLATFORM)/openldap-$(LDAP_VERSION).tgz openldap-$(LDAP_VERSION))

p4edit: $(LDAP_TGZ_TARGET)
	p4 add $(LDAP_TGZ_TARGET)
	p4 edit $(LDAP_TGZ_TARGET)
	

clean:
	/bin/rm -rf openldap-$(LDAP_VERSION)

allclean: clean
	/bin/rm -rf $(ZIMBRA_HOME)/openldap-$(LDAP_VERSION)
	/bin/rm -rf $(ZIMBRA_HOME)/openldap
