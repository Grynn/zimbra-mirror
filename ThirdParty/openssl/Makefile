OPENSSL_ROOT := $(shell pwd)
P4_ROOT ?= $(shell cd $(OPENSSL_ROOT)/../..; pwd)
MAKE ?= make
MAKEARGS := -j3

BUILD_PLATFORM ?= $(shell sh $(P4_ROOT)/ZimbraBuild/rpmconf/Build/get_plat_tag.sh)

ifeq ($(BUILD_PLATFORM), )
	BUILD_PLATFORM := "UNKNOWN"
endif

ZIMBRA_HOME ?= /opt/zimbra

ifdef BETA
	include $(OPENSSL_ROOT)/../beta_versions.def
	CONFIG_OPT	:= --libdir=lib
else
	include $(OPENSSL_ROOT)/../versions.def
endif

ifeq (MACOSXx86,$(findstring MACOSXx86,$(BUILD_PLATFORM)))
	PLAT := darwin-i386-cc
endif

ifeq ($(BUILD_PLATFORM), MACOSXx86_10.6)
	PLAT := darwin64-x86_64-cc
endif

ifeq ($(BUILD_PLATFORM), MACOSX)
	PLAT := darwin-ppc-cc
endif

ifeq (_64,$(findstring _64, $(BUILD_PLATFORM)))
	PLAT := linux-x86_64
endif

ifeq ($(PLAT), )
	PLAT := linux-elf
endif

OPENSSL_TGZ_TARGET := $(P4_ROOT)/ThirdPartyBuilds/$(BUILD_PLATFORM)/openssl/openssl-$(OPENSSL_VERSION).tgz
OPENSSL_SOURCE := $(OPENSSL_ROOT)/openssl-$(OPENSSL_VERSION)

files	:= $(wildcard src/openssl-$(OPENSSL_VERSION).tar.gz)

all: checksrc allclean build tar

checksrc:
	$(if $(files), @echo "", exit 1)

build:
	(tar xzf src/openssl-$(OPENSSL_VERSION).tar.gz; \
	cd openssl-$(OPENSSL_VERSION); \
	CFLAGS="-g -O2" \
	CXXFLAGS="-g -O2" \
	PERL=/usr/bin/perl \
	LDFLAGS="-L$(ZIMBRA_HOME)/openssl-$(OPENSSL_VERSION)/lib -R$(ZIMBRA_HOME)/openssl-$(OPENSSL_VERSION)/lib" \
	LD_RUN_PATH=$(ZIMBRA_HOME)/openssl-$(OPENSSL_VERSION)/lib ./Configure --prefix=$(ZIMBRA_HOME)/openssl-$(OPENSSL_VERSION) $(CONFIG_OPT) \
	shared $(PLAT) -g -O2; \
	LD_RUN_PATH=$(ZIMBRA_HOME)/openssl-$(OPENSSL_VERSION)/lib $(MAKE) $(MAKEARGS); \
	LD_RUN_PATH=$(ZIMBRA_HOME)/openssl-$(OPENSSL_VERSION)/lib $(MAKE) install)
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
	rm -f $(OPENSSL_TGZ_TARGET)
