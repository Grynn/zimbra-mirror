TCMALLOC_ROOT := $(shell pwd)
P4_ROOT ?= $(shell cd $(TCMALLOC_ROOT)/../..; pwd)

BUILD_PLATFORM ?= $(shell sh $(P4_ROOT)/ZimbraBuild/rpmconf/Build/get_plat_tag.sh)

ifeq ($(BUILD_PLATFORM), )
	BUILD_PLATFORM := "UNKNOWN"
endif

ifeq ($(BUILD_PLATFORM), MACOSXx86)
	ENVMOD := env LIBS="-lresolv" 
endif
ifeq ($(BUILD_PLATFORM), MACOSX)
	ENVMOD := env LIBS="-lresolv" 
endif

ZIMBRA_HOME ?= /opt/zimbra
TCMALLOC_VERSION ?= 0.92
TCMALLOC_TGZ_TARGET := $(TCMALLOC_ROOT)/builds/$(BUILD_PLATFORM)/tcmalloc-$(TCMALLOC_VERSION).tgz

all: build tar

build:
	(tar xzf google-perftools-$(TCMALLOC_VERSION).tgz; \
	cd google-perftools-$(TCMALLOC_VERSION); \
	CFLAGS="-g -O2" \
	$(ENVMOD) ./configure --prefix=$(ZIMBRA_HOME)/tcmalloc-$(TCMALLOC_VERSION); \
	make tcmalloc_minimal_unittest tcmalloc_minimal_large_unittest \
             addressmap_unittest atomicops_unittest frag_unittest \
             low_level_alloc_unittest markidle_unittest memalign_unittest \
             packed_cache_test stacktrace_unittest system_alloc_unittest \
             thread_dealloc_unittest;
	 make install)
	(cd $(ZIMBRA_HOME); ln -s tcmalloc-$(TCMALLOC_VERSION) tcmalloc)

tar:
	mkdir -p $(TCMALLOC_ROOT)/builds/$(BUILD_PLATFORM)
	(cd $(ZIMBRA_HOME); tar czf $(TCMALLOC_ROOT)/builds/$(BUILD_PLATFORM)/tcmalloc-$(TCMALLOC_VERSION).tgz tcmalloc-$(TCMALLOC_VERSION))

p4edit: $(TCMALLOC_TGZ_TARGET)
	p4 add $(TCMALLOC_TGZ_TARGET)
	p4 edit $(TCMALLOC_TGZ_TARGET)
	

clean:
	/bin/rm -rf google-perftools-$(TCMALLOC_VERSION)

allclean: clean
	/bin/rm -rf $(ZIMBRA_HOME)/tcmalloc-$(TCMALLOC_VERSION)
	/bin/rm -rf $(ZIMBRA_HOME)/tcmalloc
