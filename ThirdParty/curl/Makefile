CURL_ROOT := $(shell pwd)
P4_ROOT ?= $(shell cd $(CURL_ROOT)/../..; pwd)
MAKE ?= make
MAKEARGS ?= -j2

BUILD_PLATFORM ?= $(shell sh $(P4_ROOT)/ZimbraBuild/rpmconf/Build/get_plat_tag.sh)

ZIMBRA_HOME ?= /opt/zimbra
CURL_VERSION ?= 7.19.4
HEIMDAL_VERSION ?= 1.2.1
OPENSSL_VERSION ?= 0.9.8k
CURL_TGZ_TARGET := $(P4_ROOT)/ThirdPartyBuilds/$(BUILD_PLATFORM)/curl/curl-$(CURL_VERSION).tgz

OPENSSL_LIB_DIR ?= $(ZIMBRA_HOME)/openssl-$(OPENSSL_VERSION)/lib
HEIMDAL_LIB_DIR ?= $(ZIMBRA_HOME)/heimdal-$(HEIMDAL_VERSION)/lib
CURL_LIB_DIR ?= $(ZIMBRA_HOME)/curl-$(CURL_VERSION)/lib

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

ifeq ($(BUILD_PLATFORM), SLES10_64)
	LIB_DIR := /usr/lib64
endif

ifeq ($(BUILD_PLATFORM), SLES11_64)
	LIB_DIR := /usr/lib64
endif

ifeq ($(BUILD_PLATFORM), F11_64)
	LIB_DIR := /usr/lib64
endif

all: allclean build tar

build:
	(tar xzf src/curl-$(CURL_VERSION).tar.gz; \
	cd curl-$(CURL_VERSION); \
	patch -N -g0 -p1 < ../patches/krb5.patch; \
	patch -N -g0 -p1 < ../patches/custom-openssl.patch; \
	LD_RUN_PATH=$(OPENSSL_LIB_DIR):$(HEIMDAL_LIB_DIR):$(CURL_LIB_DIR) \
	LDFLAGS="-L$(OPENSSL_LIB_DIR) -L$(HEIMDAL_LIB_DIR) -L$(CURL_LIB_DIR) \
		-R$(OPENSSL_LIB_DIR) -R$(HEIMDAL_LIB_DIR) -R$(CURL_LIB_DIR)" \
	CFLAGS="-g -O2" \
	./configure --prefix=$(ZIMBRA_HOME)/curl-$(CURL_VERSION) --disable-ldap --disable-ldaps \
		--with-gssapi=/opt/zimbra/heimdal-$(HEIMDAL_VERSION) \
		--with-ssl=/opt/zimbra/openssl-$(OPENSSL_VERSION) \
		--with-ca-bundle=/opt/zimbra/curl-$(CURL_VERSION)/share/curl/ca-bundle.crt \
		--with-zlib=/usr/lib64 \
		--with-libidn=/usr/lib64;\
	LD_RUN_PATH=$(OPENSSL_LIB_DIR):$(HEIMDAL_LIB_DIR):$(CURL_LIB_DIR) $(MAKE) $(MAKEARGS); \
	LD_RUN_PATH=$(OPENSSL_LIB_DIR):$(HEIMDAL_LIB_DIR):$(CURL_LIB_DIR) $(MAKE) $(MAKEARGS) install; \
	$(MAKE) ca-bundle; \
	mkdir -p $(ZIMBRA_HOME)/curl-$(CURL_VERSION)/share/curl; \
	cp -f lib/ca-bundle.crt $(ZIMBRA_HOME)/curl-$(CURL_VERSION)/share/curl/ca-bundle.crt)
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
