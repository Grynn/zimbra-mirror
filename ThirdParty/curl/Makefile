CURL_ROOT := $(shell pwd)
P4_ROOT ?= $(shell cd $(CURL_ROOT)/../..; pwd)

BUILD_PLATFORM ?= $(shell sh $(P4_ROOT)/ZimbraBuild/rpmconf/Build/get_plat_tag.sh)

ZIMBRA_HOME ?= /opt/zimbra
CURL_VERSION ?= 7.17.0
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
	LD_RUN_PATH=/opt/zimbra/openssl-0.9.8e:/opt/zimbra/heimdal-1.0.1/lib \
	CFLAGS="-g -O2" \
	./configure --prefix=$(ZIMBRA_HOME)/curl-$(CURL_VERSION) --disable-ldap --disable-ldaps \
		--with-gssapi=/opt/zimbra/heimdal-1.0.1 \
		--with-ssl=/opt/zimbra/openssl-0.9.8e \
		--with-zlib=/usr/lib64 \
		--with-libidn=/usr/lib64;\
	make curl_minimal_unittest curl_minimal_large_unittest \
             addressmap_unittest atomicops_unittest frag_unittest \
             low_level_alloc_unittest markidle_unittest memalign_unittest \
             packed_cache_test stacktrace_unittest system_alloc_unittest \
             thread_dealloc_unittest; \
	 make install)
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
