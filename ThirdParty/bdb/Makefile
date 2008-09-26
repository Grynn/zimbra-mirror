BDB_ROOT := $(shell pwd)
P4_ROOT ?= $(shell cd $(BDB_ROOT)/../..; pwd)
MAKE ?= make
MAKEARGS ?= -j2

BUILD_PLATFORM ?= $(shell sh $(P4_ROOT)/ZimbraBuild/rpmconf/Build/get_plat_tag.sh)

ifeq ($(BUILD_PLATFORM), )
	BUILD_PLATFORM := "UNKNOWN"
endif

ZIMBRA_HOME ?= /opt/zimbra
BDB_RELEASE ?= 4.7.25
BDB_PATCH_LEVEL ?= 2
BDB_VERSION ?= $(BDB_RELEASE).$(BDB_PATCH_LEVEL)
BDB_TGZ_TARGET := $(P4_ROOT)/ThirdPartyBuilds/$(BUILD_PLATFORM)/bdb/bdb-$(BDB_VERSION).tgz
BDB_LIB_DIR ?= $(ZIMBRA_HOME)/bdb-$(BDB_VERSION)/lib

CONFIG_FLAG :=

ifneq (MACOSXx86,$(findstring MACOSXx86,$(BUILD_PLATFORM)))
	CONFIG_FLAG := --with-mutex=POSIX/pthreads
endif

all: build tar

build:
	(tar xzf src/db-$(BDB_RELEASE).tar.gz; \
	mv db-$(BDB_RELEASE) db-$(BDB_VERSION); \
	cd db-$(BDB_VERSION); \
	patch -g0 -p1 < ../patches/4.7.25-1.patch; \
	patch -g0 -p1 < ../patches/4.7.25-2.patch; \
	cd build_unix; \
	CFLAGS="-g -O2 -fPIC" LD_RUN_PATH=$(BDB_LIB_DIR) ../dist/configure --prefix=$(ZIMBRA_HOME)/bdb-$(BDB_VERSION) --enable-posixmutexes $(CONFIG_FLAG); \
	CFLAGS="-g -O2 -fPIC" LD_RUN_PATH=$(BDB_LIB_DIR) $(MAKE) $(MAKEARGS); \
	LD_RUN_PATH=$(BDB_LIB_DIR) $(MAKE) install)
	(cd $(ZIMBRA_HOME); ln -s bdb-$(BDB_VERSION) bdb)

tar:
	mkdir -p $(P4_ROOT)/ThirdPartyBuilds/$(BUILD_PLATFORM)/bdb
	(cd $(ZIMBRA_HOME); tar czf $(BDB_TGZ_TARGET) bdb-$(BDB_VERSION))
	chmod -R a+w $(P4_ROOT)/ThirdPartyBuilds/$(BUILD_PLATFORM)/bdb

clean:
	/bin/rm -rf db-$(BDB_VERSION)

p4edit: $(BDB_TGZ_TARGET)
	p4 add $(BDB_TGZ_TARGET) 
	p4 edit $(BDB_TGZ_TARGET) 

allclean: clean
	/bin/rm -rf $(ZIMBRA_HOME)/bdb-$(BDB_VERSION)
	/bin/rm -rf $(ZIMBRA_HOME)/bdb
