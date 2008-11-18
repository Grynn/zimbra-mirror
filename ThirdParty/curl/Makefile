CURL_ROOT := $(shell pwd)
P4_ROOT ?= $(shell cd $(CURL_ROOT)/../..; pwd)
MAKE ?= make
MAKEARGS ?= -j2

BUILD_PLATFORM ?= $(shell sh $(P4_ROOT)/ZimbraBuild/rpmconf/Build/get_plat_tag.sh)

ZIMBRA_HOME ?= /opt/zimbra
CURL_VERSION ?= 7.19.2
HEIMDAL_VERSION ?= 1.2.1
OPENSSL_VERSION ?= 0.9.8i
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

ifeq ($(BUILD_PLATFORM), SLES10_64)
	LIB_DIR := /usr/lib64
endif

all: build tar

build:
	(tar xzf src/curl-$(CURL_VERSION).tar.gz; \
	cd curl-$(CURL_VERSION); \
	make ca-bundle; \
	patch -N -g0 -p1 < ../patches/krb5.patch; \
	LD_RUN_PATH=/opt/zimbra/openssl-$(OPENSSL_VERSION):/opt/zimbra/heimdal-$(HEIMDAL_VERSION)/lib \
	CFLAGS="-g -O2" \
	./configure --prefix=$(ZIMBRA_HOME)/curl-$(CURL_VERSION) --disable-ldap --disable-ldaps \
		--with-gssapi=/opt/zimbra/heimdal-$(HEIMDAL_VERSION) \
		--with-ssl=/opt/zimbra/openssl-$(OPENSSL_VERSION) \
		--with-ca-bundle=/opt/zimbra/curl-$(CURL_VERSION)/share/curl/ca-bundle.crt \
		--with-zlib=/usr/lib64 \
		--with-libidn=/usr/lib64;\
	$(MAKE) $(MAKEARGS); \
	$(MAKE) $(MAKEARGS) install; \
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
