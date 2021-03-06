LIBTOOL_ROOT := $(shell pwd)
P4_ROOT ?= $(shell cd $(LIBTOOL_ROOT)/../..; pwd)
MAKE ?= make
MAKEARGS ?= -j2

BUILD_PLATFORM ?= $(shell sh $(P4_ROOT)/ZimbraBuild/rpmconf/Build/get_plat_tag.sh)

ifeq ($(BUILD_PLATFORM), )
	BUILD_PLATFORM := "UNKNOWN"
endif

ZIMBRA_HOME ?= /opt/zimbra

ifdef BETA
	include $(LIBTOOL_ROOT)/../beta_versions.def
else
	include $(LIBTOOL_ROOT)/../versions.def
endif

LIBTOOL_TGZ_TARGET := $(P4_ROOT)/ThirdPartyBuilds/$(BUILD_PLATFORM)/libtool/libtool-$(LIBTOOL_VERSION).tgz
LIBTOOL_LIB_DIR	?= $(ZIMBRA_HOME)/libtool-$(LIBTOOL_VERSION)/lib

files	:= $(wildcard src/libtool-$(LIBTOOL_VERSION).tar.gz)

all: checksrc allclean build tar

checksrc:
	$(if $(files), @echo "", exit 1)

build:
	(tar xzf src/libtool-$(LIBTOOL_VERSION).tar.gz; \
	cd libtool-$(LIBTOOL_VERSION); \
	CFLAGS="-D_REENTRANT -g -O2" CXXFLAGS="-D_REENTRANT -g -O2" LD_RUN_PATH=$(LIBTOOL_LIB_DIR) ./configure --prefix=$(ZIMBRA_HOME)/libtool-$(LIBTOOL_VERSION); \
	CFLAGS="-D_REENTRANT -g -O2" CXXFLAGS="-D_REENTRANT -g -O2" LD_RUN_PATH=$(LIBTOOL_LIB_DIR) $(MAKE) $(MAKEARGS); \
	LD_RUN_PATH=$(LIBTOOL_LIB_DIR) $(MAKE) install)
	(cd $(ZIMBRA_HOME); ln -s libtool-$(LIBTOOL_VERSION) libtool; rm -rf $(ZIMBRA_HOME)/libtool-$(LIBTOOL_VERSION)/{bin,share})

tar:
	mkdir -p $(P4_ROOT)/ThirdPartyBuilds/$(BUILD_PLATFORM)/libtool
	(cd $(ZIMBRA_HOME); tar czf $(LIBTOOL_TGZ_TARGET) libtool-$(LIBTOOL_VERSION))
	chmod -R a+w $(LIBTOOL_TGZ_TARGET)

clean:
	/bin/rm -rf libtool-$(LIBTOOL_VERSION)

p4edit: $(LIBTOOL_TGZ_TARGET)
	p4 add $(LIBTOOL_TGZ_TARGET) 
	p4 edit $(LIBTOOL_TGZ_TARGET) 

allclean: clean
	/bin/rm -rf $(ZIMBRA_HOME)/libtool-$(LIBTOOL_VERSION)
	/bin/rm -rf $(ZIMBRA_HOME)/libtool
	rm -f $(LIBTOOL_TGZ_TARGET)
