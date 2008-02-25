CYRUS_ROOT := $(shell pwd)
P4_ROOT ?= $(shell cd $(CYRUS_ROOT)/../..; pwd)
MAKE ?= make
MAKEARGS ?= -j2

BUILD_PLATFORM ?= $(shell sh $(P4_ROOT)/ZimbraBuild/rpmconf/Build/get_plat_tag.sh)

ifeq ($(BUILD_PLATFORM), )
	BUILD_PLATFORM := "UNKNOWN"
endif

ZIMBRA_HOME ?= /opt/zimbra

CYRUS_RELEASE ?= 2.1.22
CYRUS_PATCHLEVEL ?= 3z
CYRUS_VERSION ?= $(CYRUS_RELEASE).$(CYRUS_PATCHLEVEL)
CYRUS_TGZ_TARGET := $(P4_ROOT)/ThirdPartyBuilds/$(BUILD_PLATFORM)/cyrus-sasl/cyrus-sasl-$(CYRUS_VERSION).tgz
CYRUS_LIB_DIR ?= $(ZIMBRA_HOME)/cyrus-sasl-$(CYRUS_VERSION)/lib

BDB_VERSION ?= 4.2.52.6
HEIMDAL_VERSION ?= 1.0.2
OPENSSL_VERSION ?= 0.9.8g

BDB_LIB_DIR ?= $(ZIMBRA_HOME)/sleepycat-$(BDB_VERSION)/lib
HEIMDAL_LIB_DIR ?= $(ZIMBRA_HOME)/heimdal-$(HEIMDAL_VERSION)/lib
OPENSSL_LIB_DIR ?= $(ZIMBRA_HOME)/openssl-$(OPENSSL_VERSION)/lib

all: build 

build: 
ifeq	(MACOSXx86,$(findstring MACOSXx86,$(BUILD_PLATFORM)))
	mkdir -p $(P4_ROOT)/ThirdPartyBuilds/$(BUILD_PLATFORM)/cyrus-sasl
	_S_='~' ./zimbra-cyrus-sasl-build.sh
	(cd build/cyrus-sasl-$(CYRUS_VERSION); $(MAKE) install)
	(./fix-sasl.pl)
	(cd $(ZIMBRA_HOME); ln -s cyrus-sasl-$(CYRUS_VERSION) cyrus-sasl; \
	tar czf $(CYRUS_TGZ_TARGET) \
	cyrus-sasl-$(CYRUS_VERSION))
else
	mkdir -p $(P4_ROOT)/ThirdPartyBuilds/$(BUILD_PLATFORM)/cyrus-sasl
	_S_='~' ./zimbra-cyrus-sasl-build.sh
	(cd build/cyrus-sasl-$(CYRUS_VERSION); $(MAKE) install)
	(cd $(ZIMBRA_HOME); ln -s cyrus-sasl-$(CYRUS_VERSION) cyrus-sasl; \
	tar czf $(CYRUS_TGZ_TARGET) \
	cyrus-sasl-$(CYRUS_VERSION))
endif

/opt/zimbra/libxml2/lib/libxml2.a:
	$(MAKE) $(MAKEARGS) -C $(P4_ROOT)/ThirdParty/libxml2


p4edit: $(CYRUS_TGZ_TARGET)
	p4 add $(CYRUS_TGZ_TARGET)
	p4 edit $(CYRUS_TGZ_TARGET)

clean:
	rm -rf build

allclean: clean
	rm -rf $(ZIMBRA_HOME)/cyrus-sasl-$(CYRUS_VERSION)
	rm -rf $(ZIMBRA_HOME)/cyrus-sasl
