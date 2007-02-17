DSPAM_ROOT := $(shell pwd)
P4_ROOT ?= $(shell cd $(DSPAM_ROOT)/../..; pwd)

BUILD_PLATFORM ?= $(shell sh $(P4_ROOT)/ZimbraBuild/rpmconf/Build/get_plat_tag.sh)

BDB_ROOT ?= $(P4_ROOT)/ThirdParty/sleepycat

BDB_VERSION ?= 4.2.52.4

ifeq ($(BUILD_PLATFORM), )
	BUILD_PLATFORM := "UNKNOWN"
endif

ZIMBRA_HOME ?= /opt/zimbra
DSPAM_VERSION ?= 3.6.2

DSPAM_TGZ_TARGET := \
	$(DSPAM_ROOT)/builds/$(BUILD_PLATFORM)/dspam-$(DSPAM_VERSION).tgz

MYSQL_INCLUDE_DIR := $(ZIMBRA_HOME)/mysql/include 

ifeq ($(BUILD_PLATFORM), MACOSX)
	MYSQL_INCLUDE_DIR := $(ZIMBRA_HOME)/mysql/include/mysql
endif
ifeq ($(BUILD_PLATFORM), MACOSXx86)
	MYSQL_INCLUDE_DIR := $(ZIMBRA_HOME)/mysql/include/mysql
endif

all: build tar

build:
	(tar xzf dspam-$(DSPAM_VERSION).tar.gz; \
	cd dspam-$(DSPAM_VERSION); \
	./configure --prefix=$(ZIMBRA_HOME)/dspam-$(DSPAM_VERSION) \
	--sysconfdir=$(ZIMBRA_HOME)/conf --with-logdir=$(ZIMBRA_HOME)/log \
	--enable-daemon \
	--enable-large-scale --with-storage-driver=hash_drv,mysql_drv \
	--with-mysql-includes=$(MYSQL_INCLUDE_DIR) \
	--with-mysql-libraries=$(ZIMBRA_HOME)/mysql/lib \
	--enable-debug --enable-clamav; \
	make; make install)
	(cd $(ZIMBRA_HOME); ln -s dspam-$(DSPAM_VERSION) dspam)

tar:
	mkdir -p $(DSPAM_ROOT)/builds/$(BUILD_PLATFORM)
	(cd $(ZIMBRA_HOME); tar czf $(DSPAM_TGZ_TARGET) dspam-$(DSPAM_VERSION))

p4edit: $(DSPAM_TGZ_TARGET)
	p4 add $(DSPAM_TGZ_TARGET)
	p4 edit $(DSPAM_TGZ_TARGET)

clean:
	/bin/rm -rf dspam-$(DSPAM_VERSION)

allclean: clean
	/bin/rm -rf $(ZIMBRA_HOME)/dspam-$(DSPAM_VERSION)
	/bin/rm -rf $(ZIMBRA_HOME)/dspam
