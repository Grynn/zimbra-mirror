CLAMAV_ROOT := $(shell pwd)
P4_ROOT ?= $(shell cd $(CLAMAV_ROOT)/../..; pwd)

BUILD_PLATFORM ?= $(shell sh $(P4_ROOT)/ZimbraBuild/rpmconf/Build/get_plat_tag.sh)

ifeq ($(BUILD_PLATFORM), )
	BUILD_PLATFORM := "UNKNOWN"
endif

ZIMBRA_HOME ?= /opt/zimbra
CLAMAV_VERSION ?= 0.85.1

all: build tar

build:
	(tar xzf clamav-$(CLAMAV_VERSION).tar.gz; \
	cd clamav-$(CLAMAV_VERSION); \
	./configure --prefix=/opt/zimbra/clamav-0.85.1 \
	--with-user=zimbra --with-group=zimbra --disable-zlib-vcheck; \
	make; make install)

tar:
	mkdir -p $(CLAMAV_ROOT)/builds/$(BUILD_PLATFORM)
	(cd $(ZIMBRA_HOME); tar czf $(CLAMAV_ROOT)/builds/$(BUILD_PLATFORM)/clamav-$(CLAMAV_VERSION).tgz clamav-$(CLAMAV_VERSION))

clean:
	/bin/rm -rf clamav-$(CLAMAV_VERSION)

allclean: clean
	/bin/rm -rf $(ZIMBRA_HOME)/clamav-$(CLAMAV_VERSION)
