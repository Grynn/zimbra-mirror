OPENLDAP_ROOT := $(shell pwd)
P4_ROOT ?= $(shell cd $(OPENLDAP_ROOT)/../..; pwd)

BUILD_PLATFORM ?= $(shell sh $(P4_ROOT)/ZimbraBuild/rpmconf/Build/get_plat_tag.sh)


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
LDAP_RELEASE ?= 2.3.39
LDAP_PATCH_LEVEL ?= 6z
LDAP_VERSION ?= $(LDAP_RELEASE).$(LDAP_PATCH_LEVEL)
LDAP_TGZ_TARGET := $(P4_ROOT)/ThirdPartyBuilds/$(BUILD_PLATFORM)/openldap/openldap-$(LDAP_VERSION).tgz
LDAP_LIB_TGZ_TARGET := $(P4_ROOT)/ThirdPartyBuilds/$(BUILD_PLATFORM)/openldap/openldap-clibs-$(LDAP_VERSION).tgz

CYRUS_VERSION ?= 2.1.22.3z
BDB_VERSION ?= 4.2.52.6
OPENSSL_VERSION ?= 0.9.8e

CYRUS_LIB_DIR ?= $(ZIMBRA_HOME)/cyrus-sasl-$(CYRUS_VERSION)/lib
BDB_LIB_DIR ?= $(ZIMBRA_HOME)/sleepycat-$(BDB_VERSION)/lib
OPENSSL_LIB_DIR ?= $(ZIMBRA_HOME)/openssl-$(OPENSSL_VERSION)/lib
OPENLDAP_CLIB_DIR ?= $(ZIMBRA_HOME)/openldap-clibs-$(LDAP_VERSION)/lib

all: build tar

build:
	(tar xzf src/openldap-$(LDAP_RELEASE).tgz; \
	mv openldap-$(LDAP_RELEASE) openldap-$(LDAP_VERSION); \
	cd openldap-$(LDAP_VERSION); \
	patch -p1 < ../patches/internal-config; \
	patch -p1 < ../patches/internal-new-malloc; \
	patch -p1 < ../patches/internal-add-improvements; \
	patch -p1 < ../patches/ITS5037.patch; \
	patch -p1 < ../patches/ITS5079.patch; \
	patch -p1 < ../patches/ITS5225.patch; \
	LDFLAGS="-L$(OPENSSL_LIB_DIR) -L$(BDB_LIB_DIR) -L$(CYRUS_LIB_DIR) -L$(OPENLDAP_CLIB_DIR) -R$(OPENSSL_LIB_DIR) -R$(BDB_LIB_DIR) -R$(CYRUS_LIB_DIR) -R$(OPENLDAP_CLIB_DIR)" \
	LD_LIBRARY_PATH=$(OPENSSL_LIB_DIR):$(BDB_LIB_DIR):$(CYRUS_LIB_DIR) \
	CPPFLAGS="-I$(ZIMBRA_HOME)/sleepycat-$(BDB_VERSION)/include -I$(ZIMBRA_HOME)/cyrus-sasl-$(CYRUS_VERSION)/include -I$(ZIMBRA_HOME)/openssl-$(OPENSSL_VERSION)/include" \
	CFLAGS="-g -O0 -DSLAP_LIGHTWEIGHT_DISPATCHER" \
	$(ENVMOD) ./configure --prefix=$(ZIMBRA_HOME)/openldap-$(LDAP_VERSION) \
	--with-cyrus-sasl \
	--with-tls \
	--enable-dynamic \
	--enable-slapd \
		--enable-modules \
	--enable-backends=mod \
		--disable-shell \
		--disable-sql \
		--disable-ldbm \
	--enable-overlays=mod \
	--enable-debug \
	--enable-crypt \
	--enable-slurpd=no; \
	LD_RUN_PATH=$(OPENSSL_LIB_DIR):$(BDB_LIB_DIR):$(CYRUS_LIB_DIR) make; LD_RUN_PATH=$(OPENSSL_LIB_DIR):$(BDB_LIB_DIR):$(CYRUS_LIB_DIR) make install STRIP="")
	(cd $(ZIMBRA_HOME); ln -s openldap-$(LDAP_VERSION) openldap)

tar:
	mkdir -p $(P4_ROOT)/ThirdPartyBuilds/$(BUILD_PLATFORM)/openldap
	(cd $(ZIMBRA_HOME); tar czf $(LDAP_TGZ_TARGET) openldap-$(LDAP_VERSION))
	mkdir -p $(ZIMBRA_HOME)/openldap-clibs-$(LDAP_VERSION)/lib
	(cd $(ZIMBRA_HOME); cp -pfr $(ZIMBRA_HOME)/openldap-$(LDAP_VERSION)/lib/* $(ZIMBRA_HOME)/openldap-clibs-$(LDAP_VERSION)/lib; \
	tar cfz $(LDAP_LIB_TGZ_TARGET) openldap-clibs-$(LDAP_VERSION))

p4edit: $(LDAP_TGZ_TARGET)
	p4 add $(LDAP_TGZ_TARGET)
	p4 edit $(LDAP_TGZ_TARGET)
	p4 add $(LDAP_LIB_TGZ_TARGET)
	p4 edit $(LDAP_LIB_TGZ_TARGET)

clean:
	/bin/rm -rf openldap-$(LDAP_VERSION)

allclean: clean
	/bin/rm -rf $(ZIMBRA_HOME)/openldap-$(LDAP_VERSION)
	/bin/rm -rf $(ZIMBRA_HOME)/openldap
	/bin/rm -rf $(ZIMBRA_HOME)/openldap-clibs-$(LDAP_VERSION)
