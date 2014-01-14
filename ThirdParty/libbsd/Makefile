LIBBSD_ROOT := $(shell pwd)
P4_ROOT ?= $(shell cd $(LIBBSD_ROOT)/../..; pwd)
MAKE ?= make
MAKEARGS ?= -j2

BUILD_PLATFORM ?= $(shell sh $(P4_ROOT)/ZimbraBuild/rpmconf/Build/get_plat_tag.sh)

ifeq ($(BUILD_PLATFORM), )
	BUILD_PLATFORM := "UNKNOWN"
endif

ZIMBRA_HOME ?= /opt/zimbra

ifdef BETA
	include $(LIBBSD_ROOT)/../beta_versions.def
else
	include $(LIBBSD_ROOT)/../versions.def
endif

LIBBSD_TGZ_TARGET := $(P4_ROOT)/ThirdPartyBuilds/$(BUILD_PLATFORM)/libbsd/libbsd-$(BSD_VERSION).tgz
LIBBSD_LIB_DIR	?= $(ZIMBRA_HOME)/libbsd-$(BSD_VERSION)/lib

files	:= $(wildcard src/libbsd-$(BSD_VERSION).tar.xz)

all: checksrc allclean build tar

checksrc:
	$(if $(files), @echo "", exit 1)

build:
	(tar xJf src/libbsd-$(BSD_VERSION).tar.xz; \
	cd libbsd-$(BSD_VERSION); \
	CFLAGS="-D_REENTRANT -g -O2" CXXFLAGS="-D_REENTRANT -g -O2" LD_RUN_PATH=$(LIBBSD_LIB_DIR) ./configure --prefix=$(ZIMBRA_HOME)/libbsd-$(BSD_VERSION); \
	CFLAGS="-D_REENTRANT -g -O2" CXXFLAGS="-D_REENTRANT -g -O2" LD_RUN_PATH=$(LIBBSD_LIB_DIR) $(MAKE) $(MAKEARGS); \
	LD_RUN_PATH=$(LIBBSD_LIB_DIR) $(MAKE) install)
	(cd $(ZIMBRA_HOME); ln -s libbsd-$(BSD_VERSION) libbsd; rm -rf $(ZIMBRA_HOME)/libbsd-$(BSD_VERSION)/{bin,share})

tar:
	mkdir -p $(P4_ROOT)/ThirdPartyBuilds/$(BUILD_PLATFORM)/libbsd
	(cd $(ZIMBRA_HOME); tar czf $(LIBBSD_TGZ_TARGET) libbsd-$(BSD_VERSION))
	chmod -R a+w $(LIBBSD_TGZ_TARGET)

clean:
	/bin/rm -rf libbsd-$(BSD_VERSION)

p4edit: $(LIBBSD_TGZ_TARGET)
	p4 add $(LIBBSD_TGZ_TARGET) 
	p4 edit $(LIBBSD_TGZ_TARGET) 

allclean: clean
	/bin/rm -rf $(ZIMBRA_HOME)/libbsd-$(BSD_VERSION)
	/bin/rm -rf $(ZIMBRA_HOME)/libbsd
	rm -f $(LIBBSD_TGZ_TARGET)
