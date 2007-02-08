CLAMAV_ROOT := $(shell pwd)
P4_ROOT ?= $(shell cd $(CLAMAV_ROOT)/../..; pwd)

BUILD_PLATFORM ?= $(shell sh $(P4_ROOT)/ZimbraBuild/rpmconf/Build/get_plat_tag.sh)

ifeq ($(BUILD_PLATFORM), )
	BUILD_PLATFORM := "UNKNOWN"
endif

ZIMBRA_HOME ?= /opt/zimbra
CLAMAV_VERSION ?= 0.88.7

CLAMAV_TGZ_TARGET := \
	$(CLAMAV_ROOT)/builds/$(BUILD_PLATFORM)/clamav-$(CLAMAV_VERSION).tgz

all: build tar

build:
	(tar xzf clamav-$(CLAMAV_VERSION).tar.gz; \
	cd clamav-$(CLAMAV_VERSION); \
	./configure --prefix=/opt/zimbra/clamav-$(CLAMAV_VERSION) \
	--with-user=zimbra --with-group=zimbra --disable-zlib-vcheck; \
	make; make install)
	-(cd $(ZIMBRA_HOME); ln -s clamav-$(CLAMAV_VERSION) clamav)

tar:
	mkdir -p $(CLAMAV_ROOT)/builds/$(BUILD_PLATFORM)
	(cd $(ZIMBRA_HOME); tar czf $(CLAMAV_TGZ_TARGET) clamav-$(CLAMAV_VERSION))
	chmod -R a+w $(CLAMAV_ROOT)/builds/$(BUILD_PLATFORM)

p4edit: $(CLAMAV_TGZ_TARGET)
	p4 edit $(CLAMAV_TGZ_TARGET)

clean:
	/bin/rm -rf clamav-$(CLAMAV_VERSION)

allclean: clean
	/bin/rm -rf $(ZIMBRA_HOME)/clamav-$(CLAMAV_VERSION)
	/bin/rm -rf $(ZIMBRA_HOME)/clamav
