ZMQ_ROOT := $(shell pwd)
P4_ROOT ?= $(shell cd $(ZMQ_ROOT)/../..; pwd)
MAKE ?= make

BUILD_PLATFORM ?= $(shell sh $(P4_ROOT)/ZimbraBuild/rpmconf/Build/get_plat_tag.sh)

ifeq ($(BUILD_PLATFORM), )
	BUILD_PLATFORM := "UNKNOWN"
endif

MAKEARGS ?= -j2
ZIMBRA_HOME ?= /opt/zimbra

PATCH	:=

ifdef BETA
	include $(ZMQ_ROOT)/../beta_versions.def
else
	include $(ZMQ_ROOT)/../versions.def
endif

ZMQ_TGZ_TARGET := $(P4_ROOT)/ThirdPartyBuilds/$(BUILD_PLATFORM)/zeromq/zeromq-$(ZMQ_VERSION).tgz

files	:= $(wildcard src/zeromq-$(ZMQ_VERSION).tar.gz)

all: checksrc allclean build tar

checksrc:
	$(if $(files), @echo "", exit 1)

build:
	(tar xzf src/zeromq-$(ZMQ_VERSION).tar.gz; \
	cd zeromq-$(ZMQ_VERSION); \
	LDFLAGS="-Wl,-rpath,$(ZIMBRA_HOME)/zeromq-$(ZMQ_VERSION)/lib" \
	CFLAGS="-g -O0" \
	./configure --prefix=$(ZIMBRA_HOME)/zeromq-$(ZMQ_VERSION); \
	$(MAKE) $(MAKEARGS); \
	$(MAKE) install STRIP="";)
	(cd $(ZIMBRA_HOME); ln -s zeromq-$(ZMQ_VERSION) zeromq)

tar:
	mkdir -p $(P4_ROOT)/ThirdPartyBuilds/$(BUILD_PLATFORM)/zeromq
	(cd $(ZIMBRA_HOME); tar czf $(ZMQ_TGZ_TARGET) zeromq-$(ZMQ_VERSION))

p4edit: $(ZMQ_TGZ_TARGET)
	p4 add $(ZMQ_TGZ_TARGET)
	p4 edit $(ZMQ_TGZ_TARGET)

clean:
	/bin/rm -rf zeromq-$(ZMQ_VERSION)

allclean: clean
	/bin/rm -rf $(ZIMBRA_HOME)/zeromq-$(ZMQ_VERSION)
	/bin/rm -rf $(ZIMBRA_HOME)/zeromq
	rm -f $(ZMQ_TGZ_TARGET)
