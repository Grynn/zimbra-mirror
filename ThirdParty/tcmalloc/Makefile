TCMALLOC_ROOT := $(shell pwd)
P4_ROOT ?= $(shell cd $(TCMALLOC_ROOT)/../..; pwd)

BUILD_PLATFORM ?= $(shell sh $(P4_ROOT)/ZimbraBuild/rpmconf/Build/get_plat_tag.sh)

ifeq ($(BUILD_PLATFORM), )
	BUILD_PLATFORM := "UNKNOWN"
endif

ZIMBRA_HOME ?= /opt/zimbra

ifdef BETA
	include $(TCMALLOC_ROOT)/../beta_versions.def
else
	include $(TCMALLOC_ROOT)/../versions.def
endif

ifeq (MACOSXx86,$(findstring MACOSXx86,$(BUILD_PLATFORM)))
	ENVMOD := env LIBS="-lresolv" 
endif
ifeq ($(BUILD_PLATFORM), MACOSX)
	ENVMOD := env LIBS="-lresolv" 
endif

TCMALLOC_TGZ_TARGET := $(P4_ROOT)/ThirdPartyBuilds/$(BUILD_PLATFORM)/tcmalloc/tcmalloc-$(TCMALLOC_VERSION).tgz

files	:= $(wildcard src/google-perftools-$(TCMALLOC_VERSION).tar.gz)

all: checksrc allclean build tar

checksrc:
	$(if $(files), @echo "", exit 1)

build:
	(tar xzf src/google-perftools-$(TCMALLOC_VERSION).tar.gz; \
	cd google-perftools-$(TCMALLOC_VERSION); \
	CFLAGS="-g -O2" \
	CPPFLAGS="-I/usr/include" \
	CXXFLAGS="-g -O2 -I/usr/include" \
	$(ENVMOD) ./configure --prefix=$(ZIMBRA_HOME)/tcmalloc-$(TCMALLOC_VERSION) --enable-minimal; \
	make; \
	make install STRIP="")
	(cd $(ZIMBRA_HOME); ln -s tcmalloc-$(TCMALLOC_VERSION) tcmalloc; \
	rmdir $(ZIMBRA_HOME)/tcmalloc-$(TCMALLOC_VERSION)/bin)

tar:
	mkdir -p $(P4_ROOT)/ThirdPartyBuilds/$(BUILD_PLATFORM)/tcmalloc
	(cd $(ZIMBRA_HOME); tar czf $(TCMALLOC_TGZ_TARGET) tcmalloc-$(TCMALLOC_VERSION))

p4edit: $(TCMALLOC_TGZ_TARGET)
	p4 add $(TCMALLOC_TGZ_TARGET)
	p4 edit $(TCMALLOC_TGZ_TARGET)
	

clean:
	/bin/rm -rf google-perftools-$(TCMALLOC_VERSION)

allclean: clean
	/bin/rm -rf $(ZIMBRA_HOME)/tcmalloc-$(TCMALLOC_VERSION)
	/bin/rm -rf $(ZIMBRA_HOME)/tcmalloc
	rm -f $(TCMALLOC_TGZ_TARGET)
