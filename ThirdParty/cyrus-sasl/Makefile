CYRUS_ROOT := $(shell pwd)
P4_ROOT ?= $(shell cd $(CYRUS_ROOT)/../..; pwd)

BUILD_PLATFORM ?= $(shell sh $(P4_ROOT)/ZimbraBuild/rpmconf/Build/get_plat_tag.sh)

ifeq ($(BUILD_PLATFORM), )
	BUILD_PLATFORM := "UNKNOWN"
endif

ZIMBRA_HOME ?= /opt/zimbra

CYRUS_VERSION ?= 2.1.21.ZIMBRA

all: build 

build:
	mkdir -p $(CYRUS_ROOT)/builds/$(BUILD_PLATFORM)
	./zimbra-cyrus-sasl-build.sh
	export _S_='~'
	(cd build/cyrus-sasl-$(CYRUS_VERSION); make install)
	(cd $(ZIMBRA_HOME); ln -s cyrus-sasl-$(CYRUS_VERSION) cyrus-sasl; \
	tar czf $(CYRUS_ROOT)/builds/$(BUILD_PLATFORM)/cyrus-sasl-$(CYRUS_VERSION).tgz \
	cyrus-sasl-$(CYRUS_VERSION))

clean:
	rm -rf build

allclean: clean
	rm -rf $(ZIMBRA_HOME)/cyrus-sasl-$(CYRUS_VERSION)
	rm -rf $(ZIMBRA_HOME)/cyrus-sasl
