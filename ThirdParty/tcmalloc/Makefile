TCMALLOC_ROOT := $(shell pwd)
P4_ROOT ?= $(shell cd $(TCMALLOC_ROOT)/../..; pwd)

BUILD_PLATFORM ?= $(shell sh $(P4_ROOT)/ZimbraBuild/rpmconf/Build/get_plat_tag.sh)

ifeq ($(BUILD_PLATFORM), )
	BUILD_PLATFORM := "UNKNOWN"
endif

ifeq (MACOSXx86,$(findstring MACOSXx86,$(BUILD_PLATFORM)))
	ENVMOD := env LIBS="-lresolv" 
endif
ifeq ($(BUILD_PLATFORM), MACOSX)
	ENVMOD := env LIBS="-lresolv" 
endif

ifeq ($(BUILD_PLATFORM), RHEL4_64)
	CONF_OPS := --enable-frame-pointer
endif

ifeq ($(BUILD_PLATFORM), CentOS4_64)
	CONF_OPS := --enable-frame-pointer
endif

ifeq ($(BUILD_PLATFORM), RHEL5_64)
	CONF_OPS := --enable-frame-pointer
endif

ifeq ($(BUILD_PLATFORM), CentOS5_64)
	CONF_OPS := --enable-frame-pointer
endif

ifeq ($(BUILD_PLATFORM), SLES10_64)
	CONF_OPS := --enable-frame-pointer
endif

ifeq ($(BUILD_PLATFORM), UBUNTU6_64)
	CONF_OPS := --enable-frame-pointer
endif

ZIMBRA_HOME ?= /opt/zimbra
TCMALLOC_VERSION ?= 0.94.1
TCMALLOC_TGZ_TARGET := $(P4_ROOT)/ThirdPartyBuilds/$(BUILD_PLATFORM)/tcmalloc/tcmalloc-$(TCMALLOC_VERSION).tgz

all: build tar

build:
	(tar xzf src/google-perftools-$(TCMALLOC_VERSION).tar.gz; \
	cd google-perftools-$(TCMALLOC_VERSION); \
	CFLAGS="-g -O2" \
	$(ENVMOD) ./configure --prefix=$(ZIMBRA_HOME)/tcmalloc-$(TCMALLOC_VERSION) $(CONF_OPS); \
	make tcmalloc_minimal_unittest tcmalloc_minimal_large_unittest \
             addressmap_unittest atomicops_unittest frag_unittest \
             low_level_alloc_unittest markidle_unittest memalign_unittest \
             packed_cache_test stacktrace_unittest system_alloc_unittest \
             thread_dealloc_unittest; \
	 make install)
	(cd $(ZIMBRA_HOME); ln -s tcmalloc-$(TCMALLOC_VERSION) tcmalloc)

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
