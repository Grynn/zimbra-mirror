OPENLDAP_ROOT := $(shell pwd)
P4_ROOT ?= $(shell cd $(OPENLDAP_ROOT)/../..; pwd)

BUILD_PLATFORM ?= $(shell sh $(P4_ROOT)/ZimbraBuild/rpmconf/Build/get_plat_tag.sh)

BDB_ROOT ?= $(P4_ROOT)/ThirdParty/sleepycat

BDB_VERSION ?= 4.2.52.6

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
LDAP_RELEASE ?= 2.3.36
LDAP_PATCH_LEVEL ?= 6
LDAP_VERSION ?= $(LDAP_RELEASE).$(LDAP_PATCH_LEVEL)
SASL_VERSION ?= 2.1.22.3
LDAP_TGZ_TARGET := $(OPENLDAP_ROOT)/builds/$(BUILD_PLATFORM)/openldap-$(LDAP_VERSION).tgz

all: build tar

build:
	(tar xzf openldap-$(LDAP_RELEASE).tgz; \
	mv openldap-$(LDAP_RELEASE) openldap-$(LDAP_VERSION); \
	cd openldap-$(LDAP_VERSION); \
	patch -p1 < ../internal-config; \
	patch -p1 < ../internal-new-malloc; \
	patch -p1 < ../internal-bdb-check; \
	patch -p1 < ../internal-add-improvements; \
	patch -p1 < ../ITS5025.patch; \
	patch -p1 < ../index-param.patch; \
	LD_LIBRARY_PATH=/opt/zimbra/sleepycat-$(BDB_VERSION)/lib \
	CPPFLAGS="-I/opt/zimbra/sleepycat-$(BDB_VERSION)/include -I/usr/kerberos/include -I/opt/zimbra/cyrus-sasl-$(SASL_VERSION)/include" \
	LDFLAGS="-L/opt/zimbra/sleepycat-$(BDB_VERSION)/lib -L/opt/zimbra/cyrus-sasl-$(SASL_VERSION)/lib" \
	CFLAGS="-g -O2 -DSLAP_LIGHTWEIGHT_DISPATCHER" \
	$(ENVMOD) ./configure --prefix=$(ZIMBRA_HOME)/openldap-$(LDAP_VERSION) \
	--enable-overlays --enable-debug --enable-crypt --enable-slurpd=no; \
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
