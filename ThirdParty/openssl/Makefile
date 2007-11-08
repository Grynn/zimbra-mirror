OPENSSL_ROOT := $(shell pwd)
P4_ROOT ?= $(shell cd $(OPENSSL_ROOT)/../..; pwd)

BUILD_PLATFORM ?= $(shell sh $(P4_ROOT)/ZimbraBuild/rpmconf/Build/get_plat_tag.sh)

ifeq ($(BUILD_PLATFORM), )
	BUILD_PLATFORM := "UNKNOWN"
endif

ifeq ($(BUILD_PLATFORM), MACOSXx86)
	PLAT := darwin-i386-cc
endif

ifeq ($(BUILD_PLATFORM), MACOSX)
	PLAT := darwin-ppc-cc
endif

ifeq ($(BUILD_PLATFORM), RHEL4_64)
	PLAT := linux-x86_64
endif

ifeq ($(BUILD_PLATFORM), CentOS4_64)
	PLAT := linux-x86_64
endif

ifeq ($(BUILD_PLATFORM), RHEL5_64)
	PLAT := linux-x86_64
endif

ifeq ($(BUILD_PLATFORM), CentOS5_64)
	PLAT := linux-x86_64
endif

ifeq ($(BUILD_PLATFORM), SLES10_64)
	PLAT := linux-x86_64
endif

ifeq ($(PLAT), )
	PLAT := linux-elf
endif

ZIMBRA_HOME ?= /opt/zimbra
OPENSSL_VERSION ?=  0.9.8e
OPENSSL_TGZ_TARGET := $(P4_ROOT)/ThirdPartyBuilds/$(BUILD_PLATFORM)/openssl/openssl-$(OPENSSL_VERSION).tgz
OPENSSL_SOURCE := $(OPENSSL_ROOT)/openssl-$(OPENSSL_VERSION)

all: build tar

build:
	(tar xzf src/openssl-$(OPENSSL_VERSION).tar.gz; \
	cd openssl-$(OPENSSL_VERSION); \
	CFLAGS="-g -O2" \
	CXXFLAGS="-g -O2" \
	PERL=/usr/bin/perl \
	LDFLAGS="-L$(ZIMBRA_HOME)/openssl-$(OPENSSL_VERSION)/lib -R$(ZIMBRA_HOME)/openssl-$(OPENSSL_VERSION)/lib" \
	$(ENVMOD) ./Configure --prefix=$(ZIMBRA_HOME)/openssl-$(OPENSSL_VERSION) \
	shared $(PLAT) -g -O2; \
	make; \
	make install)
	(cd $(ZIMBRA_HOME); ln -s openssl-$(OPENSSL_VERSION) openssl)

tar:
	mkdir -p $(P4_ROOT)/ThirdPartyBuilds/$(BUILD_PLATFORM)/openssl
	(cd $(ZIMBRA_HOME); tar czf $(OPENSSL_TGZ_TARGET) openssl-$(OPENSSL_VERSION))

p4edit: $(OPENSSL_TGZ_TARGET)
	p4 add $(OPENSSL_TGZ_TARGET)
	p4 edit $(OPENSSL_TGZ_TARGET)
	

clean:
	/bin/rm -rf openssl-$(OPENSSL_VERSION)

allclean: clean
	/bin/rm -rf $(ZIMBRA_HOME)/openssl-$(OPENSSL_VERSION)
	/bin/rm -rf $(ZIMBRA_HOME)/openssl
