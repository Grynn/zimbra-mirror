HEIMDAL_ROOT := $(shell pwd)
P4_ROOT ?= $(shell cd $(HEIMDAL_ROOT)/../..; pwd)

BUILD_PLATFORM ?= $(shell sh $(P4_ROOT)/ZimbraBuild/rpmconf/Build/get_plat_tag.sh)

ifeq ($(BUILD_PLATFORM), )
	BUILD_PLATFORM := "UNKNOWN"
endif

ZIMBRA_HOME ?= /opt/zimbra
HEIMDAL_VERSION ?=  1.0.1
HEIMDAL_TGZ_TARGET := $(P4_ROOT)/ThirdPartyBuilds/$(BUILD_PLATFORM)/heimdal/heimdal-$(HEIMDAL_VERSION).tgz

all: build tar

build:
	(tar xzf src/heimdal-$(HEIMDAL_VERSION).tar.gz; \
	cd heimdal-$(HEIMDAL_VERSION); \
	CFLAGS="-g -O2 -D_REENTRANT" \
	CCFLAGS="-g -O2 -D_REENTRANT" \
	$(ENVMOD) ./configure --prefix=$(ZIMBRA_HOME)/heimdal-$(HEIMDAL_VERSION) \
	CCFLAGS="-g -O2 -D_REENTRANT" \
	--sysconfdir=/etc \
	--enable-shared \
	--enable-pthread-support \
	--without-readline \
	--without-openldap \
	--without-hesiod \
	--without-berkeley-db \
	--without-ipv6; \
	make; make install STRIP="")
	(cd $(ZIMBRA_HOME); ln -s heimdal-$(HEIMDAL_VERSION) heimdal)

tar:
	mkdir -p $(P4_ROOT)/ThirdPartyBuilds/$(BUILD_PLATFORM)/heimdal
	(cd $(ZIMBRA_HOME); tar czf $(HEIMDAL_TGZ_TARGET) heimdal-$(HEIMDAL_VERSION))

p4edit: $(HEIMDAL_TGZ_TARGET)
	p4 add $(HEIMDAL_TGZ_TARGET)
	p4 edit $(HEIMDAL_TGZ_TARGET)
	

clean:
	/bin/rm -rf heimdal-$(HEIMDAL_VERSION)

allclean: clean
	/bin/rm -rf $(ZIMBRA_HOME)/heimdal-$(HEIMDAL_VERSION)
	/bin/rm -rf $(ZIMBRA_HOME)/heimdal
