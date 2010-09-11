CURL_ROOT := $(shell pwd)
P4_ROOT ?= $(shell cd $(CURL_ROOT)/../..; pwd)
MAKE ?= make
MAKEARGS ?= -j2

BUILD_PLATFORM ?= $(shell sh $(P4_ROOT)/ZimbraBuild/rpmconf/Build/get_plat_tag.sh)

ifeq ($(BUILD_PLATFORM), MACOSXx86_10.6)
	HEIMDAL_VERSION ?= 1.3.0rc1
endif

ZIMBRA_HOME ?= /opt/zimbra

ifdef BETA
	include $(CURL_ROOT)/../beta_versions.def
	PATCH	:= patch -N -g0 -p1 < ../patches/krb5-7200.patch; patch -N -g0 -p1 < ../patches/proxy.patch; patch -N -g0 -p1 < ../patches/f13-lrt.patch;
else
	include $(CURL_ROOT)/../versions.def
	PATCH	:= patch -N -g0 -p1 < ../patches/krb5-7200.patch; patch -N -g0 -p1 < ../patches/proxy.patch; patch -N -g0 -p1 < ../patches/f13-lrt.patch;
endif


CURL_TGZ_TARGET := $(P4_ROOT)/ThirdPartyBuilds/$(BUILD_PLATFORM)/curl/curl-$(CURL_VERSION).tgz

LIB_DIR := /usr/lib

ifeq ($(BUILD_PLATFORM), RHEL4_64)
	LIB_DIR := /usr/lib64
endif

ifeq ($(BUILD_PLATFORM), CentOS4_64)
	LIB_DIR := /usr/lib64
endif

ifeq ($(BUILD_PLATFORM), RHEL5_64)
	LIB_DIR := /usr/lib64
endif

ifeq ($(BUILD_PLATFORM), CentOS5_64)
	LIB_DIR := /usr/lib64
endif

ifeq ($(BUILD_PLATFORM), RHEL6_64)
	LIB_DIR := /usr/lib64
endif

ifeq ($(BUILD_PLATFORM), CentOS6_64)
	LIB_DIR := /usr/lib64
endif

ifeq ($(BUILD_PLATFORM), SLES10_64)
	LIB_DIR := /usr/lib64
endif

ifeq ($(BUILD_PLATFORM), SLES11_64)
	LIB_DIR := /usr/lib64
endif

ifeq ($(BUILD_PLATFORM), F11_64)
	LIB_DIR := /usr/lib64
endif

ifeq ($(BUILD_PLATFORM), F13_64)
	LIB_DIR := /usr/lib64
endif

files	:= $(wildcard src/curl-$(CURL_VERSION).tar.gz)

all: checksrc allclean build check tar

checksrc:
	$(if $(files), @echo "", exit 1)

check:
	@echo "Verifying curl build...";
	$(CURL_ROOT)/../zmcheck.pl -b $(ZIMBRA_HOME)/curl-$(CURL_VERSION)/bin/curl

build:
	tar xzf src/curl-$(CURL_VERSION).tar.gz
	(cd curl-$(CURL_VERSION); \
	$(PATCH) \
	./buildconf; \
	DYLD_LIBRARY_PATH=$(OPENSSL_LIB_DIR):$(HEIMDAL_LIB_DIR):$(CURL_LIB_DIR) \
	LD_RUN_PATH=$(OPENSSL_LIB_DIR):$(HEIMDAL_LIB_DIR):$(CURL_LIB_DIR) \
	LDFLAGS="-L$(OPENSSL_LIB_DIR) -L$(HEIMDAL_LIB_DIR) -L$(CURL_LIB_DIR) \
		-R$(OPENSSL_LIB_DIR) -R$(HEIMDAL_LIB_DIR) -R$(CURL_LIB_DIR)" \
	CFLAGS="-g -O2" \
	./configure --prefix=$(ZIMBRA_HOME)/curl-$(CURL_VERSION) --disable-ldap --disable-ldaps \
		--with-gssapi=/opt/zimbra/heimdal-$(HEIMDAL_VERSION) \
		--with-ssl=/opt/zimbra/openssl-$(OPENSSL_VERSION) \
		--with-ca-bundle=/opt/zimbra/curl-$(CURL_VERSION)/share/curl/ca-bundle.crt \
		--with-zlib=$(LIB_DIR) \
		--with-libidn=$(LIB_DIR))
	(cd curl-$(CURL_VERSION);  LD_RUN_PATH=$(OPENSSL_LIB_DIR):$(HEIMDAL_LIB_DIR):$(CURL_LIB_DIR) $(MAKE) $(MAKEARGS))
	(cd curl-$(CURL_VERSION); LD_RUN_PATH=$(OPENSSL_LIB_DIR):$(HEIMDAL_LIB_DIR):$(CURL_LIB_DIR) $(MAKE) $(MAKEARGS) install)
	(cd curl-$(CURL_VERSION); $(MAKE) ca-bundle)
	mkdir -p $(ZIMBRA_HOME)/curl-$(CURL_VERSION)/share/curl
	(cd curl-$(CURL_VERSION); cp -f lib/ca-bundle.crt $(ZIMBRA_HOME)/curl-$(CURL_VERSION)/share/curl/ca-bundle.crt)
	(cd $(ZIMBRA_HOME); ln -s curl-$(CURL_VERSION) curl)

tar:
	mkdir -p $(P4_ROOT)/ThirdPartyBuilds/$(BUILD_PLATFORM)/curl
	(cd $(ZIMBRA_HOME); tar czf $(CURL_TGZ_TARGET) curl-$(CURL_VERSION))

p4edit: $(CURL_TGZ_TARGET)
	p4 add $(CURL_TGZ_TARGET)
	p4 edit $(CURL_TGZ_TARGET)
	

clean:
	/bin/rm -rf curl-$(CURL_VERSION)

allclean: clean
	/bin/rm -rf $(ZIMBRA_HOME)/curl-$(CURL_VERSION)
	/bin/rm -rf $(ZIMBRA_HOME)/curl
	rm -f $(CURL_TGZ_TARGET)
